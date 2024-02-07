<template>
  <div>
    <el-row>
      <el-col :span="12">
        <el-form-item prop="name">
          <p class="fc-input-label-txt txt-color">
            Who can perform this transition?
          </p>
          <el-select
            v-model="transitionObj.type"
            @change="onTypeChange()"
            class="fc-input-full-border-select2 width100"
          >
            <el-option
              v-for="executionType in executionTypes"
              :key="executionType.key"
              :label="executionType.label"
              :value="executionType.key"
              :default-first-option="true"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <div v-if="transitionObj.type === $constants.TRANSITION_TYPE.user">
      <p class="fc-input-label-txt txt-color">
        Specify users, roles or groups who can perform this transition
      </p>
      <div class="d-flex">
        <div
          :class="
            isConfiguredUser ? 'configured-green' : 'configure-blue pointer'
          "
          class="new-statetransition-config"
          @click="setEditObj(), (canShowPopUp = true)"
        >
          {{ isConfiguredUser ? 'Configured' : 'Configure' }}
        </div>
        <div v-if="isConfiguredUser">
          <i
            class="el-icon-edit pointer edit-icon pL30 txt-color"
            @click="setEditObj(), (canShowPopUp = true)"
            title="Edit User"
            v-tippy
          ></i>
        </div>
      </div>
      <state-transition-approvers
        v-if="canShowPopUp"
        :moduleFields="moduleFields"
        :transitionObj="transitionObj"
        @onClose="close"
        @onSave="saveUser"
      ></state-transition-approvers>

      <div class="d-flex flex-direction-column mT25">
        <el-checkbox
          v-model="transitionObj.showInOccupantPortal"
          class="fc-input-label-txt txt-color portal-checkbox"
          @change="save()"
        >
          Show in Occupant Portal
        </el-checkbox>
        <el-checkbox
          v-model="transitionObj.showInVendorPortal"
          class="fc-input-label-txt txt-color portal-checkbox"
          @change="save()"
        >
          Show in Vendor Portal
        </el-checkbox>
        <el-checkbox
          v-if="this.$helpers.isLicenseEnabled('TENANTS')"
          v-model="transitionObj.showInTenantPortal"
          class="fc-input-label-txt txt-color portal-checkbox"
          @change="save()"
        >
          Show in Tenant Portal
        </el-checkbox>
        <el-checkbox
          v-if="this.$helpers.isLicenseEnabled('CLIENT_PORTAL')"
          v-model="transitionObj.showInClientPortal"
          class="fc-input-label-txt txt-color portal-checkbox"
          @change="save()"
        >
          Show in Client Portal
        </el-checkbox>
        <el-checkbox
          v-model="transitionObj.shouldExecuteFromPermalink"
          class="fc-input-label-txt txt-color portal-checkbox"
          @change="save()"
        >
          Allow Permalinks
        </el-checkbox>
      </div>

      <hr class="separator-line mR40 mT20" />
    </div>

    <template v-if="!isAutomatedByTime">
      <p class="fc-input-label-txt txt-color">
        Specify conditions for this transition
      </p>
      <div class="d-flex">
        <div
          :class="
            isConfiguredCriteria ? 'configured-green' : 'configure-blue pointer'
          "
          class="new-statetransition-config"
          @click="setEditObj(), (canShowCriteria = true)"
        >
          {{ isConfiguredCriteria ? 'Configured' : 'Configure Criteria' }}
        </div>
        <div v-if="isConfiguredCriteria" class="d-flex">
          <i
            class="el-icon-edit pointer edit-icon pL30 txt-color"
            @click="setEditObj(), (canShowCriteria = true)"
            title="Edit Criteria"
            v-tippy
          ></i>
          <span class="reset-txt pointer mL20" @click="resetCriteria()">
            Reset
          </span>
        </div>
      </div>
      <state-transition-criteria
        v-if="canShowCriteria"
        :module="module"
        :activeCriteria="activeCriteria"
        @save="
          criteria => {
            ;(activeCriteria = criteria), saveCriteria()
          }
        "
        @close="close()"
      ></state-transition-criteria>
      <hr class="separator-line mR40 mT20" />
    </template>

    <div v-else>
      <p class="fc-input-label-txt">
        Specify the time after which the transition is performed
      </p>
      <div class="d-flex">
        <div
          :class="
            isConfiguredTime ? 'configured-green' : 'configure-blue pointer'
          "
          class="new-statetransition-config"
          @click="setEditObj(), (canShowPopUp = true)"
        >
          {{ isConfiguredTime ? 'Configured' : 'Configure Time' }}
        </div>
        <div v-if="isConfiguredTime">
          <i
            class="el-icon-edit pointer edit-icon pL30 txt-color"
            @click="setEditObj(), (canShowPopUp = true)"
            title="Edit"
            v-tippy
          ></i>
        </div>
      </div>

      <state-transition-time
        v-if="canShowPopUp"
        :timeObj="time"
        :dataObj="dateField"
        :moduleFields="moduleFields"
        @save="onTimeChange"
        @close="close()"
      />

      <hr class="separator-line mR40 mT20" />
    </div>

    <template v-if="transitionObj.type === $constants.TRANSITION_TYPE.user">
      <GeoFencing
        v-if="isGeoFenchingCheck"
        :module="module"
        :transition="transitionObj"
        :setProps="setProps"
        @onSave="save"
      />
      <StateTransitionQR
        :module="module"
        :transition="transitionObj"
        :setProps="setProps"
        @onSave="save()"
      />
      <div>
        <p class="fc-input-label-txt txt-color">
          Update fields while performing this transition
        </p>
        <div class="d-flex items-baseline">
          <template v-if="isFieldConfigured">
            <div
              class="configured-green new-statetransition-config d-flex items-center"
            >
              Configured
            </div>
            <a @click="editForm()" class="cursor-pointer reset-txt mL30">
              <inline-svg src="svgs/edit" iconClass="icon icon-xxs" />
              Customize
            </a>
            <a @click="removeForm()" class="mL20 cursor-pointer reset-txt">
              Reset
            </a>
          </template>
          <template v-else>
            <div
              class="configure-blue pointer new-statetransition-config d-flex items-center"
              @click="openPopup()"
            >
              Configure
              <Spinner :show="isSavingForm" color="#6171db" size="25"></Spinner>
            </div>
          </template>
        </div>
      </div>
      <FieldPickerDialog
        v-if="canShowFieldPopup"
        :availableFields="formFields"
        :selectedList="selectedFieldsList"
        :isInFormConfig="true"
        itemKey="fieldId"
        @save="saveFields"
        @close="closePopup"
      ></FieldPickerDialog>
      <FormsEdit
        v-if="canShowFormBuilder"
        :moduleName="module"
        :id="activeFormId"
        :onSave="closeForm"
        :showDeleteForField="({ name }) => true"
        :isUpdateForm="true"
        class="formbuilder-container"
      />

      <div>
        <hr class="separator-line mR40 mT20" />
        <p class="fc-input-label-txt txt-color">
          Create a record while performing this transition
        </p>
        <div class="d-flex">
          <div
            :class="
              isFormConfigured ? 'configured-green' : 'configure-blue pointer'
            "
            class="new-statetransition-config"
            @click="canShowModuleFormPicker = true"
          >
            {{ isFormConfigured ? 'Configured' : 'Configure' }}
          </div>
          <div v-if="isFormConfigured">
            <i
              class="el-icon-edit pointer edit-icon pL30 txt-color"
              @click="canShowModuleFormPicker = true"
            ></i>
            <span class="reset-txt pointer mL20" @click="removeForm()">
              Reset
            </span>
          </div>
        </div>
      </div>
      <ModuleFormPicker
        v-if="canShowModuleFormPicker"
        :transitionObj.sync="transitionObj"
        :module="module"
        @save="save()"
        @close="close"
      />
      <hr class="separator-line mR40 mT20" />
    </template>
  </div>
</template>

<script>
import { getApp } from '@facilio/router'
import { API } from '@facilio/api'
import { v4 as uuid } from 'uuid'
import StateTransitionApprovers from './popups/StateTransitionApprovers'
import StateTransitionCriteria from './popups/StateTransitionCriteria'
import StateTransitionTime from './popups/StateTransitionTime'
import { isEmpty } from '@facilio/utils/validation'
import ModuleFormPicker from './popups/ModuleFormPicker'
import FormsEdit from 'pages/setup/modules/FormsEdit'
import Spinner from '@/Spinner'
import FieldPickerDialog from 'newapp/components/FieldPicker'
import StateTransitionQR from './StateTransitionQR.vue'
import GeoFencing from './GeoFencing.vue'

const DIALOG_TYPE = {
  FIELD: 1,
  FORM: 2,
}
const GEOFENCING_LICENSE = [
  'workorder',
  'serviceRequest',
  'inspectionResponse',
  'asset',
  'facilitybooking',
]

export default {
  props: [
    'transitionObj',
    'module',
    'timeObj',
    'dateObj',
    'moduleFields',
    'save',
    'setProps',
  ],

  components: {
    StateTransitionApprovers,
    ModuleFormPicker,
    StateTransitionCriteria,
    StateTransitionTime,
    FormsEdit,
    Spinner,
    FieldPickerDialog,
    StateTransitionQR,
    GeoFencing,
  },

  data() {
    return {
      activeCriteria: null,
      activeFormId: null,
      time: {
        days: null,
        minute: 0,
        hours: 0,
      },
      dateField: {
        scheduleType: 3,
        dateFieldId: null,
        time: null,
      },
      canShowPopUp: false,
      canShowCriteria: false,
      canShowFormBuilder: false,
      canShowModuleFormPicker: false,
      isSavingForm: false,
      error: false,
      errorMessage: '',
      formFields: [],
      selectedFieldsList: [],
      canShowFieldPopup: false,
    }
  },
  created() {
    this.fetchFormFields()
  },
  computed: {
    isConfiguredUser() {
      let {
        transitionObj: { approvers },
      } = this
      let approver = this.$getProperty(approvers, '0', null)

      return !isEmpty(approver) && !isEmpty(approver.type)
    },
    isConfiguredTime() {
      let { days, hours, minute } = this.timeObj
      let { dateFieldId } = this.dateObj
      let timeConfig = !isEmpty(days) || minute !== 0 || hours !== 0

      return !isEmpty(dateFieldId) || timeConfig
    },

    isConfiguredCriteria() {
      return !isEmpty(this.transitionObj.criteria)
    },

    isFieldConfigured() {
      let { dialogType, formId } = this.transitionObj
      return dialogType === DIALOG_TYPE.FIELD && !isEmpty(formId)
    },

    isFormConfigured() {
      let { dialogType, formId, formModuleName } = this.transitionObj
      return (
        dialogType === DIALOG_TYPE.FORM &&
        !isEmpty(formId) &&
        !isEmpty(formModuleName)
      )
    },

    executionTypes() {
      let {
        TRANSITION_TYPE: { datefield, time },
      } = this.$constants
      let { type } = this.transitionObj
      let optionType = type === datefield ? datefield : time

      return [
        {
          key: 1,
          label: 'User',
        },
        {
          key: optionType,
          label: 'Automated by Time',
        },
        {
          key: 3,
          label: 'Automated by Condition',
        },
      ]
    },
    isAutomatedByTime() {
      let {
        TRANSITION_TYPE: { time, datefield },
      } = this.$constants
      let { type } = this.transitionObj
      return [time, datefield].includes(type)
    },
    isGeoFenchingCheck() {
      return GEOFENCING_LICENSE.includes(this.module)
    },
  },

  methods: {
    setEditObj() {
      this.activeCriteria = this.$helpers.cloneObject(
        this.transitionObj.criteria
      )
      this.time = this.$helpers.cloneObject(this.timeObj)
      this.dateField = {
        ...this.$helpers.cloneObject(this.dateObj),
        type: this.transitionObj.type,
      }
    },
    close() {
      this.canShowPopUp = false
      this.canShowCriteria = false
      this.activeCriteria = null
      this.canShowModuleFormPicker = false
      this.canShowFormBuilder = false
      this.time = {
        days: null,
        minute: 0,
        hours: 0,
      }
      this.dateField = {
        scheduleType: 3,
        dateFieldId: null,
        time: null,
      }
      this.error = false
      this.errorMessage = ''
    },

    async onTimeChange(dateObj) {
      let { type, date, scheduleType, dateFieldId, time } = dateObj

      this.$set(this.transitionObj, 'type', type)
      this.time = date
      this.dateField = { scheduleType, dateFieldId, time }

      let { TRANSITION_TYPE } = this.$constants
      let { time: TIME, datefield: DATE } = TRANSITION_TYPE

      if ([TIME, DATE].includes(this.transitionObj.type)) {
        if (this.transitionObj.type === DATE) {
          this.$emit('update:dateObj', this.dateField)
        }
        this.$emit('update:timeObj', this.time)
      }

      this.close()
      await this.save()
    },

    async onTypeChange() {
      if (this.transitionObj.type === 1) {
        await this.save()
      }
    },

    async saveUser() {
      this.close()
      await this.save()
    },

    async resetCriteria() {
      this.setProps({
        criteria: null,
        criteriaId: -99,
      })
      this.activeCriteria = null

      await this.save()
    },

    async saveCriteria() {
      if (this.canShowCriteria && !isEmpty(this.activeCriteria)) {
        this.setProps({ criteria: this.activeCriteria })
      }

      await this.save()
      this.close()
    },

    async createForm() {
      const { id: transitionId } = this.transitionObj
      const { module: moduleName, selectedFieldsList } = this
      const { linkName: appLinkName, id: appId } = getApp()
      const url = 'v2/forms/add'

      const params = {
        appLinkName,
        moduleName,
        form: {
          name: `transition_form_${transitionId}_${uuid()}`,
          displayName: 'Enter details',
          appLinkName,
          appId,
          labelPosition: 1,
          showInMobile: true,
          hideInList: true,
          siteIds: [],
          sections: [{ fields: selectedFieldsList }],
          stateFlowId: -99,
        },
      }

      this.isSavingForm = true

      const { error, data } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const {
          form: { id },
        } = data

        this.setProps({
          dialogType: DIALOG_TYPE.FIELD,
          formId: id,
        })

        await this.save()
        this.editForm(id)
      }
      this.isSavingForm = false
    },
    editForm(id) {
      let { formId } = this.transitionObj
      this.activeFormId = id || formId
      this.canShowFormBuilder = true
    },
    closeForm() {
      this.activeFormId = null
      this.canShowFormBuilder = false
    },
    async removeForm() {
      this.setProps({
        dialogType: -99,
        formId: null,
        formModuleName: '',
      })
      await this.save()
    },
    fetchFormFields() {
      return API.get(`/v2/forms/fields`, {
        moduleName: this.module,
      }).then(({ data, error }) => {
        if (!error) {
          let fields = this.$getProperty(data, 'fields') || []
          this.formFields =
            fields.filter(field => field.displayTypeEnum !== 'TASKS') || []
        }
      })
    },
    openPopup() {
      this.canShowFieldPopup = true
    },
    saveFields(fields) {
      this.selectedFieldsList = fields
      if (isEmpty(fields)) {
        this.$message.error(this.$t('common.header.please_select_one_field'))
      } else {
        this.canShowFieldPopup = false
        this.$nextTick(() => {
          this.activePopupType = null
          this.createForm()
        })
      }
    },
    closePopup() {
      this.canShowFieldPopup = false
      this.activePopupType = null
    },
  },
}
</script>
<style scoped>
.reset-txt {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #6171db;
  fill: #6171db;
  vertical-align: middle;
}
.formbuilder-container {
  top: 50px;
  left: 0;
  right: 0;
  bottom: 0;
}
</style>
