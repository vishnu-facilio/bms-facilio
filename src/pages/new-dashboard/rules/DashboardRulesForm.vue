<template>
  <el-dialog
    :visible="true"
    :fullscreen="true"
    :append-to-body="true"
    custom-class="fc-dialog-form fc-dialog-right custom-rule-dialog setup-dialog40 setup-dialog dashboard-rules-form"
    style="z-index: 1999"
  >
    <div v-if="loading" class="flex-middle height-100">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <el-form
      v-else
      :model="dashboardRules"
      ref="ruleForm"
      :rules="rules"
      :label-position="'top'"
    >
      <div
        class="new-header-container mR30 pL30 new-header-modal new-header-text"
      >
        <div class="setup-modal-title">
          {{ isNew ? 'Edit' : 'Create' }}
        </div>
      </div>
      <div class="new-body-modal pL30 pR30">
        <el-row>
          <el-form-item>
            <p class="fc-input-label-txt">
              Dashboard Name
            </p>

            <el-input
              placeholder="Enter the name"
              :autofocus="true"
              :disabled="true"
              v-model="dashboardName"
              class="width100 pR20 fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row v-if="dashboardTabName">
          <el-form-item>
            <p class="fc-input-label-txt">
              Dashboard Tab Name
            </p>

            <el-input
              placeholder="Enter the name"
              :autofocus="true"
              :disabled="true"
              v-model="dashboardTabName"
              class="width100 pR20 fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item prop="name">
            <p class="fc-input-label-txt">
              Rule Name
            </p>

            <el-input
              placeholder="Enter the name"
              :autofocus="true"
              v-model="dashboardRules.name"
              class="width100 pR20 fc-input-full-border2"
            ></el-input>
          </el-form-item>
        </el-row>
        <el-row>
          <el-form-item>
            <p class="fc-input-label-txt">
              Description
            </p>
            <el-input
              v-model="dashboardRules.desc"
              :min-rows="1"
              placeholder="Enter description"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              class="fc-input-full-border-select2 width100"
              resize="none"
            ></el-input>
          </el-form-item>
        </el-row>

        <p class="fc-modal-sub-title">
          {{ $t('maintenance._workorder.execute_on') }}
        </p>
        <p class="small-description-txt mB10">
          {{ $t('setup.setupLabel.notify_rule_executed') }}
        </p>
        <el-row class="mb10">
          <el-col :span="14">
            <el-form-item label="Trigger Type" class="mb10">
              <el-select
                v-model="dashboardRules.trigger_type"
                placeholder="On Field Update"
                class="fc-input-full-border2 width100"
                filterable
              >
                <el-option
                  v-for="(triggerTypeName, triggerType) in triggerTypeList"
                  :key="`actionType-${triggerType}`"
                  :label="triggerTypeName"
                  :value="parseInt(triggerType)"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row class="mB10" v-if="dashboardRules.trigger_type == 2">
          <el-col :span="14">
            <el-form-item label="Trigger Widget" class="mB10" :required="true">
              <el-select
                v-model="triggerWidget"
                placeholder="Select the field"
                class="fc-input-full-border2 width100 fc-tag"
                @change="currentWidgetSelected"
                filterable
                :multiple="false"
                collapse-tags
              >
                <el-option
                  v-for="(targetWidget, triggerType) in triggerWidgetList"
                  :key="`actionType-${triggerType}`"
                  :label="targetWidget.label"
                  :value="targetWidget.value"
                >
                </el-option>
              </el-select>
              <div style="display: flex;flex-direction: column;line-height: 25px;">
              <span v-if="triggerWidgetLinkName" style="opacity:0.7;">{{'widgetId : #'+ triggerWidget }}</span>
              <span v-if="triggerWidgetLinkName" style="opacity:0.7;">{{'placeholder : ${' + triggerWidgetLinkName +'}'}}</span>
            </div>
            </el-form-item>
          </el-col>
        </el-row>
        <div v-if="loadingCriteria" class="flex-middle height-100">
          <spinner :show="loadingCriteria" size="80"></spinner>
        </div>
        <el-row v-else class="mT30">
          <div class="mB20" v-if="showCriteriaBuilder">
            <label class="fc-modal-sub-title">{{
              $t('forms.rules.criteria')
            }}</label>

            <el-switch
              v-model="isCriteriaBuilderRequired"
              class="Notification-toggle mL30"
              active-color="rgba(57, 178, 194, 0.8)"
              inactive-color="#e5e5e5"
            ></el-switch>
          </div>
          <el-col :span="24" v-if="isCriteriaBuilderRequired">
            <div>
              <div class="fc-sub-title-desc">
                {{ $t('forms.rules.specify_criteria') }}
              </div>
            </div>
            <el-form-item>
              <CriteriaBuilder
                v-model="criteria"
                :moduleName="moduleName"
                v-if="!loadingCriteria"
              />
              <div class="line-height18">
                <p v-if="isCriteriaConditionEmpty" class="label-txt-red">
                  Please add valid criteria
                </p>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <div>
          <p class="subHeading-pink-txt">
            {{ $t('setup.approvalprocess.action') }}
          </p>
          <p class="small-description-txt">
            {{ $t('setup.setupLabel.set_corresponding_rule') }}
          </p>
          <DashboardRulesActionConfigure
            :widgets="triggerWidgetList"
            :type="1"
            :preExistingRule="preExistingRule"
            :dashboardRules="dashboardRules.actions"
            :triggerWidgetPlaceholders="triggerWidgetPlaceholders"
            @actions="actions"
          />
          <DashboardRulesActionConfigure
            :type="2"
            @actions="actions"
            :preExistingRule="preExistingRule"
            :dashboardRules="dashboardRules.actions"
          />
          <DashboardRulesActionConfigure
            :type="3"
            @actions="actions"
            :preExistingRule="preExistingRule"
            :dashboardRules="dashboardRules.actions"
          />
          <DashboardRulesActionConfigure
            :type="5"
            @actions="actions"
            :preExistingRule="preExistingRule"
            :dashboardRules="dashboardRules.actions"
          />
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-form-item style="margin-bottom:0px">
          <el-button
            @click="closeFormDialog()"
            class="modal-btn-cancel text-uppercase"
            >{{ $t('common._common.cancel') }}</el-button
          >
          <el-button
            type="primary"
            class="modal-btn-save"
            :loading="saving"
            @click="saveRules()"
          >
            {{
              saving ? $t('common._common._saving') : $t('common._common._save')
            }}
          </el-button>
        </el-form-item>
      </div>
    </el-form>
  </el-dialog>
</template>
<script>
import { CriteriaBuilder } from '@facilio/criteria'
import { API } from '@facilio/api'
import { isEmpty, isInteger } from '@facilio/utils/validation'
import DashboardRulesActionConfigure from './DashboardRulesActionConfigure'
const TRIGGER_TYPES = {
  1: 'Form on load',
  2: 'On widget update',
}
export default {
  props: [
    'isNew',
    'currentDashboard',
    'currentDashboardTab',
    'triggerWidgetList',
    'presentDashboardRule',
  ],
  components: {
    CriteriaBuilder,
    DashboardRulesActionConfigure,
  },
  data() {
    return {
      dashboardRules: {
        status: true,
        id: -1,
        trigger_type: 2,
        trigger_widgets: [
          {
            criteria: null,
            trigger_widget_id: null,
            moduleName: null,
          },
        ],
        actions: [],
      },
      triggerWidgetPlaceholders:[],
      loadingCriteria: false,
      triggerWidgetLinkName:null,
      triggerTypeList: TRIGGER_TYPES,

      moduleName: null,
      showCriteriaBuilder: false,
      isCriteriaBuilderRequired: false,
      dashboardName: null,
      triggerWidget: null,
      dashboardTabName:null,
      dashboardTabId: null,
      criteria: null,
      saving: false,
      loading: false,
      isCriteriaConditionEmpty: false,
      rules: {
        name: [
          {
            required: true,
            message: 'Please enter a rule name',
            trigger: 'blur',
          },
        ],
      },
    }
  },
  created() {
    this.loadDashboard()
    if (this.isNew) this.loading = true
    this.init()
  },

  watch: {
    presentDashboardRule() {
      if (this.presentDashboardRule) {
        this.dashboardRules = this.presentDashboardRule
        this.loadPresentDashboardRule(this.presentDashboardRule)
      }
    },
  },

  computed: {
    preExistingRule() {
      return !isEmpty(this.presentDashboardRule)
    },
  },
  methods: {
    init(){
      if(this.currentDashboardTab)
      {
        this.dashboardTabName = this.currentDashboardTab.name
        this.dashboardRules.dashboardTabId = this.currentDashboardTab.id
      }
    },
    loadPresentDashboardRule(dashboardrule) {
      if (!isEmpty(dashboardrule.trigger_widgets)) {
        this.triggerWidget = dashboardrule.trigger_widgets[0].trigger_widget_id
        this.criteria = dashboardrule.trigger_widgets[0].criteria
        if (!isEmpty(this.criteria)) this.isCriteriaBuilderRequired = true
      } else {
        dashboardrule.trigger_widgets = [
          {
            criteria: null,
            trigger_widget_id: null,
            moduleName: null,
          },
        ]
      }

      this.currentWidgetSelected('edit')
      this.loading = false
    },
    actions(val, val1) {
      let added = true
      if (!isEmpty(this.dashboardRules.actions)) {
        this.dashboardRules.actions.map(action => {
          if (action.type === val.type && action.type === 1) {
            action.target_widgets = val.target_widgets
            return true
          } else if (action.type === val.type && action.type === 2) {
            action.action_meta.action_detail.url =
              val.action_meta.action_detail.url
            return true
          } else if (action.type === val.type && action.type === 3) {
            action.action_meta.action_detail.section_ids =
              val.action_meta.action_detail.section_ids

            return true
          }
          else if(action.type === val.type && action.type === 5)
          {
            action.action_meta.script =
              val.action_meta.script
              return true
          }
          else if (val.id === undefined && added) {
            this.dashboardRules.actions.push(val)
            added = false
            return true
          }
          else return false
        })
        added = true
        this.dashboardRules.actions.map(action => {
          if (action.type === val1.type && action.type === 4) {
            action.action_meta.action_detail.section_ids =
              val1.action_meta.action_detail.section_ids
            return true
          } else if (val1.id === undefined && added) {
            this.dashboardRules.actions.push(val1)
            added = false
            return true
          } else return false
        })
      } else this.dashboardRules.actions.push(val, val1)
      // console.log(this.dashboardRules.actions)

      this.dashboardRules.actions = this.dashboardRules.actions.filter(
        action => {
          if (action === undefined) return false
          else if (action.type === 1 && action.target_widgets !== null) {
            return action
          } else if (
            action.type === 2 &&
            action.action_meta.action_detail.url !== null
          ) {
            return action
          } else if (
            action.type === 3 &&
            action.action_meta.action_detail.section_ids !== null
          ) {
            return action
          } else if (
            action.type === 4 &&
            action.action_meta.action_detail.section_ids !== null
          ) {
            return action
          } else if(action.type === 5 &&
            action.action_meta.script !== null){
              return action
          }
          else return false
        }
      )
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      }
      let conditions = criteria.conditions
      for (const key in conditions) {
        if (Number.isInteger(Number.parseInt(key))) {
          let condition = conditions[key]
          if (
            isEmpty(condition) ||
            isEmpty(condition.operatorId) ||
            isEmpty(condition.fieldName)
          ) {
            return false
          }
        }
      }
      return true
    },
    async saveRules() {
      if (this.isCriteriaBuilderRequired) {
        this.isCriteriaConditionEmpty = !this.isValidCriteria(this.criteria)
      }

      if (!this.isCriteriaConditionEmpty || !this.isCriteriaBuilderRequired) {
        if (!this.isCriteriaBuilderRequired) this.criteria = null
        await this.$refs['ruleForm'].validate(async valid => {
          if (valid) {
            this.saving = true
            let { dashboardRules } = this

            if (!isEmpty(this.triggerWidget)) {
              dashboardRules.trigger_widgets[0].trigger_widget_id = this.triggerWidget
              dashboardRules.trigger_widgets[0].criteria = this.criteria
              dashboardRules.trigger_widgets[0].moduleName = this.moduleName
            }

            if (isEmpty(this.triggerWidget))
              dashboardRules.trigger_widgets = null

            let params = {}
            params.dashboard_rule = dashboardRules
            let url
            if (this.isNew) {
              url = '/v3/dashboard/rule/update'
            } else {
              url = '/v3/dashboard/rule/create'
            }

            let { error } = await API.post(url, params)
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else if (this.isNew) {
              this.$message.success('Rule updated successfully.')
            } else this.$message.success('Rule added successfully.')
            this.saving = false
            this.$emit('createdRule')
          }
        })
      }
    },
    closeFormDialog() {
      this.$emit('closeDialog')
    },
    loadDashboard() {
      let { currentDashboard } = this
      let id = currentDashboard.id
      this.dashboardName = currentDashboard.label
      this.dashboardRules.dashboardId = id
    },

    async currentWidgetSelected(type) {
      this.loadingCriteria = true
      if (type !== 'edit') this.criteria = null

      let selectedWidget = this.triggerWidgetList.find(widget => {
        if (widget.value === this.triggerWidget) {
          this.triggerWidgetLinkName = !isEmpty(widget.linkName) ? widget.linkName : null
          return widget
        }
        return false
      })

      if(!isEmpty(selectedWidget.module) && !isEmpty(selectedWidget) && selectedWidget.widgetType == 'FILTER')
      {
          let url = `v3/placeholders/`+ selectedWidget.module
          let { data } = await API.get(url)
          let {
            placeholders: { fields: fields, moduleFields: moduleFields },
          } = data
          this.options = fields
          this.options.forEach((field, index) => {
            let { module } = field
            if (!isEmpty(module)) {
              let children = moduleFields[module]
              this.options[index] = { ...field, children }
            }
          })
          let placeHolders=[]
          this.options.forEach(option =>{
            let { children } = option
            if (children) {
              children.forEach(field =>{
                placeHolders.push( {label : field.displayName +'('+selectedWidget.module + '('+option.module+'))', value: this.setTriggerPlaceHolders(field, selectedWidget.linkName+'.value', option.name) })
              })
            } else {
              placeHolders.push({label : option.displayName +'('+selectedWidget.module+')', value: this.setTriggerPlaceHolders(option, selectedWidget.linkName+'.value') })
            }
          })
          this.triggerWidgetPlaceholders = placeHolders
      }
      if (!isEmpty(selectedWidget)) {
        if (!isEmpty(selectedWidget.reportId)) {
          let { data } = await API.get('v3/report/execute', {
            reportId: selectedWidget.reportId,
          })
          let { module } = data || {}
          this.moduleName = module.name
        } else {
          this.moduleName = selectedWidget.module
        }

        this.showCriteriaBuilder = true
      } else this.showCriteriaBuilder = false
      this.$nextTick(() => {
        this.loadingCriteria = false
      })
    },
    setTriggerPlaceHolders(childOption, module, parentField)
    {
      if (isEmpty(childOption.children)) {
        let placeholderString
        if (!isEmpty(parentField))
            placeholderString = `${parentField}.${childOption.name}`
        else placeholderString = `${childOption.name}`
        placeholderString = `\${${module}.${placeholderString}}`
        return placeholderString
      }
    },
  },

}
</script>
<style lang="scss">
.dashboard-rules-form {
  .el-dialog__body {
    height: 100% !important;
  }
}
</style>
