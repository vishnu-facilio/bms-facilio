package com.facilio.utility.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.utility.context.UtilityIntegrationTariffContext;
import com.facilio.utility.context.UtilityIntegrationTariffSlabContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ValidateUtilityIntegrationTariffCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<UtilityIntegrationTariffContext> tariffs = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(tariffs)) {
            for (UtilityIntegrationTariffContext tariff : tariffs) {

                if (tariff.getId() != 0 && tariff.getId() != -1) {
                    UtilityIntegrationTariffContext existingTariff = getExistingTariffById(tariff.getId());
                    if (existingTariff != null) {
                        if (!tariff.getName().equals(existingTariff.getName())) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Name cannot be changed while editing.");
                        }
                        if (!tariff.getUtilityType().equals(existingTariff.getUtilityType())) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Utility type cannot be changed while editing.");
                        }
                        if (!tariff.getUtilityProviders().equals(existingTariff.getUtilityProviders())) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Utility provider cannot be changed while editing.");
                        }
                    }
                }
                else {

                    boolean isValidName = checkIfNameAlreadyExist(tariff.getName());
                    if (isValidName) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tariff with name " + tariff.getName() + "  already exists");
                    }
                }
            }
        }
        return false;
    }
    private static boolean checkIfNameAlreadyExist(String name) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

        String slabModuleName = FacilioConstants.UTILITY_INTEGRATION_TARIFF;
        List<FacilioField> fields = modBean.getAllFields(slabModuleName);

        SelectRecordsBuilder<UtilityIntegrationTariffContext> builder = new SelectRecordsBuilder<UtilityIntegrationTariffContext>()
                .moduleName(slabModuleName)
                .select(fields)
                .beanClass(UtilityIntegrationTariffContext.class)
                .andCondition(CriteriaAPI.getCondition("NAME","name", name, StringOperators.IS));


        List<UtilityIntegrationTariffContext> props = builder.get();

        if(props == null || props.isEmpty()) {
            return false;
        }

        return true;
    }
    public static boolean validateSlabs(List<UtilityIntegrationTariffSlabContext> slabs) {

        slabs.sort(Comparator.comparingDouble(UtilityIntegrationTariffSlabContext::getFrom));

        for (int i = 1; i < slabs.size(); i++) {
            UtilityIntegrationTariffSlabContext previousSlab = slabs.get(i - 1);
            UtilityIntegrationTariffSlabContext currentSlab = slabs.get(i);

            if (currentSlab.getFrom() < previousSlab.getTo()) {
                return false; // Overlapping slabs found
            }

        }

        return true; // No overlaps detected
    }
    public static boolean isValidSlabs(List<UtilityIntegrationTariffSlabContext> slabs){

        slabs.sort(Comparator.comparingDouble(UtilityIntegrationTariffSlabContext::getFrom));

        for (UtilityIntegrationTariffSlabContext slab : slabs) {
            return slab.getFrom() >=0 && slab.getTo() > slab.getFrom(); //valid range
        }
        return false; //invalid range
    }
    private UtilityIntegrationTariffContext getExistingTariffById(Long id) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String slabModuleName = FacilioConstants.UTILITY_INTEGRATION_TARIFF;
        FacilioModule module = modBean.getModule(FacilioConstants.UTILITY_INTEGRATION_TARIFF);
        List<FacilioField> fields = modBean.getAllFields(slabModuleName);

        SelectRecordsBuilder<UtilityIntegrationTariffContext> builder = new SelectRecordsBuilder<UtilityIntegrationTariffContext>()
                .moduleName(slabModuleName)
                .select(fields)
                .beanClass(UtilityIntegrationTariffContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id,module));


        List<UtilityIntegrationTariffContext> props = builder.get();
        if(props != null){
            return props.get(0);
        }
        return null;
    }
}
