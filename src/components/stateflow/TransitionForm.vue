<template>
  <el-dialog
    :append-to-body="true"
    :visible="true"
    class="fc-dialog-header-hide wo-state-transition-dialog-center"
    custom-class="wo-web-form"
    :width="hasMultiColumnElements ? '70%' : '35%'"
    style="z-index: 999999"
  >
    <Spinner
      v-if="isLoading || isFormDataLoading"
      size="80"
      :show="true"
    ></Spinner>
    <template v-else>
      <div class="header pL30 pB20 pT10 d-flex bold">
        <div class="title mT10">{{ formObj.displayName }}</div>
      </div>
      <f-webform
        :form.sync="formObj"
        :module="moduleName"
        :isSaving="isSaving"
        :isV3Api="isV3Api"
        :canShowNotifyRequester="canShowNotifyRequester"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        :isEdit="true"
        :isEditForm="true"
        :moduleData="moduleData"
        :moduleDataId="moduleDataId"
        :modifyFieldPropsHook="modifyFieldPropsHook"
        formLabelPosition="top"
        @save="submitForm"
        @cancel="closeAction"
      ></f-webform>
    </template>
  </el-dialog>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { ACTION_TYPES } from 'pages/setup/custombutton/CustomButtonUtil'

export default {
  extends: FormCreation,
  components: { FWebform },
  props: [
    'moduleName',
    'recordId',
    'transition',
    'record',
    'formId',
    'closeAction',
    'saveAction',
    'isExternalModule',
    'isV3',
    'actionType',
    'isFormOfCustomButton',
  ],
  data() {
    return {
      isSaving: false,
      selectedForm: {
        id: this.formId,
      },
      isFormDataLoading: false,
      hasMultiColumnElements: false,
      isLoading: true,
    }
  },
  async created() {
    if (this.formId) {
      await this.loadModuleData()
      await this.loadFormData()
    }
  },
  computed: {
    moduleDataId() {
      return this.recordId
    },
    canShowNotifyRequester() {
      let { moduleName, record } = this

      if (moduleName === 'workorder') {
        return !isEmpty((record || {}).requester)
      }
      return false
    },
    isV3Api() {
      return this.isV3
    },
  },
  methods: {
    init() {
      // override to prevent default code in formcreation
    },
    async loadFormData() {
      let { selectedForm, moduleName, moduleData, transition } = this
      let { formModuleName } = transition
      let { id, name } = selectedForm
      let formObj = {}

      let _moduleName = this.isExternalModule ? formModuleName : moduleName
      let formUrl =
        id === -1
          ? `/v2/forms/${_moduleName}?formName=${name}&fetchFormRuleFields=true`
          : `/v2/forms/${_moduleName}?formId=${id}&fetchFormRuleFields=true`

      this.isLoading = true

      let { error, data } = await API.get(formUrl)

      if (error) {
        this.$message.error(error.message)
      } else {
        let { form } = data
        if (!isEmpty(form)) {
          formObj = form

          this.$set(this, 'formObj', formObj)
          this.setFormLabels()

          if (form.sections) {
            // Check if there are any subforms
            // TODO: Also handle for cases where fields with
            // span !== 1..ie multiple fields in the same row
            let { sections } = form
            this.hasMultiColumnElements = sections.some(
              section => section.subFormId > 0
            )
          }

          if (!this.isExternalModule && !isEmpty(moduleData)) {
            this.deserializeData(moduleData)
          }
        }
      }

      this.isLoading = false
      return formObj
    },
    setFormLabels() {
      this.formObj.primaryBtnLabel = this.$t('common.roles.save')
      this.formObj.secondaryBtnLabel = this.$t('common._common.cancel')
    },
    loadModuleData() {
      // Keeping this async in case we need to load data from api here
      if (!isEmpty(this.record)) {
        return new Promise(resolve => {
          this.$set(this, 'moduleData', this.record)
          resolve()
        })
      } else {
        return Promise.resolve()
      }
    },
    modifyFieldPropsHook(field) {
      let {
        moduleName,
        recordId,
        isFormOfCustomButton,
        actionType,
        isExternalModule,
      } = this
      let { displayTypeEnum, lookupModuleName } = field || {}
      let isRelatedModuleField =
        displayTypeEnum === 'LOOKUP_SIMPLE' && lookupModuleName === moduleName
      let isDisabled = isFormOfCustomButton
        ? actionType === ACTION_TYPES.CREATE_RECORD
        : isExternalModule
      if (isRelatedModuleField) {
        return { ...field, isDisabled, value: recordId }
      }
    },
    submitForm(data) {
      this.isSaving = true

      let { formObj, formId } = this
      let { subFormFiles } = data
      data = this.serializedData(formObj, data)

      if (subFormFiles) data = { ...data, subFormFiles }

      this.saveAction(data, formId)
        .catch(() => {
          this.closeAction()
        })
        .finally(() => {
          this.isSaving = false
        })
    },
  },
}
</script>
