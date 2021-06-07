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
        return c;
    }
    
    public static FacilioChain inspectionTemplateBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonQAndABeforeSave());
        c.addCommand(new InspectionTemplateBeforeSaveCommand());
        return c;
    }
    
    public static FacilioChain inductionTemplateBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonQAndABeforeSave());
        c.addCommand(new InductionTemplateBeforeSaveCommand());
        return c;
    }
    
    public static FacilioChain commonBeforeQAndAResponseUpdate() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ValidateResponseSubmit());
        return c;
    }
    
    public static FacilioChain inspectionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }
    
    public static FacilioChain inductionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddInductionTriggersCommand());
        return c;
    }
    
    public static FacilioChain inspectionAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InspectionCheckForActiveandInActiveStateFlowCommand());
        c.addCommand(new DeleteInspectionTriggersCommand());
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }
    
    public static FacilioChain inductionAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteInductionTriggersCommand());
        c.addCommand(new AddInductionTriggersCommand());
        c.addCommand(new InductionCheckForActiveandInActiveStateFlowCommand());
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
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.BEFORE_SAVE));
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
        c.addCommand(new CallQuestionHandlersFromListCommand(CallQuestionHandlersFromListCommand.ListHandlerType.BEFORE_UPDATE));
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
        c.addCommand(new ConstructAnswerPOJOsCommand(false));
        c.addCommand(new AddAnswersCommand());
        c.addCommand(new UpdateAnswersCommand());
        c.addCommand(new ConstructAnswerResponseCommand());
        c.addCommand(new UpdateResponseStateCommand());
        return c;
    }

    public static FacilioChain executeTemplateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteQAndATemplateCommand());
        return c;
    }
}
