package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.*;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FillInvoiceLookupFieldCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<SupplementRecord>();
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("shipToAddress"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("billToAddress"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("client"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("tenant"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("vendor"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("purchaseOrder"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("quote"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("group"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("approvers"));
        LookupFieldMeta workorderField = new LookupFieldMeta((LookupField) fieldsAsMap.get("workorder"));
        LookupField pmTrigger = (LookupField) modBean.getField("trigger", FacilioConstants.ContextNames.WORK_ORDER);
        workorderField.addChildSupplement(pmTrigger);
        fetchLookupsList.add(workorderField);
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("moduleState"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("tax"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("sysCreatedByPeople"));
        fetchLookupsList.add((SupplementRecord) fieldsAsMap.get("sysModifiedByPeople"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
