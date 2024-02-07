<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner :error="error"></error-banner>
    <el-form
      :model="newcategory"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <div id="newworkordercategory">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? 'New Workorder Category' : 'Edit Workorder Category' }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-row>
            <el-col :span="24" :gutter="20">
              <p class="fc-input-label-txt">Category</p>
              <el-form-item prop="name">
                <el-input
                  class="fc-input-full-border2 width100"
                  v-model="newcategory.name"
                  type="text"
                  placeholder="Enter Category"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <p class="fc-input-label-txt">Description</p>
            <el-form-item prop="description">
              <el-input
                v-model="newcategory.description"
                :placeholder="$t('common._common.enter_desc')"
                type="textarea"
                :autosize="{ minRows: 3, maxRows: 4 }"
                resize="none"
                class="fc-input-full-border-select2 width100"
              ></el-input>
            </el-form-item>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveCategory('ruleForm')"
          :loading="saving"
          class="modal-btn-save"
          >{{ saving ? 'Saving...' : 'Save' }}</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import { API } from '@facilio/api'

export default {
  props: ['category', 'isNew', 'visibility'],
  data() {
    return {
      error: false,
      saving: false,
      newcategory: {
        name: '',
        description: '',
      },
      rules: {
        name: [{ required: true, message: ' ', trigger: 'blur' }],
      },
    }
  },
  components: {
    ErrorBanner,
  },
  created() {
    this.$store.dispatch('loadTicketCategory')
    !this.isNew ? (this.newcategory = this.category) : null
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    saveCategory(ruleForm) {
      this.$refs[ruleForm].validate(valid => {
        if (!valid) {
          this.error = true
          return
        } else {
          this.saving = true

          let { name, description, id } = this.newcategory
          let ticketCategory = { name, description }
          let url = ''
          let notification = ''

          if (this.isNew) {
            url = '/picklist/addTicketCategory'
            notification = 'Workorder category added successfully!'
          } else {
            ticketCategory = { ...ticketCategory, id }
            url = '/picklist/updateTicketCategory'
            notification = 'Successfully updated the Category'
          }

          API.post(url, { ticketCategory }).then(({ data, error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.$emit('update:visibility', false)
              this.$store.commit('GENERIC_ADD_OR_UPDATE', {
                type: 'ticketCategory',
                data,
                matches: ['name', data.name],
              })
              this.$dialog.notify(notification)
            }

            this.saving = false
          })
        }
      })
    },
  },
}
</script>
<style>
.field-hint {
  padding: 8px 0;
  font-size: 12px;
  border-radius: 2px;
}
.custom-field-modal .fc-dialog-form .el-dialog__header {
  padding: 0 !important;
}
.custom-field-modal .el-dialog__header {
  padding: 0 !important;
}
.new-body-modal .check-required {
  display: block;
}
.new-body-modal .select-height {
  height: 40px;
}
.v-modal {
  z-index: 101 !important;
}
</style>
