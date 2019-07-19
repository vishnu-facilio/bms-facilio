package com.facilio.agent.controller.context;




public class OPCUAPoint extends Point
{
	private static final long serialVersionUID = 1L;
	
	
	private int nameSpace=-1;
	public int getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(int nameSpace) {
		this.nameSpace = nameSpace;
	}

	private String identifier;
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier=identifier;
	}
	
}
