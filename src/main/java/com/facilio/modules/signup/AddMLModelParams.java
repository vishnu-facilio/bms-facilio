package com.facilio.modules.signup;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.MLModelParamsContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AddMLModelParams extends SignUpData {

    private long orgId;
    @Override
    public void addData() throws Exception {
        orgId = AccountUtil.getCurrentOrg().getOrgId();
        if(orgId < 1){
            throw new IllegalArgumentException("Invalid orgId while adding ml model params entry in Org signup.");
        }
        List<MLModelParamsContext> modelDetails = new ArrayList<>();
        modelDetails.addAll(getEnergyPredictionModelDetails());
        modelDetails.addAll(getLoadPredictionModelDetails());

        addMLModelParams(modelDetails);

    }

    private List<MLModelParamsContext> getLoadPredictionModelDetails() {
        List<MLModelParamsContext> loadModelDetails = new ArrayList<>();
        String modelName = "loadprediction";

        MLModelParamsContext algoParams = new MLModelParamsContext();
        algoParams.setOrgId(orgId);
        algoParams.setModelName(modelName);
        algoParams.setDataType(FieldType.ENUM.getTypeAsInt());
        algoParams.setIsMandatory(false);
        algoParams.setKeyName("algoname");
        String[] algoValues = {"xgboost", "h2o"};
        algoParams.setKeyValue(new JSONArray(Arrays.asList(algoValues)).toString());
        loadModelDetails.add(algoParams);

        loadModelDetails.add(getWeekendParams(modelName));

        return loadModelDetails;
    }

    private List<MLModelParamsContext> getEnergyPredictionModelDetails() {
        List<MLModelParamsContext> energyModelDetails = new ArrayList<>();
        String modelName = "energyprediction";
        MLModelParamsContext dataIntervalParams = new MLModelParamsContext();
        dataIntervalParams.setOrgId(orgId);
        dataIntervalParams.setModelName(modelName);
        dataIntervalParams.setDataType(FieldType.ENUM.getTypeAsInt());
        dataIntervalParams.setIsMandatory(false);
        dataIntervalParams.setKeyName("dataInterval");
        String[] dataIntervalValues = {"hourly", "daily", "monthly"};
        dataIntervalParams.setKeyValue(new JSONArray(Arrays.asList(dataIntervalValues)).toString());
        energyModelDetails.add(dataIntervalParams);

        MLModelParamsContext algoParams = new MLModelParamsContext();
        algoParams.setOrgId(orgId);
        algoParams.setModelName(modelName);
        algoParams.setDataType(FieldType.ENUM.getTypeAsInt());
        algoParams.setIsMandatory(false);
        algoParams.setKeyName("algoname");
        String[] algoValues = {"xgboost", "h2o"};
        algoParams.setKeyValue(new JSONArray(Arrays.asList(algoValues)).toString());
        energyModelDetails.add(algoParams);


        MLModelParamsContext predictionPeriodParams = new MLModelParamsContext();
        predictionPeriodParams.setOrgId(orgId);
        predictionPeriodParams.setModelName(modelName);
        predictionPeriodParams.setDataType(FieldType.STRING.getTypeAsInt());
        predictionPeriodParams.setIsMandatory(false);
        predictionPeriodParams.setKeyName("predictionPeriod");
        energyModelDetails.add(predictionPeriodParams);

        energyModelDetails.add(getWeekendParams(modelName));
        return energyModelDetails;
    }

    private MLModelParamsContext getWeekendParams(String modelName) {
        MLModelParamsContext weekendParams = new MLModelParamsContext();
        weekendParams.setOrgId(orgId);
        weekendParams.setModelName(modelName);
        weekendParams.setDataType(FieldType.ENUM.getTypeAsInt());
        weekendParams.setIsMandatory(true);
        weekendParams.setKeyName("weekend");
        String[] weekendValues = {"mon", "tue", "wed", "thu", "fri", "sat", "sun"};
        weekendParams.setKeyValue(new JSONArray(Arrays.asList(weekendValues)).toString());
        return weekendParams;
    }

    private void addMLModelParams(List<MLModelParamsContext> mlParams) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getMLModelParamsModule().getTableName())
                .fields(FieldFactory.getMLModelParamsFields());
        List<Map<String, Object>> props = FieldUtil.getAsMapList(mlParams, MLModelParamsContext.class);
        insertBuilder.addRecords(props);
        insertBuilder.save();
    }

}
