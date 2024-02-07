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
      :model="newseverity"
      :rules="rules"
      :label-position="'top'"
      ref="ruleForm"
    >
      <div id="newworkordercategory">
        <!-- header -->
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ !isNew ? 'Edit Alarm Severity' : 'New Alarm Severity' }}
            </div>
          </div>
        </div>

        <div class="new-body-modal">
          <div class="setup-input-block">
            <p class="fc-input-label-txt">Name</p>
            <el-form-item prop="name">
              <el-input
                v-model="newseverity.name"
                type="text"
                placeholder="Enter Category"
                class="fc-input-txt fc-name-input"
              ></el-input>
            </el-form-item>
          </div>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel"
          >Cancel</el-button
        >
        <el-button
          type="primary"
          @click="saveSeverity('ruleForm')"
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
  props: ['severity', 'isNew', 'visibility'],
  data() {
    return {
      error: false,
      newseverity: {
        name: '',
      },
      rules: {
        name: [{ required: true, message: ' ', trigger: 'blur' }],
      },
    }
  },
  components: {
    ErrorBanner,
  },
  mounted() {
    this.initSeverity()
  },
  created() {
    this.$store.dispatch('loadAlarmSeverity')
  },
  title() {
    'Alarm Severity'
  },
  watch: {
    isNew: function() {
      this.initSeverity()
    },
  },
  methods: {
    initSeverity() {
      if (this.isNew) {
        this.newseverity.name = ''
      } else {
        this.newseverity.name = this.severity ? this.severity.severity : ''
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    saveSeverity(ruleForm) {
      let self = this
      this.$refs[ruleForm].validate(valid => {
        if (!valid) {
          this.error = true
          return false
        }
      })
      let data = { severity: self.newseverity.name }
      self.$http
        .post('/picklist/addAlarmSeverity', { alarmSeverity: data })
        .then(function() {
          self.$store.commit('GENERIC_ADD_OR_UPDATE', {
            type: 'alarmSeverity',
            data: {
              severity: self.newseverity.name,
              id: self.$store.state.alarmSeverity.length + 1,
            },
          })
          self.$emit('onsave')
        })
    },
    cancel() {
      this.$emit('canceled')
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
  min-height: 50px !important;
  width: 350px;
  resize: none;
}
</style>
