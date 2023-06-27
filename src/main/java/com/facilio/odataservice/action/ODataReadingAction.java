package com.facilio.odataservice.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.odataservice.service.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.struts2.ServletActionContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Getter @Setter
public class ODataReadingAction extends FacilioAction {

    private  String readingViewName;
    private static final Logger LOGGER = LogManager.getLogger(ODataReadingAction.class.getName());
    public String execute() throws Exception {
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ODATA_API)) {
            LOGGER.info("Connection Established");
            HttpServletRequest req = ServletActionContext.getRequest();
            HttpServletResponse resp = ServletActionContext.getResponse();
            OData odata = OData.newInstance();
            ReadingsEdmProvider provider = new ReadingsEdmProvider();
            ServiceMetadata edm = odata.createServiceMetadata(provider, new ArrayList<EdmxReference>());
            ODataHttpHandler handler = odata.createHandler(edm);
            handler.register(new ReadingsEntityCollectionProcessor());
            handler.register(new ReadingsServiceDocumentProcessor());
            handler.process(req, resp);
        }else {
            LOGGER.info("No License Enabled for OData Connection");
        }
        return NONE;
    }
}
