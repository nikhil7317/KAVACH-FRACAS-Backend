package com.railbit.tcasanalysis.repository.dslrepos;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.railbit.tcasanalysis.entity.QDivision;
import com.railbit.tcasanalysis.entity.QStation;
import com.railbit.tcasanalysis.entity.QZone;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.NMSStationStatus;
import com.railbit.tcasanalysis.entity.nmspackets.stationarypackets.QNMSStationStatus;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NMSStationStatusRepository {

    private final JPAQueryFactory queryFactory;

    @Autowired
    public NMSStationStatusRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    public List<NMSStationStatus> getFilteredStationStatuses(Integer zoneId, Integer divisionId, String status, String searchQuery) {
        QNMSStationStatus nms = QNMSStationStatus.nMSStationStatus;
        QStation station = QStation.station;
        QDivision division = QDivision.division;
        QZone zone = QZone.zone;

        BooleanBuilder predicate = new BooleanBuilder();

        if (zoneId != null && zoneId > 0) {
            predicate.and(division.zone.id.eq(zoneId));
        }
        if (divisionId != null && divisionId > 0) {
            predicate.and(nms.division.id.eq(divisionId));
        }
        if (status != null && !status.isEmpty()) {
            predicate.and(nms.status.eq(status));
        }
        predicate.and(nms.station.nmsVersion.eq("3.2"));
        if (searchQuery != null && !searchQuery.isEmpty()) {
            predicate.and(
                    station.name.containsIgnoreCase(searchQuery)
                            .or(station.name.containsIgnoreCase(searchQuery))
                            .or(station.code.containsIgnoreCase(searchQuery))
                            .or(nms.srcIp.containsIgnoreCase(searchQuery))
                            .or(nms.srcPort.containsIgnoreCase(searchQuery))
                            .or(nms.stnCode.containsIgnoreCase(searchQuery))
            );
        }

        return queryFactory
                .selectFrom(nms)
                .innerJoin(nms.station, station)
                .innerJoin(nms.division, division)
                .innerJoin(division.zone, zone)
                .where(predicate)
                .fetch();
    }
}
