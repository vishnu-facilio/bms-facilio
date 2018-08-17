package com.facilio.screen.action;

import com.facilio.screen.context.RemoteScreenContext;
import com.facilio.screen.context.ScreenContext;
import com.facilio.screen.util.ScreenUtil;
import com.opensymphony.xwork2.ActionSupport;

public class ScreenAction extends ActionSupport {

	public ScreenContext screenContext;
	
	public RemoteScreenContext remoteScreenContext;
	
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
