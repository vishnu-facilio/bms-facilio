package com.facilio.qa.context;

import java.util.List;

public interface QuestionHandler<Q extends QuestionContext> {

    public void validateSave (List<Q> questions) throws Exception;

    public void afterSave (List<Q> questions) throws Exception;

    public void validateUpdate (List<Q> questions) throws Exception;

    public void afterUpdate (List<Q> questions) throws Exception;

    public void afterFetch (List<Q> questions) throws Exception;

}
