package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    private static String getSplitter(HashMap<String, String> splitterMap, Map<String, Object> infoMap) {
        String agentNameKey = "splitter_" + (String) infoMap.get("agentName");
        String agentTypeKey = "splitter_" + (String) infoMap.get("agentType");
        try {
            if (splitterMap.containsKey(agentNameKey)) {
                return splitterMap.get(agentNameKey);
            }
            if (splitterMap.containsKey(agentTypeKey)) {
                return splitterMap.get(agentTypeKey);
            }
            return splitterMap.get("splitter_org");
        } catch (Exception e) {
            LOGGER.info("OrgInfo table not have a given key", e);
            throw e;
        }
    }

    public static List<String> convertToList(List<HashMap<String, Object>> pointsMapList, HashMap<String, String> splitterMap) {
        List<String> newPointNameList = new ArrayList<>();
        for (Map<String, Object> pointMap : pointsMapList) {
            String controllerName = (String) pointMap.get("controller");
            List<String> pointNameList = (List<String>) pointMap.get("pointName");
            String splitter = getSplitter(splitterMap, pointMap);
            for (String pointName : pointNameList) {
                newPointNameList.add("splitter#" + splitter + "#" + controllerName + splitter + pointName);
            }
        }
        return newPointNameList;
    }

    public static void tagPointListV1(List<HashMap<String, Object>> pointsMapList) throws Exception {
        LOGGER.info("tagPointListV1 pointsMapList to Tag :" + pointsMapList.toString());
        HashMap<String, String> splitterMap = BmsPointsTaggingUtil.getSplitterMap();
        List<String> newPointNameList = convertToList(pointsMapList,
                splitterMap);
        predictApi(newPointNameList,pointsMapList,null);
        //tagPointList(resultMap);
        //tagPointList(pointNameList, pointsMapList, null);
    }

    private static void predictApi(List<String> pointNameList,List<HashMap<String, Object>>pointsMapList ,Map<String,String>pointsMap ) throws Exception {
        LOGGER.info("predictApi method pointNameList :" + pointNameList.toString());
        JSONObject postObj = new JSONObject();
        postObj.put("data", pointNameList);
        postObj.put("pointsMapList", pointsMapList);
        postObj.put("pointsMap", pointsMap);
        //postObj.put("splitter",splitter);
        postObj.put("org_id", MLServiceUtil.getOrgInfo().get("orgId"));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postURL = FacilioProperties.getAnomalyPredictAPIURL() + "/getMetaDataV1";
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
        List<String> pointNameList = (List<String>) resultMap.get("metaData").get("pointNameList");
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
                .andCondition(CriteriaAPI.getCondition(updatedField, String.valueOf(true), BooleanOperators.IS));
        if (genericSelectBuilder.get().size() > 0) {
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
            for (String pointName : pointNameList) {
                String newPointName = pointName.replace(pointName.substring(0, 11), "");
                String clusterName = newPointName.replaceAll("[0-9]", "");
                if (clusterMap.containsKey(clusterName)) {
                    resultMap.get(newPointName).put("categoryName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("categoryName").split(","))));
                    resultMap.get(newPointName).put("readingName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("readingName").split(","))));
                    //resultMap.get(newPointName).put("assetName", new ArrayList<String>(Arrays.asList(clusterMap.get(clusterName).get("assetName").split(","))));
                }
            }
        }
        //Map<String, Map<String, Object>> newResultMap = new HashMap<>();
        for(String key:resultMap.keySet()){
            String splitter  = resultMap.get(key).get("splitter").toString();
            String controllerName  = key.split(splitter)[0];
            resultMap.get(key).put("controllerName",controllerName);
            //newResultMap.put(key.replaceFirst(controllerName+splitter,"") , resultMap.get(key));
        }
        insertRecords(resultMap, module, fields);
    }


    public static void updateRecords(Map<String, Map<String, Object>> resultMap, FacilioModule
            module, List<FacilioField> fields,long currentTime) throws Exception {
        List<String> keyList = new ArrayList<>(resultMap.keySet());
        for (String key : resultMap.keySet()) {

            Map<String, Object> row = new HashMap<>();
            row.put("categoryName", String.join(",", (List<String>) resultMap.get(key).get("categoryName")));
            row.put("readingName", String.join(",", (List<String>) resultMap.get(key).get("readingName")));
            row.put("assetName", String.join(",", (List<String>) resultMap.get(key).get("assetName")));
            row.put("modifiedTime",currentTime);

            GenericUpdateRecordBuilder builder1 = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition("POINT_NAME", "pointName", key, StringOperators.IS));
            builder1.update(row);
        }
    }

    public static void insertRecords(Map<String, Map<String, Object>> resultMap, FacilioModule
            module, List<FacilioField> fields) throws Exception {
        List<String> keyList = new ArrayList<>(resultMap.keySet());
        Map<String, Map<String, Object>> updatePointMap = getTaggedPointListFromPoint(keyList, null);
        for (String key : updatePointMap.keySet()) {
            keyList.remove(key);
            updatePointMap.put(key, resultMap.remove(key));
        }
        long currentTime = System.currentTimeMillis();
        updateRecords(updatePointMap, module, fields,currentTime);
        for (String key : keyList) {
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
            row.put("updated", false);
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

    public static void updateTable
            (Map<String, Map<String, String>> actualTaggedMap, Map<String, Map<String, Object>> map) throws
            Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule("mlBmsPointsTagging");
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        List<String> updatedClusterNames = new ArrayList<>();
        Long modifiedTime = System.currentTimeMillis();
        for (String key : actualTaggedMap.keySet()) {
            //String clusterName = ((String) map.get(key).get("newPointName")).replaceAll("[0-9]", "");
            String clusterName = key.replaceAll("[0-9]", "");
            Map<String, Object> assetNameMap = new HashMap<>();
            assetNameMap.put("assetName", actualTaggedMap.get(key).get("assetName"));
            assetNameMap.put("updated", true);
            GenericUpdateRecordBuilder builder1 = new GenericUpdateRecordBuilder()
                    .table(module.getTableName())
                    .fields(fields)
                    .andCondition(CriteriaAPI.getCondition("POINT_NAME", "pointName", key, StringOperators.IS));
            builder1.update(assetNameMap);
            if (!updatedClusterNames.contains(clusterName)) {
                Map<String, Object> row = new HashMap<String, Object>();
                row.put("categoryName", actualTaggedMap.get(key).get("categoryName"));
                row.put("readingName", actualTaggedMap.get(key).get("readingName"));
                row.put("modifiedTime", modifiedTime);
                GenericUpdateRecordBuilder builder2 = new GenericUpdateRecordBuilder()
                        .table(module.getTableName())
                        .fields(fields)
                        .andCondition(CriteriaAPI.getCondition("CLUSTER_NAME", "clusterName", clusterName, StringOperators.IS));
                builder2.update(row);
                updatedClusterNames.add(clusterName);
            }
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
        List<FacilioField> selectedFields = new ArrayList<>();
        selectedFields.add(pointNameField);
        selectedFields.add(splitterField);
        selectedFields.add(controllerField);
        GenericSelectRecordBuilder genericSelectBuilder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(selectedFields)
                .andCondition(CriteriaAPI.getCondition(updatedField, String.valueOf(false), BooleanOperators.IS));
        genericSelectBuilder.get();
        List<Map<String, Object>> records = genericSelectBuilder.get();
        List<String> pointNameList = new ArrayList<>();
        Map<String, Object> pointNameObj = new HashMap<>();
        Map<String, String> pointNameMap = new HashMap<>();
        for (Map<String, Object> each : records) {
            pointNameList.add("splitter#" + (String) each.get("splitter") + "#" + (String) each.get("pointName"));
            pointNameMap.put((String) each.get("controllerName") + (String) each.get("splitter") + (String) each.get("pointName"), (String) each.get("pointName"));
        }
        pointNameObj.put("pointNameList", pointNameList);
        pointNameObj.put("pointNameMap", pointNameMap);
        return pointNameObj;
    }

    private static void updateApi(Map<String, Map<String, String>> wronglyPredictedMap) throws Exception {
        JSONObject postObj = new JSONObject();
        postObj.put("data", wronglyPredictedMap);
        postObj.put("org_id", MLServiceUtil.getOrgInfo().get("orgId"));
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        String postURL = FacilioProperties.getAnomalyPredictAPIURL() + "/updateMetaDataV1";
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
        List<String> controllerNameList = new ArrayList<String>(actualTaggedMapWithController.keySet());
        Map<String, Map<String, Object>> predictedMap = getTaggedPointListV1(controllerNameList,false);
        Map<String, String> controllerVsSplitter = getSplitterForControllers(controllerNameList);
        Map<String, Map<String, Map<Long, String>>> actualTaggedMap = new HashMap<>();
        for(String controllerName : controllerNameList){
            for(String pointName : actualTaggedMapWithController.get(controllerName).keySet()){
                Map<Long,String> splitterMap= new HashMap<>();
                splitterMap.put(0L,controllerVsSplitter.get(controllerName));
                actualTaggedMapWithController.get(controllerName).get(pointName).put("splitter" ,splitterMap);
                actualTaggedMap.put(controllerName+controllerVsSplitter.get(controllerName)+pointName , actualTaggedMapWithController.get(controllerName).get(pointName));
            }
        }
        Map<String, Map<String, String>> wronglyPredictedMap = new HashMap<String, Map<String, String>>();
        Map<String, Map<String, String>> wronglyPredictedFieldMap = new HashMap<String, Map<String, String>>();
        for (String key : actualTaggedMap.keySet()) {
            Map<String, Map<Long, String>> resultMap = (Map<String, Map<Long, String>>) actualTaggedMap.get(key);
            if(!predictedMap.containsKey(key)){
                continue;
            }
            Map<String, Object> predictionMap = predictedMap.get(key);
            String categoryId="-1",fieldId="-1",assetId = "-1";
            if(resultMap.get("category")!= null) {
                categoryId = resultMap.get("category").keySet().toArray()[0].toString();
            } if(resultMap.get("reading")!= null) {
                fieldId = resultMap.get("reading").keySet().toArray()[0].toString();
            }if(resultMap.get("assetName")!= null) {
                assetId = resultMap.get("assetName").keySet().toArray()[0].toString();
            }
            String predictedCatId= predictionMap.get("categoryIds").toString().replace("[","").replace("]","");
            String predictedreadId= predictionMap.get("readingIds").toString().replace("[","").replace("]","");
            String predictedassetId= predictionMap.get("assetIds").toString().replace("[","").replace("]","");


            if (!(categoryId.equalsIgnoreCase(predictedCatId)) || (!assetId.equalsIgnoreCase(predictedassetId))||(!fieldId.equalsIgnoreCase(predictedreadId))) {
                Map<String, String> updateIdMap = new HashMap<String, String>();
                Map<String, String> updateNameMap = new HashMap<>();
                updateIdMap.put("categoryName", categoryId);
                updateIdMap.put("readingName", fieldId);
                updateIdMap.put("assetName", assetId);
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
                wronglyPredictedFieldMap.put(key, updateIdMap);
            }
        }
        updateTable(wronglyPredictedFieldMap, predictedMap);
        updateApi(wronglyPredictedMap);
        Map<String, Object> notUpdatedPointObj = getNotUpdatedPointList();
        List<String> notUpdatedPointList = (List<String>) notUpdatedPointObj.get("pointNameList");
        if (notUpdatedPointList.size() > 0) {
            predictApi(notUpdatedPointList,null,(Map<String, String>) notUpdatedPointObj.get("pointNameMap"));
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
        String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(), 300);
        if (org.apache.commons.lang.StringUtils.isEmpty(result) || result.contains("Internal Server Error")) {
            LOGGER.info("Error createDb api " + postURL + " ERROR MESSAGE : " + "Response is not valid. RESULT : " + result);
            throw new Exception("failed during Python - create Db");
        }
    }
}




