"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[57237,11358],{
/***/20874:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return SurveyHistory}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.function.name.js
__webpack_require__(434284);
// EXTERNAL MODULE: ./src/pages/inspection/inspection-template/InspectionHistory.vue + 4 modules
var render,staticRenderFns,InspectionHistory=__webpack_require__(243300),router=__webpack_require__(329435),SurveyHistoryvue_type_script_lang_js={extends:InspectionHistory["default"],name:"SurveyHistory",computed:{moduleName:function(){return"surveyResponse"},moduleDisplayName:function(){return"Survey"},moduleHeaderName:function(){return"Survey"}},methods:{redirectToOverview:function(record){var route,_ref=record||{},id=_ref.id,moduleName=this.moduleName;if((0,router/* isWebTabsEnabled */.tj)()){var _findRouteForModule=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW),name=_findRouteForModule.name;name&&(route=this.$router.resolve({name:name,params:{id:id,viewname:"all"}}))}else route=this.$router.resolve({name:"individualSurveySummary",params:{id:id,viewname:"all"}});route&&window.open(route.href,"_blank")}}},survey_template_SurveyHistoryvue_type_script_lang_js=SurveyHistoryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(survey_template_SurveyHistoryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,SurveyHistory=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/57237.js.map