<template>
  <div class="d-flex flex-col-reverse group-table-page">
    <div v-for="(currentGroup, index) in tableGroups" :key="index">
      <div v-if="!$validation.isEmpty(tableGroupData[currentGroup])">
        <div>
          <div class="fc-setup-group-header header-lavender">
            <div class="fc-black-14 bold text-left flex-middle">
              {{ getFormatter(currentGroup) }}
            </div>
          </div>
          <div class="mB20 fc-group-table">
            <el-table
              :data="tableGroupData[currentGroup]"
              :cell-style="{ padding: '12px 20px' }"
              style="width: 100%"
              height="auto"
              class="fc-setup-table"
              :fit="true"
              ref="emailLogsTable"
              @row-click="(...args) => handleRowClick(index, ...args)"
            >
              <el-table-column
                :label="$getProperty(displayNameMap, 'sourceType')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div>
                    {{ getSourceType(emailLog.row) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('setup.users_management.module')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div class="capitalized">
                    {{
                      $getProperty(emailLog.row, 'recordsModuleName') || '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'recordId')"
                width="150"
              >
                <template v-slot="emailLog">
                  <div>
                    {{ $getProperty(emailLog.row, 'recordId') || '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                fixed
                :label="$getProperty(displayNameMap, 'sysCreatedTime')"
                width="150"
              >
                <template v-slot="emailLog">
                  <div>
                    {{
                      getFormatTime(
                        $getProperty(emailLog.row, 'sysCreatedTime')
                      ) || '---'
                    }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'subject')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div
                    class="pL5"
                    v-if="$validation.isEmpty(emailLog.row.subject)"
                  >
                    {{ '---' }}
                  </div>
                  <div v-else>
                    <el-tooltip
                      class="item"
                      effect="dark"
                      :content="$getProperty(emailLog.row, 'subject')"
                      placement="top"
                    >
                      <span class="truncate-text m0"
                        >{{ $getProperty(emailLog.row, 'subject') || '---' }}
                      </span>
                    </el-tooltip>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'to')"
                width="400"
              >
                <template v-slot="emailLog">
                  <div
                    v-if="!canShowToolTip(toList(emailLog))"
                    class="fc-black-14 text-left truncate-text"
                  >
                    {{ getFirstItemInList(toList(emailLog)) }}
                  </div>
                  <div v-else class="width100 flex-middle">
                    <div class="fc-black-14 text-left truncate-text">
                      {{ getFirstItemInList(toList(emailLog)) }}
                    </div>
                    <el-tooltip placement="bottom">
                      <div slot="content">
                        <div
                          v-for="(receiver, index) in toList(emailLog)"
                          :key="index"
                        >
                          <div v-if="index">{{ receiver || '---' }}</div>
                        </div>
                      </div>
                      <span class="plus-more">
                        {{ getListToolTipText(toList(emailLog)) }}
                      </span>
                    </el-tooltip>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'mailStatus')"
                width="150"
              >
                <template v-slot="emailLog">
                  <div>
                    {{ $getProperty(emailLog.row, 'mailStatus') || '---' }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'recipientCount')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div class="fW500">
                    <span class="p5">{{
                      $getProperty(emailLog.row, 'recipientCount') ||
                        $t('emailLogs.zero')
                    }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'deliveredCount')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div class="fW500">
                    <span class="p5">{{
                      $getProperty(emailLog.row, 'deliveredCount') ||
                        $t('emailLogs.zero')
                    }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                :label="$getProperty(displayNameMap, 'bouncedCount')"
                width="200"
              >
                <template v-slot="emailLog">
                  <div class="fW500">
                    <span class="p5">{{
                      $getProperty(emailLog.row, 'bouncedCount') ||
                        $t('emailLogs.zero')
                    }}</span>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import { getTimeInOrgFormat } from 'src/util/helpers'

export default {
  name: 'TableGroups',
  props: ['tableGroups', 'tableGroupData', 'sourceTypeMap', 'displayNameMap'],
  data() {
    return {
      dialogTableData: [],
      showDialog: false,
      status: '',
      loggerId: null,
    }
  },
  methods: {
    canShowToolTip(dataList) {
      if (!isEmpty(dataList)) {
        return dataList.length > 1 ? true : false
      }
      return false
    },
    getFirstItemInList(dataList) {
      if (!isEmpty(dataList)) {
        return dataList[0]
      }
      return '---'
    },
    getListToolTipText(dataList) {
      let { length: dataListLen } = dataList || []
      if (!isEmpty(dataList) && dataListLen > 1) {
        return `+ ${dataListLen - 1} ${this.$t('common._common.more')}`
      }
      return '---'
    },
    toList(emailLogRow) {
      let { row: emailLog } = emailLogRow || {}
      return emailLog?.toList || []
    },
    handleRowClick(tableIndex, row) {
      let { id: loggerId } = row || {}
      if (!isEmpty(loggerId)) {
        this.$router.push({
          name: 'emailLogsSummary',
          params: {
            loggerId,
          },
        })
      }
    },
    getSourceType(row) {
      let { sourceTypeMap } = this
      let { sourceType } = row || {}
      let { [sourceType]: sourceTypeDisplayName } = sourceTypeMap || {}
      return sourceTypeDisplayName || '---'
    },
    getFormatter(timeStamp) {
      return moment(timeStamp)
        .tz(this.$timezone)
        .format('dddd, DD MMM YYYY')
    },
    getFormatTime(timeStamp) {
      return getTimeInOrgFormat(timeStamp)
    },
  },
}
</script>

<style lang="scss" scoped>
.group-table-page {
  .fc-setup-group-header {
    top: 0;
  }
  .fc-group-table {
    .plus-more {
      font-size: 13px;
      color: #39b2c2;
      padding-left: 5px;
    }
    .truncate-text {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      display: inline-block;
      max-width: 80%;
      span {
        padding: 0px;
      }
    }
  }
}
</style>
<style lang="scss">
.group-table-page {
  .header-row {
    overflow-y: scroll;
  }
  .el-table {
    overflow: visible;
    .el-table__header-wrapper {
      position: sticky;
      top: 51px;
      z-index: 50;
      .el-table__cell {
        padding: 15px 20px;
      }
    }
  }
  .el-table__fixed {
    overflow: visible;
    width: 100%;
    z-index: 100;
    .el-table__fixed-header-wrapper {
      position: sticky;
      top: 51px;
      z-index: 150;
      .el-table__cell {
        padding: 15px 20px;
      }
    }
    .el-table__fixed-header-wrapper,
    .el-table__fixed-body-wrapper {
      overflow-x: hidden;
      overflow-y: hidden;
      width: 100%;
    }
  }
  .fc-group-table {
    .pL15 {
      padding-left: 15px !important;
    }
    .el-table__body-wrapper {
      height: auto !important;
    }
    .el-table__expanded-cell {
      background: #f5f7fa;
    }
  }
}
</style>
