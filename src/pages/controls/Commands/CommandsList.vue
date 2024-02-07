<template>
  <div class="height100">
    <div
      class="fc-list-view p10 pT0 mT10 fc-list-table-container height100vh fc-table-td-height fc-table-viewchooser pB100"
      v-if="openControlId === -1"
      :class="$route.query.search ? 'fc-list-table-search-scroll' : ''"
    >
      <div v-if="loading" class="flex-middle fc-empty-white">
        <spinner :show="loading" size="80"></spinner>
      </div>
      <div v-else>
        <el-table
          :data="controllogicload"
          style="width: auto;"
          height="auto"
          :fit="true"
          :class="commandsTableHeight()"
        >
          <template slot="empty">
            <img
              class="mT50"
              src="~statics/noData-light.png"
              width="100"
              height="100"
            />
            <div class="mT10 label-txt-black f14 op6">
              {{ $t('common.products.no_control_commands_available') }}
            </div>
          </template>
          <el-table-column
            :label="$t('common.header._reading_name')"
            fixed
            min-width="300"
          >
            <template v-slot="scope">
              <div
                class="max-width300px textoverflow-ellipsis"
                :title="scope.row.field.displayName"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ scope.row.field.displayName }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common._common._asset')"
            fixed
            min-width="250"
          >
            <template v-slot="scope">
              <div
                class="max-width300px textoverflow-ellipsis"
                :title="scope.row.resource ? scope.row.resource.name : ''"
                v-tippy="{
                  placement: 'top',
                  animation: 'shift-away',
                  arrow: true,
                }"
              >
                {{ scope.row.resource ? scope.row.resource.name : '' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column :label="$t('common._common.set_value')" width="150">
            <template v-slot="scope">
              <template v-if="scope.row.value == null">
                null
              </template>
              <template v-else-if="isDecimalField(scope.row.field)">
                {{ Number(scope.row.value).toFixed(1)
                }}{{ scope.row.field.unit }}
              </template>
              <template v-else-if="isBooleanField(scope.row.field)">
                {{
                  $fieldUtils.getDisplayValue(
                    scope.row.field,
                    scope.row.value === 'true' || scope.row.value === '1'
                  )
                }}
              </template>
              <template v-else>
                {{
                  $fieldUtils.getDisplayValue(
                    addEnumMapField(scope.row.field),
                    scope.row.value
                  )
                }}
              </template>
            </template>
          </el-table-column>

          <el-table-column
            :label="$t('common.products.status')"
            width="250"
            prop="id"
          >
            <template v-slot="scope">
              <div v-if="scope.row.status === 1">
                <div class="fc-green-status bold f13 text-left">
                  {{ $t('maintenance.pm_list.success') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 2">
                <div class="fc-orange-status text-left">
                  {{ $t('controls.data_command_status.sent') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 3">
                <div class="fc-red-status f13 text-left">
                  {{ $t('maintenance._workorder.error') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 4">
                <div class="fc-black-13 f13 text-left">
                  {{ $t('controls.data_command_status.sheduled') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 5">
                <div class="fc-black-13 f13 text-left">
                  {{ $t('maintenance._workorder.without_permission') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 6">
                <div class="fc-darkRed-status f13 text-left">
                  {{ $t('controls.data_command_status.failed') }}
                </div>
              </div>
              <div v-else-if="scope.row.status === 7">
                <div class="fc-darkOrange-status f13 text-left">
                  {{ $t('controls.data_command_status.retrying') }}
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column
            :label="$t('common._common.executed_time')"
            min-width="250"
            prop=""
          >
            <template v-slot="scope">
              {{ scope.row.executedTime | formatDate() }}
            </template>
          </el-table-column>

          <el-table-column
            :label="$t('common._common.executed_by')"
            width="250"
            prop="id"
          >
            <template v-slot="scope">
              <div v-if="scope.row.executedMode === 1">
                <user-avatar
                  size="md"
                  class="pm-list-avatar"
                  :user="scope.row.executedBy"
                  :showPopover="false"
                  :showLabel="false"
                ></user-avatar>
              </div>
              <div
                v-else-if="
                  scope.row.executedMode === 2 || scope.row.executedMode === 6
                "
              >
                <user-avatar
                  size="md"
                  class="pm-list-avatar"
                  :user="scope.row.executedBy"
                  :showPopover="false"
                  :showLabel="false"
                ></user-avatar>
              </div>
              <div v-else-if="scope.row.executedMode === 3">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else-if="scope.row.executedMode === 4">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else-if="scope.row.executedMode === 5">
                <div>{{ $t('common.header.automatic') }}</div>
              </div>
              <div v-else>
                <div>---</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :label="$t('common._common.control_action_mode')"
            width="200"
            prop=""
          >
            <template v-slot="scope">
              <div v-if="scope.row.controlActionMode === 1">
                {{ $t('common._common.sandbox') }}
              </div>
              <div v-else-if="scope.row.controlActionMode === 2">
                {{ $t('common._common.live') }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import Spinner from '@/Spinner'
import { isBooleanField, isDecimalField } from '@facilio/utils/field'
import { API } from '@facilio/api'
import { isNull } from '@facilio/utils/validation'
export default {
  data() {
    return {
      controllogicload: [],
      loading: true,
      tableData: [],
    }
  },
  components: {
    Spinner,
    UserAvatar,
  },
  computed: {
    ...mapState({
      controls: state => state.control.controls,
    }),
    openControlId() {
      if (this.$route.params.id) {
        return parseInt(this.$route.params.id)
      }
      return -1
    },
    filters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    page() {
      return this.$route.query.page || 1
    },
  },
  watch: {
    filters: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadControlPoints()
      }
    },
    page: function(newVal, oldVal) {
      if (oldVal !== newVal && !this.loading) {
        this.loadControlPoints()
      }
    },
  },
  mounted: function() {
    this.loadControlPoints()
  },
  created() {
    this.isBooleanField = isBooleanField
    this.isDecimalField = isDecimalField
  },
  methods: {
    async loadControlPoints() {
      let params = {
        includeParentFilter: true,
        page: this.page ? this.page : 1,
        perPage:50,
        moduleName: 'controlActionCommand',
        withCount:true
      }
      let self = this
      self.loading = true
      let url = '/v3/modules/data/list'
      if (this.filters) {
        params['filters'] =  JSON.stringify(this.filters)
      }
      let { error, data, meta } = await API.get(url,params)
      if (error) {
        error.message || this.$t('common._common.error_occured')
      } else {
        self.controllogicload = data['controlActionCommand']
        let { pagination } = meta
        let { totalCount } = pagination
        this.$emit('syncCount', totalCount)
      }
      self.loading = false
    },
    indexMethod(index) {
      return index * 2
    },
    addEnumMapField(fields) {
      let enumMap = fields.values.reduce((acc, object) => {
        acc[object.index.toString()] = object.value
        return acc
      }, {})
      fields.enumMap = enumMap
      return fields
    },
    commandsTableHeight() {
      let { filters } = this
      return isNull(filters) ? 'commands-table-height' : 'filter-applied-table-height'
    },
  },
}
</script>
<style lang="scss" scoped>

.commands-table-height {
    height: calc(100vh - 130px) !important;
}
.filter-applied-table-height {
    height: calc(100vh - 210px) !important;
}

</style>
