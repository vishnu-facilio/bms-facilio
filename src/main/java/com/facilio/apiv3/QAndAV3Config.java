package com.facilio.apiv3;

import com.facilio.constants.FacilioConstants;
import com.facilio.qa.command.QAndAReadOnlyChainFactory;
import com.facilio.qa.command.QAndATransactionChainFactory;
import com.facilio.qa.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.annotation.Config;
import com.facilio.v3.annotation.Module;

import java.util.function.Supplier;

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
