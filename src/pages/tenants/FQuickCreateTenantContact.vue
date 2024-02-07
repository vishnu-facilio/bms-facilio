<template>
  <div>
    <el-dialog
      :visible.sync="dialogVisibility"
      :append-to-body="true"
      :title="displayName"
      :before-close="closeDialog"
      custom-class="fc-dialog-center-container f-webform-right-dialog"
    >
      <div v-if="isLoading" class="loading-container d-flex">
        <Spinner :show="isLoading"></Spinner>
      </div>
      <FWebform
        v-else
        :form.sync="formObj"
        :module="moduleName"
        :moduleDisplayName="displayName"
        :isSaving="isSaving"
        :canShowPrimaryBtn="true"
        :canShowSecondaryBtn="true"
        @save="saveRecord"
        @cancel="closeDialog"
      ></FWebform>
    </el-dialog>
  </div>
</template>
<script>
import FormCreation from '@/base/FormCreation'
import FWebform from '@/FWebform'
import { API } from '@facilio/api'

export default {
  name: 'QuickCreateForm',
  extends: FormCreation,
  components: {
    FWebform,
  },
  props: ['dialogVisibility', 'moduleName', 'parentId'],
  data() {
    return {
      isSaving: false,
    }
  },
  computed: {
    displayName() {
      return (this.formObj || {}).displayName
    },
  },
  methods: {
    async init() {
      let { moduleName } = this
      this.isLoading = true
      await this.loadFormsList(moduleName)
      await this.loadModuleData()
      this.setInitialForm()
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
    loadModuleData() {
      let record = {}
      record['tenant'] = { id: this.parentId }
      this.$set(this, 'moduleData', record)
    },
    saveRecord(formModel) {
      // Have to Handle Module Req Url, body ,result according to Module
      let { moduleName, formObj } = this
      let url = ''
      let formRecord = [this.serializedData(formObj, formModel)]
      let formData = new FormData()
      let urlKey = moduleName
      let key = 'tenantContacts'

      this.$helpers.setFormData(key, formRecord, formData)

      url = `/v2/${urlKey}/add`

      this.isSaving = true
      let promise = this.$http
        .post(url, formData)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let addedRecord = result[`${moduleName}s`]
            this.$message.success('Added successfully')
            this.$emit('saved', addedRecord[0])
            this.closeDialog()
          } else {
            throw new Error(message)
          }
        })
        .catch(({ message }) => {
          this.$message.error(message)
        })
      Promise.all([promise]).finally(() => (this.isSaving = false))
    },
    closeDialog() {
      this.$emit('update:dialogVisibility', false)
    },
  },
}
</script>
<style scoped lang="scss">
.f-webform-right-dialog {
  .f-webform-container {
    max-height: 600px;
  }
}
</style>
