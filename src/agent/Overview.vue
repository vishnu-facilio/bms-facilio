<template>
  <div class="width100">
    <router-view v-if="childRoute"></router-view>
    <div v-else class="page-width-cal fc-agent-overview-page">
      <div
        v-if="loading"
        class="flex-middle fc-empty-white m10 fc-agent-empty-state clearboth"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(agentoverview) && !loading"
        class="height100vh fc-empty-white flex-middle justify-content-center flex-direction-column fc-agent-empty-width"
      >
        <inline-svg
          src="svgs/emptystate/approval"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="nowo-label">
          No agent Available
        </div>
        <el-button class="fc-agent-add-btn mT20" @click="agentForm">
          <i class="el-icon-plus pR5 fwBold"></i>
          {{ $t('agent.agent.add_agent') }}
        </el-button>
      </div>

      <div v-if="!$validation.isEmpty(agentoverview) && !loading">
        <el-header height="80" class="agent-overview-header">
          <div class="flex-middle width100 justify-content-space">
            <div class>
              <div class="fc-black4-26 flex-middle">
                <div>
                  <b>{{ $account.org.name }}</b>
                </div>
              </div>
            </div>
            <div class="fc-red-new bold pL10 fc-text-blink">
              <div v-if="total">
                <span class="f25">{{ total }}</span>
                {{ $t('agent.agent.agent_offline') }}
              </div>
            </div>
            <div class="fR width20 text-right">
              <el-button class="fc-agent-add-btn" @click="agentForm">
                <i class="el-icon-plus pR5 fwBold"></i>
                {{ $t('agent.agent.add_agent') }}
              </el-button>
            </div>
          </div>
        </el-header>
        <!-- overview card con -->

        <div class="fc-agent-overview-container clearboth">
          <div class="fc-agent-overview-card-con">
            <div class="fc-agent-overview-card fc-agent-card1">
              <router-link :to="{ path: resolvePath('agents') }">
                <div class="flex-middle justify-content-space">
                  <div>
                    <div class="fc-white-16">
                      {{ $t('agent.agent.agents') }}
                    </div>
                  </div>
                  <div class>
                    <inline-svg
                      src="svgs/agent/member-ico"
                      iconClass="icon text-center icon-xxllg"
                    ></inline-svg>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT10">
                  <div
                    class="fc-agent-white-txt2 flex-align-baseline"
                    :class="
                      fontsize(
                        agentoverview.agent.active + agentoverview.agent.total
                      )
                    "
                  >
                    <div class="pR5">
                      {{
                        agentoverview && agentoverview.agent
                          ? agentoverview.agent.active
                          : 0
                      }}
                    </div>
                    <div>
                      /
                      {{
                        agentoverview && agentoverview.agent
                          ? agentoverview.agent.total
                          : 0
                      }}
                      <span class="f12 pL5 fwBold">{{
                        $t('agent.agent.online')
                      }}</span>
                    </div>
                  </div>
                </div>
              </router-link>
            </div>
            <div class="fc-agent-overview-card fc-agent-card2">
              <router-link :to="resolvePath('controller')">
                <div class="flex-middle justify-content-space">
                  <div class="fc-white-16">
                    {{ $t('agent.agent.controllers') }}
                  </div>
                  <div class>
                    <inline-svg
                      src="svgs/agent/controller-ico"
                      iconClass="icon text-center icon-xxllg"
                    ></inline-svg>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT10">
                  <div
                    class="fc-agent-white-txt2 flex-align-baseline"
                    :class="fontsize(agentoverview.controller.configured + '')"
                  >
                    {{
                      agentoverview && agentoverview.controller
                        ? agentoverview.controller.configured
                        : 0
                    }}
                  </div>
                </div>
              </router-link>
            </div>
            <div class="fc-agent-overview-card fc-agent-card3">
              <router-link :to="{ path: resolvePath('points') }">
                <div class="flex-middle justify-content-space">
                  <div class="fc-white-16 pT10">
                    {{ $t('agent.agent.points') }}
                  </div>
                  <div class>
                    <inline-svg
                      src="svgs/agent/points-ico"
                      iconClass="icon text-center icon-xxllg"
                    ></inline-svg>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT10">
                  <div
                    class="fc-agent-white-txt2 flex-align-baseline"
                    :class="
                      fontsize(
                        agentoverview.points.configured +
                          agentoverview.points.total
                      )
                    "
                  >
                    {{
                      agentoverview && agentoverview.points
                        ? agentoverview.points.configured
                        : 0
                    }}
                    /
                    {{
                      agentoverview && agentoverview.points
                        ? agentoverview.points.total
                        : 0
                    }}
                    <span class="f12 pL5 fwBold">
                      {{ $t('agent.agent.configured') }}</span
                    >
                  </div>
                </div>
              </router-link>
            </div>
            <div class="fc-agent-overview-card fc-agent-card4">
              <router-link :to="{ path: resolvePath('commissioning') }">
                <div class="flex-middle justify-content-space">
                  <div class="fc-white-16 pT10">
                    {{ $t('agent.agent.commissioned') }}
                  </div>
                  <div class>
                    <inline-svg
                      src="svgs/agent/commission"
                      iconClass="icon text-center icon-xxllg"
                    ></inline-svg>
                  </div>
                </div>
                <div class="flex-middle justify-content-space pT10">
                  <div
                    class="fc-agent-white-txt2 flex-align-baseline"
                    :class="fontsize(agentoverview.points.commissioned + '')"
                  >
                    {{
                      agentoverview && agentoverview.points
                        ? agentoverview.points.commissioned
                        : 0
                    }}
                    <span class="f12 pL5 fwBold">{{
                      $t('agent.agent.points')
                    }}</span>
                  </div>
                </div>
              </router-link>
            </div>
          </div>
          <!-- grpah container -->
          <div class="fc-agent-graph-con height550">
            <common-widget-chart
              v-if="chartWidget.widgetParams.chartParams"
              key="overview"
              moduleName="agentMetrics"
              isWidget="true"
              :widget="this.chartWidget"
              showPeriodSelect="true"
              type="area"
            >
              <template slot="title">
                {{ $t('agent.agent.agent_message') }}</template
              >
            </common-widget-chart>
          </div>
          <!-- white card section -->
          <!-- <div class="fc-agent-overview-card-con">
            <div
              class="fc-agent-white-card p30 bR3"
              style="background: #f65d81;"
            >
              <div class="fc-grey5-13 text-uppercase">
                {{ $t('agent.agent.subscribed') }}
              </div>
              <div class="fc-agent-black48 pT10">
                {{
                  agentoverview && agentoverview.points
                    ? agentoverview.points.subscribed
                    : 0
                }}
                <span class="f14 pL5 fwBold">{{
                  $t('agent.agent.points')
                }}</span>
              </div>
            </div>
            <div
              class="fc-agent-white-card p30 bR3"
              style="background: #3ea5fd;"
            >
              <router-link :to="{ path: resolvePath('integrations') }">
                <div class="fc-grey5-13 text-uppercase">
                  {{ $t('agent.agent.integration') }}
                </div>
                <div class="fc-agent-black48 pT10">
                  {{ integrations }}
                  <span class="f14 pL5 fwBold">{{
                    $t('agent.agent.apps')
                  }}</span>
                </div>
              </router-link>
            </div>
            <div
              class="fc-agent-white-card p30 bR3"
              style="background: #4ed88f;"
            >
              <div class="fc-grey5-13 text-uppercase">
                {{ $t('agent.agent.sites') }}
              </div>
              <div class="fc-agent-black48 pT10">
                {{
                  agentoverview && agentoverview.agent
                    ? agentoverview.agent.sites
                    : 0
                }}
                <span class="f14 pL5 fwBold">{{
                  $t('agent.agent.sites')
                }}</span>
              </div>
            </div>
          </div> -->
        </div>
      </div>
      <div class="help-icon-position pointer" @click="helpDialogOpen()">
        <inline-svg
          src="svgs/question"
          class="vertical-middle"
          iconClass="icon icon-xxlll"
        ></inline-svg>
      </div>
      <div class="help-icon-position pointer"></div>
      <agent-help
        v-if="helpDialogVisible"
        :visibility.sync="helpDialogVisible"
      ></agent-help>
      <div>
        <NewAgent
          v-if="showDialog"
          :isNew="isNew"
          :model="agent"
          :visibility.sync="showDialog"
          @saved="loadOverview"
        >
        </NewAgent>
      </div>
    </div>
  </div>
</template>
<script>
import NewAgent from 'agent/components/AgentAddForm'
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'
import AgentHelp from 'src/agent/AgentHelperDocs/HelpAgentOverview'
import {
  findRouteForTab,
  tabTypes,
  getApp,
  pageTypes,
  findRouteForModule,
} from '@facilio/router'

export default {
  title() {
    return 'Agents Overview'
  },
  props: ['childRoute'],
  data() {
    return {
      agentoverview: [],
      loading: false,
      showDialog: false,
      isNew: true,
      agent: null,
      total: null,
      helpDialogVisible: false,
      integrations: null,
      chartWidget: {
        widgetParams: {
          chartParams: null,
        },
      },
    }
  },
  components: {
    NewAgent,
    CommonWidgetChart,
    AgentHelp,
  },
  mounted() {
    this.loadOverview()
  },
  methods: {
    resolvePath(configType) {
      let appName = getApp().linkName
      if (configType === 'integrations') {
        let { path } =
          findRouteForTab(tabTypes.CUSTOM, {
            config: {
              type: 'overview',
            },
          }) || {}
        return `/${appName}/${path}/${configType}`
      } else if (configType === 'controller') {
        let { name } = findRouteForModule(configType, pageTypes.LIST) || {}
        if (name) {
          return {
            name,
            params: {
              viewname: 'all',
            },
          }
        }
      } else {
        let { path } =
          findRouteForTab(tabTypes.CUSTOM, {
            config: {
              type: configType,
            },
          }) || {}
        if(configType === 'commissioning' || (configType === 'agents')){
           return `/${appName}/${path}`
        }
        return `/${appName}/${path}?agentPointsTab=configured`
      }
    },
    helpDialogOpen() {
      this.helpDialogVisible = true
    },
    loadOverview() {
      this.loading = true
      this.$http
        .get('/v2/agent/overview')
        .then(response => {
          this.loading = false
          if (response.data.result.data) {
            this.integrations = response.data.result.data.integrations
            this.total =
              response.data.result.data.agent.total -
              response.data.result.data.agent.active
          }
          this.agentoverview = response.data.result.data
            ? response.data.result.data
            : []
          if (response.data.result.data.chartParams) {
            this.chartWidget.widgetParams.chartParams = this.agentoverview.chartParams
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    agentForm() {
      this.showDialog = true
    },
    fontsize(value) {
      if (!value) {
        return 'f36'
      }
      value = value + ''
      if (value.length < 3) {
        return 'f36'
      } else if (value.length < 6) {
        return 'f23'
      } else if (value.length < 8) {
        return 'f16'
      } else if (value.length < 10) {
        return 'f12'
      } else if (value.length < 10) {
        return 'f11'
      } else {
        return 'f10'
      }
    },
  },
}
</script>

<style lang="scss">
.fc-agent-overview-page {
  .agent-overview-header {
    padding: 30px 20px;
  }

  .fc-agent-overview-container {
    width: calc(100% - 40px);
    height: calc(100vh - 150px);
    overflow-x: hidden;
    overflow-y: scroll;
    flex: 0 0 100%;
    margin-left: 20px;
    padding-bottom: 50px;
  }

  .fc-agent-white-card {
    .fc-grey5-13,
    .fc-agent-black48,
    .f14 {
      color: #ffffff !important;
    }
  }

  .analytics-section .chart-icon {
    margin-top: 10px;

    .el-select .el-input .el-select__caret {
      line-height: 20px;
    }
  }

  .height550 {
    height: 550px !important;
  }
}
</style>
