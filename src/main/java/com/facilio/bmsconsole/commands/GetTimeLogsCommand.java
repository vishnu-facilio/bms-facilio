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
import org.apache.commons.collections.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetTimeLogsCommand extends FacilioCommand {

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
        String parentModueName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        FacilioModule parentModule = moduleBean.getModule(parentModueName);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                 .select(FieldFactory.getTimeLogFields(null))
                 .table(module.getTableName())
                 .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS));

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            builder.andCriteria(filterCriteria);
        }

        List<TimelogContext> timeLogs = FieldUtil.getAsBeanListFromMapList(builder.get(),TimelogContext.class);
        setObjectFields(timeLogs,parentModule);

        context.put(FacilioConstants.ContextNames.TIMELOGS, timeLogs);
        return false;
    }

    private void setObjectFields(List<TimelogContext> timeLogs,FacilioModule module) throws  Exception{
        Set<Long> fromStatusIds = new HashSet<>();
        Set<Long> doneBy = new HashSet<>();

        for(TimelogContext timelogContext : timeLogs){
            fromStatusIds.add(timelogContext.getFromStatusId());
            doneBy.add(timelogContext.getDoneById());
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(fromStatusIds,ModuleFactory.getTicketStatusModule()));
        List<FacilioStatus> fromStatusList = TicketAPI.getStatuses(module,criteria);

        UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
        List<User> doneByUsers = userBean.getUsers(null,false,true,doneBy);

        if(CollectionUtils.isNotEmpty(doneByUsers) && CollectionUtils.isNotEmpty(fromStatusList)){
            Map<Long,User> doneByUserMap = doneByUsers.stream().collect(Collectors.toMap(User::getId,Function.identity()));
            Map<Long,FacilioStatus> fromStatusMap = fromStatusList.stream().collect(Collectors.toMap(FacilioStatus::getId,Function.identity()));
            for(TimelogContext timelogContext : timeLogs){
                timelogContext.setDoneBy(doneByUserMap.get(timelogContext.getDoneById()));
                timelogContext.setFromStatus(fromStatusMap.get(timelogContext.getFromStatusId()));
            }
        }
    }

}
