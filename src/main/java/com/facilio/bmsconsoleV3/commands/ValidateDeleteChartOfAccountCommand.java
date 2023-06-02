package com.facilio.bmsconsoleV3.commands;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValidateDeleteChartOfAccountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);

        ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule budgetAmountModule = bean.getModule("budgetAmount");

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(budgetAmountModule.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(budgetAmountModule))
                .andCondition(CriteriaAPI.getConditionFromList("CHART_OF_ACCOUNT_ID", "chartOfAccountId", recordIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME","sysDeletedTime",null, CommonOperators.IS_EMPTY));
        List<Map<String, Object>> props = builder.get();

        if(CollectionUtils.isNotEmpty(props)) {
            Long count = (Long) props.get(0).get("id");
            if(count!=null && count > 0) {
                throw new IllegalArgumentException("This Account cannot be deleted as it is linked with Budget(s) and Transaction rules(s)");
            }
        }
        return false;
    }
}
