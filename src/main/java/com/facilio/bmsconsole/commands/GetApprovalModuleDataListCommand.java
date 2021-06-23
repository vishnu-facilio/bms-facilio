package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class GetApprovalModuleDataListCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isNotEmpty(moduleName)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Invalid module");
            }

            FacilioField approvalStatusField = modBean.getField("approvalStatus", module.getName());
            if (approvalStatusField == null) {
                throw new IllegalArgumentException("Approval is not enabled for the module " + moduleName);
            }

            Criteria criteria = new Criteria();

            Criteria requestedStateCriteria = ViewFactory.getRequestedStateCriteria(true);

            criteria.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, CommonOperators.IS_NOT_EMPTY));
            criteria.addAndCondition(CriteriaAPI.getCondition(approvalStatusField, requestedStateCriteria, LookupOperator.LOOKUP));

            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        }
        return false;
    }
}
