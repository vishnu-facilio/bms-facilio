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
import org.apache.commons.collections.CollectionUtils;import org.json.simple.JSONObject;
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
                 .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(id), NumberOperators.EQUALS))
                 .orderBy("ID DESC");

        Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        if (filterCriteria != null && !filterCriteria.isEmpty()) {
            builder.andCriteria(filterCriteria);
        }
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        List<TimelogContext> timeLogs = FieldUtil.getAsBeanListFromMapList(builder.get(),TimelogContext.class);
        setObjectFields(timeLogs,parentModule);

        context.put(FacilioConstants.ContextNames.TIMELOGS, timeLogs);
        return false;
    }

    private void setObjectFields(List<TimelogContext> timeLogs,FacilioModule module) throws  Exception{

        if(timeLogs.isEmpty()){
            return;
        }

        Set<Long> fromStatusIds = new HashSet<>();
        Set<Long> doneByIds = new HashSet<>();

        for(TimelogContext timelogContext : timeLogs){
            fromStatusIds.add(timelogContext.getFromStatusId());
            if(timelogContext.getDoneById()>0) {
              doneByIds.add(timelogContext.getDoneById());
            }
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(fromStatusIds,ModuleFactory.getTicketStatusModule()));
        List<FacilioStatus> fromStatusList = TicketAPI.getStatuses(module,criteria);

        List<User> doneByUsers = null;

        if(CollectionUtils.isNotEmpty(doneByIds)) {
            UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
            doneByUsers = userBean.getUsers(null, false, true, doneByIds);
        }
        if(doneByUsers == null){
            doneByUsers = new ArrayList<>();
        }
        if (CollectionUtils.isNotEmpty(fromStatusList)) {
                Map<Long, User> doneByUserMap = doneByUsers.stream().collect(Collectors.toMap(User::getId, Function.identity()));
                Map<Long, FacilioStatus> fromStatusMap = fromStatusList.stream().collect(Collectors.toMap(FacilioStatus::getId, Function.identity()));
                for (TimelogContext timelogContext : timeLogs) {
                    timelogContext.setDoneBy(doneByUserMap.get(timelogContext.getDoneById()));
                    timelogContext.setFromStatus(fromStatusMap.get(timelogContext.getFromStatusId()));
                }
        }

    }

}
