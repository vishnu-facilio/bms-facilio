"use strict";(self["webpackChunkfacilio_web"]=self["webpackChunkfacilio_web"]||[]).push([[94255],{
/***/74741:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Wi:function(){/* binding */return ActionRunner},
/* harmony export */Z0:function(){/* binding */return Separator},
/* harmony export */aU:function(){/* binding */return Action},
/* harmony export */eZ:function(){/* binding */return EmptySubmenuAction},
/* harmony export */wY:function(){/* binding */return SubmenuAction}
/* harmony export */});
/* harmony import */var _event_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(104669),_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(905976),_nls_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(663580),__awaiter=function(thisArg,_arguments,P,generator){function adopt(value){return value instanceof P?value:new P((function(resolve){resolve(value)}))}return new(P||(P=Promise))((function(resolve,reject){function fulfilled(value){try{step(generator.next(value))}catch(e){reject(e)}}function rejected(value){try{step(generator["throw"](value))}catch(e){reject(e)}}function step(result){result.done?resolve(result.value):adopt(result.value).then(fulfilled,rejected)}step((generator=generator.apply(thisArg,_arguments||[])).next())}))};
/* harmony import */class Action extends _lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .Disposable */.JT{constructor(id,label="",cssClass="",enabled=!0,actionCallback){super(),this._onDidChange=this._register(new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5),this.onDidChange=this._onDidChange.event,this._enabled=!0,this._id=id,this._label=label,this._cssClass=cssClass,this._enabled=enabled,this._actionCallback=actionCallback}get id(){return this._id}get label(){return this._label}set label(value){this._setLabel(value)}_setLabel(value){this._label!==value&&(this._label=value,this._onDidChange.fire({label:value}))}get tooltip(){return this._tooltip||""}set tooltip(value){this._setTooltip(value)}_setTooltip(value){this._tooltip!==value&&(this._tooltip=value,this._onDidChange.fire({tooltip:value}))}get class(){return this._cssClass}set class(value){this._setClass(value)}_setClass(value){this._cssClass!==value&&(this._cssClass=value,this._onDidChange.fire({class:value}))}get enabled(){return this._enabled}set enabled(value){this._setEnabled(value)}_setEnabled(value){this._enabled!==value&&(this._enabled=value,this._onDidChange.fire({enabled:value}))}get checked(){return this._checked}set checked(value){this._setChecked(value)}_setChecked(value){this._checked!==value&&(this._checked=value,this._onDidChange.fire({checked:value}))}run(event,data){return __awaiter(this,void 0,void 0,(function*(){this._actionCallback&&(yield this._actionCallback(event))}))}}class ActionRunner extends _lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .Disposable */.JT{constructor(){super(...arguments),this._onBeforeRun=this._register(new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5),this.onBeforeRun=this._onBeforeRun.event,this._onDidRun=this._register(new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5),this.onDidRun=this._onDidRun.event}run(action,context){return __awaiter(this,void 0,void 0,(function*(){if(!action.enabled)return;let error;this._onBeforeRun.fire({action:action});try{yield this.runAction(action,context)}catch(e){error=e}this._onDidRun.fire({action:action,error:error})}))}runAction(action,context){return __awaiter(this,void 0,void 0,(function*(){yield action.run(context)}))}}class Separator extends Action{constructor(label){super(Separator.ID,label,label?"separator text":"separator"),this.checked=!1,this.enabled=!1}}Separator.ID="vs.actions.separator";class SubmenuAction{constructor(id,label,actions,cssClass){this.tooltip="",this.enabled=!0,this.checked=!1,this.id=id,this.label=label,this.class=cssClass,this._actions=actions}get actions(){return this._actions}dispose(){
// there is NOTHING to dispose and the SubmenuAction should
// never have anything to dispose as it is a convenience type
// to bridge into the rendering world.
}run(){return __awaiter(this,void 0,void 0,(function*(){}))}}class EmptySubmenuAction extends Action{constructor(){super(EmptySubmenuAction.ID,_nls_js__WEBPACK_IMPORTED_MODULE_2__/* .localize */.N("submenu.empty","(empty)"),void 0,!1)}}EmptySubmenuAction.ID="vs.actions.empty"},
/***/609488:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* unused harmony exports lastIndex, insertInto */
/**
 * Returns the last element of an array.
 * @param array The array.
 * @param n Which element from the end (default is zero).
 */
function tail(array,n=0){return array[array.length-(1+n)]}function tail2(arr){if(0===arr.length)throw new Error("Invalid tail call");return[arr.slice(0,arr.length-1),arr[arr.length-1]]}function equals(one,other,itemEquals=((a,b)=>a===b)){if(one===other)return!0;if(!one||!other)return!1;if(one.length!==other.length)return!1;for(let i=0,len=one.length;i<len;i++)if(!itemEquals(one[i],other[i]))return!1;return!0}function binarySearch(array,key,comparator){let low=0,high=array.length-1;while(low<=high){const mid=(low+high)/2|0,comp=comparator(array[mid],key);if(comp<0)low=mid+1;else{if(!(comp>0))return mid;high=mid-1}}return-(low+1)}
/**
 * Takes a sorted array and a function p. The array is sorted in such a way that all elements where p(x) is false
 * are located before all elements where p(x) is true.
 * @returns the least x for which p(x) is true or array.length if no element fullfills the given function.
 */function findFirstInSorted(array,p){let low=0,high=array.length;if(0===high)return 0;// no children
while(low<high){const mid=Math.floor((low+high)/2);p(array[mid])?high=mid:low=mid+1}return low}function quickSelect(nth,data,compare){if(nth|=0,nth>=data.length)throw new TypeError("invalid index");let pivotValue=data[Math.floor(data.length*Math.random())],lower=[],higher=[],pivots=[];for(let value of data){const val=compare(value,pivotValue);val<0?lower.push(value):val>0?higher.push(value):pivots.push(value)}return nth<lower.length?quickSelect(nth,lower,compare):nth<lower.length+pivots.length?pivots[0]:quickSelect(nth-(lower.length+pivots.length),higher,compare)}function groupBy(data,compare){const result=[];let currentGroup;for(const element of data.slice(0).sort(compare))currentGroup&&0===compare(currentGroup[0],element)?currentGroup.push(element):(currentGroup=[element],result.push(currentGroup));return result}
/**
 * @returns New array with all falsy values removed. The original array IS NOT modified.
 */function coalesce(array){return array.filter((e=>!!e))}
/**
 * @returns false if the provided object is an array and not empty.
 */function isFalsyOrEmpty(obj){return!Array.isArray(obj)||0===obj.length}function isNonEmptyArray(obj){return Array.isArray(obj)&&obj.length>0}
/**
 * Removes duplicates from the given array. The optional keyFn allows to specify
 * how elements are checked for equality by returning an alternate value for each.
 */function distinct(array,keyFn=(value=>value)){const seen=new Set;return array.filter((element=>{const key=keyFn(element);return!seen.has(key)&&(seen.add(key),!0)}))}function findLast(arr,predicate){const idx=lastIndex(arr,predicate);if(-1!==idx)return arr[idx]}function lastIndex(array,fn){for(let i=array.length-1;i>=0;i--){const element=array[i];if(fn(element))return i}return-1}function firstOrDefault(array,notFoundValue){return array.length>0?array[0]:notFoundValue}function flatten(arr){return[].concat(...arr)}function range(arg,to){let from="number"===typeof to?arg:0;"number"===typeof to?from=arg:(from=0,to=arg);const result=[];if(from<=to)for(let i=from;i<to;i++)result.push(i);else for(let i=from;i>to;i--)result.push(i);return result}
/**
 * Insert `insertArr` inside `target` at `insertIndex`.
 * Please don't touch unless you understand https://jsperf.com/inserting-an-array-within-an-array
 */function arrayInsert(target,insertIndex,insertArr){const before=target.slice(0,insertIndex),after=target.slice(insertIndex);return before.concat(insertArr,after)}
/**
 * Pushes an element to the start of the array, if found.
 */function pushToStart(arr,value){const index=arr.indexOf(value);index>-1&&(arr.splice(index,1),arr.unshift(value))}
/**
 * Pushes an element to the end of the array, if found.
 */function pushToEnd(arr,value){const index=arr.indexOf(value);index>-1&&(arr.splice(index,1),arr.push(value))}function asArray(x){return Array.isArray(x)?x:[x]}
/**
 * Insert the new items in the array.
 * @param array The original array.
 * @param start The zero-based location in the array from which to start inserting elements.
 * @param newItems The items to be inserted
 */function insertInto(array,start,newItems){const startIdx=getActualStartIndex(array,start),originalLength=array.length,newItemsLength=newItems.length;array.length=originalLength+newItemsLength;
// Move the items after the start index, start from the end so that we don't overwrite any value.
for(let i=originalLength-1;i>=startIdx;i--)array[i+newItemsLength]=array[i];for(let i=0;i<newItemsLength;i++)array[i+startIdx]=newItems[i]}
/**
 * Removes elements from an array and inserts new elements in their place, returning the deleted elements. Alternative to the native Array.splice method, it
 * can only support limited number of items due to the maximum call stack size limit.
 * @param array The original array.
 * @param start The zero-based location in the array from which to start removing elements.
 * @param deleteCount The number of elements to remove.
 * @returns An array containing the elements that were deleted.
 */function splice(array,start,deleteCount,newItems){const index=getActualStartIndex(array,start),result=array.splice(index,deleteCount);return insertInto(array,index,newItems),result}
/**
 * Determine the actual start index (same logic as the native splice() or slice())
 * If greater than the length of the array, start will be set to the length of the array. In this case, no element will be deleted but the method will behave as an adding function, adding as many element as item[n*] provided.
 * If negative, it will begin that many elements from the end of the array. (In this case, the origin -1, meaning -n is the index of the nth last element, and is therefore equivalent to the index of array.length - n.) If array.length + start is less than 0, it will begin from index 0.
 * @param array The target array.
 * @param start The operation index.
 */function getActualStartIndex(array,start){return start<0?Math.max(start+array.length,0):Math.min(start,array.length)}
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */EB:function(){/* binding */return distinct},
/* harmony export */Gb:function(){/* binding */return tail},
/* harmony export */H9:function(){/* binding */return ArrayQueue},
/* harmony export */HW:function(){/* binding */return quickSelect},
/* harmony export */JH:function(){/* binding */return tail2},
/* harmony export */Of:function(){/* binding */return isNonEmptyArray},
/* harmony export */XY:function(){/* binding */return isFalsyOrEmpty},
/* harmony export */Xh:function(){/* binding */return firstOrDefault},
/* harmony export */Zv:function(){/* binding */return arrayInsert},
/* harmony export */_2:function(){/* binding */return asArray},
/* harmony export */al:function(){/* binding */return pushToEnd},
/* harmony export */dF:function(){/* binding */return findLast},
/* harmony export */db:function(){/* binding */return splice},
/* harmony export */fS:function(){/* binding */return equals},
/* harmony export */kX:function(){/* binding */return coalesce},
/* harmony export */lG:function(){/* binding */return findFirstInSorted},
/* harmony export */ry:function(){/* binding */return binarySearch},
/* harmony export */vM:function(){/* binding */return groupBy},
/* harmony export */w6:function(){/* binding */return range},
/* harmony export */xH:function(){/* binding */return flatten},
/* harmony export */zI:function(){/* binding */return pushToStart}
/* harmony export */});class ArrayQueue{
/**
     * Constructs a queue that is backed by the given array. Runtime is O(1).
    */
constructor(items){this.items=items,this.firstIdx=0,this.lastIdx=this.items.length-1}
/**
     * Consumes elements from the beginning of the queue as long as the predicate returns true.
     * If no elements were consumed, `null` is returned. Has a runtime of O(result.length).
    */takeWhile(predicate){
// P(k) := k <= this.lastIdx && predicate(this.items[k])
// Find s := min { k | k >= this.firstIdx && !P(k) } and return this.data[this.firstIdx...s)
let startIdx=this.firstIdx;while(startIdx<this.items.length&&predicate(this.items[startIdx]))startIdx++;const result=startIdx===this.firstIdx?null:this.items.slice(this.firstIdx,startIdx);return this.firstIdx=startIdx,result}
/**
     * Consumes elements from the end of the queue as long as the predicate returns true.
     * If no elements were consumed, `null` is returned.
     * The result has the same order as the underlying array!
    */takeFromEndWhile(predicate){
// P(k) := this.firstIdx >= k && predicate(this.items[k])
// Find s := max { k | k <= this.lastIdx && !P(k) } and return this.data(s...this.lastIdx]
let endIdx=this.lastIdx;while(endIdx>=0&&predicate(this.items[endIdx]))endIdx--;const result=endIdx===this.lastIdx?null:this.items.slice(endIdx+1,this.lastIdx+1);return this.lastIdx=endIdx,result}peek(){return this.items[this.firstIdx]}}
/***/},
/***/935146:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/**
 * Throws an error with the provided message if the provided value does not evaluate to a true Javascript value.
 */
function ok(value,message){if(!value)throw new Error(message?`Assertion failed (${message})`:"Assertion Failed")}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */ok:function(){/* binding */return ok}
/* harmony export */})},
/***/715393:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */J8:function(){/* binding */return isThenable},
/* harmony export */PG:function(){/* binding */return createCancelablePromise},
/* harmony export */Ps:function(){/* binding */return first},
/* harmony export */To:function(){/* binding */return runWhenIdle},
/* harmony export */Ue:function(){/* binding */return IdleValue},
/* harmony export */Vg:function(){/* binding */return disposableTimeout},
/* harmony export */Vs:function(){/* binding */return timeout},
/* harmony export */_F:function(){/* binding */return TimeoutTimer},
/* harmony export */eP:function(){/* binding */return raceCancellation},
/* harmony export */jT:function(){/* binding */return Promises},
/* harmony export */pY:function(){/* binding */return RunOnceScheduler},
/* harmony export */rH:function(){/* binding */return ThrottledDelayer},
/* harmony export */vp:function(){/* binding */return Delayer},
/* harmony export */zh:function(){/* binding */return IntervalTimer}
/* harmony export */});
/* unused harmony export Throttler */
/* harmony import */var _cancellation_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(471050),_errors_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(817301),_lifecycle_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(905976),__awaiter=function(thisArg,_arguments,P,generator){function adopt(value){return value instanceof P?value:new P((function(resolve){resolve(value)}))}return new(P||(P=Promise))((function(resolve,reject){function fulfilled(value){try{step(generator.next(value))}catch(e){reject(e)}}function rejected(value){try{step(generator["throw"](value))}catch(e){reject(e)}}function step(result){result.done?resolve(result.value):adopt(result.value).then(fulfilled,rejected)}step((generator=generator.apply(thisArg,_arguments||[])).next())}))};
/* harmony import */function isThenable(obj){return!!obj&&"function"===typeof obj.then}function createCancelablePromise(callback){const source=new _cancellation_js__WEBPACK_IMPORTED_MODULE_0__/* .CancellationTokenSource */.A,thenable=callback(source.token),promise=new Promise(((resolve,reject)=>{const subscription=source.token.onCancellationRequested((()=>{subscription.dispose(),source.dispose(),reject((0,_errors_js__WEBPACK_IMPORTED_MODULE_1__/* .canceled */.F0)())}));Promise.resolve(thenable).then((value=>{subscription.dispose(),source.dispose(),resolve(value)}),(err=>{subscription.dispose(),source.dispose(),reject(err)}))}));return new class{cancel(){source.cancel()}then(resolve,reject){return promise.then(resolve,reject)}catch(reject){return this.then(void 0,reject)}finally(onfinally){return promise.finally(onfinally)}}}function raceCancellation(promise,token,defaultValue){return Promise.race([promise,new Promise((resolve=>token.onCancellationRequested((()=>resolve(defaultValue)))))])}
/**
 * A helper to prevent accumulation of sequential async tasks.
 *
 * Imagine a mail man with the sole task of delivering letters. As soon as
 * a letter submitted for delivery, he drives to the destination, delivers it
 * and returns to his base. Imagine that during the trip, N more letters were submitted.
 * When the mail man returns, he picks those N letters and delivers them all in a
 * single trip. Even though N+1 submissions occurred, only 2 deliveries were made.
 *
 * The throttler implements this via the queue() method, by providing it a task
 * factory. Following the example:
 *
 * 		const throttler = new Throttler();
 * 		const letters = [];
 *
 * 		function deliver() {
 * 			const lettersToDeliver = letters;
 * 			letters = [];
 * 			return makeTheTrip(lettersToDeliver);
 * 		}
 *
 * 		function onLetterReceived(l) {
 * 			letters.push(l);
 * 			throttler.queue(deliver);
 * 		}
 */class Throttler{constructor(){this.activePromise=null,this.queuedPromise=null,this.queuedPromiseFactory=null}queue(promiseFactory){if(this.activePromise){if(this.queuedPromiseFactory=promiseFactory,!this.queuedPromise){const onComplete=()=>{this.queuedPromise=null;const result=this.queue(this.queuedPromiseFactory);return this.queuedPromiseFactory=null,result};this.queuedPromise=new Promise((resolve=>{this.activePromise.then(onComplete,onComplete).then(resolve)}))}return new Promise(((resolve,reject)=>{this.queuedPromise.then(resolve,reject)}))}return this.activePromise=promiseFactory(),new Promise(((resolve,reject)=>{this.activePromise.then((result=>{this.activePromise=null,resolve(result)}),(err=>{this.activePromise=null,reject(err)}))}))}}
/**
 * A helper to delay (debounce) execution of a task that is being requested often.
 *
 * Following the throttler, now imagine the mail man wants to optimize the number of
 * trips proactively. The trip itself can be long, so he decides not to make the trip
 * as soon as a letter is submitted. Instead he waits a while, in case more
 * letters are submitted. After said waiting period, if no letters were submitted, he
 * decides to make the trip. Imagine that N more letters were submitted after the first
 * one, all within a short period of time between each other. Even though N+1
 * submissions occurred, only 1 delivery was made.
 *
 * The delayer offers this behavior via the trigger() method, into which both the task
 * to be executed and the waiting period (delay) must be passed in as arguments. Following
 * the example:
 *
 * 		const delayer = new Delayer(WAITING_PERIOD);
 * 		const letters = [];
 *
 * 		function letterReceived(l) {
 * 			letters.push(l);
 * 			delayer.trigger(() => { return makeTheTrip(); });
 * 		}
 */class Delayer{constructor(defaultDelay){this.defaultDelay=defaultDelay,this.timeout=null,this.completionPromise=null,this.doResolve=null,this.doReject=null,this.task=null}trigger(task,delay=this.defaultDelay){return this.task=task,this.cancelTimeout(),this.completionPromise||(this.completionPromise=new Promise(((resolve,reject)=>{this.doResolve=resolve,this.doReject=reject})).then((()=>{if(this.completionPromise=null,this.doResolve=null,this.task){const task=this.task;return this.task=null,task()}}))),this.timeout=setTimeout((()=>{this.timeout=null,this.doResolve&&this.doResolve(null)}),delay),this.completionPromise}isTriggered(){return null!==this.timeout}cancel(){this.cancelTimeout(),this.completionPromise&&(this.doReject&&this.doReject((0,_errors_js__WEBPACK_IMPORTED_MODULE_1__/* .canceled */.F0)()),this.completionPromise=null)}cancelTimeout(){null!==this.timeout&&(clearTimeout(this.timeout),this.timeout=null)}dispose(){this.cancel()}}
/**
 * A helper to delay execution of a task that is being requested often, while
 * preventing accumulation of consecutive executions, while the task runs.
 *
 * The mail man is clever and waits for a certain amount of time, before going
 * out to deliver letters. While the mail man is going out, more letters arrive
 * and can only be delivered once he is back. Once he is back the mail man will
 * do one more trip to deliver the letters that have accumulated while he was out.
 */class ThrottledDelayer{constructor(defaultDelay){this.delayer=new Delayer(defaultDelay),this.throttler=new Throttler}trigger(promiseFactory,delay){return this.delayer.trigger((()=>this.throttler.queue(promiseFactory)),delay)}dispose(){this.delayer.dispose()}}function timeout(millis,token){return token?new Promise(((resolve,reject)=>{const handle=setTimeout((()=>{disposable.dispose(),resolve()}),millis),disposable=token.onCancellationRequested((()=>{clearTimeout(handle),disposable.dispose(),reject((0,_errors_js__WEBPACK_IMPORTED_MODULE_1__/* .canceled */.F0)())}))})):createCancelablePromise((token=>timeout(millis,token)))}function disposableTimeout(handler,timeout=0){const timer=setTimeout(handler,timeout);return(0,_lifecycle_js__WEBPACK_IMPORTED_MODULE_2__/* .toDisposable */.OF)((()=>clearTimeout(timer)))}function first(promiseFactories,shouldStop=(t=>!!t),defaultValue=null){let index=0;const len=promiseFactories.length,loop=()=>{if(index>=len)return Promise.resolve(defaultValue);const factory=promiseFactories[index++],promise=Promise.resolve(factory());return promise.then((result=>shouldStop(result)?Promise.resolve(result):loop()))};return loop()}class TimeoutTimer{constructor(runner,timeout){this._token=-1,"function"===typeof runner&&"number"===typeof timeout&&this.setIfNotSet(runner,timeout)}dispose(){this.cancel()}cancel(){-1!==this._token&&(clearTimeout(this._token),this._token=-1)}cancelAndSet(runner,timeout){this.cancel(),this._token=setTimeout((()=>{this._token=-1,runner()}),timeout)}setIfNotSet(runner,timeout){-1===this._token&&(this._token=setTimeout((()=>{this._token=-1,runner()}),timeout))}}class IntervalTimer{constructor(){this._token=-1}dispose(){this.cancel()}cancel(){-1!==this._token&&(clearInterval(this._token),this._token=-1)}cancelAndSet(runner,interval){this.cancel(),this._token=setInterval((()=>{runner()}),interval)}}class RunOnceScheduler{constructor(runner,delay){this.timeoutToken=-1,this.runner=runner,this.timeout=delay,this.timeoutHandler=this.onTimeout.bind(this)}
/**
     * Dispose RunOnceScheduler
     */dispose(){this.cancel(),this.runner=null}
/**
     * Cancel current scheduled runner (if any).
     */cancel(){this.isScheduled()&&(clearTimeout(this.timeoutToken),this.timeoutToken=-1)}
/**
     * Cancel previous runner (if any) & schedule a new runner.
     */schedule(delay=this.timeout){this.cancel(),this.timeoutToken=setTimeout(this.timeoutHandler,delay)}get delay(){return this.timeout}set delay(value){this.timeout=value}
/**
     * Returns true if scheduled.
     */isScheduled(){return-1!==this.timeoutToken}onTimeout(){this.timeoutToken=-1,this.runner&&this.doRun()}doRun(){this.runner&&this.runner()}}
/**
 * Execute the callback the next time the browser is idle
 */let runWhenIdle;(function(){runWhenIdle="function"!==typeof requestIdleCallback||"function"!==typeof cancelIdleCallback?runner=>{const handle=setTimeout((()=>{const end=Date.now()+15;// one frame at 64fps
runner(Object.freeze({didTimeout:!0,timeRemaining(){return Math.max(0,end-Date.now())}}))}));let disposed=!1;return{dispose(){disposed||(disposed=!0,clearTimeout(handle))}}}:(runner,timeout)=>{const handle=requestIdleCallback(runner,"number"===typeof timeout?{timeout:timeout}:void 0);let disposed=!1;return{dispose(){disposed||(disposed=!0,cancelIdleCallback(handle))}}}})();
/**
 * An implementation of the "idle-until-urgent"-strategy as introduced
 * here: https://philipwalton.com/articles/idle-until-urgent/
 */
class IdleValue{constructor(executor){this._didRun=!1,this._executor=()=>{try{this._value=executor()}catch(err){this._error=err}finally{this._didRun=!0}},this._handle=runWhenIdle((()=>this._executor()))}dispose(){this._handle.dispose()}get value(){if(this._didRun||(this._handle.dispose(),this._executor()),this._error)throw this._error;return this._value}get isInitialized(){return this._didRun}}
//#endregion
//#region Promises
var Promises;(function(Promises){
/**
     * A drop-in replacement for `Promise.all` with the only difference
     * that the method awaits every promise to either fulfill or reject.
     *
     * Similar to `Promise.all`, only the first error will be returned
     * if any.
     */
function settled(promises){return __awaiter(this,void 0,void 0,(function*(){let firstError;const result=yield Promise.all(promises.map((promise=>promise.then((value=>value),(error=>{firstError||(firstError=error)}// do not rethrow so that other promises can settle
)))));if("undefined"!==typeof firstError)throw firstError;return result;// cast is needed and protected by the `throw` above
}))}
/**
     * A helper to create a new `Promise<T>` with a body that is a promise
     * itself. By default, an error that raises from the async body will
     * end up as a unhandled rejection, so this utility properly awaits the
     * body and rejects the promise as a normal promise does without async
     * body.
     *
     * This method should only be used in rare cases where otherwise `async`
     * cannot be used (e.g. when callbacks are involved that require this).
     */
function withAsyncBody(bodyFn){
// eslint-disable-next-line no-async-promise-executor
return new Promise(((resolve,reject)=>__awaiter(this,void 0,void 0,(function*(){try{yield bodyFn(resolve,reject)}catch(error){reject(error)}}))))}Promises.settled=settled,Promises.withAsyncBody=withAsyncBody})(Promises||(Promises={}))},
/***/153060:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Ag:function(){/* binding */return readUInt32BE},
/* harmony export */Cg:function(){/* binding */return writeUInt8},
/* harmony export */KN:function(){/* binding */return VSBuffer},
/* harmony export */Q$:function(){/* binding */return readUInt8},
/* harmony export */T4:function(){/* binding */return writeUInt32BE},
/* harmony export */mP:function(){/* binding */return readUInt16LE},
/* harmony export */oq:function(){/* binding */return writeUInt16LE}
/* harmony export */});const hasBuffer="undefined"!==typeof Buffer;let textDecoder;class VSBuffer{constructor(buffer){this.buffer=buffer,this.byteLength=this.buffer.byteLength}static wrap(actual){return hasBuffer&&!Buffer.isBuffer(actual)&&(
// https://nodejs.org/dist/latest-v10.x/docs/api/buffer.html#buffer_class_method_buffer_from_arraybuffer_byteoffset_length
// Create a zero-copy Buffer wrapper around the ArrayBuffer pointed to by the Uint8Array
actual=Buffer.from(actual.buffer,actual.byteOffset,actual.byteLength)),new VSBuffer(actual)}toString(){return hasBuffer?this.buffer.toString():(textDecoder||(textDecoder=new TextDecoder),textDecoder.decode(this.buffer))}}function readUInt16LE(source,offset){return source[offset+0]<<0>>>0|source[offset+1]<<8>>>0}function writeUInt16LE(destination,value,offset){destination[offset+0]=255&value,value>>>=8,destination[offset+1]=255&value}function readUInt32BE(source,offset){return source[offset]*Math.pow(2,24)+source[offset+1]*Math.pow(2,16)+source[offset+2]*Math.pow(2,8)+source[offset+3]}function writeUInt32BE(destination,value,offset){destination[offset+3]=value,value>>>=8,destination[offset+2]=value,value>>>=8,destination[offset+1]=value,value>>>=8,destination[offset]=value}function readUInt8(source,offset){return source[offset]}function writeUInt8(destination,value,offset){destination[offset]=value}
/***/},
/***/471050:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */A:function(){/* binding */return CancellationTokenSource},
/* harmony export */T:function(){/* binding */return CancellationToken}
/* harmony export */});
/* harmony import */var _event_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(104669);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/const shortcutEvent=Object.freeze((function(callback,context){const handle=setTimeout(callback.bind(context),0);return{dispose(){clearTimeout(handle)}}}));var CancellationToken;(function(CancellationToken){function isCancellationToken(thing){return thing===CancellationToken.None||thing===CancellationToken.Cancelled||(thing instanceof MutableToken||!(!thing||"object"!==typeof thing)&&("boolean"===typeof thing.isCancellationRequested&&"function"===typeof thing.onCancellationRequested))}CancellationToken.isCancellationToken=isCancellationToken,CancellationToken.None=Object.freeze({isCancellationRequested:!1,onCancellationRequested:_event_js__WEBPACK_IMPORTED_MODULE_0__/* .Event */.ju.None}),CancellationToken.Cancelled=Object.freeze({isCancellationRequested:!0,onCancellationRequested:shortcutEvent})})(CancellationToken||(CancellationToken={}));class MutableToken{constructor(){this._isCancelled=!1,this._emitter=null}cancel(){this._isCancelled||(this._isCancelled=!0,this._emitter&&(this._emitter.fire(void 0),this.dispose()))}get isCancellationRequested(){return this._isCancelled}get onCancellationRequested(){return this._isCancelled?shortcutEvent:(this._emitter||(this._emitter=new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5),this._emitter.event)}dispose(){this._emitter&&(this._emitter.dispose(),this._emitter=null)}}class CancellationTokenSource{constructor(parent){this._token=void 0,this._parentListener=void 0,this._parentListener=parent&&parent.onCancellationRequested(this.cancel,this)}get token(){return this._token||(
// be lazy and create the token only when
// actually needed
this._token=new MutableToken),this._token}cancel(){this._token?this._token instanceof MutableToken&&
// actually cancel
this._token.cancel():
// save an object by returning the default
// cancelled token when cancellation happens
// before someone asks for the token
this._token=CancellationToken.Cancelled}dispose(cancel=!1){cancel&&this.cancel(),this._parentListener&&this._parentListener.dispose(),this._token?this._token instanceof MutableToken&&
// actually dispose
this._token.dispose():
// ensure to initialize with an empty token if we had none
this._token=CancellationToken.None}}
/***/},
/***/773046:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */CM:function(){/* binding */return registerCodicon},
/* harmony export */JL:function(){/* binding */return getCodiconAriaLabel},
/* harmony export */dT:function(){/* binding */return CSSIcon},
/* harmony export */fK:function(){/* binding */return iconRegistry},
/* harmony export */lA:function(){/* binding */return Codicon}
/* harmony export */});
/* harmony import */var _event_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(104669);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/class Registry{constructor(){this._icons=new Map,this._onDidRegister=new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5}add(icon){const existing=this._icons.get(icon.id);existing?icon.description&&(existing.description=icon.description):(this._icons.set(icon.id,icon),this._onDidRegister.fire(icon))}get(id){return this._icons.get(id)}get all(){return this._icons.values()}get onDidRegister(){return this._onDidRegister.event}}const _registry=new Registry,iconRegistry=_registry;function registerCodicon(id,def){return new Codicon(id,def)}
// Selects all codicon names encapsulated in the `$()` syntax and wraps the
// results with spaces so that screen readers can read the text better.
function getCodiconAriaLabel(text){return text?text.replace(/\$\((.*?)\)/g,((_match,codiconName)=>` ${codiconName} `)).trim():""}class Codicon{constructor(id,definition,description){this.id=id,this.definition=definition,this.description=description,_registry.add(this)}get classNames(){return"codicon codicon-"+this.id}
// classNamesArray is useful for migrating to ES6 classlist
get classNamesArray(){return["codicon","codicon-"+this.id]}get cssSelector(){return".codicon.codicon-"+this.id}}var CSSIcon;(function(CSSIcon){CSSIcon.iconNameSegment="[A-Za-z0-9]+",CSSIcon.iconNameExpression="[A-Za-z0-9\\-]+",CSSIcon.iconModifierExpression="~[A-Za-z]+";const cssIconIdRegex=new RegExp(`^(${CSSIcon.iconNameExpression})(${CSSIcon.iconModifierExpression})?$`);function asClassNameArray(icon){if(icon instanceof Codicon)return["codicon","codicon-"+icon.id];const match=cssIconIdRegex.exec(icon.id);if(!match)return asClassNameArray(Codicon.error);let[,id,modifier]=match;const classNames=["codicon","codicon-"+id];return modifier&&classNames.push("codicon-modifier-"+modifier.substr(1)),classNames}function asClassName(icon){return asClassNameArray(icon).join(" ")}function asCSSSelector(icon){return"."+asClassNameArray(icon).join(".")}CSSIcon.asClassNameArray=asClassNameArray,CSSIcon.asClassName=asClassName,CSSIcon.asCSSSelector=asCSSSelector})(CSSIcon||(CSSIcon={})),function(Codicon){
// built-in icons, with image name
Codicon.add=new Codicon("add",{fontCharacter:"\\ea60"}),Codicon.plus=new Codicon("plus",Codicon.add.definition),Codicon.gistNew=new Codicon("gist-new",Codicon.add.definition),Codicon.repoCreate=new Codicon("repo-create",Codicon.add.definition),Codicon.lightbulb=new Codicon("lightbulb",{fontCharacter:"\\ea61"}),Codicon.lightBulb=new Codicon("light-bulb",{fontCharacter:"\\ea61"}),Codicon.repo=new Codicon("repo",{fontCharacter:"\\ea62"}),Codicon.repoDelete=new Codicon("repo-delete",{fontCharacter:"\\ea62"}),Codicon.gistFork=new Codicon("gist-fork",{fontCharacter:"\\ea63"}),Codicon.repoForked=new Codicon("repo-forked",{fontCharacter:"\\ea63"}),Codicon.gitPullRequest=new Codicon("git-pull-request",{fontCharacter:"\\ea64"}),Codicon.gitPullRequestAbandoned=new Codicon("git-pull-request-abandoned",{fontCharacter:"\\ea64"}),Codicon.recordKeys=new Codicon("record-keys",{fontCharacter:"\\ea65"}),Codicon.keyboard=new Codicon("keyboard",{fontCharacter:"\\ea65"}),Codicon.tag=new Codicon("tag",{fontCharacter:"\\ea66"}),Codicon.tagAdd=new Codicon("tag-add",{fontCharacter:"\\ea66"}),Codicon.tagRemove=new Codicon("tag-remove",{fontCharacter:"\\ea66"}),Codicon.person=new Codicon("person",{fontCharacter:"\\ea67"}),Codicon.personFollow=new Codicon("person-follow",{fontCharacter:"\\ea67"}),Codicon.personOutline=new Codicon("person-outline",{fontCharacter:"\\ea67"}),Codicon.personFilled=new Codicon("person-filled",{fontCharacter:"\\ea67"}),Codicon.gitBranch=new Codicon("git-branch",{fontCharacter:"\\ea68"}),Codicon.gitBranchCreate=new Codicon("git-branch-create",{fontCharacter:"\\ea68"}),Codicon.gitBranchDelete=new Codicon("git-branch-delete",{fontCharacter:"\\ea68"}),Codicon.sourceControl=new Codicon("source-control",{fontCharacter:"\\ea68"}),Codicon.mirror=new Codicon("mirror",{fontCharacter:"\\ea69"}),Codicon.mirrorPublic=new Codicon("mirror-public",{fontCharacter:"\\ea69"}),Codicon.star=new Codicon("star",{fontCharacter:"\\ea6a"}),Codicon.starAdd=new Codicon("star-add",{fontCharacter:"\\ea6a"}),Codicon.starDelete=new Codicon("star-delete",{fontCharacter:"\\ea6a"}),Codicon.starEmpty=new Codicon("star-empty",{fontCharacter:"\\ea6a"}),Codicon.comment=new Codicon("comment",{fontCharacter:"\\ea6b"}),Codicon.commentAdd=new Codicon("comment-add",{fontCharacter:"\\ea6b"}),Codicon.alert=new Codicon("alert",{fontCharacter:"\\ea6c"}),Codicon.warning=new Codicon("warning",{fontCharacter:"\\ea6c"}),Codicon.search=new Codicon("search",{fontCharacter:"\\ea6d"}),Codicon.searchSave=new Codicon("search-save",{fontCharacter:"\\ea6d"}),Codicon.logOut=new Codicon("log-out",{fontCharacter:"\\ea6e"}),Codicon.signOut=new Codicon("sign-out",{fontCharacter:"\\ea6e"}),Codicon.logIn=new Codicon("log-in",{fontCharacter:"\\ea6f"}),Codicon.signIn=new Codicon("sign-in",{fontCharacter:"\\ea6f"}),Codicon.eye=new Codicon("eye",{fontCharacter:"\\ea70"}),Codicon.eyeUnwatch=new Codicon("eye-unwatch",{fontCharacter:"\\ea70"}),Codicon.eyeWatch=new Codicon("eye-watch",{fontCharacter:"\\ea70"}),Codicon.circleFilled=new Codicon("circle-filled",{fontCharacter:"\\ea71"}),Codicon.primitiveDot=new Codicon("primitive-dot",{fontCharacter:"\\ea71"}),Codicon.closeDirty=new Codicon("close-dirty",{fontCharacter:"\\ea71"}),Codicon.debugBreakpoint=new Codicon("debug-breakpoint",{fontCharacter:"\\ea71"}),Codicon.debugBreakpointDisabled=new Codicon("debug-breakpoint-disabled",{fontCharacter:"\\ea71"}),Codicon.debugHint=new Codicon("debug-hint",{fontCharacter:"\\ea71"}),Codicon.primitiveSquare=new Codicon("primitive-square",{fontCharacter:"\\ea72"}),Codicon.edit=new Codicon("edit",{fontCharacter:"\\ea73"}),Codicon.pencil=new Codicon("pencil",{fontCharacter:"\\ea73"}),Codicon.info=new Codicon("info",{fontCharacter:"\\ea74"}),Codicon.issueOpened=new Codicon("issue-opened",{fontCharacter:"\\ea74"}),Codicon.gistPrivate=new Codicon("gist-private",{fontCharacter:"\\ea75"}),Codicon.gitForkPrivate=new Codicon("git-fork-private",{fontCharacter:"\\ea75"}),Codicon.lock=new Codicon("lock",{fontCharacter:"\\ea75"}),Codicon.mirrorPrivate=new Codicon("mirror-private",{fontCharacter:"\\ea75"}),Codicon.close=new Codicon("close",{fontCharacter:"\\ea76"}),Codicon.removeClose=new Codicon("remove-close",{fontCharacter:"\\ea76"}),Codicon.x=new Codicon("x",{fontCharacter:"\\ea76"}),Codicon.repoSync=new Codicon("repo-sync",{fontCharacter:"\\ea77"}),Codicon.sync=new Codicon("sync",{fontCharacter:"\\ea77"}),Codicon.clone=new Codicon("clone",{fontCharacter:"\\ea78"}),Codicon.desktopDownload=new Codicon("desktop-download",{fontCharacter:"\\ea78"}),Codicon.beaker=new Codicon("beaker",{fontCharacter:"\\ea79"}),Codicon.microscope=new Codicon("microscope",{fontCharacter:"\\ea79"}),Codicon.vm=new Codicon("vm",{fontCharacter:"\\ea7a"}),Codicon.deviceDesktop=new Codicon("device-desktop",{fontCharacter:"\\ea7a"}),Codicon.file=new Codicon("file",{fontCharacter:"\\ea7b"}),Codicon.fileText=new Codicon("file-text",{fontCharacter:"\\ea7b"}),Codicon.more=new Codicon("more",{fontCharacter:"\\ea7c"}),Codicon.ellipsis=new Codicon("ellipsis",{fontCharacter:"\\ea7c"}),Codicon.kebabHorizontal=new Codicon("kebab-horizontal",{fontCharacter:"\\ea7c"}),Codicon.mailReply=new Codicon("mail-reply",{fontCharacter:"\\ea7d"}),Codicon.reply=new Codicon("reply",{fontCharacter:"\\ea7d"}),Codicon.organization=new Codicon("organization",{fontCharacter:"\\ea7e"}),Codicon.organizationFilled=new Codicon("organization-filled",{fontCharacter:"\\ea7e"}),Codicon.organizationOutline=new Codicon("organization-outline",{fontCharacter:"\\ea7e"}),Codicon.newFile=new Codicon("new-file",{fontCharacter:"\\ea7f"}),Codicon.fileAdd=new Codicon("file-add",{fontCharacter:"\\ea7f"}),Codicon.newFolder=new Codicon("new-folder",{fontCharacter:"\\ea80"}),Codicon.fileDirectoryCreate=new Codicon("file-directory-create",{fontCharacter:"\\ea80"}),Codicon.trash=new Codicon("trash",{fontCharacter:"\\ea81"}),Codicon.trashcan=new Codicon("trashcan",{fontCharacter:"\\ea81"}),Codicon.history=new Codicon("history",{fontCharacter:"\\ea82"}),Codicon.clock=new Codicon("clock",{fontCharacter:"\\ea82"}),Codicon.folder=new Codicon("folder",{fontCharacter:"\\ea83"}),Codicon.fileDirectory=new Codicon("file-directory",{fontCharacter:"\\ea83"}),Codicon.symbolFolder=new Codicon("symbol-folder",{fontCharacter:"\\ea83"}),Codicon.logoGithub=new Codicon("logo-github",{fontCharacter:"\\ea84"}),Codicon.markGithub=new Codicon("mark-github",{fontCharacter:"\\ea84"}),Codicon.github=new Codicon("github",{fontCharacter:"\\ea84"}),Codicon.terminal=new Codicon("terminal",{fontCharacter:"\\ea85"}),Codicon.console=new Codicon("console",{fontCharacter:"\\ea85"}),Codicon.repl=new Codicon("repl",{fontCharacter:"\\ea85"}),Codicon.zap=new Codicon("zap",{fontCharacter:"\\ea86"}),Codicon.symbolEvent=new Codicon("symbol-event",{fontCharacter:"\\ea86"}),Codicon.error=new Codicon("error",{fontCharacter:"\\ea87"}),Codicon.stop=new Codicon("stop",{fontCharacter:"\\ea87"}),Codicon.variable=new Codicon("variable",{fontCharacter:"\\ea88"}),Codicon.symbolVariable=new Codicon("symbol-variable",{fontCharacter:"\\ea88"}),Codicon.array=new Codicon("array",{fontCharacter:"\\ea8a"}),Codicon.symbolArray=new Codicon("symbol-array",{fontCharacter:"\\ea8a"}),Codicon.symbolModule=new Codicon("symbol-module",{fontCharacter:"\\ea8b"}),Codicon.symbolPackage=new Codicon("symbol-package",{fontCharacter:"\\ea8b"}),Codicon.symbolNamespace=new Codicon("symbol-namespace",{fontCharacter:"\\ea8b"}),Codicon.symbolObject=new Codicon("symbol-object",{fontCharacter:"\\ea8b"}),Codicon.symbolMethod=new Codicon("symbol-method",{fontCharacter:"\\ea8c"}),Codicon.symbolFunction=new Codicon("symbol-function",{fontCharacter:"\\ea8c"}),Codicon.symbolConstructor=new Codicon("symbol-constructor",{fontCharacter:"\\ea8c"}),Codicon.symbolBoolean=new Codicon("symbol-boolean",{fontCharacter:"\\ea8f"}),Codicon.symbolNull=new Codicon("symbol-null",{fontCharacter:"\\ea8f"}),Codicon.symbolNumeric=new Codicon("symbol-numeric",{fontCharacter:"\\ea90"}),Codicon.symbolNumber=new Codicon("symbol-number",{fontCharacter:"\\ea90"}),Codicon.symbolStructure=new Codicon("symbol-structure",{fontCharacter:"\\ea91"}),Codicon.symbolStruct=new Codicon("symbol-struct",{fontCharacter:"\\ea91"}),Codicon.symbolParameter=new Codicon("symbol-parameter",{fontCharacter:"\\ea92"}),Codicon.symbolTypeParameter=new Codicon("symbol-type-parameter",{fontCharacter:"\\ea92"}),Codicon.symbolKey=new Codicon("symbol-key",{fontCharacter:"\\ea93"}),Codicon.symbolText=new Codicon("symbol-text",{fontCharacter:"\\ea93"}),Codicon.symbolReference=new Codicon("symbol-reference",{fontCharacter:"\\ea94"}),Codicon.goToFile=new Codicon("go-to-file",{fontCharacter:"\\ea94"}),Codicon.symbolEnum=new Codicon("symbol-enum",{fontCharacter:"\\ea95"}),Codicon.symbolValue=new Codicon("symbol-value",{fontCharacter:"\\ea95"}),Codicon.symbolRuler=new Codicon("symbol-ruler",{fontCharacter:"\\ea96"}),Codicon.symbolUnit=new Codicon("symbol-unit",{fontCharacter:"\\ea96"}),Codicon.activateBreakpoints=new Codicon("activate-breakpoints",{fontCharacter:"\\ea97"}),Codicon.archive=new Codicon("archive",{fontCharacter:"\\ea98"}),Codicon.arrowBoth=new Codicon("arrow-both",{fontCharacter:"\\ea99"}),Codicon.arrowDown=new Codicon("arrow-down",{fontCharacter:"\\ea9a"}),Codicon.arrowLeft=new Codicon("arrow-left",{fontCharacter:"\\ea9b"}),Codicon.arrowRight=new Codicon("arrow-right",{fontCharacter:"\\ea9c"}),Codicon.arrowSmallDown=new Codicon("arrow-small-down",{fontCharacter:"\\ea9d"}),Codicon.arrowSmallLeft=new Codicon("arrow-small-left",{fontCharacter:"\\ea9e"}),Codicon.arrowSmallRight=new Codicon("arrow-small-right",{fontCharacter:"\\ea9f"}),Codicon.arrowSmallUp=new Codicon("arrow-small-up",{fontCharacter:"\\eaa0"}),Codicon.arrowUp=new Codicon("arrow-up",{fontCharacter:"\\eaa1"}),Codicon.bell=new Codicon("bell",{fontCharacter:"\\eaa2"}),Codicon.bold=new Codicon("bold",{fontCharacter:"\\eaa3"}),Codicon.book=new Codicon("book",{fontCharacter:"\\eaa4"}),Codicon.bookmark=new Codicon("bookmark",{fontCharacter:"\\eaa5"}),Codicon.debugBreakpointConditionalUnverified=new Codicon("debug-breakpoint-conditional-unverified",{fontCharacter:"\\eaa6"}),Codicon.debugBreakpointConditional=new Codicon("debug-breakpoint-conditional",{fontCharacter:"\\eaa7"}),Codicon.debugBreakpointConditionalDisabled=new Codicon("debug-breakpoint-conditional-disabled",{fontCharacter:"\\eaa7"}),Codicon.debugBreakpointDataUnverified=new Codicon("debug-breakpoint-data-unverified",{fontCharacter:"\\eaa8"}),Codicon.debugBreakpointData=new Codicon("debug-breakpoint-data",{fontCharacter:"\\eaa9"}),Codicon.debugBreakpointDataDisabled=new Codicon("debug-breakpoint-data-disabled",{fontCharacter:"\\eaa9"}),Codicon.debugBreakpointLogUnverified=new Codicon("debug-breakpoint-log-unverified",{fontCharacter:"\\eaaa"}),Codicon.debugBreakpointLog=new Codicon("debug-breakpoint-log",{fontCharacter:"\\eaab"}),Codicon.debugBreakpointLogDisabled=new Codicon("debug-breakpoint-log-disabled",{fontCharacter:"\\eaab"}),Codicon.briefcase=new Codicon("briefcase",{fontCharacter:"\\eaac"}),Codicon.broadcast=new Codicon("broadcast",{fontCharacter:"\\eaad"}),Codicon.browser=new Codicon("browser",{fontCharacter:"\\eaae"}),Codicon.bug=new Codicon("bug",{fontCharacter:"\\eaaf"}),Codicon.calendar=new Codicon("calendar",{fontCharacter:"\\eab0"}),Codicon.caseSensitive=new Codicon("case-sensitive",{fontCharacter:"\\eab1"}),Codicon.check=new Codicon("check",{fontCharacter:"\\eab2"}),Codicon.checklist=new Codicon("checklist",{fontCharacter:"\\eab3"}),Codicon.chevronDown=new Codicon("chevron-down",{fontCharacter:"\\eab4"}),Codicon.dropDownButton=new Codicon("drop-down-button",Codicon.chevronDown.definition),Codicon.chevronLeft=new Codicon("chevron-left",{fontCharacter:"\\eab5"}),Codicon.chevronRight=new Codicon("chevron-right",{fontCharacter:"\\eab6"}),Codicon.chevronUp=new Codicon("chevron-up",{fontCharacter:"\\eab7"}),Codicon.chromeClose=new Codicon("chrome-close",{fontCharacter:"\\eab8"}),Codicon.chromeMaximize=new Codicon("chrome-maximize",{fontCharacter:"\\eab9"}),Codicon.chromeMinimize=new Codicon("chrome-minimize",{fontCharacter:"\\eaba"}),Codicon.chromeRestore=new Codicon("chrome-restore",{fontCharacter:"\\eabb"}),Codicon.circleOutline=new Codicon("circle-outline",{fontCharacter:"\\eabc"}),Codicon.debugBreakpointUnverified=new Codicon("debug-breakpoint-unverified",{fontCharacter:"\\eabc"}),Codicon.circleSlash=new Codicon("circle-slash",{fontCharacter:"\\eabd"}),Codicon.circuitBoard=new Codicon("circuit-board",{fontCharacter:"\\eabe"}),Codicon.clearAll=new Codicon("clear-all",{fontCharacter:"\\eabf"}),Codicon.clippy=new Codicon("clippy",{fontCharacter:"\\eac0"}),Codicon.closeAll=new Codicon("close-all",{fontCharacter:"\\eac1"}),Codicon.cloudDownload=new Codicon("cloud-download",{fontCharacter:"\\eac2"}),Codicon.cloudUpload=new Codicon("cloud-upload",{fontCharacter:"\\eac3"}),Codicon.code=new Codicon("code",{fontCharacter:"\\eac4"}),Codicon.collapseAll=new Codicon("collapse-all",{fontCharacter:"\\eac5"}),Codicon.colorMode=new Codicon("color-mode",{fontCharacter:"\\eac6"}),Codicon.commentDiscussion=new Codicon("comment-discussion",{fontCharacter:"\\eac7"}),Codicon.compareChanges=new Codicon("compare-changes",{fontCharacter:"\\eafd"}),Codicon.creditCard=new Codicon("credit-card",{fontCharacter:"\\eac9"}),Codicon.dash=new Codicon("dash",{fontCharacter:"\\eacc"}),Codicon.dashboard=new Codicon("dashboard",{fontCharacter:"\\eacd"}),Codicon.database=new Codicon("database",{fontCharacter:"\\eace"}),Codicon.debugContinue=new Codicon("debug-continue",{fontCharacter:"\\eacf"}),Codicon.debugDisconnect=new Codicon("debug-disconnect",{fontCharacter:"\\ead0"}),Codicon.debugPause=new Codicon("debug-pause",{fontCharacter:"\\ead1"}),Codicon.debugRestart=new Codicon("debug-restart",{fontCharacter:"\\ead2"}),Codicon.debugStart=new Codicon("debug-start",{fontCharacter:"\\ead3"}),Codicon.debugStepInto=new Codicon("debug-step-into",{fontCharacter:"\\ead4"}),Codicon.debugStepOut=new Codicon("debug-step-out",{fontCharacter:"\\ead5"}),Codicon.debugStepOver=new Codicon("debug-step-over",{fontCharacter:"\\ead6"}),Codicon.debugStop=new Codicon("debug-stop",{fontCharacter:"\\ead7"}),Codicon.debug=new Codicon("debug",{fontCharacter:"\\ead8"}),Codicon.deviceCameraVideo=new Codicon("device-camera-video",{fontCharacter:"\\ead9"}),Codicon.deviceCamera=new Codicon("device-camera",{fontCharacter:"\\eada"}),Codicon.deviceMobile=new Codicon("device-mobile",{fontCharacter:"\\eadb"}),Codicon.diffAdded=new Codicon("diff-added",{fontCharacter:"\\eadc"}),Codicon.diffIgnored=new Codicon("diff-ignored",{fontCharacter:"\\eadd"}),Codicon.diffModified=new Codicon("diff-modified",{fontCharacter:"\\eade"}),Codicon.diffRemoved=new Codicon("diff-removed",{fontCharacter:"\\eadf"}),Codicon.diffRenamed=new Codicon("diff-renamed",{fontCharacter:"\\eae0"}),Codicon.diff=new Codicon("diff",{fontCharacter:"\\eae1"}),Codicon.discard=new Codicon("discard",{fontCharacter:"\\eae2"}),Codicon.editorLayout=new Codicon("editor-layout",{fontCharacter:"\\eae3"}),Codicon.emptyWindow=new Codicon("empty-window",{fontCharacter:"\\eae4"}),Codicon.exclude=new Codicon("exclude",{fontCharacter:"\\eae5"}),Codicon.extensions=new Codicon("extensions",{fontCharacter:"\\eae6"}),Codicon.eyeClosed=new Codicon("eye-closed",{fontCharacter:"\\eae7"}),Codicon.fileBinary=new Codicon("file-binary",{fontCharacter:"\\eae8"}),Codicon.fileCode=new Codicon("file-code",{fontCharacter:"\\eae9"}),Codicon.fileMedia=new Codicon("file-media",{fontCharacter:"\\eaea"}),Codicon.filePdf=new Codicon("file-pdf",{fontCharacter:"\\eaeb"}),Codicon.fileSubmodule=new Codicon("file-submodule",{fontCharacter:"\\eaec"}),Codicon.fileSymlinkDirectory=new Codicon("file-symlink-directory",{fontCharacter:"\\eaed"}),Codicon.fileSymlinkFile=new Codicon("file-symlink-file",{fontCharacter:"\\eaee"}),Codicon.fileZip=new Codicon("file-zip",{fontCharacter:"\\eaef"}),Codicon.files=new Codicon("files",{fontCharacter:"\\eaf0"}),Codicon.filter=new Codicon("filter",{fontCharacter:"\\eaf1"}),Codicon.flame=new Codicon("flame",{fontCharacter:"\\eaf2"}),Codicon.foldDown=new Codicon("fold-down",{fontCharacter:"\\eaf3"}),Codicon.foldUp=new Codicon("fold-up",{fontCharacter:"\\eaf4"}),Codicon.fold=new Codicon("fold",{fontCharacter:"\\eaf5"}),Codicon.folderActive=new Codicon("folder-active",{fontCharacter:"\\eaf6"}),Codicon.folderOpened=new Codicon("folder-opened",{fontCharacter:"\\eaf7"}),Codicon.gear=new Codicon("gear",{fontCharacter:"\\eaf8"}),Codicon.gift=new Codicon("gift",{fontCharacter:"\\eaf9"}),Codicon.gistSecret=new Codicon("gist-secret",{fontCharacter:"\\eafa"}),Codicon.gist=new Codicon("gist",{fontCharacter:"\\eafb"}),Codicon.gitCommit=new Codicon("git-commit",{fontCharacter:"\\eafc"}),Codicon.gitCompare=new Codicon("git-compare",{fontCharacter:"\\eafd"}),Codicon.gitMerge=new Codicon("git-merge",{fontCharacter:"\\eafe"}),Codicon.githubAction=new Codicon("github-action",{fontCharacter:"\\eaff"}),Codicon.githubAlt=new Codicon("github-alt",{fontCharacter:"\\eb00"}),Codicon.globe=new Codicon("globe",{fontCharacter:"\\eb01"}),Codicon.grabber=new Codicon("grabber",{fontCharacter:"\\eb02"}),Codicon.graph=new Codicon("graph",{fontCharacter:"\\eb03"}),Codicon.gripper=new Codicon("gripper",{fontCharacter:"\\eb04"}),Codicon.heart=new Codicon("heart",{fontCharacter:"\\eb05"}),Codicon.home=new Codicon("home",{fontCharacter:"\\eb06"}),Codicon.horizontalRule=new Codicon("horizontal-rule",{fontCharacter:"\\eb07"}),Codicon.hubot=new Codicon("hubot",{fontCharacter:"\\eb08"}),Codicon.inbox=new Codicon("inbox",{fontCharacter:"\\eb09"}),Codicon.issueClosed=new Codicon("issue-closed",{fontCharacter:"\\eba4"}),Codicon.issueReopened=new Codicon("issue-reopened",{fontCharacter:"\\eb0b"}),Codicon.issues=new Codicon("issues",{fontCharacter:"\\eb0c"}),Codicon.italic=new Codicon("italic",{fontCharacter:"\\eb0d"}),Codicon.jersey=new Codicon("jersey",{fontCharacter:"\\eb0e"}),Codicon.json=new Codicon("json",{fontCharacter:"\\eb0f"}),Codicon.kebabVertical=new Codicon("kebab-vertical",{fontCharacter:"\\eb10"}),Codicon.key=new Codicon("key",{fontCharacter:"\\eb11"}),Codicon.law=new Codicon("law",{fontCharacter:"\\eb12"}),Codicon.lightbulbAutofix=new Codicon("lightbulb-autofix",{fontCharacter:"\\eb13"}),Codicon.linkExternal=new Codicon("link-external",{fontCharacter:"\\eb14"}),Codicon.link=new Codicon("link",{fontCharacter:"\\eb15"}),Codicon.listOrdered=new Codicon("list-ordered",{fontCharacter:"\\eb16"}),Codicon.listUnordered=new Codicon("list-unordered",{fontCharacter:"\\eb17"}),Codicon.liveShare=new Codicon("live-share",{fontCharacter:"\\eb18"}),Codicon.loading=new Codicon("loading",{fontCharacter:"\\eb19"}),Codicon.location=new Codicon("location",{fontCharacter:"\\eb1a"}),Codicon.mailRead=new Codicon("mail-read",{fontCharacter:"\\eb1b"}),Codicon.mail=new Codicon("mail",{fontCharacter:"\\eb1c"}),Codicon.markdown=new Codicon("markdown",{fontCharacter:"\\eb1d"}),Codicon.megaphone=new Codicon("megaphone",{fontCharacter:"\\eb1e"}),Codicon.mention=new Codicon("mention",{fontCharacter:"\\eb1f"}),Codicon.milestone=new Codicon("milestone",{fontCharacter:"\\eb20"}),Codicon.mortarBoard=new Codicon("mortar-board",{fontCharacter:"\\eb21"}),Codicon.move=new Codicon("move",{fontCharacter:"\\eb22"}),Codicon.multipleWindows=new Codicon("multiple-windows",{fontCharacter:"\\eb23"}),Codicon.mute=new Codicon("mute",{fontCharacter:"\\eb24"}),Codicon.noNewline=new Codicon("no-newline",{fontCharacter:"\\eb25"}),Codicon.note=new Codicon("note",{fontCharacter:"\\eb26"}),Codicon.octoface=new Codicon("octoface",{fontCharacter:"\\eb27"}),Codicon.openPreview=new Codicon("open-preview",{fontCharacter:"\\eb28"}),Codicon.package_=new Codicon("package",{fontCharacter:"\\eb29"}),Codicon.paintcan=new Codicon("paintcan",{fontCharacter:"\\eb2a"}),Codicon.pin=new Codicon("pin",{fontCharacter:"\\eb2b"}),Codicon.play=new Codicon("play",{fontCharacter:"\\eb2c"}),Codicon.run=new Codicon("run",{fontCharacter:"\\eb2c"}),Codicon.plug=new Codicon("plug",{fontCharacter:"\\eb2d"}),Codicon.preserveCase=new Codicon("preserve-case",{fontCharacter:"\\eb2e"}),Codicon.preview=new Codicon("preview",{fontCharacter:"\\eb2f"}),Codicon.project=new Codicon("project",{fontCharacter:"\\eb30"}),Codicon.pulse=new Codicon("pulse",{fontCharacter:"\\eb31"}),Codicon.question=new Codicon("question",{fontCharacter:"\\eb32"}),Codicon.quote=new Codicon("quote",{fontCharacter:"\\eb33"}),Codicon.radioTower=new Codicon("radio-tower",{fontCharacter:"\\eb34"}),Codicon.reactions=new Codicon("reactions",{fontCharacter:"\\eb35"}),Codicon.references=new Codicon("references",{fontCharacter:"\\eb36"}),Codicon.refresh=new Codicon("refresh",{fontCharacter:"\\eb37"}),Codicon.regex=new Codicon("regex",{fontCharacter:"\\eb38"}),Codicon.remoteExplorer=new Codicon("remote-explorer",{fontCharacter:"\\eb39"}),Codicon.remote=new Codicon("remote",{fontCharacter:"\\eb3a"}),Codicon.remove=new Codicon("remove",{fontCharacter:"\\eb3b"}),Codicon.replaceAll=new Codicon("replace-all",{fontCharacter:"\\eb3c"}),Codicon.replace=new Codicon("replace",{fontCharacter:"\\eb3d"}),Codicon.repoClone=new Codicon("repo-clone",{fontCharacter:"\\eb3e"}),Codicon.repoForcePush=new Codicon("repo-force-push",{fontCharacter:"\\eb3f"}),Codicon.repoPull=new Codicon("repo-pull",{fontCharacter:"\\eb40"}),Codicon.repoPush=new Codicon("repo-push",{fontCharacter:"\\eb41"}),Codicon.report=new Codicon("report",{fontCharacter:"\\eb42"}),Codicon.requestChanges=new Codicon("request-changes",{fontCharacter:"\\eb43"}),Codicon.rocket=new Codicon("rocket",{fontCharacter:"\\eb44"}),Codicon.rootFolderOpened=new Codicon("root-folder-opened",{fontCharacter:"\\eb45"}),Codicon.rootFolder=new Codicon("root-folder",{fontCharacter:"\\eb46"}),Codicon.rss=new Codicon("rss",{fontCharacter:"\\eb47"}),Codicon.ruby=new Codicon("ruby",{fontCharacter:"\\eb48"}),Codicon.saveAll=new Codicon("save-all",{fontCharacter:"\\eb49"}),Codicon.saveAs=new Codicon("save-as",{fontCharacter:"\\eb4a"}),Codicon.save=new Codicon("save",{fontCharacter:"\\eb4b"}),Codicon.screenFull=new Codicon("screen-full",{fontCharacter:"\\eb4c"}),Codicon.screenNormal=new Codicon("screen-normal",{fontCharacter:"\\eb4d"}),Codicon.searchStop=new Codicon("search-stop",{fontCharacter:"\\eb4e"}),Codicon.server=new Codicon("server",{fontCharacter:"\\eb50"}),Codicon.settingsGear=new Codicon("settings-gear",{fontCharacter:"\\eb51"}),Codicon.settings=new Codicon("settings",{fontCharacter:"\\eb52"}),Codicon.shield=new Codicon("shield",{fontCharacter:"\\eb53"}),Codicon.smiley=new Codicon("smiley",{fontCharacter:"\\eb54"}),Codicon.sortPrecedence=new Codicon("sort-precedence",{fontCharacter:"\\eb55"}),Codicon.splitHorizontal=new Codicon("split-horizontal",{fontCharacter:"\\eb56"}),Codicon.splitVertical=new Codicon("split-vertical",{fontCharacter:"\\eb57"}),Codicon.squirrel=new Codicon("squirrel",{fontCharacter:"\\eb58"}),Codicon.starFull=new Codicon("star-full",{fontCharacter:"\\eb59"}),Codicon.starHalf=new Codicon("star-half",{fontCharacter:"\\eb5a"}),Codicon.symbolClass=new Codicon("symbol-class",{fontCharacter:"\\eb5b"}),Codicon.symbolColor=new Codicon("symbol-color",{fontCharacter:"\\eb5c"}),Codicon.symbolConstant=new Codicon("symbol-constant",{fontCharacter:"\\eb5d"}),Codicon.symbolEnumMember=new Codicon("symbol-enum-member",{fontCharacter:"\\eb5e"}),Codicon.symbolField=new Codicon("symbol-field",{fontCharacter:"\\eb5f"}),Codicon.symbolFile=new Codicon("symbol-file",{fontCharacter:"\\eb60"}),Codicon.symbolInterface=new Codicon("symbol-interface",{fontCharacter:"\\eb61"}),Codicon.symbolKeyword=new Codicon("symbol-keyword",{fontCharacter:"\\eb62"}),Codicon.symbolMisc=new Codicon("symbol-misc",{fontCharacter:"\\eb63"}),Codicon.symbolOperator=new Codicon("symbol-operator",{fontCharacter:"\\eb64"}),Codicon.symbolProperty=new Codicon("symbol-property",{fontCharacter:"\\eb65"}),Codicon.wrench=new Codicon("wrench",{fontCharacter:"\\eb65"}),Codicon.wrenchSubaction=new Codicon("wrench-subaction",{fontCharacter:"\\eb65"}),Codicon.symbolSnippet=new Codicon("symbol-snippet",{fontCharacter:"\\eb66"}),Codicon.tasklist=new Codicon("tasklist",{fontCharacter:"\\eb67"}),Codicon.telescope=new Codicon("telescope",{fontCharacter:"\\eb68"}),Codicon.textSize=new Codicon("text-size",{fontCharacter:"\\eb69"}),Codicon.threeBars=new Codicon("three-bars",{fontCharacter:"\\eb6a"}),Codicon.thumbsdown=new Codicon("thumbsdown",{fontCharacter:"\\eb6b"}),Codicon.thumbsup=new Codicon("thumbsup",{fontCharacter:"\\eb6c"}),Codicon.tools=new Codicon("tools",{fontCharacter:"\\eb6d"}),Codicon.triangleDown=new Codicon("triangle-down",{fontCharacter:"\\eb6e"}),Codicon.triangleLeft=new Codicon("triangle-left",{fontCharacter:"\\eb6f"}),Codicon.triangleRight=new Codicon("triangle-right",{fontCharacter:"\\eb70"}),Codicon.triangleUp=new Codicon("triangle-up",{fontCharacter:"\\eb71"}),Codicon.twitter=new Codicon("twitter",{fontCharacter:"\\eb72"}),Codicon.unfold=new Codicon("unfold",{fontCharacter:"\\eb73"}),Codicon.unlock=new Codicon("unlock",{fontCharacter:"\\eb74"}),Codicon.unmute=new Codicon("unmute",{fontCharacter:"\\eb75"}),Codicon.unverified=new Codicon("unverified",{fontCharacter:"\\eb76"}),Codicon.verified=new Codicon("verified",{fontCharacter:"\\eb77"}),Codicon.versions=new Codicon("versions",{fontCharacter:"\\eb78"}),Codicon.vmActive=new Codicon("vm-active",{fontCharacter:"\\eb79"}),Codicon.vmOutline=new Codicon("vm-outline",{fontCharacter:"\\eb7a"}),Codicon.vmRunning=new Codicon("vm-running",{fontCharacter:"\\eb7b"}),Codicon.watch=new Codicon("watch",{fontCharacter:"\\eb7c"}),Codicon.whitespace=new Codicon("whitespace",{fontCharacter:"\\eb7d"}),Codicon.wholeWord=new Codicon("whole-word",{fontCharacter:"\\eb7e"}),Codicon.window=new Codicon("window",{fontCharacter:"\\eb7f"}),Codicon.wordWrap=new Codicon("word-wrap",{fontCharacter:"\\eb80"}),Codicon.zoomIn=new Codicon("zoom-in",{fontCharacter:"\\eb81"}),Codicon.zoomOut=new Codicon("zoom-out",{fontCharacter:"\\eb82"}),Codicon.listFilter=new Codicon("list-filter",{fontCharacter:"\\eb83"}),Codicon.listFlat=new Codicon("list-flat",{fontCharacter:"\\eb84"}),Codicon.listSelection=new Codicon("list-selection",{fontCharacter:"\\eb85"}),Codicon.selection=new Codicon("selection",{fontCharacter:"\\eb85"}),Codicon.listTree=new Codicon("list-tree",{fontCharacter:"\\eb86"}),Codicon.debugBreakpointFunctionUnverified=new Codicon("debug-breakpoint-function-unverified",{fontCharacter:"\\eb87"}),Codicon.debugBreakpointFunction=new Codicon("debug-breakpoint-function",{fontCharacter:"\\eb88"}),Codicon.debugBreakpointFunctionDisabled=new Codicon("debug-breakpoint-function-disabled",{fontCharacter:"\\eb88"}),Codicon.debugStackframeActive=new Codicon("debug-stackframe-active",{fontCharacter:"\\eb89"}),Codicon.debugStackframeDot=new Codicon("debug-stackframe-dot",{fontCharacter:"\\eb8a"}),Codicon.debugStackframe=new Codicon("debug-stackframe",{fontCharacter:"\\eb8b"}),Codicon.debugStackframeFocused=new Codicon("debug-stackframe-focused",{fontCharacter:"\\eb8b"}),Codicon.debugBreakpointUnsupported=new Codicon("debug-breakpoint-unsupported",{fontCharacter:"\\eb8c"}),Codicon.symbolString=new Codicon("symbol-string",{fontCharacter:"\\eb8d"}),Codicon.debugReverseContinue=new Codicon("debug-reverse-continue",{fontCharacter:"\\eb8e"}),Codicon.debugStepBack=new Codicon("debug-step-back",{fontCharacter:"\\eb8f"}),Codicon.debugRestartFrame=new Codicon("debug-restart-frame",{fontCharacter:"\\eb90"}),Codicon.callIncoming=new Codicon("call-incoming",{fontCharacter:"\\eb92"}),Codicon.callOutgoing=new Codicon("call-outgoing",{fontCharacter:"\\eb93"}),Codicon.menu=new Codicon("menu",{fontCharacter:"\\eb94"}),Codicon.expandAll=new Codicon("expand-all",{fontCharacter:"\\eb95"}),Codicon.feedback=new Codicon("feedback",{fontCharacter:"\\eb96"}),Codicon.groupByRefType=new Codicon("group-by-ref-type",{fontCharacter:"\\eb97"}),Codicon.ungroupByRefType=new Codicon("ungroup-by-ref-type",{fontCharacter:"\\eb98"}),Codicon.account=new Codicon("account",{fontCharacter:"\\eb99"}),Codicon.bellDot=new Codicon("bell-dot",{fontCharacter:"\\eb9a"}),Codicon.debugConsole=new Codicon("debug-console",{fontCharacter:"\\eb9b"}),Codicon.library=new Codicon("library",{fontCharacter:"\\eb9c"}),Codicon.output=new Codicon("output",{fontCharacter:"\\eb9d"}),Codicon.runAll=new Codicon("run-all",{fontCharacter:"\\eb9e"}),Codicon.syncIgnored=new Codicon("sync-ignored",{fontCharacter:"\\eb9f"}),Codicon.pinned=new Codicon("pinned",{fontCharacter:"\\eba0"}),Codicon.githubInverted=new Codicon("github-inverted",{fontCharacter:"\\eba1"}),Codicon.debugAlt=new Codicon("debug-alt",{fontCharacter:"\\eb91"}),Codicon.serverProcess=new Codicon("server-process",{fontCharacter:"\\eba2"}),Codicon.serverEnvironment=new Codicon("server-environment",{fontCharacter:"\\eba3"}),Codicon.pass=new Codicon("pass",{fontCharacter:"\\eba4"}),Codicon.stopCircle=new Codicon("stop-circle",{fontCharacter:"\\eba5"}),Codicon.playCircle=new Codicon("play-circle",{fontCharacter:"\\eba6"}),Codicon.record=new Codicon("record",{fontCharacter:"\\eba7"}),Codicon.debugAltSmall=new Codicon("debug-alt-small",{fontCharacter:"\\eba8"}),Codicon.vmConnect=new Codicon("vm-connect",{fontCharacter:"\\eba9"}),Codicon.cloud=new Codicon("cloud",{fontCharacter:"\\ebaa"}),Codicon.merge=new Codicon("merge",{fontCharacter:"\\ebab"}),Codicon.exportIcon=new Codicon("export",{fontCharacter:"\\ebac"}),Codicon.graphLeft=new Codicon("graph-left",{fontCharacter:"\\ebad"}),Codicon.magnet=new Codicon("magnet",{fontCharacter:"\\ebae"}),Codicon.notebook=new Codicon("notebook",{fontCharacter:"\\ebaf"}),Codicon.redo=new Codicon("redo",{fontCharacter:"\\ebb0"}),Codicon.checkAll=new Codicon("check-all",{fontCharacter:"\\ebb1"}),Codicon.pinnedDirty=new Codicon("pinned-dirty",{fontCharacter:"\\ebb2"}),Codicon.passFilled=new Codicon("pass-filled",{fontCharacter:"\\ebb3"}),Codicon.circleLargeFilled=new Codicon("circle-large-filled",{fontCharacter:"\\ebb4"}),Codicon.circleLargeOutline=new Codicon("circle-large-outline",{fontCharacter:"\\ebb5"}),Codicon.combine=new Codicon("combine",{fontCharacter:"\\ebb6"}),Codicon.gather=new Codicon("gather",{fontCharacter:"\\ebb6"}),Codicon.table=new Codicon("table",{fontCharacter:"\\ebb7"}),Codicon.variableGroup=new Codicon("variable-group",{fontCharacter:"\\ebb8"}),Codicon.typeHierarchy=new Codicon("type-hierarchy",{fontCharacter:"\\ebb9"}),Codicon.typeHierarchySub=new Codicon("type-hierarchy-sub",{fontCharacter:"\\ebba"}),Codicon.typeHierarchySuper=new Codicon("type-hierarchy-super",{fontCharacter:"\\ebbb"}),Codicon.gitPullRequestCreate=new Codicon("git-pull-request-create",{fontCharacter:"\\ebbc"}),Codicon.runAbove=new Codicon("run-above",{fontCharacter:"\\ebbd"}),Codicon.runBelow=new Codicon("run-below",{fontCharacter:"\\ebbe"}),Codicon.notebookTemplate=new Codicon("notebook-template",{fontCharacter:"\\ebbf"}),Codicon.debugRerun=new Codicon("debug-rerun",{fontCharacter:"\\ebc0"}),Codicon.workspaceTrusted=new Codicon("workspace-trusted",{fontCharacter:"\\ebc1"}),Codicon.workspaceUntrusted=new Codicon("workspace-untrusted",{fontCharacter:"\\ebc2"}),Codicon.workspaceUnspecified=new Codicon("workspace-unspecified",{fontCharacter:"\\ebc3"}),Codicon.terminalCmd=new Codicon("terminal-cmd",{fontCharacter:"\\ebc4"}),Codicon.terminalDebian=new Codicon("terminal-debian",{fontCharacter:"\\ebc5"}),Codicon.terminalLinux=new Codicon("terminal-linux",{fontCharacter:"\\ebc6"}),Codicon.terminalPowershell=new Codicon("terminal-powershell",{fontCharacter:"\\ebc7"}),Codicon.terminalTmux=new Codicon("terminal-tmux",{fontCharacter:"\\ebc8"}),Codicon.terminalUbuntu=new Codicon("terminal-ubuntu",{fontCharacter:"\\ebc9"}),Codicon.terminalBash=new Codicon("terminal-bash",{fontCharacter:"\\ebca"}),Codicon.arrowSwap=new Codicon("arrow-swap",{fontCharacter:"\\ebcb"}),Codicon.copy=new Codicon("copy",{fontCharacter:"\\ebcc"}),Codicon.personAdd=new Codicon("person-add",{fontCharacter:"\\ebcd"}),Codicon.filterFilled=new Codicon("filter-filled",{fontCharacter:"\\ebce"}),Codicon.wand=new Codicon("wand",{fontCharacter:"\\ebcf"}),Codicon.debugLineByLine=new Codicon("debug-line-by-line",{fontCharacter:"\\ebd0"}),Codicon.inspect=new Codicon("inspect",{fontCharacter:"\\ebd1"}),Codicon.layers=new Codicon("layers",{fontCharacter:"\\ebd2"}),Codicon.layersDot=new Codicon("layers-dot",{fontCharacter:"\\ebd3"}),Codicon.layersActive=new Codicon("layers-active",{fontCharacter:"\\ebd4"}),Codicon.compass=new Codicon("compass",{fontCharacter:"\\ebd5"}),Codicon.compassDot=new Codicon("compass-dot",{fontCharacter:"\\ebd6"}),Codicon.compassActive=new Codicon("compass-active",{fontCharacter:"\\ebd7"}),Codicon.azure=new Codicon("azure",{fontCharacter:"\\ebd8"}),Codicon.issueDraft=new Codicon("issue-draft",{fontCharacter:"\\ebd9"}),Codicon.gitPullRequestClosed=new Codicon("git-pull-request-closed",{fontCharacter:"\\ebda"}),Codicon.gitPullRequestDraft=new Codicon("git-pull-request-draft",{fontCharacter:"\\ebdb"}),Codicon.debugAll=new Codicon("debug-all",{fontCharacter:"\\ebdc"}),Codicon.debugCoverage=new Codicon("debug-coverage",{fontCharacter:"\\ebdd"}),Codicon.runErrors=new Codicon("run-errors",{fontCharacter:"\\ebde"}),Codicon.folderLibrary=new Codicon("folder-library",{fontCharacter:"\\ebdf"}),Codicon.debugContinueSmall=new Codicon("debug-continue-small",{fontCharacter:"\\ebe0"}),Codicon.beakerStop=new Codicon("beaker-stop",{fontCharacter:"\\ebe1"}),Codicon.graphLine=new Codicon("graph-line",{fontCharacter:"\\ebe2"}),Codicon.graphScatter=new Codicon("graph-scatter",{fontCharacter:"\\ebe3"}),Codicon.pieChart=new Codicon("pie-chart",{fontCharacter:"\\ebe4"}),Codicon.bracket=new Codicon("bracket",Codicon.json.definition),Codicon.bracketDot=new Codicon("bracket-dot",{fontCharacter:"\\ebe5"}),Codicon.bracketError=new Codicon("bracket-error",{fontCharacter:"\\ebe6"}),Codicon.lockSmall=new Codicon("lock-small",{fontCharacter:"\\ebe7"}),Codicon.azureDevops=new Codicon("azure-devops",{fontCharacter:"\\ebe8"}),Codicon.verifiedFilled=new Codicon("verified-filled",{fontCharacter:"\\ebe9"})}(Codicon||(Codicon={}))},
/***/6626:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */E:function(){/* binding */return forEach},
/* harmony export */r:function(){/* binding */return SetMap}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const hasOwnProperty=Object.prototype.hasOwnProperty;
/**
 * Iterates over each entry in the provided dictionary. The iterator allows
 * to remove elements and will stop when the callback returns {{false}}.
 */function forEach(from,callback){for(let key in from)if(hasOwnProperty.call(from,key)){const result=callback({key:key,value:from[key]},(function(){delete from[key]}));if(!1===result)return}}class SetMap{constructor(){this.map=new Map}add(key,value){let values=this.map.get(key);values||(values=new Set,this.map.set(key,values)),values.add(value)}delete(key,value){const values=this.map.get(key);values&&(values.delete(value),0===values.size&&this.map.delete(key))}forEach(key,fn){const values=this.map.get(key);values&&values.forEach(fn)}}
/***/},
/***/41264:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* unused harmony export HSLA */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function roundFloat(number,decimalPoints){const decimal=Math.pow(10,decimalPoints);return Math.round(number*decimal)/decimal}
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Il:function(){/* binding */return Color},
/* harmony export */VS:function(){/* binding */return RGBA},
/* harmony export */tx:function(){/* binding */return HSVA}
/* harmony export */});class RGBA{constructor(r,g,b,a=1){this._rgbaBrand=void 0,this.r=0|Math.min(255,Math.max(0,r)),this.g=0|Math.min(255,Math.max(0,g)),this.b=0|Math.min(255,Math.max(0,b)),this.a=roundFloat(Math.max(Math.min(1,a),0),3)}static equals(a,b){return a.r===b.r&&a.g===b.g&&a.b===b.b&&a.a===b.a}}class HSLA{constructor(h,s,l,a){this._hslaBrand=void 0,this.h=0|Math.max(Math.min(360,h),0),this.s=roundFloat(Math.max(Math.min(1,s),0),3),this.l=roundFloat(Math.max(Math.min(1,l),0),3),this.a=roundFloat(Math.max(Math.min(1,a),0),3)}static equals(a,b){return a.h===b.h&&a.s===b.s&&a.l===b.l&&a.a===b.a}
/**
     * Converts an RGB color value to HSL. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes r, g, and b are contained in the set [0, 255] and
     * returns h in the set [0, 360], s, and l in the set [0, 1].
     */static fromRGBA(rgba){const r=rgba.r/255,g=rgba.g/255,b=rgba.b/255,a=rgba.a,max=Math.max(r,g,b),min=Math.min(r,g,b);let h=0,s=0;const l=(min+max)/2,chroma=max-min;if(chroma>0){switch(s=Math.min(l<=.5?chroma/(2*l):chroma/(2-2*l),1),max){case r:h=(g-b)/chroma+(g<b?6:0);break;case g:h=(b-r)/chroma+2;break;case b:h=(r-g)/chroma+4;break}h*=60,h=Math.round(h)}return new HSLA(h,s,l,a)}static _hue2rgb(p,q,t){return t<0&&(t+=1),t>1&&(t-=1),t<1/6?p+6*(q-p)*t:t<.5?q:t<2/3?p+(q-p)*(2/3-t)*6:p}
/**
     * Converts an HSL color value to RGB. Conversion formula
     * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
     * Assumes h in the set [0, 360] s, and l are contained in the set [0, 1] and
     * returns r, g, and b in the set [0, 255].
     */static toRGBA(hsla){const h=hsla.h/360,{s:s,l:l,a:a}=hsla;let r,g,b;if(0===s)r=g=b=l;// achromatic
else{const q=l<.5?l*(1+s):l+s-l*s,p=2*l-q;r=HSLA._hue2rgb(p,q,h+1/3),g=HSLA._hue2rgb(p,q,h),b=HSLA._hue2rgb(p,q,h-1/3)}return new RGBA(Math.round(255*r),Math.round(255*g),Math.round(255*b),a)}}class HSVA{constructor(h,s,v,a){this._hsvaBrand=void 0,this.h=0|Math.max(Math.min(360,h),0),this.s=roundFloat(Math.max(Math.min(1,s),0),3),this.v=roundFloat(Math.max(Math.min(1,v),0),3),this.a=roundFloat(Math.max(Math.min(1,a),0),3)}static equals(a,b){return a.h===b.h&&a.s===b.s&&a.v===b.v&&a.a===b.a}
// from http://www.rapidtables.com/convert/color/rgb-to-hsv.htm
static fromRGBA(rgba){const r=rgba.r/255,g=rgba.g/255,b=rgba.b/255,cmax=Math.max(r,g,b),cmin=Math.min(r,g,b),delta=cmax-cmin,s=0===cmax?0:delta/cmax;let m;return m=0===delta?0:cmax===r?((g-b)/delta%6+6)%6:cmax===g?(b-r)/delta+2:(r-g)/delta+4,new HSVA(Math.round(60*m),s,cmax,rgba.a)}
// from http://www.rapidtables.com/convert/color/hsv-to-rgb.htm
static toRGBA(hsva){const{h:h,s:s,v:v,a:a}=hsva,c=v*s,x=c*(1-Math.abs(h/60%2-1)),m=v-c;let[r,g,b]=[0,0,0];return h<60?(r=c,g=x):h<120?(r=x,g=c):h<180?(g=c,b=x):h<240?(g=x,b=c):h<300?(r=x,b=c):h<=360&&(r=c,b=x),r=Math.round(255*(r+m)),g=Math.round(255*(g+m)),b=Math.round(255*(b+m)),new RGBA(r,g,b,a)}}class Color{constructor(arg){if(!arg)throw new Error("Color needs a value");if(arg instanceof RGBA)this.rgba=arg;else if(arg instanceof HSLA)this._hsla=arg,this.rgba=HSLA.toRGBA(arg);else{if(!(arg instanceof HSVA))throw new Error("Invalid color ctor argument");this._hsva=arg,this.rgba=HSVA.toRGBA(arg)}}static fromHex(hex){return Color.Format.CSS.parseHex(hex)||Color.red}get hsla(){return this._hsla?this._hsla:HSLA.fromRGBA(this.rgba)}get hsva(){return this._hsva?this._hsva:HSVA.fromRGBA(this.rgba)}equals(other){return!!other&&RGBA.equals(this.rgba,other.rgba)&&HSLA.equals(this.hsla,other.hsla)&&HSVA.equals(this.hsva,other.hsva)}
/**
     * http://www.w3.org/TR/WCAG20/#relativeluminancedef
     * Returns the number in the set [0, 1]. O => Darkest Black. 1 => Lightest white.
     */getRelativeLuminance(){const R=Color._relativeLuminanceForComponent(this.rgba.r),G=Color._relativeLuminanceForComponent(this.rgba.g),B=Color._relativeLuminanceForComponent(this.rgba.b),luminance=.2126*R+.7152*G+.0722*B;return roundFloat(luminance,4)}static _relativeLuminanceForComponent(color){const c=color/255;return c<=.03928?c/12.92:Math.pow((c+.055)/1.055,2.4)}
/**
     *	http://24ways.org/2010/calculating-color-contrast
     *  Return 'true' if lighter color otherwise 'false'
     */isLighter(){const yiq=(299*this.rgba.r+587*this.rgba.g+114*this.rgba.b)/1e3;return yiq>=128}isLighterThan(another){const lum1=this.getRelativeLuminance(),lum2=another.getRelativeLuminance();return lum1>lum2}isDarkerThan(another){const lum1=this.getRelativeLuminance(),lum2=another.getRelativeLuminance();return lum1<lum2}lighten(factor){return new Color(new HSLA(this.hsla.h,this.hsla.s,this.hsla.l+this.hsla.l*factor,this.hsla.a))}darken(factor){return new Color(new HSLA(this.hsla.h,this.hsla.s,this.hsla.l-this.hsla.l*factor,this.hsla.a))}transparent(factor){const{r:r,g:g,b:b,a:a}=this.rgba;return new Color(new RGBA(r,g,b,a*factor))}isTransparent(){return 0===this.rgba.a}isOpaque(){return 1===this.rgba.a}opposite(){return new Color(new RGBA(255-this.rgba.r,255-this.rgba.g,255-this.rgba.b,this.rgba.a))}toString(){return this._toString||(this._toString=Color.Format.CSS.format(this)),this._toString}static getLighterColor(of,relative,factor){if(of.isLighterThan(relative))return of;factor=factor||.5;const lum1=of.getRelativeLuminance(),lum2=relative.getRelativeLuminance();return factor=factor*(lum2-lum1)/lum2,of.lighten(factor)}static getDarkerColor(of,relative,factor){if(of.isDarkerThan(relative))return of;factor=factor||.5;const lum1=of.getRelativeLuminance(),lum2=relative.getRelativeLuminance();return factor=factor*(lum1-lum2)/lum1,of.darken(factor)}}Color.white=new Color(new RGBA(255,255,255,1)),Color.black=new Color(new RGBA(0,0,0,1)),Color.red=new Color(new RGBA(255,0,0,1)),Color.blue=new Color(new RGBA(0,0,255,1)),Color.cyan=new Color(new RGBA(0,255,255,1)),Color.lightgrey=new Color(new RGBA(211,211,211,1)),Color.transparent=new Color(new RGBA(0,0,0,0)),function(Color){let Format;(function(Format){let CSS;(function(CSS){function formatRGB(color){return 1===color.rgba.a?`rgb(${color.rgba.r}, ${color.rgba.g}, ${color.rgba.b})`:Color.Format.CSS.formatRGBA(color)}function formatRGBA(color){return`rgba(${color.rgba.r}, ${color.rgba.g}, ${color.rgba.b}, ${+color.rgba.a.toFixed(2)})`}function formatHSL(color){return 1===color.hsla.a?`hsl(${color.hsla.h}, ${(100*color.hsla.s).toFixed(2)}%, ${(100*color.hsla.l).toFixed(2)}%)`:Color.Format.CSS.formatHSLA(color)}function formatHSLA(color){return`hsla(${color.hsla.h}, ${(100*color.hsla.s).toFixed(2)}%, ${(100*color.hsla.l).toFixed(2)}%, ${color.hsla.a.toFixed(2)})`}function _toTwoDigitHex(n){const r=n.toString(16);return 2!==r.length?"0"+r:r}
/**
             * Formats the color as #RRGGBB
             */function formatHex(color){return`#${_toTwoDigitHex(color.rgba.r)}${_toTwoDigitHex(color.rgba.g)}${_toTwoDigitHex(color.rgba.b)}`}
/**
             * Formats the color as #RRGGBBAA
             * If 'compact' is set, colors without transparancy will be printed as #RRGGBB
             */
function formatHexA(color,compact=!1){return compact&&1===color.rgba.a?Color.Format.CSS.formatHex(color):`#${_toTwoDigitHex(color.rgba.r)}${_toTwoDigitHex(color.rgba.g)}${_toTwoDigitHex(color.rgba.b)}${_toTwoDigitHex(Math.round(255*color.rgba.a))}`}
/**
             * The default format will use HEX if opaque and RGBA otherwise.
             */
function format(color){return color.isOpaque()?Color.Format.CSS.formatHex(color):Color.Format.CSS.formatRGBA(color)}
/**
             * Converts an Hex color value to a Color.
             * returns r, g, and b are contained in the set [0, 255]
             * @param hex string (#RGB, #RGBA, #RRGGBB or #RRGGBBAA).
             */
function parseHex(hex){const length=hex.length;if(0===length)
// Invalid color
return null;if(35/* Hash */!==hex.charCodeAt(0))
// Does not begin with a #
return null;if(7===length){
// #RRGGBB format
const r=16*_parseHexDigit(hex.charCodeAt(1))+_parseHexDigit(hex.charCodeAt(2)),g=16*_parseHexDigit(hex.charCodeAt(3))+_parseHexDigit(hex.charCodeAt(4)),b=16*_parseHexDigit(hex.charCodeAt(5))+_parseHexDigit(hex.charCodeAt(6));return new Color(new RGBA(r,g,b,1))}if(9===length){
// #RRGGBBAA format
const r=16*_parseHexDigit(hex.charCodeAt(1))+_parseHexDigit(hex.charCodeAt(2)),g=16*_parseHexDigit(hex.charCodeAt(3))+_parseHexDigit(hex.charCodeAt(4)),b=16*_parseHexDigit(hex.charCodeAt(5))+_parseHexDigit(hex.charCodeAt(6)),a=16*_parseHexDigit(hex.charCodeAt(7))+_parseHexDigit(hex.charCodeAt(8));return new Color(new RGBA(r,g,b,a/255))}if(4===length){
// #RGB format
const r=_parseHexDigit(hex.charCodeAt(1)),g=_parseHexDigit(hex.charCodeAt(2)),b=_parseHexDigit(hex.charCodeAt(3));return new Color(new RGBA(16*r+r,16*g+g,16*b+b))}if(5===length){
// #RGBA format
const r=_parseHexDigit(hex.charCodeAt(1)),g=_parseHexDigit(hex.charCodeAt(2)),b=_parseHexDigit(hex.charCodeAt(3)),a=_parseHexDigit(hex.charCodeAt(4));return new Color(new RGBA(16*r+r,16*g+g,16*b+b,(16*a+a)/255))}
// Invalid color
return null}function _parseHexDigit(charCode){switch(charCode){case 48/* Digit0 */:return 0;case 49/* Digit1 */:return 1;case 50/* Digit2 */:return 2;case 51/* Digit3 */:return 3;case 52/* Digit4 */:return 4;case 53/* Digit5 */:return 5;case 54/* Digit6 */:return 6;case 55/* Digit7 */:return 7;case 56/* Digit8 */:return 8;case 57/* Digit9 */:return 9;case 97/* a */:return 10;case 65/* A */:return 10;case 98/* b */:return 11;case 66/* B */:return 11;case 99/* c */:return 12;case 67/* C */:return 12;case 100/* d */:return 13;case 68/* D */:return 13;case 101/* e */:return 14;case 69/* E */:return 14;case 102/* f */:return 15;case 70/* F */:return 15}return 0}CSS.formatRGB=formatRGB,CSS.formatRGBA=formatRGBA,CSS.formatHSL=formatHSL,CSS.formatHSLA=formatHSLA,CSS.formatHex=formatHex,CSS.formatHexA=formatHexA,CSS.format=format,CSS.parseHex=parseHex})(CSS=Format.CSS||(Format.CSS={}))})(Format=Color.Format||(Color.Format={}))}(Color||(Color={}))},
/***/749898:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){function memoize(_target,key,descriptor){let fnKey=null,fn=null;if("function"===typeof descriptor.value?(fnKey="value",fn=descriptor.value,fn.length):"function"===typeof descriptor.get&&(fnKey="get",fn=descriptor.get),!fn)throw new Error("not supported");const memoizeKey=`$memoize$${key}`;descriptor[fnKey]=function(...args){return this.hasOwnProperty(memoizeKey)||Object.defineProperty(this,memoizeKey,{configurable:!1,enumerable:!1,writable:!1,value:fn.apply(this,args)}),this[memoizeKey]}}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */H:function(){/* binding */return memoize}
/* harmony export */})},
/***/722571:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{Hs:function(){/* binding */return LcsDiff},a$:function(){/* binding */return stringDiff}});// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/diff/diffChange.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/**
 * Represents information about a specific difference between two sequences.
 */
class DiffChange{
/**
     * Constructs a new DiffChange with the given sequence information
     * and content.
     */
constructor(originalStart,originalLength,modifiedStart,modifiedLength){
//Debug.Assert(originalLength > 0 || modifiedLength > 0, "originalLength and modifiedLength cannot both be <= 0");
this.originalStart=originalStart,this.originalLength=originalLength,this.modifiedStart=modifiedStart,this.modifiedLength=modifiedLength}
/**
     * The end point (exclusive) of the change in the original sequence.
     */getOriginalEnd(){return this.originalStart+this.originalLength}
/**
     * The end point (exclusive) of the change in the modified sequence.
     */getModifiedEnd(){return this.modifiedStart+this.modifiedLength}}
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/hash.js
var hash=__webpack_require__(89954);// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/diff/diff.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class StringDiffSequence{constructor(source){this.source=source}getElements(){const source=this.source,characters=new Int32Array(source.length);for(let i=0,len=source.length;i<len;i++)characters[i]=source.charCodeAt(i);return characters}}function stringDiff(original,modified,pretty){return new LcsDiff(new StringDiffSequence(original),new StringDiffSequence(modified)).ComputeDiff(pretty).changes}

// The code below has been ported from a C# implementation in VS

class Debug{static Assert(condition,message){if(!condition)throw new Error(message)}}class MyArray{
/**
     * Copies a range of elements from an Array starting at the specified source index and pastes
     * them to another Array starting at the specified destination index. The length and the indexes
     * are specified as 64-bit integers.
     * sourceArray:
     *		The Array that contains the data to copy.
     * sourceIndex:
     *		A 64-bit integer that represents the index in the sourceArray at which copying begins.
     * destinationArray:
     *		The Array that receives the data.
     * destinationIndex:
     *		A 64-bit integer that represents the index in the destinationArray at which storing begins.
     * length:
     *		A 64-bit integer that represents the number of elements to copy.
     */
static Copy(sourceArray,sourceIndex,destinationArray,destinationIndex,length){for(let i=0;i<length;i++)destinationArray[destinationIndex+i]=sourceArray[sourceIndex+i]}static Copy2(sourceArray,sourceIndex,destinationArray,destinationIndex,length){for(let i=0;i<length;i++)destinationArray[destinationIndex+i]=sourceArray[sourceIndex+i]}}
/**
 * A utility class which helps to create the set of DiffChanges from
 * a difference operation. This class accepts original DiffElements and
 * modified DiffElements that are involved in a particular change. The
 * MarkNextChange() method can be called to mark the separation between
 * distinct changes. At the end, the Changes property can be called to retrieve
 * the constructed changes.
 */class DiffChangeHelper{
/**
     * Constructs a new DiffChangeHelper for the given DiffSequences.
     */
constructor(){this.m_changes=[],this.m_originalStart=1073741824/* MAX_SAFE_SMALL_INTEGER */,this.m_modifiedStart=1073741824/* MAX_SAFE_SMALL_INTEGER */,this.m_originalCount=0,this.m_modifiedCount=0}
/**
     * Marks the beginning of the next change in the set of differences.
     */MarkNextChange(){
// Only add to the list if there is something to add
(this.m_originalCount>0||this.m_modifiedCount>0)&&
// Add the new change to our list
this.m_changes.push(new DiffChange(this.m_originalStart,this.m_originalCount,this.m_modifiedStart,this.m_modifiedCount)),
// Reset for the next change
this.m_originalCount=0,this.m_modifiedCount=0,this.m_originalStart=1073741824/* MAX_SAFE_SMALL_INTEGER */,this.m_modifiedStart=1073741824/* MAX_SAFE_SMALL_INTEGER */}
/**
     * Adds the original element at the given position to the elements
     * affected by the current change. The modified index gives context
     * to the change position with respect to the original sequence.
     * @param originalIndex The index of the original element to add.
     * @param modifiedIndex The index of the modified element that provides corresponding position in the modified sequence.
     */AddOriginalElement(originalIndex,modifiedIndex){
// The 'true' start index is the smallest of the ones we've seen
this.m_originalStart=Math.min(this.m_originalStart,originalIndex),this.m_modifiedStart=Math.min(this.m_modifiedStart,modifiedIndex),this.m_originalCount++}
/**
     * Adds the modified element at the given position to the elements
     * affected by the current change. The original index gives context
     * to the change position with respect to the modified sequence.
     * @param originalIndex The index of the original element that provides corresponding position in the original sequence.
     * @param modifiedIndex The index of the modified element to add.
     */AddModifiedElement(originalIndex,modifiedIndex){
// The 'true' start index is the smallest of the ones we've seen
this.m_originalStart=Math.min(this.m_originalStart,originalIndex),this.m_modifiedStart=Math.min(this.m_modifiedStart,modifiedIndex),this.m_modifiedCount++}
/**
     * Retrieves all of the changes marked by the class.
     */getChanges(){return(this.m_originalCount>0||this.m_modifiedCount>0)&&
// Finish up on whatever is left
this.MarkNextChange(),this.m_changes}
/**
     * Retrieves all of the changes marked by the class in the reverse order
     */getReverseChanges(){return(this.m_originalCount>0||this.m_modifiedCount>0)&&
// Finish up on whatever is left
this.MarkNextChange(),this.m_changes.reverse(),this.m_changes}}
/**
 * An implementation of the difference algorithm described in
 * "An O(ND) Difference Algorithm and its variations" by Eugene W. Myers
 */class LcsDiff{
/**
     * Constructs the DiffFinder
     */
constructor(originalSequence,modifiedSequence,continueProcessingPredicate=null){this.ContinueProcessingPredicate=continueProcessingPredicate,this._originalSequence=originalSequence,this._modifiedSequence=modifiedSequence;const[originalStringElements,originalElementsOrHash,originalHasStrings]=LcsDiff._getElements(originalSequence),[modifiedStringElements,modifiedElementsOrHash,modifiedHasStrings]=LcsDiff._getElements(modifiedSequence);this._hasStrings=originalHasStrings&&modifiedHasStrings,this._originalStringElements=originalStringElements,this._originalElementsOrHash=originalElementsOrHash,this._modifiedStringElements=modifiedStringElements,this._modifiedElementsOrHash=modifiedElementsOrHash,this.m_forwardHistory=[],this.m_reverseHistory=[]}static _isStringArray(arr){return arr.length>0&&"string"===typeof arr[0]}static _getElements(sequence){const elements=sequence.getElements();if(LcsDiff._isStringArray(elements)){const hashes=new Int32Array(elements.length);for(let i=0,len=elements.length;i<len;i++)hashes[i]=(0,hash/* stringHash */.Cv)(elements[i],0);return[elements,hashes,!0]}return elements instanceof Int32Array?[[],elements,!1]:[[],new Int32Array(elements),!1]}ElementsAreEqual(originalIndex,newIndex){return this._originalElementsOrHash[originalIndex]===this._modifiedElementsOrHash[newIndex]&&(!this._hasStrings||this._originalStringElements[originalIndex]===this._modifiedStringElements[newIndex])}ElementsAreStrictEqual(originalIndex,newIndex){if(!this.ElementsAreEqual(originalIndex,newIndex))return!1;const originalElement=LcsDiff._getStrictElement(this._originalSequence,originalIndex),modifiedElement=LcsDiff._getStrictElement(this._modifiedSequence,newIndex);return originalElement===modifiedElement}static _getStrictElement(sequence,index){return"function"===typeof sequence.getStrictElement?sequence.getStrictElement(index):null}OriginalElementsAreEqual(index1,index2){return this._originalElementsOrHash[index1]===this._originalElementsOrHash[index2]&&(!this._hasStrings||this._originalStringElements[index1]===this._originalStringElements[index2])}ModifiedElementsAreEqual(index1,index2){return this._modifiedElementsOrHash[index1]===this._modifiedElementsOrHash[index2]&&(!this._hasStrings||this._modifiedStringElements[index1]===this._modifiedStringElements[index2])}ComputeDiff(pretty){return this._ComputeDiff(0,this._originalElementsOrHash.length-1,0,this._modifiedElementsOrHash.length-1,pretty)}
/**
     * Computes the differences between the original and modified input
     * sequences on the bounded range.
     * @returns An array of the differences between the two input sequences.
     */_ComputeDiff(originalStart,originalEnd,modifiedStart,modifiedEnd,pretty){const quitEarlyArr=[!1];let changes=this.ComputeDiffRecursive(originalStart,originalEnd,modifiedStart,modifiedEnd,quitEarlyArr);return pretty&&(
// We have to clean up the computed diff to be more intuitive
// but it turns out this cannot be done correctly until the entire set
// of diffs have been computed
changes=this.PrettifyChanges(changes)),{quitEarly:quitEarlyArr[0],changes:changes}}
/**
     * Private helper method which computes the differences on the bounded range
     * recursively.
     * @returns An array of the differences between the two input sequences.
     */ComputeDiffRecursive(originalStart,originalEnd,modifiedStart,modifiedEnd,quitEarlyArr){quitEarlyArr[0]=!1;
// Find the start of the differences
while(originalStart<=originalEnd&&modifiedStart<=modifiedEnd&&this.ElementsAreEqual(originalStart,modifiedStart))originalStart++,modifiedStart++;
// Find the end of the differences
while(originalEnd>=originalStart&&modifiedEnd>=modifiedStart&&this.ElementsAreEqual(originalEnd,modifiedEnd))originalEnd--,modifiedEnd--;
// In the special case where we either have all insertions or all deletions or the sequences are identical
if(originalStart>originalEnd||modifiedStart>modifiedEnd){let changes;return modifiedStart<=modifiedEnd?(Debug.Assert(originalStart===originalEnd+1,"originalStart should only be one more than originalEnd"),
// All insertions
changes=[new DiffChange(originalStart,0,modifiedStart,modifiedEnd-modifiedStart+1)]):originalStart<=originalEnd?(Debug.Assert(modifiedStart===modifiedEnd+1,"modifiedStart should only be one more than modifiedEnd"),
// All deletions
changes=[new DiffChange(originalStart,originalEnd-originalStart+1,modifiedStart,0)]):(Debug.Assert(originalStart===originalEnd+1,"originalStart should only be one more than originalEnd"),Debug.Assert(modifiedStart===modifiedEnd+1,"modifiedStart should only be one more than modifiedEnd"),
// Identical sequences - No differences
changes=[]),changes}
// This problem can be solved using the Divide-And-Conquer technique.
const midOriginalArr=[0],midModifiedArr=[0],result=this.ComputeRecursionPoint(originalStart,originalEnd,modifiedStart,modifiedEnd,midOriginalArr,midModifiedArr,quitEarlyArr),midOriginal=midOriginalArr[0],midModified=midModifiedArr[0];if(null!==result)
// Result is not-null when there was enough memory to compute the changes while
// searching for the recursion point
return result;
// If we hit here, we quit early, and so can't return anything meaningful
if(!quitEarlyArr[0]){
// We can break the problem down recursively by finding the changes in the
// First Half:   (originalStart, modifiedStart) to (midOriginal, midModified)
// Second Half:  (midOriginal + 1, minModified + 1) to (originalEnd, modifiedEnd)
// NOTE: ComputeDiff() is inclusive, therefore the second range starts on the next point
const leftChanges=this.ComputeDiffRecursive(originalStart,midOriginal,modifiedStart,midModified,quitEarlyArr);let rightChanges=[];
// We didn't have time to finish the first half, so we don't have time to compute this half.
// Consider the entire rest of the sequence different.
return rightChanges=quitEarlyArr[0]?[new DiffChange(midOriginal+1,originalEnd-(midOriginal+1)+1,midModified+1,modifiedEnd-(midModified+1)+1)]:this.ComputeDiffRecursive(midOriginal+1,originalEnd,midModified+1,modifiedEnd,quitEarlyArr),this.ConcatenateChanges(leftChanges,rightChanges)}return[new DiffChange(originalStart,originalEnd-originalStart+1,modifiedStart,modifiedEnd-modifiedStart+1)]}WALKTRACE(diagonalForwardBase,diagonalForwardStart,diagonalForwardEnd,diagonalForwardOffset,diagonalReverseBase,diagonalReverseStart,diagonalReverseEnd,diagonalReverseOffset,forwardPoints,reversePoints,originalIndex,originalEnd,midOriginalArr,modifiedIndex,modifiedEnd,midModifiedArr,deltaIsEven,quitEarlyArr){let forwardChanges=null,reverseChanges=null,changeHelper=new DiffChangeHelper,diagonalMin=diagonalForwardStart,diagonalMax=diagonalForwardEnd,diagonalRelative=midOriginalArr[0]-midModifiedArr[0]-diagonalForwardOffset,lastOriginalIndex=-1073741824/* MIN_SAFE_SMALL_INTEGER */,historyIndex=this.m_forwardHistory.length-1;do{
// Get the diagonal index from the relative diagonal number
const diagonal=diagonalRelative+diagonalForwardBase;
// Figure out where we came from
diagonal===diagonalMin||diagonal<diagonalMax&&forwardPoints[diagonal-1]<forwardPoints[diagonal+1]?(
// Vertical line (the element is an insert)
originalIndex=forwardPoints[diagonal+1],modifiedIndex=originalIndex-diagonalRelative-diagonalForwardOffset,originalIndex<lastOriginalIndex&&changeHelper.MarkNextChange(),lastOriginalIndex=originalIndex,changeHelper.AddModifiedElement(originalIndex+1,modifiedIndex),diagonalRelative=diagonal+1-diagonalForwardBase):(
// Horizontal line (the element is a deletion)
originalIndex=forwardPoints[diagonal-1]+1,modifiedIndex=originalIndex-diagonalRelative-diagonalForwardOffset,originalIndex<lastOriginalIndex&&changeHelper.MarkNextChange(),lastOriginalIndex=originalIndex-1,changeHelper.AddOriginalElement(originalIndex,modifiedIndex+1),diagonalRelative=diagonal-1-diagonalForwardBase),historyIndex>=0&&(forwardPoints=this.m_forwardHistory[historyIndex],diagonalForwardBase=forwardPoints[0],//We stored this in the first spot
diagonalMin=1,diagonalMax=forwardPoints.length-1)}while(--historyIndex>=-1);
// Ironically, we get the forward changes as the reverse of the
// order we added them since we technically added them backwards
if(forwardChanges=changeHelper.getReverseChanges(),quitEarlyArr[0]){
// TODO: Calculate a partial from the reverse diagonals.
//       For now, just assume everything after the midOriginal/midModified point is a diff
let originalStartPoint=midOriginalArr[0]+1,modifiedStartPoint=midModifiedArr[0]+1;if(null!==forwardChanges&&forwardChanges.length>0){const lastForwardChange=forwardChanges[forwardChanges.length-1];originalStartPoint=Math.max(originalStartPoint,lastForwardChange.getOriginalEnd()),modifiedStartPoint=Math.max(modifiedStartPoint,lastForwardChange.getModifiedEnd())}reverseChanges=[new DiffChange(originalStartPoint,originalEnd-originalStartPoint+1,modifiedStartPoint,modifiedEnd-modifiedStartPoint+1)]}else{
// Now walk backward through the reverse diagonals history
changeHelper=new DiffChangeHelper,diagonalMin=diagonalReverseStart,diagonalMax=diagonalReverseEnd,diagonalRelative=midOriginalArr[0]-midModifiedArr[0]-diagonalReverseOffset,lastOriginalIndex=1073741824/* MAX_SAFE_SMALL_INTEGER */,historyIndex=deltaIsEven?this.m_reverseHistory.length-1:this.m_reverseHistory.length-2;do{
// Get the diagonal index from the relative diagonal number
const diagonal=diagonalRelative+diagonalReverseBase;
// Figure out where we came from
diagonal===diagonalMin||diagonal<diagonalMax&&reversePoints[diagonal-1]>=reversePoints[diagonal+1]?(
// Horizontal line (the element is a deletion))
originalIndex=reversePoints[diagonal+1]-1,modifiedIndex=originalIndex-diagonalRelative-diagonalReverseOffset,originalIndex>lastOriginalIndex&&changeHelper.MarkNextChange(),lastOriginalIndex=originalIndex+1,changeHelper.AddOriginalElement(originalIndex+1,modifiedIndex+1),diagonalRelative=diagonal+1-diagonalReverseBase):(
// Vertical line (the element is an insertion)
originalIndex=reversePoints[diagonal-1],modifiedIndex=originalIndex-diagonalRelative-diagonalReverseOffset,originalIndex>lastOriginalIndex&&changeHelper.MarkNextChange(),lastOriginalIndex=originalIndex,changeHelper.AddModifiedElement(originalIndex+1,modifiedIndex+1),diagonalRelative=diagonal-1-diagonalReverseBase),historyIndex>=0&&(reversePoints=this.m_reverseHistory[historyIndex],diagonalReverseBase=reversePoints[0],//We stored this in the first spot
diagonalMin=1,diagonalMax=reversePoints.length-1)}while(--historyIndex>=-1);
// There are cases where the reverse history will find diffs that
// are correct, but not intuitive, so we need shift them.
reverseChanges=changeHelper.getChanges()}return this.ConcatenateChanges(forwardChanges,reverseChanges)}
/**
     * Given the range to compute the diff on, this method finds the point:
     * (midOriginal, midModified)
     * that exists in the middle of the LCS of the two sequences and
     * is the point at which the LCS problem may be broken down recursively.
     * This method will try to keep the LCS trace in memory. If the LCS recursion
     * point is calculated and the full trace is available in memory, then this method
     * will return the change list.
     * @param originalStart The start bound of the original sequence range
     * @param originalEnd The end bound of the original sequence range
     * @param modifiedStart The start bound of the modified sequence range
     * @param modifiedEnd The end bound of the modified sequence range
     * @param midOriginal The middle point of the original sequence range
     * @param midModified The middle point of the modified sequence range
     * @returns The diff changes, if available, otherwise null
     */ComputeRecursionPoint(originalStart,originalEnd,modifiedStart,modifiedEnd,midOriginalArr,midModifiedArr,quitEarlyArr){let originalIndex=0,modifiedIndex=0,diagonalForwardStart=0,diagonalForwardEnd=0,diagonalReverseStart=0,diagonalReverseEnd=0;
// To traverse the edit graph and produce the proper LCS, our actual
// start position is just outside the given boundary
originalStart--,modifiedStart--,
// We set these up to make the compiler happy, but they will
// be replaced before we return with the actual recursion point
midOriginalArr[0]=0,midModifiedArr[0]=0,
// Clear out the history
this.m_forwardHistory=[],this.m_reverseHistory=[];
// Each cell in the two arrays corresponds to a diagonal in the edit graph.
// The integer value in the cell represents the originalIndex of the furthest
// reaching point found so far that ends in that diagonal.
// The modifiedIndex can be computed mathematically from the originalIndex and the diagonal number.
const maxDifferences=originalEnd-originalStart+(modifiedEnd-modifiedStart),numDiagonals=maxDifferences+1,forwardPoints=new Int32Array(numDiagonals),reversePoints=new Int32Array(numDiagonals),diagonalForwardBase=modifiedEnd-modifiedStart,diagonalReverseBase=originalEnd-originalStart,diagonalForwardOffset=originalStart-modifiedStart,diagonalReverseOffset=originalEnd-modifiedEnd,delta=diagonalReverseBase-diagonalForwardBase,deltaIsEven=delta%2===0;
// Here we set up the start and end points as the furthest points found so far
// in both the forward and reverse directions, respectively
forwardPoints[diagonalForwardBase]=originalStart,reversePoints[diagonalReverseBase]=originalEnd,
// Remember if we quit early, and thus need to do a best-effort result instead of a real result.
quitEarlyArr[0]=!1;
// A couple of points:
// --With this method, we iterate on the number of differences between the two sequences.
//   The more differences there actually are, the longer this will take.
// --Also, as the number of differences increases, we have to search on diagonals further
//   away from the reference diagonal (which is diagonalForwardBase for forward, diagonalReverseBase for reverse).
// --We extend on even diagonals (relative to the reference diagonal) only when numDifferences
//   is even and odd diagonals only when numDifferences is odd.
for(let numDifferences=1;numDifferences<=maxDifferences/2+1;numDifferences++){let furthestOriginalIndex=0,furthestModifiedIndex=0;
// Run the algorithm in the forward direction
diagonalForwardStart=this.ClipDiagonalBound(diagonalForwardBase-numDifferences,numDifferences,diagonalForwardBase,numDiagonals),diagonalForwardEnd=this.ClipDiagonalBound(diagonalForwardBase+numDifferences,numDifferences,diagonalForwardBase,numDiagonals);for(let diagonal=diagonalForwardStart;diagonal<=diagonalForwardEnd;diagonal+=2){
// STEP 1: We extend the furthest reaching point in the present diagonal
// by looking at the diagonals above and below and picking the one whose point
// is further away from the start point (originalStart, modifiedStart)
originalIndex=diagonal===diagonalForwardStart||diagonal<diagonalForwardEnd&&forwardPoints[diagonal-1]<forwardPoints[diagonal+1]?forwardPoints[diagonal+1]:forwardPoints[diagonal-1]+1,modifiedIndex=originalIndex-(diagonal-diagonalForwardBase)-diagonalForwardOffset;
// Save the current originalIndex so we can test for false overlap in step 3
const tempOriginalIndex=originalIndex;
// STEP 2: We can continue to extend the furthest reaching point in the present diagonal
// so long as the elements are equal.
while(originalIndex<originalEnd&&modifiedIndex<modifiedEnd&&this.ElementsAreEqual(originalIndex+1,modifiedIndex+1))originalIndex++,modifiedIndex++;
// STEP 3: If delta is odd (overlap first happens on forward when delta is odd)
// and diagonal is in the range of reverse diagonals computed for numDifferences-1
// (the previous iteration; we haven't computed reverse diagonals for numDifferences yet)
// then check for overlap.
if(forwardPoints[diagonal]=originalIndex,originalIndex+modifiedIndex>furthestOriginalIndex+furthestModifiedIndex&&(furthestOriginalIndex=originalIndex,furthestModifiedIndex=modifiedIndex),!deltaIsEven&&Math.abs(diagonal-diagonalReverseBase)<=numDifferences-1&&originalIndex>=reversePoints[diagonal])return midOriginalArr[0]=originalIndex,midModifiedArr[0]=modifiedIndex,tempOriginalIndex<=reversePoints[diagonal]&&numDifferences<=1448?this.WALKTRACE(diagonalForwardBase,diagonalForwardStart,diagonalForwardEnd,diagonalForwardOffset,diagonalReverseBase,diagonalReverseStart,diagonalReverseEnd,diagonalReverseOffset,forwardPoints,reversePoints,originalIndex,originalEnd,midOriginalArr,modifiedIndex,modifiedEnd,midModifiedArr,deltaIsEven,quitEarlyArr):null}
// Check to see if we should be quitting early, before moving on to the next iteration.
const matchLengthOfLongest=(furthestOriginalIndex-originalStart+(furthestModifiedIndex-modifiedStart)-numDifferences)/2;if(null!==this.ContinueProcessingPredicate&&!this.ContinueProcessingPredicate(furthestOriginalIndex,matchLengthOfLongest))
// We can't finish, so skip ahead to generating a result from what we have.
return quitEarlyArr[0]=!0,
// Use the furthest distance we got in the forward direction.
midOriginalArr[0]=furthestOriginalIndex,midModifiedArr[0]=furthestModifiedIndex,matchLengthOfLongest>0&&numDifferences<=1448?this.WALKTRACE(diagonalForwardBase,diagonalForwardStart,diagonalForwardEnd,diagonalForwardOffset,diagonalReverseBase,diagonalReverseStart,diagonalReverseEnd,diagonalReverseOffset,forwardPoints,reversePoints,originalIndex,originalEnd,midOriginalArr,modifiedIndex,modifiedEnd,midModifiedArr,deltaIsEven,quitEarlyArr):(
// We didn't actually remember enough of the history.
//Since we are quitting the diff early, we need to shift back the originalStart and modified start
//back into the boundary limits since we decremented their value above beyond the boundary limit.
originalStart++,modifiedStart++,[new DiffChange(originalStart,originalEnd-originalStart+1,modifiedStart,modifiedEnd-modifiedStart+1)]);
// Run the algorithm in the reverse direction
diagonalReverseStart=this.ClipDiagonalBound(diagonalReverseBase-numDifferences,numDifferences,diagonalReverseBase,numDiagonals),diagonalReverseEnd=this.ClipDiagonalBound(diagonalReverseBase+numDifferences,numDifferences,diagonalReverseBase,numDiagonals);for(let diagonal=diagonalReverseStart;diagonal<=diagonalReverseEnd;diagonal+=2){
// STEP 1: We extend the furthest reaching point in the present diagonal
// by looking at the diagonals above and below and picking the one whose point
// is further away from the start point (originalEnd, modifiedEnd)
originalIndex=diagonal===diagonalReverseStart||diagonal<diagonalReverseEnd&&reversePoints[diagonal-1]>=reversePoints[diagonal+1]?reversePoints[diagonal+1]-1:reversePoints[diagonal-1],modifiedIndex=originalIndex-(diagonal-diagonalReverseBase)-diagonalReverseOffset;
// Save the current originalIndex so we can test for false overlap
const tempOriginalIndex=originalIndex;
// STEP 2: We can continue to extend the furthest reaching point in the present diagonal
// as long as the elements are equal.
while(originalIndex>originalStart&&modifiedIndex>modifiedStart&&this.ElementsAreEqual(originalIndex,modifiedIndex))originalIndex--,modifiedIndex--;
// STEP 4: If delta is even (overlap first happens on reverse when delta is even)
// and diagonal is in the range of forward diagonals computed for numDifferences
// then check for overlap.
if(reversePoints[diagonal]=originalIndex,deltaIsEven&&Math.abs(diagonal-diagonalForwardBase)<=numDifferences&&originalIndex<=forwardPoints[diagonal])return midOriginalArr[0]=originalIndex,midModifiedArr[0]=modifiedIndex,tempOriginalIndex>=forwardPoints[diagonal]&&numDifferences<=1448?this.WALKTRACE(diagonalForwardBase,diagonalForwardStart,diagonalForwardEnd,diagonalForwardOffset,diagonalReverseBase,diagonalReverseStart,diagonalReverseEnd,diagonalReverseOffset,forwardPoints,reversePoints,originalIndex,originalEnd,midOriginalArr,modifiedIndex,modifiedEnd,midModifiedArr,deltaIsEven,quitEarlyArr):null}
// Save current vectors to history before the next iteration
if(numDifferences<=1447/* MaxDifferencesHistory */){
// We are allocating space for one extra int, which we fill with
// the index of the diagonal base index
let temp=new Int32Array(diagonalForwardEnd-diagonalForwardStart+2);temp[0]=diagonalForwardBase-diagonalForwardStart+1,MyArray.Copy2(forwardPoints,diagonalForwardStart,temp,1,diagonalForwardEnd-diagonalForwardStart+1),this.m_forwardHistory.push(temp),temp=new Int32Array(diagonalReverseEnd-diagonalReverseStart+2),temp[0]=diagonalReverseBase-diagonalReverseStart+1,MyArray.Copy2(reversePoints,diagonalReverseStart,temp,1,diagonalReverseEnd-diagonalReverseStart+1),this.m_reverseHistory.push(temp)}}
// If we got here, then we have the full trace in history. We just have to convert it to a change list
// NOTE: This part is a bit messy
return this.WALKTRACE(diagonalForwardBase,diagonalForwardStart,diagonalForwardEnd,diagonalForwardOffset,diagonalReverseBase,diagonalReverseStart,diagonalReverseEnd,diagonalReverseOffset,forwardPoints,reversePoints,originalIndex,originalEnd,midOriginalArr,modifiedIndex,modifiedEnd,midModifiedArr,deltaIsEven,quitEarlyArr)}
/**
     * Shifts the given changes to provide a more intuitive diff.
     * While the first element in a diff matches the first element after the diff,
     * we shift the diff down.
     *
     * @param changes The list of changes to shift
     * @returns The shifted changes
     */PrettifyChanges(changes){
// Shift all the changes down first
for(let i=0;i<changes.length;i++){const change=changes[i],originalStop=i<changes.length-1?changes[i+1].originalStart:this._originalElementsOrHash.length,modifiedStop=i<changes.length-1?changes[i+1].modifiedStart:this._modifiedElementsOrHash.length,checkOriginal=change.originalLength>0,checkModified=change.modifiedLength>0;while(change.originalStart+change.originalLength<originalStop&&change.modifiedStart+change.modifiedLength<modifiedStop&&(!checkOriginal||this.OriginalElementsAreEqual(change.originalStart,change.originalStart+change.originalLength))&&(!checkModified||this.ModifiedElementsAreEqual(change.modifiedStart,change.modifiedStart+change.modifiedLength))){const startStrictEqual=this.ElementsAreStrictEqual(change.originalStart,change.modifiedStart),endStrictEqual=this.ElementsAreStrictEqual(change.originalStart+change.originalLength,change.modifiedStart+change.modifiedLength);if(endStrictEqual&&!startStrictEqual)
// moving the change down would create an equal change, but the elements are not strict equal
break;change.originalStart++,change.modifiedStart++}let mergedChangeArr=[null];i<changes.length-1&&this.ChangesOverlap(changes[i],changes[i+1],mergedChangeArr)&&(changes[i]=mergedChangeArr[0],changes.splice(i+1,1),i--)}
// Shift changes back up until we hit empty or whitespace-only lines
for(let i=changes.length-1;i>=0;i--){const change=changes[i];let originalStop=0,modifiedStop=0;if(i>0){const prevChange=changes[i-1];originalStop=prevChange.originalStart+prevChange.originalLength,modifiedStop=prevChange.modifiedStart+prevChange.modifiedLength}const checkOriginal=change.originalLength>0,checkModified=change.modifiedLength>0;let bestDelta=0,bestScore=this._boundaryScore(change.originalStart,change.originalLength,change.modifiedStart,change.modifiedLength);for(let delta=1;;delta++){const originalStart=change.originalStart-delta,modifiedStart=change.modifiedStart-delta;if(originalStart<originalStop||modifiedStart<modifiedStop)break;if(checkOriginal&&!this.OriginalElementsAreEqual(originalStart,originalStart+change.originalLength))break;if(checkModified&&!this.ModifiedElementsAreEqual(modifiedStart,modifiedStart+change.modifiedLength))break;const touchingPreviousChange=originalStart===originalStop&&modifiedStart===modifiedStop,score=(touchingPreviousChange?5:0)+this._boundaryScore(originalStart,change.originalLength,modifiedStart,change.modifiedLength);score>bestScore&&(bestScore=score,bestDelta=delta)}change.originalStart-=bestDelta,change.modifiedStart-=bestDelta;const mergedChangeArr=[null];i>0&&this.ChangesOverlap(changes[i-1],changes[i],mergedChangeArr)&&(changes[i-1]=mergedChangeArr[0],changes.splice(i,1),i++)}
// There could be multiple longest common substrings.
// Give preference to the ones containing longer lines
if(this._hasStrings)for(let i=1,len=changes.length;i<len;i++){const aChange=changes[i-1],bChange=changes[i],matchedLength=bChange.originalStart-aChange.originalStart-aChange.originalLength,aOriginalStart=aChange.originalStart,bOriginalEnd=bChange.originalStart+bChange.originalLength,abOriginalLength=bOriginalEnd-aOriginalStart,aModifiedStart=aChange.modifiedStart,bModifiedEnd=bChange.modifiedStart+bChange.modifiedLength,abModifiedLength=bModifiedEnd-aModifiedStart;
// Avoid wasting a lot of time with these searches
if(matchedLength<5&&abOriginalLength<20&&abModifiedLength<20){const t=this._findBetterContiguousSequence(aOriginalStart,abOriginalLength,aModifiedStart,abModifiedLength,matchedLength);if(t){const[originalMatchStart,modifiedMatchStart]=t;originalMatchStart===aChange.originalStart+aChange.originalLength&&modifiedMatchStart===aChange.modifiedStart+aChange.modifiedLength||(
// switch to another sequence that has a better score
aChange.originalLength=originalMatchStart-aChange.originalStart,aChange.modifiedLength=modifiedMatchStart-aChange.modifiedStart,bChange.originalStart=originalMatchStart+matchedLength,bChange.modifiedStart=modifiedMatchStart+matchedLength,bChange.originalLength=bOriginalEnd-bChange.originalStart,bChange.modifiedLength=bModifiedEnd-bChange.modifiedStart)}}}return changes}_findBetterContiguousSequence(originalStart,originalLength,modifiedStart,modifiedLength,desiredLength){if(originalLength<desiredLength||modifiedLength<desiredLength)return null;const originalMax=originalStart+originalLength-desiredLength+1,modifiedMax=modifiedStart+modifiedLength-desiredLength+1;let bestScore=0,bestOriginalStart=0,bestModifiedStart=0;for(let i=originalStart;i<originalMax;i++)for(let j=modifiedStart;j<modifiedMax;j++){const score=this._contiguousSequenceScore(i,j,desiredLength);score>0&&score>bestScore&&(bestScore=score,bestOriginalStart=i,bestModifiedStart=j)}return bestScore>0?[bestOriginalStart,bestModifiedStart]:null}_contiguousSequenceScore(originalStart,modifiedStart,length){let score=0;for(let l=0;l<length;l++){if(!this.ElementsAreEqual(originalStart+l,modifiedStart+l))return 0;score+=this._originalStringElements[originalStart+l].length}return score}_OriginalIsBoundary(index){return index<=0||index>=this._originalElementsOrHash.length-1||this._hasStrings&&/^\s*$/.test(this._originalStringElements[index])}_OriginalRegionIsBoundary(originalStart,originalLength){if(this._OriginalIsBoundary(originalStart)||this._OriginalIsBoundary(originalStart-1))return!0;if(originalLength>0){const originalEnd=originalStart+originalLength;if(this._OriginalIsBoundary(originalEnd-1)||this._OriginalIsBoundary(originalEnd))return!0}return!1}_ModifiedIsBoundary(index){return index<=0||index>=this._modifiedElementsOrHash.length-1||this._hasStrings&&/^\s*$/.test(this._modifiedStringElements[index])}_ModifiedRegionIsBoundary(modifiedStart,modifiedLength){if(this._ModifiedIsBoundary(modifiedStart)||this._ModifiedIsBoundary(modifiedStart-1))return!0;if(modifiedLength>0){const modifiedEnd=modifiedStart+modifiedLength;if(this._ModifiedIsBoundary(modifiedEnd-1)||this._ModifiedIsBoundary(modifiedEnd))return!0}return!1}_boundaryScore(originalStart,originalLength,modifiedStart,modifiedLength){const originalScore=this._OriginalRegionIsBoundary(originalStart,originalLength)?1:0,modifiedScore=this._ModifiedRegionIsBoundary(modifiedStart,modifiedLength)?1:0;return originalScore+modifiedScore}
/**
     * Concatenates the two input DiffChange lists and returns the resulting
     * list.
     * @param The left changes
     * @param The right changes
     * @returns The concatenated list
     */ConcatenateChanges(left,right){let mergedChangeArr=[];if(0===left.length||0===right.length)return right.length>0?right:left;if(this.ChangesOverlap(left[left.length-1],right[0],mergedChangeArr)){
// Since we break the problem down recursively, it is possible that we
// might recurse in the middle of a change thereby splitting it into
// two changes. Here in the combining stage, we detect and fuse those
// changes back together
const result=new Array(left.length+right.length-1);return MyArray.Copy(left,0,result,0,left.length-1),result[left.length-1]=mergedChangeArr[0],MyArray.Copy(right,1,result,left.length,right.length-1),result}{const result=new Array(left.length+right.length);return MyArray.Copy(left,0,result,0,left.length),MyArray.Copy(right,0,result,left.length,right.length),result}}
/**
     * Returns true if the two changes overlap and can be merged into a single
     * change
     * @param left The left change
     * @param right The right change
     * @param mergedChange The merged change if the two overlap, null otherwise
     * @returns True if the two changes overlap
     */ChangesOverlap(left,right,mergedChangeArr){if(Debug.Assert(left.originalStart<=right.originalStart,"Left change is not less than or equal to right change"),Debug.Assert(left.modifiedStart<=right.modifiedStart,"Left change is not less than or equal to right change"),left.originalStart+left.originalLength>=right.originalStart||left.modifiedStart+left.modifiedLength>=right.modifiedStart){const originalStart=left.originalStart;let originalLength=left.originalLength;const modifiedStart=left.modifiedStart;let modifiedLength=left.modifiedLength;return left.originalStart+left.originalLength>=right.originalStart&&(originalLength=right.originalStart+right.originalLength-left.originalStart),left.modifiedStart+left.modifiedLength>=right.modifiedStart&&(modifiedLength=right.modifiedStart+right.modifiedLength-left.modifiedStart),mergedChangeArr[0]=new DiffChange(originalStart,originalLength,modifiedStart,modifiedLength),!0}return mergedChangeArr[0]=null,!1}
/**
     * Helper method used to clip a diagonal index to the range of valid
     * diagonals. This also decides whether or not the diagonal index,
     * if it exceeds the boundary, should be clipped to the boundary or clipped
     * one inside the boundary depending on the Even/Odd status of the boundary
     * and numDifferences.
     * @param diagonal The index of the diagonal to clip.
     * @param numDifferences The current number of differences being iterated upon.
     * @param diagonalBaseIndex The base reference diagonal.
     * @param numDiagonals The total number of diagonals.
     * @returns The clipped diagonal index.
     */ClipDiagonalBound(diagonal,numDifferences,diagonalBaseIndex,numDiagonals){if(diagonal>=0&&diagonal<numDiagonals)
// Nothing to clip, its in range
return diagonal;
// diagonalsBelow: The number of diagonals below the reference diagonal
// diagonalsAbove: The number of diagonals above the reference diagonal
const diagonalsBelow=diagonalBaseIndex,diagonalsAbove=numDiagonals-diagonalBaseIndex-1,diffEven=numDifferences%2===0;if(diagonal<0){const lowerBoundEven=diagonalsBelow%2===0;return diffEven===lowerBoundEven?0:1}{const upperBoundEven=diagonalsAbove%2===0;return diffEven===upperBoundEven?numDiagonals-1:numDiagonals-2}}}
/***/},
/***/159523:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */y:function(){/* binding */return toErrorMessage}
/* harmony export */});
/* harmony import */var _arrays_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(609488),_types_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(998401),_nls_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(663580);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function exceptionToErrorMessage(exception,verbose){return verbose&&(exception.stack||exception.stacktrace)?_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("stackTrace.format","{0}: {1}",detectSystemErrorMessage(exception),stackToString(exception.stack)||stackToString(exception.stacktrace)):detectSystemErrorMessage(exception)}function stackToString(stack){return Array.isArray(stack)?stack.join("\n"):stack}function detectSystemErrorMessage(exception){
// See https://nodejs.org/api/errors.html#errors_class_system_error
return"string"===typeof exception.code&&"number"===typeof exception.errno&&"string"===typeof exception.syscall?_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("nodeExceptionMessage","A system error occurred ({0})",exception.message):exception.message||_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("error.defaultMessage","An unknown error occurred. Please consult the log for more details.")}
/**
 * Tries to generate a human readable error message out of the error. If the verbose parameter
 * is set to true, the error message will include stacktrace details if provided.
 *
 * @returns A string containing the error message.
 */function toErrorMessage(error=null,verbose=!1){if(!error)return _nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("error.defaultMessage","An unknown error occurred. Please consult the log for more details.");if(Array.isArray(error)){const errors=_arrays_js__WEBPACK_IMPORTED_MODULE_1__/* .coalesce */.kX(error),msg=toErrorMessage(errors[0],verbose);return errors.length>1?_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("error.moreErrors","{0} ({1} errors in total)",msg,errors.length):msg}if(_types_js__WEBPACK_IMPORTED_MODULE_2__/* .isString */.HD(error))return error;if(error.detail){const detail=error.detail;if(detail.error)return exceptionToErrorMessage(detail.error,verbose);if(detail.exception)return exceptionToErrorMessage(detail.exception,verbose)}return error.stack?exceptionToErrorMessage(error,verbose):error.message?error.message:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N("error.defaultMessage","An unknown error occurred. Please consult the log for more details.")}
/***/},
/***/817301:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */B8:function(){/* binding */return NotSupportedError},
/* harmony export */Cp:function(){/* binding */return onUnexpectedExternalError},
/* harmony export */F0:function(){/* binding */return canceled},
/* harmony export */L6:function(){/* binding */return illegalState},
/* harmony export */VV:function(){/* binding */return isPromiseCanceledError},
/* harmony export */b1:function(){/* binding */return illegalArgument},
/* harmony export */dL:function(){/* binding */return onUnexpectedError},
/* harmony export */ri:function(){/* binding */return transformErrorForSerialization}
/* harmony export */});
/* unused harmony exports ErrorHandler, errorHandler */
// Avoid circular dependency on EventEmitter by implementing a subset of the interface.
class ErrorHandler{constructor(){this.listeners=[],this.unexpectedErrorHandler=function(e){setTimeout((()=>{if(e.stack)throw new Error(e.message+"\n\n"+e.stack);throw e}),0)}}emit(e){this.listeners.forEach((listener=>{listener(e)}))}onUnexpectedError(e){this.unexpectedErrorHandler(e),this.emit(e)}
// For external errors, we don't want the listeners to be called
onUnexpectedExternalError(e){this.unexpectedErrorHandler(e)}}const errorHandler=new ErrorHandler;function onUnexpectedError(e){
// ignore errors from cancelled promises
isPromiseCanceledError(e)||errorHandler.onUnexpectedError(e)}function onUnexpectedExternalError(e){
// ignore errors from cancelled promises
isPromiseCanceledError(e)||errorHandler.onUnexpectedExternalError(e)}function transformErrorForSerialization(error){if(error instanceof Error){let{name:name,message:message}=error;const stack=error.stacktrace||error.stack;return{$isError:!0,name:name,message:message,stack:stack}}
// return as is
return error}const canceledName="Canceled";
/**
 * Checks if the given error is a promise in canceled state
 */function isPromiseCanceledError(error){return error instanceof Error&&error.name===canceledName&&error.message===canceledName}
/**
 * Returns an error that signals cancellation.
 */function canceled(){const error=new Error(canceledName);return error.name=error.message,error}function illegalArgument(name){return name?new Error(`Illegal argument: ${name}`):new Error("Illegal argument")}function illegalState(name){return name?new Error(`Illegal state: ${name}`):new Error("Illegal state")}class NotSupportedError extends Error{constructor(message){super("NotSupported"),message&&(this.message=message)}}
/***/},
/***/104669:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */D0:function(){/* binding */return DebounceEmitter},
/* harmony export */E7:function(){/* binding */return EventBufferer},
/* harmony export */K3:function(){/* binding */return PauseableEmitter},
/* harmony export */Q5:function(){/* binding */return Emitter},
/* harmony export */ZD:function(){/* binding */return Relay},
/* harmony export */ju:function(){/* binding */return Event}
/* harmony export */});
/* harmony import */var Event,_errors_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(817301),_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(905976),_linkedList_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(791741),_stopwatch_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(84013);
/* harmony import */(function(Event){
/**
     * Given an event, returns another event which only fires once.
     */
function once(event){return(listener,thisArgs=null,disposables)=>{
// we need this, in case the event fires during the listener call
let result,didFire=!1;return result=event((e=>{if(!didFire)return result?result.dispose():didFire=!0,listener.call(thisArgs,e)}),null,disposables),didFire&&result.dispose(),result}}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function map(event,map){return snapshot(((listener,thisArgs=null,disposables)=>event((i=>listener.call(thisArgs,map(i))),null,disposables)))}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function forEach(event,each){return snapshot(((listener,thisArgs=null,disposables)=>event((i=>{each(i),listener.call(thisArgs,i)}),null,disposables)))}function filter(event,filter){return snapshot(((listener,thisArgs=null,disposables)=>event((e=>filter(e)&&listener.call(thisArgs,e)),null,disposables)))}
/**
     * Given an event, returns the same event but typed as `Event<void>`.
     */
function signal(event){return event}function any(...events){return(listener,thisArgs=null,disposables)=>(0,_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .combinedDisposable */.F8)(...events.map((event=>event((e=>listener.call(thisArgs,e)),null,disposables))))}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function reduce(event,merge,initial){let output=initial;return map(event,(e=>(output=merge(output,e),output)))}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function snapshot(event){let listener;const emitter=new Emitter({onFirstListenerAdd(){listener=event(emitter.fire,emitter)},onLastListenerRemove(){listener.dispose()}});return emitter.event}
/**
     * @deprecated DO NOT use, this leaks memory
     */function debounce(event,merge,delay=100,leading=!1,leakWarningThreshold){let subscription,output,handle,numDebouncedCalls=0;const emitter=new Emitter({leakWarningThreshold:leakWarningThreshold,onFirstListenerAdd(){subscription=event((cur=>{numDebouncedCalls++,output=merge(output,cur),leading&&!handle&&(emitter.fire(output),output=void 0),clearTimeout(handle),handle=setTimeout((()=>{const _output=output;output=void 0,handle=void 0,(!leading||numDebouncedCalls>1)&&emitter.fire(_output),numDebouncedCalls=0}),delay)}))},onLastListenerRemove(){subscription.dispose()}});return emitter.event}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function latch(event,equals=((a,b)=>a===b)){let cache,firstCall=!0;return filter(event,(value=>{const shouldEmit=firstCall||!equals(value,cache);return firstCall=!1,cache=value,shouldEmit}))}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function split(event,isT){return[Event.filter(event,isT),Event.filter(event,(e=>!isT(e)))]}
/**
     * @deprecated DO NOT use, this leaks memory
     */
function buffer(event,nextTick=!1,_buffer=[]){let buffer=_buffer.slice(),listener=event((e=>{buffer?buffer.push(e):emitter.fire(e)}));const flush=()=>{buffer&&buffer.forEach((e=>emitter.fire(e))),buffer=null},emitter=new Emitter({onFirstListenerAdd(){listener||(listener=event((e=>emitter.fire(e))))},onFirstListenerDidAdd(){buffer&&(nextTick?setTimeout(flush):flush())},onLastListenerRemove(){listener&&listener.dispose(),listener=null}});return emitter.event}Event.None=()=>_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .Disposable */.JT.None,Event.once=once,Event.map=map,Event.forEach=forEach,Event.filter=filter,Event.signal=signal,Event.any=any,Event.reduce=reduce,Event.debounce=debounce,Event.latch=latch,Event.split=split,Event.buffer=buffer;class ChainableEvent{constructor(event){this.event=event}map(fn){return new ChainableEvent(map(this.event,fn))}forEach(fn){return new ChainableEvent(forEach(this.event,fn))}filter(fn){return new ChainableEvent(filter(this.event,fn))}reduce(merge,initial){return new ChainableEvent(reduce(this.event,merge,initial))}latch(){return new ChainableEvent(latch(this.event))}debounce(merge,delay=100,leading=!1,leakWarningThreshold){return new ChainableEvent(debounce(this.event,merge,delay,leading,leakWarningThreshold))}on(listener,thisArgs,disposables){return this.event(listener,thisArgs,disposables)}once(listener,thisArgs,disposables){return once(this.event)(listener,thisArgs,disposables)}}
/**
     * @deprecated DO NOT use, this leaks memory
     */function chain(event){return new ChainableEvent(event)}function fromNodeEventEmitter(emitter,eventName,map=(id=>id)){const fn=(...args)=>result.fire(map(...args)),onFirstListenerAdd=()=>emitter.on(eventName,fn),onLastListenerRemove=()=>emitter.removeListener(eventName,fn),result=new Emitter({onFirstListenerAdd:onFirstListenerAdd,onLastListenerRemove:onLastListenerRemove});return result.event}function fromDOMEventEmitter(emitter,eventName,map=(id=>id)){const fn=(...args)=>result.fire(map(...args)),onFirstListenerAdd=()=>emitter.addEventListener(eventName,fn),onLastListenerRemove=()=>emitter.removeEventListener(eventName,fn),result=new Emitter({onFirstListenerAdd:onFirstListenerAdd,onLastListenerRemove:onLastListenerRemove});return result.event}function toPromise(event){return new Promise((resolve=>once(event)(resolve)))}Event.chain=chain,Event.fromNodeEventEmitter=fromNodeEventEmitter,Event.fromDOMEventEmitter=fromDOMEventEmitter,Event.toPromise=toPromise})(Event||(Event={}));class EventProfiling{constructor(name){this._listenerCount=0,this._invocationCount=0,this._elapsedOverall=0,this._name=`${name}_${EventProfiling._idPool++}`}start(listenerCount){this._stopWatch=new _stopwatch_js__WEBPACK_IMPORTED_MODULE_3__/* .StopWatch */.G(!0),this._listenerCount=listenerCount}stop(){if(this._stopWatch){const elapsed=this._stopWatch.elapsed();this._elapsedOverall+=elapsed,this._invocationCount+=1,this._stopWatch=void 0}}}EventProfiling._idPool=0;let _globalLeakWarningThreshold=-1;class LeakageMonitor{constructor(customThreshold,name=Math.random().toString(18).slice(2,5)){this.customThreshold=customThreshold,this.name=name,this._warnCountdown=0}dispose(){this._stacks&&this._stacks.clear()}check(listenerCount){let threshold=_globalLeakWarningThreshold;if("number"===typeof this.customThreshold&&(threshold=this.customThreshold),threshold<=0||listenerCount<threshold)return;this._stacks||(this._stacks=new Map);const stack=(new Error).stack.split("\n").slice(3).join("\n"),count=this._stacks.get(stack)||0;if(this._stacks.set(stack,count+1),this._warnCountdown-=1,this._warnCountdown<=0){
// find most frequent listener and print warning
let topStack;
// only warn on first exceed and then every time the limit
// is exceeded by 50% again
this._warnCountdown=.5*threshold;let topCount=0;for(const[stack,count]of this._stacks)(!topStack||topCount<count)&&(topStack=stack,topCount=count)}return()=>{const count=this._stacks.get(stack)||0;this._stacks.set(stack,count-1)}}}
/**
 * The Emitter can be used to expose an Event to the public
 * to fire it from the insides.
 * Sample:
    class Document {

        private readonly _onDidChange = new Emitter<(value:string)=>any>();

        public onDidChange = this._onDidChange.event;

        // getter-style
        // get onDidChange(): Event<(value:string)=>any> {
        // 	return this._onDidChange.event;
        // }

        private _doIt() {
            //...
            this._onDidChange.fire(value);
        }
    }
 */class Emitter{constructor(options){var _a;this._disposed=!1,this._options=options,this._leakageMon=_globalLeakWarningThreshold>0?new LeakageMonitor(this._options&&this._options.leakWarningThreshold):void 0,this._perfMon=(null===(_a=this._options)||void 0===_a?void 0:_a._profName)?new EventProfiling(this._options._profName):void 0}
/**
     * For the public to allow to subscribe
     * to events from this Emitter
     */get event(){return this._event||(this._event=(listener,thisArgs,disposables)=>{var _a;this._listeners||(this._listeners=new _linkedList_js__WEBPACK_IMPORTED_MODULE_2__/* .LinkedList */.S);const firstListener=this._listeners.isEmpty();firstListener&&this._options&&this._options.onFirstListenerAdd&&this._options.onFirstListenerAdd(this);const remove=this._listeners.push(thisArgs?[listener,thisArgs]:listener);firstListener&&this._options&&this._options.onFirstListenerDidAdd&&this._options.onFirstListenerDidAdd(this),this._options&&this._options.onListenerDidAdd&&this._options.onListenerDidAdd(this,listener,thisArgs);
// check and record this emitter for potential leakage
const removeMonitor=null===(_a=this._leakageMon)||void 0===_a?void 0:_a.check(this._listeners.size),result=(0,_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .toDisposable */.OF)((()=>{if(removeMonitor&&removeMonitor(),!this._disposed&&(remove(),this._options&&this._options.onLastListenerRemove)){const hasListeners=this._listeners&&!this._listeners.isEmpty();hasListeners||this._options.onLastListenerRemove(this)}}));return disposables instanceof _lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .DisposableStore */.SL?disposables.add(result):Array.isArray(disposables)&&disposables.push(result),result}),this._event}
/**
     * To be kept private to fire an event to
     * subscribers
     */fire(event){var _a,_b;if(this._listeners){
// put all [listener,event]-pairs into delivery queue
// then emit all event. an inner/nested event might be
// the driver of this
this._deliveryQueue||(this._deliveryQueue=new _linkedList_js__WEBPACK_IMPORTED_MODULE_2__/* .LinkedList */.S);for(let listener of this._listeners)this._deliveryQueue.push([listener,event]);
// start/stop performance insight collection
null===(_a=this._perfMon)||void 0===_a||_a.start(this._deliveryQueue.size);while(this._deliveryQueue.size>0){const[listener,event]=this._deliveryQueue.shift();try{"function"===typeof listener?listener.call(void 0,event):listener[0].call(listener[1],event)}catch(e){(0,_errors_js__WEBPACK_IMPORTED_MODULE_0__/* .onUnexpectedError */.dL)(e)}}null===(_b=this._perfMon)||void 0===_b||_b.stop()}}dispose(){var _a,_b,_c,_d,_e;this._disposed||(this._disposed=!0,null===(_a=this._listeners)||void 0===_a||_a.clear(),null===(_b=this._deliveryQueue)||void 0===_b||_b.clear(),null===(_d=null===(_c=this._options)||void 0===_c?void 0:_c.onLastListenerRemove)||void 0===_d||_d.call(_c),null===(_e=this._leakageMon)||void 0===_e||_e.dispose())}}class PauseableEmitter extends Emitter{constructor(options){super(options),this._isPaused=0,this._eventQueue=new _linkedList_js__WEBPACK_IMPORTED_MODULE_2__/* .LinkedList */.S,this._mergeFn=null===options||void 0===options?void 0:options.merge}pause(){this._isPaused++}resume(){if(0!==this._isPaused&&0===--this._isPaused)if(this._mergeFn){
// use the merge function to create a single composite
// event. make a copy in case firing pauses this emitter
const events=Array.from(this._eventQueue);this._eventQueue.clear(),super.fire(this._mergeFn(events))}else
// no merging, fire each event individually and test
// that this emitter isn't paused halfway through
while(!this._isPaused&&0!==this._eventQueue.size)super.fire(this._eventQueue.shift())}fire(event){this._listeners&&(0!==this._isPaused?this._eventQueue.push(event):super.fire(event))}}class DebounceEmitter extends PauseableEmitter{constructor(options){var _a;super(options),this._delay=null!==(_a=options.delay)&&void 0!==_a?_a:100}fire(event){this._handle||(this.pause(),this._handle=setTimeout((()=>{this._handle=void 0,this.resume()}),this._delay)),super.fire(event)}}
/**
 * The EventBufferer is useful in situations in which you want
 * to delay firing your events during some code.
 * You can wrap that code and be sure that the event will not
 * be fired during that wrap.
 *
 * ```
 * const emitter: Emitter;
 * const delayer = new EventDelayer();
 * const delayedEvent = delayer.wrapEvent(emitter.event);
 *
 * delayedEvent(console.log);
 *
 * delayer.bufferEvents(() => {
 *   emitter.fire(); // event will not be fired yet
 * });
 *
 * // event will only be fired at this point
 * ```
 */class EventBufferer{constructor(){this.buffers=[]}wrapEvent(event){return(listener,thisArgs,disposables)=>event((i=>{const buffer=this.buffers[this.buffers.length-1];buffer?buffer.push((()=>listener.call(thisArgs,i))):listener.call(thisArgs,i)}),void 0,disposables)}bufferEvents(fn){const buffer=[];this.buffers.push(buffer);const r=fn();return this.buffers.pop(),buffer.forEach((flush=>flush())),r}}
/**
 * A Relay is an event forwarder which functions as a replugabble event pipe.
 * Once created, you can connect an input event to it and it will simply forward
 * events from that input event through its own `event` property. The `input`
 * can be changed at any point in time.
 */class Relay{constructor(){this.listening=!1,this.inputEvent=Event.None,this.inputEventListener=_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .Disposable */.JT.None,this.emitter=new Emitter({onFirstListenerDidAdd:()=>{this.listening=!0,this.inputEventListener=this.inputEvent(this.emitter.fire,this.emitter)},onLastListenerRemove:()=>{this.listening=!1,this.inputEventListener.dispose()}}),this.event=this.emitter.event}set input(event){this.inputEvent=event,this.listening&&(this.inputEventListener.dispose(),this.inputEventListener=event(this.emitter.fire,this.emitter))}dispose(){this.inputEventListener.dispose(),this.emitter.dispose()}}
/***/},
/***/915527:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */KM:function(){/* binding */return isEqualOrParent},
/* harmony export */fn:function(){/* binding */return toPosixPath},
/* harmony export */oP:function(){/* binding */return hasDriveLetter},
/* harmony export */vY:function(){/* binding */return isRootOrDriveLetter}
/* harmony export */});
/* unused harmony exports toSlashes, isWindowsDriveLetter */
/* harmony import */var _path_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(555336),_platform_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(901432),_strings_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(697295);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/**
 * Takes a Windows OS path and changes backward slashes to forward slashes.
 * This should only be done for OS paths from Windows (or user provided paths potentially from Windows).
 * Using it on a Linux or MaxOS path might change it.
 */
function toSlashes(osPath){return osPath.replace(/[\\/]/g,_path_js__WEBPACK_IMPORTED_MODULE_0__/* .posix */.KR.sep)}
/**
 * Takes a Windows OS path (using backward or forward slashes) and turns it into a posix path:
 * - turns backward slashes into forward slashes
 * - makes it absolute if it starts with a drive letter
 * This should only be done for OS paths from Windows (or user provided paths potentially from Windows).
 * Using it on a Linux or MaxOS path might change it.
 */function toPosixPath(osPath){return-1===osPath.indexOf("/")&&(osPath=toSlashes(osPath)),/^[a-zA-Z]:(\/|$)/.test(osPath)&&(// starts with a drive letter
osPath="/"+osPath),osPath}function isEqualOrParent(base,parentCandidate,ignoreCase,separator=_path_js__WEBPACK_IMPORTED_MODULE_0__/* .sep */.ir){if(base===parentCandidate)return!0;if(!base||!parentCandidate)return!1;if(parentCandidate.length>base.length)return!1;if(ignoreCase){const beginsWith=(0,_strings_js__WEBPACK_IMPORTED_MODULE_2__/* .startsWithIgnoreCase */.ok)(base,parentCandidate);if(!beginsWith)return!1;if(parentCandidate.length===base.length)return!0;// same path, different casing
let sepOffset=parentCandidate.length;return parentCandidate.charAt(parentCandidate.length-1)===separator&&sepOffset--,base.charAt(sepOffset)===separator}return parentCandidate.charAt(parentCandidate.length-1)!==separator&&(parentCandidate+=separator),0===base.indexOf(parentCandidate)}function isWindowsDriveLetter(char0){return char0>=65/* A */&&char0<=90/* Z */||char0>=97/* a */&&char0<=122/* z */}function isRootOrDriveLetter(path){const pathNormalized=(0,_path_js__WEBPACK_IMPORTED_MODULE_0__/* .normalize */.Fv)(path);return _platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED?!(path.length>3)&&(hasDriveLetter(pathNormalized)&&(2===path.length||92/* Backslash */===pathNormalized.charCodeAt(2))):pathNormalized===_path_js__WEBPACK_IMPORTED_MODULE_0__/* .posix */.KR.sep}function hasDriveLetter(path){return!!_platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED&&(isWindowsDriveLetter(path.charCodeAt(0))&&58/* Colon */===path.charCodeAt(1))}
/***/},
/***/75392:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */CL:function(){/* binding */return FuzzyScore},
/* harmony export */EW:function(){/* binding */return fuzzyScore},
/* harmony export */Ji:function(){/* binding */return matchesPrefix},
/* harmony export */KZ:function(){/* binding */return matchesWords},
/* harmony export */Oh:function(){/* binding */return matchesFuzzy},
/* harmony export */ir:function(){/* binding */return matchesContiguousSubString},
/* harmony export */jB:function(){/* binding */return anyScore},
/* harmony export */l7:function(){/* binding */return fuzzyScoreGracefulAggressive},
/* harmony export */mB:function(){/* binding */return createMatches},
/* harmony export */or:function(){/* binding */return or}
/* harmony export */});
/* unused harmony exports matchesSubString, isUpper, matchesCamelCase, isPatternInWord */
/* harmony import */var _map_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(843702),_strings_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(697295);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
// Combined filters
/**
 * @returns A filter which combines the provided set
 * of filters with an or. The *first* filters that
 * matches defined the return value of the returned
 * filter.
 */
function or(...filter){return function(word,wordToMatchAgainst){for(let i=0,len=filter.length;i<len;i++){const match=filter[i](word,wordToMatchAgainst);if(match)return match}return null}}const matchesPrefix=_matchesPrefix.bind(void 0,!0);function _matchesPrefix(ignoreCase,word,wordToMatchAgainst){if(!wordToMatchAgainst||wordToMatchAgainst.length<word.length)return null;let matches;return matches=ignoreCase?_strings_js__WEBPACK_IMPORTED_MODULE_1__/* .startsWithIgnoreCase */.ok(wordToMatchAgainst,word):0===wordToMatchAgainst.indexOf(word),matches?word.length>0?[{start:0,end:word.length}]:[]:null}
// Contiguous Substring
function matchesContiguousSubString(word,wordToMatchAgainst){const index=wordToMatchAgainst.toLowerCase().indexOf(word.toLowerCase());return-1===index?null:[{start:index,end:index+word.length}]}
// Substring
function matchesSubString(word,wordToMatchAgainst){return _matchesSubString(word.toLowerCase(),wordToMatchAgainst.toLowerCase(),0,0)}function _matchesSubString(word,wordToMatchAgainst,i,j){if(i===word.length)return[];if(j===wordToMatchAgainst.length)return null;if(word[i]===wordToMatchAgainst[j]){let result=null;return(result=_matchesSubString(word,wordToMatchAgainst,i+1,j+1))?join({start:j,end:j+1},result):null}return _matchesSubString(word,wordToMatchAgainst,i,j+1)}
// CamelCase
function isLower(code){return 97/* a */<=code&&code<=122/* z */}function isUpper(code){return 65/* A */<=code&&code<=90/* Z */}function isNumber(code){return 48/* Digit0 */<=code&&code<=57/* Digit9 */}function isWhitespace(code){return 32/* Space */===code||9/* Tab */===code||10/* LineFeed */===code||13/* CarriageReturn */===code}const wordSeparators=new Set;function isWordSeparator(code){return isWhitespace(code)||wordSeparators.has(code)}function charactersMatch(codeA,codeB){return codeA===codeB||isWordSeparator(codeA)&&isWordSeparator(codeB)}function isAlphanumeric(code){return isLower(code)||isUpper(code)||isNumber(code)}function join(head,tail){return 0===tail.length?tail=[head]:head.end===tail[0].start?tail[0].start=head.start:tail.unshift(head),tail}function nextAnchor(camelCaseWord,start){for(let i=start;i<camelCaseWord.length;i++){const c=camelCaseWord.charCodeAt(i);if(isUpper(c)||isNumber(c)||i>0&&!isAlphanumeric(camelCaseWord.charCodeAt(i-1)))return i}return camelCaseWord.length}function _matchesCamelCase(word,camelCaseWord,i,j){if(i===word.length)return[];if(j===camelCaseWord.length)return null;if(word[i]!==camelCaseWord[j].toLowerCase())return null;{let result=null,nextUpperIndex=j+1;result=_matchesCamelCase(word,camelCaseWord,i+1,j+1);while(!result&&(nextUpperIndex=nextAnchor(camelCaseWord,nextUpperIndex))<camelCaseWord.length)result=_matchesCamelCase(word,camelCaseWord,i+1,nextUpperIndex),nextUpperIndex++;return null===result?null:join({start:j,end:j+1},result)}}
// Heuristic to avoid computing camel case matcher for words that don't
// look like camelCaseWords.
function analyzeCamelCaseWord(word){let upper=0,lower=0,alpha=0,numeric=0,code=0;for(let i=0;i<word.length;i++)code=word.charCodeAt(i),isUpper(code)&&upper++,isLower(code)&&lower++,isAlphanumeric(code)&&alpha++,isNumber(code)&&numeric++;const upperPercent=upper/word.length,lowerPercent=lower/word.length,alphaPercent=alpha/word.length,numericPercent=numeric/word.length;return{upperPercent:upperPercent,lowerPercent:lowerPercent,alphaPercent:alphaPercent,numericPercent:numericPercent}}function isUpperCaseWord(analysis){const{upperPercent:upperPercent,lowerPercent:lowerPercent}=analysis;return 0===lowerPercent&&upperPercent>.6}function isCamelCaseWord(analysis){const{upperPercent:upperPercent,lowerPercent:lowerPercent,alphaPercent:alphaPercent,numericPercent:numericPercent}=analysis;return lowerPercent>.2&&upperPercent<.8&&alphaPercent>.6&&numericPercent<.2}
// Heuristic to avoid computing camel case matcher for words that don't
// look like camel case patterns.
function isCamelCasePattern(word){let upper=0,lower=0,code=0,whitespace=0;for(let i=0;i<word.length;i++)code=word.charCodeAt(i),isUpper(code)&&upper++,isLower(code)&&lower++,isWhitespace(code)&&whitespace++;return 0!==upper&&0!==lower||0!==whitespace?upper<=5:word.length<=30}function matchesCamelCase(word,camelCaseWord){if(!camelCaseWord)return null;if(camelCaseWord=camelCaseWord.trim(),0===camelCaseWord.length)return null;if(!isCamelCasePattern(word))return null;if(camelCaseWord.length>60)return null;const analysis=analyzeCamelCaseWord(camelCaseWord);if(!isCamelCaseWord(analysis)){if(!isUpperCaseWord(analysis))return null;camelCaseWord=camelCaseWord.toLowerCase()}let result=null,i=0;word=word.toLowerCase();while(i<camelCaseWord.length&&null===(result=_matchesCamelCase(word,camelCaseWord,0,i)))i=nextAnchor(camelCaseWord,i+1);return result}
// Matches beginning of words supporting non-ASCII languages
// If `contiguous` is true then matches word with beginnings of the words in the target. E.g. "pul" will match "Git: Pull"
// Otherwise also matches sub string of the word with beginnings of the words in the target. E.g. "gp" or "g p" will match "Git: Pull"
// Useful in cases where the target is words (e.g. command labels)
function matchesWords(word,target,contiguous=!1){if(!target||0===target.length)return null;let result=null,i=0;word=word.toLowerCase(),target=target.toLowerCase();while(i<target.length&&null===(result=_matchesWords(word,target,0,i,contiguous)))i=nextWord(target,i+1);return result}function _matchesWords(word,target,i,j,contiguous){if(i===word.length)return[];if(j===target.length)return null;if(charactersMatch(word.charCodeAt(i),target.charCodeAt(j))){let result=null,nextWordIndex=j+1;if(result=_matchesWords(word,target,i+1,j+1,contiguous),!contiguous)while(!result&&(nextWordIndex=nextWord(target,nextWordIndex))<target.length)result=_matchesWords(word,target,i+1,nextWordIndex,contiguous),nextWordIndex++;return null===result?null:join({start:j,end:j+1},result)}return null}function nextWord(word,start){for(let i=start;i<word.length;i++)if(isWordSeparator(word.charCodeAt(i))||i>0&&isWordSeparator(word.charCodeAt(i-1)))return i;return word.length}
// Fuzzy
"`~!@#$%^&*()-=+[{]}\\|;:'\",.<>/?".split("").forEach((s=>wordSeparators.add(s.charCodeAt(0))));const fuzzyContiguousFilter=or(matchesPrefix,matchesCamelCase,matchesContiguousSubString),fuzzySeparateFilter=or(matchesPrefix,matchesCamelCase,matchesSubString),fuzzyRegExpCache=new _map_js__WEBPACK_IMPORTED_MODULE_0__/* .LRUCache */.z6(1e4);// bounded to 10000 elements
function matchesFuzzy(word,wordToMatchAgainst,enableSeparateSubstringMatching=!1){if("string"!==typeof word||"string"!==typeof wordToMatchAgainst)return null;// return early for invalid input
// Form RegExp for wildcard matches
let regexp=fuzzyRegExpCache.get(word);regexp||(regexp=new RegExp(_strings_js__WEBPACK_IMPORTED_MODULE_1__/* .convertSimple2RegExpPattern */.un(word),"i"),fuzzyRegExpCache.set(word,regexp));
// RegExp Filter
const match=regexp.exec(wordToMatchAgainst);return match?[{start:match.index,end:match.index+match[0].length}]:enableSeparateSubstringMatching?fuzzySeparateFilter(word,wordToMatchAgainst):fuzzyContiguousFilter(word,wordToMatchAgainst);
// Default Filter
}function anyScore(pattern,lowPattern,patternPos,word,lowWord,wordPos){const max=Math.min(13,pattern.length);for(;patternPos<max;patternPos++){const result=fuzzyScore(pattern,lowPattern,patternPos,word,lowWord,wordPos,!1);if(result)return result}return[0,wordPos]}
//#region --- fuzzyScore ---
function createMatches(score){if("undefined"===typeof score)return[];const res=[],wordPos=score[1];for(let i=score.length-1;i>1;i--){const pos=score[i]+wordPos,last=res[res.length-1];last&&last.end===pos?last.end=pos+1:res.push({start:pos,end:pos+1})}return res}const _maxLen=128;function initTable(){const table=[],row=[];for(let i=0;i<=_maxLen;i++)row[i]=0;for(let i=0;i<=_maxLen;i++)table.push(row.slice(0));return table}function initArr(maxLen){const row=[];for(let i=0;i<=maxLen;i++)row[i]=0;return row}const _minWordMatchPos=initArr(2*_maxLen),_maxWordMatchPos=initArr(2*_maxLen),_diag=initTable(),_table=initTable(),_arrows=initTable(),_debug=!1;// min word position for a certain pattern position
function printTables(pattern,patternStart,word,wordStart){pattern=pattern.substr(patternStart),word=word.substr(wordStart)}function isSeparatorAtPos(value,index){if(index<0||index>=value.length)return!1;const code=value.codePointAt(index);switch(code){case 95/* Underline */:case 45/* Dash */:case 46/* Period */:case 32/* Space */:case 47/* Slash */:case 92/* Backslash */:case 39/* SingleQuote */:case 34/* DoubleQuote */:case 58/* Colon */:case 36/* DollarSign */:case 60/* LessThan */:case 40/* OpenParen */:case 91/* OpenSquareBracket */:return!0;case void 0:return!1;default:return!!_strings_js__WEBPACK_IMPORTED_MODULE_1__/* .isEmojiImprecise */.C8(code)}}function isWhitespaceAtPos(value,index){if(index<0||index>=value.length)return!1;const code=value.charCodeAt(index);switch(code){case 32/* Space */:case 9/* Tab */:return!0;default:return!1}}function isUpperCaseAtPos(pos,word,wordLow){return word[pos]!==wordLow[pos]}function isPatternInWord(patternLow,patternPos,patternLen,wordLow,wordPos,wordLen,fillMinWordPosArr=!1){while(patternPos<patternLen&&wordPos<wordLen)patternLow[patternPos]===wordLow[wordPos]&&(fillMinWordPosArr&&(
// Remember the min word position for each pattern position
_minWordMatchPos[patternPos]=wordPos),patternPos+=1),wordPos+=1;return patternPos===patternLen;// pattern must be exhausted
}var FuzzyScore;function fuzzyScore(pattern,patternLow,patternStart,word,wordLow,wordStart,firstMatchCanBeWeak){const patternLen=pattern.length>_maxLen?_maxLen:pattern.length,wordLen=word.length>_maxLen?_maxLen:word.length;if(patternStart>=patternLen||wordStart>=wordLen||patternLen-patternStart>wordLen-wordStart)return;
// Run a simple check if the characters of pattern occur
// (in order) at all in word. If that isn't the case we
// stop because no match will be possible
if(!isPatternInWord(patternLow,patternStart,patternLen,wordLow,wordStart,wordLen,!0))return;
// Find the max matching word position for each pattern position
// NOTE: the min matching word position was filled in above, in the `isPatternInWord` call
_fillInMaxWordMatchPos(patternLen,wordLen,patternStart,wordStart,patternLow,wordLow);let row=1,column=1,patternPos=patternStart,wordPos=wordStart;const hasStrongFirstMatch=[!1];
// There will be a match, fill in tables
for(row=1,patternPos=patternStart;patternPos<patternLen;row++,patternPos++){
// Reduce search space to possible matching word positions and to possible access from next row
const minWordMatchPos=_minWordMatchPos[patternPos],maxWordMatchPos=_maxWordMatchPos[patternPos],nextMaxWordMatchPos=patternPos+1<patternLen?_maxWordMatchPos[patternPos+1]:wordLen;for(column=minWordMatchPos-wordStart+1,wordPos=minWordMatchPos;wordPos<nextMaxWordMatchPos;column++,wordPos++){let score=Number.MIN_SAFE_INTEGER,canComeDiag=!1;wordPos<=maxWordMatchPos&&(score=_doScore(pattern,patternLow,patternPos,patternStart,word,wordLow,wordPos,wordLen,wordStart,0===_diag[row-1][column-1],hasStrongFirstMatch));let diagScore=0;score!==Number.MAX_SAFE_INTEGER&&(canComeDiag=!0,diagScore=score+_table[row-1][column-1]);const canComeLeft=wordPos>minWordMatchPos,leftScore=canComeLeft?_table[row][column-1]+(_diag[row][column-1]>0?-5:0):0,canComeLeftLeft=wordPos>minWordMatchPos+1&&_diag[row][column-1]>0,leftLeftScore=canComeLeftLeft?_table[row][column-2]+(_diag[row][column-2]>0?-5:0):0;// penalty for a gap start
if(canComeLeftLeft&&(!canComeLeft||leftLeftScore>=leftScore)&&(!canComeDiag||leftLeftScore>=diagScore))
// always prefer choosing left left to jump over a diagonal because that means a match is earlier in the word
_table[row][column]=leftLeftScore,_arrows[row][column]=3/* LeftLeft */,_diag[row][column]=0;else if(canComeLeft&&(!canComeDiag||leftScore>=diagScore))
// always prefer choosing left since that means a match is earlier in the word
_table[row][column]=leftScore,_arrows[row][column]=2/* Left */,_diag[row][column]=0;else{if(!canComeDiag)throw new Error("not possible");_table[row][column]=diagScore,_arrows[row][column]=1/* Diag */,_diag[row][column]=_diag[row-1][column-1]+1}}}if(_debug&&printTables(pattern,patternStart,word,wordStart),!hasStrongFirstMatch[0]&&!firstMatchCanBeWeak)return;row--,column--;const result=[_table[row][column],wordStart];let backwardsDiagLength=0,maxMatchColumn=0;while(row>=1){
// Find the column where we go diagonally up
let diagColumn=column;do{const arrow=_arrows[row][diagColumn];if(3/* LeftLeft */===arrow)diagColumn-=2;else{if(2/* Left */!==arrow)
// found the diagonal
break;diagColumn-=1}}while(diagColumn>=1);
// Overturn the "forwards" decision if keeping the "backwards" diagonal would give a better match
backwardsDiagLength>1&&patternLow[patternStart+row-1]===wordLow[wordStart+column-1]&&!isUpperCaseAtPos(diagColumn+wordStart-1,word,wordLow)&&backwardsDiagLength+1>_diag[row][diagColumn]&&(diagColumn=column),diagColumn===column?
// this is a contiguous match
backwardsDiagLength++:backwardsDiagLength=1,maxMatchColumn||(
// remember the last matched column
maxMatchColumn=diagColumn),row--,column=diagColumn-1,result.push(column)}wordLen===patternLen&&(
// the word matches the pattern with all characters!
// giving the score a total match boost (to come up ahead other words)
result[0]+=2);
// Add 1 penalty for each skipped character in the word
const skippedCharsCount=maxMatchColumn-patternLen;return result[0]-=skippedCharsCount,result}function _fillInMaxWordMatchPos(patternLen,wordLen,patternStart,wordStart,patternLow,wordLow){let patternPos=patternLen-1,wordPos=wordLen-1;while(patternPos>=patternStart&&wordPos>=wordStart)patternLow[patternPos]===wordLow[wordPos]&&(_maxWordMatchPos[patternPos]=wordPos,patternPos--),wordPos--}function _doScore(pattern,patternLow,patternPos,patternStart,word,wordLow,wordPos,wordLen,wordStart,newMatchStart,outFirstMatchStrong){if(patternLow[patternPos]!==wordLow[wordPos])return Number.MIN_SAFE_INTEGER;let score=1,isGapLocation=!1;return wordPos===patternPos-patternStart?
// common prefix: `foobar <-> foobaz`
//                            ^^^^^
score=pattern[patternPos]===word[wordPos]?7:5:!isUpperCaseAtPos(wordPos,word,wordLow)||0!==wordPos&&isUpperCaseAtPos(wordPos-1,word,wordLow)?!isSeparatorAtPos(wordLow,wordPos)||0!==wordPos&&isSeparatorAtPos(wordLow,wordPos-1)?(isSeparatorAtPos(wordLow,wordPos-1)||isWhitespaceAtPos(wordLow,wordPos-1))&&(
// post separator: `foo <-> bar_foo`
//                              ^^^
score=5,isGapLocation=!0):
// hitting a separator: `. <-> foo.bar`
//                                ^
score=5:(
// hitting upper-case: `foo <-> forOthers`
//                              ^^ ^
score=pattern[patternPos]===word[wordPos]?7:5,isGapLocation=!0),score>1&&patternPos===patternStart&&(outFirstMatchStrong[0]=!0),isGapLocation||(isGapLocation=isUpperCaseAtPos(wordPos,word,wordLow)||isSeparatorAtPos(wordLow,wordPos-1)||isWhitespaceAtPos(wordLow,wordPos-1)),
patternPos===patternStart?// first character in pattern
wordPos>wordStart&&(
// the first pattern character would match a word character that is not at the word start
// so introduce a penalty to account for the gap preceding this match
score-=isGapLocation?3:5):
// this would be the beginning of a new match (i.e. there would be a gap before this location)
score+=newMatchStart?isGapLocation?2:0:isGapLocation?0:1,wordPos+1===wordLen&&(
// we always penalize gaps, but this gives unfair advantages to a match that would match the last character in the word
// so pretend there is a gap after the last character in the word to normalize things
score-=isGapLocation?3:5),score}
//#endregion
//#region --- graceful ---
function fuzzyScoreGracefulAggressive(pattern,lowPattern,patternPos,word,lowWord,wordPos,firstMatchCanBeWeak){return fuzzyScoreWithPermutations(pattern,lowPattern,patternPos,word,lowWord,wordPos,!0,firstMatchCanBeWeak)}function fuzzyScoreWithPermutations(pattern,lowPattern,patternPos,word,lowWord,wordPos,aggressive,firstMatchCanBeWeak){let top=fuzzyScore(pattern,lowPattern,patternPos,word,lowWord,wordPos,firstMatchCanBeWeak);if(top&&!aggressive)
// when using the original pattern yield a result we`
// return it unless we are aggressive and try to find
// a better alignment, e.g. `cno` -> `^co^ns^ole` or `^c^o^nsole`.
return top;if(pattern.length>=3){
// When the pattern is long enough then try a few (max 7)
// permutations of the pattern to find a better match. The
// permutations only swap neighbouring characters, e.g
// `cnoso` becomes `conso`, `cnsoo`, `cnoos`.
const tries=Math.min(7,pattern.length-1);for(let movingPatternPos=patternPos+1;movingPatternPos<tries;movingPatternPos++){const newPattern=nextTypoPermutation(pattern,movingPatternPos);if(newPattern){const candidate=fuzzyScore(newPattern,newPattern.toLowerCase(),patternPos,word,lowWord,wordPos,firstMatchCanBeWeak);candidate&&(candidate[0]-=3,// permutation penalty
(!top||candidate[0]>top[0])&&(top=candidate))}}}return top}function nextTypoPermutation(pattern,patternPos){if(patternPos+1>=pattern.length)return;const swap1=pattern[patternPos],swap2=pattern[patternPos+1];return swap1!==swap2?pattern.slice(0,patternPos)+swap2+swap1+pattern.slice(patternPos+2):void 0}
//#endregion
/***/(function(FuzzyScore){function isDefault(score){return!score||2===score.length&&-100===score[0]&&0===score[1]}
/**
     * No matches and value `-100`
     */
FuzzyScore.Default=[-100,0],FuzzyScore.isDefault=isDefault})(FuzzyScore||(FuzzyScore={}))},
/***/888289:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function once(fn){const _this=this;let result,didCall=!1;return function(){return didCall||(didCall=!0,result=fn.apply(_this,arguments)),result}}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */I:function(){/* binding */return once}
/* harmony export */})},
/***/753192:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */AO:function(){/* binding */return prepareQuery},
/* harmony export */Ic:function(){/* binding */return pieceToQuery},
/* harmony export */mt:function(){/* binding */return scoreFuzzy2}
/* harmony export */});
/* harmony import */var _filters_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(75392),_path_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(555336),_platform_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(901432),_strings_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(697295);
/* harmony import */const NO_SCORE2=[void 0,[]];function scoreFuzzy2(target,query,patternStart=0,wordStart=0){
// Score: multiple inputs
const preparedQuery=query;return preparedQuery.values&&preparedQuery.values.length>1?doScoreFuzzy2Multiple(target,preparedQuery.values,patternStart,wordStart):doScoreFuzzy2Single(target,query,patternStart,wordStart);
// Score: single input
}function doScoreFuzzy2Multiple(target,query,patternStart,wordStart){let totalScore=0;const totalMatches=[];for(const queryPiece of query){const[score,matches]=doScoreFuzzy2Single(target,queryPiece,patternStart,wordStart);if("number"!==typeof score)
// if a single query value does not match, return with
// no score entirely, we require all queries to match
return NO_SCORE2;totalScore+=score,totalMatches.push(...matches)}
// if we have a score, ensure that the positions are
// sorted in ascending order and distinct
return[totalScore,normalizeMatches(totalMatches)]}function doScoreFuzzy2Single(target,query,patternStart,wordStart){const score=(0,_filters_js__WEBPACK_IMPORTED_MODULE_0__/* .fuzzyScore */.EW)(query.original,query.originalLowercase,patternStart,target,target.toLowerCase(),wordStart,!0);return score?[score[0],(0,_filters_js__WEBPACK_IMPORTED_MODULE_0__/* .createMatches */.mB)(score)]:NO_SCORE2}function normalizeMatches(matches){
// sort matches by start to be able to normalize
const sortedMatches=matches.sort(((matchA,matchB)=>matchA.start-matchB.start)),normalizedMatches=[];
// merge matches that overlap
let currentMatch;for(const match of sortedMatches)
// if we have no current match or the matches
// do not overlap, we take it as is and remember
// it for future merging
currentMatch&&matchOverlaps(currentMatch,match)?(currentMatch.start=Math.min(currentMatch.start,match.start),currentMatch.end=Math.max(currentMatch.end,match.end)):(currentMatch=match,normalizedMatches.push(match));return normalizedMatches}function matchOverlaps(matchA,matchB){return!(matchA.end<matchB.start)&&!(matchB.end<matchA.start)}
/*
 * If a query is wrapped in quotes, the user does not want to
 * use fuzzy search for this query.
 */function queryExpectsExactMatch(query){return query.startsWith('"')&&query.endsWith('"')}
/**
 * Helper function to prepare a search value for scoring by removing unwanted characters
 * and allowing to score on multiple pieces separated by whitespace character.
 */const MULTIPLE_QUERY_VALUES_SEPARATOR=" ";function prepareQuery(original){"string"!==typeof original&&(original="");const originalLowercase=original.toLowerCase(),{pathNormalized:pathNormalized,normalized:normalized,normalizedLowercase:normalizedLowercase}=normalizeQuery(original),containsPathSeparator=pathNormalized.indexOf(_path_js__WEBPACK_IMPORTED_MODULE_1__/* .sep */.ir)>=0,expectExactMatch=queryExpectsExactMatch(original);let values;const originalSplit=original.split(MULTIPLE_QUERY_VALUES_SEPARATOR);if(originalSplit.length>1)for(const originalPiece of originalSplit){const expectExactMatchPiece=queryExpectsExactMatch(originalPiece),{pathNormalized:pathNormalizedPiece,normalized:normalizedPiece,normalizedLowercase:normalizedLowercasePiece}=normalizeQuery(originalPiece);normalizedPiece&&(values||(values=[]),values.push({original:originalPiece,originalLowercase:originalPiece.toLowerCase(),pathNormalized:pathNormalizedPiece,normalized:normalizedPiece,normalizedLowercase:normalizedLowercasePiece,expectContiguousMatch:expectExactMatchPiece}))}return{original:original,originalLowercase:originalLowercase,pathNormalized:pathNormalized,normalized:normalized,normalizedLowercase:normalizedLowercase,values:values,containsPathSeparator:containsPathSeparator,expectContiguousMatch:expectExactMatch}}function normalizeQuery(original){let pathNormalized;pathNormalized=_platform_js__WEBPACK_IMPORTED_MODULE_2__/* .isWindows */.ED?original.replace(/\//g,_path_js__WEBPACK_IMPORTED_MODULE_1__/* .sep */.ir):original.replace(/\\/g,_path_js__WEBPACK_IMPORTED_MODULE_1__/* .sep */.ir);
// we remove quotes here because quotes are used for exact match search
const normalized=(0,_strings_js__WEBPACK_IMPORTED_MODULE_3__/* .stripWildcards */.R1)(pathNormalized).replace(/\s|"/g,"");return{pathNormalized:pathNormalized,normalized:normalized,normalizedLowercase:normalized.toLowerCase()}}function pieceToQuery(arg1){return Array.isArray(arg1)?prepareQuery(arg1.map((piece=>piece.original)).join(MULTIPLE_QUERY_VALUES_SEPARATOR)):prepareQuery(arg1.original)}
//#endregion
/***/},
/***/314118:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */EQ:function(){/* binding */return match},
/* harmony export */Qc:function(){/* binding */return parse}
/* harmony export */});
/* unused harmony exports splitGlobAware, isRelativePattern */
/* harmony import */var _async_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(715393),_extpath_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(915527),_map_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(843702),_path_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(555336),_strings_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(697295);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const GLOBSTAR="**",GLOB_SPLIT="/",PATH_REGEX="[/\\\\]",NO_PATH_REGEX="[^/\\\\]",ALL_FORWARD_SLASHES=/\//g;function starsToRegExp(starCount){switch(starCount){case 0:return"";case 1:return`${NO_PATH_REGEX}*?`;// 1 star matches any number of characters except path separator (/ and \) - non greedy (?)
default:
// Matches:  (Path Sep OR Path Val followed by Path Sep OR Path Sep followed by Path Val) 0-many times
// Group is non capturing because we don't need to capture at all (?:...)
// Overall we use non-greedy matching because it could be that we match too much
return`(?:${PATH_REGEX}|${NO_PATH_REGEX}+${PATH_REGEX}|${PATH_REGEX}${NO_PATH_REGEX}+)*?`}}function splitGlobAware(pattern,splitChar){if(!pattern)return[];const segments=[];let inBraces=!1,inBrackets=!1,curVal="";for(const char of pattern){switch(char){case splitChar:if(!inBraces&&!inBrackets){segments.push(curVal),curVal="";continue}break;case"{":inBraces=!0;break;case"}":inBraces=!1;break;case"[":inBrackets=!0;break;case"]":inBrackets=!1;break}curVal+=char}
// Tail
return curVal&&segments.push(curVal),segments}function parseRegExp(pattern){if(!pattern)return"";let regEx="";
// Split up into segments for each slash found
const segments=splitGlobAware(pattern,GLOB_SPLIT);
// Special case where we only have globstars
if(segments.every((s=>s===GLOBSTAR)))regEx=".*";else{let previousSegmentWasGlobStar=!1;segments.forEach(((segment,index)=>{
// Globstar is special
if(segment===GLOBSTAR)
// if we have more than one globstar after another, just ignore it
return void(previousSegmentWasGlobStar||(regEx+=starsToRegExp(2),previousSegmentWasGlobStar=!0));
// States
let inBraces=!1,braceVal="",inBrackets=!1,bracketVal="";for(const char of segment)
// Support brace expansion
if("}"!==char&&inBraces)braceVal+=char;else
// Support brackets
if(!inBrackets||"]"===char&&bracketVal/* ] is literally only allowed as first character in brackets to match it */)switch(char){case"{":inBraces=!0;continue;case"[":inBrackets=!0;continue;case"}":const choices=splitGlobAware(braceVal,","),braceRegExp=`(?:${choices.map((c=>parseRegExp(c))).join("|")})`;
// Converts {foo,bar} => [foo|bar]
regEx+=braceRegExp,inBraces=!1,braceVal="";break;case"]":regEx+="["+bracketVal+"]",inBrackets=!1,bracketVal="";break;case"?":regEx+=NO_PATH_REGEX;// 1 ? matches any single character except path separator (/ and \)
continue;case"*":regEx+=starsToRegExp(1);continue;default:regEx+=_strings_js__WEBPACK_IMPORTED_MODULE_4__/* .escapeRegExpCharacters */.ec(char)}else{let res;
// range operator
res="-"===char?char:"^"!==char&&"!"!==char||bracketVal?char===GLOB_SPLIT?"":_strings_js__WEBPACK_IMPORTED_MODULE_4__/* .escapeRegExpCharacters */.ec(char):"^",bracketVal+=res}
// Tail: Add the slash we had split on if there is more to come and the remaining pattern is not a globstar
// For example if pattern: some/**/*.js we want the "/" after some to be included in the RegEx to prevent
// a folder called "something" to match as well.
// However, if pattern: some/**, we tolerate that we also match on "something" because our globstar behaviour
// is to match 0-N segments.
index<segments.length-1&&(segments[index+1]!==GLOBSTAR||index+2<segments.length)&&(regEx+=PATH_REGEX),
// reset state
previousSegmentWasGlobStar=!1}))}return regEx}
// regexes to check for trivial glob patterns that just check for String#endsWith
const T1=/^\*\*\/\*\.[\w\.-]+$/,T2=/^\*\*\/([\w\.-]+)\/?$/,T3=/^{\*\*\/[\*\.]?[\w\.-]+\/?(,\*\*\/[\*\.]?[\w\.-]+\/?)*}$/,T3_2=/^{\*\*\/[\*\.]?[\w\.-]+(\/(\*\*)?)?(,\*\*\/[\*\.]?[\w\.-]+(\/(\*\*)?)?)*}$/,T4=/^\*\*((\/[\w\.-]+)+)\/?$/,T5=/^([\w\.-]+(\/[\w\.-]+)*)\/?$/,CACHE=new _map_js__WEBPACK_IMPORTED_MODULE_2__/* .LRUCache */.z6(1e4),FALSE=function(){return!1},NULL=function(){return null};// **/*.something
function parsePattern(arg1,options){if(!arg1)return NULL;
// Handle IRelativePattern
let pattern;pattern="string"!==typeof arg1?arg1.pattern:arg1,
// Whitespace trimming
pattern=pattern.trim();
// Check cache
const patternKey=`${pattern}_${!!options.trimForExclusions}`;let match,parsedPattern=CACHE.get(patternKey);if(parsedPattern)return wrapRelativePattern(parsedPattern,arg1);
// Check for Trivials
if(T1.test(pattern)){// common pattern: **/*.txt just need endsWith check
const base=pattern.substr(4);// '**/*'.length === 4
parsedPattern=function(path,basename){return"string"===typeof path&&path.endsWith(base)?pattern:null}}else// common pattern: **/some.txt just need basename check
parsedPattern=(match=T2.exec(trimForExclusions(pattern,options)))?trivia2(match[1],pattern):(options.trimForExclusions?T3_2:T3).test(pattern)?trivia3(pattern,options):(match=T4.exec(trimForExclusions(pattern,options)))?trivia4and5(match[1].substr(1),pattern,!0):(match=T5.exec(trimForExclusions(pattern,options)))?trivia4and5(match[1],pattern,!1):toRegExp(pattern);
// Cache
return CACHE.set(patternKey,parsedPattern),wrapRelativePattern(parsedPattern,arg1)}function wrapRelativePattern(parsedPattern,arg2){return"string"===typeof arg2?parsedPattern:function(path,basename){return _extpath_js__WEBPACK_IMPORTED_MODULE_1__/* .isEqualOrParent */.KM(path,arg2.base)?parsedPattern(_path_js__WEBPACK_IMPORTED_MODULE_3__/* .relative */.Gf(arg2.base,path),basename):null}}function trimForExclusions(pattern,options){return options.trimForExclusions&&pattern.endsWith("/**")?pattern.substr(0,pattern.length-2):pattern;// dropping **, tailing / is dropped later
}
// common pattern: **/some.txt just need basename check
function trivia2(base,originalPattern){const slashBase=`/${base}`,backslashBase=`\\${base}`,parsedPattern=function(path,basename){return"string"!==typeof path?null:basename?basename===base?originalPattern:null:path===base||path.endsWith(slashBase)||path.endsWith(backslashBase)?originalPattern:null},basenames=[base];return parsedPattern.basenames=basenames,parsedPattern.patterns=[originalPattern],parsedPattern.allBasenames=basenames,parsedPattern}
// repetition of common patterns (see above) {**/*.txt,**/*.png}
function trivia3(pattern,options){const parsedPatterns=aggregateBasenameMatches(pattern.slice(1,-1).split(",").map((pattern=>parsePattern(pattern,options))).filter((pattern=>pattern!==NULL)),pattern),n=parsedPatterns.length;if(!n)return NULL;if(1===n)return parsedPatterns[0];const parsedPattern=function(path,basename){for(let i=0,n=parsedPatterns.length;i<n;i++)if(parsedPatterns[i](path,basename))return pattern;return null},withBasenames=parsedPatterns.find((pattern=>!!pattern.allBasenames));withBasenames&&(parsedPattern.allBasenames=withBasenames.allBasenames);const allPaths=parsedPatterns.reduce(((all,current)=>current.allPaths?all.concat(current.allPaths):all),[]);return allPaths.length&&(parsedPattern.allPaths=allPaths),parsedPattern}
// common patterns: **/something/else just need endsWith check, something/else just needs and equals check
function trivia4and5(targetPath,pattern,matchPathEnds){const usingPosixSep=_path_js__WEBPACK_IMPORTED_MODULE_3__/* .sep */.ir===_path_js__WEBPACK_IMPORTED_MODULE_3__/* .posix */.KR.sep,nativePath=usingPosixSep?targetPath:targetPath.replace(ALL_FORWARD_SLASHES,_path_js__WEBPACK_IMPORTED_MODULE_3__/* .sep */.ir),nativePathEnd=_path_js__WEBPACK_IMPORTED_MODULE_3__/* .sep */.ir+nativePath,targetPathEnd=_path_js__WEBPACK_IMPORTED_MODULE_3__/* .posix */.KR.sep+targetPath,parsedPattern=matchPathEnds?function(testPath,basename){return"string"!==typeof testPath||testPath!==nativePath&&!testPath.endsWith(nativePathEnd)&&(usingPosixSep||testPath!==targetPath&&!testPath.endsWith(targetPathEnd))?null:pattern}:function(testPath,basename){return"string"!==typeof testPath||testPath!==nativePath&&(usingPosixSep||testPath!==targetPath)?null:pattern};return parsedPattern.allPaths=[(matchPathEnds?"*/":"./")+targetPath],parsedPattern}function toRegExp(pattern){try{const regExp=new RegExp(`^${parseRegExp(pattern)}$`);return function(path){// reset RegExp to its initial state to reuse it!
return regExp.lastIndex=0,"string"===typeof path&&regExp.test(path)?pattern:null}}catch(error){return NULL}}function match(arg1,path,hasSibling){return!(!arg1||"string"!==typeof path)&&parse(arg1)(path,void 0,hasSibling)}function parse(arg1,options={}){if(!arg1)return FALSE;
// Glob with String
if("string"===typeof arg1||isRelativePattern(arg1)){const parsedPattern=parsePattern(arg1,options);if(parsedPattern===NULL)return FALSE;const resultPattern=function(path,basename){return!!parsedPattern(path,basename)};return parsedPattern.allBasenames&&(resultPattern.allBasenames=parsedPattern.allBasenames),parsedPattern.allPaths&&(resultPattern.allPaths=parsedPattern.allPaths),resultPattern}
// Glob with Expression
return parsedExpression(arg1,options)}function isRelativePattern(obj){const rp=obj;return rp&&"string"===typeof rp.base&&"string"===typeof rp.pattern}function parsedExpression(expression,options){const parsedPatterns=aggregateBasenameMatches(Object.getOwnPropertyNames(expression).map((pattern=>parseExpressionPattern(pattern,expression[pattern],options))).filter((pattern=>pattern!==NULL))),n=parsedPatterns.length;if(!n)return NULL;if(!parsedPatterns.some((parsedPattern=>!!parsedPattern.requiresSiblings))){if(1===n)return parsedPatterns[0];const resultExpression=function(path,basename){for(let i=0,n=parsedPatterns.length;i<n;i++){
// Pattern matches path
const result=parsedPatterns[i](path,basename);if(result)return result}return null},withBasenames=parsedPatterns.find((pattern=>!!pattern.allBasenames));withBasenames&&(resultExpression.allBasenames=withBasenames.allBasenames);const allPaths=parsedPatterns.reduce(((all,current)=>current.allPaths?all.concat(current.allPaths):all),[]);return allPaths.length&&(resultExpression.allPaths=allPaths),resultExpression}const resultExpression=function(path,basename,hasSibling){let name;for(let i=0,n=parsedPatterns.length;i<n;i++){
// Pattern matches path
const parsedPattern=parsedPatterns[i];parsedPattern.requiresSiblings&&hasSibling&&(basename||(basename=_path_js__WEBPACK_IMPORTED_MODULE_3__/* .basename */.EZ(path)),name||(name=basename.substr(0,basename.length-_path_js__WEBPACK_IMPORTED_MODULE_3__/* .extname */.DZ(path).length)));const result=parsedPattern(path,basename,name,hasSibling);if(result)return result}return null},withBasenames=parsedPatterns.find((pattern=>!!pattern.allBasenames));withBasenames&&(resultExpression.allBasenames=withBasenames.allBasenames);const allPaths=parsedPatterns.reduce(((all,current)=>current.allPaths?all.concat(current.allPaths):all),[]);return allPaths.length&&(resultExpression.allPaths=allPaths),resultExpression}function parseExpressionPattern(pattern,value,options){if(!1===value)return NULL;// pattern is disabled
const parsedPattern=parsePattern(pattern,options);if(parsedPattern===NULL)return NULL;
// Expression Pattern is <boolean>
if("boolean"===typeof value)return parsedPattern;
// Expression Pattern is <SiblingClause>
if(value){const when=value.when;if("string"===typeof when){const result=(path,basename,name,hasSibling)=>{if(!hasSibling||!parsedPattern(path,basename))return null;const clausePattern=when.replace("$(basename)",name),matched=hasSibling(clausePattern);return(0,_async_js__WEBPACK_IMPORTED_MODULE_0__/* .isThenable */.J8)(matched)?matched.then((m=>m?pattern:null)):matched?pattern:null};return result.requiresSiblings=!0,result}}
// Expression is Anything
return parsedPattern}function aggregateBasenameMatches(parsedPatterns,result){const basenamePatterns=parsedPatterns.filter((parsedPattern=>!!parsedPattern.basenames));if(basenamePatterns.length<2)return parsedPatterns;const basenames=basenamePatterns.reduce(((all,current)=>{const basenames=current.basenames;return basenames?all.concat(basenames):all}),[]);let patterns;if(result){patterns=[];for(let i=0,n=basenames.length;i<n;i++)patterns.push(result)}else patterns=basenamePatterns.reduce(((all,current)=>{const patterns=current.patterns;return patterns?all.concat(patterns):all}),[]);const aggregate=function(path,basename){if("string"!==typeof path)return null;if(!basename){let i;for(i=path.length;i>0;i--){const ch=path.charCodeAt(i-1);if(47/* Slash */===ch||92/* Backslash */===ch)break}basename=path.substr(i)}const index=basenames.indexOf(basename);return-1!==index?patterns[index]:null};aggregate.basenames=basenames,aggregate.patterns=patterns,aggregate.allBasenames=basenames;const aggregatedPatterns=parsedPatterns.filter((parsedPattern=>!parsedPattern.basenames));return aggregatedPatterns.push(aggregate),aggregatedPatterns}
/***/},
/***/89954:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Cv:function(){/* binding */return stringHash},
/* harmony export */SP:function(){/* binding */return doHash},
/* harmony export */vp:function(){/* binding */return hash},
/* harmony export */yP:function(){/* binding */return StringSHA1}
/* harmony export */});
/* unused harmony exports numberHash, toHexString */
/* harmony import */var _strings_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(697295);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/**
 * Return a hash value for an object.
 */function hash(obj){return doHash(obj,0)}function doHash(obj,hashVal){switch(typeof obj){case"object":return null===obj?numberHash(349,hashVal):Array.isArray(obj)?arrayHash(obj,hashVal):objectHash(obj,hashVal);case"string":return stringHash(obj,hashVal);case"boolean":return booleanHash(obj,hashVal);case"number":return numberHash(obj,hashVal);case"undefined":return numberHash(937,hashVal);default:return numberHash(617,hashVal)}}function numberHash(val,initialHashVal){return(initialHashVal<<5)-initialHashVal+val|0;// hashVal * 31 + ch, keep as int32
}function booleanHash(b,initialHashVal){return numberHash(b?433:863,initialHashVal)}function stringHash(s,hashVal){hashVal=numberHash(149417,hashVal);for(let i=0,length=s.length;i<length;i++)hashVal=numberHash(s.charCodeAt(i),hashVal);return hashVal}function arrayHash(arr,initialHashVal){return initialHashVal=numberHash(104579,initialHashVal),arr.reduce(((hashVal,item)=>doHash(item,hashVal)),initialHashVal)}function objectHash(obj,initialHashVal){return initialHashVal=numberHash(181387,initialHashVal),Object.keys(obj).sort().reduce(((hashVal,key)=>(hashVal=stringHash(key,hashVal),doHash(obj[key],hashVal))),initialHashVal)}function leftRotate(value,bits,totalBits=32){
// delta + bits = totalBits
const delta=totalBits-bits,mask=~((1<<delta)-1);
// All ones, expect `delta` zeros aligned to the right
// Join (value left-shifted `bits` bits) with (masked value right-shifted `delta` bits)
return(value<<bits|(mask&value)>>>delta)>>>0}function fill(dest,index=0,count=dest.byteLength,value=0){for(let i=0;i<count;i++)dest[index+i]=value}function leftPad(value,length,char="0"){while(value.length<length)value=char+value;return value}function toHexString(bufferOrValue,bitsize=32){return bufferOrValue instanceof ArrayBuffer?Array.from(new Uint8Array(bufferOrValue)).map((b=>b.toString(16).padStart(2,"0"))).join(""):leftPad((bufferOrValue>>>0).toString(16),bitsize/4)}
/**
 * A SHA1 implementation that works with strings and does not allocate.
 */class StringSHA1{constructor(){this._h0=1732584193,this._h1=4023233417,this._h2=2562383102,this._h3=271733878,this._h4=3285377520,this._buff=new Uint8Array(67/* to fit any utf-8 */),this._buffDV=new DataView(this._buff.buffer),this._buffLen=0,this._totalLen=0,this._leftoverHighSurrogate=0,this._finished=!1}update(str){const strLen=str.length;if(0===strLen)return;const buff=this._buff;let charCode,offset,buffLen=this._buffLen,leftoverHighSurrogate=this._leftoverHighSurrogate;0!==leftoverHighSurrogate?(charCode=leftoverHighSurrogate,offset=-1,leftoverHighSurrogate=0):(charCode=str.charCodeAt(0),offset=0);while(1){let codePoint=charCode;if(_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .isHighSurrogate */.ZG(charCode)){if(!(offset+1<strLen)){
// last character is a surrogate pair
leftoverHighSurrogate=charCode;break}{const nextCharCode=str.charCodeAt(offset+1);_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .isLowSurrogate */.YK(nextCharCode)?(offset++,codePoint=_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .computeCodePoint */.rL(charCode,nextCharCode)):
// illegal => unicode replacement character
codePoint=65533/* UNICODE_REPLACEMENT */}}else _strings_js__WEBPACK_IMPORTED_MODULE_0__/* .isLowSurrogate */.YK(charCode)&&(
// illegal => unicode replacement character
codePoint=65533/* UNICODE_REPLACEMENT */);if(buffLen=this._push(buff,buffLen,codePoint),offset++,!(offset<strLen))break;charCode=str.charCodeAt(offset)}this._buffLen=buffLen,this._leftoverHighSurrogate=leftoverHighSurrogate}_push(buff,buffLen,codePoint){return codePoint<128?buff[buffLen++]=codePoint:codePoint<2048?(buff[buffLen++]=192|(1984&codePoint)>>>6,buff[buffLen++]=128|(63&codePoint)>>>0):codePoint<65536?(buff[buffLen++]=224|(61440&codePoint)>>>12,buff[buffLen++]=128|(4032&codePoint)>>>6,buff[buffLen++]=128|(63&codePoint)>>>0):(buff[buffLen++]=240|(1835008&codePoint)>>>18,buff[buffLen++]=128|(258048&codePoint)>>>12,buff[buffLen++]=128|(4032&codePoint)>>>6,buff[buffLen++]=128|(63&codePoint)>>>0),buffLen>=64/* BLOCK_SIZE */&&(this._step(),buffLen-=64/* BLOCK_SIZE */,this._totalLen+=64/* BLOCK_SIZE */,
// take last 3 in case of UTF8 overflow
buff[0]=buff[64],buff[1]=buff[65],buff[2]=buff[66]),buffLen}digest(){return this._finished||(this._finished=!0,this._leftoverHighSurrogate&&(
// illegal => unicode replacement character
this._leftoverHighSurrogate=0,this._buffLen=this._push(this._buff,this._buffLen,65533/* UNICODE_REPLACEMENT */)),this._totalLen+=this._buffLen,this._wrapUp()),toHexString(this._h0)+toHexString(this._h1)+toHexString(this._h2)+toHexString(this._h3)+toHexString(this._h4)}_wrapUp(){this._buff[this._buffLen++]=128,fill(this._buff,this._buffLen),this._buffLen>56&&(this._step(),fill(this._buff));
// this will fit because the mantissa can cover up to 52 bits
const ml=8*this._totalLen;this._buffDV.setUint32(56,Math.floor(ml/4294967296),!1),this._buffDV.setUint32(60,ml%4294967296,!1),this._step()}_step(){const bigBlock32=StringSHA1._bigBlock32,data=this._buffDV;for(let j=0;j<64/* 16*4 */;j+=4)bigBlock32.setUint32(j,data.getUint32(j,!1),!1);for(let j=64;j<320/* 80*4 */;j+=4)bigBlock32.setUint32(j,leftRotate(bigBlock32.getUint32(j-12,!1)^bigBlock32.getUint32(j-32,!1)^bigBlock32.getUint32(j-56,!1)^bigBlock32.getUint32(j-64,!1),1),!1);let f,k,temp,a=this._h0,b=this._h1,c=this._h2,d=this._h3,e=this._h4;for(let j=0;j<80;j++)j<20?(f=b&c|~b&d,k=1518500249):j<40?(f=b^c^d,k=1859775393):j<60?(f=b&c|b&d|c&d,k=2400959708):(f=b^c^d,k=3395469782),temp=leftRotate(a,5)+f+e+k+bigBlock32.getUint32(4*j,!1)&4294967295,e=d,d=c,c=leftRotate(b,30),b=a,a=temp;this._h0=this._h0+a&4294967295,this._h1=this._h1+b&4294967295,this._h2=this._h2+c&4294967295,this._h3=this._h3+d&4294967295,this._h4=this._h4+e&4294967295}}StringSHA1._bigBlock32=new DataView(new ArrayBuffer(320))},
/***/534485:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{L:function(){/* binding */return HistoryNavigator}});// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/navigator.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class ArrayNavigator{constructor(items,start=0,end=items.length,index=start-1){this.items=items,this.start=start,this.end=end,this.index=index}current(){return this.index===this.start-1||this.index===this.end?null:this.items[this.index]}next(){return this.index=Math.min(this.index+1,this.end),this.current()}previous(){return this.index=Math.max(this.index-1,this.start-1),this.current()}first(){return this.index=this.start,this.current()}last(){return this.index=this.end-1,this.current()}}// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/history.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class HistoryNavigator{constructor(history=[],limit=10){this._initialize(history),this._limit=limit,this._onChange()}getHistory(){return this._elements}add(t){this._history.delete(t),this._history.add(t),this._onChange()}next(){return this._currentPosition()!==this._elements.length-1?this._navigator.next():null}previous(){return 0!==this._currentPosition()?this._navigator.previous():null}current(){return this._navigator.current()}first(){return this._navigator.first()}last(){return this._navigator.last()}has(t){return this._history.has(t)}_onChange(){this._reduceToLimit();const elements=this._elements;this._navigator=new ArrayNavigator(elements,0,elements.length,elements.length)}_reduceToLimit(){const data=this._elements;data.length>this._limit&&this._initialize(data.slice(data.length-this._limit))}_currentPosition(){const currentElement=this._navigator.current();return currentElement?this._elements.indexOf(currentElement):-1}_initialize(history){this._history=new Set;for(const entry of history)this._history.add(entry)}get _elements(){const elements=[];return this._history.forEach((e=>elements.push(e))),elements}}
/***/},
/***/459365:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */CP:function(){/* binding */return isEmptyMarkdownString},
/* harmony export */Fr:function(){/* binding */return isMarkdownString},
/* harmony export */W5:function(){/* binding */return MarkdownString},
/* harmony export */oR:function(){/* binding */return removeMarkdownEscapes},
/* harmony export */v1:function(){/* binding */return parseHrefAndDimensions}
/* harmony export */});
/* unused harmony export escapeMarkdownSyntaxTokens */
/* harmony import */var _errors_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(817301),_iconLabels_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(721212);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class MarkdownString{constructor(value="",isTrustedOrOptions=!1){var _a,_b,_c;if(this.value=value,"string"!==typeof this.value)throw(0,_errors_js__WEBPACK_IMPORTED_MODULE_0__/* .illegalArgument */.b1)("value");"boolean"===typeof isTrustedOrOptions?(this.isTrusted=isTrustedOrOptions,this.supportThemeIcons=!1,this.supportHtml=!1):(this.isTrusted=null!==(_a=isTrustedOrOptions.isTrusted)&&void 0!==_a?_a:void 0,this.supportThemeIcons=null!==(_b=isTrustedOrOptions.supportThemeIcons)&&void 0!==_b&&_b,this.supportHtml=null!==(_c=isTrustedOrOptions.supportHtml)&&void 0!==_c&&_c)}appendText(value,newlineStyle=0/* Paragraph */){return this.value+=escapeMarkdownSyntaxTokens(this.supportThemeIcons?(0,_iconLabels_js__WEBPACK_IMPORTED_MODULE_1__/* .escapeIcons */.Qo)(value):value).replace(/([ \t]+)/g,((_match,g1)=>"&nbsp;".repeat(g1.length))).replace(/\>/gm,"\\>").replace(/\n/g,1/* Break */===newlineStyle?"\\\n":"\n\n"),this}appendMarkdown(value){return this.value+=value,this}appendCodeblock(langId,code){return this.value+="\n```",this.value+=langId,this.value+="\n",this.value+=code,this.value+="\n```\n",this}}function isEmptyMarkdownString(oneOrMany){return isMarkdownString(oneOrMany)?!oneOrMany.value:!Array.isArray(oneOrMany)||oneOrMany.every(isEmptyMarkdownString)}function isMarkdownString(thing){return thing instanceof MarkdownString||!(!thing||"object"!==typeof thing)&&("string"===typeof thing.value&&("boolean"===typeof thing.isTrusted||void 0===thing.isTrusted)&&("boolean"===typeof thing.supportThemeIcons||void 0===thing.supportThemeIcons))}function escapeMarkdownSyntaxTokens(text){
// escape markdown syntax tokens: http://daringfireball.net/projects/markdown/syntax#backslash
return text.replace(/[\\`*_{}[\]()#+\-!]/g,"\\$&")}function removeMarkdownEscapes(text){return text?text.replace(/\\([\\`*_{}[\]()#+\-.!])/g,"$1"):text}function parseHrefAndDimensions(href){const dimensions=[],splitted=href.split("|").map((s=>s.trim()));href=splitted[0];const parameters=splitted[1];if(parameters){const heightFromParams=/height=(\d+)/.exec(parameters),widthFromParams=/width=(\d+)/.exec(parameters),height=heightFromParams?heightFromParams[1]:"",width=widthFromParams?widthFromParams[1]:"",widthIsFinite=isFinite(parseInt(width)),heightIsFinite=isFinite(parseInt(height));widthIsFinite&&dimensions.push(`width="${width}"`),heightIsFinite&&dimensions.push(`height="${height}"`)}return{href:href,dimensions:dimensions}}
/***/},
/***/721212:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Gt:function(){/* binding */return matchesFuzzyIconAware},
/* harmony export */Ho:function(){/* binding */return parseLabelWithIcons},
/* harmony export */Qo:function(){/* binding */return escapeIcons},
/* harmony export */f$:function(){/* binding */return markdownEscapeEscapedIcons},
/* harmony export */x$:function(){/* binding */return stripIcons}
/* harmony export */});
/* unused harmony export iconStartMarker */
/* harmony import */var _codicons_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(773046),_filters_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(75392),_strings_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(697295);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const iconStartMarker="$(",iconsRegex=new RegExp(`\\$\\(${_codicons_js__WEBPACK_IMPORTED_MODULE_0__/* .CSSIcon */.dT.iconNameExpression}(?:${_codicons_js__WEBPACK_IMPORTED_MODULE_0__/* .CSSIcon */.dT.iconModifierExpression})?\\)`,"g"),escapeIconsRegex=new RegExp(`(\\\\)?${iconsRegex.source}`,"g");function escapeIcons(text){return text.replace(escapeIconsRegex,((match,escaped)=>escaped?match:`\\${match}`))}const markdownEscapedIconsRegex=new RegExp(`\\\\${iconsRegex.source}`,"g");function markdownEscapeEscapedIcons(text){
// Need to add an extra \ for escaping in markdown
return text.replace(markdownEscapedIconsRegex,(match=>`\\${match}`))}const stripIconsRegex=new RegExp(`(\\s)?(\\\\)?${iconsRegex.source}(\\s)?`,"g");function stripIcons(text){return-1===text.indexOf(iconStartMarker)?text:text.replace(stripIconsRegex,((match,preWhitespace,escaped,postWhitespace)=>escaped?match:preWhitespace||postWhitespace||""))}function parseLabelWithIcons(text){const firstIconIndex=text.indexOf(iconStartMarker);return-1===firstIconIndex?{text:text}:doParseLabelWithIcons(text,firstIconIndex)}function doParseLabelWithIcons(text,firstIconIndex){const iconOffsets=[];let textWithoutIcons="";function appendChars(chars){if(chars){textWithoutIcons+=chars;for(const _ of chars)iconOffsets.push(iconsOffset);// make sure to fill in icon offsets
}}let char,nextChar,currentIconStart=-1,currentIconValue="",iconsOffset=0,offset=firstIconIndex;const length=text.length;
// Append all characters until the first icon
appendChars(text.substr(0,firstIconIndex));
// example: $(file-symlink-file) my cool $(other-icon) entry
while(offset<length){
// beginning of icon: some value $( <--
if(char=text[offset],nextChar=text[offset+1],char===iconStartMarker[0]&&nextChar===iconStartMarker[1])currentIconStart=offset,
// if we had a previous potential icon value without
// the closing ')', it was actually not an icon and
// so we have to add it to the actual value
appendChars(currentIconValue),currentIconValue=iconStartMarker,offset++;else if(")"===char&&-1!==currentIconStart){const currentIconLength=offset-currentIconStart+1;// +1 to include the closing ')'
iconsOffset+=currentIconLength,currentIconStart=-1,currentIconValue=""}
// within icon
else-1!==currentIconStart?
// Make sure this is a real icon name
/^[a-z0-9\-]$/i.test(char)?currentIconValue+=char:(
// This is not a real icon, treat it as text
appendChars(currentIconValue),currentIconStart=-1,currentIconValue=""):appendChars(char);offset++}
// if we had a previous potential icon value without
// the closing ')', it was actually not an icon and
// so we have to add it to the actual value
return appendChars(currentIconValue),{text:textWithoutIcons,iconOffsets:iconOffsets}}function matchesFuzzyIconAware(query,target,enableSeparateSubstringMatching=!1){const{text:text,iconOffsets:iconOffsets}=target;
// Return early if there are no icon markers in the word to match against
if(!iconOffsets||0===iconOffsets.length)return(0,_filters_js__WEBPACK_IMPORTED_MODULE_1__/* .matchesFuzzy */.Oh)(query,text,enableSeparateSubstringMatching);
// Trim the word to match against because it could have leading
// whitespace now if the word started with an icon
const wordToMatchAgainstWithoutIconsTrimmed=(0,_strings_js__WEBPACK_IMPORTED_MODULE_2__/* .ltrim */.j3)(text," "),leadingWhitespaceOffset=text.length-wordToMatchAgainstWithoutIconsTrimmed.length,matches=(0,_filters_js__WEBPACK_IMPORTED_MODULE_1__/* .matchesFuzzy */.Oh)(query,wordToMatchAgainstWithoutIconsTrimmed,enableSeparateSubstringMatching);
// Map matches back to offsets with icon and trimming
if(matches)for(const match of matches){const iconOffset=iconOffsets[match.start+leadingWhitespaceOffset]/* icon offsets at index */+leadingWhitespaceOffset/* overall leading whitespace offset */;match.start+=iconOffset,match.end+=iconOffset}return matches}
/***/},
/***/844742:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */R:function(){/* binding */return IdGenerator},
/* harmony export */a:function(){/* binding */return defaultGenerator}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class IdGenerator{constructor(prefix){this._prefix=prefix,this._lastId=0}nextId(){return this._prefix+ ++this._lastId}}const defaultGenerator=new IdGenerator("id#");
/***/},
/***/353725:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var Iterable;
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$:function(){/* binding */return Iterable}
/* harmony export */}),function(Iterable){function is(thing){return thing&&"object"===typeof thing&&"function"===typeof thing[Symbol.iterator]}Iterable.is=is;const _empty=Object.freeze([]);function empty(){return _empty}function*single(element){yield element}function from(iterable){return iterable||_empty}function isEmpty(iterable){return!iterable||!0===iterable[Symbol.iterator]().next().done}function first(iterable){return iterable[Symbol.iterator]().next().value}function some(iterable,predicate){for(const element of iterable)if(predicate(element))return!0;return!1}function find(iterable,predicate){for(const element of iterable)if(predicate(element))return element}function*filter(iterable,predicate){for(const element of iterable)predicate(element)&&(yield element)}function*map(iterable,fn){let index=0;for(const element of iterable)yield fn(element,index++)}function*concat(...iterables){for(const iterable of iterables)for(const element of iterable)yield element}function*concatNested(iterables){for(const iterable of iterables)for(const element of iterable)yield element}function reduce(iterable,reducer,initialValue){let value=initialValue;for(const element of iterable)value=reducer(value,element);return value}
/**
     * Returns an iterable slice of the array, with the same semantics as `array.slice()`.
     */
function*slice(arr,from,to=arr.length){for(from<0&&(from+=arr.length),to<0?to+=arr.length:to>arr.length&&(to=arr.length);from<to;from++)yield arr[from]}
/**
     * Consumes `atMost` elements from iterable and returns the consumed elements,
     * and an iterable for the rest of the elements.
     */
function consume(iterable,atMost=Number.POSITIVE_INFINITY){const consumed=[];if(0===atMost)return[consumed,iterable];const iterator=iterable[Symbol.iterator]();for(let i=0;i<atMost;i++){const next=iterator.next();if(next.done)return[consumed,Iterable.empty()];consumed.push(next.value)}return[consumed,{[Symbol.iterator](){return iterator}}]}
/**
     * Returns whether the iterables are the same length and all items are
     * equal using the comparator function.
     */
function equals(a,b,comparator=((at,bt)=>at===bt)){const ai=a[Symbol.iterator](),bi=b[Symbol.iterator]();while(1){const an=ai.next(),bn=bi.next();if(an.done!==bn.done)return!1;if(an.done)return!0;if(!comparator(an.value,bn.value))return!1}}Iterable.empty=empty,Iterable.single=single,Iterable.from=from,Iterable.isEmpty=isEmpty,Iterable.first=first,Iterable.some=some,Iterable.find=find,Iterable.filter=filter,Iterable.map=map,Iterable.concat=concat,Iterable.concatNested=concatNested,Iterable.reduce=reduce,Iterable.slice=slice,Iterable.consume=consume,Iterable.equals=equals}(Iterable||(Iterable={}))},
/***/22258:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */H_:function(){/* binding */return EVENT_KEY_CODE_MAP},
/* harmony export */Vd:function(){/* binding */return IMMUTABLE_CODE_TO_KEY_CODE},
/* harmony export */gx:function(){/* binding */return KeyChord},
/* harmony export */kL:function(){/* binding */return KeyCodeUtils}
/* harmony export */});
/* unused harmony exports NATIVE_WINDOWS_KEY_CODE_TO_KEY_CODE, IMMUTABLE_KEY_CODE_TO_CODE */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class KeyCodeStrMap{constructor(){this._keyCodeToStr=[],this._strToKeyCode=Object.create(null)}define(keyCode,str){this._keyCodeToStr[keyCode]=str,this._strToKeyCode[str.toLowerCase()]=keyCode}keyCodeToStr(keyCode){return this._keyCodeToStr[keyCode]}strToKeyCode(str){return this._strToKeyCode[str.toLowerCase()]||0/* Unknown */}}const uiMap=new KeyCodeStrMap,userSettingsUSMap=new KeyCodeStrMap,userSettingsGeneralMap=new KeyCodeStrMap,EVENT_KEY_CODE_MAP=new Array(230),NATIVE_WINDOWS_KEY_CODE_TO_KEY_CODE={},scanCodeIntToStr=[],scanCodeStrToInt=Object.create(null),scanCodeLowerCaseStrToInt=Object.create(null),IMMUTABLE_CODE_TO_KEY_CODE=[],IMMUTABLE_KEY_CODE_TO_CODE=[];for(let i=0;i<=193/* MAX_VALUE */;i++)IMMUTABLE_CODE_TO_KEY_CODE[i]=-1/* DependsOnKbLayout */;for(let i=0;i<=126/* MAX_VALUE */;i++)IMMUTABLE_KEY_CODE_TO_CODE[i]=-1/* DependsOnKbLayout */;var KeyCodeUtils;function KeyChord(firstPart,secondPart){const chordPart=(65535&secondPart)<<16>>>0;return(firstPart|chordPart)>>>0}
/***/(function(){
// See https://msdn.microsoft.com/en-us/library/windows/desktop/dd375731(v=vs.85).aspx
// See https://github.com/microsoft/node-native-keymap/blob/master/deps/chromium/keyboard_codes_win.h
const empty="",mappings=[
// keyCodeOrd, immutable, scanCode, scanCodeStr, keyCode, keyCodeStr, eventKeyCode, vkey, usUserSettingsLabel, generalUserSettingsLabel
[0,1,0/* None */,"None",0/* Unknown */,"unknown",0,"VK_UNKNOWN",empty,empty],[0,1,1/* Hyper */,"Hyper",0/* Unknown */,empty,0,empty,empty,empty],[0,1,2/* Super */,"Super",0/* Unknown */,empty,0,empty,empty,empty],[0,1,3/* Fn */,"Fn",0/* Unknown */,empty,0,empty,empty,empty],[0,1,4/* FnLock */,"FnLock",0/* Unknown */,empty,0,empty,empty,empty],[0,1,5/* Suspend */,"Suspend",0/* Unknown */,empty,0,empty,empty,empty],[0,1,6/* Resume */,"Resume",0/* Unknown */,empty,0,empty,empty,empty],[0,1,7/* Turbo */,"Turbo",0/* Unknown */,empty,0,empty,empty,empty],[0,1,8/* Sleep */,"Sleep",0/* Unknown */,empty,0,"VK_SLEEP",empty,empty],[0,1,9/* WakeUp */,"WakeUp",0/* Unknown */,empty,0,empty,empty,empty],[31,0,10/* KeyA */,"KeyA",31/* KeyA */,"A",65,"VK_A",empty,empty],[32,0,11/* KeyB */,"KeyB",32/* KeyB */,"B",66,"VK_B",empty,empty],[33,0,12/* KeyC */,"KeyC",33/* KeyC */,"C",67,"VK_C",empty,empty],[34,0,13/* KeyD */,"KeyD",34/* KeyD */,"D",68,"VK_D",empty,empty],[35,0,14/* KeyE */,"KeyE",35/* KeyE */,"E",69,"VK_E",empty,empty],[36,0,15/* KeyF */,"KeyF",36/* KeyF */,"F",70,"VK_F",empty,empty],[37,0,16/* KeyG */,"KeyG",37/* KeyG */,"G",71,"VK_G",empty,empty],[38,0,17/* KeyH */,"KeyH",38/* KeyH */,"H",72,"VK_H",empty,empty],[39,0,18/* KeyI */,"KeyI",39/* KeyI */,"I",73,"VK_I",empty,empty],[40,0,19/* KeyJ */,"KeyJ",40/* KeyJ */,"J",74,"VK_J",empty,empty],[41,0,20/* KeyK */,"KeyK",41/* KeyK */,"K",75,"VK_K",empty,empty],[42,0,21/* KeyL */,"KeyL",42/* KeyL */,"L",76,"VK_L",empty,empty],[43,0,22/* KeyM */,"KeyM",43/* KeyM */,"M",77,"VK_M",empty,empty],[44,0,23/* KeyN */,"KeyN",44/* KeyN */,"N",78,"VK_N",empty,empty],[45,0,24/* KeyO */,"KeyO",45/* KeyO */,"O",79,"VK_O",empty,empty],[46,0,25/* KeyP */,"KeyP",46/* KeyP */,"P",80,"VK_P",empty,empty],[47,0,26/* KeyQ */,"KeyQ",47/* KeyQ */,"Q",81,"VK_Q",empty,empty],[48,0,27/* KeyR */,"KeyR",48/* KeyR */,"R",82,"VK_R",empty,empty],[49,0,28/* KeyS */,"KeyS",49/* KeyS */,"S",83,"VK_S",empty,empty],[50,0,29/* KeyT */,"KeyT",50/* KeyT */,"T",84,"VK_T",empty,empty],[51,0,30/* KeyU */,"KeyU",51/* KeyU */,"U",85,"VK_U",empty,empty],[52,0,31/* KeyV */,"KeyV",52/* KeyV */,"V",86,"VK_V",empty,empty],[53,0,32/* KeyW */,"KeyW",53/* KeyW */,"W",87,"VK_W",empty,empty],[54,0,33/* KeyX */,"KeyX",54/* KeyX */,"X",88,"VK_X",empty,empty],[55,0,34/* KeyY */,"KeyY",55/* KeyY */,"Y",89,"VK_Y",empty,empty],[56,0,35/* KeyZ */,"KeyZ",56/* KeyZ */,"Z",90,"VK_Z",empty,empty],[22,0,36/* Digit1 */,"Digit1",22/* Digit1 */,"1",49,"VK_1",empty,empty],[23,0,37/* Digit2 */,"Digit2",23/* Digit2 */,"2",50,"VK_2",empty,empty],[24,0,38/* Digit3 */,"Digit3",24/* Digit3 */,"3",51,"VK_3",empty,empty],[25,0,39/* Digit4 */,"Digit4",25/* Digit4 */,"4",52,"VK_4",empty,empty],[26,0,40/* Digit5 */,"Digit5",26/* Digit5 */,"5",53,"VK_5",empty,empty],[27,0,41/* Digit6 */,"Digit6",27/* Digit6 */,"6",54,"VK_6",empty,empty],[28,0,42/* Digit7 */,"Digit7",28/* Digit7 */,"7",55,"VK_7",empty,empty],[29,0,43/* Digit8 */,"Digit8",29/* Digit8 */,"8",56,"VK_8",empty,empty],[30,0,44/* Digit9 */,"Digit9",30/* Digit9 */,"9",57,"VK_9",empty,empty],[21,0,45/* Digit0 */,"Digit0",21/* Digit0 */,"0",48,"VK_0",empty,empty],[3,1,46/* Enter */,"Enter",3/* Enter */,"Enter",13,"VK_RETURN",empty,empty],[9,1,47/* Escape */,"Escape",9/* Escape */,"Escape",27,"VK_ESCAPE",empty,empty],[1,1,48/* Backspace */,"Backspace",1/* Backspace */,"Backspace",8,"VK_BACK",empty,empty],[2,1,49/* Tab */,"Tab",2/* Tab */,"Tab",9,"VK_TAB",empty,empty],[10,1,50/* Space */,"Space",10/* Space */,"Space",32,"VK_SPACE",empty,empty],[83,0,51/* Minus */,"Minus",83/* Minus */,"-",189,"VK_OEM_MINUS","-","OEM_MINUS"],[81,0,52/* Equal */,"Equal",81/* Equal */,"=",187,"VK_OEM_PLUS","=","OEM_PLUS"],[87,0,53/* BracketLeft */,"BracketLeft",87/* BracketLeft */,"[",219,"VK_OEM_4","[","OEM_4"],[89,0,54/* BracketRight */,"BracketRight",89/* BracketRight */,"]",221,"VK_OEM_6","]","OEM_6"],[88,0,55/* Backslash */,"Backslash",88/* Backslash */,"\\",220,"VK_OEM_5","\\","OEM_5"],[0,0,56/* IntlHash */,"IntlHash",0/* Unknown */,empty,0,empty,empty,empty],[80,0,57/* Semicolon */,"Semicolon",80/* Semicolon */,";",186,"VK_OEM_1",";","OEM_1"],[90,0,58/* Quote */,"Quote",90/* Quote */,"'",222,"VK_OEM_7","'","OEM_7"],[86,0,59/* Backquote */,"Backquote",86/* Backquote */,"`",192,"VK_OEM_3","`","OEM_3"],[82,0,60/* Comma */,"Comma",82/* Comma */,",",188,"VK_OEM_COMMA",",","OEM_COMMA"],[84,0,61/* Period */,"Period",84/* Period */,".",190,"VK_OEM_PERIOD",".","OEM_PERIOD"],[85,0,62/* Slash */,"Slash",85/* Slash */,"/",191,"VK_OEM_2","/","OEM_2"],[8,1,63/* CapsLock */,"CapsLock",8/* CapsLock */,"CapsLock",20,"VK_CAPITAL",empty,empty],[59,1,64/* F1 */,"F1",59/* F1 */,"F1",112,"VK_F1",empty,empty],[60,1,65/* F2 */,"F2",60/* F2 */,"F2",113,"VK_F2",empty,empty],[61,1,66/* F3 */,"F3",61/* F3 */,"F3",114,"VK_F3",empty,empty],[62,1,67/* F4 */,"F4",62/* F4 */,"F4",115,"VK_F4",empty,empty],[63,1,68/* F5 */,"F5",63/* F5 */,"F5",116,"VK_F5",empty,empty],[64,1,69/* F6 */,"F6",64/* F6 */,"F6",117,"VK_F6",empty,empty],[65,1,70/* F7 */,"F7",65/* F7 */,"F7",118,"VK_F7",empty,empty],[66,1,71/* F8 */,"F8",66/* F8 */,"F8",119,"VK_F8",empty,empty],[67,1,72/* F9 */,"F9",67/* F9 */,"F9",120,"VK_F9",empty,empty],[68,1,73/* F10 */,"F10",68/* F10 */,"F10",121,"VK_F10",empty,empty],[69,1,74/* F11 */,"F11",69/* F11 */,"F11",122,"VK_F11",empty,empty],[70,1,75/* F12 */,"F12",70/* F12 */,"F12",123,"VK_F12",empty,empty],[0,1,76/* PrintScreen */,"PrintScreen",0/* Unknown */,empty,0,empty,empty,empty],[79,1,77/* ScrollLock */,"ScrollLock",79/* ScrollLock */,"ScrollLock",145,"VK_SCROLL",empty,empty],[7,1,78/* Pause */,"Pause",7/* PauseBreak */,"PauseBreak",19,"VK_PAUSE",empty,empty],[19,1,79/* Insert */,"Insert",19/* Insert */,"Insert",45,"VK_INSERT",empty,empty],[14,1,80/* Home */,"Home",14/* Home */,"Home",36,"VK_HOME",empty,empty],[11,1,81/* PageUp */,"PageUp",11/* PageUp */,"PageUp",33,"VK_PRIOR",empty,empty],[20,1,82/* Delete */,"Delete",20/* Delete */,"Delete",46,"VK_DELETE",empty,empty],[13,1,83/* End */,"End",13/* End */,"End",35,"VK_END",empty,empty],[12,1,84/* PageDown */,"PageDown",12/* PageDown */,"PageDown",34,"VK_NEXT",empty,empty],[17,1,85/* ArrowRight */,"ArrowRight",17/* RightArrow */,"RightArrow",39,"VK_RIGHT","Right",empty],[15,1,86/* ArrowLeft */,"ArrowLeft",15/* LeftArrow */,"LeftArrow",37,"VK_LEFT","Left",empty],[18,1,87/* ArrowDown */,"ArrowDown",18/* DownArrow */,"DownArrow",40,"VK_DOWN","Down",empty],[16,1,88/* ArrowUp */,"ArrowUp",16/* UpArrow */,"UpArrow",38,"VK_UP","Up",empty],[78,1,89/* NumLock */,"NumLock",78/* NumLock */,"NumLock",144,"VK_NUMLOCK",empty,empty],[108,1,90/* NumpadDivide */,"NumpadDivide",108/* NumpadDivide */,"NumPad_Divide",111,"VK_DIVIDE",empty,empty],[103,1,91/* NumpadMultiply */,"NumpadMultiply",103/* NumpadMultiply */,"NumPad_Multiply",106,"VK_MULTIPLY",empty,empty],[106,1,92/* NumpadSubtract */,"NumpadSubtract",106/* NumpadSubtract */,"NumPad_Subtract",109,"VK_SUBTRACT",empty,empty],[104,1,93/* NumpadAdd */,"NumpadAdd",104/* NumpadAdd */,"NumPad_Add",107,"VK_ADD",empty,empty],[3,1,94/* NumpadEnter */,"NumpadEnter",3/* Enter */,empty,0,empty,empty,empty],[94,1,95/* Numpad1 */,"Numpad1",94/* Numpad1 */,"NumPad1",97,"VK_NUMPAD1",empty,empty],[95,1,96/* Numpad2 */,"Numpad2",95/* Numpad2 */,"NumPad2",98,"VK_NUMPAD2",empty,empty],[96,1,97/* Numpad3 */,"Numpad3",96/* Numpad3 */,"NumPad3",99,"VK_NUMPAD3",empty,empty],[97,1,98/* Numpad4 */,"Numpad4",97/* Numpad4 */,"NumPad4",100,"VK_NUMPAD4",empty,empty],[98,1,99/* Numpad5 */,"Numpad5",98/* Numpad5 */,"NumPad5",101,"VK_NUMPAD5",empty,empty],[99,1,100/* Numpad6 */,"Numpad6",99/* Numpad6 */,"NumPad6",102,"VK_NUMPAD6",empty,empty],[100,1,101/* Numpad7 */,"Numpad7",100/* Numpad7 */,"NumPad7",103,"VK_NUMPAD7",empty,empty],[101,1,102/* Numpad8 */,"Numpad8",101/* Numpad8 */,"NumPad8",104,"VK_NUMPAD8",empty,empty],[102,1,103/* Numpad9 */,"Numpad9",102/* Numpad9 */,"NumPad9",105,"VK_NUMPAD9",empty,empty],[93,1,104/* Numpad0 */,"Numpad0",93/* Numpad0 */,"NumPad0",96,"VK_NUMPAD0",empty,empty],[107,1,105/* NumpadDecimal */,"NumpadDecimal",107/* NumpadDecimal */,"NumPad_Decimal",110,"VK_DECIMAL",empty,empty],[92,0,106/* IntlBackslash */,"IntlBackslash",92/* IntlBackslash */,"OEM_102",226,"VK_OEM_102",empty,empty],[58,1,107/* ContextMenu */,"ContextMenu",58/* ContextMenu */,"ContextMenu",93,empty,empty,empty],[0,1,108/* Power */,"Power",0/* Unknown */,empty,0,empty,empty,empty],[0,1,109/* NumpadEqual */,"NumpadEqual",0/* Unknown */,empty,0,empty,empty,empty],[71,1,110/* F13 */,"F13",71/* F13 */,"F13",124,"VK_F13",empty,empty],[72,1,111/* F14 */,"F14",72/* F14 */,"F14",125,"VK_F14",empty,empty],[73,1,112/* F15 */,"F15",73/* F15 */,"F15",126,"VK_F15",empty,empty],[74,1,113/* F16 */,"F16",74/* F16 */,"F16",127,"VK_F16",empty,empty],[75,1,114/* F17 */,"F17",75/* F17 */,"F17",128,"VK_F17",empty,empty],[76,1,115/* F18 */,"F18",76/* F18 */,"F18",129,"VK_F18",empty,empty],[77,1,116/* F19 */,"F19",77/* F19 */,"F19",130,"VK_F19",empty,empty],[0,1,117/* F20 */,"F20",0/* Unknown */,empty,0,"VK_F20",empty,empty],[0,1,118/* F21 */,"F21",0/* Unknown */,empty,0,"VK_F21",empty,empty],[0,1,119/* F22 */,"F22",0/* Unknown */,empty,0,"VK_F22",empty,empty],[0,1,120/* F23 */,"F23",0/* Unknown */,empty,0,"VK_F23",empty,empty],[0,1,121/* F24 */,"F24",0/* Unknown */,empty,0,"VK_F24",empty,empty],[0,1,122/* Open */,"Open",0/* Unknown */,empty,0,empty,empty,empty],[0,1,123/* Help */,"Help",0/* Unknown */,empty,0,empty,empty,empty],[0,1,124/* Select */,"Select",0/* Unknown */,empty,0,empty,empty,empty],[0,1,125/* Again */,"Again",0/* Unknown */,empty,0,empty,empty,empty],[0,1,126/* Undo */,"Undo",0/* Unknown */,empty,0,empty,empty,empty],[0,1,127/* Cut */,"Cut",0/* Unknown */,empty,0,empty,empty,empty],[0,1,128/* Copy */,"Copy",0/* Unknown */,empty,0,empty,empty,empty],[0,1,129/* Paste */,"Paste",0/* Unknown */,empty,0,empty,empty,empty],[0,1,130/* Find */,"Find",0/* Unknown */,empty,0,empty,empty,empty],[0,1,131/* AudioVolumeMute */,"AudioVolumeMute",112/* AudioVolumeMute */,"AudioVolumeMute",173,"VK_VOLUME_MUTE",empty,empty],[0,1,132/* AudioVolumeUp */,"AudioVolumeUp",113/* AudioVolumeUp */,"AudioVolumeUp",175,"VK_VOLUME_UP",empty,empty],[0,1,133/* AudioVolumeDown */,"AudioVolumeDown",114/* AudioVolumeDown */,"AudioVolumeDown",174,"VK_VOLUME_DOWN",empty,empty],[105,1,134/* NumpadComma */,"NumpadComma",105/* NUMPAD_SEPARATOR */,"NumPad_Separator",108,"VK_SEPARATOR",empty,empty],[110,0,135/* IntlRo */,"IntlRo",110/* ABNT_C1 */,"ABNT_C1",193,"VK_ABNT_C1",empty,empty],[0,1,136/* KanaMode */,"KanaMode",0/* Unknown */,empty,0,empty,empty,empty],[0,0,137/* IntlYen */,"IntlYen",0/* Unknown */,empty,0,empty,empty,empty],[0,1,138/* Convert */,"Convert",0/* Unknown */,empty,0,empty,empty,empty],[0,1,139/* NonConvert */,"NonConvert",0/* Unknown */,empty,0,empty,empty,empty],[0,1,140/* Lang1 */,"Lang1",0/* Unknown */,empty,0,empty,empty,empty],[0,1,141/* Lang2 */,"Lang2",0/* Unknown */,empty,0,empty,empty,empty],[0,1,142/* Lang3 */,"Lang3",0/* Unknown */,empty,0,empty,empty,empty],[0,1,143/* Lang4 */,"Lang4",0/* Unknown */,empty,0,empty,empty,empty],[0,1,144/* Lang5 */,"Lang5",0/* Unknown */,empty,0,empty,empty,empty],[0,1,145/* Abort */,"Abort",0/* Unknown */,empty,0,empty,empty,empty],[0,1,146/* Props */,"Props",0/* Unknown */,empty,0,empty,empty,empty],[0,1,147/* NumpadParenLeft */,"NumpadParenLeft",0/* Unknown */,empty,0,empty,empty,empty],[0,1,148/* NumpadParenRight */,"NumpadParenRight",0/* Unknown */,empty,0,empty,empty,empty],[0,1,149/* NumpadBackspace */,"NumpadBackspace",0/* Unknown */,empty,0,empty,empty,empty],[0,1,150/* NumpadMemoryStore */,"NumpadMemoryStore",0/* Unknown */,empty,0,empty,empty,empty],[0,1,151/* NumpadMemoryRecall */,"NumpadMemoryRecall",0/* Unknown */,empty,0,empty,empty,empty],[0,1,152/* NumpadMemoryClear */,"NumpadMemoryClear",0/* Unknown */,empty,0,empty,empty,empty],[0,1,153/* NumpadMemoryAdd */,"NumpadMemoryAdd",0/* Unknown */,empty,0,empty,empty,empty],[0,1,154/* NumpadMemorySubtract */,"NumpadMemorySubtract",0/* Unknown */,empty,0,empty,empty,empty],[0,1,155/* NumpadClear */,"NumpadClear",0/* Unknown */,empty,0,empty,empty,empty],[0,1,156/* NumpadClearEntry */,"NumpadClearEntry",0/* Unknown */,empty,0,empty,empty,empty],[5,1,0/* None */,empty,5/* Ctrl */,"Ctrl",17,"VK_CONTROL",empty,empty],[4,1,0/* None */,empty,4/* Shift */,"Shift",16,"VK_SHIFT",empty,empty],[6,1,0/* None */,empty,6/* Alt */,"Alt",18,"VK_MENU",empty,empty],[57,1,0/* None */,empty,57/* Meta */,"Meta",0,"VK_COMMAND",empty,empty],[5,1,157/* ControlLeft */,"ControlLeft",5/* Ctrl */,empty,0,"VK_LCONTROL",empty,empty],[4,1,158/* ShiftLeft */,"ShiftLeft",4/* Shift */,empty,0,"VK_LSHIFT",empty,empty],[6,1,159/* AltLeft */,"AltLeft",6/* Alt */,empty,0,"VK_LMENU",empty,empty],[57,1,160/* MetaLeft */,"MetaLeft",57/* Meta */,empty,0,"VK_LWIN",empty,empty],[5,1,161/* ControlRight */,"ControlRight",5/* Ctrl */,empty,0,"VK_RCONTROL",empty,empty],[4,1,162/* ShiftRight */,"ShiftRight",4/* Shift */,empty,0,"VK_RSHIFT",empty,empty],[6,1,163/* AltRight */,"AltRight",6/* Alt */,empty,0,"VK_RMENU",empty,empty],[57,1,164/* MetaRight */,"MetaRight",57/* Meta */,empty,0,"VK_RWIN",empty,empty],[0,1,165/* BrightnessUp */,"BrightnessUp",0/* Unknown */,empty,0,empty,empty,empty],[0,1,166/* BrightnessDown */,"BrightnessDown",0/* Unknown */,empty,0,empty,empty,empty],[0,1,167/* MediaPlay */,"MediaPlay",0/* Unknown */,empty,0,empty,empty,empty],[0,1,168/* MediaRecord */,"MediaRecord",0/* Unknown */,empty,0,empty,empty,empty],[0,1,169/* MediaFastForward */,"MediaFastForward",0/* Unknown */,empty,0,empty,empty,empty],[0,1,170/* MediaRewind */,"MediaRewind",0/* Unknown */,empty,0,empty,empty,empty],[114,1,171/* MediaTrackNext */,"MediaTrackNext",119/* MediaTrackNext */,"MediaTrackNext",176,"VK_MEDIA_NEXT_TRACK",empty,empty],[115,1,172/* MediaTrackPrevious */,"MediaTrackPrevious",120/* MediaTrackPrevious */,"MediaTrackPrevious",177,"VK_MEDIA_PREV_TRACK",empty,empty],[116,1,173/* MediaStop */,"MediaStop",121/* MediaStop */,"MediaStop",178,"VK_MEDIA_STOP",empty,empty],[0,1,174/* Eject */,"Eject",0/* Unknown */,empty,0,empty,empty,empty],[117,1,175/* MediaPlayPause */,"MediaPlayPause",122/* MediaPlayPause */,"MediaPlayPause",179,"VK_MEDIA_PLAY_PAUSE",empty,empty],[0,1,176/* MediaSelect */,"MediaSelect",123/* LaunchMediaPlayer */,"LaunchMediaPlayer",181,"VK_MEDIA_LAUNCH_MEDIA_SELECT",empty,empty],[0,1,177/* LaunchMail */,"LaunchMail",124/* LaunchMail */,"LaunchMail",180,"VK_MEDIA_LAUNCH_MAIL",empty,empty],[0,1,178/* LaunchApp2 */,"LaunchApp2",125/* LaunchApp2 */,"LaunchApp2",183,"VK_MEDIA_LAUNCH_APP2",empty,empty],[0,1,179/* LaunchApp1 */,"LaunchApp1",0/* Unknown */,empty,0,"VK_MEDIA_LAUNCH_APP1",empty,empty],[0,1,180/* SelectTask */,"SelectTask",0/* Unknown */,empty,0,empty,empty,empty],[0,1,181/* LaunchScreenSaver */,"LaunchScreenSaver",0/* Unknown */,empty,0,empty,empty,empty],[0,1,182/* BrowserSearch */,"BrowserSearch",115/* BrowserSearch */,"BrowserSearch",170,"VK_BROWSER_SEARCH",empty,empty],[0,1,183/* BrowserHome */,"BrowserHome",116/* BrowserHome */,"BrowserHome",172,"VK_BROWSER_HOME",empty,empty],[112,1,184/* BrowserBack */,"BrowserBack",117/* BrowserBack */,"BrowserBack",166,"VK_BROWSER_BACK",empty,empty],[113,1,185/* BrowserForward */,"BrowserForward",118/* BrowserForward */,"BrowserForward",167,"VK_BROWSER_FORWARD",empty,empty],[0,1,186/* BrowserStop */,"BrowserStop",0/* Unknown */,empty,0,"VK_BROWSER_STOP",empty,empty],[0,1,187/* BrowserRefresh */,"BrowserRefresh",0/* Unknown */,empty,0,"VK_BROWSER_REFRESH",empty,empty],[0,1,188/* BrowserFavorites */,"BrowserFavorites",0/* Unknown */,empty,0,"VK_BROWSER_FAVORITES",empty,empty],[0,1,189/* ZoomToggle */,"ZoomToggle",0/* Unknown */,empty,0,empty,empty,empty],[0,1,190/* MailReply */,"MailReply",0/* Unknown */,empty,0,empty,empty,empty],[0,1,191/* MailForward */,"MailForward",0/* Unknown */,empty,0,empty,empty,empty],[0,1,192/* MailSend */,"MailSend",0/* Unknown */,empty,0,empty,empty,empty],
// See https://lists.w3.org/Archives/Public/www-dom/2010JulSep/att-0182/keyCode-spec.html
// If an Input Method Editor is processing key input and the event is keydown, return 229.
[109,1,0/* None */,empty,109/* KEY_IN_COMPOSITION */,"KeyInComposition",229,empty,empty,empty],[111,1,0/* None */,empty,111/* ABNT_C2 */,"ABNT_C2",194,"VK_ABNT_C2",empty,empty],[91,1,0/* None */,empty,91/* OEM_8 */,"OEM_8",223,"VK_OEM_8",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_CLEAR",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_KANA",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_HANGUL",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_JUNJA",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_FINAL",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_HANJA",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_KANJI",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_CONVERT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_NONCONVERT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_ACCEPT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_MODECHANGE",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_SELECT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_PRINT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_EXECUTE",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_SNAPSHOT",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_HELP",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_APPS",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_PROCESSKEY",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_PACKET",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_DBE_SBCSCHAR",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_DBE_DBCSCHAR",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_ATTN",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_CRSEL",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_EXSEL",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_EREOF",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_PLAY",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_ZOOM",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_NONAME",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_PA1",empty,empty],[0,1,0/* None */,empty,0/* Unknown */,empty,0,"VK_OEM_CLEAR",empty,empty]];let seenKeyCode=[],seenScanCode=[];for(const mapping of mappings){const[_keyCodeOrd,immutable,scanCode,scanCodeStr,keyCode,keyCodeStr,eventKeyCode,vkey,usUserSettingsLabel,generalUserSettingsLabel]=mapping;if(seenScanCode[scanCode]||(seenScanCode[scanCode]=!0,scanCodeIntToStr[scanCode]=scanCodeStr,scanCodeStrToInt[scanCodeStr]=scanCode,scanCodeLowerCaseStrToInt[scanCodeStr.toLowerCase()]=scanCode,immutable&&(IMMUTABLE_CODE_TO_KEY_CODE[scanCode]=keyCode,0/* Unknown */!==keyCode&&3/* Enter */!==keyCode&&5/* Ctrl */!==keyCode&&4/* Shift */!==keyCode&&6/* Alt */!==keyCode&&57/* Meta */!==keyCode&&(IMMUTABLE_KEY_CODE_TO_CODE[keyCode]=scanCode))),!seenKeyCode[keyCode]){if(seenKeyCode[keyCode]=!0,!keyCodeStr)throw new Error(`String representation missing for key code ${keyCode} around scan code ${scanCodeStr}`);uiMap.define(keyCode,keyCodeStr),userSettingsUSMap.define(keyCode,usUserSettingsLabel||keyCodeStr),userSettingsGeneralMap.define(keyCode,generalUserSettingsLabel||usUserSettingsLabel||keyCodeStr)}eventKeyCode&&(EVENT_KEY_CODE_MAP[eventKeyCode]=keyCode),vkey&&(NATIVE_WINDOWS_KEY_CODE_TO_KEY_CODE[vkey]=keyCode)}
// Manually added due to the exclusion above (due to duplication with NumpadEnter)
IMMUTABLE_KEY_CODE_TO_CODE[3/* Enter */]=46/* Enter */})(),function(KeyCodeUtils){function toString(keyCode){return uiMap.keyCodeToStr(keyCode)}function fromString(key){return uiMap.strToKeyCode(key)}function toUserSettingsUS(keyCode){return userSettingsUSMap.keyCodeToStr(keyCode)}function toUserSettingsGeneral(keyCode){return userSettingsGeneralMap.keyCodeToStr(keyCode)}function fromUserSettings(key){return userSettingsUSMap.strToKeyCode(key)||userSettingsGeneralMap.strToKeyCode(key)}function toElectronAccelerator(keyCode){if(keyCode>=93/* Numpad0 */&&keyCode<=108/* NumpadDivide */)
// [Electron Accelerators] Electron is able to parse numpad keys, but unfortunately it
// renders them just as regular keys in menus. For example, num0 is rendered as "0",
// numdiv is rendered as "/", numsub is rendered as "-".
// This can lead to incredible confusion, as it makes numpad based keybindings indistinguishable
// from keybindings based on regular keys.
// We therefore need to fall back to custom rendering for numpad keys.
return null;switch(keyCode){case 16/* UpArrow */:return"Up";case 18/* DownArrow */:return"Down";case 15/* LeftArrow */:return"Left";case 17/* RightArrow */:return"Right"}return uiMap.keyCodeToStr(keyCode)}KeyCodeUtils.toString=toString,KeyCodeUtils.fromString=fromString,KeyCodeUtils.toUserSettingsUS=toUserSettingsUS,KeyCodeUtils.toUserSettingsGeneral=toUserSettingsGeneral,KeyCodeUtils.fromUserSettings=fromUserSettings,KeyCodeUtils.toElectronAccelerator=toElectronAccelerator}(KeyCodeUtils||(KeyCodeUtils={}))},
/***/708030:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */X4:function(){/* binding */return AriaLabelProvider},
/* harmony export */jC:function(){/* binding */return ElectronAcceleratorLabelProvider},
/* harmony export */xo:function(){/* binding */return UILabelProvider}
/* harmony export */});
/* unused harmony export ModifierLabelProvider */
/* harmony import */var _nls_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(663580);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/class ModifierLabelProvider{constructor(mac,windows,linux=windows){this.modifierLabels=[null],// index 0 will never me accessed.
this.modifierLabels[2/* Macintosh */]=mac,this.modifierLabels[1/* Windows */]=windows,this.modifierLabels[3/* Linux */]=linux}toLabel(OS,parts,keyLabelProvider){if(0===parts.length)return null;const result=[];for(let i=0,len=parts.length;i<len;i++){const part=parts[i],keyLabel=keyLabelProvider(part);if(null===keyLabel)
// this keybinding cannot be expressed...
return null;result[i]=_simpleAsString(part,keyLabel,this.modifierLabels[OS])}return result.join(" ")}}
/**
 * A label provider that prints modifiers in a suitable format for displaying in the UI.
 */const UILabelProvider=new ModifierLabelProvider({ctrlKey:"⌃",shiftKey:"⇧",altKey:"⌥",metaKey:"⌘",separator:""},{ctrlKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"ctrlKey",comment:["This is the short form for the Control key on the keyboard"]},"Ctrl"),shiftKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"shiftKey",comment:["This is the short form for the Shift key on the keyboard"]},"Shift"),altKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"altKey",comment:["This is the short form for the Alt key on the keyboard"]},"Alt"),metaKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"windowsKey",comment:["This is the short form for the Windows key on the keyboard"]},"Windows"),separator:"+"},{ctrlKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"ctrlKey",comment:["This is the short form for the Control key on the keyboard"]},"Ctrl"),shiftKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"shiftKey",comment:["This is the short form for the Shift key on the keyboard"]},"Shift"),altKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"altKey",comment:["This is the short form for the Alt key on the keyboard"]},"Alt"),metaKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"superKey",comment:["This is the short form for the Super key on the keyboard"]},"Super"),separator:"+"}),AriaLabelProvider=new ModifierLabelProvider({ctrlKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"ctrlKey.long",comment:["This is the long form for the Control key on the keyboard"]},"Control"),shiftKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"shiftKey.long",comment:["This is the long form for the Shift key on the keyboard"]},"Shift"),altKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"altKey.long",comment:["This is the long form for the Alt key on the keyboard"]},"Alt"),metaKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"cmdKey.long",comment:["This is the long form for the Command key on the keyboard"]},"Command"),separator:"+"},{ctrlKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"ctrlKey.long",comment:["This is the long form for the Control key on the keyboard"]},"Control"),shiftKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"shiftKey.long",comment:["This is the long form for the Shift key on the keyboard"]},"Shift"),altKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"altKey.long",comment:["This is the long form for the Alt key on the keyboard"]},"Alt"),metaKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"windowsKey.long",comment:["This is the long form for the Windows key on the keyboard"]},"Windows"),separator:"+"},{ctrlKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"ctrlKey.long",comment:["This is the long form for the Control key on the keyboard"]},"Control"),shiftKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"shiftKey.long",comment:["This is the long form for the Shift key on the keyboard"]},"Shift"),altKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"altKey.long",comment:["This is the long form for the Alt key on the keyboard"]},"Alt"),metaKey:_nls_js__WEBPACK_IMPORTED_MODULE_0__/* .localize */.N({key:"superKey.long",comment:["This is the long form for the Super key on the keyboard"]},"Super"),separator:"+"}),ElectronAcceleratorLabelProvider=new ModifierLabelProvider({ctrlKey:"Ctrl",shiftKey:"Shift",altKey:"Alt",metaKey:"Cmd",separator:"+"},{ctrlKey:"Ctrl",shiftKey:"Shift",altKey:"Alt",metaKey:"Super",separator:"+"});
/**
 * A label provider that prints modifiers in a suitable format for ARIA.
 */function _simpleAsString(modifiers,key,labels){if(null===key)return"";const result=[];
// translate modifier keys: Ctrl-Shift-Alt-Meta
return modifiers.ctrlKey&&result.push(labels.ctrlKey),modifiers.shiftKey&&result.push(labels.shiftKey),modifiers.altKey&&result.push(labels.altKey),modifiers.metaKey&&result.push(labels.metaKey),
// the actual key
""!==key&&result.push(key),result.join(labels.separator)}
/***/},
/***/408313:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */BQ:function(){/* binding */return ResolvedKeybindingPart},
/* harmony export */QC:function(){/* binding */return SimpleKeybinding},
/* harmony export */X_:function(){/* binding */return ChordKeybinding},
/* harmony export */f1:function(){/* binding */return ResolvedKeybinding},
/* harmony export */gm:function(){/* binding */return createKeybinding}
/* harmony export */});
/* unused harmony exports createSimpleKeybinding, ScanCodeBinding */
/* harmony import */var _errors_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(817301);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/function createKeybinding(keybinding,OS){if(0===keybinding)return null;const firstPart=(65535&keybinding)>>>0,chordPart=(4294901760&keybinding)>>>16;return new ChordKeybinding(0!==chordPart?[createSimpleKeybinding(firstPart,OS),createSimpleKeybinding(chordPart,OS)]:[createSimpleKeybinding(firstPart,OS)])}function createSimpleKeybinding(keybinding,OS){const ctrlCmd=!!(2048/* CtrlCmd */&keybinding),winCtrl=!!(256/* WinCtrl */&keybinding),ctrlKey=2/* Macintosh */===OS?winCtrl:ctrlCmd,shiftKey=!!(1024/* Shift */&keybinding),altKey=!!(512/* Alt */&keybinding),metaKey=2/* Macintosh */===OS?ctrlCmd:winCtrl,keyCode=255/* KeyCode */&keybinding;return new SimpleKeybinding(ctrlKey,shiftKey,altKey,metaKey,keyCode)}class SimpleKeybinding{constructor(ctrlKey,shiftKey,altKey,metaKey,keyCode){this.ctrlKey=ctrlKey,this.shiftKey=shiftKey,this.altKey=altKey,this.metaKey=metaKey,this.keyCode=keyCode}equals(other){return this.ctrlKey===other.ctrlKey&&this.shiftKey===other.shiftKey&&this.altKey===other.altKey&&this.metaKey===other.metaKey&&this.keyCode===other.keyCode}isModifierKey(){return 0/* Unknown */===this.keyCode||5/* Ctrl */===this.keyCode||57/* Meta */===this.keyCode||6/* Alt */===this.keyCode||4/* Shift */===this.keyCode}toChord(){return new ChordKeybinding([this])}
/**
     * Does this keybinding refer to the key code of a modifier and it also has the modifier flag?
     */isDuplicateModifierCase(){return this.ctrlKey&&5/* Ctrl */===this.keyCode||this.shiftKey&&4/* Shift */===this.keyCode||this.altKey&&6/* Alt */===this.keyCode||this.metaKey&&57/* Meta */===this.keyCode}}class ChordKeybinding{constructor(parts){if(0===parts.length)throw(0,_errors_js__WEBPACK_IMPORTED_MODULE_0__/* .illegalArgument */.b1)("parts");this.parts=parts}}class ResolvedKeybindingPart{constructor(ctrlKey,shiftKey,altKey,metaKey,kbLabel,kbAriaLabel){this.ctrlKey=ctrlKey,this.shiftKey=shiftKey,this.altKey=altKey,this.metaKey=metaKey,this.keyLabel=kbLabel,this.keyAriaLabel=kbAriaLabel}}
/**
 * A resolved keybinding. Can be a simple keybinding or a chord keybinding.
 */class ResolvedKeybinding{}
/***/},
/***/968843:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */D:function(){/* binding */return normalizeDriveLetter},
/* harmony export */p:function(){/* binding */return getBaseLabel}
/* harmony export */});
/* harmony import */var _extpath_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(915527),_network_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(566663),_platform_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(901432),_resources_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(195935),_uri_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(70666);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function getBaseLabel(resource){if(!resource)return;"string"===typeof resource&&(resource=_uri_js__WEBPACK_IMPORTED_MODULE_4__/* .URI */.o.file(resource));const base=(0,_resources_js__WEBPACK_IMPORTED_MODULE_3__/* .basename */.EZ)(resource)||(resource.scheme===_network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.file?resource.fsPath:resource.path/* can be empty string if '/' is passed in */);
// convert c: => C:
return _platform_js__WEBPACK_IMPORTED_MODULE_2__/* .isWindows */.ED&&(0,_extpath_js__WEBPACK_IMPORTED_MODULE_0__/* .isRootOrDriveLetter */.vY)(base)?normalizeDriveLetter(base):base}function normalizeDriveLetter(path){return(0,_extpath_js__WEBPACK_IMPORTED_MODULE_0__/* .hasDriveLetter */.oP)(path)?path.charAt(0).toUpperCase()+path.slice(1):path}
/***/},
/***/879579:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return Lazy}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class Lazy{constructor(executor){this.executor=executor,this._didRun=!1}
/**
     * Get the wrapped value.
     *
     * This will force evaluation of the lazy value if it has not been resolved yet. Lazy values are only
     * resolved once. `getValue` will re-throw exceptions that are hit while resolving the value
     */getValue(){if(!this._didRun)try{this._value=this.executor()}catch(err){this._error=err}finally{this._didRun=!0}if(this._error)throw this._error;return this._value}
/**
     * Get the wrapped value without forcing evaluation.
     */get rawValue(){return this._value}}
/***/},
/***/905976:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */B9:function(){/* binding */return dispose},
/* harmony export */F8:function(){/* binding */return combinedDisposable},
/* harmony export */JT:function(){/* binding */return Disposable},
/* harmony export */Jz:function(){/* binding */return ImmortalReference},
/* harmony export */OF:function(){/* binding */return toDisposable},
/* harmony export */SL:function(){/* binding */return DisposableStore},
/* harmony export */Wf:function(){/* binding */return isDisposable},
/* harmony export */XK:function(){/* binding */return MutableDisposable},
/* harmony export */dk:function(){/* binding */return markAsSingleton}
/* harmony export */});
/* unused harmony exports setDisposableTracker, MultiDisposeError */
/* harmony import */var _functional_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(888289),_iterator_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(353725);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/**
 * Enables logging of potentially leaked disposables.
 *
 * A disposable is considered leaked if it is not disposed or not registered as the child of
 * another disposable. This tracking is very simple an only works for classes that either
 * extend Disposable or use a DisposableStore. This means there are a lot of false positives.
 */
const TRACK_DISPOSABLES=!1;let disposableTracker=null;function setDisposableTracker(tracker){disposableTracker=tracker}if(TRACK_DISPOSABLES){const __is_disposable_tracked__="__is_disposable_tracked__";setDisposableTracker(new class{trackDisposable(x){new Error("Potentially leaked disposable").stack;setTimeout((()=>{x[__is_disposable_tracked__]}),3e3)}setParent(child,parent){if(child&&child!==Disposable.None)try{child[__is_disposable_tracked__]=!0}catch(_a){
// noop
}}markAsDisposed(disposable){if(disposable&&disposable!==Disposable.None)try{disposable[__is_disposable_tracked__]=!0}catch(_a){
// noop
}}markAsSingleton(disposable){}})}function trackDisposable(x){return null===disposableTracker||void 0===disposableTracker||disposableTracker.trackDisposable(x),x}function markAsDisposed(disposable){null===disposableTracker||void 0===disposableTracker||disposableTracker.markAsDisposed(disposable)}function setParentOfDisposable(child,parent){null===disposableTracker||void 0===disposableTracker||disposableTracker.setParent(child,parent)}function setParentOfDisposables(children,parent){if(disposableTracker)for(const child of children)disposableTracker.setParent(child,parent)}
/**
 * Indicates that the given object is a singleton which does not need to be disposed.
*/function markAsSingleton(singleton){return null===disposableTracker||void 0===disposableTracker||disposableTracker.markAsSingleton(singleton),singleton}class MultiDisposeError extends Error{constructor(errors){super(`Encountered errors while disposing of store. Errors: [${errors.join(", ")}]`),this.errors=errors}}function isDisposable(thing){return"function"===typeof thing.dispose&&0===thing.dispose.length}function dispose(arg){if(_iterator_js__WEBPACK_IMPORTED_MODULE_0__/* .Iterable */.$.is(arg)){let errors=[];for(const d of arg)if(d)try{d.dispose()}catch(e){errors.push(e)}if(1===errors.length)throw errors[0];if(errors.length>1)throw new MultiDisposeError(errors);return Array.isArray(arg)?[]:arg}if(arg)return arg.dispose(),arg}function combinedDisposable(...disposables){const parent=toDisposable((()=>dispose(disposables)));return setParentOfDisposables(disposables,parent),parent}function toDisposable(fn){const self=trackDisposable({dispose:(0,_functional_js__WEBPACK_IMPORTED_MODULE_1__/* .once */.I)((()=>{markAsDisposed(self),fn()}))});return self}class DisposableStore{constructor(){this._toDispose=new Set,this._isDisposed=!1,trackDisposable(this)}
/**
     * Dispose of all registered disposables and mark this object as disposed.
     *
     * Any future disposables added to this object will be disposed of on `add`.
     */dispose(){this._isDisposed||(markAsDisposed(this),this._isDisposed=!0,this.clear())}
/**
     * Dispose of all registered disposables but do not mark this object as disposed.
     */clear(){try{dispose(this._toDispose.values())}finally{this._toDispose.clear()}}add(o){if(!o)return o;if(o===this)throw new Error("Cannot register a disposable on itself!");return setParentOfDisposable(o,this),this._isDisposed?DisposableStore.DISABLE_DISPOSED_WARNING:this._toDispose.add(o),o}}DisposableStore.DISABLE_DISPOSED_WARNING=!1;class Disposable{constructor(){this._store=new DisposableStore,trackDisposable(this),setParentOfDisposable(this._store,this)}dispose(){markAsDisposed(this),this._store.dispose()}_register(o){if(o===this)throw new Error("Cannot register a disposable on itself!");return this._store.add(o)}}Disposable.None=Object.freeze({dispose(){}});
/**
 * Manages the lifecycle of a disposable value that may be changed.
 *
 * This ensures that when the disposable value is changed, the previously held disposable is disposed of. You can
 * also register a `MutableDisposable` on a `Disposable` to ensure it is automatically cleaned up.
 */
class MutableDisposable{constructor(){this._isDisposed=!1,trackDisposable(this)}get value(){return this._isDisposed?void 0:this._value}set value(value){var _a;this._isDisposed||value===this._value||(null===(_a=this._value)||void 0===_a||_a.dispose(),value&&setParentOfDisposable(value,this),this._value=value)}clear(){this.value=void 0}dispose(){var _a;this._isDisposed=!0,markAsDisposed(this),null===(_a=this._value)||void 0===_a||_a.dispose(),this._value=void 0}
/**
     * Clears the value, but does not dispose it.
     * The old value is returned.
    */clearAndLeak(){const oldValue=this._value;return this._value=void 0,oldValue&&setParentOfDisposable(oldValue,null),oldValue}}class ImmortalReference{constructor(object){this.object=object}dispose(){}}
/***/},
/***/791741:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */S:function(){/* binding */return LinkedList}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class Node{constructor(element){this.element=element,this.next=Node.Undefined,this.prev=Node.Undefined}}Node.Undefined=new Node(void 0);class LinkedList{constructor(){this._first=Node.Undefined,this._last=Node.Undefined,this._size=0}get size(){return this._size}isEmpty(){return this._first===Node.Undefined}clear(){let node=this._first;while(node!==Node.Undefined){const next=node.next;node.prev=Node.Undefined,node.next=Node.Undefined,node=next}this._first=Node.Undefined,this._last=Node.Undefined,this._size=0}unshift(element){return this._insert(element,!1)}push(element){return this._insert(element,!0)}_insert(element,atTheEnd){const newNode=new Node(element);if(this._first===Node.Undefined)this._first=newNode,this._last=newNode;else if(atTheEnd){
// push
const oldLast=this._last;this._last=newNode,newNode.prev=oldLast,oldLast.next=newNode}else{
// unshift
const oldFirst=this._first;this._first=newNode,newNode.next=oldFirst,oldFirst.prev=newNode}this._size+=1;let didRemove=!1;return()=>{didRemove||(didRemove=!0,this._remove(newNode))}}shift(){if(this._first!==Node.Undefined){const res=this._first.element;return this._remove(this._first),res}}pop(){if(this._last!==Node.Undefined){const res=this._last.element;return this._remove(this._last),res}}_remove(node){if(node.prev!==Node.Undefined&&node.next!==Node.Undefined){
// middle
const anchor=node.prev;anchor.next=node.next,node.next.prev=anchor}else node.prev===Node.Undefined&&node.next===Node.Undefined?(
// only node
this._first=Node.Undefined,this._last=Node.Undefined):node.next===Node.Undefined?(
// last
this._last=this._last.prev,this._last.next=Node.Undefined):node.prev===Node.Undefined&&(
// first
this._first=this._first.next,this._first.prev=Node.Undefined);
// done
this._size-=1}*[Symbol.iterator](){let node=this._first;while(node!==Node.Undefined)yield node.element,node=node.next}}
/***/},
/***/843702:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Id:function(){/* binding */return TernarySearchTree},
/* harmony export */Y9:function(){/* binding */return ResourceMap},
/* harmony export */z6:function(){/* binding */return LRUCache}
/* harmony export */});
/* unused harmony exports StringIterator, ConfigKeysIterator, PathIterator, UriIterator, LinkedMap */
/* harmony import */var _a,_b,_strings_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(697295);class StringIterator{constructor(){this._value="",this._pos=0}reset(key){return this._value=key,this._pos=0,this}next(){return this._pos+=1,this}hasNext(){return this._pos<this._value.length-1}cmp(a){const aCode=a.charCodeAt(0),thisCode=this._value.charCodeAt(this._pos);return aCode-thisCode}value(){return this._value[this._pos]}}class ConfigKeysIterator{constructor(_caseSensitive=!0){this._caseSensitive=_caseSensitive}reset(key){return this._value=key,this._from=0,this._to=0,this.next()}hasNext(){return this._to<this._value.length}next(){
// this._data = key.split(/[\\/]/).filter(s => !!s);
this._from=this._to;let justSeps=!0;for(;this._to<this._value.length;this._to++){const ch=this._value.charCodeAt(this._to);if(46/* Period */===ch){if(!justSeps)break;this._from++}else justSeps=!1}return this}cmp(a){return this._caseSensitive?(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareSubstring */.TT)(a,this._value,0,a.length,this._from,this._to):(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareSubstringIgnoreCase */.j_)(a,this._value,0,a.length,this._from,this._to)}value(){return this._value.substring(this._from,this._to)}}class PathIterator{constructor(_splitOnBackslash=!0,_caseSensitive=!0){this._splitOnBackslash=_splitOnBackslash,this._caseSensitive=_caseSensitive}reset(key){return this._value=key.replace(/\\$|\/$/,""),this._from=0,this._to=0,this.next()}hasNext(){return this._to<this._value.length}next(){
// this._data = key.split(/[\\/]/).filter(s => !!s);
this._from=this._to;let justSeps=!0;for(;this._to<this._value.length;this._to++){const ch=this._value.charCodeAt(this._to);if(47/* Slash */===ch||this._splitOnBackslash&&92/* Backslash */===ch){if(!justSeps)break;this._from++}else justSeps=!1}return this}cmp(a){return this._caseSensitive?(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareSubstring */.TT)(a,this._value,0,a.length,this._from,this._to):(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareSubstringIgnoreCase */.j_)(a,this._value,0,a.length,this._from,this._to)}value(){return this._value.substring(this._from,this._to)}}class UriIterator{constructor(_ignorePathCasing){this._ignorePathCasing=_ignorePathCasing,this._states=[],this._stateIdx=0}reset(key){return this._value=key,this._states=[],this._value.scheme&&this._states.push(1/* Scheme */),this._value.authority&&this._states.push(2/* Authority */),this._value.path&&(this._pathIterator=new PathIterator(!1,!this._ignorePathCasing(key)),this._pathIterator.reset(key.path),this._pathIterator.value()&&this._states.push(3/* Path */)),this._value.query&&this._states.push(4/* Query */),this._value.fragment&&this._states.push(5/* Fragment */),this._stateIdx=0,this}next(){return 3/* Path */===this._states[this._stateIdx]&&this._pathIterator.hasNext()?this._pathIterator.next():this._stateIdx+=1,this}hasNext(){return 3/* Path */===this._states[this._stateIdx]&&this._pathIterator.hasNext()||this._stateIdx<this._states.length-1}cmp(a){if(1/* Scheme */===this._states[this._stateIdx])return(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareIgnoreCase */.zY)(a,this._value.scheme);if(2/* Authority */===this._states[this._stateIdx])return(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compareIgnoreCase */.zY)(a,this._value.authority);if(3/* Path */===this._states[this._stateIdx])return this._pathIterator.cmp(a);if(4/* Query */===this._states[this._stateIdx])return(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compare */.qu)(a,this._value.query);if(5/* Fragment */===this._states[this._stateIdx])return(0,_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .compare */.qu)(a,this._value.fragment);throw new Error}value(){if(1/* Scheme */===this._states[this._stateIdx])return this._value.scheme;if(2/* Authority */===this._states[this._stateIdx])return this._value.authority;if(3/* Path */===this._states[this._stateIdx])return this._pathIterator.value();if(4/* Query */===this._states[this._stateIdx])return this._value.query;if(5/* Fragment */===this._states[this._stateIdx])return this._value.fragment;throw new Error}}class TernarySearchTreeNode{constructor(){this.height=1}rotateLeft(){const tmp=this.right;return this.right=tmp.left,tmp.left=this,this.updateHeight(),tmp.updateHeight(),tmp}rotateRight(){const tmp=this.left;return this.left=tmp.right,tmp.right=this,this.updateHeight(),tmp.updateHeight(),tmp}updateHeight(){this.height=1+Math.max(this.heightLeft,this.heightRight)}balanceFactor(){return this.heightRight-this.heightLeft}get heightLeft(){var _c,_d;return null!==(_d=null===(_c=this.left)||void 0===_c?void 0:_c.height)&&void 0!==_d?_d:0}get heightRight(){var _c,_d;return null!==(_d=null===(_c=this.right)||void 0===_c?void 0:_c.height)&&void 0!==_d?_d:0}}class TernarySearchTree{constructor(segments){this._iter=segments}static forUris(ignorePathCasing=(()=>!1)){return new TernarySearchTree(new UriIterator(ignorePathCasing))}static forStrings(){return new TernarySearchTree(new StringIterator)}static forConfigKeys(){return new TernarySearchTree(new ConfigKeysIterator)}clear(){this._root=void 0}set(key,element){const iter=this._iter.reset(key);let node;this._root||(this._root=new TernarySearchTreeNode,this._root.segment=iter.value());const stack=[];
// find insert_node
node=this._root;while(1){const val=iter.cmp(node.segment);if(val>0)
// left
node.left||(node.left=new TernarySearchTreeNode,node.left.segment=iter.value()),stack.push([-1/* Left */,node]),node=node.left;else if(val<0)
// right
node.right||(node.right=new TernarySearchTreeNode,node.right.segment=iter.value()),stack.push([1/* Right */,node]),node=node.right;else{if(!iter.hasNext())break;
// mid
iter.next(),node.mid||(node.mid=new TernarySearchTreeNode,node.mid.segment=iter.value()),stack.push([0/* Mid */,node]),node=node.mid}}
// set value
const oldElement=node.value;node.value=element,node.key=key;
// balance
for(let i=stack.length-1;i>=0;i--){const node=stack[i][1];node.updateHeight();const bf=node.balanceFactor();if(bf<-1||bf>1){
// needs rotate
const d1=stack[i][0],d2=stack[i+1][0];if(1/* Right */===d1&&1/* Right */===d2)
//right, right -> rotate left
stack[i][1]=node.rotateLeft();else if(-1/* Left */===d1&&-1/* Left */===d2)
// left, left -> rotate right
stack[i][1]=node.rotateRight();else if(1/* Right */===d1&&-1/* Left */===d2)
// right, left -> double rotate right, left
node.right=stack[i+1][1]=stack[i+1][1].rotateRight(),stack[i][1]=node.rotateLeft();else{if(-1/* Left */!==d1||1/* Right */!==d2)throw new Error;
// patch path to parent
// left, right -> double rotate left, right
node.left=stack[i+1][1]=stack[i+1][1].rotateLeft(),stack[i][1]=node.rotateRight()}if(i>0)switch(stack[i-1][0]){case-1/* Left */:stack[i-1][1].left=stack[i][1];break;case 1/* Right */:stack[i-1][1].right=stack[i][1];break;case 0/* Mid */:stack[i-1][1].mid=stack[i][1];break}else this._root=stack[0][1]}}return oldElement}get(key){var _c;return null===(_c=this._getNode(key))||void 0===_c?void 0:_c.value}_getNode(key){const iter=this._iter.reset(key);let node=this._root;while(node){const val=iter.cmp(node.segment);if(val>0)
// left
node=node.left;else if(val<0)
// right
node=node.right;else{if(!iter.hasNext())break;
// mid
iter.next(),node=node.mid}}return node}has(key){const node=this._getNode(key);return!(void 0===(null===node||void 0===node?void 0:node.value)&&void 0===(null===node||void 0===node?void 0:node.mid))}delete(key){return this._delete(key,!1)}deleteSuperstr(key){return this._delete(key,!0)}_delete(key,superStr){var _c;const iter=this._iter.reset(key),stack=[];let node=this._root;
// find node
while(node){const val=iter.cmp(node.segment);if(val>0)
// left
stack.push([-1/* Left */,node]),node=node.left;else if(val<0)
// right
stack.push([1/* Right */,node]),node=node.right;else{if(!iter.hasNext())break;
// mid
iter.next(),stack.push([0/* Mid */,node]),node=node.mid}}if(node){
// BST node removal
if(superStr?(
// removing children, reset height
node.left=void 0,node.mid=void 0,node.right=void 0,node.height=1):(
// removing element
node.key=void 0,node.value=void 0),!node.mid&&!node.value)if(node.left&&node.right){
// full node
const min=this._min(node.right),{key:key,value:value,segment:segment}=min;this._delete(min.key,!1),node.key=key,node.value=value,node.segment=segment}else{
// empty or half empty
const newChild=null!==(_c=node.left)&&void 0!==_c?_c:node.right;if(stack.length>0){const[dir,parent]=stack[stack.length-1];switch(dir){case-1/* Left */:parent.left=newChild;break;case 0/* Mid */:parent.mid=newChild;break;case 1/* Right */:parent.right=newChild;break}}else this._root=newChild}
// AVL balance
for(let i=stack.length-1;i>=0;i--){const node=stack[i][1];node.updateHeight();const bf=node.balanceFactor();
// patch path to parent
if(bf>1?(
// right heavy
node.right.balanceFactor()>=0||(
// right, left -> double rotate
node.right=stack[i+1][1]=stack[i+1][1].rotateRight()),
// right, right -> rotate left
stack[i][1]=node.rotateLeft()):bf<-1&&(
// left heavy
node.left.balanceFactor()<=0||(
// left, right -> double rotate
node.left=stack[i+1][1]=stack[i+1][1].rotateLeft()),
// left, left -> rotate right
stack[i][1]=node.rotateRight()),i>0)switch(stack[i-1][0]){case-1/* Left */:stack[i-1][1].left=stack[i][1];break;case 1/* Right */:stack[i-1][1].right=stack[i][1];break;case 0/* Mid */:stack[i-1][1].mid=stack[i][1];break}else this._root=stack[0][1]}}}_min(node){while(node.left)node=node.left;return node}findSubstr(key){const iter=this._iter.reset(key);let candidate,node=this._root;while(node){const val=iter.cmp(node.segment);if(val>0)
// left
node=node.left;else if(val<0)
// right
node=node.right;else{if(!iter.hasNext())break;
// mid
iter.next(),candidate=node.value||candidate,node=node.mid}}return node&&node.value||candidate}findSuperstr(key){const iter=this._iter.reset(key);let node=this._root;while(node){const val=iter.cmp(node.segment);if(val>0)
// left
node=node.left;else if(val<0)
// right
node=node.right;else{if(!iter.hasNext())
// collect
return node.mid?this._entries(node.mid):void 0;
// mid
iter.next(),node=node.mid}}}forEach(callback){for(const[key,value]of this)callback(value,key)}*[Symbol.iterator](){yield*this._entries(this._root)}*_entries(node){
// DFS
node&&(node.left&&(yield*this._entries(node.left)),node.value&&(yield[node.key,node.value]),node.mid&&(yield*this._entries(node.mid)),node.right&&(yield*this._entries(node.right)))}}class ResourceMapEntry{constructor(uri,value){this.uri=uri,this.value=value}}class ResourceMap{constructor(mapOrKeyFn,toKey){this[_a]="ResourceMap",mapOrKeyFn instanceof ResourceMap?(this.map=new Map(mapOrKeyFn.map),this.toKey=null!==toKey&&void 0!==toKey?toKey:ResourceMap.defaultToKey):(this.map=new Map,this.toKey=null!==mapOrKeyFn&&void 0!==mapOrKeyFn?mapOrKeyFn:ResourceMap.defaultToKey)}set(resource,value){return this.map.set(this.toKey(resource),new ResourceMapEntry(resource,value)),this}get(resource){var _c;return null===(_c=this.map.get(this.toKey(resource)))||void 0===_c?void 0:_c.value}has(resource){return this.map.has(this.toKey(resource))}get size(){return this.map.size}clear(){this.map.clear()}delete(resource){return this.map.delete(this.toKey(resource))}forEach(clb,thisArg){"undefined"!==typeof thisArg&&(clb=clb.bind(thisArg));for(let[_,entry]of this.map)clb(entry.value,entry.uri,this)}*values(){for(let entry of this.map.values())yield entry.value}*keys(){for(let entry of this.map.values())yield entry.uri}*entries(){for(let entry of this.map.values())yield[entry.uri,entry.value]}*[(_a=Symbol.toStringTag,Symbol.iterator)](){for(let[,entry]of this.map)yield[entry.uri,entry.value]}}ResourceMap.defaultToKey=resource=>resource.toString();class LinkedMap{constructor(){this[_b]="LinkedMap",this._map=new Map,this._head=void 0,this._tail=void 0,this._size=0,this._state=0}clear(){this._map.clear(),this._head=void 0,this._tail=void 0,this._size=0,this._state++}isEmpty(){return!this._head&&!this._tail}get size(){return this._size}get first(){var _c;return null===(_c=this._head)||void 0===_c?void 0:_c.value}get last(){var _c;return null===(_c=this._tail)||void 0===_c?void 0:_c.value}has(key){return this._map.has(key)}get(key,touch=0/* None */){const item=this._map.get(key);if(item)return 0/* None */!==touch&&this.touch(item,touch),item.value}set(key,value,touch=0/* None */){let item=this._map.get(key);if(item)item.value=value,0/* None */!==touch&&this.touch(item,touch);else{switch(item={key:key,value:value,next:void 0,previous:void 0},touch){case 0/* None */:this.addItemLast(item);break;case 1/* AsOld */:this.addItemFirst(item);break;case 2/* AsNew */:this.addItemLast(item);break;default:this.addItemLast(item);break}this._map.set(key,item),this._size++}return this}delete(key){return!!this.remove(key)}remove(key){const item=this._map.get(key);if(item)return this._map.delete(key),this.removeItem(item),this._size--,item.value}shift(){if(!this._head&&!this._tail)return;if(!this._head||!this._tail)throw new Error("Invalid list");const item=this._head;return this._map.delete(item.key),this.removeItem(item),this._size--,item.value}forEach(callbackfn,thisArg){const state=this._state;let current=this._head;while(current){if(thisArg?callbackfn.bind(thisArg)(current.value,current.key,this):callbackfn(current.value,current.key,this),this._state!==state)throw new Error("LinkedMap got modified during iteration.");current=current.next}}keys(){const map=this,state=this._state;let current=this._head;const iterator={[Symbol.iterator](){return iterator},next(){if(map._state!==state)throw new Error("LinkedMap got modified during iteration.");if(current){const result={value:current.key,done:!1};return current=current.next,result}return{value:void 0,done:!0}}};return iterator}values(){const map=this,state=this._state;let current=this._head;const iterator={[Symbol.iterator](){return iterator},next(){if(map._state!==state)throw new Error("LinkedMap got modified during iteration.");if(current){const result={value:current.value,done:!1};return current=current.next,result}return{value:void 0,done:!0}}};return iterator}entries(){const map=this,state=this._state;let current=this._head;const iterator={[Symbol.iterator](){return iterator},next(){if(map._state!==state)throw new Error("LinkedMap got modified during iteration.");if(current){const result={value:[current.key,current.value],done:!1};return current=current.next,result}return{value:void 0,done:!0}}};return iterator}[(_b=Symbol.toStringTag,Symbol.iterator)](){return this.entries()}trimOld(newSize){if(newSize>=this.size)return;if(0===newSize)return void this.clear();let current=this._head,currentSize=this.size;while(current&&currentSize>newSize)this._map.delete(current.key),current=current.next,currentSize--;this._head=current,this._size=currentSize,current&&(current.previous=void 0),this._state++}addItemFirst(item){
// First time Insert
if(this._head||this._tail){if(!this._head)throw new Error("Invalid list");item.next=this._head,this._head.previous=item}else this._tail=item;this._head=item,this._state++}addItemLast(item){
// First time Insert
if(this._head||this._tail){if(!this._tail)throw new Error("Invalid list");item.previous=this._tail,this._tail.next=item}else this._head=item;this._tail=item,this._state++}removeItem(item){if(item===this._head&&item===this._tail)this._head=void 0,this._tail=void 0;else if(item===this._head){
// This can only happen if size === 1 which is handled
// by the case above.
if(!item.next)throw new Error("Invalid list");item.next.previous=void 0,this._head=item.next}else if(item===this._tail){
// This can only happen if size === 1 which is handled
// by the case above.
if(!item.previous)throw new Error("Invalid list");item.previous.next=void 0,this._tail=item.previous}else{const next=item.next,previous=item.previous;if(!next||!previous)throw new Error("Invalid list");next.previous=previous,previous.next=next}item.next=void 0,item.previous=void 0,this._state++}touch(item,touch){if(!this._head||!this._tail)throw new Error("Invalid list");if(1/* AsOld */===touch||2/* AsNew */===touch)if(1/* AsOld */===touch){if(item===this._head)return;const next=item.next,previous=item.previous;
// Unlink the item
item===this._tail?(
// previous must be defined since item was not head but is tail
// So there are more than on item in the map
previous.next=void 0,this._tail=previous):(
// Both next and previous are not undefined since item was neither head nor tail.
next.previous=previous,previous.next=next),
// Insert the node at head
item.previous=void 0,item.next=this._head,this._head.previous=item,this._head=item,this._state++}else if(2/* AsNew */===touch){if(item===this._tail)return;const next=item.next,previous=item.previous;
// Unlink the item.
item===this._head?(
// next must be defined since item was not tail but is head
// So there are more than on item in the map
next.previous=void 0,this._head=next):(
// Both next and previous are not undefined since item was neither head nor tail.
next.previous=previous,previous.next=next),item.next=void 0,item.previous=this._tail,this._tail.next=item,this._tail=item,this._state++}}toJSON(){const data=[];return this.forEach(((value,key)=>{data.push([key,value])})),data}fromJSON(data){this.clear();for(const[key,value]of data)this.set(key,value)}}class LRUCache extends LinkedMap{constructor(limit,ratio=1){super(),this._limit=limit,this._ratio=Math.min(Math.max(0,ratio),1)}get limit(){return this._limit}set limit(limit){this._limit=limit,this.checkTrim()}get(key,touch=2/* AsNew */){return super.get(key,touch)}peek(key){return super.get(key,0/* None */)}set(key,value){return super.set(key,value,2/* AsNew */),this.checkTrim(),this}checkTrim(){this.size>this._limit&&this.trimOld(Math.round(this._limit*this._ratio))}}
/***/},
/***/933720:
/***/function(module,__webpack_exports__,__webpack_require__){
/**
 * marked - a markdown parser
 * Copyright (c) 2011-2021, Christopher Jeffrey. (MIT Licensed)
 * https://github.com/markedjs/marked
 */
/**
 * DO NOT EDIT THIS FILE
 * The code in this file is generated from files in ./src/
 */
// ESM-uncomment-begin
let __marked_exports;
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Qc:function(){/* binding */return parse},
/* harmony export */Th:function(){/* binding */return Renderer}
/* harmony export */}),
/* unused harmony exports marked, Parser, parser, TextRenderer, Lexer, lexer, Tokenizer, Slugger */
/* module decorator */module=__webpack_require__.hmd(module),function(){function define(factory){__marked_exports=factory()}define.amd=!0,
// ESM-uncomment-end
function(global,factory){"object"===typeof exports?module.exports=factory():"function"===typeof define&&define.amd?define(factory):(global="undefined"!==typeof globalThis?globalThis:global||self,global.marked=factory())}(this,(function(){function _defineProperties(target,props){for(var i=0;i<props.length;i++){var descriptor=props[i];descriptor.enumerable=descriptor.enumerable||!1,descriptor.configurable=!0,"value"in descriptor&&(descriptor.writable=!0),Object.defineProperty(target,descriptor.key,descriptor)}}function _createClass(Constructor,protoProps,staticProps){return protoProps&&_defineProperties(Constructor.prototype,protoProps),staticProps&&_defineProperties(Constructor,staticProps),Constructor}function _unsupportedIterableToArray(o,minLen){if(o){if("string"===typeof o)return _arrayLikeToArray(o,minLen);var n=Object.prototype.toString.call(o).slice(8,-1);return"Object"===n&&o.constructor&&(n=o.constructor.name),"Map"===n||"Set"===n?Array.from(o):"Arguments"===n||/^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(n)?_arrayLikeToArray(o,minLen):void 0}}function _arrayLikeToArray(arr,len){(null==len||len>arr.length)&&(len=arr.length);for(var i=0,arr2=new Array(len);i<len;i++)arr2[i]=arr[i];return arr2}function _createForOfIteratorHelperLoose(o,allowArrayLike){var it="undefined"!==typeof Symbol&&o[Symbol.iterator]||o["@@iterator"];if(it)return(it=it.call(o)).next.bind(it);if(Array.isArray(o)||(it=_unsupportedIterableToArray(o))||allowArrayLike&&o&&"number"===typeof o.length){it&&(o=it);var i=0;return function(){return i>=o.length?{done:!0}:{done:!1,value:o[i++]}}}throw new TypeError("Invalid attempt to iterate non-iterable instance.\nIn order to be iterable, non-array objects must have a [Symbol.iterator]() method.")}var defaults$5={exports:{}};function getDefaults$1(){return{baseUrl:null,breaks:!1,extensions:null,gfm:!0,headerIds:!0,headerPrefix:"",highlight:null,langPrefix:"language-",mangle:!0,pedantic:!1,renderer:null,sanitize:!1,sanitizer:null,silent:!1,smartLists:!1,smartypants:!1,tokenizer:null,walkTokens:null,xhtml:!1}}function changeDefaults$1(newDefaults){defaults$5.exports.defaults=newDefaults}defaults$5.exports={defaults:getDefaults$1(),getDefaults:getDefaults$1,changeDefaults:changeDefaults$1};
/**
   * Helpers
   */
var escapeTest=/[&<>"']/,escapeReplace=/[&<>"']/g,escapeTestNoEncode=/[<>"']|&(?!#?\w+;)/,escapeReplaceNoEncode=/[<>"']|&(?!#?\w+;)/g,escapeReplacements={"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"},getEscapeReplacement=function(ch){return escapeReplacements[ch]};function escape$2(html,encode){if(encode){if(escapeTest.test(html))return html.replace(escapeReplace,getEscapeReplacement)}else if(escapeTestNoEncode.test(html))return html.replace(escapeReplaceNoEncode,getEscapeReplacement);return html}var unescapeTest=/&(#(?:\d+)|(?:#x[0-9A-Fa-f]+)|(?:\w+));?/gi;function unescape$1(html){
// explicitly match decimal, hex, and named HTML entities
return html.replace(unescapeTest,(function(_,n){return n=n.toLowerCase(),"colon"===n?":":"#"===n.charAt(0)?"x"===n.charAt(1)?String.fromCharCode(parseInt(n.substring(2),16)):String.fromCharCode(+n.substring(1)):""}))}var caret=/(^|[^\[])\^/g;function edit$1(regex,opt){regex=regex.source||regex,opt=opt||"";var obj={replace:function(name,val){return val=val.source||val,val=val.replace(caret,"$1"),regex=regex.replace(name,val),obj},getRegex:function(){return new RegExp(regex,opt)}};return obj}var nonWordAndColonTest=/[^\w:]/g,originIndependentUrl=/^$|^[a-z][a-z0-9+.-]*:|^[?#]/i;function cleanUrl$1(sanitize,base,href){if(sanitize){var prot;try{prot=decodeURIComponent(unescape$1(href)).replace(nonWordAndColonTest,"").toLowerCase()}catch(e){return null}if(0===prot.indexOf("javascript:")||0===prot.indexOf("vbscript:")||0===prot.indexOf("data:"))return null}base&&!originIndependentUrl.test(href)&&(href=resolveUrl(base,href));try{href=encodeURI(href).replace(/%25/g,"%")}catch(e){return null}return href}var baseUrls={},justDomain=/^[^:]+:\/*[^/]*$/,protocol=/^([^:]+:)[\s\S]*$/,domain=/^([^:]+:\/*[^/]*)[\s\S]*$/;function resolveUrl(base,href){baseUrls[" "+base]||(
// we can ignore everything in base after the last slash of its path component,
// but we might need to add _that_
// https://tools.ietf.org/html/rfc3986#section-3
justDomain.test(base)?baseUrls[" "+base]=base+"/":baseUrls[" "+base]=rtrim$1(base,"/",!0)),base=baseUrls[" "+base];var relativeBase=-1===base.indexOf(":");return"//"===href.substring(0,2)?relativeBase?href:base.replace(protocol,"$1")+href:"/"===href.charAt(0)?relativeBase?href:base.replace(domain,"$1")+href:base+href}var noopTest$1={exec:function(){}};function merge$2(obj){for(var target,key,i=1;i<arguments.length;i++)for(key in target=arguments[i],target)Object.prototype.hasOwnProperty.call(target,key)&&(obj[key]=target[key]);return obj}function splitCells$1(tableRow,count){
// ensure that every cell-delimiting pipe has a space
// before it to distinguish it from an escaped pipe
var row=tableRow.replace(/\|/g,(function(match,offset,str){var escaped=!1,curr=offset;while(--curr>=0&&"\\"===str[curr])escaped=!escaped;return escaped?"|":" |"})),cells=row.split(/ \|/),i=0;if(// First/last cell in a row cannot be empty if it has no leading/trailing pipe
cells[0].trim()||cells.shift(),cells[cells.length-1].trim()||cells.pop(),cells.length>count)cells.splice(count);else while(cells.length<count)cells.push("");for(;i<cells.length;i++)
// leading or trailing whitespace is ignored per the gfm spec
cells[i]=cells[i].trim().replace(/\\\|/g,"|");return cells}// Remove trailing 'c's. Equivalent to str.replace(/c*$/, '').
// /c*$/ is vulnerable to REDOS.
// invert: Remove suffix of non-c chars instead. Default falsey.
function rtrim$1(str,c,invert){var l=str.length;if(0===l)return"";// Length of suffix matching the invert condition.
var suffLen=0;// Step left until we fail to match the invert condition.
while(suffLen<l){var currChar=str.charAt(l-suffLen-1);if(currChar!==c||invert){if(currChar===c||!invert)break;suffLen++}else suffLen++}return str.substr(0,l-suffLen)}function findClosingBracket$1(str,b){if(-1===str.indexOf(b[1]))return-1;for(var l=str.length,level=0,i=0;i<l;i++)if("\\"===str[i])i++;else if(str[i]===b[0])level++;else if(str[i]===b[1]&&(level--,level<0))return i;return-1}function checkSanitizeDeprecation$1(opt){opt&&opt.sanitize&&opt.silent}// copied from https://stackoverflow.com/a/5450113/806777
function repeatString$1(pattern,count){if(count<1)return"";var result="";while(count>1)1&count&&(result+=pattern),count>>=1,pattern+=pattern;return result+pattern}var helpers={escape:escape$2,unescape:unescape$1,edit:edit$1,cleanUrl:cleanUrl$1,resolveUrl:resolveUrl,noopTest:noopTest$1,merge:merge$2,splitCells:splitCells$1,rtrim:rtrim$1,findClosingBracket:findClosingBracket$1,checkSanitizeDeprecation:checkSanitizeDeprecation$1,repeatString:repeatString$1},defaults$4=defaults$5.exports.defaults,rtrim=helpers.rtrim,splitCells=helpers.splitCells,_escape=helpers.escape,findClosingBracket=helpers.findClosingBracket;function outputLink(cap,link,raw,lexer){var href=link.href,title=link.title?_escape(link.title):null,text=cap[1].replace(/\\([\[\]])/g,"$1");if("!"!==cap[0].charAt(0)){lexer.state.inLink=!0;var token={type:"link",raw:raw,href:href,title:title,text:text,tokens:lexer.inlineTokens(text,[])};return lexer.state.inLink=!1,token}return{type:"image",raw:raw,href:href,title:title,text:_escape(text)}}function indentCodeCompensation(raw,text){var matchIndentToCode=raw.match(/^(\s+)(?:```)/);if(null===matchIndentToCode)return text;var indentToCode=matchIndentToCode[1];return text.split("\n").map((function(node){var matchIndentInNode=node.match(/^\s+/);if(null===matchIndentInNode)return node;var indentInNode=matchIndentInNode[0];return indentInNode.length>=indentToCode.length?node.slice(indentToCode.length):node})).join("\n")}
/**
   * Tokenizer
   */var Tokenizer_1=function(){function Tokenizer(options){this.options=options||defaults$4}var _proto=Tokenizer.prototype;return _proto.space=function(src){var cap=this.rules.block.newline.exec(src);if(cap)return cap[0].length>1?{type:"space",raw:cap[0]}:{raw:"\n"}},_proto.code=function(src){var cap=this.rules.block.code.exec(src);if(cap){var text=cap[0].replace(/^ {1,4}/gm,"");return{type:"code",raw:cap[0],codeBlockStyle:"indented",text:this.options.pedantic?text:rtrim(text,"\n")}}},_proto.fences=function(src){var cap=this.rules.block.fences.exec(src);if(cap){var raw=cap[0],text=indentCodeCompensation(raw,cap[3]||"");return{type:"code",raw:raw,lang:cap[2]?cap[2].trim():cap[2],text:text}}},_proto.heading=function(src){var cap=this.rules.block.heading.exec(src);if(cap){var text=cap[2].trim();// remove trailing #s
if(/#$/.test(text)){var trimmed=rtrim(text,"#");this.options.pedantic?text=trimmed.trim():trimmed&&!/ $/.test(trimmed)||(
// CommonMark requires space before trailing #s
text=trimmed.trim())}var token={type:"heading",raw:cap[0],depth:cap[1].length,text:text,tokens:[]};return this.lexer.inline(token.text,token.tokens),token}},_proto.hr=function(src){var cap=this.rules.block.hr.exec(src);if(cap)return{type:"hr",raw:cap[0]}},_proto.blockquote=function(src){var cap=this.rules.block.blockquote.exec(src);if(cap){var text=cap[0].replace(/^ *> ?/gm,"");return{type:"blockquote",raw:cap[0],tokens:this.lexer.blockTokens(text,[]),text:text}}},_proto.list=function(src){var cap=this.rules.block.list.exec(src);if(cap){var raw,istask,ischecked,indent,i,blankLine,endsWithBlankLine,line,lines,itemContents,bull=cap[1].trim(),isordered=bull.length>1,list={type:"list",raw:"",ordered:isordered,start:isordered?+bull.slice(0,-1):"",loose:!1,items:[]};bull=isordered?"\\d{1,9}\\"+bull.slice(-1):"\\"+bull,this.options.pedantic&&(bull=isordered?bull:"[*+-]");// Get next list item
var itemRegex=new RegExp("^( {0,3}"+bull+")((?: [^\\n]*| *)(?:\\n[^\\n]*)*(?:\\n|$))");// Get each top-level item
while(src){if(this.rules.block.hr.test(src))
// End list if we encounter an HR (possibly move into itemRegex?)
break;if(!(cap=itemRegex.exec(src)))break;lines=cap[2].split("\n"),this.options.pedantic?(indent=2,itemContents=lines[0].trimLeft()):(indent=cap[2].search(/[^ ]/),// Find first non-space char
indent=cap[1].length+(indent>4?1:indent),// intented code blocks after 4 spaces; indent is always 1
itemContents=lines[0].slice(indent-cap[1].length)),blankLine=!1,raw=cap[0],!lines[0]&&/^ *$/.test(lines[1])&&(
// items begin with at most one blank line
raw=cap[1]+lines.slice(0,2).join("\n")+"\n",list.loose=!0,lines=[]);var nextBulletRegex=new RegExp("^ {0,"+Math.min(3,indent-1)+"}(?:[*+-]|\\d{1,9}[.)])");for(i=1;i<lines.length;i++){// End list item if found start of new bullet
if(line=lines[i],this.options.pedantic&&(
// Re-align to follow commonmark nesting rules
line=line.replace(/^ {1,4}(?=( {4})*[^ ])/g,"  ")),nextBulletRegex.test(line)){raw=cap[1]+lines.slice(0,i).join("\n")+"\n";break}// Until we encounter a blank line, item contents do not need indentation
if(blankLine){// Dedent this line
if(!(line.search(/[^ ]/)>=indent)&&line.trim()){
// Line was not properly indented; end of this item
raw=cap[1]+lines.slice(0,i).join("\n")+"\n";break}itemContents+="\n"+line.slice(indent)}else line.trim()||(
// Check if current line is empty
blankLine=!0),// Dedent if possible
line.search(/[^ ]/)>=indent?itemContents+="\n"+line.slice(indent):itemContents+="\n"+line}list.loose||(
// If the previous item ended with a blank line, the list is loose
endsWithBlankLine?list.loose=!0:/\n *\n *$/.test(raw)&&(endsWithBlankLine=!0)),// Check for task list items
this.options.gfm&&(istask=/^\[[ xX]\] /.exec(itemContents),istask&&(ischecked="[ ] "!==istask[0],itemContents=itemContents.replace(/^\[[ xX]\] +/,""))),list.items.push({type:"list_item",raw:raw,task:!!istask,checked:ischecked,loose:!1,text:itemContents}),list.raw+=raw,src=src.slice(raw.length)}// Do not consume newlines at end of final item. Alternatively, make itemRegex *start* with any newlines to simplify/speed up endsWithBlankLine logic
list.items[list.items.length-1].raw=raw.trimRight(),list.items[list.items.length-1].text=itemContents.trimRight(),list.raw=list.raw.trimRight();var l=list.items.length;// Item child tokens handled here at end because we needed to have the final item to trim it first
for(i=0;i<l;i++)this.lexer.state.top=!1,list.items[i].tokens=this.lexer.blockTokens(list.items[i].text,[]),list.items[i].tokens.some((function(t){return"space"===t.type}))&&(list.loose=!0,list.items[i].loose=!0);return list}},_proto.html=function(src){var cap=this.rules.block.html.exec(src);if(cap){var token={type:"html",raw:cap[0],pre:!this.options.sanitizer&&("pre"===cap[1]||"script"===cap[1]||"style"===cap[1]),text:cap[0]};return this.options.sanitize&&(token.type="paragraph",token.text=this.options.sanitizer?this.options.sanitizer(cap[0]):_escape(cap[0]),token.tokens=[],this.lexer.inline(token.text,token.tokens)),token}},_proto.def=function(src){var cap=this.rules.block.def.exec(src);if(cap){cap[3]&&(cap[3]=cap[3].substring(1,cap[3].length-1));var tag=cap[1].toLowerCase().replace(/\s+/g," ");return{type:"def",tag:tag,raw:cap[0],href:cap[2],title:cap[3]}}},_proto.table=function(src){var cap=this.rules.block.table.exec(src);if(cap){var item={type:"table",header:splitCells(cap[1]).map((function(c){return{text:c}})),align:cap[2].replace(/^ *|\| *$/g,"").split(/ *\| */),rows:cap[3]?cap[3].replace(/\n$/,"").split("\n"):[]};if(item.header.length===item.align.length){item.raw=cap[0];var i,j,k,row,l=item.align.length;for(i=0;i<l;i++)/^ *-+: *$/.test(item.align[i])?item.align[i]="right":/^ *:-+: *$/.test(item.align[i])?item.align[i]="center":/^ *:-+ *$/.test(item.align[i])?item.align[i]="left":item.align[i]=null;for(l=item.rows.length,i=0;i<l;i++)item.rows[i]=splitCells(item.rows[i],item.header.length).map((function(c){return{text:c}}));// parse child tokens inside headers and cells
// header child tokens
for(l=item.header.length,j=0;j<l;j++)item.header[j].tokens=[],this.lexer.inlineTokens(item.header[j].text,item.header[j].tokens);// cell child tokens
for(l=item.rows.length,j=0;j<l;j++)for(row=item.rows[j],k=0;k<row.length;k++)row[k].tokens=[],this.lexer.inlineTokens(row[k].text,row[k].tokens);return item}}},_proto.lheading=function(src){var cap=this.rules.block.lheading.exec(src);if(cap){var token={type:"heading",raw:cap[0],depth:"="===cap[2].charAt(0)?1:2,text:cap[1],tokens:[]};return this.lexer.inline(token.text,token.tokens),token}},_proto.paragraph=function(src){var cap=this.rules.block.paragraph.exec(src);if(cap){var token={type:"paragraph",raw:cap[0],text:"\n"===cap[1].charAt(cap[1].length-1)?cap[1].slice(0,-1):cap[1],tokens:[]};return this.lexer.inline(token.text,token.tokens),token}},_proto.text=function(src){var cap=this.rules.block.text.exec(src);if(cap){var token={type:"text",raw:cap[0],text:cap[0],tokens:[]};return this.lexer.inline(token.text,token.tokens),token}},_proto.escape=function(src){var cap=this.rules.inline.escape.exec(src);if(cap)return{type:"escape",raw:cap[0],text:_escape(cap[1])}},_proto.tag=function(src){var cap=this.rules.inline.tag.exec(src);if(cap)return!this.lexer.state.inLink&&/^<a /i.test(cap[0])?this.lexer.state.inLink=!0:this.lexer.state.inLink&&/^<\/a>/i.test(cap[0])&&(this.lexer.state.inLink=!1),!this.lexer.state.inRawBlock&&/^<(pre|code|kbd|script)(\s|>)/i.test(cap[0])?this.lexer.state.inRawBlock=!0:this.lexer.state.inRawBlock&&/^<\/(pre|code|kbd|script)(\s|>)/i.test(cap[0])&&(this.lexer.state.inRawBlock=!1),{type:this.options.sanitize?"text":"html",raw:cap[0],inLink:this.lexer.state.inLink,inRawBlock:this.lexer.state.inRawBlock,text:this.options.sanitize?this.options.sanitizer?this.options.sanitizer(cap[0]):_escape(cap[0]):cap[0]}},_proto.link=function(src){var cap=this.rules.inline.link.exec(src);if(cap){var trimmedUrl=cap[2].trim();if(!this.options.pedantic&&/^</.test(trimmedUrl)){
// commonmark requires matching angle brackets
if(!/>$/.test(trimmedUrl))return;// ending angle bracket cannot be escaped
var rtrimSlash=rtrim(trimmedUrl.slice(0,-1),"\\");if((trimmedUrl.length-rtrimSlash.length)%2===0)return}else{
// find closing parenthesis
var lastParenIndex=findClosingBracket(cap[2],"()");if(lastParenIndex>-1){var start=0===cap[0].indexOf("!")?5:4,linkLen=start+cap[1].length+lastParenIndex;cap[2]=cap[2].substring(0,lastParenIndex),cap[0]=cap[0].substring(0,linkLen).trim(),cap[3]=""}}var href=cap[2],title="";if(this.options.pedantic){
// split pedantic href and title
var link=/^([^'"]*[^\s])\s+(['"])(.*)\2/.exec(href);link&&(href=link[1],title=link[3])}else title=cap[3]?cap[3].slice(1,-1):"";return href=href.trim(),/^</.test(href)&&(
// pedantic allows starting angle bracket without ending angle bracket
href=this.options.pedantic&&!/>$/.test(trimmedUrl)?href.slice(1):href.slice(1,-1)),outputLink(cap,{href:href?href.replace(this.rules.inline._escapes,"$1"):href,title:title?title.replace(this.rules.inline._escapes,"$1"):title},cap[0],this.lexer)}},_proto.reflink=function(src,links){var cap;if((cap=this.rules.inline.reflink.exec(src))||(cap=this.rules.inline.nolink.exec(src))){var link=(cap[2]||cap[1]).replace(/\s+/g," ");if(link=links[link.toLowerCase()],!link||!link.href){var text=cap[0].charAt(0);return{type:"text",raw:text,text:text}}return outputLink(cap,link,cap[0],this.lexer)}},_proto.emStrong=function(src,maskedSrc,prevChar){void 0===prevChar&&(prevChar="");var match=this.rules.inline.emStrong.lDelim.exec(src);if(match&&(!match[3]||!prevChar.match(/(?:[0-9A-Za-z\xAA\xB2\xB3\xB5\xB9\xBA\xBC-\xBE\xC0-\xD6\xD8-\xF6\xF8-\u02C1\u02C6-\u02D1\u02E0-\u02E4\u02EC\u02EE\u0370-\u0374\u0376\u0377\u037A-\u037D\u037F\u0386\u0388-\u038A\u038C\u038E-\u03A1\u03A3-\u03F5\u03F7-\u0481\u048A-\u052F\u0531-\u0556\u0559\u0560-\u0588\u05D0-\u05EA\u05EF-\u05F2\u0620-\u064A\u0660-\u0669\u066E\u066F\u0671-\u06D3\u06D5\u06E5\u06E6\u06EE-\u06FC\u06FF\u0710\u0712-\u072F\u074D-\u07A5\u07B1\u07C0-\u07EA\u07F4\u07F5\u07FA\u0800-\u0815\u081A\u0824\u0828\u0840-\u0858\u0860-\u086A\u08A0-\u08B4\u08B6-\u08C7\u0904-\u0939\u093D\u0950\u0958-\u0961\u0966-\u096F\u0971-\u0980\u0985-\u098C\u098F\u0990\u0993-\u09A8\u09AA-\u09B0\u09B2\u09B6-\u09B9\u09BD\u09CE\u09DC\u09DD\u09DF-\u09E1\u09E6-\u09F1\u09F4-\u09F9\u09FC\u0A05-\u0A0A\u0A0F\u0A10\u0A13-\u0A28\u0A2A-\u0A30\u0A32\u0A33\u0A35\u0A36\u0A38\u0A39\u0A59-\u0A5C\u0A5E\u0A66-\u0A6F\u0A72-\u0A74\u0A85-\u0A8D\u0A8F-\u0A91\u0A93-\u0AA8\u0AAA-\u0AB0\u0AB2\u0AB3\u0AB5-\u0AB9\u0ABD\u0AD0\u0AE0\u0AE1\u0AE6-\u0AEF\u0AF9\u0B05-\u0B0C\u0B0F\u0B10\u0B13-\u0B28\u0B2A-\u0B30\u0B32\u0B33\u0B35-\u0B39\u0B3D\u0B5C\u0B5D\u0B5F-\u0B61\u0B66-\u0B6F\u0B71-\u0B77\u0B83\u0B85-\u0B8A\u0B8E-\u0B90\u0B92-\u0B95\u0B99\u0B9A\u0B9C\u0B9E\u0B9F\u0BA3\u0BA4\u0BA8-\u0BAA\u0BAE-\u0BB9\u0BD0\u0BE6-\u0BF2\u0C05-\u0C0C\u0C0E-\u0C10\u0C12-\u0C28\u0C2A-\u0C39\u0C3D\u0C58-\u0C5A\u0C60\u0C61\u0C66-\u0C6F\u0C78-\u0C7E\u0C80\u0C85-\u0C8C\u0C8E-\u0C90\u0C92-\u0CA8\u0CAA-\u0CB3\u0CB5-\u0CB9\u0CBD\u0CDE\u0CE0\u0CE1\u0CE6-\u0CEF\u0CF1\u0CF2\u0D04-\u0D0C\u0D0E-\u0D10\u0D12-\u0D3A\u0D3D\u0D4E\u0D54-\u0D56\u0D58-\u0D61\u0D66-\u0D78\u0D7A-\u0D7F\u0D85-\u0D96\u0D9A-\u0DB1\u0DB3-\u0DBB\u0DBD\u0DC0-\u0DC6\u0DE6-\u0DEF\u0E01-\u0E30\u0E32\u0E33\u0E40-\u0E46\u0E50-\u0E59\u0E81\u0E82\u0E84\u0E86-\u0E8A\u0E8C-\u0EA3\u0EA5\u0EA7-\u0EB0\u0EB2\u0EB3\u0EBD\u0EC0-\u0EC4\u0EC6\u0ED0-\u0ED9\u0EDC-\u0EDF\u0F00\u0F20-\u0F33\u0F40-\u0F47\u0F49-\u0F6C\u0F88-\u0F8C\u1000-\u102A\u103F-\u1049\u1050-\u1055\u105A-\u105D\u1061\u1065\u1066\u106E-\u1070\u1075-\u1081\u108E\u1090-\u1099\u10A0-\u10C5\u10C7\u10CD\u10D0-\u10FA\u10FC-\u1248\u124A-\u124D\u1250-\u1256\u1258\u125A-\u125D\u1260-\u1288\u128A-\u128D\u1290-\u12B0\u12B2-\u12B5\u12B8-\u12BE\u12C0\u12C2-\u12C5\u12C8-\u12D6\u12D8-\u1310\u1312-\u1315\u1318-\u135A\u1369-\u137C\u1380-\u138F\u13A0-\u13F5\u13F8-\u13FD\u1401-\u166C\u166F-\u167F\u1681-\u169A\u16A0-\u16EA\u16EE-\u16F8\u1700-\u170C\u170E-\u1711\u1720-\u1731\u1740-\u1751\u1760-\u176C\u176E-\u1770\u1780-\u17B3\u17D7\u17DC\u17E0-\u17E9\u17F0-\u17F9\u1810-\u1819\u1820-\u1878\u1880-\u1884\u1887-\u18A8\u18AA\u18B0-\u18F5\u1900-\u191E\u1946-\u196D\u1970-\u1974\u1980-\u19AB\u19B0-\u19C9\u19D0-\u19DA\u1A00-\u1A16\u1A20-\u1A54\u1A80-\u1A89\u1A90-\u1A99\u1AA7\u1B05-\u1B33\u1B45-\u1B4B\u1B50-\u1B59\u1B83-\u1BA0\u1BAE-\u1BE5\u1C00-\u1C23\u1C40-\u1C49\u1C4D-\u1C7D\u1C80-\u1C88\u1C90-\u1CBA\u1CBD-\u1CBF\u1CE9-\u1CEC\u1CEE-\u1CF3\u1CF5\u1CF6\u1CFA\u1D00-\u1DBF\u1E00-\u1F15\u1F18-\u1F1D\u1F20-\u1F45\u1F48-\u1F4D\u1F50-\u1F57\u1F59\u1F5B\u1F5D\u1F5F-\u1F7D\u1F80-\u1FB4\u1FB6-\u1FBC\u1FBE\u1FC2-\u1FC4\u1FC6-\u1FCC\u1FD0-\u1FD3\u1FD6-\u1FDB\u1FE0-\u1FEC\u1FF2-\u1FF4\u1FF6-\u1FFC\u2070\u2071\u2074-\u2079\u207F-\u2089\u2090-\u209C\u2102\u2107\u210A-\u2113\u2115\u2119-\u211D\u2124\u2126\u2128\u212A-\u212D\u212F-\u2139\u213C-\u213F\u2145-\u2149\u214E\u2150-\u2189\u2460-\u249B\u24EA-\u24FF\u2776-\u2793\u2C00-\u2C2E\u2C30-\u2C5E\u2C60-\u2CE4\u2CEB-\u2CEE\u2CF2\u2CF3\u2CFD\u2D00-\u2D25\u2D27\u2D2D\u2D30-\u2D67\u2D6F\u2D80-\u2D96\u2DA0-\u2DA6\u2DA8-\u2DAE\u2DB0-\u2DB6\u2DB8-\u2DBE\u2DC0-\u2DC6\u2DC8-\u2DCE\u2DD0-\u2DD6\u2DD8-\u2DDE\u2E2F\u3005-\u3007\u3021-\u3029\u3031-\u3035\u3038-\u303C\u3041-\u3096\u309D-\u309F\u30A1-\u30FA\u30FC-\u30FF\u3105-\u312F\u3131-\u318E\u3192-\u3195\u31A0-\u31BF\u31F0-\u31FF\u3220-\u3229\u3248-\u324F\u3251-\u325F\u3280-\u3289\u32B1-\u32BF\u3400-\u4DBF\u4E00-\u9FFC\uA000-\uA48C\uA4D0-\uA4FD\uA500-\uA60C\uA610-\uA62B\uA640-\uA66E\uA67F-\uA69D\uA6A0-\uA6EF\uA717-\uA71F\uA722-\uA788\uA78B-\uA7BF\uA7C2-\uA7CA\uA7F5-\uA801\uA803-\uA805\uA807-\uA80A\uA80C-\uA822\uA830-\uA835\uA840-\uA873\uA882-\uA8B3\uA8D0-\uA8D9\uA8F2-\uA8F7\uA8FB\uA8FD\uA8FE\uA900-\uA925\uA930-\uA946\uA960-\uA97C\uA984-\uA9B2\uA9CF-\uA9D9\uA9E0-\uA9E4\uA9E6-\uA9FE\uAA00-\uAA28\uAA40-\uAA42\uAA44-\uAA4B\uAA50-\uAA59\uAA60-\uAA76\uAA7A\uAA7E-\uAAAF\uAAB1\uAAB5\uAAB6\uAAB9-\uAABD\uAAC0\uAAC2\uAADB-\uAADD\uAAE0-\uAAEA\uAAF2-\uAAF4\uAB01-\uAB06\uAB09-\uAB0E\uAB11-\uAB16\uAB20-\uAB26\uAB28-\uAB2E\uAB30-\uAB5A\uAB5C-\uAB69\uAB70-\uABE2\uABF0-\uABF9\uAC00-\uD7A3\uD7B0-\uD7C6\uD7CB-\uD7FB\uF900-\uFA6D\uFA70-\uFAD9\uFB00-\uFB06\uFB13-\uFB17\uFB1D\uFB1F-\uFB28\uFB2A-\uFB36\uFB38-\uFB3C\uFB3E\uFB40\uFB41\uFB43\uFB44\uFB46-\uFBB1\uFBD3-\uFD3D\uFD50-\uFD8F\uFD92-\uFDC7\uFDF0-\uFDFB\uFE70-\uFE74\uFE76-\uFEFC\uFF10-\uFF19\uFF21-\uFF3A\uFF41-\uFF5A\uFF66-\uFFBE\uFFC2-\uFFC7\uFFCA-\uFFCF\uFFD2-\uFFD7\uFFDA-\uFFDC]|\uD800[\uDC00-\uDC0B\uDC0D-\uDC26\uDC28-\uDC3A\uDC3C\uDC3D\uDC3F-\uDC4D\uDC50-\uDC5D\uDC80-\uDCFA\uDD07-\uDD33\uDD40-\uDD78\uDD8A\uDD8B\uDE80-\uDE9C\uDEA0-\uDED0\uDEE1-\uDEFB\uDF00-\uDF23\uDF2D-\uDF4A\uDF50-\uDF75\uDF80-\uDF9D\uDFA0-\uDFC3\uDFC8-\uDFCF\uDFD1-\uDFD5]|\uD801[\uDC00-\uDC9D\uDCA0-\uDCA9\uDCB0-\uDCD3\uDCD8-\uDCFB\uDD00-\uDD27\uDD30-\uDD63\uDE00-\uDF36\uDF40-\uDF55\uDF60-\uDF67]|\uD802[\uDC00-\uDC05\uDC08\uDC0A-\uDC35\uDC37\uDC38\uDC3C\uDC3F-\uDC55\uDC58-\uDC76\uDC79-\uDC9E\uDCA7-\uDCAF\uDCE0-\uDCF2\uDCF4\uDCF5\uDCFB-\uDD1B\uDD20-\uDD39\uDD80-\uDDB7\uDDBC-\uDDCF\uDDD2-\uDE00\uDE10-\uDE13\uDE15-\uDE17\uDE19-\uDE35\uDE40-\uDE48\uDE60-\uDE7E\uDE80-\uDE9F\uDEC0-\uDEC7\uDEC9-\uDEE4\uDEEB-\uDEEF\uDF00-\uDF35\uDF40-\uDF55\uDF58-\uDF72\uDF78-\uDF91\uDFA9-\uDFAF]|\uD803[\uDC00-\uDC48\uDC80-\uDCB2\uDCC0-\uDCF2\uDCFA-\uDD23\uDD30-\uDD39\uDE60-\uDE7E\uDE80-\uDEA9\uDEB0\uDEB1\uDF00-\uDF27\uDF30-\uDF45\uDF51-\uDF54\uDFB0-\uDFCB\uDFE0-\uDFF6]|\uD804[\uDC03-\uDC37\uDC52-\uDC6F\uDC83-\uDCAF\uDCD0-\uDCE8\uDCF0-\uDCF9\uDD03-\uDD26\uDD36-\uDD3F\uDD44\uDD47\uDD50-\uDD72\uDD76\uDD83-\uDDB2\uDDC1-\uDDC4\uDDD0-\uDDDA\uDDDC\uDDE1-\uDDF4\uDE00-\uDE11\uDE13-\uDE2B\uDE80-\uDE86\uDE88\uDE8A-\uDE8D\uDE8F-\uDE9D\uDE9F-\uDEA8\uDEB0-\uDEDE\uDEF0-\uDEF9\uDF05-\uDF0C\uDF0F\uDF10\uDF13-\uDF28\uDF2A-\uDF30\uDF32\uDF33\uDF35-\uDF39\uDF3D\uDF50\uDF5D-\uDF61]|\uD805[\uDC00-\uDC34\uDC47-\uDC4A\uDC50-\uDC59\uDC5F-\uDC61\uDC80-\uDCAF\uDCC4\uDCC5\uDCC7\uDCD0-\uDCD9\uDD80-\uDDAE\uDDD8-\uDDDB\uDE00-\uDE2F\uDE44\uDE50-\uDE59\uDE80-\uDEAA\uDEB8\uDEC0-\uDEC9\uDF00-\uDF1A\uDF30-\uDF3B]|\uD806[\uDC00-\uDC2B\uDCA0-\uDCF2\uDCFF-\uDD06\uDD09\uDD0C-\uDD13\uDD15\uDD16\uDD18-\uDD2F\uDD3F\uDD41\uDD50-\uDD59\uDDA0-\uDDA7\uDDAA-\uDDD0\uDDE1\uDDE3\uDE00\uDE0B-\uDE32\uDE3A\uDE50\uDE5C-\uDE89\uDE9D\uDEC0-\uDEF8]|\uD807[\uDC00-\uDC08\uDC0A-\uDC2E\uDC40\uDC50-\uDC6C\uDC72-\uDC8F\uDD00-\uDD06\uDD08\uDD09\uDD0B-\uDD30\uDD46\uDD50-\uDD59\uDD60-\uDD65\uDD67\uDD68\uDD6A-\uDD89\uDD98\uDDA0-\uDDA9\uDEE0-\uDEF2\uDFB0\uDFC0-\uDFD4]|\uD808[\uDC00-\uDF99]|\uD809[\uDC00-\uDC6E\uDC80-\uDD43]|[\uD80C\uD81C-\uD820\uD822\uD840-\uD868\uD86A-\uD86C\uD86F-\uD872\uD874-\uD879\uD880-\uD883][\uDC00-\uDFFF]|\uD80D[\uDC00-\uDC2E]|\uD811[\uDC00-\uDE46]|\uD81A[\uDC00-\uDE38\uDE40-\uDE5E\uDE60-\uDE69\uDED0-\uDEED\uDF00-\uDF2F\uDF40-\uDF43\uDF50-\uDF59\uDF5B-\uDF61\uDF63-\uDF77\uDF7D-\uDF8F]|\uD81B[\uDE40-\uDE96\uDF00-\uDF4A\uDF50\uDF93-\uDF9F\uDFE0\uDFE1\uDFE3]|\uD821[\uDC00-\uDFF7]|\uD823[\uDC00-\uDCD5\uDD00-\uDD08]|\uD82C[\uDC00-\uDD1E\uDD50-\uDD52\uDD64-\uDD67\uDD70-\uDEFB]|\uD82F[\uDC00-\uDC6A\uDC70-\uDC7C\uDC80-\uDC88\uDC90-\uDC99]|\uD834[\uDEE0-\uDEF3\uDF60-\uDF78]|\uD835[\uDC00-\uDC54\uDC56-\uDC9C\uDC9E\uDC9F\uDCA2\uDCA5\uDCA6\uDCA9-\uDCAC\uDCAE-\uDCB9\uDCBB\uDCBD-\uDCC3\uDCC5-\uDD05\uDD07-\uDD0A\uDD0D-\uDD14\uDD16-\uDD1C\uDD1E-\uDD39\uDD3B-\uDD3E\uDD40-\uDD44\uDD46\uDD4A-\uDD50\uDD52-\uDEA5\uDEA8-\uDEC0\uDEC2-\uDEDA\uDEDC-\uDEFA\uDEFC-\uDF14\uDF16-\uDF34\uDF36-\uDF4E\uDF50-\uDF6E\uDF70-\uDF88\uDF8A-\uDFA8\uDFAA-\uDFC2\uDFC4-\uDFCB\uDFCE-\uDFFF]|\uD838[\uDD00-\uDD2C\uDD37-\uDD3D\uDD40-\uDD49\uDD4E\uDEC0-\uDEEB\uDEF0-\uDEF9]|\uD83A[\uDC00-\uDCC4\uDCC7-\uDCCF\uDD00-\uDD43\uDD4B\uDD50-\uDD59]|\uD83B[\uDC71-\uDCAB\uDCAD-\uDCAF\uDCB1-\uDCB4\uDD01-\uDD2D\uDD2F-\uDD3D\uDE00-\uDE03\uDE05-\uDE1F\uDE21\uDE22\uDE24\uDE27\uDE29-\uDE32\uDE34-\uDE37\uDE39\uDE3B\uDE42\uDE47\uDE49\uDE4B\uDE4D-\uDE4F\uDE51\uDE52\uDE54\uDE57\uDE59\uDE5B\uDE5D\uDE5F\uDE61\uDE62\uDE64\uDE67-\uDE6A\uDE6C-\uDE72\uDE74-\uDE77\uDE79-\uDE7C\uDE7E\uDE80-\uDE89\uDE8B-\uDE9B\uDEA1-\uDEA3\uDEA5-\uDEA9\uDEAB-\uDEBB]|\uD83C[\uDD00-\uDD0C]|\uD83E[\uDFF0-\uDFF9]|\uD869[\uDC00-\uDEDD\uDF00-\uDFFF]|\uD86D[\uDC00-\uDF34\uDF40-\uDFFF]|\uD86E[\uDC00-\uDC1D\uDC20-\uDFFF]|\uD873[\uDC00-\uDEA1\uDEB0-\uDFFF]|\uD87A[\uDC00-\uDFE0]|\uD87E[\uDC00-\uDE1D]|\uD884[\uDC00-\uDF4A])/)))// _ can't be between two alphanumerics. \p{L}\p{N} includes non-english alphabet/numbers as well
{var nextChar=match[1]||match[2]||"";if(!nextChar||nextChar&&(""===prevChar||this.rules.inline.punctuation.exec(prevChar))){var rDelim,rLength,lLength=match[0].length-1,delimTotal=lLength,midDelimTotal=0,endReg="*"===match[0][0]?this.rules.inline.emStrong.rDelimAst:this.rules.inline.emStrong.rDelimUnd;endReg.lastIndex=0,// Clip maskedSrc to same section of string as src (move to lexer?)
maskedSrc=maskedSrc.slice(-1*src.length+lLength);while(null!=(match=endReg.exec(maskedSrc)))if(rDelim=match[1]||match[2]||match[3]||match[4]||match[5]||match[6],rDelim)if(// skip single * in __abc*abc__
rLength=rDelim.length,match[3]||match[4])
// found another Left Delim
delimTotal+=rLength;else if(!((match[5]||match[6])&&lLength%3)||(lLength+rLength)%3){if(delimTotal-=rLength,!(delimTotal>0)){// Create `em` if smallest delimiter has odd char count. *a***
if(// Haven't found enough closing delimiters
// Remove extra characters. *a*** -> *a*
rLength=Math.min(rLength,rLength+delimTotal+midDelimTotal),Math.min(lLength,rLength)%2){var _text=src.slice(1,lLength+match.index+rLength);return{type:"em",raw:src.slice(0,lLength+match.index+rLength+1),text:_text,tokens:this.lexer.inlineTokens(_text,[])}}// Create 'strong' if smallest delimiter has even char count. **a***
var text=src.slice(2,lLength+match.index+rLength-1);return{type:"strong",raw:src.slice(0,lLength+match.index+rLength+1),text:text,tokens:this.lexer.inlineTokens(text,[])}}}else midDelimTotal+=rLength}}},_proto.codespan=function(src){var cap=this.rules.inline.code.exec(src);if(cap){var text=cap[2].replace(/\n/g," "),hasNonSpaceChars=/[^ ]/.test(text),hasSpaceCharsOnBothEnds=/^ /.test(text)&&/ $/.test(text);return hasNonSpaceChars&&hasSpaceCharsOnBothEnds&&(text=text.substring(1,text.length-1)),text=_escape(text,!0),{type:"codespan",raw:cap[0],text:text}}},_proto.br=function(src){var cap=this.rules.inline.br.exec(src);if(cap)return{type:"br",raw:cap[0]}},_proto.del=function(src){var cap=this.rules.inline.del.exec(src);if(cap)return{type:"del",raw:cap[0],text:cap[2],tokens:this.lexer.inlineTokens(cap[2],[])}},_proto.autolink=function(src,mangle){var text,href,cap=this.rules.inline.autolink.exec(src);if(cap)return"@"===cap[2]?(text=_escape(this.options.mangle?mangle(cap[1]):cap[1]),href="mailto:"+text):(text=_escape(cap[1]),href=text),{type:"link",raw:cap[0],text:text,href:href,tokens:[{type:"text",raw:text,text:text}]}},_proto.url=function(src,mangle){var cap;if(cap=this.rules.inline.url.exec(src)){var text,href;if("@"===cap[2])text=_escape(this.options.mangle?mangle(cap[0]):cap[0]),href="mailto:"+text;else{
// do extended autolink path validation
var prevCapZero;do{prevCapZero=cap[0],cap[0]=this.rules.inline._backpedal.exec(cap[0])[0]}while(prevCapZero!==cap[0]);text=_escape(cap[0]),href="www."===cap[1]?"http://"+text:text}return{type:"link",raw:cap[0],text:text,href:href,tokens:[{type:"text",raw:text,text:text}]}}},_proto.inlineText=function(src,smartypants){var text,cap=this.rules.inline.text.exec(src);if(cap)return text=this.lexer.state.inRawBlock?this.options.sanitize?this.options.sanitizer?this.options.sanitizer(cap[0]):_escape(cap[0]):cap[0]:_escape(this.options.smartypants?smartypants(cap[0]):cap[0]),{type:"text",raw:cap[0],text:text}},Tokenizer}(),noopTest=helpers.noopTest,edit=helpers.edit,merge$1=helpers.merge,block$1={newline:/^(?: *(?:\n|$))+/,code:/^( {4}[^\n]+(?:\n(?: *(?:\n|$))*)?)+/,fences:/^ {0,3}(`{3,}(?=[^`\n]*\n)|~{3,})([^\n]*)\n(?:|([\s\S]*?)\n)(?: {0,3}\1[~`]* *(?=\n|$)|$)/,hr:/^ {0,3}((?:- *){3,}|(?:_ *){3,}|(?:\* *){3,})(?:\n+|$)/,heading:/^ {0,3}(#{1,6})(?=\s|$)(.*)(?:\n+|$)/,blockquote:/^( {0,3}> ?(paragraph|[^\n]*)(?:\n|$))+/,list:/^( {0,3}bull)( [^\n]+?)?(?:\n|$)/,html:"^ {0,3}(?:<(script|pre|style|textarea)[\\s>][\\s\\S]*?(?:</\\1>[^\\n]*\\n+|$)|comment[^\\n]*(\\n+|$)|<\\?[\\s\\S]*?(?:\\?>\\n*|$)|<![A-Z][\\s\\S]*?(?:>\\n*|$)|<!\\[CDATA\\[[\\s\\S]*?(?:\\]\\]>\\n*|$)|</?(tag)(?: +|\\n|/?>)[\\s\\S]*?(?:(?:\\n *)+\\n|$)|<(?!script|pre|style|textarea)([a-z][\\w-]*)(?:attribute)*? */?>(?=[ \\t]*(?:\\n|$))[\\s\\S]*?(?:(?:\\n *)+\\n|$)|</(?!script|pre|style|textarea)[a-z][\\w-]*\\s*>(?=[ \\t]*(?:\\n|$))[\\s\\S]*?(?:(?:\\n *)+\\n|$))",def:/^ {0,3}\[(label)\]: *\n? *<?([^\s>]+)>?(?:(?: +\n? *| *\n *)(title))? *(?:\n+|$)/,table:noopTest,lheading:/^([^\n]+)\n {0,3}(=+|-+) *(?:\n+|$)/,
// regex template, placeholders will be replaced according to different paragraph
// interruption rules of commonmark and the original markdown spec:
_paragraph:/^([^\n]+(?:\n(?!hr|heading|lheading|blockquote|fences|list|html| +\n)[^\n]+)*)/,text:/^[^\n]+/,_label:/(?!\s*\])(?:\\[\[\]]|[^\[\]])+/,_title:/(?:"(?:\\"?|[^"\\])*"|'[^'\n]*(?:\n[^'\n]+)*\n?'|\([^()]*\))/};block$1.def=edit(block$1.def).replace("label",block$1._label).replace("title",block$1._title).getRegex(),block$1.bullet=/(?:[*+-]|\d{1,9}[.)])/,block$1.listItemStart=edit(/^( *)(bull) */).replace("bull",block$1.bullet).getRegex(),block$1.list=edit(block$1.list).replace(/bull/g,block$1.bullet).replace("hr","\\n+(?=\\1?(?:(?:- *){3,}|(?:_ *){3,}|(?:\\* *){3,})(?:\\n+|$))").replace("def","\\n+(?="+block$1.def.source+")").getRegex(),block$1._tag="address|article|aside|base|basefont|blockquote|body|caption|center|col|colgroup|dd|details|dialog|dir|div|dl|dt|fieldset|figcaption|figure|footer|form|frame|frameset|h[1-6]|head|header|hr|html|iframe|legend|li|link|main|menu|menuitem|meta|nav|noframes|ol|optgroup|option|p|param|section|source|summary|table|tbody|td|tfoot|th|thead|title|tr|track|ul",block$1._comment=/<!--(?!-?>)[\s\S]*?(?:-->|$)/,block$1.html=edit(block$1.html,"i").replace("comment",block$1._comment).replace("tag",block$1._tag).replace("attribute",/ +[a-zA-Z:_][\w.:-]*(?: *= *"[^"\n]*"| *= *'[^'\n]*'| *= *[^\s"'=<>`]+)?/).getRegex(),block$1.paragraph=edit(block$1._paragraph).replace("hr",block$1.hr).replace("heading"," {0,3}#{1,6} ").replace("|lheading","").replace("blockquote"," {0,3}>").replace("fences"," {0,3}(?:`{3,}(?=[^`\\n]*\\n)|~{3,})[^\\n]*\\n").replace("list"," {0,3}(?:[*+-]|1[.)]) ").replace("html","</?(?:tag)(?: +|\\n|/?>)|<(?:script|pre|style|textarea|!--)").replace("tag",block$1._tag).getRegex(),block$1.blockquote=edit(block$1.blockquote).replace("paragraph",block$1.paragraph).getRegex(),
/**
   * Normal Block Grammar
   */
block$1.normal=merge$1({},block$1),
/**
   * GFM Block Grammar
   */
block$1.gfm=merge$1({},block$1.normal,{table:"^ *([^\\n ].*\\|.*)\\n {0,3}(?:\\| *)?(:?-+:? *(?:\\| *:?-+:? *)*)\\|?(?:\\n((?:(?! *\\n|hr|heading|blockquote|code|fences|list|html).*(?:\\n|$))*)\\n*|$)"}),block$1.gfm.table=edit(block$1.gfm.table).replace("hr",block$1.hr).replace("heading"," {0,3}#{1,6} ").replace("blockquote"," {0,3}>").replace("code"," {4}[^\\n]").replace("fences"," {0,3}(?:`{3,}(?=[^`\\n]*\\n)|~{3,})[^\\n]*\\n").replace("list"," {0,3}(?:[*+-]|1[.)]) ").replace("html","</?(?:tag)(?: +|\\n|/?>)|<(?:script|pre|style|textarea|!--)").replace("tag",block$1._tag).getRegex(),
/**
   * Pedantic grammar (original John Gruber's loose markdown specification)
   */
block$1.pedantic=merge$1({},block$1.normal,{html:edit("^ *(?:comment *(?:\\n|\\s*$)|<(tag)[\\s\\S]+?</\\1> *(?:\\n{2,}|\\s*$)|<tag(?:\"[^\"]*\"|'[^']*'|\\s[^'\"/>\\s]*)*?/?> *(?:\\n{2,}|\\s*$))").replace("comment",block$1._comment).replace(/tag/g,"(?!(?:a|em|strong|small|s|cite|q|dfn|abbr|data|time|code|var|samp|kbd|sub|sup|i|b|u|mark|ruby|rt|rp|bdi|bdo|span|br|wbr|ins|del|img)\\b)\\w+(?!:|[^\\w\\s@]*@)\\b").getRegex(),def:/^ *\[([^\]]+)\]: *<?([^\s>]+)>?(?: +(["(][^\n]+[")]))? *(?:\n+|$)/,heading:/^(#{1,6})(.*)(?:\n+|$)/,fences:noopTest,
// fences not supported
paragraph:edit(block$1.normal._paragraph).replace("hr",block$1.hr).replace("heading"," *#{1,6} *[^\n]").replace("lheading",block$1.lheading).replace("blockquote"," {0,3}>").replace("|fences","").replace("|list","").replace("|html","").getRegex()});
/**
   * Inline-Level Grammar
   */
var inline$1={escape:/^\\([!"#$%&'()*+,\-./:;<=>?@\[\]\\^_`{|}~])/,autolink:/^<(scheme:[^\s\x00-\x1f<>]*|email)>/,url:noopTest,tag:"^comment|^</[a-zA-Z][\\w:-]*\\s*>|^<[a-zA-Z][\\w-]*(?:attribute)*?\\s*/?>|^<\\?[\\s\\S]*?\\?>|^<![a-zA-Z]+\\s[\\s\\S]*?>|^<!\\[CDATA\\[[\\s\\S]*?\\]\\]>",
// CDATA section
link:/^!?\[(label)\]\(\s*(href)(?:\s+(title))?\s*\)/,reflink:/^!?\[(label)\]\[(?!\s*\])((?:\\[\[\]]?|[^\[\]\\])+)\]/,nolink:/^!?\[(?!\s*\])((?:\[[^\[\]]*\]|\\[\[\]]|[^\[\]])*)\](?:\[\])?/,reflinkSearch:"reflink|nolink(?!\\()",emStrong:{lDelim:/^(?:\*+(?:([punct_])|[^\s*]))|^_+(?:([punct*])|([^\s_]))/,
//        (1) and (2) can only be a Right Delimiter. (3) and (4) can only be Left.  (5) and (6) can be either Left or Right.
//        () Skip other delimiter (1) #***                   (2) a***#, a***                   (3) #***a, ***a                 (4) ***#              (5) #***#                 (6) a***a
rDelimAst:/\_\_[^_*]*?\*[^_*]*?\_\_|[punct_](\*+)(?=[\s]|$)|[^punct*_\s](\*+)(?=[punct_\s]|$)|[punct_\s](\*+)(?=[^punct*_\s])|[\s](\*+)(?=[punct_])|[punct_](\*+)(?=[punct_])|[^punct*_\s](\*+)(?=[^punct*_\s])/,rDelimUnd:/\*\*[^_*]*?\_[^_*]*?\*\*|[punct*](\_+)(?=[\s]|$)|[^punct*_\s](\_+)(?=[punct*\s]|$)|[punct*\s](\_+)(?=[^punct*_\s])|[\s](\_+)(?=[punct*])|[punct*](\_+)(?=[punct*])/},code:/^(`+)([^`]|[^`][\s\S]*?[^`])\1(?!`)/,br:/^( {2,}|\\)\n(?!\s*$)/,del:noopTest,text:/^(`+|[^`])(?:(?= {2,}\n)|[\s\S]*?(?:(?=[\\<!\[`*_]|\b_|$)|[^ ](?= {2,}\n)))/,punctuation:/^([\spunctuation])/,// list of punctuation marks from CommonMark spec
// without * and _ to handle the different emphasis markers * and _
_punctuation:"!\"#$%&'()+\\-.,/:;<=>?@\\[\\]`^{|}~"};inline$1.punctuation=edit(inline$1.punctuation).replace(/punctuation/g,inline$1._punctuation).getRegex(),// sequences em should skip over [title](link), `code`, <html>
inline$1.blockSkip=/\[[^\]]*?\]\([^\)]*?\)|`[^`]*?`|<[^>]*?>/g,inline$1.escapedEmSt=/\\\*|\\_/g,inline$1._comment=edit(block$1._comment).replace("(?:--\x3e|$)","--\x3e").getRegex(),inline$1.emStrong.lDelim=edit(inline$1.emStrong.lDelim).replace(/punct/g,inline$1._punctuation).getRegex(),inline$1.emStrong.rDelimAst=edit(inline$1.emStrong.rDelimAst,"g").replace(/punct/g,inline$1._punctuation).getRegex(),inline$1.emStrong.rDelimUnd=edit(inline$1.emStrong.rDelimUnd,"g").replace(/punct/g,inline$1._punctuation).getRegex(),inline$1._escapes=/\\([!"#$%&'()*+,\-./:;<=>?@\[\]\\^_`{|}~])/g,inline$1._scheme=/[a-zA-Z][a-zA-Z0-9+.-]{1,31}/,inline$1._email=/[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+(@)[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+(?![-_])/,inline$1.autolink=edit(inline$1.autolink).replace("scheme",inline$1._scheme).replace("email",inline$1._email).getRegex(),inline$1._attribute=/\s+[a-zA-Z:_][\w.:-]*(?:\s*=\s*"[^"]*"|\s*=\s*'[^']*'|\s*=\s*[^\s"'=<>`]+)?/,inline$1.tag=edit(inline$1.tag).replace("comment",inline$1._comment).replace("attribute",inline$1._attribute).getRegex(),inline$1._label=/(?:\[(?:\\.|[^\[\]\\])*\]|\\.|`[^`]*`|[^\[\]\\`])*?/,inline$1._href=/<(?:\\.|[^\n<>\\])+>|[^\s\x00-\x1f]*/,inline$1._title=/"(?:\\"?|[^"\\])*"|'(?:\\'?|[^'\\])*'|\((?:\\\)?|[^)\\])*\)/,inline$1.link=edit(inline$1.link).replace("label",inline$1._label).replace("href",inline$1._href).replace("title",inline$1._title).getRegex(),inline$1.reflink=edit(inline$1.reflink).replace("label",inline$1._label).getRegex(),inline$1.reflinkSearch=edit(inline$1.reflinkSearch,"g").replace("reflink",inline$1.reflink).replace("nolink",inline$1.nolink).getRegex(),
/**
   * Normal Inline Grammar
   */
inline$1.normal=merge$1({},inline$1),
/**
   * Pedantic Inline Grammar
   */
inline$1.pedantic=merge$1({},inline$1.normal,{strong:{start:/^__|\*\*/,middle:/^__(?=\S)([\s\S]*?\S)__(?!_)|^\*\*(?=\S)([\s\S]*?\S)\*\*(?!\*)/,endAst:/\*\*(?!\*)/g,endUnd:/__(?!_)/g},em:{start:/^_|\*/,middle:/^()\*(?=\S)([\s\S]*?\S)\*(?!\*)|^_(?=\S)([\s\S]*?\S)_(?!_)/,endAst:/\*(?!\*)/g,endUnd:/_(?!_)/g},link:edit(/^!?\[(label)\]\((.*?)\)/).replace("label",inline$1._label).getRegex(),reflink:edit(/^!?\[(label)\]\s*\[([^\]]*)\]/).replace("label",inline$1._label).getRegex()}),
/**
   * GFM Inline Grammar
   */
inline$1.gfm=merge$1({},inline$1.normal,{escape:edit(inline$1.escape).replace("])","~|])").getRegex(),_extended_email:/[A-Za-z0-9._+-]+(@)[a-zA-Z0-9-_]+(?:\.[a-zA-Z0-9-_]*[a-zA-Z0-9])+(?![-_])/,url:/^((?:ftp|https?):\/\/|www\.)(?:[a-zA-Z0-9\-]+\.?)+[^\s<]*|^email/,_backpedal:/(?:[^?!.,:;*_~()&]+|\([^)]*\)|&(?![a-zA-Z0-9]+;$)|[?!.,:;*_~)]+(?!$))+/,del:/^(~~?)(?=[^\s~])([\s\S]*?[^\s~])\1(?=[^~]|$)/,text:/^([`~]+|[^`~])(?:(?= {2,}\n)|(?=[a-zA-Z0-9.!#$%&'*+\/=?_`{\|}~-]+@)|[\s\S]*?(?:(?=[\\<!\[`*~_]|\b_|https?:\/\/|ftp:\/\/|www\.|$)|[^ ](?= {2,}\n)|[^a-zA-Z0-9.!#$%&'*+\/=?_`{\|}~-](?=[a-zA-Z0-9.!#$%&'*+\/=?_`{\|}~-]+@)))/}),inline$1.gfm.url=edit(inline$1.gfm.url,"i").replace("email",inline$1.gfm._extended_email).getRegex(),
/**
   * GFM + Line Breaks Inline Grammar
   */
inline$1.breaks=merge$1({},inline$1.gfm,{br:edit(inline$1.br).replace("{2,}","*").getRegex(),text:edit(inline$1.gfm.text).replace("\\b_","\\b_| {2,}\\n").replace(/\{2,\}/g,"*").getRegex()});var rules={block:block$1,inline:inline$1},Tokenizer$1=Tokenizer_1,defaults$3=defaults$5.exports.defaults,block=rules.block,inline=rules.inline,repeatString=helpers.repeatString;
/**
   * smartypants text replacement
   */
function smartypants(text){return text.replace(/---/g,"—").replace(/--/g,"–").replace(/(^|[-\u2014/(\[{"\s])'/g,"$1‘").replace(/'/g,"’").replace(/(^|[-\u2014/(\[{\u2018\s])"/g,"$1“").replace(/"/g,"”").replace(/\.{3}/g,"…")}
/**
   * mangle email addresses
   */function mangle(text){var i,ch,out="",l=text.length;for(i=0;i<l;i++)ch=text.charCodeAt(i),Math.random()>.5&&(ch="x"+ch.toString(16)),out+="&#"+ch+";";return out}
/**
   * Block Lexer
   */var Lexer_1=function(){function Lexer(options){this.tokens=[],this.tokens.links=Object.create(null),this.options=options||defaults$3,this.options.tokenizer=this.options.tokenizer||new Tokenizer$1,this.tokenizer=this.options.tokenizer,this.tokenizer.options=this.options,this.tokenizer.lexer=this,this.inlineQueue=[],this.state={inLink:!1,inRawBlock:!1,top:!0};var rules={block:block.normal,inline:inline.normal};this.options.pedantic?(rules.block=block.pedantic,rules.inline=inline.pedantic):this.options.gfm&&(rules.block=block.gfm,this.options.breaks?rules.inline=inline.breaks:rules.inline=inline.gfm),this.tokenizer.rules=rules}
/**
     * Expose Rules
     */
/**
     * Static Lex Method
     */Lexer.lex=function(src,options){var lexer=new Lexer(options);return lexer.lex(src)}
/**
     * Static Lex Inline Method
     */,Lexer.lexInline=function(src,options){var lexer=new Lexer(options);return lexer.inlineTokens(src)}
/**
     * Preprocessing
     */;var _proto=Lexer.prototype;return _proto.lex=function(src){var next;src=src.replace(/\r\n|\r/g,"\n").replace(/\t/g,"    "),this.blockTokens(src,this.tokens);while(next=this.inlineQueue.shift())this.inlineTokens(next.src,next.tokens);return this.tokens}
/**
     * Lexing
     */,_proto.blockTokens=function(src,tokens){var token,lastToken,cutSrc,lastParagraphClipped,_this=this;void 0===tokens&&(tokens=[]),this.options.pedantic&&(src=src.replace(/^ +$/gm,""));while(src)if(!(this.options.extensions&&this.options.extensions.block&&this.options.extensions.block.some((function(extTokenizer){return!!(token=extTokenizer.call({lexer:_this},src,tokens))&&(src=src.substring(token.raw.length),tokens.push(token),!0)}))))// newline
if(token=this.tokenizer.space(src))src=src.substring(token.raw.length),token.type&&tokens.push(token);else// code
if(token=this.tokenizer.code(src))src=src.substring(token.raw.length),lastToken=tokens[tokens.length-1],// An indented code block cannot interrupt a paragraph.
!lastToken||"paragraph"!==lastToken.type&&"text"!==lastToken.type?tokens.push(token):(lastToken.raw+="\n"+token.raw,lastToken.text+="\n"+token.text,this.inlineQueue[this.inlineQueue.length-1].src=lastToken.text);else// fences
if(token=this.tokenizer.fences(src))src=src.substring(token.raw.length),tokens.push(token);else// heading
if(token=this.tokenizer.heading(src))src=src.substring(token.raw.length),tokens.push(token);else// hr
if(token=this.tokenizer.hr(src))src=src.substring(token.raw.length),tokens.push(token);else// blockquote
if(token=this.tokenizer.blockquote(src))src=src.substring(token.raw.length),tokens.push(token);else// list
if(token=this.tokenizer.list(src))src=src.substring(token.raw.length),tokens.push(token);else// html
if(token=this.tokenizer.html(src))src=src.substring(token.raw.length),tokens.push(token);else// def
if(token=this.tokenizer.def(src))src=src.substring(token.raw.length),lastToken=tokens[tokens.length-1],!lastToken||"paragraph"!==lastToken.type&&"text"!==lastToken.type?this.tokens.links[token.tag]||(this.tokens.links[token.tag]={href:token.href,title:token.title}):(lastToken.raw+="\n"+token.raw,lastToken.text+="\n"+token.raw,this.inlineQueue[this.inlineQueue.length-1].src=lastToken.text);else// table (gfm)
if(token=this.tokenizer.table(src))src=src.substring(token.raw.length),tokens.push(token);else// lheading
if(token=this.tokenizer.lheading(src))src=src.substring(token.raw.length),tokens.push(token);else if(// top-level paragraph
// prevent paragraph consuming extensions by clipping 'src' to extension start
cutSrc=src,this.options.extensions&&this.options.extensions.startBlock&&function(){var startIndex=1/0,tempSrc=src.slice(1),tempStart=void 0;_this.options.extensions.startBlock.forEach((function(getStartIndex){tempStart=getStartIndex.call({lexer:this},tempSrc),"number"===typeof tempStart&&tempStart>=0&&(startIndex=Math.min(startIndex,tempStart))})),startIndex<1/0&&startIndex>=0&&(cutSrc=src.substring(0,startIndex+1))}(),this.state.top&&(token=this.tokenizer.paragraph(cutSrc)))lastToken=tokens[tokens.length-1],lastParagraphClipped&&"paragraph"===lastToken.type?(lastToken.raw+="\n"+token.raw,lastToken.text+="\n"+token.text,this.inlineQueue.pop(),this.inlineQueue[this.inlineQueue.length-1].src=lastToken.text):tokens.push(token),lastParagraphClipped=cutSrc.length!==src.length,src=src.substring(token.raw.length);else// text
if(token=this.tokenizer.text(src))src=src.substring(token.raw.length),lastToken=tokens[tokens.length-1],lastToken&&"text"===lastToken.type?(lastToken.raw+="\n"+token.raw,lastToken.text+="\n"+token.text,this.inlineQueue.pop(),this.inlineQueue[this.inlineQueue.length-1].src=lastToken.text):tokens.push(token);else if(src){var errMsg="Infinite loop on byte: "+src.charCodeAt(0);if(this.options.silent)break;throw new Error(errMsg)}return this.state.top=!0,tokens},_proto.inline=function(src,tokens){this.inlineQueue.push({src:src,tokens:tokens})}
/**
     * Lexing/Compiling
     */,_proto.inlineTokens=function(src,tokens){var token,lastToken,cutSrc,_this2=this;void 0===tokens&&(tokens=[]);// String with links masked to avoid interference with em and strong
var match,keepPrevChar,prevChar,maskedSrc=src;// Mask out reflinks
if(this.tokens.links){var links=Object.keys(this.tokens.links);if(links.length>0)while(null!=(match=this.tokenizer.rules.inline.reflinkSearch.exec(maskedSrc)))links.includes(match[0].slice(match[0].lastIndexOf("[")+1,-1))&&(maskedSrc=maskedSrc.slice(0,match.index)+"["+repeatString("a",match[0].length-2)+"]"+maskedSrc.slice(this.tokenizer.rules.inline.reflinkSearch.lastIndex))}// Mask out other blocks
while(null!=(match=this.tokenizer.rules.inline.blockSkip.exec(maskedSrc)))maskedSrc=maskedSrc.slice(0,match.index)+"["+repeatString("a",match[0].length-2)+"]"+maskedSrc.slice(this.tokenizer.rules.inline.blockSkip.lastIndex);// Mask out escaped em & strong delimiters
while(null!=(match=this.tokenizer.rules.inline.escapedEmSt.exec(maskedSrc)))maskedSrc=maskedSrc.slice(0,match.index)+"++"+maskedSrc.slice(this.tokenizer.rules.inline.escapedEmSt.lastIndex);while(src)// extensions
if(keepPrevChar||(prevChar=""),keepPrevChar=!1,!(this.options.extensions&&this.options.extensions.inline&&this.options.extensions.inline.some((function(extTokenizer){return!!(token=extTokenizer.call({lexer:_this2},src,tokens))&&(src=src.substring(token.raw.length),tokens.push(token),!0)}))))// escape
if(token=this.tokenizer.escape(src))src=src.substring(token.raw.length),tokens.push(token);else// tag
if(token=this.tokenizer.tag(src))src=src.substring(token.raw.length),lastToken=tokens[tokens.length-1],lastToken&&"text"===token.type&&"text"===lastToken.type?(lastToken.raw+=token.raw,lastToken.text+=token.text):tokens.push(token);else// link
if(token=this.tokenizer.link(src))src=src.substring(token.raw.length),tokens.push(token);else// reflink, nolink
if(token=this.tokenizer.reflink(src,this.tokens.links))src=src.substring(token.raw.length),lastToken=tokens[tokens.length-1],lastToken&&"text"===token.type&&"text"===lastToken.type?(lastToken.raw+=token.raw,lastToken.text+=token.text):tokens.push(token);else// em & strong
if(token=this.tokenizer.emStrong(src,maskedSrc,prevChar))src=src.substring(token.raw.length),tokens.push(token);else// code
if(token=this.tokenizer.codespan(src))src=src.substring(token.raw.length),tokens.push(token);else// br
if(token=this.tokenizer.br(src))src=src.substring(token.raw.length),tokens.push(token);else// del (gfm)
if(token=this.tokenizer.del(src))src=src.substring(token.raw.length),tokens.push(token);else// autolink
if(token=this.tokenizer.autolink(src,mangle))src=src.substring(token.raw.length),tokens.push(token);else// url (gfm)
if(this.state.inLink||!(token=this.tokenizer.url(src,mangle))){if(// text
// prevent inlineText consuming extensions by clipping 'src' to extension start
cutSrc=src,this.options.extensions&&this.options.extensions.startInline&&function(){var startIndex=1/0,tempSrc=src.slice(1),tempStart=void 0;_this2.options.extensions.startInline.forEach((function(getStartIndex){tempStart=getStartIndex.call({lexer:this},tempSrc),"number"===typeof tempStart&&tempStart>=0&&(startIndex=Math.min(startIndex,tempStart))})),startIndex<1/0&&startIndex>=0&&(cutSrc=src.substring(0,startIndex+1))}(),token=this.tokenizer.inlineText(cutSrc,smartypants))src=src.substring(token.raw.length),"_"!==token.raw.slice(-1)&&(
// Track prevChar before string of ____ started
prevChar=token.raw.slice(-1)),keepPrevChar=!0,lastToken=tokens[tokens.length-1],lastToken&&"text"===lastToken.type?(lastToken.raw+=token.raw,lastToken.text+=token.text):tokens.push(token);else if(src){var errMsg="Infinite loop on byte: "+src.charCodeAt(0);if(this.options.silent)break;throw new Error(errMsg)}}else src=src.substring(token.raw.length),tokens.push(token);return tokens},_createClass(Lexer,null,[{key:"rules",get:function(){return{block:block,inline:inline}}}]),Lexer}(),defaults$2=defaults$5.exports.defaults,cleanUrl=helpers.cleanUrl,escape$1=helpers.escape,Renderer_1=function(){function Renderer(options){this.options=options||defaults$2}var _proto=Renderer.prototype;return _proto.code=function(_code,infostring,escaped){var lang=(infostring||"").match(/\S*/)[0];if(this.options.highlight){var out=this.options.highlight(_code,lang);null!=out&&out!==_code&&(escaped=!0,_code=out)}return _code=_code.replace(/\n$/,"")+"\n",lang?'<pre><code class="'+this.options.langPrefix+escape$1(lang,!0)+'">'+(escaped?_code:escape$1(_code,!0))+"</code></pre>\n":"<pre><code>"+(escaped?_code:escape$1(_code,!0))+"</code></pre>\n"},_proto.blockquote=function(quote){return"<blockquote>\n"+quote+"</blockquote>\n"},_proto.html=function(_html){return _html},_proto.heading=function(text,level,raw,slugger){return this.options.headerIds?"<h"+level+' id="'+this.options.headerPrefix+slugger.slug(raw)+'">'+text+"</h"+level+">\n":"<h"+level+">"+text+"</h"+level+">\n";// ignore IDs
},_proto.hr=function(){return this.options.xhtml?"<hr/>\n":"<hr>\n"},_proto.list=function(body,ordered,start){var type=ordered?"ol":"ul",startatt=ordered&&1!==start?' start="'+start+'"':"";return"<"+type+startatt+">\n"+body+"</"+type+">\n"},_proto.listitem=function(text){return"<li>"+text+"</li>\n"},_proto.checkbox=function(checked){return"<input "+(checked?'checked="" ':"")+'disabled="" type="checkbox"'+(this.options.xhtml?" /":"")+"> "},_proto.paragraph=function(text){return"<p>"+text+"</p>\n"},_proto.table=function(header,body){return body&&(body="<tbody>"+body+"</tbody>"),"<table>\n<thead>\n"+header+"</thead>\n"+body+"</table>\n"},_proto.tablerow=function(content){return"<tr>\n"+content+"</tr>\n"},_proto.tablecell=function(content,flags){var type=flags.header?"th":"td",tag=flags.align?"<"+type+' align="'+flags.align+'">':"<"+type+">";return tag+content+"</"+type+">\n"}// span level renderer
,_proto.strong=function(text){return"<strong>"+text+"</strong>"},_proto.em=function(text){return"<em>"+text+"</em>"},_proto.codespan=function(text){return"<code>"+text+"</code>"},_proto.br=function(){return this.options.xhtml?"<br/>":"<br>"},_proto.del=function(text){return"<del>"+text+"</del>"},_proto.link=function(href,title,text){if(href=cleanUrl(this.options.sanitize,this.options.baseUrl,href),null===href)return text;var out='<a href="'+escape$1(href)+'"';return title&&(out+=' title="'+title+'"'),out+=">"+text+"</a>",out},_proto.image=function(href,title,text){if(href=cleanUrl(this.options.sanitize,this.options.baseUrl,href),null===href)return text;var out='<img src="'+href+'" alt="'+text+'"';return title&&(out+=' title="'+title+'"'),out+=this.options.xhtml?"/>":">",out},_proto.text=function(_text){return _text},Renderer}(),TextRenderer_1=function(){function TextRenderer(){}var _proto=TextRenderer.prototype;
// no need for block level renderers
return _proto.strong=function(text){return text},_proto.em=function(text){return text},_proto.codespan=function(text){return text},_proto.del=function(text){return text},_proto.html=function(text){return text},_proto.text=function(_text){return _text},_proto.link=function(href,title,text){return""+text},_proto.image=function(href,title,text){return""+text},_proto.br=function(){return""},TextRenderer}(),Slugger_1=function(){function Slugger(){this.seen={}}var _proto=Slugger.prototype;return _proto.serialize=function(value){return value.toLowerCase().trim().replace(/<[!\/a-z].*?>/gi,"").replace(/[\u2000-\u206F\u2E00-\u2E7F\\'!"#$%&()*+,./:;<=>?@[\]^`{|}~]/g,"").replace(/\s/g,"-")}
/**
     * Finds the next safe (unique) slug to use
     */,_proto.getNextSafeSlug=function(originalSlug,isDryRun){var slug=originalSlug,occurenceAccumulator=0;if(this.seen.hasOwnProperty(slug)){occurenceAccumulator=this.seen[originalSlug];do{occurenceAccumulator++,slug=originalSlug+"-"+occurenceAccumulator}while(this.seen.hasOwnProperty(slug))}return isDryRun||(this.seen[originalSlug]=occurenceAccumulator,this.seen[slug]=0),slug}
/**
     * Convert string to unique id
     * @param {object} options
     * @param {boolean} options.dryrun Generates the next unique slug without updating the internal accumulator.
     */,_proto.slug=function(value,options){void 0===options&&(options={});var slug=this.serialize(value);return this.getNextSafeSlug(slug,options.dryrun)},Slugger}(),Renderer$1=Renderer_1,TextRenderer$1=TextRenderer_1,Slugger$1=Slugger_1,defaults$1=defaults$5.exports.defaults,unescape=helpers.unescape,Parser_1=function(){function Parser(options){this.options=options||defaults$1,this.options.renderer=this.options.renderer||new Renderer$1,this.renderer=this.options.renderer,this.renderer.options=this.options,this.textRenderer=new TextRenderer$1,this.slugger=new Slugger$1}
/**
     * Static Parse Method
     */Parser.parse=function(tokens,options){var parser=new Parser(options);return parser.parse(tokens)}
/**
     * Static Parse Inline Method
     */,Parser.parseInline=function(tokens,options){var parser=new Parser(options);return parser.parseInline(tokens)}
/**
     * Parse Loop
     */;var _proto=Parser.prototype;return _proto.parse=function(tokens,top){void 0===top&&(top=!0);var i,j,k,l2,l3,row,cell,header,body,token,ordered,start,loose,itemBody,item,checked,task,checkbox,ret,out="",l=tokens.length;for(i=0;i<l;i++)// Run any renderer extensions
if(token=tokens[i],this.options.extensions&&this.options.extensions.renderers&&this.options.extensions.renderers[token.type]&&(ret=this.options.extensions.renderers[token.type].call({parser:this},token),!1!==ret||!["space","hr","heading","code","table","blockquote","list","html","paragraph","text"].includes(token.type)))out+=ret||"";else switch(token.type){case"space":continue;case"hr":out+=this.renderer.hr();continue;case"heading":out+=this.renderer.heading(this.parseInline(token.tokens),token.depth,unescape(this.parseInline(token.tokens,this.textRenderer)),this.slugger);continue;case"code":out+=this.renderer.code(token.text,token.lang,token.escaped);continue;case"table":for(header="",// header
cell="",l2=token.header.length,j=0;j<l2;j++)cell+=this.renderer.tablecell(this.parseInline(token.header[j].tokens),{header:!0,align:token.align[j]});for(header+=this.renderer.tablerow(cell),body="",l2=token.rows.length,j=0;j<l2;j++){for(row=token.rows[j],cell="",l3=row.length,k=0;k<l3;k++)cell+=this.renderer.tablecell(this.parseInline(row[k].tokens),{header:!1,align:token.align[k]});body+=this.renderer.tablerow(cell)}out+=this.renderer.table(header,body);continue;case"blockquote":body=this.parse(token.tokens),out+=this.renderer.blockquote(body);continue;case"list":for(ordered=token.ordered,start=token.start,loose=token.loose,l2=token.items.length,body="",j=0;j<l2;j++)item=token.items[j],checked=item.checked,task=item.task,itemBody="",item.task&&(checkbox=this.renderer.checkbox(checked),loose?item.tokens.length>0&&"paragraph"===item.tokens[0].type?(item.tokens[0].text=checkbox+" "+item.tokens[0].text,item.tokens[0].tokens&&item.tokens[0].tokens.length>0&&"text"===item.tokens[0].tokens[0].type&&(item.tokens[0].tokens[0].text=checkbox+" "+item.tokens[0].tokens[0].text)):item.tokens.unshift({type:"text",text:checkbox}):itemBody+=checkbox),itemBody+=this.parse(item.tokens,loose),body+=this.renderer.listitem(itemBody,task,checked);out+=this.renderer.list(body,ordered,start);continue;case"html":
// TODO parse inline content if parameter markdown=1
out+=this.renderer.html(token.text);continue;case"paragraph":out+=this.renderer.paragraph(this.parseInline(token.tokens));continue;case"text":body=token.tokens?this.parseInline(token.tokens):token.text;while(i+1<l&&"text"===tokens[i+1].type)token=tokens[++i],body+="\n"+(token.tokens?this.parseInline(token.tokens):token.text);out+=top?this.renderer.paragraph(body):body;continue;default:var errMsg='Token with "'+token.type+'" type was not found.';if(this.options.silent)return;throw new Error(errMsg)}return out}
/**
     * Parse Inline Tokens
     */,_proto.parseInline=function(tokens,renderer){renderer=renderer||this.renderer;var i,token,ret,out="",l=tokens.length;for(i=0;i<l;i++)// Run any renderer extensions
if(token=tokens[i],this.options.extensions&&this.options.extensions.renderers&&this.options.extensions.renderers[token.type]&&(ret=this.options.extensions.renderers[token.type].call({parser:this},token),!1!==ret||!["escape","html","link","image","strong","em","codespan","br","del","text"].includes(token.type)))out+=ret||"";else switch(token.type){case"escape":out+=renderer.text(token.text);break;case"html":out+=renderer.html(token.text);break;case"link":out+=renderer.link(token.href,token.title,this.parseInline(token.tokens,renderer));break;case"image":out+=renderer.image(token.href,token.title,token.text);break;case"strong":out+=renderer.strong(this.parseInline(token.tokens,renderer));break;case"em":out+=renderer.em(this.parseInline(token.tokens,renderer));break;case"codespan":out+=renderer.codespan(token.text);break;case"br":out+=renderer.br();break;case"del":out+=renderer.del(this.parseInline(token.tokens,renderer));break;case"text":out+=renderer.text(token.text);break;default:var errMsg='Token with "'+token.type+'" type was not found.';if(this.options.silent)return;throw new Error(errMsg)}return out},Parser}(),Lexer=Lexer_1,Parser=Parser_1,Tokenizer=Tokenizer_1,Renderer=Renderer_1,TextRenderer=TextRenderer_1,Slugger=Slugger_1,merge=helpers.merge,checkSanitizeDeprecation=helpers.checkSanitizeDeprecation,escape=helpers.escape,getDefaults=defaults$5.exports.getDefaults,changeDefaults=defaults$5.exports.changeDefaults,defaults=defaults$5.exports.defaults;
/**
   * Marked
   */
function marked(src,opt,callback){
// throw error in case of non string input
if("undefined"===typeof src||null===src)throw new Error("marked(): input parameter is undefined or null");if("string"!==typeof src)throw new Error("marked(): input parameter is of type "+Object.prototype.toString.call(src)+", string expected");if("function"===typeof opt&&(callback=opt,opt=null),opt=merge({},marked.defaults,opt||{}),checkSanitizeDeprecation(opt),callback){var tokens,highlight=opt.highlight;try{tokens=Lexer.lex(src,opt)}catch(e){return callback(e)}var done=function(err){var out;if(!err)try{opt.walkTokens&&marked.walkTokens(tokens,opt.walkTokens),out=Parser.parse(tokens,opt)}catch(e){err=e}return opt.highlight=highlight,err?callback(err):callback(null,out)};if(!highlight||highlight.length<3)return done();if(delete opt.highlight,!tokens.length)return done();var pending=0;return marked.walkTokens(tokens,(function(token){"code"===token.type&&(pending++,setTimeout((function(){highlight(token.text,token.lang,(function(err,code){if(err)return done(err);null!=code&&code!==token.text&&(token.text=code,token.escaped=!0),pending--,0===pending&&done()}))}),0))})),void(0===pending&&done())}try{var _tokens=Lexer.lex(src,opt);return opt.walkTokens&&marked.walkTokens(_tokens,opt.walkTokens),Parser.parse(_tokens,opt)}catch(e){if(e.message+="\nPlease report this to https://github.com/markedjs/marked.",opt.silent)return"<p>An error occurred:</p><pre>"+escape(e.message+"",!0)+"</pre>";throw e}}
/**
   * Options
   */marked.options=marked.setOptions=function(opt){return merge(marked.defaults,opt),changeDefaults(marked.defaults),marked},marked.getDefaults=getDefaults,marked.defaults=defaults,
/**
   * Use Extension
   */
marked.use=function(){for(var _this=this,_len=arguments.length,args=new Array(_len),_key=0;_key<_len;_key++)args[_key]=arguments[_key];var hasExtensions,opts=merge.apply(void 0,[{}].concat(args)),extensions=marked.defaults.extensions||{renderers:{},childTokens:{}};args.forEach((function(pack){// ==-- Parse WalkTokens extensions --== //
if(
// ==-- Parse "addon" extensions --== //
pack.extensions&&(hasExtensions=!0,pack.extensions.forEach((function(ext){if(!ext.name)throw new Error("extension name required");if(ext.renderer){
// Renderer extensions
var prevRenderer=extensions.renderers?extensions.renderers[ext.name]:null;
// Replace extension with func to run new extension but fall back if false
extensions.renderers[ext.name]=prevRenderer?function(){for(var _len2=arguments.length,args=new Array(_len2),_key2=0;_key2<_len2;_key2++)args[_key2]=arguments[_key2];var ret=ext.renderer.apply(this,args);return!1===ret&&(ret=prevRenderer.apply(this,args)),ret}:ext.renderer}if(ext.tokenizer){
// Tokenizer Extensions
if(!ext.level||"block"!==ext.level&&"inline"!==ext.level)throw new Error("extension level must be 'block' or 'inline'");extensions[ext.level]?extensions[ext.level].unshift(ext.tokenizer):extensions[ext.level]=[ext.tokenizer],ext.start&&(
// Function to check for start of token
"block"===ext.level?extensions.startBlock?extensions.startBlock.push(ext.start):extensions.startBlock=[ext.start]:"inline"===ext.level&&(extensions.startInline?extensions.startInline.push(ext.start):extensions.startInline=[ext.start]))}ext.childTokens&&(
// Child tokens to be visited by walkTokens
extensions.childTokens[ext.name]=ext.childTokens)}))),// ==-- Parse "overwrite" extensions --== //
pack.renderer&&function(){var renderer=marked.defaults.renderer||new Renderer,_loop=function(prop){var prevRenderer=renderer[prop];// Replace renderer with func to run extension, but fall back if false
renderer[prop]=function(){for(var _len3=arguments.length,args=new Array(_len3),_key3=0;_key3<_len3;_key3++)args[_key3]=arguments[_key3];var ret=pack.renderer[prop].apply(renderer,args);return!1===ret&&(ret=prevRenderer.apply(renderer,args)),ret}};for(var prop in pack.renderer)_loop(prop);opts.renderer=renderer}(),pack.tokenizer&&function(){var tokenizer=marked.defaults.tokenizer||new Tokenizer,_loop2=function(prop){var prevTokenizer=tokenizer[prop];// Replace tokenizer with func to run extension, but fall back if false
tokenizer[prop]=function(){for(var _len4=arguments.length,args=new Array(_len4),_key4=0;_key4<_len4;_key4++)args[_key4]=arguments[_key4];var ret=pack.tokenizer[prop].apply(tokenizer,args);return!1===ret&&(ret=prevTokenizer.apply(tokenizer,args)),ret}};for(var prop in pack.tokenizer)_loop2(prop);opts.tokenizer=tokenizer}(),pack.walkTokens){var walkTokens=marked.defaults.walkTokens;opts.walkTokens=function(token){pack.walkTokens.call(_this,token),walkTokens&&walkTokens(token)}}hasExtensions&&(opts.extensions=extensions),marked.setOptions(opts)}))},
/**
   * Run callback for every token
   */
marked.walkTokens=function(tokens,callback){for(var _step,_loop3=function(){var token=_step.value;switch(callback(token),token.type){case"table":for(var _step2,_iterator2=_createForOfIteratorHelperLoose(token.header);!(_step2=_iterator2()).done;){var cell=_step2.value;marked.walkTokens(cell.tokens,callback)}for(var _step3,_iterator3=_createForOfIteratorHelperLoose(token.rows);!(_step3=_iterator3()).done;)for(var _step4,row=_step3.value,_iterator4=_createForOfIteratorHelperLoose(row);!(_step4=_iterator4()).done;){var _cell=_step4.value;marked.walkTokens(_cell.tokens,callback)}break;case"list":marked.walkTokens(token.items,callback);break;default:marked.defaults.extensions&&marked.defaults.extensions.childTokens&&marked.defaults.extensions.childTokens[token.type]?
// Walk any extensions
marked.defaults.extensions.childTokens[token.type].forEach((function(childTokens){marked.walkTokens(token[childTokens],callback)})):token.tokens&&marked.walkTokens(token.tokens,callback)}},_iterator=_createForOfIteratorHelperLoose(tokens);!(_step=_iterator()).done;)_loop3()},
/**
   * Parse Inline
   */
marked.parseInline=function(src,opt){
// throw error in case of non string input
if("undefined"===typeof src||null===src)throw new Error("marked.parseInline(): input parameter is undefined or null");if("string"!==typeof src)throw new Error("marked.parseInline(): input parameter is of type "+Object.prototype.toString.call(src)+", string expected");opt=merge({},marked.defaults,opt||{}),checkSanitizeDeprecation(opt);try{var tokens=Lexer.lexInline(src,opt);return opt.walkTokens&&marked.walkTokens(tokens,opt.walkTokens),Parser.parseInline(tokens,opt)}catch(e){if(e.message+="\nPlease report this to https://github.com/markedjs/marked.",opt.silent)return"<p>An error occurred:</p><pre>"+escape(e.message+"",!0)+"</pre>";throw e}},
/**
   * Expose
   */
marked.Parser=Parser,marked.parser=Parser.parse,marked.Renderer=Renderer,marked.TextRenderer=TextRenderer,marked.Lexer=Lexer,marked.lexer=Lexer.lex,marked.Tokenizer=Tokenizer,marked.Slugger=Slugger,marked.parse=marked;var marked_1=marked;return marked_1}))}();__marked_exports.Parser,__marked_exports.parser;var Renderer=__marked_exports.Renderer,parse=(__marked_exports.TextRenderer,__marked_exports.Lexer,__marked_exports.lexer,__marked_exports.Tokenizer,__marked_exports.Slugger,__marked_exports.parse)},
/***/323897:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Q:function(){/* binding */return parse}
/* harmony export */});
/* unused harmony export revive */
/* harmony import */var _buffer_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(153060),_uri_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(70666);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function parse(text){let data=JSON.parse(text);return data=revive(data),data}function revive(obj,depth=0){if(!obj||depth>200)return obj;if("object"===typeof obj){switch(obj.$mid){case 1/* Uri */:return _uri_js__WEBPACK_IMPORTED_MODULE_1__/* .URI */.o.revive(obj);case 2/* Regexp */:return new RegExp(obj.source,obj.flags)}if(obj instanceof _buffer_js__WEBPACK_IMPORTED_MODULE_0__/* .VSBuffer */.KN||obj instanceof Uint8Array)return obj;if(Array.isArray(obj))for(let i=0;i<obj.length;++i)obj[i]=revive(obj[i],depth+1);else
// walk object
for(const key in obj)Object.hasOwnProperty.call(obj,key)&&(obj[key]=revive(obj[key],depth+1))}return obj}
/***/},
/***/581170:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */G8:function(){/* binding */return guessMimeTypes},
/* harmony export */bS:function(){/* binding */return clearTextMimes},
/* harmony export */sA:function(){/* binding */return registerTextMime},
/* harmony export */vW:function(){/* binding */return Mimes}
/* harmony export */});
/* harmony import */var Mimes,_glob_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(314118),_network_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(566663),_path_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(555336),_resources_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(195935),_strings_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(697295);
/* harmony import */(function(Mimes){Mimes.text="text/plain",Mimes.binary="application/octet-stream",Mimes.unknown="application/unknown",Mimes.markdown="text/markdown",Mimes.latex="text/latex"})(Mimes||(Mimes={}));let registeredAssociations=[],nonUserRegisteredAssociations=[],userRegisteredAssociations=[];
/**
 * Associate a text mime to the registry.
 */
function registerTextMime(association,warnOnOverwrite=!1){
// Register
const associationItem=toTextMimeAssociationItem(association);registeredAssociations.push(associationItem),associationItem.userConfigured?userRegisteredAssociations.push(associationItem):nonUserRegisteredAssociations.push(associationItem),
// Check for conflicts unless this is a user configured association
warnOnOverwrite&&!associationItem.userConfigured&&registeredAssociations.forEach((a=>{a.mime===associationItem.mime||a.userConfigured||(associationItem.extension&&(a.extension,associationItem.extension),associationItem.filename&&(a.filename,associationItem.filename),associationItem.filepattern&&(a.filepattern,associationItem.filepattern),associationItem.firstline&&(a.firstline,associationItem.firstline))}))}function toTextMimeAssociationItem(association){return{id:association.id,mime:association.mime,filename:association.filename,extension:association.extension,filepattern:association.filepattern,firstline:association.firstline,userConfigured:association.userConfigured,filenameLowercase:association.filename?association.filename.toLowerCase():void 0,extensionLowercase:association.extension?association.extension.toLowerCase():void 0,filepatternLowercase:association.filepattern?(0,_glob_js__WEBPACK_IMPORTED_MODULE_0__/* .parse */.Qc)(association.filepattern.toLowerCase()):void 0,filepatternOnPath:!!association.filepattern&&association.filepattern.indexOf(_path_js__WEBPACK_IMPORTED_MODULE_2__/* .posix */.KR.sep)>=0}}
/**
 * Clear text mimes from the registry.
 */function clearTextMimes(onlyUserConfigured){onlyUserConfigured?(registeredAssociations=registeredAssociations.filter((a=>!a.userConfigured)),userRegisteredAssociations=[]):(registeredAssociations=[],nonUserRegisteredAssociations=[],userRegisteredAssociations=[])}
/**
 * Given a file, return the best matching mime type for it
 */function guessMimeTypes(resource,firstLine){let path;if(resource)switch(resource.scheme){case _network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.file:path=resource.fsPath;break;case _network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.data:const metadata=_resources_js__WEBPACK_IMPORTED_MODULE_3__/* .DataUri */.Vb.parseMetaData(resource);path=metadata.get(_resources_js__WEBPACK_IMPORTED_MODULE_3__/* .DataUri */.Vb.META_DATA_LABEL);break;default:path=resource.path}if(!path)return[Mimes.unknown];path=path.toLowerCase();const filename=(0,_path_js__WEBPACK_IMPORTED_MODULE_2__/* .basename */.EZ)(path),configuredMime=guessMimeTypeByPath(path,filename,userRegisteredAssociations);
// 1.) User configured mappings have highest priority
if(configuredMime)return[configuredMime,Mimes.text];
// 2.) Registered mappings have middle priority
const registeredMime=guessMimeTypeByPath(path,filename,nonUserRegisteredAssociations);if(registeredMime)return[registeredMime,Mimes.text];
// 3.) Firstline has lowest priority
if(firstLine){const firstlineMime=guessMimeTypeByFirstline(firstLine);if(firstlineMime)return[firstlineMime,Mimes.text]}return[Mimes.unknown]}function guessMimeTypeByPath(path,filename,associations){var _a;let filenameMatch=null,patternMatch=null,extensionMatch=null;
// We want to prioritize associations based on the order they are registered so that the last registered
// association wins over all other. This is for https://github.com/microsoft/vscode/issues/20074
for(let i=associations.length-1;i>=0;i--){const association=associations[i];
// First exact name match
if(filename===association.filenameLowercase){filenameMatch=association;break;// take it!
}
// Longest pattern match
if(association.filepattern&&(!patternMatch||association.filepattern.length>patternMatch.filepattern.length)){const target=association.filepatternOnPath?path:filename;// match on full path if pattern contains path separator
(null===(_a=association.filepatternLowercase)||void 0===_a?void 0:_a.call(association,target))&&(patternMatch=association)}
// Longest extension match
association.extension&&(!extensionMatch||association.extension.length>extensionMatch.extension.length)&&filename.endsWith(association.extensionLowercase)&&(extensionMatch=association)}
// 1.) Exact name match has second highest priority
return filenameMatch?filenameMatch.mime:
// 2.) Match on pattern
patternMatch?patternMatch.mime:
// 3.) Match on extension comes next
extensionMatch?extensionMatch.mime:null}function guessMimeTypeByFirstline(firstLine){if((0,_strings_js__WEBPACK_IMPORTED_MODULE_4__/* .startsWithUTF8BOM */.uS)(firstLine)&&(firstLine=firstLine.substr(1)),firstLine.length>0)
// We want to prioritize associations based on the order they are registered so that the last registered
// association wins over all other. This is for https://github.com/microsoft/vscode/issues/20074
for(let i=registeredAssociations.length-1;i>=0;i--){const association=registeredAssociations[i];if(!association.firstline)continue;const matches=firstLine.match(association.firstline);if(matches&&matches.length>0)return association.mime}return null}
/***/},
/***/566663:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Gi:function(){/* binding */return FileAccess},
/* harmony export */WX:function(){/* binding */return RemoteAuthorities},
/* harmony export */lg:function(){/* binding */return Schemas}
/* harmony export */});
/* harmony import */var Schemas,_platform_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(901432),_uri_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(70666);
/* harmony import */(function(Schemas){
/**
     * A schema that is used for models that exist in memory
     * only and that have no correspondence on a server or such.
     */
Schemas.inMemory="inmemory",
/**
     * A schema that is used for setting files
     */
Schemas.vscode="vscode",
/**
     * A schema that is used for internal private files
     */
Schemas.internal="private",
/**
     * A walk-through document.
     */
Schemas.walkThrough="walkThrough",
/**
     * An embedded code snippet.
     */
Schemas.walkThroughSnippet="walkThroughSnippet",Schemas.http="http",Schemas.https="https",Schemas.file="file",Schemas.mailto="mailto",Schemas.untitled="untitled",Schemas.data="data",Schemas.command="command",Schemas.vscodeRemote="vscode-remote",Schemas.vscodeRemoteResource="vscode-remote-resource",Schemas.userData="vscode-userdata",Schemas.vscodeCustomEditor="vscode-custom-editor",Schemas.vscodeNotebook="vscode-notebook",Schemas.vscodeNotebookCell="vscode-notebook-cell",Schemas.vscodeNotebookCellMetadata="vscode-notebook-cell-metadata",Schemas.vscodeNotebookCellOutput="vscode-notebook-cell-output",Schemas.vscodeInteractive="vscode-interactive",Schemas.vscodeInteractiveInput="vscode-interactive-input",Schemas.vscodeSettings="vscode-settings",Schemas.vscodeWorkspaceTrust="vscode-workspace-trust",Schemas.vscodeTerminal="vscode-terminal",Schemas.webviewPanel="webview-panel",
/**
     * Scheme used for loading the wrapper html and script in webviews.
     */
Schemas.vscodeWebview="vscode-webview",
/**
     * Scheme used for extension pages
     */
Schemas.extension="extension",
/**
     * Scheme used as a replacement of `file` scheme to load
     * files with our custom protocol handler (desktop only).
     */
Schemas.vscodeFileResource="vscode-file",
/**
     * Scheme used for temporary resources
     */
Schemas.tmp="tmp"})(Schemas||(Schemas={}));class RemoteAuthoritiesImpl{constructor(){this._hosts=Object.create(null),this._ports=Object.create(null),this._connectionTokens=Object.create(null),this._preferredWebSchema="http",this._delegate=null}setPreferredWebSchema(schema){this._preferredWebSchema=schema}rewrite(uri){if(this._delegate)return this._delegate(uri);const authority=uri.authority;let host=this._hosts[authority];host&&-1!==host.indexOf(":")&&(host=`[${host}]`);const port=this._ports[authority],connectionToken=this._connectionTokens[authority];let query=`path=${encodeURIComponent(uri.path)}`;return"string"===typeof connectionToken&&(query+=`&tkn=${encodeURIComponent(connectionToken)}`),_uri_js__WEBPACK_IMPORTED_MODULE_1__/* .URI */.o.from({scheme:_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .isWeb */.$L?this._preferredWebSchema:Schemas.vscodeRemoteResource,authority:`${host}:${port}`,path:"/vscode-remote-resource",query:query})}}const RemoteAuthorities=new RemoteAuthoritiesImpl;class FileAccessImpl{asBrowserUri(uriOrModule,moduleIdToUrl){const uri=this.toUri(uriOrModule,moduleIdToUrl);
// Handle remote URIs via `RemoteAuthorities`
return uri.scheme===Schemas.vscodeRemote?RemoteAuthorities.rewrite(uri):
// Convert to `vscode-file` resource..
// ...only ever for `file` resources
uri.scheme===Schemas.file&&(
// ...and we run in native environments
_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .isNative */.tY||
// ...or web worker extensions on desktop
"function"===typeof _platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.importScripts&&_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.origin===`${Schemas.vscodeFileResource}://${FileAccessImpl.FALLBACK_AUTHORITY}`)?uri.with({scheme:Schemas.vscodeFileResource,
// We need to provide an authority here so that it can serve
// as origin for network and loading matters in chromium.
// If the URI is not coming with an authority already, we
// add our own
authority:uri.authority||FileAccessImpl.FALLBACK_AUTHORITY,query:null,fragment:null}):uri}toUri(uriOrModule,moduleIdToUrl){return _uri_js__WEBPACK_IMPORTED_MODULE_1__/* .URI */.o.isUri(uriOrModule)?uriOrModule:_uri_js__WEBPACK_IMPORTED_MODULE_1__/* .URI */.o.parse(moduleIdToUrl.toUrl(uriOrModule))}}FileAccessImpl.FALLBACK_AUTHORITY="vscode-app";const FileAccess=new FileAccessImpl;
/***/},
/***/159870:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function clamp(value,min,max){return Math.min(Math.max(value,min),max)}
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */n:function(){/* binding */return MovingAverage},
/* harmony export */u:function(){/* binding */return clamp}
/* harmony export */});class MovingAverage{constructor(){this._n=1,this._val=0}update(value){return this._val=this._val+(value-this._val)/this._n,this._n+=1,this}get value(){return this._val}}
/***/},
/***/936248:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */CJ:function(){/* binding */return getOrDefault},
/* harmony export */I8:function(){/* binding */return deepClone},
/* harmony export */_A:function(){/* binding */return deepFreeze},
/* harmony export */fS:function(){/* binding */return equals},
/* harmony export */jB:function(){/* binding */return mixin},
/* harmony export */rs:function(){/* binding */return cloneAndChange}
/* harmony export */});
/* harmony import */var _types_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(998401);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/function deepClone(obj){if(!obj||"object"!==typeof obj)return obj;if(obj instanceof RegExp)
// See https://github.com/microsoft/TypeScript/issues/10990
return obj;const result=Array.isArray(obj)?[]:{};return Object.keys(obj).forEach((key=>{obj[key]&&"object"===typeof obj[key]?result[key]=deepClone(obj[key]):result[key]=obj[key]})),result}function deepFreeze(obj){if(!obj||"object"!==typeof obj)return obj;const stack=[obj];while(stack.length>0){const obj=stack.shift();Object.freeze(obj);for(const key in obj)if(_hasOwnProperty.call(obj,key)){const prop=obj[key];"object"!==typeof prop||Object.isFrozen(prop)||stack.push(prop)}}return obj}const _hasOwnProperty=Object.prototype.hasOwnProperty;function cloneAndChange(obj,changer){return _cloneAndChange(obj,changer,new Set)}function _cloneAndChange(obj,changer,seen){if((0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isUndefinedOrNull */.Jp)(obj))return obj;const changed=changer(obj);if("undefined"!==typeof changed)return changed;if((0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isArray */.kJ)(obj)){const r1=[];for(const e of obj)r1.push(_cloneAndChange(e,changer,seen));return r1}if((0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(obj)){if(seen.has(obj))throw new Error("Cannot clone recursive data-structure");seen.add(obj);const r2={};for(let i2 in obj)_hasOwnProperty.call(obj,i2)&&(r2[i2]=_cloneAndChange(obj[i2],changer,seen));return seen.delete(obj),r2}return obj}
/**
 * Copies all properties of source into destination. The optional parameter "overwrite" allows to control
 * if existing properties on the destination should be overwritten or not. Defaults to true (overwrite).
 */function mixin(destination,source,overwrite=!0){return(0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(destination)?((0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(source)&&Object.keys(source).forEach((key=>{key in destination?overwrite&&((0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(destination[key])&&(0,_types_js__WEBPACK_IMPORTED_MODULE_0__/* .isObject */.Kn)(source[key])?mixin(destination[key],source[key],overwrite):destination[key]=source[key]):destination[key]=source[key]})),destination):source}function equals(one,other){if(one===other)return!0;if(null===one||void 0===one||null===other||void 0===other)return!1;if(typeof one!==typeof other)return!1;if("object"!==typeof one)return!1;if(Array.isArray(one)!==Array.isArray(other))return!1;let i,key;if(Array.isArray(one)){if(one.length!==other.length)return!1;for(i=0;i<one.length;i++)if(!equals(one[i],other[i]))return!1}else{const oneKeys=[];for(key in one)oneKeys.push(key);oneKeys.sort();const otherKeys=[];for(key in other)otherKeys.push(key);if(otherKeys.sort(),!equals(oneKeys,otherKeys))return!1;for(i=0;i<oneKeys.length;i++)if(!equals(one[oneKeys[i]],other[oneKeys[i]]))return!1}return!0}function getOrDefault(obj,fn,defaultValue){const result=fn(obj);return"undefined"===typeof result?defaultValue:result}
/***/},
/***/555336:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{EZ:function(){/* binding */return basename},XX:function(){/* binding */return dirname},DZ:function(){/* binding */return extname},Fv:function(){/* binding */return normalize},KR:function(){/* binding */return posix},Gf:function(){/* binding */return relative},DB:function(){/* binding */return resolve},ir:function(){/* binding */return sep},Ku:function(){/* binding */return win32}});
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/platform.js
var platform=__webpack_require__(901432);// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/process.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
let safeProcess;
// Native sandbox environment
if("undefined"!==typeof platform/* globals */.li.vscode&&"undefined"!==typeof platform/* globals */.li.vscode.process){const sandboxProcess=platform/* globals */.li.vscode.process;safeProcess={get platform(){return sandboxProcess.platform},get arch(){return sandboxProcess.arch},get env(){return sandboxProcess.env},cwd(){return sandboxProcess.cwd()},nextTick(callback){return(0,platform/* setImmediate */.xS)(callback)}}}
// Native node.js environment
else safeProcess="undefined"!==typeof process?{get platform(){return process.platform},get arch(){return process.arch},get env(){return{NODE_ENV:"production",BASE_URL:"{{staticURL}}/"}},cwd(){return{NODE_ENV:"production",BASE_URL:"{{staticURL}}/"}["VSCODE_CWD"]||process.cwd()},nextTick(callback){return process.nextTick(callback)}}:{
// Supported
get platform(){return platform/* isWindows */.ED?"win32":platform/* isMacintosh */.dz?"darwin":"linux"},get arch(){/* arch is undefined in web */},nextTick(callback){return(0,platform/* setImmediate */.xS)(callback)},
// Unsupported
get env(){return{}},cwd(){return"/"}}
/**
 * Provides safe access to the `cwd` property in node.js, sandboxed or web
 * environments.
 *
 * Note: in web, this property is hardcoded to be `/`.
 */;const cwd=safeProcess.cwd,process_platform=(safeProcess.env,safeProcess.platform),CHAR_UPPERCASE_A=65,CHAR_LOWERCASE_A=97,CHAR_UPPERCASE_Z=90,CHAR_LOWERCASE_Z=122,CHAR_DOT=46,CHAR_FORWARD_SLASH=47,CHAR_BACKWARD_SLASH=92,CHAR_COLON=58,CHAR_QUESTION_MARK=63;
/**
 * Provides safe access to the `env` property in node.js, sandboxed or web
 * environments.
 *
 * Note: in web, this property is hardcoded to be `{}`.
 */ /* ? */
class ErrorInvalidArgType extends Error{constructor(name,expected,actual){
// determiner: 'must be' or 'must not be'
let determiner;"string"===typeof expected&&0===expected.indexOf("not ")?(determiner="must not be",expected=expected.replace(/^not /,"")):determiner="must be";const type=-1!==name.indexOf(".")?"property":"argument";let msg=`The "${name}" ${type} ${determiner} of type ${expected}`;msg+=". Received type "+typeof actual,super(msg),this.code="ERR_INVALID_ARG_TYPE"}}function validateString(value,name){if("string"!==typeof value)throw new ErrorInvalidArgType(name,"string",value)}function isPathSeparator(code){return code===CHAR_FORWARD_SLASH||code===CHAR_BACKWARD_SLASH}function isPosixPathSeparator(code){return code===CHAR_FORWARD_SLASH}function isWindowsDeviceRoot(code){return code>=CHAR_UPPERCASE_A&&code<=CHAR_UPPERCASE_Z||code>=CHAR_LOWERCASE_A&&code<=CHAR_LOWERCASE_Z}
// Resolves . and .. elements in a path with directory names
function normalizeString(path,allowAboveRoot,separator,isPathSeparator){let res="",lastSegmentLength=0,lastSlash=-1,dots=0,code=0;for(let i=0;i<=path.length;++i){if(i<path.length)code=path.charCodeAt(i);else{if(isPathSeparator(code))break;code=CHAR_FORWARD_SLASH}if(isPathSeparator(code)){if(lastSlash===i-1||1===dots);else if(2===dots){if(res.length<2||2!==lastSegmentLength||res.charCodeAt(res.length-1)!==CHAR_DOT||res.charCodeAt(res.length-2)!==CHAR_DOT){if(res.length>2){const lastSlashIndex=res.lastIndexOf(separator);-1===lastSlashIndex?(res="",lastSegmentLength=0):(res=res.slice(0,lastSlashIndex),lastSegmentLength=res.length-1-res.lastIndexOf(separator)),lastSlash=i,dots=0;continue}if(0!==res.length){res="",lastSegmentLength=0,lastSlash=i,dots=0;continue}}allowAboveRoot&&(res+=res.length>0?`${separator}..`:"..",lastSegmentLength=2)}else res.length>0?res+=`${separator}${path.slice(lastSlash+1,i)}`:res=path.slice(lastSlash+1,i),lastSegmentLength=i-lastSlash-1;lastSlash=i,dots=0}else code===CHAR_DOT&&-1!==dots?++dots:dots=-1}return res}function _format(sep,pathObject){if(null===pathObject||"object"!==typeof pathObject)throw new ErrorInvalidArgType("pathObject","Object",pathObject);const dir=pathObject.dir||pathObject.root,base=pathObject.base||`${pathObject.name||""}${pathObject.ext||""}`;return dir?dir===pathObject.root?`${dir}${base}`:`${dir}${sep}${base}`:base}const win32={
// path.resolve([from ...], to)
resolve(...pathSegments){let resolvedDevice="",resolvedTail="",resolvedAbsolute=!1;for(let i=pathSegments.length-1;i>=-1;i--){let path;if(i>=0){
// Skip empty entries
if(path=pathSegments[i],validateString(path,"path"),0===path.length)continue}else 0===resolvedDevice.length?path=cwd():(
// Windows has the concept of drive-specific current working
// directories. If we've resolved a drive letter but not yet an
// absolute path, get cwd for that drive, or the process cwd if
// the drive cwd is not available. We're sure the device is not
// a UNC path at this points, because UNC paths are always absolute.
path={NODE_ENV:"production",BASE_URL:"{{staticURL}}/"}[`=${resolvedDevice}`]||cwd(),
// Verify that a cwd was found and that it actually points
// to our drive. If not, default to the drive's root.
(void 0===path||path.slice(0,2).toLowerCase()!==resolvedDevice.toLowerCase()&&path.charCodeAt(2)===CHAR_BACKWARD_SLASH)&&(path=`${resolvedDevice}\\`));const len=path.length;let rootEnd=0,device="",isAbsolute=!1;const code=path.charCodeAt(0);
// Try to match a root
if(1===len)isPathSeparator(code)&&(
// `path` contains just a path separator
rootEnd=1,isAbsolute=!0);else if(isPathSeparator(code))if(
// Possible UNC root
// If we started with a separator, we know we at least have an
// absolute path of some kind (UNC or otherwise)
isAbsolute=!0,isPathSeparator(path.charCodeAt(1))){
// Matched double path separator at beginning
let j=2,last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){const firstPart=path.slice(last,j);
// Matched!
last=j;
// Match 1 or more path separators
while(j<len&&isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;j!==len&&j===last||(
// We matched a UNC root
device=`\\\\${firstPart}\\${path.slice(last,j)}`,rootEnd=j)}}}else rootEnd=1;else isWindowsDeviceRoot(code)&&path.charCodeAt(1)===CHAR_COLON&&(
// Possible device root
device=path.slice(0,2),rootEnd=2,len>2&&isPathSeparator(path.charCodeAt(2))&&(
// Treat separator following drive name as an absolute path
// indicator
isAbsolute=!0,rootEnd=3));if(device.length>0)if(resolvedDevice.length>0){if(device.toLowerCase()!==resolvedDevice.toLowerCase())
// This path points to another device so it is not applicable
continue}else resolvedDevice=device;if(resolvedAbsolute){if(resolvedDevice.length>0)break}else if(resolvedTail=`${path.slice(rootEnd)}\\${resolvedTail}`,resolvedAbsolute=isAbsolute,isAbsolute&&resolvedDevice.length>0)break}
// At this point the path should be resolved to a full absolute path,
// but handle relative paths to be safe (might happen when process.cwd()
// fails)
// Normalize the tail path
return resolvedTail=normalizeString(resolvedTail,!resolvedAbsolute,"\\",isPathSeparator),resolvedAbsolute?`${resolvedDevice}\\${resolvedTail}`:`${resolvedDevice}${resolvedTail}`||"."},normalize(path){validateString(path,"path");const len=path.length;if(0===len)return".";let device,rootEnd=0,isAbsolute=!1;const code=path.charCodeAt(0);
// Try to match a root
if(1===len)
// `path` contains just a single char, exit early to avoid
// unnecessary work
return isPosixPathSeparator(code)?"\\":path;if(isPathSeparator(code))if(
// Possible UNC root
// If we started with a separator, we know we at least have an absolute
// path of some kind (UNC or otherwise)
isAbsolute=!0,isPathSeparator(path.charCodeAt(1))){
// Matched double path separator at beginning
let j=2,last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){const firstPart=path.slice(last,j);
// Matched!
last=j;
// Match 1 or more path separators
while(j<len&&isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j===len)
// We matched a UNC root only
// Return the normalized version of the UNC root since there
// is nothing left to process
return`\\\\${firstPart}\\${path.slice(last)}\\`;j!==last&&(
// We matched a UNC root with leftovers
device=`\\\\${firstPart}\\${path.slice(last,j)}`,rootEnd=j)}}}else rootEnd=1;else isWindowsDeviceRoot(code)&&path.charCodeAt(1)===CHAR_COLON&&(
// Possible device root
device=path.slice(0,2),rootEnd=2,len>2&&isPathSeparator(path.charCodeAt(2))&&(
// Treat separator following drive name as an absolute path
// indicator
isAbsolute=!0,rootEnd=3));let tail=rootEnd<len?normalizeString(path.slice(rootEnd),!isAbsolute,"\\",isPathSeparator):"";return 0!==tail.length||isAbsolute||(tail="."),tail.length>0&&isPathSeparator(path.charCodeAt(len-1))&&(tail+="\\"),void 0===device?isAbsolute?`\\${tail}`:tail:isAbsolute?`${device}\\${tail}`:`${device}${tail}`},isAbsolute(path){validateString(path,"path");const len=path.length;if(0===len)return!1;const code=path.charCodeAt(0);return isPathSeparator(code)||
// Possible device root
len>2&&isWindowsDeviceRoot(code)&&path.charCodeAt(1)===CHAR_COLON&&isPathSeparator(path.charCodeAt(2))},join(...paths){if(0===paths.length)return".";let joined,firstPart;for(let i=0;i<paths.length;++i){const arg=paths[i];validateString(arg,"path"),arg.length>0&&(void 0===joined?joined=firstPart=arg:joined+=`\\${arg}`)}if(void 0===joined)return".";
// Make sure that the joined path doesn't start with two slashes, because
// normalize() will mistake it for a UNC path then.

// This step is skipped when it is very clear that the user actually
// intended to point at a UNC path. This is assumed when the first
// non-empty string arguments starts with exactly two slashes followed by
// at least one more non-slash character.

// Note that for normalize() to treat a path as a UNC path it needs to
// have at least 2 components, so we don't filter for that here.
// This means that the user can use join to construct UNC paths from
// a server name and a share name; for example:
//   path.join('//server', 'share') -> '\\\\server\\share\\')
let needsReplace=!0,slashCount=0;if("string"===typeof firstPart&&isPathSeparator(firstPart.charCodeAt(0))){++slashCount;const firstLen=firstPart.length;firstLen>1&&isPathSeparator(firstPart.charCodeAt(1))&&(++slashCount,firstLen>2&&(isPathSeparator(firstPart.charCodeAt(2))?++slashCount:
// We matched a UNC path in the first part
needsReplace=!1))}if(needsReplace){
// Find any more consecutive slashes we need to replace
while(slashCount<joined.length&&isPathSeparator(joined.charCodeAt(slashCount)))slashCount++;
// Replace the slashes if needed
slashCount>=2&&(joined=`\\${joined.slice(slashCount)}`)}return win32.normalize(joined)},
// It will solve the relative path from `from` to `to`, for instance:
//  from = 'C:\\orandea\\test\\aaa'
//  to = 'C:\\orandea\\impl\\bbb'
// The output of the function should be: '..\\..\\impl\\bbb'
relative(from,to){if(validateString(from,"from"),validateString(to,"to"),from===to)return"";const fromOrig=win32.resolve(from),toOrig=win32.resolve(to);if(fromOrig===toOrig)return"";if(from=fromOrig.toLowerCase(),to=toOrig.toLowerCase(),from===to)return"";
// Trim any leading backslashes
let fromStart=0;while(fromStart<from.length&&from.charCodeAt(fromStart)===CHAR_BACKWARD_SLASH)fromStart++;
// Trim trailing backslashes (applicable to UNC paths only)
let fromEnd=from.length;while(fromEnd-1>fromStart&&from.charCodeAt(fromEnd-1)===CHAR_BACKWARD_SLASH)fromEnd--;const fromLen=fromEnd-fromStart;
// Trim any leading backslashes
let toStart=0;while(toStart<to.length&&to.charCodeAt(toStart)===CHAR_BACKWARD_SLASH)toStart++;
// Trim trailing backslashes (applicable to UNC paths only)
let toEnd=to.length;while(toEnd-1>toStart&&to.charCodeAt(toEnd-1)===CHAR_BACKWARD_SLASH)toEnd--;const toLen=toEnd-toStart,length=fromLen<toLen?fromLen:toLen;
// Compare paths to find the longest common path from root
let lastCommonSep=-1,i=0;for(;i<length;i++){const fromCode=from.charCodeAt(fromStart+i);if(fromCode!==to.charCodeAt(toStart+i))break;fromCode===CHAR_BACKWARD_SLASH&&(lastCommonSep=i)}
// We found a mismatch before the first common path separator was seen, so
// return the original `to`.
if(i!==length){if(-1===lastCommonSep)return toOrig}else{if(toLen>length){if(to.charCodeAt(toStart+i)===CHAR_BACKWARD_SLASH)
// We get here if `from` is the exact base path for `to`.
// For example: from='C:\\foo\\bar'; to='C:\\foo\\bar\\baz'
return toOrig.slice(toStart+i+1);if(2===i)
// We get here if `from` is the device root.
// For example: from='C:\\'; to='C:\\foo'
return toOrig.slice(toStart+i)}fromLen>length&&(from.charCodeAt(fromStart+i)===CHAR_BACKWARD_SLASH?
// We get here if `to` is the exact base path for `from`.
// For example: from='C:\\foo\\bar'; to='C:\\foo'
lastCommonSep=i:2===i&&(
// We get here if `to` is the device root.
// For example: from='C:\\foo\\bar'; to='C:\\'
lastCommonSep=3)),-1===lastCommonSep&&(lastCommonSep=0)}let out="";
// Generate the relative path based on the path difference between `to` and
// `from`
for(i=fromStart+lastCommonSep+1;i<=fromEnd;++i)i!==fromEnd&&from.charCodeAt(i)!==CHAR_BACKWARD_SLASH||(out+=0===out.length?"..":"\\..");
// Lastly, append the rest of the destination (`to`) path that comes after
// the common path parts
return toStart+=lastCommonSep,out.length>0?`${out}${toOrig.slice(toStart,toEnd)}`:(toOrig.charCodeAt(toStart)===CHAR_BACKWARD_SLASH&&++toStart,toOrig.slice(toStart,toEnd))},toNamespacedPath(path){
// Note: this will *probably* throw somewhere.
if("string"!==typeof path)return path;if(0===path.length)return"";const resolvedPath=win32.resolve(path);if(resolvedPath.length<=2)return path;if(resolvedPath.charCodeAt(0)===CHAR_BACKWARD_SLASH){
// Possible UNC root
if(resolvedPath.charCodeAt(1)===CHAR_BACKWARD_SLASH){const code=resolvedPath.charCodeAt(2);if(code!==CHAR_QUESTION_MARK&&code!==CHAR_DOT)
// Matched non-long UNC root, convert the path to a long UNC path
return`\\\\?\\UNC\\${resolvedPath.slice(2)}`}}else if(isWindowsDeviceRoot(resolvedPath.charCodeAt(0))&&resolvedPath.charCodeAt(1)===CHAR_COLON&&resolvedPath.charCodeAt(2)===CHAR_BACKWARD_SLASH)
// Matched device root, convert the path to a long UNC path
return`\\\\?\\${resolvedPath}`;return path},dirname(path){validateString(path,"path");const len=path.length;if(0===len)return".";let rootEnd=-1,offset=0;const code=path.charCodeAt(0);if(1===len)
// `path` contains just a path separator, exit early to avoid
// unnecessary work or a dot.
return isPathSeparator(code)?path:".";
// Try to match a root
if(isPathSeparator(code)){if(
// Possible UNC root
rootEnd=offset=1,isPathSeparator(path.charCodeAt(1))){
// Matched double path separator at beginning
let j=2,last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more path separators
while(j<len&&isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j===len)
// We matched a UNC root only
return path;j!==last&&(
// We matched a UNC root with leftovers
// Offset by 1 to include the separator after the UNC root to
// treat it as a "normal root" on top of a (UNC) root
rootEnd=offset=j+1)}}}
// Possible device root
}else isWindowsDeviceRoot(code)&&path.charCodeAt(1)===CHAR_COLON&&(rootEnd=len>2&&isPathSeparator(path.charCodeAt(2))?3:2,offset=rootEnd);let end=-1,matchedSlash=!0;for(let i=len-1;i>=offset;--i)if(isPathSeparator(path.charCodeAt(i))){if(!matchedSlash){end=i;break}}else
// We saw the first non-path separator
matchedSlash=!1;if(-1===end){if(-1===rootEnd)return".";end=rootEnd}return path.slice(0,end)},basename(path,ext){void 0!==ext&&validateString(ext,"ext"),validateString(path,"path");let i,start=0,end=-1,matchedSlash=!0;if(
// Check for a drive letter prefix so as not to mistake the following
// path separator as an extra separator at the end of the path that can be
// disregarded
path.length>=2&&isWindowsDeviceRoot(path.charCodeAt(0))&&path.charCodeAt(1)===CHAR_COLON&&(start=2),void 0!==ext&&ext.length>0&&ext.length<=path.length){if(ext===path)return"";let extIdx=ext.length-1,firstNonSlashEnd=-1;for(i=path.length-1;i>=start;--i){const code=path.charCodeAt(i);if(isPathSeparator(code)){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){start=i+1;break}}else-1===firstNonSlashEnd&&(
// We saw the first non-path separator, remember this index in case
// we need it if the extension ends up not matching
matchedSlash=!1,firstNonSlashEnd=i+1),extIdx>=0&&(
// Try to match the explicit extension
code===ext.charCodeAt(extIdx)?-1===--extIdx&&(
// We matched the extension, so mark this as the end of our path
// component
end=i):(
// Extension does not match, so our result is the entire path
// component
extIdx=-1,end=firstNonSlashEnd))}return start===end?end=firstNonSlashEnd:-1===end&&(end=path.length),path.slice(start,end)}for(i=path.length-1;i>=start;--i)if(isPathSeparator(path.charCodeAt(i))){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){start=i+1;break}}else-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// path component
matchedSlash=!1,end=i+1);return-1===end?"":path.slice(start,end)},extname(path){validateString(path,"path");let start=0,startDot=-1,startPart=0,end=-1,matchedSlash=!0,preDotState=0;
// Check for a drive letter prefix so as not to mistake the following
// path separator as an extra separator at the end of the path that can be
// disregarded
path.length>=2&&path.charCodeAt(1)===CHAR_COLON&&isWindowsDeviceRoot(path.charCodeAt(0))&&(start=startPart=2);for(let i=path.length-1;i>=start;--i){const code=path.charCodeAt(i);if(isPathSeparator(code)){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){startPart=i+1;break}}else-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// extension
matchedSlash=!1,end=i+1),code===CHAR_DOT?
// If this is our first dot, mark it as the start of our extension
-1===startDot?startDot=i:1!==preDotState&&(preDotState=1):-1!==startDot&&(
// We saw a non-dot and non-path separator before our dot, so we should
// have a good chance at having a non-empty extension
preDotState=-1)}return-1===startDot||-1===end||
// We saw a non-dot character immediately before the dot
0===preDotState||
// The (right-most) trimmed path component is exactly '..'
1===preDotState&&startDot===end-1&&startDot===startPart+1?"":path.slice(startDot,end)},format:_format.bind(null,"\\"),parse(path){validateString(path,"path");const ret={root:"",dir:"",base:"",ext:"",name:""};if(0===path.length)return ret;const len=path.length;let rootEnd=0,code=path.charCodeAt(0);if(1===len)return isPathSeparator(code)?(
// `path` contains just a path separator, exit early to avoid
// unnecessary work
ret.root=ret.dir=path,ret):(ret.base=ret.name=path,ret);
// Try to match a root
if(isPathSeparator(code)){if(
// Possible UNC root
rootEnd=1,isPathSeparator(path.charCodeAt(1))){
// Matched double path separator at beginning
let j=2,last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more path separators
while(j<len&&isPathSeparator(path.charCodeAt(j)))j++;if(j<len&&j!==last){
// Matched!
last=j;
// Match 1 or more non-path separators
while(j<len&&!isPathSeparator(path.charCodeAt(j)))j++;j===len?
// We matched a UNC root only
rootEnd=j:j!==last&&(
// We matched a UNC root with leftovers
rootEnd=j+1)}}}}else if(isWindowsDeviceRoot(code)&&path.charCodeAt(1)===CHAR_COLON){
// Possible device root
if(len<=2)
// `path` contains just a drive root, exit early to avoid
// unnecessary work
return ret.root=ret.dir=path,ret;if(rootEnd=2,isPathSeparator(path.charCodeAt(2))){if(3===len)
// `path` contains just a drive root, exit early to avoid
// unnecessary work
return ret.root=ret.dir=path,ret;rootEnd=3}}rootEnd>0&&(ret.root=path.slice(0,rootEnd));let startDot=-1,startPart=rootEnd,end=-1,matchedSlash=!0,i=path.length-1,preDotState=0;
// Get non-dir info
for(;i>=rootEnd;--i)if(code=path.charCodeAt(i),isPathSeparator(code)){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){startPart=i+1;break}}else-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// extension
matchedSlash=!1,end=i+1),code===CHAR_DOT?
// If this is our first dot, mark it as the start of our extension
-1===startDot?startDot=i:1!==preDotState&&(preDotState=1):-1!==startDot&&(
// We saw a non-dot and non-path separator before our dot, so we should
// have a good chance at having a non-empty extension
preDotState=-1);return-1!==end&&(-1===startDot||
// We saw a non-dot character immediately before the dot
0===preDotState||
// The (right-most) trimmed path component is exactly '..'
1===preDotState&&startDot===end-1&&startDot===startPart+1?ret.base=ret.name=path.slice(startPart,end):(ret.name=path.slice(startPart,startDot),ret.base=path.slice(startPart,end),ret.ext=path.slice(startDot,end))),
// If the directory is the root, use the entire root as the `dir` including
// the trailing slash if any (`C:\abc` -> `C:\`). Otherwise, strip out the
// trailing slash (`C:\abc\def` -> `C:\abc`).
ret.dir=startPart>0&&startPart!==rootEnd?path.slice(0,startPart-1):ret.root,ret},sep:"\\",delimiter:";",win32:null,posix:null},posix={
// path.resolve([from ...], to)
resolve(...pathSegments){let resolvedPath="",resolvedAbsolute=!1;for(let i=pathSegments.length-1;i>=-1&&!resolvedAbsolute;i--){const path=i>=0?pathSegments[i]:cwd();validateString(path,"path"),
// Skip empty entries
0!==path.length&&(resolvedPath=`${path}/${resolvedPath}`,resolvedAbsolute=path.charCodeAt(0)===CHAR_FORWARD_SLASH)}
// At this point the path should be resolved to a full absolute path, but
// handle relative paths to be safe (might happen when process.cwd() fails)
// Normalize the path
return resolvedPath=normalizeString(resolvedPath,!resolvedAbsolute,"/",isPosixPathSeparator),resolvedAbsolute?`/${resolvedPath}`:resolvedPath.length>0?resolvedPath:"."},normalize(path){if(validateString(path,"path"),0===path.length)return".";const isAbsolute=path.charCodeAt(0)===CHAR_FORWARD_SLASH,trailingSeparator=path.charCodeAt(path.length-1)===CHAR_FORWARD_SLASH;
// Normalize the path
return path=normalizeString(path,!isAbsolute,"/",isPosixPathSeparator),0===path.length?isAbsolute?"/":trailingSeparator?"./":".":(trailingSeparator&&(path+="/"),isAbsolute?`/${path}`:path)},isAbsolute(path){return validateString(path,"path"),path.length>0&&path.charCodeAt(0)===CHAR_FORWARD_SLASH},join(...paths){if(0===paths.length)return".";let joined;for(let i=0;i<paths.length;++i){const arg=paths[i];validateString(arg,"path"),arg.length>0&&(void 0===joined?joined=arg:joined+=`/${arg}`)}return void 0===joined?".":posix.normalize(joined)},relative(from,to){if(validateString(from,"from"),validateString(to,"to"),from===to)return"";
// Trim leading forward slashes.
if(from=posix.resolve(from),to=posix.resolve(to),from===to)return"";const fromStart=1,fromEnd=from.length,fromLen=fromEnd-fromStart,toStart=1,toLen=to.length-toStart,length=fromLen<toLen?fromLen:toLen;let lastCommonSep=-1,i=0;for(;i<length;i++){const fromCode=from.charCodeAt(fromStart+i);if(fromCode!==to.charCodeAt(toStart+i))break;fromCode===CHAR_FORWARD_SLASH&&(lastCommonSep=i)}if(i===length)if(toLen>length){if(to.charCodeAt(toStart+i)===CHAR_FORWARD_SLASH)
// We get here if `from` is the exact base path for `to`.
// For example: from='/foo/bar'; to='/foo/bar/baz'
return to.slice(toStart+i+1);if(0===i)
// We get here if `from` is the root
// For example: from='/'; to='/foo'
return to.slice(toStart+i)}else fromLen>length&&(from.charCodeAt(fromStart+i)===CHAR_FORWARD_SLASH?
// We get here if `to` is the exact base path for `from`.
// For example: from='/foo/bar/baz'; to='/foo/bar'
lastCommonSep=i:0===i&&(
// We get here if `to` is the root.
// For example: from='/foo/bar'; to='/'
lastCommonSep=0));let out="";
// Generate the relative path based on the path difference between `to`
// and `from`.
for(i=fromStart+lastCommonSep+1;i<=fromEnd;++i)i!==fromEnd&&from.charCodeAt(i)!==CHAR_FORWARD_SLASH||(out+=0===out.length?"..":"/..");
// Lastly, append the rest of the destination (`to`) path that comes after
// the common path parts.
return`${out}${to.slice(toStart+lastCommonSep)}`},toNamespacedPath(path){
// Non-op on posix systems
return path},dirname(path){if(validateString(path,"path"),0===path.length)return".";const hasRoot=path.charCodeAt(0)===CHAR_FORWARD_SLASH;let end=-1,matchedSlash=!0;for(let i=path.length-1;i>=1;--i)if(path.charCodeAt(i)===CHAR_FORWARD_SLASH){if(!matchedSlash){end=i;break}}else
// We saw the first non-path separator
matchedSlash=!1;return-1===end?hasRoot?"/":".":hasRoot&&1===end?"//":path.slice(0,end)},basename(path,ext){void 0!==ext&&validateString(ext,"ext"),validateString(path,"path");let i,start=0,end=-1,matchedSlash=!0;if(void 0!==ext&&ext.length>0&&ext.length<=path.length){if(ext===path)return"";let extIdx=ext.length-1,firstNonSlashEnd=-1;for(i=path.length-1;i>=0;--i){const code=path.charCodeAt(i);if(code===CHAR_FORWARD_SLASH){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){start=i+1;break}}else-1===firstNonSlashEnd&&(
// We saw the first non-path separator, remember this index in case
// we need it if the extension ends up not matching
matchedSlash=!1,firstNonSlashEnd=i+1),extIdx>=0&&(
// Try to match the explicit extension
code===ext.charCodeAt(extIdx)?-1===--extIdx&&(
// We matched the extension, so mark this as the end of our path
// component
end=i):(
// Extension does not match, so our result is the entire path
// component
extIdx=-1,end=firstNonSlashEnd))}return start===end?end=firstNonSlashEnd:-1===end&&(end=path.length),path.slice(start,end)}for(i=path.length-1;i>=0;--i)if(path.charCodeAt(i)===CHAR_FORWARD_SLASH){
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){start=i+1;break}}else-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// path component
matchedSlash=!1,end=i+1);return-1===end?"":path.slice(start,end)},extname(path){validateString(path,"path");let startDot=-1,startPart=0,end=-1,matchedSlash=!0,preDotState=0;for(let i=path.length-1;i>=0;--i){const code=path.charCodeAt(i);if(code!==CHAR_FORWARD_SLASH)-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// extension
matchedSlash=!1,end=i+1),code===CHAR_DOT?
// If this is our first dot, mark it as the start of our extension
-1===startDot?startDot=i:1!==preDotState&&(preDotState=1):-1!==startDot&&(
// We saw a non-dot and non-path separator before our dot, so we should
// have a good chance at having a non-empty extension
preDotState=-1);else
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){startPart=i+1;break}}return-1===startDot||-1===end||
// We saw a non-dot character immediately before the dot
0===preDotState||
// The (right-most) trimmed path component is exactly '..'
1===preDotState&&startDot===end-1&&startDot===startPart+1?"":path.slice(startDot,end)},format:_format.bind(null,"/"),parse(path){validateString(path,"path");const ret={root:"",dir:"",base:"",ext:"",name:""};if(0===path.length)return ret;const isAbsolute=path.charCodeAt(0)===CHAR_FORWARD_SLASH;let start;isAbsolute?(ret.root="/",start=1):start=0;let startDot=-1,startPart=0,end=-1,matchedSlash=!0,i=path.length-1,preDotState=0;
// Get non-dir info
for(;i>=start;--i){const code=path.charCodeAt(i);if(code!==CHAR_FORWARD_SLASH)-1===end&&(
// We saw the first non-path separator, mark this as the end of our
// extension
matchedSlash=!1,end=i+1),code===CHAR_DOT?
// If this is our first dot, mark it as the start of our extension
-1===startDot?startDot=i:1!==preDotState&&(preDotState=1):-1!==startDot&&(
// We saw a non-dot and non-path separator before our dot, so we should
// have a good chance at having a non-empty extension
preDotState=-1);else
// If we reached a path separator that was not part of a set of path
// separators at the end of the string, stop now
if(!matchedSlash){startPart=i+1;break}}if(-1!==end){const start=0===startPart&&isAbsolute?1:startPart;-1===startDot||
// We saw a non-dot character immediately before the dot
0===preDotState||
// The (right-most) trimmed path component is exactly '..'
1===preDotState&&startDot===end-1&&startDot===startPart+1?ret.base=ret.name=path.slice(start,end):(ret.name=path.slice(start,startDot),ret.base=path.slice(start,end),ret.ext=path.slice(startDot,end))}return startPart>0?ret.dir=path.slice(0,startPart-1):isAbsolute&&(ret.dir="/"),ret},sep:"/",delimiter:":",win32:null,posix:null};posix.win32=win32.win32=win32,posix.posix=win32.posix=posix;const normalize="win32"===process_platform?win32.normalize:posix.normalize,resolve="win32"===process_platform?win32.resolve:posix.resolve,relative="win32"===process_platform?win32.relative:posix.relative,dirname="win32"===process_platform?win32.dirname:posix.dirname,basename="win32"===process_platform?win32.basename:posix.basename,extname="win32"===process_platform?win32.extname:posix.extname,sep="win32"===process_platform?win32.sep:posix.sep},
/***/901432:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var _a;
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$L:function(){/* binding */return isWeb},
/* harmony export */ED:function(){/* binding */return isWindows},
/* harmony export */IJ:function(){/* binding */return isLinux},
/* harmony export */OS:function(){/* binding */return OS},
/* harmony export */WE:function(){/* binding */return userAgent},
/* harmony export */dz:function(){/* binding */return isMacintosh},
/* harmony export */gn:function(){/* binding */return isIOS},
/* harmony export */li:function(){/* binding */return globals},
/* harmony export */r:function(){/* binding */return isLittleEndian},
/* harmony export */tY:function(){/* binding */return isNative},
/* harmony export */xS:function(){/* binding */return setImmediate}
/* harmony export */});const LANGUAGE_DEFAULT="en";let _locale,_userAgent,_isWindows=!1,_isMacintosh=!1,_isLinux=!1,_isLinuxSnap=!1,_isNative=!1,_isWeb=!1,_isIOS=!1,_language=/* unused pure expression or super */null,_translationsConfigFile=/* unused pure expression or super */null;const globals="object"===typeof self?self:"object"===typeof __webpack_require__.g?__webpack_require__.g:{};let nodeProcess;"undefined"!==typeof globals.vscode&&"undefined"!==typeof globals.vscode.process?
// Native environment (sandboxed)
nodeProcess=globals.vscode.process:"undefined"!==typeof process&&(
// Native environment (non-sandboxed)
nodeProcess=process);const isElectronRenderer="string"===typeof(null===(_a=null===nodeProcess||void 0===nodeProcess?void 0:nodeProcess.versions)||void 0===_a?void 0:_a.electron)&&"renderer"===nodeProcess.type;
// Web environment
if("object"!==typeof navigator||isElectronRenderer){if("object"===typeof nodeProcess){_isWindows="win32"===nodeProcess.platform,_isMacintosh="darwin"===nodeProcess.platform,_isLinux="linux"===nodeProcess.platform,_isLinuxSnap=_isLinux&&!!nodeProcess.env["SNAP"]&&!!nodeProcess.env["SNAP_REVISION"],_locale=LANGUAGE_DEFAULT,_language=LANGUAGE_DEFAULT;const rawNlsConfig=nodeProcess.env["VSCODE_NLS_CONFIG"];if(rawNlsConfig)try{const nlsConfig=JSON.parse(rawNlsConfig),resolved=nlsConfig.availableLanguages["*"];_locale=nlsConfig.locale,
// VSCode's default language is 'en'
_language=resolved||LANGUAGE_DEFAULT,_translationsConfigFile=nlsConfig._translationsConfigFile}catch(e){}_isNative=!0}
// Unknown environment
}else _userAgent=navigator.userAgent,_isWindows=_userAgent.indexOf("Windows")>=0,_isMacintosh=_userAgent.indexOf("Macintosh")>=0,_isIOS=(_userAgent.indexOf("Macintosh")>=0||_userAgent.indexOf("iPad")>=0||_userAgent.indexOf("iPhone")>=0)&&!!navigator.maxTouchPoints&&navigator.maxTouchPoints>0,_isLinux=_userAgent.indexOf("Linux")>=0,_isWeb=!0,_locale=navigator.language,_language=_locale;let _platform=0/* Web */;_isMacintosh?_platform=1/* Mac */:_isWindows?_platform=3/* Windows */:_isLinux&&(_platform=2/* Linux */);const isWindows=_isWindows,isMacintosh=_isMacintosh,isLinux=_isLinux,isNative=_isNative,isWeb=_isWeb,isIOS=_isIOS,userAgent=_userAgent,setImmediate=function(){if(globals.setImmediate)return globals.setImmediate.bind(globals);if("function"===typeof globals.postMessage&&!globals.importScripts){let pending=[];globals.addEventListener("message",(e=>{if(e.data&&e.data.vscodeSetImmediateId)for(let i=0,len=pending.length;i<len;i++){const candidate=pending[i];if(candidate.id===e.data.vscodeSetImmediateId)return pending.splice(i,1),void candidate.callback()}}));let lastId=0;return callback=>{const myId=++lastId;pending.push({id:myId,callback:callback}),globals.postMessage({vscodeSetImmediateId:myId},"*")}}if("function"===typeof(null===nodeProcess||void 0===nodeProcess?void 0:nodeProcess.nextTick))return nodeProcess.nextTick.bind(nodeProcess);const _promise=Promise.resolve();return callback=>_promise.then(callback)}(),OS=_isMacintosh||_isIOS?2/* Macintosh */:_isWindows?1/* Windows */:3/* Linux */;let _isLittleEndian=!0,_isLittleEndianComputed=!1;function isLittleEndian(){if(!_isLittleEndianComputed){_isLittleEndianComputed=!0;const test=new Uint8Array(2);test[0]=1,test[1]=2;const view=new Uint16Array(test.buffer);_isLittleEndian=513===view[0]}return _isLittleEndian}
/***/},
/***/661134:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var Range;
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */e:function(){/* binding */return Range}
/* harmony export */}),function(Range){
/**
     * Returns the intersection between two ranges as a range itself.
     * Returns `{ start: 0, end: 0 }` if the intersection is empty.
     */
function intersect(one,other){if(one.start>=other.end||other.start>=one.end)return{start:0,end:0};const start=Math.max(one.start,other.start),end=Math.min(one.end,other.end);return end-start<=0?{start:0,end:0}:{start:start,end:end}}function isEmpty(range){return range.end-range.start<=0}function intersects(one,other){return!isEmpty(intersect(one,other))}function relativeComplement(one,other){const result=[],first={start:one.start,end:Math.min(other.start,one.end)},second={start:Math.max(other.end,one.start),end:one.end};return isEmpty(first)||result.push(first),isEmpty(second)||result.push(second),result}Range.intersect=intersect,Range.isEmpty=isEmpty,Range.intersects=intersects,Range.relativeComplement=relativeComplement}(Range||(Range={}))},
/***/195935:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */AH:function(){/* binding */return normalizePath},
/* harmony export */EZ:function(){/* binding */return basename},
/* harmony export */Hx:function(){/* binding */return basenameOrAuthority},
/* harmony export */SF:function(){/* binding */return extUri},
/* harmony export */Vb:function(){/* binding */return DataUri},
/* harmony export */Vo:function(){/* binding */return joinPath},
/* harmony export */XX:function(){/* binding */return dirname},
/* harmony export */Xy:function(){/* binding */return isEqual},
/* harmony export */i3:function(){/* binding */return resolvePath},
/* harmony export */z_:function(){/* binding */return originalFSPath}
/* harmony export */});
/* unused harmony export ExtUri */
/* harmony import */var _extpath_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(915527),_network_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(566663),_path_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(555336),_strings_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(697295),_uri_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(70666);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function originalFSPath(uri){return(0,_uri_js__WEBPACK_IMPORTED_MODULE_4__/* .uriToFsPath */.q)(uri,!0)}class ExtUri{constructor(_ignorePathCasing){this._ignorePathCasing=_ignorePathCasing}compare(uri1,uri2,ignoreFragment=!1){return uri1===uri2?0:(0,_strings_js__WEBPACK_IMPORTED_MODULE_3__/* .compare */.qu)(this.getComparisonKey(uri1,ignoreFragment),this.getComparisonKey(uri2,ignoreFragment))}isEqual(uri1,uri2,ignoreFragment=!1){return uri1===uri2||!(!uri1||!uri2)&&this.getComparisonKey(uri1,ignoreFragment)===this.getComparisonKey(uri2,ignoreFragment)}getComparisonKey(uri,ignoreFragment=!1){return uri.with({path:this._ignorePathCasing(uri)?uri.path.toLowerCase():void 0,fragment:ignoreFragment?null:void 0}).toString()}
// --- path math
joinPath(resource,...pathFragment){return _uri_js__WEBPACK_IMPORTED_MODULE_4__/* .URI */.o.joinPath(resource,...pathFragment)}basenameOrAuthority(resource){return basename(resource)||resource.authority}basename(resource){return _path_js__WEBPACK_IMPORTED_MODULE_2__/* .posix */.KR.basename(resource.path)}dirname(resource){if(0===resource.path.length)return resource;let dirname;return resource.scheme===_network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.file?dirname=_uri_js__WEBPACK_IMPORTED_MODULE_4__/* .URI */.o.file(_path_js__WEBPACK_IMPORTED_MODULE_2__/* .dirname */.XX(originalFSPath(resource))).path:(dirname=_path_js__WEBPACK_IMPORTED_MODULE_2__/* .posix */.KR.dirname(resource.path),resource.authority&&dirname.length&&47/* Slash */!==dirname.charCodeAt(0)&&(dirname="/")),resource.with({path:dirname})}normalizePath(resource){if(!resource.path.length)return resource;let normalizedPath;return normalizedPath=resource.scheme===_network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.file?_uri_js__WEBPACK_IMPORTED_MODULE_4__/* .URI */.o.file(_path_js__WEBPACK_IMPORTED_MODULE_2__/* .normalize */.Fv(originalFSPath(resource))).path:_path_js__WEBPACK_IMPORTED_MODULE_2__/* .posix */.KR.normalize(resource.path),resource.with({path:normalizedPath})}resolvePath(base,path){if(base.scheme===_network_js__WEBPACK_IMPORTED_MODULE_1__/* .Schemas */.lg.file){const newURI=_uri_js__WEBPACK_IMPORTED_MODULE_4__/* .URI */.o.file(_path_js__WEBPACK_IMPORTED_MODULE_2__/* .resolve */.DB(originalFSPath(base),path));return base.with({authority:newURI.authority,path:newURI.path})}// we allow path to be a windows path
return path=_extpath_js__WEBPACK_IMPORTED_MODULE_0__/* .toPosixPath */.fn(path),base.with({path:_path_js__WEBPACK_IMPORTED_MODULE_2__/* .posix */.KR.resolve(base.path,path)})}}
/**
 * Unbiased utility that takes uris "as they are". This means it can be interchanged with
 * uri#toString() usages. The following is true
 * ```
 * assertEqual(aUri.toString() === bUri.toString(), exturi.isEqual(aUri, bUri))
 * ```
 */const extUri=new ExtUri((()=>!1)),isEqual=extUri.isEqual.bind(extUri),basenameOrAuthority=extUri.basenameOrAuthority.bind(extUri),basename=extUri.basename.bind(extUri),dirname=extUri.dirname.bind(extUri),joinPath=extUri.joinPath.bind(extUri),normalizePath=extUri.normalizePath.bind(extUri),resolvePath=extUri.resolvePath.bind(extUri);
/**
 * Data URI related helpers.
 */
var DataUri;(function(DataUri){function parseMetaData(dataUri){const metadata=new Map,meta=dataUri.path.substring(dataUri.path.indexOf(";")+1,dataUri.path.lastIndexOf(";"));
// Given a URI of:  data:image/png;size:2313;label:SomeLabel;description:SomeDescription;base64,77+9UE5...
// the metadata is: size:2313;label:SomeLabel;description:SomeDescription
meta.split(";").forEach((property=>{const[key,value]=property.split(":");key&&value&&metadata.set(key,value)}));
// Given a URI of:  data:image/png;size:2313;label:SomeLabel;description:SomeDescription;base64,77+9UE5...
// the mime is: image/png
const mime=dataUri.path.substring(0,dataUri.path.indexOf(";"));return mime&&metadata.set(DataUri.META_DATA_MIME,mime),metadata}DataUri.META_DATA_LABEL="label",DataUri.META_DATA_DESCRIPTION="description",DataUri.META_DATA_SIZE="size",DataUri.META_DATA_MIME="mime",DataUri.parseMetaData=parseMetaData})(DataUri||(DataUri={}))},
/***/676633:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Rm:function(){/* binding */return Scrollable}
/* harmony export */});
/* unused harmony exports ScrollState, SmoothScrollingUpdate, SmoothScrollingOperation */
/* harmony import */var _event_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(104669),_lifecycle_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(905976);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
class ScrollState{constructor(width,scrollWidth,scrollLeft,height,scrollHeight,scrollTop){this._scrollStateBrand=void 0,width|=0,scrollWidth|=0,scrollLeft|=0,height|=0,scrollHeight|=0,scrollTop|=0,this.rawScrollLeft=scrollLeft,// before validation
this.rawScrollTop=scrollTop,// before validation
width<0&&(width=0),scrollLeft+width>scrollWidth&&(scrollLeft=scrollWidth-width),scrollLeft<0&&(scrollLeft=0),height<0&&(height=0),scrollTop+height>scrollHeight&&(scrollTop=scrollHeight-height),scrollTop<0&&(scrollTop=0),this.width=width,this.scrollWidth=scrollWidth,this.scrollLeft=scrollLeft,this.height=height,this.scrollHeight=scrollHeight,this.scrollTop=scrollTop}equals(other){return this.rawScrollLeft===other.rawScrollLeft&&this.rawScrollTop===other.rawScrollTop&&this.width===other.width&&this.scrollWidth===other.scrollWidth&&this.scrollLeft===other.scrollLeft&&this.height===other.height&&this.scrollHeight===other.scrollHeight&&this.scrollTop===other.scrollTop}withScrollDimensions(update,useRawScrollPositions){return new ScrollState("undefined"!==typeof update.width?update.width:this.width,"undefined"!==typeof update.scrollWidth?update.scrollWidth:this.scrollWidth,useRawScrollPositions?this.rawScrollLeft:this.scrollLeft,"undefined"!==typeof update.height?update.height:this.height,"undefined"!==typeof update.scrollHeight?update.scrollHeight:this.scrollHeight,useRawScrollPositions?this.rawScrollTop:this.scrollTop)}withScrollPosition(update){return new ScrollState(this.width,this.scrollWidth,"undefined"!==typeof update.scrollLeft?update.scrollLeft:this.rawScrollLeft,this.height,this.scrollHeight,"undefined"!==typeof update.scrollTop?update.scrollTop:this.rawScrollTop)}createScrollEvent(previous,inSmoothScrolling){const widthChanged=this.width!==previous.width,scrollWidthChanged=this.scrollWidth!==previous.scrollWidth,scrollLeftChanged=this.scrollLeft!==previous.scrollLeft,heightChanged=this.height!==previous.height,scrollHeightChanged=this.scrollHeight!==previous.scrollHeight,scrollTopChanged=this.scrollTop!==previous.scrollTop;return{inSmoothScrolling:inSmoothScrolling,oldWidth:previous.width,oldScrollWidth:previous.scrollWidth,oldScrollLeft:previous.scrollLeft,width:this.width,scrollWidth:this.scrollWidth,scrollLeft:this.scrollLeft,oldHeight:previous.height,oldScrollHeight:previous.scrollHeight,oldScrollTop:previous.scrollTop,height:this.height,scrollHeight:this.scrollHeight,scrollTop:this.scrollTop,widthChanged:widthChanged,scrollWidthChanged:scrollWidthChanged,scrollLeftChanged:scrollLeftChanged,heightChanged:heightChanged,scrollHeightChanged:scrollHeightChanged,scrollTopChanged:scrollTopChanged}}}class Scrollable extends _lifecycle_js__WEBPACK_IMPORTED_MODULE_1__/* .Disposable */.JT{constructor(smoothScrollDuration,scheduleAtNextAnimationFrame){super(),this._scrollableBrand=void 0,this._onScroll=this._register(new _event_js__WEBPACK_IMPORTED_MODULE_0__/* .Emitter */.Q5),this.onScroll=this._onScroll.event,this._smoothScrollDuration=smoothScrollDuration,this._scheduleAtNextAnimationFrame=scheduleAtNextAnimationFrame,this._state=new ScrollState(0,0,0,0,0,0),this._smoothScrolling=null}dispose(){this._smoothScrolling&&(this._smoothScrolling.dispose(),this._smoothScrolling=null),super.dispose()}setSmoothScrollDuration(smoothScrollDuration){this._smoothScrollDuration=smoothScrollDuration}validateScrollPosition(scrollPosition){return this._state.withScrollPosition(scrollPosition)}getScrollDimensions(){return this._state}setScrollDimensions(dimensions,useRawScrollPositions){const newState=this._state.withScrollDimensions(dimensions,useRawScrollPositions);this._setState(newState,Boolean(this._smoothScrolling)),
// Validate outstanding animated scroll position target
this._smoothScrolling&&this._smoothScrolling.acceptScrollDimensions(this._state)}
/**
     * Returns the final scroll position that the instance will have once the smooth scroll animation concludes.
     * If no scroll animation is occurring, it will return the current scroll position instead.
     */getFutureScrollPosition(){return this._smoothScrolling?this._smoothScrolling.to:this._state}
/**
     * Returns the current scroll position.
     * Note: This result might be an intermediate scroll position, as there might be an ongoing smooth scroll animation.
     */getCurrentScrollPosition(){return this._state}setScrollPositionNow(update){
// no smooth scrolling requested
const newState=this._state.withScrollPosition(update);
// Terminate any outstanding smooth scrolling
this._smoothScrolling&&(this._smoothScrolling.dispose(),this._smoothScrolling=null),this._setState(newState,!1)}setScrollPositionSmooth(update,reuseAnimation){if(0===this._smoothScrollDuration)
// Smooth scrolling not supported.
return this.setScrollPositionNow(update);if(this._smoothScrolling){
// Combine our pending scrollLeft/scrollTop with incoming scrollLeft/scrollTop
update={scrollLeft:"undefined"===typeof update.scrollLeft?this._smoothScrolling.to.scrollLeft:update.scrollLeft,scrollTop:"undefined"===typeof update.scrollTop?this._smoothScrolling.to.scrollTop:update.scrollTop};
// Validate `update`
const validTarget=this._state.withScrollPosition(update);if(this._smoothScrolling.to.scrollLeft===validTarget.scrollLeft&&this._smoothScrolling.to.scrollTop===validTarget.scrollTop)
// No need to interrupt or extend the current animation since we're going to the same place
return;let newSmoothScrolling;newSmoothScrolling=reuseAnimation?new SmoothScrollingOperation(this._smoothScrolling.from,validTarget,this._smoothScrolling.startTime,this._smoothScrolling.duration):this._smoothScrolling.combine(this._state,validTarget,this._smoothScrollDuration),this._smoothScrolling.dispose(),this._smoothScrolling=newSmoothScrolling}else{
// Validate `update`
const validTarget=this._state.withScrollPosition(update);this._smoothScrolling=SmoothScrollingOperation.start(this._state,validTarget,this._smoothScrollDuration)}
// Begin smooth scrolling animation
this._smoothScrolling.animationFrameDisposable=this._scheduleAtNextAnimationFrame((()=>{this._smoothScrolling&&(this._smoothScrolling.animationFrameDisposable=null,this._performSmoothScrolling())}))}_performSmoothScrolling(){if(!this._smoothScrolling)return;const update=this._smoothScrolling.tick(),newState=this._state.withScrollPosition(update);return this._setState(newState,!0),this._smoothScrolling?update.isDone?(this._smoothScrolling.dispose(),void(this._smoothScrolling=null)):
// Continue smooth scrolling animation
void(this._smoothScrolling.animationFrameDisposable=this._scheduleAtNextAnimationFrame((()=>{this._smoothScrolling&&(this._smoothScrolling.animationFrameDisposable=null,this._performSmoothScrolling())}))):void 0}_setState(newState,inSmoothScrolling){const oldState=this._state;oldState.equals(newState)||(this._state=newState,this._onScroll.fire(this._state.createScrollEvent(oldState,inSmoothScrolling)))}}class SmoothScrollingUpdate{constructor(scrollLeft,scrollTop,isDone){this.scrollLeft=scrollLeft,this.scrollTop=scrollTop,this.isDone=isDone}}function createEaseOutCubic(from,to){const delta=to-from;return function(completion){return from+delta*easeOutCubic(completion)}}function createComposed(a,b,cut){return function(completion){return completion<cut?a(completion/cut):b((completion-cut)/(1-cut))}}class SmoothScrollingOperation{constructor(from,to,startTime,duration){this.from=from,this.to=to,this.duration=duration,this.startTime=startTime,this.animationFrameDisposable=null,this._initAnimations()}_initAnimations(){this.scrollLeft=this._initAnimation(this.from.scrollLeft,this.to.scrollLeft,this.to.width),this.scrollTop=this._initAnimation(this.from.scrollTop,this.to.scrollTop,this.to.height)}_initAnimation(from,to,viewportSize){const delta=Math.abs(from-to);if(delta>2.5*viewportSize){let stop1,stop2;return from<to?(
// scroll to 75% of the viewportSize
stop1=from+.75*viewportSize,stop2=to-.75*viewportSize):(stop1=from-.75*viewportSize,stop2=to+.75*viewportSize),createComposed(createEaseOutCubic(from,stop1),createEaseOutCubic(stop2,to),.33)}return createEaseOutCubic(from,to)}dispose(){null!==this.animationFrameDisposable&&(this.animationFrameDisposable.dispose(),this.animationFrameDisposable=null)}acceptScrollDimensions(state){this.to=state.withScrollPosition(this.to),this._initAnimations()}tick(){return this._tick(Date.now())}_tick(now){const completion=(now-this.startTime)/this.duration;if(completion<1){const newScrollLeft=this.scrollLeft(completion),newScrollTop=this.scrollTop(completion);return new SmoothScrollingUpdate(newScrollLeft,newScrollTop,!1)}return new SmoothScrollingUpdate(this.to.scrollLeft,this.to.scrollTop,!0)}combine(from,to,duration){return SmoothScrollingOperation.start(from,to,duration)}static start(from,to,duration){
// +10 / -10 : pretend the animation already started for a quicker response to a scroll request
duration+=10;const startTime=Date.now()-10;return new SmoothScrollingOperation(from,to,startTime,duration)}}function easeInCubic(t){return Math.pow(t,3)}function easeOutCubic(t){return 1-easeInCubic(1-t)}
/***/},
/***/529415:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return buildReplaceStringWithCasePreserved}
/* harmony export */});
/* harmony import */var _strings_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(697295);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/function buildReplaceStringWithCasePreserved(matches,pattern){if(matches&&""!==matches[0]){const containsHyphens=validateSpecificSpecialCharacter(matches,pattern,"-"),containsUnderscores=validateSpecificSpecialCharacter(matches,pattern,"_");return containsHyphens&&!containsUnderscores?buildReplaceStringForSpecificSpecialCharacter(matches,pattern,"-"):!containsHyphens&&containsUnderscores?buildReplaceStringForSpecificSpecialCharacter(matches,pattern,"_"):matches[0].toUpperCase()===matches[0]?pattern.toUpperCase():matches[0].toLowerCase()===matches[0]?pattern.toLowerCase():_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .containsUppercaseCharacter */.Kw(matches[0][0])&&pattern.length>0?pattern[0].toUpperCase()+pattern.substr(1):matches[0][0].toUpperCase()!==matches[0][0]&&pattern.length>0?pattern[0].toLowerCase()+pattern.substr(1):pattern}return pattern}function validateSpecificSpecialCharacter(matches,pattern,specialCharacter){const doesContainSpecialCharacter=-1!==matches[0].indexOf(specialCharacter)&&-1!==pattern.indexOf(specialCharacter);return doesContainSpecialCharacter&&matches[0].split(specialCharacter).length===pattern.split(specialCharacter).length}function buildReplaceStringForSpecificSpecialCharacter(matches,pattern,specialCharacter){const splitPatternAtSpecialCharacter=pattern.split(specialCharacter),splitMatchAtSpecialCharacter=matches[0].split(specialCharacter);let replaceString="";return splitPatternAtSpecialCharacter.forEach(((splitValue,index)=>{replaceString+=buildReplaceStringWithCasePreserved([splitMatchAtSpecialCharacter[index]],splitValue)+specialCharacter})),replaceString.slice(0,-1)}
/***/},
/***/14603:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony import */var Severity,_strings_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(697295);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/(function(Severity){Severity[Severity["Ignore"]=0]="Ignore",Severity[Severity["Info"]=1]="Info",Severity[Severity["Warning"]=2]="Warning",Severity[Severity["Error"]=3]="Error"})(Severity||(Severity={})),function(Severity){const _error="error",_warning="warning",_warn="warn",_info="info",_ignore="ignore";
/**
     * Parses 'error', 'warning', 'warn', 'info' in call casings
     * and falls back to ignore.
     */
function fromValue(value){return value?_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .equalsIgnoreCase */.qq(_error,value)?Severity.Error:_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .equalsIgnoreCase */.qq(_warning,value)||_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .equalsIgnoreCase */.qq(_warn,value)?Severity.Warning:_strings_js__WEBPACK_IMPORTED_MODULE_0__/* .equalsIgnoreCase */.qq(_info,value)?Severity.Info:Severity.Ignore:Severity.Ignore}function toString(severity){switch(severity){case Severity.Error:return _error;case Severity.Warning:return _warning;case Severity.Info:return _info;default:return _ignore}}Severity.fromValue=fromValue,Severity.toString=toString}(Severity||(Severity={})),
/* harmony default export */__webpack_exports__.Z=Severity},
/***/84013:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */G:function(){/* binding */return StopWatch}
/* harmony export */});
/* harmony import */var _platform_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(901432);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/const hasPerformanceNow=_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.performance&&"function"===typeof _platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.performance.now;class StopWatch{constructor(highResolution){this._highResolution=hasPerformanceNow&&highResolution,this._startTime=this._now(),this._stopTime=-1}static create(highResolution=!0){return new StopWatch(highResolution)}stop(){this._stopTime=this._now()}elapsed(){return-1!==this._stopTime?this._stopTime-this._startTime:this._now()-this._startTime}_now(){return this._highResolution?_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.performance.now():Date.now()}}
/***/},
/***/697295:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function isFalsyOrWhitespace(str){return!str||"string"!==typeof str||0===str.trim().length}
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$i:function(){/* binding */return isBasicASCII},
/* harmony export */C8:function(){/* binding */return isEmojiImprecise},
/* harmony export */GF:function(){/* binding */return createRegExp},
/* harmony export */HO:function(){/* binding */return prevCharLength},
/* harmony export */IO:function(){/* binding */return regExpLeadsToEndlessLoop},
/* harmony export */K7:function(){/* binding */return isFullWidthCharacter},
/* harmony export */Kw:function(){/* binding */return containsUppercaseCharacter},
/* harmony export */LC:function(){/* binding */return firstNonWhitespaceIndex},
/* harmony export */Mh:function(){/* binding */return commonPrefixLength},
/* harmony export */P1:function(){/* binding */return commonSuffixLength},
/* harmony export */PJ:function(){/* binding */return singleLetterHash},
/* harmony export */Qe:function(){/* binding */return UNUSUAL_LINE_TERMINATORS},
/* harmony export */R1:function(){/* binding */return stripWildcards},
/* harmony export */RP:function(){/* binding */return containsEmoji},
/* harmony export */S6:function(){/* binding */return getGraphemeBreakType},
/* harmony export */TT:function(){/* binding */return compareSubstring},
/* harmony export */Ut:function(){/* binding */return containsRTL},
/* harmony export */V8:function(){/* binding */return getLeadingWhitespace},
/* harmony export */WU:function(){/* binding */return format},
/* harmony export */YK:function(){/* binding */return isLowSurrogate},
/* harmony export */YU:function(){/* binding */return escape},
/* harmony export */ZG:function(){/* binding */return isHighSurrogate},
/* harmony export */ZH:function(){/* binding */return getNextCodePoint},
/* harmony export */ab:function(){/* binding */return containsUnusualLineTerminators},
/* harmony export */c1:function(){/* binding */return UTF8_BOM_CHARACTER},
/* harmony export */df:function(){/* binding */return isUpperAsciiLetter},
/* harmony export */ec:function(){/* binding */return escapeRegExpCharacters},
/* harmony export */fi:function(){/* binding */return breakBetweenGraphemeBreakType},
/* harmony export */fy:function(){/* binding */return trim},
/* harmony export */j3:function(){/* binding */return ltrim},
/* harmony export */j_:function(){/* binding */return compareSubstringIgnoreCase},
/* harmony export */m5:function(){/* binding */return isFalsyOrWhitespace},
/* harmony export */mK:function(){/* binding */return isLowerAsciiLetter},
/* harmony export */mr:function(){/* binding */return regExpFlags},
/* harmony export */oH:function(){/* binding */return getLeftDeleteOffset},
/* harmony export */oL:function(){/* binding */return rtrim},
/* harmony export */ok:function(){/* binding */return startsWithIgnoreCase},
/* harmony export */ow:function(){/* binding */return lastNonWhitespaceIndex},
/* harmony export */qq:function(){/* binding */return equalsIgnoreCase},
/* harmony export */qu:function(){/* binding */return compare},
/* harmony export */rL:function(){/* binding */return computeCodePoint},
/* harmony export */uS:function(){/* binding */return startsWithUTF8BOM},
/* harmony export */un:function(){/* binding */return convertSimple2RegExpPattern},
/* harmony export */uq:function(){/* binding */return splitLines},
/* harmony export */vH:function(){/* binding */return nextCharLength},
/* harmony export */xe:function(){/* binding */return containsFullWidthCharacter},
/* harmony export */zY:function(){/* binding */return compareIgnoreCase}
/* harmony export */});const _formatRegexp=/{(\d+)}/g;
/**
 * Helper to produce a string with a variable number of arguments. Insert variable segments
 * into the string using the {n} notation where N is the index of the argument following the string.
 * @param value string to which formatting is applied
 * @param args replacements for {n}-entries
 */function format(value,...args){return 0===args.length?value:value.replace(_formatRegexp,(function(match,group){const idx=parseInt(group,10);return isNaN(idx)||idx<0||idx>=args.length?match:args[idx]}))}
/**
 * Converts HTML characters inside the string to use entities instead. Makes the string safe from
 * being used e.g. in HTMLElement.innerHTML.
 */function escape(html){return html.replace(/[<>&]/g,(function(match){switch(match){case"<":return"&lt;";case">":return"&gt;";case"&":return"&amp;";default:return match}}))}
/**
 * Escapes regular expression characters in a given string
 */function escapeRegExpCharacters(value){return value.replace(/[\\\{\}\*\+\?\|\^\$\.\[\]\(\)]/g,"\\$&")}
/**
 * Removes all occurrences of needle from the beginning and end of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim (default is a blank)
 */function trim(haystack,needle=" "){const trimmed=ltrim(haystack,needle);return rtrim(trimmed,needle)}
/**
 * Removes all occurrences of needle from the beginning of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim
 */function ltrim(haystack,needle){if(!haystack||!needle)return haystack;const needleLen=needle.length;if(0===needleLen||0===haystack.length)return haystack;let offset=0;while(haystack.indexOf(needle,offset)===offset)offset+=needleLen;return haystack.substring(offset)}
/**
 * Removes all occurrences of needle from the end of haystack.
 * @param haystack string to trim
 * @param needle the thing to trim
 */function rtrim(haystack,needle){if(!haystack||!needle)return haystack;const needleLen=needle.length,haystackLen=haystack.length;if(0===needleLen||0===haystackLen)return haystack;let offset=haystackLen,idx=-1;while(1){if(idx=haystack.lastIndexOf(needle,offset-1),-1===idx||idx+needleLen!==offset)break;if(0===idx)return"";offset=idx}return haystack.substring(0,offset)}function convertSimple2RegExpPattern(pattern){return pattern.replace(/[\-\\\{\}\+\?\|\^\$\.\,\[\]\(\)\#\s]/g,"\\$&").replace(/[\*]/g,".*")}function stripWildcards(pattern){return pattern.replace(/\*/g,"")}function createRegExp(searchString,isRegex,options={}){if(!searchString)throw new Error("Cannot create regex from empty string");isRegex||(searchString=escapeRegExpCharacters(searchString)),options.wholeWord&&(/\B/.test(searchString.charAt(0))||(searchString="\\b"+searchString),/\B/.test(searchString.charAt(searchString.length-1))||(searchString+="\\b"));let modifiers="";return options.global&&(modifiers+="g"),options.matchCase||(modifiers+="i"),options.multiline&&(modifiers+="m"),options.unicode&&(modifiers+="u"),new RegExp(searchString,modifiers)}function regExpLeadsToEndlessLoop(regexp){
// Exit early if it's one of these special cases which are meant to match
// against an empty string
if("^"===regexp.source||"^$"===regexp.source||"$"===regexp.source||"^\\s*$"===regexp.source)return!1;
// We check against an empty string. If the regular expression doesn't advance
// (e.g. ends in an endless loop) it will match an empty string.
const match=regexp.exec("");return!(!match||0!==regexp.lastIndex)}function regExpFlags(regexp){return(regexp.global?"g":"")+(regexp.ignoreCase?"i":"")+(regexp.multiline?"m":"")+(regexp/* standalone editor compilation */.unicode?"u":"")}function splitLines(str){return str.split(/\r\n|\r|\n/)}
/**
 * Returns first index of the string that is not whitespace.
 * If string is empty or contains only whitespaces, returns -1
 */function firstNonWhitespaceIndex(str){for(let i=0,len=str.length;i<len;i++){const chCode=str.charCodeAt(i);if(32/* Space */!==chCode&&9/* Tab */!==chCode)return i}return-1}
/**
 * Returns the leading whitespace of the string.
 * If the string contains only whitespaces, returns entire string
 */function getLeadingWhitespace(str,start=0,end=str.length){for(let i=start;i<end;i++){const chCode=str.charCodeAt(i);if(32/* Space */!==chCode&&9/* Tab */!==chCode)return str.substring(start,i)}return str.substring(start,end)}
/**
 * Returns last index of the string that is not whitespace.
 * If string is empty or contains only whitespaces, returns -1
 */function lastNonWhitespaceIndex(str,startIndex=str.length-1){for(let i=startIndex;i>=0;i--){const chCode=str.charCodeAt(i);if(32/* Space */!==chCode&&9/* Tab */!==chCode)return i}return-1}function compare(a,b){return a<b?-1:a>b?1:0}function compareSubstring(a,b,aStart=0,aEnd=a.length,bStart=0,bEnd=b.length){for(;aStart<aEnd&&bStart<bEnd;aStart++,bStart++){let codeA=a.charCodeAt(aStart),codeB=b.charCodeAt(bStart);if(codeA<codeB)return-1;if(codeA>codeB)return 1}const aLen=aEnd-aStart,bLen=bEnd-bStart;return aLen<bLen?-1:aLen>bLen?1:0}function compareIgnoreCase(a,b){return compareSubstringIgnoreCase(a,b,0,a.length,0,b.length)}function compareSubstringIgnoreCase(a,b,aStart=0,aEnd=a.length,bStart=0,bEnd=b.length){for(;aStart<aEnd&&bStart<bEnd;aStart++,bStart++){let codeA=a.charCodeAt(aStart),codeB=b.charCodeAt(bStart);if(codeA===codeB)
// equal
continue;if(codeA>=128||codeB>=128)
// not ASCII letters -> fallback to lower-casing strings
return compareSubstring(a.toLowerCase(),b.toLowerCase(),aStart,aEnd,bStart,bEnd);
// mapper lower-case ascii letter onto upper-case varinats
// [97-122] (lower ascii) --> [65-90] (upper ascii)
isLowerAsciiLetter(codeA)&&(codeA-=32),isLowerAsciiLetter(codeB)&&(codeB-=32);
// compare both code points
const diff=codeA-codeB;if(0!==diff)return diff}const aLen=aEnd-aStart,bLen=bEnd-bStart;return aLen<bLen?-1:aLen>bLen?1:0}function isLowerAsciiLetter(code){return code>=97/* a */&&code<=122/* z */}function isUpperAsciiLetter(code){return code>=65/* A */&&code<=90/* Z */}function equalsIgnoreCase(a,b){return a.length===b.length&&0===compareSubstringIgnoreCase(a,b)}function startsWithIgnoreCase(str,candidate){const candidateLength=candidate.length;return!(candidate.length>str.length)&&0===compareSubstringIgnoreCase(str,candidate,0,candidateLength)}
/**
 * @returns the length of the common prefix of the two strings.
 */function commonPrefixLength(a,b){let i,len=Math.min(a.length,b.length);for(i=0;i<len;i++)if(a.charCodeAt(i)!==b.charCodeAt(i))return i;return len}
/**
 * @returns the length of the common suffix of the two strings.
 */function commonSuffixLength(a,b){let i,len=Math.min(a.length,b.length);const aLastIndex=a.length-1,bLastIndex=b.length-1;for(i=0;i<len;i++)if(a.charCodeAt(aLastIndex-i)!==b.charCodeAt(bLastIndex-i))return i;return len}
/**
 * See http://en.wikipedia.org/wiki/Surrogate_pair
 */function isHighSurrogate(charCode){return 55296<=charCode&&charCode<=56319}
/**
 * See http://en.wikipedia.org/wiki/Surrogate_pair
 */function isLowSurrogate(charCode){return 56320<=charCode&&charCode<=57343}
/**
 * See http://en.wikipedia.org/wiki/Surrogate_pair
 */function computeCodePoint(highSurrogate,lowSurrogate){return lowSurrogate-56320+(highSurrogate-55296<<10)+65536}
/**
 * get the code point that begins at offset `offset`
 */function getNextCodePoint(str,len,offset){const charCode=str.charCodeAt(offset);if(isHighSurrogate(charCode)&&offset+1<len){const nextCharCode=str.charCodeAt(offset+1);if(isLowSurrogate(nextCharCode))return computeCodePoint(charCode,nextCharCode)}return charCode}
/**
 * get the code point that ends right before offset `offset`
 */function getPrevCodePoint(str,offset){const charCode=str.charCodeAt(offset-1);if(isLowSurrogate(charCode)&&offset>1){const prevCharCode=str.charCodeAt(offset-2);if(isHighSurrogate(prevCharCode))return computeCodePoint(prevCharCode,charCode)}return charCode}function nextCharLength(str,offset){const graphemeBreakTree=GraphemeBreakTree.getInstance(),initialOffset=offset,len=str.length,initialCodePoint=getNextCodePoint(str,len,offset);offset+=initialCodePoint>=65536/* UNICODE_SUPPLEMENTARY_PLANE_BEGIN */?2:1;let graphemeBreakType=graphemeBreakTree.getGraphemeBreakType(initialCodePoint);while(offset<len){const nextCodePoint=getNextCodePoint(str,len,offset),nextGraphemeBreakType=graphemeBreakTree.getGraphemeBreakType(nextCodePoint);if(breakBetweenGraphemeBreakType(graphemeBreakType,nextGraphemeBreakType))break;offset+=nextCodePoint>=65536/* UNICODE_SUPPLEMENTARY_PLANE_BEGIN */?2:1,graphemeBreakType=nextGraphemeBreakType}return offset-initialOffset}function prevCharLength(str,offset){const graphemeBreakTree=GraphemeBreakTree.getInstance(),initialOffset=offset,initialCodePoint=getPrevCodePoint(str,offset);offset-=initialCodePoint>=65536/* UNICODE_SUPPLEMENTARY_PLANE_BEGIN */?2:1;let graphemeBreakType=graphemeBreakTree.getGraphemeBreakType(initialCodePoint);while(offset>0){const prevCodePoint=getPrevCodePoint(str,offset),prevGraphemeBreakType=graphemeBreakTree.getGraphemeBreakType(prevCodePoint);if(breakBetweenGraphemeBreakType(prevGraphemeBreakType,graphemeBreakType))break;offset-=prevCodePoint>=65536/* UNICODE_SUPPLEMENTARY_PLANE_BEGIN */?2:1,graphemeBreakType=prevGraphemeBreakType}return initialOffset-offset}
/**
 * Generated using https://github.com/alexdima/unicode-utils/blob/master/generate-rtl-test.js
 */const CONTAINS_RTL=/(?:[\u05BE\u05C0\u05C3\u05C6\u05D0-\u05F4\u0608\u060B\u060D\u061B-\u064A\u066D-\u066F\u0671-\u06D5\u06E5\u06E6\u06EE\u06EF\u06FA-\u0710\u0712-\u072F\u074D-\u07A5\u07B1-\u07EA\u07F4\u07F5\u07FA-\u0815\u081A\u0824\u0828\u0830-\u0858\u085E-\u08BD\u200F\uFB1D\uFB1F-\uFB28\uFB2A-\uFD3D\uFD50-\uFDFC\uFE70-\uFEFC]|\uD802[\uDC00-\uDD1B\uDD20-\uDE00\uDE10-\uDE33\uDE40-\uDEE4\uDEEB-\uDF35\uDF40-\uDFFF]|\uD803[\uDC00-\uDCFF]|\uD83A[\uDC00-\uDCCF\uDD00-\uDD43\uDD50-\uDFFF]|\uD83B[\uDC00-\uDEBB])/;
/**
 * Returns true if `str` contains any Unicode character that is classified as "R" or "AL".
 */function containsRTL(str){return CONTAINS_RTL.test(str)}
/**
 * Generated using https://github.com/alexdima/unicode-utils/blob/master/generate-emoji-test.js
 */const CONTAINS_EMOJI=/(?:[\u231A\u231B\u23F0\u23F3\u2600-\u27BF\u2B50\u2B55]|\uD83C[\uDDE6-\uDDFF\uDF00-\uDFFF]|\uD83D[\uDC00-\uDE4F\uDE80-\uDEFC\uDFE0-\uDFEB]|\uD83E[\uDD00-\uDDFF\uDE70-\uDED6])/;function containsEmoji(str){return CONTAINS_EMOJI.test(str)}const IS_BASIC_ASCII=/^[\t\n\r\x20-\x7E]*$/;
/**
 * Returns true if `str` contains only basic ASCII characters in the range 32 - 126 (including 32 and 126) or \n, \r, \t
 */function isBasicASCII(str){return IS_BASIC_ASCII.test(str)}const UNUSUAL_LINE_TERMINATORS=/[\u2028\u2029]/;// LINE SEPARATOR (LS) or PARAGRAPH SEPARATOR (PS)
/**
 * Returns true if `str` contains unusual line terminators, like LS or PS
 */function containsUnusualLineTerminators(str){return UNUSUAL_LINE_TERMINATORS.test(str)}function containsFullWidthCharacter(str){for(let i=0,len=str.length;i<len;i++)if(isFullWidthCharacter(str.charCodeAt(i)))return!0;return!1}function isFullWidthCharacter(charCode){// @perf
// Do a cheap trick to better support wrapping of wide characters, treat them as 2 columns
// http://jrgraphix.net/research/unicode_blocks.php
//          2E80 — 2EFF   CJK Radicals Supplement
//          2F00 — 2FDF   Kangxi Radicals
//          2FF0 — 2FFF   Ideographic Description Characters
//          3000 — 303F   CJK Symbols and Punctuation
//          3040 — 309F   Hiragana
//          30A0 — 30FF   Katakana
//          3100 — 312F   Bopomofo
//          3130 — 318F   Hangul Compatibility Jamo
//          3190 — 319F   Kanbun
//          31A0 — 31BF   Bopomofo Extended
//          31F0 — 31FF   Katakana Phonetic Extensions
//          3200 — 32FF   Enclosed CJK Letters and Months
//          3300 — 33FF   CJK Compatibility
//          3400 — 4DBF   CJK Unified Ideographs Extension A
//          4DC0 — 4DFF   Yijing Hexagram Symbols
//          4E00 — 9FFF   CJK Unified Ideographs
//          A000 — A48F   Yi Syllables
//          A490 — A4CF   Yi Radicals
//          AC00 — D7AF   Hangul Syllables
// [IGNORE] D800 — DB7F   High Surrogates
// [IGNORE] DB80 — DBFF   High Private Use Surrogates
// [IGNORE] DC00 — DFFF   Low Surrogates
// [IGNORE] E000 — F8FF   Private Use Area
//          F900 — FAFF   CJK Compatibility Ideographs
// [IGNORE] FB00 — FB4F   Alphabetic Presentation Forms
// [IGNORE] FB50 — FDFF   Arabic Presentation Forms-A
// [IGNORE] FE00 — FE0F   Variation Selectors
// [IGNORE] FE20 — FE2F   Combining Half Marks
// [IGNORE] FE30 — FE4F   CJK Compatibility Forms
// [IGNORE] FE50 — FE6F   Small Form Variants
// [IGNORE] FE70 — FEFF   Arabic Presentation Forms-B
//          FF00 — FFEF   Halfwidth and Fullwidth Forms
//               [https://en.wikipedia.org/wiki/Halfwidth_and_fullwidth_forms]
//               of which FF01 - FF5E fullwidth ASCII of 21 to 7E
// [IGNORE]    and FF65 - FFDC halfwidth of Katakana and Hangul
// [IGNORE] FFF0 — FFFF   Specials
return charCode=+charCode,charCode>=11904&&charCode<=55215||charCode>=63744&&charCode<=64255||charCode>=65281&&charCode<=65374}
/**
 * A fast function (therefore imprecise) to check if code points are emojis.
 * Generated using https://github.com/alexdima/unicode-utils/blob/master/generate-emoji-test.js
 */function isEmojiImprecise(x){return x>=127462&&x<=127487||8986===x||8987===x||9200===x||9203===x||x>=9728&&x<=10175||11088===x||11093===x||x>=127744&&x<=128591||x>=128640&&x<=128764||x>=128992&&x<=129003||x>=129280&&x<=129535||x>=129648&&x<=129750}
// -- UTF-8 BOM
const UTF8_BOM_CHARACTER=String.fromCharCode(65279/* UTF8_BOM */);function startsWithUTF8BOM(str){return!!(str&&str.length>0&&65279/* UTF8_BOM */===str.charCodeAt(0))}function containsUppercaseCharacter(target,ignoreEscapedChars=!1){return!!target&&(ignoreEscapedChars&&(target=target.replace(/\\./g,"")),target.toLowerCase()!==target)}
/**
 * Produces 'a'-'z', followed by 'A'-'Z'... followed by 'a'-'z', etc.
 */function singleLetterHash(n){const LETTERS_CNT=26;return n%=2*LETTERS_CNT,n<LETTERS_CNT?String.fromCharCode(97/* a */+n):String.fromCharCode(65/* A */+n-LETTERS_CNT)}
//#region Unicode Grapheme Break
function getGraphemeBreakType(codePoint){const graphemeBreakTree=GraphemeBreakTree.getInstance();return graphemeBreakTree.getGraphemeBreakType(codePoint)}function breakBetweenGraphemeBreakType(breakTypeA,breakTypeB){
// http://www.unicode.org/reports/tr29/#Grapheme_Cluster_Boundary_Rules
// !!! Let's make the common case a bit faster
return 0/* Other */===breakTypeA?5/* Extend */!==breakTypeB&&7/* SpacingMark */!==breakTypeB:
// Do not break between a CR and LF. Otherwise, break before and after controls.
// GB3                                        CR × LF
// GB4                       (Control | CR | LF) ÷
// GB5                                           ÷ (Control | CR | LF)
(2/* CR */!==breakTypeA||3/* LF */!==breakTypeB)&&(4/* Control */===breakTypeA||2/* CR */===breakTypeA||3/* LF */===breakTypeA||(4/* Control */===breakTypeB||2/* CR */===breakTypeB||3/* LF */===breakTypeB||
// Do not break Hangul syllable sequences.
// GB6                                         L × (L | V | LV | LVT)
// GB7                                  (LV | V) × (V | T)
// GB8                                 (LVT | T) × T
(8/* L */!==breakTypeA||8/* L */!==breakTypeB&&9/* V */!==breakTypeB&&11/* LV */!==breakTypeB&&12/* LVT */!==breakTypeB)&&((11/* LV */!==breakTypeA&&9/* V */!==breakTypeA||9/* V */!==breakTypeB&&10/* T */!==breakTypeB)&&((12/* LVT */!==breakTypeA&&10/* T */!==breakTypeA||10/* T */!==breakTypeB)&&(
// Do not break before extending characters or ZWJ.
// GB9                                           × (Extend | ZWJ)
5/* Extend */!==breakTypeB&&13/* ZWJ */!==breakTypeB&&(
// The GB9a and GB9b rules only apply to extended grapheme clusters:
// Do not break before SpacingMarks, or after Prepend characters.
// GB9a                                          × SpacingMark
// GB9b                                  Prepend ×
7/* SpacingMark */!==breakTypeB&&(1/* Prepend */!==breakTypeA&&(
// Do not break within emoji modifier sequences or emoji zwj sequences.
// GB11    \p{Extended_Pictographic} Extend* ZWJ × \p{Extended_Pictographic}
(13/* ZWJ */!==breakTypeA||14/* Extended_Pictographic */!==breakTypeB)&&(6/* Regional_Indicator */!==breakTypeA||6/* Regional_Indicator */!==breakTypeB)))))))))}class GraphemeBreakTree{constructor(){this._data=getGraphemeBreakRawData()}static getInstance(){return GraphemeBreakTree._INSTANCE||(GraphemeBreakTree._INSTANCE=new GraphemeBreakTree),GraphemeBreakTree._INSTANCE}getGraphemeBreakType(codePoint){
// !!! Let's make 7bit ASCII a bit faster: 0..31
if(codePoint<32)return 10/* LineFeed */===codePoint?3/* LF */:13/* CarriageReturn */===codePoint?2/* CR */:4/* Control */;
// !!! Let's make 7bit ASCII a bit faster: 32..126
if(codePoint<127)return 0/* Other */;const data=this._data,nodeCount=data.length/3;let nodeIndex=1;while(nodeIndex<=nodeCount)if(codePoint<data[3*nodeIndex])
// go left
nodeIndex*=2;else{if(!(codePoint>data[3*nodeIndex+1]))
// hit
return data[3*nodeIndex+2];
// go right
nodeIndex=2*nodeIndex+1}return 0/* Other */}}function getGraphemeBreakRawData(){
// generated using https://github.com/alexdima/unicode-utils/blob/master/generate-grapheme-break.js
return JSON.parse("[0,0,0,51592,51592,11,44424,44424,11,72251,72254,5,7150,7150,7,48008,48008,11,55176,55176,11,128420,128420,14,3276,3277,5,9979,9980,14,46216,46216,11,49800,49800,11,53384,53384,11,70726,70726,5,122915,122916,5,129320,129327,14,2558,2558,5,5906,5908,5,9762,9763,14,43360,43388,8,45320,45320,11,47112,47112,11,48904,48904,11,50696,50696,11,52488,52488,11,54280,54280,11,70082,70083,1,71350,71350,7,73111,73111,5,127892,127893,14,128726,128727,14,129473,129474,14,2027,2035,5,2901,2902,5,3784,3789,5,6754,6754,5,8418,8420,5,9877,9877,14,11088,11088,14,44008,44008,5,44872,44872,11,45768,45768,11,46664,46664,11,47560,47560,11,48456,48456,11,49352,49352,11,50248,50248,11,51144,51144,11,52040,52040,11,52936,52936,11,53832,53832,11,54728,54728,11,69811,69814,5,70459,70460,5,71096,71099,7,71998,71998,5,72874,72880,5,119149,119149,7,127374,127374,14,128335,128335,14,128482,128482,14,128765,128767,14,129399,129400,14,129680,129685,14,1476,1477,5,2377,2380,7,2759,2760,5,3137,3140,7,3458,3459,7,4153,4154,5,6432,6434,5,6978,6978,5,7675,7679,5,9723,9726,14,9823,9823,14,9919,9923,14,10035,10036,14,42736,42737,5,43596,43596,5,44200,44200,11,44648,44648,11,45096,45096,11,45544,45544,11,45992,45992,11,46440,46440,11,46888,46888,11,47336,47336,11,47784,47784,11,48232,48232,11,48680,48680,11,49128,49128,11,49576,49576,11,50024,50024,11,50472,50472,11,50920,50920,11,51368,51368,11,51816,51816,11,52264,52264,11,52712,52712,11,53160,53160,11,53608,53608,11,54056,54056,11,54504,54504,11,54952,54952,11,68108,68111,5,69933,69940,5,70197,70197,7,70498,70499,7,70845,70845,5,71229,71229,5,71727,71735,5,72154,72155,5,72344,72345,5,73023,73029,5,94095,94098,5,121403,121452,5,126981,127182,14,127538,127546,14,127990,127990,14,128391,128391,14,128445,128449,14,128500,128505,14,128752,128752,14,129160,129167,14,129356,129356,14,129432,129442,14,129648,129651,14,129751,131069,14,173,173,4,1757,1757,1,2274,2274,1,2494,2494,5,2641,2641,5,2876,2876,5,3014,3016,7,3262,3262,7,3393,3396,5,3570,3571,7,3968,3972,5,4228,4228,7,6086,6086,5,6679,6680,5,6912,6915,5,7080,7081,5,7380,7392,5,8252,8252,14,9096,9096,14,9748,9749,14,9784,9786,14,9833,9850,14,9890,9894,14,9938,9938,14,9999,9999,14,10085,10087,14,12349,12349,14,43136,43137,7,43454,43456,7,43755,43755,7,44088,44088,11,44312,44312,11,44536,44536,11,44760,44760,11,44984,44984,11,45208,45208,11,45432,45432,11,45656,45656,11,45880,45880,11,46104,46104,11,46328,46328,11,46552,46552,11,46776,46776,11,47000,47000,11,47224,47224,11,47448,47448,11,47672,47672,11,47896,47896,11,48120,48120,11,48344,48344,11,48568,48568,11,48792,48792,11,49016,49016,11,49240,49240,11,49464,49464,11,49688,49688,11,49912,49912,11,50136,50136,11,50360,50360,11,50584,50584,11,50808,50808,11,51032,51032,11,51256,51256,11,51480,51480,11,51704,51704,11,51928,51928,11,52152,52152,11,52376,52376,11,52600,52600,11,52824,52824,11,53048,53048,11,53272,53272,11,53496,53496,11,53720,53720,11,53944,53944,11,54168,54168,11,54392,54392,11,54616,54616,11,54840,54840,11,55064,55064,11,65438,65439,5,69633,69633,5,69837,69837,1,70018,70018,7,70188,70190,7,70368,70370,7,70465,70468,7,70712,70719,5,70835,70840,5,70850,70851,5,71132,71133,5,71340,71340,7,71458,71461,5,71985,71989,7,72002,72002,7,72193,72202,5,72281,72283,5,72766,72766,7,72885,72886,5,73104,73105,5,92912,92916,5,113824,113827,4,119173,119179,5,121505,121519,5,125136,125142,5,127279,127279,14,127489,127490,14,127570,127743,14,127900,127901,14,128254,128254,14,128369,128370,14,128400,128400,14,128425,128432,14,128468,128475,14,128489,128494,14,128715,128720,14,128745,128745,14,128759,128760,14,129004,129023,14,129296,129304,14,129340,129342,14,129388,129392,14,129404,129407,14,129454,129455,14,129485,129487,14,129659,129663,14,129719,129727,14,917536,917631,5,13,13,2,1160,1161,5,1564,1564,4,1807,1807,1,2085,2087,5,2363,2363,7,2402,2403,5,2507,2508,7,2622,2624,7,2691,2691,7,2786,2787,5,2881,2884,5,3006,3006,5,3072,3072,5,3170,3171,5,3267,3268,7,3330,3331,7,3406,3406,1,3538,3540,5,3655,3662,5,3897,3897,5,4038,4038,5,4184,4185,5,4352,4447,8,6068,6069,5,6155,6157,5,6448,6449,7,6742,6742,5,6783,6783,5,6966,6970,5,7042,7042,7,7143,7143,7,7212,7219,5,7412,7412,5,8206,8207,4,8294,8303,4,8596,8601,14,9410,9410,14,9742,9742,14,9757,9757,14,9770,9770,14,9794,9794,14,9828,9828,14,9855,9855,14,9882,9882,14,9900,9903,14,9929,9933,14,9963,9967,14,9987,9988,14,10006,10006,14,10062,10062,14,10175,10175,14,11744,11775,5,42607,42607,5,43043,43044,7,43263,43263,5,43444,43445,7,43569,43570,5,43698,43700,5,43766,43766,5,44032,44032,11,44144,44144,11,44256,44256,11,44368,44368,11,44480,44480,11,44592,44592,11,44704,44704,11,44816,44816,11,44928,44928,11,45040,45040,11,45152,45152,11,45264,45264,11,45376,45376,11,45488,45488,11,45600,45600,11,45712,45712,11,45824,45824,11,45936,45936,11,46048,46048,11,46160,46160,11,46272,46272,11,46384,46384,11,46496,46496,11,46608,46608,11,46720,46720,11,46832,46832,11,46944,46944,11,47056,47056,11,47168,47168,11,47280,47280,11,47392,47392,11,47504,47504,11,47616,47616,11,47728,47728,11,47840,47840,11,47952,47952,11,48064,48064,11,48176,48176,11,48288,48288,11,48400,48400,11,48512,48512,11,48624,48624,11,48736,48736,11,48848,48848,11,48960,48960,11,49072,49072,11,49184,49184,11,49296,49296,11,49408,49408,11,49520,49520,11,49632,49632,11,49744,49744,11,49856,49856,11,49968,49968,11,50080,50080,11,50192,50192,11,50304,50304,11,50416,50416,11,50528,50528,11,50640,50640,11,50752,50752,11,50864,50864,11,50976,50976,11,51088,51088,11,51200,51200,11,51312,51312,11,51424,51424,11,51536,51536,11,51648,51648,11,51760,51760,11,51872,51872,11,51984,51984,11,52096,52096,11,52208,52208,11,52320,52320,11,52432,52432,11,52544,52544,11,52656,52656,11,52768,52768,11,52880,52880,11,52992,52992,11,53104,53104,11,53216,53216,11,53328,53328,11,53440,53440,11,53552,53552,11,53664,53664,11,53776,53776,11,53888,53888,11,54000,54000,11,54112,54112,11,54224,54224,11,54336,54336,11,54448,54448,11,54560,54560,11,54672,54672,11,54784,54784,11,54896,54896,11,55008,55008,11,55120,55120,11,64286,64286,5,66272,66272,5,68900,68903,5,69762,69762,7,69817,69818,5,69927,69931,5,70003,70003,5,70070,70078,5,70094,70094,7,70194,70195,7,70206,70206,5,70400,70401,5,70463,70463,7,70475,70477,7,70512,70516,5,70722,70724,5,70832,70832,5,70842,70842,5,70847,70848,5,71088,71089,7,71102,71102,7,71219,71226,5,71231,71232,5,71342,71343,7,71453,71455,5,71463,71467,5,71737,71738,5,71995,71996,5,72000,72000,7,72145,72147,7,72160,72160,5,72249,72249,7,72273,72278,5,72330,72342,5,72752,72758,5,72850,72871,5,72882,72883,5,73018,73018,5,73031,73031,5,73109,73109,5,73461,73462,7,94031,94031,5,94192,94193,7,119142,119142,7,119155,119162,4,119362,119364,5,121476,121476,5,122888,122904,5,123184,123190,5,126976,126979,14,127184,127231,14,127344,127345,14,127405,127461,14,127514,127514,14,127561,127567,14,127778,127779,14,127896,127896,14,127985,127986,14,127995,127999,5,128326,128328,14,128360,128366,14,128378,128378,14,128394,128397,14,128405,128406,14,128422,128423,14,128435,128443,14,128453,128464,14,128479,128480,14,128484,128487,14,128496,128498,14,128640,128709,14,128723,128724,14,128736,128741,14,128747,128748,14,128755,128755,14,128762,128762,14,128981,128991,14,129096,129103,14,129292,129292,14,129311,129311,14,129329,129330,14,129344,129349,14,129360,129374,14,129394,129394,14,129402,129402,14,129413,129425,14,129445,129450,14,129466,129471,14,129483,129483,14,129511,129535,14,129653,129655,14,129667,129670,14,129705,129711,14,129731,129743,14,917505,917505,4,917760,917999,5,10,10,3,127,159,4,768,879,5,1471,1471,5,1536,1541,1,1648,1648,5,1767,1768,5,1840,1866,5,2070,2073,5,2137,2139,5,2307,2307,7,2366,2368,7,2382,2383,7,2434,2435,7,2497,2500,5,2519,2519,5,2563,2563,7,2631,2632,5,2677,2677,5,2750,2752,7,2763,2764,7,2817,2817,5,2879,2879,5,2891,2892,7,2914,2915,5,3008,3008,5,3021,3021,5,3076,3076,5,3146,3149,5,3202,3203,7,3264,3265,7,3271,3272,7,3298,3299,5,3390,3390,5,3402,3404,7,3426,3427,5,3535,3535,5,3544,3550,7,3635,3635,7,3763,3763,7,3893,3893,5,3953,3966,5,3981,3991,5,4145,4145,7,4157,4158,5,4209,4212,5,4237,4237,5,4520,4607,10,5970,5971,5,6071,6077,5,6089,6099,5,6277,6278,5,6439,6440,5,6451,6456,7,6683,6683,5,6744,6750,5,6765,6770,7,6846,6846,5,6964,6964,5,6972,6972,5,7019,7027,5,7074,7077,5,7083,7085,5,7146,7148,7,7154,7155,7,7222,7223,5,7394,7400,5,7416,7417,5,8204,8204,5,8233,8233,4,8288,8292,4,8413,8416,5,8482,8482,14,8986,8987,14,9193,9203,14,9654,9654,14,9733,9733,14,9745,9745,14,9752,9752,14,9760,9760,14,9766,9766,14,9774,9775,14,9792,9792,14,9800,9811,14,9825,9826,14,9831,9831,14,9852,9853,14,9872,9873,14,9880,9880,14,9885,9887,14,9896,9897,14,9906,9916,14,9926,9927,14,9936,9936,14,9941,9960,14,9974,9974,14,9982,9985,14,9992,9997,14,10002,10002,14,10017,10017,14,10055,10055,14,10071,10071,14,10145,10145,14,11013,11015,14,11503,11505,5,12334,12335,5,12951,12951,14,42612,42621,5,43014,43014,5,43047,43047,7,43204,43205,5,43335,43345,5,43395,43395,7,43450,43451,7,43561,43566,5,43573,43574,5,43644,43644,5,43710,43711,5,43758,43759,7,44005,44005,5,44012,44012,7,44060,44060,11,44116,44116,11,44172,44172,11,44228,44228,11,44284,44284,11,44340,44340,11,44396,44396,11,44452,44452,11,44508,44508,11,44564,44564,11,44620,44620,11,44676,44676,11,44732,44732,11,44788,44788,11,44844,44844,11,44900,44900,11,44956,44956,11,45012,45012,11,45068,45068,11,45124,45124,11,45180,45180,11,45236,45236,11,45292,45292,11,45348,45348,11,45404,45404,11,45460,45460,11,45516,45516,11,45572,45572,11,45628,45628,11,45684,45684,11,45740,45740,11,45796,45796,11,45852,45852,11,45908,45908,11,45964,45964,11,46020,46020,11,46076,46076,11,46132,46132,11,46188,46188,11,46244,46244,11,46300,46300,11,46356,46356,11,46412,46412,11,46468,46468,11,46524,46524,11,46580,46580,11,46636,46636,11,46692,46692,11,46748,46748,11,46804,46804,11,46860,46860,11,46916,46916,11,46972,46972,11,47028,47028,11,47084,47084,11,47140,47140,11,47196,47196,11,47252,47252,11,47308,47308,11,47364,47364,11,47420,47420,11,47476,47476,11,47532,47532,11,47588,47588,11,47644,47644,11,47700,47700,11,47756,47756,11,47812,47812,11,47868,47868,11,47924,47924,11,47980,47980,11,48036,48036,11,48092,48092,11,48148,48148,11,48204,48204,11,48260,48260,11,48316,48316,11,48372,48372,11,48428,48428,11,48484,48484,11,48540,48540,11,48596,48596,11,48652,48652,11,48708,48708,11,48764,48764,11,48820,48820,11,48876,48876,11,48932,48932,11,48988,48988,11,49044,49044,11,49100,49100,11,49156,49156,11,49212,49212,11,49268,49268,11,49324,49324,11,49380,49380,11,49436,49436,11,49492,49492,11,49548,49548,11,49604,49604,11,49660,49660,11,49716,49716,11,49772,49772,11,49828,49828,11,49884,49884,11,49940,49940,11,49996,49996,11,50052,50052,11,50108,50108,11,50164,50164,11,50220,50220,11,50276,50276,11,50332,50332,11,50388,50388,11,50444,50444,11,50500,50500,11,50556,50556,11,50612,50612,11,50668,50668,11,50724,50724,11,50780,50780,11,50836,50836,11,50892,50892,11,50948,50948,11,51004,51004,11,51060,51060,11,51116,51116,11,51172,51172,11,51228,51228,11,51284,51284,11,51340,51340,11,51396,51396,11,51452,51452,11,51508,51508,11,51564,51564,11,51620,51620,11,51676,51676,11,51732,51732,11,51788,51788,11,51844,51844,11,51900,51900,11,51956,51956,11,52012,52012,11,52068,52068,11,52124,52124,11,52180,52180,11,52236,52236,11,52292,52292,11,52348,52348,11,52404,52404,11,52460,52460,11,52516,52516,11,52572,52572,11,52628,52628,11,52684,52684,11,52740,52740,11,52796,52796,11,52852,52852,11,52908,52908,11,52964,52964,11,53020,53020,11,53076,53076,11,53132,53132,11,53188,53188,11,53244,53244,11,53300,53300,11,53356,53356,11,53412,53412,11,53468,53468,11,53524,53524,11,53580,53580,11,53636,53636,11,53692,53692,11,53748,53748,11,53804,53804,11,53860,53860,11,53916,53916,11,53972,53972,11,54028,54028,11,54084,54084,11,54140,54140,11,54196,54196,11,54252,54252,11,54308,54308,11,54364,54364,11,54420,54420,11,54476,54476,11,54532,54532,11,54588,54588,11,54644,54644,11,54700,54700,11,54756,54756,11,54812,54812,11,54868,54868,11,54924,54924,11,54980,54980,11,55036,55036,11,55092,55092,11,55148,55148,11,55216,55238,9,65056,65071,5,65529,65531,4,68097,68099,5,68159,68159,5,69446,69456,5,69688,69702,5,69808,69810,7,69815,69816,7,69821,69821,1,69888,69890,5,69932,69932,7,69957,69958,7,70016,70017,5,70067,70069,7,70079,70080,7,70089,70092,5,70095,70095,5,70191,70193,5,70196,70196,5,70198,70199,5,70367,70367,5,70371,70378,5,70402,70403,7,70462,70462,5,70464,70464,5,70471,70472,7,70487,70487,5,70502,70508,5,70709,70711,7,70720,70721,7,70725,70725,7,70750,70750,5,70833,70834,7,70841,70841,7,70843,70844,7,70846,70846,7,70849,70849,7,71087,71087,5,71090,71093,5,71100,71101,5,71103,71104,5,71216,71218,7,71227,71228,7,71230,71230,7,71339,71339,5,71341,71341,5,71344,71349,5,71351,71351,5,71456,71457,7,71462,71462,7,71724,71726,7,71736,71736,7,71984,71984,5,71991,71992,7,71997,71997,7,71999,71999,1,72001,72001,1,72003,72003,5,72148,72151,5,72156,72159,7,72164,72164,7,72243,72248,5,72250,72250,1,72263,72263,5,72279,72280,7,72324,72329,1,72343,72343,7,72751,72751,7,72760,72765,5,72767,72767,5,72873,72873,7,72881,72881,7,72884,72884,7,73009,73014,5,73020,73021,5,73030,73030,1,73098,73102,7,73107,73108,7,73110,73110,7,73459,73460,5,78896,78904,4,92976,92982,5,94033,94087,7,94180,94180,5,113821,113822,5,119141,119141,5,119143,119145,5,119150,119154,5,119163,119170,5,119210,119213,5,121344,121398,5,121461,121461,5,121499,121503,5,122880,122886,5,122907,122913,5,122918,122922,5,123628,123631,5,125252,125258,5,126980,126980,14,127183,127183,14,127245,127247,14,127340,127343,14,127358,127359,14,127377,127386,14,127462,127487,6,127491,127503,14,127535,127535,14,127548,127551,14,127568,127569,14,127744,127777,14,127780,127891,14,127894,127895,14,127897,127899,14,127902,127984,14,127987,127989,14,127991,127994,14,128000,128253,14,128255,128317,14,128329,128334,14,128336,128359,14,128367,128368,14,128371,128377,14,128379,128390,14,128392,128393,14,128398,128399,14,128401,128404,14,128407,128419,14,128421,128421,14,128424,128424,14,128433,128434,14,128444,128444,14,128450,128452,14,128465,128467,14,128476,128478,14,128481,128481,14,128483,128483,14,128488,128488,14,128495,128495,14,128499,128499,14,128506,128591,14,128710,128714,14,128721,128722,14,128725,128725,14,128728,128735,14,128742,128744,14,128746,128746,14,128749,128751,14,128753,128754,14,128756,128758,14,128761,128761,14,128763,128764,14,128884,128895,14,128992,129003,14,129036,129039,14,129114,129119,14,129198,129279,14,129293,129295,14,129305,129310,14,129312,129319,14,129328,129328,14,129331,129338,14,129343,129343,14,129351,129355,14,129357,129359,14,129375,129387,14,129393,129393,14,129395,129398,14,129401,129401,14,129403,129403,14,129408,129412,14,129426,129431,14,129443,129444,14,129451,129453,14,129456,129465,14,129472,129472,14,129475,129482,14,129484,129484,14,129488,129510,14,129536,129647,14,129652,129652,14,129656,129658,14,129664,129666,14,129671,129679,14,129686,129704,14,129712,129718,14,129728,129730,14,129744,129750,14,917504,917504,4,917506,917535,4,917632,917759,4,918000,921599,4,0,9,4,11,12,4,14,31,4,169,169,14,174,174,14,1155,1159,5,1425,1469,5,1473,1474,5,1479,1479,5,1552,1562,5,1611,1631,5,1750,1756,5,1759,1764,5,1770,1773,5,1809,1809,5,1958,1968,5,2045,2045,5,2075,2083,5,2089,2093,5,2259,2273,5,2275,2306,5,2362,2362,5,2364,2364,5,2369,2376,5,2381,2381,5,2385,2391,5,2433,2433,5,2492,2492,5,2495,2496,7,2503,2504,7,2509,2509,5,2530,2531,5,2561,2562,5,2620,2620,5,2625,2626,5,2635,2637,5,2672,2673,5,2689,2690,5,2748,2748,5,2753,2757,5,2761,2761,7,2765,2765,5,2810,2815,5,2818,2819,7,2878,2878,5,2880,2880,7,2887,2888,7,2893,2893,5,2903,2903,5,2946,2946,5,3007,3007,7,3009,3010,7,3018,3020,7,3031,3031,5,3073,3075,7,3134,3136,5,3142,3144,5,3157,3158,5,3201,3201,5,3260,3260,5,3263,3263,5,3266,3266,5,3270,3270,5,3274,3275,7,3285,3286,5,3328,3329,5,3387,3388,5,3391,3392,7,3398,3400,7,3405,3405,5,3415,3415,5,3457,3457,5,3530,3530,5,3536,3537,7,3542,3542,5,3551,3551,5,3633,3633,5,3636,3642,5,3761,3761,5,3764,3772,5,3864,3865,5,3895,3895,5,3902,3903,7,3967,3967,7,3974,3975,5,3993,4028,5,4141,4144,5,4146,4151,5,4155,4156,7,4182,4183,7,4190,4192,5,4226,4226,5,4229,4230,5,4253,4253,5,4448,4519,9,4957,4959,5,5938,5940,5,6002,6003,5,6070,6070,7,6078,6085,7,6087,6088,7,6109,6109,5,6158,6158,4,6313,6313,5,6435,6438,7,6441,6443,7,6450,6450,5,6457,6459,5,6681,6682,7,6741,6741,7,6743,6743,7,6752,6752,5,6757,6764,5,6771,6780,5,6832,6845,5,6847,6848,5,6916,6916,7,6965,6965,5,6971,6971,7,6973,6977,7,6979,6980,7,7040,7041,5,7073,7073,7,7078,7079,7,7082,7082,7,7142,7142,5,7144,7145,5,7149,7149,5,7151,7153,5,7204,7211,7,7220,7221,7,7376,7378,5,7393,7393,7,7405,7405,5,7415,7415,7,7616,7673,5,8203,8203,4,8205,8205,13,8232,8232,4,8234,8238,4,8265,8265,14,8293,8293,4,8400,8412,5,8417,8417,5,8421,8432,5,8505,8505,14,8617,8618,14,9000,9000,14,9167,9167,14,9208,9210,14,9642,9643,14,9664,9664,14,9728,9732,14,9735,9741,14,9743,9744,14,9746,9746,14,9750,9751,14,9753,9756,14,9758,9759,14,9761,9761,14,9764,9765,14,9767,9769,14,9771,9773,14,9776,9783,14,9787,9791,14,9793,9793,14,9795,9799,14,9812,9822,14,9824,9824,14,9827,9827,14,9829,9830,14,9832,9832,14,9851,9851,14,9854,9854,14,9856,9861,14,9874,9876,14,9878,9879,14,9881,9881,14,9883,9884,14,9888,9889,14,9895,9895,14,9898,9899,14,9904,9905,14,9917,9918,14,9924,9925,14,9928,9928,14,9934,9935,14,9937,9937,14,9939,9940,14,9961,9962,14,9968,9973,14,9975,9978,14,9981,9981,14,9986,9986,14,9989,9989,14,9998,9998,14,10000,10001,14,10004,10004,14,10013,10013,14,10024,10024,14,10052,10052,14,10060,10060,14,10067,10069,14,10083,10084,14,10133,10135,14,10160,10160,14,10548,10549,14,11035,11036,14,11093,11093,14,11647,11647,5,12330,12333,5,12336,12336,14,12441,12442,5,12953,12953,14,42608,42610,5,42654,42655,5,43010,43010,5,43019,43019,5,43045,43046,5,43052,43052,5,43188,43203,7,43232,43249,5,43302,43309,5,43346,43347,7,43392,43394,5,43443,43443,5,43446,43449,5,43452,43453,5,43493,43493,5,43567,43568,7,43571,43572,7,43587,43587,5,43597,43597,7,43696,43696,5,43703,43704,5,43713,43713,5,43756,43757,5,43765,43765,7,44003,44004,7,44006,44007,7,44009,44010,7,44013,44013,5,44033,44059,12,44061,44087,12,44089,44115,12,44117,44143,12,44145,44171,12,44173,44199,12,44201,44227,12,44229,44255,12,44257,44283,12,44285,44311,12,44313,44339,12,44341,44367,12,44369,44395,12,44397,44423,12,44425,44451,12,44453,44479,12,44481,44507,12,44509,44535,12,44537,44563,12,44565,44591,12,44593,44619,12,44621,44647,12,44649,44675,12,44677,44703,12,44705,44731,12,44733,44759,12,44761,44787,12,44789,44815,12,44817,44843,12,44845,44871,12,44873,44899,12,44901,44927,12,44929,44955,12,44957,44983,12,44985,45011,12,45013,45039,12,45041,45067,12,45069,45095,12,45097,45123,12,45125,45151,12,45153,45179,12,45181,45207,12,45209,45235,12,45237,45263,12,45265,45291,12,45293,45319,12,45321,45347,12,45349,45375,12,45377,45403,12,45405,45431,12,45433,45459,12,45461,45487,12,45489,45515,12,45517,45543,12,45545,45571,12,45573,45599,12,45601,45627,12,45629,45655,12,45657,45683,12,45685,45711,12,45713,45739,12,45741,45767,12,45769,45795,12,45797,45823,12,45825,45851,12,45853,45879,12,45881,45907,12,45909,45935,12,45937,45963,12,45965,45991,12,45993,46019,12,46021,46047,12,46049,46075,12,46077,46103,12,46105,46131,12,46133,46159,12,46161,46187,12,46189,46215,12,46217,46243,12,46245,46271,12,46273,46299,12,46301,46327,12,46329,46355,12,46357,46383,12,46385,46411,12,46413,46439,12,46441,46467,12,46469,46495,12,46497,46523,12,46525,46551,12,46553,46579,12,46581,46607,12,46609,46635,12,46637,46663,12,46665,46691,12,46693,46719,12,46721,46747,12,46749,46775,12,46777,46803,12,46805,46831,12,46833,46859,12,46861,46887,12,46889,46915,12,46917,46943,12,46945,46971,12,46973,46999,12,47001,47027,12,47029,47055,12,47057,47083,12,47085,47111,12,47113,47139,12,47141,47167,12,47169,47195,12,47197,47223,12,47225,47251,12,47253,47279,12,47281,47307,12,47309,47335,12,47337,47363,12,47365,47391,12,47393,47419,12,47421,47447,12,47449,47475,12,47477,47503,12,47505,47531,12,47533,47559,12,47561,47587,12,47589,47615,12,47617,47643,12,47645,47671,12,47673,47699,12,47701,47727,12,47729,47755,12,47757,47783,12,47785,47811,12,47813,47839,12,47841,47867,12,47869,47895,12,47897,47923,12,47925,47951,12,47953,47979,12,47981,48007,12,48009,48035,12,48037,48063,12,48065,48091,12,48093,48119,12,48121,48147,12,48149,48175,12,48177,48203,12,48205,48231,12,48233,48259,12,48261,48287,12,48289,48315,12,48317,48343,12,48345,48371,12,48373,48399,12,48401,48427,12,48429,48455,12,48457,48483,12,48485,48511,12,48513,48539,12,48541,48567,12,48569,48595,12,48597,48623,12,48625,48651,12,48653,48679,12,48681,48707,12,48709,48735,12,48737,48763,12,48765,48791,12,48793,48819,12,48821,48847,12,48849,48875,12,48877,48903,12,48905,48931,12,48933,48959,12,48961,48987,12,48989,49015,12,49017,49043,12,49045,49071,12,49073,49099,12,49101,49127,12,49129,49155,12,49157,49183,12,49185,49211,12,49213,49239,12,49241,49267,12,49269,49295,12,49297,49323,12,49325,49351,12,49353,49379,12,49381,49407,12,49409,49435,12,49437,49463,12,49465,49491,12,49493,49519,12,49521,49547,12,49549,49575,12,49577,49603,12,49605,49631,12,49633,49659,12,49661,49687,12,49689,49715,12,49717,49743,12,49745,49771,12,49773,49799,12,49801,49827,12,49829,49855,12,49857,49883,12,49885,49911,12,49913,49939,12,49941,49967,12,49969,49995,12,49997,50023,12,50025,50051,12,50053,50079,12,50081,50107,12,50109,50135,12,50137,50163,12,50165,50191,12,50193,50219,12,50221,50247,12,50249,50275,12,50277,50303,12,50305,50331,12,50333,50359,12,50361,50387,12,50389,50415,12,50417,50443,12,50445,50471,12,50473,50499,12,50501,50527,12,50529,50555,12,50557,50583,12,50585,50611,12,50613,50639,12,50641,50667,12,50669,50695,12,50697,50723,12,50725,50751,12,50753,50779,12,50781,50807,12,50809,50835,12,50837,50863,12,50865,50891,12,50893,50919,12,50921,50947,12,50949,50975,12,50977,51003,12,51005,51031,12,51033,51059,12,51061,51087,12,51089,51115,12,51117,51143,12,51145,51171,12,51173,51199,12,51201,51227,12,51229,51255,12,51257,51283,12,51285,51311,12,51313,51339,12,51341,51367,12,51369,51395,12,51397,51423,12,51425,51451,12,51453,51479,12,51481,51507,12,51509,51535,12,51537,51563,12,51565,51591,12,51593,51619,12,51621,51647,12,51649,51675,12,51677,51703,12,51705,51731,12,51733,51759,12,51761,51787,12,51789,51815,12,51817,51843,12,51845,51871,12,51873,51899,12,51901,51927,12,51929,51955,12,51957,51983,12,51985,52011,12,52013,52039,12,52041,52067,12,52069,52095,12,52097,52123,12,52125,52151,12,52153,52179,12,52181,52207,12,52209,52235,12,52237,52263,12,52265,52291,12,52293,52319,12,52321,52347,12,52349,52375,12,52377,52403,12,52405,52431,12,52433,52459,12,52461,52487,12,52489,52515,12,52517,52543,12,52545,52571,12,52573,52599,12,52601,52627,12,52629,52655,12,52657,52683,12,52685,52711,12,52713,52739,12,52741,52767,12,52769,52795,12,52797,52823,12,52825,52851,12,52853,52879,12,52881,52907,12,52909,52935,12,52937,52963,12,52965,52991,12,52993,53019,12,53021,53047,12,53049,53075,12,53077,53103,12,53105,53131,12,53133,53159,12,53161,53187,12,53189,53215,12,53217,53243,12,53245,53271,12,53273,53299,12,53301,53327,12,53329,53355,12,53357,53383,12,53385,53411,12,53413,53439,12,53441,53467,12,53469,53495,12,53497,53523,12,53525,53551,12,53553,53579,12,53581,53607,12,53609,53635,12,53637,53663,12,53665,53691,12,53693,53719,12,53721,53747,12,53749,53775,12,53777,53803,12,53805,53831,12,53833,53859,12,53861,53887,12,53889,53915,12,53917,53943,12,53945,53971,12,53973,53999,12,54001,54027,12,54029,54055,12,54057,54083,12,54085,54111,12,54113,54139,12,54141,54167,12,54169,54195,12,54197,54223,12,54225,54251,12,54253,54279,12,54281,54307,12,54309,54335,12,54337,54363,12,54365,54391,12,54393,54419,12,54421,54447,12,54449,54475,12,54477,54503,12,54505,54531,12,54533,54559,12,54561,54587,12,54589,54615,12,54617,54643,12,54645,54671,12,54673,54699,12,54701,54727,12,54729,54755,12,54757,54783,12,54785,54811,12,54813,54839,12,54841,54867,12,54869,54895,12,54897,54923,12,54925,54951,12,54953,54979,12,54981,55007,12,55009,55035,12,55037,55063,12,55065,55091,12,55093,55119,12,55121,55147,12,55149,55175,12,55177,55203,12,55243,55291,10,65024,65039,5,65279,65279,4,65520,65528,4,66045,66045,5,66422,66426,5,68101,68102,5,68152,68154,5,68325,68326,5,69291,69292,5,69632,69632,7,69634,69634,7,69759,69761,5]")}
//#endregion
/**
 * Computes the offset after performing a left delete on the given string,
 * while considering unicode grapheme/emoji rules.
*/function getLeftDeleteOffset(offset,str){if(0===offset)return 0;
// Try to delete emoji part.
const emojiOffset=getOffsetBeforeLastEmojiComponent(offset,str);if(void 0!==emojiOffset)return emojiOffset;
// Otherwise, just skip a single code point.
const codePoint=getPrevCodePoint(str,offset);return offset-=getUTF16Length(codePoint),offset}function getOffsetBeforeLastEmojiComponent(offset,str){
// See https://www.unicode.org/reports/tr51/tr51-14.html#EBNF_and_Regex for the
// structure of emojis.
let codePoint=getPrevCodePoint(str,offset);offset-=getUTF16Length(codePoint);
// Skip modifiers
while(isEmojiModifier(codePoint)||65039/* emojiVariantSelector */===codePoint||8419/* enclosingKeyCap */===codePoint){if(0===offset)
// Cannot skip modifier, no preceding emoji base.
return;codePoint=getPrevCodePoint(str,offset),offset-=getUTF16Length(codePoint)}
// Expect base emoji
if(isEmojiImprecise(codePoint)){if(offset>=0){
// Skip optional ZWJ code points that combine multiple emojis.
// In theory, we should check if that ZWJ actually combines multiple emojis
// to prevent deleting ZWJs in situations we didn't account for.
const optionalZwjCodePoint=getPrevCodePoint(str,offset);8205/* zwj */===optionalZwjCodePoint&&(offset-=getUTF16Length(optionalZwjCodePoint))}return offset}}function getUTF16Length(codePoint){return codePoint>=65536/* UNICODE_SUPPLEMENTARY_PLANE_BEGIN */?2:1}function isEmojiModifier(codePoint){return 127995<=codePoint&&codePoint<=127999}
/***/GraphemeBreakTree._INSTANCE=null},
/***/998401:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* unused harmony exports validateConstraint, getAllPropertyNames */
/**
 * @returns whether the provided parameter is a JavaScript Array or not.
 */
function isArray(array){return Array.isArray(array)}
/**
 * @returns whether the provided parameter is a JavaScript String or not.
 */function isString(str){return"string"===typeof str}
/**
 *
 * @returns whether the provided parameter is of type `object` but **not**
 *	`null`, an `array`, a `regexp`, nor a `date`.
 */function isObject(obj){
// The method can't do a type cast since there are type (like strings) which
// are subclasses of any put not positvely matched by the function. Hence type
// narrowing results in wrong results.
return"object"===typeof obj&&null!==obj&&!Array.isArray(obj)&&!(obj instanceof RegExp)&&!(obj instanceof Date)}
/**
 * In **contrast** to just checking `typeof` this will return `false` for `NaN`.
 * @returns whether the provided parameter is a JavaScript Number or not.
 */function isNumber(obj){return"number"===typeof obj&&!isNaN(obj)}
/**
 * @returns whether the provided parameter is a JavaScript Boolean or not.
 */function isBoolean(obj){return!0===obj||!1===obj}
/**
 * @returns whether the provided parameter is undefined.
 */function isUndefined(obj){return"undefined"===typeof obj}
/**
 * @returns whether the provided parameter is defined.
 */function isDefined(arg){return!isUndefinedOrNull(arg)}
/**
 * @returns whether the provided parameter is undefined or null.
 */function isUndefinedOrNull(obj){return isUndefined(obj)||null===obj}function assertType(condition,type){if(!condition)throw new Error(type?`Unexpected type, expected '${type}'`:"Unexpected type")}
/**
 * Asserts that the argument passed in is neither undefined nor null.
 */function assertIsDefined(arg){if(isUndefinedOrNull(arg))throw new Error("Assertion Failed: argument is undefined or null");return arg}
/**
 * @returns whether the provided parameter is a JavaScript Function or not.
 */function isFunction(obj){return"function"===typeof obj}function validateConstraints(args,constraints){const len=Math.min(args.length,constraints.length);for(let i=0;i<len;i++)validateConstraint(args[i],constraints[i])}function validateConstraint(arg,constraint){if(isString(constraint)){if(typeof arg!==constraint)throw new Error(`argument does not match constraint: typeof ${constraint}`)}else if(isFunction(constraint)){try{if(arg instanceof constraint)return}catch(_a){
// ignore
}if(!isUndefinedOrNull(arg)&&arg.constructor===constraint)return;if(1===constraint.length&&!0===constraint.call(void 0,arg))return;throw new Error("argument does not match one of these constraints: arg instanceof constraint, arg.constructor === constraint, nor constraint(arg) === true")}}function getAllPropertyNames(obj){let res=[],proto=Object.getPrototypeOf(obj);while(Object.prototype!==proto)res=res.concat(Object.getOwnPropertyNames(proto)),proto=Object.getPrototypeOf(proto);return res}function getAllMethodNames(obj){const methods=[];for(const prop of getAllPropertyNames(obj))"function"===typeof obj[prop]&&methods.push(prop);return methods}function createProxyObject(methodNames,invoke){const createProxyMethod=method=>function(){const args=Array.prototype.slice.call(arguments,0);return invoke(method,args)};let result={};for(const methodName of methodNames)result[methodName]=createProxyMethod(methodName);return result}
/**
 * Converts null to undefined, passes all other values through.
 */function withNullAsUndefined(x){return null===x?void 0:x}function assertNever(value,message="Unreachable"){throw new Error(message)}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */$E:function(){/* binding */return getAllMethodNames},
/* harmony export */$K:function(){/* binding */return isDefined},
/* harmony export */D8:function(){/* binding */return validateConstraints},
/* harmony export */HD:function(){/* binding */return isString},
/* harmony export */IU:function(){/* binding */return createProxyObject},
/* harmony export */Jp:function(){/* binding */return isUndefinedOrNull},
/* harmony export */Kn:function(){/* binding */return isObject},
/* harmony export */cW:function(){/* binding */return assertIsDefined},
/* harmony export */f6:function(){/* binding */return withNullAsUndefined},
/* harmony export */hj:function(){/* binding */return isNumber},
/* harmony export */jn:function(){/* binding */return isBoolean},
/* harmony export */kJ:function(){/* binding */return isArray},
/* harmony export */mf:function(){/* binding */return isFunction},
/* harmony export */o8:function(){/* binding */return isUndefined},
/* harmony export */p_:function(){/* binding */return assertType},
/* harmony export */vE:function(){/* binding */return assertNever}
/* harmony export */})},
/***/385427:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
function toUint8(v){return v<0?0:v>255/* MAX_UINT_8 */?255/* MAX_UINT_8 */:0|v}function toUint32(v){return v<0?0:v>4294967295/* MAX_UINT_32 */?4294967295/* MAX_UINT_32 */:0|v}
/***/
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */A:function(){/* binding */return toUint32},
/* harmony export */K:function(){/* binding */return toUint8}
/* harmony export */})},
/***/70666:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */o:function(){/* binding */return URI},
/* harmony export */q:function(){/* binding */return uriToFsPath}
/* harmony export */});
/* harmony import */var _path_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(555336),_platform_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(901432);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const _schemePattern=/^\w[\w\d+.-]*$/,_singleSlashStart=/^\//,_doubleSlashStart=/^\/\//;function _validateUri(ret,_strict){
// scheme, must be set
if(!ret.scheme&&_strict)throw new Error(`[UriError]: Scheme is missing: {scheme: "", authority: "${ret.authority}", path: "${ret.path}", query: "${ret.query}", fragment: "${ret.fragment}"}`);
// scheme, https://tools.ietf.org/html/rfc3986#section-3.1
// ALPHA *( ALPHA / DIGIT / "+" / "-" / "." )
if(ret.scheme&&!_schemePattern.test(ret.scheme))throw new Error("[UriError]: Scheme contains illegal characters.");
// path, http://tools.ietf.org/html/rfc3986#section-3.3
// If a URI contains an authority component, then the path component
// must either be empty or begin with a slash ("/") character.  If a URI
// does not contain an authority component, then the path cannot begin
// with two slash characters ("//").
if(ret.path)if(ret.authority){if(!_singleSlashStart.test(ret.path))throw new Error('[UriError]: If a URI contains an authority component, then the path component must either be empty or begin with a slash ("/") character')}else if(_doubleSlashStart.test(ret.path))throw new Error('[UriError]: If a URI does not contain an authority component, then the path cannot begin with two slash characters ("//")')}
// for a while we allowed uris *without* schemes and this is the migration
// for them, e.g. an uri without scheme and without strict-mode warns and falls
// back to the file-scheme. that should cause the least carnage and still be a
// clear warning
function _schemeFix(scheme,_strict){return scheme||_strict?scheme:"file"}
// implements a bit of https://tools.ietf.org/html/rfc3986#section-5
function _referenceResolution(scheme,path){
// the slash-character is our 'default base' as we don't
// support constructing URIs relative to other URIs. This
// also means that we alter and potentially break paths.
// see https://tools.ietf.org/html/rfc3986#section-5.1.4
switch(scheme){case"https":case"http":case"file":path?path[0]!==_slash&&(path=_slash+path):path=_slash;break}return path}const _empty="",_slash="/",_regexp=/^(([^:/?#]+?):)?(\/\/([^/?#]*))?([^?#]*)(\?([^#]*))?(#(.*))?/;
/**
 * Uniform Resource Identifier (URI) http://tools.ietf.org/html/rfc3986.
 * This class is a simple parser which creates the basic component parts
 * (http://tools.ietf.org/html/rfc3986#section-3) with minimal validation
 * and encoding.
 *
 * ```txt
 *       foo://example.com:8042/over/there?name=ferret#nose
 *       \_/   \______________/\_________/ \_________/ \__/
 *        |           |            |            |        |
 *     scheme     authority       path        query   fragment
 *        |   _____________________|__
 *       / \ /                        \
 *       urn:example:animal:ferret:nose
 * ```
 */
class URI{
/**
     * @internal
     */
constructor(schemeOrData,authority,path,query,fragment,_strict=!1){"object"===typeof schemeOrData?(this.scheme=schemeOrData.scheme||_empty,this.authority=schemeOrData.authority||_empty,this.path=schemeOrData.path||_empty,this.query=schemeOrData.query||_empty,this.fragment=schemeOrData.fragment||_empty):(this.scheme=_schemeFix(schemeOrData,_strict),this.authority=authority||_empty,this.path=_referenceResolution(this.scheme,path||_empty),this.query=query||_empty,this.fragment=fragment||_empty,_validateUri(this,_strict))}static isUri(thing){return thing instanceof URI||!!thing&&("string"===typeof thing.authority&&"string"===typeof thing.fragment&&"string"===typeof thing.path&&"string"===typeof thing.query&&"string"===typeof thing.scheme&&"string"===typeof thing.fsPath&&"function"===typeof thing.with&&"function"===typeof thing.toString)}
// ---- filesystem path -----------------------
/**
     * Returns a string representing the corresponding file system path of this URI.
     * Will handle UNC paths, normalizes windows drive letters to lower-case, and uses the
     * platform specific path separator.
     *
     * * Will *not* validate the path for invalid characters and semantics.
     * * Will *not* look at the scheme of this URI.
     * * The result shall *not* be used for display purposes but for accessing a file on disk.
     *
     *
     * The *difference* to `URI#path` is the use of the platform specific separator and the handling
     * of UNC paths. See the below sample of a file-uri with an authority (UNC path).
     *
     * ```ts
        const u = URI.parse('file://server/c$/folder/file.txt')
        u.authority === 'server'
        u.path === '/shares/c$/file.txt'
        u.fsPath === '\\server\c$\folder\file.txt'
    ```
     *
     * Using `URI#path` to read a file (using fs-apis) would not be enough because parts of the path,
     * namely the server name, would be missing. Therefore `URI#fsPath` exists - it's sugar to ease working
     * with URIs that represent files on disk (`file` scheme).
     */
get fsPath(){
// if (this.scheme !== 'file') {
// 	console.warn(`[UriError] calling fsPath with scheme ${this.scheme}`);
// }
return uriToFsPath(this,!1)}
// ---- modify to new -------------------------
with(change){if(!change)return this;let{scheme:scheme,authority:authority,path:path,query:query,fragment:fragment}=change;return void 0===scheme?scheme=this.scheme:null===scheme&&(scheme=_empty),void 0===authority?authority=this.authority:null===authority&&(authority=_empty),void 0===path?path=this.path:null===path&&(path=_empty),void 0===query?query=this.query:null===query&&(query=_empty),void 0===fragment?fragment=this.fragment:null===fragment&&(fragment=_empty),scheme===this.scheme&&authority===this.authority&&path===this.path&&query===this.query&&fragment===this.fragment?this:new Uri(scheme,authority,path,query,fragment)}
// ---- parse & validate ------------------------
/**
     * Creates a new URI from a string, e.g. `http://www.msft.com/some/path`,
     * `file:///usr/home`, or `scheme:with/path`.
     *
     * @param value A string which represents an URI (see `URI#toString`).
     */
static parse(value,_strict=!1){const match=_regexp.exec(value);return match?new Uri(match[2]||_empty,percentDecode(match[4]||_empty),percentDecode(match[5]||_empty),percentDecode(match[7]||_empty),percentDecode(match[9]||_empty),_strict):new Uri(_empty,_empty,_empty,_empty,_empty)}
/**
     * Creates a new URI from a file system path, e.g. `c:\my\files`,
     * `/usr/home`, or `\\server\share\some\path`.
     *
     * The *difference* between `URI#parse` and `URI#file` is that the latter treats the argument
     * as path, not as stringified-uri. E.g. `URI.file(path)` is **not the same as**
     * `URI.parse('file://' + path)` because the path might contain characters that are
     * interpreted (# and ?). See the following sample:
     * ```ts
    const good = URI.file('/coding/c#/project1');
    good.scheme === 'file';
    good.path === '/coding/c#/project1';
    good.fragment === '';
    const bad = URI.parse('file://' + '/coding/c#/project1');
    bad.scheme === 'file';
    bad.path === '/coding/c'; // path is now broken
    bad.fragment === '/project1';
    ```
     *
     * @param path A file system path (see `URI#fsPath`)
     */static file(path){let authority=_empty;
// normalize to fwd-slashes on windows,
// on other systems bwd-slashes are valid
// filename character, eg /f\oo/ba\r.txt
// check for authority as used in UNC shares
// or use the path as given
if(_platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED&&(path=path.replace(/\\/g,_slash)),path[0]===_slash&&path[1]===_slash){const idx=path.indexOf(_slash,2);-1===idx?(authority=path.substring(2),path=_slash):(authority=path.substring(2,idx),path=path.substring(idx)||_slash)}return new Uri("file",authority,path,_empty,_empty)}static from(components){const result=new Uri(components.scheme,components.authority,components.path,components.query,components.fragment);return _validateUri(result,!0),result}
/**
     * Join a URI path with path fragments and normalizes the resulting path.
     *
     * @param uri The input URI.
     * @param pathFragment The path fragment to add to the URI path.
     * @returns The resulting URI.
     */static joinPath(uri,...pathFragment){if(!uri.path)throw new Error("[UriError]: cannot call joinPath on URI without path");let newPath;return newPath=_platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED&&"file"===uri.scheme?URI.file(_path_js__WEBPACK_IMPORTED_MODULE_0__/* .win32 */.Ku.join(uriToFsPath(uri,!0),...pathFragment)).path:_path_js__WEBPACK_IMPORTED_MODULE_0__/* .posix */.KR.join(uri.path,...pathFragment),uri.with({path:newPath})}
// ---- printing/externalize ---------------------------
/**
     * Creates a string representation for this URI. It's guaranteed that calling
     * `URI.parse` with the result of this function creates an URI which is equal
     * to this URI.
     *
     * * The result shall *not* be used for display purposes but for externalization or transport.
     * * The result will be encoded using the percentage encoding and encoding happens mostly
     * ignore the scheme-specific encoding rules.
     *
     * @param skipEncoding Do not encode the result, default is `false`
     */
toString(skipEncoding=!1){return _asFormatted(this,skipEncoding)}toJSON(){return this}static revive(data){if(data){if(data instanceof URI)return data;{const result=new Uri(data);return result._formatted=data.external,result._fsPath=data._sep===_pathSepMarker?data.fsPath:null,result}}return data}}const _pathSepMarker=_platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED?1:void 0;
// This class exists so that URI is compatible with vscode.Uri (API).
class Uri extends URI{constructor(){super(...arguments),this._formatted=null,this._fsPath=null}get fsPath(){return this._fsPath||(this._fsPath=uriToFsPath(this,!1)),this._fsPath}toString(skipEncoding=!1){return skipEncoding?_asFormatted(this,!0):(this._formatted||(this._formatted=_asFormatted(this,!1)),this._formatted)}toJSON(){const res={$mid:1/* Uri */};
// cached state
return this._fsPath&&(res.fsPath=this._fsPath,res._sep=_pathSepMarker),this._formatted&&(res.external=this._formatted),
// uri components
this.path&&(res.path=this.path),this.scheme&&(res.scheme=this.scheme),this.authority&&(res.authority=this.authority),this.query&&(res.query=this.query),this.fragment&&(res.fragment=this.fragment),res}}
// reserved characters: https://tools.ietf.org/html/rfc3986#section-2.2
const encodeTable={[58/* Colon */]:"%3A",[47/* Slash */]:"%2F",[63/* QuestionMark */]:"%3F",[35/* Hash */]:"%23",[91/* OpenSquareBracket */]:"%5B",[93/* CloseSquareBracket */]:"%5D",[64/* AtSign */]:"%40",[33/* ExclamationMark */]:"%21",[36/* DollarSign */]:"%24",[38/* Ampersand */]:"%26",[39/* SingleQuote */]:"%27",[40/* OpenParen */]:"%28",[41/* CloseParen */]:"%29",[42/* Asterisk */]:"%2A",[43/* Plus */]:"%2B",[44/* Comma */]:"%2C",[59/* Semicolon */]:"%3B",[61/* Equals */]:"%3D",[32/* Space */]:"%20"};function encodeURIComponentFast(uriComponent,allowSlash){let res,nativeEncodePos=-1;for(let pos=0;pos<uriComponent.length;pos++){const code=uriComponent.charCodeAt(pos);
// unreserved characters: https://tools.ietf.org/html/rfc3986#section-2.3
if(code>=97/* a */&&code<=122/* z */||code>=65/* A */&&code<=90/* Z */||code>=48/* Digit0 */&&code<=57/* Digit9 */||45/* Dash */===code||46/* Period */===code||95/* Underline */===code||126/* Tilde */===code||allowSlash&&47/* Slash */===code)
// check if we are delaying native encode
-1!==nativeEncodePos&&(res+=encodeURIComponent(uriComponent.substring(nativeEncodePos,pos)),nativeEncodePos=-1),
// check if we write into a new string (by default we try to return the param)
void 0!==res&&(res+=uriComponent.charAt(pos));else{
// encoding needed, we need to allocate a new string
void 0===res&&(res=uriComponent.substr(0,pos));
// check with default table first
const escaped=encodeTable[code];void 0!==escaped?(
// check if we are delaying native encode
-1!==nativeEncodePos&&(res+=encodeURIComponent(uriComponent.substring(nativeEncodePos,pos)),nativeEncodePos=-1),
// append escaped variant to result
res+=escaped):-1===nativeEncodePos&&(
// use native encode only when needed
nativeEncodePos=pos)}}return-1!==nativeEncodePos&&(res+=encodeURIComponent(uriComponent.substring(nativeEncodePos))),void 0!==res?res:uriComponent}function encodeURIComponentMinimal(path){let res;for(let pos=0;pos<path.length;pos++){const code=path.charCodeAt(pos);35/* Hash */===code||63/* QuestionMark */===code?(void 0===res&&(res=path.substr(0,pos)),res+=encodeTable[code]):void 0!==res&&(res+=path[pos])}return void 0!==res?res:path}
/**
 * Compute `fsPath` for the given uri
 */function uriToFsPath(uri,keepDriveLetterCasing){let value;
// unc path: file://shares/c$/far/boo
return value=uri.authority&&uri.path.length>1&&"file"===uri.scheme?`//${uri.authority}${uri.path}`:47/* Slash */===uri.path.charCodeAt(0)&&(uri.path.charCodeAt(1)>=65/* A */&&uri.path.charCodeAt(1)<=90/* Z */||uri.path.charCodeAt(1)>=97/* a */&&uri.path.charCodeAt(1)<=122/* z */)&&58/* Colon */===uri.path.charCodeAt(2)?keepDriveLetterCasing?uri.path.substr(1):uri.path[1].toLowerCase()+uri.path.substr(2):uri.path,_platform_js__WEBPACK_IMPORTED_MODULE_1__/* .isWindows */.ED&&(value=value.replace(/\//g,"\\")),value}
/**
 * Create the external version of a uri
 */function _asFormatted(uri,skipEncoding){const encoder=skipEncoding?encodeURIComponentMinimal:encodeURIComponentFast;let res="",{scheme:scheme,authority:authority,path:path,query:query,fragment:fragment}=uri;if(scheme&&(res+=scheme,res+=":"),(authority||"file"===scheme)&&(res+=_slash,res+=_slash),authority){let idx=authority.indexOf("@");if(-1!==idx){
// <user>@<auth>
const userinfo=authority.substr(0,idx);authority=authority.substr(idx+1),idx=userinfo.indexOf(":"),-1===idx?res+=encoder(userinfo,!1):(
// <user>:<pass>@<auth>
res+=encoder(userinfo.substr(0,idx),!1),res+=":",res+=encoder(userinfo.substr(idx+1),!1)),res+="@"}authority=authority.toLowerCase(),idx=authority.indexOf(":"),-1===idx?res+=encoder(authority,!1):(
// <auth>:<port>
res+=encoder(authority.substr(0,idx),!1),res+=authority.substr(idx))}if(path){
// lower-case windows drive letters in /C:/fff or C:/fff
if(path.length>=3&&47/* Slash */===path.charCodeAt(0)&&58/* Colon */===path.charCodeAt(2)){const code=path.charCodeAt(1);code>=65/* A */&&code<=90/* Z */&&(path=`/${String.fromCharCode(code+32)}:${path.substr(3)}`)}else if(path.length>=2&&58/* Colon */===path.charCodeAt(1)){const code=path.charCodeAt(0);code>=65/* A */&&code<=90/* Z */&&(path=`${String.fromCharCode(code+32)}:${path.substr(2)}`)}
// encode the rest of the path
res+=encoder(path,!0)}return query&&(res+="?",res+=encoder(query,!1)),fragment&&(res+="#",res+=skipEncoding?fragment:encodeURIComponentFast(fragment,!1)),res}
// --- decode
function decodeURIComponentGraceful(str){try{return decodeURIComponent(str)}catch(_a){return str.length>3?str.substr(0,3)+decodeURIComponentGraceful(str.substr(3)):str}}const _rEncodedAsHex=/(%[0-9A-Za-z][0-9A-Za-z])+/g;function percentDecode(str){return str.match(_rEncodedAsHex)?str.replace(_rEncodedAsHex,(match=>decodeURIComponentGraceful(match))):str}
/***/},
/***/98e3:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */R:function(){/* binding */return generateUuid}
/* harmony export */});
// prep-work
const _data=new Uint8Array(16),_hex=[];for(let i=0;i<256;i++)_hex.push(i.toString(16).padStart(2,"0"));
// todo@jrieken - with node@15 crypto#getRandomBytes is available everywhere, https://developer.mozilla.org/en-US/docs/Web/API/Crypto/getRandomValues#browser_compatibility
let _fillRandomValues;function generateUuid(){
// get data
_fillRandomValues(_data),
// set version bits
_data[6]=15&_data[6]|64,_data[8]=63&_data[8]|128;
// print as string
let i=0,result="";return result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+="-",result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+="-",result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+="-",result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+="-",result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result+=_hex[_data[i++]],result}
/***/
// browser
_fillRandomValues="object"===typeof crypto&&"function"===typeof crypto.getRandomValues?crypto.getRandomValues.bind(crypto):function(bucket){for(let i=0;i<bucket.length;i++)bucket[i]=Math.floor(256*Math.random());return bucket}},
/***/318352:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */HZ:function(){/* binding */return logOnceWebWorkerWarning},
/* harmony export */PB:function(){/* binding */return SimpleWorkerClient}
/* harmony export */});
/* unused harmony exports SimpleWorkerServer, create */
/* harmony import */var _errors_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(817301),_event_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(104669),_lifecycle_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(905976),_platform_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(901432),_types_js__WEBPACK_IMPORTED_MODULE_5__=__webpack_require__(998401),_strings_js__WEBPACK_IMPORTED_MODULE_4__=__webpack_require__(697295);
/* harmony import */
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const INITIALIZE="$initialize";let webWorkerWarningLogged=!1;function logOnceWebWorkerWarning(err){_platform_js__WEBPACK_IMPORTED_MODULE_3__/* .isWeb */.$L&&(webWorkerWarningLogged||(webWorkerWarningLogged=!0))}class RequestMessage{constructor(vsWorker,req,method,args){this.vsWorker=vsWorker,this.req=req,this.method=method,this.args=args,this.type=0/* Request */}}class ReplyMessage{constructor(vsWorker,seq,res,err){this.vsWorker=vsWorker,this.seq=seq,this.res=res,this.err=err,this.type=1/* Reply */}}class SubscribeEventMessage{constructor(vsWorker,req,eventName,arg){this.vsWorker=vsWorker,this.req=req,this.eventName=eventName,this.arg=arg,this.type=2/* SubscribeEvent */}}class EventMessage{constructor(vsWorker,req,event){this.vsWorker=vsWorker,this.req=req,this.event=event,this.type=3/* Event */}}class UnsubscribeEventMessage{constructor(vsWorker,req){this.vsWorker=vsWorker,this.req=req,this.type=4/* UnsubscribeEvent */}}class SimpleWorkerProtocol{constructor(handler){this._workerId=-1,this._handler=handler,this._lastSentReq=0,this._pendingReplies=Object.create(null),this._pendingEmitters=new Map,this._pendingEvents=new Map}setWorkerId(workerId){this._workerId=workerId}sendMessage(method,args){const req=String(++this._lastSentReq);return new Promise(((resolve,reject)=>{this._pendingReplies[req]={resolve:resolve,reject:reject},this._send(new RequestMessage(this._workerId,req,method,args))}))}listen(eventName,arg){let req=null;const emitter=new _event_js__WEBPACK_IMPORTED_MODULE_1__/* .Emitter */.Q5({onFirstListenerAdd:()=>{req=String(++this._lastSentReq),this._pendingEmitters.set(req,emitter),this._send(new SubscribeEventMessage(this._workerId,req,eventName,arg))},onLastListenerRemove:()=>{this._pendingEmitters.delete(req),this._send(new UnsubscribeEventMessage(this._workerId,req)),req=null}});return emitter.event}handleMessage(message){message&&message.vsWorker&&(-1!==this._workerId&&message.vsWorker!==this._workerId||this._handleMessage(message))}_handleMessage(msg){switch(msg.type){case 1/* Reply */:return this._handleReplyMessage(msg);case 0/* Request */:return this._handleRequestMessage(msg);case 2/* SubscribeEvent */:return this._handleSubscribeEventMessage(msg);case 3/* Event */:return this._handleEventMessage(msg);case 4/* UnsubscribeEvent */:return this._handleUnsubscribeEventMessage(msg)}}_handleReplyMessage(replyMessage){if(!this._pendingReplies[replyMessage.seq])return;let reply=this._pendingReplies[replyMessage.seq];if(delete this._pendingReplies[replyMessage.seq],replyMessage.err){let err=replyMessage.err;return replyMessage.err.$isError&&(err=new Error,err.name=replyMessage.err.name,err.message=replyMessage.err.message,err.stack=replyMessage.err.stack),void reply.reject(err)}reply.resolve(replyMessage.res)}_handleRequestMessage(requestMessage){let req=requestMessage.req,result=this._handler.handleMessage(requestMessage.method,requestMessage.args);result.then((r=>{this._send(new ReplyMessage(this._workerId,req,r,void 0))}),(e=>{e.detail instanceof Error&&(
// Loading errors have a detail property that points to the actual error
e.detail=(0,_errors_js__WEBPACK_IMPORTED_MODULE_0__/* .transformErrorForSerialization */.ri)(e.detail)),this._send(new ReplyMessage(this._workerId,req,void 0,(0,_errors_js__WEBPACK_IMPORTED_MODULE_0__/* .transformErrorForSerialization */.ri)(e)))}))}_handleSubscribeEventMessage(msg){const req=msg.req,disposable=this._handler.handleEvent(msg.eventName,msg.arg)((event=>{this._send(new EventMessage(this._workerId,req,event))}));this._pendingEvents.set(req,disposable)}_handleEventMessage(msg){this._pendingEmitters.has(msg.req)&&this._pendingEmitters.get(msg.req).fire(msg.event)}_handleUnsubscribeEventMessage(msg){this._pendingEvents.has(msg.req)&&(this._pendingEvents.get(msg.req).dispose(),this._pendingEvents.delete(msg.req))}_send(msg){let transfer=[];if(0/* Request */===msg.type)for(let i=0;i<msg.args.length;i++)msg.args[i]instanceof ArrayBuffer&&transfer.push(msg.args[i]);else 1/* Reply */===msg.type&&msg.res instanceof ArrayBuffer&&transfer.push(msg.res);this._handler.sendMessage(msg,transfer)}}
/**
 * Main thread side
 */class SimpleWorkerClient extends _lifecycle_js__WEBPACK_IMPORTED_MODULE_2__/* .Disposable */.JT{constructor(workerFactory,moduleId,host){super();let lazyProxyReject=null;this._worker=this._register(workerFactory.create("vs/base/common/worker/simpleWorker",(msg=>{this._protocol.handleMessage(msg)}),(err=>{
// in Firefox, web workers fail lazily :(
// we will reject the proxy
lazyProxyReject&&lazyProxyReject(err)}))),this._protocol=new SimpleWorkerProtocol({sendMessage:(msg,transfer)=>{this._worker.postMessage(msg,transfer)},handleMessage:(method,args)=>{if("function"!==typeof host[method])return Promise.reject(new Error("Missing method "+method+" on main thread host."));try{return Promise.resolve(host[method].apply(host,args))}catch(e){return Promise.reject(e)}},handleEvent:(eventName,arg)=>{if(propertyIsDynamicEvent(eventName)){const event=host[eventName].call(host,arg);if("function"!==typeof event)throw new Error(`Missing dynamic event ${eventName} on main thread host.`);return event}if(propertyIsEvent(eventName)){const event=host[eventName];if("function"!==typeof event)throw new Error(`Missing event ${eventName} on main thread host.`);return event}throw new Error(`Malformed event name ${eventName}`)}}),this._protocol.setWorkerId(this._worker.getId());
// Gather loader configuration
let loaderConfiguration=null;"undefined"!==typeof _platform_js__WEBPACK_IMPORTED_MODULE_3__/* .globals */.li.require&&"function"===typeof _platform_js__WEBPACK_IMPORTED_MODULE_3__/* .globals */.li.require.getConfig?
// Get the configuration from the Monaco AMD Loader
loaderConfiguration=_platform_js__WEBPACK_IMPORTED_MODULE_3__/* .globals */.li.require.getConfig():"undefined"!==typeof _platform_js__WEBPACK_IMPORTED_MODULE_3__/* .globals */.li.requirejs&&(
// Get the configuration from requirejs
loaderConfiguration=_platform_js__WEBPACK_IMPORTED_MODULE_3__/* .globals */.li.requirejs.s.contexts._.config);const hostMethods=_types_js__WEBPACK_IMPORTED_MODULE_5__/* .getAllMethodNames */.$E(host);
// Send initialize message
this._onModuleLoaded=this._protocol.sendMessage(INITIALIZE,[this._worker.getId(),JSON.parse(JSON.stringify(loaderConfiguration)),moduleId,hostMethods]);
// Create proxy to loaded code
const proxyMethodRequest=(method,args)=>this._request(method,args),proxyListen=(eventName,arg)=>this._protocol.listen(eventName,arg);this._lazyProxy=new Promise(((resolve,reject)=>{lazyProxyReject=reject,this._onModuleLoaded.then((availableMethods=>{resolve(createProxyObject(availableMethods,proxyMethodRequest,proxyListen))}),(e=>{reject(e),this._onError("Worker failed to load "+moduleId,e)}))}))}getProxyObject(){return this._lazyProxy}_request(method,args){return new Promise(((resolve,reject)=>{this._onModuleLoaded.then((()=>{this._protocol.sendMessage(method,args).then(resolve,reject)}),reject)}))}_onError(message,error){}}function propertyIsEvent(name){
// Assume a property is an event if it has a form of "onSomething"
return"o"===name[0]&&"n"===name[1]&&_strings_js__WEBPACK_IMPORTED_MODULE_4__/* .isUpperAsciiLetter */.df(name.charCodeAt(2))}function propertyIsDynamicEvent(name){
// Assume a property is a dynamic event (a method that returns an event) if it has a form of "onDynamicSomething"
return/^onDynamic/.test(name)&&_strings_js__WEBPACK_IMPORTED_MODULE_4__/* .isUpperAsciiLetter */.df(name.charCodeAt(9))}function createProxyObject(methodNames,invoke,proxyListen){const createProxyMethod=method=>function(){const args=Array.prototype.slice.call(arguments,0);return invoke(method,args)},createProxyDynamicEvent=eventName=>function(arg){return proxyListen(eventName,arg)};let result={};for(const methodName of methodNames)propertyIsDynamicEvent(methodName)?result[methodName]=createProxyDynamicEvent(methodName):propertyIsEvent(methodName)?result[methodName]=proxyListen(methodName,void 0):result[methodName]=createProxyMethod(methodName);return result}
/**
 * Worker side
 */},
/***/80605:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
// EXPORTS
__webpack_require__.d(__webpack_exports__,{O:function(){/* binding */return QuickInputController}});
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/browser/dom.js
var dom=__webpack_require__(965321),browser_keyboardEvent=__webpack_require__(459069),actionbar=__webpack_require__(790317),button_button=__webpack_require__(243416),countBadge=__webpack_require__(467488),iconLabels=__webpack_require__(156811),progressbar=__webpack_require__(812726),actions=__webpack_require__(74741),arrays=__webpack_require__(609488),common_async=__webpack_require__(715393),cancellation=__webpack_require__(471050),codicons=__webpack_require__(773046),common_event=__webpack_require__(104669),lifecycle=__webpack_require__(905976),platform=__webpack_require__(901432),common_severity=__webpack_require__(14603),idGenerator=__webpack_require__(844742);
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/browser/keyboardEvent.js
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/parts/quickinput/browser/quickInputUtils.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const iconPathToClass={},iconClassGenerator=new idGenerator/* IdGenerator */.R("quick-input-button-icon-");function getIconClass(iconPath){if(!iconPath)return;let iconClass;const key=iconPath.dark.toString();return iconPathToClass[key]?iconClass=iconPathToClass[key]:(iconClass=iconClassGenerator.nextId(),dom/* createCSSRule */.fk(`.${iconClass}`,`background-image: ${dom/* asCSSUrl */.wY(iconPath.light||iconPath.dark)}`),dom/* createCSSRule */.fk(`.vs-dark .${iconClass}, .hc-black .${iconClass}`,`background-image: ${dom/* asCSSUrl */.wY(iconPath.dark)}`),iconPathToClass[key]=iconClass),iconClass}
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/parts/quickinput/common/quickInput.js
var quickInput=__webpack_require__(867746),nls=__webpack_require__(663580),mouseEvent=__webpack_require__(23938),inputBox=__webpack_require__(540114);
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/nls.js
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/parts/quickinput/browser/quickInputBox.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const $=dom.$;class QuickInputBox extends lifecycle/* Disposable */.JT{constructor(parent){super(),this.parent=parent,this.onKeyDown=handler=>dom/* addDisposableListener */.nm(this.inputBox.inputElement,dom/* EventType */.tw.KEY_DOWN,(e=>{handler(new browser_keyboardEvent/* StandardKeyboardEvent */.y(e))})),this.onMouseDown=handler=>dom/* addDisposableListener */.nm(this.inputBox.inputElement,dom/* EventType */.tw.MOUSE_DOWN,(e=>{handler(new mouseEvent/* StandardMouseEvent */.n(e))})),this.onDidChange=handler=>this.inputBox.onDidChange(handler),this.container=dom/* append */.R3(this.parent,$(".quick-input-box")),this.inputBox=this._register(new inputBox/* InputBox */.W(this.container,void 0))}get value(){return this.inputBox.value}set value(value){this.inputBox.value=value}select(range=null){this.inputBox.select(range)}isSelectionAtEnd(){return this.inputBox.isSelectionAtEnd()}get placeholder(){return this.inputBox.inputElement.getAttribute("placeholder")||""}set placeholder(placeholder){this.inputBox.setPlaceHolder(placeholder)}get ariaLabel(){return this.inputBox.getAriaLabel()}set ariaLabel(ariaLabel){this.inputBox.setAriaLabel(ariaLabel)}get password(){return"password"===this.inputBox.inputElement.type}set password(password){this.inputBox.inputElement.type=password?"password":"text"}setAttribute(name,value){this.inputBox.inputElement.setAttribute(name,value)}removeAttribute(name){this.inputBox.inputElement.removeAttribute(name)}showDecoration(decoration){decoration===common_severity/* default */.Z.Ignore?this.inputBox.hideMessage():this.inputBox.showMessage({type:decoration===common_severity/* default */.Z.Info?1/* INFO */:decoration===common_severity/* default */.Z.Warning?2/* WARNING */:3/* ERROR */,content:""})}stylesForType(decoration){return this.inputBox.stylesForType(decoration===common_severity/* default */.Z.Info?1/* INFO */:decoration===common_severity/* default */.Z.Warning?2/* WARNING */:3/* ERROR */)}setFocus(){this.inputBox.focus()}layout(){this.inputBox.layout()}style(styles){this.inputBox.style(styles)}}
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/browser/ui/highlightedlabel/highlightedLabel.js
var highlightedLabel=__webpack_require__(734650),iconLabel=__webpack_require__(10489),keybindingLabel=__webpack_require__(455496);
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/browser/ui/iconLabel/iconLabel.js + 1 modules
// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/common/comparers.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
// When comparing large numbers of strings it's better for performance to create an
// Intl.Collator object and use the function provided by its compare property
// than it is to use String.prototype.localeCompare()
// A collator with numeric sorting enabled, and no sensitivity to case, accents or diacritics.
const intlFileNameCollatorBaseNumeric=new common_async/* IdleValue */.Ue((()=>{const collator=new Intl.Collator(void 0,{numeric:!0,sensitivity:"base"});return{collator:collator,collatorIsNumeric:collator.resolvedOptions().numeric}}));
/** Compares filenames without distinguishing the name from the extension. Disambiguates by unicode comparison. */function compareFileNames(one,other,caseSensitive=!1){const a=one||"",b=other||"",result=intlFileNameCollatorBaseNumeric.value.collator.compare(a,b);
// Using the numeric option will make compare(`foo1`, `foo01`) === 0. Disambiguate.
return intlFileNameCollatorBaseNumeric.value.collatorIsNumeric&&0===result&&a!==b?a<b?-1:1:result}function compareAnything(one,other,lookFor){const elementAName=one.toLowerCase(),elementBName=other.toLowerCase(),prefixCompare=compareByPrefix(one,other,lookFor);if(prefixCompare)return prefixCompare;
// Sort suffix matches over non suffix matches
const elementASuffixMatch=elementAName.endsWith(lookFor),elementBSuffixMatch=elementBName.endsWith(lookFor);if(elementASuffixMatch!==elementBSuffixMatch)return elementASuffixMatch?-1:1;
// Understand file names
const r=compareFileNames(elementAName,elementBName);return 0!==r?r:elementAName.localeCompare(elementBName);
// Compare by name
}function compareByPrefix(one,other,lookFor){const elementAName=one.toLowerCase(),elementBName=other.toLowerCase(),elementAPrefixMatch=elementAName.startsWith(lookFor),elementBPrefixMatch=elementBName.startsWith(lookFor);if(elementAPrefixMatch!==elementBPrefixMatch)return elementAPrefixMatch?-1:1;if(elementAPrefixMatch&&elementBPrefixMatch){if(elementAName.length<elementBName.length)return-1;if(elementAName.length>elementBName.length)return 1}return 0}
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/decorators.js
var decorators=__webpack_require__(749898),common_iconLabels=__webpack_require__(721212),types=__webpack_require__(998401),__decorate=function(decorators,target,key,desc){var d,c=arguments.length,r=c<3?target:null===desc?desc=Object.getOwnPropertyDescriptor(target,key):desc;if("object"===typeof Reflect&&"function"===typeof Reflect.decorate)r=Reflect.decorate(decorators,target,key,desc);else for(var i=decorators.length-1;i>=0;i--)(d=decorators[i])&&(r=(c<3?d(r):c>3?d(target,key,r):d(target,key))||r);return c>3&&r&&Object.defineProperty(target,key,r),r},__awaiter=function(thisArg,_arguments,P,generator){function adopt(value){return value instanceof P?value:new P((function(resolve){resolve(value)}))}return new(P||(P=Promise))((function(resolve,reject){function fulfilled(value){try{step(generator.next(value))}catch(e){reject(e)}}function rejected(value){try{step(generator["throw"](value))}catch(e){reject(e)}}function step(result){result.done?resolve(result.value):adopt(result.value).then(fulfilled,rejected)}step((generator=generator.apply(thisArg,_arguments||[])).next())}))};
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/base/common/iconLabels.js
const quickInputList_$=dom.$;class ListElement{constructor(init){this.hidden=!1,this._onChecked=new common_event/* Emitter */.Q5,this.onChecked=this._onChecked.event,Object.assign(this,init)}get checked(){return!!this._checked}set checked(value){value!==this._checked&&(this._checked=value,this._onChecked.fire(value))}dispose(){this._onChecked.dispose()}}class ListElementRenderer{get templateId(){return ListElementRenderer.ID}renderTemplate(container){const data=Object.create(null);data.toDisposeElement=[],data.toDisposeTemplate=[],data.entry=dom/* append */.R3(container,quickInputList_$(".quick-input-list-entry"));
// Checkbox
const label=dom/* append */.R3(data.entry,quickInputList_$("label.quick-input-list-label"));data.toDisposeTemplate.push(dom/* addStandardDisposableListener */.mu(label,dom/* EventType */.tw.CLICK,(e=>{data.checkbox.offsetParent||// If checkbox not visible:
e.preventDefault()}))),data.checkbox=dom/* append */.R3(label,quickInputList_$("input.quick-input-list-checkbox")),data.checkbox.type="checkbox",data.toDisposeTemplate.push(dom/* addStandardDisposableListener */.mu(data.checkbox,dom/* EventType */.tw.CHANGE,(e=>{data.element.checked=data.checkbox.checked})));
// Rows
const rows=dom/* append */.R3(label,quickInputList_$(".quick-input-list-rows")),row1=dom/* append */.R3(rows,quickInputList_$(".quick-input-list-row")),row2=dom/* append */.R3(rows,quickInputList_$(".quick-input-list-row"));
// Label
data.label=new iconLabel/* IconLabel */.g(row1,{supportHighlights:!0,supportDescriptionHighlights:!0,supportIcons:!0});
// Keybinding
const keybindingContainer=dom/* append */.R3(row1,quickInputList_$(".quick-input-list-entry-keybinding"));data.keybinding=new keybindingLabel/* KeybindingLabel */.e(keybindingContainer,platform.OS);
// Detail
const detailContainer=dom/* append */.R3(row2,quickInputList_$(".quick-input-list-label-meta"));return data.detail=new highlightedLabel/* HighlightedLabel */.q(detailContainer,!0),
// Separator
data.separator=dom/* append */.R3(data.entry,quickInputList_$(".quick-input-list-separator")),
// Actions
data.actionBar=new actionbar/* ActionBar */.o(data.entry),data.actionBar.domNode.classList.add("quick-input-list-entry-action-bar"),data.toDisposeTemplate.push(data.actionBar),data}renderElement(element,index,data){data.toDisposeElement=(0,lifecycle/* dispose */.B9)(data.toDisposeElement),data.element=element,data.checkbox.checked=element.checked,data.toDisposeElement.push(element.onChecked((checked=>data.checkbox.checked=checked)));const{labelHighlights:labelHighlights,descriptionHighlights:descriptionHighlights,detailHighlights:detailHighlights}=element,options=Object.create(null);
// Label
options.matches=labelHighlights||[],options.descriptionTitle=element.saneDescription,options.descriptionMatches=descriptionHighlights||[],options.extraClasses=element.item.iconClasses,options.italic=element.item.italic,options.strikethrough=element.item.strikethrough,data.label.setLabel(element.saneLabel,element.saneDescription,options),
// Keybinding
data.keybinding.set(element.item.keybinding),
// Meta
data.detail.set(element.saneDetail,detailHighlights),
// Separator
element.separator&&element.separator.label?(data.separator.textContent=element.separator.label,data.separator.style.display=""):data.separator.style.display="none",data.entry.classList.toggle("quick-input-list-separator-border",!!element.separator),
// Actions
data.actionBar.clear();const buttons=element.item.buttons;buttons&&buttons.length?(data.actionBar.push(buttons.map(((button,index)=>{let cssClasses=button.iconClass||(button.iconPath?getIconClass(button.iconPath):void 0);button.alwaysVisible&&(cssClasses=cssClasses?`${cssClasses} always-visible`:"always-visible");const action=new actions/* Action */.aU(`id-${index}`,"",cssClasses,!0,(()=>__awaiter(this,void 0,void 0,(function*(){element.fireButtonTriggered({button:button,item:element.item})}))));return action.tooltip=button.tooltip||"",action})),{icon:!0,label:!1}),data.entry.classList.add("has-actions")):data.entry.classList.remove("has-actions")}disposeElement(element,index,data){data.toDisposeElement=(0,lifecycle/* dispose */.B9)(data.toDisposeElement)}disposeTemplate(data){data.toDisposeElement=(0,lifecycle/* dispose */.B9)(data.toDisposeElement),data.toDisposeTemplate=(0,lifecycle/* dispose */.B9)(data.toDisposeTemplate)}}ListElementRenderer.ID="listelement";class ListElementDelegate{getHeight(element){return element.saneDetail?44:22}getTemplateId(element){return ListElementRenderer.ID}}var QuickInputListFocus;(function(QuickInputListFocus){QuickInputListFocus[QuickInputListFocus["First"]=1]="First",QuickInputListFocus[QuickInputListFocus["Second"]=2]="Second",QuickInputListFocus[QuickInputListFocus["Last"]=3]="Last",QuickInputListFocus[QuickInputListFocus["Next"]=4]="Next",QuickInputListFocus[QuickInputListFocus["Previous"]=5]="Previous",QuickInputListFocus[QuickInputListFocus["NextPage"]=6]="NextPage",QuickInputListFocus[QuickInputListFocus["PreviousPage"]=7]="PreviousPage"})(QuickInputListFocus||(QuickInputListFocus={}));class QuickInputList{constructor(parent,id,options){this.parent=parent,this.inputElements=[],this.elements=[],this.elementsToIndexes=new Map,this.matchOnDescription=!1,this.matchOnDetail=!1,this.matchOnLabel=!0,this.matchOnMeta=!0,this.sortByLabel=!0,this._onChangedAllVisibleChecked=new common_event/* Emitter */.Q5,this.onChangedAllVisibleChecked=this._onChangedAllVisibleChecked.event,this._onChangedCheckedCount=new common_event/* Emitter */.Q5,this.onChangedCheckedCount=this._onChangedCheckedCount.event,this._onChangedVisibleCount=new common_event/* Emitter */.Q5,this.onChangedVisibleCount=this._onChangedVisibleCount.event,this._onChangedCheckedElements=new common_event/* Emitter */.Q5,this.onChangedCheckedElements=this._onChangedCheckedElements.event,this._onButtonTriggered=new common_event/* Emitter */.Q5,this.onButtonTriggered=this._onButtonTriggered.event,this._onKeyDown=new common_event/* Emitter */.Q5,this.onKeyDown=this._onKeyDown.event,this._onLeave=new common_event/* Emitter */.Q5,this.onLeave=this._onLeave.event,this._fireCheckedEvents=!0,this.elementDisposables=[],this.disposables=[],this.id=id,this.container=dom/* append */.R3(this.parent,quickInputList_$(".quick-input-list"));const delegate=new ListElementDelegate,accessibilityProvider=new QuickInputAccessibilityProvider;this.list=options.createList("QuickInput",this.container,delegate,[new ListElementRenderer],{identityProvider:{getId:element=>element.saneLabel},setRowLineHeight:!1,multipleSelectionSupport:!1,horizontalScrolling:!1,accessibilityProvider:accessibilityProvider}),this.list.getHTMLElement().id=id,this.disposables.push(this.list),this.disposables.push(this.list.onKeyDown((e=>{const event=new browser_keyboardEvent/* StandardKeyboardEvent */.y(e);switch(event.keyCode){case 10/* Space */:this.toggleCheckbox();break;case 31/* KeyA */:(platform/* isMacintosh */.dz?e.metaKey:e.ctrlKey)&&this.list.setFocus((0,arrays/* range */.w6)(this.list.length));break;case 16/* UpArrow */:const focus1=this.list.getFocus();1===focus1.length&&0===focus1[0]&&this._onLeave.fire();break;case 18/* DownArrow */:const focus2=this.list.getFocus();1===focus2.length&&focus2[0]===this.list.length-1&&this._onLeave.fire();break}this._onKeyDown.fire(event)}))),this.disposables.push(this.list.onMouseDown((e=>{2!==e.browserEvent.button&&
// Works around / fixes #64350.
e.browserEvent.preventDefault()}))),this.disposables.push(dom/* addDisposableListener */.nm(this.container,dom/* EventType */.tw.CLICK,(e=>{(e.x||e.y)&&// Avoid 'click' triggered by 'space' on checkbox.
this._onLeave.fire()}))),this.disposables.push(this.list.onMouseMiddleClick((e=>{this._onLeave.fire()}))),this.disposables.push(this.list.onContextMenu((e=>{"number"===typeof e.index&&(e.browserEvent.preventDefault(),
// we want to treat a context menu event as
// a gesture to open the item at the index
// since we do not have any context menu
// this enables for example macOS to Ctrl-
// click on an item to open it.
this.list.setSelection([e.index]))}))),this.disposables.push(this._onChangedAllVisibleChecked,this._onChangedCheckedCount,this._onChangedVisibleCount,this._onChangedCheckedElements,this._onButtonTriggered,this._onLeave,this._onKeyDown)}get onDidChangeFocus(){return common_event/* Event */.ju.map(this.list.onDidChangeFocus,(e=>e.elements.map((e=>e.item))))}get onDidChangeSelection(){return common_event/* Event */.ju.map(this.list.onDidChangeSelection,(e=>({items:e.elements.map((e=>e.item)),event:e.browserEvent})))}get scrollTop(){return this.list.scrollTop}set scrollTop(scrollTop){this.list.scrollTop=scrollTop}getAllVisibleChecked(){return this.allVisibleChecked(this.elements,!1)}allVisibleChecked(elements,whenNoneVisible=!0){for(let i=0,n=elements.length;i<n;i++){const element=elements[i];if(!element.hidden){if(!element.checked)return!1;whenNoneVisible=!0}}return whenNoneVisible}getCheckedCount(){let count=0;const elements=this.elements;for(let i=0,n=elements.length;i<n;i++)elements[i].checked&&count++;return count}getVisibleCount(){let count=0;const elements=this.elements;for(let i=0,n=elements.length;i<n;i++)elements[i].hidden||count++;return count}setAllVisibleChecked(checked){try{this._fireCheckedEvents=!1,this.elements.forEach((element=>{element.hidden||(element.checked=checked)}))}finally{this._fireCheckedEvents=!0,this.fireCheckedEvents()}}setElements(inputElements){this.elementDisposables=(0,lifecycle/* dispose */.B9)(this.elementDisposables);const fireButtonTriggered=event=>this.fireButtonTriggered(event);this.inputElements=inputElements,this.elements=inputElements.reduce(((result,item,index)=>{var _a,_b,_c;if("separator"!==item.type){const previous=index&&inputElements[index-1],saneLabel=item.label&&item.label.replace(/\r?\n/g," "),saneMeta=item.meta&&item.meta.replace(/\r?\n/g," "),saneDescription=item.description&&item.description.replace(/\r?\n/g," "),saneDetail=item.detail&&item.detail.replace(/\r?\n/g," "),saneAriaLabel=item.ariaLabel||[saneLabel,saneDescription,saneDetail].map((s=>(0,codicons/* getCodiconAriaLabel */.JL)(s))).filter((s=>!!s)).join(", ");result.push(new ListElement({index:index,item:item,saneLabel:saneLabel,saneMeta:saneMeta,saneAriaLabel:saneAriaLabel,saneDescription:saneDescription,saneDetail:saneDetail,labelHighlights:null===(_a=item.highlights)||void 0===_a?void 0:_a.label,descriptionHighlights:null===(_b=item.highlights)||void 0===_b?void 0:_b.description,detailHighlights:null===(_c=item.highlights)||void 0===_c?void 0:_c.detail,checked:!1,separator:previous&&"separator"===previous.type?previous:void 0,fireButtonTriggered:fireButtonTriggered}))}return result}),[]),this.elementDisposables.push(...this.elements),this.elementDisposables.push(...this.elements.map((element=>element.onChecked((()=>this.fireCheckedEvents()))))),this.elementsToIndexes=this.elements.reduce(((map,element,index)=>(map.set(element.item,index),map)),new Map),this.list.splice(0,this.list.length),// Clear focus and selection first, sending the events when the list is empty.
this.list.splice(0,this.list.length,this.elements),this._onChangedVisibleCount.fire(this.elements.length)}getFocusedElements(){return this.list.getFocusedElements().map((e=>e.item))}setFocusedElements(items){if(this.list.setFocus(items.filter((item=>this.elementsToIndexes.has(item))).map((item=>this.elementsToIndexes.get(item)))),items.length>0){const focused=this.list.getFocus()[0];"number"===typeof focused&&this.list.reveal(focused)}}getActiveDescendant(){return this.list.getHTMLElement().getAttribute("aria-activedescendant")}setSelectedElements(items){this.list.setSelection(items.filter((item=>this.elementsToIndexes.has(item))).map((item=>this.elementsToIndexes.get(item))))}getCheckedElements(){return this.elements.filter((e=>e.checked)).map((e=>e.item))}setCheckedElements(items){try{this._fireCheckedEvents=!1;const checked=new Set;for(const item of items)checked.add(item);for(const element of this.elements)element.checked=checked.has(element.item)}finally{this._fireCheckedEvents=!0,this.fireCheckedEvents()}}set enabled(value){this.list.getHTMLElement().style.pointerEvents=value?"":"none"}focus(what){if(!this.list.length)return;switch(what===QuickInputListFocus.Next&&this.list.getFocus()[0]===this.list.length-1&&(what=QuickInputListFocus.First),what===QuickInputListFocus.Previous&&0===this.list.getFocus()[0]&&(what=QuickInputListFocus.Last),what===QuickInputListFocus.Second&&this.list.length<2&&(what=QuickInputListFocus.First),what){case QuickInputListFocus.First:this.list.focusFirst();break;case QuickInputListFocus.Second:this.list.focusNth(1);break;case QuickInputListFocus.Last:this.list.focusLast();break;case QuickInputListFocus.Next:this.list.focusNext();break;case QuickInputListFocus.Previous:this.list.focusPrevious();break;case QuickInputListFocus.NextPage:this.list.focusNextPage();break;case QuickInputListFocus.PreviousPage:this.list.focusPreviousPage();break}const focused=this.list.getFocus()[0];"number"===typeof focused&&this.list.reveal(focused)}clearFocus(){this.list.setFocus([])}domFocus(){this.list.domFocus()}layout(maxHeight){this.list.getHTMLElement().style.maxHeight=maxHeight?`calc(${44*Math.floor(maxHeight/44)}px)`:"",this.list.layout()}filter(query){if(!(this.sortByLabel||this.matchOnLabel||this.matchOnDescription||this.matchOnDetail))return this.list.layout(),!1;
// Reset filtering
if(query=query.trim(),query&&(this.matchOnLabel||this.matchOnDescription||this.matchOnDetail)){let currentSeparator;this.elements.forEach((element=>{const labelHighlights=this.matchOnLabel?(0,types/* withNullAsUndefined */.f6)((0,common_iconLabels/* matchesFuzzyIconAware */.Gt)(query,(0,common_iconLabels/* parseLabelWithIcons */.Ho)(element.saneLabel))):void 0,descriptionHighlights=this.matchOnDescription?(0,types/* withNullAsUndefined */.f6)((0,common_iconLabels/* matchesFuzzyIconAware */.Gt)(query,(0,common_iconLabels/* parseLabelWithIcons */.Ho)(element.saneDescription||""))):void 0,detailHighlights=this.matchOnDetail?(0,types/* withNullAsUndefined */.f6)((0,common_iconLabels/* matchesFuzzyIconAware */.Gt)(query,(0,common_iconLabels/* parseLabelWithIcons */.Ho)(element.saneDetail||""))):void 0,metaHighlights=this.matchOnMeta?(0,types/* withNullAsUndefined */.f6)((0,common_iconLabels/* matchesFuzzyIconAware */.Gt)(query,(0,common_iconLabels/* parseLabelWithIcons */.Ho)(element.saneMeta||""))):void 0;
// we can show the separator unless the list gets sorted by match
if(labelHighlights||descriptionHighlights||detailHighlights||metaHighlights?(element.labelHighlights=labelHighlights,element.descriptionHighlights=descriptionHighlights,element.detailHighlights=detailHighlights,element.hidden=!1):(element.labelHighlights=void 0,element.descriptionHighlights=void 0,element.detailHighlights=void 0,element.hidden=!element.item.alwaysShow),element.separator=void 0,!this.sortByLabel){const previous=element.index&&this.inputElements[element.index-1];currentSeparator=previous&&"separator"===previous.type?previous:currentSeparator,currentSeparator&&!element.hidden&&(element.separator=currentSeparator,currentSeparator=void 0)}}))}else this.elements.forEach((element=>{element.labelHighlights=void 0,element.descriptionHighlights=void 0,element.detailHighlights=void 0,element.hidden=!1;const previous=element.index&&this.inputElements[element.index-1];element.separator=previous&&"separator"===previous.type?previous:void 0}));const shownElements=this.elements.filter((element=>!element.hidden));
// Sort by value
if(this.sortByLabel&&query){const normalizedSearchValue=query.toLowerCase();shownElements.sort(((a,b)=>compareEntries(a,b,normalizedSearchValue)))}return this.elementsToIndexes=shownElements.reduce(((map,element,index)=>(map.set(element.item,index),map)),new Map),this.list.splice(0,this.list.length,shownElements),this.list.setFocus([]),this.list.layout(),this._onChangedAllVisibleChecked.fire(this.getAllVisibleChecked()),this._onChangedVisibleCount.fire(shownElements.length),!0}toggleCheckbox(){try{this._fireCheckedEvents=!1;const elements=this.list.getFocusedElements(),allChecked=this.allVisibleChecked(elements);for(const element of elements)element.checked=!allChecked}finally{this._fireCheckedEvents=!0,this.fireCheckedEvents()}}display(display){this.container.style.display=display?"":"none"}isDisplayed(){return"none"!==this.container.style.display}dispose(){this.elementDisposables=(0,lifecycle/* dispose */.B9)(this.elementDisposables),this.disposables=(0,lifecycle/* dispose */.B9)(this.disposables)}fireCheckedEvents(){this._fireCheckedEvents&&(this._onChangedAllVisibleChecked.fire(this.getAllVisibleChecked()),this._onChangedCheckedCount.fire(this.getCheckedCount()),this._onChangedCheckedElements.fire(this.getCheckedElements()))}fireButtonTriggered(event){this._onButtonTriggered.fire(event)}style(styles){this.list.style(styles)}}function compareEntries(elementA,elementB,lookFor){const labelHighlightsA=elementA.labelHighlights||[],labelHighlightsB=elementB.labelHighlights||[];return labelHighlightsA.length&&!labelHighlightsB.length?-1:!labelHighlightsA.length&&labelHighlightsB.length?1:0===labelHighlightsA.length&&0===labelHighlightsB.length?0:compareAnything(elementA.saneLabel,elementB.saneLabel,lookFor)}__decorate([decorators/* memoize */.H],QuickInputList.prototype,"onDidChangeFocus",null),__decorate([decorators/* memoize */.H],QuickInputList.prototype,"onDidChangeSelection",null);class QuickInputAccessibilityProvider{getWidgetAriaLabel(){return(0,nls/* localize */.N)("quickInput","Quick Input")}getAriaLabel(element){return element.saneAriaLabel}getWidgetRole(){return"listbox"}getRole(){return"option"}}// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/base/parts/quickinput/browser/quickInput.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
var quickInput_awaiter=function(thisArg,_arguments,P,generator){function adopt(value){return value instanceof P?value:new P((function(resolve){resolve(value)}))}return new(P||(P=Promise))((function(resolve,reject){function fulfilled(value){try{step(generator.next(value))}catch(e){reject(e)}}function rejected(value){try{step(generator["throw"](value))}catch(e){reject(e)}}function step(result){result.done?resolve(result.value):adopt(result.value).then(fulfilled,rejected)}step((generator=generator.apply(thisArg,_arguments||[])).next())}))};const quickInput_$=dom.$,backButtonIcon=(0,codicons/* registerCodicon */.CM)("quick-input-back",codicons/* Codicon */.lA.arrowLeft),backButton={iconClass:backButtonIcon.classNames,tooltip:(0,nls/* localize */.N)("quickInput.back","Back"),handle:-1};class QuickInput extends lifecycle/* Disposable */.JT{constructor(ui){super(),this.ui=ui,this.visible=!1,this._enabled=!0,this._busy=!1,this._ignoreFocusOut=!1,this._buttons=[],this.noValidationMessage=QuickInput.noPromptMessage,this._severity=common_severity/* default */.Z.Ignore,this.buttonsUpdated=!1,this.onDidTriggerButtonEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidHideEmitter=this._register(new common_event/* Emitter */.Q5),this.onDisposeEmitter=this._register(new common_event/* Emitter */.Q5),this.visibleDisposables=this._register(new lifecycle/* DisposableStore */.SL),this.onDidHide=this.onDidHideEmitter.event}get title(){return this._title}set title(title){this._title=title,this.update()}get description(){return this._description}set description(description){this._description=description,this.update()}get step(){return this._steps}set step(step){this._steps=step,this.update()}get totalSteps(){return this._totalSteps}set totalSteps(totalSteps){this._totalSteps=totalSteps,this.update()}get enabled(){return this._enabled}set enabled(enabled){this._enabled=enabled,this.update()}get contextKey(){return this._contextKey}set contextKey(contextKey){this._contextKey=contextKey,this.update()}get busy(){return this._busy}set busy(busy){this._busy=busy,this.update()}get ignoreFocusOut(){return this._ignoreFocusOut}set ignoreFocusOut(ignoreFocusOut){const shouldUpdate=this._ignoreFocusOut!==ignoreFocusOut&&!platform/* isIOS */.gn;this._ignoreFocusOut=ignoreFocusOut&&!platform/* isIOS */.gn,shouldUpdate&&this.update()}get buttons(){return this._buttons}set buttons(buttons){this._buttons=buttons,this.buttonsUpdated=!0,this.update()}get validationMessage(){return this._validationMessage}set validationMessage(validationMessage){this._validationMessage=validationMessage,this.update()}get severity(){return this._severity}set severity(severity){this._severity=severity,this.update()}show(){this.visible||(this.visibleDisposables.add(this.ui.onDidTriggerButton((button=>{-1!==this.buttons.indexOf(button)&&this.onDidTriggerButtonEmitter.fire(button)}))),this.ui.show(this),
// update properties in the controller that get reset in the ui.show() call
this.visible=!0,
// This ensures the message/prompt gets rendered
this._lastValidationMessage=void 0,
// This ensures the input box has the right severity applied
this._lastSeverity=void 0,this.buttons.length&&(
// if there are buttons, the ui.show() clears them out of the UI so we should
// rerender them.
this.buttonsUpdated=!0),this.update())}hide(){this.visible&&this.ui.hide()}didHide(reason=quickInput/* QuickInputHideReason */.Jq.Other){this.visible=!1,this.visibleDisposables.clear(),this.onDidHideEmitter.fire({reason:reason})}update(){if(!this.visible)return;const title=this.getTitle();title&&this.ui.title.textContent!==title?this.ui.title.textContent=title:title||"&nbsp;"===this.ui.title.innerHTML||(this.ui.title.innerText=" ");const description=this.getDescription();if(this.ui.description1.textContent!==description&&(this.ui.description1.textContent=description),this.ui.description2.textContent!==description&&(this.ui.description2.textContent=description),this.busy&&!this.busyDelay&&(this.busyDelay=new common_async/* TimeoutTimer */._F,this.busyDelay.setIfNotSet((()=>{this.visible&&this.ui.progressBar.infinite()}),800)),!this.busy&&this.busyDelay&&(this.ui.progressBar.stop(),this.busyDelay.cancel(),this.busyDelay=void 0),this.buttonsUpdated){this.buttonsUpdated=!1,this.ui.leftActionBar.clear();const leftButtons=this.buttons.filter((button=>button===backButton));this.ui.leftActionBar.push(leftButtons.map(((button,index)=>{const action=new actions/* Action */.aU(`id-${index}`,"",button.iconClass||getIconClass(button.iconPath),!0,(()=>quickInput_awaiter(this,void 0,void 0,(function*(){this.onDidTriggerButtonEmitter.fire(button)}))));return action.tooltip=button.tooltip||"",action})),{icon:!0,label:!1}),this.ui.rightActionBar.clear();const rightButtons=this.buttons.filter((button=>button!==backButton));this.ui.rightActionBar.push(rightButtons.map(((button,index)=>{const action=new actions/* Action */.aU(`id-${index}`,"",button.iconClass||getIconClass(button.iconPath),!0,(()=>quickInput_awaiter(this,void 0,void 0,(function*(){this.onDidTriggerButtonEmitter.fire(button)}))));return action.tooltip=button.tooltip||"",action})),{icon:!0,label:!1})}this.ui.ignoreFocusOut=this.ignoreFocusOut,this.ui.setEnabled(this.enabled),this.ui.setContextKey(this.contextKey);const validationMessage=this.validationMessage||this.noValidationMessage;this._lastValidationMessage!==validationMessage&&(this._lastValidationMessage=validationMessage,dom/* reset */.mc(this.ui.message,...(0,iconLabels/* renderLabelWithIcons */.T)(validationMessage))),this._lastSeverity!==this.severity&&(this._lastSeverity=this.severity,this.showMessageDecoration(this.severity))}getTitle(){return this.title&&this.step?`${this.title} (${this.getSteps()})`:this.title?this.title:this.step?this.getSteps():""}getDescription(){return this.description||""}getSteps(){return this.step&&this.totalSteps?(0,nls/* localize */.N)("quickInput.steps","{0}/{1}",this.step,this.totalSteps):this.step?String(this.step):""}showMessageDecoration(severity){if(this.ui.inputBox.showDecoration(severity),severity!==common_severity/* default */.Z.Ignore){const styles=this.ui.inputBox.stylesForType(severity);this.ui.message.style.color=styles.foreground?`${styles.foreground}`:"",this.ui.message.style.backgroundColor=styles.background?`${styles.background}`:"",this.ui.message.style.border=styles.border?`1px solid ${styles.border}`:"",this.ui.message.style.paddingBottom="4px"}else this.ui.message.style.color="",this.ui.message.style.backgroundColor="",this.ui.message.style.border="",this.ui.message.style.paddingBottom=""}dispose(){this.hide(),this.onDisposeEmitter.fire(),super.dispose()}}QuickInput.noPromptMessage=(0,nls/* localize */.N)("inputModeEntry","Press 'Enter' to confirm your input or 'Escape' to cancel");class QuickPick extends QuickInput{constructor(){super(...arguments),this._value="",this.onDidChangeValueEmitter=this._register(new common_event/* Emitter */.Q5),this.onWillAcceptEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidAcceptEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidCustomEmitter=this._register(new common_event/* Emitter */.Q5),this._items=[],this.itemsUpdated=!1,this._canSelectMany=!1,this._canAcceptInBackground=!1,this._matchOnDescription=!1,this._matchOnDetail=!1,this._matchOnLabel=!0,this._sortByLabel=!0,this._autoFocusOnList=!0,this._keepScrollPosition=!1,this._itemActivation=this.ui.isScreenReaderOptimized()?quickInput/* ItemActivation */.jG.NONE/* https://github.com/microsoft/vscode/issues/57501 */:quickInput/* ItemActivation */.jG.FIRST,this._activeItems=[],this.activeItemsUpdated=!1,this.activeItemsToConfirm=[],this.onDidChangeActiveEmitter=this._register(new common_event/* Emitter */.Q5),this._selectedItems=[],this.selectedItemsUpdated=!1,this.selectedItemsToConfirm=[],this.onDidChangeSelectionEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidTriggerItemButtonEmitter=this._register(new common_event/* Emitter */.Q5),this.valueSelectionUpdated=!0,this._ok="default",this._customButton=!1,this.filterValue=value=>value,this.onDidChangeValue=this.onDidChangeValueEmitter.event,this.onWillAccept=this.onWillAcceptEmitter.event,this.onDidAccept=this.onDidAcceptEmitter.event,this.onDidChangeActive=this.onDidChangeActiveEmitter.event,this.onDidChangeSelection=this.onDidChangeSelectionEmitter.event,this.onDidTriggerItemButton=this.onDidTriggerItemButtonEmitter.event}get quickNavigate(){return this._quickNavigate}set quickNavigate(quickNavigate){this._quickNavigate=quickNavigate,this.update()}get value(){return this._value}set value(value){this._value!==value&&(this._value=value||"",this.update(),this.onDidChangeValueEmitter.fire(this._value))}set ariaLabel(ariaLabel){this._ariaLabel=ariaLabel,this.update()}get ariaLabel(){return this._ariaLabel}get placeholder(){return this._placeholder}set placeholder(placeholder){this._placeholder=placeholder,this.update()}get items(){return this._items}get scrollTop(){return this.ui.list.scrollTop}set scrollTop(scrollTop){this.ui.list.scrollTop=scrollTop}set items(items){this._items=items,this.itemsUpdated=!0,this.update()}get canSelectMany(){return this._canSelectMany}set canSelectMany(canSelectMany){this._canSelectMany=canSelectMany,this.update()}get canAcceptInBackground(){return this._canAcceptInBackground}set canAcceptInBackground(canAcceptInBackground){this._canAcceptInBackground=canAcceptInBackground}get matchOnDescription(){return this._matchOnDescription}set matchOnDescription(matchOnDescription){this._matchOnDescription=matchOnDescription,this.update()}get matchOnDetail(){return this._matchOnDetail}set matchOnDetail(matchOnDetail){this._matchOnDetail=matchOnDetail,this.update()}get matchOnLabel(){return this._matchOnLabel}set matchOnLabel(matchOnLabel){this._matchOnLabel=matchOnLabel,this.update()}get sortByLabel(){return this._sortByLabel}set sortByLabel(sortByLabel){this._sortByLabel=sortByLabel,this.update()}get autoFocusOnList(){return this._autoFocusOnList}set autoFocusOnList(autoFocusOnList){this._autoFocusOnList=autoFocusOnList,this.update()}get keepScrollPosition(){return this._keepScrollPosition}set keepScrollPosition(keepScrollPosition){this._keepScrollPosition=keepScrollPosition}get itemActivation(){return this._itemActivation}set itemActivation(itemActivation){this._itemActivation=itemActivation}get activeItems(){return this._activeItems}set activeItems(activeItems){this._activeItems=activeItems,this.activeItemsUpdated=!0,this.update()}get selectedItems(){return this._selectedItems}set selectedItems(selectedItems){this._selectedItems=selectedItems,this.selectedItemsUpdated=!0,this.update()}get keyMods(){return this._quickNavigate?quickInput/* NO_KEY_MODS */.X5:this.ui.keyMods}set valueSelection(valueSelection){this._valueSelection=valueSelection,this.valueSelectionUpdated=!0,this.update()}get customButton(){return this._customButton}set customButton(showCustomButton){this._customButton=showCustomButton,this.update()}get customLabel(){return this._customButtonLabel}set customLabel(label){this._customButtonLabel=label,this.update()}get customHover(){return this._customButtonHover}set customHover(hover){this._customButtonHover=hover,this.update()}get ok(){return this._ok}set ok(showOkButton){this._ok=showOkButton,this.update()}get hideInput(){return!!this._hideInput}set hideInput(hideInput){this._hideInput=hideInput,this.update()}trySelectFirst(){this.autoFocusOnList&&(this.canSelectMany||this.ui.list.focus(QuickInputListFocus.First))}show(){this.visible||(this.visibleDisposables.add(this.ui.inputBox.onDidChange((value=>{if(value===this.value)return;this._value=value;const didFilter=this.ui.list.filter(this.filterValue(this.ui.inputBox.value));didFilter&&this.trySelectFirst(),this.onDidChangeValueEmitter.fire(value)}))),this.visibleDisposables.add(this.ui.inputBox.onMouseDown((event=>{this.autoFocusOnList||this.ui.list.clearFocus()}))),this.visibleDisposables.add((this._hideInput?this.ui.list:this.ui.inputBox).onKeyDown((event=>{switch(event.keyCode){case 18/* DownArrow */:this.ui.list.focus(QuickInputListFocus.Next),this.canSelectMany&&this.ui.list.domFocus(),dom/* EventHelper */.zB.stop(event,!0);break;case 16/* UpArrow */:this.ui.list.getFocusedElements().length?this.ui.list.focus(QuickInputListFocus.Previous):this.ui.list.focus(QuickInputListFocus.Last),this.canSelectMany&&this.ui.list.domFocus(),dom/* EventHelper */.zB.stop(event,!0);break;case 12/* PageDown */:this.ui.list.focus(QuickInputListFocus.NextPage),this.canSelectMany&&this.ui.list.domFocus(),dom/* EventHelper */.zB.stop(event,!0);break;case 11/* PageUp */:this.ui.list.focus(QuickInputListFocus.PreviousPage),this.canSelectMany&&this.ui.list.domFocus(),dom/* EventHelper */.zB.stop(event,!0);break;case 17/* RightArrow */:if(!this._canAcceptInBackground)return;// needs to be enabled
if(!this.ui.inputBox.isSelectionAtEnd())return;// ensure input box selection at end
this.activeItems[0]&&(this._selectedItems=[this.activeItems[0]],this.onDidChangeSelectionEmitter.fire(this.selectedItems),this.handleAccept(!0));break;case 14/* Home */:!event.ctrlKey&&!event.metaKey||event.shiftKey||event.altKey||(this.ui.list.focus(QuickInputListFocus.First),dom/* EventHelper */.zB.stop(event,!0));break;case 13/* End */:!event.ctrlKey&&!event.metaKey||event.shiftKey||event.altKey||(this.ui.list.focus(QuickInputListFocus.Last),dom/* EventHelper */.zB.stop(event,!0));break}}))),this.visibleDisposables.add(this.ui.onDidAccept((()=>{this.canSelectMany?
// if there are no checked elements, it means that an onDidChangeSelection never fired to overwrite
// `_selectedItems`. In that case, we should emit one with an empty array to ensure that
// `.selectedItems` is up to date.
this.ui.list.getCheckedElements().length||(this._selectedItems=[],this.onDidChangeSelectionEmitter.fire(this.selectedItems)):this.activeItems[0]&&(
// For single-select, we set `selectedItems` to the item that was accepted.
this._selectedItems=[this.activeItems[0]],this.onDidChangeSelectionEmitter.fire(this.selectedItems)),this.handleAccept(!1)}))),this.visibleDisposables.add(this.ui.onDidCustom((()=>{this.onDidCustomEmitter.fire()}))),this.visibleDisposables.add(this.ui.list.onDidChangeFocus((focusedItems=>{this.activeItemsUpdated||this.activeItemsToConfirm!==this._activeItems&&(0,arrays/* equals */.fS)(focusedItems,this._activeItems,((a,b)=>a===b))||(this._activeItems=focusedItems,this.onDidChangeActiveEmitter.fire(focusedItems))}))),this.visibleDisposables.add(this.ui.list.onDidChangeSelection((({items:selectedItems,event:event})=>{this.canSelectMany?selectedItems.length&&this.ui.list.setSelectedElements([]):this.selectedItemsToConfirm!==this._selectedItems&&(0,arrays/* equals */.fS)(selectedItems,this._selectedItems,((a,b)=>a===b))||(this._selectedItems=selectedItems,this.onDidChangeSelectionEmitter.fire(selectedItems),selectedItems.length&&this.handleAccept(event instanceof MouseEvent&&1/* mouse middle click */===event.button))}))),this.visibleDisposables.add(this.ui.list.onChangedCheckedElements((checkedItems=>{this.canSelectMany&&(this.selectedItemsToConfirm!==this._selectedItems&&(0,arrays/* equals */.fS)(checkedItems,this._selectedItems,((a,b)=>a===b))||(this._selectedItems=checkedItems,this.onDidChangeSelectionEmitter.fire(checkedItems)))}))),this.visibleDisposables.add(this.ui.list.onButtonTriggered((event=>this.onDidTriggerItemButtonEmitter.fire(event)))),this.visibleDisposables.add(this.registerQuickNavigation()),this.valueSelectionUpdated=!0),super.show()}handleAccept(inBackground){
// Figure out veto via `onWillAccept` event
let veto=!1;this.onWillAcceptEmitter.fire({veto:()=>veto=!0}),
// Continue with `onDidAccept` if no veto
veto||this.onDidAcceptEmitter.fire({inBackground:inBackground})}registerQuickNavigation(){return dom/* addDisposableListener */.nm(this.ui.container,dom/* EventType */.tw.KEY_UP,(e=>{if(this.canSelectMany||!this._quickNavigate)return;const keyboardEvent=new browser_keyboardEvent/* StandardKeyboardEvent */.y(e),keyCode=keyboardEvent.keyCode,quickNavKeys=this._quickNavigate.keybindings,wasTriggerKeyPressed=quickNavKeys.some((k=>{const[firstPart,chordPart]=k.getParts();return!chordPart&&(firstPart.shiftKey&&4/* Shift */===keyCode?!(keyboardEvent.ctrlKey||keyboardEvent.altKey||keyboardEvent.metaKey):!(!firstPart.altKey||6/* Alt */!==keyCode)||(!(!firstPart.ctrlKey||5/* Ctrl */!==keyCode)||!(!firstPart.metaKey||57/* Meta */!==keyCode)))}));wasTriggerKeyPressed&&(this.activeItems[0]&&(this._selectedItems=[this.activeItems[0]],this.onDidChangeSelectionEmitter.fire(this.selectedItems),this.handleAccept(!1)),
// Unset quick navigate after press. It is only valid once
// and should not result in any behaviour change afterwards
// if the picker remains open because there was no active item
this._quickNavigate=void 0)}))}update(){if(!this.visible)return;
// store the scrollTop before it is reset
const scrollTopBefore=this.keepScrollPosition?this.scrollTop:0,hideInput=!!this._hideInput&&this._items.length>0;this.ui.container.classList.toggle("hidden-input",hideInput&&!this.description);const visibilities={title:!!this.title||!!this.step||!!this.buttons.length,description:!!this.description,checkAll:this.canSelectMany&&!this._hideCheckAll,checkBox:this.canSelectMany,inputBox:!hideInput,progressBar:!hideInput,visibleCount:!0,count:this.canSelectMany,ok:"default"===this.ok?this.canSelectMany:this.ok,list:!0,message:!!this.validationMessage,customButton:this.customButton};this.ui.setVisibilities(visibilities),super.update(),this.ui.inputBox.value!==this.value&&(this.ui.inputBox.value=this.value),this.valueSelectionUpdated&&(this.valueSelectionUpdated=!1,this.ui.inputBox.select(this._valueSelection&&{start:this._valueSelection[0],end:this._valueSelection[1]})),this.ui.inputBox.placeholder!==(this.placeholder||"")&&(this.ui.inputBox.placeholder=this.placeholder||"");const ariaLabel=this.ariaLabel||this.placeholder||QuickPick.DEFAULT_ARIA_LABEL;if(this.ui.inputBox.ariaLabel!==ariaLabel&&(this.ui.inputBox.ariaLabel=ariaLabel),this.ui.list.matchOnDescription=this.matchOnDescription,this.ui.list.matchOnDetail=this.matchOnDetail,this.ui.list.matchOnLabel=this.matchOnLabel,this.ui.list.sortByLabel=this.sortByLabel,this.itemsUpdated)switch(this.itemsUpdated=!1,this.ui.list.setElements(this.items),this.ui.list.filter(this.filterValue(this.ui.inputBox.value)),this.ui.checkAll.checked=this.ui.list.getAllVisibleChecked(),this.ui.visibleCount.setCount(this.ui.list.getVisibleCount()),this.ui.count.setCount(this.ui.list.getCheckedCount()),this._itemActivation){case quickInput/* ItemActivation */.jG.NONE:this._itemActivation=quickInput/* ItemActivation */.jG.FIRST;// only valid once, then unset
break;case quickInput/* ItemActivation */.jG.SECOND:this.ui.list.focus(QuickInputListFocus.Second),this._itemActivation=quickInput/* ItemActivation */.jG.FIRST;// only valid once, then unset
break;case quickInput/* ItemActivation */.jG.LAST:this.ui.list.focus(QuickInputListFocus.Last),this._itemActivation=quickInput/* ItemActivation */.jG.FIRST;// only valid once, then unset
break;default:this.trySelectFirst();break}this.ui.container.classList.contains("show-checkboxes")!==!!this.canSelectMany&&(this.canSelectMany?this.ui.list.clearFocus():this.trySelectFirst()),this.activeItemsUpdated&&(this.activeItemsUpdated=!1,this.activeItemsToConfirm=this._activeItems,this.ui.list.setFocusedElements(this.activeItems),this.activeItemsToConfirm===this._activeItems&&(this.activeItemsToConfirm=null)),this.selectedItemsUpdated&&(this.selectedItemsUpdated=!1,this.selectedItemsToConfirm=this._selectedItems,this.canSelectMany?this.ui.list.setCheckedElements(this.selectedItems):this.ui.list.setSelectedElements(this.selectedItems),this.selectedItemsToConfirm===this._selectedItems&&(this.selectedItemsToConfirm=null)),this.ui.customButton.label=this.customLabel||"",this.ui.customButton.element.title=this.customHover||"",this.ui.setComboboxAccessibility(!0),visibilities.inputBox||(
// we need to move focus into the tree to detect keybindings
// properly when the input box is not visible (quick nav)
this.ui.list.domFocus(),
// Focus the first element in the list if multiselect is enabled
this.canSelectMany&&this.ui.list.focus(QuickInputListFocus.First)),
// Set the scroll position to what it was before updating the items
this.keepScrollPosition&&(this.scrollTop=scrollTopBefore)}}QuickPick.DEFAULT_ARIA_LABEL=(0,nls/* localize */.N)("quickInputBox.ariaLabel","Type to narrow down results.");class QuickInputController extends lifecycle/* Disposable */.JT{constructor(options){super(),this.options=options,this.comboboxAccessibility=!1,this.enabled=!0,this.onDidAcceptEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidCustomEmitter=this._register(new common_event/* Emitter */.Q5),this.onDidTriggerButtonEmitter=this._register(new common_event/* Emitter */.Q5),this.keyMods={ctrlCmd:!1,alt:!1},this.controller=null,this.onShowEmitter=this._register(new common_event/* Emitter */.Q5),this.onShow=this.onShowEmitter.event,this.onHideEmitter=this._register(new common_event/* Emitter */.Q5),this.onHide=this.onHideEmitter.event,this.idPrefix=options.idPrefix,this.parentElement=options.container,this.styles=options.styles,this.registerKeyModsListeners()}registerKeyModsListeners(){const listener=e=>{this.keyMods.ctrlCmd=e.ctrlKey||e.metaKey,this.keyMods.alt=e.altKey};this._register(dom/* addDisposableListener */.nm(window,dom/* EventType */.tw.KEY_DOWN,listener,!0)),this._register(dom/* addDisposableListener */.nm(window,dom/* EventType */.tw.KEY_UP,listener,!0)),this._register(dom/* addDisposableListener */.nm(window,dom/* EventType */.tw.MOUSE_DOWN,listener,!0))}getUI(){if(this.ui)return this.ui;const container=dom/* append */.R3(this.parentElement,quickInput_$(".quick-input-widget.show-file-icons"));container.tabIndex=-1,container.style.display="none";const styleSheet=dom/* createStyleSheet */.dS(container),titleBar=dom/* append */.R3(container,quickInput_$(".quick-input-titlebar")),leftActionBar=this._register(new actionbar/* ActionBar */.o(titleBar));leftActionBar.domNode.classList.add("quick-input-left-action-bar");const title=dom/* append */.R3(titleBar,quickInput_$(".quick-input-title")),rightActionBar=this._register(new actionbar/* ActionBar */.o(titleBar));rightActionBar.domNode.classList.add("quick-input-right-action-bar");const description1=dom/* append */.R3(container,quickInput_$(".quick-input-description")),headerContainer=dom/* append */.R3(container,quickInput_$(".quick-input-header")),checkAll=dom/* append */.R3(headerContainer,quickInput_$("input.quick-input-check-all"));checkAll.type="checkbox",this._register(dom/* addStandardDisposableListener */.mu(checkAll,dom/* EventType */.tw.CHANGE,(e=>{const checked=checkAll.checked;list.setAllVisibleChecked(checked)}))),this._register(dom/* addDisposableListener */.nm(checkAll,dom/* EventType */.tw.CLICK,(e=>{(e.x||e.y)&&// Avoid 'click' triggered by 'space'...
inputBox.setFocus()})));const description2=dom/* append */.R3(headerContainer,quickInput_$(".quick-input-description")),extraContainer=dom/* append */.R3(headerContainer,quickInput_$(".quick-input-and-message")),filterContainer=dom/* append */.R3(extraContainer,quickInput_$(".quick-input-filter")),inputBox=this._register(new QuickInputBox(filterContainer));inputBox.setAttribute("aria-describedby",`${this.idPrefix}message`);const visibleCountContainer=dom/* append */.R3(filterContainer,quickInput_$(".quick-input-visible-count"));visibleCountContainer.setAttribute("aria-live","polite"),visibleCountContainer.setAttribute("aria-atomic","true");const visibleCount=new countBadge/* CountBadge */.Z(visibleCountContainer,{countFormat:(0,nls/* localize */.N)({key:"quickInput.visibleCount",comment:["This tells the user how many items are shown in a list of items to select from. The items can be anything. Currently not visible, but read by screen readers."]},"{0} Results")}),countContainer=dom/* append */.R3(filterContainer,quickInput_$(".quick-input-count"));countContainer.setAttribute("aria-live","polite");const count=new countBadge/* CountBadge */.Z(countContainer,{countFormat:(0,nls/* localize */.N)({key:"quickInput.countSelected",comment:["This tells the user how many items are selected in a list of items to select from. The items can be anything."]},"{0} Selected")}),okContainer=dom/* append */.R3(headerContainer,quickInput_$(".quick-input-action")),ok=new button_button/* Button */.z(okContainer);ok.label=(0,nls/* localize */.N)("ok","OK"),this._register(ok.onDidClick((e=>{this.onDidAcceptEmitter.fire()})));const customButtonContainer=dom/* append */.R3(headerContainer,quickInput_$(".quick-input-action")),customButton=new button_button/* Button */.z(customButtonContainer);customButton.label=(0,nls/* localize */.N)("custom","Custom"),this._register(customButton.onDidClick((e=>{this.onDidCustomEmitter.fire()})));const message=dom/* append */.R3(extraContainer,quickInput_$(`#${this.idPrefix}message.quick-input-message`)),list=this._register(new QuickInputList(container,this.idPrefix+"list",this.options));this._register(list.onChangedAllVisibleChecked((checked=>{checkAll.checked=checked}))),this._register(list.onChangedVisibleCount((c=>{visibleCount.setCount(c)}))),this._register(list.onChangedCheckedCount((c=>{count.setCount(c)}))),this._register(list.onLeave((()=>{
// Defer to avoid the input field reacting to the triggering key.
setTimeout((()=>{inputBox.setFocus(),this.controller instanceof QuickPick&&this.controller.canSelectMany&&list.clearFocus()}),0)}))),this._register(list.onDidChangeFocus((()=>{this.comboboxAccessibility&&this.getUI().inputBox.setAttribute("aria-activedescendant",this.getUI().list.getActiveDescendant()||"")})));const progressBar=new progressbar/* ProgressBar */.k(container);progressBar.getContainer().classList.add("quick-input-progress");const focusTracker=dom/* trackFocus */.go(container);return this._register(focusTracker),this._register(dom/* addDisposableListener */.nm(container,dom/* EventType */.tw.FOCUS,(e=>{this.previousFocusElement=e.relatedTarget instanceof HTMLElement?e.relatedTarget:void 0}),!0)),this._register(focusTracker.onDidBlur((()=>{this.getUI().ignoreFocusOut||this.options.ignoreFocusOut()||this.hide(quickInput/* QuickInputHideReason */.Jq.Blur),this.previousFocusElement=void 0}))),this._register(dom/* addDisposableListener */.nm(container,dom/* EventType */.tw.FOCUS,(e=>{inputBox.setFocus()}))),this._register(dom/* addDisposableListener */.nm(container,dom/* EventType */.tw.KEY_DOWN,(e=>{const event=new browser_keyboardEvent/* StandardKeyboardEvent */.y(e);switch(event.keyCode){case 3/* Enter */:dom/* EventHelper */.zB.stop(e,!0),this.onDidAcceptEmitter.fire();break;case 9/* Escape */:dom/* EventHelper */.zB.stop(e,!0),this.hide(quickInput/* QuickInputHideReason */.Jq.Gesture);break;case 2/* Tab */:if(!event.altKey&&!event.ctrlKey&&!event.metaKey){const selectors=[".action-label.codicon"];container.classList.contains("show-checkboxes")?selectors.push("input"):selectors.push("input[type=text]"),this.getUI().list.isDisplayed()&&selectors.push(".monaco-list");const stops=container.querySelectorAll(selectors.join(", "));event.shiftKey&&event.target===stops[0]?(dom/* EventHelper */.zB.stop(e,!0),stops[stops.length-1].focus()):event.shiftKey||event.target!==stops[stops.length-1]||(dom/* EventHelper */.zB.stop(e,!0),stops[0].focus())}break}}))),this.ui={container:container,styleSheet:styleSheet,leftActionBar:leftActionBar,titleBar:titleBar,title:title,description1:description1,description2:description2,rightActionBar:rightActionBar,checkAll:checkAll,filterContainer:filterContainer,inputBox:inputBox,visibleCountContainer:visibleCountContainer,visibleCount:visibleCount,countContainer:countContainer,count:count,okContainer:okContainer,ok:ok,message:message,customButtonContainer:customButtonContainer,customButton:customButton,list:list,progressBar:progressBar,onDidAccept:this.onDidAcceptEmitter.event,onDidCustom:this.onDidCustomEmitter.event,onDidTriggerButton:this.onDidTriggerButtonEmitter.event,ignoreFocusOut:!1,keyMods:this.keyMods,isScreenReaderOptimized:()=>this.options.isScreenReaderOptimized(),show:controller=>this.show(controller),hide:()=>this.hide(),setVisibilities:visibilities=>this.setVisibilities(visibilities),setComboboxAccessibility:enabled=>this.setComboboxAccessibility(enabled),setEnabled:enabled=>this.setEnabled(enabled),setContextKey:contextKey=>this.options.setContextKey(contextKey)},this.updateStyles(),this.ui}pick(picks,options={},token=cancellation/* CancellationToken */.T.None){return new Promise(((doResolve,reject)=>{let resolve=result=>{resolve=doResolve,options.onKeyMods&&options.onKeyMods(input.keyMods),doResolve(result)};if(token.isCancellationRequested)return void resolve(void 0);const input=this.createQuickPick();let activeItem;const disposables=[input,input.onDidAccept((()=>{if(input.canSelectMany)resolve(input.selectedItems.slice()),input.hide();else{const result=input.activeItems[0];result&&(resolve(result),input.hide())}})),input.onDidChangeActive((items=>{const focused=items[0];focused&&options.onDidFocus&&options.onDidFocus(focused)})),input.onDidChangeSelection((items=>{if(!input.canSelectMany){const result=items[0];result&&(resolve(result),input.hide())}})),input.onDidTriggerItemButton((event=>options.onDidTriggerItemButton&&options.onDidTriggerItemButton(Object.assign(Object.assign({},event),{removeItem:()=>{const index=input.items.indexOf(event.item);if(-1!==index){const items=input.items.slice(),removed=items.splice(index,1),activeItems=input.activeItems.filter((activeItem=>activeItem!==removed[0])),keepScrollPositionBefore=input.keepScrollPosition;input.keepScrollPosition=!0,input.items=items,activeItems&&(input.activeItems=activeItems),input.keepScrollPosition=keepScrollPositionBefore}}})))),input.onDidChangeValue((value=>{!activeItem||value||1===input.activeItems.length&&input.activeItems[0]===activeItem||(input.activeItems=[activeItem])})),token.onCancellationRequested((()=>{input.hide()})),input.onDidHide((()=>{(0,lifecycle/* dispose */.B9)(disposables),resolve(void 0)}))];input.title=options.title,input.canSelectMany=!!options.canPickMany,input.placeholder=options.placeHolder,input.ignoreFocusOut=!!options.ignoreFocusLost,input.matchOnDescription=!!options.matchOnDescription,input.matchOnDetail=!!options.matchOnDetail,input.matchOnLabel=void 0===options.matchOnLabel||options.matchOnLabel,// default to true
input.autoFocusOnList=void 0===options.autoFocusOnList||options.autoFocusOnList,// default to true
input.quickNavigate=options.quickNavigate,input.contextKey=options.contextKey,input.busy=!0,Promise.all([picks,options.activeItem]).then((([items,_activeItem])=>{activeItem=_activeItem,input.busy=!1,input.items=items,input.canSelectMany&&(input.selectedItems=items.filter((item=>"separator"!==item.type&&item.picked))),activeItem&&(input.activeItems=[activeItem])})),input.show(),Promise.resolve(picks).then(void 0,(err=>{reject(err),input.hide()}))}))}createQuickPick(){const ui=this.getUI();return new QuickPick(ui)}show(controller){const ui=this.getUI();this.onShowEmitter.fire();const oldController=this.controller;this.controller=controller,oldController&&oldController.didHide(),this.setEnabled(!0),ui.leftActionBar.clear(),ui.title.textContent="",ui.description1.textContent="",ui.description2.textContent="",ui.rightActionBar.clear(),ui.checkAll.checked=!1,
// ui.inputBox.value = ''; Avoid triggering an event.
ui.inputBox.placeholder="",ui.inputBox.password=!1,ui.inputBox.showDecoration(common_severity/* default */.Z.Ignore),ui.visibleCount.setCount(0),ui.count.setCount(0),dom/* reset */.mc(ui.message),ui.progressBar.stop(),ui.list.setElements([]),ui.list.matchOnDescription=!1,ui.list.matchOnDetail=!1,ui.list.matchOnLabel=!0,ui.list.sortByLabel=!0,ui.ignoreFocusOut=!1,this.setComboboxAccessibility(!1),ui.inputBox.ariaLabel="";const backKeybindingLabel=this.options.backKeybindingLabel();backButton.tooltip=backKeybindingLabel?(0,nls/* localize */.N)("quickInput.backWithKeybinding","Back ({0})",backKeybindingLabel):(0,nls/* localize */.N)("quickInput.back","Back"),ui.container.style.display="",this.updateLayout(),ui.inputBox.setFocus()}setVisibilities(visibilities){const ui=this.getUI();ui.title.style.display=visibilities.title?"":"none",ui.description1.style.display=visibilities.description&&(visibilities.inputBox||visibilities.checkAll)?"":"none",ui.description2.style.display=!visibilities.description||visibilities.inputBox||visibilities.checkAll?"none":"",ui.checkAll.style.display=visibilities.checkAll?"":"none",ui.filterContainer.style.display=visibilities.inputBox?"":"none",ui.visibleCountContainer.style.display=visibilities.visibleCount?"":"none",ui.countContainer.style.display=visibilities.count?"":"none",ui.okContainer.style.display=visibilities.ok?"":"none",ui.customButtonContainer.style.display=visibilities.customButton?"":"none",ui.message.style.display=visibilities.message?"":"none",ui.progressBar.getContainer().style.display=visibilities.progressBar?"":"none",ui.list.display(!!visibilities.list),ui.container.classList[visibilities.checkBox?"add":"remove"]("show-checkboxes"),this.updateLayout()}setComboboxAccessibility(enabled){if(enabled!==this.comboboxAccessibility){const ui=this.getUI();this.comboboxAccessibility=enabled,this.comboboxAccessibility?(ui.inputBox.setAttribute("role","combobox"),ui.inputBox.setAttribute("aria-haspopup","true"),ui.inputBox.setAttribute("aria-autocomplete","list"),ui.inputBox.setAttribute("aria-activedescendant",ui.list.getActiveDescendant()||"")):(ui.inputBox.removeAttribute("role"),ui.inputBox.removeAttribute("aria-haspopup"),ui.inputBox.removeAttribute("aria-autocomplete"),ui.inputBox.removeAttribute("aria-activedescendant"))}}setEnabled(enabled){if(enabled!==this.enabled){this.enabled=enabled;for(const item of this.getUI().leftActionBar.viewItems)item.getAction().enabled=enabled;for(const item of this.getUI().rightActionBar.viewItems)item.getAction().enabled=enabled;this.getUI().checkAll.disabled=!enabled,
// this.getUI().inputBox.enabled = enabled; Avoid loosing focus.
this.getUI().ok.enabled=enabled,this.getUI().list.enabled=enabled}}hide(reason){var _a;const controller=this.controller;if(controller){const focusChanged=!(null===(_a=this.ui)||void 0===_a?void 0:_a.container.contains(document.activeElement));this.controller=null,this.onHideEmitter.fire(),this.getUI().container.style.display="none",focusChanged||(this.previousFocusElement&&this.previousFocusElement.offsetParent?(this.previousFocusElement.focus(),this.previousFocusElement=void 0):this.options.returnFocus()),controller.didHide(reason)}}layout(dimension,titleBarOffset){this.dimension=dimension,this.titleBarOffset=titleBarOffset,this.updateLayout()}updateLayout(){if(this.ui){this.ui.container.style.top=`${this.titleBarOffset}px`;const style=this.ui.container.style,width=Math.min(.62/* golden cut */*this.dimension.width,QuickInputController.MAX_WIDTH);style.width=width+"px",style.marginLeft="-"+width/2+"px",this.ui.inputBox.layout(),this.ui.list.layout(this.dimension&&.4*this.dimension.height)}}applyStyles(styles){this.styles=styles,this.updateStyles()}updateStyles(){if(this.ui){const{quickInputTitleBackground:quickInputTitleBackground,quickInputBackground:quickInputBackground,quickInputForeground:quickInputForeground,contrastBorder:contrastBorder,widgetShadow:widgetShadow}=this.styles.widget;this.ui.titleBar.style.backgroundColor=quickInputTitleBackground?quickInputTitleBackground.toString():"",this.ui.container.style.backgroundColor=quickInputBackground?quickInputBackground.toString():"",this.ui.container.style.color=quickInputForeground?quickInputForeground.toString():"",this.ui.container.style.border=contrastBorder?`1px solid ${contrastBorder}`:"",this.ui.container.style.boxShadow=widgetShadow?`0 0 8px 2px ${widgetShadow}`:"",this.ui.inputBox.style(this.styles.inputBox),this.ui.count.style(this.styles.countBadge),this.ui.ok.style(this.styles.button),this.ui.customButton.style(this.styles.button),this.ui.progressBar.style(this.styles.progressBar),this.ui.list.style(this.styles.list);const content=[];this.styles.list.pickerGroupBorder&&content.push(`.quick-input-list .quick-input-list-entry { border-top-color:  ${this.styles.list.pickerGroupBorder}; }`),this.styles.list.pickerGroupForeground&&content.push(`.quick-input-list .quick-input-list-separator { color:  ${this.styles.list.pickerGroupForeground}; }`),(this.styles.keybindingLabel.keybindingLabelBackground||this.styles.keybindingLabel.keybindingLabelBorder||this.styles.keybindingLabel.keybindingLabelBottomBorder||this.styles.keybindingLabel.keybindingLabelShadow||this.styles.keybindingLabel.keybindingLabelForeground)&&(content.push(".quick-input-list .monaco-keybinding > .monaco-keybinding-key {"),this.styles.keybindingLabel.keybindingLabelBackground&&content.push(`background-color: ${this.styles.keybindingLabel.keybindingLabelBackground};`),this.styles.keybindingLabel.keybindingLabelBorder&&
// Order matters here. `border-color` must come before `border-bottom-color`.
content.push(`border-color: ${this.styles.keybindingLabel.keybindingLabelBorder};`),this.styles.keybindingLabel.keybindingLabelBottomBorder&&content.push(`border-bottom-color: ${this.styles.keybindingLabel.keybindingLabelBottomBorder};`),this.styles.keybindingLabel.keybindingLabelShadow&&content.push(`box-shadow: inset 0 -1px 0 ${this.styles.keybindingLabel.keybindingLabelShadow};`),this.styles.keybindingLabel.keybindingLabelForeground&&content.push(`color: ${this.styles.keybindingLabel.keybindingLabelForeground};`),content.push("}"));const newStyles=content.join("\n");newStyles!==this.ui.styleSheet.textContent&&(this.ui.styleSheet.textContent=newStyles)}}}QuickInputController.MAX_WIDTH=600},
/***/867746:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Jq:function(){/* binding */return QuickInputHideReason},
/* harmony export */X5:function(){/* binding */return NO_KEY_MODS},
/* harmony export */jG:function(){/* binding */return ItemActivation}
/* harmony export */});
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
const NO_KEY_MODS={ctrlCmd:!1,alt:!1};var QuickInputHideReason,ItemActivation;(function(QuickInputHideReason){
/**
     * Focus moved away from the quick input.
     */
QuickInputHideReason[QuickInputHideReason["Blur"]=1]="Blur",
/**
     * An explicit user gesture, e.g. pressing Escape key.
     */
QuickInputHideReason[QuickInputHideReason["Gesture"]=2]="Gesture",
/**
     * Anything else.
     */
QuickInputHideReason[QuickInputHideReason["Other"]=3]="Other"})(QuickInputHideReason||(QuickInputHideReason={})),function(ItemActivation){ItemActivation[ItemActivation["NONE"]=0]="NONE",ItemActivation[ItemActivation["FIRST"]=1]="FIRST",ItemActivation[ItemActivation["SECOND"]=2]="SECOND",ItemActivation[ItemActivation["LAST"]=3]="LAST"}(ItemActivation||(ItemActivation={}))},
/***/210748:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */K:function(){/* binding */return Storage},
/* harmony export */W:function(){/* binding */return InMemoryStorageDatabase}
/* harmony export */});
/* harmony import */var StorageState,_common_async_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(715393),_common_event_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(104669),_common_lifecycle_js__WEBPACK_IMPORTED_MODULE_2__=__webpack_require__(905976),_common_types_js__WEBPACK_IMPORTED_MODULE_3__=__webpack_require__(998401),__awaiter=function(thisArg,_arguments,P,generator){function adopt(value){return value instanceof P?value:new P((function(resolve){resolve(value)}))}return new(P||(P=Promise))((function(resolve,reject){function fulfilled(value){try{step(generator.next(value))}catch(e){reject(e)}}function rejected(value){try{step(generator["throw"](value))}catch(e){reject(e)}}function step(result){result.done?resolve(result.value):adopt(result.value).then(fulfilled,rejected)}step((generator=generator.apply(thisArg,_arguments||[])).next())}))};
/* harmony import */(function(StorageState){StorageState[StorageState["None"]=0]="None",StorageState[StorageState["Initialized"]=1]="Initialized",StorageState[StorageState["Closed"]=2]="Closed"})(StorageState||(StorageState={}));class Storage extends _common_lifecycle_js__WEBPACK_IMPORTED_MODULE_2__/* .Disposable */.JT{constructor(database,options=Object.create(null)){super(),this.database=database,this.options=options,this._onDidChangeStorage=this._register(new _common_event_js__WEBPACK_IMPORTED_MODULE_1__/* .Emitter */.Q5),this.onDidChangeStorage=this._onDidChangeStorage.event,this.state=StorageState.None,this.cache=new Map,this.flushDelayer=new _common_async_js__WEBPACK_IMPORTED_MODULE_0__/* .ThrottledDelayer */.rH(Storage.DEFAULT_FLUSH_DELAY),this.pendingDeletes=new Set,this.pendingInserts=new Map,this.whenFlushedCallbacks=[],this.registerListeners()}registerListeners(){this._register(this.database.onDidChangeItemsExternal((e=>this.onDidChangeItemsExternal(e))))}onDidChangeItemsExternal(e){var _a,_b;
// items that change external require us to update our
// caches with the values. we just accept the value and
// emit an event if there is a change.
null===(_a=e.changed)||void 0===_a||_a.forEach(((value,key)=>this.accept(key,value))),null===(_b=e.deleted)||void 0===_b||_b.forEach((key=>this.accept(key,void 0)))}accept(key,value){if(this.state===StorageState.Closed)return;// Return early if we are already closed
let changed=!1;
// Item got removed, check for deletion
if((0,_common_types_js__WEBPACK_IMPORTED_MODULE_3__/* .isUndefinedOrNull */.Jp)(value))changed=this.cache.delete(key);else{const currentValue=this.cache.get(key);currentValue!==value&&(this.cache.set(key,value),changed=!0)}
// Signal to outside listeners
changed&&this._onDidChangeStorage.fire(key)}get(key,fallbackValue){const value=this.cache.get(key);return(0,_common_types_js__WEBPACK_IMPORTED_MODULE_3__/* .isUndefinedOrNull */.Jp)(value)?fallbackValue:value}getBoolean(key,fallbackValue){const value=this.get(key);return(0,_common_types_js__WEBPACK_IMPORTED_MODULE_3__/* .isUndefinedOrNull */.Jp)(value)?fallbackValue:"true"===value}getNumber(key,fallbackValue){const value=this.get(key);return(0,_common_types_js__WEBPACK_IMPORTED_MODULE_3__/* .isUndefinedOrNull */.Jp)(value)?fallbackValue:parseInt(value,10)}set(key,value){return __awaiter(this,void 0,void 0,(function*(){if(this.state===StorageState.Closed)return;// Return early if we are already closed
// We remove the key for undefined/null values
if((0,_common_types_js__WEBPACK_IMPORTED_MODULE_3__/* .isUndefinedOrNull */.Jp)(value))return this.delete(key);
// Otherwise, convert to String and store
const valueStr=String(value),currentValue=this.cache.get(key);
// Return early if value already set
return currentValue!==valueStr?(
// Update in cache and pending
this.cache.set(key,valueStr),this.pendingInserts.set(key,valueStr),this.pendingDeletes.delete(key),
// Event
this._onDidChangeStorage.fire(key),this.flushDelayer.trigger((()=>this.flushPending()))):void 0}))}delete(key){return __awaiter(this,void 0,void 0,(function*(){if(this.state===StorageState.Closed)return;// Return early if we are already closed
// Remove from cache and add to pending
const wasDeleted=this.cache.delete(key);return wasDeleted?(this.pendingDeletes.has(key)||this.pendingDeletes.add(key),this.pendingInserts.delete(key),
// Event
this._onDidChangeStorage.fire(key),this.flushDelayer.trigger((()=>this.flushPending()))):void 0}))}get hasPending(){return this.pendingInserts.size>0||this.pendingDeletes.size>0}flushPending(){return __awaiter(this,void 0,void 0,(function*(){if(!this.hasPending)return;// return early if nothing to do
// Get pending data
const updateRequest={insert:this.pendingInserts,delete:this.pendingDeletes};
// Reset pending data for next run
// Update in storage and release any
// waiters we have once done
return this.pendingDeletes=new Set,this.pendingInserts=new Map,this.database.updateItems(updateRequest).finally((()=>{var _a;if(!this.hasPending)while(this.whenFlushedCallbacks.length)null===(_a=this.whenFlushedCallbacks.pop())||void 0===_a||_a()}))}))}dispose(){this.flushDelayer.dispose(),super.dispose()}}Storage.DEFAULT_FLUSH_DELAY=100;class InMemoryStorageDatabase{constructor(){this.onDidChangeItemsExternal=_common_event_js__WEBPACK_IMPORTED_MODULE_1__/* .Event */.ju.None,this.items=new Map}updateItems(request){return __awaiter(this,void 0,void 0,(function*(){request.insert&&request.insert.forEach(((value,key)=>this.items.set(key,value))),request.delete&&request.delete.forEach((key=>this.items.delete(key)))}))}}
/***/},
/***/517549:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */H:function(){/* binding */return DefaultWorkerFactory}
/* harmony export */});
/* harmony import */var _a,_common_platform_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(901432),_common_worker_simpleWorker_js__WEBPACK_IMPORTED_MODULE_1__=__webpack_require__(318352);
/* harmony import */const ttPolicy=null===(_a=window.trustedTypes)||void 0===_a?void 0:_a.createPolicy("defaultWorkerFactory",{createScriptURL:value=>value});function getWorker(workerId,label){
// Option for hosts to overwrite the worker script (used in the standalone editor)
if(_common_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.MonacoEnvironment){if("function"===typeof _common_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.MonacoEnvironment.getWorker)return _common_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.MonacoEnvironment.getWorker(workerId,label);if("function"===typeof _common_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.MonacoEnvironment.getWorkerUrl){const workerUrl=_common_platform_js__WEBPACK_IMPORTED_MODULE_0__/* .globals */.li.MonacoEnvironment.getWorkerUrl(workerId,label);return new Worker(ttPolicy?ttPolicy.createScriptURL(workerUrl):workerUrl,{name:label})}}
// ESM-comment-begin
// 	if (typeof require === 'function') {
// 		// check if the JS lives on a different origin
// 		const workerMain = require.toUrl('./' + workerId); // explicitly using require.toUrl(), see https://github.com/microsoft/vscode/issues/107440#issuecomment-698982321
// 		const workerUrl = getWorkerBootstrapUrl(workerMain, label);
// 		return new Worker(ttPolicy ? ttPolicy.createScriptURL(workerUrl) as unknown as string : workerUrl, { name: label });
// 	}
// ESM-comment-end
throw new Error("You must define a function MonacoEnvironment.getWorkerUrl or MonacoEnvironment.getWorker")}
// ESM-comment-begin
// export function getWorkerBootstrapUrl(scriptPath: string, label: string): string {
// 	if (/^((http:)|(https:)|(file:))/.test(scriptPath) && scriptPath.substring(0, self.origin.length) !== self.origin) {
// 		// this is the cross-origin case
// 		// i.e. the webpage is running at a different origin than where the scripts are loaded from
// 		const myPath = 'vs/base/worker/defaultWorkerFactory.js';
// 		const workerBaseUrl = require.toUrl(myPath).slice(0, -myPath.length); // explicitly using require.toUrl(), see https://github.com/microsoft/vscode/issues/107440#issuecomment-698982321
// 		const js = `/*${label}*/self.MonacoEnvironment={baseUrl: '${workerBaseUrl}'};const ttPolicy = self.trustedTypes?.createPolicy('defaultWorkerFactory', { createScriptURL: value => value });importScripts(ttPolicy?.createScriptURL('${scriptPath}') ?? '${scriptPath}');/*${label}*/`;
// 		const blob = new Blob([js], { type: 'application/javascript' });
// 		return URL.createObjectURL(blob);
// 	}
// 	return scriptPath + '#' + label;
// }
// ESM-comment-end
function isPromiseLike(obj){return"function"===typeof obj.then}
/**
 * A worker that uses HTML5 web workers so that is has
 * its own global scope and its own thread.
 */class WebWorker{constructor(moduleId,id,label,onMessageCallback,onErrorCallback){this.id=id;const workerOrPromise=getWorker("workerMain.js",label);isPromiseLike(workerOrPromise)?this.worker=workerOrPromise:this.worker=Promise.resolve(workerOrPromise),this.postMessage(moduleId,[]),this.worker.then((w=>{w.onmessage=function(ev){onMessageCallback(ev.data)},w.onmessageerror=onErrorCallback,"function"===typeof w.addEventListener&&w.addEventListener("error",onErrorCallback)}))}getId(){return this.id}postMessage(message,transfer){this.worker&&this.worker.then((w=>w.postMessage(message,transfer)))}dispose(){this.worker&&this.worker.then((w=>w.terminate())),this.worker=null}}class DefaultWorkerFactory{constructor(label){this._label=label,this._webWorkerFailedBeforeError=!1}create(moduleId,onMessageCallback,onErrorCallback){let workerId=++DefaultWorkerFactory.LAST_WORKER_ID;if(this._webWorkerFailedBeforeError)throw this._webWorkerFailedBeforeError;return new WebWorker(moduleId,workerId,this._label||"anonymous"+workerId,onMessageCallback,(err=>{(0,_common_worker_simpleWorker_js__WEBPACK_IMPORTED_MODULE_1__/* .logOnceWebWorkerWarning */.HZ)(err),this._webWorkerFailedBeforeError=err,onErrorCallback(err)}))}}DefaultWorkerFactory.LAST_WORKER_ID=0},
/***/289587:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */Mj:function(){/* reexport safe */return _editor_editor_api_js__WEBPACK_IMPORTED_MODULE_0__.languages}
/* harmony export */});
/* harmony import */var _editor_editor_api_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(506586);
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
/***/},
/***/690780:
/***/function(__unused_webpack_module,__unused_webpack___webpack_exports__,__webpack_require__){
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/editor/editor.api.js
__webpack_require__(506586);
// EXTERNAL MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/fillers/monaco-editor-core.js
var monaco_editor_core=__webpack_require__(289587),languageDefinitions={},lazyLanguageLoaders={},LazyLanguageLoader=/** @class */function(){function LazyLanguageLoader(languageId){var _this=this;this._languageId=languageId,this._loadingTriggered=!1,this._lazyLoadPromise=new Promise((function(resolve,reject){_this._lazyLoadPromiseResolve=resolve,_this._lazyLoadPromiseReject=reject}))}return LazyLanguageLoader.getOrCreate=function(languageId){return lazyLanguageLoaders[languageId]||(lazyLanguageLoaders[languageId]=new LazyLanguageLoader(languageId)),lazyLanguageLoaders[languageId]},LazyLanguageLoader.prototype.whenLoaded=function(){return this._lazyLoadPromise},LazyLanguageLoader.prototype.load=function(){var _this=this;return this._loadingTriggered||(this._loadingTriggered=!0,languageDefinitions[this._languageId].loader().then((function(mod){return _this._lazyLoadPromiseResolve(mod)}),(function(err){return _this._lazyLoadPromiseReject(err)}))),this._lazyLoadPromise},LazyLanguageLoader}();function registerLanguage(def){var languageId=def.id;languageDefinitions[languageId]=def,monaco_editor_core/* languages */.Mj.register(def);var lazyLanguageLoader=LazyLanguageLoader.getOrCreate(languageId);monaco_editor_core/* languages */.Mj.setMonarchTokensProvider(languageId,lazyLanguageLoader.whenLoaded().then((function(mod){return mod.language}))),monaco_editor_core/* languages */.Mj.onLanguage(languageId,(function(){lazyLanguageLoader.load().then((function(mod){monaco_editor_core/* languages */.Mj.setLanguageConfiguration(languageId,mod.conf)}))}))}// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/abap/abap.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"abap",extensions:[".abap"],aliases:["abap","ABAP"],loader:function(){return __webpack_require__.e(/* import() */40848).then(__webpack_require__.bind(__webpack_require__,240848))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/apex/apex.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"apex",extensions:[".cls"],aliases:["Apex","apex"],mimetypes:["text/x-apex-source","text/x-apex"],loader:function(){return __webpack_require__.e(/* import() */54386).then(__webpack_require__.bind(__webpack_require__,754386))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/azcli/azcli.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"azcli",extensions:[".azcli"],aliases:["Azure CLI","azcli"],loader:function(){return __webpack_require__.e(/* import() */31471).then(__webpack_require__.bind(__webpack_require__,831471))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/bat/bat.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"bat",extensions:[".bat",".cmd"],aliases:["Batch","bat"],loader:function(){return __webpack_require__.e(/* import() */84129).then(__webpack_require__.bind(__webpack_require__,84129))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/bicep/bicep.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"bicep",extensions:[".bicep"],aliases:["Bicep"],loader:function(){return __webpack_require__.e(/* import() */47131).then(__webpack_require__.bind(__webpack_require__,847131))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/cameligo/cameligo.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"cameligo",extensions:[".mligo"],aliases:["Cameligo"],loader:function(){return __webpack_require__.e(/* import() */11448).then(__webpack_require__.bind(__webpack_require__,911448))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/clojure/clojure.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"clojure",extensions:[".clj",".cljs",".cljc",".edn"],aliases:["clojure","Clojure"],loader:function(){return __webpack_require__.e(/* import() */33036).then(__webpack_require__.bind(__webpack_require__,33036))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/coffee/coffee.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"coffeescript",extensions:[".coffee"],aliases:["CoffeeScript","coffeescript","coffee"],mimetypes:["text/x-coffeescript","text/coffeescript"],loader:function(){return __webpack_require__.e(/* import() */21147).then(__webpack_require__.bind(__webpack_require__,621147))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/cpp/cpp.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"c",extensions:[".c",".h"],aliases:["C","c"],loader:function(){return __webpack_require__.e(/* import() */71960).then(__webpack_require__.bind(__webpack_require__,671960))}}),registerLanguage({id:"cpp",extensions:[".cpp",".cc",".cxx",".hpp",".hh",".hxx"],aliases:["C++","Cpp","cpp"],loader:function(){return __webpack_require__.e(/* import() */71960).then(__webpack_require__.bind(__webpack_require__,671960))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/csharp/csharp.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"csharp",extensions:[".cs",".csx",".cake"],aliases:["C#","csharp"],loader:function(){return __webpack_require__.e(/* import() */18719).then(__webpack_require__.bind(__webpack_require__,518719))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/csp/csp.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"csp",extensions:[],aliases:["CSP","csp"],loader:function(){return __webpack_require__.e(/* import() */68946).then(__webpack_require__.bind(__webpack_require__,668946))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/css/css.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"css",extensions:[".css"],aliases:["CSS","css"],mimetypes:["text/css"],loader:function(){return __webpack_require__.e(/* import() */62075).then(__webpack_require__.bind(__webpack_require__,62075))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/dart/dart.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"dart",extensions:[".dart"],aliases:["Dart","dart"],mimetypes:["text/x-dart-source","text/x-dart"],loader:function(){return __webpack_require__.e(/* import() */39343).then(__webpack_require__.bind(__webpack_require__,139343))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/dockerfile/dockerfile.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"dockerfile",extensions:[".dockerfile"],filenames:["Dockerfile"],aliases:["Dockerfile"],loader:function(){return __webpack_require__.e(/* import() */25849).then(__webpack_require__.bind(__webpack_require__,425849))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/ecl/ecl.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"ecl",extensions:[".ecl"],aliases:["ECL","Ecl","ecl"],loader:function(){return __webpack_require__.e(/* import() */12814).then(__webpack_require__.bind(__webpack_require__,712814))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/elixir/elixir.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"elixir",extensions:[".ex",".exs"],aliases:["Elixir","elixir","ex"],loader:function(){return __webpack_require__.e(/* import() */92240).then(__webpack_require__.bind(__webpack_require__,392240))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/flow9/flow9.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"flow9",extensions:[".flow"],aliases:["Flow9","Flow","flow9","flow"],loader:function(){return __webpack_require__.e(/* import() */14188).then(__webpack_require__.bind(__webpack_require__,914188))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/fsharp/fsharp.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"fsharp",extensions:[".fs",".fsi",".ml",".mli",".fsx",".fsscript"],aliases:["F#","FSharp","fsharp"],loader:function(){return __webpack_require__.e(/* import() */96241).then(__webpack_require__.bind(__webpack_require__,196241))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/go/go.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"go",extensions:[".go"],aliases:["Go"],loader:function(){return __webpack_require__.e(/* import() */80249).then(__webpack_require__.bind(__webpack_require__,480249))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/graphql/graphql.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"graphql",extensions:[".graphql",".gql"],aliases:["GraphQL","graphql","gql"],mimetypes:["application/graphql"],loader:function(){return __webpack_require__.e(/* import() */66489).then(__webpack_require__.bind(__webpack_require__,766489))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/handlebars/handlebars.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"handlebars",extensions:[".handlebars",".hbs"],aliases:["Handlebars","handlebars","hbs"],mimetypes:["text/x-handlebars-template"],loader:function(){return __webpack_require__.e(/* import() */15703).then(__webpack_require__.bind(__webpack_require__,615703))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/hcl/hcl.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"hcl",extensions:[".tf",".tfvars",".hcl"],aliases:["Terraform","tf","HCL","hcl"],loader:function(){return __webpack_require__.e(/* import() */53632).then(__webpack_require__.bind(__webpack_require__,953632))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/html/html.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"html",extensions:[".html",".htm",".shtml",".xhtml",".mdoc",".jsp",".asp",".aspx",".jshtm"],aliases:["HTML","htm","html","xhtml"],mimetypes:["text/html","text/x-jshtm","text/template","text/ng-template"],loader:function(){return __webpack_require__.e(/* import() */2571).then(__webpack_require__.bind(__webpack_require__,102571))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/ini/ini.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"ini",extensions:[".ini",".properties",".gitconfig"],filenames:["config",".gitattributes",".gitconfig",".editorconfig"],aliases:["Ini","ini"],loader:function(){return __webpack_require__.e(/* import() */52798).then(__webpack_require__.bind(__webpack_require__,252798))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/java/java.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"java",extensions:[".java",".jav"],aliases:["Java","java"],mimetypes:["text/x-java-source","text/x-java"],loader:function(){return __webpack_require__.e(/* import() */17043).then(__webpack_require__.bind(__webpack_require__,17043))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/javascript/javascript.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"javascript",extensions:[".js",".es6",".jsx",".mjs"],firstLine:"^#!.*\\bnode",filenames:["jakefile"],aliases:["JavaScript","javascript","js"],mimetypes:["text/javascript"],loader:function(){return Promise.all(/* import() */[__webpack_require__.e(93208),__webpack_require__.e(50232),__webpack_require__.e(93765),__webpack_require__.e(95953),__webpack_require__.e(35309),__webpack_require__.e(18778),__webpack_require__.e(12303),__webpack_require__.e(84290),__webpack_require__.e(31721),__webpack_require__.e(64418),__webpack_require__.e(5603),__webpack_require__.e(57432),__webpack_require__.e(435),__webpack_require__.e(72230),__webpack_require__.e(94255),__webpack_require__.e(67683),__webpack_require__.e(44178),__webpack_require__.e(91132),__webpack_require__.e(20280),__webpack_require__.e(78256),__webpack_require__.e(22200),__webpack_require__.e(49081),__webpack_require__.e(89472),__webpack_require__.e(12085),__webpack_require__.e(55754),__webpack_require__.e(86494),__webpack_require__.e(3037),__webpack_require__.e(49908),__webpack_require__.e(22061),__webpack_require__.e(31602),__webpack_require__.e(73609),__webpack_require__.e(41134)]).then(__webpack_require__.bind(__webpack_require__,241134))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/julia/julia.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"julia",extensions:[".jl"],aliases:["julia","Julia"],loader:function(){return __webpack_require__.e(/* import() */34946).then(__webpack_require__.bind(__webpack_require__,234946))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/kotlin/kotlin.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"kotlin",extensions:[".kt"],aliases:["Kotlin","kotlin"],mimetypes:["text/x-kotlin-source","text/x-kotlin"],loader:function(){return __webpack_require__.e(/* import() */84368).then(__webpack_require__.bind(__webpack_require__,784368))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/less/less.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"less",extensions:[".less"],aliases:["Less","less"],mimetypes:["text/x-less","text/less"],loader:function(){return __webpack_require__.e(/* import() */35593).then(__webpack_require__.bind(__webpack_require__,135593))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/lexon/lexon.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"lexon",extensions:[".lex"],aliases:["Lexon"],loader:function(){return __webpack_require__.e(/* import() */64912).then(__webpack_require__.bind(__webpack_require__,364912))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/lua/lua.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"lua",extensions:[".lua"],aliases:["Lua","lua"],loader:function(){return __webpack_require__.e(/* import() */20911).then(__webpack_require__.bind(__webpack_require__,620911))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/liquid/liquid.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"liquid",extensions:[".liquid",".html.liquid"],aliases:["Liquid","liquid"],mimetypes:["application/liquid"],loader:function(){return __webpack_require__.e(/* import() */94028).then(__webpack_require__.bind(__webpack_require__,694028))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/m3/m3.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"m3",extensions:[".m3",".i3",".mg",".ig"],aliases:["Modula-3","Modula3","modula3","m3"],loader:function(){return __webpack_require__.e(/* import() */38906).then(__webpack_require__.bind(__webpack_require__,38906))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/markdown/markdown.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"markdown",extensions:[".md",".markdown",".mdown",".mkdn",".mkd",".mdwn",".mdtxt",".mdtext"],aliases:["Markdown","markdown"],loader:function(){return __webpack_require__.e(/* import() */42954).then(__webpack_require__.bind(__webpack_require__,242954))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/mips/mips.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"mips",extensions:[".s"],aliases:["MIPS","MIPS-V"],mimetypes:["text/x-mips","text/mips","text/plaintext"],loader:function(){return __webpack_require__.e(/* import() */60854).then(__webpack_require__.bind(__webpack_require__,560854))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/msdax/msdax.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"msdax",extensions:[".dax",".msdax"],aliases:["DAX","MSDAX"],loader:function(){return __webpack_require__.e(/* import() */79398).then(__webpack_require__.bind(__webpack_require__,179398))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/mysql/mysql.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"mysql",extensions:[],aliases:["MySQL","mysql"],loader:function(){return __webpack_require__.e(/* import() */31961).then(__webpack_require__.bind(__webpack_require__,531961))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/objective-c/objective-c.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"objective-c",extensions:[".m"],aliases:["Objective-C"],loader:function(){return __webpack_require__.e(/* import() */79537).then(__webpack_require__.bind(__webpack_require__,779537))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/pascal/pascal.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"pascal",extensions:[".pas",".p",".pp"],aliases:["Pascal","pas"],mimetypes:["text/x-pascal-source","text/x-pascal"],loader:function(){return __webpack_require__.e(/* import() */86082).then(__webpack_require__.bind(__webpack_require__,86082))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/pascaligo/pascaligo.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"pascaligo",extensions:[".ligo"],aliases:["Pascaligo","ligo"],loader:function(){return __webpack_require__.e(/* import() */98084).then(__webpack_require__.bind(__webpack_require__,298084))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/perl/perl.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"perl",extensions:[".pl"],aliases:["Perl","pl"],loader:function(){return __webpack_require__.e(/* import() */8070).then(__webpack_require__.bind(__webpack_require__,508070))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/pgsql/pgsql.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"pgsql",extensions:[],aliases:["PostgreSQL","postgres","pg","postgre"],loader:function(){return __webpack_require__.e(/* import() */20996).then(__webpack_require__.bind(__webpack_require__,120996))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/php/php.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"php",extensions:[".php",".php4",".php5",".phtml",".ctp"],aliases:["PHP","php"],mimetypes:["application/x-php"],loader:function(){return __webpack_require__.e(/* import() */47835).then(__webpack_require__.bind(__webpack_require__,347835))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/pla/pla.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"pla",extensions:[".pla"],loader:function(){return __webpack_require__.e(/* import() */23682).then(__webpack_require__.bind(__webpack_require__,523682))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/postiats/postiats.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"postiats",extensions:[".dats",".sats",".hats"],aliases:["ATS","ATS/Postiats"],loader:function(){return __webpack_require__.e(/* import() */48180).then(__webpack_require__.bind(__webpack_require__,548180))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/powerquery/powerquery.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"powerquery",extensions:[".pq",".pqm"],aliases:["PQ","M","Power Query","Power Query M"],loader:function(){return __webpack_require__.e(/* import() */94407).then(__webpack_require__.bind(__webpack_require__,794407))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/powershell/powershell.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"powershell",extensions:[".ps1",".psm1",".psd1"],aliases:["PowerShell","powershell","ps","ps1"],loader:function(){return __webpack_require__.e(/* import() */37562).then(__webpack_require__.bind(__webpack_require__,747410))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/protobuf/protobuf.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"proto",extensions:[".proto"],aliases:["protobuf","Protocol Buffers"],loader:function(){return __webpack_require__.e(/* import() */63760).then(__webpack_require__.bind(__webpack_require__,263760))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/pug/pug.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"pug",extensions:[".jade",".pug"],aliases:["Pug","Jade","jade"],loader:function(){return __webpack_require__.e(/* import() */22892).then(__webpack_require__.bind(__webpack_require__,22892))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/python/python.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"python",extensions:[".py",".rpy",".pyw",".cpy",".gyp",".gypi"],aliases:["Python","py"],firstLine:"^#!/.*\\bpython[0-9.-]*\\b",loader:function(){return __webpack_require__.e(/* import() */37287).then(__webpack_require__.bind(__webpack_require__,337287))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/qsharp/qsharp.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"qsharp",extensions:[".qs"],aliases:["Q#","qsharp"],loader:function(){return __webpack_require__.e(/* import() */69400).then(__webpack_require__.bind(__webpack_require__,269400))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/r/r.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"r",extensions:[".r",".rhistory",".rmd",".rprofile",".rt"],aliases:["R","r"],loader:function(){return __webpack_require__.e(/* import() */22140).then(__webpack_require__.bind(__webpack_require__,822140))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/razor/razor.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"razor",extensions:[".cshtml"],aliases:["Razor","razor"],mimetypes:["text/x-cshtml"],loader:function(){return __webpack_require__.e(/* import() */76424).then(__webpack_require__.bind(__webpack_require__,376424))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/redis/redis.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"redis",extensions:[".redis"],aliases:["redis"],loader:function(){return __webpack_require__.e(/* import() */91259).then(__webpack_require__.bind(__webpack_require__,891259))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/redshift/redshift.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"redshift",extensions:[],aliases:["Redshift","redshift"],loader:function(){return __webpack_require__.e(/* import() */56449).then(__webpack_require__.bind(__webpack_require__,456449))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/restructuredtext/restructuredtext.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"restructuredtext",extensions:[".rst"],aliases:["reStructuredText","restructuredtext"],loader:function(){return __webpack_require__.e(/* import() */71065).then(__webpack_require__.bind(__webpack_require__,971065))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/ruby/ruby.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"ruby",extensions:[".rb",".rbx",".rjs",".gemspec",".pp"],filenames:["rakefile","Gemfile"],aliases:["Ruby","rb"],loader:function(){return __webpack_require__.e(/* import() */69684).then(__webpack_require__.bind(__webpack_require__,469684))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/rust/rust.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"rust",extensions:[".rs",".rlib"],aliases:["Rust","rust"],loader:function(){return __webpack_require__.e(/* import() */8715).then(__webpack_require__.bind(__webpack_require__,308715))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/sb/sb.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"sb",extensions:[".sb"],aliases:["Small Basic","sb"],loader:function(){return __webpack_require__.e(/* import() */95062).then(__webpack_require__.bind(__webpack_require__,95062))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/scala/scala.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"scala",extensions:[".scala",".sc",".sbt"],aliases:["Scala","scala","SBT","Sbt","sbt","Dotty","dotty"],mimetypes:["text/x-scala-source","text/x-scala","text/x-sbt","text/x-dotty"],loader:function(){return __webpack_require__.e(/* import() */90180).then(__webpack_require__.bind(__webpack_require__,790180))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/scheme/scheme.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"scheme",extensions:[".scm",".ss",".sch",".rkt"],aliases:["scheme","Scheme"],loader:function(){return __webpack_require__.e(/* import() */32060).then(__webpack_require__.bind(__webpack_require__,432060))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/scss/scss.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"scss",extensions:[".scss"],aliases:["Sass","sass","scss"],mimetypes:["text/x-scss","text/scss"],loader:function(){return __webpack_require__.e(/* import() */90525).then(__webpack_require__.bind(__webpack_require__,790525))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/shell/shell.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"shell",extensions:[".sh",".bash"],aliases:["Shell","sh"],loader:function(){return __webpack_require__.e(/* import() */88670).then(__webpack_require__.bind(__webpack_require__,388670))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/solidity/solidity.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"sol",extensions:[".sol"],aliases:["sol","solidity","Solidity"],loader:function(){return __webpack_require__.e(/* import() */1156).then(__webpack_require__.bind(__webpack_require__,401156))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/sophia/sophia.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"aes",extensions:[".aes"],aliases:["aes","sophia","Sophia"],loader:function(){return __webpack_require__.e(/* import() */63919).then(__webpack_require__.bind(__webpack_require__,263919))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/sparql/sparql.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"sparql",extensions:[".rq"],aliases:["sparql","SPARQL"],loader:function(){return __webpack_require__.e(/* import() */85962).then(__webpack_require__.bind(__webpack_require__,585962))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/sql/sql.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"sql",extensions:[".sql"],aliases:["SQL"],loader:function(){return __webpack_require__.e(/* import() */27778).then(__webpack_require__.bind(__webpack_require__,727778))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/st/st.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"st",extensions:[".st",".iecst",".iecplc",".lc3lib"],aliases:["StructuredText","scl","stl"],loader:function(){return __webpack_require__.e(/* import() */86587).then(__webpack_require__.bind(__webpack_require__,586587))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/swift/swift.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"swift",aliases:["Swift","swift"],extensions:[".swift"],mimetypes:["text/swift"],loader:function(){return __webpack_require__.e(/* import() */42911).then(__webpack_require__.bind(__webpack_require__,942911))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/systemverilog/systemverilog.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"systemverilog",extensions:[".sv",".svh"],aliases:["SV","sv","SystemVerilog","systemverilog"],loader:function(){return __webpack_require__.e(/* import() */81886).then(__webpack_require__.bind(__webpack_require__,81886))}}),registerLanguage({id:"verilog",extensions:[".v",".vh"],aliases:["V","v","Verilog","verilog"],loader:function(){return __webpack_require__.e(/* import() */81886).then(__webpack_require__.bind(__webpack_require__,81886))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/tcl/tcl.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"tcl",extensions:[".tcl"],aliases:["tcl","Tcl","tcltk","TclTk","tcl/tk","Tcl/Tk"],loader:function(){return __webpack_require__.e(/* import() */57637).then(__webpack_require__.bind(__webpack_require__,457637))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/twig/twig.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"twig",extensions:[".twig"],aliases:["Twig","twig"],mimetypes:["text/x-twig"],loader:function(){return __webpack_require__.e(/* import() */98424).then(__webpack_require__.bind(__webpack_require__,598424))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/typescript/typescript.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"typescript",extensions:[".ts",".tsx"],aliases:["TypeScript","ts","typescript"],mimetypes:["text/typescript"],loader:function(){return Promise.all(/* import() */[__webpack_require__.e(93208),__webpack_require__.e(50232),__webpack_require__.e(93765),__webpack_require__.e(95953),__webpack_require__.e(35309),__webpack_require__.e(18778),__webpack_require__.e(12303),__webpack_require__.e(84290),__webpack_require__.e(31721),__webpack_require__.e(64418),__webpack_require__.e(5603),__webpack_require__.e(57432),__webpack_require__.e(435),__webpack_require__.e(72230),__webpack_require__.e(94255),__webpack_require__.e(67683),__webpack_require__.e(44178),__webpack_require__.e(91132),__webpack_require__.e(20280),__webpack_require__.e(78256),__webpack_require__.e(22200),__webpack_require__.e(49081),__webpack_require__.e(89472),__webpack_require__.e(12085),__webpack_require__.e(55754),__webpack_require__.e(86494),__webpack_require__.e(3037),__webpack_require__.e(49908),__webpack_require__.e(22061),__webpack_require__.e(31602),__webpack_require__.e(73609)]).then(__webpack_require__.bind(__webpack_require__,896717))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/vb/vb.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"vb",extensions:[".vb"],aliases:["Visual Basic","vb"],loader:function(){return __webpack_require__.e(/* import() */39907).then(__webpack_require__.bind(__webpack_require__,939907))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/xml/xml.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"xml",extensions:[".xml",".dtd",".ascx",".csproj",".config",".wxi",".wxl",".wxs",".xaml",".svg",".svgz",".opf",".xsl"],firstLine:"(\\<\\?xml.*)|(\\<svg)|(\\<\\!doctype\\s+svg)",aliases:["XML","xml"],mimetypes:["text/xml","application/xml","application/xaml+xml","application/xml-dtd"],loader:function(){return __webpack_require__.e(/* import() */4902).then(__webpack_require__.bind(__webpack_require__,104902))}}),// CONCATENATED MODULE: ./node_modules/monaco-editor/esm/vs/basic-languages/yaml/yaml.contribution.js
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
registerLanguage({id:"yaml",extensions:[".yaml",".yml"],aliases:["YAML","yaml","YML","yml"],mimetypes:["application/x-yaml","text/x-yaml"],loader:function(){return __webpack_require__.e(/* import() */23585).then(__webpack_require__.bind(__webpack_require__,623585))}})},
/***/896717:
/***/function(__unused_webpack_module,__webpack_exports__,__webpack_require__){__webpack_require__.r(__webpack_exports__),
/* harmony export */__webpack_require__.d(__webpack_exports__,{
/* harmony export */conf:function(){/* binding */return conf},
/* harmony export */language:function(){/* binding */return language}
/* harmony export */});
/* harmony import */var _fillers_monaco_editor_core_js__WEBPACK_IMPORTED_MODULE_0__=__webpack_require__(289587),conf={wordPattern:/(-?\d*\.\d\w*)|([^\`\~\!\@\#\%\^\&\*\(\)\-\=\+\[\{\]\}\\\|\;\:\'\"\,\.\<\>\/\?\s]+)/g,comments:{lineComment:"//",blockComment:["/*","*/"]},brackets:[["{","}"],["[","]"],["(",")"]],onEnterRules:[{
// e.g. /** | */
beforeText:/^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,afterText:/^\s*\*\/$/,action:{indentAction:_fillers_monaco_editor_core_js__WEBPACK_IMPORTED_MODULE_0__/* .languages */.Mj.IndentAction.IndentOutdent,appendText:" * "}},{
// e.g. /** ...|
beforeText:/^\s*\/\*\*(?!\/)([^\*]|\*(?!\/))*$/,action:{indentAction:_fillers_monaco_editor_core_js__WEBPACK_IMPORTED_MODULE_0__/* .languages */.Mj.IndentAction.None,appendText:" * "}},{
// e.g.  * ...|
beforeText:/^(\t|(\ \ ))*\ \*(\ ([^\*]|\*(?!\/))*)?$/,action:{indentAction:_fillers_monaco_editor_core_js__WEBPACK_IMPORTED_MODULE_0__/* .languages */.Mj.IndentAction.None,appendText:"* "}},{
// e.g.  */|
beforeText:/^(\t|(\ \ ))*\ \*\/\s*$/,action:{indentAction:_fillers_monaco_editor_core_js__WEBPACK_IMPORTED_MODULE_0__/* .languages */.Mj.IndentAction.None,removeText:1}}],autoClosingPairs:[{open:"{",close:"}"},{open:"[",close:"]"},{open:"(",close:")"},{open:'"',close:'"',notIn:["string"]},{open:"'",close:"'",notIn:["string","comment"]},{open:"`",close:"`",notIn:["string","comment"]},{open:"/**",close:" */",notIn:["string"]}],folding:{markers:{start:new RegExp("^\\s*//\\s*#?region\\b"),end:new RegExp("^\\s*//\\s*#?endregion\\b")}}},language={
// Set defaultToken to invalid to see what you do not tokenize yet
defaultToken:"invalid",tokenPostfix:".ts",keywords:[
// Should match the keys of textToKeywordObj in
// https://github.com/microsoft/TypeScript/blob/master/src/compiler/scanner.ts
"abstract","any","as","asserts","bigint","boolean","break","case","catch","class","continue","const","constructor","debugger","declare","default","delete","do","else","enum","export","extends","false","finally","for","from","function","get","if","implements","import","in","infer","instanceof","interface","is","keyof","let","module","namespace","never","new","null","number","object","package","private","protected","public","override","readonly","require","global","return","set","static","string","super","switch","symbol","this","throw","true","try","type","typeof","undefined","unique","unknown","var","void","while","with","yield","async","await","of"],operators:["<=",">=","==","!=","===","!==","=>","+","-","**","*","/","%","++","--","<<","</",">>",">>>","&","|","^","!","~","&&","||","??","?",":","=","+=","-=","*=","**=","/=","%=","<<=",">>=",">>>=","&=","|=","^=","@"],
// we include these common regular expressions
symbols:/[=><!~?:&|+\-*\/\^%]+/,escapes:/\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,digits:/\d+(_+\d+)*/,octaldigits:/[0-7]+(_+[0-7]+)*/,binarydigits:/[0-1]+(_+[0-1]+)*/,hexdigits:/[[0-9a-fA-F]+(_+[0-9a-fA-F]+)*/,regexpctl:/[(){}\[\]\$\^|\-*+?\.]/,regexpesc:/\\(?:[bBdDfnrstvwWn0\\\/]|@regexpctl|c[A-Z]|x[0-9a-fA-F]{2}|u[0-9a-fA-F]{4})/,
// The main tokenizer for our languages
tokenizer:{root:[[/[{}]/,"delimiter.bracket"],{include:"common"}],common:[
// identifiers and keywords
[/[a-z_$][\w$]*/,{cases:{"@keywords":"keyword","@default":"identifier"}}],[/[A-Z][\w\$]*/,"type.identifier"],
// [/[A-Z][\w\$]*/, 'identifier'],
// whitespace
{include:"@whitespace"},
// regular expression: ensure it is terminated before beginning (otherwise it is an opeator)
[/\/(?=([^\\\/]|\\.)+\/([dgimsuy]*)(\s*)(\.|;|,|\)|\]|\}|$))/,{token:"regexp",bracket:"@open",next:"@regexp"}],
// delimiters and operators
[/[()\[\]]/,"@brackets"],[/[<>](?!@symbols)/,"@brackets"],[/!(?=([^=]|$))/,"delimiter"],[/@symbols/,{cases:{"@operators":"delimiter","@default":""}}],
// numbers
[/(@digits)[eE]([\-+]?(@digits))?/,"number.float"],[/(@digits)\.(@digits)([eE][\-+]?(@digits))?/,"number.float"],[/0[xX](@hexdigits)n?/,"number.hex"],[/0[oO]?(@octaldigits)n?/,"number.octal"],[/0[bB](@binarydigits)n?/,"number.binary"],[/(@digits)n?/,"number"],
// delimiter: after number because of .\d floats
[/[;,.]/,"delimiter"],
// strings
[/"([^"\\]|\\.)*$/,"string.invalid"],[/'([^'\\]|\\.)*$/,"string.invalid"],[/"/,"string","@string_double"],[/'/,"string","@string_single"],[/`/,"string","@string_backtick"]],whitespace:[[/[ \t\r\n]+/,""],[/\/\*\*(?!\/)/,"comment.doc","@jsdoc"],[/\/\*/,"comment","@comment"],[/\/\/.*$/,"comment"]],comment:[[/[^\/*]+/,"comment"],[/\*\//,"comment","@pop"],[/[\/*]/,"comment"]],jsdoc:[[/[^\/*]+/,"comment.doc"],[/\*\//,"comment.doc","@pop"],[/[\/*]/,"comment.doc"]],
// We match regular expression quite precisely
regexp:[[/(\{)(\d+(?:,\d*)?)(\})/,["regexp.escape.control","regexp.escape.control","regexp.escape.control"]],[/(\[)(\^?)(?=(?:[^\]\\\/]|\\.)+)/,["regexp.escape.control",{token:"regexp.escape.control",next:"@regexrange"}]],[/(\()(\?:|\?=|\?!)/,["regexp.escape.control","regexp.escape.control"]],[/[()]/,"regexp.escape.control"],[/@regexpctl/,"regexp.escape.control"],[/[^\\\/]/,"regexp"],[/@regexpesc/,"regexp.escape"],[/\\\./,"regexp.invalid"],[/(\/)([dgimsuy]*)/,[{token:"regexp",bracket:"@close",next:"@pop"},"keyword.other"]]],regexrange:[[/-/,"regexp.escape.control"],[/\^/,"regexp.invalid"],[/@regexpesc/,"regexp.escape"],[/[^\]]/,"regexp"],[/\]/,{token:"regexp.escape.control",next:"@pop",bracket:"@close"}]],string_double:[[/[^\\"]+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/"/,"string","@pop"]],string_single:[[/[^\\']+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/'/,"string","@pop"]],string_backtick:[[/\$\{/,{token:"delimiter.bracket",next:"@bracketCounting"}],[/[^\\`$]+/,"string"],[/@escapes/,"string.escape"],[/\\./,"string.escape.invalid"],[/`/,"string","@pop"]],bracketCounting:[[/\{/,"delimiter.bracket","@bracketCounting"],[/\}/,"delimiter.bracket","@pop"],{include:"common"}]}};
/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/}}]);
//# sourceMappingURL=http://localhost:5050/sourcemaps/js/chunk-common-b7831af0.js.map