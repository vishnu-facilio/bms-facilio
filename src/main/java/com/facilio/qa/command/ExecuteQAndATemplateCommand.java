package com.facilio.qa.command;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecuteQAndATemplateCommand extends FacilioCommand {


    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        Map<String, Object> errorParams = new HashMap<>();
        errorParams.put("moduleName", moduleName);
        V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, "errors.qa.executeQandATemplateCommand.moduleCheck",true,errorParams);
        //V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid Q And A Module ({0}) specified while executing template", moduleName),true,errorParams);
        Long id = Constants.getRecordId(context);
        QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);
        
        FacilioContext summaryContext = V3Util.getSummary(moduleName, Collections.singletonList(id));
        
        QAndATemplateContext template = (QAndATemplateContext) Constants.getRecordListFromContext(summaryContext, moduleName).get(0);
        errorParams.put("id", id);
        V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, "errors.qa.executeQandATemplateCommand.idCheck",true,errorParams);
        //V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) specified while executing template", id),true,errorParams);

        List<ResourceContext> resources = (List<ResourceContext>) context.get(FacilioConstants.ContextNames.RESOURCE_LIST);

        List response = template.constructResponses(resources);

        if(CollectionUtils.isNotEmpty(response)) {
        	QAndAUtil.addRecordViaV3Chain(type.getResponseModule(), response);
        	context.put(FacilioConstants.QAndA.RESPONSE, response);
        }

        return false;
    }
}
