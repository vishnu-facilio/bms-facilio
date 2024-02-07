"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[91859],{
/***/641384:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return BaseConfig}});
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/objectSpread2.js
var render,staticRenderFns,objectSpread2=__webpack_require__(595082),createForOfIteratorHelper=__webpack_require__(566347),esm_typeof=__webpack_require__(103336),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),validation=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(670560),__webpack_require__(169358),__webpack_require__(821057),__webpack_require__(450886),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(425728),__webpack_require__(434284),__webpack_require__(990260)),debounce=__webpack_require__(23279),debounce_default=__webpack_require__.n(debounce),cloneDeep=__webpack_require__(150361),cloneDeep_default=__webpack_require__.n(cloneDeep),uuid=__webpack_require__(455877),card_constants=__webpack_require__(806186),isString=__webpack_require__(747037),isString_default=__webpack_require__.n(isString),api=__webpack_require__(32284),BaseConfigvue_type_script_lang_js={props:["isNew","savedCardData","cardType","onClose","onGoBack","onCardSave","onCardUpdate","closePopup","cardMeta","isDuplicate","onCardDuplicate"],data:function(){return{conditionalFormatting:{},decimalPlaces:card_constants/* decimalPlaces */.N3,scriptModeInt:null,customScriptId:null,nameSpaces:[],enableBoxchecked:!1}},created:function(){this.initializeValidators();var isNew=this.isNew,savedCardData=this.savedCardData;isNew||(0,validation/* isEmpty */.xb)(savedCardData)||(this.deserialize(this.savedCardData.cardParams),this.deserializeState(this.savedCardData.cardState),this.deserializeActions(this.savedCardData.cardDrilldown),this.deserializeConditions(this.savedCardData.conditionalFormatting),this.deserializeCustomScriptData(this.savedCardData),
// TODO Revaluate if necessary and model this as computed
this.setConditionalData(this.cardDataObj))},watch:{cardDataObj:{deep:!0,handler:function(){this.validate()&&this.debouncedGetData()}},cardStateObj:{deep:!0,handler:function(){this.validate()&&this.debouncedGetData()}},conditionalFormatting:{deep:!0,handler:function(){this.validate()&&this.debouncedGetData()}},customScriptId:{deep:!0,handler:function(newval,oldVal){newval!==oldVal&&this.debouncedGetData()}}},computed:{previewState:function(){var result=this.result,cardStateObj=this.cardStateObj,_ref=result||{},cardContext=_ref.cardContext;return(0,validation/* isEmpty */.xb)(result&&cardContext)?this.serializeState(cardStateObj):result.state?result.state:this.serializeState(cardStateObj)},componentKey:function(){return(0,uuid.v4)()},variables:function(){var result=this.result;if(result&&result.data){var previewData=result.data,variables=previewData.variables;if(variables)return variables}return[]}},methods:{setProps:function(){3===this.scriptModeInt||null===this.scriptModeInt?this.enableBoxchecked=!1:null!==this.scriptModeInt&&(this.enableBoxchecked=!0)},loadNameSpaceList:function(){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,api/* API */.bl.get("/v2/workflow/getNameSpaceListWithFunctions").then((function(_ref2){var data=_ref2.data;_this.nameSpaces=data.workflowNameSpaceList?data.workflowNameSpaceList:[]})).catch((function(){_this.nameSpaces=[]}));case 2:case"end":return _context.stop()}}),_callee)})))()},deserializeCustomScriptData:function(_ref3){var customScriptId=_ref3.customScriptId,scriptModeInt=_ref3.scriptModeInt;this.customScriptId=customScriptId,this.scriptModeInt=scriptModeInt},serializeCustomScriptData:function(_ref4){var customScriptId=_ref4.customScriptId,scriptModeInt=_ref4.scriptModeInt;this.customScriptId=customScriptId,this.scriptModeInt=scriptModeInt},validate:function(){var hasEmptyField=!1,customRules=this.validateProperty()||{},cardDataObj=this.serialize();return this.resourceProps.forEach((function(prop){var isMap="object"===(0,esm_typeof/* default */.Z)(prop),propName=isMap?prop.prop:prop;customRules[propName]?customRules[propName](cardDataObj)&&(hasEmptyField=!0):isMap&&prop.resourceProps?prop.resourceProps.forEach((function(p){((0,validation/* isEmpty */.xb)(cardDataObj[propName])||(0,validation/* isEmpty */.xb)(cardDataObj[propName][p]))&&(hasEmptyField=!0)})):(0,validation/* isEmpty */.xb)(cardDataObj[propName])&&(hasEmptyField=!0)})),!hasEmptyField},changescriptModeInt:function(){var id=[];if(this.nameSpaces){var _step,_iterator=(0,createForOfIteratorHelper/* default */.Z)(this.nameSpaces);try{for(_iterator.s();!(_step=_iterator.n()).done;){var nameSpace=_step.value;if(null!==nameSpace.functions){var _step2,_iterator2=(0,createForOfIteratorHelper/* default */.Z)(nameSpace.functions);try{for(_iterator2.s();!(_step2=_iterator2.n()).done;){var func=_step2.value;id.push(func.id)}}catch(err){_iterator2.e(err)}finally{_iterator2.f()}}}}catch(err){_iterator.e(err)}finally{_iterator.f()}}1==this.enableBoxchecked?(id.length&&(this.customScriptId=id[0]),this.scriptModeInt=2):(this.customScriptId=-99,this.scriptModeInt=3)},validateProperty:function(){
// To be overriden in card component if data validation rule for a property
// needs to be more complex that just an isEmpty() check
// {
//   prop1: validatorFn1(data) {
//      return true when field is invalid or has error
//   },
//   prop2: validatorFn2(data) {
//      return false when data is correct
//   }
// }
},validateForm:function(){var form=this.$refs["".concat(this.cardLayout,"_form")];return new Promise((function(resolve,reject){form.validate((function(isValid){isValid?resolve():reject()}))}))},initializeValidators:function(){var _this2=this,cardDataObj=this.cardDataObj,customValidationRules=this.validateField()||{},validationRules={};Object.keys(cardDataObj).forEach((function(prop){var propName="object"===(0,esm_typeof/* default */.Z)(prop)?prop.prop:prop,validator=function(rule,value,callback){(0,validation/* isEmpty */.xb)(this.cardDataObj[propName])?callback(new Error("Field can not be empty")):callback()}.bind(_this2),type=_this2.$getProperty(_this2,"cardMeta.type")||null;"KPICARD2"===type&&"dateRange"===propName||(validationRules[propName]={trigger:"blur",validator:validator})})),this.$set(this,"validationRules",(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},validationRules),customValidationRules))},validateField:function(){
// To be overriden in card component if form validation rule for a property
// needs to be more complex that just an isEmpty() check
// Return an object with prop vs {trigger, validator} (ElementUI form rule format)
// as below:
// {
//   prop1: {trigger: 'change', validator: (rule, value, callback)=>{}}
//   prop2: {trigger: 'blur', validator: (rule, value, callback)=>{}}
// }
},deserialize:function(cardParams){var _this3=this,params=cloneDeep_default()(cardParams);Object.keys(params).forEach((function(prop){return _this3.deserializeProperty(prop,params)}))},deserializeProperty:function(prop,data){
// To be overriden in card components if required.
// By default only sets the props in resourceProps to cardDataObj
var validProps=this.resourceProps.map((function(p){return"object"===(0,esm_typeof/* default */.Z)(p)?p.prop:p}));validProps.includes(prop)&&this.$set(this.cardDataObj,prop,data[prop])},serialize:function(){var _data$kpiType,_this4=this,resourceProps=this.resourceProps,cardDataObj=this.cardDataObj,data={};resourceProps.forEach((function(prop){var propName="object"===(0,esm_typeof/* default */.Z)(prop)?prop.prop:prop;data[propName]=cardDataObj[propName];var value=_this4.serializeProperty(propName,cardDataObj);(0,validation/* isUndefined */.o8)(value)||(data[propName]=value)}));var kpiType=null!==(_data$kpiType=null===data||void 0===data?void 0:data.kpiType)&&void 0!==_data$kpiType?_data$kpiType:null;if("reading"===kpiType){var newData=this.$helpers.cloneObject(data),_ref5=null!==newData&&void 0!==newData?newData:{},kpis=_ref5.kpis,maxSafeLimitKpi=_ref5.maxSafeLimitKpi,centerTextKpi=_ref5.centerTextKpi,kpi=_ref5.kpi;return(0,validation/* isEmpty */.xb)(kpis)||(newData.kpis=kpis.map((function(kpi){var kpiId=kpi.kpiId;return!(0,validation/* isEmpty */.xb)(kpiId)&&isString_default()(kpiId)&&(kpi.kpiId=parseInt(kpiId.split("_")[0])),kpi}))),[maxSafeLimitKpi,centerTextKpi,kpi].forEach((function(key,index){if(!(0,validation/* isEmpty */.xb)(key)){var kpiId=key.kpiId;key.kpiId=isString_default()(kpiId)?parseInt(kpiId.split("_")[0]):kpiId,0===index?newData["maxSafeLimitKpi"]=key:1===index?newData["centerTextkpi"]=key:newData["kpi"]=key}})),newData}return data},serializeProperty:function(){
// To be implemented in card components if any special handling is required
// before sending to server, for any of the props in resourceProps
}/* prop, data */,serializeState:function(){
// To be overriden in the card components as required.
// By default, sends the cardStateObj as it is.
var cardStateObj=this.cardStateObj;if(!(0,validation/* isEmpty */.xb)(cardStateObj))return cardStateObj},serializeConditions:function(){
// to serialize the conditional formatting
var conditionalFormatting=this.conditionalFormatting;if(!(0,validation/* isEmpty */.xb)(conditionalFormatting))return conditionalFormatting},deserializeConditions:function(conditionalFormatting){
// TO deserialize the state
(0,validation/* isEmpty */.xb)(conditionalFormatting)||this.$set(this,"conditionalFormatting",conditionalFormatting)},deserializeState:function(cardState){
// To be overriden in the card components if required.
// Receives cardState from server as the only param
(0,validation/* isEmpty */.xb)(cardState)||this.$set(this,"cardStateObj",cardState)},serializeActions:function(){
// To be overriden in the card components as required.
// By default, sends the cardActions as it is.
var cardActions=this.cardActions;if(!(0,validation/* isEmpty */.xb)(cardActions))return cardActions},deserializeActions:function(cardDrilldown){
// To be overriden in the card components if required.
// Receives cardDrillDdown from server as the only param
(0,validation/* isEmpty */.xb)(cardDrilldown)||this.$set(this,"cardActions",cardDrilldown)},configWatchHook:function(){
// use this method insted of watch => config
},customValidation:function(){
// this methods should be removed
// if any custom validation requirement needed then use this method ..also this is the last validation
return!0},getData:function(){var _this5=this;this.configWatchHook();var data={cardContext:{cardLayout:this.cardLayout,cardParams:this.serialize(),cardState:this.serializeState(),cardDrilldown:this.serializeActions(),conditionalFormatting:this.serializeConditions(),customScriptId:this.customScriptId,scriptModeInt:this.scriptModeInt}};return this.isPreviewLoading=!0,this.$http.post("/v2/dashboard/cards/getCardData",data).then((function(_ref6){var data=_ref6.data;0===data.responseCode?_this5.$set(_this5,"result",data.result):_this5.$set(_this5,"result",null),_this5.isPreviewLoading=!1})).catch((function(){_this5.isPreviewLoading=!1}))},debouncedGetData:debounce_default()((function(){return this.getData()}),1e3),save:function(){var _this6=this;this.validateForm().then((function(){if(_this6.customValidation()){var data={cardContext:{cardLayout:_this6.cardLayout,cardParams:_this6.serialize(),cardState:_this6.serializeState(),cardDrilldown:_this6.serializeActions(),conditionalFormatting:_this6.serializeConditions(),customScriptId:_this6.customScriptId,scriptModeInt:_this6.scriptModeInt}};_this6.isNew?_this6.onCardSave(data,_this6.layout):_this6.isDuplicate?_this6.onCardDuplicate(data,_this6.layout):_this6.onCardUpdate(data,_this6.layout)}})).catch((function(){}))},close:function(){this.onClose()},setConditionalData:function(cardDataObj){var reading=cardDataObj.reading,readings=cardDataObj.readings,readingArray=(0,validation/* isEmpty */.xb)(reading)?(0,validation/* isEmpty */.xb)(readings)?[]:readings:[reading];this.setConditionalVariables(readingArray,!0)},setConditionalVariables:function(){var _this7=this,readings=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[],skipRerender=arguments.length>1?arguments[1]:void 0;Array.isArray(readings)||(readings=[readings]);var cardType=this.cardType;readings.forEach((function(reading){var dataType=reading.dataType,conditionalVariables=_this7.conditionalVariables,variable=(conditionalVariables||[]).find((function(v){return"value"===v.name})),assignOperator=function(){variable.dataType=2===dataType?"NUMBER":3===dataType?"DECIMAL":4===dataType?"BOOLEAN":"STRING"};("kpi"===cardType&&dataType||!(0,validation/* isEmpty */.xb)(conditionalVariables)&&variable&&"kpi"!=cardType)&&assignOperator(),skipRerender||_this7.rerenderConditionalFormatting()}))},rerenderConditionalFormatting:function(){var conditionalComponent=this.$refs["conditional-formatting"];(0,validation/* isEmpty */.xb)(conditionalComponent)||conditionalComponent.rerender()}}},common_BaseConfigvue_type_script_lang_js=BaseConfigvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(common_BaseConfigvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,BaseConfig=component.exports;
// EXTERNAL MODULE: ./node_modules/@babel/runtime/helpers/esm/createForOfIteratorHelper.js
},
/***/391859:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return type1_Config}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/cards/floorPlan/type1/Config.vue?vue&type=template&id=46df513f&scoped=true
var Config_render,Config_staticRenderFns,render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30"},[_c("div",{staticClass:"header cards-config-header "},[_c("span",{staticClass:"pointer",on:{click:_vm.onGoBack}},[_c("inline-svg",{staticClass:"vertical-top rotate-90 mR20",attrs:{src:"svgs/arrow",iconClass:"icon"}})],1),_vm._v(" "+_vm._s(_vm.cardMeta&&_vm.cardMeta.name||"Web Card")+" "),_c("span",{staticClass:"pointer",on:{click:_vm.onClose}},[_c("inline-svg",{staticClass:"vertical-middle fR",attrs:{src:"svgs/close",iconClass:"icon icon-sm"}})],1)]),_c("div",{staticClass:"container mT20 mB60"},[_c("div",{staticClass:"section"},[_c("el-form",{ref:this.cardLayout+"_form",attrs:{model:_vm.cardDataObj,"label-position":"top"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"title"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Title")]),_c("el-input",{staticClass:"width100 fc-input-full-border2",attrs:{autofocus:_vm.isNew},model:{value:_vm.cardDataObj.title,callback:function($$v){_vm.$set(_vm.cardDataObj,"title",$$v)},expression:"cardDataObj.title"}})],1)],1)],1),_c("el-row",{staticClass:"mB10",attrs:{gutter:20}},[_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"siteId"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Sites")]),_c("el-select",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"db-filter fc-tag width100",attrs:{filterable:"",clearable:"","collapse-tags":"",placeholder:"Sites"},model:{value:_vm.cardDataObj.siteId,callback:function($$v){_vm.$set(_vm.cardDataObj,"siteId",$$v)},expression:"cardDataObj.siteId"}},[_vm._l(_vm.sites,(function(site,index){return _c("el-option",{key:index,attrs:{label:site.name,value:site.id}})})),_c("el-option",{key:-1,attrs:{label:"All Sites",value:-1}})],2)],1)],1),_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"floorId"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Floor")]),_c("el-select",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"db-filter fc-tag width100",attrs:{filterable:"",clearable:"","collapse-tags":"",placeholder:"Floor"},model:{value:_vm.cardDataObj.floorId,callback:function($$v){_vm.$set(_vm.cardDataObj,"floorId",$$v)},expression:"cardDataObj.floorId"}},[_vm._l(_vm.floors,(function(floor,index){return _c("el-option",{key:index,attrs:{label:floor.name,value:floor.id}})})),_c("el-option",{key:-1,attrs:{label:"All Floor",value:-1}})],2)],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"floorPlanId"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Floor Plan")]),_c("el-select",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"db-filter fc-tag width100",attrs:{filterable:"",clearable:"","collapse-tags":"",placeholder:"Floor Plan"},model:{value:_vm.cardDataObj.floorPlanId,callback:function($$v){_vm.$set(_vm.cardDataObj,"floorPlanId",$$v)},expression:"cardDataObj.floorPlanId"}},_vm._l(_vm.filteredFloorPLanList,(function(floorplan,index){return _c("el-option",{key:index,attrs:{label:floorplan.name,value:floorplan.id}})})),1)],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"viewMode"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Select mode")]),_c("el-select",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"db-filter fc-tag width100",attrs:{filterable:"","collapse-tags":"",placeholder:"Mode"},model:{value:_vm.cardDataObj.viewMode,callback:function($$v){_vm.$set(_vm.cardDataObj,"viewMode",$$v)},expression:"cardDataObj.viewMode"}},[_vm._l(_vm.viewModes,(function(view,index){return _c("el-option",{key:index,attrs:{label:view.label,value:view.value}})})),343===_vm.$account.org.id||155===_vm.$account.org.id?_c("el-option",{key:213,attrs:{label:"VAV readings",value:"vavreadings"}}):_vm._e(),410===_vm.$account.org.id?_c("el-option",{key:213,attrs:{label:"Custom script",value:"customscript"}}):_vm._e()],2)],1)],1)],1),"readings"===_vm.cardDataObj.viewMode?_c("el-row",{staticClass:"mB10",attrs:{gutter:20}},[_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"viewMode"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Asset Category")]),_c("el-select",{staticClass:" fc-input-full-border2 width100",attrs:{filterable:"",placeholder:"Select"},on:{change:function($event){return _vm.loadAssetFields()}},model:{value:_vm.cardDataObj.viewParams.assetCategoryId,callback:function($$v){_vm.$set(_vm.cardDataObj.viewParams,"assetCategoryId",$$v)},expression:"cardDataObj.viewParams.assetCategoryId"}},_vm._l(_vm.assetCategory,(function(category,index){return _c("el-option",{key:index,attrs:{label:category.name,value:category.id}})})),1)],1)],1),_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"viewMode"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Asset Field Name")]),_c("el-select",{staticClass:" fc-input-full-border2 width100",attrs:{filterable:"",placeholder:"Select"},on:{change:function($event){return _vm.setReadingFieldModuleName()}},model:{value:_vm.cardDataObj.viewParams.readingFieldName,callback:function($$v){_vm.$set(_vm.cardDataObj.viewParams,"readingFieldName",$$v)},expression:"cardDataObj.viewParams.readingFieldName"}},_vm._l(_vm.assetFields,(function(field,index){return _c("el-option",{key:index,attrs:{label:field.displayName,value:field.name}})})),1)],1)],1)],1):_vm._e()],1)],1),_c("div",{staticClass:"preview-panel section"},[_c("div",{staticClass:"card-wrapper"},[_c("Card",{attrs:{cardDataObj:_vm.cardDataObj,cardData:_vm.previewData,cardState:_vm.previewState,isLoading:_vm.isPreviewLoading}})],1)])]),_c("div",{staticClass:"d-flex mT-auto form-action-btn"},[_c("el-button",{staticClass:"form-btn f13 bold secondary text-center text-uppercase",on:{click:function($event){return _vm.onGoBack()}}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"form-btn f13 bold primary m0 text-center text-uppercase",attrs:{type:"primary"},on:{click:function($event){return _vm.save()}}},[_vm._v("Save")])],1)])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),BaseConfig=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(425728),__webpack_require__(434284),__webpack_require__(641384)),Configvue_type_script_lang_js={extends:BaseConfig/* default */.Z},base_Configvue_type_script_lang_js=Configvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Configvue_type_script_lang_js,Config_render,Config_staticRenderFns,!1,null,null,null)
/* harmony default export */,Config=component.exports,Card=__webpack_require__(60143),validation=__webpack_require__(990260),vuex_esm=__webpack_require__(420629),type1_Configvue_type_script_lang_js={name:"WebCard1",extends:Config,props:["isNew","cardType","onClose","onGoBack","onCardSave","onCardUpdate","closePopup"],components:{Card:Card/* default */.Z},created:function(){this.$store.dispatch("loadAssetCategory"),this.$store.dispatch("loadSites"),this.loadFloors(),this.getFloorPlanList()},mounted:function(){this.cardDataObj.viewParams&&this.cardDataObj.viewParams.assetCategoryId&&this.loadAssetFields(!0)},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({sites:function(state){return state.sites},assetCategory:function(state){return state.assetCategory}})),{},{filteredFloorPLanList:function(){var _this=this;return this.cardDataObj.siteId>0&&this.cardDataObj.floorId?this.floorPlanList.filter((function(floorPLan){if(floorPLan.siteId===_this.cardDataObj.siteId&&floorPLan.floorId===_this.cardDataObj.floorId)return floorPLan})):this.cardDataObj.siteId>0?this.floorPlanList.filter((function(floorPLan){if(floorPLan.siteId===_this.cardDataObj.siteId)return floorPLan})):this.floorPlanList},previewData:function(){return(0,validation/* isEmpty */.xb)(this.result)?{title:null,value:null}:this.result.data}}),data:function(){return{cardLayout:"floorplan_layout_1",isPreviewLoading:!1,floors:[],assetFields:[],resourceProps:["title","floorPlanId","siteId","floorId","viewMode","viewParams"],cardDataObj:{title:"",siteId:-1,floorPlanId:null,floorId:-1,viewMode:"default",viewParams:{assetCategoryId:null,readingFieldName:null,readingModule:null}},layout:{w:48,h:24},cardStateObj:{canResize:!0,styles:{hideHeader:!0}},result:null,floorPlanList:[],viewModes:[{label:"Default",value:"default"},{label:"Maintenance",value:"maintenance"},{label:"Control points",value:"control_points"},{label:"Readings",value:"readings"},{label:"Desk Manager",value:"employee"}]}},methods:{validateProperty:function(){var _this2=this;return{siteId:function(){return!1},floorId:function(){return!1},viewMode:function(){return!1},viewParams:function(data){return!("readings"!==data.viewMode||!data.viewParams)&&_this2.validateReadingModeFields(data.viewParams)},scriptModeInt:function(){return!1}}},validateReadingModeFields:function(viewParams){var assetCategoryId=viewParams.assetCategoryId,readingFieldName=viewParams.readingFieldName,readingModule=viewParams.readingModule,error=!1;return((0,validation/* isEmpty */.xb)(readingFieldName)||(0,validation/* isEmpty */.xb)(assetCategoryId)||(0,validation/* isEmpty */.xb)(readingModule))&&(error=!0),error},loadFloors:function(){var _this3=this;this.$http.get("/v2/module/data/list?moduleName=floor").then((function(_ref){var data=_ref.data;0===data.responseCode&&(_this3.floors=data.result.moduleDatas)}))},getFloorPlanList:function(){var _this4=this,self=this;self.$http.get("/v2/floorPlan/getall").then((function(_ref2){var data=_ref2.data;0===data.responseCode&&(_this4.floorPlanList=data.result.floorPlans)}))},setReadingFieldModuleName:function(){if(this.cardDataObj.viewParams.readingFieldName){var fieldname=this.cardDataObj.viewParams.readingFieldName,fieldobj=this.assetFields.find((function(rt){return rt.name===fieldname}));this.cardDataObj.viewParams.readingModule=fieldobj.module.name}},loadAssetFields:function(edit){var _this5=this;edit||(this.cardDataObj.viewParams.readingFieldName=null),this.$util.loadAssetReadingFields(-1,this.cardDataObj.viewParams.assetCategoryId).then((function(fields){_this5.assetFields=fields}))}}},floorPlan_type1_Configvue_type_script_lang_js=type1_Configvue_type_script_lang_js,Config_component=(0,componentNormalizer/* default */.Z)(floorPlan_type1_Configvue_type_script_lang_js,render,staticRenderFns,!1,null,"46df513f",null)
/* harmony default export */,type1_Config=Config_component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/91859.js.map