package com.facilio.bmsconsoleV3.commands.site;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SiteFillLookupFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        fetchLookupsList.add((LookupField) fieldsAsMap.get("location"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("client"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("failureClass"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("decommissionedBy"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("managedBy"));
        if(fieldsAsMap.get("territory") != null){
            fetchLookupsList.add((LookupField) fieldsAsMap.get("territory"));
        }


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
