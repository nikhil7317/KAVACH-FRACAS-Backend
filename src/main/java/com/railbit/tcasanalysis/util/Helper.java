package com.railbit.tcasanalysis.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {
    public static Map<String,Long> convertListToMap(List<Object[]> keyValuePairs) {

        Map<String, Long> resultMap = new HashMap<>();

        for (Object[] pair : keyValuePairs) {
            if (pair.length >= 2) {
                String key = pair[0].toString();     // Assuming key is a String
                Long value = (Long) pair[1];        // Assuming value is a String
                resultMap.put(key.toLowerCase(), value);
            }
        }
        return resultMap;
    }

}
