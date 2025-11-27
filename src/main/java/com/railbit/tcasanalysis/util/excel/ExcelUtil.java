package com.railbit.tcasanalysis.util.excel;

import com.railbit.tcasanalysis.DTO.incident.NMSIncidentDTO;
import com.railbit.tcasanalysis.DTO.reports.OpenTicketReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {

    public static File createNmsIncidentReportExcel(List<NMSIncidentDTO> reportList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("NMS Incident Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"SNo", "Zone", "Division", "Station", "Station OEM", "Incident Ticket", "Category", "Loco No", "Loco OEM", "Loco Type", "Loco Version", "Date", "Time", "Brief Description"};
        int[] columnWidths = {5, 10, 10, 10, 10, 15, 15, 10, 10, 10, 10, 10, 10, 50};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.setColumnWidth(i, Math.min(columnWidths[i] * 256, 255 * 256)); // Set column width
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            cell.setCellStyle(style);
        }

        // Populate data rows
        int rowNum = 1;
        for (NMSIncidentDTO report : reportList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(report.getZoneCode());
            row.createCell(2).setCellValue(report.getDivisionCode());
            row.createCell(3).setCellValue(report.getStnCode());
            row.createCell(4).setCellValue(report.getStnOem());
            row.createCell(5).setCellValue(report.getIncidentTicket());
            row.createCell(6).setCellValue(report.getCategory());
            row.createCell(7).setCellValue(report.getLocoNo());
            row.createCell(8).setCellValue(report.getLocoOem());
            row.createCell(9).setCellValue(report.getLocoType());
            row.createCell(10).setCellValue(report.getLocoVersion());
            row.createCell(11).setCellValue(report.getTripDate().toString());
            row.createCell(12).setCellValue(report.getIncidentTime());
            row.createCell(13).setCellValue(report.getBriefDescription());
        }

        // Write the output to a file
        File file = File.createTempFile("nms-incident-report", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        return file;
    }

    public static File createOpenTicketReportExcel(List<OpenTicketReportDTO> reportList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Open Ticket Report");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"SNo", "Zone", "Division", "TicketNo", "Category", "Failure Category", "Failure Sub Category", "Incidents Occurred", "Incident Description", "Ticket Description", "First Incident Date", "Assigned To"};
        int[] columnWidths = {5, 10, 10, 15, 15, 15, 15, 15, 50, 50, 15, 10};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            sheet.setColumnWidth(i, Math.min(columnWidths[i] * 256, 255 * 256)); // Set column width
            CellStyle style = workbook.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setAlignment(HorizontalAlignment.CENTER);
            cell.setCellStyle(style);
        }

        // Populate data rows
        int rowNum = 1;
        for (OpenTicketReportDTO report : reportList) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(report.getZoneCode());
            row.createCell(2).setCellValue(report.getDivisionCode());
            row.createCell(3).setCellValue(report.getTicketNo());
            row.createCell(4).setCellValue(report.getIssue() != null ? report.getIssue().getName() : "");
            row.createCell(5).setCellValue(report.getRootCause() != null ? report.getRootCause().getName() : "");
            row.createCell(6).setCellValue(report.getRootCauseSubCategory() != null ? report.getRootCauseSubCategory().getName() : "");
            row.createCell(7).setCellValue(report.getIncidentCount());
            row.createCell(8).setCellValue(report.getIncidentDescription());
            row.createCell(9).setCellValue(report.getTicketDescription());
            row.createCell(10).setCellValue(report.getFirstIncidentDate() != null ? report.getFirstIncidentDate().toString() : "");
            row.createCell(11).setCellValue(report.getAssignTo());
        }

        // Write the output to a file
        File file = File.createTempFile("open-ticket-report", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        return file;
    }
    public static File createDummyExcel() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Dummy Data");

        Row headerRow = sheet.createRow(0);
        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("ID");
        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Name");

        for (int i = 1; i <= 10; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("Name " + i);
        }

        File file = File.createTempFile("dummy-data", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
        return file;
    }
}
