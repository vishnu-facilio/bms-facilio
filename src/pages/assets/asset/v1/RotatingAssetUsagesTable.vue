<template>
  <div class="rotating-asset-table">
    <el-table :data="list" style="width: 100%" height="210px">
      <el-table-column
        prop="id"
        :formatter="getId"
        :label="$t('common._common._id')"
        min-width="200"
      >
      </el-table-column>
      <el-table-column
        prop="name"
        :label="$t('common.products._name')"
        min-width="250"
      >
      </el-table-column>
      <el-table-column prop="id" min-width="150">
        <template v-slot="data">
          <div
            class="rotating-asset-view-container"
            @click="redirectToSummary(data.row.id)"
          >
            <span class="rotating-asset-view">
              <fc-icon
                class="view-icon"
                group="dsm"
                name="open-window"
                size="16"
              ></fc-icon
              >{{ $t('common._common.view') }}</span
            >
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['list', 'moduleName'],
  computed: {
    getSummaryName() {
      let { moduleName } = this
      return moduleName === 'plannedmaintenance'
        ? 'pm-summary'
        : 'inspectionTemplateSummary'
    },
  },
  methods: {
    getId(val) {
      return `#${val.id}`
    },
    redirectToSummary(id) {
      let route
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}

        if (name) {
          route = this.$router.resolve({
            name,
            params: { viewname: 'all', id },
          }).href
        }
      } else {
        route = this.$router.resolve({
          name: this.getSummaryName,
          params: { viewname: 'all', id },
        }).href
      }
      route && window.open(route, '_blank')
    },
  },
}
</script>
<style lang="scss">
@import 'src/pages/Inventory/styles/inventory-styles.scss';
.rotating-asset-table .el-table th.is-leaf {
  background-color: #f0f8ff;
}
.rotating-asset-table .el-table .el-table__cell {
  padding-left: 16px;
}
.rotating-asset-view {
  display: flex;
  color: #0074d1;
  font-size: 14px;
  line-height: 1.43;
  padding-left: 2px;
  cursor: pointer;
  .view-icon {
    fill: #0074d1;
    margin-right: 5px;
  }
}
.rotating-asset-view-container:hover {
  text-decoration: underline;
}
.rotating-asset-view-container {
  display: flex;
  align-items: center;
}
</style>
