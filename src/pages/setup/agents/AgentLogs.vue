<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Logs</div>
        <div class="heading-description">List of all log entries</div>
      </div>
    </div>
    <div class="container-scroll">
      <div class="row setting-Rlayout mT20">
        <div class="col-lg-12 col-md-12 overflow-x">
          <div class="d-flex flex-direction-row justify-content-space pB25">
            <div>
              <div class="category-blue-txt pB10">Agent</div>
              <el-select
                v-model="selectedAgentId"
                filterable
                @change="totalCount = null"
                placeholder="Select Agent"
                width="250px"
                class="fc-input-full-border-select2"
              >
                <el-option
                  v-for="(agent, index) in agents"
                  :key="index"
                  :label="agent.displayName || agent.name"
                  :value="agent.id"
                  no-data-text="No controllers available"
                ></el-option>
              </el-select>
            </div>
            <div class="d-flex flex-direction-row">
              <pagination
                v-if="!loading && totalCount && logs.length !== 0"
                :total="totalCount"
                :perPage="perPage"
                class="pB15"
                style="margin-top: auto;"
              ></pagination>
            </div>
          </div>
          <table class="setting-list-view-table">
            <thead>
              <tr>
                <th class="setting-table-th setting-th-text">Command</th>
                <th class="setting-table-th setting-th-text">Status</th>
                <th class="setting-table-th setting-th-text">Content</th>
                <th class="setting-table-th setting-th-text">Timestamp</th>
                <!-- <th></th> -->
              </tr>
            </thead>
            <tbody v-if="loading">
              <tr>
                <td colspan="100%" class="text-center">
                  <spinner :show="loading" size="80"></spinner>
                </td>
              </tr>
            </tbody>
            <tbody v-else-if="logs && logs.length === 0">
              <tr>
                <td colspan="100%" class="text-center">No logs available.</td>
              </tr>
            </tbody>
            <tbody v-else>
              <tr
                class="tablerow"
                v-for="log in logs"
                :key="log.timeStamp"
                v-loading="loading"
              >
                <td>
                  <div class="label-txt3-14">
                    {{ $constants.AGENT_LOG_CMD_HASH[log.command] || '---' }}
                  </div>
                </td>
                <td>
                  <div class="label-txt3-14">
                    {{ $constants.AGENT_LOG_STATUS_HASH[log.status] || '---' }}
                  </div>
                </td>
                <td>
                  <div v-if="log.isCustomMessage">
                    <a @click="showPublishMessage(log.msgid)" class="f14"
                      >Show Message</a
                    >
                  </div>
                  <template v-else>
                    <div class="label-txt3-14">{{ log.content }}</div>
                    <div v-if="log.retries" class="label-txt3-12 mT5">
                      Connection Count: {{ log.retries }}
                    </div>
                  </template>
                </td>
                <td>
                  <div class="label-txt3-14">
                    {{ log.timestamp | formatDate }}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <el-dialog
      v-if="showLogMsgPopup"
      title="Agent Details"
      :visible.sync="showLogMsgPopup"
      width="40%"
      style="z-index: 9999999999;"
      class="agents-dialog"
    >
      <div v-if="!messageHash[selectedMessageId]">No data received.</div>
      <div v-else-if="messageHash[selectedMessageId].loading">
        Loading message...
      </div>
      <pre v-else class="json-view">{{
        messageHash[selectedMessageId].json
      }}</pre>
    </el-dialog>
  </div>
</template>
<script>
import Pagination from '@/list/FPagination'
import { isNumber, isEmpty } from '@facilio/utils/validation'

export default {
  title() {
    return 'Logs'
  },
  components: { Pagination },

  data() {
    return {
      isNew: true,
      loading: false,
      logs: [],
      totalCount: null,
      showDialog: false,
      agents: [],
      selectedAgentId: null,
      perPage: 50,
      messageHash: {},
      selectedMessageId: null,
      showLogMsgPopup: false,
    }
  },

  created() {
    this.selectedAgentId = this.$route.query.agentId
      ? parseInt(this.$route.query.agentId)
      : null

    this.loadAgents().then(() => {
      if (!isEmpty(this.agents)) {
        this.loadLogEntries()
        this.loadCount()
      }
    })
  },

  methods: {
    loadAgents() {
      this.loading = true
      return this.$http
        .get('/v2/setup/agent/list')
        .then(response => {
          this.agents = response.data.result.agentDetails
            ? response.data.result.agentDetails
            : []
          this.selectedAgentId =
            parseInt(this.$route.query.agentId) || this.agents[0].id
        })
        .catch(() => {
          this.loading = false
        })
    },

    processLogEntry(logEntry) {
      let message = isNumber(logEntry.message)
        ? Number(logEntry.message)
        : logEntry.message

      if (isNumber(logEntry.message) && message > 1000) {
        let retryCount = message % 1000
        let content = message - retryCount

        logEntry = {
          ...logEntry,
          content: this.$constants.AGENT_LOG_CONTENT_HASH[content],
          retries: retryCount,
        }
      } else {
        logEntry.content =
          this.$constants.AGENT_LOG_CONTENT_HASH[message] || message
      }

      let hasMsgId = !isEmpty(logEntry.msgid)
      let isMsgNull = isEmpty(logEntry.message)
      let isMsgEqualsId = Number(logEntry.msgid) === Number(logEntry.message)

      if (hasMsgId && (isMsgNull || isMsgEqualsId)) {
        logEntry.isCustomMessage = true
      }

      return logEntry
    },

    loadLogEntries() {
      this.loading = true
      return this.$http
        .get(
          `/v2/setup/agent/log?agentId=${this.selectedAgentId}&perPage=${this.perPage}&page=${this.page}`
        )
        .then(response => {
          this.loading = false
          if (response.data.responseCode === 0) {
            this.logs =
              response.data.result.logs.map(this.processLogEntry) || []
          }
        })
        .catch(() => {
          this.logs = []
          this.loading = false
        })
    },

    loadCount() {
      return this.$http
        .get(`/v2/setup/agent/logCount?agentId=${this.selectedAgentId}`)
        .then(response => {
          this.totalCount = response.data.result.logCount || null
        })
        .catch(() => {
          this.loading = false
        })
    },

    showPublishMessage(msgId) {
      this.fetchMessage(msgId)
      this.selectedMessageId = msgId
      this.showLogMsgPopup = true
    },

    fetchMessage(msgid) {
      const msgUnAvailable = msgid => {
        this.$set(this, 'messageHash', {
          ...this.messageHash,
          [msgid]: null,
        })
      }

      if (isEmpty(this.messageHash[msgid])) {
        this.messageHash[msgid] = { loading: true, json: '' }

        return this.$http
          .get(`/v2/setup/agent/fetchMessage?messageId=${msgid}`)
          .then(response => {
            if (
              response.data.responseCode === 0 &&
              !isEmpty(response.data.result.publishMessage)
            ) {
              let message = response.data.result.publishMessage
              let json = message && JSON.parse(message.dataStr)

              this.$set(this, 'messageHash', {
                ...this.messageHash,
                [msgid]: {
                  loading: false,
                  json: json ? JSON.stringify(json, null, 2) : '',
                },
              })
            } else {
              msgUnAvailable(msgid)
            }
          })
          .catch(() => msgUnAvailable(msgid))
      }
    },
  },

  computed: {
    page() {
      return this.$route.query.page || 1
    },
  },

  watch: {
    selectedAgentId(val) {
      this.$router.push({ query: { agentId: val } })
      this.loadLogEntries()
      this.loadCount()
    },
    page() {
      this.loadLogEntries()
    },
  },
}
</script>
<style scoped>
.controller-right-actions {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.controller-add-btn {
  color: #ffffff;
  border-radius: 0;
  font-size: 21px;
  font-weight: bold;
  padding: 6px 13px;
  background: #39b2c2;
  border: none;
}
.controller-add-btn:hover {
  background: #39b2c2;
}
.controller-download .el-icon-download {
  font-size: 20px;
  color: #1e201d;
  font-weight: 500;
}
.controller-id {
  font-size: 12px;
  letter-spacing: 0.5px;
  color: #a7a7ad;
}
.controller-time {
  font-size: 11px;
  letter-spacing: 0.4px;
  color: #c5c4cb;
  margin-left: 20px;
}
.controller-active-section {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.dropdown-controller-item .el-dropdown-menu__item {
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #67666a;
  border-bottom: 1px solid rgba(151, 151, 151, 0.1);
}
.controller-points {
  font-size: 11px;
  letter-spacing: 0.4px;
  color: #a7a7ad;
}
.json-view {
  white-space: pre-wrap;
  margin: 0;
  min-height: 200px;
}
</style>
