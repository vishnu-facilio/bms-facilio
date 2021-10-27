package com.facilio.delegate.command;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetAllMyDelegationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean onlyMyDelegation = (boolean) context.get(FacilioConstants.ContextNames.ONLY_MY_DELEGATION);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getUserDelegationModule().getTableName())
                .select(FieldFactory.getUserDelegationFields());

        if (onlyMyDelegation) {
            builder.andCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(AccountUtil.getCurrentUser().getId()), NumberOperators.EQUALS));
        }
        List<DelegationContext> delegationContexts = FieldUtil.getAsBeanListFromMapList(builder.get(), DelegationContext.class);
        fillDelegation(delegationContexts);
        context.put(FacilioConstants.ContextNames.DELEGATION_LIST, delegationContexts);

        return false;
    }

    private void fillDelegation(List<DelegationContext> delegationContexts) throws Exception {
        if (CollectionUtils.isEmpty(delegationContexts)) {
            return;
        }

        Set<Long> userIds = new HashSet<>();
        for (DelegationContext delegationContext : delegationContexts) {
            userIds.add(delegationContext.getUserId());
            userIds.add(delegationContext.getDelegateUserId());
        }

        // fill user details here
        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        List<User> users = userBean.getUsers(null, false, true, userIds);
        if (CollectionUtils.isNotEmpty(users)) {
            Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            for (DelegationContext delegationContext : delegationContexts) {
                delegationContext.setUser(userMap.get(delegationContext.getUserId()));
                delegationContext.setDelegateUser(userMap.get(delegationContext.getDelegateUserId()));
            }
        }
    }
}
