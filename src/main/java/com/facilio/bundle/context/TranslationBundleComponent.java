package com.facilio.bundle.context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.localization.translationbean.TranslationBean;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import io.jsonwebtoken.lang.Collections;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TranslationBundleComponent extends CommonBundleComponent{
	@Override
	public void getFormatedObject(FacilioContext context) throws Exception{

		Long translationId = (Long) context.get(BundleConstants.COMPONENT_ID);
		XMLBuilder xmlBuilder = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);

		Map<String,Object> prop = TranslationsUtil.getTranslation((Long) context.get(BundleConstants.COMPONENT_ID));


		String langCode = (String) prop.get(TranslationConstants.LANG_CODE);

		TranslationBean bean = (TranslationBean) TransactionBeanFactory.lookup("TranslationBean");
		Properties properties = bean.getTranslationFile(langCode);
		FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

		Set<Map.Entry<Object, Object>> entries = properties.entrySet();
		ModuleBean modBean = Constants.getModBean();

		for (Map.Entry<Object, Object> entry : entries) {

			String itr = (String) entry.getKey();
			String splittedVal[] = itr.split("\\.");
			String prefix = splittedVal[0];
			String key = splittedVal[1];

			if(Character.isDigit(key.charAt(0))){
				switch(prefix){
					case "webTab":
						key = ApplicationApi.getWebTab(Long.parseLong(key)).getName();
						break;
					case "field":
						FacilioField field = modBean.getField(Long.parseLong(key));
						if(field != null){
							key = field.getName();
						}
					case "module":
						FacilioModule module = modBean.getModule(Long.parseLong(key));
						if(module != null){
							key = module.getName();
						}
						break;
				}

			}
			String suffix = splittedVal[2];

			xmlBuilder
					.attr(TranslationConstants.LANG_CODE, langCode)
					.element(TranslationConstants.PREFIX).text(prefix).p()
					.element(TranslationConstants.KEY).text(key).p()
					.element(TranslationConstants.SUFFIX).text(suffix).p();
		}

	}

	@Override
	public String getBundleXMLComponentFileName(FacilioContext context) throws Exception{

		Map<String,Object> prop = TranslationsUtil.getTranslation((Long) context.get(BundleConstants.COMPONENT_ID));
		String langCode = (String) prop.get(TranslationConstants.LANG_CODE);
		return langCode;

	}

	@Override
	public BundleModeEnum getInstallMode(FacilioContext context) throws Exception{

		XMLBuilder xmlContent = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);

		String translationFileName = xmlContent.getElement(TranslationConstants.LANG_CODE).getText();

		long fileId = TranslationsUtil.getTranslationFileId(translationFileName);

		BundleModeEnum installMode = null;

		if(fileId > 0L) {
			installMode = BundleModeEnum.UPDATE;
		}
		else {
			installMode = BundleModeEnum.ADD;
		}

		return installMode;
	}

	@Override
	public void install(FacilioContext context) throws Exception{

		InstalledBundleContext installedBundle = (InstalledBundleContext) context.get(BundleConstants.INSTALLED_BUNDLE);

		BundleModeEnum modeEnum = (BundleModeEnum) context.get(BundleConstants.INSTALL_MODE);

		TranslationBean bean = (TranslationBean)TransactionBeanFactory.lookup("TranslationBean");

		switch(modeEnum){
			case ADD:
				XMLBuilder element = (XMLBuilder) context.get(BundleConstants.COMPONENT_XML_BUILDER);

				String langCode = element.getAttribute(TranslationConstants.LANG_CODE);

				if(TranslationsUtil.getTranslationFileId(langCode) < 0L){
					bean.add(langCode);
				}

				bean.save(langCode,prepareTranslationList(element));

				break;

			case UPDATE:
				break;
		}

	}

	private List<Map<String, Object>> prepareTranslationList(XMLBuilder element){

		List<Map<String,Object>> props = new ArrayList<>();

		Map<String,Object> prop = new HashMap<>();

		prop.put(TranslationConstants.PREFIX,element.getElement(TranslationConstants.PREFIX));
		prop.put(TranslationConstants.KEY,element.getElement(TranslationConstants.KEY));
		prop.put(TranslationConstants.SUFFIX,element.getElement(TranslationConstants.SUFFIX));
		props.add(prop);
		return props;
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception{

	}

	@Override
	public void getAddedChangeSet(FacilioContext context) throws Exception{

		BundleComponentsEnum component = (BundleComponentsEnum) context.get(BundleConstants.COMPONENT);

		BundleContext bundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);

		Map<BundleComponentsEnum, List<BundleChangeSetContext>> changeSetCache = (Map<BundleComponentsEnum, List<BundleChangeSetContext>>) context.get(BundleConstants.CHANGE_SET_CACHE);

		List<Long> alreadyAddedComponentIds = new ArrayList<Long>();

		if(!Collections.isEmpty(changeSetCache.get(component))) {
			alreadyAddedComponentIds.addAll(changeSetCache.get(component).stream().map(BundleChangeSetContext::getComponentId).collect(Collectors.toList()));
		}

		Map<String, FacilioField> componentFieldMap = component.getFields().stream().collect(Collectors.toMap(FacilioField::getName, Function.identity()));

		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
													.select(component.getFields())
													.table(component.getModule().getTableName())
													.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getModifiedTimeFieldName()), "", CommonOperators.IS_NOT_EMPTY))
				;


		if(!Collections.isEmpty(alreadyAddedComponentIds)) {
			select.andCondition(CriteriaAPI.getCondition(componentFieldMap.get(component.getIdFieldName()), StringUtils.join(alreadyAddedComponentIds, ","), NumberOperators.NOT_EQUALS));
		}

		Condition condition = getFetchChangeSetCondition(context);

		if(condition != null) {
			select.andCondition(condition);
		}

		Map<String, Object> prop = select.fetchFirst();

		List<BundleChangeSetContext> changeSet = new ArrayList<BundleChangeSetContext>();

		if(prop != null && !prop.isEmpty()) {

			String langCode = (String) prop.get(TranslationConstants.LANG_CODE);

			TranslationBean bean = (TranslationBean) TransactionBeanFactory.lookup("TranslationBean");
			Properties properties = bean.getTranslationFile(langCode);
			FacilioUtil.throwIllegalArgumentException(properties == null,"Translation file is an empty");

			properties.forEach((key, value) -> System.out.println(key + " : " + value));

			if(isPackableComponent(context)) {
				BundleChangeSetContext change = new BundleChangeSetContext();

				long componentID = (Long)prop.get(component.getIdFieldName());

				context.put(BundleConstants.COMPONENT_ID, componentID);

				change.setComponentId(componentID);
				change.setComponentTypeEnum(component);
				change.setModeEnum(BundleModeEnum.ADD);

				changeSet.add(change);
				context.put(BundleConstants.CHANGE_SET, changeSet);
			}
		}else {
			context.put(BundleConstants.CHANGE_SET, null);
		}

	}
}
