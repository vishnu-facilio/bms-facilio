package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.PositionUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdateQuestionPosOnDelete extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);
        PositionUtil.updatePositionOnRemove(FacilioConstants.QAndA.QUESTION,
                                            "page",
                                            "position",
                                            QuestionContext.class,
                                            recordIds,
                                            QuestionContext::getPosition,
                                            QuestionContext::setPosition,
                                            q -> q.getPage().getId()
                                            );
        return false;
    }
}
