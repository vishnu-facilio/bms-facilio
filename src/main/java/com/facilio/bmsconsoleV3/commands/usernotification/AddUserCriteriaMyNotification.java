package com.facilio.bmsconsoleV3.commands.usernotification;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class AddUserCriteriaMyNotification extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.USER_NOTIFICATION));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("user"), String.valueOf(AccountUtil.getCurrentUser().getId()), PickListOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("application"), String.valueOf(AccountUtil.getCurrentApp().getId()), PickListOperators.IS));

        context.put(FacilioConstants.ContextNames.SORTING_QUERY, "User_Notification.SYS_CREATED_TIME desc");
        context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, criteria);
        return false;
    }
}
