package com.facilio.util;
import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Log4j
public class ValidatePermissionUtil {
    private static List<String> urls = new ArrayList<>();
    public static void initialize() throws IOException {
        try {

            ValidatePermissionUtil util = new ValidatePermissionUtil();
            File file = new File(util.getClass().getClassLoader().getResource(FacilioUtil.normalizePath("conf/permission.txt")).getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> skipPermissionUrls = new ArrayList<>();
            String url;
            while ((url = reader.readLine()) != null) {
                skipPermissionUrls.add(url);
            }
            urls = skipPermissionUrls;
            reader.close();
        }
        catch (Exception e) {
            LOGGER.info(e);
        }
    }
    public static List<String> getUrls() {
        return urls;
    }
    public static void setUrls(List<String> urls) {
        ValidatePermissionUtil.urls = urls;
    }
    public static boolean hasUrl(String url) {
        if(urls.contains(url)){
            return true;
        }
        return false;
    }
}