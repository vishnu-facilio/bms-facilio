package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeProblemsContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.List;
import java.util.Map;

import static com.facilio.bmsconsole.util.ImportAPI.getValidatedRows;

public class HandleFailureCauseAfterImportCommand extends FacilioCommand {
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
            String problemFailureCodeName = (String) colVal.get("Problem");

            V3FailureClassContext failureClass = FailureClassImportAPI.getFailureClass(failureClassName);
            V3FailureCodeContext failureCodeObj = FailureClassImportAPI.getFailureCode(problemFailureCodeName);

            V3FailureCodeProblemsContext failureCodeProblem = FailureClassImportAPI.getFailureCodeProblem(failureClass, failureCodeObj);

            insertRecords.get(ix).put("failureCodeProblems", failureCodeProblem);
        }

        return false;
    }



}
