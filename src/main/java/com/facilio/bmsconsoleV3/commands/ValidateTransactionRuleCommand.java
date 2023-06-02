package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsole.workflow.rule.TransactionRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class ValidateTransactionRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioModule module = ModuleFactory.getTransactionRuleModule();
        TransactionRuleContext rule = (TransactionRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
        GenericSelectRecordBuilder recordbuilder=new GenericSelectRecordBuilder().select(FieldFactory.getTransactionWorkflowRuleFields())
                .table("Transaction_Rule_Config")
                .andCondition(CriteriaAPI.getCondition("TRANSACTION_DATE_FIELD_ID","transactionDateFieldId",String.valueOf(rule.getTransactionDateFieldId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("RESOURCE_FIELDID","resourceFieldId",String.valueOf(rule.getResourceFieldId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TRANSACTION_AMOUNT_FIELD_ID","transactionAmountFieldId",String.valueOf(rule.getTransactionAmountFieldId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("ACCOUNT_ID","accountId",String.valueOf(rule.getAccountId()),NumberOperators.EQUALS));
        List<Map<String, Object>> transactionrule = recordbuilder.get();
        if(!transactionrule.isEmpty()){
            {
                if(transactionrule.size()==1){
                    for(Map<String, Object> props : transactionrule){
                        Long id=(Long) props.get("id");
                        if(rule.getId()!=id){
                            throw new IllegalArgumentException("Transaction rule already exists!");
                        }
                    }
                }
                else{
                    throw new IllegalArgumentException("Transaction rule already exists!");
                }
            }

        }
        return false;
    }
}
