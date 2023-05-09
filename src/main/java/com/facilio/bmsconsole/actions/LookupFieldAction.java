package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;

public class LookupFieldAction extends FacilioAction{

    private String moduleName;
    public String getModuleName() {
        return moduleName;
    }
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
   private boolean isOneLevelFields;

    public boolean isOneLevelFields() {
        return isOneLevelFields;
    }

    public void setOneLevelFields(boolean oneLevelFields) {
        isOneLevelFields = oneLevelFields;
    }

    public String lookupFieldList() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getLookupFieldList();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
        context.put(FacilioConstants.ContextNames.IS_ONE_LEVEL_FIELDS,isOneLevelFields());
        chain.execute();
        setResult(FacilioConstants.ContextNames.FIELDS, context.get(FacilioConstants.ContextNames.FIELDS));
        return SUCCESS;
    }
}
