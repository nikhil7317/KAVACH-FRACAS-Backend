package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Asset;
import com.railbit.tcasanalysis.repository.AssetRepo;
import com.railbit.tcasanalysis.service.AssetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AssetServiceImpl implements AssetService {
    private final AssetRepo assetRepo;

    @Override
    public Asset getAssetById(Long id) {
        Optional<Asset> data=assetRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Asset not found");
        return data.get();
    }

    @Override
    public List<Asset> getAllAsset() {
        return assetRepo.findAll();
    }

    @Override
    public Asset postAsset(Asset asset) {
        return assetRepo.save(asset);
    }

    @Override
    public void updateAsset(Asset asset) {
        assetRepo.save(asset);
    }

    @Override
    public void deleteAssetById(Long id) {
        assetRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }
}
