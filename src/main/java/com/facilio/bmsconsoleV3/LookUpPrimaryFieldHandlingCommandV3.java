package com.facilio.bmsconsoleV3;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.LookupPrimaryFieldHandlingCommand;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LookUpPrimaryFieldHandlingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (isV4) {
            return false;
        }
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        if (records == null) {
            records = new ArrayList<ModuleBaseWithCustomFields>();
            ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
            if (record != null) {
                records.add(record);
            }
        }
        if (records == null) {
            return false;
        }

        if (CollectionUtils.isNotEmpty(fields)) {
            List<FacilioField> lookupFields = fields.stream().filter(field -> field.getDataTypeEnum() == FieldType.LOOKUP && ((LookupField) field).getSpecialType() == null).collect(Collectors.toList());
            Map<String, FacilioField> primaryFieldMap = new HashMap<>();
            for (FacilioField field : lookupFields) {
                LookupField lookupField = (LookupField) field;
                String name = lookupField.getLookupModule().getName();
                FacilioField primaryField = modBean.getPrimaryField(name);
                if (primaryField != null) {
                    primaryFieldMap.put(field.getName(), primaryField);
                }
            }

            if (CollectionUtils.isNotEmpty(lookupFields)) {
                for (ModuleBaseWithCustomFields record : records) {
                    for (FacilioField field : lookupFields) {
                        if (!primaryFieldMap.containsKey(field.getName())) {
                            continue;
                        }
                        LookupField lookupField = (LookupField) field;

                        ModuleBaseWithCustomFields lookupRecord=null;
                        Object lookupRecordObject = FieldUtil.getValue(record,lookupField);

                        if(lookupRecordObject instanceof ModuleBaseWithCustomFields){
                            lookupRecord=(ModuleBaseWithCustomFields)lookupRecordObject;
                        }

                        if (lookupRecord == null) {
                            continue;
                        }
                        Object property = LookupPrimaryFieldHandlingCommand.getMainFieldProperty(lookupRecord, primaryFieldMap.get(field.getName()), modBean);
                        if (property == null) {
                            continue;
                        }
                        FieldUtil.setPrimaryValue(lookupRecord,property);
                    }
                }
            }
        }

        return false;
    }
}
