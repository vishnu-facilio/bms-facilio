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

public class GasDataReadingModule extends BaseModuleConfig{
    public GasDataReadingModule(){
        setModuleName(FacilioConstants.Meter.GAS_DATA_READING);
    }


    @Override
    public void addData() throws Exception {

        ModuleBean modBean = Constants.getModBean();
        FacilioModule gasMeterModule = modBean.getModule(FacilioConstants.Meter.GAS_METER);

        List<FacilioModule> modules = new ArrayList<>();

        FacilioModule gasDataModule = addGasDataModule();
        modules.add(gasDataModule);

        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        addModuleChain.getContext().put(FacilioConstants.ContextNames.PARENT_MODULE, gasMeterModule.getName());
        addModuleChain.getContext().put(FacilioConstants.ContextNames.IS_SKIP_COUNTER_FIELD_ADD, true);
        addModuleChain.execute();


    }

    public FacilioModule addGasDataModule() throws Exception{

        FacilioModule module = new FacilioModule("gasdata", "Gas Data", "Gas_Data", FacilioModule.ModuleType.READING);

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

        NumberField gasVolumeAccumulator = new NumberField(module, "gasVolumeAccumulator", "Gas Volume Accumulator", FacilioField.FieldDisplayType.NUMBER, "GAS_VOLUME_ACCUMULATOR", FieldType.DECIMAL, true, false, true, null);
        gasVolumeAccumulator.setUnit(Unit.CUBICMETER.getSymbol());
        gasVolumeAccumulator.setUnitId(Unit.CUBICMETER.getUnitId());
        gasVolumeAccumulator.setMetric(Metric.VOLUME.getMetricId());
        gasVolumeAccumulator.setCounterField(true);
        fields.add(gasVolumeAccumulator);

        NumberField gasVolumeAccumulatorDelta = new NumberField(module, "gasVolumeAccumulatorDelta", "Gas Volume", FacilioField.FieldDisplayType.NUMBER, "GAS_VOLUME_ACCUMULATOR_DELTA", FieldType.DECIMAL, true, false, true, null);
        gasVolumeAccumulatorDelta.setUnit(Unit.CUBICMETER.getSymbol());
        gasVolumeAccumulatorDelta.setUnitId(Unit.CUBICMETER.getUnitId());
        gasVolumeAccumulatorDelta.setMetric(Metric.VOLUME.getMetricId());
        fields.add(gasVolumeAccumulatorDelta);

        NumberField flowRate = new NumberField(module, "flowRate", "Flow Rate", FacilioField.FieldDisplayType.NUMBER, "FLOW_RATE", FieldType.DECIMAL, true, false, true, null);
        flowRate.setUnit(Unit.CUBICMETERPERHOUR.getSymbol());
        flowRate.setUnitId(Unit.CUBICMETERPERHOUR.getUnitId());
        flowRate.setMetric(Metric.FLOWRATE.getMetricId());
        fields.add(flowRate);

        NumberField density = new NumberField(module, "density", "Density", FacilioField.FieldDisplayType.NUMBER, "DENSITY", FieldType.DECIMAL, true, false, true, null);
        fields.add(density);

        module.setFields(fields);
        return module;
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> gasDataReading = new ArrayList<FacilioView>();
        gasDataReading.add(getReportView().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.Meter.GAS_DATA_READING);
        groupDetails.put("views", gasDataReading);
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
