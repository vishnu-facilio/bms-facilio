package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class WatchlistModule extends BaseModuleConfig{
    public WatchlistModule(){
        setModuleName(FacilioConstants.ContextNames.WATCHLIST);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> watchlist = new ArrayList<FacilioView>();
        watchlist.add(getBlockedWatchListView().setOrder(order++));
        watchlist.add(getVipWatchListView().setOrder(order++));
        watchlist.add(getAllWatchListView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.WATCHLIST);
        groupDetails.put("views", watchlist);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getBlockedWatchListView() {
        FacilioView view = new FacilioView();
        view.setDisplayName("Blocked Watchlist");
        view.setName("blocked");
        FacilioField blockedField = FieldFactory.getField("isBlocked", "IS_BLOCKED", FieldType.BOOLEAN);
        Condition blockedCondition = CriteriaAPI.getCondition(blockedField, String.valueOf(true), BooleanOperators.IS);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(blockedCondition);
        view.setCriteria(criteria);

        return view;
    }

    private static FacilioView getVipWatchListView() {
        FacilioView view = new FacilioView();
        view.setDisplayName("VIP Watchlist");
        view.setName("vip");
        FacilioField vipField = FieldFactory.getField("isVip", "IS_VIP", FieldType.BOOLEAN);
        Condition vipCondition = CriteriaAPI.getCondition(vipField, String.valueOf(true), BooleanOperators.IS);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(vipCondition);
        view.setCriteria(criteria);

        return view;
    }

    private static FacilioView getAllWatchListView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Watchlist");

        return allView;
    }
}
