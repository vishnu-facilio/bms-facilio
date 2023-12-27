package com.facilio.fields.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.filters.FilterFieldContext;
import com.facilio.bmsconsole.util.ModuleLocalIdUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.ModuleViewField;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class ViewFieldsResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<FacilioField> fieldsList = (List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS);
        fieldsList = CollectionUtils.isNotEmpty(fieldsList) ? fieldsList : new ArrayList<>();

        // siteId Field
        FieldUtil.setSiteIdFieldForModuleFields(fieldsList, moduleName);

        List<String> fixedFieldNames = (List<String>) context.get(FacilioConstants.FieldsConfig.FIXED_FIELD_NAMES);
        List<String> fixedSelectableFieldNames = (List<String>) context.get(FacilioConstants.FieldsConfig.FIXED_SELECTABLE_FIELD_NAMES);
        Map<String, JSONObject> customization = (Map<String, JSONObject>) context.get(FacilioConstants.FieldsConfig.CUSTOMIZATION);

        List<ModuleViewField> viewFieldList = new ArrayList<>();
        boolean isCustomization = MapUtils.isNotEmpty(customization);
        boolean hasFixedFields = CollectionUtils.isNotEmpty(fixedFieldNames);
        boolean hasFixedSelectableFields = CollectionUtils.isNotEmpty(fixedSelectableFieldNames);
        for (FacilioField field : fieldsList) {
            ModuleViewField viewField = new ModuleViewField(field);
            if ( hasFixedFields && fixedFieldNames.contains(field.getName())) {
                viewField.setFixed(true);
            } else if (hasFixedSelectableFields && fixedSelectableFieldNames.contains(field.getName())) {
                viewField.setFixedSelectable(true);
            }
            if(isCustomization && customization.containsKey(viewField.getName())){
                viewField.setCustomization(customization.get(viewField.getName()).toJSONString());
            }
            viewFieldList.add(viewField);
        }

        viewFieldList.sort(Comparator.comparing(ModuleViewField::isFixed).reversed()
                .thenComparing(ModuleViewField::getDisplayName));

        context.put(FacilioConstants.ContextNames.FIELDS, viewFieldList);

        return false;
    }

}
