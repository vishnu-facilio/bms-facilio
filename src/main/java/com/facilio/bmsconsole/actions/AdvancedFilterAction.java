package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;

public class AdvancedFilterAction extends FacilioAction {

    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
    private String module;

    public String fetchFilterableFields() throws Exception {
        FacilioChain filterableFields = ReadOnlyChainFactory.getFilterableFields();
        filterableFields.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, module);
        filterableFields.getContext().put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
        filterableFields.execute();

        setResult("fields", filterableFields.getContext().get(FacilioConstants.Filters.FILTER_FIELDS));
        return SUCCESS;
    }
}
