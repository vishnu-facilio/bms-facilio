<template>
  <!--  SINGLE FORM-->
  <div
    class="form-data-creation"
    :class="[
      !$validation.isEmpty(widgetsArr) ? 'form-creation-widgets' : '',
      customClassForContainer,
    ]"
    v-if="!bulkMode"
  >
    <div v-if="showEmptyState" class="form-empty-container">
      <inline-svg
        src="svgs/no-configuration"
        class="d-flex module-view-empty-state"
        iconClass="icon"
      ></inline-svg>
      <div class="mB10 mT10 no-template-text f14 self-center">
        {{ $t('forms.live_form.no_forms') }}
      </div>
      <div class="contact-admin-text f14 self-center">
        {{ $t('forms.live_form.contact_admin') }}
      </div>
    </div>
    <div
      v-else
      :class="[!$validation.isEmpty(widgetsArr) ? 'form-left-container' : '']"
      class="height-100"
    >
      <div class="header mT20 mB10 d-flex">
        <div class="title mT10">{{ title }}</div>
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
        :isEdit="isEdit"
        :isV3Api="isV3Api"
        :isWidgetsSupported="isWidgetsSupported"
        :moduleData="moduleData"
        :moduleDataId="moduleDataId"
        :subFormRecords="subFormRecords"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        @onBlur="onBlurHook"
        @onWidgetChange="onWidgetChange"
        @save="saveRecord"
        @cancel="redirectToList"
        @onFormModelChange="handleFormModlechange"
      ></f-webform>
    </div>
    <portal-target class="widgets-container" name="side-bar-widgets">
    </portal-target>
  </div>
  <!-- BULK Form -->
  <!-- eslint-disable-next-line vue/valid-template-root -->
  <div class="fc-bulk-form-page" v-else>
    <div class="bulk-form-data-creation">
      <el-header height="80" class="bulk-form-header">
        <div class="header d-flex justify-content-space">
          <div class="title line-height40 fw4">{{ title }}</div>
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
              >{{ $t('common._common.cancel') }}</el-button
            >
            <el-button
              class="small-border-btn-save width50 bR3 pB13 pT13 f12 text-uppercase"
              @click="triggerBulkSubmit"
              :loading="isSaving"
              >{{ $t('common._common._save') }}</el-button
            >
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
import Spinner from '@/Spinner'
import FWebform from '@/FWebform'
import {
  isEmpty,
  isFunction,
  isNullOrUndefined,
} from '@facilio/utils/validation'
import Constants from 'util/constant'
import isEqual from 'lodash/isEqual'
import DataCreationMixin from '@/mixins/DataCreationMixin'
import FormsEventMixin from '@/mixins/forms/FormsEventMixin'
import bulkForm from '@/bulkform/BulkForm'
import { API } from '@facilio/api'
import pick from 'lodash/pick'
import { getBaseURL } from 'util/baseUrl'
import { deepCloneObject } from 'util/utility-methods'

export default {
  name: 'form-creation',
  mixins: [DataCreationMixin, FormsEventMixin],
  components: {
    Spinner,
    FWebform,
    bulkForm,
  },
  data() {
    return {
      forms: [],
      isLoading: true,
      selectedForm: {},
      formObj: {},
      isSaving: false,
      moduleData: {},
      widgetsArr: [],
      subFormRecords: {},
    }
  },
  computed: {
    showEmptyState() {
      let { forms } = this || {}
      return (
        !this.isLoading && isEmpty(forms) && !this.isCreateEditWidgetConfigured
      )
    },
    bulkMode() {
      let { $route } = this
      let { query } = $route || {}
      let { bulkmode } = query || {}
      return bulkmode
    },
    moduleDataId() {
      let { $route, dataId } = this
      if (isEmpty(dataId)) {
        let id = this.$attrs.id || $route.params.id

        return !isEmpty(id) ? Number(id) : id
      }
      return dataId
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      return isEmpty(moduleDataId)
        ? `Create ${moduleDisplayName}`
        : `Edit ${moduleDisplayName}`
    },
    isEdit() {
      let { moduleDataId } = this
      return !isEmpty(moduleDataId)
    },
    customClass() {
      return ''
    },
    queryFormId() {
      let { $route } = this
      let {
        query: { formId },
      } = $route
      if (!isEmpty(formId)) {
        return parseInt(formId)
      }
      return formId
    },
    isV3Api() {
      return false
    },
    isWidgetsSupported() {
      return true
    },
    customClassForContainer() {
      return ''
    },
  },
  created() {
    this.init()
  },
  watch: {
    selectedForm: {
      async handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.widgetsArr = []
          this.isLoading = true
          await this.loadFormData()
          this.isLoading = false
        }
      },
      deep: true,
    },
    queryFormId: {
      async handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.setInitialForm()
        }
      },
      deep: true,
    },
  },
  methods: {
    //promise rej->indicates validation fail, remove loading state
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
    },
    async init() {
      let { moduleDataId, moduleName } = this
      this.isLoading = true
      await this.loadFormsList(moduleName)
      if (!isEmpty(moduleDataId)) {
        await this.loadModuleData({
          moduleName,
          moduleDataId,
        })
      }
      this.setInitialForm()

      this.isLoading = false
    },
    async loadFormsList(moduleName) {
      let url = `/v2/forms?moduleName=${moduleName}`

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
    async loadModuleData({ moduleDataId, moduleName }) {
      let response

      if (this.isV3Api) {
        response = await API.fetchRecord(moduleName, { id: moduleDataId })
      } else {
        response = await API.get(
          `/v2/module/data/${moduleDataId}?moduleName=${moduleName}`
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
    },
    async setInitialForm() {
      let { moduleData, forms, queryFormId } = this
      let selectedForm = {}
      let formId = null
      if (!isEmpty(forms)) {
        if (!isEmpty(queryFormId)) {
          formId = queryFormId
        } else {
          formId = this.getSelectedFormId(moduleData)
        }

        if (isEmpty(formId)) {
          selectedForm = this.$getProperty(this, 'forms.0')
        } else {
          let existingForm = this.forms.find(form => formId === form.id)
          if (isEmpty(existingForm))
            selectedForm = this.$getProperty(this, 'forms.0')
          else selectedForm = existingForm
        }

        this.$set(this, 'selectedForm', selectedForm)
      }
    },
    async loadFormData(formId) {
      let formObj = {}
      let { selectedForm, moduleName, moduleData, isEdit } = this
      let { id, name, module } = selectedForm
      let { name: formModuleName } = module || {}
      moduleName = isEmpty(formModuleName) ? moduleName : formModuleName
      let formUrl =
        id === -1
          ? `/v2/forms/${moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${moduleName}?formId=${formId ||
              id}&fetchFormRuleFields=true`
      if (!isEdit) {
        formUrl = `${formUrl}&forCreate=true`
      }

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
          if (!isEmpty(moduleData)) {
            this.deserializeData(moduleData)
          }
        }
      }
      return formObj
    },
    onBlurHook() {
      // override in consuming components
    },
    onWidgetChange(widgetsArr) {
      this.$set(this, 'widgetsArr', widgetsArr)
    },
    async saveRecord(formModel) {
      let {
        formObj,
        afterSerializeHook,
        afterSaveHook,
        moduleName,
        moduleDataId,
      } = this
      let { formId } = formModel
      let response = {}
      let data = this.serializedData(formObj, formModel)
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      if (!isEmpty(moduleDataId)) {
        data.id = moduleDataId
      }
      // Hook to overwrite the serialized data, before performing crud operation
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
      // Hook to handle notification after crud operation
      this.notificationHandler(response)

      // Hook to handle response after crud operation
      if (!isEmpty(afterSaveHook) && isFunction(afterSaveHook)) {
        this.afterSaveHook(response)
      }
    },
    notificationHandler(response) {
      let { moduleDataId, moduleDisplayName } = this
      let { error } = response
      if (error) {
        let { message = 'Error occured' } = error
        this.$message.error(message)
      } else {
        let successMsg = moduleDataId
          ? `${moduleDisplayName} updated successfully`
          : `${moduleDisplayName} created successfully`
        this.$message.success(successMsg)
      }
    },
    getSelectedFormId(moduleData) {
      if (!isEmpty(moduleData)) {
        let { formId } = moduleData
        return formId
      }
    },
    modifyFieldPropsHook() {
      // Overriding field object in consuming component
    },
    handleFormModlechange() {
      //this method to handle formModel change
    },
    deserializeData(moduleData) {
      let { data } = moduleData
      let { isV3Api } = this
      let { formObj } = this
      if (!isEmpty(formObj)) {
        let { sections } = formObj
        if (!isEmpty(sections)) {
          sections.forEach(section => {
            let { fields } = section
            if (!isEmpty(fields)) {
              fields.forEach(field => {
                let {
                  field: fieldObj,
                  name,
                  displayTypeEnum,
                  displayType,
                } = field

                // Custom fields data extraction
                if (
                  !isEmpty(fieldObj) &&
                  !fieldObj.default &&
                  !isEmpty(data) &&
                  !isV3Api
                ) {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData.data[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData.data[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData.data[`${name}Id`]
                    let fileObj = { name: moduleData.data[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (displayTypeEnum === 'LOOKUP_SIMPLE') {
                    this.$set(field, 'value', (data[name] || {}).id)
                  } else if (displayTypeEnum === 'DURATION') {
                    this.$set(
                      field,
                      'value',
                      this.$helpers.getDurationInSecs(
                        data[name],
                        !isEmpty((fieldObj || {}).unit) ? fieldObj.unit : 's'
                      )
                    )
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.data.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  } else {
                    this.$set(field, 'value', data[name])
                  }
                } else if (displayTypeEnum === 'TEAMSTAFFASSIGNMENT') {
                  let { assignedTo, assignmentGroup } = moduleData
                  let fieldValue = {
                    assignedTo: {
                      id: '',
                    },
                    assignmentGroup: {
                      id: '',
                    },
                  }
                  if (!isEmpty(assignedTo)) {
                    fieldValue.assignedTo = assignedTo
                  }
                  if (!isEmpty(assignmentGroup)) {
                    fieldValue.assignmentGroup = assignmentGroup
                  }
                  this.$set(field, 'value', fieldValue)
                } else {
                  if (
                    displayTypeEnum === 'IMAGE' ||
                    displayTypeEnum === 'SIGNATURE'
                  ) {
                    let imageId = moduleData[`${name}Id`]
                    let imgUrl = `${getBaseURL()}/v2/files/preview/${
                      moduleData[`${name}Id`]
                    }`
                    this.$set(field, 'imgUrl', imageId ? imgUrl : null)
                    this.$set(field, 'value', imageId)
                  } else if (displayTypeEnum === 'FILE') {
                    let fileId = moduleData[`${name}Id`]
                    let fileObj = { name: moduleData[`${name}FileName`] }
                    this.$set(field, 'fileObj', fileId ? fileObj : null)
                    this.$set(field, 'value', fileId)
                  } else if (
                    (displayTypeEnum === 'LOOKUP_SIMPLE' ||
                      displayTypeEnum === 'REQUESTER' ||
                      displayTypeEnum === 'SPACECHOOSER') &&
                    field.name !== 'siteId'
                  ) {
                    this.$set(field, 'value', (moduleData[name] || {}).id)
                  } else if (displayType === 56) {
                    let { config } = field
                    let {
                      endFieldName,
                      startFieldName,
                      scheduleJsonName,
                    } = config
                    let scheduleValueObj = {
                      startFieldValue: moduleData[startFieldName],
                      endFieldValue: moduleData[endFieldName],
                      scheduleJsonValue: moduleData[scheduleJsonName],
                      isRecurring: moduleData.isRecurring,
                    }
                    this.$set(field, 'scheduleValueObj', scheduleValueObj)
                  } else if (
                    displayTypeEnum === 'SADDRESS' ||
                    displayTypeEnum === 'ADDRESS'
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(
                        this,
                        `moduleData.${field.name}`,
                        deepCloneObject(Constants.ADDRESS_FIELD_DEFAULTS)
                      )
                    )
                  } else if (displayTypeEnum === 'QUOTE_ADDRESS') {
                    this.$set(
                      field,
                      'billToAddress',
                      this.$getProperty(this, 'moduleData.billToAddress', {})
                    )
                    this.$set(
                      field,
                      'shipToAddress',
                      this.$getProperty(this, 'moduleData.shipToAddress', {})
                    )
                  }
                  else if(displayTypeEnum === 'EVENT_CONFIGURATION'){
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.${name}`, {})
                    )
                  }
                   else if (
                    ['QUOTE_LINE_ITEMS', 'LINEITEMS'].includes(displayTypeEnum)
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.${name}`, null)
                    )
                    Constants.QUOTE_LINE_ITEMS_ADDITIONAL_FIELDS.forEach(
                      additionalField => {
                        let value = this.$getProperty(
                          this,
                          `moduleData.${additionalField}`,
                          null
                        )
                        if (additionalField === 'tax') {
                          if (isEmpty(value)) {
                            value = { id: null }
                          } else {
                            value = pick(value, ['id'])
                          }
                        }
                        this.$set(field, additionalField, value)
                      }
                    )
                  } else if (
                    ['INVREQUEST_LINE_ITEMS'].includes(displayTypeEnum)
                  ) {
                    this.$set(
                      field,
                      'value',
                      this.$getProperty(this, `moduleData.lineItems`, null) ||
                        this.$getProperty(
                          this,
                          `moduleData.inventoryrequestlineitems`,
                          null
                        )
                    )
                  } else if (displayTypeEnum === 'BUDGET_AMOUNT') {
                    this.$set(
                      field,
                      'value',
                      this.getFormattedBudgetAmountsData(moduleData)
                    )
                  } else if (displayTypeEnum === 'FACILITY_BOOKING_SLOTS') {
                    this.$set(
                      field,
                      'value',
                      this.getFormattedSlotData(moduleData)
                    )
                    this.$set(field, 'bookingDate', moduleData.bookingDate)
                  } else if (displayTypeEnum === 'FACILITY_AVAILABILITY') {
                    this.$set(
                      field,
                      'value',
                      this.formatForFacilityAvailability(moduleData)
                    )
                  } else if (displayTypeEnum === 'NOTES') {
                    this.$set(field, 'value', field.value)
                  } else {
                    if (
                      ['NUMBER', 'DECIMAL'].includes(displayTypeEnum) &&
                      !isNullOrUndefined(moduleData[name])
                    ) {
                      this.$set(field, 'value', moduleData[name])
                    } else if (isEmpty(moduleData[name])) {
                      this.$set(field, 'value', null)
                    } else {
                      this.$set(field, 'value', moduleData[name])
                    }
                  }
                }
              })
            }
          })
        }
      }
    },
  },
}
</script>

<style lang="scss">
.form-empty-container {
  height: calc(100vh - 150px);
  margin-top: 60px;
  background-color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  border: 1px solid #ebedf4;
}
.module-view-empty-state svg.icon {
  width: 150px;
  height: 120px;
}
.no-template-text {
  color: #6fa5f2;
  font-weight: bold;
}
.contact-admin-text {
  color: #6fa5f2;
}
</style>
