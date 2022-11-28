package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import com.facilio.accounts.bean.UserBean;
import org.apache.commons.chain.Context;
import com.facilio.accounts.dto.User;
import com.facilio.fw.BeanFactory;

import java.util.*;

public class GetCurrencySupplementsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<CurrencyContext> currencyList = (List<CurrencyContext>) context.get(FacilioConstants.ContextNames.CURRENCIES_LIST);
        Map<String, Map<Long, Object>> supplements = new HashMap<>();

        if (CollectionUtils.isNotEmpty(currencyList)) {
            Set<Long> userIds = new HashSet<>();
            for (CurrencyContext currency : currencyList) {
                if (currency.getSysCreatedBy() != -1) {
                    userIds.add(currency.getSysCreatedBy());
                }
                if (currency.getSysModifiedBy() != -1) {
                    userIds.add(currency.getSysModifiedBy());
                }
            }

            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            Map<Long, User> usersMap = userBean.getUsersAsMap(null, userIds);

            supplements.put("sysCreatedBy", new HashMap<>());
            supplements.put("sysModifiedBy", new HashMap<>());
            for (CurrencyContext currency : currencyList) {
                long createdBy = currency.getSysCreatedBy();
                long modifiedBy = currency.getSysModifiedBy();
                supplements.get("sysCreatedBy").put(createdBy, usersMap.get(createdBy));
                supplements.get("sysModifiedBy").put(modifiedBy, usersMap.get(modifiedBy));
            }
        }
        context.put(FacilioConstants.ContextNames.SUPPLEMENTS, supplements);

        return false;
    }
}
