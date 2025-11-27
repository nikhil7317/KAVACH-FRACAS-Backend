package com.railbit.tcasanalysis.service;



import com.railbit.tcasanalysis.entity.Asset;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AssetService {
    Asset getAssetById(Long id);
    List<Asset> getAllAsset();
    Asset postAsset(Asset asset);
    void updateAsset(Asset asset);
    void deleteAssetById(Long id);
    int importByExcelSheet(MultipartFile excelSheet) throws Exception;
}
