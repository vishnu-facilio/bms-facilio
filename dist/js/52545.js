"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[52545],{
/***/752545:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Layout}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/parkingstall/Layout.vue?vue&type=template&id=0d5ea504
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"height100 pL50"},[_c("router-view")],1)},staticRenderFns=[],createForOfIteratorHelper=__webpack_require__(566347),Layoutvue_type_script_lang_js=(__webpack_require__(564043),__webpack_require__(857267),{data:function(){return{product:{code:"ps",label:"Parking Stall",path:"/app/ps",modules:[{label:this.$t("common.products.parkingstall"),path:{path:"/app/ps/parkingstall"}}]}}},mounted:function(){this.initProduct()},watch:{$route:function(){this.initProduct()}},methods:{initProduct:function(){if(this.$store.dispatch("switchProduct",this.product),"/app/ps"===this.$route.path){var _step,_iterator=(0,createForOfIteratorHelper/* default */.Z)(this.product.modules);try{for(_iterator.s();!(_step=_iterator.n()).done;){var mod=_step.value;
//   if (this.$hasPermission(mod.permission)) {
return void this.$router.replace(mod.path);
//   }
}}catch(err){_iterator.e(err)}finally{_iterator.f()}}}}}),parkingstall_Layoutvue_type_script_lang_js=Layoutvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(parkingstall_Layoutvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Layout=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/52545.js.map