<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog"
    :before-close="closeDialog"
    style="z-index: 999999"
  >
    <error-banner
      :error.sync="error"
      :errorMessage.sync="errorText"
    ></error-banner>
    <el-form
      :model="newgroup"
      :label-position="'top'"
      ref="ruleForm"
      label-width="120px"
      class="fc-form"
    >
      <!-- header -->
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">
            {{
              isNew
                ? $t('setup.users_management.new_team')
                : $t('setup.users_management.edit_team')
            }}
          </div>
        </div>
      </div>
      <!-- body -->
      <div class="new-body-modal">
        <el-row :gutter="20">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.team_name') }}
            </p>
            <el-form-item prop="name">
              <el-input
                :autofocus="true"
                v-model="newgroup.name"
                :placeholder="$t('setup.users_management.enter_your_team')"
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="mT10">
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.approvalprocess.description') }}
            </p>
            <el-form-item prop="description">
              <el-input
                :placeholder="$t('common._common.enter_desc')"
                v-model="newgroup.description"
                autoComplete="off"
                :autosize="{ minRows: 3, maxRows: 6 }"
                type="textarea"
                resize="none"
                class="fc-input-full-border-select2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.site') }}
            </p>
            <el-form-item prop="site">
              <Lookup
                v-model="newgroup.siteId"
                :field="fields.site"
                :hideLookupIcon="true"
                @recordSelected="setSelectedValue"
                @showLookupWizard="showLookupWizardSite"
              ></Lookup>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.users_management.users') }}
            </p>
            <el-form-item prop="members">
              <el-select
                v-model="newgroup.members"
                multiple
                filterable
                collapse-tags
                class="width100 fc-tag fc-input-full-border-select2"
              >
                <el-option
                  v-for="user in users"
                  :key="user.id"
                  :label="user.name"
                  :value="user.id"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <p class="fc-input-label-txt pB10">
              {{ $t('setup.setup_profile.email') }}
            </p>

            <el-form-item prop="email">
              <el-input
                v-model="newgroup.email"
                type="text"
                autocomplete="off"
                :placeholder="
                  $t('setup.users_management.please_enter_the_email')
                "
                class="width100 fc-input-full-border2"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </div>

      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel">
          {{ $t('setup.users_management.cancel') }}</el-button
        >
        <el-button
          type="primary"
          @click="submitForm('ruleForm')"
          :loading="saving"
          class="modal-btn-save"
          >{{
            saving ? $t('common._common._saving') : $t('common._common._save')
          }}
        </el-button>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import ErrorBanner from '@/ErrorBanner'
import { isEmpty } from '@facilio/utils/validation'
import { Lookup } from '@facilio/ui/forms'
const fields = {
  site: {
    isDataLoading: false,
    options: [],
    lookupModuleName: 'site',
    field: {
      lookupModule: {
        name: 'site',
        displayName: 'Sites',
      },
    },

    multiple: false,
    additionalParams: {
      orderBy: 'spaceType',
      orderType: 'asc',
    },
  },
}

export default {
  props: ['group', 'isNew', 'visibility', 'rule'],

  data() {
    return {
      fields,
      selected: [],
      saving: false,
      errorText: '',
      error: false,
      newgroup: {
        name: '',
        description: '',
        members: [],
        siteId: null,
        email: '',
      },
    }
  },
  created() {
    this.$store.dispatch('loadSite')
  },
  mounted() {
    if (!this.isNew) {
      this.deserializeData()
    }
  },
  components: {
    Lookup,
    ErrorBanner,
  },
  computed: {
    users() {
      let userList = this.$store.state.users
      if (userList) {
        return userList
      }
      return []
    },
  },
  methods: {
    deserializeData() {
      let { group } = this
      Object.assign(this.newgroup, group)
      this.newgroup.members = []
      if (isEmpty(group.siteId)) {
        this.newgroup.siteId = null
      }
      if (!isEmpty(group.members)) {
        for (let i = 0; i < group.members.length; i++) {
          this.newgroup.members.push(group.members[i].id)
        }
      }
    },
    showLookupWizardSite(field, canShow) {
      canShow = false
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizardSite', canShow)
    },
    setSelectedValue(selectedValues, field) {
      selectedValues
      field
    },
    validation(rule) {
      this.error = false
      this.errorText = ''
      if (!rule.name) {
        this.errorText = this.$t(
          'setup.users_management.please_enter_team_name'
        )
        this.error = true
      } else if (!rule.members) {
        this.errorText = this.$t('setup.users_management.please_select_users')
        this.error = true
      } else if (rule.email) {
        rule.email = rule.email.trim()
        let isValid = this.$common.validateEmail(rule.email)
        if (!isValid) {
          this.errorText = this.$t(
            'setup.users_management.please_enter_valid_email_only'
          )
          this.error = true
        }
      } else {
        this.errorText = ''
        this.error = false
      }
    },
    submitForm(ruleForm) {
      this.validation(this.newgroup)
      if (this.error) {
        return
      }
      if (this.newgroup.siteId === null) {
        this.newgroup.siteId = -1
      }
      this.saving = true
      let url = '/setup/addgroup'
      if (!this.isNew) {
        this.newgroup.id = this.group.id
        url = '/setup/updategroup'
      }
      let members = []
      this.newgroup.members.forEach(i => members.push(i))
      this.newgroup.members = []
      this.newgroup.siteId = !this.newgroup.siteId ? -1 : this.newgroup.siteId
      this.$http
        .post(url, { group: this.newgroup, members })
        .then(response => {
          if (response.status === 200) {
            if (this.isNew) {
              this.$message.success(
                this.$t('setup.users_management.new_team_created_success')
              )
              this.$emit('saved')
              this.saving = false
              this.$emit('update:visibility', false)
              this.$refs[ruleForm].resetFields()
            } else {
              this.$message.success(
                this.$t('setup.users_management.team_details_success')
              )
              this.$emit('update:visibility', false)
            }
            this.saving = false
            this.resetForm()
            this.$emit('saved')
          }
        })
        .catch(_ => {
          this.saving = false
        })
    },
    resetForm() {
      this.$refs['ruleForm'].resetFields()
    },
    cancel() {
      this.$emit('canceled')
    },
    save() {
      this.saving = true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
