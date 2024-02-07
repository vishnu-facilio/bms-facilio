<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <div>
      <el-form
        :model="newaddController"
        :rules="rules"
        :label-position="'top'"
        ref="controllerForm"
        label-width="120px"
        class="fc-form"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{ isNew ? 'New' : 'Edit' }} Controller
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <div class="setup-input-block">
            <p class="fc-input-label-txt">Controller Name</p>
            <el-form-item prop="name">
              <el-input
                :disabled="!isNew"
                v-model="newaddController.name"
                placeholder="Enter Controller Name"
                class="fc-input-txt fc-name-input"
              ></el-input>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">
              Display Name
            </p>
            <el-form-item prop="name">
              <el-input
                v-model="newaddController.displayName"
                placeholder="Enter Display Name"
                class="fc-input-txt fc-name-input"
              ></el-input>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">
              Controller Type
            </p>
            <el-form-item prop="controllerType">
              <el-select
                :disabled="!isNew"
                v-model="newaddController.controllerType"
              >
                <el-option
                  v-for="(label, value) in $constants.ControllerTypes"
                  :key="value"
                  :label="label"
                  :value="parseInt(value)"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div
            class="setup-input-block"
            v-if="$common.isBacnetController(newaddController)"
          >
            <p class="fc-input-label-txt fc-input-space-vertical">
              Instance Number
            </p>
            <el-form-item prop="instanceNumber">
              <el-input
                :disabled="!isNew"
                v-model="newaddController.instanceNumber"
                placeholder="Enter Instance Number"
                class="fc-input-txt fc-desc-input"
              ></el-input>
            </el-form-item>
          </div>
          <div class="setup-input-block">
            <p class="fc-input-label-txt fc-input-space-vertical">
              Mac Address
            </p>
            <el-form-item prop="macAddr">
              <el-input
                :disabled="!isNew"
                v-model="newaddController.macAddr"
                placeholder="Enter Mac Address"
                class="fc-input-txt fc-desc-input"
              ></el-input>
            </el-form-item>
          </div>
          <div>
            <p class="fc-input-label-txt fc-input-space-vertical">Interval</p>
            <el-form-item prop="dataInterval">
              <el-select
                placeholder="Enter Interval"
                v-model="newaddController.dataInterval"
                class="fc-input-select"
              >
                <el-option label="5 mins" :value="5"></el-option>
                <el-option label="10 mins" :value="10"></el-option>
                <el-option label="15 mins" :value="15"></el-option>
                <el-option label="20 mins" :value="20"></el-option>
                <el-option label="30 mins" :value="30"></el-option>
                <el-option label="1 hour" :value="60"></el-option>
              </el-select>
            </el-form-item>
          </div>
          <div>
            <p class="fc-input-label-txt fc-input-space-vertical inline mR20">
              Is Writable
            </p>
            <el-switch
              v-model="newaddController.writable"
              class="Notification-toggle"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">
            CANCEL</el-button
          >
          <el-button
            type="primary"
            @click="submitForm('controllerForm')"
            :loading="saving"
            class="modal-btn-save"
            >{{ saving ? 'Saving...' : 'SAVE' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: ['visibility', 'isNew', 'model'],
  data() {
    return {
      saving: false,
      newaddnetworkForm: {
        id: -1,
        name: '',
        macAddr: '',
        controllerType: null,
        dataInterval: null,
        writable: null,
        instanceNumber: null,
        displayName: '',
      },
      rules: {
        dataInterval: [
          {
            required: true,
            message: 'Please select a data interval',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  mounted() {
    if (!this.isNew) {
      let {
        id,
        name,
        macAddr,
        controllerType,
        dataInterval,
        writable,
        instanceNumber,
        displayName,
      } = this.model
      Object.assign(this.newaddController, {
        id,
        name,
        macAddr,
        controllerType,
        dataInterval,
        writable,
        instanceNumber,
        displayName,
      })

      let nullableFields = ['dataInterval', 'controllerType']
      nullableFields.forEach(fieldName => {
        if (this.newAgent[fieldName] === -1) {
          this.newAgent[fieldName] = null
        }
      })
    }
  },
  methods: {
    submitForm(controllerForm) {
      this.$refs[controllerForm].validate(valid => {
        if (valid) {
          this.saving = true
          let data = {
            controllerContext: this.$helpers.cloneObject(this.newaddController),
          }
          if (!data.controllerContext.dataInterval) {
            data.controllerContext.dataInterval = 15
          }
          this.$http.post('/v2/setup/controller/edit', data).then(response => {
            if (response.status === 200) {
              this.$message('Controller has been updated.')
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
      this.$refs['controllerForm'].resetFields()
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    cancel() {
      this.$emit('canceled')
    },
  },
}
</script>
