<template>
  <div class="email-settings-container">
    <portal to="wo-email-settings-header-email">
      <div class="display-flex-between-space m20">
        <div class="setting-title-block">
          <div class="setting-form-title">
            {{ $t('setup.workordersettings.email_settings') }}
          </div>
          <div class="heading-description">
            {{ $t('setup.workordersettings.email_settings_decsription') }}
          </div>
        </div>
        <div class="action-btn setting-page-btn">
          <el-button
            type="primary"
            class="el-button setup-el-btn el-button--primary"
            @click="addEmail"
            >{{ $t('setup.workordersettings.add_email') }}</el-button
          >
        </div>
      </div>
    </portal>
    <div v-if="loading" class="flex-middle">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div
      v-else-if="$validation.isEmpty(supportEmails)"
      class="flex-middle height100 justify-content-center flex-direction-column white-bg"
    >
      <inline-svg
        src="svgs/emptystate/notes"
        iconClass="icon text-center icon-130"
      ></inline-svg>
      <div class="nowo-label">
        {{ $t('setup.emailSettings.no_email_msg') }}
      </div>
    </div>
    <div v-else>
      <div
        v-for="(supportMail, index) in supportEmails"
        :key="index"
        class="check-list-hor-card d-flex display-flex-between-space visibility-visible-actions"
      >
        <div>
          <div class="flex-middle">
            <div class="fc-black3-16">{{ supportMail.replyName }}</div>
            <el-button
              size="mini"
              v-if="supportMail.isCustomMail"
              class="mL15 fc-btn-grey-border-mini text-uppercase pT3 pB3 pL3 pR3 f9"
              >{{ $t('setup.workordersettings.imap') }}</el-button
            >
            <el-button
              size="mini"
              class="mL15 small-border-btn pT3 pB3 text-uppercase pL3 pR3 f9"
              v-else
              >{{ $t('setup.workordersettings.default') }}</el-button
            >
            <div class="pL20 visibility-hide-actions">
              <i
                class="el-icon-edit fc-black-14"
                @click="editMail(supportMail)"
              ></i>
              <i
                class="el-icon-delete fc-delete-icon pL10"
                @click="deleteEmail(supportMail.id, index)"
              ></i>
            </div>
          </div>
          <div class="fc-grey-dark f13 pT10">
            {{ supportMail.actualEmail }}
          </div>
        </div>
        <div class="flex-middle">
          <div
            class="fc-txt-terkish f13 pR10"
            v-if="supportRule[supportMail.supportRuleId]"
          >
            <div
              class="fc-txt-terkish f13"
              @click="addConfiguration(supportMail, true)"
            >
              {{ $t('setup.workordersettings.edit_action') }}
            </div>
          </div>
          <div>
            <el-button
              v-if="!supportRule[supportMail.supportRuleId]"
              size="mini"
              class="bR3 add-border-btn"
              @click="addConfiguration(supportMail, false)"
              >{{ $t('setup.workordersettings.configure_action') }}</el-button
            >
            <el-button v-else size="mini" class="fc-btn-grey-fill">
              {{ generateActionName(supportRule[supportMail.supportRuleId]) }}
            </el-button>
          </div>
          <div class="visibility-hide-actions email-settings-action">
            <el-dropdown
              trigger="click"
              v-if="supportRule[supportMail.supportRuleId]"
            >
              <span class="el-dropdown-link">
                <InlineSvg
                  src="svgs/menu"
                  iconClass="icon icon-xs mR10"
                ></InlineSvg>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <div @click="deleteRule(supportMail)">
                    Delete Action
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>
    <AddNewemail
      v-if="showDialog"
      @onsave="onEmailCreated"
      :supportEmailObj="supportMailContext"
      :isNew="isNew"
      :visibility.sync="showDialog"
    ></AddNewemail>
    <configuration
      v-if="showconfigureDialog"
      :isNew="!isEditAction"
      @onsave="loadnotifications"
      :customMailFields="fields"
      :moduleAction="moduleAction"
      :supportEmail="supportMailContext"
      :rule="rule"
      :showconfigureDialog.sync="showconfigureDialog"
    ></configuration>
  </div>
</template>
<script>
import AddNewemail from 'pages/setup/emailSettings/components/AddNewEmailForm'
import { isEmpty } from '@facilio/utils/validation'
import Configuration from 'pages/setup/emailSettings/components/EmailConfiguration'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'

export default {
  title() {
    return this.$t('setup.workordersettings.email_settings')
  },
  data() {
    return {
      showDialog: false,
      showconfigureDialog: false,
      isNew: true,
      isEditAction: false,
      supportMailContext: null,
      supportEmails: null,
      moduleAction: [
        {
          actionType: 35,
          templateJson: {
            name: 'WorkOrder',
            mappingJson: {},
          },
          moduleName: 'workorder',
          displayName: 'Create Work Order',
        },
        {
          actionType: 35,
          templateJson: {
            name: 'Service Request',
            mappingJson: {},
          },
          moduleName: 'serviceRequest',
          displayName: 'Create Service Request',
        },
      ],
      supportRule: [],
      rule: null,
      moduleName: 'customMailMessages',
      fields: [],
      loading: false,
    }
  },
  components: {
    AddNewemail,
    Configuration,
    Spinner,
  },
  created() {
    this.loadFields()
  },
  mounted: function() {
    this.loadnotifications()
    this.loadModules()
  },
  methods: {
    loadModules() {
      API.get('v2/module/lists?defaultModules=true').then(({ error, data }) => {
        if (!error) {
          if (data.modules) {
            if (
              data.modules.customModules &&
              data.modules.customModules.length
            ) {
              let customModule = data.modules.customModules
              customModule.forEach(d => {
                this.moduleAction.push({
                  actionType: 33,
                  templateJson: {
                    name: d.displayName,
                    mappingJson: {},
                  },
                  moduleName: d.name,
                  displayName: 'Create ' + d.displayName,
                })
              })
            }
          }
        }
      })
    },
    loadnotifications() {
      this.loading = true
      API.get('/setup/emailsettings').then(({ error, data }) => {
        if (!error) {
          this.supportEmails = data.supportEmails
          this.supportRule = data.supportMailRules || []
          this.loading = false
        } else {
          this.$message.error(
            error?.message || 'Error occurred while loading email list'
          )
        }
        this.loading = false
      })
    },
    loadFields() {
      return API.get('/module/metafields?moduleName=' + this.moduleName).then(
        ({ data }) => {
          this.fields = this.$getProperty(data, 'meta.fields', null) || []
        }
      )
    },
    deleteRule(supportMail) {
      this.$dialog
        .confirm({
          title: this.$t('setup.emailSettings.remove_action'),
          message: this.$t('setup.emailSettings.delete_action_msg'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            let url = '/v2/modules/rules/delete'
            let ids = {
              ids: [supportMail.supportRuleId],
            }
            API.post(url, ids).then(({ error }) => {
              if (error) {
                this.$message.error(
                  error?.message ||
                    this.$t('setup.emailSettings.action_removed_msg')
                )
              } else {
                this.$message.success(
                  this.$t('setup.emailSettings.action_removed_msg')
                )
                supportMail.supportRuleId = null
              }
            })
          }
        })
    },
    addEmail() {
      this.showDialog = true
      this.supportMailContext = null
      this.isNew = true
    },
    addConfiguration(mailContext, isEdit) {
      this.isEditAction = isEdit
      this.showconfigureDialog = true
      this.supportMailContext = mailContext
      if (this.supportRule != null) {
        this.rule = this.supportRule[this.supportMailContext.supportRuleId]
      }
    },
    editMail(mailContext) {
      this.isNew = false
      let { imapServiceProviderType, isCustomMail } = mailContext || {}

      if (!isEmpty(mailContext) && isCustomMail) {
        let emailSystemTypeHash = {
          GMAIL: 'gmail',
          MICROSOFT_OFFICE_365: 'outLook',
        }
        mailContext.emailSystem = this.$getProperty(
          emailSystemTypeHash,
          imapServiceProviderType,
          null
        )
      }
      this.supportMailContext = mailContext
      this.showDialog = true
    },
    onEmailCreated() {
      this.loadnotifications()
    },
    deleteEmail(id) {
      this.$dialog
        .confirm({
          title: this.$t('setup.emailSettings.delete_email_msg'),
          message: this.$t('setup.emailSettings.delete_email'),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(value => {
          if (value) {
            API.post('/setup/deleteemailsettings', { supportEmailId: id }).then(
              ({ error }) => {
                if (error) {
                  this.$message.error(
                    error?.message || this.$t('common._common.deletion_failed')
                  )
                } else {
                  this.$message.success(
                    this.$t('common._common.deleted_successfully')
                  )
                  this.loadnotifications()
                }
              }
            )
          }
        })
    },
    generateActionName(rule) {
      if (!isEmpty(rule.actions)) {
        let template = rule.actions[0].template
        return 'Create ' + template.name
      } else {
        return 'Create Record'
      }
    },
  },
}
</script>
