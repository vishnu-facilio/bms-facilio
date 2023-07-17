package com.facilio.fields.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldsAction extends FacilioAction {

    private String moduleName;


    public String fetchFilterFields() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getAdvancedFilterFieldsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.CRITERIA.getVal());
        chain.execute();
        setResult("fields", chain.getContext().get(FacilioConstants.Filters.FILTER_FIELDS));
        return SUCCESS;
    }

    public String fetchSortableFields() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getSortableFieldsChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.FIELD_ACCESS_TYPE, FacilioField.AccessType.SORT.getVal());
        chain.execute();
        setResult("fields", context.get(FacilioConstants.ContextNames.SORT_FIELDS));
        return SUCCESS;
    }
}
