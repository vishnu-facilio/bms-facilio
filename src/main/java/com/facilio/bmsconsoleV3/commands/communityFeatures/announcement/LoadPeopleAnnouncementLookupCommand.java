package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.agent.integration.AgentIntegrationKeys;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadPeopleAnnouncementLookupCommand extends FacilioCommand {
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
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
        additionaLookups.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.ANNOUNCEMENT));
        additionaLookups.add(sysModifiedBy);
        additionaLookups.add((LookupField) fieldsAsMap.get("audience"));
        additionaLookups.add((LargeTextField)fieldsAsMap.get("longDescription"));


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);

        return false;
    }
}
