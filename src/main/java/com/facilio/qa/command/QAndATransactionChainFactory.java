package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.VerifyApprovalCommand;
import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.chain.FacilioChain;
import com.facilio.qa.rules.commands.ExecuteQAndAActionRules;
import com.facilio.qa.rules.commands.ExecuteQAndAScoringRules;

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

    public static FacilioChain inspectionTemplateBeforeFetchChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new InspectionSupplementSupplyCommand());
        c.addCommand(new LoadInspectionExtraFieldsCommand());
        return c;
    }
    
    public static FacilioChain surveyTemplateBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonQAndABeforeSave());
        c.addCommand(new SurveyTemplateBeforeSaveCommand());
        return c;
    }
    
    public static FacilioChain inductionTemplateBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonQAndABeforeSave());
        c.addCommand(new InductionTemplateBeforeSaveCommand());
        return c;
    }

    public static FacilioChain workOrderSurveyTemplateBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(commonQAndABeforeSave());
        c.addCommand(new WorkOrderSurveyTemplateBeforeSaveCommand());
        return c;
    }

    public static FacilioChain commonBeforeQAndAResponseUpdate() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteOnSubmitProcessOfResponse());
        return c;
    }

    public static FacilioChain onSubmitProcessOfResponse() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchQuestionsAndAnswersOfResponse());
        c.addCommand(new ValidateMandatoryQuestions());

        // For now doing this before update because total score and full score can be updated in the current update flow. Also therefore can be used in stateflows too
        // So not adding roll-up field entry for total score and full score. We should add when we execute scoring rules while saving answer itself
        // Also when we implement action rules, that should be executed in post transaction
        c.addCommand(new ExecuteQAndAScoringRules());
        c.addCommand(new UpdateAnswerScoreCommand());
        c.addCommand(new ExecuteQAndAActionRules());

        return c;
    }
    
    public static FacilioChain inspectionAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddInspectionTriggersCommand());
        return c;
    }
    
    public static FacilioChain surveyAfterSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSurveyTriggersCommand());
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
    
    public static FacilioChain surveyAfterUpdateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new SurveyCheckForActiveandInActiveStateFlowCommand());
        c.addCommand(new DeleteSurveyTriggersCommand());
        c.addCommand(new AddSurveyTriggersCommand());
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
    
    public static FacilioChain surveyTriggerBeforeSaveChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new AddSurveyTriggersCommand());
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
        c.addCommand(new SetResponseForStatusChange());
        c.addCommand(new UpdateResponseStateToPartialAnswered());
        c.addCommand(new ConstructActivityForAddOrUpdateAnswerCommand());
        c.addCommand(new AddActivitiesCommandV3());
        return c;
    }

    public static FacilioChain beforeAnswerDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchResponsesForStatusUpdateOnAnswerDelete());
        return c;
    }

    public static FacilioChain afterAnswerDeleteChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new UpdateResponseStateToPartialAnswered());
        return c;
    }

    public static FacilioChain executeTemplateChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new ExecuteQAndATemplateCommand());
        return c;
    }

	public static FacilioChain executeSurveyTemplateChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ExecuteSurveyTemplateCommand());
		return c;
	}

    public static FacilioChain clonePageChain() {
        FacilioChain c = getDefaultChain();
        c.addCommand(new FetchPageAndQuestionsCommand());
        c.addCommand(new ClonePageAndQuestionsCommand());
        return c;
    }

	public static FacilioChain addOrUpdateMatrixAnswersChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new AddOrUpdateMatrixAnswersCommand());
        return c;
	}
	
	public static FacilioChain addOrUpdateDisplayLogicChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new PrepareDisplayLogicforAddOrUpdate());
        c.addCommand(new AddOrUpdateDisplayLogicCommand());
        c.addCommand(new AddDisplayLogicDependentsCommand());
        return c;
	}

	public static FacilioChain deleteDisplayLogicChain() {
		FacilioChain c = getDefaultChain();
        c.addCommand(new DeleteDisplayLogicCommand());
        return c;
	}
	
	public static FacilioChain executeDisplayLogicChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new ValidateResponseStatus());
        c.addCommand(new PrepareQuestionForDisplayLogicExecution());
        c.addCommand(new FetchDisplayLogicForExecution());
        c.addCommand(new PrepareAnswersForDisplayLogicExecution());
        c.addCommand(new ExecuteDisplayLogicRules());
        return c;
	}
	
	public static FacilioChain getDisplayLogicListChain() {
		FacilioChain c = getDefaultChain();
		c.addCommand(new FetchDisplayLogicListCommand());
        return c;
	}

	public static FacilioChain updateResponseRetakeExpiryChain(){
		FacilioChain c = getDefaultChain();
		c.addCommand(new UpdateResponseRetakeExpiryCommand());
		return c;
	}
}
