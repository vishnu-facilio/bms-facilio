package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.amazonaws.http.HttpMethodName;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RelatedDataAction extends RESTAPIHandler {
    private static final Logger LOGGER = Logger.getLogger(RelatedDataAction.class.getName());

    private String throwValidationException(String message) {
        this.setMessage(message);
        this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
        LOGGER.log(Level.SEVERE, message);
        return ERROR;
    }

    private String relationName;
    private String relatedModuleName;
    private String relatedFieldName;
    private boolean unAssociated;
    @Override
    public String execute() throws Exception {
        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        switch (httpMethod) {
            case GET:
                FacilioChain getChain = ReadOnlyChainFactoryV3.getRelatedDataChain();

                FacilioContext chainContext = getChain.getContext();
                addGetChainContext(chainContext);
                if (StringUtils.isNotEmpty(relationName)) {
                    chainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATIONSHIP);
                    chainContext.put(FacilioConstants.ContextNames.RELATION_NAME, relationName);
                } else {
                    chainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATED_LIST);
                    chainContext.put(FacilioConstants.ContextNames.RELATED_MODULE_NAME, relatedModuleName);
                    chainContext.put(FacilioConstants.ContextNames.RELATED_FIELD_NAME, relatedFieldName);
                }
                getChain.execute();

                setData((JSONObject) chainContext.get(FacilioConstants.ContextNames.RESULT));
                if (chainContext.containsKey(FacilioConstants.ContextNames.META)) {
                    setMeta((JSONObject) chainContext.get(FacilioConstants.ContextNames.META));
                }

                return SUCCESS;

            case POST:
                FacilioChain associateChain = ReadOnlyChainFactoryV3.getAssociateDissociateDataChain();

                FacilioContext associateChainContext = associateChain.getContext();
                associateChainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATIONSHIP);
                associateChainContext.put(FacilioConstants.ContextNames.RELATIONSHIP_ACTION_TYPE, "ASSOCIATE");
                addDataAlterChainContext(associateChainContext);
                associateChain.execute();

                setData((JSONObject) associateChainContext.get(FacilioConstants.ContextNames.RESULT));
                return SUCCESS;

            case PATCH:
                FacilioChain dissociateChain = ReadOnlyChainFactoryV3.getAssociateDissociateDataChain();

                FacilioContext dissociateChainContext = dissociateChain.getContext();
                dissociateChainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATIONSHIP);
                dissociateChainContext.put(FacilioConstants.ContextNames.RELATIONSHIP_ACTION_TYPE, "DISSOCIATE");
                addDataAlterChainContext(dissociateChainContext);
                dissociateChain.execute();

                setData((JSONObject) dissociateChainContext.get(FacilioConstants.ContextNames.RESULT));
                return SUCCESS;

            default:
                return throwValidationException("Unsupported HTTP Method : " + httpMethod.name());
        }

    }

    public void addGetChainContext(FacilioContext context) throws Exception {
        context.put("unAssociated", unAssociated);
        context.put(FacilioConstants.ContextNames.ID, this.getId());
        context.put(Constants.WITH_COUNT, this.getWithCount());
        context.put(FacilioConstants.ContextNames.CV_NAME, this.getViewName());
        context.put(FacilioConstants.ContextNames.FILTERS, this.getFilters());
        context.put(FacilioConstants.ContextNames.ORDER_BY, this.getOrderBy());
        context.put(FacilioConstants.ContextNames.ORDER_TYPE, this.getOrderType());
        context.put(FacilioConstants.ContextNames.PAGE, this.getPage());
        context.put(FacilioConstants.ContextNames.PARAMS, this.getParams());
        context.put(FacilioConstants.ContextNames.PER_PAGE, this.getPerPage());
        context.put(FacilioConstants.ContextNames.SEARCH, this.getSearch());
        context.put(Constants.EXCLUDE_PARENT_CRITERIA, this.getExcludeParentFilter());
        context.put(Constants.WITHOUT_CUSTOMBUTTONS, this.getWithoutCustomButtons());
        context.put(FacilioConstants.ContextNames.FETCH_ONLY_VIEW_GROUP_COLUMN,this.getFetchOnlyViewGroupColumn());
        context.put(FacilioConstants.ContextNames.QUERY_PARAMS, this.getQueryParameters());
        context.put(FacilioConstants.ContextNames.CLIENT_FILTER_CRITERIA, this.getClientCriteria());
    }

    public void addDataAlterChainContext(FacilioContext context) {
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.RELATION_NAME, relationName);
        context.put(FacilioConstants.ContextNames.PARAMS, getParams());
        context.put(FacilioConstants.ContextNames.DATA, getData());
        context.put(FacilioConstants.ContextNames.ID, getId());
    }
}
