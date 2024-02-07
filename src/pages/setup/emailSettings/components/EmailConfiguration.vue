<template>
  <div>
    <el-dialog
      :visible.sync="showconfigureDialog"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog40 setup-dialog email-configuration-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <error-banner
        :error.sync="error"
        :errorMessage="errorMessage"
        class="text-transform: capitalize;"
      ></error-banner>
      <el-form
        :model="configureData"
        :label-position="'top'"
        :rules="rules"
        ref="configurationForm"
      >
        <div class="new-header-container">
          <div class="new-header-text">
            <div class="fc-setup-modal-title">
              {{
                isNew ? $t('common._common._new') : $t('common._common.edit')
              }}
              {{ $t('setup.emailSettings.configure_action') }}
            </div>
          </div>
        </div>
        <div class="new-body-modal">
          <el-form-item
            :label="$t('setup.emailSettings.choose_action')"
            prop="chooseAction"
          >
            <el-select
              v-model="configureData.chooseAction"
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
              @change="loadWoForms(configureData.chooseAction), resetForm()"
            >
              <el-option
                v-for="action in woActionList"
                :key="action.moduleName"
                :label="action.displayName"
                :value="action.moduleName"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item
            :label="$t('setup.emailSettings.choose_template')"
            prop="formId"
          >
            <el-select
              v-model="configureData.formId"
              placeholder="Select"
              class="fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="(form, index) in templates"
                :label="form.displayName"
                :key="index"
                :value="form.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <div class="pT20">
            <div class="fc-text-pink12 text-uppercase bold">
              {{ $t('setup.workordersettings.email_mapping') }}
            </div>
            <div class="heading-description pT5">
              {{ $t('setup.workordersettings.choose_map_fields') }}
            </div>
          </div>
          <template-configuration
            :parentFields="customMailFields"
            :targetFields="moduleFields"
            :config="moduleConfigData"
            :reset.sync="reset"
            :ruleAction="rule ? rule.actions[0] : []"
            @saveAction="saveAction"
            :isSave.sync="isSave"
            :configData="configureData"
          ></template-configuration>
        </div>
        <div class="modal-dialog-footer">
          <el-button @click="closeDialog()" class="modal-btn-cancel">{{
            $t('common._common.cancel')
          }}</el-button>
          <el-button
            type="primary"
            @click="save()"
            :loading="saving"
            class="modal-btn-save"
          >
            {{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </div>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import TemplateConfiguration from 'pages/setup/emailSettings/components/TemplateConfiguration'
import ErrorBanner from '@/ErrorBanner'

export default {
  props: [
    'isNew',
    'showconfigureDialog',
    'supportEmail',
    'rule',
    'customMailFields',
    'moduleAction',
  ],
  components: {
    TemplateConfiguration,
    ErrorBanner,
  },
  data() {
    return {
      saving: false,
      error: false,
      reset: false,
      errorMessage: null,
      moduleFields: null,
      isSave: false,
      moduleConfigData: null,
      rules: {
        formId: [
          {
            required: true,
            message: 'Please Select Form ',
            trigger: 'blur',
          },
        ],
      },
      configureData: {
        formId: null,
        chooseAction: null,
        chooseTemplate: null,
      },
      configData: [
        {
          sourceFields: ['subject', 'content', 'from', 'recipient'],
          moduleName: 'workorder',
          displayName: 'Create Work Order',
          defaultValue: { subject: 'subject' },
        },
        {
          sourceFields: ['subject', 'content', 'from', 'recipient'],
          moduleName: 'customModules',
          isCustom: true,
          displayName: 'Create Service Request',
        },
      ],
      templates: null,
      workflowRule: {
        name: 'Support Mail Configuration ',
        actions: null,
        activityType: 1,
        ruleType: 37,
        criteria: {
          conditions: {
            1: {
              columnName: 'CustomMailMessage.PARENT_ID',
              fieldName: 'parentId',
              operatorId: 36,
              value: 3,
            },
          },
          pattern: '(1)',
        },
      },
    }
  },
  watch: {
    rule: {
      handler(value, oldValue) {
        if (!isEmpty(value)) {
          let { id } = value
          let { id: oldId = null } = oldValue || {}
          if (id !== oldId) this.setForms()
        }
      },
      immediate: true,
    },
  },
  mounted() {},
  created() {
    // this.loadModules()
  },
  computed: {
    woActionList() {
      return this.moduleAction
    },
  },
  methods: {
    setForms() {
      if (!isEmpty(this.rule)) {
        this.workflowRule = { ...this.workflowRule, ...this.rule }
        let action = this.rule.actions ? this.rule.actions[0] : null
        if (!isEmpty(action)) {
          this.loadWoForms(action.template.form.module.name)
          this.configureData.chooseAction = action.template.form.module.name
          if (!isEmpty(action.template)) {
            this.configureData.formId = action.template.formId
          }
        }
      }
    },
    resetForm() {
      this.reset = true
      this.configureData.formId = null
      console.log('Reset Form')
    },
    closeDialog() {
      this.$emit('update:showconfigureDialog', false)
    },
    loadWoForms(moduleName) {
      this.moduleConfigData = this.configData.filter(
        d => d.moduleName === moduleName
      )
      if (isEmpty(this.moduleConfigData)) {
        // assuming it as custom
        this.moduleConfigData.push(this.configData[1])
      }
      this.loadFields(moduleName)
      let url = `/v2/forms?moduleName=${moduleName}`
      this.$http.get(url).then(response => {
        if (response.data.responseCode === 0) {
          this.templates = (response.data.result.forms || []).filter(
            form => form.id > 0
          )
        }
      })
    },
    loadFields(moduleName) {
      this.$util.loadFields(moduleName, false).then(fields => {
        this.moduleFields = fields
      })
    },
    save() {
      this.isSave = true
    },
    saveAction(mappingJson) {
      if (mappingJson.error) {
        this.error = mappingJson.error
        this.isSave = false
        this.errorMessage = mappingJson.errorMessage
        // console.log(mappingJson.errorMessage)
        return
      }
      this.$refs['configurationForm'].validate(valid => {
        if (valid) {
          let apiaction
          this.saving = true
          let action
          Object.keys(this.woActionList).forEach(d => {
            if (
              this.woActionList[d].moduleName ===
              this.configureData.chooseAction
            ) {
              action = this.woActionList[d]
            }
          })
          if (!isEmpty(this.rule) && this.rule.id > 0) {
            apiaction = 'updatecustom'
            this.workflowRule.criteria = null
          } else {
            this.workflowRule.criteria.conditions[1].value = this.supportEmail.id
            apiaction = 'addcustom'
          }
          let self = this
          if (!isEmpty(this.configureData.formId)) {
            action.templateJson.formId = this.configureData.formId
          }

          action.templateJson.mappingJson = mappingJson
          // action.templateJson.mappingJson.moduleName = this.configureData.chooseAction
          this.workflowRule.actions = [action]

          this.$util
            .addOrUpdateRule(
              'customMailMessages',
              { rule: this.workflowRule, actions: [action] },
              !this.isNew,
              apiaction
            )
            .then(rule => {
              if (this.isNew) {
                self.updateRuleInSupportMail(rule)
              } else {
                self.saving = false
                self.$emit('onsave', rule)
                self.closeDialog()
              }
            })
            .catch(function(error) {
              self.saving = false
              console.log(error)
            })
        }
      })
    },
    updateRuleInSupportMail(rule) {
      let url
      let data = {}

      url = '/setup/updateemailsettings'
      data.id = this.supportEmail.id
      data.supportRuleId = rule.id
      API.post(url, { supportEmail: data }).then(({ error }) => {
        if (error) {
          this.saving = false
          this.$message.error(
            error.message || this.$t('common._common.deletion_failed')
          )
        } else {
          this.$emit('onsave', rule)
          this.closeDialog()
        }
      })
    },
  },
}
</script>
<style lang="scss">
.email-configuration-dialog {
  .error-txt {
    text-transform: capitalize;
  }
}
</style>
