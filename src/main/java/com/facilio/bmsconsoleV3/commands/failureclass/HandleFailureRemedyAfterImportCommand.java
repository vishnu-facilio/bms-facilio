package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeCausesContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeProblemsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsole.util.ImportAPI.getValidatedRows;

public class HandleFailureRemedyAfterImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ImportProcessContext importProcessContext = (ImportProcessContext)
                context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);

        List<Map<String, Object>> insertRecords = (List<Map<String, Object>>)
                context.get(ImportAPI.ImportProcessConstants.INSERT_RECORDS);

        List<Map<String, Object>> list = getValidatedRows(importProcessContext.getId());

        for (int ix = 0; ix < list.size(); ix++) {
            Map<String, Object> row = list.get(ix);
            JSONParser parser = new JSONParser();
            JSONArray rawContents = (JSONArray) parser.parse((String) row.get("rowContextString"));
            if (rawContents.size() != 1) {
                continue;
            }
            JSONObject colVal = (JSONObject) ((JSONObject) rawContents.get(0)).get("colVal");
            String failureClassName = (String) colVal.get("Failure Class Name");
            String causeFailureCodeName = (String) colVal.get("Cause");
            String problemFailureCodeName = (String) colVal.get("Problem");

            V3FailureClassContext failureClass = FailureClassImportAPI.getFailureClass(failureClassName);

            V3FailureCodeContext failureCode = FailureClassImportAPI.getFailureCode(problemFailureCodeName);
            V3FailureCodeProblemsContext problem = FailureClassImportAPI.getFailureCodeProblem(failureClass, failureCode);

            failureCode = FailureClassImportAPI.getFailureCode(causeFailureCodeName);
            V3FailureCodeCausesContext failureCodeCause = getFailureCodeCause(problem, failureCode);

            insertRecords.get(ix).put("failureCodeCauses", failureCodeCause);
        }

        return false;
    }

    private V3FailureCodeCausesContext getFailureCodeCause(V3FailureCodeProblemsContext problem,
                                                           V3FailureCodeContext failureCode) throws Exception {

        Condition codeCondition =
                CriteriaAPI.getCondition("FAILURE_CODE_ID", "code",
                        String.valueOf(failureCode.getId()), StringOperators.IS);

        Condition classCondition =
                CriteriaAPI.getCondition("FAILURE_CODE_PROBLEMS_ID", "problem",
                        String.valueOf(problem.getId()), StringOperators.IS);

        FacilioModule failureCodeCausesModule = ModuleFactory.getFailureCodeCausesModule();

        ArrayList<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(failureCodeCausesModule));
        fields.add(FieldFactory.getField("code", "FAILURE_CODE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getField("problem", "FAILURE_CODE_PROBLEMS_ID", FieldType.NUMBER));


        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(failureCodeCausesModule.getTableName())
                .select(fields)
                .andCondition(codeCondition)
                .andCondition(classCondition);
        Map<String, Object> map = builder.fetchFirst();
        if (map == null) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "cause code not found");
        }
        V3FailureCodeCausesContext cause = new V3FailureCodeCausesContext();
        cause.setId((Long) map.get("id"));
        cause.setFailureCodeProblems(problem);
        cause.setFailureCode(failureCode);
        return cause;
    }
}
