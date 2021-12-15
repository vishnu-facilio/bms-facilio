package com.facilio.bmsconsoleV3.commands.termsandconditions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.V3TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CheckForPublishedCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TermsAndConditionContext> termsAndConditionContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(termsAndConditionContexts)) {
            for (V3TermsAndConditionContext termsAndConditionContext : termsAndConditionContexts) {
                Map<String, Object> bodyParams = Constants.getBodyParams(context);
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("publish")) {
                    termsAndConditionContext.setIsPublished(true);
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TERMS_AND_CONDITIONS);
                    List<FacilioField> fields = modBean.getAllFields(module.getName());
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    V3RecordAPI.updateRecord(termsAndConditionContext, module, Arrays.asList(fieldsMap.get("isPublished")));
                }
            }
        }

        return false;
    }
}

