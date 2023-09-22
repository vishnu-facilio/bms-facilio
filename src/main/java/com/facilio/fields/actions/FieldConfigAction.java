package com.facilio.fields.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.util.FieldsConfigChainUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FieldConfigAction extends FacilioAction {
    private String moduleName;
    private String fieldListType;
    private List<Long> defaultFieldIds;

    public String fetchFieldList() throws Exception {
        FieldListType fieldListTypeEnum = FieldListType.getValueOfType(fieldListType);
        FacilioContext fieldsContext = FieldsConfigChainUtil.fetchFieldList(getModuleName(), fieldListTypeEnum, defaultFieldIds);

        setResult(FacilioConstants.ContextNames.FIELDS, fieldsContext.get(FacilioConstants.ContextNames.FIELDS));

        Object supplementMap = fieldsContext.get(FacilioConstants.ContextNames.SUPPLEMENTS);
        setResult(FacilioConstants.FieldsConfig.SUPPLEMENTS, supplementMap);

        return SUCCESS;
    }
}
