package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class ModuleMappingConfigAction extends V3Action {

    private String sourceModule;
    private String targetModule;
    private long templateId;
    private long recordId;
    private JSONObject record;

    public String fetchTargetModuleContext() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.fetchModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD, record);

        chain.execute();

        setData(targetModule, context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }

    public String addContextToTargetModule() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addModuleMappingConfig();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ModuleMapping.SOURCE_MODULE, sourceModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TARGET_MODULE, targetModule);
        context.put(FacilioConstants.ContextNames.ModuleMapping.TEMPLATE_ID, templateId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD_ID, recordId);
        context.put(FacilioConstants.ContextNames.ModuleMapping.RECORD, record);

        chain.execute();

        setData(targetModule, context.get(FacilioConstants.ContextNames.ModuleMapping.DATA));

        return V3Action.SUCCESS;
    }
}
