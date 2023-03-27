package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetGeoLocationFiledsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DISPLAY_TYPE","displayTypeInt", String.valueOf(FacilioField.FieldDisplayType.GEO_LOCATION.getIntValForDB()), PickListOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE","dataType",String.valueOf(FieldType.STRING.getTypeAsInt()),PickListOperators.IS));

        Criteria fetchLookUpCriteria = new Criteria();
        fetchLookUpCriteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE","dataType",String.valueOf(FieldType.LOOKUP.getTypeAsInt()),PickListOperators.IS));

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(modBean.getAllFields(moduleName, null, null, criteria));
        List<FacilioField> lookupFields = modBean.getAllFields(moduleName,null,null,fetchLookUpCriteria);
        fields.addAll(lookupFields);
        List<Map<String,Object>> allFields = getFields(fields);


        Map<String, Object> moduleNameVsFieldsMap = new HashMap<>();
        for(FacilioField lookupField:lookupFields){
            List<FacilioField> lookUpLocationFields = modBean.getAllFields(((LookupField) lookupField).getLookupModule().getName(),null,null,criteria);
            if(LookupSpecialTypeUtil.isSpecialType(((LookupField) lookupField).getLookupModule().getName())){
                lookUpLocationFields = lookUpLocationFields.stream().filter(field -> (field.getDataTypeEnum() != null && field.getDisplayType() != null) && (field.getDisplayType().equals(FacilioField.FieldDisplayType.GEO_LOCATION))).collect(Collectors.toList());
            }
            List<Map<String, Object>> moduleFields = getFields(lookUpLocationFields);
            if(CollectionUtils.isNotEmpty(moduleFields)){
                moduleNameVsFieldsMap.put(((LookupField) lookupField).getLookupModule().getName(),moduleFields);
            }
        }

        context.put(FacilioConstants.ContextNames.FIELDS,allFields);
        context.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST,moduleNameVsFieldsMap);


        return false;
    }

    public List<Map<String, Object>> getFields(List<FacilioField> fields) {
        List<Map<String, Object>> moduleFields = new ArrayList<>();
        for (FacilioField field : fields) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", field.getName());
                map.put("displayName", field.getDisplayName());
                map.put("dataType", field.getDataTypeEnum().name());
                map.put("fieldId", field.getFieldId());
                FacilioField.FieldDisplayType displayType = field.getDisplayType();
                map.put("displayType",displayType.name());
                if (field instanceof LookupField) {
                    map.put("module", ((LookupField) field).getLookupModule().getName());
                }
                moduleFields.add(map);
        }
        return moduleFields;

    }
}
