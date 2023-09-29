package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

import static com.facilio.readingrule.util.NewReadingRuleAPI.getSystemPage;

public class NewReadingRulesModule extends BaseModuleConfig {
    public NewReadingRulesModule() {
        setModuleName(FacilioConstants.ReadingRules.NEW_READING_RULE);
    }

    @Override
    protected void addTriggers() throws Exception {
        return;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> newReadingRule = new ArrayList<FacilioView>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        newReadingRule.add(getRulesByStatusForNewRule("active", "Active", true, modBean).setOrder(order++));
        newReadingRule.add(getRulesByStatusForNewRule("inactive", "In Active", false, modBean).setOrder(order++));
        newReadingRule.add(getAllNewReadingRules(modBean).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ReadingRules.NEW_READING_RULE);
        groupDetails.put("views", newReadingRule);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getRulesByStatusForNewRule(String name, String displayName, boolean status, ModuleBean modBean) throws Exception {
        List<FacilioField> rulesFields = modBean.getAllFields(FacilioConstants.ReadingRules.NEW_READING_RULE);
        FacilioField statusField = FieldFactory.getAsMap(rulesFields).get("status");

        Condition statusCondition = CriteriaAPI.getCondition(statusField, String.valueOf(status), BooleanOperators.IS);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusCondition);
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(ModuleFactory.getNewReadingRuleModule());

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        FacilioView view = new FacilioView();
        view.setName(name);
        view.setDisplayName(displayName);
        view.setCriteria(criteria);
        view.setSortFields(sortFields);
        view.setFields(getViewFields());
        view.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));

        return view;
    }

    private static FacilioView getAllNewReadingRules(ModuleBean modBean) throws Exception {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");

        createdTime.setModule(modBean.getModule(FacilioConstants.ReadingRules.NEW_READING_RULE));

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));
        allView.setSortFields(sortFields);
        allView.setFields(getViewFields());
        allView.setAppLinkNames(Arrays.asList(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        return allView;
        
    }

    private static List<ViewField> getViewFields() {
        List<ViewField> columns = new ArrayList<>();
        columns.add(new ViewField("name", "Name"));
        columns.add(new ViewField("assetCategory", "Asset Category"));
        columns.add(new ViewField("status", "Status"));
        return columns;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();
        List<String> appNames = new ArrayList<>();

        appNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);

        for (String appName : appNames) {
            ApplicationContext app = ApplicationApi.getApplicationForLinkName(appName);
            appNameVsPage.put(appName,getSystemPage(app));
        }
        return appNameVsPage;
    }
}
