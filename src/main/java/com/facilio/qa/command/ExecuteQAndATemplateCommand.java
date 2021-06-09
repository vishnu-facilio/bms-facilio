package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.qa.context.ResponseContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

public class ExecuteQAndATemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        FacilioModule module = Constants.getModBean().getModule(moduleName);
        V3Util.throwRestException(module == null || module.getTypeEnum() != FacilioModule.ModuleType.Q_AND_A, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid Q And A Module ({0}) specified while executing template", moduleName));
        Long id = Constants.getRecordId(context);
        QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);
        QAndATemplateContext template = V3RecordAPI.getRecord(moduleName, id, type.getTemplateClass());
        V3Util.throwRestException(template == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid id ({0}) specified while executing template", id));
        List response = template.constructResponses();

        QAndAUtil.addRecordViaV3Chain(type.getResponseModule(), response);
        context.put(FacilioConstants.QAndA.RESPONSE, response);

        return false;
    }
}
