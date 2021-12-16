package com.facilio.bmsconsole.commands;

import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.User;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContractsContext;
import com.facilio.bmsconsole.context.TimelogContext;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetTimeLogCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if(moduleName == null){
            throw new IllegalArgumentException("Module cannot be empty");
        }

        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        if(id <= 0){
            throw new IllegalArgumentException("Id is not found");
        }
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                 .select(FieldFactory.getTimeLogFields(null))
                 .table(module.getTableName())
                 .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS));

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            builder.andCriteria(filterCriteria);
        }

        List<TimelogContext> timeLogs = FieldUtil.getAsBeanListFromMapList(builder.get(),TimelogContext.class);
        setObjectFields(timeLogs,moduleBean,module);

        context.put(FacilioConstants.ContextNames.WORK_ORDER, timeLogs);
        return false;
    }

    private void setObjectFields(List<TimelogContext> timeLogs, ModuleBean moduleBean,FacilioModule module) throws  Exception{
        Set<Long> fromStatusIds = new HashSet<>();
        Set<Long> doneBy = new HashSet<>();

        for(TimelogContext timelogContext : timeLogs){
            fromStatusIds.add(timelogContext.getFromStatusId());
            doneBy.add(timelogContext.getDoneById());
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(fromStatusIds,ModuleFactory.getTicketStatusModule()));
         TicketAPI.getStatuses(module,criteria);

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        List<User> doneByUsers = userBean.getUsers(null,false,true,doneBy);

        if(!(doneByUsers.isEmpty())){
            Map<Long,User> doneByUserMap = doneByUsers.stream().collect(Collectors.toMap(User::getId,Function.identity()));
            for(TimelogContext timelogContext : timeLogs){
                timelogContext.setDoneBy(doneByUserMap.get(timelogContext.getDoneById()));
            }
        }

      for(TimelogContext timelogContext : timeLogs){
            timelogContext.setFromStatus(moduleBean.getField(timelogContext.getFromStatusId()));
       }

    }

}
