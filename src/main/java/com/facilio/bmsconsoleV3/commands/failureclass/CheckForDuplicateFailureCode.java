package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeCausesContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeProblemsContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeRemediesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckForDuplicateFailureCode extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> queryMap = (Map<String, List>) context.get("queryParams");
        List<Long> codeIds = new ArrayList<>();
        List excludeAvailableFailureCodes = queryMap.get("excludeAvailableFailureCodes");
        if(excludeAvailableFailureCodes != null && excludeAvailableFailureCodes.size() > 0) {
            String isExcludeAvailableFailureCodes = (String) excludeAvailableFailureCodes.get(0);
            if (isExcludeAvailableFailureCodes != null && isExcludeAvailableFailureCodes.equals("true")) {
                List summaryIds = queryMap.get("summaryId");
                Long failure_class_id = Long.parseLong((String) summaryIds.get(0));
                List<Long> problemIds = new ArrayList<>();
                List<Long> causeIds = new ArrayList<>();

                Criteria criteria = new Criteria();
                Condition condition = CriteriaAPI.getCondition("FAILURE_CLASS_ID", "failureClass", String.valueOf(failure_class_id), NumberOperators.EQUALS);
                criteria.addAndCondition(condition);
                Map<Long, V3FailureCodeProblemsContext> props1 = V3RecordAPI.getRecordsMap("failurecodeproblems", null, V3FailureCodeProblemsContext.class, criteria, null);
                if (props1 != null) {
                    for (Long id : props1.keySet()) {
                        V3FailureCodeProblemsContext problemsContext = props1.get(id);
                        problemIds.add(problemsContext.getId());
                        codeIds.add(problemsContext.getFailureCode().getId());
                    }
                }
                if (CollectionUtils.isNotEmpty(problemIds)) {
                    Criteria criteria_1 = new Criteria();
                    Condition condition_1 = CriteriaAPI.getCondition("FAILURE_CODE_PROBLEMS_ID", "failureCodeProblems", StringUtils.join(problemIds, ','), NumberOperators.EQUALS);
                    criteria_1.addAndCondition(condition_1);
                    Map<Long, V3FailureCodeCausesContext> props2 = V3RecordAPI.getRecordsMap("failurecodecauses", null, V3FailureCodeCausesContext.class, criteria_1, null);
                    if (props2 != null) {
                        for (Long id : props2.keySet()) {
                            V3FailureCodeCausesContext causesContext = props2.get(id);
                            causeIds.add(causesContext.getId());
                            codeIds.add(causesContext.getFailureCode().getId());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(causeIds)) {
                        Criteria criteria_2 = new Criteria();
                        Condition condition_2 = CriteriaAPI.getCondition("FAILURE_CODE_CAUSES_ID", "failureCodeCauses", StringUtils.join(causeIds, ','), NumberOperators.EQUALS);
                        criteria_2.addAndCondition(condition_2);
                        Map<Long, V3FailureCodeRemediesContext> props3 = V3RecordAPI.getRecordsMap("failurecoderemedies", null, V3FailureCodeRemediesContext.class, criteria_2, null);
                        if (props2 != null) {
                            for (Long id : props3.keySet()) {
                                V3FailureCodeRemediesContext remedyContext = props3.get(id);
                                codeIds.add(remedyContext.getFailureCode().getId());
                            }
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(codeIds)) {
                    Condition codeCondition = CriteriaAPI.getCondition("ID", "id",
                            StringUtils.join(codeIds, ','), NumberOperators.NOT_EQUALS);
                    context.put(FacilioConstants.ContextNames.FILTER_SERVER_CRITERIA, codeCondition);
                }
            }
        }
            return false;
        }
    }
