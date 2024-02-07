<template>
  <div class="energy-meter-page height100vh">
    <div class="fc-setup-header">
      <div class="setting-title-block fL">
        <div class="setting-form-title">
          {{ $t('common.header.energy_meter') }}
        </div>
        <div class="heading-description">
          {{ $t('setup.list.list_energymeter') }}
        </div>
      </div>
      <div class="action-btn setting-page-btn fR">
        <el-button
          type="primary"
          class="fc-btn-grey-border p5"
          style="width: 40px;height: 42px;"
          @click="runthroughVisible = true"
        >
          <inline-svg
            src="svgs/filter"
            class="vertical-middle fc-grey-svg-color pT10 pL3"
            iconClass="icon icon-md"
          ></inline-svg>
        </el-button>
        <el-button
          type="primary"
          class="setup-el-btn"
          @click="showNewEnergyMeter"
        >
          {{ $t('setup.add.new_energyMeter') }}
        </el-button>
      </div>
      <!-- Energy meter tab section -->
      <el-tabs v-model="activeName" class="fc-setup-tab">
        <el-tab-pane label="Energy Meter" name="first">
          <!-- energy meter  -->
          <div class="row setting-Rlayout mT20 p0">
            <div class="col-lg-12 col-md-12 overflow-x">
              <table class="setting-list-view-table">
                <thead>
                  <tr>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('common.roles.name') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('setup.setupLabel.purpose') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('setup.setupLabel.serving_location') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('setup.setupLabel.is_root_meter') }}
                    </th>
                    <th class="setting-table-th setting-th-text">
                      {{ $t('setup.setupLabel.is_virtual_meter') }}
                    </th>
                    <th class="setting-table-th setting-th-text"></th>
                  </tr>
                </thead>
                <tbody v-if="loading">
                  <tr>
                    <td colspan="100%" class="text-center">
                      <spinner :show="loading" size="80"></spinner>
                    </td>
                  </tr>
                </tbody>
                <tbody v-else-if="!meterList.length">
                  <tr>
                    <td>{{ $t('setup.empty.empty_energyMeter') }}</td>
                  </tr>
                </tbody>
                <tbody v-else>
                  <tr
                    class="tablerow"
                    v-for="(meter, index) in meterList"
                    :key="index"
                    v-loading="loading"
                  >
                    <td>{{ meter.name }}</td>
                    <td>
                      {{
                        meter.purpose && meter.purpose.id
                          ? serviceList[meter.purpose.id]
                          : '---'
                      }}
                    </td>
                    <td>
                      {{
                        meter.purposeSpace &&
                        $store.getters.getSpace(meter.purposeSpace.id)
                          ? $store.getters.getSpace(meter.purposeSpace.id).name
                          : '---'
                      }}
                    </td>
                    <td>{{ meter.root ? 'Yes' : 'No' }}</td>
                    <td>{{ meter.isVirtual ? 'Yes' : 'No' }}</td>
                    <td>
                      <div
                        class="text-left actions"
                        style="margin-top:-3px;margin-right: 15px;text-align:center;width: 80px;"
                      >
                        <i
                          class="el-icon-edit pointer"
                          @click="showEditEnergyMeter(meter)"
                        ></i>
                        &nbsp;&nbsp;
                        <i
                          class="el-icon-delete pointer"
                          @click="deleteEm(meter.id)"
                        ></i>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="Activity Log" name="second">
          <div class="row setting-Rlayout mT20 p0 fc-border1">
            <div
              class="col-lg-12 col-md-12 overflow-x height100 width100 overflow-y-scroll"
            >
              <el-row class="activity-log-header width100 pT20 pB20 pL30 pR30">
                <el-col :span="4">
                  <div class="history-log-header-txt">
                    {{ $t('setup.setupLabel.energy_meter_name') }}
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="history-log-header-txt">
                    {{ $t('setup.setupLabel.startTime_endTime') }}
                  </div>
                </el-col>
                <el-col :span="5">
                  <div class="history-log-header-txt">
                    {{ $t('setup.setupLabel.executed_by') }}
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="history-log-header-txt">
                    {{ $t('setup.setupLabel.executed_time') }}
                  </div>
                </el-col>
                <el-col :span="2">
                  <div class="history-log-header-txt text-right">
                    {{ $t('maintenance._workorder.status') }}
                  </div>
                </el-col>
              </el-row>
              <el-tree
                :data="activities"
                node-key="id"
                :lazy="true"
                :load="loadChildren"
                class="energy-meter-tree"
                :expand-on-click-node="true"
                :empty-text="this.$t('setup.setupLabel.no_ActivityLog')"
              >
                <template v-slot="{ node, data }">
                  <div class="custom-tree-node">
                    <el-row
                      class="history-log-header width100 pT20 pB20 pL5 pR30"
                    >
                      <el-col :span="4">
                        <div
                          class="label-txt-black bold textoverflow-ellipsis"
                          :title="node.label"
                          v-tippy="{
                            placement: 'top',
                            animation: 'shift-away',
                            arrow: true,
                          }"
                        >
                          {{ node.label }}
                        </div>
                      </el-col>
                      <el-col :span="8">
                        <div v-if="node.level === 1" class="label-txt-black">
                          {{ data.info.startTime | formatDate() }} -
                          {{ data.info.endTime | formatDate() }}
                        </div>
                      </el-col>
                      <el-col :span="5">
                        <div
                          v-if="node.level === 1"
                          class="fc-energy-meter-ellipsis"
                        >
                          <user-avatar
                            size="md"
                            :user="$store.getters.getUser(data.info.createdBy)"
                          ></user-avatar>
                        </div>
                      </el-col>
                      <el-col :span="4">
                        <div v-if="node.level === 1" class="label-txt-black">
                          {{ data.info.createdTime | formatDate() }}
                        </div>
                      </el-col>
                      <el-col :span="2" class="fR">
                        <div
                          v-bind:style="{
                            color:
                              $constants.JOB_STATUS[Number(data.info.status)]
                                .color,
                          }"
                        >
                          <!-- {{data.info.status}} -->
                          {{
                            $constants.JOB_STATUS[Number(data.info.status)]
                              .label
                          }}
                        </div>
                      </el-col>
                    </el-row>
                  </div>
                </template>
              </el-tree>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <run-through-filter
      :visible.sync="runthroughVisible"
      @loadfilterlist="loadactivitylog"
      :meterList="meterList"
    ></run-through-filter>
    <new-energy-meter
      v-if="loadEnergyMeterForm"
      :meter="selectedMeter"
      :rule="rule"
      :isNew="isNew"
      @saved="addMeterSaved"
      @canceled="close"
      :visibility.sync="loadEnergyMeterForm"
    ></new-energy-meter>
  </div>
</template>
<script>
import NewEnergyMeter from 'pages/setup/new/NewEnergyMeter'
import RunThroughFilter from 'pages/setup/NewRunThroughFilter'
import UserAvatar from '@/avatar/User'
import { mapState } from 'vuex'

export default {
  title() {
    return 'Energy Meters'
  },
  components: {
    NewEnergyMeter,
    RunThroughFilter,
    UserAvatar,
  },
  data() {
    return {
      loading: true,
      meterList: [],
      parentActivitylog: [],
      meterchildId: null,
      selectedMeter: {},
      parentLogLoading: false,
      isNew: true,
      loadEnergyMeterForm: false,
      runthroughVisible: false,
      rule: null,
      activities: [],
      newEnergyMeter: {
        name: '',
        description: '',
        serialNumber: '',
        space: {
          id: null,
        },
        purposeSpace: {
          id: null,
        },
        purpose: {
          id: null,
        },
        isVirtual: false,
        root: false,
        childMeterExpression: '',
        siteId: null,
      },
      activeName: 'first',
      activeNames: ['1'],
    }
  },
  created() {
    this.$store.dispatch('loadEnergyMeters')
    this.$store.dispatch('loadServiceList')
    this.$store.dispatch('loadSite')
  },
  mounted: function() {
    this.loadaddMeter()
    this.loadactivitylog()
  },

  computed: {
    ...mapState({
      sites: state => state.site,
      serviceList: state => state.serviceList,
    }),
  },

  methods: {
    showNewEnergyMeter: function() {
      this.loadEnergyMeterForm = true
      this.selectedMeter = this.$helpers.cloneObject(this.newEnergyMeter)
      if (this.sites.length === 1) {
        this.selectedMeter.siteId = this.sites[0].id
      }
      this.isNew = true
    },
    showEditEnergyMeter: function(meter) {
      this.loadEnergyMeterForm = true
      this.selectedMeter = this.$helpers.cloneObject(meter)
      this.selectedMeter.space = {
        id:
          this.selectedMeter.space && this.selectedMeter.space.id > -1
            ? this.selectedMeter.space.id
            : null,
      }
      this.selectedMeter.purposeSpace = {
        id:
          this.selectedMeter.purposeSpace && this.selectedMeter.space.id > -1
            ? this.selectedMeter.purposeSpace.id
            : null,
      }
      this.selectedMeter.purpose = {
        id:
          this.selectedMeter.purpose && this.selectedMeter.purpose.id > -1
            ? this.selectedMeter.purpose.id
            : null,
      }
      this.isNew = false
    },
    close: function() {
      this.loadEnergyMeterForm = false
    },
    loadaddMeter: function() {
      let that = this
      that.$http.get('/energymeter/all').then(function(response) {
        that.loading = false
        if (response.status === 200) {
          that.meterList = response.data ? response.data : []
        }
      })
    },
    loadactivitylog() {
      this.parentLogLoading = true
      let url = 'v2/historicalLogger/vmParentMeters'
      this.$http.get(url).then(response => {
        if (response.status === 200) {
          this.parentActivitylog = response.data.result.parentHistoricalLoggers
          if (this.parentActivitylog) {
            this.activities = this.parentActivitylog.map(activity => ({
              id: activity.id,
              label: activity.parentResourceContext.name,
              info: activity,
              children: [],
            }))
          }
          this.parentLogLoading = false
        }
      })
    },
    loadChildren(node, resolve) {
      if (node.level === 1) {
        let id = node.data.id
        this.$http
          .get(`v2/historicalLogger/vmChildMeters?parentId=${id}`)
          .then(response => {
            if (response.data.responseCode === 0) {
              let childMeters = response.data.result.childMeters
              let logs = []
              if (childMeters && childMeters[1]) {
                this.setChildMeters(1, childMeters, id, logs)
              }
              return resolve(logs)
            } else {
              self.$message.error('Error Occured')
            }
          })
      } else {
        return resolve(node.data.children || [])
      }
    },
    setChildMeters(level, meters, id, logs) {
      if (meters[level]) {
        logs.push(
          ...meters[level]
            .filter(meter => meter.dependentId === id)
            .map(meter => {
              let child = {
                id: meter.id,
                label: meter.parentResourceContext.name,
                info: meter,
                children: [],
              }
              this.setChildMeters(level + 1, meters, meter.id, child.children)
              return child
            })
        )
      }
      return []
    },
    addMeterSaved() {
      this.loadaddMeter()
      this.loadEnergyMeterForm = false
    },
    deleteEm(id) {
      let self = this
      self.$dialog
        .confirm({
          title: this.$t('common.wo_report.delete_energy_meter_title'),
          message: this.$t(
            'common.header.are_you_sure_you_want_to_delete_this_meter'
          ),
          rbDanger: true,
          rbLabel: this.$t('common._common.delete'),
        })
        .then(function(value) {
          if (value) {
            self.$http
              .post('/v2/assets/delete', {
                assetsId: [id],
              })
              .then(function(response) {
                if (response.data.responseCode === 0) {
                  self.$message.success(
                    this.$t('common.products.meter_removed_successfully')
                  )
                  let idx = self.meterList.findIndex(meter => meter.id === id)
                  self.meterList.splice(idx, 1)
                }
              })
          }
        })
    },
  },
}
</script>
<style lang="scss">
.energy-meter-page {
  .el-tree-node__expand-icon {
    font-size: 14px;
    color: #000000;
  }
  .custom-tree-node {
    width: 100%;
  }
  .el-tree-node__content {
    height: 50px;
    line-height: 50px;
    border-bottom: 1px solid #ebedf4;
  }
  .activity-log-header {
    background: #fff;
    border-bottom: 1px solid #ebedf4;
  }
  .activity-log-header .history-log-header-txt {
    font-size: 11px;
    font-weight: 600;
    text-transform: uppercase;
    line-height: normal;
    letter-spacing: 1px;
    color: #324056;
  }
  .el-tree-node__content:hover,
  .el-tree-node:focus {
    background-color: #f7fdfe;
  }
  .el-tree {
    height: 100%;
    padding-bottom: 100px;
    overflow-y: scroll;
  }
  .energy-meter-tree .el-icon-loading {
    text-align: center;
    display: none;
  }
  .fc-energy-meter-ellipsis .q-item-label {
    width: 145px;
    display: block;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .history-log-header {
    border-bottom: none;
  }
}
</style>
