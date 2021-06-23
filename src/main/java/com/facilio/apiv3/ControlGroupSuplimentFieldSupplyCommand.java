package com.facilio.apiv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ControlGroupSuplimentFieldSupplyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        
        fetchLookupsList.add((LookupField) fieldsAsMap.get("space"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("controlSchedule"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("sysCreatedBy"));
        
        if(moduleName.equals(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)) {
        	fetchLookupsList.add((LookupField) fieldsAsMap.get("controlScheduleChild"));
        }
        
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);


        return false;
    }
}
