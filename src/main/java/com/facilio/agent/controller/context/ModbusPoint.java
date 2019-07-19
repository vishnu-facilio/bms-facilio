package com.facilio.agent.controller.context;



public class ModbusPoint extends Point
{
	private static final long serialVersionUID = 1L;
	
	private long registerNumber=-1;
	public long getRegisterNumber() {
		return registerNumber;
	}
	public void setRegisterNumber(long registerNumber) {
		this.registerNumber = registerNumber;
	}

	private int functionCode=-1;
	public int getFunctionCode() {
		return functionCode;
	}
	public void setFunctionCode(int functionCode) {
		this.functionCode = functionCode;
	}

	private int modbusDataType=-1;

	public int getModbusDataType() {
		return modbusDataType;
	}
	public void setModbusDataType(int modbusDataType) {
		this.modbusDataType = modbusDataType;
	}

}
