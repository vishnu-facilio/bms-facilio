"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[14816,32686,26715,80865,53021],{
/***/514826:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return IndividualInductionSummary}});
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.array.includes.js
__webpack_require__(976801),__webpack_require__(434284),__webpack_require__(670560);
// EXTERNAL MODULE: ./node_modules/core-js/modules/es.function.name.js
var render,staticRenderFns,IndividualInspectionSummary=__webpack_require__(13427),router=__webpack_require__(329435),IndividualInductionSummaryvue_type_script_lang_js={extends:IndividualInspectionSummary["default"],computed:{pdfUrl:function(){return window.location.protocol+"//"+window.location.host+"/app/pdf/inductionPdf?id=".concat(this.id)},liveFormBtnText:function(){return"Attend Induction"},canShowLiveForm:function(){var record=this.record,$account=this.$account,peopleId=this.$getProperty(record,"people.id"),currUserPeopleId=this.$getProperty($account,"user.peopleId"),responseStatus=this.$getProperty(record,"responseStatus"),canEditRecord=this.$getProperty(this,"canEditRecord",!0),returnVal=record&&![1,4].includes(responseStatus)&&!this.$getProperty(record,"parent.deleted")&&peopleId===currUserPeopleId&&canEditRecord;return returnVal}},methods:{openLiveForm:function(){var record=this.record,moduleName=this.moduleName,id=this.$getProperty(record,"id","");if((0,router/* isWebTabsEnabled */.tj)()){var _ref=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.EDIT)||{},name=_ref.name;name&&this.$router.push({name:name,params:{id:id}})}else this.$router.push({name:"induction-live-form",params:{id:id}})}}},individual_induction_IndividualInductionSummaryvue_type_script_lang_js=IndividualInductionSummaryvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(individual_induction_IndividualInductionSummaryvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,IndividualInductionSummary=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/14816.js.map