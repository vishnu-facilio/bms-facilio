package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.vividsolutions.jts.geom.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class UpdateStateForModuleDataCommand extends FacilioCommand {
	private static final double meterConstant = 1d/1084/100;

	private static final Logger LOGGER = LogManager.getLogger(UpdateStateForModuleDataCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		Map<String, List> recordMap = CommonCommandUtil.getRecordMap((FacilioContext) context);
		Long currentTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		String qrCode = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
		String locationValue = (String) context.get(FacilioConstants.ContextNames.LOCATION_VALUE);
		Map<String, Double> currentLocation = (Map<String, Double>) context.get(FacilioConstants.ContextNames.CURRENT_LOCATION);
		boolean isfromV2 = context.containsKey(FacilioConstants.ContextNames.IS_FROM_V2) ? (boolean) context.get(FacilioConstants.ContextNames.IS_FROM_V2) : false;
		ModuleBean moduleBean = Constants.getModBean();
		List<? extends ModuleBaseWithCustomFields> records = null;
		if (MapUtils.isNotEmpty(recordMap)) {
			records = recordMap.get(moduleName);
		}
		// there is no transition info
		if (currentTransitionId == null || currentTransitionId == -1) {
			return false;
		}

		if (CollectionUtils.isNotEmpty(records)) {
			StateflowTransitionContext stateflowTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(currentTransitionId);
			if (stateflowTransition == null) {
				return false;
			}
			for (ModuleBaseWithCustomFields record : records) {
				if (record.getApprovalFlowId() > -1 && record.getApprovalFlowId() > 0) {
					throw new IllegalArgumentException("Cannot change state as it is in approval");
				}

				record.setSubForm(null); // temp fix
				Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, record, WorkflowRuleAPI.getOrgPlaceHolders());
				/*if (wo.getModuleState().getId() != stateflowTransition.getFromStateId()) {
					throw new IllegalArgumentException("Invalid transition");
				}*/
				if (stateflowTransition.getLocationFieldId() > 0) {

					FacilioField locationField = moduleBean.getField(stateflowTransition.getLocationFieldId());
					Double[] coordinates = null;
					Object value = FieldUtil.getValue(record, locationField);

					if(locationField instanceof LookupField && value!=null){
						if(stateflowTransition.getLocationLookupFieldId()>0) {
							Object recordLocationValue = null;
							FacilioField locationLookUpField = moduleBean.getField(stateflowTransition.getLocationLookupFieldId());
							if(locationLookUpField instanceof LookupField){
								Map<String, List<ModuleBaseWithCustomFields>> recordObject;
								if(value instanceof ModuleBaseWithCustomFields){
									recordObject = Constants.getRecordMap(V3Util.getSummary(locationLookUpField.getModule().getName(), Collections.singletonList(((ModuleBaseWithCustomFields) value).getId())));
									recordLocationValue = recordObject.get(locationLookUpField.getModule().getName()) != null ? recordObject.get(locationLookUpField.getModule().getName()).get(0) : null;
								} else if (value instanceof Map) {
									recordObject = Constants.getRecordMap(V3Util.getSummary(locationLookUpField.getModule().getName(), Collections.singletonList((Long) ((Map) value).get("id"))));
									recordLocationValue = recordObject.get(locationLookUpField.getModule().getName()) != null ? recordObject.get(locationLookUpField.getModule().getName()).get(0) : null;
								}
								coordinates = getCoordinates(getValueFromRecord(recordLocationValue,locationLookUpField));
							}
							else {
								recordLocationValue = getValueFromRecord(value,locationLookUpField);
								coordinates = getCoordinateFromString((String) recordLocationValue);
							}
						}
						else{
						       coordinates = getCoordinates(value);
						}
					}
					else{
						coordinates = getCoordinateFromString((String) value);
					}
					Long radius = stateflowTransition.getRadius();
					validateLocation(locationValue,currentLocation,coordinates,radius);
				}

				if(stateflowTransition.getQrFieldId() > 0){
					String qrValue = null;
					FacilioField qrField = moduleBean.getField(stateflowTransition.getQrFieldId());

					Object value = FieldUtil.getValue(record, qrField);
					if (value != null) {
						if (qrField instanceof LookupField) {
							FacilioField qrLookupField = moduleBean.getField(stateflowTransition.getQrLookupFieldId());
							if (value instanceof ModuleBaseWithCustomFields) {
								if (qrLookupField != null) {
									Map<String,Object> qrLookupFieldValue;
									if ( isfromV2 && ((ModuleBaseWithCustomFields) value).getId() > 0){
										FacilioModule qrModule = moduleBean.getModule(qrLookupField.getModuleId());
										qrLookupFieldValue =  FieldUtil.getAsProperties(RecordAPI.getRecord(qrModule.getName(),((ModuleBaseWithCustomFields) value).getId()));
										if (qrLookupFieldValue != null){
											qrValue = (String) qrLookupFieldValue.get(qrLookupField.getName());
										}
									}else {
										qrValue = (String) FieldUtil.getValue((ModuleBaseWithCustomFields) value, qrLookupField);
									}
								}
							} else if (value instanceof Map) {
								qrValue = (String) ((Map) value).get(qrLookupField.getName());
							}
						} else {
							qrValue = (String) value;
						}
					}

					if((qrValue!=null) && (qrCode==null)){
						throw new IllegalArgumentException("Qr is mandatory");
					}
					if ((qrValue!=null) && (!qrValue.equals(qrCode))) {
						throw new IllegalArgumentException("QR code doesn't match to this record");
					}
				}

				boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateflowTransition, moduleName, record, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, record.getId()), recordPlaceHolders, (FacilioContext) context, false);
				if (shouldChangeState) {
					FacilioStatus newState = StateFlowRulesAPI.getStateContext(stateflowTransition.getToStateId());
					if (newState == null) {
						throw new Exception("Invalid state");
					}
					stateflowTransition.executeTrueActions(record, context, recordPlaceHolders);
				}
			}
		}
		return false;
	}

	private Double[] getCoordinateFromString(String recordLocationValue) {
		String[] strings = FacilioUtil.splitByComma(recordLocationValue);
		Double[] coordinate = new Double[2];
		coordinate[0] = Double.parseDouble(strings[0]);
		coordinate[1] = Double.parseDouble(strings[1]);
		return coordinate;
	}

	public void validateLocation(String locationValue,Map<String, Double> currentLocationValue, Double[] coordinates, Long radius) {

		if(coordinates == null) {
			return;
		}

		FacilioUtil.throwIllegalArgumentException(locationValue == null && currentLocationValue == null, "Location value is mandatory");

		Point area = getPointFromCoordinates(coordinates[0],coordinates[1]);

		Geometry geometry = (area != null) ? area.buffer(radius*meterConstant) : null;

		Point currentLocation = locationValue != null ? getPointFromCoordinates(locationValue) : getPointFromCoordinates(currentLocationValue.get("latitude"),currentLocationValue.get("longitude"));

		FacilioUtil.throwIllegalArgumentException((geometry != null && currentLocation != null) && !geometry.contains(currentLocation), "This transition can only be performed if it is within the range of the chosen location");

	}
	public Point getPointFromCoordinates(String coordinatesValue) {
		if (coordinatesValue != null) {

			String regex = "^[-+]?[0-9]*\\.?[0-9]+,[-+]?[0-9]*\\.?[0-9]+$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(coordinatesValue);

			FacilioUtil.throwIllegalArgumentException(!matcher.matches(), "Invalid GeoLocation Pattern");

			String[] coordinates = FacilioUtil.splitByComma(coordinatesValue);
			Coordinate coordinate = new Coordinate(Double.parseDouble(coordinates[1].toString()), Double.parseDouble(coordinates[0].toString()));
			GeometryFactory geometryFactory = new GeometryFactory();
			Point point = geometryFactory.createPoint(coordinate);
			return point;
		}
		return null;
	}

	public Point getPointFromCoordinates(Double latitude,Double longitude){
		Coordinate coordinate = new Coordinate(longitude, latitude);
		GeometryFactory geometryFactory = new GeometryFactory();
		Point point = geometryFactory.createPoint(coordinate);
		return point;
	}

	public Object getValueFromRecord(Object record,FacilioField field) throws Exception {
		Object value = null;
		if (record == null){
			return null;
		}
		if (record instanceof ModuleBaseWithCustomFields) {
			value = FieldUtil.getValue((ModuleBaseWithCustomFields) record, field);
		} else if (record instanceof Map) {
			value = ((Map) record).get(field.getName());
		}
		return value;
	}
	public Double[] getCoordinates(Object record){
		Double[] location = new Double[2];

		if (record instanceof LocationContext){
			location[0] = ((LocationContext) record).getLat();
			location[1] = ((LocationContext) record).getLng();
		}
		else if (record instanceof Map) {
			location[0] = (Double) ((Map) record).get("lat");
			location[1] = (Double) ((Map) record).get("lng");
		}
		else if(record instanceof ModuleBaseWithCustomFields){
			Map<String,Object> data =((ModuleBaseWithCustomFields) record).getData();
			location[0] = (Double) data.get("lat");
			location[1] = (Double) data.get("lng");
		}
		return location;
	}
}
