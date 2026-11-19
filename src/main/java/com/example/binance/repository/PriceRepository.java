package com.example.binance.repository;

import com.example.binance.entity.PriceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface PriceRepository extends JpaRepository<PriceEntity, Long> {
    Optional<PriceEntity> findTopBySymbolOrderByFetchedAtDescIdDesc(String symbol);
    Page<PriceEntity> findBySymbolAndFetchedAtGreaterThanEqualAndFetchedAtLessThanEqualOrderByFetchedAtDescIdDesc(String symbol, Instant from, Instant to, Pageable pageable);
    @Query("select avg(p.price) from PriceEntity p where p.symbol = :symbol and p.fetchedAt >= :from and p.fetchedAt <= :to")
    Optional<BigDecimal> averagePrice(@Param("symbol") String symbol, @Param("from") Instant from, @Param("to") Instant to);
    @Query("select p from PriceEntity p where p.symbol = :symbol and p.fetchedAt >= :from and p.fetchedAt <= :to order by p.fetchedAt asc, p.id asc")
    java.util.List<PriceEntity> findPeriod(@Param("symbol") String symbol, @Param("from") Instant from, @Param("to") Instant to);
}
