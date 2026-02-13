package com.ffms.app.repository;

import com.ffms.app.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByFamilyIdOrderByUpdatedAtDesc(Long familyId);

    @Query("SELECT COALESCE(SUM(a.amount), 0) FROM Asset a WHERE a.familyId = :familyId")
    BigDecimal sumAmountByFamilyId(Long familyId);
}
