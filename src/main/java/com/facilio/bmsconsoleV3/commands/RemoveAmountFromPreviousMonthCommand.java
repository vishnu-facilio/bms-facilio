package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveAmountFromPreviousMonthCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TRANSACTION);
        List<FacilioField> fields = modBean.getAllFields(module.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<V3TransactionContext> recordList = Constants.getRecordListFromMap(recordMap, module.getName());
        V3TransactionContext record= recordList.get(0);


        GenericSelectRecordBuilder recbuilder=new GenericSelectRecordBuilder().select(fields)
                .table("Transactions")
                .andCondition(CriteriaAPI.getCondition("TRANSACTION_SOURCE_RECORD_ID","transactionSourceRecordId",String.valueOf(record.getTransactionSourceRecordId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ID","id",String.valueOf(record.getId()),NumberOperators.EQUALS));
        List<Map<String, Object>> transaction = recbuilder.get();
        Map<String, Object> oldTransaction = new HashMap<>();
        Long transactionDate=0L;
        Double debitAmount=0d;
        Long orgId=-1l;
        Long accounttype=0L;
        Long resource=0L;
        Map<String,Object> account=new HashMap<>();
        Map<String,Object> transactionResource=new HashMap<>();
        for(Map<String, Object> props : transaction){
            transactionDate    = (Long) props.get("transactionDate");
            orgId=(Long)props.get("orgId");
            accounttype=(Long)props.get("account");
            resource=(Long)props.get("transactionResource");
        }

        account.put("id",accounttype);
        transactionResource.put("id",resource);

        oldTransaction.put("transactionDate", transactionDate);
        oldTransaction.put("debitAmount", debitAmount);
        oldTransaction.put("orgId", orgId);
        oldTransaction.put("account", account);
        oldTransaction.put("transactionResource", transactionResource);
        context.put("transaction",oldTransaction);
        context.put("previoustransaction",transactionDate);


        return false;
    }
}
