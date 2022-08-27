package com.facilio.bmsconsoleV3.commands.failureclass;

import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureCodeProblemsContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class FailureClassImportAPI {

    public static V3FailureClassContext getFailureClass(String failureClassName) throws Exception {
        Condition nameCondition =
                CriteriaAPI.getCondition("NAME", "name", failureClassName, StringOperators.IS);

        FacilioModule failureClassModule = ModuleFactory.getFailureClassModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(failureClassModule.getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(failureClassModule)))
                .andCondition(nameCondition);
        Map<String, Object> map = builder.fetchFirst();
        return FieldUtil.getAsBeanFromMap(map, V3FailureClassContext.class);
    }

    public static V3FailureCodeContext getFailureCode(String failureCode) throws Exception {

        Condition codeCondition =
                CriteriaAPI.getCondition("CODE", "code",
                        failureCode, StringOperators.IS);

        ArrayList<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getField("code", "CODE", FieldType.STRING));
        fields.add(FieldFactory.getField("id", "ID", FieldType.NUMBER));

        FacilioModule failureCodeModule = ModuleFactory.getFailureCodeModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(failureCodeModule.getTableName())
                .select(fields)
                .andCondition(codeCondition);
        Map<String, Object> map = builder.fetchFirst();
        return FieldUtil.getAsBeanFromMap(map, V3FailureCodeContext.class);
    }

    public static V3FailureCodeProblemsContext getFailureCodeProblem(V3FailureClassContext failureClass,
                                                                     V3FailureCodeContext failureCode) throws Exception {

        Condition codeCondition =
                CriteriaAPI.getCondition("FAILURE_CODE_ID", "code",
                        String.valueOf(failureCode.getId()), StringOperators.IS);

        Condition classCondition =
                CriteriaAPI.getCondition("FAILURE_CLASS_ID", "class",
                        String.valueOf(failureClass.getId()), StringOperators.IS);

        FacilioModule failureCodeProblemModule = ModuleFactory.getFailureCodeProblemModule();

        ArrayList<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(failureCodeProblemModule));
        fields.add(FieldFactory.getField("code", "FAILURE_CODE_ID", FieldType.NUMBER));
        fields.add(FieldFactory.getField("class", "FAILURE_CLASS_ID", FieldType.NUMBER));


        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(failureCodeProblemModule.getTableName())
                .select(fields)
                .andCondition(codeCondition)
                .andCondition(classCondition);
        Map<String, Object> map = builder.fetchFirst();
        if (map == null) {
            throw new RESTException(ErrorCode.RESOURCE_NOT_FOUND, "problem code not found");
        }
        V3FailureCodeProblemsContext problem = new V3FailureCodeProblemsContext();
        problem.setId((Long) map.get("id"));
        problem.setFailureClass(failureClass);
        problem.setFailureCode(failureCode);
        return problem;
    }
}
