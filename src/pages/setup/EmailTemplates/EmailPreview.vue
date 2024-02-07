<template>
  <div>
    <el-dialog
      :visible="true"
      width="80%"
      class="pB15 fc-dialog-center-container fc-dialog-center-body-p0 fc-email-preview-dialog"
      :title="$t('setup.emailTemplates.test_email')"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <div>
        <div class="email-preview-container width100">
          <div class="email-preview-inner-block">
            <div v-html="sanitize(emailMessage)"></div>
          </div>
        </div>

        <el-footer height="100" class="pB20 pT20 pL30 pR30">
          <el-form ref="emailTestform" :model="mailTestForm" class="width500px">
            <!-- <el-form-item label="Fields">
              <el-select
                v-model="mailTestForm.fields"
                placeholder="please select fields"
                class="width100 fc-input-full-border2"
              >
                <el-option label="Zone one" value="shanghai"></el-option>
              </el-select>
            </el-form-item> -->
            <el-form-item>
              <div class="fwBold fc-black-14 text-left">
                To
              </div>
              <div class="flex-middle">
                <el-input
                  v-model="mailTestForm.toMail"
                  class="width100 fc-input-full-border2"
                  disabled
                ></el-input>
                <el-button class="setup-el-btn mL20">Send</el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-footer>
      </div>
      <!-- <div class="modal-dialog-footer" style="z-index: 900;">
        <el-button @click="closeDialog" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="actionSave">
          {{ $t('common.wo_report.send') }}
        </el-button>
      </div> -->
    </el-dialog>
  </div>
</template>
<script>
import { sanitize } from '@facilio/utils/sanitize'
export default {
  props: ['emailMessage', 'messageText'],
  data() {
    return {
      mailTestForm: {
        fields: null,
        toMail: this.$account.user.email,
      },
    }
  },
  created() {
    this.sanitize = sanitize
  },
  methods: {
    closeDialog() {
      this.$emit('onClose')
    },
    actionSave() {
      console.log('Test email')
    },
  },
}
</script>
<style lang="scss">
.email-preview-container {
  width: 100%;
  height: 85vh;
  padding: 10px;
  background: #f0f0f0;
  padding: 20px;
}
.fc-email-preview-dialog {
  .el-dialog {
    margin-top: 3vh !important;
  }
  .email-preview-inner-block {
    max-width: 1000px;
    height: 100%;
    border-radius: 4px;
    background: #fff;
    margin-left: auto;
    margin-right: auto;
    padding: 20px;
    color: #000;
    table {
      width: 100%;
      th {
        font-weight: bold;
        text-align: left;
        background-color: #f5f6f8;

        min-width: 1em;
        border: 2px solid #ced4da;
        padding: 3px 5px;
        vertical-align: top;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
        position: relative;
      }
      td {
        min-width: 1em;
        border: 2px solid #ced4da;
        padding: 3px 5px;
        vertical-align: top;
        -webkit-box-sizing: border-box;
        box-sizing: border-box;
        position: relative;
      }
      p {
        font-size: 1em;
        margin: 0 0 1em;
        padding: 0;
        letter-spacing: 0;
      }
    }
  }
  .setup-el-btn {
    width: 100px;
    height: 40px !important;
  }
}
</style>
