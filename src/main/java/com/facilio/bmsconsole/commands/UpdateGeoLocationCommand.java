package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		AssetContext asset = (AssetContext) context.get(FacilioConstants.ContextNames.ASSET);
		String location = (String) context.get(FacilioConstants.ContextNames.LOCATION);
		
		if (asset != null && asset.isGeoLocationEnabled() && StringUtils.isNotEmpty(location)) {
			String[] latLng = location.trim().split("\\s*,\\s*");
			double lat = Double.parseDouble(latLng[0]);
			double lng = Double.parseDouble(latLng[1]);

			JSONObject info = null;
			String newLocation = null;
			if (asset.isMoved() && !isWithInLocation(asset.getCurrentLocation(), lat, lng, asset.getBoundaryRadius())) {
				newLocation = location;
			}
			
			Boolean isMoved = null;
			boolean isWithinGeoLocation = isWithInLocation(asset.getGeoLocation(), lat, lng, asset.getBoundaryRadius());
			if (asset.isMoved()) {
				if (isWithinGeoLocation) {
					newLocation = asset.getGeoLocation();
					isMoved = false;
				}
			}
			else if (!isWithinGeoLocation) {
				newLocation = location;
				isMoved = true;
			}
			
			if (newLocation != null) {
				info = new JSONObject();
				info.put("currentLocation", newLocation);
				if (isMoved != null) {
					info.put("moved", isMoved);
				}
				updateCurrentLocation(asset.getId(), newLocation, isMoved);
				CommonCommandUtil.addActivityToContext(asset.getId(), -1, AssetActivityType.LOCATION, info, (FacilioContext) context);
			}
		}
		return false;
	}
	
	private boolean isWithInLocation (String location, double lat, double lng, int boundaryRadius) {
		if (StringUtils.isEmpty(location)) {
			return false;
		}
		String[] latLng = location.trim().split("\\s*,\\s*");
		double prevLat = Double.parseDouble(latLng[0]);
		double prevLng = Double.parseDouble(latLng[1]);
		return FacilioUtil.calculateHaversineDistance(prevLat, prevLng, lat, lng) <= boundaryRadius;
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
	
	private void updateCurrentLocation(long id, String location, Boolean isMoved) throws Exception {
		AssetContext asset = new AssetContext();
		asset.setCurrentLocation(location);
		asset.setMoved(isMoved);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		UpdateRecordBuilder<AssetContext> updateBuilder = new UpdateRecordBuilder<AssetContext>()
																.fields(modBean.getAllFields(FacilioConstants.ContextNames.ASSET))
																.module(module)
																.andCondition(CriteriaAPI.getIdCondition(id, module))
																;
		updateBuilder.update(asset);
	}

}
