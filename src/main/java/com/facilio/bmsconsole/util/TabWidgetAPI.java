package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.TabWidgetContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.sql.GenericSelectRecordBuilder;

public class TabWidgetAPI {
	
	@SuppressWarnings({ "deprecation" })
	public static TabWidgetContext getTabWidget(String tabLinkName) throws Exception {
		try {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.select(FieldFactory.getTabWidgetFields())
					.table("Tab_Widget")
					.andCustomWhere("Tab_Widget.ORGID = ? AND Tab_Widget.TAB_LINK_NAME = ?", AccountUtil.getCurrentOrg().getOrgId(), tabLinkName);
	
			List<Map<String, Object>> tabWidgets = selectBuider.get();
			TabWidgetContext tabWidget = FieldUtil.getAsBeanFromMap(tabWidgets.get(0), TabWidgetContext.class);
			
			GenericSelectRecordBuilder connectedAppSelectBuider = new GenericSelectRecordBuilder()
					.select(FieldFactory.getConnectedAppFields())
					.table("Connected_App")
					.andCustomWhere("Connected_App.ORGID = ? AND Connected_App.CONNECTED_APP_ID = ?", AccountUtil.getCurrentOrg().getOrgId(), tabWidget.getConnectedAppId());
	
			List<Map<String, Object>> connectedApps = connectedAppSelectBuider.get();
			ConnectedAppContext connectedApp = FieldUtil.getAsBeanFromMap(connectedApps.get(0), ConnectedAppContext.class);
			
			tabWidget.setConnectedApp(connectedApp);
			return tabWidget;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
