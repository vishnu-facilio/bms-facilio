<template>
  <div class="p15">
    <div
      class="widget-heading-triggers d-flex flex-direction-column justify-center pT5 pB5"
      v-if="!$validation.isEmpty(triggers)"
    >
      Inspection Triggers
    </div>
    <div
      class="fc-empty-center mT20"
      ref="inspection-trigger-table"
      v-if="$validation.isEmpty(triggers)"
    >
      <inline-svg
        src="svgs/list-empty"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="bold f14">{{ $t('qanda.template.no_triggers') }}</div>
    </div>
    <div v-else ref="inspection-trigger-table">
      <el-table
        :data="triggers"
        style="width: 100%"
        class="trigger-table mT20"
        :header-cell-style="{ background: '#f3f1fc' }"
      >
        <el-table-column :label="$t('qanda.triggers.id')" width="100">
          <template v-slot="data">
            <div class="fc-id">#{{ (data.row || {}).id }}</div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('qanda.triggers.name')" width="300">
          <template v-slot="data">
            <div>{{ (data.row || {}).name || '---' }}</div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('qanda.triggers.type')" width="250">
          <template v-slot="data">
            <div>{{ getType(data.row) }}</div>
          </template>
        </el-table-column>
        <el-table-column
          :label="$t('qanda.triggers.frequency_type')"
          width="250"
        >
          <template v-slot="data">
            <div>{{ getFrequencyType(data.row) }}</div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('qanda.triggers.created_time')" width="250">
          <template v-slot="data">
            <div>{{ getLastTriggeredTime(data.row) }}</div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details', 'resizeWidget'],
  computed: {
    triggers() {
      let { details } = this
      let { triggers } = details || {}
      return triggers
    },
  },
  mounted() {
    // Since this block called before triggers rendered, used setTimeOut with 0sec
    setTimeout(() => {
      this.autoResize()
    }, 0)
  },
  methods: {
    getLastTriggeredTime(trigger) {
      let { sysCreatedTime } = trigger
      return this.$options.filters.fromNow(sysCreatedTime)
    },
    getFrequencyType(value) {
      let frequencyType = this.$getProperty(
        value,
        'schedule.scheduleInfo.frequencyTypeEnum'
      )
      return frequencyType || '---'
    },
    getType(value) {
      this.autoResize()
      let { type } = value || {}
      return type === 1 ? 'Schedule' : 'Manual'
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['inspection-trigger-table']

        if (container) {
          let width = container.scrollWidth
          let height = container.scrollHeight
          if (this.resizeWidget) {
            this.resizeWidget({ height: height + 100, width })
          }
        }
      })
    },
  },
}
</script>

<style lang="scss">
.trigger-table {
  th {
    border-top: solid 0.5px #f2f5f6;
    border-bottom: solid 0.5px #f2f5f6;
  }
  th > .cell {
    padding: 0px;
  }
  td {
    border-bottom: solid 0.5px #f2f5f6;
  }
  td.el-table__cell {
    padding-left: 20px;
  }
}
</style>
<style>
.widget-heading-triggers {
  font-size: 14px;
  letter-spacing: 1px;
  font-weight: 500;
  color: #385571;
  margin-left: 10px;
}
</style>
