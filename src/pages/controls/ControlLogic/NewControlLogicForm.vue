<template>
  <!-- logic form  start-->
  <el-dialog
    :visible.sync="visibility"
    width="50%"
    :before-close="closeDialog"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog"
  >
    <el-form :model="controlpoints" :label-position="'top'" ref="logicrule">
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ isNew ? 'New Control Logic' : 'Edit Control Logic' }}
          </div>
        </div>
      </div>
      <div class="new-body-modal">
        <el-form-item :label="'Name'">
          <el-input
            autofocus
            v-model="controlpoints.name"
            placeholder="Control Logic Name"
            class="fc-input-full-border2"
          ></el-input>
        </el-form-item>
        <el-form-item :label="'Execute Based On'">
          <el-radio-group
            @change="changeOptions()"
            :disabled="disableRadioGroup"
            v-model="executed"
          >
            <el-radio :label="35" class="fc-radio-btn"> Alarms</el-radio>
            <el-radio :label="36" class="fc-radio-btn"> Schedule</el-radio>
            <el-radio
              :label="34"
              class="fc-radio-btn"
              v-if="this.$helpers.isLicenseEnabled('RESOURCE_BOOKING')"
            >
              Reservation</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <!-- Alarms Executed -->
        <div v-show="executed === 35">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="'Asset Category'">
                <el-select
                  v-model="assetcategoryId"
                  placeholder="Select Asset category"
                  class="width100"
                  @change="fetchRules"
                >
                  <el-option
                    v-for="(category, index) in assetCategory"
                    :key="index"
                    :label="category.displayName"
                    :value="parseInt(category.id)"
                  ></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'Rule'">
                <el-select
                  v-model="actionTyperule"
                  filterable
                  class="width100 fc-input-full-border2"
                  @change="rcaRulesDataload"
                  loading-text="Loading..."
                  :loading="ruleLoading"
                >
                  <el-option
                    v-for="(rule, index) in rules"
                    :key="index"
                    :label="rule.name"
                    :value="rule.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item :label="'Asset'">
                <el-select
                  v-model="ruleMatchedResources"
                  filterable
                  class="width100 fc-input-full-border2"
                >
                  <el-option
                    v-for="(resource, index) in matchedResources"
                    :key="index"
                    :label="resource.name"
                    :value="resource.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item :label="'Rca Rule'">
                <el-select
                  v-model="actionRcaRule"
                  filterable
                  class="width100 fc-input-full-border2"
                  no-data-text="No Rca Rule available"
                >
                  <el-option
                    v-for="(rcarule, index) in rcarules"
                    :key="index"
                    :label="rcarule.name"
                    :value="rcarule.id"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        <!-- Schedule -->
        <div v-show="executed === 36">
          <schedule-trigger
            v-if="trigger.type === 1"
            :trigger="trigger"
            :hideFields="hideFields"
          ></schedule-trigger>
        </div>
        <!-- Reservations -->
        <div
          v-if="this.$helpers.isLicenseEnabled('RESOURCE_BOOKING')"
          v-show="executed === 34"
        >
          <!-- reservation ui -->
          <div class="fc-div-bg-border">
            <el-row>
              <el-col :span="24">
                <p class="fc-input-label-txt">Name</p>
                <el-select
                  v-model="reservationData.id"
                  filterable
                  :disabled="reservationData.disabled"
                  placeholder="Select"
                  class="width100"
                >
                  <el-option
                    v-for="(reserv, index) in reservationName"
                    :key="index"
                    :label="reserv.name"
                    :value="reserv.id"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>

            <el-row class="mT20">
              <el-col :span="12">
                <p class="fc-input-label-txt">Date Field</p>
                <el-select
                  v-model="dateFieldId"
                  placeholder="Select"
                  filterable
                  class="width300px"
                >
                  <el-option
                    v-for="(fld, index) in dateFields"
                    :key="index"
                    :label="fld.displayName"
                    :value="fld.id"
                  >
                  </el-option>
                </el-select>
              </el-col>
              <el-col :span="12" class="pL40">
                <p class="fc-input-label-txt mB10">
                  {{ $t('setup.setupLabel.scheduled_type') }}
                </p>
                <el-radio-group v-model="scheduleType">
                  <el-radio
                    class="fc-radio-btn"
                    :label="2"
                    :key="2"
                    :value="true"
                    >On</el-radio
                  >
                  <el-radio
                    class="fc-radio-btn"
                    :label="3"
                    :key="3"
                    :value="false"
                    >After</el-radio
                  >
                  <el-radio
                    class="fc-radio-btn"
                    :label="1"
                    :key="1"
                    :value="false"
                    >Before</el-radio
                  >
                </el-radio-group>
              </el-col>
            </el-row>
            <el-row
              class="mT30"
              :gutter="20"
              v-if="[1, 3].includes(parseInt(scheduleType))"
            >
              <!-- <el-col :span="12">
                    <p class="fc-input-label-txt">Days</p>
                   <el-select v-model="dateObject.days" clearable placeholder="Select" class="fc-input-full-border-select2 width100">
                    <el-option v-for="index in 10" :label="index" :key="index+ 1" :value="index"></el-option>
                  </el-select>
                </el-col> -->
              <el-col :span="12">
                <p class="fc-input-label-txt">Hours</p>
                <el-select
                  v-model="dateObject.hours"
                  clearable
                  filterable
                  class="fc-input-full-border-select2 width100"
                >
                  <el-option
                    v-for="index in $constants.HOURS"
                    :label="index"
                    :key="index + 1"
                    :value="index"
                  ></el-option>
                </el-select>
              </el-col>
              <el-col :span="12">
                <p class="fc-input-label-txt">Mins</p>
                <el-select
                  v-model="dateObject.minute"
                  filterable
                  placeholder="Select"
                  class="fc-input-full-border-select2"
                >
                  <el-option
                    v-for="index in $constants.MINUTES"
                    :label="index"
                    :key="index + 1"
                    :value="index"
                  ></el-option>
                </el-select>
              </el-col>
            </el-row>
          </div>

          <!-- reservation ui -->
        </div>
        <!-- <div class="mT20">
          <div class="fc-text-pink text-uppercase">CONDITION</div>
          <el-checkbox @change="changeOptions()" v-model="isFormulaField">Enable Condition</el-checkbox>
        </div>
        <f-formula-builder v-if="isFormulaField" v-model="controlpoints.workFlow" module="formulaField"
          hideCodeView="false" hideModeChange="false" class="mT20"></f-formula-builder> -->
        <!-- control actions start-->
        <div class="fc-text-pink text-uppercase mT20">Command Action</div>
        <el-collapse
          class="new-rule-collapse position-relative controllogic-collapse"
          v-model="activeNames"
          :accordion="true"
        >
          <el-collapse-item
            class="rule-border-blue mT20 position-relative"
            style="border-left: 1px solid rgb(228, 235, 241);"
            v-for="(action, cindex) in controlaction"
            :key="cindex"
            :name="action.name"
          >
            <template slot="title">
              {{ action.name }}
            </template>
            <div class="">
              <el-radio-group
                v-model="action.templateJson.actionType"
                @change="changecondition"
              >
                <el-radio :label="1" class="fc-radio-btn">Point</el-radio>
                <el-radio :label="2" class="fc-radio-btn">Group</el-radio>
              </el-radio-group>
            </div>

            <div
              class="pT20"
              v-show="
                action.templateJson.actionType === 1 ||
                  action.templateJson.actionType === -1
              "
            >
              <el-row>
                <el-col :span="4">
                  <div class="label-txt-black">Asset</div>
                </el-col>
                <el-col :span="15">
                  <el-form-item>
                    <el-select
                      v-model="action.templateJson.resource"
                      placeholder="Select asset"
                      class="fc-input-full-border2 width100"
                      @change="fetchFields"
                    >
                      <el-option
                        v-for="(asset, index) in assets"
                        :key="index"
                        :label="asset.name"
                        :value="asset.id"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="">
                <el-col :span="4">
                  <div class="label-txt-black">Field</div>
                </el-col>
                <el-col :span="15">
                  <el-form-item>
                    <el-select
                      v-model="action.templateJson.metric"
                      placeholder="Select fields"
                      class="fc-input-full-border2 width100"
                    >
                      <el-option
                        v-for="(field, index) in assetFields[
                          action.templateJson.resource
                        ]"
                        :key="index"
                        :label="field.displayName"
                        :value="field.id"
                      ></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="">
                <el-col :span="4">
                  <div class="label-txt-black">Value</div>
                </el-col>
                <el-col :span="15">
                  <el-form-item>
                    <el-input
                      autofocus
                      v-model="action.templateJson.val"
                      placeholder="Enter the value"
                      type="number"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <img
                src="~assets/remove-icon.svg"
                style="height:18px;width:18px;margin-right: 3px;"
                @click="deleteaction(cindex)"
                class="delete-icon pointer"
              />
            </div>

            <div v-show="action.templateJson.actionType === 2" class="pT20">
              <el-row class="">
                <el-col :span="4">
                  <div class="label-txt-black">Actions Group</div>
                </el-col>
                <el-col :span="15">
                  <el-form-item>
                    <el-select
                      v-model="action.templateJson.controlActionGroupId"
                      filterable
                      class="width100 fc-input-full-border2"
                    >
                      <el-option
                        v-for="(grouprule, index) in rcagrouplist"
                        :key="index"
                        :label="grouprule.name"
                        :value="grouprule.id"
                      >
                      </el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row class="">
                <el-col :span="4">
                  <div class="label-txt-black">Value</div>
                </el-col>
                <el-col :span="15">
                  <el-form-item>
                    <el-input
                      autofocus
                      v-model="action.templateJson.val"
                      placeholder="Enter the value"
                      type="number"
                      class="fc-input-full-border2"
                    ></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <img
                src="~assets/remove-icon.svg"
                style="height:18px;width:18px;margin-right: 3px;"
                @click="deleteaction(cindex)"
                class="delete-icon pointer"
              />
            </div>
          </el-collapse-item>
        </el-collapse>
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill mT20"
          @click="addaction"
          >Add Command</el-button
        >
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          @click="submitForm()"
          class="modal-btn-save"
          :loading="saving"
        >
          {{ saving ? 'Submitting...' : 'SAVE' }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ScheduleTrigger from '@/TriggerTypeSchedule'
import { mapState } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
import { deepCloneObject } from 'util/utility-methods'
export default {
  props: [
    'visibility',
    'controlLogic',
    'ruleId',
    'isNew',
    'reservationEditObj',
  ],
  data() {
    return {
      hideFields: {
        skipEvery: true,
      },
      // Reservations
      dateFields: [],
      scheduleType: null,
      interval: null,
      dateObject: {
        days: null,
        minute: 0,
        hours: 0,
        minu: null,
      },
      ruleLoading: false,
      assetcategoryId: null,
      dateFieldId: '',
      time: null,
      executed: 35,
      id: '',
      rulefield: {},
      isEdit: false,
      controllogicsingleruleload: [],
      isFormulaField: false,
      controlObj: {},
      ruleMatchedResources: '',
      actionRcaRule: '',
      actionGroupRule: '',
      assets: [],
      rules: [],
      rcarules: [],
      rcagrouplist: [],
      actionTyperule: '',
      reservationName: [],
      reservationData: { id: '', disabled: false },
      assetFields: {},
      logicaction: {},
      showControlAction: true,
      controlaction: [],
      activeNames: ['actionName0'],
      isNewControl: false,
      controllogicformload: [],
      resourceQuery: null,
      isIncludeResource: false,
      chooserVisibility: false,
      chooserVisibilityAction: false,
      resourceType: 'asset',
      saving: false,
      loading: true,
      updatelogiclist: [],
      selectedResourceList: [],
      actionIndex: 0,
      selectedResource: {
        name: '',
      },
      formula: {
        name: '',
        workflow: null,
        includedResources: [],
        frequency: null,
      },
      trigger: {
        name: name,
        type: 1,
        startDate: new Date(),
        startTime: new Date().getTime(),
        showSkip: false,
        basedOn: 'Date',
        schedule: {
          times: ['00:00'],
          frequency: 1,
          skipEvery: -1,
          showSkip: false,
          values: [],
          frequencyType: 1,
          weekFrequency: -1,
          yearlyDayValue: null,
          monthValue: -1,
          yearlyDayOfWeekValues: [],
        },
        stopAfter: 'never',
        endTime: new Date(),
        startReading: null,
        readingFieldId: null,
        stopAfterReading: 'never',
        endReading: null,
        readingInterval: null,
        assignedTo: null,
        reminders: [],
        criteria: null,
      },
      frequencyTypes: [
        {
          label: 'Do not repeat',
          value: 0,
        },
      ],
      controlpoints: {
        name: '',
        actionTyperule: '',
      },
      disableRadioGroup: false,
      rcaRuleSelectShow: false,
    }
  },
  components: {
    ScheduleTrigger,
  },
  computed: {
    isAsset() {
      return this.resourceType === 'asset'
    },
    isMultiResource() {
      return (
        this.isAsset &&
        (!this.rule ||
          !Object.keys(this.rule).length ||
          this.rule.assetCategoryId > 0)
      )
    },
    triggerTypes() {
      let types = [
        {
          label: 'Schedule',
          value: 1,
        },
      ]
      types.splice(1, 2)
      return types
    },
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
    selectedRule() {
      if (this.actionTyperule) {
        return this.rules.find(rule => this.actionTyperule === rule.id)
      }
      return null
    },
    matchedResources() {
      return this.selectedRule ? this.selectedRule.matchedResources : []
    },
    resourceData() {
      return {
        assetCategory: this.controlpoints.assetCategoryId,
        isIncludeResource: this.isIncludeResource,
        selectedResources: Array.isArray(this.selectedResourceList)
          ? this.selectedResourceList.map(resource => ({
              id:
                resource && typeof resource === 'object'
                  ? resource.id
                  : resource,
            }))
          : this.selectedResourceList.id,
      }
    },
    //   isDateTimeField () {
    //   let isDatetime = false
    //   this.dateFields.filter(d => {
    //     if(d.fieldId === this.dateFieldId){
    //       return isDatetime = d.dataTypeEnum._name === 'DATE_TIME'
    //     }
    //   })
    // return isDatetime
    // }
  },
  mounted: function() {
    this.addaction()
    this.getfetchrule()
    this.setReservationForOptions()
    this.setOptionsForAssets()
    this.init()
    this.fetchReservationFields()
    this.fieldrules()
    this.loadrulegroup()
    if (this.reservationEditObj) {
      this.constructForReservation()
    }
    if (this.controlpoints.interval) {
      this.dateObject = this.$helpers.secTodaysHoursMinu(this.interval)
    }
    this.$forceUpdate()
  },
  watch: {
    trigger: function() {
      console.log('watching')
    },
    isNew: function() {
      this.init()
    },
  },
  methods: {
    init: function() {
      if (!this.isNew) {
        this.controlpoints = this.controlLogic
      } else {
        this.controlpoints = {
          name: '',
          actionTyperule: '',
        }
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    addControllogic() {
      this.controlObj = this.$helpers.cloneObject(this.controlmodel)
      this.isNewControl = true

      this.visibility = true
    },
    changeOptions() {
      this.$forceUpdate()
    },
    changecondition() {
      this.$forceUpdate()
    },
    submitForm() {
      let self = this
      self.saving = true
      if (!self.controlpoints.resourceId) {
        self.controlpoints.resourceId = -1
      }
      let url
      let payload = null
      let context = {
        actions: this.controlaction,
        name: this.controlpoints.name,
        id: this.controlpoints.id,
        resourceId: this.ruleMatchedResources,
        event: {
          moduleName: 'alarm',
        },
      }
      if (this.executed === 36) {
        payload = {
          workflowRuleContext: context,
        }
        payload.workflowRuleContext.schedule = this._data.trigger.schedule
        payload.workflowRuleContext.event.activityType = 2097152
      } else if (this.executed === 35) {
        payload = {
          readingAlarmRuleContext: context,
        }
        if (this.selectedRule) {
          payload.readingAlarmRuleContext.readingRuleGroupId = this.selectedRule.ruleGroupId
        }
        payload.readingAlarmRuleContext.event.activityType = 3
        payload.readingAlarmRuleContext.rcaRuleId = this.actionRcaRule || -1
      } else if (this.executed === 34) {
        payload = {
          recordRule: context,
        }
        delete payload.recordRule.resourceId
        payload.recordRule.event.activityType = 524288
        payload.recordRule.event.moduleName = 'reservation'
        payload.recordRule.scheduleType = this.scheduleType
        payload.recordRule.parentId = this.reservationData.id
        payload.recordRule.dateFieldId = this.dateFieldId
        payload.recordRule.ruleType = 34
        payload.recordRule.interval =
          (this.dateObject.hours * 60 + this.dateObject.min) * 60
      }
      if (this.controlpoints.id) {
        url = '/v2/controlAction/updateControlActionRule'
        if (this.executed === 34) {
          url = '/v2/recordrule/addOrUpdate'
        }
        self.$http
          .post(url, payload)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.controlpoints = response.data.result.readingAlarmRuleContext
              this.controlpoints = {
                name: '',
              }
              self.$message.success('ControlLogic Edit Successfully')
              self.$emit('update:visibility', false)
              self.$emit('saved')
            } else {
              self.$message.error(response.data.message)
            }
          })
          .catch(error => {
            console.log(error)
            self.$message.error('Unable to Edit')
          })
      } else {
        url = '/v2/controlAction/addControlActionRule'
        if (this.executed === 34) {
          url = '/v2/recordrule/addOrUpdate'
        }
        self.$http
          .post(url, payload)
          .then(response => {
            if (response.data.responseCode === 0) {
              this.controlpoints = response.data.result.readingAlarmRuleContext
              this.controlpoints = {
                name: '',
              }
              self.$message.success('ControlLogic Added Successfully')
              self.$emit('update:visibility', false)
              self.$emit('saved')
            } else {
              self.$message.error(response.data.message)
            }
            self.saving = false
          })
          .catch(error => {
            console.log(error)
            self.saving = false
            self.$message.error('Unable to Edit')
          })
      }
    },
    getfetchrule() {
      if (!this.controlLogic) {
        return
      }
      let self = this
      self.loading = true
      let url =
        '/v2/controlAction/getControlActionRule?ruleId=' + this.controlLogic.id
      this.$http.get(url).then(response => {
        this.controlLogicEdit = response.data.result.workflowRule || null
        this.setData(response.data.result.workflowRule)
        self.loading = false
      })
    },
    setData(workflowRule) {
      this.executed = workflowRule.moduleName
      this.executed = workflowRule.ruleType
      this.controlpoints.name = workflowRule.name
      this.controlpoints.id = workflowRule.id
      this.controlaction = this.decoeactions(workflowRule.actions)
      this.ruleMatchedResources = workflowRule.resourceId
      if (this.executed === '35') {
        // this.assetcategoryId =
        this.ruleMatchedResources = workflowRule.resourceId
      } else if (this.executed === '34') {
        this.reservationData.id = workflowRule.parentId
        this.reservationData.disabled = true
        this.dateFieldId = workflowRule.dateFieldId
        this.scheduleType = workflowRule.scheduleType
        this.interval = workflowRule.interval
        this.dateObject = this.$helpers.secTodaysHoursMinu(this.interval)
      } else if (this.executed === '36') {
        this.trigger = workflowRule
        this.executed = workflowRule.ruleType
      }

      this.ruleMatchedResources = workflowRule.resourceId
      this._data.trigger.schedule = workflowRule.schedule
    },
    decoeactions(actions) {
      actions.forEach((rt, index) => {
        if (!rt.templateJson) {
          this.$set(rt, 'templateJson', rt.template)
          this.$set(rt, 'name', `Command Action ${index + 1}`)
        }
      })
      return actions
    },
    rcaRulesDataload(ruleId) {
      let self = this
      self.loading = true
      this.rcarules = []
      let url = `/v2/alarm/rules/fetchRule?ruleId=${ruleId}`
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (
            response.data.result.alarmRule &&
            response.data.result.alarmRule.alarmRCARules
          ) {
            this.rcarules = response.data.result.alarmRule.alarmRCARules
          }
          self.loading = false
        }
      })
    },
    showActionAssetChooser(index) {
      this.actionIndex = index
      this.chooserVisibilityAction = true
    },
    addaction() {
      this.controlaction.push({
        actionType: 18,
        templateJson: {
          val: '',
          resource: null,
          metric: null,
          actionType: 1,
          controlActionGroupId: null,
        },
        name: 'Command Action' + (this.controlaction.length + 1),
      })
    },
    deleteaction(key) {
      this.controlaction.splice(key, 1)
    },
    onTriggerTypeChange(val) {
      if (val != 1) {
        this.trigger.schedule = null
      } else {
        this.trigger.schedule = deepCloneObject(PMMixin.INITIAL_SCHEDULE)
      }
    },
    setOptionsForAssets() {
      let self = this
      self.loading = true
      let url = '/v2/controlAction/getControllableAssets'
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controllableResources) {
            self.assets = [
              { id: -1, name: 'Current Asset' },
              ...response.data.result.controllableResources,
            ]
          }
          // this.addaction()
          self.loading = false
        }
      })
    },
    fetchFields(resourceId) {
      if (this.assetFields[resourceId]) {
        return
      }
      let self = this
      self.loading = true
      let url =
        '/v2/controlAction/getControllableFields?resourceId=' + resourceId
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controllableFields) {
            self.$set(
              self.assetFields,
              resourceId,
              response.data.result.controllableFields
            )
          }
          self.loading = false
        }
      })
    },
    fieldrules() {
      let self = this
      self.loading = true
      let url = '/v2/rules/all'
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.rules) {
            self.rules = response.data.result.rules
          }
          self.loading = false
        }
      })
    },
    loadrulegroup() {
      let self = this
      self.loading = true
      let url = '/v2/controlAction/getControlGroups'
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          if (response.data.result.controlActionGroups) {
            self.rcagrouplist = response.data.result.controlActionGroups
          }
          self.loading = false
        }
      })
    },
    setReservationForOptions() {
      let self = this
      self.loading = true
      let url = '/v2/reservations/view/all?parentId'
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          if (response.data.result.reservations) {
            self.reservationName = response.data.result.reservations
          }
          self.loading = false
        }
      })
    },
    fetchReservationFields() {
      this.$http.get('/module/meta?moduleName=reservation').then(response => {
        let tempFields
        if (response.data.meta) {
          tempFields = response.data.meta.fields
          this.dateFields = tempFields.filter(d => {
            return d.dataTypeEnum._name === 'DATE_TIME'
          })
        }
      })
    },
    constructForReservation() {
      if (this.reservationEditObj) {
        this.executed = 34
        this.reservationData.id = this.reservationEditObj.id
        this.reservationData.disabled = true
        this.disableRadioGroup = true
        this.controlpoints.name = this.reservationEditObj.name + '_Control'
      }
    },
    fetchRules(assetcategoryId) {
      this.rules = []
      let queryparams = {
        assetCategoryId: {
          operatorId: 36,
          value: [assetcategoryId + ''],
        },
      }
      this.ruleLoading = true
      let url = `/v2/rules/all?filters=${encodeURIComponent(
        JSON.stringify(queryparams)
      )}&includeParentFilter=true`
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.rules = response.data.result.rules
            this.ruleLoading = false
          }
        })
        .catch(() => {
          this.ruleLoading = false
        })
    },
  },
}
</script>
