package com.facilio.bmsconsoleV3.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsoleV3.context.Shift;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ShiftAPI {

    public static final long UNLIMITED_PERIOD = -2;

    public static Shift getShift(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
        SelectRecordsBuilder<Shift> builder = new SelectRecordsBuilder<Shift>()
                .beanClass(Shift.class)
                .module(module)
                .select(modBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        return builder.fetchFirst();
    }

    public static List<ShiftUserRelContext> getShiftUserMapping(long startTime, long endTime, long orgUserId, long shiftId, boolean alignDate) throws Exception {
        startTime = DateTimeUtil.getDayStartTimeOf(startTime);
        endTime = DateTimeUtil.getDayEndTimeOf(endTime);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getShiftUserRelModule().getTableName())
                .select(FieldFactory.getShiftUserRelModuleFields())
                .orderBy("START_TIME");

        Criteria c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
        c.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
        builder.andCriteria(c);

        c = new Criteria();
        c.addAndCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
        c.addOrCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
        builder.andCriteria(c);

        if (orgUserId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", String.valueOf(orgUserId), NumberOperators.EQUALS));
        }
        if (shiftId > 0) {
            builder.andCondition(CriteriaAPI.getCondition("SHIFTID", "shiftId", String.valueOf(shiftId), NumberOperators.EQUALS));
        }

        List<Map<String, Object>> list = builder.get();
        List<ShiftUserRelContext> shiftUserMapping = FieldUtil.getAsBeanListFromMapList(list, ShiftUserRelContext.class);

        if (alignDate) {
            if (CollectionUtils.isNotEmpty(shiftUserMapping)) {
                for (ShiftUserRelContext rel : shiftUserMapping) {
                    if (rel.getStartTime() == com.facilio.bmsconsole.util.ShiftAPI.UNLIMITED_PERIOD || rel.getStartTime() < startTime) {
                        rel.setStartTime(startTime);
                    }

                    if (rel.getEndTime() == com.facilio.bmsconsole.util.ShiftAPI.UNLIMITED_PERIOD || rel.getEndTime() > endTime) {
                        rel.setEndTime(endTime);
                    }
                }
            }
        }
        return shiftUserMapping;
    }

}
