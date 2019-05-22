package com.facilio.bmsconsole.db;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.util.DBConf;

public class BmsDBConf extends DBConf {

    @Override
    public String getTransactionId() {
        Account account = AccountUtil.getCurrentAccount();
        StringBuilder transactionId = new StringBuilder();
        if (account != null) {
            transactionId.append(account.getOrg().getDomain())
                            .append("-")
                            .append(account.getUser().getId())
                            .append("-")
                            ;
        }
        transactionId.append(Thread.currentThread().getName());
        return transactionId.toString();
    }
}
