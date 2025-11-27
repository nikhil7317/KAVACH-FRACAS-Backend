package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.service.FileService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.util.StringUtils;
import java.util.Base64;

@Service
public class FileServiceImpl implements FileService {

    private final Path RESOURCE_PATH;

    @Autowired
    public FileServiceImpl(@Value("${resources.path}") String RESOURCE_PATH) throws Exception {
        this.RESOURCE_PATH = Paths.get(RESOURCE_PATH)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.RESOURCE_PATH);
        } catch (Exception ex) {
            throw new Exception("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public void uploadFile(List<MultipartFile> fileList,Long inspectionId, Long inspectionTrackId, String filePath) throws Exception {
        if(fileList==null||fileList.isEmpty())
            throw new FileNotFoundException("empty file uploading");
        String FINAL_PATH=RESOURCE_PATH+File.separator+filePath+File.separator+inspectionId+File.separator+inspectionTrackId;
        File file=new File(FINAL_PATH);
        if(!file.exists())
            if(!file.mkdirs())
                throw new Exception("unable to create directory of "+filePath);
        for(MultipartFile multipartFile:fileList){
            Files.copy(multipartFile.getInputStream()
                    , Path.of(FINAL_PATH
                            + File.separator + multipartFile.getOriginalFilename())
                    , StandardCopyOption.REPLACE_EXISTING
            );
        }

    }


    @Override
    public String storeFiles(String base64Data, long maint_pk, String file_name) throws Exception {

        String fileName = StringUtils.cleanPath(file_name);

        try {

            if (fileName.contains("..")) {
                throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
            }

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String FINAL_PATH = RESOURCE_PATH + File.separator + "maintdoc" + File.separator + maint_pk + File.separator + currentDate;
            Path finalDirectory = Paths.get(FINAL_PATH);
            Files.createDirectories(finalDirectory);
            Path targetLocation = finalDirectory.resolve(fileName);
            byte[] fileBytes = decodeBase64Data(base64Data);
            try (FileOutputStream fos = new FileOutputStream(targetLocation.toFile())) {
                fos.write(fileBytes);
            }
            return targetLocation.toString();
        } catch (IOException ex) {
            throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    private byte[] decodeBase64Data(String base64Data) {
        if (base64Data.contains(",")) {
            base64Data = base64Data.split(",")[1];
        }
        return Base64.getDecoder().decode(base64Data);
    }

    @Override
    public void deleteFile(String filePath) throws FileNotFoundException {
        File file=new File(RESOURCE_PATH+File.separator+filePath);
        if(file.exists())
            System.out.println("file exists:"+file.exists()+"file deleted :"+file.delete());
        else throw new FileNotFoundException("");
    }
    @Override
    public void copyFile(Integer sourceId, Integer destinationId, String filePath) throws Exception {
        String FINAL_SOURCE_PATH=RESOURCE_PATH+File.separator+filePath+File.separator+sourceId;
        String FINAL_DESTINATION_PATH=RESOURCE_PATH+File.separator+filePath+File.separator+destinationId;
        File destinationFile=new File(FINAL_DESTINATION_PATH);
        File sourceFile=new File(FINAL_SOURCE_PATH);
        if(!destinationFile.exists())
            if(!destinationFile.mkdirs())
                throw new Exception("unable to create directory of "+filePath);

        FileUtils.copyDirectory(sourceFile,destinationFile);

    }
}
