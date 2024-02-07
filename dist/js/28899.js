"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[28899],{
/***/928899:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// ESM COMPAT FLAG
__webpack_require__.r(__webpack_exports__),
// EXPORTS
__webpack_require__.d(__webpack_exports__,{default:function(){/* binding */return BookingViewLeagends}});// CONCATENATED MODULE: ./node_modules/cache-loader/dist/cjs.js?{"cacheDirectory":"node_modules/.cache/vue-loader","cacheIdentifier":"5f5f0a0c-vue-loader-template"}!./node_modules/@vue/vue-loader-v15/lib/loaders/templateLoader.js??vue-loader-options!./node_modules/cache-loader/dist/cjs.js??ruleSet[0].use[0]!./node_modules/@vue/vue-loader-v15/lib/index.js??vue-loader-options!./src/pages/indoorFloorPlan/components/BookingViewLeagends.vue?vue&type=template&id=323f6fd7&scoped=true
var render=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",[_c("div",{staticClass:"assignment-viwe-leagends"},[_vm.hideLeagend?_vm._e():_c("el-tabs",{staticClass:"card-tab-fixed aignment-leagends",model:{value:_vm.activeTab,callback:function($$v){_vm.activeTab=$$v},expression:"activeTab"}},[_vm.deskList.length?_c("el-tab-pane",{attrs:{label:"Availability",name:"availability"}},[_vm._l(_vm.availability,(function(data,index){return[_c("div",{key:index,staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:data.color,opacity:data.opacity||1}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s(data.name+" ("+data.value+") ")+" ")])])]}))],2):_vm._e(),_vm.deskList.length?_c("el-tab-pane",{attrs:{label:"Desk Types",name:"desktype"}},[_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("inline-svg",{attrs:{src:"svgs/floorplan/hotDesk",iconClass:"icon text-center icon-sm"}}),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s(" Hot Desk ("+_vm.deskVsType.hot+")")+" ")])],1),_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("inline-svg",{attrs:{src:"svgs/floorplan/hotelDesk",iconClass:"icon text-center icon-sm"}}),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s(" Hotel Desk ("+_vm.deskVsType.hotel+")")+" ")])],1)]):_vm._e(),_vm.lockers.length?_c("el-tab-pane",{attrs:{label:"Locker",name:"locker"}},[_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:"#22ae5c"}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s("Vacant Lockers ("+(_vm.unAssignedLockers.length||0)+") ")+" ")])]),_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:"#dc4a4c"}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s("Occupied Lockers ("+(_vm.assignedLockers.length||0)+") ")+" ")])])]):_vm._e(),_vm.parkingList.length?_c("el-tab-pane",{attrs:{label:"Parking",name:"parking"}},[_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:_vm.getParrkingUnassignedColor}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s("Vacant Parking ("+(_vm.getVacantList.length||0)+") ")+" ")])]),_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:_vm.getParrkingAssignedColor}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s("Occupied Parking ("+(_vm.getBookedList.length||0)+") ")+" ")])]),_c("div",{staticClass:"inline-flex pT10 pB10 border-bottom15"},[_c("div",{style:{color:_vm.getParkingReservableColor}},[_c("i",{staticClass:"fa fa-circle f14",attrs:{"aria-hidden":"true"}})]),_c("div",{staticClass:"pL8 fc-black-13"},[_vm._v(" "+_vm._s("Non Reservable Parking ("+(_vm.getNonReservalbleList.length||0)+") ")+" ")])])]):_vm._e()],1),_vm.hideLeagend?_vm._e():_c("div",{staticClass:"sidebarhide",on:{click:function($event){_vm.hideLeagend=!0}}},[_c("div",{staticClass:"sidebarhide-bar"},[_vm.hideLeagend?_c("i",{staticClass:"el-icon-arrow-right"}):_c("i",{staticClass:"el-icon-d-arrow-left"})])])],1),_vm.hideLeagend?_c("div",{staticClass:"show-leadends-text",on:{click:function($event){_vm.hideLeagend=!1}}},[_vm._v(" Show Legend "),_c("i",{staticClass:"el-icon-d-arrow-left"})]):_vm._e()])},staticRenderFns=[],util=(__webpack_require__(260228),__webpack_require__(947522),__webpack_require__(434284),__webpack_require__(664333)),BookingViewLeagendsvue_type_script_lang_js={props:["departments","deskList","bookings","lockers","parkingList","settings","spaceList"],data:function(){return{activeTab:"availability",hideLeagend:!0}},computed:{getParrkingUnassignedColor:function(){var availableColor=this.settings.bookingState.availableColor;return availableColor||"#22ae5c"},getParrkingAssignedColor:function(){var notAvailableColor=this.settings.bookingState.notAvailableColor;return notAvailableColor||"#dc4a4c"},getParkingReservableColor:function(){var nonReservableColor=this.settings.bookingState.nonReservableColor;return nonReservableColor||"#dc4a4c"},getNonReservalbleList:function(){return(0,util/* getNonReservalbleList */.DM)(this.parkingList)},unAssignedParking:function(){return(0,util/* getUnassignedList */.Iv)(this.parkingList)},getVacantList:function(){return(0,util/* getVacantList */.bu)(this.parkingList)},getBookedList:function(){return(0,util/* getBookedList */.z6)(this.parkingList)},assignedParking:function(){return(0,util/* getAssignedList */.vU)(this.parkingList)},unAssignedLockers:function(){return(0,util/* getUnassignedList */.Iv)(this.lockers)},assignedLockers:function(){return(0,util/* getAssignedList */.vU)(this.lockers)},departmentVsDesk:function(){var _this=this,departmentMap={};return this.deskList.forEach((function(desk){if(desk.department)if(departmentMap[desk.department.name]){var count=departmentMap[desk.department.name]+1;_this.$set(departmentMap,desk.department.name,count)}else _this.$set(departmentMap,desk.department.name,1)})),departmentMap},availability:function(){var availableColor=this.settings.bookingState.availableColor,bookedColor=this.settings.bookingState.notAvailableColor,nonReservableColor=this.settings.bookingState.nonReservableColor,availabilityMap=[{name:"Available",value:0,color:availableColor},
// { name: 'Partially available', value: 0, color: '#ffe4a6' },
//partial state pendings
{name:"Booked",value:0,color:bookedColor},{name:"Non reservable",value:this.deskVsType.assign,color:nonReservableColor,opacity:.4}];
//get available and unavailable count ,check if desk has booking entry
return this.deskList.forEach((function(desk){3!=desk.deskType&&2!=desk.deskType||(desk.isBooked?availabilityMap[1].value++:availabilityMap[0].value++)})),availabilityMap},deskVsType:function(){var statusCount={hot:0,hotel:0,assign:0};return this.deskList.forEach((function(desk){if(3===desk.deskType){var count=statusCount["hot"]+1;statusCount["hot"]=count}else if(2===desk.deskType){var _count=statusCount["hotel"]+1;statusCount["hotel"]=_count}else if(1===desk.deskType){var _count2=statusCount["assign"]+1;statusCount["assign"]=_count2}})),statusCount}},watch:{lockers:{handler:function(){this.setView()},immediate:!0}},mounted:function(){this.setView()},methods:{setView:function(){this.lockers.length&&!this.deskList.length?this.activeTab="locker":this.parkingList.length&&!this.deskList.length&&(this.activeTab="parking")}}},components_BookingViewLeagendsvue_type_script_lang_js=BookingViewLeagendsvue_type_script_lang_js,componentNormalizer=__webpack_require__(801001),component=(0,componentNormalizer/* default */.Z)(components_BookingViewLeagendsvue_type_script_lang_js,render,staticRenderFns,!1,null,"323f6fd7",null)
/* harmony default export */,BookingViewLeagends=component.exports}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/28899.js.map