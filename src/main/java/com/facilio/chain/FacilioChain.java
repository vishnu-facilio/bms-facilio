package com.facilio.chain;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.cache.CacheUtil;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.LRUCache;

public class FacilioChain extends ChainBase {
	private static final ThreadLocal<FacilioChain> rootChain = new ThreadLocal<>();
//	private static final ThreadLocal<FacilioContext> postTransactionContext = new ThreadLocal<>();
	
	private List<PostTransactionCommand> postTransactionChains;
	private boolean enableTransaction = true;
	private int timeout = -1;
	
	public static FacilioChain getTransactionChain() {
		return new FacilioChain(true);
	}
	public static FacilioChain getTransactionChain(int timeout) {
		return new FacilioChain(timeout);
	}
	public static FacilioChain getNonTransactionChain() {
		return new FacilioChain(false);
	}
	
	private FacilioChain(boolean isTransactionChain) {
		// TODO Auto-generated constructor stub
		this.enableTransaction = isTransactionChain;
	}
	
	private FacilioChain (int timeout) { //If timeout is required, it's assumed to be Transaction chain
		this.timeout = timeout;
	}
	
    private static final Logger LOGGER = LogManager.getLogger(FacilioChain.class.getName());

//	public Chain getPostTransactionChain() {
//		return postTransactionChain;
//	}
//
//	public void setPostTransactionChain(Chain postTransaction) {
//		this.postTransactionChain = postTransaction;
//	}
	
//	public static void addPostTrasanction(Object key, Object value) {
//		FacilioContext facilioContext = getPostTransactionContext();
//		facilioContext.put(key, value);
//	}
//	
//	public static void addPostTransactionListObject(Object key, Object value) {
//		FacilioContext facilioContext = getPostTransactionContext();
//		List list = (List) facilioContext.get(key);
//		if (list == null) {
//			list = new ArrayList();
//			facilioContext.put(key, list);
//		}
//		list.add(value);
//	}

//	private static FacilioContext getPostTransactionContext() {
//		FacilioContext facilioContext = postTransactionContext.get();
//		if (facilioContext == null) {
//			facilioContext = new FacilioContext();
//			postTransactionContext.set(facilioContext);
//		}
//		return facilioContext;
//	}
    
    @Override
    public void addCommand(Command command) {
    	if (command instanceof PostTransactionCommand) {
    		addPostTransaction((PostTransactionCommand) command);
    	} else if (command instanceof FacilioChain) {
    		FacilioChain chain = ((FacilioChain) command);
    		addPostTransaction(chain.postTransactionChains);
    		chain.postTransactionChains = null;
    	}
    	super.addCommand(command);
    }
    
    private void addPostTransaction(List<PostTransactionCommand> commands) {
    	if (CollectionUtils.isEmpty(commands)) {
    		return;
    	}
    	
    	if (postTransactionChains == null) {
    		postTransactionChains = new ArrayList<>();
    	}
    	postTransactionChains.addAll(commands);
    }
    
    private void addPostTransaction(PostTransactionCommand command) {
    	if (command == null) {
    		return;
    	}
    	
    	if (postTransactionChains == null) {
    		postTransactionChains = new ArrayList<>();
    	}
    	postTransactionChains.add(command);
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
					clearResponseCache();
				}
			}
			
			if (CollectionUtils.isNotEmpty(postTransactionChains)) {
				FacilioChain root = rootChain.get();
				if (this.equals(root)) {
					try {
						TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
						Transaction currenttrans = tm.getTransaction();
						if (currenttrans == null) {
							tm.begin();
						} 
						for (PostTransactionCommand postTransactionCommand: postTransactionChains) {
							postTransactionCommand.postExecute();
						}
	
						FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
					} catch (Throwable e) {
						LOGGER.error("Exception occurred ", e);
						FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
					}
					// clear rootChain to set transaction chain as root
					rootChain.remove();
				}
				else {
					root.addPostTransaction(this.postTransactionChains);
				}
			}

			return status;
		} catch (Throwable e) {
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
	
	private void clearResponseCache() {
		LRUCache.getResponseCache().remove(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
	}
}