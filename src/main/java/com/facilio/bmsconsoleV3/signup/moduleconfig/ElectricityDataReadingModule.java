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

public class ElectricityDataReadingModule extends BaseModuleConfig{
    public ElectricityDataReadingModule(){
        setModuleName(FacilioConstants.Meter.ELECTRICITY_DATA_READING);
    }


    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule electricityMeterModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_METER);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule electricityDataModule = addElectricityDataModule();
        modules.add(electricityDataModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, electricityMeterModule.getName());
        addModuleChain.execute();


    }

    public FacilioModule addElectricityDataModule() throws Exception{

        FacilioModule module = new FacilioModule("electricitydata", "Electricity Data", "Electricity_Data", FacilioModule.ModuleType.READING);

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

        BooleanField marked = new BooleanField(module, "marked", "Marked", FacilioField.FieldDisplayType.SELECTBOX, "Marked", FieldType.BOOLEAN, false, false, true, null);
        fields.add(marked);

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

        NumberField totalEnergyConsumption = new NumberField(module, "totalEnergyConsumption", "Energy Reading", FacilioField.FieldDisplayType.NUMBER, "TOTAL_ENERGY_CONSUMPTION", FieldType.DECIMAL, true, false, true, null);
        totalEnergyConsumption.setUnit(Unit.KWH.getSymbol());
        totalEnergyConsumption.setUnitId(Unit.KWH.getUnitId());
        totalEnergyConsumption.setMetric(Metric.ENERGY.getMetricId());
        fields.add(totalEnergyConsumption);

        NumberField totalEnergyConsumptionDelta = new NumberField(module, "totalEnergyConsumptionDelta", "Energy", FacilioField.FieldDisplayType.NUMBER, "TOTAL_ENERGY_CONSUMPTION_DELTA", FieldType.DECIMAL, true, false, true, null);
        totalEnergyConsumptionDelta.setUnit(Unit.KWH.getSymbol());
        totalEnergyConsumptionDelta.setUnitId(Unit.KWH.getUnitId());
        totalEnergyConsumptionDelta.setMetric(Metric.ENERGY.getMetricId());
        fields.add(totalEnergyConsumptionDelta);

        NumberField totalDemand = new NumberField(module, "totalDemand", "Power", FacilioField.FieldDisplayType.NUMBER, "TOTAL_DEMAND", FieldType.DECIMAL, true, false, true, null);
        totalDemand.setUnit(Unit.KILOWATT.getSymbol());
        totalDemand.setUnitId(Unit.KILOWATT.getUnitId());
        totalDemand.setMetric(Metric.POWER.getMetricId());
        fields.add(totalDemand);

        NumberField activePowerB = new NumberField(module, "activePowerB", "Active Power B", FacilioField.FieldDisplayType.NUMBER, "ACTIVE_POWER_B", FieldType.DECIMAL, true, false, true, null);
        activePowerB.setUnit(Unit.KILOWATT.getSymbol());
        activePowerB.setUnitId(Unit.KILOWATT.getUnitId());
        activePowerB.setMetric(Metric.POWER.getMetricId());
        fields.add(activePowerB);

        NumberField activePowerR = new NumberField(module, "activePowerR", "Active Power R", FacilioField.FieldDisplayType.NUMBER, "ACTIVE_POWER_R", FieldType.DECIMAL, true, false, true, null);
        activePowerR.setUnit(Unit.KILOWATT.getSymbol());
        activePowerR.setUnitId(Unit.KILOWATT.getUnitId());
        activePowerR.setMetric(Metric.POWER.getMetricId());
        fields.add(activePowerR);

        NumberField activePowerY = new NumberField(module, "activePowerY", "Active Power Y", FacilioField.FieldDisplayType.NUMBER, "ACTIVE_POWER_Y", FieldType.DECIMAL, true, false, true, null);
        activePowerY.setUnit(Unit.KILOWATT.getSymbol());
        activePowerY.setUnitId(Unit.KILOWATT.getUnitId());
        activePowerY.setMetric(Metric.POWER.getMetricId());
        fields.add(activePowerY);

        NumberField apparentPowerB = new NumberField(module, "apparentPowerB", "Apparent Power B", FacilioField.FieldDisplayType.NUMBER, "APPARENT_POWER_B", FieldType.DECIMAL, true, false, true, null);
        apparentPowerB.setUnit(Unit.KILOVOLTAMP.getSymbol());
        apparentPowerB.setUnitId(Unit.KILOVOLTAMP.getUnitId());
        apparentPowerB.setMetric(Metric.APPARENTPOWER.getMetricId());
        fields.add(apparentPowerB);

        NumberField apparentPowerR = new NumberField(module, "apparentPowerR", "Apparent Power R", FacilioField.FieldDisplayType.NUMBER, "APPARENT_POWER_R", FieldType.DECIMAL, true, false, true, null);
        apparentPowerR.setUnit(Unit.KILOVOLTAMP.getSymbol());
        apparentPowerR.setUnitId(Unit.KILOVOLTAMP.getUnitId());
        apparentPowerR.setMetric(Metric.APPARENTPOWER.getMetricId());
        fields.add(apparentPowerR);

        NumberField apparentPowerY = new NumberField(module, "apparentPowerY", "Apparent Power Y", FacilioField.FieldDisplayType.NUMBER, "APPARENT_POWER_Y", FieldType.DECIMAL, true, false, true, null);
        apparentPowerY.setUnit(Unit.KILOVOLTAMP.getSymbol());
        apparentPowerY.setUnitId(Unit.KILOVOLTAMP.getUnitId());
        apparentPowerY.setMetric(Metric.APPARENTPOWER.getMetricId());
        fields.add(apparentPowerY);

        NumberField frequencyB = new NumberField(module, "frequencyB", "Frequency B", FacilioField.FieldDisplayType.NUMBER, "FREQUENCY_B", FieldType.DECIMAL, true, false, true, null);
        frequencyB.setUnit(Unit.HERTZ.getSymbol());
        frequencyB.setUnitId(Unit.HERTZ.getUnitId());
        frequencyB.setMetric(Metric.FREQUENCY.getMetricId());
        fields.add(frequencyB);

        NumberField frequencyR = new NumberField(module, "frequencyR", "Frequency R", FacilioField.FieldDisplayType.NUMBER, "FREQUENCY_R", FieldType.DECIMAL, true, false, true, null);
        frequencyR.setUnit(Unit.HERTZ.getSymbol());
        frequencyR.setUnitId(Unit.HERTZ.getUnitId());
        frequencyR.setMetric(Metric.FREQUENCY.getMetricId());
        fields.add(frequencyR);

        NumberField frequencyY = new NumberField(module, "frequencyY", "Frequency Y", FacilioField.FieldDisplayType.NUMBER, "FREQUENCY_Y", FieldType.DECIMAL, true, false, true, null);
        frequencyY.setUnit(Unit.HERTZ.getSymbol());
        frequencyY.setUnitId(Unit.HERTZ.getUnitId());
        frequencyY.setMetric(Metric.FREQUENCY.getMetricId());
        fields.add(frequencyY);

        NumberField lineCurrentB = new NumberField(module, "lineCurrentB", "Line Current B", FacilioField.FieldDisplayType.NUMBER, "LINE_CURRENT_B", FieldType.DECIMAL, true, false, true, null);
        lineCurrentB.setUnit(Unit.AMPERE.getSymbol());
        lineCurrentB.setUnitId(Unit.AMPERE.getUnitId());
        lineCurrentB.setMetric(Metric.CURRENT.getMetricId());
        fields.add(lineCurrentB);

        NumberField lineCurrentR = new NumberField(module, "lineCurrentR", "Line Current R", FacilioField.FieldDisplayType.NUMBER, "LINE_CURRENT_R", FieldType.DECIMAL, true, false, true, null);
        lineCurrentR.setUnit(Unit.AMPERE.getSymbol());
        lineCurrentR.setUnitId(Unit.AMPERE.getUnitId());
        lineCurrentR.setMetric(Metric.CURRENT.getMetricId());
        fields.add(lineCurrentR);

        NumberField lineCurrentY = new NumberField(module, "lineCurrentY", "Line Current Y", FacilioField.FieldDisplayType.NUMBER, "LINE_CURRENT_Y", FieldType.DECIMAL, true, false, true, null);
        lineCurrentY.setUnit(Unit.AMPERE.getSymbol());
        lineCurrentY.setUnitId(Unit.AMPERE.getUnitId());
        lineCurrentY.setMetric(Metric.CURRENT.getMetricId());
        fields.add(lineCurrentY);

        NumberField lineVoltageB = new NumberField(module, "lineVoltageB", "Line Voltage B", FacilioField.FieldDisplayType.NUMBER, "LINE_VOLTAGE_B", FieldType.DECIMAL, true, false, true, null);
        lineVoltageB.setUnit(Unit.VOLT.getSymbol());
        lineVoltageB.setUnitId(Unit.VOLT.getUnitId());
        lineVoltageB.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(lineVoltageB);

        NumberField lineVoltageR = new NumberField(module, "lineVoltageR", "Line Voltage R", FacilioField.FieldDisplayType.NUMBER, "LINE_VOLTAGE_R", FieldType.DECIMAL, true, false, true, null);
        lineVoltageR.setUnit(Unit.VOLT.getSymbol());
        lineVoltageR.setUnitId(Unit.VOLT.getUnitId());
        lineVoltageR.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(lineVoltageR);

        NumberField lineVoltageY = new NumberField(module, "lineVoltageY", "Line Voltage Y", FacilioField.FieldDisplayType.NUMBER, "LINE_VOLTAGE_Y", FieldType.DECIMAL, true, false, true, null);
        lineVoltageY.setUnit(Unit.VOLT.getSymbol());
        lineVoltageY.setUnitId(Unit.VOLT.getUnitId());
        lineVoltageY.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(lineVoltageY);

        NumberField phaseEnergyB = new NumberField(module, "phaseEnergyB", "Phase Energy B", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_B", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyB.setUnit(Unit.KWH.getSymbol());
        phaseEnergyB.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyB.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyB);

        NumberField phaseEnergyBDelta = new NumberField(module, "phaseEnergyBDelta", "Phase Energy B Delta", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_B_DELTA", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyBDelta.setUnit(Unit.KWH.getSymbol());
        phaseEnergyBDelta.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyBDelta.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyBDelta);

        NumberField phaseEnergyR = new NumberField(module, "phaseEnergyR", "Phase Energy R", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_R", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyR.setUnit(Unit.KWH.getSymbol());
        phaseEnergyR.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyR.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyR);

        NumberField phaseEnergyRDelta = new NumberField(module, "phaseEnergyRDelta", "Phase Energy R Delta", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_R_DELTA", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyRDelta.setUnit(Unit.KWH.getSymbol());
        phaseEnergyRDelta.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyRDelta.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyRDelta);

        NumberField phaseEnergyY = new NumberField(module, "phaseEnergyY", "Phase Energy Y", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_Y", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyY.setUnit(Unit.KWH.getSymbol());
        phaseEnergyY.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyY.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyY);

        NumberField phaseEnergyYDelta = new NumberField(module, "phaseEnergyYDelta", "Phase Energy Y Delta", FacilioField.FieldDisplayType.NUMBER, "PHASE_ENERGY_Y_DELTA", FieldType.DECIMAL, true, false, true, null);
        phaseEnergyYDelta.setUnit(Unit.KWH.getSymbol());
        phaseEnergyYDelta.setUnitId(Unit.KWH.getUnitId());
        phaseEnergyYDelta.setMetric(Metric.ENERGY.getMetricId());
        fields.add(phaseEnergyYDelta);

        NumberField phaseVoltageB = new NumberField(module, "phaseVoltageB", "Phase Voltage B", FacilioField.FieldDisplayType.NUMBER, "PHASE_VOLTAGE_B", FieldType.DECIMAL, true, false, true, null);
        phaseVoltageB.setUnit(Unit.VOLT.getSymbol());
        phaseVoltageB.setUnitId(Unit.VOLT.getUnitId());
        phaseVoltageB.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(phaseVoltageB);

        NumberField phaseVoltageR = new NumberField(module, "phaseVoltageR", "Phase Voltage R", FacilioField.FieldDisplayType.NUMBER, "PHASE_VOLTAGE_R", FieldType.DECIMAL, true, false, true, null);
        phaseVoltageR.setUnit(Unit.VOLT.getSymbol());
        phaseVoltageR.setUnitId(Unit.VOLT.getUnitId());
        phaseVoltageR.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(phaseVoltageR);

        NumberField phaseVoltageY = new NumberField(module, "phaseVoltageY", "Phase Voltage Y", FacilioField.FieldDisplayType.NUMBER, "PHASE_VOLTAGE_Y", FieldType.DECIMAL, true, false, true, null);
        phaseVoltageY.setUnit(Unit.VOLT.getSymbol());
        phaseVoltageY.setUnitId(Unit.VOLT.getUnitId());
        phaseVoltageY.setMetric(Metric.VOLTAGE.getMetricId());
        fields.add(phaseVoltageY);

        NumberField powerFactorB = new NumberField(module, "powerFactorB", "Power Factor B", FacilioField.FieldDisplayType.NUMBER, "POWER_FACTOR_B", FieldType.DECIMAL, true, false, true, null);
        fields.add(powerFactorB);

        NumberField powerFactorR = new NumberField(module, "powerFactorR", "Power Factor R", FacilioField.FieldDisplayType.NUMBER, "POWER_FACTOR_R", FieldType.DECIMAL, true, false, true, null);
        fields.add(powerFactorR);

        NumberField powerFactorY = new NumberField(module, "powerFactorY", "Power Factor Y", FacilioField.FieldDisplayType.NUMBER, "POWER_FACTOR_Y", FieldType.DECIMAL, true, false, true, null);
        fields.add(powerFactorY);

        NumberField reactivePowerB = new NumberField(module, "reactivePowerB", "Reactive Power B", FacilioField.FieldDisplayType.NUMBER, "REACTIVE_POWER_B", FieldType.DECIMAL, true, false, true, null);
        reactivePowerB.setUnit(Unit.KILOVAR.getSymbol());
        reactivePowerB.setUnitId(Unit.KILOVAR.getUnitId());
        reactivePowerB.setMetric(Metric.REACTIVEPOWER.getMetricId());
        fields.add(reactivePowerB);

        NumberField reactivePowerR = new NumberField(module, "reactivePowerR", "Reactive Power R", FacilioField.FieldDisplayType.NUMBER, "REACTIVE_POWER_R", FieldType.DECIMAL, true, false, true, null);
        reactivePowerR.setUnit(Unit.KILOVAR.getSymbol());
        reactivePowerR.setUnitId(Unit.KILOVAR.getUnitId());
        reactivePowerR.setMetric(Metric.REACTIVEPOWER.getMetricId());
        fields.add(reactivePowerR);

        NumberField reactivePowerY = new NumberField(module, "reactivePowerY", "Reactive Power Y", FacilioField.FieldDisplayType.NUMBER, "REACTIVE_POWER_Y", FieldType.DECIMAL, true, false, true, null);
        reactivePowerY.setUnit(Unit.KILOVAR.getSymbol());
        reactivePowerY.setUnitId(Unit.KILOVAR.getUnitId());
        reactivePowerY.setMetric(Metric.REACTIVEPOWER.getMetricId());
        fields.add(reactivePowerY);


        module.setFields(fields);
        return module;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> electricityDataReading = new ArrayList<FacilioView>();
        electricityDataReading.add(getReportView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Meter.ELECTRICITY_DATA_READING);
        groupDetails.put("views", electricityDataReading);
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
