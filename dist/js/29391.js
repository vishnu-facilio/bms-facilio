(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[29391,86020],{
/***/486020:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return KFICards}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/dashboard/widget/cards/KFICards.vue?vue&type=template&id=64e47662
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.domRerender?_c("div",{staticClass:"dragabale-card height100 kpi-card-1",style:_vm.getScale},[_vm.loading?_c("div"):[_vm.mode?_c("div",{staticClass:"settings-over"},[_c("div",{staticClass:"reading-card-conditional-formater",on:{click:function($event){_vm.visibility=!0}}},[_c("img",{attrs:{src:__webpack_require__(99469)}})]),_c("el-popover",{attrs:{placement:"right",width:"300","popper-class":"reading-color-picker",trigger:"click"}},[_c("div",[_c("div",{staticClass:"color-picker-section"},[_c("div",{staticClass:"c-picker-label"},[_vm._v(" "+_vm._s(_vm.$t("panel.layout.back_colour"))+" : ")]),_vm._l(_vm.predefineColors,(function(color,index){return _c("div",{key:index,staticClass:"color-picker-conatiner"},[_c("div",{staticClass:"color-box",class:{active:_vm.data.style.bgcolor===color},style:"background:"+color+";",on:{click:function($event){return _vm.setColor("bg",color)}}})])}))],2),_c("div",{staticClass:"color-picker-section mT20"},[_c("div",{staticClass:"c-picker-label"},[_vm._v("Text color :")]),_vm._l(_vm.predefineColors,(function(color,index){return _c("div",{key:index,staticClass:"color-picker-conatiner"},[_c("div",{staticClass:"color-box",class:{active:_vm.data.style.color===color},style:"background:"+color+";",on:{click:function($event){return _vm.setColor("color",color)}}})])}))],2)]),_vm.mode?_c("div",{staticClass:"color-choose-icon",attrs:{slot:"reference"},slot:"reference"},[_c("div",{staticClass:"reading-card-color-picker"},[_c("img",{attrs:{src:__webpack_require__(516615)}})])]):_vm._e()])],1):_vm._e(),_c("div",{staticClass:"kpi-container",style:_vm.getStyle()},[_c("div",{staticClass:"kpi-sections"},[_vm._v(" "+_vm._s(_vm.data.expressions&&_vm.data.expressions.length?_vm.data.expressions[0].actualName:"")+" ")]),this.data.hasOwnProperty("unitString")?_c("div",{staticClass:"kpi-data",style:_vm.getDisplayStyleObj(),on:{click:_vm.drilldown}},[_c("div",{staticClass:"kpi-count pointer"},[_vm._v(" "+_vm._s(this.result&&this.result.hasOwnProperty("result")?_vm.formattedResult()+" ":"--- ")+" "),_vm.data&&_vm.data.unitString?_c("span",{domProps:{innerHTML:_vm._s(_vm.data.unitString)}}):_vm._e()])]):this.result.hasOwnProperty("unitString")?_c("div",{staticClass:"kpi-data",style:_vm.getDisplayStyleObj(),on:{click:_vm.drilldown}},[_c("div",{staticClass:"kpi-count pointer"},[_vm._v(" "+_vm._s(this.result&&this.result.hasOwnProperty("result")?_vm.formattedResult():"--- ")+" "),_vm.result&&_vm.result.unitString?_c("span",{domProps:{innerHTML:_vm._s(_vm.result.unitString)}}):_vm._e()])]):_c("div",{staticClass:"kpi-data",style:_vm.getDisplayStyleObj(),on:{click:_vm.drilldown}},[_c("div",{staticClass:"kpi-count pointer"},[_vm._v(" "+_vm._s(this.result&&this.result.hasOwnProperty("result")?_vm.formattedResult():"--- ")+" ")])]),_vm.enable[_vm.widget.id]?_vm._e():_c("div",{staticClass:"kpi-label"},[_c("div",{staticClass:"kpi-period"},[_vm._v(" "+_vm._s(_vm.getdateOperators().find((function(rt){return rt.value===_vm.data.operatorId}))?_vm.getdateOperators().find((function(rt){return rt.value===_vm.data.operatorId})).label:"")+" ")])])]),_vm.visibility?_c("el-dialog",{attrs:{"custom-class":"f-kfi-card-builder fc-dialog-center-container kpi-map-setup-dilaog","append-to-body":!0,visible:_vm.visibility,width:"50%",title:"KPI CARD","before-close":_vm.closedialog},on:{"update:visible":function($event){_vm.visibility=$event}}},[_c("span",{staticClass:"dialog-footer",attrs:{slot:"title"},slot:"title"},[_c("span",{staticClass:"kpi-card-header"},[_vm._v(_vm._s(_vm.$t("panel.tyre.settings")))])]),_c("div",{staticStyle:{height:"500px"}},[_c("smart-table-settings",{attrs:{data:_vm.data,column:_vm.data,settings:_vm.settings}})],1),_c("div",{staticClass:"modal-dialog-footer"},[_c("el-button",{staticClass:"modal-btn-cancel",on:{click:_vm.closedialog}},[_vm._v(_vm._s(_vm.$t("panel.tyre.clos")))]),_c("el-button",{staticClass:"modal-btn-save",attrs:{type:"primary"},on:{click:function($event){return _vm.save()}}},[_vm._v(_vm._s(_vm.$t("panel.tyre.ok")))])],1)]):_vm._e()]],2):_vm._e()},staticRenderFns=[],DashboardFilters=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(425728),__webpack_require__(186466),__webpack_require__(169358),__webpack_require__(879288),__webpack_require__(634338),__webpack_require__(897389),__webpack_require__(648324),__webpack_require__(506319)),DateHelper=__webpack_require__(281230),colors=__webpack_require__(519898),JumpToHelper=__webpack_require__(921466),SmartTableSettings=__webpack_require__(100385),criteriaHelper=__webpack_require__(292296),KFICardsvue_type_script_lang_js={props:["widget","config"],mixins:[DateHelper/* default */.Z,DashboardFilters/* default */.Z,JumpToHelper/* default */.Z,criteriaHelper/* default */.Z],data:function(){return{visibility:!1,enable:{10227:10227,10228:10228,10229:10229,10230:10230,10231:10231,10232:10232,10233:10233,10234:10234,10235:10235},settings:{critera:{key:null,operatorId:null,value:null},display:{displayValue:null},action:{blinkValue:!1},style:{color:"#000",bgcolor:"#fff"}},rerender:!1,data:{style:{color:"red",bgcolor:"pink"},unitString:""},style:{bgcolor:"#fff",color:"#000"},result:null,domRerender:!0,predefineColors:colors/* default */.Z.readingcardColors,loading:!1}},components:{SmartTableSettings:SmartTableSettings/* default */.Z},mounted:function(){this.loadCardData()},computed:{mode:function(){return!(!this.$route.query||!this.$route.query.create||"edit"!==this.$route.query.create)||!(!this.$route.query||!this.$route.query.create||"new"!==this.$route.query.create)},getScale:function(){return this.$style.responsiveScale(200,200,1,this.currentWidth,this.currentHeight)},currentWidth:function(){return this.$el&&this.$el.clientWidth?this.$el.clientWidth:200},currentHeight:function(){return this.$el&&this.$el.clientHeight?this.$el.clientHeight:200}},watch:{dashboardDateFilter:{handler:function(newData,oldData){newData&&this.refresh()}}},methods:{getStyle:function(){if(this.data.conditionalFormatting&&this.data.conditionalFormatting.length&&this.data){var conditionalFormatting=this.data.conditionalFormatting;return conditionalFormatting[0].critera&&conditionalFormatting[0].critera.operatorId?this.getStyleObj(this.result.result,this.data.conditionalFormatting,this.data):{background:this.style.bgcolor,color:this.style.color}}return{background:this.style.bgcolor,color:this.style.color}},getDisplayStyleObj:function(){if(this.data.conditionalFormatting&&this.data.conditionalFormatting.length&&this.data){var conditionalFormatting=this.data.conditionalFormatting;if(conditionalFormatting[0].critera&&conditionalFormatting[0].critera.operatorId)return this.getDisStyleObj(this.result.result,this.data.conditionalFormatting,this.data)}},getDisStyleObj:function(value,conditionalFormatting,config){var _this=this,self=this;if(conditionalFormatting&&conditionalFormatting.length){var style={};return conditionalFormatting.forEach((function(rt){var operator=null;operator=Object.values(self.$constants.OPERATORS[config.datatype]).find((function(rl){return rl.operatorId===rt.critera.operatorId}));var val=null;val=Object.keys(value).length>1?value.value:value,operator&&_this.criteriaValidate(val,rt.critera.value,operator._name,config.datatype)&&rt.action.blinkValue&&(style={animation:"blinker 1s linear infinite"})})),style}return{}},getStyleObj:function(value,conditionalFormatting,config){var _this2=this,self=this;if(conditionalFormatting&&conditionalFormatting.length){var style={};return conditionalFormatting.forEach((function(rt){var operator=null;operator=Object.values(self.$constants.OPERATORS[config.datatype]).find((function(rl){return rl.operatorId===rt.critera.operatorId}));var val=null;val=Object.keys(value).length>1?value.value:value,operator&&_this2.criteriaValidate(val,rt.critera.value,operator._name,config.datatype)&&(style={color:rt.style.color,"background-color":rt.style.bgcolor})})),style}return{}},getDisplayObj:function(value,conditionalFormatting,config){var _this3=this,self=this;if(conditionalFormatting&&conditionalFormatting.length){var displayValue=this.normalValue();return conditionalFormatting.forEach((function(rt){var operator=null;operator=Object.values(self.$constants.OPERATORS[config.datatype]).find((function(rl){return rl.operatorId===rt.critera.operatorId}));var val=null;val=Object.keys(value).length>1?value.value:value,operator&&_this3.criteriaValidate(val,rt.critera.value,operator._name,config.datatype)&&rt.display&&rt.display.displayValue&&(displayValue=rt.display.displayValue)})),displayValue}return{}},closedialog:function(){this.visibility=!1,this.selectcolumn=null},save:function(){var _this4=this;this.setParams(),this.closedialog(),this.rerender=!0,setTimeout((function(){_this4.rerender=!1}),150)},formattedResult:function(){if(this.data.conditionalFormatting&&this.data.conditionalFormatting.length&&this.data){var conditionalFormatting=this.data.conditionalFormatting;return conditionalFormatting[0].critera&&conditionalFormatting[0].critera.operatorId?this.getDisplayObj(this.result.result,this.data.conditionalFormatting,this.data):this.normalValue()}return this.normalValue()},normalValue:function(){var _this5=this;if(this.result.result&&this.result.result.hasOwnProperty("unit")&&this.result.result.hasOwnProperty("value")){if(this.$convert().possibilities().find((function(rt){return rt===_this5.result.result.unit}))){var obj=this.$convert(Number(this.result.result.value)).from(this.result.result.unit).toBest();return"".concat(Number(obj.val).toFixed(2)," ").concat(obj.unit)}return"".concat(Number(this.result.result.value).toFixed(2)," ").concat(this.result.result.unit)}return this.result.result&&this.result.result.hasOwnProperty("value")?this.result.result:this.result.result||"---"},drilldown:function(){if(!this.mode&&!this.$mobile&&this.data&&this.data.expressions&&this.data.expressions.length){var data=this.$helpers.expressionsToFilters(this.data.expressions);data.filters&&data.moduleName&&this.jumpToViewList(data.filters,data.moduleName)}},refresh:function(){this.updateCard()},loadCardData:function(){var self=this,params=null;this.widget&&this.widget.id>-1?(params={widgetId:self.widget.id},this.dashboardDateFilter?(params.startTime=this.getDateFilterStartTime(),params.endTime=this.getDateFilterEndTime()):!this.dashboardDateFilter||this.widget.widgetSettingsJson&&!this.widget.widgetSettingsJson.useDashboardFilter||(params.startTime=this.getDateFilterStartTime(),params.endTime=this.getDateFilterEndTime())):params={workflow:{expressions:this.widget.dataOptions.data&&this.widget.dataOptions.data.expressions?this.widget.dataOptions.data.expressions:[],workflowUIMode:1},staticKey:"kpiCard"},this.loading=!0,self.$http.post("dashboard/getCardData",params).then((function(response){self.getParams(),self.getData(response.data.cardResult),self.loading=!1})).catch((function(error){self.loading=!1}))},updateCard:function(){var self=this,params=null;!this.dashboardDateFilter||this.widget.widgetSettingsJson&&!this.widget.widgetSettingsJson.useDashboardFilter?params={workflow:{expressions:this.widget.dataOptions.data&&this.widget.dataOptions.data.expressions?this.widget.dataOptions.data.expressions:[],workflowUIMode:1},staticKey:"kpiCard"}:this.widget&&this.widget.id>-1?(params={widgetId:self.widget.id},params.startTime=this.getDateFilterStartTime(),params.endTime=this.getDateFilterEndTime()):(params={workflow:{expressions:this.widget.dataOptions.data&&this.widget.dataOptions.data.expressions?this.widget.dataOptions.data.expressions:[],workflowUIMode:1},staticKey:"kpiCard"},params.startTime=this.getDateFilterStartTime(),params.endTime=this.getDateFilterEndTime()),this.loading=!0,self.getParams(),self.$http.post("dashboard/getCardData",params).then((function(response){self.getData(response.data.cardResult),self.loading=!1})).catch((function(error){self.loading=!1}))},setColor:function(mode,color){"bg"===mode?this.style.bgcolor=color:"color"===mode&&(this.style.color=color),this.domRerender=!1,this.domRerender=!0,this.setParams()},getParams:function(){if(this.widget.dataOptions.data?(this.widget.dataOptions.metaJson=JSON.stringify(this.widget.dataOptions.data),this.data=this.widget.dataOptions.data):this.widget.id>-1&&this.widget.dataOptions.metaJson&&(this.data=JSON.parse(this.widget.dataOptions.metaJson)),!this.data.conditionalFormatting){var condition=[{critera:{key:null,operatorId:null,value:null},display:{displayValue:null},action:{blinkValue:!1},style:{color:"#000",bgcolor:"#fff"}}];this.$set(this.data,"conditionalFormatting",condition),this.data.datatype="NUMBER"}this.widget.hasOwnProperty("widgetVsWorkflowContexts")&&this.widget.widgetVsWorkflowContexts.length&&this.widget.widgetVsWorkflowContexts[0].workflow&&(this.data["v2Script"]=this.widget.widgetVsWorkflowContexts[0].workflow["v2Script"],this.data["workflowV2String"]=this.widget.widgetVsWorkflowContexts[0].workflow["workflowV2String"]),this.setParams()},setParams:function(){this.data&&!this.data.style?this.data.style={color:this.color,bgcolor:this.bgcolor}:this.style=this.data.style,this.widget.dataOptions.metaJson=JSON.stringify(this.data)},getData:function(data){data.hasOwnProperty("result")&&(this.result=data)}}},cards_KFICardsvue_type_script_lang_js=KFICardsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(cards_KFICardsvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,KFICards=component.exports},
/***/516615:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"f5ac1ecd58898bcca9f15043de765de0.svg";
/***/},
/***/99469:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"c02b6a98da05ac72d94483a93776b086.svg";
/***/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/29391.js.map