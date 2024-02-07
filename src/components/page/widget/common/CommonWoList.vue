<template>
  <div class="asset-sum-empty-block">
    <!-- Widget TopBar Section -->
    <template v-if="isActive">
      <portal :to="portalName" :key="portalName + '-portalwrap'">
        <pagination
          :key="portalName + '-pagination'"
          :currentPage.sync="page"
          :total="totalCount"
          :perPage="perPage"
          class="mT5 mR15 border-right"
        ></pagination>
        <f-search
          v-model="list"
          searchKey="subject"
          :key="portalName + '-search'"
          class="mT5 mR15"
        ></f-search>
      </portal>
    </template>
    <!-- Widget Content -->
    <table class="setting-list-view-table" style="width: 100%">
      <thead>
        <tr v-if="list && list.length > 0">
          <th class="setting-table-th setting-th-text">Subject</th>
          <th class="setting-table-th setting-th-text">
            {{ customTableHeaders.dateColumn[tabType] }}
          </th>
          <th class="setting-table-th setting-th-text">Assigned To</th>
          <th class="setting-table-th setting-th-text">
            {{ customTableHeaders.resolveColumn[tabType] }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading" class="nodata">
          <td colspan="100%" class="text-center p30imp">
            <spinner :show="loading"></spinner>
          </td>
        </tr>
        <tr
          v-else-if="!list || list.length === 0"
          class="nodata border-tlbr-none"
        >
          <td colspan="100%" class="text-center p30imp">
            <div class="mT40">
              <InlineSvg
                src="svgs/emptystate/maintenance"
                iconClass="icon text-center icon-xxxxlg"
              ></InlineSvg>
              <div class="pT20 fc-black-dark f18 bold">
                {{ emptyStateMsg.title }}
              </div>
              <div class="fc-grayish pT10">{{ emptyStateMsg.desc }}</div>
            </div>
          </td>
        </tr>
        <template v-else>
          <tr
            v-for="(workorder, index) in list"
            :key="index"
            @click="goToSummary(workorder)"
          >
            <td>
              <div style="width:100%" class="text-ellipsis">
                {{ workorder.subject }}
              </div>
            </td>
            <td>
              <div style="width:90%" class="text-ellipsis">
                {{ getDateColumnValue(workorder, tabType) }}
              </div>
            </td>
            <td>
              <div style="width:90%" class="text-ellipsis">
                <user-avatar
                  size="md"
                  :user="workorder.assignedTo"
                  :group="workorder.assignmentGroup"
                  :showPopover="true"
                  :showLabel="true"
                  moduleName="workorder"
                ></user-avatar>
              </div>
            </td>
            <td>
              <div style="width:100%" class="text-ellipsis">
                {{ getResolveColumnValue(workorder, tabType) }}
              </div>
            </td>
          </tr>
        </template>
      </tbody>
    </table>
  </div>
</template>
<script>
import FSearch from '@/FSearch'
import Pagination from 'pageWidgets/utils/WidgetPagination'

import UserAvatar from '@/avatar/User'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: { FSearch, Pagination, UserAvatar },
  props: [
    'tabType',
    'url',
    'isActive',
    'portalName',
    'emptyStateMsg',
    'goToSummary',
  ],
  mounted() {
    this.loadData()
    this.loadCount()
  },
  data() {
    return {
      loading: false,
      list: null,
      page: 1,
      perPage: 5,
      totalCount: null,
    }
  },
  methods: {
    loadData() {
      this.loading = true

      let promise = this.$http
        .get(`${this.url}&perPage=${this.perPage}&page=${this.page}`)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.list = [...response.data.result.workorders]
          } else {
            // TODO handle errors
            this.list = null
            this.loading = false
          }
        })
        .catch(() => (this.list = null))
      Promise.all([promise]).finally(() => (this.loading = false))
    },
    loadCount() {
      this.$http
        .get(`${this.url}&count=true`)
        .then(response => {
          if (response.data.responseCode === 0) {
            this.totalCount = response.data.result.workorderscount
          } else {
            // TODO handle errors
            this.totalCount = null
          }
        })
        .catch(() => (this.totalCount = null))
    },
    getDateColumnValue(workorder, tabType) {
      if (tabType === 'closed') {
        if (isEmpty(workorder['actualWorkEnd'])) {
          return '---'
        } else {
          return this.$options.filters.formatDate(
            workorder['actualWorkEnd'],
            true,
            false
          )
        }
      } else if (tabType === 'open') {
        if (isEmpty(workorder['createdTime'])) {
          return '---'
        } else {
          return this.$options.filters.formatDate(
            workorder['createdTime'],
            true,
            false
          )
        }
      } else {
        if (isEmpty(workorder['scheduledStart'])) {
          return '---'
        } else {
          return this.$options.filters.formatDate(
            workorder['scheduledStart'],
            true,
            false
          )
        }
      }
    },
    getResolveColumnValue(workorder, tabType) {
      if (isEmpty(workorder) || isEmpty(workorder.scheduledStart)) {
        return '---'
      } else if (tabType === 'closed') {
        if (isEmpty(workorder.actualWorkDuration)) {
          return '---'
        }

        return this.$helpers.getFormattedDuration(
          workorder.actualWorkDuration,
          'seconds'
        )
      } else if (!isEmpty(workorder.dueDate)) {
        let duration = moment.duration(
          moment(workorder.dueDate).diff(moment(workorder.scheduledStart))
        )
        return this.$helpers.getFormattedDuration(duration, null, true)
      } else {
        return '---'
      }
    },
  },
  computed: {
    customTableHeaders() {
      return {
        dateColumn: {
          upcoming: this.$t('asset.maintenance.scheduled_on'),
          open: this.$t('maintenance.wr_list.created_on'),
          closed: this.$t('asset.maintenance.closed_on'),
        },
        resolveColumn: {
          upcoming: this.$t('asset.maintenance.est_duration_to_res'),
          open: this.$t('asset.maintenance.est_duration_to_res'),
          closed: this.$t('asset.maintenance.time_taken_to_res'),
        },
      }
    },
  },
  watch: {
    page() {
      this.loadData()
    },
  },
}
</script>
<style lang="scss">
.asset-sum-empty-block {
  .setting-list-view-table .nodata {
    border: none;
    border-bottom: none !important;
    &:hover {
      border: none !important;
      border-bottom: none !important;
      cursor: default;
    }
    &:hover td {
      border-top: none !important;
      cursor: default;
    }
  }
}
</style>
