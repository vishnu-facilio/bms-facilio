<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog fc-new-app-dialog"
    style="z-index: 999999"
    :before-close="closeDialog"
  >
    <el-form
      :model="appDetails"
      :rules="rules"
      :label-position="'top'"
      ref="webtab-app"
      class="fc-form webtab-app"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{ isNew ? 'ADD' : 'EDIT' }} APP
          </div>
        </div>
      </div>

      <div class="new-body-modal enpi-body-modal">
        <div class="body-scroll">
          <el-form-item :label="$t('common._common.name')" prop="name">
            <el-input
              class="width100 fc-input-full-border2"
              autofocus
              v-model="appDetails.name"
              type="text"
              placeholder="Enter App Name"
            />
          </el-form-item>

          <el-form-item label="Route" prop="linkName">
            <el-input
              class="width100 fc-input-full-border2 fc-route-input"
              autofocus
              v-model="appDetails.linkName"
              type="text"
              placeholder="Enter app Route"
            >
              <template slot="prepend">{{ appUrl }}</template>
            </el-input>
          </el-form-item>

          <el-form-item label="Description" prop="description">
            <el-input
              v-model="appDetails.description"
              :min-rows="1"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              class="fc-input-full-border-select2 width100"
              :placeholder="$t('common._common.enter_desc')"
              resize="none"
            ></el-input>
          </el-form-item>

          <el-form-item></el-form-item>
          <el-form-item></el-form-item>
        </div>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          CANCEL
        </el-button>
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="submitForm()"
          :loading="saving"
        >
          {{ saving ? 'Saving...' : 'SAVE' }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
export default {
  props: ['selectedApp'],

  data() {
    return {
      appDetails: {
        name: null,
        linkName: null,
        description: null,
        appCategory: 3,
        layoutType: 2,
        domainType: 1,
        isDefault: false,
      },
      saving: false,
      rules: {
        name: {
          required: true,
          message: 'Please enter a name',
          trigger: 'change',
        },
        linkName: {
          required: true,
          message: 'Please enter a route',
          trigger: 'change',
        },
      },
    }
  },

  created() {
    if (!this.isNew) {
      this.appDetails = { ...this.selectedApp }
    }
  },

  computed: {
    isNew() {
      return isEmpty(this.selectedApp)
    },
    appUrl() {
      let { isNew, $account, selectedApp } = this
      let appDomain = null

      if (isNew) {
        let { user } = $account || {}
        appDomain = (user || {}).appDomain
      } else {
        appDomain = selectedApp.appDomain
      }

      let { domain } = appDomain || {}
      return `https://${domain || 'domain'}`
    },
  },

  methods: {
    async submitForm() {
      this.$refs['webtab-app'].validate(async valid => {
        if (!valid) return false

        let { appDetails, isNew } = this
        let params = { application: {}, addLayout: true }

        if (isNew) {
          params.application = { ...appDetails }
        } else {
          let { name, linkName, description, isDefault, id } = appDetails
          params.application = { name, linkName, description, isDefault, id }
        }

        this.saving = true
        let { error } = await API.post('/v2/application/addOrUpdate', params)

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.$message.success('App saved successfully')
          this.$emit('onSave')
          this.closeDialog()
        }

        this.saving = false
      })
    },
    closeDialog() {
      this.$emit('onClose')
    },
  },
}
</script>
<style lang="scss">
.fc-new-app-dialog {
  .fc-route-input {
    .el-input__inner {
      border-left: none !important;
      border-top-left-radius: 0 !important;
      border-bottom-left-radius: 0 !important;
    }
  }
}
</style>
