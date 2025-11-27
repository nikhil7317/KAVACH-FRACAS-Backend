package com.railbit.tcasanalysis.service;

import com.railbit.tcasanalysis.entity.MaintenanceReport;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MaintenanceReportSpecification {

    public static Specification<MaintenanceReport> hasDivId(Integer divId) {
        return (root, query, criteriaBuilder) -> (divId == null || divId == 0) ? null : criteriaBuilder.equal(root.get("divId"), divId);
    }

    public static Specification<MaintenanceReport> hasStnId(Integer stnId) {
        return (root, query, criteriaBuilder) -> stnId == null ? null : criteriaBuilder.equal(root.get("stnId").get("id"), stnId);
    }

    public static Specification<MaintenanceReport> hasLocoId(Integer locoId) {
        return (root, query, criteriaBuilder) -> locoId == null ? null : criteriaBuilder.equal(root.get("locoId").get("id"), locoId);
    }

    public static Specification<MaintenanceReport> hasDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }
}