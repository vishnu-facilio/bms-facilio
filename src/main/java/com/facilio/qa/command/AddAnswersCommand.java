package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.QAndAUtil;
import com.facilio.qa.context.AnswerContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import java.util.List;

public class AddAnswersCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<AnswerContext> answers = (List<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWERS_TO_BE_ADDED);
        if (CollectionUtils.isNotEmpty(answers)) {
            QAndAUtil.addRecordViaChain(FacilioConstants.QAndA.ANSWER, answers);
        }

        return false;
    }
}
