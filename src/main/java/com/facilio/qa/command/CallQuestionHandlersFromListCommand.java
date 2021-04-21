package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.QuestionType;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ExtendedModuleUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

@Log4j
@AllArgsConstructor
public class CallQuestionHandlersFromListCommand extends FacilioCommand {

    private ListHandlerType handlerType;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuestionContext> questions = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isNotEmpty(questions)) {
            Map<QuestionType, List<QuestionContext>> splitByType = ExtendedModuleUtil.splitRecordsByType(questions, q -> q.getQuestionType());
            for (Map.Entry<QuestionType, List<QuestionContext>> entry : splitByType.entrySet()) {
                QuestionType type = entry.getKey();
                if (type.getHandler() != null && CollectionUtils.isNotEmpty(entry.getValue())) {
                    callHandlerMethods(type.getHandler(), entry.getValue());
                }
            }
        }
        return false;
    }

    private void callHandlerMethods (QuestionHandler handler, List<? extends QuestionContext> questions) throws Exception {
        switch (handlerType) {
            case VALIDATE_SAVE:
                handler.validateSave(questions);
                break;
            case VALIDATE_UPDATE:
                handler.validateUpdate(questions);
                break;
            case FETCH:
                handler.afterFetch(questions);
                break;
        }
    }

    public static enum ListHandlerType {
        VALIDATE_SAVE,
        VALIDATE_UPDATE,
        FETCH
        ;
    }
}
