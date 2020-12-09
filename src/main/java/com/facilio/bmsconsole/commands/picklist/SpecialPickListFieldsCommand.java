package com.facilio.bmsconsole.commands.picklist;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SpecialPickListFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean =  (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField defaultField = (FacilioField) context.get(FacilioConstants.ContextNames.DEFAULT_FIELD);
        List<FacilioField> selectFields = new ArrayList<>();
        List<LookupField> supplements = new ArrayList<>();

        if (moduleName.equals(FacilioConstants.ContextNames.RESOURCE)) { //Technically this is not secondary field
            selectFields.add(modBean.getField("resourceType", moduleName));
        }

        addField(defaultField, selectFields, supplements);
        FacilioField secondaryField = getSecondaryField(moduleName, modBean);
        if (secondaryField != null) {
            context.put(FacilioConstants.PickList.SECONDARY_FIELD, secondaryField);
            addField(secondaryField, selectFields, supplements);
        }
        if (CollectionUtils.isNotEmpty(supplements)) {
            context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, supplements);
        }
        context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, selectFields);
        return false;
    }

    private void addField (FacilioField field, List<FacilioField> selectFields, List<LookupField> supplements) {
        if (field instanceof LookupField) {
            supplements.add((LookupField) field);
        }
        selectFields.add(field);
    }

    private FacilioField getSecondaryField (String moduleName, ModuleBean modBean) throws Exception {
        switch (moduleName) {
            case FacilioConstants.ContextNames.TOOL:
            case FacilioConstants.ContextNames.ITEM:
                return modBean.getField("storeRoom", moduleName);
            default:
                return null;
        }
    }
}
