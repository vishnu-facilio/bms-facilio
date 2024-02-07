"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[16843],{
/***/856778:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FColorPalettes}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/newcharts/components/FColorPalettes.vue?vue&type=template&id=253b7996
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm._m(0),_c("div",{staticClass:"flex-middle"},[_c("div",{staticClass:"colorPalette-main",on:{click:_vm.openDialog}},_vm._l(_vm.Colors[_vm.chosenColors],(function(color,idx){return _c("div",{key:idx,staticClass:"colorBlock",style:{background:color}})})),0),_c("div",{staticClass:"flex-middle mL30 pB10 pointer",on:{click:_vm.openDialog}},[_c("div",{staticClass:"fc-link2 f11"},[_vm._v("Change")]),_c("InlineSvg",{staticClass:"vertical-middle pointer color-pallete",staticStyle:{top:"2px"},attrs:{src:"svgs/painter-palette",iconClass:"icon icon-xs mL5"}})],1)]),_c("el-dialog",{staticClass:"fc-dialog-center-container color-pallete-lock",attrs:{title:"Colour palettes",width:"35%",visible:_vm.showColorPalette,"append-to-body":!0,"lock-scroll":!0},on:{"update:visible":function($event){_vm.showColorPalette=$event}}},[_c("div",{staticStyle:{height:"580px"}},[_c("el-row",{attrs:{gutter:20}},_vm._l(_vm.Colors,(function(colors,index){return _c("el-col",{key:index,staticClass:"dialogcolorpalette mB10",attrs:{span:12}},[_vm.loading?_c("div",{staticClass:"color-palette-loading-con mB10"},[_c("div",{staticClass:"fc-animated-background width100px height20"}),_c("div",{staticClass:"width100 height20 mT10 fc-animated-background"})]):_c("div",[_c("div",{staticClass:"fc-input-label-txt f13 flex-middle position-relative"},[_vm._v(" "+_vm._s(_vm.formaterIndex(index))+" "),_vm.chosenColors===index?_c("div",{staticClass:"selected-icon-color position-absolute"},[_c("InlineSvg",{staticClass:"vertical-middle",attrs:{src:"svgs/check-icon",iconClass:"icon icon-xs mL10"}})],1):_vm._e()]),_c("div",{staticClass:"colorPalette position-relative",on:{click:function($event){return _vm.changeColor(index)}}},_vm._l(colors,(function(color,idx){return _c("div",{key:idx,staticClass:"colorBlock",style:{background:color}})})),0)])])})),1)],1)])],1)},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"flex-middle"},[_c("p",{staticClass:"label-txt-black pT20 line-height20 mB10"},[_vm._v("Color Palette")])])}],InlineSvg=__webpack_require__(586166),FColorPalettesvue_type_script_lang_js={props:["colorIndex"],data:function(){return{Colors:{1:["#1d7f01","#6cb302","#e9f501","#fda504","#fb5905"],2:["#93d5ed","#45a5f5","#4285f4","#2f5ec4","#073a93"],3:["#04b9bd","#007e80","#004852","#f63700","#fb6900"],4:["#ffaf00","#ff7300","#ff0057","#500083","#0b0411"],5:["#b0f400","#74c700","#00696d","#002e5b","#2e004d"],6:["#00fff1","#00c0d0","#4c60a6","#4d177c","#4a0052"],7:["#00effc","#00b9e7","#357ac9","#793fad","#980084"],8:["#c5ff00","#fef800","#00daff","#0050ff","#1700ff"],9:["#39678a","#27916e","#becd70","#d7e1b2","#9db4a4"],10:["#eeebff","#c0b8dd","#9184ba","#635198","#341e75"],11:["#e1f6de","#a9d0b2","#71a986","#38835a","#005c2e"],12:["#ffed95","#f0b277","#e0775a","#d13b3c","#c1001e"],13:["#eaf9a8","#b1c5a0","#779299","#3d5e91","#042a89"],14:["#ffc300","#eead09","#de9812","#cd821b","#bc6c24"],15:["#c5dcfb","#6fe8f2","#ecf365","#f89c3e","#f82422"]},chosenColors:1,showColorPalette:!1,loading:!0,updateTimeout:null}},mounted:function(){this.Colors[this.colorIndex]&&(this.chosenColors=this.colorIndex)},watch:{colorIndex:{handler:function(){this.chosenColors!==this.colorIndex&&(this.chosenColors=this.colorIndex,this.$emit("colorSelected",this.chosenColors,this.Colors[this.chosenColors]))}}},components:{InlineSvg:InlineSvg/* default */.Z},methods:{openDialog:function(){var _this=this;this.showColorPalette=!0,this.loading=!0,this.updateTimeout&&clearTimeout(this.updateTimeout),this.updateTimeout=setTimeout((function(){_this.loading=!1}),500)},changeColor:function(index){this.chosenColors=index,this.showColorPalette=!1,this.$emit("colorSelected",this.chosenColors,this.Colors[this.chosenColors])},formaterIndex:function(index){return"1"===index?"Default":"Palette "+index}}},components_FColorPalettesvue_type_script_lang_js=FColorPalettesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FColorPalettesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FColorPalettes=component.exports},
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
/***/216843:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Config}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/card-builder/cards/mapCard/base/Config.vue?vue&type=template&id=67232db6&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p30 fc-card-builder-page-animation"},[_c("div",{staticClass:"header"},[_c("span",{staticClass:"pointer",on:{click:_vm.onGoBack}},[_c("inline-svg",{staticClass:"vertical-top rotate-90 mR20",attrs:{src:"svgs/arrow",iconClass:"icon"}})],1),_vm._v(" "+_vm._s(_vm.cardMeta&&_vm.cardMeta.name||"Map Layout")+" "),_c("span",{staticClass:"pointer",on:{click:_vm.onClose}},[_c("inline-svg",{staticClass:"vertical-middle fR",attrs:{src:"svgs/close",iconClass:"icon icon-sm"}})],1)]),_c("div",{staticClass:"container mT20"},[_c("div",{staticClass:"section"},[_c("el-form",{ref:this.cardLayout+"_form",attrs:{model:_vm.cardDataObj,rules:_vm.validationRules,"label-position":"top"}},[_c("el-tabs",{staticClass:"card-tab-fixed",model:{value:_vm.activeTab,callback:function($$v){_vm.activeTab=$$v},expression:"activeTab"}},[_c("el-tab-pane",{attrs:{label:"Config",name:"config"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"title"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Title")]),_c("el-input",{staticClass:"width100 pR20 fc-input-full-border2",attrs:{autofocus:_vm.isNew},model:{value:_vm.cardDataObj.title,callback:function($$v){_vm.$set(_vm.cardDataObj,"title",$$v)},expression:"cardDataObj.title"}})],1)],1)],1),_c("el-row",{staticClass:"mB10",attrs:{gutter:20}},[_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"module"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Location Module")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a building"},on:{change:function($event){return _vm.selectModuleAction()}},model:{value:_vm.cardDataObj.moduleName,callback:function($$v){_vm.$set(_vm.cardDataObj,"moduleName",$$v)},expression:"cardDataObj.moduleName"}},_vm._l(_vm.locationModules,(function(value,key){return _c("el-option",{key:key,attrs:{label:key,value:value}})})),1)],1)],1),_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"marker"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Marker type")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{placeholder:"Please select a building"},model:{value:_vm.cardDataObj.marker.type,callback:function($$v){_vm.$set(_vm.cardDataObj.marker,"type",$$v)},expression:"cardDataObj.marker.type"}},[_vm._l(_vm.markerValues,(function(value,key){return _c("el-option",{key:key,attrs:{label:key,value:value}})})),"asset"===_vm.cardDataObj.moduleName?_c("el-option",{attrs:{label:"Reading",value:"reading"}}):_vm._e()],2)],1)],1)],1),"reading"===_vm.cardDataObj.marker.type?[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"marker"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Asset Category")]),_c("el-select",{staticClass:"width100 pR20 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},on:{change:function($event){_vm.cardDataObj.marker.reading.fieldName=null}},model:{value:_vm.cardDataObj.assetCategory,callback:function($$v){_vm.$set(_vm.cardDataObj,"assetCategory",$$v)},expression:"cardDataObj.assetCategory"}},_vm._l(_vm.assetCategory,(function(category,index){return _c("el-option",{key:index,attrs:{label:category.name,value:category.id}})})),1)],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"marker"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Asset Fields")]),_c("el-select",{staticClass:"width100 pR20 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},on:{change:function($event){return _vm.setReadingModuleName(_vm.cardDataObj.marker.reading.fieldName)}},model:{value:_vm.cardDataObj.marker.reading.fieldName,callback:function($$v){_vm.$set(_vm.cardDataObj.marker.reading,"fieldName",$$v)},expression:"cardDataObj.marker.reading.fieldName"}},_vm._l(_vm.readings.categoryWithFields[this.cardDataObj.assetCategory]||{},(function(value,key){return _c("el-option",{key:key,attrs:{label:_vm.fields[key].displayName,value:_vm.fields[key].name}})})),1)],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"yAggr"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Aggregation")]),_c("el-select",{staticClass:"width100 pR20 el-input-textbox-full-border",attrs:{placeholder:"Please select an aggregation"},model:{value:_vm.cardDataObj.marker.reading.yAggr,callback:function($$v){_vm.$set(_vm.cardDataObj.marker.reading,"yAggr",$$v)},expression:"cardDataObj.marker.reading.yAggr"}},_vm._l(_vm.aggregateFunctions,(function(fn,index){return _c("el-option",{key:index,attrs:{label:fn.label,value:fn.value}})})),1)],1)],1)],1),_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:24}},[_c("el-form-item",{staticClass:"mB10",attrs:{prop:"dateRange"}},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Period")]),_c("el-select",{staticClass:"width100 pR20 el-input-textbox-full-border",attrs:{placeholder:"Please select a period"},model:{value:_vm.cardDataObj.dateRange,callback:function($$v){_vm.$set(_vm.cardDataObj,"dateRange",$$v)},expression:"cardDataObj.dateRange"}},[_vm._l(_vm.dateOperators,(function(dateRange,index){return[_c("el-option",{key:index,attrs:{label:dateRange.label,value:dateRange.value}})]}))],2)],1)],1)],1)]:_vm._e()],2),_c("el-tab-pane",{attrs:{label:"Styles",name:"styles"}},[_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:12}},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Map Theme")]),_c("el-select",{staticClass:"width100 pR20 el-input-textbox-full-border",attrs:{placeholder:"Please select a building"},model:{value:_vm.cardStateObj.styles.theme,callback:function($$v){_vm.$set(_vm.cardStateObj.styles,"theme",$$v)},expression:"cardStateObj.styles.theme"}},[_vm._l(_vm.mapTheams,(function(theme,idx){return _c("el-option",{key:idx,attrs:{value:theme.value,label:theme.displayName}})})),_c("el-option",{attrs:{value:null,label:"Default"}})],2)],1)],1),_c("el-col",{attrs:{span:6}},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Icon")]),_c("el-select",{staticClass:"fc-input-full-border2 icon-picker mR15",attrs:{filterable:!0,"default-first-option":!0,"value-key":"name"},model:{value:_vm.cardDataObj.marker.icon,callback:function($$v){_vm.$set(_vm.cardDataObj.marker,"icon",$$v)},expression:"cardDataObj.marker.icon"}},[_c("inline-svg",{staticClass:"mR10",attrs:{slot:"prefix",src:_vm.mapIcons.find((function(rt){return rt.value===_vm.cardDataObj.marker.icon})).path,iconClass:"icon icon-sm-md"},slot:"prefix"}),_vm._l(_vm.mapIcons,(function(icon,idx){return _c("el-option",{key:idx+icon.name,attrs:{value:icon.value,label:icon.name}},[_c("inline-svg",{staticClass:"vertical-middle",attrs:{src:icon.path,iconClass:"icon"}})],1)}))],2)],1)],1),_c("el-col",{attrs:{span:6}},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Icon color")]),_c("div",{staticClass:"fc-color-picker card-color-container"},[_c("el-color-picker",{attrs:{predefine:_vm.getPredefinedColors()},model:{value:_vm.cardStateObj.marker.styles.color,callback:function($$v){_vm.$set(_vm.cardStateObj.marker.styles,"color",$$v)},expression:"cardStateObj.marker.styles.color"}})],1)])],1)],1),_c("el-row",{staticClass:"mB10",attrs:{gutter:20}},[_c("el-col",{attrs:{span:11}},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v("Select Layer")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{multiple:"","collapse-tags":"",filterable:!0,placeholder:"Select layer"},on:{change:_vm.setDefaultData},model:{value:_vm.cardStateObj.layers,callback:function($$v){_vm.$set(_vm.cardStateObj,"layers",$$v)},expression:"cardStateObj.layers"}},_vm._l(_vm.layers,(function(item){return _c("el-option",{key:item.value,attrs:{label:item.label,value:item.value}})})),1)],1)],1),_c("el-col",{attrs:{span:11}},[_c("el-form-item",{staticClass:"mB10"},[_c("p",{staticClass:"fc-input-label-txt pB5"},[_vm._v(" Map Zoom level ")]),_c("el-select",{staticClass:"width100 el-input-textbox-full-border",attrs:{"collapse-tags":"",filterable:!0,placeholder:"Select Zoom"},model:{value:_vm.cardStateObj.zoom,callback:function($$v){_vm.$set(_vm.cardStateObj,"zoom",$$v)},expression:"cardStateObj.zoom"}},[_c("el-option",{attrs:{label:"Auto",value:null}}),_vm._l(19,(function(number){return _c("el-option",{key:number,attrs:{label:number,value:number}})}))],2)],1)],1)],1),_vm.isHeatmapEnabled?_c("el-row",{staticClass:"mB10"},[_c("el-col",{attrs:{span:16}},[_c("el-form-item",{staticClass:"mB10"},[_c("f-color-palettes",{attrs:{colorIndex:_vm.cardStateObj.heatmap.colorIndex},on:{colorSelected:_vm.onColorChange}})],1)],1)],1):_vm._e()],1)],1)],1)],1),_c("div",{staticClass:"preview-panel section"},[_c("div",{staticClass:"card-wrapper"},[_c("Card",{attrs:{cardData:_vm.previewData,cardStyle:_vm.previewStyles,cardState:_vm.previewState,loading:_vm.isPreviewLoading}})],1)])]),_c("div",{staticClass:"d-flex mT-auto form-action-btn"},[_c("el-button",{staticClass:"form-btn f13 bold secondary text-center text-uppercase",on:{click:function($event){return _vm.onGoBack()}}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"form-btn f13 bold primary m0 text-center text-uppercase",attrs:{type:"primary"},on:{click:function($event){return _vm.save()}}},[_vm._v("Save")])],1)])},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),BaseConfig=(__webpack_require__(339772),__webpack_require__(670560),__webpack_require__(260228),__webpack_require__(564043),__webpack_require__(907409),__webpack_require__(506203),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(425728),__webpack_require__(186466),__webpack_require__(821057),__webpack_require__(641384)),Card=__webpack_require__(540803),card_constants=__webpack_require__(806186),validation=__webpack_require__(990260),DateHelper=__webpack_require__(281230),colors=__webpack_require__(519898),map_constant=__webpack_require__(942930),FColorPalettes=__webpack_require__(856778),api=__webpack_require__(32284),Configvue_type_script_lang_js={extends:BaseConfig/* default */.Z,props:["isNew","onClose","onGoBack"],mixins:[DateHelper/* default */.Z],components:{Card:Card/* default */.Z,FColorPalettes:FColorPalettes/* default */.Z},data:function(){return{layers:[{value:"marker",label:"Marker"},{value:"heatmap",label:"HeatMap"}],cardLayout:"mapcard_layout_1",rerenderCriteriaBuilder:!1,activeTab:"config",readings:null,isPreviewLoading:!1,resourceProps:["title","moduleName",{prop:"marker",resourceProps:["icon","type","reading"]},"dateRange"],cardDataObj:{title:"",moduleName:"site",assetCategory:null,marker:{icon:1,type:"No of Workoders",reading:{fieldName:null,moduleName:null,yAggr:"sum"}},dateRange:"Current Month"},cardStateObj:{canResize:!0,styles:{theme:null},zoom:null,layers:["marker"],heatmap:{colorIndex:1,gradient:[],radius:30,opacity:1},marker:{styles:{color:"#FE2C25"}}},cardActions:{default:{actionType:"showTrend"}},layout:{w:16,h:19},result:null,validationRules:{},locationModules:card_constants/* locationModules */.f5,markerValues:card_constants/* markerValues */.pM,iconTypes:card_constants/* iconTypes */.IU,aggregateFunctions:card_constants/* aggregateFunctions */.rD,dateOperators:card_constants/* dateOperators */._M,mapIcons:card_constants/* mapIcons */.Y6,mapTheams:map_constant/* mapTheams */.I}},created:function(){this.$store.dispatch("loadAssetCategory"),this.loadReadings()},computed:{isHeatmapEnabled:function(){var layers=this.cardStateObj.layers;if(!(0,validation/* isEmpty */.xb)(layers)){var heatmapIndex=layers.findIndex((function(layer){return"heatmap"===layer}));if(heatmapIndex>-1)return!0}return!1},previewData:function(){var result=this.result,_ref=result||{},data=_ref.data;return(0,validation/* isEmpty */.xb)(this.result)?{period:"Last Month",targetValue:{value:100},title:"Gauge Card 1",value:{unit:"KwH",value:65}}:data},previewStyles:function(){var result=this.result,cardStateObj=this.cardStateObj,_ref2=result||{},cardContext=_ref2.cardContext;return(0,validation/* isEmpty */.xb)(this.result)?this.serializeState(cardStateObj):cardContext.cardState||{}},assetCategory:function(){return this.$store.state.assetCategory},categoryWithFields:function(){return this.cardDataObj.assetCategory?this.readings.categoryWithFields[this.cardDataObj.assetCategory]:{}},fields:function(){return this.readings.fields},markerstyle:function(){return!(0,validation/* isEmpty */.xb)(this.cardStateObj)&&this.cardStateObj.marker?this.cardStateObj.marker:{}}},methods:{setDefaultData:function(){var _this=this,heatmap={colorIndex:1,gradient:[],radius:20,opacity:1},colorArr=["#1d7f01","#6cb302","#e9f501","#fda504","#fb5905"];if(!(0,validation/* isEmpty */.xb)(colorArr)){var rgbaColors=[],firstColor=colorArr[0];rgbaColors.push(this.hexToRgbA(firstColor,0)),// doing bcs google map taking first color as default so which is rendered to full google map
colorArr.forEach((function(hex){rgbaColors.push(_this.hexToRgbA(hex))})),heatmap.gradient=rgbaColors}this.cardStateObj.heatmap||this.$set(this.cardStateObj,"heatmap",heatmap)},hexToRgbA:function(hex,opacity){var c;if(/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex))return c=hex.substring(1).split(""),3==c.length&&(c=[c[0],c[0],c[1],c[1],c[2],c[2]]),c="0x"+c.join(""),"rgba("+[c>>16&255,c>>8&255,255&c].join(",")+","+("undefined"!==typeof opacity?opacity:1)+")"},onColorChange:function(idx,colorArr){var _this2=this,heatmap={colorIndex:idx,gradient:[]};if(!(0,validation/* isEmpty */.xb)(colorArr)){var rgbaColors=[],firstColor=colorArr[0];rgbaColors.push(this.hexToRgbA(firstColor,0)),// doing bcs google map taking first color as default so which is rendered to full google map
colorArr.forEach((function(hex){rgbaColors.push(_this2.hexToRgbA(hex))})),heatmap.gradient=rgbaColors}this.$set(this.cardStateObj,"heatmap",heatmap)},setReadingModuleName:function(fieldName){this.cardDataObj.marker.reading.moduleName=Object.values(this.fields).find((function(rt){return rt.name===fieldName})).module.name},loadReadings:function(){var _this3=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var _yield$API$get,data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return _context.next=2,api/* API */.bl.get("/asset/getreadings");case 2:_yield$API$get=_context.sent,data=_yield$API$get.data,data&&(_this3.readings=data);case 5:case"end":return _context.stop()}}),_callee)})))()},getPredefinedColors:function(){return colors/* default */.Z.readingcardColors},serializeState:function(){return this.cardStateObj},validateProperty:function(){var _marker=this.cardDataObj.marker,_ref3=_marker||{},reading=_ref3.reading;return{marker:function(){return!("reading"!==_marker.type||!(0,validation/* isEmpty */.xb)(reading.moduleName))||!("reading"!==_marker.type||!(0,validation/* isEmpty */.xb)(reading.fieldName))}}},validateReadingField:function(marker){var hasReading=!0,reading=marker.reading;return"reading"===marker.type&&(hasReading=!(0,validation/* isEmpty */.xb)(reading),["moduleName","yAggr","fieldName"].forEach((function(prop){hasReading=!(0,validation/* isEmpty */.xb)(reading[prop])}))),hasReading},validateField:function(){var validator=function(rule,value,callback){var marker=this.cardDataObj.marker;this.validateReadingField(marker)||callback(new Error("Reading Fields can not be empty")),callback()}.bind(this);return{marker:{trigger:"change",validator:validator}}},getCriteria:function(condition){this.cardDataObj.criteria=condition},selectModuleAction:function(){var _this4=this;this.rerenderCriteriaBuilder=!0,setTimeout((function(){_this4.rerenderCriteriaBuilder=!1}),100)}}},base_Configvue_type_script_lang_js=Configvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(base_Configvue_type_script_lang_js,render,staticRenderFns,!1,null,"67232db6",null)
/* harmony default export */,Config=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/16843.js.map