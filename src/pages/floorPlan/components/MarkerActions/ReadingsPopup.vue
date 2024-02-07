<template>
  <div
    class="fplan-space-info-card"
    v-if="selectedSpaceInfo && selectedSpaceInfo.space"
    :style="{
      top: selectedSpaceInfo.top + 'px',
      left: selectedSpaceInfo.left + 'px',
    }"
  >
    <div class="position-relative">
      <div @click="close()" class="space-popver-colse-container">
        <i class="el-icon-close pointer"></i>
      </div>
      <el-row>
        <el-col :span="24" class="mT20">
          <space-avatar
            :space="selectedSpaceInfo.space"
            name="false"
            size="xxxlg"
            :hovercard="false"
          ></space-avatar>
        </el-col>
        <el-col :span="24" class="space-title">{{
          selectedSpaceInfo.name
        }}</el-col>
        <template v-if="data">
          <el-col
            :span="24"
            class="space-title"
            v-if="tooltipData && tooltipData.value"
          >
            {{ tooltipData.value }}
            {{ tooltipData.unit ? ' ' + tooltipData.unit : '' }}
          </el-col>
          <el-col
            :span="24"
            class="space-title"
            v-if="tooltipData && !tooltipData.value"
          >
            {{ `No readings` }}
          </el-col>
          <el-col
            :span="24"
            v-if="data.value.description"
            class="space-subtitle"
            >{{ data.value.description }}</el-col
          >
          <el-col :span="24" class="mT30">
            <template>
              <button
                type="button"
                class="footer-btn footer-btn-secondary el-col el-col-24"
                @click="openSpace(selectedSpaceInfo.space, tooltipData)"
              >
                <span>View Details</span>
              </button>
            </template>
          </el-col>
        </template>
      </el-row>
    </div>
  </div>
</template>

<script>
import SpaceAvatar from '@/avatar/Space'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'spaceList',
    'visibility',
    'element',
    'floorPlan',
    'area',
    'data',
    'canvasMeta',
  ],
  components: { SpaceAvatar },
  data() {
    return {
      selectedSpaceInfo: null,
    }
  },
  mounted() {
    this.initData()
  },
  computed: {
    tooltipData() {
      if (this.area && this.area.length) {
        if (this.area[0] && this.area[0].value) {
          return this.area[0].value
        }
      }
      return null
    },
  },
  watch: {
    'element.target': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal) {
          this.initData()
        }
      },
      deep: true,
    },
  },
  methods: {
    initData() {
      if (
        this.element &&
        this.element.target &&
        this.element.target.floorplan &&
        this.element.target.floorplan.spaceId
      ) {
        let obj = this.element.target
        let event = this.element.e
        let { floorplan } = this.element.target
        let { spaceId } = floorplan
        let spaceObj = this.spaceList.find(rt => rt.id === spaceId)
        let metaObj = this.getMetaObj(event, this.canvasMeta)
        this.selectedSpaceInfo = { ...spaceObj, ...metaObj }
      } else {
        this.selectedSpaceInfo = null
      }
    },
    getMetaObj(event, canvasMeta) {
      let popupHeight = 240
      let popupWidth = 250
      let height = canvasMeta.height
      let width = canvasMeta.width
      let calY = event.offsetY + popupHeight
      let calX = event.offsetX + popupWidth
      if (calY > height && calX > width) {
        let x = event.offsetX - popupWidth
        let y = event.offsetY - popupHeight
        if (y < 1) {
          y = 10
        }
        return {
          top: y,
          left: x,
        }
      }
      if (calY > height && calX < width) {
        let x = event.offsetX - popupWidth
        let y = event.offsetY - popupHeight
        if (y < 1) {
          y = 10
        }
        if (x < 1) {
          x = 10
        }
        return {
          top: y,
          left: x,
        }
      }
      return {
        top: event.offsetY,
        left: event.offsetX - 150,
      }
    },
    close() {
      this.$emit('close')
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openSpace(space, tooltipData) {
      let parentPath = this.findRoute()
      let { redirectUrl } = tooltipData || {}

      if (redirectUrl) {
        this.$router.push({ path: redirectUrl })
      } else if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${this.floorPlan.siteId}/space/${space.id}`,
        })
      }
      this.selectedSpaceInfo = null
    },
  },
}
</script>

<style>
.space-popver-colse-container {
  top: 5px;
  position: absolute;
  right: 6px;
  cursor: pointer;
  z-index: 10;
}
</style>
