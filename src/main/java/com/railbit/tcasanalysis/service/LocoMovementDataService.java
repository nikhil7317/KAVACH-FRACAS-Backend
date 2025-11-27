package com.railbit.tcasanalysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.railbit.tcasanalysis.DTO.LocoMovementDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.nmspackets.AlertsCount;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncident;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncidentDate;
import com.railbit.tcasanalysis.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LocoMovementDataService {

    private static final Logger log = LogManager.getLogger(LocoMovementDataService.class);
    @Autowired
    private LocoMovementDataRepo locoDataRepository;

    @Autowired
    LocoRepo locoRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StationRepo stationRepo;

    @Autowired
    EntityManager entityManager;

    @Autowired
    private NmsIncidentDateRepo nmsIncidentDateRepo;

    @Autowired
    NmsIncidentRepo nmsIncidentRepo;

    private final FirmService firmService;

    // Define the input date format and MySQL date format
    private static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yy");
    private static final SimpleDateFormat mysqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public Map<String, Object> getFilteredLocoMovementData(Integer page, Integer intRecord, Integer locoId, Integer stationId, LocalDateTime fromDate, LocalDateTime toDate) throws ParseException {

        Pageable pageable = PageRequest.of(page, intRecord);

        // Step 1: Get the CriteriaBuilder and CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<LocoMovementDTO> cq = cb.createQuery(LocoMovementDTO.class);

        // Step 2: Define the root of the query (the main entity)
        Root<LocoMovementData> lm = cq.from(LocoMovementData.class);

        // Step 3: Join with related entities (stnCode and locoID)
        Join<LocoMovementData, Station> stnCodeJoin = lm.join("stnCode", JoinType.LEFT);
        Join<Station, Division> divisionJoin = stnCodeJoin.join("division", JoinType.LEFT);
        Join<Station, Firm> stnFirmJoin = stnCodeJoin.join("firm", JoinType.LEFT);
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone", JoinType.LEFT);
        Join<LocoMovementData, Loco> locoIDJoin = lm.join("locoID", JoinType.LEFT);
        Join<Loco, Firm> firmJoin = locoIDJoin.join("firm", JoinType.LEFT);

        // Step 4: Define the selection of the specific fields and map them to LocoMovementDTO
        cq.select(cb.construct(
                LocoMovementDTO.class,
                lm.get("id"),
                lm.get("date"),
                lm.get("time"),
                stnCodeJoin.get("code"),  // Join on Station to get its name
                locoIDJoin.get("locoNo"), // Join on Loco to get locoNo
                stnFirmJoin.get("name"),     // Join on Firm (through Loco) to get the firm name
                lm.get("stnFrameNum"),
                lm.get("packetType"),
                lm.get("locoFrameNum"),
                lm.get("absLocation"),
                lm.get("trainLen"),
                lm.get("locoSpeed"),
                lm.get("locoDir"),
                lm.get("emrStatus"),
                lm.get("locoMode"),
                lm.get("rfid"),
                lm.get("tin"),
                lm.get("brakeMode"),
                lm.get("locoRandNum"),
                lm.get("frameOffset"),
                lm.get("locoSOS"),
                lm.get("typOfSig"),
                lm.get("sigDir"),
                lm.get("lineNum"),
                lm.get("curSigAsp"),
                lm.get("nxtSigAsp"),
                lm.get("ma"),
                lm.get("gradient"),
                lm.get("appSigDist"),
                lm.get("toSpeed"),
                lm.get("toDistance"),
                lm.get("toReleaseDist"),
                lm.get("allocFreqPair"),
                lm.get("allocTDMASlot"),
                lm.get("stnRandNum"),
                lm.get("emrGenSOS"),
                lm.get("emrLocoSOS"),
                lm.get("profileId"),
                lm.get("staticProfileInfoUptoMA"),
                lm.get("crcStatus"),
                lm.get("locoNum"),
                lm.get("stnNum"),
                lm.get("zoneId"),
                lm.get("divId"),
                lm.get("kv"),
                lm.get("nmsId"),
                stnFirmJoin.get("name"),
                lm.get("destinationLocoCount"),
                lm.get("destinationLocoId"),
                lm.get("destToLocoSOS"),
                lm.get("referenceFrameNumber"),
                lm.get("trainLenInfo"),
                lm.get("appStnId"),
                lm.get("dstToTurnOut")
        ));
        Predicate filterPredicate = cb.conjunction();

        if (locoId != null && locoId > 0) {
            Predicate locoIDPredicate = cb.equal(locoIDJoin.get("id"), locoId); // Assuming locoNo is the field to filter
            filterPredicate = cb.and(filterPredicate, locoIDPredicate);
        }
        if (stationId != null && stationId > 0) {
            Predicate predicate = cb.equal(stnCodeJoin.get("id"), stationId); // Assuming locoNo is the field to filter
            filterPredicate = cb.and(filterPredicate, predicate);
        }

        if (fromDate != null && toDate != null) {

            LocalDate fromLocalDate = fromDate.toLocalDate();
            LocalTime fromLocalTime = fromDate.toLocalTime();
            LocalDate toLocalDate = toDate.toLocalDate();
            LocalTime toLocalTime = toDate.toLocalTime();

            // Create predicates for date and time columns
            Predicate datePredicate = cb.between(lm.get("date"), fromLocalDate, toLocalDate);
            Predicate timePredicate = cb.between(lm.get("time"), fromLocalTime, toLocalTime);

            filterPredicate = cb.and(datePredicate, timePredicate);
        }

        cq.where(filterPredicate);
        // Step 6: Create count query to get the total records count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<LocoMovementData> lmForCount = countQuery.from(LocoMovementData.class);

        // Apply count operation
        countQuery.select(cb.count(lmForCount));

        // Apply the same filter predicate to the count query
        countQuery.where(filterPredicate);

        List<LocoMovementDTO> totalRecordsDTO = entityManager.createQuery(cq).getResultList();

        Long totalRecords = (long) totalRecordsDTO.size();
        // Execute the query and get the total record count


        //    Long totalRecords = 100L;
        // Step 6: Apply pagination if Pageable is provided
        if (pageable != null) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            cq.select(cb.construct(LocoMovementDTO.class,
                    // Construct fields (same as above)
                    lm.get("id"),
                    lm.get("date"),
                    lm.get("time"),
                    stnCodeJoin.get("code"),
                    locoIDJoin.get("locoNo"),
                    stnFirmJoin.get("name"),
                    lm.get("stnFrameNum"),
                    lm.get("packetType"),
                    lm.get("locoFrameNum"),
                    lm.get("absLocation"),
                    lm.get("trainLen"),
                    lm.get("locoSpeed"),
                    lm.get("locoDir"),
                    lm.get("emrStatus"),
                    lm.get("locoMode"),
                    lm.get("rfid"),
                    lm.get("tin"),
                    lm.get("brakeMode"),
                    lm.get("locoRandNum"),
                    lm.get("frameOffset"),
                    lm.get("locoSOS"),
                    lm.get("typOfSig"),
                    lm.get("sigDir"),
                    lm.get("lineNum"),
                    lm.get("curSigAsp"),
                    lm.get("nxtSigAsp"),
                    lm.get("ma"),
                    lm.get("gradient"),
                    lm.get("appSigDist"),
                    lm.get("toSpeed"),
                    lm.get("toDistance"),
                    lm.get("toReleaseDist"),
                    lm.get("allocFreqPair"),
                    lm.get("allocTDMASlot"),
                    lm.get("stnRandNum"),
                    lm.get("emrGenSOS"),
                    lm.get("emrLocoSOS"),
                    lm.get("profileId"),
                    lm.get("staticProfileInfoUptoMA"),
                    lm.get("crcStatus"),
                    lm.get("locoNum"),
                    lm.get("stnNum"),
//                    lm.get("zoneId"),
                    zoneJoin.get("code"),
//                    lm.get("divId"),
                    divisionJoin.get("code"),
                    lm.get("kv"),
                    lm.get("nmsId"),
                    stnFirmJoin.get("name"),
                    lm.get("destinationLocoCount"),
                    lm.get("destinationLocoId"),
                    lm.get("destToLocoSOS"),
                    lm.get("referenceFrameNumber"),
                    lm.get("trainLenInfo"),
                    lm.get("appStnId"),
                    lm.get("dstToTurnOut")
            ));

            // Create descending order predicate for id
            Order order = cb.desc(lm.get("id"));

            // Apply the order to the query
            cq.orderBy(order);

            // Apply pagination
            TypedQuery<LocoMovementDTO> query = entityManager.createQuery(cq);
            query.setFirstResult(pageNumber * pageSize); // Set offset
            query.setMaxResults(pageSize); // Set limit

            // Execute the query and get paginated results
            List<LocoMovementDTO> resultList = query.getResultList();

            // Return both paginated results and total record count in a map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", resultList);
            resultMap.put("totalRecords", totalRecords);

            return resultMap;
        }

        // Step 7: Execute the query without pagination if no Pageable is provided
        TypedQuery<LocoMovementDTO> query = entityManager.createQuery(cq);
        List<LocoMovementDTO> resultList = query.getResultList();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultList);
        resultMap.put("totalRecords", totalRecords);

        return resultMap;
        //   return locoDataRepository.findLocoMovement(pageable);
    }

    public long postLocoMovementData(LocoMovementDTO locoMovementData) {

        log.info("LocoMovementDTO : {}", locoMovementData.toString());

        LocoMovementData newLocoMovementData = objectMapper.convertValue(locoMovementData, com.railbit.tcasanalysis.entity.LocoMovementData.class);
        log.info("newLocoMovementData : {}", newLocoMovementData);
        newLocoMovementData.setDate(
                Optional.ofNullable(locoMovementData.getDate())
                        .map(date -> {
                            try {
                                return date;
                            } catch (DateTimeParseException e) {
                                return LocalDate.of(1970, 1, 1);
                            }
                        })
                        .orElse(LocalDate.of(1970, 1, 1))
        );

        newLocoMovementData.setTime(
                Optional.ofNullable(locoMovementData.getTime())
                        .map(time -> {
                            try {
                                return time;
                            } catch (DateTimeParseException e) {
                                return LocalTime.of(0, 0);
                            }
                        })
                        .orElse(LocalTime.of(0, 0))
        );

        Station station = new Station();
        Loco loco = new Loco();

        if (locoMovementData.getStnCode() != null && !locoMovementData.getStnCode().isEmpty()) {
            Station station1 = stationRepo.findByTcassubsysid(Integer.valueOf(locoMovementData.getStnCode()));
            if (station1 != null && station1.getId() != null) {
                station = station1;
            }
        }

        if (!StringUtils.isEmpty(locoMovementData.getLocoID())) {
            Loco loco1 = locoRepo.findByNmsLocoId(locoMovementData.getLocoID());
            if (loco1 != null && loco1.getId() != null) {
                loco = loco1;
            }
        }

        if (station.getId() == null) {
            station.setCode(locoMovementData.getStnCode());
            station.setName(locoMovementData.getStnCode());
            station.setTcassubsysid(Integer.valueOf(locoMovementData.getStnCode()));
            station = stationRepo.save(station);
        }

        if (loco.getId() == null) {
            loco.setLocoNo(locoMovementData.getLocoID());
            loco.setNmsLocoId(locoMovementData.getLocoID());
            loco = locoRepo.save(loco);
        }

        newLocoMovementData.setStnCode(station);
        newLocoMovementData.setLocoID(loco);
        newLocoMovementData.setStnFrameNum(locoMovementData.getStnFrameNum());
        newLocoMovementData.setPacketType(locoMovementData.getPacketType());
        newLocoMovementData.setLocoFrameNum(locoMovementData.getLocoFrameNum());
        newLocoMovementData.setAbsLocation(locoMovementData.getAbsLocation());
        newLocoMovementData.setTrainLen(locoMovementData.getTrainLen());
        newLocoMovementData.setLocoSpeed(locoMovementData.getLocoSpeed());
        newLocoMovementData.setEmrStatus(locoMovementData.getEmrStatus());
        newLocoMovementData.setLocoMode(locoMovementData.getLocoMode());
        newLocoMovementData.setRfid(locoMovementData.getRfid());
        newLocoMovementData.setTin(locoMovementData.getTin());
        newLocoMovementData.setBrakeMode(locoMovementData.getBrakeMode());
        newLocoMovementData.setLocoRandNum(locoMovementData.getLocoRandNum());
        newLocoMovementData.setFrameOffset(locoMovementData.getFrameOffset());
        newLocoMovementData.setLocoSOS(locoMovementData.getLocoSOS());
        newLocoMovementData.setTypOfSig(locoMovementData.getTypOfSig());
        newLocoMovementData.setSigDir(locoMovementData.getSigDir());
        newLocoMovementData.setLineNum(locoMovementData.getLineNum());
        newLocoMovementData.setCurSigAsp(locoMovementData.getCurSigAsp());
        newLocoMovementData.setNxtSigAsp(locoMovementData.getNxtSigAsp());
        newLocoMovementData.setMa(locoMovementData.getMa());
        newLocoMovementData.setGradient(locoMovementData.getGradient());
        newLocoMovementData.setAppSigDist(locoMovementData.getAppSigDist());
        newLocoMovementData.setToSpeed(locoMovementData.getToSpeed());
        newLocoMovementData.setToDistance(locoMovementData.getToDistance());
        newLocoMovementData.setToReleaseDist(locoMovementData.getToReleaseDist());
        newLocoMovementData.setAllocFreqPair(locoMovementData.getAllocFreqPair());
        newLocoMovementData.setAllocTDMASlot(locoMovementData.getAllocTDMASlot());
        newLocoMovementData.setStnRandNum(locoMovementData.getStnRandNum());
        newLocoMovementData.setEmrGenSOS(locoMovementData.getEmrGenSOS());
        newLocoMovementData.setEmrLocoSOS(locoMovementData.getEmrLocoSOS());
        newLocoMovementData.setProfileId(locoMovementData.getProfileId());
        newLocoMovementData.setStaticProfileInfoUptoMA(locoMovementData.getStaticProfileInfoUptoMA());

        LocoMovementData newLocoMovementData1 = locoDataRepository.save(newLocoMovementData);

        try {
            NmsIncidentDate nmsIncidentDate = new NmsIncidentDate();
            nmsIncidentDate.setDate(newLocoMovementData.getDate());
            nmsIncidentDateRepo.save(nmsIncidentDate);
        } catch (Exception ignored) {

        }


        return newLocoMovementData1.getId();
    }

    // ----------------------- Fetch Records for Analytics ---------------------------------------------------------------

    public List<String> getDistinctLocoIds(LocalDateTime fromDate, LocalDateTime toDate) throws ParseException {

        String fromDateStr = fromDate.toLocalDate().toString();
        String fromTimeStr = fromDate.toLocalTime().toString();
        String toTimeStr = toDate.toLocalTime().toString();
        return locoDataRepository.getDistinctLocoIds(fromDateStr, fromTimeStr, toTimeStr);
    }

    public List<NmsIncident> getLocoIncident(LocalDateTime fromDate, LocalDateTime toDate) throws ParseException {
        String fromDateStr = fromDate.toLocalDate().toString();
        String fromTimeStr = fromDate.toLocalTime().toString();
        String toTimeStr = toDate.toLocalTime().toString();
        return nmsIncidentRepo.getFilteredLocoMovementData(fromDateStr);
    }


    public List<LocoMovementData> getAllLocoMovementData(String locoid, LocalDateTime fromDate, LocalDateTime toDate) throws ParseException {
        String fromDateStr = fromDate.toLocalDate().toString();
        String fromTimeStr = fromDate.toLocalTime().toString();
        String toTimeStr = toDate.toLocalTime().toString();
        return locoDataRepository.getFilteredLocoMovementData(locoid, fromDateStr, fromTimeStr, toTimeStr);
    }

    public LocoMovementData getAllLocoMovementDataWithOtherMode(String locoid, LocalDateTime fromDate, LocalDateTime toDate, String LcoMode) throws ParseException {
        String fromDateStr = fromDate.toLocalDate().toString();
        String fromTimeStr = fromDate.toLocalTime().toString();
        String toTimeStr = toDate.toLocalTime().toString();
        return locoDataRepository.getFilteredLocoMovementDataWithLocoMode(locoid, fromDateStr, fromTimeStr, toTimeStr, LcoMode);
    }

    public List<Map<String, Object>> getWeeklySummary(LocalDate weekStart) {
        // Define start and end of the week (Monday to Sunday)
        LocalDateTime startDate = weekStart.atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(7).minusSeconds(1);

        // Fetch data for the week
        List<LocoMovementSummary> events = locoDataRepository.findByReportFromTimeBetween(startDate, endDate);

        // Group and aggregate data by date
        Map<LocalDate, Map<String, Object>> dailySummary = events.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getReportFromTime().toLocalDate(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                dailyEvents -> {
                                    Map<String, Object> summary = new HashMap<>();
                                    summary.put("totalTimeMin", dailyEvents.stream()
                                            .mapToInt(LocoMovementSummary::getTotalTimeMin)
                                            .sum());
                                    summary.put("travelledMeter", dailyEvents.stream()
                                            .map(LocoMovementSummary::getTravelledMeter)
                                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                                    return summary;
                                }
                        )
                ));

        // Prepare a complete weekly summary with defaults for missing days
        List<Map<String, Object>> weeklySummary = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = weekStart.plusDays(i);
            Map<String, Object> dailyData = dailySummary.getOrDefault(currentDate, new HashMap<>());

            // Populate missing data with 0
            Map<String, Object> result = new HashMap<>();
            result.put("date", currentDate); // Include the date as LocalDate
            result.put("day", currentDate.getDayOfWeek().name().substring(0, 3)); // Get the first 3 letters of the day name
            result.put("totalTimeMin", dailyData.getOrDefault("totalTimeMin", 0));
            result.put("travelledMeter", dailyData.getOrDefault("travelledMeter", BigDecimal.ZERO));

            weeklySummary.add(result);
        }

        return weeklySummary;
    }

//    public List<LocoMovementData> getFilteredCurrentDateLocoData(String locoID, String startDateString, String endDateString) throws ParseException {
//        // Check if filtering by date is needed
//        Date startDate = (startDateString != null) ? mysqlDateFormat.parse(startDateString) : null;
//        Date endDate = (endDateString != null) ? mysqlDateFormat.parse(endDateString) : null;
//
//        Pageable pageable = PageRequest.of(0, 100);
//
//        if (locoID != null && startDate != null && endDate != null) {
//            // Filter by locoID and date range
//            return locoDataRepository.findByLocoIDAndDateBetweenOrderByIdAsc(locoID, startDate, endDate);
//        } else if (locoID != null) {
//            // Filter by locoID only
//            return locoDataRepository.findByLocoIDOrderByIdDesc(locoID,pageable);
//        } else if (startDate != null && endDate != null) {
//            // Filter by date range only
//            return locoDataRepository.findByDateBetweenOrderByIdDesc(startDate, endDate,pageable);
//        } else {
//            // No filters applied, return all data
//            return locoDataRepository.findAllByOrderByIdDesc(pageable);
//        }
//    }



    public Map<String, String> getRunningLocosWithLastPacket() {
        String sql = "WITH LatestLocoMovement AS (" +
                "    SELECT locoID, createdDateTime, " +
                "           ROW_NUMBER() OVER (PARTITION BY locoID ORDER BY createdDateTime DESC) AS rn " +
                "    FROM locomovementdata " +
                "    WHERE date = CURRENT_DATE" +
                ") " +
                "SELECT loco.locoNo, lm.createdDateTime " +
                "FROM LatestLocoMovement lm " +
                "JOIN loco loco ON lm.locoID = loco.id " +
                "WHERE lm.rn = 1";

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Map<String, String> locoLastPacketMap = new HashMap<>();
        for (Object[] result : results) {
            String locoNo = (String) result[0];
            Timestamp timestamp = (Timestamp) result[1];
            LocalDateTime lastPacketTime = timestamp.toLocalDateTime();
            String formattedTime = lastPacketTime.format(formatter);
            locoLastPacketMap.put(locoNo, formattedTime);
        }

        return locoLastPacketMap;
    }


    //    public AlertsCount getAlertsCount(LocalDate fromDate, LocalDate toDate) {
//        int spareCount = locoDataRepository.countByEmrStatusAndDate("spare", fromDate, toDate);
//        int headOnCollisionCount = locoDataRepository.countByEmrStatusAndDate("Head On Collision", fromDate, toDate);
//        int rearEndCollisionCount = locoDataRepository.countByEmrStatusAndDate("Rear End Collision", fromDate, toDate);
//        int sosLocoCount = locoDataRepository.countByEmrStatusAndDate("Sos", fromDate, toDate);
//        int sosStationCount = locoDataRepository.countByEmrGenSOSAndDate("Sos", fromDate, toDate);
//        int overrideModeCount = locoDataRepository.countByLocoModeAndDate("override", fromDate, toDate);
//        int tripModeCount = locoDataRepository.countByLocoModeAndDate("trip", fromDate, toDate);
//
//        return new AlertsCount(spareCount, headOnCollisionCount, rearEndCollisionCount, sosLocoCount, sosStationCount, overrideModeCount, tripModeCount);
//    }

    public AlertsCount getAlertsCount(LocalDate fromDate, LocalDate toDate) {
        List<Object[]> results = locoDataRepository.getAllAlertCounts(fromDate, toDate);

        if (results == null || results.isEmpty() || results.get(0) == null) {
            return new AlertsCount(0, 0, 0, 0, 0, 0, 0);
        }

        Object[] row = results.get(0);

        return new AlertsCount(
                row[0] != null ? ((Number) row[0]).intValue() : 0,
                row[1] != null ? ((Number) row[1]).intValue() : 0,
                row[2] != null ? ((Number) row[2]).intValue() : 0,
                row[3] != null ? ((Number) row[3]).intValue() : 0,
                row[4] != null ? ((Number) row[4]).intValue() : 0,
                row[5] != null ? ((Number) row[5]).intValue() : 0,
                row[6] != null ? ((Number) row[6]).intValue() : 0
        );
    }




    public List<LocoMovementData> getLocoMovementListByAlertAndDateRange(String alertType, LocalDate fromDate, LocalDate toDate) {

        if (alertType.equalsIgnoreCase("Head On Collision")) {
            return locoDataRepository.locoMovementDataListByEmrStatusAndDate("Head On Collision", fromDate, toDate);
        } else if (alertType.equalsIgnoreCase("Rear End Collision")) {
            return locoDataRepository.locoMovementDataListByEmrStatusAndDate("Rear End Collision", fromDate, toDate);
        } else if (alertType.equalsIgnoreCase("SOS (Loco)")) {
            return locoDataRepository.locoMovementDataListByEmrStatusAndDate("Sos", fromDate, toDate);
        } else if (alertType.equalsIgnoreCase("SOS (Station)")) {
            return locoDataRepository.locoMovementDataListByEmrGenSOSAndDate("Sos", fromDate, toDate);
        } else if (alertType.equalsIgnoreCase("Override Mode")) {
            return locoDataRepository.locoMovementDataListByLocoModeAndDate("override", fromDate, toDate);
        } else if (alertType.equalsIgnoreCase("Trip Mode")) {
            return locoDataRepository.locoMovementDataListByLocoModeAndDate("trip", fromDate, toDate);
        }

        return new ArrayList<>();
    }



//    public Map<String, Object> getAvgClosureTime(LocalDate startDate, LocalDate endDate) {
//        String sql = """
//        SELECT
//            AVG(TIMESTAMPDIFF(SECOND,
//                STR_TO_DATE(CONCAT(DATE_FORMAT(tripDate, '%Y-%m-%d'), ' 12:00:00'), '%Y-%m-%d %H:%i:%s'),
//                closureDateTime
//            )) AS avg_seconds
//        FROM
//            incidentticket
//        WHERE
//            status = 0
//            AND tripDate BETWEEN :startDate AND :endDate
//            AND closureDateTime IS NOT NULL
//            AND tripDate IS NOT NULL
//        """;
//
//        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter("startDate", java.sql.Date.valueOf(startDate));
//        query.setParameter("endDate", java.sql.Date.valueOf(endDate));
//
//        Object result = query.getSingleResult();
//        Map<String, Object> resultMap = new HashMap<>();
//
//        if (result != null) {
//            Number avgSeconds = (Number) result;
//            if (avgSeconds != null) {
//                long totalSeconds = avgSeconds.longValue();
//
//                long days = totalSeconds / 86400;
//                long hours = (totalSeconds % 86400) / 3600;
//                long minutes = (totalSeconds % 3600) / 60;
//
//                String formatted = days + " days " + hours + " hours " + minutes + " minutes";
//
//                BigDecimal avgDaysDecimal = BigDecimal.valueOf((double) totalSeconds / 86400)
//                        .setScale(2, RoundingMode.HALF_UP);
//
//                resultMap.put("formatted", formatted);
//                resultMap.put("avgDays", avgDaysDecimal.doubleValue());
//            }
//        }
//
//        return resultMap;
//    }

}