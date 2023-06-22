package com.facilio.fsm.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fsm.commands.FSMReadOnlyChainFactory;
import com.facilio.v3.V3Action;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;

@Getter @Setter
public class fsmAction extends V3Action {
    private List<Long> resourceIds;
    private Criteria criteria;
    private Long startTime;
    private Long endTime;
    private Long boardId;
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

        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }
    public String eventsList() throws Exception{
        FacilioChain chain = FSMReadOnlyChainFactory.fetchEventsListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PEOPLE_IDS,getResourceIds());
        context.put(FacilioConstants.ContextNames.START_TIME,getStartTime());
        context.put(FacilioConstants.ContextNames.END_TIME,getEndTime());
        context.put(FacilioConstants.Dispatcher.BOARD_ID,getBoardId());
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.Dispatcher.EVENTS));
        return SUCCESS;
    }
}
