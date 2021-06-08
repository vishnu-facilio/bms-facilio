package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

public class ClonePageAndQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PageContext page = (PageContext) context.get(FacilioConstants.QAndA.PAGE);
        PageContext clonedPage = page.toBuilder().position(page.getPosition()+1).questions(null).build();
        QAndAUtil.addRecordViaV3Chain(FacilioConstants.QAndA.PAGE, Collections.singletonList(clonedPage));

        List<QuestionContext> questions = page.getQuestions();
        if (CollectionUtils.isNotEmpty(questions)) {
            questions.stream().forEach(q -> updateQuestionProps(q, clonedPage));
            QAndAUtil.addRecordViaV3Chain(FacilioConstants.QAndA.QUESTION, questions);

            clonedPage.setQuestions(questions);
        }
        context.put(FacilioConstants.QAndA.PAGE, clonedPage);

        return false;
    }

    private void updateQuestionProps (QuestionContext question, PageContext clonedPage) {
        question._setId(null);
        question.setPage(clonedPage);
    }
}
