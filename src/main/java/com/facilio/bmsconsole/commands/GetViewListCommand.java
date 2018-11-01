package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetViewListCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule moduleObj = null;
		Map<String,FacilioView> viewMap = ViewFactory.getModuleViews(moduleName);
		List<FacilioView> dbViews = null;
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			dbViews = ViewAPI.getAllViews(moduleName);
		} else {
			if (!moduleName.equals("approval")) {
				moduleObj = modBean.getModule(moduleName);
				dbViews = ViewAPI.getAllViews(moduleObj.getModuleId());
			}
		}
		
		if (dbViews != null) {
			for(FacilioView view: dbViews) {
				viewMap.put(view.getName(), view);
			}
		}
		
		viewMap.entrySet().removeIf(enrty -> enrty.getValue().isHidden());
		List<FacilioView> allViews = new ArrayList<>(viewMap.values());
		allViews.sort(Comparator.comparing(FacilioView::getSequenceNumber, (s1, s2) -> {
			if(s1 == s2){
		         return 0;
		    }
		    return s1 < s2 ? -1 : 1;
		}));
		context.put(FacilioConstants.ContextNames.VIEW_LIST, allViews);
		return false;
	}

}
