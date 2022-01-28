package com.facilio.agentv2.iotmessage;

import com.facilio.agentv2.AgentConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.command.PostTransactionCommand;
import com.facilio.db.transaction.FTransactionManager;
import com.facilio.db.transaction.FacilioTransaction;
import com.facilio.db.transaction.FacilioTransactionManager;

import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

public class AddAndSendIotMessageCommand extends FacilioCommand implements PostTransactionCommand{

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(AddAndSendIotMessageCommand.class.getName());

    IotData data;
    
    @Override
    public boolean executeCommand(Context context) throws Exception {
        data = (IotData) context.get(AgentConstants.DATA);
        IotMessageApiV2.addIotData(data);
        IotMessageApiV2.addIotMessage(data.getId(), data.getMessages());
        return false;
    }

	@Override
	public boolean postExecute() throws Exception {
		try {
			TransactionManager tm = FTransactionManager.getTransactionManager();
			Transaction t = tm.getTransaction();
			if(t != null ) {
			   LOGGER.info("Transaction id in post execute - " + ((FacilioTransaction)t).getTransactionId());
			}
		} catch (SystemException e) {
			LOGGER.info("Exception occurred while getting transaction", e);
		}
		
        IotMessageApiV2.publishIotMessage(data);
		return false;
	}
}
