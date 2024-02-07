<template>
  <div class="fc-bulk-form-page">
    <div class="bulk-form-data-creation">
      <el-header height="80" class="bulk-form-header">
        <div class="header d-flex justify-content-space">
          <div class="form-header-name-description">
            <el-input
              v-model="groupName"
              placeholder="Group Invite Name"
              :autofocus="true"
              class="width300px pR30"
            ></el-input>
            <button
              @click="groupinvitedialog = true"
              class="group-invite-description"
              style="position: relative;
                top: 5px;
                font-size: medium;"
            >
              <i class="el-icon-tickets" style="width: 16px;"></i>
            </button>

            <el-dialog
              title="Group description"
              :visible.sync="groupinvitedialog"
              class="el-dialog__body"
            >
              <el-input
                type="textarea"
                :autosize="{ minRows: 7, maxRows: 7 }"
                class="fc-input-full-border-textarea"
                :placeholder="$t('setup.setupLabel.add_a_decs')"
                v-model="groupDescription"
                resize="none"
              ></el-input>

              <div class="modal-dialog-footer">
                <el-button @click="canceldialog" class="modal-btn-cancel">
                  {{ $t('common._common.cancel') }}
                </el-button>
                <el-button
                  type="primary"
                  class="modal-btn-save"
                  @click="groupinvitedialog = false"
                >
                  {{ $t('common._common.confirm') }}
                </el-button>
              </div>
            </el-dialog>
          </div>
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
              v-if="!this.isEdit"
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit()"
              :loading="isSaving"
            >
              {{ $t('common._common._save') }}
            </el-button>
            <el-button
              v-if="this.isEdit"
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="bulkSubmit()"
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
      <InviteBulkForm
        ref="invitebulkform"
        v-else-if="lookupLoading"
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="false"
        :canShowSecondaryBtn="false"
        :isEdit="isEdit"
        :customClass="customClass"
        :moduleDataId="moduleDataId"
        :moduleData="invitesList"
        :lookupOptions="lookupOptions"
      ></InviteBulkForm>
    </div>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import InviteBulkForm from '../../../components/bulkform/InviteBulkForm.vue'
import { isEmpty, isFunction } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { isLookupSimple } from '@facilio/utils/field'
import { getFieldOptions } from 'util/picklist'
import { isChooserTypeField } from 'util/field-utils'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import Constants from 'util/constant'

const { FORM_SOURCE } = Constants

export default {
  extends: FormCreation,
  components: { InviteBulkForm },
  props: {
    beforeSaveHook: {
      type: Function,
    },
  },
  data() {
    return {
      groupName: null,
      groupDescription: '',
      invitesList: {},
      invitevisitor: [],
      lookupOptions: {},
      lookupLoading: false,
      groupinvitedialog: false,
      desc: '',
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
        await this.loadGroupInviteModuleData({
          moduleName,
          moduleDataId,
        })
      }
      await this.loadform()
    },
    triggerBulkSubmit() {
      this.isSaving = true
      this.$refs['invitebulkform']
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
    async loadGroupInviteModuleData({ moduleDataId, moduleName }) {
      if (moduleDataId !== null) {
        let { groupinvite, error } = await API.fetchRecord(moduleName, {
          id: moduleDataId,
        })
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
        } else {
          this.summaryData = groupinvite || []
          this.invitesList = groupinvite.groupChildInvites
          this.groupName = groupinvite.name
          this.groupDescription = groupinvite.description
          this.desc = groupinvite.description
        }
      }
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
      this.loadPrefillData(formObj.fields)
      this.isLoading = false
      return formObj
    },
    async loadPrefillData(allFields) {
      if (this.isEdit) {
        let promiseArray = []
        allFields.forEach(async formField => {
          if (isLookupSimple(formField) || isChooserTypeField(formField)) {
            promiseArray.push(this.getLookupData(formField))
          }
        })
        let response = await Promise.all(promiseArray)
        response.forEach(lookupData => {
          this.lookupOptions[lookupData.moduleName] = lookupData.options
        })
      }
      this.lookupLoading = true
    },
    async getLookupData(formField) {
      let moduleName = formField.lookupModuleName
      let url = `/v3/picklist/${moduleName}`
      let { error, data } = (await API.get(url)) || {}
      let { pickList: options } = data || {}
      if (error) {
        this.$message.error('Error Fetching Lookup')
      } else {
        let lookup = { moduleName: moduleName, options: options }
        return lookup
      }
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
        groupDescription,
        visitorTypeId,
        afterSerializeHook,
      } = this
      let { id: formId } = formObj || {}
      let response = {}

      formModel.forEach(inviteData => {
        let grpInvite = this.serializedData(formObj, inviteData)
        grpInvite = {
          ...grpInvite,
          visitorType: { id: visitorTypeId },
          formId,
          id: inviteData.id,
        }
        this.invitevisitor.push(grpInvite)
      })

      let data = {
        name: groupName,
        description: groupDescription,
        relations: { invitevisitor: [{ data: this.invitevisitor }] },
      }

      if (!isEmpty(formId)) {
        data.formId = formId
      }

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
      let moduleName = 'groupinvite'

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        name && this.$router.push({ name })
      } else {
        if (this.$helpers.isLicenseEnabled('GROUP_INVITES')) {
          this.$router.push({ name: 'group-invites-list' })
        } else {
          this.$router.push({ name: 'invites-list' })
        }
      }
    },
    canceldialog() {
      if (this.isEdit) {
        this.groupinvitedialog = false
        this.groupDescription = this.desc
      } else {
        this.groupinvitedialog = false
        this.groupDescription = ''
      }
    },
  },
}
</script>
<style scoped lang="scss">
.group-invite-description {
  padding: 5px 5px 5px 4px;
  cursor: pointer;
  text-align: center;
  border: none;
  border-left: 2px solid transparent;
  background: none;
}
.group-invite-description:hover,
.group-invite-description:focus {
  padding: 5px 5px 5px 4px;
  border-radius: 50%;
  background-color: rgb(202 212 216 / 0.5);
}
.el-dialog__body {
  width: 70%;
  display: block;
  margin-left: auto;
  margin-right: auto;
  padding-top: 1px;
}
.fc-input-full-border-textarea {
  height: 199px;
  position: relative;
  bottom: 14px;
}
.form-header-name-description {
  display: block ruby;
}
.el-dialog__title {
  position: relative;
  bottom: 20px;
}
</style>
