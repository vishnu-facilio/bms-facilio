package com.facilio.bmsconsoleV3.commands.safetyplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeleteWorkorderPrecaution extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = (List<Long>) context.get("recordIds");
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderPrecautionModule = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD_PRECAUTION);
        Criteria criteria = new Criteria();
        Condition condition = CriteriaAPI.getCondition("WORKORDER_HAZARD_ID", "workorderHazard", StringUtils.join(recordIds,','), NumberOperators.EQUALS);
        criteria.addAndCondition(condition);
        V3RecordAPI.deleteRecords(workorderPrecautionModule.getName(),criteria,false);
        return false;
    }
}
