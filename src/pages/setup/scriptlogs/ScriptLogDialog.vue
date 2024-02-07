<template>
  <el-dialog
    :visible.sync="centerDialogVisible"
    :title="title"
    width="40%"
    class="fc-dialog-center-container dialog-padding-bottom script-log-dialog"
    :append-to-body="true"
    :before-close="handleClose"
  >
    <div v-if="loading" class="flex-middle dialog-style">
      <spinner :show="loading" size="60"></spinner>
    </div>
    <div class="dialog-style" v-else-if="exceptionLogCheck">
      <span class="sub-title">{{ $t('setup.scriptlogs.log_value') }}</span>
      <div class="sub-content">
        {{ logValue }}
      </div>
      <div class="seperator"></div>
      <span class="sub-title">{{ $t('setup.scriptlogs.exception') }}</span>
      <div class="sub-content">
        {{ exception }}
      </div>
    </div>
    <div v-else class="dialog-style sub-content">
      <div>{{ getContent }}</div>
      <div class="seperator pT15"></div>
    </div>
  </el-dialog>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  name: 'ScriptDialog',
  props: ['centerDialogVisible', 'rowData'],
  data() {
    return {
      workflowLogSummary: {},
      loading: false,
    }
  },
  computed: {
    title() {
      let { rowData } = this || {}
      let { exceptionAvailable, logAvailable } = rowData || {}
      if (exceptionAvailable && logAvailable) {
        return this.$t('setup.scriptlogs.logvalue_exception')
      } else if (exceptionAvailable) {
        return this.$t('setup.scriptlogs.exception')
      } else {
        return this.$t('setup.scriptlogs.log_value')
      }
    },
    logValue() {
      let { logValue } = this.workflowLogSummary
      return !isEmpty(logValue) ? logValue : ''
    },
    exception() {
      let { exception } = this.workflowLogSummary
      return !isEmpty(exception) ? exception : ''
    },
    getContent() {
      let { logValue, exception } = this
      return !isEmpty(exception) ? exception : logValue
    },
    exceptionLogCheck() {
      let { rowData } = this || {}
      let { exceptionAvailable, logAvailable } = rowData || {}
      return exceptionAvailable && logAvailable
    },
  },
  created() {
    this.loadScripLogSummary()
  },
  methods: {
    async loadScripLogSummary() {
      this.loading = true
      let id = this.$getProperty(this, 'rowData.id', null)
      let { workflowLog, error } = await API.fetchRecord('workflowLog', { id })
      if (isEmpty(error)) {
        this.workflowLogSummary = workflowLog
      }
      this.loading = false
    },
    handleClose() {
      this.$emit('handleClose')
    },
  },
}
</script>
<style lang="scss" scoped>
.script-log-dialog {
  .dialog-style {
    overflow-y: scroll;
    font-size: 14px;
    min-height: 300px;
    max-height: 450px;
    letter-spacing: 0.2px;
  }
  .sub-title {
    font-size: 16px;
    font-weight: bold;
    padding-left: 3% !important;
  }
  .sub-content {
    padding: 5% !important;
    padding-top: 15px !important;
  }
  .seperator {
    width: 100%;
    margin-top: 10px;
    margin-bottom: 15px;
    border-bottom: 1.5px solid #f0f0f0;
  }
}
</style>
<style lang="scss">
.script-log-dialog {
  .el-dialog {
    padding-bottom: 50px !important;
    .el-dialog__header {
      padding-left: 15px;
    }
    .el-dialog__body {
      padding: 10px 0px !important;
    }
    .el-dialog__title {
      text-transform: capitalize;
      color: #324056;
      font-size: 16px;
      font-weight: bold;
      letter-spacing: 0.4px;
    }
  }
}
</style>
