package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.LookupField;

import java.util.*;

public class TaskModule extends BaseModuleConfig{
    public TaskModule(){
        setModuleName(FacilioConstants.ContextNames.TASK);
    }



    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> task = new ArrayList<FacilioView>();
        task.add(getMyTasks().setOrder(order++));
        task.add(getAllTaskView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.TASK);
        groupDetails.put("views", task);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getMyTasks() {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(getMyUserCondition());

        FacilioView openTicketsView = new FacilioView();
        openTicketsView.setName("mytasks");
        openTicketsView.setDisplayName("My Tasks");
        openTicketsView.setCriteria(criteria);

        return openTicketsView;
    }

    private static FacilioView getAllTaskView() {

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Tasks");

        return allView;
    }

    public static Condition getMyUserCondition() {
        LookupField userField = new LookupField();
        userField.setName("assignedTo");
        userField.setColumnName("ASSIGNED_TO_ID");
        userField.setDataType(FieldType.LOOKUP);
        userField.setModule(ModuleFactory.getTicketsModule());
        userField.setSpecialType(FacilioConstants.ContextNames.USERS);

        Condition myUserCondition = new Condition();
        myUserCondition.setField(userField);
        myUserCondition.setOperator(PickListOperators.IS);
        myUserCondition.setValue(FacilioConstants.Criteria.LOGGED_IN_USER);

        return myUserCondition;
    }

}
