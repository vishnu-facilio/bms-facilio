<template>
  <el-dialog
    :visible.sync="visibility"
    :append-to-body="true"
    @close="close"
    custom-class="fc-dialog-center-container fc-quick-add-dialog fc-web-form-dialog"
  >
    <div v-if="params">
      <facilio-web-form
        :emitForm="emitForm"
        :name="params.formName"
        @failed="saving = false"
        @validated="data => submitForm(data)"
        :reset.sync="resetForm"
        class="facilio-web-form-body"
      >
      </facilio-web-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="cancelForm()"
        >CANCEL</el-button
      >
      <el-button
        class="modal-btn-save"
        type="primary"
        @click="saveForm()"
        :loading="saving"
        >{{ saving ? 'Adding...' : 'ADD' }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import FacilioWebForm from '@/FacilioWebForm'
export default {
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      params: null,
      visibility: false,
      resetForm: false,
      emitForm: false,
      saving: false,
    }
  },
  methods: {
    open(params) {
      this.params = params
      this.visibility = true
    },
    close() {
      this.visibility = false
      this.params = null
    },
    cancelForm() {
      this.resetForm = true
      this.visibility = false
      if (this.params.onCancel) {
        this.params.onCancel()
      }
    },
    submitForm(data) {
      let self = this
      if (this.params.moduleName === 'inventoryCategory') {
        if (data.displayName !== undefined) {
          data.name = data.displayName
        }
      }
      let params
      let url
      if (this.params.formName === 'porequesterForm') {
        params = {
          user: data,
        }
        this.$http
          .post('/setup/inviterequester', params)
          .then(function(response) {
            JSON.stringify(response)
            if (response.status === 200) {
              self.$message.success('Record added successfully.')
              self.visibility = false
              self.resetForm = true
              if (self.params.onAdd) {
                self.params.onAdd({
                  moduleData: {
                    displayName: data.name,
                    id: response.data.userId,
                  },
                })
              }
            } else {
              alert(JSON.stringify(response))
            }
          })
          .catch(function(error) {
            self.saving = false
            if (error.response.data.fieldErrors.email[0] != null) {
              self.$message.error(error.response.data.fieldErrors.email[0])
            }
          })
      } else {
        url = '/v2/module/data/add'
        params = {
          moduleName: this.params.moduleName,
          dataString: JSON.stringify(data),
          withLocalId: false,
        }
        self.saving = true
        self.$http
          .post(url, params)
          .then(response => {
            self.saving = false
            if (response.data.responseCode !== 0) {
              this.$message.error(response.data.message)
            } else {
              if (
                self.$account.data[self.params.moduleName] &&
                Array.isArray(self.$account.data[self.params.moduleName])
              ) {
                self.$account.data[self.params.moduleName].push(
                  response.data.result.moduleData
                )
              }
              self.$message.success('Record added successfully.')
              self.visibility = false
              self.resetForm = true
              if (self.params.onAdd) {
                self.params.onAdd(response.data.result)
              }
            }
          })
          .catch(() => {
            self.saving = false
            self.$message.error('Unable to add record.')
          })
      }
      self.emitForm = false
    },
    saveForm() {
      this.emitForm = true
    },
  },
}
</script>

<style>
.fc-quick-add-dialog .el-dialog__header {
  display: none;
}
.fc-quick-add-dialog .el-dialog__body {
  padding: 0px;
  height: 500px;
  overflow: scroll;
  padding-bottom: 50px;
}
.fc-quick-add-dialog .width500px {
  width: 100% !important;
}
</style>
