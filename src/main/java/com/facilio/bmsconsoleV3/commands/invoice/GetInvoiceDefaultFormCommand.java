package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetInvoiceDefaultFormCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule invoiceModule = modBean.getModule(FacilioConstants.ContextNames.INVOICE);
        FacilioForm defaultForm = new FacilioForm();
        ApplicationContext currentApp = AccountUtil.getCurrentAccount().getApp();
        String formName = "default_system_invoice_web_"+currentApp.getLinkName();
        if(currentApp != null) {
            defaultForm = FormsAPI.getDefaultFormFromDB(FacilioConstants.ContextNames.INVOICE,currentApp.getLinkName());
        }
        context.put(FacilioConstants.ContextNames.FORM,defaultForm);

        return false;
    }
}
