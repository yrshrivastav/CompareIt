package com.compareit.app.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
//    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public ApiResponse(String message) {
//        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
