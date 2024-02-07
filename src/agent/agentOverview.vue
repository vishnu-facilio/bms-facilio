<template>
  <div class="page-width-cal fc-agent-summary-page position-relative">
    <el-header class="fc-agent-main-header z-30" height="130px">
      <div class="flex-middle justify-content-space">
        <div class="flex-middle">
          <div class="fc-agent-blue-txt14 pointer bold" @click="back">
            <i class="el-icon-back bold"></i> {{ $t('agent.agent.back') }}
          </div>
          <el-divider direction="vertical"></el-divider>
          <div class="fc-agent-black-26 pL10">
            {{ agentsOverview.displayName }}
          </div>
        </div>
        <div class="flex-middle">
          <div class="fc-black-16 pT20 poition-relative" style="top: 50px;">
            <inline-svg
              src="svgs/maps"
              class="vertical-middle mR10"
              iconClass="icon icon-sm-md"
            ></inline-svg>

            {{
              agentsOverview.siteId > 0
                ? siteList[agentsOverview.siteId]
                : '---'
            }}
          </div>
        </div>
      </div>
      <div class="pT15 justify-content-space flex-middle">
        <!-- <el-button type="button" class="fc-white-btn" disabled>{{
          $t('agent.agent.jv_stats')
        }}</el-button> -->
        <div class="flex-middle">
          <!-- <a
            v-if="agentsOverview.agentType === 1"
            :href="downloadAgent"
            @click="agentDownload"
            class="fc-white-btn mR10"
            target="_blank"
            >{{ $t('agent.agent.download_agent') }}</a
          > -->
          <template
            v-if="
              agentsOverview.agentType != 3 && agentsOverview.agentType != 6
            "
          >
            <a
              :href="agentCertificate"
              @click="agentCert"
              class="fc-white-btn mR20"
              >{{ $t('agent.agent.download_certificate') }}</a
            >
            <a :href="agentConfig" class="fc-white-btn mR20">{{
              $t('agent.agent.download_config')
            }}</a>
          </template>
        </div>
        <div class="flex-middle justify-content-end">
          <el-dropdown
            @command="exportPoints($event)"
            class="mL10 fc-btn-ico-lg pointer"
            style="padding: 2px 10px 3px;"
          >
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/new-download"
                iconClass="icon export-icon icon-sm-md"
              ></inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown" class="controller-dropdown-item">
              <el-dropdown-item command="1">{{
                $t('common.wo_report.export_csv')
              }}</el-dropdown-item>
              <el-dropdown-item command="2">{{
                $t('common.wo_report.export_xcl')
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-dropdown
            @command="onOptionsSelect($event)"
            class="mL10 fc-btn-ico-lg pointer"
            style="padding-top: 2px; padding-bottom: 3px;"
          >
            <span class="el-dropdown-link">
              <img src="~assets/menu.svg" width="8" height="16" />
            </span>
            <el-dropdown-menu slot="dropdown" class="controller-dropdown-item">
              <el-dropdown-item command="ping">{{
                $t('agent.agent.ping')
              }}</el-dropdown-item>
              <el-dropdown-item command="discover">{{
                $t('agent.agent.discover')
              }}</el-dropdown-item>
              <span v-if="agentsOverview.agentType === 1">
                <el-dropdown-item command="restart">{{
                  $t('agent.agent.restart')
                }}</el-dropdown-item>
                <el-dropdown-item command="upgrade">{{
                  $t('agent.agent.upgrade')
                }}</el-dropdown-item>
              </span>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <div
      v-if="loading"
      class="flex-middle fc-empty-white m10 fc-agent-empty-state"
    >
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div class="fc-agent-overview-container fc-agent-list-overview p20" v-else>
      <div class="fc-agent-overview-card-con">
        <router-link
          :to="resolvePath('controller')"
          class="fc-agent-white-card p20 pointer"
          style="background: #f7c137;"
        >
          <div>
            <div class="fc-grey5-11 text-uppercase">
              {{ $t('agent.agent.controllers') }}
            </div>
            <div
              class="fc-agent-white-txt2 pT20 flex-align-baseline"
              :class="fontsize(agentsOverview.controller.configured + '')"
            >
              {{
                agentsOverview && agentsOverview.controller
                  ? agentsOverview.controller.configured
                  : 0
              }}
            </div>
          </div>
        </router-link>
        <router-link
          :to="{ path: resolvePath('configured') }"
          class="fc-agent-white-card p20 pointer"
          style="background: #8c54ff;"
        >
          <div>
            <div class="fc-grey5-11 text-uppercase">
              {{ $t('agent.agent.points') }}
            </div>
            <div
              class="fc-agent-white-txt2 pT10 flex-align-baseline"
              :class="
                fontsize(
                  agentsOverview.points.configured &&
                    agentsOverview.points.total + ''
                )
              "
            >
              {{
                agentsOverview && agentsOverview.points
                  ? agentsOverview.points.configured
                  : 0
              }}
              /
              {{
                agentsOverview && agentsOverview.points
                  ? agentsOverview.points.total
                  : 0
              }}
              <span class="f14 pL5 fwBold">{{
                $t('agent.agent.configured')
              }}</span>
            </div>
          </div>
        </router-link>
        <router-link
          :to="{ path: resolvePath('commissioned') }"
          class="fc-agent-white-card p20 pointer"
          style="background: #00c1d4;"
        >
          <div>
            <div class="fc-grey5-11 text-uppercase">
              {{ $t('agent.agent.commissioned') }}
            </div>
            <div
              class="fc-agent-white-txt2 pT10 flex-align-baseline"
              :class="
                fontsize(
                  agentsOverview.points.commissioned &&
                    agentsOverview.points.subscribed + ''
                )
              "
            >
              {{
                agentsOverview && agentsOverview.points
                  ? agentsOverview.points.commissioned
                  : 0
              }}
              <!-- /
            {{
              agentsOverview && agentsOverview.points
                ? agentsOverview.points.subscribed
                : 0
            }} -->
              <span class="f14 pL5 fwBold">Points</span>
            </div>
          </div>
        </router-link>
        <router-link
          :to="{ path: resolvePath('subscribed') }"
          class="fc-agent-white-card p20 pointer"
          style="background: rgb(246, 93, 129);"
        >
          <div
            class="fc-agent-white-card p20"
            style="background: rgb(246, 93, 129);"
          >
            <div class="fc-grey5-11 text-uppercase">
              {{ $t('agent.agent.subscribed') }}
            </div>

            <div
              class="fc-agent-white-txt2 pT10 flex-align-baseline"
              :class="fontsize(agentsOverview.points.subscribed + '')"
            >
              {{
                agentsOverview && agentsOverview.points
                  ? agentsOverview.points.subscribed
                  : 0
              }}
              <span class="f14 pL5 fwBold">
                {{ $t('agent.agent.points') }}</span
              >
            </div>
          </div>
        </router-link>
      </div>
      <!-- graph container -->
      <div class="fc-agent-graph-con">
        <common-widget-chart
          v-if="chartWidget.widgetParams.chartParams"
          key="agentoverview"
          moduleName="agentMetrics"
          isWidget="true"
          :widget="this.chartWidget"
          showPeriodSelect="true"
          type="area"
        >
          <template slot="title">
            {{ $t('agent.agent.agent_message') }}
          </template>
        </common-widget-chart>
      </div>
      <!-- table container -->

      <div
        v-if="loading"
        class="flex-middle fc-empty-white m10 fc-agent-empty-state"
      >
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div
        v-if="$validation.isEmpty(agentiotmsg) && !loading"
        class="height400 fc-empty-white flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
      >
        <inline-svg
          src="svgs/list-empty"
          iconClass="icon text-center icon-xxxxlg"
        ></inline-svg>
        <div class="q-item-label nowo-label">
          {{ $t('agent.agent.no_message') }}
        </div>
      </div>
      <div class="mT20" v-if="!loading && !$validation.isEmpty(agentiotmsg)">
        <div
          class="flex-middle justify-content-space width100 fc-white-bg p20 border-bottom2"
        >
          <div class>
            <div class="fc-grey5-13">{{ $t('agent.agent.iot_message') }}</div>
          </div>
          <div class="flex-middle">
            <pagination
              :total="totalCount"
              :perPage="perPage"
              ref="f-page"
            ></pagination>
            <f-search class="mR20" v-model="agentiotmsg"></f-search>
            <div class="">
              <span
                class="pointer fwBold pR10 f16"
                @click="listRefresh"
                v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
                content="Refresh"
              >
                <i class="el-icon-refresh fwBold f16"></i>
              </span>
            </div>
          </div>
        </div>
        <el-table
          :data="agentiotmsg"
          style="width: 100%"
          class="fc-agent-iot-table"
        >
          <el-table-column label="Command" width="180">
            <template v-slot="iotMessage">
              {{
                $constants.AgentIotMessage[iotMessage.row.command]
                  ? $constants.AgentIotMessage[iotMessage.row.command]
                  : '---'
              }}
            </template>
          </el-table-column>
          <el-table-column label="Sent" width="180" prop="date">
            <template v-slot="iotMessage">
              <div v-if="iotMessage.row.sentTime" class="nowrap">
                {{ iotMessage.row.sentTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column label="Ack" width="180" prop="date">
            <template v-slot="iotMessage">
              <div v-if="iotMessage.row.ackTime" class="nowrap">
                {{ iotMessage.row.ackTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column label="Completed" width="180" prop="date">
            <template v-slot="iotMessage">
              <div class="nowrap" v-if="iotMessage.row.completedTime">
                {{ iotMessage.row.completedTime | formatDate() }}
              </div>
              <div v-else>---</div>
            </template>
          </el-table-column>
          <el-table-column label="Status" width="180" prop="date">
            <template v-slot="iotMessage">
              {{
                $constants.AgentLogStatus[iotMessage.row.status]
                  ? $constants.AgentLogStatus[iotMessage.row.status]
                  : '---'
              }}
            </template>
          </el-table-column>
          <el-table-column label="Message">
            <template v-slot="iotMessage">
              <div
                class="fc-agent-show-txt"
                @click="showIotMessage(iotMessage.row), selectedIndex"
              >
                {{ $t('agent.agent.show_message') }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-dialog
        v-if="showMsgJson && showMsgJson.id"
        :title="$t('common._common.message')"
        :visible.sync="showMsgPopup"
        width="40%"
        :selected="selectedIndex"
        :append-to-body="true"
        style="z-index: 9999999999;"
        class="agents-dialog fc-dialog-center-container"
      >
        <div class="label-txt-black line-height24">
          <pre class="fc-json-view">{{ showMsgJson.msgData }}</pre>
        </div>
        <el-table :data="agentsOverview"> </el-table>
      </el-dialog>
    </div>
    <!-- <NewAgent
      v-if="showDialog"
      :isNew="isNew"
      :model="agent"
      :visibility.sync="showDialog"
      @saved="onAgentCreated"
    >
    </NewAgent>-->
    <div class="help-icon-position pointer" @click="helpDialogOpen()">
      <inline-svg
        src="svgs/question"
        class="vertical-middle"
        iconClass="icon icon-xxlll"
      ></inline-svg>
    </div>
    <agent-help
      v-if="showHelpDialog"
      :visibility.sync="showHelpDialog"
    ></agent-help>
    <el-dialog
      title="Agent Version"
      :visible.sync="versionDialogVisible"
      width="30%"
      :before-close="handleClose"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div class="height150">
        <div
          v-if="versionData.data && versionData.data.length"
          class="label-txt-black"
        >
          {{ $t('agent.agent.version_upgrade') }}
          <span class="fwBold">{{ versionData.data[0].version }}</span
          >, {{ $t('agent.agent.version_upgrade_end') }}
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="versionDialogVisible = false"
          class="modal-btn-cancel"
          >{{ $t('agent.agent.cancel') }}
        </el-button>
        <el-button type="primary" class="modal-btn-save" @click="agentUpgrade()"
          >{{ $t('agent.agent.update') }}
        </el-button>
      </div>
    </el-dialog>
    <!-- Discover controllers.. -->
    <el-dialog
      title="Discover Controller"
      :visible.sync="discoverDialogVisible"
      width="30%"
      :before-close="handleClose"
      class="fc-dialog-center-container"
      :append-to-body="true"
    >
      <div :class="getDiscoverControllersDialogHeight()">
        <el-form label-position="top" ref="agentForm">
          <el-form-item class="mB20" :required="true" label="Protocol">
            <el-select
              v-model="selectedType"
              placeholder="Select Type"
              class="fc-input-full-border-select2 width100"
            >
              <div v-if="agentsOverview.agentType == 2">
                <el-option
                  v-for="item in niagaraOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
              <div v-else-if="agentsOverview.agentType == 1">
                <el-option
                  v-for="item in options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
              <div v-else-if="agentsOverview.agentType == 9">
                <el-option
                  v-for="item in e2Options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                >
                </el-option>
              </div>
            </el-select>
          </el-form-item>
          <el-form-item
            :required="true"
            label="IP Address"
            v-if="selectedType == 14"
          >
            <el-input
              type="text"
              v-model="controllerIpAddress"
              placeholder="Enter IP Address"
              class="fc-input-full-border2 width100"
            ></el-input>
          </el-form-item>
          <el-form-item :required="true" label="Port" v-if="selectedType == 14">
            <el-input
              type="text"
              v-model="controllerPort"
              placeholder="Enter Port"
              class="fc-input-full-border2 width100"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="modal-dialog-footer">
        <el-button @click="handleClose()" class="modal-btn-cancel">{{
          $t('agent.agent.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="discoverDevices()"
          :disabled="handleDisable"
          >{{ $t('agent.agent.discover') }}</el-button
        >
      </div>
    </el-dialog>
    <iframe v-if="exportUrl" :src="exportUrl" style="display: none;"></iframe>
  </div>
</template>
<script>
import FSearch from '@/FSearch'
import CommonWidgetChart from '@/page/widget/performance/charts/CommonWidgetChart'
import AgentHelp from 'agent/AgentHelperDocs/HelpAgentOverview'
import { API } from '@facilio/api'
import Pagination from 'src/components/list/FPagination'
import { getFieldOptions } from 'util/picklist'

import {
  findRouteForTab,
  tabTypes,
  getApp,
  pageTypes,
  findRouteForModule,
} from '@facilio/router'
import { getBaseURL } from 'util/baseUrl'

export default {
  data() {
    return {
      loading: false,
      siteList: {},
      agentsOverview: {},
      controllerIpAddress: null,
      controllerPort: null,
      agentiotmsg: [],
      iotMessage: null,
      content: [],
      showHelpDialog: false,
      showMsgPopup: false,
      selectedIndex: null,
      showMsgJson: null,
      versionData: [],
      totalCount: null,
      perPage: 30,
      versionDialogVisible: false,
      discoverDialogVisible: false,
      selectedType: null,
      controllerType: null,
      agentCertificate: null,
      downloadAgent: null,
      previewUrl: null,
      exportUrl: null,
      chartWidget: {
        widgetParams: {
          chartParams: null,
        },
      },
      options: [
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 12,
          label: 'System',
        },
      ],
      e2Options: [
        {
          value: 14,
          label: 'E2',
        },
      ],
      value: -1,
      niagaraOptions: [
        {
          value: 0,
          label: 'Misc',
        },
        {
          value: 1,
          label: 'BACnet Ip',
        },
        {
          value: 3,
          label: 'Fox',
        },
        {
          value: 5,
          label: 'Modbus_Rtu',
        },
        {
          value: 8,
          label: 'Lon Works',
        },
      ],
    }
  },
  mounted() {
    this.loadAgentsSummary()
    this.loadIotMessage()
    this.agentVersion()
  },
  watch: {
    page() {
      this.loadIotMessage()
    },
  },
  computed: {
    handleDisable() {
      return this.selectedType === 14
        ? !(this.controllerIpAddress != null && this.controllerPort != null)
        : false
    },
    agentId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    page() {
      return this.$route.query.page || 1
    },
    agentConfig() {
      return getBaseURL() + `/v2/agent/config?agentId=${this.agentId}`
    },
  },
  components: {
    FSearch,
    AgentHelp,
    CommonWidgetChart,
    Pagination,
  },
  methods: {
    async loadSites() {
      let siteIds = [this.agentsOverview.siteId] || []
      let defaultIds = [...new Set(siteIds)]
      let perPage = defaultIds.length

      if (perPage === 0) return

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: 'site', skipDeserialize: true },
        defaultIds,
        perPage,
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.siteList = options
      }
    },
    getDiscoverControllersDialogHeight() {
      return this.selectedType === 14 ? 'height400' : 'height200'
    },
    back() {
      this.$router.go(-1)
    },
    showAddAgent() {
      this.showDialog = true
    },
    resolvePath(configType) {
      let appName = getApp().linkName
      if (configType === 'controller') {
        let { name } = findRouteForModule(configType, pageTypes.LIST) || {}
        if (name) {
          return {
            name,
            query: {
              agentId: this.agentsOverview.id,
            },
          }
        }
      } else {
        let { path } =
          findRouteForTab(tabTypes.CUSTOM, {
            config: {
              type: 'points',
            },
          }) || {}
        return `/${appName}/${path}?agentId=${this.agentsOverview.id}&agentPointsTab=${configType}`
      }
    },
    loadAgentsSummary() {
      this.loading = true
      this.$http
        .get('/v2/agent/agentOverview?agentId=' + this.agentId)
        .then(response => {
          this.loading = false
          this.agentsOverview = response.data.result.data
            ? response.data.result.data
            : []
          if (this.agentsOverview.chartParams) {
            this.chartWidget.widgetParams.chartParams = this.agentsOverview.chartParams
          }
          if (this.agentsOverview.type) {
            this.controllerType = this.agentsOverview
          }
          this.loadCount()
          this.loadSites()
        })
        .catch(() => {
          this.loading = false
        })
    },
    showIotMessage(iotMessage) {
      this.showMsgPopup = true
      this.showMsgJson = {
        ...iotMessage,
        msgData: JSON.stringify(JSON.parse(iotMessage.msgData), null, 2).trim(),
      }
      // this.selectedIndex = JSON.stringify(this.showMsgJson)
    },
    agentUpgradeDialog() {
      this.versionDialogVisible = true
    },
    handleClose() {
      this.discoverDialogVisible = false
      this.selectedType = null
      ;(this.controllerIpAddress = null), (this.controllerPort = null)
    },
    discoverControllerDialog() {
      this.discoverDialogVisible = true
    },
    loadIotMessage() {
      this.loading = true
      this.$http
        .get(
          `/v2/agent/getIotMessages?agentId=${this.agentId}&page=${this.page}&perPage=${this.perPage}`
        )
        .then(response => {
          this.loading = false
          this.agentiotmsg = response.data.result.data
            ? response.data.result.data
            : []
        })
        .catch(() => {
          this.agentiotmsg = []
          this.loading = false
        })
    },
    agentPing() {
      let url = '/v2/agent/ping'
      let params = {
        agentId: this.agentId,
      }
      this.$http.post(url, params).then(response => {
        if (response.data.responseCode === 200) {
          this.$message.success('Agent Pinged')
          this.loadIotMessage()
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
    discoverDevices() {
      let discoverControllersData = {
        agentId: this.agentId,
        controllerType: this.selectedType,
      }
      if (this.selectedType === 14) {
        ;(discoverControllersData.ipAddress = this.controllerIpAddress),
          (discoverControllersData.port = this.controllerPort)
      }
      this.loading = true
      this.$http
        .post('/v2/controller/discover ', discoverControllersData)
        .then(response => {
          if (response.data.responseCode === 200) {
            this.loading = false
            this.handleClose()
            this.$message.info('Controllers will be discovered in a while.')
          } else {
            this.loading = false
            this.handleClose()
            this.$message.error(response.data.message)
          }
        })
    },
    agentRestart() {
      let url = '/v2/agent/restart'
      let params = {
        recordIds: [this.agentId],
      }
      this.$http.post(url, params).then(response => {
        if (response.data.responseCode === 200) {
          this.$message.info('Agent will be restarted in a while.')
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
    agentDownload() {
      this.downloadAgent =
        getBaseURL() + `/v2/agent/agentDownload?agentId=${this.agentId}`
    },
    agentCert() {
      this.agentCertificate =
        getBaseURL() + `/v2/agent/certificate?agentId=${this.agentId}`
    },
    listRefresh() {
      this.loadIotMessage()
    },
    helpDialogOpen() {
      this.showHelpDialog = true
    },
    agentJvmStatus() {
      let url = '/v2/agent/jvmStatus'
      let params = {
        agentId: this.agentId,
      }
      this.$http.post(url, params).then(response => {
        if (response.data.responseCode === 0) {
          this.$message.success('Jvm Status updated successfully.')
        } else {
          this.$message.error(response.data.message)
        }
      })
    },
    loadCount() {
      return this.$http
        .get('/v2/agent/iotMessageCount?agentId=' + this.agentId)
        .then(response => {
          this.totalCount = response.data.result.data || null
        })
        .catch(() => {
          this.loading = false
        })
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
    agentUpgrade() {
      this.loading = true
      let params = {
        agentId: this.agentId,
        versionId: this.versionData.data[0].id,
      }
      this.$http.post('/v2/agent/upgradeAgent', params).then(response => {
        if (response.data.responseCode === 200) {
          this.$message.success('Agent Upgrade successfully.')
          this.loading = false
          this.versionDialogVisible = false
        } else {
          this.$message.error(response.data.message)
          this.loading = false
          this.versionDialogVisible = false
        }
      })
    },
    async agentVersion() {
      let { error, data } = await API.get(
        `v2/agent/versions?latestVersion=true`
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.versionData = data || []
      }
    },
    onOptionsSelect(command) {
      switch (command) {
        case 'ping':
          this.agentPing()
          break
        case 'discover':
          this.discoverControllerDialog()
          break
        case 'upgrade':
          this.agentUpgradeDialog()
          break
        case 'restart':
          this.agentRestart()
          break
        case 'confiure_notification':
          break
      }
    },
    async exportPoints(command) {
      let url = `/v2/point/export?agentId=${this.agentId}&type=${command}`
      this.$message.success({
        message: 'Downloading',
        type: 'success',
        showClose: true,
      })
      let { data, error } = await API.get(url)
      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.exportUrl = data.fileUrl
      }
    },
  },
}
</script>
<style lang="scss">
.fc-agent-summary-page {
  .fc-agent-white-card {
    width: 24%;
    height: 140px;
    display: flex;
    align-items: flex-start;
    flex-direction: column;
    justify-content: center;
  }

  .fc-agent-overview-container {
    height: calc(100vh - 150px);
    overflow-y: scroll;
    padding-bottom: 100px;
  }

  .fc-point-export-btn {
    padding: 9px 10px 8px;
    &:hover {
      background: #fff;
      border: 1px solid #d0d9e2;
    }
  }
}

.fc-agent-list-overview {
  .fc-agent-white-txt2,
  .fc-grey5-11,
  .fc-agent-black-18 {
    color: #ffffff;
  }

  .fc-agent-white-card {
    border: none;
    border-radius: 3px;
  }

  .fc-grey5-11 {
    font-weight: bold;
  }

  .fwidget-report-period-select {
    margin-top: 10px;
  }

  .agents-dialog .el-dialog__body {
    height: 300px;
    padding: 10px 20px 5px 20px;
  }
}
</style>
