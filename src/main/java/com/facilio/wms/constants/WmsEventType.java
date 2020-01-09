package com.facilio.wms.constants;

public interface WmsEventType {

	public static enum Module implements WmsEventType {
		RECORD_UPDATE;
	}
	
	public static enum RemoteScreen implements WmsEventType {
		REFRESH;
	}
	
	public static enum Device implements WmsEventType {
		RELOAD_CONF,
		DISCONNECT;
	}
	
	public static enum VisitorKiosk implements WmsEventType {
		CHECK_PRINTER_STATUS;
	}
}
