package com.railbit.tcasanalysis.service;


import com.railbit.tcasanalysis.DTO.CMSAbn.CMSAbnImportResponseDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.cmsabn.CMSAbn;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.repository.CMSAbnRepo;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class CMSAbnService {

    private static final Logger log = LogManager.getLogger(CMSAbnService.class);
    @Autowired
    EntityManager entityManager;

    private final CMSAbnRepo cMSAbnRepo;

    private final StationService stationService;
    private final LocoService locoService;
    private final DivisionService divisionService;
    private final RoleService roleService;
    private final UserService userService;
    private final EmailService emailService;
    private final OtpService otpService;
    @Autowired
    private ModelMapper modelMapper;
    
    public List<CMSAbn> getAllCMSAbns() {
        return cMSAbnRepo.findAll();
    }

    public CMSAbn getCMSAbnById(Long id) {
        Optional<CMSAbn> data=cMSAbnRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("CMSAbn not found");
        return data.get();
    }

    public Long postCMSAbn(CMSAbn cMSAbn) {
        CMSAbn newCMSAbn = cMSAbnRepo.save(cMSAbn);
        return newCMSAbn.getId();
    }

    public Page<CMSAbn> getFilteredCMSAbns(Long zoneId,
                                           Long divisionId,
                                           String abnType,
                                           String subHead,
                                           Long stationId,
                                           Long locoId,
                                           Long tcasId,
                                           String stcasLtcas,
                                           Long rootCauseCategory,
                                           String status,
                                           LocalDateTime fromDate,
                                           LocalDateTime toDate,
                                           String searchQuery,
                                           Pageable pageable) {

        // Get CriteriaBuilder from EntityManager
        CriteriaBuilder cbMain = entityManager.getCriteriaBuilder();

        // Main query (for fetching results)
        CriteriaQuery<CMSAbn> query = cbMain.createQuery(CMSAbn.class);
        Root<CMSAbn> root = query.from(CMSAbn.class);

        // Joins for the main query
        Join<CMSAbn, Division> divisionJoin = root.join("division");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");
        Join<CMSAbn, Loco> locoJoin = root.join("loco", JoinType.LEFT);
        Join<CMSAbn, Station> stationJoin = root.join("faultyStation", JoinType.LEFT);
        Join<CMSAbn, Tcas> tcasJoin = root.join("tcas", JoinType.LEFT);
        Join<CMSAbn, PossibleRootCause> rootCauseJoin = root.join("possibleRootCause", JoinType.LEFT);

        // Create a list to hold predicates for the main query
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates for filtering
        if (zoneId != null && zoneId > 0) {
            predicates.add(cbMain.equal(zoneJoin.get("id"), zoneId));
        }

        if (divisionId != null && divisionId > 0) {
            predicates.add(cbMain.equal(divisionJoin.get("id"), divisionId));
        }

        if (stationId != null && stationId > 0) {
            predicates.add(cbMain.equal(stationJoin.get("id"), stationId));
        }

        if (locoId != null && locoId > 0) {
            predicates.add(cbMain.equal(locoJoin.get("id"), locoId));
        }

        if (tcasId != null && tcasId > 0) {
            predicates.add(cbMain.equal(tcasJoin.get("id"), tcasId));
        }

        if (rootCauseCategory != null && rootCauseCategory > 0) {
            predicates.add(cbMain.equal(rootCauseJoin.get("id"), rootCauseCategory));
        }

//        if (!StringUtils.isEmpty(abnType) && !abnType.equalsIgnoreCase("all")) {
//            predicates.add(cbMain.equal(root.get("abnType"), abnType));
//        }

        if (!StringUtils.isEmpty(subHead) && !subHead.equalsIgnoreCase("all")) {
            predicates.add(cbMain.equal(root.get("subHead"), subHead));
        }

        if (!StringUtils.isEmpty(stcasLtcas) && !stcasLtcas.equalsIgnoreCase("all")) {
            predicates.add(cbMain.equal(root.get("ltcasStcas"), stcasLtcas));
        }

        if (!StringUtils.isEmpty(status) && !status.equalsIgnoreCase("all")) {
            predicates.add(cbMain.equal(root.get("status"), status));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(cbMain.between(root.get("abnDateTime"), fromDate, toDate));
        }

        // For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            predicates.add(cbMain.or(
                    cbMain.like(root.get("abnId"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("abnType"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("subHead"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("description"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("initialClosingRemark"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("ticketNo"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("oemRemark"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("analysis"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("ltcasStcas"), "%" + searchQuery + "%"),
                    cbMain.like(rootCauseJoin.get("name"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("status"), "%" + searchQuery + "%"),
                    cbMain.like(root.get("finalClosingRemark"), "%" + searchQuery + "%")
            ));
        }

        // Apply predicates to the main query
        query.where(cbMain.and(predicates.toArray(new Predicate[0])));

        // Create a TypedQuery for the main query
        TypedQuery<CMSAbn> typedQuery = entityManager.createQuery(query);
        int totalRows = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Fetch the results for the main query
        List<CMSAbn> results = typedQuery.getResultList();

        // Return results as a Page
        return new PageImpl<>(results, pageable, totalRows);
    }

    public void updateCMSAbn(CMSAbn cMSAbn) throws Exception {

        CMSAbn existingEntity = cMSAbnRepo.findById(cMSAbn.getId())
                .orElseThrow(() -> new Exception("TcasBreakingInspection not found"));

        // Map DTO properties to existing entity
        modelMapper.map(cMSAbn, existingEntity);

        // Save the updated entity
        CMSAbn savedEntity = cMSAbnRepo.save(existingEntity);

//        cMSAbnRepo.save(cMSAbn);
    }

    public void deleteCMSAbnById(Long id) {
        cMSAbnRepo.deleteById(id);
    }

    public Long addCMSAbnRemark(CMSAbn cmsAbn) {
        String sendingUser = cmsAbn.getFrwdByUser();
        CMSAbn cmsAbn1 = cMSAbnRepo.save(cmsAbn);

        if (cmsAbn1.getId() > 0) {
            CMSAbn savedCMSAbn = getCMSAbnById(cmsAbn1.getId());
            log.info("CMSABN : {}", savedCMSAbn);

            Role role = roleService.getRoleByName("ROLE_DIVISION");
            List<User> userList = userService.getUserByRoleIdAndDivisionId(
                    role.getId(),
                    savedCMSAbn.getDivision() != null ? savedCMSAbn.getDivision().getId() : null
            );
            for (User user : userList) {
                String userName = user.getName() != null ? user.getName() : "";
                String userEmail = user.getEmail() != null ? user.getEmail() : "";
                String userContact = user.getContact() != null ? user.getContact() : "";
                String mailBody = getString(savedCMSAbn, userName, sendingUser);
                String mailSubject = savedCMSAbn.getStatus();
                List<String> toList = Collections.singletonList(userEmail);

                // Sending Email
                emailService.mailService(mailSubject, mailBody, toList);
            }
        }

        return cmsAbn1.getId();
    }

    private static String getString(CMSAbn savedCMSAbn, String userName, String sendingUser) {
        String abnId = savedCMSAbn.getAbnId() != null ? savedCMSAbn.getAbnId() : "";
        String divisionCode = savedCMSAbn.getDivision() !=null ? savedCMSAbn.getDivision().getCode():null;

        String mailBody = "Dear " + userName + ",\n\n" +
                "Remarks has been added by " + sendingUser +
                "ABN Id : " + abnId + "\n" +
                "Division : " + divisionCode + "\n" +
                "Remarks : " + savedCMSAbn.getAppRemarks() + "\n" +
                "\n\n" +
                "Best Regards,\n\n" +
                "Kavach Administration" + "\n";
        return mailBody;
    }

    public CMSAbnImportResponseDTO importByExcelSheet(MultipartFile excelSheet) throws Exception {

        CMSAbnImportResponseDTO cmsAbnImportResponseDTO = new CMSAbnImportResponseDTO(0, 0, new HashMap<>());

        int abnIdIndex = -1;
        int abnTypeIndex = -1;
        int subHeadIndex = -1;
        int reportingStationIndex = -1;
        int fromIndex = -1;
        int toIndex = -1;
        int cliIdIndex = -1;
        int cliNameIndex = -1;
        int desigIndex = -1;
        int abnDateTimeIndex = -1;
        int fromKmIndex = -1;
        int toKmIndex = -1;
        int statusIndex = -1;
        int filledByIndex = -1;
        int locoIndex = -1;
        int trainIndex = -1;
        int smsToLpIndex = -1;
        int frwdByLocIndex = -1;
        int frwdByUserIndex = -1;
        int frwdByAuthIndex = -1;
        int frwdDateTimeIndex = -1;
        int divIndex = -1;
        int remSttsIndex = -1;
        int detailIndex = -1;
        int closingRmrkIndex = -1;
        int frwdRmrkIndex = -1;
        int appRmrkIndex = -1;
        int appRmrkdateTimeIndex = -1;
        int rprtDateTimeIndex = -1;

        InputStream inputStream = excelSheet.getInputStream();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            for (Sheet sheet : workbook) {
                log.info("No of Rows: {}", sheet.getPhysicalNumberOfRows());
                if (sheet.getPhysicalNumberOfRows() < 1) {
                    throw new Exception("Excel Sheet is Empty");
                }
                for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
                    Row row = sheet.getRow(i);
                    if (i == 2) {
                        for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
                            Cell cell = row.getCell(j);
                            if (cell != null) {
                                // Process each cell
                                System.out.print(cell + "\t");
                                if (cell.toString().trim().equalsIgnoreCase("ABN.ID")) {
                                    abnIdIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("ABN.TYPE")) {
                                    abnTypeIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("SUBHEAD")) {
                                    subHeadIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("REPORTSTTN")) {
                                    reportingStationIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FROM")) {
                                    fromIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("TO")) {
                                    toIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("CREW/CLIID")) {
                                    cliIdIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("CREW/CLINAME")) {
                                    cliNameIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("DESIG")) {
                                    desigIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("ABNDATE")) {
                                    abnDateTimeIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FROMKM")) {
                                    fromKmIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("TO KM")) {
                                    toKmIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("STATUS")) {
                                    statusIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FILLEDBY")) {
                                    filledByIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("LOCO")) {
                                    locoIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("TRAIN")) {
                                    trainIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("SMSTO LP")) {
                                    smsToLpIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FrwdByLoc")) {
                                    frwdByLocIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FrwdByUser")) {
                                    frwdByUserIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FrwdDateTime")) {
                                    frwdDateTimeIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("FrwdByAuth")) {
                                    frwdByAuthIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Div")) {
                                    divIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("RemStts")) {
                                    remSttsIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Detail")) {
                                    detailIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Closing Remarks")) {
                                    closingRmrkIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Forwarding Remarks")) {
                                    frwdRmrkIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("App Remarks")) {
                                    appRmrkIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("App Remarks Date")) {
                                    appRmrkdateTimeIndex = j;
                                } else if (cell.toString().trim().equalsIgnoreCase("Reporting Date")) {
                                    rprtDateTimeIndex = j;
                                }
                            }
                        }
                    } else if (i >2){
                        CMSAbn cmsAbn = new CMSAbn();
//                        {
//                            String abnId = row.getCell(abnIdIndex).toString().trim();
//                            cmsAbn.setAbnId(abnId);
//                        }
                        // Set properties based on the mapped indices

                        if (abnIdIndex != -1 && row.getCell(abnIdIndex) != null) cmsAbn.setAbnId(getCellValueAsString(row.getCell(abnIdIndex)));
                        if (abnTypeIndex != -1 && row.getCell(abnTypeIndex) != null) cmsAbn.setAbnType(getCellValueAsString(row.getCell(abnTypeIndex)));
                        if (subHeadIndex != -1 && row.getCell(subHeadIndex) != null) cmsAbn.setSubHead(getCellValueAsString(row.getCell(subHeadIndex)));
                        if (reportingStationIndex != -1 && row.getCell(reportingStationIndex) != null) {
                            String stationString = row.getCell(reportingStationIndex).toString();
                            Station station = stationService.getStationByCode(stationString);
                            cmsAbn.setFaultyStation(station);
                        }
                        if (fromIndex != -1 && row.getCell(fromIndex) != null) {
                            String stationString = row.getCell(fromIndex).toString();
                            Station station = stationService.getStationByCode(stationString);
                            cmsAbn.setFromStation(station);
                        }
                        if (toIndex != -1 && row.getCell(toIndex) != null) {
                            String stationString = row.getCell(toIndex).toString();
                            Station station = stationService.getStationByCode(stationString);
                            cmsAbn.setToStation(station);
                        }
                        if (cliIdIndex != -1 && row.getCell(cliIdIndex) != null) cmsAbn.setCliId(getCellValueAsString(row.getCell(cliIdIndex)));
                        if (cliNameIndex != -1 && row.getCell(cliNameIndex) != null) cmsAbn.setCliName(getCellValueAsString(row.getCell(cliNameIndex)));
                        if (desigIndex != -1 && row.getCell(desigIndex) != null) cmsAbn.setCliDesig(getCellValueAsString(row.getCell(desigIndex)));
                        if (abnDateTimeIndex != -1 && row.getCell(abnDateTimeIndex) != null) cmsAbn.setAbnDateTime(getCellValueAsDateTime(row.getCell(abnDateTimeIndex)));
                        if (fromKmIndex != -1 && row.getCell(fromKmIndex) != null) cmsAbn.setFromKm(getCellValueAsString(row.getCell(fromKmIndex)));
                        if (toKmIndex != -1 && row.getCell(toKmIndex) != null) cmsAbn.setToKm(getCellValueAsString(row.getCell(toKmIndex)));
                        if (statusIndex != -1 && row.getCell(statusIndex) != null) cmsAbn.setStatus(getCellValueAsString(row.getCell(statusIndex)));
                        if (filledByIndex != -1 && row.getCell(filledByIndex) != null) cmsAbn.setFilledBy(getCellValueAsString(row.getCell(filledByIndex)));
                        if (locoIndex != -1 && row.getCell(locoIndex) != null) {
                            String locoNo = row.getCell(locoIndex).toString();
                            Loco loco = locoService.findByLocoNo(HelpingHand.convertToInt(locoNo));
                            cmsAbn.setLoco(loco);
                        }
                        if (trainIndex != -1 && row.getCell(trainIndex) != null) cmsAbn.setTrain(getCellValueAsString(row.getCell(trainIndex)));
                        if (smsToLpIndex != -1 && row.getCell(smsToLpIndex) != null) cmsAbn.setSmstoLp(getCellValueAsDateTime(row.getCell(smsToLpIndex)));
                        if (frwdByLocIndex != -1 && row.getCell(frwdByLocIndex) != null) cmsAbn.setFrwdByLoc(getCellValueAsString(row.getCell(frwdByLocIndex)));
                        if (frwdByUserIndex != -1 && row.getCell(frwdByUserIndex) != null) cmsAbn.setFrwdByUser(getCellValueAsString(row.getCell(frwdByUserIndex)));
                        if (frwdDateTimeIndex != -1 && row.getCell(frwdDateTimeIndex) != null) cmsAbn.setFrwdDateTime(getCellValueAsDateTime(row.getCell(frwdDateTimeIndex)));
                        if (frwdByAuthIndex != -1 && row.getCell(frwdByAuthIndex) != null) cmsAbn.setFrwdByAuth(getCellValueAsString(row.getCell(frwdByAuthIndex)));
                        if (divIndex != -1 && row.getCell(divIndex) != null) {
                            String divString = row.getCell(divIndex).toString();
                            Division division = divisionService.getDivisionByCode(divString);
                            cmsAbn.setDivision(division);
                        }
                        if (remSttsIndex != -1 && row.getCell(remSttsIndex) != null) cmsAbn.setRemStts(getCellValueAsString(row.getCell(remSttsIndex)));
                        if (detailIndex != -1 && row.getCell(detailIndex) != null) cmsAbn.setDescription(getCellValueAsString(row.getCell(detailIndex)));
                        if (closingRmrkIndex != -1 && row.getCell(closingRmrkIndex) != null) cmsAbn.setInitialClosingRemark(getCellValueAsString(row.getCell(closingRmrkIndex)));
                        if (frwdRmrkIndex != -1 && row.getCell(frwdRmrkIndex) != null) cmsAbn.setFwrdRemarks(getCellValueAsString(row.getCell(frwdRmrkIndex)));
                        if (appRmrkIndex != -1 && row.getCell(appRmrkIndex) != null) cmsAbn.setAppRemarks(getCellValueAsString(row.getCell(appRmrkIndex)));
                        if (appRmrkdateTimeIndex != -1 && row.getCell(appRmrkdateTimeIndex) != null) cmsAbn.setAppRmrkDateTime(getCellValueAsDateTime(row.getCell(appRmrkdateTimeIndex)));
                        if (rprtDateTimeIndex != -1 && row.getCell(rprtDateTimeIndex) != null) cmsAbn.setRprtDateTime(getCellValueAsDateTime(row.getCell(rprtDateTimeIndex)));

                        if (cMSAbnRepo.existsByAbnId(cmsAbn.getAbnId())) {
                            cmsAbnImportResponseDTO.setRowFailed(cmsAbnImportResponseDTO.getRowFailed() + 1);
                            cmsAbnImportResponseDTO.getFailedRows().put("Duplicate Entry",cmsAbn);
                            log.info("ABN ID already exists: {}", cmsAbn.getAbnId());
                        } else {
                            Long insertId = postCMSAbn(cmsAbn);
                            if (insertId > 0) {
                                cmsAbnImportResponseDTO.setRowInserted(cmsAbnImportResponseDTO.getRowInserted() + 1);
                            } else {
                                cmsAbnImportResponseDTO.setRowFailed(cmsAbnImportResponseDTO.getRowFailed() + 1);
                            }
                        }


                    }
                }
            }
        } catch (Exception e) {
            log.error("Excel Exception ",e);
        }

        return cmsAbnImportResponseDTO;
    }

    private String getCellValueAsString(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> cell.getCellFormula().trim();
            default -> "";
        };
    }
    private LocalDateTime getCellValueAsDateTime(Cell cell) {
        if (cell == null) return null;
        try {
            return cell.getLocalDateTimeCellValue();
        } catch (IllegalStateException e) {
            // If the cell is not a date, try to parse it as a string
            try {
                return LocalDateTime.parse(cell.getStringCellValue().trim());
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
