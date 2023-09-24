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

        SupplementRecord meterLocationField = (SupplementRecord) fieldsAsMap.get("meterLocation");
        SupplementRecord utilityTypeField = (SupplementRecord) fieldsAsMap.get("utilityType");
        SupplementRecord parentMeter = (SupplementRecord) fieldsAsMap.get("parentMeter");
        SupplementRecord virtualMeterTemplateField = (SupplementRecord) fieldsAsMap.get("virtualMeterTemplate");
        SupplementRecord sysCreatedBy = (SupplementRecord) fieldsAsMap.get("sysCreatedByPeople");
        SupplementRecord sysModifiedBy = (SupplementRecord) fieldsAsMap.get("sysModifiedByPeople");

        supplementFields.add(meterLocationField);
        supplementFields.add(utilityTypeField);
        supplementFields.add(parentMeter);
        supplementFields.add(virtualMeterTemplateField);
        supplementFields.add(sysCreatedBy);
        supplementFields.add(sysModifiedBy);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);

        return false;
    }
}
