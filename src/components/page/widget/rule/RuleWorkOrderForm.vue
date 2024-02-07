<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="closeForm"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog50 setup-dialog "
      style="z-index: 1999"
    >
      <template v-if="loading"
        ><spinner :show="loading" size="80"></spinner
      ></template>
      <template v-if="!loading">
        <div id="new-custom-rule">
          <error-banner
            :error="error"
            :errorMessage="errorMessage"
          ></error-banner>
          <el-form>
            <div class="new-header-container">
              <div class="new-header-modal">
                <div class="new-header-text">
                  <div class="setup-modal-title">
                    {{ $t('rule.faultToWorkorder.fault_to_wo') }}
                  </div>
                </div>
              </div>
            </div>
            <div class="new-body-modal" v-if="loading">
              <el-row>
                <el-col :span="12" class="pR10">
                  <span class="line1 loading-shimmer width50"></span>
                  <span class="line2 loading-shimmer width100"></span>
                </el-col>
                <el-col :span="12" class="pL10">
                  <span class="line1 loading-shimmer width50"></span>
                  <span class="line2 loading-shimmer width100"></span>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <span class="line3 loading-shimmer width50"></span>
                  <span class="line4 loading-shimmer width100"></span>
                </el-col>
              </el-row>
            </div>
            <div class="new-body-modal" v-else>
              <div class="pB30" style="border-bottom:1px solid #e5e5ea;">
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block fc-id bold pointer f14 pL5 pT10"
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('maintenance._workorder.execute_on') }}
                    </p>
                    <p class="small-description-txt mB10">
                      {{ $t('rule.faultToWorkorder.define_execute') }}
                    </p>
                    <el-form-item prop="activityType">
                      <el-select
                        v-model="workflowRule.event.activityType"
                        placeholder="Select"
                        class="width100 fc-input-full-border-select2"
                      >
                        <el-option
                          v-for="(type, index) in createWoActivityType"
                          :key="index"
                          :label="type.label"
                          :value="type.value"
                        >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <FaultToWoFields
                  v-if="activityType === 'Field Change'"
                  :workflowRule="workflowRule"
                  :module="moduleName"
                  @selectedFields="addWoCreationFields"
                  :isEdit="isEdit"
                />
                <SchedulerForRuleWo
                  v-if="activityType === 'On Date'"
                  :moduleFields="moduleFields"
                  :module="moduleName"
                  :workflowRule="workflowRule"
                  @schedulerFields="addWodateField"
                  :isEdit="isEdit"
                />
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block fc-id bold pointer f14 pL5 pT10"
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('setup.approvalprocess.action') }}
                    </p>
                    <el-form-item
                      :label="$t('rule.faultToWorkorder.workorder_template')"
                      prop="formId"
                    >
                      <el-select
                        v-model="alarmWo.templateJson.formId"
                        @change="loadWOFormFields(formId, true)"
                        placeholder="Select"
                        class="fc-input-full-border-select2 width100"
                      >
                        <el-option
                          v-for="(form, index) in templates"
                          :label="form.displayName"
                          :key="index"
                          :value="form.id"
                        ></el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block fc-id bold pointer f14 pL5 pT10"
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('rule.faultToWorkorder.workorder_field_mapping') }}
                    </p>
                    <div class="fc__layout__has__row pB10">
                      <div
                        v-for="(fieldMatcher, index) in alarmFieldMatcher"
                        :key="index"
                      >
                        <el-row
                          class="visibility-visible-actions fc-row-hover pT10 pB10  pointer"
                        >
                          <el-col :span="9">
                            <el-select
                              v-model="fieldMatcher.field"
                              @change="statusFieldName(fieldMatcher, index)"
                              :disabled="disableFormFieldSelect(index)"
                              filterable
                              class="fc-input-full-border-select2 width100"
                            >
                              <el-option
                                v-for="(field, index) in woFormFields"
                                :key="index"
                                :label="field.displayName"
                                :value="field.name"
                              ></el-option>
                            </el-select>
                          </el-col>
                          <el-col :span="9">
                            <div class>
                              <div v-if="!fieldMatcher.fieldObj">
                                <el-input
                                  v-model="fieldMatcher.value"
                                  class="fc-input-full-border-select2 mL20 width100"
                                ></el-input>
                              </div>
                              <div v-else-if="fieldMatcher.isSpacePicker">
                                <el-input
                                  v-model="field.parseLabel"
                                  disabled
                                  class="fc-input-full-border-select2 mL20 width100"
                                >
                                  <i
                                    slot="suffix"
                                    class="el-input__icon el-icon-search space-picker"
                                  ></i>
                                </el-input>
                              </div>
                              <div v-else class="flex-middle">
                                <el-select
                                  @change="changeDataType(fieldMatcher)"
                                  v-if="lookupCondition(fieldMatcher)"
                                  filterable
                                  collapse-tags
                                  v-model="fieldMatcher.valueArray"
                                  class="fc-input-full-border-select2 width100 mL20"
                                >
                                  <el-option
                                    v-for="(value, key) in pickList(
                                      fieldMatcher
                                    )"
                                    :key="key"
                                    :label="value"
                                    :value="key"
                                  ></el-option>
                                </el-select>
                                <el-select
                                  @change="changeDataType(fieldMatcher)"
                                  v-else-if="
                                    dataTypeEnum(fieldMatcher)._name ===
                                      'LOOKUP'
                                  "
                                  filterable
                                  collapse-tags
                                  v-model="fieldMatcher.valueArray"
                                  class="fc-input-full-border-select2 width100 mL20"
                                >
                                  <el-option
                                    v-for="(value, key) in pickList(
                                      fieldMatcher
                                    )"
                                    :key="key"
                                    :label="value"
                                    :value="key"
                                  ></el-option>
                                </el-select>
                                <el-input
                                  v-else
                                  v-model="fieldMatcher.value"
                                  class="fc-input-full-border-select2 mL20 width130px"
                                ></el-input>
                              </div>
                            </div>
                          </el-col>
                          <el-col :span="4">
                            <div
                              class="visibility-hide-actions pT10 mR20 text-right pointer"
                            >
                              <fc-icon
                                group="action"
                                name="circle-plus"
                                @click="addRowWO"
                                class="pointer"
                              ></fc-icon>
                              <fc-icon
                                v-if="
                                  fieldMatch.length > 1 &&
                                    !(fieldMatch.length === 1)
                                "
                                group="action"
                                name="circle-minus"
                                @click="deleteRow(index)"
                                class="pointer mL5"
                              ></fc-icon>
                            </div>
                          </el-col>
                        </el-row>
                      </div>
                    </div> </el-col
                ></el-row>
                <div class="mT40">
                  <el-checkbox
                    v-model="includeComments"
                    @change="createComment = !createComment"
                    >{{
                      $t('rule.faultToWorkorder.include_commands')
                    }}</el-checkbox
                  >
                </div>
                <el-row>
                  <el-col :span="24">
                    <el-input
                      v-if="createComment"
                      v-model="workflowRule.comments.create"
                      :min-rows="1"
                      type="textarea"
                      :autosize="{ minRows: 1, maxRows: 1 }"
                      :placeholder="$t('rule.faultToWorkorder.enter_commands')"
                      reseize="none"
                      class="fc-input-full-border-select2 pT10"
                    >
                    </el-input>
                  </el-col>
                </el-row>
                <div>
                  <div class="mT40">
                    <el-checkbox
                      v-model="workflowRule.isRecommendationAsTask"
                      >{{
                        $t('rule.faultToWorkorder.fault_to_workorder_task')
                      }}</el-checkbox
                    >
                  </div>
                  <div class="mT40">
                    <el-checkbox v-model="workflowRule.isPossibleCauseAsDesc">{{
                      $t('rule.faultToWorkorder.fault_to_workorder_desc')
                    }}</el-checkbox>
                  </div>
                </div>
                <div>
                  <div class="pT30 switch-align">
                    <div>
                      <p class="form-item bold ">
                        {{ $t('rule.faultToWorkorder.skip_creation') }}
                      </p>
                    </div>
                    <div>
                      <el-switch
                        v-model="workflowRule.isSkip"
                        @change="skipactions"
                      >
                      </el-switch>
                    </div>
                  </div>
                  <div v-if="workflowRule.isSkip">
                    <el-row>
                      <el-col
                        :span="24"
                        class="utility-block fc-id cpointer f14 pL5 pT10"
                      >
                        <p class="subHeading-pink-txt">
                          {{ $t('common._common.criteria') }}
                        </p>
                        <div class="small-description-txt mB10">
                          {{ $t('rule.faultToWorkorder.skip_criteria') }}
                        </div>
                        <CriteriaBuilder
                          key="criteria-woCreation"
                          class="pB20 pR75 "
                          v-model="workflowRule.woCriteria"
                          :moduleName="actionModule"
                          :disabled="!workflowRule.isSkip"
                        />
                      </el-col>
                    </el-row>
                    <div class="mT40">
                      <el-checkbox
                        v-model="includeCommentsOnSkip"
                        @change="skipComment = !skipComment"
                        :disabled="!workflowRule.isSkip"
                        >{{
                          $t('rule.faultToWorkorder.include_commands')
                        }}</el-checkbox
                      >
                    </div>
                    <el-row>
                      <el-col :span="24">
                        <el-input
                          v-if="skipComment"
                          v-model="workflowRule.comments.skip"
                          :min-rows="1"
                          type="textarea"
                          :autosize="{ minRows: 1, maxRows: 1 }"
                          :placeholder="
                            $t('rule.faultToWorkorder.enter_commands')
                          "
                          reseize="none"
                          class="fc-input-full-border-select2 pT10"
                        >
                        </el-input>
                      </el-col>
                    </el-row>
                  </div>
                </div>
              </div>
              <div>
                <div class="pT20 switch-align">
                  <div>
                    <p class="form-item bold ">
                      {{ $t('rule.faultToWorkorder.auto_close_wo') }}
                    </p>
                  </div>
                  <div>
                    <el-switch v-model="closeWorkflowRule.status"> </el-switch>
                  </div>
                </div>
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block pT20 fc-id bold pointer f14 pL5 "
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('maintenance._workorder.execute_on') }}
                    </p>
                    <p class="small-description-txt mB10">
                      {{ $t('rule.faultToWorkorder.define_execute') }}
                    </p>
                    <el-form-item prop="activityType">
                      <el-select
                        v-model="closeEvent.activityType"
                        placeholder="Select"
                        class="width100 fc-input-full-border-select2"
                        :disabled="!closeWoStatus"
                      >
                        <el-option
                          v-for="(type, index) in closeWoActivityType"
                          :key="index"
                          :label="type.label"
                          :value="type.value"
                        >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <FaultToWoFields
                  v-if="closeActivityType === 'Field Change'"
                  :workflowRule="closeWorkflowRule"
                  :module="moduleName"
                  @selectedFields="addCloseWoFields"
                  :isEdit="isEdit"
                />
                <SchedulerForRuleWo
                  v-if="closeActivityType === 'On Date'"
                  :moduleFields="moduleFields"
                  :module="moduleName"
                  :workflowRule="closeWorkflowRule"
                  @schedulerFields="addClosedateField"
                  :isEdit="isEdit"
                />
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block fc-id cpointer f14 pL5 pT10"
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('common._common.criteria') }}
                    </p>
                    <div class="small-description-txt mB10">
                      {{ $t('rule.faultToWorkorder.close_criteria') }}
                    </div>
                    <CriteriaBuilder
                      key="criteria-closeWoCriteria"
                      class="pB20 pR75 "
                      v-model="closeWoCriteria"
                      :moduleName="actionModule"
                      :disabled="!closeWoStatus"
                    />
                  </el-col>
                </el-row>
                <el-row>
                  <el-col
                    :span="24"
                    class="utility-block fc-id bold pointer f14 pL5 pT10"
                  >
                    <p class="subHeading-pink-txt">
                      {{ $t('setup.approvalprocess.action') }}
                    </p>
                    <p class="small-description-txt mB10">
                      {{ $t('rule.faultToWorkorder.workorder_status') }}
                    </p>
                    <el-form-item prop="Actions">
                      <el-select
                        v-model="closeWoAction.templateJson.new_state"
                        placeholder="Select"
                        class="width100 fc-input-full-border-select2"
                        :disabled="!closeWoStatus"
                      >
                        <el-option
                          v-for="(status, index) in statusList"
                          :key="index"
                          :label="status.label"
                          :value="status.value"
                        >
                        </el-option>
                      </el-select>
                    </el-form-item>
                  </el-col>
                </el-row>
                <div class="mT40">
                  <el-checkbox
                    v-model="includeCloseComments"
                    @change="closeComment = !closeComment"
                    :disabled="!closeWoStatus"
                    >{{
                      $t('rule.faultToWorkorder.include_commands')
                    }}</el-checkbox
                  >
                </div>
                <el-row>
                  <el-col :span="24">
                    <el-input
                      v-if="closeComment"
                      v-model="closeWorkflowRule.comments.close"
                      :min-rows="1"
                      type="textarea"
                      :autosize="{ minRows: 1, maxRows: 1 }"
                      :placeholder="$t('rule.faultToWorkorder.enter_commands')"
                      reseize="none"
                      class="fc-input-full-border-select2 pT10"
                      :disabled="!closeWoStatus"
                    >
                    </el-input>
                  </el-col>
                </el-row>
              </div>
            </div>
            <div class="modal-dialog-footer">
              <el-button @click="closeForm()" class="modal-btn-cancel">{{
                $t('setup.users_management.cancel')
              }}</el-button>
              <el-button
                type="primary"
                class="modal-btn-save"
                @click="saveForm"
                :loading="isloading"
                >{{
                  isloading
                    ? $t('maintenance._workorder.saving')
                    : $t('maintenance._workorder.save')
                }}</el-button
              >
            </div>
          </el-form>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { CriteriaBuilder } from '@facilio/criteria'
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'
import FaultToWoFields from 'src/components/page/widget/rule/FaultToWoFields.vue'
import SchedulerForRuleWo from 'src/components/page/widget/rule/SchedulerForRuleWo.vue'
import NewCustomTypeRuleVue from 'pages/setup/notificationAndWorkflows/NewCustomTypeRule.vue'

export default {
  components: { CriteriaBuilder, FaultToWoFields, SchedulerForRuleWo },
  extends: NewCustomTypeRuleVue,
  props: [
    'visibility',
    'modules',
    'moduleName',
    'ruleId',
    'rulename',
    'isEdit',
    'details',
    'activityTypes',
  ],
  data() {
    return {
      errorMessage: '',
      error: '',
      isloading: false,
      comments: null,
      closeEvent: {
        moduleName: 'newreadingalarm',
        activityType: 2048,
      },
      wofields: [],
      closefields: [],
      templates: [],
      woModuleFields: [],
      status: true,
      updateComments: null,
      woStatus: true,
      statusActions: [],
      alarmWo: {
        actionType: 11,
        templateJson: {
          formId: null,
          fieldMatcher: [],
        },
      },
      closeWoAction: {
        actionType: 12,
        templateJson: { new_state: null },
      },
      workflowRule: {
        scheduleType: 3,
        ruleType: 48,
        event: {
          moduleName: 'newreadingalarm',
          activityType: 549755813888,
        },
        actions: [],
        status: true,
        name: null,
        fields: [],
        interval: null,
        dateFieldId: -1,
        woCriteria: null,
        comments: {
          create: null,
          skip: null,
        },
        isSkip: false,
        id: null,
        isRecommendationAsTask: true,
        isPossibleCauseAsDesc: true,
      },
      includeComments: true,
      includeCommentsOnSkip: true,
      closeWoCriteria: null,
      woFormFields: [],
      picklistOptions: {},
      assets: null,
      fields: [],
      categoryMetric: null,
      closeWorkflowRule: {
        name: '',
        scheduleType: 3,
        ruleType: 48,
        fields: [],
        interval: null,
        dateFieldId: -1,
        actions: [],
        comments: { close: null },
        woCriteria: {},
        status: true,
        id: null,
      },
      includeCloseComments: true,
      reqWoFields: [],
      optionalWoFields: [],
      closeWoFields: [],
      dateFieldToExclude: [
        'sysCreatedTime',
        'sysModifiedTime',
        'sysDeletedTime',
      ],
      beforeDateFieldsToExclude: ['createdTime', 'modifiedTime'],
      dateFieldName: ['DATE', 'DATE_TIME'],
      metaInfo: {},
      dateObject: {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      },
      statusList: [],
      closeState: null,
      loading: false,
    }
  },
  created() {
    this.fetchMetaFields()
    this.$store.dispatch('loadTicketStatus', this.actionModule)
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadGroups')
  },
  mounted() {
    this.init()
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketcategory: state => state.ticketCategory,
      assetCategory: state => state.assetCategory,
      ticketpriority: state => state.ticketPriority,
    }),
    ...mapGetters(['getTicketStatusPickList']),
    alarmFieldMatcher() {
      let { alarmWo } = this
      let { templateJson = {} } = alarmWo
      let { fieldMatcher = [] } = templateJson
      return fieldMatcher
    },
    closeActivityType() {
      let { activityTypes, closeEvent = {} } = this
      let { activityType } = closeEvent || {}
      let activity = []
      if (activityTypes) {
        activity = activityTypes.filter(types => types.value === activityType)
      }
      return activity[0].label
    },
    activityType() {
      let { activityTypes, workflowRule } = this
      let { event } = workflowRule || {}
      let { activityType } = event || {}
      let activity = []
      if (activityTypes) {
        activity = activityTypes.filter(types => types.value === activityType)
      }
      return activity[0].label
    },
    closeWoStatus() {
      let { closeWorkflowRule = {} } = this
      let { status } = closeWorkflowRule
      return status
    },
    skipComment() {
      let { includeCommentsOnSkip } = this
      return includeCommentsOnSkip
    },
    createComment() {
      let { includeComments } = this
      return includeComments
    },
    closeComment() {
      let { includeCloseComments } = this
      return includeCloseComments
    },
    createWoActivityType() {
      let { activityTypes } = this || {}
      let activity = []
      if (activityTypes) {
        activity = activityTypes.filter(types => types.value !== 2048)
      }
      return activity
    },
    moduleFields() {
      return this.$getProperty(this.metaInfo, 'fields', null)
    },
    closeWoActivityType() {
      let { activityTypes } = this || {}
      let activity = []
      if (activityTypes) {
        activity = activityTypes.filter(types => types.value !== 549755813888)
      }
      return activity
    },
    actionModule() {
      return 'workorder'
    },
    formId() {
      let { alarmWo } = this || {}
      let { templateJson } = alarmWo || {}
      let { formId } = templateJson || {}
      if (!isEmpty(formId)) {
        return formId
      }
      return null
    },
    fieldMatch() {
      let { alarmWo } = this
      let { templateJson } = alarmWo
      let { fieldMatcher } = templateJson
      return fieldMatcher
    },
  },
  methods: {
    lookupCondition(fieldMatcher) {
      let { fieldObj } = fieldMatcher
      let { displayType, dataTypeEnum } = fieldObj || {}
      return dataTypeEnum === 'LOOKUP' || displayType === 'LOOKUP_SIMPLE'
    },
    pickList(fieldMatcher) {
      let { field } = fieldMatcher
      let { picklistOptions = {} } = this
      return picklistOptions[field]
    },
    dataTypeEnum(fieldMatcher) {
      let { fieldObj } = fieldMatcher
      let { dataTypeEnum } = fieldObj || {}
      return dataTypeEnum
    },

    async getMetaFields() {
      let { module } = this
      if (module) {
        let url = `/module/meta?moduleName=${module}`
        let { data } = await API.get(url, {
          module,
        })
        this.metaInfo = data.meta
      }
    },
    woStatusList() {
      let { closeWoAction, statusList, isEdit } = this
      let { templateJson } = closeWoAction
      for (const [key, value] of Object.entries(
        this.getTicketStatusPickList('workorder')
      )) {
        let data = {
          label: value,
          value: key,
        }
        statusList.push(data)
      }
      this.closeState = statusList.filter(state => state.label === 'Closed')

      if (!isEdit) {
        this.$set(templateJson, 'new_state', this.closeState[0]?.value)
      }
    },
    async init() {
      this.loading = true
      await this.loadWoTemplates('workorder')
      this.woStatusList()
      this.addRowWO()
      await this.initPrefill()
      await this.getMetaFields()
      this.skipactions()
      this.loading = false
    },
    async initPrefill() {
      let { isEdit } = this
      if (isEdit) {
        await this.prefill()
      }
    },

    changeDataType(criteria) {
      let { fieldObj, dateObject, valueArray } = criteria || {}
      let { dataTypeEnum } = fieldObj || {}
      if (['DATE_TIME', 'DATE'].includes(dataTypeEnum)) {
        if (dateObject) {
          let interval = this.$helpers.daysHoursMinuToSec(dateObject)
          criteria.value = interval * 1000 // second to milliseconds
        }
      } else {
        if (valueArray) {
          criteria.value = { id: valueArray }
        }
      }
      this.$forceUpdate()
    },

    statusFieldName(selectedField, index) {
      let { field } = selectedField || {}
      let { alarmWo, picklistOptions } = this
      let { templateJson } = alarmWo || {}
      let { fieldMatcher } = templateJson || {}

      if (!isEmpty(field)) {
        let fld = this.woFormFields.filter(
          field => field.name === selectedField.field
        )

        fieldMatcher[index].field = selectedField.field
        fieldMatcher[index].fieldObj = fld[0].field
        fieldMatcher[index].isSpacePicker = false
        fieldMatcher[index].columnName = fld[0].field.completeColumnName

        if (!isEmpty(fld)) {
          let { field, lookupModuleName, name } = fld[0] || {}
          let { dataTypeEnum, specialType, lookupModule } = field || {}

          if (lookupModuleName === 'site') {
            this.$set(fieldMatcher[index].fieldObj, 'dataTypeEnum', 'LOOKUP')
            this.loadPickList(lookupModuleName, 'siteId')
          }
          if (dataTypeEnum === 'ENUM') {
            this.$set(picklistOptions, field.name, field.enumMap)
          }
          if (dataTypeEnum === 'LOOKUP' && specialType) {
            this.loadSpecialTypePickList(specialType, name)
          } else if (dataTypeEnum === 'LOOKUP' && lookupModule) {
            if (lookupModule.name === 'ticketstatus') {
              this.$set(
                picklistOptions,
                name,
                this.getTicketStatusPickList(this.module)
              )
            } else if (lookupModule.name === 'ticketpriority') {
              let priority = {}
              this.ticketpriority.forEach(d => {
                priority[d.id] = d.displayName
              })
              this.$set(picklistOptions, name, priority)
            } else {
              this.loadPickList(lookupModule.name, name)
            }
          }
        }
      }
    },

    skipactions() {
      let { closeWorkflowRule } = this
      let { status } = closeWorkflowRule
      this.$set(this, 'includeCloseComments', status)
    },
    async addRowWO() {
      let { alarmWo } = this
      let { templateJson } = alarmWo || {}
      let { fieldMatcher } = templateJson || {}

      fieldMatcher.push({
        field: '',
        isSpacePicker: false,
        value: null,
        parseLabel: null,
        valueArray: null,
        dateObject: {},
        fieldObj: null,
      })
    },

    async fetchMetaFields() {
      let { module: moduleName } = this
      let { data } = await API.get('/module/metafields', {
        moduleName,
      })
      this.fields = this.$getProperty(data, 'meta.fields', [])
    },
    loadCategoryMetric(id) {
      this.$util.loadAssetReadingFields(-1, id).then(fields => {
        this.categoryMetric = fields
      })
      this.$util
        .loadAsset({ withReadings: true, categoryId: id })
        .then(response => {
          this.assets = []
          this.assets.push({
            id: '${workorder.resource.id}',
            name: 'Current Asset',
          })
          this.assets.push(...response.assets)
        })
    },
    async prefill() {
      let { details } = this || {}
      let { woCreation: workOrderCreation } = details || {}
      let { closeWo } = details || {}
      if (!isEmpty(workOrderCreation)) {
        let workflowRule = this.$getProperty(details, 'woCreation')
        this.$set(this, 'workflowRule', workflowRule)
        this.dateObj = this.$getProperty(workOrderCreation, 'interval')

        this.alarmWo.templateJson = this.$getProperty(
          workOrderCreation.actions[0].template,
          'originalTemplate'
        )

        this.$set(workOrderCreation, 'status', true)
        let { formId } = this || {}
        await this.loadWOFormFields(formId, false)

        let i = 0
        let { alarmWo } = this
        let { templateJson } = alarmWo
        let { fieldMatcher } = templateJson
        fieldMatcher.forEach(field => {
          this.statusFieldName(field, i)
          i++
        })
      }

      if (!isEmpty(closeWo)) {
        let { closeWoAction } = this
        let closeWorkflowRule = this.$getProperty(details, 'closeWo')
        this.$set(this, 'closeWorkflowRule', closeWorkflowRule)
        let { woCriteria } = closeWo || {}

        this.$set(this, 'closeWoCriteria', woCriteria)
        this.$set(
          closeWoAction,
          'templateJson',
          closeWo.actions[0].template.additionInfo
        )
        this.closeEvent.activityType = this.$getProperty(
          closeWo.event,
          'activityType'
        )
      }
    },
    deleteAction(idx) {
      this.workflowRule.actions.splice(idx, 1)
      this.closeWorkflowRule.actions.splice(idx, 1)
    },
    serializeData() {
      let { rulename } = this || {}
      let { workflowRule = {}, closeWorkflowRule = {} } = this
      this.$set(workflowRule, 'name', rulename)
      this.$set(closeWorkflowRule, 'name', rulename)

      this.setAction()
      if (!this.validateForm(workflowRule)) {
        return false
      }

      delete workflowRule.commentsJsonStr
      delete closeWorkflowRule.commentsJsonStr
      if (isEmpty(this.closeWoCriteria)) {
        this.setDefaultCloseCriteria()
      }

      closeWorkflowRule = {
        ...closeWorkflowRule,
        woCriteria: this.closeWoCriteria,
      }
      if (!this.validateForm(this.closeWorkflowRule)) {
        return false
      }
      let ruleWoData = { woCreation: workflowRule, closeWo: closeWorkflowRule }

      return ruleWoData
    },
    setDefaultCloseCriteria() {
      let { closeState } = this
      let criteria = {
        conditions: {
          1: {
            fieldName: 'moduleState',
            operatorId: 37,
            value: closeState[0].value,
          },
        },
        pattern: '( 1 )',
      }
      this.$set(this, 'closeWoCriteria', criteria)
    },
    setAction() {
      let { workflowRule, alarmWo } = this || {}
      let { isEdit } = this
      if (isEdit) {
        this.deleteAction(0)
      }
      if (!isEmpty(alarmWo)) {
        let { actions, event } = workflowRule || {}
        let { templateJson } = alarmWo
        let { fieldMatcher } = templateJson
        fieldMatcher.forEach(d => {
          if (!isEmpty(d.fieldObj)) {
            this.$set(d, 'fieldObj', null)
          }
        })
        actions.push(alarmWo)
        this.$set(workflowRule, 'activityType', event.activityType)
      }

      if (!isEmpty(this.closeWorkflowRule)) {
        let { closeWorkflowRule, closeWoFields } = this || {}
        let { actions } = closeWorkflowRule || {}
        let { closeEvent } = this || {}

        actions.push(this.closeWoAction)
        this.$set(this.closeWorkflowRule, 'event', closeEvent)
        this.$set(
          this.closeWorkflowRule,
          'activityType',
          closeEvent.activityType
        )
        this.closeWorkflowRule = {
          ...closeWorkflowRule,
          fields: closeWoFields,
        }
      }
    },
    checkComments() {
      let { closeComment, createComment, skipComment } = this
      let { closeWorkflowRule, workflowRule } = this
      if (!skipComment) {
        this.$set(workflowRule, 'comments.skip', null)
      }
      if (!createComment) {
        this.$set(workflowRule, 'comments.create', null)
      }
      if (!closeComment) {
        this.$set(closeWorkflowRule, 'comments.close', null)
      }
    },
    addCloseWoFields(selectedFields) {
      let fields = []
      selectedFields.forEach(d => {
        fields.push(d)
      })
      this.$set(this, 'closeWoFields', fields)
    },
    addWoCreationFields(selectedFields) {
      let { workflowRule } = this || {}
      this.workflowRule = { ...workflowRule, fields: selectedFields }
    },

    async saveForm() {
      let { isEdit } = this || {}
      this.isloading = true
      let ruleWoData = this.serializeData()
      this.checkComments()
      if (ruleWoData) {
        if (isEdit) {
          this.updateRuleWorkOrder(ruleWoData)
        } else {
          this.addRuleWorkOrder(ruleWoData)
        }
      }
    },
    closeForm() {
      this.$emit('close')
      this.$emit('update:visibility', false)
      this.$emit('loadPage')
    },

    async updateRuleWorkOrder(ruleWoData) {
      let url = 'v3/readingrule/actions/updateRuleWo'
      let { error, data } = await API.post(url, {
        faultToWorkorder: ruleWoData,
        ruleId: this.ruleId,
      })
      if (error) {
        this.isloading = false
        this.$message.error('Error Occured')
      } else {
        this.closeForm()
        this.$message.success('WorkOrderForm Updated')
      }
    },
    async addRuleWorkOrder(ruleWoData) {
      let url = 'v3/readingrule/actions/addRuleWo'
      let { error, data } = await API.post(url, {
        faultToWorkorder: ruleWoData,
        ruleId: this.ruleId,
      })
      if (error) {
        this.isloading = false
        this.$message.error('Error Occured')
      } else {
        this.closeForm()
        this.$message.success('WorkOrder Form Configured')
      }
    },

    async loadWoTemplates(moduleName) {
      this.loadWoFields(moduleName)
      let { alarmWo, isEdit } = this
      let url = `/v2/forms?moduleName=${moduleName}`
      this.isLoading = true
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.$set(this, 'templates', forms)
        let form = this.$getProperty(this, 'templates.0.id', null)
        if (!isEdit) {
          this.$setProperty(alarmWo.templateJson, 'formId', form)
          this.loadWOFormFields(form, true)
        }
      }
    },
    loadSpecialTypePickList(specialType, fieldName) {
      let pickOption = {}
      let store = this.$store.state
      if (specialType === 'users') {
        let { users } = store
        let userList = users
        pickOption['$' + '{LOGGED_USER}'] = 'Current User'
        for (let user of userList) {
          pickOption[user.id] = user.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'groups') {
        let { groups } = store
        let groupList = groups
        for (let group of groupList) {
          pickOption[group.groupId] = group.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'basespace') {
        let { spaces } = store
        let spaceList = spaces
        for (let space of spaceList) {
          pickOption[space.id] = space.name
        }
        this.$set(this.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'alarmType') {
        this.$set(
          this.picklistOptions,
          fieldName,
          this.$constants.AlarmCategory
        )
      } else if (specialType === 'sourceType') {
        this.$set(this.picklistOptions, fieldName, this.$constants.SourceType)
      }
    },
    async loadPickList(moduleName, fieldName) {
      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(this.picklistOptions, fieldName, options)
      }
    },
    async loadWOFormFields(formId, change) {
      if (formId > 0) {
        let url = `/v2/forms/workorder?formId=${formId}`
        let { data, error } = await API.get(url)
        if (error) {
          let { message } = error
          this.$message.error(message)
        } else {
          let { form } = data || {}
          let { fields } = form || {}
          this.woFormFields = (fields || []).flatMap(fld => {
            return !isEmpty(fld.field) && fld.name !== 'siteId' ? fld : []
          })

          await this.filterWoFormFields(change)
        }
      }
    },
    async filterWoFormFields(change) {
      let { woFormFields } = this || {}
      this.reqWoFields = woFormFields.filter(fld => fld.required === true)
      this.optionalWoFields = woFormFields.filter(fld => fld.required === false)
      if (this.reqWoFields) {
        let { alarmWo } = this
        let { templateJson } = alarmWo
        let { fieldMatcher } = templateJson
        if (change) {
          let i = 0
          fieldMatcher.splice(0, fieldMatcher.length)
          this.reqWoFields.forEach(d => {
            if (!isEmpty(d.name)) {
              this.addRowWO()
              this.$set(fieldMatcher[i], 'field', d.name)
              if (d.name === 'subject') {
                this.$set(
                  fieldMatcher[i],
                  'value',
                  '${newreadingalarm.subject}'
                )
              }
              this.statusFieldName(fieldMatcher[i], i)
              ++i
            }
          })
        }
      }
    },
    disableFormFieldSelect(index) {
      let { reqWoFields } = this
      if (index < reqWoFields.length) {
        return true
      }
      return false
    },
    loadWoFields(moduleName) {
      this.$util.loadFields(moduleName, false).then(fields => {
        this.woModuleFields = fields
      })
    },
    deleteRow(index) {
      this.alarmWo.templateJson.fieldMatcher.splice(index, 1)
    },
    addWodateField(scheduleFields) {
      let { dateFieldId, scheduleType, dateObject, checkDateTimeField } =
        scheduleFields || {}
      let { workflowRule } = this || {}
      this.$set(workflowRule, 'dateFieldId', dateFieldId)
      this.$set(workflowRule, 'scheduleType', scheduleType)
      this.dateField(workflowRule, dateObject, checkDateTimeField)
    },
    addClosedateField(scheduleFields) {
      let { dateFieldId, scheduleType, dateObject, checkDateTimeField } =
        scheduleFields || {}
      let { closeWorkflowRule } = this || {}
      this.$set(closeWorkflowRule, 'dateFieldId', dateFieldId)
      this.$set(closeWorkflowRule, 'scheduleType', scheduleType)
      this.dateField(closeWorkflowRule, dateObject, checkDateTimeField)
    },
    dateField(workflowRule, dateObject, checkDateTimeField) {
      workflowRule.interval = null
      let { closeEvent } = this
      if (
        closeEvent.activityType === 524288 ||
        parseInt(workflowRule.event.activityType) === 524288
      ) {
        if (dateObject) {
          if (workflowRule.dateFieldId) {
            if (!checkDateTimeField) {
              dateObject.hours = 0
              dateObject.minute = 0
            }
          }
          if (
            workflowRule.scheduleType === 1 ||
            workflowRule.scheduleType === 3
          ) {
            workflowRule.interval = this.$helpers.daysHoursMinuToSec(dateObject)
          } else {
            workflowRule.interval = null
          }
        }
      }
      if (!workflowRule.dateFieldId) {
        delete workflowRule.dateFieldId
        delete workflowRule.interval
      }
      if (workflowRule.dateFieldId && this.checkDateTimeField) {
        workflowRule.time = null
      }
    },
    dateFieldsCheck(rule, dateObj) {
      if (rule.event.activityType === 524288 && !isEmpty(rule.dateFieldId)) {
        if (dateObj && [1, 3].includes(parseInt(rule.scheduleType))) {
          let dateObject = this.$helpers.secTodaysHoursMinu(dateObj)
          this.dateObject = {
            ...dateObject,
          }
        }
        return rule.dateFieldId
      }
      this.dateObject = {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      }
      return null
    },
    validateWoFormFields(ruleData) {
      this.error = false
      let validate = true
      let { reqWoFields } = this
      let { actions } = ruleData || {}
      let { templateJson = {} } = actions[0]
      let { fieldMatcher } = templateJson
      reqWoFields.forEach(d => {
        if (d.name !== 'siteId') {
          let val = fieldMatcher.filter(val => d.name === val.field)
          if (isEmpty(val[0]) || (!val[0]?.value && isEmpty(val[0].value))) {
            validate = false
          }
        }
      })
      return validate
    },
    validateForm(ruleData) {
      this.error = false
      let { actions } = ruleData || {}
      if (ruleData.event.activityType === 524288) {
        if (!ruleData.dateFieldId) {
          this.isloading = false
          this.error = true
          this.errorMessage = 'Please select field for on date activity'
          return false
        }
        if (ruleData.scheduleType === 3) {
          if (!ruleData.interval) {
            this.isloading = false
            this.error = true
            this.errorMessage = 'Please select duration'
            return false
          }
        }
      } else if (ruleData.event.activityType === 1048576) {
        if (!ruleData.fields || !ruleData.fields.length > 0) {
          this.error = true
          this.isloading = false
          this.errorMessage =
            'Please select atleast on field for field change activity'
          return false
        }
      }
      if (!isEmpty(actions[0].templateJson.fieldMatcher)) {
        if (!this.validateWoFormFields(ruleData)) {
          this.error = true
          this.isloading = false
          this.errorMessage = 'Please fill mandatory form fields'
          return false
        }
      }
      return true
    },
  },
}
</script>
<style lang="scss">
.switch-align {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.space-picker {
  line-height: 0px !important;
  font-size: 16px !important;
  cursor: pointer;
}
</style>
