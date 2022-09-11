package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ControlActionCommandModule extends BaseModuleConfig{
    public ControlActionCommandModule(){
        setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> controlActionCommandModule = new ArrayList<FacilioView>();
        controlActionCommandModule.add(getUpcomingControlCommandView().setOrder(order++));
        controlActionCommandModule.add(getHistoryControlCommandView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        groupDetails.put("views", controlActionCommandModule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getUpcomingControlCommandView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), true));

        FacilioView allView = new FacilioView();
        allView.setName("upcoming");
        allView.setDisplayName("Upcoming Commands");
        allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        allView.setSortFields(sortFields);

        Criteria criteria = new Criteria();

        List<Integer> upcomingStatusInt = new ArrayList<Integer>();

        upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED.getIntVal());
        upcomingStatusInt.add(ControlActionCommandContext.Status.SCHEDULED_WITH_NO_PERMISSION.getIntVal());

        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(upcomingStatusInt, ","), NumberOperators.EQUALS));

        allView.setCriteria(criteria);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }

    private static FacilioView getHistoryControlCommandView() {
        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("executedTime", "EXECUTED_TIME", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("history");
        allView.setDisplayName("Command History");
        allView.setModuleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
        allView.setSortFields(sortFields);

        List<Integer> historyStatusInt = new ArrayList<Integer>();

        historyStatusInt.add(ControlActionCommandContext.Status.SUCCESS.getIntVal());
        historyStatusInt.add(ControlActionCommandContext.Status.ERROR.getIntVal());
        historyStatusInt.add(ControlActionCommandContext.Status.SENT.getIntVal());

        Criteria criteria = new Criteria();

        criteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", StringUtils.join(historyStatusInt, ","), NumberOperators.EQUALS));

        allView.setCriteria(criteria);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
