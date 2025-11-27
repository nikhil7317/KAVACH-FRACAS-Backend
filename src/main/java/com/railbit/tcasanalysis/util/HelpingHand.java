package com.railbit.tcasanalysis.util;

import com.railbit.tcasanalysis.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HelpingHand {

    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");


    // Helper method to generate color codes (customize as needed)
    public static String generateColorCode(String rootCauseSubCategoryName) {
        // Example: Generate color codes based on the hash of the string
        int hash = rootCauseSubCategoryName.hashCode();
        return String.format("#%06X", (0xFFFFFF & hash));
    }

    // Utility method to generate random color
    public static String generateRandomColor() {
        Random random = new Random();
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }

    public static String getMonthName(int index) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        if (index < 1 || index > 12) {
            return "Invalid index";
        }
        return months[index - 1];
    }
    public static String getRandomColor(){
        List<String> colorCodes = new ArrayList<>();
//        colorCodes.add("#00FFFF"); // Aqua
        colorCodes.add("#ACE4AA"); // Celadon
        colorCodes.add("#7FB7BE"); // Moonstone
        colorCodes.add("#948392"); // Mountbatten Pink
        colorCodes.add("#DCCFEC"); // Thistle
        colorCodes.add("#7E8D85"); // Battleship Grey
//        colorCodes.add("#F0F7F4"); // Mint Cream
        colorCodes.add("#73937E"); // Cambridge Blue
        colorCodes.add("#8EA8C3"); // Powder Blue
        colorCodes.add("#95BF8F"); // Olivine
        colorCodes.add("#777DA7"); // Glaucous
        colorCodes.add("#987D7C"); // Cinereous
        colorCodes.add("#AF8D86"); // Rosy Brown
        colorCodes.add("#B36A5E"); // Redwood
        colorCodes.add("#F4F1DE"); // EggShell
        colorCodes.add("#E07A5F"); // Brunt Sienna
        colorCodes.add("#3D405B"); // Delft Blue
        colorCodes.add("#F2CC8F"); // Sunset
        colorCodes.add("#9B2915"); // Rufous
        colorCodes.add("#B9C0DA"); // French Grey
        colorCodes.add("#C4DACF"); // Ash Grey
        colorCodes.add("#9E4770"); // Magenta Haze

        Random random = new Random();
        int index = random.nextInt(colorCodes.size());

        return colorCodes.get(index);

    }
    public static Long getUserIdByAuthentication(Authentication authentication){

        // Extract user ID from authentication object
        User user = (User) authentication.getPrincipal();

        Long userId = user.getId(); // Assuming the username is used as the user ID

        return userId;
    }

    public static String convertToInt(String locoNo) {
        try {
            // Try converting the string to a float
            float locoNoFloat = Float.parseFloat(locoNo);
            // If successful, convert the float to an integer
            int locoNoInt = (int) locoNoFloat;
            // Return the converted integer as a string
            return Integer.toString(locoNoInt);
        } catch (NumberFormatException e) {
            // If the conversion to float fails, return the original value
            return locoNo;
        }
    }

    public static Integer convertToInteger(String locoNo) {         
        try {
            // Try converting the string to a float
            float locoNoFloat = Float.parseFloat(locoNo);
            // If successful, convert the float to an integer
            int locoNoInt = (int) locoNoFloat;
            // Return the converted integer as a string
            return locoNoInt;
        } catch (NumberFormatException e) {
            // If the conversion to float fails, return the original value
            return 0;
        }
    }
}
