package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerSettingsContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.criteria.BuildingOperator;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());

	public static List<ControllerSettingsContext> getAllControllers() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getControllerModule().getTableName())
				.select(FieldFactory.getControllerFields())
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ControllerSettingsContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				controllers.add(FieldUtil.getAsBeanFromMap(prop, ControllerSettingsContext.class));
			}
			return controllers;
		}
		return null;
	}

	//for the org..
	public static List<EnergyMeterContext> getAllEnergyMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		return selectBuilder.get();
	}

	//for building..
	public static List<EnergyMeterContext> getMainEnergyMeter(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	//for org..
	public static List<EnergyMeterContext> getAllMainEnergyMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	//for org..
	public static List<EnergyMeterContext> getAllServiceMeters() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NOT NULL")
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getServiceMeter(long purposeId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("PURPOSE_ID= ?",purposeId )
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	// for building..
	public static List<EnergyMeterContext> getRootServiceMeters(String buildingList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NOT NULL")
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE-SPACE_ID",buildingList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}


	//for org..
	public static List<EnergyMeterContext> getEnergyMetersOfPurpose(String purposeList,boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT=?",root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	//floor/space/zone service/purpose meters when root is false..
	// building's raiser Main of a purpose when root is true with building ID..
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList,String purposeList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?",root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("PURPOSE_ID","PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}

	//building root meters including service/purpose meters when root is true..
	// floor meters when root is false & spaceList as floor ID...
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);

		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", root)
				.andCondition(CriteriaAPI.getCondition("PURPOSE_SPACE_ID","PURPOSE-SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(Long buildingId, Long purposeId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.maxLevel(0);
		
		if (buildingId != null) {
			FacilioField spaceIdFld = new FacilioField();
			spaceIdFld.setName("space_id");
			spaceIdFld.setColumnName("SPACE_ID");
			spaceIdFld.setModule(ModuleFactory.getAssetsModule());
			spaceIdFld.setDataType(FieldType.NUMBER);
			
			Condition spaceCond = new Condition();
			spaceCond.setField(spaceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
			
			selectBuilder.andCondition(spaceCond);
		}
		if (purposeId != null) {
			selectBuilder.andCustomWhere("PURPOSE_ID= ?", purposeId);
		}
		
		return selectBuilder.get();
	}
	
	//for org..
		public static List<EnergyMeterPurposeContext> getAllPurposes() throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
			SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
					new SelectRecordsBuilder<EnergyMeterPurposeContext>()
					.select(modBean.getAllFields(module.getName()))
					.module(module)
					.beanClass(EnergyMeterPurposeContext.class)
					.maxLevel(0);
			return selectBuilder.get();
		}
		
		
		public static List<EnergyMeterContext> getAllVirtualMeters() throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
					.select(fields)
					.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
					.beanClass(EnergyMeterContext.class)
					.andCustomWhere("IS_VIRTUAL = ?", true);

			List<EnergyMeterContext> virtualMeters = selectBuilder.get();
			return virtualMeters;
		}
		
		public static List<EnergyMeterContext> getVirtualMeters(String deviceList) throws Exception {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ENERGY_METER);
			SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
					.select(fields)
					.moduleName(FacilioConstants.ContextNames.ENERGY_METER)
					.beanClass(EnergyMeterContext.class)
					.andCustomWhere("IS_VIRTUAL = ?", true)
					.andCondition(CriteriaAPI.getCondition("ID","ID", deviceList,NumberOperators.EQUALS));
			List<EnergyMeterContext> virtualMeters = selectBuilder.get();
			return virtualMeters;
		}

}
