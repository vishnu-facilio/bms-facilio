package com.facilio.transaction;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionMonitor extends TimerTask {

    private static final Logger LOGGER = LogManager.getLogger(TransactionMonitor.class.getName());
    public void run() {

        ConcurrentHashMap<FacilioTransaction, Long> transactionMap = FTransactionManager.getTransactionTimeoutMap();
        transactionMap.entrySet().forEach(this::markRolledBack);
    }

    private void markRolledBack(Map.Entry<FacilioTransaction, Long> entry){
        if((System.currentTimeMillis()-entry.getValue()) > entry.getKey().getTransactionTimeout()){
            try {
                entry.getKey().setRollbackOnly();
            } catch (SystemException e) {
                LOGGER.info("Exception while setting transaction as rolled back. ", e);
            }
        }
    }
}
