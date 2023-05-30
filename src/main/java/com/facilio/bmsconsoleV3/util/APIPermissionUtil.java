package com.facilio.bmsconsoleV3.util;

import com.facilio.security.requestvalidator.retree.Matcher;
import com.facilio.security.requestvalidator.retree.URLReTree;
import com.facilio.util.FacilioUtil;
import com.facilio.util.ValidatePermissionUtil;
import com.opensymphony.xwork2.ActionContext;
import lombok.extern.log4j.Log4j;
import org.apache.struts2.dispatcher.Parameter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class APIPermissionUtil {
    private static URLReTree ExclusionURLReTree;

    public static void init() {
        try {
            ValidatePermissionUtil util = new ValidatePermissionUtil();
            File file = new File(util.getClass().getClassLoader().getResource(FacilioUtil.normalizePath("conf/excludepermission.txt")).getFile());
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> skipPermissionUrls = new ArrayList<>();
            String url;
            while ((url = reader.readLine()) != null) {
                skipPermissionUrls.add(url);
            }
            String[] stringList = new String[skipPermissionUrls.size()];
            skipPermissionUrls.toArray(stringList);
            ExclusionURLReTree = new URLReTree(stringList);
            reader.close();
        }
        catch (Exception e) {
            LOGGER.info(e);
        }
    }

    public static boolean shouldCheckPermission(String url) {
        Matcher exclution = ExclusionURLReTree.matcher(url);
        if(exclution.isMatch()){
            return false;
        }
        return true;
    }
}