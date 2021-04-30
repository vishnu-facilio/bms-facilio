package com.facilio.bmsconsole.context;
import java.io.Serializable;

import com.facilio.modules.FacilioIntEnum;

public class PrinterContext implements Serializable{
	public static enum ConnectMode implements FacilioIntEnum {
    	WIFI("Wifi"),
    	ETHERNET("Ethernet"),
    	
        ;

        public static ConnectMode valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
        private String name; 

        ConnectMode (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

	public static enum PrinterModel implements FacilioIntEnum {
    	BROTHER_QL_820NWB("BROTHER-QL-820NWB"),
    	BROTHER_QL_720NW("Brother QL-720NW"),
    	BROTHER_QL_710W("Brother QL-710W"),
        ;

        public static PrinterModel valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
        private String name;

        PrinterModel (String name) {
            this.name = name;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
	
	private static final long serialVersionUID = 1L;

	private long id = -1;

	
	private String name;

	private String ip;

	private String info;

	Boolean connected;

	private PrinterModel printerModel;

	private ConnectMode connectMode;

	public Boolean getConnected() {
		return connected;
	}

	public int getConnectMode() {
		if(connectMode!=null)
		{
			return connectMode.getIndex();
		}
		return -1;
	}

	public ConnectMode getConnectModeEnum() { 
		return this.connectMode;
	}
	public long getId() {
		return id;
	}
	public String getInfo() {
		return info;
	}
     
	public String getIp() {
		return ip;
	}
	public int getPrinterModel() {
		if(printerModel!=null)
		{
			return printerModel.getIndex();
		}
		return -1;
	}
	
	
	
	public PrinterModel getPrinterModelEnum() {
		return printerModel;
	}
	
	public String getName() {
		return name;
	}
	

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}
	public void setConnectMode(ConnectMode connectMode) {
		this.connectMode = connectMode;
	}
	
    public void setConnectMode(int connectMode) {
		this.connectMode = ConnectMode.valueOf(connectMode);
	}
    
    public void setId(long id) {
		this.id = id;
	}
    
    public void setInfo(String info) {
		this.info = info;
	}
	
	public void setIp(String ip) {
		this.ip = ip;
	}
	

	public void setPrinterModel(int model) {
		this.printerModel = PrinterModel.valueOf(model);
	}
	public void setPrinterModel(PrinterModel model) {
		this.printerModel = model;
	}
	
    public void setName(String name) {
		this.name = name;
	}
	
	
}
