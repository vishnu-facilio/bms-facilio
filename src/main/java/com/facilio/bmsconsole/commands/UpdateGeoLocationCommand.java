package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

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
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.LookupField;

public class UpdateGeoLocationCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(UpdateGeoLocationCommand.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
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
	
				if (!asset.isDesignatedLocation() && !AssetsAPI.isWithInLocation(asset.getCurrentLocation(), lat, lng, asset.getBoundaryRadius())) {
					newLocation = location;
				}
				
				distanceMoved = AssetsAPI.getDistance(asset.getGeoLocation(), lat, lng);
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
				updateAsset(asset, geoLocation, newLocation, isDesignatedLocation, distanceMoved, context);
				CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.LOCATION, newinfo, (FacilioContext) context);
			}
		}
		return false;
	}
	
	
	private SiteContext getNearestLocation(double lat, double lng) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<SiteContext> sites = SpaceAPI.getAllSites(Collections.singletonList((LookupField) modBean.getField("location", FacilioConstants.ContextNames.SITE)));
		
		if (CollectionUtils.isNotEmpty(sites)) {
			for (SiteContext site : sites) {
				if (AssetsAPI.isWithinSiteLocation(site, lat, lng)) {
					return site;
				}
			}
		}
		
		return null;
	}
	
	private void updateAsset(AssetContext asset, String geoLocation, String newLocation, Boolean isDesignatedLocation, double distanceMoved, Context context) throws Exception {
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
		updateBuilder.withChangeSet(AssetContext.class).update(updateAsset);
		
		context.put(FacilioConstants.ContextNames.CHANGE_SET, updateBuilder.getChangeSet());
	}

}
