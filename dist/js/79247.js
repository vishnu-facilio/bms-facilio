"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[79247],{
/***/436756:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FDialogNew}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/FDialogNew.vue?vue&type=template&id=388b7f7a
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticClass:"costdialog",class:["f-dialog",_vm.customClass],attrs:{visible:_vm.visible,"before-close":_vm.close,width:_vm.width,"append-to-body":!0},on:{"update:visible":function($event){_vm.visible=$event}}},[_c("div",{staticClass:"f-dialog-title",attrs:{slot:"title"},slot:"title"},[_vm._t("header",(function(){return[_c("div",{staticClass:"label-txt-black fwBold"},[_vm._v(" "+_vm._s(_vm.title)+" ")])]}))],2),_c("div",{staticClass:"f-dialog-content",style:_vm.styleCss},[_vm._t("content"),_vm._t("default")],2),_c("div",{staticClass:"f-footer row",staticStyle:{height:"46px"}},[_vm._t("footer",(function(){return[_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.close}},[_vm._v(_vm._s(_vm.$t("common._common.cancel")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary",loading:_vm.loading},on:{click:_vm.save}},[_vm._v(_vm._s(_vm.loading?_vm.loadingTitle||"Saving...":_vm.confirmTitle||"Save"))])],1)]}))],2)])},staticRenderFns=[],FDialogNewvue_type_script_lang_js={props:["title","confirmTitle","visible","customClass","width","maxHeight","stayOnSave","loading","loadingTitle","record"],computed:{styleCss:function(){return this.maxHeight?{"max-height":this.maxHeight,overflow:"scroll"}:null}},methods:{close:function(){this.$emit("close"),this.$emit("update:visible",!1)},save:function(){this.$emit("save"),this.stayOnSave||this.$emit("update:visible",!1)}}},components_FDialogNewvue_type_script_lang_js=FDialogNewvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FDialogNewvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FDialogNew=component.exports},
/***/379247:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Commands}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/readings/Commands.vue?vue&type=template&id=a26eae9e&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"command-card"},[_c("div",{staticClass:"f14 bold"},[_vm._v(_vm._s(_vm.reading.field.displayName))]),_c("div",{staticClass:"mT30 bold f18"},[_c("div",{domProps:{innerHTML:_vm._s(_vm.$fieldUtils.getDisplayValue(_vm.reading.field,_vm.reading.value))}})]),_c("el-button",{staticClass:"text-uppercase fc__border__btn mT35 mB0 pR30 pL30",staticStyle:{"max-width":"90px"},attrs:{size:"small"},on:{click:function($event){return _vm.showSetReadingDialog(_vm.reading.field,_vm.reading.value)}}},[_vm._v("Set")]),_vm.showSetDialog?_c("SetReadingPopup",{key:_vm.newReading.field.id,attrs:{reading:_vm.newReading,saveAction:_vm.closePopup,closeAction:_vm.closePopup,recordId:_vm.details.id,recordName:_vm.details.name}}):_vm._e()],1)},staticRenderFns=[],SetReadingValue=__webpack_require__(681121),Commandsvue_type_script_lang_js={props:["details","layoutParams","hideTitleSection","sectionKey","widget","resizeWidget"],components:{SetReadingPopup:SetReadingValue/* default */.Z},data:function(){return{loading:!1,reading:null,showSetDialog:!1,newReading:{value:null,ttime:null},readingSaving:!1}},computed:{setValue:function(){var data=this.$store.state.publishdata.setValue[this.details.id];return data?data[this.reading.field.id]:null}},watch:{widget:{immediate:!0,handler:"loadData"},setValue:{handler:"setFieldValue"}},methods:{loadData:function(){this.reading=this.widget.widgetParams.data},showSetReadingDialog:function(field,value){this.newReading.field=field,this.newReading.value=value,this.showSetDialog=!0},setFieldValue:function(){this.setValue&&(this.reading.value=this.setValue)},closePopup:function(){this.showSetDialog=!1,this.resetEditObj()},resetEditObj:function(){this.newReading={ttime:null,value:null}}}},readings_Commandsvue_type_script_lang_js=Commandsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(readings_Commandsvue_type_script_lang_js,render,staticRenderFns,!1,null,"a26eae9e",null)
/* harmony default export */,Commands=component.exports},
/***/681121:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return SetReadingValue}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/readings/SetReadingValue.vue?vue&type=template&id=f05d1da2
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("f-dialog",{attrs:{title:_vm.getTitle,visible:!0,loading:_vm.isSaving,stayOnSave:!0,record:_vm.record,width:_vm.$mobile?"90%":"30%"},on:{save:_vm.saveReading,close:_vm.closeDialog}},[_vm.readingObj?_c("div",{staticClass:"pB30 mT10",attrs:{slot:"content"},slot:"content"},[_c("div",{staticClass:"mb5"},[_vm._v(_vm._s(_vm.getSubTitle))]),_c("div",{staticStyle:{display:"flex"}},[_vm.isBooleanField(_vm.readingObj.field)?_c("div",{staticClass:"pT10 width60"},[_vm._v(" "+_vm._s(_vm.readingObj.field.falseVal||"False")+" "),_c("el-switch",{staticClass:"pL10 pR10",attrs:{disabled:_vm.isNullValueSelected},model:{value:_vm.readingObj.value,callback:function($$v){_vm.$set(_vm.readingObj,"value",$$v)},expression:"readingObj.value"}}),_vm._v(" "+_vm._s(_vm.readingObj.field.trueVal||"True")+" ")],1):_vm.isEnumField(_vm.readingObj.field)?_c("div",{staticClass:"width80"},[_c("el-select",{staticClass:"fc-input-full-border-select2 width90",attrs:{disabled:_vm.isNullValueSelected,filterable:"","collapse-tags":""},model:{value:_vm.readingObj.value,callback:function($$v){_vm.$set(_vm.readingObj,"value",$$v)},expression:"readingObj.value"}},_vm._l(_vm.fieldOptions,(function(item,index){return _c("el-option",{key:index,attrs:{label:item.label,value:item.value}})})),1)],1):_c("div",{staticClass:"width80"},[_c("el-input",{staticClass:"fc-input-full-border2 control-action-reading-field",staticStyle:{width:"90%","text-align":"left"},attrs:{disabled:_vm.isNullValueSelected,"controls-position":"right",type:_vm.fieldType},model:{value:_vm.readingObj.value,callback:function($$v){_vm.$set(_vm.readingObj,"value",$$v)},expression:"readingObj.value"}},[_vm.readingObj.field.unit?_c("template",{slot:"append"},[_vm._v(_vm._s(_vm.readingObj.field.unit)+" ")]):_vm._e()],2)],1),_c("div",{staticStyle:{"padding-top":"10px"}},[_c("el-checkbox",{on:{change:function($event){return _vm.changeValue()}},model:{value:_vm.isNullValueSelected,callback:function($$v){_vm.isNullValueSelected=$$v},expression:"isNullValueSelected"}},[_vm._v("null")])],1)])]):_c("div",{staticClass:"pB40 mT10"},[_vm._v("Loading....")])])},staticRenderFns=[],FDialogNew=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),__webpack_require__(212826),__webpack_require__(436756)),validation=__webpack_require__(990260),utility_methods=__webpack_require__(555005),utils_field=__webpack_require__(815872),SetReadingValuevue_type_script_lang_js={props:["reading","saveAction","closeAction","recordId","recordName","fieldId","groupId","pointId"],components:{FDialog:FDialogNew/* default */.Z},data:function(){return{readingObj:null,isSaving:!1,record:null,recordLoading:!1,isNullValueSelected:!1}},created:function(){var _this=this;this.isBooleanField=utils_field/* isBooleanField */.Mw,this.isEnumField=utils_field/* isEnumField */.Ri,this.reading?this.readingObj=this.reading:this.groupId?this.$http.get("/v2/controlAction/getControlGroupMeta?controlGroupId="+this.groupId).then((function(response){response.data.result&&response.data.result.controlActionGroup&&(_this.readingObj=response.data.result.controlActionGroup)})):this.pointId?this.$http.get("/v2/controlAction/getControllablePoints").then((function(response){response.data.result&&response.data.result.controllablePoints&&response.data.result.controllablePoints.length&&(_this.readingObj=response.data.result.controllablePoints.find((function(rt){return rt.id===_this.pointId})))})):this.$util.loadLatestReading(this.recordId,!1,!1,null,this.fieldId).then((function(fields){fields&&fields.length&&(_this.readingObj=fields[0])}))},mounted:function(){var _this2=this;this.recordId&&(this.recordLoading=!0,this.$util.getAssetById(this.recordId).then((function(assetObj){_this2.record=assetObj,_this2.recordLoading=!1})))},computed:{fieldOptions:function(){return this.readingObj?(0,utility_methods/* constructFieldOptions */.aE)(this.readingObj.field.enumMap||[]):[]},fieldType:function(){var readingObj=this.readingObj,_ref=readingObj||{},field=_ref.field;return(0,validation/* isEmpty */.xb)(field)||!(0,utils_field/* isNumberField */.Z2)(field)&&!(0,utils_field/* isDecimalField */.No)(field)?"text":"number"},getTitle:function(){return this.readingObj?this.recordLoading?"Loading...":this.recordId&&this.record?this.record.name:this.readingObj.field.displayName:"Loading..."},getSubTitle:function(){return this.readingObj?this.recordLoading?"Loading...":this.recordId&&this.record?this.readingObj.field.displayName:"Set Reading":"Loading..."}},methods:{validate:function(){return(0,validation/* isEmpty */.xb)(this.readingObj.value)?(this.error=!0,this.errorMessage="Please enter a value for the reading"):(this.error=!1,this.errorMessage=""),!this.error},saveReading:function(){var _this$$util,_this3=this;if(this.isNullValueSelected||this.validate()){var newValue;this.isSaving=!0,newValue=null==this.readingObj.value?null:this.readingObj.value.toString();var data=[this.recordId,this.readingObj.field.fieldId,
// this.readingObj.value.toString(),
newValue,this.recordName,this.readingObj.field.displayName,this.groupId];(_this$$util=this.$util).setReadingValue.apply(_this$$util,data).then((function(){_this3.saveAction(_this3.readingObj),_this3.isSaving=!1})).catch((function(){_this3.isSaving=!1}))}},closeDialog:function(){this.closeAction(this.readingObj)},changeValue:function(){this.readingObj.value=null}}},readings_SetReadingValuevue_type_script_lang_js=SetReadingValuevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(readings_SetReadingValuevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SetReadingValue=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/79247.js.map