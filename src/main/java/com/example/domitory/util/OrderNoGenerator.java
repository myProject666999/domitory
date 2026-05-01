package com.example.domitory.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OrderNoGenerator {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();
    
    public static String generateRequestNo() {
        String prefix = "RX";
        String timeStr = LocalDateTime.now().format(FORMATTER);
        int random = RANDOM.nextInt(9000) + 1000;
        return prefix + timeStr + random;
    }
    
    public static String generateBillNo() {
        String prefix = "B";
        String timeStr = LocalDateTime.now().format(FORMATTER);
        int random = RANDOM.nextInt(9000) + 1000;
        return prefix + timeStr + random;
    }
    
    public static String generateRecordNo() {
        String prefix = "R";
        String timeStr = LocalDateTime.now().format(FORMATTER);
        int random = RANDOM.nextInt(9000) + 1000;
        return prefix + timeStr + random;
    }
}
