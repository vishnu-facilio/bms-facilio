package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20ScheduleContext;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetSFGSyncHistoryDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long syncId = (Long) context.get("sfgSyncId");
        Long type = (Long) context.get("flowType");
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        FacilioModule module = ModuleFactory.getSFG20JobPlanModule();
        List<FacilioField> fields = FieldFactory.getSFG20JobPlansFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("syncId"), Collections.singleton(syncId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("flowType"), Collections.singleton(type), NumberOperators.EQUALS));
        if(pagination !=null) {
            int perPage;
            int page;
            boolean withCount = false;
            if (pagination != null) {
                page = (int) (pagination.get("page") == null ? 1 : pagination.get("page"));
                perPage = (int) (pagination.get("perPage") == null ? 50 : pagination.get("perPage"));
                withCount = (boolean) pagination.get("withCount");
            } else {
                page = 1;
                perPage = 50;
            }
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            selectBuilder.offset(offset);
            selectBuilder.limit(perPage);
        }
        List<Map<String,Object>>props = selectBuilder.get();
        List<SFG20ScheduleContext> historyList = FieldUtil.getAsBeanListFromMapList(props,SFG20ScheduleContext.class);
        context.put(FacilioConstants.ContextNames.SFG20.SCHEDULES_DETAILS_LIST,historyList);
        return false;
    }
}