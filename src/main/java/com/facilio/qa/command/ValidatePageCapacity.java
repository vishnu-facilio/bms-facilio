package com.facilio.qa.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.qa.context.QuestionContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidatePageCapacity extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuestionContext> list = Constants.getRecordList((FacilioContext) context);

        if (CollectionUtils.isNotEmpty(list)) {
            for (QuestionContext question : list) {
                Long pageId = question.getPage() == null ? null : question.getPage().getId();
                V3Util.throwRestException(pageId==null, ErrorCode.VALIDATION_ERROR, "errors.qa.validatePageCapacity.pageIdCheck",true,null);
                //V3Util.throwRestException(pageId==null, ErrorCode.VALIDATION_ERROR, "Page ID is mandatory",true,null);
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .select(FieldFactory.getCountField())
                        .table("Q_And_A_Questions")
                        .andCondition(CriteriaAPI.getCondition("PAGE_ID","page", String.valueOf(pageId), NumberOperators.EQUALS))
                        .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted",String.valueOf(Boolean.FALSE), BooleanOperators.IS));

                Map<String, Object> modulesMap = builder.fetchFirst();
                long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

                V3Util.throwRestException(count>=50, ErrorCode.VALIDATION_ERROR, "errors.qa.validatePageCapacity.questionCountCheck",true,null);
                //V3Util.throwRestException(count>=50, ErrorCode.VALIDATION_ERROR, "Only 50 questions can be added to a single page",true,null);
            }
        }
        return false;
    }
}
