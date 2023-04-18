package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.json.simple.JSONObject;

import java.util.List;

public class AccessibleSpacesAction extends V3Action {

    private List<Long> spaceIds;

    public List<Long> getSpaceIds() {
        return spaceIds;
    }

    public void setSpaceIds(List<Long> spaceIds) {
        this.spaceIds = spaceIds;
    }

    private Long ouId;

    public Long getOuId() {
        return ouId;
    }

    public void setOuId(Long ouId) {
        this.ouId = ouId;
    }

    private Long peopleId;

    public Long getPeopleId() {
        return peopleId;
    }

    public void setPeopleId(Long peopleId) {
        this.peopleId = peopleId;
    }

    public String add() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getAddAccessibleSpaceChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, spaceIds);
        context.put(FacilioConstants.ContextNames.ORG_USER_ID, ouId);
        context.put(FacilioConstants.ContextNames.PEOPLE_ID, peopleId);

        chain.execute();
        setData("message", "Added Successfully!");
        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getDeleteAccessibleSpaceChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, spaceIds);
        context.put(FacilioConstants.ContextNames.ORG_USER_ID, ouId);
        context.put(FacilioConstants.ContextNames.PEOPLE_ID, peopleId);

        chain.execute();
        setData("message", "Deleted Successfully!");

        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getFetchAccessibleSpaceChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ORG_USER_ID, ouId);
        context.put(FacilioConstants.ContextNames.PEOPLE_ID, peopleId);

        JSONObject pagination = new JSONObject();
        pagination.put("page",getPage());
        pagination.put("perPage",getPerPage());
        if(getPerPage() < 0){
            pagination.put("perPage",5000);
        }
        context.put(FacilioConstants.ContextNames.PAGINATION,pagination);

        if(getSearch() != null){
            context.put(FacilioConstants.ContextNames.SEARCH,getSearch());
        }

        if(getWithCount()){
            context.put(FacilioConstants.ContextNames.FETCH_COUNT,true);
        }

        chain.execute();
        List<BaseSpaceContext> baseSpaces = (List<BaseSpaceContext>)context.get(FacilioConstants.ContextNames.ACCESSIBLE_SPACE);
        if(context.get(FacilioConstants.ContextNames.FETCH_COUNT) != null){
           if(context.get(FacilioConstants.ContextNames.COUNT) != null) {
               setData("count",context.get(FacilioConstants.ContextNames.COUNT));
           }
           else {
               setData("count",0);
           }
        }
        setData(FacilioConstants.ContextNames.ACCESSIBLE_SPACE, baseSpaces);


        return SUCCESS;
    }
}
