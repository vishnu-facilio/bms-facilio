package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateTransitionValidationCommand extends FacilioCommand {

    private static final double meterConstant = 1d/1084/100;

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String locationValue = (String) context.get(FacilioConstants.ContextNames.LOCATION_VALUE);
        Map<String, Double> currentLocation = (Map<String, Double>) context.get(FacilioConstants.ContextNames.CURRENT_LOCATION);
        String qrCode = (String) context.get(FacilioConstants.ContextNames.QR_VALUE);
        boolean isfromV2 = context.containsKey(FacilioConstants.ContextNames.IS_FROM_V2) ? (boolean) context.get(FacilioConstants.ContextNames.IS_FROM_V2) : false;
        ModuleBean moduleBean = Constants.getModBean();

        if (stateTransitionId == null || stateTransitionId <= 0) {
            return false;
        }

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);
        WorkflowRuleContext stateTransition = WorkflowRuleAPI.getWorkflowRule(stateTransitionId);

        if (CollectionUtils.isNotEmpty(records)) {

            for (ModuleBaseWithCustomFields record : records) {

                if (record != null) {

                    ModuleBaseWithCustomFields oldRecord = Constants.getOldRecord(context, moduleName, record.getId());

                    //temp fix
                    ModuleBaseWithCustomFields currentRecord = oldRecord != null ? oldRecord : record;

                    Map<String, Object> recordPlaceHolders = WorkflowRuleAPI.getRecordPlaceHolders(moduleName, currentRecord, WorkflowRuleAPI.getOrgPlaceHolders());
                    boolean shouldChangeState = WorkflowRuleAPI.evaluateWorkflowAndExecuteActions(stateTransition, moduleName, currentRecord, StateFlowRulesAPI.getDefaultFieldChangeSet(moduleName, record.getId()), recordPlaceHolders, (FacilioContext) context, false);
                    if (shouldChangeState) {
                        FacilioStatus newState = StateFlowRulesAPI.getStateContext(((StateflowTransitionContext) stateTransition).getToStateId());
                        if (newState == null) {
                            throw new Exception("Invalid state");
                        }
                        if (((StateflowTransitionContext) stateTransition).getLocationFieldId() > 0) {
                            validateGeoLocation((StateflowTransitionContext) stateTransition, currentRecord, moduleBean, locationValue, currentLocation);
                        }
                        if (((StateflowTransitionContext) stateTransition).getQrFieldId() > 0) {
                            validateQrValue(moduleBean, (StateflowTransitionContext) stateTransition, currentRecord, isfromV2, qrCode);
                        }
                    } else {
                        throw new IllegalArgumentException("State transition button is not valid for current record");
                    }
                }

            }
        }

        return false;
    }

    private void validateQrValue(ModuleBean moduleBean, StateflowTransitionContext stateflowTransition,ModuleBaseWithCustomFields oldRecord,boolean isfromV2,
                                 String qrCode) throws Exception {
        String qrValue = null;
        FacilioField qrField = moduleBean.getField(stateflowTransition.getQrFieldId());

        Object value = FieldUtil.getValue(oldRecord, qrField);
        if (value != null) {
            if (qrField instanceof LookupField) {
                FacilioField qrLookupField = moduleBean.getField(stateflowTransition.getQrLookupFieldId());
                if (value instanceof ModuleBaseWithCustomFields) {
                    if (qrLookupField != null) {
                        Map<String, Object> qrLookupFieldValue;
                        if (isfromV2 && ((ModuleBaseWithCustomFields) value).getId() > 0) {
                            FacilioModule qrModule = moduleBean.getModule(qrLookupField.getModuleId());
                            qrLookupFieldValue = FieldUtil.getAsProperties(RecordAPI.getRecord(qrModule.getName(), ((ModuleBaseWithCustomFields) value).getId()));
                            if (qrLookupFieldValue != null) {
                                qrValue = (String) qrLookupFieldValue.get(qrLookupField.getName());
                            }
                        } else {
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

        if ((qrValue != null) && (qrCode == null)) {
            throw new IllegalArgumentException("Qr is mandatory");
        }
        if ((qrValue != null) && (!qrValue.equals(qrCode))) {
            throw new IllegalArgumentException("QR code doesn't match to this record");
        }
    }

    private void validateGeoLocation(StateflowTransitionContext stateTransition, ModuleBaseWithCustomFields oldRecord, ModuleBean moduleBean,String locationValue, Map<String,Double> currentLocation) throws Exception {
        if (stateTransition.getLocationFieldId() > 0){
            Double[] coordinates = null;
            FacilioField locationField = moduleBean.getField(((StateflowTransitionContext) stateTransition).getLocationFieldId());
            Object value = FieldUtil.getValue(oldRecord, locationField);
            if (locationField instanceof LookupField && value != null) {
                if (stateTransition.getLocationLookupFieldId() > 0) {
                    Object recordLocationValue = null;
                    FacilioField locationLookUpField = moduleBean.getField(stateTransition.getLocationLookupFieldId());
                    if (locationLookUpField instanceof LookupField) {
                        Map<String, List<ModuleBaseWithCustomFields>> recordObject;
                        if (value instanceof ModuleBaseWithCustomFields) {
                            recordObject = Constants.getRecordMap(V3Util.getSummary(locationLookUpField.getModule().getName(), Collections.singletonList(((ModuleBaseWithCustomFields) value).getId())));
                            recordLocationValue = recordObject.get(locationLookUpField.getModule().getName()) != null ? recordObject.get(locationLookUpField.getModule().getName()).get(0) : null;
                        } else if (value instanceof Map) {
                            recordObject = Constants.getRecordMap(V3Util.getSummary(locationLookUpField.getModule().getName(), Collections.singletonList((Long) ((Map) value).get("id"))));
                            recordLocationValue = recordObject.get(locationLookUpField.getModule().getName()) != null ? recordObject.get(locationLookUpField.getModule().getName()).get(0) : null;
                        }
                        coordinates = getCoordinates(getValueFromRecord(recordLocationValue, locationLookUpField));
                    } else {
                        recordLocationValue = getValueFromRecord(value, locationLookUpField);
                        coordinates = getCoordinateFromString((String) recordLocationValue);
                    }
                } else {
                    coordinates = getCoordinates(value);
                }
            } else {
                coordinates = getCoordinateFromString((String) value);
            }
            Long radius = stateTransition.getRadius();
            validateLocation(locationValue, currentLocation, coordinates, radius);
        }
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
