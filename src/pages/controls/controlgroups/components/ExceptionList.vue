<template>
  <div class="exception-list">
    <div
      class="pT15 pB15 white-background flex justify-between"
      style="border-bottom: 1px solid #f7f8f9;"
    >
      <div class="widget-title">Schedule Changes</div>
      <div>
        <pagination
          v-if="listCount"
          :total="listCount"
          :perPage="perPage"
          :currentPage.sync="page"
        ></pagination>
      </div>
    </div>
    <div>
      <div v-if="showLoading" class="text-center width100 pT50">
        <spinner :show="showLoading" size="80"></spinner>
      </div>
      <div
        v-else-if="$validation.isEmpty(exceptionList)"
        class="flex-middle justify-content-center flex-direction-column mT200"
      >
        <img src="~statics/noData-light.png" width="100" height="100" />
        <div class="mT10 label-txt-black f14 op6">
          No Schedule Changes Available.
        </div>
      </div>
      <el-table v-else :data="exceptionList" :max-height="maxHeight">
        <el-table-column
          label="ID"
          header-align="center"
          align="center"
          min-width="100px"
          fixed
        >
          <template v-slot="exception">
            <div class="fc-id">
              {{ '#' + exception.row.id }}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="SUBJECT"
          fixed
          header-align="center"
          align="center"
          min-width="200px"
        >
          <template v-slot="exception">
            {{ exception.row.name }}
          </template>
        </el-table-column>
        <el-table-column
          label="Extend/Reduce"
          header-align="center"
          align="center"
          min-width="200px"
        >
          <template v-slot="exception">
            {{
              !exception.row.offSchedule ? 'Extended Hours' : 'Reduced Hours'
            }}
          </template>
        </el-table-column>

        <el-table-column
          label="Frequency"
          header-align="center"
          align="center"
          min-width="200px"
        >
          <template v-slot="exception">
            {{ exception.row.isDay ? 'Weekly' : `Specific Date` }}
          </template>
        </el-table-column>

        <el-table-column
          label="Requested By"
          header-align="center"
          align="center"
          min-width="200px"
        >
          <template v-slot="exception">
            {{
              exception.row.sysCreatedBy
                ? exception.row.sysCreatedBy.name
                : `---`
            }}
          </template>
        </el-table-column>

        <el-table-column
          label="From"
          header-align="center"
          align="center"
          min-width="150px"
        >
          <template v-slot="exception">
            {{ getHour(exception.row.startTime, exception.row.isDay) }}
          </template>
        </el-table-column>
        <el-table-column
          label="To"
          header-align="center"
          align="center"
          min-width="150px"
        >
          <template v-slot="exception">
            {{ getHour(exception.row.endTime, exception.row.isDay) }}
          </template>
        </el-table-column>

        <el-table-column
          label="Created Time"
          header-align="center"
          align="center"
          min-width="200px"
        >
          <template v-slot="exception">
            {{ getTime(exception.row.sysCreatedTime) }}
          </template>
        </el-table-column>

        <el-table-column
          prop
          label
          width="130"
          class="visibility-visible-actions"
          fixed="right"
        >
          <template v-slot="data">
            <div class="text-center">
              <i
                class="el-icon-edit pointer edit-icon-color visibility-hide-actions mL10"
                data-arrow="true"
                :title="$t('common._common.edit')"
                v-tippy
                @click="editRecord(data.row)"
              ></i>
              <i
                class="el-icon-delete pointer edit-icon-color visibility-hide-actions mL10"
                data-arrow="true"
                :title="$t('common._common.delete')"
                v-tippy
                @click="deleteRecord(data.row.id)"
              ></i>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <ChangeSchedule
      v-if="showChangeSchedule"
      :closeDialog="closeDialog"
      @onSave="scheduleChange"
      :isTenantGroup="true"
      :tenantId="tenantId"
      :group="group"
      :record="formattedSlotData"
      moduleName="controlScheduleExceptionTenant"
      :isEditTypeDisabled="true"
    />
    <DeleteSchedule
      v-if="showDeleteDialog"
      :closeDialog="closeDialog"
      :recordId="deleteRecordId"
      :moduleName="moduleName"
    />
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import helpers from 'src/util/helpers'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import DeleteSchedule from './DeleteSchedule'
import ChangeSchedule from './ChangeSchedule'

const { getOrgMoment: moment } = helpers
export default {
  components: { Pagination, DeleteSchedule, ChangeSchedule },
  props: ['group', 'isLoading', 'moduleName', 'tenantId'],
  data() {
    return {
      loading: false,
      exceptions: [],
      perPage: 30,
      listCount: null,
      page: 1,
      maxHeight: null,
      showDeleteDialog: false,
      deleteRecordId: null,
      showChangeSchedule: false,
      formattedSlotData: null,
    }
  },
  mounted() {
    this.maxHeight = this.$el.offsetHeight - 55 // height of this component - height of el-tabs header
  },
  watch: {
    group: {
      handler() {
        this.loadRecords()
      },
      immediate: true,
    },
    page: 'loadRecords',
  },
  computed: {
    exceptionList() {
      let { exceptions } = this
      return exceptions.map(exception => {
        let { startSchedule, endSchedule } = exception
        if (!isEmpty(startSchedule) && !isEmpty(endSchedule)) {
          startSchedule = JSON.parse(startSchedule)
          endSchedule = JSON.parse(endSchedule)

          let { times: startTimes } = startSchedule
          let { times: endTimes } = endSchedule
          return {
            ...exception,
            startTime: startTimes[0],
            endTime: endTimes[0],
            isDay: true,
          }
        } else {
          let { startTime, endTime } = exception
          return {
            ...exception,
            startTime,
            endTime,
            isDay: false,
          }
        }
      })
    },
    showLoading() {
      return this.loading || this.isLoading
    },
  },
  methods: {
    async loadRecords() {
      this.loading = true
      let {
        group: { parentGroup },
      } = this
      let { id: groupId } = parentGroup || {}
      let filters = {
        tenant: { operatorId: 36, value: [this.tenantId + ''] },
        parentGroup: { operatorId: 36, value: [groupId + ''] },
      }
      let params = {
        page: this.page || 1,
        perPage: this.perPage,
        withCount: true,
        filters: JSON.stringify(filters),
      }
      let { list, error, meta } = await API.fetchAll(this.moduleName, params)
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let {
          pagination: { totalCount },
        } = meta
        this.listCount = totalCount
        this.exceptions = list
      }
      this.loading = false
    },

    getTime(miliseconds) {
      return this.$options.filters.fromNow(miliseconds)
    },
    getHour(time, isDay) {
      if (isDay) return new moment(time, 'HH:mm').format('hh:mm A')
      else return new moment(time, 'x').format('hh:mm A')
    },
    getDate(time) {
      return new moment(time, 'hh:mm A').format('DD/MM')
    },
    closeDialog(isActionExecuted) {
      this.showDeleteDialog = false
      this.showChangeSchedule = false
      if (isActionExecuted) this.$emit('onDelete')
    },
    deleteRecord(id) {
      this.showDeleteDialog = true
      this.deleteRecordId = id
    },
    editRecord(exception) {
      let { startTime, endTime, isDay } = exception
      let formattedSlotData
      if (isDay) {
        formattedSlotData = {
          ...exception,
          exception: exception,
          startTimeMilli: startTime,
        }
      } else {
        formattedSlotData = {
          ...exception,
          exception: exception,
          startTime: new moment(startTime, 'x').format('HH:mm'),
          endTime: new moment(endTime, 'x').format('HH:mm'),
          startTimeMilli: startTime,
        }
      }
      this.formattedSlotData = formattedSlotData
      this.showChangeSchedule = true
    },
    scheduleChange() {
      this.showChangeSchedule = false
    },
  },
}
</script>

<style lang="scss">
.exception-list {
  background-color: #ffffff;
  padding: 0px 10px;
  overflow: hidden;
  position: relative;

  .mT200 {
    margin-top: 200px;
  }
}
</style>
<style lang="scss">
.exception-list .el-tabs__header {
  margin-bottom: 0;
}
</style>
