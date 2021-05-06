package com.facilio.qa.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Log4j
public class AddDefaultQAndAPropsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<QAndATemplateContext> list = Constants.getRecordList(recordMap, moduleName);

        if (AccountUtil.getCurrentOrg().getId() == 155) {
            LOGGER.info(MessageFormat.format("Template list for module ({0}) : {1}", moduleName, list));
            LOGGER.info(MessageFormat.format("Raw input from client : {0}", Constants.getRawInput(context)));
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);
        V3Util.throwRestException(type == null, ErrorCode.VALIDATION_ERROR, "Invalid module for q and a");
        FacilioModule responseModule = modBean.getModule(type.getResponseModule());
        V3Util.throwRestException(responseModule == null, ErrorCode.VALIDATION_ERROR, MessageFormat.format("Invalid response module for type {0}. This is not supposed to happen", type.getValue()));

        for (QAndATemplateContext template : list) {
            template.setQAndAType(type);
            template.setTotalPages(0);
            template.setTotalQuestions(0);
        }

        return false;
    }
}
