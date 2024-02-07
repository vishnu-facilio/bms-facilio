<template>
  <div>
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
    <push-notification
      v-if="showMobileConfig"
      ref="push-notification"
      :actionObj="mobileContent"
      :moduleName="module"
      :metaFields="fields"
      @onClose="closeNotifiDialog"
      @onSave="actionSave"
    >
    </push-notification>
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
    <el-row v-if="actions.includes('email')" class="mT20">
      <el-col :span="10">
        <p class="details-Heading">{{ $t('setup.setupLabel.send_email') }}</p>
        <p class="small-description-txt2">
          {{ $t('setup.setupLabel.send_email_desc') }} <br />
          {{ $t('setup.setupLabel.send_email_desc_continue') }}
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="isEmailConfi ? 'success-green-btn' : 'small-border-btn'"
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
          <span class="mL10 reset-txt pointer" @click="reset('mail')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <el-row v-if="workPermitActionPhone" class="mT20">
      <el-col :span="10">
        <p class="details-Heading">Make a Call</p>
        <p class="small-description-txt2">
          The system will make a <br />
          call based on the criteria
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="
            isPhoneCallConfi ? 'success-green-btn' : 'small-border-btn'
          "
          @click="!isPhoneCallConfi ? addAction('phonecall') : null"
          >{{ isPhoneCallConfi ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isPhoneCallConfi" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(phoneCallContext)"
            title="Edit Phone Call"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('phonecall')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <el-row
      v-if="actions.includes('sms') && $helpers.isLicenseEnabled('SMS')"
      class="mT20 border-top-grey pT20"
    >
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
          v-bind:class="isSmsConfi ? 'success-green-btn' : 'small-border-btn'"
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
          <span class="mL10 reset-txt pointer" @click="reset('sms')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <el-row v-if="workPermitActionWhatsapp && false" class="mT20">
      <el-col :span="10">
        <p class="details-Heading">Send Whatsapp</p>
        <p class="small-description-txt2">
          The system will send a <br />
          whatsapp message based on the criteria
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="
            isWhatsappConfi ? 'success-green-btn' : 'small-border-btn'
          "
          @click="!isWhatsappConfi ? addAction('whatsapp') : null"
          >{{ isWhatsappConfi ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isWhatsappConfi" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(whatsappContext)"
            title="Edit Whatsapp Content"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('whatsapp')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <el-row
      class="mT20 border-top-grey pT20"
      v-if="
        (module !== 'alarm' ||
          [1, 1024, 1025].includes(parseInt(rule.rule.event.activityType))) &&
          actions.includes('mobile') &&
          module != 'agentAlarm'
      "
    >
      <el-col :span="10">
        <p class="details-Heading">Send In-app Notification</p>
        <p class="small-description-txt2">
          The system will send <br />
          notification based on the criteria
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
          <span class="mL10 reset-txt pointer" @click="reset('mobile')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>
    <el-row
      class="mT20 border-top-grey pT20"
      v-if="
        parseInt(rule.rule.event.activityType) !== 2048 &&
          actions.includes('createwo')
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
          v-bind:class="isWOConfi ? 'success-green-btn' : 'small-border-btn'"
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
          <span class="mL10 reset-txt pointer" @click="reset('wo')">Reset</span>
        </span>
      </el-col>
    </el-row>
    <el-row
      class="mT20 border-top-grey pT20"
      v-else-if="actions.includes('closewo')"
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
          <span class="mL10 reset-txt pointer" @click="reset('woClose')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>
    <el-row
      class="mT20 border-top-grey pT20"
      v-if="actions.includes('fieldUpdate')"
    >
      <el-col :span="10">
        <p class="details-Heading">Field Update</p>
        <p class="small-description-txt2">
          The system will update <br />
          field value based on the criteria
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="isFieldConfi ? 'success-green-btn' : 'small-border-btn'"
          @click="
            !isFieldConfi ? addAction('fieldChange') : null,
              (fieldUpdateValue = true)
          "
          >{{ isFieldConfi ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isFieldConfi" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(fieldContext)"
            title="Edit Field"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('fieldChange')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <el-row class="mT20 border-top-grey pT20" v-if="actions.includes('script')">
      <el-col :span="10">
        <p class="details-Heading">Execute Script</p>
        <p class="small-description-txt2">
          The system will execute <br />
          script based on the criteria
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="
            isScriptConfigured ? 'success-green-btn' : 'small-border-btn'
          "
          @click="
            !isScriptConfigured ? addAction('script') : null,
              (showScriptDialog = true)
          "
          >{{ isScriptConfigured ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isScriptConfigured" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(scriptData)"
            title="Edit Script"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('script')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>
    <el-row
      class="mT20 border-top-grey pT20"
      v-if="actions.includes('changeStatus')"
    >
      <el-col :span="10">
        <p class="details-Heading">Change Status</p>
        <p class="small-description-txt2">
          The system will change <br />
          status based on the criteria
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="
            isStatusConfigured ? 'success-green-btn' : 'small-border-btn'
          "
          @click="
            !isStatusConfigured ? addAction('changeStatus') : null,
              (openStatusDialog = true)
          "
          >{{ isStatusConfigured ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isStatusConfigured" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(changeStatus)"
            title="Edit Status"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('changeStatus')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>

    <!-- Create Control -->

    <el-row
      class="mT20 border-top-grey pT20"
      v-if="
        $helpers.isLicenseEnabled('CONTROL_ACTIONS') &&
          actions.includes('controls')
      "
    >
      <el-col :span="10">
        <p class="details-Heading">Control</p>
        <p class="small-description-txt2">
          Control actions
          <br />
        </p>
      </el-col>
      <el-col :span="8" class="mT20">
        <el-button
          type="button"
          v-bind:class="
            isControlConfi ? 'success-green-btn' : 'small-border-btn'
          "
          @click="
            !isControlConfi ? addAction('controls') : null,
              (controlsAction = true)
          "
          >{{ isControlConfi ? 'Configured' : 'Configure' }}</el-button
        >
        <span v-if="isControlConfi" class="mL10">
          <i
            class="el-icon-edit pointer"
            @click="editAction(controlData)"
            title="Edit Field"
            v-tippy
          ></i>
          <span class="mL10 reset-txt pointer" @click="reset('controls')"
            >Reset</span
          >
        </span>
      </el-col>
    </el-row>
    <!-- End of Create  -->

    <!-- Create a workorder dialog start-->
    <el-dialog
      :visible.sync="createworkorderDialog"
      width="28%"
      class="creteawo-Dialog"
      :append-to-body="true"
    >
      <div class="setup-modal-title">
        Create a Workorder
      </div>
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

    <!-- Update Field Value dialog start -->
    <field-update-dialog
      v-if="fieldUpdateValue"
      :statusFieldName="statusFieldName"
      :fieldChange="fieldChange"
      :moduleFields="moduleFields"
      :module="module"
      :picklistOptions="picklistOptions"
      :addRow="addRow"
      :actionSave="actionSave"
      :fieldUpdateValue.sync="fieldUpdateValue"
      :durationConfig="{ minInterval: 1 }"
    ></field-update-dialog>
    <ChangeStatusDialog
      v-if="openStatusDialog"
      :actionObj="changeStatus"
      :moduleName="module"
      :moduleId="moduleId"
      @onSave="actionSave"
      @onClose="openStatusDialog = false"
    ></ChangeStatusDialog>
    <email-action
      v-if="showEmailActions"
      ref="email-notification"
      :moduleName="module"
      :actionObj="emailContext"
      :rule="rule.rule"
      @onClose="closeEmailActions"
      @onSave="actionSave"
    >
    </email-action>
    <!-- Update Field Value dialog end -->

    <!-- Control Dialog -->
    <el-dialog
      :visible.sync="controlsAction"
      width="40%"
      class="fieldchange-Dialog pB15"
      :append-to-body="true"
    >
      <div class="height400 overflow-y-scroll pB50">
        <div class="setup-modal-title mB20">Control Action</div>
        <el-row>
          <el-col :span="24">
            <p class="fc-input-label-txt">Category</p>
            <el-select
              v-model="controlData.templateJson.assetCategory"
              @change="
                loadCategoryMetric(controlData.templateJson.assetCategory)
              "
              clearable
              class="workorder-select fc-input-full-border-select2"
              style="width: 100%;"
            >
              <el-option
                v-for="category in assetCategory"
                :key="category.id"
                :label="category.displayName"
                :value="parseInt(category.id)"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="pT10">
          <el-col :span="24">
            <p class="fc-input-label-txt">Metric</p>
            <el-select
              v-model="controlData.templateJson.metric"
              clearable
              class="workorder-select fc-input-full-border-select2"
              style="width: 100%;"
            >
              <el-option
                v-for="metric in categoryMetric"
                :key="metric.id"
                :label="metric.displayName"
                :value="String(metric.id)"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="pT10">
          <el-col :span="24">
            <p class="fc-input-label-txt">Asset</p>
            <el-select
              v-model="controlData.templateJson.resource"
              clearable
              class="workorder-select fc-input-full-border-select2"
              style="width: 100%;"
            >
              <el-option
                v-for="category in assets"
                :key="category.id"
                :label="category.name"
                :value="String(category.id)"
              ></el-option>
            </el-select>
          </el-col>
        </el-row>
        <el-row class="pT10">
          <el-col :span="24">
            <p class="fc-input-label-txt">Value</p>
            <f-formula-builder
              style="padding-top: 20px;"
              title=""
              module="workorder"
              v-model="controlData.templateJson.workflow"
              :assetCategory="controlData.templateJson.category"
              class="rule-condition-formula"
              :setResultAsExpr="true"
            ></f-formula-builder>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="controlsAction = false" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="actionSave(controlData)"
          >Save</el-button
        >
      </div>
    </el-dialog>
    <!-- script change dialog-->
    <ScriptDialog
      v-if="showScriptDialog"
      :actionObj="scriptData"
      :moduleName="module"
      @onSave="actionSave"
      @onClose="showScriptDialog = false"
    ></ScriptDialog>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import { API } from '@facilio/api'
import FAssignment from '@/FAssignment'
import SendNotification from 'pages/setup/actions/SendNotification'
import FFormulaBuilder from '@/workflow/FFormulaBuilder'
import NotificationHelper from 'pages/setup/actions/NotificationHelper'
import FieldUpdateDialog from '@/fields/FieldUpdateDialog'
import ChangeStatusDialog from 'src/newapp/setupActions/components/StatusUpdate.vue'
import { getFieldOptions } from 'util/picklist'
import ScriptDialog from 'src/newapp/setupActions/components/ScriptEditor'
import pushNotification from 'src/newapp/setupActions/components/PushNotification'
import { isEmpty } from '@facilio/utils/validation'
import EmailAction from 'src/newapp/setupActions/components/EmailAction.vue'
import { EmailModel } from 'src/newapp/setupActions/models/EmailModel.js'
import { NotificationModel } from 'src/newapp/setupActions/models/NotificationModel.js'
import TeamStaffMixin from '@/mixins/TeamStaffMixin'

const notificationLists = ['whatsapp', 'phonecall']

export default {
  mixins: [NotificationHelper, TeamStaffMixin],
  created() {
    this.fetchMetaFields()
    this.$store.dispatch('loadTicketStatus', this.module)
    this.$store.dispatch('loadTicketCategory')
    this.$store.dispatch('loadTicketPriority')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadGroups')
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

    isWorkPermitLicenseEnabled() {
      let { $helpers } = this
      let { isLicenseEnabled } = $helpers || {}

      return isLicenseEnabled('WORK_PERMIT')
    },
    workPermitActionPhone() {
      let { workPermitAction } = this

      return workPermitAction('phonecall')
    },
    workPermitActionWhatsapp() {
      let { workPermitAction } = this

      return workPermitAction('whatsapp')
    },
  },
  data() {
    return {
      assets: null,
      categoryMetric: null,
      isEmailConfi: false,
      isPhoneCallConfi: false,
      isWhatsappConfi: false,
      isFieldConfi: false,
      isControlConfi: false,
      isScriptConfigured: false,
      isStatusConfigured: false,
      emailContext: {},
      showEmailActions: false,
      phoneCallContext: '',
      whatsappContext: '',
      smsContent: '',
      fieldContext: '',
      controlContext: '',
      mobileContent: {},
      showMobileConfig: false,
      woContext: '',
      isSmsConfi: false,
      isWOConfi: false,
      isMobileConfig: false,
      isWoCloseConfig: false,
      actionMode: 'mail',
      actionEdit: false,
      picklistOptions: {},
      //  idOldActionEdit: false,
      actionIndex: null,
      createworkorderDialog: false,
      fieldUpdateValue: false,
      openStatusDialog: false,
      controlsAction: false,
      actionData: null,
      fields: [],
      showScriptDialog: false,
      showDiff: false,
      controlData: {
        actionType: 18,
        templateJson: {},
      },
      closeWo: {
        actionType: 12,
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
      },

      fieldChange: {
        actionType: 13,
        templateJson: {
          fieldMatcher: [],
        },
      },

      changeStatus: {
        actionType: 19,
        templateJson: {
          new_state: null,
        },
      },

      scriptData: null,
    }
  },
  components: {
    pushNotification,
    SendNotification,
    FFormulaBuilder,
    FAssignment,
    FieldUpdateDialog,
    ChangeStatusDialog,
    ScriptDialog,
    EmailAction,
  },
  props: [
    'ruleType',
    'rule',
    'module',
    'moduleFields',
    'isUpdate',
    'actions',
    'moduleId',
  ],
  watch: {
    rule: function(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.initData(newVal)
      }
    },
  },
  mounted() {
    if (this.rule) {
      this.initData(this.rule)
    }
    if (this.rule.actions.length === 0) {
      this.addRow()
    }
  },
  methods: {
    statusFieldName(selectedField, index) {
      let field = this.moduleFields.filter(
        field => field.name === selectedField.field
      )
      this.fieldChange.templateJson.fieldMatcher[index].fieldObj = field
      this.fieldChange.templateJson.fieldMatcher[index].isSpacePicker = false
      if (field.length > 0) {
        this.fieldChange.templateJson.fieldMatcher[index].columnName =
          field[0].completeColumnName
        if (field[0].dataTypeEnum._name === 'ENUM') {
          this.$set(this.picklistOptions, field[0].name, field[0].enumMap)
        }
        if (field[0].dataTypeEnum._name === 'LOOKUP' && field[0].specialType) {
          this.loadSpecialTypePickList(field[0].specialType, field[0].name)
        } else if (
          field[0].dataTypeEnum._name === 'LOOKUP' &&
          field[0].lookupModule
        ) {
          if (field[0].lookupModule.name === 'ticketstatus') {
            this.$set(
              this.picklistOptions,
              field[0].name,
              this.getTicketStatusPickList(this.module)
            )
          } else if (field[0].lookupModule.name === 'ticketpriority') {
            // handling  key value pair to match existing flow   pattern
            let priority = {}
            this.ticketpriority.forEach(d => {
              priority[d.id] = d.displayName
            })
            this.$set(this.picklistOptions, field[0].name, priority)
          } else {
            this.loadPickList(field[0].lookupModule.name, field[0].name)
          }
        }
        this.$forceUpdate()
      }
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
    updateAction(actions) {
      // Removing web notifications for now
      let newlist = []
      actions
        .filter(action => action.actionType !== 5)
        .map((action, i) => {
          let template

          if (action.actionType === 3) {
            if (isEmpty(this.emailContext.templateJson)) {
              let emailObj = new EmailModel({
                ...(this.emailContext || {}),
                moduleName: this.module,
              })
              this.emailContext = emailObj.serialize()
            }
            template = this.emailContext
          } else if (action.actionType === 4) {
            template = this.$refs.notificationpage.getDataToSave(
              this.smsContent,
              'sms'
            )
          } else if (action.actionType === 7) {
            if (isEmpty(this.mobileContent.templateJson)) {
              let pushObj = new NotificationModel({
                ...(this.mobileContent || {}),
                moduleName: this.module,
              })
              this.mobileContent = pushObj.serialize()
            }
            template = this.mobileContent
          } else if (action.actionType === 13) {
            template = this.fieldChange
          } else if (action.actionType === 19) {
            template = this.changeStatus
          } else if (action.actionType === 18) {
            template = {}
            template.actionType = this.controlData.actionType
            template.templateJson = this.controlData.templateJson
          } else if (action.actionType === 21) {
            template = {
              actionType: this.scriptData.actionType,
              templateJson: this.scriptData.templateJson,
            }
          } else if (action.actionType === 11) {
            template = this.$helpers.cloneObject(this.alarmWo)
            template.templateJson.category =
              template.templateJson.category.id > 0
                ? template.templateJson.category
                : { id: -1 }
            template.templateJson.priority =
              template.templateJson.priority.id > 0
                ? template.templateJson.priority
                : { id: -1 }
            template.templateJson.assignmentGroup =
              template.templateJson.assignmentGroup.id > 0
                ? template.templateJson.assignmentGroup
                : { id: -1 }
            template.templateJson.assignedTo =
              template.templateJson.assignedTo.id > 0
                ? template.templateJson.assignedTo
                : { id: -1 }
          } else if (action.actionType === 26) {
            template = this.$refs.notificationpage.getDataToSave(
              this.whatsappContext,
              'whatsapp'
            )
          } else if (action.actionType === 27) {
            template = this.$refs.notificationpage.getDataToSave(
              this.phoneCallContext,
              'phonecall'
            )
          }
          this.addModuleParam(template)
          newlist.push(template || action)
        })
      this.$emit('update:actions', newlist)
      return newlist
    },
    loadSpecialTypePickList(specialType, fieldName) {
      let self = this
      let pickOption = {}
      if (specialType === 'users') {
        let userList = self.$store.state.users
        pickOption['$' + '{LOGGED_USER}'] = 'Current User'
        for (let user of userList) {
          pickOption[user.id] = user.name
        }
        self.$set(self.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'groups') {
        let groupList = self.$store.state.groups
        for (let group of groupList) {
          pickOption[group.groupId] = group.name
        }
        self.$set(self.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'basespace') {
        let spaceList = self.$store.state.spaces
        for (let space of spaceList) {
          pickOption[space.id] = space.name
        }
        self.$set(self.picklistOptions, fieldName, pickOption)
      } else if (specialType === 'alarmType') {
        self.$set(
          self.picklistOptions,
          fieldName,
          this.$constants.AlarmCategory
        )
      } else if (specialType === 'sourceType') {
        self.$set(self.picklistOptions, fieldName, this.$constants.SourceType)
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
    initData(rule) {
      rule.actions.forEach(d => {
        if (parseInt(d.actionType) === 3) {
          this.isEmailConfi = true
          this.emailContext = d
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
          if (d.template !== null) {
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
          } //  this.actionMode = 'workorder'
          this.woContext = d
        } else if (parseInt(d.actionType) === 12) {
          this.isWoCloseConfig = true
        } else if (parseInt(d.actionType) === 18) {
          this.isControlConfi = true
          this.loadCategoryMetric(d.template.assetCategory)
          d.templateJson = d.template.originalTemplate
          d.templateJson.assetCategory = d.template.assetCategory
          d.templateJson.workflow = d.template.workflow
          // d.templateJson.metric = parseInt(d.template.metric)
          this.controlData.templateJson = d.templateJson
        } else if (parseInt(d.actionType) === 21) {
          this.isScriptConfigured = true
          let templateJson = {
            resultWorkflowContext: d.template.originalTemplate.workflowContext,
          }
          this.scriptData = { ...d, templateJson }
        } else if (parseInt(d.actionType) === 13) {
          this.isFieldConfi = true
          let i = 0
          if (d.template !== null) {
            for (let key in d.template.originalTemplate) {
              this.addRow()
              if (d.template.originalTemplate.hasOwnProperty(key)) {
                this.fieldChange.templateJson.fieldMatcher[i].field = key
                this.fieldChange.templateJson.fieldMatcher[i].value =
                  d.template.originalTemplate[key]
                i++
                if (self.moduleFields) {
                  let field = self.moduleFields.filter(
                    field => field.name === key
                  )
                  this.fieldChange.templateJson.fieldMatcher[i].fieldObj = field
                  if (field[0].dataTypeEnum._name === 'LOOKUP') {
                    this.fieldChange.templateJson.fieldMatcher[i].value =
                      d.template.originalTemplate[key]
                    this.fieldChange.templateJson.fieldMatcher[i].valueArray =
                      d.template.originalTemplate[key]
                  } else if (
                    field[0].dataTypeEnum._name === 'DATE_TIME' ||
                    field[0].dataTypeEnum._name === 'DATE'
                  ) {
                    if (d.template.originalTemplate[key]) {
                      this.fieldChange.templateJson.fieldMatcher[
                        i
                      ].dateObject = this.$helpers.secTodaysHoursMinu(
                        this.rule.interval / 1000
                      )
                    }
                  }
                  this.statusFieldName(
                    this.fieldChange.templateJson.fieldMatcher[i],
                    i
                  )
                }
              }
            }
            this.fieldContext = d
          }
        } else if (parseInt(d.actionType) === 26) {
          this.isWhatsappConfi = true
          this.whatsappContext = d
        } else if (parseInt(d.actionType) === 27) {
          this.isPhoneCallConfi = true
          this.phoneCallContext = d
        } else if (parseInt(d.actionType) === 19) {
          this.isStatusConfigured = true
          let new_state = this.$getProperty(
            d,
            'template.originalTemplate.new_state'
          )
          let { changeStatus } = this
          let templateJson = { new_state }
          this.changeStatus = { ...changeStatus, templateJson }
        }
      })
    },
    addRow() {
      this.fieldChange.templateJson.fieldMatcher.push({
        field: '',
        isSpacePicker: false,
        value: null,
        parseLabel: null,
        valueArray: null,
        dateObject: {},
        fieldObj: null,
      })
    },
    closeNotifiDialog() {
      this.showMobileConfig = false
    },
    actionSave(datas) {
      let data = this.$helpers.cloneObject(datas)
      let idx = (this.rule.actions || []).findIndex(
        d => d.actionType === data.actionType
      )
      if (idx !== -1) {
        this.rule.actions.splice(idx, 1)
      }
      if (data.actionType === 3) {
        this.closeEmailActions()
        this.isEmailConfi = true
        this.emailContext = data
      } else if (data.actionType === 4) {
        this.isSmsConfi = true
        this.smsContent = data
      } else if (data.actionType === 7) {
        this.isMobileConfig = true
        this.closeNotifiDialog()
        this.mobileContent = data
      } else if (data.actionType === 11) {
        this.createworkorderDialog = false
        this.isWOConfi = true
        this.woContext = data
        let template = this.alarmWo.templateJson
        Object.keys(data).forEach(
          key =>
            ['actionType', 'templateJson'].includes(key) || delete data[key]
        )
        if (template) {
          data.template = null
          data.templateJson.category =
            template.category.id > 0 ? template.category : { id: -1 }
          data.templateJson.priority =
            template.priority.id > 0 ? template.priority : { id: -1 }
          data.templateJson.assignmentGroup =
            template.assignmentGroup.id > 0
              ? template.assignmentGroup
              : { id: -1 }
          data.templateJson.assignedTo =
            template.assignedTo.id > 0 ? template.assignedTo : { id: -1 }
        }
      } else if (data.actionType === 12) {
        this.isWoCloseConfig = true
        // this.woContext = data
      } else if (data.actionType === 13) {
        this.fieldUpdateValue = false
        this.fieldContext = data
        this.isFieldConfi = true
      } else if (data.actionType === 19) {
        this.openStatusDialog = false
        this.changeStatus = data
        this.isStatusConfigured = true
      } else if (data.actionType === 18) {
        this.controlsAction = false
        data.templateJson.val = '${resultExpr}'
        if (
          data.templateJson.resource === '${workorder.resource.id}' &&
          data.templateJson.workflow.expressions
        ) {
          let expName = 'workorder.resource.id'
          data.templateJson.workflow.expressions.push({
            name: expName,
            constant: '${' + expName + '}',
          })
          data.templateJson.workflow.parameters.push({
            name: expName,
            typeString: 'String',
          })
        }
        this.controlContext = data
        this.isControlConfi = true
      } else if (data.actionType === 21) {
        this.showScriptDialog = false
        this.isScriptConfigured = true
        this.scriptData = data || {}
      } else if (data.actionType === 26) {
        this.isWhatsappConfi = true
        this.whatsappContext = data
      } else if (data.actionType === 27) {
        this.isPhoneCallConfi = true
        this.phoneCallContext = data
      }
      this.addModuleParam(data)
      this.rule.actions.push(data)
      this.actionEdit = false
      this.$emit('actions', this.rule.actions)
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
      this.rule.actions.splice(idx, 1)
    },
    closeEmailActions() {
      this.showEmailActions = false
    },

    editAction(action, idx) {
      if (action.actionType === 3) {
        this.showEmailActions = true
        this.actionData = action
        this.isEmailConfi = true
      } else if (action.actionType === 4) {
        this.actionData = action
        this.actionMode = 'sms'
        this.actionEdit = true
        this.isSmsConfi = true
      } else if (action.actionType === 7) {
        this.mobileContent = action
        this.isMobileConfig = true
        this.showMobileConfig = true
      } else if (action.actionType === 11) {
        this.createworkorderDialog = true
        this.actionMode = 'workorder'
        this.isWOConfi = true
      } else if (action.actionType === 13) {
        this.isFieldConfi = true
        this.fieldUpdateValue = true
        this.actionMode = 'fieldChange'
        let self = this
        let i = 0
        for (let key in action.template.originalTemplate) {
          if (action.template.originalTemplate.hasOwnProperty(key)) {
            this.fieldChange.templateJson.fieldMatcher[i].field = key
            this.fieldChange.templateJson.fieldMatcher[i].value =
              action.template.originalTemplate[key]
            let field = self.moduleFields.filter(field => field.name === key)
            if (field[0].dataTypeEnum._name === 'LOOKUP') {
              this.fieldChange.templateJson.fieldMatcher[i].valueArray =
                action.template.originalTemplate[key].id
              this.fieldChange.templateJson.fieldMatcher[i].value.id =
                action.template.originalTemplate[key].id
            } else if (
              field[0].dataTypeEnum._name === 'DATE_TIME' ||
              field[0].dataTypeEnum._name === 'DATE'
            ) {
              this.fieldChange.templateJson.fieldMatcher[
                i
              ].dateObject = this.$helpers.secTodaysHoursMinu(
                action.template.originalTemplate[key] / 1000
              )
            }
            this.fieldChange.templateJson.fieldMatcher[i].fieldObj = field
            this.statusFieldName(
              this.fieldChange.templateJson.fieldMatcher[i],
              i
            )
            if (
              field[0].lookupModule &&
              field[0].lookupModule.name === 'resource'
            ) {
              this.fieldChange.templateJson.fieldMatcher[i].isSpacePicker = true
            }
            i++
          }
        }
        this.$forceUpdate()
      } else if (action.actionType === 18) {
        this.actionMode = 'controls'
        this.controlsAction = true
        this.isControlConfi = true
      } else if (action.actionType === 21) {
        this.actionMode = 'script'
        this.showScriptDialog = true
        this.isScriptConfigured = true
      } else if (action.actionType === 19) {
        this.actionMode = 'changeStatus'
        this.openStatusDialog = true
        this.isStatusConfigured = true
      } else if (action.actionType === 26) {
        this.actionData = action
        this.actionMode = 'whatsapp'
        this.actionEdit = true
        this.isWhatsappConfi = true
      } else if (action.actionType === 27) {
        this.actionData = action
        this.actionMode = 'phonecall'
        this.actionEdit = true
        this.isPhoneCallConfi = true
      }
    },
    addAction(cmd) {
      if (cmd === 'mobile') {
        this.showMobileConfig = true
        return
      }
      if (cmd === 'mail') {
        this.showEmailActions = true
        return
      }
      this.actionMode = cmd
      this.actionData = null

      if (cmd === 'script') {
        this.showScriptDialog = true
      } else if (
        cmd !== 'workorder' &&
        cmd !== 'fieldChange' &&
        cmd !== 'changeStatus' &&
        cmd !== 'controls' &&
        cmd !== 'script'
      ) {
        this.actionEdit = true
        //  this.idOldActionEdit = false
      } else if (
        cmd === 'fieldChange' &&
        this.fieldChange.templateJson.fieldMatcher.length <= 0
      ) {
        this.addRow()
      } else if (cmd === 'changeStatus') {
        this.openStatusDialog = true
      }
    },
    reset(action, idx) {
      if (action === 'sms') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 4)
        this.rule.actions.splice(idx, 1)
        this.smsContent = ''
        this.isSmsConfi = false
      } else if (action === 'mail') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 3)
        this.rule.actions.splice(idx, 1)
        this.emailContext = {}
        this.isEmailConfi = false
      } else if (action === 'mobile') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 7)
        this.rule.actions.splice(idx, 1)
        this.mobileContent = {}
        this.isMobileConfig = false
      }
      if (action === 'whatsapp') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 26)
        this.rule.actions.splice(idx, 1)
        this.whatsappContext = ''
        this.isWhatsappConfi = false
      }
      if (action === 'phonecall') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 27)
        this.rule.actions.splice(idx, 1)
        this.phoneCallContext = ''
        this.isPhoneCallConfi = false
      } else if (action === 'wo') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 11)
        this.rule.actions.splice(idx, 1)
        this.woContext = null
        this.isWOConfi = false
      } else if (action === 'woClose') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 12)
        this.rule.actions.splice(idx, 1)
        this.isWoCloseConfig = false
      } else if (action === 'fieldChange') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 13)
        this.rule.actions.splice(idx, 1)
        this.isFieldConfi = false
      } else if (action === 'controls') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 18)
        this.rule.actions.splice(idx, 1)
        this.isControlConfi = false
        this.controlData.templateJson = {}
      } else if (action === 'script') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 21)
        this.rule.actions.splice(idx, 1)
        this.isScriptConfigured = false
        this.scriptData = null
      } else if (action === 'changeStatus') {
        let idx = this.rule.actions.findIndex(d => d.actionType === 19)
        this.rule.actions.splice(idx, 1)
        this.isStatusConfigured = false
        this.changeStatus = null
      }
      this.$forceUpdate()
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
    closeDialog() {
      this.openStatusDialog = false
    },
    workPermitAction(actions) {
      let { isWorkPermitLicenseEnabled } = this
      if (notificationLists.includes(actions)) {
        return isWorkPermitLicenseEnabled
      }
    },
  },
}
</script>

<style scoped>
.dialog-switch {
  position: absolute;
  top: 20px;
  right: 60px;
  font-weight: bolder;
}
</style>
