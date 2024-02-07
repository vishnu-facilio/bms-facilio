(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[53811],{
/***/678380:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FDialog}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/FDialog.vue?vue&type=template&id=621b9da3&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{class:[_vm.options.className]},[_c("transition",{attrs:{name:_vm.options.transition}},[_vm.visible?_c("div",{staticClass:"f-dialog-wrapper",attrs:{id:_vm.options.id},on:{click:function($event){return $event.target!==$event.currentTarget?null:_vm.maskClick.apply(null,arguments)},keydown:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"esc",27,$event.key,["Esc","Escape"])?null:_vm.escape.apply(null,arguments)}}},[_c("div",{staticClass:"f-dialog-backdrop",style:{background:_vm.options.maskColor}}),_c("div",{ref:"dialogContent",staticClass:"f-dialog-content",class:{"f-dialog-wide":_vm.options.wide},attrs:{tabindex:"-1"}},[_c("div",{staticClass:"f-dialog-header",class:{"with-body":_vm.options.message}},[_c("span",{staticClass:"content delete-heading"})]),_vm.options.message?_c("div",{staticClass:"f-dialog-body"},[_c("h3",{staticClass:"delete-heading"},[_vm._v(_vm._s(_vm.options.title))]),_c("span",{staticClass:"content delete-content"},[_vm._v(_vm._s(_vm.options.message))])]):_vm.options.htmlMessage?_c("div",{staticClass:"f-dialog-body"},[_c("h3",{staticClass:"delete-heading"},[_vm._v(_vm._s(_vm.options.title))]),_c("span",{staticClass:"content delete-content",domProps:{innerHTML:_vm._s(_vm.options.htmlMessage)}})]):"alert"===_vm.options.mode?_c("div",{staticClass:"f-dialog-body"},[_c("span",{staticClass:"content delete-content"},[_vm._v(_vm._s(_vm.options.message))])]):"prompt"===_vm.options.mode?_c("div",{staticClass:"prompt-dialog"},[_c("div",{staticClass:"prompt-input-wrapper"},["textarea"===_vm.options.promptType?_c("textarea",{directives:[{name:"model",rawName:"v-model",value:_vm.input,expression:"input"}],ref:"promptInput",staticClass:"prompt-input",staticStyle:{resize:"none"},attrs:{placeholder:_vm.options.promptPlaceholder},domProps:{value:_vm.input},on:{keydown:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.rbClick.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.input=$event.target.value)}}}):_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.input,expression:"input"}],ref:"promptInput",staticClass:"prompt-input",attrs:{placeholder:_vm.options.promptPlaceholder,type:"text"},domProps:{value:_vm.input},on:{keydown:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:_vm.rbClick.apply(null,arguments)},input:function($event){$event.target.composing||(_vm.input=$event.target.value)}}})])]):"confirm"===_vm.options.mode&&_vm.options.confirmOptions&&_vm.options.confirmOptions.length?_c("div",{staticClass:"f-dialog-body"},_vm._l(_vm.options.confirmOptions,(function(item,idx){return _c("el-checkbox",{key:idx,model:{value:item.value,callback:function($$v){_vm.$set(item,"value",$$v)},expression:"item.value"}},[_vm._v(_vm._s(item.label))])})),1):_vm._e(),_c("div",{staticClass:"f-dialog-footer btn-block"},[_vm.options.lbHide?_vm._e():_c("button",{ref:"lbButton",staticClass:"btn btn--secondary del-cancel-btn",class:{red:_vm.options.lbDanger},attrs:{type:"button",value:_vm.options.lbLabel},on:{click:_vm.lbClick}},[_vm._v(" "+_vm._s(_vm.options.lbLabel||_vm.$t("common._common.cancel"))+" ")]),_vm.options.rbHide?_vm._e():_c("button",{ref:"rbButton",staticClass:"btn btn--primary",class:[_vm.options.rbDanger&&"btn--danger",_vm.options.rbClass?_vm.options.rbClass:"del-delete-btn"],attrs:{type:"button",value:_vm.options.rbLabel,tabindex:"-1",disabled:_vm.loadingOnConfirm},on:{click:_vm.rbClick}},[_vm._v(" "+_vm._s(_vm.options.rbLabel||"Delete")+" ")])])])]):_vm._e()])],1)},staticRenderFns=[],createClass=__webpack_require__(862833),classCallCheck=__webpack_require__(513087),OPTIONS_TEMPLATE=(__webpack_require__(260228),__webpack_require__(670560),{id:"vue-fdialog-default",
// DOM id
className:"",
// additional class name
maskColor:"rgba(40, 40, 40, 0.6)",
// color of the mask area
override:!1,
// current dialog overrides the queue and cancel any dialogs before it
parent:"body",
// parent DOM node
transition:"f-dialog-transition",
// transition name
duration:0,
// milliseconds before auto close, set to 0 or any falsy value to disable auto close
wide:!1,
// show as a wide dialog
title:"",
// dialog title
message:"",
// dialog message
mode:"alert",
// alert, confirm or prompt
forceStay:!0,
// set to true to prevent closing / canceling the dialog when mask area is clicked
defaultButton:"r",
// set the button that gets focus when the dialog shows, available when at least two buttons are shown
lbDanger:!1,
// set to true to style the left button as danger
rbDanger:!1,
// set to true to style the right button as danger
lbHide:!1,
// hide left button
rbHide:!1,
// hide right button
lbClass:"",rbClass:"",lbLabel:"Cancel",
// left button label text
rbLabel:"Ok",
// right button label text
actionAlt:null,
// callback function when left button is clicked
action:null,
// callback function when right button is clicked
promptType:"text",
// prompt type 'text' or 'textarea'
promptPlaceholder:"",htmlMessage:"",confirmOptions:null,proceedFunc:null}),CANCELLED=!0,Later=(0,createClass/* default */.Z)((function Later(){var _this=this;(0,classCallCheck/* default */.Z)(this,Later),this.promise=new Promise((function(resolve,reject){_this.reject=reject,_this.resolve=resolve}))})),FDialogvue_type_script_lang_js={OPTIONS_TEMPLATE:OPTIONS_TEMPLATE,props:[],components:{},computed:{options:function(){return Object.assign({},OPTIONS_TEMPLATE,this.optionsData)}},watch:{},data:function(){return{queue:[],optionsData:{},visible:!1,input:"",timeoutHandler:null,promiseHandler:null,loadingOnConfirm:!1}},methods:{enqueue:function(args){var pending=this.queue.length;return args.override?(this.queue=[args],this.transit()):(this.queue.push(args),0===pending&&this.transit()),args.promiseHandler=new Later,args.promiseHandler.promise},consume:function(cancelled){var _this2=this;switch(this.options.mode){case"alert":this.optionsData.promiseHandler.resolve();break;case"confirm":var value=!cancelled;this.options.confirmOptions&&this.options.confirmOptions.length&&(value=[value,this.options.confirmOptions]),this.optionsData.promiseHandler.resolve(value);break;case"prompt":cancelled?this.optionsData.promiseHandler.resolve(null):this.optionsData.promiseHandler.resolve(this.input);break;default:this.optionsData.promiseHandler.resolve();break}this.timeoutHandler&&clearTimeout(this.timeoutHandler),this.timeoutHandler=null,this.input="",cancelled||!this.options.proceedFunc?(this.transit(),this.queue.shift()):(this.loadingOnConfirm=!0,this.options.proceedFunc().then((function(){_this2.close()})))},close:function(){this.loadingOnConfirm=!1,this.visible=!1,this.queue.shift()},transit:function(){var _this3=this;this.visible=!1,setTimeout((function(){_this3.queue.length&&(_this3.optionsData=_this3.queue[0],_this3.visible=!0,setTimeout((function(){"prompt"===_this3.options.mode?_this3.$refs.promptInput.focus():_this3.$refs.rbButton.focus()})),_this3.options.duration&&(_this3.timeoutHandler=setTimeout((function(){_this3.consume()}),_this3.options.duration)))}))},escape:function(){this.consume(CANCELLED)},maskClick:function(){!1===this.options.forceStay&&(this.consume(CANCELLED),"function"===typeof this.options.actionAlt&&this.options.actionAlt())},lbClick:function(){this.consume(CANCELLED),"function"===typeof this.options.actionAlt&&this.options.actionAlt()},rbClick:function(){this.consume(),"function"===typeof this.options.action&&this.options.action()}},mounted:function(){}},components_FDialogvue_type_script_lang_js=FDialogvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FDialogvue_type_script_lang_js,render,staticRenderFns,!1,null,"621b9da3",null)
/* harmony default export */,FDialog=component.exports},
/***/989715:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return FSound}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/FSound.vue?vue&type=template&id=74a36aeb
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"hide"},_vm._l(_vm.sounds,(function(sound,index){return _c("audio",{key:index,ref:"fglobalSound",refInFor:!0,attrs:{src:sound.url}})})),0)},staticRenderFns=[],FSoundvue_type_script_lang_js=(__webpack_require__(425728),__webpack_require__(260228),__webpack_require__(434284),{data:function(){return{activeSound:__webpack_require__(405074),sounds:[{name:"default",url:__webpack_require__(405074)},{name:"stuffed",url:__webpack_require__(76834)},{name:"alarm",
// url: require('statics/sounds/alarm_default.mp3'),
url:__webpack_require__(930760)}]}},methods:{play:function(name){var soundName=name||"default",sound=this.sounds.find((function(sound){return sound.name===soundName})),soundIndex=this.sounds.indexOf(sound);this.$refs.fglobalSound[soundIndex].play()},stop:function(name){var soundName=name||"default",sound=this.sounds.find((function(sound){return sound.name===soundName})),soundIndex=this.sounds.indexOf(sound);this.$refs.fglobalSound[soundIndex].pause(),this.$refs.fglobalSound[soundIndex].currentTime=0}}}),components_FSoundvue_type_script_lang_js=FSoundvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_FSoundvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,FSound=component.exports},
/***/369955:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return ValueFormatter}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/ValueFormatter.vue?vue&type=template&id=8ba0e75e
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("span",[_vm.val?_c("span",{staticClass:"value"},[_vm._v(" "+_vm._s(_vm.val)+" ")]):_vm._e(),_vm.withUnit?_c("span",{staticClass:"unit"},[_vm._v(" "+_vm._s(_vm.uni)+" ")]):_vm._e()])},staticRenderFns=[],esm_typeof=__webpack_require__(103336),validation=(__webpack_require__(879288),__webpack_require__(897389),__webpack_require__(990260)),ValueFormattervue_type_script_lang_js={props:["withUnit","config","value","unit","maxDecimal"],data:function(){return{val:null,uni:""}},mounted:function(){this.config?this.loadDataWithConfig():this.loadConfig()},methods:{loadConfig:function(){if(this.unit&&""!==this.unit){var obj=this.$convert(this.value).from(this.unit).toBest();
// value set
"object"===(0,esm_typeof/* default */.Z)(obj)&&(Number(obj.val)%1===0||Number(obj.val)%1===0||(0,validation/* isUndefined */.o8)(this.maxDecimal)?this.val=obj.val:this.val=obj.val.toFixed(this.maxDecimal)),
// unit set
this.withUnit&&"object"===(0,esm_typeof/* default */.Z)(obj)&&(this.uni=obj.unit)}else this.val=this.value},loadDataWithConfig:function(){}}},components_ValueFormattervue_type_script_lang_js=ValueFormattervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_ValueFormattervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,ValueFormatter=component.exports},
/***/231296:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return currency}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/currency.vue?vue&type=template&id=043ee61d
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_vm.prefixUnit?_c("span",{staticClass:"fc-currency-value",class:"symbol-"+_vm.symbolSize},[_vm._v(_vm._s(_vm.recordCurrency||_vm.$currency))]):_vm._e(),_c("span",{class:"curreny-"+_vm.valueSize},[_vm._v(_vm._s(_vm.formatedValue))]),_vm.prefixUnit?_vm._e():_c("span",{staticClass:"fc-currency-value",class:"symbol-"+_vm.symbolSize+" mL5"},[_vm._v(_vm._s(_vm.recordCurrency||_vm.$currency))])])},staticRenderFns=[],currencyvue_type_script_lang_js=(__webpack_require__(976801),{props:["value","symbolSize","valueSize","recordCurrency"],computed:{formatedValue:function(){return this.$d3.format(",.2f")(this.value)},prefixUnit:function(){var $currency=this.$currency;return["$","£","₹"].includes($currency)}}}),components_currencyvue_type_script_lang_js=currencyvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_currencyvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,currency=component.exports},
/***/207437:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return DashboardLoopRunner}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/tv/DashboardLoopRunner.vue?vue&type=template&id=25418c77
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return!_vm.remoteScreen.screenId||_vm.remoteScreen.screenId<0?_c("div",{staticClass:"no-screens-casted"},[_vm._v(" "+_vm._s(_vm.$t("panel.no_screen"))+" ")]):_vm.remoteScreen.screenContext.screenDashboards&&_vm.remoteScreen.screenContext.screenDashboards.length?_vm.slideShow.currentDashboard?_c("div",{on:{mousemove:_vm.showControlsToolbar}},[_c("transition",{attrs:{name:"toolbarfade"}},[_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.controlsToolbar.show,expression:"controlsToolbar.show"}],staticClass:"screen-controls-toolbar"},[_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fa fa-backward",class:{disabled:!_vm.previousDashboardName},attrs:{title:_vm.previousDashboardName?"Previous: "+_vm.previousDashboardName:"Previous","data-arrow":"true"},on:{click:_vm.previous}}),_vm.slideShow.pause?_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fa fa-play-circle playpause",attrs:{title:"Play","data-arrow":"true"},on:{click:function($event){_vm.slideShow.pause=!1}}}):_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fa fa-pause-circle playpause",attrs:{title:"Pause","data-arrow":"true"},on:{click:function($event){_vm.slideShow.pause=!0}}}),_c("i",{directives:[{name:"tippy",rawName:"v-tippy"}],staticClass:"fa fa-forward",class:{disabled:!_vm.nextDashboardName},attrs:{title:_vm.nextDashboardName?"Next: "+_vm.nextDashboardName:"Next","data-arrow":"true"},on:{click:_vm.next}})])]),_vm.showDashboard?_c("div",{on:{click:_vm.openFullScreen}},[_c(_vm.whichDashboardViewer,{tag:"component",attrs:{currentDashboard:_vm.slideShow.currentDashboard,refresh:_vm.refresh,isTv:!0},on:{dashboardLoaded:_vm.getDashboardobj}},[_c("el-popover",{attrs:{slot:"dashboardNameList",placement:"bottom",width:"300","popper-class":"dashboard-switcher",trigger:"click"},slot:"dashboardNameList",model:{value:_vm.toggle,callback:function($$v){_vm.toggle=$$v},expression:"toggle"}},[_c("ul",_vm._l(this.remoteScreen.screenContext.screenDashboards,(function(Dashboard,index){return _c("li",{key:index,class:{active:_vm.slideShow.currentDashboardIndex===index},on:{click:function($event){return _vm.openDashBoard(index)}}},[_vm._v(" "+_vm._s(Dashboard.dashboard.dashboardName)+" ")])})),0),_c("span",{attrs:{slot:"reference"},slot:"reference"},[_vm._v(_vm._s(_vm.slideShow.currentDashboard.dashboardName)+" "),_c("i",{staticClass:"el-icon-arrow-down el-icon-arrow-down-tv"})])])],1)],1):_vm._e()],1):_vm._e():_c("div",{staticClass:"no-screens-casted"},[_vm._v(" "+_vm._s(_vm.$t("panel.no_dashboard"))+" ")])},staticRenderFns=[],DashboardViewer=__webpack_require__(328917),dashboard_DashboardViewer=__webpack_require__(825026),DashboardLoopRunnervue_type_script_lang_js={props:["remoteScreen"],components:{NewDashboardViewer:dashboard_DashboardViewer["default"],OldDashboardViewer:DashboardViewer["default"]},data:function(){return{showDashboard:!0,refresh:!0,toggle:!1,slideShow:{currentDashboardIndex:0,currentDashboard:null,loopInterval:null,pause:!1},controlsToolbar:{show:!1,autoHideTimeout:5e3,autoHideTimeoutObj:null}}},mounted:function(){this.initLoopRunner()},destroyed:function(){this.clearLoopRunner()},computed:{whichDashboardViewer:function(){var isNewDashboardEnabled=this.$helpers.isLicenseEnabled("NEW_DASHBOARD_FLOW");return isNewDashboardEnabled?"NewDashboardViewer":"OldDashboardViewer"},previousDashboardName:function(){var prevIndex=this.slideShow.currentDashboardIndex-1;if(prevIndex>=0){var dbProps=this.getDashboardProps(prevIndex);return dbProps.dashboardName}return null},nextDashboardName:function(){var nextIndex=this.slideShow.currentDashboardIndex+1;if(nextIndex<this.remoteScreen.screenContext.screenDashboards.length){var dbProps=this.getDashboardProps(nextIndex);return dbProps.dashboardName}return null}},watch:{remoteScreen:{handler:function(){this.reload()},deep:!0}},methods:{getDashboardobj:function(dashboard){dashboard&&dashboard.children&&dashboard.children.length&&this.applyDashboardFullScreenChanges()},reload:function(){this.initLoopRunner()},openDashBoard:function(currentDashboardIndex){this.slideShow.currentDashboard=this.getDashboardProps(currentDashboardIndex),this.slideShow.currentDashboardIndex=currentDashboardIndex,this.toggle=!1},initLoopRunner:function(){var self=this;if(self.clearLoopRunner(),this.remoteScreen.screenId&&this.remoteScreen.screenId>0&&this.remoteScreen.screenContext.screenDashboards&&this.remoteScreen.screenContext.screenDashboards.length){this.slideShow.currentDashboard=this.getDashboardProps(0);var interval=1e3*this.remoteScreen.screenContext.interval;this.slideShow.loopInterval=setInterval((function(){try{self.applyDashboardFullScreenChanges(),self.slideShow.pause||self.next()}catch(err){}}),interval)}},applyDashboardFullScreenChanges:function(){1===document.getElementsByClassName("single-dashboard").length&&(document.getElementsByClassName("dashboard-widget-view").length||document.body.classList.add("dashboard-widget-view"),document.getElementsByClassName("fc-widget-header").length&&(document.getElementsByClassName("fc-widget-header")[0].style.display="none"))},removeDashboardFullScreenChanges:function(){document.body.classList.remove("dashboard-widget-view"),1===document.getElementsByClassName("single-dashboard").length&&document.getElementsByClassName("fc-widget-header").length&&(document.getElementsByClassName("fc-widget-header")[0].style.display="flex")},clearLoopRunner:function(){this.slideShow.loopInterval&&(clearInterval(this.slideShow.loopInterval),this.removeDashboardFullScreenChanges()),this.slideShow.currentDashboardIndex=0,this.slideShow.currentDashboard=null,this.slideShow.pause=!1},next:function(){this.showDashboard=!1;var self=this;this.$nextTick((function(){self.slideShow.currentDashboardIndex=self.slideShow.currentDashboardIndex+1,self.slideShow.currentDashboardIndex>=self.remoteScreen.screenContext.screenDashboards.length&&(self.slideShow.currentDashboardIndex=0),self.slideShow.currentDashboard=self.getDashboardProps(self.slideShow.currentDashboardIndex),1==self.remoteScreen.screenContext.screenDashboards.length&&(self.refresh=!self.refresh),self.showDashboard=!0}))},previous:function(){var self=this;self.slideShow.currentDashboardIndex=self.slideShow.currentDashboardIndex-1,self.slideShow.currentDashboardIndex<0&&(self.slideShow.currentDashboardIndex=0),self.slideShow.currentDashboard=self.getDashboardProps(self.slideShow.currentDashboardIndex)},showControlsToolbar:function(){var self=this;clearTimeout(self.autoHideTimeoutObj),self.controlsToolbar.show=!0,self.autoHideTimeoutObj=setTimeout((function(){self.controlsToolbar.show=!1}),self.controlsToolbar.autoHideTimeout)},getDashboardProps:function(index){var screenDashboard=this.remoteScreen.screenContext.screenDashboards[index],linkName=screenDashboard.dashboard.linkName,buildingId=null,siteId=null;return"residentialbuildingdashboard"!==linkName&&"commercialbuildingdashboard"!==linkName||(linkName="buildingdashboard"),"buildingdashboard"===linkName?buildingId=screenDashboard.spaceId:"sitedashboard"===linkName&&(siteId=screenDashboard.spaceId),{id:screenDashboard.dashboardId,dashboardName:screenDashboard.dashboard.dashboardName,linkName:linkName,moduleName:screenDashboard.dashboard.moduleName,buildingId:buildingId,siteId:siteId,readOnly:!0,screenSetting:this.remoteScreen.screenContext.screenSetting}},openFullScreen:function(){var element=document.getElementsByTagName("body")[0];element.requestFullscreen?element.requestFullscreen():element.mozRequestFullScreen?element.mozRequestFullScreen():element.webkitRequestFullscreen?element.webkitRequestFullscreen():element.msRequestFullscreen&&element.msRequestFullscreen()}}},tv_DashboardLoopRunnervue_type_script_lang_js=DashboardLoopRunnervue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(tv_DashboardLoopRunnervue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,DashboardLoopRunner=component.exports},
/***/852146:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){"use strict";
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return Home}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/tv/Home.vue?vue&type=template&id=ced03dd8
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.loading?_c("div",[_vm._v(_vm._s(_vm.$t("panel.loading_load")))]):_vm.account?_c("div",{staticClass:"layout-page-container",class:{"fc-white-bg":"/app/pdf/workpermit"===_vm.$route.path&&"/app/pdf/quotationpdf"===_vm.$route.path}},[_c("div",{staticClass:"layout-page scrollable",class:_vm.themeClass},[_c("dashboard-loop-runner",{attrs:{remoteScreen:_vm.account.data.connectedScreen}})],1)]):_c("div",{staticClass:"passcode-page"},[_c("table",[_vm._m(0),_c("tr",[_c("td",[_c("div",{staticClass:"code-header"},[_vm._v(" "+_vm._s(_vm.$t("panel.pass"))+" ")])])]),_vm.refreshPageToGetCode?_c("tr",[_c("td",[_c("div",{staticClass:"error-box"},[_vm._v(" "+_vm._s(_vm.$t("panel.refresh"))+" ")])])]):_c("tr",[_c("td",[_c("div",{staticClass:"code-subheader"},[_vm._v(" "+_vm._s(_vm.$t("panel.connect_facilio_tv"))+" ")]),_c("div",{staticClass:"code mT50"},[_vm._v(" "+_vm._s(_vm.passcode?_vm.passcode:"---")+" ")])])])])])},staticRenderFns=[function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("tr",[_c("td",[_c("img",{staticClass:"fc-logo",attrs:{src:__webpack_require__(181242)}})])])}],axios=__webpack_require__(409669),axios_default=__webpack_require__.n(axios),http=__webpack_require__(218430),util=__webpack_require__(948288),vue_runtime_esm=__webpack_require__(720144),build=__webpack_require__(82154),build_default=__webpack_require__.n(build),DashboardLoopRunner=__webpack_require__(207437),wms_client=__webpack_require__(907974),Homevue_type_script_lang_js={components:{DashboardLoopRunner:DashboardLoopRunner/* default */.Z},data:function(){return{loading:!0,validateCodeInterval:3e3,passcode:null,refreshPageToGetCode:!1,account:null}},mounted:function(){axios_default().defaults.headers.common["X-Remote-Screen"]="true",this.connectScreen()},computed:{themeClass:function(){var theme="white",storedTheme=window.localStorage.getItem("theme"),paramTheme=this.$route.query.theme&&""!==this.$route.query.theme?this.$route.query.theme:null;return paramTheme?theme=paramTheme:storedTheme&&(theme=storedTheme),window.localStorage.setItem("theme",theme),document.body.classList.remove("fc-white-theme"),document.body.classList.remove("fc-black-theme"),document.body.classList.add("fc-"+theme+"-theme"),"fc-"+theme+"-theme"},refreshCount:function(){return this.$store.state.remotescreen.refreshCount},buildUpdate:function(){return this.$store.state.system.reload}},watch:{refreshCount:function(newVal){window.location.reload?window.location.reload():this.connectScreen()},buildUpdate:function(){this.buildUpdate&&(window.location.reload?window.location.reload():this.connectScreen())}},methods:{connectScreen:function(){var self=this;self.loading=!0,self.account=null,http/* default */.ZP.get("/tv/connect").then((function(response){self.loading=!1,response.data.account?(self.account=response.data.account,self.loadAccount()):self.generateCode()})).catch((function(){self.loading=!1,self.generateCode()}))},loadAccount:function(){if(this.$store.dispatch("setCurrentAccount",this.account),vue_runtime_esm["default"].use(util/* default */.Z,{account:this.account}),this.account.config&&this.account.config.ws_endpoint&&(vue_runtime_esm["default"].use(build_default(),this.account.config.ws_endpoint,{store:this.$store,format:"json",reconnection:!0,
// (Boolean) whether to reconnect automatically (false)
reconnectionDelay:1e4}),null===this.wsPingPongInterval)){var self=this;this.wsPingPongInterval=setInterval((function(){if(self.$socket){var obj={from:0,to:0,content:{ping:"check"}};self.$socket.sendObj(obj)}}),6e4)}try{this.account.config.new_ws_endpoint&&(vue_runtime_esm["default"].prototype.$wms=new wms_client/* default */.Z({endpoint:this.account.config.new_ws_endpoint,log:!0}))}catch(err){}},generateCode:function(){var self=this;this.passcode||http/* default */.ZP.get("/tv/generatecode").then((function(response){self.passcode=response.data.result.code,self.validateCode()}))},validateCode:function(){var self=this;http/* default */.ZP.get("/tv/validatecode?code="+self.passcode).then((function(response){1===response.data.responseCode?
// error
self.refreshPageToGetCode=!0:"connected"===response.data.result.status?self.connectScreen():setTimeout(self.validateCode,self.validateCodeInterval)})).catch((function(error){setTimeout(self.validateCode,self.validateCodeInterval)}))}}},tv_Homevue_type_script_lang_js=Homevue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(tv_Homevue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,Home=component.exports},
/***/181242:
/***/function(module,__unused_webpack_exports,__webpack_require__){module.exports=__webpack_require__.p+"da85ccff86897ff2fe48e5fab3a86d03.svg";
/***/},
/***/930760:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";module.exports=__webpack_require__.p+"media/htc_single_beep.2f739e6d.mp3"},
/***/405074:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";module.exports=__webpack_require__.p+"media/notification.3537b31a.mp3"},
/***/76834:
/***/function(module,__unused_webpack_exports,__webpack_require__){"use strict";module.exports=__webpack_require__.p+"media/stuffed-and-dropped.922dd833.mp3"}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/53811.js.map