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
    moduleDataId() {
      return -1
    },
  },
  methods: {
    saveRecord(formModel) {
      // Have to Handle Module Req Url, body ,result according to Module
      let { moduleName, formObj } = this
      let url = ''
      let formRecord =
        moduleName === 'custom_tenantutility'
          ? this.serializedData(formObj, formModel)
          : [this.serializedData(formObj, formModel)]
      let formData = new FormData()
      let urlKey = moduleName
      let key =
        moduleName === 'vendorDocuments'
          ? 'documents'
          : moduleName === 'tenantcontact'
          ? 'tenantContacts'
          : `${moduleName}s`
      if (moduleName === 'vendorcontact') {
        key = 'vendorContacts'
      } else if (moduleName === 'clientcontact') {
        key = 'clientContacts'
      } else if (moduleName === 'custom_tenantutility') {
        key = 'moduleData'
      }
      this.$helpers.setFormData(key, formRecord, formData)
      if (moduleName === 'contact') {
        urlKey = 'contacts'
      } else if (moduleName === 'custom_tenantutility') {
        urlKey = 'moduleData'
      }
      if (moduleName === 'vendorDocuments') {
        formData.append('parentAttachmentModuleName', moduleName)
        formData.append('documents[0].parentId.id', this.parentId)
        urlKey = 'documents'
      } else if (moduleName === 'contact') {
        formData.append('contacts[0].vendor.id', this.parentId)
        formData.append('contacts[0].contactType', 2)
      } else if (moduleName === 'insurance') {
        formData.append('insurances[0].vendor.id', this.parentId)
      } else if (moduleName === 'tenantcontact') {
        formData.delete('tenantContacts[0].tenant.id')
        formData.append('tenantContacts[0].tenant.id', this.parentId)
      } else if (moduleName === 'vendorcontact') {
        formData.delete('vendorContacts[0].vendor.id')
        formData.append('vendorContacts[0].vendor.id', this.parentId)
      } else if (moduleName === 'clientcontact') {
        formData.delete('clientContacts[0].client.id')
        formData.append('clientContacts[0].client.id', this.parentId)
      }

      url = `/v2/${urlKey}/add`
      if (moduleName === 'custom_tenantutility') {
        url = 'v2/module/data/add'
        formData.append('moduleName', 'custom_tenantutility')
        formData.append('withLocalId', false)
      }
      this.isSaving = true
      let promise = this.$http
        .post(url, formData)
        .then(({ data: { message, responseCode, result } }) => {
          if (responseCode === 0) {
            let addedRecord =
              result[
                moduleName === 'vendorDocuments' ||
                moduleName === 'custom_tenantutility'
                  ? urlKey
                  : `${moduleName}s`
              ]
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
