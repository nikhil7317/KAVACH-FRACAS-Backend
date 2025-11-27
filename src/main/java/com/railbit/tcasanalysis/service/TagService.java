package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.Tag;
import com.railbit.tcasanalysis.repository.TagRepo;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TagService {

    /**
     * This service class provides methods to manage Tags.
     * It includes methods to get all tags, get a tag by ID, add a new tag,
     * update an existing tag, delete a tag by ID, and import tags from an Excel sheet.
     */
    private static final Logger log = LogManager.getLogger(TagService.class);
    private final TagRepo tagRepo;
    private final StationService stationService;

    public List<Tag> getAllTags() {
        return tagRepo.findAll();
    }

    public Tag getTagById(Long id) {
        Optional<Tag> data=tagRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("Tag not found");
        return data.get();
    }

    public Long postTag(Tag tag) {
        Tag newTag = tagRepo.save(tag);
        return newTag.getId();
    }

    public void updateTag(Tag tag) {
        tagRepo.save(tag);
    }

    public void deleteTagById(Long id) {
        tagRepo.deleteById(id);
    }

    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        int rowsInserted = 0;

        int stnIndex = -1;
        int tagNoIndex = -1;
        int tagTypeIndex = -1;
        int latIndex = -1;
        int longIndex = -1;
        int roadNoIndex = -1;
        int sectionIndex = -1;

        InputStream inputStream = excelSheet.getInputStream();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {

            for (Sheet sheet : workbook) {
                log.info("No of Rows: {}", sheet.getPhysicalNumberOfRows());
                if (sheet.getPhysicalNumberOfRows() < 1) {
                    throw new Exception("Excel Sheet is Empty");
                }
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (i == 0) {
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                // Process each cell
                                System.out.print(cell + "\t");
                                if (cell.toString().trim().equalsIgnoreCase("Stn")) {
                                    stnIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Tag No")) {
                                    tagNoIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Tag Type")) {
                                    tagTypeIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Lat")) {
                                    latIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Long")) {
                                    longIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Road No")) {
                                    roadNoIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Section")) {
                                    sectionIndex = j;
                                }
                            }
                        }
                    } else {
                        if (isRowEmpty(row)) {
                            continue;  // skip this row
                        }

                        Tag tag = new Tag();
                        if (stnIndex != -1 && row.getCell(stnIndex) != null) {
                            String stationString = row.getCell(stnIndex).toString().trim();
                            Station station = stationService.getStationByCode(stationString);
                            tag.setStation(station);
                        }
                        if (tagNoIndex != -1 && row.getCell(tagNoIndex) != null) tag.setTagNo(getCellValueAsString(row.getCell(tagNoIndex)));
                        if (tagTypeIndex != -1 && row.getCell(tagTypeIndex) != null) tag.setTagType(getCellValueAsString(row.getCell(tagTypeIndex)));
                        if (roadNoIndex != -1 && row.getCell(roadNoIndex) != null) tag.setRoadNo(getCellValueAsString(row.getCell(roadNoIndex)));
                        if (sectionIndex != -1 && row.getCell(sectionIndex) != null) tag.setSection(getCellValueAsString(row.getCell(sectionIndex)));

                        if (latIndex != -1 && row.getCell(latIndex) != null) tag.setLatitude(getCellValueAsDouble(row.getCell(latIndex)));
                        if (longIndex != -1 && row.getCell(longIndex) != null) tag.setLongitude(getCellValueAsDouble(row.getCell(longIndex)));

                        Long insertId = postTag(tag);
                        if (insertId > 0) {
                            rowsInserted++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Excel Exception ",e);
        }

        return rowsInserted;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                double value = cell.getNumericCellValue();
                if (value == Math.floor(value)) {
                    yield String.valueOf((long) value);
                } else {
                    yield String.valueOf(value);
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> cell.getCellFormula().trim();
            default -> "";
        };
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue().trim();
            if (value.isEmpty()) return null;
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (Cell cell : row) {
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }


}
