"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[43205,32686,57373,80865,53021],{
/***/665774:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return TransitionAuth}});
// EXTERNAL MODULE: ./node_modules/@facilio/api/index.mjs
var render,staticRenderFns,api=__webpack_require__(32284),Transition=__webpack_require__(973930),validation=__webpack_require__(990260),TransitionAuthvue_type_script_lang_js={extends:Transition["default"],props:["moduleName","recordId","transitionId"],created:function(){this.hasProps?(this.loadOrgInfo(),this.loadData()):(this.isTransitionInProgress=!1,this.isTransitionComplete=!1)},computed:{hasProps:function(){return this.moduleName&&this.recordId&&this.transitionId},canShowTransitionBlock:function(){return this.hasProps&&!this.hasTransitionFailed}},methods:{loadData:function(){this.details={recordId:this.recordId,moduleName:this.moduleName},this.loadTransition()},loadTransition:function(){var _this=this;return api/* API */.bl.post("v2/statetransition/view",{stateTransitionId:this.transitionId}).then((function(_ref){var error=_ref.error,data=_ref.data;if(!error)return _this.transition=(0,validation/* isEmpty */.xb)(data&&data.transition)?null:data.transition,_this.loadModule().then((function(){return(0,validation/* isEmpty */.xb)(_this.transition.formId)?_this.transitionToState():_this.showTransitionForm()}));_this.setFailureState("Link Expired","TRANSITION_FETCH_FAILED")}))}}},transition_TransitionAuthvue_type_script_lang_js=TransitionAuthvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(transition_TransitionAuthvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,TransitionAuth=component.exports;
// EXTERNAL MODULE: ./src/permalink/pages/transition/Transition.vue + 10 modules
}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/43205.js.map