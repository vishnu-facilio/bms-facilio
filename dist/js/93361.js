"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[93361],{
/***/345659:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return UnitPicker}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/cards/common/UnitPicker.vue?vue&type=template&id=b69f3a16
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.unitConfig?_c("div",[_c("el-row",{},[_c("el-col",{attrs:{span:12}},[_c("el-form-item",{attrs:{prop:"unit"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Unit")]),_c("div",{staticClass:"d-flex  card-color-container fc-color-picker"},[_c("el-input",{staticClass:"width100 fc-input-full-border2",attrs:{clearable:""},model:{value:_vm.unitConfig.unit,callback:function($$v){_vm.$set(_vm.unitConfig,"unit",$$v)},expression:"unitConfig.unit"}})],1)])],1),_c("el-col",{attrs:{span:12}},[_c("el-form-item",{attrs:{prop:"position"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Position")]),_c("div",{staticClass:"d-flex  card-color-container fc-color-picker"},[_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.unitConfig.position,callback:function($$v){_vm.$set(_vm.unitConfig,"position",$$v)},expression:"unitConfig.position"}},[_vm._l(_vm.unitPosition,(function(value,key){return[_c("el-option",{key:key,attrs:{label:key,value:value}})]}))],2)],1)])],1)],1)],1):_vm._e()},staticRenderFns=[],card_constants=__webpack_require__(806186),UnitPickervue_type_script_lang_js={props:["unitConfig"],data:function(){return{unitPosition:card_constants/* unitPosition */.v}}},common_UnitPickervue_type_script_lang_js=UnitPickervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(common_UnitPickervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,UnitPicker=component.exports},
/***/293361:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return type1_Config}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/cards/pmReadings/type1/Config.vue?vue&type=template&id=3c7fa09d&scoped=true
var Config_render,Config_staticRenderFns,render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30"},[_c("div",{staticClass:"header cards-config-header "},[_c("span",{staticClass:"pointer",on:{click:_vm.onGoBack}},[_c("inline-svg",{staticClass:"vertical-top rotate-90 mR20",attrs:{src:"svgs/arrow",iconClass:"icon"}})],1),_vm._v(" "+_vm._s(_vm.cardMeta&&_vm.cardMeta.name||"PM Reading Card Layout")+" "),_c("span",{staticClass:"pointer",on:{click:_vm.onClose}},[_c("inline-svg",{staticClass:"vertical-middle fR",attrs:{src:"svgs/close",iconClass:"icon icon-sm"}})],1)]),_c("div",{staticClass:"container mT20"},[_c("div",{staticClass:"section config-panel panel-scroll"},[_c("el-form",{ref:this.cardLayout+"_form",attrs:{model:_vm.cardDataObj,rules:_vm.validationRules,"label-position":"top"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"title"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Title")]),_c("el-input",{staticClass:"width100 fc-input-full-border2",attrs:{autofocus:_vm.isNew},model:{value:_vm.cardDataObj.title,callback:function($$v){_vm.$set(_vm.cardDataObj,"title",$$v)},expression:"cardDataObj.title"}})],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"dateRange"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Preventive Maintenance")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},on:{change:_vm.loadResources},model:{value:_vm.cardDataObj.pmId,callback:function($$v){_vm.$set(_vm.cardDataObj,"pmId",$$v)},expression:"cardDataObj.pmId"}},[_vm._l(_vm.pmlist,(function(pm,index){return[_c("el-option",{key:index,attrs:{label:pm.name,value:pm.id}})]}))],2)],1)],1)],1),_vm.currentPM&&2===_vm.currentPM.pmCreationType?_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"dateRange"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v(" "+_vm._s(4===_vm.currentPM.assignmentType?"Asset":"Space")+" ")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.cardDataObj.resourceId,callback:function($$v){_vm.$set(_vm.cardDataObj,"resourceId",$$v)},expression:"cardDataObj.resourceId"}},[_vm._l(_vm.pmResourceList,(function(resource,index){return[_c("el-option",{key:index,attrs:{label:resource.name,value:resource.id}})]}))],2)],1)],1)],1):_vm._e(),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"dateRange"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Default Period")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.cardDataObj.dateRange,callback:function($$v){_vm.$set(_vm.cardDataObj,"dateRange",$$v)},expression:"cardDataObj.dateRange"}},[_vm._l(_vm.dateOperators,(function(dateRange,index){return[_c("el-option",{key:index,attrs:{label:dateRange.label,value:dateRange.value}})]}))],2)],1)],1)],1)],1)],1),_c("div",{staticClass:"preview-panel section"},[_c("div",{staticClass:"card-wrapper"},[_c("Card",{attrs:{cardDataObj:_vm.cardDataObj,cardData:_vm.previewData,cardState:_vm.previewState,isLoading:_vm.isPreviewLoading}})],1)])]),_c("div",{staticClass:"d-flex mT-auto form-action-btn"},[_c("el-button",{staticClass:"form-btn f13 bold secondary text-center text-uppercase",on:{click:function($event){return _vm.onGoBack()}}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"form-btn f13 bold primary m0 text-center text-uppercase",attrs:{type:"primary"},on:{click:function($event){return _vm.save()}}},[_vm._v("Save")])],1)])},staticRenderFns=[],Config=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(39428)),Configvue_type_script_lang_js={extends:Config["default"]},base_Configvue_type_script_lang_js=Configvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Configvue_type_script_lang_js,Config_render,Config_staticRenderFns,!1,null,null,null)
/* harmony default export */,base_Config=component.exports,Card=__webpack_require__(618966),type1_Configvue_type_script_lang_js={name:"PmReadingcCard1",extends:base_Config,components:{Card:Card/* default */.Z},data:function(){return{cardLayout:"pmreadings_layout_1",currentPM:null,pmlist:[],pmResourceList:[],resourceProps:["title","pmId","dateRange"],cardDataObj:{title:"",pmId:null,resourceId:null,dateRange:"Today"}}},mounted:function(){this.loadPmList()},methods:{loadResources:function(){var _this=this,cardDataObj=this.cardDataObj;this.pmResourceList=[],cardDataObj.resourceId=null;var pm=this.pmlist.find((function(rt){return rt.id===cardDataObj.pmId}));this.currentPM=pm,this.$http.get("/workorder/getMultiplePMResources?pmId=".concat(cardDataObj.pmId)).then((function(response){_this.pmResourceList=response.data.multiPmResources}))},loadPmList:function(){var self=this;this.$http.get("/planned/all").then((function(response){self.pmlist=response.data})).catch((function(){}))}}},pmReadings_type1_Configvue_type_script_lang_js=type1_Configvue_type_script_lang_js,Config_component=(0,componentNormalizer/* default */.Z)(pmReadings_type1_Configvue_type_script_lang_js,render,staticRenderFns,!1,null,"3c7fa09d",null)
/* harmony default export */,type1_Config=Config_component.exports},
/***/39428:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return type1_Config}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/cards/readingCard/type1/Config.vue?vue&type=template&id=3ce0d8ba&scoped=true
var Config_render,Config_staticRenderFns,render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30"},[_c("div",{staticClass:"header"},[_c("span",{staticClass:"pointer",on:{click:_vm.onGoBack}},[_c("inline-svg",{staticClass:"vertical-top rotate-90 mR20",attrs:{src:"svgs/arrow",iconClass:"icon"}})],1),_vm._v(" "+_vm._s(_vm.cardMeta&&_vm.cardMeta.name||"Reading Card Layout")+" "),_c("span",{staticClass:"pointer",on:{click:_vm.onClose}},[_c("inline-svg",{staticClass:"vertical-middle fR",attrs:{src:"svgs/close",iconClass:"icon icon-sm"}})],1)]),_c("div",{staticClass:"container mT20"},[_c("div",{staticClass:"section config-panel"},[_c("el-tabs",{staticClass:"card-tab-fixed",model:{value:_vm.activeTab,callback:function($$v){_vm.activeTab=$$v},expression:"activeTab"}},[_c("el-tab-pane",{attrs:{label:"Config",name:"config"}},[_c("el-form",{ref:this.cardLayout+"_form",attrs:{model:_vm.cardDataObj,rules:_vm.validationRules,"label-position":"top"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"title"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Title")]),_c("el-input",{staticClass:"width100 fc-input-full-border2",attrs:{autofocus:_vm.isNew},model:{value:_vm.cardDataObj.title,callback:function($$v){_vm.$set(_vm.cardDataObj,"title",$$v)},expression:"cardDataObj.title"}})],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"reading"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Readings")]),_c("ReadingPicker",{attrs:{options:_vm.readingPickerOptions,initialReading:_vm.cardDataObj.reading},on:{onReadingSelect:function(reading){return _vm.setReading("reading",reading)}}})],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"dateRange"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Period")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.cardDataObj.dateRange,callback:function($$v){_vm.$set(_vm.cardDataObj,"dateRange",$$v)},expression:"cardDataObj.dateRange"}},[_vm._l(_vm.dateOperators,(function(dateRange,index){return[_c("el-option",{key:index,attrs:{label:dateRange.label,value:dateRange.value}})]}))],2)],1)],1)],1)],1)],1),_c("el-tab-pane",{attrs:{label:"Styles",name:"styles"}},[_c("el-form",{attrs:{model:_vm.cardStateObj,"label-position":"top"}},[_c("el-row",{staticClass:"mB10 mT20"},[_c("el-col",{attrs:{span:8}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"primaryColor"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Primary Text")]),_c("div",{staticClass:"d-flex mT5 card-color-container fc-color-picker"},[_c("el-color-picker",{key:"primaryColor"+_vm.cardStateObj.styles.primaryColor,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},model:{value:_vm.cardStateObj.styles.primaryColor,callback:function($$v){_vm.$set(_vm.cardStateObj.styles,"primaryColor",$$v)},expression:"cardStateObj.styles.primaryColor"}})],1)])],1),_c("el-col",{attrs:{span:8}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"secondaryColor"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Secondary Text")]),_c("div",{staticClass:"d-flex mT5 card-color-container fc-color-picker"},[_c("el-color-picker",{key:"secondaryColor"+_vm.cardStateObj.styles.secondaryColor,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},model:{value:_vm.cardStateObj.styles.secondaryColor,callback:function($$v){_vm.$set(_vm.cardStateObj.styles,"secondaryColor",$$v)},expression:"cardStateObj.styles.secondaryColor"}})],1)])],1),_c("el-col",{attrs:{span:8}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"backgroundColor"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Background Color")]),_c("div",{staticClass:"d-flex mT5 card-color-container fc-color-picker"},[_c("el-color-picker",{key:"backgroundColor"+_vm.cardStateObj.styles.backgroundColor,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},model:{value:_vm.cardStateObj.styles.backgroundColor,callback:function($$v){_vm.$set(_vm.cardStateObj.styles,"backgroundColor",$$v)},expression:"cardStateObj.styles.backgroundColor"}})],1)])],1)],1),_c("el-row",{staticClass:"mB10 mT20"},[_c("el-col",{attrs:{span:8}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"backgroundColor"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Decimal Place")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.cardStateObj.styles.decimalPlace,callback:function($$v){_vm.$set(_vm.cardStateObj.styles,"decimalPlace",$$v)},expression:"cardStateObj.styles.decimalPlace"}},[_vm._l(_vm.decimalPlaces,(function(value,key){return[_c("el-option",{key:key,attrs:{label:key,value:value}})]}))],2)],1)],1),_c("el-col",{attrs:{span:16}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"backgroundColor"}},[_c("unit-picker",{staticClass:"pL10",attrs:{unitConfig:_vm.cardStateObj.styles.unitConfig}})],1)],1)],1),_c("el-row",{staticClass:"mB10 mT20"},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB10"},[_vm._v("Card Image")]),_c("ImageUpload",{model:{value:_vm.cardDataObj["imageId"],callback:function($$v){_vm.$set(_vm.cardDataObj,"imageId",$$v)},expression:"cardDataObj['imageId']"}})],1)],1)],1)],1),_c("el-tab-pane",{attrs:{label:"Actions",name:"actions"}},[_c("ActionPicker",{ref:"action-picker",attrs:{elements:[{name:"default",displayName:"Default"}],variables:_vm.variables},model:{value:_vm.cardActions,callback:function($$v){_vm.cardActions=$$v},expression:"cardActions"}},[_c("template",{slot:"element-title"},[_c("div",{staticClass:"pT15"})])],2)],1),_c("el-tab-pane",{attrs:{label:"FORMATTING",name:"conditionalformatting"}},[_c("div",{staticClass:"card-builder-criteria-block"},[_c("ConditionalFormating",{ref:"conditional-formatting",attrs:{variables:_vm.conditionalVariables,cardData:_vm.previewData,cardStyles:_vm.cardStateObj.styles},model:{value:_vm.conditionalFormatting,callback:function($$v){_vm.conditionalFormatting=$$v},expression:"conditionalFormatting"}})],1)]),_vm.nameSpaces.length?_c("el-tab-pane",{attrs:{label:"CUSTOM SCRIPT"}},[_c("div",{staticClass:"p10"},[_c("el-checkbox",{on:{change:function($event){return _vm.changescriptModeInt()}},model:{value:_vm.enableBoxchecked,callback:function($$v){_vm.enableBoxchecked=$$v},expression:"enableBoxchecked"}},[_vm._v("Enable Custom Script")]),1==_vm.enableBoxchecked?_c("div",{staticClass:"mT30"},[_vm.nameSpaces.length?_c("div",[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v(" Select Function ")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please Select a Script"},model:{value:_vm.customScriptId,callback:function($$v){_vm.customScriptId=$$v},expression:"customScriptId"}},_vm._l(_vm.nameSpaces,(function(nameSpace,index){return _c("el-option-group",{key:index,attrs:{label:nameSpace.name}},_vm._l(nameSpace.functions,(function(func,index){return _c("el-option",{key:index,attrs:{label:func.name,value:func.id}})})),1)})),1)],1):_c("div",{staticClass:"fc-input-label-txt mT30"},[_vm._v(" No functions available. Kindly add some functions and check. ")])]):_vm._e()],1)]):_vm._e()],1)],1),_c("div",{staticClass:"preview-panel section"},[_c("div",{staticClass:"card-wrapper",style:_vm.cardWrapper},[_c("Card",{attrs:{cardData:_vm.previewData,cardState:_vm.previewState,isLoading:_vm.isPreviewLoading}})],1)])]),_c("div",{staticClass:"d-flex mT-auto form-action-btn"},[_c("el-button",{staticClass:"form-btn f13 bold secondary text-center text-uppercase",on:{click:function($event){return _vm.onGoBack()}}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"form-btn f13 bold primary m0 text-center text-uppercase",attrs:{type:"primary"},on:{click:function($event){return _vm.save()}}},[_vm._v("Save")])],1)])},staticRenderFns=[],defineProperty=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(976801),__webpack_require__(482482)),objectSpread2=__webpack_require__(595082),BaseConfig=__webpack_require__(641384),validation=__webpack_require__(990260),UnitPicker=__webpack_require__(345659),Configvue_type_script_lang_js={extends:BaseConfig/* default */.Z,components:{UnitPicker:UnitPicker/* default */.Z},methods:{setReading:function(prop,reading){if(reading){var cardDataObj=this.cardDataObj;this.$set(this,"cardDataObj",(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},cardDataObj),{},(0,defineProperty/* default */.Z)({},prop,reading))),this.setConditionalVariables(reading)}},setTitleFromReading:function(reading){var cardDataObj=this.cardDataObj,cardName=this.cardDataObj.title;cardDataObj.title=(0,validation/* isEmpty */.xb)(cardName)?reading.readingName:cardName}},mounted:function(){this.setProps(),this.loadNameSpaceList()}},base_Configvue_type_script_lang_js=Configvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Configvue_type_script_lang_js,Config_render,Config_staticRenderFns,!1,null,null,null)
/* harmony default export */,Config=component.exports,Card=__webpack_require__(562946),DateHelper=__webpack_require__(281230),card_constants=__webpack_require__(806186),ReadingPicker=__webpack_require__(700697),ActionPicker=__webpack_require__(624817),CardConditionalFormating=__webpack_require__(962424),ImageUpload=__webpack_require__(924743),type1_Configvue_type_script_lang_js={name:"ReadingCard1",extends:Config,props:["isNew","cardType","onClose","onGoBack","onCardSave","onCardUpdate","closePopup"],mixins:[DateHelper/* default */.Z],components:{Card:Card/* default */.Z,ReadingPicker:ReadingPicker/* default */.Z,ActionPicker:ActionPicker/* default */.Z,ConditionalFormating:CardConditionalFormating/* default */.Z,ImageUpload:ImageUpload/* default */.Z},data:function(){return{cardLayout:"readingcard_layout_1",activeTab:"config",isPreviewLoading:!1,resourceProps:["title",{prop:"reading",resourceProps:["moduleName","parentId","fieldName","fieldId","dataType","yAggr","parentType","parentName"]},"dateRange","imageId"],conditionalVariables:[{name:"title",dataType:"STRING",displayName:"Title"},{name:"value",dataType:"NUMBER",displayName:"Value"}],cardDataObj:{title:"",operatorId:null,reading:{yAggr:"avg"},dateRange:"Today",imageId:null},cardStateObj:{canResize:!0,styles:{primaryColor:"#110d24",secondaryColor:"#969caf",backgroundColor:"#FFF",decimalPlace:-1,unitConfig:{unit:null,position:2}}},cardActions:{default:{actionType:"showTrend"}},result:null,validationRules:{},readingPickerOptions:{parentId:{type:"single"},fieldName:{type:"single"},fieldId:{type:"single"},dataType:{type:"single"},moduleName:{type:"single"},parentName:{type:"single"},parentType:{type:"single"},yAggr:{type:"single"},selectedFieldId:{type:"single"},kpiType:{type:"single"}},predefinedColors:card_constants/* predefinedColors */.UH,dateOperators:card_constants/* dateOperators */._M,aggregateFunctions:card_constants/* aggregateFunctions */.rD}},mounted:function(){var decimalPlace=this.$getProperty(this,"cardStateObj.styles.decimalPlace",null);"Auto"===decimalPlace&&(this.cardStateObj.styles.decimalPlace=-1)},computed:{previewData:function(){return(0,validation/* isEmpty */.xb)(this.result)?{title:null,value:{},period:null,unit:null}:this.result.data},layout:function(){var imageId=this.cardDataObj.imageId;return{w:(0,validation/* isEmpty */.xb)(imageId)?24:26,h:16}},cardWrapper:function(){var imageId=this.cardDataObj.imageId;return{width:(0,validation/* isEmpty */.xb)(imageId)?"220px":"300px"}}},methods:{validateProperty:function(){return{reading:function(data){var readingProp=this.resourceProps.find((function(prop){return(0,validation/* isObject */.Kn)(prop)?"reading"===prop.prop:"reading"===prop}));if((0,validation/* isObject */.Kn)(readingProp)){var resourceProps=readingProp.resourceProps,nullableFields=["fieldId","dataType"],hasEmptyField=!1;return resourceProps.forEach((function(prop){!nullableFields.includes(prop)&&(0,validation/* isEmpty */.xb)(data["reading"][prop])&&(hasEmptyField=!0)})),hasEmptyField}return(0,validation/* isEmpty */.xb)(data["reading"])}.bind(this),imageId:function(){return!1},baseline:function(){return!1}}}}},readingCard_type1_Configvue_type_script_lang_js=type1_Configvue_type_script_lang_js,Config_component=(0,componentNormalizer/* default */.Z)(readingCard_type1_Configvue_type_script_lang_js,render,staticRenderFns,!1,null,"3ce0d8ba",null)
/* harmony default export */,type1_Config=Config_component.exports},
/***/962424:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return CardConditionalFormating}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/components/CardConditionalFormating.vue?vue&type=template&id=1088c554
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.loading?_vm._e():_c("div",{staticClass:"conditional-formatting p10"},[_c("el-collapse",{staticClass:"new-rule-collapse position-relative controllogic-collapse",attrs:{accordion:!0},model:{value:_vm.activeNames,callback:function($$v){_vm.activeNames=$$v},expression:"activeNames"}},_vm._l(_vm.conditionalFormatting,(function(conditional,index){return _c("el-collapse-item",{key:index,staticClass:"rule-border-blue mT20 position-relative",staticStyle:{"border-left":"1px solid rgb(228, 235, 241)"},attrs:{name:index}},[_c("template",{slot:"title"},[_vm._v(" Conditional Rule "+_vm._s(index+1)+" ")]),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"conditional-formatting"},[_c("new-criteria-builder",{staticClass:"graphics-criteria-builder",attrs:{title:"Specify conditions",exrule:conditional.criteria,index:index,showSiteField:!0,module:"cardbuilder",variables:_vm.filteredVariables},on:{condition:_vm.getCriteria}}),_c("div",{staticClass:"style-container pB20"},[_c("el-row",[_c("label",{staticClass:"fc-modal-sub-title",attrs:{for:"name"}},[_vm._v("Formatting")]),_c("div",{staticClass:"fc-sub-title-desc"},[_vm._v(" Specify styles for the conditions ")])]),_c("el-row",{staticClass:"mT10",attrs:{gutter:10}},[conditional.styles.hasOwnProperty("primaryColor")?_c("el-col",{attrs:{span:7}},[_c("div",{staticClass:"fc-input-label-txt mb5"},[_vm._v("Primary")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},on:{change:_vm.emitData},model:{value:conditional.styles.primaryColor,callback:function($$v){_vm.$set(conditional.styles,"primaryColor",$$v)},expression:"conditional.styles.primaryColor"}})],1)]):_vm._e(),conditional.styles.hasOwnProperty("secondaryColor")?_c("el-col",{attrs:{span:7}},[_c("div",{staticClass:"fc-input-label-txt mb5"},[_vm._v("Secondary")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},on:{change:_vm.emitData},model:{value:conditional.styles.secondaryColor,callback:function($$v){_vm.$set(conditional.styles,"secondaryColor",$$v)},expression:"conditional.styles.secondaryColor"}})],1)]):_vm._e(),conditional.styles.hasOwnProperty("arrowUpColor")?_c("el-col",{attrs:{span:7}},[_c("div",{staticClass:"fc-input-label-txt mb5"},[_vm._v("Arrow Up color")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},on:{change:_vm.emitData},model:{value:conditional.styles.arrowUpColor,callback:function($$v){_vm.$set(conditional.styles,"arrowUpColor",$$v)},expression:"conditional.styles.arrowUpColor"}})],1)]):_vm._e(),conditional.styles.hasOwnProperty("arrowDownColor")?_c("el-col",{attrs:{span:7}},[_c("div",{staticClass:"fc-input-label-txt mb5"},[_vm._v("Arrow Down color")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},on:{change:_vm.emitData},model:{value:conditional.styles.arrowDownColor,callback:function($$v){_vm.$set(conditional.styles,"arrowDownColor",$$v)},expression:"conditional.styles.arrowDownColor"}})],1)]):_vm._e(),conditional.styles.hasOwnProperty("backgroundColor")?_c("el-col",{attrs:{span:7}},[_c("div",{staticClass:"fc-input-label-txt mb5"},[_vm._v("BG")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},on:{change:_vm.emitData},model:{value:conditional.styles.backgroundColor,callback:function($$v){_vm.$set(conditional.styles,"backgroundColor",$$v)},expression:"conditional.styles.backgroundColor"}})],1)]):_vm._e(),conditional.styles.hasOwnProperty("colors")&&["gauge_layout_1","gauge_layout_3"].includes(_vm.cardLayout)?_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Gauge Colors")]),_c("div",{staticClass:"mT5 card-color-container"},_vm._l(conditional.styles.colors,(function(color,index){return _c("div",{key:index,staticClass:"mR10 fc-color-picker card-color-container"},[_c("el-color-picker",{key:""+color.id+color.hex,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},on:{change:_vm.emitData},model:{value:color.hex,callback:function($$v){_vm.$set(color,"hex",$$v)},expression:"color.hex"}})],1)})),0)]):_vm._e(),conditional.styles.hasOwnProperty("color")&&["gauge_layout_2","gauge_layout_4"].includes(_vm.cardLayout)?_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Gauge Color")]),_c("div",{staticClass:"mT5"},[_c("div",{staticClass:"mR10 fc-color-picker card-color-container"},[_c("el-color-picker",{key:""+conditional.styles.color.id+conditional.styles.color.hex,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},on:{change:_vm.emitData},model:{value:conditional.styles.color.hex,callback:function($$v){_vm.$set(conditional.styles.color,"hex",$$v)},expression:"conditional.styles.color.hex"}})],1)])]):_vm._e(),conditional.styles.hasOwnProperty("tickColor")&&["gauge_layout_3","gauge_layout_4"].includes(_vm.cardLayout)?_c("el-col",{staticClass:"mB50",attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Tick Color")]),_c("div",{staticClass:"mT5 card-color-container"},[_c("div",{staticClass:"mR10 fc-color-picker card-color-container"},[_c("el-color-picker",{key:""+conditional.styles.tickColor.id+conditional.styles.tickColor.hex,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},on:{change:_vm.emitData},model:{value:conditional.styles.tickColor.hex,callback:function($$v){_vm.$set(conditional.styles.tickColor,"hex",$$v)},expression:"conditional.styles.tickColor.hex"}})],1)])]):_vm._e(),conditional.styles.hasOwnProperty("colorBarProgress")?_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Bar start Color")]),_c("div",{staticClass:"d-flex mT5 card-color-container"},[_c("div",{staticClass:"mR10 fc-color-picker card-color-container"},[_c("el-color-picker",{key:""+conditional.styles.colorBarProgress,attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},on:{change:_vm.emitData},model:{value:conditional.styles.colorBarProgress,callback:function($$v){_vm.$set(conditional.styles,"colorBarProgress",$$v)},expression:"conditional.styles.colorBarProgress"}})],1)])]):_vm._e(),conditional.styles.hasOwnProperty("colorBarProgressEnd")?_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Bar end color")]),_c("div",{staticClass:"d-flex mT5 card-color-container"},[_c("div",{staticClass:"mR10 fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.predefinedColors,size:"small","popper-class":"chart-custom-color-picker"},on:{change:_vm.emitData},model:{value:conditional.styles.colorBarProgressEnd,callback:function($$v){_vm.$set(conditional.styles,"colorBarProgressEnd",$$v)},expression:"conditional.styles.colorBarProgressEnd"}})],1)])]):_vm._e(),conditional.styles.hasOwnProperty("blink")?_c("el-col",{attrs:{span:12}},[_c("div",{staticClass:"mT40"},[_c("el-checkbox",{on:{change:_vm.emitData},model:{value:conditional.styles.blink,callback:function($$v){_vm.$set(conditional.styles,"blink",$$v)},expression:"conditional.styles.blink"}},[_vm._v("Blink")])],1)]):_vm._e(),conditional.styles.hasOwnProperty("displayValue")?_c("el-col",{attrs:{span:12}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Display value")]),_c("div",[_c("el-input",{staticClass:"fc-border-select",model:{value:conditional.styles.displayValue,callback:function($$v){_vm.$set(conditional.styles,"displayValue",$$v)},expression:"conditional.styles.displayValue"}})],1)]):_vm._e()],1)],1)],1),_vm.conditionalFormatting.length>1?_c("img",{staticClass:"delete-icon pointer",staticStyle:{height:"18px",width:"18px","margin-right":"3px"},attrs:{src:__webpack_require__(385769)},on:{click:function($event){return _vm.deleteRule(index)}}}):_vm._e()])],2)})),1),_c("el-button",{staticClass:"fc-btn-green-medium-fill mT20 f14 fwBold",attrs:{type:"primary",icon:"el-icon-plus",circle:""},on:{click:_vm.addRule}})],1)},staticRenderFns=[],NewCriteriaBuilder=(__webpack_require__(538077),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(947522),__webpack_require__(162506),__webpack_require__(169358),__webpack_require__(670560),__webpack_require__(859850)),colors=__webpack_require__(519898),card_constants=__webpack_require__(806186),validation=__webpack_require__(990260),CardConditionalFormatingvue_type_script_lang_js={props:["value","cardStyles","variables","cardLayout"],components:{NewCriteriaBuilder:NewCriteriaBuilder/* default */.Z},data:function(){return{loading:!1,predefinedColors:card_constants/* predefinedColors */.UH,activeNames:["0"],conditionalFormatting:[],styles:{primaryColor:"#110d24",secondaryColor:"#969caf",backgroundColor:"#FFF",tickColor:{id:0,hex:"#969aa2"},color:{id:0,hex:"#FF728E"},colors:[{id:0,hex:"#ff7878"},{id:1,hex:"#7d49ff"},{id:2,hex:"#514dff"},{id:3,hex:"#1eb9b7"}],backgroundColors:[{id:0,hex:"#fff"},{id:0,hex:"#fff"}],colorBarProgress:"#3adaad",colorBarProgressEnd:"#0232ab",arrowUpColor:"#008000",arrowDownColor:"#ff0100",blink:!1,displayValue:null}}},mounted:function(){this.initConditionalFormatting()},watch:{currentObject:function(){this.initConditionalFormatting()}},computed:{filteredVariables:function(){return this.variables.filter((function(rt){return"title"!==rt.name}))}},methods:{rerender:function(){var _this=this;this.loading=!0,setTimeout((function(){_this.loading=!1,_this.cleardata()}),500)},cleardata:function(){this.conditionalFormatting=[],this.addRule(),this.emitData()},initConditionalFormatting:function(){if((0,validation/* isEmpty */.xb)(this.$getProperty(this,"value.conditionalFormatting",null)))this.conditionalFormatting=[],this.addRule();else{var value=this.value;this.conditionalFormatting=this.getExtraStyles(value.conditionalFormatting)}},getExtraStyles:function(conditionalFormatting){var _this2=this;return conditionalFormatting.forEach((function(rt){var styles=rt.styles;styles.hasOwnProperty("displayValue")||_this2.$set(styles,"displayValue",null),styles.hasOwnProperty("blink")||_this2.$set(styles,"blink",!1)})),conditionalFormatting},getCriteria:function(criteria,index){this.conditionalFormatting[index].criteria=criteria},getPredefinedColors:function(){return colors/* default */.Z.readingcardColors},deleteRule:function(key){this.conditionalFormatting.splice(key,1)},getEmptyCFObject:function(){var _this3=this,cf={criteria:{pattern:""},styles:{}};return Object.keys(this.cardStyles).forEach((function(rt){_this3.$set(cf.styles,rt,_this3.styles[rt])})),cf.styles=this.setAdditionalStats(cf.styles),cf},setAdditionalStats:function(styles){return this.$set(styles,"blink",!1),this.$set(styles,"displayValue",null),styles},addRule:function(){this.conditionalFormatting.push(this.getEmptyCFObject())},emitData:function(){var conditionalFormatting={conditionalFormatting:this.conditionalFormatting};this.$emit("input",conditionalFormatting)}}},components_CardConditionalFormatingvue_type_script_lang_js=CardConditionalFormatingvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_CardConditionalFormatingvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,CardConditionalFormating=component.exports},
/***/924743:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ImageUpload}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/components/ImageUpload.vue?vue&type=template&id=da46c074
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.$validation.isEmpty(_vm.value)?_c("el-upload",{staticClass:"card-img-upload mR40",attrs:{drag:"",action:"",accept:"image/*","show-file-list":!1,"http-request":_vm.onUpload,"on-remove":_vm.onRemove,"on-success":_vm.onUploadSuccess}},[_c("div",{staticClass:"d-flex flex-direction-column"},[_c("InlineSvg",{staticClass:"d-flex justify-content-center folder-icon",attrs:{src:"svgs/file-image-solid",iconClass:"icon icon-xlg"}}),_c("div",{staticClass:"self-center"},[_c("p",{staticClass:"help-text"},[_vm._v(" "+_vm._s(_vm.$t("common.attachment_form.drag_drop_text"))+" "+_vm._s(_vm.$t("common.attachment_form.click_to_browse"))+" ")])])],1)]):_c("div",{staticClass:"inline line-height24"},[_c("el-image",{staticClass:"fc-avatar-square card-image-preview",attrs:{src:_vm.previewUrl,fit:"cover"}},[_c("div",{staticClass:"image-slot",attrs:{slot:"error"},slot:"error"},[_c("InlineSvg",{attrs:{src:"svgs/photo",iconClass:"icon fill-grey icon-xxlll op5"}})],1)]),_c("a",{staticClass:"f12 block text-center",on:{click:_vm.onRemove}},[_vm._v("Remove")])],1)],1)},staticRenderFns=[],slicedToArray=__webpack_require__(554621),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),api=__webpack_require__(32284),baseUrl=__webpack_require__(420499),ImageUploadvue_type_script_lang_js={props:["value"],data:function(){return{uploding:!1}},computed:{previewUrl:function(){var value=this.value;return(0,baseUrl/* getBaseURL */.BS)()+"/v2/files/preview/"+value}},methods:{onUpload:function(_ref){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var file;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return file=_ref.file,_ref.onError,_this.uploding=!0,_context.abrupt("return",api/* API */.bl.uploadFiles([file]));case 3:case"end":return _context.stop()}}),_callee)})))()},onUploadSuccess:function(response){var ids=response.ids,_ids=(response.error,(0,slicedToArray/* default */.Z)(ids,1)),id=_ids[0];this.$emit("input",id),this.uploding=!1},onRemove:function(){
// TODO delete file
this.$emit("input",null)}}},components_ImageUploadvue_type_script_lang_js=ImageUploadvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ImageUploadvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ImageUpload=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/93361.js.map