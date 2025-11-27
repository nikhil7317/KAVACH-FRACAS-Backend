package com.railbit.tcasanalysis.service.serviceImpl;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.railbit.tcasanalysis.DTO.MonthWiseData;
import com.railbit.tcasanalysis.entity.analysis.BarGraphDataSet;
import com.railbit.tcasanalysis.entity.analysis.oemwiseanalysis.*;
import com.railbit.tcasanalysis.entity.*;
import com.railbit.tcasanalysis.entity.analysis.PieChartData;
import com.railbit.tcasanalysis.entity.analysis.SingleIncidentAnalysisChartData;
import com.railbit.tcasanalysis.entity.analysis.StringAndCount;
import com.railbit.tcasanalysis.entity.analysis.oembargraph.BarGroup;
import com.railbit.tcasanalysis.entity.analysis.oembargraph.BarGroupListContainer;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.FirmMonthStatusCount;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.MonthlyGraphData;
import com.railbit.tcasanalysis.entity.analysis.yearlygraph.YearlyGraphData;
import com.railbit.tcasanalysis.repository.*;
import com.railbit.tcasanalysis.service.FirmService;
import com.railbit.tcasanalysis.service.GraphsAndChartsService;
import com.railbit.tcasanalysis.service.IssueCategoryService;
import com.railbit.tcasanalysis.service.PossibleIssueService;
import com.railbit.tcasanalysis.util.HelpingHand;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GraphsAndChartsServiceImpl implements GraphsAndChartsService {
    private static final Logger log = LoggerFactory.getLogger(GraphsAndChartsServiceImpl.class);
    private final TcasBreakingInspectionRepo tcasBreakingInspectionRepo;
    private final IncidentReportsRepo incidentReportsRepo;
    private final OemReportsRepo oemReportsRepo;
    private final IssueCategoryService issueCategoryService;
    private final PossibleIssueService possibleIssueService;
    private final UserRepo userRepo;
    private final FirmService firmService;
    private final IncidentTicketRepo incidentTicketRepo;

    @Autowired
    EntityManager entityManager;

    public List<Object[]> getLast12MonthsAndYearsFromTripDate() {
        String query = "SELECT DISTINCT YEAR(t.tripDate) AS year, MONTH(t.tripDate) AS month " +
                "FROM tcasbreakinginspection t " +
                "WHERE t.tripDate >= :startDate " +
                "ORDER BY year ASC, month ASC";

        // Calculate the start date for the last 12 months
        java.time.LocalDate now = java.time.LocalDate.now();
        java.time.LocalDate startDate = now.minusMonths(12);

        // Execute the query
        List<Object[]> results = entityManager.createQuery(query)
                .setParameter("startDate", startDate) // Pass LocalDate directly
                .getResultList();

        // Prepare the HashMap
//        Map<Integer, Integer> monthYearMap = new HashMap<>();
//        for (Object[] result : results) {
//            Integer year = (Integer) result[0];
//            Integer month = (Integer) result[1];
//            // Print month and year
//            System.out.println("Month: " + month + ", Year: " + year);
//            monthYearMap.put(month, year);
//        }
//
//        log.info("Hashmap {}",monthYearMap);
        return results;
    }

    @Transactional
    @Override
    public List<MonthWiseData> getIssueWiseYearlyGraphData(Long zoneId, Long divisionId) {

        List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();

        List<MonthWiseData> yearlyGraphDataList = new ArrayList<>();

        try {

            List<Object[]> last12Months = getLast12MonthsAndYearsFromTripDate();

            // First Graph Data for Over All Below
//
//            YearlyGraphData yearlyGraphData = new YearlyGraphData();
//            yearlyGraphData.setTitle("Monthly Incidents");
//            List<MonthlyGraphData> monthlyGraphDataList = new ArrayList<>();

            for (Object[] result : last12Months) {

                Integer y = (Integer) result[0];
                Integer m = (Integer) result[1];


                MonthWiseData monthWiseData = new MonthWiseData();
                monthWiseData.setMonth(Month.of(m).name());
                monthWiseData.setBarGraphDataSetList(new ArrayList<>()); // Initialize the list

                //"Total Counts" Removed for temporary
//                stringAndCountList.add(stringAndCountTotal);

                for (IssueCategory issueCategory : issueCategoryList) {
                    int issueCategoryWiseCount = 0;

                    if (issueCategory.getName().equalsIgnoreCase("No Issue")
                            || issueCategory.getName().equalsIgnoreCase("Trial")) {
                        continue;
                    }

                    List<Object[]> countResult = tcasBreakingInspectionRepo.findAllInspectionsCountByMonthYearIssueCategoryIdAndFilters(m, y, issueCategory.getId(),zoneId,divisionId);

                    for (Object[] output : countResult) {
                        issueCategoryWiseCount = Math.toIntExact((Long) output[1]);
                    }

                    StringAndCount stringAndCountIssueCategoryWise = new StringAndCount();
                    stringAndCountIssueCategoryWise.setName(issueCategory.getName());
                    stringAndCountIssueCategoryWise.setColorCode(HelpingHand.getRandomColor());
                    stringAndCountIssueCategoryWise.setCount(Math.toIntExact(issueCategoryWiseCount));

                    monthWiseData.getBarGraphDataSetList().add(
                            new BarGraphDataSet(issueCategory.getName(), issueCategoryWiseCount,String.valueOf(issueCategoryWiseCount), "#ff6384")
                    );
                }

                yearlyGraphDataList.add(monthWiseData);
            }


        } catch (Exception e) {
            log.error("Exception : {}", e.toString());
            throw new RuntimeException(e);
        }

        return yearlyGraphDataList;
    }

    @Transactional
    @Override
    public List<SingleIncidentAnalysisChartData> getCountsByFirmAndDate(LocalDateTime fromDate, LocalDateTime toDate, int firmId) {

        List<SingleIncidentAnalysisChartData> singleIncidentAnalysisChartDataList = new ArrayList<>();
        try {
            // Overall OEM wise Summary Added Below
            {
                SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                List<PieChartData> chartDataList = new ArrayList<>();
                int totalCount = 0;
                {
                    // adding Issue Category wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> issueCategoryObjAndCountList = new ArrayList<>();
                    List<Object[]> counts = oemReportsRepo.findInspectionCountsByIssueCategoryAndDate(firmId, fromDate, toDate);

                    for (Object[] result : counts) {
                        IssueCategory issueCategory = (IssueCategory) result[0];
                        Long count = (Long) result[1];
                        totalCount += count;
                        StringAndCount issueCategoryObjAndCount = new StringAndCount(issueCategory.getName(), Math.toIntExact(count), "");
                        if (issueCategory.getName().equalsIgnoreCase("Mode Change")) {
                            issueCategoryObjAndCount.setColorCode("#f0ad4e");
                        } else if (issueCategory.getName().equalsIgnoreCase("Undesirable Braking")) {
                            issueCategoryObjAndCount.setColorCode("#E91E63");
                        } else {
                            issueCategoryObjAndCount.setColorCode(HelpingHand.getRandomColor());
                        }
                        issueCategoryObjAndCountList.add(issueCategoryObjAndCount);
                    }
                    pieChartData.setName("IssueCategory");
                    pieChartData.setChartDataList(issueCategoryObjAndCountList);
                    chartDataList.add(pieChartData);

                    singleIncidentAnalysisChartData.setTotal(totalCount);
                    // adding Issue Category wise data below
                }

                {
                    // adding Possible Issue wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = oemReportsRepo.findInspectionCountsByPossibleIssueAndDate(firmId, fromDate, toDate);
                    int totalPossibleIssueWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleIssue possibleIssue = (PossibleIssue) result[0];
                        Long count = (Long) result[1];
                        totalPossibleIssueWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleIssue.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleIssueWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
//                        stringAndCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("PossibleIssue");
                    pieChartData.setChartDataList(stringAndCountList);
                    chartDataList.add(pieChartData);
                    // adding Possible Issue wise data above
                }
                {
                    // adding Possible Root Cause wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = oemReportsRepo.findInspectionCountsByPossibleRootCauseAndDate(firmId, fromDate, toDate);
                    int totalPossibleRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleRootCause possibleRootCause = (PossibleRootCause) result[0];
                        Long count = (Long) result[1];
                        totalPossibleRootCauseWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleRootCause.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleRootCauseWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleRootCauseWiseCount), HelpingHand.getRandomColor());
//                        stringAndCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("PossibleRootCause");
                    pieChartData.setChartDataList(stringAndCountList);
                    chartDataList.add(pieChartData);
                    // adding Possible Root Cause wise data above
                }

                singleIncidentAnalysisChartData.setChartTitle("Overall");
                singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

                // Overall OEM wise Summary Added Above
            }

            {

                // Issue Category Wise Summary Added Below

                List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();

                for (IssueCategory issueCategory : issueCategoryList) {

                    if (issueCategory.getName().equalsIgnoreCase("Desirable Braking")
                            || issueCategory.getName().equalsIgnoreCase("No Issue")
                            || issueCategory.getName().equalsIgnoreCase("Trial")) {
                        continue;
                    }

                    int issueCategoryId = issueCategory.getId();
                    // Getting Total Counts according to Issue Category
                    int totalCount = 0;
                    List<Object[]> countResult = oemReportsRepo.findAllInspectionsCountByMonthYearAndIssueCategoryIdAndFirmId(fromDate, toDate, issueCategoryId, firmId);

                    for (Object[] output : countResult) {
                        Long count = (Long) output[1]; // Assuming count is of type Long
                        totalCount = Math.toIntExact(count);
                    }

                    SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                    List<PieChartData> chartDataList = new ArrayList<>();
                    singleIncidentAnalysisChartData.setChartTitle(issueCategory.getName());

                    {
                        // Adding issue wise chart data below
                        List<Object[]> counts = oemReportsRepo.findInspectionCountsByPossibleIssueAndIssueCategoryAndDate(issueCategoryId, firmId, fromDate, toDate);
                        PieChartData pieChartData = new PieChartData();
                        List<StringAndCount> issueCatWiseDataCountList = new ArrayList<>();
                        int totalPossibleIssueWiseCount = 0;
                        for (Object[] result : counts) {
                            PossibleIssue possibleIssue = (PossibleIssue) result[0];
                            Long inspectionCount = (Long) result[1];
                            totalPossibleIssueWiseCount += inspectionCount;
                            StringAndCount stringAndCount = new StringAndCount(possibleIssue.getName(), Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());
                            issueCatWiseDataCountList.add(stringAndCount);
                        }
//                        if (totalCount - totalPossibleIssueWiseCount > 0) {
//                            StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
//                            issueCatWiseDataCountList.add(stringAndCount);
//                        }
                        pieChartData.setName("Possible Issue");
                        pieChartData.setChartDataList(issueCatWiseDataCountList);
                        chartDataList.add(pieChartData);
                        // Adding issue wise chart data above
                    }

                    {
                        // Adding Root Cause Wise chart data below
                        List<Object[]> counts = oemReportsRepo.findInspectionCountsByPossibleRootCauseAndIssueCategoryAndDate(issueCategoryId, firmId, fromDate, toDate);
                        PieChartData pieChartData = new PieChartData();
                        List<StringAndCount> rootCauseDataCountList = new ArrayList<>();
                        int totalRootCauseWiseCount = 0;
                        for (Object[] result : counts) {
                            PossibleRootCause possibleRootCause = (PossibleRootCause) result[0];
                            Long inspectionCount = (Long) result[1];
                            totalRootCauseWiseCount += inspectionCount;
                            StringAndCount stringAndCount = new StringAndCount(possibleRootCause.getName(), Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());

                            rootCauseDataCountList.add(stringAndCount);
                        }
//                        if (totalCount - totalRootCauseWiseCount > 0) {
//                            StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalRootCauseWiseCount), HelpingHand.getRandomColor());
//                            rootCauseDataCountList.add(stringAndCount);
//                        }
                        pieChartData.setName("Possible Root Cause");
                        pieChartData.setChartDataList(rootCauseDataCountList);
                        chartDataList.add(pieChartData);
                        // Adding Root Cause Wise chart data above
                    }

                    // Adding all Data
                    singleIncidentAnalysisChartData.setTotal(totalCount);
                    singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                    singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

                }

                // Issue Category Wise Summary Added Above
            }

        } catch (Exception e) {
            log.error("Exception : ", e);
        }

        return singleIncidentAnalysisChartDataList;
    }

    @Override
    public List<BarGroupListContainer> getFirmWiseBarGraphData(LocalDateTime fromDate, LocalDateTime toDate) {

        List<Firm> firmList = firmService.getAllFirm();

        List<BarGroupListContainer> barGroupListContainerList = new ArrayList<>();

        List<String> statusList = new ArrayList<>();
        statusList.add("Open");
        statusList.add("Close");

        List<String> conditionsList = new ArrayList<>();
        statusList.add("less than 2 days");
        statusList.add("less than 5 Days");
        statusList.add("more than 5 Days");
        statusList.add("more than 10 Days");
        statusList.add("more than 30 Days");

        {
            // For open inspections
            String status = "Ongoing Issues";
            BarGroupListContainer barGroupListContainer = new BarGroupListContainer();
            barGroupListContainer.setTitle(status);

            List<BarGroup> barGroupList = new ArrayList<>();

            {
                //Firm wise iteration

                for (Firm firm : firmList) {

                    BarGroup barGroup = new BarGroup();
                    barGroup.setTitle(firm.getName());
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    {
                        //More than 2 days
                        int count = (int) oemReportsRepo.countOpenInspectionsOlderThan2Days(firm.getId(), fromDate, toDate);
                        String title = "More than 2 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    {
                        //More than 5 days
                        int count = (int) oemReportsRepo.countOpenInspectionsOlderThan5Days(firm.getId(), fromDate, toDate);
                        String title = "More than 5 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }
                    {
                        //More than 10 days
                        int count = (int) oemReportsRepo.countOpenInspectionsOlderThan10Days(firm.getId(), fromDate, toDate);
                        String title = "More than 10 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }
                    {
                        //More than 20 days
                        int count = (int) oemReportsRepo.countOpenInspectionsOlderThan20Days(firm.getId(), fromDate, toDate);
                        String title = "More than 20 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    {
                        //More than 30 days
                        int count = (int) oemReportsRepo.countOpenInspectionsOlderThan30Days(firm.getId(), fromDate, toDate);
                        String title = "More than 30 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    barGroup.setBarDataList(stringAndCountList);
                    barGroupList.add(barGroup);
                }


            }

            barGroupListContainer.setBarGroupList(barGroupList);

            barGroupListContainerList.add(barGroupListContainer);
        }

        {
            // For closed inspections
            String status = "Closed Issues";
            BarGroupListContainer barGroupListContainer = new BarGroupListContainer();
            barGroupListContainer.setTitle(status);

            List<BarGroup> barGroupList = new ArrayList<>();

            {
                //Firm wise iteration

                for (Firm firm : firmList) {

                    BarGroup barGroup = new BarGroup();
                    barGroup.setTitle(firm.getName());
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    {
                        //for less than 2 days
                        int count = (int) oemReportsRepo.countByFirmWiseClosedInspectionWithinThreeDays(firm.getId(), fromDate, toDate);
                        String title = "Less than 2 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    {
                        //Within 2 to 5 days
                        int count = (int) oemReportsRepo.countByFirmWiseClosedInspectionWithinFiveDays(firm.getId(), fromDate, toDate);
                        String title = "Less than 5 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }
                    {
                        //Within 5 to 10 days
                        int count = (int) oemReportsRepo.countByFirmWiseClosedInspectionWithinFiveToTenDays(firm.getId(), fromDate, toDate);
                        String title = "Within 5 to 10 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }
                    {
                        //Within 10 to 30 days
                        int count = (int) oemReportsRepo.countByFirmWiseClosedInspectionWithinTenToThirtyDays(firm.getId(), fromDate, toDate);
                        String title = "Within 10 to 30 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    {
                        //Within more than 30 days
                        int count = (int) oemReportsRepo.countByFirmWiseClosedInspectionInMoreThanThirtyDays(firm.getId(), fromDate, toDate);
                        String title = "More than 30 days";
                        StringAndCount stringAndCount = new StringAndCount();
                        stringAndCount.setName(title);
                        stringAndCount.setCount(count);
                        stringAndCount.setColorCode(HelpingHand.getRandomColor());

                        stringAndCountList.add(stringAndCount);
                    }

                    barGroup.setBarDataList(stringAndCountList);
                    barGroupList.add(barGroup);
                }


            }

            barGroupListContainer.setBarGroupList(barGroupList);

            barGroupListContainerList.add(barGroupListContainer);
        }

        return barGroupListContainerList;
    }

    @Override
    public List<SingleIncidentAnalysisChartData> getIncidentsOverViewDatewise(Long zoneId, Long divisionId, LocalDateTime fromDate, LocalDateTime toDate) {

        List<SingleIncidentAnalysisChartData> singleIncidentAnalysisChartDataList = new ArrayList<>();
        try {
            // Overall Summary Added Below
            {

                SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                List<PieChartData> chartDataList = new ArrayList<>();
                int totalCount = 0;
                {
                    // adding Issue Category wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> issueCategoryObjAndCountList = new ArrayList<>();
//                    log.info("Division {}",divisionId);
                    List<Object[]> counts = incidentReportsRepo.findCountsByIssueCategoryWithFilters(fromDate.toLocalDate(), toDate.toLocalDate(),zoneId,divisionId);
                    for (Object[] result : counts) {
                        IssueCategory issueCategory = (IssueCategory) result[0];
                        Long count = (Long) result[1];
                        totalCount += count;
                        StringAndCount issueCategoryObjAndCount = new StringAndCount(issueCategory.getName(), Math.toIntExact(count), "");
                        if (issueCategory.getName().equalsIgnoreCase("Mode Change")) {
                            issueCategoryObjAndCount.setColorCode("#f0ad4e");
                        } else if (issueCategory.getName().equalsIgnoreCase("Undesirable Braking")) {
                            issueCategoryObjAndCount.setColorCode("#E91E63");
                        } else {
                            issueCategoryObjAndCount.setColorCode(HelpingHand.getRandomColor());
                        }
                        issueCategoryObjAndCountList.add(issueCategoryObjAndCount);
                    }
                    pieChartData.setName("");
                    pieChartData.setChartDataList(issueCategoryObjAndCountList);
                    chartDataList.add(pieChartData);

                    singleIncidentAnalysisChartData.setTotal(totalCount);
                    // adding Issue Category wise data below
                }

//                {
//                    // adding TCAS wise data
//                    PieChartData pieChartData = new PieChartData();
//                    List<StringAndCount> tcasObjAndCountList = new ArrayList<>();
//                    List<Object[]> counts = tcasBreakingInspectionRepo.findCountsByTcasForMonthAndYear(month, year);
//                    for (Object[] result : counts) {
//                        Tcas tcas = (Tcas) result[0];
//                        Long count = (Long) result[1];
//                        StringAndCount tcasObjAndCount = new StringAndCount(tcas.getName(), Math.toIntExact(count), "");
//                        if (tcas.getName().equalsIgnoreCase("TCAS")) {
//                            tcasObjAndCount.setColorCode("#2196F3");
//                        } else if (tcas.getName().equalsIgnoreCase("Non - TCAS")) {
//                            tcasObjAndCount.setColorCode("#5cb85c");
//                        } else {
//                            tcasObjAndCount.setColorCode(HelpingHand.getRandomColor());
//                        }
//                        tcasObjAndCountList.add(tcasObjAndCount);
//                    }
//
//                    pieChartData.setName("Tcas");
//                    pieChartData.setChartDataList(tcasObjAndCountList);
//                    chartDataList.add(pieChartData);
//                }

                {
                    // adding Possible Issue wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = incidentReportsRepo.findCountsByPossibleIssueBetweenDateAndZoneAndDivision(fromDate.toLocalDate(), toDate.toLocalDate(),zoneId,divisionId);
                    int totalPossibleIssueWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleIssue possibleIssue = (PossibleIssue) result[0];
                        Long count = (Long) result[1];
                        totalPossibleIssueWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleIssue.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleIssueWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
//                        stringAndCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("");
                    pieChartData.setChartDataList(stringAndCountList);
//                    chartDataList.add(pieChartData);
                    // adding Possible Issue wise data above
                }
                {
                    // adding Possible Root Cause wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = incidentReportsRepo.findCountsByPossibleRootCauseBetweenDateAndZoneAndDivision(fromDate.toLocalDate(), toDate.toLocalDate(),zoneId,divisionId);
                    int totalPossibleRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        PossibleRootCause possibleRootCause = (PossibleRootCause) result[0];
                        Long count = (Long) result[1];
                        totalPossibleRootCauseWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleRootCause.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleRootCauseWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleRootCauseWiseCount), HelpingHand.getRandomColor());
//                        stringAndCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("Root  Cause");
                    pieChartData.setChartDataList(stringAndCountList);
                    chartDataList.add(pieChartData);
                    // adding Possible Root Cause wise data above
                }
                {
                    // adding Possible Root Cause wise data below
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> stringAndCountList = new ArrayList<>();
                    List<Object[]> counts = incidentReportsRepo.findCountsByRootCauseSubCategoryBetweenDateAndZoneAndDivision(fromDate.toLocalDate(), toDate.toLocalDate(),zoneId,divisionId);
                    int totalPossibleRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        RootCauseSubCategory possibleRootCause = (RootCauseSubCategory) result[0];
                        Long count = (Long) result[1];
                        totalPossibleRootCauseWiseCount += count;
                        StringAndCount stringAndCount = new StringAndCount(possibleRootCause.getName(), Math.toIntExact(count), HelpingHand.getRandomColor());
                        stringAndCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleRootCauseWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleRootCauseWiseCount), HelpingHand.getRandomColor());
//                        stringAndCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("Root Cause Sub Category");
                    pieChartData.setChartDataList(stringAndCountList);
//                    chartDataList.add(pieChartData);
                    // adding Possible Root Cause wise data above
                }

                singleIncidentAnalysisChartData.setChartTitle("Overall Summary");
                singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

                // Overall Summary Added Above
            }

            // Issue Category Wise Summary Added Below

            List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();

            for (IssueCategory issueCategory : issueCategoryList) {

                if (issueCategory.getName().equalsIgnoreCase("No Issue")
                        || issueCategory.getName().equalsIgnoreCase("Trial")) {
                    continue;
                }

                int issueCategoryId = issueCategory.getId();
                int totalCount = 0;
                List<Object[]> countResult = incidentReportsRepo.findAllInspectionsCountBetweenDateAndFilters(fromDate.toLocalDate(), toDate.toLocalDate(), issueCategoryId,zoneId,divisionId);

                for (Object[] output : countResult) {
                    Long count = (Long) output[1]; // Assuming count is of type Long
                    totalCount = Math.toIntExact(count);
                }

                SingleIncidentAnalysisChartData singleIncidentAnalysisChartData = new SingleIncidentAnalysisChartData();
                List<PieChartData> chartDataList = new ArrayList<>();
                singleIncidentAnalysisChartData.setChartTitle("Summary of " + issueCategory.getName());

                {
                    // Adding issue wise chart data below
                    List<Object[]> counts = incidentReportsRepo.findIssuesCountsByIssueCategoryIdAndFilters(fromDate.toLocalDate(), toDate.toLocalDate(), issueCategoryId,zoneId,divisionId);
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> issueCatWiseDataCountList = new ArrayList<>();
                    int totalPossibleIssueWiseCount = 0;
                    for (Object[] result : counts) {
                        String possibleIssueName = (String) result[0];
                        Long inspectionCount = (Long) result[1];
                        totalPossibleIssueWiseCount += inspectionCount;
                        if (StringUtils.isEmpty(possibleIssueName)) {
                            possibleIssueName = "Others";
                        }
                        StringAndCount stringAndCount = new StringAndCount(possibleIssueName, Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());
                        issueCatWiseDataCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalPossibleIssueWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalPossibleIssueWiseCount), HelpingHand.getRandomColor());
//                        issueCatWiseDataCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("");
                    pieChartData.setChartDataList(issueCatWiseDataCountList);
                    if (!issueCatWiseDataCountList.isEmpty()) {
                        chartDataList.add(pieChartData);
                    }
                    // Adding issue wise chart data above
                }

                {
                    // Adding Root Cause Wise chart data below
                    List<Object[]> counts = incidentReportsRepo.findRootCausesCountsByIssueCategoryIdAndFilters(fromDate.toLocalDate(), toDate.toLocalDate(), issueCategoryId,zoneId,divisionId);
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> rootCauseDataCountList = new ArrayList<>();
                    int totalRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        String rootCauseName = (String) result[0];
                        Long inspectionCount = (Long) result[1];
                        totalRootCauseWiseCount += inspectionCount;
                        if (StringUtils.isEmpty(rootCauseName)) {
                            rootCauseName = "Others";
                        }
                        StringAndCount stringAndCount = new StringAndCount(rootCauseName, Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());

                        rootCauseDataCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalRootCauseWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalRootCauseWiseCount), HelpingHand.getRandomColor());
//                        rootCauseDataCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("Root Cause");
                    pieChartData.setChartDataList(rootCauseDataCountList);
                    if (!rootCauseDataCountList.isEmpty()) {
                        chartDataList.add(pieChartData);
                    }
                    // Adding Root Cause Wise chart data above
                }

                {
                    // Adding Root Cause Wise chart data below
                    List<Object[]> counts = incidentReportsRepo.findRootCauseSubCategoryCountsWithFilters(fromDate.toLocalDate(), toDate.toLocalDate(), issueCategoryId,zoneId,divisionId);
                    PieChartData pieChartData = new PieChartData();
                    List<StringAndCount> rootCauseDataCountList = new ArrayList<>();
                    int totalRootCauseWiseCount = 0;
                    for (Object[] result : counts) {
                        String rootCauseName = (String) result[0];
                        Long inspectionCount = (Long) result[1];
                        totalRootCauseWiseCount += inspectionCount;
                        if (StringUtils.isEmpty(rootCauseName)) {
                            rootCauseName = "Others";
                        }
                        StringAndCount stringAndCount = new StringAndCount(rootCauseName, Math.toIntExact(inspectionCount), HelpingHand.getRandomColor());

                        rootCauseDataCountList.add(stringAndCount);
                    }
//                    if (totalCount - totalRootCauseWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalRootCauseWiseCount), HelpingHand.getRandomColor());
//                        rootCauseDataCountList.add(stringAndCount);
//                    }
                    pieChartData.setName("Root Cause Sub Category");
                    pieChartData.setChartDataList(rootCauseDataCountList);
                    if (!rootCauseDataCountList.isEmpty()) {
                        chartDataList.add(pieChartData);
                    }

                    // Adding Root Cause Wise chart data above
                }

//                {
//                    // Adding Tcaswise chart data below
//                    List<Object[]> counts = tcasBreakingInspectionRepo.findTcasCountsByIssueCategoryIdForMonthAndYear(month, year, issueCategoryId);
//                    List<StringAndCount> tcasDataCountList = new ArrayList<>();
//                    PieChartData pieChartData = new PieChartData();
//                    int totalTcasWiseCount = 0;
//                    for (Object[] result : counts) {
//                        Tcas tcas = (Tcas) result[0];
//                        Long inspectionCount = (Long) result[1];
//                        totalTcasWiseCount += inspectionCount;
//                        StringAndCount tcasObjAndCount = new StringAndCount(tcas.getName(), Math.toIntExact(inspectionCount), "");
//                        if (tcas.getName().equalsIgnoreCase("TCAS")) {
//                            tcasObjAndCount.setColorCode("#2196F3");
//                        } else if (tcas.getName().equalsIgnoreCase("Non - TCAS")) {
//                            tcasObjAndCount.setColorCode("#5cb85c");
//                        } else {
//                            tcasObjAndCount.setColorCode(HelpingHand.getRandomColor());
//                        }
//                        tcasDataCountList.add(tcasObjAndCount);
//                    }
//                    if (totalCount - totalTcasWiseCount > 0) {
//                        StringAndCount stringAndCount = new StringAndCount("Unknown", Math.toIntExact(totalCount - totalTcasWiseCount), HelpingHand.getRandomColor());
//                        tcasDataCountList.add(stringAndCount);
//                    }
//                    pieChartData.setName("Tcas");
//                    pieChartData.setChartDataList(tcasDataCountList);
//                    chartDataList.add(pieChartData);
//                    // Adding Tcaswise chart data above
//                }

                // Adding all Data
                singleIncidentAnalysisChartData.setTotal(totalCount);
                singleIncidentAnalysisChartData.setChartDataList(chartDataList);
                singleIncidentAnalysisChartDataList.add(singleIncidentAnalysisChartData);

            }

            // Issue Category Wise Summary Added Above

        } catch (Exception e) {
            e.printStackTrace();
        }

        return singleIncidentAnalysisChartDataList;
    }

    @Override
    public List<StringAndCount> getDashboardCounts() {

        List<StringAndCount> dashboardCounts = new ArrayList<>();

        {
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total Incidents");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZone(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now())));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Without Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndWithoutTicket(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now())));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("With Ticket");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now(),null)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("With Open Tickets");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now(),true)));
            dashboardCounts.add(stringAndCount);
        }
        {

            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("With Closed Tickets");
            stringAndCount.setCount(Math.toIntExact(getCountByDivisionAndZoneAndTicketStatus(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now(),false)));
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Total Tickets");
            stringAndCount.setCount((int) incidentTicketRepo.count());
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Open Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Open Tickets");
//            stringAndCount.setCount(Math.toIntExact(incidentReportsRepo.findCountByStatus("Open")));
            stringAndCount.setCount(incidentTicketRepo.countByStatus(true));
//            stringAndCount.setCount(0);
            dashboardCounts.add(stringAndCount);
        }
        {
            //Total Closed Incident Counts
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Closed Tickets");
//            stringAndCount.setCount(Math.toIntExact(incidentReportsRepo.findCountByStatus("Close")));
            stringAndCount.setCount(incidentTicketRepo.countByStatus(false));
//            stringAndCount.setCount(0);
            dashboardCounts.add(stringAndCount);
        }
        {
            
            StringAndCount stringAndCount = new StringAndCount();
            stringAndCount.setName("Trip Without Issues");
            stringAndCount.setCount(Math.toIntExact(getNoIssueCountByDivisionAndZone(0L,10L, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now())));
            dashboardCounts.add(stringAndCount);
        }

//        {
//            
//            StringAndCount stringAndCount = new StringAndCount();
//            stringAndCount.setName("Total Users");
//            stringAndCount.setCount(Math.toIntExact(userRepo.count()));
//            dashboardCounts.add(stringAndCount);
//        }


        return dashboardCounts;
    }

    @Override
    public List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirm() {

        List<FirmAndAvgDay> firmAndDaysList = new ArrayList<>();
        List<Firm> firmList = firmService.getAllFirm();

        for (Firm firm :
                firmList) {

            FirmAndAvgDay firmAndDays = new FirmAndAvgDay(firm, 0);

            List<Long> days = oemReportsRepo.daysTakenToCloseByFirm(firm.getId());
//            log.info("Days : {}", days);

            float average = (float) days.stream()
                    .mapToLong(Long::longValue)
                    .average().orElse(0);
            firmAndDays.setAvgDays(Math.round(average));

            firmAndDaysList.add(firmAndDays);
        }

        return firmAndDaysList;
    }

    @Override
    public List<FirmIssueCategoryAvgDay> getAvgDaysTakenToCloseByFirmAndIssueCategory() {
        List<FirmIssueCategoryAvgDay> firmIssueCategoryAvgDayList = new ArrayList<>();

        List<Firm> firmList = firmService.getAllFirm();
        for (Firm firm :
                firmList) {

            FirmIssueCategoryAvgDay firmIssueCategoryAvgDay = new FirmIssueCategoryAvgDay();
            firmIssueCategoryAvgDay.setFirm(firm);
            List<IssueCategoryAvgDay> issueCategoryAvgDayList = new ArrayList<>();

            List<IssueCategory> issueCategoryList = issueCategoryService.getAllIssueCategory();

            for (IssueCategory issueCategory : issueCategoryList) {

                IssueCategoryAvgDay issueCategoryAvgDay = new IssueCategoryAvgDay(issueCategory, 0);

                List<Long> days = oemReportsRepo.daysTakenToCloseByFirmAndIssueCategory(firm.getId(), issueCategory.getId());
//              log.info("Days : {}", days);

                float average = (float) days.stream()
                        .mapToLong(Long::longValue)
                        .average().orElse(0);
                issueCategoryAvgDay.setAvgDays(Math.round(average));

                issueCategoryAvgDayList.add(issueCategoryAvgDay);

            }

            firmIssueCategoryAvgDay.setIssueCategoryAvgDayList(issueCategoryAvgDayList);
            firmIssueCategoryAvgDayList.add(firmIssueCategoryAvgDay);

        }

        return firmIssueCategoryAvgDayList;
    }

    @Override
    public List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirmAndModeChange() {
        List<FirmAndAvgDay> firmAndDaysList = new ArrayList<>();
        List<Firm> firmList = firmService.getAllFirm();

        for (Firm firm :
                firmList) {

            FirmAndAvgDay firmAndDays = new FirmAndAvgDay(firm, 0);

            List<Long> days = oemReportsRepo.daysTakenToCloseByFirmAndIssueCategory(firm.getId(), 1);
//            log.info("Days : {}", days);

            float average = (float) days.stream()
                    .mapToLong(Long::longValue)
                    .average().orElse(0);
            firmAndDays.setAvgDays(Math.round(average));

            firmAndDaysList.add(firmAndDays);

        }

        return firmAndDaysList;
    }

    @Override
    public List<FirmAndAvgDay> getAvgDaysTakenToCloseByFirmAndUndesirableBreaking() {
        List<FirmAndAvgDay> firmAndDaysList = new ArrayList<>();
        List<Firm> firmList = firmService.getAllFirm();

        for (Firm firm :
                firmList) {

            FirmAndAvgDay firmAndDays = new FirmAndAvgDay(firm, 0);

            List<Long> days = oemReportsRepo.daysTakenToCloseByFirmAndIssueCategory(firm.getId(), 2);
//            log.info("Days : {}", days);

            float average = (float) days.stream()
                    .mapToLong(Long::longValue)
                    .average().orElse(0);
            firmAndDays.setAvgDays(Math.round(average));

            firmAndDaysList.add(firmAndDays);

        }

        return firmAndDaysList;
    }

    @Override
    public List<FirmsAndStatusCounts> getCountsByFirmsAndStatus() {

        List<FirmsAndStatusCounts> firmsAndStatusCountsList = new ArrayList<>();

        try {
            List<Firm> firmList = firmService.getAllFirm();
            for (Firm firm : firmList) {

                List<Object[]> counts = oemReportsRepo.findCountsByFirmAndStatus(firm.getId());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("firm", firm.getName());

                Object obj = new Object();
                FirmsAndStatusCounts firmsAndStatusCounts = new FirmsAndStatusCounts();
                firmsAndStatusCounts.setFirm(firm.getName());
                for (Object[] result : counts) {

                    String status = (String) result[0];
                    long totalIncidents = ((Number) result[1]).longValue();

                    if (status.equalsIgnoreCase("open")) {
                        firmsAndStatusCounts.setOpen(Math.toIntExact(totalIncidents));
                    } else if (status.equalsIgnoreCase("close")) {
                        firmsAndStatusCounts.setClose(Math.toIntExact(totalIncidents));
                    } else if (status.equalsIgnoreCase("total")) {
                        firmsAndStatusCounts.setTotal(Math.toIntExact(totalIncidents));
                    }

                }

                firmsAndStatusCountsList.add(firmsAndStatusCounts);
            }
        } catch (Exception e) {
            log.error("GetCountsByFirmsAndStatus Exception : {}", e.toString());
        }

        return firmsAndStatusCountsList;
    }

    @Override
    public List<FirmMonthStatusCount> getMonthlyInspectionCountsByFirmGroupByStatus(int year, int firmId) {
        List<FirmMonthStatusCount> firmMonthStatusCountList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            FirmMonthStatusCount firmMonthStatusCount = new FirmMonthStatusCount();
            if (i == 0) {
                firmMonthStatusCount.setMonth("January");
            } else if (i == 1) {
                firmMonthStatusCount.setMonth("February");
            } else if (i == 2) {
                firmMonthStatusCount.setMonth("March");
            } else if (i == 3) {
                firmMonthStatusCount.setMonth("April");
            } else if (i == 4) {
                firmMonthStatusCount.setMonth("May");
            } else if (i == 5) {
                firmMonthStatusCount.setMonth("June");
            } else if (i == 6) {
                firmMonthStatusCount.setMonth("July");
            } else if (i == 7) {
                firmMonthStatusCount.setMonth("August");
            } else if (i == 8) {
                firmMonthStatusCount.setMonth("September");
            } else if (i == 9) {
                firmMonthStatusCount.setMonth("October");
            } else if (i == 10) {
                firmMonthStatusCount.setMonth("November");
            } else if (i == 11) {
                firmMonthStatusCount.setMonth("December");
            }

            int totalCount = 0;
            List<Object[]> counts = oemReportsRepo.findMonthlyInspectionCountsByFirmGroupByStatus(i+1, year, firmId);
            for (Object[] result : counts) {

                String status = (String) result[0];
                long totalIncidents = ((Number) result[1]).longValue();
                totalCount += totalIncidents;
                if (status.equalsIgnoreCase("open")) {
                    firmMonthStatusCount.setOpen(Math.toIntExact(totalIncidents));
                } else if (status.equalsIgnoreCase("close")) {
                    firmMonthStatusCount.setClose(Math.toIntExact(totalIncidents));
                }

            }
            firmMonthStatusCount.setTotal(Math.toIntExact(totalCount));

            firmMonthStatusCountList.add(firmMonthStatusCount);
        }

        return firmMonthStatusCountList;
    }

    @Override
    public List<FirmsAndStatusCounts> getCountsByFirmAndIssueCategory(int issueCategoryId) {
        List<FirmsAndStatusCounts> firmsAndStatusCountsList = new ArrayList<>();

        List<Firm> firmList = firmService.getAllFirm();

        for (Firm firm : firmList) {
            List<Object[]> counts = oemReportsRepo.findInspectionCountsByFirmAndStatus(firm.getId(),issueCategoryId);
            int totalCount = 0;
            FirmsAndStatusCounts firmsAndStatusCounts = new FirmsAndStatusCounts();
            firmsAndStatusCounts.setFirm(firm.getName());
            for (Object[] result : counts) {
                String status = (String) result[0];
                long totalIncidents = ((Number) result[1]).longValue();
                totalCount += totalIncidents;
                if (status.equalsIgnoreCase("open")) {
                    firmsAndStatusCounts.setOpen(Math.toIntExact(totalIncidents));
                } else if (status.equalsIgnoreCase("close")) {
                    firmsAndStatusCounts.setClose(Math.toIntExact(totalIncidents));
                }
            }
            firmsAndStatusCounts.setTotal(totalCount);
            firmsAndStatusCountsList.add(firmsAndStatusCounts);
        }

        return firmsAndStatusCountsList;
    }

    //Get Total incident count filter wise
    public long getCountByDivisionAndZone(Long divisionId, Long zoneId, LocalDateTime fromDate, LocalDateTime toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //get Total incidents with ticket status
    public long getCountByDivisionAndZoneAndTicketStatus(Long divisionId, Long zoneId, LocalDateTime fromDate, LocalDateTime toDate, Boolean ticketStatus) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IncidentTicket> ticketJoin = inspectionRoot.join("incidentTicket");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (ticketStatus != null) {
            predicates.add(criteriaBuilder.equal(ticketJoin.get("status"), ticketStatus));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public long getCountByDivisionAndZoneAndWithoutTicket(Long divisionId, Long zoneId, LocalDateTime fromDate, LocalDateTime toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.isNull(inspectionRoot.get("incidentTicket")));

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.notEqual(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    //Get Total incident With No Isse count filter wise
    public long getNoIssueCountByDivisionAndZone(Long divisionId, Long zoneId, LocalDateTime fromDate, LocalDateTime toDate) {
        // Create CriteriaBuilder
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        // Create CriteriaQuery
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // Define Root for TcasBreakingInspection
        Root<TcasBreakingInspection> inspectionRoot = criteriaQuery.from(TcasBreakingInspection.class);

        // Join Division and Zone
        Join<TcasBreakingInspection, Division> divisionJoin = inspectionRoot.join("division");
        Join<TcasBreakingInspection, IssueCategory> issueCategoryJoin = inspectionRoot.join("issueCategory");
        Join<Division, Zone> zoneJoin = divisionJoin.join("zone");

        // Select COUNT(t.id)
        criteriaQuery.select(criteriaBuilder.count(inspectionRoot.get("id")));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();

        // Add predicates conditionally
        if (divisionId != null && divisionId != 0) {
            predicates.add(criteriaBuilder.equal(divisionJoin.get("id"), divisionId));
        }
        if (zoneId != null && zoneId != 0) {
            predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
        }
        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(inspectionRoot.get("tripDate"), fromDate, toDate));
        }

        // Add predicate for issueCategory.name != "No Issue"
        predicates.add(criteriaBuilder.equal(issueCategoryJoin.get("name"), "No Issue"));

        // Combine all predicates with AND
        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Execute query
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

}

