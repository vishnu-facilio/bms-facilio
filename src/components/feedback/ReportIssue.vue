<template>
  <el-dialog
    title="Report a problem"
    :visible="true"
    width="40%"
    style="z-index: 9999999999;"
    class="report-issue-dialog"
    :append-to-body="true"
    :before-close="onClose"
  >
    <el-form
      :model="issue"
      :rules="rules"
      ref="report_issue_form"
      :label-position="'top'"
    >
      <el-row class="mB10">
        <el-col :span="24">
          <el-form-item prop="message" class="mB10">
            <p class="fc-input-label-txt">
              Tell us what happened and our team will get to it in no time.
            </p>
            <el-input
              type="textarea"
              :autofocus="true"
              v-model="issue.message"
              :autosize="{ minRows: 6, maxRows: 4 }"
              placeholder="I clicked on 'X', then hit 'Configure'"
              class="fc-input-full-border-textarea"
              resize="none"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="d-flex mT-auto form-action-btn">
      <el-button
        class="form-btn f13 bold secondary text-center text-uppercase"
        @click="onClose()"
        >Cancel</el-button
      >
      <el-button
        type="primary"
        class="form-btn f13 bold primary m0 text-center text-uppercase"
        @click="sendToSentry()"
        >Send</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['onClose'],
  data() {
    return {
      issue: {
        message: null,
      },
      rules: {
        message: {
          trigger: 'change',
          validator: function(rule, value, callback) {
            if (isEmpty(value)) {
              callback(new Error('This field is required'))
            } else callback()
          },
        },
      },
    }
  },
  computed: {
    preparedMessage() {
      return '[CX REPORTED]: ' + this.issue.message
    },
  },
  methods: {
    sendToSentry() {
      this.$refs['report_issue_form'].validate().then(isValid => {
        if (isValid) {
          /*
            TODO
            send issue report
          */

          this.$nextTick(() => this.onClose())
        }
      })
    },
  },
}
</script>
<style lang="scss">
.report-issue-dialog {
  .el-dialog__body {
    padding: 0;
  }
  .el-form {
    padding: 10px 20px 20px;
  }
}
</style>
