<template>
  <div>
    <div id="customization-header" class="section-header">
      {{ $t('common._common.customization') }}
    </div>
    <div class="p50 pT20 pR70 pB30 el-form">
      <div class="mB20 pB20 border-bottom3">
        <p class="fc-input-label-txt txt-color">
          {{ $t('common._common.choose_fields_show_overview') }}
        </p>
        <div class="d-flex">
          <div
            :class="
              isSummaryFieldConfigured
                ? 'configured-green'
                : 'configure-blue pointer'
            "
            class="new-statetransition-config"
            @click="canShowSummaryFields = true"
          >
            {{
              isSummaryFieldConfigured
                ? $t('common._common.configured')
                : $t('common._common.configure')
            }}
          </div>
          <div v-if="isSummaryFieldConfigured">
            <i
              class="el-icon-edit pointer edit-icon pL30 txt-color"
              @click="canShowSummaryFields = true"
              :title="$t('common.header.edit_fields')"
              v-tippy
            ></i>
          </div>
        </div>
      </div>
      <div class="mB20 pB20 border-bottom3">
        <p class="fc-input-label-txt txt-color">
          {{ $t('common._common.choose_related_modules_show') }}
        </p>
        <div class="d-flex">
          <div
            :class="
              isRelatedModulesConfigured
                ? 'configured-green'
                : 'configure-blue pointer'
            "
            class="new-statetransition-config"
            @click="canShowRelatedModules = true"
          >
            {{
              isRelatedModulesConfigured
                ? $t('common._common.configured')
                : $t('common._common.configure')
            }}
          </div>
          <div v-if="isRelatedModulesConfigured">
            <i
              class="el-icon-edit pointer edit-icon pL30 txt-color"
              @click="canShowRelatedModules = true"
              :title="$t('common.header.edit_fields')"
              v-tippy
            ></i>
          </div>
        </div>
      </div>
      <div class="mB20 pB20 border-bottom3">
        <p class="fc-input-label-txt txt-color">
          {{ $t('common._common.choose_fields_to_update_while_approving') }}
        </p>
        <div class="d-flex">
          <template v-if="isApprovalFormConfigured">
            <div class="configured-green new-statetransition-config">
              {{ $t('common._common.configured') }}
            </div>
            <div>
              <i
                class="el-icon-edit pointer edit-icon pL30 txt-color"
                @click="editForm('approval')"
                :title="$t('common.header.edit_fields')"
                v-tippy
              ></i>
            </div>
            <div>
              <a
                @click="removeForm('approval')"
                class="mL20 cursor-pointer reset-txt"
              >
                {{ $t('common._common.reset') }}
              </a>
            </div>
          </template>
          <template v-else>
            <div
              class="configure-blue pointer new-statetransition-config"
              @click="openPopup(popupTypes.APPROVE)"
            >
              {{ $t('common._common.configure') }}
            </div>
          </template>
        </div>
      </div>
      <div class="mB20 pB20">
        <p class="fc-input-label-txt txt-color">
          {{ $t('common._common.choose_fields_to_update_while_rejecting') }}
        </p>
        <div class="d-flex">
          <template v-if="isRejectionFormConfigured">
            <div class="configured-green new-statetransition-config">
              {{ $t('common._common.configured') }}
            </div>
            <div>
              <i
                class="el-icon-edit pointer edit-icon pL30 txt-color"
                @click="editForm('reject')"
                :title="$t('common.header.edit_fields')"
                v-tippy
              ></i>
            </div>
            <div>
              <a
                @click="removeForm('reject')"
                class="mL20 cursor-pointer reset-txt"
              >
                {{ $t('common._common.reset') }}
              </a>
            </div>
          </template>
          <template v-else>
            <div
              class="configure-blue pointer new-statetransition-config"
              @click="openPopup(popupTypes.REJECT)"
            >
              {{ $t('common._common.configure') }}
            </div>
          </template>
        </div>
      </div>
      <div class="mB20 pB20">
        <p class="fc-input-label-txt txt-color">
          {{ $t('common._common.choose_field_to_update_while_resending') }}
        </p>
        <div class="d-flex">
          <template v-if="isResendFormConfigured">
            <div class="configured-green new-statetransition-config">
              {{ $t('common._common.configured') }}
            </div>
            <div>
              <i
                class="el-icon-edit pointer edit-icon pL30 txt-color"
                @click="editForm('resend')"
                :title="$t('common.header.edit_fields')"
                v-tippy
              ></i>
            </div>
            <div>
              <a
                @click="removeForm('resend')"
                class="mL20 cursor-pointer reset-txt"
              >
                {{ $t('common._common.reset') }}
              </a>
            </div>
          </template>
          <template v-else>
            <div
              class="configure-blue pointer new-statetransition-config"
              @click="openPopup(popupTypes.RESEND)"
            >
              {{ $t('common._common.configure') }}
            </div>
          </template>
        </div>
      </div>
    </div>

    <FieldPickerDialog
      v-if="canShowFieldPopup"
      :availableFields="availableFields"
      :selectedList="selectedFieldsList"
      :isInFormConfig="activePopupType !== popupTypes.SUMMARY"
      itemKey="fieldId"
      @save="saveFields"
      @close="closePopup"
    ></FieldPickerDialog>

    <FieldPickerDialog
      v-if="canShowSummaryFields"
      :title="$t('common._common.configure_overview_fields')"
      :availableFields="availableFields"
      :selectedList="summaryFields"
      :isInFormConfig="false"
      itemKey="fieldId"
      @save="list => (summaryFields = list)"
      @close="canShowSummaryFields = false"
    ></FieldPickerDialog>

    <ItemSelectionDialog
      v-if="canShowRelatedModules"
      v-model="selectedModules"
      :availableItems="relatedModules"
      itemKey="moduleId"
      :title="$t('common._common.configure_related_modules')"
      @close="canShowRelatedModules = false"
    >
    </ItemSelectionDialog>
    <FormsEdit
      v-if="canShowFormBuilder"
      :moduleName="moduleName"
      :id="activeFormId"
      :onSave="closeForm"
      :showDeleteForField="({ name }) => true"
      :isUpdateForm="true"
      class="formbuilder-container"
    />
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import ItemSelectionDialog from './ItemSelectionDialog'
import cloneDeep from 'lodash/cloneDeep'
import FieldPickerDialog from 'newapp/components/FieldPicker'
import { getApp } from '@facilio/router'
import FormsEdit from 'pages/setup/modules/FormsEdit'
import { API } from '@facilio/api'
import { v4 as uuid } from 'uuid'

const popupTypes = {
  APPROVE: 1,
  REJECT: 2,
  SUMMARY: 3,
  RESEND: 4,
}

const moduleTypes = {
  BASE_ENTITY: 1,
  SUB_ENTITY: 18,
}

export default {
  name: 'Customization',
  props: {
    policy: {
      type: Object,
    },
    moduleFields: {
      type: Array,
    },
    formFields: {
      type: Array,
    },
  },
  components: {
    ItemSelectionDialog,
    FieldPickerDialog,
    FormsEdit,
  },
  data() {
    return {
      approvalFields: [],
      rejectFields: [],
      resendFields: [],
      summaryFields: [],
      selectedModules: [],
      canShowFieldPopup: false,
      canShowRelatedModules: false,
      canShowSummaryFields: false,
      canShowFormBuilder: false,
      activePopupType: null,
      relatedModules: [],
      popupTypes,
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.$route.params.id)
    },
    moduleName() {
      return this.$route.params.moduleName
    },
    availableFields() {
      return this.formFields
    },
    isSummaryFieldConfigured() {
      return !isEmpty(this.summaryFields)
    },
    isRelatedModulesConfigured() {
      return !isEmpty(this.selectedModulesList)
    },
    isApprovalFormConfigured() {
      let { approvalFormId } = this.policy
      return !isEmpty(approvalFormId) ? approvalFormId !== -1 : false
    },
    isRejectionFormConfigured() {
      let { rejectFormId } = this.policy
      return !isEmpty(rejectFormId) ? rejectFormId !== -1 : false
    },
    isResendFormConfigured() {
      let { resendFormId } = this.policy
      return !isEmpty(resendFormId) ? resendFormId !== -1 : false
    },
    selectedModulesList: {
      get() {
        return this.selectedModules
      },
      set(values) {
        this.selectedModules = values
      },
    },
    selectedFieldsList: {
      get() {
        let { activePopupType } = this
        if (activePopupType === popupTypes.APPROVE) {
          return this.approvalFields
        } else if (activePopupType === popupTypes.REJECT) {
          return this.rejectFields
        } else if (activePopupType === popupTypes.RESEND) {
          return this.resendFields
        } else {
          return this.summaryFields
        }
      },
      set(fields) {
        let { activePopupType } = this
        if (activePopupType === popupTypes.APPROVE) {
          this.approvalFields = fields
        } else if (activePopupType === popupTypes.REJECT) {
          this.rejectFields = fields
        } else if (activePopupType === popupTypes.RESEND) {
          this.resendFields = fields
        } else {
          this.summaryFields = fields
        }
      },
    },
    fieldPopupDescription() {
      let { activePopupType } = this
      if (activePopupType === popupTypes.SUMMARY) {
        return this.$t('common._common.choose_fields_to_display_overview')
      } else if (activePopupType === popupTypes.APPROVE) {
        return this.$t('common._common.choose_fields_to_update_while_approving')
      } else if (activePopupType === popupTypes.RESEND) {
        return this.$t('common._common.choose_fields_to_update_while_resending')
      } else {
        return this.$t('common._common.choose_fields_to_update_while_rejecting')
      }
    },
  },
  watch: {
    policy: {
      async handler() {
        await this.getModules()
        this.init()
      },
      immediate: true,
    },
    approvalFields: {
      handler() {
        if (this.canWatchForChanges) this.$emit('modified')
      },
      deep: true,
    },
    rejectFields: {
      handler() {
        if (this.canWatchForChanges) this.$emit('modified')
      },
      deep: true,
    },
    resendFields: {
      handler() {
        if (this.canWatchForChanges) this.$emit('modified')
      },
      deep: true,
    },
  },
  methods: {
    init() {
      if (this.isNew) {
        return
      }
      let { formFields, relatedModules } = this
      let { approvalForm, rejectForm, configJson } = this.policy

      let sections = approvalForm ? approvalForm.sections : []
      let fields = !isEmpty(sections) ? sections[0].fields : []
      this.approvalFields = cloneDeep(fields)

      sections = rejectForm ? rejectForm.sections : []
      fields = !isEmpty(sections) ? sections[0].fields : []
      this.rejectFields = cloneDeep(fields)

      let { fields: fieldList, relatedModules: moduleList } = JSON.parse(
        configJson || {}
      )

      this.summaryFields = cloneDeep(fieldList || [])
        .map(id => formFields.find(({ fieldId }) => fieldId === id))
        .filter(field => !isEmpty(field))

      this.selectedModules = cloneDeep(moduleList || [])
        .map(id => relatedModules.find(({ moduleId }) => moduleId === id))
        .filter(moduleId => !isEmpty(moduleId))

      this.$nextTick(() => (this.canWatchForChanges = true))
    },

    getModules() {
      return this.$http
        .get(`v2/forms/subFormModules?moduleName=${this.moduleName}`)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.relatedModules = (
              data.result.modules || []
            ).filter(moduleObj =>
              [moduleTypes.BASE_ENTITY, moduleTypes.SUB_ENTITY].includes(
                moduleObj.type
              )
            )
          }
        })
    },

    saveFields(fields) {
      this.selectedFieldsList = fields
      if (isEmpty(fields)) {
        this.$message.error(this.$t('common.header.please_select_one_field'))
      } else {
        if (this.activePopupType === popupTypes.APPROVE) {
          this.createForm('approval', this.approvalFields)
        }
        if (this.activePopupType === popupTypes.REJECT) {
          this.createForm('reject', this.rejectFields)
        }
        if (this.activePopupType === popupTypes.RESEND) {
          this.createForm('resend', this.resendFields)
        }
        this.canShowFieldPopup = false
        this.$nextTick(() => (this.activePopupType = null))
      }
    },
    openPopup(type) {
      this.activePopupType = type
      this.canShowFieldPopup = true
    },
    closePopup() {
      this.canShowFieldPopup = false
      this.activePopupType = null
    },
    async createForm(formType, fields) {
      const { moduleName } = this
      const { linkName: appLinkName, id: appId } = getApp()
      const url = 'v2/forms/add'

      const params = {
        appLinkName,
        moduleName,
        form: {
          name: `${formType}_${uuid()}`,
          displayName: 'Enter details',
          appLinkName,
          appId,
          labelPosition: 1,
          showInMobile: true,
          hideInList: true,
          siteIds: [],
          sections: [{ fields: fields }],
          stateFlowId: -99,
        },
      }

      const { error, data } = await API.post(url, params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        const { form } = data

        if (formType === 'approval') {
          this.$emit('setProps', {
            approvalFormId: form.id,
            approvalForm: form,
          })
        }
        if (formType === 'resend') {
          this.$emit('setProps', {
            resendFormId: form.id,
            resendForm: form,
          })
        }
        if (formType === 'reject') {
          this.$emit('setProps', {
            rejectFormId: form.id,
            rejectForm: form,
          })
        }

        this.editForm(formType, form.id)
      }
    },
    editForm(formType, id) {
      if (formType === 'approval') {
        let { id: formId } = this.policy.approvalForm
        this.activeFormId = id || formId
      } else if (formType === 'resend') {
        this.activeFormId = this.policy.resendFormId
      } else {
        let { id: formId } = this.policy.rejectForm
        this.activeFormId = id || formId
      }
      this.canShowFormBuilder = true
    },
    closeForm() {
      this.activeFormId = null
      this.canShowFormBuilder = false
    },
    removeForm(formType) {
      if (formType === 'approval') {
        this.$emit('setProps', {
          approvalFormId: -1,
          approvalForm: null,
        })

        this.approvalFields = []
      } else if (formType === 'resend') {
        this.$emit('setProps', {
          resendFormId: -1,
          resendForm: null,
        })

        this.resendFields = []
      } else {
        this.$emit('setProps', {
          rejectFormId: -1,
          rejectForm: null,
        })

        this.rejectFields = []
      }
    },

    serialize() {
      let { approvalFormId, rejectFormId, resendFormId } = this.policy
      let configJson = JSON.stringify({
        fields: this.summaryFields
          .map(({ fieldId }) => fieldId)
          .filter(id => !isEmpty(id)),

        relatedModules: this.selectedModules
          .map(({ moduleId }) => moduleId)
          .filter(moduleId => !isEmpty(moduleId)),
      })

      return {
        configJson,
        approvalFormId,
        rejectFormId,
        resendFormId,
      }
    },
  },
}
</script>
