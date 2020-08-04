package com.facilio.elasticsearch.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.services.FacilioHttpUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ESUtil {

    public static class ESException extends Exception {
        public ESException(String message) {
            super(message);
        }
    }

    private static String constructESUrl() throws ESException {
        String esDomain = FacilioProperties.getEsDomain();
        String esIndex = FacilioProperties.getEsIndex();
        if (StringUtils.isEmpty(esDomain) || StringUtils.isEmpty(esIndex)) {
            throw new ESException("ES not configured");
        }
        String url = esDomain + "/" + esIndex;
        return url;
    }

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return headers;
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

            String s = FacilioHttpUtils.doHttpPost(url, getHeaders(), null, bodyContent.toString());
            System.out.println("Response: " + s);
        } catch (ESException ex) {
            // not configured
        }
        catch (Exception ex) {
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
        try {
            String url = constructESUrl() + "/_bulk";
            String s = FacilioHttpUtils.doHttpPost(url, getHeaders(), null, bodyContent.toString());
            System.out.println("Response: " + s);
        }
        catch (ESException ex) {}
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void deleteDataOfModule(Long moduleId) {
        try {
            if (moduleId == null) {
                return;
            }
            String url = constructESUrl() + "/_delete_by_query";

            JSONObject query = new ESQuery.ESQueryBuilder(true)
                    .addMatch("moduleId", moduleId)
                    .build();

            String s = FacilioHttpUtils.doHttpPost(url, getHeaders(), null, query.toString());
            System.out.println("Response: " + s);
        }
        catch (ESException ex) {}
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Map<Long, List<Long>> query(String search) throws Exception {
        try {
            String url = constructESUrl() + "/_search";

            String collapseField = "moduleId";

            JSONObject queryBody = new ESQuery.ESQueryBuilder(true)
                    .addQueryString(search)
                    .build();
            JSONObject collapseJSON = new JSONObjectBuilder()
                    .put("field", "moduleId")
                    .put("inner_hits", new JSONObjectBuilder()
                            .put("_source", new JSONArrayBuilder().add("id").add("moduleId").build() )
                            .put("name", collapseField)
                            .put("size", 5)
                            .build()
                    )
                    .build();

            queryBody.put("collapse", collapseJSON);
            String s = FacilioHttpUtils.doHttpPost(url, getHeaders(), null, queryBody.toString());
            return getResultFromSearch(s, collapseField);
        }
        catch (ESException ex) {}
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Map<Long, List<Long>> getResultFromSearch(String resultString, String collapseField) throws Exception {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(resultString);

        Map<Long, List<Long>> hitMap = new HashMap<>();
        JSONObject hits = (JSONObject) json.get("hits");
        if (hits != null && hits.containsKey("hits")) {
            JSONArray resultArray = (JSONArray) hits.get("hits");
            for (int i = 0; i < resultArray.size(); i++) {
                JSONObject innerHit = (JSONObject) ((JSONObject) resultArray.get(i)).get("inner_hits");
                if (innerHit.containsKey(collapseField)) {
                    JSONObject collapseJSON = (JSONObject) innerHit.get(collapseField);
                    JSONObject hitObject = (JSONObject) collapseJSON.get("hits");
                    addInMap(hitMap, hitObject);
                }
            }
        }
        return hitMap;
    }

    private static void addInMap(Map<Long, List<Long>> hitMap, JSONObject hits) {
        JSONArray result = (JSONArray) hits.get("hits");
        for (int i = 0; i < result.size(); i++) {
            JSONObject object = (JSONObject) result.get(i);
            object = (JSONObject) object.get("_source");    // data will be found inside this _source
            Long moduleId = ((Number) object.get("moduleId")).longValue();
            List<Long> ids = hitMap.computeIfAbsent(moduleId, k -> new ArrayList<>());

            Long id = ((Number) object.get("id")).longValue();
            ids.add(id);
        }
    }

}
