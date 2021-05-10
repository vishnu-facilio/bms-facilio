package com.facilio.qa.context;

import java.util.List;

public interface QuestionHandler<Q extends QuestionContext> {

    public void validateSave (List<Q> questions) throws Exception;

    public void afterSave (List<Q> questions) throws Exception;

    public void validateUpdate (List<Q> questions) throws Exception;

    public void afterUpdate (List<Q> questions) throws Exception;

    public void afterFetch (List<Q> questions) throws Exception;

    /**
     *
     * Remember to override fetchSummaryOfResponse method in corresponding AnswerHandler
     *
     * @param question
     * @return
     * @throws Exception
     */
    default boolean showSummaryOfResponses (QuestionContext question) throws Exception {
        return false;
    }

    default boolean hideShowResponseButton (QuestionContext question) throws Exception {
        return false;
    }

}
