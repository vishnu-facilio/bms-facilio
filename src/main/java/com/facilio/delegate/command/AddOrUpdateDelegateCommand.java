package com.facilio.delegate.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddOrUpdateDelegateCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        DelegationContext delegationContext = (DelegationContext) context.get(FacilioConstants.ContextNames.DELEGATION_CONTEXT);

        validateDelegationContext(delegationContext);

        if (delegationContext.getId() > 0) {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getUserDelegationModule().getTableName())
                    .fields(FieldFactory.getUserDelegationFields())
                    .andCondition(CriteriaAPI.getIdCondition(delegationContext.getId(), ModuleFactory.getUserDelegationModule()));
            builder.update(FieldUtil.getAsProperties(delegationContext));
        } else {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getUserDelegationModule().getTableName())
                    .fields(FieldFactory.getUserDelegationFields());
            long delegationId = builder.insert(FieldUtil.getAsProperties(delegationContext));
            delegationContext.setId(delegationId);
        }

        return false;
    }

    private void validateDelegationContext(DelegationContext delegationContext) throws Exception {
        if (StringUtils.isEmpty(delegationContext.getName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delegation name should not empty");
        }
//        if (delegationContext.getAppId() == -1) {
//            throw new RESTException(ErrorCode.VALIDATION_ERROR, "App is mandatory");
//        }
        if (delegationContext.getFromTime() == -1 || delegationContext.getToTime() == -1) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Time range is mandatory");
        }
        if (delegationContext.getUserId() == -1) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "User is mandatory");
        }
        if (delegationContext.getDelegateUserId() == -1) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delegated user is mandatory");
        }
        if (delegationContext.getDelegateUserId() == delegationContext.getUserId()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delegate to yourself ;)");
        }
        if (delegationContext.getDelegationTypeEnum() == null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delegation features is missing");
        }
        // check whether user is valid or not

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getUserDelegationModule().getTableName())
                .select(FieldFactory.getUserDelegationFields());
//                .andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(delegationContext.getAppId()), NumberOperators.EQUALS));
        if (delegationContext.getId() > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(delegationContext.getId()), NumberOperators.NOT_EQUALS));
        }
        List<DelegationContext> otherDelegations =
                FieldUtil.getAsBeanListFromMapList(builder.get(), DelegationContext.class);
        if (CollectionUtils.isNotEmpty(otherDelegations)) {
            for (DelegationContext other : otherDelegations) {
                isBetween(other, delegationContext.getFromTime());
                isBetween(other, delegationContext.getToTime());
            }
        }
    }

    private void isBetween(DelegationContext other, long time) throws RESTException {
        if (other.getFromTime() <= time &&
                time <= other.getToTime()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, String.format("Delegation for this date range already found in %s", other.getName()));
        }
    }
}
