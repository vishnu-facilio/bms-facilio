<template>
  <div>
    <div class="dashboard-rules-container" v-if="isDashboardRulesEnabled">
      <div class="action-btn setting-page-btn create-button">
        <div>
          <p class="setting-form-title text-capitalize dashboard-heading">
            Dashboard Rules
          </p>
          <p class="heading-description text-capitalize">Description</p>
        </div>
        <div>
          <el-button
            size="medium"
            class="plain f13"
            @click="$router.back()"
            style="height: 42px !important;"
          >
            CANCEL
          </el-button>
          <el-button type="primary" class="setup-el-btn" @click="openForm">
            ADD RULES
          </el-button>
        </div>
      </div>
      <div v-if="loading" class="flex-middle height-100">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else class="container-scroll mT30 pB30 p20">
        <el-table
          :data="dashboardRuleList"
          height="100%"
          :header-cell-style="{ background: '#f3f1fc' }"
          empty-text="No rules is added"
          class="form-list-table overflow-y-scroll "
        >
          <el-table-column
            prop="name"
            label="Name"
            width="260"
          ></el-table-column>
          <el-table-column
            prop="desc"
            label="Description"
            width="290"
          ></el-table-column>
          <el-table-column label="Trigger Widget" width="370">
            <template v-slot="dashboardRule">
              {{
                getTargetWidgetName(dashboardRule.row.trigger_widgets[0]) ||
                  '---'
              }}
            </template>
          </el-table-column>
          <el-table-column label="Status" width="300">
            <template v-slot="dashboardRule">
              <el-switch
                v-model="dashboardRule.row.status"
                @change="
                  () => {
                    enableDisabledRule(dashboardRule)
                  }
                "
                class="Notification-toggle"
                active-color="rgba(57, 178, 194, 0.8)"
                inactive-color="#e5e5e5"
              ></el-switch>
            </template>
          </el-table-column>
          <el-table-column fixed="right">
            <template v-slot="dashboardRule">
              <div class=" d-flex">
                <i
                  class="el-icon-edit pointer visibility-hide-actions mL10"
                  @click="editRule(dashboardRule.row.id)"
                  data-arrow="true"
                  title="Edit Rule"
                  v-tippy
                ></i>
                <i
                  class="el-icon-delete pointer visibility-hide-actions mL10"
                  @click="deleteRuleConfirm(dashboardRule.row.id)"
                  data-arrow="true"
                  title="Delete Rule"
                  v-tippy
                ></i>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <DashboardRulesForm
        v-if="openRulesForm"
        :isNew="isNew"
        :currentDashboard="currentDashboardDetails"
        :currentDashboardTab="currentDashboardTab"
        :triggerWidgetList="triggerWidgetList"
        :presentDashboardRule="presentDashboardRule"
        @closeDialog="closeForm"
        @createdRule="loadRulesList"
      />
    </div>
    <div v-else>
      Please enable license for dashboard rules
    </div>
  </div>
</template>
<script>
import DashboardRulesForm from './DashboardRulesForm'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
export default {
  components: {
    DashboardRulesForm,
  },
  data() {
    return {
      openRulesForm: false,
      dashboardRuleList: null,
      loading: false,
      isNew: false,
      currentDashboardDetails: null,
      currentDashboardTab:null,
      dashboardId: null,
      triggerWidgetList: [],
      widgets: null,
      presentDashboardRule: {},
    }
  },
  created() {
    this.loadWidgetsList()
  },
  computed: {
    isDashboardRulesEnabled() {
      return this.$helpers.isLicenseEnabled('DASHBOARD_ACTION') ?? false
    },
  },
  methods: {
    dashboardTabId(){
      return !isEmpty(this.$route.query) && !isEmpty(this.$route.query.tabId) ? this.$route.query.tabId : null
    },
    enableDisabledRule({ row: rule }) {
      API.post('/v3/dashboard/rule/update', { dashboard_rule: rule }).then(
        ({ error, data }) => {
          if (!isEmpty(error)) {
            // API failed, reset optimistic update.
            this.$message.error(error.message)
            rule.status = !rule.status
          }
        }
      )
    },
    async loadWidgetsList() {
      this.loading = true
      let currentDashboardLinkName = this.$route.params.dashboardlink
      let { data, error } = await API.get(
        `/dashboard/${currentDashboardLinkName}`
      )
      if (!error) {
        let { dashboardJson } = data || {}
        if (!isEmpty(dashboardJson)) {
          this.currentDashboardDetails = dashboardJson[0]
          this.dashboardId = dashboardJson[0].id
          if(this.dashboardTabId()){
            let self = this
            this.currentDashboardDetails.tabs.forEach( tab => {
                if(tab.id == Number(this.dashboardTabId())){
                  this.currentDashboardTab = cloneDeep(tab)
                }
            })
          }
          this.getTargetWidgetsList()
        }
      }
    },
    async getTargetWidgetsList() {
      let { dashboardId } = this
      let url = '/v3/dashboard/rule/widgets/' + dashboardId
      if(this.dashboardTabId()){
        url = '/v3/dashboard/rule/widgets/tab/' + this.dashboardTabId()
      }
      let { data } = await API.get(url)
      this.widgets = data.widgets

      this.triggerWidgetList = this.widgets.map(widget => {
        let widgetDetails = {}
        if (widget.widgetType == 'FILTER') {
          widgetDetails.label = widget.label
          widgetDetails.value = widget.id
          widgetDetails.module = !isEmpty(widget.moduleName)
            ? widget.moduleName
            : widget?.field?.module?.name
        } else if (widget.widgetType == 'CHART') {
          widgetDetails.label = widget.headerText
          widgetDetails.value = widget.id
          widgetDetails.reportType = widget.reportType
          widgetDetails.reportId = widget.newReportId
        } else if (widget.widgetType == 'CARD') {
          widgetDetails.label = widget.cardParams.title
          widgetDetails.value = widget.id
          widgetDetails.module = widget.cardParams?.reading?.moduleName
        } else if (widget.widgetType == 'LIST_VIEW') {
          widgetDetails.label = widget.headerText
          widgetDetails.value = widget.id
          widgetDetails.module = widget.moduleName
        }
        if (!isEmpty(widget.linkName) && !isEmpty(widgetDetails)) {
          widgetDetails.linkName = widget.linkName
        }
        widgetDetails.widgetType = widget.widgetType
        return widgetDetails
      })

      this.loadRulesList()
    },
    async loadRulesList() {
      this.openRulesForm = false

      let { dashboardId } = this
      let url = `/v3/dashboard/rule/${dashboardId}/list`
      if(this.dashboardTabId())
      {
        url = url +"/"+ this.dashboardTabId()
      }
      let { data, error } = await API.get(url)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { dashboard_rule } = data || {}
        this.dashboardRuleList = dashboard_rule
      }
      this.loading = false
    },

    openForm() {
      this.openRulesForm = true
      this.isNew = false
      this.presentDashboardRule = {}
    },
    closeForm() {
      this.openRulesForm = false
    },
    getTargetWidgetName(triggerWidget) {
      let { trigger_widget_id } = triggerWidget || {}

      let triggerWidgetName = this.triggerWidgetList.find(
        widget => widget.value === trigger_widget_id
      )
      let { label } = triggerWidgetName || {}
      return label
    },
    async editRule(id) {
      this.openRulesForm = true
      this.isNew = true
      let url = '/v3/dashboard/rule/' + id
      let { error, data } = await API.get(url)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { dashboard_rule } = data || {}
        this.presentDashboardRule = dashboard_rule
      }
    },
    deleteRuleConfirm(id) {
      let dialogObj = {
        title: 'Delete Rule',
        htmlMessage: 'Are you sure want to delete rule?',
        rbDanger: true,
        rbLabel: 'Confirm',
      }
      this.$dialog.confirm(dialogObj).then(value => {
        if (value) {
          this.deleteRule(id)
        }
      })
    },
    async deleteRule(id) {
      let url = '/v3/dashboard/rule/delete/' + id
      let { error } = await API.delete(url)
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else this.$message.success('Rule deleted successfully.')
      this.loadRulesList()
    },
  },
}
</script>
<style scoped lang="scss">
.dashboard-rules-container {
  height: 100%;
  width: 100%;
  .create-button {
    width: 100%;
    display: flex;
    justify-content: space-between;
    padding: 10px 15px;
    background-color: #ffffff;
    .dashboard-heading {
      margin: 0px !important;
    }
  }
  .setting-list-view-table .setting-table-th {
    vertical-align: middle;

    border: none;
  }
}
</style>
<style lang="scss">
.form-list-table .el-table__cell {
  padding-left: 20px !important;
}
</style>
