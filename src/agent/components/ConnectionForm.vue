<template>
  <el-dialog
    :visible="true"
    :append-to-body="true"
    custom-class="setup-dialog40 connection-form-dialog"
    style="z-index: 999999"
    :before-close="onClose"
    title="Add Connection"
  >
    <el-form
      :model="messageSource"
      :rules="rules"
      label-position="top"
      ref="connectionForm"
      label-width="120px"
      class="fc-form"
    >
      <el-row :gutter="20" class="mR20">
        <p class="fc-input-label-txt fc-input-space-vertical">
          {{ $t('agent.agent.name') }}
        </p>
        <el-form-item>
          <el-input
            v-model="messageSource.source.name"
            placeholder="Enter Name"
            class="fc-input-full-border-select2 width100"
          ></el-input>
        </el-form-item>
      </el-row>
      <el-row :gutter="20" class="mR20">
        <p class="fc-input-label-txt fc-input-space-vertical">
          {{ $t('agent.agent.host') }}
        </p>
        <el-form-item>
          <el-input
            v-model="messageSource.configs.endPoint"
            placeholder="Enter Host Name"
            class="fc-input-full-border-select2 width100"
          >
          </el-input>
        </el-form-item>
      </el-row>

      <!-- <el-row :gutter="20" class="mR20">
        <p class="fc-input-label-txt fc-input-space-vertical">
          {{ $t('agent.agent.port') }}
        </p>
        <el-form-item>
          <el-input
            v-model="messageSource.configs.port"
            placeholder="Enter Port Number"
            class="fc-input-full-border-select2 width50"
            type="number"
          >
          </el-input>
        </el-form-item>
      </el-row> -->

      <el-row :gutter="20" class="mR20">
        <p class="fc-input-label-txt fc-input-space-vertical">
          {{ $t('agent.agent.authMode') }}
        </p>
        <el-form-item>
          <el-select
            placeholder="Enter Authentication Mode"
            v-model="messageSource.configs.authModeInt"
            class="fc-input-full-border-select2 width50"
          >
            <el-option label="Basic" :value="1"></el-option>
            <el-option label="Client Certificate" :value="2"></el-option>
          </el-select>
        </el-form-item>
      </el-row>
      <template v-if="messageSource.configs.authModeInt === 1">
        <el-row :gutter="20" class="mR20">
          <p class="fc-input-label-txt fc-input-space-vertical">
            {{ $t('agent.agent.userName') }}
          </p>
          <el-input
            v-model="messageSource.configs.userName"
            placeholder="Enter Username"
            class="fc-input-full-border-select2 width50"
          ></el-input>
        </el-row>
        <el-row :gutter="20" class="mR20">
          <p class="fc-input-label-txt fc-input-space-vertical">
            {{ $t('agent.agent.password') }}
          </p>
          <el-input
            v-model="messageSource.configs.password"
            placeholder="Enter Password"
            class="fc-input-full-border-select2 width50"
          >
          </el-input>
        </el-row>
      </template>
      <template v-if="messageSource.configs.authModeInt === 2">
        <el-row :gutter="20" class="mR20">
          <p class="fc-input-label-txt fc-input-space-vertical">
            {{ $t('agent.agent.certificate') }}
          </p>
          <el-input
            v-model="messageSource.configs.certificate"
            placeholder="Enter Client Certificate File"
            class="fc-input-full-border-select2 width100"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
          >
          </el-input>
        </el-row>

        <el-row :gutter="20" class="mR20">
          <p class="fc-input-label-txt fc-input-space-vertical">
            {{ $t('agent.agent.rootCa') }}
          </p>
          <el-input
            v-model="messageSource.configs.rootCa"
            placeholder="Enter Client CA File"
            class="fc-input-full-border-select2 width100"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
          >
          </el-input>
        </el-row>

        <el-row :gutter="20" class="mR20">
          <p class="fc-input-label-txt fc-input-space-vertical">
            {{ $t('agent.agent.privateKey') }}
          </p>
          <el-input
            v-model="messageSource.configs.privateKey"
            placeholder="Enter Client Key File"
            class="fc-input-full-border-select2 width100"
            type="textarea"
            :autosize="{ minRows: 3, maxRows: 6 }"
          >
          </el-input>
        </el-row>
      </template>
    </el-form>
    <div class="modal-dialog-footer">
      <el-button @click="closeDialog()" class="modal-btn-cancel"
        >CANCEL</el-button
      >
      <el-button
        type="primary"
        @click="submitForm"
        :loading="saving"
        class="modal-btn-save"
        >{{ saving ? 'Saving...' : 'SAVE' }}</el-button
      >
    </div>
  </el-dialog>
</template>
<script>
import { API } from '@facilio/api'
export default {
  name: 'ConnectionForm',
  props: ['onClose'],
  data() {
    return {
      messageSource: {
        source: {
          name: null,
          type: 'mqtt',
        },
        configs: {
          endPoint: null,
          port: 8083,
          authModeInt: null,
          userName: null,
          password: null,
          certificate: null,
          rootCa: null,
          privateKey: null,
        },
      },
      id: null,
      saving: false,
    }
  },
  methods: {
    closeDialog() {
      this.onClose()
    },
    async submitForm() {
      let { messageSource } = this || {}
      let { configs } = messageSource || {}
      configs = {
        ...configs,
        port: parseInt(configs.port),
        authModeInt: parseInt(configs.authModeInt),
      }
      messageSource = { ...messageSource, configs }
      let msgsrc = { messageSource }

      let json = { data: msgsrc }
      let url = 'v3/agent/addConnection'
      let { data, error } = await API.post(url, json)
      if (error) {
        this.$message.error(error.message || 'Error occured')
      } else {
        this.$message.success('Connection created')
        this.onClose()
        let { id } = data || {}
        this.$emit('save', id)
      }
    },
  },
}
</script>
<style lang="scss">
.connection-form-dialog {
  .el-dialog__body {
    height: 400px;
    overflow: scroll;
    padding: 0px 30px 70px;
  }
}
</style>
