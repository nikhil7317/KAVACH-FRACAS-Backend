package com.railbit.tcasanalysis.service.serviceImpl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.railbit.tcasanalysis.entity.LocoMovementData;
import com.railbit.tcasanalysis.entity.Station;
import com.railbit.tcasanalysis.entity.nmspackets.FaultCode;
import com.railbit.tcasanalysis.DTO.FaultCodeCountDTO;
import com.railbit.tcasanalysis.entity.nmspackets.FaultPacket;
import com.railbit.tcasanalysis.DTO.FaultPacketDTO;
import com.railbit.tcasanalysis.repository.FaultCodeRepo;
import com.railbit.tcasanalysis.repository.FaultPacketRepo;
import com.railbit.tcasanalysis.repository.StationRepo;
import com.railbit.tcasanalysis.service.FaultPacketService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
@AllArgsConstructor

public class FaultPacketServiceImpl implements FaultPacketService {

    @Autowired
    EntityManager entityManager;

    @Autowired
    StationRepo stationRepo;

    @Autowired
    FaultCodeRepo faultCodeRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private final  FaultPacketRepo faultPacketRepo;

    public Map<String, Object> getAllFaultPacketData(Integer page, Integer intRecord, LocalDateTime fromDate, LocalDateTime toDate) {

        Pageable pageable = PageRequest.of(page, intRecord);

        // Step 1: Get the CriteriaBuilder and CriteriaQuery
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<FaultPacketDTO> cq = cb.createQuery(FaultPacketDTO.class);

        // Step 2: Define the root of the query (the main entity)
        Root<FaultPacket> lm = cq.from(FaultPacket.class);

        // Step 3: Join with related entities (stnCode and locoID)
        Join<FaultPacket, Station> stnCodeJoin = lm.join("tcasSubsysId", JoinType.LEFT);
        Join<FaultPacket, FaultCode> faultCodeJoin = lm.join("faultCode", JoinType.LEFT);
        //Join<FaultPacket, Firm> firmJoin = FaultCode.join("firm", JoinType.LEFT);

        // Step 4: Define the selection of the specific fields and map them to LocoMovementDTO
        cq.select(cb.construct(
                FaultPacketDTO.class,
                lm.get("id"),
                lm.get("date"),
                lm.get("time"),
                stnCodeJoin.get("name"),  // Join on Station to get its name
                faultCodeJoin.get("code"), // Join on FaultCode to get faultCode
                faultCodeJoin.get("faultName"), // Join on FaultCode to get faultCode
                faultCodeJoin.get("firm").get("name"),     // Join on FaultCode to get the firm name
                lm.get("crc"),
                lm.get("hexValue"),
                lm.get("msgLength"),
                lm.get("msgType"),
                lm.get("startFrame"),
                lm.get("tcasSubsysType"),
                lm.get("totalFaultCode")
        ));
        Predicate filterPredicate = cb.conjunction();

//        if (locoid != null){
//            Predicate locoIDPredicate = cb.equal(stnCodeJoin.get("id"), locoid); // Assuming locoNo is the field to filter
//            filterPredicate = cb.and(filterPredicate, locoIDPredicate);
//        }

        if (fromDate != null && toDate != null){

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
        Root<FaultPacket> lmForCount = countQuery.from(FaultPacket.class);

        // Apply count operation
        countQuery.select(cb.count(lmForCount));

        // Apply the same filter predicate to the count query
        countQuery.where(filterPredicate);

        List<FaultPacketDTO> totalRecordsDTO =  entityManager.createQuery(cq).getResultList();

        Long totalRecords = (long) totalRecordsDTO.size();
        // Execute the query and get the total record count


        //    Long totalRecords = 100L;
        // Step 6: Apply pagination if Pageable is provided
        if (pageable != null) {
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            cq.select(cb.construct(
                    FaultPacketDTO.class,
                    lm.get("id"),
                    lm.get("date"),
                    lm.get("time"),
                    stnCodeJoin.get("name"),  // Join on Station to get its name
                    faultCodeJoin.get("code"), // Join on FaultCode to get faultCode
                    faultCodeJoin.get("faultName"), // Join on FaultCode to get faultCode
                    faultCodeJoin.get("firm").get("name"),     // Join on FaultCode to get the firm name
                    lm.get("crc"),
                    lm.get("hexValue"),
                    lm.get("msgLength"),
                    lm.get("msgType"),
                    lm.get("startFrame"),
                    lm.get("tcasSubsysType"),
                    lm.get("totalFaultCode")
            ));

            // Create descending order predicate for id
            Order order = cb.desc(lm.get("id"));

            // Apply the order to the query
            cq.orderBy(order);

            // Apply pagination
            TypedQuery<FaultPacketDTO> query = entityManager.createQuery(cq);
            query.setFirstResult(pageNumber * pageSize); // Set offset
            query.setMaxResults(pageSize); // Set limit

            // Execute the query and get paginated results
            List<FaultPacketDTO> resultList = query.getResultList();

            // Return both paginated results and total record count in a map
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("data", resultList);
            resultMap.put("totalRecords", totalRecords);

            return resultMap;
        }

        // Step 7: Execute the query without pagination if no Pageable is provided
        TypedQuery<FaultPacketDTO> query = entityManager.createQuery(cq);
        List<FaultPacketDTO> resultList = query.getResultList();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultList);
        resultMap.put("totalRecords", totalRecords);

        return resultMap;
        //   return locoDataRepository.findLocoMovement(pageable);
    }

    @Override
    public FaultPacket getFaultPacketById(Long id) {
        Optional<FaultPacket> data=faultPacketRepo.findById(id);
        if(data.isEmpty())
            throw new NoSuchElementException("FaultPacket not found");
        return data.get();
    }

    public String postFaultPacket(FaultPacketDTO faultPacket) {

        FaultPacket newFaultPacket = objectMapper.convertValue(faultPacket, com.railbit.tcasanalysis.entity.nmspackets.FaultPacket.class);

        Optional<FaultPacket> optionalFaultPacket = faultPacketRepo.findByHexValue(faultPacket.getHex_value());
        if (optionalFaultPacket.isPresent()) {
            return "Packet Already Available";
        } else {
            //faultPacketRepo.save(faultPacket);

            newFaultPacket.setDate(
                    Optional.ofNullable(faultPacket.getDate())
                            .map(date -> {
                                try { return date; }
                                catch (DateTimeParseException e) { return LocalDate.of(1970, 1, 1); }
                            })
                            .orElse(LocalDate.of(1970, 1, 1))
            );

            newFaultPacket.setTime(
                    Optional.ofNullable(faultPacket.getTime())
                            .map(time -> {
                                try { return time; }
                                catch (DateTimeParseException e) { return LocalTime.of(0, 0); }
                            })
                            .orElse(LocalTime.of(0, 0))
            );

            newFaultPacket.setCrc(faultPacket.getCrc());
            newFaultPacket.setFaultCode(faultCodeRepo.findByCode(faultPacket.getFault_code()));
            newFaultPacket.setHexValue(faultPacket.getHex_value());
            newFaultPacket.setMsgLength(faultPacket.getMsg_length());
            newFaultPacket.setMsgType(faultPacket.getMsg_type());
            newFaultPacket.setStartFrame(faultPacket.getStart_frame());
            newFaultPacket.setTcasSubsysId(stationRepo.findByTcassubsysid(faultPacket.getTcas_subsys_id()));
            newFaultPacket.setTcasSubsysType(faultPacket.getTcas_subsys_type());
            newFaultPacket.setTotalFaultCode(faultPacket.getTotal_fault_code());

            faultPacketRepo.save(newFaultPacket);

            return "Added Successfully";
        }

    }

    @Override
    public void updateFaultPacket(FaultPacket faultPacket) {
        faultPacketRepo.save(faultPacket);
    }

    @Override
    public void deleteFaultPacketById(Long id) {
        faultPacketRepo.deleteById(id);
    }

    @Override
    public int importByExcelSheet(MultipartFile excelSheet) throws Exception {
        return 0;
    }

    @Override
    public List<FaultCodeCountDTO> getFaultCodeCountsByTime() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusDays(1);
        String formattedThirtyMinutesAgo = thirtyMinutesAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Object[]> faultCount = faultPacketRepo.findFaultCodeCountsBytime(formattedThirtyMinutesAgo,pageable);
        return parseFaultCount(faultCount);
    }

    private List<FaultCodeCountDTO> parseFaultCount(List<Object[]> faultCount) {
        List<FaultCodeCountDTO> result = new ArrayList<>();
        for (Object[] row : faultCount) {

            int code = (int) row[0];  // fault code
            String msg = (String) row[1];   // fault name
            int count = ((Number) row[2]).intValue(); // count

            FaultCodeCountDTO dto = new FaultCodeCountDTO(code, msg, count);
            result.add(dto);
        }
        return result;
    }
}
