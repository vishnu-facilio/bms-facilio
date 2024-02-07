<template>
  <el-dialog
    :append-to-body="true"
    :visible.sync="visibility"
    v-if="visibility"
    :fullscreen="true"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog fc-web-form-dialog fc-item-type-summary-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div class="facilio-inventory-web-form-body">
      <error-banner
        :error.sync="error"
        :errorMessage.sync="errorText"
      ></error-banner>
      <facilio-web-form
        :editObj="labourEditData"
        :reset.sync="resetForm"
        :model.sync="labourEditData"
        :emitForm="emitForm"
        :name="'labourForm'"
        @validated="data => save(data)"
        @failed=";(saving = false), (emitForm = false)"
        @loaded="loading = false"
        class="facilio-web-form-body"
      ></facilio-web-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel">{{
        $t('common._common.cancel')
      }}</el-button>
      <el-button
        type="primary"
        @click="saveForm()"
        :loading="saving"
        class="modal-btn-save"
        >{{
          saving ? $t('common._common._saving') : $t('common._common._save')
        }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import FacilioWebForm from '@/FacilioWebForm'
import { API } from '@facilio/api'
export default {
  props: ['visibility', 'labour', 'source', 'isNew'],
  data() {
    return {
      emitForm: false,
      saving: false,
      dialogVisible: false,
      error: false,
      resetForm: false,
      errorText: '',
      labourId: null,
      labourEditData: null,
    }
  },
  components: {
    ErrorBanner,
    FacilioWebForm,
  },
  mounted: function() {
    if (!this.isNew) {
      this.labourId = this.labour.id
      this.labourEditData = this.$helpers.cloneObject(this.labour)
      if (Number(this.labourEditData.siteId) === -1) {
        this.labourEditData.siteId = null
      }
    }
  },
  methods: {
    async save(labour) {
      if (!labour.email) {
        delete labour.email
      }
      if (!labour.phone) {
        delete labour.phone
      }
      if (!labour.cost) {
        delete labour.cost
      }
      if (!labour.user || !labour.user.id) {
        delete labour.user
      }
      if (labour.siteId === '') {
        delete labour.siteId
      }
      let { error } = this.isNew
        ? await API.createRecord('labour', {
            data: labour,
          })
        : await API.updateRecord('labour', {
            id: this.labourId,
            data: labour,
          })
      let messages = {
        add: {
          success: this.$t('common._common.labor_added_successfully'),
          error: this.$t('common.wo_report.unable_to_add_record'),
        },
        update: {
          success: this.$t('common._common.labor_updated_succcessfully'),
          error: this.$t('common._common.labor_updated_succcessfully'),
        },
      }
      let msg = this.isNew ? messages['add'] : messages['update']
      if (!error) {
        this.$message.success(msg.success)
        this.isFormSaved = true
      } else {
        this.$message.error(msg.error)
      }
      this.resetForm = true
      this.$emit('saved')
      this.emitForm = false
    },
    initaSave() {
      this.emitForm = true
    },
    saveForm() {
      this.emitForm = true
    },
    closeDialog() {
      this.$emit('close')
    },
  },
}
</script>
<style>
.styled-select select {
  background: transparent;
  border: none;
  font-size: 14px;
  height: 29px;
  padding: 5px;
  width: 100%;
}
.rounded {
  -webkit-border-radius: 20px;
  -moz-border-radius: 20px;
  border-radius: 20px;
}
.green {
  background-color: #f2fbeb;
}
</style>
