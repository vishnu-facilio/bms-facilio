package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetConditionsListFromCriteria extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName= (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,null);
        Long criteriaId= (Long) context.getOrDefault(FacilioConstants.ContextNames.SpaceBooking.CRITERIAID,-1);
        FacilioUtil.throwIllegalArgumentException(moduleName==null || criteriaId<0,"Module Name or Criteria Id is null");
        JSONObject pagination = (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.PAGINATION,null);
        int page=1;
        int perPage = 10;
        int offset = 0;
        if (pagination != null) {
            page = (int) ((int)pagination.get("page") <=0 ? 1 : pagination.get("page"));
            perPage = (int) ((int)pagination.get("perPage") <=0 ? 10 : pagination.get("perPage"));
            offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
        }
        context.put(FacilioConstants.ContextNames.CONDITIONS,getConditionList(perPage, offset,criteriaId));
        return false;
    }
    public static List<Condition> getConditionList(int perPage , int offset, Long criteriaId) throws Exception{
        FacilioModule conditionModule = ModuleFactory.getConditionsModule();
        GenericSelectRecordBuilder criteriaBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getConditionFields())
                .table(conditionModule.getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_CRITERIA_ID","parentCriteriaId",String.valueOf(criteriaId), NumberOperators.EQUALS))
                .offset(offset)
                .limit(perPage);

        List<Map<String, Object>> criteriaProps = criteriaBuilder.get();


        List<Condition> conditionList=new ArrayList<>();
        if(criteriaProps != null && !criteriaProps.isEmpty()) {
            for(Map<String, Object> props : criteriaProps) {
                Condition condition = FieldUtil.getAsBeanFromMap(props, Condition.class);
                if(condition.getCriteriaValueId() > 0) {
                    condition.setCriteriaValue(CriteriaAPI.getCriteria(AccountUtil.getCurrentUser().getOrgId(), condition.getCriteriaValueId()));
                }
                conditionList.add(condition);
            }
        }
        return conditionList;
    }
}
