package com.facilio.bmsconsoleV3.commands.ocr;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.bmsconsoleV3.util.OcrUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.modules.*;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class PushOCRTemplateToWMSCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(PushOCRTemplateToWMSCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        try {
            Map<String, List<BillTemplateContext>> billTemplateContextMap = (Map<String, List<BillTemplateContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
            ModuleBean moduleBean = Constants.getModBean();
            FacilioModule billTemplateModule = moduleBean.getModule(moduleName);

            List<BillTemplateContext> billTemplateContextList = billTemplateContextMap.get(moduleName);

            if(CollectionUtils.isEmpty(billTemplateContextList)){
                throw new IllegalArgumentException("Bill template not found");
            }

            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            for (BillTemplateContext billTemplateContext : billTemplateContextList) {

                OcrUtil.updateTemplateStatus(BillTemplateContext.templateStatusEnum.PARSING_IN_PROGRESS, billTemplateContext);
                 
                JSONObject message = new JSONObject();
                message.put("orgId", orgId);
                message.put(FacilioConstants.Ocr.TEMPLATE_ID, billTemplateContext.getId());

                Messenger.getMessenger().sendMessage(new Message()
                        .setKey("ocrTemplateBillParser/" + billTemplateContext.getId())
                        .setOrgId(orgId)
                        .setContent(message)
                );
            }

        }catch (Exception e){
            LOGGER.debug("Error while parsing sample bill", e);
        }

        return false;
    }
}
