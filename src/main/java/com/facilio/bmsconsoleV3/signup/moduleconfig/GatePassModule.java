package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

import java.util.*;

public class GatePassModule extends BaseModuleConfig{
    public GatePassModule(){
        setModuleName(FacilioConstants.ContextNames.GATE_PASS);
    }

    @Override
    protected void addForms() throws Exception {

    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> gatePass = new ArrayList<FacilioView>();
        gatePass.add(getAllGatePass().setOrder(order++));
        gatePass.add(getGatePassForStatus("pending", "Pending", 1).setOrder(order++));
        gatePass.add(getGatePassForStatus("approved", "Approved", 2).setOrder(order++));
        gatePass.add(getGatePassForStatus("rejected", "Rejected", 4).setOrder(order++));
        gatePass.add(getPendingReturnGatePass("pendingreturn", "Pending Return", 2).setOrder(order++));
        gatePass.add(getOverDueReturnGatePass("overduereturn", "Overdue Return", 2).setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.GATE_PASS);
        groupDetails.put("views", gatePass);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getGatePassForStatus(String viewName, String viewDisplayName, int status) {
        FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(gatePassModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getGatePassCriteria(gatePassModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static FacilioView getAllGatePass() {
        FacilioModule purchasedItemModule = ModuleFactory.getGatePassModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("issuedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("ISSUED_TIME");
        createdTime.setModule(purchasedItemModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, true));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Gate Pass");
        allView.setSortFields(sortFields);

        return allView;
    }

    private static Criteria getGatePassCriteria(FacilioModule module, int status) {
        FacilioField gpStatusField = new FacilioField();
        gpStatusField.setName("status");
        gpStatusField.setColumnName("STATUS");
        gpStatusField.setDataType(FieldType.NUMBER);
        gpStatusField.setModule(ModuleFactory.getGatePassModule());

        Condition statusCond = new Condition();
        statusCond.setField(gpStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        Criteria gatePassStatusCriteria = new Criteria();
        gatePassStatusCriteria.addAndCondition(statusCond);
        return gatePassStatusCriteria;
    }

    private static FacilioView getPendingReturnGatePass(String viewName, String viewDisplayName, int status) {
        FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(gatePassModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getPendingReturnGatePassCriteria(gatePassModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getPendingReturnGatePassCriteria(FacilioModule module, int status) {

        FacilioField gpStatusField = new FacilioField();
        gpStatusField.setName("status");
        gpStatusField.setColumnName("STATUS");
        gpStatusField.setModule(ModuleFactory.getGatePassModule());

        Condition statusCond = new Condition();
        statusCond.setField(gpStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        FacilioField gpReturnableField = new FacilioField();
        gpReturnableField.setName("isReturnable");
        gpReturnableField.setColumnName("IS_RETURNABLE");
        gpReturnableField.setDataType(FieldType.BOOLEAN);
        gpReturnableField.setModule(ModuleFactory.getGatePassModule());

        Condition returnCond = new Condition();
        returnCond.setField(gpReturnableField);
        returnCond.setOperator(BooleanOperators.IS);
        returnCond.setValue(String.valueOf(true));

        Criteria gatePassStatusCriteria = new Criteria();
        gatePassStatusCriteria.addAndCondition(statusCond);
        gatePassStatusCriteria.addAndCondition(returnCond);
        return gatePassStatusCriteria;
    }

    private static FacilioView getOverDueReturnGatePass(String viewName, String viewDisplayName, int status) {
        FacilioModule gatePassModule = ModuleFactory.getGatePassModule();

        FacilioField createdTime = new FacilioField();
        createdTime.setName("sysCreatedTime");
        createdTime.setDataType(FieldType.NUMBER);
        createdTime.setColumnName("SYS_CREATED_TIME");
        createdTime.setModule(gatePassModule);

        List<SortField> sortFields = Arrays.asList(new SortField(createdTime, false));

        Criteria criteria = getOverdueReturnGatePassCriteria(gatePassModule, status);

        FacilioView statusView = new FacilioView();
        statusView.setName(viewName);
        statusView.setDisplayName(viewDisplayName);
        statusView.setSortFields(sortFields);
        statusView.setCriteria(criteria);

        return statusView;
    }

    private static Criteria getOverdueReturnGatePassCriteria(FacilioModule module, int status) {

        FacilioField gpStatusField = new FacilioField();
        gpStatusField.setName("status");
        gpStatusField.setColumnName("STATUS");
        gpStatusField.setModule(ModuleFactory.getGatePassModule());

        Condition statusCond = new Condition();
        statusCond.setField(gpStatusField);
        statusCond.setOperator(NumberOperators.EQUALS);
        statusCond.setValue(String.valueOf(status));

        FacilioField gpReturnableField = new FacilioField();
        gpReturnableField.setName("isReturnable");
        gpReturnableField.setColumnName("IS_RETURNABLE");
        gpReturnableField.setDataType(FieldType.BOOLEAN);
        gpReturnableField.setModule(ModuleFactory.getGatePassModule());

        Condition returnCond = new Condition();
        returnCond.setField(gpReturnableField);
        returnCond.setOperator(BooleanOperators.IS);
        returnCond.setValue(String.valueOf(true));

        FacilioField gpReturnTimeField = new FacilioField();
        gpReturnTimeField.setName("returnTime");
        gpReturnTimeField.setColumnName("RETURN_TIME");
        gpReturnTimeField.setDataType(FieldType.NUMBER);
        gpReturnTimeField.setModule(ModuleFactory.getGatePassModule());

        Long currTime = DateTimeUtil.getCurrenTime();

        Condition overDue = new Condition();
        overDue.setField(gpReturnTimeField);
        overDue.setOperator(NumberOperators.LESS_THAN);
        overDue.setValue(currTime + "");

        Criteria gatePassStatusCriteria = new Criteria();
        gatePassStatusCriteria.addAndCondition(statusCond);
        gatePassStatusCriteria.addAndCondition(returnCond);
        gatePassStatusCriteria.addAndCondition(overDue);
        return gatePassStatusCriteria;
    }
}
