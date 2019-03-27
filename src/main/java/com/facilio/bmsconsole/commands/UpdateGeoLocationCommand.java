package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.LookupFieldMeta;
import com.facilio.bmsconsole.modules.UpdateRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.util.FacilioUtil;

public class UpdateGeoLocationCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(UpdateGeoLocationCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.ASSET);
		String location = (String) context.get(FacilioConstants.ContextNames.LOCATION);
		
		if (asset != null && asset.isGeoLocationEnabled() && StringUtils.isNotEmpty(location)) {
			LOGGER.info("Received Location for asset "+asset.getId()+" is "+location);
			JSONObject info = null;
			String newLocation = null, geoLocation = null;
			Boolean isDesignatedLocation = null;
			double distanceMoved = -1;
			if (StringUtils.isEmpty(asset.getGeoLocation())) {
				geoLocation = location;
				newLocation = location;
			}
			else {
				String[] latLng = location.trim().split("\\s*,\\s*");
				double lat = Double.parseDouble(latLng[0]);
				double lng = Double.parseDouble(latLng[1]);
	
				if (!asset.isDesignatedLocation() && !isWithInLocation(asset.getCurrentLocation(), lat, lng, asset.getBoundaryRadius())) {
					newLocation = location;
				}
				
				distanceMoved = getDistance(asset.getGeoLocation(), lat, lng);
				boolean isWithinGeoLocation = StringUtils.isNoneEmpty(asset.getGeoLocation()) && distanceMoved <= asset.getBoundaryRadius();
				if (!asset.isDesignatedLocation()) {
					if (isWithinGeoLocation) {
						newLocation = asset.getGeoLocation();
						isDesignatedLocation = true;
						distanceMoved = 0;
					}
				}
				else if (!isWithinGeoLocation) {
					newLocation = location;
					isDesignatedLocation = false;
				}
			}
			
			if (newLocation != null) {
				info = new JSONObject();
				info.put("currentLocation", newLocation);
				if (distanceMoved != 0 && distanceMoved != -1) {
					info.put("distanceMoved", distanceMoved);
				}
				if (isDesignatedLocation != null) {
					info.put("designatedLocation", isDesignatedLocation);
				}
				JSONObject newinfo = new JSONObject();
                newinfo.put("Location",info);
				LOGGER.info("Asset Acitibity "+info.toJSONString());
				updateAsset(asset, geoLocation, newLocation, isDesignatedLocation, distanceMoved);
				CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.LOCATION, newinfo, (FacilioContext) context);
			}
		}
		return false;
	}
	
	private boolean isWithInLocation (String location, double lat, double lng, int boundaryRadius) {
		if (StringUtils.isEmpty(location)) {
			return false;
		}
		return getDistance(location, lat, lng) <= boundaryRadius;
	}
	
	private double getDistance (String location, double lat, double lng) {
		if (StringUtils.isEmpty(location)) {
			return 0;
		}
		String[] latLng = location.trim().split("\\s*,\\s*");
		double prevLat = Double.parseDouble(latLng[0]);
		double prevLng = Double.parseDouble(latLng[1]);
		
		double distance = FacilioUtil.calculateHaversineDistance(prevLat, prevLng, lat, lng);
		LOGGER.info("Distance between ("+location+") and ("+lat+", "+lng+") is "+distance);
		return distance;
	}
	
	private SiteContext getNearestLocation(double lat, double lng) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<SiteContext> sites = SpaceAPI.getAllSites(Collections.singletonList(new LookupFieldMeta((LookupField) modBean.getField("location", FacilioConstants.ContextNames.SITE))));
		
		if (CollectionUtils.isNotEmpty(sites)) {
			for (SiteContext site : sites) {
				if (isWithinLocation(site, lat, lng)) {
					return site;
				}
			}
		}
		
		return null;
	}
	
	private static final int MAX_DISTANCE = 1000; //meter 
	private boolean isWithinLocation (SiteContext site, double lat, double lng) {
		if (site == null || site.getLocation() == null || site.getLocation().getLat() == -1 || site.getLocation().getLng() == -1) {
			return false;
		}
		return FacilioUtil.calculateHaversineDistance(site.getLocation().getLat(), site.getLocation().getLng(), lat, lng) <= MAX_DISTANCE;
	}
	
	private void updateAsset(AssetContext asset, String geoLocation, String newLocation, Boolean isDesignatedLocation, double distanceMoved) throws Exception {
		AssetContext updateAsset = new AssetContext();
		updateAsset.setCurrentLocation(newLocation);
		asset.setCurrentLocation(newLocation);
		
		if (geoLocation != null) {
			updateAsset.setGeoLocation(geoLocation);
			asset.setGeoLocation(geoLocation);
			isDesignatedLocation = true;
		}
		if (isDesignatedLocation != null) {
			updateAsset.setDesignatedLocation(isDesignatedLocation);
			asset.setDesignatedLocation(isDesignatedLocation);
		}
		if (distanceMoved != -1) {
			updateAsset.setDistanceMoved(distanceMoved);
			asset.setDistanceMoved(distanceMoved);
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
																.fields(modBean.getAllFields(FacilioConstants.ContextNames.ASSET))
																.module(module)
																.andCondition(CriteriaAPI.getIdCondition(asset.getId(), module))
																;
		updateBuilder.update(updateAsset);
	}

}
