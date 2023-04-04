package com.facilio.apiv3;

import java.util.function.Supplier;

import com.facilio.bmsconsoleV3.commands.AddActivitiesCommandV3;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTemplateContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyResponseContext;
import com.facilio.bmsconsoleV3.context.workordersurvey.WorkOrderSurveyTemplateContext;
import com.facilio.bmsconsoleV3.interfaces.customfields.ModuleCustomFieldCount50;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;
import com.facilio.v3.commands.ConstructAddCustomActivityCommandV3;

@Config
public class ExtendedQAndAV3Config {

    /**
     * All modules extending Q and A template should make sure to include the following
     *
     *     .create()
     *     .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())
     *
     *     .summary()
     *     .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndATemplateFetch())
     *
     * All modules extending Q and A response should make sure to include the following
     *      .update()
     *      .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
     *
     * Make sure to add them directly or include them in the chain of corresponding hooks
     *
     */


    @Module(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
    public static Supplier<V3Config> getInspection() {
        return () -> new V3Config(InspectionTemplateContext.class, new ModuleCustomFieldCount50())
                .create()
                .beforeSave(QAndATransactionChainFactory.inspectionTemplateBeforeSaveChain())
                .afterSave(QAndATransactionChainFactory.inspectionAfterSaveChain())
                .update()
                .beforeSave(QAndATransactionChainFactory.inspectionTemplateBeforeSaveChain())
                .afterSave(QAndATransactionChainFactory.inspectionAfterUpdateChain())
                .list()
                .beforeFetch( QAndATransactionChainFactory.inspectionTemplateBeforeFetchChain())
                .delete().afterDelete(new DeleteInspectionTriggerCommand())
                .summary()
                .beforeFetch(new InspectionSupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.afterInspectionTemplateFetch())
                .build();
    }
    
    @Module(FacilioConstants.Inspection.INSPECTION_RESPONSE)
    public static Supplier<V3Config> getInspectionResponse() {
        return () -> new V3Config(InspectionResponseContext.class, new ModuleCustomFieldCount50())
        		.create()
        		.afterSave(new ConstructAddCustomActivityCommandV3())
        		.afterTransaction(new AddActivitiesCommandV3())
                .update()
                .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
                .afterTransaction(new AddActivitiesCommandV3())
        		.list()
        		.beforeFetch(new InspectionSupplementSupplyCommand())
        		.afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndAResponseListFetch())
        		.summary()
        		.beforeFetch(new InspectionSupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndAResponseFetch())
                .build();
    }

    @Module(FacilioConstants.Inspection.INSPECTION_TRIGGER)
    public static Supplier<V3Config> getInspectionTriggers() {
        return () -> new V3Config(InspectionTriggerContext.class, null)
        		.create()
                	.beforeSave(QAndATransactionChainFactory.inspectionTriggerBeforeSaveChain())
                .update()
                	.beforeSave(QAndATransactionChainFactory.inspectionTriggerBeforeSaveChain())
                .build();
    }
    
    @Module(FacilioConstants.Inspection.INSPECTION_CATEGORY)
    public static Supplier<V3Config> getInspectionCategory() {
        return () -> new V3Config(InspectionCategoryContext.class, null)
        		.create()
        			.beforeSave(QAndATransactionChainFactory.inspectionCategoryBeforeSaveChain())
        		.build();
    }
    
    @Module(FacilioConstants.Inspection.INSPECTION_PRIORITY)
    public static Supplier<V3Config> getInspectionPriority() {
        return () -> new V3Config(InspectionPriorityContext.class, null)
        		.create()
    			.beforeSave(QAndATransactionChainFactory.inspectionPriorityBeforeSaveChain())
    		.build();
    }
    
    @Module(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL)
    public static Supplier<V3Config> getInspectionTriggerInclExcl() {
        return () -> new V3Config(InspectionTriggerIncludeExcludeResourceContext.class, null);
    }
    
    
    
    @Module(FacilioConstants.Survey.SURVEY_TEMPLATE)
    public static Supplier<V3Config> getSurvey() {
        return () -> new V3Config(SurveyTemplateContext.class, new ModuleCustomFieldCount50())
                .create()
                .beforeSave(QAndATransactionChainFactory.surveyTemplateBeforeSaveChain())
                .afterSave(QAndATransactionChainFactory.surveyAfterSaveChain())
                .update()
                .afterSave(QAndATransactionChainFactory.surveyAfterUpdateChain())
                .list()
                .beforeFetch(new SurveySupplementSupplyCommand())
                .summary()
                .beforeFetch(new SurveySupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.afterSurveyTemplateFetch())
                .build();
    }
    
    @Module(FacilioConstants.Survey.SURVEY_RESPONSE)
    public static Supplier<V3Config> getSurveyResponse() {
        return () -> new V3Config(SurveyResponseContext.class, new ModuleCustomFieldCount50())
        		.create()
        		.afterSave(new ConstructAddCustomActivityCommandV3())
        		.afterTransaction(new AddActivitiesCommandV3())
                .update()
                .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
                .afterTransaction(new AddActivitiesCommandV3())
        		.list()
        		.beforeFetch(new SurveySupplementSupplyCommand())
        		.summary()
        		.beforeFetch(new SurveySupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndAResponseFetch())
                .build();
    }

    @Module(FacilioConstants.Survey.SURVEY_TRIGGER)
    public static Supplier<V3Config> getSurveyTriggers() {
        return () -> new V3Config(SurveyTriggerContext.class, null)
        		.create()
                	.beforeSave(QAndATransactionChainFactory.surveyTriggerBeforeSaveChain())
                .update()
                	.beforeSave(QAndATransactionChainFactory.surveyTriggerBeforeSaveChain())
                .build();
    }
    
    @Module(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL)
    public static Supplier<V3Config> getSurveyTriggerInclExcl() {
        return () -> new V3Config(SurveyTriggerIncludeExcludeResourceContext.class, null);
    }
    
    
    @Module(FacilioConstants.Induction.INDUCTION_TEMPLATE)
    public static Supplier<V3Config> getInduction() {
        return () -> new V3Config(InductionTemplateContext.class, new ModuleCustomFieldCount50())
                .create()
                .beforeSave(QAndATransactionChainFactory.inductionTemplateBeforeSaveChain())
                .afterSave(QAndATransactionChainFactory.inductionAfterSaveChain())
                .update()
                .afterSave(QAndATransactionChainFactory.inductionAfterUpdateChain())
                .list()
                .beforeFetch(QAndATransactionChainFactory.inductionTemplateBeforeFetchChain())
                .summary()
                .beforeFetch(new InductionSupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.afterInductionTemplateFetch())
                .build();
    }
    
    @Module(FacilioConstants.Induction.INDUCTION_RESPONSE)
    public static Supplier<V3Config> getInductionResponse() {
        return () -> new V3Config(InductionResponseContext.class, new ModuleCustomFieldCount50())
        		.create()
        		.afterSave(new ConstructAddCustomActivityCommandV3())
        		.afterTransaction(new AddActivitiesCommandV3())
                .update()
                .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
                .afterTransaction(new AddActivitiesCommandV3())
        		.list()
        		.beforeFetch(new InductionSupplementSupplyCommand())
        		.summary()
        		.beforeFetch(new InductionSupplementSupplyCommand())
                .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndAResponseFetch())
                .build();
    }
    
    @Module(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL)
    public static Supplier<V3Config> getInductionTriggerInclExcl() {
        return () -> new V3Config(InductionTriggerIncludeExcludeResourceContext.class, null);
    }

	@Module (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_TEMPLATE)
	public static Supplier<V3Config> getWorkOrderSurvey() {
		return () -> new V3Config(WorkOrderSurveyTemplateContext.class, new ModuleCustomFieldCount50())
				.create()
				.beforeSave(QAndATransactionChainFactory.workOrderSurveyTemplateBeforeSaveChain())
				.afterSave()
				.update()
				.afterSave()
				.list()
				.summary()
				.build();
	}

	@Module (FacilioConstants.WorkOrderSurvey.WORK_ORDER_SURVEY_RESPONSE)
	public static Supplier<V3Config> getWorkOrderSurveyResponse() {
		return () -> new V3Config(WorkOrderSurveyResponseContext.class, new ModuleCustomFieldCount50())
				.create()
				.afterSave(new ConstructAddCustomActivityCommandV3())
				.afterTransaction(new AddActivitiesCommandV3())
				.update()
				.beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
				.afterTransaction(new AddActivitiesCommandV3())
				.list()
		        .beforeFetch(QAndAReadOnlyChainFactory.fetchSurveyListChain())
				.afterFetch(new FilterRetakeExpiryForSurvey())
				.summary()
				.beforeFetch(new SurveySupplementSupplyCommand())
				.afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndAResponseFetch())
				.build();
	}
}
