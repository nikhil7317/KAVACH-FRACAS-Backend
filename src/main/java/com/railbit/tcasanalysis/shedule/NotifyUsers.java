package com.railbit.tcasanalysis.shedule;

import com.railbit.tcasanalysis.entity.TcasBreakingInspection;
import com.railbit.tcasanalysis.entity.User;
import com.railbit.tcasanalysis.service.NotificationService;
import com.railbit.tcasanalysis.service.TcasBreakingInspectionService;
import com.railbit.tcasanalysis.service.UserService;
import com.railbit.tcasanalysis.util.HelpingHand;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotifyUsers implements Job {

    @Autowired
    private TcasBreakingInspectionService tcasBreakingInspectionService;
    @Autowired
    private UserService userService;
    @Autowired
    private final NotificationService notificationService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

//        notifyOEMs();
    }

    private void notifyOEMs() {

        List<TcasBreakingInspection> pendingInspections = tcasBreakingInspectionService.getPendingInspections();

        for (TcasBreakingInspection inspection : pendingInspections) {
            List<User> userListToNotify = userService.getAllOEMUsersRelatedToInspection(inspection);

            String title = "Attention Required";
            String msg = "Attention required on the incident occurred in loco " + inspection.getLoco().getLocoNo() + " on "+ inspection.getCreatedDateTime().format(HelpingHand.dateFormatter) + " " + inspection.getCreatedDateTime().format(HelpingHand.timeFormatter) +" .";

            notificationService.sendNotificationToUsersAfterInspectionStatusAdded(userListToNotify,inspection,title,msg);
        }

    }

}
