package com.facilio.bmsconsoleV3.commands.communityFeatures.dealsandoffers;

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

public class DealsAndOffersFillLookupFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> fetchLookupsList = new ArrayList<>();
        LookupFieldMeta neighbourhoodField = new LookupFieldMeta((LookupField) fieldsAsMap.get("neighbourhood"));
        LookupField location = (LookupField) modBean.getField("location", FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD);
        neighbourhoodField.addChildSupplement(location);
        fetchLookupsList.add(neighbourhoodField);
        LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(moduleName));
        fetchLookupsList.add(sysCreatedBy);
        LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(moduleName));
        fetchLookupsList.add(sysModifiedBy);
        fetchLookupsList.add((LookupField) fieldsAsMap.get("audience"));
        fetchLookupsList.add((LargeTextField)fieldsAsMap.get("description"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);

        return false;
    }
}
