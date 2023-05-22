package com.facilio.bmsconsoleV3.commands.communityFeatures.announcement;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class UserScopingForAnnouncements extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule announcementModule = Constants.getModBean().getModule("peopleannouncement");
        List<FacilioField> fields = Constants.getModBean().getAllFields(announcementModule.getName());
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        Criteria criteria = new Criteria();
        Condition condition = new Condition();
        condition.setValue(String.valueOf(AccountUtil.getCurrentUser().getPeopleId()));
        condition.setOperator(StringOperators.IS);
        condition.setField(fieldMap.get("people"));
        criteria.addAndCondition(condition);
        context.put(Constants.BEFORE_FETCH_CRITERIA,criteria);
        return false;
    }
}
