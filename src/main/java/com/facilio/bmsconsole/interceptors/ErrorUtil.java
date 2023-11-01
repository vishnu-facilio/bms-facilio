package com.facilio.bmsconsole.interceptors;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

public class ErrorUtil {
    public static String sendError(Error error) throws IOException {
        String requestURI = ServletActionContext.getRequest().getRequestURI();
        JsonObject json = new JsonObject();
        json.addProperty("errorCode",error.getErrorCode());
        json.addProperty("errorMessage",error.getErrorMessage()+requestURI);
        String jsonString = new Gson().toJson(json);
        HttpServletResponse response = ServletActionContext.getResponse();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(error.getHttpStatus());
        PrintWriter writer = response.getWriter();
        writer.write(jsonString);
        writer.flush();
        return error.getType();
    }

    public static final String RESOURCE_NOT_FOUND = "resourcenotfound";
    public static final String UNAUTHORIZED = "unauthorized";

    @AllArgsConstructor
    @Getter
    enum Error {
        USER_NOT_IN_APP(HttpURLConnection.HTTP_FORBIDDEN,1,"Sorry, you do not have access to this application. Please contact your admin",UNAUTHORIZED),
        NO_PERMISSION(HttpURLConnection.HTTP_FORBIDDEN,2,"Sorry, you do not have access to this page. Please contact your admin",UNAUTHORIZED),
        PAGE_NOT_FOUND(HttpURLConnection.HTTP_NOT_FOUND,3,"Sorry, the page you have been looking for does not exist. The link you followed maybe broken, or the page may have been moved",RESOURCE_NOT_FOUND),
        BUTTON_VALIDATION(HttpURLConnection.HTTP_BAD_REQUEST,4,"Invalid Button Action for the current Record",RESOURCE_NOT_FOUND),
        PERMISSION_NOT_HANDLED(430,5,"Permission is not handled for:",UNAUTHORIZED);
        private int httpStatus;
        private long errorCode;
        private String errorMessage;
        private String type;
    }

}
