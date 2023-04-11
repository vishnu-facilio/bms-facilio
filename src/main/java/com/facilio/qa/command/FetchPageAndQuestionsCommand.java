package com.facilio.qa.command;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class FetchPageAndQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long pageId = (Long) context.get(FacilioConstants.QAndA.Command.PAGE_ID);
        V3Util.throwRestException(pageId == null || pageId <= 0, ErrorCode.VALIDATION_ERROR, "Invalid page id");
        PageContext page = V3RecordAPI.getRecord(FacilioConstants.QAndA.PAGE, pageId);
        V3Util.throwRestException(page == null, ErrorCode.VALIDATION_ERROR, "Invalid page id");
        QAndAUtil.populateQuestionsInPages(Collections.singletonList(page));
        context.put(FacilioConstants.QAndA.PAGE, page);
        context.put(FacilioConstants.QAndA.Command.QUESTION_LIST, page.getQuestions());
        context.put(FacilioConstants.QAndA.Command.TEMPLATE_ID, page.getParent().getId());

        return false;
    }
}
