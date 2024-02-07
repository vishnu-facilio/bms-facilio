<template>
  <div>
    <Spinner
      v-if="isLoading || isFormDataLoading"
      size="80"
      :show="true"
    ></Spinner>
    <f-webform
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :isSaving="isSaving"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="false"
      formLabelPosition="top"
      :isEdit="false"
      :moduleDataId="moduleDataId"
      :removeDefaultStyling="true"
      :isV3Api="isV3Api"
      @save="submitForm"
      @cancel="closeAction"
    ></f-webform>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

const urlHash = {
  workorder: id => `workorder/summary/${id}`,
  asset: id => `asset/summary/${id}?fetchHierarchy=true`,
}

export default {
  extends: FormCreation,
  components: { FWebform },
  props: [
    'moduleName',
    'recordId',
    'formId',
    'closeAction',
    'saveAction',
    'btnLabel',
    'isV3',
  ],
  data() {
    return {
      isSaving: false,
      selectedForm: {
        id: this.formId,
      },
      isFormDataLoading: false,
    }
  },
  created() {
    if (this.formId) {
      this.loadModuleData().then(() => {
        this.isLoading = true
        this.loadFormData()
          .then(() => {
            this.$nextTick(() => {
              this.formObj.primaryBtnLabel = this.btnLabel
            })
          })
          .finally(() => {
            this.isLoading = false
          })
      })
    }
  },
  computed: {
    moduleDataId() {
      return this.recordId
    },
    isV3Api() {
      return this.isV3
    },
  },
  methods: {
    init() {
      /* overriden to prevent default code in formcreation*/
    },

    async loadModuleData() {
      if (this.isV3) {
        this.isFormDataLoading = true

        let { moduleName, recordId } = this

        let { error, [moduleName]: record } = await API.fetchRecord(
          moduleName,
          { id: recordId }
        )

        if (!error) {
          this.$set(this, 'moduleData', record)
        }
        this.isFormDataLoading = false
      } else {
        const modueErrorFn = () => {
          console.warn(
            `No moduleUrlFn defined for the module: ${this.moduleName}`
          )
        }
        const moduleUrlFn = urlHash[this.moduleName] || modueErrorFn

        let url = moduleUrlFn(this.recordId, this.moduleName)

        if (!isEmpty(url)) {
          this.isFormDataLoading = true

          let { error, data } = await API.get(url)

          if (!error && data) {
            let record = data[this.moduleName]
            this.$set(this, 'moduleData', record)
          }

          this.isFormDataLoading = false
        } else {
          return Promise.resolve()
        }
      }
    },

    submitForm(data) {
      let { formObj } = this
      this.isSaving = true
      let serializedData = this.serializedData(formObj, data)

      this.saveAction(serializedData)
        .then(() => {
          this.isSaving = false
        })
        .catch(() => {
          this.isSaving = false
          this.closeAction()
        })
    },
  },
}
</script>
