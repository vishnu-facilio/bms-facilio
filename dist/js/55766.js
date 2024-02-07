"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[55766],{
/***/455766:
/***/function(__unused_webpack___webpack_module__,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */j:function(){/* binding */return SchedulerComponent}
/* harmony export */});
/* harmony import */__webpack_require__(891719),__webpack_require__(670560)
/* harmony import */;var _facilio_api__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(32284),_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__=__webpack_require__(990260),moment_timezone__WEBPACK_IMPORTED_MODULE_4__=(__webpack_require__(900278),__webpack_require__(480008)),lodash__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(496486),dlv__WEBPACK_IMPORTED_MODULE_6__=__webpack_require__(226905),_facilio_ui_app__WEBPACK_IMPORTED_MODULE_7__=__webpack_require__(782080);const getMoment=(timezone,dateObj)=>moment_timezone__WEBPACK_IMPORTED_MODULE_4__.tz(dateObj,timezone),calenderViews={1:"Day",2:"Week",3:"Month",4:"Year"},viewState={1:{gridLines:"1H"},2:{gridLines:"DAY"},3:{gridLines:"DAY"},4:{gridLines:"WEEK"}},weekdays=["Sun","Mon","Tues","Wed","Thur","Fri","Sat"],dateOperator={1:19,2:12,3:12,4:11},intervalObj={DAY:"day",WEEK:"week",MONTH:"month","1H":"hours"},WEEK_DAYS={1:"Mon",2:"Tues",3:"Wed",4:"Thur",5:"Fri",6:"Sat",7:"Sun"},dataTypes={BOOLEAN:4,DATE:5,DATE_TIME:6,LOOKUP:7,ENUM:8,SYSTEM_ENUM:12},lookupType={BASE_ENTITY:1,PICK_LIST:2},loadSchedulerData=async params=>{let url="v3/modules/data/timeline/get",{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.post(url,params);return error?{error:error}:{data:(null===data||void 0===data?void 0:data.timelineData)||{}}},loadSchedulerDataList=async(params,moduleName)=>{let url="v3/modules/data/timeline/list",{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.post(url,params);if(error)return{error:error};{let{[moduleName]:list}=data||{};return{data:list||[]}}},updateSchedulerEvent=async params=>{let url="v3/modules/data/timeline/update",{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.post(url,params);return error?{error:error}:{data:(null===data||void 0===data?void 0:data.timelineData)||{}}},loadUnscheduledDataList=async(params,moduleName)=>{let url="v3/modules/data/timeline/unscheduled",{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.post(url,params);if(error)return{error:error};{let{[moduleName]:list}=data||{};return{data:list||[]}}},formatDate=(date,{timezone:timezone,dateformat:dateformat,timeformat:timeformat})=>moment_timezone__WEBPACK_IMPORTED_MODULE_4__(date).tz(timezone).format(dateformat+" "+timeformat),DATA_TYPE_VALUE_HASH={LOOKUP(field,data){let{name:fieldName}=field,{primaryValue:primaryValue,name:name,displayName:displayName,subject:subject}=data[fieldName]||{},value=displayName||name||subject;return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(primaryValue)?value:primaryValue},FILE(field,data){let filename=data[`${field.name}FileName`];return filename},DATE(field,data,formatDetails){let{name:name}=field||{},value=data[name];return value&&-1!==value?formatDate(value,formatDetails):""},DATE_TIME(field,data,formatDetails){let{name:name}=field||{},value=data[name];return value&&-1!==value?formatDate(value,formatDetails):""},ENUM(field,data){let{name:name,enumMap:enumMap}=field||{},value=data[name];return value=value?enumMap[data[name]]:value,value},SYSTEM_ENUM(field,data){let{name:name,enumMap:enumMap}=field||{},value=data[name];return value=value?enumMap[data[name]]:value,value},MULTI_ENUM(field,data){let{name:name,enumMap:enumMap}=field||{},values=data[name]||[],valueStr=values.reduce(((accStr,value)=>{let str=enumMap[value]||"";return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(accStr)?`${str}`:`${accStr}, ${str}`}),"");return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(valueStr)?"---":valueStr},BOOLEAN(field,data){let{name:name}=field||{},value=data[name]?field.trueVal||"Yes":field.falseVal||"No";return value},MULTI_LOOKUP(field,data){let{name:name}=field,value=data[name]||[],lookupRecordNames=(value||[]).map((currRecord=>currRecord.displayName||currRecord.name||currRecord.subject));return lookupRecordNames.length>2?`${lookupRecordNames.slice(0,2).join(", ")} +${Math.abs(lookupRecordNames.length-2)}`:(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(lookupRecordNames)?"---":`${lookupRecordNames.join(", ")}`},OTHERS(field,data){let{name:name}=field||{};if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isObject */.Kn)(data[name]))return data[name];{let{name:name}=data[name]||{};if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(name))return name;data[name]}}};var PushPin={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 508.096 508.096"}},[_c("path",{attrs:{d:"M501.925 126.048l-120-120c-4-4-8-6-12.4-6-6.8 0-12 4.8-14.4 12.8l-5.6 18.8c-2.8 10-11.6 24.8-19.2 32.4l-97.6 97.2c-7.6 7.6-22 16.4-32 19.6l-56.4 18.4c-6.4 2-10.8 6.4-12 11.6-1.2 5.2.8 10.8 5.6 15.6l56 56.4-120.8 120.8-.4.4-72 97.6c-1.2 1.6-.8 3.6.4 5.2.8.8 1.6 1.2 2.8 1.2.8 0 1.6-.4 2.4-.8l97.6-72 .4-.4 120.8-120.8 56.4 56.8c4 4 8.4 6.4 12.4 6.4 6.8 0 12-5.2 14.8-12.8l18.4-56.8c3.2-10 12.4-24.8 19.6-32.4l97.6-97.6c7.6-7.6 22-16.4 32.4-19.2l18.8-5.6c6.4-2 10.8-6 12.4-11.2.8-4.8-1.2-10.8-6-15.6z"}})])}},LeftArrow={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{viewBox:"0 0 24 24",xmlns:"http://www.w3.org/2000/svg"}},[_c("path",{attrs:{d:"M18.364 7.757l-1.415-1.414L11.293 12l5.656 5.657 1.415-1.414L14.12 12l4.243-4.243z"}}),_c("path",{attrs:{d:"M11.293 6.343l1.414 1.414L8.464 12l4.243 4.243-1.414 1.414L5.636 12l5.657-5.657z"}})])}},RightArrow={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{viewBox:"0 0 24 24",xmlns:"http://www.w3.org/2000/svg"}},[_c("path",{attrs:{d:"M5.636 7.757L7.05 6.343 12.707 12 7.05 17.657l-1.414-1.414L9.878 12 5.636 7.757z"}}),_c("path",{attrs:{d:"M12.707 6.343l-1.414 1.414L15.535 12l-4.242 4.243 1.414 1.414L18.364 12l-5.657-5.657z"}})])}},script$8={data(){return{isResized:!1,isDragging:!1,resizingElement:null,draggingElement:null,resizeHighlighter:{startTimeIdx:-1,endTimeIdx:-1},activeEventTarget:null}},computed:{filters(){let{query:query}=this.$route||{},{search:search}=query||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(search)?null:JSON.parse(search)},hasActionOngoing(){return this.isResized||this.isDragging},hasAssignEnabled(){let{allowGroupAssignment:allowGroupAssignment}=this.viewDetails||{};return allowGroupAssignment},hasRescheduleEnabled(){let{allowRescheduling:allowRescheduling}=this.viewDetails||{};return allowRescheduling},hasReAssignEnabled(){let{allowReAssignment:allowReAssignment}=this.viewDetails||{};return allowReAssignment},weekendIndex(){let{headerGridColumns:headerGridColumns,viewDetails:viewDetails}=this,{weekend:weekend}=viewDetails||{},{valueJSON:valueJSON}=weekend||{},{All:All}=valueJSON||{},weekendVsDay=(All||[]).reduce(((weekendObj,day)=>(weekendObj[WEEK_DAYS[day]]=day,weekendObj)),{});return headerGridColumns.reduce(((indexArray,grid,gridIndex)=>(weekendVsDay[grid.day]&&indexArray.push(gridIndex),indexArray)),[])},disabledWeekend(){let{disableWeekends:disableWeekends}=this.viewDetails||{};return disableWeekends},disabledPastDate(){let{allowPastAssignment:allowPastAssignment}=this.viewDetails||{};return!allowPastAssignment},disabledPastEvents(){let{disablePastEvents:disablePastEvents}=this.viewDetails||{};return disablePastEvents}},methods:{mouseDownEvent(domEvent,eventObj){var _attributes$sidePosi;let{target:target}=domEvent,{attributes:attributes,parentElement:eventTarget}=target||{},blockIndex=parseInt(attributes["block-index"].value),grpId=attributes["group-id"].value,eventPosition=null===(_attributes$sidePosi=attributes["side-position"])||void 0===_attributes$sidePosi?void 0:_attributes$sidePosi.value;this.isResized=!0,this.resizingElement={blockIndex:blockIndex,grpId:grpId,eventPosition:eventPosition,eventObj:eventObj,eventTarget:eventTarget};let{blockDivision:blockDivision}=this,{hasAfterLimit:hasAfterLimit,workEnd:workEnd}=eventObj||{};this.resizeHighlighter.startTimeIdx=blockIndex,this.resizeHighlighter.endTimeIdx=hasAfterLimit?blockDivision-1:this.headerGridColumns.findIndex((grid=>grid.startTime<=workEnd&&grid.endTime>=workEnd))},mouseMoveEvent(domEvent){var _attributes$blockInd,_attributes$eventId,_attributes$startSco;if(!this.isResized)return;domEvent.preventDefault();let{target:target}=domEvent,{attributes:attributes}=target||{},blockIndex=parseInt(null===(_attributes$blockInd=attributes["block-index"])||void 0===_attributes$blockInd?void 0:_attributes$blockInd.value),eventId=null===(_attributes$eventId=attributes["event-id"])||void 0===_attributes$eventId?void 0:_attributes$eventId.value,startScope=null===(_attributes$startSco=attributes["start-scope"])||void 0===_attributes$startSco?void 0:_attributes$startSco.value,{eventObj:eventObj,eventPosition:eventPosition,eventTarget:eventTarget}=this.resizingElement||{},{startTimeIdx:startTimeIdx,endTimeIdx:endTimeIdx}=this.resizeHighlighter||{},{id:id}=eventObj,{blockDivision:blockDivision,disabledWeekend:disabledWeekend,weekendIndex:weekendIndex,disabledPastDate:disabledPastDate,headerGridColumns:headerGridColumns}=this,{endTime:endTime}=headerGridColumns[blockIndex]||{},currentTime=this.getTimeStamp().valueOf();if(parseInt(eventId)===id&&"true"===startScope&&endTimeIdx!==blockIndex+1||disabledWeekend&&weekendIndex.includes(blockIndex)||disabledPastDate&&currentTime>endTime)return;"start"===eventPosition?(startTimeIdx=blockIndex<=endTimeIdx?blockIndex:endTimeIdx,eventTarget.style.left=`calc(${startTimeIdx} * (100%/${blockDivision}))`):"end"===eventPosition&&(endTimeIdx=blockIndex>=startTimeIdx?blockIndex:startTimeIdx);let width=endTimeIdx-startTimeIdx+1;eventTarget.style.zIndex=1,eventTarget.style.width=`calc(((100%/${blockDivision}) * ${width}) - 10px)`,this.resizeHighlighter={startTimeIdx:startTimeIdx,endTimeIdx:endTimeIdx}},async mouseUpEvent(){if(!this.isResized||(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(this.resizingElement))return;let{blockIndex:blockIndex,eventObj:eventObj,grpId:grpId,eventPosition:eventPosition,eventTarget:eventTarget}=this.resizingElement||{},{startTimeIdx:startTimeIdx,endTimeIdx:endTimeIdx}=this.resizeHighlighter||{},{workStart:workStart,workEnd:workEnd,id:id,name:name,isNeedConfirm:isNeedConfirm}=eventObj;if(isNeedConfirm){let value=await this.getConfirmation({name:name,id:id});if(!value)return}let elementEndBlockIdx=blockIndex,{endTime:blockEndTime}=this.headerGridColumns[blockIndex]||{};if(workEnd>blockEndTime){let remainingDiff=workEnd-blockEndTime;elementEndBlockIdx+=Math.ceil(remainingDiff/this.blockTime)}let isEventResized=blockIndex!==startTimeIdx||elementEndBlockIdx!==endTimeIdx;if(isEventResized){if("start"===eventPosition){if(blockIndex>startTimeIdx)workStart-=(blockIndex-startTimeIdx)*this.blockTime;else if(blockIndex<startTimeIdx){let movedStart=workStart+(startTimeIdx-blockIndex)*this.blockTime;workEnd>movedStart&&(workStart=movedStart)}}else if("end"===eventPosition)if(elementEndBlockIdx<endTimeIdx)workEnd+=(endTimeIdx-elementEndBlockIdx)*this.blockTime;else if(elementEndBlockIdx>endTimeIdx){let movedEnd=workEnd-(elementEndBlockIdx-endTimeIdx)*this.blockTime;workStart<movedEnd&&(workEnd=movedEnd)}let rowEvents=this.eventList[grpId]||{},blockEvents=rowEvents[blockIndex]||{},eventIndex=blockEvents.findIndex((evnt=>evnt.id===id));-1!==eventIndex&&(blockEvents.splice(eventIndex,1,{...eventObj,cursor:"wait",startResizeDisabled:!0,endResizeDisabled:!0,draggable:!1}),this.$set(rowEvents,blockIndex,blockEvents),this.$set(this.eventList,grpId,rowEvents)),this.updateEvent({id:id,workEnd:workEnd,workStart:workStart},[grpId])}this.activeEventTarget=eventTarget,this.isResized=!1,this.resizingElement=null,this.resizeHighlighter={startTimeIdx:-1,endTimeIdx:-1}},addDragEventListener(){document.addEventListener("dragstart",this.dragStartEvent,!1),document.addEventListener("dragend",this.dragEndEvent,!1),document.addEventListener("dragover",this.dragOverEvent,!1),document.addEventListener("dragenter",this.dragEnterEvent,!1),document.addEventListener("dragleave",this.dragLeaveEvent,!1),document.addEventListener("drop",this.dropEvent,!1)},dragStartEvent(event){if(event.target.className.includes("event-class")||event.target.className.includes("sidebar-event")){var _attributes$groupId,_attributes$blockInd2;let draggedEvent=event.target,{attributes:attributes}=draggedEvent||{},grpId=null===(_attributes$groupId=attributes["group-id"])||void 0===_attributes$groupId?void 0:_attributes$groupId.value,eventId=parseInt(attributes["event-id"].value),blockIndex=parseInt(null===(_attributes$blockInd2=attributes["block-index"])||void 0===_attributes$blockInd2?void 0:_attributes$blockInd2.value),eventObj=(this.eventList[grpId][blockIndex]||[]).find((e=>e.id===eventId))||{};draggedEvent.style.opacity=.5,this.draggingElement={eventId:eventId,grpId:grpId,blockIndex:blockIndex,draggedEvent:draggedEvent,eventObj:eventObj},this.isDragging=!0}},dragEndEvent(event){event.target.style.opacity="",this.isDragging=!1,this.draggingElement=null},dragOverEvent(event){event.preventDefault()},dragEnterEvent(event){let eventClassName=event.target.className||[];if(event.dataTransfer.dropEffect="move",eventClassName.includes("grid-item")&&!eventClassName.includes("grid-item-disabled")){let{attributes:blockAttributes}=event.target||{},blockIndex=parseInt(blockAttributes["block-index"].value);this.resizeHighlighter={startTimeIdx:blockIndex,endTimeIdx:blockIndex},event.target.style.background="#f2fafc",event.target.parentElement.style.cursor="grabbing"}},dragLeaveEvent(event){let eventClassName=event.target.className||[];eventClassName.includes("grid-item")&&!eventClassName.includes("grid-item-disabled")&&(event.target.style.background="",event.target.parentElement.style.cursor="")},async dropEvent(event){if(!this.isDragging)return;event.target.style.background="",event.target.parentElement.style.cursor="";let{attributes:blockAttributes}=event.target||{},blockIndex=parseInt(blockAttributes["block-index"].value),grpId=blockAttributes["group-id"].value,{eventId:eventId,grpId:eventGrpId,blockIndex:eventBlockIndex,draggedEvent:draggedDOMEvent,eventObj:draggedEventObj}=this.draggingElement||{};draggedDOMEvent&&(draggedDOMEvent.style.opacity="",draggedDOMEvent.style.zIndex=1,draggedDOMEvent.style.border="1px solid rgb(0, 0, 0, 0.2)",draggedDOMEvent.style.boxShadow="0px 3px 5px 0px rgb(0 0 0 / 30%)",this.activeEventTarget=draggedDOMEvent);let isValidDrop=this.dropValidation({grpId:grpId,eventGrpId:eventGrpId,blockIndex:blockIndex,eventObj:draggedEventObj});if(isValidDrop)this.throwWarning();else{let{id:id,name:name,isNeedConfirm:isNeedConfirm}=draggedEventObj;if(isNeedConfirm){let value=await this.getConfirmation({name:name,id:id});if(!value)return draggedDOMEvent.style.zIndex="",draggedDOMEvent.style.border="",draggedDOMEvent.style.boxShadow="",void(this.activeEventTarget=null)}if("sidebar-event"===draggedDOMEvent.className){const UNSCHEDULED_LIST=this.$refs["sidebar-list"];let{recordList:recordList,totalCount:totalCount}=UNSCHEDULED_LIST||{},recordIdx=(recordList||[]).findIndex((r=>r.id===eventId));if(recordIdx>-1){let grpIdToMove,record=recordList[recordIdx]||{},eventObj={...record,blockIndex:blockIndex,startScope:!0,className:"",cursor:"wait",startResizeDisabled:!0,endResizeDisabled:!0,draggable:!1},{groupName:groupName,blockIndex:eventBlockIndex}=this.sideBarDetails||{};if(groupName)grpIdToMove=grpId,this.moveEvent({eventObj:eventObj,blockIndex:blockIndex,eventBlockIndex:eventBlockIndex,grpId:grpId,eventGrpId:eventGrpId});else{let{startTime:startTime,endTime:endTime}=this.headerGridColumns[blockIndex]||{};grpIdToMove=-1===parseInt(eventGrpId)?grpId:eventGrpId,eventObj={...eventObj,workStart:startTime,workEnd:endTime,cursor:"wait",startResizeDisabled:!0,endResizeDisabled:!0,draggable:!1};let{workEnd:workEnd,workStart:workStart,id:id}=eventObj;this.updateMovedEvent({grpId:grpIdToMove,eventObj:eventObj,blockIndex:blockIndex}),this.constructEventPosition(grpId),this.updateEvent({groupFieldId:grpIdToMove,workEnd:workEnd,workStart:workStart,id:id},[grpIdToMove])}UNSCHEDULED_LIST.recordList.splice(recordIdx,1),UNSCHEDULED_LIST.totalCount=totalCount-1,this.scrollCurrentBlock(`grid-event-${grpIdToMove}-${blockIndex}`),this.$forceUpdate()}}else{let curRowEvents=this.eventList[eventGrpId]||[],curEventList=curRowEvents[eventBlockIndex]||[],eventIdx=curEventList.findIndex((event=>event.id===eventId));if(-1===eventIdx)return;let eventObj=curEventList[eventIdx]||{};eventObj={...eventObj,cursor:"wait",startResizeDisabled:!0,endResizeDisabled:!0,draggable:!1},this.moveEvent({eventObj:eventObj,blockIndex:blockIndex,eventBlockIndex:eventBlockIndex,grpId:grpId,eventGrpId:eventGrpId})}}this.isDragging=!1,this.draggingElement=null,this.resizeHighlighter={startTimeIdx:-1,endTimeIdx:-1}},dropValidation({grpId:grpId,eventGrpId:eventGrpId,blockIndex:blockIndex,eventObj:eventObj}){let{hasAssignEnabled:hasAssignEnabled,hasRescheduleEnabled:hasRescheduleEnabled,hasReAssignEnabled:hasReAssignEnabled,disabledWeekend:disabledWeekend,weekendIndex:weekendIndex,disabledPastDate:disabledPastDate,headerGridColumns:headerGridColumns}=this,{endTime:endTime}=headerGridColumns[blockIndex]||{},currentTime=this.getTimeStamp().valueOf(),{blockSpan:blockSpan}=eventObj||{},eventEndBlockIdx=blockIndex+(blockSpan-1);return(!hasRescheduleEnabled||hasRescheduleEnabled&&grpId!==eventGrpId)&&(!hasReAssignEnabled||hasReAssignEnabled&&[parseInt(eventGrpId),parseInt(grpId)].includes(-1))&&(!hasAssignEnabled||hasAssignEnabled&&-1!==parseInt(eventGrpId))||disabledWeekend&&(weekendIndex.includes(blockIndex)||weekendIndex.includes(eventEndBlockIdx))||disabledPastDate&&currentTime>endTime},throwWarning:(0,lodash__WEBPACK_IMPORTED_MODULE_5__.debounce)((function(){let{displayName:viewDisplayName}=this.viewDetails||{};this.$message.warning(`${viewDisplayName} has unauthorized to perform last action`)}),500),updateMovedEvent(details){let{eventObj:eventObj,blockIndex:blockIndex,eventBlockIndex:eventBlockIndex,grpId:grpId,eventGrpId:eventGrpId}=details||{},{id:id}=eventObj;if(eventGrpId){let rowEvents=this.eventList[eventGrpId]||{},blockEvents=rowEvents[eventBlockIndex]||{},eventIndex=blockEvents.findIndex((evnt=>evnt.id===id));eventIndex>-1&&(blockEvents.splice(eventIndex,1),this.$set(rowEvents,eventBlockIndex,blockEvents),this.$set(this.eventList,eventGrpId,rowEvents))}let{maxResultPerCell:maxResultPerCell}=this,rowEvents=this.eventList[grpId]||{},blockEvents=rowEvents[blockIndex]||{};blockEvents.push(eventObj),blockEvents=(0,lodash__WEBPACK_IMPORTED_MODULE_5__.sortBy)(blockEvents,["workStart"]).slice(0,maxResultPerCell),this.$set(rowEvents,blockIndex,blockEvents),this.$set(this.eventList,grpId,rowEvents);let rowEventPosition=this.eventsPositionMap[grpId]||{};rowEventPosition[id]=0,this.$set(this.eventsPositionMap,grpId,rowEventPosition)},moveEvent(details){let{eventObj:eventObj,blockIndex:blockIndex,eventBlockIndex:eventBlockIndex,grpId:grpId,eventGrpId:eventGrpId}=details||{},{workStart:workStart,workEnd:workEnd,id:id}=eventObj||{},isEventMoved=eventBlockIndex!==blockIndex||grpId!==eventGrpId;if(!isEventMoved)return;if(eventBlockIndex>blockIndex){let dayDiff=(eventBlockIndex-blockIndex)*this.blockTime;workStart-=dayDiff,workEnd-=dayDiff}else{let dayDiff=(blockIndex-eventBlockIndex)*this.blockTime;workStart+=dayDiff,workEnd+=dayDiff}this.updateMovedEvent(details);let grpList=[eventGrpId],groupFieldId=grpId;grpId!==eventGrpId&&grpList.push(grpId),this.updateEvent({groupFieldId:groupFieldId,workEnd:workEnd,workStart:workStart,id:id},grpList)},async updateEvent(data,grpList){let{startDateField:startDateField,endDateField:endDateField,groupByField:groupByField}=this.viewDetails||{},{name:startTimeKey}=startDateField||{},{name:endTimeKey}=endDateField||{},{name:groupByKey,dataType:dataType}=groupByField||{},{workEnd:workEnd,workStart:workStart,groupFieldId:groupFieldId,id:id}=data||{},payload={id:id,data:{id:id,[startTimeKey]:workStart,[endTimeKey]:workEnd}};if(groupFieldId){let grpId=parseInt(groupFieldId);if(-1!==grpId){let{ENUM:ENUM,SYSTEM_ENUM:SYSTEM_ENUM,LOOKUP:LOOKUP}=dataTypes||{};[ENUM,SYSTEM_ENUM].includes(dataType)?payload.data[groupByKey]=grpId:dataType===LOOKUP&&(payload.data[groupByKey]={id:grpId})}else payload.data[groupByKey]=null}this.saving=!0,this.$emit("updateGrpEvents",grpList,payload)},async getConfirmation(eventObj){let{startDateField:startDateField,endDateField:endDateField}=this.viewDetails||{},{displayName:startDateFieldName}=startDateField||{},{displayName:endDateFieldName}=endDateField||{};return await this.dialogPrompt.confirm({title:"Warning",htmlMessage:`${eventObj.name}(${eventObj.id}) has ${endDateFieldName}(end time field) before ${startDateFieldName}(start time field). As default value, end time will be 1 hour ahead of start time. Performing actions will change actual end time. Do you want to update?`,rbDanger:!0,rbLabel:"Confirm"})}}};function normalizeComponent(template,style,script,scopeId,isFunctionalTemplate,moduleIdentifier/* server only */,shadowMode,createInjector,createInjectorSSR,createInjectorShadow){"boolean"!==typeof shadowMode&&(createInjectorSSR=createInjector,createInjector=shadowMode,shadowMode=!1);
// Vue.extend constructor export interop.
const options="function"===typeof script?script.options:script;
// render functions
let hook;if(template&&template.render&&(options.render=template.render,options.staticRenderFns=template.staticRenderFns,options._compiled=!0,
// functional template
isFunctionalTemplate&&(options.functional=!0)),
// scopedId
scopeId&&(options._scopeId=scopeId),moduleIdentifier?(
// server build
hook=function(context){
// 2.3 injection
context=context||// cached call
this.$vnode&&this.$vnode.ssrContext||// stateful
this.parent&&this.parent.$vnode&&this.parent.$vnode.ssrContext,// functional
// 2.2 with runInNewContext: true
context||"undefined"===typeof __VUE_SSR_CONTEXT__||(context=__VUE_SSR_CONTEXT__),
// inject component styles
style&&style.call(this,createInjectorSSR(context)),
// register component module identifier for async chunk inference
context&&context._registeredComponents&&context._registeredComponents.add(moduleIdentifier)},
// used by ssr in case component is cached and beforeCreate
// never gets called
options._ssrRegister=hook):style&&(hook=shadowMode?function(context){style.call(this,createInjectorShadow(context,this.$root.$options.shadowRoot))}:function(context){style.call(this,createInjector(context))}),hook)if(options.functional){
// register for functional component in vue file
const originalRender=options.render;options.render=function(h,context){return hook.call(context),originalRender(h,context)}}else{
// inject component registration as beforeCreate hook
const existing=options.beforeCreate;options.beforeCreate=existing?[].concat(existing,hook):[hook]}return script}
/* script */const __vue_script__$8=script$8,__vue_inject_styles__$8=void 0,__vue_scope_id__$8=void 0,__vue_module_identifier__$8=void 0,__vue_is_functional_template__$8=void 0,__vue_component__$8=normalizeComponent({},__vue_inject_styles__$8,__vue_script__$8,__vue_scope_id__$8,__vue_is_functional_template__$8,__vue_module_identifier__$8,!1,void 0,void 0,void 0);
/* template */
/* style */
var script$7={methods:{close(){this.$parent.$emit("onClose")}}};const isOldIE="undefined"!==typeof navigator&&/msie [6-9]\\b/.test(navigator.userAgent.toLowerCase());function createInjector(context){return(id,style)=>addStyle(id,style)}let HEAD;const styles={};function addStyle(id,css){const group=isOldIE?css.media||"default":id,style=styles[group]||(styles[group]={ids:new Set,styles:[]});if(!style.ids.has(id)){style.ids.add(id);let code=css.source;if(css.map&&(
// https://developer.chrome.com/devtools/docs/javascript-debugging
// this makes source maps inside style tags work properly in Chrome
code+="\n/*# sourceURL="+css.map.sources[0]+" */",
// http://stackoverflow.com/a/26603875
code+="\n/*# sourceMappingURL=data:application/json;base64,"+btoa(unescape(encodeURIComponent(JSON.stringify(css.map))))+" */"),style.element||(style.element=document.createElement("style"),style.element.type="text/css",css.media&&style.element.setAttribute("media",css.media),void 0===HEAD&&(HEAD=document.head||document.getElementsByTagName("head")[0]),HEAD.appendChild(style.element)),"styleSheet"in style.element)style.styles.push(code),style.element.styleSheet.cssText=style.styles.filter(Boolean).join("\n");else{const index=style.ids.size-1,textNode=document.createTextNode(code),nodes=style.element.childNodes;nodes[index]&&style.element.removeChild(nodes[index]),nodes.length?style.element.insertBefore(textNode,nodes[index]):style.element.appendChild(textNode)}}}
/* script */const __vue_script__$7=script$7;
/* template */var __vue_render__$7=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"sidebar-list-container"},[_c("div",{staticClass:"sidebar-open-popup"},[_c("div",{staticClass:"sidebar-header"},[_c("div",{staticClass:"header-text"},[_vm._t("title")],2),_vm._v(" "),_c("div",{staticClass:"hide-text",on:{click:_vm.close}},[_vm._v("Hide")])]),_vm._v(" "),_c("div",{staticClass:"header-actions"},[_vm._t("header-actions")],2),_vm._v(" "),_c("div",{staticClass:"sidebar-event-list"},[_vm._t("list")],2)])])},__vue_staticRenderFns__$7=[];
/* style */
const __vue_inject_styles__$7=function(inject){inject&&inject("data-v-d238c104_0",{source:".sidebar-list-container[data-v-d238c104]{z-index:4;box-shadow:-1px 0 2px 0 rgba(0,0,0,.11);background:#fff}.sidebar-list-container .sidebar-open-popup[data-v-d238c104]{height:100%;width:300px}.sidebar-list-container .sidebar-open-popup .sidebar-header[data-v-d238c104]{display:flex;justify-content:space-between;align-items:center;padding:15px;padding-bottom:20px}.sidebar-list-container .sidebar-open-popup .sidebar-header .header-text[data-v-d238c104]{font-size:14px;font-weight:700;color:#000}.sidebar-list-container .sidebar-open-popup .sidebar-header .hide-text[data-v-d238c104]{font-size:14px;cursor:pointer;color:#4f4f4f;padding:5px 9px;border-radius:4px}.sidebar-list-container .sidebar-open-popup .sidebar-header .hide-text[data-v-d238c104]:hover{background:#f3f4f7}.sidebar-list-container .sidebar-open-popup .header-actions[data-v-d238c104]{display:flex;align-items:center;justify-content:space-between;padding:0 15px 15px;border-bottom:1px solid #e0e0e0}.sidebar-list-container .sidebar-open-popup .sidebar-event-list[data-v-d238c104]{overflow:scroll;height:calc(100% - 105px);padding:5px 15px;margin-top:5px}.sidebar-list-container .sidebar-open-popup .sidebar-event-list .empty-list[data-v-d238c104]{display:flex;justify-content:center;align-items:center;height:100%}.sidebar-list-container .sidebar-open-popup .sidebar-event-list .color-item[data-v-d238c104]{display:flex;align-items:center;margin-bottom:5px;min-height:40px}.sidebar-list-container .sidebar-open-popup .sidebar-event-list .color-item .color-block[data-v-d238c104]{margin-right:10px;height:20px;width:20px;border-radius:5px;border:1px solid rgba(0,0,0,.1)}.sidebar-list-container .sidebar-open-popup .sidebar-event-list .color-item .option-text[data-v-d238c104]{font-size:14px;font-weight:400;letter-spacing:.4px;color:#324056}",map:void 0,media:void 0})},__vue_scope_id__$7="data-v-d238c104",__vue_module_identifier__$7=void 0,__vue_is_functional_template__$7=!1,__vue_component__$7=normalizeComponent({render:__vue_render__$7,staticRenderFns:__vue_staticRenderFns__$7},__vue_inject_styles__$7,__vue_script__$7,__vue_scope_id__$7,__vue_is_functional_template__$7,__vue_module_identifier__$7,!1,createInjector,void 0,void 0);
/* scoped */var DragHandle={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{viewBox:"0 0 9 16",xmlns:"http://www.w3.org/2000/svg"}},[_c("g",{attrs:{"fill-rule":"evenodd"}},[_c("circle",{attrs:{cx:"7",cy:"2",r:"1.5"}}),_c("circle",{attrs:{cx:"7",cy:"8",r:"1.5"}}),_c("circle",{attrs:{cx:"7",cy:"14",r:"1.5"}}),_c("circle",{attrs:{cx:"2",cy:"2",r:"1.5"}}),_c("circle",{attrs:{cx:"2",cy:"8",r:"1.5"}}),_c("circle",{attrs:{cx:"2",cy:"14",r:"1.5"}})])])}},script$6={props:["hasMoreList","currentPage","perPage","currentCount"],data(){return{from:0,to:0,page:1}},mounted(){this.init()},watch:{currentCount(){this.init()},currentPage(val){val!==this.page&&this.init()}},methods:{init(){let{currentPage:currentPage,currentCount:currentCount,perPage:perPage}=this;this.page=currentPage||1,this.from=(this.page-1)*perPage+1,this.to=(this.page-1)*perPage+currentCount},next(){this.hasMoreList&&(this.from=this.to+1,this.to+=this.perPage,this.page++,this.$emit("update:currentPage",this.page))},prev(){this.from<=1||(this.to=this.from-1,this.from-=this.perPage,this.from<=1?this.from=this.page=1:this.page--,this.$emit("update:currentPage",this.page))}}};




























/* script */
const __vue_script__$6=script$6;
/* template */var __vue_render__$6=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _vm.currentCount>0?_c("div",{staticClass:"grp-pagination-text"},[_c("el-tooltip",{attrs:{disabled:_vm.from<=1,effect:"dark",content:_vm.from-_vm.perPage+" - "+(_vm.from-1),placement:"top-start"}},[_c("span",{staticClass:"el-icon-arrow-left arrow-16",class:{disable:_vm.from<=1},on:{click:_vm.prev}})]),_vm._v(" "),_c("el-tooltip",{attrs:{disabled:!_vm.hasMoreList,effect:"dark",content:_vm.to+1+" - "+(_vm.perPage+_vm.to),placement:"top-end"}},[_c("span",{staticClass:"el-icon-arrow-right arrow-16",class:{disable:!_vm.hasMoreList},on:{click:_vm.next}})])],1):_vm._e()},__vue_staticRenderFns__$6=[];
/* style */
const __vue_inject_styles__$6=function(inject){inject&&inject("data-v-c755826e_0",{source:".grp-pagination-text[data-v-c755826e]{display:flex}.grp-pagination-text .arrow-16[data-v-c755826e]{cursor:pointer;letter-spacing:.5px;font-size:14px;margin-top:auto;font-weight:700;padding:5px;border-radius:4px}.grp-pagination-text .arrow-16[data-v-c755826e]:hover{background:#f3f4f7}.grp-pagination-text .arrow-16.disable[data-v-c755826e]{cursor:not-allowed;color:#a9aacb}",map:void 0,media:void 0})},__vue_scope_id__$6="data-v-c755826e",__vue_module_identifier__$6=void 0,__vue_is_functional_template__$6=!1,__vue_component__$6=normalizeComponent({render:__vue_render__$6,staticRenderFns:__vue_staticRenderFns__$6},__vue_inject_styles__$6,__vue_script__$6,__vue_scope_id__$6,__vue_is_functional_template__$6,__vue_module_identifier__$6,!1,createInjector,void 0,void 0);
/* scoped */
var script$5={props:["borderColor","position","background","isTimelineSideBarOpen","isDragging"],data(){return{showPopup:!1,styleObj:{background:this.background||"#fff",position:"absolute","box-shadow":"0 2px 12px 0 rgb(0 0 0 / 10%)",border:`2px solid ${this.borderColor}`,"border-radius":"4px","min-width":"200px","max-width":"250px",padding:"10px","z-index":10,width:"fit-content"},currentPosition:null}},computed:{arrowPosition(){return this.position||this.currentPosition},arrowStyle(){let sidesVsComponent={left:{top:"5px",right:"-9px",transform:"rotate(90deg)"},right:{top:"5px",left:"-9px",transform:"rotate(270deg)"},top:{left:"5px",bottom:"-6px",transform:"rotate(180deg)"},bottom:{left:"5px",top:"-6px",transform:"rotate(0deg)"},"top-right":{right:"5px",bottom:"-6px",transform:"rotate(180deg)"},"bottom-right":{right:"5px",top:"-6px",transform:"rotate(0deg)"}},{arrowPosition:arrowPosition,borderColor:borderColor}=this;return arrowPosition?{...sidesVsComponent[arrowPosition],"border-bottom-color":borderColor}:{}},filters(){let{query:query}=this.$route||{},{search:search}=query||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(search)?null:JSON.parse(search)}},methods:{mouseEnter(event){this.showPopup||(this.showPopup=!0,this.$nextTick((()=>{this.$set(this.styleObj,"left",""),this.$set(this.styleObj,"right",""),this.$set(this.styleObj,"top","");let{target:target,x:eventX,fromElement:fromElement}=event||{},{offsetHeight:targetHeight,offsetWidth:targetWidth,previousElementSibling:previousElementSibling}=target||{},{top:targetTop,left:targetLeft,right:targetRight,bottom:targetBottom}=target.getBoundingClientRect(),{offsetHeight:popoverHeight,offsetWidth:popoverWidth}=previousElementSibling||{},{position:position,isTimelineSideBarOpen:isTimelineSideBarOpen,filters:filters}=this;if(position){let navbarHeight=49.5,top=targetTop-navbarHeight,left="";"left"===position?left=targetLeft-(popoverWidth+20)-60+"px":"right"===position?left=`${targetLeft+targetWidth+20}px`:"top"===position?top-=popoverHeight+20:"bottom"===position&&(top=top+targetHeight+20),this.$set(this.styleObj,"top",`${top}px`),this.$set(this.styleObj,"left",left)}else{let{innerWidth:innerWidth,innerHeight:innerHeight}=window,{offsetWidth:blockWidth}=fromElement||{},totalWidth=innerWidth-(isTimelineSideBarOpen?300:0),totalHeight=innerHeight,widthToOmit=270+(JSON.parse(localStorage.getItem("fc-view-sidepanel"))?288:0),heightToOmit=170+((0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(filters)?0:65);//navbarHeight + headerHeight + tableMargin + tableHeaderHeight + FtagsHeight
if(targetLeft-widthToOmit>popoverWidth+30&&eventX-targetLeft<blockWidth&&totalHeight-targetTop-10>popoverHeight){let left=-(popoverWidth+30);this.$set(this.styleObj,"left",`${left}px`),this.currentPosition="left"}else if(totalWidth>targetRight+popoverWidth+30&&targetRight-eventX<blockWidth&&totalHeight-targetTop-10>popoverHeight){let right=-(popoverWidth+30);this.$set(this.styleObj,"right",`${right}px`),this.currentPosition="right"}else if(targetTop-(heightToOmit+popoverHeight+20)>0){let top=-(popoverHeight+20),left=eventX-targetLeft;this.currentPosition="top",totalWidth<eventX+popoverWidth&&(left-=popoverWidth,this.currentPosition="top-right"),this.$set(this.styleObj,"top",`${top}px`),this.$set(this.styleObj,"left",`${left}px`)}else if(totalHeight-targetBottom>popoverHeight+20){let top=targetHeight+20,left=eventX-targetLeft+20;this.currentPosition="bottom",totalWidth<eventX+popoverWidth&&(left-=popoverWidth,this.currentPosition="bottom-right"),this.$set(this.styleObj,"top",`${top}px`),this.$set(this.styleObj,"left",`${left}px`)}}})))},mouseLeave(){this.showPopup&&(this.showPopup=!1)}}};
/* script */const __vue_script__$5=script$5;
/* template */var __vue_render__$5=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"popover-dialog"},[_vm.showPopup&&!_vm.isDragging?_c("div",{style:_vm.styleObj},[_vm._t("content"),_vm._v(" "),_c("div",{staticClass:"arrow-outer",style:_vm.arrowStyle},[_c("div",{staticClass:"arrow-inner",style:{"border-bottom-color":_vm.background||"#fff"}})])],2):_vm._e(),_vm._v(" "),_c("div",{on:{mouseenter:_vm.mouseEnter,mouseleave:_vm.mouseLeave}},[_vm._t("default")],2)])},__vue_staticRenderFns__$5=[];
/* style */
const __vue_inject_styles__$5=function(inject){inject&&inject("data-v-51c30f33_0",{source:".popover-dialog .sidebar-event[data-v-51c30f33]{font-size:12px;font-weight:400;letter-spacing:.5px;color:#fff;padding:7px 5px;border-radius:5px;background-color:#4d95ff;margin-bottom:6px;display:flex;align-items:center}.popover-dialog .sidebar-event[data-v-51c30f33]:hover{box-shadow:0 3px 5px 0 rgba(0,0,0,.3)}.popover-dialog .sidebar-event .drag-handle[data-v-51c30f33]{height:16px;width:16px;margin-right:5px;cursor:grab;fill:#fff}.popover-dialog .sidebar-event .event-main-field[data-v-51c30f33]{white-space:nowrap;overflow:hidden;text-overflow:ellipsis;cursor:pointer}.popover-dialog .arrow-outer[data-v-51c30f33]{position:absolute;border-width:5px;border-color:transparent;border-style:solid;border-top-width:0}.popover-dialog .arrow-outer .arrow-inner[data-v-51c30f33]{top:2.5px;left:-5px;position:absolute;border-width:5px;border-color:transparent;border-style:solid;border-top-width:0;border-bottom-color:#fff}",map:void 0,media:void 0})},__vue_scope_id__$5="data-v-51c30f33",__vue_module_identifier__$5=void 0,__vue_is_functional_template__$5=!1,__vue_component__$5=normalizeComponent({render:__vue_render__$5,staticRenderFns:__vue_staticRenderFns__$5},__vue_inject_styles__$5,__vue_script__$5,__vue_scope_id__$5,__vue_is_functional_template__$5,__vue_module_identifier__$5,!1,createInjector,void 0,void 0);
/* scoped */var script$4={props:["moduleName","viewname","viewDetails","groupingListArr","details","canEditRecord","currentModuleState","hasRescheduleEnabled","hasReAssignEnabled","hasAssignEnabled","updatePermission","disabledPastEvents","currentViewTime","mainField","blockTime","blockDivision","timezone","dateformat","timeformat"],components:{SidebarLayout:__vue_component__$7,DragHandle:DragHandle,GroupPagination:__vue_component__$6,PopupDialog:__vue_component__$5},data(){return{page:1,totalCount:0,recordList:[],currentGrpId:null,loading:!1,haslist:!1,isDragging:!1,isNullOrUndefined:_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isNullOrUndefined */.le}},async created(){let{grpId:grpId}=this.details||{};this.currentGrpId=(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isNullOrUndefined */.le)(grpId)?-1:parseInt(grpId),this.addDragEventListener()},computed:{getGroupFieldName(){let{groupByField:groupByField}=this.viewDetails||{},{name:name}=groupByField||{};return name},groupList(){let{groupingListArr:groupingListArr,viewDetails:viewDetails}=this,{groupByField:groupByField}=viewDetails||{},{displayName:displayName}=groupByField||{},groupingList=[],isUngroupAvailable=(groupingListArr||[]).find((pickItem=>-1===pickItem.value));return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(isUngroupAvailable)&&groupingList.unshift({value:-1,label:`No ${displayName}`}),groupingList=[...groupingList,...groupingListArr],groupingList},blockFilters(){let{query:query}=this.$route||{},{search:search}=query||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(search)?null:JSON.parse(search)}},watch:{page(){this.loadUnscheduledList()},currentGrpId(){let{grpId:grpId}=this.details||{};this.page=1,(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isNullOrUndefined */.le)(grpId)?this.loadUnscheduledList():this.loadScheduledList()},details:{handler:"init",deep:!0},viewDetails:"init"},methods:{async init(){let{grpId:grpId}=this.details||{};(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isNullOrUndefined */.le)(grpId)?(this.currentGrpId=-1,await this.loadUnscheduledList()):(this.currentGrpId=parseInt(grpId),await this.loadScheduledList())},getParams(payload={}){let{moduleName:moduleName,blockFilters:blockFilters,currentGrpId:currentGrpId,viewname:viewname,page:page}=this,getUnGrouped=-1===currentGrpId;return{timelineRequest:{moduleName:moduleName,viewName:viewname,groupIds:getUnGrouped||!currentGrpId?[]:[currentGrpId],getUnGrouped:getUnGrouped,filters:blockFilters,...payload},page:page,perPage:20}},constructEventList(eventList){let{viewDetails:viewDetails,mainField:mainField,details:details,blockTime:blockTime,blockDivision:blockDivision}=this,{startDateField:startDateField,endDateField:endDateField}=viewDetails||{},{name:startTimeKey}=startDateField||{},{name:endTimeKey}=endDateField||{};return(eventList||[]).map((eventObj=>{let{data:data,customization:customization}=eventObj||{},{eventColor:eventColor}=customization&&JSON.parse(customization)||{},{[startTimeKey]:workStart,[endTimeKey]:actualWorkEnd,[mainField]:name,id:id,moduleState:moduleState}=data||{},workEnd=workStart>actualWorkEnd?null:actualWorkEnd,{updatePermission:updatePermission,currentGrpId:currentGrpId,hasRescheduleEnabled:hasRescheduleEnabled,hasReAssignEnabled:hasReAssignEnabled,hasAssignEnabled:hasAssignEnabled,disabledPastEvents:disabledPastEvents,currentViewTime:currentViewTime}=this,{startTime:startTime,endTime:endTime,blockIndex:blockIndex}=details||{},width=1,hasAfterLimit=!1,hasBeforeLimit=workStart>0&&workStart<startTime,isLocked=!this.canEditRecord(data),draggable=updatePermission&&!hasBeforeLimit&&(hasRescheduleEnabled||hasReAssignEnabled||hasAssignEnabled&&-1===parseInt(currentGrpId))&&(!disabledPastEvents||disabledPastEvents&&workStart>currentViewTime),disableResize=!(updatePermission&&!isLocked&&hasRescheduleEnabled),startResizeDisabled=disableResize||disabledPastEvents&&workStart<currentViewTime,endResizeDisabled=disableResize||disabledPastEvents&&workEnd<currentViewTime;endTime<workEnd&&(width+=Math.ceil((workEnd-endTime)/blockTime),width>blockDivision-blockIndex&&(width=blockDivision-blockIndex,hasAfterLimit=!0));let cursor=isLocked?"url(data:image/svg+xml,%3Csvg%20xmlns=%22http://www.w3.org/2000/svg%22%20viewBox=%220%200%2011%2015%22%20width=%2214px%22%20height=%2214px%22%3E%0A%20%20%3Cpath%20d=%22M7.934%205.493H3.052v-1.22a2.444%202.444%200%200%201%202.44-2.442%202.444%202.444%200%200%201%202.442%202.441v1.221zm2.747%200h-.916v-1.22A4.275%204.275%200%200%200%205.493%200%204.277%204.277%200%200%200%201.22%204.272v1.221H.305A.305.305%200%200%200%200%205.798v7.63c0%20.673.548%201.22%201.221%201.22h8.544c.674%200%201.221-.547%201.221-1.22v-7.63a.305.305%200%200%200-.305-.305z%22/%3E%0A%3C/svg%3E) 10 10, auto":draggable?"grab":"not-allowed",{timezone:timezone,dateformat:dateformat,timeformat:timeformat}=this;return{workStart:workStart,workEnd:workEnd,actualWorkEnd:actualWorkEnd,isNeedConfirm:!actualWorkEnd||workStart>actualWorkEnd,id:id,name:name,eventColor:eventColor,moduleState:this.currentModuleState(moduleState),draggable:draggable,isLocked:isLocked,hasBeforeLimit:hasBeforeLimit,hasAfterLimit:hasAfterLimit,cursor:cursor,blockSpan:width,width:`calc(((100%/${blockDivision}) * ${width}) - 10px)`,startResizeDisabled:startResizeDisabled,endResizeDisabled:endResizeDisabled,startTimeDisplayFormat:workStart?formatDate(workStart,{timezone:timezone,dateformat:dateformat,timeformat:timeformat}):null,endTimeDisplayFormat:actualWorkEnd?formatDate(actualWorkEnd,{timezone:timezone,dateformat:dateformat,timeformat:timeformat}):null}}))},async loadUnscheduledList(){this.loading=!0;let{moduleName:moduleName}=this,params=this.getParams(),response=await loadUnscheduledDataList(params,moduleName);this.updateEventDetails(response),this.loading=!1},async loadScheduledList(){this.loading=!0;let{moduleName:moduleName,details:details}=this,{startTime:startTime,endTime:endTime,currentView:currentView}=details||{},payload={startTime:startTime,endTime:endTime,dateAggregateOperator:dateOperator[currentView]},params=this.getParams(payload),response=await loadSchedulerDataList(params,moduleName);this.updateEventDetails(response),this.loading=!1},updateEventDetails({error:error,data:data}){error?this.$message.error(error.message||"Error Occurred"):(this.haslist=20===data.length,this.totalCount=data.length||0,this.recordList=this.constructEventList(data||[]))},openSummary(eventObj){let{currentGrpId:currentGrpId,groupList:groupList}=this,selectedGrp=(groupList||[]).find((item=>item.value===currentGrpId))||[];selectedGrp=selectedGrp.map((item=>({id:item.value,name:item.label})));let grpObj=selectedGrp;this.$emit("openQuickSummary",eventObj,grpObj)},addDragEventListener(){document.addEventListener("dragstart",(()=>this.isDragging=!0),!1),document.addEventListener("dragend",(()=>this.isDragging=!1),!1),document.addEventListener("drop",(()=>this.isDragging=!1),!1)}}};
/* script */const __vue_script__$4=script$4;
/* template */var __vue_render__$4=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("SidebarLayout",{scopedSlots:_vm._u([{key:"title",fn:function(){return[_vm._v(_vm._s(_vm.details.title))]},proxy:!0},{key:"header-actions",fn:function(){return[_vm.isNullOrUndefined(_vm.details.grpId)?_c("el-select",{staticClass:"sidebar-list-grp-dropdown",attrs:{filterable:"",size:"small"},model:{value:_vm.currentGrpId,callback:function($$v){_vm.currentGrpId=$$v},expression:"currentGrpId"}},[_c("el-option",{attrs:{label:"All",value:""}}),_vm._v(" "),_vm._l(_vm.groupList,(function(grp,index){return _c("el-option",{key:grp.value+" "+index,attrs:{label:grp.label,value:parseInt(grp.value)}})}))],2):_c("div",[_vm._v(_vm._s(_vm.details.groupName))]),_vm._v(" "),!_vm.loading&&_vm.haslist?_c("GroupPagination",{staticStyle:{"margin-left":"auto"},attrs:{hasMoreList:_vm.haslist,currentPage:_vm.page,perPage:20,currentCount:_vm.totalCount},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}):_vm._e()]},proxy:!0},{key:"list",fn:function(){return[_vm.loading?_vm._t("spinner"):0===_vm.recordList.length?_c("div",{staticClass:"empty-list"},[_vm._v("\n      No Record Available\n    ")]):_vm._l(_vm.recordList,(function(record){return _c("PopupDialog",{key:"popup-"+record.id,attrs:{position:"left",borderColor:record.eventColor,isDragging:_vm.isDragging},scopedSlots:_vm._u([{key:"content",fn:function(){return[_c("div",{staticClass:"timeline-event-tooltip-content"},[record.isLocked||!record.draggable?_c("div",{staticClass:"warning-text"},[_c("i",{staticClass:"el-icon-info",staticStyle:{"margin-right":"5px"}}),_vm._v(" "),_c("div",[_vm._v("You don't have permission to edit this record")])]):_vm._e(),_vm._v(" "),_c("div",{staticClass:"event-name"},[_vm._v(_vm._s(record.name))]),_vm._v(" "),record.startTimeDisplayFormat?[_c("div",{staticClass:"event-time"},[_c("div",{staticClass:"event-time-format"},[_vm._v("\n                "+_vm._s(record.startTimeDisplayFormat)+"\n              ")]),_vm._v(" "),record.endTimeDisplayFormat?[_c("div",{staticClass:"time-divider"},[_vm._v("-")]),_vm._v(" "),_c("div",{staticClass:"event-time-format"},[_vm._v("\n                  "+_vm._s(record.endTimeDisplayFormat)+"\n                ")])]:_vm._e()],2)]:_vm._e()],2)]},proxy:!0}],null,!0)},[_vm._v(" "),_c("div",{staticClass:"sidebar-event",style:{"background-color":record.eventColor},attrs:{draggable:!record.isLocked&&record.draggable,"group-id":_vm.currentGrpId,"event-id":record.id},on:{click:function($event){return _vm.openSummary(record)}}},[_c("DragHandle",{staticClass:"drag-handle flex-shrink-0",style:{cursor:record.cursor}}),_vm._v(" "),_c("div",{staticClass:"event-main-field"},[_vm._v(_vm._s(record.name))])],1)])}))]},proxy:!0}],null,!0)})},__vue_staticRenderFns__$4=[];
/* style */
const __vue_inject_styles__$4=function(inject){inject&&inject("data-v-1382cae2_0",{source:".sidebar-list-grp-dropdown.el-select .el-input .el-input__inner{padding:5px;border:1px solid #e3eaed;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;border-radius:4px}.sidebar-list-grp-dropdown.el-select .el-input .el-input__inner:focus,.sidebar-list-grp-dropdown.el-select .el-input .el-input__inner:hover{border-color:#4d95ff!important}.sidebar-list-grp-dropdown.el-select .el-input .el-input__suffix{top:3px}.sidebar-list-grp-dropdown.el-select .el-input.is-focus .el-input__inner,.sidebar-list-grp-dropdown.el-select:hover .el-input__inner{border-color:#4d95ff}",map:void 0,media:void 0})},__vue_scope_id__$4=void 0,__vue_module_identifier__$4=void 0,__vue_is_functional_template__$4=!1,__vue_component__$4=normalizeComponent({render:__vue_render__$4,staticRenderFns:__vue_staticRenderFns__$4},__vue_inject_styles__$4,__vue_script__$4,__vue_scope_id__$4,__vue_is_functional_template__$4,__vue_module_identifier__$4,!1,createInjector,void 0,void 0);
/* scoped */
/**
 * Appends the elements of `values` to `array`.
 *
 * @private
 * @param {Array} array The array to modify.
 * @param {Array} values The values to append.
 * @returns {Array} Returns `array`.
 */
function arrayPush(array,values){var index=-1,length=values.length,offset=array.length;while(++index<length)array[offset+index]=values[index];return array}var _arrayPush=arrayPush,commonjsGlobal="undefined"!==typeof globalThis?globalThis:"undefined"!==typeof window?window:"undefined"!==typeof global?global:"undefined"!==typeof self?self:{};function createCommonjsModule(fn){var module={exports:{}};return fn(module,module.exports),module.exports
/** Detect free variable `global` from Node.js. */}var freeGlobal="object"==typeof commonjsGlobal&&commonjsGlobal&&commonjsGlobal.Object===Object&&commonjsGlobal,_freeGlobal=freeGlobal,freeSelf="object"==typeof self&&self&&self.Object===Object&&self,root=_freeGlobal||freeSelf||Function("return this")(),_root=root,Symbol=_root.Symbol,_Symbol=Symbol,objectProto$b=Object.prototype,hasOwnProperty$8=objectProto$b.hasOwnProperty,nativeObjectToString$1=objectProto$b.toString,symToStringTag$1=_Symbol?_Symbol.toStringTag:void 0;
/**
 * A specialized version of `baseGetTag` which ignores `Symbol.toStringTag` values.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the raw `toStringTag`.
 */
function getRawTag(value){var isOwn=hasOwnProperty$8.call(value,symToStringTag$1),tag=value[symToStringTag$1];try{value[symToStringTag$1]=void 0;var unmasked=!0}catch(e){}var result=nativeObjectToString$1.call(value);return unmasked&&(isOwn?value[symToStringTag$1]=tag:delete value[symToStringTag$1]),result}var _getRawTag=getRawTag,objectProto$a=Object.prototype,nativeObjectToString=objectProto$a.toString;
/** Used for built-in method references. */
/**
 * Converts `value` to a string using `Object.prototype.toString`.
 *
 * @private
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 */
function objectToString(value){return nativeObjectToString.call(value)}var _objectToString=objectToString,nullTag="[object Null]",undefinedTag="[object Undefined]",symToStringTag=_Symbol?_Symbol.toStringTag:void 0;
/** `Object#toString` result references. */
/**
 * The base implementation of `getTag` without fallbacks for buggy environments.
 *
 * @private
 * @param {*} value The value to query.
 * @returns {string} Returns the `toStringTag`.
 */
function baseGetTag(value){return null==value?void 0===value?undefinedTag:nullTag:symToStringTag&&symToStringTag in Object(value)?_getRawTag(value):_objectToString(value)}var _baseGetTag=baseGetTag;
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
 */function isObjectLike(value){return null!=value&&"object"==typeof value}var isObjectLike_1=isObjectLike,argsTag$2="[object Arguments]";
/** `Object#toString` result references. */
/**
 * The base implementation of `_.isArguments`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is an `arguments` object,
 */
function baseIsArguments(value){return isObjectLike_1(value)&&_baseGetTag(value)==argsTag$2}var _baseIsArguments=baseIsArguments,objectProto$9=Object.prototype,hasOwnProperty$7=objectProto$9.hasOwnProperty,propertyIsEnumerable$1=objectProto$9.propertyIsEnumerable,isArguments=_baseIsArguments(function(){return arguments}())?_baseIsArguments:function(value){return isObjectLike_1(value)&&hasOwnProperty$7.call(value,"callee")&&!propertyIsEnumerable$1.call(value,"callee")},isArguments_1=isArguments,isArray=Array.isArray,isArray_1=isArray,spreadableSymbol=_Symbol?_Symbol.isConcatSpreadable:void 0;
/** Used for built-in method references. */
/**
 * Checks if `value` is a flattenable `arguments` object or array.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is flattenable, else `false`.
 */
function isFlattenable(value){return isArray_1(value)||isArguments_1(value)||!!(spreadableSymbol&&value&&value[spreadableSymbol])}var _isFlattenable=isFlattenable;
/**
 * The base implementation of `_.flatten` with support for restricting flattening.
 *
 * @private
 * @param {Array} array The array to flatten.
 * @param {number} depth The maximum recursion depth.
 * @param {boolean} [predicate=isFlattenable] The function invoked per iteration.
 * @param {boolean} [isStrict] Restrict to values that pass `predicate` checks.
 * @param {Array} [result=[]] The initial result value.
 * @returns {Array} Returns the new flattened array.
 */function baseFlatten(array,depth,predicate,isStrict,result){var index=-1,length=array.length;predicate||(predicate=_isFlattenable),result||(result=[]);while(++index<length){var value=array[index];depth>0&&predicate(value)?depth>1?
// Recursively flatten arrays (susceptible to call stack limits).
baseFlatten(value,depth-1,predicate,isStrict,result):_arrayPush(result,value):isStrict||(result[result.length]=value)}return result}var _baseFlatten=baseFlatten;
/**
 * A specialized version of `_.map` for arrays without support for iteratee
 * shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 */function arrayMap(array,iteratee){var index=-1,length=null==array?0:array.length,result=Array(length);while(++index<length)result[index]=iteratee(array[index],index,array);return result}var _arrayMap=arrayMap,symbolTag$1="[object Symbol]";
/** `Object#toString` result references. */
/**
 * Checks if `value` is classified as a `Symbol` primitive or object.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a symbol, else `false`.
 * @example
 *
 * _.isSymbol(Symbol.iterator);
 * // => true
 *
 * _.isSymbol('abc');
 * // => false
 */
function isSymbol(value){return"symbol"==typeof value||isObjectLike_1(value)&&_baseGetTag(value)==symbolTag$1}var isSymbol_1=isSymbol,reIsDeepProp=/\.|\[(?:[^[\]]*|(["'])(?:(?!\1)[^\\]|\\.)*?\1)\]/,reIsPlainProp=/^\w*$/;
/** Used to match property names within property paths. */
/**
 * Checks if `value` is a property name and not a property path.
 *
 * @private
 * @param {*} value The value to check.
 * @param {Object} [object] The object to query keys on.
 * @returns {boolean} Returns `true` if `value` is a property name, else `false`.
 */
function isKey(value,object){if(isArray_1(value))return!1;var type=typeof value;return!("number"!=type&&"symbol"!=type&&"boolean"!=type&&null!=value&&!isSymbol_1(value))||(reIsPlainProp.test(value)||!reIsDeepProp.test(value)||null!=object&&value in Object(object))}var _isKey=isKey;
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
 */function isObject(value){var type=typeof value;return null!=value&&("object"==type||"function"==type)}var isObject_1=isObject,asyncTag="[object AsyncFunction]",funcTag$1="[object Function]",genTag="[object GeneratorFunction]",proxyTag="[object Proxy]";
/** `Object#toString` result references. */
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
 */
function isFunction(value){if(!isObject_1(value))return!1;
// The use of `Object#toString` avoids issues with the `typeof` operator
// in Safari 9 which returns 'object' for typed arrays and other constructors.
var tag=_baseGetTag(value);return tag==funcTag$1||tag==genTag||tag==asyncTag||tag==proxyTag}var isFunction_1=isFunction,coreJsData=_root["__core-js_shared__"],_coreJsData=coreJsData,maskSrcKey=function(){var uid=/[^.]+$/.exec(_coreJsData&&_coreJsData.keys&&_coreJsData.keys.IE_PROTO||"");return uid?"Symbol(src)_1."+uid:""}();
/** Used to detect overreaching core-js shims. */
/**
 * Checks if `func` has its source masked.
 *
 * @private
 * @param {Function} func The function to check.
 * @returns {boolean} Returns `true` if `func` is masked, else `false`.
 */
function isMasked(func){return!!maskSrcKey&&maskSrcKey in func}var _isMasked=isMasked,funcProto$1=Function.prototype,funcToString$1=funcProto$1.toString;
/** Used for built-in method references. */
/**
 * Converts `func` to its source code.
 *
 * @private
 * @param {Function} func The function to convert.
 * @returns {string} Returns the source code.
 */
function toSource(func){if(null!=func){try{return funcToString$1.call(func)}catch(e){}try{return func+""}catch(e){}}return""}var _toSource=toSource,reRegExpChar=/[\\^$.*+?()[\]{}|]/g,reIsHostCtor=/^\[object .+?Constructor\]$/,funcProto=Function.prototype,objectProto$8=Object.prototype,funcToString=funcProto.toString,hasOwnProperty$6=objectProto$8.hasOwnProperty,reIsNative=RegExp("^"+funcToString.call(hasOwnProperty$6).replace(reRegExpChar,"\\$&").replace(/hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g,"$1.*?")+"$");
/**
 * Used to match `RegExp`
 * [syntax characters](http://ecma-international.org/ecma-262/7.0/#sec-patterns).
 */
/**
 * The base implementation of `_.isNative` without bad shim checks.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a native function,
 *  else `false`.
 */
function baseIsNative(value){if(!isObject_1(value)||_isMasked(value))return!1;var pattern=isFunction_1(value)?reIsNative:reIsHostCtor;return pattern.test(_toSource(value))}var _baseIsNative=baseIsNative;
/**
 * Gets the value at `key` of `object`.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {string} key The key of the property to get.
 * @returns {*} Returns the property value.
 */function getValue(object,key){return null==object?void 0:object[key]}var _getValue=getValue;
/**
 * Gets the native function at `key` of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {string} key The key of the method to get.
 * @returns {*} Returns the function if it's native, else `undefined`.
 */function getNative(object,key){var value=_getValue(object,key);return _baseIsNative(value)?value:void 0}var _getNative=getNative,nativeCreate=_getNative(Object,"create"),_nativeCreate=nativeCreate;
/* Built-in method references that are verified to be native. */
/**
 * Removes all key-value entries from the hash.
 *
 * @private
 * @name clear
 * @memberOf Hash
 */
function hashClear(){this.__data__=_nativeCreate?_nativeCreate(null):{},this.size=0}var _hashClear=hashClear;
/**
 * Removes `key` and its value from the hash.
 *
 * @private
 * @name delete
 * @memberOf Hash
 * @param {Object} hash The hash to modify.
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function hashDelete(key){var result=this.has(key)&&delete this.__data__[key];return this.size-=result?1:0,result}var _hashDelete=hashDelete,HASH_UNDEFINED$2="__lodash_hash_undefined__",objectProto$7=Object.prototype,hasOwnProperty$5=objectProto$7.hasOwnProperty;
/** Used to stand-in for `undefined` hash values. */
/**
 * Gets the hash value for `key`.
 *
 * @private
 * @name get
 * @memberOf Hash
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */
function hashGet(key){var data=this.__data__;if(_nativeCreate){var result=data[key];return result===HASH_UNDEFINED$2?void 0:result}return hasOwnProperty$5.call(data,key)?data[key]:void 0}var _hashGet=hashGet,objectProto$6=Object.prototype,hasOwnProperty$4=objectProto$6.hasOwnProperty;
/** Used for built-in method references. */
/**
 * Checks if a hash value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Hash
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */
function hashHas(key){var data=this.__data__;return _nativeCreate?void 0!==data[key]:hasOwnProperty$4.call(data,key)}var _hashHas=hashHas,HASH_UNDEFINED$1="__lodash_hash_undefined__";
/** Used to stand-in for `undefined` hash values. */
/**
 * Sets the hash `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Hash
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the hash instance.
 */
function hashSet(key,value){var data=this.__data__;return this.size+=this.has(key)?0:1,data[key]=_nativeCreate&&void 0===value?HASH_UNDEFINED$1:value,this}var _hashSet=hashSet;
/**
 * Creates a hash object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */function Hash(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
// Add methods to `Hash`.
Hash.prototype.clear=_hashClear,Hash.prototype["delete"]=_hashDelete,Hash.prototype.get=_hashGet,Hash.prototype.has=_hashHas,Hash.prototype.set=_hashSet;var _Hash=Hash;
/**
 * Removes all key-value entries from the list cache.
 *
 * @private
 * @name clear
 * @memberOf ListCache
 */function listCacheClear(){this.__data__=[],this.size=0}var _listCacheClear=listCacheClear;
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
 */function eq(value,other){return value===other||value!==value&&other!==other}var eq_1=eq;
/**
 * Gets the index at which the `key` is found in `array` of key-value pairs.
 *
 * @private
 * @param {Array} array The array to inspect.
 * @param {*} key The key to search for.
 * @returns {number} Returns the index of the matched value, else `-1`.
 */function assocIndexOf(array,key){var length=array.length;while(length--)if(eq_1(array[length][0],key))return length;return-1}var _assocIndexOf=assocIndexOf,arrayProto=Array.prototype,splice=arrayProto.splice;
/** Used for built-in method references. */
/**
 * Removes `key` and its value from the list cache.
 *
 * @private
 * @name delete
 * @memberOf ListCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */
function listCacheDelete(key){var data=this.__data__,index=_assocIndexOf(data,key);if(index<0)return!1;var lastIndex=data.length-1;return index==lastIndex?data.pop():splice.call(data,index,1),--this.size,!0}var _listCacheDelete=listCacheDelete;
/**
 * Gets the list cache value for `key`.
 *
 * @private
 * @name get
 * @memberOf ListCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function listCacheGet(key){var data=this.__data__,index=_assocIndexOf(data,key);return index<0?void 0:data[index][1]}var _listCacheGet=listCacheGet;
/**
 * Checks if a list cache value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf ListCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function listCacheHas(key){return _assocIndexOf(this.__data__,key)>-1}var _listCacheHas=listCacheHas;
/**
 * Sets the list cache `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf ListCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the list cache instance.
 */function listCacheSet(key,value){var data=this.__data__,index=_assocIndexOf(data,key);return index<0?(++this.size,data.push([key,value])):data[index][1]=value,this}var _listCacheSet=listCacheSet;
/**
 * Creates an list cache object.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */function ListCache(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
// Add methods to `ListCache`.
ListCache.prototype.clear=_listCacheClear,ListCache.prototype["delete"]=_listCacheDelete,ListCache.prototype.get=_listCacheGet,ListCache.prototype.has=_listCacheHas,ListCache.prototype.set=_listCacheSet;var _ListCache=ListCache,Map=_getNative(_root,"Map"),_Map=Map;
/* Built-in method references that are verified to be native. */
/**
 * Removes all key-value entries from the map.
 *
 * @private
 * @name clear
 * @memberOf MapCache
 */
function mapCacheClear(){this.size=0,this.__data__={hash:new _Hash,map:new(_Map||_ListCache),string:new _Hash}}var _mapCacheClear=mapCacheClear;
/**
 * Checks if `value` is suitable for use as unique object key.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is suitable, else `false`.
 */function isKeyable(value){var type=typeof value;return"string"==type||"number"==type||"symbol"==type||"boolean"==type?"__proto__"!==value:null===value}var _isKeyable=isKeyable;
/**
 * Gets the data for `map`.
 *
 * @private
 * @param {Object} map The map to query.
 * @param {string} key The reference key.
 * @returns {*} Returns the map data.
 */function getMapData(map,key){var data=map.__data__;return _isKeyable(key)?data["string"==typeof key?"string":"hash"]:data.map}var _getMapData=getMapData;
/**
 * Removes `key` and its value from the map.
 *
 * @private
 * @name delete
 * @memberOf MapCache
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function mapCacheDelete(key){var result=_getMapData(this,key)["delete"](key);return this.size-=result?1:0,result}var _mapCacheDelete=mapCacheDelete;
/**
 * Gets the map value for `key`.
 *
 * @private
 * @name get
 * @memberOf MapCache
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function mapCacheGet(key){return _getMapData(this,key).get(key)}var _mapCacheGet=mapCacheGet;
/**
 * Checks if a map value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf MapCache
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function mapCacheHas(key){return _getMapData(this,key).has(key)}var _mapCacheHas=mapCacheHas;
/**
 * Sets the map `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf MapCache
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the map cache instance.
 */function mapCacheSet(key,value){var data=_getMapData(this,key),size=data.size;return data.set(key,value),this.size+=data.size==size?0:1,this}var _mapCacheSet=mapCacheSet;
/**
 * Creates a map cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */function MapCache(entries){var index=-1,length=null==entries?0:entries.length;this.clear();while(++index<length){var entry=entries[index];this.set(entry[0],entry[1])}}
// Add methods to `MapCache`.
MapCache.prototype.clear=_mapCacheClear,MapCache.prototype["delete"]=_mapCacheDelete,MapCache.prototype.get=_mapCacheGet,MapCache.prototype.has=_mapCacheHas,MapCache.prototype.set=_mapCacheSet;var _MapCache=MapCache,FUNC_ERROR_TEXT="Expected a function";
/** Error message constants. */
/**
 * Creates a function that memoizes the result of `func`. If `resolver` is
 * provided, it determines the cache key for storing the result based on the
 * arguments provided to the memoized function. By default, the first argument
 * provided to the memoized function is used as the map cache key. The `func`
 * is invoked with the `this` binding of the memoized function.
 *
 * **Note:** The cache is exposed as the `cache` property on the memoized
 * function. Its creation may be customized by replacing the `_.memoize.Cache`
 * constructor with one whose instances implement the
 * [`Map`](http://ecma-international.org/ecma-262/7.0/#sec-properties-of-the-map-prototype-object)
 * method interface of `clear`, `delete`, `get`, `has`, and `set`.
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Function
 * @param {Function} func The function to have its output memoized.
 * @param {Function} [resolver] The function to resolve the cache key.
 * @returns {Function} Returns the new memoized function.
 * @example
 *
 * var object = { 'a': 1, 'b': 2 };
 * var other = { 'c': 3, 'd': 4 };
 *
 * var values = _.memoize(_.values);
 * values(object);
 * // => [1, 2]
 *
 * values(other);
 * // => [3, 4]
 *
 * object.a = 2;
 * values(object);
 * // => [1, 2]
 *
 * // Modify the result cache.
 * values.cache.set(object, ['a', 'b']);
 * values(object);
 * // => ['a', 'b']
 *
 * // Replace `_.memoize.Cache`.
 * _.memoize.Cache = WeakMap;
 */
function memoize(func,resolver){if("function"!=typeof func||null!=resolver&&"function"!=typeof resolver)throw new TypeError(FUNC_ERROR_TEXT);var memoized=function(){var args=arguments,key=resolver?resolver.apply(this,args):args[0],cache=memoized.cache;if(cache.has(key))return cache.get(key);var result=func.apply(this,args);return memoized.cache=cache.set(key,result)||cache,result};return memoized.cache=new(memoize.Cache||_MapCache),memoized}
// Expose `MapCache`.
memoize.Cache=_MapCache;var memoize_1=memoize,MAX_MEMOIZE_SIZE=500;
/** Used as the maximum memoize cache size. */
/**
 * A specialized version of `_.memoize` which clears the memoized function's
 * cache when it exceeds `MAX_MEMOIZE_SIZE`.
 *
 * @private
 * @param {Function} func The function to have its output memoized.
 * @returns {Function} Returns the new memoized function.
 */
function memoizeCapped(func){var result=memoize_1(func,(function(key){return cache.size===MAX_MEMOIZE_SIZE&&cache.clear(),key})),cache=result.cache;return result}var _memoizeCapped=memoizeCapped,rePropName=/[^.[\]]+|\[(?:(-?\d+(?:\.\d+)?)|(["'])((?:(?!\2)[^\\]|\\.)*?)\2)\]|(?=(?:\.|\[\])(?:\.|\[\]|$))/g,reEscapeChar=/\\(\\)?/g,stringToPath=_memoizeCapped((function(string){var result=[];return 46/* . */===string.charCodeAt(0)&&result.push(""),string.replace(rePropName,(function(match,number,quote,subString){result.push(quote?subString.replace(reEscapeChar,"$1"):number||match)})),result})),_stringToPath=stringToPath,INFINITY$1=1/0,symbolProto$1=_Symbol?_Symbol.prototype:void 0,symbolToString=symbolProto$1?symbolProto$1.toString:void 0;
/** Used to match property names within property paths. */
/**
 * The base implementation of `_.toString` which doesn't convert nullish
 * values to empty strings.
 *
 * @private
 * @param {*} value The value to process.
 * @returns {string} Returns the string.
 */
function baseToString(value){
// Exit early for strings to avoid a performance hit in some environments.
if("string"==typeof value)return value;if(isArray_1(value))
// Recursively convert values (susceptible to call stack limits).
return _arrayMap(value,baseToString)+"";if(isSymbol_1(value))return symbolToString?symbolToString.call(value):"";var result=value+"";return"0"==result&&1/value==-INFINITY$1?"-0":result}var _baseToString=baseToString;
/**
 * Converts `value` to a string. An empty string is returned for `null`
 * and `undefined` values. The sign of `-0` is preserved.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Lang
 * @param {*} value The value to convert.
 * @returns {string} Returns the converted string.
 * @example
 *
 * _.toString(null);
 * // => ''
 *
 * _.toString(-0);
 * // => '-0'
 *
 * _.toString([1, 2, 3]);
 * // => '1,2,3'
 */function toString(value){return null==value?"":_baseToString(value)}var toString_1=toString;
/**
 * Casts `value` to a path array if it's not one.
 *
 * @private
 * @param {*} value The value to inspect.
 * @param {Object} [object] The object to query keys on.
 * @returns {Array} Returns the cast property path array.
 */function castPath(value,object){return isArray_1(value)?value:_isKey(value,object)?[value]:_stringToPath(toString_1(value))}var _castPath=castPath,INFINITY=1/0;
/** Used as references for various `Number` constants. */
/**
 * Converts `value` to a string key if it's not a string or symbol.
 *
 * @private
 * @param {*} value The value to inspect.
 * @returns {string|symbol} Returns the key.
 */
function toKey(value){if("string"==typeof value||isSymbol_1(value))return value;var result=value+"";return"0"==result&&1/value==-INFINITY?"-0":result}var _toKey=toKey;
/**
 * The base implementation of `_.get` without support for default values.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @returns {*} Returns the resolved value.
 */function baseGet(object,path){path=_castPath(path,object);var index=0,length=path.length;while(null!=object&&index<length)object=object[_toKey(path[index++])];return index&&index==length?object:void 0}var _baseGet=baseGet;
/**
 * Removes all key-value entries from the stack.
 *
 * @private
 * @name clear
 * @memberOf Stack
 */function stackClear(){this.__data__=new _ListCache,this.size=0}var _stackClear=stackClear;
/**
 * Removes `key` and its value from the stack.
 *
 * @private
 * @name delete
 * @memberOf Stack
 * @param {string} key The key of the value to remove.
 * @returns {boolean} Returns `true` if the entry was removed, else `false`.
 */function stackDelete(key){var data=this.__data__,result=data["delete"](key);return this.size=data.size,result}var _stackDelete=stackDelete;
/**
 * Gets the stack value for `key`.
 *
 * @private
 * @name get
 * @memberOf Stack
 * @param {string} key The key of the value to get.
 * @returns {*} Returns the entry value.
 */function stackGet(key){return this.__data__.get(key)}var _stackGet=stackGet;
/**
 * Checks if a stack value for `key` exists.
 *
 * @private
 * @name has
 * @memberOf Stack
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function stackHas(key){return this.__data__.has(key)}var _stackHas=stackHas,LARGE_ARRAY_SIZE=200;
/** Used as the size to enable large array optimizations. */
/**
 * Sets the stack `key` to `value`.
 *
 * @private
 * @name set
 * @memberOf Stack
 * @param {string} key The key of the value to set.
 * @param {*} value The value to set.
 * @returns {Object} Returns the stack cache instance.
 */
function stackSet(key,value){var data=this.__data__;if(data instanceof _ListCache){var pairs=data.__data__;if(!_Map||pairs.length<LARGE_ARRAY_SIZE-1)return pairs.push([key,value]),this.size=++data.size,this;data=this.__data__=new _MapCache(pairs)}return data.set(key,value),this.size=data.size,this}var _stackSet=stackSet;
/**
 * Creates a stack cache object to store key-value pairs.
 *
 * @private
 * @constructor
 * @param {Array} [entries] The key-value pairs to cache.
 */function Stack(entries){var data=this.__data__=new _ListCache(entries);this.size=data.size}
// Add methods to `Stack`.
Stack.prototype.clear=_stackClear,Stack.prototype["delete"]=_stackDelete,Stack.prototype.get=_stackGet,Stack.prototype.has=_stackHas,Stack.prototype.set=_stackSet;var _Stack=Stack,HASH_UNDEFINED="__lodash_hash_undefined__";
/** Used to stand-in for `undefined` hash values. */
/**
 * Adds `value` to the array cache.
 *
 * @private
 * @name add
 * @memberOf SetCache
 * @alias push
 * @param {*} value The value to cache.
 * @returns {Object} Returns the cache instance.
 */
function setCacheAdd(value){return this.__data__.set(value,HASH_UNDEFINED),this}var _setCacheAdd=setCacheAdd;
/**
 * Checks if `value` is in the array cache.
 *
 * @private
 * @name has
 * @memberOf SetCache
 * @param {*} value The value to search for.
 * @returns {number} Returns `true` if `value` is found, else `false`.
 */function setCacheHas(value){return this.__data__.has(value)}var _setCacheHas=setCacheHas;
/**
 *
 * Creates an array cache object to store unique values.
 *
 * @private
 * @constructor
 * @param {Array} [values] The values to cache.
 */function SetCache(values){var index=-1,length=null==values?0:values.length;this.__data__=new _MapCache;while(++index<length)this.add(values[index])}
// Add methods to `SetCache`.
SetCache.prototype.add=SetCache.prototype.push=_setCacheAdd,SetCache.prototype.has=_setCacheHas;var _SetCache=SetCache;
/**
 * A specialized version of `_.some` for arrays without support for iteratee
 * shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} predicate The function invoked per iteration.
 * @returns {boolean} Returns `true` if any element passes the predicate check,
 *  else `false`.
 */function arraySome(array,predicate){var index=-1,length=null==array?0:array.length;while(++index<length)if(predicate(array[index],index,array))return!0;return!1}var _arraySome=arraySome;
/**
 * Checks if a `cache` value for `key` exists.
 *
 * @private
 * @param {Object} cache The cache to query.
 * @param {string} key The key of the entry to check.
 * @returns {boolean} Returns `true` if an entry for `key` exists, else `false`.
 */function cacheHas(cache,key){return cache.has(key)}var _cacheHas=cacheHas,COMPARE_PARTIAL_FLAG$5=1,COMPARE_UNORDERED_FLAG$3=2;
/** Used to compose bitmasks for value comparisons. */
/**
 * A specialized version of `baseIsEqualDeep` for arrays with support for
 * partial deep comparisons.
 *
 * @private
 * @param {Array} array The array to compare.
 * @param {Array} other The other array to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `array` and `other` objects.
 * @returns {boolean} Returns `true` if the arrays are equivalent, else `false`.
 */
function equalArrays(array,other,bitmask,customizer,equalFunc,stack){var isPartial=bitmask&COMPARE_PARTIAL_FLAG$5,arrLength=array.length,othLength=other.length;if(arrLength!=othLength&&!(isPartial&&othLength>arrLength))return!1;
// Check that cyclic values are equal.
var arrStacked=stack.get(array),othStacked=stack.get(other);if(arrStacked&&othStacked)return arrStacked==other&&othStacked==array;var index=-1,result=!0,seen=bitmask&COMPARE_UNORDERED_FLAG$3?new _SetCache:void 0;stack.set(array,other),stack.set(other,array);
// Ignore non-index properties.
while(++index<arrLength){var arrValue=array[index],othValue=other[index];if(customizer)var compared=isPartial?customizer(othValue,arrValue,index,other,array,stack):customizer(arrValue,othValue,index,array,other,stack);if(void 0!==compared){if(compared)continue;result=!1;break}
// Recursively compare arrays (susceptible to call stack limits).
if(seen){if(!_arraySome(other,(function(othValue,othIndex){if(!_cacheHas(seen,othIndex)&&(arrValue===othValue||equalFunc(arrValue,othValue,bitmask,customizer,stack)))return seen.push(othIndex)}))){result=!1;break}}else if(arrValue!==othValue&&!equalFunc(arrValue,othValue,bitmask,customizer,stack)){result=!1;break}}return stack["delete"](array),stack["delete"](other),result}var _equalArrays=equalArrays,Uint8Array=_root.Uint8Array,_Uint8Array=Uint8Array;
/** Built-in value references. */
/**
 * Converts `map` to its key-value pairs.
 *
 * @private
 * @param {Object} map The map to convert.
 * @returns {Array} Returns the key-value pairs.
 */
function mapToArray(map){var index=-1,result=Array(map.size);return map.forEach((function(value,key){result[++index]=[key,value]})),result}var _mapToArray=mapToArray;
/**
 * Converts `set` to an array of its values.
 *
 * @private
 * @param {Object} set The set to convert.
 * @returns {Array} Returns the values.
 */function setToArray(set){var index=-1,result=Array(set.size);return set.forEach((function(value){result[++index]=value})),result}var _setToArray=setToArray,COMPARE_PARTIAL_FLAG$4=1,COMPARE_UNORDERED_FLAG$2=2,boolTag$1="[object Boolean]",dateTag$1="[object Date]",errorTag$1="[object Error]",mapTag$2="[object Map]",numberTag$1="[object Number]",regexpTag$1="[object RegExp]",setTag$2="[object Set]",stringTag$1="[object String]",symbolTag="[object Symbol]",arrayBufferTag$1="[object ArrayBuffer]",dataViewTag$2="[object DataView]",symbolProto=_Symbol?_Symbol.prototype:void 0,symbolValueOf=symbolProto?symbolProto.valueOf:void 0;
/** Used to compose bitmasks for value comparisons. */
/**
 * A specialized version of `baseIsEqualDeep` for comparing objects of
 * the same `toStringTag`.
 *
 * **Note:** This function only supports comparing values with tags of
 * `Boolean`, `Date`, `Error`, `Number`, `RegExp`, or `String`.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {string} tag The `toStringTag` of the objects to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function equalByTag(object,other,tag,bitmask,customizer,equalFunc,stack){switch(tag){case dataViewTag$2:if(object.byteLength!=other.byteLength||object.byteOffset!=other.byteOffset)return!1;object=object.buffer,other=other.buffer;case arrayBufferTag$1:return!(object.byteLength!=other.byteLength||!equalFunc(new _Uint8Array(object),new _Uint8Array(other)));case boolTag$1:case dateTag$1:case numberTag$1:
// Coerce booleans to `1` or `0` and dates to milliseconds.
// Invalid dates are coerced to `NaN`.
return eq_1(+object,+other);case errorTag$1:return object.name==other.name&&object.message==other.message;case regexpTag$1:case stringTag$1:
// Coerce regexes to strings and treat strings, primitives and objects,
// as equal. See http://www.ecma-international.org/ecma-262/7.0/#sec-regexp.prototype.tostring
// for more details.
return object==other+"";case mapTag$2:var convert=_mapToArray;case setTag$2:var isPartial=bitmask&COMPARE_PARTIAL_FLAG$4;if(convert||(convert=_setToArray),object.size!=other.size&&!isPartial)return!1;
// Assume cyclic values are equal.
var stacked=stack.get(object);if(stacked)return stacked==other;bitmask|=COMPARE_UNORDERED_FLAG$2,
// Recursively compare objects (susceptible to call stack limits).
stack.set(object,other);var result=_equalArrays(convert(object),convert(other),bitmask,customizer,equalFunc,stack);return stack["delete"](object),result;case symbolTag:if(symbolValueOf)return symbolValueOf.call(object)==symbolValueOf.call(other)}return!1}var _equalByTag=equalByTag;
/**
 * The base implementation of `getAllKeys` and `getAllKeysIn` which uses
 * `keysFunc` and `symbolsFunc` to get the enumerable property names and
 * symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Function} keysFunc The function to get the keys of `object`.
 * @param {Function} symbolsFunc The function to get the symbols of `object`.
 * @returns {Array} Returns the array of property names and symbols.
 */function baseGetAllKeys(object,keysFunc,symbolsFunc){var result=keysFunc(object);return isArray_1(object)?result:_arrayPush(result,symbolsFunc(object))}var _baseGetAllKeys=baseGetAllKeys;
/**
 * A specialized version of `_.filter` for arrays without support for
 * iteratee shorthands.
 *
 * @private
 * @param {Array} [array] The array to iterate over.
 * @param {Function} predicate The function invoked per iteration.
 * @returns {Array} Returns the new filtered array.
 */function arrayFilter(array,predicate){var index=-1,length=null==array?0:array.length,resIndex=0,result=[];while(++index<length){var value=array[index];predicate(value,index,array)&&(result[resIndex++]=value)}return result}var _arrayFilter=arrayFilter;
/**
 * This method returns a new empty array.
 *
 * @static
 * @memberOf _
 * @since 4.13.0
 * @category Util
 * @returns {Array} Returns the new empty array.
 * @example
 *
 * var arrays = _.times(2, _.stubArray);
 *
 * console.log(arrays);
 * // => [[], []]
 *
 * console.log(arrays[0] === arrays[1]);
 * // => false
 */function stubArray(){return[]}var stubArray_1=stubArray,objectProto$5=Object.prototype,propertyIsEnumerable=objectProto$5.propertyIsEnumerable,nativeGetSymbols=Object.getOwnPropertySymbols,getSymbols=nativeGetSymbols?function(object){return null==object?[]:(object=Object(object),_arrayFilter(nativeGetSymbols(object),(function(symbol){return propertyIsEnumerable.call(object,symbol)})))}:stubArray_1,_getSymbols=getSymbols;
/** Used for built-in method references. */
/**
 * The base implementation of `_.times` without support for iteratee shorthands
 * or max array length checks.
 *
 * @private
 * @param {number} n The number of times to invoke `iteratee`.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the array of results.
 */
function baseTimes(n,iteratee){var index=-1,result=Array(n);while(++index<n)result[index]=iteratee(index);return result}var _baseTimes=baseTimes;
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
 */function stubFalse(){return!1}var stubFalse_1=stubFalse,isBuffer_1=createCommonjsModule((function(module,exports){
/** Detect free variable `exports`. */
var freeExports=exports&&!exports.nodeType&&exports,freeModule=freeExports&&module&&!module.nodeType&&module,moduleExports=freeModule&&freeModule.exports===freeExports,Buffer=moduleExports?_root.Buffer:void 0,nativeIsBuffer=Buffer?Buffer.isBuffer:void 0,isBuffer=nativeIsBuffer||stubFalse_1;
/** Detect free variable `module`. */module.exports=isBuffer})),MAX_SAFE_INTEGER$1=9007199254740991,reIsUint=/^(?:0|[1-9]\d*)$/;
/**
 * Checks if `value` is a valid array-like index.
 *
 * @private
 * @param {*} value The value to check.
 * @param {number} [length=MAX_SAFE_INTEGER] The upper bounds of a valid index.
 * @returns {boolean} Returns `true` if `value` is a valid index, else `false`.
 */
function isIndex(value,length){var type=typeof value;return length=null==length?MAX_SAFE_INTEGER$1:length,!!length&&("number"==type||"symbol"!=type&&reIsUint.test(value))&&value>-1&&value%1==0&&value<length}var _isIndex=isIndex,MAX_SAFE_INTEGER=9007199254740991;
/** Used as references for various `Number` constants. */
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
 */
function isLength(value){return"number"==typeof value&&value>-1&&value%1==0&&value<=MAX_SAFE_INTEGER}var isLength_1=isLength,argsTag$1="[object Arguments]",arrayTag$1="[object Array]",boolTag="[object Boolean]",dateTag="[object Date]",errorTag="[object Error]",funcTag="[object Function]",mapTag$1="[object Map]",numberTag="[object Number]",objectTag$2="[object Object]",regexpTag="[object RegExp]",setTag$1="[object Set]",stringTag="[object String]",weakMapTag$1="[object WeakMap]",arrayBufferTag="[object ArrayBuffer]",dataViewTag$1="[object DataView]",float32Tag="[object Float32Array]",float64Tag="[object Float64Array]",int8Tag="[object Int8Array]",int16Tag="[object Int16Array]",int32Tag="[object Int32Array]",uint8Tag="[object Uint8Array]",uint8ClampedTag="[object Uint8ClampedArray]",uint16Tag="[object Uint16Array]",uint32Tag="[object Uint32Array]",typedArrayTags={};
/** `Object#toString` result references. */
/**
 * The base implementation of `_.isTypedArray` without Node.js optimizations.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a typed array, else `false`.
 */
function baseIsTypedArray(value){return isObjectLike_1(value)&&isLength_1(value.length)&&!!typedArrayTags[_baseGetTag(value)]}typedArrayTags[float32Tag]=typedArrayTags[float64Tag]=typedArrayTags[int8Tag]=typedArrayTags[int16Tag]=typedArrayTags[int32Tag]=typedArrayTags[uint8Tag]=typedArrayTags[uint8ClampedTag]=typedArrayTags[uint16Tag]=typedArrayTags[uint32Tag]=!0,typedArrayTags[argsTag$1]=typedArrayTags[arrayTag$1]=typedArrayTags[arrayBufferTag]=typedArrayTags[boolTag]=typedArrayTags[dataViewTag$1]=typedArrayTags[dateTag]=typedArrayTags[errorTag]=typedArrayTags[funcTag]=typedArrayTags[mapTag$1]=typedArrayTags[numberTag]=typedArrayTags[objectTag$2]=typedArrayTags[regexpTag]=typedArrayTags[setTag$1]=typedArrayTags[stringTag]=typedArrayTags[weakMapTag$1]=!1;var _baseIsTypedArray=baseIsTypedArray;
/**
 * The base implementation of `_.unary` without support for storing metadata.
 *
 * @private
 * @param {Function} func The function to cap arguments for.
 * @returns {Function} Returns the new capped function.
 */function baseUnary(func){return function(value){return func(value)}}var _baseUnary=baseUnary,_nodeUtil=createCommonjsModule((function(module,exports){
/** Detect free variable `exports`. */
var freeExports=exports&&!exports.nodeType&&exports,freeModule=freeExports&&module&&!module.nodeType&&module,moduleExports=freeModule&&freeModule.exports===freeExports,freeProcess=moduleExports&&_freeGlobal.process,nodeUtil=function(){try{
// Use `util.types` for Node.js 10+.
var types=freeModule&&freeModule.require&&freeModule.require("util").types;return types||freeProcess&&freeProcess.binding&&freeProcess.binding("util");
// Legacy `process.binding('util')` for Node.js < 10.
}catch(e){}}();
/** Detect free variable `module`. */module.exports=nodeUtil})),nodeIsTypedArray=_nodeUtil&&_nodeUtil.isTypedArray,isTypedArray=nodeIsTypedArray?_baseUnary(nodeIsTypedArray):_baseIsTypedArray,isTypedArray_1=isTypedArray,objectProto$4=Object.prototype,hasOwnProperty$3=objectProto$4.hasOwnProperty;
/**
 * Creates an array of the enumerable property names of the array-like `value`.
 *
 * @private
 * @param {*} value The value to query.
 * @param {boolean} inherited Specify returning inherited property names.
 * @returns {Array} Returns the array of property names.
 */
function arrayLikeKeys(value,inherited){var isArr=isArray_1(value),isArg=!isArr&&isArguments_1(value),isBuff=!isArr&&!isArg&&isBuffer_1(value),isType=!isArr&&!isArg&&!isBuff&&isTypedArray_1(value),skipIndexes=isArr||isArg||isBuff||isType,result=skipIndexes?_baseTimes(value.length,String):[],length=result.length;for(var key in value)!inherited&&!hasOwnProperty$3.call(value,key)||skipIndexes&&(
// Safari 9 has enumerable `arguments.length` in strict mode.
"length"==key||
// Node.js 0.10 has enumerable non-index properties on buffers.
isBuff&&("offset"==key||"parent"==key)||
// PhantomJS 2 has enumerable non-index properties on typed arrays.
isType&&("buffer"==key||"byteLength"==key||"byteOffset"==key)||
// Skip index properties.
_isIndex(key,length))||result.push(key);return result}var _arrayLikeKeys=arrayLikeKeys,objectProto$3=Object.prototype;
/** Used for built-in method references. */
/**
 * Checks if `value` is likely a prototype object.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` is a prototype, else `false`.
 */
function isPrototype(value){var Ctor=value&&value.constructor,proto="function"==typeof Ctor&&Ctor.prototype||objectProto$3;return value===proto}var _isPrototype=isPrototype;
/**
 * Creates a unary function that invokes `func` with its argument transformed.
 *
 * @private
 * @param {Function} func The function to wrap.
 * @param {Function} transform The argument transform.
 * @returns {Function} Returns the new function.
 */function overArg(func,transform){return function(arg){return func(transform(arg))}}var _overArg=overArg,nativeKeys=_overArg(Object.keys,Object),_nativeKeys=nativeKeys,objectProto$2=Object.prototype,hasOwnProperty$2=objectProto$2.hasOwnProperty;
/* Built-in method references for those with the same name as other `lodash` methods. */
/**
 * The base implementation of `_.keys` which doesn't treat sparse arrays as dense.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names.
 */
function baseKeys(object){if(!_isPrototype(object))return _nativeKeys(object);var result=[];for(var key in Object(object))hasOwnProperty$2.call(object,key)&&"constructor"!=key&&result.push(key);return result}var _baseKeys=baseKeys;
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
 */function isArrayLike(value){return null!=value&&isLength_1(value.length)&&!isFunction_1(value)}var isArrayLike_1=isArrayLike;
/**
 * Creates an array of the own enumerable property names of `object`.
 *
 * **Note:** Non-object values are coerced to objects. See the
 * [ES spec](http://ecma-international.org/ecma-262/7.0/#sec-object.keys)
 * for more details.
 *
 * @static
 * @since 0.1.0
 * @memberOf _
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
 * _.keys(new Foo);
 * // => ['a', 'b'] (iteration order is not guaranteed)
 *
 * _.keys('hi');
 * // => ['0', '1']
 */function keys(object){return isArrayLike_1(object)?_arrayLikeKeys(object):_baseKeys(object)}var keys_1=keys;
/**
 * Creates an array of own enumerable property names and symbols of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the array of property names and symbols.
 */function getAllKeys(object){return _baseGetAllKeys(object,keys_1,_getSymbols)}var _getAllKeys=getAllKeys,COMPARE_PARTIAL_FLAG$3=1,objectProto$1=Object.prototype,hasOwnProperty$1=objectProto$1.hasOwnProperty;
/** Used to compose bitmasks for value comparisons. */
/**
 * A specialized version of `baseIsEqualDeep` for objects with support for
 * partial deep comparisons.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} stack Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function equalObjects(object,other,bitmask,customizer,equalFunc,stack){var isPartial=bitmask&COMPARE_PARTIAL_FLAG$3,objProps=_getAllKeys(object),objLength=objProps.length,othProps=_getAllKeys(other),othLength=othProps.length;if(objLength!=othLength&&!isPartial)return!1;var index=objLength;while(index--){var key=objProps[index];if(!(isPartial?key in other:hasOwnProperty$1.call(other,key)))return!1}
// Check that cyclic values are equal.
var objStacked=stack.get(object),othStacked=stack.get(other);if(objStacked&&othStacked)return objStacked==other&&othStacked==object;var result=!0;stack.set(object,other),stack.set(other,object);var skipCtor=isPartial;while(++index<objLength){key=objProps[index];var objValue=object[key],othValue=other[key];if(customizer)var compared=isPartial?customizer(othValue,objValue,key,other,object,stack):customizer(objValue,othValue,key,object,other,stack);
// Recursively compare objects (susceptible to call stack limits).
if(!(void 0===compared?objValue===othValue||equalFunc(objValue,othValue,bitmask,customizer,stack):compared)){result=!1;break}skipCtor||(skipCtor="constructor"==key)}if(result&&!skipCtor){var objCtor=object.constructor,othCtor=other.constructor;
// Non `Object` object instances with different constructors are not equal.
objCtor==othCtor||!("constructor"in object)||!("constructor"in other)||"function"==typeof objCtor&&objCtor instanceof objCtor&&"function"==typeof othCtor&&othCtor instanceof othCtor||(result=!1)}return stack["delete"](object),stack["delete"](other),result}var _equalObjects=equalObjects,DataView=_getNative(_root,"DataView"),_DataView=DataView,Promise$1=_getNative(_root,"Promise"),_Promise=Promise$1,Set$1=_getNative(_root,"Set"),_Set=Set$1,WeakMap=_getNative(_root,"WeakMap"),_WeakMap=WeakMap,mapTag="[object Map]",objectTag$1="[object Object]",promiseTag="[object Promise]",setTag="[object Set]",weakMapTag="[object WeakMap]",dataViewTag="[object DataView]",dataViewCtorString=_toSource(_DataView),mapCtorString=_toSource(_Map),promiseCtorString=_toSource(_Promise),setCtorString=_toSource(_Set),weakMapCtorString=_toSource(_WeakMap),getTag=_baseGetTag;
/* Built-in method references that are verified to be native. */
// Fallback for data views, maps, sets, and weak maps in IE 11 and promises in Node.js < 6.
(_DataView&&getTag(new _DataView(new ArrayBuffer(1)))!=dataViewTag||_Map&&getTag(new _Map)!=mapTag||_Promise&&getTag(_Promise.resolve())!=promiseTag||_Set&&getTag(new _Set)!=setTag||_WeakMap&&getTag(new _WeakMap)!=weakMapTag)&&(getTag=function(value){var result=_baseGetTag(value),Ctor=result==objectTag$1?value.constructor:void 0,ctorString=Ctor?_toSource(Ctor):"";if(ctorString)switch(ctorString){case dataViewCtorString:return dataViewTag;case mapCtorString:return mapTag;case promiseCtorString:return promiseTag;case setCtorString:return setTag;case weakMapCtorString:return weakMapTag}return result});var _getTag=getTag,COMPARE_PARTIAL_FLAG$2=1,argsTag="[object Arguments]",arrayTag="[object Array]",objectTag="[object Object]",objectProto=Object.prototype,hasOwnProperty=objectProto.hasOwnProperty;
/** Used to compose bitmasks for value comparisons. */
/**
 * A specialized version of `baseIsEqual` for arrays and objects which performs
 * deep comparisons and tracks traversed objects enabling objects with circular
 * references to be compared.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {number} bitmask The bitmask flags. See `baseIsEqual` for more details.
 * @param {Function} customizer The function to customize comparisons.
 * @param {Function} equalFunc The function to determine equivalents of values.
 * @param {Object} [stack] Tracks traversed `object` and `other` objects.
 * @returns {boolean} Returns `true` if the objects are equivalent, else `false`.
 */
function baseIsEqualDeep(object,other,bitmask,customizer,equalFunc,stack){var objIsArr=isArray_1(object),othIsArr=isArray_1(other),objTag=objIsArr?arrayTag:_getTag(object),othTag=othIsArr?arrayTag:_getTag(other);objTag=objTag==argsTag?objectTag:objTag,othTag=othTag==argsTag?objectTag:othTag;var objIsObj=objTag==objectTag,othIsObj=othTag==objectTag,isSameTag=objTag==othTag;if(isSameTag&&isBuffer_1(object)){if(!isBuffer_1(other))return!1;objIsArr=!0,objIsObj=!1}if(isSameTag&&!objIsObj)return stack||(stack=new _Stack),objIsArr||isTypedArray_1(object)?_equalArrays(object,other,bitmask,customizer,equalFunc,stack):_equalByTag(object,other,objTag,bitmask,customizer,equalFunc,stack);if(!(bitmask&COMPARE_PARTIAL_FLAG$2)){var objIsWrapped=objIsObj&&hasOwnProperty.call(object,"__wrapped__"),othIsWrapped=othIsObj&&hasOwnProperty.call(other,"__wrapped__");if(objIsWrapped||othIsWrapped){var objUnwrapped=objIsWrapped?object.value():object,othUnwrapped=othIsWrapped?other.value():other;return stack||(stack=new _Stack),equalFunc(objUnwrapped,othUnwrapped,bitmask,customizer,stack)}}return!!isSameTag&&(stack||(stack=new _Stack),_equalObjects(object,other,bitmask,customizer,equalFunc,stack))}var _baseIsEqualDeep=baseIsEqualDeep;
/**
 * The base implementation of `_.isEqual` which supports partial comparisons
 * and tracks traversed objects.
 *
 * @private
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @param {boolean} bitmask The bitmask flags.
 *  1 - Unordered comparison
 *  2 - Partial comparison
 * @param {Function} [customizer] The function to customize comparisons.
 * @param {Object} [stack] Tracks traversed `value` and `other` objects.
 * @returns {boolean} Returns `true` if the values are equivalent, else `false`.
 */function baseIsEqual(value,other,bitmask,customizer,stack){return value===other||(null==value||null==other||!isObjectLike_1(value)&&!isObjectLike_1(other)?value!==value&&other!==other:_baseIsEqualDeep(value,other,bitmask,customizer,baseIsEqual,stack))}var _baseIsEqual=baseIsEqual,COMPARE_PARTIAL_FLAG$1=1,COMPARE_UNORDERED_FLAG$1=2;
/** Used to compose bitmasks for value comparisons. */
/**
 * The base implementation of `_.isMatch` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The object to inspect.
 * @param {Object} source The object of property values to match.
 * @param {Array} matchData The property names, values, and compare flags to match.
 * @param {Function} [customizer] The function to customize comparisons.
 * @returns {boolean} Returns `true` if `object` is a match, else `false`.
 */
function baseIsMatch(object,source,matchData,customizer){var index=matchData.length,length=index,noCustomizer=!customizer;if(null==object)return!length;object=Object(object);while(index--){var data=matchData[index];if(noCustomizer&&data[2]?data[1]!==object[data[0]]:!(data[0]in object))return!1}while(++index<length){data=matchData[index];var key=data[0],objValue=object[key],srcValue=data[1];if(noCustomizer&&data[2]){if(void 0===objValue&&!(key in object))return!1}else{var stack=new _Stack;if(customizer)var result=customizer(objValue,srcValue,key,object,source,stack);if(!(void 0===result?_baseIsEqual(srcValue,objValue,COMPARE_PARTIAL_FLAG$1|COMPARE_UNORDERED_FLAG$1,customizer,stack):result))return!1}}return!0}var _baseIsMatch=baseIsMatch;
/**
 * Checks if `value` is suitable for strict equality comparisons, i.e. `===`.
 *
 * @private
 * @param {*} value The value to check.
 * @returns {boolean} Returns `true` if `value` if suitable for strict
 *  equality comparisons, else `false`.
 */function isStrictComparable(value){return value===value&&!isObject_1(value)}var _isStrictComparable=isStrictComparable;
/**
 * Gets the property names, values, and compare flags of `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @returns {Array} Returns the match data of `object`.
 */function getMatchData(object){var result=keys_1(object),length=result.length;while(length--){var key=result[length],value=object[key];result[length]=[key,value,_isStrictComparable(value)]}return result}var _getMatchData=getMatchData;
/**
 * A specialized version of `matchesProperty` for source values suitable
 * for strict equality comparisons, i.e. `===`.
 *
 * @private
 * @param {string} key The key of the property to get.
 * @param {*} srcValue The value to match.
 * @returns {Function} Returns the new spec function.
 */function matchesStrictComparable(key,srcValue){return function(object){return null!=object&&(object[key]===srcValue&&(void 0!==srcValue||key in Object(object)))}}var _matchesStrictComparable=matchesStrictComparable;
/**
 * The base implementation of `_.matches` which doesn't clone `source`.
 *
 * @private
 * @param {Object} source The object of property values to match.
 * @returns {Function} Returns the new spec function.
 */function baseMatches(source){var matchData=_getMatchData(source);return 1==matchData.length&&matchData[0][2]?_matchesStrictComparable(matchData[0][0],matchData[0][1]):function(object){return object===source||_baseIsMatch(object,source,matchData)}}var _baseMatches=baseMatches;
/**
 * Gets the value at `path` of `object`. If the resolved value is
 * `undefined`, the `defaultValue` is returned in its place.
 *
 * @static
 * @memberOf _
 * @since 3.7.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path of the property to get.
 * @param {*} [defaultValue] The value returned for `undefined` resolved values.
 * @returns {*} Returns the resolved value.
 * @example
 *
 * var object = { 'a': [{ 'b': { 'c': 3 } }] };
 *
 * _.get(object, 'a[0].b.c');
 * // => 3
 *
 * _.get(object, ['a', '0', 'b', 'c']);
 * // => 3
 *
 * _.get(object, 'a.b.c', 'default');
 * // => 'default'
 */function get(object,path,defaultValue){var result=null==object?void 0:_baseGet(object,path);return void 0===result?defaultValue:result}var get_1=get;
/**
 * The base implementation of `_.hasIn` without support for deep paths.
 *
 * @private
 * @param {Object} [object] The object to query.
 * @param {Array|string} key The key to check.
 * @returns {boolean} Returns `true` if `key` exists, else `false`.
 */function baseHasIn(object,key){return null!=object&&key in Object(object)}var _baseHasIn=baseHasIn;
/**
 * Checks if `path` exists on `object`.
 *
 * @private
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @param {Function} hasFunc The function to check properties.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 */function hasPath(object,path,hasFunc){path=_castPath(path,object);var index=-1,length=path.length,result=!1;while(++index<length){var key=_toKey(path[index]);if(!(result=null!=object&&hasFunc(object,key)))break;object=object[key]}return result||++index!=length?result:(length=null==object?0:object.length,!!length&&isLength_1(length)&&_isIndex(key,length)&&(isArray_1(object)||isArguments_1(object)))}var _hasPath=hasPath;
/**
 * Checks if `path` is a direct or inherited property of `object`.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Object
 * @param {Object} object The object to query.
 * @param {Array|string} path The path to check.
 * @returns {boolean} Returns `true` if `path` exists, else `false`.
 * @example
 *
 * var object = _.create({ 'a': _.create({ 'b': 2 }) });
 *
 * _.hasIn(object, 'a');
 * // => true
 *
 * _.hasIn(object, 'a.b');
 * // => true
 *
 * _.hasIn(object, ['a', 'b']);
 * // => true
 *
 * _.hasIn(object, 'b');
 * // => false
 */function hasIn(object,path){return null!=object&&_hasPath(object,path,_baseHasIn)}var hasIn_1=hasIn,COMPARE_PARTIAL_FLAG=1,COMPARE_UNORDERED_FLAG=2;
/** Used to compose bitmasks for value comparisons. */
/**
 * The base implementation of `_.matchesProperty` which doesn't clone `srcValue`.
 *
 * @private
 * @param {string} path The path of the property to get.
 * @param {*} srcValue The value to match.
 * @returns {Function} Returns the new spec function.
 */
function baseMatchesProperty(path,srcValue){return _isKey(path)&&_isStrictComparable(srcValue)?_matchesStrictComparable(_toKey(path),srcValue):function(object){var objValue=get_1(object,path);return void 0===objValue&&objValue===srcValue?hasIn_1(object,path):_baseIsEqual(srcValue,objValue,COMPARE_PARTIAL_FLAG|COMPARE_UNORDERED_FLAG)}}var _baseMatchesProperty=baseMatchesProperty;
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
 */function identity(value){return value}var identity_1=identity;
/**
 * The base implementation of `_.property` without support for deep paths.
 *
 * @private
 * @param {string} key The key of the property to get.
 * @returns {Function} Returns the new accessor function.
 */function baseProperty(key){return function(object){return null==object?void 0:object[key]}}var _baseProperty=baseProperty;
/**
 * A specialized version of `baseProperty` which supports deep paths.
 *
 * @private
 * @param {Array|string} path The path of the property to get.
 * @returns {Function} Returns the new accessor function.
 */function basePropertyDeep(path){return function(object){return _baseGet(object,path)}}var _basePropertyDeep=basePropertyDeep;
/**
 * Creates a function that returns the value at `path` of a given object.
 *
 * @static
 * @memberOf _
 * @since 2.4.0
 * @category Util
 * @param {Array|string} path The path of the property to get.
 * @returns {Function} Returns the new accessor function.
 * @example
 *
 * var objects = [
 *   { 'a': { 'b': 2 } },
 *   { 'a': { 'b': 1 } }
 * ];
 *
 * _.map(objects, _.property('a.b'));
 * // => [2, 1]
 *
 * _.map(_.sortBy(objects, _.property(['a', 'b'])), 'a.b');
 * // => [1, 2]
 */function property(path){return _isKey(path)?_baseProperty(_toKey(path)):_basePropertyDeep(path)}var property_1=property;
/**
 * The base implementation of `_.iteratee`.
 *
 * @private
 * @param {*} [value=_.identity] The value to convert to an iteratee.
 * @returns {Function} Returns the iteratee.
 */function baseIteratee(value){
// Don't store the `typeof` result in a variable to avoid a JIT bug in Safari 9.
// See https://bugs.webkit.org/show_bug.cgi?id=156034 for more details.
return"function"==typeof value?value:null==value?identity_1:"object"==typeof value?isArray_1(value)?_baseMatchesProperty(value[0],value[1]):_baseMatches(value):property_1(value)}var _baseIteratee=baseIteratee;
/**
 * Creates a base function for methods like `_.forIn` and `_.forOwn`.
 *
 * @private
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {Function} Returns the new base function.
 */function createBaseFor(fromRight){return function(object,iteratee,keysFunc){var index=-1,iterable=Object(object),props=keysFunc(object),length=props.length;while(length--){var key=props[fromRight?length:++index];if(!1===iteratee(iterable[key],key,iterable))break}return object}}var _createBaseFor=createBaseFor,baseFor=_createBaseFor(),_baseFor=baseFor;
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
 */
/**
 * The base implementation of `_.forOwn` without support for iteratee shorthands.
 *
 * @private
 * @param {Object} object The object to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Object} Returns `object`.
 */
function baseForOwn(object,iteratee){return object&&_baseFor(object,iteratee,keys_1)}var _baseForOwn=baseForOwn;
/**
 * Creates a `baseEach` or `baseEachRight` function.
 *
 * @private
 * @param {Function} eachFunc The function to iterate over a collection.
 * @param {boolean} [fromRight] Specify iterating from right to left.
 * @returns {Function} Returns the new base function.
 */function createBaseEach(eachFunc,fromRight){return function(collection,iteratee){if(null==collection)return collection;if(!isArrayLike_1(collection))return eachFunc(collection,iteratee);var length=collection.length,index=fromRight?length:-1,iterable=Object(collection);while(fromRight?index--:++index<length)if(!1===iteratee(iterable[index],index,iterable))break;return collection}}var _createBaseEach=createBaseEach,baseEach=_createBaseEach(_baseForOwn),_baseEach=baseEach;
/**
 * The base implementation of `_.forEach` without support for iteratee shorthands.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array|Object} Returns `collection`.
 */
/**
 * The base implementation of `_.map` without support for iteratee shorthands.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function} iteratee The function invoked per iteration.
 * @returns {Array} Returns the new mapped array.
 */
function baseMap(collection,iteratee){var index=-1,result=isArrayLike_1(collection)?Array(collection.length):[];return _baseEach(collection,(function(value,key,collection){result[++index]=iteratee(value,key,collection)})),result}var _baseMap=baseMap;
/**
 * The base implementation of `_.sortBy` which uses `comparer` to define the
 * sort order of `array` and replaces criteria objects with their corresponding
 * values.
 *
 * @private
 * @param {Array} array The array to sort.
 * @param {Function} comparer The function to define sort order.
 * @returns {Array} Returns `array`.
 */function baseSortBy(array,comparer){var length=array.length;array.sort(comparer);while(length--)array[length]=array[length].value;return array}var _baseSortBy=baseSortBy;
/**
 * Compares values to sort them in ascending order.
 *
 * @private
 * @param {*} value The value to compare.
 * @param {*} other The other value to compare.
 * @returns {number} Returns the sort order indicator for `value`.
 */function compareAscending(value,other){if(value!==other){var valIsDefined=void 0!==value,valIsNull=null===value,valIsReflexive=value===value,valIsSymbol=isSymbol_1(value),othIsDefined=void 0!==other,othIsNull=null===other,othIsReflexive=other===other,othIsSymbol=isSymbol_1(other);if(!othIsNull&&!othIsSymbol&&!valIsSymbol&&value>other||valIsSymbol&&othIsDefined&&othIsReflexive&&!othIsNull&&!othIsSymbol||valIsNull&&othIsDefined&&othIsReflexive||!valIsDefined&&othIsReflexive||!valIsReflexive)return 1;if(!valIsNull&&!valIsSymbol&&!othIsSymbol&&value<other||othIsSymbol&&valIsDefined&&valIsReflexive&&!valIsNull&&!valIsSymbol||othIsNull&&valIsDefined&&valIsReflexive||!othIsDefined&&valIsReflexive||!othIsReflexive)return-1}return 0}var _compareAscending=compareAscending;
/**
 * Used by `_.orderBy` to compare multiple properties of a value to another
 * and stable sort them.
 *
 * If `orders` is unspecified, all values are sorted in ascending order. Otherwise,
 * specify an order of "desc" for descending or "asc" for ascending sort order
 * of corresponding values.
 *
 * @private
 * @param {Object} object The object to compare.
 * @param {Object} other The other object to compare.
 * @param {boolean[]|string[]} orders The order to sort by for each property.
 * @returns {number} Returns the sort order indicator for `object`.
 */function compareMultiple(object,other,orders){var index=-1,objCriteria=object.criteria,othCriteria=other.criteria,length=objCriteria.length,ordersLength=orders.length;while(++index<length){var result=_compareAscending(objCriteria[index],othCriteria[index]);if(result){if(index>=ordersLength)return result;var order=orders[index];return result*("desc"==order?-1:1)}}
// Fixes an `Array#sort` bug in the JS engine embedded in Adobe applications
// that causes it, under certain circumstances, to provide the same value for
// `object` and `other`. See https://github.com/jashkenas/underscore/pull/1247
// for more details.

// This also ensures a stable sort in V8 and other engines.
// See https://bugs.chromium.org/p/v8/issues/detail?id=90 for more details.
return object.index-other.index}var _compareMultiple=compareMultiple;
/**
 * The base implementation of `_.orderBy` without param guards.
 *
 * @private
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Function[]|Object[]|string[]} iteratees The iteratees to sort by.
 * @param {string[]} orders The sort orders of `iteratees`.
 * @returns {Array} Returns the new sorted array.
 */function baseOrderBy(collection,iteratees,orders){iteratees=iteratees.length?_arrayMap(iteratees,(function(iteratee){return isArray_1(iteratee)?function(value){return _baseGet(value,1===iteratee.length?iteratee[0]:iteratee)}:iteratee})):[identity_1];var index=-1;iteratees=_arrayMap(iteratees,_baseUnary(_baseIteratee));var result=_baseMap(collection,(function(value,key,collection){var criteria=_arrayMap(iteratees,(function(iteratee){return iteratee(value)}));return{criteria:criteria,index:++index,value:value}}));return _baseSortBy(result,(function(object,other){return _compareMultiple(object,other,orders)}))}var _baseOrderBy=baseOrderBy;
/**
 * A faster alternative to `Function#apply`, this function invokes `func`
 * with the `this` binding of `thisArg` and the arguments of `args`.
 *
 * @private
 * @param {Function} func The function to invoke.
 * @param {*} thisArg The `this` binding of `func`.
 * @param {Array} args The arguments to invoke `func` with.
 * @returns {*} Returns the result of `func`.
 */function apply(func,thisArg,args){switch(args.length){case 0:return func.call(thisArg);case 1:return func.call(thisArg,args[0]);case 2:return func.call(thisArg,args[0],args[1]);case 3:return func.call(thisArg,args[0],args[1],args[2])}return func.apply(thisArg,args)}var _apply=apply,nativeMax=Math.max;
/* Built-in method references for those with the same name as other `lodash` methods. */
/**
 * A specialized version of `baseRest` which transforms the rest array.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @param {Function} transform The rest array transform.
 * @returns {Function} Returns the new function.
 */
function overRest(func,start,transform){return start=nativeMax(void 0===start?func.length-1:start,0),function(){var args=arguments,index=-1,length=nativeMax(args.length-start,0),array=Array(length);while(++index<length)array[index]=args[start+index];index=-1;var otherArgs=Array(start+1);while(++index<start)otherArgs[index]=args[index];return otherArgs[start]=transform(array),_apply(func,this,otherArgs)}}var _overRest=overRest;
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
 */function constant(value){return function(){return value}}var constant_1=constant,defineProperty=function(){try{var func=_getNative(Object,"defineProperty");return func({},"",{}),func}catch(e){}}(),_defineProperty=defineProperty,baseSetToString=_defineProperty?function(func,string){return _defineProperty(func,"toString",{configurable:!0,enumerable:!1,value:constant_1(string),writable:!0})}:identity_1,_baseSetToString=baseSetToString,HOT_COUNT=800,HOT_SPAN=16,nativeNow=Date.now;
/**
 * Creates a function that'll short out and invoke `identity` instead
 * of `func` when it's called `HOT_COUNT` or more times in `HOT_SPAN`
 * milliseconds.
 *
 * @private
 * @param {Function} func The function to restrict.
 * @returns {Function} Returns the new shortable function.
 */
function shortOut(func){var count=0,lastCalled=0;return function(){var stamp=nativeNow(),remaining=HOT_SPAN-(stamp-lastCalled);if(lastCalled=stamp,remaining>0){if(++count>=HOT_COUNT)return arguments[0]}else count=0;return func.apply(void 0,arguments)}}var _shortOut=shortOut,setToString=_shortOut(_baseSetToString),_setToString=setToString;
/**
 * Sets the `toString` method of `func` to return `string`.
 *
 * @private
 * @param {Function} func The function to modify.
 * @param {Function} string The `toString` result.
 * @returns {Function} Returns `func`.
 */
/**
 * The base implementation of `_.rest` which doesn't validate or coerce arguments.
 *
 * @private
 * @param {Function} func The function to apply a rest parameter to.
 * @param {number} [start=func.length-1] The start position of the rest parameter.
 * @returns {Function} Returns the new function.
 */
function baseRest(func,start){return _setToString(_overRest(func,start,identity_1),func+"")}var _baseRest=baseRest;
/**
 * Checks if the given arguments are from an iteratee call.
 *
 * @private
 * @param {*} value The potential iteratee value argument.
 * @param {*} index The potential iteratee index or key argument.
 * @param {*} object The potential iteratee object argument.
 * @returns {boolean} Returns `true` if the arguments are from an iteratee call,
 *  else `false`.
 */function isIterateeCall(value,index,object){if(!isObject_1(object))return!1;var type=typeof index;return!!("number"==type?isArrayLike_1(object)&&_isIndex(index,object.length):"string"==type&&index in object)&&eq_1(object[index],value)}var _isIterateeCall=isIterateeCall,sortBy=_baseRest((function(collection,iteratees){if(null==collection)return[];var length=iteratees.length;return length>1&&_isIterateeCall(collection,iteratees[0],iteratees[1])?iteratees=[]:length>2&&_isIterateeCall(iteratees[0],iteratees[1],iteratees[2])&&(iteratees=[iteratees[0]]),_baseOrderBy(collection,_baseFlatten(iteratees,1),[])})),sortBy_1=sortBy;
/**
 * Creates an array of elements, sorted in ascending order by the results of
 * running each element in a collection thru each iteratee. This method
 * performs a stable sort, that is, it preserves the original sort order of
 * equal elements. The iteratees are invoked with one argument: (value).
 *
 * @static
 * @memberOf _
 * @since 0.1.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {...(Function|Function[])} [iteratees=[_.identity]]
 *  The iteratees to sort by.
 * @returns {Array} Returns the new sorted array.
 * @example
 *
 * var users = [
 *   { 'user': 'fred',   'age': 48 },
 *   { 'user': 'barney', 'age': 36 },
 *   { 'user': 'fred',   'age': 30 },
 *   { 'user': 'barney', 'age': 34 }
 * ];
 *
 * _.sortBy(users, [function(o) { return o.user; }]);
 * // => objects for [['barney', 36], ['barney', 34], ['fred', 48], ['fred', 30]]
 *
 * _.sortBy(users, ['user', 'age']);
 * // => objects for [['barney', 34], ['barney', 36], ['fred', 30], ['fred', 48]]
 */
/**
 * This method is like `_.sortBy` except that it allows specifying the sort
 * orders of the iteratees to sort by. If `orders` is unspecified, all values
 * are sorted in ascending order. Otherwise, specify an order of "desc" for
 * descending or "asc" for ascending sort order of corresponding values.
 *
 * @static
 * @memberOf _
 * @since 4.0.0
 * @category Collection
 * @param {Array|Object} collection The collection to iterate over.
 * @param {Array[]|Function[]|Object[]|string[]} [iteratees=[_.identity]]
 *  The iteratees to sort by.
 * @param {string[]} [orders] The sort orders of `iteratees`.
 * @param- {Object} [guard] Enables use as an iteratee for methods like `_.reduce`.
 * @returns {Array} Returns the new sorted array.
 * @example
 *
 * var users = [
 *   { 'user': 'fred',   'age': 48 },
 *   { 'user': 'barney', 'age': 34 },
 *   { 'user': 'fred',   'age': 40 },
 *   { 'user': 'barney', 'age': 36 }
 * ];
 *
 * // Sort by `user` in ascending order and by `age` in descending order.
 * _.orderBy(users, ['user', 'age'], ['asc', 'desc']);
 * // => objects for [['barney', 36], ['barney', 34], ['fred', 48], ['fred', 40]]
 */
function orderBy(collection,iteratees,orders,guard){return null==collection?[]:(isArray_1(iteratees)||(iteratees=null==iteratees?[]:[iteratees]),orders=guard?void 0:orders,isArray_1(orders)||(orders=null==orders?[]:[orders]),_baseOrderBy(collection,iteratees,orders))}var orderBy_1=orderBy,OpenNewTab={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 13 13"}},[_c("path",{attrs:{d:"M4.73 1H1.996A.995.995 0 001 1.995v9.32c0 .55.445.995.995.995h9.075a.995.995 0 00.994-.995V8.197h0m0-7.197l-6.17 6.2M7.339 1h4.725v4.591",fill:"none",stroke:"#489EDC","stroke-linecap":"round","stroke-width":".949"}})])}},Tick={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 510 510"}},[_c("path",{attrs:{d:"M150.45 206.55l-35.7 35.7L229.5 357l255-255-35.7-35.7-219.3 219.3-79.05-79.05zM459 255c0 112.2-91.8 204-204 204S51 367.2 51 255 142.8 51 255 51c20.4 0 38.25 2.55 56.1 7.65l40.801-40.8C321.3 7.65 288.15 0 255 0 114.75 0 0 114.75 0 255s114.75 255 255 255 255-114.75 255-255h-51z"}})])}},Close={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{viewBox:"0 0 24 24",xmlns:"http://www.w3.org/2000/svg"}},[_c("path",{attrs:{d:"M16.34 9.322a1 1 0 10-1.364-1.463l-2.926 2.728L9.322 7.66A1 1 0 007.86 9.024l2.728 2.926-2.927 2.728a1 1 0 101.364 1.462l2.926-2.727 2.728 2.926a1 1 0 101.462-1.363l-2.727-2.926 2.926-2.728z"}}),_c("path",{attrs:{"fill-rule":"evenodd",d:"M1 12C1 5.925 5.925 1 12 1s11 4.925 11 11-4.925 11-11 11S1 18.075 1 12zm11 9a9 9 0 110-18 9 9 0 010 18z"}})])}},script$3={props:["moduleName","eventObj","groupObj","metaInfo","viewDetails","timezone","dateformat","timeformat","getSiteName","groupingListArr","saving"],components:{OpenNewTab:OpenNewTab,Tick:Tick,Close:Close},data(){return{eventData:{},fieldObj:{},moduleState:null,loading:!1,details:{grpId:null,workStart:null,workEnd:null},enableEdit:!1}},created(){this.init()},computed:{id(){let{id:id}=this.eventObj||{};return id},canShowAdditionalDetails(){let{loading:loading,fieldObj:fieldObj}=this;return loading||!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(fieldObj)},popupFields(){let{configJson:configJson}=this.viewDetails||{};configJson=configJson&&JSON.parse(configJson)||{};let{"popup-fields":popupFields}=configJson||{};return popupFields=(popupFields||[]).map((fldId=>parseInt(fldId))),popupFields},canEdit(){let{canEdit:canEdit,isLocked:isLocked}=this.eventObj||{};return canEdit&&!isLocked}},watch:{saving(newVal){if(!newVal){let{groupingListArr:groupingListArr,details:details,timezone:timezone,dateformat:dateformat,timeformat:timeformat}=this,{workStart:workStart,workEnd:workEnd,grpId:grpId}=details||{},formatDetails={timezone:timezone,dateformat:dateformat,timeformat:timeformat},groupObj=!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(grpId)&&(groupingListArr||[]).find((item=>{item.value})),grouplabel=(groupObj||{}).label||"---";this.$set(this.eventData.groupByObj,"fieldValue",grouplabel),this.$set(this.eventData.workStartObj,"fieldValue",workStart&&-1!==workStart?formatDate(workStart,formatDetails):"---"),this.$set(this.eventData.workEndObj,"fieldValue",workEnd&&-1!==workEnd?formatDate(workEnd,formatDetails):"---"),this.enableEdit=!1}}},methods:{init(){let{timezone:timezone,dateformat:dateformat,timeformat:timeformat}=this,{workStart:workStart,actualWorkEnd:actualWorkEnd,name:name,id:id,moduleState:moduleState}=this.eventObj||{},{name:groupByDisplayName,id:groupById}=this.groupObj||{},{startDateField:startDateField,endDateField:endDateField,groupByField:groupByField}=this.viewDetails||{},{displayName:startDateFieldName}=startDateField||{},{displayName:endDateFieldName}=endDateField||{},{displayName:groupByFieldName}=groupByField||{},formatDetails={timezone:timezone,dateformat:dateformat,timeformat:timeformat};this.eventData={name:name,id:id,moduleState:moduleState,workStartObj:{fieldName:startDateFieldName,fieldValue:workStart&&-1!==workStart?formatDate(workStart,formatDetails):"---"},workEndObj:{fieldName:endDateFieldName,fieldValue:actualWorkEnd&&-1!==actualWorkEnd?formatDate(actualWorkEnd,formatDetails):"---"},groupByObj:{fieldName:groupByFieldName,fieldValue:(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(groupById)?"---":groupByDisplayName}},this.details={grpId:(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(groupById)?"-1":groupById,workStart:workStart&&-1!==workStart?workStart:"",workEnd:actualWorkEnd&&-1!==actualWorkEnd?actualWorkEnd:""},(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(this.popupFields)||this.loadRecord()},async loadRecord(){this.loading=!0;let{moduleName:moduleName,id:id}=this,{[moduleName]:data,error:error}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.fetchRecord(moduleName,{id:id});error?this.$message.error(error.message||"Error Occurred"):(this.moduleState=(null===data||void 0===data?void 0:data.moduleState)||null,this.fieldObj=this.constructData(data||{})),this.loading=!1},constructData(record){let{timezone:timezone,dateformat:dateformat,timeformat:timeformat,metaInfo:metaInfo}=this,formatDetails={timezone:timezone,dateformat:dateformat,timeformat:timeformat},{fields:fields}=metaInfo||{},popupFieldList=(fields||[]).filter((fld=>this.popupFields.includes(fld.id)));return(popupFieldList||[]).reduce(((dataObj,field)=>{let value,{displayName:displayName,dataTypeEnum:dataTypeEnum}=field,{_name:dataType}=dataTypeEnum||{};return value="siteId"===field.name?this.getSiteName(field,record):(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(DATA_TYPE_VALUE_HASH[dataType])?DATA_TYPE_VALUE_HASH["OTHERS"](field,record):DATA_TYPE_VALUE_HASH[dataType](field,record,formatDetails),dataObj[displayName]=(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(value)?"---":value,dataObj}),{})},openEdit(){this.canEdit&&!this.saving&&(this.enableEdit=!0)},saveData(){if(this.saving)return;let{id:id}=this.eventObj||{},{params:params,grpList:grpList,error:error}=this.getParams();if(error)this.$message.warning(error);else if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(params)){let payload={id:id,data:{id:id,...params}};this.$parent.saving=!0,this.$parent.$emit("updateGrpEvents",grpList,payload)}},getParams(){let{startDateField:startDateField,endDateField:endDateField}=this.viewDetails||{},{name:startTimeKey}=startDateField||{},{name:endTimeKey}=endDateField||{},{groupByField:groupByField}=this.viewDetails||{},{name:groupByKey}=groupByField||{},{workStart:workStart,workEnd:workEnd,grpId:grpId}=this.details||{},{id:groupById}=this.groupObj||{},oldGrpId=parseInt(groupById),currentGrpId=parseInt(grpId),params={},grpList=[],isWorkStartEmpty=!workStart||-1===workStart,isWorkEndEmpty=!workEnd||-1===workEnd,isGrpEmpty=!currentGrpId||-1===currentGrpId;return isWorkStartEmpty||isWorkEndEmpty||isGrpEmpty?{error:`Cannot update because ${isWorkStartEmpty?"Start time,":""} ${isWorkEndEmpty?"End time,":""} ${isGrpEmpty?"Grouping field":""} is/are empty`}:(params[startTimeKey]=workStart,params[endTimeKey]=workEnd,grpList.push(oldGrpId),oldGrpId!==currentGrpId&&(params[groupByKey]=-1!==currentGrpId?{id:currentGrpId}:null,grpList.push(currentGrpId)),{params:params,grpList:grpList})},resetEdit(){let{workStart:workStart,actualWorkEnd:actualWorkEnd}=this.eventObj||{},{id:groupById}=this.groupObj||{};this.details={grpId:-1!==parseInt(groupById)?groupById:null,workStart:workStart&&-1!==workStart?workStart:"",workEnd:actualWorkEnd&&-1!==actualWorkEnd?actualWorkEnd:""},this.enableEdit=!1},openSummary(){this.$parent.$emit("openSummary",{id:this.id})},close(){this.$emit("onClose")}}};
/* script */
const __vue_script__$3=script$3;
/* template */var __vue_render__$3=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("el-dialog",{attrs:{visible:!0,fullscreen:!0,"custom-class":"fc-dialog-form setup-dialog-right f-page-dialog-right width30 timeline-quick-summary-container","before-close":_vm.close}},[_c("div",{staticClass:"details-section"},[_c("div",{staticClass:"header-section"},[_c("div",{staticClass:"d-flex flex-col"},[_c("div",{staticClass:"record-id bold"},[_vm._v("#"+_vm._s(_vm.eventData.id))]),_vm._v(" "),_c("div",{staticClass:"record-name-container"},[_c("div",{staticClass:"record-name"},[_vm._v("\n            "+_vm._s(_vm.eventData.name)+"\n          ")]),_vm._v(" "),_c("div",{staticClass:"open-summary",on:{click:_vm.openSummary}},[_c("OpenNewTab",{staticClass:"svg-icon-12"})],1),_vm._v(" "),_c("div",{staticClass:"fc-badge text-uppercase inline vertical-middle"},[_vm._v("\n            "+_vm._s(_vm.eventData.moduleState)+"\n          ")])])]),_vm._v(" "),_c("div",{on:{click:_vm.close}},[_c("i",{staticClass:"el-icon-close close-icon"})])]),_vm._v(" "),_c("div",{staticClass:"grouping-field-details border-bottom1px"},[_c("div",{staticClass:"grouping-field-header flex-align-center"},[_c("div",[_vm._v(_vm._s("Details"))]),_vm._v(" "),_vm.enableEdit?_c("div",{staticClass:"flex-middle"},[_c("div",{staticClass:"tick-svg",on:{click:_vm.saveData}},[_c("Tick")],1),_vm._v(" "),_c("div",{staticClass:"close-svg",on:{click:_vm.resetEdit}},[_c("Close")],1)]):_vm.canEdit&&!_vm.saving?_c("i",{staticClass:"el-icon-edit edit-icon",on:{click:_vm.openEdit}}):_vm._e()]),_vm._v(" "),_c("div",{staticClass:"grouping-field-container"},[_c("div",{staticClass:"grouping-field"},[_c("div",{staticClass:"grouping-field-name"},[_vm._v("\n            "+_vm._s(_vm.eventData["workStartObj"].fieldName)+"\n          ")]),_vm._v(" "),_c("div",{staticClass:"flex-align-center"},[_c("el-date-picker",{class:["date-picker",!_vm.enableEdit&&"hide-border"],attrs:{format:"yyyy-MM-dd HH:mm",type:"datetime","value-format":"timestamp",placeholder:"Select start date",disabled:!_vm.canEdit||_vm.saving},on:{focus:_vm.openEdit},model:{value:_vm.details.workStart,callback:function($$v){_vm.$set(_vm.details,"workStart",$$v)},expression:"details.workStart"}})],1)]),_vm._v(" "),_c("div",{staticClass:"grouping-field"},[_c("div",{staticClass:"grouping-field-name"},[_vm._v("\n            "+_vm._s(_vm.eventData["workEndObj"].fieldName)+"\n          ")]),_vm._v(" "),_c("div",{staticClass:"flex-align-center"},[_c("el-date-picker",{class:["date-picker",!_vm.enableEdit&&"hide-border"],attrs:{format:"yyyy-MM-dd HH:mm",type:"datetime","value-format":"timestamp",placeholder:"Select end date",disabled:!_vm.canEdit||_vm.saving},on:{focus:_vm.openEdit},model:{value:_vm.details.workEnd,callback:function($$v){_vm.$set(_vm.details,"workEnd",$$v)},expression:"details.workEnd"}})],1)]),_vm._v(" "),_c("div",{staticClass:"grouping-field"},[_c("div",{staticClass:"grouping-field-name"},[_vm._v("\n            "+_vm._s(_vm.eventData["groupByObj"].fieldName)+"\n          ")]),_vm._v(" "),_c("div",{staticClass:"flex-align-center"},[_c("el-select",{staticClass:"fc-input-full-border-select2 width-100",class:[!_vm.enableEdit&&"hide-border"],attrs:{placeholder:"Select the group",disabled:!_vm.canEdit||_vm.saving,filterable:""},on:{focus:_vm.openEdit},model:{value:_vm.details.grpId,callback:function($$v){_vm.$set(_vm.details,"grpId",$$v)},expression:"details.grpId"}},_vm._l(_vm.groupingListArr,(function(grp,index){return _c("el-option",{key:grp.value+" "+index,attrs:{label:grp.label,value:grp.value}})})),1)],1)])])]),_vm._v(" "),_vm.canShowAdditionalDetails?_c("div",{staticClass:"grouping-field-details border-bottom1px"},[_c("div",{staticClass:"grouping-field-header"},[_vm._v(_vm._s("Additional Details"))]),_vm._v(" "),_vm.loading?_c("div",{staticClass:"grouping-field-container"},_vm._l([1,2,3],(function(index){return _c("div",{key:index,staticClass:"grouping-field"},[_c("div",{staticClass:"lines loading-shimmer"}),_vm._v(" "),_c("div",{staticClass:"lines loading-shimmer"})])})),0):_c("div",{staticClass:"grouping-field-container"},_vm._l(_vm.fieldObj,(function(fieldValue,fieldName,fieldIdx){return _c("div",{key:fieldName+"-"+fieldIdx,staticClass:"grouping-field"},[_c("div",{staticClass:"grouping-field-name"},[_vm._v(_vm._s(fieldName))]),_vm._v(" "),_c("div",{staticClass:"grouping-field-value"},[_vm._v(_vm._s(fieldValue))])])})),0)]):_vm._e()])])},__vue_staticRenderFns__$3=[];
/* style */
const __vue_inject_styles__$3=function(inject){inject&&inject("data-v-b2d3ff52_0",{source:".timeline-quick-summary-container .el-dialog__header{display:none}.timeline-quick-summary-container .details-section{overflow:scroll}.timeline-quick-summary-container .details-section .header-section{display:flex;flex-direction:row;justify-content:space-between;align-items:baseline;flex-grow:1;border-bottom:1px solid #e2e8ee;padding:25px 30px}.timeline-quick-summary-container .details-section .header-section .record-id{font-size:12px;color:#39b2c2}.timeline-quick-summary-container .details-section .header-section .record-name-container{display:flex;align-items:center}.timeline-quick-summary-container .details-section .header-section .record-name-container .record-name{font-size:16px;color:#324056;font-weight:500;line-height:normal;letter-spacing:.5px;word-break:break-word;display:flex}.timeline-quick-summary-container .details-section .header-section .record-name-container .fc-badge{flex-shrink:0;color:#fff;background-color:#23b096;padding:5px 18px}.timeline-quick-summary-container .details-section .header-section .record-name-container .open-summary{padding:2px 5px;margin:3px 10px;height:18px;cursor:pointer}.timeline-quick-summary-container .details-section .header-section .record-name-container .open-summary .svg-icon-12{height:12px;width:12px}.timeline-quick-summary-container .details-section .header-section .record-name-container .open-summary:hover{background:#f3f4f7;border-radius:4px}.timeline-quick-summary-container .details-section .header-section .close-icon{font-size:17px;font-weight:700;cursor:pointer}.timeline-quick-summary-container .details-section .grouping-field-details{padding:10px 30px}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-header{font-size:11px;font-weight:700;color:#ee518f;text-transform:uppercase;padding:15px 0;letter-spacing:1px;display:flex;align-items:center;justify-content:space-between}.timeline-quick-summary-container .details-section .grouping-field-details .tick-svg{margin-right:5px;cursor:pointer;height:25px;width:25px;display:flex}.timeline-quick-summary-container .details-section .grouping-field-details .tick-svg svg{fill:#3db4c2;height:15px;width:15px;margin:auto}.timeline-quick-summary-container .details-section .grouping-field-details .tick-svg:hover{background:#f3f4f7;border-radius:4px}.timeline-quick-summary-container .details-section .grouping-field-details .close-svg{cursor:pointer;height:25px;width:25px;display:flex}.timeline-quick-summary-container .details-section .grouping-field-details .close-svg svg{fill:#de7272;height:17px;width:17px;margin:auto}.timeline-quick-summary-container .details-section .grouping-field-details .close-svg:hover{background:#f3f4f7;border-radius:4px}.timeline-quick-summary-container .details-section .grouping-field-details .edit-icon{color:#319aa8;font-size:14px;cursor:pointer;padding:5px}.timeline-quick-summary-container .details-section .grouping-field-details .edit-icon:hover{background:#f3f4f7;border-radius:4px}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container{display:flex;flex-wrap:wrap}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .grouping-field{width:50%;flex-grow:1;padding:15px 10px 15px 0;word-break:break-word}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .grouping-field .lines{height:18px;width:90%;margin:0 5px;border-radius:5px}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .grouping-field .date-picker .el-input__inner{border-radius:3px;background-color:#fff;border:solid 1px #d0d9e2;height:40px}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .grouping-field-name{color:#8ca1ad;font-size:11px;font-weight:500;line-height:22px;letter-spacing:.92px;text-transform:uppercase}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .grouping-field-value{color:#324056;letter-spacing:.5px;font-size:13px;font-weight:400}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .flex-align-center{display:flex;align-items:center}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .width-100{width:100%}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .date-picker.hide-border .el-input__inner,.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .hide-border .el-input__inner{background:#f3f4f7;border-color:transparent!important}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .date-picker.hide-border .el-input__inner:hover,.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .hide-border .el-input__inner:hover{border-color:#39b2c2!important}.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .date-picker.hide-border .el-input__suffix,.timeline-quick-summary-container .details-section .grouping-field-details .grouping-field-container .hide-border .el-input__suffix{visibility:hidden}",map:void 0,media:void 0})},__vue_scope_id__$3=void 0,__vue_module_identifier__$3=void 0,__vue_is_functional_template__$3=!1,__vue_component__$3=normalizeComponent({render:__vue_render__$3,staticRenderFns:__vue_staticRenderFns__$3},__vue_inject_styles__$3,__vue_script__$3,__vue_scope_id__$3,__vue_is_functional_template__$3,__vue_module_identifier__$3,!1,createInjector,void 0,void 0);
/* scoped */var ColorLegendIcon={render:function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("svg",{attrs:{xmlns:"http://www.w3.org/2000/svg",viewBox:"0 0 192 192"}},[_c("g",{attrs:{"data-name":"trim area"}},[_c("circle",{attrs:{cx:"62",cy:"62",r:"30",fill:"#8d72d3"}}),_c("circle",{attrs:{cx:"130",cy:"62",r:"30",fill:"#eb8359"}}),_c("circle",{attrs:{cx:"62",cy:"130",r:"30",fill:"#549773"}}),_c("circle",{attrs:{cx:"130",cy:"130",r:"30",fill:"#d494be"}}),_c("path",{attrs:{fill:"none",d:"M0 0h192v192H0z"}})])])}},script$2={props:["viewDetails","metaInfo","moduleName"],components:{SidebarLayout:__vue_component__$7},data(){return{loading:!1,colorCustomization:{},fieldName:null,isEmpty:_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb}},created(){this.deserialize()},computed:{isListEmpty(){let{values:values}=this.colorCustomization||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(values)},selectFieldList(){let{BOOLEAN:BOOLEAN,LOOKUP:LOOKUP,ENUM:ENUM,SYSTEM_ENUM:SYSTEM_ENUM}=dataTypes,{PICK_LIST:PICK_LIST}=lookupType,{metaInfo:metaInfo}=this,{fields:fields}=metaInfo||{};return(fields||[]).filter((fld=>{let{dataType:dataType,lookupModule:lookupModule}=fld,{type:type}=lookupModule||{};return[BOOLEAN,ENUM,SYSTEM_ENUM].includes(dataType)||LOOKUP===dataType&&type===PICK_LIST}))}},methods:{deserialize(){let{recordCustomization:recordCustomization}=this.viewDetails||{};if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(recordCustomization)){let{values:values,customizationType:customizationType,defaultCustomization:defaultCustomization,customizationFieldId:customizationFieldId}=recordCustomization||{};values=values.map((val=>{let valueObj={},{customization:customization,namedCriteriaId:namedCriteriaId,fieldValue:fieldValue}=val||{};return customization=JSON.parse(customization)||{},valueObj=1===customizationType?{customization:customization,fieldValue:fieldValue}:{customization:customization,namedCriteriaId:namedCriteriaId},valueObj})),defaultCustomization=defaultCustomization&&JSON.parse(defaultCustomization)||{eventColor:"#4d95ff"},this.colorCustomization={customizationType:customizationType,values:values,defaultCustomization:defaultCustomization},1===customizationType?(this.colorCustomization={...this.colorCustomization,customizationFieldId:customizationFieldId},this.loadOptions()):(this.fieldName="Criteria",this.loadConditionManager())}},async loadOptions(){this.loading=!0;let{BOOLEAN:BOOLEAN,LOOKUP:LOOKUP,ENUM:ENUM,SYSTEM_ENUM:SYSTEM_ENUM}=dataTypes,{selectFieldList:selectFieldList,colorCustomization:colorCustomization}=this,{customizationFieldId:fieldId,values:values}=colorCustomization||{},selectedField=selectFieldList.find((f=>f.id===fieldId))||{},{dataType:dataType,displayName:displayName}=selectedField||{};if(BOOLEAN===dataType){let{falseVal:falseVal,trueVal:trueVal}=selectedField||{};values=values.map((option=>{let{fieldValue:fieldValue}=option||{},label="true"===fieldValue?trueVal||"True":falseVal||"False";return{...option,label:label}}))}else if([ENUM,SYSTEM_ENUM].includes(dataType)){let{enumMap:enumMap}=selectedField||{};values=values.map((option=>{let{fieldValue:fieldValue}=option||{};return{...option,label:enumMap[fieldValue]}}))}else if(LOOKUP===dataType){let{PICK_LIST:PICK_LIST}=lookupType,{lookupModule:lookupModule}=selectedField||{},{name:name,type:type}=lookupModule||{};if(type===PICK_LIST){let{data:data,error:error}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.get("v2/module/data/list",{moduleName:name});if(!error){let optionMap=(data.moduleDatas||[]).reduce(((optionMapObj,option)=>{let{id:id,displayName:displayName,name:name}=option;return optionMapObj[id]=displayName||name,optionMapObj}),{});values=values.map((option=>{let{fieldValue:fieldValue}=option||{};return{...option,label:optionMap[fieldValue]}}))}}}this.$set(this.colorCustomization,"values",values),this.fieldName=displayName,this.loading=!1},async loadConditionManager(){this.loading=!0;let{moduleName:moduleName}=this,{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.post("v2/namedCriteria/list",{moduleName:moduleName});if(error)this.$message.error(error.message||"Error Occurred");else{let{values:values}=this.colorCustomization||{},namedCriteriaIdVsName=(data.namedCriteriaList||[]).reduce(((namedCriteriaOptions,criteriaObj)=>{let{id:id,name:name}=criteriaObj||{};return namedCriteriaOptions[id]=name,namedCriteriaOptions}),{});this.colorCustomization.values=values.map((valueObj=>{let{namedCriteriaId:namedCriteriaId}=valueObj;return valueObj.label=namedCriteriaIdVsName[namedCriteriaId],valueObj}))}this.loading=!1}}};
/* script */
const __vue_script__$2=script$2;
/* template */var __vue_render__$2=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("SidebarLayout",{scopedSlots:_vm._u([{key:"title",fn:function(){return[_vm._v(_vm._s("Colors"))]},proxy:!0},{key:"header-actions",fn:function(){return[_vm._v(_vm._s(_vm.fieldName))]},proxy:!0},{key:"list",fn:function(){return[_vm.loading?_vm._t("spinner"):_vm.isListEmpty?_c("div",{staticClass:"empty-list"},[_vm._v("\n      No Color Customization Available\n    ")]):[_vm._l(_vm.colorCustomization.values,(function(valueObj,index){return _c("div",{key:"color-field-option-"+index,staticClass:"color-item",attrs:{span:12}},[_c("div",{staticClass:"color-block",style:{"background-color":valueObj.customization.eventColor}}),_vm._v(" "),_c("div",{staticClass:"option-text"},[_vm._v("\n          "+_vm._s(valueObj.label)+"\n        ")])])})),_vm._v(" "),_c("div",{staticClass:"color-item"},[_c("div",{staticClass:"color-block",style:{"background-color":_vm.colorCustomization.defaultCustomization.eventColor}}),_vm._v(" "),_c("div",{staticClass:"option-text"},[_vm._v("Default")])])]]},proxy:!0}],null,!0)})},__vue_staticRenderFns__$2=[];
/* style */
const __vue_inject_styles__$2=void 0,__vue_scope_id__$2=void 0,__vue_module_identifier__$2=void 0,__vue_is_functional_template__$2=!1,__vue_component__$2=normalizeComponent({render:__vue_render__$2,staticRenderFns:__vue_staticRenderFns__$2},__vue_inject_styles__$2,__vue_script__$2,__vue_scope_id__$2,__vue_is_functional_template__$2,__vue_module_identifier__$2,!1,void 0,void 0,void 0);
/* scoped */var script$1={props:["viewDetails","groupingListArr","schedulerData","headerGridColumns","currentCalendarView","pinnedGroupObj","currentTimeLimit","todayIndex","moduleName","viewname","getTimeStamp","maxResultPerCell","createPermission","updatePermission","metaInfo","isStatusLocked","getApprovalStatus","timezone","dateformat","timeformat","getTicketStatus","dialogPrompt"],components:{PushPin:PushPin,LeftArrow:LeftArrow,RightArrow:RightArrow,SidebarList:__vue_component__$4,PopupDialog:__vue_component__$5,EventSummary:__vue_component__$3,ColorLegendIcon:ColorLegendIcon,ColorLegend:__vue_component__$2},mixins:[__vue_component__$8],data(){return{pinnedGrpObj:{},eventList:{},eventsPositionMap:{},loading:!1,canShowSideBarList:!1,sideBarDetails:{},eventCount:{},saving:null,showSummary:!1,currentEventObj:null,currentEventGrpObj:null,showColorLegend:!1}},created(){let{hasAssignEnabled:hasAssignEnabled,hasRescheduleEnabled:hasRescheduleEnabled,hasReAssignEnabled:hasReAssignEnabled}=this;(hasAssignEnabled||hasRescheduleEnabled||hasReAssignEnabled)&&this.addDragEventListener(),this.loading=!0,this.init(),this.$nextTick((()=>{this.loading=!1,this.$nextTick((()=>{this.todayIndex>-1&&this.scrollCurrentBlock(`header-${this.todayIndex}`)}))}))},computed:{groupName(){let{groupByField:groupByField}=this.viewDetails||{},{displayName:displayName}=groupByField||{};return displayName},totalEventList(){let{eventList:eventList,pinnedGrpObj:pinnedGrpObj}=this,clonedEventList={...eventList},pinnedRow={};if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(pinnedGrpObj)){let{value:value}=pinnedGrpObj;pinnedRow={[value]:clonedEventList[value]},delete clonedEventList[value]}return{pinnedRow:pinnedRow,eventList:clonedEventList}},isDayView(){return"Day"===calenderViews[this.currentCalendarView]},isYearView(){return"Year"===calenderViews[this.currentCalendarView]},blockTime(){const blockTimeInMS={HOUR:36e5,WEEK:6048e5,DAY:864e5};return this.isYearView?blockTimeInMS.WEEK:this.isDayView?blockTimeInMS.HOUR:blockTimeInMS.DAY},groupingListIds(){return(this.groupingListArr||[]).map((pickListItem=>pickListItem.value))},blockDivision(){return this.headerGridColumns.length},mainField(){let{fields:fields}=this.metaInfo||{},{name:name}=(fields||[]).find((fld=>fld.mainField))||{};return name||"name"},canShowColorLegend(){let{recordCustomization:recordCustomization}=this.viewDetails||{};return!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(recordCustomization)},currentViewTime(){return this.getTimeStamp().startOf("day").valueOf()}},watch:{pinnedGroupObj:{handler(newVal){this.pinnedGrpObj=newVal},immediate:!0},pinnedGrpObj(newVal){this.$emit("pinnedGrpObj",newVal)},schedulerData:{handler(){this.init(),this.saving=!1,this.activeEventTarget&&(this.activeEventTarget.style.zIndex="",this.activeEventTarget.style.left="",this.activeEventTarget.style.border="",this.activeEventTarget.style.boxShadow=""),this.showSummary&&(this.canShowSideBarList=!1)},immediate:!0},todayIndex(value){value>-1&&!this.loading&&this.scrollCurrentBlock(`header-${value}`)}},methods:{init(){this.constructTsCount(),this.constructEventList(),this.constructEventPosition()},switchView({startTime:startTime}){if(this.isYearView){let weekView=2;this.$emit("switchView",startTime,weekView)}else if(!this.isDayView){let dayView=1;this.$emit("switchView",startTime,dayView)}},getGroupLabelById(id){let filterList=(this.groupingListArr||[]).find((listItem=>listItem.value===id));return(filterList||{}).label},getMaxCount(grpEvent){return grpEvent.reduce(((maxCount,rowCount)=>(maxCount=rowCount>maxCount?rowCount:maxCount,maxCount)),0)},constructTsCount(){let{schedulerData:schedulerData,headerGridColumns:headerGridColumns,groupingListIds:groupingListIds}=this;groupingListIds.forEach((grpId=>{let rowCount=headerGridColumns.map((grid=>{let{startTime:startTime,endTime:endTime}=grid||{},availableTS=schedulerData[grpId]||{},timeStampsBtwGridCol=Object.keys(availableTS).filter((ts=>startTime<=parseInt(ts)&&endTime>=parseInt(ts))).map((ts=>parseInt(ts)));return timeStampsBtwGridCol.reduce(((totalCount,ts)=>{let{count:count}=availableTS[ts]||{};return totalCount+=count,totalCount}),0)})),maxCount=this.getMaxCount(rowCount);maxCount&&(this.eventCount[grpId]={rowCount:rowCount,maxCount:maxCount})}))},getGrpEventList(grpId){return Object.values(this.schedulerData[grpId]||{}).reduce(((eventList,tsEvents)=>{let{list:list}=tsEvents||{},dataList=list.map((dataObj=>{let{data:data,customization:customization}=dataObj||{},colorObj=customization&&JSON.parse(customization)||{};return{...data,...colorObj}}));return eventList=[...eventList,...dataList],eventList}),[])},updateRowTotalCount(grpId){let{maxCount:maxCount,rowCount:rowCount}=this.eventCount[grpId]||{};rowCount&&(maxCount=this.getMaxCount(rowCount),maxCount?this.eventCount[grpId]={rowCount:rowCount,maxCount:maxCount}:delete this.eventCount[grpId])},constructEventList(eventGrpId){let{groupingListIds:groupingListIds}=this;(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(eventGrpId)?(this.eventList={},groupingListIds.forEach((grpId=>{this.eventList[grpId]=this.getEventListForGrp(grpId),this.updateRowTotalCount(grpId)}))):(this.eventList[eventGrpId]=this.getEventListForGrp(eventGrpId),this.updateRowTotalCount(eventGrpId))},getBlockEventForGivenTS({eventList:eventList,timeStampKey:timeStampKey,currentTS:currentTS}){let{startTimeKey:startTimeKey,endTimeKey:endTimeKey}=timeStampKey||{},{startTime:startTime,endTime:endTime}=currentTS||{};return eventList.filter((eventObj=>{let{[startTimeKey]:workStart,[endTimeKey]:actualWorkEnd}=eventObj||{},workEnd=workStart>actualWorkEnd?null:actualWorkEnd;return startTime<=workStart&&endTime>=workStart||startTime<=workEnd&&endTime>=workEnd||workStart<startTime&&workEnd>endTime}))},constructBlockEvents({blockEventList:blockEventList,calenderDetails:calenderDetails,grpId:grpId,blockIndex:blockIndex,grid:grid}){let{blockDivision:blockDivision,currentTimeLimit:currentTimeLimit,blockTime:blockTime}=this,{startTimeKey:startTimeKey,endTimeKey:endTimeKey,mainField:mainField}=calenderDetails||{};return blockEventList.map((eventObj=>{let{[startTimeKey]:workStart,[endTimeKey]:actualWorkEnd,[mainField]:name,id:id,eventColor:eventColor="#4d95ff",moduleState:moduleState}=eventObj||{},{startTime:startTime,endTime:endTime}=grid,hasAfterLimit=!1,hasBeforeLimit=!1,blockSpan=1,width=1,className="",workEnd=workStart>actualWorkEnd?null:actualWorkEnd,{startTime:currentST}=currentTimeLimit;workStart<currentST&&(hasBeforeLimit=!0),(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(workEnd)&&(workEnd=workStart+36e5);let startScope=startTime<=workStart&&endTime>=workStart||0===blockIndex&&hasBeforeLimit,endScope=workEnd>=startTime&&workEnd<=endTime;startScope?endTime<workEnd&&(width+=Math.ceil((workEnd-endTime)/blockTime),blockSpan=width,width>blockDivision-blockIndex&&(width=blockDivision-blockIndex,hasAfterLimit=!0)):className="transparent";let{hasAssignEnabled:hasAssignEnabled,hasRescheduleEnabled:hasRescheduleEnabled,hasReAssignEnabled:hasReAssignEnabled,disabledPastEvents:disabledPastEvents,currentViewTime:currentViewTime,updatePermission:updatePermission}=this,isLocked=!this.canEditRecord(eventObj),canEdit=startScope&&updatePermission&&(hasRescheduleEnabled||hasReAssignEnabled||hasAssignEnabled&&-1===parseInt(grpId))&&(!disabledPastEvents||disabledPastEvents&&workStart>currentViewTime),draggable=!hasBeforeLimit&&canEdit,disableResize=!(updatePermission&&!isLocked&&hasRescheduleEnabled),startResizeDisabled=!startScope||disableResize||disabledPastEvents&&workStart<currentViewTime,endResizeDisabled=!startScope||disableResize||disabledPastEvents&&workEnd<currentViewTime,cursor=isLocked?"url(data:image/svg+xml,%3Csvg%20xmlns=%22http://www.w3.org/2000/svg%22%20viewBox=%220%200%2011%2015%22%20width=%2214px%22%20height=%2214px%22%3E%0A%20%20%3Cpath%20d=%22M7.934%205.493H3.052v-1.22a2.444%202.444%200%200%201%202.44-2.442%202.444%202.444%200%200%201%202.442%202.441v1.221zm2.747%200h-.916v-1.22A4.275%204.275%200%200%200%205.493%200%204.277%204.277%200%200%200%201.22%204.272v1.221H.305A.305.305%200%200%200%200%205.798v7.63c0%20.673.548%201.22%201.221%201.22h8.544c.674%200%201.221-.547%201.221-1.22v-7.63a.305.305%200%200%200-.305-.305z%22/%3E%0A%3C/svg%3E) 10 10, auto":!draggable&&startScope?"pointer":"grab",{timezone:timezone,dateformat:dateformat,timeformat:timeformat}=this;return{workStart:workStart,workEnd:workEnd,actualWorkEnd:actualWorkEnd,isNeedConfirm:!actualWorkEnd||workStart>actualWorkEnd,name:name,id:id,width:`calc(((100%/${blockDivision}) * ${width}) - ${startScope?10:endScope?20:0}px)`,cursor:cursor,hasAfterLimit:hasAfterLimit,hasBeforeLimit:hasBeforeLimit,eventColor:eventColor,startScope:startScope,className:className,blockIndex:blockIndex,blockSpan:blockSpan,canEdit:canEdit,draggable:draggable,isLocked:isLocked,moduleState:this.currentModuleState(moduleState),startResizeDisabled:startResizeDisabled,endResizeDisabled:endResizeDisabled,startTimeDisplayFormat:workStart?formatDate(workStart,{timezone:timezone,dateformat:dateformat,timeformat:timeformat}):null,endTimeDisplayFormat:actualWorkEnd?formatDate(actualWorkEnd,{timezone:timezone,dateformat:dateformat,timeformat:timeformat}):null}}))},getEventListForGrp(grpId,grpEventList){let{headerGridColumns:headerGridColumns,viewDetails:viewDetails,maxResultPerCell:maxResultPerCell,mainField:mainField}=this,{startDateField:startDateField,endDateField:endDateField}=viewDetails||{},{name:startTimeKey}=startDateField||{},{name:endTimeKey}=endDateField||{},eventList=grpEventList||this.getGrpEventList(grpId);return headerGridColumns.map(((grid,blockIndex)=>{let{startTime:startTime,endTime:endTime}=grid||{},dataToGetTotalBlockEvents={eventList:eventList,timeStampKey:{startTimeKey:startTimeKey,endTimeKey:endTimeKey},currentTS:{startTime:startTime,endTime:endTime}},totalBlockEvents=this.getBlockEventForGivenTS(dataToGetTotalBlockEvents),dataToConstructBlockEvents={blockEventList:totalBlockEvents,calenderDetails:{startTimeKey:startTimeKey,endTimeKey:endTimeKey,mainField:mainField},grpId:grpId,blockIndex:blockIndex,grid:grid};totalBlockEvents=this.constructBlockEvents(dataToConstructBlockEvents),this.updateRowCount({totalBlockEvents:totalBlockEvents,grpId:grpId,blockIndex:blockIndex});let passThroughEvents=sortBy_1(totalBlockEvents.filter((e=>e.hasBeforeLimit||!e.startScope)),["workStart"]),blockStartScopeEvents=orderBy_1(totalBlockEvents.filter((e=>!e.hasBeforeLimit&&e.startScope)),["blockSpan"],["desc"]),blockEvents=[...passThroughEvents,...blockStartScopeEvents],eventIdsToRemove=blockEvents.slice(maxResultPerCell).map((e=>e.id));return this.removeEventsFromlist(eventList,eventIdsToRemove),blockEvents.slice(0,maxResultPerCell)}))},currentModuleState(moduleState){let{moduleName:moduleName}=this,currentStateId=dlv__WEBPACK_IMPORTED_MODULE_6__(moduleState,"id"),currentState=this.getTicketStatus(currentStateId,moduleName),{displayName:displayName,status:status}=currentState||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(displayName)?status||null:displayName},updateRowCount({totalBlockEvents:totalBlockEvents,grpId:grpId,blockIndex:blockIndex}){if(this.hasActionOngoing)return;let{blockDivision:blockDivision,maxResultPerCell:maxResultPerCell}=this,totalBlockCount=totalBlockEvents.length,{rowCount:rowCount,maxCount:maxCount=0}=this.eventCount[grpId]||{},blockCount=dlv__WEBPACK_IMPORTED_MODULE_6__(rowCount,`${blockIndex}`,0);(totalBlockCount>maxResultPerCell||blockCount<totalBlockCount)&&(blockCount=blockCount>maxResultPerCell?blockCount-maxResultPerCell+totalBlockCount:totalBlockCount,(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isArray */.kJ)(rowCount)||(rowCount=Array(blockDivision).fill(0)),rowCount[blockIndex]=blockCount,this.eventCount[grpId]={rowCount:rowCount,maxCount:maxCount})},removeEventsFromlist(eventList,eventIdsToRemove){eventList=eventList.filter((e=>!eventIdsToRemove.includes(e.id)))},constructEventPosition(eventGrpId){let{eventList:eventList,eventsPositionMap:eventsPositionMap}=this;(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(eventGrpId)?Object.entries(eventList).forEach((([grpId,eventDetails])=>{eventsPositionMap[grpId]=this.calculateEventPosition(eventDetails,grpId)})):eventsPositionMap[eventGrpId]=this.calculateEventPosition(eventList[eventGrpId],eventGrpId)},calculateEventPosition(eventDetails,grpId){let grpEventList=eventDetails.reduce(((grpEventList,blockEventList)=>(grpEventList=[...grpEventList,...blockEventList],grpEventList)),[]).filter((event=>event.startScope)),eventPositionObj={},position=0,eventIdsToRemove=[];while(grpEventList.length>0){let endEventTime=0,lastBlockIndex=0;grpEventList.forEach((event=>{let{id:id,workStart:workStart,workEnd:workEnd,blockIndex:blockIndex}=event||{};if(workStart>endEventTime||workStart===endEventTime&&blockIndex>lastBlockIndex){let{endTime:endTime}=this.headerGridColumns.find((d=>d.startTime<=workEnd&&d.endTime>=workEnd))||{};eventPositionObj[id]=position,endEventTime=endTime,lastBlockIndex=blockIndex,eventIdsToRemove.push(id)}})),position++,grpEventList=grpEventList.filter((event=>!eventIdsToRemove.includes(event.id)))}let styleObj={height:"3em"},rowMaxCount=position,{maxCount:maxCount=0}=this.eventCount[grpId]||{};return rowMaxCount>0&&(styleObj.height=40*rowMaxCount+10+"px",maxCount>5&&(styleObj.height=40*rowMaxCount+30+"px")),{...eventPositionObj,styleObj:styleObj}},scrollCurrentBlock(elementId){let currentBlockToScroll=document.getElementById(elementId);currentBlockToScroll.scrollIntoView({behavior:"smooth",block:"nearest",inline:"center"})},togglePinnedGrp(pinnedGrpId){if(this.pinnedGrpObj.value!==pinnedGrpId){let label=this.getGroupLabelById(pinnedGrpId);this.pinnedGrpObj={value:pinnedGrpId,label:label}}else this.pinnedGrpObj={}},bindElementDetails(blockIdx,grpId,eventId,startScope,sidePosition){let bindDetails={"block-index":blockIdx,"group-id":grpId};return eventId&&(bindDetails["event-id"]=eventId,bindDetails["start-scope"]=startScope),sidePosition&&(bindDetails["side-position"]=sidePosition),bindDetails},openSideBarList({title:title,grpId:grpId,blockIndex:blockIndex}){if((0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(title)){let groupName=this.getGroupLabelById(grpId),{startTime:startTime,endTime:endTime}=this.headerGridColumns[blockIndex]||{},title="";title=this.isDayView?this.getTimeStamp(startTime).format("hh:mm:ss a"):this.getTimeStamp(startTime).format("DD MMM"),this.sideBarDetails={title:title,groupName:groupName,startTime:startTime,endTime:endTime,grpId:grpId,blockIndex:blockIndex,currentView:this.currentCalendarView}}else this.sideBarDetails={title:title};this.canShowSideBarList=!0},getGridBlockClass(gridIdx){let{disabledWeekend:disabledWeekend,weekendIndex:weekendIndex,disabledPastDate:disabledPastDate,headerGridColumns:headerGridColumns,currentViewTime:currentViewTime}=this,{endTime:endTime}=headerGridColumns[gridIdx]||{},hasDisabledWeekend=disabledWeekend&&weekendIndex.includes(gridIdx),hasDisabledPastDate=disabledPastDate&&currentViewTime>endTime;return hasDisabledWeekend||hasDisabledPastDate?"grid-item-disabled":""},canEditRecord(eventObj){return!!this.updatePermission&&(!this.isStateFlowEnabled(eventObj)||!this.isRecordLocked(eventObj)&&!this.isRequestedState(eventObj))},isStateFlowEnabled(eventObj){let{module:moduleDetails}=this.metaInfo||{},{custom:custom,stateFlowEnabled:stateFlowEnabled}=moduleDetails||{},{moduleState:moduleState}=eventObj||{},hasState=dlv__WEBPACK_IMPORTED_MODULE_6__(moduleState,"id");return hasState&&(!custom||custom&&stateFlowEnabled)},isRecordLocked(eventObj){let{moduleName:moduleName}=this,{moduleState:moduleState}=eventObj||{};if(this.isStateFlowEnabled){let hasState=dlv__WEBPACK_IMPORTED_MODULE_6__(moduleState,"id");return hasState&&this.isStatusLocked(hasState,moduleName)}return!1},isRequestedState(eventObj){let{approvalStatus:approvalStatus}=eventObj;if(!(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(approvalStatus)){let statusObj=this.getApprovalStatus(approvalStatus.id);return dlv__WEBPACK_IMPORTED_MODULE_6__(statusObj,"requestedState",!1)}return!1},openSummary(eventObj,grpObj){this.currentEventObj=eventObj,this.currentEventGrpObj=grpObj,this.showSummary=!0},closeTabs(){this.showColorLegend=!1,this.canShowSideBarList=!1}}};
/* script */const __vue_script__$1=script$1;
/* template */var __vue_render__$1=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"scheduler-table"},[_c("div",{staticClass:"grid-container"},[_vm._t("ftags"),_vm._v(" "),_c("div",{staticClass:"table-container-layout",style:{maxHeight:_vm.filters?"calc(100% - 140px)":"calc(100% - 70px)"}},[_vm.loading?_vm._t("spinner"):_c("div",{staticClass:"table-container"},[_c("div",{staticClass:"grid-row-fixed grid-row"},[_c("div",{staticClass:"grid-item-sidebar grid-item"},[_vm._t("search-sort")],2),_vm._v(" "),_vm._l(_vm.headerGridColumns,(function(headerGrid,index){return _c("div",{key:"sidebar-"+index,staticClass:"grid-item header-item",class:{"highlight-grid-item":_vm.resizeHighlighter.startTimeIdx<=index&&_vm.resizeHighlighter.endTimeIdx>=index,"today-highlight":_vm.todayIndex===index,pointer:!_vm.isDayView},attrs:{id:"header-"+index},on:{click:function($event){return _vm.switchView(headerGrid)}}},[_vm.isDayView?[_c("div",{staticClass:"m-auto time-period line-height-18"},[_vm._v("\n                "+_vm._s(headerGrid.time)+"\n              ")])]:_vm.isYearView?[_c("div",{staticClass:"m-auto time-period line-height-18"},[_vm._v("\n                "+_vm._s(headerGrid.week)+"\n              ")])]:[_c("div",{staticClass:"time-period time-period-day"},[_vm._v("\n                "+_vm._s(headerGrid.day)+"\n              ")]),_vm._v(" "),_c("div",{staticClass:"time-period time-period-date"},[_vm._v("\n                "+_vm._s(headerGrid.date)+"\n              ")])]],2)}))],2),_vm._v(" "),_vm._l(_vm.totalEventList,(function(eventDetails,name){return[_vm._l(_vm.groupingListIds,(function(rowGrpId){return[eventDetails[rowGrpId]?[_c("div",{key:name+"-"+rowGrpId,class:["relative grid-row","pinnedRow"===name?"pinned-grid":""],style:_vm.eventsPositionMap[rowGrpId].styleObj},[_c("div",{staticClass:"grid-item-sidebar grid-item",on:{click:function($event){return _vm.togglePinnedGrp(rowGrpId)}}},[_vm._v("\n                  "+_vm._s(_vm.getGroupLabelById(rowGrpId))+"\n                  "),_c("PushPin",{class:["svg-icon-16",rowGrpId===_vm.pinnedGrpObj.value?"pinned":"unpinned"]})],1),_vm._v(" "),_c("div",{staticClass:"relative grid-row",style:_vm.eventsPositionMap[rowGrpId].styleObj,on:{mousemove:function($event){return $event.stopPropagation(),_vm.mouseMoveEvent.apply(null,arguments)},mouseup:function($event){return $event.stopPropagation(),_vm.mouseUpEvent.apply(null,arguments)}}},_vm._l(eventDetails[rowGrpId],(function(gridEventList,gridEventIdx){return _c("div",_vm._b({key:"grid-event-"+gridEventIdx,class:["grid-item",_vm.getGridBlockClass(gridEventIdx)],attrs:{id:"grid-event-"+rowGrpId+"-"+gridEventIdx}},"div",_vm.bindElementDetails(gridEventIdx,rowGrpId),!1),[gridEventList.length>0?[_vm._l(gridEventList,(function(eventObj){return _c("div",_vm._b({key:eventObj.name+"-"+eventObj.id+"-"+gridEventIdx,staticClass:"event-class d-flex",class:[""+eventObj.className,!eventObj.startScope&&!_vm.hasActionOngoing&&"visible-hidden"],style:{width:eventObj.width,top:3*_vm.eventsPositionMap[rowGrpId][eventObj.id]+"em","background-color":eventObj.eventColor,cursor:eventObj.cursor},attrs:{draggable:!eventObj.isLocked&&!_vm.isResized&&eventObj.draggable}},"div",_vm.bindElementDetails(gridEventIdx,rowGrpId,eventObj.id,eventObj.startScope),!1),[eventObj.hasBeforeLimit?_c("LeftArrow",{staticClass:"svg-icon-18"}):eventObj.startResizeDisabled?_vm._e():_c("div",_vm._b({staticClass:"resize-div",on:{mousedown:function($event){return $event.stopPropagation(),_vm.mouseDownEvent($event,eventObj)}}},"div",_vm.bindElementDetails(gridEventIdx,rowGrpId,eventObj.id,eventObj.startScope,"start"),!1),[_vm._v("\n                          ||\n                        ")]),_vm._v(" "),_c("PopupDialog",{key:"popup-table-"+eventObj.id,staticClass:"event-item-class",attrs:{borderColor:eventObj.eventColor,isTimelineSideBarOpen:_vm.canShowSideBarList||_vm.showColorLegend,isDragging:_vm.isDragging},scopedSlots:_vm._u([{key:"content",fn:function(){return[_c("div",{staticClass:"timeline-event-tooltip-content"},[eventObj.isLocked||!eventObj.canEdit?_c("div",{staticClass:"warning-text"},[_c("i",{staticClass:"el-icon-info",staticStyle:{"margin-right":"5px"}}),_vm._v(" "),_c("div",[_vm._v("\n                                  You don't have permission to edit this\n                                  record\n                                ")])]):_vm._e(),_vm._v(" "),_c("div",{staticClass:"event-name"},[_vm._v("\n                                "+_vm._s(eventObj.name)+"\n                              ")]),_vm._v(" "),eventObj.startTimeDisplayFormat?[_c("div",{staticClass:"event-time"},[_c("div",{staticClass:"event-time-format"},[_vm._v("\n                                    "+_vm._s(eventObj.startTimeDisplayFormat)+"\n                                  ")]),_vm._v(" "),_c("div",{staticClass:"time-divider"},[_vm._v("-")]),_vm._v(" "),_c("div",{staticClass:"event-time-format"},[_vm._v("\n                                    "+_vm._s(eventObj.endTimeDisplayFormat?eventObj.endTimeDisplayFormat:"---")+"\n                                  ")])])]:_vm._e()],2)]},proxy:!0}],null,!0)},[_vm._v(" "),_c("div",_vm._b({staticClass:"text-overflow-ellipsis",on:{click:function($event){_vm.openSummary(eventObj,{id:rowGrpId,name:_vm.getGroupLabelById(rowGrpId)})}}},"div",_vm.bindElementDetails(gridEventIdx,rowGrpId,eventObj.id,eventObj.startScope),!1),[_vm._v("\n                            "+_vm._s((eventObj.startScope,eventObj.name))+"\n                          ")])]),_vm._v(" "),eventObj.hasAfterLimit?_c("RightArrow",{staticClass:"svg-icon-18"}):eventObj.endResizeDisabled?_vm._e():_c("div",_vm._b({staticClass:"resize-div",on:{mousedown:function($event){return $event.stopPropagation(),_vm.mouseDownEvent($event,eventObj)}}},"div",_vm.bindElementDetails(gridEventIdx,rowGrpId,eventObj.id,eventObj.startScope,"end"),!1),[_vm._v("\n                          ||\n                        ")])],1)})),_vm._v(" "),_vm.eventCount[rowGrpId]&&_vm.eventCount[rowGrpId].rowCount[gridEventIdx]>_vm.maxResultPerCell?_c("div",{staticClass:"more-events",on:{click:function($event){return _vm.openSideBarList({grpId:rowGrpId,blockIndex:gridEventIdx})}}},[_c("div",{staticClass:"view-more-txt"},[_vm._v("View More")])]):_vm._e()]:_vm._e()],2)})),0)])]:_vm._e()]}))]}))],2)],2),_vm._v(" "),_vm._t("grp-pagination")],2),_vm._v(" "),_vm.canShowSideBarList?_c("SidebarList",{ref:"sidebar-list",attrs:{viewname:_vm.viewname,moduleName:_vm.moduleName,viewDetails:_vm.viewDetails,groupingListArr:_vm.groupingListArr,details:_vm.sideBarDetails,canEditRecord:_vm.canEditRecord,currentModuleState:_vm.currentModuleState,hasRescheduleEnabled:_vm.hasRescheduleEnabled,hasReAssignEnabled:_vm.hasReAssignEnabled,hasAssignEnabled:_vm.hasAssignEnabled,updatePermission:_vm.updatePermission,disabledPastEvents:_vm.disabledPastEvents,currentViewTime:_vm.currentViewTime,mainField:_vm.mainField,blockTime:_vm.blockTime,blockDivision:_vm.blockDivision,timezone:_vm.timezone,dateformat:_vm.dateformat,timeformat:_vm.timeformat},on:{openQuickSummary:_vm.openSummary,onClose:_vm.closeTabs},scopedSlots:_vm._u([{key:"spinner",fn:function(){return[_vm._t("spinner")]},proxy:!0}],null,!0)}):_vm.showColorLegend?_c("ColorLegend",{attrs:{viewDetails:_vm.viewDetails,metaInfo:_vm.metaInfo,moduleName:_vm.moduleName},on:{onClose:_vm.closeTabs},scopedSlots:_vm._u([{key:"spinner",fn:function(){return[_vm._t("spinner")]},proxy:!0}],null,!0)}):_c("div",{staticClass:"unscheduled-sidebar-list"},[_vm.canShowColorLegend?_c("div",{staticClass:"color-icon-block",on:{click:function($event){_vm.showColorLegend=!0}}},[_c("ColorLegendIcon",{staticClass:"color-icon"})],1):_vm._e(),_vm._v(" "),_c("div",{staticClass:"unscheduled-closed-popup",on:{click:function($event){return _vm.openSideBarList({title:"Unscheduled"})}}},[_c("div",{staticClass:"unscheduled-header"},[_vm._v("Unscheduled list")])])]),_vm._v(" "),_c("portal",{attrs:{to:"event-save"}},[_c("div",{staticClass:"scheduler-saving"},[_vm._v("\n      "+_vm._s(_vm.saving?"Saving...":"")+"\n    ")])]),_vm._v(" "),_vm.showSummary?_c("EventSummary",_vm._b({attrs:{moduleName:_vm.moduleName,viewDetails:_vm.viewDetails,groupingListArr:_vm.groupingListArr,metaInfo:_vm.metaInfo,eventObj:_vm.currentEventObj,timezone:_vm.timezone,dateformat:_vm.dateformat,timeformat:_vm.timeformat,groupObj:_vm.currentEventGrpObj,saving:_vm.saving},on:{onClose:function($event){_vm.showSummary=!1}}},"EventSummary",_vm.$attrs,!1)):_vm._e()],1)},__vue_staticRenderFns__$1=[];
/* style */
const __vue_inject_styles__$1=function(inject){inject&&(inject("data-v-35d07063_0",{source:".scheduler-table[data-v-35d07063]{display:flex;height:100%}.scheduler-table .grid-container[data-v-35d07063]{margin:10px;overflow:hidden;flex-grow:1}.scheduler-table .grid-container .table-container-layout[data-v-35d07063]{overflow:auto;display:grid}.scheduler-table .grid-container .table-container-layout .table-container[data-v-35d07063]{height:max-content;background:#fff;display:flex;flex-direction:column}.scheduler-table .grid-container .table-container-layout .grid-row[data-v-35d07063]{display:flex;flex-grow:1}.scheduler-table .grid-container .table-container-layout .grid-row .grid-item[data-v-35d07063]{border-bottom:1px solid #e5effe;border-right:1px solid #e5effe}.scheduler-table .grid-container .table-container-layout .grid-row .grid-item.grid-item-disabled[data-v-35d07063]{background:#fbfbfc;cursor:not-allowed}.scheduler-table .grid-container .table-container-layout .grid-row:last-of-type .grid-item[data-v-35d07063]{border-bottom:1px solid transparent}.scheduler-table .grid-container .table-container-layout .grid-item-sidebar+.grid-row:last-of-type .grid-item[data-v-35d07063]{border-bottom:1px solid #e5effe}.scheduler-table .grid-container .table-container-layout .grid-row:last-of-type .grid-item-sidebar+.grid-row:last-of-type .grid-item[data-v-35d07063]{border-bottom:1px solid transparent}.scheduler-table .grid-container .table-container-layout .grid-row-fixed[data-v-35d07063]{position:sticky;top:0;z-index:4;background:#fff;height:50px}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .grid-item-sidebar.grid-item[data-v-35d07063]{padding:0 15px;border:1px solid #e5effe}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .grid-item-sidebar.grid-item .grp-name[data-v-35d07063]{letter-spacing:.5px;text-transform:uppercase;color:#324056;font-size:14px;font-weight:700}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .grid-item[data-v-35d07063]{padding:0 10px;border-top:1px solid #e5effe;border-bottom:1px solid #e5effe;border-right:1px solid #e5effe}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .header-item[data-v-35d07063]{padding:5px 0;flex-direction:column}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .header-item[data-v-35d07063]:hover:not(.today-highlight){background:#f8f8f9}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .header-item:hover:not(.today-highlight) .time-period[data-v-35d07063]{color:#5e6776}.scheduler-table .grid-container .table-container-layout .grid-row-fixed .header-item:hover .time-period[data-v-35d07063]{font-weight:700}.scheduler-table .grid-container .table-container-layout .grid-item[data-v-35d07063]{min-width:100px;flex-grow:1;width:100px;display:flex;align-items:center;justify-content:space-between}.scheduler-table .grid-container .table-container-layout .grid-item-sidebar[data-v-35d07063]{width:200px;position:sticky;background:#fff;left:0;flex-grow:0;z-index:2;padding:15px}.scheduler-table .grid-container .table-container-layout .grid-item-sidebar.grid-item[data-v-35d07063]{border-left:1px solid #e5effe}.scheduler-table .grid-container .table-container-layout .grid-item-sidebar:hover .unpinned[data-v-35d07063]{fill:#ccc}.scheduler-table .grid-container .table-container-layout .grid-item-sidebar:hover .unpinned[data-v-35d07063]:hover{fill:#324056}.scheduler-table .grid-container .table-container-layout .time-period[data-v-35d07063]{letter-spacing:.5px;font-size:14px;text-transform:uppercase;color:#5e6776;font-weight:500}.scheduler-table .grid-container .table-container-layout .time-period.time-period-date[data-v-35d07063]{font-size:18px}.scheduler-table .grid-container .table-container-layout .time-period.time-period-day[data-v-35d07063]{font-size:12px}.scheduler-table .grid-container .table-container-layout .m-auto[data-v-35d07063]{margin:auto}.scheduler-table .grid-container .table-container-layout .line-height-18[data-v-35d07063]{line-height:18px}.scheduler-table .grid-container .table-container-layout .today-highlight[data-v-35d07063]{background:#4d95ff}.scheduler-table .grid-container .table-container-layout .today-highlight .time-period[data-v-35d07063]{color:#fff}.scheduler-table .grid-container .table-container-layout .highlight-grid-item[data-v-35d07063]{background:#5e5ce6}.scheduler-table .grid-container .table-container-layout .highlight-grid-item .time-period[data-v-35d07063]{color:#fff}.scheduler-table .grid-container .table-container-layout .pinned-grid[data-v-35d07063]{position:sticky!important;top:30px;z-index:3;background:#fff}.scheduler-table .grid-container .table-container-layout .svg-icon-16[data-v-35d07063]{height:16px;width:16px;cursor:pointer}.scheduler-table .grid-container .table-container-layout .event-class[data-v-35d07063]{position:absolute;height:30px;color:#fff;font-weight:400;font-size:12px;cursor:grab;border-radius:5px;margin:5px;display:flex;justify-content:center;align-items:center;transition:all .5s ease}.scheduler-table .grid-container .table-container-layout .event-class.transparent[data-v-35d07063]{opacity:0;z-index:1;margin:5px 0}.scheduler-table .grid-container .table-container-layout .event-class.transparent .resize-div[data-v-35d07063],.scheduler-table .grid-container .table-container-layout .event-class.transparent .svg-icon-18[data-v-35d07063]{visibility:hidden}.scheduler-table .grid-container .table-container-layout .event-class.visible-hidden[data-v-35d07063]{visibility:hidden}.scheduler-table .grid-container .table-container-layout .event-class .text-overflow-ellipsis[data-v-35d07063]{white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.scheduler-table .grid-container .table-container-layout .event-class .resize-div[data-v-35d07063]{width:10px;height:100%;cursor:col-resize;color:#fff;font-size:13px;display:flex;justify-content:center;align-items:center;visibility:hidden}.scheduler-table .grid-container .table-container-layout .event-class .event-item-class[data-v-35d07063]{margin-left:5px;flex-grow:1;line-height:30px;width:calc(100% - 40px)}.scheduler-table .grid-container .table-container-layout .event-class .svg-icon-18[data-v-35d07063]{height:18px;width:18px;fill:#fff;fill-opacity:.8}.scheduler-table .grid-container .table-container-layout .event-class:hover .resize-div[data-v-35d07063]{visibility:visible}.scheduler-table .grid-container .table-container-layout .pinned[data-v-35d07063]{fill:#324056}.scheduler-table .grid-container .table-container-layout .unpinned[data-v-35d07063]{fill:transparent}.scheduler-table .grid-container .table-container-layout .more-events[data-v-35d07063]{display:flex;position:absolute;width:inherit;justify-content:center;font-size:10px;letter-spacing:.29px;bottom:.5em;cursor:pointer;color:#324056;line-height:.75}.scheduler-table .grid-container .table-container-layout .more-events .view-more-txt[data-v-35d07063]{border-radius:8px;padding:4px 15px;margin:5px;border:1px solid #dcdfe6}.scheduler-table .grid-container .table-container-layout .more-events .view-more-txt[data-v-35d07063]:hover{border:1px solid #4d95ff}.scheduler-table .unscheduled-sidebar-list[data-v-35d07063]{z-index:4;box-shadow:-1px 0 2px 0 rgba(0,0,0,.11);background:#fff}.scheduler-table .unscheduled-sidebar-list .color-icon-block[data-v-35d07063]{display:flex;margin-top:10px}.scheduler-table .unscheduled-sidebar-list .color-icon-block .color-icon[data-v-35d07063]{height:30px;width:30px;margin:auto;cursor:pointer;border-radius:4px;padding:5px}.scheduler-table .unscheduled-sidebar-list .color-icon-block .color-icon[data-v-35d07063]:hover{background:#f3f4f7}.scheduler-table .unscheduled-sidebar-list .unscheduled-closed-popup[data-v-35d07063]{cursor:pointer;min-width:30px;display:flex;flex-direction:column;align-items:center;font-size:14px;letter-spacing:.5px;color:#324056;margin:10px 5px}.scheduler-table .unscheduled-sidebar-list .unscheduled-closed-popup .unscheduled-header[data-v-35d07063]{writing-mode:vertical-rl;transform:rotate(180deg);border-radius:4px;padding:8px 7px}.scheduler-table .unscheduled-sidebar-list .unscheduled-closed-popup .unscheduled-header[data-v-35d07063]:hover{background:#f3f4f7}.scheduler-saving[data-v-35d07063]{font-size:14px;font-weight:500;font-style:italic;letter-spacing:.4px;color:#39b2c2;margin-right:20px}",map:void 0,media:void 0}),inject("data-v-35d07063_1",{source:".timeline-event-tooltip-content .warning-text{display:flex;margin-bottom:10px;align-items:center;color:#0566f6;font-size:10px}.timeline-event-tooltip-content .event-name{font-size:12px;font-weight:500;letter-spacing:.43px;color:#324056;margin-bottom:10px;line-height:20px}.timeline-event-tooltip-content .event-time{display:flex;align-items:center}.timeline-event-tooltip-content .event-time .event-time-format{font-size:11px;letter-spacing:.43px;color:#324056;max-width:80px;line-height:15px}.timeline-event-tooltip-content .event-time .time-divider{font-size:12px;letter-spacing:.43px;color:#324056;padding:0 15px 0 10px}",map:void 0,media:void 0}))},__vue_scope_id__$1="data-v-35d07063",__vue_module_identifier__$1=void 0,__vue_is_functional_template__$1=!1,__vue_component__$1=normalizeComponent({render:__vue_render__$1,staticRenderFns:__vue_staticRenderFns__$1},__vue_inject_styles__$1,__vue_script__$1,__vue_scope_id__$1,__vue_is_functional_template__$1,__vue_module_identifier__$1,!1,createInjector,void 0,void 0),viewDisplayValue={DAY:1,WEEK:2,MONTH:3,YEAR:4};
/* scoped */var script={name:"SchedulerWrapper",props:["moduleName","viewname","timezone","viewDetails","timeStamp","filterObj"],components:{SchedulerTable:__vue_component__$1,GroupPagination:__vue_component__$6,Sort:_facilio_ui_app__WEBPACK_IMPORTED_MODULE_7__/* .Sort */.PE},data(){return{loading:!0,page:1,schedulerData:{},groupingListArr:[],hasGrouplist:!1,hasMoreList:!1,currentGrpCount:0,searchText:"",currentCalendarView:null,currentTimeLimit:{startTime:null,endTime:null},headerGridColumns:[],pinnedGrpObj:{},currentTime:this.getTimeStamp().valueOf(),isEmpty:_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb,sortObj:null,showSearchIcon:!0,calenderViews:calenderViews,freezeTable:null,currentSearchText:""}},async created(){this.currentTimeLimit=this.preservedTimeLimit},computed:{groupName(){let{groupByField:groupByField}=this.viewDetails||{},{displayName:displayName}=groupByField||{};return displayName},sortByField(){let{viewDetails:viewDetails}=this;return(viewDetails||{}).groupByField||{}},initialLoading(){return this.loading&&(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(this.groupingListArr)},currentView:{get(){let{defaultCalendarView:defaultCalendarView}=this.viewDetails||{},{query:query}=this.$route||{};return this.currentCalendarView||viewDisplayValue[null===query||void 0===query?void 0:query.currentView]||defaultCalendarView},set(value){this.currentCalendarView=value}},preservedTimeLimit(){let{query:query}=this.$route||{},{startTime:startTime,endTime:endTime=null}=query||{};return{startTime:startTime?parseInt(startTime):null,endTime:endTime?parseInt(endTime):null}},todayIndex(){let{getTimeStamp:getTimeStamp,headerGridColumns:headerGridColumns}=this,value=getTimeStamp().valueOf();return headerGridColumns.findIndex((grid=>grid.startTime<=value&&grid.endTime>=value))},filters(){if((0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isUndefined */.o8)(this.filterObj)){let{query:query}=this.$route||{},{search:search}=query||{};return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(search)?null:JSON.parse(search)}return(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(this.filterObj)?null:this.filterObj},getUnGrouped(){let{pinnedGrpObj:pinnedGrpObj,page:page}=this,{value:value}=pinnedGrpObj;return 1===page||-1===parseInt(value)},maxResultPerCell(){let{currentView:currentView}=this;return currentView===viewDisplayValue.YEAR?3:5},perPage(){let{currentView:currentView}=this;return currentView===viewDisplayValue.YEAR?10:20}},watch:{page(){this.loadGroupingData()},timeStamp(newVal){let{operationOn:operationOn,value:value}=newVal||{},[startTime,endTime]=value||[];this.currentView=viewDisplayValue[operationOn.toUpperCase()],this.currentTimeLimit={startTime:startTime,endTime:endTime},this.currentTime=startTime,this.constructData()},filters(){this.loadData()},viewDetails:{handler(newVal){(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(newVal)||this.loadGroupingData()},immediate:!0},currentView(newVal,oldVal){let previousPage=this.page;newVal===viewDisplayValue.YEAR?this.page=2*this.page-1:oldVal===viewDisplayValue.YEAR&&(this.page=Math.ceil(this.page/2)),[newVal,oldVal].includes(viewDisplayValue.YEAR)?1===previousPage&&this.loadGroupingData():this.init()}},methods:{getTimeStamp(timeStamp){return getMoment(this.timezone,timeStamp)},async loadGroupingData(sortObj,searchInvoke=!1){sortObj&&(this.sortObj=sortObj),this.loading=!0;let{page:page,searchText:searchText,viewDetails:viewDetails,perPage:perPage}=this,{groupByField:groupByField,moduleName:timelineModuleName,name:timelineViewName}=viewDetails||{},{displayName:displayName,name:name}=groupByField||{},url="/v3/timelineGroupData/fetch",sortContent=this.sortObj||{orderBy:name,orderType:"asc"},params={page:page,perPage:perPage,timelineModuleName:timelineModuleName,timelineViewName:timelineViewName,viewName:"hidden-all",...sortContent};(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(searchText)?this.currentSearchText=searchText:searchInvoke&&(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(sortObj)?(this.page=1,params.page=this.page,params.search=searchText,this.currentSearchText=searchText):params.search=this.currentSearchText;let{error:error,data:data}=await _facilio_api__WEBPACK_IMPORTED_MODULE_2__/* .API */.bl.get(url,params);if(error)this.$message.error(error.message||"Error Occurred");else{let{pickList:pickList}=data||{};this.hasGrouplist=(null===pickList||void 0===pickList?void 0:pickList.length)>=perPage||1!==page,this.hasMoreList=(null===pickList||void 0===pickList?void 0:pickList.length)>=perPage,this.currentGrpCount=(null===pickList||void 0===pickList?void 0:pickList.length)||0,this.setGrpData(pickList||[],displayName)}},searchGroup(clearInvoke=!1){let{searchText:searchText,currentSearchText:currentSearchText}=this;clearInvoke&&(this.searchText="",this.currentSearchText="",this.toggleSearchIcon()),(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(currentSearchText)&&(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isEmpty */.xb)(searchText)||this.$nextTick((()=>{this.loadGroupingData(null,!0)}))},onPinGroupChange(newPinObj){let{currentGrpCount:currentGrpCount,page:page}=this,maxGrpCount=1===page?currentGrpCount+=1:currentGrpCount,shiftArr=maxGrpCount<(this.groupingListArr||[]).length;shiftArr&&(0,_facilio_utils_validation__WEBPACK_IMPORTED_MODULE_8__/* .isArray */.kJ)(this.groupingListArr)&&this.groupingListArr.shift(),this.pinnedGrpObj=newPinObj},toggleSearchIcon(){this.showSearchIcon=!this.showSearchIcon,this.showSearchIcon||this.$nextTick((()=>{this.$refs["search-input"].focus()}))},setGrpData(groupingListArr,displayName){let{value:value,label:label}=this.pinnedGrpObj||{},isPinnedGrpPresent=groupingListArr.find((item=>item.value===value));if(this.getUnGrouped){let unGroupIndex=groupingListArr.findIndex((item=>-1===item.value)),unGroupList={value:-1,label:`No ${displayName}`};-1!==unGroupIndex?groupingListArr.splice(unGroupIndex,1,unGroupList):groupingListArr.unshift(unGroupList)}value>0&&!isPinnedGrpPresent&&groupingListArr.unshift({value:value,label:label}),this.groupingListArr=groupingListArr,this.init()},init(){let{currentTime:currentTime,currentTimeLimit:currentTimeLimit}=this,{startTime:startTime,endTime:endTime}=currentTimeLimit||{},isInCurrentTimeLimit=!startTime&&!endTime||startTime<=currentTime&&endTime>=currentTime,date=isInCurrentTimeLimit?currentTime:startTime;this.changeTS(date)},changeTS(date,currentView){var _this$currentTimeLimi;currentView&&(this.currentView=currentView),this.setTimeLimit(date),this.$emit("viewChanged",{view:calenderViews[this.currentView].toUpperCase(),startTime:null===(_this$currentTimeLimi=this.currentTimeLimit)||void 0===_this$currentTimeLimi?void 0:_this$currentTimeLimi.startTime}),this.constructData()},setTimeLimit(date){let{currentView:currentView,getTimeStamp:getTimeStamp}=this,view=calenderViews[currentView].toLowerCase(),start=getTimeStamp(date).startOf(view).valueOf(),end=getTimeStamp(date).endOf(view).valueOf()-1;this.$set(this.currentTimeLimit,"startTime",start),this.$set(this.currentTimeLimit,"endTime",end)},constructData(){this.constructHeaderColumnData(),this.loadData()},constructHeaderColumnData(){this.headerGridColumns=[];let{currentTimeLimit:currentTimeLimit,currentView:view,getTimeStamp:getTimeStamp}=this,{startTime:startTime,endTime:endTime}=currentTimeLimit||{},currentTimeStamp=startTime;while(currentTimeStamp<=endTime){let weekVal=getTimeStamp(currentTimeStamp).format("w"),endTimeStamp=this.getNextTimeStamp(currentTimeStamp,viewState[view].gridLines,weekVal),gridColumnObj={startTime:currentTimeStamp,endTime:endTimeStamp-1,date:getTimeStamp(currentTimeStamp).get("date"),day:weekdays[getTimeStamp(currentTimeStamp).day()],year:getTimeStamp(currentTimeStamp).get("year"),time:getTimeStamp(currentTimeStamp).format("h a"),week:weekVal+" W"};this.headerGridColumns.push(gridColumnObj),currentTimeStamp=endTimeStamp}},getNextTimeStamp(timeStamp,interval,weekVal){return"1"===weekVal&&"WEEK"===interval&&(timeStamp=this.getTimeStamp(timeStamp).startOf("week").valueOf()),this.getTimeStamp(timeStamp).add(1,intervalObj[interval]).valueOf()},async loadData(){this.loading=!0,this.schedulerData={};let{moduleName:moduleName,currentTimeLimit:currentTimeLimit,currentView:currentView,filters:filters,getUnGrouped:getUnGrouped,viewname:viewname,maxResultPerCell:maxResultPerCell}=this,{startTime:startTime,endTime:endTime}=currentTimeLimit||{},viewGroupIds=(this.groupingListArr||[]).map((l=>parseInt(l.value))).filter((l=>l>0)),params={timelineRequest:{viewName:viewname,startTime:startTime,endTime:endTime,moduleName:moduleName,dateAggregateOperator:dateOperator[currentView],maxResultPerCell:maxResultPerCell,groupIds:viewGroupIds,getUnGrouped:getUnGrouped,filters:filters}},{error:error,data:data}=await loadSchedulerData(params);error?this.$message.error(error.message||"Failed to fetch group events"):this.schedulerData=data,this.loading=!1},todayClicked(){let{startTime:startTime,endTime:endTime}=this.currentTimeLimit||{},currentTime=this.getTimeStamp().valueOf(),hasCurrentLimit=startTime<=currentTime&&endTime>=currentTime;hasCurrentLimit?this.$refs["scheduler-table"].scrollCurrentBlock(`header-${this.todayIndex}`):this.changeTS(currentTime)},async updateEventList(grpIds,payload){let{moduleName:moduleName,currentTimeLimit:currentTimeLimit,currentView:currentView,filters:filters,viewname:viewname,maxResultPerCell:maxResultPerCell}=this,{startTime:startTime,endTime:endTime}=currentTimeLimit||{},groupIds=grpIds.map((l=>parseInt(l))).filter((l=>l>0)),getUnGrouped=grpIds.some((l=>-1===parseInt(l))),params={timelineRequest:{viewName:viewname,startTime:startTime,endTime:endTime,moduleName:moduleName,dateAggregateOperator:dateOperator[currentView],maxResultPerCell:maxResultPerCell,groupIds:groupIds,getUnGrouped:getUnGrouped,filters:filters},moduleName:moduleName,viewName:viewname,...payload},{error:error,data:data}=await updateSchedulerEvent(params);error?(this.$message.error(error.message||"Failed to fetch group events"),this.schedulerData={...this.schedulerData}):(grpIds.forEach((grpId=>{let grpIdHasValue=Object.keys(data);grpIdHasValue.includes(grpId)||delete this.schedulerData[grpId]})),this.schedulerData={...this.schedulerData,...data})}}};
/* script */const __vue_script__=script;
/* template */var __vue_render__=function(){var _vm=this,_h=_vm.$createElement,_c=_vm._self._c||_h;return _c("div",{staticClass:"h-100"},[_c("portal",{attrs:{to:"scheduler-today"}},[_c("el-button",{staticClass:"today-btn",on:{click:_vm.todayClicked}},[_vm._v(" Today ")])],1),_vm._v(" "),_c("portal",{attrs:{to:"calendar-view"}},[_c("el-select",{staticClass:"scheduler-calender-view",attrs:{size:"small"},model:{value:_vm.currentView,callback:function($$v){_vm.currentView=$$v},expression:"currentView"}},_vm._l(_vm.calenderViews,(function(view,viewId){return _c("el-option",{key:view+"-"+viewId,attrs:{label:view,value:parseInt(viewId)}})})),1)],1),_vm._v(" "),[_vm.initialLoading?_c("div",{staticClass:"h-100 d-flex"},[_vm._t("spinner")],2):_vm._e(),_vm._v(" "),_vm.isEmpty(_vm.groupingListArr)?_c("div",{staticClass:"scheduler-empty-grp-list"},[_vm._v("\n      No Groups Available\n    ")]):[_c("SchedulerTable",_vm._g(_vm._b({directives:[{name:"loading",rawName:"v-loading",value:_vm.loading,expression:"loading"}],ref:"scheduler-table",staticClass:"scheduler-table",attrs:{viewDetails:_vm.viewDetails,headerGridColumns:_vm.headerGridColumns,groupingListArr:_vm.groupingListArr,schedulerData:_vm.schedulerData,currentCalendarView:_vm.currentView,pinnedGroupObj:_vm.pinnedGrpObj,currentTimeLimit:_vm.currentTimeLimit,todayIndex:_vm.todayIndex,viewname:_vm.viewname,moduleName:_vm.moduleName,getTimeStamp:_vm.getTimeStamp,maxResultPerCell:_vm.maxResultPerCell,timezone:_vm.timezone},on:{pinnedGrpObj:_vm.onPinGroupChange,updateGrpEvents:_vm.updateEventList,switchView:_vm.changeTS},scopedSlots:_vm._u([{key:"spinner",fn:function(){return[_vm._t("spinner")]},proxy:!0},{key:"ftags",fn:function(){return[_vm._t("ftags")]},proxy:!0},{key:"search-sort",fn:function(){return[_c("div",{staticClass:"search-sort-group"},[_vm.showSearchIcon?_c("div",{staticClass:"grp-name"},[_vm._v(_vm._s(_vm.groupName))]):_c("div",{staticClass:"tgroup-flex-spacebetween"},[_c("input",{directives:[{name:"model",rawName:"v-model",value:_vm.searchText,expression:"searchText"}],ref:"search-input",staticClass:"search-btn-tgroup",attrs:{placeholder:" Search"},domProps:{value:_vm.searchText},on:{keyup:function($event){return!$event.type.indexOf("key")&&_vm._k($event.keyCode,"enter",13,$event.key,"Enter")?null:function(){return _vm.searchGroup()}.apply(null,arguments)},blur:function(){return!_vm.searchText&&_vm.searchGroup(!0)},input:function($event){$event.target.composing||(_vm.searchText=$event.target.value)}}}),_c("i",{staticClass:"el-icon-circle-close",attrs:{size:"medium"},on:{click:function($event){return _vm.searchGroup(!0)}}})]),_vm._v(" "),_c("div",{directives:[{name:"show",rawName:"v-show",value:_vm.showSearchIcon,expression:"showSearchIcon"}],staticClass:"tgroup-flex-spacebetween width25"},[_c("fc-icon",{attrs:{group:"action",size:16,name:"search"},on:{click:function($event){return _vm.toggleSearchIcon()}}}),_vm._v(" "),_c("div",{staticClass:"seperate-grp-mod"}),_vm._v(" "),_c("sort",{attrs:{moduleName:_vm.moduleName,skipOrderBy:!0,sortByField:_vm.sortByField},on:{onSortChange:function(sortObj){return _vm.loadGroupingData(sortObj)}}})],1)])]},proxy:!0},{key:"grp-pagination",fn:function(){return[_c("div",{staticClass:"pagination-wrapper"},[_vm.hasGrouplist?_c("GroupPagination",{staticClass:"grp-pagination",attrs:{hasMoreList:_vm.hasMoreList,currentPage:_vm.page,perPage:_vm.perPage,currentCount:_vm.currentGrpCount},on:{"update:currentPage":function($event){_vm.page=$event},"update:current-page":function($event){_vm.page=$event}}}):_vm._e()],1)]},proxy:!0}],null,!0)},"SchedulerTable",_vm.$attrs,!1),_vm.$listeners))]]],2)},__vue_staticRenderFns__=[];
/* style */
const __vue_inject_styles__=function(inject){inject&&(inject("data-v-d3772fae_0",{source:".search-wrapper .input{height:27px!important;background:#f3f3f3!important;width:130px!important;float:left!important;top:10px!important;left:-50px!important;position:relative!important}.today-btn{width:80px;height:30px;margin:0 40px;padding:5px 12px;border-radius:4px;font-weight:400}.today-btn span{font-size:14px;letter-spacing:.5px;color:#324056}.today-btn:hover{border-color:#4d95ff;background-color:#fff}.h-100{height:100%}.scheduler-empty-grp-list{display:flex;justify-content:center;align-items:center;height:100%}.scheduler-calender-view{height:30px;width:100px;margin-left:60px}.scheduler-calender-view .el-input__inner{padding:5px;padding-left:9px;border-radius:4px;border:solid 1px #e3eaed;font-size:14px;color:#324056;height:30px;letter-spacing:.5px}.scheduler-calender-view .el-input__suffix{height:30px}.scheduler-calender-view.el-select .el-input.is-focus .el-input__inner,.scheduler-calender-view.el-select:hover .el-input__inner{border-color:#4d95ff}.scheduler-calender-view .el-input__inner:focus,.scheduler-calender-view .el-input__inner:hover{border-color:#4d95ff!important}",map:void 0,media:void 0}),inject("data-v-d3772fae_1",{source:".pagination-wrapper[data-v-d3772fae]{border-top:solid 1px #e5effe}.pagination-wrapper .grp-pagination[data-v-d3772fae]{z-index:1;background:#fff;border-right:solid 1px #e5effe;border-bottom:solid 1px #e5effe;border-left:solid 1px #e5effe;display:flex;padding:15px;overflow:auto}.search-btn-tgroup[data-v-d3772fae]{padding-left:12px;transition:.2s linear;line-height:1.8;margin-bottom:5px;outline:0;border:none;height:32px;width:155px;margin:0 8px 0 0;border-radius:4px;font-size:14px;background-color:#f3f3f3;justify-content:center!important}.search-sort-group[data-v-d3772fae]{display:flex;justify-content:space-between;align-items:center;width:100%}.search-sort-group .seperate-grp-mod[data-v-d3772fae]{border-right:1px solid #ccc;margin-right:8px;margin-left:8px;padding-top:14px}.search-sort-group .width80[data-v-d3772fae]{width:80%}.search-sort-group .wdith25[data-v-d3772fae]{width:25%}.search-sort-group .tgroup-flex-spacebetween[data-v-d3772fae]{display:flex;justify-content:space-between;align-items:baseline}.search-sort-group .vertical-seperator[data-v-d3772fae]{margin:10px 0;border-right:1px solid #f1f1f1}",map:void 0,media:void 0}))},__vue_scope_id__="data-v-d3772fae",__vue_module_identifier__=void 0,__vue_is_functional_template__=!1,__vue_component__=normalizeComponent({render:__vue_render__,staticRenderFns:__vue_staticRenderFns__},__vue_inject_styles__,__vue_script__,__vue_scope_id__,__vue_is_functional_template__,__vue_module_identifier__,!1,createInjector,void 0,void 0),SchedulerComponent=__vue_component__;
/* scoped */}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/55766.js.map