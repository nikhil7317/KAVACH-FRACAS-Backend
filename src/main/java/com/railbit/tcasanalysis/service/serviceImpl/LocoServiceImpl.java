package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.loco.LocoType;
import com.railbit.tcasanalysis.entity.loco.Shed;
import com.railbit.tcasanalysis.repository.LocoRepo;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.service.LocoService;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class LocoServiceImpl implements LocoService {
    private static final Logger log = LoggerFactory.getLogger(LocoServiceImpl.class);
    private final LocoRepo locoRepo;
    private final FirmService firmService;
    private final LocoTypeService locoTypeService;
    private final ShedService shedService;
    @Override
    public Loco getLocoById(Integer id) {
        Optional<Loco> data=locoRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Loco not found");
        return data.get();
    }

    @Override
    public Loco findByLocoNo(String locoNo) {
        return locoRepo.findByLocoNo(locoNo);
    }

    @Override
    public List<Loco> getAllLoco() {
        return locoRepo.findAll();
    }

    @Override
    public List<String> getAllLocoNos() {
        return getAllLoco().stream()
                .map(Loco::getLocoNo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Loco> getAllLocos() {
        return getAllLoco();
    }

    @Override
    public Page<Loco> getLocos(String searchTerm, Integer locoType, Integer firm, Integer shed,
                               String month, String version, Pageable pageable) {
        return locoRepo.searchLocos(searchTerm, locoType, firm, shed, month, version, pageable);
    }

    @Override
    public int postLoco(Loco loco) {
        Loco newLoco =  locoRepo.save(loco);
        return newLoco.getId();
    }

    @Override
    public void updateLoco(Loco loco) {
        locoRepo.save(loco);
    }

    @Override
    public List<String> getDistinctVersions() {
        return locoRepo.findDistinctVersions();
    }

    @Override
    public void deleteLocoById(Integer id) {
        locoRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        InputStream inputStream = excelSheet.getInputStream();
        int rowInserted = 0;
        int rowFailed = 0;

        int locoNoIndex = -1;
        int typeIndex   = -1;
        int firmIndex   = -1;
        int shedIndex   = -1;
        int monthIndex  = -1;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook){
                System.out.println(sheet.getSheetName());
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (i==0){
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                // Process each cell
                                System.out.print(cell.toString() + "\t");
                                if (cell.toString().equalsIgnoreCase("locono")){
                                    locoNoIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("type")){
                                    typeIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("firm")){
                                    firmIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("shed")){
                                    shedIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("month")){
                                    monthIndex = j;
                                }

                            }
                        }
                        System.out.println();
                    } else {
                        String locoNo = row.getCell(locoNoIndex).toString();
                        if (locoNo.isEmpty()){
                            rowFailed++;
                            continue;
                        }
//                        System.out.println(locoNo);
                        String type ="";
                        String firm ="";
                        String shed ="";
                        String month="";

                        type = row.getCell(typeIndex).toString();
                        firm = row.getCell(firmIndex).toString();
                        shed = row.getCell(shedIndex).toString();
                        if (monthIndex >= 0) {
                            month = row.getCell(monthIndex).toString();
                        }

                        LocoType locoTypeObj = locoTypeService.getLocoTypeByName(type.trim());
                        Shed shedObj = shedService.getShedByName(shed.trim());
                        Firm firmObj = firmService.getFirmByName(firm.trim());

                        if (locoTypeObj == null) {
                            int newId = locoTypeService.postLocoType(new LocoType(null,type));
                            locoTypeObj = locoTypeService.getLocoTypeById(newId);
                        }
                        if (shedObj == null) {
                            int newId = shedService.postShed(new Shed(null,shed));
                            shedObj = shedService.getShedById(newId);
                        }

                        Loco loco = new Loco();
                        loco.setLocoNo(HelpingHand.convertToInt(locoNo.trim()));
                        loco.setLocoType(locoTypeObj);
                        loco.setFirm(firmObj);
                        loco.setShed(shedObj);
                        loco.setMonth(month.trim());

                        if (findByLocoNo(loco.getLocoNo()) == null) {
                            postLoco(loco);
                            rowInserted++;
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.error("Excel Exception ",e);
            throw new Exception("Excel File Not Valid");
        }
        return rowInserted;
    }


}
