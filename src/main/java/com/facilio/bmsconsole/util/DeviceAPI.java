package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.context.EnergyMeterPurposeContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class DeviceAPI 
{
	private static Logger logger = Logger.getLogger(DeviceAPI.class.getName());

	public static List<ControllerContext> getAllControllers() throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(ModuleFactory.getControllerModule().getTableName())
														.select(FieldFactory.getControllerFields())
														.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<ControllerContext> controllers = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				controllers.add(FieldUtil.getAsBeanFromMap(prop, ControllerContext.class));
			}
			return controllers;
		}
		return null;
	}
	
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
	
	public static List<EnergyMeterContext> getEnergyMetersOfSpace(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCondition(getCondition("PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getEnergyMetersOfPurpose(String purposeList,boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT=?",root)
				.andCondition(getCondition("PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList,String purposeList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?",root)
				.andCondition(getCondition("PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.andCondition(getCondition("PURPOSE_ID",purposeList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	public static List<EnergyMeterContext> getAllEnergyMeters(String spaceList, boolean root) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", root)
				.andCondition(getCondition("PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	
	public static List<EnergyMeterContext> getRootEnergyMeter(String spaceList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		
		SelectRecordsBuilder<EnergyMeterContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterContext.class)
				.andCustomWhere("IS_ROOT= ?", true)
				.andCustomWhere("PARENT_ASSET_ID IS NULL")
				.andCondition(getCondition("PURPOSE_SPACE_ID",spaceList,NumberOperators.EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
			
	public static List<EnergyMeterPurposeContext> getEnergyMeterPurpose(String purpose) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		
		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.andCustomWhere("NAME = ?", purpose)
				.maxLevel(0);
		return selectBuilder.get();
	}
	
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
	
	public static List<EnergyMeterPurposeContext> getFilteredPurposes(String purposeFilter,NumberOperators operator) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		
		SelectRecordsBuilder<EnergyMeterPurposeContext> selectBuilder = 
				new SelectRecordsBuilder<EnergyMeterPurposeContext>()
				.select(modBean.getAllFields(module.getName()))
				.module(module)
				.beanClass(EnergyMeterPurposeContext.class)
				.andCondition(getCondition("ID",purposeFilter,operator))
				//.andCondition(getCondition("ID",purposeFilter,NumberOperators.NOT_EQUALS))
				.maxLevel(0);
		return selectBuilder.get();
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Condition getCondition (String colName,String valueList,Operator operator)
	{
				Condition condition = new Condition();
				condition.setColumnName(colName);
				condition.setOperator(operator);
				condition.setValue(valueList);
				return condition;
	}
	
}
