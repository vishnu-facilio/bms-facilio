package com.facilio.apiv3;

import java.util.function.Supplier;

import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.MultiFileAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

@Config
public class QAndAV3Config {

    @Module(FacilioConstants.QAndA.PAGE)
    public static Supplier<V3Config> getPage() {
        return () -> new V3Config(PageContext.class, null)
                            .create()
                                .beforeSave(QAndATransactionChainFactory.beforePageSave())
                            .update()
                                .beforeSave(QAndATransactionChainFactory.beforePageUpdate())
                            .delete()
                                .beforeDelete(QAndATransactionChainFactory.beforePageDelete())
                            .summary()
                                .afterFetch(QAndAReadOnlyChainFactory.afterPagesFetch())
                            .build();
    }

    @Module(FacilioConstants.QAndA.QUESTION)
    public static Supplier<V3Config> getQuestion() {
        return () -> new V3Config(QuestionContext.class, null)
                        .create()
                            .beforeSave(QAndATransactionChainFactory.beforeQuestionSave())
                            .afterSave(QAndATransactionChainFactory.afterQuestionSave())
                        .update()
                            .beforeSave(QAndATransactionChainFactory.beforeQuestionUpdate())
                            .afterSave(QAndATransactionChainFactory.afterQuestionUpdate())
                        .summary()
                            .afterFetch(QAndAReadOnlyChainFactory.afterQuestionFetch())
                        .list()
                            .afterFetch(QAndAReadOnlyChainFactory.afterQuestionFetch())
                        .delete()
                            .beforeDelete(QAndATransactionChainFactory.beforeQuestionDelete())
                        .build();
    }

    // Do not add separate entry for extended questions. Please handle them with QuestionCRUDHandler
    
    
    @Module(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
    public static Supplier<V3Config> getInspection() {
        return () -> new V3Config(InspectionTemplateContext.class, null)
                .create()
//                    .beforeSave(QAndATransactionChainFactory.commonQAndABeforeSave())
                	.afterSave(QAndATransactionChainFactory.inspectionAfterSaveChain())
                .update()
                	.afterSave(QAndATransactionChainFactory.inspectionAfterUpdateChain())
//                .list()
//                .delete()
                .summary()
                    .afterFetch(QAndAReadOnlyChainFactory.commonAfterQAndATemplateFetch())
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

    @Module(FacilioConstants.QAndA.ANSWER)
    public static Supplier<V3Config> getQAndAAnswer() {
        return () -> new V3Config(AnswerContext.class, null)
                .create()
                .update()
//                .list()
                .delete()
                .summary()
                .build();
    }

    @Module(FacilioConstants.QAndA.MULTI_FILE_ANSWER)
    public static Supplier<V3Config> getMultiFileAnswer() {
        return () -> new V3Config(MultiFileAnswerContext.class, null)
                    .build();
    }
}
