"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[97232],{
/***/397232:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return InspectionWidgetCount}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/inspection/inspection-template/InspectionWidgetCount.vue?vue&type=template&id=6a32c038&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"inspection-count-widget",staticClass:"inspection-counts-widget"},[_c("div",{staticClass:"count-widget-card border-left-widget"},[_c("div",{staticClass:"count"},[_vm._v(_vm._s(_vm.questionCount))]),_c("div",{staticClass:"f13 fc-black-11 fwBold text-uppercase"},[_vm._v(" "+_vm._s(1===_vm.questionCount?_vm.$t("qanda.template.question"):_vm.$t("qanda.template.questions"))+" ")])]),_c("div",{staticClass:"count-widget-card border-left-widget"},[_c("div",{staticClass:"count"},[_vm._v(_vm._s(_vm.pagesCount))]),_c("div",{staticClass:"f13 fc-black-11 fwBold text-uppercase"},[_vm._v(" "+_vm._s(1===_vm.pagesCount?_vm.$t("qanda.template.page"):_vm.$t("qanda.template.pages"))+" ")])]),_c("div",{staticClass:"count-widget-card"},[_c("div",{staticClass:"f13 fc-black-11 fwBold text-uppercase"},[_vm._v(" "+_vm._s(_vm.$validation.isEmpty(_vm.lastTriggeredOn)?_vm.$t("qanda.template.not_triggered"):_vm.$t("qanda.template.last_triggered"))+" ")]),_c("div",{staticClass:"count-triggerd-on"},[_vm._v(_vm._s(_vm.lastTriggeredOn))])])])},staticRenderFns=[],InspectionWidgetCountvue_type_script_lang_js={props:["details","resizeWidget","calculateDimensions"],data:function(){return{questions:[]}},mounted:function(){var _this=this;this.$nextTick((function(){var container=_this.$refs["inspection-count-widget"];if(container){var width=container.scrollWidth,height=container.scrollHeight;_this.resizeWidget&&_this.resizeWidget({height:height-70,width:width})}}))},computed:{pagesCount:function(){var details=this.details,pages=this.$getProperty(details,"totalPages",0);return pages},questionCount:function(){var details=this.details,questions=this.$getProperty(details,"totalQuestions",0);return questions},lastTriggeredOn:function(){var lastTriggered=this.$getProperty(this,"details.lastTriggeredTime"),triggeredTime=this.$options.filters.fromNow(lastTriggered);return lastTriggered?triggeredTime:null},moduleName:function(){return"inspectionTemplate"}}},inspection_template_InspectionWidgetCountvue_type_script_lang_js=InspectionWidgetCountvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(inspection_template_InspectionWidgetCountvue_type_script_lang_js,render,staticRenderFns,!1,null,"6a32c038",null)
/* harmony default export */,InspectionWidgetCount=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/97232.js.map