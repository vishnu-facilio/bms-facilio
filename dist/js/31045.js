"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[31045,32686,80865,53021],{
/***/746027:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return InsuranceForm}});
// EXTERNAL MODULE: ./node_modules/@facilio/utils/validation.js
var render,staticRenderFns,validation=__webpack_require__(990260),FetchViewsMixin=__webpack_require__(646716),ModuleForm=__webpack_require__(498915),InsuranceFormvue_type_script_lang_js={extends:ModuleForm["default"],mixins:[FetchViewsMixin/* default */.Z],computed:{title:function(){return(0,validation/* isEmpty */.xb)(this.$route.params.id)?this.$t("common.products.add_insurance"):this.$t("common.products.edit_insurance")},moduleDisplayName:function(){return this.formObj&&this.formObj.module?this.formObj.module.displayName:"Insurance"},moduleDataId:function(){return this.$route.params.id}},methods:{afterSerializeHook:function(_ref){var data=_ref.data;return(0,validation/* isEmpty */.xb)(this.moduleDataId)&&(data["addedBy"]={id:this.$portaluser.ouid}),data}}},insurance_InsuranceFormvue_type_script_lang_js=InsuranceFormvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(insurance_InsuranceFormvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,InsuranceForm=component.exports;
// EXTERNAL MODULE: ./src/components/base/FetchViewsMixin.vue + 2 modules
}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/31045.js.map