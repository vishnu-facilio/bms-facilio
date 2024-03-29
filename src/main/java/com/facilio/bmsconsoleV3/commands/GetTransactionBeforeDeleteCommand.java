package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3TransactionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.leed.util.LeedIntegrator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLogger;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTransactionBeforeDeleteCommand extends FacilioCommand {
        private static final Logger LOGGER = LogManager.getLogger(GetTransactionBeforeDeleteCommand.class.getName());;

        @Override
    public boolean executeCommand(Context context) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<Long>ids=(List<Long>)context.get("recordIds");
            FacilioContext transactionContext = V3Util.getSummary(FacilioConstants.ContextNames.TRANSACTION,ids);
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TRANSACTION);
                if(!transactionContext.isEmpty())
                {
                        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(transactionContext);
                        if(!recordMap.isEmpty()){
                                List<V3TransactionContext> recordList = Constants.getRecordListFromMap(recordMap, module.getName());
                                Map<String, Object> oldTransaction = new HashMap<>();
                                Long transactionDate=0L;
                                Double debitAmount=0d;
                                Long orgId=-1l;
                                Long accounttype=0L;
                                Long resource=0L;
                                Map<String,Object> account=new HashMap<>();
                                Map<String,Object> transactionResource=new HashMap<>();
                                if(!recordList.isEmpty() && isValidate(recordList)){
                                        V3TransactionContext record= recordList.get(0);
                                        transactionDate    = record.getTransactionDate();
                                        orgId=record.getOrgId();
                                        accounttype=record.getAccount().getId();
                                        resource=record.getTransactionResource().getId();

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

                        }

                }

        return false;
    }
    public boolean isValidate(List<V3TransactionContext> recordList){
            for(V3TransactionContext record : recordList){
                    if(record.getTransactionDate()==null || record.getOrgId()<1 || record.getTransactionResource()==null || record.getAccount()==null)
                    {
                            LOGGER.info("Invalid transaction record -- "+record); // will be removed after sometimes
                            return false;
                    }
                    else{
                            return true;
                    }
            }
            return false;
    }
}
