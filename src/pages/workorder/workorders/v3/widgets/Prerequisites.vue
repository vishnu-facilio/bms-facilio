<template>
  <div v-if="loading" class="text-center width100 pT50 mT50">
    <spinner :show="loading" size="80"></spinner>
  </div>
  <div v-else>
    <div class="pm-summary-inner-container" id="summarytask">
      <div>
        <div
          class="pm-summary-inner-left-con fL fc-res-dell fc-wo-page-scroll scrollbar-style"
          v-bind:class="{ width71: !showAddTask, width66: showAddTask }"
        >
          <div>
            <div class="fc-res-dell fc-wo-page-scroll scrollbar-style">
              <div>
                <div class="white-bg-block pT40 pL30 pB60 pR30 height100 mB100">
                  <div class="label-txt-black pB20 bold">
                    {{ $t('common.wo_report.photos') }}
                  </div>
                  <el-upload
                    class="avatar-uploader"
                    list-type="picture-card"
                    multiple
                    :file-list="photos"
                    action=""
                    accept="image/*"
                    :http-request="onUpload"
                    :on-success="onUploadSuccess"
                    :on-remove="onPhotoDelete"
                  >
                    <i class="el-icon-plus"></i>
                  </el-upload>

                  <div
                    v-for="(section, index) in Object.keys(preRequestList)"
                    :key="index"
                    class="pT40"
                  >
                    <div class="label-txt-black p20 border-bottom4 fw-bold">
                      {{
                        preRequestSections[section]
                          ? preRequestSections[section].name
                          : ''
                      }}
                    </div>
                    <div
                      v-bind:class="{
                        selected: selectedPreRequest === task,
                        closed: task.status.type !== 'OPEN',
                      }"
                      v-for="(task, index1) in preRequestList[section]"
                      :key="index1"
                      class="fc__sum__task__list"
                    >
                      <div
                        class="task-item width100 flex-middle"
                        @click="showPreRequestDetails(task)"
                      >
                        <div class="dot-green mR10 mT3"></div>
                        <div
                          :class="[
                            { closedTask: task.status.type !== 'OPEN' },
                            'label-txt-black pointer',
                          ]"
                          style="width: 100%; word-wrap: break-word;"
                          type="text"
                          placeholder
                        >
                          {{ task.subject }}
                        </div>
                      </div>
                      <div
                        class="text-right width40 flex-middle justify-content-end"
                        v-if="task.inputType !== 1 && task.id !== -1"
                      >
                        <div
                          v-if="task.inputType === 6 || task.inputType === '6'"
                          class="flex-middle justify-content-end"
                        >
                          <el-radio-group
                            @change="
                              checkAndShowPreRequest(task)
                              addPreRequest(task)
                            "
                            v-model="task.inputValue"
                            :disabled="task.status.type !== 'OPEN'"
                            class="fc-radio-btn"
                          >
                            <el-radio
                              color="secondary"
                              :label="task.truevalue"
                              :key="task.truevalue"
                              >{{ task.truevalue }}</el-radio
                            >
                            <el-radio
                              color="secondary"
                              :label="task.falsevalue"
                              :key="task.falsevalue"
                              >{{ task.falsevalue }}</el-radio
                            >
                          </el-radio-group>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- right column -->
      <div
        v-if="showAddTask && selectedTask.id !== -1"
        class="fR pm-summary-inner-right-con mT20 fc-wo-summary-task-fixed width32-4"
      >
        <div class="fc-scrollbar-wrap mT0">
          <div class="white-bg-block fc__sum__task__details position-relative">
            <div class>
              <i
                aria-hidden="true"
                class="q-icon el-icon-close material-icons pull-right pointer wo__close__icon"
                @click="closedisplay"
                :title="$t('common._common.close')"
                v-tippy="{
                  arrow: true,
                  placement: 'top',
                }"
              ></i>
              <div class="inline-flex">
                <div class="pL30 pT20 pR30">
                  <div class="fc-black2-18">
                    {{ selectedTask.subject ? selectedTask.subject : '' }}
                  </div>
                  <div class="flex-middle mT10">
                    <div
                      v-if="selectedTask.resource"
                      class="pR5 vertical-middle"
                    >
                      <img
                        v-if="selectedTask.resource.resourceType === 1"
                        src="~statics/space/space-resource.svg"
                        style="height: 12px; width: 14px;"
                      />
                      <img
                        v-else
                        src="~statics/space/asset-resource.svg"
                        style="height: 11px; width: 14px;"
                      />
                    </div>
                    <div class="fc-grey2-text12">
                      {{
                        selectedTask.resource ? selectedTask.resource.name : ''
                      }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="mL30 mR30">
              <div class="fc-black-13">
                {{ selectedTask.description }}
              </div>
              <div class="mT20">
                <div
                  v-if="
                    selectedTask.inputType === 2 ||
                      selectedTask.inputType === '2'
                  "
                >
                  <div class="taskoptions pB10">
                    {{ $t('maintenance._workorder.enter') }}
                    {{ selectedTask.readingField.displayName }}
                    {{
                      selectedTask.readingField.unit
                        ? '(in ' + selectedTask.readingField.unit + ')'
                        : ''
                    }}
                  </div>
                  <el-radio-group
                    v-if="
                      selectedTask.readingField &&
                        isBooleanField(selectedTask.readingField)
                    "
                    v-model="selectedTask.inputValue"
                    @change="addTask(selectedTask)"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    class="fc-radio-btn"
                  >
                    <el-radio class="pT15" color="secondary" label="1">{{
                      selectedTask.readingField.trueVal || 'True'
                    }}</el-radio>
                    <el-radio class="pT15" color="secondary" label="0">{{
                      selectedTask.readingField.falseVal || 'False'
                    }}</el-radio>
                  </el-radio-group>
                  <el-input
                    v-else
                    @change="addTask(selectedTask)"
                    v-model="selectedTask.inputValue"
                    type="number"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    class="fc-input-full-border2"
                  ></el-input>
                  <el-date-picker
                    class="inputtime-detail fc-input-full-border2 width100 mT20"
                    v-model="selectedTask.inputTime"
                    @change="onReadingChange(selectedTask)"
                    :type="'datetime'"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                  ></el-date-picker>
                </div>
                <div
                  v-if="
                    selectedTask.inputType === 3 ||
                      selectedTask.inputType === '3'
                  "
                >
                  <el-input
                    @change="addTask(selectedTask)"
                    v-model="selectedTask.inputValue"
                    type="text"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    class="fc-input-full-border2"
                  ></el-input>
                </div>
                <div
                  v-if="
                    selectedTask.inputType === 4 ||
                      selectedTask.inputType === '4'
                  "
                >
                  <el-input
                    @change="addTask(selectedTask)"
                    v-model="selectedTask.inputValue"
                    type="number"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    class="fc-input-full-border2"
                  ></el-input>
                </div>
                <div
                  v-if="
                    selectedTask.inputType === 5 ||
                      selectedTask.inputType === '5'
                  "
                  class="f-element"
                >
                  <div
                    v-for="(option, index) in selectedTask.options"
                    :key="index"
                  >
                    <el-radio
                      @change="addTask(selectedTask)"
                      v-model="selectedTask.inputValue"
                      class="pT15 fc-radio-btn"
                      color="secondary"
                      :label="option"
                      :disabled="
                        selectedTask.status.type !== 'OPEN' ||
                          !isPrerequisiteCompleted
                      "
                      >{{ option }}</el-radio
                    >
                  </div>
                </div>
                <div
                  v-if="
                    selectedTask.inputType === 6 ||
                      selectedTask.inputType === '6'
                  "
                >
                  <el-checkbox-group
                    v-model="selectedTask.inputValues"
                    @change="addTask(selectedTask)"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
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
                <div class="taskoptions pB10 mT20">
                  <mobile-attachment
                    module="taskattachments"
                    :record="selectedTask"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    @taskattachmentactivity="updateWoActions()"
                  ></mobile-attachment>
                </div>
                <div class="taskoptions pB10 pT20 fc__wo__remarks">
                  <el-input
                    @change="addTask(selectedTask)"
                    type="text"
                    v-model="selectedTask.remarks"
                    :disabled="
                      selectedTask.status.type !== 'OPEN' ||
                        !isPrerequisiteCompleted
                    "
                    class="fc-input-full-border2"
                    :placeholder="$t('common.products.add_remarks')"
                  ></el-input>
                </div>
              </div>
            </div>
            <div class="fc__task__complete__btn">
              <el-button
                type="button"
                class="fc-pink-btn-full-width"
                :disabled="!isPrerequisiteCompleted"
                @click="changeTaskStatus(selectedTask)"
                >{{ $t('maintenance._workorder.complete_task') }}</el-button
              >
            </div>
          </div>
        </div>
      </div>

      <!-- new add task section-->
      <div
        v-else-if="showAddTask && selectedTask.id === -1"
        class="fR pm-summary-inner-right-con mT20 fc-wo-summary-task-fixed width32-4"
      >
        <div class="fc-scrollbar-wrap mT0">
          <div class="white-bg-block fc__sum__task__details position-relative">
            <div>
              <div
                class
                style="
                          padding-bottom: 25px;
                          padding-top: 17px;
                          padding-left: 30px;
                        "
              >
                <div class="fw-bold label-txt-black pB15">
                  {{ $t('maintenance._workorder.add_task') }}
                </div>
                <i
                  aria-hidden="true"
                  class="q-icon el-icon-close material-icons destroy pull-right pointer"
                  @click="closedisplay"
                ></i>
                <div class="inline-flex">
                  <div class="flLeft">
                    <i
                      class="el-icon-circle-check"
                      style="color: rgb(255, 49, 132); font-size: 28px;"
                    ></i>
                  </div>
                  <div class="flLeft">
                    <div class="taskheader">
                      <el-input
                        style="width: 100%;"
                        type="text"
                        v-model="selectedTask.subject"
                        :placeholder="
                          $t('maintenance._workorder.write_a_task_name')
                        "
                      ></el-input>
                    </div>
                    <div style="margin-top: -3px;">
                      <div
                        class="tasksubheader flLeft"
                        style="padding-left: 10px;"
                      >
                        {{ selectedTask.selectedResourceName }}
                      </div>
                      <div
                        class="flLeft"
                        style="
                                  height: 10px;
                                  width: 10px;
                                  margin-left: 10px;
                                  cursor: pointer;
                                "
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
              <div
                style="
                          margin-left: 30px;
                          margin-right: 30px;
                          margin-top: 30px;
                        "
              >
                <div>
                  <el-row align="middle">
                    <el-col :span="24">
                      <div class="label-txt-black fw-bold">
                        <div
                          class="taskdescinput"
                          style="
                                    font-size: 13px;
                                    letter-spacing: 0.4px;
                                    color: #6b7e91;
                                  "
                        >
                          {{ $t('maintenance._workorder.description') }}
                        </div>
                        <el-input
                          v-model="selectedTask.description"
                          class="form-item pT20 fc-input-full-border-select2"
                          type="textarea"
                        />
                      </div>
                    </el-col>
                  </el-row>
                  <div style="padding-top: 25px;" class="enableInput">
                    <el-checkbox v-model="selectedTask.attachmentRequired">{{
                      $t('maintenance._workorder.photo')
                    }}</el-checkbox>
                  </div>
                  <div style="padding-top: 25px;" class="enableInput">
                    <el-checkbox
                      v-model="selectedTask.enableInput"
                      @change="
                        selectedTask.inputType = !selectedTask.enableInput
                          ? 1
                          : selectedTask.inputType
                      "
                      >{{
                        $t('maintenance._workorder.enable_input')
                      }}</el-checkbox
                    >
                  </div>
                  <div v-if="selectedTask.enableInput" class="f-element">
                    <div
                      class="pT30"
                      style="
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                              "
                    >
                      {{ $t('maintenance._workorder.task_type') }}
                    </div>
                    <el-radio
                      @change="onSelectInput"
                      :disabled="
                        !selectedTask.resource ||
                          !selectedTask.resource.id ||
                          selectedTask.resource.id === -1 ||
                          !isPrerequisiteCompleted
                      "
                      v-model="selectedTask.inputType"
                      class="pT15 fc-radio-btn"
                      color="secondary"
                      label="2"
                    >
                      {{ $t('maintenance._workorder.reading') }}
                      {{
                        !selectedTask.resource ||
                        !selectedTask.resource.id ||
                        selectedTask.resource.id === -1
                          ? $t('maintenance._workorder.select_asset_space')
                          : ''
                      }}
                    </el-radio>
                    <div>
                      <el-radio
                        @change="onSelectInput"
                        v-model="selectedTask.inputType"
                        class="pT15 fc-radio-btn"
                        color="secondary"
                        label="3"
                        >{{ $t('maintenance._workorder.text') }}</el-radio
                      >
                    </div>
                    <el-radio
                      @change="onSelectInput"
                      v-model="selectedTask.inputType"
                      class="pT15 fc-radio-btn"
                      color="secondary"
                      label="4"
                      >{{ $t('maintenance._workorder.numeric') }}</el-radio
                    >
                    <div>
                      <el-radio
                        @change="onSelectInput"
                        v-model="selectedTask.inputType"
                        class="pT15 fc-radio-btn"
                        color="secondary"
                        label="5"
                        >{{ $t('maintenance._workorder.option') }}</el-radio
                      >
                    </div>
                    <el-radio
                      v-show="false"
                      @change="onSelectInput"
                      v-model="selectedTask.inputType"
                      class="pT15 fc-radio-btn"
                      color="secondary"
                      label="6"
                      >{{ $t('maintenance._workorder.checkbox') }}</el-radio
                    >
                  </div>
                  <div
                    style="padding-top: 20px;"
                    v-if="selectedTask.inputType === '2'"
                  >
                    <div
                      style="
                                padding-top: 20px;
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                              "
                    >
                      {{ $t('maintenance._workorder.reading_field') }}
                    </div>
                    <div>
                      <el-select
                        v-model="selectedTask.readingFieldId"
                        style="width: 100%;"
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
                      style="
                                font-size: 13px;
                                letter-spacing: 0.4px;
                                color: #6b7e91;
                                padding-bottom: 10px;
                              "
                    >
                      {{ $t('maintenance._workorder.options') }}
                    </div>
                    <div
                      v-for="option in selectedTask.options"
                      style="padding-bottom: 10px;"
                      :key="option"
                    >
                      <div
                        v-if="selectedTask.inputType === '5'"
                        style="
                                  width: 14px;
                                  height: 14px;
                                  margin-top: 9px;
                                "
                        class="circle flLeft"
                      ></div>
                      <div
                        v-if="selectedTask.inputType === '6'"
                        style="
                                  width: 14px;
                                  height: 14px;
                                  margin-top: 9px;
                                  margin-right: 7px;
                                  border-radius: 3px;
                                  border: 1px solid #c4d1dd;
                                "
                        class="flLeft"
                      ></div>
                      <div
                        class="flLeft"
                        style="padding-left: 3px; width: 200px;"
                      >
                        <el-input
                          style="width: 100%;"
                          type="text"
                          v-model="option.name"
                          placeholder
                          class="options-input"
                        ></el-input>
                      </div>
                      <div class="clearboth"></div>
                    </div>
                    <div
                      style="
                                margin-bottom: 30px;
                                font-size: 13px;
                                color: rgb(57, 178, 194);
                                cursor: pointer;
                                border-bottom: 1px solid rgb(57, 178, 194);
                                width: 70px;
                                padding-top: 10px;
                              "
                      @click="addTaskOption(selectedTask)"
                    >
                      {{ $t('maintenance._workorder.add_option') }}
                    </div>
                  </div>
                </div>
                <div class="p30">
                  <el-button
                    @click="addTask(selectedTask)"
                    data-test-selector="Save_Task"
                    class="btnsize txt fc-btn-green-medium-fill"
                    >{{ $t('maintenance._workorder.save') }}</el-button
                  >
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <wo-field-details
        v-else
        ref="wo-field-details"
        :workorder="workorder"
        :excludeFields="[]"
        :refreshWoDetails="refreshWoDetails"
        :updateWoActions="updateWoActions"
        :currentView="currentView"
        :forV3="true"
      ></wo-field-details>
      <el-dialog
        :visible.sync="showMandatoryInputsDialog"
        :title="mandatoryInputsTitle"
        custom-class="fc-dialog-center-container"
      >
        <div v-if="showMandatoryInputsDialog" class="max-height500 pB100">
          <div v-if="addAttachmentRequiredDialog" class="pB20">
            <mobile-attachment
              module="taskattachments"
              :record="selectedTask"
              :disabled="
                selectedTask.status.type !== 'OPEN' || !isPrerequisiteCompleted
              "
              @marshalled="handlePhotoUpload($event)"
              :doNotSave="true"
            ></mobile-attachment>
          </div>
          <el-row v-if="addTaskRemarksDialog">
            <el-col :span="24">
              <p class="fc-input-label-txt">
                {{ $t('common.wo_report.remarks') }}
              </p>
              <div>
                <el-input
                  type="textarea"
                  v-model="selectedTask.remarks"
                  :autosize="{ minRows: 6, maxRows: 4 }"
                  :placeholder="$t('common.products.add_remarks')"
                  class="fc-input-full-border-textarea"
                  resize="none"
                ></el-input>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            class="modal-btn-cancel"
            @click="showMandatoryInputsDialog = false"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="addTask(selectedTask), (showMandatoryInputsDialog = false)"
            >{{ $t('common._common._save') }}</el-button
          >
        </div>
      </el-dialog>
    </div>
    <task-space-asset-chooser
      @associate="associate"
      :visibility.sync="visibility"
      :quickSearchQuery="quickSearchQuery"
      :filter="filter"
    ></task-space-asset-chooser>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import MobileAttachment from '@/MobileAttachment2'
import workorderMixin from 'pages/workorder/workorders/v1/mixins/workorderHelper'
import AlertBanner from '@/AlertBanner'
import WarningBanner from '@/WarningBanner'
import moment from 'moment-timezone'
import { QIcon } from 'quasar'
import { isEmpty } from '@facilio/utils/validation'
import { eventBus } from '@/page/widget/utils/eventBus'
import WoFieldDetails from 'pages/workorder/workorders/v1/WorkOrderFieldDetails.vue'
import { POSITION_TYPE } from 'pages/setup/custombutton/CustomButtonUtil'
import { getFieldOptions } from 'util/picklist'
import { sanitize } from '@facilio/utils/sanitize'
import TaskSpaceAssetChooser from '@/SpaceAssetChooser'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isBooleanField } from '@facilio/utils/field'

export default {
  name: 'WorkorderSummary',
  mixins: [workorderMixin],
  components: {
    QIcon,
    MobileAttachment,
    AlertBanner,
    WarningBanner,
    WoFieldDetails,
    TaskSpaceAssetChooser,
  },
  data() {
    return {
      taskErrorMap: {},
      invokeRelatedListWidget: false,
      woWidget: {
        relatedList: {
          module: {},
        },
      },
      permitWidget: {
        relatedList: {
          module: {},
          field: { name: 'ticket' },
        },
      },
      showPermitListWidget: false,
      nextExecutionTime: null,
      photos: [],
      onFocus: false,
      photoUploadUrl: null,
      photoUploadData: {},
      showAssetBreakdown: false,
      assetBDSourceDetails: {
        condition: null,
        assetid: null,
        sourceId: null,
        sourceType: null,
      },
      tasks: [],
      module_name: 'workorder',
      activeName: 'first',
      dialogVisible: false,
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
      editFormVisibilty: false,
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
      preRequestList: {},
      newTaskList: [],
      sections: {},
      preRequestSections: {},
      newComment: {
        parentModuleLinkName: 'ticketnotes',
        parentId: this.$route.params.id,
        body: null,
        notifyRequester: false,
      },
      commentFocus: true,
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
      showAddPreRequest: false,
      selectedPreRequest: {
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
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 3600 * 1000 * 24
        },
      },
      waitBoolean: true,
      downloadSummary: false,
      workOrderTotalCost: null,
      workOrderSubTotal: null,
      taskChartData: null,
      workOrderStates: [],
      activeWoStateData: null,
      selectedTaskResource: null,
      showTaskResourceFilter: false,
      metricWithUnits: {},
      readingFieldUnits: [],
      skipValidation: false,
      readingFieldErrorToggle: false,
      readingFieldError: null,
      currentValue: null,
      previousValue: null,
      readingFieldSuggestionToggle: false,
      readingFieldSuggestions: [],
      averageValue: [],
      suggestedUnits: [],
      currentTaskObj: null,
      addTaskRemarksDialog: false,
      showMandatoryInputsDialog: false,
      addAttachmentRequiredDialog: false,
      vendorsList: [],
      openTaskOptions: false,
      hazardWidget: {
        relatedList: {
          module: {},
          field: { name: 'workorder' },
        },
      },
      showHazardListWidget: false,
      currentTaskformData: {},
      POSITION: POSITION_TYPE,
      readingData: 6,
      consumptionData: '',
    }
  },
  created() {
    this.isBooleanField = isBooleanField
    window.addEventListener('click', this.blurCommentBox)
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketStatus', 'workorder')
    this.$store.dispatch('loadApprovalStatus')
    this.$store.dispatch('view/loadModuleMeta', 'workorder')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketType')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.init()
    let woPromise = this.fetchWo(true)
    let taskPromise = this.loadTasks(this.workorder)
    this.loadComments()
    this.loadReadingFieldUnits()
    Promise.all([woPromise, taskPromise]).then(resp => {
      if (this.workorder.pm && this.workorder.pm.id) {
        this.getNextWorkOrderTime().then(resp => {
          if (!resp.data.result) {
            return
          }
          this.nextExecutionTime = resp.data.result.nextExecutionTime

          if (this.taskList) {
            let keys = Object.keys(this.taskList)
            for (let i = 0; i < keys.length; i++) {
              let tasks = this.taskList[keys[i]]
              for (let k = 0; k < tasks.length; k++) {
                if (
                  this.workorder.pm &&
                  (!this.taskList[keys[i]][k].inputTime ||
                    this.taskList[keys[i]][k].inputTime === -1)
                ) {
                  let nextExec = this.nextExecutionTime
                  if (nextExec && nextExec > 0) {
                    let currentTime = new Date().valueOf()
                    if (
                      !(
                        currentTime >= this.workorder.createdTime &&
                        currentTime < nextExec
                      )
                    ) {
                      this.taskList[keys[i]][
                        k
                      ].inputTime = this.convertTimeAsIfFromDatePicker(
                        this.workorder.createdTime
                      )
                    }
                  }
                }
              }
            }
          }
        })
      }
    })
    let currentSiteId = Number(this.$cookie.get('fc.currentSite')) || -1
    if (currentSiteId !== -1) {
      this.showSiteEdit = false
    }
    this.$http
      .get('v2/photos/prerequisitephotos?parentId=' + this.openWorkorderId)
      .then(response => {
        if (response.data && response.data.responseCode === 0) {
          this.photos = response.data.result.photos
            ? response.data.result.photos
            : []
        }
      })

    this.loadVendorsPicklist()
    eventBus.$on('refesh-parent', () => {
      this.fetchWo(true)
    })
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      ticketstatus: state => state.ticketStatus.workorder,
      moduleMeta: state => state.view.metaInfo,
      sites: state => state.site,
      hasWorkOrderClosePermission() {
        if(isWebTabsEnabled()){
          return this.$hasPermission(`${this.moduleName}:UPDATE_CLOSE_WORKORDER`)
        }
      return this.$hasPermission(`${this.moduleName}:CLOSE_WORK_ORDER`)
    },
    }),
    ...mapGetters([
      'getTicketTypePickList',
      'getTicketPriority',
      'getApprovalStatus',
      'isStatusLocked',
    ]),
    mandatoryInputsTitle() {
      if (this.addAttachmentRequiredDialog && this.addTaskRemarksDialog) {
        return 'Attach Photo and Add Remarks'
      }

      if (this.addAttachmentRequiredDialog) {
        return 'Attach Photo'
      }

      if (this.addTaskRemarksDialog) {
        return 'Add Remarks'
      }

      return ''
    },
    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
    readingPickerOptions() {
      if (
        !this.workorder.pm ||
        !this.nextExecutionTime ||
        this.nextExecutionTime <= 0
      ) {
        return {
          disabledDate(time) {
            return false
          },
        }
      }

      let startTime = this.$helpers
        .getOrgMoment(this.workorder.createdTime)
        .valueOf()
      let endTime = this.$helpers.getOrgMoment(this.nextExecutionTime).valueOf()

      return {
        disabledDate(time) {
          let res = !(
            (time.getTime() >= startTime && time.getTime() < endTime) ||
            (time.getTime() + 86400000 >= startTime && time.getTime() < endTime)
          )
          if (res === false) {
            console.log('time ' + time)
          }
          return res
        },
      }
    },
    tickettype() {
      return this.getTicketTypePickList()
    },
    isPrerequisiteCompleted() {
      return (
        !this.workorder.prerequisiteEnabled ||
        !this.workorder.allowNegativePreRequisite ||
        this.workorder.allowNegativePreRequisite === -1 ||
        !this.workorder.preRequestStatus ||
        this.workorder.preRequestStatus === -1 ||
        (this.workorder.preRequestStatus &&
          this.workorder.allowNegativePreRequisite === 2 &&
          (this.workorder.preRequestStatus === 2 ||
            this.workorder.preRequestStatus === 3)) ||
        (this.workorder.allowNegativePreRequisite === 3 &&
          this.workorder.preRequestStatus === 3)
      )
    },
    enablePrerequisiteApprove() {
      return (
        this.workorder.prerequisiteEnabled &&
        this.workorder.allowNegativePreRequisite &&
        this.workorder.allowNegativePreRequisite === 3 &&
        this.workorder.prerequisiteApprover &&
        !this.workorder.preRequisiteApproved &&
        this.workorder.preRequestStatus === 2
      )
    },
    isTenantEnabled() {
      return this.$helpers.isLicenseEnabled('TENANTS')
    },
    isReadingFieldValidationEnabled() {
      return this.$helpers.isLicenseEnabled('READING_FIELD_UNITS_VALIDATION')
    },
    canEdit() {
      let hasState = this.$getProperty(this.workorder, 'moduleState.id', null)
      let isLocked = this.isStatusLocked(
        this.workorder.moduleState.id,
        'workorder'
      )
      let { isRequestedState } = this
      return hasState && !isLocked && !isRequestedState
    },
    openWorkorderId() {
      let id = this.$attrs.id || this.$route.params.id
      if (id) {
        return parseInt(id)
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

          let isOpen = this.taskList[idx][idx2].status.type === 'OPEN'
          let hasInputValue = this.taskList[idx][idx2].inputValue

          if (
            isOpen &&
            this.taskList[idx][idx2].inputType !== 1 &&
            !hasInputValue
          ) {
            if (status === null) {
              status = false
            }
            status = status && false
          } else {
            if (status === null) {
              status = true
            }
            status = status && true

            if (!isOpen) {
              closedCount = closedCount + 1
            }
          }
        }
      }
      if (closedCount === taskCount) {
        status = false
      }
      return status
    },
    openStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Submitted',
        'workorder'
      ).id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    closedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel('Closed', 'workorder')
        .id
      if (id) {
        return parseInt(id)
      }
      return id
    },
    resolvedStatusId() {
      let id = this.$store.getters.getTicketStatusByLabel(
        'Resolved',
        'workorder'
      ).id
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
          ) || ''
        )
      }
      return ''
    },
    inventory() {
      return this.$store.state.inventory.inventory
    },
    stockedTools() {
      return this.$store.state.stockedtool.stockedTools
    },
    taskResources() {
      let obj = {}
      if (this.taskList) {
        for (let idx in this.taskList) {
          for (let task of this.taskList[idx]) {
            if (task.resource && task.resource.id > 0) {
              obj[task.resource.id] = task.resource
            }
          }
        }
      }
      return Object.values(obj)
    },
    filter() {
      let filter = {}
      if (this.workorder.siteId && this.workorder.siteId > 0) {
        filter.site = Number(this.workorder.siteId)
      }
      return filter
    },
    allowEdit() {
      return false
    },
    isApprovalEnabled() {
      let { workorder = {} } = this
      let { approvalFlowId, approvalStatus } = workorder || {}
      return !isEmpty(approvalFlowId) && !isEmpty(approvalStatus)
    },
    isRequestedState() {
      let { workorder } = this
      let { approvalStatus } = workorder || {}

      if (isEmpty(approvalStatus)) {
        return false
      } else {
        let statusObj = this.getApprovalStatus(approvalStatus.id)
        return this.$getProperty(statusObj, 'requestedState', false)
      }
    },
    canDisable() {
      return this.isApprovalEnabled && !this.isRequestedState
    },
    currentView() {
      let viewName =
        this.$attrs.viewname || this.$route.params.viewname || 'all'

      return viewName
    },
    isDisabled() {
      return !this.consumptionData
    },
    canShowReportDownTime() {
      let { workorder, isNotPortal } = this
      let { resource } = workorder || {}
      let { resourceType, id } = resource || {}

      return isNotPortal && resourceType === 2 && id !== -1
    },
  },
  methods: {
    convertTimeAsIfFromDatePicker(dateInMillis) {
      let currentBrowserTimeZone = Intl.DateTimeFormat().resolvedOptions()
        .timeZone
      return moment.tz(
        this.$helpers.getOrgMoment(dateInMillis).format('YYYY-MM-DD HH:mm:ss'),
        currentBrowserTimeZone
      )
    },
    getDescription(record) {
      let { description } = record
      return !isEmpty(description) ? sanitize(description) : '---'
    },
    onTransitionSuccess() {
      this.workorder.loadTimer = false
      this.refreshWoDetails()
      this.reloadRelatedRecords()
      this.onStateFlowFormSaved()
      this.loadTasks()
      this.reloadInventorySummary()
    },
    reloadRelatedRecords() {
      eventBus.$emit('refresh-related-list')
    },
    reloadInventorySummary() {
      eventBus.$emit('refresh-inventory-summary')
    },
    deleteNote(note) {
      let obj = {
        noteId: note.id,
        module: 'ticketnotes',
        parentModuleName: 'workorder',
        note: note,
      }
      this.$dialog
        .confirm({
          title: this.$t('common.header.delete_note'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_note_form_workorder'
          ),
          rbDanger: true,
          rbLabel: 'Delete',
        })
        .then(value => {
          if (value) {
            this.$http
              .post('/v2/notes/delete', obj)
              .then(({ data: { message, responseCode, result = {} } }) => {
                if (responseCode === 0) {
                  this.$message.success(
                    this.$t('common.products.note_deleted_successfully')
                  )
                  let doNote = this.comments.find(r => r.id === note.id)
                  if (doNote) {
                    this.comments.splice(this.comments.indexOf(doNote), 1)
                  }
                } else {
                  throw new Error(message)
                }
              })
          }
        })
    },
    handlePhotoUpload(attachment) {
      if (!this.selectedTask.attachment) {
        this.selectedTask.attachment = {}
      }

      this.selectedTask.attachment.module = attachment.module
      this.selectedTask.attachment.recordId = attachment.recordId
      if (attachment.attachmentType === 1) {
        this.selectedTask.attachment.beforeFile = attachment.file
        this.selectedTask.attachment.beforeFileName = attachment.fileName
      } else {
        this.selectedTask.attachment.afterFile = attachment.file
        this.selectedTask.attachment.afterFileName = attachment.fileName
      }
    },
    openParentWo(parentWo) {
      let { id } = parentWo || {}

      if (id) {
        if (isWebTabsEnabled()) {
          let { currentView } = this
          let { name } =
            findRouteForModule('workorder', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: currentView,
                id,
              },
            })
          }
        } else {
          this.$router.push({ path: '/app/wo/orders/summary/' + id })
        }
      } else {
        return
      }
    },
    openPrintOptionDialog() {
      this.openTaskOptions = true
    },
    init() {
      let { isWorkPermitLicenseEnabled } = this
      this.woWidget.relatedList.module = (this.moduleMeta || {}).module
      if (isWorkPermitLicenseEnabled) {
        this.$http.get('module/meta?moduleName=workPermit').then(response => {
          if (response.status === 200) {
            this.$setProperty(
              this,
              'permitWidget.relatedList.module',
              this.$getProperty(response, 'data.meta.module', null)
            )
            this.showPermitListWidget = true
          }
        })
      }
      this.loadSafetyPlanWidgets()
      this.invokeRelatedListWidget = true
      this.loading = false
    },
    loadSafetyPlanWidgets() {
      if (this.$helpers.isLicenseEnabled('SAFETY_PLAN')) {
        this.$http
          .get('module/meta?moduleName=workorderHazard')
          .then(response => {
            if (response.status === 200) {
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module',
                this.$getProperty(response, 'data.meta.module', null)
              )
              this.$setProperty(
                this,
                'hazardWidget.relatedList.module.displayName',
                'Hazards'
              )
              this.showHazardListWidget = true
            }
          })
      }
    },
    updateReading(selectedTask) {
      if (
        selectedTask.inputTime &&
        this.nextExecutionTime &&
        this.nextExecutionTime > 0
      ) {
        let startTime = this.workorder.createdTime
        let endTime = this.nextExecutionTime

        if (
          !(
            selectedTask.inputTime.valueOf() >= startTime &&
            selectedTask.inputTime.valueOf() < endTime
          )
        ) {
          let s = this.$helpers.getOrgMoment(startTime).format('D MMM, h:mm a')
          let e = this.$helpers.getOrgMoment(endTime).format('D MMM, h:mm a')
          this.$message.error(
            this.$t('maintenance._workorder.task_input_time_invalid', [s, e])
          )
          return
        }
      }
      this.onReadingChange(selectedTask)
    },
    checkForHoursAndMins(task) {
      this.$nextTick(() => {
        if (
          this.$refs['fTaskMin' + task.id] &&
          this.$refs['fTaskMin' + task.id][0] &&
          document.activeElement ===
            this.$refs['fTaskMin' + task.id][0].$el.querySelector(
              '.el-input input.el-input__inner'
            )
        ) {
          console.log(
            document.activeElement,
            this.$refs['fTaskMin' + task.id][0].$el
          )
        } else {
          if (task && (task.hours || task.minutes)) {
            let seconds = moment
              .duration(
                `${task.hours ? task.hours : '00'}:${
                  task.minutes ? task.minutes : '00'
                }:00`
              )
              .asSeconds()
            task.inputValue = seconds
          }
          this.onReadingChange(task)
        }
      })
    },
    onUpload({ file, onSuccess, onError }) {
      let data = {
        parentId: this.openWorkorderId,
        module: 'prerequisitephotos',
      }

      let formData = new FormData()
      formData.append('file', file, file.name)
      for (let key in data) {
        this.$helpers.setFormData(key, data[key], formData)
      }

      return this.$http.post(`v2/photos/upload`, formData)
    },
    onUploadSuccess(response) {
      let {
        data: { responseCode, result },
      } = response

      if (responseCode === 0) {
        this.photos.push(result.photos[0])
        this.getPrerequisiteStatus()
        this.loadactivitie()
      }
    },
    onPhotoDelete(file) {
      this.deletePhoto(file)
    },
    focusCommentBoxtextarea() {
      this.$refs.commentBoxRef.focus()
      this.commentFocus = true
    },
    callOnReadingChangeWithoutValidation(currentTaskObj) {
      if (!isEmpty(currentTaskObj)) {
        this.onReadingChange(currentTaskObj, false)
        this.resetReadingFieldUnitDialogBox()
      }
    },
    beforeCloseDialogBox(done) {
      this.resetReadingFieldUnitDialogBox()
      done()
    },
    closeReadingFieldSuggestionDialogBox() {
      this.readingFieldSuggestionToggle = false
      this.readingFieldSuggestions = []
      this.currentTaskObj = null
    },
    closeReadingFieldErrorDialogBox() {
      this.resetReadingFieldUnitDialogBox()
      this.readingData = 6 // meterReset state is now false .
      this.consumptionData = ''
    },
    resetReadingFieldUnitDialogBox() {
      this.readingFieldErrorToggle = false
      this.readingFieldError = null
      this.readingFieldSuggestionToggle = false
      this.readingFieldSuggestions = []
      this.currentTaskObj = null
    },
    deletePhoto(value) {
      if (value) {
        this.$http
          .post('v2/photos/delete?module=prerequisitephotos', {
            id: value.id,
            parentId: value.parentId,
            photoId: value.photoId,
          })
          .then(response => {
            if (
              response.data &&
              response.data.result &&
              response.data.result.Id
            ) {
              let pht = this.photos.find(r => r.id === response.data.result.Id)
              if (pht) {
                this.photos.splice(this.photos.indexOf(pht), 1)
              }
              this.getPrerequisiteStatus()
              this.loadactivitie()
              this.$message.success(this.$t('common._common.delete_success'))
            } else {
              this.$message.error(
                this.$t('common._common.deletion_operation_failed')
              )
            }
          })
      }
    },
    getPrerequisiteStatus() {
      let self = this
      self.$http
        .get(
          'v2/workorders/getPrerequisiteStatus?workOrderId=' +
            self.openWorkorderId
        )
        .then(function(response) {
          self.workorder.preRequestStatus =
            response.data.result.preRequestStatus
        })
    },
    openTenant(id) {
      if (id) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('tenant', pageTypes.OVERVIEW) || {}

          if (name) {
            this.$router.push({
              name,
              params: {
                viewname: 'all',
                id,
              },
            })
          }
        } else {
          let url = '/app/tm/tenants/all/' + id + '/overview'
          this.$router.replace({ path: url })
        }
      }
    },
    updateWoActions() {
      this.loadactivitie()
    },
    refreshWoDetails() {
      this.fetchWo(true).then(() => {})
    },
    onStateFlowFormSaved() {
      this.updateWoActions()
      this.loadComments()
    },
    associate(selectedObj) {
      this.visibility = false
      this.selectedTask.resource = selectedObj
    },
    preRequisiteApprove() {
      this.updateWorkOrder([this.openWorkorderId], {
        preRequisiteApproved: true,
        preRequestStatus: 3,
      })
    },
    blurCommentBox(e) {
      let itTargetPar = e.path.filter(ele => ele.id === 'commentBoxPar')
      if (
        (!this.newComment.body || this.newComment.body.trim() === '') &&
        !isEmpty(this.comments) &&
        !itTargetPar.length
      ) {
        this.commentFocus = false
      }
    },
    loadReadingFieldUnits() {
      this.$http.get('v2/getReadingFieldUnits').then(response => {
        if (response.data && response.data.responseCode === 0) {
          this.metricWithUnits =
            response.data.result.readingFieldUnits.MetricsWithUnits
        }
      })
    },
    getReadingFieldUnits(readingFieldMetricId) {
      if (!isEmpty(this.metricWithUnits)) {
        let currentMetricId = Object.keys(this.metricWithUnits).find(
          metricUnitKey => metricUnitKey == readingFieldMetricId
        )
        let readingFieldUnits = this.metricWithUnits[currentMetricId]
        return readingFieldUnits
      }
    },
    redirectToSpecialModule(field, data) {
      if (isEmpty(field) || isEmpty(data)) return

      let { name: fieldName, lookupModule = {} } = field

      if (isEmpty(lookupModule)) return

      let { name } = lookupModule

      if (isWebTabsEnabled()) {
        let { name: routeName } =
          findRouteForModule(name, pageTypes.OVERVIEW) || {}

        if (routeName) {
          let { id } = name === 'vendors' ? data[name] : data[fieldName]

          this.$router.push({
            routeName,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        let urlHash = {
          vendors: {
            name: 'vendorsummary',
            params: { viewname: 'all', vendorid: data['vendors'].id },
          },
          custom: {
            name: 'custommodules-summary',
            params: {
              viewName: 'all',
              id: data[fieldName].id,
              moduleName: name,
            },
          },
        }
        let routerParams = {}

        if (name === 'vendors') routerParams = urlHash['vendors']
        else if (lookupModule.typeEnum === 'CUSTOM')
          routerParams = urlHash['custom']

        if (!isEmpty(routerParams)) {
          this.$router.push(routerParams)
        }
      }
    },
    async loadVendorsPicklist() {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'vendors' },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.vendorsList = options
      }
    },
    updateUrl(transition) {
      if (!isEmpty(transition)) {
        let stateObj = this.$store.getters.getTicketStatus(
          transition.toStateId,
          'workorder'
        )

        if (this.$getProperty(stateObj, 'type') === 'CLOSED') {
          return '/v2/workorders/close'
        } else return '/v2/workorders/update'
      }
    },
    transitionFilter(transition) {
      let stateObj = this.$store.getters.getTicketStatus(
        transition.toStateId,
        'workorder'
      )
      let isDisallowedState =
        stateObj.type === 'CLOSED' &&
        !hasWorkOrderClosePermission
      return !isDisallowedState
    },
  },
  watch: {
    workorder: function() {
      let title =
        '[#' + this.workorder.serialNumber + '] ' + this.workorder.subject
      this.setTitle(title)
      if (this.workorder.priority) {
        this.workorder.priority = this.getTicketPriority(
          this.workorder.priority.id
        )
      }
      if (!this.workorder.data) {
        this.workorder.data = {}
      }
      this.setDueDate(this.workorder.dueDate)
      this.updateTaskChartData()
    },
    openWorkorderId: function() {
      this.fetchWo(true)
    },
    filteredComments(list) {
      if (isEmpty(list)) this.commentFocus = true
    },
    moduleMeta(newVal, oldVal) {
      if (newVal && oldVal !== newVal) {
        this.init()
      }
    },
    isActive(isActive) {
      if (!isActive && this.canShowAllComments) this.showAllComments()
    },
  },
  beforeDestroy() {
    this.$store.dispatch('workorder/removeCurrentWo')
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
<style lang="scss" scoped>
.wo-summary-container {
  margin: -40px 20px 0px;
}
/* Hacks for portal */
.fc-wo-page-scroll {
  height: calc(100vh - 200px);
}
.portal-wo-summary-page .fc-wo-page-scroll {
  height: calc(100vh - 150px);
}
</style>
<style lang="scss">
.wo-summary-container .el-tabs__header {
  margin-bottom: 0;
}
.wo-summary-container.offset-top {
  margin-top: -96px;

  .el-tabs__content {
    margin-top: 58px;
  }

  .el-tabs__active-bar {
    top: 37px !important;
  }
}
.new-summary-view .fc__wo__task__bar > span {
  margin-left: 0px;
  /* For Safari 3.1 to 6.0 */
  -webkit-transition-property: width;
  -webkit-transition-duration: 1s;
  -webkit-transition-timing-function: ease-out;
  /* Standard syntax */
  transition-property: width;
  transition-duration: 1s;
  transition-timing-function: ease-out;
}
.wo__close__icon {
  padding-top: 20px;
  padding-right: 20px;
  font-size: 18px;
  font-weight: 600;
  color: #324056;
}
.upload-space-photos {
  position: absolute;
  bottom: 5px;
  right: 0px;
  display: none;
  z-index: 10;
  width: 100%;
  text-align: center;
  background: #fff;
  padding: 5px;
}
.duration {
  bottom: 13px;
  /* left: 10px; */
  position: absolute;
  right: 1px;
}
.fc-avatar-element:hover .upload-space-photos {
  display: block;
}

.fc-avatar-carousel .el-carousel__container {
  height: 120px;
}
.avatar-uploader .el-upload--picture-card {
  width: 98px;
  height: 89px;
  line-height: 100px;
  border-radius: 4px;
  background-color: #f3f5f5;
  border: 1px solid rgba(50, 64, 86, 0.2);
  border-style: dashed;
}
.avatar-uploader .el-icon-plus {
  font-size: 22px;
  color: #324056;
  opacity: 0.5;
  font-weight: bold;
  vertical-align: baseline;
}
.avatar-uploader .el-upload-list--picture-card .el-upload-list__item {
  width: 98px;
  height: 89px;
}
.wo-summary-page .wo__details__res {
  padding-bottom: 20px;
  display: flex;
  align-items: center;
}
.task-input-width {
  width: 150px !important;
}
#summarytask .f-element .el-radio {
  width: inherit !important;
}
.wo-duration .fc-timer .t-separate {
  margin-top: 5px !important;
}
.wo-error-dialog-body {
  padding-bottom: 70px;
}
.wo-task-group-input .el-input-group__append {
  border: none;
}
.wo-task-group-input {
  width: 200px;
}
.wo-task-group-input .el-input__inner:first-child {
  width: inherit !important;
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
  border-right: 1px solid transparent !important;
}
.wo-task-group-input .el-input__inner:first-child:hover,
.wo-task-group-input .el-input__inner:first-child:active,
.wo-task-group-input .el-input__inner:first-child:focus {
  border-right: 1px #39b3c2 transparent !important;
}
.wo-task-group-input .el-input-group__append {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
}
.wo-task-group-input .el-input-group__append .el-input__inner,
.wo-task-group-input .el-input-group__append .el-select .el-input__inner {
  width: 60px !important;
  font-size: 12px !important;
  padding-left: 10px !important;
  border-left: none;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
  border-right: 1px solid #d8dce5 !important;
}
.wo-task-group-input .el-input-group__append .el-select .el-input__inner {
  width: 100px;
}
#summarytask .wo-reading-input-width,
.wo-text-input-width .el-input__inner {
  width: 260px !important;
}
#summarytask .wo-reading-input-width .el-input__inner {
  border-right: 1px solid #d8dce5 !important;
  border-top-right-radius: 3px !important;
  border-bottom-right-radius: 3px !important;
}
.summaryTask-input .task-minutes-hours-input .el-input__inner {
  width: 126px !important;
}
.task-right-right-r-remove .el-input__inner {
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}
.task-right-left-r-remove .el-input__inner {
  border-left: none !important;
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
}
.fc-wo-page-scroll {
  .wo-widget-header {
    border-bottom: 1px solid #f7f8f9;
    padding: 10px 15px;
    display: flex;
    flex-direction: column;
    .header {
      display: flex;
      min-height: 35px;
    }
  }
}
.new-cmd-discription {
  white-space: pre-line;
}
.fc-width-input .el-input__inner {
  width: 100%;
  border-right: none !important;
  border-top-right-radius: 0 !important;
  border-bottom-right-radius: 0 !important;
}
.fc-width-input .el-select,
.fc-width-input .el-select .el-input__inner {
  width: 80px;
}

.fc-value-border .el-input__inner {
  border: 1px solid #d8dce5 !important;
}

.options-input input {
  border-bottom: 1px solid rgba(50, 64, 86, 0.2) !important;
}
#pane-prerequisites {
  .transparent-bg {
    background: none;
  }
}
</style>
