package com.facilio.utility.context;

import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FieldUtil;
import com.facilio.util.ArithmeticUtil;
import com.facilio.v3.context.V3Context;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONArray;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UtilityIntegrationTariffContext extends V3Context {

    private static final long serialVersionUID = 1L;

    Double flatRatePerUnit;
    String name;
    String description;
    Double fuelSurcharge;
    List<UtilityIntegrationTariffSlabContext> tarriffSlabs;
    Long fromDate ;
    Long toDate ;
    String unit;

    //private Integer utilityProviders;
    private UtilityProviders utilityProviders;

    public UtilityProviders getUtilityProvidersEnum() {
        return utilityProviders;
    }

    public Integer getUtilityProviders() {
        if(utilityProviders != null) {
            return utilityProviders.getIndex();
        }
        else {
            return null;
        }
    }
    public void setUtilityProviders(Integer utilityProviders) {
        if (utilityProviders != null) {
            this.utilityProviders = UtilityProviders.valueOf(utilityProviders);
        }
    }
    public  enum UtilityProviders implements FacilioIntEnum {
        ACE("ACE","Atlantic City Electric"),
        AEPOHIO("AEPOHIO","American Electric Power Ohio"),
        ALPower("ALPower","Alabama Power"),
        APS("APS","Arizona Public Service Company"),
        Ameren("Ameren","Ameren"),
        AppalachianPower("AppalachianPower","Appalachian Power"),
        AustinEnergy("AustinEnergy","Austin Energy"),
        BGE("BGE","Baltimore Gas & Electric"),
        CEIC("CEIC","Cleveland Electric Illuminating Company"),
        ComEd("ComEd","Commonwealth Edison"),
        ConEd("ConEd","Consolidated Edison New York"),
        DEMO("DEMO","Demonstration Utility"),
        DOMINION("DOMINION","Dominion Energy"),
        Duke("Duke","Duke Energy"),
        EVRSRC("EVRSRC","Eversource Energy"),
        FPL("FPL","Florida Power and Light Company"),
        GAPower("GAPower","Georgia Power"),
        HECO("HECO","Hawaii Electric"),
        JCPL("JCPL","Jersey Central Power and Light"),
        LADWP("LADWP","Los Angeles Department of Water & Power"),
        MonPower("MonPower","Mon Power"),
        NATGD("NATGD","National Grid"),
        NVE("NVE","Nevada Energy"),
        NYSEG("NYSEG","New York State Electric and Gas Corporation"),
        OhioEd("OhioEd","Ohio Edison"),
        ORU("ORU","Orange and Rockland Utilities"),
        PacPower("PacPower","Pacific Power Utilities"),
        PECO("PECO","PECO Energy"),
        Penelec("Penelec","Penelec"),
        PEPCO("PEPCO","Potomac Electric Power Company"),
        PGE("PG&E","Pacific Gas and Electric"),
        PORTGE("PORTGE","Portland General Electric"),
        PPL("PPL","Pennsylvania Power and Light"),
        PSEG("PSEG","Public Services Electric and Gas"),
        PSEGLI("PSEGLI","Public Services Electric and Gas - Long Island"),
        PSO("PSO","Public Service Company of Oklahoma"),
        RMP("RMP","Rocky Mountain Power"),
        SCE("SCE","Southern California Edison"),
        SDGE("SDG&E","San Diego Gas and Electric"),
        SFPUC("SFPUC","San Francisco Public Utilities Commission"),
        SMUD("SMUD","Sacramento Municipal Utility District"),
        SoCalGas("SoCalGas","Southern California Gas Company"),
        SRP("SRP","Salt River Project"),
        TEP("TEP","Tucson Electric Power"),
        WestPennPower("WestPennPower","West Penn Power"),
        XCEL("XCEL","Xcel Energy");


        private final String code;
        private final String value;
        UtilityProviders(String code, String value) {
            this.code = code;this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        public String getCode() {
            return code;
        }
        @Override
        public String getValue() {
            return value;
        }


        public static UtilityProviders valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
   }
    //private Integer utilityType;

    private UtilityType utilityType;

    public UtilityType getUtilityTypeEnum() {
        return utilityType;
    }

    public Integer getUtilityType() {
        if(utilityType != null) {
            return utilityType.getIndex();
        }
        else {
            return null;
        }
    }
    public void setUtilityType(Integer utilityType) {
        if (utilityType != null) {
            this.utilityType = UtilityType.valueOf(utilityType);
        }
    }
    public  enum UtilityType implements FacilioIntEnum {
        GAS("Gas"),
        WATER("Water"),
        ELECTRICITY("Electricity"),
        BTUMETER("Heat Meter (LTHW and CHW)");

        private final String value;
        UtilityType(String value) {
            this.value = value;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
                return value;
        }

        public static UtilityType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }


    public Double calculateCost(double consumption) {

        BigDecimal cost = new BigDecimal("0");
        if(flatRatePerUnit > 0) {
            BigDecimal cons = new BigDecimal(String.valueOf(consumption));
            cost = cons.multiply(new BigDecimal(String.valueOf(flatRatePerUnit)));
        }
        else if(getTarriffSlabs() != null) {

            for(UtilityIntegrationTariffSlabContext slab : getTarriffSlabs()) {

                if(consumption == 0) {
                    break;
                }

                if(slab.getTo() > 0) {
                    double slabBar = slab.getTo() - slab.getFrom();

                    if((consumption - slabBar) > 0) {
                        consumption = consumption - slabBar;
                    }
                    else {
                        slabBar = consumption;
                        consumption = 0;
                    }

                    BigDecimal bigDSlabBar = new BigDecimal(""+slabBar);
                    BigDecimal bigDSlabPrice = new BigDecimal(""+slab.getPrice());

                    cost = cost.add(bigDSlabBar.multiply(bigDSlabPrice));
                }
                else {
                    BigDecimal bigDConsumption = new BigDecimal(""+consumption);
                    BigDecimal bigDSlabPrice = new BigDecimal(""+slab.getPrice());

                    cost = cost.add(bigDConsumption.multiply(bigDSlabPrice));
                    consumption = 0;
                    break;
                }
            }
        }

//        if(fuelSurcharge > 0) {
//            double fuelSurchabgePrice = ArithmeticUtil.multiply(String.valueOf(consumption), getFuelSurcharge()+"");
//            cost = cost.add(new BigDecimal(""+fuelSurchabgePrice));
//        }

        return cost.doubleValue();
    }

    public JSONArray getSlabJson(double consumption) throws Exception {

        JSONArray slabJson = new JSONArray();

        List<UtilityIntegrationTariffSlabContext> applicableTarriffs = new ArrayList<>();

        if(getTarriffSlabs() != null) {
            BigDecimal cost = new BigDecimal("0");

            for(UtilityIntegrationTariffSlabContext slab : getTarriffSlabs()) {

                slab.setUnit(this.getUnit());

                if(consumption == 0) {
                    break;
                }

                if(slab.getTo() > 0) {
                    double slabBar = slab.getTo() - slab.getFrom();

                    if((consumption - slabBar) > 0) {
                        consumption = consumption - slabBar;
                    }
                    else {
                        slabBar = consumption;
                        consumption = 0;
                    }

                    BigDecimal bigDSlabBar = new BigDecimal(""+slabBar);
                    BigDecimal bigDSlabPrice = new BigDecimal(""+slab.getPrice());

                    BigDecimal result = bigDSlabBar.multiply(bigDSlabPrice);

                    slab.setAmount(result.doubleValue());
                    slab.setConsumption(bigDSlabBar.doubleValue());

                    cost = cost.add(result);

                    applicableTarriffs.add(slab);
                }
                else {
                    BigDecimal bigDConsumption = new BigDecimal(""+consumption);
                    BigDecimal bigDSlabPrice = new BigDecimal(""+slab.getPrice());

                    BigDecimal result = bigDConsumption.multiply(bigDSlabPrice);

                    slab.setAmount(result.doubleValue());
                    slab.setConsumption(bigDConsumption.doubleValue());

                    cost = cost.add(result);
                    consumption = 0;
                    applicableTarriffs.add(slab);
                    break;
                }
            }
            slabJson = FieldUtil.getAsJSONArray(applicableTarriffs, UtilityIntegrationTariffSlabContext.class);
        }

        return slabJson;
    }

}
