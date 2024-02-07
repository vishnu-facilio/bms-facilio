<template>
  <el-dialog
    :title="title"
    :visible.sync="canShowDialog"
    :fullscreen="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog50 setup-dialog catalog-item-creation"
    :append-to-body="true"
    :before-close="beforeClose"
  >
    <ErrorBanner
      :error.sync="error"
      :errorMessage.sync="errorMessage"
    ></ErrorBanner>
    <div class="new-header-container">
      <div class="new-header-text">
        <div class="fc-setup-modal-title">
          {{ title }}
        </div>
      </div>
    </div>
    <div class="f-webform-container catalog-creation pL40 pR40 pT10">
      <el-form
        ref="newServiceCatalogItem"
        :rules="rules"
        :model="serviceCatalogItem"
      >
        <div class="d-flex">
          <FFileUpload
            :imgContentUrl.sync="serviceCatalogItem.photoUrl"
            :isFileType="false"
            :showWebCamPhoto="false"
            :additionalFileFormat="['image/svg+xml']"
            :addImgFile="addImgFile"
            @removeImgFile="removeImgFile"
          >
            <div slot="placeHolderSlot" class="d-flex flex-direction-column">
              <inline-svg
                src="svgs/photo"
                class="folder-icon self-center"
                iconClass="icon icon-xxl"
              ></inline-svg>
              <div class="self-center">
                <p class="upload-img-text">
                  {{ $t('common._common.upload_logo') }}
                </p>
              </div>
            </div>
          </FFileUpload>
          <div class="name-section">
            <el-form-item
              :label="$t('common._common.name')"
              prop="name"
              class="mB10"
              :required="true"
            >
              <el-input
                :placeholder="$t('common._common.enter_name')"
                v-model="serviceCatalogItem.name"
                class="fc-input-full-border-select2"
              ></el-input>
            </el-form-item>
            <el-form-item
              :label="$t('servicecatalog.setup.category')"
              prop="groupId"
              class="mB10"
              :required="!serviceCatalogItem.complaintType"
            >
              <el-select
                :placeholder="$t('servicecatalog.setup.select_category')"
                v-model="serviceCatalogItem.groupId"
                class="fc-input-full-border-select2 width100"
                :disabled="serviceCatalogItem.groupId === -1"
              >
                <el-option
                  v-for="group in catalogGroups"
                  :key="group.id"
                  :label="group.name"
                  :value="group.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
        </div>
        <el-form-item
          :label="$t('common.wo_report.report_description')"
          prop="description"
          class="mB10"
        >
          <el-input
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 4 }"
            :placeholder="$t('common._common.enter_desc')"
            v-model="serviceCatalogItem.description"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <el-form-item
          :label="$t('asset.assets.asset_type')"
          prop="type"
          class="mB10 d-flex flex-direction-column catalog-type"
        >
          <el-radio-group v-model="serviceCatalogItem.type">
            <el-radio class="fc-radio-btn" :label="1">{{
              $t('common._common.form')
            }}</el-radio>
            <el-radio class="fc-radio-btn" :label="2">{{
              $t('servicecatalog.setup.external_url')
            }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item
          v-if="canShowUrlBox"
          :label="$t('servicecatalog.setup.external_url')"
          class="mB10"
          prop="externalURL"
          :required="true"
        >
          <el-input
            :placeholder="$t('servicecatalog.setup.enter_external_url')"
            v-model="serviceCatalogItem.externalURL"
            class="fc-input-full-border-select2"
          ></el-input>
        </el-form-item>
        <div class="sub-header">
          {{ $t('servicecatalog.setup.moduledetails') }}
        </div>
        <el-form-item
          :label="$t('servicecatalog.setup.module_name')"
          prop="moduleId"
          class="mB10"
        >
          <el-select
            :placeholder="$t('servicecatalog.setup.selectmodules')"
            v-model="serviceCatalogItem.moduleId"
            class="fc-input-full-border-select2 width100"
            :loading="isModulesLoading"
          >
            <el-option
              v-for="(module, index) in modulesList"
              :key="index"
              :label="module.displayName"
              :value="module.moduleId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item
          :label="$t('servicecatalog.setup.forms')"
          prop="formName"
          class="mB10"
        >
          <el-select
            :placeholder="$t('servicecatalog.setup.selectforms')"
            v-model="serviceCatalogItem.formName"
            class="fc-input-full-border-select2 width100"
            :loading="isFormsLoading"
          >
            <el-option
              v-for="(form, index) in formsList"
              :key="index"
              :label="form.displayName"
              :value="form.name"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialogBox" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        :loading="isSaving"
        type="primary"
        class="modal-btn-save"
        @click="saveRecord"
        >{{ $t('common._common.confirm') }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import FFileUpload from '@/FFileUpload'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import cloneDeep from 'lodash/cloneDeep'
import { serializeProps } from '@facilio/utils/utility-methods'
import Constants from 'util/constant'

export default {
  props: [
    'canAddCatalogItem',
    'isEdit',
    'serviceCatalogItem',
    'catalogGroups',
    'cataLogModulesList',
    'appId',
  ],
  components: {
    ErrorBanner,
    FFileUpload,
  },
  data() {
    return {
      catalogLogo: null,
      formsList: [],
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter name',
            trigger: 'blur',
          },
        ],
        groupId: [
          {
            required: true,
            message: 'Please select a category',
            trigger: 'change',
          },
        ],
        description: [
          {
            required: true,
            message: 'Please enter description',
            trigger: 'blur',
          },
        ],
        moduleId: [
          {
            required: true,
            message: 'Please select a module',
            trigger: 'change',
          },
        ],
        formName: [
          {
            required: true,
            message: 'Please select a form',
            trigger: 'change',
          },
        ],
        externalURL: [
          {
            required: true,
            message: 'Please enter the external url',
            trigger: 'blur',
          },
        ],
      },
      error: '',
      errorMessage: '',
      isSaving: false,
      isModulesLoading: false,
      isFormsLoading: false,
    }
  },
  computed: {
    canShowDialog: {
      get() {
        return this.canAddCatalogItem
      },
      set(value) {
        this.$emit('update:canAddCatalogItem', value)
      },
    },
    modulesList: {
      get() {
        return this.cataLogModulesList
      },
      set(value) {
        this.$emit('update:cataLogModulesList', value)
      },
    },
    title() {
      let { isEdit } = this
      let title = this.$t('servicecatalog.setup.item')
      return isEdit ? `Edit ${title}` : `New ${title}`
    },
    canShowUrlBox() {
      let { serviceCatalogItem } = this
      let { type } = serviceCatalogItem || {}
      return type === 2
    },
  },
  watch: {
    'serviceCatalogItem.moduleId': {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.loadFormsList()
        }
      },
    },
    'serviceCatalogItem.type': {
      handler(newVal) {
        if (newVal === 1) {
          this.$set(this.serviceCatalogItem, 'externalURL', '')
        }
      },
    },
  },
  created() {
    let { modulesList, isEdit } = this
    if (isEmpty(modulesList)) {
      this.loadCatalogModulesList().then(() => {
        if (isEdit) {
          this.setCatalogFormData()
        }
      })
    } else {
      if (isEdit) {
        this.setCatalogFormData()
      }
    }
    if (!isEdit) {
      this.$set(this.serviceCatalogItem, 'type', 1)
    }
  },
  methods: {
    setFormValid() {
      this.error = false
      this.errorMessage = ''
    },
    setFormInvalid(msg) {
      this.error = true
      this.errorMessage = msg
    },
    closeDialogBox() {
      this.canShowDialog = false
    },
    beforeClose(done) {
      this.$set(this, 'errMsgVal', null)
      this.$set(this, 'errVal', false)
      done()
    },
    addImgFile(file) {
      this.$set(this, 'catalogLogo', file)
    },
    removeImgFile() {
      this.$set(this, 'catalogLogo', null)
      this.$set(this.serviceCatalogItem, 'photoId', null)
    },
    loadCatalogModulesList() {
      let url = `v2/servicecatalog/modules`
      this.$set(this, 'isModulesLoading', true)
      let promise = this.$http
        .get(url)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let { modules } = result
            let filteredModules = modules.filter(module => !isEmpty(module))
            this.$set(this, 'modulesList', filteredModules)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      return Promise.all([promise]).finally(() =>
        this.$set(this, 'isModulesLoading', false)
      )
    },
    loadFormsList() {
      let { serviceCatalogItem, appId } = this
      let { moduleId } = serviceCatalogItem
      let selectedModuleName = this.getSelectedModuleName(moduleId)
      if (!isEmpty(selectedModuleName)) {
        let url = `v2/forms?moduleName=${selectedModuleName}`
        if (!isEmpty(appId)) {
          url = url + `&appId=${appId}`
        }
        this.$set(this, 'isFormsLoading', true)
        let promise = this.$http
          .get(url)
          .then(({ data: { message, responseCode, result } }) => {
            if (responseCode === 0) {
              let { forms } = result
              this.$set(this, 'formsList', forms)
            } else {
              throw new Error(message)
            }
          })
          .catch(({ message }) => {
            this.$message.error(message)
          })
        return Promise.all([promise]).finally(() =>
          this.$set(this, 'isFormsLoading', false)
        )
      }
    },
    getSelectedModuleName(moduleId) {
      let { modulesList } = this
      return (modulesList.find(module => module.moduleId === moduleId) || {})
        .name
    },
    getSelectedForm(propValue, key) {
      let { formsList } = this
      return formsList.find(form => form[key] === propValue) || {}
    },
    setCatalogFormData() {
      let { serviceCatalogItem } = this
      let { formId } = serviceCatalogItem
      this.loadFormsList().then(() => {
        let selectedForm = this.getSelectedForm(formId, 'id') || {}
        this.$set(this.serviceCatalogItem, 'formName', selectedForm.name)
      })
    },
    saveRecord() {
      this.$refs['newServiceCatalogItem'].validate(valid => {
        if (valid) {
          let { serviceCatalogItem } = this
          let { formName } = serviceCatalogItem
          let selectedForm = this.getSelectedForm(formName, 'name')
          if (!isEmpty(selectedForm)) {
            let { id } = selectedForm
            if (isEmpty(id)) {
              this.addNewForm(serviceCatalogItem, selectedForm)
            } else {
              this.$set(serviceCatalogItem, 'formId', id)
              this.saveCatalogItemRecord(serviceCatalogItem)
            }
          }
        }
      })
    },
    addNewForm(serviceCatalogItem, selectedForm) {
      let { moduleId } = serviceCatalogItem
      let { description, displayName, appLinkName, name } = selectedForm
      let url = `v2/forms/add`
      let data = {
        moduleName: this.getSelectedModuleName(moduleId),
        form: {
          description,
          displayName,
          appLinkName,
          name,
        },
      }
      this.$http
        .post(url, data)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let {
              form: { id },
            } = result || {}
            this.$set(serviceCatalogItem, 'formId', id)
            this.saveCatalogItemRecord(serviceCatalogItem)
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
    },
    saveCatalogItemRecord(serviceCatalogItem) {
      let { catalogLogo } = this
      let { moduleId } = serviceCatalogItem
      let _clonedCatalogItem = cloneDeep(serviceCatalogItem)
      if (!isEmpty(_clonedCatalogItem.formName)) {
        delete _clonedCatalogItem.formName
      }
      _clonedCatalogItem = serializeProps(
        _clonedCatalogItem,
        Constants.CATALOG_RESOURCE_PROPS
      )
      if (catalogLogo) {
        _clonedCatalogItem.photoId = catalogLogo
      } else {
        _clonedCatalogItem.photoId = serviceCatalogItem.photoId
      }
      let data = {
        moduleName: this.getSelectedModuleName(moduleId),
        serviceCatalog: _clonedCatalogItem,
      }

      let url = `v2/servicecatalog/addOrUpdate`
      this.$set(this, 'isSaving', true)
      let promise = this.$http
        .post(url, data)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            this.$message.success(
              this.$t('common.products.service_item_created_successfully')
            )
            this.$emit('setActiveCatalog', result.serviceCatalog)
            this.closeDialogBox()
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => this.$set(this, 'isSaving', false))
    },
  },
}
</script>
<style lang="scss">
.catalog-item-creation {
  .f-webform-container {
    &.catalog-creation {
      .catalog-type {
        .el-form-item__label {
          align-self: flex-start;
        }
      }
      .el-form-item.is-required {
        .el-form-item__label {
          color: #324056;
        }
      }
    }
  }
  .sub-header {
    letter-spacing: 1.5px;
    color: #ef4f8f;
    font-size: 12px;
    font-weight: 500;
    margin: 30px 0px 10px;
    text-transform: uppercase;
  }
  .f-img-container {
    .upload-img-text {
      width: 86px;
      font-size: 13px;
      letter-spacing: 0.4px;
      text-align: center;
      color: #747f8f;
    }
    .photo-container {
      position: relative;
    }
    flex: 1 1 30%;
    display: flex;
    padding: 20px 20px 10px 0px;
    .f-img-empty {
      flex: 100%;
    }
  }
  .name-section {
    flex: 1 1 70%;
  }
}
</style>
