<template>
  <div class="bacnet-config height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">{{ $t('setup.setup.config') }}</div>
        <div class="heading-description">
          List of all {{ selectedTab }} points
        </div>
      </div>
    </div>

    <div class="container-scroll">
      <div class="row setting-Rlayout">
        <div class="col-lg-12 col-md-12">
          <controller-filter
            :loading.sync="loading"
            @onControllerSelected="onControllerSelected"
          >
            <el-col :span="4">
              <div
                v-if="discovering"
                class="pL10 fc-dark-blue-txt pointer flLeft"
                :class="
                  selectedController.availablePoints <= 0 && newPoints <= 0
                    ? 'pT35'
                    : 'pT30'
                "
              >
                Discovering...
                <span v-if="selectedController.availablePoints > 0"
                  >{{ totalCount }}/{{
                    selectedController.availablePoints
                  }}</span
                >
                <div v-if="newPoints > 0">
                  ({{ newPoints }} new points found)
                </div>
              </div>
              <span
                class="pL10 pT35 fc-dark-blue-txt pointer flLeft"
                v-else
                :disabled="!selectedController.id"
                @click="selectedController ? discover() : null"
                >Discover Points</span
              >
            </el-col>

            <el-col :span="4" class="flRight pT5">
              <el-button
                v-if="selectedTab === 'unconfigured'"
                :disabled="!selectedInstanceIds.length"
                type="primary"
                class="fc-btn-green-medium-fill f13 mT20 flRight"
                @click="configure"
                :loading="configuring"
                >{{ configuring ? 'Configuring...' : 'Configure' }}</el-button
              >
            </el-col>
          </controller-filter>
          <div class="fR commissioning-search-con">
            <pagination
              :total="listCount"
              :perPage="perPage"
              ref="f-page"
              class="commission-pagenation-con"
            ></pagination>
            <div
              v-if="totalCount || quickSearchQueries"
              class="commissioning-search-block"
            >
              <div
                class="row"
                style="margin-right: 20px"
                v-if="showQuickSearches"
              >
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
                      />
                    </svg>
                    <input
                      ref="quickSearchQueries"
                      autofocus
                      type="text"
                      v-model="quickSearchQueries"
                      @keyup="quickSearches()"
                      @keyup.enter="quickSearches(true)"
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
                      />
                    </svg>
                  </div>
                </div>
              </div>
              <div
                class="pointer text-right search-show-hide"
                @click="toggleQuickSearches"
              >
                <span class>
                  <i
                    class="fa fa-search"
                    aria-hidden="true"
                    style="font-size: 14px;"
                  ></i>
                </span>
              </div>
            </div>
          </div>
          <el-tabs
            v-model="selectedTab"
            class="pT20"
            @tab-click="
              listCount = null
              loadInstances()
            "
          >
            <el-tab-pane
              v-for="tab in tabs"
              :label="tab.label"
              :name="tab.key"
              :key="tab.key"
            >
              <table class="setting-list-view-table">
                <thead>
                  <tr>
                    <th
                      style="width:5%;padding: 15px 30px;"
                      v-if="!isConfiguredTab"
                    >
                      <el-checkbox
                        :value="
                          selectedInstanceIds.length &&
                            selectedInstanceIds.length ===
                              filterInstances.filter(
                                instance => instance.configureStatus !== 2
                              ).length
                        "
                        @change="toggleSelectAll"
                      ></el-checkbox>
                    </th>
                    <th class="setting-table-th setting-th-text">NAME</th>
                    <template
                      v-if="
                        selectedController &&
                          $common.isBacnetController(selectedController)
                      "
                    >
                      <th class="setting-table-th setting-th-text">
                        INSTANCE NUMBER
                      </th>
                      <th class="setting-table-th setting-th-text">
                        INSTANCE TYPE
                      </th>
                    </template>
                    <!-- <template v-else-if="selectedController && isNiagaraController">
                      <th class="setting-table-th setting-th-text">PATH</th>
                    </template>-->
                    <th class="setting-table-th setting-th-text">ADDED TIME</th>
                    <th
                      class="setting-table-th setting-th-text"
                      v-if="!isConfiguredTab"
                    >
                      STATUS
                    </th>
                  </tr>
                </thead>
                <tbody v-if="loading">
                  <tr>
                    <td colspan="100%" class="text-center">
                      <spinner :show="loading" size="80"></spinner>
                    </td>
                  </tr>
                </tbody>
                <tbody v-else-if="!filterInstances.length">
                  <tr>
                    <td colspan="100%" class="text-center">
                      No instances available
                    </td>
                  </tr>
                </tbody>
                <tbody v-else>
                  <tr
                    class="tablerow"
                    v-for="instance in filterInstances"
                    :key="instance.id"
                    v-loading="loading"
                  >
                    <td style="width:5%;" v-if="!isConfiguredTab">
                      <el-checkbox
                        :value="selectedInstanceIds.indexOf(instance.id) >= 0"
                        @change="toggleSelection(instance.id)"
                        :disabled="
                          !isConfiguredTab && instance.configureStatus === 2
                        "
                      ></el-checkbox>
                    </td>
                    <td class="max-width400px break-word">
                      {{ instance.instance }}
                    </td>
                    <template
                      v-if="$common.isBacnetController(selectedController)"
                    >
                      <td>{{ instance.objectInstanceNumber }}</td>
                      <td>{{ instance.instanceTypeVal | pascalCase }}</td>
                    </template>
                    <!-- <template v-else-if="isNiagaraController">
                      <td>{{instance.pointPath}}</td>
                    </template>-->
                    <td>{{ instance.createdTime | formatDate }}</td>
                    <td v-if="!isConfiguredTab">
                      {{
                        $constants.CONFIGURE_STATUS[instance.configureStatus]
                      }}
                    </td>
                  </tr>
                </tbody>
              </table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Pagination from '@/list/FPagination'
import ControllerFilter from 'pages/setup/agents/ControllerFilter'
export default {
  components: {
    Pagination,
    ControllerFilter,
  },
  title() {
    return 'Configuration'
  },
  data() {
    return {
      agents: [],
      selectedType: null,
      listCount: null,
      totalCount: 0,
      quickSearchQueries: null,
      showQuickSearches: false,
      perPage: 100,
      controllers: [],
      instances: [],
      filterInstances: [],

      selectedController: {},
      selectedInstanceIds: [],
      selectedInstances: [],
      loading: false,
      configuring: false,
      showTypePopover: false,
      showQuickSearch: false,
      quickSearchQuery: null,
      selectedTab: 'unconfigured',
      tabs: [
        { key: 'unconfigured', label: 'Unconfigured' },
        { key: 'configured', label: 'Configured' },
      ],
      discovering: false,
      discoveringId: null,
      showThresholdDialog: false,
      newPoints: 0,
    }
  },
  computed: {
    isConfiguredTab() {
      return this.selectedTab === 'configured'
    },
    publishData() {
      return this.$store.state.publishdata.data
    },
    publishInfo() {
      return this.$store.state.publishdata.info
    },
    page() {
      return this.$route.query.page || 1
    },
    availableTypes() {
      return this.controllers.map(controller => controller.controllerType)
    },
    isNiagaraController() {
      return this.selectedController.controllerType === 3
    },
    filteredControllers() {
      return this.controllers.filter(
        controller => controller.controllerType === this.selectedType
      )
    },
  },
  watch: {
    publishData: {
      handler(val) {
        if (
          val[this.discoveringId].controllerId === this.selectedController.id &&
          val[this.discoveringId].response
        ) {
          if (val[this.discoveringId].response !== 'failed') {
            this.$message.success('Discovery completed')
          }
          this.discovering = false
          this.loadInstances()
        }
      },
      deep: true,
    },
    publishInfo: {
      handler(val) {
        let info = val[this.selectedController.id]
        if (info && info.publishType === 'devicePoints') {
          this.newPoints += info.count
          this.loadInstances()
        }
      },
      deep: true,
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loading = true
        this.loadInstances()
      }
    },
    selectedTab: function(newVal, oldVal) {
      if (oldVal !== newVal) {
        this.listCount = null
      }
    },
  },
  methods: {
    quickSearches(isEnterClicked) {
      let string = this.quickSearchQueries
      if (string) {
        this.loadInstances(true, isEnterClicked)
      }
    },
    toggleQuickSearches() {
      this.showQuickSearches = !this.showQuickSearches
      if (this.showQuickSearches) {
        this.$nextTick(() => {
          this.$refs.quickSearchQueries.focus()
        })
      }
    },
    closeSearches() {
      this.toggleQuickSearches()
      this.quickSearchQueries = null
      this.listCount = null
      this.totalCount = 0
      this.loadInstances()
    },
    onControllerSelected(controller) {
      this.selectedController = controller || {}
      if (this.publishData) {
        for (let key in this.publishData) {
          let data = this.publishData[key]
          if (
            data.controllerId === this.selectedController.id &&
            !data.response
          ) {
            this.discovering = true
            this.discoveringId = data.id
            return
          }
        }
      }
      this.totalCount = 0
      this.discovering = false
      this.listCount = null
      this.loadInstances()
    },
    loadInstancesCount() {
      let self = this
      let count = true
      let params = ''
      /* if (this.quickSearchQueries) {
        params = params + '&search=' + this.quickSearchQueries
      } */
      this.$http
        .get(
          '/v2/instances/count?controllerId=' +
            this.selectedController.id +
            '&configured=' +
            (this.isConfiguredTab ? 'true' : 'false') +
            '&count=' +
            count +
            params
        )
        .then(response => {
          if (response.data.result.instances) {
            self.listCount = response.data.result.instances[0].count
            this.totalCount = self.listCount
          }
        })
    },
    loadInstances(isSearch, isEnterClicked) {
      if (!isSearch || this.totalCount > this.perPage) {
        if (isSearch && !isEnterClicked) {
          return
        }
        if (!this.listCount) {
          this.$refs['f-page'].reset()
          this.loadInstancesCount()
        }
        let params = ''
        if (this.quickSearchQueries) {
          if (this.listCount) {
            this.$refs['f-page'].reset()
          }
          params = params + '&search=' + this.quickSearchQueries
        }
        this.loading = true
        this.$http
          .get(
            '/v2/instances/' +
              this.selectedController.id +
              '?configured=' +
              (this.isConfiguredTab ? 'true' : 'false') +
              '&perPage=' +
              this.perPage +
              '&page=' +
              this.page +
              params
          )
          .then(response => {
            this.loading = false
            if (response.data.responseCode === 0) {
              this.instances = response.data.result.instances || []
              this.filterInstances = this.instances
            }
          })
          .catch(_ => {
            this.loading = false
          })
      } else {
        let string = this.quickSearchQueries.toLowerCase()
        this.filterInstances = this.instances.filter(function(cust) {
          return (
            cust.instance.toLowerCase().includes(string) ||
            (cust.objectInstanceNumber &&
              cust.objectInstanceNumber.toString().includes(string))
          )
        })
      }
    },
    configure() {
      this.configuring = true
      this.$http
        .post('/v2/instances/configure', {
          ids: this.selectedInstanceIds,
          configured: !this.isConfiguredTab,
          controllerId: this.selectedController.id,
        })
        .then(response => {
          this.configuring = false
          if (response.data.responseCode === 0) {
            this.$message.success('Instances will be configured in a while')
            this.toggleSelectAll(false)
            this.loadInstances()
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(_ => {
          this.configuring = false
        })
    },
    discover() {
      if (this.discovering) {
        return
      }
      this.discovering = true
      this.$http
        .post('/v2/instances/discover', {
          controllerId: this.selectedController.id,
        })
        .then(response => {
          if (response.data.responseCode === 0) {
            let data = response.data.result.data
            this.$store.dispatch('publishdata/listen', data)
            this.discoveringId = data.id
          } else {
            this.$message.error(response.data.message)
            this.discovering = false
          }
        })
        .catch(_ => {
          this.discovering = false
        })
    },
    toggleSelection(instance) {
      let idx = this.selectedInstanceIds.indexOf(instance)
      if (idx === -1) {
        this.selectedInstanceIds.push(instance)
      } else {
        this.selectedInstanceIds.splice(idx, 1)
      }
    },
    toggleSelectAll(val) {
      this.selectedInstanceIds = val
        ? this.filterInstances
            .filter(instance => instance.configureStatus !== 2)
            .map(instance => instance.id)
        : []
    },
  },
}
</script>
<style>
.controller-filter {
  margin-left: 30px;
  margin-top: 30px;
}
.controller-filter .el-input__suffix-inner .el-select__caret {
  color: #333333;
  font-weight: 900;
  font-size: 16px;
}
.controller-filter-popover {
  padding-top: 10px;
  padding-bottom: 10px;
}

.bacnet-config .el-input__inner {
  background: transparent;
}

.search-container2 {
  position: fixed;
  right: 0;
  right: 1%;
  z-index: 2;
  background: #f8f9fa;
  margin-top: 3px;
}
.search-icon3 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  top: 12px;
  left: 0;
  position: absolute;
}
.close-icon3 {
  width: 15px;
  fill: #6f7c87;
  height: 20px;
  position: absolute;
  right: 5px;
  top: 13px;
  cursor: pointer;
}
.quick-search-input3 {
  transition: 0.2s linear;
  padding: 10px 40px 8px 20px !important;
  line-height: 1.8;
  width: 100%;
  margin-bottom: 5px;
  border: none !important;
  outline: none;
  background: transparent;
  border-bottom: 1px solid #6f7c87 !important;
}
.fc-list-search-wrapper-reading {
  background: none !important;
}

.bacnet-config .el-tabs__item {
  padding: 0 22px;
  height: 40px;
  box-sizing: border-box;
  line-height: 40px;
  display: inline-block;
  list-style: none;
  position: relative;
  font-size: 12px;
  letter-spacing: 0.6px;
  font-weight: 500;
  color: #333333;
  text-transform: uppercase;
}

.bacnet-config .el-tabs__item.is-active {
  font-weight: 600;
  letter-spacing: 0.6px;
  /* letter-spacing: 0.6px;
  font-size: 12px; */
}

.bacnet-config .el-tabs__active-bar {
  background: #ef4f8f;
  width: 51px !important;
}

.bacnet-config .el-tabs__nav-wrap::after {
  height: 1px;
  background-color: #f0f0f0;
}

.threshold-value-dialog .threshold-value-item {
  display: flex;
  align-items: center;
}

.bacnet-config .search-container3 {
  position: absolute;
  right: 27px;
  top: 120px;
  z-index: 2;
}
</style>
