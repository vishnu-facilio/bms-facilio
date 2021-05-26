package com.facilio.qa.context.questions.handler;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.qa.context.QuestionHandler;
import com.facilio.qa.context.questions.HeadingQuestionContext;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.v3.context.Constants;

import java.util.Collections;
import java.util.List;

public class HeadingQuestionHandler implements QuestionHandler<HeadingQuestionContext> {
    @Override
    public SaveOptions defaultSaveOption() throws Exception {
        SaveOptions options = new SaveOptions();
        options.setSupplements(
                Collections.singletonList(
                        (SupplementRecord) Constants.getModBean().getField("richText", FacilioConstants.QAndA.Questions.HEADING_QUESTION)));

        return options;
    }

    @Override
    public void validateSave(List<HeadingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterSave(List<HeadingQuestionContext> questions) throws Exception {

    }

    @Override
    public void validateUpdate(List<HeadingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterUpdate(List<HeadingQuestionContext> questions) throws Exception {

    }

    @Override
    public void afterFetch(List<HeadingQuestionContext> questions) throws Exception {

    }
}
