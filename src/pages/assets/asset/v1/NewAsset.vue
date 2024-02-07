<template>
  <div>
    <el-dialog
      :visible.sync="showCreateNewDialog"
      v-if="showCreateNewDialog"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog f-webform-right-dialog new-asset-container"
      :before-close="closeDialog"
    >
      <div class="header pT10 pB10 pL40 pR40 d-flex">
        <div class="title mT10">{{ title }}</div>
        <el-select
          v-if="forms.length > 1"
          v-model="selectedForm"
          @change="handleSelectChange(...arguments)"
          value-key="name"
          class="fc-input-full-border-select2 mL-auto width25"
        >
          <el-option
            v-for="(form, index) in forms"
            v-if="form.name !== 'default_asset_mobile'"
            :key="index"
            :value="form"
            :label="form.displayName"
          ></el-option>
        </el-select>
      </div>
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <f-webform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="moduleDisplayName"
        :fieldTransitionFn="setSelectedCategory"
        formLabelPosition="top"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="!$validation.isEmpty(dataId)"
        @save="saveRecord"
        @cancel="closeDialog"
      ></f-webform>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import FormCreation from '@/base/FormCreation'
import { isEmpty } from '@facilio/utils/validation'

export default {
  extends: FormCreation,
  props: [
    'canShowAssetCreation',
    'moduleName',
    'moduleDisplayName',
    'dataId',
    'selectedCategory',
  ],
  computed: {
    showCreateNewDialog: {
      get() {
        return this.canShowAssetCreation
      },
      set(value) {
        this.$emit('update:canShowAssetCreation', value)
      },
    },
    currentSelectedCategory: {
      get() {
        return this.selectedCategory
      },
      set(value) {
        this.$emit('update:selectedCategory', value)
      },
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    /* getter setter methods*/
    setSelectedCategory(field) {
      let { selectedCategory } = this
      if (field.name === 'category') {
        let categoryId = (selectedCategory || {}).id
        this.$set(field, 'value', categoryId)
        this.$set(field, 'disabled', true)
      }
      return field
    },
    /* event related methods */
    closeDialog() {
      this.$set(this, 'currentSelectedCategory', null)
      this.$set(this, 'showCreateNewDialog', false)
    },
    handleSelectChange(formObj) {
      let { module } = formObj
      let { name } = module || {}
      if (!isEmpty(name)) {
        this.$emit('update:moduleName', name)
      }
      this.$emit('update:moduleName', 'asset')
    },
    async saveRecord(data) {
      let { formObj, moduleDataId, moduleDisplayName, moduleName } = this
      let formId = formObj?.id
      let successMsg = ''
      let assetToSave = this.serializedData(formObj, data)
      if (
        assetToSave.rotatingItem &&
        assetToSave.rotatingItem.id &&
        assetToSave.rotatingItem.id > 0 &&
        assetToSave.rotatingTool &&
        assetToSave.rotatingTool.id &&
        assetToSave.rotatingTool.id > 0
      ) {
        this.$message.error(
          'Asset can only be either a Rotating Item or a Rotating Tool'
        )
        return
      }
      if (isEmpty(moduleDataId)) {
        successMsg = `${moduleDisplayName} created successfully!`
      } else {
        successMsg = `${moduleDisplayName} updated successfully!`
        assetToSave.id = moduleDataId
      }
      this.isSaving = true
      if (!isEmpty(formId)) {
        assetToSave.formId = formId
      }
      let response = {}
      if (isEmpty(moduleDataId)) {
        response = await API.createRecord(moduleName, {
          data: assetToSave,
        })
      } else {
        response = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data: assetToSave,
        })
      }
      let { error } = response || {}
      this.isSaving = false
      if (error) {
        this.$message.error(
          error?.message || 'Error occured while saving asset'
        )
      } else {
        this.$message.success(successMsg)
        this.closeDialog()
        this.$emit('refreshlist')
      }
    },
    /* Overrided hooks */
    async loadFormsList(moduleName) {
      let categoryModuleName = this.selectedCategory.moduleName
      let url = `/v2/forms?moduleName=${categoryModuleName}&fetchExtendedModuleForms=${true}`
      this.isLoading = true
      let { data, error } = await API.get(url)
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        let { forms = [] } = data || {}
        this.$set(this, 'forms', forms)
      }
    },
  },
}
</script>
<style lang="scss">
.new-asset-container {
  .header {
    .title {
      font-size: 14px;
      font-weight: 700;
      letter-spacing: 0.3px;
      letter-spacing: 0.9px;
      color: #333;
      text-transform: uppercase;
      background: #fff;
    }
    border-bottom: 1px solid #ebedf4;
  }
  .pT50 {
    padding-top: 20px;
  }

  .f-webform-container .el-form .section-header {
    margin-bottom: 10px;
  }
}
</style>
