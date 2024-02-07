<template>
  <div class="fc-bulk-form-page">
    <div class="bulk-form-data-creation">
      <el-header height="80" class="bulk-form-header">
        <div class="header d-flex justify-content-space">
          <el-input
            v-model="groupName"
            placeholder="Group Invite Name"
            :autofocus="true"
            class="width300px"
          ></el-input>
          <el-select
            v-if="forms.length > 1"
            v-model="selectedForm"
            value-key="name"
            class="fc-input-full-border-select2 mL-auto width25"
          >
            <el-option
              v-for="(form, index) in forms"
              :key="index"
              :value="form"
              :label="form.displayName"
            ></el-option>
          </el-select>

          <div class="flex-middle width15">
            <el-button
              class="small-border-btn width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="redirectToList"
            >
              Cancel
            </el-button>
            <el-button
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit"
              :loading="isSaving"
            >
              Save
            </el-button>
          </div>
        </div>
      </el-header>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <bulk-form
        ref="bulkform"
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="false"
        :canShowSecondaryBtn="false"
        :isEdit="isEdit"
        :customClass="customClass"
      ></bulk-form>
    </div>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import Constants from 'util/constant'

const { FORM_SOURCE } = Constants

export default {
  extends: FormCreation,
  props: {
    beforeSaveHook: {
      type: Function,
    },
  },
  data() {
    return {
      groupName: null,
    }
  },
  computed: {
    isV3Api() {
      return true
    },
    bulkMode() {
      return true
    },
    moduleName() {
      return 'groupinvite'
    },
    moduleDisplayName() {
      let { module: formModule } = this.formObj || {}
      let { displayName } = formModule || {}

      return displayName || ''
    },
    formId() {
      let { query } = this.$route
      let { formId } = query || {}
      return formId
    },
    visitorTypeId() {
      let { query } = this.$route
      let { visitorTypeId } = query || {}
      return visitorTypeId
    },
    isWidgetsSupported() {
      return true
    },
  },
  methods: {
    async init() {
      let { moduleDataId, moduleName } = this

      if (!isEmpty(moduleDataId)) {
        await this.loadModuleData({
          moduleName,
          moduleDataId,
        })
      }
      await this.loadform()
    },
    triggerBulkSubmit() {
      this.isSaving = true
      this.$refs['bulkform']
        .triggerSubmit()
        .then(data => {
          this.saveRecord(data)
        })
        .catch(error => {
          this.$message(error)
          this.isSaving = false
        })
      this.isSaving = false
    },
    async loadFormForType() {
      this.isLoading = true
      let { visitorTypeId } = this
      let { data, error } = await API.post('v2/visitorSettings/get', {
        visitorType: { id: visitorTypeId },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.selectedForm = this.$getProperty(
          data,
          'visitorSettings.visitorInviteForm',
          {}
        )
      }
      this.isLoading = false
    },

    async loadform() {
      let params = {
        fetchFormRuleFields: true,
        formId: this.formId,
      }
      this.isLoading = true
      let { data, error } = await API.get('/v2/forms/invitevisitor', params)

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.selectedForm = data.form
      }
      this.isLoading = false
    },

    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName

      let params = id === -1 ? { formName: name } : { formId: formId || id }
      params['formSourceType'] = FORM_SOURCE.BULK_FORM

      this.isLoading = true
      let { data, error } = await API.get(`/v2/forms/${moduleName}`, params)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { form } = data
        if (!isEmpty(form)) {
          formObj = form
          formObj.secondaryBtnLabel = 'CANCEL'
          formObj.primaryBtnLabel = 'SAVE'
          this.$set(this, 'formObj', formObj)
          if (!isEmpty(moduleData)) {
            this.deserializeData(moduleData)
          }
        }
      }
      this.isLoading = false
      return formObj
    },

    bulkSubmit() {
      if (isEmpty(this.groupName)) {
        this.$message.error('Group name cannot be empty')
        return
      }
      this.triggerBulkSubmit()
    },
    async saveRecord(formModel) {
      let {
        formObj,
        moduleName,
        moduleDataId,
        groupName,
        visitorTypeId,
        afterSerializeHook,
      } = this
      let { id: formId } = formObj || {}
      let response = {}
      let data = { name: groupName, invitevisitor: [] }

      if (!isEmpty(formId)) {
        data.formId = formId
      }
      formModel.forEach(inviteData => {
        let grpInvite = this.serializedData(formObj, inviteData)
        grpInvite = { ...grpInvite, visitorType: { id: visitorTypeId }, formId }
        data.invitevisitor.push(grpInvite)
      })
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      if (!isEmpty(afterSerializeHook) && isFunction(afterSerializeHook)) {
        data = this.afterSerializeHook({
          data,
          formModel,
          formObj,
        })
      }
      this.isSaving = true
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        })
      }
      this.isSaving = false
      this.notificationHandler(response)
      this.afterSaveHook(response)
    },
    afterSaveHook({ error }) {
      if (!error) {
        this.beforeSaveHook()
        this.redirectToList()
      }
    },
    redirectToList() {
      let  moduleName  = 'invitevisitor'

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name })
      } else {
        this.$router.push({ name: 'invites-list' })
      }
    },
  },
}
</script>
