<template>
  <div
    class="fplan-space-info-card"
    v-if="selectedSpaceInfo"
    :style="{
      top: selectedSpaceInfo.top + 'px',
      left: selectedSpaceInfo.left + 'px',
    }"
  >
    <div class="position-relative" v-if="user">
      <div class="popover-content-block">
        <div class="popover-avatar" v-if="user.data.boolean">
          <avatar size="md" :user="user" :showPopover="false"></avatar>
          <!-- <user-avatar size="lg" :user="user" :showPopover="false"></user-avatar> -->
        </div>
        <div class="popover-avatar-det">
          <div class="avatar-name-txt" v-if="user.data.boolean">
            {{ user.name ? user.name : '----' }}
          </div>
          <div class="mail-block" v-if="user.data.boolean">
            <img
              src="~assets/message.svg"
              class="envelope-icon"
              style="height: 13px;vertical-align: middle;"
            />
            <span class="mail-txt ellipsis"
              >{{ user.data ? user.data.singleline : '----' }}
            </span>
          </div>
          <div v-if="user.data.singleline_2 && user.data.boolean">
            <img
              src="~assets/smartphone1.svg"
              class="envelope-icon"
              style="height: 15px;"
            />
            <span class="popover-phone-number">
              {{ user.data ? user.data.singleline_2 : '----' }}
            </span>
          </div>
          <div v-if="user.data.picklist && user.data.boolean" class="pB10">
            <img
              src="~assets/department.svg"
              class="envelope-icon"
              style="height: 15px;"
            />
            <span class="">
              {{ getFieldData('picklist') }}
            </span>
          </div>
          <div>
            <el-tag type="danger" v-if="user.data.boolean">{{
              'Occupied'
            }}</el-tag>

            <el-tag type="success" v-else>{{ 'Unoccupied' }}</el-tag>
          </div>
        </div>
        <div
          v-if="user"
          class="popover-footer desk-color"
          :style="{
            background: color,
            color: '#fff',
            'border-bottom-right-radius': '5px',
            'border-bottom-left-radius': '5px',
            'padding-left': '10px',
            'font-size': '12px',
            'text-align': 'left',
          }"
        >
          {{ user.data.singleline_1 }}
        </div>
      </div>
      <template slot="reference"><slot name="reference"></slot></template>
    </div>
    <div class="position-relative" v-else>
      <div class="popover-content-block unoccupider-block">
        <div class="popover-avatar-det">
          <div>
            <el-tag type="success">{{ 'Unoccupied' }}</el-tag>
          </div>
        </div>
        <div
          v-if="user"
          class="popover-footer desk-color"
          :style="{
            background: color,
            color: '#fff',
            'border-bottom-right-radius': '5px',
            'border-bottom-left-radius': '5px',
            'padding-left': '10px',
            'font-size': '12px',
            'text-align': 'left',
          }"
        >
          {{ user.data.singleline_1 }}
        </div>
      </div>
      <template slot="reference"><slot name="reference"></slot></template>
    </div>
  </div>
</template>

<script>
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import Avatar from '@/Avatar'

export default {
  props: [
    'visibility',
    'element',
    'areas',
    'employeeList',
    'canvasMeta',
    'fields',
  ],
  components: { Avatar },
  data() {
    return {
      selectedSpaceInfo: null,
      user: null,
    }
  },
  computed: {
    employeeId() {
      let { element } = this
      let { target } = element
      if (target && target.floorplan && target.floorplan.employeeId) {
        return target.floorplan.employeeId
      }
      return null
    },
    color() {
      let { area } = this
      if (area && area.styles && area.styles.pointer_bg_circle) {
        return area.styles.pointer_bg_circle.fill
      }
      return 'gray'
    },
    area() {
      let { areas, employeeId } = this
      let a = areas.find(rt => rt.employeeId === employeeId)
      if (a) {
        return a
      }
      return null
    },
  },
  mounted() {
    this.initData()
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
      if (this.employeeId) {
        let obj = this.element.target
        let event = this.element.e
        this.user = this.employeeList.find(rt => rt.id === this.employeeId)
        this.$nextTick(() => {
          let metaObj = this.getMetaObj(event, this.canvasMeta)
          this.selectedSpaceInfo = { ...metaObj }
        })
      } else {
        // this.selectedSpaceInfo = null
      }
    },
    getMetaObj(event, canvasMeta) {
      let popupHeight = 240
      let popupWidth = 300

      let height = 500
      let width = 500
      if (canvasMeta) {
        height = canvasMeta.height
        width = canvasMeta.width
      }
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
        top: event.offsetY + 20,
        left: event.offsetX - 140,
      }
    },
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist') {
          let { options } = fieldobj
          let object = options.find(
            rt => rt.value === this.user.data['picklist'].toString()
          )
          return object.label
        }
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
    openSpace(space) {
      let parentPath = this.findRoute()

      if (parentPath) {
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
.avatar-popover {
  float: left;
  width: 38px;
  padding: 0 !important;
}
.avatar-popover-container .el-popover--plain,
.avatar-popover-container .el-popover {
  max-width: 383px;
  padding-bottom: 30px;
  box-shadow: 0 8px 14px 0 rgba(0, 0, 0, 0.15);
  background-color: #ffffff;
  border: solid 1px #edeaea;
  transition-delay: 0.8s;
  -webkit-transition-delay: 0.8s;
  padding: 0;
}
.avatar-popover-container .el-popover--plain,
.avatar-popover-container .el-popover:hover {
  transition-delay: 0.2s;
  -webkit-transition-delay: 0.2s;
}
.popover-content-block {
  width: 100%;
  padding-top: 0px;
}
.popover-avatar {
  width: 30%;
  padding-left: 10px;
  float: left;
}
.popover-avatar-det {
  width: 70%;
  float: left;
  padding-right: 10px;
  padding-left: 20px;
}
.avatar-name-txt {
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 0.3px;
  text-align: left;
  margin-bottom: 10px;
  color: #333333;
}
.mail-txt {
  width: 300px;
  max-width: 300px;
  overflow-x: hidden;
  word-break: break-all;
  padding-right: 10px;
  font-size: 13px;
  letter-spacing: 0.9px;
  text-align: left;
  padding-left: 5px;
  color: #2b7ec7;
}
.popover-footer ul {
  width: 100%;
  list-style-type: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-left: 0px;
  text-align: center;
  padding-top: 0;
  padding-bottom: 0px;
  margin-top: 0px;
  margin-bottom: 0px;
}
.popover-phone-number {
  padding-right: 5px;
  font-size: 13px;
  letter-spacing: 0.8px;
  text-align: left;
  color: #333333;
}
.popover-footer-list {
  width: 33.3%;
  padding-top: 10px;
  padding-bottom: 10px;
  cursor: pointer;
}
.border-RL {
  border-right: 1px solid #edeaea;
  border-left: 1px solid #edeaea;
}
.popover-footer {
  width: 100%;
  clear: both;
  position: relative;
  border-top: 1px solid #edeaea;
  bottom: 0;
  left: 0;
  right: 0;
  line-height: 2;
}
.mail-block {
  display: inline-flex;
  margin-bottom: 9px;
}
.popover-avatar-det {
  padding: 20px;
}
.open-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #5dc6d5;
}
.result-txt-black {
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.2px;
  text-align: center;
  color: #333333;
}
.overdue-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #e07575;
}
.dueday-result-txt {
  font-size: 22px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-align: center;
  color: #e4ba2d;
}

.popover-avatar .fc-avatar {
  width: 80px;
  height: 80px;
  border-radius: 100%;
  position: relative;
  top: 20px;
  text-align: center;
  left: 10px;
  font-size: 18px;
}
.avatar-popover-container .wo-team-txt {
  margin-left: 10px !important;
  max-width: 100px !important;
  overflow-x: hidden !important;
  text-overflow: ellipsis !important;
  white-space: nowrap !important;
}
</style>
<style lang="scss">
.popover-avatar-det {
  text-align: left !important;
}
.unoccupider-block {
  padding: 0px !important;
}
.unoccupider-block {
  .popover-avatar-det {
    padding: 20px !important;
  }
}
</style>
