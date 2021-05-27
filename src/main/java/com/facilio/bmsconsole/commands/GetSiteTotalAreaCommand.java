package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class GetSiteTotalAreaCommand extends FacilioCommand  {
    public boolean executeCommand(Context context) throws Exception {
        List<BaseSpaceContext> sites = CommonCommandUtil.getMySites();
        long totalArea = 0;
        for (BaseSpaceContext site: sites) {
            if (site.getArea() > 0) {
                totalArea += site.getArea();
            }
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField areaField = modBean.getField("area", "basespace");
        String unitLabel = null;
        if(areaField instanceof NumberField) {
			NumberField numberField = (NumberField) areaField;
			unitLabel = numberField.getUnit();
        }
        context.put(FacilioConstants.ContextNames.UNIT, unitLabel);
        context.put(FacilioConstants.ContextNames.TOTAL_AREA, totalArea);

        return false;
    }
}
