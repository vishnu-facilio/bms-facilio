<template>
  <div v-if="currCategory" class="group-category-table">
    <div class="table-header-category">
      <div>
        <span class="font-medium">Category : </span>
        {{ currCategory.type.displayName }}
        <span class="font-medium pL8">Assets : </span
        ><span v-for="(asset, index) in currCategory.assetList" :key="index">
          <template v-if="index < maxAssetDisplay">
            {{ isSummary ? asset.asset.name : asset.label }},
          </template>
        </span>
        <span v-if="getRemainingAssetCount">+{{ getRemainingAssetCount }}</span>
      </div>
      <div
        v-if="!isSummary"
        @click="$emit('editTable', { group, category: currCategory })"
        class="pR10 f16 pointer"
      >
        <i class="el-icon-edit"></i>
      </div>
    </div>
    <el-table
      v-if="currCategory.controlPoints"
      :data="currCategory.controlPoints"
      ref="tableList"
      class="width100"
      :fit="true"
      row-class-name="row-bg"
      header-row-class-name="row-bg"
    >
      <el-table-column label="POINTS" align="left">
        <template v-slot="data">
          {{ getPointDisplayName(data.row) }}
        </template>
      </el-table-column>
      <el-table-column label="EVENT TRUE" align="left">
        <template v-slot="data">
          {{ getEventValue(data.row, true) }}
        </template>
      </el-table-column>
      <el-table-column label="EVENT FALSE" align="left">
        <template v-slot="data">
          {{ getEventValue(data.row, false) }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { isEmpty, isObject } from '@facilio/utils/validation'
export default {
  props: ['category', 'group', 'isSummary', 'isEdit'],
  data() {
    return { maxAssetDisplay: 2, currCategory: null }
  },
  computed: {
    getRemainingAssetCount() {
      let { assetList } = this.currCategory
      if (this.maxAssetDisplay < assetList.length)
        return Math.abs(assetList.length - this.maxAssetDisplay)
      else return null
    },
  },
  watch: {
    category: {
      handler(newVal) {
        if (!isEmpty(newVal)) this.serialize()
      },
      immediate: true,
    },
  },
  methods: {
    async serialize() {
      let { isSummary, category, isEdit } = this
      if (isSummary || isEdit) {
        let formattedCategory = category
        let { controlPoints } = formattedCategory
        controlPoints = controlPoints.map(point => {
          let { field } = point
          return { ...point, point: field }
        })
        this.currCategory = { ...formattedCategory, controlPoints }
      } else {
        this.currCategory = category
      }
    },
    getPointDisplayName(row) {
      let displayName = this.$getProperty(row, 'point.displayName')
      return displayName
    },
    getEventValue(row, isEventTrue) {
      let point = this.$getProperty(row, 'point')
      if (!isEmpty(point)) {
        // if (this.isBooleanField(point)) {
        //   let { trueVal: eventTrue, falseVal: eventFalse } = row
        //   if (isEventTrue) {
        //     return eventTrue
        //   } else {
        //     return eventFalse
        //   }
        // } else {
        let { unit } = point
        if (isEventTrue) return `${row.trueVal} ${unit ? unit : ''}`
        else return `${row.falseVal} ${unit ? unit : ''}`
        // }
      }
    },
    isBooleanField(point) {
      let type = this.$getProperty(point, 'dataTypeEnum')
      if (isObject(type)) {
        let { _name } = type
        if (_name === 'BOOLEAN') return true
        else false
      } else {
        if (type === 'BOOLEAN') return true
        else false
      }
    },
  },
}
</script>

<style lang="scss">
.group-category-table {
  .table-header-category {
    background-color: #f1f8ff;
    padding: 15px 10px;
    display: flex;
    flex-direction: row;
    justify-content: space-between;
  }
  .el-table .row-bg {
    background-color: #fafcff;
  }
  .el-table td {
    padding-right: 10px;
    padding-left: 20px;
  }
  .el-table th.is-leaf {
    padding-left: 8px;
    background-color: #fafcff;
  }
}
</style>
