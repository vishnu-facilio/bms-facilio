"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[86275],{
/***/886275:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return SupplierDetailWidget}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/etisalat/suppliers/SupplierDetailWidget.vue?vue&type=template&id=50feb73e&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{ref:"preview-container",staticClass:"asset-details-widget"},[_c("div",{staticClass:"container"},[_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["picklist"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("picklist"))+" ")])],1)],1),_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline"))+" ")])],1)],1),_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline_1"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline_1"))+" ")])],1)],1),_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline_2"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline_2"))+" ")])],1)],1),_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline_3"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline_3"))+" ")])],1)],1),_c("div",{staticClass:"field"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline_4"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline_4"))+" ")])],1)],1),_c("div",{staticClass:"field border-none"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["number_3"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("number_3"))+" ")])],1)],1),_c("div",{staticClass:"field border-none"},[_c("el-row",[_c("el-col",{staticClass:"field-label",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.fieldDisplayNames["singleline_5"])+" ")]),_c("el-col",{staticClass:"field-value",attrs:{span:12}},[_vm._v(" "+_vm._s(_vm.getFieldData("singleline_5"))+" ")])],1)],1)])])])},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(425728),__webpack_require__(420629)),validation=(__webpack_require__(480008),__webpack_require__(990260)),SupplierDetailWidgetvue_type_script_lang_js={props:["details","moduleName","resizeWidget","calculateDimensions"],data:function(){return{initialWidgetHeight:null}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({moduleMeta:function(state){return state.view.metaInfo}})),{},{fields:function(){return this.moduleMeta.fields||[]},fieldDisplayNames:function(){var _this=this;if(this.moduleMeta&&this.moduleMeta.fields){var fieldMap={};return this.moduleMeta.fields.forEach((function(field){_this.$set(fieldMap,field.name,field.displayName)})),fieldMap}return[]},data:function(){return this.details.data||null},currentView:function(){var $route=this.$route;return $route.params.viewName?$route.params.viewName:null}}),mounted:function(){this.autoResize()},methods:{autoResize:function(){var _this2=this;this.$nextTick((function(){if(!(0,validation/* isEmpty */.xb)(_this2.$refs["preview-container"])){var height=_this2.$refs["preview-container"].scrollHeight+100,width=_this2.$refs["preview-container"].scrollWidth;_this2.resizeWidget&&_this2.resizeWidget({height:height,width:width})}}))},getFieldData:function(fieldName){if(fieldName){this.fields.find((function(rt){return rt.name===fieldName}));if("singleline"===fieldName)return this.data.singleline?this.data.singleline:"---";if("singleline_1"===fieldName)return this.data.singleline_1?this.data.singleline_1:"---";if("singleline_2"===fieldName)return this.data.singleline_2?this.data.singleline_2:"---";if("singleline_3"===fieldName)return this.data.singleline_3?this.data.singleline_3:"---";if("singleline_4"===fieldName)return this.data.singleline_4?this.data.singleline_4:"---";if("singleline_5"===fieldName)return this.data.singleline_5?this.data.singleline_5:"---";if("number_3"===fieldName)return this.data.number_3?this.data.number_3:"---";if("picklist"===fieldName)return 1===this.data.picklist?"Authority":2===this.data.picklist?"Landlord":"---"}return""}}},suppliers_SupplierDetailWidgetvue_type_script_lang_js=SupplierDetailWidgetvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(suppliers_SupplierDetailWidgetvue_type_script_lang_js,render,staticRenderFns,!1,null,"50feb73e",null)
/* harmony default export */,SupplierDetailWidget=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/86275.js.map