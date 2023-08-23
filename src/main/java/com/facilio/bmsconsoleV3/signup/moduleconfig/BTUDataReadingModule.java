package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;

import java.util.*;

public class BTUDataReadingModule extends BaseModuleConfig{
    public BTUDataReadingModule(){
        setModuleName(FacilioConstants.Meter.BTU_DATA_READING);
    }


    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule btuMeterModule = modBean.getModule(FacilioConstants.Meter.BTU_METER);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule btuDataModule = addBTUDataModule();
        modules.add(btuDataModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, btuMeterModule.getName());
        addModuleChain.getContext().put(FacilioConstants.ContextNames.IS_SKIP_COUNTER_FIELD_ADD, true);
        addModuleChain.execute();


    }

    public FacilioModule addBTUDataModule() throws Exception{

        FacilioModule module = new FacilioModule("btudata", "BTU Data", "BTU_Data", FacilioModule.ModuleType.READING);

        List<FacilioField> fields = new ArrayList<>();

        NumberField parentId = new NumberField(module, "parentId", "Parent", FacilioField.FieldDisplayType.NUMBER, "PARENT_METER_ID", FieldType.NUMBER, true, false, true, null);
        fields.add(parentId);

        SystemEnumField sourceType = new SystemEnumField();
        sourceType.setModule(module);
        sourceType.setName("sourceType");
        sourceType.setDisplayName("Source Type");
        sourceType.setDisplayType(FacilioField.FieldDisplayType.SELECTBOX);
        sourceType.setColumnName("SOURCE_TYPE");
        sourceType.setDataType(FieldType.SYSTEM_ENUM);
        sourceType.setRequired(false);
        sourceType.setDisabled(false);
        sourceType.setDefault(true);
        sourceType.setEnumName("SourceType");

        fields.add(sourceType);

        NumberField sourceId = new NumberField(module, "sourceId", "Source Id", FacilioField.FieldDisplayType.NUMBER, "SOURCE_ID", FieldType.NUMBER, false, false, true, null);
        fields.add(sourceId);

        DateField actualTtime = new DateField(module, "actualTtime", "Actual Timestamp", FacilioField.FieldDisplayType.NUMBER, "ACTUAL_TTIME", FieldType.DATE_TIME, true, false, true, null);
        fields.add(actualTtime);

        DateField sysCreatedTime = new DateField(module, "sysCreatedTime", "System Created Time", FacilioField.FieldDisplayType.NUMBER, "CREATED_TIME", FieldType.DATE_TIME, true, false, true, null);
        fields.add(sysCreatedTime);

        DateField ttime = new DateField(module, "ttime", "Timestamp", FacilioField.FieldDisplayType.NUMBER, "TTIME", FieldType.DATE_TIME, true, false, true, null);
        fields.add(ttime);

        StringField date = new StringField(module, "date", "Date", FacilioField.FieldDisplayType.TEXTBOX, "TTIME_DATE", FieldType.STRING, true, false, true, null);
        fields.add(date);

        NumberField month = new NumberField(module, "month", "Month", FacilioField.FieldDisplayType.NUMBER, "TTIME_MONTH", FieldType.NUMBER, true, false, true, null);
        fields.add(month);

        NumberField week = new NumberField(module, "week", "Week", FacilioField.FieldDisplayType.NUMBER, "TTIME_WEEK", FieldType.NUMBER, true, false, true, null);
        fields.add(week);

        NumberField day = new NumberField(module, "day", "Day", FacilioField.FieldDisplayType.NUMBER, "TTIME_DAY", FieldType.NUMBER, true, false, true, null);
        fields.add(day);

        NumberField hour = new NumberField(module, "hour", "Hour", FacilioField.FieldDisplayType.NUMBER, "TTIME_HOUR", FieldType.NUMBER, true, false, true, null);
        fields.add(hour);

        NumberField flowRate = new NumberField(module, "flowRate", "Flow Rate", FacilioField.FieldDisplayType.NUMBER, "FLOW_RATE", FieldType.DECIMAL, true, false, true, null);
        flowRate.setUnit(Unit.CUBICMETERPERHOUR.getSymbol());
        flowRate.setUnitId(Unit.CUBICMETERPERHOUR.getUnitId());
        flowRate.setMetric(Metric.FLOWRATE.getMetricId());
        fields.add(flowRate);

        NumberField inletCHWTemperature = new NumberField(module, "inletCHWTemperature", "Inlet CHW Temperature", FacilioField.FieldDisplayType.NUMBER, "INLET_CHW_TEMPERATURE", FieldType.DECIMAL, true, false, true, null);
        inletCHWTemperature.setUnit(Unit.CELSIUS.getSymbol());
        inletCHWTemperature.setUnitId(Unit.CELSIUS.getUnitId());
        inletCHWTemperature.setMetric(Metric.TEMPERATURE.getMetricId());
        fields.add(inletCHWTemperature);

        NumberField outletCHWTemperature = new NumberField(module, "outletCHWTemperature", "Outlet CHW Temperature", FacilioField.FieldDisplayType.NUMBER, "OUTLET_CHW_TEMPERATURE", FieldType.DECIMAL, true, false, true, null);
        outletCHWTemperature.setUnit(Unit.CELSIUS.getSymbol());
        outletCHWTemperature.setUnitId(Unit.CELSIUS.getUnitId());
        outletCHWTemperature.setMetric(Metric.TEMPERATURE.getMetricId());
        fields.add(outletCHWTemperature);

        NumberField CHWConsumptionAccumulator = new NumberField(module, "CHWConsumptionAccumulator", "CHW Consumption Accumulator", FacilioField.FieldDisplayType.NUMBER, "CHW_CONSUMPTION_ACCUMULATOR", FieldType.DECIMAL, true, false, true, null);
        CHWConsumptionAccumulator.setUnit(Unit.KWH.getSymbol());
        CHWConsumptionAccumulator.setUnitId(Unit.KWH.getUnitId());
        CHWConsumptionAccumulator.setMetric(Metric.ENERGY.getMetricId());
        CHWConsumptionAccumulator.setCounterField(true);
        fields.add(CHWConsumptionAccumulator);

        NumberField CHWConsumptionAccumulatorDelta = new NumberField(module, "CHWConsumptionAccumulatorDelta", "CHW Consumption", FacilioField.FieldDisplayType.NUMBER, "CHW_CONSUMPTION_ACCUMULATOR_DELTA", FieldType.DECIMAL, true, false, true, null);
        CHWConsumptionAccumulatorDelta.setUnit(Unit.KWH.getSymbol());
        CHWConsumptionAccumulatorDelta.setUnitId(Unit.KWH.getUnitId());
        CHWConsumptionAccumulatorDelta.setMetric(Metric.ENERGY.getMetricId());
        fields.add(CHWConsumptionAccumulatorDelta);

        module.setFields(fields);
        return module;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> btuDataReading = new ArrayList<FacilioView>();
        btuDataReading.add(getReportView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Meter.BTU_DATA_READING);
        groupDetails.put("views", btuDataReading);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getReportView() {

        FacilioView reportView = new FacilioView();
        reportView.setName("report");
        reportView.setHidden(true);

        return reportView;
    }
}
