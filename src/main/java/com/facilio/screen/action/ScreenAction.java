package com.facilio.screen.action;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.facilio.wms.message.WmsRemoteScreenMessage;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionSupport;

import java.util.List;
import java.util.Map;

public class ScreenAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<ScreenContext> screenContexts;
	public List<RemoteScreenContext> remoteScreenContexts;
	public ScreenContext screenContext;
	
	public RemoteScreenContext remoteScreenContext;
	
	public Long getRemoteScreenId() {
		return remoteScreenId;
	}

	public void setRemoteScreenId(Long remoteScreenId) {
		this.remoteScreenId = remoteScreenId;
	}

	public Long getScreenId() {
		return screenId;
	}

	public void setScreenId(Long screenId) {
		this.screenId = screenId;
	}

	public Long remoteScreenId,screenId;
	

	public List<ScreenContext> getScreenContexts() {
		return screenContexts;
	}

	public void setScreenContexts(List<ScreenContext> screenContexts) {
		this.screenContexts = screenContexts;
	}

	public List<RemoteScreenContext> getRemoteScreenContexts() {
		return remoteScreenContexts;
	}

	public void setRemoteScreenContexts(List<RemoteScreenContext> remoteScreenContexts) {
		this.remoteScreenContexts = remoteScreenContexts;
	}
	public ScreenContext getScreenContext() {
		return screenContext;
	}

	public void setScreenContext(ScreenContext screenContext) {
		this.screenContext = screenContext;
	}

	public RemoteScreenContext getRemoteScreenContext() {
		return remoteScreenContext;
	}

	public void setRemoteScreenContext(RemoteScreenContext remoteScreenContext) {
		this.remoteScreenContext = remoteScreenContext;
	}
	
	public String getScreens() throws Exception {
		
		screenContexts = ScreenUtil.getAllScreen();
		
		return SUCCESS;
	}
	
	public String getRemoteScreens() throws Exception {
		
		remoteScreenContexts = ScreenUtil.getAllRemoteScreen();
		
		return SUCCESS;
	}
	
	public String getRemoteScreen() throws Exception {
		
		remoteScreenContext = ScreenUtil.getRemoteScreen(remoteScreenId);
		
		return SUCCESS;
	}
	
	public String getScreen() throws Exception {
		
		screenContext = ScreenUtil.getScreen(screenId);
		
		return SUCCESS;
	}
	public String addScreen() throws Exception {
		
		if(screenContext != null) {
			screenContext.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			ScreenUtil.addScreen(screenContext);
		}
		return SUCCESS;
	}
	
	public String updateScreen() throws Exception {
		
		if(screenContext != null) {
			ScreenUtil.updateScreen(screenContext);
		}
		return SUCCESS;
	}
	
	public String deleteScreen() throws Exception {
		
		if(screenContext != null) {
			ScreenUtil.deleteScreen(screenContext);
		}
		return SUCCESS;
	}
	
	public String addRemoteScreen() throws Exception {
		
		if(remoteScreenContext != null) {
			ScreenUtil.addRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
	
	private String code;
	
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String connectRemoteScreen() throws Exception {
		
		if (code != null) {
			if (ScreenUtil.validateTVPasscode(code)) {
				if (remoteScreenContext == null) {
					remoteScreenContext = new RemoteScreenContext();
					remoteScreenContext.setName("Remote Screen 1");
				}
				remoteScreenContext.setOrgId(AccountUtil.getCurrentOrg().getId());
				remoteScreenContext.setSessionStartTime(System.currentTimeMillis());
				
				Map<String, Object> codeInfo = ScreenUtil.getTVPasscode(code);
				if (codeInfo != null && codeInfo.get("info") != null) {
					remoteScreenContext.setSessionInfo((String) codeInfo.get("info"));
				}
				
				ScreenUtil.addRemoteScreen(remoteScreenContext);
				
				long remoteScreenId = remoteScreenContext.getId();
				
				ScreenUtil.updateConnectedScreen(code, remoteScreenId);
				
				return SUCCESS;
			}
		}
		return ERROR;
	}
	
	public String refreshRemoteScreen() throws Exception {
		
		if(remoteScreenContext != null) {
			WmsApi.sendRemoteMessage(remoteScreenContext.getId(), new WmsRemoteScreenMessage().setAction(WmsRemoteScreenMessage.RemoteScreenAction.REFRESH));
		}
		return SUCCESS;
	}
	
	public String updateRemoteScreen() throws Exception {
		
		if(remoteScreenContext != null) {
			ScreenUtil.updateRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
	
	public String deleteRemoteScreen() throws Exception {
		
		if(remoteScreenContext != null) {
			ScreenUtil.deleteRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
}
