package com.facilio.bmsconsoleV3.actions;

import com.amazonaws.http.HttpMethodName;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.RESTAPIHandler;
import com.facilio.v3.exception.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class GlimpseAction extends RESTAPIHandler {

    private static final Logger LOGGER = Logger.getLogger(GlimpseAction.class.getName());

    private String throwValidationException(String message) {
        this.setMessage(message);
        this.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        getHttpServletResponse().setStatus(ErrorCode.VALIDATION_ERROR.getHttpStatus());
        LOGGER.log(Level.SEVERE, message);
        return ERROR;
    }

    private String moduleName;
    private String glimpseFieldName;
    private Long recordId;

    @Override
    public String execute() throws Exception {

        HttpMethodName httpMethod = HttpMethodName.fromValue(getHttpServletRequest().getMethod());

        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(glimpseFieldName) || StringUtils.isEmpty(moduleName) || recordId < 0, "Invalid request params");

        switch (httpMethod) {

            case GET:

                FacilioChain chain = ReadOnlyChainFactory.getGlimpseDetailsChain();
                FacilioContext context = chain.getContext();
                context.put(FacilioConstants.ContextNames.MODULE_NAME,getModuleName());
                context.put(FacilioConstants.ContextNames.RECORD_ID,getRecordId());
                context.put(FacilioConstants.ContextNames.GLIMPSE_FIELD_NAME,getGlimpseFieldName());
                chain.execute();

                JSONObject result = (JSONObject) context.getOrDefault(FacilioConstants.ContextNames.GLIMPSE_RECORD, null);

                if(result == null){
                    this.setData("message","The information you were looking for does not exist.");
                    return ERROR;
                }else{
                    this.setData(result);
                    return SUCCESS;
                }


            default:
                return throwValidationException("Unsupported HTTP Method : " + httpMethod.name());
        }
    }


}
