<!-- -->
<template>
  <div>
    <el-dialog
      :visible.sync="visibilityshow"
      :fullscreen="true"
      :append-to-body="true"
      :before-close="closeDialog"
      custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog50 setup-dialog"
      style="z-index: 1999"
    >
      <send-notification
        ref="notificationpage"
        :visibility.sync="actionEdit"
        :mode.sync="actionMode"
        :option="actionData"
        @onsave="actionSave"
        class="ruleDialog"
        :rule="rule"
        :module="module"
      ></send-notification>
      <div id="new-custom-rule">
        <error-banner
          :error="error"
          :errorMessage="errorMessage"
        ></error-banner>
        <el-form :model="data" ref="newCustomRule" :label-position="'top'">
          <div class="new-header-container">
            <div class="new-header-modal">
              <div class="new-header-text">
                <div class="setup-modal-title">
                  {{
                    isNew
                      ? 'New ' + moduleText + ' Action'
                      : 'Edit ' + moduleText + ' Action'
                  }}
                </div>
              </div>
            </div>
          </div>
          <div class="new-body-modal" style="padding-right: 20px;">
            <el-row>
              <el-col :span="9">
                <el-form-item prop="name">
                  <p class="fc-input-label-txt">
                    {{ moduleText }} {{ $t('setup.setupLabel.action_name') }}
                  </p>
                  <!-- Removing Disabled for isDefault :disabled="!isNew && isDefaultRule" -->
                  <el-input
                    :autofocus="true"
                    v-model="data.rule.name"
                    class="fc-name-input"
                    placeholder="Enter Workorder name"
                  >
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="9">
                <el-form-item prop="siteId">
                  <p class="fc-input-label-txt">Site</p>
                  <el-select
                    v-model="data.rule.siteId"
                    style="width:70%"
                    class="form-item"
                    placeholder="Select Site"
                  >
                    <el-option
                      v-for="site in site"
                      :key="site.id"
                      :label="site.name"
                      :value="site.id"
                    ></el-option>
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20">
                <el-form-item prop="description">
                  <p class="fc-input-label-txt">Description</p>
                  <el-input
                    v-model="data.rule.description"
                    :min-rows="1"
                    type="textarea"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    class="edittext"
                    :placeholder="$t('common._common.enter_desc')"
                    reseize="none"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="20" class="utility-block">
                <p class="subHeading-pink-txt">EXECUTE ON</p>
                <p class="small-description-txt">
                  Rule on when the {{ moduleText }} as to be executed
                </p>
              </el-col>
            </el-row>
            <!-- execute block start-->
            <div class="execute-block mT20" style="margin-bottom:30px">
              <el-form-item prop="activityType">
                <!-- Removing Disabled for isDefault :disabled="!isNew && isDefaultRule" -->
                <el-radio-group v-model="data.rule.event.activityType">
                  <el-radio-button
                    v-for="type in activityTypes"
                    :key="type.value"
                    :label="type.value"
                    >{{ type.label }}</el-radio-button
                  >
                </el-radio-group>
              </el-form-item>
            </div>
            <br /><br />
            <!-- execute block end -->
            <div class="form-input mT40">
              <new-criteria-builder
                v-model="data.rule.criteria"
                :exrule="data.rule.criteria"
                @condition="somefnt"
                :module="module"
                :title="'Specify rules for ' + moduleText + ' action'"
              ></new-criteria-builder>
            </div>
            <el-row>
              <el-col :span="20" class="utility-block">
                <p class="subHeading-pink-txt">
                  {{ $t('setup.approvalprocess.action') }}
                </p>
                <p class="small-description-txt">
                  {{ $t('setup.setupLabel.set_corresponding_rule') }}
                </p>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="10">
                <p class="details-Heading">
                  {{ $t('setup.setupLabel.send_email') }}
                </p>
                <p class="small-description-txt2">
                  {{ $t('setup.setupLabel.send_email_desc') }} <br />
                  {{ $t('setup.setupLabel.send_email_desc_continue') }}
                </p>
              </el-col>
              <el-col :span="8" class="mT20">
                <el-button
                  type="button"
                  v-bind:class="
                    isEmailConfi ? 'success-green-btn' : 'small-border-btn'
                  "
                  @click="!isEmailConfi ? addAction('mail') : null"
                  >{{ isEmailConfi ? 'Configured' : 'Configure' }}</el-button
                >
                <span v-if="isEmailConfi" class="mL10">
                  <i
                    class="el-icon-edit pointer"
                    @click="editAction(emailContext)"
                    title="Edit Mail"
                    v-tippy
                  ></i>
                  <!-- &nbsp;&nbsp;
                              <i class="el-icon-delete pointer" @click="deleteAction(index)"></i> -->
                  <span class="mL10 reset-txt pointer" @click="reset('mail')"
                    >Reset</span
                  >
                </span>
              </el-col>
            </el-row>
            <!-- v-bind:class="{'fc-widget-body': widget.header.title !== null, 'fc-widget-dragable-body': widget.header.title === null}" -->
            <el-row class="mT20 border-top-grey pT20">
              <el-col :span="10">
                <p class="details-Heading">Send SMS</p>
                <p class="small-description-txt2">
                  The system will send an <br />
                  sms based on the criteria
                </p>
              </el-col>
              <el-col :span="8" class="mT20">
                <el-button
                  type="button"
                  v-bind:class="
                    isSmsConfi ? 'success-green-btn' : 'small-border-btn'
                  "
                  @click="!isSmsConfi ? addAction('sms') : null"
                  >{{ isSmsConfi ? 'Configured' : 'Configure' }}</el-button
                >
                <span v-if="isSmsConfi" class="mL10">
                  <i
                    class="el-icon-edit pointer"
                    @click="editAction(smsContent)"
                    title="Edit Sms"
                    v-tippy
                  ></i>
                  <!-- &nbsp;&nbsp;
                              <i class="el-icon-delete pointer" @click="deleteAction(index)"></i> -->
                  <span class="mL10 reset-txt pointer" @click="reset('sms')"
                    >Reset</span
                  >
                </span>
              </el-col>
            </el-row>
            <el-row
              class="mT20 border-top-grey pT20"
              v-if="
                module !== 'alarm' ||
                  [1, 1024, 1025].includes(
                    parseInt(data.rule.event.activityType)
                  )
              "
            >
              <el-col :span="10">
                <p class="details-Heading">Send Mobile Notification</p>
                <p class="small-description-txt2">
                  The system will send <br />
                  mobile notification based on the criteria
                </p>
              </el-col>
              <el-col :span="8" class="mT20">
                <el-button
                  type="button"
                  v-bind:class="
                    isMobileConfig ? 'success-green-btn' : 'small-border-btn'
                  "
                  @click="!isMobileConfig ? addAction('mobile') : null"
                  >{{ isMobileConfig ? 'Configured' : 'Configure' }}</el-button
                >
                <span v-if="isMobileConfig" class="mL10">
                  <i
                    class="el-icon-edit pointer"
                    @click="editAction(mobileContent)"
                    title="Edit Mobile"
                    v-tippy
                  ></i>
                  <!-- &nbsp;&nbsp;
                              <i class="el-icon-delete pointer" @click="deleteAction(index)"></i> -->
                  <span class="mL10 reset-txt pointer" @click="reset('mobile')"
                    >Reset</span
                  >
                </span>
              </el-col>
            </el-row>
            <el-row
              class="mT20 border-top-grey pT20"
              v-if="
                parseInt(data.rule.event.activityType) !== 2048 &&
                  module === 'alarm'
              "
            >
              <el-col :span="10">
                <p class="details-Heading">Create Workorder</p>
                <p class="small-description-txt2">
                  The system will <br />
                  create workorder based on the criteria
                </p>
              </el-col>
              <el-col :span="8" class="mT20">
                <el-button
                  type="button"
                  v-bind:class="
                    isWOConfi ? 'success-green-btn' : 'small-border-btn'
                  "
                  @click="
                    !isWOConfi ? addAction('workorder') : null,
                      (createworkorderDialog = true)
                  "
                  >{{ isWOConfi ? 'Configured' : 'Configure' }}</el-button
                >
                <span v-if="isWOConfi" class="mL10">
                  <i
                    class="el-icon-edit pointer"
                    @click="editAction(alarmWo)"
                    title="Edit WorkOrder"
                    v-tippy
                  ></i>
                  <!-- &nbsp;&nbsp;
                              <i class="el-icon-delete pointer" @click="deleteAction(index)"></i> -->
                  <span class="mL10 reset-txt pointer" @click="reset('wo')"
                    >Reset</span
                  >
                </span>
              </el-col>
            </el-row>
            <el-row
              class="mT20 border-top-grey pT20"
              v-else-if="module === 'alarm'"
            >
              <el-col :span="10">
                <p class="details-Heading">Close Workorder</p>
                <p class="small-description-txt2">
                  The system will close the workorder
                </p>
              </el-col>
              <el-col :span="8" class="mT20">
                <el-button
                  :style="'min-width:78px;'"
                  type="button"
                  v-bind:class="
                    isWoCloseConfig ? 'success-green-btn' : 'small-border-btn'
                  "
                  @click="!isWoCloseConfig ? actionSave(closeWo) : null"
                  >{{ isWoCloseConfig ? 'Enabled' : 'Enable' }}</el-button
                >
                <span v-if="isWoCloseConfig" class="mL10">
                  <!-- &nbsp;&nbsp;
                              <i class="el-icon-delete pointer" @click="deleteAction(index)"></i> -->
                  <span class="mL10 reset-txt pointer" @click="reset('woClose')"
                    >Reset</span
                  >
                </span>
              </el-col>
            </el-row>
          </div>
          <div class="modal-dialog-footer">
            <el-button @click="closeDialog()" class="modal-btn-cancel"
              >CANCEL</el-button
            >
            <el-button
              type="primary"
              class="modal-btn-save"
              @click="save"
              :loading="saving"
              >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
            >
          </div>
          <!-- Create a workorder dialog start-->
          <el-dialog
            :visible.sync="createworkorderDialog"
            width="28%"
            class="creteawo-Dialog"
            :append-to-body="true"
          >
            <div class="setup-modal-title">Create a Workorder</div>
            <div style="margin-top:25px;" v-if="actionMode === 'workorder'">
              <el-row>
                <el-col :span="24">
                  <p class="fc-input-label-txt">Category</p>
                  <el-select
                    v-model="alarmWo.templateJson.category.id"
                    clearable
                    class="workorder-select fc-input-full-border-select2"
                    style="width: 100%;"
                  >
                    <el-option
                      v-for="category in ticketcategory"
                      :key="category.id"
                      :label="category.displayName"
                      :value="parseInt(category.id)"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
              <el-row class="mT24">
                <el-col :span="24">
                  <p class="fc-input-label-txt">Team/Staff</p>
                  <div class="fc-border-input-div2">
                    <span>{{ getTeamStaffLabel(alarmWo.templateJson) }}</span>
                    <span style="float: right;">
                      <i
                        class="el-icon-arrow-down"
                        style="position: relative;top: -4px;right: 3px;color: #c0c4cc;"
                      ></i>
                    </span>
                    <f-assignment
                      :model="alarmWo.templateJson"
                      viewtype="form"
                    ></f-assignment>
                  </div>
                  <!-- <el-select v-model="alarmWo.assignedToId" style="width: 100%;">
                              <el-option v-for="(label, value) in users" :key="value" :label="label.name" :value="value"></el-option>
                           </el-select> -->
                </el-col>
              </el-row>
              <el-row class="mT20">
                <el-col :span="24">
                  <p class="fc-input-label-txt">Priority</p>
                  <el-select
                    v-model="alarmWo.templateJson.priority.id"
                    clearable
                    style="width: 100%;"
                    class="workorder-select fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="priority in ticketpriority"
                      :key="priority.priority"
                      :label="priority.displayName"
                      :value="parseInt(priority.id)"
                    ></el-option>
                  </el-select>
                </el-col>
              </el-row>
            </div>
            <div class="modal-dialog-footer">
              <el-button
                @click="createworkorderDialog = false"
                class="modal-btn-cancel"
                >CANCEL</el-button
              >
              <el-button
                type="primary"
                class="modal-btn-save"
                @click="actionSave(alarmWo)"
                >Save</el-button
              >
            </div>
          </el-dialog>
          <!-- Create a workorder dialog end -->
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import ErrorBanner from '@/ErrorBanner'
import SendNotification from 'pages/setup/actions/SendNotification'
import FAssignment from '@/FAssignment'
import NotificationHelper from 'pages/setup/actions/NotificationHelper'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'
export default {
  props: [
    'isNew',
    'visibilityshow',
    'rule',
    'mailWidth',
    'module',
    'activityTypes',
  ],
  mixins: [NotificationHelper, TeamStaffMixin],
  data() {
    return {
      error: false,
      errorMessage: '',
      isEmailConfi: false,
      emailContext: '',
      smsContent: '',
      mobileContent: '',
      woContext: '',
      isSmsConfi: false,
      isWOConfi: false,
      isMobileConfig: false,
      isWoCloseConfig: false,
      actionMode: 'mail',
      actionEdit: false,
      idOldActionEdit: false,
      actionIndex: null,
      actionData: null,
      moduleMeta: null,
      saving: false,
      data: {
        rule: {
          name: '',
          description: '',
          ruleType: 8,
          event: {
            moduleName: 'alarm',
            activityType: '1',
          },
          criteria: null,
        },
        siteId: '',
        actions: [],
      },
      alarmWo: {
        actionType: 11,
        templateJson: {
          category: {
            id: '',
          },
          assignedTo: {
            id: '',
          },
          assignmentGroup: {
            id: '',
          },
          priority: {
            id: '',
          },
        },
        //  categoryId: null,
        //  assignedTo: {
        //    id: ''
        //  },
        //  assignmentGroup: {
        //    id: ''
        //  },
        //  priorityId: null
      },
      closeWo: {
        actionType: 12,
      },
      createworkorderDialog: false,
      ruleDialog: true,
    }
  },
  components: {
    ErrorBanner,
    NewCriteriaBuilder,
    SendNotification,
    FAssignment,
  },
  created() {
    this.$store.dispatch('loadSite')
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadGroups')
  },
  computed: {
    ...mapState({
      users: state => state.users,
      ticketcategory: state => state.ticketCategory,
      ticketpriority: state => state.ticketPriority,
      site: state => state.site,
    }),
    moduleText() {
      if (this.module === 'alarm') {
        return 'Alarm'
      } else if (this.module === 'workorder') {
        return 'Work Order'
      } else if (this.module === 'workorderrequest') {
        return 'Work Request'
      }
    },
  },
  mounted() {
    if (!this.isNew) {
      this.rule.actions = this.rule.actions ? this.rule.actions : []
      this.data = {
        rule: {
          name: this.rule.name,
          siteId: this.rule.siteId,
          description: this.rule.description,
          ruleType: this.rule.ruleType,
          event: {
            activityType: this.rule.event.activityType,
            moduleName: this.rule.event.moduleName,
          },
          id: this.rule.id,
          criteria: this.rule.criteria,
        },
        actions: this.rule.actions ? this.rule.actions : [],
      }
      this.rule.actions.forEach(d => {
        console.log(d.actionType)
        if (parseInt(d.actionType) === 3) {
          this.isEmailConfi = true
          this.emailContext = d
          //  this.isEmailConfi.active = true
        } else if (parseInt(d.actionType) === 4) {
          this.isSmsConfi = true
          this.smsContent = d
          //  this.isSmsConfi.active = true
        } else if (parseInt(d.actionType) === 7) {
          this.isMobileConfig = true
          this.mobileContent = d
          //  this.isSmsConfi.active = true
        } else if (parseInt(d.actionType) === 11) {
          this.isWOConfi = true
          this.alarmWo.templateJson.category.id =
            d.template.categoryId !== -1 ? d.template.categoryId : ''
          this.alarmWo.templateJson.assignedTo.id =
            d.template.assignedToId !== -1 ? d.template.assignedToId : ''
          this.alarmWo.templateJson.assignmentGroup.id =
            d.template.assignmentGroupId !== -1
              ? d.template.assignmentGroupId
              : ''
          this.alarmWo.templateJson.priority.id =
            d.template.priorityId !== -1 ? d.template.priorityId : ''
          this.actionMode = 'workorder'
          this.woContext = d
        } else if (parseInt(d.actionType) === 12) {
          this.isWoCloseConfig = true
        }
      })
      //  let idx = this.rule.actions.findIndex(rule => rule.actionType === 11)
      //  if (idx !== -1) {
      //    let woAction = this.rule.actions[idx]
      //    this.alarmWo.templateJson.category.id = woAction[0].template.categoryId.toString()
      //    this.alarmWo.templateJson.assignedTo.id = woAction[0].template.assignedToId
      //    this.alarmWo.te.priority.id = woAction[0].template.priorityId.toString()
      //    this.actionMode = 'workorder'
      //  }
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibilityshow', false)
    },
    somefnt(newVal) {
      this.data.rule.criteria = newVal
    },
    save() {
      let self = this
      let actions = this.data.actions
      if (!self.isNew) {
        // Removing web notifications for now
        actions = actions
          .filter(action => action.actionType !== 5)
          .map((action, i) => {
            let template
            if (action.actionType === 3) {
              template = this.$refs.notificationpage.getDataToSave(
                this.emailContext,
                'mail'
              )
            } else if (action.actionType === 4) {
              template = this.$refs.notificationpage.getDataToSave(
                this.smsContent,
                'sms'
              )
            } else if (action.actionType === 7) {
              let data = this.$refs.notificationpage.getDataToSave(
                this.mobileContent,
                'mobile'
              )
              this.formatMobileData(data.templateJson)
              template = data
            }
            this.addModuleParam(template)
            return template || action
          })
      }

      if (parseInt(this.data.rule.event.activityType) === 2048) {
        let woActionIdx = actions.findIndex(action => action.actionType === 11)
        if (woActionIdx !== -1) {
          actions.splice(woActionIdx, 1)
        }
      } else {
        let idx = actions.findIndex(action => action.actionType === 12)
        if (idx !== -1) {
          actions.splice(idx, 1)
        }
      }

      let woAction = actions.find(action => action.actionType === 11)
      if (woAction) {
        let template = this.alarmWo.templateJson
        Object.keys(woAction).forEach(
          key =>
            ['actionType', 'templateJson'].includes(key) || delete woAction[key]
        )
        if (template) {
          woAction.template = null
          woAction.templateJson = {}
          if (template.category.id > 0) {
            woAction.templateJson.category = template.category
          }
          if (template.priority.id > 0) {
            woAction.templateJson.priority = template.priority
          }
          if (template.assignmentGroup.id > 0) {
            woAction.templateJson.assignmentGroup = template.assignmentGroup
          }
          if (template.assignedTo.id > 0) {
            woAction.templateJson.assignedTo = template.assignedTo
          }
        }
      }
      Object.keys(self.data.rule.criteria.conditions).forEach(function(kessy) {
        if (
          !self.data.rule.criteria.conditions[kessy].hasOwnProperty('fieldName')
        ) {
          delete self.data.rule.criteria.conditions[kessy]
        } else {
          if (self.data.rule.criteria) {
            delete self.data.rule.criteria.conditions[kessy].valueArray
            delete self.data.rule.criteria.conditions[kessy].operatorsDataType
            delete self.data.rule.criteria.conditions[kessy].operatorLabel
          }
        }
      })

      let data = this.$helpers.cloneObject(this.data)
      data.actions = actions
      let apiaction
      if (self.isNew) {
        if (this.module === 'alarm') {
          data.rule.ruleType = 8
        } else if (this.module === 'workorder') {
          data.rule.ruleType = 11
        }
        data.rule.event.moduleName = this.module
        apiaction = 'addcustom'
      } else {
        if (this.rule.event.activityType === data.rule.event.activityType) {
          delete data.rule.event
        }
        apiaction = 'updatecustom'
      }
      this.saving = true
      this.$util
        .addOrUpdateRule(
          this.module === 'newreadingalarm' ? 'alarm' : this.module,
          data,
          !this.isNew,
          apiaction
        )
        .then(rule => {
          self.saving = false
          self.$emit('actionSaved', rule)
          self.closeDialog()
        })
        .catch(function(error) {
          self.saving = false
          console.log(error)
        })
      //  }
    },
    validateForm(ruleData) {
      if (
        ruleData.rule.name &&
        ruleData.rule.criteria.conditions[1].fieldName
      ) {
        if (ruleData.rule.criteria.conditions) {
          Object.keys(ruleData.rule.criteria.conditions).forEach(function(
            kessy
          ) {
            let condObj = ruleData.rule.criteria.conditions[kessy]
            if (!condObj.fieldName) {
              this.error = true
              this.errorMessage = 'Please selet Field name'
            } else if (!condObj.operatorLabel) {
              this.error = true
              this.errorMessage = 'Please select operator for condition'
            } else if (!condObj.value && condObj.active) {
              this.error = true
              this.errorMessage = 'Please give value for condition'
            }
          })
        } else {
          this.errorMessage =
            'Please have atleast one condition to set alarm rules'
        }
        // this.template.message && this.toAddr.length
      } else if (
        !ruleData.rule.name &&
        !ruleData.rule.criteria.conditions[1].fieldName
      ) {
        this.error = true
        this.errorMessage = 'Please fill all fields'
      } else if (!ruleData.rule.criteria.conditions[1].fieldName) {
        this.error = true
        this.errorMessage =
          'Please have atleast one condition to set alarm rules'
      }
      //  else {
      //    this.error = true
      //  }
    },
    actionSave(data) {
      let idx = this.data.actions.findIndex(
        d => d.actionType === data.actionType
      )
      if (idx !== -1) {
        this.data.actions.splice(idx, 1)
      }
      if (data.actionType === 3) {
        this.isEmailConfi = true
        this.emailContext = data
      } else if (data.actionType === 4) {
        this.isSmsConfi = true
        this.smsContent = data
      } else if (data.actionType === 7) {
        this.isMobileConfig = true
        this.formatMobileData(data.templateJson)
        this.mobileContent = data
      } else if (data.actionType === 11) {
        this.createworkorderDialog = false
        this.isWOConfi = true
        this.woContext = data
      } else if (data.actionType === 12) {
        this.isWoCloseConfig = true
        this.woContext = data
      }
      this.addModuleParam(data)
      this.data.actions.push(data)
      this.actionEdit = false
    },
    addModuleParam(template) {
      if (
        template &&
        template.templateJson &&
        template.templateJson.ftl &&
        template.templateJson.workflow &&
        !template.templateJson.workflow.parameters.some(
          param => param.name === this.module
        )
      ) {
        template.templateJson.workflow.parameters.push({
          name: this.module,
          typeString: 'String',
        })
      }
    },
    deleteAction(idx) {
      this.data.actions.splice(idx, 1)
    },
    editAction(action, idx) {
      this.idOldActionEdit = true
      this.isWoEdit = true
      this.actionData = action
      if (action.actionType === 3) {
        this.actionMode = 'mail'
        this.actionEdit = true
        this.isEmailConfi = true
      } else if (action.actionType === 4) {
        this.actionMode = 'sms'
        this.actionEdit = true
        this.isSmsConfi = true
      } else if (action.actionType === 7) {
        this.actionMode = 'mobile'
        this.actionEdit = true
        this.isMobileConfig = true
      } else if (action.actionType === 11) {
        this.createworkorderDialog = true
        this.actionMode = 'workorder'
        this.isWOConfi = true
      }
    },
    addAction(cmd) {
      this.actionMode = cmd
      this.actionData = null
      if (cmd !== 'workorder') {
        this.actionEdit = true
        this.idOldActionEdit = false
      }
    },
    reset(action, idx) {
      if (action === 'sms') {
        let idx = this.data.actions.findIndex(d => d.actionType === 4)
        this.data.actions.splice(idx, 1)
        this.smsContent = ''
        this.isSmsConfi = false
      } else if (action === 'mail') {
        let idx = this.data.actions.findIndex(d => d.actionType === 3)
        this.data.actions.splice(idx, 1)
        this.emailContext = ''
        this.isEmailConfi = false
      } else if (action === 'mobile') {
        let idx = this.data.actions.findIndex(d => d.actionType === 7)
        this.data.actions.splice(idx, 1)
        this.mobileContent = ''
        this.isMobileConfig = false
      } else if (action === 'wo') {
        let idx = this.data.actions.findIndex(d => d.actionType === 11)
        this.data.actions.splice(idx, 1)
        this.woContext = null
        this.isWOConfi = false
      } else if (action === 'woClose') {
        let idx = this.data.actions.findIndex(d => d.actionType === 12)
        this.data.actions.splice(idx, 1)
        this.isWoCloseConfig = false
      }
    },

    // Temporary...TODO need to handle in server
    formatMobileData(data) {
      let summaryPage = this.module.toUpperCase() + '_SUMMARY'
      let message = {
        content_available: true,
        summary_id: '${' + this.module + '.id}',
        sound: 'default',
        module_name: this.module,
        priority: 'high',
        text: data.message,
        click_action: summaryPage,
        title: data.subject,
      }
      data.body = JSON.stringify({
        name: this.module.toUpperCase() + '_PUSH_NOTIFICATION',
        notification: message,
        data: message,
        id: data.id,
      })
      data.to = data.id
      this.$common.setExpressionFromPlaceHolder(
        data.workflow,
        '${' + this.module + '.id}'
      )
    },
  },
}
</script>
<style>
#new-custom-rule .setting-list-view-table tbody tr {
  border-bottom: none !important;
}
.execute-block {
  height: 70px;
}
.execute-block .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  color: #39b2c2;
  background-color: #f1fdff !important;
  border-color: #39b2c2 !important;
  box-shadow: -1px 0 0 0 #39b2c2 !important;
  letter-spacing: 0.5px;
  font-size: 14px !important;
}

/* .execute-block .el-radio-button:first-child .el-radio-button__inner{
 border-left: 1px solid ;
} */

.execute-block .el-radio-button__inner {
  border-color: #e2e8ee !important;
}

.execute-block .el-radio-button__inner:hover {
  color: #39b2c2;
}

.execute-block .el-radio-button__inner {
  color: #333333;
  font-size: 14px !important;
  letter-spacing: 0.5px !important;
}

/* .execute-block .el-radio-group {
 box-shadow: 0 2px 4px 0 rgba(232, 229, 229, 0.5);
 } */
.execute-block .el-radio-button {
  padding-bottom: 10px;
}
.creteawo-Dialog .el-dialog__header {
  display: none !important;
}
.creteawo-Dialog .el-dialog__body {
  padding: 40px;
  height: 450px !important;
}
.ruleDialog {
  /* width: 40%; */
}
.ruleDialog .el-dialog {
  position: relative;
  margin: 0 auto 50px;
  background: #fff;
  border-radius: 2px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.3);
  box-sizing: border-box;
  height: 70vh;
  margin-top: 15vh !important;
}
.ruleDialog .mail-message-textarea {
  max-height: 300px;
  height: 300px;
  overflow-x: hidden;
  overflow-y: scroll;
  position: relative;
  padding-bottom: 40px;
  white-space: pre-line;
}
.ruleDialog .mail-message-textarea {
  max-height: 250px;
  height: 250px;
  overflow-y: scroll;
  padding-bottom: 40px;
}
.ruleDialog .subject .el-textarea {
  min-height: 200px;
  border: 0px solid;
  overflow-y: scroll;
  height: 240px;
}
.ruleDialog .setup-dialog-lay {
  overflow-x: hidden;
  overflow-y: scroll;
}
.reset-txt {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #30a0af;
}
.new-body-modal .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  border-left: 1px solid transparent !important;
}
.workorder-select .el-input__suffix {
  right: -1px;
}
#new-custom-rule .el-input.is-disabled .el-input__inner {
  background-color: transparent;
}
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .custom-rule-dialog {
    width: 55% !important;
  }
}
</style>
