<template>
  <div>
    <el-dialog
      :visible="true"
      width="45%"
      custom-class="fc-dialog-right fc-setup-modal-title setup-dialog55 setup-dialog-header-hide "
      :title="formTitle"
      :append-to-body="true"
      :before-close="closeDialog"
    >
      <el-form ref="connectedApp" :rules="rules" :model="connectedApp">
        <div class="new-body-modal">
          <el-form-item :label="$t('setup.approvalprocess.name')" prop="name">
            <el-input
              class="width100 fc-input-full-border2"
              v-model="connectedApp.name"
              type="text"
              placeholder="Enter name"
            />
          </el-form-item>
          <el-form-item prop="description" label="Description">
            <el-input
              class="fc-input-full-border-textarea text-capitalize"
              type="textarea"
              resize="none"
              v-model="connectedApp.description"
              :autosize="{ minRows: 5, maxRows: 5 }"
              :placeholder="$t('common.roles.description')"
            />
          </el-form-item>

          <el-form-item label="Sandbox Base URL" prop="sandBoxBaseUrl">
            <el-input
              class="width100 fc-input-full-border2"
              :autofocus="true"
              v-model="connectedApp.sandBoxBaseUrl"
              type="text"
              placeholder="Enter sandbox base URL"
            />
          </el-form-item>
          <el-form-item label="Production Base URL" prop="productionBaseUrl">
            <el-input
              class="width100 fc-input-full-border2"
              :autofocus="true"
              v-model="connectedApp.productionBaseUrl"
              type="text"
              placeholder="Enter production base URL"
            />
          </el-form-item>
          <el-form-item label="Welcome URL" prop="startUrl">
            <el-input
              class="width100 fc-input-full-border2"
              :autofocus="true"
              v-model="connectedApp.startUrl"
              type="text"
              placeholder="Enter Welcome URL"
            />
          </el-form-item>
          <el-form-item label="Show in Launcher" prop="lancher">
            <el-checkbox v-model="connectedApp.showInLauncher"></el-checkbox>
          </el-form-item>
        </div>
      </el-form>

      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="closeDialog">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="saveForm()"
          :loading="saving"
          >{{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import cloneDeep from 'lodash/cloneDeep'
export default {
  props: ['isNew', 'connectedAppData'],
  data() {
    return {
      connectedApp: {
        name: '',
        description: '',
        productionBaseUrl: '',
        sandBoxBaseUrl: '',
        showInLauncher: false,
        startUrl: '',
        hostingType: null,
        id: null,
        isActive: null,
        linkName: null,
        logoId: null,
      },
      formTitle: 'ADD CONNECTED APP',
      saving: false,
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter the name',
            trigger: 'blur',
          },
        ],
        sandBoxBaseUrl: [
          {
            required: true,
            message: 'Please enter the URL',
            trigger: 'blur',
          },
        ],
        productionBaseUrl: [
          {
            required: true,
            message: 'Please enter the URL',
            trigger: 'blur',
          },
        ],
        startUrl: [
          {
            required: true,
            message: 'Please enter the URL',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  mounted() {
    if (!this.isNew) {
      let { connectedAppData } = this
      this.connectedApp = cloneDeep(connectedAppData)
      this.formTitle = 'EDIT CONNECTED APP'
    }
  },
  components: {},
  methods: {
    saveForm() {
      this.$refs['connectedApp'].validate(async valid => {
        if (!valid) return false
        this.saving = true
        let url = null
        if (this.isNew) {
          url = 'v2/connectedApps/add'
        } else {
          url = 'v2/connectedApps/update'
        }
        let { connectedApp, isNew } = this
        if (isNew) connectedApp.id = null
        let params = {
          connectedApp,
        }
        let { error, data } = await API.post(url, params)
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occur')
          )
        } else {
          this.$message.success(
            this.$t('common._common.connected_saved_successfully')
          )
          this.$emit('onSave', data.connectedContext)
          this.saving = false
          this.closeDialog()
        }
        this.$forceUpdate()
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.el-dialog {
  .fc-dialog-right {
    position: absolute;
    right: 0;
    top: 0;
    bottom: 0;
    overflow-y: hidden;
    border-radius: 0px;
    width: 55%;
    transition: transform 0.25s ease;
    text-align: left;
    box-shadow: none;
    margin-top: 0px !important;
    margin-bottom: 0px !important;
    padding-top: 0%;

    .el-dialog__body {
      padding: 0;
    }
  }
  .fc-setup-modal-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
  .setup-dialog-header-hide .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0px 20px !important;
  }
  .el-dialog__header {
    padding: 20px 0px 15px 40px;
    border-bottom: 1px solid #ebedf4;
  }
  .el-form-item {
    text-transform: capitalize;
  }
  .el-form-item__label {
    color: rgb(81, 66, 66);
  }
  .is-required .el-form-item__label {
    color: rgb(197, 30, 30);
  }
}
.new-body-modal {
  overflow-y: scroll;
  overflow-x: hidden;
  padding-left: 20px;
  padding-right: 40px;
  text-align: left;
  margin: 0px;
  height: calc(100vh - 150px);
  position: relative;
  padding-bottom: 30px;
}
</style>
