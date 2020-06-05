package com.facilio.bmsconsoleV3.commands.visitor;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadVisitorLookUpCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField hostField = (LookupField) fieldsAsMap.get("lastVisitedHost");
        LookupField spaceField = (LookupField) fieldsAsMap.get("lastVisitedSpace");
        LookupField locationField = (LookupField) fieldsAsMap.get("location");
        LookupField visitorTypefield = (LookupField) fieldsAsMap.get("visitorType");

        additionaLookups.add(hostField);
        additionaLookups.add(spaceField);
        additionaLookups.add(locationField);
        additionaLookups.add(visitorTypefield);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}
