package com.ffms.app.service;

import com.ffms.app.model.Asset;
import com.ffms.app.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    public List<Asset> getByFamily(Long familyId) {
        return assetRepository.findByFamilyIdOrderByUpdatedAtDesc(familyId);
    }

    public Asset getById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset update(Long id, Asset updated) {
        Asset asset = getById(id);
        asset.setName(updated.getName());
        asset.setType(updated.getType());
        asset.setAmount(updated.getAmount());
        asset.setDescription(updated.getDescription());
        return assetRepository.save(asset);
    }

    public void delete(Long id) {
        assetRepository.deleteById(id);
    }

    public BigDecimal getTotalByFamily(Long familyId) {
        return assetRepository.sumAmountByFamilyId(familyId);
    }
}
