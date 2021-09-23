package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuditLogDefaultListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        context.put(FacilioConstants.ContextNames.SORTING_QUERY, "TIME DESC");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.AUDIT_LOGS));
        List<SupplementRecord> supplements = new ArrayList<>();
        supplements.add((LookupField) fieldMap.get("performedBy"));

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplements);

        return false;
    }
}
