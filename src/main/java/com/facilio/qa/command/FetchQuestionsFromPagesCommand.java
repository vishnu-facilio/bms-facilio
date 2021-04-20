package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QAndATemplateContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchQuestionsFromPagesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<PageContext> pages = Constants.getRecordList((FacilioContext) context);
        QAndAUtil.fetchChildrenFromParent(pages,
                FacilioConstants.QAndA.QUESTION,
                "page",
                QuestionContext.class,
                p -> p.getPage().getId(),
                QuestionContext::setPage,
                PageContext::setQuestions,
                q -> q.setParent(null));

        return false;
    }
}
