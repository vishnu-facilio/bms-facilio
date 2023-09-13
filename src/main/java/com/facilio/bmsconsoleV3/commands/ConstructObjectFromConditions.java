package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;

import com.facilio.db.criteria.operators.UserOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class ConstructObjectFromConditions extends FacilioCommand {

    private static final int minSize = 10;

    private  JSONArray conditions=new JSONArray();
    @Override
    public boolean executeCommand(Context context) throws Exception {
       String moduleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
       List<Condition> conditionList= (List<Condition>) context.getOrDefault(FacilioConstants.ContextNames.CONDITIONS,null);
        if(CollectionUtils.isNotEmpty(conditionList)) {
            for (Condition condition : conditionList) {
                conditions.add(constructCondition(moduleName, condition));
            }
        }
        context.put(FacilioConstants.ContextNames.CONDITIONS,conditions);
        return false;
    }

    public static JSONObject constructCondition(String moduleName, Condition condition) throws Exception{
        JSONObject conditionObj=new JSONObject();
        String[] fieldNameArray=condition.getFieldName().split("\\.");;
        String fieldName;
        if(fieldNameArray.length>1){
            fieldName=fieldNameArray[1];
        }else{
            fieldName=fieldNameArray[0];
        }

        conditionObj.put("fieldName",fieldName);
        conditionObj.put("operatorId",condition.getOperatorId());
        conditionObj.put("operator",condition.getOperator().getOperator());


        if(condition.getCriteriaValue()!=null){

            FacilioField field= Constants.getModBean().getField(fieldName,fieldNameArray[0]);
            String lookupModuleName=((LookupField)field).getLookupModule().getName();

            conditionObj.put("displayName",field.getDisplayName());
            conditionObj.put("operatorId",condition.getOperatorId());
            conditionObj.put("lookupModuleName",lookupModuleName);

            Criteria criteriaValue=condition.getCriteriaValue();
            conditionObj.put("CriteriaValue",constructCondition(lookupModuleName,criteriaValue.getConditions().get("1")));


        }else if(condition.getValue()!=null){


            FacilioField field= Constants.getModBean().getField(fieldName,moduleName);
            conditionObj.put("displayName",field.getDisplayName());

            if(field instanceof LookupField || field instanceof MultiLookupField) {
                String lookupModuleName;

                  if(field instanceof LookupField){
                      lookupModuleName=((LookupField)field).getLookupModule().getName();
                  }else {
                      lookupModuleName=((MultiLookupField)field).getLookupModule().getName();
                  }

                if(LookupSpecialTypeUtil.isSpecialType(lookupModuleName)){

                    handlingForSpecialModules(lookupModuleName,condition,conditionObj,field);

                } else {

                    List<Long> totalRecordIds = convertStringToList(condition.getValue());
                    List<Long> recordIdList = getLimitedIds(totalRecordIds);

                    if(totalRecordIds.size()> minSize) {
                        conditionObj.put("totalValues", totalRecordIds);
                    }

                    List records =RecordAPI.getRecords(lookupModuleName,recordIdList);
                    if(CollectionUtils.isNotEmpty(records)) {

                        boolean isResource = moduleName.equals(FacilioConstants.ContextNames.RESOURCE);
                        List<Map<String,Object>> recorMap=FieldUtil.getAsMapList(records,FacilioConstants.ContextNames.getClassFromModuleName(lookupModuleName));
                        List<FieldOption<Long>> fieldOptions = RecordAPI.constructFieldOptionsFromRecords(recorMap, Constants.getModBean().getPrimaryField(lookupModuleName), null, null, isResource);
                        conditionObj.put("value", FieldUtil.getAsMapList(fieldOptions, FieldOption.class));

                    }

                }
            }
            else if(field instanceof EnumField){

                List<EnumFieldValue<Integer>> enumValues= ((SystemEnumField) field).getValues();

                List<Integer> valueList= Stream.of(condition.getValue().split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());

                List<EnumFieldValue<Integer>> filteredItems = enumValues.stream()
                .filter(i -> valueList.contains(i.getIndex()))
                .collect(Collectors.toList());

                conditionObj.put("value",FieldUtil.getAsMapList(filteredItems,EnumFieldValue.class));
            }
            else if(field.getName().equals("siteId")){

                List<Long> totolRecordIds=convertStringToList(condition.getValue());
                List<Long> recordIds = getLimitedIds(totolRecordIds);

                if(totolRecordIds.size()> minSize) {

                    conditionObj.put("totalValues",totolRecordIds);

                }
                List records = RecordAPI.getRecords(FacilioConstants.ContextNames.SITE,recordIds);
                if(CollectionUtils.isNotEmpty(records)){

                List<Map<String,Object>> recordMap=FieldUtil.getAsMapList(records,FacilioConstants.ContextNames.getClassFromModuleName(FacilioConstants.ContextNames.SITE));
                List<FieldOption<Long>> fieldOptions= RecordAPI.constructFieldOptionsFromRecords(recordMap, Constants.getModBean().getPrimaryField(FacilioConstants.ContextNames.SITE), null, null, false);
                conditionObj.put("value", FieldUtil.getAsMapList(fieldOptions, FieldOption.class));

                }
            }
            else{
                    conditionObj.put("value", Stream.of(condition.getValue().split(","))
                            .map(String::trim)
                            .collect(Collectors.toList()));
            }
        }
        return conditionObj;

    }
    public static void handlingForSpecialModules(String specialModule,Condition condition,JSONObject conditionObj,FacilioField field) throws Exception {

        if(condition.getValue()==null) {
            return;
        }
        List<Long> recordIds=new ArrayList<>();
        Map<String,Object> currentUser=new HashMap<>();

        if(FacilioConstants.ContextNames.USERS.equals(specialModule)) {

            List<String> stringList=new ArrayList<String>(Arrays.asList(condition.getValue().trim().split(",")));
            if(condition.getValue().contains(FacilioConstants.Criteria.LOGGED_IN_USER)){

                currentUser.put("value",FacilioConstants.Criteria.LOGGED_IN_USER);
                stringList.remove(FacilioConstants.Criteria.LOGGED_IN_USER);

            }

            List<Long> totalRecordIds=stringList.stream().map(Long::valueOf).sorted().collect(Collectors.toList());
            recordIds = getLimitedIds(totalRecordIds);
            List userList = null;

            if(condition.getOperator() instanceof UserOperators){

                List<Role>   records=AccountUtil.getRoleBean().getRoles(recordIds);

                if(CollectionUtils.isNotEmpty(records)) {
                    userList = records.stream()
                            .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                            .collect(Collectors.toList());
                }

            }
            else {

                List<User> records =AccountUtil.getUserBean().getUsers(recordIds,false);
                if(CollectionUtils.isNotEmpty(records)) {
                    userList = records.stream()
                            .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                            .collect(Collectors.toList());
                }

            }

            if(totalRecordIds.size()> minSize) {
                conditionObj.put("totalValues",totalRecordIds);
            }
            if(CollectionUtils.isNotEmpty(userList)) {

                List<Map<String, Object>> usersMap = FieldUtil.getAsMapList(userList, FieldOption.class);
                if (MapUtils.isNotEmpty(currentUser)) {
                    usersMap.add(currentUser);
                }
                conditionObj.put("value", usersMap);

            }

        }else if(FacilioConstants.ContextNames.GROUPS.equals(specialModule)){

            List<FieldOption<Long>> groups = null;
            List<Long> totalRecordsIds=convertStringToList(condition.getValue());
           recordIds= getLimitedIds(totalRecordsIds);

            if(totalRecordsIds.size()> minSize) {
                conditionObj.put("totalValues",totalRecordsIds);
            }

           List<Group> records=AccountUtil.getGroupBean().getGroups(recordIds);

            if (CollectionUtils.isNotEmpty(records)) {

            groups= records.stream()
                            .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                            .collect(Collectors.toList());

                conditionObj.put("value", FieldUtil.getAsMapList(groups, FieldOption.class));
            }

        }else if(FacilioConstants.ContextNames.REQUESTER.equals(specialModule)) {

            List<FieldOption<Long>> requesters = null;
            List<User> allUsers = null;
            List<Long> totalRecordsIds=convertStringToList(condition.getValue());
            long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);

            if(appId > 0) {

                allUsers = ApplicationApi.getRequesterList(AccountUtil.getCurrentOrg().getOrgId(), appId, null);
                if(CollectionUtils.isNotEmpty(allUsers)) {
                    List<Long> allSelectedUserIds = allUsers.stream().filter(i -> totalRecordsIds.contains(i.getOuid())).map(i -> i.getOuid()).collect(Collectors.toList());

                    List<User> users = new LinkedList<>();
                    if (allSelectedUserIds.size() > minSize) {

                        List<Long> selectedIds = allSelectedUserIds.subList(0, minSize);
                        users = allUsers.stream().filter(i -> selectedIds.contains(i.getOuid())).collect(Collectors.toList());
                        conditionObj.put("totalValues", allSelectedUserIds);

                    } else {
                        users = allUsers.stream().filter(i -> allSelectedUserIds.contains(i.getOuid())).collect(Collectors.toList());
                    }

                    if (CollectionUtils.isNotEmpty(users)) {
                        requesters = users.stream()
                                .map(usr -> new FieldOption<>(usr.getId(), usr.getName()))
                                .collect(Collectors.toList());
                        conditionObj.put("value", FieldUtil.getAsMapList(requesters, FieldOption.class));
                    }
                }

            }
        }
    }

    public static List<Long> convertStringToList(String ids){

            List<Long> recordIds = Stream.of(ids.split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .sorted()
                    .collect(Collectors.toList());
            return recordIds;
    }
    public static List<Long> getLimitedIds(List<Long> ids){
            if (ids.size() <= minSize) {
                return ids;
            } else {
                return ids.subList(0, minSize);
            }
    }
}
