package com.facilio.leed.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.EnergyDataContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class BuildingHome extends ActionSupport {

	private BuildingContext building;
	private long buildingId;
	
	public String execute() throws Exception
	{
		return null;
	}
	
	public BuildingContext getBuilding() 
	{
		return building;
	}
	public void setBuilding(BuildingContext building) 
	{
		this.building = building;
	}
	
	
	public long getBuildingId() 
	{
		return buildingId;
	}
	public void setBuildingId(long buildingId) 
	{
		this.buildingId = buildingId;
	}

	public String detailspage() throws Exception
	{
		setBuildingId(1);
		
		FacilioContext context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		
		Chain getBuildingChain = FacilioChainFactory.getBuildingUtilityProviderDetailsChain();
		
		getBuildingChain.execute(context);
		
		setBuilding((BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING));
		
		System.out.println(">>>>>>>>>> Comes Here >>>>>>>>>>>");
		
		return SUCCESS;
	}
	
	private String leedID;
	
	public String getLeedID()
	{
		return leedID;
	}
	
	public void setLeedID(String leedID)
	{
		this.leedID = leedID;
	}
	
	private String meterID;
	
	public String getMeterID()
	{
		return meterID;
	}
	
	public void setMeterID(String meterID)
	{
		this.meterID = meterID;
	}
	
	private List<EnergyDataContext> energyData;
	
	public List<EnergyDataContext> getEnergyData() 
	{
		return energyData;
	}
	
	public void setEnergyData(List<EnergyDataContext> energyData) 
	{
		this.energyData = energyData;
	}
	
	public String addConsumptionData() throws Exception
	{
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ID, getBuildingId());
		context.put(FacilioConstants.ContextNames.LeedID, getLeedID());
		context.put(FacilioConstants.ContextNames.MeterID, getMeterID());
		context.put(FacilioConstants.ContextNames.COMSUMPTIONDATA_LIST, getEnergyData());
		Chain addConsumptionDataChain = FacilioChainFactory.addConsumptionDataChain();
		addConsumptionDataChain.execute(context);
		
		
		return SUCCESS;
		
	}
}
