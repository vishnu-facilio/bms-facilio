package com.facilio.screen.action;

import java.util.List;

import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.opensymphony.xwork2.ActionSupport;

public class ScreenAction extends ActionSupport {

	public List<ScreenContext> screenContexts;
	public List<RemoteScreenContext> remoteScreenContexts;
	public ScreenContext screenContext;
	
	public RemoteScreenContext remoteScreenContext;
	
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
			ScreenUtil.updateScreen(screenContext);
		}
		return SUCCESS;
	}
	
	public String addRemoteScreen() throws Exception {
		
		if(screenContext != null) {
			ScreenUtil.addRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
	
	public String updateRemoteScreen() throws Exception {
		
		if(screenContext != null) {
			ScreenUtil.updateRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
	
	public String deleteRemoteScreen() throws Exception {
		
		if(screenContext != null) {
			ScreenUtil.deleteRemoteScreen(remoteScreenContext);
		}
		return SUCCESS;
	}
}
