package com.facilio.moduleBuilder.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.moduleBuilder.util.ModuleListConfigChainUtil;
import lombok.Setter;

import java.util.Map;

@Setter
public class ModuleListAction extends FacilioAction {
    private String feature;
    private long appId;
    private String moduleName;

    public String fetchModuleList() throws Exception {
        FacilioChain chain = ModuleListConfigChainUtil.getModuleListChain(feature, appId);
        FacilioContext context = chain.getContext();
        chain.execute();
        Map<String, Object> resultMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RESULT);
        setResult(FacilioConstants.ContextNames.MODULE_LIST, resultMap.get(FacilioConstants.ContextNames.MODULE_LIST));
        setResult("subModules", resultMap.get("subModules"));
        return SUCCESS;
    }

    public String fetchModuleDetails() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getResponseFieldModuleMeta();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();
        setResult(FacilioConstants.ContextNames.MODULE, context.get(FacilioConstants.ContextNames.MODULE));
        return SUCCESS;
    }
}
