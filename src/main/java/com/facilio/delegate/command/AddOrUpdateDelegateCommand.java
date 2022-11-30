package com.facilio.delegate.command;

import com.facilio.banner.context.BannerContext;
import com.facilio.banner.util.BannerUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationContext;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Collections;
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

        FacilioTimer.deleteJob(delegationContext.getId(), DelegationUtil.SEND_DELEGATE_REMINDER_JOB_NAME);
        long nextExecutionTime = (delegationContext.getFromTime() - (DateTimeUtil.ONE_HOUR_MILLIS_VALUE * 24));
        if (nextExecutionTime > System.currentTimeMillis()) {
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(delegationContext.getId(), DelegationUtil.SEND_DELEGATE_REMINDER_JOB_NAME, nextExecutionTime / 1000, "facilio");
        }
        DelegationUtil.fillDelegation(Collections.singletonList(delegationContext));

        // remove any banner associate with this delegation
        BannerUtil.deleteBanner("userDelegation_" + delegationContext.getId(), false);
        JSONObject linkConfig = new JSONObject();
        linkConfig.put("id", delegationContext.getId());
        linkConfig.put("navigateTo", "UserDelegation");
        BannerUtil.addBanner("userDelegation_" + delegationContext.getId(),
                delegationContext.getDelegateUserId(), String.format("%s delegates the work to you from %s till %s",
                        delegationContext.getUser().getName(), DateTimeUtil.getFormattedTime(delegationContext.getFromTime(), "dd-MM-yyyy"), DateTimeUtil.getFormattedTime(delegationContext.getToTime(), "dd-MM-yyyy")),
                delegationContext.getFromTime(), delegationContext.getToTime(), BannerContext.Type.COLLAPSE, BannerContext.Priority.HIGH,
                linkConfig.toJSONString());

        return false;
    }

    private void validateDelegationContext(DelegationContext delegationContext) throws Exception {
        if (StringUtils.isEmpty(delegationContext.getName())) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delegation name should not empty");
        }
        if (delegationContext.getFromTime() == -1 || delegationContext.getToTime() == -1) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Time range is mandatory");
        }
        if (delegationContext.getFromTime() > delegationContext.getToTime()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Start time cannot be greater than End Time");
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
        if (delegationContext.getDelegationType() <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Delegation features is missing");
        }
        // check whether user is valid or not

        // check whether user already delegated in same time
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("DELEGATE_USER_ID", "delegateUserId", String.valueOf(delegationContext.getUserId()), NumberOperators.EQUALS));
        List<DelegationContext> delegatedUsersDelegations = getDelegationContext(delegationContext.getFromTime(), delegationContext.getToTime(), delegationContext.getId(), criteria);
        if (CollectionUtils.isNotEmpty(delegatedUsersDelegations)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, String.format("Delegation for this date range is assigned to the user in %s", delegatedUsersDelegations.get(0).getName()));
        }

        // check whether user has delegated to other user for same delegate type
        criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(delegationContext.getUserId()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("DELEGATION_TYPE & " + delegationContext.getDelegationType(), "checkDelegation", String.valueOf(0), NumberOperators.GREATER_THAN));
        List<DelegationContext> otherDelegations = getDelegationContext(delegationContext.getFromTime(), delegationContext.getToTime(), delegationContext.getId(), criteria);
        if (CollectionUtils.isNotEmpty(otherDelegations)) {
            for (DelegationContext otherDelegation : otherDelegations) {
                if (otherDelegation.getDelegateUserId() != delegationContext.getDelegateUserId()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, String.format("One or few Delegation types is already granted in %s", otherDelegation.getName()));
                }
            }
        }
    }

    private List<DelegationContext> getDelegationContext(long fromTime, long toTime, long ignoreId, Criteria otherCriteria) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getUserDelegationModule().getTableName())
                .select(FieldFactory.getUserDelegationFields());
        Criteria timeCriteria = new Criteria();
        timeCriteria.andCriteria(getTimeCriteria(fromTime));
        timeCriteria.orCriteria(getTimeCriteria(toTime));
        builder.andCriteria(timeCriteria);

        if (otherCriteria != null && !otherCriteria.isEmpty()) {
            builder.andCriteria(otherCriteria);
        }

        if (ignoreId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(ignoreId), NumberOperators.NOT_EQUALS));
        }
        List<DelegationContext> otherDelegations =
                FieldUtil.getAsBeanListFromMapList(builder.get(), DelegationContext.class);
        return otherDelegations;
    }

    private Criteria getTimeCriteria(long time) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("FROM_TIME", "fromTime", String.valueOf(time), NumberOperators.LESS_THAN_EQUAL));
        criteria.addAndCondition(CriteriaAPI.getCondition("TO_TIME", "toTime", String.valueOf(time), NumberOperators.GREATER_THAN_EQUAL));
        return criteria;
    }

    private void isBetween(DelegationContext other, long time) throws RESTException {
        if (other.getFromTime() <= time &&
                time <= other.getToTime()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, String.format("Delegation for this date range already found in %s", other.getName()));
        }
    }
}
