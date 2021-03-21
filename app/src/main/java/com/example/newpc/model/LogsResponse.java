package com.example.newpc.model;

import java.util.List;

public class LogsResponse {

    private boolean success;
    private String message;
    private List<LogsModel> data;

    public LogsResponse(boolean success, String message, List<LogsModel> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<LogsModel> getData() {
        return data;
    }
}
