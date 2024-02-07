<template>
  <div class="page-width-cal alarm-mapping-page">
    <el-header class="fc-agent-main-header" height="80">
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">
          {{ $t('agent.agent.source') }}
        </div>
      </div>
    </el-header>
    <div class="flex-middle justify-content-space">
      <div>
        <el-select
          v-model="selectedAgentId"
          filterable
          default-first-option
          @change="listRefresh"
          placeholder="Select Agent"
          width="250px"
          class="fc-input-full-border-select2 mL10 mT10"
        >
          <el-option
            v-for="(agent, index) in agents"
            :key="index"
            :label="agent.displayName || agent.name"
            :value="agent.id"
            no-data-text="No Source available"
          ></el-option>
        </el-select>
        <el-button
          v-if="agents.length && (agentType === 2 || agentType === 9)"
          type="button"
          class="fc-white-btn mL20 mT10"
          @click="discoverSource"
          :loading="discovering"
          >{{
            discovering
              ? $t('agent.alarm_mapping.alarm_mapping_discovering')
              : $t('agent.alarm_mapping.alarm_mapping_discover')
          }}
        </el-button>
      </div>

      <div class="flex-middle">
        <div class="row" style="margin-right: 20px" v-if="showQuickSearches">
          <div class="fc-list-search">
            <div
              class="fc-list-search-wrapper fc-list-search-wrapper-reading relative"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 32 32"
                class="search-icon3"
              >
                <title>search</title>
                <path
                  d="M21.487 22.927l7.037 7.037c.473.473 1.238.475 1.71.003s.47-1.237-.003-1.71l-7.037-7.037c3.96-4.82 3.675-11.967-.846-16.487C17.539-.076 9.757-.092 4.966 4.699S.191 17.272 5 22.081c4.52 4.52 11.667 4.805 16.487.846zM6.679 6.413c3.848-3.848 10.099-3.836 13.962.027s3.875 10.114.027 13.962c-3.848 3.848-10.099 3.836-13.962-.027S2.831 10.261 6.679 6.413z"
                ></path>
              </svg>
              <input
                ref="querySearch"
                autofocus
                type="text"
                v-model="querySearch"
                @keyup.enter="quickSearches()"
                placeholder="Search"
                class="quick-search-input6"
              />
              <svg
                @click="closeSearches"
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 32 32"
                class="close-icon6"
                aria-hidden="true"
              >
                <title>close</title>
                <path
                  d="M17.992 16l8.796-8.796a1.409 1.409 0 0 0-1.992-1.992L16 14.008 7.204 5.212a1.409 1.409 0 0 0-1.992 1.992L14.008 16l-8.796 8.796a1.409 1.409 0 0 0 1.992 1.992L16 17.992l8.796 8.796a1.409 1.409 0 0 0 1.992-1.992L17.992 16z"
                ></path>
              </svg>
            </div>
          </div>
        </div>
        <div
          class="pointer"
          @click="toggleQuickSearches"
          v-show="!showQuickSearches"
        >
          <span class="">
            <i
              class="fa fa-search"
              aria-hidden="true"
              style="font-size: 14px;"
            ></i>
          </span>
        </div>
        <span class="separator">|</span>
        <pagination
          :total="listCount"
          :perPage="perPage"
          ref="f-page"
        ></pagination>
        <span class="separator" v-if="listCount">|</span>
        <span class="pointer fwBold pR10 f16" @click="listRefresh">
          <i
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            content="Refresh"
            class="el-icon-refresh fwBold f16"
          ></i>
        </span>
      </div>
    </div>
    <div class="white-bg mL10 mR20 mT10">
      <el-tabs
        v-model="currentTab"
        class="agent-tabs"
        @tab-click="listRefresh()"
      >
        <el-tab-pane
          v-for="tab in tabs"
          :key="tab.key"
          :label="tab.label"
          :name="tab.key"
        >
          <div
            v-if="loading"
            class="flex-middle fc-empty-white m10 fc-agent-empty-state"
          >
            <spinner :show="loading" size="80"></spinner>
          </div>
          <div
            v-if="$validation.isEmpty(tableData) && !loading"
            class="height200vh  flex-middle justify-content-center flex-direction-column fc-agent-table m10 fc-agent-empty-state"
          >
            <inline-svg
              src="svgs/list-empty"
              iconClass="icon text-center icon-xxxxlg"
            ></inline-svg>
            <div class="q-item-label nowo-label">
              {{ $t('agent.empty.no_source') }}
            </div>
          </div>
          <div
            v-if="!loading && !$validation.isEmpty(tableData)"
            class="fc-agent-table agent-list-scroll scrollbar-style p10"
          >
            <el-table
              :data="tableData"
              style="width:100%"
              height="auto"
              class="fc-list-view p10 pT0 mT10 fc-list-table-container fc-commissioning-table fc-table-td-height fc-table-viewchooser pB100"
            >
              <el-table-column prop="source" label="SOURCE" width="350">
              </el-table-column>
              <el-table-column label="ASSET" width="250">
                <template slot-scope="scope">
                  <div v-if="scope.row.name">
                    {{ scope.row.name }}
                  </div>
                  <div v-else>
                    <el-button @click="handleClick(scope.row)" type="text"
                      >Map Asset</el-button
                    >
                  </div>
                </template>
              </el-table-column>
              <el-table-column width="150" class="visibility-visible-actions">
                <template slot-scope="scope">
                  <div v-if="scope.row.name">
                    <div class="visibility-hide-actions text-right mR30">
                      <el-dropdown
                        @command="onOptionsSelect($event, scope.row)"
                      >
                        <span class="el-dropdown-link">
                          <i class="el-icon-more controller-more"></i>
                        </span>
                        <el-dropdown-menu
                          slot="dropdown"
                          class="controller-dropdown-item"
                        >
                          <el-dropdown-item command="edit">{{
                            $t('agent.agent.edit')
                          }}</el-dropdown-item>
                          <el-dropdown-item command="remove">{{
                            $t('agent.agent.remove_resource')
                          }}</el-dropdown-item>
                        </el-dropdown-menu>
                      </el-dropdown>
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div v-if="canShowLookupWizard">
      <FLookupFieldWizard
        :canShowLookupWizard.sync="canShowLookupWizard"
        :selectedLookupField="assetField"
        @setLookupFieldValue="setLookupFieldValue"
      ></FLookupFieldWizard>
    </div>
  </div>
</template>

<script>
import agentmixin from '@/mixins/AgentMixin'
import Pagination from 'src/components/list/FPagination'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'

export default {
  title() {
    return 'Alarm Mapping'
  },
  data() {
    return {
      agents: [],
      tableData: [],
      selectedAgentId: null,
      selectedSourceId: null,
      isMapped: false,
      perPage: 50,
      loading: false,
      listCount: 0,
      canShowLookupWizard: false,
      selectedLookupField: null,
      assetField: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'asset',
        selectedItems: [],
        field: {
          lookupModule: {
            name: 'asset',
            displayName: 'Asset',
          },
        },
      },
      currentTab: 'unMapped',
      tabs: [
        {
          key: 'unMapped',
          label: 'UnMapped Source',
        },
        {
          key: 'mapped',
          label: 'Mapped Source',
        },
      ],
      discovering: false,
      querySearch: null,
      showQuickSearches: false,
    }
  },
  components: {
    Pagination,
    FLookupFieldWizard,
  },
  mixins: [agentmixin],
  created() {
    this.getdata()
  },
  watch: {
    page() {
      this.getSource()
    },
  },
  computed: {
    page() {
      return this.$route.query.page || 1
    },
  },
  methods: {
    setCurrentTab() {
      if (this.currentTab === 'mapped') {
        this.isMapped = true
      }
    },
    listRefresh() {
      let agentCopy = [...this.agents]
      this.isAgentOffline(this.selectedAgentId, agentCopy)
      this.setCurrentTab()
      this.getSource()
      this.loadCount()
    },
    handleClick(selectedSource) {
      if (!isEmpty(selectedSource)) {
        this.selectedSourceId = selectedSource.id
        this.assetField.selectedItems = []
        this.showLookupWizard(this.assetField, true)
      }
    },
    getdata() {
      this.loading = true
      this.loadAgent()
        .then(response => {
          if (response.responseCode === 200) {
            this.agents = response.result.data
            this.selectedAgentId = this.agents[0].id
            let agentCopy = [...this.agents]
            this.isAgentOffline(this.selectedAgentId, agentCopy)
            this.getSource()
            this.loadCount()
          }
        })
        .catch(() => {
          this.loading = false
        })
    },
    getSource() {
      let url = `/v2/event/sources?agentId=${this.selectedAgentId}&page=${this.page}&perPage=${this.perPage}&mapped=${this.isMapped}`
      if (this.querySearch) {
        url += `&querySearch=${this.querySearch}`
      }
      this.loading = true
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 200) {
            this.tableData = response.data.result.sources
              ? response.data.result.sources
              : []
            this.loading = false
          }
        })
        .catch(() => {
          this.loading = false
          this.tableData = []
        })
    },
    loadCount() {
      this.listCount = 0
      API.get('/v2/event/count', {
        agentId: this.selectedAgentId,
        mapped: this.isMapped,
        querySearch: this.querySearch,
      }).then(({ error, data }) => {
        if (!error) {
          this.listCount = data.count
        }
        if (this.isMapped) {
          this.isMapped = false
        }
      })
    },
    setLookupFieldValue(props) {
      this.updateSourceResource(props)
    },
    onOptionsSelect(command, selectedSource) {
      if (!isEmpty(selectedSource)) {
        this.selectedSourceId = selectedSource.id
        if (command === 'remove') {
          this.resetMappedSource(selectedSource)
        } else {
          this.assetField.selectedItems = [
            {
              lable: selectedSource.name,
              value: selectedSource.resourceId,
            },
          ]
          this.showLookupWizard(this.assetField, true)
        }
      }
    },
    showLookupWizard(field, canShow) {
      this.$set(this, 'selectedLookupField', field)
      this.$set(this, 'canShowLookupWizard', canShow)
    },
    updateSourceResource(props) {
      if (isEmpty(props)) {
        this.$message.error('resource should not be null')
        return
      }
      this.loading = true
      const param = {
        id: this.selectedSourceId,
        resourceId: props.field.selectedItems[0].value,
      }
      const url = `/v2/event/updatesource`
      this.$http.put(url, param).then(response => {
        if (response.data) {
          setTimeout(() => {
            this.$message.success(
              this.$t('agent.alarm_mapping.alarm_mapping_update')
            )
            this.loading = false
            this.listRefresh()
          }, 1000)
        } else {
          this.loading = false
          this.$t('agent.alarm_mapping.alarm_mapping_error')
        }
      })
    },
    discoverSource() {
      if (!this.isConnected) {
        this.$message.warning(this.$t('agent.agent.agent_offline_msg'))
        return
      }
      this.loading = true
      this.discovering = true
      const params = {
        agentId: this.selectedAgentId,
      }
      this.$http.post('/v2/event/discoverSource', params).then(response => {
        if (response.data.responseCode === 200) {
          setTimeout(() => {
            this.$message.success('Discovered Alarm sources successfully.')
            this.loading = false
            this.discovering = false
            this.listRefresh()
          }, 5000)
        } else {
          this.loading = false
          this.discovering = false
          this.$message.error('Error occurred')
        }
      })
    },
    resetMappedSource(source) {
      this.$dialog
        .confirm({
          title: this.$t('agent.agent.remove_resource'),
          htmlMessage:
            this.$t('agent.alarm_mapping.alarm_mapping_remove_msg') +
            source.name +
            '?',
          rbDanger: true,
          rbLabel: this.$t('agent.agent.remove'),
        })
        .then(value => {
          if (value) {
            this.$http
              .put(`/v2/event/removeResource`, {
                sourceId: source.id,
              })
              .then(response => {
                if (response.data.responseCode === 200) {
                  this.$message.success(
                    this.$t('agent.alarm_mapping.alarm_mapping_update')
                  )
                  this.listRefresh()
                } else {
                  this.$message.error('Error occurred')
                }
                this.loading = false
              })
          }
        })
    },
    quickSearches() {
      if (this.querySearch) {
        this.getSource()
        this.loadCount()
      }
    },
    toggleQuickSearches() {
      this.showQuickSearches = !this.showQuickSearches
      if (this.showQuickSearches) {
        this.$nextTick(() => {
          this.$refs.querySearch.focus()
        })
      }
    },
    closeSearches() {
      this.toggleQuickSearches()
      this.querySearch = null
    },
  },
}
</script>
<style lang="scss">
.alarm-mapping-page {
  .agent-list-scroll .el-table {
    height: calc(100vh - 300px) !important;
    padding-bottom: 100px;
  }
}
</style>
