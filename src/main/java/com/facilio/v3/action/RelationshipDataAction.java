package com.facilio.v3.action;

import com.facilio.relation.util.RelationshipDataUtil;
import com.facilio.v3.RESTAPIHandler;
import org.json.simple.JSONObject;

import java.util.logging.Logger;

//TODO remove static methods, instantiate as object, better when testing
public class RelationshipDataAction extends RESTAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(RelationshipDataAction.class.getName());

    public String associateRelation() throws Exception {
        JSONObject result = RelationshipDataUtil.associateRelation(getModuleName(), getData(), getQueryParameters(), getParams());
        setData(result);
        return SUCCESS;
    }

    public String dissociateRelation() throws Exception {
        JSONObject result = RelationshipDataUtil.dissociateRelation(getModuleName(), getData(), getQueryParameters(), getParams());
        this.setData(result);
        return SUCCESS;
    }


}
