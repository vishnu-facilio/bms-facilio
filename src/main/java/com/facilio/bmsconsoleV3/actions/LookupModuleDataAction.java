package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter @Setter
public class LookupModuleDataAction extends RelatedDataAction {
    private String moduleName;
    private String relatedFieldName;
    private String extendedModuleName;
    
    public String getLookupListData() throws Exception {
        FacilioChain getChain = ReadOnlyChainFactoryV3.getRelatedModuleDataChain();

        FacilioContext chainContext = getChain.getContext();
        addGetChainContext(chainContext);

        chainContext.put(FacilioConstants.ContextNames.RELATED_MODULE_NAME, moduleName);
        chainContext.put(FacilioConstants.ContextNames.RELATED_FIELD_NAME, relatedFieldName);
        chainContext.put(FacilioConstants.ContextNames.EXTENDED_MODULE_NAME, extendedModuleName);

        getChain.execute();

        setData((JSONObject) chainContext.get(FacilioConstants.ContextNames.RESULT));
        if (chainContext.containsKey(FacilioConstants.ContextNames.META)) {
            setMeta((JSONObject) chainContext.get(FacilioConstants.ContextNames.META));
        }

        return SUCCESS;
    }

}
