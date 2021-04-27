package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.VerifyApprovalCommand;

import com.facilio.chain.FacilioChain;

public class QAndATransactionChainFactory {
    private static FacilioChain getDefaultChain() {
        return FacilioChain.getTransactionChain();
    }

    public static FacilioChain commonQAndABeforeSave() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddDefaultQAndAPropsCommand());
//        c.addCommand(new AddPagesAndQuestionsCommand());
        return c;
    }
    
    public static FacilioChain inspectionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }
    
    public static FacilioChain inspectionAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteInspectionTriggersCommand());
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }
    
    public static FacilioChain inspectionTriggerBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }

    public static FacilioChain beforePageSave() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidatePageAddAndUpdatePos());
        return c;
    }

    public static FacilioChain beforePageUpdate() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidatePageUpdateAndUpdatePos());
        return c;
    }

    public static FacilioChain beforePageDelete() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdatePagePosAndDeleteQuestions());
        return c;
    }

    public static FacilioChain beforeQuestionDelete() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateQuestionPosOnDelete());
        return c;
    }

    public static FacilioChain beforeQuestionSave() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.VALIDATE_SAVE));
        c.addCommand(new ValidateQuestionAddAndUpdatePos());
        return c;
    }

    public static FacilioChain afterQuestionSave() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CallQuestionHandlersFromModuleMapCommand(CallQuestionHandlersFromModuleMapCommand.ModuleMapHandlerType.SAVE));
        return c;
    }

    public static FacilioChain beforeQuestionUpdate() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.VALIDATE_UPDATE));
        c.addCommand(new ValidateQuestionUpdateAndUpdatePos());
        return c;
    }

    public static FacilioChain afterQuestionUpdate() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new CallQuestionHandlersFromModuleMapCommand(CallQuestionHandlersFromModuleMapCommand.ModuleMapHandlerType.UPDATE));
        return c;
    }

	public static FacilioChain inspectionCategoryBeforeSaveChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new InspectionCategoryBeforeSaveCommand());
        return c;
	}
	
	public static FacilioChain inspectionPriorityBeforeSaveChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new InspectionPriorityBeforeSaveCommand());
        return c;
	}

	public static FacilioChain addOrUpdateAnswersChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateResponseStatus());
        c.addCommand(new VerifyApprovalCommand());
        c.addCommand(new ConstructAnswerPOJOsCommand());
        c.addCommand(new AddAnswersCommand());
        c.addCommand(new UpdateAnswersCommand());
        c.addCommand(new ConstructAnswerResponseCommand());
        return c;
    }

    public static FacilioChain executeTemplateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteQAndATemplateCommand());
        return c;
    }
}
