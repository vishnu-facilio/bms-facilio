package com.facilio.delivery.action;

import com.facilio.bmsconsoleV3.commands.DeliveryDataParserCommand;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.commands.AttachmentCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;

import java.util.List;
import java.util.Map;

public class DeliveryParser  extends V3Action {
    
    public String deliveryParserAction() throws Exception {
        FacilioChain transactionChain = FacilioChain.getTransactionChain();
        transactionChain.addCommand(new AttachmentCommand());
        transactionChain.addCommand(new DeliveryDataParserCommand());
        FacilioContext context = transactionChain.getContext();

        Constants.setAttachmentFileNames(context, this.getFilesFileName());
        Constants.setAttachmentContentTypes(context, this.getFilesContentType());
        Constants.setAttachmentFileList(context, this.getFiles());

        transactionChain.execute(context);
        if(context.containsKey("parserResult")){
            if(Long.parseLong(context.get("parserResult").toString()) == -1) {
                this.setCode(ErrorCode.DELIVERY_PARSING_FAILED.getCode());
            } else if(Long.parseLong(context.get("parserResult").toString()) == 1) {
                this.setCode(ErrorCode.DELIVERY_PARSING_SUCCESS.getCode());
            } else if(Long.parseLong(context.get("parserResult").toString()) == 2) {
                this.setCode(ErrorCode.DELIVERY_PARSING_EMPLOYEE_NOT_FOUND.getCode());
            }
        }

        Map<String, Long> attachmentNameVsId = Constants.getAttachmentNameVsId(context);
        List<Map<String, Object>> deliveries = (List<Map<String, Object>>) context.get("deliveries");
        this.setData("deliveries", deliveries);
        this.setData("attachmentNameVsId", attachmentNameVsId);
        return SUCCESS;
    }
}
