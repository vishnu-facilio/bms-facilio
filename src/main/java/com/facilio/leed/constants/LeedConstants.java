package com.facilio.leed.constants;

import org.apache.commons.chain.impl.ChainBase;

import com.facilio.chain.FacilioChain;
import com.facilio.leed.commands.ArcLoginCommand;
import com.facilio.leed.commands.FetchLeedListCommand;
import com.facilio.leed.commands.FetchMeterListCommand;

public class LeedConstants {
	
	public static class ContextNames {
		
		public static final String COMSUMPTIONDATA_LIST = "comsumptionDataList";		
		public static final String LEEDID = "leedID";
		public static final String LEED = "LEED";
		public static final String LEEDLIST = "LEEDLIST";
		public static final String METERID = "meterID";
		public static final String METER = "METER";
		public static final String BUILDINGID = "BUILDINGID";
		public static final String METERNAME = "METERNAME";
		public static final String FUELTYPE = "FUELTYPE";
		public static final String UTILITYPROVIDER = "UtilityProvider";
		public static final String DEVICEID = "DEVICEID";
		public static final String METERLIST = "METERLIST";
		public static final String METERTYPE = "METERTYPE";
		public static final String LOGINREQ = "LoginRequired";	
		public static final String ORGID = "OrgId";
		public static final String ARCCONTEXT = "ARCCONTEXT";
		public static final String LEEDMETERCONTEXT = "LEEDMETERCONTEXT";
		
		
	}
	
	public static FacilioChain FetchLeedListChain()
	{
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new FetchLeedListCommand());
		return c;
	}
	
	public static FacilioChain ArcLoginChain()
	{
		FacilioChain c = FacilioChain.getTransactionChain();
		c.addCommand(new ArcLoginCommand());
		return c;
	}
	
	public static FacilioChain MeterListChain()
	{
		FacilioChain c = FacilioChain.getNonTransactionChain();
		c.addCommand(new FetchMeterListCommand());
		return c;
	}

}
