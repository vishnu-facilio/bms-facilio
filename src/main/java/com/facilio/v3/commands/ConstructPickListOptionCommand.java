package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FieldOption;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConstructPickListOptionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, Object> recordMap = (Map<String, Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<Map<String, Object>> records = new ArrayList<>();

        if(MapUtils.isNotEmpty(recordMap)){
            Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
            records = FieldUtil.getAsMapList((List<ModuleBaseWithCustomFields>)recordMap.get(moduleName), beanClass);
        }
        List<FieldOption<Long>> pickList = null;
        if(CollectionUtils.isNotEmpty(records)) {
            FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
            FacilioField secondaryField = (FacilioField) context.get(FacilioConstants.PickList.SECONDARY_FIELD);
            FacilioField fourthField = (FacilioField) context.get(FacilioConstants.PickList.FOURTH_FIELD);
            FacilioField subModuleField = (FacilioField) context.get(FacilioConstants.PickList.SUBMODULE_FIELD);
            FacilioField colorField = (FacilioField) context.get(FacilioConstants.PickList.COLOR_FIELD);
            FacilioField accentField = (FacilioField) context.get(FacilioConstants.PickList.ACCENT_FIELD);
            String severityLevel = (String) context.get(FacilioConstants.PickList.SEVERITY_LEVEL);
            FacilioField subModuleTypeField = (FacilioField) context.get(FacilioConstants.PickList.SUB_MODULE_TYPE_FIELD);
            pickList = constructFieldOptionsFromRecords(records, defaultField, secondaryField , fourthField, subModuleField,colorField,accentField,severityLevel,subModuleTypeField);

            int pickListRecordCount = pickList == null ? 0 : pickList.size();
            boolean localSearch = true;
            JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
            if (pagination != null) {
                int page = (int) pagination.get("page");
                int perPage = (int) pagination.get("perPage");
                localSearch = page == 1 && pickListRecordCount < perPage;
            }
            context.put(FacilioConstants.PickList.LOCAL_SEARCH, localSearch);
        }
        context.put(FacilioConstants.ContextNames.PICKLIST, pickList);

        return false;

    }

    public static List<FieldOption<Long>> constructFieldOptionsFromRecords (List<Map<String, Object>> records, FacilioField defaultField, FacilioField secondaryField,FacilioField fourthField, FacilioField subModuleField,FacilioField colorField,FacilioField accentField,String severityLevel,FacilioField subModuleTypeField) throws Exception {

        if (CollectionUtils.isEmpty(records)) {
            return null;
        }

        List<FieldOption<Long>> options = new ArrayList<>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField defaultFieldLookupPrimary = getMainFieldOfLookup(defaultField, modBean);
        FacilioField secondaryFieldLookupPrimary = getMainFieldOfLookup(secondaryField, modBean);
        FacilioField subModuleFieldLookupPrimary = getMainFieldOfLookup(subModuleField, modBean);
        FacilioField fourthFieldLookupPrimary = getMainFieldOfLookup(fourthField, modBean);
        FacilioField colorFieldLookupPrimary = getMainFieldOfLookup(colorField, modBean);
        FacilioField accentFieldLookupPrimary = getMainFieldOfLookup(accentField, modBean);
        FacilioField subModuleTypeFieldLookupPrimary = getMainFieldOfLookup(subModuleTypeField, modBean);
        for (Map<String, Object> prop : records) {
            Long id = (Long) prop.get("id");
            Object primaryLabel = getValue(prop, defaultField, defaultFieldLookupPrimary);
            Object secondaryLabel = secondaryField == null ? null : getValue(prop, secondaryField, secondaryFieldLookupPrimary);
            Object fourthLabel = fourthField == null ? null : getValue(prop , fourthField ,fourthFieldLookupPrimary);
            String subModuleLabel = subModuleField == null ? null : String.valueOf(getValue(prop, subModuleField, subModuleFieldLookupPrimary));
            String color = colorField == null ? null :(String) getValue(prop , colorField ,colorFieldLookupPrimary);
            String accent = accentField == null ? null :(String) getValue(prop , accentField ,accentFieldLookupPrimary);
            String moduleName= subModuleTypeField ==null?null:getSubModuleName(prop,subModuleTypeField,subModuleTypeFieldLookupPrimary);
            options.add(new FieldOption<>(
                    id,
                    primaryLabel,
                    secondaryLabel,
                    fourthLabel,
                    subModuleLabel,
                    color,accent,severityLevel,moduleName
            ));
        }
        return options;

    }
    private static FacilioField getMainFieldOfLookup (FacilioField field, ModuleBean modBean) throws Exception {
        if (field != null && field instanceof LookupField && StringUtils.isEmpty(((LookupField) field).getSpecialType())) {
            return modBean.getPrimaryField(((LookupField) field).getLookupModule().getName());
        }
        return null;

    }
    private static Object getValue (Map<String, Object> prop, FacilioField field, FacilioField lookupMainField) {
        if (field instanceof LookupField) {
            LookupField lookupField = (LookupField) field;
            if (StringUtils.isEmpty(lookupField.getSpecialType())) {
                Map<String, Object> val = (Map<String, Object>) prop.get(field.getName());
                return val == null ? null : val.get(lookupMainField.getName());
            }
            else {
                return LookupSpecialTypeUtil.getPrimaryFieldValue(lookupField.getSpecialType(), prop);
            }
        }
        else {
            return prop.get(field.getName());
        }

    }
    private static String getSubModuleName(Map<String, Object> prop, FacilioField field, FacilioField lookupMainField) throws Exception {

        String moduleName=field.getModule().getName();
        String subModuleName = null;
        if(moduleName.equals(FacilioConstants.ContextNames.BASE_SPACE)) {
            subModuleName= V3BaseSpaceContext.SpaceType.getType((Integer)getValue(prop,field,lookupMainField)).getModuleName();
        }else if(moduleName.equals(FacilioConstants.ContextNames.RESOURCE)){
            subModuleName= V3ResourceContext.ResourceType.valueOf((Integer)getValue(prop,field,lookupMainField)).getSubModuleName();
            if(subModuleName.equals(FacilioConstants.ContextNames.BASE_SPACE)){
                subModuleName= SpaceAPI.getBaseSpace((Long) prop.get("id")).getSpaceTypeEnum().getModuleName();
            }
        }
        return subModuleName;

    }
}



