package com.facilio.qa.command;

import com.facilio.chain.FacilioChain;

public class QAndAReadOnlyChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getNonTransactionChain();
    }

    public static FacilioChain afterQuestionFetch() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchExtendedQuestionsCommand());
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.FETCH));
        return c;
    }

    public static FacilioChain afterPagesFetch() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchQuestionsFromPagesCommand());
        c.addCommand(new FetchAnswersForQuestionsCommand());
        return c;
    }

    public static FacilioChain commonAfterQAndATemplateFetch() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPagesFromTemplateCommand());
        return c;
    }
    
    public static FacilioChain commonAfterInspectionTemplateFetch() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonAfterQAndATemplateFetch());
        c.addCommand(new FetchRelatedItemsForInspectionTemplateCommand());
        return c;
    }

    public static FacilioChain callQuestionFetchHandlers() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.FETCH));
        return c;
    }

    public static FacilioChain beforeAnswerFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddAnswerSupplementsCommand());
        return c;
    }

    public static FacilioChain afterAnswerFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SerializeAnswersCommand());
        return c;
    }
}
