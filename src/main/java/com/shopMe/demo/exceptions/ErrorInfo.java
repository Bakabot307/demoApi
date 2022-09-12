package com.shopMe.demo.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public class ErrorInfo {
    private final Date timestamp;
    private final Integer status;
    private final String url;
    private final String message;



    public ErrorInfo(String url, String message,Date timestamp,Integer status) {
        this.url = url;
        this.message = message;
        this.timestamp= timestamp;
        this.status=status;
    }

    public ErrorInfo(HttpServletRequest request,HttpServletResponse response, Exception ex) {
        this.url = request.getRequestURL().toString();
        this.message = ex.getMessage();
        this.status = response.getStatus();
        this.timestamp = new Date();
    }



    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Integer getStatus() {
        return status;
    }
}
