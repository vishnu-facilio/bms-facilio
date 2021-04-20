package com.facilio.qa.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QAndAType;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class AddDefaultQAndAPropsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<QAndATemplateContext> list = Constants.getRecordList(recordMap, moduleName);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        QAndAType type = QAndAType.getQAndATypeFromTemplateModule(moduleName);
        V3Util.throwRestException(type == null, ErrorCode.VALIDATION_ERROR, "Invalid module for q and a");
        FacilioModule responseModule = modBean.getModule(type.getResponseModule());
        FacilioUtil.throwIllegalArgumentException(responseModule == null, MessageFormat.format("Invalid response module for type {0}. This is not supposed to happen", type.getValue()));

        for (QAndATemplateContext template : list) {
            template.setTypeEnum(type);
            template.setResponseModuleId(responseModule.getModuleId());
        }

        return false;
    }
}
