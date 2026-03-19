package com.goldloan.service;
import com.goldloan.dto.goldrate.*;
import com.goldloan.entity.*;
import com.goldloan.exception.ResourceNotFoundException;
import com.goldloan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
@Service
@RequiredArgsConstructor
public class GoldRateService {
    private final GoldRateRepository goldRateRepository;
    private final UserRepository userRepository;
    public GoldRateResponse setRate(GoldRateRequest req, String email) {
        UserEntity user = userRepository.findByEmail(email).orElse(null);
        LocalDate date = req.getEffectiveDate() != null ? LocalDate.parse(req.getEffectiveDate()) : LocalDate.now();
        GoldRate rate = GoldRate.builder().karat(req.getKarat()).ratePerGramPaise(req.getRatePerGramPaise())
                .effectiveDate(date).createdBy(user).build();
        return toResponse(goldRateRepository.save(rate));
    }
    public List<GoldRateResponse> getTodayRates() {
        List<GoldRateResponse> rates = new ArrayList<>();
        for (int k : new int[]{18, 22, 24}) {
            goldRateRepository.findTopByKaratAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(k, LocalDate.now())
                    .ifPresent(r -> rates.add(toResponse(r)));
        }
        return rates;
    }
    public Long getRateForKarat(int karat) {
        return goldRateRepository.findTopByKaratAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(karat, LocalDate.now())
                .map(GoldRate::getRatePerGramPaise)
                .orElseThrow(() -> new ResourceNotFoundException("Gold rate not found for " + karat + "K. Please set today's rate first."));
    }
    private GoldRateResponse toResponse(GoldRate r) {
        return GoldRateResponse.builder().id(r.getId()).karat(r.getKarat())
                .ratePerGramPaise(r.getRatePerGramPaise()).effectiveDate(r.getEffectiveDate())
                .createdAt(r.getCreatedAt()).build();
    }
}
