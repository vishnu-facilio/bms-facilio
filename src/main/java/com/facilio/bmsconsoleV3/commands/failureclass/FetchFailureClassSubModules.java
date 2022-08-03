package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FetchFailureClassSubModules extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        if (CollectionUtils.isNotEmpty(recordMap.get("failureclass"))) {
            List<V3FailureClassContext> failureClassList = recordMap.get("failureclass");
            for (V3FailureClassContext failureclass : failureClassList) {
                List<FacilioField> failurecodeFieldsForProblems = modBean.getAllFields("failurecode");
                failurecodeFieldsForProblems.add(AccountConstants.getFailureCodeProblemField());
                failurecodeFieldsForProblems.add(AccountConstants.getFailureCodeField());
                List<Map<String, Object>> props = getSelectBuilder("Failure_Code_Problems",failurecodeFieldsForProblems,"Failure_Code.ID = Failure_Code_Problems.Failure_Code_ID","FAILURE_CLASS_ID",String.valueOf(failureclass.getId())).get();
                if (CollectionUtils.isNotEmpty(props)) {
                    List<Map<String, Object>> problems = new ArrayList<>();
                    List<FacilioField> failurecodeFieldsForCauses = modBean.getAllFields("failurecode");
                    failurecodeFieldsForCauses.add(AccountConstants.getFailureCodeCausesField());
                    failurecodeFieldsForCauses.add(AccountConstants.getFailureCodeField());
                    for (Map<String, Object> problem : props) {
                        List<Map<String, Object>> causes = getSelectBuilder("Failure_Code_Causes",failurecodeFieldsForCauses,"Failure_Code.ID = Failure_Code_Causes.Failure_Code_ID","FAILURE_CODE_PROBLEMS_ID", String.valueOf(problem.get("id"))).get();
                        List<Map<String, Object>> fccauses = new ArrayList<>();
                        for (Map<String, Object> cause : causes) {
                            List<FacilioField> failurecodeFieldsForRemedies = modBean.getAllFields("failurecode");
                            failurecodeFieldsForRemedies.add(AccountConstants.getFailureCodeRemediesField());
                            failurecodeFieldsForRemedies.add(AccountConstants.getFailureCodeField());
                            List<Map<String, Object>> remedies = getSelectBuilder("Failure_Code_Remedies",failurecodeFieldsForRemedies,"Failure_Code.ID = Failure_Code_Remedies.Failure_Code_ID","FAILURE_CODE_CAUSES_ID", String.valueOf(cause.get("id"))).get();
                            cause.put("failureCodeRemedies", remedies);
                            fccauses.add(cause);
                        }
                        problem.put("failureCodeCauses", fccauses);
                        problems.add(problem);
                    }
                    failureclass.setFailureCodeProblems(problems);
                }
            }
        }
        return false;
    }

    public static GenericSelectRecordBuilder getSelectBuilder(String tableName,List<FacilioField> fields,String condition,String columnName,String id){
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(fields)
                .innerJoin("Failure_Code")
                .on(condition)
                .andCondition(CriteriaAPI.getCondition(columnName, "id", id, NumberOperators.EQUALS));

        return selectBuilder;
    }
}
