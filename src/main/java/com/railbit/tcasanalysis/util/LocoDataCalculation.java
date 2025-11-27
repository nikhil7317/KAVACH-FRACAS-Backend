package com.railbit.tcasanalysis.util;

import com.railbit.tcasanalysis.entity.LocoMovementData;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocoDataCalculation {

    public List<LocoMovementData> removeDuplicates(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> uniqueKeys = new HashSet<>();
        return dataList.stream()
                .filter(data -> data != null && uniqueKeys.add(data.getLocoFrameNum() + "_" + data.getLocoID()))
                .collect(Collectors.toList());
    }


    public List<LocoMovementData> sortByTimeAsc(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Collections.emptyList();
        }

        return dataList.stream()
                .sorted(Comparator.comparing(LocoMovementData::getTime))
                .collect(Collectors.toList());
    }


    public float calculateTotalMetersTraveled(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0.0F;
        }

        List<LocoMovementData> filteredData = dataList.stream()
                .filter(data -> Integer.parseInt(data.getAbsLocation()) != 0)
                .sorted(Comparator.comparingInt(data -> Integer.parseInt(data.getAbsLocation())))
                .collect(Collectors.toList());

        if (filteredData.size() < 2) {
            return 0.0F;
        }

        float totalDistance = 0.0F;

        for (int i = 1; i < filteredData.size(); i++) {
            int currentLocation = Integer.parseInt(filteredData.get(i).getAbsLocation());
            int previousLocation = Integer.parseInt(filteredData.get(i - 1).getAbsLocation());

            totalDistance += Math.abs(currentLocation - previousLocation);
        }

        return totalDistance;
    }

    public long calculateTotalTravelledTimeInMinutes(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0;
        }

        List<LocoMovementData> filteredData = dataList.stream()
                .filter(data -> Integer.parseInt(data.getAbsLocation()) != 0)
                .collect(Collectors.toList());

        if (filteredData.isEmpty()) {
            return 0;
        }

        LocalTime earliestTime = filteredData.stream()
                .map(LocoMovementData::getTime)
                .min(Comparator.naturalOrder())
                .orElse(LocalTime.MIN);

        LocalTime latestTime = filteredData.stream()
                .map(LocoMovementData::getTime)
                .max(Comparator.naturalOrder())
                .orElse(LocalTime.MAX);

        //return Duration.between(earliestTime, latestTime).toMinutes();

        return Math.round(Duration.between(earliestTime, latestTime).getSeconds() / 60.0);
    }


    public static Duration calculateTotalTime(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Duration.ZERO;
        }

        LocalTime earliestTime = dataList.stream().map(data -> data.getTime())
                .min(Comparator.naturalOrder()).orElse(LocalTime.MIN);

        LocalTime latestTime = dataList.stream().map(data ->data.getTime())
                .max(Comparator.naturalOrder()).orElse(LocalTime.MAX);

        return Duration.between(earliestTime, latestTime);
    }

    public static Duration calculateUpTime(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return Duration.ZERO;
        }

        List<String> upModes = List.of("Full Supervision", "On Sight", "Stand By", "Post Trip", "Override", "Shunt");

        // Filter and sort by time
        List<LocoMovementData> filteredModes = dataList.stream()
                .filter(data -> upModes.contains(data.getLocoMode()))
                .sorted(Comparator.comparing(LocoMovementData::getTime))
                .collect(Collectors.toList());

        Duration totalUpTime = Duration.ZERO;

        for (int i = 1; i < filteredModes.size(); i++) {
            LocalTime previousTime = filteredModes.get(i - 1).getTime();
            LocalTime currentTime = filteredModes.get(i).getTime();
            totalUpTime = totalUpTime.plus(Duration.between(previousTime, currentTime));
        }

        return totalUpTime;
    }


    public static double calculateAvailableTime(List<LocoMovementData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return 0.0;
        }

        Duration upTime = calculateUpTime(dataList);
        Duration totalTime = calculateTotalTime(dataList);
        return totalTime.isZero() ? 0.0 : (double) upTime.toSeconds() / totalTime.toSeconds();
    }
}
