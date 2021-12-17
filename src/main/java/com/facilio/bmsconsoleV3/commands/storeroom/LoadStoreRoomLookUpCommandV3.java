package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FacilioField.FieldDisplayType;
import com.facilio.modules.fields.LookupField;

public class LoadStoreRoomLookUpCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField locationField = (LookupField) fieldsAsMap.get("location");
        LookupField ownerField = (LookupField) fieldsAsMap.get("owner");
        LookupField siteField = (LookupField) fieldsAsMap.get("site");
        locationField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
        ownerField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);
        siteField.setDisplayType(FieldDisplayType.LOOKUP_SIMPLE);

        additionaLookups.add(locationField);
        additionaLookups.add(ownerField);
        additionaLookups.add(siteField);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}