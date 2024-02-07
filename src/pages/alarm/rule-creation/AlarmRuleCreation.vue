<template>
  <div
    :class="{
      'formbuilder-fullscreen-popup sla-policy-container mL0 mT0': isV2,
      'formbuilder-fullscreen-popup sla-policy-container': !isV2,
    }"
    ref="formbuilder"
  >
    <div class="setting-header">
      <div v-if="!isNew">
        <div class="pointer fc-link fw-normal f13" @click="goBack">
          <inline-svg
            src="left-arrow"
            iconClass="icon icon-sm vertical-text-top mR5"
          ></inline-svg>
          {{ $t('setup.setupLabel.go_back') }}
        </div>
        <div class="mT10 mB5 f22 fw3 letter-spacing0_5">
          {{ title }}
        </div>
      </div>
      <div class="d-flex flex-direction-column" v-else>
        <div class="mT10 mB10 f22 fw3 letter-spacing0_5">{{ title }}</div>
      </div>
      <div :class="{ isV2: 'fR', 'fR stateflow-btn-wrapper': !isV2 }">
        <async-button
          :clickAction="goBack"
          :class="{ 'v2-cancel-color ': isV2, 'asset-el-btn': !isV2 }"
        >
          {{ $t('common._common.cancel') }}
        </async-button>
        <async-button
          button="primary"
          :class="{ 'v2-save-color mL20': isV2, 'asset-el-btn': !isV2 }"
          :clickAction="save"
        >
          {{ $t('common._common._save') }}
        </async-button>
      </div>
    </div>
    <div class="d-flex setup-grey-bg">
      <div class="sla-sidebar pT10">
        <a
          id="ruledetails-link"
          @click="scrollTo('ruledetails')"
          class="sla-link active"
        >
          {{ $t('alarm.rules.rule_details') }}
        </a>
        <a
          id="conditions-link"
          @click="scrollTo('conditions')"
          class="sla-link"
        >
          {{ $t('alarm.rules.rule_conditions') }}
        </a>
        <a
          id="alarmdetails-link"
          @click="scrollTo('alarmdetails')"
          class="sla-link"
        >
          {{ $t('alarm.rules.rule_alarm_details') }}
        </a>
        <a id="impact-link" @click="scrollTo('impact')" class="sla-link">
          {{ $t('rule.create.impact') }}
        </a>
        <a
          id="rootcauses-link"
          @click="scrollTo('rootcauses')"
          class="sla-link"
        >
          {{ $t('alarm.rules.rule_root_causes') }}
        </a>
      </div>
      <div class="scroll-container">
        <div v-if="isLoading" class="mT20">
          <spinner :show="isLoading" size="80"></spinner>
        </div>
        <template v-else>
          <RuleDetailsForm
            id="ruledetails-section"
            ref="ruledetails-section"
            :isNew="isNew"
            @ruleDetailsChange="setProperties"
            @sendRuleDetail="setRuleDetail"
            :ruleInfo="alarmRules"
            class="mB20"
          ></RuleDetailsForm>
          <RuleConditions
            id="conditions-section"
            ref="conditions-section"
            class="mB20"
            :moduleName="moduleName"
            :ruleInfo="alarmRules"
            :isEditForm="isEditForm"
            @onConditionsChange="setProperties"
            :isV2="isV2"
          >
          </RuleConditions>
          <AlarmDetails
            id="alarmdetails-section"
            ref="alarmdetails-section"
            class="mB20"
            :ruleDetails="alarmRules"
            @onDetailsChange="setProperties"
            :isV2="isV2"
          ></AlarmDetails>
          <Impact
            id="impact-section"
            ref="impact-section"
            class="mB20"
            @ruleDetailsChange="setProperties"
            :alarmImpactObj="alarmRules"
            :ruleDetail="ruleDetail"
            :isV2="isV2"
          />
          <RootCause
            id="rootcauses-section"
            ref="rootcauses-section"
            class="mB20"
            @ruleDetailsChange="setProperties"
            :alarmRulesObj="alarmRules"
            :isEditForm="isEditForm"
            :moduleName="moduleName"
            :isV2="isV2"
          />
        </template>
        <div class="mT50"></div>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from 'util/validation'
import AsyncButton from '@/AsyncButton'
import SidebarScrollMixin from 'pages/setup/sla/mixins/SidebarScrollMixin'
import RuleDetailsForm from './component/RuleDetailsForm'
import RuleConditions from './component/RuleConditions'
import AlarmDetails from './component/AlarmDetails'
import RootCause from './component/RootCause'
import Impact from './component/ImpactAssociation'
import RuleMixin from '@/mixins/RuleMixin'
import { validateRule, validateFields, validateRca } from './ruleValidation'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import getProperty from 'dlv'

export default {
  name: 'New-SLA',
  props: ['id', 'rule', 'isEdit', 'isV2'],
  mixins: [SidebarScrollMixin, RuleMixin],
  components: {
    AsyncButton,
    RuleDetailsForm,
    RuleConditions,
    AlarmDetails,
    RootCause,
    Impact,
  },
  mounted() {
    this.$nextTick(this.registerScrollHandler)
  },
  async created() {
    let { isEditForm } = this
    // this.$setProperty(this, 'isEditForm', this.isEdit)
    if (isEditForm) {
      await this.prefillRule()
      this.isNew = false
    }
  },
  data() {
    return {
      isLoading: false,
      hasChanged: false,
      rootElementForScroll: '.scroll-container',
      sidebarElements: [
        '#ruledetails-link',
        '#conditions-link',
        '#alarmdetails-link',
        '#impact-link',
        '#rootcauses-link',
      ],
      sectionElements: [
        '#ruledetails-section',
        '#conditions-section',
        '#alarmdetails-section',
        '#impact-section',
        '#rootcauses-section',
      ],
      alarmRules: {},
      ruleList: [],
      ruleDetail: {},
      isNew: true,
    }
  },
  computed: {
    title() {
      let { isNew } = this
      let title = isNew
        ? this.$t('rule.create.new_alarm_rule')
        : this.$t('rule.create.edit_alarm_rule')
      return title
    },
    isEditForm() {
      let id = getProperty(this, '$route.params.id')
      return !isEmpty(id)
    },
    moduleName() {
      return 'newreadingrules'
    },
    moduleDisplayName() {
      return 'NewReadingRule'
    },
    viewname() {
      return this.$route.params.viewname
    },
  },
  watch: {
    rule: {
      async handler(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.alarmRules = this.rule
        }
      },
    },
  },
  methods: {
    async save() {
      let { isEditForm, moduleName, isV2 } = this
      let rule = this.serializeData()
      let { data } = rule || {}
      let { id } = data || {}
      let params = {
        data,
      }
      if (isEditForm) {
        this.$set(params, 'id', id)
      }
      let isValidRule = validateRule(rule, 'readingRule')
      let isValidFormula = validateFields(rule, 'readingRule')
      let isValidRca = validateRca(rule)
      if (isValidRule && isValidFormula && isValidRca) {
        let promise
        if (isEditForm) {
          promise = await API.updateRecord(moduleName, params)
        } else {
          promise = await API.createRecord(moduleName, params)
        }
        let { newreadingrules, error } = await promise

        if (error) {
          this.$message.error(error)
        } else {
          this.ruleList = newreadingrules

          let { id } = newreadingrules
          let { viewname, $route } = this || {}
          let { query } = $route || {}

          if (isV2) {
            console.log(id)
            this.$emit('ruleEvent', { moduleName: moduleName, id, viewname })
          } else if (isWebTabsEnabled()) {
            let { moduleName } = this
            let { name } =
              findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
            name &&
              this.$router.push({
                name,
                params: { id, viewname },
                query,
              })
          } else {
            this.$router.push({
              name: 'newRulesSummary',
              params: { id, viewname },
              query,
            })
          }
        }
      } else {
        if (!isValidFormula) {
          this.$message.error(this.$t('rule.create.variables_must_be_unique'))
        } else if (!isValidRule) {
          this.$message.error(this.$t('rule.create.fill_mandatory_fields'))
        } else {
          this.$message.error(
            this.$t('rule.create.invalid_group_configuration')
          )
        }
      }
    },
    async prefillRule() {
      this.isLoading = true
      let id = getProperty(this, '$route.params.id')
      let params = { id: id }
      try {
        let { newreadingrules } = await API.fetchRecord(this.moduleName, params)
        this.alarmRules = newreadingrules
        this.isLoading = false
      } catch (errorMsg) {
        this.$message.error(errorMsg.message)
      }
    },
    goBack() {
      this.$emit('closeEditForm', true)
      this.$router.go(-1)
    },
    setProperties(value) {
      this.alarmRules = { ...this.alarmRules, ...value }
    },
    serializeData() {
      let { alarmRules } = this
      let {
        actions,
        alertDownTime,
        autoCloseWo,
        createWo,
        readingAlarmRuleContexts,
        ns,
      } = alarmRules || {}
      let { fields } = ns || {}
      fields.forEach(field => this.sanitizeField(field))
      ns = { ...ns, fields }
      alarmRules = { ...alarmRules, ns }
      let workflowRulesForAlarms = []

      // severity change and notification actions
      if (!isEmpty(actions)) {
        workflowRulesForAlarms = [...actions]
      }

      // report downtime action
      if (alertDownTime) {
        let reportDowntime = {
          actions: [
            {
              actionType: 22,
              templateJson: {},
            },
          ],
          name: 'Report Downtime',
          event: { moduleName: 'newreadingalarm', activityType: 1 },
        }
        if (readingAlarmRuleContexts) {
          readingAlarmRuleContexts.forEach(ruleContext => {
            ruleContext.actions = reportDowntime.actions
          })
        } else {
          readingAlarmRuleContexts = [reportDowntime]
        }
      } else {
        readingAlarmRuleContexts = null
      }

      // create and auto close workorder action
      if (createWo) {
        workflowRulesForAlarms.push({
          name: 'Create Wo',
          event: { moduleName: 'newreadingalarm', activityType: 1 },
          actions: [
            {
              actionType: 11,
              templateJson: {},
            },
          ],
        })
        if (autoCloseWo) {
          workflowRulesForAlarms.push({
            name: 'Close Wo',
            event: { moduleName: 'newreadingalarm', activityType: 1 },
            actions: [
              {
                actionType: 12,
                templateJson: {},
              },
            ],
          })
        }
      }
      if (!isEmpty(workflowRulesForAlarms)) {
        alarmRules = { ...alarmRules, workflowRulesForAlarms }
      }

      let { rca } = alarmRules || {}
      if (!isEmpty(rca)) {
        let { groups } = rca || []
        groups.forEach(group => {
          let { id } = group || {}
          if (typeof id === 'string' || id instanceof String) delete group.id
        })
      }
      return { data: alarmRules }
    },
    sanitizeField(field) {
      let excludeFields = [
        'id',
        'selectedLookupField',
        'type',
        'readingsLoading',
        'readingsInitLoading',
        'canShowLookupWizard',
        'relGroupId',
        'varDataType',
      ]
      excludeFields.forEach(excludeField => delete (field || {})[excludeField])
    },
    setRuleDetail(rule) {
      this.ruleDetail = Object.assign({}, rule)
    },
  },
}
</script>
<style lang="scss">
.sla-policy-container {
  border-left: 1px solid #e3e7ed;
  margin-left: 60px;
  margin-top: 50px;
  padding-left: 0px !important;

  .setting-header {
    box-shadow: none;
    border-bottom: 1px solid #e3e7ed;
  }

  .sla-sidebar {
    background-color: #fff;
    min-width: 300px;
    height: 100vh;
    margin-right: 20px;
  }

  .scroll-container {
    flex-grow: 1;
    margin: 20px 20px 0 0;
    overflow-y: scroll;
    max-height: calc(100vh - 150px);
    position: relative;

    > * {
      background-color: #fff;
    }
  }

  .asset-el-btn {
    height: 40px !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
  }

  .section-header {
    font-size: 12px;
    font-weight: 500;
    letter-spacing: 1.6px;
    color: var(--fc-theme-color);
    text-transform: uppercase;
    margin: 0;
    padding: 28px 50px 20px;

    &.anchor-top {
      position: sticky;
      top: 0;
      width: 100%;
      background: #fff;
      z-index: 2;
      box-shadow: 0 2px 3px 0 rgba(233, 233, 226, 0.5);
    }
  }

  .sla-link {
    display: block;
    position: relative;
    padding: 11px 0px 11px 40px;
    margin: 0;
    color: #555;
    font-size: 14px;
    border-left: 3px solid transparent;
    letter-spacing: 0.2px;
    text-transform: capitalize;

    &.active {
      background: #f3f4f7;
    }
  }

  .el-form {
    width: 95%;
    max-width: 998px;
    padding-right: 20%;
  }

  .el-table::before {
    background: initial;
  }
  .el-table th .cell {
    letter-spacing: 1.6px;
    color: #385571;
    padding-left: 0;
  }
  .el-table--border td:first-child .cell {
    padding-left: initial;
  }
  .el-table__empty-block {
    border-bottom: 1px solid #ebeef5;
  }
  .el-table__row .actions {
    visibility: hidden;
  }
  .el-table__row:hover .actions {
    visibility: visible;
  }

  .sla-criteria .fc-modal-sub-title {
    color: #385571;
  }

  .task-add-btn {
    padding: 10px 20px;
    border: 1px solid #39b2c2;
    background-color: #f7feff;
    min-height: 36px;
    &:hover {
      border: 1px solid #39b2c2;
      background-color: #f7feff;
    }
    .btn-label {
      font-size: 12px;
      font-weight: 500;
      color: #39b2c2;
      letter-spacing: 0.5px;
    }
    img {
      width: 9px;
    }
  }
  .disable-save {
    pointer-events: none;
    opacity: 0.4;
  }

  .v2-cancel-color {
    width: 85px;
    height: 35px;
    border-radius: 4px;
    border: solid 1px #c6cdd2;
    text-transform: capitalize;
    color: #1d384e;
    font-weight: 500;
    font-size: 14px;
    margin-left: 15px !important;
    &:hover {
      background-color: #0074d1;
      color: #ffffff;
    }
    &:active {
      color: #324056;
      background-color: #ffffff;
      border: solid 1px #0074d1;
    }
  }
  .v2-save-color {
    width: 85px;
    height: 35px;
    border-radius: 4px;
    border-color: transparent;
    background-color: #0074d1;
    color: #fff;
    font-weight: 500;
    font-size: 14px;
    text-transform: capitalize;
    &:hover {
      background-color: #0074d1;
      color: #ffffff;
    }
    &:active {
      color: #fff;
      background-color: #0074d1;
      border: transparent;
    }
  }
}
</style>
