package com.facilio.chain;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.transaction.FacilioTransactionManager;

public class FacilioChain extends ChainBase {
	private static final ThreadLocal<FacilioChain> rootChain = new ThreadLocal<>();
	
	private Chain postTransactionChain;
	private boolean enableTransaction = false;
	private int timeout = -1;
	public FacilioChain(boolean isTransactionChain) {
		// TODO Auto-generated constructor stub
		this.enableTransaction = isTransactionChain && Boolean.valueOf(AwsUtil.getConfig("enable.transaction"));
	}
	
	public FacilioChain (int timeout) { //If timeout is required, it's assumed to be Transaction chain
		this.timeout = timeout;
		this.enableTransaction = Boolean.valueOf(AwsUtil.getConfig("enable.transaction"));
	}
	
    private static final Logger LOGGER = LogManager.getLogger(FacilioChain.class.getName());

	public Chain getPostTransactionChain() {
		return postTransactionChain;
	}

	public void setPostTransactionChain(Chain postTransaction) {
		this.postTransactionChain = postTransaction;
	}

	public boolean execute(Context context) throws Exception {
		this.addCommand(new FacilioChainExceptionHandler());

		FacilioChain facilioChain = rootChain.get();
		if (facilioChain == null) {
			rootChain.set(this);
		}
		
		boolean istransaction = false;
		try {
			if (enableTransaction) {
				TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
				Transaction currenttrans = tm.getTransaction();
				if (currenttrans == null) {
					tm.begin();
					
					if (timeout != -1) {
						tm.setTransactionTimeout(timeout);
					}
					
					istransaction = true;
				} else {
					//LOGGER.info("joining parent transaction for " + method.getName());
				} 
			}
			boolean status = super.execute(context);
			if (enableTransaction) {
				if (istransaction) {
				//	LOGGER.info("commit transaction for " + method.getName());
					FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
				}
			}
			
			if (postTransactionChain != null) {
				FacilioChain root = rootChain.get();
				if (this.equals(root)) {
					// clear rootChain to set transaction chain as root
					rootChain.remove();
					postTransactionChain.execute(context);
				} else {
					if (root.getPostTransactionChain() != null) {
						root.getPostTransactionChain().addCommand(this.postTransactionChain);
					} else {
						root.setPostTransactionChain(this.postTransactionChain);
					}
				}
			}

			return status;
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			LOGGER.error("Exception occurred ", e);
			if (enableTransaction) {
				if (istransaction) {
					FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
					//LOGGER.info("rollback transaction for " + method.getName());
				} 
			}
			throw e;
		} finally {
			FacilioChain root = rootChain.get();
			if (this.equals(root)) {
				rootChain.remove();
			}
		}
	}
}