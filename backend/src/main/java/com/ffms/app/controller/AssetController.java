package com.ffms.app.controller;

import com.ffms.app.model.Asset;
import com.ffms.app.model.dto.ApiResponse;
import com.ffms.app.security.JwtUtil;
import com.ffms.app.service.AssetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Asset>>> getAssets(HttpServletRequest request) {
        Long familyId = extractFamilyId(request);
        return ResponseEntity.ok(ApiResponse.ok(assetService.getByFamily(familyId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset>> getAsset(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(assetService.getById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Asset>> createAsset(@RequestBody Asset asset, HttpServletRequest request) {
        asset.setFamilyId(extractFamilyId(request));
        asset.setCreatedBy(extractUserId(request));
        return ResponseEntity.ok(ApiResponse.ok(assetService.create(asset)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Asset>> updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        return ResponseEntity.ok(ApiResponse.ok(assetService.update(id, asset)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Asset deleted", null));
    }

    private Long extractFamilyId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractFamilyId(token);
    }

    private Long extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return jwtUtil.extractUserId(token);
    }
}
