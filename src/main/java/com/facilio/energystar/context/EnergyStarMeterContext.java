package com.facilio.energystar.context;

import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateRange;

public class EnergyStarMeterContext extends ModuleBaseWithCustomFields {

	long propertyId = -1; 
	Meter_Category type;
	long meterId = -1;
	String energyStarMeterId;
	String firstBillDate;
	String meta;
	String energyStarPropertyId;
	public String getEnergyStarPropertyId() {
		return energyStarPropertyId;
	}

	@JSON(serialize=false)
	public void setEnergyStarPropertyId(String energyStarPropertyId) {
		this.energyStarPropertyId = energyStarPropertyId;
	}
	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}
	
	public String getFirstBillDate() {
		return firstBillDate;
	}
	public void setFirstBillDate(String firstBillDate) {
		this.firstBillDate = firstBillDate;
	}
	AssetContext meterContext;
	
	public AssetContext getMeterContext() {
		return meterContext;
	}
	public void setMeterContext(AssetContext meterContext) {
		this.meterContext = meterContext;
	}
	public long getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(long properyId) {
		this.propertyId = properyId;
	}
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public Meter_Category getTypeEnum() {
		return type;
	}
	public void setType(int type) {
		this.type = Meter_Category.getAllAppTypes().get(type);
	}
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public String getEnergyStarMeterId() {
		return energyStarMeterId;
	}
	public void setEnergyStarMeterId(String energyStarMeterId) {
		this.energyStarMeterId = energyStarMeterId;
	}
	
	List<DateRange> energyStarDataAvailableRanges;
	List<DateRange> energyStarDataMissingRanges;
	DateRange dataAvailableInFacilio;
	List<DateRange> energyStarDataMissingRangesThatCanBeFilled;
	
	
	public List<DateRange> getEnergyStarDataAvailableRanges() {
		return energyStarDataAvailableRanges;
	}

	public void setEnergyStarDataAvailableRanges(List<DateRange> energyStarDataAvailableRanges) {
		this.energyStarDataAvailableRanges = energyStarDataAvailableRanges;
	}

	public List<DateRange> getEnergyStarDataMissingRanges() {
		return energyStarDataMissingRanges;
	}

	public void setEnergyStarDataMissingRanges(List<DateRange> energyStarDataMissingRanges) {
		this.energyStarDataMissingRanges = energyStarDataMissingRanges;
	}

	public DateRange getDataAvailableInFacilio() {
		return dataAvailableInFacilio;
	}

	public void setDataAvailableInFacilio(DateRange dataAvailableInFacilio) {
		this.dataAvailableInFacilio = dataAvailableInFacilio;
	}

	public List<DateRange> getEnergyStarDataMissingRangesThatCanBeFilled() {
		return energyStarDataMissingRangesThatCanBeFilled;
	}

	public void setEnergyStarDataMissingRangesThatCanBeFilled(List<DateRange> energyStarDataMissingRangesThatCanBeFilled) {
		this.energyStarDataMissingRangesThatCanBeFilled = energyStarDataMissingRangesThatCanBeFilled;
	}
}
