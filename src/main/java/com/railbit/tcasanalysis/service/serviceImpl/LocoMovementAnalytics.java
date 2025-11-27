package com.railbit.tcasanalysis.service.serviceImpl;

import com.railbit.tcasanalysis.DTO.incident.IncidentDTO;
import com.railbit.tcasanalysis.DTO.incident.NMSIncidentDTO;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.loco.EvntMsg;
import com.railbit.tcasanalysis.entity.loco.Loco;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncident;
import com.railbit.tcasanalysis.entity.nmspackets.NmsIncidentDate;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import com.railbit.tcasanalysis.repository.KavachDashboardRepository;
import com.railbit.tcasanalysis.repository.NmsIncidentDateRepo;
import com.railbit.tcasanalysis.repository.NmsIncidentRepo;
import com.railbit.tcasanalysis.service.*;
import com.railbit.tcasanalysis.util.LocoDataCalculation;
import com.railbit.tcasanalysis.util.LocoFaultMessage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class LocoMovementAnalytics {
    private static final Logger log = LoggerFactory.getLogger(LocoMovementAnalytics.class);

    private LocoMovementDataService locoMovementDataService;
    private LocoDataCalculation locoDataCalculation;
    private final StationService stationService;
    private final DivisionService divisionService;
    private LocoPktRetrieverConfigService locoPktRetrieverConfigService;
    private LocoMovementSummaryService locoMovementSummaryService;
    private final KavachDashboardRepository kavachDashboardRepository;
    private LocoFaultMessage locoFaultMessage;
    private NmsIncidentDateRepo nmsIncidentDateRepo;
    private NmsIncidentRepo nmsIncidentRepo;
    private LocoService locoService;
    private StationNMSPKTRepository StationNMSPKTRepository;

    EntityManager entityManager;


    @Scheduled(fixedRate = 4 * 60 * 1000)
    public void nmsStationStatus() {

//          log.info("nmsStationStatus at :{}",new Date());
          StationNMSPKTRepository.findLatestStationaryPackets();
    }


    @Scheduled(fixedRate = 7 * 60 * 1000)
    public void ticketsNMSIncident() {

//        log.info("testTicketsNMSIncident at :{}",new Date());
        NmsIncidentDate incidentDate=nmsIncidentDateRepo.findFirstRecord();
        if(incidentDate==null){
//            log.warn("NmsIncidentDate not found :{}",new Date());
            return;
        }
        LocalDate date = incidentDate.getDate();
        LocalDateTime currentTime = date.atTime(LocalTime.MIN);
        LocalDateTime toTime = date.atTime(LocalTime.MAX);
        nmsIncidentDateRepo.deleteById(incidentDate.getId());
       // Fetch incidents
        List<IncidentDTO> allLocoIncidents = getAllLocoIncidents(currentTime, toTime);
        System.out.println("modeChangeDescriptions :"+allLocoIncidents.toString());
        if(!allLocoIncidents.isEmpty()){
            int i=0;
            for (IncidentDTO incidentDTO:allLocoIncidents){
                createIncidentTicket(incidentDTO,i);
                i++;
            }
        }
    }

   // @Scheduled(fixedRate = 10 * 60 * 1000)
    public void fetchAndProcessData() {

//        log.info("fetchAndProcessData at :{}",new Date());
        LocoPktRetrieverConfig config = locoPktRetrieverConfigService.getLocoPktRetrieverConfig();
        if (config == null) {
//            log.warn("LocoPktRetrieverConfig not found !!!!");
            return;
        }

        LocalDateTime lastSyncTime = config.getLastSync();
        LocalDateTime addedTime = lastSyncTime.plusHours(1);
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endOfCurrentDay = currentTime.toLocalDate().atTime(LocalTime.MAX);

        if (addedTime.isAfter(endOfCurrentDay)) {
            addedTime = endOfCurrentDay;
        }

        if (addedTime.toLocalDate().isAfter(lastSyncTime.toLocalDate())) {
            LocalDateTime endOfDay = lastSyncTime.toLocalDate().atTime(LocalTime.MAX);
            config.setLastSync(endOfDay);
            locoPktRetrieverConfigService.updateLocoPktRetrieverConfig(config);
            fetchRecordsBetween(lastSyncTime, endOfDay);
        } else if (addedTime.isBefore(currentTime)) {
            if (addedTime.isAfter(lastSyncTime)) {
                config.setLastSync(addedTime);
                locoPktRetrieverConfigService.updateLocoPktRetrieverConfig(config);
                fetchRecordsBetween(lastSyncTime, addedTime);
            }
        }
    }

    private void fetchRecordsBetween(LocalDateTime start, LocalDateTime end) {
        try {
            List<String> locoIds = locoMovementDataService.getDistinctLocoIds(start, end);
            locoIds.forEach(locoid -> {
                List<LocoMovementData> records = null;
                try {
//                    log.info("Fetching LOCO PKT for locoId :{} {} {}",locoIds,start,end);
                    records = locoMovementDataService.getAllLocoMovementData(locoid, start, end);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                processRecords(records,Integer.parseInt(locoid),start,end);
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void processRecords(List<LocoMovementData> records,int locoId,LocalDateTime start, LocalDateTime end) {
        List<LocoMovementData>  filteredRecords= locoDataCalculation.removeDuplicates(records);
        List<LocoMovementData>  sortedRecords= locoDataCalculation.sortByTimeAsc(filteredRecords);

        float totalKmTraveled = locoDataCalculation.calculateTotalMetersTraveled(sortedRecords);
        long totalMinutes = locoDataCalculation.calculateTotalTravelledTimeInMinutes(sortedRecords);

        LocoMovementSummary movementSummary = LocoMovementSummary.builder()
                .locoId(locoId)
                .eventTime(LocalDateTime.now())
                .oemId(1)
                .travelledMeter(BigDecimal.valueOf(totalKmTraveled))
                .totalTimeMin((int) totalMinutes)
                .reportFromTime(start)
                .reportToTime(end)
                .build();
        locoMovementSummaryService.saveLocoMovementSummary(movementSummary);

        updateOrInsertKavachDashboard(totalKmTraveled, totalMinutes);
    }

    public void updateOrInsertKavachDashboard(float totalKmTraveled, long totalMinutes) {
        // Calculate total hours and minutes from total minutes
        int hours = (int) (totalMinutes / 60);  // Get the whole number of hours
        int minutes = (int) (totalMinutes % 60);  // Get the remaining minutes

        // Convert to LocalTime object (HH:mm format)
        LocalTime totalHours = LocalTime.of(hours, minutes);

        // Check if a dashboard record exists
        Optional<KavachDashboard> existingDashboardOpt = kavachDashboardRepository.findAll().stream().findFirst(); // Adjust as necessary

        if (existingDashboardOpt.isPresent()) {
            // Get the existing dashboard record
            KavachDashboard existingDashboard = existingDashboardOpt.get();

            float totalDbKm = 0.0F;

            if(existingDashboard.getTotal_km() != null){

                totalDbKm = existingDashboard.getTotal_km();
            }

            totalKmTraveled = totalKmTraveled / 1000;

            // Sum the existing total km and new total km
            float updatedTotalKmTraveled = totalDbKm + totalKmTraveled; // Sum the existing and new km

            // Calculate the updated total hours (by adding the new hours and minutes to the existing ones)
            int currentHours = (existingDashboard.getTotal_hours() != null ? existingDashboard.getTotal_hours().getHour() : 0);
            int currentMinutes = (existingDashboard.getTotal_hours() != null ? existingDashboard.getTotal_hours().getMinute() : 0);

            int totalHoursToAdd = currentHours + hours;  // Add current hours and new hours
            int totalMinutesToAdd = currentMinutes + minutes;  // Add current minutes and new minutes

            // Adjust if minutes exceed 60
            if (totalMinutesToAdd >= 60) {
                totalHoursToAdd += totalMinutesToAdd / 60;  // Add the overflow hours
                totalMinutesToAdd = totalMinutesToAdd % 60;  // Adjust minutes to be less than 60
            }

            // Set the updated hours and minutes
            LocalTime updatedTotalHours = LocalTime.of(totalHoursToAdd, totalMinutesToAdd);

            // Update the existing dashboard record with the new summed values
            existingDashboard.setTotal_km(updatedTotalKmTraveled);  // Convert to float if necessary
            existingDashboard.setTotal_hours(updatedTotalHours);  // Set the updated total hours
            existingDashboard.setTime_stamp(LocalDateTime.now());

            // Save the updated dashboard record
            kavachDashboardRepository.save(existingDashboard);
        } else {
            // If no existing dashboard, create a new one and save it
            KavachDashboard newDashboard = new KavachDashboard();
            newDashboard.setTotal_km(totalKmTraveled / 1000);  // Convert Double to float if necessary
            newDashboard.setTotal_hours(totalHours);  // Set the calculated total hours
            newDashboard.setTime_stamp(LocalDateTime.now());

            // Save the new dashboard
            kavachDashboardRepository.save(newDashboard);
        }
    }

    public int createIncidentTicket(IncidentDTO incidentDTO, int i) {
        try {

            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sfI = new SimpleDateFormat("yy/MM/dd");
            SimpleDateFormat sfym = new SimpleDateFormat("yyyy-MM");
            SimpleDateFormat ymdd = new SimpleDateFormat("yyyy-MM-dd");

            String dateId = sf.format(incidentDTO.getDate()) + incidentDTO.getTime().replace(":", "");
            String nmsId = incidentDTO.getLocoNo() + "_" + dateId;

            if (nmsIncidentRepo.findByNmsIncidentId(nmsId).isPresent()) {
                return 0;
            }

            int incidentCount=0;
            String tripDate=ymdd.format(incidentDTO.getDate());
            Integer dayIncidentCount = nmsIncidentRepo.countLocoIncidentRecords(incidentDTO.getStationDao().getDivision().getId(), tripDate);
            incidentCount = (dayIncidentCount == null || dayIncidentCount <= 0) ? 1 : dayIncidentCount + 1;


            String incidentTicket = String.format("%s/%s/%d",
                    incidentDTO.getStationDao().getDivision().getCode(),
                    sfI.format(incidentDTO.getDate()),
                    incidentCount);

            int tripNo = 1;
            String tripMonth = sfym.format(incidentDTO.getDate());
            NmsIncident locoLastTrip = nmsIncidentRepo.findLocoLastRecord(
                    (long) incidentDTO.getLoco().getId(), tripMonth);

            if (locoLastTrip != null) {
                boolean isUnidentified = "Unidentified".equalsIgnoreCase(locoLastTrip.getLocoDir());
                boolean isSameDirection = locoLastTrip.getLocoDir().equalsIgnoreCase(incidentDTO.getLocoDir());
                tripNo = (isUnidentified || isSameDirection) ? locoLastTrip.getTripNo() : locoLastTrip.getTripNo() + 1;
            }

             NmsIncident nmsIncident = NmsIncident.builder()
                    .nmsIncidentId(nmsId)
                    .tripDate(incidentDTO.getDate())
                    .incidentTime(incidentDTO.getTime())
                    .locoDir(incidentDTO.getLocoDir())
                    .stnId((long) incidentDTO.getStationDao().getId())
                    .divId(incidentDTO.getStationDao().getDivision().getId())
                    .description(incidentDTO.getBriefDescription())
                    .issueCategory(incidentDTO.getIssueCategory())
                    .tripNo(tripNo)
                    .incidentTicket(incidentTicket)
                    .locoId((long) incidentDTO.getLoco().getId())
                    .build();

            nmsIncidentRepo.save(nmsIncident);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Failure
        }
    }


    public List<IncidentDTO> getAllLocoIncidents(LocalDateTime start, LocalDateTime end) {
        List<IncidentDTO>  modeChangeDescriptions=new ArrayList<>();
        try {
            List<String> locoIds = locoMovementDataService.getDistinctLocoIds(start, end);
            locoIds.forEach(locoid -> {
                List<LocoMovementData> records = null;
                try {
//                    log.info("Fetching LOCO PKT for locoId :{} {} {}",locoid,start,end);
                    records = locoMovementDataService.getAllLocoMovementData(locoid, start, end);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                List<IncidentDTO> localrecords=  processIncidentRecords(records,Integer.parseInt(locoid),start,end);
                modeChangeDescriptions.addAll(localrecords);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        modeChangeDescriptions.sort(Comparator.comparing(IncidentDTO::getTime));
        return modeChangeDescriptions;
    }

    /**
     * Fetches a paginated list of locomotive incidents based on various filtering criteria.
     *
     * @param zoneId      the ID of the zone to filter incidents by; can be null or zero to ignore this filter.
     * @param divisionId  the ID of the division to filter incidents by; can be null or zero to ignore this filter.
     * @param stationId   the ID of the station to filter incidents by; can be null or zero to ignore this filter.
     * @param locoId      the ID of the locomotive to filter incidents by; can be null or zero to ignore this filter.
     * @param category    the category of the incident to filter by; can be null or empty to ignore this filter.
     * @param fromDate    the start date and time for filtering incidents; cannot be null.
     * @param toDate      the end date and time for filtering incidents; cannot be null.
     * @param searchQuery a search query to filter incidents by various fields; can be null or empty to ignore this filter.
     * @param pageable    the pagination information, including page number and size; cannot be null.
     * @return a paginated list of {@link IncidentDTO} objects that match the filtering criteria.
     */
    @Transactional
    public Page<NMSIncidentDTO> fetchLocoIncidents(Long zoneId, Long divisionId, Long stationId, Long locoId, String category, LocalDateTime fromDate, LocalDateTime toDate, String searchQuery, Pageable pageable) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<NMSIncidentDTO> criteriaQuery = criteriaBuilder.createQuery(NMSIncidentDTO.class);
        Root<NmsIncident> root = criteriaQuery.from(NmsIncident.class);

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

//        if (zoneId != null && zoneId > 0) {
//            predicates.add(criteriaBuilder.equal(zoneRoot.get("id"), zoneId));
//        }
        if (divisionId != null && divisionId > 0) {
            predicates.add(criteriaBuilder.equal(root.get("divId"), divisionId));
        }
        if (stationId != null && stationId > 0) {
            predicates.add(criteriaBuilder.equal(root.get("stnId"), stationId));
        }
        if (locoId != null && locoId > 0) {
            predicates.add(criteriaBuilder.equal(root.get("locoId"), locoId));
        }

        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            predicates.add(criteriaBuilder.equal(root.get("issueCategory"), category));
        }

        // For Search Item
        if (searchQuery != null && !searchQuery.isEmpty()) {
            Predicate searchPredicate = criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("incidentTicket")), "%" + searchQuery.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("issueCategory")), "%" + searchQuery.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + searchQuery.toLowerCase() + "%")
            );
            predicates.add(searchPredicate);
        }

        Expression<String> tripDateTimeString = criteriaBuilder.concat(
                criteriaBuilder.concat(root.get("tripDate").as(String.class), " "),
                root.get("incidentTime")
        );

        Expression<LocalDateTime> tripDateTime = criteriaBuilder.function(
                "TO_TIMESTAMP",
                LocalDateTime.class,
                tripDateTimeString,
                criteriaBuilder.literal("YYYY-MM-DD HH24:MI:SS")
        );

        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(root.get("tripDate"), fromDate, toDate));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));

        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));

        String locoFirm = "";
        String zoneCode = "";
        String divCode = "";
        String stnCode = "";
        String stnFirm = "";

        criteriaQuery.select(criteriaBuilder.construct(NMSIncidentDTO.class,
                root.get("incidentTicket"),
                root.get("issueCategory"),
                root.get("locoId"),
                criteriaBuilder.literal(locoFirm),
                criteriaBuilder.literal(locoFirm),
                criteriaBuilder.literal(locoFirm),
                criteriaBuilder.literal(locoFirm),
                criteriaBuilder.literal(zoneCode),
                root.get("divId"),
                criteriaBuilder.literal(divCode),
                root.get("stnId"),
                criteriaBuilder.literal(stnCode),
                criteriaBuilder.literal(stnFirm),
                root.get("tripDate"),
                root.get("incidentTime"),
                root.get("description")
        ));

        TypedQuery<NMSIncidentDTO> query = entityManager.createQuery(criteriaQuery);
        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<NMSIncidentDTO> result = query.getResultList();
        for (NMSIncidentDTO incident : result) {
            try {
                Station station = stationService.getStationById(Math.toIntExact(incident.getStnId()));
                if (station != null) {
                    incident.setStnCode(station.getCode());
                    incident.setStnOem(station.getFirm().getName());
                } else {
                    log.warn("Station not found for ID: {}", incident.getStnId());
                }
            } catch (Exception e) {
                log.error("Error fetching station for ID: {}", incident.getStnId(), e);
            }

            try {
                Division division = divisionService.getDivisionById(Math.toIntExact(incident.getDivId()));
                if (division != null) {
                    incident.setDivisionCode(division.getCode());
                    incident.setZoneCode(division.getZone().getCode());
                } else {
                    log.warn("Division not found for ID: {}", incident.getDivId());
                }
            } catch (Exception e) {
                log.error("Error fetching division for ID: {}", incident.getDivId(), e);
            }

            try {
                Loco loco = locoService.getLocoById(Math.toIntExact(incident.getLocoId()));
                if (loco != null) {
                    incident.setLocoNo(loco.getLocoNo());
                    if (loco.getFirm() != null) {
                        incident.setLocoOem(loco.getFirm().getName());
                    }
                    if (loco.getLocoType() != null) {
                        incident.setLocoType(loco.getLocoType().getName());
                    }
                    if (loco.getVersion() != null) {
                        incident.setLocoVersion(loco.getVersion());
                    }
                } else {
                    log.warn("Loco not found for ID: {}", incident.getLocoId());
                }
            } catch (Exception e) {
                log.error("Error fetching loco for ID: {}", incident.getLocoId(), e);
            }
        }

        return new PageImpl<>(result, pageable, totalRows);
    }

    // for live incident showing without ticket no.
    public List<IncidentDTO> fetchFilterLocoIncidents(LocalDateTime start, LocalDateTime end) {
        List<IncidentDTO>  modeChangeDescriptions=new ArrayList<>();
        try {
            List<String> locoIds = locoMovementDataService.getDistinctLocoIds(start, end);
            locoIds.forEach(locoid -> {
                List<LocoMovementData> records = null;
                try {
//                    log.info("Fetching LOCO PKT for locoId :{} {} {}",locoid,start,end);
                    if(locoid.equals("70")) {
                        records = locoMovementDataService.getAllLocoMovementData(locoid, start, end);
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                List<IncidentDTO> localrecords=  processIncidentRecords(records,Integer.parseInt(locoid),start,end);
                modeChangeDescriptions.addAll(localrecords);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        modeChangeDescriptions.sort(Comparator.comparing(IncidentDTO::getTime));
        return modeChangeDescriptions;
    }
    private List<IncidentDTO> processIncidentRecords(List<LocoMovementData> records, int locoId, LocalDateTime start, LocalDateTime end) {
        List<IncidentDTO> modeChangeDescriptions = new ArrayList<>();

        // Sort records by time in ascending order
        List<LocoMovementData> sortedRecordsForReport = locoDataCalculation.sortByTimeAsc(records);

        // Process mode changes and add to descriptions if non-empty
        List<IncidentDTO> modeChanges = findModeChanges(sortedRecordsForReport);
        if (!modeChanges.isEmpty()) {
            modeChangeDescriptions.addAll(modeChanges);
        }

        // Process break applied and add to descriptions if non-empty
        List<IncidentDTO> breakAppliedFSB = findFSBBreakApplied(sortedRecordsForReport);
        if (!breakAppliedFSB.isEmpty()) {
            modeChangeDescriptions.addAll(breakAppliedFSB);
        }

        List<IncidentDTO> breakAppliedEB = findEBBreakApplied(sortedRecordsForReport);
        if (!breakAppliedEB.isEmpty()) {
            modeChangeDescriptions.addAll(breakAppliedEB);
        }
        List<IncidentDTO> breakAppliedSpare = findSpareBreakApplied(sortedRecordsForReport);
        if (!breakAppliedSpare.isEmpty()) {
            modeChangeDescriptions.addAll(breakAppliedSpare);
        }

        return modeChangeDescriptions;
    }

    public List<IncidentDTO> findModeChanges(List<LocoMovementData> dataList) {
        List<IncidentDTO> modeChangeDescriptions = new ArrayList<>();
        if (dataList == null || dataList.isEmpty()) {
            return modeChangeDescriptions;
        }

        // Sort the list by time in ascending order
        dataList.sort(Comparator.comparing(LocoMovementData::getTime));

        String previousMode = null;

        for (int i = 0; i < dataList.size(); i++) {
            LocoMovementData currentData = dataList.get(i);

            if (previousMode != null && !previousMode.equals(currentData.getLocoMode())) {
                // Skip conditions where incidentDTO should be null
                if (previousMode.equals("Staff Responsible") && currentData.getLocoMode().equals("Limited Supervision")) {
                    previousMode = currentData.getLocoMode();
                    continue; // Skip adding IncidentDTO for SR -> LS
                }
                if (previousMode.equals("Limited Supervision") && currentData.getLocoMode().equals("Full Supervision")) {
                    previousMode = currentData.getLocoMode();
                    continue; // Skip adding IncidentDTO for LS -> FS
                }
                if (previousMode.equals("Staff Responsible") && currentData.getLocoMode().equals("Full Supervision")) {
                    previousMode = currentData.getLocoMode();
                    continue; // Skip adding IncidentDTO for SR -> FS
                }
                if(isSameTimeModeChange(currentData.getTime().toString(),modeChangeDescriptions)){
                    continue; //skip
                }

                IncidentDTO incidentDTO = new IncidentDTO();
                //Station station = stationService.findByTcassubsysid(Integer.parseInt(currentData.getStnCode()));
                Station station = currentData.getStnCode();
                String staName;

                if (station != null) {
                    staName = station.getCode();
                } else {
                    staName = currentData.getStnNum();
                }

                incidentDTO.setStation(staName);
                incidentDTO.setLocoNo(currentData.getLocoID().getLocoNo());
                Date date = Date.from(currentData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                incidentDTO.setDate(date);
                incidentDTO.setTime(currentData.getTime().toString());
                incidentDTO.setAbsLocation(currentData.getAbsLocation());
                incidentDTO.setLocoSpeed(currentData.getLocoSpeed());
                incidentDTO.setToSpeed(currentData.getToSpeed());
                incidentDTO.setBrakeMode(currentData.getBrakeMode());
                incidentDTO.setLocoMode(currentData.getLocoMode());
                incidentDTO.setMA(currentData.getStaticProfileInfoUptoMA());
                incidentDTO.setLocoDir(currentData.getLocoDir());
                incidentDTO.setStationDao(currentData.getStnCode());
                incidentDTO.setLoco(currentData.getLocoID());

               // String message = "At " + staName + ", the mode was changed from " + previousMode + " to " + currentData.getLocoMode();
                EvntMsg  evntMsg= locoFaultMessage.getModeChangeFaultMsg(incidentDTO,currentData.getLocoMode());

                String comeBackMsg="";
                try {
                    LocalDateTime fromDateTime = currentData.getDate().atTime(currentData.getTime());
                    LocalDateTime toDateTime = fromDateTime.plusMinutes(30);
                    LocoMovementData locoMovementData= locoMovementDataService.getAllLocoMovementDataWithOtherMode(String.valueOf(currentData.getLocoID().getId()),fromDateTime,toDateTime,currentData.getLocoMode());
                   if(locoMovementData!=null){
                       comeBackMsg= locoFaultMessage.getNextLocoModeMsg(locoMovementData.getAbsLocation(),locoMovementData.getTime().toString(),locoMovementData.getLocoMode());
                   }
                  }catch (Exception e){
                    log.error("ERROR in getNextLocoModeMsg :{}",e.getMessage());
                 }

                incidentDTO.setBriefDescription(evntMsg.getIssueMsg()+comeBackMsg);
                incidentDTO.setIssueCategory(evntMsg.getIssueCategory());
                incidentDTO.setIssue(evntMsg.getIssueName());
                modeChangeDescriptions.add(incidentDTO);
            }
           previousMode = currentData.getLocoMode();
        }
        return modeChangeDescriptions;
    }

    public boolean isSameTimeModeChange(String time, List<IncidentDTO> modeChangeDescriptions) {
        try {
            if (modeChangeDescriptions == null || modeChangeDescriptions.isEmpty()) {
                return false;
            }
            Map<String, IncidentDTO> timeToIncidentMap = modeChangeDescriptions.stream()
                    .filter(incident -> incident != null && incident.getTime() != null)
                    .collect(Collectors.toMap(IncidentDTO::getTime, incident -> incident, (a, b) -> a));
            return timeToIncidentMap.containsKey(time);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isSameTimeBrakeApplied(String time, List<IncidentDTO> list) {
        try {
            if (list == null || list.isEmpty()) {
                return false;
            }
            Map<String, IncidentDTO> timeToIncidentMap = list.stream()
                    .filter(incident -> incident != null && incident.getTime() != null)
                    .collect(Collectors.toMap(IncidentDTO::getTime, incident -> incident, (a, b) -> a));
            return timeToIncidentMap.containsKey(time);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public  List<IncidentDTO> findFSBBreakApplied(List<LocoMovementData> dataList) {
        List<IncidentDTO> modeChangeDescriptions = new ArrayList<>();
        if (dataList == null || dataList.isEmpty()) {
            return modeChangeDescriptions;
        }
        try {

            dataList.sort(Comparator.comparing(LocoMovementData::getTime));

            String previousMode = null;

            List<LocoMovementData> allFSBBreaks = getAllFSBBreaks(dataList);
            if (allFSBBreaks == null || allFSBBreaks.isEmpty()) {
                return modeChangeDescriptions;
            }
            //System.out.println(allFSBBreaks);
            allFSBBreaks.sort(Comparator.comparing(LocoMovementData::getTime));

            for (LocoMovementData currentData : allFSBBreaks) {

                IncidentDTO incidentDTO = new IncidentDTO();
                incidentDTO.setLocoNo(currentData.getLocoID().getLocoNo());
                Date date = Date.from(currentData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                incidentDTO.setDate(date);
                incidentDTO.setStation(currentData.getStnCode().getCode());
                incidentDTO.setTime(currentData.getTime().toString());
                incidentDTO.setAbsLocation(currentData.getAbsLocation());
                incidentDTO.setLocoSpeed(currentData.getLocoSpeed());
                incidentDTO.setToSpeed(currentData.getToSpeed());
                incidentDTO.setBrakeMode(currentData.getBrakeMode());
                incidentDTO.setLocoMode(currentData.getLocoMode());
                incidentDTO.setToDistance(currentData.getDstToTurnOut());
                incidentDTO.setLocoDir(currentData.getLocoDir());
                incidentDTO.setStationDao(currentData.getStnCode());
                incidentDTO.setLoco(currentData.getLocoID());
                incidentDTO.setMA(currentData.getMa());

                EvntMsg evtmsg=getBreakModeFaultMessage(incidentDTO);
                incidentDTO.setBriefDescription(evtmsg.getIssueMsg());
                incidentDTO.setIssueCategory(evtmsg.getIssueCategory());
                incidentDTO.setIssue(evtmsg.getIssueName());
                modeChangeDescriptions.add(incidentDTO);

            }

//            for (LocoMovementData currentData : dataList) {
//                if (previousMode != null  && !previousMode.equalsIgnoreCase(currentData.getBrakeMode())) {
//                    if (!is_NOS_OS_NSB_Break(currentData.getBrakeMode())) {
//                        if(isSameTimeBrakeApplied(currentData.getTime().toString(),modeChangeDescriptions)){
//                            continue; //skip
//                        }
//                        IncidentDTO incidentDTO = new IncidentDTO();
//                        //Station station = stationService.findByTcassubsysid(Integer.parseInt(currentData.getStnCode()));
//                        Station station = currentData.getStnCode();
//                        String staName = currentData.getStnCode().getName();
//                        if (station != null) {
//                            staName = station.getCode();
//                        }
//                        incidentDTO.setStation(staName);
//                        incidentDTO.setLocoNo(currentData.getLocoID().getLocoNo());
//                        Date date = Date.from(currentData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
//                        incidentDTO.setDate(date);
//                        incidentDTO.setTime(currentData.getTime().toString());
//                        incidentDTO.setAbsLocation(currentData.getAbsLocation());
//                        incidentDTO.setLocoSpeed(currentData.getLocoSpeed());
//                        incidentDTO.setToSpeed(currentData.getToSpeed());
//                        incidentDTO.setBrakeMode(currentData.getBrakeMode());
//                        incidentDTO.setLocoMode(currentData.getLocoMode());
//                        incidentDTO.setToDistance(currentData.getDstToTurnOut());
//                        incidentDTO.setLocoDir(currentData.getLocoDir());
//                        incidentDTO.setStationDao(currentData.getStnCode());
//                        incidentDTO.setLoco(currentData.getLocoID());
//                        incidentDTO.setMA(currentData.getMa());
//
//                        if(currentData.getMa()==null || currentData.getMa().isEmpty()){
//                            LocoMovementData movementData=getMA(currentData.getTime(),dataList,currentData.getBrakeMode());
//                            if(movementData!=null){
//                                if(movementData.getDstToTurnOut()==null || movementData.getDstToTurnOut().equalsIgnoreCase("0")){
//                                    continue;
//                                }
//                                incidentDTO.setStation(movementData.getStnCode().getCode());
//                                incidentDTO.setTime(movementData.getTime().toString());
//                                incidentDTO.setAbsLocation(movementData.getAbsLocation());
//                                incidentDTO.setLocoSpeed(movementData.getLocoSpeed());
//                                incidentDTO.setToSpeed(movementData.getToSpeed());
//                                incidentDTO.setBrakeMode(movementData.getBrakeMode());
//                                incidentDTO.setLocoMode(movementData.getLocoMode());
//                                incidentDTO.setToDistance(movementData.getDstToTurnOut());
//                                incidentDTO.setLocoDir(movementData.getLocoDir());
//                                incidentDTO.setStationDao(movementData.getStnCode());
//                                incidentDTO.setLoco(movementData.getLocoID());
//                                incidentDTO.setMA(movementData.getMa());
//                            }
//                        }
//
//                        EvntMsg evtmsg=getBreakModeFaultMessage(incidentDTO);
//                        incidentDTO.setBriefDescription(evtmsg.getIssueMsg());
//                        incidentDTO.setIssueCategory(evtmsg.getIssueCategory());
//                        incidentDTO.setIssue(evtmsg.getIssueName());
//                        modeChangeDescriptions.add(incidentDTO);
//                    }
//                }
//                previousMode = currentData.getBrakeMode();
//            }

        }catch (Exception e){

        }
        return modeChangeDescriptions;
    }

    public  List<IncidentDTO> findEBBreakApplied(List<LocoMovementData> dataList) {
        List<IncidentDTO> modeChangeDescriptions = new ArrayList<>();
        if (dataList == null || dataList.isEmpty()) {
            return modeChangeDescriptions;
        }
        try {

            dataList.sort(Comparator.comparing(LocoMovementData::getTime));
            List<LocoMovementData> allEBBreaks = getAllEBBreaks(dataList);
            if (allEBBreaks == null || allEBBreaks.isEmpty()) {
                return modeChangeDescriptions;
            }
            allEBBreaks.sort(Comparator.comparing(LocoMovementData::getTime));

            for (LocoMovementData currentData : allEBBreaks) {

                IncidentDTO incidentDTO = new IncidentDTO();
                incidentDTO.setLocoNo(currentData.getLocoID().getLocoNo());
                Date date = Date.from(currentData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                incidentDTO.setDate(date);
                incidentDTO.setStation(currentData.getStnCode().getCode());
                incidentDTO.setTime(currentData.getTime().toString());
                incidentDTO.setAbsLocation(currentData.getAbsLocation());
                incidentDTO.setLocoSpeed(currentData.getLocoSpeed());
                incidentDTO.setToSpeed(currentData.getToSpeed());
                incidentDTO.setBrakeMode(currentData.getBrakeMode());
                incidentDTO.setLocoMode(currentData.getLocoMode());
                incidentDTO.setToDistance(currentData.getDstToTurnOut());
                incidentDTO.setLocoDir(currentData.getLocoDir());
                incidentDTO.setStationDao(currentData.getStnCode());
                incidentDTO.setLoco(currentData.getLocoID());
                incidentDTO.setMA(currentData.getMa());

                EvntMsg evtmsg=getBreakModeFaultMessage(incidentDTO);
                incidentDTO.setBriefDescription(evtmsg.getIssueMsg());
                incidentDTO.setIssueCategory(evtmsg.getIssueCategory());
                incidentDTO.setIssue(evtmsg.getIssueName());
                modeChangeDescriptions.add(incidentDTO);

            }
        }catch (Exception e){

        }
        return modeChangeDescriptions;
    }

    public  List<IncidentDTO> findSpareBreakApplied(List<LocoMovementData> dataList) {
        List<IncidentDTO> modeChangeDescriptions = new ArrayList<>();
        if (dataList == null || dataList.isEmpty()) {
            return modeChangeDescriptions;
        }
        try {

            dataList.sort(Comparator.comparing(LocoMovementData::getTime));
            List<LocoMovementData> allSpareBreaks = getAllSpareBreaks(dataList);
            if (allSpareBreaks == null || allSpareBreaks.isEmpty()) {
                return modeChangeDescriptions;
            }
            allSpareBreaks.sort(Comparator.comparing(LocoMovementData::getTime));

            for (LocoMovementData currentData : allSpareBreaks) {

                IncidentDTO incidentDTO = new IncidentDTO();
                incidentDTO.setLocoNo(currentData.getLocoID().getLocoNo());
                Date date = Date.from(currentData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                incidentDTO.setDate(date);
                incidentDTO.setStation(currentData.getStnCode().getCode());
                incidentDTO.setTime(currentData.getTime().toString());
                incidentDTO.setAbsLocation(currentData.getAbsLocation());
                incidentDTO.setLocoSpeed(currentData.getLocoSpeed());
                incidentDTO.setToSpeed(currentData.getToSpeed());
                incidentDTO.setBrakeMode(currentData.getBrakeMode());
                incidentDTO.setLocoMode(currentData.getLocoMode());
                incidentDTO.setToDistance(currentData.getDstToTurnOut());
                incidentDTO.setLocoDir(currentData.getLocoDir());
                incidentDTO.setStationDao(currentData.getStnCode());
                incidentDTO.setLoco(currentData.getLocoID());
                incidentDTO.setMA(currentData.getMa());

                EvntMsg evtmsg=getBreakModeFaultMessage(incidentDTO);
                incidentDTO.setBriefDescription(evtmsg.getIssueMsg());
                incidentDTO.setIssueCategory(evtmsg.getIssueCategory());
                incidentDTO.setIssue(evtmsg.getIssueName());
                modeChangeDescriptions.add(incidentDTO);

            }
        }catch (Exception e){

        }
        return modeChangeDescriptions;
    }



    public LocoMovementData getMA(LocalTime time, List<LocoMovementData> records,String breakMode) {
        try {

            if (records == null || records.isEmpty()) {
                return null;
            }
            LocoMovementData nextRecord = getNextMA(time, records,breakMode);
            if (nextRecord != null) {
                return nextRecord;
            }
//            LocoMovementData previousRecord = getPreviousMA(time, records);
//             if (previousRecord != null) {
//                return previousRecord;
//            }

            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public List<LocoMovementData> getAllFSBBreaks(List<LocoMovementData> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if records are null or empty
        }

        try {
            // Use a Set to track unique LocalTime values
            Set<LocalTime> processedTimes = new HashSet<>();

            return records.stream()
                    .filter(record ->
                            record.getMa() != null &&
                                    !record.getMa().isEmpty() &&
                                    record.getDstToTurnOut() != null &&
                                    !record.getDstToTurnOut().isEmpty() &&
//                                    !record.getDstToTurnOut().equalsIgnoreCase("0") &&
                                    record.getBrakeMode() != null &&
                                    record.getBrakeMode().equalsIgnoreCase("FSB") &&
                                    processedTimes.add(record.getTime()))
                    .sorted(Comparator.comparing(LocoMovementData::getTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<LocoMovementData> getAllEBBreaks(List<LocoMovementData> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            // Use a Set to track unique LocalTime values
            Set<LocalTime> processedTimes = new HashSet<>();

            return records.stream()
                    .filter(record ->
                            record.getMa() != null &&
                                    !record.getMa().isEmpty() &&
                                    record.getLocoSpeed() != null &&
                                    !record.getLocoSpeed().isEmpty() &&
                                    record.getBrakeMode() != null &&
                                    record.getBrakeMode().equalsIgnoreCase("EB") &&
                                    processedTimes.add(record.getTime()))
                    .sorted(Comparator.comparing(LocoMovementData::getTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<LocoMovementData> getAllSpareBreaks(List<LocoMovementData> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList(); // Return an empty list if records are null or empty
        }

        try {
            // Use a Set to track unique LocalTime values
            Set<LocalTime> processedTimes = new HashSet<>();

            return records.stream()
                    .filter(record ->
                                   record.getBrakeMode() != null &&
                                    record.getBrakeMode().equalsIgnoreCase("Spare") &&
                                    processedTimes.add(record.getTime()))
                    .sorted(Comparator.comparing(LocoMovementData::getTime))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }



    public LocoMovementData getNextMA(LocalTime time, List<LocoMovementData> records, String breakMode) {
        if (records == null || records.isEmpty()) {
            return null; // Return null if records are null or empty
        }

        try {
            LocalTime newTime = time.plusMinutes(1);

            // Filter the records to find the one that satisfies the condition
            return records.stream()
                    .filter(record ->
                            record.getTime().isAfter(newTime) &&
                                    record.getMa() != null &&
                                    !record.getMa().isEmpty() &&
                                    breakMode != null &&
                                    breakMode.equalsIgnoreCase(record.getBrakeMode()))
                    .min(Comparator.comparing(LocoMovementData::getTime)) // Get the earliest time that satisfies the condition
                    .orElse(null);

        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
            return null; // Return null in case of any exception
        }
    }



    public  EvntMsg getBreakModeFaultMessage(IncidentDTO incidentDTO) {
        if (incidentDTO == null || incidentDTO.getBrakeMode() == null) {
            return null;
        }

        String brakeMode = incidentDTO.getBrakeMode();
        EvntMsg evntMsg;
        switch (brakeMode) {
            case "FSB":
                evntMsg= locoFaultMessage.getFSBBreakFaultMsg(incidentDTO);
                return evntMsg;
            case "EB":
                evntMsg= locoFaultMessage.getEBFaultMsg(incidentDTO);
                return evntMsg;
            case "Spare":
                evntMsg=new EvntMsg();
                evntMsg.setIssueMsg("Spare");
                return evntMsg;
            default:
                return null;
        }
    }

   public boolean is_NOS_OS_NSB_Break(String currentMode) {
        return "NOS".equalsIgnoreCase(currentMode) ||
                "OS".equalsIgnoreCase(currentMode) ||
                "NSB".equalsIgnoreCase(currentMode);
    }

    // Utility method to format total time into hours, minutes, and seconds
    private String formatDuration(long totalMinutes) {
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        long seconds = (totalMinutes % 1) * 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}