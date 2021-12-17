package com.facilio.banner.commands;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.banner.context.BannerContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetBannersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        User currentUser = AccountUtil.getCurrentUser();

        Criteria timeCriteria = new Criteria();
        long currentTime = System.currentTimeMillis();
        timeCriteria.addAndCondition(CriteriaAPI.getCondition("START_DATE", "startDate", String.valueOf(currentTime), NumberOperators.LESS_THAN_EQUAL));
        timeCriteria.addAndCondition(CriteriaAPI.getCondition("END_DATE", "endDate", String.valueOf(currentTime), NumberOperators.GREATER_THAN_EQUAL));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getBannerModule().getTableName())
                .select(FieldFactory.getBannerFields())
                .andCondition(CriteriaAPI.getCondition("USER_ID", "userId", String.valueOf(currentUser.getOuid()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("CANCELLED", "cancelled", String.valueOf(false), BooleanOperators.IS))
                .andCriteria(timeCriteria)
                .orderBy("PRIORITY")
                .limit(5);
        List<BannerContext> banners = FieldUtil.getAsBeanListFromMapList(builder.get(), BannerContext.class);
        context.put(FacilioConstants.ContextNames.FACILIO_BANNERS, banners);
        return false;
    }
}
