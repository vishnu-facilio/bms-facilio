package com.facilio.fsm.actions;


import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fsm.commands.FSMReadOnlyChainFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class fsmAction extends V3Action {
    private String resourceIds;
    private Criteria criteria;
    private Long startTime;
    private Long endTime;
    private Long boardId;

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    private String viewName;

    private String moduleName;
    private long appId = -1;

    public String resourceList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchPeopleListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PAGE,getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE,getPerPage());
        context.put(FacilioConstants.ContextNames.START_TIME,getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME,getEndTime());
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        context.put(FacilioConstants.ContextNames.SEARCH,getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS,getFilters());
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.Dispatcher.RESOURCES));
        return SUCCESS;
    }
    public String eventsList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchEventsListChain();
        FacilioContext context = chain.getContext();
        if (StringUtils.isNotEmpty(resourceIds)) {
            String[] ids = FacilioUtil.splitByComma(resourceIds);
            List<Long> defaultIdList = Arrays.stream(ids).map(Long::parseLong).collect(Collectors.toList());
            context.put(FacilioConstants.ContextNames.PEOPLE_IDS, defaultIdList);
        }
        context.put(FacilioConstants.ContextNames.START_TIME,getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME,getEndTime());
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.Dispatcher.EVENTS));
        return SUCCESS;
    }
    public String serviceAppointmentList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchServiceAppointmentListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        context.put(FacilioConstants.ContextNames.APP_ID, appId);
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        context.put(FacilioConstants.ContextNames.VIEW_NAME,getViewName());
        context.put(FacilioConstants.ContextNames.PAGE,getPage());
        context.put(FacilioConstants.ContextNames.PER_PAGE,getPerPage());
        context.put(FacilioConstants.ContextNames.SEARCH,getSearch());
        context.put(FacilioConstants.ContextNames.FILTERS,getFilters());

        chain.execute();
        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }
}
