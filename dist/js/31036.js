"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[31036],{
/***/931036:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Tasks}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/workorders/v3/widgets/Tasks.vue?vue&type=template&id=f5d51dba
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",_vm._l(_vm.workorder.taskSections,(function(woSection,woSectionIx){return _c("div",{key:woSectionIx,staticClass:"white-bg-block mB20 pB40"},[_c("div",{staticClass:"label-txt-black p20 border-bottom4 fw-bold"},[_vm._v(" "+_vm._s(woSection.name)+" ")]),_vm._l(_vm.workorder.tasks[woSection.id],(function(task,taskIx){return _c("div",{key:taskIx,staticClass:"fc__sum__task__list",class:{selected:_vm.selectedTask===task,closed:"OPEN"!==task.statusNewEnum}},[_c("div",{staticClass:"width40px",staticStyle:{cursor:"pointer","padding-left":"5px"}},["OPEN"===task.statusNewEnum?_c("i",{staticClass:"fa fa-circle-thin",staticStyle:{color:"#ff3184","font-size":"28px"}}):_c("i",{staticClass:"el-icon-circle-check",staticStyle:{color:"#ff3184","font-size":"28px"}})]),_c("div",{staticClass:"task-item width50",staticStyle:{"padding-left":"10px",position:"relative"},on:{click:function($event){return _vm.showTaskDetails(task)}}},[-1===task.id?_c("el-input",{staticStyle:{width:"100%"},attrs:{type:"text",placeholder:_vm.$t("maintenance._workorder.write_a_task_name")},model:{value:task.subject,callback:function($$v){_vm.$set(task,"subject",$$v)},expression:"task.subject"}}):_c("div",{class:[{closedTask:"OPEN"!==task.statusNewEnum},"label-txt-black pointer"],staticStyle:{width:"100%","word-wrap":"break-word"},attrs:{type:"text",placeholder:""}},[_c("span",{staticClass:"fc-id"},[_vm._v("#"+_vm._s(task.uniqueId))]),_vm._v(" "+_vm._s(task.subject)+" ")]),-1===task.id||task.resource&&task.resource.name?_c("div",{staticClass:"pT5 inline-flex"}):_vm._e(),_c("div")],1)])}))],2)})),0)},staticRenderFns=[],Tasksvue_type_script_lang_js={name:"Tasks",props:["moduleName","details"],data:function(){return{showAddTask:!1,selectedTask:{inputValidationRuleId:-1,inputValidation:null,safeLimitRuleId:-1,minSafeLimit:null,maxSafeLimit:null,options:[],taskType:-1,enableInput:!1,inputType:1,attachmentRequired:!1,subject:"",selectedResourceName:"",inputValues:[]}}},computed:{currModuleName:function(){return"workorder"},widgetTitle:function(){return"Tasks"},workorder:function(){return this.details.workorder}},methods:{closeAddTask:function(){this.showAddTask=!1},getSortedTaskList:function(){
// let sorted = []
// if (this.workorder.tasks) {
//   let list = this.workorder.tasks
//   if (this.selectedTaskResource > 0) {
//     list = list.filter(
//       task =>
//         task.resource && task.resource.id === this.selectedTaskResource
//     )
//   }
//   sorted.push({ sectionId: -1, taskList: list })
// }
// if (this.sections) {
//   let seqList = Object.keys(this.sections).map(sectionId => ({
//     secId: sectionId,
//     sequence: this.taskList[sectionId][0].sequence,
//   }))
//   sorted.push(
//     ...seqList
//       .sort((a, b) => a.sequence - b.sequence)
//       .map(obj => ({
//         sectionId: obj.secId,
//         taskList:
//           this.selectedTaskResource > 0
//             ? this.taskList[obj.secId].filter(
//                 task =>
//                   task.resource &&
//                   task.resource.id === this.selectedTaskResource
//               )
//             : this.taskList[obj.secId],
//       }))
//   )
// }
// return sorted.filter(obj => obj.taskList.length > 0)
return this.workorder.tasks}}},widgets_Tasksvue_type_script_lang_js=Tasksvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(widgets_Tasksvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Tasks=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/31036.js.map