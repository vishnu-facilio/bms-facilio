package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.util.AccessibleSpacesUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FetchSpaceExcludingAccessibleSpacesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        boolean excludeAccessibleSpace = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "excludeAccessibleSpace"));
        FacilioField idField = FieldFactory.getField("id", "ID", ModuleFactory.getModule(moduleName), FieldType.NUMBER);
        UserBean userBean = AccountUtil.getUserBean();
        List<Long> accessibleSpaceList = new ArrayList<>();
        if(excludeAccessibleSpace) {
            long peopleId = -1L;
            if(Constants.containsQueryParam(context,FacilioConstants.ContextNames.PEOPLE_ID)){
                peopleId = FacilioUtil.parseLong(Constants.getQueryParam(context, FacilioConstants.ContextNames.PEOPLE_ID));
            }
            if(peopleId > 0){
                List<BaseSpaceContext> baseSpaces = AccessibleSpacesUtil.getAccessibleSpaceList(null,peopleId,-1,-1,null);
                if(CollectionUtils.isNotEmpty(baseSpaces)){
                    accessibleSpaceList =  baseSpaces.stream().map(BaseSpaceContext::getId).collect(Collectors.toList());
                }
            } else {
                long orgUserId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "ouid"));
                accessibleSpaceList = userBean.getAccessibleSpaceList(orgUserId);
            }
            if (CollectionUtils.isNotEmpty(accessibleSpaceList)) {
                Condition condition = CriteriaAPI.getCondition(idField, String.valueOf(StringUtils.join(accessibleSpaceList, ",")), NumberOperators.NOT_EQUALS);
                context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, condition);
            }
        }
        return false;
    }
}