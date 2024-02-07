<template>
  <div class="planner-summary-container p0 height100">
    <div class="d-flex justify-between items-center pT20 pB20 pR20 pL30">
      <div class="planner-summary-header mB0">
        {{ `${placeholder} ${$t('maintenance.pm.resource_planner')}` }}
      </div>
      <pagination
        :currentPage.sync="page"
        :total="totalCount"
        :perPage="perPage"
        class="self-center mR20"
      ></pagination>
    </div>
    <div v-if="isLoading" class="width100 pB20">
      <spinner class="mT40" :show="isLoading"></spinner>
    </div>
    <el-table
      v-else
      :data="resourcePlannerList"
      height="calc(100vh - 380px)"
      :fit="true"
    >
      <el-table-column width="40">
        <template v-slot="record">
          <span
            v-if="isDecommission(record.row)"
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            class="pointer"
            ><fc-icon
              group="alert"
              name="decommissioning"
              class="pR5 pT5"
              size="16"
            ></fc-icon
          ></span>
        </template>
      </el-table-column>
      <el-table-column prop="resource" :label="mainFieldName">
        <template v-slot="record">
          {{ getResourceValue(record, 'resource') }}
        </template>
      </el-table-column>
      <el-table-column prop="jobPlan" :label="$t('maintenance.pm.job_plan')">
        <template v-slot="record">
          {{ getResourceValue(record, 'jobPlan') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="assignedTo"
        :label="$t('maintenance.wr_list.assignedto')"
      >
        <template v-slot="record">
          {{ getResourceValue(record, 'assignedTo') }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import isEqual from 'lodash/isEqual'
import { getRelatedFieldName } from 'src/util/relatedFieldUtil'

export default {
  name: 'ResourcePlannerList',
  props: ['placeholder'],
  components: { Pagination },
  created() {
    this.loadResources()
  },
  data: () => ({
    isLoading: false,
    moduleName: 'pmResourcePlanner',
    resourcePlannerList: [],
    page: 1,
    totalCount: 0,
    perPage: 10,
  }),
  watch: {
    page: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          this.loadResources()
        }
      },
    },
  },
  computed: {
    plannerId() {
      let { $attrs } = this
      let { planner } = $attrs || {}
      let { id: plannerId } = planner || {}

      return plannerId
    },
    mainFieldName() {
      let { placeholder } = this
      let placeholderHash = {
        Sites: this.$t('maintenance.pm.site_name'),
        Assets: this.$t('maintenance.pm.asset_name'),
        Spaces: this.$t('maintenance.pm.space_name'),
        Buildings: this.$t('maintenance.pm.building_name'),
      }

      return this.$getProperty(
        placeholderHash,
        placeholder,
        this.$t('maintenance.pm.resource')
      )
    },
  },
  methods: {
    async loadResources() {
      let { moduleName, page, plannerId } = this || {}
      this.isLoading = true
      let params = {
        page,
        perPage: 10,
        withCount: true,
      }
      let relatedFieldName = getRelatedFieldName('pmPlanner', moduleName)
      let relatedConfig = {
        moduleName: 'pmPlanner',
        id: plannerId,
        relatedModuleName: moduleName,
        relatedFieldName,
      }

      let { error, list, meta } = await API.fetchAllRelatedList(
        relatedConfig,
        params
      )
      if (!isEmpty(error)) {
        this.$message.error(error.message || 'Error occured')
      } else {
        this.totalCount = this.$getProperty(meta, 'pagination.totalCount')
        this.resourcePlannerList = list
      }
      this.isLoading = false
    },
    isDecommission(data) {
      let { resource } = data || {}
      let { decommission } = resource || {}
      return isEmpty(decommission) ? false : decommission
    },
    getResourceValue(record, prop) {
      let value = this.$getProperty(record, `row.${prop}.name`, '---')
      return value
    },
  },
}
</script>

<style lang="scss">
.planner-summary-container {
  .el-table th.is-leaf {
    background-color: #f3f1fc;
  }
  .el-table .el-table__cell {
    padding-left: 20px;
  }
}
</style>
