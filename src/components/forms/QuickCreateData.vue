<template>
  <div>
    <el-dialog
      :visible.sync="canShowDialog"
      :append-to-body="true"
      :title="`Add ${moduleDisplayName}`"
      :before-close="closeDialog"
      custom-class="fc-dialog-center-container f-webform-right-dialog quick-create-container"
      top="5vh"
    >
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        @save="createQuickRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Constants from 'util/constant'

export default {
  name: 'QuickCreateData',
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['canShowDialog', 'quickCreateField', 'setAddedRecord'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    moduleName() {
      let { quickCreateField } = this
      let { lookupModuleName, config } = quickCreateField
      let {
        isFiltersEnabled = false,
        filterValue,
        lookupModuleName: configLookupModuleName,
      } = config || {}
      if (isFiltersEnabled) {
        lookupModuleName =
          configLookupModuleName || Constants.LOOKUP_FILTERS_MAP[filterValue]
      }
      return lookupModuleName
    },
    moduleDisplayName() {
      let { quickCreateField } = this
      let { displayName } = quickCreateField || {}

      return displayName
    },
    moduleDataId() {
      return null
    },
    // Have to remove this hack, once we implement v3 for below listed module
    isV3Api() {
      let { moduleName } = this
      let v2SupportedArr = [
        'building',
        'asset',
        'tenantcontact',
        'clientcontact',
        'vendorcontact',
        'employee',
      ]
      return !v2SupportedArr.includes(moduleName)
    },
  },
  methods: {
    async afterSaveHook(response) {
      let { moduleName, isV3Api } = this
      let moduleData = {}
      if (isV3Api) {
        moduleData = response[moduleName] || {}
      } else {
        moduleData = response['moduleData'] || {}
      }
      if (!isEmpty(moduleData)) {
        this.setAddedRecord({
          record: moduleData,
        })
        this.closeDialog()
      }
    },
    closeDialog() {
      this.$emit('update:canShowDialog', false)
      this.$emit('update:quickCreateField', null)
    },
    // Have to remove this once, all the modules are supported by v3
    createQuickRecord(formModel) {
      let { isV3Api } = this
      if (isV3Api) {
        this.saveRecord(formModel)
      } else {
        this.saveRecordV2(formModel)
      }
    },
    systemFieldHandler(finalObj, field, value) {
      let { name } = field
      if (!isEmpty(value)) {
        finalObj.data[name] = value
      }
      return finalObj
    },
    async saveRecordV2(formModel) {
      let { moduleName, formObj } = this
      let { formId } = formModel
      let url = '/v2/module/data/add'
      let data = {
        moduleName,
        withLocalId: false,
        moduleData: this.serializedData(formObj, formModel),
      }
      if (!isEmpty(formId)) {
        data.moduleData.formId = formId
      }
      this.isSaving = true
      let response = await API.post(url, data)
      this.isSaving = false
      let { data: responseData = {} } = response || {}
      this.notificationHandler(response)
      this.afterSaveHook(responseData)
    },
  },
}
</script>
<style lang="scss">
.quick-create-container {
  .f-webform-container {
    max-height: 600px;
    padding: 0px;
    .el-form {
      padding-bottom: 60px;
      .section-container {
        &.flex-container {
          padding: 0px;
          padding-top: 10px;
        }
        .pT50 {
          padding-top: 0px;
          padding-bottom: 10px;
        }
      }
    }
  }
}
</style>
