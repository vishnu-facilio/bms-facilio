package com.facilio.bmsconsoleV3.commands.meter;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeterSupplementsSupplyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = "meter";
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (supplementFields == null) {
            supplementFields = new ArrayList<>();
        }

        SupplementRecord spaceField = (SupplementRecord) fieldsAsMap.get("space");
        SupplementRecord servingToField = (SupplementRecord) fieldsAsMap.get("servingTo");
        SupplementRecord utilityTypeField = (SupplementRecord) fieldsAsMap.get("utilityType");
        SupplementRecord virtualMeterTemplateField = (SupplementRecord) fieldsAsMap.get("virtualMeterTemplate");

        supplementFields.add(spaceField);
        supplementFields.add(servingToField);
        supplementFields.add(utilityTypeField);
        supplementFields.add(virtualMeterTemplateField);


        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        supplementFields.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        supplementFields.add(sysModifiedBy);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);

        return false;
    }
}
