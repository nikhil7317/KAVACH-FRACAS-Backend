package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.DTO.RepeatedRFIDReportDTO;
import com.railbit.tcasanalysis.repository.KavachAlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepeatedRFIDReportService {

    private static final int MAX_PAGE_SIZE = 200;

    private final KavachAlertRepository kavachAlertRepository;

    @Transactional(readOnly = true)
    public Page<RepeatedRFIDReportDTO> getRepeatedRFIDReport(
            Integer locoId,
            Integer lastRfidTag,
            Date fromDate,
            Date toDate,
            int page,
            int size
    ) {
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        page = Math.max(page, 0);

        if (toDate != null) {
            toDate = endOfDay(toDate);
        }

        long totalRecords = kavachAlertRepository.countRepeatedRFIDRows(locoId, lastRfidTag, fromDate, toDate);

        if (totalRecords == 0) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        }

        List<Object[]> rawRows = kavachAlertRepository.findRepeatedRFIDRows(locoId, lastRfidTag, fromDate, toDate);

        List<RepeatedRFIDReportDTO> allDtos = new ArrayList<>(rawRows.size());
        for (Object[] row : rawRows) {
            allDtos.add(mapRow(row));
        }

        int fromIdx = page * size;
        int toIdx   = Math.min(fromIdx + size, allDtos.size());

        if (fromIdx >= allDtos.size()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), totalRecords);
        }

        List<RepeatedRFIDReportDTO> pageContent = new ArrayList<>(allDtos.subList(fromIdx, toIdx));
        return new PageImpl<>(pageContent, PageRequest.of(page, size), totalRecords);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private RepeatedRFIDReportDTO mapRow(Object[] row) {
        return RepeatedRFIDReportDTO.builder()
                .locoId(toInteger(row[0]))
                .lastRfidTag(toInteger(row[1]))
                .eventTime(toDate(row[2]))
                .groupCount(toLong(row[3]))
                .build();
    }

    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Integer i) return i;
        if (o instanceof Number  n) return n.intValue();
        return Integer.parseInt(o.toString());
    }

    private Long toLong(Object o) {
        if (o == null) return null;
        if (o instanceof Long   l) return l;
        if (o instanceof Number n) return n.longValue();
        return Long.parseLong(o.toString());
    }

    private Date toDate(Object o) {
        if (o == null) return null;
        if (o instanceof Date d) return d;
        return new Date(((java.sql.Timestamp) o).getTime());
    }

    private Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.HOUR_OF_DAY) == 0
                && cal.get(Calendar.MINUTE) == 0
                && cal.get(Calendar.SECOND) == 0) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE,      59);
            cal.set(Calendar.SECOND,      59);
            cal.set(Calendar.MILLISECOND, 999);
        }
        return cal.getTime();
    }
}