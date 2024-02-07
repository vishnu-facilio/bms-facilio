<template>
  <div v-if="showForm">
    <TransitionForm
      :moduleName="selectedButton.moduleName"
      :recordId="record.id"
      :formId="selectedButton.formId"
      :record="record"
      :isExternalModule="isExternalModule"
      :transition="selectedButton"
      :saveAction="executeAction"
      :closeAction="closeFlowForm"
      :actionType="actionType"
      :isFormOfCustomButton="true"
      :approvalRule="null"
      :isV3="isV3"
    ></TransitionForm>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import helpers from 'src/util/helpers.js'
import TransitionForm from '@/stateflow/TransitionForm'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { ACTION_TYPES } from 'pages/setup/custombutton/CustomButtonUtil'
import { getApp } from '@facilio/router'
export default {
  props: [
    'selectedButton',
    'record',
    'moduleName',
    'updateUrl',
    'transformFn',
    'actionType',
    'isPortalApp',
  ],
  components: { TransitionForm },
  data() {
    return {
      showForm: false,
    }
  },
  created() {
    this.startAction()
  },
  computed: {
    isExternalModule() {
      let formModuleName = this.$getProperty(
        this.selectedButton,
        'formModuleName'
      )
      let { moduleName } = this
      return (
        formModuleName &&
        (formModuleName !== moduleName || formModuleName === 'workorder')
      )
    },
    isV3() {
      let { moduleName, updateUrl, isExternalModule } = this
      let isUpdateUrlEmpty = !isEmpty(moduleName) && isEmpty(updateUrl)

      return isExternalModule || isUpdateUrlEmpty
    },
    woTransitionStateLicenseEnabled() {
      const { isLicenseEnabled } = helpers || {}
      const woTransitionStateLicense = isLicenseEnabled(
        'WO_STATE_TRANSITION_V3'
      )
      return woTransitionStateLicense
    },
  },
  methods: {
    async startAction() {
      this.showForm = true
    },
    closeFlowForm() {
      this.showForm = false
      this.$emit('closed')
    },
    async executeAction(formData, formId) {
      let {
        selectedButton,
        record,
        moduleName,
        isExternalModule,
        woTransitionStateLicenseEnabled,
      } = this
      let params
      let isVendorPortal = getApp().linkName === 'vendor'
      let isNotWoV3 =
        this.moduleName === 'workorder' && !woTransitionStateLicenseEnabled

      if ((isVendorPortal || !this.isPortalApp) && isNotWoV3) {
        let url = this.getUrl(selectedButton)
        let formModuleName = this.$getProperty(selectedButton, 'formModuleName')

        if (this.actionType === ACTION_TYPES.CREATE_RECORD) {
          let { error } = await this.createRecordForModule(
            formModuleName,
            formData,
            selectedButton
          )
          if (error) {
            this.responseHandler({ error: error })
            return
          }
        }
        params = this.transformFn(
          {
            moduleName: moduleName,
            id: record.id,
            customButtonId: selectedButton.id,
          },
          isExternalModule ? {} : formData
        )

        return API.post(url, params).then(this.responseHandler)
      } else {
        let relations
        if (this.actionType === ACTION_TYPES.CREATE_RECORD) {
          let fieldId = this.$getProperty(selectedButton, 'lookupField.id')
          let formModuleName = this.$getProperty(
            selectedButton,
            'formModuleName'
          )

          relations = {
            [formModuleName]: [
              {
                fieldId: fieldId,
                data: [{ ...formData, formId }],
              },
            ],
          }
        }
        params = {
          id: record.id,
          customButtonId: selectedButton.id,
          data: isExternalModule ? { relations } : formData,
        }

        let { id: recordId } = record || {}
        let url = `v3/action/${moduleName}/${recordId}/customButton`

        return API.patch(url, params).then(this.responseHandler)
      }
    },
    createRecordForModule(moduleName, formData, selectedButton) {
      let recordId = this.$getProperty(this.record, 'id')
      let fieldName = this.$getProperty(selectedButton, 'lookupField.name')

      if (moduleName === 'workorder') {
        let siteId = this.$getProperty(this.record, 'siteId')
        let formId = this.$getProperty(selectedButton, 'formId')

        let { tasksString, ticketattachments } = formData
        delete formData.ticketattachments
        delete formData.tasksString

        if (this.$getProperty(selectedButton, 'lookupField.default')) {
          formData[fieldName] = { id: recordId }
        } else {
          this.$setProperty(formData, `data.${fieldName}`, recordId)
        }

        formData = {
          workorder: { ...formData, formId, siteId },
        }
        if (!isEmpty(ticketattachments)) {
          formData['ticketattachments'] = ticketattachments
        }
        if (!isEmpty(tasksString)) {
          formData['tasksString'] = JSON.stringify(tasksString)
        }

        let url = 'v2/workorders/add'
        return API.post(url, formData)
      } else {
        formData[fieldName] = { id: recordId }

        return API.createRecord(moduleName, {
          data: formData,
        })
      }
    },
    responseHandler({ error }) {
      this.$emit('closed')
      if (error) {
        this.$message.error(error.message || 'Error occured')
        this.$emit('response', false)
      } else {
        if (this.isExternalModule) {
          this.$message.success('Record created succesfully')
        } else {
          this.$message.success('Action executed successfully')
        }
        this.$emit('response', true)
      }
    },
    getUrl(buttonItem) {
      if (isFunction(this.updateUrl)) {
        return this.updateUrl(buttonItem)
      } else {
        return this.updateUrl
      }
    },
  },
}
</script>

<style></style>
