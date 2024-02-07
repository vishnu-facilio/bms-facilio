"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[99480],{
/***/806339:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Z:function(){/* binding */return WidgetPagination}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/components/page/widget/utils/WidgetPagination.vue?vue&type=template&id=6b10cd5b
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"fc-widget-pagination"},[_vm.total>0?_c("div",{class:{"fc-black-small-txt-12":!_vm.hideToggle}},[_vm.from!==_vm.to?_c("span",[_vm._v(_vm._s(_vm.from)+" -")]):_vm._e(),_c("span",[_vm._v(_vm._s(_vm.to))]),_vm.hideToggle?_vm._e():[_vm._v(" of "+_vm._s(_vm.total)+" "),_c("span",{staticClass:"el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5",class:{disable:_vm.from<=1},on:{click:function($event){_vm.from>1&&_vm.prev()}}}),_c("span",{staticClass:"el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold",class:{disable:_vm.to===_vm.total},on:{click:function($event){_vm.to!==_vm.total&&_vm.next()}}})]],2):_vm._e()])},staticRenderFns=[],WidgetPaginationvue_type_script_lang_js={props:["currentPage","perPage","total","hideToggle"],data:function(){return{from:0,to:0,page:1}},mounted:function(){this.init()},watch:{total:function(){this.init()},currentPage:function(val){val!==this.page&&this.init()}},methods:{init:function(){this.page=this.currentPage||1,this.from=(this.page-1)*this.perPage+1;var to=this.from+this.perPage-1;this.to=this.total>to?to:this.total},next:function(){this.from=this.to+1,this.to+=this.perPage,this.to>this.total&&(this.to=this.total),this.page++,this.updateCurrentPage()},prev:function(){this.to=this.from-1,this.from-=this.perPage,this.from<=1?this.from=this.page=1:this.page--,this.updateCurrentPage()},updateCurrentPage:function(){
// Update currentPage value
this.$emit("update:currentPage",this.page),this.$emit("onChange")}}},utils_WidgetPaginationvue_type_script_lang_js=WidgetPaginationvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(utils_WidgetPaginationvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,WidgetPagination=component.exports},
/***/399480:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return BookingExternalAttendees}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/facilitybooking/booking/widgets/BookingExternalAttendees.vue?vue&type=template&id=729d26e8
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{ref:"external-attendee-container",staticClass:"p30"},[_c("portal",{attrs:{to:_vm.widget.key+"-title-section"}},[_c("div",{staticClass:"facility-header"},[_c("div",{staticClass:"section-header-label"},[_vm._v("EXTERNAL ATTENDEE LIST")]),_c("div",[_c("div",{staticClass:"d-flex flex-row "},[_c("pagination",{staticClass:"self-center mL10",attrs:{currentPage:_vm.page,total:_vm.totalCount,perPage:_vm.perPage},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}})],1)])])]),_vm.loading?_c("spinner",{attrs:{show:!0,size:"80"}}):_vm.$validation.isEmpty(_vm.recordsList)?_c("div",{staticClass:"d-flex justify-content-center flex-direction-column align-center height100"},[_c("div",[_c("inline-svg",{staticClass:"vertical-middle'",attrs:{src:"svgs/emptystate/commonempty",iconClass:"icon icon-80"}})],1),_c("div",{staticClass:"fc-black-dark f18 self-center bold"},[_vm._v(" No External Attendees registered ")])]):_c("table",{staticClass:"facility-summary-table width100"},[_c("thead",[_c("tr",[_c("th",{staticClass:"width300px"},[_vm._v("NAME")]),_c("th",{staticClass:"width300px"},[_vm._v("EMAIL")]),_c("th",[_vm._v("CONTACT NO")])])]),_c("tbody",_vm._l(_vm.recordsList,(function(attendee,index){return _c("tr",{key:index},[_c("td",[_vm._v(_vm._s(attendee.name||"---"))]),_c("td",[_vm._v(_vm._s(attendee.phone||"---"))]),_c("td",[_vm._v(_vm._s(attendee.email||"---"))])])})),0)])],1)},staticRenderFns=[],regeneratorRuntime=__webpack_require__(150124),asyncToGenerator=__webpack_require__(548534),WidgetPagination=(__webpack_require__(648324),__webpack_require__(806339)),validation=__webpack_require__(990260),api=__webpack_require__(32284),isEqual=__webpack_require__(618446),isEqual_default=__webpack_require__.n(isEqual),BookingExternalAttendeesvue_type_script_lang_js={components:{Pagination:WidgetPagination/* default */.Z},props:["widget","details","resizeWidget"],data:function(){return{recordsList:[],totalCount:0,perPage:5,page:1,loading:!0}},created:function(){this.loadRelatedRecords()},watch:{page:function(newVal,oldVal){isEqual_default()(newVal,oldVal)||this.loadRelatedRecords()}},methods:{autoResize:function(){var _this=this;this.$nextTick((function(){if(!(0,validation/* isEmpty */.xb)(_this.$refs["external-attendee-container"])){var height=_this.$refs["external-attendee-container"].scrollHeight+55,width=_this.$refs["external-attendee-container"].scrollWidth;_this.resizeWidget&&_this.resizeWidget({height:height,width:width})}}))},loadRelatedRecords:function(){var _this2=this;return(0,asyncToGenerator/* default */.Z)((0,regeneratorRuntime/* default */.Z)().mark((function _callee(){var recordFilter,queryObj,_yield$API$fetchAll,list,error,meta,_error$message,message;return(0,regeneratorRuntime/* default */.Z)().wrap((function(_context){while(1)switch(_context.prev=_context.next){case 0:return recordFilter={facilityBooking:{operatorId:36,value:["".concat(_this2.details.id)]}},queryObj={page:_this2.page,perPage:_this2.perPage,withCount:!0,filters:JSON.stringify(recordFilter)},_this2.loading=!0,_context.next=5,api/* API */.bl.fetchAll("facilityBookingExternalAttendee",queryObj);case 5:_yield$API$fetchAll=_context.sent,list=_yield$API$fetchAll.list,error=_yield$API$fetchAll.error,meta=_yield$API$fetchAll.meta,error?(_error$message=error.message,message=void 0===_error$message?"Error loading external attendee list":_error$message,_this2.$message.error(message)):(_this2.recordsList=list,_this2.totalCount=_this2.$getProperty(meta,"pagination.totalCount",null)),_this2.loading=!1,_this2.autoResize();case 12:case"end":return _context.stop()}}),_callee)})))()}}},widgets_BookingExternalAttendeesvue_type_script_lang_js=BookingExternalAttendeesvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(widgets_BookingExternalAttendeesvue_type_script_lang_js,render,staticRenderFns,!1,null,null,null)
/* harmony default export */,BookingExternalAttendees=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/99480.js.map