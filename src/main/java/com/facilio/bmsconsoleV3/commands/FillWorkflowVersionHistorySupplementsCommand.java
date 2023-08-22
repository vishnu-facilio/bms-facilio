package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;
import java.util.ArrayList;
import java.util.List;

public class FillWorkflowVersionHistorySupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY;
        List<SupplementRecord> supplementFields = new ArrayList<>();

        LookupField createdByPeople = (LookupField) modBean.getField("createdByPeople",moduleName);
        supplementFields.add(createdByPeople);
        LookupField modifiedByPeople = (LookupField) modBean.getField("modifiedByPeople",moduleName);
        supplementFields.add(modifiedByPeople);

        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, supplementFields);
        return false;
    }
}
