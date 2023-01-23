package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class PeopleAnnouncementModule extends BaseModuleConfig{
    public PeopleAnnouncementModule(){
        setModuleName(FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
    }


    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> peopleAnnouncement = new ArrayList<FacilioView>();
        peopleAnnouncement.add(getAllPeopleAnnouncementView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS);
        groupDetails.put("views", peopleAnnouncement);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllPeopleAnnouncementView() {

        FacilioModule module = ModuleFactory.getPeopleAnnouncementModule();
        FacilioModule announcementModule = ModuleFactory.getAnnouncementModule();

        FacilioField sysCreatedTime = new FacilioField();
        sysCreatedTime.setName("sysCreatedTime");
        sysCreatedTime.setColumnName("SYS_CREATED_TIME");
        sysCreatedTime.setDataType(FieldType.DATE_TIME);
        sysCreatedTime.setModule(announcementModule);

        List<SortField> sortFields = Arrays.asList(new SortField(sysCreatedTime, false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Announcements");
        allView.setModuleName(module.getName());
        allView.setSortFields(sortFields);

        FacilioField isCancelledField = new FacilioField();
        isCancelledField.setName("isCancelled");
        isCancelledField.setColumnName("IS_CANCELLED");
        isCancelledField.setDataType(FieldType.BOOLEAN);
        isCancelledField.setModule(announcementModule);

        Condition condition = new Condition();
        condition.setField(isCancelledField);
        condition.setOperator(BooleanOperators.IS);
        condition.setValue("false");

        Criteria criteria = new Criteria();
        criteria.addOrCondition(condition);

        allView.setCriteria(criteria);

        allView.setAppLinkNames(Collections.singletonList(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP));

        return allView;
    }
}
