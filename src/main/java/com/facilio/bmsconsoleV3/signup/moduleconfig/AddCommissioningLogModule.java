package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;

import java.util.*;


public class AddCommissioningLogModule extends SignUpData {

    @Override
    public void addData() throws Exception {
        FacilioModule commissioningModule = addCommissioningModule();

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(commissioningModule));
        addModuleChain.execute();
    }

    private FacilioModule addCommissioningModule() throws Exception{
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule(FacilioConstants.ContextNames.COMMISSIONING_LOG,
                "Commissioning Log",
                "CommissioningLog",
                FacilioModule.ModuleType.SUPPORTING_ENTITY
        );

        List<FacilioField> fields = new ArrayList<>();

        LookupField agentId  = (LookupField) FieldFactory.getDefaultField(AgentConstants.AGENT,"Agent","AGENT_ID", module, FieldType.LOOKUP);
        agentId.setSpecialType(AgentConstants.AGENT);
        agentId.setLookupModule(ModuleFactory.getNewAgentModule());
        agentId.setDisplayType(FacilioField.FieldDisplayType.LOOKUP_SIMPLE);
        agentId.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(agentId);

        NumberField controllerType = (NumberField) FieldFactory.getDefaultField("controllerType", "Controller Type", "CONTROLLER_TYPE", FieldType.NUMBER);
        controllerType.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
        controllerType.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(controllerType);

        StringField pointJSON = FieldFactory.getDefaultField("pointJsonStr","Point JSON","POINT_JSON",FieldType.STRING);
        pointJSON.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        pointJSON.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(pointJSON);

        StringField clientMetaJSON = FieldFactory.getDefaultField("clientMetaStr","Client Meta JSON","CLIENT_META_JSON",FieldType.STRING);
        clientMetaJSON.setDisplayType(FacilioField.FieldDisplayType.TEXTAREA);
        clientMetaJSON.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(clientMetaJSON);

        BooleanField logical = FieldFactory.getDefaultField("logical","Logical","LOGICAL",FieldType.BOOLEAN);
        logical.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        logical.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(logical);

        BooleanField prefillMLData = FieldFactory.getDefaultField("prefillMlData","Prefill Meta Data","PREFILL_ML_DATA",FieldType.BOOLEAN);
        prefillMLData.setDisplayType(FacilioField.FieldDisplayType.DECISION_BOX);
        prefillMLData.setAccessType(calculateAccessType(FacilioField.AccessType.READ));
        fields.add(prefillMLData);

        DateField publishedTime = FieldFactory.getDefaultField("publishedTime","Published Time","PUBLISHED_TIME",FieldType.DATE_TIME);
        publishedTime.setDisplayType(FacilioField.FieldDisplayType.DATETIME);
        publishedTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(publishedTime);

        FacilioField createdTime = FieldFactory.getSystemField("sysCreatedTime", module);
        createdTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(createdTime);

        FacilioField modifiedTime = FieldFactory.getSystemField("sysModifiedTime",module);
        modifiedTime.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(modifiedTime);

        LookupField createdBy =(LookupField) FieldFactory.getSystemField("sysCreatedBy",module);
        createdBy.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(createdBy);

        LookupField modifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy",module);
        modifiedBy.setAccessType(calculateAccessType(FacilioField.AccessType.READ,FacilioField.AccessType.CRITERIA));
        fields.add(modifiedBy);

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