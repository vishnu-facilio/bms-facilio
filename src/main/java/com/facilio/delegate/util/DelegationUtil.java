package com.facilio.delegate.util;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class DelegationUtil {

    private static GenericSelectRecordBuilder getBuilder(long timestamp, DelegationType delegationType) {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getUserDelegationModule().getTableName())
                .select(FieldFactory.getUserDelegationFields())
                .andCondition(CriteriaAPI.getCondition("FROM_TIME", "fromTime", String.valueOf(timestamp), NumberOperators.LESS_THAN_EQUAL))
                .andCondition(CriteriaAPI.getCondition("TO_TIME", "toTime", String.valueOf(timestamp), NumberOperators.GREATER_THAN_EQUAL));
        builder.andCustomWhere("(? & User_Delegation.DELEGATION_TYPE = ?)", delegationType.getIndex(), delegationType.getIndex());
        builder.orderBy("ID ASC");
        return builder;
    }

    public static User getUser(User delegatedUser, long timestamp, DelegationType delegationType) throws Exception {
        if (delegatedUser == null) {
            throw new NullPointerException("deletegatedUser cannot be empty");
        }
        GenericSelectRecordBuilder builder = getBuilder(timestamp, delegationType);
        builder.andCondition(CriteriaAPI.getCondition("DELEGATE_USER_ID", "delegateUserId", String.valueOf(delegatedUser.getId()), NumberOperators.EQUALS));

        DelegationContext delegationContext = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), DelegationContext.class);
        if (delegationContext != null) {
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            long appId = AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1L;
            User user = userBean.getUser(appId, delegationContext.getUserId());
            if (user != null) {
                return user;
            }
        }

        return delegatedUser;
    }

    public static User getDelegatedUser(User user, long timestamp, DelegationType delegationType) throws Exception {
        if (user == null) {
            throw new NullPointerException("user cannot be empty");
        }
        GenericSelectRecordBuilder builder = getBuilder(timestamp, delegationType);

        builder.andCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(user.getId()), NumberOperators.EQUALS));
        DelegationContext delegationContext = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), DelegationContext.class);

        if (delegationContext != null) {
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            long appId = AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1L;
            User delegatedUser = userBean.getUser(appId, delegationContext.getDelegateUserId());
            if (delegatedUser != null) {
                return delegatedUser;
            }
        }
        return user;
    }
}
