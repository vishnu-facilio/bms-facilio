package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoadAnnouncementLookupCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        if (fields == null) {
            fields = modBean.getAllFields(moduleName);
        }
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> additionaLookups = new ArrayList<SupplementRecord>();
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(moduleName));
        additionaLookups.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(moduleName));
        additionaLookups.add(sysModifiedBy);
        
        MultiLookupMeta audienceField = new MultiLookupMeta((MultiLookupField) fieldsAsMap.get("audience"));

        FacilioField nameField = FieldFactory.getField("name", "NAME", modBean.getModule(FacilioConstants.ContextNames.AUDIENCE), FieldType.STRING);
        audienceField.setSelectFields(Collections.singletonList(nameField));
        additionaLookups.add(audienceField);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);

        return false;
    }
}
