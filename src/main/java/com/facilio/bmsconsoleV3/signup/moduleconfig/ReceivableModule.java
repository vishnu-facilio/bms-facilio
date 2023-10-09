package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class ReceivableModule extends BaseModuleConfig{
    public ReceivableModule(){
        setModuleName(FacilioConstants.ContextNames.RECEIVABLE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> receivable = new ArrayList<FacilioView>();
        receivable.add(getAllReceivableView().setOrder(order++));
        receivable.add(getReceivableForStatus("pending", "Pending", 1).setOrder(order++));
        receivable.add(getReceivableForStatus("partial", "Partially Received", 2).setOrder(order++));
        receivable.add(getReceivableForStatus("received", "Received", 3).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.RECEIVABLE);
        groupDetails.put("views", receivable);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllReceivableView() {
        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getReceivableModule());

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All");
        allView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getReceivableForStatus(String viewName, String viewDisplayName, int status) {
        FacilioModule receivableModule = ModuleFactory.getReceivableModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(receivableModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getReceivableStatusCriteria(receivableModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        statusView.setAppLinkNames(appLinkNames);

        return statusView;
    }

    private static Criteria getReceivableStatusCriteria(FacilioModule module, int status) {

        FacilioField receivableStatusField = new FacilioField();
        receivableStatusField.setName("status");
        receivableStatusField.setColumnName("STATUS");
        receivableStatusField.setDataType(FieldType.NUMBER);
        receivableStatusField.setModule(ModuleFactory.getReceivableModule());

        Condition statusCond = new Condition();
        statusCond.setField(receivableStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        Criteria receivableStatusCriteria = new Criteria();
        receivableStatusCriteria.addAndCondition(statusCond);

        return receivableStatusCriteria;
    }
}
