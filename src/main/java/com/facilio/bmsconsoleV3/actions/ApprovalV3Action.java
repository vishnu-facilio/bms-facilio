package com.facilio.bmsconsoleV3.actions;

import com.amazonaws.http.HttpMethodName;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.action.RestAPIHandlerV3;
import com.facilio.v3.context.ConfigParams;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApprovalV3Action extends RESTAPIHandler {

    private static final Logger LOGGER = Logger.getLogger(RestAPIHandlerV3.class.getName());

    private static final long serialVersionUID = 1L;
    @Getter @Setter
    private Map<String, List<Object>> queryParameters;
    private String throwValidationException(String message) {

        this.setMessage(message);
        this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
        LOGGER.log(Level.SEVERE, message);
        return ERROR;
    }
    public String execute() throws Exception {

        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        switch (httpMethod) {
            case GET:
                return pendingApprovalList();
            default:
                return throwValidationException("Unsuported HTTP Method : " + httpMethod.name());
        }
    }
    public String pendingApprovalList () throws Exception {
        FacilioStatus status = TicketAPI.getApprovalStatus("Requested");
        api currentApi = currentApi();
        Criteria criteria = new Criteria();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField("approvalStatus", getModuleName());
        criteria.addAndCondition(CriteriaAPI.getCondition(field, String.valueOf(status.getId()), PickListOperators.IS));
        ConfigParams configParams = new ConfigParams();
        configParams.setSelectableFieldNames(getSelectableFieldNames());
        FacilioContext listContext = V3Util.fetchList(this.getModuleName(), (currentApi == api.v3), this.getViewName(), this.getFilters(), this.getExcludeParentFilter(), this.getClientCriteria(),
                    this.getOrderBy(), this.getOrderType(), this.getSearch(), this.getPage(), this.getPerPage(), this.getWithCount(), getQueryParameters(), criteria,configParams);

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);
        this.setData(recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            this.setMeta((JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }
        return SUCCESS;

    }

}
