package com.railbit.tcasanalysis.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.util.List;

public interface FileService {

    void uploadFile(List<MultipartFile> fileList,Long inspectionId, Long inspectionTrackId,String path) throws Exception;

    void deleteFile(String filePath) throws FileNotFoundException;

    void copyFile(Integer sourceId, Integer destinationId, String filePath) throws Exception;
    String storeFiles(String base64Data, long maint_pk, String file_name) throws Exception;

}
