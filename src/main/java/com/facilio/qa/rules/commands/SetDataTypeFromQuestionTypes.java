package com.facilio.qa.rules.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldType;
import com.facilio.qa.context.QuestionType;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class SetDataTypeFromQuestionTypes extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<FieldType> types = new ArrayList<>();
        for (QuestionType type : QuestionType.values()) {
            if (type.isOperatorSupportedForRules()) {
                types.add(type.getAnswerFieldType());
            }
        }
        context.put(FacilioConstants.Filters.FILTER_DATA_TYPES, types.toArray(new FieldType[types.size()]));

        return false;
    }
}
