<template>
  <el-dialog
    :visible.sync="visibility"
    v-if="visibility"
    :append-to-body="true"
    :title="title ? title : 'Connect Device'"
    class="fieldchange-Dialog pB15 fc-dialog-center-container"
    custom-class="dialog-header-padding"
    :before-close="closeNewDialog"
  >
    <div style="height: 250px;">
      <div class="code-info-badge">
        <slot>
          Access <b>app.facilio.com/device</b> on your device and copy paste the
          passcode here:
        </slot>
      </div>

      <el-form
        :model="addCode"
        :label-position="'top'"
        @submit.native.prevent="connectDevice"
      >
        <el-form-item prop="code">
          <p class="grey-text2">Passcode</p>
          <el-input
            :autofocus="true"
            style="width: 250px;"
            ref="codeInput"
            v-model="addCode.code"
            placeholder="Enter passcode (6 digit code)"
            class="fc-input-full-border2"
          ></el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeNewDialog()"
        >Cancel</el-button
      >
      <el-button
        class="modal-btn-save"
        type="primary"
        @click="connectDevice()"
        :loading="connecting"
        >{{ connecting ? 'Connecting...' : 'Connect' }}</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['device', 'visibility', 'title'],
  components: {},
  data() {
    return {
      connecting: false,
      addCode: {
        code: null,
      },
    }
  },
  methods: {
    connectDevice() {
      this.connecting = false
      if (!this.addCode.code || this.addCode.code.trim().length !== 6) {
        alert('Invalid code!')
      } else {
        let data = {
          code: this.addCode.code,
          device: {
            id: this.device.id,
          },
        }
        this.connecting = true
        this.$http
          .post('/v2/devices/connect', data)
          .then(response => {
            this.connecting = false
            if (response.data.result.rowsUpdated) {
              //reload list
              this.$message({
                message: ' Device connected successfully!',
                type: 'success',
              })
              this.$emit('connected')
              this.closeNewDialog()
            } else {
              alert('Invalid code!')
            }
          })
          .catch(error => {
            this.connecting = false
            console.log(error)
            alert('Invalid code!')
          })
      }
    },
    closeNewDialog() {
      this.addCode.code = null
      this.$emit('update:visibility', false)
    },
  },
}
</script>

<style>
.code-info-badge {
  word-break: break-word;
  padding: 8px;
  border: 1px solid #bbba;
  background: #fafafa;
  text-align: left;
  display: block;
  font-size: 11px;
  margin: 20px 0;
}
</style>
