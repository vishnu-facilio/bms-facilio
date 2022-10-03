package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class EventModule extends BaseModuleConfig{
    public EventModule(){
        setModuleName(FacilioConstants.ContextNames.EVENT);
    }

    @Override
    protected void addTriggers() throws Exception {
        return;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> event = new ArrayList<FacilioView>();
        event.add(getEvents("Today").setOrder(order++));
        event.add(getEvents("Yesterday").setOrder(order++));
        event.add(getEvents("ThisWeek").setOrder(order++));
        event.add(getEvents("LastWeek").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.EVENT);
        groupDetails.put("views", event);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getEvents(String category) {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(EventConstants.EventModuleFactory.getEventModule());

        Condition dateCondition = new Condition();
        dateCondition.setField(createdTime);
        if (category.equals("Today")) {
            dateCondition.setOperator(DateOperators.TODAY);
        } else if (category.equals("Yesterday")) {
            dateCondition.setOperator(DateOperators.YESTERDAY);
        } else if (category.equals("ThisWeek")) {
            dateCondition.setOperator(DateOperators.CURRENT_WEEK);
        } else if (category.equals("LastWeek")) {
            dateCondition.setOperator(DateOperators.LAST_WEEK);
        }
        Criteria criteria = new Criteria();
        criteria.addAndCondition(dateCondition);

        FacilioView eventsView = new FacilioView();
        eventsView.setName("Created Time");
        eventsView.setDisplayName("Event Time");
        eventsView.setCriteria(criteria);

        return eventsView;
    }
}

