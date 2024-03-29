package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

public class VisitorKioskContext extends DeviceContext{
	private static final long serialVersionUID = 1L;

	private long id = -1;
	private long printerId = -1;
	private long kioskForSpaceId = -1;
	
	private PrinterContext printer;
	private BaseSpaceContext kioskForSpace;

	@Getter@Setter
	private String countryCode;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getPrinterId() {
		return printerId;
	}
	public void setPrinterId(long printerId) {
		this.printerId = printerId;
	}
	public long getKioskForSpaceId() {
		return kioskForSpaceId;
	}
	public void setKioskForSpaceId(long kioskForSpaceId) {
		this.kioskForSpaceId = kioskForSpaceId;
	}
	public PrinterContext getPrinter() {
		return printer;
	}
	public void setPrinter(PrinterContext printer) {
		this.printer = printer;
	}
	public BaseSpaceContext getKioskForSpace() {
		return kioskForSpace;
	}
	public void setKioskForSpace(BaseSpaceContext kioskForSpace) {
		this.kioskForSpace = kioskForSpace;
	}
	
	
	
	
}
