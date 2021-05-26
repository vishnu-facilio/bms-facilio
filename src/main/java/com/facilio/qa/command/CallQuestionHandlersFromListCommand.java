package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.QuestionType;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ExtendedModuleUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Log4j
@AllArgsConstructor
public class CallQuestionHandlersFromListCommand extends FacilioCommand {

    private ListHandlerType handlerType;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Collection<QuestionContext> questions = Constants.getRecordList((FacilioContext) context);
        if (CollectionUtils.isEmpty(questions)) {
            questions = (Collection<QuestionContext>) context.get(FacilioConstants.QAndA.Command.QUESTION_LIST);
        }
        if (CollectionUtils.isNotEmpty(questions)) {
            Map<QuestionType, List<QuestionContext>> splitByType = ExtendedModuleUtil.splitRecordsByType(questions, q -> q.getQuestionType());
            for (Map.Entry<QuestionType, List<QuestionContext>> entry : splitByType.entrySet()) {
                QuestionType type = entry.getKey();
                if (type.getQuestionHandler() != null && CollectionUtils.isNotEmpty(entry.getValue())) {
                    callHandlerMethods((FacilioContext) context, type.getSubModuleName(), type.getQuestionHandler(), entry.getValue());
                }
            }
        }
        return false;
    }

    private void callHandlerMethods (FacilioContext context, String subModule, QuestionHandler handler, List<? extends QuestionContext> questions) throws Exception {
        switch (handlerType) {
            case BEFORE_SAVE:
                handler.validateSave(questions);
                addSaveOptions(context, subModule, handler);
                break;
            case BEFORE_UPDATE:
                handler.validateUpdate(questions);
                addSaveOptions(context, subModule, handler);
                break;
            case FETCH:
                handler.afterFetch(questions);
                break;
        }
    }

    private void addSaveOptions (FacilioContext context, String subModule, QuestionHandler handler) throws Exception {
        SaveOptions options = handler.defaultSaveOption();
        if (options != null) {
            Constants.addExtendedSaveOption(context, subModule, options);
        }
    }

    public static enum ListHandlerType {
        BEFORE_SAVE,
        BEFORE_UPDATE,
        FETCH
        ;
    }
}
