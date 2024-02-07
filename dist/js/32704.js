"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[32704],{
/***/332704:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Shifts}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/setup/Shifts.vue?vue&type=template&id=11dc5903
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"full-layout-white height100"},[_c("div",{staticClass:"setting-header"},[_vm._m(0),_c("div",{staticClass:"action-btn setting-page-btn"},[_c("el-button",{staticClass:"setup-el-btn",attrs:{type:"primary"},on:{click:_vm.newShift}},[_vm._v("New Shift")]),_vm.showDialog?_c("new-shift",{attrs:{isNew:_vm.isNew,visibility:_vm.showDialog},on:{"update:visibility":function($event){_vm.showDialog=$event},reset:_vm.getShifts},model:{value:_vm.model,callback:function($$v){_vm.model=$$v},expression:"model"}}):_vm._e()],1)]),_c("div",{staticClass:"container-scroll"},[_c("div",{staticClass:"row setting-Rlayout mT30"},[_c("div",{staticClass:"col-lg-12 col-md-12"},[_c("table",{staticClass:"setting-list-view-table"},[_vm._m(1),_vm.loading?_c("tbody",[_c("tr",[_c("td",{staticClass:"text-center",attrs:{colspan:"100%"}},[_c("spinner",{attrs:{show:_vm.loading,size:"80"}})],1)])]):_c("tbody",_vm._l(_vm.shifts,(function(s,idx){return _c("tr",{key:s.id,staticClass:"tablerow"},[_c("td",[_vm._v(_vm._s(s.name))]),_c("td",_vm._l(_vm.formatDays(s),(function(s,i){return _c("el-row",{key:i},[_vm._v(_vm._s(s))])})),1),_c("td",_vm._l(_vm.formatTimes(s),(function(s,i){return _c("el-row",{key:i},[_vm._v(_vm._s(s))])})),1),_c("td",[_c("div",{staticClass:"text-left actions",staticStyle:{"margin-top":"-3px","margin-right":"15px","text-align":"center"}},[_c("i",{staticClass:"el-icon-edit pointer",on:{click:function($event){return _vm.editShift(s)}}}),_vm._v("    "),_c("i",{staticClass:"el-icon-delete pointer",on:{click:function($event){return _vm.deleteShift(s.id,idx)}}})])])])})),0)])])])])])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"setting-title-block"},[_c("div",{staticClass:"setting-form-title"},[_vm._v("Shifts Hours")]),_c("div",{staticClass:"heading-description"},[_vm._v("List of all Shift Hours")])])},function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("thead",[_c("tr",[_c("th",{staticClass:"setting-table-th setting-th-text"},[_vm._v("NAME")]),_c("th",{staticClass:"setting-table-th setting-th-text"},[_vm._v("SHIFT DAYS")]),_c("th",{staticClass:"setting-table-th setting-th-text"},[_vm._v("SHIFT TIMING")]),_c("th",{staticClass:"setting-table-th setting-th-text"})])])}],objectSpread2=__webpack_require__(595082),NewShiftvue_type_template_id_20dd3346_render=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(670560),function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{staticStyle:{"z-index":"999999"},attrs:{visible:_vm.visibility,fullscreen:!0,"custom-class":"fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog","before-close":_vm.closeDialog},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("el-form",{attrs:{model:_vm.value,"label-position":"top"}},[_c("div",{staticClass:"new-header-container"},[_c("div",{staticClass:"new-header-modal"},[_c("div",{staticClass:"new-header-text"},[_vm.isNew?_c("div",{staticClass:"setup-modal-title"},[_vm._v("New Shift")]):_c("div",{staticClass:"setup-modal-title"},[_vm._v("Edit Shift Hours")]),_c("div",{staticClass:"heading-description"},[_vm._v("List of Shift Hours")])])])]),_c("div",{staticClass:"new-body-modal",staticStyle:{"margin-top":"0"}},[_c("div",{staticClass:"modal-form-input"},[_c("p",{staticClass:"new-label-text"},[_vm._v("Name")]),_c("el-input",{staticClass:"form-item",model:{value:_vm.value.name,callback:function($$v){_vm.$set(_vm.value,"name",$$v)},expression:"value.name"}})],1),_c("div",{staticClass:"modal-form-input"},[_c("p",{staticClass:"new-label-text"},[_vm._v("Site")]),_c("el-select",{staticClass:"form-item",model:{value:_vm.value.siteId,callback:function($$v){_vm.$set(_vm.value,"siteId",$$v)},expression:"value.siteId"}},_vm._l(this.sites,(function(s){return _c("el-option",{key:s.id,attrs:{label:s.label,value:s.id}})})),1)],1),_c("div",{staticClass:"modal-form-input"},[_c("p",{staticClass:"new-label-text"},[_vm._v("Shift Hours")]),_c("el-row",{staticClass:"form-item"},[_c("el-radio",{staticClass:"form-item row",attrs:{label:"same"},model:{value:_vm.shiftHoursType,callback:function($$v){_vm.shiftHoursType=$$v},expression:"shiftHoursType"}},[_c("el-col",{attrs:{span:9}},[_vm._v(" Same hours every day ")]),_c("span",{directives:[{name:"show",rawName:"v-show",value:!_vm.isDifferent,expression:"!isDifferent"}]},[_c("el-col",{attrs:{span:6}},[_c("el-select",{staticClass:"form-item",staticStyle:{width:"100%"},model:{value:_vm.value.startTime,callback:function($$v){_vm.$set(_vm.value,"startTime",$$v)},expression:"value.startTime"}},_vm._l(this.timesOption,(function(time){return _c("el-option",{key:time,attrs:{label:time,value:time}})})),1)],1),_c("el-col",{attrs:{span:1}},[_vm._v(" to ")]),_c("el-col",{attrs:{span:6}},[_c("el-select",{staticClass:"form-item",staticStyle:{width:"100%"},model:{value:_vm.value.endTime,callback:function($$v){_vm.$set(_vm.value,"endTime",$$v)},expression:"value.endTime"}},_vm._l(this.timesOption,(function(time){return _c("el-option",{key:time,attrs:{label:time,value:time}})})),1)],1)],1)],1)],1),_c("el-row",[_c("el-radio",{staticClass:"form-item",attrs:{label:"different"},model:{value:_vm.shiftHoursType,callback:function($$v){_vm.shiftHoursType=$$v},expression:"shiftHoursType"}},[_vm._v(" Different hours every day ")])],1)],1),_c("div",{staticClass:"modal-form-input"},[_c("p",{staticClass:"new-label-text"},[_vm._v("Shift Days")])]),_vm._l(_vm.weekdays,(function(d){return _c("el-row",{key:d.label},[_c("el-col",{attrs:{span:6}},[_c("el-checkbox",{model:{value:d.checked,callback:function($$v){_vm.$set(d,"checked",$$v)},expression:"d.checked"}},[_vm._v(" "+_vm._s(d.dayOfWeekVal))])],1),_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.isDifferent&&d.checked,expression:"isDifferent && d.checked"}]},[_c("el-col",{attrs:{span:6}},[_c("el-select",{staticClass:"form-item",staticStyle:{width:"100%"},model:{value:d.startTime,callback:function($$v){_vm.$set(d,"startTime",$$v)},expression:"d.startTime"}},_vm._l(_vm.timesOption,(function(time){return _c("el-option",{key:time,attrs:{label:time,value:time}})})),1)],1),_c("el-col",{attrs:{span:1}},[_vm._v(" to ")]),_c("el-col",{attrs:{span:6}},[_c("el-select",{staticClass:"form-item",staticStyle:{width:"100%"},model:{value:d.endTime,callback:function($$v){_vm.$set(d,"endTime",$$v)},expression:"d.endTime"}},_vm._l(_vm.timesOption,(function(time){return _c("el-option",{key:time,attrs:{label:time,value:time}})})),1)],1)],1)],1)}))],2),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:function($event){return _vm.closeDialog()}}},[_vm._v("Cancel")]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"},on:{click:_vm.save}},[_vm._v("Save")])],1)])],1)}),NewShiftvue_type_template_id_20dd3346_staticRenderFns=[],NewShiftvue_type_script_lang_js=(__webpack_require__(538077),__webpack_require__(434284),__webpack_require__(425728),{props:["value","visibility","isNew"],created:function(){this.$store.dispatch("loadShifts")},mounted:function(){this.setHourOptions(),this.getSites(),this.setWeekdays()},computed:{isDifferent:function(){return!this.value.isSameTime},shiftHoursType:{get:function(){return this.value.isSameTime?"same":"different"},set:function(val){"same"===val?this.value.isSameTime=!0:"different"===val&&(this.value.isSameTime=!1)}}},data:function(){return{sites:[],timesOption:[],weekdays:[{checked:!1,dayOfWeek:1,dayOfWeekVal:"MONDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:2,dayOfWeekVal:"TUESDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:3,dayOfWeekVal:"WEDNESDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:4,dayOfWeekVal:"THURSDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:5,dayOfWeekVal:"FRIDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:6,dayOfWeekVal:"SATURDAY",startTime:"09:00",endTime:"17:00"},{checked:!1,dayOfWeek:7,dayOfWeekVal:"SUNDAY",startTime:"09:00",endTime:"17:00"}]}},methods:{closeDialog:function(){this.$emit("update:visibility",!1)},save:function(){var _this=this,param=this.value,days=this.weekdays.filter((function(e){return e.checked}));param.isSameTime?days.forEach((function(e){e.startTime=_this.value.startTime,e.endTime=_this.value.endTime})):(param.startTime="",param.endTime=""),param.days=days,this.isNew?this.$http.post("/shifts/add",{shift:param}).then((function(response){_this.$message.success("New Shift Added Successfully."),_this.closeDialog(),_this.$emit("reset")})):this.$http.post("/shifts/update",{shift:param}).then((function(response){_this.$message.success("Shift Update Successfully."),_this.closeDialog(),_this.$emit("reset")}))},setHourOptions:function(){for(var i=0;i<=23;i++){var time=(i<10?"0"+i:i)+":";this.timesOption.push(time+"00"),this.timesOption.push(time+"30")}},getSites:function(){var _this2=this;this.$http.get("/campus").then((function(response){response.data.records&&response.data.records.forEach((function(r){_this2.sites.push({label:r.name,id:r.id})}))}))},isExist:function(val){var res=this.findDay(val);return!!res},findDay:function(val){return this.value.days.find((function(d){return d.dayOfWeek===val}))},setWeekdays:function(){var _this3=this;this.weekdays.forEach((function(e){var val=_this3.findDay(e.dayOfWeek);val?(e.label=val.label,e.startTime=val.startTime,e.endTime=val.endTime,e.checked=!0):e.checked=!1}))}}}),setup_NewShiftvue_type_script_lang_js=NewShiftvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(setup_NewShiftvue_type_script_lang_js,NewShiftvue_type_template_id_20dd3346_render,NewShiftvue_type_template_id_20dd3346_staticRenderFns,!1,null,null,null)
/* harmony default export */,NewShift=component.exports,vuex_esm=__webpack_require__(420629),Shiftsvue_type_script_lang_js={components:{NewShift:NewShift},mounted:function(){this.model=this.getInitModel()},data:function(){return{isNew:!1,loading:!1,showDialog:!1,model:null}},watch:{showDialog:function(){this.showDialog||this.resetModel()}},created:function(){this.$store.dispatch("loadShifts")},computed:(0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({shifts:function(state){return state.shifts}})),methods:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapActions */.nv)(["loadShifts"])),{},{deleteShift:function(id,idx){var _this=this;this.$http.post("/shifts/delete?doValidation=true",{id:id}).then((function(response){if(response.data.result)if("failure"===response.data.result){var userList="<ul>";response.data.users.forEach((function(e){userList+="<li>".concat(e,"</li>")})),userList+="</ul>",_this.$dialog.confirm({title:"Delete Shift",htmlMessage:"Following Users work in this shift ".concat(userList," Do you want to proceed deleting ? <br/>"),rbDanger:!0,rbLabel:"Delete"}).then((function(confirmation){confirmation&&_this.$http.post("/shifts/delete?doValidation=false",{id:id}).then((function(response){"success"===response.data.result&&(_this.$message.success("Shift deleted successfully."),_this.$store.commit("GENERIC_DELETE",{type:"shifts",matches:idx}))}))}))}else"success"===response.data.result&&_this.$store.commit("GENERIC_DELETE",{type:"shifts",matches:idx})}))},getShifts:function(){var _this2=this;this.loading=!0,this.loadShifts().then((function(response){_this2.loading=!1}))},formatDays:function(val){return this.format(val).stringFmts},formatTimes:function(val){return this.format(val).tStringFmts},format:function(val){var isFirst=!0,days=[],curr=[];val.days.forEach((function(e){if(isFirst)curr.push(e),isFirst=!1;else{var c=curr[curr.length-1];c.startTime===e.startTime&&c.endTime===e.endTime?curr.push(e):(days.push(curr),curr=[e])}})),0!==curr.length&&days.push(curr);var lastPass=[];days.forEach((function(e){var c=[],isFirst=!0;e.forEach((function(a){isFirst?(c.push(a),isFirst=!1):c[c.length-1].dayOfWeek+1===a.dayOfWeek?c.push(a):(lastPass.push(c),c=[a])})),0!==c.length&&lastPass.push(c)}));var stringFmts=[],tStringFmts=[];return lastPass.forEach((function(e){tStringFmts.push(e[0].startTime+"-"+e[0].endTime),1===e.length?stringFmts.push(e[0].dayOfWeekVal):2===e.length?stringFmts.push(e[0].dayOfWeekVal+","+e[1].dayOfWeekVal):stringFmts.push(e[0].dayOfWeekVal+"-"+e[e.length-1].dayOfWeekVal)})),{stringFmts:stringFmts,tStringFmts:tStringFmts}},editShift:function(val){this.showDialog=!0,this.model=val,this.isNew=!1},newShift:function(val){this.showDialog=!0,this.isNew=!0},getInitModel:function(){return{name:"",siteId:null,isSameTime:!0,startTime:"09:00",endTime:"17:00",days:[{dayOfWeek:1,dayOfWeekVal:"MONDAY",startTime:"09:00",endTime:"17:00"},{dayOfWeek:2,dayOfWeekVal:"TUESDAY",startTime:"09:00",endTime:"17:00"},{dayOfWeek:3,dayOfWeekVal:"THURSDAY",startTime:"09:00",endTime:"17:00"},{dayOfWeek:4,dayOfWeekVal:"FRIDAY",startTime:"09:00",endTime:"17:00"},{dayOfWeek:5,dayOfWeekVal:"SATURDAY",startTime:"09:00",endTime:"17:00"}]}},resetModel:function(){this.model=this.getInitModel()}})},setup_Shiftsvue_type_script_lang_js=Shiftsvue_type_script_lang_js,Shifts_component=(0,componentNormalizer/* default */.Z)(setup_Shiftsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Shifts=Shifts_component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/32704.js.map