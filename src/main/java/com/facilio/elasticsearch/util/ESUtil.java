package com.facilio.elasticsearch.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.services.FacilioHttpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESUtil {

    private static String constructESUrl() {
        String url = FacilioProperties.getEsDomain() + "/" + FacilioProperties.getEsIndex();
        return url;
    }

    public static void addData(FacilioModule module, List<JSONObject> objectsToBeAdded) {
        try {
            String url = constructESUrl() + "/_bulk";
            StringBuilder bodyContent = new StringBuilder();
            for (JSONObject object : objectsToBeAdded) {
                JSONObject json = new JSONObject();
                JSONObject indexJson = new JSONObject();
                String id = AccountUtil.getCurrentOrg().getOrgId() + "_" + module.getModuleId() + "_" + object.get("id");
                indexJson.put("_id", id);
                json.put("index", indexJson);
                bodyContent.append(json.toString());
                bodyContent.append("\n");
                bodyContent.append(object.toString());
                bodyContent.append("\n");
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            String s = FacilioHttpUtils.doHttpPost(url, headers, null, bodyContent.toString());
            System.out.println("Response: " + s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteData(Map<String, List<Long>> deleteData) throws Exception {
        if (MapUtils.isEmpty(deleteData)) {
            return;
        }

        StringBuilder bodyContent = new StringBuilder();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (String moduleName: deleteData.keySet()) {
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                continue;
            }

            List<Long> ids = deleteData.get(moduleName);
            if (CollectionUtils.isEmpty(ids)) {
                continue;
            }

            for (Long id: ids) {
                JSONObject json = new JSONObject();
                String esId = AccountUtil.getCurrentOrg().getOrgId() + "_" + module.getModuleId() + "_" + id;
                json.put("_id", esId);
                JSONObject deleteJson = new JSONObject();
                deleteJson.put("delete", json);

                bodyContent.append(deleteJson.toJSONString());
                bodyContent.append("\n");
            }
        }
        if (bodyContent.length() <= 0) {
            return;
        }
        String url = constructESUrl() + "/_bulk";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String s = FacilioHttpUtils.doHttpPost(url, headers, null, bodyContent.toString());
        System.out.println("Response: " + s);
    }

    public static void deleteDataOfModule(Long moduleId) {
        try {
            if (moduleId == null) {
                return;
            }
            String url = constructESUrl() + "/_delete_by_query";
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");

            JSONObject query = new ESQuery.ESQueryBuilder(true)
                    .addMatch("moduleId", moduleId)
                    .build();

            String s = FacilioHttpUtils.doHttpPost(url, headers, null, query.toString());
            System.out.println("Response: " + s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
