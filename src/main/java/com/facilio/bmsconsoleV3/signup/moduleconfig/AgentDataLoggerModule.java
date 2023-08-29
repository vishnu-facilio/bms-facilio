package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.*;

public class AgentDataLoggerModule extends BaseModuleConfig {
    public AgentDataLoggerModule(){setModuleName(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);}
    @Override
    public void addData() throws Exception {
        FacilioModule agentDataLoggerModule = agentDataLoggerModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(agentDataLoggerModule));
        addModuleChain.execute();
    }

    private FacilioModule agentDataLoggerModule() throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // change name
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.AGENT_DATA_LOGGER,
                "Agent Data Logger",
                "AgentData_Logger",
                FacilioModule.ModuleType.SUPPORTING_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        LookupField agentId  = (LookupField) FieldFactory.getDefaultField(AgentConstants.AGENT,"Agent","AGENT_ID", module, FieldType.LOOKUP);
        agentId.setSpecialType(AgentConstants.AGENT);
        agentId.setLookupModule(ModuleFactory.getNewAgentModule());
        agentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        agentId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(agentId);

        LookupField controllerId = (LookupField) FieldFactory.getDefaultField(AgentConstants.CONTROLLER,"Controller","CONTROLLER_ID", module, FieldType.LOOKUP);
        controllerId.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.CONTROLLER));
        controllerId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        controllerId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(controllerId);

        NumberField recordId = FieldFactory.getDefaultField(AgentKeys.RECORD_ID,"Record Id", "RECORD_ID",FieldType.NUMBER);
        recordId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        recordId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(recordId);

        NumberField partitionId = FieldFactory.getDefaultField(AgentConstants.PARTITION_ID,"Partition Id", "PARTITION_ID",FieldType.NUMBER);
        partitionId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        partitionId.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(partitionId);

        StringField messageSource = FieldFactory.getDefaultField(AgentConstants.MESSAGE_SOURCE,"Message Source","MESSAGE_SOURCE",FieldType.STRING);
        messageSource.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        messageSource.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(messageSource);

        NumberField publishType = FieldFactory.getDefaultField(AgentConstants.PUBLISH_TYPE,"Publish Type", "PUBLISH_TYPE",FieldType.NUMBER);
        publishType.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        publishType.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(publishType);

        SystemEnumField msgStatus = (SystemEnumField) FieldFactory.getDefaultField("messageStatus","Message Status","MESSAGE_STATUS",FieldType.SYSTEM_ENUM);
        msgStatus.setEnumName("Agent_Message_Status");
        msgStatus.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(msgStatus);


        DateField startTime = FieldFactory.getDefaultField("startTime","Start Time","START_TIME",FieldType.DATE_TIME);
        startTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        startTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(startTime);

        DateField endTime = FieldFactory.getDefaultField("endTime","End Time","END_TIME",FieldType.DATE_TIME);
        endTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        endTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(endTime);

        StringField subjectField = FieldFactory.getDefaultField("payload","Payload","PAYLOAD",FieldType.STRING);
        subjectField.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        subjectField.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(subjectField);

        StringField errorStackTrace = FieldFactory.getDefaultField("errorStackTrace","ErrorStackTrace","ERROR_STACK_TRACE",FieldType.STRING);
        errorStackTrace.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        errorStackTrace.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(errorStackTrace);

        module.setFields(fields);
        return module;
    }
    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> views = new ArrayList<FacilioView>();
        views.add(getAgentDataLoggerView().setOrder(order));

        groupDetails = new HashMap<>();
        groupDetails.put(AgentConstants.NAME, FacilioConstants.ContextNames.AGENT_DATA_LOGGER +"View");
        groupDetails.put(AgentConstants.DISPLAY_NAME, "Agent Data Logger Views");
        groupDetails.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        groupDetails.put("views", views);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        groupDetails.put("appLinkNames", appLinkNames);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }
    private FacilioView getAgentDataLoggerView(){

        List<ViewField>viewFields = new ArrayList<>();
        viewFields.add(new ViewField(AgentConstants.AGENT,"Agent"));
        viewFields.add(new ViewField(AgentConstants.CONTROLLER,"Controller"));
        viewFields.add(new ViewField(AgentKeys.RECORD_ID,"Record Id"));
        viewFields.add(new ViewField(AgentConstants.PARTITION_ID,"Partition Id"));
        viewFields.add(new ViewField(AgentConstants.MESSAGE_SOURCE,"Message Source"));
        viewFields.add(new ViewField(AgentConstants.PUBLISH_TYPE,"Publish Type"));
        viewFields.add(new ViewField("messageStatus","Message Status"));
        viewFields.add(new ViewField("startTime","Start Time"));
        viewFields.add(new ViewField("endTime","End Time"));
        viewFields.add(new ViewField("errorStackTrace","ErrorStackTrace"));

        FacilioView view = new FacilioView();
        view.setName(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        view.setDisplayName("Agent Data Logger");
        view.setModuleName(FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        view.setFields(viewFields);

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
        view.setAppLinkNames(appLinkNames);

        return view;
    }
    private long calculateAccessType(FacilioField.AccessType... accessTypes) {
        long result = 0;
        for (FacilioField.AccessType accessType : accessTypes) {
            result += accessType.getVal();
        }
        return result;
    }
}
