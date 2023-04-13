package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.PeopleTypeField;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;

import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class getPeopleFromRecordFieldsCommand extends FacilioCommand {
    // fetching user type fields should be removed after user deprecation

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        if(moduleName == null){
            throw new IllegalArgumentException("Module Name cannot be empty");
        }
        if(recordId == null){
            throw new IllegalArgumentException("Record Id should not be empty");
        }
        List<PeopleTypeField> peopleFieldsPicklist = getPeopleFieldsPicklistMap(moduleName, recordId);
        context.put(FacilioConstants.ContextNames.DATA,peopleFieldsPicklist);

        return false;
    }

    private static List<PeopleTypeField> getPeopleFieldsPicklistMap(String moduleName, Long recordId) throws Exception {
        HashMap<String, ArrayList<FacilioField>> fieldsWithTypeMap = getFieldsWithTypeMap(moduleName);
        if(fieldsWithTypeMap == null || fieldsWithTypeMap.isEmpty()){
            return null;
        }
        return constructPeopleFieldsPickList(moduleName, recordId, fieldsWithTypeMap);

    }
    private static HashMap<String, ArrayList<FacilioField>> getFieldsWithTypeMap(String moduleName) throws Exception {
        HashMap<String, ArrayList<FacilioField>> fieldsWithTypeMap = new HashMap<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        if(allFields == null || allFields.isEmpty()){
            return null;
        }
        for (FacilioField field : allFields) {
            if (field.getDataTypeEnum() == FieldType.LOOKUP) {
                LookupField lookupField = (LookupField) field;
                FacilioModule lookupModule = lookupField.getLookupModule();
                if(lookupModule != null){
                    if ( (lookupModule.getName().equals(FacilioConstants.ContextNames.PEOPLE)) || (lookupModule.getExtendModule()!= null && lookupModule.getExtendModule().getName().equals(FacilioConstants.ContextNames.PEOPLE))) {
                        if(fieldsWithTypeMap.containsKey(FacilioConstants.ContextNames.PEOPLE)){
                            fieldsWithTypeMap.get(FacilioConstants.ContextNames.PEOPLE).add(field);
                        } else {
                            fieldsWithTypeMap.put(FacilioConstants.ContextNames.PEOPLE,new ArrayList<>(Arrays.asList(field)));
                        }
                    }
                } if ((lookupModule != null && lookupModule.getName().equals(FacilioConstants.ContextNames.USERS)) ||(lookupField.getSpecialType() != null && (lookupField.getSpecialType().equals(FacilioConstants.ContextNames.USERS)  || lookupField.getSpecialType().equals(FacilioConstants.ContextNames.REQUESTER)))) {
                    if(fieldsWithTypeMap.containsKey(FacilioConstants.ContextNames.USERS)){
                        fieldsWithTypeMap.get(FacilioConstants.ContextNames.USERS).add(field);
                    } else {
                        fieldsWithTypeMap.put(FacilioConstants.ContextNames.USERS,new ArrayList<>(Arrays.asList(field)));
                    }
                }
            }
        }
        return fieldsWithTypeMap;
    }
    private static List<PeopleTypeField> constructPeopleFieldsPickList(String moduleName, Long recordId, HashMap<String, ArrayList<FacilioField>> fieldsWithTypeMap) throws Exception {
        if(fieldsWithTypeMap.isEmpty()){
            return null;
        }
        Map<Long, PeopleTypeField> peopleFieldsPicklist = new HashMap<>();
        ArrayList<FacilioField> peopleTypeFields = fieldsWithTypeMap.get(FacilioConstants.ContextNames.PEOPLE);
        List<SupplementRecord> supplementsToFetch = null;
        if(peopleTypeFields != null && !peopleTypeFields.isEmpty()){
            supplementsToFetch = peopleTypeFields.stream().map(a -> (LookupField) a).collect(Collectors.toList());
        }
        Map<String, Object> recordProp = FieldUtil.getAsProperties(V3RecordAPI.getRecordsListWithSupplements(moduleName, Arrays.asList(recordId), null, supplementsToFetch).get(0));
        if(recordProp.isEmpty()){
            return null;
        }
        if(fieldsWithTypeMap.containsKey(FacilioConstants.ContextNames.PEOPLE)){
            ArrayList<FacilioField> fields = fieldsWithTypeMap.get(FacilioConstants.ContextNames.PEOPLE);
            for (FacilioField field: fields) {
                if(!recordProp.containsKey(field.getName())){
                    continue;
                }
                V3PeopleContext peopleTypeRecord = FieldUtil.getAsBeanFromMap((Map<String, Object>) recordProp.get(field.getName()),V3PeopleContext.class);
                if(peopleFieldsPicklist.containsKey(peopleTypeRecord.getId())){
                    PeopleTypeField ppl = peopleFieldsPicklist.get(peopleTypeRecord.getId());
                    List<String> fieldsList = ppl.getFields();
                    if(fieldsList == null || fieldsList.isEmpty()){
                        ppl.setFields(Arrays.asList(field.getDisplayName()));
                    } else {
                        ArrayList<String> fieldNames = (ArrayList<String>) ppl.getFields();
                        fieldNames.add(field.getDisplayName());
                    }
                } else {
                    List<String> list = Arrays.asList(field.getDisplayName());
                    PeopleTypeField ppl = new PeopleTypeField(peopleTypeRecord.getId(), peopleTypeRecord.getName(),list );
                    peopleFieldsPicklist.put(peopleTypeRecord.getId(),ppl);
                }
            }
        }
        if(fieldsWithTypeMap.containsKey(FacilioConstants.ContextNames.USERS)){
            ArrayList<FacilioField> fields = fieldsWithTypeMap.get(FacilioConstants.ContextNames.USERS);
            Map<Long, ArrayList<String>> userFieldNameMap = new HashMap<>();
            for (FacilioField field: fields){
                if(!recordProp.containsKey(field.getName())){
                    continue;
                }
                User user = FieldUtil.getAsBeanFromMap((Map<String, Object>) recordProp.get(field.getName()),User.class);
                if(userFieldNameMap.containsKey(user.getOuid())){
                    userFieldNameMap.get(user.getOuid()).add(field.getDisplayName());
                }else {
                    userFieldNameMap.put(user.getOuid(),new ArrayList<>(Arrays.asList(field.getDisplayName())));
                }
            }
            List<Long> ouIds = new ArrayList<>(userFieldNameMap.keySet());
            Map<Long, Map<String, Object>> ouIdWithPeopleMap = PeopleAPI.getPeopleNameForUserIds(ouIds);
            if (ouIds == null || ouIds.isEmpty()){
                return null;
            }
            for (Long ouId:ouIds) {
                ArrayList<String> fieldsForUsers = userFieldNameMap.get(ouId);
                Map<String, Object> ppl = ouIdWithPeopleMap.get(ouId);
                PeopleTypeField peopleOption = new PeopleTypeField((Long) ppl.get("id"),(String) ppl.get("name"), fieldsForUsers);
                peopleFieldsPicklist.put((Long) ppl.get("id"),peopleOption);
            }
        }
        List<PeopleTypeField> peopleList = new ArrayList<>(peopleFieldsPicklist.values());
        return peopleList;
    }




}
