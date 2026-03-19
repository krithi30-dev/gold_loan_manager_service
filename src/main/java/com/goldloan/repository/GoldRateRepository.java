package com.goldloan.repository;
import com.goldloan.entity.GoldRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface GoldRateRepository extends JpaRepository<GoldRate, UUID> {
    Optional<GoldRate> findTopByKaratAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(Integer karat, LocalDate date);
    List<GoldRate> findByEffectiveDateOrderByKaratAsc(LocalDate date);
}
