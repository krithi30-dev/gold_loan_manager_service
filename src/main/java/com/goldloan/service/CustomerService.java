package com.goldloan.service;
import com.goldloan.dto.customer.*;
import com.goldloan.entity.*;
import com.goldloan.exception.*;
import com.goldloan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    public CustomerResponse create(CustomerRequest req, String email) {
        if (req.getAadhaar() != null && customerRepository.existsByAadhaar(req.getAadhaar()))
            throw new BusinessException("DUPLICATE_AADHAAR", "Customer with this Aadhaar already exists");
        Branch branch = req.getBranchId() != null
                ? branchRepository.findById(UUID.fromString(req.getBranchId())).orElse(null) : null;
        UserEntity createdBy = userRepository.findByEmail(email).orElse(null);
        String code = generateCode();
        Customer c = Customer.builder().customerCode(code).name(req.getName())
                .dob(req.getDob() != null ? LocalDate.parse(req.getDob()) : null)
                .phone(req.getPhone()).email(req.getEmail()).aadhaar(req.getAadhaar()).pan(req.getPan())
                .address(req.getAddress()).photoUrl(req.getPhotoUrl()).signatureUrl(req.getSignatureUrl())
                .branch(branch).createdBy(createdBy).build();
        c = customerRepository.save(c);
        auditLogRepository.save(AuditLog.builder().entityType("CUSTOMER").entityId(c.getId().toString())
                .action("CREATE").performedBy(createdBy).build());
        return toResponse(c);
    }
    @Transactional(readOnly = true)
    public CustomerResponse getById(UUID id) {
        return toResponse(customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id.toString())));
    }
    @Transactional(readOnly = true)
    public Page<CustomerResponse> search(String q, Pageable pageable) {
        return customerRepository.search(q != null ? q : "", pageable).map(this::toResponse);
    }
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toResponse);
    }
    public CustomerResponse update(UUID id, CustomerRequest req, String email) {
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id.toString()));
        if (req.getName() != null) c.setName(req.getName());
        if (req.getPhone() != null) c.setPhone(req.getPhone());
        if (req.getEmail() != null) c.setEmail(req.getEmail());
        if (req.getAddress() != null) c.setAddress(req.getAddress());
        if (req.getPhotoUrl() != null) c.setPhotoUrl(req.getPhotoUrl());
        if (req.getSignatureUrl() != null) c.setSignatureUrl(req.getSignatureUrl());
        return toResponse(customerRepository.save(c));
    }
    private String generateCode() {
        return customerRepository.findMaxCustomerCode().map(max -> {
            int n = Integer.parseInt(max.replace("CUST", "")) + 1;
            return String.format("CUST%06d", n);
        }).orElse("CUST000001");
    }
    private CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder().id(c.getId()).customerCode(c.getCustomerCode()).name(c.getName())
                .dob(c.getDob()).phone(c.getPhone()).email(c.getEmail()).aadhaar(c.getAadhaar()).pan(c.getPan())
                .address(c.getAddress()).photoUrl(c.getPhotoUrl()).signatureUrl(c.getSignatureUrl())
                .branchId(c.getBranch() != null ? c.getBranch().getId().toString() : null)
                .branchName(c.getBranch() != null ? c.getBranch().getName() : null)
                .createdAt(c.getCreatedAt())
                .kycDocuments(c.getKycDocuments() != null ? c.getKycDocuments().stream()
                        .map(d -> CustomerResponse.KycDocResponse.builder().id(d.getId())
                                .documentType(d.getDocumentType()).fileUrl(d.getFileUrl()).build()).toList() : null)
                .build();
    }
}
