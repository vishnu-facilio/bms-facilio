package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;

public class LoadStoreRoomLookUpCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> additionaLookups = new ArrayList<SupplementRecord>();
        LookupField locationField = (LookupField) fieldsAsMap.get("location");
        LookupField ownerField = (LookupField) fieldsAsMap.get("owner");
        LookupField siteField = (LookupField) fieldsAsMap.get("site");

        MultiLookupMeta servingsites = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("servingsites"));

        FacilioField nameField = FieldFactory.getField("name", "NAME", resourceModule, FieldType.STRING);
        servingsites.setSelectFields(Collections.singletonList(nameField));

        additionaLookups.add(locationField);
        additionaLookups.add(ownerField);
        additionaLookups.add(siteField);
        additionaLookups.add(servingsites);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}