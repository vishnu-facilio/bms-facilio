package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ExtendedModuleUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class FetchExtendedQuestionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuestionContext> questions = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(questions)) {
            ExtendedModuleUtil.replaceWithExtendedRecords(questions, q -> q.getQuestionType().getSubModuleName());
        }

        return false;
    }
}
