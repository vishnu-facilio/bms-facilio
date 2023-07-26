package com.facilio.bmsconsole.commands;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;


public class LookupFilterFieldListCommand extends FacilioCommand {

    private Map<String, Object> oneLevelFields = new LinkedHashMap<>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isOneLevelFields= (boolean) context.getOrDefault(FacilioConstants.ContextNames.IS_ONE_LEVEL_FIELDS,false);
        if(!isOneLevelFields) {
            return false;
        }
        List<Map<String, Object>> moduleFields = new ArrayList<>();
        Map<String, Object> lookupModuleFields = new HashMap<>();
        oneLevelFields.put("moduleFields", moduleFields);
        oneLevelFields.put("lookupModuleFields", lookupModuleFields);
        handleFields(context,true);
        context.put(FacilioConstants.ContextNames.FIELDS, oneLevelFields);

        return false;
    }
    private List<FacilioField> getFieldsFromFilterFields(List<FilterFieldContext> filterFields) {
        return filterFields.stream().map(FilterFieldContext::getField).collect(Collectors.toList());
    }
    private void handleFields(Context context,Boolean isModuleFields) throws Exception
    {
        List<FilterFieldContext> filterFields = (List<FilterFieldContext>) context.get(FacilioConstants.Filters.FILTER_FIELDS);
        List<Long> defaultFieldIds = (List<Long>) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD_IDS);
        List<FacilioField> fields = getFieldsFromFilterFields(filterFields);
        List<Map<String, Object>> moduleFieldsList= (List<Map<String, Object>>) oneLevelFields.get("moduleFields");
        Map lookupModuleMap = (Map) oneLevelFields.get("lookupModuleFields");
        List<Map<String, Object>> lookupModuleFieldsList = new ArrayList<>();
        for(FacilioField field : fields) {
            Map<String, Object> fieldMap = createPlaceHolder(field);
            if(isModuleFields){
                if (field instanceof LookupField && field.getName()!="siteId" && ((LookupField) field).getSpecialType()==null) {
                    FacilioModule lookupModule = ((LookupField)field).getLookupModule();
                    String lookupModuleName = lookupModule.getName();
                    handleLookupFields(lookupModuleName, defaultFieldIds);
                }
                moduleFieldsList.add(fieldMap);
            }
            else{
                lookupModuleFieldsList.add(fieldMap);
            }
        }
        if(!isModuleFields) {
            lookupModuleMap.put(context.get(FacilioConstants.ContextNames.MODULE_NAME), lookupModuleFieldsList);
        }

    }
    private void handleLookupFields(String lookupModuleName, List<Long> defaultFieldIds) throws Exception {
        if(!((Map) oneLevelFields.get("lookupModuleFields")).containsKey(lookupModuleName))
        {
            FacilioChain chain = ReadOnlyChainFactory.getAdvancedFilterFieldsChain();
            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.ContextNames.MODULE_NAME, lookupModuleName);
            context.put(FacilioConstants.ContextNames.DEFAULT_FIELD_IDS, defaultFieldIds);
            context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
            chain.execute();
            handleFields(context,false);
        }
    }
    private Map<String, Object> createPlaceHolder(FacilioField field) {
        Map<String, Object> placeHolder = new LinkedHashMap<>();
        String name = field.getName();
        placeHolder.put("name", name);
        placeHolder.put("displayName", field.getDisplayName());
        placeHolder.put("moduleName",field.getModule().getName());
        placeHolder.put("dataType", field.getDataTypeEnum().name());
        placeHolder.put("isDefault",field.isDefault());
        FacilioField.FieldDisplayType displayType = field.getDisplayType();
        if (displayType == null) {
            FacilioField.FieldDisplayType type = FieldFactory.getDefaultDisplayTypeFromDataType(field.getDataTypeEnum());
            if (type != null) {
                displayType = type;
            }
        }
        placeHolder.put("displayType", displayType != null ? displayType.name() : null);
        if(field instanceof LookupField)
        {
            placeHolder.put("lookupModuleName",((LookupField)field).getLookupModule().getName());
        }
        return placeHolder;
    }
}
