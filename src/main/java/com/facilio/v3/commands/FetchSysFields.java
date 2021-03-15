package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class FetchSysFields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isV4 = Constants.isV4(context);
        if (isV4) {
            return false;
        }
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        FacilioModule module = modBean.getModule(moduleName);

        if (!module.isCustom()) {
            return false;
        }

        List<SupplementRecord> sysLookupFields = new ArrayList<>();
        sysLookupFields.add((LookupField) FieldFactory.getSystemField("sysCreatedBy", module));
        sysLookupFields.add((LookupField) FieldFactory.getSystemField("sysModifiedBy", module));

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (supplementFields == null) {
            supplementFields = new ArrayList<>();
            context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);
        }
        supplementFields.addAll(sysLookupFields);

        return false;
    }
}
