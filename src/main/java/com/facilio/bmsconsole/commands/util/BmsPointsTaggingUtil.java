package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.util.*;

public class BmsPointsTaggingUtil {
    private static final Logger LOGGER = LogManager.getLogger(BmsPointsTaggingUtil.class.getName());

    private static HashMap<String, String> getSplitterMap() throws Exception {
        HashMap<String, String> splitterMap = new HashMap<>();
        FacilioModule module = AccountConstants.getOrgInfoModule();
        List<FacilioField> fields = AccountConstants.getOrgInfoFields();
        FacilioField name = FieldFactory.getAsMap(fields).get("name");
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName());
        List<Map<String, Object>> orgInfoTable = selectBuilder.get();
        for (Map<String, Object> eachRecord : orgInfoTable) {
            if (((String) eachRecord.get("name")).contains("splitter_")) {
                splitterMap.put((String) eachRecord.get("name"), (String) eachRecord.get("value"));
            }
        }
        return splitterMap;
    }

    private static String getSplitter(HashMap<String, String> splitterMap, Map<String, Object> infoMap){
        String agentNameKey = "splitter_" + (String) infoMap.get("agentName");
        String agentTypeKey = "splitter_" + (String) infoMap.get("agentType");
            if (splitterMap.containsKey(agentNameKey)) {
                return splitterMap.get(agentNameKey);
            }
            if (splitterMap.containsKey(agentTypeKey)) {
                return splitterMap.get(agentTypeKey);
            }
            if (splitterMap.containsKey("splitter_org")) {
                return splitterMap.get("splitter_org");
            }
            throw new RuntimeException("OrgInfo table doesn't have a given key");
    }

    public static JSONObject convertToList(List<HashMap<String, Object>> pointsMapList, HashMap<String, String> splitterMap) throws Exception {
        List<String> newPointNameList = new ArrayList<>();
        Map<String,Long> siteIdMap = new HashMap<>();
        JSONObject jsonData = new JSONObject();
        for (Map<String, Object> pointMap : pointsMapList) {
            String controllerName = (String) pointMap.get("controller");
            List<String> pointNameList = (List<String>) pointMap.get("pointName");
            String splitter = getSplitter(splitterMap, pointMap);
            for (String pointName : pointNameList) {
                String newPointName = "splitter#" + splitter + "#" + controllerName + splitter + pointName;
                newPointNameList.add(newPointName);
                siteIdMap.put(newPointName,(Long) pointMap.get("siteId"));
            }
        }
        jsonData.put("pointNameList",newPointNameList);
        jsonData.put("siteIdMap",siteIdMap);
        return jsonData;
    }

    public static void tagPointListV1(List<HashMap<String, Object>> pointsMapList) throws Exception {
        LOGGER.info("tagPointListV1 pointsMapList to Tag :" + pointsMapList.toString());
        HashMap<String, String> splitterMap = BmsPointsTaggingUtil.getSplitterMap();
        JSONObject jsonData = convertToList(pointsMapList,
                splitterMap);
        predictApi((List<String>)jsonData.get("pointNameList"),pointsMapList,(Map<String,Long>) jsonData.get("siteIdMap"),null);
        //tagPointList(resultMap);
        //tagPointList(pointNameList, pointsMapList, null);
    }

    private static void predictApi(List<String> pointNameList,List<HashMap<String, Object>> pointsMapList,Map<String, Long>siteIdMap ,Map<String,Map<String,Object>>pointsMap ) throws Exception {
        LOGGER.info("predictApi method pointNameList :" + pointNameList.toString());
        JSONObject postObj = new JSONObject();
        postObj.put("data", pointNameList);
        postObj.put("siteIdMap", siteIdMap);
        postObj.put("pointsMap", pointsMap);
        postObj.put("pointsMapList",pointsMapList);
        postObj.put("org_id", MLServiceUtil.getOrgInfo().get("orgId"));
        postObj.put("callback_url",FacilioProperties.getMainAppDomain());
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postURL = FacilioProperties.getAnomalyPredictAPIURL() + "/getMetaDataV1";
        LOGGER.info("predict api ---"+"POSTURL---  "+postURL+"HEADERS--- "+headers+"   POSTOBJ --- "+postObj.toString());
        String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(), 300);
        if (org.apache.commons.lang.StringUtils.isEmpty(result) || result.contains("Internal Server Error")) {
            LOGGER.fatal("Error getMetaDataV1 api " + postURL + " ERROR MESSAGE : " + "Response is not valid. RESULT : " + result);
        }
        //JSONParser parser = new JSONParser();
        //JSONObject response = (JSONObject) parser.parse(result);
        //Map<String, Map<String, Object>> resultMap = (Map<String, Map<String, Object>>) response.get("data");
        //return resultMap;
    }

    public static void tagPointList(Map<String, Map<String, Object>> resultMap) throws
            Exception {
        //Map<String, Map<String, Object>> resultMap = predictApi(pointNameList);
        resultMap.remove("remote_ip");
        resultMap.remove("responseName");
        resultMap.remove("orgid");
        resultMap.remove("agentId");
        //Map<String,Long>  = (Map<String,Long>) resultMap.get("metaData").get("siteIdMap");
        Map<String , Map<String,Object>> pointNameMap = (Map<String,Map<String,Object>>) resultMap.get("metaData").get("pointNameMap");

        resultMap.remove("metaData");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

        FacilioField clusterNameField = fieldsMap.get("clusterName");
        FacilioField categoryField = fieldsMap.get("categoryName");
        FacilioField readingsField = fieldsMap.get("readingName");
        FacilioField updatedField = fieldsMap.get("updated");
        List<FacilioField> selectedField = new ArrayList<FacilioField>();
        selectedField.add(clusterNameField);
        selectedField.add(categoryField);
        selectedField.add(readingsField);
        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(updatedField, "2", NumberOperators.GREATER_THAN));
        if ((genericSelectBuilder.get().size() > 0) && (pointNameMap == null)) {
            List<Map<String, Object>> selectedRecords = genericSelectBuilder.get();
            Map<String, Map<String, String>> clusterMap = new HashMap<>();
            for (Map<String, Object> EachMap : selectedRecords) {
                String clusterName = (String) EachMap.get("clusterName");
                if (!clusterMap.containsKey(clusterName)) {
                    Map<String, String> currMap = new HashMap<>();
                    currMap.put("categoryName", (String) EachMap.get("categoryName"));
                    currMap.put("readingName", (String) EachMap.get("readingName"));
                    currMap.put("assetName", (String) EachMap.get("assetName"));
                    clusterMap.put(clusterName, currMap);
                }
            }
            for (Map.Entry<String, Map<String, Object>> pointMap : resultMap.entrySet()) {
                String newPointName = pointMap.getKey();
                String clusterName = newPointName.replaceAll("[0-9]", "");
                if (clusterMap.containsKey(clusterName)) {
                    resultMap.get(newPointName).put("categoryName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("categoryName").split(","))));
                    resultMap.get(newPointName).put("readingName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("readingName").split(","))));
                    //resultMap.get(newPointName).put("assetName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("assetName").split(","))));
                    resultMap.get(newPointName).put("isUpdated", 3);

                }
            }
        }
        //Map<String, Map<String, Object>> newResultMap = new HashMap<>();
        for(Map.Entry<String, Map<String, Object>> taggedMap:resultMap.entrySet()){
            String key = taggedMap.getKey();
            String splitter  = resultMap.get(key).get("splitter").toString();
            String controllerName  = key.split(splitter)[0];
            resultMap.get(key).put("controllerName",controllerName);
            //newResultMap.put(key.replaceFirst(controllerName+splitter,"") , resultMap.get(key));
        }
        if(pointNameMap == null){
        insertRecords(resultMap, module, fields);
        }else{
            updateRecords(resultMap,module,fields,pointNameMap);
        }
    }


    public static void updateRecords(Map<String, Map<String, Object>> resultMap, FacilioModule
            module, List<FacilioField> fields,Map<String , Map<String,Object>> pointNameMap) throws Exception {
        //List<String> keyList = new ArrayList<>(resultMap.keySet());
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, Map<String, Object>> pointMap  : resultMap.entrySet()) {
            String key = pointMap.getKey();
            String newKey = "splitter#"+resultMap.get(key).get("splitter")+"#"+key;
            Map<String, Object> row = new HashMap<>();
            if(((Long) pointNameMap.get(newKey).get("code")) == 1) {
                //row.put("categoryName", String.join(",", (List<String>) resultMap.get(key).get("categoryName")));
                row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
                row.put("assetName", String.join(",", (List<String>) resultMap.get(key).get("assetName")));
            }else if(((Long) pointNameMap.get(newKey).get("code")) == 2){
                row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
            }else if(((Long) pointNameMap.get(newKey).get("code")) == 3){
                row.put("assetName", String.join(",", (List<String>) resultMap.get(key).get("assetName")));
            }else {
                row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
                row.put("assetName", String.join(",", (List<String>) resultMap.get(key).get("assetName")));
                row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
            }
            row.put("modifiedTime",currentTime);
            row.put("logs", ((Map<String, Object>) resultMap.get(key).get("logs")).toString());
            GenericUpdateRecordBuilder builder1 = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition("POINT_NAME", "pointName", key, StringOperators.IS));
            builder1.update(row);
        }
    }

    public static void insertRecords(Map<String, Map<String, Object>> resultMap, FacilioModule
            module, List<FacilioField> fields) throws Exception {
        /*List<String> keyList = new ArrayList<>(resultMap.keySet());
        Map<String, Map<String, Object>> updatePointMap = getTaggedPointListFromPoint(keyList, null);
        for (String key : updatePointMap.keySet()) {
            keyList.remove(key);
            updatePointMap.put(key, resultMap.remove(key));
        }
        //updateRecords(updatePointMap, module, fields,currentTime);*/
        //List<String> keyList = new ArrayList<>(resultMap.);
        long currentTime = System.currentTimeMillis();

        for (Map.Entry<String, Map<String, Object>> pointMap : resultMap.entrySet()) {
            String key = pointMap.getKey();
            Map<String, Object> row = new HashMap<>();
            String splitter = resultMap.get(key).get("splitter").toString();
            String controllerName= resultMap.get(key).get("controllerName").toString();
            row.put("pointName", key);
            row.put("controllerName", controllerName);
            row.put("moduleId", module.getModuleId());
            row.put("clusterName", resultMap.get(key).get("cluster_name"));
            row.put("categoryName", String.join(",", (List<String>) resultMap.get(key).get("categoryName")));
            row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
            row.put("assetName", String.join(",", (List<String>) resultMap.get(key).get("assetName")));
            if(resultMap.get(key).containsKey("isUpdated")) {
                row.put("updated", resultMap.get(key).get("isUpdated"));
            }else{ row.put("updated", 0);
            }
            row.put("siteId", resultMap.get(key).get("siteId"));
            row.put("splitter", splitter);
            row.put("createdTime", currentTime);
            row.put("logs", ((Map<String, Object>) resultMap.get(key).get("logs")).toString());
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields);
            builder.insert(row);
        }
    }
    public static Map<String, Map<String, Object>> getTaggedPointList(List<String> controllerNameList) throws Exception {
     return getTaggedPointListV1(controllerNameList , true);
    }

    public static Map<String, Map<String, Object>> getTaggedPointListV1(List<String> controllerNameList,Boolean filterControllerName) throws
            Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField pointNameField = fieldsMap.get("controllerName");

        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(pointNameField, StringUtils.join(controllerNameList, ","), StringOperators.IS));

        List<Map<String, Object>> predictedPointNameList = genericSelectBuilder.get();
        Map<String, Map<String, Object>> recordMap = new HashMap<>();
        for (Map<String, Object> pointNameMap : predictedPointNameList) {
            Map<String, Object> map = new HashMap<>();
            List<String> categoryList = new ArrayList<>();
            List<String> assetList = new ArrayList<>();
            List<String> readingList = new ArrayList<>();

            if (pointNameMap.get("categoryName") != null) {
                categoryList = Arrays.asList(((String) pointNameMap.get("categoryName")).split(","));
            }
            if (pointNameMap.get("assetName") != null) {
                assetList = Arrays.asList(((String) pointNameMap.get("assetName")).split(","));
            }
            if (pointNameMap.get("readingName") != null) {
                readingList = Arrays.asList(((String) pointNameMap.get("readingName")).split(","));
            }
            map.put("categoryIds", categoryList);
            map.put("assetIds", assetList);
            map.put("readingIds", readingList);
            String newPointName =(String) pointNameMap.get("pointName");
            if(filterControllerName) {
                newPointName = newPointName.replaceFirst((String) pointNameMap.get("controllerName") + (String) pointNameMap.get("splitter"), "");
            }
            recordMap.put(newPointName, map);
            }
        return recordMap;
    }

    public static Map<String, Map<String, Object>> getTaggedPointListFromPoint
            (List<String> pointNameList, String controllerName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField pointNameField = fieldsMap.get("pointName");
        FacilioField controllerNameField = fieldsMap.get("controllerName");

        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(pointNameField, StringUtils.join(pointNameList, ","), StringOperators.IS));
        if (controllerName != null) {
            genericSelectBuilder.andCondition(CriteriaAPI.getCondition(controllerNameField, controllerName, StringOperators.IS));
        }
        List<Map<String, Object>> predictedPointNameList = genericSelectBuilder.get();
        Map<String, Map<String, Object>> recordMap = new HashMap<>();
        for (Map<String, Object> pointNameMap : predictedPointNameList) {
            //String newPointName = (String) pointNameMap.get("controllerName") + (String) pointNameMap.get("splitter") + (String) pointNameMap.get("pointName");
            //pointNameMap.put("newPointName", newPointName);
            recordMap.put((String) pointNameMap.remove("pointName"), pointNameMap);
        }
        return recordMap;
    }
    private static void updateCluster(String clusterName,Map<String, Object> row,FacilioModule module,List<FacilioField> fields) throws Exception {

            GenericUpdateRecordBuilder builder2 = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition("CLUSTER_NAME", "clusterName", clusterName, StringOperators.IS));
            builder2.update(row);
    }


    public static void updateTable
            (Map<String, Map<String, String>> actualTaggedMap) throws
            Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        List<String> categoryUpdatedClusterNames = new ArrayList<>();
        List<String> readingUpdatedClusterNames = new ArrayList<>();
        Long modifiedTime = System.currentTimeMillis();
        for (Map.Entry<String, Map<String, String>> pointMap : actualTaggedMap.entrySet()) {
            String key = pointMap.getKey();
            //String clusterName = ((String) map.get(key).get("newPointName")).replaceAll("[0-9]", "");
            String clusterName = key.replaceAll("[0-9]", "");
            Map<String, Object> assetNameMap = new HashMap<>();
            assetNameMap.put("assetName", actualTaggedMap.get(key).get("assetName"));
            int isUpdated = Integer.parseInt(actualTaggedMap.get(key).get("isUpdated"));
            assetNameMap.put("updated",isUpdated);

            Map<String, Object> row = new HashMap<String, Object>();
            if((isUpdated ==1 || isUpdated ==2) && (!categoryUpdatedClusterNames.contains(clusterName))){
                row.put("categoryName", actualTaggedMap.get(key).get("categoryName"));
                row.put("updated",isUpdated);
                row.put("modifiedTime", modifiedTime);
                updateCluster(clusterName,row,module,fields);
                categoryUpdatedClusterNames.add(clusterName);
            } else if((!readingUpdatedClusterNames.contains(clusterName))) {
                row.put("categoryName", actualTaggedMap.get(key).get("categoryName"));
                row.put("readingName", actualTaggedMap.get(key).get("readingName"));
                row.put("updated",3);
                row.put("modifiedTime", modifiedTime);
                updateCluster(clusterName,row,module,fields);
                readingUpdatedClusterNames.add(clusterName);
            }
            GenericUpdateRecordBuilder builder1 = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition("POINT_NAME", "pointName", key, StringOperators.IS));
            builder1.update(assetNameMap);
        }
    }

    public static Map<String, Object> getNotUpdatedPointList() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField updatedField = fieldsMap.get("updated");
        FacilioField pointNameField = fieldsMap.get("pointName");
        FacilioField splitterField = fieldsMap.get("splitter");
        FacilioField controllerField = fieldsMap.get("controllerName");
        FacilioField categoryField = fieldsMap.get("categoryName");
        FacilioField siteIdField = fieldsMap.get("siteId");


        List<FacilioField> selectedFields = new ArrayList<>();
        selectedFields.add(pointNameField);
        selectedFields.add(splitterField);
        selectedFields.add(controllerField);
        selectedFields.add(updatedField);
        selectedFields.add(categoryField);
        selectedFields.add(siteIdField);

        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(selectedFields)
                .andCondition(CriteriaAPI.getCondition(updatedField, "4", NumberOperators.NOT_EQUALS));
        genericSelectBuilder.get();
        List<Map<String, Object>> records = genericSelectBuilder.get();
        List<String> pointNameList = new ArrayList<>();
        Map<String, Long> siteIdMap = new HashMap<>();
        Map<String, Object> pointNameObj = new HashMap<>();
        Map<String,Map<String,Object>> pointNameMap = new HashMap<>();
        for (Map<String, Object> each : records) {
            Map<String,Object> taggedIds = new HashMap<>();
            String newPointName = "splitter#" + (String) each.get("splitter") + "#" + (String) each.get("pointName");
            pointNameList.add(newPointName);
            taggedIds.put("category",(String) each.get("categoryName"));
            taggedIds.put("code",(Integer) each.get("updated"));
            pointNameMap.put(newPointName,taggedIds);
            siteIdMap.put(newPointName,(Long) each.get("siteId"));
        }
        pointNameObj.put("pointNameList", pointNameList);
        pointNameObj.put("pointNameMap", pointNameMap);
        pointNameObj.put("siteIdMap", siteIdMap);
        return pointNameObj;
    }

    private static void updateApi(Map<String, Map<String, String>> wronglyPredictedMap) throws Exception {
        JSONObject postObj = new JSONObject();
        postObj.put("data", wronglyPredictedMap);
        postObj.put("org_id", MLServiceUtil.getOrgInfo().get("orgId"));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postURL = FacilioProperties.getAnomalyPredictAPIURL() + "/updateMetaDataV1";
        LOGGER.info("update api---"+"POSTURL---  "+postURL+"HEADERS--- "+headers+"   POSTOBJ --- "+postObj.toString());
        String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(), 300);
        if (org.apache.commons.lang.StringUtils.isEmpty(result) || result.contains("Internal Server Error")) {
            LOGGER.fatal("Error_updateMetaDataV1 api " + postURL + " ERROR MESSAGE : " + "Response is not valid. RESULT : " + result);
        }
    }
    private static Map<String, String> getSplitterForControllers(List<String> controllerList)throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        FacilioField pointNameField = fieldsMap.get("controllerName");
        FacilioField splitter = fieldsMap.get("splitter");
        List<FacilioField> requiredFields = new ArrayList<>();
        requiredFields.add(pointNameField);
        requiredFields.add(splitter);
        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(requiredFields)
                .andCondition(CriteriaAPI.getCondition(pointNameField, StringUtils.join(controllerList, ","), StringOperators.IS));

        List<Map<String, Object>> records = genericSelectBuilder.get();
        Map <String,String> controllerVssplitter  = new HashMap<>();
        for(Map<String,Object> eachRecord : records){
            controllerVssplitter.put((String) eachRecord.get("controllerName") ,(String) eachRecord.get("splitter"));
        }
        return controllerVssplitter;
    }

    public static void updateTaggedPointList(Map<String , Map<String, Map<String, Map<Long, String>>>> actualTaggedMapWithController) throws
            Exception {
        LOGGER.info("updateTaggedPointList method actualTaggedMap :" + actualTaggedMapWithController.toString());

        //Map<String, String> concatnatedPointNameMap = getConcatenatedPointMap(new ArrayList<String>(actualTaggedMap.keySet()));
        //Map<String, Map<String, Object>> predictedMap = getTaggedPointListFromPoint(new ArrayList<String>(actualTaggedMap.keySet()), null);
        List<String> controllerNameList = new ArrayList<>();
        for(Map.Entry<String , Map<String, Map<String, Map<Long, String>>>> taggedMap:actualTaggedMapWithController.entrySet()){
            controllerNameList.add(taggedMap.getKey());
        }
        //List<String> controllerNameList = new ArrayList<String>(actualTaggedMapWithController.keySet());
        Map<String, Map<String, Object>> predictedMap = getTaggedPointListV1(controllerNameList,false);
        Map<String, String> controllerVsSplitter = getSplitterForControllers(controllerNameList);
        Map<String, Map<String, Map<Long, String>>> actualTaggedMap = new HashMap<>();
        for(String controllerName : controllerNameList){
            for(Map.Entry<String, Map<String, Map<Long, String>>> taggedPointMap : actualTaggedMapWithController.get(controllerName).entrySet()){
                String pointName = taggedPointMap.getKey();
                Map<Long,String> splitterMap= new HashMap<>();
                splitterMap.put(0L,controllerVsSplitter.get(controllerName));
                actualTaggedMapWithController.get(controllerName).get(pointName).put("splitter" ,splitterMap);
                actualTaggedMap.put(controllerName+controllerVsSplitter.get(controllerName)+pointName , actualTaggedMapWithController.get(controllerName).get(pointName));
            }
        }
        Map<String, Map<String, String>> wronglyPredictedMap = new HashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> wronglyPredictedFieldMap = new HashMap<String, Map<String, String>>();
        for (Map.Entry<String, Map<String, Map<Long, String>>> taggedPointMap : actualTaggedMap.entrySet()) {
            String key = taggedPointMap.getKey();
            Map<String, Map<Long, String>> resultMap = (Map<String, Map<Long, String>>) actualTaggedMap.get(key);
            if(!predictedMap.containsKey(key)){
                continue;
            }
            Map<String, Object> predictionMap = predictedMap.get(key);
            String categoryId="-1",fieldId="-1",assetId = "-1";
            Map<String, String> updateIdMap = new HashMap<String, String>();

            if(resultMap.get("category")!= null) {
                for(Map.Entry<Long,String> taggedValues:resultMap.get("category").entrySet() ){
                    categoryId =    taggedValues.getKey().toString();
                }
                updateIdMap.put("categoryName", categoryId);
                updateIdMap.put("isUpdated","1");
            }else{continue;}
            if(resultMap.get("assetName")!= null) {
                for(Map.Entry<Long,String> taggedValues:resultMap.get("assetName").entrySet() ){
                    assetId =    taggedValues.getKey().toString();
                }
                updateIdMap.put("assetName", assetId);
                updateIdMap.put("isUpdated","2");
            }
            if(resultMap.get("reading")!= null) {
                for(Map.Entry<Long,String> taggedValues:resultMap.get("reading").entrySet() ){
                    fieldId = taggedValues.getKey().toString();
                }                updateIdMap.put("readingName", fieldId);
                updateIdMap.put("isUpdated","3");
            }
            if(!categoryId.equals("-1")&&!assetId.equals("-1")&&!fieldId.equals("-1")){
                updateIdMap.put("isUpdated","4");
            }
            wronglyPredictedFieldMap.put(key,updateIdMap);
            String predictedCatId= predictionMap.get("categoryIds").toString().replace("[","").replace("]","");
            String predictedreadId= predictionMap.get("readingIds").toString().replace("[","").replace("]","");
            String predictedassetId= predictionMap.get("assetIds").toString().replace("[","").replace("]","");

            if (!(categoryId.equalsIgnoreCase(predictedCatId)) || (!assetId.equalsIgnoreCase(predictedassetId))||(!fieldId.equalsIgnoreCase(predictedreadId))) {
                Map<String, String> updateNameMap = new HashMap<>();
                if(resultMap.get("category")!= null) {
                    updateNameMap.put("categoryName", resultMap.get("category").get(Long.parseLong(categoryId)));
                }else {updateNameMap.put("categoryName","");}
                if(resultMap.get("reading")!= null) {
                    updateNameMap.put("readingName", resultMap.get("reading").get(Long.parseLong(fieldId)));
                }else {updateNameMap.put("readingName","");}
                if(resultMap.get("assetName")!= null) {
                    updateNameMap.put("assetName", resultMap.get("assetName").get(Long.parseLong(assetId)));
                }else {updateNameMap.put("assetName","");}
                updateNameMap.put("splitter",resultMap.get("splitter").get(0l));
                wronglyPredictedMap.put(key, updateNameMap);
                if((categoryId.equalsIgnoreCase("-1")) ||(fieldId.equalsIgnoreCase("-1"))){
                    continue;
                }
            }
        }
        updateTable(wronglyPredictedFieldMap);
        updateApi(wronglyPredictedMap);
        Map<String, Object> notUpdatedPointObj = getNotUpdatedPointList();
        List<String> notUpdatedPointList = (List<String>) notUpdatedPointObj.get("pointNameList");
        if (notUpdatedPointList.size() > 0) {
            predictApi(notUpdatedPointList,null,(HashMap<String,Long>)notUpdatedPointObj.get("siteIdMap"),(Map<String,Map<String, Object>>) notUpdatedPointObj.get("pointNameMap"));
        }
    }

    public static void createMlDb(Map<Long, Map<Long, String>> categoryVsField, Map<Long, String> categoryIdVsName, List<Map<String, Object>> selectedRecords, Long orgId) throws Exception {
        JSONObject postObj = new JSONObject();
        postObj.put("readings", categoryVsField);
        postObj.put("org_id", orgId);
        postObj.put("cat_dict", categoryIdVsName);
        //postObj.put("cat_map",categoryIdVsName);
        postObj.put("asset_fields", selectedRecords);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postURL = FacilioProperties.getAnomalyPredictAPIURL() + "/createDb";
        LOGGER.info("create db api---"+"POSTURL---  "+postURL+"HEADERS--- "+headers+"   POSTOBJ --- "+postObj.toString());
        String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(), 300);
        if (org.apache.commons.lang.StringUtils.isEmpty(result) || result.contains("Internal Server Error")) {
            LOGGER.info("Error createDb api " + postURL + " ERROR MESSAGE : " + "Response is not valid. RESULT : " + result);
            throw new Exception("failed during Python - create Db");
        }
    }
}




