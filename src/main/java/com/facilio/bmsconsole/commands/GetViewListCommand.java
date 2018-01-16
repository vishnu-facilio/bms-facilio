package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FacilioModule;
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
		FacilioModule moduleObj = modBean.getModule(moduleName);
		Map<String,FacilioView> viewMap = ViewFactory.getModuleViews(moduleName);
		List<FacilioView> dbViews = ViewAPI.getAllViews(moduleObj.getModuleId(), AccountUtil.getCurrentOrg().getOrgId());
		for(FacilioView view: dbViews) {
			viewMap.put(view.getName(), view);
		}
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
