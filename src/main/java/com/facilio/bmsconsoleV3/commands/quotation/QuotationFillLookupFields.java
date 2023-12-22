package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.LookupFieldMeta;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuotationFillLookupFields extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        fetchLookupsList.add((LookupField) fieldsAsMap.get("shipToAddress"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("billToAddress"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("client"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("tenant"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("vendor"));
        LookupFieldMeta workorderField = new LookupFieldMeta((LookupField) fieldsAsMap.get("workorder"));
        LookupField pmTrigger = (LookupField) modBean.getField("trigger", FacilioConstants.ContextNames.WORK_ORDER);
        workorderField.addChildSupplement(pmTrigger);
        fetchLookupsList.add(workorderField);
        fetchLookupsList.add((LookupField) fieldsAsMap.get("moduleState"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("contact"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("tax"));
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(moduleName));
        fetchLookupsList.add(sysCreatedBy);


        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
