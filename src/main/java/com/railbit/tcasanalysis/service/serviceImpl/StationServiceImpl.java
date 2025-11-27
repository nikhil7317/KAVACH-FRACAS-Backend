package com.railbit.tcasanalysis.service.serviceImpl;


import com.railbit.tcasanalysis.entity.Division;
import com.railbit.tcasanalysis.entity.Firm;
import com.railbit.tcasanalysis.entity.Section;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.DivisionRepo;
import com.railbit.tcasanalysis.repository.StationRepo;
import com.railbit.tcasanalysis.service.DivisionService;
import com.railbit.tcasanalysis.service.FirmService;
import com.railbit.tcasanalysis.service.StationService;
import com.railbit.tcasanalysis.service.TcasService;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StationServiceImpl implements StationService {

    private final StationRepo stationRepo;
    private final DivisionService divisionService;
    private final FirmService firmService;
    private final TcasService tcasService;
    private static final Logger log = LoggerFactory.getLogger(StationServiceImpl.class);

    @Override
    public Station getStationById(Integer id) {
        Optional<Station> data=stationRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Station not found");
        return data.get();
    }
    @Override
    public Station getStationByName(String name) {
        return stationRepo.findByName(name);
    }
    @Override
    public Station getStationByCode(String code) {
        return stationRepo.findByCode(code);
    }

    @Override
    public Station findByTcassubsysid(Integer id) {
        return stationRepo.findByTcassubsysid(id);
    }

    @Override
    public List<Station> getAllStations() {
        return stationRepo.findAll();
    }
//    @Override
//    public List<Station> getAllStationsBySection(Integer sectionId) {
//        return stationRepo.findBySectionId(sectionId);
//    }
    @Override
    public List<Station> getAllStationsByZone(Integer zoneId) {
        return stationRepo.findByDivision_Zone_Id(zoneId);
    }
    @Override
    public List<Station> getAllStationsByDivision(Integer divisionId) {
        return stationRepo.findByDivisionId(divisionId);
    }
    @Override
    public int postStation(Station station) {
       Station newStation = stationRepo.save(station);
       return newStation.getId();
    }
    @Override
    public void updateStation(Station station) {
        stationRepo.save(station);
    }

    @Override
    public void deleteStationById(Integer id) {
        stationRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        InputStream inputStream = excelSheet.getInputStream();
        int rowInserted = 0;
        int rowFailed = 0;

        int nameIndex = -1;
        int codeIndex   = -1;
        int firmIndex   = -1;
        int divisionIndex = -1;

        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook){
//                System.out.println(sheet.getSheetName());
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (i==0){
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                // Process each cell
                                System.out.print(cell.toString() + "\t");
                                if (cell.toString().equalsIgnoreCase("name")){
                                    nameIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("code")){
                                    codeIndex = j;
                                } else if (cell.toString().equalsIgnoreCase("division")){
                                    divisionIndex = j;
                                }
                            }
                        }
                    } else {
                        String code = row.getCell(codeIndex).toString();
                        if (code.isEmpty()){
                            rowFailed++;
                            continue;
                        }
//                        System.out.println(locoNo);
                        String name = row.getCell(nameIndex).toString();
                        String firm = row.getCell(firmIndex).toString();
                        String division = row.getCell(divisionIndex).toString();

                        Firm firmObj = firmService.getFirmByName(firm.trim());
                        Division divisionObj = divisionService.getDivisionByName(division.trim());

                        Station station = new Station(null,name,code,divisionObj,firmObj,tcasService.getTcasById(1),1,"3.2");
                        postStation(station);
                        rowInserted++;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Exception :",e);
            throw new Exception("Excel File Not Valid");
        }

        return rowInserted;
    }
}
