package com.facilio.qa.command;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.PageContext;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.PositionUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class UpdatePagePosAndDeleteQuestions extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);
        PositionUtil.updatePositionOnRemove(FacilioConstants.QAndA.PAGE,
                                            "parent",
                                            "position",
                                            PageContext.class,
                                            recordIds,
                                            PageContext::getPosition,
                                            PageContext::setPosition,
                                            p -> p.getParent().getId()
                                            );


        FacilioField pageField = Constants.getModBean().getField("page", FacilioConstants.QAndA.QUESTION);
        DeleteRecordBuilder<QuestionContext> deleteBuilder = new DeleteRecordBuilder<QuestionContext>()
                                                                .moduleName(FacilioConstants.QAndA.QUESTION)
                                                                .andCondition(CriteriaAPI.getCondition(pageField, recordIds, PickListOperators.IS))
                                                                ;
        int rows = deleteBuilder.markAsDelete();
        return false;
    }
}
