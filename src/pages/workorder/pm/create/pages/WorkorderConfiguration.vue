<template>
  <div class="wo-config-container">
    <div v-if="isLoading" class="d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <template v-else>
      <div class="width50 mB30">
        <div class="section-heading mB10 mT20">
          {{ $t('maintenance.pm.scope') }}
        </div>
        <ScopeConfiguration
          ref="scope-config-form"
          @onScopeChange="setScopeProperties"
          :labelPosition="labelPosition"
          :scopeModel="scopeModel"
          :isEdit="isEdit"
          :isScopeEdited.sync="isScopeEdited"
          :deletedSiteObj.sync="deletedSiteObj"
        />
      </div>
      <div
        class="form-data-creation inspection-form-template pm-content-width"
        :class="[isLoading ? 'height100' : '', customClassForContainer]"
      >
        <div class="header mT20 mB10 d-flex">
          <div class="section-heading flex align-center">
            {{ $t('maintenance.pm.workorder_details') }}
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
        </div>

        <div class="inspection-form-creation">
          <f-webform
            ref="f-web-form"
            :form.sync="formObj"
            :module="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :isSaving="isSaving"
            :canShowPrimaryBtn="false"
            :canShowSecondaryBtn="false"
            :isEdit="isEdit"
            :isV3Api="isV3Api"
            :isWidgetsSupported="isWidgetsSupported"
            :moduleData="moduleData"
            :moduleDataId="moduleDataId"
            @disableSaveBtn="disableSaveBtn"
            @onFormModelChange="setFormProperties"
            @save="saveRecord"
          ></f-webform>
        </div>
      </div>
    </template>
    <slot></slot>
  </div>
</template>

<script>
import FormCreation from 'pages/custom-module/CustomModulesCreation'
import ScopeConfiguration from '../components/ScopeConfiguration.vue'
import { API } from '@facilio/api'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'
import { getPlaceholderText } from '../utils/pm-utils.js'
import { PUBLISH_STATUS } from 'pages/workorder/pm/create/utils/pm-utils.js'

export default {
  name: 'WorkorderConfiguration',
  extends: FormCreation,
  props: ['pmProps', 'disableButton'],
  components: { ScopeConfiguration },
  data: () => ({
    formModel: {},
    scopeModel: {},
    isScopeEdited: false,
    deletedSiteObj: {},
  }),
  computed: {
    moduleName() {
      return 'plannedmaintenance'
    },
    moduleDataId() {
      let { $route, dataId } = this
      let { pmProps } = this || {}
      let { id: pmId } = pmProps || {}

      if (!isEmpty(pmId)) {
        return pmId
      }
      if (isEmpty(dataId)) {
        let id = this.$attrs.id || $route.params.id

        return !isEmpty(id) ? Number(id) : id
      }
      return dataId
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    isV3Api() {
      return true
    },
    labelPosition() {
      let { formObj } = this || {}
      let { labelPosition } = formObj || {}
      return Constants.FORMS_LABELALIGNMENT_ENUMHASH[labelPosition] || 'left'
    },
    siteDialogMessage() {
      let { deletedSiteObj } = this
      let { deletedSiteCount } = deletedSiteObj || {}
      if (deletedSiteCount === 1) {
        return this.$t('maintenance.pm.site_confirmation1', {
          siteCount: deletedSiteCount,
        })
      } else {
        return this.$t('maintenance.pm.site_confirmation2', {
          siteCount: deletedSiteCount,
        })
      }
    },
  },
  watch: {
    isLoading(value) {
      this.disableSaveBtn(value)
    },
  },
  methods: {
    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData, moduleDataId } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName
      let formUrl =
        id === -1
          ? `/v2/forms/${moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${moduleName}?formId=${formId ||
              id}&fetchFormRuleFields=true`
      this.isLoading = true
      let { data, error } = await API.get(formUrl)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { form } = data
        if (!isEmpty(form)) {
          formObj = form
          formObj.secondaryBtnLabel = this.$t('common._common.cancel')
          formObj.primaryBtnLabel = this.$t('common._common._save')
          this.$set(this, 'formObj', formObj)
          if (!isEmpty(moduleDataId)) {
            this.deserializeData(moduleData)
          }
          this.scopeModel = moduleData
        }
      }
      this.isLoading = false
      return formObj
    },
    setScopeProperties(value) {
      this.scopeModel = value
    },
    setFormProperties(value) {
      let { formModel } = this || {}
      this.formModel = { ...formModel, ...value }
    },
    async onSave() {
      await this.$refs['scope-config-form'].validate()
      this.$refs['f-web-form'].saveRecord()
    },
    async loadModuleData({ moduleDataId, moduleName }) {
      let response
      this.isLoading = true
      if (this.isV3Api) {
        response = await API.fetchRecord(moduleName, { id: moduleDataId })
      } else {
        response = await API.get(
          `/v2/module/data/${moduleDataId}?moduleName=${moduleName}`,
          { force: true }
        )
      }

      let { error, data = {}, [moduleName]: record } = response
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let moduleData = this.isV3Api ? record : data['moduleData']
        this.$set(this, 'moduleData', moduleData)
      }
      this.isLoading = false
    },
    getFormattedText(text, isUpperCase) {
      let { moduleData } = this || {}

      return getPlaceholderText({ pmRecord: moduleData, text, isUpperCase })
    },
    async saveRecord() {
      let {
        moduleName,
        moduleDataId,
        isScopeEdited,
        deletedSiteObj,
        siteDialogMessage,
      } = this || {}

      let { deletedSiteCount } = deletedSiteObj || {}
      let canSave = true
      if (isScopeEdited) {
        let dialogObj = {
          title: this.getFormattedText(this.$t('maintenance.pm.scope_changed')),
          htmlMessage: this.getFormattedText(
            this.$t('maintenance.pm.scope_changed_desc')
          ),
          rbDanger: true,
          rbLabel: 'Confirm',
        }
        canSave = await this.$dialog.confirm(dialogObj)
      }
      if (deletedSiteCount) {
        canSave = await this.$dialog.confirm({
          htmlMessage: siteDialogMessage,
          rbLabel: this.$t('maintenance.pm.proceed'),
          rbClass: 'pmv2-edit-dialog-btn',
          className: 'pmv2-edit-dialog',
        })
        if (!canSave) this.deletedSiteObj.canRevert = true
      }
      if (canSave) {
        this.$emit('update:isSaving', true)
        let { formModel, scopeModel, formObj, selectedForm } = this || {}
        let { id: formId } = selectedForm || {}
        let data = this.serializedData(formObj, formModel)
        data = { ...scopeModel, ...data, formId }
        // Create PM without Publishing it.
        data = { ...data, pmStatus: PUBLISH_STATUS['Unpublish'] }
        let promise
        if (isEmpty(moduleDataId)) {
          promise = API.createRecord(moduleName, {
            data,
          })
        } else {
          promise = API.updateRecord(moduleName, {
            data,
            id: moduleDataId,
          })
        }

        let { error, [moduleName]: record } = await promise

        if (!error) {
          let { id } = record || {}
          this.$router.push({ path: '', query: { id: id, tab: 'planner' } })
        } else {
          this.$message.error(
            error || 'Error occured while creating pm configuration'
          )
        }

        this.$emit('update:isSaving', false)
      }
    },
    disableSaveBtn(value) {
      this.$emit('update:disableButton', value)
    },
  },
}
</script>

<style scoped>
.wo-config-container {
  overflow-y: scroll;
  height: calc(100vh - 210px);
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}
.form-data-creation {
  margin-left: 0px;
  max-width: none;
}
</style>

<style lang="scss">
.pmv2-edit-dialog {
  .f-dialog-header {
    display: none;
  }
  .f-dialog-content {
    padding-top: 30px;
    padding-left: 30px;
    padding-right: 10px;
    min-height: 185px;
    width: 650px;
    text-align: justify;
  }
  .f-dialog-body {
    padding: 0px;
  }
  .del-cancel-btn {
    width: 50%;
  }
  .pmv2-edit-dialog-btn {
    width: 50%;
    background-color: #39b2c2 !important;
    border: transparent;
    margin-left: 0;
    padding-top: 20px;
    padding-bottom: 20px;
    border-radius: 0;
    font-size: 13px;
    font-weight: bold;
    letter-spacing: 1.1px;
    text-align: center;
    color: #ffffff;
    &:hover {
      background-color: #3cbfd0 !important;
    }
  }
}
.wo-config-container {
  .inspection-form-creation {
    .height100 {
      height: auto !important;
    }
  }
  .pT50 {
    padding-top: 10px;
  }
  .f-webform-container {
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    height: 100%;
    .el-form .section-container {
      border: none;
      padding: 0px 1px;
      .section-header {
        margin-bottom: 0px;
      }
      .section-items {
        padding: 0px;
      }
    }
  }
}
</style>
