package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.page.factory.PageFactory;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

public class GetRelatedListMetaCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if(module == null) {
			throw new IllegalArgumentException("Invalid module name");
		}
		Page.Section relatedListSection = new Page().new Section();
		PageFactory.getRelatedListMeta(relatedListSection, module.getModuleId(), null, false);

		JSONArray relatedlistArr = new JSONArray();
		if(relatedListSection != null && !CollectionUtils.isEmpty(relatedListSection.getWidgets()) ) {
			for(PageWidget widget : relatedListSection.getWidgets()) {
				if(widget.getWidgetType() == PageWidget.WidgetType.RELATED_LIST.getValue()) {
					relatedlistArr.add(widget.getRelatedList());
				}
			}

			context.put(FacilioConstants.ContextNames.RELATED_LIST_META, relatedlistArr);
		}
		return false;
	}

}
