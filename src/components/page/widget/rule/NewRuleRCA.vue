<template>
  <div
    v-if="isLoading"
    class="white-background  width-100 flex-middle justify-content-center flex-direction-column flex-grow"
  >
    <Spinner :show="isLoading" size="80"></Spinner>
  </div>
  <div v-else-if="$validation.isEmpty(records)">
    <div class="mT40 mB40 text-center p30imp">
      <InlineSvg
        src="svgs/emptystate/alarmEmpty"
        iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
      ></InlineSvg>
      <div class="pT20 fc-black-dark f18 bold">
        {{ $t('alarm.rules.no_root_cause') }}
      </div>
      <div class="fc-grayish pT10">
        {{ $t('alarm.rules.no_root_cause_created') }}
      </div>
    </div>
  </div>
  <div v-else>
    <div class="d-flex flex-direction-column height-100">
      <div class="top-bar root-cause-header">
        <div class="f15 bold pL15">
          {{ $t('rule.create.root_cause') }}
        </div>
        <Pagination
          :currentPage.sync="page"
          :total="recordCount"
          :perPage="perPage"
          @update:currentPage="loadNextPage"
        ></Pagination>
      </div>
      <div>
        <el-table
          :data="records"
          :header-cell-style="{
            background: '#f3f1fc',
          }"
          class="root-cause-table"
          :fit="true"
          style="width: 100%;"
          @row-click="redirectToSummary"
        >
          <el-table-column
            prop="ruleName"
            label="Rule Name"
            header-align="center"
            align="center"
          >
            <template v-slot="record">
              <div
                class="textoverflow-ellipsis"
                :title="getRecordData(record, 'ruleName')"
                v-tippy="tippyOptions"
              >
                {{ getRecordData(record, 'ruleName') }}
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="severity"
            label="Severity"
            header-align="center"
            align="center"
          ></el-table-column>
          <el-table-column
            label="Fault Type"
            header-align="center"
            align="center"
          >
            <template v-slot="record">
              {{ getRecordData(record, 'faultType') }}
            </template>
          </el-table-column>
          <el-table-column
            label="Category"
            header-align="center"
            align="center"
          >
            <template v-slot="record">
              <div
                class="textoverflow-ellipsis"
                :title="getRecordData(record, 'category')"
                v-tippy="tippyOptions"
              >
                {{ getRecordData(record, 'category') }}
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
import { isEmpty } from '@facilio/utils/validation'
import Pagination from 'pageWidgets/utils/WidgetPagination'
import Spinner from '@/Spinner'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['ruleId', 'moduleName'],
  components: {
    Pagination,
    Spinner,
  },
  data() {
    return {
      isLoading: false,
      records: [],
      page: 1,
      perPage: 50,
      recordCount: null,
      tippyOptions: {
        placement: 'top',
        animation: 'shift-away',
        arrow: true,
      },
    }
  },

  created() {
    this.loadRecords()
  },

  methods: {
    async loadRecords() {
      let { ruleId, page, perPage } = this
      let queryParam = {
        ruleId,
        page,
        perPage,
      }
      this.isLoading = true
      let { data, error } = await API.get(
        '/v3/readingrule/fetchRootCause',
        queryParam
      )
      if (isEmpty(error)) {
        let { result } = data
        let { records, recordCount } = result || {}
        this.records = records
        this.recordCount = recordCount
      } else {
        this.$message.error(error.message)
      }
      this.isLoading = false
    },
    getRecordData(record, property) {
      let { row } = record || {}
      return this.$getProperty(row, `${property}`, '---')
    },
    async loadNextPage(page) {
      this.page = page
      await this.loadRecords()
    },
    redirectToSummary(record) {
      let { id } = record || {}
      let { moduleName, $route } = this
      let { params } = $route
      let { viewname } = params

      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule('newreadingrules', pageTypes.OVERVIEW)
        if (name) {
          this.$router.push({
            name,
            params: { viewname, id },
          })
        }
      } else {
        this.$router.push({
          name: 'newRulesSummary',
          params: { moduleName, viewname, id },
        })
      }
    },
  },
}
</script>
<style lang="scss" scoped>
.root-cause-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;
}
</style>
<style lang="scss">
.root-cause-table {
  .el-table__row td {
    padding-left: 20px;
  }
}
</style>
