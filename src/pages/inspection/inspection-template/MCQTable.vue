<template>
  <div class="mcq-table-container justify-content-center d-flex">
    <el-table
      v-if="!$validation.isEmpty(mcqSummary)"
      :data="mcqSummary"
      style="width: 100%"
      class="mcq-table mT20"
    >
      <el-table-column :label="$t('qanda.template.choice')">
        <template v-slot="data">
          <div>{{ (data.row || {}).option.label }}</div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('qanda.template.response_percent')">
        <template v-slot="data">
          <div>{{ (data.row || {}).percent || 0 }} %</div>
        </template>
      </el-table-column>
      <el-table-column :label="$t('qanda.template.response_count')">
        <template v-slot="data">
          <div>{{ (data.row || {}).count || 0 }}</div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  props: ['question'],
  computed: {
    mcqSummary() {
      let { question } = this
      let { summary, options } = question || {}
      let deserializedSummary = []
      if (summary) {
        deserializedSummary = summary.map(currSummary => {
          let { option } = currSummary
          let optionObject = options.find(
            currOption => currOption.id === option
          )
          return { ...currSummary, option: optionObject }
        })
      }
      return deserializedSummary
    },
  },
}
</script>

<style lang="scss">
.mcq-table {
  border-top: solid 0.5px #c6ecea;
  th {
    border: none;
    border-left: solid 0.5px #c6ecea;
    border-bottom: solid 0.5px #c6ecea;
  }
  td {
    border: none;
    border-left: solid 0.5px #c6ecea;
    padding-left: 30px;
  }
  td:last-child,
  th:last-child {
    border: none;
    border-left: solid 0.5px #c6ecea;
    border-right: solid 0.5px #c6ecea;
  }
}
.mcq-table-container {
  .el-table td.el-table__cell {
    padding-left: 30px;
    padding-right: 30px;
    border-bottom: solid 0.5px #c6ecea;
  }
  .el-table th.el-table__cell {
    background-color: #f1fafa;
    border-bottom: solid 0.5px #c6ecea;
  }
}
</style>
