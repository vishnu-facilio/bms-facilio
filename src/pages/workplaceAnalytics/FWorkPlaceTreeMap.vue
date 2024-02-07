<template>
  <div class="wp-conatiner" id="wp-conatiner">
    <el-container
      v-if="data && data.children"
      class="wp-chart-conatiner"
      ref="wp-chart-conatiner"
    >
      <div
        v-for="(d, index) in sortedTreeData"
        :key="index"
        class="mB20 ct-continer"
      >
        <div
          :key="`name-${index}`"
          class="treemap-floor-header self-center"
          @click="getFloor(d.key)"
          v-if="data.children.length > 1"
        >
          <div class="justify-content-space flex-middle">
            <div>
              {{ `${d.name}` }}
            </div>
            <span class="f13 wp-secondary-text-color">
              <inline-svg
                style="top:2px"
                src="svgs/desk2"
                iconClass="icon text-center fp-icon-inside icon-sm"
              ></inline-svg
              >{{ d.totalDesk }}
            </span>
          </div>
        </div>
        <FNewTreeMap
          :key="index"
          :data="d"
          v-if="renderTreeMap"
          :width="clientWidth"
          :height="treemapHeight"
          :options="options"
          @floor="getFloor"
          @rendred="() => treeMapRendred(index)"
        >
        </FNewTreeMap>
      </div>
    </el-container>
  </div>
</template>
<script>
import FNewTreeMap from '@/FNewTreeMap.vue'
import { API } from '@facilio/api'
import ReportDataUtil from 'src/pages/report/mixins/ReportDataUtil'
import { eventBus } from '@/page/widget/utils/eventBus'
import { mapGetters } from 'vuex'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'
import orderBy from 'lodash/orderBy'

export default {
  mixins: [ReportDataUtil],
  props: ['resultObject', 'reportObject', 'config', 'width', 'height'],
  components: { FNewTreeMap },
  created() {
    this.$store.dispatch('loadTicketStatus', 'desks')
  },
  data() {
    return {
      departments: [],
      renderTreeMap: false,
      data: null,
      switchcomp: false,
      clientHeight: 500,
      clientWidth: 500,
      departmentInfo: {},
      children: [],
      sortedChildren: [],
      floorsList: [],
      tilesList: [
        { label: 'Resquarify tree', value: 'treemapResquarify' },
        { label: 'Binary tree', value: 'treemapBinary' },
        { label: 'Dice tree', value: 'treemapDice' },
        { label: 'Slice tree', value: 'treemapSlice' },
        { label: 'Slice Dice tree', value: 'treemapSliceDice' },
      ],
    }
  },
  computed: {
    ...mapGetters(['getTicketStatus']),
    sortedTreeData() {
      let data = []
      this.data.children.forEach(d => {
        let sortedlist = orderBy(d.children, ['value'], ['desc'])
        let newData = d
        this.$set(newData, 'children', sortedlist)
        data.push(newData)
      })
      return data
    },
    treemapHeight() {
      let offset = 20
      if (this.data?.children && this.data.children.length) {
        let hight = this.clientHeight / this.data.children.length
        let finalheight = hight - offset
        return finalheight > 57 ? finalheight : 57
      }
      return 100 - offset
    },
    querryTileName() {
      return this.$route?.query?.tile || 'treemapBinary'
    },
    floorVsLevels() {
      let levels = {}
      this.floorsList.forEach(rt => {
        this.$set(levels, rt.id, rt.floor.floorlevel)
      })
      return levels
    },
    options() {
      return {
        hideHeader: true,
        tileName: this.querryTileName,
        showTooltip: this.floorId ? true : false,
      }
    },
    buildingId() {
      if (this.$route?.params?.buildingId) {
        return Number(this.$route.params.buildingId)
      }
      return null
    },
    floorId() {
      if (this.$route?.params?.floorId) {
        return Number(this.$route.params.floorId)
      }
      return null
    },
    colorMap() {
      let dataMap = {}

      if (this.departments && this.departments.length) {
        this.departments.forEach(rt => {
          this.$set(dataMap, rt.id, rt.color)
        })
      }
      return dataMap
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async loadFloor() {
      let { buildingId, floorId } = this
      if (buildingId && !floorId) {
        let filter = {
          building: { operatorId: 36, value: [buildingId + ''] },
          indoorFloorPlanId: { operatorId: 2 },
        }

        let url = `/v2/module/data/list`
        let params = {
          moduleName: 'floor',
          viewName: 'hidden-all',
          orderBy: 'floorlevel',
          orderType: 'desc',
          filters: JSON.stringify(filter),
        }
        let { data } = await API.get(url, params)
        if (data) {
          this.floorsList = data.moduleDatas
        }
      }
    },
    sortedChildrenList(children) {
      let newChildrenList = []
      this.floorsList.forEach(floor => {
        let dataChild = children[floor.id]
        if (dataChild) {
          newChildrenList.push(dataChild)
        }
      })

      return newChildrenList
    },
    treeMapRendred(index) {
      if (this.data.children.length - 1 === index) {
        eventBus.$emit('heatmapLoaded', { departmentInfo: this.departmentInfo })
      }
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'workplace-analytics' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/wp/workplacetreemap'
      }
    },
    getFloor(floorId) {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/${this.buildingId}/${floorId}`,
        })
      }
    },
    aggValue(children) {
      let value = 0
      if (children && children.length) {
        children.forEach(rt => {
          if (rt.children) {
            value = this.aggValue(rt.children)
          } else {
            value = value + rt.value
          }
        })
      }
      return value
    },
    switchCompoent() {
      this.switchcomp = !this.switchcomp
    },
    prepareData() {
      return new Promise(resolve => {
        /// to be removed
        // eslint-disable-next-line vue/no-mutating-props
        this.reportObject.options.type = 'treemap'

        this.data = this.formatToWorkplaceTreeMap(
          this.resultObject.reportData.data
        )
        if (!this.floorId) {
          this.children = {}
          this.data.children.forEach(child => {
            this.$set(this.children, child.key, child)
          })
          let data = this.sortedChildrenList(this.children).reverse()
          this.data.children = this.$helpers.cloneObject(data)
        } else {
          let data = this.sortedData(this.data.children).reverse()
          this.data.children = this.$helpers.cloneObject(data)
        }

        this.$set(this.reportObject, 'treeMapData', this.data)

        resolve()
      })
    },
    sortedData(items) {
      return items.sort(function(a, b) {
        return a.key - b.key
      })
    },
    getLookupMap(resultObject) {
      let result = {}
      if (
        resultObject?.report?.dataPoints &&
        resultObject.report.dataPoints.length
      ) {
        let dataPoint = resultObject.report.dataPoints[0]
        if (dataPoint?.xAxis?.lookupMap) {
          result['X'] = dataPoint.xAxis.lookupMap
        }

        if (dataPoint?.groupByFields && dataPoint.groupByFields.length) {
          dataPoint.groupByFields.forEach(rt => {
            if (rt.lookupMap) {
              result[rt.alias] = rt.lookupMap
            }
          })
        }
      }
      return result
    },
    formatToWorkplaceTreeMap(data) {
      //value = (this.getTicketStatus(id, 'asset') || {}).displayName
      let lookupMap = this.getLookupMap(this.resultObject)
      let occupiedList = []
      let { departmentInfo } = this
      data.forEach(floor => {
        let dep = floor.department
        if (dep) {
          let occupiedDataMap = {}
          let occupiedData = []
          let vacantMap = {}
          let vacentCount = 0
          let vacantData = []
          let totalDesk = 0
          this.$set(departmentInfo, floor.X, {})
          dep.forEach(rt => {
            let departmentId = rt.department
            if (departmentId === '') {
              departmentId = -1
            }
            let d = departmentInfo[floor.X][departmentId]
            if (!d) {
              this.$set(departmentInfo[floor.X], departmentId, {})
            }
            if (rt.moduleState && rt.moduleState.length && rt.moduleState[0]) {
              let moduleStateData = rt.moduleState[0]

              if (
                lookupMap['moduleState'][moduleStateData.moduleState] ===
                'Occupied'
              ) {
                this.$set(occupiedDataMap, departmentId, {
                  value: moduleStateData.Id,
                })
                this.$set(departmentInfo[floor.X], departmentId, {
                  ...d,
                  ...{
                    occupied: moduleStateData.Id,
                  },
                })

                if (departmentId === -1) {
                  occupiedData.push({
                    name: 'No department',
                    key: -2,
                    value: moduleStateData.Id,
                    color: '#000',
                    sizePoint: 'Number of Desks',
                    mode: 'Department',
                  })
                } else {
                  occupiedData.push({
                    name: lookupMap['department']
                      ? lookupMap['department'][departmentId]
                      : 'No department',
                    key: departmentId,
                    value: moduleStateData.Id,
                    color: this.colorMap[departmentId] || '#000',
                    sizePoint: 'Number of Desks',
                    mode: 'Department',
                  })
                }
              } else if (
                lookupMap['moduleState'][moduleStateData.moduleState] ===
                'Vacant'
              ) {
                this.$set(vacantMap, departmentId, {
                  value: moduleStateData.Id,
                })
                this.$set(departmentInfo[floor.X], departmentId, {
                  ...d,
                  ...{
                    vacant: moduleStateData.Id,
                  },
                })
                vacentCount = vacentCount + moduleStateData.Id
                vacantData.push({
                  name: lookupMap['department']
                    ? lookupMap['department'][departmentId]
                    : '',
                  key: departmentId,
                  value: moduleStateData.Id,
                  color: this.colorMap[departmentId] || '#d9d9d9',
                  sizePoint: 'Number of Desks',
                  mode: 'Department',
                })
              }
              totalDesk = totalDesk + moduleStateData.Id
            }
          })

          occupiedData.push({
            name: 'Empty',
            key: -1,
            value: vacentCount,
            color: '#d9d9d9',
            sizePoint: 'Number of Desks',
            mode: 'Department',
          })
          occupiedList.push({
            name: lookupMap['X'][floor.X],
            key: floor.X,
            totalDesk: totalDesk,
            children: occupiedData,
          })
          // vacantList.push({
          //   name: lookupMap['X'][floor.X],
          //   key: floor.X,
          //   children: vacantData,
          // })
        }
      })
      return {
        name: 'No of Desks',
        children: occupiedList,
      }
    },
    async init() {
      await this.loadFloor()
      this.calClientHeight()
      this.calClientWidth()
      await this.loadDepartment()
      await this.prepareData()
      await this.render()
    },
    calClientHeight() {
      this.clientHeight = document.getElementById('wp-conatiner')?.clientHeight
        ? document.getElementById('wp-conatiner').clientHeight
        : 500
    },
    calClientWidth() {
      this.clientWidth = document.getElementById('wp-conatiner')?.clientWidth
        ? document.getElementById('wp-conatiner').clientWidth
        : 500
    },
    render() {
      return new Promise(resolve => {
        this.renderTreeMap = false
        this.$nextTick(() => {
          this.renderTreeMap = true
          resolve()
        })
      })
    },
    getMode(mode) {
      switch (parseInt(mode)) {
        case 4:
          return 'Time'
        case 6:
          return 'Site'
        case 7:
          return 'Building'
        case 8:
          return 'Asset'
        default:
          return ''
      }
    },
    async loadDepartment() {
      let params = {
        moduleName: 'department',
      }
      return await API.get(`v2/module/data/list`, params).then(({ data }) => {
        if (data) {
          this.departments = data.moduleDatas
        }
      })
    },
  },
}
</script>
<style scoped>
.wp-conatiner {
  width: 100%;
  height: calc(100vh - 250px);
  overflow: auto;
  /* margin-left: 80px;
  margin-right: 80px; */

  /* display: flex;
  flex-direction: column; */
}
.wp-chart-conatiner {
  /* display: flex;
  flex-direction: column; */
  display: block;
  height: calc(100vh - 250px);
  overflow: auto;
}
#TreeMap {
  width: 100%;
  height: 100%;
}
.treemap-floor-header {
  width: 200px;
  white-space: nowrap;
  background: #fff;
  text-align: left;
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #324049;
  padding: 8px;
  border-radius: 4px 4px 0px 0px;
  border: solid 1px #d9d9e5;
  border-bottom: 0;
}
</style>
<style>
.F-New-Treemap svg {
  border: solid 1px #d9d9e5;
  cursor: pointer;
}
.ct-continer:hover .treemap-floor-header {
  border: solid 1px #0091eb;
  border-bottom: 0;
  cursor: pointer;
}
.ct-continer:hover .F-New-Treemap svg {
  border: solid 1px #0091eb;
  cursor: pointer;
}
.wp-secondary-text-color {
  color: #6b7e91;
  font-weight: 400;
}
</style>
