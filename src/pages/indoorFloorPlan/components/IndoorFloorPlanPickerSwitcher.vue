<template>
  <div class="width100 fp-picker-switcher relative" v-if="!loading">
    <i class="el-icon-office-building fp-picker-prefix-icon"></i>
    <el-cascader
      :ref="`floorplan-swicther`"
      class="width100 el-input-textbox-full-border fp-picker-selecter"
      v-model="value"
      :options="switcherData"
      :props="propData"
      @change="expandChange"
      filterable
    >
      <template slot-scope="{ data }">
        <span>{{ `${data.label}` }}</span>
      </template>
    </el-cascader>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import IndoorFloorPlanSwitcher from 'src/pages/indoorFloorPlan/components/IndoorFloorPlanSwitcher.vue'
export default {
  extends: IndoorFloorPlanSwitcher,
  data() {
    let self = this
    return {
      loading: true,
      value: [],
      propData: {
        lazy: true,
        async lazyLoad(node, resolve) {
          if (!isEmpty(node.data)) {
            let {
              data: { id },
            } = node
            await self.loadFloor(id)
            let data = self.formatChildrenData(id)
            resolve(data)
          } else {
            resolve([])
          }
        },
      },
    }
  },
  mounted() {
    this.initData()
  },
  computed: {
    formatedData() {
      let data = []
      if (!isEmpty(this.filterdBuildingMap)) {
        Object.values(this.filterdBuildingMap).forEach(buildings => {
          data = [...data, ...buildings]
        })
      }
      return data
    },
    switcherData() {
      this.formatedData.forEach(building => {
        if (building) {
          this.$set(building, 'label', building.name)
          this.$set(building, 'value', building.id)
        }
      })
      return this.formatedData
    },
  },
  methods: {
    async initData() {
      this.loading = true
      if (this.buildingId) {
        this.value = [this.buildingId]
        if (this.floorId) {
          this.value.push(this.floorId)
          await this.loadFloor(this.buildingId).then(() => {
            let data = this.formatChildrenData(this.buildingId) || []
            this.switcherData.forEach(building => {
              if (building.id === this.buildingId) {
                this.$set(building, 'children', data)
              }
            })
          })
        }
      }
      this.loading = false
    },
    expandChange(value) {
      if (value && value.length && value.length > 1) {
        this.$emit('changeFloor', value[1])
      }
    },
    formatChildrenData(id) {
      let data = this.buildingFloorMap[id]
      if (!isEmpty(data)) {
        data.forEach(floor => {
          this.$set(floor, 'label', floor.name)
          this.$set(floor, 'value', floor.id)
          this.$set(floor, 'leaf', 1)
        })
        return data
      }
      return []
    },
  },
}
</script>
<style lang="scss">
.fp-picker-switcher {
  .fp-picker-selecter .el-input__inner {
    padding-left: 30px;
  }
  .fp-picker-prefix-icon {
    position: absolute;
    z-index: 10;
    top: 13px;
    left: 10px;
    font-size: 15px;
  }
  .fp-picker-selecter .el-input__validateIcon {
    display: none;
  }
}
</style>
