package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.context.ConnectedAppContext;
import com.facilio.bmsconsole.context.TabWidgetContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class TabWidgetAPI {
	
	@SuppressWarnings({ "deprecation" })
	public static TabWidgetContext getTabWidget(String tabLinkName) throws Exception {
		TabWidgetContext tabWidget = new TabWidgetContext();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getTabWidgetFields())
					.table("Tab_Widget")
					.andCustomWhere("Tab_Widget.ORGID = ? AND Tab_Widget.TAB_LINK_NAME = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), tabLinkName);
	
			List<Map<String, Object>> tabWidgets = selectBuider.get();
			BeanUtils.populate(tabWidget, tabWidgets.get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		ConnectedAppContext connectedApp = new ConnectedAppContext();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(FieldFactory.getConnectedAppFields())
					.table("Connected_App")
					.andCustomWhere("Connected_App.ORGID = ? AND Connected_App.CONNECTED_APP_ID = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), tabWidget.getConnectedAppId());
	
			List<Map<String, Object>> connectedApps = selectBuider.get();
			BeanUtils.populate(connectedApp, connectedApps.get(0));
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		tabWidget.setConnectedApp(connectedApp);
		return tabWidget;
	}
}
