package com.facilio.bmsconsoleV3.commands.termsandconditions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReviseTandCCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("revise")) {
            String moduleName = Constants.getModuleName(context);
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<V3TermsAndConditionContext> list = recordMap.get(moduleName);
            if (CollectionUtils.isNotEmpty(list)) {
                for (V3TermsAndConditionContext termsAndConditionContext : list) {
                    if (!termsAndConditionContext.isPublished()) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Only published record can be revised");
                    }
                }
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

                List<V3TermsAndConditionContext> revisedList = new ArrayList<>();
                for (V3TermsAndConditionContext terms : list) {
                    if (terms.getId() <= 0) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parent record Id is needed for revising the terms and conditions");
                    }
                    V3TermsAndConditionContext exitingQuotation = V3RecordAPI.getRecord(moduleName, terms.getId(), V3TermsAndConditionContext.class);
                    terms.setIsRevised(true);
                    V3RecordAPI.updateRecord(terms, module, Arrays.asList(fieldsMap.get("isRevised")));

                    V3TermsAndConditionContext revisedTerms = terms.clone();
                   revisedTerms.setParentId(exitingQuotation.getId());
                    revisedList.add(revisedTerms);
                }
                recordMap.put(moduleName, revisedList);
            }
        }

        return false;
    }
}

