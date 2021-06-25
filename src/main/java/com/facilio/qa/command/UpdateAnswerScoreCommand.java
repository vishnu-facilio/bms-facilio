package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.fields.FacilioField;
import com.facilio.qa.context.AnswerContext;
import com.facilio.qa.context.ResponseContext;
import com.facilio.util.MathUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UpdateAnswerScoreCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Collection<AnswerContext> answers = (Collection<AnswerContext>) context.get(FacilioConstants.QAndA.Command.ANSWER_LIST);
        if (CollectionUtils.isNotEmpty(answers)) {
            List<FacilioField> updateFields = Constants.getModBean().getAllFields(FacilioConstants.QAndA.ANSWER);
            List<FacilioField> scoreFields = updateFields.stream().filter(this::isScoreField).collect(Collectors.toList());
            V3RecordAPI.batchUpdateRecords(FacilioConstants.QAndA.ANSWER, answers, scoreFields);

            ResponseContext response = (ResponseContext) context.get(FacilioConstants.QAndA.RESPONSE);
            double totalScore = answers.stream().mapToDouble(AnswerContext::scoreWithZeroOnNull).sum();
            response.setTotalScore(totalScore);
            response.setScorePercent(MathUtil.calculatePercentage(response.getTotalScore(), response.getFullScore()));
        }
        return false;
    }

    private boolean isScoreField (FacilioField field) {
        return "fullScore".equals(field.getName()) || "score".equals(field.getName()) || "scorePercent".equals(field.getName());
    }
}
