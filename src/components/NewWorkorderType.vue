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
      :model="newtype"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <div id="newworkordercategory">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? 'New Maintenance Type' : 'Edit Maintenance Type' }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <el-row>
            <el-col :span="24" :gutter="20">
              <p class="fc-input-label-txt">Type</p>
              <el-form-item prop="name">
                <el-input
                  class="fc-input-full-border-select2 width100"
                  v-model="newtype.name"
                  type="text"
                  placeholder="Enter Category"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row>
            <el-col :span="24" :gutter="20">
              <p class="fc-input-label-txt">Description</p>
              <el-form-item prop="description">
                <el-input
                  v-model="newtype.description"
                  :placeholder="$t('common._common.enter_desc')"
                  type="textarea"
                  :autosize="{ minRows: 4, maxRows: 6 }"
                  class="fc-input-full-border-select2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveType('ruleForm')"
          class="modal-btn-save"
          >Save</el-button
        >
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
export default {
  props: ['type', 'isNew', 'visibility'],
  data() {
    return {
      error: false,
      newtype: {
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
    this.$store.dispatch('loadTicketType')
  },
  mounted() {
    this.initType()
  },
  methods: {
    initType() {
      if (this.isNew) {
        this.newtype.name = ''
        this.newtype.description = ''
      } else {
        this.newtype = this.type
      }
    },
    saveType(ruleForm) {
      let self = this
      this.$refs[ruleForm].validate(valid => {
        if (!valid) {
          this.error = true
          return false
        }
      })
      // self.saving = true

      if (!this.isNew && this.type.id > 0) {
        let ticketType = {
          name: self.newtype.name,
          description: self.newtype.description,
          id: self.type.id,
        }
        self.$http
          .post('/picklist/updateTicketType', { ticketType })
          .then(function(response) {
            self.saving = false
            self.$emit('update:visibility', false)
            self.$emit('reload', response.data)
            self.$dialog.notify('Workorder type updated successfully!')
          })
      } else {
        let ticketType = {
          name: self.newtype.name,
          description: self.newtype.description,
        }
        self.$http
          .post('/picklist/addTicketType', { ticketType })
          .then(function(response) {
            self.$store.commit('GENERIC_ADD_OR_UPDATE', {
              type: 'ticketType',
              data: response.data,
              matches: ['name', response.data.name],
            })
            self.saving = false
            self.$emit('update:visibility', false)
            self.$dialog.notify('Workorder type added successfully!')
          })
      }
    },
    cancel() {
      this.$emit('canceled')
    },
    closeDialog() {
      this.$emit('update:visibility', false)
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
/* .new-body-modal .fc-form input:not(.q-input-target):not(.el-input__inner):not(.el-select__input):not(.btn), .fc-form textarea:not(.q-input-target):not(.el-textarea__inner):not(.el-input__inner):not(.el-select__input), .fc-form .fselect{
    width: 300px;
  } */
.new-body-modal .check-required {
  display: block;
}
.new-body-modal .select-height {
  height: 40px;
}
.v-modal {
  z-index: 101 !important;
}
#newworkordercategory .el-textarea .el-textarea__inner {
  /* min-height: 50px !important; */
  /* width: 350px; */
  resize: none;
}
</style>
