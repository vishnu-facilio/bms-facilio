<template>
  <el-dialog
    :visible="centerDialogVisible"
    :title="title"
    width="50%"
    class="fc-dialog-center-container dialog-padding-bottom decommission-log-dialog"
    :append-to-body="true"
    :before-close="handleClose"
  >
   <div class="decommission-container">
    <div v-if="loading" class="flex-middle">
      <spinner :show="loading" size="60"></spinner>
    </div>
    <div class="dialog-style" v-else-if="!exceptionLogCheck">
      <div class="mL30 mT10 f14 time-msg-color">
        <span class="text-capitalize bold">{{ getCommisionedUser }}</span>
        <span>{{ getCommissioningTimeMessage }}</span>
      </div>
      <div class="mL30 mR40 seperator mT20">
        <div class="width100 height40">
          <span
            class="width100 text-left pB5 dialog-header-color text-fc-black f16 fL"
            >{{ $t('setup.decommission.portfolio') }}</span
          >
          <span class="width100 pT5 f12 fc-text-grey">
            {{ getPortfolioDescription }}
          </span>
        </div>
        <div class="mB25">
          <div class="width100 pT20 flex-direction-row">
            <div
              class="width100 fw6 text-capitalize flex-direction-row fL bold f14"
            >
              {{ $t('setup.decommission.resource_name') }}
            </div>
            <div class="width100 flex-direction-row pT8 fL f14">
              {{ resourceName }}
            </div>
          </div>
          <div v-if="!isLogsEmpty" class="width100 flex-direction-row ">
            <div
              class="width25 pT20 inline-block"
              v-for="(key, value, index) in portfolioDependency"
              :key="index"
            >
              <div class="width100 fw6 text-capitalize flex-direction-row">
                {{ value }}
              </div>
              <div class="width100 flex-direction-row pT8">{{ key }}</div>
            </div>
          </div>
        </div>
      </div>
      <div
        class="mL30 mR40 seperator mT20"
        v-if="!isModuleDependencyEmpty && !isLogsEmpty"
      >
        <div class="width100 height30">
          <div
            class="width100 pB5 text-left dialog-header-color text-fc-black f16 fL"
          >
            {{ $t('setup.setupLabel.modules') }}
          </div>
          <div class="width100 f12 fc-text-grey">
            {{ $t('setup.decommission.module_log_description') }}
          </div>
        </div>
        <div class="mB25">
          <div class="width100 flex-direction-row mT20 width100">
            <div
              class="width30 pT10 inline-block"
              v-for="item in moduleDependency"
              :key="item"
            >
              <div
                class="width100 fw6 text-capitalize flex-direction-row font-bold"
              >
                {{ $t(`setup.decommission.${getModuleName(item)}`) }}
              </div>
              <div class="width100 flex-direction-row pT8">
                {{ item.count }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="mL30 mR40 mT20" v-if="isRemarksAvailable">
        <div class="width100 height40">
          <div
            class="width100 text-left dialog-header-color text-fc-black f16 fL"
          >
            {{ getRemarkHeader }}
          </div>
        </div>
        <div >{{ getRemarks }}</div>
      </div>
    </div>
  </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { formatDate } from 'src/util/filters'
export default {
  name: 'DecommissionLogDialog',
  props: ['centerDialogVisible', 'rowData', 'moduleName'],
  data() {
    return {
      decommissionLogs: [],
      loading: false,
    }
  },
  computed: {
    title() {
      let { isDecommissioned } = this
      return `${
        isDecommissioned
          ? this.$t('setup.decommission.decommissioned')
          : this.$t('setup.decommission.recommissioned')
      } log`
    },
    getRemarkHeader() {
      let { isDecommissioned } = this
      return `${this.$t('setup.decommission.remark')} ${
        isDecommissioned ? 'decommissioning' : 'recommissioning'
      }`
    },
    exceptionLogCheck() {
      let { decommissionLogs } = this
      return isEmpty(decommissionLogs)
    },
    commissionedTime() {
      let { decommissionLogs } = this
      return this.$getProperty(decommissionLogs, '0.commissionedTime', null)
    },
    resourceName() {
      let { rowData } = this
      return this.$getProperty(rowData, 'name', null)
    },
    logId() {
      let { rowData } = this
      return this.$getProperty(rowData, 'id', null)
    },
    getPortfolioDescription() {
      let { moduleName } = this
      return `${this.$t(
        'setup.decommission.portfolio_description'
      )} ${moduleName} .`
    },
    portfolioDependency() {
      let { decommissionLogs } = this
      let logValue = JSON.parse(
        this.$getProperty(decommissionLogs, '0.logValue', null)
      )
      let { resource } = logValue || {}
      return resource
    },
    moduleDependency() {
      let { decommissionLogs } = this
      let logValue = JSON.parse(
        this.$getProperty(decommissionLogs, '0.logValue', null)
      )
      let { modules } = logValue || {}
      return modules
    },
    isModuleDependencyEmpty() {
      let { moduleDependency } = this
      return isEmpty(moduleDependency)
    },
    isLogsEmpty() {
      let { portfolioDependency, moduleDependency } = this
      return isEmpty(portfolioDependency) && isEmpty(moduleDependency)
    },
    isDecommissioned() {
      let { decommissionLogs } = this
      return this.$getProperty(decommissionLogs, '0.decommission', null)
    },
    getCommissioningTimeMessage() {
      let { isDecommissioned, commissionedTime, moduleName } = this
      let type = isDecommissioned ? 'decommissioned' : 'recommissioned'
      let time = this.getDecommissionedTime(commissionedTime)
      return ` ${type} this ${moduleName} on ${time}`
    },
    isRemarksAvailable() {
      let { getRemarks } = this
      return !isEmpty(getRemarks)
    },
    getRemarks() {
      let { decommissionLogs } = this
      return this.$getProperty(decommissionLogs, '0.remarks', null)
    },
    getCommisionedUser() {
      return this.$getProperty(
        this,
        'decommissionLogs.0.commissionedBy.name',
        '---'
      )
    },
  },
  created() {
    this.loadDecommissionLogs()
  },
  methods: {
    getDecommissionedTime(time) {
      return !isEmpty(time) ? formatDate(time) : '---'
    },
    async loadDecommissionLogs() {
      this.loading = true
      let { logId } = this
      let filters = {
        id: {
          operatorId: 9,
          value: [`${logId}`],
        },
      }
      let params = {
        filters: JSON.stringify(filters),
        force: true,
      }
      let { error, list } = await API.fetchAll('decommissionLog', params)
      if (!error) {
        this.decommissionLogs = list
        this.loading = false
      } else {
        this.$message.error(error.message)
      }
    },
    handleClose() {
      this.$emit('handleClose')
    },
    getModuleName(data) {
      let { moduleName } = data || {}
      return moduleName?.toLowerCase()
    },
  },
}
</script>
<style lang="scss" scoped>
.decommission-container{
  max-height: 525px;
  overflow-y: scroll;
}
.decommission-log-dialog {
  .dialog-style {
    overflow-y: scroll;
    font-size: 14px;
    min-height: 240px;
    letter-spacing: 0.2px;
  }
  .time-msg-color {
    color: #253f54;
  }

  .seperator {
    border-bottom: 1.5px solid #f0f0f0;
    min-height: 120px;
  }
}
.dialog-header-color {
  color: #1d384e !important;
  font-weight: bolder;
}
</style>
<style lang="scss">
.decommission-log-dialog {
  .el-dialog {
    padding-bottom: 25px !important;
    border-radius: 8px !important;
    .el-dialog__header {
      padding-left: 30px !important;
      padding-right: 20px !important;
      padding-top: 32px !important;
      padding-bottom: 24px !important;
      .el-dialog__headerbtn {
        padding-top: 13px !important;
      }
    }
    .el-dialog__body {
      padding: 10px 0px !important;
    }
    .el-dialog__title {
      text-transform: capitalize;
      color: #324056;
      font-size: 19px;
      font-weight: bold;
      letter-spacing: 0.4px;
    }
  }
}
</style>
