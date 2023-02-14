package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddAssociateResourceCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {


        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<LookupField> fillLookups=new ArrayList<LookupField>();
        LookupField associatedResource=(LookupField)modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES);
        fillLookups.add(associatedResource);
        LookupField site=(LookupField)modBean.getField("identifiedLocation", FacilioConstants.ContextNames.ASSET);
        fillLookups.add(site);
        context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, fillLookups);

        return false;
    }
}
