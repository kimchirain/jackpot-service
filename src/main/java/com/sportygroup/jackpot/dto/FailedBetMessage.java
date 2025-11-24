package com.sportygroup.jackpot.dto;

import java.time.LocalDateTime;

public record FailedBetMessage(
    BetRequest betRequest,
    String errorMessage,
    String stackTrace,
    int retryCount,
    LocalDateTime failedAt,
    String correlationId
) {

    public static FailedBetMessage from(BetRequest betRequest, Exception e, int retryCount) {
        return new FailedBetMessage(
            betRequest,
            e.getMessage(),
            getStackTraceAsString(e),
            retryCount,
            LocalDateTime.now(),
            betRequest.correlationId()
        );
    }
    
    private static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
            if (sb.length() > 500) { // limit stack trace length
                sb.append("...(truncated)");
                break;
            }
        }
        return sb.toString();
    }
}
