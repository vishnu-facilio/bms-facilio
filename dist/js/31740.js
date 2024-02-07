"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[31740],{
/***/431740:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return DigitalLogBook}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/devices/DigitalLogBook.vue?vue&type=template&id=3c135fb1&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-white-theme digital-log-book-container height100"},[_c("div",{staticClass:"space-info fc-border-1 p15  position-sticky top0"},[_vm._v(" "+_vm._s(_vm.currentAccount.data.device.associatedResource.name)+" ")]),_c("router-view")],1)},staticRenderFns=[],objectSpread2=__webpack_require__(595082),vuex_esm=(__webpack_require__(434284),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(564043),__webpack_require__(857267),__webpack_require__(648324),__webpack_require__(420629)),DigitalLogBookvue_type_script_lang_js={components:{},beforeRouteLeave:function(to,from,next){
//when navigating to summary page stop it and navigate to your summary page : )
//  if(to.name.includes('summary'))
//  {
//navigate to wo summary in devices , same component
//just switch between list and summary component
//accessing router props directly throws type error
var cp={};Object.assign(cp,to),cp.name&&cp.name.includes("summary")?next({name:"deviceWoSummary",params:{id:cp.params.id}}):next()},watch:{$route:{handler:function(newVal){
//when workorder list navigates to  summary page , redirect to summary within devices router
"deviceWoSummary"!==newVal.name&&this.setQueryParams()},deep:!0,immediate:!0}},data:function(){return{currentView:"open",showWOSummary:!1}},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapGetters */.Se)(["currentAccount"])),(0,vuex_esm/* mapActions */.nv)({loadViewDetail:"view/loadViewDetail",savesorting:"view/savesorting"})),mounted:function(){this.loadViewss(),this.getViewDetail(),this.$store.dispatch("view/loadModuleMeta","workorder")},methods:{setQueryParams:function(){var spaceFilter={resource:[{operatorId:38,value:[this.currentAccount.data.device.associatedResource.id+""]}]};this.$router.replace({query:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},this.$route.query),{},{search:JSON.stringify(spaceFilter),includeParentFilter:!0})})},loadViewss:function(){var param={moduleName:"workorder"};this.$store.dispatch("view/loadGroupViews",param)},getViewDetail:function(){this.$store.dispatch("view/loadViewDetail",{viewName:this.currentView,moduleName:"workorder"})}}},devices_DigitalLogBookvue_type_script_lang_js=DigitalLogBookvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(devices_DigitalLogBookvue_type_script_lang_js,render,staticRenderFns,!1,null,"3c135fb1",null)
/* harmony default export */,DigitalLogBook=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/31740.js.map