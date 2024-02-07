<template>
  <div>
    <el-dialog
      title="Create form Network"
      :visible.sync="visibility"
      width="50%"
      :before-close="closeDialog"
      class="fc-dialog-center-container scale-up-center"
      :append-to-body="true"
    >
      <div class="height400">
        <el-form ref="networkForm" :model="newaddnetworkForm">
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Network Name
              </p>
              <el-form-item>
                <el-input
                  v-model="newaddnetworkForm.name"
                  placeholder="Enter Network Name"
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Port
              </p>
              <el-form-item>
                <el-select
                  v-model="newaddnetworkForm.port"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                >
                  <el-option label="0" value="0"></el-option>
                  <el-option label="1" value="1"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Baud Rate
              </p>
              <el-form-item>
                <el-input
                  v-model="newaddnetworkForm.baud"
                  placeholder="Enter Network Name"
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Data Bits
              </p>
              <el-form-item>
                <el-select
                  v-model="newaddnetworkForm.dataBits"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                >
                  <el-option label="0" value="0"></el-option>
                  <el-option label="1" value="1"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Stop Bits
              </p>
              <el-form-item>
                <el-input
                  v-model="newaddnetworkForm.topBits"
                  placeholder="Enter stop bits Name"
                  class="fc-input-full-border2 width100"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <p class="fc-input-label-txt pT10">
                Parity
              </p>
              <el-form-item>
                <el-select
                  v-model="newaddnetworkForm.parity"
                  placeholder="Select"
                  class="fc-input-full-border2 width100"
                >
                  <el-option key="0" label="0" value="0"></el-option>
                  <el-option key="1" label="1" value="1"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="modal-dialog-footer">
            <el-button @click="closeDialog()" class="modal-btn-cancel"
              >CANCEL</el-button
            >
            <el-button
              type="primary"
              @click="submitForm('networkForm')"
              :loading="saving"
              class="modal-btn-save"
              >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
            >
          </div>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: ['visibility'],
  data() {
    return {
      saving: false,
      newaddnetworkForm: {
        name: '',
        port: null,
        baud: '',
        dataBits: null,
        topBits: '',
        parity: null,
        ports: null,
      },
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    submitForm(networkForm) {
      this.$refs[networkForm].validate(valid => {
        if (valid) {
          this.saving = true
          let networkPayload = {
            name: this.newaddnetworkForm.name,
            comPort: 1,
            baudRate: 9600,
            dataBits: 0,
            stopBits: 1,
            parity: 0,
            agentId: 1,
          }
          this.$http
            .post('/v2/modbus/addRtuNetwork', networkPayload)
            .then(response => {
              if (response.status === 200) {
                this.$message('Network has been updated.')
                this.saving = false
                this.resetForm()
                this.$emit('saved')
                this.$emit('update:visibility', false)
              }
            })
        }
      })
    },
    resetForm() {
      this.$refs['networkForm'].resetFields()
    },
    cancel() {
      this.$emit('canceled')
    },
  },
}
</script>
<style lang="scss"></style>
