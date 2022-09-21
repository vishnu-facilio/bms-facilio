package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoadSparePartsSupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
        fetchLookupsList.add((LookupField) fieldsAsMap.get("sysCreatedBy"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("sysModifiedBy"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("asset"));
        fetchLookupsList.add((LookupField) fieldsAsMap.get("itemType"));
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fetchLookupsList);
        return false;
    }
}