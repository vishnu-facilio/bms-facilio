package com.facilio.bmsconsoleV3.commands.safetyplan;

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

public class V3LoadWorkAssetLookUpsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

        List<LookupField> additionaLookups = new ArrayList<LookupField>();
        LookupField baseSpaceField = (LookupField) fieldsAsMap.get("space");
        LookupField assetField = (LookupField) fieldsAsMap.get("asset");
        LookupField safetyPlanField = (LookupField) fieldsAsMap.get("safetyPlan");
        additionaLookups.add(baseSpaceField);
        additionaLookups.add(assetField);
        additionaLookups.add(safetyPlanField);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS,additionaLookups);
        return false;
    }
}
