package com.facilio.remotemonitoring.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.peoplegroup.V3PeopleGroupContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.RelatedModuleOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.remotemonitoring.compute.FlaggedEventUtil;
import com.facilio.remotemonitoring.context.FlaggedEventContext;
import org.apache.commons.chain.Context;

public class FetchFlaggedEventPeopleOptionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long flaggedEventId = (Long) context.get(FacilioConstants.ContextNames.ID);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Criteria criteria = null;
        if(flaggedEventId != null) {
            FlaggedEventContext event = FlaggedEventUtil.getFlaggedEvent(flaggedEventId);
            if(event != null) {
                V3PeopleGroupContext team = event.getTeam();
                if(team != null && team.getId() > 0) {
                    criteria = new Criteria();
                    Criteria teamCriteria = new Criteria();
                    teamCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("group",FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER),String.valueOf(team.getId()), NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("people",FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER),teamCriteria, RelatedModuleOperator.RELATED));
                }
            }
        }
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        return false;
    }
}
