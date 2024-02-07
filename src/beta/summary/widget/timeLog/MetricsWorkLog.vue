<template>
  <div class="related-list-container" ref="timeLogListContainer">
    <div class="related-list-header">
      <portal
        :to="`action-${widget.id}-${widget.name}`"
        class="header justify-content-space"
      >
        <div class="flex-middle">
          <div
            class="pointer fwBold pL15 f16"
            @click="reloadLog"
            v-tippy="{ arrow: true, arrowType: 'round', animation: 'fade' }"
            :content="$t('common._common.refresh')"
          >
            <i class="el-icon-refresh fwBold f16"></i>
          </div>
          <pagination
            :total="totalCount"
            :current-page.sync="logPage"
            @pagechanged="setPage"
            class="mL10"
          ></pagination>
        </div>
      </portal>
    </div>
    <div class="width100">
      <div class="fc-related-list-table contract-table">
        <div v-if="loading" class="height55vh flex-center-vH">
          <spinner :show="loading" size="80"></spinner>
        </div>
        <div
          class="text-center height55vh flex-center-vH"
          v-else-if="$validation.isEmpty(logModuleData) && !loading"
        >
          <InlineSvg
            src="svgs/emptystate/readings-empty"
            iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
          ></InlineSvg>

          <div class="fc-black-dark f18 bold pL50 line-height10">
            {{ $t('common._common.no_log_available') }}
          </div>
        </div>
        <el-table
          :data="logModuleData"
          style="width: 100%"
          class="fc-table-th-pad0"
          height="100%"
          :fit="true"
          v-else
        >
          <el-table-column class="pR0">
            <template slot="header">
              {{ $t('setup.setup_profile.state') }}
            </template>
            <template v-slot="log">
              <div class="table-subheading">
                {{ $getProperty(log.row, 'fromStatus.displayName') }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="Start Time" class="pR0">
            <template v-slot="log">
              <div class="table-subheading">
                {{ log.row.startTime | formatDate() }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="End Time" class="pR0">
            <template v-slot="log">
              <div v-if="log.row.endTime === -1" class="table-subheading">
                {{ '---' }}
              </div>
              <div v-else-if="log.row.endTime" class="table-subheading">
                {{ log.row.endTime | formatDate() }}
              </div>
              <div v-else class="table-subheading fc-grey-notext14">
                {{ '---' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column class="pR0">
            <template slot="header">
              {{ $t('setup.setupLabel.performed_by') }}
            </template>
            <template v-slot="log">
              <div v-if="log.row.doneBy" class="table-subheading">
                {{ $getProperty(log.row, 'doneBy.name') }}
              </div>
              <div v-else class="table-subheading">{{ '---' }}'</div>
            </template>
          </el-table-column>
          <el-table-column label="Duration" class="pR0">
            <template v-slot="log">
              <div v-if="log.row.duration === -1" class="table-subheading">
                {{ '---' }}'
              </div>
              <div v-else-if="log.row.duration" class="table-subheading">
                {{ $helpers.getFormattedDurationSeconds(log.row.duration) }}
              </div>
              <div v-else class="table-subheading">
                {{ '0sec' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column>
            <template v-slot="log">
              <div
                v-if="log.row.timerEnabled === true"
                class="table-subheading"
                v-tippy
                :title="$t('common.wo_report.state_enabled')"
              >
                <InlineSvg
                  src="svgs/clock"
                  iconClass="icon icon-sm fc-color"
                ></InlineSvg>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import Pagination from './AuditLogPagination'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['details', 'moduleName', 'resizeWidget', 'widget'],

  data() {
    return {
      loading: true,
      logModuleData: [],
      totalCount: 0,
      perPage: 10,
      statusModelData: null,
      performedModelData: null,
      logPage: 1,
      stateListData: [],
      userListData: [],
      userlist: [],
      timerEnabled: false,
    }
  },
  components: {
    Pagination,
    Spinner,
  },
  mounted() {
    this.getLogModule()
    this.getState()
    this.loadusers()
    this.autoResize()
  },
  methods: {
    async getLogModule(force = true) {
      this.loading = true
      let filters = {}
      if (this.statusModelData) {
        filters.fromStatusId = {
          operatorId: 9,
          value: [this.statusModelData],
        }
      }
      if (this.performedModelData) {
        filters.doneById = {
          operatorId: 9,
          value: [this.performedModelData],
        }
      }
      if (this.timerEnabled) {
        filters.timerEnabled = {
          operatorId: 15,
          value: ['true'],
        }
      }
      let { error, data } = await API.get(
        `/v2/statetransition/timelog?parentModuleName=${this.moduleName}&includeParentFilter=true&id=${this.details.id}&page=${this.logPage}&perPage=${this.perPage}`,
        { filters: !isEmpty(filters) ? JSON.stringify(filters) : null },
        { force }
      )
      if (error) {
        let { message } = error
        this.$message.error(message)
      } else {
        this.logModuleData = data.Timelogs || []
        this.totalCount = this.logModuleData.length
      }
      this.loading = false
    },
    reloadLog() {
      this.getLogModule(true)
    },
    statusFilter(status) {
      let filters = {}
      filters.moduleState = {
        operatorId: 36,
        value: [status.id],
      }
      this.getLogModule()
    },
    setPage(page) {
      this.logPage = page
      this.getLogModule()
    },
    async getState() {
      this.loading = true
      let { data, error } = await API.get(`v2/state/list?parentModuleName`, {
        moduleName: this.moduleName,
      })
      if (error) {
        this.$message.error(error.message)
      } else {
        this.stateListData = data.status
      }
      this.loading = false
    },
    loadusers() {
      this.loading = true
      API.get(`/v2/application/users/list?inviteAcceptStatus=true`).then(
        ({ data, error }) => {
          if (error) {
            this.$message.error(error.message || 'Error Occured')
          } else {
            this.userlist = data.users || []
            const uniqueValuesSet = new Set()
            this.userListData = this.userlist.filter(obj => {
              const isPresentInSet = uniqueValuesSet.has(obj.ouid)
              uniqueValuesSet.add(obj.ouid)
              return !isPresentInSet
            })
          }
          this.loading = false
        }
      )
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['timeLogListContainer']
        if (container) {
          let width = container.scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height: 545, width })
          }
        }
      })
    },
  },
}
</script>
<style lang="scss">
.fc-grey-notext14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: 0.2px;
  color: #bdc1c6;
}
</style>
