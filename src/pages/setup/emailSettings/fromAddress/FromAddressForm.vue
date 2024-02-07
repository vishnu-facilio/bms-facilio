<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
      :before-close="() => closeDialog()"
    >
      <el-form
        :model="formModel"
        :rules="rules"
        :label-position="'top'"
        ref="fromAddressForm"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew ? $t('common._common._new') : $t('common._common.edit')
              }}
              {{ $t('setup.from_address.from_address') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-form-item
            :label="$t('setup.from_address.source_type')"
            prop="sourceType"
          >
            <el-select
              v-model="formModel.sourceType"
              class="fc-input-full-border-select2 width100"
              :placeholder="$t('setup.from_address.select_source_type')"
              :disabled="!isNew"
            >
              <el-option
                v-for="key in Object.keys(
                  $constants.fromEmailAddressSourceType
                )"
                :key="key"
                :label="$constants.fromEmailAddressSourceType[key]"
                :value="Number(key)"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item
            :label="$t('setup.approvalprocess.name')"
            prop="displayName"
          >
            <el-input
              type="text"
              v-model="formModel.displayName"
              class="fc-input-full-border2"
              :placeholder="$t('setup.from_address.display_name')"
            ></el-input>
          </el-form-item>
          <el-form-item :label="$t('setup.setup_profile.email')" prop="email">
            <el-checkbox
              @change="addFormRule"
              v-model="isUseOwnMail"
              :disabled="!isNew"
              class="from-email-checkbox"
              >Use my own email</el-checkbox
            >
            <el-input
              v-model="formModel.email"
              :disabled="!isNew"
              class="fc-input-full-border2 email-append-slot"
              :placeholder="$t('setup.emailSettings.enter_email')"
            >
              <template v-if="!isUseOwnMail" v-slot:append>
                {{ `@${$account.org.domain || ''}.` + getMailDomain() }}
              </template>
            </el-input>
          </el-form-item>

          <!-- site drop down only for support addresses -->
          <el-form-item
            :label="$t('setup.users_management.site')"
            prop="siteId"
          >
            <el-select
              v-model="formModel.siteId"
              class="fc-input-full-border-select2 width100"
              :placeholder="$t('setup.setup.select_site')"
              :loading="siteLoading"
              filterable
              clearable
              remote
              :remote-method="siteRemoteMethod"
            >
              <el-option
                v-for="site in sites"
                :key="site.id"
                :label="site.name"
                :value="site.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            :loading="saving"
            class="modal-btn-save"
            @click="validateFrom"
            >{{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}</el-button
          >
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { getSites } from 'pages/workorder/wo-util.js'
import { API } from '@facilio/api'
import debounce from 'lodash/debounce'

export default {
  props: ['visibility', 'record'],
  data() {
    return {
      saving: false,
      isUseOwnMail: false,
      siteLoading: false,
      sites: [],
      rules: {
        displayName: [
          {
            required: true,
            message: 'Please input display name',
            trigger: 'blur',
          },
        ],
        email: [{ required: true, message: 'Please input email address' }],
        sourceType: [{ required: true, message: 'Please input source type' }],
      },
      formModel: {
        displayName: null,
        email: null,
        siteId: null,
        sourceType: null,
      },
    }
  },
  computed: {
    isNew() {
      return isEmpty(this.record)
    },
  },
  created() {
    this.siteRemoteMethod = debounce(this.filterSites, 500)
  },
  mounted() {
    this.initModel()
  },
  methods: {
    async initModel() {
      let { record } = this
      this.sites = await getSites()
      if (!this.isNew) {
        Object.keys(this.formModel).forEach(key => {
          this.$setProperty(this.formModel, `${key}`, record?.[key])
        })
        if (
          record.email &&
          record.email.indexOf(
            `@${this.$account.org.domain || ''}.` + this.getMailDomain()
          ) > -1
        ) {
          let mailAddress = record.email.split(
            `@${this.$account.org.domain || ''}.` + this.getMailDomain()
          )
          this.formModel.email = mailAddress[0]
        } else {
          this.isUseOwnMail = true
        }
      } else if (this.formModel.sourceType === 2) {
        this.rules.siteId = [{ required: true, message: 'Please input site?' }]
      }
    },
    async filterSites(searchText) {
      this.siteLoading = true
      this.sites = await getSites({ searchText })
      this.siteLoading = false
    },
    addFormRule(value) {
      if (value) {
        this.rules.email.push({
          type: 'email',
          message: 'Please input correct email address',
        })
      } else {
        this.rules.email = [
          { required: true, message: 'Please input email address' },
        ]
      }
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    validateFrom() {
      this.$refs['fromAddressForm'].validate(valid => {
        if (valid) {
          if (this.isNew) {
            this.createRecord()
          } else {
            this.updateRecord()
          }
        }
      })
    },
    getMailDomain() {
      return this.$account.config.mailDomain
    },
    formatData(record) {
      if (!this.isUseOwnMail) {
        record.email +=
          `@${this.$account.org.domain || ''}.` + this.getMailDomain()
      }
    },
    async createRecord() {
      this.saving = true
      let record = this.$helpers.cloneObject(this.formModel)
      this.formatData(record)
      let { error } = await API.createRecord('emailFromAddress', {
        data: record,
      })
      if (error) {
        let { message } = error
        this.$message.error(
          message || this.$t('setup.from_address.error_create')
        )
      } else {
        this.$emit('saved')
        this.$message.success(this.$t('setup.from_address.success_create'))
        this.closeDialog()
      }
      this.saving = false
    },
    async updateRecord() {
      this.saving = true
      let record = this.$helpers.cloneObject(this.formModel)
      record.id = this.record?.id
      record.email = null
      record.sourceType = null
      let { error } = await API.updateRecord('emailFromAddress', {
        id: this.record?.id,
        data: record,
      })
      if (error) {
        let { message } = error
        this.$message.error(
          message || this.$t('setup.from_address.error_update')
        )
      } else {
        this.$emit('saved')
        this.$message.success(this.$t('setup.from_address.success_update'))
        this.closeDialog()
      }
      this.saving = false
    },
  },
}
</script>
