package com.facilio.bmsconsoleV3.signup.Point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.signup.moduleconfig.BaseModuleConfig;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.StringField;
import com.facilio.modules.fields.SystemEnumField;

import java.util.*;

public class AddBacnetIpPointModule extends BaseModuleConfig {

    public AddBacnetIpPointModule() {
        setModuleName(AgentConstants.BACNET_IP_POINT_MODULE);
    }

    @Override
    public void addData() throws Exception {
        FacilioModule module = getBacnetIpPointModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(module));
        addModuleChain.execute();
    }
    //1-bacnet ip
    private FacilioModule getBacnetIpPointModule() throws Exception {

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);

        FacilioModule module = new FacilioModule(AgentConstants.BACNET_IP_POINT_MODULE,
                "Bacnet IP Point",
                "BACnet_IP_Point",
                FacilioModule.ModuleType.BASE_ENTITY,
                pointModule);

        List<FacilioField> fields = new ArrayList<>();

        FacilioField controllerId = FieldFactory.getControllerIdField(module);
        controllerId.setAccessType(FacilioField.AccessType.READ.getVal());
        fields.add(controllerId);

        NumberField instanceNumber = FieldFactory.getField(AgentConstants.INSTANCE_NUMBER,"Instance Number", "INSTANCE_NUMBER", module, FieldType.NUMBER);
        instanceNumber.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(instanceNumber);

        SystemEnumField instanceType = FieldFactory.getField(AgentConstants.INSTANCE_TYPE,"Instance Type", "INSTANCE_TYPE", module, FieldType.SYSTEM_ENUM);
        instanceType.setEnumName("BacnetPointInstanceType");
        instanceType.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(instanceType);

        StringField unit = FieldFactory.getField(AgentConstants.ACTUAL_UNIT,"Actual Unit", "ACTUAL_UNIT", module, FieldType.STRING);
        unit.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(unit);

        module.setFields(fields);
        return module;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> views = new ArrayList<FacilioView>();
        views.add(getUnconfiguredBacnetIPPointView().setOrder(order++));
        views.add(getConfiguredBacnetIPPointView().setOrder(order++));
        views.add(getCommissionedBacnetIPPointView().setOrder(order++));
        views.add(getSubscribedBacnetIPPointView().setOrder(order));

        groupDetails = new HashMap<>();
        groupDetails.put(AgentConstants.NAME, AgentConstants.BACNET_IP_POINT_MODULE+"View");
        groupDetails.put(AgentConstants.DISPLAY_NAME, "Bacnet IP Point Views");
        groupDetails.put(FacilioConstants.ContextNames.MODULE_NAME, AgentConstants.BACNET_IP_POINT_MODULE);
        groupDetails.put("views", views);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        groupDetails.put("appLinkNames", appLinkNames);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private FacilioView getUnconfiguredBacnetIPPointView(){

        List<ViewField>viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.DISPLAY_NAME,"Display Name"));
        viewFields.add(new ViewField(AgentConstants.DEVICE_NAME, "Controller"));
        viewFields.add(new ViewField(AgentConstants.DESCRIPTION, "Description"));
        viewFields.add(new ViewField(AgentConstants.ACTUAL_UNIT,"Actual Unit"));
        viewFields.addAll(getBacnetPointViewFields());
        viewFields.add(new ViewField(AgentConstants.CREATED_TIME, "Created Time"));
        viewFields.add(new ViewField(AgentConstants.WRITABLE, "Writable"));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POINT_TYPE",AgentConstants.POINT_TYPE,String.valueOf(1), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CONFIGURE_STATUS",AgentConstants.CONFIGURE_STATUS, String.valueOf(3), NumberOperators.NOT_EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SUBSCRIBE_STATUS",AgentConstants.SUBSCRIBE_STATUS, String.valueOf(3), NumberOperators.NOT_EQUALS));

        FacilioView view = new FacilioView();
        view.setName("unconfigured"+AgentConstants.BACNET_IP_POINT_MODULE);
        view.setDisplayName("Unconfigured BacnetIP Points");
        view.setModuleName(AgentConstants.BACNET_IP_POINT_MODULE);
        view.setFields(viewFields);
        view.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }

    private FacilioView getConfiguredBacnetIPPointView(){
        List<ViewField>viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.DISPLAY_NAME,"Display Name"));
        viewFields.add(new ViewField(AgentConstants.DEVICE_NAME, "Controller"));
        viewFields.add(new ViewField(AgentConstants.DESCRIPTION, "Description"));
        viewFields.add(new ViewField(AgentConstants.ACTUAL_UNIT,"Actual Unit"));
        viewFields.add(new ViewField(AgentConstants.DATA_INTERVAL, "Interval"));
        viewFields.addAll(getBacnetPointViewFields());
        viewFields.add(new ViewField(AgentConstants.CREATED_TIME, "Created Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_TIME, "Last Recorded Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_VALUE, "Last Recorded Value"));
        viewFields.add(new ViewField(AgentConstants.WRITABLE, "Writable"));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POINT_TYPE",AgentConstants.POINT_TYPE,String.valueOf(1), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("CONFIGURE_STATUS",AgentConstants.CONFIGURE_STATUS,String.valueOf(PointEnum.ConfigureStatus.CONFIGURED.getIndex()),NumberOperators.EQUALS));

        FacilioView view = new FacilioView();
        view.setName("configured"+AgentConstants.BACNET_IP_POINT_MODULE);
        view.setDisplayName("Configured BacnetIP Points");
        view.setModuleName(AgentConstants.BACNET_IP_POINT_MODULE);
        view.setFields(viewFields);
        view.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private FacilioView getCommissionedBacnetIPPointView(){
        List<ViewField>viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.DISPLAY_NAME,"Display Name"));
        viewFields.add(new ViewField(AgentConstants.DEVICE_NAME, "Controller"));
        viewFields.addAll(getBacnetPointViewFields());
        viewFields.add(new ViewField(AgentConstants.DATA_INTERVAL, "Interval"));
        viewFields.add(new ViewField(AgentConstants.ASSET_CATEGORY_ID, "Category"));
        viewFields.add(new ViewField(AgentConstants.RESOURCE_ID,"Asset"));
        viewFields.add(new ViewField(AgentConstants.FIELD_ID,"Reading"));
        viewFields.add(new ViewField(AgentConstants.UNIT, "Unit"));
        viewFields.add(new ViewField(AgentConstants.CREATED_TIME, "Created Time"));
        viewFields.add(new ViewField(AgentConstants.MAPPED_TIME, "Mapped Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_TIME, "Last Recorded Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_VALUE, "Last Recorded Value"));
        viewFields.add(new ViewField(AgentConstants.WRITABLE, "Writable"));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POINT_TYPE",AgentConstants.POINT_TYPE,String.valueOf(1), NumberOperators.EQUALS));
        criteria.addAndCondition(getResourceCondition());
        criteria.addAndCondition(getFieldCondition());

        FacilioView view = new FacilioView();
        view.setName("commissioned"+AgentConstants.BACNET_IP_POINT_MODULE);
        view.setDisplayName("Commissioned BacnetIP Points");
        view.setModuleName(AgentConstants.BACNET_IP_POINT_MODULE);
        view.setFields(viewFields);
        view.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private FacilioView getSubscribedBacnetIPPointView(){
        List<ViewField>viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.DISPLAY_NAME,"Display Name"));
        viewFields.add(new ViewField(AgentConstants.DEVICE_NAME, "Controller"));
        viewFields.add(new ViewField(AgentConstants.DESCRIPTION, "Description"));
        viewFields.addAll(getBacnetPointViewFields());
        viewFields.add(new ViewField(AgentConstants.CREATED_TIME, "Created Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_TIME, "Last Recorded Time"));
        viewFields.add(new ViewField(AgentConstants.LAST_RECORDED_VALUE, "Last Recorded Value"));
        viewFields.add(new ViewField(AgentConstants.WRITABLE, "Writable"));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("POINT_TYPE",AgentConstants.POINT_TYPE,String.valueOf(1), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("SUBSCRIBE_STATUS",AgentConstants.SUBSCRIBE_STATUS, String.valueOf(3), NumberOperators.EQUALS));

        FacilioView view = new FacilioView();
        view.setName("subscribed"+AgentConstants.BACNET_IP_POINT_MODULE);
        view.setDisplayName("Subscribed BacnetIP Points");
        view.setModuleName(AgentConstants.BACNET_IP_POINT_MODULE);
        view.setFields(viewFields);
        view.setCriteria(criteria);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private List<ViewField> getBacnetPointViewFields() {
        List<ViewField> viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.INSTANCE_TYPE,"Instance Type"));
        viewFields.add(new ViewField(AgentConstants.INSTANCE_NUMBER,"Instance Number"));
        return viewFields;
    }
    private Condition getResourceCondition(){
        Condition resourceCondition = new Condition();
        resourceCondition.setColumnName("RESOURCE_ID");
        resourceCondition.setFieldName(AgentConstants.RESOURCE_ID);
        resourceCondition.setOperator(CommonOperators.IS_NOT_EMPTY);
        return resourceCondition;
    }

    private Condition getFieldCondition(){
        Condition fieldCondition = new Condition();
        fieldCondition.setColumnName("FIELD_ID");
        fieldCondition.setFieldName(AgentConstants.FIELD_ID);
        fieldCondition.setOperator(CommonOperators.IS_NOT_EMPTY);
        return fieldCondition;
    }
}
