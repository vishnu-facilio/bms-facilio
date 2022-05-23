package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class BaseEventModule extends BaseModuleConfig{
    public BaseEventModule(){
        setModuleName(FacilioConstants.ContextNames.BASE_EVENT);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> baseEvent = new ArrayList<FacilioView>();
        baseEvent.add(getBaseEvents(DateOperators.TODAY).setOrder(order++));
        baseEvent.add(getBaseEvents(DateOperators.YESTERDAY).setOrder(order++));
        baseEvent.add(getBaseEvents(DateOperators.CURRENT_WEEK).setOrder(order++));
        baseEvent.add(getBaseEvents(DateOperators.LAST_WEEK).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.BASE_EVENT);
        groupDetails.put("views", baseEvent);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getBaseEvents(DateOperators dateOperator) {

        FacilioField createdTime = new FacilioField();
        createdTime.setName("createdTime");
        createdTime.setDataType(FieldType.DATE_TIME);
        createdTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        createdTime.setColumnName("CREATED_TIME");
        createdTime.setModule(ModuleFactory.getBaseEventModule());

        Condition dateCondition = new Condition();
        dateCondition.setField(createdTime);
        dateCondition.setOperator(dateOperator);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(dateCondition);

        FacilioView eventsView = new FacilioView();
        eventsView.setName(dateOperator.getOperator());
        eventsView.setDisplayName(dateOperator.getOperator());
        eventsView.setCriteria(criteria);

        return eventsView;
    }
}

