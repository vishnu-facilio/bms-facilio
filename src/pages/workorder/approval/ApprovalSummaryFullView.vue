<template>
  <div id="summarylayout">
    <div class="subheader-preventive-summary">
      <div class="subheader-block">
        <div class="flRight" style="padding-top:25px;padding-right:25px">
          <approve-reject
            v-if="workorder.moduleState && workorder.moduleState.id > 0"
            :record="workorder"
            moduleName="workorder"
            :transformFn="transformFormData"
            updateUrl="/v2/workorders/update"
            @transitionSuccess="fetchWo"
          >
            <div slot="waiting" class="fc-summary-chip">
              {{ $t('maintenance.approval.waiting_for_approval') }}
            </div>
          </approve-reject>
        </div>

        <div
          class="summary-back"
          style="text-transform: uppercase;"
          @click="back"
        >
          <i class="el-icon-back"></i>
          {{ $t('maintenance._workorder.back') }}
        </div>

        <div class="summary-title-container">
          <div class="summary-title-block wo-title-block">
            <div class="summary-title">
              <i
                v-if="!canEdit"
                class="fa fa-lock locked-wo"
                data-arrow="true"
                :title="$t('common.products.waiting_for_approval')"
                v-tippy
              ></i>
              {{ workorder.subject }}
              <span
                v-if="workorder.urgency > 0"
                class="fc-tag-urgent mL20"
                :style="{ background: urgencyColorCode[workorder.urgency] }"
              >
                {{ $constants.WO_URGENCY[workorder.urgency] }}</span
              >
            </div>
          </div>
        </div>
        <div
          style="font-size: 13px;position:relative;clear:both;top: 4px;left: 0px;"
        >
          <span class="fc-id bold">#{{ workorder.serialNumber }}</span>
          <span v-if="workorder.sourceType" class="separator">|</span>
          <template>
            <span style="color: #8ca1ad; padding-right: 5px;">
              <i v-if="workorder.sourceType === 2" class="fa fa-envelope-o"></i>
              <i
                v-else-if="workorder.sourceType === 10"
                class="f14 fa fa-globe"
              ></i>
              <i
                v-else-if="workorder.sourceType === 1"
                class="f14 el-icon-service approval-el-icon-service"
              ></i>
              <i
                v-else-if="workorder.sourceType === 4"
                class="f14 el-icon-bell approval-el-icon-bell"
              ></i>
              <i
                v-else-if="workorder.sourceType === 5"
                class="f14 el-icon-date approval-el-icon-date"
              ></i>
            </span>
            <span
              v-if="workorder.requester"
              style="color: #2ea2b2; font-size: 13px;vertical-align: middle;padding-right: 8px;padding-left: 5px;"
              >{{ workorder.requester.name }}</span
            >
          </template>
          <div class="clearboth"></div>
        </div>
      </div>
    </div>
    <el-row style="margin:20px">
      <el-col
        style="margin-right:20px;"
        v-bind:class="{ width66: !showAddTask, width56: showAddTask }"
      >
        <div
          class="scrollable"
          style="background:white;height:80vh;padding-bottom: 50px;"
        >
          <div
            class="fc-summary2"
            style="padding: 6px;padding-left: 15px;    border-bottom: 1px solid #eeeeee;"
          >
            <div class="row" style="position:relative;padding-bottom: 6px;">
              <div
                style="padding-top: 14px;"
                v-bind:class="{ tabselected: subSection === 'summarypage' }"
                class="tabel-selector subtitle"
                @click="
                  selectSubSection('summarypage')
                  closedisplay()
                "
              >
                {{ $t('common._common._summary') }}
                <div
                  style="position: absolute;margin-top: 12px;"
                  v-bind:class="{ selectionbar: subSection === 'summarypage' }"
                ></div>
              </div>
              <div
                v-if="subSection === 'taskpage'"
                style="position:absolute;right:30px;top:25%"
              >
                <el-button
                  class="fc-summary-chip"
                  style="text-align: left;"
                  :disabled="!allTaskStatus"
                  @click="closeAllTask"
                  >{{ $t('maintenance._workorder.close_all_task') }}
                </el-button>
              </div>
              <div class="clearboth"></div>
            </div>
          </div>
          <div
            v-if="subSection === 'summarypage'"
            style="margin-top: 32px;margin-left: 0px;"
          >
            <div class="flLeft">
              <div
                style="padding-left: 30px;font-size: 13px;color: #39b2c2 !important;letter-spacing: 0.4px;"
              >
                #{{ workorder.serialNumber }}
              </div>
              <div
                style="padding-left: 30px;font-weight:normal;padding-top: 5px;"
                class="coltitle"
              >
                {{ workorder.subject ? workorder.subject : '---' }}
              </div>
            </div>
            <div
              v-if="workorder.requester && workorder.requester.name"
              class="flRight"
              style="margin-right: 30px;"
            >
              <avatar
                size="lg"
                :user="{ name: workorder.requester.name }"
              ></avatar>
            </div>
            <div class="flRight" style="margin-right: 8px;">
              <div
                v-if="workorder.requester && workorder.requester.name"
                style="text-align: right;padding-top: 3px;"
              >
                {{ workorder.requester.name }}
              </div>
            </div>
            <div class="clearboth"></div>
            <div class="row" style="padding-left: 30px;    padding-top: 8px;">
              <span
                class="coldisc"
                style="max-width: 100%;white-space: pre-wrap;"
                >{{ workorder.description }}</span
              >
            </div>
            <div style="padding-top: 50px;">
              <span
                class="commenttitle"
                style="padding-bottom:10px;padding-left:30px;"
                >{{ $t('maintenance.wr_list.comments') }}</span
              >
            </div>
            <div
              style="padding-top:10px; border-bottom:1px solid #f0f0f0;margin-right: 30px;margin-left: 30px;"
            ></div>
            <div
              style="margin-left: 30px;width: 70%;"
              v-bind:class="{ mT20: comments.length > 0 }"
            >
              <div
                class="fc-comment-row col-12"
                style="padding-bottom: 30px;"
                v-for="comment in comments"
                :key="comment.id"
              >
                <div class="flLeft">
                  <avatar
                    size="md"
                    :user="{ name: comment.createdBy.name }"
                  ></avatar>
                </div>
                <div
                  class="flLeft"
                  style="margin-top: 7px;padding-left: 10px;max-width: 90%;"
                >
                  <div class="flex-middle">
                    <div class="flLeft">
                      <span
                        style="font-size:13px; font-weight:500; letter-spacing:0.2px;"
                        >{{ comment.createdBy.name }}</span
                      >
                    </div>
                    <div
                      class="flLeft"
                      style="width: 6px;height: 6px;background: rgb(216, 216, 216);border-radius: 50%;margin-left: 10px;"
                    ></div>
                    <div
                      style="color:#8c8c8c; font-size:13px;letter-spacing: 0.2px;    padding-left: 11px;"
                      class="flLeft pL5 secondary-color f12 comment-time col-4"
                    >
                      {{ comment.createdTime | fromNow }}
                    </div>
                    <div class="clearboth"></div>
                  </div>
                  <div style="padding-top: 5px;">
                    <div
                      style="font-size: 13px;font-weight: normal;letter-spacing: 0.2px;color: #333333;    white-space: pre-line;"
                    >
                      {{ comment.body }}
                    </div>
                  </div>
                </div>
                <div class="clearboth"></div>
              </div>
            </div>
            <div
              v-if="comments.length > 0"
              style="padding-top:10px; border-bottom:1px solid #f0f0f0;margin-right: 30px;margin-left: 30px;"
            ></div>
            <div style="" class="row comment-area2" id="commentBoxPar">
              <form v-on:submit.prevent="addComment" class="col-12">
                <textarea
                  style="font-size: 13px;letter-spacing: 0.3px;color: #333333;border-color:#d6d6d6;"
                  v-model="newComment.body"
                  :placeholder="$t('common._common.write_comment')"
                  v-bind:class="{
                    height75: commentFocus,
                    height35: !commentFocus,
                  }"
                  class="comment-box"
                  @click="focusCommentBox"
                  ref="commentBoxRef"
                />
                <div v-if="commentFocus">
                  <div class="flRight">
                    <button
                      class="comment-btn"
                      style="text-transform: uppercase;"
                    >
                      {{ $t('common._common.comment') }}
                    </button>
                  </div>
                  <div v-if="workorder.requester" class="flLeft">
                    <input
                      type="checkbox"
                      v-model="newComment.notifyRequester"
                      name="notifyRequester"
                    /><label class="notify-req" for="notifyRequester">{{
                      $t('common._common.notify')
                    }}</label>
                  </div>
                  <div class="clearboth"></div>
                </div>
              </form>
            </div>
          </div>
          <div
            id="summarytask"
            v-if="subSection === 'taskpage'"
            style="padding-right: 30px;margin-top: 30px;padding-left: 30px;"
          >
            <div>
              <div
                v-if="sequencedData"
                v-for="(obj, index) in newTaskList"
                :key="index"
              >
                <div
                  v-if="sections[obj.sectionId]"
                  style="padding-top:35px; font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                  :style="{
                    'border-bottom': sections[obj.sectionId]
                      ? 'solid 2px #d9f4f7'
                      : '0',
                  }"
                >
                  {{
                    sections[obj.sectionId] ? sections[obj.sectionId].name : ''
                  }}
                </div>
                <div
                  v-bind:class="{
                    selected: selectedTask === task,
                    closed: task.status.type !== 'OPEN',
                  }"
                  v-for="(task, index1) in obj.taskList"
                  :key="index1"
                  class="task-list"
                >
                  <div
                    @click="changeTaskStatus(task)"
                    style="cursor:pointer;padding-left:5px;margin-right:8px"
                  >
                    <i
                      v-if="task.status.type === 'OPEN'"
                      class="el-icon-circle-check-outline"
                      style="color: #b8e5eb;font-size: 24px;"
                    ></i>
                    <i
                      v-else
                      class="el-icon-circle-check"
                      style="color: #39b2c2;font-size: 24px;"
                    ></i>
                  </div>
                  <div
                    class="task-item"
                    style="padding-left: 5px; position: relative;"
                    :style="{ width: showAddTask ? '50%' : '60%' }"
                    @click="showTaskDetails(task)"
                  >
                    <el-input
                      v-if="task.id === -1"
                      style="width:100%;"
                      v-model="task.subject"
                      type="text"
                      :placeholder="
                        $t('maintenance._workorder.write_a_task_name')
                      "
                    ></el-input>
                    <div
                      v-else
                      :class="[
                        { closedTask: task.status.type !== 'OPEN' },
                        'task-subject',
                      ]"
                      style="width:100%;word-wrap: break-word;"
                      type="text"
                      placeholder=""
                    >
                      {{ task.subject }}
                    </div>
                    <div
                      v-if="
                        task.id === -1 || (task.resource && task.resource.name)
                      "
                      class="pT5"
                    >
                      <div
                        v-if="task.id === -1"
                        @click="showSpaceAssetChooser()"
                        class="flLeft"
                        style="height:10px; width:10px;cursor:pointer;"
                      >
                        <img src="~statics/icons/assetspace.svg" />
                      </div>
                      <div v-else class="flLeft">
                        <img
                          v-if="task.resource.resourceType === 1"
                          src="~statics/space/space-resource.svg"
                          style="height:12px; width:14px;"
                        />
                        <img
                          v-else
                          src="~statics/space/asset-resource.svg"
                          style="height:11px; width:14px;"
                        />
                      </div>
                      <div
                        style="padding-left: 6px;text-overflow: ellipsis;letter-spacing: 0.5px;width: 85%;white-space: nowrap;overflow: hidden;color: #878787;font-size: 12px;"
                      >
                        {{ task.resource.name || '' }}
                      </div>
                    </div>
                  </div>
                  <div
                    class="task-item f-element"
                    v-if="task.inputType !== 1 && task.id !== -1"
                  >
                    <div v-if="task.inputType === 2 || task.inputType === '2'">
                      <el-input
                        :class="[{ 'validation-failed': errorMap[task.id] }]"
                        class="inline"
                        style="width:auto;"
                        type="number"
                        @focus="checkAndShowTask(task)"
                        @change="onReadingChange(task)"
                        v-model="task.inputValue"
                        :placeholder="
                          $t('maintenance._workorder.enter_reading')
                        "
                        :disabled="task.status.type !== 'OPEN'"
                      ></el-input>
                      <div
                        class="inline"
                        style="padding-left:7px"
                        v-if="task.readingField && task.readingField.unit"
                      >
                        {{ task.readingField.unit }}
                      </div>
                      <div
                        class="inline"
                        v-if="
                          task.lastReading &&
                            task.lastReading !== -1 &&
                            task.lastReading !== '-1'
                        "
                      >
                        <q-icon
                          name="info_outline"
                          class="info-icon"
                          size="18px"
                          style="position: absolute;top: -14px;right: -21px;vertical-align: middle;"
                          v-tippy="{
                            html: '#prevpopup_' + task.id,
                            position: 'bottom',
                            arrow: true,
                            theme: 'light',
                            animation: 'scale',
                          }"
                          title=""
                        ></q-icon>
                      </div>
                      <div :id="'prevpopup_' + task.id" class="hide">
                        <div class="task-prevreading">
                          {{ task.lastReading
                          }}{{
                            task.readingField && task.readingField.unit
                              ? task.readingField.unit
                              : ''
                          }}<span>
                            ({{ $t('common.header.previous_reading') }})</span
                          >
                        </div>
                      </div>
                      <el-date-picker
                        class="reading-time"
                        :placeholder="
                          $t('maintenance._workorder.time_of_reading')
                        "
                        v-model="task.inputTime"
                        @change="onReadingChange(task)"
                        :type="'datetime'"
                        :editable="false"
                        :disabled="task.status.type !== 'OPEN'"
                        :default-value="new Date(workorder.createdTime)"
                      ></el-date-picker>
                    </div>
                    <el-input
                      v-if="task.inputType === 3 || task.inputType === '3'"
                      type="text"
                      @focus="checkAndShowTask(task)"
                      :placeholder="$t('maintenance._workorder.enter_text')"
                      @change="addTask(task)"
                      v-model="task.inputValue"
                      :disabled="task.status.type !== 'OPEN'"
                    ></el-input>
                    <el-input
                      :class="[{ 'validation-failed': errorMap[task.id] }]"
                      v-if="task.inputType === 4 || task.inputType === '4'"
                      type="number"
                      @focus="checkAndShowTask(task)"
                      :placeholder="
                        $t('maintenance._workorder.enter_numeric_value')
                      "
                      @change="addTask(task)"
                      v-model="task.inputValue"
                      :disabled="task.status.type !== 'OPEN'"
                    ></el-input>
                    <div v-show="errorMap[task.id]" class="err-msg">
                      {{ errorMessage[task.id] }}
                    </div>
                    <span v-if="task.inputType === 5 || task.inputType === '5'">
                      <el-radio-group
                        v-if="checkOptionsLength(task.options)"
                        @change="
                          checkAndShowTask(task)
                          addTask(task)
                        "
                        v-model="task.inputValue"
                        :disabled="task.status.type !== 'OPEN'"
                      >
                        <el-radio
                          v-for="(option, index) in task.options"
                          class="pT15"
                          color="secondary"
                          :label="option"
                          :key="index"
                          >{{ option }}</el-radio
                        >
                      </el-radio-group>
                      <el-select
                        v-else
                        @change="addTask(task)"
                        @focus="checkAndShowTask(task)"
                        v-model="task.inputValue"
                        popper-class="f-dropdown"
                        :disabled="task.status.type !== 'OPEN'"
                      >
                        <el-option
                          v-for="(option, index) in task.options"
                          :key="index"
                          :label="option"
                          :value="option"
                        ></el-option>
                      </el-select>
                    </span>
                  </div>
                </div>
              </div>
              <div
                v-if="!sequencedData"
                v-for="(section, index) in Object.keys(taskList).sort()"
                :key="index"
              >
                <div
                  v-if="sections[section]"
                  style="padding-top:35px; font-size: 13px;font-weight: bold;letter-spacing: 0.6px;color: #333333;padding-bottom: 10px;padding-left:8px;"
                  :style="{
                    'border-bottom': sections[section]
                      ? 'solid 2px #d9f4f7'
                      : '0',
                  }"
                >
                  {{ sections[section] ? sections[section].name : '' }}
                </div>
                <div
                  v-bind:class="{
                    selected: selectedTask === task,
                    closed: task.status.type !== 'OPEN',
                  }"
                  v-for="(task, index1) in taskList[section]"
                  :key="index1"
                  class="task-list"
                >
                  <div
                    @click="changeTaskStatus(task)"
                    style="cursor:pointer;padding-left:5px;margin-right:8px"
                  >
                    <i
                      v-if="task.status.type === 'OPEN'"
                      class="el-icon-circle-check-outline"
                      style="color: #b8e5eb;font-size: 24px;"
                    ></i>
                    <i
                      v-else
                      class="el-icon-circle-check"
                      style="color: #39b2c2;font-size: 24px;"
                    ></i>
                  </div>
                  <div
                    class="task-item"
                    style="padding-left: 5px; position: relative;"
                    :style="{ width: showAddTask ? '50%' : '57%' }"
                    @click="showTaskDetails(task)"
                  >
                    <el-input
                      v-if="task.id === -1"
                      style="width:100%;"
                      v-model="task.subject"
                      type="text"
                      :placeholder="
                        $t('maintenance._workorder.write_a_task_name')
                      "
                    ></el-input>
                    <div
                      v-else
                      :class="[
                        { closedTask: task.status.type !== 'OPEN' },
                        'task-subject',
                      ]"
                      style="width:100%;word-wrap: break-word;"
                      type="text"
                      placeholder=""
                    >
                      {{ task.subject }}
                    </div>
                    <div
                      v-if="
                        task.id === -1 || (task.resource && task.resource.name)
                      "
                      class="pT5"
                    >
                      <div
                        v-if="task.id === -1"
                        @click="showSpaceAssetChooser()"
                        class="flLeft"
                        style="height:10px; width:10px;cursor:pointer;"
                      >
                        <img src="~statics/icons/assetspace.svg" />
                      </div>
                      <div v-else class="flLeft">
                        <img
                          v-if="task.resource.resourceType === 1"
                          src="~statics/space/space-resource.svg"
                          style="height:12px; width:14px;"
                        />
                        <img
                          v-else
                          src="~statics/space/asset-resource.svg"
                          style="height:11px; width:14px;"
                        />
                      </div>
                      <div
                        style="padding-left: 6px;text-overflow: ellipsis;letter-spacing: 0.5px;width: 85%;white-space: nowrap;overflow: hidden;color: #878787;font-size: 12px;"
                      >
                        {{ task.resource.name || '' }}
                      </div>
                    </div>
                  </div>
                  <div
                    class="task-item f-element"
                    v-if="task.inputType !== 1 && task.id !== -1"
                  >
                    <div v-if="task.inputType === 2 || task.inputType === '2'">
                      <el-input
                        class="inline"
                        style="width:auto"
                        type="number"
                        @focus="checkAndShowTask(task)"
                        @change="onReadingChange(task)"
                        v-model="task.inputValue"
                        :placeholder="
                          $t('maintenance._workorder.enter_reading')
                        "
                        :disabled="task.status.type !== 'OPEN'"
                      ></el-input>
                      <div
                        class="inline"
                        style="padding-left:7px"
                        v-if="task.readingField && task.readingField.unit"
                      >
                        {{ task.readingField.unit }}
                      </div>
                      <div
                        class="inline"
                        v-if="
                          task.lastReading &&
                            task.lastReading !== -1 &&
                            task.lastReading !== '-1'
                        "
                      >
                        <q-icon
                          name="info_outline"
                          class="info-icon"
                          size="18px"
                          style="margin-left: 22px;vertical-align: middle;"
                          v-tippy="{
                            html: '#prevpopup_' + task.id,
                            position: 'bottom',
                            arrow: true,
                            theme: 'light',
                            animation: 'scale',
                          }"
                          title=""
                        ></q-icon>
                      </div>
                      <div :id="'prevpopup_' + task.id" class="hide">
                        <div class="task-prevreading">
                          {{ task.lastReading
                          }}{{
                            task.readingField && task.readingField.unit
                              ? task.readingField.unit
                              : ''
                          }}<span>
                            ({{ $t('common.header.previous_reading') }})</span
                          >
                        </div>
                      </div>
                      <el-date-picker
                        class="reading-time"
                        :placeholder="
                          $t('maintenance._workorder.time_of_reading')
                        "
                        v-model="task.inputTime"
                        @change="onReadingChange(task)"
                        :type="'datetime'"
                        :editable="false"
                        :disabled="task.status.type !== 'OPEN'"
                        :default-value="new Date(workorder.createdTime)"
                      ></el-date-picker>
                    </div>
                    <el-input
                      v-if="task.inputType === 3 || task.inputType === '3'"
                      type="text"
                      @focus="checkAndShowTask(task)"
                      :placeholder="$t('maintenance._workorder.enter_text')"
                      @change="addTask(task)"
                      v-model="task.inputValue"
                      :disabled="task.status.type !== 'OPEN'"
                    ></el-input>
                    <el-input
                      v-if="task.inputType === 4 || task.inputType === '4'"
                      type="number"
                      @focus="checkAndShowTask(task)"
                      :placeholder="
                        $t('maintenance._workorder.enter_numeric_value')
                      "
                      @change="addTask(task)"
                      v-model="task.inputValue"
                      :disabled="task.status.type !== 'OPEN'"
                    ></el-input>
                    <span v-if="task.inputType === 5 || task.inputType === '5'">
                      <el-radio-group
                        v-if="checkOptionsLength(task.options)"
                        @change="
                          checkAndShowTask(task)
                          addTask(task)
                        "
                        v-model="task.inputValue"
                        :disabled="task.status.type !== 'OPEN'"
                      >
                        <el-radio
                          v-for="(option, index) in task.options"
                          class="pT15"
                          color="secondary"
                          :label="option"
                          :key="index"
                          >{{ option }}</el-radio
                        >
                      </el-radio-group>
                      <el-select
                        v-else
                        @change="addTask(task)"
                        @focus="checkAndShowTask(task)"
                        v-model="task.inputValue"
                        popper-class="f-dropdown"
                        :disabled="task.status.type !== 'OPEN'"
                      >
                        <el-option
                          v-for="(option, index) in task.options"
                          :key="index"
                          :label="option"
                          :value="option"
                        ></el-option>
                      </el-select>
                    </span>
                  </div>
                </div>
              </div>
              <div
                class="pT15"
                style="padding-bottom:30px;"
                v-if="workorder.sourceType !== 5 && canEdit"
              >
                <el-button
                  @click="addNewTask"
                  class="btnsize txt"
                  style="font-size: 10px;font-weight: bold;padding-right: 13px !important;padding-left: 13px !important;border: 1px solid rgb(57, 178, 194);color: rgb(57, 178, 194);padding-top: 6px !important;padding-bottom: 6px !important;border-radius: 3px;"
                  >{{ $t('maintenance._workorder.add_task') }}</el-button
                >
                <el-button
                  v-show="false"
                  @click="addNewTask"
                  class="btnsize txt"
                  style="font-size: 10px;font-weight: bold;padding-right: 13px !important;padding-left: 13px !important;border: 1px solid rgb(57, 178, 194);color: rgb(57, 178, 194);padding-top: 6px !important;padding-bottom: 6px !important;border-radius: 3px;"
                  >{{ $t('maintenance._workorder.add_section') }}</el-button
                >
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col
        v-if="showAddTask && selectedTask.id !== -1"
        class="cardpadding width42"
      >
        <div
          class="scrollable"
          style="background:white;height:80vh;padding-bottom: 50px;"
        >
          <div
            class="subheader-section "
            style="padding-bottom: 25px;padding-top:17px;padding-left:30px"
          >
            <div class="taskdetails" style="padding-bottom: 20px;">
              {{ $t('maintenance._workorder.task_details') }}
            </div>
            <i
              aria-hidden="true"
              class="q-icon el-icon-close material-icons destroy pull-right pointer"
              @click="closedisplay"
            ></i>
            <div>
              <div
                @click="changeTaskStatus(selectedTask)"
                style="cursor:pointer;"
                class="flLeft"
              >
                <i
                  v-if="selectedTask.status.type === 'OPEN'"
                  class="el-icon-circle-check-outline"
                  style="color: #b8e5eb;font-size: 40px;"
                ></i>
                <i
                  v-else
                  class="el-icon-circle-check"
                  style="color: #62c1cd;font-size: 40px;"
                ></i>
              </div>
              <div
                class="flLeft"
                style="padding-left: 8px;position: relative;width: 85%;"
              >
                <div class="taskheader">{{ selectedTask.subject }}</div>
                <div>
                  <div
                    v-if="selectedTask.resource"
                    class="flLeft"
                    style="margin-right:5px;padding-top: 1px;"
                  >
                    <img
                      v-if="selectedTask.resource.resourceType === 1"
                      src="~statics/space/space-resource.svg"
                      style="height:12px; width:14px;"
                    />
                    <img
                      v-else
                      src="~statics/space/asset-resource.svg"
                      style="height:11px; width:14px;"
                    />
                  </div>
                  <div class="tasksubheader flLeft">
                    {{
                      selectedTask.resource ? selectedTask.resource.name : ''
                    }}
                  </div>
                  <div class="clearboth"></div>
                </div>
              </div>
              <div class="clearboth"></div>
            </div>
          </div>
          <div style="margin-left:30px;margin-right:30px">
            <div style="margin-top:30px" class="taskdisc">
              {{ selectedTask.description }}
            </div>
            <div>
              <div
                v-if="
                  selectedTask.inputType === 2 || selectedTask.inputType === '2'
                "
              >
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.enter') }}
                  {{ selectedTask.readingField.displayName }}
                  {{
                    selectedTask.readingField.unit
                      ? '(in ' + selectedTask.readingField.unit + ')'
                      : ''
                  }}
                </div>
                <el-input
                  @change="addTask(selectedTask)"
                  v-model="selectedTask.inputValue"
                  type="number"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></el-input>
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.time_of_reading') }}
                </div>
                <el-date-picker
                  class="inputtime-detail"
                  v-model="selectedTask.inputTime"
                  @change="onReadingChange(selectedTask)"
                  :type="'datetime'"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></el-date-picker>
              </div>
              <div
                v-if="
                  selectedTask.inputType === 3 || selectedTask.inputType === '3'
                "
              >
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.specify_input') }}
                </div>
                <el-input
                  @change="addTask(selectedTask)"
                  v-model="selectedTask.inputValue"
                  type="text"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></el-input>
              </div>
              <div
                v-if="
                  selectedTask.inputType === 4 || selectedTask.inputType === '4'
                "
              >
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.specify_input') }}
                </div>
                <el-input
                  @change="addTask(selectedTask)"
                  v-model="selectedTask.inputValue"
                  type="number"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></el-input>
              </div>
              <div
                v-if="
                  selectedTask.inputType === 5 || selectedTask.inputType === '5'
                "
                class="f-element"
              >
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.select_input') }}
                </div>
                <div
                  v-for="(option, index) in selectedTask.options"
                  :key="index"
                >
                  <el-radio
                    @change="addTask(selectedTask)"
                    v-model="selectedTask.inputValue"
                    class="pT15"
                    color="secondary"
                    :label="option"
                    :disabled="selectedTask.status.type !== 'OPEN'"
                    >{{ option }}</el-radio
                  >
                </div>
              </div>
              <div
                v-if="
                  selectedTask.inputType === 6 || selectedTask.inputType === '6'
                "
              >
                <div class="pT30 taskoptions">
                  {{ $t('maintenance._workorder.select_input') }}
                </div>
                <el-checkbox-group
                  v-model="selectedTask.inputValues"
                  @change="addTask(selectedTask)"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                >
                  <el-checkbox
                    v-for="(option, index) in selectedTask.options"
                    class="newradio pT15 f-checkbox"
                    color="secondary"
                    :label="option"
                    :key="index"
                    >{{ option }}</el-checkbox
                  >
                </el-checkbox-group>
              </div>
              <div class="pT30 taskoptions">
                {{ $t('maintenance._workorder.remarks') }}
              </div>
              <div>
                <el-input
                  @change="addTask(selectedTask)"
                  type="text"
                  v-model="selectedTask.remarks"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></el-input>
              </div>
              <div class="pT30 taskoptions">
                <mobile-attachment
                  module="taskattachments"
                  :record="selectedTask"
                  :disabled="selectedTask.status.type !== 'OPEN'"
                ></mobile-attachment>
              </div>
            </div>
          </div>
        </div>
      </el-col>
      <el-col
        v-else-if="showAddTask && selectedTask.id === -1"
        class="cardpadding width42"
      >
        <div class="scrollable" style="background:white;height:75vh">
          <div
            class="subheader-section "
            style="padding-bottom: 25px;padding-top:17px;padding-left:30px"
          >
            <div class="taskdetails" style="padding-bottom: 20px;">
              {{ $t('maintenance._workorder.add_task') }}
            </div>
            <i
              aria-hidden="true"
              class="q-icon el-icon-close material-icons destroy pull-right pointer"
              @click="closedisplay"
            ></i>
            <div>
              <div class="flLeft">
                <i
                  class="el-icon-circle-check-outline"
                  style="color: #b8e5eb;font-size: 40px;"
                ></i>
              </div>
              <div class="flLeft">
                <div class="taskheader">
                  <el-input
                    style="width:100%;"
                    type="text"
                    v-model="selectedTask.subject"
                    :placeholder="
                      $t('maintenance._workorder.write_a_task_name')
                    "
                  ></el-input>
                </div>
                <div style="margin-top:-3px;">
                  <div class="tasksubheader flLeft" style="padding-left:10px;">
                    {{ selectedTask.selectedResourceName }}
                  </div>
                  <div
                    class="flLeft"
                    style="height:10px;width:10px;margin-left:10px; cursor:pointer;"
                    @click="showSpaceAssetChooser()"
                  >
                    <img src="~statics/icons/assetspace.svg" />
                  </div>
                  <div class="clearboth"></div>
                </div>
              </div>
              <div class="clearboth"></div>
            </div>
          </div>
          <div style="margin-left:30px;margin-right:30px;margin-top:30px;">
            <div>
              <el-row align="middle">
                <el-col :span="24">
                  <div class="textcolor">
                    <div
                      class="taskdescinput"
                      style="  font-size: 13px; letter-spacing: 0.4px;  color: #6b7e91;"
                    >
                      {{ $t('maintenance._workorder.description') }}
                    </div>
                    <el-input
                      v-model="selectedTask.description"
                      style="padding-top:10px;"
                      class="form-item "
                      type="textarea"
                    />
                  </div>
                </el-col>
              </el-row>
              <div style="padding-top:25px;" class="enableInput">
                <el-checkbox v-model="selectedTask.attachmentRequired">{{
                  $t('maintenance._workorder.photo')
                }}</el-checkbox>
              </div>
              <div style="padding-top:25px;" class="enableInput">
                <el-checkbox
                  v-model="selectedTask.enableInput"
                  @change="
                    selectedTask.inputType = !selectedTask.enableInput
                      ? 1
                      : selectedTask.inputType
                  "
                  >{{ $t('maintenance._workorder.enable_input') }}</el-checkbox
                >
              </div>
              <div v-if="selectedTask.enableInput" class="f-element">
                <div
                  class="pT30"
                  style="font-size: 13px;letter-spacing: 0.4px;color: #6b7e91;"
                >
                  {{ $t('maintenance._workorder.task_type') }}
                </div>
                <el-radio
                  @change="onSelectInput"
                  :disabled="
                    !this.selectedTask.resource ||
                      !selectedTask.resource.id ||
                      selectedTask.resource.id === -1
                  "
                  v-model="selectedTask.inputType"
                  class="pT15"
                  color="secondary"
                  label="2"
                  >{{ $t('maintenance._workorder.reading') }}
                  {{
                    !selectedTask.resource ||
                    !selectedTask.resource.id ||
                    selectedTask.resource.id === -1
                      ? $t('maintenance._workorder.select_asset_space')
                      : ''
                  }}</el-radio
                >
                <div>
                  <el-radio
                    @change="onSelectInput"
                    v-model="selectedTask.inputType"
                    class="pT15"
                    color="secondary"
                    label="3"
                    >{{ $t('maintenance._workorder.text') }}</el-radio
                  >
                </div>
                <el-radio
                  @change="onSelectInput"
                  v-model="selectedTask.inputType"
                  class="pT15"
                  color="secondary"
                  label="4"
                  >{{ $t('maintenance._workorder.numeric') }}</el-radio
                >
                <div>
                  <el-radio
                    @change="onSelectInput"
                    v-model="selectedTask.inputType"
                    class="pT15"
                    color="secondary"
                    label="5"
                    >{{ $t('maintenance._workorder.option') }}</el-radio
                  >
                </div>
                <el-radio
                  v-show="false"
                  @change="onSelectInput"
                  v-model="selectedTask.inputType"
                  class="newradio pT15"
                  color="secondary"
                  label="6"
                  >{{ $t('maintenance._workorder.checkbox') }}</el-radio
                >
              </div>
              <div
                style="padding-top:20px;"
                v-if="selectedTask.inputType === '2'"
              >
                <div
                  style="padding-top:20px;font-size: 13px;letter-spacing: 0.4px;color: #6b7e91;"
                >
                  {{ $t('maintenance._workorder.reading_field') }}
                </div>
                <div>
                  <el-select
                    v-model="selectedTask.readingFieldId"
                    style="width:100%"
                    class="form-item"
                    placeholder=" "
                  >
                    <el-option
                      v-for="readingField in taskReadingFields"
                      :key="readingField.id"
                      :label="readingField.displayName"
                      :value="readingField.id"
                    ></el-option>
                  </el-select>
                </div>
              </div>
              <div
                v-if="
                  selectedTask.inputType === '5' ||
                    selectedTask.inputType === '6'
                "
              >
                <div
                  class="pT30"
                  style="font-size: 13px;letter-spacing: 0.4px;color: #6b7e91;padding-bottom:10px;"
                >
                  {{ $t('maintenance._workorder.options') }}
                </div>
                <div
                  v-for="option in selectedTask.options"
                  style="padding-bottom:10px;"
                  :key="option"
                >
                  <div
                    v-if="selectedTask.inputType === '5'"
                    style="width: 14px;height: 14px;margin-top: 9px;"
                    class="circle flLeft"
                  ></div>
                  <div
                    v-if="selectedTask.inputType === '6'"
                    style="width: 14px;height: 14px;margin-top: 9px;margin-right: 7px;    border-radius: 3px;    border: 1px solid #c4d1dd;"
                    class="flLeft"
                  ></div>
                  <div class="flLeft" style="padding-left:3px; width:200px;">
                    <el-input
                      style="width:100%;"
                      type="text"
                      v-model="option.name"
                      placeholder=""
                    ></el-input>
                  </div>
                  <div class="clearboth"></div>
                </div>
                <div
                  style="margin-bottom:30px;font-size: 13px;color: rgb(57, 178, 194);cursor: pointer;border-bottom: 1px solid rgb(57, 178, 194);width: 70px;padding-top: 10px;"
                  @click="addTaskOption(selectedTask)"
                >
                  {{ $t('maintenance._workorder.add_option') }}
                </div>
              </div>
            </div>
            <div class="pT15" style="padding-top:30px;padding-bottom:30px;">
              <el-button
                @click="addTask(selectedTask)"
                class="btnsize txt"
                style="font-size: 11px;font-weight: bold;padding: 9px 17px !important;border: 1px solid rgb(57, 178, 194);color: rgb(57, 178, 194);border-radius: 3px;"
                >{{ $t('maintenance._workorder.save') }}</el-button
              >
            </div>
          </div>
        </div>
      </el-col>
      <el-col v-else class="cardpadding width32">
        <div
          class="scrollable"
          style="background:white;height:80vh;padding-bottom: 50px;"
        >
          <div style="padding:0px; border-bottom:solid 1px #eeeeee;">
            <div
              class="flLeft"
              style="border-right:solid 1px #eeeeee; cursor:pointer;"
              @click="selectSummarySection('details')"
            >
              <img
                style="padding-top: 20px;padding-left: 20px;padding-right: 20px;padding-bottom: 12px;"
                src="~statics/icons/info.svg"
              />
              <div
                v-if="summarySection === 'details'"
                class="selectionbar"
                style="margin-top: 0px;width: 100%;"
              ></div>
            </div>
            <div
              class="flLeft"
              style="border-right:solid 1px #eeeeee; cursor:pointer;"
              @click="selectSummarySection('attachments')"
            >
              <img
                style="padding-top: 20px;padding-left: 20px;padding-right: 20px;padding-bottom: 12px;"
                src="~statics/icons/clipboard.svg"
              />
              <div
                v-if="summarySection === 'attachments'"
                class="selectionbar"
                style="margin-top: 0px;width: 100%;"
              ></div>
            </div>
            <div
              class="flLeft"
              style="border-right:solid 1px #eeeeee; cursor:pointer;"
              @click="selectSummarySection('activities')"
            >
              <img
                style="padding-top: 20px;padding-left: 20px;padding-right: 20px;padding-bottom: 12px;"
                src="~statics/icons/clock.svg"
              />
              <div
                v-if="summarySection === 'activities'"
                class="selectionbar"
                style="margin-top: 0px;width: 100%;"
              ></div>
            </div>
            <div class="clearboth"></div>
          </div>

          <!-- Workorder Details -->

          <div v-if="summarySection === 'details'" style="margin-left:30px">
            <div v-if="workorder.space">
              <div style="margin-top:30px" class="raised">
                {{ $t('maintenance._workorder.raised_for') }}
              </div>
              <div v-if="workorder.space.spaceTypeVal === 'Building'">
                <div class="place">
                  {{
                    workorder.space.id
                      ? getSpace(workorder.space.siteId).name
                      : '---'
                  }}
                </div>
              </div>
              <div v-else-if="workorder.space.spaceTypeVal === 'Site'">
                <div style="padding-bottom:35px" class="place">
                  {{
                    workorder.space.id
                      ? getSpace(workorder.space.id).name
                      : '---'
                  }}
                </div>
              </div>
              <div v-if="workorder.space.spaceTypeVal === 'Building'">
                <div style="padding-bottom:35px" class="inplace">
                  {{ workorder.space.name }}
                </div>
              </div>
            </div>
            <div style="margin-top: 40px" v-else></div>
            <div>
              <div v-if="showSiteEdit" style="padding-bottom: 23px;">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.site') }}
                </div>
                <div
                  style="font-size: 14px;letter-spacing: 0.6px;color: #333;"
                  :class="['flLeft picklist', { pointer: canEdit }]"
                >
                  <div
                    class="flLeft"
                    v-if="workorder.siteId && workorder.siteId > 0"
                  >
                    {{ getSiteName(workorder.siteId) }}
                  </div>
                  <div class="flLeft color-d" v-else>---</div>
                  <div
                    class="flLeft"
                    style="    padding-left: 10px;"
                    v-if="canEdit"
                  >
                    <span class="picklist-downarrow">
                      <i
                        aria-hidden="true"
                        class="fa fa-sort-desc wl-icon-downarrow"
                      ></i>
                    </span>
                  </div>
                  <div class="clearboth"></div>
                  <q-popover ref="sitepopover" v-if="canEdit">
                    <q-list link class="scroll" style="min-width: 180px">
                      <q-item
                        v-for="(site, key) in sites"
                        :key="key"
                        @click="
                          updateSite(workorder.id, site.id),
                            $refs.sitepopover.close()
                        "
                      >
                        <q-item-main
                          :label="site.name"
                          style="font-size: 13px;"
                        />
                      </q-item>
                    </q-list>
                  </q-popover>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 23px;">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.type') }}
                </div>
                <div
                  style="font-size: 14px;letter-spacing: 0.6px;color: #333;"
                  :class="['flLeft picklist', { pointer: canEdit }]"
                >
                  <div
                    class="flLeft"
                    v-if="workorder.type && workorder.type.id > 0"
                  >
                    {{ getPMType(workorder.type.id) }}
                  </div>
                  <div class="flLeft color-d" v-else>---</div>
                  <div
                    class="flLeft"
                    style="    padding-left: 10px;"
                    v-if="canEdit"
                  >
                    <span class="picklist-downarrow">
                      <i
                        aria-hidden="true"
                        class="fa fa-sort-desc wl-icon-downarrow"
                      ></i>
                    </span>
                  </div>
                  <div class="clearboth"></div>
                  <q-popover ref="typepopover" v-if="canEdit">
                    <q-list link class="scroll" style="min-width: 180px">
                      <q-item
                        v-for="(type, key) in tickettype"
                        :key="key"
                        @click="
                          updateWorkOrder([workorder.id], {
                            type: { id: key, name: type },
                          }),
                            $refs.typepopover.close()
                        "
                      >
                        <q-item-main :label="type" style="font-size: 13px;" />
                      </q-item>
                    </q-list>
                  </q-popover>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 23px;">
                <div
                  class="flLeft heading"
                  style="width:30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.priority') }}
                </div>
                <div
                  style="font-size: 14px;letter-spacing: 0.6px;color: #333;"
                  :class="['flLeft picklist', { pointer: canEdit }]"
                >
                  <div
                    class="flLeft"
                    v-if="workorder.priority && workorder.priority.id > 0"
                  >
                    <span class="q-item-label"
                      ><i
                        class="fa fa-circle prioritytag"
                        v-bind:style="{
                          color: getTicketPriority(workorder.priority.id)
                            .colour,
                        }"
                        aria-hidden="true"
                      >
                      </i
                      >{{
                        workorder.priority && workorder.priority.id > 0
                          ? getTicketPriority(workorder.priority.id).displayName
                          : '---'
                      }}</span
                    >
                  </div>
                  <div class="flLeft color-d" v-else>---</div>
                  <div
                    class="flLeft"
                    style="    padding-left: 10px;"
                    v-if="canEdit"
                  >
                    <span class="picklist-downarrow">
                      <i
                        aria-hidden="true"
                        class="fa fa-sort-desc wl-icon-downarrow"
                      ></i>
                    </span>
                  </div>
                  <div class="clearboth"></div>
                  <q-popover ref="prioritypopover" v-if="canEdit">
                    <q-list link class="scroll" style="min-width: 180px">
                      <q-item
                        v-for="(priority, key) in ticketpriority"
                        :key="key"
                        @click="
                          updateWorkOrder([workorder.id], {
                            priority: { id: key, priority: priority },
                          }),
                            $refs.prioritypopover.close()
                        "
                      >
                        <q-item-main
                          :label="priority"
                          style="font-size: 13px;"
                        />
                      </q-item>
                    </q-list>
                  </q-popover>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 23px;">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.category') }}
                </div>
                <div
                  style="font-size: 14px;letter-spacing: 0.6px;color: #333;"
                  :class="['flLeft picklist', { pointer: canEdit }]"
                >
                  <div
                    class="flLeft"
                    v-if="workorder.category && workorder.category.id > 0"
                  >
                    {{ getTicketCategory(workorder.category.id).displayName }}
                  </div>
                  <div class="flLeft color-d" v-else>---</div>
                  <div
                    class="flLeft"
                    style="    padding-left: 10px;"
                    v-if="canEdit"
                  >
                    <span class="picklist-downarrow">
                      <i
                        aria-hidden="true"
                        class="fa fa-sort-desc wl-icon-downarrow"
                      ></i>
                    </span>
                  </div>
                  <div class="clearboth"></div>
                  <q-popover ref="categorypopover" v-if="canEdit">
                    <q-list link class="scroll" style="min-width: 180px">
                      <q-item
                        v-for="(category, key) in ticketcategory"
                        :key="key"
                        @click="
                          updateWorkOrder([workorder.id], {
                            category: { id: key, name: category },
                          }),
                            $refs.categorypopover.close()
                        "
                      >
                        <q-item-main
                          :label="category"
                          style="font-size: 13px;"
                        />
                      </q-item>
                    </q-list>
                  </q-popover>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 23px;">
                <div
                  v-if="
                    workorder.resource && workorder.resource.resourceType === 2
                  "
                >
                  <div
                    class="flLeft heading"
                    style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                  >
                    {{ $t('maintenance._workorder.asset') }}
                  </div>
                  <div>
                    <div>
                      <div style="float:left;width: 10%;">
                        <asset-avatar
                          size="mobile-xlg"
                          name="false"
                          :asset="{ id: -1, name: '' }"
                          class="asset-space-img"
                        ></asset-avatar>
                      </div>
                      <div style="float:right;width: 53%;">
                        <div
                          @click="openAsset(workorder.resource.id)"
                          class="profile-name-txt"
                          style="cursor: pointer;"
                        >
                          {{
                            workorder.resource ? workorder.resource.name : '---'
                          }}
                        </div>
                        <div class="heading">
                          {{
                            workorder.resource.spaceId > 0
                              ? getSpace(workorder.resource.spaceId).name
                              : '---'
                          }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div
                  v-else-if="
                    workorder.resource && workorder.resource.resourceType === 1
                  "
                >
                  <div
                    class="flLeft heading"
                    style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                  >
                    {{ $t('maintenance._workorder.space') }}
                  </div>
                  <div class="flLeft">
                    <div style="display:flex">
                      <div style="float:left">
                        <space-avatar
                          class="asset-space-img"
                          :name="false"
                          size="mobile-xlg"
                          :space="workorder.resource"
                        ></space-avatar>
                      </div>
                      <div style="float: right">
                        <div
                          class="bluetxt"
                          style="cursor:pointer;"
                          @click="redirect"
                        >
                          {{ workorder.resource.name }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else>
                  <div
                    class="flLeft heading"
                    style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                  >
                    {{ $t('maintenance.wr_list.space_asset') }}
                  </div>
                  <div class="heading">---</div>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 39px;">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance._workorder.created_time') }}
                </div>
                <div
                  style="font-size: 14px;letter-spacing: 0.6px;"
                  class="flLeft picklist"
                >
                  <div class="flLeft">
                    {{ workorder.createdTime | formatDate() }}
                  </div>
                </div>
              </div>
              <div class="clearboth"></div>
              <div style="padding-bottom: 11px;" class="picklist">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.duedate') }}
                </div>
                <div class="flLeft color-d" id="showdatepicker" v-if="canEdit">
                  <el-date-picker
                    class="f-custom-datepicker"
                    ref="showdatepicker"
                    prefix-icon=" "
                    clear-icon=" "
                    v-model="duedate"
                    align="right"
                    type="datetime"
                    @change="onChangeDueDate"
                    :picker-options="pickerOptions"
                  >
                  </el-date-picker>
                </div>
                <div
                  :class="['flLeft', { pointer: canEdit }]"
                  @click="$refs.showdatepicker.focus()"
                  v-if="workorder.dueDate !== -1"
                >
                  {{ workorder.dueDate | formatDate() }}
                </div>
                <div
                  :class="['flLeft color-d', { pointer: canEdit }]"
                  @click="canEdit ? $refs.showdatepicker.focus() : null"
                  v-else
                >
                  ---
                </div>
                <div class="flLeft" style="padding-left: 10px;" v-if="canEdit">
                  <span class="picklist-downarrow">
                    <i
                      aria-hidden="true"
                      class="fa fa-sort-desc wl-icon-downarrow"
                    ></i>
                  </span>
                </div>
                <div class="clearboth"></div>
              </div>
              <div style="padding-bottom: 23px;" class="picklist">
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;text-transform: uppercase;"
                >
                  {{ $t('maintenance.wr_list.assignedto') }}
                </div>
                <div
                  class="flLeft"
                  v-if="
                    (workorder.assignedTo && workorder.assignedTo.id > 0) ||
                      (workorder.assignmentGroup &&
                        workorder.assignmentGroup.id > 0)
                  "
                >
                  <user-avatar
                    size="md"
                    :user="workorder.assignedTo"
                    :group="workorder.assignmentGroup"
                    :showPopover="true"
                    :showLabel="true"
                    moduleName="workorder"
                  ></user-avatar>
                </div>
                <div class="flLeft color-d" v-else>--- / ---</div>
                <div class="flLeft" style="padding-left: 10px;" v-if="canEdit">
                  <span class="picklist-downarrow">
                    <i
                      aria-hidden="true"
                      class="fa fa-sort-desc wl-icon-downarrow"
                    ></i>
                  </span>
                </div>
                <div class="clearboth"></div>
                <f-assignment
                  v-if="canEdit"
                  viewtype="view"
                  :record="workorder"
                  :siteId="workorder.siteId"
                ></f-assignment>
              </div>
              <div
                v-if="workorder.sourceType === 5"
                style="padding-bottom: 23px;"
              >
                <div
                  class="flLeft heading"
                  style="width: 30%;padding-top: 2px;"
                >
                  {{ $t('maintenance._workorder.pm_id') }}
                </div>
                <div
                  class="flLeft bluetxt"
                  style="cursor:pointer;"
                  @click="openPM"
                >
                  #{{ workorder.pm.id }}
                </div>
                <div class="clearboth"></div>
              </div>
              <div class="flLeft color-d" id="showcustomdatepicker">
                <el-date-picker
                  class="f-custom-datepicker"
                  ref="showcustomdatepicker"
                  prefix-icon=" "
                  clear-icon=" "
                  v-model="selectedFieldValue"
                  align="right"
                  type="datetime"
                  @change="onChangeCustomField"
                >
                </el-date-picker>
              </div>
              <div
                v-if="!field.default"
                style="padding-bottom: 23px;"
                class="edit-icon-block"
                v-for="(field, index) in fields"
                :key="index"
              >
                <div
                  class="flLeft heading"
                  style="text-transform:uppercase;width: 30%;padding-top: 2px;"
                >
                  {{ field.displayName }}
                </div>
                <div
                  :class="['flLeft', { pointer: canEdit }]"
                  @click="
                    canEdit
                      ? ($refs.showcustomdatepicker.focus(),
                        (selectedFieldUpdate = field),
                        (selectedFieldValue = workorder.data[field.name]))
                      : null
                  "
                  v-if="
                    workorder.data &&
                      workorder.data[field.name] &&
                      (field.dataTypeEnum === 'DATE' ||
                        field.dataTypeEnum._name === 'DATE')
                  "
                >
                  {{ workorder.data[field.name] | formatDate() }}
                </div>
                <div
                  :class="['flLeft', { pointer: canEdit }]"
                  @click="
                    canEdit
                      ? ($refs.showcustomdatepicker.focus(),
                        (selectedFieldUpdate = field),
                        (selectedFieldValue = workorder.data[field.name]))
                      : null
                  "
                  v-else-if="
                    workorder.data &&
                      workorder.data[field.name] &&
                      (field.dataTypeEnum === 'DATE_TIME' ||
                        field.dataTypeEnum._name === 'DATE_TIME')
                  "
                >
                  {{ workorder.data[field.name] | formatDate() }}
                </div>
                <div
                  :class="['flLeft', { pointer: canEdit }]"
                  @click="
                    canEdit
                      ? ((customFieldUpdate = true),
                        (selectedFieldUpdate = field),
                        (selectedFieldValue = workorder.data[field.name]))
                      : null
                  "
                  v-else-if="workorder.data && workorder.data[field.name]"
                >
                  {{ workorder.data[field.name] }}
                </div>
                <div
                  :class="['flLeft color-d', { pointer: canEdit }]"
                  @click="
                    canEdit
                      ? ((selectedFieldUpdate = field),
                        (selectedFieldValue = null),
                        updateEmptyField())
                      : null
                  "
                  v-else
                >
                  ---
                </div>
                <div class="flLeft" style="padding-left: 10px;" v-if="canEdit">
                  <span class="picklist-downarrow">
                    <i
                      aria-hidden="true"
                      class="fa fa-sort-desc wl-icon-downarrow"
                    ></i>
                  </span>
                </div>
                <div class="clearboth"></div>
              </div>
            </div>
          </div>
          <div v-else-if="summarySection === 'attachments'">
            <attachments
              module="ticketattachments"
              :record="workorder"
            ></attachments>
          </div>
          <div
            v-else-if="summarySection === 'activities'"
            style="margin-left:30px"
          >
            <activities :record="workorder" ref="activitiesDiv"></activities>
          </div>
        </div>
      </el-col>
    </el-row>
    <f-dialog
      v-if="customFieldUpdate"
      :visible="customFieldUpdate"
      :width="'20%'"
      :title="'Enter ' + selectedFieldUpdate.displayName + ' Value'"
      @save="updateCustomFields()"
      @close="closeUpdateDialog"
      :confirmTitle="'Update'"
      maxHeight="200px"
    >
      <div slot="content">
        <el-input
          type="textarea"
          v-if="
            selectedFieldUpdate.dataTypeEnum === 'TEXTAREA' ||
              selectedFieldUpdate.dataTypeEnum._name === 'TEXTAREA'
          "
          v-model="selectedFieldValue"
        ></el-input>
        <el-input
          type="textarea"
          v-else-if="
            selectedFieldUpdate.dataTypeEnum === 'STRING' ||
              selectedFieldUpdate.dataTypeEnum._name === 'STRING'
          "
          v-model="selectedFieldValue"
        ></el-input>
        <el-input
          type="number"
          v-else-if="
            selectedFieldUpdate.dataTypeEnum === 'NUMBER' ||
              selectedFieldUpdate.dataTypeEnum._name === 'NUMBER' ||
              selectedFieldUpdate.dataTypeEnum === 'DECIMAL' ||
              selectedFieldUpdate.dataTypeEnum._name === 'DECIMAL'
          "
          v-model="selectedFieldValue"
        >
        </el-input>
      </div>
    </f-dialog>
    <task-space-asset-chooser
      @associate="associate"
      :visibility.sync="visibility"
      :quickSearchQuery="quickSearchQuery"
    ></task-space-asset-chooser>
    <work-hours
      v-if="showWorkDurationDialog"
      :visible.sync="showWorkDurationDialog"
      :id="workorder.id"
      :callback="workDurationCallBack"
    ></work-hours>
  </div>
</template>
<style></style>
<script>
import Attachments from '@/relatedlist/SummaryAttachment'
import Activities from '@/relatedlist/Activities2'
import { mapState, mapGetters } from 'vuex'
import Avatar from '@/Avatar'
import UserAvatar from '@/avatar/User'
import AssetAvatar from '@/avatar/Asset'
import FDialog from '@/FDialogNew'
import TaskSpaceAssetChooser from '@/SpaceAssetChooser'
import MobileAttachment from '@/MobileAttachment2'
import FAssignment from '@/FAssignment'
import WorkHours from 'pages/workorder/widgets/dialogs/WorkHours'
import $helpers from 'util/helpers'
import ApproveReject from '@/stateflow/ApproveReject'
import { isEmpty } from '@facilio/utils/validation'
import transformMixin from 'pages/workorder/workorders/v1/mixins/workorderTransform'
import { QIcon, QList, QItem, QItemMain, QPopover, Alert } from 'quasar'
import {
  findRouteForModule,
  findRouteForTab,
  isWebTabsEnabled,
  pageTypes,
  tabTypes,
} from '@facilio/router'
import { mapGettersWithLogging } from 'store/utils/log-map-getters'

export default {
  name: 'ApprovalSummaryFullView',
  mixins: [transformMixin],
  data() {
    return {
      showSiteEdit: true,
      validationRules: {},
      errorMap: {},
      errorQue: {},
      errorMessage: {},
      taskBulkList: [],
      fields: [],
      noOfUpdatedTAsk: 0,
      testVar: null,
      customFieldUpdate: false,
      visibility: false,
      quickSearchQuery: '',
      loading: true,
      summoryview: true,
      fieldvisibility: false,
      showWorkDurationDialog: false,
      workDurationCallBack: null,
      radio2: '',
      selectedFieldUpdate: null,
      selectedFieldValue: null,
      summaryWindow: 'true',
      subSection: 'summarypage',
      sequencedData: false,
      summarySection: 'details',
      taskReadingFields: [],
      duedate: '',
      value1: '',
      subheaderMenu: [
        {
          label: this.$t('common.events.today'),
          path: { path: '/app/fa/events/today' },
        },
        {
          label: this.$t('common.events.yesterday'),
          path: { path: '/app/fa/events/yesterday' },
        },
        {
          label: this.$t('common.events.this_week'),
          path: { path: '/app/fa/events/thisweek' },
        },
        {
          label: this.$t('common.events.last_week'),
          path: { path: '/app/fa/events/lastweek' },
        },
        {
          label: this.$t('common.events.all_events'),
          path: { path: '/app/fa/events/all' },
        },
      ],
      comments: [],
      taskList: {},
      newTaskList: [],
      sections: {},
      newComment: {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.$route.params.id,
        body: null,
        notifyRequester: false,
      },
      commentFocus: false,
      newTask: {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        inputValidation: null,
        minSafeLimit: null,
        maxSafeLimit: null,
        id: -1,
      },
      showAddTask: false,
      selectedTask: {
        inputValidationRuleId: -1,
        inputValidation: null,
        safeLimitRuleId: -1,
        minSafeLimit: null,
        maxSafeLimit: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        subject: '',
        selectedResourceName: '',
        inputValues: [],
      },
      urgencyColorCode: {
        1: '#7fa5ff',
        2: '#f56837',
        3: '#e65244',
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 3600 * 1000 * 24
        },
      },
      waitBoolean: true,
    }
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadGroups')
    this.fetchWo()
    window.addEventListener('click', this.blurCommentBox)
  },
  mounted() {
    this.loadComments()
    this.loadTasks()
    this.loadFields()
    let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
    if (currentSiteId !== -1) {
      this.showSiteEdit = false
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      ticketstatus: state => state.ticketStatus.workorder,
      sites: state => state.site,
    }),
    ...mapGetters([
      'getTicketTypePickList',
      'getTicketStatusByLabel',
      'getTicketPriority',
      'getTicketCategory',
    ]),
    ...mapGettersWithLogging(['getSpace']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    canEdit() {
      let moduleId = this.$getProperty(this.workorder, 'moduleState.id', null)
      return (
        !isEmpty(moduleId) &&
        !this.$store.getters.isStatusLocked(
          this.workorder.moduleState.id,
          'workorder'
        )
      )
    },
    openWorkorderId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    allTaskStatus() {
      let status = null
      let closedCount = 0
      let taskCount = 0
      for (let idx in this.taskList) {
        for (let idx2 in this.taskList[idx]) {
          taskCount = taskCount + 1
          let completed = this.taskList[idx][idx2].status.type === 'OPEN'
          if (
            completed &&
            this.taskList[idx][idx2].inputType !== 1 &&
            !this.taskList[idx][idx2].inputValue
          ) {
            if (status === null) {
              status = false
            }
            status = status && false
          } else {
            if (!completed) {
              closedCount = closedCount + 1
            }
            if (status === null) {
              status = true
            }
            status = status && true
          }
        }
      }
      if (closedCount === taskCount) {
        status = false
      }
      return status
    },
    openStatusId() {
      let id = this.getTicketStatusByLabel('Submitted', 'workorder').id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    closedStatusId() {
      let id = this.getTicketStatusByLabel('Closed', 'workorder').id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    workorder() {
      return this.$store.state.workorder.currentWorkOrder
    },
    ticketStatusValue() {
      if (this.workorder && this.workorder.status && this.workorder.status.id) {
        return (
          this.$store.getters.getTicketStatus(
            this.workorder.status.id,
            'workorder'
          ).status || ''
        )
      }
      return ''
    },
  },
  watch: {
    workorder: function() {
      let title =
        '[#' + this.workorder.serialNumber + '] ' + this.workorder.subject
      this.setTitle(title)
      this.setDueDate(this.workorder.dueDate)
    },
    openWorkorderId: function(newVal) {
      this.fetchWo()
    },
  },
  methods: {
    fetchWo(force) {
      this.$store.dispatch('workorder/fetchWorkOrder', {
        id: this.openWorkorderId,
        force,
      })
    },
    updateEmptyField() {
      if (
        this.selectedFieldUpdate.dataTypeEnum === 'DATE' ||
        this.selectedFieldUpdate.dataTypeEnum._name === 'DATE'
      ) {
        this.$refs.showcustomdatepicker.focus()
      } else if (
        this.selectedFieldUpdate.dataTypeEnum === 'DATE_TIME' ||
        this.selectedFieldUpdate.dataTypeEnum._name === 'DATE_TIME'
      ) {
        this.$refs.showcustomdatepicker.focus()
      } else {
        this.customFieldUpdate = true
      }
    },
    updateCustomFields() {
      let updateCustomfield = {}
      updateCustomfield[this.selectedFieldUpdate.name] = this.selectedFieldValue
      this.updateWorkOrder([this.workorder.id], { data: updateCustomfield })
      this.customFieldUpdate = false
    },
    closeUpdateDialog() {
      this.customFieldUpdate = false
    },
    getSiteName(siteId) {
      if (this.sites) {
        let s = this.sites.find(i => i.id === siteId)
        if (!s) {
          return '---'
        }
        return s.name
      }
      return '---'
    },
    openAsset(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.replace({ name, params: { viewname: 'all', id } })
          }
        } else {
          let url = '/app/at/assets/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },
    updateSite(woId, siteId) {
      this.$dialog
        .confirm({
          title: this.$t('maintenance._workorder.change_site'),
          message: this.$t('maintenance._workorder.change_site_message'),
          rbDanger: true,
          rbLabel: this.$t('maintenance._workorder.change_site_message_label'),
        })
        .then(value => {
          if (value) {
            this.updateWorkOrder([woId], { siteId })
          }
        })
    },
    setDueDate(duedate) {
      if (duedate === -1) {
        this.duedate = Date.now() - 3600 * 1000 * 24
      } else {
        this.duedate = new Date(duedate)
      }
    },
    onChangeDueDate(pickVal) {
      this.updateWorkOrderDueDate(
        [this.workorder.id],
        new Date(pickVal).getTime()
      )
    },
    onChangeCustomField(pickVal) {
      let updateCustomfield = {}
      updateCustomfield[this.selectedFieldUpdate.name] = new Date(
        pickVal
      ).getTime()
      this.updateWorkOrder([this.workorder.id], { data: updateCustomfield })
    },
    back() {
      this.$router.go(-1)
    },
    closeAllTask() {
      let taskIds = []
      let ticketId
      for (let idx in this.taskList) {
        for (let idx2 in this.taskList[idx]) {
          let task = this.taskList[idx][idx2]
          ticketId = task.parentTicketId
          taskIds.push(this.taskList[idx][idx2].id)
        }
      }
      let data = {
        taskIdList: taskIds,
        parentTicketId: ticketId,
      }
      let self = this
      this.$http.post('/task/closeAllTask', data).then(function(response) {
        self.loadTasks()
      })
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    redirect() {
      if (this.workorder.resource.siteId) {
        let parentPath = this.findRoute()

        if (parentPath) {
          let url = `${parentPath}/site/${this.workorder.resource.siteId}`

          if (this.workorder.resource.spaceType === 1) {
            url += '/overview'
          } else if (this.workorder.resource.spaceType === 2) {
            url += '/building/' + this.workorder.resource.spaceId
          } else if (this.workorder.resource.spaceType === 3) {
            url += '/floor/' + this.workorder.resource.spaceId
          } else if (this.workorder.resource.spaceType === 4) {
            url += '/space/' + this.workorder.resource.spaceId
          }
          this.$router.push({ path: url })
        }
      }
    },
    loadFields() {
      let self = this
      self.loading = true
      self.$http
        .get('/module/fields?moduleName=workorder')
        .then(function(response) {
          self.fields = response.data.fields
          self.loading = false
        })
    },
    taskStatus(task) {
      let completed = task.status.type === 'OPEN'
      let data = {
        id: [task.id],
        task: {
          id: task.id,
          parentTicketId: task.parentTicketId,
          status: {
            id: completed ? this.closedStatusId : this.openStatusId,
            type: completed ? 'CLOSED' : 'OPEN',
          },
        },
      }

      let self = this
      this.$http.post('/task/updatestatus', data).then(function(response) {
        if (typeof response.data === 'object') {
          if (completed) {
            task.status = {
              id: self.closedStatusId,
              type: 'CLOSED',
            }
            self.$message({
              message: self.$t('maintenance._workorder.task_close_success'),
              type: 'success',
            })
          } else {
            task.status = {
              id: self.openStatusId,
              type: 'OPEN',
            }
            self.$message({
              message: self.$t('maintenance._workorder.task_open_success'),
              type: 'success',
            })
          }
          if (self.ticketstatusvalue !== 'Work in Progress') {
            self.workorder.status.id = self.getTicketStatusByLabel(
              'Work in Progress',
              'workorder'
            ).id
          }
        } else {
          self.$message({
            message: self.$t('maintenance._workorder.operation_failed'),
            type: 'error',
          })
        }
      })
    },
    changeTaskStatus(task) {
      let completed = task.status.type === 'OPEN'
      if (completed && task.inputType !== 1 && !task.inputValue) {
        this.$message({
          message: this.$t('maintenance._workorder.task_input'),
          type: 'warning',
        })
        return
      }
      if (completed) {
        if (this.taskBulkList.length) {
          setTimeout(() => this.taskStatus(task), 5000)
        } else {
          this.taskStatus(task)
        }
      } else {
        this.taskStatus(task)
      }
    },
    showSpaceAssetChooser() {
      this.visibility = true
    },
    associate(selectedObj) {
      this.visibility = false
      this.selectedTask.resource = selectedObj
      this.selectedTask.selectedResourceName = selectedObj.name
    },
    onReadingChange(task) {
      if (!task.inputValue) {
        return
      }
      this.addTask(task)
    },
    asyncTaskUpdate(doValidation = true) {
      let self = this
      let taskListContext = []
      if (self.waitBoolean) {
        if (self.taskBulkList) {
          self.taskBulkList.filter(function(task) {
            let data = {
              id: task.id,
              inputValue: task.inputValue,
              inputValues: task.inputValues,
              remarks: task.remarks,
              readingFieldId: task.readingFieldId,
            }
            if (task.inputTime > 0) {
              data.inputTime = self.$helpers.getTimeInOrg(task.inputTime)
            }
            taskListContext.push(data)
          })
          if (taskListContext) {
            self.updateTaskList(
              { taskContextList: taskListContext },
              doValidation
            )
          }
        }
      }
    },
    updateTaskList(data, doValidation = true) {
      this.taskBulkList = []
      this.waitBoolean = false
      let self = this
      self.$http
        .post(`/task/updateAllTask?doValidation=${doValidation}`, data)
        .then(function(response) {
          if (typeof response.data === 'object') {
            self.waitBoolean = true
            self.noOfUpdatedTAsk =
              self.noOfUpdatedTAsk + response.data.rowsUpdated
            if (
              typeof response.data.error !== 'undefined' &&
              response.data.error
            ) {
              data.taskContextList.forEach(function(datum) {
                if (!(datum.id in response.data.error)) {
                  self.$set(self.errorMap, datum.id, false)
                  self.$set(self.errorMessage, datum.id, null)
                }
              })
              Object.assign(self.errorQue, response.data.error)
            } else {
              data.taskContextList.forEach(function(datum) {
                self.$set(self.errorMap, datum.id, false)
                self.$set(self.errorMessage, datum.id, null)
              })
            }
            if (self.taskBulkList.length > 0) {
              self.asyncTaskUpdate(doValidation)
              return
            } else {
              self.$message({
                showClose: true,
                message:
                  self.noOfUpdatedTAsk +
                  self.$t('maintenance._workorder.task_updated_success'),
                type: 'success',
              })
              self.noOfUpdatedTAsk = 0
            }
            if (self.ticketstatusvalue !== 'Work in Progress') {
              self.workorder.status.id = self.getTicketStatusByLabel(
                'Work in Progress',
                'workorder'
              ).id
            }
            if (Object.keys(self.errorQue).length > 0) {
              self.$dialog
                .confirm({
                  title: self.$t('_workoder.add_readings'),
                  message: self.$t('_workoder.add_readings_message'),
                  rbDanger: true,
                  rbLabel: self.$t('maintenance._workorder.save'),
                })
                .then(function(val) {
                  if (val) {
                    let tasks = self.newTaskList
                      .map(v => v.taskList)
                      .reduce((a, b) => a.concat(b))
                      .filter(v => {
                        return v.id in self.errorQue
                      })
                    let taskContextList = []
                    tasks.forEach(t => {
                      let data = {
                        id: t.id,
                        inputValue: t.inputValue,
                        inputValues: t.inputValues,
                        remarks: t.remarks,
                        readingFieldId: t.readingFieldId,
                      }
                      taskContextList.push(data)
                    })
                    self.errorQue = {}
                    self.updateTaskList({ taskContextList }, false)
                  } else {
                    Object.keys(self.errorQue).forEach(function(key) {
                      self.$set(
                        self.errorMessage,
                        key,
                        self.errorQue[key].message
                      )
                      self.$set(self.errorMap, key, true)
                    })
                  }
                  self.errorQue = {}
                })
            }
            self.taskBulkList = []
          } else {
            self.$message({
              showClose: true,
              message: this.$t('maintenance._workorder.task_updated_failed'),
              type: 'error',
            })
          }
        })
    },
    waitFunc: $helpers.debounce(function() {
      if (this.taskBulkList.length) {
        this.asyncTaskUpdate()
      }
    }, 5000),
    addTask(task) {
      let self = this
      if (task.id !== -1) {
        this.taskBulkList.push(task)
        this.asyncTaskUpdate()
      } else {
        let formData = {}
        if (task.subject.trim() === '') {
          this.$message({
            message: this.$t('maintenance._workorder.task_title_required'),
            type: 'error',
          })
          return
        }
        if (!task.resource.id) {
          delete task.resource
        }
        if (!task.assignedTo) {
          delete task.assignedTo
        }
        if (!task.readingFieldId) {
          task.readingFieldId = -1
        }
        if (task.inputType === '5' || task.inputType === '6') {
          let taskOptions = []
          for (let key in task.options) {
            let option = task.options[key]
            taskOptions[key] = option.name
          }
          task.options = taskOptions
        }

        task.parentTicketId = self.workorder.id
        formData.task = task

        self.$http.post('/task/add', formData).then(function(response) {
          task.id = response.data.taskId
          self.tasks.push(task)
          self.newTask = {
            subject: '',
            description: '',
            resource: {
              id: -1,
            },
            isReadingTask: false,
            readingFieldId: -1,
            options: [],
            taskType: -1,
            enableInput: false,
            attachmentRequired: false,
            id: -1,
          }
        })
        const TaskAddedmsg = Alert.create({
          html: self.$t('maintenance._workorder.task_added_success'),
          color: 'positive',
          position: 'top-center',
        })
        setTimeout(function() {
          TaskAddedmsg.dismiss()
        }, 1500)
      }
    },
    addTaskOption(selectedTask) {
      selectedTask.options.push({ name: '' })
    },
    getPMType(type) {
      return this.tickettype[type]
    },
    addNewTask() {
      if (this.selectedTask && this.selectedTask.subject !== '') {
        this.addTask(this.selectedTask)
      }
      let lastSection = -1
      for (let key in this.taskList) {
        if (this.taskList.hasOwnProperty(key)) {
          if (
            key !== -1 ||
            (key === -1 && Object.keys(this.taskList).length === 1)
          ) {
            lastSection = key
          }
        }
      }
      this.newTask = {
        subject: '',
        description: '',
        resource: {
          id: -1,
        },
        isReadingTask: false,
        readingFieldId: null,
        options: [],
        taskType: -1,
        enableInput: false,
        inputType: 1,
        attachmentRequired: false,
        status: {
          type: 'OPEN',
          typeCode: 1,
          id: -1,
        },
        id: -1,
        sectionId: lastSection,
      }
      if (!this.taskList[lastSection]) {
        this.taskList[lastSection] = []
      }
      this.taskList[lastSection].push(this.newTask)
      this.showTaskDetails(this.newTask)
    },
    checkAndShowTask(task) {
      if (this.showAddTask) {
        this.showTaskDetails(task)
      }
    },
    checkOptionsLength(options) {
      return options.length <= 2 && !options.some(option => option.length > 10)
    },
    onSelectInput() {
      if (this.selectedTask.inputType === '2') {
        this.fillTaskReadings()
      }
    },
    fillTaskReadings() {
      if (
        this.selectedTask.resource &&
        this.selectedTask.resource.resourceType === 2 &&
        this.selectedTask.resource.category
      ) {
        let url =
          '/reading/getassetreadings?parentCategoryId=' +
          this.selectedTask.resource.category.id
        this.$http.get(url).then(response => {
          let readings = response.data
          for (let reading of readings) {
            for (let field of reading.fields) {
              this.taskReadingFields.push(field)
            }
          }
        })
      } else if (this.selectedTask.resource) {
        let parentCategoryId = -1
        if (self.selectedTask.resource.category) {
          parentCategoryId = self.selectedTask.resource.category.id
        }
        let url =
          '/reading/getwospacereadings?parentCategoryId=' + parentCategoryId
        this.$http.get(url).then(function(response) {
          let readings = response.data
          for (let reading of readings) {
            for (let field of reading.fields) {
              self.taskReadingFields.push(field)
            }
          }
        })
      }
    },
    showTaskDetails(task) {
      this.showAddTask = true
      this.selectedTask = task
    },
    focusCommentBox() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
    },
    blurCommentBox(e) {
      let itTargetPar = e.path.filter(ele => {
        if (ele.id === 'commentBoxPar') {
          return true
        }
      })
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !itTargetPar.length
      ) {
        this.commentFocus = false
      }
    },
    addComment() {
      if (this.newComment.body) {
        let self = this
        self.$http
          .post('/note/add', {
            note: self.newComment,
            module: 'ticketnotes',
            parentModuleName: 'workorder',
          })
          .then(function(response) {
            self.comments.push(response.data.note)
            self.reset()
            self.commentFocus = false
            if (self.$refs['activitiesDiv']) {
              self.$refs['activitiesDiv'].loadActivities()
            }
          })
      }
    },
    loadactivitie() {
      if (this.$refs['activitiesDiv']) {
        this.$refs['activitiesDiv'].loadActivities()
      }
    },
    reset() {
      this.newComment = {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.$route.params.id,
        body: null,
        notifyRequester: false,
      }
    },
    loadComments() {
      let self = this
      self.loading = true
      return this.$http
        .get('/note/get?module=ticketnotes&parentId=' + this.$route.params.id)
        .then(function(response) {
          self.loading = false
          self.comments = response.data ? response.data : []
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.comments = []
          }
        })
    },
    sort(arr) {
      let len = arr.length
      for (let i = len - 1; i >= 0; i--) {
        for (let j = 1; j <= i; j++) {
          if (arr[j - 1].seq > arr[j].seq) {
            let temp = arr[j - 1]
            arr[j - 1] = arr[j]
            arr[j] = temp
          }
        }
      }
      return arr
    },
    loadTasks() {
      let self = this
      self.loading = true
      return this.$http
        .get('/task?module=task&recordId=' + this.$route.params.id)
        .then(function(response) {
          self.loading = false
          if (
            response.data.tasks[Object.keys(response.data.tasks)[0]][0]
              .sequence !== -1
          ) {
            let unsorted = []
            Object.keys(response.data.tasks).forEach(key => {
              unsorted.push({
                secId: key,
                seq: response.data.tasks[key][0].sequence,
              })
            })
            let sorted = self.sort(unsorted)
            let newTaskList = []
            sorted.forEach(ele => {
              newTaskList.push({
                sectionId: ele.secId,
                taskList: response.data.tasks[ele.secId],
              })
            })
            newTaskList = newTaskList.map((obj, objInx) => {
              let tasks = obj.taskList.map((task, index) => {
                if (!task.status || task.status.id === self.openStatusId) {
                  task.status = {
                    type: 'OPEN',
                    typeCode: 1,
                    id: self.openStatusId,
                  }
                } else {
                  task.status = {
                    type: 'CLOSED',
                    typeCode: 2,
                    id: self.closedStatusId,
                  }
                }
                if (task.inputTime === -1) {
                  task.inputTime = null
                }
                return task
              })
              obj.taskList = tasks
              return obj
            })
            self.newTaskList = newTaskList
            self.sequencedData = true
          } else {
            self.sequencedData = false
          }
          self.taskList = response.data.tasks ? response.data.tasks : {}
          for (let idx in self.taskList) {
            for (let idx2 in self.taskList[idx]) {
              if (
                !self.taskList[idx][idx2].status ||
                self.taskList[idx][idx2].status.id === self.openStatusId
              ) {
                self.taskList[idx][idx2].status = {
                  type: 'OPEN',
                  typeCode: 1,
                  id: self.openStatusId,
                }
              } else {
                self.taskList[idx][idx2].status = {
                  type: 'CLOSED',
                  typeCode: 2,
                  id: self.closedStatusId,
                }
              }
              if (self.taskList[idx][idx2].inputTime === -1) {
                self.taskList[idx][idx2].inputTime = null
              }
            }
          }
          self.sections = response.data.sections ? response.data.sections : {}
        })
        .catch(function(error) {
          if (error) {
            self.loading = false
            self.tasks = []
          }
        })
    },
    workorderstatus(id) {
      let priority = this.getTicketPriority(id)
      if (priority) {
        return priority
      } else {
        return ''
      }
    },
    closedisplay() {
      this.showAddTask = false
    },
    ticketstatusvalue(id) {
      if (id !== null) {
        let status = this.$store.getters.getTicketStatus(id, 'workorder').status
        if (status) {
          return status
        } else {
          return ''
        }
      }
      return ''
    },
    assignWorkOrder(assignedTo, assignmentGroup) {
      let self = this
      let assignObj = {}
      assignObj.id = [this.workorder.id]
      if (assignedTo) {
        assignObj.assignedTo = assignedTo
      }
      if (assignmentGroup) {
        assignObj.assignmentGroup = assignmentGroup
      }
      assignObj.status = {
        id: this.getTicketStatusByLabel('Assigned', 'workorder').id,
        status: 'Assigned',
      }

      self.$store
        .dispatch('workorder/assignWorkOrder', assignObj)
        .then(function() {
          self.$dialog.notify(
            self.$t('maintenance._workorder.wo_assigned_success')
          )
          self.loadactivitie()
          self.fetchWo(true)
        })
    },
    updateWorkOrderDueDate(idList, datetime) {
      let updateObj = {
        id: idList,
        fields: {
          dueDate: datetime,
        },
      }
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(response => {
          this.loading = true
          this.$message.success(
            this.$t('maintenance._workorder.wo_duedate_update_success')
          )
          this.loading = false
          this.fetchWo(true)
        })
        .catch(() => {
          this.$message.error('Workorder Due Date updation failed')
        })
    },
    updateWorkOrder(idList, field, actualTimings) {
      let updateObj = {
        id: idList,
        fields: field,
        actualTimings,
      }
      this.$store
        .dispatch('workorder/updateWorkOrder', updateObj)
        .then(response => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_update_success')
          )
          this.fetchWo(true)
          this.loadactivitie()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_update_failed')
          )
        })
    },
    resolveWorkOrder(idList, actualWorkDuration, actualTimings, statusField) {
      let updateObj = {
        id: idList,
      }
      if (actualWorkDuration > 0) {
        updateObj.actualWorkDuration = actualWorkDuration
        updateObj.actualTimings = actualTimings
      }
      this.$store
        .dispatch('workorder/resolve', updateObj)
        .then(response => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_resolved_success')
          )
          this.loading = false
          this.fetchWo(true)
          this.loadactivitie()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_resolved_failed')
          )
        })
    },
    updateStatus(status) {
      let self = this
      if (status === 'On Hold') {
        self.rejectWorkOrderDialog()
        return
      } else if (
        status === 'Resolved' &&
        self.workorder.isWorkDurationChangeAllowed
      ) {
        self.workDurationCallBack = self.setStatusUpdate.bind(self, status)
        self.showWorkDurationDialog = true
        return
      }
      self.setStatusUpdate(status)
    },
    rejectWorkOrderDialog() {
      let self = this
      let promptObj = {
        title: self.$t('maintenance._workorder.wo_pause_title'),
        message: self.$t('maintenance._workorder.wo_pause_message'),
        promptType: 'textarea',
        promptPlaceholder: self.$t(
          'maintenance._workorder.wo_pause_message_area'
        ),
        rbDanger: true,
        rbLabel: self.$t('maintenance._workorder.pause'),
      }
      self.$dialog.prompt(promptObj).then(function(value) {
        if (value !== null) {
          let newComments = {
            parentModuleLinkName: 'ticketnotes',
            parentId: self.workorder.id,
            body: value,
            notifyRequester: false,
          }
          self.setStatusUpdate('On Hold')
          self.$http
            .post('/note/add', {
              note: newComments,
              module: 'ticketnotes',
              ticketModuleName: 'workorder',
            })
            .then(function(response) {
              self.comments.push(response.data.note)
              self.reset()
              self.loadactivitie()
            })
        }
      })
    },
    setStatusUpdate(status, actualDuration) {
      let self = this
      let statusField = {
        id: this.getTicketStatusByLabel(status, 'workorder').id,
        status: status,
      }

      let paramObj = { status: statusField }
      let workTimings = []
      let duration = -1
      if (actualDuration) {
        workTimings = actualDuration.workTimings
        duration = actualDuration.duration
      }
      if (status === 'Resolved') {
        self.resolveWorkOrder(
          [this.workorder.id],
          duration,
          workTimings,
          statusField
        )
      } else if (status) {
        self.updateWorkOrder([this.workorder.id], paramObj, workTimings)
        self.workorder.status = statusField
      }
    },
    closeWO() {
      if (!this.checkInputTasksCompletion()) {
        return
      }
      if (
        this.workorder.isWorkDurationChangeAllowed &&
        this.ticketStatusValue !== 'Resolved'
      ) {
        this.workDurationCallBack = this.closeWorkOrder.bind(this)
        this.showWorkDurationDialog = true
        return
      }
      this.closeWorkOrder()
    },
    closeWorkOrder(actualDuration) {
      let paramObj = {
        id: [this.workorder.id],
      }
      if (actualDuration) {
        if (actualDuration.duration !== -1) {
          paramObj.actualWorkDuration = actualDuration.duration
        }
      }
      this.$store
        .dispatch('workorder/closeWorkOrder', paramObj)
        .then(response => {
          this.$message.success(
            this.$t('maintenance._workorder.wo_closed_success')
          )
          this.closeSummary()
        })
        .catch(() => {
          this.$message.error(
            this.$t('maintenance._workorder.wo_close_error_failed')
          )
        })
    },
    checkInputTasksCompletion() {
      for (let idx in this.taskList) {
        if (this.taskList.hasOwnProperty(idx)) {
          let check = this.taskList[idx].some(
            task => task.status.type === 'OPEN'
          )
          if (check) {
            this.$message({
              message: this.$t('maintenance._workorder.wo_resolved_failed'),
              type: 'warning',
            })
            return false
          }
        }
      }
      return true
    },
    closeSummary() {
      this.summoryview = false
      let newpath = this.$route.path.substring(
        0,
        this.$route.path.indexOf('/summary/')
      )
      this.$router.replace({ path: newpath })
    },
    selectSubSection(section) {
      this.subSection = section
    },
    selectSummarySection(section) {
      this.summarySection = section
    },
  },
  components: {
    Avatar,
    UserAvatar,
    Attachments,
    Activities,
    QIcon,
    QList,
    QItem,
    QItemMain,
    QPopover,
    AssetAvatar,
    FDialog,
    TaskSpaceAssetChooser,
    MobileAttachment,
    FAssignment,
    WorkHours,
    ApproveReject,
  },
  filters: {
    getUser: function(id, users) {
      let userObj = users.find(user => user.id === id)
      if (userObj) {
        return userObj
      } else {
        return {
          name: 'System',
        }
      }
    },
    options: function(jsonobj, type) {
      let array = []
      if (type === 'category' || type === 'priority') {
        for (let jsonkey in jsonobj) {
          let val = jsonobj[jsonkey]
          array.push({ label: val, value: parseInt(jsonkey) })
        }
      } else if (type === 'user' || type === 'space') {
        for (let key in jsonobj) {
          array.push({ label: jsonobj[key].name, value: jsonobj[key].id })
        }
      }
      return array
    },
  },
}
</script>
<style>
.fc-comments {
  margin-left: -19px;
  width: 100%;
}
.fc-comments .fc-comment-row .comment-body {
  max-width: 73% !important;
}
.tasklist {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.tasklistselected {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #a7c4c7;
}
.attachmentcolor .img {
  height: 12px;
  width: 12px;
  fill: #f3f3f2;
}

.newradio .el-radio__input.is-checked .el-radio__inner {
  border-color: #39b2c2;
  background: #ffffff;
}
.newradio .el-radio__inner::after {
  width: 7px;
  height: 7px;
  border-radius: 100%;
  background-color: #39b2c2;
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  -webkit-transform: translate(-50%, -50%) scale(0);
  transform: translate(-50%, -50%) scale(0);
  transition: -webkit-transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6);
  transition: transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6);
  transition: transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6),
    -webkit-transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6);
  transition: transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6),
    -webkit-transform 0.15s cubic-bezier(0.71, -0.46, 0.88, 0.6);
}
.newradio .el-radio__input.is-checked + .el-radio__label {
  color: #666666;
}
.headtask {
  font-size: 11px;
  font-weight: bold;
  letter-spacing: 1.3px;
  color: #b7b7b7;
}
.taskdetails {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.2px;
  text-align: left;
  color: #b7b7b7;
}
.taskheader {
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: left;
  color: #333333;
}
.taskdescinput .el-textarea .el-textarea__inner {
  color: #333333 !important;
}
.taskoptioninput .el-input .el-input__inner {
  margin-left: 7px;
}
.taskheader .el-input .el-input__inner {
  border: none;
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.6px;
  text-align: left;
  color: #333333;
  padding-left: 10px;
  margin-top: -6px;
}
.tasksubheader {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
}
.taskdisc {
  font-size: 14px;
  line-height: 1.57;
  letter-spacing: 0.3px;
  text-align: left;
  color: #666666;
}
.taskoptions {
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #6b7e91;
}

#summarytask .f-element .el-input__inner {
  border: 1px solid #d8dce5;
  border-radius: 3px;
  padding: 0 15px;
  width: 210px;
  height: 35px;
  color: #333333;
  letter-spacing: 0.5px;
  font-size: 14px;
  margin: 0px;
  padding-top: 8px;
}

.summary-title .locked-wo {
  float: left;
  margin-top: 3px;
  margin-right: 5px;
  color: #626565;
  font-size: 20px;
  margin-left: 3px;
}
</style>
