package com.railbit.tcasanalysis.util;

import com.railbit.tcasanalysis.DTO.incident.IncidentDTO;
import com.railbit.tcasanalysis.entity.loco.EvntMsg;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class LocoFaultMessage {

    private EvntMsg getGeneralSOSFaultMsg(IncidentDTO rlf) {
        EvntMsg evntMsg = new EvntMsg();
        String absLoc=formatAbsLocation(rlf.getAbsLocation());
        String message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", General SOS generated.";
        evntMsg.setIssueMsg(message);
        evntMsg.setStartTime(rlf.getDate());
        evntMsg.setEndTime(rlf.getDate());
        return evntMsg;
    }

   private EvntMsg getLocoSOSFaultMsg(IncidentDTO rlf) {
        EvntMsg evntMsg = new EvntMsg();
        String absLoc=formatAbsLocation(rlf.getAbsLocation());
        String message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco SOS generated.";
        evntMsg.setIssueMsg(message);
        evntMsg.setStartTime(rlf.getDate());
        evntMsg.setEndTime(rlf.getDate());
        return evntMsg;
    }

    public EvntMsg getFSBBreakFaultMsg(IncidentDTO rlf) {
        EvntMsg evntMsg = new EvntMsg();
        String absLoc=formatAbsLocation(rlf.getAbsLocation());
        String message = rlf.getStation() + ": FSB applied at " + rlf.getTime() + " hrs/Km No " + absLoc +
                " when Loco Speed was " + rlf.getLocoSpeed() + " kmph";
        if (rlf.getToSpeed() != null && !rlf.getToSpeed().trim().isEmpty()
                && !"Not Used".equalsIgnoreCase(rlf.getToSpeed())
                && !"Unrestricted".equalsIgnoreCase(rlf.getToSpeed())) {
            message += ", Turnout speed " + rlf.getToSpeed()+" kmph";
        }
//        else {
//            message += ", Turnout speed Overspeed";
//        }

        if (rlf.getToDistance() != null && !rlf.getToDistance().trim().isEmpty()
                && !"0".equals(rlf.getToDistance())) {
            message += ", Turnout Distance " + rlf.getToDistance() + " mtrs.";
        }

        if (rlf.getMA() != null && !rlf.getMA().trim().isEmpty()
                && !"0".equalsIgnoreCase(rlf.getMA().trim())) {
            message += " and MA " + rlf.getMA();
        }

        evntMsg.setIssueMsg(message);
        evntMsg.setStartTime(rlf.getDate());
        evntMsg.setEndTime(rlf.getDate());
        evntMsg.setIssueName("FSB");
        evntMsg.setIssueCategory("Desirable Braking");
        return evntMsg;
    }

    public EvntMsg getEBFaultMsg(IncidentDTO rlf) {
        EvntMsg evntMsg = new EvntMsg();
        String absLoc = formatAbsLocation(rlf.getAbsLocation());

        StringBuilder message = new StringBuilder(rlf.getStation())
                .append(": EB applied");

        String value = rlf.getLocoMode();
        if (value != null && value.equalsIgnoreCase("System Failure")) {
            message.append(" in system failure mode ").append(value);
        }
         message.append(" at ")
                .append(rlf.getTime())
                .append(" hrs/Km No ")
                .append(absLoc)
                .append(" when Loco Speed was ")
                .append(rlf.getLocoSpeed())
                .append(" Kmph");

        if (rlf.getMA() != null && !rlf.getMA().isEmpty()) {
            try {
                Integer maValue = Integer.parseInt(rlf.getMA());
                if (maValue > 0) {
                    message.append(" and MA was ").append(maValue);
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid MA value: " + rlf.getMA());
            }
        }

        message.append(".");

        // Set event message details
        evntMsg.setIssueMsg(message.toString());
        evntMsg.setStartTime(rlf.getDate());
        evntMsg.setEndTime(rlf.getDate());
        evntMsg.setIssueName("EB");
        evntMsg.setIssueCategory("Undesirable Braking");

        return evntMsg;
    }




    public EvntMsg getModeChangeFaultMsg(IncidentDTO rlf, String mode) {
        EvntMsg evntMsg = new EvntMsg();
        String message;
        String absLoc=formatAbsLocation(rlf.getAbsLocation());
        switch (mode.trim()) {
            case "Limited Supervision":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco went to LS Mode";
                evntMsg.setIssueName("LS mode");
                break;
            case "Staff Responsible":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco went to SR Mode";
                evntMsg.setIssueName("SR mode");
                break;
            case "Trip":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco went to Trip Mode";
                evntMsg.setIssueName("Trip mode");
                break;
            case "Isolation":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco TCAS Isolated by LP";
               // evntMsg.setIssueName("Isolation");
                break;
            case "System Failure":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco went to System Failure Mode";
               // evntMsg.setIssueName("SF");
                break;
            case "Override":
                message = rlf.getStation() + ": at " + rlf.getTime() + " hrs/Km No " + absLoc + ", Loco went to Override Mode";
                evntMsg.setIssueName("Override mode");
                break;
            default:
                message = rlf.getStation() + ". Loco Mode " + mode + " at " + rlf.getTime();
        }
        evntMsg.setIssueMsg(message);
        evntMsg.setIssueCategory("Mode Change");
        evntMsg.setStartTime(rlf.getDate());
        evntMsg.setEndTime(rlf.getDate());
        return evntMsg;
    }

    public static String formatAbsLocation(String absLocation) {

        if (absLocation == null || absLocation.isEmpty()) {
            return "0";
        }
      try {

            int absLocationValue = Integer.parseInt(absLocation);
            if (absLocationValue == 0) {
                return "0";
            }
            BigDecimal value = BigDecimal.valueOf(absLocationValue / 1000.0);
            return value.stripTrailingZeros().toPlainString();
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid absLocation value: " + absLocation, e);
        }
    }

    private String getLS2FSModeMsg(IncidentDTO rlf) {
        return " and came back to FS mode at " + rlf.getTime() + " hrs/Km No " + rlf.getAbsLocation() + ".";
    }

    public String getNextLocoModeMsg(String absLocation,String time, String mode) {
        EvntMsg evntMsg = new EvntMsg();
        String message;
        String absLoc = formatAbsLocation(absLocation);
        switch (mode.trim()) {
            case "Limited Supervision":
                message = " and came back to LS mode " + "at " + time + " hrs/Km No " + absLoc + ".";
                evntMsg.setIssueName("LS mode");
                break;
             case "Full Supervision":
                message = " and came back to FS mode" + " at " + time + " hrs/Km No " + absLoc +".";
                evntMsg.setIssueName("FS mode");
                break;
            case "Staff Responsible":
                message =" and came back to SR mode" + " at " + time + " hrs/Km No " + absLoc +".";
                evntMsg.setIssueName("SR mode");
                break;
            case "Trip":
                message = " and came back to Trip mode" + " at " + time + " hrs/Km No " + absLoc + ".";
                evntMsg.setIssueName("Trip mode");
                break;
            case "Isolation":
                message = " and came back to Isolation mode" + " at " + time + " hrs/Km No " + absLoc + ".";
                break;
            case "System Failure":
                message = " and came back to SF mode" + " at " + time + " hrs/Km No " + absLoc + ".";
               break;
            case "Override":
                message = " and came back to Override mode" + " at " + time + " hrs/Km No " + absLoc + ".";
                evntMsg.setIssueName("Override mode");
                break;
            default:
                message = ".";
        }
        return  message;
    }

//    private EvntMsg GetGeneralSOSFaultMsg(RlyLocoFields rlf)
//    {
//        EvntMsg evntMsg = new EvntMsg();
//        string empty = string.Empty;
//        empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", General SOS generated.";
//        evntMsg.issueMsg = empty;
//        evntMsg.startTime = rlf.dtDateTime;
//        evntMsg.endTime = rlf.dtDateTime;
//        return evntMsg;
//    }
//
//    private EvntMsg GetLocoSOSFaultMsg(RlyLocoFields rlf)
//    {
//        EvntMsg evntMsg = new EvntMsg();
//        string empty = string.Empty;
//        empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco SOS generated.";
//        evntMsg.issueMsg = empty;
//        evntMsg.startTime = rlf.dtDateTime;
//        evntMsg.endTime = rlf.dtDateTime;
//        return evntMsg;
//    }
//
//    private EvntMsg GetFSBFaultMsg(RlyLocoFields rlf)
//    {
//        EvntMsg evntMsg = new EvntMsg();
//        string empty = string.Empty;
//        empty = rlf.StnCode + ": FSB applied at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + " when Loco Speed was " + rlf.LocoSpeed + " Kmph";
//        empty = ((!(rlf.TOSpeed == "Not Used") && !(rlf.TOSpeed == "Unrestricted")) ? (empty + ", Turnout speed " + rlf.TOSpeed) : (empty + ", Turnout speed Overspeed"));
//        if (rlf.ToDistance != "0")
//        {
//            empty = empty + ", Turnout Distance " + rlf.ToDistance + " mtrs.";
//        }
//        if (rlf.MA != "0")
//        {
//            empty = empty + " and MA " + rlf.MA;
//        }
//        evntMsg.issueMsg = empty;
//        evntMsg.startTime = rlf.dtDateTime;
//        evntMsg.endTime = rlf.dtDateTime;
//        evntMsg.issueName = "FSB";
//        return evntMsg;
//    }
//
//    private EvntMsg GetEBFaultMsg(RlyLocoFields rlf)
//    {
//        EvntMsg evntMsg = new EvntMsg();
//        string empty = string.Empty;
//        empty = rlf.StnCode + ": EB applied at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + " when Loco Speed was " + rlf.LocoSpeed + " Kmph and MA was " + rlf.MA + ".";
//        evntMsg.issueMsg = empty;
//        evntMsg.startTime = rlf.dtDateTime;
//        evntMsg.endTime = rlf.dtDateTime;
//        evntMsg.issueName = "EB";
//        return evntMsg;
//    }
//
//    private EvntMsg GetModeFaultMsg(RlyLocoFields rlf, string mode)
//    {
//        EvntMsg evntMsg = new EvntMsg();
//        string empty = string.Empty;
//        if (mode == modeLimitedSupervision)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco went to LS Mode.";
//            evntMsg.issueName = "LS mode";
//        }
//        else if (mode == modeSR)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco went to SR Mode.";
//            evntMsg.issueName = "SR mode";
//        }
//        else if (mode == modeTrip)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco went to Trip Mode.";
//            evntMsg.issueName = "Trip mode";
//        }
//        else if (mode == modeIsolation)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco TCAS Isolated by LP.";
//        }
//        else if (mode == modeSystemFailure)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco went to System Failure Mode.";
//        }
//        else if (mode == modeOverride)
//        {
//            empty = rlf.StnCode + ": at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ", Loco went to Override Mode.";
//            evntMsg.issueName = "Override mode";
//        }
//        else
//        {
//            empty = rlf.StnCode + ". Loco Mode " + mode + " at " + rlf.Time;
//        }
//        evntMsg.startTime = rlf.dtDateTime;
//        evntMsg.endTime = rlf.dtDateTime;
//        evntMsg.issueMsg = empty;
//        return evntMsg;
//    }
//
//    private string GetLS2FSModeMsg(RlyLocoFields rlf)
//    {
//        return " and came back to FS mode at " + rlf.Time + " hrs/Km No " + rlf.AbsLocation + ".";
//    }
}
