"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[40685],{
/***/940685:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return templateSummaryDetails}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/template/templateSummaryDetails.vue?vue&type=template&id=72bc26ee
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"p20"},[_c("div",{staticClass:"fc-text-pink11 text-uppercase"},[_vm._v("Description")]),_c("div",{staticClass:"fc-black-13 pT10 text-left"},[_vm._v(_vm._s(_vm.details.description))]),_c("div",{staticClass:"pT20"},[_c("el-row",[_c("el-col",{attrs:{span:6}},[_c("div",{staticClass:"fc-blue-label text-uppercase text-left"},[_vm._v("Alarm Type")]),_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v("Assets")])]),_c("el-col",{attrs:{span:6}},[_c("div",{staticClass:"fc-blue-label text-uppercase text-left"},[_vm._v(" ASSET CATEGORY ")]),_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v(_vm._s(_vm.details.category))])]),_c("el-col",{attrs:{span:6}},[_c("div",{staticClass:"fc-blue-label text-uppercase text-left"},[_vm._v("ASSETS")]),_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm.getAssetDetail.totalAssets===_vm.getAssetDetail.unavailable?_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v(" No Assets Applied ")]):_vm.getAssetDetail.totalAssets===_vm.getAssetDetail.available?_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v(" All "+_vm._s(_vm.details.category)+"s ")]):_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v(" "+_vm._s(_vm.partialAssetDetail())+" ")])])]),_c("el-col",{attrs:{span:6}},[_c("div",{staticClass:"fc-blue-label text-uppercase text-left"},[_vm._v(" THRESHOLD METRIC ")]),_c("div",{staticClass:"fc-black-13 text-left pT5"},[_vm._v(" "+_vm._s(_vm.details.threshold_metric_display)+" ")])])],1)],1)])},staticRenderFns=[],templateSummaryDetailsvue_type_script_lang_js=(__webpack_require__(354754),{props:["details"],methods:{partialAssetDetail:function(){var detail=this.getAssetDetail,strConst="";return strConst=1===detail.available?"Assigned to "+detail.available+" "+this.details.category+" of "+detail.totalAssets:"Assigned to "+detail.available+" "+this.details.category+"s of "+detail.totalAssets,strConst}},computed:{getAssetDetail:function(){return this.details.assetDetail}}}),template_templateSummaryDetailsvue_type_script_lang_js=templateSummaryDetailsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(template_templateSummaryDetailsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,templateSummaryDetails=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/40685.js.map