package com.facilio.delegate.command;

import com.facilio.banner.util.BannerUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

public class DeleteUserDelegationCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long delegationId = (Long) context.get(FacilioConstants.ContextNames.ID);

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getUserDelegationModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(delegationId, ModuleFactory.getUserDelegationModule()));
        builder.delete();

        FacilioTimer.deleteJob(delegationId, DelegationUtil.SEND_DELEGATE_REMINDER_JOB_NAME);
        BannerUtil.deleteBanner("userDelegation_" + delegationId, false);

        return false;
    }
}
