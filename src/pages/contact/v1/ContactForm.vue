<template>
  <div>
    <div v-if="visibility">
      <el-dialog
        :visible.sync="visibility"
        :before-close="closeForm"
        :append-to-body="true"
        custom-class="fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog assetaddvaluedialog fc-web-form-dialog fc-item-type-summary-dialog"
      >
        <facilio-web-form
          :editObj="editData"
          :emitForm="emitForm"
          name="contactForm"
          @failed=";(saving = false), (emitForm = false)"
          @validated="data => submitForm(data)"
          :reset.sync="resetForm"
          class="facilio-inventory-web-form-body"
        ></facilio-web-form>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-cancel" @click="closeForm"
            >CANCEL</el-button
          >
          <el-button
            class="modal-btn-save"
            type="primary"
            @click="emitFormData()"
            :loading="saving"
            >{{ saving ? 'Submitting...' : 'SAVE' }}</el-button
          >
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import FacilioWebForm from '@/FacilioWebForm'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['visibility', 'editData', 'moduleName'],
  components: {
    FacilioWebForm,
  },
  data() {
    return {
      emitForm: false,
      loading: true,
      resetForm: false,
      saving: false,
    }
  },
  methods: {
    emitFormData() {
      this.emitForm = true
    },
    submitForm(data) {
      let url = `v2/contacts/`
      let message
      if (this.editData) {
        data.id = this.editData.id
        url += 'update'
        data['requester'] = this.editData.requester
        if (isEmpty(data.vendor.id)) {
          data.vendor.id = -99
        }
        if (isEmpty(data.tenant.id)) {
          data.tenant.id = -99
        }
        message = `Contact Edited Successfully`
      } else {
        url += 'add'
        message = `Contact Added Successfully`
      }
      let param = {
        contacts: [data],
      }
      this.saving = true
      API.post(url, param).then(({ error }) => {
        if (error) {
          this.$message.error(error.message || 'Error Occurred')
          this.isFormSaved = false
        } else {
          this.resetForm = false
          this.$message.success(message)
          this.$emit('saved')
          this.$emit('update:visibility', false)
        }
        this.saving = false
      })
      this.emitForm = false
    },
    closeForm() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
