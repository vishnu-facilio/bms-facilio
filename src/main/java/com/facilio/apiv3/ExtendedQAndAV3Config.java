package com.facilio.apiv3;

import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerIncludeExcludeResourceContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.InductionSupplementSupplyCommand;
import com.facilio.qa.command.InspectionSupplementSupplyCommand;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

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
        return () -> new V3Config(InspectionTemplateContext.class, null)
                .create()
                .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())
                .afterSave(QAndATransactionChainFactory.inspectionAfterSaveChain())
                .update()
                .afterSave(QAndATransactionChainFactory.inspectionAfterUpdateChain())
//                .list()
//                .delete()
                .summary()
                .beforeFetch(new InspectionSupplementSupplyCommand())
                .afterFetch(new InspectionSupplementSupplyCommand())
                .build();
    }
    
    @Module(FacilioConstants.Inspection.INSPECTION_RESPONSE)
    public static Supplier<V3Config> getInspectionResponse() {
        return () -> new V3Config(InspectionResponseContext.class, null)
                .update()
                .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
        		.list()
        		.beforeFetch(new InspectionSupplementSupplyCommand())
        		.summary()
        		.beforeFetch(new InspectionSupplementSupplyCommand())
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
    
    
    
    @Module(FacilioConstants.Induction.INDUCTION_TEMPLATE)
    public static Supplier<V3Config> getInduction() {
        return () -> new V3Config(InductionTemplateContext.class, null)
                .create()
                .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())
                .afterSave(QAndATransactionChainFactory.inductionAfterSaveChain())
                .update()
                .afterSave(QAndATransactionChainFactory.inductionAfterUpdateChain())
//                .list()
//                .delete()
                .summary()
                .beforeFetch(new InductionSupplementSupplyCommand())
                .afterFetch(new InductionSupplementSupplyCommand())
                .build();
    }
    
    @Module(FacilioConstants.Induction.INDUCTION_RESPONSE)
    public static Supplier<V3Config> getInductionResponse() {
        return () -> new V3Config(InductionResponseContext.class, null)
                .update()
                .beforeSave(QAndATransactionChainFactory.commonBeforeQAndAResponseUpdate())
        		.list()
        		.beforeFetch(new InductionSupplementSupplyCommand())
        		.summary()
        		.beforeFetch(new InductionSupplementSupplyCommand())
                .build();
    }
    
    @Module(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL)
    public static Supplier<V3Config> getInductionTriggerInclExcl() {
        return () -> new V3Config(InductionTriggerIncludeExcludeResourceContext.class, null);
    }
}
