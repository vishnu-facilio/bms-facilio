package com.facilio.chain;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.PostTransactionCommand;
import com.facilio.bmsconsole.db.ResponseCacheUtil;
import com.facilio.db.transaction.FacilioTransactionManager;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.transaction.Status;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FacilioChain extends ChainBase {
	private static final ThreadLocal<FacilioChain> rootChain = new ThreadLocal<>();

	public static FacilioChain getRootchain () {
		return rootChain.get();
	}
	public static void setRootChain(FacilioChain chain) {
		rootChain.set(chain);
	}

	private List<PostTransactionCommand> postTransactionChains;
	private boolean enableTransaction = true;
	private int timeout = -1;
	private FacilioContext context;

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

	private FacilioChain(int timeout) { // If timeout is required, it's assumed to be Transaction chain
		this.timeout = timeout;
	}

	private static final Logger LOGGER = LogManager.getLogger(FacilioChain.class.getName());

	public FacilioContext getContext() {
		if (context == null) {
			context = new FacilioContext();
		}
		return context;
	}
	public void setContext(FacilioContext context) {
		Objects.requireNonNull(context, "Cannot set null context for Facilio Chain");
		this.context = context;
	}

	@Override
	public void addCommand(Command command) {
		if (!FacilioProperties.isProduction()) {
			if (command instanceof Command && !(command instanceof Chain)) {
				if (!(command instanceof FacilioCommand) && !("ScheduleNewPMCommand".equalsIgnoreCase(command.getClass().getSimpleName()))) {
					throw new IllegalArgumentException("Only FacilioCommand is accepted");
				}
			}
		}

		if (command instanceof PostTransactionCommand) {
			addPostTransaction((PostTransactionCommand) command);
		} else if (command instanceof FacilioChain) {
			FacilioChain chain = ((FacilioChain) command);
			addPostTransaction(chain.postTransactionChains);
			chain.postTransactionChains = null;
			chain.context = context;
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

	private boolean throwError = true;
	public boolean execute() throws Exception {
		throwError = false;
		return execute(getContext());
	}

	/**
	 * @deprecated
	 * Use execute() instead
	 */
	@Deprecated
	public boolean execute(Context context) throws Exception {

//		if (throwError && DBConf.getInstance().isDevelopment()) {
//			throw new IllegalArgumentException("Use execute() directly and not this execute.");
//		}

		this.addCommand(new FacilioChainExceptionHandler());

		FacilioChain facilioChain = rootChain.get();
		if (facilioChain == null) {
			LOGGER.debug(MessageFormat.format("Setting this as root chain : {0}", this));
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
					// LOGGER.info("joining parent transaction for " + method.getName());
				}
			}
			boolean status = super.execute(context);
			int transactionStatus = -1;
			if (enableTransaction) {
				if (istransaction) {
					// LOGGER.info("commit transaction for " + method.getName());
					transactionStatus = FacilioTransactionManager.INSTANCE.getTransactionManager().getStatus();
					FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
					clearResponseCache();
				}
			}

			if (CollectionUtils.isNotEmpty(postTransactionChains)) {
				FacilioChain root = rootChain.get();
				if (this.equals(root)) {
					LOGGER.debug(MessageFormat.format("Executing post transactions for this chain : {0}", this));
					boolean onSuccess = true;
					if (enableTransaction) {
						onSuccess = transactionStatus != Status.STATUS_MARKED_ROLLBACK;
					}
					handlePostTransaction(onSuccess);
					// clear rootChain to set transaction chain as root
					rootChain.remove();
				}
				else {
					root.addPostTransaction(postTransactionChains);
				}
			}

			return status;
		} catch (Throwable e) {
			LOGGER.error("Exception occurred ", e);
			if (enableTransaction) {
				if (istransaction) {
					FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
					// LOGGER.info("rollback transaction for " + method.getName());
				}
			}

			if (CollectionUtils.isNotEmpty(postTransactionChains)) {
				FacilioChain root = rootChain.get();
				if (this.equals(root)) {
					handlePostTransaction(false);
					// clear rootChain to set transaction chain as root
					rootChain.remove();
				}
				else {
					root.addPostTransaction(postTransactionChains);
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

	private void handlePostTransaction(boolean onSuccess) throws Exception {
		try {
			TransactionManager tm = FacilioTransactionManager.INSTANCE.getTransactionManager();
			Transaction currenttrans = tm.getTransaction();
			if (currenttrans == null) {
				tm.begin();
			}
			while (CollectionUtils.isNotEmpty(postTransactionChains)) {
				List<PostTransactionCommand> newList = new ArrayList<>(postTransactionChains);
				postTransactionChains.clear();
				Iterator<PostTransactionCommand> iterator = newList.iterator();
				while (iterator.hasNext()) {
					PostTransactionCommand postTransactionCommand = iterator.next();
					long time = System.currentTimeMillis();
					int selectCount = AccountUtil.getCurrentSelectQuery(), pSelectCount = AccountUtil.getCurrentPublicSelectQuery();

					if (onSuccess) {
						postTransactionCommand.postExecute();
					} else {
						postTransactionCommand.onError();
					}

					long executionTime = System.currentTimeMillis() - time;
					int totalSelect = AccountUtil.getCurrentSelectQuery() - selectCount;
					int totalPublicSelect = AccountUtil.getCurrentPublicSelectQuery() - pSelectCount;
					if (executionTime > 50 || totalSelect > 10 || totalPublicSelect > 10) {
						String msg = MessageFormat.format("### time taken for {0} of {1} is {2}, select : {3}, pSelect : {4}", onSuccess ? "postExecute" : "onError", postTransactionCommand.getClass().getSimpleName(), executionTime, totalSelect, totalPublicSelect);
						if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getOrgId() == 274 || AccountUtil.getCurrentOrg().getOrgId() == 317) ) {
							LOGGER.info(msg);
						}
						else {
							LOGGER.debug(msg);
						}
					}
				}
			}

			FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
		} catch (Throwable e) {
			LOGGER.error("Exception occurred ", e);
			FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
		}
	}

	private void clearResponseCache() {
		if (AccountUtil.getCurrentAccount() != null) {
			ResponseCacheUtil.removeOrgCache(AccountUtil.getCurrentOrg().getId());
		}
	}
}