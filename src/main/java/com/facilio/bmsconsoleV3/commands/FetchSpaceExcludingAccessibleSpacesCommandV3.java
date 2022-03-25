package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FetchSpaceExcludingAccessibleSpacesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        boolean excludeAccessibleSpace = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "excludeAccessibleSpace"));
        FacilioField idField = FieldFactory.getField("id", "ID", ModuleFactory.getModule(moduleName), FieldType.NUMBER);
        if(excludeAccessibleSpace) {
            long orgUserId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "ouid"));
            UserBean userBean = AccountUtil.getUserBean();
            List<Long> accessibleSpaceList = userBean.getAccessibleSpaceList(orgUserId);
            if(CollectionUtils.isNotEmpty(accessibleSpaceList)) {
                Condition condition = CriteriaAPI.getCondition(idField, String.valueOf(StringUtils.join(accessibleSpaceList, ",")), NumberOperators.NOT_EQUALS);
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
            }
        }
        return false;
    }
}