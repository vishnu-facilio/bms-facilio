<template>
  <div class="page-width-cal">
    <el-header
      class="fc-agent-main-header"
      height="80"
      v-if="openAlarmId === -1"
    >
      <div class="flex-middle justify-content-space">
        <div class="fc-agent-black-26">{{ $t('agent.agent.alarm') }}</div>
        <div class="flex-middle flex-row">
          <div v-if="recordCount" class="agent-alarm-search flex-middle">
            <el-tooltip
              effect="dark"
              :content="$t('common._common.search')"
              placement="left"
            >
              <AdvancedSearch
                key="alarm-search"
                moduleName="agentAlarm"
                moduleDisplayName="Alarm"
              >
              </AdvancedSearch>
            </el-tooltip>
            <span class="separator">|</span>

            <pagination
              :total="recordCount"
              :perPage="perPage"
              ref="f-page"
            ></pagination>
          </div>
        </div>
      </div>
    </el-header>
    <div>
      <FTags
        v-if="openAlarmId === -1"
        :key="moduleName"
        :moduleName="moduleName"
      ></FTags>
    </div>
    <div class="fc-agent-table">
      <div class="height100 alarm-list-page">
        <div class="white-bg mL20 mR20 mT10" v-if="openAlarmId == -1">
          <el-tabs
            v-model="selectedView"
            class="agent-tabs"
            @tab-click="tabSwitch"
          >
            <el-tab-pane
              v-for="tab in tabs"
              :key="tab.key"
              :label="tab.label"
              :name="tab.key"
            >
              <div v-if="isLoading" class="spinner alarm-list">
                <Spinner :show="isLoading"></Spinner>
              </div>
              <div
                v-if="$validation.isEmpty(records) && !isLoading"
                class="flex-middle justify-content-center flex-direction-column alarm-list"
              >
                <inline-svg
                  src="svgs/emptystate/alarmEmpty"
                  iconClass="icon text-center icon-xxxxlg"
                ></inline-svg>
                <div class="nowo-label">
                  {{ $t('alarm.alarm.no_alarms_found') }}
                </div>
              </div>
              <div v-if="!$validation.isEmpty(records) && !isLoading">
                <CommonList
                  :viewDetail="viewDetail"
                  :records="records"
                  :moduleName="moduleName"
                  :columnConfig="columnConfig"
                  :slotList="slotList"
                  :class="filtersApplyed"
                  hideListSelect="true"
                >
                  <template #[slotList[0].name]="{ record }">
                    <div class="center f10 secondary-color mW90">
                      <div
                        class="q-item-label uppercase severityTag"
                        :style="{
                          'background-color': getAlarmColor(record),
                        }"
                      >
                        {{ getAlarmDisplayName(record) }}
                      </div>
                    </div>
                  </template>
                  <template #[slotList[1].name]="{ record }">
                    <div class="mL15">
                      <div class="q-item-sublabel ellipsis mW90 mT5 f20">
                        <div>
                          <span class="fc-id">#{{ record.id }}</span>
                          <span class="fc-badge mL8 inline middle nowrap">{{
                            $constants.AlarmTypes[record.agentAlarmTypeEnum]
                          }}</span>
                        </div>
                      </div>
                      <div>
                        <div
                          class="d-flex fw5 label-txt-black  main-field-column"
                          @click="opensummary(record.id)"
                        >
                          <div class="self-center">
                            <span>{{ record.subject || '---' }}</span>
                          </div>
                        </div>
                      </div>
                      <div class="ellipsis f12 suva-grey mT5">
                        <span>{{ record.lastOccurredTime | fromNow }}</span>
                      </div>
                    </div>
                  </template>
                  <template #[slotList[2].criteria]="{ record }">
                    <div class="self-center f10 secondary-color mW90 mL15">
                      <span class="list-main-field f12">
                        {{ record.agent.displayName }}
                      </span>
                    </div>
                  </template>
                  <template #[slotList[3].criteria]="{ record }">
                    <div
                      class="self-center f12 letter-spacing0_5 fc-grey5"
                      v-if="record.lastCreatedTime > -1"
                    >
                      <span class="pL20 f12">
                        {{ record.lastCreatedTime | formatDate() }}
                      </span>
                    </div>
                  </template>
                  <template #[slotList[4].criteria]="{ record }">
                    <div class="max-width50 mL10">
                      <div class="flLeft">
                        <inline-svg src="event" class="flLeft w15" />
                      </div>
                      <span class="q-item-label pL5 f13 letter-spacing0_4">
                        {{ getNoOfOccurrences(record) }}
                      </span>
                    </div>
                  </template>
                </CommonList>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
        <div class="fc-column-view height100 border-border-none" v-else>
          <div class="row fc-column-view-title height100">
            <div
              class="col-4 fc-column-view-left height100"
              style="max-width: 26.3333%; flex: 0 0 26.3333%;"
            >
              <div
                class="fc-agent-txt14 pointer bold p20 f15 capitalize"
                @click="back"
              >
                <i class="el-icon-back bold"></i> {{ currentViewDetail }}
              </div>
              <v-infinite-scroll
                :offset="20"
                class="height100vh pB150 overflow-y-scroll"
              >
                <table
                  class="fc-list-view-table overflow-y-scroll height100vh-100px pB60 display-block"
                >
                  <tbody v-if="records.length">
                    <template v-for="(alarm, index) in records">
                      <tr
                        @click="opensummary(alarm.id)"
                        class="tablerow"
                        :key="index"
                        v-bind:class="{ active: openAlarmId === alarm.id }"
                      >
                        <td class="text-left width100 pL5">
                          <div class="q-item-main q-item-section pL10">
                            <div
                              class="fw5 workRequest-heading f14 textoverflow-height-ellipsis"
                              v-tippy
                              :title="alarm.Subject"
                            >
                              {{ alarm.subject }}
                            </div>
                            <div class="flex-middle pT5">
                              <div class="uppercase fc-id">#{{ alarm.id }}</div>
                              <div class="separator">|</div>
                              <div class="fc-grey2-text12">
                                {{
                                  alarm.agent ? alarm.agent.displayName : '---'
                                }}
                              </div>
                            </div>
                          </div>
                        </td>
                        <td class="text-left clearboth" v-if="alarm.severity">
                          <span class="ellipsis pT5">
                            <span class="q-item-label">
                              <i
                                class="fa fa-circle prioritytag"
                                v-bind:style="{
                                  color: getAlarmColor(alarm),
                                }"
                                aria-hidden="true"
                              ></i>
                            </span>
                            <span
                              class="q-item-label uppercase secondary-color f10 letter-spacing0_7 hex-grey"
                            >
                              {{ getAlarmDisplayName(alarm) }}
                            </span>
                          </span>
                        </td>
                      </tr>
                    </template>
                  </tbody>
                </table>
              </v-infinite-scroll>
            </div>

            <div
              class="col-8 fc-column-alarm-right new-alarm-summ-bg"
              style="max-width: 73.6667%; flex: 0 0 73.6667%;"
            >
              <div>
                <router-view></router-view>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CommonModuleList from 'src/newapp/list/CommonModuleList.vue'
import AdvancedSearch from 'newapp/components/search/AdvancedSearch'
import Pagination from 'src/components/list/FPagination'
import FTags from 'newapp/components/search/FTags'
import { findRouteForModule, pageTypes } from '@facilio/router'
import { isEmpty, isNull, isNullOrUndefined } from '@facilio/utils/validation'
import { formatDate, fromNow } from 'src/util/filters'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import VInfiniteScroll from 'v-infinite-scroll'
import infiniteScroll from 'vue-infinite-scroll'
import { mapState } from 'vuex'
import Spinner from '@/Spinner'

export default {
  title() {
    return 'Agent Alarms'
  },
  extends: CommonModuleList,
  name: 'AgentAlarmList',
  mixins: [NewAlarmMixin],
  components: {
    Pagination,
    AdvancedSearch,
    VInfiniteScroll,
    FTags,
    Spinner,
  },
  directives: {
    infiniteScroll,
  },
  data() {
    return {
      perPage: 50,
      tabs: [],
      columnConfig: {
        fixedSelectableColumns: ['severity', 'subject'],
      },
      selectedView: null,
    }
  },
  async created() {
    await this.loadViews()
  },
  computed: {
    // ...mapState({
    //   groupViews: state => {
    //     return !isEmpty(state.view.groupViews) ? state.view.groupViews : []
    //   },
    // }),
    filtersApplyed() {
      let { filters } = this
      return !isNull(filters) ? 'alarm-list-filter-applied' : 'alarm-list'
    },
    page() {
      return this.$route.query.page || 1
    },
    slotList() {
      return [
        {
          name: 'severity',
          isHardcodedColumn: true,
          columnAttrs: {
            'min-width': 120,
            fixed: 'left',
          },
        },
        {
          name: 'subject',
          isHardcodedColumn: true,
          columnAttrs: {
            width: 400,
            label: 'MESSAGE',
            fixed: 'left',
          },
        },
        {
          criteria: JSON.stringify({ name: 'agent' }),
        },
        {
          criteria: JSON.stringify({ name: 'lastOccurredTime' }),
        },
        {
          criteria: JSON.stringify({ name: 'noOfOccurrences' }),
        },
      ]
    },
    openAlarmId() {
      if (this.$route.params.id) {
        this.$emit('showTag', false)
        return parseInt(this.$route.params.id)
      }
      this.$emit('showTag', true)
      return -1
    },
  },
  methods: {
    async loadViews() {
      await this.$store
        .dispatch('view/loadGroupViews', {
          moduleName: this.moduleName,
        })
        .then(async () => {
          await this.initViews()
          this.constructTabs()
        })
    },
    async initViews() {
      let { views } = this.groupViews.find(group => !isEmpty(group.views)) || {}
      if (!isEmpty(views)) {
        await this.goToView(views[0])
      }
    },
    async goToView(view) {
      let { viewname } = this.$route.params
      this.selectedView = isNullOrUndefined(viewname) ? view.name : viewname
      if (isNullOrUndefined(viewname)) {
        await this.$router.push({
          params: { viewname: view.name },
        })
      }
    },
    constructTabs() {
      let { views } = this.groupViews.find(group => !isEmpty(group.views)) || {}
      if (!isEmpty(views)) {
        for (const view of views) {
          this.tabs.push({
            label: view.displayName,
            key: view.name,
          })
        }
      }
    },
    opensummary(id) {
      let { name } =
        findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
      if (name) {
        this.$router.push({
          name,
          params: { id, viewname: this.viewname },
          query: { ...this.$route.query, page: this.page },
        })
      }
    },
    tabSwitch() {
      let query = this.$route.query
      this.$router.push({
        params: { viewname: this.selectedView },
        query: { ...query, page: 1 },
      })
    },
    back() {
      let { selectedView } = this
      this.$router.push({
        path: `/iot/agent/alarm/${selectedView}`,
        query: { ...this.$route.query },
      })
    },
  },
}
</script>

<style scoped lang="scss">
.alarm-list-page {
  .fc-timer.alarm-timer .t-label {
    font-size: 16px !important;
    font-weight: 500;
    letter-spacing: 0.6px;
    padding-top: 0;
  }
  .fc-timer.alarm-timer .t-sublabel {
    font-size: 9px !important;
    letter-spacing: 1.1px;
    color: #a5a5a5;
  }
  .alarm-timer {
    padding: 0 !important;
  }
  .fc-list-view-table2 tbody tr.tablerow td:first-child {
    width: 10px !important;
  }
  .fc-alarm-list-view {
    height: 43px !important;
  }

  .alarm-list-filter-applied {
    height: calc(100vh - 300px) !important;
  }

  .alarm-list {
    height: calc(100vh - 215px) !important;
  }

  .fc-badge {
    border-radius: 15px;
    background-color: #edeef6;
    font-size: 10px;
    font-weight: 500;
    text-align: center;
    color: #605f65;
    overflow: hidden;
    text-align: center;
    padding: 1px 10px;
  }

  .mW90 {
    min-width: 90px;
  }

  .mL50 {
    margin-left: 50px;
  }

  .center {
    display: flex;
    justify-content: center;
  }

  .suva-grey {
    color: #8a8a8a;
  }

  .mL10 {
    margin-left: 10%;
  }

  .w15 {
    width: 15px;
  }

  .height100vh {
    height: 100vh;
  }

  .height100vh-100px {
    height: calc(100vh - 100px);
  }

  .display-block {
    display: block;
  }

  .letter-spacing0_7 {
    letter-spacing: 0.7px;
  }

  .hex-grey {
    color: #333333;
  }

  .max-width50 {
    max-width: 50%;
  }

  .spinner {
    background-color: #fff;
    height: 100%;
  }
}
</style>
