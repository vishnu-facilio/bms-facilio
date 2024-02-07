<template>
  <div class="d-flex flex-direction-column fc-table-td-height kpi-monthly-data">
    <pagination
      key="kpiMonthlyDataPagination"
      :currentPage.sync="page"
      :total="totalCount"
      :perPage="perPage"
      class="mT15 mR10 position-absolute"
      style="right: 0; z-index: 1;"
    ></pagination>
    <el-table
      :data="monthlyData"
      style="width: 100%"
      height="auto"
      :fit="true"
      ref="kpi-table"
    >
      <el-table-column
        prop="name"
        width="230"
        label="Asset Name"
        fixed
      ></el-table-column>
      <el-table-column label="Monthly Data (Kwh)">
        <el-table-column
          :prop="`data[${month.id - 1}]`"
          v-for="month in months"
          :key="month.id"
          :label="month.label"
          width="120"
        ></el-table-column>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import Pagination from 'pageWidgets/utils/WidgetPagination'
export default {
  props: ['moduleName', 'details', 'layoutParams', 'resizeWidget'],
  components: { Pagination },
  data() {
    return {
      page: 1,
      perPage: 10,
      totalCount: 100,
      months: [
        { label: 'January', id: 1 },
        { label: 'February', id: 2 },
        { label: 'March', id: 3 },
        { label: 'April', id: 4 },
        { label: 'May', id: 5 },
        { label: 'June', id: 6 },
        { label: 'July', id: 7 },
        { label: 'August', id: 8 },
        { label: 'September', id: 9 },
        { label: 'October', id: 10 },
        { label: 'November', id: 11 },
        { label: 'December', id: 12 },
      ],
    }
  },
  mounted() {
    this.$nextTick(() => this.autoResize())
  },
  computed: {
    monthlyData() {
      return [
        {
          name: 'SPYKR-Tower',
          data: [
            0.4,
            0.2,
            0.45,
            0.11,
            0.9,
            0.1,
            0.33,
            0.77,
            0.87,
            0.34,
            0.56,
            0.85,
          ],
        },
        {
          name: 'Lumina 1 Main Meter',
          data: [
            0.77,
            0.87,
            0.34,
            0.56,
            0.85,
            0.4,
            0.2,
            0.45,
            0.11,
            0.9,
            0.1,
            0.33,
          ],
        },
        {
          name: 'Spykr Tower Energy per sq.ft',
          data: [
            0.87,
            0.34,
            0.2,
            0.45,
            0.11,
            '---',
            0.9,
            0.1,
            0.33,
            0.56,
            0.85,
            0.4,
          ],
        },
        {
          name: 'Spykr Tower',
          data: [
            0.87,
            0.34,
            0.2,
            0.45,
            0.11,
            0.77,
            0.9,
            0.1,
            0.33,
            0.56,
            '---',
            0.4,
          ],
        },
        {
          name: 'Lumina 1 Main Meter',
          data: [
            0.77,
            0.87,
            0.34,
            0.56,
            0.85,
            0.4,
            0.2,
            0.45,
            0.11,
            0.9,
            0.1,
            0.33,
          ],
        },
        {
          name: 'Spykr C',
          data: [
            0.87,
            '---',
            0.2,
            0.45,
            0.11,
            0.77,
            0.9,
            0.1,
            0.33,
            0.56,
            0.85,
            0.4,
          ],
        },
        {
          name: 'Lumina 1 Main Meter',
          data: [
            0.77,
            0.87,
            0.34,
            0.56,
            0.85,
            0.4,
            0.2,
            0.45,
            0.11,
            0.9,
            0.1,
            0.33,
          ],
        },
        {
          name: 'Spykr Tower 3',
          data: [
            0.87,
            0.34,
            0.2,
            0.45,
            0.11,
            0.77,
            0.9,
            0.1,
            0.33,
            0.56,
            0.85,
            0.4,
          ],
        },
      ]
    },
  },
  methods: {
    autoResize() {
      let { layoutParams } = this
      let tableEl = this.$refs['kpi-table']
        ? this.$refs['kpi-table'].$refs['bodyWrapper'].children[0]
        : null

      let height = tableEl ? tableEl.scrollHeight + 100 : layoutParams.h
      let width = tableEl ? tableEl.scrollWidth : layoutParams.w

      if (this.resizeWidget) {
        this.$nextTick(() => this.resizeWidget({ height, width }))
      }
    },
  },
}
</script>
<style lang="scss">
.kpi-monthly-data {
  .el-table thead.is-group {
    th {
      background: none;
      text-transform: uppercase;
    }
    th:not(.is-leaf) {
      padding-left: 30px;
      color: #39b2c2;
      font-weight: normal;
    }
  }
  .el-table .cell {
    white-space: nowrap;
    text-overflow: ellipsis;
  }
}
</style>
