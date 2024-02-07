(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[85001],{
/***/685001:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Calendar}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/workorder/Calendar.vue?vue&type=template&id=5c54e24e
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"layout-padding scrollable",attrs:{id:"calendarpage"}},[_c("full-calendar",{ref:"calendar",staticClass:"calenderview fc-theme-color",staticStyle:{"padding-bottom":"100px"},attrs:{id:"calenderview",config:_vm.config,events:_vm.events},on:{"event-drop":_vm.eventDrop,"event-selected":_vm.eventSelected}}),_c("div",{staticStyle:{width:"192px","padding-top":"22px",position:"absolute",top:"20px","z-index":"1",right:"39px"},attrs:{id:"calendercolors"}},[_c("div",{staticStyle:{float:"left","border-left":"1px solid #979797",height:"20px",opacity:"0.2","margin-top":"4px"}}),_c("div",{staticClass:"flLeft",staticStyle:{"padding-left":"12px"}},[_vm._l(_vm.typeConfig[_vm.calendarType].options,(function(type,key,index){return[4===index&&_vm.optionsCount>5?[_c("div",{directives:[{name:"click-outside",rawName:"v-click-outside",value:_vm.closeDropdown,expression:"closeDropdown"}],key:key,class:["circle-inactive","colordropdown","flLeft",{active:_vm.showAllCircles}],on:{click:function($event){$event.stopPropagation(),_vm.showAllCircles=!_vm.showAllCircles}}},[_c("i",{staticClass:"el-icon-arrow-down"})]),_c("div",{key:"clear"+key,staticStyle:{clear:"both"}})]:_vm._e(),_c("div",{key:"color"+key,class:["colorContainer","colorTitlePosition"+_vm.currentColorTypes[key].colorcode,index<4||5===_vm.optionsCount?"flLeft":"circlemore",{active:_vm.showAllCircles}]},[_vm.currentColorTypes[key].active?_c("i",{class:["el-icon-circle-check","color"+_vm.currentColorTypes[key].colorcode],style:{"font-size":"27px",position:"relative"},on:{click:function($event){return _vm.filterCalendar(key)}}}):_c("div",{class:["circle-inactive","color"+_vm.currentColorTypes[key].colorcode],on:{click:function($event){return _vm.filterCalendar(key)}}}),_c("div",{staticStyle:{clear:"both"}}),_c("div",{class:["colorTitle","colorcard",index<4||5===_vm.optionsCount?"colorcard"+_vm.currentColorTypes[key].colorcode:"colormore"]},[_c("div",{staticClass:"typestyle"},[_vm._v(" "+_vm._s(_vm.typeConfig[_vm.calendarType].label)+" : ")]),_c("div",{staticClass:"colorcardtext"},[_vm._v(_vm._s(type))]),_c("div",{staticStyle:{clear:"both"}})])])]})),_c("div",{staticStyle:{clear:"both"}})],2),_c("el-popover",{ref:"typepopover",attrs:{placement:"bottom",width:"200",trigger:"click"},model:{value:_vm.showTypePopup,callback:function($$v){_vm.showTypePopup=$$v},expression:"showTypePopup"}},[_c("div",{staticStyle:{left:"10%",position:"relative","padding-bottom":"10px"}},[_c("div",{staticStyle:{margin:"4px 0 8px"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.calender.select_color_type"))+" ")]),_c("el-select",{staticClass:"mB5",on:{change:function($event){_vm.updateCalendarColor(),_vm.showTypePopup=!1}},model:{value:_vm.calendarType,callback:function($$v){_vm.calendarType=$$v},expression:"calendarType"}},_vm._l(_vm.typeConfig,(function(type,name){return type.views&&type.views.length&&!type.views.includes(_vm.currentView)?_vm._e():_c("el-option",{key:type.id,attrs:{label:type.label,value:name}})})),1)],1),_c("div",{staticClass:"flRight",staticStyle:{"margin-top":"6px","padding-left":"5px",cursor:"pointer"},attrs:{slot:"reference"},slot:"reference"},[_c("span",{staticClass:"more-overflow"}),_c("span",{staticClass:"more-overflow"}),_c("span",{staticClass:"more-overflow"})])]),_c("div",{staticStyle:{clear:"both"}})],1),_c("div",{staticClass:"popup1"},[_c("div",{staticClass:"dropdownarrow"},[_c("div",{staticClass:"dropdown",on:{click:function($event){return _vm.loadResources("staff")}}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.calender.staff"))+" ")]),_c("div",{staticClass:"dropdown",on:{click:function($event){return _vm.loadResources("team")}}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.calender.team"))+" ")])])]),_c("div",{staticClass:"popup2",staticStyle:{padding:"10px"}},[["asset","space"].includes(_vm.selectedResource)?[_vm._l(_vm.resourceConfig,(function(field){return(4!==field.spaceType||_vm.isAsset)&&field.options&&Object.keys(field.options).length?_c("div",{key:"resource"+field.spaceType,staticClass:"pB15"},[_c("div",{staticClass:"filtertitle"},[_vm._v(_vm._s(field.placeHolder))]),_c("el-select",{attrs:{filterable:"",clearable:""},on:{change:function($event){_vm.isEventLoading=!0,_vm.onLocationSelected(field)}},model:{value:field.value,callback:function($$v){_vm.$set(field,"value",$$v)},expression:"field.value"}},_vm._l(field.options,(function(option){return _c("el-option",{key:field.spaceType+"_"+option.id,attrs:{label:option.name,value:option.id}})})),1)],1):_vm._e()})),_vm.isAsset?[_c("div",{staticClass:"filtertitle"},[_vm._v(_vm._s(_vm.assetCategory.placeHolder))]),_c("el-select",{attrs:{filterable:"",clearable:""},on:{change:function($event){_vm.isEventLoading=!0,_vm.onCategorySelected()}},model:{value:_vm.assetCategory.value,callback:function($$v){_vm.$set(_vm.assetCategory,"value",$$v)},expression:"assetCategory.value"}},_vm._l(_vm.assetCategory.options,(function(label,value){return _c("el-option",{key:value,attrs:{label:label,value:value}})})),1)]:_vm._e()]:"staff"===_vm.selectedResource?[_c("div",{staticClass:"filtertitle"},[_vm._v(_vm._s(_vm.$t("maintenance.calender.team")))]),_c("el-select",{attrs:{filterable:"",clearable:""},on:{change:function($event){return _vm.onTeamSelected()}},model:{value:_vm.selectedTeam,callback:function($$v){_vm.selectedTeam=$$v},expression:"selectedTeam"}},_vm._l(_vm.groups,(function(group){return _c("el-option",{key:"group"+group.id,attrs:{label:group.name,value:group.id}})})),1)]:_vm._e()],2),_vm.isPlanned?_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.showPopover,expression:"showPopover"},{name:"click-outside",rawName:"v-click-outside",value:_vm.hide,expression:"hide"}],staticClass:"position",staticStyle:{width:"404px","min-height":"272px","background-color":"#fff","z-index":"1000",position:"absolute"},style:_vm.popoverStyle,attrs:{id:"eventpopover"}},[_c("div",{class:_vm.eventDetails.colortype,staticStyle:{"border-bottom":"1px solid #eaedf1","padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"}},[_c("div",[_c("span",{staticClass:"pull-right",staticStyle:{cursor:"pointer"}},[_vm.eventDetails.projected?_vm._e():[1===_vm.eventDetails.jobStatus||2===_vm.eventDetails.jobStatus?_c("el-dropdown",{attrs:{trigger:"click",size:"small"},on:{command:function($event){return _vm.updatePmJob($event,_vm.eventDetails,4)}}},[_vm.eventDetails.isMulti?_vm._e():_c("el-button",{staticClass:"roundbutton",attrs:{round:""}},[_vm._v(_vm._s(_vm.$t("maintenance.calender.turn_off")))]),_c("el-dropdown-menu",{attrs:{slot:"dropdown"},slot:"dropdown"},[_c("el-dropdown-item",{attrs:{command:"current"}},[_vm._v(_vm._s(_vm.$t("maintenance.calender.selected")))]),_c("el-dropdown-item",{attrs:{command:"all"}},[_vm._v("All")])],1)],1):4===_vm.eventDetails.jobStatus?_c("el-button",{staticClass:"roundbutton",attrs:{round:""},on:{click:function($event){return _vm.updatePmJob("current",_vm.eventDetails,1)}}},[_vm._v(_vm._s(_vm.$t("maintenance.calender.turn_on")))]):_vm._e()],_c("i",{staticClass:"el-icon-close",staticStyle:{"font-size":"18px","font-weight":"normal",color:"#fff","vertical-align":"middle"},on:{click:_vm.closeDialog}})],2),_c("div",{staticClass:"datestyle"},[_vm._v(_vm._s(_vm.eventDetails.executionTime))]),_c("div",{staticClass:"eventstyle"},[_vm._v(_vm._s(_vm.eventDetails.eventTitle))])])]),_c("div",{staticStyle:{"padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"}},[_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.type"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(_vm._s(_vm.eventDetails.type))]),_c("div",{staticStyle:{clear:"both"}})]),_vm.eventDetails.category?_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.space_asset"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.category)+" ")]),_c("div",{staticStyle:{clear:"both"}})]):_vm.eventDetails.resource?_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.space_asset"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.resource)+" ")]),_c("div",{staticStyle:{clear:"both"}})]):_vm._e(),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.assignedto"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_c("span",[_vm._v(_vm._s(_vm.eventDetails.assignmentGroup?_vm.eventDetails.assignmentGroup:"---")+" /")]),_c("span",[_vm._v(_vm._s(_vm.eventDetails.assignedTo?_vm.eventDetails.assignedTo:"---"))])]),_c("div",{staticStyle:{clear:"both"}})]),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance._workorder.frequency"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.frequency)+" ")]),_c("div",{staticStyle:{clear:"both"}})]),_vm.eventDetails.colorTypeValue?_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.typeConfig[_vm.calendarType].label)+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.colorTypeValue)+" ")]),_c("div",{staticStyle:{clear:"both"}})]):_vm._e()]),_c("div",{staticStyle:{cursor:"pointer","padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"},on:{click:function($event){return _vm.opensummary(_vm.eventDetails)}}},[_c("span",{staticClass:"moredetails"},[_vm._v(_vm._s(_vm.$t("maintenance.calender.view_details")))])])]):_vm.isPlanned?_vm._e():_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.showPopover,expression:"showPopover"},{name:"click-outside",rawName:"v-click-outside",value:_vm.hide,expression:"hide"}],staticClass:"position",staticStyle:{width:"404px","min-height":"272px","background-color":"#fff","z-index":"1",position:"absolute"},style:_vm.popoverStyle,attrs:{id:"eventpopover"}},[_c("div",{class:_vm.eventDetails.colortype,staticStyle:{"border-bottom":"1px solid #eaedf1","padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"}},[_c("div",[_c("span",{staticClass:"pull-right",staticStyle:{cursor:"pointer"},on:{click:_vm.closeDialog}},[_c("i",{staticClass:"el-icon-close",staticStyle:{"font-size":"18px","font-weight":"normal"}})]),_c("div",{staticClass:"datestyle"},[_vm._v(_vm._s(_vm.eventDetails.executionTime))]),_c("div",{staticClass:"eventstyle"},[_vm._v(_vm._s(_vm.eventDetails.eventTitle))])])]),_c("div",{staticStyle:{"padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"}},[_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.type"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(_vm._s(_vm.eventDetails.type))]),_c("div",{staticStyle:{clear:"both"}})]),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.space_asset"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.resource)+" ")]),_c("div",{staticStyle:{clear:"both"}})]),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.assignedto"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_c("span",[_vm._v(_vm._s(_vm.eventDetails.assignmentGroup?_vm.eventDetails.assignmentGroup:"---")+" /")]),_c("span",[_vm._v(_vm._s(_vm.eventDetails.assignedTo?_vm.eventDetails.assignedTo:"---"))])]),_c("div",{staticStyle:{clear:"both"}})]),_c("div",{staticClass:"pT20"},[_c("div",{staticClass:"subheader",staticStyle:{width:"100px",float:"left"}},[_vm._v(" "+_vm._s(_vm.$t("maintenance.wr_list.category"))+" ")]),_c("div",{staticClass:"intext",staticStyle:{float:"left"}},[_vm._v(" "+_vm._s(_vm.eventDetails.category)+" ")]),_c("div",{staticStyle:{clear:"both"}})])]),_c("div",{staticStyle:{cursor:"pointer","padding-top":"20px","padding-left":"30px","padding-right":"30px","padding-bottom":"20px"},on:{click:function($event){return _vm.opensummary(_vm.eventDetails)}}},[_c("span",{staticClass:"moredetails"},[_vm._v(_vm._s(_vm.$t("maintenance.calender.more_details")))])])]),_c("div",{staticStyle:{position:"absolute",top:"50%",left:"47.5%","z-index":"5"}},[_c("spinner",{attrs:{show:_vm.isEventLoading,size:"70"}})],1)],1)},staticRenderFns=[],defineProperty=__webpack_require__(482482),esm_typeof=__webpack_require__(103336),toConsumableArray=__webpack_require__(488478),regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),objectSpread2=__webpack_require__(595082),vue_runtime_esm=(__webpack_require__(434284),__webpack_require__(976801),__webpack_require__(843843),__webpack_require__(564043),__webpack_require__(61514),__webpack_require__(169358),__webpack_require__(857267),__webpack_require__(450886),__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(538077),__webpack_require__(506203),__webpack_require__(322462),__webpack_require__(634338),__webpack_require__(670560),__webpack_require__(425728),__webpack_require__(879288),__webpack_require__(648324),__webpack_require__(865137),__webpack_require__(76265),__webpack_require__(720144)),FullCalendarvue_type_template_id_41e9886e_render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"calendar",attrs:{id:"calendar"}})},FullCalendarvue_type_template_id_41e9886e_staticRenderFns=[],lodash_defaultsdeep=__webpack_require__(892098),lodash_defaultsdeep_default=__webpack_require__.n(lodash_defaultsdeep),jquery=(__webpack_require__(868672),__webpack_require__(619755)),jquery_default=__webpack_require__.n(jquery),FullCalendarvue_type_script_lang_js={props:{events:{default(){return[]}},eventSources:{default(){return[]}},editable:{default(){return!0}},selectable:{default(){return!0}},selectHelper:{default(){return!0}},header:{default(){return{left:"prev,next today",center:"title",right:"month,agendaWeek,agendaDay"}}},defaultView:{default(){return"agendaWeek"}},sync:{default(){return!1}},config:{type:Object,default(){return{}}}},computed:{defaultConfig(){const self=this;return{header:this.header,defaultView:this.defaultView,editable:this.editable,selectable:this.selectable,selectHelper:this.selectHelper,aspectRatio:2,timeFormat:"HH:mm",events:this.events,eventSources:this.eventSources,eventRender(...args){this.sync&&(self.events=cal.fullCalendar("clientEvents")),self.$emit("event-render",...args)},viewRender(...args){this.sync&&(self.events=cal.fullCalendar("clientEvents")),self.$emit("view-render",...args)},eventDestroy(event){this.sync&&(self.events=cal.fullCalendar("clientEvents"))},eventClick(...args){self.$emit("event-selected",...args)},eventMouseover(...args){self.$emit("event-mouseover",...args)},eventMouseout(...args){self.$emit("event-mouseout",...args)},eventDrop(...args){self.$emit("event-drop",...args)},eventReceive(...args){self.$emit("event-receive",...args)},eventResize(...args){self.$emit("event-resize",...args)},dayClick(...args){self.$emit("day-click",...args)},select(start,end,jsEvent,view,resource){self.$emit("event-created",{start:start,end:end,allDay:!start.hasTime()&&!end.hasTime(),view:view,resource:resource})}}}},mounted(){const cal=jquery_default()(this.$el);this.$on("remove-event",(event=>{event&&event.hasOwnProperty("id")?jquery_default()(this.$el).fullCalendar("removeEvents",event.id):jquery_default()(this.$el).fullCalendar("removeEvents",event)})),this.$on("rerender-events",(()=>{jquery_default()(this.$el).fullCalendar("rerenderEvents")})),this.$on("refetch-events",(()=>{jquery_default()(this.$el).fullCalendar("refetchEvents")})),this.$on("render-event",(event=>{jquery_default()(this.$el).fullCalendar("renderEvent",event)})),this.$on("reload-events",(()=>{jquery_default()(this.$el).fullCalendar("removeEvents"),jquery_default()(this.$el).fullCalendar("addEventSource",this.events)})),this.$on("rebuild-sources",(()=>{jquery_default()(this.$el).fullCalendar("removeEventSources"),this.eventSources.map((event=>{jquery_default()(this.$el).fullCalendar("addEventSource",event)}))})),cal.fullCalendar(lodash_defaultsdeep_default()(this.config,this.defaultConfig))},methods:{fireMethod(...options){return jquery_default()(this.$el).fullCalendar(...options)}},watch:{events:{deep:!0,handler(val){jquery_default()(this.$el).fullCalendar("removeEvents"),jquery_default()(this.$el).fullCalendar("addEventSource",this.events)}},eventSources:{deep:!0,handler(val){this.$emit("rebuild-sources")}}},beforeDestroy(){this.$off("remove-event"),this.$off("rerender-events"),this.$off("refetch-events"),this.$off("render-event"),this.$off("reload-events"),this.$off("rebuild-sources")}},components_FullCalendarvue_type_script_lang_js=FullCalendarvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FullCalendarvue_type_script_lang_js,FullCalendarvue_type_template_id_41e9886e_render,FullCalendarvue_type_template_id_41e9886e_staticRenderFns,!1,null,null,null)
/* harmony default export */,FullCalendar=component.exports,vuex_esm=__webpack_require__(420629),ResourceMixin=__webpack_require__(22637),moment_timezone=__webpack_require__(480008),moment_timezone_default=__webpack_require__.n(moment_timezone),picklist=__webpack_require__(961358),router=__webpack_require__(329435),validation=__webpack_require__(990260),api=__webpack_require__(32284),Calendarvue_type_script_lang_js={title:function(){return this.$options.filters.viewName(this.currentView)},mixins:[ResourceMixin/* default */.Z],data:function(){return{spaces:[],assets:[],showPopover:!1,dontHide:!1,popoverStyle:{top:"0px",left:"0px"},isEventLoading:!1,eventDetails:{eventTitle:null,executionTime:null,type:null,asset:null,space:null,assignedTo:null,frequency:null,jobStatus:null,pmId:-1},events:[],eventList:{},triggers:{},selectedResource:"staff",selectedTeam:null,resourceList:[],config:{viewRender:function(view){var name=jquery_default()(".calenderview").data("resourceName"),field=jquery_default()(".calenderview").data("resourceField");0===jquery_default()(".calenderview .fc-resource-area .fc-widget-header #filtericons").length&&jquery_default()(".calenderview .fc-resource-area .fc-widget-header").append('<div id="filtericons"><i id="dropdownid" class="el-icon-arrow-down" style="position:absolute;bottom: 3px;z-index:1000;right: 72px;cursor: pointer;"></i><img src ="'+__webpack_require__(684890)+' " id="filterid" style="cursor: pointer;width: 16px;height:15px;transform: rotate(90deg);position: absolute;z-index:1000;right: 15px;top: -18px;"></div>'),"team"===field&&jquery_default()(".calenderview #filterid").hide(),jquery_default()(".fc-resource-area .fc-widget-header .fc-cell-text").text(name||"Staff")},eventRender:function(event,element){var isYearView="timelineYear"===jquery_default()("#calenderview").fullCalendar("getView").name&&event.frequencyText,html='<div id="'+event.key+'" bgcolortype="'+event.colortype+'" class="fc-content eventcontainer bdcolor'+event.colortype+(isYearView?" pm-year":"")+'"><span class="fc-time">';html+=isYearView?event.frequencyText+"</span>":event.starttime+'</span> <span class="fc-title">'+event.title+"</span></div>",element.html(html)},header:{left:"today,prev,next title",center:"",right:"timelineDay,timelineWeek,timelineMonth,timelineYear,agendaDay,agendaWeek,month"},buttonText:{today:this.$t("maintenance.calender.go_to_today"),month:this.$t("maintenance.planner.views.MONTH"),week:this.$t("maintenance.planner.views.WEEK"),day:this.$t("maintenance.planner.views.DAY")},views:{timelineYear:{type:"timeline",duration:{year:1},resourceAreaWidth:"15%",slotDuration:{weeks:1},slotLabelFormat:["ww"],slotWidth:30},timelineMonth:{type:"timeline",duration:{month:1},resourceAreaWidth:"15%",slotLabelFormat:["DD"],slotWidth:30},timelineWeek:{type:"timeline",duration:{weeks:1},slotDuration:{days:1},resourceAreaWidth:"15%",slotLabelFormat:["ddd M/D"]},timelineDay:{type:"timeline",duration:{days:1},resourceAreaWidth:"15%",slotMinutes:60,slotDuration:"01:00:00",slotLabelInterval:"04:00:00"},agendaDay:{resources:!1},basicWeek:{buttonText:"BASIC WEEK"},basicDay:{buttonText:"BASIC DAY",resources:!1}},eventLimit:!0,navLinks:!0,selectable:!0,selectHelper:!0,editable:!0,schedulerLicenseKey:"GPL-My-Project-Is-Open-Source",defaultView:"pmplanner"===this.$route.params.viewname||"woplanner"===this.$route.params.viewname?"timelineMonth":"month",fixedWeekCount:!1,resources:[],eventAllow:function(dropLocation,draggedEvent){return!draggedEvent.resourceId.includes("_")},locale:this.$i18n.locale},calendarType:"frequency",typeConfig:{typeId:{label:this.$t("maintenance.wr_list.type"),options:{},wokey:"type",noneLabel:"Others"},categoryId:{label:this.$t("maintenance.wr_list.category"),options:{},wokey:"category",noneLabel:"Others"},statusId:{label:this.$t("maintenance._workorder.status"),options:{},wokey:"status",views:["woplanner","workorder"]},priorityId:{label:this.$t("maintenance.wr_list.priority"),options:{},wokey:"priority",noneLabel:"Others"},frequency:{label:this.$t("maintenance._workorder.frequency"),options:{},views:["pmplanner","planned"]}},currentColorTypes:{"-1":{colorcode:1,active:!0}},eventColors:["#ffccc2","#ffedb3","#cdefdb","#d9d3ee","#f9c9db","#b9ede9","#ffddba","#faf5cb","#e7f3cf","#bfd7e1"],showTypePopup:!1,showAllCircles:!1,frequencyText:{0:"O",1:"D",2:"W",3:"M",4:"Q",5:"H",6:"A"}}},filters:{viewName:function(name){return"planned"===name?"Planned Maintenance":"pmplanner"===name?"PM Planner":"workorder"===name?"Work Order":"woplanner"===name?"Work Order Planner":void 0}},created:function(){this.$store.dispatch("loadTicketCategory"),this.$store.dispatch("loadTicketType"),this.$store.dispatch("loadTicketStatus","workorder"),this.$store.dispatch("loadAssetCategory"),this.$store.dispatch("loadSpaceCategory"),this.$store.dispatch("loadGroups"),this.$store.dispatch("loadUsers")},computed:(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},(0,vuex_esm/* mapState */.rn)({groups:function(state){return state.groups},ticketstatus:function(state){return state.ticketStatus.workorder},assetcategory:function(state){return state.assetCategory},users:function(state){return state.users}})),(0,vuex_esm/* mapGetters */.Se)(["getTicketTypePickList","getSpaceCategoryPickList","getGroup","getTicketCategory"])),{},{tickettype:function(){return this.getTicketTypePickList()},spacecategory:function(){return this.getSpaceCategoryPickList()},filters:function(){return this.$route.query.search?JSON.parse(this.$route.query.search):null},currentView:function(){return this.$route.params.viewname},typeFieldName:function(){return this.typeConfig[this.calendarType].hasOwnProperty("key")?this.typeConfig[this.calendarType].key:this.calendarType},optionsCount:function(){return Object.keys(this.typeConfig[this.calendarType].options).length},resourceview:function(){return"pmplanner"===this.currentView||"woplanner"===this.currentView},isPlanned:function(){return"planned"===this.currentView||"pmplanner"===this.currentView},isPmV2:function(){return this.$helpers.isLicenseEnabled("PM_PLANNER")},moduleName:function(){return this.isPlanned?"planned":"workorder"},calType:function(){return this.isPlanned?"":this.$route.query.type||"dueDate"},isNewCode:function(){return"old"!==this.$route.query.code}}),components:{"full-calendar":FullCalendar},watch:{filters:function(val){this.resetCircle(),this.loadEvents()},currentView:function(newVal,oldVal){oldVal!==newVal&&(this.loadEvents(),this.setTitle(this.$options.filters.viewName(newVal))),this.configHeaders(),this.registerEvents(),this.resourceview&&this.reInitResourceData()},calendarType:function(){this.setCurrentColorType(),this.filters?this.$router.replace({query:{}}):this.loadEvents()},assetList:function(assets){if("asset"===this.selectedResource){if(!assets||!assets.length)return;var list=assets.map((function(asset){return{id:asset.id,title:asset.name}})),resourceMap={};this.isPlanned?(this.events&&this.events.forEach((function(i){resourceMap[i.space]=!0,resourceMap[i.asset]=!0})),list&&(this.resourceList=list.filter((function(i){return resourceMap[i.id]})))):this.resourceList=list,this.sortAndSetResources()}},spaceList:function(list){if("space"===this.selectedResource){if(!list||!list.length)return;var l=list.map((function(space){return{id:space.id,title:space.name}})),resourceMap={};this.events&&this.events.forEach((function(i){resourceMap[i.space]=!0,resourceMap[i.asset]=!0})),list&&(this.resourceList=l.filter((function(i){return resourceMap[i.id]}))),this.sortAndSetResources()}},calType:function(newVal,oldVal){oldVal!==newVal&&this.loadEvents()}},mounted:function(){vue_runtime_esm["default"].use(FullCalendar),this.setFilterTypeOptions(),this.configHeaders(),this.loadEvents(),this.registerEvents()},methods:{loadSpaceList:function(){var l=this.spaces.map((function(space){return{id:space.id,title:space.name}})),resourceMap={};this.events&&this.events.forEach((function(i){resourceMap[i.space]=!0,resourceMap[i.asset]=!0})),l&&(this.resourceList=l.filter((function(i){return resourceMap[i.id]}))),this.sortAndSetResources()},loadAssetList:function(){var list=this.assets.map((function(asset){return{id:asset.id,title:asset.name}})),resourceMap={};this.events&&this.events.forEach((function(i){resourceMap[i.space]=!0,resourceMap[i.asset]=!0})),list&&(this.resourceList=list.filter((function(i){return resourceMap[i.id]}))),this.sortAndSetResources()},handleScroll:function(){jquery_default()("#calendercolors").css({top:20+jquery_default()("#calenderview").position().top})},configHeaders:function(){this.resourceview?("agendaWeek"===jquery_default()("#calenderview").fullCalendar("getView").name?jquery_default()("#calenderview").fullCalendar("changeView","timelineWeek"):"agendaDay"===jquery_default()("#calenderview").fullCalendar("getView").name?jquery_default()("#calenderview").fullCalendar("changeView","timelineDay"):jquery_default()("#calenderview").fullCalendar("changeView","timelineMonth"),this.loadResources("staff"),jquery_default()("#calenderview .fc-month-button").hide(),jquery_default()("#calenderview .fc-agendaWeek-button").hide(),jquery_default()("#calenderview .fc-agendaDay-button").hide(),jquery_default()("#calenderview .fc-timelineYear-button").show(),jquery_default()("#calenderview .fc-timelineMonth-button").show(),jquery_default()("#calenderview .fc-timelineWeek-button").show(),jquery_default()("#calenderview .fc-timelineDay-button").show()):("timelineWeek"===jquery_default()("#calenderview").fullCalendar("getView").name?jquery_default()("#calenderview").fullCalendar("changeView","agendaWeek"):"timelineDay"===jquery_default()("#calenderview").fullCalendar("getView").name?jquery_default()("#calenderview").fullCalendar("changeView","agendaDay"):jquery_default()("#calenderview").fullCalendar("changeView","month"),jquery_default()("#calenderview .fc-timelineYear-button").hide(),jquery_default()("#calenderview .fc-timelineMonth-button").hide(),jquery_default()("#calenderview .fc-timelineWeek-button").hide(),jquery_default()("#calenderview .fc-timelineDay-button").hide(),jquery_default()("#calenderview .fc-month-button").show(),jquery_default()("#calenderview .fc-agendaWeek-button").show(),jquery_default()("#calenderview .fc-agendaDay-button").show())},getType:function(template){var isPmV2=this.isPmV2,_ref=template||{},typeId=_ref.typeId;return isPmV2?this.$getProperty(template,"type","---"):typeId>0?this.getPMType(typeId)||"---":void 0},eventSelected:function(calEvent,jsEvent,view){var _this=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var eventList,_ref2,job,pmId,pmObj,_ref3,woTemplate,_ref4,typeId,template,asset,_yield$getFieldValue,error,data,value,_ref5,type,_asset,space,resource,assignedTo,assignmentGroup,assignmentGroupId,category,id,assetValue,_yield$getFieldValue2,_error,_data;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:if(!_this.isPlanned){_context.next=19;break}if(eventList=_this.eventList,_ref2=calEvent||{},job=_ref2.job,pmId=_ref2.pmId,pmObj=eventList[pmId]||{},_ref3=pmObj||{},woTemplate=_ref3.woTemplate,_ref4=woTemplate||{},typeId=_ref4.typeId,template=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},job),{},{typeId:typeId}),asset="---",!(template.assetId>0)){_context.next=15;break}return _context.next=11,(0,picklist/* getFieldValue */.oe)({lookupModuleName:"asset",selectedOptionId:[template.assetId]});case 11:_yield$getFieldValue=_context.sent,error=_yield$getFieldValue.error,data=_yield$getFieldValue.data,error?_this.$message.error(error.message||"Error Occured"):asset=_this.$getProperty(data,"0.label","---");case 15:_this.eventDetails={isMulti:calEvent.isMulti,eventTitle:calEvent.title,executionTime:calEvent.startdate,type:_this.getType(template),asset:asset,space:template.spaceId>0?_this.$store.getters.getSpace(template.spaceId).displayName:"---",resource:template.resourceId>0&&template.resource?template.resource.name:"---",assignedTo:template.assignedToId>0?_this.$store.getters.getUser(template.assignedToId).name:"",assignmentGroup:template.assignmentGroupId>0?_this.getGroup(template.assignmentGroupId).name:"",frequency:_this.triggers[calEvent.job.pmTriggerId].scheduleMsg,id:calEvent.id,colortype:"bgcolor"+calEvent.colortype,jobId:calEvent.job.id,pmId:calEvent.pmId,jobStatus:calEvent.job.status,projected:calEvent.job.projected,category:calEvent.category},"typeId"!==_this.calendarType&&"frequency"!==_this.calendarType&&(value=_this.eventList[calEvent.pmId][_this.calendarType]||template[_this.calendarType]||-1,_this.eventDetails.colorTypeValue=-1!==value?_this.typeConfig[_this.calendarType].options[value]:"---"),_context.next=29;break;case 19:if(_ref5=_this.eventList[calEvent.key]||{},type=_ref5.type,_asset=_ref5.asset,space=_ref5.space,resource=_ref5.resource,assignedTo=_ref5.assignedTo,assignmentGroup=_ref5.assignmentGroup,assignmentGroupId=_ref5.assignmentGroupId,category=_ref5.category,id=_ref5.id,assetValue="---",!(_asset&&_asset.id>0)){_context.next=28;break}return _context.next=24,(0,picklist/* getFieldValue */.oe)({lookupModuleName:"asset",selectedOptionId:[_asset.id]});case 24:_yield$getFieldValue2=_context.sent,_error=_yield$getFieldValue2.error,_data=_yield$getFieldValue2.data,_error?_this.$message.error(_error.message||"Error Occured"):_asset=_this.$getProperty(_data,"0.label","---");case 28:_this.eventDetails={eventTitle:calEvent.title,executionTime:calEvent.startdate,type:type&&type.id>0?_this.getPMType(type.id):"---",asset:assetValue,space:space&&space.id>0?_this.$store.getters.getSpace(space.id).displayName:"---",resource:resource&&resource.id>0?resource.name:"---",assignedTo:assignedTo&&assignedTo.id>0?_this.$store.getters.getUser(assignedTo.id).name:"",assignmentGroup:assignmentGroup&&assignmentGroupId>0?_this.getGroup(assignmentGroupId).name:"",category:category&&category.id>0?_this.getTicketCategory(category.id).displayName:"---",id:id,colortype:"bgcolor"+calEvent.colortype};case 29:jquery_default()("a[class*='eventselected']").removeClass((function(index,css){return(css.match(/(^|\s)eventselected\S+/g)||[]).join(" ")})),calEvent.resourceview?jquery_default()("#"+calEvent.key).parent().addClass("eventselected00").addClass("eventselected"+jquery_default()("#"+calEvent.key).attr("bgcolortype")):jquery_default()("#"+calEvent.key).parent().addClass("eventselected0").addClass("eventselected"+jquery_default()("#"+calEvent.key).attr("bgcolortype")),_this.showPopover=!0,_this.dontHide=!0,_this.$nextTick((function(){var appHeight=jquery_default()("#q-app").height(),menuWidth=jquery_default()("#eventpopover").width(),menuHeight=jquery_default()("#eventpopover").height()+40,reSize=jsEvent.pageY-menuHeight/2;appHeight<=reSize+menuHeight&&(reSize=jsEvent.pageY-menuHeight-50);var borderHeight=reSize-50;borderHeight<=120&&(borderHeight=120);var menuVisX=jsEvent.pageX>jquery_default()(window).width()/2?jsEvent.pageX-menuWidth-100:jsEvent.pageX,menuVisY=borderHeight;_this.popoverStyle.left=menuVisX+"px",_this.popoverStyle.top=menuVisY+"px"}));case 34:case"end":return _context.stop()}}),_callee)})))()},eventDrop:function(calEvent){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee2(){var self,params,_yield$API$post,error,_params;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context2){while(1)switch(_context2.prev=_context2.next){case 0:if(!_this2.isPlanned){_context2.next=13;break}if(-1===calEvent.job.id){_context2.next=11;break}return self=_this2,params={id:[calEvent.job.id],pmJob:{nextExecutionTime:self.$helpers.getDateInOrg(calEvent.start.format()).unix()},pmId:calEvent.pmId},_this2.$helpers.isLicenseEnabled("SCHEDULED_WO")&&(params={id:[calEvent.job.id],pmJob:{nextExecutionTime:1e3*self.$helpers.getDateInOrg(calEvent.start.format()).unix()},pmId:calEvent.pmId}),"staff"!==_this2.selectedResource&&"team"!==_this2.selectedResource||(params.resourceId=calEvent.resourceId,params.resourceType=_this2.selectedResource),_context2.next=8,api/* API */.bl.post("/workorder/updatePreventiveMaintenanceJob",params);case 8:_yield$API$post=_context2.sent,error=_yield$API$post.error,error||(_this2.$message.success(_this2.$t("maintenance.pm.job_updated")),_this2.loadEvents());case 11:_context2.next=16;break;case 13:_params={id:[calEvent.key],fields:{dueDate:_this2.$helpers.getTimeInOrg(calEvent.start.format())}},"staff"===_this2.selectedResource?_params.fields.assignedTo={id:calEvent.resourceId}:"team"===_this2.selectedResource?_params.fields.assignmentGroup={id:calEvent.resourceId}:_params.fields.resource={id:calEvent.resourceId},_this2.$store.dispatch("workorder/updateWorkOrder",_params).then((function(response){_this2.$message.success("Workorder updated successfully!"),_this2.loadEvents()})).catch((function(){_this2.$message.error("Workorder updation failed")}));case 16:case"end":return _context2.stop()}}),_callee2)})))()},getPMType:function(type){return this.tickettype[type]},removeResources:function(){for(var prevResources=jquery_default()("#calenderview").fullCalendar("getResources"),i=0;i<prevResources.length;i++)jquery_default()("#calenderview").fullCalendar("removeResource",prevResources[i].id)},loadResources:function(resource){var name;if(this.isEventLoading=!0,this.removeResources(),this.selectedResource=resource,"staff"===resource)name="Staff",this.resourceList=[{id:-1,title:"Unassigned"}].concat((0,toConsumableArray/* default */.Z)(this.users.map((function(user){return{id:user.id,title:user.name}})))),"staff"!==this.selectedResource&&(this.selectedTeam=null);else if("team"===resource){name="Team";var groups=this.groups;this.resourceList=[{id:-1,title:"Unassigned"}];for(var i=0;i<groups.length;i++){this.resourceList.push({id:groups[i].id,title:groups[i].name,children:[]});for(var j=0;j<groups[i].members.length;j++)this.resourceList[i+1].children.push({id:groups[i].id+"_"+groups[i].members[j].memberId,title:groups[i].members[j].name})}}else"asset"===resource?(name="Asset",this.isAsset=!0,this.loadAssetList()):"space"===resource&&(name="Space",this.isAsset=!1,this.loadSpaceList());"team"!==resource?jquery_default()(".calenderview #filterid").show():jquery_default()(".calenderview #filterid").hide(),jquery_default()(".fc-resource-area .fc-widget-header .fc-cell-text").text(name),jquery_default()(".calenderview").data("resourceName",name),jquery_default()(".calenderview").data("resourceField",this.selectedResource),"asset"!==resource&&"space"!==resource&&this.sortAndSetResources()},onTeamSelected:function(){var _this3=this;if(jquery_default()("#calendarpage .popup2").removeClass("showpopup2"),this.selectedTeam){this.removeResources();var users=this.groups.find((function(group){return group.id===_this3.selectedTeam})).members;this.resourceList=[];for(var i=0;i<users.length;i++)this.resourceList[i]={id:users[i].id,title:users[i].name};this.sortAndSetResources()}else this.loadResources("staff")},loadGroupedEvents:function(response){return this.$helpers.isLicenseEnabled("SCHEDULED_WO")?this.loadNewGroupedEvents(response):this.loadOldGroupedEvents(response)},loadNewGroupedEvents:function(response){var _this4=this;if(!response.data.pmTriggerTimeBasedGroupedMap)return[];var events=[],timeGroups=response.data.pmTriggerTimeBasedGroupedMap,pmMap=response.data.pmMap||{},triggerMap=response.data.pmTriggerMap||{},dateFormat="YYYY-MM-DD HH:mm";return Object.keys(timeGroups).forEach((function(nextExecutionTime){var trigJobs=timeGroups[nextExecutionTime];Object.keys(trigJobs).forEach((function(triggerId,i){var jobs=trigJobs[triggerId],trigger=triggerMap[triggerId],pm=pmMap[trigger.pmId],template=pm.woTemplate,category=null,resources=jobs.map((function(x){return x.resource})),length=resources?resources.length:0;if(pm.pmCreationType&&2===pm.pmCreationType&&pm.assignmentType)if(1===pm.assignmentType)length&&(category="".concat(length," Floors"));else if(4===pm.assignmentType&&pm.assetCategoryId&&pm.assetCategoryId>0&&_this4.assetcategory){var cat=_this4.assetcategory.find((function(category){return category.id===pm.assetCategoryId}));cat&&length&&(category="".concat(length," ").concat(cat.name))}else if(3===pm.assignmentType&&pm.spaceCategoryId&&pm.spaceCategoryId>0&&_this4.spacecategory){var _cat=_this4.spacecategory[pm.spaceCategoryId];_cat&&length&&(category="".concat(length," ").concat(_cat.name))}var next=Number(nextExecutionTime);if(!(4===jobs[0].status&&next<_this4.$options.filters.now())){var event={isMulti:!0,key:"event-multi"+i,jobId:null,pmId:pm.id,title:_this4.$getProperty(jobs[0],"woSubject","---"),start:_this4.$options.filters.toDateFormat(next,dateFormat),end:_this4.$options.filters.toDateFormat(Number(nextExecutionTime)+1800,dateFormat),asset:template.resourceId,space:template.resourceId,team:template.assignmentGroupId,staff:template.assignedToId,starttime:_this4.$options.filters.toDateFormat(next,"HH:mm"),startdate:_this4.$options.filters.toDateFormat(next,dateFormat),pmfrequency:pm.frequency,frequencyText:_this4.frequencyText[pm.frequency],resourceview:_this4.resourceview,job:jobs[0],assignmentType:pm.assignmentType,category:category,constraint:{start:_this4.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:_this4.$options.filters.addDays(new Date,61,!0)}};_this4.$getProperty(event,"job.pmTriggerId")&&(event.frequency=_this4.$getProperty(_this4.triggers,"".concat(event.job.pmTriggerId,".frequency"),-1)),event.frequency||(event.frequency=-1),_this4.setPMEventOptions(event),events.push(event)}}))})),events},loadOldGroupedEvents:function(response){var _this5=this;if(!response.data.pmTriggerTimeBasedGroupedMap)return[];var events=[],timeGroups=response.data.pmTriggerTimeBasedGroupedMap,pmMap=response.data.pmMap||{},triggerMap=response.data.pmTriggerMap||{},dateFormat="YYYY-MM-DD HH:mm";return Object.keys(timeGroups).forEach((function(nextExecutionTime){var trigJobs=timeGroups[nextExecutionTime];Object.keys(trigJobs).forEach((function(triggerId,i){var jobs=trigJobs[triggerId],trigger=triggerMap[triggerId],pm=pmMap[trigger.pmId],template=pm.woTemplate,category=null,resources=jobs.map((function(x){return x.resource})),length=resources?resources.length:0;if(pm.pmCreationType&&2===pm.pmCreationType&&pm.assignmentType)if(1===pm.assignmentType)length&&(category="".concat(length," Floors"));else if(4===pm.assignmentType&&pm.assetCategoryId&&pm.assetCategoryId>0&&_this5.assetcategory){var cat=_this5.assetcategory.find((function(category){return category.id===pm.assetCategoryId}));cat&&length&&(category="".concat(length," ").concat(cat.name))}else if(3===pm.assignmentType&&pm.spaceCategoryId&&pm.spaceCategoryId>0&&_this5.spacecategory){var _cat2=_this5.spacecategory[pm.spaceCategoryId];_cat2&&length&&(category="".concat(length," ").concat(_cat2.name))}var next=1e3*Number(nextExecutionTime);if(!(4===jobs[0].status&&next<_this5.$options.filters.now())){var event={isMulti:!0,key:"event-multi"+i,jobId:null,pmId:pm.id,title:_this5.$getProperty(jobs[0],"woSubject","---"),start:_this5.$options.filters.toDateFormat(next,dateFormat),end:_this5.$options.filters.toDateFormat(1e3*(Number(nextExecutionTime)+1800),dateFormat),asset:template.resourceId,space:template.resourceId,team:template.assignmentGroupId,staff:template.assignedToId,starttime:_this5.$options.filters.toDateFormat(next,"HH:mm"),startdate:_this5.$options.filters.toDateFormat(next,dateFormat),pmfrequency:pm.frequency,frequencyText:_this5.frequencyText[pm.frequency],resourceview:_this5.resourceview,job:jobs[0],assignmentType:pm.assignmentType,category:category,constraint:{start:_this5.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:_this5.$options.filters.addDays(new Date,61,!0)}};_this5.setPMEventOptions(event),events.push(event)}}))})),events},loadEvents:function(){var _this6=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee3(){var isPmV2;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context3){while(1)switch(_context3.prev=_context3.next){case 0:if(isPmV2=_this6.isPmV2,!isPmV2){_context3.next=6;break}return _context3.next=4,_this6.loadPmV2Events();case 4:_context3.next=7;break;case 6:_this6.$helpers.isLicenseEnabled("SCHEDULED_WO")?_this6.loadNewEvents():_this6.loadOldEvents();case 7:case"end":return _context3.stop()}}),_callee3)})))()},loadPmV2Events:function(){var _this7=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee4(){var $timezone,$options,currentView,filters,resourceview,frequencyText,isPlanned,startTime,endTime,url,params,_ref6,frequency,_yield$API$get,error,data,_ref7,result,_ref8,_ref8$plannedmaintena,pmJobList,pmMap,pmTriggerMap,dateFormat,_this7$events,groupedEvents;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context4){while(1)switch(_context4.prev=_context4.next){case 0:return _this7.isEventLoading=!0,_this7.events=[],$timezone=_this7.$timezone,$options=_this7.$options,currentView=_this7.currentView,filters=_this7.filters,resourceview=_this7.resourceview,frequencyText=_this7.frequencyText,isPlanned=_this7.isPlanned,startTime=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").start).tz($timezone).startOf("day").valueOf()/1e3,endTime=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").end).tz($timezone).startOf("day").valueOf()/1e3,url="v3/workorder/calender/getPpmJobs",params={startTime:startTime,endTime:endTime,currentView:currentView},_ref6=filters||{},frequency=_ref6.frequency,(0,validation/* isEmpty */.xb)(filters)||(params=(0,validation/* isEmpty */.xb)(frequency)?(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},params),{},{filters:encodeURIComponent(JSON.stringify(filters))}):(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},params),{},{frequencyFilter:encodeURIComponent(JSON.stringify(filters))})),_context4.next=11,api/* API */.bl.get(url,params);case 11:_yield$API$get=_context4.sent,error=_yield$API$get.error,data=_yield$API$get.data,(0,validation/* isEmpty */.xb)(error)&&(_ref7=data||{},result=_ref7.result,_ref8=result||{},_ref8$plannedmaintena=_ref8.plannedmaintenanceJobList,pmJobList=void 0===_ref8$plannedmaintena?[]:_ref8$plannedmaintena,pmMap=_ref8.plannedmaintenanceMap,pmTriggerMap=_ref8.planedmaintenaceTriggerList,dateFormat="YYYY-MM-DD HH:mm",(0,validation/* isEmpty */.xb)(pmJobList)||(_this7.eventList=pmMap||{},_this7.triggers=pmTriggerMap||{},pmJobList.forEach((function(job){var _ref9=job||{},nextExecutionTime=_ref9.nextExecutionTime,pmTriggerId=_ref9.pmTriggerId,resourceId=_ref9.resourceId,status=_ref9.status,id=_ref9.id,woSubject=_ref9.woSubject,assignmentGroupId=_ref9.assignmentGroupId,assignedToId=_ref9.assignedToId,pmTrigger=_this7.$getProperty(pmTriggerMap,"".concat(pmTriggerId),{}),_ref10=pmTrigger||{},frequency=_ref10.frequency,skipEvent=4===status&&nextExecutionTime<$options.filters.now();if(!skipEvent){var event=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},job),{},{key:"event".concat(id),jobId:id,title:woSubject,start:$options.filters.toDateFormat(nextExecutionTime,dateFormat),end:$options.filters.toDateFormat(nextExecutionTime+1800,dateFormat),asset:resourceId,space:resourceId,team:assignmentGroupId,staff:assignedToId,starttime:$options.filters.toDateFormat(nextExecutionTime,"HH:mm"),startdate:$options.filters.toDateFormat(nextExecutionTime,dateFormat),frequencyText:_this7.$getProperty(frequencyText,"".concat(frequency),"---"),constraint:{start:$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:$options.filters.addDays(new Date,61,!0)},resourceview:resourceview,job:job,frequency:frequency});event=_this7.setPMV2EventOptions(event)||{},_this7.events.push(event)}}))),isPlanned&&"planned"===currentView&&(groupedEvents=_this7.loadGroupedEvents({data:result})||[],(0,validation/* isEmpty */.xb)(groupedEvents)||(_this7$events=_this7.events).push.apply(_this7$events,(0,toConsumableArray/* default */.Z)(groupedEvents))),_this7.sortAndSetResources()),_this7.isEventLoading=!1;case 16:case"end":return _context4.stop()}}),_callee4)})))()},loadNewEvents:function(){var self=this;self.events=[];var startDate=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").start).tz(self.$timezone).startOf("day").valueOf()/1e3,endDate=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").end).tz(self.$timezone).startOf("day").valueOf()/1e3,url="";if(this.isPlanned?(url="/workorder/getUpcomingPreventiveMaintenance?startTime="+startDate+"&endTime="+endDate,self.isNewCode&&(url="/workorder/getPMJobs?startTime="+startDate+"&endTime="+endDate),url+="&currentView=".concat(self.currentView)):url="/workorder/open?startTime="+startDate+"&endTime="+endDate,this.filters){var filters=this.filters;
//frequency field doesn not exist in workorder module , sending it breaks Filter->Criteria construction
// is frequency filter is selected , send it as a seperate param instead
this.isPlanned||(url+="&includeParentFilter=true"),filters.frequency?url+="&frequencyFilter="+encodeURIComponent(JSON.stringify(filters)):url+="&filters="+encodeURIComponent(JSON.stringify(filters))}this.isEventLoading=!0,self.$http.get(url).then((function(response){self.events=[];var dateFormat="YYYY-MM-DD HH:mm";if(self.isPlanned&&(response.data.pmJobs||response.data.pmJobList)){if(self.eventList=response.data.pmMap,self.triggers=response.data.pmTriggerMap,self.assets=[],self.spaces=[],response.data.pmResourcesMap)for(var key in response.data.pmResourcesMap)2===response.data.pmResourcesMap[key].resourceType?self.assets.push(response.data.pmResourcesMap[key]):self.spaces.push(response.data.pmResourcesMap[key]);var pmJobs=[];pmJobs=self.isNewCode?response.data.pmJobList:response.data.pmJobs;for(var i=0;i<pmJobs.length;i++){var job=pmJobs[i],nextExecutionTime=job.nextExecutionTime;if(!(4===job.status&&nextExecutionTime<self.$options.filters.now())){var pmTrigger=response.data.pmTriggerMap[job.pmTriggerId],pm=response.data.pmMap[pmTrigger.pmId],template=job.template?job.template:pm.woTemplate,resourceId=template.resourceId;(!resourceId||resourceId<=0)&&job.resourceId&&job.resourceId>0&&(resourceId=job.resourceId);var assignedToId=job.assignedToId;(!assignedToId||assignedToId<=0)&&(assignedToId=template.assignedToId);var event={id:job.id,key:"event"+i,jobId:job.id,pmId:pm.id,title:self.$getProperty(job,"woSubject","---"),start:self.$options.filters.toDateFormat(nextExecutionTime,dateFormat),end:self.$options.filters.toDateFormat(job.nextExecutionTime+1800,dateFormat),asset:resourceId,space:resourceId,team:template.assignmentGroupId,staff:assignedToId,starttime:self.$options.filters.toDateFormat(nextExecutionTime,"HH:mm"),startdate:self.$options.filters.toDateFormat(nextExecutionTime,dateFormat),pmfrequency:pm.frequency,frequencyText:self.frequencyText[pm.frequency],resourceview:self.resourceview,job:job,constraint:{start:self.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:self.$options.filters.addDays(new Date,61,!0)}};
//temp fix for frequency legend color not set
self.$getProperty(event,"job.pmTriggerId")&&(event.frequency=self.$getProperty(self.triggers,"".concat(event.job.pmTriggerId,".frequency"),-1)),event.frequency||(event.frequency=-1),self.setPMEventOptions(event),self.events.push(event)}}}else if(response.data.workOrders)for(var x in self.assets=[],self.spaces=[],response.data.workOrders){var wo=response.data.workOrders[x];wo.resource&&(2===wo.resource.resourceType?self.assets.push(wo.resource):self.spaces.push(wo.resource));var colorCode=void 0,color=void 0,type=self.typeConfig[self.calendarType].wokey?self.typeConfig[self.calendarType].wokey:self.calendarType,colorTypeValue=(wo[type]&&"object"===(0,esm_typeof/* default */.Z)(wo[type])?wo[type].id:wo[type])||-1;colorCode=self.currentColorTypes[colorTypeValue].colorcode,color=self.eventColors[colorCode-1];var start=void 0,end=void 0;"est"===self.calType?(start=self.$options.filters.toDateFormat(wo.estimatedStart,dateFormat),end=self.$options.filters.toDateFormat(wo.estimatedEnd,dateFormat)):(start=self.$options.filters.toDateFormat(wo.dueDate,dateFormat),end=self.$options.filters.toDateFormat(wo.dueDate+18e5,dateFormat)),self.events.push({key:wo.id,title:wo.subject,start:start,end:end,starttime:self.$options.filters.toDateFormat(wo.dueDate,"HH:mm"),startdate:self.$options.filters.toDateFormat(wo.dueDate,dateFormat),asset:wo.resource?wo.resource.id:-1,space:wo.resource?wo.resource.id:-1,team:wo.assignmentGroup?wo.assignmentGroup.id:-1,staff:wo.assignedTo?wo.assignedTo.id:-1,resourceview:self.resourceview,editable:!0,colortype:colorCode,color:color,constraint:{start:self.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:self.$options.filters.addDays(new Date,61,!0)}}),self.eventList[wo.id]=wo}if(self.isPlanned&&"planned"===self.currentView){var _self$events,multi=self.loadGroupedEvents(response);if(multi&&multi.length)(_self$events=self.events).push.apply(_self$events,(0,toConsumableArray/* default */.Z)(multi))}self.sortAndSetResources(),self.isEventLoading=!1}))},loadOldEvents:function(){var self=this;self.events=[];var startDate=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").start).tz(self.$timezone).startOf("day").valueOf()/1e3,endDate=moment_timezone_default()(jquery_default()("#calenderview").fullCalendar("getView").end).tz(self.$timezone).startOf("day").valueOf()/1e3,url="";if(this.isPlanned?(url="/workorder/getUpcomingPreventiveMaintenance?startTime="+startDate+"&endTime="+endDate,self.isNewCode&&(url="/workorder/getPMJobs?startTime="+startDate+"&endTime="+endDate),url+="&currentView=".concat(self.currentView)):url="/workorder/open?startTime="+startDate+"&endTime="+endDate,this.filters){var filters=this.filters;this.isPlanned||(url+="&includeParentFilter=true"),url+="&filters="+encodeURIComponent(JSON.stringify(filters))}this.isEventLoading=!0,self.$http.get(url).then((function(response){self.events=[];var dateFormat="YYYY-MM-DD HH:mm";if(self.isPlanned&&(response.data.pmJobs||response.data.pmJobList)){if(self.eventList=response.data.pmMap,self.triggers=response.data.pmTriggerMap,self.assets=[],self.spaces=[],response.data.pmResourcesMap)for(var key in response.data.pmResourcesMap)2===response.data.pmResourcesMap[key].resourceType?self.assets.push(response.data.pmResourcesMap[key]):self.spaces.push(response.data.pmResourcesMap[key]);var pmJobs=[];pmJobs=self.isNewCode?response.data.pmJobList:response.data.pmJobs;for(var i=0;i<pmJobs.length;i++){var job=pmJobs[i],nextExecutionTime=1e3*job.nextExecutionTime;if(!(4===job.status&&nextExecutionTime<self.$options.filters.now())){var pmTrigger=response.data.pmTriggerMap[job.pmTriggerId],pm=response.data.pmMap[pmTrigger.pmId],template=job.template?job.template:pm.woTemplate,resourceId=template.resourceId;(!resourceId||resourceId<=0)&&job.resourceId&&job.resourceId>0&&(resourceId=job.resourceId);var assignedToId=job.assignedToId;(!assignedToId||assignedToId<=0)&&(assignedToId=template.assignedToId);var event={id:job.id,key:"event"+i,jobId:job.id,pmId:pm.id,title:self.$getProperty(job,"woSubject","---"),start:self.$options.filters.toDateFormat(nextExecutionTime,dateFormat),end:self.$options.filters.toDateFormat(1e3*(job.nextExecutionTime+1800),dateFormat),asset:resourceId,space:resourceId,team:template.assignmentGroupId,staff:assignedToId,starttime:self.$options.filters.toDateFormat(nextExecutionTime,"HH:mm"),startdate:self.$options.filters.toDateFormat(nextExecutionTime,dateFormat),pmfrequency:pm.frequency,frequencyText:self.frequencyText[pm.frequency],resourceview:self.resourceview,job:job,constraint:{start:self.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:self.$options.filters.addDays(new Date,61,!0)}};self.setPMEventOptions(event),self.events.push(event)}}}else if(response.data.workOrders)for(var x in self.assets=[],self.spaces=[],response.data.workOrders){var wo=response.data.workOrders[x];wo.resource&&(2===wo.resource.resourceType?self.assets.push(wo.resource):self.spaces.push(wo.resource));var colorCode=void 0,color=void 0,type=self.typeConfig[self.calendarType].wokey?self.typeConfig[self.calendarType].wokey:self.calendarType,colorTypeValue=(wo[type]&&"object"===(0,esm_typeof/* default */.Z)(wo[type])?wo[type].id:wo[type])||-1;colorCode=self.currentColorTypes[colorTypeValue].colorcode,color=self.eventColors[colorCode-1];var start=void 0,end=void 0;"est"===self.calType?(start=self.$options.filters.toDateFormat(wo.estimatedStart,dateFormat),end=self.$options.filters.toDateFormat(wo.estimatedEnd,dateFormat)):(start=self.$options.filters.toDateFormat(wo.dueDate,dateFormat),end=self.$options.filters.toDateFormat(wo.dueDate+18e5,dateFormat)),self.events.push({key:wo.id,title:wo.subject,start:start,end:end,starttime:self.$options.filters.toDateFormat(wo.dueDate,"HH:mm"),startdate:self.$options.filters.toDateFormat(wo.dueDate,dateFormat),asset:wo.resource?wo.resource.id:-1,space:wo.resource?wo.resource.id:-1,team:wo.assignmentGroup?wo.assignmentGroup.id:-1,staff:wo.assignedTo?wo.assignedTo.id:-1,resourceview:self.resourceview,editable:!0,colortype:colorCode,color:color,constraint:{start:self.$options.filters.toDateFormat(new Date,"YYYY-MM-DD"),end:self.$options.filters.addDays(new Date,61,!0)}}),self.eventList[wo.id]=wo}if(self.isPlanned&&"planned"===self.currentView){var _self$events2,multi=self.loadGroupedEvents(response);if(multi&&multi.length)(_self$events2=self.events).push.apply(_self$events2,(0,toConsumableArray/* default */.Z)(multi))}self.sortAndSetResources(),self.isEventLoading=!1}))},sortAndSetResources:function(){var _this8=this;this.removeResources();var resources=[];if(this.events.forEach((function(event){event.resourceId=event[_this8.selectedResource],resources.push(event.resourceId),"team"===_this8.selectedResource?-1===event.resourceId&&event.staff>0&&(event.resourceId=-2):"staff"===_this8.selectedResource&&-1===event.resourceId&&event.team>0&&(event.resourceId=-2),event.resourceEditable=event.editable&&("staff"===_this8.selectedResource||"team"===_this8.selectedResource)})),this.resourceview){this.resourceList.sort((function(a,b){if(-1===a.id||-1===b.id)return-1;var event1=_this8.events.find((function(event){return event.resourceId===a.id})),event2=_this8.events.find((function(event){return event.resourceId===b.id}));return _this8.isPlanned?event1&&event2?parseInt(event1.pmfrequency)-parseInt(event2.pmfrequency):event1||!event2?-1:1:event1&&event2?0:event1||!event2?-1:1}));// temp
for(var len=this.resourceList.length<=100?this.resourceList.length:100,i=0;i<len;i++){var option=this.resourceList[i];jquery_default()("#calenderview").fullCalendar("addResource",option)}setTimeout((function(){_this8.$refs.calendar.$emit("reload-events"),_this8.isEventLoading=!1}),100)}},setPMEventOptions:function(event){if(3===event.job.status)event.colortype=this.currentColorTypes.completed.colorcode,event.color=this.currentColorTypes.completed.color,event.editable=!1;else if(4===event.job.status)event.colortype=this.currentColorTypes.inactive.colorcode,event.color=this.currentColorTypes.inactive.color,event.editable=!1;else{var pm=this.eventList[event.pmId],template=event.job.template||pm.woTemplate,colorTypeValue=pm[this.calendarType]||template[this.calendarType]||-1;"frequency"==this.calendarType&&event.frequency&&(
//temp fix
colorTypeValue=event.frequency),event.colortype=this.currentColorTypes[colorTypeValue].colorcode,event.color=this.eventColors[event.colortype-1],event.editable=!event.job.projected}},setPMV2EventOptions:function(event){var eventColors=this.eventColors,currentColorTypes=this.currentColorTypes,_ref11=event||{},frequency=_ref11.frequency,colortype=this.$getProperty(currentColorTypes,"".concat(frequency,".colorcode"),-1),color=this.$getProperty(eventColors,"".concat(colortype-1),null);return event=(0,objectSpread2/* default */.Z)((0,objectSpread2/* default */.Z)({},event),{},{colortype:colortype,color:color,editable:!0}),event},updatePmJob:function(command,eventDetails,status){var url,params,_this9=this;"current"===command?(url="/workorder/updatePreventiveMaintenanceJob",params={id:[eventDetails.jobId],pmJob:{status:status}}):(url="/workorder/changePreventiveMaintenanceStatus",params={id:[eventDetails.pmId],preventivemaintenance:{status:!1}}),this.$http.post(url,params).then((function(response){if("object"===(0,esm_typeof/* default */.Z)(response.data)){if(_this9.$message.success("Status Updated Successfully"),"current"===command){var event=_this9.events.find((function(event){return event.jobId===eventDetails.jobId}));event.job.status=response.data.pmJob.status,_this9.setPMEventOptions(event)}else _this9.events=_this9.events.filter((function(event){return event.pmId!==eventDetails.pmId}));_this9.closeDialog()}else _this9.$message.error("Updation Failed")}))},opensummary:function(event){var _ref12=event||{},id=_ref12.id;if((0,router/* isWebTabsEnabled */.tj)()){var moduleName="workorder",_ref13=(0,router/* findRouteForModule */.Jp)(moduleName,router/* pageTypes */.As.OVERVIEW)||{},name=_ref13.name;name&&this.$router.push({name:name,params:{viewname:"all",id:id}})}else this.$router.push({path:"/app/wo/orders/summary/"+id})},prettyScheduleObject:function(scheduleObj){if(!scheduleObj)return"---";var prettyStr="";if("DAILY"===scheduleObj.frequencyTypeEnum)prettyStr="Every "+(1===scheduleObj.frequency?"day":scheduleObj.frequency+" days");else if("WEEKLY"===scheduleObj.frequencyTypeEnum){prettyStr="Every "+(1===scheduleObj.frequency?"week":scheduleObj.frequency+" weeks");var days=["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];prettyStr+=" on ";for(var i=0;i<scheduleObj.values.length;i++)0!==i&&(prettyStr+=","),prettyStr+=days[i+1]}else if("MONTHLY_DAY"===scheduleObj.frequencyTypeEnum){prettyStr="Every "+(1===scheduleObj.frequency?"month":scheduleObj.frequency+" months"),prettyStr+=" on ";for(var _i=0;_i<scheduleObj.values.length;_i++)0!==_i&&(prettyStr+=","),prettyStr+=scheduleObj.values[_i]+this.nth(scheduleObj.values[_i])}else if("MONTHLY_WEEK"===scheduleObj.frequencyTypeEnum){prettyStr="Every "+(1===scheduleObj.frequency?"month":scheduleObj.frequency+" months"),prettyStr+=" on (";var weeks=["First","Second","Third","Fourth","Last"],_days=["Mon","Tue","Wed","Thu","Fri","Sat","Sun"];prettyStr+=weeks[scheduleObj.weekFrequency-1]+" week",prettyStr+=" - ";for(var _i2=0;_i2<scheduleObj.values.length;_i2++)0!==_i2&&(prettyStr+=","),prettyStr+=_days[_i2+1];prettyStr+=")"}else if("YEARLY"===scheduleObj.frequencyTypeEnum){prettyStr="Every "+(1===scheduleObj.frequency?"year":scheduleObj.frequency+" years"),prettyStr+=" on ";for(var months=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],_i3=0;_i3<scheduleObj.values.length;_i3++){0!==_i3&&(prettyStr+=",");var j=scheduleObj.values[_i3];prettyStr+=months[j-1]}}return prettyStr},nth:function(d){if(d>3&&d<21)return"th";switch(d%10){case 1:return"st";case 2:return"nd";case 3:return"rd";default:return"th"}},setFilterTypeOptions:function(){this.typeConfig.typeId.options=this.$helpers.cloneObject(this.tickettype),this.typeConfig.statusId.options=this.$helpers.cloneObject(this.ticketstatus),this.typeConfig.frequency.options=Object.assign({},this.$constants.FACILIO_FREQUENCY,{0:"Once"}),"categoryId"!==this.calendarType&&"priorityId"!==this.calendarType&&this.setCurrentColorType(),this.loadPickList("ticketcategory","categoryId"),this.loadPickList("ticketpriority","priorityId")},setCurrentColorType:function(){var _this10=this;this.currentColorTypes={"-1":{colorcode:10,active:!0},completed:{colorcode:"complete",color:"#d0cece"},inactive:{colorcode:"inactive",color:"#ededed"}},Object.keys(this.typeConfig[this.calendarType].options).filter((function(key){return-1!==parseInt(key)})).forEach((function(key,index){_this10.currentColorTypes[key]={colorcode:index%_this10.eventColors.length+1,active:!0}})),this.typeConfig[this.calendarType].noneLabel&&!this.typeConfig[this.calendarType].options[-1]&&(this.typeConfig[this.calendarType].options[-1]=this.typeConfig[this.calendarType].noneLabel),null!=this.filters&&Object.keys(this.filters).length&&this.resetCircle()},resetCircle:function(){return this.$helpers.isLicenseEnabled("SCHEDULED_WO")?this.resetNewCircle():this.resetOldCircle()},resetOldCircle:function(){var _this11=this,type=!this.isPlanned&&this.typeConfig[this.calendarType].wokey?this.typeConfig[this.calendarType].wokey:this.calendarType,isColorFiltered=this.filters&&this.filters[type]&&this.filters[type].value,_loop=function(key){if(_this11.currentColorTypes.hasOwnProperty(key)){var data=_this11.currentColorTypes[key];data.active=!isColorFiltered||(-1!==parseInt(key)?_this11.filters[type].value.find((function(val){return parseInt(val)===parseInt(key)})):!_this11.$route.query.hasOwnProperty("isnone")||_this11.$route.query.isnone)}};for(var key in this.currentColorTypes)_loop(key)},resetNewCircle:function(){var _this12=this,type=this.typeConfig[this.calendarType].wokey?this.typeConfig[this.calendarType].wokey:this.calendarType,isColorFiltered=this.filters&&this.filters[type]&&this.filters[type].value,_loop2=function(key){if(_this12.currentColorTypes.hasOwnProperty(key)){var data=_this12.currentColorTypes[key];data.active=!isColorFiltered||(-1!==parseInt(key)?_this12.filters[type].value.find((function(val){return parseInt(val)===parseInt(key)})):!_this12.$route.query.hasOwnProperty("isnone")||_this12.$route.query.isnone)}};for(var key in this.currentColorTypes)_loop2(key)},updateCalendarColor:function(){this.$store.dispatch("updateCalendarColor",this.calendarType)},filterCalendar:function(value){
//disble click action
// if (this.$helpers.isLicenseEnabled('SCHEDULED_WO')) {
//   return this.filterNewCalendar(value)
// } else {
//   return this.filterOldCalendar(value)
// }
},filterNewCalendar:function(value){this.currentColorTypes[value].active=!this.currentColorTypes[value].active;var config=this.typeConfig[this.calendarType];if(config){var values=[];for(var key in this.typeConfig[this.calendarType].options)this.typeConfig[this.calendarType].options.hasOwnProperty(key)&&-1!==parseInt(key)&&this.currentColorTypes[key].active&&values.push(key+"");var count=this.typeConfig[this.calendarType].options[-1]&&this.currentColorTypes[-1].active?this.optionsCount-1:this.optionsCount;if(values.length&&values.length!==count){var _key=this.typeConfig[this.calendarType].wokey?this.typeConfig[this.calendarType].wokey:this.calendarType,filter=(0,defineProperty/* default */.Z)({},_key,{operatorId:config.operatorId||36,value:values}),query={search:JSON.stringify(filter),open:!1};this.typeConfig[this.calendarType].options[-1]&&(query.isnone=this.currentColorTypes[-1].active),this.$router.replace({query:query})}else this.$router.replace({query:{}})}},filterOldCalendar:function(value){this.currentColorTypes[value].active=!this.currentColorTypes[value].active;var config=this.typeConfig[this.calendarType];if(config){var values=[];for(var key in this.typeConfig[this.calendarType].options)this.typeConfig[this.calendarType].options.hasOwnProperty(key)&&-1!==parseInt(key)&&this.currentColorTypes[key].active&&values.push(key+"");var count=this.typeConfig[this.calendarType].options[-1]&&this.currentColorTypes[-1].active?this.optionsCount-1:this.optionsCount;if(values.length&&values.length!==count){var _key2=!this.isPlanned&&this.typeConfig[this.calendarType].wokey?this.typeConfig[this.calendarType].wokey:this.calendarType,filter=(0,defineProperty/* default */.Z)({},_key2,{operatorId:config.operatorId||36,value:values}),query={search:JSON.stringify(filter),open:!1};this.typeConfig[this.calendarType].options[-1]&&(query.isnone=this.currentColorTypes[-1].active),this.$router.replace({query:query})}else this.$router.replace({query:{}})}},loadPickList:function(moduleName,fieldName){var _this13=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee5(){var _yield$getFieldOption,error,options;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context5){while(1)switch(_context5.prev=_context5.next){case 0:return _this13.picklistOptions={},_context5.next=3,(0,picklist/* getFieldOptions */._K)({field:{lookupModuleName:moduleName,skipDeserialize:!0}});case 3:_yield$getFieldOption=_context5.sent,error=_yield$getFieldOption.error,options=_yield$getFieldOption.options,error?_this13.$message.error(error.message||"Error Occured"):(_this13.typeConfig[fieldName].options=options,_this13.calendarType===fieldName&&_this13.setCurrentColorType());case 7:case"end":return _context5.stop()}}),_callee5)})))()},registerEvents:function(){var self=this;jquery_default()("#calenderview .fc-button-group .fc-prev-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-next-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-today-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-month-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-agendaWeek-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-agendaDay-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-basicWeek-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-basicDay-button").click((function(){return self.events=[],self.loadEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-timelineYear-button").click((function(){return self.events=[],self.loadEvents(),self.registerPopupEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-timelineMonth-button").click((function(){return self.events=[],self.loadEvents(),self.registerPopupEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-timelineWeek-button").click((function(){return self.events=[],self.loadEvents(),self.registerPopupEvents(),!1})),jquery_default()("#calenderview .fc-button-group .fc-timelineDay-button").click((function(){return self.events=[],self.loadEvents(),self.registerPopupEvents(),!1})),self.registerPopupEvents()},registerPopupEvents:function(){jquery_default()("#dropdownid").click((function(){return jquery_default()("#calendarpage .popup1").addClass("showpopup1"),jquery_default()("#calendarpage .popup2").removeClass("showpopup2"),!1})),jquery_default()("#filterid").click((function(){return jquery_default()("#calendarpage .popup2").addClass("showpopup2"),jquery_default()("#calendarpage .popup1").removeClass("showpopup1"),!1})),jquery_default()("html").click((function(event){0===jquery_default()(event.target).closest("#calendarpage .popup2.showpopup2").length&&(jquery_default()("#calendarpage .popup1").removeClass("showpopup1"),jquery_default()("#calendarpage .popup2").removeClass("showpopup2"))})),jquery_default()("#calendarpage").on("scroll",this.handleScroll)},hide:function(){this.showPopover&&!this.dontHide&&(this.showPopover=!1,jquery_default()("a[class*='eventselected']").removeClass((function(index,css){return(css.match(/(^|\s)eventselected\S+/g)||[]).join(" ")}))),this.dontHide=!1},closeDialog:function(){this.showPopover=!1,jquery_default()("a[class*='eventselected']").removeClass((function(index,css){return(css.match(/(^|\s)eventselected\S+/g)||[]).join(" ")}))},closeDropdown:function(){this.showAllCircles=!1},getPmCreationTypeEnum:function(pmId){var pm=this.$getProperty(this,"eventList.".concat(pmId));return(0,validation/* isEmpty */.xb)(pm)?null:pm.pmCreationTypeEnum}}},workorder_Calendarvue_type_script_lang_js=Calendarvue_type_script_lang_js,Calendar_component=(0,componentNormalizer/* default */.Z)(workorder_Calendarvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Calendar=Calendar_component.exports},
/***/684890:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"c5bbcb00b28261c206e2f2de4a9fe124.svg";
/***/},
/***/892098:
/***/function(module,exports,__webpack_require__){
/* module decorator */module=__webpack_require__.nmd(module);
/**
 * Lodash (Custom Build) <https://lodash.com/>
 * Build: `lodash modularize exports="npm" -o ./`
 * Copyright OpenJS Foundation and other contributors <https://openjsf.org/>
 * Released under MIT license <https://lodash.com/license>
 * Based on Underscore.js 1.8.3 <http://underscorejs.org/LICENSE>
 * Copyright Jeremy Ashkenas, DocumentCloud and Investigative Reporters & Editors
 */
/** Used as the size to enable large array optimizations. */
var LARGE_ARRAY_SIZE=200,HASH_UNDEFINED="__lodash_hash_undefined__",HOT_COUNT=800,HOT_SPAN=16,MAX_SAFE_INTEGER=9007199254740991,argsTag="[object Arguments]",arrayTag="[object Array]",asyncTag="[object AsyncFunction]",boolTag="[object Boolean]",dateTag="[object Date]",errorTag="[object Error]",funcTag="[object Function]",genTag="[object GeneratorFunction]",mapTag="[object Map]",numberTag="[object Number]",nullTag="[object Null]",objectTag="[object Object]",proxyTag="[object Proxy]",regexpTag="[object RegExp]",setTag="[object Set]",stringTag="[object String]",undefinedTag="[object Undefined]",weakMapTag="[object WeakMap]",arrayBufferTag="[object ArrayBuffer]",dataViewTag="[object DataView]",float32Tag="[object Float32Array]",float64Tag="[object Float64Array]",int8Tag="[object Int8Array]",int16Tag="[object Int16Array]",int32Tag="[object Int32Array]",uint8Tag="[object Uint8Array]",uint8ClampedTag="[object Uint8ClampedArray]",uint16Tag="[object Uint16Array]",uint32Tag="[object Uint32Array]",reRegExpChar=/[\\^$.*+?()[\]{}|]/g,reIsHostCtor=/^\[object .+?Constructor\]$/,reIsUint=/^(?:0|[1-9]\d*)$/,typedArrayTags={};
/** Used to stand-in for `undefined` hash values. */typedArrayTags[float32Tag]=typedArrayTags[float64Tag]=typedArrayTags[int8Tag]=typedArrayTags[int16Tag]=typedArrayTags[int32Tag]=typedArrayTags[uint8Tag]=typedArrayTags[uint8ClampedTag]=typedArrayTags[uint16Tag]=typedArrayTags[uint32Tag]=!0,typedArrayTags[argsTag]=typedArrayTags[arrayTag]=typedArrayTags[arrayBufferTag]=typedArrayTags[boolTag]=typedArrayTags[dataViewTag]=typedArrayTags[dateTag]=typedArrayTags[errorTag]=typedArrayTags[funcTag]=typedArrayTags[mapTag]=typedArrayTags[numberTag]=typedArrayTags[objectTag]=typedArrayTags[regexpTag]=typedArrayTags[setTag]=typedArrayTags[stringTag]=typedArrayTags[weakMapTag]=!1;
/** Detect free variable `global` from Node.js. */
var freeGlobal="object"==typeof __webpack_require__.g&&__webpack_require__.g&&__webpack_require__.g.Object===Object&&__webpack_require__.g,freeSelf="object"==typeof self&&self&&self.Object===Object&&self,root=freeGlobal||freeSelf||Function("return this")(),freeExports=exports&&!exports.nodeType&&exports,freeModule=freeExports&&module&&!module.nodeType&&module,moduleExports=freeModule&&freeModule.exports===freeExports,freeProcess=moduleExports&&freeGlobal.process,nodeUtil=function(){try{
// Use `util.types` for Node.js 10+.
var types=freeModule&&freeModule.require&&freeModule.require("util").types;return types||freeProcess&&freeProcess.binding&&freeProcess.binding("util");
// Legacy `process.binding('util')` for Node.js < 10.
}catch(e){}}(),nodeIsTypedArray=nodeUtil&&nodeUtil.isTypedArray;
/** Detect free variable `self`. */
/**
 * A faster alternative to `Function#apply`, this function invokes `func`
 * with the `this` binding of `thisArg` and the arguments of `args`.
 *
 * @private
 * @param {Function} func The function to invoke.
 * @param {*} thisArg The `this` binding of `func`.
 * @param {Array} args The arguments to invoke `func` with.
 * @returns {*} Returns the result of `func`.
 */
function apply(func,thisArg,args){switch(args.length){case 0:return func.call(thisArg);case 1:return func.call(thisArg,args[0]);case 2:return func.call(thisArg,args[0],args[1]);case 3:return func.call(thisArg,args[0],args[1],args[2])}return func.apply(thisArg,args)}
/**
 * The base implementation of `_.times` without support for iteratee shorthands
 * or max array length checks.
 *
 * @private
 * @param {number} n The number of times to invoke `iteratee`.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the array of results.
 */function baseTimes(n,iteratee){var index=-1,result=Array(n);while(++index<n)result[index]=iteratee(index);return result}
/**
 * The base implementation of `_.unary` without support for storing metadata.
 *
 * @private
 * @param {Function} func The function to cap arguments for.
 * @returns {Function} Returns the new capped function.
 */function baseUnary(func){return function(value){return func(value)}}
/**
 * Gets the value at `key` of `object`.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {string} key The key of the property to get.
 * @returns {*} Returns the property value.
 */function getValue(object,key){return null==object?void 0:object[key]}
/**
 * Creates a unary function that invokes `func` with its argument transformed.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {Function} transform The argument transform.
 * @returns {Function} Returns the new function.
 */function overArg(func,transform){return function(arg){return func(transform(arg))}}
/** Used for built-in method references. */var arrayProto=Array.prototype,funcProto=Function.prototype,objectProto=Object.prototype,coreJsData=root["__core-js_shared__"],funcToString=funcProto.toString,hasOwnProperty=objectProto.hasOwnProperty,maskSrcKey=function(){var uid=/[^.]+$/.exec(coreJsData&&coreJsData.keys&&coreJsData.keys.IE_PROTO||"");return uid?"Symbol(src)_1."+uid:""}(),nativeObjectToString=objectProto.toString,objectCtorString=funcToString.call(Object),reIsNative=RegExp("^"+funcToString.call(hasOwnProperty).replace(reRegExpChar,"\\$&").replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g,"$1.*?")+"$"),Buffer=moduleExports?root.Buffer:void 0,Symbol=root.Symbol,Uint8Array=root.Uint8Array,allocUnsafe=Buffer?Buffer.allocUnsafe:void 0,getPrototype=overArg(Object.getPrototypeOf,Object),objectCreate=Object.create,propertyIsEnumerable=objectProto.propertyIsEnumerable,splice=arrayProto.splice,symToStringTag=Symbol?Symbol.toStringTag:void 0,defineProperty=function(){try{var func=getNative(Object,"defineProperty");return func({},"",{}),func}catch(e){}}(),nativeIsBuffer=Buffer?Buffer.isBuffer:void 0,nativeMax=Math.max,nativeNow=Date.now,Map=getNative(root,"Map"),nativeCreate=getNative(Object,"create"),baseCreate=function(){function object(){}return function(proto){if(!isObject(proto))return{};if(objectCreate)return objectCreate(proto);object.prototype=proto;var result=new object;return object.prototype=void 0,result}}();
/** Used to detect overreaching core-js shims. */
/**
 * Creates a hash object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function Hash(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
/**
 * Removes all key-value entries from the hash.
 *
 * @private
 * @name clear
 * @memberOf Hash
 */function hashClear(){this.__data__=nativeCreate?nativeCreate(null):{},this.size=0}
/**
 * Removes `key` and its value from the hash.
 *
 * @private
 * @name delete
 * @memberOf Hash
 * @param {Object} hash The hash to modify.
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function hashDelete(key){var result=this.has(key)&&delete this.__data__[key];return this.size-=result?1:0,result}
/**
 * Gets the hash value for `key`.
 *
 * @private
 * @name get
 * @memberOf Hash
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function hashGet(key){var data=this.__data__;if(nativeCreate){var result=data[key];return result===HASH_UNDEFINED?void 0:result}return hasOwnProperty.call(data,key)?data[key]:void 0}
/**
 * Checks if a hash value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Hash
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function hashHas(key){var data=this.__data__;return nativeCreate?void 0!==data[key]:hasOwnProperty.call(data,key)}
/**
 * Sets the hash `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Hash
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the hash instance.
 */function hashSet(key,value){var data=this.__data__;return this.size+=this.has(key)?0:1,data[key]=nativeCreate&&void 0===value?HASH_UNDEFINED:value,this}
// Add methods to `Hash`.
/**
 * Creates an list cache object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function ListCache(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
/**
 * Removes all key-value entries from the list cache.
 *
 * @private
 * @name clear
 * @memberOf ListCache
 */function listCacheClear(){this.__data__=[],this.size=0}
/**
 * Removes `key` and its value from the list cache.
 *
 * @private
 * @name delete
 * @memberOf ListCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function listCacheDelete(key){var data=this.__data__,index=assocIndexOf(data,key);if(index<0)return!1;var lastIndex=data.length-1;return index==lastIndex?data.pop():splice.call(data,index,1),--this.size,!0}
/**
 * Gets the list cache value for `key`.
 *
 * @private
 * @name get
 * @memberOf ListCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function listCacheGet(key){var data=this.__data__,index=assocIndexOf(data,key);return index<0?void 0:data[index][1]}
/**
 * Checks if a list cache value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf ListCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function listCacheHas(key){return assocIndexOf(this.__data__,key)>-1}
/**
 * Sets the list cache `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf ListCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the list cache instance.
 */function listCacheSet(key,value){var data=this.__data__,index=assocIndexOf(data,key);return index<0?(++this.size,data.push([key,value])):data[index][1]=value,this}
// Add methods to `ListCache`.
/**
 * Creates a map cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function MapCache(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
/**
 * Removes all key-value entries from the map.
 *
 * @private
 * @name clear
 * @memberOf MapCache
 */function mapCacheClear(){this.size=0,this.__data__={hash:new Hash,map:new(Map||ListCache),string:new Hash}}
/**
 * Removes `key` and its value from the map.
 *
 * @private
 * @name delete
 * @memberOf MapCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function mapCacheDelete(key){var result=getMapData(this,key)["delete"](key);return this.size-=result?1:0,result}
/**
 * Gets the map value for `key`.
 *
 * @private
 * @name get
 * @memberOf MapCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function mapCacheGet(key){return getMapData(this,key).get(key)}
/**
 * Checks if a map value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf MapCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function mapCacheHas(key){return getMapData(this,key).has(key)}
/**
 * Sets the map `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf MapCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the map cache instance.
 */function mapCacheSet(key,value){var data=getMapData(this,key),size=data.size;return data.set(key,value),this.size+=data.size==size?0:1,this}
// Add methods to `MapCache`.
/**
 * Creates a stack cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */
function Stack(entries){var data=this.__data__=new ListCache(entries);this.size=data.size}
/**
 * Removes all key-value entries from the stack.
 *
 * @private
 * @name clear
 * @memberOf Stack
 */function stackClear(){this.__data__=new ListCache,this.size=0}
/**
 * Removes `key` and its value from the stack.
 *
 * @private
 * @name delete
 * @memberOf Stack
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function stackDelete(key){var data=this.__data__,result=data["delete"](key);return this.size=data.size,result}
/**
 * Gets the stack value for `key`.
 *
 * @private
 * @name get
 * @memberOf Stack
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function stackGet(key){return this.__data__.get(key)}
/**
 * Checks if a stack value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Stack
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function stackHas(key){return this.__data__.has(key)}
/**
 * Sets the stack `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Stack
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the stack cache instance.
 */function stackSet(key,value){var data=this.__data__;if(data instanceof ListCache){var pairs=data.__data__;if(!Map||pairs.length<LARGE_ARRAY_SIZE-1)return pairs.push([key,value]),this.size=++data.size,this;data=this.__data__=new MapCache(pairs)}return data.set(key,value),this.size=data.size,this}
// Add methods to `Stack`.
/**
 * Creates an array of the enumerable property names of the array-like `value`.
 *
 * @private
 * @param {*} value The value to query.
 * @param {boolean} inherited Specify returning inherited property names.
 * @returns {Array} Returns the array of property names.
 */
function arrayLikeKeys(value,inherited){var isArr=isArray(value),isArg=!isArr&&isArguments(value),isBuff=!isArr&&!isArg&&isBuffer(value),isType=!isArr&&!isArg&&!isBuff&&isTypedArray(value),skipIndexes=isArr||isArg||isBuff||isType,result=skipIndexes?baseTimes(value.length,String):[],length=result.length;for(var key in value)!inherited&&!hasOwnProperty.call(value,key)||skipIndexes&&(
// Safari 9 has enumerable `arguments.length` in strict mode.
"length"==key||
// Node.js 0.10 has enumerable non-index properties on buffers.
isBuff&&("offset"==key||"parent"==key)||
// PhantomJS 2 has enumerable non-index properties on typed arrays.
isType&&("buffer"==key||"byteLength"==key||"byteOffset"==key)||
// Skip index properties.
isIndex(key,length))||result.push(key);return result}
/**
 * This function is like `assignValue` except that it doesn't assign
 * `undefined` values.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */function assignMergeValue(object,key,value){(void 0!==value&&!eq(object[key],value)||void 0===value&&!(key in object))&&baseAssignValue(object,key,value)}
/**
 * Assigns `value` to `key` of `object` if the existing value is not equivalent
 * using [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * for equality comparisons.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */function assignValue(object,key,value){var objValue=object[key];hasOwnProperty.call(object,key)&&eq(objValue,value)&&(void 0!==value||key in object)||baseAssignValue(object,key,value)}
/**
 * Gets the index at which the `key` is found in `array` of key-value pairs.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} key The key to search for.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */function assocIndexOf(array,key){var length=array.length;while(length--)if(eq(array[length][0],key))return length;return-1}
/**
 * The base implementation of `assignValue` and `assignMergeValue` without
 * value checks.
 *
 * @private
 * @param {Object} object The object to modify.
 * @param {string} key The key of the property to assign.
 * @param {*} value The value to assign.
 */function baseAssignValue(object,key,value){"__proto__"==key&&defineProperty?defineProperty(object,key,{configurable:!0,enumerable:!0,value:value,writable:!0}):object[key]=value}
/**
 * The base implementation of `baseForOwn` which iterates over `object`
 * properties returned by `keysFunc` and invokes `iteratee` for each property.
 * Iteratee functions may exit iteration early by explicitly returning `false`.
 *
 * @private
 * @param {Object} object The object to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @param {Function} keysFunc The function to get the keys of `object`.
 * @returns {Object} Returns `object`.
 */Hash.prototype.clear=hashClear,Hash.prototype["delete"]=hashDelete,Hash.prototype.get=hashGet,Hash.prototype.has=hashHas,Hash.prototype.set=hashSet,ListCache.prototype.clear=listCacheClear,ListCache.prototype["delete"]=listCacheDelete,ListCache.prototype.get=listCacheGet,ListCache.prototype.has=listCacheHas,ListCache.prototype.set=listCacheSet,MapCache.prototype.clear=mapCacheClear,MapCache.prototype["delete"]=mapCacheDelete,MapCache.prototype.get=mapCacheGet,MapCache.prototype.has=mapCacheHas,MapCache.prototype.set=mapCacheSet,Stack.prototype.clear=stackClear,Stack.prototype["delete"]=stackDelete,Stack.prototype.get=stackGet,Stack.prototype.has=stackHas,Stack.prototype.set=stackSet;var baseFor=createBaseFor();
/**
 * The base implementation of `getTag` without fallbacks for buggy environments.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the `toStringTag`.
 */function baseGetTag(value){return null==value?void 0===value?undefinedTag:nullTag:symToStringTag&&symToStringTag in Object(value)?getRawTag(value):objectToString(value)}
/**
 * The base implementation of `_.isArguments`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 */function baseIsArguments(value){return isObjectLike(value)&&baseGetTag(value)==argsTag}
/**
 * The base implementation of `_.isNative` without bad shim checks.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a native function,
 *  else `false`.
 */function baseIsNative(value){if(!isObject(value)||isMasked(value))return!1;var pattern=isFunction(value)?reIsNative:reIsHostCtor;return pattern.test(toSource(value))}
/**
 * The base implementation of `_.isTypedArray` without Node.js optimizations.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a typed array, else `false`.
 */function baseIsTypedArray(value){return isObjectLike(value)&&isLength(value.length)&&!!typedArrayTags[baseGetTag(value)]}
/**
 * The base implementation of `_.keysIn` which doesn't treat sparse arrays as dense.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */function baseKeysIn(object){if(!isObject(object))return nativeKeysIn(object);var isProto=isPrototype(object),result=[];for(var key in object)("constructor"!=key||!isProto&&hasOwnProperty.call(object,key))&&result.push(key);return result}
/**
 * The base implementation of `_.merge` without support for multiple sources.
 *
 * @private
 * @param {Object} object The destination object.
 * @param {Object} source The source object.
 * @param {number} srcIndex The index of `source`.
 * @param {Function} [customizer] The function to customize merged values.
 * @param {Object} [stack] Tracks traversed source values and their merged
 *  counterparts.
 */function baseMerge(object,source,srcIndex,customizer,stack){object!==source&&baseFor(source,(function(srcValue,key){if(stack||(stack=new Stack),isObject(srcValue))baseMergeDeep(object,source,key,srcIndex,baseMerge,customizer,stack);else{var newValue=customizer?customizer(safeGet(object,key),srcValue,key+"",object,source,stack):void 0;void 0===newValue&&(newValue=srcValue),assignMergeValue(object,key,newValue)}}),keysIn)}
/**
 * A specialized version of `baseMerge` for arrays and objects which performs
 * deep merges and tracks traversed objects enabling objects with circular
 * references to be merged.
 *
 * @private
 * @param {Object} object The destination object.
 * @param {Object} source The source object.
 * @param {string} key The key of the value to merge.
 * @param {number} srcIndex The index of `source`.
 * @param {Function} mergeFunc The function to merge values.
 * @param {Function} [customizer] The function to customize assigned values.
 * @param {Object} [stack] Tracks traversed source values and their merged
 *  counterparts.
 */function baseMergeDeep(object,source,key,srcIndex,mergeFunc,customizer,stack){var objValue=safeGet(object,key),srcValue=safeGet(source,key),stacked=stack.get(srcValue);if(stacked)assignMergeValue(object,key,stacked);else{var newValue=customizer?customizer(objValue,srcValue,key+"",object,source,stack):void 0,isCommon=void 0===newValue;if(isCommon){var isArr=isArray(srcValue),isBuff=!isArr&&isBuffer(srcValue),isTyped=!isArr&&!isBuff&&isTypedArray(srcValue);newValue=srcValue,isArr||isBuff||isTyped?isArray(objValue)?newValue=objValue:isArrayLikeObject(objValue)?newValue=copyArray(objValue):isBuff?(isCommon=!1,newValue=cloneBuffer(srcValue,!0)):isTyped?(isCommon=!1,newValue=cloneTypedArray(srcValue,!0)):newValue=[]:isPlainObject(srcValue)||isArguments(srcValue)?(newValue=objValue,isArguments(objValue)?newValue=toPlainObject(objValue):isObject(objValue)&&!isFunction(objValue)||(newValue=initCloneObject(srcValue))):isCommon=!1}isCommon&&(
// Recursively merge objects and arrays (susceptible to call stack limits).
stack.set(srcValue,newValue),mergeFunc(newValue,srcValue,srcIndex,customizer,stack),stack["delete"](srcValue)),assignMergeValue(object,key,newValue)}}
/**
 * The base implementation of `_.rest` which doesn't validate or coerce arguments.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @returns {Function} Returns the new function.
 */function baseRest(func,start){return setToString(overRest(func,start,identity),func+"")}
/**
 * The base implementation of `setToString` without support for hot loop shorting.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */var baseSetToString=defineProperty?function(func,string){return defineProperty(func,"toString",{configurable:!0,enumerable:!1,value:constant(string),writable:!0})}:identity;
/**
 * Creates a clone of  `buffer`.
 *
 * @private
 * @param {Buffer} buffer The buffer to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Buffer} Returns the cloned buffer.
 */function cloneBuffer(buffer,isDeep){if(isDeep)return buffer.slice();var length=buffer.length,result=allocUnsafe?allocUnsafe(length):new buffer.constructor(length);return buffer.copy(result),result}
/**
 * Creates a clone of `arrayBuffer`.
 *
 * @private
 * @param {ArrayBuffer} arrayBuffer The array buffer to clone.
 * @returns {ArrayBuffer} Returns the cloned array buffer.
 */function cloneArrayBuffer(arrayBuffer){var result=new arrayBuffer.constructor(arrayBuffer.byteLength);return new Uint8Array(result).set(new Uint8Array(arrayBuffer)),result}
/**
 * Creates a clone of `typedArray`.
 *
 * @private
 * @param {Object} typedArray The typed array to clone.
 * @param {boolean} [isDeep] Specify a deep clone.
 * @returns {Object} Returns the cloned typed array.
 */function cloneTypedArray(typedArray,isDeep){var buffer=isDeep?cloneArrayBuffer(typedArray.buffer):typedArray.buffer;return new typedArray.constructor(buffer,typedArray.byteOffset,typedArray.length)}
/**
 * Copies the values of `source` to `array`.
 *
 * @private
 * @param {Array} source The array to copy values from.
 * @param {Array} [array=[]] The array to copy values to.
 * @returns {Array} Returns `array`.
 */function copyArray(source,array){var index=-1,length=source.length;array||(array=Array(length));while(++index<length)array[index]=source[index];return array}
/**
 * Copies properties of `source` to `object`.
 *
 * @private
 * @param {Object} source The object to copy properties from.
 * @param {Array} props The property identifiers to copy.
 * @param {Object} [object={}] The object to copy properties to.
 * @param {Function} [customizer] The function to customize copied values.
 * @returns {Object} Returns `object`.
 */function copyObject(source,props,object,customizer){var isNew=!object;object||(object={});var index=-1,length=props.length;while(++index<length){var key=props[index],newValue=customizer?customizer(object[key],source[key],key,object,source):void 0;void 0===newValue&&(newValue=source[key]),isNew?baseAssignValue(object,key,newValue):assignValue(object,key,newValue)}return object}
/**
 * Creates a function like `_.assign`.
 *
 * @private
 * @param {Function} assigner The function to assign values.
 * @returns {Function} Returns the new assigner function.
 */function createAssigner(assigner){return baseRest((function(object,sources){var index=-1,length=sources.length,customizer=length>1?sources[length-1]:void 0,guard=length>2?sources[2]:void 0;customizer=assigner.length>3&&"function"==typeof customizer?(length--,customizer):void 0,guard&&isIterateeCall(sources[0],sources[1],guard)&&(customizer=length<3?void 0:customizer,length=1),object=Object(object);while(++index<length){var source=sources[index];source&&assigner(object,source,index,customizer)}return object}))}
/**
 * Creates a base function for methods like `_.forIn` and `_.forOwn`.
 *
 * @private
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {Function} Returns the new base function.
 */function createBaseFor(fromRight){return function(object,iteratee,keysFunc){var index=-1,iterable=Object(object),props=keysFunc(object),length=props.length;while(length--){var key=props[fromRight?length:++index];if(!1===iteratee(iterable[key],key,iterable))break}return object}}
/**
 * Used by `_.defaultsDeep` to customize its `_.merge` use to merge source
 * objects into destination objects that are passed thru.
 *
 * @private
 * @param {*} objValue The destination value.
 * @param {*} srcValue The source value.
 * @param {string} key The key of the property to merge.
 * @param {Object} object The parent object of `objValue`.
 * @param {Object} source The parent object of `srcValue`.
 * @param {Object} [stack] Tracks traversed source values and their merged
 *  counterparts.
 * @returns {*} Returns the value to assign.
 */function customDefaultsMerge(objValue,srcValue,key,object,source,stack){return isObject(objValue)&&isObject(srcValue)&&(
// Recursively merge objects and arrays (susceptible to call stack limits).
stack.set(srcValue,objValue),baseMerge(objValue,srcValue,void 0,customDefaultsMerge,stack),stack["delete"](srcValue)),objValue}
/**
 * Gets the data for `map`.
 *
 * @private
 * @param {Object} map The map to query.
 * @param {string} key The reference key.
 * @returns {*} Returns the map data.
 */function getMapData(map,key){var data=map.__data__;return isKeyable(key)?data["string"==typeof key?"string":"hash"]:data.map}
/**
 * Gets the native function at `key` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {string} key The key of the method to get.
 * @returns {*} Returns the function if it's native, else `undefined`.
 */function getNative(object,key){var value=getValue(object,key);return baseIsNative(value)?value:void 0}
/**
 * A specialized version of `baseGetTag` which ignores `Symbol.toStringTag` values.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the raw `toStringTag`.
 */function getRawTag(value){var isOwn=hasOwnProperty.call(value,symToStringTag),tag=value[symToStringTag];try{value[symToStringTag]=void 0;var unmasked=!0}catch(e){}var result=nativeObjectToString.call(value);return unmasked&&(isOwn?value[symToStringTag]=tag:delete value[symToStringTag]),result}
/**
 * Initializes an object clone.
 *
 * @private
 * @param {Object} object The object to clone.
 * @returns {Object} Returns the initialized clone.
 */function initCloneObject(object){return"function"!=typeof object.constructor||isPrototype(object)?{}:baseCreate(getPrototype(object))}
/**
 * Checks if `value` is a valid array-like index.
 *
 * @private
 * @param {*} value The value to check.
 * @param {number} [length=MAX_SAFE_INTEGER] The upper bounds of a valid index.
 * @returns {boolean} Returns `true` if `value` is a valid index, else `false`.
 */function isIndex(value,length){var type=typeof value;return length=null==length?MAX_SAFE_INTEGER:length,!!length&&("number"==type||"symbol"!=type&&reIsUint.test(value))&&value>-1&&value%1==0&&value<length}
/**
 * Checks if the given arguments are from an iteratee call.
 *
 * @private
 * @param {*} value The potential iteratee value argument.
 * @param {*} index The potential iteratee index or key argument.
 * @param {*} object The potential iteratee object argument.
 * @returns {boolean} Returns `true` if the arguments are from an iteratee call,
 *  else `false`.
 */function isIterateeCall(value,index,object){if(!isObject(object))return!1;var type=typeof index;return!!("number"==type?isArrayLike(object)&&isIndex(index,object.length):"string"==type&&index in object)&&eq(object[index],value)}
/**
 * Checks if `value` is suitable for use as unique object key.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is suitable, else `false`.
 */function isKeyable(value){var type=typeof value;return"string"==type||"number"==type||"symbol"==type||"boolean"==type?"__proto__"!==value:null===value}
/**
 * Checks if `func` has its source masked.
 *
 * @private
 * @param {Function} func The function to check.
 * @returns {boolean} Returns `true` if `func` is masked, else `false`.
 */function isMasked(func){return!!maskSrcKey&&maskSrcKey in func}
/**
 * Checks if `value` is likely a prototype object.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a prototype, else `false`.
 */function isPrototype(value){var Ctor=value&&value.constructor,proto="function"==typeof Ctor&&Ctor.prototype||objectProto;return value===proto}
/**
 * This function is like
 * [`Object.keys`](http://ecma-international.org/ecma-262/7.0/#sec-object.keys)
 * except that it includes inherited enumerable properties.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */function nativeKeysIn(object){var result=[];if(null!=object)for(var key in Object(object))result.push(key);return result}
/**
 * Converts `value` to a string using `Object.prototype.toString`.
 *
 * @private
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 */function objectToString(value){return nativeObjectToString.call(value)}
/**
 * A specialized version of `baseRest` which transforms the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @param {Function} transform The rest array transform.
 * @returns {Function} Returns the new function.
 */function overRest(func,start,transform){return start=nativeMax(void 0===start?func.length-1:start,0),function(){var args=arguments,index=-1,length=nativeMax(args.length-start,0),array=Array(length);while(++index<length)array[index]=args[start+index];index=-1;var otherArgs=Array(start+1);while(++index<start)otherArgs[index]=args[index];return otherArgs[start]=transform(array),apply(func,this,otherArgs)}}
/**
 * Gets the value at `key`, unless `key` is "__proto__" or "constructor".
 *
 * @private
 * @param {Object} object The object to query.
 * @param {string} key The key of the property to get.
 * @returns {*} Returns the property value.
 */function safeGet(object,key){if(("constructor"!==key||"function"!==typeof object[key])&&"__proto__"!=key)return object[key]}
/**
 * Sets the `toString` method of `func` to return `string`.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */var setToString=shortOut(baseSetToString);
/**
 * Creates a function that'll short out and invoke `identity` instead
 * of `func` when it's called `HOT_COUNT` or more times in `HOT_SPAN`
 * milliseconds.
 *
 * @private
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new shortable function.
 */function shortOut(func){var count=0,lastCalled=0;return function(){var stamp=nativeNow(),remaining=HOT_SPAN-(stamp-lastCalled);if(lastCalled=stamp,remaining>0){if(++count>=HOT_COUNT)return arguments[0]}else count=0;return func.apply(void 0,arguments)}}
/**
 * Converts `func` to its source code.
 *
 * @private
 * @param {Function} func The function to convert.
 * @returns {string} Returns the source code.
 */function toSource(func){if(null!=func){try{return funcToString.call(func)}catch(e){}try{return func+""}catch(e){}}return""}
/**
 * Performs a
 * [`SameValueZero`](http://ecma-international.org/ecma-262/7.0/#sec-samevaluezero)
 * comparison between two values to determine if they are equivalent.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
 * @example
 *
 * var object = { 'a': 1 };
 * var other = { 'a': 1 };
 *
 * _.eq(object, object);
 * // => true
 *
 * _.eq(object, other);
 * // => false
 *
 * _.eq('a', 'a');
 * // => true
 *
 * _.eq('a', Object('a'));
 * // => false
 *
 * _.eq(NaN, NaN);
 * // => true
 */function eq(value,other){return value===other||value!==value&&other!==other}
/**
 * Checks if `value` is likely an `arguments` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 *  else `false`.
 * @example
 *
 * _.isArguments(function() { return arguments; }());
 * // => true
 *
 * _.isArguments([1, 2, 3]);
 * // => false
 */var isArguments=baseIsArguments(function(){return arguments}())?baseIsArguments:function(value){return isObjectLike(value)&&hasOwnProperty.call(value,"callee")&&!propertyIsEnumerable.call(value,"callee")},isArray=Array.isArray;
/**
 * Checks if `value` is classified as an `Array` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an array, else `false`.
 * @example
 *
 * _.isArray([1, 2, 3]);
 * // => true
 *
 * _.isArray(document.body.children);
 * // => false
 *
 * _.isArray('abc');
 * // => false
 *
 * _.isArray(_.noop);
 * // => false
 */
/**
 * Checks if `value` is array-like. A value is considered array-like if it's
 * not a function and has a `value.length` that's an integer greater than or
 * equal to `0` and less than or equal to `Number.MAX_SAFE_INTEGER`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is array-like, else `false`.
 * @example
 *
 * _.isArrayLike([1, 2, 3]);
 * // => true
 *
 * _.isArrayLike(document.body.children);
 * // => true
 *
 * _.isArrayLike('abc');
 * // => true
 *
 * _.isArrayLike(_.noop);
 * // => false
 */
function isArrayLike(value){return null!=value&&isLength(value.length)&&!isFunction(value)}
/**
 * This method is like `_.isArrayLike` except that it also checks if `value`
 * is an object.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an array-like object,
 *  else `false`.
 * @example
 *
 * _.isArrayLikeObject([1, 2, 3]);
 * // => true
 *
 * _.isArrayLikeObject(document.body.children);
 * // => true
 *
 * _.isArrayLikeObject('abc');
 * // => false
 *
 * _.isArrayLikeObject(_.noop);
 * // => false
 */function isArrayLikeObject(value){return isObjectLike(value)&&isArrayLike(value)}
/**
 * Checks if `value` is a buffer.
 *
 * @static
 * @memberOf _
 * @since 4.3.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a buffer, else `false`.
 * @example
 *
 * _.isBuffer(new Buffer(2));
 * // => true
 *
 * _.isBuffer(new Uint8Array(2));
 * // => false
 */var isBuffer=nativeIsBuffer||stubFalse;
/**
 * Checks if `value` is classified as a `Function` object.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a function, else `false`.
 * @example
 *
 * _.isFunction(_);
 * // => true
 *
 * _.isFunction(/abc/);
 * // => false
 */function isFunction(value){if(!isObject(value))return!1;
// The use of `Object#toString` avoids issues with the `typeof` operator
// in Safari 9 which returns 'object' for typed arrays and other constructors.
var tag=baseGetTag(value);return tag==funcTag||tag==genTag||tag==asyncTag||tag==proxyTag}
/**
 * Checks if `value` is a valid array-like length.
 *
 * **Note:** This method is loosely based on
 * [`ToLength`](http://ecma-international.org/ecma-262/7.0/#sec-tolength).
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a valid length, else `false`.
 * @example
 *
 * _.isLength(3);
 * // => true
 *
 * _.isLength(Number.MIN_VALUE);
 * // => false
 *
 * _.isLength(Infinity);
 * // => false
 *
 * _.isLength('3');
 * // => false
 */function isLength(value){return"number"==typeof value&&value>-1&&value%1==0&&value<=MAX_SAFE_INTEGER}
/**
 * Checks if `value` is the
 * [language type](http://www.ecma-international.org/ecma-262/7.0/#sec-ecmascript-language-types)
 * of `Object`. (e.g. arrays, functions, objects, regexes, `new Number(0)`, and `new String('')`)
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an object, else `false`.
 * @example
 *
 * _.isObject({});
 * // => true
 *
 * _.isObject([1, 2, 3]);
 * // => true
 *
 * _.isObject(_.noop);
 * // => true
 *
 * _.isObject(null);
 * // => false
 */function isObject(value){var type=typeof value;return null!=value&&("object"==type||"function"==type)}
/**
 * Checks if `value` is object-like. A value is object-like if it's not `null`
 * and has a `typeof` result of "object".
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is object-like, else `false`.
 * @example
 *
 * _.isObjectLike({});
 * // => true
 *
 * _.isObjectLike([1, 2, 3]);
 * // => true
 *
 * _.isObjectLike(_.noop);
 * // => false
 *
 * _.isObjectLike(null);
 * // => false
 */function isObjectLike(value){return null!=value&&"object"==typeof value}
/**
 * Checks if `value` is a plain object, that is, an object created by the
 * `Object` constructor or one with a `[[Prototype]]` of `null`.
 *
 * @static
 * @memberOf _
 * @since 0.8.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a plain object, else `false`.
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 * }
 *
 * _.isPlainObject(new Foo);
 * // => false
 *
 * _.isPlainObject([1, 2, 3]);
 * // => false
 *
 * _.isPlainObject({ 'x': 0, 'y': 0 });
 * // => true
 *
 * _.isPlainObject(Object.create(null));
 * // => true
 */function isPlainObject(value){if(!isObjectLike(value)||baseGetTag(value)!=objectTag)return!1;var proto=getPrototype(value);if(null===proto)return!0;var Ctor=hasOwnProperty.call(proto,"constructor")&&proto.constructor;return"function"==typeof Ctor&&Ctor instanceof Ctor&&funcToString.call(Ctor)==objectCtorString}
/**
 * Checks if `value` is classified as a typed array.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a typed array, else `false`.
 * @example
 *
 * _.isTypedArray(new Uint8Array);
 * // => true
 *
 * _.isTypedArray([]);
 * // => false
 */var isTypedArray=nodeIsTypedArray?baseUnary(nodeIsTypedArray):baseIsTypedArray;
/**
 * Converts `value` to a plain object flattening inherited enumerable string
 * keyed properties of `value` to own properties of the plain object.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {Object} Returns the converted plain object.
 * @example
 *
 * function Foo() {
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.assign({ 'a': 1 }, new Foo);
 * // => { 'a': 1, 'b': 2 }
 *
 * _.assign({ 'a': 1 }, _.toPlainObject(new Foo));
 * // => { 'a': 1, 'b': 2, 'c': 3 }
 */function toPlainObject(value){return copyObject(value,keysIn(value))}
/**
 * This method is like `_.defaults` except that it recursively assigns
 * default properties.
 *
 * **Note:** This method mutates `object`.
 *
 * @static
 * @memberOf _
 * @since 3.10.0
 * @category Object
 * @param {Object} object The destination object.
 * @param {...Object} [sources] The source objects.
 * @returns {Object} Returns `object`.
 * @see _.defaults
 * @example
 *
 * _.defaultsDeep({ 'a': { 'b': 2 } }, { 'a': { 'b': 1, 'c': 3 } });
 * // => { 'a': { 'b': 2, 'c': 3 } }
 */var defaultsDeep=baseRest((function(args){return args.push(void 0,customDefaultsMerge),apply(mergeWith,void 0,args)}));
/**
 * Creates an array of the own and inherited enumerable property names of `object`.
 *
 * **Note:** Non-object values are coerced to objects.
 *
 * @static
 * @memberOf _
 * @since 3.0.0
 * @category Object
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 * @example
 *
 * function Foo() {
 *   this.a = 1;
 *   this.b = 2;
 * }
 *
 * Foo.prototype.c = 3;
 *
 * _.keysIn(new Foo);
 * // => ['a', 'b', 'c'] (iteration order is not guaranteed)
 */function keysIn(object){return isArrayLike(object)?arrayLikeKeys(object,!0):baseKeysIn(object)}
/**
 * This method is like `_.merge` except that it accepts `customizer` which
 * is invoked to produce the merged values of the destination and source
 * properties. If `customizer` returns `undefined`, merging is handled by the
 * method instead. The `customizer` is invoked with six arguments:
 * (objValue, srcValue, key, object, source, stack).
 *
 * **Note:** This method mutates `object`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The destination object.
 * @param {...Object} sources The source objects.
 * @param {Function} customizer The function to customize assigned values.
 * @returns {Object} Returns `object`.
 * @example
 *
 * function customizer(objValue, srcValue) {
 *   if (_.isArray(objValue)) {
 *     return objValue.concat(srcValue);
 *   }
 * }
 *
 * var object = { 'a': [1], 'b': [2] };
 * var other = { 'a': [3], 'b': [4] };
 *
 * _.mergeWith(object, other, customizer);
 * // => { 'a': [1, 3], 'b': [2, 4] }
 */var mergeWith=createAssigner((function(object,source,srcIndex,customizer){baseMerge(object,source,srcIndex,customizer)}));
/**
 * Creates a function that returns `value`.
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Util
 * @param {*} value The value to return from the new function.
 * @returns {Function} Returns the new constant function.
 * @example
 *
 * var objects = _.times(2, _.constant({ 'a': 1 }));
 *
 * console.log(objects);
 * // => [{ 'a': 1 }, { 'a': 1 }]
 *
 * console.log(objects[0] === objects[1]);
 * // => true
 */function constant(value){return function(){return value}}
/**
 * This method returns the first argument it receives.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
 * @category Util
 * @param {*} value Any value.
 * @returns {*} Returns `value`.
 * @example
 *
 * var object = { 'a': 1 };
 *
 * console.log(_.identity(object) === object);
 * // => true
 */function identity(value){return value}
/**
 * This method returns `false`.
 *
 * @static
 * @memberOf _
 * @since 4.13.0
 * @category Util
 * @returns {boolean} Returns `false`.
 * @example
 *
 * _.times(2, _.stubFalse);
 * // => [false, false]
 */function stubFalse(){return!1}module.exports=defaultsDeep},
/***/868672:
/***/function(module,__unused_webpack_exports,__webpack_require__){
/*!
 * FullCalendar v3.10.1
 * Docs & License: https://fullcalendar.io/
 * (c) 2019 Adam Shaw
 */
(function(root,factory){module.exports=factory(__webpack_require__(730381),__webpack_require__(619755))})("undefined"!==typeof self&&self,(function(__WEBPACK_EXTERNAL_MODULE_0__,__WEBPACK_EXTERNAL_MODULE_3__){/******/
return function(modules){// webpackBootstrap
/******/ // The module cache
/******/var installedModules={};
/******/
/******/ // The require function
/******/function __nested_webpack_require_862__(moduleId){
/******/
/******/ // Check if module is in cache
/******/if(installedModules[moduleId])
/******/return installedModules[moduleId].exports;
/******/
/******/ // Create a new module (and put it into the cache)
/******/var module=installedModules[moduleId]={
/******/i:moduleId,
/******/l:!1,
/******/exports:{}
/******/};
/******/
/******/ // Execute the module function
/******/
/******/
/******/ // Return the exports of the module
/******/return modules[moduleId].call(module.exports,module,module.exports,__nested_webpack_require_862__),
/******/
/******/ // Flag the module as loaded
/******/module.l=!0,module.exports;
/******/}
/******/
/******/
/******/ // expose the modules object (__webpack_modules__)
/******/
/******/
/******/ // Load entry module and return exports
/******/return __nested_webpack_require_862__.m=modules,
/******/
/******/ // expose the module cache
/******/__nested_webpack_require_862__.c=installedModules,
/******/
/******/ // define getter function for harmony exports
/******/__nested_webpack_require_862__.d=function(exports,name,getter){
/******/__nested_webpack_require_862__.o(exports,name)||
/******/Object.defineProperty(exports,name,{
/******/configurable:!1,
/******/enumerable:!0,
/******/get:getter
/******/})
/******/},
/******/
/******/ // getDefaultExport function for compatibility with non-harmony modules
/******/__nested_webpack_require_862__.n=function(module){
/******/var getter=module&&module.__esModule?
/******/function(){return module["default"]}:
/******/function(){return module};
/******/
/******/return __nested_webpack_require_862__.d(getter,"a",getter),getter;
/******/},
/******/
/******/ // Object.prototype.hasOwnProperty.call
/******/__nested_webpack_require_862__.o=function(object,property){return Object.prototype.hasOwnProperty.call(object,property)},
/******/
/******/ // __webpack_public_path__
/******/__nested_webpack_require_862__.p="",__nested_webpack_require_862__(__nested_webpack_require_862__.s=256);
/******/}
/************************************************************************/
/******/([
/* 0 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE_0__;
/***/},
/* 1 */
/* 2 */
/***/,function(module,exports){
/*
derived from:
https://github.com/Microsoft/tslib/blob/v1.6.0/tslib.js

only include the helpers we need, to keep down filesize
*/
var extendStatics=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(d,b){d.__proto__=b}||function(d,b){for(var p in b)b.hasOwnProperty(p)&&(d[p]=b[p])};exports.__extends=function(d,b){function __(){this.constructor=d}extendStatics(d,b),d.prototype=null===b?Object.create(b):(__.prototype=b.prototype,new __)}},
/* 3 */
/***/function(module,exports){module.exports=__WEBPACK_EXTERNAL_MODULE_3__;
/***/},
/* 4 */
/***/function(module,exports,__nested_webpack_require_3989__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_3989__(0),$=__nested_webpack_require_3989__(3);
/* FullCalendar-specific DOM Utilities
----------------------------------------------------------------------------------------------------------------------*/
// Given the scrollbar widths of some other container, create borders/margins on rowEls in order to match the left
// and right space that was offset by the scrollbars. A 1-pixel border first, then margin beyond that.
function compensateScroll(rowEls,scrollbarWidths){scrollbarWidths.left&&rowEls.css({"border-left-width":1,"margin-left":scrollbarWidths.left-1}),scrollbarWidths.right&&rowEls.css({"border-right-width":1,"margin-right":scrollbarWidths.right-1})}
// Undoes compensateScroll and restores all borders/margins
function uncompensateScroll(rowEls){rowEls.css({"margin-left":"","margin-right":"","border-left-width":"","border-right-width":""})}
// Make the mouse cursor express that an event is not allowed in the current area
function disableCursor(){$("body").addClass("fc-not-allowed")}
// Returns the mouse cursor to its original look
function enableCursor(){$("body").removeClass("fc-not-allowed")}
// Given a total available height to fill, have `els` (essentially child rows) expand to accomodate.
// By default, all elements that are shorter than the recommended height are expanded uniformly, not considering
// any other els that are already too tall. if `shouldRedistribute` is on, it considers these tall rows and
// reduces the available height.
function distributeHeight(els,availableHeight,shouldRedistribute){
// *FLOORING NOTE*: we floor in certain places because zoom can give inaccurate floating-point dimensions,
// and it is better to be shorter than taller, to avoid creating unnecessary scrollbars.
var minOffset1=Math.floor(availableHeight/els.length),minOffset2=Math.floor(availableHeight-minOffset1*(els.length-1)),flexEls=[],flexOffsets=[],flexHeights=[],usedHeight=0;// for non-last element
undistributeHeight(els),// give all elements their natural height
// find elements that are below the recommended height (expandable).
// important to query for heights in a single first pass (to avoid reflow oscillation).
els.each((function(i,el){var minOffset=i===els.length-1?minOffset2:minOffset1,naturalOffset=$(el).outerHeight(!0);naturalOffset<minOffset?(flexEls.push(el),flexOffsets.push(naturalOffset),flexHeights.push($(el).height())):
// this element stretches past recommended height (non-expandable). mark the space as occupied.
usedHeight+=naturalOffset})),
// readjust the recommended height to only consider the height available to non-maxed-out rows.
shouldRedistribute&&(availableHeight-=usedHeight,minOffset1=Math.floor(availableHeight/flexEls.length),minOffset2=Math.floor(availableHeight-minOffset1*(flexEls.length-1))),
// assign heights to all expandable elements
$(flexEls).each((function(i,el){var minOffset=i===flexEls.length-1?minOffset2:minOffset1,naturalOffset=flexOffsets[i],naturalHeight=flexHeights[i],newHeight=minOffset-(naturalOffset-naturalHeight);// subtract the margin/padding
naturalOffset<minOffset&&// we check this again because redistribution might have changed things
$(el).height(newHeight)}))}
// Undoes distrubuteHeight, restoring all els to their natural height
function undistributeHeight(els){els.height("")}
// Given `els`, a jQuery set of <td> cells, find the cell with the largest natural width and set the widths of all the
// cells to be that width.
// PREREQUISITE: if you want a cell to take up width, it needs to have a single inner element w/ display:inline
function matchCellWidths(els){var maxInnerWidth=0;return els.find("> *").each((function(i,innerEl){var innerWidth=$(innerEl).outerWidth();innerWidth>maxInnerWidth&&(maxInnerWidth=innerWidth)})),maxInnerWidth++,// sometimes not accurate of width the text needs to stay on one line. insurance
els.width(maxInnerWidth),maxInnerWidth}
// Given one element that resides inside another,
// Subtracts the height of the inner element from the outer element.
function subtractInnerElHeight(outerEl,innerEl){var diff,both=outerEl.add(innerEl);// undo hack
// effin' IE8/9/10/11 sometimes returns 0 for dimensions. this weird hack was the only thing that worked
return both.css({position:"relative",left:-1}),diff=outerEl.outerHeight()-innerEl.outerHeight(),// grab the dimensions
both.css({position:"",left:""}),diff}
/* Element Geom Utilities
----------------------------------------------------------------------------------------------------------------------*/
// borrowed from https://github.com/jquery/jquery-ui/blob/1.11.0/ui/core.js#L51
function getScrollParent(el){var position=el.css("position"),scrollParent=el.parents().filter((function(){var parent=$(this);return/(auto|scroll)/.test(parent.css("overflow")+parent.css("overflow-y")+parent.css("overflow-x"))})).eq(0);return"fixed"!==position&&scrollParent.length?scrollParent:$(el[0].ownerDocument||document)}
// Queries the outer bounding area of a jQuery element.
// Returns a rectangle with absolute coordinates: left, right (exclusive), top, bottom (exclusive).
// Origin is optional.
function getOuterRect(el,origin){var offset=el.offset(),left=offset.left-(origin?origin.left:0),top=offset.top-(origin?origin.top:0);return{left:left,right:left+el.outerWidth(),top:top,bottom:top+el.outerHeight()}}
// Queries the area within the margin/border/scrollbars of a jQuery element. Does not go within the padding.
// Returns a rectangle with absolute coordinates: left, right (exclusive), top, bottom (exclusive).
// Origin is optional.
// WARNING: given element can't have borders
// NOTE: should use clientLeft/clientTop, but very unreliable cross-browser.
function getClientRect(el,origin){var offset=el.offset(),scrollbarWidths=getScrollbarWidths(el),left=offset.left+getCssFloat(el,"border-left-width")+scrollbarWidths.left-(origin?origin.left:0),top=offset.top+getCssFloat(el,"border-top-width")+scrollbarWidths.top-(origin?origin.top:0);return{left:left,right:left+el[0].clientWidth,top:top,bottom:top+el[0].clientHeight}}
// Queries the area within the margin/border/padding of a jQuery element. Assumed not to have scrollbars.
// Returns a rectangle with absolute coordinates: left, right (exclusive), top, bottom (exclusive).
// Origin is optional.
function getContentRect(el,origin){var offset=el.offset(),left=offset.left+getCssFloat(el,"border-left-width")+getCssFloat(el,"padding-left")-(origin?origin.left:0),top=offset.top+getCssFloat(el,"border-top-width")+getCssFloat(el,"padding-top")-(origin?origin.top:0);// just outside of border, margin not included
return{left:left,right:left+el.width(),top:top,bottom:top+el.height()}}
// Returns the computed left/right/top/bottom scrollbar widths for the given jQuery element.
// WARNING: given element can't have borders (which will cause offsetWidth/offsetHeight to be larger).
// NOTE: should use clientLeft/clientTop, but very unreliable cross-browser.
function getScrollbarWidths(el){var widths,leftRightWidth=el[0].offsetWidth-el[0].clientWidth,bottomWidth=el[0].offsetHeight-el[0].clientHeight;return leftRightWidth=sanitizeScrollbarWidth(leftRightWidth),bottomWidth=sanitizeScrollbarWidth(bottomWidth),widths={left:0,right:0,top:0,bottom:bottomWidth},getIsLeftRtlScrollbars()&&"rtl"===el.css("direction")?// is the scrollbar on the left side?
widths.left=leftRightWidth:widths.right=leftRightWidth,widths}
// The scrollbar width computations in getScrollbarWidths are sometimes flawed when it comes to
// retina displays, rounding, and IE11. Massage them into a usable value.
function sanitizeScrollbarWidth(width){return width=Math.max(0,width),// no negatives
width=Math.round(width),width}
// Logic for determining if, when the element is right-to-left, the scrollbar appears on the left side
exports.compensateScroll=compensateScroll,exports.uncompensateScroll=uncompensateScroll,exports.disableCursor=disableCursor,exports.enableCursor=enableCursor,exports.distributeHeight=distributeHeight,exports.undistributeHeight=undistributeHeight,exports.matchCellWidths=matchCellWidths,exports.subtractInnerElHeight=subtractInnerElHeight,exports.getScrollParent=getScrollParent,exports.getOuterRect=getOuterRect,exports.getClientRect=getClientRect,exports.getContentRect=getContentRect,exports.getScrollbarWidths=getScrollbarWidths;var _isLeftRtlScrollbars=null;function getIsLeftRtlScrollbars(){return null===_isLeftRtlScrollbars&&(_isLeftRtlScrollbars=computeIsLeftRtlScrollbars()),_isLeftRtlScrollbars}function computeIsLeftRtlScrollbars(){var el=$("<div><div/></div>").css({position:"absolute",top:-1e3,left:0,border:0,padding:0,overflow:"scroll",direction:"rtl"}).appendTo("body"),innerEl=el.children(),res=innerEl.offset().left>el.offset().left;// is the inner div shifted to accommodate a left scrollbar?
return el.remove(),res}
// Retrieves a jQuery element's computed CSS value as a floating-point number.
// If the queried value is non-numeric (ex: IE can return "medium" for border width), will just return zero.
function getCssFloat(el,prop){return parseFloat(el.css(prop))||0}
/* Mouse / Touch Utilities
----------------------------------------------------------------------------------------------------------------------*/
// Returns a boolean whether this was a left mouse click and no ctrl key (which means right click on Mac)
function isPrimaryMouseButton(ev){return 1===ev.which&&!ev.ctrlKey}function getEvX(ev){var touches=ev.originalEvent.touches;
// on mobile FF, pageX for touch events is present, but incorrect,
// so, look at touch coordinates first.
return touches&&touches.length?touches[0].pageX:ev.pageX}function getEvY(ev){var touches=ev.originalEvent.touches;
// on mobile FF, pageX for touch events is present, but incorrect,
// so, look at touch coordinates first.
return touches&&touches.length?touches[0].pageY:ev.pageY}function getEvIsTouch(ev){return/^touch/.test(ev.type)}function preventSelection(el){el.addClass("fc-unselectable").on("selectstart",preventDefault)}function allowSelection(el){el.removeClass("fc-unselectable").off("selectstart",preventDefault)}
// Stops a mouse/touch event from doing it's native browser action
function preventDefault(ev){ev.preventDefault()}
/* General Geometry Utils
----------------------------------------------------------------------------------------------------------------------*/
// Returns a new rectangle that is the intersection of the two rectangles. If they don't intersect, returns false
function intersectRects(rect1,rect2){var res={left:Math.max(rect1.left,rect2.left),right:Math.min(rect1.right,rect2.right),top:Math.max(rect1.top,rect2.top),bottom:Math.min(rect1.bottom,rect2.bottom)};return res.left<res.right&&res.top<res.bottom&&res}
// Returns a new point that will have been moved to reside within the given rectangle
function constrainPoint(point,rect){return{left:Math.min(Math.max(point.left,rect.left),rect.right),top:Math.min(Math.max(point.top,rect.top),rect.bottom)}}
// Returns a point that is the center of the given rectangle
function getRectCenter(rect){return{left:(rect.left+rect.right)/2,top:(rect.top+rect.bottom)/2}}
// Subtracts point2's coordinates from point1's coordinates, returning a delta
function diffPoints(point1,point2){return{left:point1.left-point2.left,top:point1.top-point2.top}}
/* Object Ordering by Field
----------------------------------------------------------------------------------------------------------------------*/
function parseFieldSpecs(input){var i,token,specs=[],tokens=[];for("string"===typeof input?tokens=input.split(/\s*,\s*/):"function"===typeof input?tokens=[input]:$.isArray(input)&&(tokens=input),i=0;i<tokens.length;i++)token=tokens[i],"string"===typeof token?specs.push("-"===token.charAt(0)?{field:token.substring(1),order:-1}:{field:token,order:1}):"function"===typeof token&&specs.push({func:token});return specs}function compareByFieldSpecs(obj1,obj2,fieldSpecs,obj1fallback,obj2fallback){var i,cmp;for(i=0;i<fieldSpecs.length;i++)if(cmp=compareByFieldSpec(obj1,obj2,fieldSpecs[i],obj1fallback,obj2fallback),cmp)return cmp;return 0}function compareByFieldSpec(obj1,obj2,fieldSpec,obj1fallback,obj2fallback){if(fieldSpec.func)return fieldSpec.func(obj1,obj2);var val1=obj1[fieldSpec.field],val2=obj2[fieldSpec.field];return null==val1&&obj1fallback&&(val1=obj1fallback[fieldSpec.field]),null==val2&&obj2fallback&&(val2=obj2fallback[fieldSpec.field]),flexibleCompare(val1,val2)*(fieldSpec.order||1)}function flexibleCompare(a,b){return a||b?null==b?-1:null==a?1:"string"===$.type(a)||"string"===$.type(b)?String(a).localeCompare(String(b)):a-b:0}// descending
// Diffs the two moments into a Duration where full-days are recorded first, then the remaining time.
// Moments will have their timezones normalized.
function diffDayTime(a,b){return moment.duration({days:a.clone().stripTime().diff(b.clone().stripTime(),"days"),ms:a.time()-b.time()})}
// Diffs the two moments via their start-of-day (regardless of timezone). Produces whole-day durations.
function diffDay(a,b){return moment.duration({days:a.clone().stripTime().diff(b.clone().stripTime(),"days")})}
// Diffs two moments, producing a duration, made of a whole-unit-increment of the given unit. Uses rounding.
function diffByUnit(a,b,unit){return moment.duration(Math.round(a.diff(b,unit,!0)),// returnFloat=true
unit)}
// Computes the unit name of the largest whole-unit period of time.
// For example, 48 hours will be "days" whereas 49 hours will be "hours".
// Accepts start/end, a range object, or an original duration object.
function computeGreatestUnit(start,end){var i,unit,val;for(i=0;i<exports.unitsDesc.length;i++)if(unit=exports.unitsDesc[i],val=computeRangeAs(unit,start,end),val>=1&&isInt(val))break;return unit;// will be "milliseconds" if nothing else matches
}
// like computeGreatestUnit, but has special abilities to interpret the source input for clues
function computeDurationGreatestUnit(duration,durationInput){var unit=computeGreatestUnit(duration);
// prevent days:7 from being interpreted as a week
return"week"===unit&&"object"===typeof durationInput&&durationInput.days&&(unit="day"),unit}
// Computes the number of units (like "hours") in the given range.
// Range can be a {start,end} object, separate start/end args, or a Duration.
// Results are based on Moment's .as() and .diff() methods, so results can depend on internal handling
// of month-diffing logic (which tends to vary from version to version).
function computeRangeAs(unit,start,end){return null!=end?end.diff(start,unit,!0):moment.isDuration(start)?start.as(unit):start.end.diff(start.start,unit,!0)}
// Intelligently divides a range (specified by a start/end params) by a duration
function divideRangeByDuration(start,end,dur){var months;return durationHasTime(dur)?(end-start)/dur:(months=dur.asMonths(),Math.abs(months)>=1&&isInt(months)?end.diff(start,"months",!0)/months:end.diff(start,"days",!0)/dur.asDays())}
// Intelligently divides one duration by another
function divideDurationByDuration(dur1,dur2){var months1,months2;return durationHasTime(dur1)||durationHasTime(dur2)?dur1/dur2:(months1=dur1.asMonths(),months2=dur2.asMonths(),Math.abs(months1)>=1&&isInt(months1)&&Math.abs(months2)>=1&&isInt(months2)?months1/months2:dur1.asDays()/dur2.asDays())}
// Intelligently multiplies a duration by a number
function multiplyDuration(dur,n){var months;return durationHasTime(dur)?moment.duration(dur*n):(months=dur.asMonths(),Math.abs(months)>=1&&isInt(months)?moment.duration({months:months*n}):moment.duration({days:dur.asDays()*n}))}
// Returns a boolean about whether the given duration has any time parts (hours/minutes/seconds/ms)
function durationHasTime(dur){return Boolean(dur.hours()||dur.minutes()||dur.seconds()||dur.milliseconds())}function isNativeDate(input){return"[object Date]"===Object.prototype.toString.call(input)||input instanceof Date}
// Returns a boolean about whether the given input is a time string, like "06:40:00" or "06:00"
function isTimeString(str){return"string"===typeof str&&/^\d+\:\d+(?:\:\d+\.?(?:\d{3})?)?$/.test(str)}
/* Logging and Debug
----------------------------------------------------------------------------------------------------------------------*/
function log(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];var console=window.console;if(console&&console.log)return console.log.apply(console,args)}function warn(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];var console=window.console;return console&&console.warn?console.warn.apply(console,args):log.apply(null,args)}exports.isPrimaryMouseButton=isPrimaryMouseButton,exports.getEvX=getEvX,exports.getEvY=getEvY,exports.getEvIsTouch=getEvIsTouch,exports.preventSelection=preventSelection,exports.allowSelection=allowSelection,exports.preventDefault=preventDefault,exports.intersectRects=intersectRects,exports.constrainPoint=constrainPoint,exports.getRectCenter=getRectCenter,exports.diffPoints=diffPoints,exports.parseFieldSpecs=parseFieldSpecs,exports.compareByFieldSpecs=compareByFieldSpecs,exports.compareByFieldSpec=compareByFieldSpec,exports.flexibleCompare=flexibleCompare,
/* Date Utilities
----------------------------------------------------------------------------------------------------------------------*/
exports.dayIDs=["sun","mon","tue","wed","thu","fri","sat"],exports.unitsDesc=["year","month","week","day","hour","minute","second","millisecond"],exports.diffDayTime=diffDayTime,exports.diffDay=diffDay,exports.diffByUnit=diffByUnit,exports.computeGreatestUnit=computeGreatestUnit,exports.computeDurationGreatestUnit=computeDurationGreatestUnit,exports.divideRangeByDuration=divideRangeByDuration,exports.divideDurationByDuration=divideDurationByDuration,exports.multiplyDuration=multiplyDuration,exports.durationHasTime=durationHasTime,exports.isNativeDate=isNativeDate,exports.isTimeString=isTimeString,exports.log=log,exports.warn=warn;
/* General Utilities
----------------------------------------------------------------------------------------------------------------------*/
var hasOwnPropMethod={}.hasOwnProperty;
// Merges an array of objects into a single object.
// The second argument allows for an array of property names who's object values will be merged together.
function mergeProps(propObjs,complexProps){var i,name,complexObjs,j,val,props,dest={};if(complexProps)for(i=0;i<complexProps.length;i++){
// collect the trailing object values, stopping when a non-object is discovered
for(name=complexProps[i],complexObjs=[],j=propObjs.length-1;j>=0;j--)if(val=propObjs[j][name],"object"===typeof val)complexObjs.unshift(val);else if(void 0!==val){dest[name]=val;// if there were no objects, this value will be used
break}
// if the trailing values were objects, use the merged value
complexObjs.length&&(dest[name]=mergeProps(complexObjs))}
// copy values into the destination, going from last to first
for(i=propObjs.length-1;i>=0;i--)for(name in props=propObjs[i],props)name in dest||(// if already assigned by previous props or complex props, don't reassign
dest[name]=props[name]);return dest}function copyOwnProps(src,dest){for(var name_1 in src)hasOwnProp(src,name_1)&&(dest[name_1]=src[name_1])}function hasOwnProp(obj,name){return hasOwnPropMethod.call(obj,name)}function applyAll(functions,thisObj,args){if($.isFunction(functions)&&(functions=[functions]),functions){var i=void 0,ret=void 0;for(i=0;i<functions.length;i++)ret=functions[i].apply(thisObj,args)||ret;return ret}}function removeMatching(array,testFunc){var removeCnt=0,i=0;while(i<array.length)testFunc(array[i])?(// truthy value means *remove*
array.splice(i,1),removeCnt++):i++;return removeCnt}function removeExact(array,exactVal){var removeCnt=0,i=0;while(i<array.length)array[i]===exactVal?(array.splice(i,1),removeCnt++):i++;return removeCnt}function isArraysEqual(a0,a1){var i,len=a0.length;if(null==len||len!==a1.length)// not array? or not same length?
return!1;for(i=0;i<len;i++)if(a0[i]!==a1[i])return!1;return!0}function firstDefined(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];for(var i=0;i<args.length;i++)if(void 0!==args[i])return args[i]}function htmlEscape(s){return(s+"").replace(/&/g,"&amp;").replace(/</g,"&lt;").replace(/>/g,"&gt;").replace(/'/g,"&#039;").replace(/"/g,"&quot;").replace(/\n/g,"<br />")}function stripHtmlEntities(text){return text.replace(/&.*?;/g,"")}
// Given a hash of CSS properties, returns a string of CSS.
// Uses property names as-is (no camel-case conversion). Will not make statements for null/undefined values.
function cssToStr(cssProps){var statements=[];return $.each(cssProps,(function(name,val){null!=val&&statements.push(name+":"+val)})),statements.join(";")}
// Given an object hash of HTML attribute names to values,
// generates a string that can be injected between < > in HTML
function attrsToStr(attrs){var parts=[];return $.each(attrs,(function(name,val){null!=val&&parts.push(name+'="'+htmlEscape(val)+'"')})),parts.join(" ")}function capitaliseFirstLetter(str){return str.charAt(0).toUpperCase()+str.slice(1)}function compareNumbers(a,b){return a-b}function isInt(n){return n%1===0}
// Returns a method bound to the given object context.
// Just like one of the jQuery.proxy signatures, but without the undesired behavior of treating the same method with
// different contexts as identical when binding/unbinding events.
function proxy(obj,methodName){var method=obj[methodName];return function(){return method.apply(obj,arguments)}}
// Returns a function, that, as long as it continues to be invoked, will not
// be triggered. The function will be called after it stops being called for
// N milliseconds. If `immediate` is passed, trigger the function on the
// leading edge, instead of the trailing.
// https://github.com/jashkenas/underscore/blob/1.6.0/underscore.js#L714
function debounce(func,wait,immediate){var timeout,args,context,timestamp,result;void 0===immediate&&(immediate=!1);var later=function(){var last=+new Date-timestamp;last<wait?timeout=setTimeout(later,wait-last):(timeout=null,immediate||(result=func.apply(context,args),context=args=null))};return function(){context=this,args=arguments,timestamp=+new Date;var callNow=immediate&&!timeout;return timeout||(timeout=setTimeout(later,wait)),callNow&&(result=func.apply(context,args),context=args=null),result}}exports.mergeProps=mergeProps,exports.copyOwnProps=copyOwnProps,exports.hasOwnProp=hasOwnProp,exports.applyAll=applyAll,exports.removeMatching=removeMatching,exports.removeExact=removeExact,exports.isArraysEqual=isArraysEqual,exports.firstDefined=firstDefined,exports.htmlEscape=htmlEscape,exports.stripHtmlEntities=stripHtmlEntities,exports.cssToStr=cssToStr,exports.attrsToStr=attrsToStr,exports.capitaliseFirstLetter=capitaliseFirstLetter,exports.compareNumbers=compareNumbers,exports.isInt=isInt,exports.proxy=proxy,exports.debounce=debounce},
/* 5 */
/***/function(module,exports,__nested_webpack_require_33967__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_33967__(0),moment_ext_1=__nested_webpack_require_33967__(11),UnzonedRange=/** @class */function(){function UnzonedRange(startInput,endInput){
// TODO: move these into footprint.
// Especially, doesn't make sense for null startMs/endMs.
this.isStart=!0,this.isEnd=!0,moment.isMoment(startInput)&&(startInput=startInput.clone().stripZone()),moment.isMoment(endInput)&&(endInput=endInput.clone().stripZone()),startInput&&(this.startMs=startInput.valueOf()),endInput&&(this.endMs=endInput.valueOf())}
/*
    SIDEEFFECT: will mutate eventRanges.
    Will return a new array result.
    Only works for non-open-ended ranges.
    */return UnzonedRange.invertRanges=function(ranges,constraintRange){var i,dateRange,invertedRanges=[],startMs=constraintRange.startMs;for(
// ranges need to be in order. required for our date-walking algorithm
ranges.sort(compareUnzonedRanges),i=0;i<ranges.length;i++)dateRange=ranges[i],
// add the span of time before the event (if there is any)
dateRange.startMs>startMs&&// compare millisecond time (skip any ambig logic)
invertedRanges.push(new UnzonedRange(startMs,dateRange.startMs)),dateRange.endMs>startMs&&(startMs=dateRange.endMs);
// add the span of time after the last event (if there is any)
return startMs<constraintRange.endMs&&// compare millisecond time (skip any ambig logic)
invertedRanges.push(new UnzonedRange(startMs,constraintRange.endMs)),invertedRanges},UnzonedRange.prototype.intersect=function(otherRange){var startMs=this.startMs,endMs=this.endMs,newRange=null;return null!=otherRange.startMs&&(startMs=null==startMs?otherRange.startMs:Math.max(startMs,otherRange.startMs)),null!=otherRange.endMs&&(endMs=null==endMs?otherRange.endMs:Math.min(endMs,otherRange.endMs)),(null==startMs||null==endMs||startMs<endMs)&&(newRange=new UnzonedRange(startMs,endMs),newRange.isStart=this.isStart&&startMs===this.startMs,newRange.isEnd=this.isEnd&&endMs===this.endMs),newRange},UnzonedRange.prototype.intersectsWith=function(otherRange){return(null==this.endMs||null==otherRange.startMs||this.endMs>otherRange.startMs)&&(null==this.startMs||null==otherRange.endMs||this.startMs<otherRange.endMs)},UnzonedRange.prototype.containsRange=function(innerRange){return(null==this.startMs||null!=innerRange.startMs&&innerRange.startMs>=this.startMs)&&(null==this.endMs||null!=innerRange.endMs&&innerRange.endMs<=this.endMs)},
// `date` can be a moment, a Date, or a millisecond time.
UnzonedRange.prototype.containsDate=function(date){var ms=date.valueOf();return(null==this.startMs||ms>=this.startMs)&&(null==this.endMs||ms<this.endMs)},
// If the given date is not within the given range, move it inside.
// (If it's past the end, make it one millisecond before the end).
// `date` can be a moment, a Date, or a millisecond time.
// Returns a MS-time.
UnzonedRange.prototype.constrainDate=function(date){var ms=date.valueOf();return null!=this.startMs&&ms<this.startMs&&(ms=this.startMs),null!=this.endMs&&ms>=this.endMs&&(ms=this.endMs-1),ms},UnzonedRange.prototype.equals=function(otherRange){return this.startMs===otherRange.startMs&&this.endMs===otherRange.endMs},UnzonedRange.prototype.clone=function(){var range=new UnzonedRange(this.startMs,this.endMs);return range.isStart=this.isStart,range.isEnd=this.isEnd,range},
// Returns an ambig-zoned moment from startMs.
// BEWARE: returned moment is not localized.
// Formatting and start-of-week will be default.
UnzonedRange.prototype.getStart=function(){return null!=this.startMs?moment_ext_1.default.utc(this.startMs).stripZone():null},
// Returns an ambig-zoned moment from startMs.
// BEWARE: returned moment is not localized.
// Formatting and start-of-week will be default.
UnzonedRange.prototype.getEnd=function(){return null!=this.endMs?moment_ext_1.default.utc(this.endMs).stripZone():null},UnzonedRange.prototype.as=function(unit){return moment.utc(this.endMs).diff(moment.utc(this.startMs),unit,!0)},UnzonedRange}();
/*
Only works for non-open-ended ranges.
*/
function compareUnzonedRanges(range1,range2){return range1.startMs-range2.startMs;// earlier ranges go first
}
/***/exports.default=UnzonedRange},
/* 6 */
/***/function(module,exports,__nested_webpack_require_39996__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_39996__(2),$=__nested_webpack_require_39996__(3),ParsableModelMixin_1=__nested_webpack_require_39996__(52),Class_1=__nested_webpack_require_39996__(35),EventDefParser_1=__nested_webpack_require_39996__(36),EventSource=/** @class */function(_super){
// can we do away with calendar? at least for the abstract?
// useful for buildEventDef
function EventSource(calendar){var _this=_super.call(this)||this;return _this.calendar=calendar,_this.className=[],_this.uid=String(EventSource.uuid++),_this}
/*
    rawInput can be any data type!
    */return tslib_1.__extends(EventSource,_super),EventSource.parse=function(rawInput,calendar){var source=new this(calendar);return!("object"!==typeof rawInput||!source.applyProps(rawInput))&&source},EventSource.normalizeId=function(id){return id?String(id):null},EventSource.prototype.fetch=function(start,end,timezone){
// subclasses must implement. must return a promise.
},EventSource.prototype.removeEventDefsById=function(eventDefId){
// optional for subclasses to implement
},EventSource.prototype.removeAllEventDefs=function(){
// optional for subclasses to implement
},
/*
    For compairing/matching
    */
EventSource.prototype.getPrimitive=function(otherSource){
// subclasses must implement
},EventSource.prototype.parseEventDefs=function(rawEventDefs){var i,eventDef,eventDefs=[];for(i=0;i<rawEventDefs.length;i++)eventDef=this.parseEventDef(rawEventDefs[i]),eventDef&&eventDefs.push(eventDef);return eventDefs},EventSource.prototype.parseEventDef=function(rawInput){var calendarTransform=this.calendar.opt("eventDataTransform"),sourceTransform=this.eventDataTransform;return calendarTransform&&(rawInput=calendarTransform(rawInput,this.calendar)),sourceTransform&&(rawInput=sourceTransform(rawInput,this.calendar)),EventDefParser_1.default.parse(rawInput,this)},EventSource.prototype.applyManualStandardProps=function(rawProps){return null!=rawProps.id&&(this.id=EventSource.normalizeId(rawProps.id)),
// TODO: converge with EventDef
$.isArray(rawProps.className)?this.className=rawProps.className:"string"===typeof rawProps.className&&(this.className=rawProps.className.split(/\s+/)),!0},EventSource.uuid=0,EventSource.defineStandardProps=ParsableModelMixin_1.default.defineStandardProps,EventSource.copyVerbatimStandardProps=ParsableModelMixin_1.default.copyVerbatimStandardProps,EventSource}(Class_1.default);exports.default=EventSource,ParsableModelMixin_1.default.mixInto(EventSource),
// Parsing
// ---------------------------------------------------------------------------------------------------------------------
EventSource.defineStandardProps({
// manually process...
id:!1,className:!1,
// automatically transfer...
color:!0,backgroundColor:!0,borderColor:!0,textColor:!0,editable:!0,startEditable:!0,durationEditable:!0,rendering:!0,overlap:!0,constraint:!0,allDayDefault:!0,eventDataTransform:!0})},
/* 7 */
/***/function(module,exports,__nested_webpack_require_44080__){
/*
Utility methods for easily listening to events on another object,
and more importantly, easily unlistening from them.

USAGE:
  import { default as ListenerMixin, ListenerInterface } from './ListenerMixin'
in class:
  listenTo: ListenerInterface['listenTo']
  stopListeningTo: ListenerInterface['stopListeningTo']
after class:
  ListenerMixin.mixInto(TheClass)
*/
Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_44080__(2),$=__nested_webpack_require_44080__(3),Mixin_1=__nested_webpack_require_44080__(15),guid=0,ListenerMixin=/** @class */function(_super){function ListenerMixin(){return null!==_super&&_super.apply(this,arguments)||this}
/*
    Given an `other` object that has on/off methods, bind the given `callback` to an event by the given name.
    The `callback` will be called with the `this` context of the object that .listenTo is being called on.
    Can be called:
      .listenTo(other, eventName, callback)
    OR
      .listenTo(other, {
        eventName1: callback1,
        eventName2: callback2
      })
    */return tslib_1.__extends(ListenerMixin,_super),ListenerMixin.prototype.listenTo=function(other,arg,callback){if("object"===typeof arg)// given dictionary of callbacks
for(var eventName in arg)arg.hasOwnProperty(eventName)&&this.listenTo(other,eventName,arg[eventName]);else"string"===typeof arg&&other.on(arg+"."+this.getListenerNamespace(),// use event namespacing to identify this object
$.proxy(callback,this))},
/*
    Causes the current object to stop listening to events on the `other` object.
    `eventName` is optional. If omitted, will stop listening to ALL events on `other`.
    */
ListenerMixin.prototype.stopListeningTo=function(other,eventName){other.off((eventName||"")+"."+this.getListenerNamespace())},
/*
    Returns a string, unique to this object, to be used for event namespacing
    */
ListenerMixin.prototype.getListenerNamespace=function(){return null==this.listenerId&&(this.listenerId=guid++),"_listener"+this.listenerId},ListenerMixin}(Mixin_1.default);exports.default=ListenerMixin},
/* 8 */
/* 9 */
/***/,function(module,exports,__nested_webpack_require_46839__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_46839__(2),EventDef_1=__nested_webpack_require_46839__(37),EventInstance_1=__nested_webpack_require_46839__(53),EventDateProfile_1=__nested_webpack_require_46839__(16),SingleEventDef=/** @class */function(_super){function SingleEventDef(){return null!==_super&&_super.apply(this,arguments)||this}
/*
    Will receive start/end params, but will be ignored.
    */return tslib_1.__extends(SingleEventDef,_super),SingleEventDef.prototype.buildInstances=function(){return[this.buildInstance()]},SingleEventDef.prototype.buildInstance=function(){return new EventInstance_1.default(this,// definition
this.dateProfile)},SingleEventDef.prototype.isAllDay=function(){return this.dateProfile.isAllDay()},SingleEventDef.prototype.clone=function(){var def=_super.prototype.clone.call(this);return def.dateProfile=this.dateProfile,def},SingleEventDef.prototype.rezone=function(){var calendar=this.source.calendar,dateProfile=this.dateProfile;this.dateProfile=new EventDateProfile_1.default(calendar.moment(dateProfile.start),dateProfile.end?calendar.moment(dateProfile.end):null,calendar)},
/*
    NOTE: if super-method fails, should still attempt to apply
    */
SingleEventDef.prototype.applyManualStandardProps=function(rawProps){var superSuccess=_super.prototype.applyManualStandardProps.call(this,rawProps),dateProfile=EventDateProfile_1.default.parse(rawProps,this.source);// returns null on failure
return!!dateProfile&&(this.dateProfile=dateProfile,
// make sure `date` shows up in the legacy event objects as-is
null!=rawProps.date&&(this.miscProps.date=rawProps.date),superSuccess)},SingleEventDef}(EventDef_1.default);exports.default=SingleEventDef,
// Parsing
// ---------------------------------------------------------------------------------------------------------------------
SingleEventDef.defineStandardProps({start:!1,date:!1,end:!1,allDay:!1})},
/* 10 */
/* 11 */
/***/,function(module,exports,__nested_webpack_require_49405__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_49405__(0),$=__nested_webpack_require_49405__(3),util_1=__nested_webpack_require_49405__(4),ambigDateOfMonthRegex=/^\s*\d{4}-\d\d$/,ambigTimeOrZoneRegex=/^\s*\d{4}-(?:(\d\d-\d\d)|(W\d\d$)|(W\d\d-\d)|(\d\d\d))((T| )(\d\d(:\d\d(:\d\d(\.\d+)?)?)?)?)?$/,newMomentProto=moment.fn;// where we will attach our new methods
exports.newMomentProto=newMomentProto;var oldMomentProto=$.extend({},newMomentProto);// copy of original moment methods
exports.oldMomentProto=oldMomentProto;
// tell momentjs to transfer these properties upon clone
var momentProperties=moment.momentProperties;
/*
Call this if you want Moment's original format method to be used
*/
function oldMomentFormat(mom,formatStr){return oldMomentProto.format.call(mom,formatStr);// oldMomentProto defined in moment-ext.js
}momentProperties.push("_fullCalendar"),momentProperties.push("_ambigTime"),momentProperties.push("_ambigZone"),exports.oldMomentFormat=oldMomentFormat;
// Creating
// -------------------------------------------------------------------------------------------------
// Creates a new moment, similar to the vanilla moment(...) constructor, but with
// extra features (ambiguous time, enhanced formatting). When given an existing moment,
// it will function as a clone (and retain the zone of the moment). Anything else will
// result in a moment in the local zone.
var momentExt=function(){return makeMoment(arguments)};
// Builds an enhanced moment from args. When given an existing moment, it clones. When given a
// native Date, or called with no arguments (the current time), the resulting moment will be local.
// Anything else needs to be "parsed" (a string or an array), and will be affected by:
//    parseAsUTC - if there is no zone information, should we parse the input in UTC?
//    parseZone - if there is zone information, should we force the zone of the moment?
function makeMoment(args,parseAsUTC,parseZone){void 0===parseAsUTC&&(parseAsUTC=!1),void 0===parseZone&&(parseZone=!1);var isAmbigTime,isAmbigZone,ambigMatch,mom,input=args[0],isSingleString=1===args.length&&"string"===typeof input;// flag for extended functionality
return moment.isMoment(input)||util_1.isNativeDate(input)||void 0===input?mom=moment.apply(null,args):(// "parsing" is required
isAmbigTime=!1,isAmbigZone=!1,isSingleString?ambigDateOfMonthRegex.test(input)?(
// accept strings like '2014-05', but convert to the first of the month
input+="-01",args=[input],// for when we pass it on to moment's constructor
isAmbigTime=!0,isAmbigZone=!0):(ambigMatch=ambigTimeOrZoneRegex.exec(input))&&(isAmbigTime=!ambigMatch[5],// no time part?
isAmbigZone=!0):$.isArray(input)&&(
// arrays have no timezone information, so assume ambiguous zone
isAmbigZone=!0),
// otherwise, probably a string with a format
mom=parseAsUTC||isAmbigTime?moment.utc.apply(moment,args):moment.apply(null,args),isAmbigTime?(mom._ambigTime=!0,mom._ambigZone=!0):parseZone&&(// let's record the inputted zone somehow
isAmbigZone?mom._ambigZone=!0:isSingleString&&mom.utcOffset(input))),mom._fullCalendar=!0,mom}
// Week Number
// -------------------------------------------------------------------------------------------------
// Returns the week number, considering the locale's custom week number calcuation
// `weeks` is an alias for `week`
exports.default=momentExt,
// Sames as momentExt, but forces the resulting moment to be in the UTC timezone.
momentExt.utc=function(){var mom=makeMoment(arguments,!0);
// Force it into UTC because makeMoment doesn't guarantee it
// (if given a pre-existing moment for example)
return mom.hasTime()&&// don't give ambiguously-timed moments a UTC zone
mom.utc(),mom},
// Same as momentExt, but when given an ISO8601 string, the timezone offset is preserved.
// ISO8601 strings with no timezone offset will become ambiguously zoned.
momentExt.parseZone=function(){return makeMoment(arguments,!0,!0)},newMomentProto.week=newMomentProto.weeks=function(input){var weekCalc=this._locale._fullCalendar_weekCalc;return null==input&&"function"===typeof weekCalc?weekCalc(this):"ISO"===weekCalc?oldMomentProto.isoWeek.apply(this,arguments):oldMomentProto.week.apply(this,arguments)},
// Time-of-day
// -------------------------------------------------------------------------------------------------
// GETTER
// Returns a Duration with the hours/minutes/seconds/ms values of the moment.
// If the moment has an ambiguous time, a duration of 00:00 will be returned.
// SETTER
// You can supply a Duration, a Moment, or a Duration-like argument.
// When setting the time, and the moment has an ambiguous time, it then becomes unambiguous.
newMomentProto.time=function(time){
// Fallback to the original method (if there is one) if this moment wasn't created via FullCalendar.
// `time` is a generic enough method name where this precaution is necessary to avoid collisions w/ other plugins.
if(!this._fullCalendar)return oldMomentProto.time.apply(this,arguments);if(null==time)// getter
return moment.duration({hours:this.hours(),minutes:this.minutes(),seconds:this.seconds(),milliseconds:this.milliseconds()});// setter
this._ambigTime=!1,// mark that the moment now has a time
moment.isDuration(time)||moment.isMoment(time)||(time=moment.duration(time));
// The day value should cause overflow (so 24 hours becomes 00:00:00 of next day).
// Only for Duration times, not Moment times.
var dayHours=0;
// We need to set the individual fields.
// Can't use startOf('day') then add duration. In case of DST at start of day.
return moment.isDuration(time)&&(dayHours=24*Math.floor(time.asDays())),this.hours(dayHours+time.hours()).minutes(time.minutes()).seconds(time.seconds()).milliseconds(time.milliseconds())},
// Converts the moment to UTC, stripping out its time-of-day and timezone offset,
// but preserving its YMD. A moment with a stripped time will display no time
// nor timezone offset when .format() is called.
newMomentProto.stripTime=function(){return this._ambigTime||(this.utc(!0),// keepLocalTime=true (for keeping *date* value)
// set time to zero
this.set({hours:0,minutes:0,seconds:0,ms:0}),
// Mark the time as ambiguous. This needs to happen after the .utc() call, which might call .utcOffset(),
// which clears all ambig flags.
this._ambigTime=!0,this._ambigZone=!0),this;// for chaining
},
// Returns if the moment has a non-ambiguous time (boolean)
newMomentProto.hasTime=function(){return!this._ambigTime},
// Timezone
// -------------------------------------------------------------------------------------------------
// Converts the moment to UTC, stripping out its timezone offset, but preserving its
// YMD and time-of-day. A moment with a stripped timezone offset will display no
// timezone offset when .format() is called.
newMomentProto.stripZone=function(){var wasAmbigTime;return this._ambigZone||(wasAmbigTime=this._ambigTime,this.utc(!0),// keepLocalTime=true (for keeping date and time values)
// the above call to .utc()/.utcOffset() unfortunately might clear the ambig flags, so restore
this._ambigTime=wasAmbigTime||!1,
// Mark the zone as ambiguous. This needs to happen after the .utc() call, which might call .utcOffset(),
// which clears the ambig flags.
this._ambigZone=!0),this;// for chaining
},
// Returns of the moment has a non-ambiguous timezone offset (boolean)
newMomentProto.hasZone=function(){return!this._ambigZone},
// implicitly marks a zone
newMomentProto.local=function(keepLocalTime){
// for when converting from ambiguously-zoned to local,
// keep the time values when converting from UTC -> local
return oldMomentProto.local.call(this,this._ambigZone||keepLocalTime),
// ensure non-ambiguous
// this probably already happened via local() -> utcOffset(), but don't rely on Moment's internals
this._ambigTime=!1,this._ambigZone=!1,this;// for chaining
},
// implicitly marks a zone
newMomentProto.utc=function(keepLocalTime){return oldMomentProto.utc.call(this,keepLocalTime),
// ensure non-ambiguous
// this probably already happened via utc() -> utcOffset(), but don't rely on Moment's internals
this._ambigTime=!1,this._ambigZone=!1,this},
// implicitly marks a zone (will probably get called upon .utc() and .local())
newMomentProto.utcOffset=function(tzo){return null!=tzo&&(// setter
// these assignments needs to happen before the original zone method is called.
// I forget why, something to do with a browser crash.
this._ambigTime=!1,this._ambigZone=!1),oldMomentProto.utcOffset.apply(this,arguments)}},
/* 12 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});
/*
Meant to be immutable
*/
var ComponentFootprint=/** @class */function(){function ComponentFootprint(unzonedRange,isAllDay){this.isAllDay=!1,// component can choose to ignore this
this.unzonedRange=unzonedRange,this.isAllDay=isAllDay}
/*
    Only works for non-open-ended ranges.
    */return ComponentFootprint.prototype.toLegacy=function(calendar){return{start:calendar.msToMoment(this.unzonedRange.startMs,this.isAllDay),end:calendar.msToMoment(this.unzonedRange.endMs,this.isAllDay)}},ComponentFootprint}();exports.default=ComponentFootprint},
/* 13 */
/***/function(module,exports,__nested_webpack_require_61077__){
/*
USAGE:
  import { default as EmitterMixin, EmitterInterface } from './EmitterMixin'
in class:
  on: EmitterInterface['on']
  one: EmitterInterface['one']
  off: EmitterInterface['off']
  trigger: EmitterInterface['trigger']
  triggerWith: EmitterInterface['triggerWith']
  hasHandlers: EmitterInterface['hasHandlers']
after class:
  EmitterMixin.mixInto(TheClass)
*/
Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_61077__(2),$=__nested_webpack_require_61077__(3),Mixin_1=__nested_webpack_require_61077__(15),EmitterMixin=/** @class */function(_super){function EmitterMixin(){return null!==_super&&_super.apply(this,arguments)||this}
// jQuery-ification via $(this) allows a non-DOM object to have
// the same event handling capabilities (including namespaces).
return tslib_1.__extends(EmitterMixin,_super),EmitterMixin.prototype.on=function(types,handler){return $(this).on(types,this._prepareIntercept(handler)),this;// for chaining
},EmitterMixin.prototype.one=function(types,handler){return $(this).one(types,this._prepareIntercept(handler)),this;// for chaining
},EmitterMixin.prototype._prepareIntercept=function(handler){
// handlers are always called with an "event" object as their first param.
// sneak the `this` context and arguments into the extra parameter object
// and forward them on to the original handler.
var intercept=function(ev,extra){return handler.apply(extra.context||this,extra.args||[])};
// mimick jQuery's internal "proxy" system (risky, I know)
// causing all functions with the same .guid to appear to be the same.
// https://github.com/jquery/jquery/blob/2.2.4/src/core.js#L448
// this is needed for calling .off with the original non-intercept handler.
return handler.guid||(handler.guid=$.guid++),intercept.guid=handler.guid,intercept},EmitterMixin.prototype.off=function(types,handler){return $(this).off(types,handler),this;// for chaining
},EmitterMixin.prototype.trigger=function(types){for(var args=[],_i=1;_i<arguments.length;_i++)args[_i-1]=arguments[_i];
// pass in "extra" info to the intercept
return $(this).triggerHandler(types,{args:args}),this;// for chaining
},EmitterMixin.prototype.triggerWith=function(types,context,args){
// `triggerHandler` is less reliant on the DOM compared to `trigger`.
// pass in "extra" info to the intercept.
return $(this).triggerHandler(types,{context:context,args:args}),this;// for chaining
},EmitterMixin.prototype.hasHandlers=function(type){var hash=$._data(this,"events");// http://blog.jquery.com/2012/08/09/jquery-1-8-released/
return hash&&hash[type]&&hash[type].length>0},EmitterMixin}(Mixin_1.default);exports.default=EmitterMixin},
/* 14 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var Interaction=/** @class */function(){function Interaction(component){this.view=component._getView(),this.component=component}return Interaction.prototype.opt=function(name){return this.view.opt(name)},Interaction.prototype.end=function(){
// subclasses can implement
},Interaction}();exports.default=Interaction},
/* 15 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var Mixin=/** @class */function(){function Mixin(){}return Mixin.mixInto=function(destClass){var _this=this;Object.getOwnPropertyNames(this.prototype).forEach((function(name){destClass.prototype[name]||(// if destination class doesn't already define it
destClass.prototype[name]=_this.prototype[name])}))},
/*
    will override existing methods
    TODO: remove! not used anymore
    */
Mixin.mixOver=function(destClass){var _this=this;Object.getOwnPropertyNames(this.prototype).forEach((function(name){destClass.prototype[name]=_this.prototype[name]}))},Mixin}();exports.default=Mixin},
/* 16 */
/***/function(module,exports,__nested_webpack_require_65886__){Object.defineProperty(exports,"__esModule",{value:!0});var UnzonedRange_1=__nested_webpack_require_65886__(5),EventDateProfile=/** @class */function(){function EventDateProfile(start,end,calendar){this.start=start,this.end=end||null,this.unzonedRange=this.buildUnzonedRange(calendar)}
/*
    Needs an EventSource object
    */return EventDateProfile.parse=function(rawProps,source){var startInput=rawProps.start||rawProps.date,endInput=rawProps.end;if(!startInput)return!1;var calendar=source.calendar,start=calendar.moment(startInput),end=endInput?calendar.moment(endInput):null,forcedAllDay=rawProps.allDay,forceEventDuration=calendar.opt("forceEventDuration");return!!start.isValid()&&(null==forcedAllDay&&(forcedAllDay=source.allDayDefault,null==forcedAllDay&&(forcedAllDay=calendar.opt("allDayDefault"))),!0===forcedAllDay?(start.stripTime(),end&&end.stripTime()):!1===forcedAllDay&&(start.hasTime()||start.time(0),end&&!end.hasTime()&&end.time(0)),!end||end.isValid()&&end.isAfter(start)||(end=null),!end&&forceEventDuration&&(end=calendar.getDefaultEventEnd(!start.hasTime(),start)),new EventDateProfile(start,end,calendar))},EventDateProfile.isStandardProp=function(propName){return"start"===propName||"date"===propName||"end"===propName||"allDay"===propName},EventDateProfile.prototype.isAllDay=function(){return!(this.start.hasTime()||this.end&&this.end.hasTime())},
/*
    Needs a Calendar object
    */
EventDateProfile.prototype.buildUnzonedRange=function(calendar){var startMs=this.start.clone().stripZone().valueOf(),endMs=this.getEnd(calendar).stripZone().valueOf();return new UnzonedRange_1.default(startMs,endMs)},
/*
    Needs a Calendar object
    */
EventDateProfile.prototype.getEnd=function(calendar){return this.end?this.end.clone():
// derive the end from the start and allDay. compute allDay if necessary
calendar.getDefaultEventEnd(this.isAllDay(),this.start)},EventDateProfile}();
/*
Meant to be immutable
*/exports.default=EventDateProfile},
/* 17 */
/***/function(module,exports,__nested_webpack_require_68892__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_68892__(2),util_1=__nested_webpack_require_68892__(4),DragListener_1=__nested_webpack_require_68892__(59),HitDragListener=/** @class */function(_super){function HitDragListener(component,options){var _this=_super.call(this,options)||this;return _this.component=component,_this}
// Called when drag listening starts (but a real drag has not necessarily began).
// ev might be undefined if dragging was started manually.
return tslib_1.__extends(HitDragListener,_super),HitDragListener.prototype.handleInteractionStart=function(ev){var subjectRect,origPoint,point,subjectEl=this.subjectEl;this.component.hitsNeeded(),this.computeScrollBounds(),// for autoscroll
ev?(origPoint={left:util_1.getEvX(ev),top:util_1.getEvY(ev)},point=origPoint,
// constrain the point to bounds of the element being dragged
subjectEl&&(subjectRect=util_1.getOuterRect(subjectEl),// used for centering as well
point=util_1.constrainPoint(point,subjectRect)),this.origHit=this.queryHit(point.left,point.top),
// treat the center of the subject as the collision point?
subjectEl&&this.options.subjectCenter&&(
// only consider the area the subject overlaps the hit. best for large subjects.
// TODO: skip this if hit didn't supply left/right/top/bottom
this.origHit&&(subjectRect=util_1.intersectRects(this.origHit,subjectRect)||subjectRect),point=util_1.getRectCenter(subjectRect)),this.coordAdjust=util_1.diffPoints(point,origPoint)):(this.origHit=null,this.coordAdjust=null),
// call the super-method. do it after origHit has been computed
_super.prototype.handleInteractionStart.call(this,ev)},
// Called when the actual drag has started
HitDragListener.prototype.handleDragStart=function(ev){var hit;_super.prototype.handleDragStart.call(this,ev),
// might be different from this.origHit if the min-distance is large
hit=this.queryHit(util_1.getEvX(ev),util_1.getEvY(ev)),
// report the initial hit the mouse is over
// especially important if no min-distance and drag starts immediately
hit&&this.handleHitOver(hit)},
// Called when the drag moves
HitDragListener.prototype.handleDrag=function(dx,dy,ev){var hit;_super.prototype.handleDrag.call(this,dx,dy,ev),hit=this.queryHit(util_1.getEvX(ev),util_1.getEvY(ev)),isHitsEqual(hit,this.hit)||(// a different hit than before?
this.hit&&this.handleHitOut(),hit&&this.handleHitOver(hit))},
// Called when dragging has been stopped
HitDragListener.prototype.handleDragEnd=function(ev){this.handleHitDone(),_super.prototype.handleDragEnd.call(this,ev)},
// Called when a the mouse has just moved over a new hit
HitDragListener.prototype.handleHitOver=function(hit){var isOrig=isHitsEqual(hit,this.origHit);this.hit=hit,this.trigger("hitOver",this.hit,isOrig,this.origHit)},
// Called when the mouse has just moved out of a hit
HitDragListener.prototype.handleHitOut=function(){this.hit&&(this.trigger("hitOut",this.hit),this.handleHitDone(),this.hit=null)},
// Called after a hitOut. Also called before a dragStop
HitDragListener.prototype.handleHitDone=function(){this.hit&&this.trigger("hitDone",this.hit)},
// Called when the interaction ends, whether there was a real drag or not
HitDragListener.prototype.handleInteractionEnd=function(ev,isCancelled){_super.prototype.handleInteractionEnd.call(this,ev,isCancelled),this.origHit=null,this.hit=null,this.component.hitsNotNeeded()},
// Called when scrolling has stopped, whether through auto scroll, or the user scrolling
HitDragListener.prototype.handleScrollEnd=function(){_super.prototype.handleScrollEnd.call(this),
// hits' absolute positions will be in new places after a user's scroll.
// HACK for recomputing.
this.isDragging&&(this.component.releaseHits(),this.component.prepareHits())},
// Gets the hit underneath the coordinates for the given mouse event
HitDragListener.prototype.queryHit=function(left,top){return this.coordAdjust&&(left+=this.coordAdjust.left,top+=this.coordAdjust.top),this.component.queryHit(left,top)},HitDragListener}(DragListener_1.default);
// Returns `true` if the hits are identically equal. `false` otherwise. Must be from the same component.
// Two null values will be considered equal, as two "out of the component" states are the same.
function isHitsEqual(hit0,hit1){return!hit0&&!hit1||!(!hit0||!hit1)&&(hit0.component===hit1.component&&isHitPropsWithin(hit0,hit1)&&isHitPropsWithin(hit1,hit0))}
// Returns true if all of subHit's non-standard properties are within superHit
function isHitPropsWithin(subHit,superHit){for(var propName in subHit)if(!/^(component|left|right|top|bottom)$/.test(propName)&&subHit[propName]!==superHit[propName])return!1;return!0}
/***/exports.default=HitDragListener},
/* 18 */
/***/function(module,exports,__nested_webpack_require_75712__){Object.defineProperty(exports,"__esModule",{value:!0}),exports.version="3.10.1",
// When introducing internal API incompatibilities (where fullcalendar plugins would break),
// the minor version of the calendar should be upped (ex: 2.7.2 -> 2.8.0)
// and the below integer should be incremented.
exports.internalApiVersion=12;var util_1=__nested_webpack_require_75712__(4);exports.applyAll=util_1.applyAll,exports.debounce=util_1.debounce,exports.isInt=util_1.isInt,exports.htmlEscape=util_1.htmlEscape,exports.cssToStr=util_1.cssToStr,exports.proxy=util_1.proxy,exports.capitaliseFirstLetter=util_1.capitaliseFirstLetter,exports.getOuterRect=util_1.getOuterRect,exports.getClientRect=util_1.getClientRect,exports.getContentRect=util_1.getContentRect,exports.getScrollbarWidths=util_1.getScrollbarWidths,exports.preventDefault=util_1.preventDefault,exports.parseFieldSpecs=util_1.parseFieldSpecs,exports.compareByFieldSpecs=util_1.compareByFieldSpecs,exports.compareByFieldSpec=util_1.compareByFieldSpec,exports.flexibleCompare=util_1.flexibleCompare,exports.computeGreatestUnit=util_1.computeGreatestUnit,exports.divideRangeByDuration=util_1.divideRangeByDuration,exports.divideDurationByDuration=util_1.divideDurationByDuration,exports.multiplyDuration=util_1.multiplyDuration,exports.durationHasTime=util_1.durationHasTime,exports.log=util_1.log,exports.warn=util_1.warn,exports.removeExact=util_1.removeExact,exports.intersectRects=util_1.intersectRects,exports.allowSelection=util_1.allowSelection,exports.attrsToStr=util_1.attrsToStr,exports.compareNumbers=util_1.compareNumbers,exports.compensateScroll=util_1.compensateScroll,exports.computeDurationGreatestUnit=util_1.computeDurationGreatestUnit,exports.constrainPoint=util_1.constrainPoint,exports.copyOwnProps=util_1.copyOwnProps,exports.diffByUnit=util_1.diffByUnit,exports.diffDay=util_1.diffDay,exports.diffDayTime=util_1.diffDayTime,exports.diffPoints=util_1.diffPoints,exports.disableCursor=util_1.disableCursor,exports.distributeHeight=util_1.distributeHeight,exports.enableCursor=util_1.enableCursor,exports.firstDefined=util_1.firstDefined,exports.getEvIsTouch=util_1.getEvIsTouch,exports.getEvX=util_1.getEvX,exports.getEvY=util_1.getEvY,exports.getRectCenter=util_1.getRectCenter,exports.getScrollParent=util_1.getScrollParent,exports.hasOwnProp=util_1.hasOwnProp,exports.isArraysEqual=util_1.isArraysEqual,exports.isNativeDate=util_1.isNativeDate,exports.isPrimaryMouseButton=util_1.isPrimaryMouseButton,exports.isTimeString=util_1.isTimeString,exports.matchCellWidths=util_1.matchCellWidths,exports.mergeProps=util_1.mergeProps,exports.preventSelection=util_1.preventSelection,exports.removeMatching=util_1.removeMatching,exports.stripHtmlEntities=util_1.stripHtmlEntities,exports.subtractInnerElHeight=util_1.subtractInnerElHeight,exports.uncompensateScroll=util_1.uncompensateScroll,exports.undistributeHeight=util_1.undistributeHeight,exports.dayIDs=util_1.dayIDs,exports.unitsDesc=util_1.unitsDesc;var date_formatting_1=__nested_webpack_require_75712__(49);exports.formatDate=date_formatting_1.formatDate,exports.formatRange=date_formatting_1.formatRange,exports.queryMostGranularFormatUnit=date_formatting_1.queryMostGranularFormatUnit;var locale_1=__nested_webpack_require_75712__(32);exports.datepickerLocale=locale_1.datepickerLocale,exports.locale=locale_1.locale,exports.getMomentLocaleData=locale_1.getMomentLocaleData,exports.populateInstanceComputableOptions=locale_1.populateInstanceComputableOptions;var util_2=__nested_webpack_require_75712__(19);exports.eventDefsToEventInstances=util_2.eventDefsToEventInstances,exports.eventFootprintToComponentFootprint=util_2.eventFootprintToComponentFootprint,exports.eventInstanceToEventRange=util_2.eventInstanceToEventRange,exports.eventInstanceToUnzonedRange=util_2.eventInstanceToUnzonedRange,exports.eventRangeToEventFootprint=util_2.eventRangeToEventFootprint;var moment_ext_1=__nested_webpack_require_75712__(11);exports.moment=moment_ext_1.default;var EmitterMixin_1=__nested_webpack_require_75712__(13);exports.EmitterMixin=EmitterMixin_1.default;var ListenerMixin_1=__nested_webpack_require_75712__(7);exports.ListenerMixin=ListenerMixin_1.default;var Model_1=__nested_webpack_require_75712__(51);exports.Model=Model_1.default;var Constraints_1=__nested_webpack_require_75712__(217);exports.Constraints=Constraints_1.default;var DateProfileGenerator_1=__nested_webpack_require_75712__(55);exports.DateProfileGenerator=DateProfileGenerator_1.default;var UnzonedRange_1=__nested_webpack_require_75712__(5);exports.UnzonedRange=UnzonedRange_1.default;var ComponentFootprint_1=__nested_webpack_require_75712__(12);exports.ComponentFootprint=ComponentFootprint_1.default;var BusinessHourGenerator_1=__nested_webpack_require_75712__(218);exports.BusinessHourGenerator=BusinessHourGenerator_1.default;var EventPeriod_1=__nested_webpack_require_75712__(219);exports.EventPeriod=EventPeriod_1.default;var EventManager_1=__nested_webpack_require_75712__(220);exports.EventManager=EventManager_1.default;var EventDef_1=__nested_webpack_require_75712__(37);exports.EventDef=EventDef_1.default;var EventDefMutation_1=__nested_webpack_require_75712__(39);exports.EventDefMutation=EventDefMutation_1.default;var EventDefParser_1=__nested_webpack_require_75712__(36);exports.EventDefParser=EventDefParser_1.default;var EventInstance_1=__nested_webpack_require_75712__(53);exports.EventInstance=EventInstance_1.default;var EventRange_1=__nested_webpack_require_75712__(50);exports.EventRange=EventRange_1.default;var RecurringEventDef_1=__nested_webpack_require_75712__(54);exports.RecurringEventDef=RecurringEventDef_1.default;var SingleEventDef_1=__nested_webpack_require_75712__(9);exports.SingleEventDef=SingleEventDef_1.default;var EventDefDateMutation_1=__nested_webpack_require_75712__(40);exports.EventDefDateMutation=EventDefDateMutation_1.default;var EventDateProfile_1=__nested_webpack_require_75712__(16);exports.EventDateProfile=EventDateProfile_1.default;var EventSourceParser_1=__nested_webpack_require_75712__(38);exports.EventSourceParser=EventSourceParser_1.default;var EventSource_1=__nested_webpack_require_75712__(6);exports.EventSource=EventSource_1.default;var ThemeRegistry_1=__nested_webpack_require_75712__(57);exports.defineThemeSystem=ThemeRegistry_1.defineThemeSystem,exports.getThemeSystemClass=ThemeRegistry_1.getThemeSystemClass;var EventInstanceGroup_1=__nested_webpack_require_75712__(20);exports.EventInstanceGroup=EventInstanceGroup_1.default;var ArrayEventSource_1=__nested_webpack_require_75712__(56);exports.ArrayEventSource=ArrayEventSource_1.default;var FuncEventSource_1=__nested_webpack_require_75712__(223);exports.FuncEventSource=FuncEventSource_1.default;var JsonFeedEventSource_1=__nested_webpack_require_75712__(224);exports.JsonFeedEventSource=JsonFeedEventSource_1.default;var EventFootprint_1=__nested_webpack_require_75712__(34);exports.EventFootprint=EventFootprint_1.default;var Class_1=__nested_webpack_require_75712__(35);exports.Class=Class_1.default;var Mixin_1=__nested_webpack_require_75712__(15);exports.Mixin=Mixin_1.default;var CoordCache_1=__nested_webpack_require_75712__(58);exports.CoordCache=CoordCache_1.default;var Iterator_1=__nested_webpack_require_75712__(225);exports.Iterator=Iterator_1.default;var DragListener_1=__nested_webpack_require_75712__(59);exports.DragListener=DragListener_1.default;var HitDragListener_1=__nested_webpack_require_75712__(17);exports.HitDragListener=HitDragListener_1.default;var MouseFollower_1=__nested_webpack_require_75712__(226);exports.MouseFollower=MouseFollower_1.default;var ParsableModelMixin_1=__nested_webpack_require_75712__(52);exports.ParsableModelMixin=ParsableModelMixin_1.default;var Popover_1=__nested_webpack_require_75712__(227);exports.Popover=Popover_1.default;var Promise_1=__nested_webpack_require_75712__(21);exports.Promise=Promise_1.default;var TaskQueue_1=__nested_webpack_require_75712__(228);exports.TaskQueue=TaskQueue_1.default;var RenderQueue_1=__nested_webpack_require_75712__(229);exports.RenderQueue=RenderQueue_1.default;var Scroller_1=__nested_webpack_require_75712__(41);exports.Scroller=Scroller_1.default;var Theme_1=__nested_webpack_require_75712__(22);exports.Theme=Theme_1.default;var Component_1=__nested_webpack_require_75712__(230);exports.Component=Component_1.default;var DateComponent_1=__nested_webpack_require_75712__(231);exports.DateComponent=DateComponent_1.default;var InteractiveDateComponent_1=__nested_webpack_require_75712__(42);exports.InteractiveDateComponent=InteractiveDateComponent_1.default;var Calendar_1=__nested_webpack_require_75712__(232);exports.Calendar=Calendar_1.default;var View_1=__nested_webpack_require_75712__(43);exports.View=View_1.default;var ViewRegistry_1=__nested_webpack_require_75712__(24);exports.defineView=ViewRegistry_1.defineView,exports.getViewConfig=ViewRegistry_1.getViewConfig;var DayTableMixin_1=__nested_webpack_require_75712__(60);exports.DayTableMixin=DayTableMixin_1.default;var BusinessHourRenderer_1=__nested_webpack_require_75712__(61);exports.BusinessHourRenderer=BusinessHourRenderer_1.default;var EventRenderer_1=__nested_webpack_require_75712__(44);exports.EventRenderer=EventRenderer_1.default;var FillRenderer_1=__nested_webpack_require_75712__(62);exports.FillRenderer=FillRenderer_1.default;var HelperRenderer_1=__nested_webpack_require_75712__(63);exports.HelperRenderer=HelperRenderer_1.default;var ExternalDropping_1=__nested_webpack_require_75712__(233);exports.ExternalDropping=ExternalDropping_1.default;var EventResizing_1=__nested_webpack_require_75712__(234);exports.EventResizing=EventResizing_1.default;var EventPointing_1=__nested_webpack_require_75712__(64);exports.EventPointing=EventPointing_1.default;var EventDragging_1=__nested_webpack_require_75712__(235);exports.EventDragging=EventDragging_1.default;var DateSelecting_1=__nested_webpack_require_75712__(236);exports.DateSelecting=DateSelecting_1.default;var DateClicking_1=__nested_webpack_require_75712__(237);exports.DateClicking=DateClicking_1.default;var Interaction_1=__nested_webpack_require_75712__(14);exports.Interaction=Interaction_1.default;var StandardInteractionsMixin_1=__nested_webpack_require_75712__(65);exports.StandardInteractionsMixin=StandardInteractionsMixin_1.default;var AgendaView_1=__nested_webpack_require_75712__(238);exports.AgendaView=AgendaView_1.default;var TimeGrid_1=__nested_webpack_require_75712__(239);exports.TimeGrid=TimeGrid_1.default;var TimeGridEventRenderer_1=__nested_webpack_require_75712__(240);exports.TimeGridEventRenderer=TimeGridEventRenderer_1.default;var TimeGridFillRenderer_1=__nested_webpack_require_75712__(242);exports.TimeGridFillRenderer=TimeGridFillRenderer_1.default;var TimeGridHelperRenderer_1=__nested_webpack_require_75712__(241);exports.TimeGridHelperRenderer=TimeGridHelperRenderer_1.default;var DayGrid_1=__nested_webpack_require_75712__(66);exports.DayGrid=DayGrid_1.default;var DayGridEventRenderer_1=__nested_webpack_require_75712__(243);exports.DayGridEventRenderer=DayGridEventRenderer_1.default;var DayGridFillRenderer_1=__nested_webpack_require_75712__(245);exports.DayGridFillRenderer=DayGridFillRenderer_1.default;var DayGridHelperRenderer_1=__nested_webpack_require_75712__(244);exports.DayGridHelperRenderer=DayGridHelperRenderer_1.default;var BasicView_1=__nested_webpack_require_75712__(67);exports.BasicView=BasicView_1.default;var BasicViewDateProfileGenerator_1=__nested_webpack_require_75712__(68);exports.BasicViewDateProfileGenerator=BasicViewDateProfileGenerator_1.default;var MonthView_1=__nested_webpack_require_75712__(246);exports.MonthView=MonthView_1.default;var MonthViewDateProfileGenerator_1=__nested_webpack_require_75712__(247);exports.MonthViewDateProfileGenerator=MonthViewDateProfileGenerator_1.default;var ListView_1=__nested_webpack_require_75712__(248);exports.ListView=ListView_1.default;var ListEventPointing_1=__nested_webpack_require_75712__(250);exports.ListEventPointing=ListEventPointing_1.default;var ListEventRenderer_1=__nested_webpack_require_75712__(249);exports.ListEventRenderer=ListEventRenderer_1.default},
/* 19 */
/***/function(module,exports,__nested_webpack_require_87820__){Object.defineProperty(exports,"__esModule",{value:!0});var EventRange_1=__nested_webpack_require_87820__(50),EventFootprint_1=__nested_webpack_require_87820__(34),ComponentFootprint_1=__nested_webpack_require_87820__(12);function eventDefsToEventInstances(eventDefs,unzonedRange){var i,eventInstances=[];for(i=0;i<eventDefs.length;i++)eventInstances.push.apply(eventInstances,// append
eventDefs[i].buildInstances(unzonedRange));return eventInstances}function eventInstanceToEventRange(eventInstance){return new EventRange_1.default(eventInstance.dateProfile.unzonedRange,eventInstance.def,eventInstance)}function eventRangeToEventFootprint(eventRange){return new EventFootprint_1.default(new ComponentFootprint_1.default(eventRange.unzonedRange,eventRange.eventDef.isAllDay()),eventRange.eventDef,eventRange.eventInstance)}function eventInstanceToUnzonedRange(eventInstance){return eventInstance.dateProfile.unzonedRange}function eventFootprintToComponentFootprint(eventFootprint){return eventFootprint.componentFootprint}exports.eventDefsToEventInstances=eventDefsToEventInstances,exports.eventInstanceToEventRange=eventInstanceToEventRange,exports.eventRangeToEventFootprint=eventRangeToEventFootprint,exports.eventInstanceToUnzonedRange=eventInstanceToUnzonedRange,exports.eventFootprintToComponentFootprint=eventFootprintToComponentFootprint},
/* 20 */
/***/function(module,exports,__nested_webpack_require_89406__){Object.defineProperty(exports,"__esModule",{value:!0});var UnzonedRange_1=__nested_webpack_require_89406__(5),util_1=__nested_webpack_require_89406__(19),EventRange_1=__nested_webpack_require_89406__(50),EventInstanceGroup=/** @class */function(){function EventInstanceGroup(eventInstances){this.eventInstances=eventInstances||[]}return EventInstanceGroup.prototype.getAllEventRanges=function(constraintRange){return constraintRange?this.sliceNormalRenderRanges(constraintRange):this.eventInstances.map(util_1.eventInstanceToEventRange)},EventInstanceGroup.prototype.sliceRenderRanges=function(constraintRange){return this.isInverse()?this.sliceInverseRenderRanges(constraintRange):this.sliceNormalRenderRanges(constraintRange)},EventInstanceGroup.prototype.sliceNormalRenderRanges=function(constraintRange){var i,eventInstance,slicedRange,eventInstances=this.eventInstances,slicedEventRanges=[];for(i=0;i<eventInstances.length;i++)eventInstance=eventInstances[i],slicedRange=eventInstance.dateProfile.unzonedRange.intersect(constraintRange),slicedRange&&slicedEventRanges.push(new EventRange_1.default(slicedRange,eventInstance.def,eventInstance));return slicedEventRanges},EventInstanceGroup.prototype.sliceInverseRenderRanges=function(constraintRange){var unzonedRanges=this.eventInstances.map(util_1.eventInstanceToUnzonedRange),ownerDef=this.getEventDef();return unzonedRanges=UnzonedRange_1.default.invertRanges(unzonedRanges,constraintRange),unzonedRanges.map((function(unzonedRange){return new EventRange_1.default(unzonedRange,ownerDef);// don't give an EventInstance
}))},EventInstanceGroup.prototype.isInverse=function(){return this.getEventDef().hasInverseRendering()},EventInstanceGroup.prototype.getEventDef=function(){return this.explicitEventDef||this.eventInstances[0].def},EventInstanceGroup}();exports.default=EventInstanceGroup},
/* 21 */
/***/function(module,exports,__nested_webpack_require_92062__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_92062__(3),PromiseStub={construct:function(executor){var deferred=$.Deferred(),promise=deferred.promise();return"function"===typeof executor&&executor((function(val){deferred.resolve(val),attachImmediatelyResolvingThen(promise,val)}),(function(){deferred.reject(),attachImmediatelyRejectingThen(promise)})),promise},resolve:function(val){var deferred=$.Deferred().resolve(val),promise=deferred.promise();return attachImmediatelyResolvingThen(promise,val),promise},reject:function(){var deferred=$.Deferred().reject(),promise=deferred.promise();return attachImmediatelyRejectingThen(promise),promise}};function attachImmediatelyResolvingThen(promise,val){promise.then=function(onResolve){return"function"===typeof onResolve?PromiseStub.resolve(onResolve(val)):promise}}function attachImmediatelyRejectingThen(promise){promise.then=function(onResolve,onReject){return"function"===typeof onReject&&onReject(),promise}}
/***/exports.default=PromiseStub},
/* 22 */
/***/function(module,exports,__nested_webpack_require_93675__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_93675__(3),Theme=/** @class */function(){function Theme(optionsManager){this.optionsManager=optionsManager,this.processIconOverride()}return Theme.prototype.processIconOverride=function(){this.iconOverrideOption&&this.setIconOverride(this.optionsManager.get(this.iconOverrideOption))},Theme.prototype.setIconOverride=function(iconOverrideHash){var iconClassesCopy,buttonName;if($.isPlainObject(iconOverrideHash)){for(buttonName in iconClassesCopy=$.extend({},this.iconClasses),iconOverrideHash)iconClassesCopy[buttonName]=this.applyIconOverridePrefix(iconOverrideHash[buttonName]);this.iconClasses=iconClassesCopy}else!1===iconOverrideHash&&(this.iconClasses={})},Theme.prototype.applyIconOverridePrefix=function(className){var prefix=this.iconOverridePrefix;return prefix&&0!==className.indexOf(prefix)&&(// if not already present
className=prefix+className),className},Theme.prototype.getClass=function(key){return this.classes[key]||""},Theme.prototype.getIconClass=function(buttonName){var className=this.iconClasses[buttonName];return className?this.baseIconClass+" "+className:""},Theme.prototype.getCustomButtonIconClass=function(customButtonProps){var className;return this.iconOverrideCustomButtonOption&&(className=customButtonProps[this.iconOverrideCustomButtonOption],className)?this.baseIconClass+" "+this.applyIconOverridePrefix(className):""},Theme}();exports.default=Theme,Theme.prototype.classes={},Theme.prototype.iconClasses={},Theme.prototype.baseIconClass="",Theme.prototype.iconOverridePrefix=""},
/* 23 */
/***/function(module,exports,__nested_webpack_require_96033__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_96033__(3),exportHooks=__nested_webpack_require_96033__(18),EmitterMixin_1=__nested_webpack_require_96033__(13),ListenerMixin_1=__nested_webpack_require_96033__(7);exportHooks.touchMouseIgnoreWait=500;var globalEmitter=null,neededCount=0,GlobalEmitter=/** @class */function(){function GlobalEmitter(){this.isTouching=!1,this.mouseIgnoreDepth=0}
// gets the singleton
return GlobalEmitter.get=function(){return globalEmitter||(globalEmitter=new GlobalEmitter,globalEmitter.bind()),globalEmitter},
// called when an object knows it will need a GlobalEmitter in the near future.
GlobalEmitter.needed=function(){GlobalEmitter.get(),// ensures globalEmitter
neededCount++},
// called when the object that originally called needed() doesn't need a GlobalEmitter anymore.
GlobalEmitter.unneeded=function(){neededCount--,neededCount||(// nobody else needs it
globalEmitter.unbind(),globalEmitter=null)},GlobalEmitter.prototype.bind=function(){var _this=this;this.listenTo($(document),{touchstart:this.handleTouchStart,touchcancel:this.handleTouchCancel,touchend:this.handleTouchEnd,mousedown:this.handleMouseDown,mousemove:this.handleMouseMove,mouseup:this.handleMouseUp,click:this.handleClick,selectstart:this.handleSelectStart,contextmenu:this.handleContextMenu}),
// because we need to call preventDefault
// because https://www.chromestatus.com/features/5093566007214080
// TODO: investigate performance because this is a global handler
window.addEventListener("touchmove",this.handleTouchMoveProxy=function(ev){_this.handleTouchMove($.Event(ev))},{passive:!1}),
// attach a handler to get called when ANY scroll action happens on the page.
// this was impossible to do with normal on/off because 'scroll' doesn't bubble.
// http://stackoverflow.com/a/32954565/96342
window.addEventListener("scroll",this.handleScrollProxy=function(ev){_this.handleScroll($.Event(ev))},!0)},GlobalEmitter.prototype.unbind=function(){this.stopListeningTo($(document)),window.removeEventListener("touchmove",this.handleTouchMoveProxy,{passive:!1}),window.removeEventListener("scroll",this.handleScrollProxy,!0)},
// Touch Handlers
// -----------------------------------------------------------------------------------------------------------------
GlobalEmitter.prototype.handleTouchStart=function(ev){
// if a previous touch interaction never ended with a touchend, then implicitly end it,
// but since a new touch interaction is about to begin, don't start the mouse ignore period.
this.stopTouch(ev,!0),// skipMouseIgnore=true
this.isTouching=!0,this.trigger("touchstart",ev)},GlobalEmitter.prototype.handleTouchMove=function(ev){this.isTouching&&this.trigger("touchmove",ev)},GlobalEmitter.prototype.handleTouchCancel=function(ev){this.isTouching&&(this.trigger("touchcancel",ev),
// Have touchcancel fire an artificial touchend. That way, handlers won't need to listen to both.
// If touchend fires later, it won't have any effect b/c isTouching will be false.
this.stopTouch(ev))},GlobalEmitter.prototype.handleTouchEnd=function(ev){this.stopTouch(ev)},
// Mouse Handlers
// -----------------------------------------------------------------------------------------------------------------
GlobalEmitter.prototype.handleMouseDown=function(ev){this.shouldIgnoreMouse()||this.trigger("mousedown",ev)},GlobalEmitter.prototype.handleMouseMove=function(ev){this.shouldIgnoreMouse()||this.trigger("mousemove",ev)},GlobalEmitter.prototype.handleMouseUp=function(ev){this.shouldIgnoreMouse()||this.trigger("mouseup",ev)},GlobalEmitter.prototype.handleClick=function(ev){this.shouldIgnoreMouse()||this.trigger("click",ev)},
// Misc Handlers
// -----------------------------------------------------------------------------------------------------------------
GlobalEmitter.prototype.handleSelectStart=function(ev){this.trigger("selectstart",ev)},GlobalEmitter.prototype.handleContextMenu=function(ev){this.trigger("contextmenu",ev)},GlobalEmitter.prototype.handleScroll=function(ev){this.trigger("scroll",ev)},
// Utils
// -----------------------------------------------------------------------------------------------------------------
GlobalEmitter.prototype.stopTouch=function(ev,skipMouseIgnore){void 0===skipMouseIgnore&&(skipMouseIgnore=!1),this.isTouching&&(this.isTouching=!1,this.trigger("touchend",ev),skipMouseIgnore||this.startTouchMouseIgnore())},GlobalEmitter.prototype.startTouchMouseIgnore=function(){var _this=this,wait=exportHooks.touchMouseIgnoreWait;wait&&(this.mouseIgnoreDepth++,setTimeout((function(){_this.mouseIgnoreDepth--}),wait))},GlobalEmitter.prototype.shouldIgnoreMouse=function(){return this.isTouching||Boolean(this.mouseIgnoreDepth)},GlobalEmitter}();exports.default=GlobalEmitter,ListenerMixin_1.default.mixInto(GlobalEmitter),EmitterMixin_1.default.mixInto(GlobalEmitter)},
/* 24 */
/***/function(module,exports,__nested_webpack_require_103166__){Object.defineProperty(exports,"__esModule",{value:!0});var exportHooks=__nested_webpack_require_103166__(18);function defineView(viewName,viewConfig){exports.viewHash[viewName]=viewConfig}function getViewConfig(viewName){return exports.viewHash[viewName]}exports.viewHash={},exportHooks.views=exports.viewHash,exports.defineView=defineView,exports.getViewConfig=getViewConfig},
/* 25 */
/* 26 */,
/* 27 */,
/* 28 */,
/* 29 */,
/* 30 */,
/* 31 */,
/* 32 */
/***/,function(module,exports,__nested_webpack_require_103732__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_103732__(3),moment=__nested_webpack_require_103732__(0),exportHooks=__nested_webpack_require_103732__(18),options_1=__nested_webpack_require_103732__(33),util_1=__nested_webpack_require_103732__(4);exports.localeOptionHash={},exportHooks.locales=exports.localeOptionHash;
// NOTE: can't guarantee any of these computations will run because not every locale has datepicker
// configs, so make sure there are English fallbacks for these in the defaults file.
var dpComputableOptions={buttonText:function(dpOptions){return{
// the translations sometimes wrongly contain HTML entities
prev:util_1.stripHtmlEntities(dpOptions.prevText),next:util_1.stripHtmlEntities(dpOptions.nextText),today:util_1.stripHtmlEntities(dpOptions.currentText)}},
// Produces format strings like "MMMM YYYY" -> "September 2014"
monthYearFormat:function(dpOptions){return dpOptions.showMonthAfterYear?"YYYY["+dpOptions.yearSuffix+"] MMMM":"MMMM YYYY["+dpOptions.yearSuffix+"]"}},momComputableOptions={
// Produces format strings like "ddd M/D" -> "Fri 9/15"
dayOfMonthFormat:function(momOptions,fcOptions){var format=momOptions.longDateFormat("l");// for the format like "M/D/YYYY"
// strip the year off the edge, as well as other misc non-whitespace chars
return format=format.replace(/^Y+[^\w\s]*|[^\w\s]*Y+$/g,""),fcOptions.isRTL?format+=" ddd":format="ddd "+format,format},
// Produces format strings like "h:mma" -> "6:00pm"
mediumTimeFormat:function(momOptions){return momOptions.longDateFormat("LT").replace(/\s*a$/i,"a");// convert AM/PM/am/pm to lowercase. remove any spaces beforehand
},
// Produces format strings like "h(:mm)a" -> "6pm" / "6:30pm"
smallTimeFormat:function(momOptions){return momOptions.longDateFormat("LT").replace(":mm","(:mm)").replace(/(\Wmm)$/,"($1)").replace(/\s*a$/i,"a");// convert AM/PM/am/pm to lowercase. remove any spaces beforehand
},
// Produces format strings like "h(:mm)t" -> "6p" / "6:30p"
extraSmallTimeFormat:function(momOptions){return momOptions.longDateFormat("LT").replace(":mm","(:mm)").replace(/(\Wmm)$/,"($1)").replace(/\s*a$/i,"t");// convert to AM/PM/am/pm to lowercase one-letter. remove any spaces beforehand
},
// Produces format strings like "ha" / "H" -> "6pm" / "18"
hourFormat:function(momOptions){return momOptions.longDateFormat("LT").replace(":mm","").replace(/(\Wmm)$/,"").replace(/\s*a$/i,"a");// convert AM/PM/am/pm to lowercase. remove any spaces beforehand
},
// Produces format strings like "h:mm" -> "6:30" (with no AM/PM)
noMeridiemTimeFormat:function(momOptions){return momOptions.longDateFormat("LT").replace(/\s*a$/i,"");// remove trailing AM/PM
}},instanceComputableOptions={
// Produces format strings for results like "Mo 16"
smallDayDateFormat:function(options){return options.isRTL?"D dd":"dd D"},
// Produces format strings for results like "Wk 5"
weekFormat:function(options){return options.isRTL?"w[ "+options.weekNumberTitle+"]":"["+options.weekNumberTitle+" ]w"},
// Produces format strings for results like "Wk5"
smallWeekFormat:function(options){return options.isRTL?"w["+options.weekNumberTitle+"]":"["+options.weekNumberTitle+"]w"}};
// TODO: make these computable properties in optionsManager
function populateInstanceComputableOptions(options){$.each(instanceComputableOptions,(function(name,func){null==options[name]&&(options[name]=func(options))}))}
// Initialize jQuery UI datepicker translations while using some of the translations
// Will set this as the default locales for datepicker.
function datepickerLocale(localeCode,dpLocaleCode,dpOptions){
// get the FullCalendar internal option hash for this locale. create if necessary
var fcOptions=exports.localeOptionHash[localeCode]||(exports.localeOptionHash[localeCode]={});
// transfer some simple options from datepicker to fc
fcOptions.isRTL=dpOptions.isRTL,fcOptions.weekNumberTitle=dpOptions.weekHeader,
// compute some more complex options from datepicker
$.each(dpComputableOptions,(function(name,func){fcOptions[name]=func(dpOptions)}));var jqDatePicker=$.datepicker;
// is jQuery UI Datepicker is on the page?
jqDatePicker&&(
// Register the locale data.
// FullCalendar and MomentJS use locale codes like "pt-br" but Datepicker
// does it like "pt-BR" or if it doesn't have the locale, maybe just "pt".
// Make an alias so the locale can be referenced either way.
jqDatePicker.regional[dpLocaleCode]=jqDatePicker.regional[localeCode]=// alias
dpOptions,
// Alias 'en' to the default locale data. Do this every time.
jqDatePicker.regional.en=jqDatePicker.regional[""],
// Set as Datepicker's global defaults.
jqDatePicker.setDefaults(dpOptions))}
// Sets FullCalendar-specific translations. Will set the locales as the global default.
function locale(localeCode,newFcOptions){var fcOptions,momOptions;
// get the FullCalendar internal option hash for this locale. create if necessary
fcOptions=exports.localeOptionHash[localeCode]||(exports.localeOptionHash[localeCode]={}),
// provided new options for this locales? merge them in
newFcOptions&&(fcOptions=exports.localeOptionHash[localeCode]=options_1.mergeOptions([fcOptions,newFcOptions])),
// compute locale options that weren't defined.
// always do this. newFcOptions can be undefined when initializing from i18n file,
// so no way to tell if this is an initialization or a default-setting.
momOptions=getMomentLocaleData(localeCode),// will fall back to en
$.each(momComputableOptions,(function(name,func){null==fcOptions[name]&&(fcOptions[name]=func(momOptions,fcOptions))})),
// set it as the default locale for FullCalendar
options_1.globalDefaults.locale=localeCode}
// Returns moment's internal locale data. If doesn't exist, returns English.
function getMomentLocaleData(localeCode){return moment.localeData(localeCode)||moment.localeData("en")}exports.populateInstanceComputableOptions=populateInstanceComputableOptions,exports.datepickerLocale=datepickerLocale,exports.locale=locale,exports.getMomentLocaleData=getMomentLocaleData,
// Initialize English by forcing computation of moment-derived options.
// Also, sets it as the default.
locale("en",options_1.englishDefaults)},
/* 33 */
/***/function(module,exports,__nested_webpack_require_111727__){Object.defineProperty(exports,"__esModule",{value:!0});var util_1=__nested_webpack_require_111727__(4);exports.globalDefaults={titleRangeSeparator:" – ",monthYearFormat:"MMMM YYYY",defaultTimedEventDuration:"02:00:00",defaultAllDayEventDuration:{days:1},forceEventDuration:!1,nextDayThreshold:"09:00:00",
// display
columnHeader:!0,defaultView:"month",aspectRatio:1.35,header:{left:"title",center:"",right:"today prev,next"},weekends:!0,weekNumbers:!1,weekNumberTitle:"W",weekNumberCalculation:"local",
// editable: false,
// nowIndicator: false,
scrollTime:"06:00:00",minTime:"00:00:00",maxTime:"24:00:00",showNonCurrentDates:!0,
// event ajax
lazyFetching:!0,startParam:"start",endParam:"end",timezoneParam:"timezone",timezone:!1,
// allDayDefault: undefined,
// locale
locale:null,isRTL:!1,buttonText:{prev:"prev",next:"next",prevYear:"prev year",nextYear:"next year",year:"year",today:"today",month:"month",week:"week",day:"day"},
// buttonIcons: null,
allDayText:"all-day",
// allows setting a min-height to the event segment to prevent short events overlapping each other
agendaEventMinHeight:0,
// jquery-ui theming
theme:!1,
// themeButtonIcons: null,
// eventResizableFromStart: false,
dragOpacity:.75,dragRevertDuration:500,dragScroll:!0,
// selectable: false,
unselectAuto:!0,
// selectMinDistance: 0,
dropAccept:"*",eventOrder:"title",
// eventRenderWait: null,
eventLimit:!1,eventLimitText:"more",eventLimitClick:"popover",dayPopoverFormat:"LL",handleWindowResize:!0,windowResizeDelay:100,longPressDelay:1e3},exports.englishDefaults={dayPopoverFormat:"dddd, MMMM D"},exports.rtlDefaults={header:{left:"next,prev today",center:"",right:"title"},buttonIcons:{prev:"right-single-arrow",next:"left-single-arrow",prevYear:"right-double-arrow",nextYear:"left-double-arrow"},themeButtonIcons:{prev:"circle-triangle-e",next:"circle-triangle-w",nextYear:"seek-prev",prevYear:"seek-next"}};var complexOptions=["header","footer","buttonText","buttonIcons","themeButtonIcons"];
// Merges an array of option objects into a single object
function mergeOptions(optionObjs){return util_1.mergeProps(optionObjs,complexOptions)}exports.mergeOptions=mergeOptions},
/* 34 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var EventFootprint=/** @class */function(){function EventFootprint(componentFootprint,eventDef,eventInstance){this.componentFootprint=componentFootprint,this.eventDef=eventDef,eventInstance&&(this.eventInstance=eventInstance)}return EventFootprint.prototype.getEventLegacy=function(){return(this.eventInstance||this.eventDef).toLegacy()},EventFootprint}();exports.default=EventFootprint},
/* 35 */
/***/function(module,exports,__nested_webpack_require_115377__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_115377__(2),util_1=__nested_webpack_require_115377__(4),Class=/** @class */function(){function Class(){}
// Called on a class to create a subclass.
// LIMITATION: cannot provide a constructor!
return Class.extend=function(members){var SubClass=/** @class */function(_super){function SubClass(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(SubClass,_super),SubClass}(this);return util_1.copyOwnProps(members,SubClass.prototype),SubClass},
// Adds new member variables/methods to the class's prototype.
// Can be called with another class, or a plain object hash containing new members.
Class.mixin=function(members){util_1.copyOwnProps(members,this.prototype)},Class}();exports.default=Class},
/* 36 */
/***/function(module,exports,__nested_webpack_require_116554__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_116554__(0),util_1=__nested_webpack_require_116554__(4),SingleEventDef_1=__nested_webpack_require_116554__(9),RecurringEventDef_1=__nested_webpack_require_116554__(54);exports.default={parse:function(eventInput,source){return util_1.isTimeString(eventInput.start)||moment.isDuration(eventInput.start)||util_1.isTimeString(eventInput.end)||moment.isDuration(eventInput.end)?RecurringEventDef_1.default.parse(eventInput,source):SingleEventDef_1.default.parse(eventInput,source)}}},
/* 37 */
/***/function(module,exports,__nested_webpack_require_117315__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_117315__(3),ParsableModelMixin_1=__nested_webpack_require_117315__(52),EventDef=/** @class */function(){function EventDef(source){this.source=source,this.className=[],this.miscProps={}}return EventDef.parse=function(rawInput,source){var def=new this(source);return!!def.applyProps(rawInput)&&def},EventDef.normalizeId=function(id){return String(id)},EventDef.generateId=function(){return"_fc"+EventDef.uuid++},EventDef.prototype.clone=function(){var copy=new this.constructor(this.source);return copy.id=this.id,copy.rawId=this.rawId,copy.uid=this.uid,// not really unique anymore :(
EventDef.copyVerbatimStandardProps(this,copy),copy.className=this.className.slice(),// copy
copy.miscProps=$.extend({},this.miscProps),copy},EventDef.prototype.hasInverseRendering=function(){return"inverse-background"===this.getRendering()},EventDef.prototype.hasBgRendering=function(){var rendering=this.getRendering();return"inverse-background"===rendering||"background"===rendering},EventDef.prototype.getRendering=function(){return null!=this.rendering?this.rendering:this.source.rendering},EventDef.prototype.getConstraint=function(){return null!=this.constraint?this.constraint:null!=this.source.constraint?this.source.constraint:this.source.calendar.opt("eventConstraint")},EventDef.prototype.getOverlap=function(){return null!=this.overlap?this.overlap:null!=this.source.overlap?this.source.overlap:this.source.calendar.opt("eventOverlap")},EventDef.prototype.isStartExplicitlyEditable=function(){return null!=this.startEditable?this.startEditable:this.source.startEditable},EventDef.prototype.isDurationExplicitlyEditable=function(){return null!=this.durationEditable?this.durationEditable:this.source.durationEditable},EventDef.prototype.isExplicitlyEditable=function(){return null!=this.editable?this.editable:this.source.editable},EventDef.prototype.toLegacy=function(){var obj=$.extend({},this.miscProps);return obj._id=this.uid,obj.source=this.source,obj.className=this.className.slice(),// copy
obj.allDay=this.isAllDay(),null!=this.rawId&&(obj.id=this.rawId),EventDef.copyVerbatimStandardProps(this,obj),obj},EventDef.prototype.applyManualStandardProps=function(rawProps){return null!=rawProps.id?this.id=EventDef.normalizeId(this.rawId=rawProps.id):this.id=EventDef.generateId(),null!=rawProps._id?// accept this prop, even tho somewhat internal
this.uid=String(rawProps._id):this.uid=EventDef.generateId(),
// TODO: converge with EventSource
$.isArray(rawProps.className)&&(this.className=rawProps.className),"string"===typeof rawProps.className&&(this.className=rawProps.className.split(/\s+/)),!0},EventDef.prototype.applyMiscProps=function(rawProps){$.extend(this.miscProps,rawProps)},EventDef.uuid=0,EventDef.defineStandardProps=ParsableModelMixin_1.default.defineStandardProps,EventDef.copyVerbatimStandardProps=ParsableModelMixin_1.default.copyVerbatimStandardProps,EventDef}();exports.default=EventDef,ParsableModelMixin_1.default.mixInto(EventDef),EventDef.defineStandardProps({
// not automatically assigned (`false`)
_id:!1,id:!1,className:!1,source:!1,
// automatically assigned (`true`)
title:!0,url:!0,rendering:!0,constraint:!0,overlap:!0,editable:!0,startEditable:!0,durationEditable:!0,color:!0,backgroundColor:!0,borderColor:!0,textColor:!0})},
/* 38 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0}),exports.default={sourceClasses:[],registerClass:function(EventSourceClass){this.sourceClasses.unshift(EventSourceClass);// give highest priority
},parse:function(rawInput,calendar){var i,eventSource,sourceClasses=this.sourceClasses;for(i=0;i<sourceClasses.length;i++)if(eventSource=sourceClasses[i].parse(rawInput,calendar),eventSource)return eventSource}}},
/* 39 */
/***/function(module,exports,__nested_webpack_require_123088__){Object.defineProperty(exports,"__esModule",{value:!0});var util_1=__nested_webpack_require_123088__(4),EventDateProfile_1=__nested_webpack_require_123088__(16),EventDef_1=__nested_webpack_require_123088__(37),EventDefDateMutation_1=__nested_webpack_require_123088__(40),SingleEventDef_1=__nested_webpack_require_123088__(9),EventDefMutation=/** @class */function(){function EventDefMutation(){}return EventDefMutation.createFromRawProps=function(eventInstance,rawProps,largeUnit){var propName,dateProfile,dateMutation,defMutation,eventDef=eventInstance.def,dateProps={},standardProps={},miscProps={},verbatimStandardProps={},eventDefId=null,className=null;for(propName in rawProps)EventDateProfile_1.default.isStandardProp(propName)?dateProps[propName]=rawProps[propName]:eventDef.isStandardProp(propName)?standardProps[propName]=rawProps[propName]:eventDef.miscProps[propName]!==rawProps[propName]&&(// only if changed
miscProps[propName]=rawProps[propName]);return dateProfile=EventDateProfile_1.default.parse(dateProps,eventDef.source),dateProfile&&(// no failure?
dateMutation=EventDefDateMutation_1.default.createFromDiff(eventInstance.dateProfile,dateProfile,largeUnit)),standardProps.id!==eventDef.id&&(eventDefId=standardProps.id),util_1.isArraysEqual(standardProps.className,eventDef.className)||(className=standardProps.className),EventDef_1.default.copyVerbatimStandardProps(standardProps,// src
verbatimStandardProps),defMutation=new EventDefMutation,defMutation.eventDefId=eventDefId,defMutation.className=className,defMutation.verbatimStandardProps=verbatimStandardProps,defMutation.miscProps=miscProps,dateMutation&&(defMutation.dateMutation=dateMutation),defMutation},
/*
    eventDef assumed to be a SingleEventDef.
    returns an undo function.
    */
EventDefMutation.prototype.mutateSingle=function(eventDef){var origDateProfile;return this.dateMutation&&(origDateProfile=eventDef.dateProfile,eventDef.dateProfile=this.dateMutation.buildNewDateProfile(origDateProfile,eventDef.source.calendar)),
// can't undo
// TODO: more DRY with EventDef::applyManualStandardProps
null!=this.eventDefId&&(eventDef.id=EventDef_1.default.normalizeId(eventDef.rawId=this.eventDefId)),
// can't undo
// TODO: more DRY with EventDef::applyManualStandardProps
this.className&&(eventDef.className=this.className),
// can't undo
this.verbatimStandardProps&&SingleEventDef_1.default.copyVerbatimStandardProps(this.verbatimStandardProps,// src
eventDef),
// can't undo
this.miscProps&&eventDef.applyMiscProps(this.miscProps),origDateProfile?function(){eventDef.dateProfile=origDateProfile}:function(){}},EventDefMutation.prototype.setDateMutation=function(dateMutation){dateMutation&&!dateMutation.isEmpty()?this.dateMutation=dateMutation:this.dateMutation=null},EventDefMutation.prototype.isEmpty=function(){return!this.dateMutation},EventDefMutation}();exports.default=EventDefMutation},
/* 40 */
/***/function(module,exports,__nested_webpack_require_127408__){Object.defineProperty(exports,"__esModule",{value:!0});var util_1=__nested_webpack_require_127408__(4),EventDateProfile_1=__nested_webpack_require_127408__(16),EventDefDateMutation=/** @class */function(){function EventDefDateMutation(){this.clearEnd=!1,this.forceTimed=!1,this.forceAllDay=!1}return EventDefDateMutation.createFromDiff=function(dateProfile0,dateProfile1,largeUnit){var dateDelta,endDiff,endDelta,mutation,clearEnd=dateProfile0.end&&!dateProfile1.end,forceTimed=dateProfile0.isAllDay()&&!dateProfile1.isAllDay(),forceAllDay=!dateProfile0.isAllDay()&&dateProfile1.isAllDay();
// subtracts the dates in the appropriate way, returning a duration
function subtractDates(date1,date0){return largeUnit?util_1.diffByUnit(date1,date0,largeUnit):dateProfile1.isAllDay()?util_1.diffDay(date1,date0):util_1.diffDayTime(date1,date0)}return dateDelta=subtractDates(dateProfile1.start,dateProfile0.start),dateProfile1.end&&(
// use unzonedRanges because dateProfile0.end might be null
endDiff=subtractDates(dateProfile1.unzonedRange.getEnd(),dateProfile0.unzonedRange.getEnd()),endDelta=endDiff.subtract(dateDelta)),mutation=new EventDefDateMutation,mutation.clearEnd=clearEnd,mutation.forceTimed=forceTimed,mutation.forceAllDay=forceAllDay,mutation.setDateDelta(dateDelta),mutation.setEndDelta(endDelta),mutation},
/*
    returns an undo function.
    */
EventDefDateMutation.prototype.buildNewDateProfile=function(eventDateProfile,calendar){var start=eventDateProfile.start.clone(),end=null,shouldRezone=!1;return eventDateProfile.end&&!this.clearEnd?end=eventDateProfile.end.clone():this.endDelta&&!end&&(end=calendar.getDefaultEventEnd(eventDateProfile.isAllDay(),start)),this.forceTimed?(shouldRezone=!0,start.hasTime()||start.time(0),end&&!end.hasTime()&&end.time(0)):this.forceAllDay&&(start.hasTime()&&start.stripTime(),end&&end.hasTime()&&end.stripTime()),this.dateDelta&&(shouldRezone=!0,start.add(this.dateDelta),end&&end.add(this.dateDelta)),
// do this before adding startDelta to start, so we can work off of start
this.endDelta&&(shouldRezone=!0,end.add(this.endDelta)),this.startDelta&&(shouldRezone=!0,start.add(this.startDelta)),shouldRezone&&(start=calendar.applyTimezone(start),end&&(end=calendar.applyTimezone(end))),
// TODO: okay to access calendar option?
!end&&calendar.opt("forceEventDuration")&&(end=calendar.getDefaultEventEnd(eventDateProfile.isAllDay(),start)),new EventDateProfile_1.default(start,end,calendar)},EventDefDateMutation.prototype.setDateDelta=function(dateDelta){dateDelta&&dateDelta.valueOf()?this.dateDelta=dateDelta:this.dateDelta=null},EventDefDateMutation.prototype.setStartDelta=function(startDelta){startDelta&&startDelta.valueOf()?this.startDelta=startDelta:this.startDelta=null},EventDefDateMutation.prototype.setEndDelta=function(endDelta){endDelta&&endDelta.valueOf()?this.endDelta=endDelta:this.endDelta=null},EventDefDateMutation.prototype.isEmpty=function(){return!this.clearEnd&&!this.forceTimed&&!this.forceAllDay&&!this.dateDelta&&!this.startDelta&&!this.endDelta},EventDefDateMutation}();exports.default=EventDefDateMutation},
/* 41 */
/***/function(module,exports,__nested_webpack_require_132399__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_132399__(2),$=__nested_webpack_require_132399__(3),util_1=__nested_webpack_require_132399__(4),Class_1=__nested_webpack_require_132399__(35),Scroller=/** @class */function(_super){function Scroller(options){var _this=_super.call(this)||this;return options=options||{},_this.overflowX=options.overflowX||options.overflow||"auto",_this.overflowY=options.overflowY||options.overflow||"auto",_this}return tslib_1.__extends(Scroller,_super),Scroller.prototype.render=function(){this.el=this.renderEl(),this.applyOverflow()},Scroller.prototype.renderEl=function(){return this.scrollEl=$('<div class="fc-scroller"></div>')},
// sets to natural height, unlocks overflow
Scroller.prototype.clear=function(){this.setHeight("auto"),this.applyOverflow()},Scroller.prototype.destroy=function(){this.el.remove()},
// Overflow
// -----------------------------------------------------------------------------------------------------------------
Scroller.prototype.applyOverflow=function(){this.scrollEl.css({"overflow-x":this.overflowX,"overflow-y":this.overflowY})},
// Causes any 'auto' overflow values to resolves to 'scroll' or 'hidden'.
// Useful for preserving scrollbar widths regardless of future resizes.
// Can pass in scrollbarWidths for optimization.
Scroller.prototype.lockOverflow=function(scrollbarWidths){var overflowX=this.overflowX,overflowY=this.overflowY;scrollbarWidths=scrollbarWidths||this.getScrollbarWidths(),"auto"===overflowX&&(overflowX=scrollbarWidths.top||scrollbarWidths.bottom||// horizontal scrollbars?
// OR scrolling pane with massless scrollbars?
this.scrollEl[0].scrollWidth-1>this.scrollEl[0].clientWidth?"scroll":"hidden"),"auto"===overflowY&&(overflowY=scrollbarWidths.left||scrollbarWidths.right||// vertical scrollbars?
// OR scrolling pane with massless scrollbars?
this.scrollEl[0].scrollHeight-1>this.scrollEl[0].clientHeight?"scroll":"hidden"),this.scrollEl.css({"overflow-x":overflowX,"overflow-y":overflowY})},
// Getters / Setters
// -----------------------------------------------------------------------------------------------------------------
Scroller.prototype.setHeight=function(height){this.scrollEl.height(height)},Scroller.prototype.getScrollTop=function(){return this.scrollEl.scrollTop()},Scroller.prototype.setScrollTop=function(top){this.scrollEl.scrollTop(top)},Scroller.prototype.getClientWidth=function(){return this.scrollEl[0].clientWidth},Scroller.prototype.getClientHeight=function(){return this.scrollEl[0].clientHeight},Scroller.prototype.getScrollbarWidths=function(){return util_1.getScrollbarWidths(this.scrollEl)},Scroller}(Class_1.default);exports.default=Scroller},
/* 42 */
/***/function(module,exports,__nested_webpack_require_136144__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_136144__(2),$=__nested_webpack_require_136144__(3),util_1=__nested_webpack_require_136144__(4),DateComponent_1=__nested_webpack_require_136144__(231),GlobalEmitter_1=__nested_webpack_require_136144__(23),InteractiveDateComponent=/** @class */function(_super){function InteractiveDateComponent(_view,_options){var _this=_super.call(this,_view,_options)||this;
// self-config, overridable by subclasses
return _this.segSelector=".fc-event-container > *",// what constitutes an event element?
_this.dateSelectingClass&&(_this.dateClicking=new _this.dateClickingClass(_this)),_this.dateSelectingClass&&(_this.dateSelecting=new _this.dateSelectingClass(_this)),_this.eventPointingClass&&(_this.eventPointing=new _this.eventPointingClass(_this)),_this.eventDraggingClass&&_this.eventPointing&&(_this.eventDragging=new _this.eventDraggingClass(_this,_this.eventPointing)),_this.eventResizingClass&&_this.eventPointing&&(_this.eventResizing=new _this.eventResizingClass(_this,_this.eventPointing)),_this.externalDroppingClass&&(_this.externalDropping=new _this.externalDroppingClass(_this)),_this}
// Sets the container element that the view should render inside of, does global DOM-related initializations,
// and renders all the non-date-related content inside.
return tslib_1.__extends(InteractiveDateComponent,_super),InteractiveDateComponent.prototype.setElement=function(el){_super.prototype.setElement.call(this,el),this.dateClicking&&this.dateClicking.bindToEl(el),this.dateSelecting&&this.dateSelecting.bindToEl(el),this.bindAllSegHandlersToEl(el)},InteractiveDateComponent.prototype.removeElement=function(){this.endInteractions(),_super.prototype.removeElement.call(this)},InteractiveDateComponent.prototype.executeEventUnrender=function(){this.endInteractions(),_super.prototype.executeEventUnrender.call(this)},InteractiveDateComponent.prototype.bindGlobalHandlers=function(){_super.prototype.bindGlobalHandlers.call(this),this.externalDropping&&this.externalDropping.bindToDocument()},InteractiveDateComponent.prototype.unbindGlobalHandlers=function(){_super.prototype.unbindGlobalHandlers.call(this),this.externalDropping&&this.externalDropping.unbindFromDocument()},InteractiveDateComponent.prototype.bindDateHandlerToEl=function(el,name,handler){var _this=this;
// attach a handler to the grid's root element.
// jQuery will take care of unregistering them when removeElement gets called.
this.el.on(name,(function(ev){if(!$(ev.target).is(_this.segSelector+":not(.fc-helper),"+// directly on an event element
_this.segSelector+":not(.fc-helper) *,.fc-more,a[data-goto]"))return handler.call(_this,ev)}))},InteractiveDateComponent.prototype.bindAllSegHandlersToEl=function(el){[this.eventPointing,this.eventDragging,this.eventResizing].forEach((function(eventInteraction){eventInteraction&&eventInteraction.bindToEl(el)}))},InteractiveDateComponent.prototype.bindSegHandlerToEl=function(el,name,handler){var _this=this;el.on(name,this.segSelector,(function(ev){var segEl=$(ev.currentTarget);if(!segEl.is(".fc-helper")){var seg=segEl.data("fc-seg");// grab segment data. put there by View::renderEventsPayload
if(seg&&!_this.shouldIgnoreEventPointing())return handler.call(_this,seg,ev);// context will be the Grid
}}))},InteractiveDateComponent.prototype.shouldIgnoreMouse=function(){
// HACK
// This will still work even though bindDateHandlerToEl doesn't use GlobalEmitter.
return GlobalEmitter_1.default.get().shouldIgnoreMouse()},InteractiveDateComponent.prototype.shouldIgnoreTouch=function(){var view=this._getView();
// On iOS (and Android?) when a new selection is initiated overtop another selection,
// the touchend never fires because the elements gets removed mid-touch-interaction (my theory).
// HACK: simply don't allow this to happen.
// ALSO: prevent selection when an *event* is already raised.
return view.isSelected||view.selectedEvent},InteractiveDateComponent.prototype.shouldIgnoreEventPointing=function(){
// only call the handlers if there is not a drag/resize in progress
return this.eventDragging&&this.eventDragging.isDragging||this.eventResizing&&this.eventResizing.isResizing},InteractiveDateComponent.prototype.canStartSelection=function(seg,ev){return util_1.getEvIsTouch(ev)&&!this.canStartResize(seg,ev)&&(this.isEventDefDraggable(seg.footprint.eventDef)||this.isEventDefResizable(seg.footprint.eventDef))},InteractiveDateComponent.prototype.canStartDrag=function(seg,ev){return!this.canStartResize(seg,ev)&&this.isEventDefDraggable(seg.footprint.eventDef)},InteractiveDateComponent.prototype.canStartResize=function(seg,ev){var view=this._getView(),eventDef=seg.footprint.eventDef;return(!util_1.getEvIsTouch(ev)||view.isEventDefSelected(eventDef))&&this.isEventDefResizable(eventDef)&&$(ev.target).is(".fc-resizer")},
// Kills all in-progress dragging.
// Useful for when public API methods that result in re-rendering are invoked during a drag.
// Also useful for when touch devices misbehave and don't fire their touchend.
InteractiveDateComponent.prototype.endInteractions=function(){[this.dateClicking,this.dateSelecting,this.eventPointing,this.eventDragging,this.eventResizing].forEach((function(interaction){interaction&&interaction.end()}))},
// Event Drag-n-Drop
// ---------------------------------------------------------------------------------------------------------------
// Computes if the given event is allowed to be dragged by the user
InteractiveDateComponent.prototype.isEventDefDraggable=function(eventDef){return this.isEventDefStartEditable(eventDef)},InteractiveDateComponent.prototype.isEventDefStartEditable=function(eventDef){var isEditable=eventDef.isStartExplicitlyEditable();return null==isEditable&&(isEditable=this.opt("eventStartEditable"),null==isEditable&&(isEditable=this.isEventDefGenerallyEditable(eventDef))),isEditable},InteractiveDateComponent.prototype.isEventDefGenerallyEditable=function(eventDef){var isEditable=eventDef.isExplicitlyEditable();return null==isEditable&&(isEditable=this.opt("editable")),isEditable},
// Event Resizing
// ---------------------------------------------------------------------------------------------------------------
// Computes if the given event is allowed to be resized from its starting edge
InteractiveDateComponent.prototype.isEventDefResizableFromStart=function(eventDef){return this.opt("eventResizableFromStart")&&this.isEventDefResizable(eventDef)},
// Computes if the given event is allowed to be resized from its ending edge
InteractiveDateComponent.prototype.isEventDefResizableFromEnd=function(eventDef){return this.isEventDefResizable(eventDef)},
// Computes if the given event is allowed to be resized by the user at all
InteractiveDateComponent.prototype.isEventDefResizable=function(eventDef){var isResizable=eventDef.isDurationExplicitlyEditable();return null==isResizable&&(isResizable=this.opt("eventDurationEditable"),null==isResizable&&(isResizable=this.isEventDefGenerallyEditable(eventDef))),isResizable},
// Event Mutation / Constraints
// ---------------------------------------------------------------------------------------------------------------
// Diffs the two dates, returning a duration, based on granularity of the grid
// TODO: port isTimeScale into this system?
InteractiveDateComponent.prototype.diffDates=function(a,b){return this.largeUnit?util_1.diffByUnit(a,b,this.largeUnit):util_1.diffDayTime(a,b)},
// is it allowed, in relation to the view's validRange?
// NOTE: very similar to isExternalInstanceGroupAllowed
InteractiveDateComponent.prototype.isEventInstanceGroupAllowed=function(eventInstanceGroup){var i,view=this._getView(),dateProfile=this.dateProfile,eventFootprints=this.eventRangesToEventFootprints(eventInstanceGroup.getAllEventRanges());for(i=0;i<eventFootprints.length;i++)
// TODO: just use getAllEventRanges directly
if(!dateProfile.validUnzonedRange.containsRange(eventFootprints[i].componentFootprint.unzonedRange))return!1;return view.calendar.constraints.isEventInstanceGroupAllowed(eventInstanceGroup)},
// NOTE: very similar to isEventInstanceGroupAllowed
// when it's a completely anonymous external drag, no event.
InteractiveDateComponent.prototype.isExternalInstanceGroupAllowed=function(eventInstanceGroup){var i,view=this._getView(),dateProfile=this.dateProfile,eventFootprints=this.eventRangesToEventFootprints(eventInstanceGroup.getAllEventRanges());for(i=0;i<eventFootprints.length;i++)if(!dateProfile.validUnzonedRange.containsRange(eventFootprints[i].componentFootprint.unzonedRange))return!1;for(i=0;i<eventFootprints.length;i++)
// treat it as a selection
// TODO: pass in eventInstanceGroup instead
//  because we don't want calendar's constraint system to depend on a component's
//  determination of footprints.
if(!view.calendar.constraints.isSelectionFootprintAllowed(eventFootprints[i].componentFootprint))return!1;return!0},InteractiveDateComponent}(DateComponent_1.default);exports.default=InteractiveDateComponent},
/* 43 */
/***/function(module,exports,__nested_webpack_require_148167__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_148167__(2),$=__nested_webpack_require_148167__(3),moment=__nested_webpack_require_148167__(0),util_1=__nested_webpack_require_148167__(4),RenderQueue_1=__nested_webpack_require_148167__(229),DateProfileGenerator_1=__nested_webpack_require_148167__(55),InteractiveDateComponent_1=__nested_webpack_require_148167__(42),GlobalEmitter_1=__nested_webpack_require_148167__(23),UnzonedRange_1=__nested_webpack_require_148167__(5),View=/** @class */function(_super){function View(calendar,viewSpec){var _this=_super.call(this,null,viewSpec.options)||this;return _this.batchRenderDepth=0,_this.isSelected=!1,// boolean whether a range of time is user-selected or not
_this.calendar=calendar,_this.viewSpec=viewSpec,
// shortcuts
_this.type=viewSpec.type,
// .name is deprecated
_this.name=_this.type,_this.initRenderQueue(),_this.initHiddenDays(),_this.dateProfileGenerator=new _this.dateProfileGeneratorClass(_this),_this.bindBaseRenderHandlers(),_this.eventOrderSpecs=util_1.parseFieldSpecs(_this.opt("eventOrder")),
// legacy
_this["initialize"]&&_this["initialize"](),_this}return tslib_1.__extends(View,_super),View.prototype._getView=function(){return this},
// Retrieves an option with the given name
View.prototype.opt=function(name){return this.options[name]},
/* Render Queue
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.initRenderQueue=function(){this.renderQueue=new RenderQueue_1.default({event:this.opt("eventRenderWait")}),this.renderQueue.on("start",this.onRenderQueueStart.bind(this)),this.renderQueue.on("stop",this.onRenderQueueStop.bind(this)),this.on("before:change",this.startBatchRender),this.on("change",this.stopBatchRender)},View.prototype.onRenderQueueStart=function(){this.calendar.freezeContentHeight(),this.addScroll(this.queryScroll())},View.prototype.onRenderQueueStop=function(){this.calendar.updateViewSize()&&// success?
this.popScroll(),this.calendar.thawContentHeight()},View.prototype.startBatchRender=function(){this.batchRenderDepth++||this.renderQueue.pause()},View.prototype.stopBatchRender=function(){--this.batchRenderDepth||this.renderQueue.resume()},View.prototype.requestRender=function(func,namespace,actionType){this.renderQueue.queue(func,namespace,actionType)},
// given func will auto-bind to `this`
View.prototype.whenSizeUpdated=function(func){this.renderQueue.isRunning?this.renderQueue.one("stop",func.bind(this)):func.call(this)},
/* Title and Date Formatting
    ------------------------------------------------------------------------------------------------------------------*/
// Computes what the title at the top of the calendar should be for this view
View.prototype.computeTitle=function(dateProfile){var unzonedRange;
// for views that span a large unit of time, show the proper interval, ignoring stray days before and after
return unzonedRange=/^(year|month)$/.test(dateProfile.currentRangeUnit)?dateProfile.currentUnzonedRange:dateProfile.activeUnzonedRange,this.formatRange({start:this.calendar.msToMoment(unzonedRange.startMs,dateProfile.isRangeAllDay),end:this.calendar.msToMoment(unzonedRange.endMs,dateProfile.isRangeAllDay)},dateProfile.isRangeAllDay,this.opt("titleFormat")||this.computeTitleFormat(dateProfile),this.opt("titleRangeSeparator"))},
// Generates the format string that should be used to generate the title for the current date range.
// Attempts to compute the most appropriate format if not explicitly specified with `titleFormat`.
View.prototype.computeTitleFormat=function(dateProfile){var currentRangeUnit=dateProfile.currentRangeUnit;return"year"===currentRangeUnit?"YYYY":"month"===currentRangeUnit?this.opt("monthYearFormat"):dateProfile.currentUnzonedRange.as("days")>1?"ll":"LL"},
// Date Setting/Unsetting
// -----------------------------------------------------------------------------------------------------------------
View.prototype.setDate=function(date){var currentDateProfile=this.get("dateProfile"),newDateProfile=this.dateProfileGenerator.build(date,void 0,!0);// forceToValid=true
currentDateProfile&&currentDateProfile.activeUnzonedRange.equals(newDateProfile.activeUnzonedRange)||this.set("dateProfile",newDateProfile)},View.prototype.unsetDate=function(){this.unset("dateProfile")},
// Event Data
// -----------------------------------------------------------------------------------------------------------------
View.prototype.fetchInitialEvents=function(dateProfile){var calendar=this.calendar,forceAllDay=dateProfile.isRangeAllDay&&!this.usesMinMaxTime;return calendar.requestEvents(calendar.msToMoment(dateProfile.activeUnzonedRange.startMs,forceAllDay),calendar.msToMoment(dateProfile.activeUnzonedRange.endMs,forceAllDay))},View.prototype.bindEventChanges=function(){this.listenTo(this.calendar,"eventsReset",this.resetEvents);// TODO: make this a real event
},View.prototype.unbindEventChanges=function(){this.stopListeningTo(this.calendar,"eventsReset")},View.prototype.setEvents=function(eventsPayload){this.set("currentEvents",eventsPayload),this.set("hasEvents",!0)},View.prototype.unsetEvents=function(){this.unset("currentEvents"),this.unset("hasEvents")},View.prototype.resetEvents=function(eventsPayload){this.startBatchRender(),this.unsetEvents(),this.setEvents(eventsPayload),this.stopBatchRender()},
// Date High-level Rendering
// -----------------------------------------------------------------------------------------------------------------
View.prototype.requestDateRender=function(dateProfile){var _this=this;this.requestRender((function(){_this.executeDateRender(dateProfile)}),"date","init")},View.prototype.requestDateUnrender=function(){var _this=this;this.requestRender((function(){_this.executeDateUnrender()}),"date","destroy")},
// if dateProfile not specified, uses current
View.prototype.executeDateRender=function(dateProfile){_super.prototype.executeDateRender.call(this,dateProfile),this["render"]&&this["render"](),this.trigger("datesRendered"),this.addScroll({isDateInit:!0}),this.startNowIndicator()},View.prototype.executeDateUnrender=function(){this.unselect(),this.stopNowIndicator(),this.trigger("before:datesUnrendered"),this["destroy"]&&this["destroy"](),_super.prototype.executeDateUnrender.call(this)},
// "Base" rendering
// -----------------------------------------------------------------------------------------------------------------
View.prototype.bindBaseRenderHandlers=function(){var _this=this;this.on("datesRendered",(function(){_this.whenSizeUpdated(_this.triggerViewRender)})),this.on("before:datesUnrendered",(function(){_this.triggerViewDestroy()}))},View.prototype.triggerViewRender=function(){this.publiclyTrigger("viewRender",{context:this,args:[this,this.el]})},View.prototype.triggerViewDestroy=function(){this.publiclyTrigger("viewDestroy",{context:this,args:[this,this.el]})},
// Event High-level Rendering
// -----------------------------------------------------------------------------------------------------------------
View.prototype.requestEventsRender=function(eventsPayload){var _this=this;this.requestRender((function(){_this.executeEventRender(eventsPayload),_this.whenSizeUpdated(_this.triggerAfterEventsRendered)}),"event","init")},View.prototype.requestEventsUnrender=function(){var _this=this;this.requestRender((function(){_this.triggerBeforeEventsDestroyed(),_this.executeEventUnrender()}),"event","destroy")},
// Business Hour High-level Rendering
// -----------------------------------------------------------------------------------------------------------------
View.prototype.requestBusinessHoursRender=function(businessHourGenerator){var _this=this;this.requestRender((function(){_this.renderBusinessHours(businessHourGenerator)}),"businessHours","init")},View.prototype.requestBusinessHoursUnrender=function(){var _this=this;this.requestRender((function(){_this.unrenderBusinessHours()}),"businessHours","destroy")},
// Misc view rendering utils
// -----------------------------------------------------------------------------------------------------------------
// Binds DOM handlers to elements that reside outside the view container, such as the document
View.prototype.bindGlobalHandlers=function(){_super.prototype.bindGlobalHandlers.call(this),this.listenTo(GlobalEmitter_1.default.get(),{touchstart:this.processUnselect,mousedown:this.handleDocumentMousedown})},
// Unbinds DOM handlers from elements that reside outside the view container
View.prototype.unbindGlobalHandlers=function(){_super.prototype.unbindGlobalHandlers.call(this),this.stopListeningTo(GlobalEmitter_1.default.get())},
/* Now Indicator
    ------------------------------------------------------------------------------------------------------------------*/
// Immediately render the current time indicator and begins re-rendering it at an interval,
// which is defined by this.getNowIndicatorUnit().
// TODO: somehow do this for the current whole day's background too
View.prototype.startNowIndicator=function(){var unit,update,delay,_this=this;// ms wait value
this.opt("nowIndicator")&&(unit=this.getNowIndicatorUnit(),unit&&(update=util_1.proxy(this,"updateNowIndicator"),// bind to `this`
this.initialNowDate=this.calendar.getNow(),this.initialNowQueriedMs=(new Date).valueOf(),
// wait until the beginning of the next interval
delay=this.initialNowDate.clone().startOf(unit).add(1,unit).valueOf()-this.initialNowDate.valueOf(),this.nowIndicatorTimeoutID=setTimeout((function(){_this.nowIndicatorTimeoutID=null,update(),delay=+moment.duration(1,unit),delay=Math.max(100,delay),// prevent too frequent
_this.nowIndicatorIntervalID=setInterval(update,delay)}),delay)))},
// rerenders the now indicator, computing the new current time from the amount of time that has passed
// since the initial getNow call.
View.prototype.updateNowIndicator=function(){this.isDatesRendered&&this.initialNowDate&&(this.unrenderNowIndicator(),// won't unrender if unnecessary
this.renderNowIndicator(this.initialNowDate.clone().add((new Date).valueOf()-this.initialNowQueriedMs)),this.isNowIndicatorRendered=!0)},
// Immediately unrenders the view's current time indicator and stops any re-rendering timers.
// Won't cause side effects if indicator isn't rendered.
View.prototype.stopNowIndicator=function(){this.isNowIndicatorRendered&&(this.nowIndicatorTimeoutID&&(clearTimeout(this.nowIndicatorTimeoutID),this.nowIndicatorTimeoutID=null),this.nowIndicatorIntervalID&&(clearInterval(this.nowIndicatorIntervalID),this.nowIndicatorIntervalID=null),this.unrenderNowIndicator(),this.isNowIndicatorRendered=!1)},
/* Dimensions
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.updateSize=function(totalHeight,isAuto,isResize){this["setHeight"]?// for legacy API
this["setHeight"](totalHeight,isAuto):_super.prototype.updateSize.call(this,totalHeight,isAuto,isResize),this.updateNowIndicator()},
/* Scroller
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.addScroll=function(scroll){var queuedScroll=this.queuedScroll||(this.queuedScroll={});$.extend(queuedScroll,scroll)},View.prototype.popScroll=function(){this.applyQueuedScroll(),this.queuedScroll=null},View.prototype.applyQueuedScroll=function(){this.queuedScroll&&this.applyScroll(this.queuedScroll)},View.prototype.queryScroll=function(){var scroll={};return this.isDatesRendered&&$.extend(scroll,this.queryDateScroll()),scroll},View.prototype.applyScroll=function(scroll){scroll.isDateInit&&this.isDatesRendered&&$.extend(scroll,this.computeInitialDateScroll()),this.isDatesRendered&&this.applyDateScroll(scroll)},View.prototype.computeInitialDateScroll=function(){return{};// subclasses must implement
},View.prototype.queryDateScroll=function(){return{};// subclasses must implement
},View.prototype.applyDateScroll=function(scroll){
// subclasses must implement
},
/* Event Drag-n-Drop
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.reportEventDrop=function(eventInstance,eventMutation,el,ev){var eventManager=this.calendar.eventManager,undoFunc=eventManager.mutateEventsWithId(eventInstance.def.id,eventMutation),dateMutation=eventMutation.dateMutation;
// update the EventInstance, for handlers
dateMutation&&(eventInstance.dateProfile=dateMutation.buildNewDateProfile(eventInstance.dateProfile,this.calendar)),this.triggerEventDrop(eventInstance,
// a drop doesn't necessarily mean a date mutation (ex: resource change)
dateMutation&&dateMutation.dateDelta||moment.duration(),undoFunc,el,ev)},
// Triggers event-drop handlers that have subscribed via the API
View.prototype.triggerEventDrop=function(eventInstance,dateDelta,undoFunc,el,ev){this.publiclyTrigger("eventDrop",{context:el[0],args:[eventInstance.toLegacy(),dateDelta,undoFunc,ev,{},this]})},
/* External Element Drag-n-Drop
    ------------------------------------------------------------------------------------------------------------------*/
// Must be called when an external element, via jQuery UI, has been dropped onto the calendar.
// `meta` is the parsed data that has been embedded into the dragging event.
// `dropLocation` is an object that contains the new zoned start/end/allDay values for the event.
View.prototype.reportExternalDrop=function(singleEventDef,isEvent,isSticky,el,ev,ui){isEvent&&this.calendar.eventManager.addEventDef(singleEventDef,isSticky),this.triggerExternalDrop(singleEventDef,isEvent,el,ev,ui)},
// Triggers external-drop handlers that have subscribed via the API
View.prototype.triggerExternalDrop=function(singleEventDef,isEvent,el,ev,ui){
// trigger 'drop' regardless of whether element represents an event
this.publiclyTrigger("drop",{context:el[0],args:[singleEventDef.dateProfile.start.clone(),ev,ui,this]}),isEvent&&
// signal an external event landed
this.publiclyTrigger("eventReceive",{context:this,args:[singleEventDef.buildInstance().toLegacy(),this]})},
/* Event Resizing
    ------------------------------------------------------------------------------------------------------------------*/
// Must be called when an event in the view has been resized to a new length
View.prototype.reportEventResize=function(eventInstance,eventMutation,el,ev){var eventManager=this.calendar.eventManager,undoFunc=eventManager.mutateEventsWithId(eventInstance.def.id,eventMutation);
// update the EventInstance, for handlers
eventInstance.dateProfile=eventMutation.dateMutation.buildNewDateProfile(eventInstance.dateProfile,this.calendar);var resizeDelta=eventMutation.dateMutation.endDelta||eventMutation.dateMutation.startDelta;this.triggerEventResize(eventInstance,resizeDelta,undoFunc,el,ev)},
// Triggers event-resize handlers that have subscribed via the API
View.prototype.triggerEventResize=function(eventInstance,resizeDelta,undoFunc,el,ev){this.publiclyTrigger("eventResize",{context:el[0],args:[eventInstance.toLegacy(),resizeDelta,undoFunc,ev,{},this]})},
/* Selection (time range)
    ------------------------------------------------------------------------------------------------------------------*/
// Selects a date span on the view. `start` and `end` are both Moments.
// `ev` is the native mouse event that begin the interaction.
View.prototype.select=function(footprint,ev){this.unselect(ev),this.renderSelectionFootprint(footprint),this.reportSelection(footprint,ev)},View.prototype.renderSelectionFootprint=function(footprint){this["renderSelection"]?// legacy method in custom view classes
this["renderSelection"](footprint.toLegacy(this.calendar)):_super.prototype.renderSelectionFootprint.call(this,footprint)},
// Called when a new selection is made. Updates internal state and triggers handlers.
View.prototype.reportSelection=function(footprint,ev){this.isSelected=!0,this.triggerSelect(footprint,ev)},
// Triggers handlers to 'select'
View.prototype.triggerSelect=function(footprint,ev){var dateProfile=this.calendar.footprintToDateProfile(footprint);// abuse of "Event"DateProfile?
this.publiclyTrigger("select",{context:this,args:[dateProfile.start,dateProfile.end,ev,this]})},
// Undoes a selection. updates in the internal state and triggers handlers.
// `ev` is the native mouse event that began the interaction.
View.prototype.unselect=function(ev){this.isSelected&&(this.isSelected=!1,this["destroySelection"]&&this["destroySelection"](),this.unrenderSelection(),this.publiclyTrigger("unselect",{context:this,args:[ev,this]}))},
/* Event Selection
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.selectEventInstance=function(eventInstance){this.selectedEventInstance&&this.selectedEventInstance===eventInstance||(this.unselectEventInstance(),this.getEventSegs().forEach((function(seg){seg.footprint.eventInstance===eventInstance&&seg.el&&seg.el.addClass("fc-selected")})),this.selectedEventInstance=eventInstance)},View.prototype.unselectEventInstance=function(){this.selectedEventInstance&&(this.getEventSegs().forEach((function(seg){seg.el&&// necessary?
seg.el.removeClass("fc-selected")})),this.selectedEventInstance=null)},View.prototype.isEventDefSelected=function(eventDef){
// event references might change on refetchEvents(), while selectedEventInstance doesn't,
// so compare IDs
return this.selectedEventInstance&&this.selectedEventInstance.def.id===eventDef.id},
/* Mouse / Touch Unselecting (time range & event unselection)
    ------------------------------------------------------------------------------------------------------------------*/
// TODO: move consistently to down/start or up/end?
// TODO: don't kill previous selection if touch scrolling
View.prototype.handleDocumentMousedown=function(ev){util_1.isPrimaryMouseButton(ev)&&this.processUnselect(ev)},View.prototype.processUnselect=function(ev){this.processRangeUnselect(ev),this.processEventUnselect(ev)},View.prototype.processRangeUnselect=function(ev){var ignore;
// is there a time-range selection?
this.isSelected&&this.opt("unselectAuto")&&(
// only unselect if the clicked element is not identical to or inside of an 'unselectCancel' element
ignore=this.opt("unselectCancel"),ignore&&$(ev.target).closest(ignore).length||this.unselect(ev))},View.prototype.processEventUnselect=function(ev){this.selectedEventInstance&&($(ev.target).closest(".fc-selected").length||this.unselectEventInstance())},
/* Triggers
    ------------------------------------------------------------------------------------------------------------------*/
View.prototype.triggerBaseRendered=function(){this.publiclyTrigger("viewRender",{context:this,args:[this,this.el]})},View.prototype.triggerBaseUnrendered=function(){this.publiclyTrigger("viewDestroy",{context:this,args:[this,this.el]})},
// Triggers handlers to 'dayClick'
// Span has start/end of the clicked area. Only the start is useful.
View.prototype.triggerDayClick=function(footprint,dayEl,ev){var dateProfile=this.calendar.footprintToDateProfile(footprint);// abuse of "Event"DateProfile?
this.publiclyTrigger("dayClick",{context:dayEl,args:[dateProfile.start,ev,this]})},
/* Date Utils
    ------------------------------------------------------------------------------------------------------------------*/
// For DateComponent::getDayClasses
View.prototype.isDateInOtherMonth=function(date,dateProfile){return!1},
// Arguments after name will be forwarded to a hypothetical function value
// WARNING: passed-in arguments will be given to generator functions as-is and can cause side-effects.
// Always clone your objects if you fear mutation.
View.prototype.getUnzonedRangeOption=function(name){var val=this.opt(name);if("function"===typeof val&&(val=val.apply(null,Array.prototype.slice.call(arguments,1))),val)return this.calendar.parseUnzonedRange(val)},
/* Hidden Days
    ------------------------------------------------------------------------------------------------------------------*/
// Initializes internal variables related to calculating hidden days-of-week
View.prototype.initHiddenDays=function(){var i,hiddenDays=this.opt("hiddenDays")||[],isHiddenDayHash=[],dayCnt=0;// array of day-of-week indices that are hidden
for(!1===this.opt("weekends")&&hiddenDays.push(0,6),i=0;i<7;i++)(isHiddenDayHash[i]=-1!==$.inArray(i,hiddenDays))||dayCnt++;if(!dayCnt)throw new Error("invalid hiddenDays");// all days were hidden? bad.
this.isHiddenDayHash=isHiddenDayHash},
// Remove days from the beginning and end of the range that are computed as hidden.
// If the whole range is trimmed off, returns null
View.prototype.trimHiddenDays=function(inputUnzonedRange){var start=inputUnzonedRange.getStart(),end=inputUnzonedRange.getEnd();return start&&(start=this.skipHiddenDays(start)),end&&(end=this.skipHiddenDays(end,-1,!0)),null===start||null===end||start<end?new UnzonedRange_1.default(start,end):null},
// Is the current day hidden?
// `day` is a day-of-week index (0-6), or a Moment
View.prototype.isHiddenDay=function(day){return moment.isMoment(day)&&(day=day.day()),this.isHiddenDayHash[day]},
// Incrementing the current day until it is no longer a hidden day, returning a copy.
// DOES NOT CONSIDER validUnzonedRange!
// If the initial value of `date` is not a hidden day, don't do anything.
// Pass `isExclusive` as `true` if you are dealing with an end date.
// `inc` defaults to `1` (increment one day forward each time)
View.prototype.skipHiddenDays=function(date,inc,isExclusive){void 0===inc&&(inc=1),void 0===isExclusive&&(isExclusive=!1);var out=date.clone();while(this.isHiddenDayHash[(out.day()+(isExclusive?inc:0)+7)%7])out.add(inc,"days");return out},View}(InteractiveDateComponent_1.default);exports.default=View,View.prototype.usesMinMaxTime=!1,View.prototype.dateProfileGeneratorClass=DateProfileGenerator_1.default,View.watch("displayingDates",["isInDom","dateProfile"],(function(deps){this.requestDateRender(deps.dateProfile)}),(function(){this.requestDateUnrender()})),View.watch("displayingBusinessHours",["displayingDates","businessHourGenerator"],(function(deps){this.requestBusinessHoursRender(deps.businessHourGenerator)}),(function(){this.requestBusinessHoursUnrender()})),View.watch("initialEvents",["dateProfile"],(function(deps){return this.fetchInitialEvents(deps.dateProfile)})),View.watch("bindingEvents",["initialEvents"],(function(deps){this.setEvents(deps.initialEvents),this.bindEventChanges()}),(function(){this.unbindEventChanges(),this.unsetEvents()})),View.watch("displayingEvents",["displayingDates","hasEvents"],(function(){this.requestEventsRender(this.get("currentEvents"))}),(function(){this.requestEventsUnrender()})),View.watch("title",["dateProfile"],(function(deps){return this.title=this.computeTitle(deps.dateProfile);// assign to View for legacy reasons
})),View.watch("legacyDateProps",["dateProfile"],(function(deps){var calendar=this.calendar,dateProfile=deps.dateProfile;
// DEPRECATED, but we need to keep it updated...
this.start=calendar.msToMoment(dateProfile.activeUnzonedRange.startMs,dateProfile.isRangeAllDay),this.end=calendar.msToMoment(dateProfile.activeUnzonedRange.endMs,dateProfile.isRangeAllDay),this.intervalStart=calendar.msToMoment(dateProfile.currentUnzonedRange.startMs,dateProfile.isRangeAllDay),this.intervalEnd=calendar.msToMoment(dateProfile.currentUnzonedRange.endMs,dateProfile.isRangeAllDay)}))},
/* 44 */
/***/function(module,exports,__nested_webpack_require_180157__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_180157__(3),util_1=__nested_webpack_require_180157__(4),EventRenderer=/** @class */function(){function EventRenderer(component,fillRenderer){this.view=component._getView(),this.component=component,this.fillRenderer=fillRenderer}return EventRenderer.prototype.opt=function(name){return this.view.opt(name)},
// Updates values that rely on options and also relate to range
EventRenderer.prototype.rangeUpdated=function(){var displayEventTime,displayEventEnd;this.eventTimeFormat=this.opt("eventTimeFormat")||this.opt("timeFormat")||// deprecated
this.computeEventTimeFormat(),displayEventTime=this.opt("displayEventTime"),null==displayEventTime&&(displayEventTime=this.computeDisplayEventTime()),displayEventEnd=this.opt("displayEventEnd"),null==displayEventEnd&&(displayEventEnd=this.computeDisplayEventEnd()),this.displayEventTime=displayEventTime,this.displayEventEnd=displayEventEnd},EventRenderer.prototype.render=function(eventsPayload){var eventDefId,instanceGroup,eventRanges,dateProfile=this.component._getDateProfile(),bgRanges=[],fgRanges=[];for(eventDefId in eventsPayload)instanceGroup=eventsPayload[eventDefId],eventRanges=instanceGroup.sliceRenderRanges(dateProfile.activeUnzonedRange),instanceGroup.getEventDef().hasBgRendering()?bgRanges.push.apply(bgRanges,eventRanges):fgRanges.push.apply(fgRanges,eventRanges);this.renderBgRanges(bgRanges),this.renderFgRanges(fgRanges)},EventRenderer.prototype.unrender=function(){this.unrenderBgRanges(),this.unrenderFgRanges()},EventRenderer.prototype.renderFgRanges=function(eventRanges){var eventFootprints=this.component.eventRangesToEventFootprints(eventRanges),segs=this.component.eventFootprintsToSegs(eventFootprints);
// render an `.el` on each seg
// returns a subset of the segs. segs that were actually rendered
segs=this.renderFgSegEls(segs),!1!==this.renderFgSegs(segs)&&(// no failure?
this.fgSegs=segs)},EventRenderer.prototype.unrenderFgRanges=function(){this.unrenderFgSegs(this.fgSegs||[]),this.fgSegs=null},EventRenderer.prototype.renderBgRanges=function(eventRanges){var eventFootprints=this.component.eventRangesToEventFootprints(eventRanges),segs=this.component.eventFootprintsToSegs(eventFootprints);!1!==this.renderBgSegs(segs)&&(// no failure?
this.bgSegs=segs)},EventRenderer.prototype.unrenderBgRanges=function(){this.unrenderBgSegs(),this.bgSegs=null},EventRenderer.prototype.getSegs=function(){return(this.bgSegs||[]).concat(this.fgSegs||[])},
// Renders foreground event segments onto the grid
EventRenderer.prototype.renderFgSegs=function(segs){
// subclasses must implement
// segs already has rendered els, and has been filtered.
return!1;// signal failure if not implemented
},
// Unrenders all currently rendered foreground segments
EventRenderer.prototype.unrenderFgSegs=function(segs){
// subclasses must implement
},EventRenderer.prototype.renderBgSegs=function(segs){var _this=this;if(!this.fillRenderer)return!1;// signal failure if no fillRenderer
this.fillRenderer.renderSegs("bgEvent",segs,{getClasses:function(seg){return _this.getBgClasses(seg.footprint.eventDef)},getCss:function(seg){return{"background-color":_this.getBgColor(seg.footprint.eventDef)}},filterEl:function(seg,el){return _this.filterEventRenderEl(seg.footprint,el)}})},EventRenderer.prototype.unrenderBgSegs=function(){this.fillRenderer&&this.fillRenderer.unrender("bgEvent")},
// Renders and assigns an `el` property for each foreground event segment.
// Only returns segments that successfully rendered.
EventRenderer.prototype.renderFgSegEls=function(segs,disableResizing){var _this=this;void 0===disableResizing&&(disableResizing=!1);var i,hasEventRenderHandlers=this.view.hasPublicHandlers("eventRender"),html="",renderedSegs=[];if(segs.length){// don't build an empty html string
// build a large concatenation of event segment HTML
for(i=0;i<segs.length;i++)this.beforeFgSegHtml(segs[i]),html+=this.fgSegHtml(segs[i],disableResizing);
// Grab individual elements from the combined HTML string. Use each as the default rendering.
// Then, compute the 'el' for each segment. An el might be null if the eventRender callback returned false.
$(html).each((function(i,node){var seg=segs[i],el=$(node);hasEventRenderHandlers&&(// optimization
el=_this.filterEventRenderEl(seg.footprint,el)),el&&(el.data("fc-seg",seg),// used by handlers
seg.el=el,renderedSegs.push(seg))}))}return renderedSegs},EventRenderer.prototype.beforeFgSegHtml=function(seg){},
// Generates the HTML for the default rendering of a foreground event segment. Used by renderFgSegEls()
EventRenderer.prototype.fgSegHtml=function(seg,disableResizing){
// subclasses should implement
},
// Generic utility for generating the HTML classNames for an event segment's element
EventRenderer.prototype.getSegClasses=function(seg,isDraggable,isResizable){var classes=["fc-event",seg.isStart?"fc-start":"fc-not-start",seg.isEnd?"fc-end":"fc-not-end"].concat(this.getClasses(seg.footprint.eventDef));return isDraggable&&classes.push("fc-draggable"),isResizable&&classes.push("fc-resizable"),
// event is currently selected? attach a className.
this.view.isEventDefSelected(seg.footprint.eventDef)&&classes.push("fc-selected"),classes},
// Given an event and the default element used for rendering, returns the element that should actually be used.
// Basically runs events and elements through the eventRender hook.
EventRenderer.prototype.filterEventRenderEl=function(eventFootprint,el){var legacy=eventFootprint.getEventLegacy(),custom=this.view.publiclyTrigger("eventRender",{context:legacy,args:[legacy,el,this.view]});return!1===custom?// means don't render at all
el=null:custom&&!0!==custom&&(el=$(custom)),el},
// Compute the text that should be displayed on an event's element.
// `range` can be the Event object itself, or something range-like, with at least a `start`.
// If event times are disabled, or the event has no time, will return a blank string.
// If not specified, formatStr will default to the eventTimeFormat setting,
// and displayEnd will default to the displayEventEnd setting.
EventRenderer.prototype.getTimeText=function(eventFootprint,formatStr,displayEnd){return this._getTimeText(eventFootprint.eventInstance.dateProfile.start,eventFootprint.eventInstance.dateProfile.end,eventFootprint.componentFootprint.isAllDay,formatStr,displayEnd)},EventRenderer.prototype._getTimeText=function(start,end,isAllDay,formatStr,displayEnd){return null==formatStr&&(formatStr=this.eventTimeFormat),null==displayEnd&&(displayEnd=this.displayEventEnd),this.displayEventTime&&!isAllDay?displayEnd&&end?this.view.formatRange({start:start,end:end},!1,// allDay
formatStr):start.format(formatStr):""},EventRenderer.prototype.computeEventTimeFormat=function(){return this.opt("smallTimeFormat")},EventRenderer.prototype.computeDisplayEventTime=function(){return!0},EventRenderer.prototype.computeDisplayEventEnd=function(){return!0},EventRenderer.prototype.getBgClasses=function(eventDef){var classNames=this.getClasses(eventDef);return classNames.push("fc-bgevent"),classNames},EventRenderer.prototype.getClasses=function(eventDef){var i,objs=this.getStylingObjs(eventDef),classNames=[];for(i=0;i<objs.length;i++)classNames.push.apply(// append
classNames,objs[i].eventClassName||objs[i].className||[]);return classNames},
// Utility for generating event skin-related CSS properties
EventRenderer.prototype.getSkinCss=function(eventDef){return{"background-color":this.getBgColor(eventDef),"border-color":this.getBorderColor(eventDef),color:this.getTextColor(eventDef)}},
// Queries for caller-specified color, then falls back to default
EventRenderer.prototype.getBgColor=function(eventDef){var i,val,objs=this.getStylingObjs(eventDef);for(i=0;i<objs.length&&!val;i++)val=objs[i].eventBackgroundColor||objs[i].eventColor||objs[i].backgroundColor||objs[i].color;return val||(val=this.opt("eventBackgroundColor")||this.opt("eventColor")),val},
// Queries for caller-specified color, then falls back to default
EventRenderer.prototype.getBorderColor=function(eventDef){var i,val,objs=this.getStylingObjs(eventDef);for(i=0;i<objs.length&&!val;i++)val=objs[i].eventBorderColor||objs[i].eventColor||objs[i].borderColor||objs[i].color;return val||(val=this.opt("eventBorderColor")||this.opt("eventColor")),val},
// Queries for caller-specified color, then falls back to default
EventRenderer.prototype.getTextColor=function(eventDef){var i,val,objs=this.getStylingObjs(eventDef);for(i=0;i<objs.length&&!val;i++)val=objs[i].eventTextColor||objs[i].textColor;return val||(val=this.opt("eventTextColor")),val},EventRenderer.prototype.getStylingObjs=function(eventDef){var objs=this.getFallbackStylingObjs(eventDef);return objs.unshift(eventDef),objs},EventRenderer.prototype.getFallbackStylingObjs=function(eventDef){return[eventDef.source]},EventRenderer.prototype.sortEventSegs=function(segs){segs.sort(util_1.proxy(this,"compareEventSegs"))},
// A cmp function for determining which segments should take visual priority
EventRenderer.prototype.compareEventSegs=function(seg1,seg2){var f1=seg1.footprint,f2=seg2.footprint,cf1=f1.componentFootprint,cf2=f2.componentFootprint,r1=cf1.unzonedRange,r2=cf2.unzonedRange;return r1.startMs-r2.startMs||// earlier events go first
r2.endMs-r2.startMs-(r1.endMs-r1.startMs)||// tie? longer events go first
cf2.isAllDay-cf1.isAllDay||// tie? put all-day events first (booleans cast to 0/1)
util_1.compareByFieldSpecs(f1.eventDef,f2.eventDef,this.view.eventOrderSpecs,f1.eventDef.miscProps,f2.eventDef.miscProps)},EventRenderer}();exports.default=EventRenderer},
/* 45 */
/* 46 */,
/* 47 */,
/* 48 */,
/* 49 */
/***/,function(module,exports,__nested_webpack_require_193977__){Object.defineProperty(exports,"__esModule",{value:!0});var moment_ext_1=__nested_webpack_require_193977__(11);
// Plugin
// -------------------------------------------------------------------------------------------------
function englishMoment(mom){return"en"!==mom.locale()?mom.clone().locale("en"):mom}
// Config
// ---------------------------------------------------------------------------------------------------------------------
/*
Inserted between chunks in the fake ("intermediate") formatting string.
Important that it passes as whitespace (\s) because moment often identifies non-standalone months
via a regexp with an \s.
*/moment_ext_1.newMomentProto.format=function(){return this._fullCalendar&&arguments[0]?formatDate(this,arguments[0]):this._ambigTime?moment_ext_1.oldMomentFormat(englishMoment(this),"YYYY-MM-DD"):this._ambigZone?moment_ext_1.oldMomentFormat(englishMoment(this),"YYYY-MM-DD[T]HH:mm:ss"):this._fullCalendar?moment_ext_1.oldMomentFormat(englishMoment(this)):moment_ext_1.oldMomentProto.format.apply(this,arguments)},moment_ext_1.newMomentProto.toISOString=function(){return this._ambigTime?moment_ext_1.oldMomentFormat(englishMoment(this),"YYYY-MM-DD"):this._ambigZone?moment_ext_1.oldMomentFormat(englishMoment(this),"YYYY-MM-DD[T]HH:mm:ss"):this._fullCalendar?moment_ext_1.oldMomentProto.toISOString.apply(englishMoment(this),arguments):moment_ext_1.oldMomentProto.toISOString.apply(this,arguments)};var PART_SEPARATOR="\v",SPECIAL_TOKEN_MARKER="",MAYBE_MARKER="",MAYBE_REGEXP=new RegExp(MAYBE_MARKER+"([^"+MAYBE_MARKER+"]*)"+MAYBE_MARKER,"g"),specialTokens={t:function(date){return moment_ext_1.oldMomentFormat(date,"a").charAt(0)},T:function(date){return moment_ext_1.oldMomentFormat(date,"A").charAt(0)}},largeTokenMap={Y:{value:1,unit:"year"},M:{value:2,unit:"month"},W:{value:3,unit:"week"},w:{value:3,unit:"week"},D:{value:4,unit:"day"},d:{value:4,unit:"day"}};// vertical tab
/*
Inserted as the first character of a literal-text chunk to indicate that the literal text is not actually literal text,
but rather, a "special" token that has custom rendering (see specialTokens map).
*/
// Single Date Formatting
// ---------------------------------------------------------------------------------------------------------------------
/*
Formats `date` with a Moment formatting string, but allow our non-zero areas and special token
*/
function formatDate(date,formatStr){return renderFakeFormatString(getParsedFormatString(formatStr).fakeFormatString,date)}
// Date Range Formatting
// -------------------------------------------------------------------------------------------------
// TODO: make it work with timezone offset
/*
Using a formatting string meant for a single date, generate a range string, like
"Sep 2 - 9 2013", that intelligently inserts a separator where the dates differ.
If the dates are the same as far as the format string is concerned, just return a single
rendering of one date, without any separator.
*/
function formatRange(date1,date2,formatStr,separator,isRTL){var localeData;return date1=moment_ext_1.default.parseZone(date1),date2=moment_ext_1.default.parseZone(date2),localeData=date1.localeData(),
// Expand localized format strings, like "LL" -> "MMMM D YYYY".
// BTW, this is not important for `formatDate` because it is impossible to put custom tokens
// or non-zero areas in Moment's localized format strings.
formatStr=localeData.longDateFormat(formatStr)||formatStr,renderParsedFormat(getParsedFormatString(formatStr),date1,date2,separator||" - ",isRTL)}
/*
Renders a range with an already-parsed format string.
*/
function renderParsedFormat(parsedFormat,date1,date2,separator,isRTL){var leftI,rightI,middleI,sameUnits=parsedFormat.sameUnits,unzonedDate1=date1.clone().stripZone(),unzonedDate2=date2.clone().stripZone(),renderedParts1=renderFakeFormatStringParts(parsedFormat.fakeFormatString,date1),renderedParts2=renderFakeFormatStringParts(parsedFormat.fakeFormatString,date2),leftStr="",rightStr="",middleStr1="",middleStr2="",middleStr="";
// Start at the leftmost side of the formatting string and continue until you hit a token
// that is not the same between dates.
for(leftI=0;leftI<sameUnits.length&&(!sameUnits[leftI]||unzonedDate1.isSame(unzonedDate2,sameUnits[leftI]));leftI++)leftStr+=renderedParts1[leftI];
// Similarly, start at the rightmost side of the formatting string and move left
for(rightI=sameUnits.length-1;rightI>leftI&&(!sameUnits[rightI]||unzonedDate1.isSame(unzonedDate2,sameUnits[rightI]));rightI--){
// If current chunk is on the boundary of unique date-content, and is a special-case
// date-formatting postfix character, then don't consume it. Consider it unique date-content.
// TODO: make configurable
if(rightI-1===leftI&&"."===renderedParts1[rightI])break;rightStr=renderedParts1[rightI]+rightStr}
// The area in the middle is different for both of the dates.
// Collect them distinctly so we can jam them together later.
for(middleI=leftI;middleI<=rightI;middleI++)middleStr1+=renderedParts1[middleI],middleStr2+=renderedParts2[middleI];return(middleStr1||middleStr2)&&(middleStr=isRTL?middleStr2+separator+middleStr1:middleStr1+separator+middleStr2),processMaybeMarkers(leftStr+middleStr+rightStr)}
// Format String Parsing
// ---------------------------------------------------------------------------------------------------------------------
exports.formatDate=formatDate,exports.formatRange=formatRange;var parsedFormatStrCache={};
/*
Returns a parsed format string, leveraging a cache.
*/function getParsedFormatString(formatStr){return parsedFormatStrCache[formatStr]||(parsedFormatStrCache[formatStr]=parseFormatString(formatStr))}
/*
Parses a format string into the following:
- fakeFormatString: a momentJS formatting string, littered with special control characters that get post-processed.
- sameUnits: for every part in fakeFormatString, if the part is a token, the value will be a unit string (like "day"),
  that indicates how similar a range's start & end must be in order to share the same formatted text.
  If not a token, then the value is null.
  Always a flat array (not nested liked "chunks").
*/function parseFormatString(formatStr){var chunks=chunkFormatString(formatStr);return{fakeFormatString:buildFakeFormatString(chunks),sameUnits:buildSameUnits(chunks)}}
/*
Break the formatting string into an array of chunks.
A 'maybe' chunk will have nested chunks.
*/function chunkFormatString(formatStr){var match,chunks=[],chunker=/\[([^\]]*)\]|\(([^\)]*)\)|(LTS|LT|(\w)\4*o?)|([^\w\[\(]+)/g;while(match=chunker.exec(formatStr))match[1]?// a literal string inside [ ... ]
chunks.push.apply(chunks,// append
splitStringLiteral(match[1])):match[2]?// non-zero formatting inside ( ... )
chunks.push({maybe:chunkFormatString(match[2])}):match[3]?// a formatting token
chunks.push({token:match[3]}):match[5]&&// an unenclosed literal string
chunks.push.apply(chunks,// append
splitStringLiteral(match[5]));return chunks}
/*
Potentially splits a literal-text string into multiple parts. For special cases.
*/function splitStringLiteral(s){return". "===s?["."," "]:[s]}
/*
Given chunks parsed from a real format string, generate a fake (aka "intermediate") format string with special control
characters that will eventually be given to moment for formatting, and then post-processed.
*/function buildFakeFormatString(chunks){var i,chunk,parts=[];for(i=0;i<chunks.length;i++)chunk=chunks[i],"string"===typeof chunk?parts.push("["+chunk+"]"):chunk.token?chunk.token in specialTokens?parts.push(SPECIAL_TOKEN_MARKER+// useful during post-processing
"["+chunk.token+"]"):parts.push(chunk.token):chunk.maybe&&parts.push(MAYBE_MARKER+// useful during post-processing
buildFakeFormatString(chunk.maybe)+MAYBE_MARKER);return parts.join(PART_SEPARATOR)}
/*
Given parsed chunks from a real formatting string, generates an array of unit strings (like "day") that indicate
in which regard two dates must be similar in order to share range formatting text.
The `chunks` can be nested (because of "maybe" chunks), however, the returned array will be flat.
*/function buildSameUnits(chunks){var i,chunk,tokenInfo,units=[];for(i=0;i<chunks.length;i++)chunk=chunks[i],chunk.token?(tokenInfo=largeTokenMap[chunk.token.charAt(0)],units.push(tokenInfo?tokenInfo.unit:"second")):chunk.maybe?units.push.apply(units,// append
buildSameUnits(chunk.maybe)):units.push(null);return units}
// Rendering to text
// ---------------------------------------------------------------------------------------------------------------------
/*
Formats a date with a fake format string, post-processes the control characters, then returns.
*/function renderFakeFormatString(fakeFormatString,date){return processMaybeMarkers(renderFakeFormatStringParts(fakeFormatString,date).join(""))}
/*
Formats a date into parts that will have been post-processed, EXCEPT for the "maybe" markers.
*/function renderFakeFormatStringParts(fakeFormatString,date){var i,fakePart,parts=[],fakeRender=moment_ext_1.oldMomentFormat(date,fakeFormatString),fakeParts=fakeRender.split(PART_SEPARATOR);for(i=0;i<fakeParts.length;i++)fakePart=fakeParts[i],fakePart.charAt(0)===SPECIAL_TOKEN_MARKER?parts.push(
// the literal string IS the token's name.
// call special token's registered function.
specialTokens[fakePart.substring(1)](date)):parts.push(fakePart);return parts}
/*
Accepts an almost-finally-formatted string and processes the "maybe" control characters, returning a new string.
*/function processMaybeMarkers(s){return s.replace(MAYBE_REGEXP,(function(m0,m1){return m1.match(/[1-9]/)?m1:""}))}
// Misc Utils
// -------------------------------------------------------------------------------------------------
/*
Returns a unit string, either 'year', 'month', 'day', or null for the most granular formatting token in the string.
*/function queryMostGranularFormatUnit(formatStr){var i,chunk,candidate,best,chunks=chunkFormatString(formatStr);for(i=0;i<chunks.length;i++)chunk=chunks[i],chunk.token&&(candidate=largeTokenMap[chunk.token.charAt(0)],candidate&&(!best||candidate.value>best.value)&&(best=candidate));return best?best.unit:null}exports.queryMostGranularFormatUnit=queryMostGranularFormatUnit},
/* 50 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var EventRange=/** @class */function(){function EventRange(unzonedRange,eventDef,eventInstance){this.unzonedRange=unzonedRange,this.eventDef=eventDef,eventInstance&&(this.eventInstance=eventInstance)}return EventRange}();exports.default=EventRange},
/* 51 */
/***/function(module,exports,__nested_webpack_require_208896__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_208896__(2),Class_1=__nested_webpack_require_208896__(35),EmitterMixin_1=__nested_webpack_require_208896__(13),ListenerMixin_1=__nested_webpack_require_208896__(7),Model=/** @class */function(_super){function Model(){var _this=_super.call(this)||this;return _this._watchers={},_this._props={},_this.applyGlobalWatchers(),_this.constructed(),_this}return tslib_1.__extends(Model,_super),Model.watch=function(name){for(var args=[],_i=1;_i<arguments.length;_i++)args[_i-1]=arguments[_i];
// subclasses should make a masked-copy of the superclass's map
// TODO: write test
this.prototype.hasOwnProperty("_globalWatchArgs")||(this.prototype._globalWatchArgs=Object.create(this.prototype._globalWatchArgs)),this.prototype._globalWatchArgs[name]=args},Model.prototype.constructed=function(){
// useful for monkeypatching. TODO: BaseClass?
},Model.prototype.applyGlobalWatchers=function(){var name,map=this._globalWatchArgs;for(name in map)this.watch.apply(this,[name].concat(map[name]))},Model.prototype.has=function(name){return name in this._props},Model.prototype.get=function(name){return void 0===name?this._props:this._props[name]},Model.prototype.set=function(name,val){var newProps;"string"===typeof name?(newProps={},newProps[name]=void 0===val?null:val):newProps=name,this.setProps(newProps)},Model.prototype.reset=function(newProps){var name,oldProps=this._props,changeset={};for(name in oldProps)changeset[name]=void 0;for(name in newProps)changeset[name]=newProps[name];this.setProps(changeset)},Model.prototype.unset=function(name){var names,i,newProps={};for(names="string"===typeof name?[name]:name,i=0;i<names.length;i++)newProps[names[i]]=void 0;this.setProps(newProps)},Model.prototype.setProps=function(newProps){var name,val,changedProps={},changedCnt=0;for(name in newProps)val=newProps[name],
// a change in value?
// if an object, don't check equality, because might have been mutated internally.
// TODO: eventually enforce immutability.
"object"!==typeof val&&val===this._props[name]||(changedProps[name]=val,changedCnt++);if(changedCnt){for(name in this.trigger("before:batchChange",changedProps),changedProps)val=changedProps[name],this.trigger("before:change",name,val),this.trigger("before:change:"+name,val);for(name in changedProps)val=changedProps[name],void 0===val?delete this._props[name]:this._props[name]=val,this.trigger("change:"+name,val),this.trigger("change",name,val);this.trigger("batchChange",changedProps)}},Model.prototype.watch=function(name,depList,startFunc,stopFunc){var _this=this;this.unwatch(name),this._watchers[name]=this._watchDeps(depList,(function(deps){var res=startFunc.call(_this,deps);res&&res.then?(_this.unset(name),// put in an unset state while resolving
res.then((function(val){_this.set(name,val)}))):_this.set(name,res)}),(function(deps){_this.unset(name),stopFunc&&stopFunc.call(_this,deps)}))},Model.prototype.unwatch=function(name){var watcher=this._watchers[name];watcher&&(delete this._watchers[name],watcher.teardown())},Model.prototype._watchDeps=function(depList,startFunc,stopFunc){var _this=this,queuedChangeCnt=0,depCnt=depList.length,satisfyCnt=0,values={},bindTuples=[],isCallingStop=!1,onBeforeDepChange=function(depName,val,isOptional){queuedChangeCnt++,1===queuedChangeCnt&&satisfyCnt===depCnt&&(// all deps previously satisfied?
isCallingStop=!0,stopFunc(values),isCallingStop=!1)},onDepChange=function(depName,val,isOptional){void 0===val?(// unsetting a value?
// required dependency that was previously set?
isOptional||void 0===values[depName]||satisfyCnt--,delete values[depName]):(// setting a value?
// required dependency that was previously unset?
isOptional||void 0!==values[depName]||satisfyCnt++,values[depName]=val),queuedChangeCnt--,queuedChangeCnt||// last change to cause a "start"?
// now finally satisfied or satisfied all along?
satisfyCnt===depCnt&&(
// if the stopFunc initiated another value change, ignore it.
// it will be processed by another change event anyway.
isCallingStop||startFunc(values))},bind=function(eventName,handler){_this.on(eventName,handler),bindTuples.push([eventName,handler])};
// listen to dependency changes
return depList.forEach((function(depName){var isOptional=!1;"?"===depName.charAt(0)&&(// TODO: more DRY
depName=depName.substring(1),isOptional=!0),bind("before:change:"+depName,(function(val){onBeforeDepChange(depName,val,isOptional)})),bind("change:"+depName,(function(val){onDepChange(depName,val,isOptional)}))})),
// process current dependency values
depList.forEach((function(depName){var isOptional=!1;"?"===depName.charAt(0)&&(// TODO: more DRY
depName=depName.substring(1),isOptional=!0),_this.has(depName)?(values[depName]=_this.get(depName),satisfyCnt++):isOptional&&satisfyCnt++})),
// initially satisfied
satisfyCnt===depCnt&&startFunc(values),{teardown:function(){
// remove all handlers
for(var i=0;i<bindTuples.length;i++)_this.off(bindTuples[i][0],bindTuples[i][1]);bindTuples=null,
// was satisfied, so call stopFunc
satisfyCnt===depCnt&&stopFunc()},flash:function(){satisfyCnt===depCnt&&(stopFunc(),startFunc(values))}}},Model.prototype.flash=function(name){var watcher=this._watchers[name];watcher&&watcher.flash()},Model}(Class_1.default);exports.default=Model,Model.prototype._globalWatchArgs={},// mutation protection in Model.watch
EmitterMixin_1.default.mixInto(Model),ListenerMixin_1.default.mixInto(Model)},
/* 52 */
/***/function(module,exports,__nested_webpack_require_218553__){
/*
USAGE:
  import { default as ParsableModelMixin, ParsableModelInterface } from './ParsableModelMixin'
in class:
  applyProps: ParsableModelInterface['applyProps']
  applyManualStandardProps: ParsableModelInterface['applyManualStandardProps']
  applyMiscProps: ParsableModelInterface['applyMiscProps']
  isStandardProp: ParsableModelInterface['isStandardProp']
  static defineStandardProps = ParsableModelMixin.defineStandardProps
  static copyVerbatimStandardProps = ParsableModelMixin.copyVerbatimStandardProps
after class:
  ParsableModelMixin.mixInto(TheClass)
*/
Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_218553__(2),util_1=__nested_webpack_require_218553__(4),Mixin_1=__nested_webpack_require_218553__(15),ParsableModelMixin=/** @class */function(_super){function ParsableModelMixin(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(ParsableModelMixin,_super),ParsableModelMixin.defineStandardProps=function(propDefs){var proto=this.prototype;proto.hasOwnProperty("standardPropMap")||(proto.standardPropMap=Object.create(proto.standardPropMap)),util_1.copyOwnProps(propDefs,proto.standardPropMap)},ParsableModelMixin.copyVerbatimStandardProps=function(src,dest){var propName,map=this.prototype.standardPropMap;for(propName in map)null!=src[propName]&&// in the src object?
!0===map[propName]&&(dest[propName]=src[propName])},
/*
    Returns true/false for success.
    Meant to be only called ONCE, at object creation.
    */
ParsableModelMixin.prototype.applyProps=function(rawProps){var propName,standardPropMap=this.standardPropMap,manualProps={},miscProps={};for(propName in rawProps)!0===standardPropMap[propName]?// copy verbatim
this[propName]=rawProps[propName]:!1===standardPropMap[propName]?manualProps[propName]=rawProps[propName]:miscProps[propName]=rawProps[propName];return this.applyMiscProps(miscProps),this.applyManualStandardProps(manualProps)},
/*
    If subclasses override, they must call this supermethod and return the boolean response.
    Meant to be only called ONCE, at object creation.
    */
ParsableModelMixin.prototype.applyManualStandardProps=function(rawProps){return!0},
/*
    Can be called even after initial object creation.
    */
ParsableModelMixin.prototype.applyMiscProps=function(rawProps){
// subclasses can implement
},
/*
    TODO: why is this a method when defineStandardProps is static
    */
ParsableModelMixin.prototype.isStandardProp=function(propName){return propName in this.standardPropMap},ParsableModelMixin}(Mixin_1.default);exports.default=ParsableModelMixin,ParsableModelMixin.prototype.standardPropMap={}},
/* 53 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var EventInstance=/** @class */function(){function EventInstance(def,dateProfile){this.def=def,this.dateProfile=dateProfile}return EventInstance.prototype.toLegacy=function(){var dateProfile=this.dateProfile,obj=this.def.toLegacy();return obj.start=dateProfile.start.clone(),obj.end=dateProfile.end?dateProfile.end.clone():null,obj},EventInstance}();exports.default=EventInstance},
/* 54 */
/***/function(module,exports,__nested_webpack_require_222717__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_222717__(2),$=__nested_webpack_require_222717__(3),moment=__nested_webpack_require_222717__(0),EventDef_1=__nested_webpack_require_222717__(37),EventInstance_1=__nested_webpack_require_222717__(53),EventDateProfile_1=__nested_webpack_require_222717__(16),RecurringEventDef=/** @class */function(_super){function RecurringEventDef(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(RecurringEventDef,_super),RecurringEventDef.prototype.isAllDay=function(){return!this.startTime&&!this.endTime},RecurringEventDef.prototype.buildInstances=function(unzonedRange){var zonedDayStart,instanceStart,instanceEnd,calendar=this.source.calendar,unzonedDate=unzonedRange.getStart(),unzonedEnd=unzonedRange.getEnd(),instances=[];while(unzonedDate.isBefore(unzonedEnd))
// if everyday, or this particular day-of-week
this.dowHash&&!this.dowHash[unzonedDate.day()]||(zonedDayStart=calendar.applyTimezone(unzonedDate),instanceStart=zonedDayStart.clone(),instanceEnd=null,this.startTime?instanceStart.time(this.startTime):instanceStart.stripTime(),this.endTime&&(instanceEnd=zonedDayStart.clone().time(this.endTime)),instances.push(new EventInstance_1.default(this,// definition
new EventDateProfile_1.default(instanceStart,instanceEnd,calendar)))),unzonedDate.add(1,"days");return instances},RecurringEventDef.prototype.setDow=function(dowNumbers){this.dowHash||(this.dowHash={});for(var i=0;i<dowNumbers.length;i++)this.dowHash[dowNumbers[i]]=!0},RecurringEventDef.prototype.clone=function(){var def=_super.prototype.clone.call(this);return def.startTime&&(def.startTime=moment.duration(this.startTime)),def.endTime&&(def.endTime=moment.duration(this.endTime)),this.dowHash&&(def.dowHash=$.extend({},this.dowHash)),def},RecurringEventDef}(EventDef_1.default);exports.default=RecurringEventDef,
/*
HACK to work with TypeScript mixins
NOTE: if super-method fails, should still attempt to apply
*/
RecurringEventDef.prototype.applyProps=function(rawProps){var superSuccess=EventDef_1.default.prototype.applyProps.call(this,rawProps);return rawProps.start&&(this.startTime=moment.duration(rawProps.start)),rawProps.end&&(this.endTime=moment.duration(rawProps.end)),rawProps.dow&&this.setDow(rawProps.dow),superSuccess},
// Parsing
// ---------------------------------------------------------------------------------------------------------------------
RecurringEventDef.defineStandardProps({start:!1,end:!1,dow:!1})},
/* 55 */
/***/function(module,exports,__nested_webpack_require_226297__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_226297__(0),util_1=__nested_webpack_require_226297__(4),UnzonedRange_1=__nested_webpack_require_226297__(5),DateProfileGenerator=/** @class */function(){function DateProfileGenerator(_view){this._view=_view}return DateProfileGenerator.prototype.opt=function(name){return this._view.opt(name)},DateProfileGenerator.prototype.trimHiddenDays=function(unzonedRange){return this._view.trimHiddenDays(unzonedRange)},DateProfileGenerator.prototype.msToUtcMoment=function(ms,forceAllDay){return this._view.calendar.msToUtcMoment(ms,forceAllDay)},
/* Date Range Computation
    ------------------------------------------------------------------------------------------------------------------*/
// Builds a structure with info about what the dates/ranges will be for the "prev" view.
DateProfileGenerator.prototype.buildPrev=function(currentDateProfile){var prevDate=currentDateProfile.date.clone().startOf(currentDateProfile.currentRangeUnit).subtract(currentDateProfile.dateIncrement);return this.build(prevDate,-1)},
// Builds a structure with info about what the dates/ranges will be for the "next" view.
DateProfileGenerator.prototype.buildNext=function(currentDateProfile){var nextDate=currentDateProfile.date.clone().startOf(currentDateProfile.currentRangeUnit).add(currentDateProfile.dateIncrement);return this.build(nextDate,1)},
// Builds a structure holding dates/ranges for rendering around the given date.
// Optional direction param indicates whether the date is being incremented/decremented
// from its previous value. decremented = -1, incremented = 1 (default).
DateProfileGenerator.prototype.build=function(date,direction,forceToValid){void 0===forceToValid&&(forceToValid=!1);var validUnzonedRange,currentInfo,isRangeAllDay,renderUnzonedRange,activeUnzonedRange,isValid,isDateAllDay=!date.hasTime(),minTime=null,maxTime=null;return validUnzonedRange=this.buildValidRange(),validUnzonedRange=this.trimHiddenDays(validUnzonedRange),forceToValid&&(date=this.msToUtcMoment(validUnzonedRange.constrainDate(date),// returns MS
isDateAllDay)),currentInfo=this.buildCurrentRangeInfo(date,direction),isRangeAllDay=/^(year|month|week|day)$/.test(currentInfo.unit),renderUnzonedRange=this.buildRenderRange(this.trimHiddenDays(currentInfo.unzonedRange),currentInfo.unit,isRangeAllDay),renderUnzonedRange=this.trimHiddenDays(renderUnzonedRange),activeUnzonedRange=renderUnzonedRange.clone(),this.opt("showNonCurrentDates")||(activeUnzonedRange=activeUnzonedRange.intersect(currentInfo.unzonedRange)),minTime=moment.duration(this.opt("minTime")),maxTime=moment.duration(this.opt("maxTime")),activeUnzonedRange=this.adjustActiveRange(activeUnzonedRange,minTime,maxTime),activeUnzonedRange=activeUnzonedRange.intersect(validUnzonedRange),// might return null
activeUnzonedRange&&(date=this.msToUtcMoment(activeUnzonedRange.constrainDate(date),// returns MS
isDateAllDay)),
// it's invalid if the originally requested date is not contained,
// or if the range is completely outside of the valid range.
isValid=currentInfo.unzonedRange.intersectsWith(validUnzonedRange),{
// constraint for where prev/next operations can go and where events can be dragged/resized to.
// an object with optional start and end properties.
validUnzonedRange:validUnzonedRange,
// range the view is formally responsible for.
// for example, a month view might have 1st-31st, excluding padded dates
currentUnzonedRange:currentInfo.unzonedRange,
// name of largest unit being displayed, like "month" or "week"
currentRangeUnit:currentInfo.unit,isRangeAllDay:isRangeAllDay,
// dates that display events and accept drag-n-drop
// will be `null` if no dates accept events
activeUnzonedRange:activeUnzonedRange,
// date range with a rendered skeleton
// includes not-active days that need some sort of DOM
renderUnzonedRange:renderUnzonedRange,
// Duration object that denotes the first visible time of any given day
minTime:minTime,
// Duration object that denotes the exclusive visible end time of any given day
maxTime:maxTime,isValid:isValid,date:date,
// how far the current date will move for a prev/next operation
dateIncrement:this.buildDateIncrement(currentInfo.duration)}},
// Builds an object with optional start/end properties.
// Indicates the minimum/maximum dates to display.
// not responsible for trimming hidden days.
DateProfileGenerator.prototype.buildValidRange=function(){return this._view.getUnzonedRangeOption("validRange",this._view.calendar.getNow())||new UnzonedRange_1.default;// completely open-ended
},
// Builds a structure with info about the "current" range, the range that is
// highlighted as being the current month for example.
// See build() for a description of `direction`.
// Guaranteed to have `range` and `unit` properties. `duration` is optional.
// TODO: accept a MS-time instead of a moment `date`?
DateProfileGenerator.prototype.buildCurrentRangeInfo=function(date,direction){var dayCount,viewSpec=this._view.viewSpec,duration=null,unit=null,unzonedRange=null;return viewSpec.duration?(duration=viewSpec.duration,unit=viewSpec.durationUnit,unzonedRange=this.buildRangeFromDuration(date,direction,duration,unit)):(dayCount=this.opt("dayCount"))?(unit="day",unzonedRange=this.buildRangeFromDayCount(date,direction,dayCount)):(unzonedRange=this.buildCustomVisibleRange(date))?unit=util_1.computeGreatestUnit(unzonedRange.getStart(),unzonedRange.getEnd()):(duration=this.getFallbackDuration(),unit=util_1.computeGreatestUnit(duration),unzonedRange=this.buildRangeFromDuration(date,direction,duration,unit)),{duration:duration,unit:unit,unzonedRange:unzonedRange}},DateProfileGenerator.prototype.getFallbackDuration=function(){return moment.duration({days:1})},
// Returns a new activeUnzonedRange to have time values (un-ambiguate)
// minTime or maxTime causes the range to expand.
DateProfileGenerator.prototype.adjustActiveRange=function(unzonedRange,minTime,maxTime){var start=unzonedRange.getStart(),end=unzonedRange.getEnd();return this._view.usesMinMaxTime&&(minTime<0&&start.time(0).add(minTime),maxTime>864e5&&// beyond 24 hours?
end.time(maxTime-864e5)),new UnzonedRange_1.default(start,end)},
// Builds the "current" range when it is specified as an explicit duration.
// `unit` is the already-computed computeGreatestUnit value of duration.
// TODO: accept a MS-time instead of a moment `date`?
DateProfileGenerator.prototype.buildRangeFromDuration=function(date,direction,duration,unit){var dateIncrementInput,dateIncrementDuration,start,end,res,alignment=this.opt("dateAlignment");function computeRes(){start=date.clone().startOf(alignment),end=start.clone().add(duration),res=new UnzonedRange_1.default(start,end)}
// compute what the alignment should be
return alignment||(dateIncrementInput=this.opt("dateIncrement"),dateIncrementInput?(dateIncrementDuration=moment.duration(dateIncrementInput),
// use the smaller of the two units
alignment=dateIncrementDuration<duration?util_1.computeDurationGreatestUnit(dateIncrementDuration,dateIncrementInput):unit):alignment=unit),
// if the view displays a single day or smaller
duration.as("days")<=1&&this._view.isHiddenDay(start)&&(start=this._view.skipHiddenDays(start,direction),start.startOf("day")),computeRes(),
// if range is completely enveloped by hidden days, go past the hidden days
this.trimHiddenDays(res)||(date=this._view.skipHiddenDays(date,direction),computeRes()),res},
// Builds the "current" range when a dayCount is specified.
// TODO: accept a MS-time instead of a moment `date`?
DateProfileGenerator.prototype.buildRangeFromDayCount=function(date,direction,dayCount){var start,end,customAlignment=this.opt("dateAlignment"),runningCount=0;if(customAlignment||-1!==direction){start=date.clone(),customAlignment&&start.startOf(customAlignment),start.startOf("day"),start=this._view.skipHiddenDays(start),end=start.clone();do{end.add(1,"day"),this._view.isHiddenDay(end)||runningCount++}while(runningCount<dayCount)}else{end=date.clone().startOf("day").add(1,"day"),end=this._view.skipHiddenDays(end,-1,!0),start=end.clone();do{start.add(-1,"day"),this._view.isHiddenDay(start)||runningCount++}while(runningCount<dayCount)}return new UnzonedRange_1.default(start,end)},
// Builds a normalized range object for the "visible" range,
// which is a way to define the currentUnzonedRange and activeUnzonedRange at the same time.
// TODO: accept a MS-time instead of a moment `date`?
DateProfileGenerator.prototype.buildCustomVisibleRange=function(date){var visibleUnzonedRange=this._view.getUnzonedRangeOption("visibleRange",this._view.calendar.applyTimezone(date));return!visibleUnzonedRange||null!=visibleUnzonedRange.startMs&&null!=visibleUnzonedRange.endMs?visibleUnzonedRange:null},
// Computes the range that will represent the element/cells for *rendering*,
// but which may have voided days/times.
// not responsible for trimming hidden days.
DateProfileGenerator.prototype.buildRenderRange=function(currentUnzonedRange,currentRangeUnit,isRangeAllDay){return currentUnzonedRange.clone()},
// Compute the duration value that should be added/substracted to the current date
// when a prev/next operation happens.
DateProfileGenerator.prototype.buildDateIncrement=function(fallback){var customAlignment,dateIncrementInput=this.opt("dateIncrement");return dateIncrementInput?moment.duration(dateIncrementInput):(customAlignment=this.opt("dateAlignment"))?moment.duration(1,customAlignment):fallback||moment.duration({days:1})},DateProfileGenerator}();exports.default=DateProfileGenerator},
/* 56 */
/***/function(module,exports,__nested_webpack_require_239550__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_239550__(2),$=__nested_webpack_require_239550__(3),util_1=__nested_webpack_require_239550__(4),Promise_1=__nested_webpack_require_239550__(21),EventSource_1=__nested_webpack_require_239550__(6),SingleEventDef_1=__nested_webpack_require_239550__(9),ArrayEventSource=/** @class */function(_super){function ArrayEventSource(calendar){var _this=_super.call(this,calendar)||this;// for if setRawEventDefs is never called
return _this.eventDefs=[],_this}return tslib_1.__extends(ArrayEventSource,_super),ArrayEventSource.parse=function(rawInput,calendar){var rawProps;
// normalize raw input
return $.isArray(rawInput.events)?// extended form
rawProps=rawInput:$.isArray(rawInput)&&(// short form
rawProps={events:rawInput}),!!rawProps&&EventSource_1.default.parse.call(this,rawProps,calendar)},ArrayEventSource.prototype.setRawEventDefs=function(rawEventDefs){this.rawEventDefs=rawEventDefs,this.eventDefs=this.parseEventDefs(rawEventDefs)},ArrayEventSource.prototype.fetch=function(start,end,timezone){var i,eventDefs=this.eventDefs;if(null!=this.currentTimezone&&this.currentTimezone!==timezone)for(i=0;i<eventDefs.length;i++)eventDefs[i]instanceof SingleEventDef_1.default&&eventDefs[i].rezone();return this.currentTimezone=timezone,Promise_1.default.resolve(eventDefs)},ArrayEventSource.prototype.addEventDef=function(eventDef){this.eventDefs.push(eventDef)},
/*
    eventDefId already normalized to a string
    */
ArrayEventSource.prototype.removeEventDefsById=function(eventDefId){return util_1.removeMatching(this.eventDefs,(function(eventDef){return eventDef.id===eventDefId}))},ArrayEventSource.prototype.removeAllEventDefs=function(){this.eventDefs=[]},ArrayEventSource.prototype.getPrimitive=function(){return this.rawEventDefs},ArrayEventSource.prototype.applyManualStandardProps=function(rawProps){var superSuccess=_super.prototype.applyManualStandardProps.call(this,rawProps);return this.setRawEventDefs(rawProps.events),superSuccess},ArrayEventSource}(EventSource_1.default);exports.default=ArrayEventSource,ArrayEventSource.defineStandardProps({events:!1})},
/* 57 */
/***/function(module,exports,__nested_webpack_require_242526__){Object.defineProperty(exports,"__esModule",{value:!0});var StandardTheme_1=__nested_webpack_require_242526__(221),JqueryUiTheme_1=__nested_webpack_require_242526__(222),themeClassHash={};function defineThemeSystem(themeName,themeClass){themeClassHash[themeName]=themeClass}function getThemeSystemClass(themeSetting){return themeSetting?!0===themeSetting?JqueryUiTheme_1.default:themeClassHash[themeSetting]:StandardTheme_1.default}exports.defineThemeSystem=defineThemeSystem,exports.getThemeSystemClass=getThemeSystemClass},
/* 58 */
/***/function(module,exports,__nested_webpack_require_243268__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_243268__(3),util_1=__nested_webpack_require_243268__(4),CoordCache=/** @class */function(){function CoordCache(options){this.isHorizontal=!1,// whether to query for left/right/width
this.isVertical=!1,// whether to query for top/bottom/height
this.els=$(options.els),this.isHorizontal=options.isHorizontal,this.isVertical=options.isVertical,this.forcedOffsetParentEl=options.offsetParent?$(options.offsetParent):null}
// Queries the els for coordinates and stores them.
// Call this method before using and of the get* methods below.
return CoordCache.prototype.build=function(){var offsetParentEl=this.forcedOffsetParentEl;!offsetParentEl&&this.els.length>0&&(offsetParentEl=this.els.eq(0).offsetParent()),this.origin=offsetParentEl?offsetParentEl.offset():null,this.boundingRect=this.queryBoundingRect(),this.isHorizontal&&this.buildElHorizontals(),this.isVertical&&this.buildElVerticals()},
// Destroys all internal data about coordinates, freeing memory
CoordCache.prototype.clear=function(){this.origin=null,this.boundingRect=null,this.lefts=null,this.rights=null,this.tops=null,this.bottoms=null},
// When called, if coord caches aren't built, builds them
CoordCache.prototype.ensureBuilt=function(){this.origin||this.build()},
// Populates the left/right internal coordinate arrays
CoordCache.prototype.buildElHorizontals=function(){var lefts=[],rights=[];this.els.each((function(i,node){var el=$(node),left=el.offset().left,width=el.outerWidth();lefts.push(left),rights.push(left+width)})),this.lefts=lefts,this.rights=rights},
// Populates the top/bottom internal coordinate arrays
CoordCache.prototype.buildElVerticals=function(){var tops=[],bottoms=[];this.els.each((function(i,node){var el=$(node),top=el.offset().top,height=el.outerHeight();tops.push(top),bottoms.push(top+height)})),this.tops=tops,this.bottoms=bottoms},
// Given a left offset (from document left), returns the index of the el that it horizontally intersects.
// If no intersection is made, returns undefined.
CoordCache.prototype.getHorizontalIndex=function(leftOffset){this.ensureBuilt();var i,lefts=this.lefts,rights=this.rights,len=lefts.length;for(i=0;i<len;i++)if(leftOffset>=lefts[i]&&leftOffset<rights[i])return i},
// Given a top offset (from document top), returns the index of the el that it vertically intersects.
// If no intersection is made, returns undefined.
CoordCache.prototype.getVerticalIndex=function(topOffset){this.ensureBuilt();var i,tops=this.tops,bottoms=this.bottoms,len=tops.length;for(i=0;i<len;i++)if(topOffset>=tops[i]&&topOffset<bottoms[i])return i},
// Gets the left offset (from document left) of the element at the given index
CoordCache.prototype.getLeftOffset=function(leftIndex){return this.ensureBuilt(),this.lefts[leftIndex]},
// Gets the left position (from offsetParent left) of the element at the given index
CoordCache.prototype.getLeftPosition=function(leftIndex){return this.ensureBuilt(),this.lefts[leftIndex]-this.origin.left},
// Gets the right offset (from document left) of the element at the given index.
// This value is NOT relative to the document's right edge, like the CSS concept of "right" would be.
CoordCache.prototype.getRightOffset=function(leftIndex){return this.ensureBuilt(),this.rights[leftIndex]},
// Gets the right position (from offsetParent left) of the element at the given index.
// This value is NOT relative to the offsetParent's right edge, like the CSS concept of "right" would be.
CoordCache.prototype.getRightPosition=function(leftIndex){return this.ensureBuilt(),this.rights[leftIndex]-this.origin.left},
// Gets the width of the element at the given index
CoordCache.prototype.getWidth=function(leftIndex){return this.ensureBuilt(),this.rights[leftIndex]-this.lefts[leftIndex]},
// Gets the top offset (from document top) of the element at the given index
CoordCache.prototype.getTopOffset=function(topIndex){return this.ensureBuilt(),this.tops[topIndex]},
// Gets the top position (from offsetParent top) of the element at the given position
CoordCache.prototype.getTopPosition=function(topIndex){return this.ensureBuilt(),this.tops[topIndex]-this.origin.top},
// Gets the bottom offset (from the document top) of the element at the given index.
// This value is NOT relative to the offsetParent's bottom edge, like the CSS concept of "bottom" would be.
CoordCache.prototype.getBottomOffset=function(topIndex){return this.ensureBuilt(),this.bottoms[topIndex]},
// Gets the bottom position (from the offsetParent top) of the element at the given index.
// This value is NOT relative to the offsetParent's bottom edge, like the CSS concept of "bottom" would be.
CoordCache.prototype.getBottomPosition=function(topIndex){return this.ensureBuilt(),this.bottoms[topIndex]-this.origin.top},
// Gets the height of the element at the given index
CoordCache.prototype.getHeight=function(topIndex){return this.ensureBuilt(),this.bottoms[topIndex]-this.tops[topIndex]},
// Bounding Rect
// TODO: decouple this from CoordCache
// Compute and return what the elements' bounding rectangle is, from the user's perspective.
// Right now, only returns a rectangle if constrained by an overflow:scroll element.
// Returns null if there are no elements
CoordCache.prototype.queryBoundingRect=function(){var scrollParentEl;return this.els.length>0&&(scrollParentEl=util_1.getScrollParent(this.els.eq(0)),!scrollParentEl.is(document)&&!scrollParentEl.is("html,body"))?util_1.getClientRect(scrollParentEl):null},CoordCache.prototype.isPointInBounds=function(leftOffset,topOffset){return this.isLeftInBounds(leftOffset)&&this.isTopInBounds(topOffset)},CoordCache.prototype.isLeftInBounds=function(leftOffset){return!this.boundingRect||leftOffset>=this.boundingRect.left&&leftOffset<this.boundingRect.right},CoordCache.prototype.isTopInBounds=function(topOffset){return!this.boundingRect||topOffset>=this.boundingRect.top&&topOffset<this.boundingRect.bottom},CoordCache}();exports.default=CoordCache},
/* 59 */
/***/function(module,exports,__nested_webpack_require_251605__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_251605__(3),util_1=__nested_webpack_require_251605__(4),ListenerMixin_1=__nested_webpack_require_251605__(7),GlobalEmitter_1=__nested_webpack_require_251605__(23),DragListener=/** @class */function(){function DragListener(options){this.isInteracting=!1,this.isDistanceSurpassed=!1,this.isDelayEnded=!1,this.isDragging=!1,this.isTouch=!1,this.isGeneric=!1,// initiated by 'dragstart' (jqui)
this.shouldCancelTouchScroll=!0,this.scrollAlwaysKills=!1,this.isAutoScroll=!1,
// defaults
this.scrollSensitivity=30,// pixels from edge for scrolling to start
this.scrollSpeed=200,// pixels per second, at maximum speed
this.scrollIntervalMs=50,// millisecond wait between scroll increment
this.options=options||{}}
// Interaction (high-level)
// -----------------------------------------------------------------------------------------------------------------
return DragListener.prototype.startInteraction=function(ev,extraOptions){if(void 0===extraOptions&&(extraOptions={}),"mousedown"===ev.type){if(GlobalEmitter_1.default.get().shouldIgnoreMouse())return;if(!util_1.isPrimaryMouseButton(ev))return;ev.preventDefault()}this.isInteracting||(
// process options
this.delay=util_1.firstDefined(extraOptions.delay,this.options.delay,0),this.minDistance=util_1.firstDefined(extraOptions.distance,this.options.distance,0),this.subjectEl=this.options.subjectEl,util_1.preventSelection($("body")),this.isInteracting=!0,this.isTouch=util_1.getEvIsTouch(ev),this.isGeneric="dragstart"===ev.type,this.isDelayEnded=!1,this.isDistanceSurpassed=!1,this.originX=util_1.getEvX(ev),this.originY=util_1.getEvY(ev),this.scrollEl=util_1.getScrollParent($(ev.target)),this.bindHandlers(),this.initAutoScroll(),this.handleInteractionStart(ev),this.startDelay(ev),this.minDistance||this.handleDistanceSurpassed(ev))},DragListener.prototype.handleInteractionStart=function(ev){this.trigger("interactionStart",ev)},DragListener.prototype.endInteraction=function(ev,isCancelled){this.isInteracting&&(this.endDrag(ev),this.delayTimeoutId&&(clearTimeout(this.delayTimeoutId),this.delayTimeoutId=null),this.destroyAutoScroll(),this.unbindHandlers(),this.isInteracting=!1,this.handleInteractionEnd(ev,isCancelled),util_1.allowSelection($("body")))},DragListener.prototype.handleInteractionEnd=function(ev,isCancelled){this.trigger("interactionEnd",ev,isCancelled||!1)},
// Binding To DOM
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.bindHandlers=function(){
// some browsers (Safari in iOS 10) don't allow preventDefault on touch events that are bound after touchstart,
// so listen to the GlobalEmitter singleton, which is always bound, instead of the document directly.
var globalEmitter=GlobalEmitter_1.default.get();this.isGeneric?this.listenTo($(document),{drag:this.handleMove,dragstop:this.endInteraction}):this.isTouch?this.listenTo(globalEmitter,{touchmove:this.handleTouchMove,touchend:this.endInteraction,scroll:this.handleTouchScroll}):this.listenTo(globalEmitter,{mousemove:this.handleMouseMove,mouseup:this.endInteraction}),this.listenTo(globalEmitter,{selectstart:util_1.preventDefault,contextmenu:util_1.preventDefault})},DragListener.prototype.unbindHandlers=function(){this.stopListeningTo(GlobalEmitter_1.default.get()),this.stopListeningTo($(document))},
// Drag (high-level)
// -----------------------------------------------------------------------------------------------------------------
// extraOptions ignored if drag already started
DragListener.prototype.startDrag=function(ev,extraOptions){this.startInteraction(ev,extraOptions),// ensure interaction began
this.isDragging||(this.isDragging=!0,this.handleDragStart(ev))},DragListener.prototype.handleDragStart=function(ev){this.trigger("dragStart",ev)},DragListener.prototype.handleMove=function(ev){var distanceSq,dx=util_1.getEvX(ev)-this.originX,dy=util_1.getEvY(ev)-this.originY,minDistance=this.minDistance;// current distance from the origin, squared
this.isDistanceSurpassed||(distanceSq=dx*dx+dy*dy,distanceSq>=minDistance*minDistance&&// use pythagorean theorem
this.handleDistanceSurpassed(ev)),this.isDragging&&this.handleDrag(dx,dy,ev)},
// Called while the mouse is being moved and when we know a legitimate drag is taking place
DragListener.prototype.handleDrag=function(dx,dy,ev){this.trigger("drag",dx,dy,ev),this.updateAutoScroll(ev)},DragListener.prototype.endDrag=function(ev){this.isDragging&&(this.isDragging=!1,this.handleDragEnd(ev))},DragListener.prototype.handleDragEnd=function(ev){this.trigger("dragEnd",ev)},
// Delay
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.startDelay=function(initialEv){var _this=this;this.delay?this.delayTimeoutId=setTimeout((function(){_this.handleDelayEnd(initialEv)}),this.delay):this.handleDelayEnd(initialEv)},DragListener.prototype.handleDelayEnd=function(initialEv){this.isDelayEnded=!0,this.isDistanceSurpassed&&this.startDrag(initialEv)},
// Distance
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.handleDistanceSurpassed=function(ev){this.isDistanceSurpassed=!0,this.isDelayEnded&&this.startDrag(ev)},
// Mouse / Touch
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.handleTouchMove=function(ev){
// prevent inertia and touchmove-scrolling while dragging
this.isDragging&&this.shouldCancelTouchScroll&&ev.preventDefault(),this.handleMove(ev)},DragListener.prototype.handleMouseMove=function(ev){this.handleMove(ev)},
// Scrolling (unrelated to auto-scroll)
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.handleTouchScroll=function(ev){
// if the drag is being initiated by touch, but a scroll happens before
// the drag-initiating delay is over, cancel the drag
this.isDragging&&!this.scrollAlwaysKills||this.endInteraction(ev,!0)},
// Utils
// -----------------------------------------------------------------------------------------------------------------
// Triggers a callback. Calls a function in the option hash of the same name.
// Arguments beyond the first `name` are forwarded on.
DragListener.prototype.trigger=function(name){for(var args=[],_i=1;_i<arguments.length;_i++)args[_i-1]=arguments[_i];this.options[name]&&this.options[name].apply(this,args),
// makes _methods callable by event name. TODO: kill this
this["_"+name]&&this["_"+name].apply(this,args)},
// Auto-scroll
// -----------------------------------------------------------------------------------------------------------------
DragListener.prototype.initAutoScroll=function(){var scrollEl=this.scrollEl;this.isAutoScroll=this.options.scroll&&scrollEl&&!scrollEl.is(window)&&!scrollEl.is(document),this.isAutoScroll&&
// debounce makes sure rapid calls don't happen
this.listenTo(scrollEl,"scroll",util_1.debounce(this.handleDebouncedScroll,100))},DragListener.prototype.destroyAutoScroll=function(){this.endAutoScroll(),// kill any animation loop
// remove the scroll handler if there is a scrollEl
this.isAutoScroll&&this.stopListeningTo(this.scrollEl,"scroll")},
// Computes and stores the bounding rectangle of scrollEl
DragListener.prototype.computeScrollBounds=function(){this.isAutoScroll&&(this.scrollBounds=util_1.getOuterRect(this.scrollEl))},
// Called when the dragging is in progress and scrolling should be updated
DragListener.prototype.updateAutoScroll=function(ev){var topCloseness,bottomCloseness,leftCloseness,rightCloseness,sensitivity=this.scrollSensitivity,bounds=this.scrollBounds,topVel=0,leftVel=0;bounds&&(// only scroll if scrollEl exists
// compute closeness to edges. valid range is from 0.0 - 1.0
topCloseness=(sensitivity-(util_1.getEvY(ev)-bounds.top))/sensitivity,bottomCloseness=(sensitivity-(bounds.bottom-util_1.getEvY(ev)))/sensitivity,leftCloseness=(sensitivity-(util_1.getEvX(ev)-bounds.left))/sensitivity,rightCloseness=(sensitivity-(bounds.right-util_1.getEvX(ev)))/sensitivity,
// translate vertical closeness into velocity.
// mouse must be completely in bounds for velocity to happen.
topCloseness>=0&&topCloseness<=1?topVel=topCloseness*this.scrollSpeed*-1:bottomCloseness>=0&&bottomCloseness<=1&&(topVel=bottomCloseness*this.scrollSpeed),
// translate horizontal closeness into velocity
leftCloseness>=0&&leftCloseness<=1?leftVel=leftCloseness*this.scrollSpeed*-1:rightCloseness>=0&&rightCloseness<=1&&(leftVel=rightCloseness*this.scrollSpeed)),this.setScrollVel(topVel,leftVel)},
// Sets the speed-of-scrolling for the scrollEl
DragListener.prototype.setScrollVel=function(topVel,leftVel){this.scrollTopVel=topVel,this.scrollLeftVel=leftVel,this.constrainScrollVel(),// massages into realistic values
// if there is non-zero velocity, and an animation loop hasn't already started, then START
!this.scrollTopVel&&!this.scrollLeftVel||this.scrollIntervalId||(this.scrollIntervalId=setInterval(util_1.proxy(this,"scrollIntervalFunc"),// scope to `this`
this.scrollIntervalMs))},
// Forces scrollTopVel and scrollLeftVel to be zero if scrolling has already gone all the way
DragListener.prototype.constrainScrollVel=function(){var el=this.scrollEl;this.scrollTopVel<0?// scrolling up?
el.scrollTop()<=0&&(// already scrolled all the way up?
this.scrollTopVel=0):this.scrollTopVel>0&&el.scrollTop()+el[0].clientHeight>=el[0].scrollHeight&&(// already scrolled all the way down?
this.scrollTopVel=0),this.scrollLeftVel<0?// scrolling left?
el.scrollLeft()<=0&&(// already scrolled all the left?
this.scrollLeftVel=0):this.scrollLeftVel>0&&el.scrollLeft()+el[0].clientWidth>=el[0].scrollWidth&&(// already scrolled all the way right?
this.scrollLeftVel=0)},
// This function gets called during every iteration of the scrolling animation loop
DragListener.prototype.scrollIntervalFunc=function(){var el=this.scrollEl,frac=this.scrollIntervalMs/1e3;// considering animation frequency, what the vel should be mult'd by
// change the value of scrollEl's scroll
this.scrollTopVel&&el.scrollTop(el.scrollTop()+this.scrollTopVel*frac),this.scrollLeftVel&&el.scrollLeft(el.scrollLeft()+this.scrollLeftVel*frac),this.constrainScrollVel(),// since the scroll values changed, recompute the velocities
// if scrolled all the way, which causes the vels to be zero, stop the animation loop
this.scrollTopVel||this.scrollLeftVel||this.endAutoScroll()},
// Kills any existing scrolling animation loop
DragListener.prototype.endAutoScroll=function(){this.scrollIntervalId&&(clearInterval(this.scrollIntervalId),this.scrollIntervalId=null,this.handleScrollEnd())},
// Get called when the scrollEl is scrolled (NOTE: this is delayed via debounce)
DragListener.prototype.handleDebouncedScroll=function(){
// recompute all coordinates, but *only* if this is *not* part of our scrolling animation
this.scrollIntervalId||this.handleScrollEnd()},DragListener.prototype.handleScrollEnd=function(){
// Called when scrolling has stopped, whether through auto scroll, or the user scrolling
},DragListener}();exports.default=DragListener,ListenerMixin_1.default.mixInto(DragListener)},
/* 60 */
/***/function(module,exports,__nested_webpack_require_268140__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_268140__(2),util_1=__nested_webpack_require_268140__(4),Mixin_1=__nested_webpack_require_268140__(15),DayTableMixin=/** @class */function(_super){function DayTableMixin(){return null!==_super&&_super.apply(this,arguments)||this}
// Populates internal variables used for date calculation and rendering
return tslib_1.__extends(DayTableMixin,_super),DayTableMixin.prototype.updateDayTable=function(){var daysPerRow,firstDay,rowCnt,t=this,view=t.view,calendar=view.calendar,date=calendar.msToUtcMoment(t.dateProfile.renderUnzonedRange.startMs,!0),end=calendar.msToUtcMoment(t.dateProfile.renderUnzonedRange.endMs,!0),dayIndex=-1,dayIndices=[],dayDates=[];while(date.isBefore(end))// loop each day from start to end
view.isHiddenDay(date)?dayIndices.push(dayIndex+.5):(dayIndex++,dayIndices.push(dayIndex),dayDates.push(date.clone())),date.add(1,"days");if(this.breakOnWeeks){for(
// count columns until the day-of-week repeats
firstDay=dayDates[0].day(),daysPerRow=1;daysPerRow<dayDates.length;daysPerRow++)if(dayDates[daysPerRow].day()===firstDay)break;rowCnt=Math.ceil(dayDates.length/daysPerRow)}else rowCnt=1,daysPerRow=dayDates.length;this.dayDates=dayDates,this.dayIndices=dayIndices,this.daysPerRow=daysPerRow,this.rowCnt=rowCnt,this.updateDayTableCols()},
// Computes and assigned the colCnt property and updates any options that may be computed from it
DayTableMixin.prototype.updateDayTableCols=function(){this.colCnt=this.computeColCnt(),this.colHeadFormat=this.opt("columnHeaderFormat")||this.opt("columnFormat")||// deprecated
this.computeColHeadFormat()},
// Determines how many columns there should be in the table
DayTableMixin.prototype.computeColCnt=function(){return this.daysPerRow},
// Computes the ambiguously-timed moment for the given cell
DayTableMixin.prototype.getCellDate=function(row,col){return this.dayDates[this.getCellDayIndex(row,col)].clone()},
// Computes the ambiguously-timed date range for the given cell
DayTableMixin.prototype.getCellRange=function(row,col){var start=this.getCellDate(row,col),end=start.clone().add(1,"days");return{start:start,end:end}},
// Returns the number of day cells, chronologically, from the first of the grid (0-based)
DayTableMixin.prototype.getCellDayIndex=function(row,col){return row*this.daysPerRow+this.getColDayIndex(col)},
// Returns the numner of day cells, chronologically, from the first cell in *any given row*
DayTableMixin.prototype.getColDayIndex=function(col){return this.isRTL?this.colCnt-1-col:col},
// Given a date, returns its chronolocial cell-index from the first cell of the grid.
// If the date lies between cells (because of hiddenDays), returns a floating-point value between offsets.
// If before the first offset, returns a negative number.
// If after the last offset, returns an offset past the last cell offset.
// Only works for *start* dates of cells. Will not work for exclusive end dates for cells.
DayTableMixin.prototype.getDateDayIndex=function(date){var dayIndices=this.dayIndices,dayOffset=date.diff(this.dayDates[0],"days");return dayOffset<0?dayIndices[0]-1:dayOffset>=dayIndices.length?dayIndices[dayIndices.length-1]+1:dayIndices[dayOffset]},
/* Options
    ------------------------------------------------------------------------------------------------------------------*/
// Computes a default column header formatting string if `colFormat` is not explicitly defined
DayTableMixin.prototype.computeColHeadFormat=function(){
// if more than one week row, or if there are a lot of columns with not much space,
// put just the day numbers will be in each cell
return this.rowCnt>1||this.colCnt>10?"ddd":this.colCnt>1?this.opt("dayOfMonthFormat"):"dddd"},
/* Slicing
    ------------------------------------------------------------------------------------------------------------------*/
// Slices up a date range into a segment for every week-row it intersects with
DayTableMixin.prototype.sliceRangeByRow=function(unzonedRange){var row,rowFirst,rowLast,segFirst,segLast,daysPerRow=this.daysPerRow,normalRange=this.view.computeDayRange(unzonedRange),rangeFirst=this.getDateDayIndex(normalRange.start),rangeLast=this.getDateDayIndex(normalRange.end.clone().subtract(1,"days")),segs=[];// inclusive day-index range for segment
for(row=0;row<this.rowCnt;row++)rowFirst=row*daysPerRow,rowLast=rowFirst+daysPerRow-1,
// intersect segment's offset range with the row's
segFirst=Math.max(rangeFirst,rowFirst),segLast=Math.min(rangeLast,rowLast),
// deal with in-between indices
segFirst=Math.ceil(segFirst),// in-between starts round to next cell
segLast=Math.floor(segLast),// in-between ends round to prev cell
segFirst<=segLast&&// was there any intersection with the current row?
segs.push({row:row,
// normalize to start of row
firstRowDayIndex:segFirst-rowFirst,lastRowDayIndex:segLast-rowFirst,
// must be matching integers to be the segment's start/end
isStart:segFirst===rangeFirst,isEnd:segLast===rangeLast});return segs},
// Slices up a date range into a segment for every day-cell it intersects with.
// TODO: make more DRY with sliceRangeByRow somehow.
DayTableMixin.prototype.sliceRangeByDay=function(unzonedRange){var row,rowFirst,rowLast,i,segFirst,segLast,daysPerRow=this.daysPerRow,normalRange=this.view.computeDayRange(unzonedRange),rangeFirst=this.getDateDayIndex(normalRange.start),rangeLast=this.getDateDayIndex(normalRange.end.clone().subtract(1,"days")),segs=[];// inclusive day-index range for segment
for(row=0;row<this.rowCnt;row++)for(rowFirst=row*daysPerRow,rowLast=rowFirst+daysPerRow-1,i=rowFirst;i<=rowLast;i++)
// intersect segment's offset range with the row's
segFirst=Math.max(rangeFirst,i),segLast=Math.min(rangeLast,i),
// deal with in-between indices
segFirst=Math.ceil(segFirst),// in-between starts round to next cell
segLast=Math.floor(segLast),// in-between ends round to prev cell
segFirst<=segLast&&// was there any intersection with the current row?
segs.push({row:row,
// normalize to start of row
firstRowDayIndex:segFirst-rowFirst,lastRowDayIndex:segLast-rowFirst,
// must be matching integers to be the segment's start/end
isStart:segFirst===rangeFirst,isEnd:segLast===rangeLast});return segs},
/* Header Rendering
    ------------------------------------------------------------------------------------------------------------------*/
DayTableMixin.prototype.renderHeadHtml=function(){var theme=this.view.calendar.theme;return'<div class="fc-row '+theme.getClass("headerRow")+'"><table class="'+theme.getClass("tableGrid")+'"><thead>'+this.renderHeadTrHtml()+"</thead></table></div>"},DayTableMixin.prototype.renderHeadIntroHtml=function(){return this.renderIntroHtml();// fall back to generic
},DayTableMixin.prototype.renderHeadTrHtml=function(){return"<tr>"+(this.isRTL?"":this.renderHeadIntroHtml())+this.renderHeadDateCellsHtml()+(this.isRTL?this.renderHeadIntroHtml():"")+"</tr>"},DayTableMixin.prototype.renderHeadDateCellsHtml=function(){var col,date,htmls=[];for(col=0;col<this.colCnt;col++)date=this.getCellDate(0,col),htmls.push(this.renderHeadDateCellHtml(date));return htmls.join("")},
// TODO: when internalApiVersion, accept an object for HTML attributes
// (colspan should be no different)
DayTableMixin.prototype.renderHeadDateCellHtml=function(date,colspan,otherAttrs){var innerHtml,t=this,view=t.view,isDateValid=t.dateProfile.activeUnzonedRange.containsDate(date),classNames=["fc-day-header",view.calendar.theme.getClass("widgetHeader")];return innerHtml="function"===typeof t.opt("columnHeaderHtml")?t.opt("columnHeaderHtml")(date):"function"===typeof t.opt("columnHeaderText")?util_1.htmlEscape(t.opt("columnHeaderText")(date)):util_1.htmlEscape(date.format(t.colHeadFormat)),
// if only one row of days, the classNames on the header can represent the specific days beneath
1===t.rowCnt?classNames=classNames.concat(
// includes the day-of-week class
// noThemeHighlight=true (don't highlight the header)
t.getDayClasses(date,!0)):classNames.push("fc-"+util_1.dayIDs[date.day()]),'<th class="'+classNames.join(" ")+'"'+(1===(isDateValid&&t.rowCnt)?' data-date="'+date.format("YYYY-MM-DD")+'"':"")+(colspan>1?' colspan="'+colspan+'"':"")+(otherAttrs?" "+otherAttrs:"")+">"+(isDateValid?
// don't make a link if the heading could represent multiple days, or if there's only one day (forceOff)
view.buildGotoAnchorHtml({date:date,forceOff:t.rowCnt>1||1===t.colCnt},innerHtml):
// if not valid, display text, but no link
innerHtml)+"</th>"},
/* Background Rendering
    ------------------------------------------------------------------------------------------------------------------*/
DayTableMixin.prototype.renderBgTrHtml=function(row){return"<tr>"+(this.isRTL?"":this.renderBgIntroHtml(row))+this.renderBgCellsHtml(row)+(this.isRTL?this.renderBgIntroHtml(row):"")+"</tr>"},DayTableMixin.prototype.renderBgIntroHtml=function(row){return this.renderIntroHtml();// fall back to generic
},DayTableMixin.prototype.renderBgCellsHtml=function(row){var col,date,htmls=[];for(col=0;col<this.colCnt;col++)date=this.getCellDate(row,col),htmls.push(this.renderBgCellHtml(date));return htmls.join("")},DayTableMixin.prototype.renderBgCellHtml=function(date,otherAttrs){var t=this,view=t.view,isDateValid=t.dateProfile.activeUnzonedRange.containsDate(date),classes=t.getDayClasses(date);return classes.unshift("fc-day",view.calendar.theme.getClass("widgetContent")),'<td class="'+classes.join(" ")+'"'+(isDateValid?' data-date="'+date.format("YYYY-MM-DD")+'"':// if date has a time, won't format it
"")+(otherAttrs?" "+otherAttrs:"")+"></td>"},
/* Generic
    ------------------------------------------------------------------------------------------------------------------*/
DayTableMixin.prototype.renderIntroHtml=function(){
// Generates the default HTML intro for any row. User classes should override
},
// TODO: a generic method for dealing with <tr>, RTL, intro
// when increment internalApiVersion
// wrapTr (scheduler)
/* Utils
    ------------------------------------------------------------------------------------------------------------------*/
// Applies the generic "intro" and "outro" HTML to the given cells.
// Intro means the leftmost cell when the calendar is LTR and the rightmost cell when RTL. Vice-versa for outro.
DayTableMixin.prototype.bookendCells=function(trEl){var introHtml=this.renderIntroHtml();introHtml&&(this.isRTL?trEl.append(introHtml):trEl.prepend(introHtml))},DayTableMixin}(Mixin_1.default);exports.default=DayTableMixin},
/* 61 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var BusinessHourRenderer=/** @class */function(){
/*
    component implements:
      - eventRangesToEventFootprints
      - eventFootprintsToSegs
    */
function BusinessHourRenderer(component,fillRenderer){this.component=component,this.fillRenderer=fillRenderer}return BusinessHourRenderer.prototype.render=function(businessHourGenerator){var component=this.component,unzonedRange=component._getDateProfile().activeUnzonedRange,eventInstanceGroup=businessHourGenerator.buildEventInstanceGroup(component.hasAllDayBusinessHours,unzonedRange),eventFootprints=eventInstanceGroup?component.eventRangesToEventFootprints(eventInstanceGroup.sliceRenderRanges(unzonedRange)):[];this.renderEventFootprints(eventFootprints)},BusinessHourRenderer.prototype.renderEventFootprints=function(eventFootprints){var segs=this.component.eventFootprintsToSegs(eventFootprints);this.renderSegs(segs),this.segs=segs},BusinessHourRenderer.prototype.renderSegs=function(segs){this.fillRenderer&&this.fillRenderer.renderSegs("businessHours",segs,{getClasses:function(seg){return["fc-nonbusiness","fc-bgevent"]}})},BusinessHourRenderer.prototype.unrender=function(){this.fillRenderer&&this.fillRenderer.unrender("businessHours"),this.segs=null},BusinessHourRenderer.prototype.getSegs=function(){return this.segs||[]},BusinessHourRenderer}();exports.default=BusinessHourRenderer},
/* 62 */
/***/function(module,exports,__nested_webpack_require_286071__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_286071__(3),util_1=__nested_webpack_require_286071__(4),FillRenderer=/** @class */function(){function FillRenderer(component){this.fillSegTag="div",this.component=component,this.elsByFill={}}return FillRenderer.prototype.renderFootprint=function(type,componentFootprint,props){this.renderSegs(type,this.component.componentFootprintToSegs(componentFootprint),props)},FillRenderer.prototype.renderSegs=function(type,segs,props){var els;return segs=this.buildSegEls(type,segs,props),// assignes `.el` to each seg. returns successfully rendered segs
els=this.attachSegEls(type,segs),els&&this.reportEls(type,els),segs},
// Unrenders a specific type of fill that is currently rendered on the grid
FillRenderer.prototype.unrender=function(type){var el=this.elsByFill[type];el&&(el.remove(),delete this.elsByFill[type])},
// Renders and assigns an `el` property for each fill segment. Generic enough to work with different types.
// Only returns segments that successfully rendered.
FillRenderer.prototype.buildSegEls=function(type,segs,props){var i,_this=this,html="",renderedSegs=[];if(segs.length){
// build a large concatenation of segment HTML
for(i=0;i<segs.length;i++)html+=this.buildSegHtml(type,segs[i],props);
// Grab individual elements from the combined HTML string. Use each as the default rendering.
// Then, compute the 'el' for each segment.
$(html).each((function(i,node){var seg=segs[i],el=$(node);
// allow custom filter methods per-type
props.filterEl&&(el=props.filterEl(seg,el)),el&&(// custom filters did not cancel the render
el=$(el),// allow custom filter to return raw DOM node
// correct element type? (would be bad if a non-TD were inserted into a table for example)
el.is(_this.fillSegTag)&&(seg.el=el,renderedSegs.push(seg)))}))}return renderedSegs},
// Builds the HTML needed for one fill segment. Generic enough to work with different types.
FillRenderer.prototype.buildSegHtml=function(type,seg,props){
// custom hooks per-type
var classes=props.getClasses?props.getClasses(seg):[],css=util_1.cssToStr(props.getCss?props.getCss(seg):{});return"<"+this.fillSegTag+(classes.length?' class="'+classes.join(" ")+'"':"")+(css?' style="'+css+'"':"")+" />"},
// Should return wrapping DOM structure
FillRenderer.prototype.attachSegEls=function(type,segs){
// subclasses must implement
},FillRenderer.prototype.reportEls=function(type,nodes){this.elsByFill[type]?this.elsByFill[type]=this.elsByFill[type].add(nodes):this.elsByFill[type]=$(nodes)},FillRenderer}();exports.default=FillRenderer},
/* 63 */
/***/function(module,exports,__nested_webpack_require_289870__){Object.defineProperty(exports,"__esModule",{value:!0});var SingleEventDef_1=__nested_webpack_require_289870__(9),EventFootprint_1=__nested_webpack_require_289870__(34),EventSource_1=__nested_webpack_require_289870__(6),HelperRenderer=/** @class */function(){function HelperRenderer(component,eventRenderer){this.view=component._getView(),this.component=component,this.eventRenderer=eventRenderer}return HelperRenderer.prototype.renderComponentFootprint=function(componentFootprint){this.renderEventFootprints([this.fabricateEventFootprint(componentFootprint)])},HelperRenderer.prototype.renderEventDraggingFootprints=function(eventFootprints,sourceSeg,isTouch){this.renderEventFootprints(eventFootprints,sourceSeg,"fc-dragging",isTouch?null:this.view.opt("dragOpacity"))},HelperRenderer.prototype.renderEventResizingFootprints=function(eventFootprints,sourceSeg,isTouch){this.renderEventFootprints(eventFootprints,sourceSeg,"fc-resizing")},HelperRenderer.prototype.renderEventFootprints=function(eventFootprints,sourceSeg,extraClassNames,opacity){var i,segs=this.component.eventFootprintsToSegs(eventFootprints),classNames="fc-helper "+(extraClassNames||"");for(
// assigns each seg's el and returns a subset of segs that were rendered
segs=this.eventRenderer.renderFgSegEls(segs),i=0;i<segs.length;i++)segs[i].el.addClass(classNames);if(null!=opacity)for(i=0;i<segs.length;i++)segs[i].el.css("opacity",opacity);this.helperEls=this.renderSegs(segs,sourceSeg)},
/*
    Must return all mock event elements
    */
HelperRenderer.prototype.renderSegs=function(segs,sourceSeg){
// Subclasses must implement
},HelperRenderer.prototype.unrender=function(){this.helperEls&&(this.helperEls.remove(),this.helperEls=null)},HelperRenderer.prototype.fabricateEventFootprint=function(componentFootprint){var dummyInstance,calendar=this.view.calendar,eventDateProfile=calendar.footprintToDateProfile(componentFootprint),dummyEvent=new SingleEventDef_1.default(new EventSource_1.default(calendar));return dummyEvent.dateProfile=eventDateProfile,dummyInstance=dummyEvent.buildInstance(),new EventFootprint_1.default(componentFootprint,dummyEvent,dummyInstance)},HelperRenderer}();exports.default=HelperRenderer},
/* 64 */
/***/function(module,exports,__nested_webpack_require_292763__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_292763__(2),GlobalEmitter_1=__nested_webpack_require_292763__(23),Interaction_1=__nested_webpack_require_292763__(14),EventPointing=/** @class */function(_super){function EventPointing(){return null!==_super&&_super.apply(this,arguments)||this}
/*
    component must implement:
      - publiclyTrigger
    */return tslib_1.__extends(EventPointing,_super),EventPointing.prototype.bindToEl=function(el){var component=this.component;component.bindSegHandlerToEl(el,"click",this.handleClick.bind(this)),component.bindSegHandlerToEl(el,"mouseenter",this.handleMouseover.bind(this)),component.bindSegHandlerToEl(el,"mouseleave",this.handleMouseout.bind(this))},EventPointing.prototype.handleClick=function(seg,ev){var res=this.component.publiclyTrigger("eventClick",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,this.view]});!1===res&&ev.preventDefault()},
// Updates internal state and triggers handlers for when an event element is moused over
EventPointing.prototype.handleMouseover=function(seg,ev){GlobalEmitter_1.default.get().shouldIgnoreMouse()||this.mousedOverSeg||(this.mousedOverSeg=seg,
// TODO: move to EventSelecting's responsibility
this.view.isEventDefResizable(seg.footprint.eventDef)&&seg.el.addClass("fc-allow-mouse-resize"),this.component.publiclyTrigger("eventMouseover",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,this.view]}))},
// Updates internal state and triggers handlers for when an event element is moused out.
// Can be given no arguments, in which case it will mouseout the segment that was previously moused over.
EventPointing.prototype.handleMouseout=function(seg,ev){this.mousedOverSeg&&(this.mousedOverSeg=null,
// TODO: move to EventSelecting's responsibility
this.view.isEventDefResizable(seg.footprint.eventDef)&&seg.el.removeClass("fc-allow-mouse-resize"),this.component.publiclyTrigger("eventMouseout",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev||{},this.view]}))},EventPointing.prototype.end=function(){this.mousedOverSeg&&this.handleMouseout(this.mousedOverSeg)},EventPointing}(Interaction_1.default);exports.default=EventPointing},
/* 65 */
/***/function(module,exports,__nested_webpack_require_295840__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_295840__(2),Mixin_1=__nested_webpack_require_295840__(15),DateClicking_1=__nested_webpack_require_295840__(237),DateSelecting_1=__nested_webpack_require_295840__(236),EventPointing_1=__nested_webpack_require_295840__(64),EventDragging_1=__nested_webpack_require_295840__(235),EventResizing_1=__nested_webpack_require_295840__(234),ExternalDropping_1=__nested_webpack_require_295840__(233),StandardInteractionsMixin=/** @class */function(_super){function StandardInteractionsMixin(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(StandardInteractionsMixin,_super),StandardInteractionsMixin}(Mixin_1.default);exports.default=StandardInteractionsMixin,StandardInteractionsMixin.prototype.dateClickingClass=DateClicking_1.default,StandardInteractionsMixin.prototype.dateSelectingClass=DateSelecting_1.default,StandardInteractionsMixin.prototype.eventPointingClass=EventPointing_1.default,StandardInteractionsMixin.prototype.eventDraggingClass=EventDragging_1.default,StandardInteractionsMixin.prototype.eventResizingClass=EventResizing_1.default,StandardInteractionsMixin.prototype.externalDroppingClass=ExternalDropping_1.default},
/* 66 */
/***/function(module,exports,__nested_webpack_require_297217__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_297217__(2),$=__nested_webpack_require_297217__(3),util_1=__nested_webpack_require_297217__(4),CoordCache_1=__nested_webpack_require_297217__(58),Popover_1=__nested_webpack_require_297217__(227),UnzonedRange_1=__nested_webpack_require_297217__(5),ComponentFootprint_1=__nested_webpack_require_297217__(12),EventFootprint_1=__nested_webpack_require_297217__(34),BusinessHourRenderer_1=__nested_webpack_require_297217__(61),StandardInteractionsMixin_1=__nested_webpack_require_297217__(65),InteractiveDateComponent_1=__nested_webpack_require_297217__(42),DayTableMixin_1=__nested_webpack_require_297217__(60),DayGridEventRenderer_1=__nested_webpack_require_297217__(243),DayGridHelperRenderer_1=__nested_webpack_require_297217__(244),DayGridFillRenderer_1=__nested_webpack_require_297217__(245),DayGrid=/** @class */function(_super){function DayGrid(view){var _this=_super.call(this,view)||this;return _this.cellWeekNumbersVisible=!1,// display week numbers in day cell?
_this.bottomCoordPadding=0,// hack for extending the hit area for the last row of the coordinate grid
// isRigid determines whether the individual rows should ignore the contents and be a constant height.
// Relies on the view's colCnt and rowCnt. In the future, this component should probably be self-sufficient.
_this.isRigid=!1,_this.hasAllDayBusinessHours=!0,_this}
// Slices up the given span (unzoned start/end with other misc data) into an array of segments
return tslib_1.__extends(DayGrid,_super),DayGrid.prototype.componentFootprintToSegs=function(componentFootprint){var i,seg,segs=this.sliceRangeByRow(componentFootprint.unzonedRange);for(i=0;i<segs.length;i++)seg=segs[i],this.isRTL?(seg.leftCol=this.daysPerRow-1-seg.lastRowDayIndex,seg.rightCol=this.daysPerRow-1-seg.firstRowDayIndex):(seg.leftCol=seg.firstRowDayIndex,seg.rightCol=seg.lastRowDayIndex);return segs},
/* Date Rendering
    ------------------------------------------------------------------------------------------------------------------*/
DayGrid.prototype.renderDates=function(dateProfile){this.dateProfile=dateProfile,this.updateDayTable(),this.renderGrid()},DayGrid.prototype.unrenderDates=function(){this.removeSegPopover()},
// Renders the rows and columns into the component's `this.el`, which should already be assigned.
DayGrid.prototype.renderGrid=function(){var row,col,view=this.view,rowCnt=this.rowCnt,colCnt=this.colCnt,html="";for(this.headContainerEl&&this.headContainerEl.html(this.renderHeadHtml()),row=0;row<rowCnt;row++)html+=this.renderDayRowHtml(row,this.isRigid);
// trigger dayRender with each cell's element
for(this.el.html(html),this.rowEls=this.el.find(".fc-row"),this.cellEls=this.el.find(".fc-day, .fc-disabled-day"),this.rowCoordCache=new CoordCache_1.default({els:this.rowEls,isVertical:!0}),this.colCoordCache=new CoordCache_1.default({els:this.cellEls.slice(0,this.colCnt),isHorizontal:!0}),row=0;row<rowCnt;row++)for(col=0;col<colCnt;col++)this.publiclyTrigger("dayRender",{context:view,args:[this.getCellDate(row,col),this.getCellEl(row,col),view]})},
// Generates the HTML for a single row, which is a div that wraps a table.
// `row` is the row number.
DayGrid.prototype.renderDayRowHtml=function(row,isRigid){var theme=this.view.calendar.theme,classes=["fc-row","fc-week",theme.getClass("dayRow")];return isRigid&&classes.push("fc-rigid"),'<div class="'+classes.join(" ")+'"><div class="fc-bg"><table class="'+theme.getClass("tableGrid")+'">'+this.renderBgTrHtml(row)+'</table></div><div class="fc-content-skeleton"><table>'+(this.getIsNumbersVisible()?"<thead>"+this.renderNumberTrHtml(row)+"</thead>":"")+"</table></div></div>"},DayGrid.prototype.getIsNumbersVisible=function(){return this.getIsDayNumbersVisible()||this.cellWeekNumbersVisible},DayGrid.prototype.getIsDayNumbersVisible=function(){return this.rowCnt>1},
/* Grid Number Rendering
    ------------------------------------------------------------------------------------------------------------------*/
DayGrid.prototype.renderNumberTrHtml=function(row){return"<tr>"+(this.isRTL?"":this.renderNumberIntroHtml(row))+this.renderNumberCellsHtml(row)+(this.isRTL?this.renderNumberIntroHtml(row):"")+"</tr>"},DayGrid.prototype.renderNumberIntroHtml=function(row){return this.renderIntroHtml()},DayGrid.prototype.renderNumberCellsHtml=function(row){var col,date,htmls=[];for(col=0;col<this.colCnt;col++)date=this.getCellDate(row,col),htmls.push(this.renderNumberCellHtml(date));return htmls.join("")},
// Generates the HTML for the <td>s of the "number" row in the DayGrid's content skeleton.
// The number row will only exist if either day numbers or week numbers are turned on.
DayGrid.prototype.renderNumberCellHtml=function(date){var classes,weekCalcFirstDoW,view=this.view,html="",isDateValid=this.dateProfile.activeUnzonedRange.containsDate(date),isDayNumberVisible=this.getIsDayNumbersVisible()&&isDateValid;return isDayNumberVisible||this.cellWeekNumbersVisible?(classes=this.getDayClasses(date),classes.unshift("fc-day-top"),this.cellWeekNumbersVisible&&(
// To determine the day of week number change under ISO, we cannot
// rely on moment.js methods such as firstDayOfWeek() or weekday(),
// because they rely on the locale's dow (possibly overridden by
// our firstDay option), which may not be Monday. We cannot change
// dow, because that would affect the calendar start day as well.
weekCalcFirstDoW="ISO"===date._locale._fullCalendar_weekCalc?1:date._locale.firstDayOfWeek()),html+='<td class="'+classes.join(" ")+'"'+(isDateValid?' data-date="'+date.format()+'"':"")+">",this.cellWeekNumbersVisible&&date.day()===weekCalcFirstDoW&&(html+=view.buildGotoAnchorHtml({date:date,type:"week"},{class:"fc-week-number"},date.format("w"))),isDayNumberVisible&&(html+=view.buildGotoAnchorHtml(date,{class:"fc-day-number"},date.format("D"))),html+="</td>",html):"<td/>"},
/* Hit System
    ------------------------------------------------------------------------------------------------------------------*/
DayGrid.prototype.prepareHits=function(){this.colCoordCache.build(),this.rowCoordCache.build(),this.rowCoordCache.bottoms[this.rowCnt-1]+=this.bottomCoordPadding},DayGrid.prototype.releaseHits=function(){this.colCoordCache.clear(),this.rowCoordCache.clear()},DayGrid.prototype.queryHit=function(leftOffset,topOffset){if(this.colCoordCache.isLeftInBounds(leftOffset)&&this.rowCoordCache.isTopInBounds(topOffset)){var col=this.colCoordCache.getHorizontalIndex(leftOffset),row=this.rowCoordCache.getVerticalIndex(topOffset);if(null!=row&&null!=col)return this.getCellHit(row,col)}},DayGrid.prototype.getHitFootprint=function(hit){var range=this.getCellRange(hit.row,hit.col);return new ComponentFootprint_1.default(new UnzonedRange_1.default(range.start,range.end),!0)},DayGrid.prototype.getHitEl=function(hit){return this.getCellEl(hit.row,hit.col)},
/* Cell System
    ------------------------------------------------------------------------------------------------------------------*/
// FYI: the first column is the leftmost column, regardless of date
DayGrid.prototype.getCellHit=function(row,col){return{row:row,col:col,component:this,left:this.colCoordCache.getLeftOffset(col),right:this.colCoordCache.getRightOffset(col),top:this.rowCoordCache.getTopOffset(row),bottom:this.rowCoordCache.getBottomOffset(row)}},DayGrid.prototype.getCellEl=function(row,col){return this.cellEls.eq(row*this.colCnt+col)},
/* Event Rendering
    ------------------------------------------------------------------------------------------------------------------*/
// Unrenders all events currently rendered on the grid
DayGrid.prototype.executeEventUnrender=function(){this.removeSegPopover(),// removes the "more.." events popover
_super.prototype.executeEventUnrender.call(this)},
// Retrieves all rendered segment objects currently rendered on the grid
DayGrid.prototype.getOwnEventSegs=function(){
// append the segments from the "more..." popover
return _super.prototype.getOwnEventSegs.call(this).concat(this.popoverSegs||[])},
/* Event Drag Visualization
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of an event or external element being dragged.
// `eventLocation` has zoned start and end (optional)
DayGrid.prototype.renderDrag=function(eventFootprints,seg,isTouch){var i;for(i=0;i<eventFootprints.length;i++)this.renderHighlight(eventFootprints[i].componentFootprint);
// render drags from OTHER components as helpers
if(eventFootprints.length&&seg&&seg.component!==this)return this.helperRenderer.renderEventDraggingFootprints(eventFootprints,seg,isTouch),!0;// signal helpers rendered
},
// Unrenders any visual indication of a hovering event
DayGrid.prototype.unrenderDrag=function(){this.unrenderHighlight(),this.helperRenderer.unrender()},
/* Event Resize Visualization
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of an event being resized
DayGrid.prototype.renderEventResize=function(eventFootprints,seg,isTouch){var i;for(i=0;i<eventFootprints.length;i++)this.renderHighlight(eventFootprints[i].componentFootprint);this.helperRenderer.renderEventResizingFootprints(eventFootprints,seg,isTouch)},
// Unrenders a visual indication of an event being resized
DayGrid.prototype.unrenderEventResize=function(){this.unrenderHighlight(),this.helperRenderer.unrender()},
/* More+ Link Popover
    ------------------------------------------------------------------------------------------------------------------*/
DayGrid.prototype.removeSegPopover=function(){this.segPopover&&this.segPopover.hide()},
// Limits the number of "levels" (vertically stacking layers of events) for each row of the grid.
// `levelLimit` can be false (don't limit), a number, or true (should be computed).
DayGrid.prototype.limitRows=function(levelLimit){var row,rowLevelLimit,rowStructs=this.eventRenderer.rowStructs||[];for(row=0;row<rowStructs.length;row++)this.unlimitRow(row),rowLevelLimit=!!levelLimit&&("number"===typeof levelLimit?levelLimit:this.computeRowLevelLimit(row)),!1!==rowLevelLimit&&this.limitRow(row,rowLevelLimit)},
// Computes the number of levels a row will accomodate without going outside its bounds.
// Assumes the row is "rigid" (maintains a constant height regardless of what is inside).
// `row` is the row number.
DayGrid.prototype.computeRowLevelLimit=function(row){var i,trEl,trHeight,rowEl=this.rowEls.eq(row),rowHeight=rowEl.height(),trEls=this.eventRenderer.rowStructs[row].tbodyEl.children();// the containing "fake" row div
function iterInnerHeights(i,childNode){trHeight=Math.max(trHeight,$(childNode).outerHeight())}
// Reveal one level <tr> at a time and stop when we find one out of bounds
for(i=0;i<trEls.length;i++)if(trEl=trEls.eq(i).removeClass("fc-limited"),// reset to original state (reveal)
// with rowspans>1 and IE8, trEl.outerHeight() would return the height of the largest cell,
// so instead, find the tallest inner content element.
trHeight=0,trEl.find("> td > :first-child").each(iterInnerHeights),trEl.position().top+trHeight>rowHeight)return i;return!1;// should not limit at all
},
// Limits the given grid row to the maximum number of levels and injects "more" links if necessary.
// `row` is the row number.
// `levelLimit` is a number for the maximum (inclusive) number of levels allowed.
DayGrid.prototype.limitRow=function(row,levelLimit){var levelSegs,cellMatrix,limitedNodes,i,seg,segsBelow,totalSegsBelow,colSegsBelow,td,rowspan,segMoreNodes,j,moreTd,moreWrap,moreLink,_this=this,rowStruct=this.eventRenderer.rowStructs[row],moreNodes=[],col=0,emptyCellsUntil=function(endCol){while(col<endCol)segsBelow=_this.getCellSegs(row,col,levelLimit),segsBelow.length&&(td=cellMatrix[levelLimit-1][col],moreLink=_this.renderMoreLink(row,col,segsBelow),moreWrap=$("<div/>").append(moreLink),td.append(moreWrap),moreNodes.push(moreWrap[0])),col++};if(levelLimit&&levelLimit<rowStruct.segLevels.length){// hide elements and get a simple DOM-nodes array
// iterate though segments in the last allowable level
for(// is it actually over the limit?
levelSegs=rowStruct.segLevels[levelLimit-1],cellMatrix=rowStruct.cellMatrix,limitedNodes=rowStruct.tbodyEl.children().slice(levelLimit).addClass("fc-limited").get(),i=0;i<levelSegs.length;i++){seg=levelSegs[i],emptyCellsUntil(seg.leftCol),// process empty cells before the segment
// determine *all* segments below `seg` that occupy the same columns
colSegsBelow=[],totalSegsBelow=0;while(col<=seg.rightCol)segsBelow=this.getCellSegs(row,col,levelLimit),colSegsBelow.push(segsBelow),totalSegsBelow+=segsBelow.length,col++;if(totalSegsBelow){
// make a replacement <td> for each column the segment occupies. will be one for each colspan
for(// do we need to replace this segment with one or many "more" links?
td=cellMatrix[levelLimit-1][seg.leftCol],// the segment's parent cell
rowspan=td.attr("rowspan")||1,segMoreNodes=[],j=0;j<colSegsBelow.length;j++)moreTd=$('<td class="fc-more-cell"/>').attr("rowspan",rowspan),segsBelow=colSegsBelow[j],moreLink=this.renderMoreLink(row,seg.leftCol+j,[seg].concat(segsBelow)),moreWrap=$("<div/>").append(moreLink),moreTd.append(moreWrap),segMoreNodes.push(moreTd[0]),moreNodes.push(moreTd[0]);td.addClass("fc-limited").after($(segMoreNodes)),// hide original <td> and inject replacements
limitedNodes.push(td[0])}}emptyCellsUntil(this.colCnt),// finish off the level
rowStruct.moreEls=$(moreNodes),// for easy undoing later
rowStruct.limitedEls=$(limitedNodes)}},
// Reveals all levels and removes all "more"-related elements for a grid's row.
// `row` is a row number.
DayGrid.prototype.unlimitRow=function(row){var rowStruct=this.eventRenderer.rowStructs[row];rowStruct.moreEls&&(rowStruct.moreEls.remove(),rowStruct.moreEls=null),rowStruct.limitedEls&&(rowStruct.limitedEls.removeClass("fc-limited"),rowStruct.limitedEls=null)},
// Renders an <a> element that represents hidden event element for a cell.
// Responsible for attaching click handler as well.
DayGrid.prototype.renderMoreLink=function(row,col,hiddenSegs){var _this=this,view=this.view;return $('<a class="fc-more"/>').text(this.getMoreLinkText(hiddenSegs.length)).on("click",(function(ev){var clickOption=_this.opt("eventLimitClick"),date=_this.getCellDate(row,col),moreEl=$(ev.currentTarget),dayEl=_this.getCellEl(row,col),allSegs=_this.getCellSegs(row,col),reslicedAllSegs=_this.resliceDaySegs(allSegs,date),reslicedHiddenSegs=_this.resliceDaySegs(hiddenSegs,date);"function"===typeof clickOption&&(
// the returned value can be an atomic option
clickOption=_this.publiclyTrigger("eventLimitClick",{context:view,args:[{date:date.clone(),dayEl:dayEl,moreEl:moreEl,segs:reslicedAllSegs,hiddenSegs:reslicedHiddenSegs},ev,view]})),"popover"===clickOption?_this.showSegPopover(row,col,moreEl,reslicedAllSegs):"string"===typeof clickOption&&// a view name
view.calendar.zoomTo(date,clickOption)}))},
// Reveals the popover that displays all events within a cell
DayGrid.prototype.showSegPopover=function(row,col,moreLink,segs){var topEl,options,_this=this,view=this.view,moreWrap=moreLink.parent();topEl=1===this.rowCnt?view.el:this.rowEls.eq(row),options={className:"fc-more-popover "+view.calendar.theme.getClass("popover"),content:this.renderSegPopoverContent(row,col,segs),parentEl:view.el,top:topEl.offset().top,autoHide:!0,viewportConstrain:this.opt("popoverViewportConstrain"),hide:function(){
// kill everything when the popover is hidden
// notify events to be removed
_this.popoverSegs&&_this.triggerBeforeEventSegsDestroyed(_this.popoverSegs),_this.segPopover.removeElement(),_this.segPopover=null,_this.popoverSegs=null}},
// Determine horizontal coordinate.
// We use the moreWrap instead of the <td> to avoid border confusion.
this.isRTL?options.right=moreWrap.offset().left+moreWrap.outerWidth()+1:options.left=moreWrap.offset().left-1,this.segPopover=new Popover_1.default(options),this.segPopover.show(),
// the popover doesn't live within the grid's container element, and thus won't get the event
// delegated-handlers for free. attach event-related handlers to the popover.
this.bindAllSegHandlersToEl(this.segPopover.el),this.triggerAfterEventSegsRendered(segs)},
// Builds the inner DOM contents of the segment popover
DayGrid.prototype.renderSegPopoverContent=function(row,col,segs){var i,view=this.view,theme=view.calendar.theme,title=this.getCellDate(row,col).format(this.opt("dayPopoverFormat")),content=$('<div class="fc-header '+theme.getClass("popoverHeader")+'"><span class="fc-close '+theme.getIconClass("close")+'"></span><span class="fc-title">'+util_1.htmlEscape(title)+'</span><div class="fc-clear"/></div><div class="fc-body '+theme.getClass("popoverContent")+'"><div class="fc-event-container"></div></div>'),segContainer=content.find(".fc-event-container");for(
// render each seg's `el` and only return the visible segs
segs=this.eventRenderer.renderFgSegEls(segs,!0),// disableResizing=true
this.popoverSegs=segs,i=0;i<segs.length;i++)
// because segments in the popover are not part of a grid coordinate system, provide a hint to any
// grids that want to do drag-n-drop about which cell it came from
this.hitsNeeded(),segs[i].hit=this.getCellHit(row,col),this.hitsNotNeeded(),segContainer.append(segs[i].el);return content},
// Given the events within an array of segment objects, reslice them to be in a single day
DayGrid.prototype.resliceDaySegs=function(segs,dayDate){var i,seg,slicedRange,dayStart=dayDate.clone(),dayEnd=dayStart.clone().add(1,"days"),dayRange=new UnzonedRange_1.default(dayStart,dayEnd),newSegs=[];for(i=0;i<segs.length;i++)seg=segs[i],slicedRange=seg.footprint.componentFootprint.unzonedRange.intersect(dayRange),slicedRange&&newSegs.push($.extend({},seg,{footprint:new EventFootprint_1.default(new ComponentFootprint_1.default(slicedRange,seg.footprint.componentFootprint.isAllDay),seg.footprint.eventDef,seg.footprint.eventInstance),isStart:seg.isStart&&slicedRange.isStart,isEnd:seg.isEnd&&slicedRange.isEnd}));
// force an order because eventsToSegs doesn't guarantee one
// TODO: research if still needed
return this.eventRenderer.sortEventSegs(newSegs),newSegs},
// Generates the text that should be inside a "more" link, given the number of events it represents
DayGrid.prototype.getMoreLinkText=function(num){var opt=this.opt("eventLimitText");return"function"===typeof opt?opt(num):"+"+num+" "+opt},
// Returns segments within a given cell.
// If `startLevel` is specified, returns only events including and below that level. Otherwise returns all segs.
DayGrid.prototype.getCellSegs=function(row,col,startLevel){var seg,segMatrix=this.eventRenderer.rowStructs[row].segMatrix,level=startLevel||0,segs=[];while(level<segMatrix.length)seg=segMatrix[level][col],seg&&segs.push(seg),level++;return segs},DayGrid}(InteractiveDateComponent_1.default);exports.default=DayGrid,DayGrid.prototype.eventRendererClass=DayGridEventRenderer_1.default,DayGrid.prototype.businessHourRendererClass=BusinessHourRenderer_1.default,DayGrid.prototype.helperRendererClass=DayGridHelperRenderer_1.default,DayGrid.prototype.fillRendererClass=DayGridFillRenderer_1.default,StandardInteractionsMixin_1.default.mixInto(DayGrid),DayTableMixin_1.default.mixInto(DayGrid)},
/* 67 */
/***/function(module,exports,__nested_webpack_require_326615__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_326615__(2),$=__nested_webpack_require_326615__(3),util_1=__nested_webpack_require_326615__(4),Scroller_1=__nested_webpack_require_326615__(41),View_1=__nested_webpack_require_326615__(43),BasicViewDateProfileGenerator_1=__nested_webpack_require_326615__(68),DayGrid_1=__nested_webpack_require_326615__(66),BasicView=/** @class */function(_super){function BasicView(calendar,viewSpec){var _this=_super.call(this,calendar,viewSpec)||this;return _this.dayGrid=_this.instantiateDayGrid(),_this.dayGrid.isRigid=_this.hasRigidRows(),_this.opt("weekNumbers")&&(_this.opt("weekNumbersWithinDays")?(_this.dayGrid.cellWeekNumbersVisible=!0,_this.dayGrid.colWeekNumbersVisible=!1):(_this.dayGrid.cellWeekNumbersVisible=!1,_this.dayGrid.colWeekNumbersVisible=!0)),_this.addChild(_this.dayGrid),_this.scroller=new Scroller_1.default({overflowX:"hidden",overflowY:"auto"}),_this}
// Generates the DayGrid object this view needs. Draws from this.dayGridClass
return tslib_1.__extends(BasicView,_super),BasicView.prototype.instantiateDayGrid=function(){
// generate a subclass on the fly with BasicView-specific behavior
// TODO: cache this subclass
var subclass=makeDayGridSubclass(this.dayGridClass);return new subclass(this)},BasicView.prototype.executeDateRender=function(dateProfile){this.dayGrid.breakOnWeeks=/year|month|week/.test(dateProfile.currentRangeUnit),_super.prototype.executeDateRender.call(this,dateProfile)},BasicView.prototype.renderSkeleton=function(){var dayGridContainerEl,dayGridEl;this.el.addClass("fc-basic-view").html(this.renderSkeletonHtml()),this.scroller.render(),dayGridContainerEl=this.scroller.el.addClass("fc-day-grid-container"),dayGridEl=$('<div class="fc-day-grid" />').appendTo(dayGridContainerEl),this.el.find(".fc-body > tr > td").append(dayGridContainerEl),this.dayGrid.headContainerEl=this.el.find(".fc-head-container"),this.dayGrid.setElement(dayGridEl)},BasicView.prototype.unrenderSkeleton=function(){this.dayGrid.removeElement(),this.scroller.destroy()},
// Builds the HTML skeleton for the view.
// The day-grid component will render inside of a container defined by this HTML.
BasicView.prototype.renderSkeletonHtml=function(){var theme=this.calendar.theme;return'<table class="'+theme.getClass("tableGrid")+'">'+(this.opt("columnHeader")?'<thead class="fc-head"><tr><td class="fc-head-container '+theme.getClass("widgetHeader")+'">&nbsp;</td></tr></thead>':"")+'<tbody class="fc-body"><tr><td class="'+theme.getClass("widgetContent")+'"></td></tr></tbody></table>'},
// Generates an HTML attribute string for setting the width of the week number column, if it is known
BasicView.prototype.weekNumberStyleAttr=function(){return null!=this.weekNumberWidth?'style="width:'+this.weekNumberWidth+'px"':""},
// Determines whether each row should have a constant height
BasicView.prototype.hasRigidRows=function(){var eventLimit=this.opt("eventLimit");return eventLimit&&"number"!==typeof eventLimit},
/* Dimensions
    ------------------------------------------------------------------------------------------------------------------*/
// Refreshes the horizontal dimensions of the view
BasicView.prototype.updateSize=function(totalHeight,isAuto,isResize){var scrollerHeight,scrollbarWidths,eventLimit=this.opt("eventLimit"),headRowEl=this.dayGrid.headContainerEl.find(".fc-row");
// hack to give the view some height prior to dayGrid's columns being rendered
// TODO: separate setting height from scroller VS dayGrid.
this.dayGrid.rowEls?(_super.prototype.updateSize.call(this,totalHeight,isAuto,isResize),this.dayGrid.colWeekNumbersVisible&&(
// Make sure all week number cells running down the side have the same width.
// Record the width for cells created later.
this.weekNumberWidth=util_1.matchCellWidths(this.el.find(".fc-week-number"))),
// reset all heights to be natural
this.scroller.clear(),util_1.uncompensateScroll(headRowEl),this.dayGrid.removeSegPopover(),// kill the "more" popover if displayed
// is the event limit a constant level number?
eventLimit&&"number"===typeof eventLimit&&this.dayGrid.limitRows(eventLimit),
// distribute the height to the rows
// (totalHeight is a "recommended" value if isAuto)
scrollerHeight=this.computeScrollerHeight(totalHeight),this.setGridHeight(scrollerHeight,isAuto),
// is the event limit dynamically calculated?
eventLimit&&"number"!==typeof eventLimit&&this.dayGrid.limitRows(eventLimit),isAuto||(// should we force dimensions of the scroll container?
this.scroller.setHeight(scrollerHeight),scrollbarWidths=this.scroller.getScrollbarWidths(),(scrollbarWidths.left||scrollbarWidths.right)&&(// using scrollbars?
util_1.compensateScroll(headRowEl,scrollbarWidths),
// doing the scrollbar compensation might have created text overflow which created more height. redo
scrollerHeight=this.computeScrollerHeight(totalHeight),this.scroller.setHeight(scrollerHeight)),
// guarantees the same scrollbar widths
this.scroller.lockOverflow(scrollbarWidths))):isAuto||(scrollerHeight=this.computeScrollerHeight(totalHeight),this.scroller.setHeight(scrollerHeight))},
// given a desired total height of the view, returns what the height of the scroller should be
BasicView.prototype.computeScrollerHeight=function(totalHeight){return totalHeight-util_1.subtractInnerElHeight(this.el,this.scroller.el);// everything that's NOT the scroller
},
// Sets the height of just the DayGrid component in this view
BasicView.prototype.setGridHeight=function(height,isAuto){isAuto?util_1.undistributeHeight(this.dayGrid.rowEls):util_1.distributeHeight(this.dayGrid.rowEls,height,!0)},
/* Scroll
    ------------------------------------------------------------------------------------------------------------------*/
BasicView.prototype.computeInitialDateScroll=function(){return{top:0}},BasicView.prototype.queryDateScroll=function(){return{top:this.scroller.getScrollTop()}},BasicView.prototype.applyDateScroll=function(scroll){void 0!==scroll.top&&this.scroller.setScrollTop(scroll.top)},BasicView}(View_1.default);
// customize the rendering behavior of BasicView's dayGrid
function makeDayGridSubclass(SuperClass){/** @class */
return function(_super){function SubClass(){var _this=null!==_super&&_super.apply(this,arguments)||this;// display week numbers along the side?
return _this.colWeekNumbersVisible=!1,_this}
// Generates the HTML that will go before the day-of week header cells
return tslib_1.__extends(SubClass,_super),SubClass.prototype.renderHeadIntroHtml=function(){var view=this.view;return this.colWeekNumbersVisible?'<th class="fc-week-number '+view.calendar.theme.getClass("widgetHeader")+'" '+view.weekNumberStyleAttr()+"><span>"+// needed for matchCellWidths
util_1.htmlEscape(this.opt("weekNumberTitle"))+"</span></th>":""},
// Generates the HTML that will go before content-skeleton cells that display the day/week numbers
SubClass.prototype.renderNumberIntroHtml=function(row){var view=this.view,weekStart=this.getCellDate(row,0);return this.colWeekNumbersVisible?'<td class="fc-week-number" '+view.weekNumberStyleAttr()+">"+view.buildGotoAnchorHtml(// aside from link, important for matchCellWidths
{date:weekStart,type:"week",forceOff:1===this.colCnt},weekStart.format("w"))+"</td>":""},
// Generates the HTML that goes before the day bg cells for each day-row
SubClass.prototype.renderBgIntroHtml=function(){var view=this.view;return this.colWeekNumbersVisible?'<td class="fc-week-number '+view.calendar.theme.getClass("widgetContent")+'" '+view.weekNumberStyleAttr()+"></td>":""},
// Generates the HTML that goes before every other type of row generated by DayGrid.
// Affects helper-skeleton and highlight-skeleton rows.
SubClass.prototype.renderIntroHtml=function(){var view=this.view;return this.colWeekNumbersVisible?'<td class="fc-week-number" '+view.weekNumberStyleAttr()+"></td>":""},SubClass.prototype.getIsNumbersVisible=function(){return DayGrid_1.default.prototype.getIsNumbersVisible.apply(this,arguments)||this.colWeekNumbersVisible},SubClass}(SuperClass)}
/***/exports.default=BasicView,BasicView.prototype.dateProfileGeneratorClass=BasicViewDateProfileGenerator_1.default,BasicView.prototype.dayGridClass=DayGrid_1.default},
/* 68 */
/***/function(module,exports,__nested_webpack_require_338487__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_338487__(2),UnzonedRange_1=__nested_webpack_require_338487__(5),DateProfileGenerator_1=__nested_webpack_require_338487__(55),BasicViewDateProfileGenerator=/** @class */function(_super){function BasicViewDateProfileGenerator(){return null!==_super&&_super.apply(this,arguments)||this}
// Computes the date range that will be rendered.
return tslib_1.__extends(BasicViewDateProfileGenerator,_super),BasicViewDateProfileGenerator.prototype.buildRenderRange=function(currentUnzonedRange,currentRangeUnit,isRangeAllDay){var renderUnzonedRange=_super.prototype.buildRenderRange.call(this,currentUnzonedRange,currentRangeUnit,isRangeAllDay),start=this.msToUtcMoment(renderUnzonedRange.startMs,isRangeAllDay),end=this.msToUtcMoment(renderUnzonedRange.endMs,isRangeAllDay);// an UnzonedRange
// year and month views should be aligned with weeks. this is already done for week
return/^(year|month)$/.test(currentRangeUnit)&&(start.startOf("week"),
// make end-of-week if not already
end.weekday()&&end.add(1,"week").startOf("week")),new UnzonedRange_1.default(start,end)},BasicViewDateProfileGenerator}(DateProfileGenerator_1.default);exports.default=BasicViewDateProfileGenerator},
/* 69 */
/* 70 */,
/* 71 */,
/* 72 */,
/* 73 */,
/* 74 */,
/* 75 */,
/* 76 */,
/* 77 */,
/* 78 */,
/* 79 */,
/* 80 */,
/* 81 */,
/* 82 */,
/* 83 */,
/* 84 */,
/* 85 */,
/* 86 */,
/* 87 */,
/* 88 */,
/* 89 */,
/* 90 */,
/* 91 */,
/* 92 */,
/* 93 */,
/* 94 */,
/* 95 */,
/* 96 */,
/* 97 */,
/* 98 */,
/* 99 */,
/* 100 */,
/* 101 */,
/* 102 */,
/* 103 */,
/* 104 */,
/* 105 */,
/* 106 */,
/* 107 */,
/* 108 */,
/* 109 */,
/* 110 */,
/* 111 */,
/* 112 */,
/* 113 */,
/* 114 */,
/* 115 */,
/* 116 */,
/* 117 */,
/* 118 */,
/* 119 */,
/* 120 */,
/* 121 */,
/* 122 */,
/* 123 */,
/* 124 */,
/* 125 */,
/* 126 */,
/* 127 */,
/* 128 */,
/* 129 */,
/* 130 */,
/* 131 */,
/* 132 */,
/* 133 */,
/* 134 */,
/* 135 */,
/* 136 */,
/* 137 */,
/* 138 */,
/* 139 */,
/* 140 */,
/* 141 */,
/* 142 */,
/* 143 */,
/* 144 */,
/* 145 */,
/* 146 */,
/* 147 */,
/* 148 */,
/* 149 */,
/* 150 */,
/* 151 */,
/* 152 */,
/* 153 */,
/* 154 */,
/* 155 */,
/* 156 */,
/* 157 */,
/* 158 */,
/* 159 */,
/* 160 */,
/* 161 */,
/* 162 */,
/* 163 */,
/* 164 */,
/* 165 */,
/* 166 */,
/* 167 */,
/* 168 */,
/* 169 */,
/* 170 */,
/* 171 */,
/* 172 */,
/* 173 */,
/* 174 */,
/* 175 */,
/* 176 */,
/* 177 */,
/* 178 */,
/* 179 */,
/* 180 */,
/* 181 */,
/* 182 */,
/* 183 */,
/* 184 */,
/* 185 */,
/* 186 */,
/* 187 */,
/* 188 */,
/* 189 */,
/* 190 */,
/* 191 */,
/* 192 */,
/* 193 */,
/* 194 */,
/* 195 */,
/* 196 */,
/* 197 */,
/* 198 */,
/* 199 */,
/* 200 */,
/* 201 */,
/* 202 */,
/* 203 */,
/* 204 */,
/* 205 */,
/* 206 */,
/* 207 */,
/* 208 */,
/* 209 */,
/* 210 */,
/* 211 */,
/* 212 */,
/* 213 */,
/* 214 */,
/* 215 */,
/* 216 */,
/* 217 */
/***/,function(module,exports,__nested_webpack_require_341703__){Object.defineProperty(exports,"__esModule",{value:!0});var UnzonedRange_1=__nested_webpack_require_341703__(5),ComponentFootprint_1=__nested_webpack_require_341703__(12),EventDefParser_1=__nested_webpack_require_341703__(36),EventSource_1=__nested_webpack_require_341703__(6),util_1=__nested_webpack_require_341703__(19),Constraints=/** @class */function(){function Constraints(eventManager,_calendar){this.eventManager=eventManager,this._calendar=_calendar}return Constraints.prototype.opt=function(name){return this._calendar.opt(name)},
/*
    determines if eventInstanceGroup is allowed,
    in relation to other EVENTS and business hours.
    */
Constraints.prototype.isEventInstanceGroupAllowed=function(eventInstanceGroup){var i,eventDef=eventInstanceGroup.getEventDef(),eventFootprints=this.eventRangesToEventFootprints(eventInstanceGroup.getAllEventRanges()),peerEventInstances=this.getPeerEventInstances(eventDef),peerEventRanges=peerEventInstances.map(util_1.eventInstanceToEventRange),peerEventFootprints=this.eventRangesToEventFootprints(peerEventRanges),constraintVal=eventDef.getConstraint(),overlapVal=eventDef.getOverlap(),eventAllowFunc=this.opt("eventAllow");for(i=0;i<eventFootprints.length;i++)if(!this.isFootprintAllowed(eventFootprints[i].componentFootprint,peerEventFootprints,constraintVal,overlapVal,eventFootprints[i].eventInstance))return!1;if(eventAllowFunc)for(i=0;i<eventFootprints.length;i++)if(!1===eventAllowFunc(eventFootprints[i].componentFootprint.toLegacy(this._calendar),eventFootprints[i].getEventLegacy()))return!1;return!0},Constraints.prototype.getPeerEventInstances=function(eventDef){return this.eventManager.getEventInstancesWithoutId(eventDef.id)},Constraints.prototype.isSelectionFootprintAllowed=function(componentFootprint){var selectAllowFunc,peerEventInstances=this.eventManager.getEventInstances(),peerEventRanges=peerEventInstances.map(util_1.eventInstanceToEventRange),peerEventFootprints=this.eventRangesToEventFootprints(peerEventRanges);return!!this.isFootprintAllowed(componentFootprint,peerEventFootprints,this.opt("selectConstraint"),this.opt("selectOverlap"))&&(selectAllowFunc=this.opt("selectAllow"),!selectAllowFunc||!1!==selectAllowFunc(componentFootprint.toLegacy(this._calendar)))},Constraints.prototype.isFootprintAllowed=function(componentFootprint,peerEventFootprints,constraintVal,overlapVal,subjectEventInstance){var constraintFootprints,overlapEventFootprints;// ComponentFootprint[]
// EventFootprint[]
if(null!=constraintVal&&(constraintFootprints=this.constraintValToFootprints(constraintVal,componentFootprint.isAllDay),!this.isFootprintWithinConstraints(componentFootprint,constraintFootprints)))return!1;if(overlapEventFootprints=this.collectOverlapEventFootprints(peerEventFootprints,componentFootprint),!1===overlapVal){if(overlapEventFootprints.length)return!1}else if("function"===typeof overlapVal&&!isOverlapsAllowedByFunc(overlapEventFootprints,overlapVal,subjectEventInstance))return!1;return!(subjectEventInstance&&!isOverlapEventInstancesAllowed(overlapEventFootprints,subjectEventInstance))},
// Constraint
// ------------------------------------------------------------------------------------------------
Constraints.prototype.isFootprintWithinConstraints=function(componentFootprint,constraintFootprints){var i;for(i=0;i<constraintFootprints.length;i++)if(this.footprintContainsFootprint(constraintFootprints[i],componentFootprint))return!0;return!1},Constraints.prototype.constraintValToFootprints=function(constraintVal,isAllDay){var eventInstances;return"businessHours"===constraintVal?this.buildCurrentBusinessFootprints(isAllDay):"object"===typeof constraintVal?(eventInstances=this.parseEventDefToInstances(constraintVal),// handles recurring events
eventInstances?this.eventInstancesToFootprints(eventInstances):this.parseFootprints(constraintVal)):null!=constraintVal?(// an ID
eventInstances=this.eventManager.getEventInstancesWithId(constraintVal),this.eventInstancesToFootprints(eventInstances)):void 0},
// returns ComponentFootprint[]
// uses current view's range
Constraints.prototype.buildCurrentBusinessFootprints=function(isAllDay){var view=this._calendar.view,businessHourGenerator=view.get("businessHourGenerator"),unzonedRange=view.dateProfile.activeUnzonedRange,eventInstanceGroup=businessHourGenerator.buildEventInstanceGroup(isAllDay,unzonedRange);return eventInstanceGroup?this.eventInstancesToFootprints(eventInstanceGroup.eventInstances):[]},
// conversion util
Constraints.prototype.eventInstancesToFootprints=function(eventInstances){var eventRanges=eventInstances.map(util_1.eventInstanceToEventRange),eventFootprints=this.eventRangesToEventFootprints(eventRanges);return eventFootprints.map(util_1.eventFootprintToComponentFootprint)},
// Overlap
// ------------------------------------------------------------------------------------------------
Constraints.prototype.collectOverlapEventFootprints=function(peerEventFootprints,targetFootprint){var i,overlapEventFootprints=[];for(i=0;i<peerEventFootprints.length;i++)this.footprintsIntersect(targetFootprint,peerEventFootprints[i].componentFootprint)&&overlapEventFootprints.push(peerEventFootprints[i]);return overlapEventFootprints},
// Conversion: eventDefs -> eventInstances -> eventRanges -> eventFootprints -> componentFootprints
// ------------------------------------------------------------------------------------------------
// NOTE: this might seem like repetitive code with the Grid class, however, this code is related to
// constraints whereas the Grid code is related to rendering. Each approach might want to convert
// eventRanges -> eventFootprints in a different way. Regardless, there are opportunities to make
// this more DRY.
/*
    Returns false on invalid input.
    */
Constraints.prototype.parseEventDefToInstances=function(eventInput){var eventManager=this.eventManager,eventDef=EventDefParser_1.default.parse(eventInput,new EventSource_1.default(this._calendar));return!!eventDef&&eventDef.buildInstances(eventManager.currentPeriod.unzonedRange)},Constraints.prototype.eventRangesToEventFootprints=function(eventRanges){var i,eventFootprints=[];for(i=0;i<eventRanges.length;i++)eventFootprints.push.apply(// footprints
eventFootprints,this.eventRangeToEventFootprints(eventRanges[i]));return eventFootprints},Constraints.prototype.eventRangeToEventFootprints=function(eventRange){return[util_1.eventRangeToEventFootprint(eventRange)]},
/*
    Parses footprints directly.
    Very similar to EventDateProfile::parse :(
    */
Constraints.prototype.parseFootprints=function(rawInput){var start,end;return rawInput.start&&(start=this._calendar.moment(rawInput.start),start.isValid()||(start=null)),rawInput.end&&(end=this._calendar.moment(rawInput.end),end.isValid()||(end=null)),[new ComponentFootprint_1.default(new UnzonedRange_1.default(start,end),start&&!start.hasTime()||end&&!end.hasTime())]},
// Footprint Utils
// ----------------------------------------------------------------------------------------
Constraints.prototype.footprintContainsFootprint=function(outerFootprint,innerFootprint){return outerFootprint.unzonedRange.containsRange(innerFootprint.unzonedRange)},Constraints.prototype.footprintsIntersect=function(footprint0,footprint1){return footprint0.unzonedRange.intersectsWith(footprint1.unzonedRange)},Constraints}();
// optional subjectEventInstance
function isOverlapsAllowedByFunc(overlapEventFootprints,overlapFunc,subjectEventInstance){var i;for(i=0;i<overlapEventFootprints.length;i++)if(!overlapFunc(overlapEventFootprints[i].eventInstance.toLegacy(),subjectEventInstance?subjectEventInstance.toLegacy():null))return!1;return!0}function isOverlapEventInstancesAllowed(overlapEventFootprints,subjectEventInstance){var i,overlapEventInstance,overlapEventDef,overlapVal,subjectLegacyInstance=subjectEventInstance.toLegacy();for(i=0;i<overlapEventFootprints.length;i++){if(overlapEventInstance=overlapEventFootprints[i].eventInstance,overlapEventDef=overlapEventInstance.def,
// don't need to pass in calendar, because don't want to consider global eventOverlap property,
// because we already considered that earlier in the process.
overlapVal=overlapEventDef.getOverlap(),!1===overlapVal)return!1;if("function"===typeof overlapVal&&!overlapVal(overlapEventInstance.toLegacy(),subjectLegacyInstance))return!1}return!0}
/***/exports.default=Constraints},
/* 218 */
/***/function(module,exports,__nested_webpack_require_353263__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_353263__(3),util_1=__nested_webpack_require_353263__(19),EventInstanceGroup_1=__nested_webpack_require_353263__(20),RecurringEventDef_1=__nested_webpack_require_353263__(54),EventSource_1=__nested_webpack_require_353263__(6),BUSINESS_HOUR_EVENT_DEFAULTS={start:"09:00",end:"17:00",dow:[1,2,3,4,5],rendering:"inverse-background"},BusinessHourGenerator=/** @class */function(){function BusinessHourGenerator(rawComplexDef,calendar){this.rawComplexDef=rawComplexDef,this.calendar=calendar}return BusinessHourGenerator.prototype.buildEventInstanceGroup=function(isAllDay,unzonedRange){var eventInstanceGroup,eventDefs=this.buildEventDefs(isAllDay);if(eventDefs.length)return eventInstanceGroup=new EventInstanceGroup_1.default(util_1.eventDefsToEventInstances(eventDefs,unzonedRange)),
// so that inverse-background rendering can happen even when no eventRanges in view
eventInstanceGroup.explicitEventDef=eventDefs[0],eventInstanceGroup},BusinessHourGenerator.prototype.buildEventDefs=function(isAllDay){var i,rawComplexDef=this.rawComplexDef,rawDefs=[],requireDow=!1,defs=[];for(!0===rawComplexDef?rawDefs=[{}]:$.isPlainObject(rawComplexDef)?rawDefs=[rawComplexDef]:$.isArray(rawComplexDef)&&(rawDefs=rawComplexDef,requireDow=!0),i=0;i<rawDefs.length;i++)requireDow&&!rawDefs[i].dow||defs.push(this.buildEventDef(isAllDay,rawDefs[i]));return defs},BusinessHourGenerator.prototype.buildEventDef=function(isAllDay,rawDef){var fullRawDef=$.extend({},BUSINESS_HOUR_EVENT_DEFAULTS,rawDef);return isAllDay&&(fullRawDef.start=null,fullRawDef.end=null),RecurringEventDef_1.default.parse(fullRawDef,new EventSource_1.default(this.calendar))},BusinessHourGenerator}();exports.default=BusinessHourGenerator},
/* 219 */
/***/function(module,exports,__nested_webpack_require_355955__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_355955__(3),util_1=__nested_webpack_require_355955__(4),Promise_1=__nested_webpack_require_355955__(21),EmitterMixin_1=__nested_webpack_require_355955__(13),UnzonedRange_1=__nested_webpack_require_355955__(5),EventInstanceGroup_1=__nested_webpack_require_355955__(20),EventPeriod=/** @class */function(){function EventPeriod(start,end,timezone){this.pendingCnt=0,this.freezeDepth=0,this.stuntedReleaseCnt=0,this.releaseCnt=0,this.start=start,this.end=end,this.timezone=timezone,this.unzonedRange=new UnzonedRange_1.default(start.clone().stripZone(),end.clone().stripZone()),this.requestsByUid={},this.eventDefsByUid={},this.eventDefsById={},this.eventInstanceGroupsById={}}return EventPeriod.prototype.isWithinRange=function(start,end){
// TODO: use a range util function?
return!start.isBefore(this.start)&&!end.isAfter(this.end)},
// Requesting and Purging
// -----------------------------------------------------------------------------------------------------------------
EventPeriod.prototype.requestSources=function(sources){this.freeze();for(var i=0;i<sources.length;i++)this.requestSource(sources[i]);this.thaw()},EventPeriod.prototype.requestSource=function(source){var _this=this,request={source:source,status:"pending",eventDefs:null};this.requestsByUid[source.uid]=request,this.pendingCnt+=1,source.fetch(this.start,this.end,this.timezone).then((function(eventDefs){"cancelled"!==request.status&&(request.status="completed",request.eventDefs=eventDefs,_this.addEventDefs(eventDefs),_this.pendingCnt--,_this.tryRelease())}),(function(){"cancelled"!==request.status&&(request.status="failed",_this.pendingCnt--,_this.tryRelease())}))},EventPeriod.prototype.purgeSource=function(source){var request=this.requestsByUid[source.uid];request&&(delete this.requestsByUid[source.uid],"pending"===request.status?(request.status="cancelled",this.pendingCnt--,this.tryRelease()):"completed"===request.status&&request.eventDefs.forEach(this.removeEventDef.bind(this)))},EventPeriod.prototype.purgeAllSources=function(){var uid,request,requestsByUid=this.requestsByUid,completedCnt=0;for(uid in requestsByUid)request=requestsByUid[uid],"pending"===request.status?request.status="cancelled":"completed"===request.status&&completedCnt++;this.requestsByUid={},this.pendingCnt=0,completedCnt&&this.removeAllEventDefs()},
// Event Definitions
// -----------------------------------------------------------------------------------------------------------------
EventPeriod.prototype.getEventDefByUid=function(eventDefUid){return this.eventDefsByUid[eventDefUid]},EventPeriod.prototype.getEventDefsById=function(eventDefId){var a=this.eventDefsById[eventDefId];return a?a.slice():[]},EventPeriod.prototype.addEventDefs=function(eventDefs){for(var i=0;i<eventDefs.length;i++)this.addEventDef(eventDefs[i])},EventPeriod.prototype.addEventDef=function(eventDef){var i,eventDefsById=this.eventDefsById,eventDefId=eventDef.id,eventDefs=eventDefsById[eventDefId]||(eventDefsById[eventDefId]=[]),eventInstances=eventDef.buildInstances(this.unzonedRange);for(eventDefs.push(eventDef),this.eventDefsByUid[eventDef.uid]=eventDef,i=0;i<eventInstances.length;i++)this.addEventInstance(eventInstances[i],eventDefId)},EventPeriod.prototype.removeEventDefsById=function(eventDefId){var _this=this;this.getEventDefsById(eventDefId).forEach((function(eventDef){_this.removeEventDef(eventDef)}))},EventPeriod.prototype.removeAllEventDefs=function(){var isEmpty=$.isEmptyObject(this.eventDefsByUid);this.eventDefsByUid={},this.eventDefsById={},this.eventInstanceGroupsById={},isEmpty||this.tryRelease()},EventPeriod.prototype.removeEventDef=function(eventDef){var eventDefsById=this.eventDefsById,eventDefs=eventDefsById[eventDef.id];delete this.eventDefsByUid[eventDef.uid],eventDefs&&(util_1.removeExact(eventDefs,eventDef),eventDefs.length||delete eventDefsById[eventDef.id],this.removeEventInstancesForDef(eventDef))},
// Event Instances
// -----------------------------------------------------------------------------------------------------------------
EventPeriod.prototype.getEventInstances=function(){var id,eventInstanceGroupsById=this.eventInstanceGroupsById,eventInstances=[];for(id in eventInstanceGroupsById)eventInstances.push.apply(eventInstances,// append
eventInstanceGroupsById[id].eventInstances);return eventInstances},EventPeriod.prototype.getEventInstancesWithId=function(eventDefId){var eventInstanceGroup=this.eventInstanceGroupsById[eventDefId];return eventInstanceGroup?eventInstanceGroup.eventInstances.slice():[]},EventPeriod.prototype.getEventInstancesWithoutId=function(eventDefId){var id,eventInstanceGroupsById=this.eventInstanceGroupsById,matchingInstances=[];for(id in eventInstanceGroupsById)id!==eventDefId&&matchingInstances.push.apply(matchingInstances,// append
eventInstanceGroupsById[id].eventInstances);return matchingInstances},EventPeriod.prototype.addEventInstance=function(eventInstance,eventDefId){var eventInstanceGroupsById=this.eventInstanceGroupsById,eventInstanceGroup=eventInstanceGroupsById[eventDefId]||(eventInstanceGroupsById[eventDefId]=new EventInstanceGroup_1.default);eventInstanceGroup.eventInstances.push(eventInstance),this.tryRelease()},EventPeriod.prototype.removeEventInstancesForDef=function(eventDef){var removeCnt,eventInstanceGroupsById=this.eventInstanceGroupsById,eventInstanceGroup=eventInstanceGroupsById[eventDef.id];eventInstanceGroup&&(removeCnt=util_1.removeMatching(eventInstanceGroup.eventInstances,(function(currentEventInstance){return currentEventInstance.def===eventDef})),eventInstanceGroup.eventInstances.length||delete eventInstanceGroupsById[eventDef.id],removeCnt&&this.tryRelease())},
// Releasing and Freezing
// -----------------------------------------------------------------------------------------------------------------
EventPeriod.prototype.tryRelease=function(){this.pendingCnt||(this.freezeDepth?this.stuntedReleaseCnt++:this.release())},EventPeriod.prototype.release=function(){this.releaseCnt++,this.trigger("release",this.eventInstanceGroupsById)},EventPeriod.prototype.whenReleased=function(){var _this=this;return this.releaseCnt?Promise_1.default.resolve(this.eventInstanceGroupsById):Promise_1.default.construct((function(onResolve){_this.one("release",onResolve)}))},EventPeriod.prototype.freeze=function(){this.freezeDepth++||(this.stuntedReleaseCnt=0)},EventPeriod.prototype.thaw=function(){--this.freezeDepth||!this.stuntedReleaseCnt||this.pendingCnt||this.release()},EventPeriod}();exports.default=EventPeriod,EmitterMixin_1.default.mixInto(EventPeriod)},
/* 220 */
/***/function(module,exports,__nested_webpack_require_365678__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_365678__(3),util_1=__nested_webpack_require_365678__(4),EventPeriod_1=__nested_webpack_require_365678__(219),ArrayEventSource_1=__nested_webpack_require_365678__(56),EventSource_1=__nested_webpack_require_365678__(6),EventSourceParser_1=__nested_webpack_require_365678__(38),SingleEventDef_1=__nested_webpack_require_365678__(9),EventInstanceGroup_1=__nested_webpack_require_365678__(20),EmitterMixin_1=__nested_webpack_require_365678__(13),ListenerMixin_1=__nested_webpack_require_365678__(7),EventManager=/** @class */function(){function EventManager(calendar){this.calendar=calendar,this.stickySource=new ArrayEventSource_1.default(calendar),this.otherSources=[]}return EventManager.prototype.requestEvents=function(start,end,timezone,force){return!force&&this.currentPeriod&&this.currentPeriod.isWithinRange(start,end)&&timezone===this.currentPeriod.timezone||this.setPeriod(// will change this.currentPeriod
new EventPeriod_1.default(start,end,timezone)),this.currentPeriod.whenReleased()},
// Source Adding/Removing
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.addSource=function(eventSource){this.otherSources.push(eventSource),this.currentPeriod&&this.currentPeriod.requestSource(eventSource)},EventManager.prototype.removeSource=function(doomedSource){util_1.removeExact(this.otherSources,doomedSource),this.currentPeriod&&this.currentPeriod.purgeSource(doomedSource)},EventManager.prototype.removeAllSources=function(){this.otherSources=[],this.currentPeriod&&this.currentPeriod.purgeAllSources()},
// Source Refetching
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.refetchSource=function(eventSource){var currentPeriod=this.currentPeriod;currentPeriod&&(currentPeriod.freeze(),currentPeriod.purgeSource(eventSource),currentPeriod.requestSource(eventSource),currentPeriod.thaw())},EventManager.prototype.refetchAllSources=function(){var currentPeriod=this.currentPeriod;currentPeriod&&(currentPeriod.freeze(),currentPeriod.purgeAllSources(),currentPeriod.requestSources(this.getSources()),currentPeriod.thaw())},
// Source Querying
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.getSources=function(){return[this.stickySource].concat(this.otherSources)},
// like querySources, but accepts multple match criteria (like multiple IDs)
EventManager.prototype.multiQuerySources=function(matchInputs){
// coerce into an array
matchInputs?$.isArray(matchInputs)||(matchInputs=[matchInputs]):matchInputs=[];var i,matchingSources=[];
// resolve raw inputs to real event source objects
for(i=0;i<matchInputs.length;i++)matchingSources.push.apply(// append
matchingSources,this.querySources(matchInputs[i]));return matchingSources},
// matchInput can either by a real event source object, an ID, or the function/URL for the source.
// returns an array of matching source objects.
EventManager.prototype.querySources=function(matchInput){var i,source,sources=this.otherSources;
// given a proper event source object
for(i=0;i<sources.length;i++)if(source=sources[i],source===matchInput)return[source];
// an ID match
return source=this.getSourceById(EventSource_1.default.normalizeId(matchInput)),source?[source]:(
// parse as an event source
matchInput=EventSourceParser_1.default.parse(matchInput,this.calendar),matchInput?$.grep(sources,(function(source){return isSourcesEquivalent(matchInput,source)})):void 0)},
/*
    ID assumed to already be normalized
    */
EventManager.prototype.getSourceById=function(id){return $.grep(this.otherSources,(function(source){return source.id&&source.id===id}))[0]},
// Event-Period
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.setPeriod=function(eventPeriod){this.currentPeriod&&(this.unbindPeriod(this.currentPeriod),this.currentPeriod=null),this.currentPeriod=eventPeriod,this.bindPeriod(eventPeriod),eventPeriod.requestSources(this.getSources())},EventManager.prototype.bindPeriod=function(eventPeriod){this.listenTo(eventPeriod,"release",(function(eventsPayload){this.trigger("release",eventsPayload)}))},EventManager.prototype.unbindPeriod=function(eventPeriod){this.stopListeningTo(eventPeriod)},
// Event Getting/Adding/Removing
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.getEventDefByUid=function(uid){if(this.currentPeriod)return this.currentPeriod.getEventDefByUid(uid)},EventManager.prototype.addEventDef=function(eventDef,isSticky){isSticky&&this.stickySource.addEventDef(eventDef),this.currentPeriod&&this.currentPeriod.addEventDef(eventDef)},EventManager.prototype.removeEventDefsById=function(eventId){this.getSources().forEach((function(eventSource){eventSource.removeEventDefsById(eventId)})),this.currentPeriod&&this.currentPeriod.removeEventDefsById(eventId)},EventManager.prototype.removeAllEventDefs=function(){this.getSources().forEach((function(eventSource){eventSource.removeAllEventDefs()})),this.currentPeriod&&this.currentPeriod.removeAllEventDefs()},
// Event Mutating
// -----------------------------------------------------------------------------------------------------------------
/*
    Returns an undo function.
    */
EventManager.prototype.mutateEventsWithId=function(eventDefId,eventDefMutation){var eventDefs,currentPeriod=this.currentPeriod,undoFuncs=[];return currentPeriod?(currentPeriod.freeze(),eventDefs=currentPeriod.getEventDefsById(eventDefId),eventDefs.forEach((function(eventDef){
// add/remove esp because id might change
currentPeriod.removeEventDef(eventDef),undoFuncs.push(eventDefMutation.mutateSingle(eventDef)),currentPeriod.addEventDef(eventDef)})),currentPeriod.thaw(),function(){currentPeriod.freeze();for(var i=0;i<eventDefs.length;i++)currentPeriod.removeEventDef(eventDefs[i]),undoFuncs[i](),currentPeriod.addEventDef(eventDefs[i]);currentPeriod.thaw()}):function(){}},
/*
    copies and then mutates
    */
EventManager.prototype.buildMutatedEventInstanceGroup=function(eventDefId,eventDefMutation){var i,defCopy,eventDefs=this.getEventDefsById(eventDefId),allInstances=[];for(i=0;i<eventDefs.length;i++)defCopy=eventDefs[i].clone(),defCopy instanceof SingleEventDef_1.default&&(eventDefMutation.mutateSingle(defCopy),allInstances.push.apply(allInstances,// append
defCopy.buildInstances()));return new EventInstanceGroup_1.default(allInstances)},
// Freezing
// -----------------------------------------------------------------------------------------------------------------
EventManager.prototype.freeze=function(){this.currentPeriod&&this.currentPeriod.freeze()},EventManager.prototype.thaw=function(){this.currentPeriod&&this.currentPeriod.thaw()},
// methods that simply forward to EventPeriod
EventManager.prototype.getEventDefsById=function(eventDefId){return this.currentPeriod.getEventDefsById(eventDefId)},EventManager.prototype.getEventInstances=function(){return this.currentPeriod.getEventInstances()},EventManager.prototype.getEventInstancesWithId=function(eventDefId){return this.currentPeriod.getEventInstancesWithId(eventDefId)},EventManager.prototype.getEventInstancesWithoutId=function(eventDefId){return this.currentPeriod.getEventInstancesWithoutId(eventDefId)},EventManager}();function isSourcesEquivalent(source0,source1){return source0.getPrimitive()===source1.getPrimitive()}
/***/exports.default=EventManager,EmitterMixin_1.default.mixInto(EventManager),ListenerMixin_1.default.mixInto(EventManager)},
/* 221 */
/***/function(module,exports,__nested_webpack_require_376332__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_376332__(2),Theme_1=__nested_webpack_require_376332__(22),StandardTheme=/** @class */function(_super){function StandardTheme(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(StandardTheme,_super),StandardTheme}(Theme_1.default);exports.default=StandardTheme,StandardTheme.prototype.classes={widget:"fc-unthemed",widgetHeader:"fc-widget-header",widgetContent:"fc-widget-content",buttonGroup:"fc-button-group",button:"fc-button",cornerLeft:"fc-corner-left",cornerRight:"fc-corner-right",stateDefault:"fc-state-default",stateActive:"fc-state-active",stateDisabled:"fc-state-disabled",stateHover:"fc-state-hover",stateDown:"fc-state-down",popoverHeader:"fc-widget-header",popoverContent:"fc-widget-content",
// day grid
headerRow:"fc-widget-header",dayRow:"fc-widget-content",
// list view
listView:"fc-widget-content"},StandardTheme.prototype.baseIconClass="fc-icon",StandardTheme.prototype.iconClasses={close:"fc-icon-x",prev:"fc-icon-left-single-arrow",next:"fc-icon-right-single-arrow",prevYear:"fc-icon-left-double-arrow",nextYear:"fc-icon-right-double-arrow"},StandardTheme.prototype.iconOverrideOption="buttonIcons",StandardTheme.prototype.iconOverrideCustomButtonOption="icon",StandardTheme.prototype.iconOverridePrefix="fc-icon-"},
/* 222 */
/***/function(module,exports,__nested_webpack_require_378020__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_378020__(2),Theme_1=__nested_webpack_require_378020__(22),JqueryUiTheme=/** @class */function(_super){function JqueryUiTheme(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(JqueryUiTheme,_super),JqueryUiTheme}(Theme_1.default);exports.default=JqueryUiTheme,JqueryUiTheme.prototype.classes={widget:"ui-widget",widgetHeader:"ui-widget-header",widgetContent:"ui-widget-content",buttonGroup:"fc-button-group",button:"ui-button",cornerLeft:"ui-corner-left",cornerRight:"ui-corner-right",stateDefault:"ui-state-default",stateActive:"ui-state-active",stateDisabled:"ui-state-disabled",stateHover:"ui-state-hover",stateDown:"ui-state-down",today:"ui-state-highlight",popoverHeader:"ui-widget-header",popoverContent:"ui-widget-content",
// day grid
headerRow:"ui-widget-header",dayRow:"ui-widget-content",
// list view
listView:"ui-widget-content"},JqueryUiTheme.prototype.baseIconClass="ui-icon",JqueryUiTheme.prototype.iconClasses={close:"ui-icon-closethick",prev:"ui-icon-circle-triangle-w",next:"ui-icon-circle-triangle-e",prevYear:"ui-icon-seek-prev",nextYear:"ui-icon-seek-next"},JqueryUiTheme.prototype.iconOverrideOption="themeButtonIcons",JqueryUiTheme.prototype.iconOverrideCustomButtonOption="themeIcon",JqueryUiTheme.prototype.iconOverridePrefix="ui-icon-"},
/* 223 */
/***/function(module,exports,__nested_webpack_require_379741__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_379741__(2),$=__nested_webpack_require_379741__(3),Promise_1=__nested_webpack_require_379741__(21),EventSource_1=__nested_webpack_require_379741__(6),FuncEventSource=/** @class */function(_super){function FuncEventSource(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(FuncEventSource,_super),FuncEventSource.parse=function(rawInput,calendar){var rawProps;
// normalize raw input
return $.isFunction(rawInput.events)?// extended form
rawProps=rawInput:$.isFunction(rawInput)&&(// short form
rawProps={events:rawInput}),!!rawProps&&EventSource_1.default.parse.call(this,rawProps,calendar)},FuncEventSource.prototype.fetch=function(start,end,timezone){var _this=this;return this.calendar.pushLoading(),Promise_1.default.construct((function(onResolve){_this.func.call(_this.calendar,start.clone(),end.clone(),timezone,(function(rawEventDefs){_this.calendar.popLoading(),onResolve(_this.parseEventDefs(rawEventDefs))}))}))},FuncEventSource.prototype.getPrimitive=function(){return this.func},FuncEventSource.prototype.applyManualStandardProps=function(rawProps){var superSuccess=_super.prototype.applyManualStandardProps.call(this,rawProps);return this.func=rawProps.events,superSuccess},FuncEventSource}(EventSource_1.default);exports.default=FuncEventSource,FuncEventSource.defineStandardProps({events:!1})},
/* 224 */
/***/function(module,exports,__nested_webpack_require_381744__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_381744__(2),$=__nested_webpack_require_381744__(3),util_1=__nested_webpack_require_381744__(4),Promise_1=__nested_webpack_require_381744__(21),EventSource_1=__nested_webpack_require_381744__(6),JsonFeedEventSource=/** @class */function(_super){function JsonFeedEventSource(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(JsonFeedEventSource,_super),JsonFeedEventSource.parse=function(rawInput,calendar){var rawProps;
// normalize raw input
return"string"===typeof rawInput.url?// extended form
rawProps=rawInput:"string"===typeof rawInput&&(// short form
rawProps={url:rawInput}),!!rawProps&&EventSource_1.default.parse.call(this,rawProps,calendar)},JsonFeedEventSource.prototype.fetch=function(start,end,timezone){var _this=this,ajaxSettings=this.ajaxSettings,onSuccess=ajaxSettings.success,onError=ajaxSettings.error,requestParams=this.buildRequestParams(start,end,timezone);
// todo: eventually handle the promise's then,
// don't intercept success/error
// tho will be a breaking API change
return this.calendar.pushLoading(),Promise_1.default.construct((function(onResolve,onReject){$.ajax($.extend({},// destination
JsonFeedEventSource.AJAX_DEFAULTS,ajaxSettings,{url:_this.url,data:requestParams,success:function(rawEventDefs,status,xhr){var callbackRes;_this.calendar.popLoading(),rawEventDefs?(callbackRes=util_1.applyAll(onSuccess,_this,[rawEventDefs,status,xhr]),// redirect `this`
$.isArray(callbackRes)&&(rawEventDefs=callbackRes),onResolve(_this.parseEventDefs(rawEventDefs))):onReject()},error:function(xhr,statusText,errorThrown){_this.calendar.popLoading(),util_1.applyAll(onError,_this,[xhr,statusText,errorThrown]),// redirect `this`
onReject()}}))}))},JsonFeedEventSource.prototype.buildRequestParams=function(start,end,timezone){var startParam,endParam,timezoneParam,customRequestParams,calendar=this.calendar,ajaxSettings=this.ajaxSettings,params={};return startParam=this.startParam,null==startParam&&(startParam=calendar.opt("startParam")),endParam=this.endParam,null==endParam&&(endParam=calendar.opt("endParam")),timezoneParam=this.timezoneParam,null==timezoneParam&&(timezoneParam=calendar.opt("timezoneParam")),
// retrieve any outbound GET/POST $.ajax data from the options
// supplied as a function that returns a key/value object
customRequestParams=$.isFunction(ajaxSettings.data)?ajaxSettings.data():ajaxSettings.data||{},$.extend(params,customRequestParams),params[startParam]=start.format(),params[endParam]=end.format(),timezone&&"local"!==timezone&&(params[timezoneParam]=timezone),params},JsonFeedEventSource.prototype.getPrimitive=function(){return this.url},JsonFeedEventSource.prototype.applyMiscProps=function(rawProps){this.ajaxSettings=rawProps},JsonFeedEventSource.AJAX_DEFAULTS={dataType:"json",cache:!1},JsonFeedEventSource}(EventSource_1.default);exports.default=JsonFeedEventSource,JsonFeedEventSource.defineStandardProps({
// automatically transfer (true)...
url:!0,startParam:!0,endParam:!0,timezoneParam:!0})},
/* 225 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});var Iterator=/** @class */function(){function Iterator(items){this.items=items||[]}
/* Calls a method on every item passing the arguments through */return Iterator.prototype.proxyCall=function(methodName){for(var args=[],_i=1;_i<arguments.length;_i++)args[_i-1]=arguments[_i];var results=[];return this.items.forEach((function(item){results.push(item[methodName].apply(item,args))})),results},Iterator}();exports.default=Iterator},
/* 226 */
/***/function(module,exports,__nested_webpack_require_387302__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_387302__(3),util_1=__nested_webpack_require_387302__(4),ListenerMixin_1=__nested_webpack_require_387302__(7),MouseFollower=/** @class */function(){function MouseFollower(sourceEl,options){this.isFollowing=!1,this.isHidden=!1,this.isAnimating=!1,// doing the revert animation?
this.options=options=options||{},this.sourceEl=sourceEl,this.parentEl=options.parentEl?$(options.parentEl):sourceEl.parent()}
// Causes the element to start following the mouse
return MouseFollower.prototype.start=function(ev){this.isFollowing||(this.isFollowing=!0,this.y0=util_1.getEvY(ev),this.x0=util_1.getEvX(ev),this.topDelta=0,this.leftDelta=0,this.isHidden||this.updatePosition(),util_1.getEvIsTouch(ev)?this.listenTo($(document),"touchmove",this.handleMove):this.listenTo($(document),"mousemove",this.handleMove))},
// Causes the element to stop following the mouse. If shouldRevert is true, will animate back to original position.
// `callback` gets invoked when the animation is complete. If no animation, it is invoked immediately.
MouseFollower.prototype.stop=function(shouldRevert,callback){var _this=this,revertDuration=this.options.revertDuration,complete=function(){_this.isAnimating=!1,_this.removeElement(),_this.top0=_this.left0=null,// reset state for future updatePosition calls
callback&&callback()};this.isFollowing&&!this.isAnimating&&(// disallow more than one stop animation at a time
this.isFollowing=!1,this.stopListeningTo($(document)),shouldRevert&&revertDuration&&!this.isHidden?(// do a revert animation?
this.isAnimating=!0,this.el.animate({top:this.top0,left:this.left0},{duration:revertDuration,complete:complete})):complete())},
// Gets the tracking element. Create it if necessary
MouseFollower.prototype.getEl=function(){var el=this.el;return el||(el=this.el=this.sourceEl.clone().addClass(this.options.additionalClass||"").css({position:"absolute",visibility:"",display:this.isHidden?"none":"",margin:0,right:"auto",bottom:"auto",width:this.sourceEl.width(),height:this.sourceEl.height(),opacity:this.options.opacity||"",zIndex:this.options.zIndex}),
// we don't want long taps or any mouse interaction causing selection/menus.
// would use preventSelection(), but that prevents selectstart, causing problems.
el.addClass("fc-unselectable"),el.appendTo(this.parentEl)),el},
// Removes the tracking element if it has already been created
MouseFollower.prototype.removeElement=function(){this.el&&(this.el.remove(),this.el=null)},
// Update the CSS position of the tracking element
MouseFollower.prototype.updatePosition=function(){var sourceOffset,origin;this.getEl(),// ensure this.el
// make sure origin info was computed
null==this.top0&&(sourceOffset=this.sourceEl.offset(),origin=this.el.offsetParent().offset(),this.top0=sourceOffset.top-origin.top,this.left0=sourceOffset.left-origin.left),this.el.css({top:this.top0+this.topDelta,left:this.left0+this.leftDelta})},
// Gets called when the user moves the mouse
MouseFollower.prototype.handleMove=function(ev){this.topDelta=util_1.getEvY(ev)-this.y0,this.leftDelta=util_1.getEvX(ev)-this.x0,this.isHidden||this.updatePosition()},
// Temporarily makes the tracking element invisible. Can be called before following starts
MouseFollower.prototype.hide=function(){this.isHidden||(this.isHidden=!0,this.el&&this.el.hide())},
// Show the tracking element after it has been temporarily hidden
MouseFollower.prototype.show=function(){this.isHidden&&(this.isHidden=!1,this.updatePosition(),this.getEl().show())},MouseFollower}();exports.default=MouseFollower,ListenerMixin_1.default.mixInto(MouseFollower)},
/* 227 */
/***/function(module,exports,__nested_webpack_require_393151__){
/* A rectangular panel that is absolutely positioned over other content
------------------------------------------------------------------------------------------------------------------------
Options:
  - className (string)
  - content (HTML string or jQuery element set)
  - parentEl
  - top
  - left
  - right (the x coord of where the right edge should be. not a "CSS" right)
  - autoHide (boolean)
  - show (callback)
  - hide (callback)
*/
Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_393151__(3),util_1=__nested_webpack_require_393151__(4),ListenerMixin_1=__nested_webpack_require_393151__(7),Popover=/** @class */function(){function Popover(options){this.isHidden=!0,this.margin=10,// the space required between the popover and the edges of the scroll container
this.options=options||{}}
// Shows the popover on the specified position. Renders it if not already
return Popover.prototype.show=function(){this.isHidden&&(this.el||this.render(),this.el.show(),this.position(),this.isHidden=!1,this.trigger("show"))},
// Hides the popover, through CSS, but does not remove it from the DOM
Popover.prototype.hide=function(){this.isHidden||(this.el.hide(),this.isHidden=!0,this.trigger("hide"))},
// Creates `this.el` and renders content inside of it
Popover.prototype.render=function(){var _this=this,options=this.options;this.el=$('<div class="fc-popover"/>').addClass(options.className||"").css({
// position initially to the top left to avoid creating scrollbars
top:0,left:0}).append(options.content).appendTo(options.parentEl),
// when a click happens on anything inside with a 'fc-close' className, hide the popover
this.el.on("click",".fc-close",(function(){_this.hide()})),options.autoHide&&this.listenTo($(document),"mousedown",this.documentMousedown)},
// Triggered when the user clicks *anywhere* in the document, for the autoHide feature
Popover.prototype.documentMousedown=function(ev){
// only hide the popover if the click happened outside the popover
this.el&&!$(ev.target).closest(this.el).length&&this.hide()},
// Hides and unregisters any handlers
Popover.prototype.removeElement=function(){this.hide(),this.el&&(this.el.remove(),this.el=null),this.stopListeningTo($(document),"mousedown")},
// Positions the popover optimally, using the top/left/right options
Popover.prototype.position=function(){var viewportTop,viewportLeft,viewportOffset,top,left,options=this.options,origin=this.el.offsetParent().offset(),width=this.el.outerWidth(),height=this.el.outerHeight(),windowEl=$(window),viewportEl=util_1.getScrollParent(this.el);// compute top and left
top=options.top||0,left=void 0!==options.left?options.left:void 0!==options.right?options.right-width:0,viewportEl.is(window)||viewportEl.is(document)?(// normalize getScrollParent's result
viewportEl=windowEl,viewportTop=0,// the window is always at the top left
viewportLeft=0):(viewportOffset=viewportEl.offset(),viewportTop=viewportOffset.top,viewportLeft=viewportOffset.left),
// if the window is scrolled, it causes the visible area to be further down
viewportTop+=windowEl.scrollTop(),viewportLeft+=windowEl.scrollLeft(),
// constrain to the view port. if constrained by two edges, give precedence to top/left
!1!==options.viewportConstrain&&(top=Math.min(top,viewportTop+viewportEl.outerHeight()-height-this.margin),top=Math.max(top,viewportTop+this.margin),left=Math.min(left,viewportLeft+viewportEl.outerWidth()-width-this.margin),left=Math.max(left,viewportLeft+this.margin)),this.el.css({top:top-origin.top,left:left-origin.left})},
// Triggers a callback. Calls a function in the option hash of the same name.
// Arguments beyond the first `name` are forwarded on.
// TODO: better code reuse for this. Repeat code
Popover.prototype.trigger=function(name){this.options[name]&&this.options[name].apply(this,Array.prototype.slice.call(arguments,1))},Popover}();exports.default=Popover,ListenerMixin_1.default.mixInto(Popover)},
/* 228 */
/***/function(module,exports,__nested_webpack_require_398881__){Object.defineProperty(exports,"__esModule",{value:!0});var EmitterMixin_1=__nested_webpack_require_398881__(13),TaskQueue=/** @class */function(){function TaskQueue(){this.q=[],this.isPaused=!1,this.isRunning=!1}return TaskQueue.prototype.queue=function(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];this.q.push.apply(this.q,args),// append
this.tryStart()},TaskQueue.prototype.pause=function(){this.isPaused=!0},TaskQueue.prototype.resume=function(){this.isPaused=!1,this.tryStart()},TaskQueue.prototype.getIsIdle=function(){return!this.isRunning&&!this.isPaused},TaskQueue.prototype.tryStart=function(){!this.isRunning&&this.canRunNext()&&(this.isRunning=!0,this.trigger("start"),this.runRemaining())},TaskQueue.prototype.canRunNext=function(){return!this.isPaused&&this.q.length},TaskQueue.prototype.runRemaining=function(){var task,res,_this=this;do{if(task=this.q.shift(),// always freshly reference q. might have been reassigned.
res=this.runTask(task),res&&res.then)return void res.then((function(){_this.canRunNext()&&_this.runRemaining()}));// prevent marking as stopped
}while(this.canRunNext());this.trigger("stop"),// not really a 'stop' ... more of a 'drained'
this.isRunning=!1,
// if 'stop' handler added more tasks.... TODO: write test for this
this.tryStart()},TaskQueue.prototype.runTask=function(task){return task();// task *is* the function, but subclasses can change the format of a task
},TaskQueue}();exports.default=TaskQueue,EmitterMixin_1.default.mixInto(TaskQueue)},
/* 229 */
/***/function(module,exports,__nested_webpack_require_401223__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_401223__(2),TaskQueue_1=__nested_webpack_require_401223__(228),RenderQueue=/** @class */function(_super){function RenderQueue(waitsByNamespace){var _this=_super.call(this)||this;return _this.waitsByNamespace=waitsByNamespace||{},_this}return tslib_1.__extends(RenderQueue,_super),RenderQueue.prototype.queue=function(taskFunc,namespace,type){var waitMs,task={func:taskFunc,namespace:namespace,type:type};namespace&&(waitMs=this.waitsByNamespace[namespace]),this.waitNamespace&&(namespace===this.waitNamespace&&null!=waitMs?this.delayWait(waitMs):(this.clearWait(),this.tryStart())),this.compoundTask(task)&&(// appended to queue?
this.waitNamespace||null==waitMs?this.tryStart():this.startWait(namespace,waitMs))},RenderQueue.prototype.startWait=function(namespace,waitMs){this.waitNamespace=namespace,this.spawnWait(waitMs)},RenderQueue.prototype.delayWait=function(waitMs){clearTimeout(this.waitId),this.spawnWait(waitMs)},RenderQueue.prototype.spawnWait=function(waitMs){var _this=this;this.waitId=setTimeout((function(){_this.waitNamespace=null,_this.tryStart()}),waitMs)},RenderQueue.prototype.clearWait=function(){this.waitNamespace&&(clearTimeout(this.waitId),this.waitId=null,this.waitNamespace=null)},RenderQueue.prototype.canRunNext=function(){if(!_super.prototype.canRunNext.call(this))return!1;
// waiting for a certain namespace to stop receiving tasks?
if(this.waitNamespace){
// if there was a different namespace task in the meantime,
// that forces all previously-waiting tasks to suddenly execute.
// TODO: find a way to do this in constant time.
for(var q=this.q,i=0;i<q.length;i++)if(q[i].namespace!==this.waitNamespace)return!0;// allow execution
return!1}return!0},RenderQueue.prototype.runTask=function(task){task.func()},RenderQueue.prototype.compoundTask=function(newTask){var i,task,q=this.q,shouldAppend=!0;if(newTask.namespace&&"destroy"===newTask.type)
// remove all init/add/remove ops with same namespace, regardless of order
for(i=q.length-1;i>=0;i--)if(task=q[i],task.namespace===newTask.namespace)switch(task.type){case"init":shouldAppend=!1;
// the latest destroy is cancelled out by not doing the init
/* falls through */case"add":
/* falls through */case"remove":q.splice(i,1);// remove task
}return shouldAppend&&q.push(newTask),shouldAppend},RenderQueue}(TaskQueue_1.default);exports.default=RenderQueue},
/* 230 */
/***/function(module,exports,__nested_webpack_require_405336__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_405336__(2),Model_1=__nested_webpack_require_405336__(51),Component=/** @class */function(_super){function Component(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(Component,_super),Component.prototype.setElement=function(el){this.el=el,this.bindGlobalHandlers(),this.renderSkeleton(),this.set("isInDom",!0)},Component.prototype.removeElement=function(){this.unset("isInDom"),this.unrenderSkeleton(),this.unbindGlobalHandlers(),this.el.remove()},Component.prototype.bindGlobalHandlers=function(){
// subclasses can override
},Component.prototype.unbindGlobalHandlers=function(){
// subclasses can override
},
/*
    NOTE: Can't have a `render` method. Read the deprecation notice in View::executeDateRender
    */
// Renders the basic structure of the view before any content is rendered
Component.prototype.renderSkeleton=function(){
// subclasses should implement
},
// Unrenders the basic structure of the view
Component.prototype.unrenderSkeleton=function(){
// subclasses should implement
},Component}(Model_1.default);exports.default=Component},
/* 231 */
/***/function(module,exports,__nested_webpack_require_407114__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_407114__(2),$=__nested_webpack_require_407114__(3),moment=__nested_webpack_require_407114__(0),util_1=__nested_webpack_require_407114__(4),moment_ext_1=__nested_webpack_require_407114__(11),date_formatting_1=__nested_webpack_require_407114__(49),Component_1=__nested_webpack_require_407114__(230),util_2=__nested_webpack_require_407114__(19),DateComponent=/** @class */function(_super){function DateComponent(_view,_options){var _this=_super.call(this)||this;return _this.isRTL=!1,// frequently accessed options
_this.hitsNeededDepth=0,// necessary because multiple callers might need the same hits
_this.hasAllDayBusinessHours=!1,// TODO: unify with largeUnit and isTimeScale?
_this.isDatesRendered=!1,
// hack to set options prior to the this.opt calls
_view&&(_this["view"]=_view),_options&&(_this["options"]=_options),_this.uid=String(DateComponent.guid++),_this.childrenByUid={},_this.nextDayThreshold=moment.duration(_this.opt("nextDayThreshold")),_this.isRTL=_this.opt("isRTL"),_this.fillRendererClass&&(_this.fillRenderer=new _this.fillRendererClass(_this)),_this.eventRendererClass&&(// fillRenderer is optional -----v
_this.eventRenderer=new _this.eventRendererClass(_this,_this.fillRenderer)),_this.helperRendererClass&&_this.eventRenderer&&(_this.helperRenderer=new _this.helperRendererClass(_this,_this.eventRenderer)),_this.businessHourRendererClass&&_this.fillRenderer&&(_this.businessHourRenderer=new _this.businessHourRendererClass(_this,_this.fillRenderer)),_this}// TODO: better system for this?
return tslib_1.__extends(DateComponent,_super),DateComponent.prototype.addChild=function(child){return!this.childrenByUid[child.uid]&&(this.childrenByUid[child.uid]=child,!0)},DateComponent.prototype.removeChild=function(child){return!!this.childrenByUid[child.uid]&&(delete this.childrenByUid[child.uid],!0)},
// TODO: only do if isInDom?
// TODO: make part of Component, along with children/batch-render system?
DateComponent.prototype.updateSize=function(totalHeight,isAuto,isResize){this.callChildren("updateSize",arguments)},
// Options
// -----------------------------------------------------------------------------------------------------------------
DateComponent.prototype.opt=function(name){return this._getView().opt(name);// default implementation
},DateComponent.prototype.publiclyTrigger=function(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];var calendar=this._getCalendar();return calendar.publiclyTrigger.apply(calendar,args)},DateComponent.prototype.hasPublicHandlers=function(){for(var args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];var calendar=this._getCalendar();return calendar.hasPublicHandlers.apply(calendar,args)},
// Date
// -----------------------------------------------------------------------------------------------------------------
DateComponent.prototype.executeDateRender=function(dateProfile){this.dateProfile=dateProfile,// for rendering
this.renderDates(dateProfile),this.isDatesRendered=!0,this.callChildren("executeDateRender",arguments)},DateComponent.prototype.executeDateUnrender=function(){this.callChildren("executeDateUnrender",arguments),this.dateProfile=null,this.unrenderDates(),this.isDatesRendered=!1},
// date-cell content only
DateComponent.prototype.renderDates=function(dateProfile){
// subclasses should implement
},
// date-cell content only
DateComponent.prototype.unrenderDates=function(){
// subclasses should override
},
// Now-Indicator
// -----------------------------------------------------------------------------------------------------------------
// Returns a string unit, like 'second' or 'minute' that defined how often the current time indicator
// should be refreshed. If something falsy is returned, no time indicator is rendered at all.
DateComponent.prototype.getNowIndicatorUnit=function(){
// subclasses should implement
},
// Renders a current time indicator at the given datetime
DateComponent.prototype.renderNowIndicator=function(date){this.callChildren("renderNowIndicator",arguments)},
// Undoes the rendering actions from renderNowIndicator
DateComponent.prototype.unrenderNowIndicator=function(){this.callChildren("unrenderNowIndicator",arguments)},
// Business Hours
// ---------------------------------------------------------------------------------------------------------------
DateComponent.prototype.renderBusinessHours=function(businessHourGenerator){this.businessHourRenderer&&this.businessHourRenderer.render(businessHourGenerator),this.callChildren("renderBusinessHours",arguments)},
// Unrenders previously-rendered business-hours
DateComponent.prototype.unrenderBusinessHours=function(){this.callChildren("unrenderBusinessHours",arguments),this.businessHourRenderer&&this.businessHourRenderer.unrender()},
// Event Displaying
// -----------------------------------------------------------------------------------------------------------------
DateComponent.prototype.executeEventRender=function(eventsPayload){this.eventRenderer?(this.eventRenderer.rangeUpdated(),// poorly named now
this.eventRenderer.render(eventsPayload)):this["renderEvents"]&&// legacy
this["renderEvents"](convertEventsPayloadToLegacyArray(eventsPayload)),this.callChildren("executeEventRender",arguments)},DateComponent.prototype.executeEventUnrender=function(){this.callChildren("executeEventUnrender",arguments),this.eventRenderer?this.eventRenderer.unrender():this["destroyEvents"]&&// legacy
this["destroyEvents"]()},DateComponent.prototype.getBusinessHourSegs=function(){var segs=this.getOwnBusinessHourSegs();return this.iterChildren((function(child){segs.push.apply(segs,child.getBusinessHourSegs())})),segs},DateComponent.prototype.getOwnBusinessHourSegs=function(){return this.businessHourRenderer?this.businessHourRenderer.getSegs():[]},DateComponent.prototype.getEventSegs=function(){var segs=this.getOwnEventSegs();return this.iterChildren((function(child){segs.push.apply(segs,child.getEventSegs())})),segs},DateComponent.prototype.getOwnEventSegs=function(){return this.eventRenderer?this.eventRenderer.getSegs():[]},
// Event Rendering Triggering
// -----------------------------------------------------------------------------------------------------------------
DateComponent.prototype.triggerAfterEventsRendered=function(){this.triggerAfterEventSegsRendered(this.getEventSegs()),this.publiclyTrigger("eventAfterAllRender",{context:this,args:[this]})},DateComponent.prototype.triggerAfterEventSegsRendered=function(segs){var _this=this;
// an optimization, because getEventLegacy is expensive
this.hasPublicHandlers("eventAfterRender")&&segs.forEach((function(seg){var legacy;seg.el&&(// necessary?
legacy=seg.footprint.getEventLegacy(),_this.publiclyTrigger("eventAfterRender",{context:legacy,args:[legacy,seg.el,_this]}))}))},DateComponent.prototype.triggerBeforeEventsDestroyed=function(){this.triggerBeforeEventSegsDestroyed(this.getEventSegs())},DateComponent.prototype.triggerBeforeEventSegsDestroyed=function(segs){var _this=this;this.hasPublicHandlers("eventDestroy")&&segs.forEach((function(seg){var legacy;seg.el&&(// necessary?
legacy=seg.footprint.getEventLegacy(),_this.publiclyTrigger("eventDestroy",{context:legacy,args:[legacy,seg.el,_this]}))}))},
// Event Rendering Utils
// -----------------------------------------------------------------------------------------------------------------
// Hides all rendered event segments linked to the given event
// RECURSIVE with subcomponents
DateComponent.prototype.showEventsWithId=function(eventDefId){this.getEventSegs().forEach((function(seg){seg.footprint.eventDef.id===eventDefId&&seg.el&&seg.el.css("visibility","")})),this.callChildren("showEventsWithId",arguments)},
// Shows all rendered event segments linked to the given event
// RECURSIVE with subcomponents
DateComponent.prototype.hideEventsWithId=function(eventDefId){this.getEventSegs().forEach((function(seg){seg.footprint.eventDef.id===eventDefId&&seg.el&&seg.el.css("visibility","hidden")})),this.callChildren("hideEventsWithId",arguments)},
// Drag-n-Drop Rendering (for both events and external elements)
// ---------------------------------------------------------------------------------------------------------------
// Renders a visual indication of a event or external-element drag over the given drop zone.
// If an external-element, seg will be `null`.
// Must return elements used for any mock events.
DateComponent.prototype.renderDrag=function(eventFootprints,seg,isTouch){var renderedHelper=!1;return this.iterChildren((function(child){child.renderDrag(eventFootprints,seg,isTouch)&&(renderedHelper=!0)})),renderedHelper},
// Unrenders a visual indication of an event or external-element being dragged.
DateComponent.prototype.unrenderDrag=function(){this.callChildren("unrenderDrag",arguments)},
// Event Resizing
// ---------------------------------------------------------------------------------------------------------------
// Renders a visual indication of an event being resized.
DateComponent.prototype.renderEventResize=function(eventFootprints,seg,isTouch){this.callChildren("renderEventResize",arguments)},
// Unrenders a visual indication of an event being resized.
DateComponent.prototype.unrenderEventResize=function(){this.callChildren("unrenderEventResize",arguments)},
// Selection
// ---------------------------------------------------------------------------------------------------------------
// Renders a visual indication of the selection
// TODO: rename to `renderSelection` after legacy is gone
DateComponent.prototype.renderSelectionFootprint=function(componentFootprint){this.renderHighlight(componentFootprint),this.callChildren("renderSelectionFootprint",arguments)},
// Unrenders a visual indication of selection
DateComponent.prototype.unrenderSelection=function(){this.unrenderHighlight(),this.callChildren("unrenderSelection",arguments)},
// Highlight
// ---------------------------------------------------------------------------------------------------------------
// Renders an emphasis on the given date range. Given a span (unzoned start/end and other misc data)
DateComponent.prototype.renderHighlight=function(componentFootprint){this.fillRenderer&&this.fillRenderer.renderFootprint("highlight",componentFootprint,{getClasses:function(){return["fc-highlight"]}}),this.callChildren("renderHighlight",arguments)},
// Unrenders the emphasis on a date range
DateComponent.prototype.unrenderHighlight=function(){this.fillRenderer&&this.fillRenderer.unrender("highlight"),this.callChildren("unrenderHighlight",arguments)},
// Hit Areas
// ---------------------------------------------------------------------------------------------------------------
// just because all DateComponents support this interface
// doesn't mean they need to have their own internal coord system. they can defer to sub-components.
DateComponent.prototype.hitsNeeded=function(){this.hitsNeededDepth++||this.prepareHits(),this.callChildren("hitsNeeded",arguments)},DateComponent.prototype.hitsNotNeeded=function(){this.hitsNeededDepth&&! --this.hitsNeededDepth&&this.releaseHits(),this.callChildren("hitsNotNeeded",arguments)},DateComponent.prototype.prepareHits=function(){
// subclasses can implement
},DateComponent.prototype.releaseHits=function(){
// subclasses can implement
},
// Given coordinates from the topleft of the document, return data about the date-related area underneath.
// Can return an object with arbitrary properties (although top/right/left/bottom are encouraged).
// Must have a `grid` property, a reference to this current grid. TODO: avoid this
// The returned object will be processed by getHitFootprint and getHitEl.
DateComponent.prototype.queryHit=function(leftOffset,topOffset){var uid,hit,childrenByUid=this.childrenByUid;for(uid in childrenByUid)if(hit=childrenByUid[uid].queryHit(leftOffset,topOffset),hit)break;return hit},DateComponent.prototype.getSafeHitFootprint=function(hit){var footprint=this.getHitFootprint(hit);return this.dateProfile.activeUnzonedRange.containsRange(footprint.unzonedRange)?footprint:null},DateComponent.prototype.getHitFootprint=function(hit){
// what about being abstract!?
},
// Given position-level information about a date-related area within the grid,
// should return a jQuery element that best represents it. passed to dayClick callback.
DateComponent.prototype.getHitEl=function(hit){
// what about being abstract!?
},
/* Converting eventRange -> eventFootprint
    ------------------------------------------------------------------------------------------------------------------*/
DateComponent.prototype.eventRangesToEventFootprints=function(eventRanges){var i,eventFootprints=[];for(i=0;i<eventRanges.length;i++)eventFootprints.push.apply(// append
eventFootprints,this.eventRangeToEventFootprints(eventRanges[i]));return eventFootprints},DateComponent.prototype.eventRangeToEventFootprints=function(eventRange){return[util_2.eventRangeToEventFootprint(eventRange)]},
/* Converting componentFootprint/eventFootprint -> segs
    ------------------------------------------------------------------------------------------------------------------*/
DateComponent.prototype.eventFootprintsToSegs=function(eventFootprints){var i,segs=[];for(i=0;i<eventFootprints.length;i++)segs.push.apply(segs,this.eventFootprintToSegs(eventFootprints[i]));return segs},
// Given an event's span (unzoned start/end and other misc data), and the event itself,
// slices into segments and attaches event-derived properties to them.
// eventSpan - { start, end, isStart, isEnd, otherthings... }
DateComponent.prototype.eventFootprintToSegs=function(eventFootprint){var segs,i,seg,unzonedRange=eventFootprint.componentFootprint.unzonedRange;for(segs=this.componentFootprintToSegs(eventFootprint.componentFootprint),i=0;i<segs.length;i++)seg=segs[i],unzonedRange.isStart||(seg.isStart=!1),unzonedRange.isEnd||(seg.isEnd=!1),seg.footprint=eventFootprint;return segs},DateComponent.prototype.componentFootprintToSegs=function(componentFootprint){return[]},
// Utils
// ---------------------------------------------------------------------------------------------------------------
DateComponent.prototype.callChildren=function(methodName,args){this.iterChildren((function(child){child[methodName].apply(child,args)}))},DateComponent.prototype.iterChildren=function(func){var uid,childrenByUid=this.childrenByUid;for(uid in childrenByUid)func(childrenByUid[uid])},DateComponent.prototype._getCalendar=function(){var t=this;return t.calendar||t.view.calendar},DateComponent.prototype._getView=function(){return this.view},DateComponent.prototype._getDateProfile=function(){return this._getView().get("dateProfile")},
// Generates HTML for an anchor to another view into the calendar.
// Will either generate an <a> tag or a non-clickable <span> tag, depending on enabled settings.
// `gotoOptions` can either be a moment input, or an object with the form:
// { date, type, forceOff }
// `type` is a view-type like "day" or "week". default value is "day".
// `attrs` and `innerHtml` are use to generate the rest of the HTML tag.
DateComponent.prototype.buildGotoAnchorHtml=function(gotoOptions,attrs,innerHtml){var date,type,forceOff,finalOptions;return $.isPlainObject(gotoOptions)?(date=gotoOptions.date,type=gotoOptions.type,forceOff=gotoOptions.forceOff):date=gotoOptions,date=moment_ext_1.default(date),// if a string, parse it
finalOptions={date:date.format("YYYY-MM-DD"),type:type||"day"},"string"===typeof attrs&&(innerHtml=attrs,attrs=null),attrs=attrs?" "+util_1.attrsToStr(attrs):"",// will have a leading space
innerHtml=innerHtml||"",!forceOff&&this.opt("navLinks")?"<a"+attrs+' data-goto="'+util_1.htmlEscape(JSON.stringify(finalOptions))+'">'+innerHtml+"</a>":"<span"+attrs+">"+innerHtml+"</span>"},DateComponent.prototype.getAllDayHtml=function(){return this.opt("allDayHtml")||util_1.htmlEscape(this.opt("allDayText"))},
// Computes HTML classNames for a single-day element
DateComponent.prototype.getDayClasses=function(date,noThemeHighlight){var today,view=this._getView(),classes=[];return this.dateProfile.activeUnzonedRange.containsDate(date)?(classes.push("fc-"+util_1.dayIDs[date.day()]),view.isDateInOtherMonth(date,this.dateProfile)&&// TODO: use DateComponent subclass somehow
classes.push("fc-other-month"),today=view.calendar.getNow(),date.isSame(today,"day")?(classes.push("fc-today"),!0!==noThemeHighlight&&classes.push(view.calendar.theme.getClass("today"))):date<today?classes.push("fc-past"):classes.push("fc-future")):classes.push("fc-disabled-day"),classes},
// Utility for formatting a range. Accepts a range object, formatting string, and optional separator.
// Displays all-day ranges naturally, with an inclusive end. Takes the current isRTL into account.
// The timezones of the dates within `range` will be respected.
DateComponent.prototype.formatRange=function(range,isAllDay,formatStr,separator){var end=range.end;return isAllDay&&(end=end.clone().subtract(1)),date_formatting_1.formatRange(range.start,end,formatStr,separator,this.isRTL)},
// Compute the number of the give units in the "current" range.
// Will return a floating-point number. Won't round.
DateComponent.prototype.currentRangeAs=function(unit){return this._getDateProfile().currentUnzonedRange.as(unit)},
// Returns the date range of the full days the given range visually appears to occupy.
// Returns a plain object with start/end, NOT an UnzonedRange!
DateComponent.prototype.computeDayRange=function(unzonedRange){var calendar=this._getCalendar(),startDay=calendar.msToUtcMoment(unzonedRange.startMs,!0),end=calendar.msToUtcMoment(unzonedRange.endMs),endTimeMS=+end.time(),endDay=end.clone().stripTime();// the beginning of the day the range exclusively ends
// If the end time is actually inclusively part of the next day and is equal to or
// beyond the next day threshold, adjust the end to be the exclusive end of `endDay`.
// Otherwise, leaving it as inclusive will cause it to exclude `endDay`.
return endTimeMS&&endTimeMS>=this.nextDayThreshold&&endDay.add(1,"days"),
// If end is within `startDay` but not past nextDayThreshold, assign the default duration of one day.
endDay<=startDay&&(endDay=startDay.clone().add(1,"days")),{start:startDay,end:endDay}},
// Does the given range visually appear to occupy more than one day?
DateComponent.prototype.isMultiDayRange=function(unzonedRange){var dayRange=this.computeDayRange(unzonedRange);return dayRange.end.diff(dayRange.start,"days")>1},DateComponent.guid=0,DateComponent}(Component_1.default);
// legacy
function convertEventsPayloadToLegacyArray(eventsPayload){var eventDefId,eventInstances,i,legacyEvents=[];for(eventDefId in eventsPayload)for(eventInstances=eventsPayload[eventDefId].eventInstances,i=0;i<eventInstances.length;i++)legacyEvents.push(eventInstances[i].toLegacy());return legacyEvents}
/***/exports.default=DateComponent},
/* 232 */
/***/function(module,exports,__nested_webpack_require_432738__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_432738__(3),moment=__nested_webpack_require_432738__(0),util_1=__nested_webpack_require_432738__(4),options_1=__nested_webpack_require_432738__(33),Iterator_1=__nested_webpack_require_432738__(225),GlobalEmitter_1=__nested_webpack_require_432738__(23),EmitterMixin_1=__nested_webpack_require_432738__(13),ListenerMixin_1=__nested_webpack_require_432738__(7),Toolbar_1=__nested_webpack_require_432738__(257),OptionsManager_1=__nested_webpack_require_432738__(258),ViewSpecManager_1=__nested_webpack_require_432738__(259),Constraints_1=__nested_webpack_require_432738__(217),locale_1=__nested_webpack_require_432738__(32),moment_ext_1=__nested_webpack_require_432738__(11),UnzonedRange_1=__nested_webpack_require_432738__(5),ComponentFootprint_1=__nested_webpack_require_432738__(12),EventDateProfile_1=__nested_webpack_require_432738__(16),EventManager_1=__nested_webpack_require_432738__(220),BusinessHourGenerator_1=__nested_webpack_require_432738__(218),EventSourceParser_1=__nested_webpack_require_432738__(38),EventDefParser_1=__nested_webpack_require_432738__(36),SingleEventDef_1=__nested_webpack_require_432738__(9),EventDefMutation_1=__nested_webpack_require_432738__(39),EventSource_1=__nested_webpack_require_432738__(6),ThemeRegistry_1=__nested_webpack_require_432738__(57),Calendar=/** @class */function(){function Calendar(el,overrides){this.loadingLevel=0,// number of simultaneous loading tasks
this.ignoreUpdateViewSize=0,this.freezeContentHeightDepth=0,
// declare the current calendar instance relies on GlobalEmitter. needed for garbage collection.
// unneeded() is called in destroy.
GlobalEmitter_1.default.needed(),this.el=el,this.viewsByType={},this.optionsManager=new OptionsManager_1.default(this,overrides),this.viewSpecManager=new ViewSpecManager_1.default(this.optionsManager,this),this.initMomentInternals(),// needs to happen after options hash initialized
this.initCurrentDate(),this.initEventManager(),this.constraints=new Constraints_1.default(this.eventManager,this),this.constructed()}return Calendar.prototype.constructed=function(){
// useful for monkeypatching. used?
},Calendar.prototype.getView=function(){return this.view},Calendar.prototype.publiclyTrigger=function(name,triggerInfo){var context,args,optHandler=this.opt(name);// Emitter's method
if($.isPlainObject(triggerInfo)?(context=triggerInfo.context,args=triggerInfo.args):$.isArray(triggerInfo)&&(args=triggerInfo),null==context&&(context=this.el[0]),args||(args=[]),this.triggerWith(name,context,args),optHandler)return optHandler.apply(context,args)},Calendar.prototype.hasPublicHandlers=function(name){return this.hasHandlers(name)||this.opt(name);// handler specified in options
},
// Options Public API
// -----------------------------------------------------------------------------------------------------------------
// public getter/setter
Calendar.prototype.option=function(name,value){var newOptionHash;if("string"===typeof name){if(void 0===value)// getter
return this.optionsManager.get(name);// setter for individual option
newOptionHash={},newOptionHash[name]=value,this.optionsManager.add(newOptionHash)}else"object"===typeof name&&// compound setter with object input
this.optionsManager.add(name)},
// private getter
Calendar.prototype.opt=function(name){return this.optionsManager.get(name)},
// View
// -----------------------------------------------------------------------------------------------------------------
// Given a view name for a custom view or a standard view, creates a ready-to-go View object
Calendar.prototype.instantiateView=function(viewType){var spec=this.viewSpecManager.getViewSpec(viewType);if(!spec)throw new Error('View type "'+viewType+'" is not valid');return new spec["class"](this,spec)},
// Returns a boolean about whether the view is okay to instantiate at some point
Calendar.prototype.isValidViewType=function(viewType){return Boolean(this.viewSpecManager.getViewSpec(viewType))},Calendar.prototype.changeView=function(viewName,dateOrRange){dateOrRange&&(dateOrRange.start&&dateOrRange.end?// a range
this.optionsManager.recordOverrides({visibleRange:dateOrRange}):// a date
this.currentDate=this.moment(dateOrRange).stripZone()),this.renderView(viewName)},
// Forces navigation to a view for the given date.
// `viewType` can be a specific view name or a generic one like "week" or "day".
Calendar.prototype.zoomTo=function(newDate,viewType){var spec;viewType=viewType||"day",// day is default zoom
spec=this.viewSpecManager.getViewSpec(viewType)||this.viewSpecManager.getUnitViewSpec(viewType),this.currentDate=newDate.clone(),this.renderView(spec?spec.type:null)},
// Current Date
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.initCurrentDate=function(){var defaultDateInput=this.opt("defaultDate");
// compute the initial ambig-timezone date
this.currentDate=null!=defaultDateInput?this.moment(defaultDateInput).stripZone():this.getNow()},Calendar.prototype.prev=function(){var view=this.view,prevInfo=view.dateProfileGenerator.buildPrev(view.get("dateProfile"));prevInfo.isValid&&(this.currentDate=prevInfo.date,this.renderView())},Calendar.prototype.next=function(){var view=this.view,nextInfo=view.dateProfileGenerator.buildNext(view.get("dateProfile"));nextInfo.isValid&&(this.currentDate=nextInfo.date,this.renderView())},Calendar.prototype.prevYear=function(){this.currentDate.add(-1,"years"),this.renderView()},Calendar.prototype.nextYear=function(){this.currentDate.add(1,"years"),this.renderView()},Calendar.prototype.today=function(){this.currentDate=this.getNow(),// should deny like prev/next?
this.renderView()},Calendar.prototype.gotoDate=function(zonedDateInput){this.currentDate=this.moment(zonedDateInput).stripZone(),this.renderView()},Calendar.prototype.incrementDate=function(delta){this.currentDate.add(moment.duration(delta)),this.renderView()},
// for external API
Calendar.prototype.getDate=function(){return this.applyTimezone(this.currentDate);// infuse the calendar's timezone
},
// Loading Triggering
// -----------------------------------------------------------------------------------------------------------------
// Should be called when any type of async data fetching begins
Calendar.prototype.pushLoading=function(){this.loadingLevel++||this.publiclyTrigger("loading",[!0,this.view])},
// Should be called when any type of async data fetching completes
Calendar.prototype.popLoading=function(){--this.loadingLevel||this.publiclyTrigger("loading",[!1,this.view])},
// High-level Rendering
// -----------------------------------------------------------------------------------
Calendar.prototype.render=function(){this.contentEl?this.elementVisible()&&(
// mainly for the public API
this.calcSize(),this.updateViewSize()):this.initialRender()},Calendar.prototype.initialRender=function(){var _this=this,el=this.el;el.addClass("fc"),
// event delegation for nav links
el.on("click.fc","a[data-goto]",(function(ev){var anchorEl=$(ev.currentTarget),gotoOptions=anchorEl.data("goto"),date=_this.moment(gotoOptions.date),viewType=gotoOptions.type,customAction=_this.view.opt("navLink"+util_1.capitaliseFirstLetter(viewType)+"Click");"function"===typeof customAction?customAction(date,ev):("string"===typeof customAction&&(viewType=customAction),_this.zoomTo(date,viewType))})),
// called immediately, and upon option change
this.optionsManager.watch("settingTheme",["?theme","?themeSystem"],(function(opts){var themeClass=ThemeRegistry_1.getThemeSystemClass(opts.themeSystem||opts.theme),theme=new themeClass(_this.optionsManager),widgetClass=theme.getClass("widget");_this.theme=theme,widgetClass&&el.addClass(widgetClass)}),(function(){var widgetClass=_this.theme.getClass("widget");_this.theme=null,widgetClass&&el.removeClass(widgetClass)})),this.optionsManager.watch("settingBusinessHourGenerator",["?businessHours"],(function(deps){_this.businessHourGenerator=new BusinessHourGenerator_1.default(deps.businessHours,_this),_this.view&&_this.view.set("businessHourGenerator",_this.businessHourGenerator)}),(function(){_this.businessHourGenerator=null})),
// called immediately, and upon option change.
// HACK: locale often affects isRTL, so we explicitly listen to that too.
this.optionsManager.watch("applyingDirClasses",["?isRTL","?locale"],(function(opts){el.toggleClass("fc-ltr",!opts.isRTL),el.toggleClass("fc-rtl",opts.isRTL)})),this.contentEl=$("<div class='fc-view-container'/>").prependTo(el),this.initToolbars(),this.renderHeader(),this.renderFooter(),this.renderView(this.opt("defaultView")),this.opt("handleWindowResize")&&$(window).resize(this.windowResizeProxy=util_1.debounce(// prevents rapid calls
this.windowResize.bind(this),this.opt("windowResizeDelay")))},Calendar.prototype.destroy=function(){this.view&&this.clearView(),this.toolbarsManager.proxyCall("removeElement"),this.contentEl.remove(),this.el.removeClass("fc fc-ltr fc-rtl"),
// removes theme-related root className
this.optionsManager.unwatch("settingTheme"),this.optionsManager.unwatch("settingBusinessHourGenerator"),this.el.off(".fc"),// unbind nav link handlers
this.windowResizeProxy&&($(window).unbind("resize",this.windowResizeProxy),this.windowResizeProxy=null),GlobalEmitter_1.default.unneeded()},Calendar.prototype.elementVisible=function(){return this.el.is(":visible")},
// Render Queue
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.bindViewHandlers=function(view){var _this=this;view.watch("titleForCalendar",["title"],(function(deps){view===_this.view&&// hack
_this.setToolbarsTitle(deps.title)})),view.watch("dateProfileForCalendar",["dateProfile"],(function(deps){view===_this.view&&(// hack
_this.currentDate=deps.dateProfile.date,// might have been constrained by view dates
_this.updateToolbarButtons(deps.dateProfile))}))},Calendar.prototype.unbindViewHandlers=function(view){view.unwatch("titleForCalendar"),view.unwatch("dateProfileForCalendar")},
// View Rendering
// -----------------------------------------------------------------------------------
// Renders a view because of a date change, view-type change, or for the first time.
// If not given a viewType, keep the current view but render different dates.
// Accepts an optional scroll state to restore to.
Calendar.prototype.renderView=function(viewType){var newView,oldView=this.view;this.freezeContentHeight(),oldView&&viewType&&oldView.type!==viewType&&this.clearView(),
// if viewType changed, or the view was never created, create a fresh view
!this.view&&viewType&&(newView=this.view=this.viewsByType[viewType]||(this.viewsByType[viewType]=this.instantiateView(viewType)),this.bindViewHandlers(newView),newView.startBatchRender(),// so that setElement+setDate rendering are joined
newView.setElement($("<div class='fc-view fc-"+viewType+"-view' />").appendTo(this.contentEl)),this.toolbarsManager.proxyCall("activateButton",viewType)),this.view&&(
// prevent unnecessary change firing
this.view.get("businessHourGenerator")!==this.businessHourGenerator&&this.view.set("businessHourGenerator",this.businessHourGenerator),this.view.setDate(this.currentDate),newView&&newView.stopBatchRender()),this.thawContentHeight()},
// Unrenders the current view and reflects this change in the Header.
// Unregsiters the `view`, but does not remove from viewByType hash.
Calendar.prototype.clearView=function(){var currentView=this.view;this.toolbarsManager.proxyCall("deactivateButton",currentView.type),this.unbindViewHandlers(currentView),currentView.removeElement(),currentView.unsetDate(),// so bindViewHandlers doesn't fire with old values next time
this.view=null},
// Destroys the view, including the view object. Then, re-instantiates it and renders it.
// Maintains the same scroll state.
// TODO: maintain any other user-manipulated state.
Calendar.prototype.reinitView=function(){var oldView=this.view,scroll=oldView.queryScroll();// wouldn't be so complicated if Calendar owned the scroll
this.freezeContentHeight(),this.clearView(),this.calcSize(),this.renderView(oldView.type),// needs the type to freshly render
this.view.applyScroll(scroll),this.thawContentHeight()},
// Resizing
// -----------------------------------------------------------------------------------
Calendar.prototype.getSuggestedViewHeight=function(){return null==this.suggestedViewHeight&&this.calcSize(),this.suggestedViewHeight},Calendar.prototype.isHeightAuto=function(){return"auto"===this.opt("contentHeight")||"auto"===this.opt("height")},Calendar.prototype.updateViewSize=function(isResize){void 0===isResize&&(isResize=!1);var scroll,view=this.view;if(!this.ignoreUpdateViewSize&&view)return isResize&&(this.calcSize(),scroll=view.queryScroll()),this.ignoreUpdateViewSize++,view.updateSize(this.getSuggestedViewHeight(),this.isHeightAuto(),isResize),this.ignoreUpdateViewSize--,isResize&&view.applyScroll(scroll),!0;// signal success
},Calendar.prototype.calcSize=function(){this.elementVisible()&&this._calcSize()},Calendar.prototype._calcSize=function(){var contentHeightInput=this.opt("contentHeight"),heightInput=this.opt("height");// exists and not 'auto'
this.suggestedViewHeight="number"===typeof contentHeightInput?contentHeightInput:"function"===typeof contentHeightInput?contentHeightInput():"number"===typeof heightInput?heightInput-this.queryToolbarsHeight():"function"===typeof heightInput?heightInput()-this.queryToolbarsHeight():"parent"===heightInput?this.el.parent().height()-this.queryToolbarsHeight():Math.round(this.contentEl.width()/Math.max(this.opt("aspectRatio"),.5))},Calendar.prototype.windowResize=function(ev){
// the purpose: so we don't process jqui "resize" events that have bubbled up
// cast to any because .target, which is Element, can't be compared to window for some reason.
ev.target===window&&this.view&&this.view.isDatesRendered&&this.updateViewSize(!0)&&// isResize=true, returns true on success
this.publiclyTrigger("windowResize",[this.view])},
/* Height "Freezing"
    -----------------------------------------------------------------------------*/
Calendar.prototype.freezeContentHeight=function(){this.freezeContentHeightDepth++||this.forceFreezeContentHeight()},Calendar.prototype.forceFreezeContentHeight=function(){this.contentEl.css({width:"100%",height:this.contentEl.height(),overflow:"hidden"})},Calendar.prototype.thawContentHeight=function(){this.freezeContentHeightDepth--,
// always bring back to natural height
this.contentEl.css({width:"",height:"",overflow:""}),
// but if there are future thaws, re-freeze
this.freezeContentHeightDepth&&this.forceFreezeContentHeight()},
// Toolbar
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.initToolbars=function(){this.header=new Toolbar_1.default(this,this.computeHeaderOptions()),this.footer=new Toolbar_1.default(this,this.computeFooterOptions()),this.toolbarsManager=new Iterator_1.default([this.header,this.footer])},Calendar.prototype.computeHeaderOptions=function(){return{extraClasses:"fc-header-toolbar",layout:this.opt("header")}},Calendar.prototype.computeFooterOptions=function(){return{extraClasses:"fc-footer-toolbar",layout:this.opt("footer")}},
// can be called repeatedly and Header will rerender
Calendar.prototype.renderHeader=function(){var header=this.header;header.setToolbarOptions(this.computeHeaderOptions()),header.render(),header.el&&this.el.prepend(header.el)},
// can be called repeatedly and Footer will rerender
Calendar.prototype.renderFooter=function(){var footer=this.footer;footer.setToolbarOptions(this.computeFooterOptions()),footer.render(),footer.el&&this.el.append(footer.el)},Calendar.prototype.setToolbarsTitle=function(title){this.toolbarsManager.proxyCall("updateTitle",title)},Calendar.prototype.updateToolbarButtons=function(dateProfile){var now=this.getNow(),view=this.view,todayInfo=view.dateProfileGenerator.build(now),prevInfo=view.dateProfileGenerator.buildPrev(view.get("dateProfile")),nextInfo=view.dateProfileGenerator.buildNext(view.get("dateProfile"));this.toolbarsManager.proxyCall(todayInfo.isValid&&!dateProfile.currentUnzonedRange.containsDate(now)?"enableButton":"disableButton","today"),this.toolbarsManager.proxyCall(prevInfo.isValid?"enableButton":"disableButton","prev"),this.toolbarsManager.proxyCall(nextInfo.isValid?"enableButton":"disableButton","next")},Calendar.prototype.queryToolbarsHeight=function(){return this.toolbarsManager.items.reduce((function(accumulator,toolbar){var toolbarHeight=toolbar.el?toolbar.el.outerHeight(!0):0;// includes margin
return accumulator+toolbarHeight}),0)},
// Selection
// -----------------------------------------------------------------------------------------------------------------
// this public method receives start/end dates in any format, with any timezone
Calendar.prototype.select=function(zonedStartInput,zonedEndInput){this.view.select(this.buildSelectFootprint.apply(this,arguments))},Calendar.prototype.unselect=function(){this.view&&this.view.unselect()},
// Given arguments to the select method in the API, returns a span (unzoned start/end and other info)
Calendar.prototype.buildSelectFootprint=function(zonedStartInput,zonedEndInput){var end,start=this.moment(zonedStartInput).stripZone();return end=zonedEndInput?this.moment(zonedEndInput).stripZone():start.hasTime()?start.clone().add(this.defaultTimedEventDuration):start.clone().add(this.defaultAllDayEventDuration),new ComponentFootprint_1.default(new UnzonedRange_1.default(start,end),!start.hasTime())},
// Date Utils
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.initMomentInternals=function(){var _this=this;this.defaultAllDayEventDuration=moment.duration(this.opt("defaultAllDayEventDuration")),this.defaultTimedEventDuration=moment.duration(this.opt("defaultTimedEventDuration")),
// Called immediately, and when any of the options change.
// Happens before any internal objects rebuild or rerender, because this is very core.
this.optionsManager.watch("buildingMomentLocale",["?locale","?monthNames","?monthNamesShort","?dayNames","?dayNamesShort","?firstDay","?weekNumberCalculation"],(function(opts){var _week,weekNumberCalculation=opts.weekNumberCalculation,firstDay=opts.firstDay;
// normalize
"iso"===weekNumberCalculation&&(weekNumberCalculation="ISO");var localeData=Object.create(// make a cheap copy
locale_1.getMomentLocaleData(opts.locale));opts.monthNames&&(localeData._months=opts.monthNames),opts.monthNamesShort&&(localeData._monthsShort=opts.monthNamesShort),opts.dayNames&&(localeData._weekdays=opts.dayNames),opts.dayNamesShort&&(localeData._weekdaysShort=opts.dayNamesShort),null==firstDay&&"ISO"===weekNumberCalculation&&(firstDay=1),null!=firstDay&&(_week=Object.create(localeData._week),// _week: { dow: # }
_week.dow=firstDay,localeData._week=_week),// whitelist certain kinds of input
"ISO"!==weekNumberCalculation&&"local"!==weekNumberCalculation&&"function"!==typeof weekNumberCalculation||(localeData._fullCalendar_weekCalc=weekNumberCalculation),_this.localeData=localeData,
// If the internal current date object already exists, move to new locale.
// We do NOT need to do this technique for event dates, because this happens when converting to "segments".
_this.currentDate&&_this.localizeMoment(_this.currentDate)}))},
// Builds a moment using the settings of the current calendar: timezone and locale.
// Accepts anything the vanilla moment() constructor accepts.
Calendar.prototype.moment=function(){for(var mom,args=[],_i=0;_i<arguments.length;_i++)args[_i]=arguments[_i];// TODO
return"local"===this.opt("timezone")?(mom=moment_ext_1.default.apply(null,args),
// Force the moment to be local, because momentExt doesn't guarantee it.
mom.hasTime()&&// don't give ambiguously-timed moments a local zone
mom.local()):mom="UTC"===this.opt("timezone")?moment_ext_1.default.utc.apply(null,args):moment_ext_1.default.parseZone.apply(null,args),this.localizeMoment(mom),mom},Calendar.prototype.msToMoment=function(ms,forceAllDay){var mom=moment_ext_1.default.utc(ms);// TODO: optimize by using Date.UTC
return forceAllDay?mom.stripTime():mom=this.applyTimezone(mom),this.localizeMoment(mom),mom},Calendar.prototype.msToUtcMoment=function(ms,forceAllDay){var mom=moment_ext_1.default.utc(ms);// TODO: optimize by using Date.UTC
return forceAllDay&&mom.stripTime(),this.localizeMoment(mom),mom},
// Updates the given moment's locale settings to the current calendar locale settings.
Calendar.prototype.localizeMoment=function(mom){mom._locale=this.localeData},
// Returns a boolean about whether or not the calendar knows how to calculate
// the timezone offset of arbitrary dates in the current timezone.
Calendar.prototype.getIsAmbigTimezone=function(){return"local"!==this.opt("timezone")&&"UTC"!==this.opt("timezone")},
// Returns a copy of the given date in the current timezone. Has no effect on dates without times.
Calendar.prototype.applyTimezone=function(date){if(!date.hasTime())return date.clone();var adjustedZonedDate,zonedDate=this.moment(date.toArray()),timeAdjust=date.time().asMilliseconds()-zonedDate.time().asMilliseconds();
// Safari sometimes has problems with this coersion when near DST. Adjust if necessary. (bug #2396)
return timeAdjust&&(// is the time result different than expected?
adjustedZonedDate=zonedDate.clone().add(timeAdjust),// add milliseconds
date.time().asMilliseconds()-adjustedZonedDate.time().asMilliseconds()===0&&(// does it match perfectly now?
zonedDate=adjustedZonedDate)),zonedDate},
/*
    Assumes the footprint is non-open-ended.
    */
Calendar.prototype.footprintToDateProfile=function(componentFootprint,ignoreEnd){void 0===ignoreEnd&&(ignoreEnd=!1);var end,start=moment_ext_1.default.utc(componentFootprint.unzonedRange.startMs);return ignoreEnd||(end=moment_ext_1.default.utc(componentFootprint.unzonedRange.endMs)),componentFootprint.isAllDay?(start.stripTime(),end&&end.stripTime()):(start=this.applyTimezone(start),end&&(end=this.applyTimezone(end))),this.localizeMoment(start),end&&this.localizeMoment(end),new EventDateProfile_1.default(start,end,this)},
// Returns a moment for the current date, as defined by the client's computer or from the `now` option.
// Will return an moment with an ambiguous timezone.
Calendar.prototype.getNow=function(){var now=this.opt("now");return"function"===typeof now&&(now=now()),this.moment(now).stripZone()},
// Produces a human-readable string for the given duration.
// Side-effect: changes the locale of the given duration.
Calendar.prototype.humanizeDuration=function(duration){return duration.locale(this.opt("locale")).humanize()},
// will return `null` if invalid range
Calendar.prototype.parseUnzonedRange=function(rangeInput){var start=null,end=null;return rangeInput.start&&(start=this.moment(rangeInput.start).stripZone()),rangeInput.end&&(end=this.moment(rangeInput.end).stripZone()),start||end?start&&end&&end.isBefore(start)?null:new UnzonedRange_1.default(start,end):null},
// Event-Date Utilities
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.initEventManager=function(){var _this=this,eventManager=new EventManager_1.default(this),rawSources=this.opt("eventSources")||[],singleRawSource=this.opt("events");this.eventManager=eventManager,singleRawSource&&rawSources.unshift(singleRawSource),eventManager.on("release",(function(eventsPayload){_this.trigger("eventsReset",eventsPayload)})),eventManager.freeze(),rawSources.forEach((function(rawSource){var source=EventSourceParser_1.default.parse(rawSource,_this);source&&eventManager.addSource(source)})),eventManager.thaw()},Calendar.prototype.requestEvents=function(start,end){return this.eventManager.requestEvents(start,end,this.opt("timezone"),!this.opt("lazyFetching"))},
// Get an event's normalized end date. If not present, calculate it from the defaults.
Calendar.prototype.getEventEnd=function(event){return event.end?event.end.clone():this.getDefaultEventEnd(event.allDay,event.start)},
// Given an event's allDay status and start date, return what its fallback end date should be.
// TODO: rename to computeDefaultEventEnd
Calendar.prototype.getDefaultEventEnd=function(allDay,zonedStart){var end=zonedStart.clone();return allDay?end.stripTime().add(this.defaultAllDayEventDuration):end.add(this.defaultTimedEventDuration),this.getIsAmbigTimezone()&&end.stripZone(),end},
// Public Events API
// -----------------------------------------------------------------------------------------------------------------
Calendar.prototype.rerenderEvents=function(){this.view.flash("displayingEvents")},Calendar.prototype.refetchEvents=function(){this.eventManager.refetchAllSources()},Calendar.prototype.renderEvents=function(eventInputs,isSticky){this.eventManager.freeze();for(var i=0;i<eventInputs.length;i++)this.renderEvent(eventInputs[i],isSticky);this.eventManager.thaw()},Calendar.prototype.renderEvent=function(eventInput,isSticky){void 0===isSticky&&(isSticky=!1);var eventManager=this.eventManager,eventDef=EventDefParser_1.default.parse(eventInput,eventInput.source||eventManager.stickySource);eventDef&&eventManager.addEventDef(eventDef,isSticky)},
// legacyQuery operates on legacy event instance objects
Calendar.prototype.removeEvents=function(legacyQuery){var eventDef,i,eventManager=this.eventManager,legacyInstances=[],idMap={};if(null==legacyQuery)// shortcut for removing all
eventManager.removeAllEventDefs();// persist=true
else{
// compute unique IDs
for(eventManager.getEventInstances().forEach((function(eventInstance){legacyInstances.push(eventInstance.toLegacy())})),legacyInstances=filterLegacyEventInstances(legacyInstances,legacyQuery),i=0;i<legacyInstances.length;i++)eventDef=this.eventManager.getEventDefByUid(legacyInstances[i]._id),idMap[eventDef.id]=!0;for(i in eventManager.freeze(),idMap)// reuse `i` as an "id"
eventManager.removeEventDefsById(i);// persist=true
eventManager.thaw()}},
// legacyQuery operates on legacy event instance objects
Calendar.prototype.clientEvents=function(legacyQuery){var legacyEventInstances=[];return this.eventManager.getEventInstances().forEach((function(eventInstance){legacyEventInstances.push(eventInstance.toLegacy())})),filterLegacyEventInstances(legacyEventInstances,legacyQuery)},Calendar.prototype.updateEvents=function(eventPropsArray){this.eventManager.freeze();for(var i=0;i<eventPropsArray.length;i++)this.updateEvent(eventPropsArray[i]);this.eventManager.thaw()},Calendar.prototype.updateEvent=function(eventProps){var eventInstance,eventDefMutation,eventDef=this.eventManager.getEventDefByUid(eventProps._id);eventDef instanceof SingleEventDef_1.default&&(eventInstance=eventDef.buildInstance(),eventDefMutation=EventDefMutation_1.default.createFromRawProps(eventInstance,eventProps,// raw props
null),this.eventManager.mutateEventsWithId(eventDef.id,eventDefMutation))},
// Public Event Sources API
// ------------------------------------------------------------------------------------
Calendar.prototype.getEventSources=function(){return this.eventManager.otherSources.slice();// clone
},Calendar.prototype.getEventSourceById=function(id){return this.eventManager.getSourceById(EventSource_1.default.normalizeId(id))},Calendar.prototype.addEventSource=function(sourceInput){var source=EventSourceParser_1.default.parse(sourceInput,this);source&&this.eventManager.addSource(source)},Calendar.prototype.removeEventSources=function(sourceMultiQuery){var sources,i,eventManager=this.eventManager;if(null==sourceMultiQuery)this.eventManager.removeAllSources();else{for(sources=eventManager.multiQuerySources(sourceMultiQuery),eventManager.freeze(),i=0;i<sources.length;i++)eventManager.removeSource(sources[i]);eventManager.thaw()}},Calendar.prototype.removeEventSource=function(sourceQuery){var i,eventManager=this.eventManager,sources=eventManager.querySources(sourceQuery);for(eventManager.freeze(),i=0;i<sources.length;i++)eventManager.removeSource(sources[i]);eventManager.thaw()},Calendar.prototype.refetchEventSources=function(sourceMultiQuery){var i,eventManager=this.eventManager,sources=eventManager.multiQuerySources(sourceMultiQuery);for(eventManager.freeze(),i=0;i<sources.length;i++)eventManager.refetchSource(sources[i]);eventManager.thaw()},
// not for internal use. use options module directly instead.
Calendar.defaults=options_1.globalDefaults,Calendar.englishDefaults=options_1.englishDefaults,Calendar.rtlDefaults=options_1.rtlDefaults,Calendar}();function filterLegacyEventInstances(legacyEventInstances,legacyQuery){return null==legacyQuery?legacyEventInstances:$.isFunction(legacyQuery)?legacyEventInstances.filter(legacyQuery):(// an event ID
legacyQuery+="",legacyEventInstances.filter((function(legacyEventInstance){
// soft comparison because id not be normalized to string
// tslint:disable-next-line
return legacyEventInstance.id==legacyQuery||legacyEventInstance._id===legacyQuery;// can specify internal id, but must exactly match
})))}
/***/exports.default=Calendar,EmitterMixin_1.default.mixInto(Calendar),ListenerMixin_1.default.mixInto(Calendar)},
/* 233 */
/***/function(module,exports,__nested_webpack_require_474032__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_474032__(2),$=__nested_webpack_require_474032__(3),moment=__nested_webpack_require_474032__(0),exportHooks=__nested_webpack_require_474032__(18),util_1=__nested_webpack_require_474032__(4),moment_ext_1=__nested_webpack_require_474032__(11),ListenerMixin_1=__nested_webpack_require_474032__(7),HitDragListener_1=__nested_webpack_require_474032__(17),SingleEventDef_1=__nested_webpack_require_474032__(9),EventInstanceGroup_1=__nested_webpack_require_474032__(20),EventSource_1=__nested_webpack_require_474032__(6),Interaction_1=__nested_webpack_require_474032__(14),ExternalDropping=/** @class */function(_super){function ExternalDropping(){var _this=null!==_super&&_super.apply(this,arguments)||this;// jqui-dragging an external element? boolean
return _this.isDragging=!1,_this}
/*
    component impements:
      - eventRangesToEventFootprints
      - isEventInstanceGroupAllowed
      - isExternalInstanceGroupAllowed
      - renderDrag
      - unrenderDrag
    */return tslib_1.__extends(ExternalDropping,_super),ExternalDropping.prototype.end=function(){this.dragListener&&this.dragListener.endInteraction()},ExternalDropping.prototype.bindToDocument=function(){this.listenTo($(document),{dragstart:this.handleDragStart,sortstart:this.handleDragStart})},ExternalDropping.prototype.unbindFromDocument=function(){this.stopListeningTo($(document))},
// Called when a jQuery UI drag is initiated anywhere in the DOM
ExternalDropping.prototype.handleDragStart=function(ev,ui){var el,accept;this.opt("droppable")&&(// only listen if this setting is on
el=$((ui?ui.item:null)||ev.target),
// Test that the dragged element passes the dropAccept selector or filter function.
// FYI, the default is "*" (matches all)
accept=this.opt("dropAccept"),($.isFunction(accept)?accept.call(el[0],el):el.is(accept))&&(this.isDragging||// prevent double-listening if fired twice
this.listenToExternalDrag(el,ev,ui)))},
// Called when a jQuery UI drag starts and it needs to be monitored for dropping
ExternalDropping.prototype.listenToExternalDrag=function(el,ev,ui){var singleEventDef,_this=this,component=this.component,view=this.view,meta=getDraggedElMeta(el),dragListener=this.dragListener=new HitDragListener_1.default(component,{interactionStart:function(){_this.isDragging=!0},hitOver:function(hit){var mutatedEventInstanceGroup,isAllowed=!0,hitFootprint=hit.component.getSafeHitFootprint(hit);hitFootprint?(singleEventDef=_this.computeExternalDrop(hitFootprint,meta),singleEventDef?(mutatedEventInstanceGroup=new EventInstanceGroup_1.default(singleEventDef.buildInstances()),isAllowed=meta.eventProps?// isEvent?
component.isEventInstanceGroupAllowed(mutatedEventInstanceGroup):component.isExternalInstanceGroupAllowed(mutatedEventInstanceGroup)):isAllowed=!1):isAllowed=!1,isAllowed||(singleEventDef=null,util_1.disableCursor()),singleEventDef&&component.renderDrag(// called without a seg parameter
component.eventRangesToEventFootprints(mutatedEventInstanceGroup.sliceRenderRanges(component.dateProfile.renderUnzonedRange,view.calendar)))},hitOut:function(){singleEventDef=null;// signal unsuccessful
},hitDone:function(){util_1.enableCursor(),component.unrenderDrag()},interactionEnd:function(ev){singleEventDef&&// element was dropped on a valid hit
view.reportExternalDrop(singleEventDef,Boolean(meta.eventProps),// isEvent
Boolean(meta.stick),// isSticky
el,ev,ui),_this.isDragging=!1,_this.dragListener=null}});dragListener.startDrag(ev)},
// Given a hit to be dropped upon, and misc data associated with the jqui drag (guaranteed to be a plain object),
// returns the zoned start/end dates for the event that would result from the hypothetical drop. end might be null.
// Returning a null value signals an invalid drop hit.
// DOES NOT consider overlap/constraint.
// Assumes both footprints are non-open-ended.
ExternalDropping.prototype.computeExternalDrop=function(componentFootprint,meta){var end,eventDef,calendar=this.view.calendar,start=moment_ext_1.default.utc(componentFootprint.unzonedRange.startMs).stripZone();return componentFootprint.isAllDay&&(
// if dropped on an all-day span, and element's metadata specified a time, set it
meta.startTime?start.time(meta.startTime):start.stripTime()),meta.duration&&(end=start.clone().add(meta.duration)),start=calendar.applyTimezone(start),end&&(end=calendar.applyTimezone(end)),eventDef=SingleEventDef_1.default.parse($.extend({},meta.eventProps,{start:start,end:end}),new EventSource_1.default(calendar)),eventDef},ExternalDropping}(Interaction_1.default);
// Given a jQuery element that might represent a dragged FullCalendar event, returns an intermediate data structure
// to be used for Event Object creation.
// A defined `.eventProps`, even when empty, indicates that an event should be created.
function getDraggedElMeta(el){var eventProps,startTime,duration,stick,prefix=exportHooks.dataAttrPrefix;return prefix&&(prefix+="-"),eventProps=el.data(prefix+"event")||null,eventProps&&(eventProps="object"===typeof eventProps?$.extend({},eventProps):{},
// pluck special-cased date/time properties
startTime=eventProps.start,null==startTime&&(startTime=eventProps.time),// accept 'time' as well
duration=eventProps.duration,stick=eventProps.stick,delete eventProps.start,delete eventProps.time,delete eventProps.duration,delete eventProps.stick),
// fallback to standalone attribute values for each of the date/time properties
null==startTime&&(startTime=el.data(prefix+"start")),null==startTime&&(startTime=el.data(prefix+"time")),// accept 'time' as well
null==duration&&(duration=el.data(prefix+"duration")),null==stick&&(stick=el.data(prefix+"stick")),
// massage into correct data types
startTime=null!=startTime?moment.duration(startTime):null,duration=null!=duration?moment.duration(duration):null,stick=Boolean(stick),{eventProps:eventProps,startTime:startTime,duration:duration,stick:stick}}
/***/exports.default=ExternalDropping,ListenerMixin_1.default.mixInto(ExternalDropping),
/* External-Dragging-Element Data
----------------------------------------------------------------------------------------------------------------------*/
// Require all HTML5 data-* attributes used by FullCalendar to have this prefix.
// A value of '' will query attributes like data-event. A value of 'fc' will query attributes like data-fc-event.
exportHooks.dataAttrPrefix=""},
/* 234 */
/***/function(module,exports,__nested_webpack_require_483452__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_483452__(2),$=__nested_webpack_require_483452__(3),util_1=__nested_webpack_require_483452__(4),EventDefMutation_1=__nested_webpack_require_483452__(39),EventDefDateMutation_1=__nested_webpack_require_483452__(40),HitDragListener_1=__nested_webpack_require_483452__(17),Interaction_1=__nested_webpack_require_483452__(14),EventResizing=/** @class */function(_super){
/*
    component impements:
      - bindSegHandlerToEl
      - publiclyTrigger
      - diffDates
      - eventRangesToEventFootprints
      - isEventInstanceGroupAllowed
      - getSafeHitFootprint
    */
function EventResizing(component,eventPointing){var _this=_super.call(this,component)||this;return _this.isResizing=!1,_this.eventPointing=eventPointing,_this}return tslib_1.__extends(EventResizing,_super),EventResizing.prototype.end=function(){this.dragListener&&this.dragListener.endInteraction()},EventResizing.prototype.bindToEl=function(el){var component=this.component;component.bindSegHandlerToEl(el,"mousedown",this.handleMouseDown.bind(this)),component.bindSegHandlerToEl(el,"touchstart",this.handleTouchStart.bind(this))},EventResizing.prototype.handleMouseDown=function(seg,ev){this.component.canStartResize(seg,ev)&&this.buildDragListener(seg,$(ev.target).is(".fc-start-resizer")).startInteraction(ev,{distance:5})},EventResizing.prototype.handleTouchStart=function(seg,ev){this.component.canStartResize(seg,ev)&&this.buildDragListener(seg,$(ev.target).is(".fc-start-resizer")).startInteraction(ev)},
// Creates a listener that tracks the user as they resize an event segment.
// Generic enough to work with any type of Grid.
EventResizing.prototype.buildDragListener=function(seg,isStart){var isDragging,resizeMutation,_this=this,component=this.component,view=this.view,calendar=view.calendar,eventManager=calendar.eventManager,el=seg.el,eventDef=seg.footprint.eventDef,eventInstance=seg.footprint.eventInstance,dragListener=this.dragListener=new HitDragListener_1.default(component,{scroll:this.opt("dragScroll"),subjectEl:el,interactionStart:function(){isDragging=!1},dragStart:function(ev){isDragging=!0,
// ensure a mouseout on the manipulated event has been reported
_this.eventPointing.handleMouseout(seg,ev),_this.segResizeStart(seg,ev)},hitOver:function(hit,isOrig,origHit){var mutatedEventInstanceGroup,isAllowed=!0,origHitFootprint=component.getSafeHitFootprint(origHit),hitFootprint=component.getSafeHitFootprint(hit);origHitFootprint&&hitFootprint?(resizeMutation=isStart?_this.computeEventStartResizeMutation(origHitFootprint,hitFootprint,seg.footprint):_this.computeEventEndResizeMutation(origHitFootprint,hitFootprint,seg.footprint),resizeMutation?(mutatedEventInstanceGroup=eventManager.buildMutatedEventInstanceGroup(eventDef.id,resizeMutation),isAllowed=component.isEventInstanceGroupAllowed(mutatedEventInstanceGroup)):isAllowed=!1):isAllowed=!1,isAllowed?resizeMutation.isEmpty()&&(
// no change. (FYI, event dates might have zones)
resizeMutation=null):(resizeMutation=null,util_1.disableCursor()),resizeMutation&&(view.hideEventsWithId(seg.footprint.eventDef.id),view.renderEventResize(component.eventRangesToEventFootprints(mutatedEventInstanceGroup.sliceRenderRanges(component.dateProfile.renderUnzonedRange,calendar)),seg))},hitOut:function(){resizeMutation=null},hitDone:function(){view.unrenderEventResize(seg),view.showEventsWithId(seg.footprint.eventDef.id),util_1.enableCursor()},interactionEnd:function(ev){isDragging&&_this.segResizeStop(seg,ev),resizeMutation&&// valid date to resize to?
// no need to re-show original, will rerender all anyways. esp important if eventRenderWait
view.reportEventResize(eventInstance,resizeMutation,el,ev),_this.dragListener=null}});return dragListener},
// Called before event segment resizing starts
EventResizing.prototype.segResizeStart=function(seg,ev){this.isResizing=!0,this.component.publiclyTrigger("eventResizeStart",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,{},this.view]})},
// Called after event segment resizing stops
EventResizing.prototype.segResizeStop=function(seg,ev){this.isResizing=!1,this.component.publiclyTrigger("eventResizeStop",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,{},this.view]})},
// Returns new date-information for an event segment being resized from its start
EventResizing.prototype.computeEventStartResizeMutation=function(startFootprint,endFootprint,origEventFootprint){var dateMutation,eventDefMutation,origRange=origEventFootprint.componentFootprint.unzonedRange,startDelta=this.component.diffDates(endFootprint.unzonedRange.getStart(),startFootprint.unzonedRange.getStart());return origRange.getStart().add(startDelta)<origRange.getEnd()&&(dateMutation=new EventDefDateMutation_1.default,dateMutation.setStartDelta(startDelta),eventDefMutation=new EventDefMutation_1.default,eventDefMutation.setDateMutation(dateMutation),eventDefMutation)},
// Returns new date-information for an event segment being resized from its end
EventResizing.prototype.computeEventEndResizeMutation=function(startFootprint,endFootprint,origEventFootprint){var dateMutation,eventDefMutation,origRange=origEventFootprint.componentFootprint.unzonedRange,endDelta=this.component.diffDates(endFootprint.unzonedRange.getEnd(),startFootprint.unzonedRange.getEnd());return origRange.getEnd().add(endDelta)>origRange.getStart()&&(dateMutation=new EventDefDateMutation_1.default,dateMutation.setEndDelta(endDelta),eventDefMutation=new EventDefMutation_1.default,eventDefMutation.setDateMutation(dateMutation),eventDefMutation)},EventResizing}(Interaction_1.default);exports.default=EventResizing},
/* 235 */
/***/function(module,exports,__nested_webpack_require_491969__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_491969__(2),util_1=__nested_webpack_require_491969__(4),EventDefMutation_1=__nested_webpack_require_491969__(39),EventDefDateMutation_1=__nested_webpack_require_491969__(40),DragListener_1=__nested_webpack_require_491969__(59),HitDragListener_1=__nested_webpack_require_491969__(17),MouseFollower_1=__nested_webpack_require_491969__(226),Interaction_1=__nested_webpack_require_491969__(14),EventDragging=/** @class */function(_super){
/*
    component implements:
      - bindSegHandlerToEl
      - publiclyTrigger
      - diffDates
      - eventRangesToEventFootprints
      - isEventInstanceGroupAllowed
    */
function EventDragging(component,eventPointing){var _this=_super.call(this,component)||this;return _this.isDragging=!1,_this.eventPointing=eventPointing,_this}return tslib_1.__extends(EventDragging,_super),EventDragging.prototype.end=function(){this.dragListener&&this.dragListener.endInteraction()},EventDragging.prototype.getSelectionDelay=function(){var delay=this.opt("eventLongPressDelay");return null==delay&&(delay=this.opt("longPressDelay")),delay},EventDragging.prototype.bindToEl=function(el){var component=this.component;component.bindSegHandlerToEl(el,"mousedown",this.handleMousedown.bind(this)),component.bindSegHandlerToEl(el,"touchstart",this.handleTouchStart.bind(this))},EventDragging.prototype.handleMousedown=function(seg,ev){!this.component.shouldIgnoreMouse()&&this.component.canStartDrag(seg,ev)&&this.buildDragListener(seg).startInteraction(ev,{distance:5})},EventDragging.prototype.handleTouchStart=function(seg,ev){var component=this.component,settings={delay:this.view.isEventDefSelected(seg.footprint.eventDef)?// already selected?
0:this.getSelectionDelay()};component.canStartDrag(seg,ev)?this.buildDragListener(seg).startInteraction(ev,settings):component.canStartSelection(seg,ev)&&this.buildSelectListener(seg).startInteraction(ev,settings)},
// seg isn't draggable, but let's use a generic DragListener
// simply for the delay, so it can be selected.
// Has side effect of setting/unsetting `dragListener`
EventDragging.prototype.buildSelectListener=function(seg){var _this=this,view=this.view,eventDef=seg.footprint.eventDef,eventInstance=seg.footprint.eventInstance;// null for inverse-background events
if(this.dragListener)return this.dragListener;var dragListener=this.dragListener=new DragListener_1.default({dragStart:function(ev){dragListener.isTouch&&!view.isEventDefSelected(eventDef)&&eventInstance&&
// if not previously selected, will fire after a delay. then, select the event
view.selectEventInstance(eventInstance)},interactionEnd:function(ev){_this.dragListener=null}});return dragListener},
// Builds a listener that will track user-dragging on an event segment.
// Generic enough to work with any type of Grid.
// Has side effect of setting/unsetting `dragListener`
EventDragging.prototype.buildDragListener=function(seg){var isDragging,mouseFollower,eventDefMutation,_this=this,component=this.component,view=this.view,calendar=view.calendar,eventManager=calendar.eventManager,el=seg.el,eventDef=seg.footprint.eventDef,eventInstance=seg.footprint.eventInstance;if(this.dragListener)return this.dragListener;
// Tracks mouse movement over the *view's* coordinate map. Allows dragging and dropping between subcomponents
// of the view.
var dragListener=this.dragListener=new HitDragListener_1.default(view,{scroll:this.opt("dragScroll"),subjectEl:el,subjectCenter:!0,interactionStart:function(ev){seg.component=component,// for renderDrag
isDragging=!1,mouseFollower=new MouseFollower_1.default(seg.el,{additionalClass:"fc-dragging",parentEl:view.el,opacity:dragListener.isTouch?null:_this.opt("dragOpacity"),revertDuration:_this.opt("dragRevertDuration"),zIndex:2}),mouseFollower.hide(),// don't show until we know this is a real drag
mouseFollower.start(ev)},dragStart:function(ev){dragListener.isTouch&&!view.isEventDefSelected(eventDef)&&eventInstance&&
// if not previously selected, will fire after a delay. then, select the event
view.selectEventInstance(eventInstance),isDragging=!0,
// ensure a mouseout on the manipulated event has been reported
_this.eventPointing.handleMouseout(seg,ev),_this.segDragStart(seg,ev),view.hideEventsWithId(seg.footprint.eventDef.id)},hitOver:function(hit,isOrig,origHit){var origFootprint,footprint,mutatedEventInstanceGroup,isAllowed=!0;
// starting hit could be forced (DayGrid.limit)
seg.hit&&(origHit=seg.hit),
// hit might not belong to this grid, so query origin grid
origFootprint=origHit.component.getSafeHitFootprint(origHit),footprint=hit.component.getSafeHitFootprint(hit),origFootprint&&footprint?(eventDefMutation=_this.computeEventDropMutation(origFootprint,footprint,eventDef),eventDefMutation?(mutatedEventInstanceGroup=eventManager.buildMutatedEventInstanceGroup(eventDef.id,eventDefMutation),isAllowed=component.isEventInstanceGroupAllowed(mutatedEventInstanceGroup)):isAllowed=!1):isAllowed=!1,isAllowed||(eventDefMutation=null,util_1.disableCursor()),
// if a valid drop location, have the subclass render a visual indication
eventDefMutation&&view.renderDrag(// truthy if rendered something
component.eventRangesToEventFootprints(mutatedEventInstanceGroup.sliceRenderRanges(component.dateProfile.renderUnzonedRange,calendar)),seg,dragListener.isTouch)?mouseFollower.hide():mouseFollower.show(),isOrig&&(
// needs to have moved hits to be a valid drop
eventDefMutation=null)},hitOut:function(){view.unrenderDrag(seg),// unrender whatever was done in renderDrag
mouseFollower.show(),// show in case we are moving out of all hits
eventDefMutation=null},hitDone:function(){util_1.enableCursor()},interactionEnd:function(ev){delete seg.component,// prevent side effects
// do revert animation if hasn't changed. calls a callback when finished (whether animation or not)
mouseFollower.stop(!eventDefMutation,(function(){isDragging&&(view.unrenderDrag(seg),_this.segDragStop(seg,ev)),view.showEventsWithId(seg.footprint.eventDef.id),eventDefMutation&&
// no need to re-show original, will rerender all anyways. esp important if eventRenderWait
view.reportEventDrop(eventInstance,eventDefMutation,el,ev)})),_this.dragListener=null}});return dragListener},
// Called before event segment dragging starts
EventDragging.prototype.segDragStart=function(seg,ev){this.isDragging=!0,this.component.publiclyTrigger("eventDragStart",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,{},this.view]})},
// Called after event segment dragging stops
EventDragging.prototype.segDragStop=function(seg,ev){this.isDragging=!1,this.component.publiclyTrigger("eventDragStop",{context:seg.el[0],args:[seg.footprint.getEventLegacy(),ev,{},this.view]})},
// DOES NOT consider overlap/constraint
EventDragging.prototype.computeEventDropMutation=function(startFootprint,endFootprint,eventDef){var eventDefMutation=new EventDefMutation_1.default;return eventDefMutation.setDateMutation(this.computeEventDateMutation(startFootprint,endFootprint)),eventDefMutation},EventDragging.prototype.computeEventDateMutation=function(startFootprint,endFootprint){var dateDelta,dateMutation,date0=startFootprint.unzonedRange.getStart(),date1=endFootprint.unzonedRange.getStart(),clearEnd=!1,forceTimed=!1,forceAllDay=!1;return startFootprint.isAllDay!==endFootprint.isAllDay&&(clearEnd=!0,endFootprint.isAllDay?(forceAllDay=!0,date0.stripTime()):forceTimed=!0),dateDelta=this.component.diffDates(date1,date0),dateMutation=new EventDefDateMutation_1.default,dateMutation.clearEnd=clearEnd,dateMutation.forceTimed=forceTimed,dateMutation.forceAllDay=forceAllDay,dateMutation.setDateDelta(dateDelta),dateMutation},EventDragging}(Interaction_1.default);exports.default=EventDragging},
/* 236 */
/***/function(module,exports,__nested_webpack_require_504069__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_504069__(2),util_1=__nested_webpack_require_504069__(4),HitDragListener_1=__nested_webpack_require_504069__(17),ComponentFootprint_1=__nested_webpack_require_504069__(12),UnzonedRange_1=__nested_webpack_require_504069__(5),Interaction_1=__nested_webpack_require_504069__(14),DateSelecting=/** @class */function(_super){
/*
    component must implement:
      - bindDateHandlerToEl
      - getSafeHitFootprint
      - renderHighlight
      - unrenderHighlight
    */
function DateSelecting(component){var _this=_super.call(this,component)||this;return _this.dragListener=_this.buildDragListener(),_this}return tslib_1.__extends(DateSelecting,_super),DateSelecting.prototype.end=function(){this.dragListener.endInteraction()},DateSelecting.prototype.getDelay=function(){var delay=this.opt("selectLongPressDelay");return null==delay&&(delay=this.opt("longPressDelay")),delay},DateSelecting.prototype.bindToEl=function(el){var _this=this,component=this.component,dragListener=this.dragListener;component.bindDateHandlerToEl(el,"mousedown",(function(ev){_this.opt("selectable")&&!component.shouldIgnoreMouse()&&dragListener.startInteraction(ev,{distance:_this.opt("selectMinDistance")})})),component.bindDateHandlerToEl(el,"touchstart",(function(ev){_this.opt("selectable")&&!component.shouldIgnoreTouch()&&dragListener.startInteraction(ev,{delay:_this.getDelay()})})),util_1.preventSelection(el)},
// Creates a listener that tracks the user's drag across day elements, for day selecting.
DateSelecting.prototype.buildDragListener=function(){var selectionFootprint,_this=this,component=this.component,dragListener=new HitDragListener_1.default(component,{scroll:this.opt("dragScroll"),interactionStart:function(){selectionFootprint=null},dragStart:function(ev){_this.view.unselect(ev);// since we could be rendering a new selection, we want to clear any old one
},hitOver:function(hit,isOrig,origHit){var origHitFootprint,hitFootprint;origHit&&(// click needs to have started on a hit
origHitFootprint=component.getSafeHitFootprint(origHit),hitFootprint=component.getSafeHitFootprint(hit),selectionFootprint=origHitFootprint&&hitFootprint?_this.computeSelection(origHitFootprint,hitFootprint):null,selectionFootprint?component.renderSelectionFootprint(selectionFootprint):!1===selectionFootprint&&util_1.disableCursor())},hitOut:function(){selectionFootprint=null,component.unrenderSelection()},hitDone:function(){util_1.enableCursor()},interactionEnd:function(ev,isCancelled){!isCancelled&&selectionFootprint&&
// the selection will already have been rendered. just report it
_this.view.reportSelection(selectionFootprint,ev)}});return dragListener},
// Given the first and last date-spans of a selection, returns another date-span object.
// Subclasses can override and provide additional data in the span object. Will be passed to renderSelectionFootprint().
// Will return false if the selection is invalid and this should be indicated to the user.
// Will return null/undefined if a selection invalid but no error should be reported.
DateSelecting.prototype.computeSelection=function(footprint0,footprint1){var wholeFootprint=this.computeSelectionFootprint(footprint0,footprint1);return!(wholeFootprint&&!this.isSelectionFootprintAllowed(wholeFootprint))&&wholeFootprint},
// Given two spans, must return the combination of the two.
// TODO: do this separation of concerns (combining VS validation) for event dnd/resize too.
// Assumes both footprints are non-open-ended.
DateSelecting.prototype.computeSelectionFootprint=function(footprint0,footprint1){var ms=[footprint0.unzonedRange.startMs,footprint0.unzonedRange.endMs,footprint1.unzonedRange.startMs,footprint1.unzonedRange.endMs];return ms.sort(util_1.compareNumbers),new ComponentFootprint_1.default(new UnzonedRange_1.default(ms[0],ms[3]),footprint0.isAllDay)},DateSelecting.prototype.isSelectionFootprintAllowed=function(componentFootprint){return this.component.dateProfile.validUnzonedRange.containsRange(componentFootprint.unzonedRange)&&this.view.calendar.constraints.isSelectionFootprintAllowed(componentFootprint)},DateSelecting}(Interaction_1.default);exports.default=DateSelecting},
/* 237 */
/***/function(module,exports,__nested_webpack_require_510158__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_510158__(2),HitDragListener_1=__nested_webpack_require_510158__(17),Interaction_1=__nested_webpack_require_510158__(14),DateClicking=/** @class */function(_super){
/*
    component must implement:
      - bindDateHandlerToEl
      - getSafeHitFootprint
      - getHitEl
    */
function DateClicking(component){var _this=_super.call(this,component)||this;return _this.dragListener=_this.buildDragListener(),_this}return tslib_1.__extends(DateClicking,_super),DateClicking.prototype.end=function(){this.dragListener.endInteraction()},DateClicking.prototype.bindToEl=function(el){var component=this.component,dragListener=this.dragListener;component.bindDateHandlerToEl(el,"mousedown",(function(ev){component.shouldIgnoreMouse()||dragListener.startInteraction(ev)})),component.bindDateHandlerToEl(el,"touchstart",(function(ev){component.shouldIgnoreTouch()||dragListener.startInteraction(ev)}))},
// Creates a listener that tracks the user's drag across day elements, for day clicking.
DateClicking.prototype.buildDragListener=function(){var dayClickHit,_this=this,component=this.component,dragListener=new HitDragListener_1.default(component,{scroll:this.opt("dragScroll"),interactionStart:function(){dayClickHit=dragListener.origHit},hitOver:function(hit,isOrig,origHit){
// if user dragged to another cell at any point, it can no longer be a dayClick
isOrig||(dayClickHit=null)},hitOut:function(){dayClickHit=null},interactionEnd:function(ev,isCancelled){var componentFootprint;!isCancelled&&dayClickHit&&(componentFootprint=component.getSafeHitFootprint(dayClickHit),componentFootprint&&_this.view.triggerDayClick(componentFootprint,component.getHitEl(dayClickHit),ev))}});
// because dragListener won't be called with any time delay, "dragging" will begin immediately,
// which will kill any touchmoving/scrolling. Prevent this.
return dragListener.shouldCancelTouchScroll=!1,dragListener.scrollAlwaysKills=!0,dragListener},DateClicking}(Interaction_1.default);exports.default=DateClicking},
/* 238 */
/***/function(module,exports,__nested_webpack_require_513228__){Object.defineProperty(exports,"__esModule",{value:!0});var agendaTimeGridMethods,agendaDayGridMethods,tslib_1=__nested_webpack_require_513228__(2),moment=__nested_webpack_require_513228__(0),$=__nested_webpack_require_513228__(3),util_1=__nested_webpack_require_513228__(4),Scroller_1=__nested_webpack_require_513228__(41),View_1=__nested_webpack_require_513228__(43),TimeGrid_1=__nested_webpack_require_513228__(239),DayGrid_1=__nested_webpack_require_513228__(66),AGENDA_ALL_DAY_EVENT_LIMIT=5,AgendaView=/** @class */function(_super){function AgendaView(calendar,viewSpec){var _this=_super.call(this,calendar,viewSpec)||this;return _this.usesMinMaxTime=!0,// indicates that minTime/maxTime affects rendering
_this.timeGrid=_this.instantiateTimeGrid(),_this.addChild(_this.timeGrid),_this.opt("allDaySlot")&&(// should we display the "all-day" area?
_this.dayGrid=_this.instantiateDayGrid(),// the all-day subcomponent of this view
_this.addChild(_this.dayGrid)),_this.scroller=new Scroller_1.default({overflowX:"hidden",overflowY:"auto"}),_this}
// Instantiates the TimeGrid object this view needs. Draws from this.timeGridClass
return tslib_1.__extends(AgendaView,_super),AgendaView.prototype.instantiateTimeGrid=function(){var timeGrid=new this.timeGridClass(this);return util_1.copyOwnProps(agendaTimeGridMethods,timeGrid),timeGrid},
// Instantiates the DayGrid object this view might need. Draws from this.dayGridClass
AgendaView.prototype.instantiateDayGrid=function(){var dayGrid=new this.dayGridClass(this);return util_1.copyOwnProps(agendaDayGridMethods,dayGrid),dayGrid},
/* Rendering
    ------------------------------------------------------------------------------------------------------------------*/
AgendaView.prototype.renderSkeleton=function(){var timeGridWrapEl,timeGridEl;this.el.addClass("fc-agenda-view").html(this.renderSkeletonHtml()),this.scroller.render(),timeGridWrapEl=this.scroller.el.addClass("fc-time-grid-container"),timeGridEl=$('<div class="fc-time-grid" />').appendTo(timeGridWrapEl),this.el.find(".fc-body > tr > td").append(timeGridWrapEl),this.timeGrid.headContainerEl=this.el.find(".fc-head-container"),this.timeGrid.setElement(timeGridEl),this.dayGrid&&(this.dayGrid.setElement(this.el.find(".fc-day-grid")),
// have the day-grid extend it's coordinate area over the <hr> dividing the two grids
this.dayGrid.bottomCoordPadding=this.dayGrid.el.next("hr").outerHeight())},AgendaView.prototype.unrenderSkeleton=function(){this.timeGrid.removeElement(),this.dayGrid&&this.dayGrid.removeElement(),this.scroller.destroy()},
// Builds the HTML skeleton for the view.
// The day-grid and time-grid components will render inside containers defined by this HTML.
AgendaView.prototype.renderSkeletonHtml=function(){var theme=this.calendar.theme;return'<table class="'+theme.getClass("tableGrid")+'">'+(this.opt("columnHeader")?'<thead class="fc-head"><tr><td class="fc-head-container '+theme.getClass("widgetHeader")+'">&nbsp;</td></tr></thead>':"")+'<tbody class="fc-body"><tr><td class="'+theme.getClass("widgetContent")+'">'+(this.dayGrid?'<div class="fc-day-grid"/><hr class="fc-divider '+theme.getClass("widgetHeader")+'"/>':"")+"</td></tr></tbody></table>"},
// Generates an HTML attribute string for setting the width of the axis, if it is known
AgendaView.prototype.axisStyleAttr=function(){return null!=this.axisWidth?'style="width:'+this.axisWidth+'px"':""},
/* Now Indicator
    ------------------------------------------------------------------------------------------------------------------*/
AgendaView.prototype.getNowIndicatorUnit=function(){return this.timeGrid.getNowIndicatorUnit()},
/* Dimensions
    ------------------------------------------------------------------------------------------------------------------*/
// Adjusts the vertical dimensions of the view to the specified values
AgendaView.prototype.updateSize=function(totalHeight,isAuto,isResize){var eventLimit,scrollerHeight,scrollbarWidths;
// hack to give the view some height prior to timeGrid's columns being rendered
// TODO: separate setting height from scroller VS timeGrid.
if(_super.prototype.updateSize.call(this,totalHeight,isAuto,isResize),
// make all axis cells line up, and record the width so newly created axis cells will have it
this.axisWidth=util_1.matchCellWidths(this.el.find(".fc-axis")),this.timeGrid.colEls){
// set of fake row elements that must compensate when scroller has scrollbars
var noScrollRowEls=this.el.find(".fc-row:not(.fc-scroller *)");
// reset all dimensions back to the original state
this.timeGrid.bottomRuleEl.hide(),// .show() will be called later if this <hr> is necessary
this.scroller.clear(),// sets height to 'auto' and clears overflow
util_1.uncompensateScroll(noScrollRowEls),
// limit number of events in the all-day area
this.dayGrid&&(this.dayGrid.removeSegPopover(),// kill the "more" popover if displayed
eventLimit=this.opt("eventLimit"),eventLimit&&"number"!==typeof eventLimit&&(eventLimit=AGENDA_ALL_DAY_EVENT_LIMIT),eventLimit&&this.dayGrid.limitRows(eventLimit)),isAuto||(// should we force dimensions of the scroll container?
scrollerHeight=this.computeScrollerHeight(totalHeight),this.scroller.setHeight(scrollerHeight),scrollbarWidths=this.scroller.getScrollbarWidths(),(scrollbarWidths.left||scrollbarWidths.right)&&(// using scrollbars?
// make the all-day and header rows lines up
util_1.compensateScroll(noScrollRowEls,scrollbarWidths),
// the scrollbar compensation might have changed text flow, which might affect height, so recalculate
// and reapply the desired height to the scroller.
scrollerHeight=this.computeScrollerHeight(totalHeight),this.scroller.setHeight(scrollerHeight)),
// guarantees the same scrollbar widths
this.scroller.lockOverflow(scrollbarWidths),
// if there's any space below the slats, show the horizontal rule.
// this won't cause any new overflow, because lockOverflow already called.
this.timeGrid.getTotalSlatHeight()<scrollerHeight&&this.timeGrid.bottomRuleEl.show())}else isAuto||(scrollerHeight=this.computeScrollerHeight(totalHeight),this.scroller.setHeight(scrollerHeight))},
// given a desired total height of the view, returns what the height of the scroller should be
AgendaView.prototype.computeScrollerHeight=function(totalHeight){return totalHeight-util_1.subtractInnerElHeight(this.el,this.scroller.el);// everything that's NOT the scroller
},
/* Scroll
    ------------------------------------------------------------------------------------------------------------------*/
// Computes the initial pre-configured scroll state prior to allowing the user to change it
AgendaView.prototype.computeInitialDateScroll=function(){var scrollTime=moment.duration(this.opt("scrollTime")),top=this.timeGrid.computeTimeTop(scrollTime);
// zoom can give weird floating-point values. rather scroll a little bit further
return top=Math.ceil(top),top&&top++,{top:top}},AgendaView.prototype.queryDateScroll=function(){return{top:this.scroller.getScrollTop()}},AgendaView.prototype.applyDateScroll=function(scroll){void 0!==scroll.top&&this.scroller.setScrollTop(scroll.top)},
/* Hit Areas
    ------------------------------------------------------------------------------------------------------------------*/
// forward all hit-related method calls to the grids (dayGrid might not be defined)
AgendaView.prototype.getHitFootprint=function(hit){
// TODO: hit.component is set as a hack to identify where the hit came from
return hit.component.getHitFootprint(hit)},AgendaView.prototype.getHitEl=function(hit){
// TODO: hit.component is set as a hack to identify where the hit came from
return hit.component.getHitEl(hit)},
/* Event Rendering
    ------------------------------------------------------------------------------------------------------------------*/
AgendaView.prototype.executeEventRender=function(eventsPayload){var id,eventInstanceGroup,dayEventsPayload={},timedEventsPayload={};
// separate the events into all-day and timed
for(id in eventsPayload)eventInstanceGroup=eventsPayload[id],eventInstanceGroup.getEventDef().isAllDay()?dayEventsPayload[id]=eventInstanceGroup:timedEventsPayload[id]=eventInstanceGroup;this.timeGrid.executeEventRender(timedEventsPayload),this.dayGrid&&this.dayGrid.executeEventRender(dayEventsPayload)},
/* Dragging/Resizing Routing
    ------------------------------------------------------------------------------------------------------------------*/
// A returned value of `true` signals that a mock "helper" event has been rendered.
AgendaView.prototype.renderDrag=function(eventFootprints,seg,isTouch){var groups=groupEventFootprintsByAllDay(eventFootprints),renderedHelper=!1;return renderedHelper=this.timeGrid.renderDrag(groups.timed,seg,isTouch),this.dayGrid&&(renderedHelper=this.dayGrid.renderDrag(groups.allDay,seg,isTouch)||renderedHelper),renderedHelper},AgendaView.prototype.renderEventResize=function(eventFootprints,seg,isTouch){var groups=groupEventFootprintsByAllDay(eventFootprints);this.timeGrid.renderEventResize(groups.timed,seg,isTouch),this.dayGrid&&this.dayGrid.renderEventResize(groups.allDay,seg,isTouch)},
/* Selection
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of a selection
AgendaView.prototype.renderSelectionFootprint=function(componentFootprint){componentFootprint.isAllDay?this.dayGrid&&this.dayGrid.renderSelectionFootprint(componentFootprint):this.timeGrid.renderSelectionFootprint(componentFootprint)},AgendaView}(View_1.default);function groupEventFootprintsByAllDay(eventFootprints){var i,allDay=[],timed=[];for(i=0;i<eventFootprints.length;i++)eventFootprints[i].componentFootprint.isAllDay?allDay.push(eventFootprints[i]):timed.push(eventFootprints[i]);return{allDay:allDay,timed:timed}}
/***/exports.default=AgendaView,AgendaView.prototype.timeGridClass=TimeGrid_1.default,AgendaView.prototype.dayGridClass=DayGrid_1.default,
// Will customize the rendering behavior of the AgendaView's timeGrid
agendaTimeGridMethods={
// Generates the HTML that will go before the day-of week header cells
renderHeadIntroHtml:function(){var weekText,view=this.view,calendar=view.calendar,weekStart=calendar.msToUtcMoment(this.dateProfile.renderUnzonedRange.startMs,!0);return this.opt("weekNumbers")?(weekText=weekStart.format(this.opt("smallWeekFormat")),'<th class="fc-axis fc-week-number '+calendar.theme.getClass("widgetHeader")+'" '+view.axisStyleAttr()+">"+view.buildGotoAnchorHtml(// aside from link, important for matchCellWidths
{date:weekStart,type:"week",forceOff:this.colCnt>1},util_1.htmlEscape(weekText))+"</th>"):'<th class="fc-axis '+calendar.theme.getClass("widgetHeader")+'" '+view.axisStyleAttr()+"></th>"},
// Generates the HTML that goes before the bg of the TimeGrid slot area. Long vertical column.
renderBgIntroHtml:function(){var view=this.view;return'<td class="fc-axis '+view.calendar.theme.getClass("widgetContent")+'" '+view.axisStyleAttr()+"></td>"},
// Generates the HTML that goes before all other types of cells.
// Affects content-skeleton, helper-skeleton, highlight-skeleton for both the time-grid and day-grid.
renderIntroHtml:function(){var view=this.view;return'<td class="fc-axis" '+view.axisStyleAttr()+"></td>"}},
// Will customize the rendering behavior of the AgendaView's dayGrid
agendaDayGridMethods={
// Generates the HTML that goes before the all-day cells
renderBgIntroHtml:function(){var view=this.view;return'<td class="fc-axis '+view.calendar.theme.getClass("widgetContent")+'" '+view.axisStyleAttr()+"><span>"+// needed for matchCellWidths
view.getAllDayHtml()+"</span></td>"},
// Generates the HTML that goes before all other types of cells.
// Affects content-skeleton, helper-skeleton, highlight-skeleton for both the time-grid and day-grid.
renderIntroHtml:function(){var view=this.view;return'<td class="fc-axis" '+view.axisStyleAttr()+"></td>"}}},
/* 239 */
/***/function(module,exports,__nested_webpack_require_529338__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_529338__(2),$=__nested_webpack_require_529338__(3),moment=__nested_webpack_require_529338__(0),util_1=__nested_webpack_require_529338__(4),InteractiveDateComponent_1=__nested_webpack_require_529338__(42),BusinessHourRenderer_1=__nested_webpack_require_529338__(61),StandardInteractionsMixin_1=__nested_webpack_require_529338__(65),DayTableMixin_1=__nested_webpack_require_529338__(60),CoordCache_1=__nested_webpack_require_529338__(58),UnzonedRange_1=__nested_webpack_require_529338__(5),ComponentFootprint_1=__nested_webpack_require_529338__(12),TimeGridEventRenderer_1=__nested_webpack_require_529338__(240),TimeGridHelperRenderer_1=__nested_webpack_require_529338__(241),TimeGridFillRenderer_1=__nested_webpack_require_529338__(242),AGENDA_STOCK_SUB_DURATIONS=[{hours:1},{minutes:30},{minutes:15},{seconds:30},{seconds:15}],TimeGrid=/** @class */function(_super){function TimeGrid(view){var _this=_super.call(this,view)||this;return _this.processOptions(),_this}
// Slices up the given span (unzoned start/end with other misc data) into an array of segments
return tslib_1.__extends(TimeGrid,_super),TimeGrid.prototype.componentFootprintToSegs=function(componentFootprint){var i,segs=this.sliceRangeByTimes(componentFootprint.unzonedRange);for(i=0;i<segs.length;i++)this.isRTL?segs[i].col=this.daysPerRow-1-segs[i].dayIndex:segs[i].col=segs[i].dayIndex;return segs},
/* Date Handling
    ------------------------------------------------------------------------------------------------------------------*/
TimeGrid.prototype.sliceRangeByTimes=function(unzonedRange){var segRange,dayIndex,segs=[];for(dayIndex=0;dayIndex<this.daysPerRow;dayIndex++)segRange=unzonedRange.intersect(this.dayRanges[dayIndex]),segRange&&segs.push({startMs:segRange.startMs,endMs:segRange.endMs,isStart:segRange.isStart,isEnd:segRange.isEnd,dayIndex:dayIndex});return segs},
/* Options
    ------------------------------------------------------------------------------------------------------------------*/
// Parses various options into properties of this object
TimeGrid.prototype.processOptions=function(){var input,slotDuration=this.opt("slotDuration"),snapDuration=this.opt("snapDuration");slotDuration=moment.duration(slotDuration),snapDuration=snapDuration?moment.duration(snapDuration):slotDuration,this.slotDuration=slotDuration,this.snapDuration=snapDuration,this.snapsPerSlot=slotDuration/snapDuration,// TODO: ensure an integer multiple?
// might be an array value (for TimelineView).
// if so, getting the most granular entry (the last one probably).
input=this.opt("slotLabelFormat"),$.isArray(input)&&(input=input[input.length-1]),this.labelFormat=input||this.opt("smallTimeFormat"),// the computed default
input=this.opt("slotLabelInterval"),this.labelInterval=input?moment.duration(input):this.computeLabelInterval(slotDuration)},
// Computes an automatic value for slotLabelInterval
TimeGrid.prototype.computeLabelInterval=function(slotDuration){var i,labelInterval,slotsPerLabel;
// find the smallest stock label interval that results in more than one slots-per-label
for(i=AGENDA_STOCK_SUB_DURATIONS.length-1;i>=0;i--)if(labelInterval=moment.duration(AGENDA_STOCK_SUB_DURATIONS[i]),slotsPerLabel=util_1.divideDurationByDuration(labelInterval,slotDuration),util_1.isInt(slotsPerLabel)&&slotsPerLabel>1)return labelInterval;return moment.duration(slotDuration);// fall back. clone
},
/* Date Rendering
    ------------------------------------------------------------------------------------------------------------------*/
TimeGrid.prototype.renderDates=function(dateProfile){this.dateProfile=dateProfile,this.updateDayTable(),this.renderSlats(),this.renderColumns()},TimeGrid.prototype.unrenderDates=function(){
// this.unrenderSlats(); // don't need this because repeated .html() calls clear
this.unrenderColumns()},TimeGrid.prototype.renderSkeleton=function(){var theme=this.view.calendar.theme;this.el.html('<div class="fc-bg"></div><div class="fc-slats"></div><hr class="fc-divider '+theme.getClass("widgetHeader")+'" style="display:none" />'),this.bottomRuleEl=this.el.find("hr")},TimeGrid.prototype.renderSlats=function(){var theme=this.view.calendar.theme;this.slatContainerEl=this.el.find("> .fc-slats").html(// avoids needing ::unrenderSlats()
'<table class="'+theme.getClass("tableGrid")+'">'+this.renderSlatRowHtml()+"</table>"),this.slatEls=this.slatContainerEl.find("tr"),this.slatCoordCache=new CoordCache_1.default({els:this.slatEls,isVertical:!0})},
// Generates the HTML for the horizontal "slats" that run width-wise. Has a time axis on a side. Depends on RTL.
TimeGrid.prototype.renderSlatRowHtml=function(){var slotDate,isLabeled,axisHtml,view=this.view,calendar=view.calendar,theme=calendar.theme,isRTL=this.isRTL,dateProfile=this.dateProfile,html="",slotTime=moment.duration(+dateProfile.minTime),slotIterator=moment.duration(0);
// Calculate the time for each slot
while(slotTime<dateProfile.maxTime)slotDate=calendar.msToUtcMoment(dateProfile.renderUnzonedRange.startMs).time(slotTime),isLabeled=util_1.isInt(util_1.divideDurationByDuration(slotIterator,this.labelInterval)),axisHtml='<td class="fc-axis fc-time '+theme.getClass("widgetContent")+'" '+view.axisStyleAttr()+">"+(isLabeled?"<span>"+// for matchCellWidths
util_1.htmlEscape(slotDate.format(this.labelFormat))+"</span>":"")+"</td>",html+='<tr data-time="'+slotDate.format("HH:mm:ss")+'"'+(isLabeled?"":' class="fc-minor"')+">"+(isRTL?"":axisHtml)+'<td class="'+theme.getClass("widgetContent")+'"/>'+(isRTL?axisHtml:"")+"</tr>",slotTime.add(this.slotDuration),slotIterator.add(this.slotDuration);return html},TimeGrid.prototype.renderColumns=function(){var dateProfile=this.dateProfile,theme=this.view.calendar.theme;this.dayRanges=this.dayDates.map((function(dayDate){return new UnzonedRange_1.default(dayDate.clone().add(dateProfile.minTime),dayDate.clone().add(dateProfile.maxTime))})),this.headContainerEl&&this.headContainerEl.html(this.renderHeadHtml()),this.el.find("> .fc-bg").html('<table class="'+theme.getClass("tableGrid")+'">'+this.renderBgTrHtml(0)+// row=0
"</table>"),this.colEls=this.el.find(".fc-day, .fc-disabled-day"),this.colCoordCache=new CoordCache_1.default({els:this.colEls,isHorizontal:!0}),this.renderContentSkeleton()},TimeGrid.prototype.unrenderColumns=function(){this.unrenderContentSkeleton()},
/* Content Skeleton
    ------------------------------------------------------------------------------------------------------------------*/
// Renders the DOM that the view's content will live in
TimeGrid.prototype.renderContentSkeleton=function(){var i,skeletonEl,cellHtml="";for(i=0;i<this.colCnt;i++)cellHtml+='<td><div class="fc-content-col"><div class="fc-event-container fc-helper-container"></div><div class="fc-event-container"></div><div class="fc-highlight-container"></div><div class="fc-bgevent-container"></div><div class="fc-business-container"></div></div></td>';skeletonEl=this.contentSkeletonEl=$('<div class="fc-content-skeleton"><table><tr>'+cellHtml+"</tr></table></div>"),this.colContainerEls=skeletonEl.find(".fc-content-col"),this.helperContainerEls=skeletonEl.find(".fc-helper-container"),this.fgContainerEls=skeletonEl.find(".fc-event-container:not(.fc-helper-container)"),this.bgContainerEls=skeletonEl.find(".fc-bgevent-container"),this.highlightContainerEls=skeletonEl.find(".fc-highlight-container"),this.businessContainerEls=skeletonEl.find(".fc-business-container"),this.bookendCells(skeletonEl.find("tr")),// TODO: do this on string level
this.el.append(skeletonEl)},TimeGrid.prototype.unrenderContentSkeleton=function(){this.contentSkeletonEl&&(// defensive :(
this.contentSkeletonEl.remove(),this.contentSkeletonEl=null,this.colContainerEls=null,this.helperContainerEls=null,this.fgContainerEls=null,this.bgContainerEls=null,this.highlightContainerEls=null,this.businessContainerEls=null)},
// Given a flat array of segments, return an array of sub-arrays, grouped by each segment's col
TimeGrid.prototype.groupSegsByCol=function(segs){var i,segsByCol=[];for(i=0;i<this.colCnt;i++)segsByCol.push([]);for(i=0;i<segs.length;i++)segsByCol[segs[i].col].push(segs[i]);return segsByCol},
// Given segments grouped by column, insert the segments' elements into a parallel array of container
// elements, each living within a column.
TimeGrid.prototype.attachSegsByCol=function(segsByCol,containerEls){var col,segs,i;for(col=0;col<this.colCnt;col++)for(// iterate each column grouping
segs=segsByCol[col],i=0;i<segs.length;i++)containerEls.eq(col).append(segs[i].el)},
/* Now Indicator
    ------------------------------------------------------------------------------------------------------------------*/
TimeGrid.prototype.getNowIndicatorUnit=function(){return"minute";// will refresh on the minute
},TimeGrid.prototype.renderNowIndicator=function(date){
// HACK: if date columns not ready for some reason (scheduler)
if(this.colContainerEls){
// seg system might be overkill, but it handles scenario where line needs to be rendered
//  more than once because of columns with the same date (resources columns for example)
var i,segs=this.componentFootprintToSegs(new ComponentFootprint_1.default(new UnzonedRange_1.default(date,date.valueOf()+1),// protect against null range
!1)),top=this.computeDateTop(date,date),nodes=[];
// render lines within the columns
for(i=0;i<segs.length;i++)nodes.push($('<div class="fc-now-indicator fc-now-indicator-line"></div>').css("top",top).appendTo(this.colContainerEls.eq(segs[i].col))[0]);
// render an arrow over the axis
segs.length>0&&// is the current time in view?
nodes.push($('<div class="fc-now-indicator fc-now-indicator-arrow"></div>').css("top",top).appendTo(this.el.find(".fc-content-skeleton"))[0]),this.nowIndicatorEls=$(nodes)}},TimeGrid.prototype.unrenderNowIndicator=function(){this.nowIndicatorEls&&(this.nowIndicatorEls.remove(),this.nowIndicatorEls=null)},
/* Coordinates
    ------------------------------------------------------------------------------------------------------------------*/
TimeGrid.prototype.updateSize=function(totalHeight,isAuto,isResize){_super.prototype.updateSize.call(this,totalHeight,isAuto,isResize),this.slatCoordCache.build(),isResize&&this.updateSegVerticals([].concat(this.eventRenderer.getSegs(),this.businessSegs||[]))},TimeGrid.prototype.getTotalSlatHeight=function(){return this.slatContainerEl.outerHeight()},
// Computes the top coordinate, relative to the bounds of the grid, of the given date.
// `ms` can be a millisecond UTC time OR a UTC moment.
// A `startOfDayDate` must be given for avoiding ambiguity over how to treat midnight.
TimeGrid.prototype.computeDateTop=function(ms,startOfDayDate){return this.computeTimeTop(moment.duration(ms-startOfDayDate.clone().stripTime()))},
// Computes the top coordinate, relative to the bounds of the grid, of the given time (a Duration).
TimeGrid.prototype.computeTimeTop=function(time){var slatIndex,slatRemainder,len=this.slatEls.length,dateProfile=this.dateProfile,slatCoverage=(time-dateProfile.minTime)/this.slotDuration;
// compute a floating-point number for how many slats should be progressed through.
// from 0 to number of slats (inclusive)
// constrained because minTime/maxTime might be customized.
return slatCoverage=Math.max(0,slatCoverage),slatCoverage=Math.min(len,slatCoverage),
// an integer index of the furthest whole slat
// from 0 to number slats (*exclusive*, so len-1)
slatIndex=Math.floor(slatCoverage),slatIndex=Math.min(slatIndex,len-1),
// how much further through the slatIndex slat (from 0.0-1.0) must be covered in addition.
// could be 1.0 if slatCoverage is covering *all* the slots
slatRemainder=slatCoverage-slatIndex,this.slatCoordCache.getTopPosition(slatIndex)+this.slatCoordCache.getHeight(slatIndex)*slatRemainder},
// Refreshes the CSS top/bottom coordinates for each segment element.
// Works when called after initial render, after a window resize/zoom for example.
TimeGrid.prototype.updateSegVerticals=function(segs){this.computeSegVerticals(segs),this.assignSegVerticals(segs)},
// For each segment in an array, computes and assigns its top and bottom properties
TimeGrid.prototype.computeSegVerticals=function(segs){var i,seg,dayDate,eventMinHeight=this.opt("agendaEventMinHeight");for(i=0;i<segs.length;i++)seg=segs[i],dayDate=this.dayDates[seg.dayIndex],seg.top=this.computeDateTop(seg.startMs,dayDate),seg.bottom=Math.max(seg.top+eventMinHeight,this.computeDateTop(seg.endMs,dayDate))},
// Given segments that already have their top/bottom properties computed, applies those values to
// the segments' elements.
TimeGrid.prototype.assignSegVerticals=function(segs){var i,seg;for(i=0;i<segs.length;i++)seg=segs[i],seg.el.css(this.generateSegVerticalCss(seg))},
// Generates an object with CSS properties for the top/bottom coordinates of a segment element
TimeGrid.prototype.generateSegVerticalCss=function(seg){return{top:seg.top,bottom:-seg.bottom}},
/* Hit System
    ------------------------------------------------------------------------------------------------------------------*/
TimeGrid.prototype.prepareHits=function(){this.colCoordCache.build(),this.slatCoordCache.build()},TimeGrid.prototype.releaseHits=function(){this.colCoordCache.clear();
// NOTE: don't clear slatCoordCache because we rely on it for computeTimeTop
},TimeGrid.prototype.queryHit=function(leftOffset,topOffset){var snapsPerSlot=this.snapsPerSlot,colCoordCache=this.colCoordCache,slatCoordCache=this.slatCoordCache;if(colCoordCache.isLeftInBounds(leftOffset)&&slatCoordCache.isTopInBounds(topOffset)){var colIndex=colCoordCache.getHorizontalIndex(leftOffset),slatIndex=slatCoordCache.getVerticalIndex(topOffset);if(null!=colIndex&&null!=slatIndex){var slatTop=slatCoordCache.getTopOffset(slatIndex),slatHeight=slatCoordCache.getHeight(slatIndex),partial=(topOffset-slatTop)/slatHeight,localSnapIndex=Math.floor(partial*snapsPerSlot),snapIndex=slatIndex*snapsPerSlot+localSnapIndex,snapTop=slatTop+localSnapIndex/snapsPerSlot*slatHeight,snapBottom=slatTop+(localSnapIndex+1)/snapsPerSlot*slatHeight;return{col:colIndex,snap:snapIndex,component:this,left:colCoordCache.getLeftOffset(colIndex),right:colCoordCache.getRightOffset(colIndex),top:snapTop,bottom:snapBottom}}}},TimeGrid.prototype.getHitFootprint=function(hit){var end,start=this.getCellDate(0,hit.col),time=this.computeSnapTime(hit.snap);// row=0
return start.time(time),end=start.clone().add(this.snapDuration),new ComponentFootprint_1.default(new UnzonedRange_1.default(start,end),!1)},
// Given a row number of the grid, representing a "snap", returns a time (Duration) from its start-of-day
TimeGrid.prototype.computeSnapTime=function(snapIndex){return moment.duration(this.dateProfile.minTime+this.snapDuration*snapIndex)},TimeGrid.prototype.getHitEl=function(hit){return this.colEls.eq(hit.col)},
/* Event Drag Visualization
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of an event being dragged over the specified date(s).
// A returned value of `true` signals that a mock "helper" event has been rendered.
TimeGrid.prototype.renderDrag=function(eventFootprints,seg,isTouch){var i;if(seg){// if there is event information for this drag, render a helper event
if(eventFootprints.length)
// signal that a helper has been rendered
return this.helperRenderer.renderEventDraggingFootprints(eventFootprints,seg,isTouch),!0}else// otherwise, just render a highlight
for(i=0;i<eventFootprints.length;i++)this.renderHighlight(eventFootprints[i].componentFootprint)},
// Unrenders any visual indication of an event being dragged
TimeGrid.prototype.unrenderDrag=function(){this.unrenderHighlight(),this.helperRenderer.unrender()},
/* Event Resize Visualization
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of an event being resized
TimeGrid.prototype.renderEventResize=function(eventFootprints,seg,isTouch){this.helperRenderer.renderEventResizingFootprints(eventFootprints,seg,isTouch)},
// Unrenders any visual indication of an event being resized
TimeGrid.prototype.unrenderEventResize=function(){this.helperRenderer.unrender()},
/* Selection
    ------------------------------------------------------------------------------------------------------------------*/
// Renders a visual indication of a selection. Overrides the default, which was to simply render a highlight.
TimeGrid.prototype.renderSelectionFootprint=function(componentFootprint){this.opt("selectHelper")?// this setting signals that a mock helper event should be rendered
this.helperRenderer.renderComponentFootprint(componentFootprint):this.renderHighlight(componentFootprint)},
// Unrenders any visual indication of a selection
TimeGrid.prototype.unrenderSelection=function(){this.helperRenderer.unrender(),this.unrenderHighlight()},TimeGrid}(InteractiveDateComponent_1.default);exports.default=TimeGrid,TimeGrid.prototype.eventRendererClass=TimeGridEventRenderer_1.default,TimeGrid.prototype.businessHourRendererClass=BusinessHourRenderer_1.default,TimeGrid.prototype.helperRendererClass=TimeGridHelperRenderer_1.default,TimeGrid.prototype.fillRendererClass=TimeGridFillRenderer_1.default,StandardInteractionsMixin_1.default.mixInto(TimeGrid),DayTableMixin_1.default.mixInto(TimeGrid)},
/* 240 */
/***/function(module,exports,__nested_webpack_require_553516__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_553516__(2),util_1=__nested_webpack_require_553516__(4),EventRenderer_1=__nested_webpack_require_553516__(44),TimeGridEventRenderer=/** @class */function(_super){function TimeGridEventRenderer(timeGrid,fillRenderer){var _this=_super.call(this,timeGrid,fillRenderer)||this;return _this.timeGrid=timeGrid,_this}return tslib_1.__extends(TimeGridEventRenderer,_super),TimeGridEventRenderer.prototype.renderFgSegs=function(segs){this.renderFgSegsIntoContainers(segs,this.timeGrid.fgContainerEls)},
// Given an array of foreground segments, render a DOM element for each, computes position,
// and attaches to the column inner-container elements.
TimeGridEventRenderer.prototype.renderFgSegsIntoContainers=function(segs,containerEls){var segsByCol,col;for(segsByCol=this.timeGrid.groupSegsByCol(segs),col=0;col<this.timeGrid.colCnt;col++)this.updateFgSegCoords(segsByCol[col]);this.timeGrid.attachSegsByCol(segsByCol,containerEls)},TimeGridEventRenderer.prototype.unrenderFgSegs=function(){this.fgSegs&&// hack
this.fgSegs.forEach((function(seg){seg.el.remove()}))},
// Computes a default event time formatting string if `timeFormat` is not explicitly defined
TimeGridEventRenderer.prototype.computeEventTimeFormat=function(){return this.opt("noMeridiemTimeFormat");// like "6:30" (no AM/PM)
},
// Computes a default `displayEventEnd` value if one is not expliclty defined
TimeGridEventRenderer.prototype.computeDisplayEventEnd=function(){return!0},
// Renders the HTML for a single event segment's default rendering
TimeGridEventRenderer.prototype.fgSegHtml=function(seg,disableResizing){var timeText,fullTimeText,startTimeText,view=this.view,calendar=view.calendar,componentFootprint=seg.footprint.componentFootprint,isAllDay=componentFootprint.isAllDay,eventDef=seg.footprint.eventDef,isDraggable=view.isEventDefDraggable(eventDef),isResizableFromStart=!disableResizing&&seg.isStart&&view.isEventDefResizableFromStart(eventDef),isResizableFromEnd=!disableResizing&&seg.isEnd&&view.isEventDefResizableFromEnd(eventDef),classes=this.getSegClasses(seg,isDraggable,isResizableFromStart||isResizableFromEnd),skinCss=util_1.cssToStr(this.getSkinCss(eventDef));
// if the event appears to span more than one day...
if(// just the start time text
classes.unshift("fc-time-grid-event","fc-v-event"),view.isMultiDayRange(componentFootprint.unzonedRange)){
// Don't display time text on segments that run entirely through a day.
// That would appear as midnight-midnight and would look dumb.
// Otherwise, display the time text for the *segment's* times (like 6pm-midnight or midnight-10am)
if(seg.isStart||seg.isEnd){var zonedStart=calendar.msToMoment(seg.startMs),zonedEnd=calendar.msToMoment(seg.endMs);timeText=this._getTimeText(zonedStart,zonedEnd,isAllDay),fullTimeText=this._getTimeText(zonedStart,zonedEnd,isAllDay,"LT"),startTimeText=this._getTimeText(zonedStart,zonedEnd,isAllDay,null,!1)}}else
// Display the normal time text for the *event's* times
timeText=this.getTimeText(seg.footprint),fullTimeText=this.getTimeText(seg.footprint,"LT"),startTimeText=this.getTimeText(seg.footprint,null,!1);return'<a class="'+classes.join(" ")+'"'+(eventDef.url?' href="'+util_1.htmlEscape(eventDef.url)+'"':"")+(skinCss?' style="'+skinCss+'"':"")+'><div class="fc-content">'+(timeText?'<div class="fc-time" data-start="'+util_1.htmlEscape(startTimeText)+'" data-full="'+util_1.htmlEscape(fullTimeText)+'"><span>'+util_1.htmlEscape(timeText)+"</span></div>":"")+(eventDef.title?'<div class="fc-title">'+util_1.htmlEscape(eventDef.title)+"</div>":"")+'</div><div class="fc-bg"/>'+(
/* TODO: write CSS for this
            (isResizableFromStart ?
              '<div class="fc-resizer fc-start-resizer" />' :
              ''
              ) +
            */
isResizableFromEnd?'<div class="fc-resizer fc-end-resizer" />':"")+"</a>"},
// Given segments that are assumed to all live in the *same column*,
// compute their verical/horizontal coordinates and assign to their elements.
TimeGridEventRenderer.prototype.updateFgSegCoords=function(segs){this.timeGrid.computeSegVerticals(segs),// horizontals relies on this
this.computeFgSegHorizontals(segs),// compute horizontal coordinates, z-index's, and reorder the array
this.timeGrid.assignSegVerticals(segs),this.assignFgSegHorizontals(segs)},
// Given an array of segments that are all in the same column, sets the backwardCoord and forwardCoord on each.
// NOTE: Also reorders the given array by date!
TimeGridEventRenderer.prototype.computeFgSegHorizontals=function(segs){var levels,level0,i;if(this.sortEventSegs(segs),// order by certain criteria
levels=buildSlotSegLevels(segs),computeForwardSlotSegs(levels),level0=levels[0]){for(i=0;i<level0.length;i++)computeSlotSegPressures(level0[i]);for(i=0;i<level0.length;i++)this.computeFgSegForwardBack(level0[i],0,0)}},
// Calculate seg.forwardCoord and seg.backwardCoord for the segment, where both values range
// from 0 to 1. If the calendar is left-to-right, the seg.backwardCoord maps to "left" and
// seg.forwardCoord maps to "right" (via percentage). Vice-versa if the calendar is right-to-left.
// The segment might be part of a "series", which means consecutive segments with the same pressure
// who's width is unknown until an edge has been hit. `seriesBackwardPressure` is the number of
// segments behind this one in the current series, and `seriesBackwardCoord` is the starting
// coordinate of the first segment in the series.
TimeGridEventRenderer.prototype.computeFgSegForwardBack=function(seg,seriesBackwardPressure,seriesBackwardCoord){var i,forwardSegs=seg.forwardSegs;if(void 0===seg.forwardCoord)// # of segments in the series
// use this segment's coordinates to computed the coordinates of the less-pressurized
// forward segments
for(// not already computed
forwardSegs.length?(
// sort highest pressure first
this.sortForwardSegs(forwardSegs),
// this segment's forwardCoord will be calculated from the backwardCoord of the
// highest-pressure forward segment.
this.computeFgSegForwardBack(forwardSegs[0],seriesBackwardPressure+1,seriesBackwardCoord),seg.forwardCoord=forwardSegs[0].backwardCoord):
// if there are no forward segments, this segment should butt up against the edge
seg.forwardCoord=1,
// calculate the backwardCoord from the forwardCoord. consider the series
seg.backwardCoord=seg.forwardCoord-(seg.forwardCoord-seriesBackwardCoord)/(// available width for series
seriesBackwardPressure+1),i=0;i<forwardSegs.length;i++)this.computeFgSegForwardBack(forwardSegs[i],0,seg.forwardCoord)},TimeGridEventRenderer.prototype.sortForwardSegs=function(forwardSegs){forwardSegs.sort(util_1.proxy(this,"compareForwardSegs"))},
// A cmp function for determining which forward segment to rely on more when computing coordinates.
TimeGridEventRenderer.prototype.compareForwardSegs=function(seg1,seg2){
// put higher-pressure first
return seg2.forwardPressure-seg1.forwardPressure||
// put segments that are closer to initial edge first (and favor ones with no coords yet)
(seg1.backwardCoord||0)-(seg2.backwardCoord||0)||
// do normal sorting...
this.compareEventSegs(seg1,seg2)},
// Given foreground event segments that have already had their position coordinates computed,
// assigns position-related CSS values to their elements.
TimeGridEventRenderer.prototype.assignFgSegHorizontals=function(segs){var i,seg;for(i=0;i<segs.length;i++)seg=segs[i],seg.el.css(this.generateFgSegHorizontalCss(seg)),
// if the event is short that the title will be cut off,
// attach a className that condenses the title into the time area.
seg.footprint.eventDef.title&&seg.bottom-seg.top<30&&seg.el.addClass("fc-short")},
// Generates an object with CSS properties/values that should be applied to an event segment element.
// Contains important positioning-related properties that should be applied to any event element, customized or not.
TimeGridEventRenderer.prototype.generateFgSegHorizontalCss=function(seg){var left,right,shouldOverlap=this.opt("slotEventOverlap"),backwardCoord=seg.backwardCoord,forwardCoord=seg.forwardCoord,props=this.timeGrid.generateSegVerticalCss(seg),isRTL=this.timeGrid.isRTL;// amount of space from right edge, a fraction of the total width
return shouldOverlap&&(
// double the width, but don't go beyond the maximum forward coordinate (1.0)
forwardCoord=Math.min(1,backwardCoord+2*(forwardCoord-backwardCoord))),isRTL?(left=1-forwardCoord,right=backwardCoord):(left=backwardCoord,right=1-forwardCoord),props.zIndex=seg.level+1,// convert from 0-base to 1-based
props.left=100*left+"%",props.right=100*right+"%",shouldOverlap&&seg.forwardPressure&&(
// add padding to the edge so that forward stacked events don't cover the resizer's icon
props[isRTL?"marginLeft":"marginRight"]=20),props},TimeGridEventRenderer}(EventRenderer_1.default);
// Builds an array of segments "levels". The first level will be the leftmost tier of segments if the calendar is
// left-to-right, or the rightmost if the calendar is right-to-left. Assumes the segments are already ordered by date.
function buildSlotSegLevels(segs){var i,seg,j,levels=[];for(i=0;i<segs.length;i++){
// go through all the levels and stop on the first level where there are no collisions
for(seg=segs[i],j=0;j<levels.length;j++)if(!computeSlotSegCollisions(seg,levels[j]).length)break;seg.level=j,(levels[j]||(levels[j]=[])).push(seg)}return levels}
// For every segment, figure out the other segments that are in subsequent
// levels that also occupy the same vertical space. Accumulate in seg.forwardSegs
function computeForwardSlotSegs(levels){var i,level,j,seg,k;for(i=0;i<levels.length;i++)for(level=levels[i],j=0;j<level.length;j++)for(seg=level[j],seg.forwardSegs=[],k=i+1;k<levels.length;k++)computeSlotSegCollisions(seg,levels[k],seg.forwardSegs)}
// Figure out which path forward (via seg.forwardSegs) results in the longest path until
// the furthest edge is reached. The number of segments in this path will be seg.forwardPressure
function computeSlotSegPressures(seg){var i,forwardSeg,forwardSegs=seg.forwardSegs,forwardPressure=0;if(void 0===seg.forwardPressure){// not already computed
for(i=0;i<forwardSegs.length;i++)forwardSeg=forwardSegs[i],
// figure out the child's maximum forward path
computeSlotSegPressures(forwardSeg),
// either use the existing maximum, or use the child's forward pressure
// plus one (for the forwardSeg itself)
forwardPressure=Math.max(forwardPressure,1+forwardSeg.forwardPressure);seg.forwardPressure=forwardPressure}}
// Find all the segments in `otherSegs` that vertically collide with `seg`.
// Append into an optionally-supplied `results` array and return.
function computeSlotSegCollisions(seg,otherSegs,results){void 0===results&&(results=[]);for(var i=0;i<otherSegs.length;i++)isSlotSegCollision(seg,otherSegs[i])&&results.push(otherSegs[i]);return results}
// Do these segments occupy the same vertical space?
function isSlotSegCollision(seg1,seg2){return seg1.bottom>seg2.top&&seg1.top<seg2.bottom}
/***/exports.default=TimeGridEventRenderer},
/* 241 */
/***/function(module,exports,__nested_webpack_require_568911__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_568911__(2),$=__nested_webpack_require_568911__(3),HelperRenderer_1=__nested_webpack_require_568911__(63),TimeGridHelperRenderer=/** @class */function(_super){function TimeGridHelperRenderer(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(TimeGridHelperRenderer,_super),TimeGridHelperRenderer.prototype.renderSegs=function(segs,sourceSeg){var i,seg,sourceEl,helperNodes=[];
// Try to make the segment that is in the same row as sourceSeg look the same
for(
// TODO: not good to call eventRenderer this way
this.eventRenderer.renderFgSegsIntoContainers(segs,this.component.helperContainerEls),i=0;i<segs.length;i++)seg=segs[i],sourceSeg&&sourceSeg.col===seg.col&&(sourceEl=sourceSeg.el,seg.el.css({left:sourceEl.css("left"),right:sourceEl.css("right"),"margin-left":sourceEl.css("margin-left"),"margin-right":sourceEl.css("margin-right")})),helperNodes.push(seg.el[0]);return $(helperNodes);// must return the elements rendered
},TimeGridHelperRenderer}(HelperRenderer_1.default);exports.default=TimeGridHelperRenderer},
/* 242 */
/***/function(module,exports,__nested_webpack_require_570539__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_570539__(2),FillRenderer_1=__nested_webpack_require_570539__(62),TimeGridFillRenderer=/** @class */function(_super){function TimeGridFillRenderer(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(TimeGridFillRenderer,_super),TimeGridFillRenderer.prototype.attachSegEls=function(type,segs){var containerEls,timeGrid=this.component;
// TODO: more efficient lookup
return"bgEvent"===type?containerEls=timeGrid.bgContainerEls:"businessHours"===type?containerEls=timeGrid.businessContainerEls:"highlight"===type&&(containerEls=timeGrid.highlightContainerEls),timeGrid.updateSegVerticals(segs),timeGrid.attachSegsByCol(timeGrid.groupSegsByCol(segs),containerEls),segs.map((function(seg){return seg.el[0]}))},TimeGridFillRenderer}(FillRenderer_1.default);exports.default=TimeGridFillRenderer},
/* 243 */
/***/function(module,exports,__nested_webpack_require_571837__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_571837__(2),$=__nested_webpack_require_571837__(3),util_1=__nested_webpack_require_571837__(4),EventRenderer_1=__nested_webpack_require_571837__(44),DayGridEventRenderer=/** @class */function(_super){function DayGridEventRenderer(dayGrid,fillRenderer){var _this=_super.call(this,dayGrid,fillRenderer)||this;return _this.dayGrid=dayGrid,_this}return tslib_1.__extends(DayGridEventRenderer,_super),DayGridEventRenderer.prototype.renderBgRanges=function(eventRanges){
// don't render timed background events
eventRanges=$.grep(eventRanges,(function(eventRange){return eventRange.eventDef.isAllDay()})),_super.prototype.renderBgRanges.call(this,eventRanges)},
// Renders the given foreground event segments onto the grid
DayGridEventRenderer.prototype.renderFgSegs=function(segs){var rowStructs=this.rowStructs=this.renderSegRows(segs);
// append to each row's content skeleton
this.dayGrid.rowEls.each((function(i,rowNode){$(rowNode).find(".fc-content-skeleton > table").append(rowStructs[i].tbodyEl)}))},
// Unrenders all currently rendered foreground event segments
DayGridEventRenderer.prototype.unrenderFgSegs=function(){var rowStruct,rowStructs=this.rowStructs||[];while(rowStruct=rowStructs.pop())rowStruct.tbodyEl.remove();this.rowStructs=null},
// Uses the given events array to generate <tbody> elements that should be appended to each row's content skeleton.
// Returns an array of rowStruct objects (see the bottom of `renderSegRow`).
// PRECONDITION: each segment shoud already have a rendered and assigned `.el`
DayGridEventRenderer.prototype.renderSegRows=function(segs){var segRows,row,rowStructs=[];// group into nested arrays
// iterate each row of segment groupings
for(segRows=this.groupSegRows(segs),row=0;row<segRows.length;row++)rowStructs.push(this.renderSegRow(row,segRows[row]));return rowStructs},
// Given a row # and an array of segments all in the same row, render a <tbody> element, a skeleton that contains
// the segments. Returns object with a bunch of internal data about how the render was calculated.
// NOTE: modifies rowSegs
DayGridEventRenderer.prototype.renderSegRow=function(row,rowSegs){var i,levelSegs,col,tr,j,seg,td,colCnt=this.dayGrid.colCnt,segLevels=this.buildSegLevels(rowSegs),levelCnt=Math.max(1,segLevels.length),tbody=$("<tbody/>"),segMatrix=[],cellMatrix=[],loneCellMatrix=[];
// populates empty cells from the current column (`col`) to `endCol`
function emptyCellsUntil(endCol){while(col<endCol)
// try to grab a cell from the level above and extend its rowspan. otherwise, create a fresh cell
td=(loneCellMatrix[i-1]||[])[col],td?td.attr("rowspan",parseInt(td.attr("rowspan")||1,10)+1):(td=$("<td/>"),tr.append(td)),cellMatrix[i][col]=td,loneCellMatrix[i][col]=td,col++}for(i=0;i<levelCnt;i++){
// levelCnt might be 1 even though there are no actual levels. protect against this.
// this single empty row is useful for styling.
if(// iterate through all levels
levelSegs=segLevels[i],col=0,tr=$("<tr/>"),segMatrix.push([]),cellMatrix.push([]),loneCellMatrix.push([]),levelSegs)for(j=0;j<levelSegs.length;j++){// iterate through segments in level
seg=levelSegs[j],emptyCellsUntil(seg.leftCol),
// create a container that occupies or more columns. append the event element.
td=$('<td class="fc-event-container"/>').append(seg.el),seg.leftCol!==seg.rightCol?td.attr("colspan",seg.rightCol-seg.leftCol+1):// a single-column segment
loneCellMatrix[i][col]=td;while(col<=seg.rightCol)cellMatrix[i][col]=td,segMatrix[i][col]=seg,col++;tr.append(td)}emptyCellsUntil(colCnt),// finish off the row
this.dayGrid.bookendCells(tr),tbody.append(tr)}return{row:row,tbodyEl:tbody,cellMatrix:cellMatrix,segMatrix:segMatrix,segLevels:segLevels,segs:rowSegs}},
// Stacks a flat array of segments, which are all assumed to be in the same row, into subarrays of vertical levels.
// NOTE: modifies segs
DayGridEventRenderer.prototype.buildSegLevels=function(segs){var i,seg,j,levels=[];for(
// Give preference to elements with certain criteria, so they have
// a chance to be closer to the top.
this.sortEventSegs(segs),i=0;i<segs.length;i++){
// loop through levels, starting with the topmost, until the segment doesn't collide with other segments
for(seg=segs[i],j=0;j<levels.length;j++)if(!isDaySegCollision(seg,levels[j]))break;
// `j` now holds the desired subrow index
seg.level=j,
// create new level array if needed and append segment
(levels[j]||(levels[j]=[])).push(seg)}
// order segments left-to-right. very important if calendar is RTL
for(j=0;j<levels.length;j++)levels[j].sort(compareDaySegCols);return levels},
// Given a flat array of segments, return an array of sub-arrays, grouped by each segment's row
DayGridEventRenderer.prototype.groupSegRows=function(segs){var i,segRows=[];for(i=0;i<this.dayGrid.rowCnt;i++)segRows.push([]);for(i=0;i<segs.length;i++)segRows[segs[i].row].push(segs[i]);return segRows},
// Computes a default event time formatting string if `timeFormat` is not explicitly defined
DayGridEventRenderer.prototype.computeEventTimeFormat=function(){return this.opt("extraSmallTimeFormat");// like "6p" or "6:30p"
},
// Computes a default `displayEventEnd` value if one is not expliclty defined
DayGridEventRenderer.prototype.computeDisplayEventEnd=function(){return 1===this.dayGrid.colCnt;// we'll likely have space if there's only one day
},
// Builds the HTML to be used for the default element for an individual segment
DayGridEventRenderer.prototype.fgSegHtml=function(seg,disableResizing){var timeText,titleHtml,view=this.view,eventDef=seg.footprint.eventDef,isAllDay=seg.footprint.componentFootprint.isAllDay,isDraggable=view.isEventDefDraggable(eventDef),isResizableFromStart=!disableResizing&&isAllDay&&seg.isStart&&view.isEventDefResizableFromStart(eventDef),isResizableFromEnd=!disableResizing&&isAllDay&&seg.isEnd&&view.isEventDefResizableFromEnd(eventDef),classes=this.getSegClasses(seg,isDraggable,isResizableFromStart||isResizableFromEnd),skinCss=util_1.cssToStr(this.getSkinCss(eventDef)),timeHtml="";return classes.unshift("fc-day-grid-event","fc-h-event"),
// Only display a timed events time if it is the starting segment
seg.isStart&&(timeText=this.getTimeText(seg.footprint),timeText&&(timeHtml='<span class="fc-time">'+util_1.htmlEscape(timeText)+"</span>")),titleHtml='<span class="fc-title">'+(util_1.htmlEscape(eventDef.title||"")||"&nbsp;")+// we always want one line of height
"</span>",'<a class="'+classes.join(" ")+'"'+(eventDef.url?' href="'+util_1.htmlEscape(eventDef.url)+'"':"")+(skinCss?' style="'+skinCss+'"':"")+'><div class="fc-content">'+(this.dayGrid.isRTL?titleHtml+" "+timeHtml:// put a natural space in between
timeHtml+" "+titleHtml)+"</div>"+(isResizableFromStart?'<div class="fc-resizer fc-start-resizer" />':"")+(isResizableFromEnd?'<div class="fc-resizer fc-end-resizer" />':"")+"</a>"},DayGridEventRenderer}(EventRenderer_1.default);
// Computes whether two segments' columns collide. They are assumed to be in the same row.
function isDaySegCollision(seg,otherSegs){var i,otherSeg;for(i=0;i<otherSegs.length;i++)if(otherSeg=otherSegs[i],otherSeg.leftCol<=seg.rightCol&&otherSeg.rightCol>=seg.leftCol)return!0;return!1}
// A cmp function for determining the leftmost event
function compareDaySegCols(a,b){return a.leftCol-b.leftCol}
/***/exports.default=DayGridEventRenderer},
/* 244 */
/***/function(module,exports,__nested_webpack_require_583050__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_583050__(2),$=__nested_webpack_require_583050__(3),HelperRenderer_1=__nested_webpack_require_583050__(63),DayGridHelperRenderer=/** @class */function(_super){function DayGridHelperRenderer(){return null!==_super&&_super.apply(this,arguments)||this}
// Renders a mock "helper" event. `sourceSeg` is the associated internal segment object. It can be null.
return tslib_1.__extends(DayGridHelperRenderer,_super),DayGridHelperRenderer.prototype.renderSegs=function(segs,sourceSeg){var rowStructs,helperNodes=[];
// TODO: not good to call eventRenderer this way
return rowStructs=this.eventRenderer.renderSegRows(segs),
// inject each new event skeleton into each associated row
this.component.rowEls.each((function(row,rowNode){var skeletonTopEl,skeletonTop,rowEl=$(rowNode),skeletonEl=$('<div class="fc-helper-skeleton"><table/></div>');// the .fc-row
// If there is an original segment, match the top position. Otherwise, put it at the row's top level
sourceSeg&&sourceSeg.row===row?skeletonTop=sourceSeg.el.position().top:(skeletonTopEl=rowEl.find(".fc-content-skeleton tbody"),skeletonTopEl.length||(// when no events
skeletonTopEl=rowEl.find(".fc-content-skeleton table")),skeletonTop=skeletonTopEl.position().top),skeletonEl.css("top",skeletonTop).find("table").append(rowStructs[row].tbodyEl),rowEl.append(skeletonEl),helperNodes.push(skeletonEl[0])})),$(helperNodes);// must return the elements rendered
},DayGridHelperRenderer}(HelperRenderer_1.default);exports.default=DayGridHelperRenderer},
/* 245 */
/***/function(module,exports,__nested_webpack_require_585270__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_585270__(2),$=__nested_webpack_require_585270__(3),FillRenderer_1=__nested_webpack_require_585270__(62),DayGridFillRenderer=/** @class */function(_super){function DayGridFillRenderer(){var _this=null!==_super&&_super.apply(this,arguments)||this;// override the default tag name
return _this.fillSegTag="td",_this}return tslib_1.__extends(DayGridFillRenderer,_super),DayGridFillRenderer.prototype.attachSegEls=function(type,segs){var i,seg,skeletonEl,nodes=[];for(i=0;i<segs.length;i++)seg=segs[i],skeletonEl=this.renderFillRow(type,seg),this.component.rowEls.eq(seg.row).append(skeletonEl),nodes.push(skeletonEl[0]);return nodes},
// Generates the HTML needed for one row of a fill. Requires the seg's el to be rendered.
DayGridFillRenderer.prototype.renderFillRow=function(type,seg){var className,skeletonEl,trEl,colCnt=this.component.colCnt,startCol=seg.leftCol,endCol=seg.rightCol+1;return className="businessHours"===type?"bgevent":type.toLowerCase(),skeletonEl=$('<div class="fc-'+className+'-skeleton"><table><tr/></table></div>'),trEl=skeletonEl.find("tr"),startCol>0&&trEl.append(
// will create (startCol + 1) td's
new Array(startCol+1).join("<td/>")),trEl.append(seg.el.attr("colspan",endCol-startCol)),endCol<colCnt&&trEl.append(
// will create (colCnt - endCol) td's
new Array(colCnt-endCol+1).join("<td/>")),this.component.bookendCells(trEl),skeletonEl},DayGridFillRenderer}(FillRenderer_1.default);exports.default=DayGridFillRenderer},
/* 246 */
/***/function(module,exports,__nested_webpack_require_587584__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_587584__(2),moment=__nested_webpack_require_587584__(0),util_1=__nested_webpack_require_587584__(4),BasicView_1=__nested_webpack_require_587584__(67),MonthViewDateProfileGenerator_1=__nested_webpack_require_587584__(247),MonthView=/** @class */function(_super){function MonthView(){return null!==_super&&_super.apply(this,arguments)||this}
// Overrides the default BasicView behavior to have special multi-week auto-height logic
return tslib_1.__extends(MonthView,_super),MonthView.prototype.setGridHeight=function(height,isAuto){
// if auto, make the height of each row the height that it would be if there were 6 weeks
isAuto&&(height*=this.dayGrid.rowCnt/6),util_1.distributeHeight(this.dayGrid.rowEls,height,!isAuto)},MonthView.prototype.isDateInOtherMonth=function(date,dateProfile){return date.month()!==moment.utc(dateProfile.currentUnzonedRange.startMs).month();// TODO: optimize
},MonthView}(BasicView_1.default);exports.default=MonthView,MonthView.prototype.dateProfileGeneratorClass=MonthViewDateProfileGenerator_1.default},
/* 247 */
/***/function(module,exports,__nested_webpack_require_589195__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_589195__(2),BasicViewDateProfileGenerator_1=__nested_webpack_require_589195__(68),UnzonedRange_1=__nested_webpack_require_589195__(5),MonthViewDateProfileGenerator=/** @class */function(_super){function MonthViewDateProfileGenerator(){return null!==_super&&_super.apply(this,arguments)||this}
// Computes the date range that will be rendered.
return tslib_1.__extends(MonthViewDateProfileGenerator,_super),MonthViewDateProfileGenerator.prototype.buildRenderRange=function(currentUnzonedRange,currentRangeUnit,isRangeAllDay){var rowCnt,renderUnzonedRange=_super.prototype.buildRenderRange.call(this,currentUnzonedRange,currentRangeUnit,isRangeAllDay),start=this.msToUtcMoment(renderUnzonedRange.startMs,isRangeAllDay),end=this.msToUtcMoment(renderUnzonedRange.endMs,isRangeAllDay);
// ensure 6 weeks
return this.opt("fixedWeekCount")&&(rowCnt=Math.ceil(// could be partial weeks due to hiddenDays
end.diff(start,"weeks",!0)),end.add(6-rowCnt,"weeks")),new UnzonedRange_1.default(start,end)},MonthViewDateProfileGenerator}(BasicViewDateProfileGenerator_1.default);exports.default=MonthViewDateProfileGenerator},
/* 248 */
/***/function(module,exports,__nested_webpack_require_590736__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_590736__(2),$=__nested_webpack_require_590736__(3),util_1=__nested_webpack_require_590736__(4),UnzonedRange_1=__nested_webpack_require_590736__(5),View_1=__nested_webpack_require_590736__(43),Scroller_1=__nested_webpack_require_590736__(41),ListEventRenderer_1=__nested_webpack_require_590736__(249),ListEventPointing_1=__nested_webpack_require_590736__(250),ListView=/** @class */function(_super){function ListView(calendar,viewSpec){var _this=_super.call(this,calendar,viewSpec)||this;return _this.segSelector=".fc-list-item",// which elements accept event actions
_this.scroller=new Scroller_1.default({overflowX:"hidden",overflowY:"auto"}),_this}return tslib_1.__extends(ListView,_super),ListView.prototype.renderSkeleton=function(){this.el.addClass("fc-list-view "+this.calendar.theme.getClass("listView")),this.scroller.render(),this.scroller.el.appendTo(this.el),this.contentEl=this.scroller.scrollEl},ListView.prototype.unrenderSkeleton=function(){this.scroller.destroy();// will remove the Grid too
},ListView.prototype.updateSize=function(totalHeight,isAuto,isResize){_super.prototype.updateSize.call(this,totalHeight,isAuto,isResize),this.scroller.clear(),// sets height to 'auto' and clears overflow
isAuto||this.scroller.setHeight(this.computeScrollerHeight(totalHeight))},ListView.prototype.computeScrollerHeight=function(totalHeight){return totalHeight-util_1.subtractInnerElHeight(this.el,this.scroller.el);// everything that's NOT the scroller
},ListView.prototype.renderDates=function(dateProfile){var calendar=this.calendar,dayStart=calendar.msToUtcMoment(dateProfile.renderUnzonedRange.startMs,!0),viewEnd=calendar.msToUtcMoment(dateProfile.renderUnzonedRange.endMs,!0),dayDates=[],dayRanges=[];while(dayStart<viewEnd)dayDates.push(dayStart.clone()),dayRanges.push(new UnzonedRange_1.default(dayStart,dayStart.clone().add(1,"day"))),dayStart.add(1,"day");this.dayDates=dayDates,this.dayRanges=dayRanges},
// slices by day
ListView.prototype.componentFootprintToSegs=function(footprint){var dayIndex,segRange,seg,dayRanges=this.dayRanges,segs=[];for(dayIndex=0;dayIndex<dayRanges.length;dayIndex++)if(segRange=footprint.unzonedRange.intersect(dayRanges[dayIndex]),segRange&&(seg={startMs:segRange.startMs,endMs:segRange.endMs,isStart:segRange.isStart,isEnd:segRange.isEnd,dayIndex:dayIndex},segs.push(seg),!seg.isEnd&&!footprint.isAllDay&&dayIndex+1<dayRanges.length&&footprint.unzonedRange.endMs<dayRanges[dayIndex+1].startMs+this.nextDayThreshold)){seg.endMs=footprint.unzonedRange.endMs,seg.isEnd=!0;break}return segs},ListView.prototype.renderEmptyMessage=function(){this.contentEl.html('<div class="fc-list-empty-wrap2"><div class="fc-list-empty-wrap1"><div class="fc-list-empty">'+util_1.htmlEscape(this.opt("noEventsMessage"))+"</div></div></div>")},
// render the event segments in the view
ListView.prototype.renderSegList=function(allSegs){var dayIndex,daySegs,i,segsByDay=this.groupSegsByDay(allSegs),tableEl=$('<table class="fc-list-table '+this.calendar.theme.getClass("tableList")+'"><tbody/></table>'),tbodyEl=tableEl.find("tbody");// sparse array
for(dayIndex=0;dayIndex<segsByDay.length;dayIndex++)if(daySegs=segsByDay[dayIndex],daySegs)for(// sparse array, so might be undefined
// append a day header
tbodyEl.append(this.dayHeaderHtml(this.dayDates[dayIndex])),this.eventRenderer.sortEventSegs(daySegs),i=0;i<daySegs.length;i++)tbodyEl.append(daySegs[i].el);// append event row
this.contentEl.empty().append(tableEl)},
// Returns a sparse array of arrays, segs grouped by their dayIndex
ListView.prototype.groupSegsByDay=function(segs){var i,seg,segsByDay=[];// sparse array
for(i=0;i<segs.length;i++)seg=segs[i],(segsByDay[seg.dayIndex]||(segsByDay[seg.dayIndex]=[])).push(seg);return segsByDay},
// generates the HTML for the day headers that live amongst the event rows
ListView.prototype.dayHeaderHtml=function(dayDate){var mainFormat=this.opt("listDayFormat"),altFormat=this.opt("listDayAltFormat");return'<tr class="fc-list-heading" data-date="'+dayDate.format("YYYY-MM-DD")+'"><td class="'+(this.calendar.theme.getClass("tableListHeading")||this.calendar.theme.getClass("widgetHeader"))+'" colspan="3">'+(mainFormat?this.buildGotoAnchorHtml(dayDate,{class:"fc-list-heading-main"},util_1.htmlEscape(dayDate.format(mainFormat))):"")+(altFormat?this.buildGotoAnchorHtml(dayDate,{class:"fc-list-heading-alt"},util_1.htmlEscape(dayDate.format(altFormat))):"")+"</td></tr>"},ListView}(View_1.default);exports.default=ListView,ListView.prototype.eventRendererClass=ListEventRenderer_1.default,ListView.prototype.eventPointingClass=ListEventPointing_1.default},
/* 249 */
/***/function(module,exports,__nested_webpack_require_597791__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_597791__(2),util_1=__nested_webpack_require_597791__(4),EventRenderer_1=__nested_webpack_require_597791__(44),ListEventRenderer=/** @class */function(_super){function ListEventRenderer(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(ListEventRenderer,_super),ListEventRenderer.prototype.renderFgSegs=function(segs){segs.length?this.component.renderSegList(segs):this.component.renderEmptyMessage()},
// generates the HTML for a single event row
ListEventRenderer.prototype.fgSegHtml=function(seg){var timeHtml,view=this.view,calendar=view.calendar,theme=calendar.theme,eventFootprint=seg.footprint,eventDef=eventFootprint.eventDef,componentFootprint=eventFootprint.componentFootprint,url=eventDef.url,classes=["fc-list-item"].concat(this.getClasses(eventDef)),bgColor=this.getBgColor(eventDef);return timeHtml=componentFootprint.isAllDay?view.getAllDayHtml():view.isMultiDayRange(componentFootprint.unzonedRange)?seg.isStart||seg.isEnd?util_1.htmlEscape(this._getTimeText(calendar.msToMoment(seg.startMs),calendar.msToMoment(seg.endMs),componentFootprint.isAllDay)):view.getAllDayHtml():util_1.htmlEscape(this.getTimeText(eventFootprint)),url&&classes.push("fc-has-url"),'<tr class="'+classes.join(" ")+'">'+(this.displayEventTime?'<td class="fc-list-item-time '+theme.getClass("widgetContent")+'">'+(timeHtml||"")+"</td>":"")+'<td class="fc-list-item-marker '+theme.getClass("widgetContent")+'"><span class="fc-event-dot"'+(bgColor?' style="background-color:'+bgColor+'"':"")+'></span></td><td class="fc-list-item-title '+theme.getClass("widgetContent")+'"><a'+(url?' href="'+util_1.htmlEscape(url)+'"':"")+">"+util_1.htmlEscape(eventDef.title||"")+"</a></td></tr>"},
// like "4:00am"
ListEventRenderer.prototype.computeEventTimeFormat=function(){return this.opt("mediumTimeFormat")},ListEventRenderer}(EventRenderer_1.default);exports.default=ListEventRenderer},
/* 250 */
/***/function(module,exports,__nested_webpack_require_601047__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_601047__(2),$=__nested_webpack_require_601047__(3),EventPointing_1=__nested_webpack_require_601047__(64),ListEventPointing=/** @class */function(_super){function ListEventPointing(){return null!==_super&&_super.apply(this,arguments)||this}
// for events with a url, the whole <tr> should be clickable,
// but it's impossible to wrap with an <a> tag. simulate this.
return tslib_1.__extends(ListEventPointing,_super),ListEventPointing.prototype.handleClick=function(seg,ev){var url;_super.prototype.handleClick.call(this,seg,ev),// might prevent the default action
// not clicking on or within an <a> with an href
$(ev.target).closest("a[href]").length||(url=seg.footprint.eventDef.url,url&&!ev.isDefaultPrevented()&&(// jsEvent not cancelled in handler
window.location.href=url))},ListEventPointing}(EventPointing_1.default);exports.default=ListEventPointing},
/* 251 */
/* 252 */,
/* 253 */,
/* 254 */,
/* 255 */,
/* 256 */
/***/,function(module,exports,__nested_webpack_require_602359__){var $=__nested_webpack_require_602359__(3),exportHooks=__nested_webpack_require_602359__(18),util_1=__nested_webpack_require_602359__(4),Calendar_1=__nested_webpack_require_602359__(232);
// for intentional side-effects
__nested_webpack_require_602359__(11),__nested_webpack_require_602359__(49),__nested_webpack_require_602359__(260),__nested_webpack_require_602359__(261),__nested_webpack_require_602359__(264),__nested_webpack_require_602359__(265),__nested_webpack_require_602359__(266),__nested_webpack_require_602359__(267),$.fullCalendar=exportHooks,$.fn.fullCalendar=function(options){var args=Array.prototype.slice.call(arguments,1),res=this;// for a possible method call
// what this function will return (this jQuery object by default)
return this.each((function(i,_element){var singleRes,element=$(_element),calendar=element.data("fullCalendar");// the returned value of this single method call
// a method call
"string"===typeof options?"getCalendar"===options?i||(// first element only
res=calendar):"destroy"===options?// don't warn if no calendar object
calendar&&(calendar.destroy(),element.removeData("fullCalendar")):calendar?$.isFunction(calendar[options])?(singleRes=calendar[options].apply(calendar,args),i||(res=singleRes),"destroy"===options&&// for the destroy method, must remove Calendar object data
element.removeData("fullCalendar")):util_1.warn("'"+options+"' is an unknown FullCalendar method."):util_1.warn("Attempting to call a FullCalendar method on an element with no calendar."):calendar||(// don't initialize twice
calendar=new Calendar_1.default(element,options),element.data("fullCalendar",calendar),calendar.render())})),res},module.exports=exportHooks},
/* 257 */
/***/function(module,exports,__nested_webpack_require_604839__){Object.defineProperty(exports,"__esModule",{value:!0});var $=__nested_webpack_require_604839__(3),util_1=__nested_webpack_require_604839__(4),Toolbar=/** @class */function(){function Toolbar(calendar,toolbarOptions){this.el=null,// mirrors local `el`
this.viewsWithButtons=[],this.calendar=calendar,this.toolbarOptions=toolbarOptions}
// method to update toolbar-specific options, not calendar-wide options
return Toolbar.prototype.setToolbarOptions=function(newToolbarOptions){this.toolbarOptions=newToolbarOptions},
// can be called repeatedly and will rerender
Toolbar.prototype.render=function(){var sections=this.toolbarOptions.layout,el=this.el;sections?(el?el.empty():el=this.el=$("<div class='fc-toolbar "+this.toolbarOptions.extraClasses+"'/>"),el.append(this.renderSection("left")).append(this.renderSection("right")).append(this.renderSection("center")).append('<div class="fc-clear"/>')):this.removeElement()},Toolbar.prototype.removeElement=function(){this.el&&(this.el.remove(),this.el=null)},Toolbar.prototype.renderSection=function(position){var _this=this,calendar=this.calendar,theme=calendar.theme,optionsManager=calendar.optionsManager,viewSpecManager=calendar.viewSpecManager,sectionEl=$('<div class="fc-'+position+'"/>'),buttonStr=this.toolbarOptions.layout[position],calendarCustomButtons=optionsManager.get("customButtons")||{},calendarButtonTextOverrides=optionsManager.overrides.buttonText||{},calendarButtonText=optionsManager.get("buttonText")||{};return buttonStr&&$.each(buttonStr.split(" "),(function(i,buttonGroupStr){var groupEl,groupChildren=$(),isOnlyButtons=!0;$.each(buttonGroupStr.split(","),(function(j,buttonName){var customButtonProps,viewSpec,buttonClick,buttonIcon,buttonText,buttonInnerHtml,buttonClasses,buttonEl,buttonAriaAttr;"title"===buttonName?(groupChildren=groupChildren.add($("<h2>&nbsp;</h2>")),// we always want it to take up height
isOnlyButtons=!1):((customButtonProps=calendarCustomButtons[buttonName])?(buttonClick=function(ev){customButtonProps.click&&customButtonProps.click.call(buttonEl[0],ev)},(buttonIcon=theme.getCustomButtonIconClass(customButtonProps))||(buttonIcon=theme.getIconClass(buttonName))||(buttonText=customButtonProps.text)):(viewSpec=viewSpecManager.getViewSpec(buttonName))?(_this.viewsWithButtons.push(buttonName),buttonClick=function(){calendar.changeView(buttonName)},(buttonText=viewSpec.buttonTextOverride)||(buttonIcon=theme.getIconClass(buttonName))||(buttonText=viewSpec.buttonTextDefault)):calendar[buttonName]&&(// a calendar method
buttonClick=function(){calendar[buttonName]()},(buttonText=calendarButtonTextOverrides[buttonName])||(buttonIcon=theme.getIconClass(buttonName))||(buttonText=calendarButtonText[buttonName])),buttonClick&&(buttonClasses=["fc-"+buttonName+"-button",theme.getClass("button"),theme.getClass("stateDefault")],buttonText?(buttonInnerHtml=util_1.htmlEscape(buttonText),buttonAriaAttr=""):buttonIcon&&(buttonInnerHtml="<span class='"+buttonIcon+"'></span>",buttonAriaAttr=' aria-label="'+buttonName+'"'),buttonEl=$(// type="button" so that it doesn't submit a form
'<button type="button" class="'+buttonClasses.join(" ")+'"'+buttonAriaAttr+">"+buttonInnerHtml+"</button>").click((function(ev){
// don't process clicks for disabled buttons
buttonEl.hasClass(theme.getClass("stateDisabled"))||(buttonClick(ev),
// after the click action, if the button becomes the "active" tab, or disabled,
// it should never have a hover class, so remove it now.
(buttonEl.hasClass(theme.getClass("stateActive"))||buttonEl.hasClass(theme.getClass("stateDisabled")))&&buttonEl.removeClass(theme.getClass("stateHover")))})).mousedown((function(){
// the *down* effect (mouse pressed in).
// only on buttons that are not the "active" tab, or disabled
buttonEl.not("."+theme.getClass("stateActive")).not("."+theme.getClass("stateDisabled")).addClass(theme.getClass("stateDown"))})).mouseup((function(){
// undo the *down* effect
buttonEl.removeClass(theme.getClass("stateDown"))})).hover((function(){
// the *hover* effect.
// only on buttons that are not the "active" tab, or disabled
buttonEl.not("."+theme.getClass("stateActive")).not("."+theme.getClass("stateDisabled")).addClass(theme.getClass("stateHover"))}),(function(){
// undo the *hover* effect
buttonEl.removeClass(theme.getClass("stateHover")).removeClass(theme.getClass("stateDown"));// if mouseleave happens before mouseup
})),groupChildren=groupChildren.add(buttonEl)))})),isOnlyButtons&&groupChildren.first().addClass(theme.getClass("cornerLeft")).end().last().addClass(theme.getClass("cornerRight")).end(),groupChildren.length>1?(groupEl=$("<div/>"),isOnlyButtons&&groupEl.addClass(theme.getClass("buttonGroup")),groupEl.append(groupChildren),sectionEl.append(groupEl)):sectionEl.append(groupChildren)})),sectionEl},Toolbar.prototype.updateTitle=function(text){this.el&&this.el.find("h2").text(text)},Toolbar.prototype.activateButton=function(buttonName){this.el&&this.el.find(".fc-"+buttonName+"-button").addClass(this.calendar.theme.getClass("stateActive"))},Toolbar.prototype.deactivateButton=function(buttonName){this.el&&this.el.find(".fc-"+buttonName+"-button").removeClass(this.calendar.theme.getClass("stateActive"))},Toolbar.prototype.disableButton=function(buttonName){this.el&&this.el.find(".fc-"+buttonName+"-button").prop("disabled",!0).addClass(this.calendar.theme.getClass("stateDisabled"))},Toolbar.prototype.enableButton=function(buttonName){this.el&&this.el.find(".fc-"+buttonName+"-button").prop("disabled",!1).removeClass(this.calendar.theme.getClass("stateDisabled"))},Toolbar.prototype.getViewsWithButtons=function(){return this.viewsWithButtons},Toolbar}();exports.default=Toolbar},
/* 258 */
/***/function(module,exports,__nested_webpack_require_615826__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_615826__(2),$=__nested_webpack_require_615826__(3),util_1=__nested_webpack_require_615826__(4),options_1=__nested_webpack_require_615826__(33),locale_1=__nested_webpack_require_615826__(32),Model_1=__nested_webpack_require_615826__(51),OptionsManager=/** @class */function(_super){function OptionsManager(_calendar,overrides){var _this=_super.call(this)||this;return _this._calendar=_calendar,_this.overrides=$.extend({},overrides),// make a copy
_this.dynamicOverrides={},_this.compute(),_this}return tslib_1.__extends(OptionsManager,_super),OptionsManager.prototype.add=function(newOptionHash){var optionName,optionCnt=0;// will trigger this model's watchers
for(optionName in this.recordOverrides(newOptionHash),newOptionHash)optionCnt++;
// special-case handling of single option change.
// if only one option change, `optionName` will be its name.
if(1===optionCnt){if("height"===optionName||"contentHeight"===optionName||"aspectRatio"===optionName)// isResize=true
return void this._calendar.updateViewSize(!0);if("defaultDate"===optionName)return;// can't change date this way. use gotoDate instead
if("businessHours"===optionName)return;// this model already reacts to this
if(/^(event|select)(Overlap|Constraint|Allow)$/.test(optionName))return;// doesn't affect rendering. only interactions.
if("timezone"===optionName)return void this._calendar.view.flash("initialEvents")}
// catch-all. rerender the header and footer and rebuild/rerender the current view
this._calendar.renderHeader(),this._calendar.renderFooter(),
// even non-current views will be affected by this option change. do before rerender
// TODO: detangle
this._calendar.viewsByType={},this._calendar.reinitView()},
// Computes the flattened options hash for the calendar and assigns to `this.options`.
// Assumes this.overrides and this.dynamicOverrides have already been initialized.
OptionsManager.prototype.compute=function(){var locale,localeDefaults,isRTL,dirDefaults,rawOptions;locale=util_1.firstDefined(// explicit locale option given?
this.dynamicOverrides.locale,this.overrides.locale),localeDefaults=locale_1.localeOptionHash[locale],localeDefaults||(// explicit locale option not given or invalid?
locale=options_1.globalDefaults.locale,localeDefaults=locale_1.localeOptionHash[locale]||{}),isRTL=util_1.firstDefined(// based on options computed so far, is direction RTL?
this.dynamicOverrides.isRTL,this.overrides.isRTL,localeDefaults.isRTL,options_1.globalDefaults.isRTL),dirDefaults=isRTL?options_1.rtlDefaults:{},this.dirDefaults=dirDefaults,this.localeDefaults=localeDefaults,rawOptions=options_1.mergeOptions([options_1.globalDefaults,dirDefaults,localeDefaults,this.overrides,this.dynamicOverrides]),locale_1.populateInstanceComputableOptions(rawOptions),// fill in gaps with computed options
this.reset(rawOptions)},
// stores the new options internally, but does not rerender anything.
OptionsManager.prototype.recordOverrides=function(newOptionHash){var optionName;for(optionName in newOptionHash)this.dynamicOverrides[optionName]=newOptionHash[optionName];this._calendar.viewSpecManager.clearCache(),// the dynamic override invalidates the options in this cache, so just clear it
this.compute()},OptionsManager}(Model_1.default);exports.default=OptionsManager},
/* 259 */
/***/function(module,exports,__nested_webpack_require_620377__){Object.defineProperty(exports,"__esModule",{value:!0});var moment=__nested_webpack_require_620377__(0),$=__nested_webpack_require_620377__(3),ViewRegistry_1=__nested_webpack_require_620377__(24),util_1=__nested_webpack_require_620377__(4),options_1=__nested_webpack_require_620377__(33),locale_1=__nested_webpack_require_620377__(32),ViewSpecManager=/** @class */function(){function ViewSpecManager(optionsManager,_calendar){this.optionsManager=optionsManager,this._calendar=_calendar,this.clearCache()}return ViewSpecManager.prototype.clearCache=function(){this.viewSpecCache={}},
// Gets information about how to create a view. Will use a cache.
ViewSpecManager.prototype.getViewSpec=function(viewType){var cache=this.viewSpecCache;return cache[viewType]||(cache[viewType]=this.buildViewSpec(viewType))},
// Given a duration singular unit, like "week" or "day", finds a matching view spec.
// Preference is given to views that have corresponding buttons.
ViewSpecManager.prototype.getUnitViewSpec=function(unit){var viewTypes,i,spec;if(-1!==$.inArray(unit,util_1.unitsDesc))for(
// put views that have buttons first. there will be duplicates, but oh well
viewTypes=this._calendar.header.getViewsWithButtons(),// TODO: include footer as well?
$.each(ViewRegistry_1.viewHash,(function(viewType){viewTypes.push(viewType)})),i=0;i<viewTypes.length;i++)if(spec=this.getViewSpec(viewTypes[i]),spec&&spec.singleUnit===unit)return spec},
// Builds an object with information on how to create a given view
ViewSpecManager.prototype.buildViewSpec=function(requestedViewType){var spec,overrides,durationInput,duration,unit,viewOverrides=this.optionsManager.overrides.views||{},specChain=[],defaultsChain=[],overridesChain=[],viewType=requestedViewType;
// iterate from the specific view definition to a more general one until we hit an actual View class
while(viewType)spec=ViewRegistry_1.viewHash[viewType],overrides=viewOverrides[viewType],viewType=null,// clear. might repopulate for another iteration
"function"===typeof spec&&(// TODO: deprecate
spec={class:spec}),spec&&(specChain.unshift(spec),defaultsChain.unshift(spec.defaults||{}),durationInput=durationInput||spec.duration,viewType=viewType||spec.type),overrides&&(overridesChain.unshift(overrides),// view-specific option hashes have options at zero-level
durationInput=durationInput||overrides.duration,viewType=viewType||overrides.type);return spec=util_1.mergeProps(specChain),spec.type=requestedViewType,!!spec["class"]&&(
// fall back to top-level `duration` option
durationInput=durationInput||this.optionsManager.dynamicOverrides.duration||this.optionsManager.overrides.duration,durationInput&&(duration=moment.duration(durationInput),duration.valueOf()&&(// valid?
unit=util_1.computeDurationGreatestUnit(duration,durationInput),spec.duration=duration,spec.durationUnit=unit,
// view is a single-unit duration, like "week" or "day"
// incorporate options for this. lowest priority
1===duration.as(unit)&&(spec.singleUnit=unit,overridesChain.unshift(viewOverrides[unit]||{})))),spec.defaults=options_1.mergeOptions(defaultsChain),spec.overrides=options_1.mergeOptions(overridesChain),this.buildViewSpecOptions(spec),this.buildViewSpecButtonText(spec,requestedViewType),spec)},
// Builds and assigns a view spec's options object from its already-assigned defaults and overrides
ViewSpecManager.prototype.buildViewSpecOptions=function(spec){var optionsManager=this.optionsManager;spec.options=options_1.mergeOptions([options_1.globalDefaults,spec.defaults,optionsManager.dirDefaults,optionsManager.localeDefaults,optionsManager.overrides,spec.overrides,optionsManager.dynamicOverrides]),locale_1.populateInstanceComputableOptions(spec.options)},
// Computes and assigns a view spec's buttonText-related options
ViewSpecManager.prototype.buildViewSpecButtonText=function(spec,requestedViewType){var optionsManager=this.optionsManager;
// given an options object with a possible `buttonText` hash, lookup the buttonText for the
// requested view, falling back to a generic unit entry like "week" or "day"
function queryButtonText(options){var buttonText=options.buttonText||{};return buttonText[requestedViewType]||(
// view can decide to look up a certain key
spec.buttonTextKey?buttonText[spec.buttonTextKey]:null)||(
// a key like "month"
spec.singleUnit?buttonText[spec.singleUnit]:null)}
// highest to lowest priority
spec.buttonTextOverride=queryButtonText(optionsManager.dynamicOverrides)||queryButtonText(optionsManager.overrides)||// constructor-specified buttonText lookup hash takes precedence
spec.overrides.buttonText,// `buttonText` for view-specific options is a string
// highest to lowest priority. mirrors buildViewSpecOptions
spec.buttonTextDefault=queryButtonText(optionsManager.localeDefaults)||queryButtonText(optionsManager.dirDefaults)||spec.defaults.buttonText||// a single string. from ViewSubclass.defaults
queryButtonText(options_1.globalDefaults)||(spec.duration?this._calendar.humanizeDuration(spec.duration):null)||// like "3 days"
requestedViewType},ViewSpecManager}();exports.default=ViewSpecManager},
/* 260 */
/***/function(module,exports,__nested_webpack_require_627756__){Object.defineProperty(exports,"__esModule",{value:!0});var EventSourceParser_1=__nested_webpack_require_627756__(38),ArrayEventSource_1=__nested_webpack_require_627756__(56),FuncEventSource_1=__nested_webpack_require_627756__(223),JsonFeedEventSource_1=__nested_webpack_require_627756__(224);EventSourceParser_1.default.registerClass(ArrayEventSource_1.default),EventSourceParser_1.default.registerClass(FuncEventSource_1.default),EventSourceParser_1.default.registerClass(JsonFeedEventSource_1.default)},
/* 261 */
/***/function(module,exports,__nested_webpack_require_628326__){Object.defineProperty(exports,"__esModule",{value:!0});var ThemeRegistry_1=__nested_webpack_require_628326__(57),StandardTheme_1=__nested_webpack_require_628326__(221),JqueryUiTheme_1=__nested_webpack_require_628326__(222),Bootstrap3Theme_1=__nested_webpack_require_628326__(262),Bootstrap4Theme_1=__nested_webpack_require_628326__(263);ThemeRegistry_1.defineThemeSystem("standard",StandardTheme_1.default),ThemeRegistry_1.defineThemeSystem("jquery-ui",JqueryUiTheme_1.default),ThemeRegistry_1.defineThemeSystem("bootstrap3",Bootstrap3Theme_1.default),ThemeRegistry_1.defineThemeSystem("bootstrap4",Bootstrap4Theme_1.default)},
/* 262 */
/***/function(module,exports,__nested_webpack_require_629018__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_629018__(2),Theme_1=__nested_webpack_require_629018__(22),Bootstrap3Theme=/** @class */function(_super){function Bootstrap3Theme(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(Bootstrap3Theme,_super),Bootstrap3Theme}(Theme_1.default);exports.default=Bootstrap3Theme,Bootstrap3Theme.prototype.classes={widget:"fc-bootstrap3",tableGrid:"table-bordered",tableList:"table",tableListHeading:"active",buttonGroup:"btn-group",button:"btn btn-default",stateActive:"active",stateDisabled:"disabled",today:"alert alert-info",popover:"panel panel-default",popoverHeader:"panel-heading",popoverContent:"panel-body",
// day grid
// for left/right border color when border is inset from edges (all-day in agenda view)
// avoid `panel` class b/c don't want margins/radius. only border color.
headerRow:"panel-default",dayRow:"panel-default",
// list view
listView:"panel panel-default"},Bootstrap3Theme.prototype.baseIconClass="glyphicon",Bootstrap3Theme.prototype.iconClasses={close:"glyphicon-remove",prev:"glyphicon-chevron-left",next:"glyphicon-chevron-right",prevYear:"glyphicon-backward",nextYear:"glyphicon-forward"},Bootstrap3Theme.prototype.iconOverrideOption="bootstrapGlyphicons",Bootstrap3Theme.prototype.iconOverrideCustomButtonOption="bootstrapGlyphicon",Bootstrap3Theme.prototype.iconOverridePrefix="glyphicon-"},
/* 263 */
/***/function(module,exports,__nested_webpack_require_630781__){Object.defineProperty(exports,"__esModule",{value:!0});var tslib_1=__nested_webpack_require_630781__(2),Theme_1=__nested_webpack_require_630781__(22),Bootstrap4Theme=/** @class */function(_super){function Bootstrap4Theme(){return null!==_super&&_super.apply(this,arguments)||this}return tslib_1.__extends(Bootstrap4Theme,_super),Bootstrap4Theme}(Theme_1.default);exports.default=Bootstrap4Theme,Bootstrap4Theme.prototype.classes={widget:"fc-bootstrap4",tableGrid:"table-bordered",tableList:"table",tableListHeading:"table-active",buttonGroup:"btn-group",button:"btn btn-primary",stateActive:"active",stateDisabled:"disabled",today:"alert alert-info",popover:"card card-primary",popoverHeader:"card-header",popoverContent:"card-body",
// day grid
// for left/right border color when border is inset from edges (all-day in agenda view)
// avoid `table` class b/c don't want margins/padding/structure. only border color.
headerRow:"table-bordered",dayRow:"table-bordered",
// list view
listView:"card card-primary"},Bootstrap4Theme.prototype.baseIconClass="fa",Bootstrap4Theme.prototype.iconClasses={close:"fa-times",prev:"fa-chevron-left",next:"fa-chevron-right",prevYear:"fa-angle-double-left",nextYear:"fa-angle-double-right"},Bootstrap4Theme.prototype.iconOverrideOption="bootstrapFontAwesome",Bootstrap4Theme.prototype.iconOverrideCustomButtonOption="bootstrapFontAwesome",Bootstrap4Theme.prototype.iconOverridePrefix="fa-"},
/* 264 */
/***/function(module,exports,__nested_webpack_require_632529__){Object.defineProperty(exports,"__esModule",{value:!0});var ViewRegistry_1=__nested_webpack_require_632529__(24),BasicView_1=__nested_webpack_require_632529__(67),MonthView_1=__nested_webpack_require_632529__(246);ViewRegistry_1.defineView("basic",{class:BasicView_1.default}),ViewRegistry_1.defineView("basicDay",{type:"basic",duration:{days:1}}),ViewRegistry_1.defineView("basicWeek",{type:"basic",duration:{weeks:1}}),ViewRegistry_1.defineView("month",{class:MonthView_1.default,duration:{months:1},defaults:{fixedWeekCount:!0}})},
/* 265 */
/***/function(module,exports,__nested_webpack_require_633235__){Object.defineProperty(exports,"__esModule",{value:!0});var ViewRegistry_1=__nested_webpack_require_633235__(24),AgendaView_1=__nested_webpack_require_633235__(238);ViewRegistry_1.defineView("agenda",{class:AgendaView_1.default,defaults:{allDaySlot:!0,slotDuration:"00:30:00",slotEventOverlap:!0}}),ViewRegistry_1.defineView("agendaDay",{type:"agenda",duration:{days:1}}),ViewRegistry_1.defineView("agendaWeek",{type:"agenda",duration:{weeks:1}})},
/* 266 */
/***/function(module,exports,__nested_webpack_require_633916__){Object.defineProperty(exports,"__esModule",{value:!0});var ViewRegistry_1=__nested_webpack_require_633916__(24),ListView_1=__nested_webpack_require_633916__(248);ViewRegistry_1.defineView("list",{class:ListView_1.default,buttonTextKey:"list",defaults:{buttonText:"list",listDayFormat:"LL",noEventsMessage:"No events to display"}}),ViewRegistry_1.defineView("listDay",{type:"list",duration:{days:1},defaults:{listDayFormat:"dddd"}}),ViewRegistry_1.defineView("listWeek",{type:"list",duration:{weeks:1},defaults:{listDayFormat:"dddd",listDayAltFormat:"LL"}}),ViewRegistry_1.defineView("listMonth",{type:"list",duration:{month:1},defaults:{listDayAltFormat:"dddd"}}),ViewRegistry_1.defineView("listYear",{type:"list",duration:{year:1},defaults:{listDayAltFormat:"dddd"}})},
/* 267 */
/***/function(module,exports){Object.defineProperty(exports,"__esModule",{value:!0});
/***/}
/******/])}));
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/85001.js.map