package com.facilio.apiv3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ControlActionCommandSuppimentFieldSupplyCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        
        fetchLookupsList.add((LookupField) fieldsAsMap.get("executedBy"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("resource"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("group"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("schedule"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("routine"));
        
        fetchLookupsList.add((LookupField) fieldsAsMap.get("sysCreatedBy"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);


        return false;
    }
}
