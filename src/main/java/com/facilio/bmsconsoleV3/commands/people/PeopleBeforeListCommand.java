package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class PeopleBeforeListCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
        if(StringUtils.isNotEmpty(search)){
            FacilioField nameField = Constants.getModBean().getField(FacilioConstants.ContextNames.NAME,FacilioConstants.ContextNames.PEOPLE);
            FacilioField emailField = Constants.getModBean().getField(FacilioConstants.ContextNames.EMAIL,FacilioConstants.ContextNames.PEOPLE);
            Criteria searchCriteria = new Criteria();
            searchCriteria.addAndCondition(CriteriaAPI.getCondition(nameField,search, StringOperators.CONTAINS));
            searchCriteria.addOrCondition(CriteriaAPI.getCondition(emailField,search, StringOperators.CONTAINS));
            context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, searchCriteria);
            context.remove(FacilioConstants.ContextNames.SEARCH);
        }
        return false;
    }
}
