<template>
  <el-dialog
    :visible="true"
    :before-close="closeDialog"
    :append-to-body="true"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog f-webform-right-dialog"
  >
    <div class="fc-pm-main-content-H">Visit</div>
    <div v-if="isLoading" class="loading-container d-flex">
      <Spinner :show="isLoading"></Spinner>
    </div>
    <f-webform
      v-else
      :form.sync="formObj"
      :module="moduleName"
      :moduleData="moduleData"
      :moduleDataId="moduleDataId"
      :moduleDisplayName="moduleDisplayName"
      :isSaving="isSaving"
      :isV3Api="isV3Api"
      :isEdit="isEdit"
      :canShowPrimaryBtn="true"
      :canShowSecondaryBtn="true"
      @onBlur="onBlurHook"
      @save="saveRecord"
      @cancel="closeDialog"
    ></f-webform>
  </el-dialog>
</template>
<script>
import { isEmpty, isArray } from '@facilio/utils/validation'
import FWebform from '@/FWebform'
import FormCreation from '@/base/FormCreation'
import { API } from '@facilio/api'

export default {
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: [
    'formVisibility',
    'formData',
    'visitorTypeId',
    'recordData',
    'moduleName',
    'requestedBy',
  ],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return 'Visit'
    },
    moduleDataId() {
      let { moduleData } = this
      let { id } = moduleData || {}
      return id || null
    },
    isV3Api() {
      return true
    },
  },
  methods: {
    init() {
      if (this.formData) {
        this.formObj = this.formData
        this.selectedForm = this.formData
      }
      if (this.recordData) {
        this.moduleData = this.recordData
      }
    },
    async onBlurHook({ field, value: phoneNumber, formModel }) {
      if (
        field.name === 'visitorPhone' &&
        isEmpty(formModel['visitorName']) &&
        isEmpty(formModel['visitorEmail'])
      ) {
        let filters = {
          phone: {
            operatorId: 3,
            value: [phoneNumber + ''],
          },
        }

        let params = {
          viewname: 'all',
          filters: JSON.stringify(filters),
        }
        let { list, error } = await API.fetchAll('visitor', params)
        if (!error) {
          if (!isEmpty(list) && isArray(list)) {
            let [visitor] = list
            if (!isEmpty(visitor)) {
              this.$set(formModel, 'visitorName', visitor.name)
              this.$set(formModel, 'visitorEmail', visitor.email)
            }
          }
        } else {
          if (error.message) this.$message.error(error.message)
        }
      }
    },
    afterSerializeHook({ data }) {
      let { visitorTypeId: id, moduleName, requestedBy } = this

      data = { ...data, visitorType: { id } }

      if (moduleName === 'invitevisitor') {
        data = { ...data, hasCheckedIn: true }

        if (!isEmpty(requestedBy)) {
          data = { ...data, requestedBy }
        }
      }

      return data
    },
    afterSaveHook({ error }) {
      if (!error) {
        this.$emit('saved')
      }
    },
    closeDialog() {
      this.$emit('closeForm')
    },
  },
}
</script>
