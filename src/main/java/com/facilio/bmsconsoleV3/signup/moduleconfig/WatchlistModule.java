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

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormSection;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class WatchlistModule extends BaseModuleConfig{
    public WatchlistModule(){
        setModuleName(FacilioConstants.ContextNames.WATCHLIST);
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

    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule watchlistModule = modBean.getModule(FacilioConstants.ContextNames.WATCHLIST);

        FacilioForm watchListForm = new FacilioForm();
        watchListForm.setDisplayName("WATCH LIST");
        watchListForm.setName("default_watch_list_web");
        watchListForm.setModule(watchlistModule);
        watchListForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        watchListForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP));

        List<FormField> watchListFormFields = new ArrayList<>();
        watchListFormFields.add(new FormField("avatar", FacilioField.FieldDisplayType.IMAGE,"Visitor Photo", FormField.Required.OPTIONAL,1,1));
        watchListFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 2, 1));
        watchListFormFields.add(new FormField("phone", FacilioField.FieldDisplayType.TEXTBOX, "Phone", FormField.Required.REQUIRED, 3, 2));
        watchListFormFields.add(new FormField("email", FacilioField.FieldDisplayType.TEXTBOX, "Email", FormField.Required.OPTIONAL, 4, 3));
        watchListFormFields.add(new FormField("aliases", FacilioField.FieldDisplayType.LONG_DESC, "Aliases", FormField.Required.OPTIONAL, 5, 2));
        watchListFormFields.add(new FormField("physicalDescription", FacilioField.FieldDisplayType.LONG_DESC, "Physical Description", FormField.Required.OPTIONAL, 6, 2));
        watchListFormFields.add(new FormField("isBlocked", FacilioField.FieldDisplayType.DECISION_BOX, "Blocked Entry", FormField.Required.OPTIONAL, 7, 2));
        watchListFormFields.add(new FormField("isVip", FacilioField.FieldDisplayType.DECISION_BOX, "VIP", FormField.Required.OPTIONAL, 8, 3));
        watchListFormFields.add(new FormField("remarks", FacilioField.FieldDisplayType.LONG_DESC, "Remarks", FormField.Required.OPTIONAL, 9, 2));
//        watchListForm.setFields(watchListFormFields);

        FormSection section = new FormSection("Default", 1, watchListFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        watchListForm.setSections(Collections.singletonList(section));

        return Collections.singletonList(watchListForm);
    }

}
