package com.facilio.apiv3;

import java.util.function.Supplier;

import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.answers.MultiFileAnswerContext;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.qa.context.questions.MCQOptionContext;
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
    
    @Module(FacilioConstants.QAndA.ANSWER)
    public static Supplier<V3Config> getQAndAAnswer() {
        return () -> new V3Config(AnswerContext.class, null)
                .list()
                    .beforeFetch(QAndAReadOnlyChainFactory.beforeAnswerFetchChain())
                    .afterFetch(QAndAReadOnlyChainFactory.afterAnswerFetchChain())
                .summary()
                    .afterFetch(QAndAReadOnlyChainFactory.afterAnswerFetchChain())
                .build();
    }

    @Module(FacilioConstants.QAndA.Answers.MULTI_FILE_ANSWER)
    public static Supplier<V3Config> getMultiFileAnswer() {
        return () -> new V3Config(MultiFileAnswerContext.class, null)
                    .build();
    }

    @Module(FacilioConstants.QAndA.Questions.MCQ_SINGLE_OPTIONS)
    public static Supplier<V3Config> getMcqSingleOptions() {
        return () -> new V3Config(MCQOptionContext.class, null)
                    .build();
    }

    @Module(FacilioConstants.QAndA.Questions.MCQ_MULTI_OPTIONS)
    public static Supplier<V3Config> getMcqMultiOptions() {
        return () -> new V3Config(MCQOptionContext.class, null)
                .build();
    }
}
