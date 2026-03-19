package com.goldloan.repository;
import com.goldloan.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%',:q,'%')) OR c.phone LIKE CONCAT('%',:q,'%')")
    Page<Customer> search(@Param("q") String q, Pageable pageable);
    boolean existsByAadhaar(String aadhaar);
    @Query("SELECT MAX(c.customerCode) FROM Customer c WHERE c.customerCode LIKE 'CUST%'")
    Optional<String> findMaxCustomerCode();
}
