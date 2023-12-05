package com.facilio.multiImport.command;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.activity.ActivityType;
import com.facilio.bmsconsole.activity.ImportActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class SetActivityMessageForImportRecordsCommand extends FacilioCommand {
    private boolean isUpdateMode;
    private String activityContext;
    private List<FacilioField> lookupFields;
    private Map<String, FacilioField> primaryFieldMap;
    private Context context;
    public SetActivityMessageForImportRecordsCommand(boolean isUpdateMode,String activityModuleName){
        this.isUpdateMode = isUpdateMode;
        this.activityContext = activityModuleName;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        this.context=context;
        String moduleName = Constants.getModuleName(context);
        FacilioModule module = Constants.getModBean().getModule(moduleName);

        if (StringUtils.isEmpty(this.activityContext)) {
            if (module.isCustom()) {        // only for custom module, we should add this activity type
                activityContext = FacilioConstants.ContextNames.CUSTOM_ACTIVITY;
            }
        }
        Constants.setActivityContext(context, activityContext);

        if (StringUtils.isEmpty(activityContext)) { // No activity context found
            return false;
        }

        ActivityType activityType = isUpdateMode ? ImportActivityType.UPDATE_RECORD : ImportActivityType.ADD_RECORD;

        addActivitiesToContext(moduleName,activityType,context);


        return false;
    }
    private void addActivitiesToContext(String moduleName, ActivityType activityType,
                                          Context context) throws Exception {

        Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> recordMap = isUpdateMode?ImportConstants.getUpdateRecordMap(context):ImportConstants.getInsertRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> recordList = recordMap.get(moduleName);
        if(CollectionUtils.isEmpty(recordList)){
            return;
        }

        Map<Long, ImportRowContext> logIdVsRowContext = ImportConstants.getLogIdVsRowContextMap(context);
        Map<Long, ModuleBaseWithCustomFields> oldRecordMap = ImportConstants.getOldRecordsMap(context);

        ImportDataDetails importDataDetails = (ImportDataDetails)context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        User user = userBean.getUserInternal(importDataDetails.getCreatedBy());

        if (isUpdateMode){
            loadPrimaryFieldMap();
        }

        for (Pair<Long,ModuleBaseWithCustomFields> pair : recordList) {

            Long logId = pair.getKey();
            ModuleBaseWithCustomFields record = pair.getValue();
            ImportRowContext rowContext = logIdVsRowContext.get(logId);
            if(rowContext.isErrorOccurredRow()){
                continue;
            }

            JSONObject info = new JSONObject();

            if(isUpdateMode){
                List<UpdateChangeSet> changeSets = Constants.getRecordChangeSets(context, record.getId());
                if(CollectionUtils.isEmpty(changeSets)){
                    continue;
                }
                ModuleBaseWithCustomFields oldRecord = oldRecordMap.get(logId);
                info.put(FacilioConstants.ContextNames.IMPORT_ID,importDataDetails.getId());

                List<Object> changeList = new ArrayList<>();
                for (UpdateChangeSet changeSet : changeSets) {
                    long fieldId = changeSet.getFieldId();
                    Object oldValue = changeSet.getOldValue();
                    Object newValue = changeSet.getNewValue();
                    FacilioField field = Constants.getModBean().getField(fieldId, moduleName);

                    if ((oldValue == null && newValue == null) || field ==null) {
                        continue;
                    }

                    JSONObject changeObj = new JSONObject();
                    changeObj.put("field", field.getName());
                    changeObj.put("displayName", field.getDisplayName());

                    if (field instanceof LookupField && oldValue != null) {
                        long recId = (long) oldValue;
                        info.put("oldRecordId", recId);

                        LookupField lookupField = (LookupField) field;
                        Object oldLookupRecordObject = FieldUtil.getValue(oldRecord,lookupField);
                        try{
                            oldValue = getPrimaryValue(lookupField,oldLookupRecordObject);
                        }catch (UnsupportedOperationException e){
                            LOGGER.info("PrimaryValueException:"+e.getMessage());
                            continue;
                        }

                    }
                    changeObj.put("oldValue", oldValue);

                    if(field instanceof LookupField && newValue!=null){
                        long recId = (long) newValue;
                        info.put("recordId", recId);

                        LookupField lookupField = (LookupField) field;
                        Object lookupRecordObject = FieldUtil.getValue(record,lookupField);
                        try {
                            newValue = getPrimaryValue(lookupField,lookupRecordObject);
                        }catch (UnsupportedOperationException e){
                            LOGGER.info("PrimaryValueException:"+e.getMessage());
                            continue;
                        }

                    }
                    changeObj.put("newValue", newValue);

                    changeList.add(changeObj);
                }
                info.put("changeSet", changeList);
            } else {
                info.put(FacilioConstants.ContextNames.IMPORT_ID,importDataDetails.getId());
            }

            CommonCommandUtil.addActivityToContext(record.getId(), -1, activityType, info, (FacilioContext) context,user);
        }
    }
    private  Object getPrimaryValue(LookupField lookupField,Object lookupRecordObject){
        if(lookupRecordObject instanceof ModuleBaseWithCustomFields){
            ModuleBaseWithCustomFields lookupRecord=(ModuleBaseWithCustomFields)lookupRecordObject;;
            return getMainFieldProperty(lookupRecord,primaryFieldMap.get(lookupField.getName()));
        } else if (lookupRecordObject instanceof User) {
           return ((User) lookupRecordObject).getName();
        } else if (lookupRecordObject instanceof Role) {
            return ((Role) lookupRecordObject).getName();
        }else {
            throw new UnsupportedOperationException("Un supported lookup field data bean for:"+lookupField.getName());
        }
    }
    public static Object getMainFieldProperty(ModuleBaseWithCustomFields lookupRecord, FacilioField primaryField) {
        if (lookupRecord == null || primaryField == null) {
            return null;
        }
        Object property;
        try {
            property = FieldUtil.getProperty(lookupRecord, primaryField.getName());
            if (primaryField instanceof LookupField && ((LookupField) primaryField).getSpecialType() == null) {
                FacilioField childMainField = Constants.getModBean().getPrimaryField(((LookupField) primaryField).getLookupModule().getName());
                if (childMainField != null) {
                    property = getMainFieldProperty((ModuleBaseWithCustomFields) property, childMainField);
                }
            }
        } catch (Exception e) {
            property = lookupRecord.getId();
        }
        return property;
    }
    private void loadPrimaryFieldMap() throws Exception{
        primaryFieldMap = new HashMap<>();
        lookupFields = new ArrayList<>();
        List<FacilioField> fields = (List<FacilioField>) context.get(Constants.PATCH_FIELDS);

        if(CollectionUtils.isEmpty(fields)){
            return;
        }
        lookupFields = fields.stream().filter(field->field.getDataTypeEnum()== FieldType.LOOKUP).collect(Collectors.toList());

        for (FacilioField field : lookupFields) {
            LookupField lookupField = (LookupField) field;
            String name = lookupField.getLookupModule().getName();
            FacilioField primaryField = Constants.getModBean().getPrimaryField(name);
            if (primaryField != null) {
                primaryFieldMap.put(field.getName(), primaryField);
            }
        }
    }

}
