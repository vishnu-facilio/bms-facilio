package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.agent.AgentKeys;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgentDataProcessingLoggerModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        FacilioModule agentDataSummaryModule = agentDataProcessingLoggerModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(agentDataSummaryModule));
        //change parent module name to whatever you're changing dont miss this

        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE,FacilioConstants.ContextNames.AGENT_DATA_LOGGER);
        addModuleChain.execute();
    }

    private FacilioModule agentDataProcessingLoggerModule() throws Exception{
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        //change name now
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.AGENT_DATA_PROCESSING_LOGGER,
                "Agent Data Processing Logger",
                "AgentData_Processing_Logger",
                FacilioModule.ModuleType.SUPPORTING_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();
        //asset,reading,point,value,status,recordId,controllerId

        NumberField recordId = FieldFactory.getDefaultField(AgentKeys.RECORD_ID,"Record Id", "RECORD_ID", FieldType.NUMBER);
        recordId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        recordId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(recordId);

        NumberField controllerId = FieldFactory.getDefaultField(AgentKeys.CONTROLLER_ID,"Controller Id", "CONTROLLER_ID",FieldType.NUMBER);
        controllerId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        controllerId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(controllerId);

        NumberField parentId = FieldFactory.getDefaultField(AgentConstants.PARENT_ID,"Parent Id", "PARENT_ID",FieldType.NUMBER);
        parentId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        parentId.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(parentId);

        StringField point = FieldFactory.getDefaultField(AgentConstants.POINT,"Point","POINT",FieldType.STRING);
        point.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        point.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(point);

        LookupField assetId = (LookupField) FieldFactory.getDefaultField("asset","Asset","ASSET_ID",module,FieldType.LOOKUP);
        assetId.setLookupModule(moduleBean.getModule(FacilioConstants.ContextNames.RESOURCE));
        assetId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        assetId.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(assetId);

        NumberField reading = (NumberField) FieldFactory.getDefaultField("readingId","Reading Id","READING_ID",FieldType.NUMBER);
        reading.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        reading.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(reading);

        StringField value = FieldFactory.getDefaultField("value","Value", "VALUE",FieldType.STRING);
        value.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        value.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(value);

        SystemEnumField status = (SystemEnumField) FieldFactory.getDefaultField("status","Status","STATUS",FieldType.SYSTEM_ENUM);
        status.setEnumName("TimeSeries_Status");
        status.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(status);

        StringField errorStackTrace = FieldFactory.getDefaultField("errorStackTrace","ErrorStackTrace","ERROR_STACK_TRACE",FieldType.STRING);
        errorStackTrace.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        errorStackTrace.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(errorStackTrace);

        SystemEnumField readingScope = (SystemEnumField) FieldFactory.getDefaultField(AgentConstants.READING_SCOPE, "Scope", "SCOPE", module, FieldType.SYSTEM_ENUM);
        readingScope.setEnumName("ConnectedResourceAssignmentType");
        readingScope.setValues(FacilioEnum.getEnumValues(readingScope.getEnumName()));
        readingScope.setAccessType(FacilioField.AccessType.READ.getVal()+FacilioField.AccessType.CRITERIA.getVal());
        fields.add(readingScope);

        NumberField resourceId = FieldFactory.getDefaultField(AgentConstants.RESOURCE_ID,"Resource Id", "RESOURCE_ID",FieldType.NUMBER);
        resourceId.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        resourceId.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(resourceId);


        module.setFields(fields);
        return module;
    }
    private long calculateAccessType(FacilioField.AccessType... accessTypes) {
        long result = 0;
        for (FacilioField.AccessType accessType : accessTypes) {
            result += accessType.getVal();
        }
        return result;
    }
}
