package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.QuestionType;
import com.facilio.v3.context.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j
@AllArgsConstructor
public class CallQuestionHandlersFromModuleMapCommand extends FacilioCommand {

    private ModuleMapHandlerType handlerType;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        Set<String> extendedModules = Constants.getExtendedModules(context);
        for (String extendedModule : extendedModules) {
            QuestionType type = QuestionType.getTypeFromSubModule(extendedModule);
            if (type.getQuestionHandler() != null) {
                List<? extends QuestionContext> questions = Constants.getRecordList(recordMap, extendedModule);
                if (CollectionUtils.isNotEmpty(questions)) {
                    callHandlerMethods(type.getQuestionHandler(), questions);
                }
            }
        }
        return false;
    }

    private void callHandlerMethods (QuestionHandler handler, List<? extends QuestionContext> questions) throws Exception {
        switch (handlerType) {
            case SAVE:
                handler.afterSave(questions);
                break;
            case UPDATE:
                handler.afterUpdate(questions);
                break;
        }
    }

    public static enum ModuleMapHandlerType {
        SAVE,
        UPDATE,
        ;
    }
}
