package com.facilio.bmsconsoleV3.commands.space;

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

public class SpaceFillLookupFieldsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        fetchLookupsList.add((LookupField) fieldsAsMap.get("location"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("spaceCategory"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("building"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("floor"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("space1"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("space2"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("space3"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("space4"));



        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
