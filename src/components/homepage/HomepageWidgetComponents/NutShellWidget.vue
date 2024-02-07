<template>
  <div v-if="!loading">
    <ArrowSlider
      :cardsLength="formatedWidgetData.length"
      :containerName="'nutshellcard'"
      :height="154"
    >
      <div class="flex-middle nutShellWidget " id="nutshellcard">
        <div
          v-for="(values, key) in formatedWidgetData"
          style="min-width: 240px; margin-right: 12px;"
          :key="key"
        >
          <el-card class="box-card fc-first-sec-card" @click="rediectBooking()">
            <div class="flex-middle mB12">
              <div
                v-bind:class="
                  cardTypeVsColor[values.name] || 'fc-pink-circle-img'
                "
              >
                <inline-svg
                  v-if="values.name == 'assignedDesk'"
                  :src="'svgs/employeePortal/hot-desk'"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
                <inline-svg
                  v-if="values.name == 'desks'"
                  :src="'svgs/employeePortal/hot-desk'"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
                <inline-svg
                  v-else-if="values.name == 'parkingstall'"
                  :src="'svgs/employeePortal/parking'"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
                <inline-svg
                  v-else-if="values.name == 'spacebooking'"
                  :src="'svgs/employeePortal/meeting-room'"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
                <inline-svg
                  v-else-if="values.name == 'visitor'"
                  :src="'svgs/employeePortal/meeting-room'"
                  iconClass="icon text-center icon-md vertical-bottom"
                ></inline-svg>
              </div>
              <div class="fc-portal-booking-name mL10" style="max-width:215px">
                {{ values.primaryText }}
              </div>
            </div>
            <div class="mB12">
              <div
                class="fc-portal-space-name ellipsis"
                style="max-width:215px"
                v-if="values.secondaryText"
              >
                {{ values.secondaryText }}
              </div>
            </div>
            <div class="flex-middle card-bottom-text">
              <div class="inline-flex mB8">
                <div class=" ellipsis letter-spacing0_6 sub-content">
                  <inline-svg
                    v-if="values.floorName || values.buildingName"
                    src="svgs/employeePortal/new_location"
                    iconClass="icon text-center icon-sm vertical-bottom nutshell-location"
                  ></inline-svg>

                  <div class="sub-content pL5 op7 fW400">
                    <template v-if="values.name == 'spacebooking'">
                      <span
                        v-if="values.space && values.name == 'spacebooking'"
                        >{{ values.space.name }}</span
                      >
                      <span v-if="values.floorName">
                        {{ `, ${values.floorName}` }}
                      </span>
                    </template>
                    <template v-else>
                      <span v-if="values">
                        {{ values | getSpaceName }}
                      </span>
                    </template>
                  </div>
                </div>
              </div>
              <div class="inline-flex mB8">
                <div class=" ellipsis letter-spacing0_6">
                  <inline-svg
                    :src="'svgs/employeePortal/ic-date-range_2'"
                    iconClass="icon text-center icon-sm vertical-bottom"
                  ></inline-svg>
                  <span class="op7 fW400"> {{ values.date }}</span>
                </div>
              </div>
              <div class="inline-flex">
                <div class=" ellipsis letter-spacing0_6">
                  <inline-svg
                    :src="'svgs/employeePortal/clock_mini'"
                    iconClass="icon text-center icon-sm vertical-bottom"
                  ></inline-svg>
                  <span class="op7 mL5 fW400">{{ values.time }}</span>
                </div>
              </div>
            </div>
            <div class="redirect-div opacity-none">
              <TabCheckRedirect
                ref="bookingredirect"
                :moduleName="values.moduleName"
                :id="values.recordId"
                src="svgs/external-link2"
                iconClass="icon icon-xs  fill-blue "
                class="flex-middle pointer"
              ></TabCheckRedirect>
            </div>
          </el-card>
        </div>
        <div class="card-empty-state flex-center">
          <inline-svg
            :src="emptyImagePath"
            iconClass="text-center vertical-bottom h170 w100"
          ></inline-svg>
        </div>
      </div>
    </ArrowSlider>
  </div>
</template>
<script>
import baseWidget from 'src/components/homepage/HomepageWidgetComponents/BaseWidget.vue'
import ArrowSlider from '@/ArrowSlider.vue'
import moment from 'moment-timezone'
import Vue from 'vue'
import { mapGetters } from 'vuex'
import TabCheckRedirect from 'src/components/TabCheckRedirect.vue'

export default {
  extends: baseWidget,
  components: { ArrowSlider, TabCheckRedirect },
  props: {
    widgetData: {
      required: true,
    },
  },
  filters: {
    format: values => {
      return `+${values.length - 1} more`
    },
    getSpaceName: values => {
      let spaceName = ''
      if (values.floorName) {
        spaceName += `${values.floorName}`
      }
      if (values.floorName && values.buildingName) {
        spaceName += `, ${values.buildingName}`
      } else if (values.buildingName) {
        spaceName += `${values.buildingName}`
      }

      return spaceName
    },
  },
  data() {
    return {
      loading: true,
      emptyImagePath: 'svgs/employeePortal/nutshell-empty-state-copy-1',
      cardTypeVsColor: {
        spacebooking: 'fc-green-circle-img',
        desks: 'fc-pink-circle-img',
        parkingstall: 'fc-red-circle-img',
        visitor: 'fc-green-blue-img',
      },
      iconMap: {
        1: `svgs/employeePortal/hot-desk`,
        2: `svgs/employeePortal/parking`,
        3: `svgs/employeePortal/meeting-room`,
        4: `svgs/employeePortal/announcement`, // need deliveries icon
        5: `svgs/employeePortal/announcement`,
      },
      emptyStateIconMap: {
        1: 'svgs/employeePortal/nutshell-empty-state-copy-1',
        2: 'svgs/employeePortal/EmptyState/Nutshell-card-1',
        3: 'svgs/employeePortal/EmptyState/Nutshell-card-2',
        4: 'svgs/employeePortal/EmptyState/Nutshell-card-3',
        5: 'svgs/employeePortal/EmptyState/Nutshell-card-4',
      },
      quetsWidget: {
        title: '',
        backgroundColor: this.getRandomColor(),
        widgetType: 2,
      },
      timeFormat: Vue.prototype.$timeformat || 'HH:mm a',
      dateFormat: Vue.prototype.$dateformat,
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),

    formatedWidgetData() {
      let { widgetData } = this
      let array = []
      for (let element in widgetData) {
        if (widgetData[element].length) {
          widgetData[element].forEach(ele => {
            let value = ele
            value.name = element
            value.date = this.getUserDate(value.startTime)
            let time1 = this.getUserTime(value.startTime)
            let time2 = this.getUserTime(value.endTime)
            value.time = `${time1} to ${time2}`
            array.push(value)
          })
        }
      }
      return array
    },
  },
  methods: {
    widgetInit() {
      this.getEmptyImagePath()
    },
    getEmptyImagePath() {
      let { formatedWidgetData } = this
      this.loading = true
      this.emptyImagePath = this.emptyStateIconMap[
        formatedWidgetData.length + 1
      ]
      this.$nextTick(() => {
        this.loading = false
      })
    },
    rediectBooking() {
      if (this.$refs['bookingredirect']) {
        this.$refs['bookingredirect'].openRecordSummary()
      }
    },
    checkEmptyData(values) {
      if (values && values.length && values[0]) {
        return true
      }
      return false
    },
    getRandomColor() {
      return (
        '#' + ((Math.random() * 0xffffff) << 0).toString(16).padStart(6, '0')
      )
    },
    getUserTime(timestamp) {
      return moment(timestamp).format(this.timeFormat)
    },
    getUserDate(timestamp) {
      return moment(timestamp).format(this.dateFormat)
    },
  },
}
</script>
<style>
.fc-first-sec-card {
  cursor: pointer;
}
.opacity-none {
  opacity: 0;
}
.nutShellWidget .el-card {
  border: none;
}
.fc-portal-booking-name {
  font-weight: 500;
  font-size: 14px;
  line-height: normal;
  letter-spacing: normal;
  color: #464646;
}
.fc-portal-space-name {
  font-size: 14px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: 0.5px;
  color: #324056;
  line-height: 14px;
}
.nutShellWidget {
  overflow-x: scroll;
  width: 100%;
}
.flex-center {
  display: flex;
  justify-content: center;
}
.sub-content {
  display: flex;
  flex-direction: row;
}
.nutshell-location {
  opacity: 0.5;
}
.card-empty-state {
  margin: 0px 20px;
  width: 100%;
  display: flex;
  justify-content: center;
}
.w100 {
  width: 100%;
}
.mB8 {
  margin-bottom: 8px;
}
.mB12 {
  margin-bottom: 12px;
}
.fW400 {
  font-weight: 400;
}
.card-bottom-text {
  font-size: 12px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: justify;
  color: #000;
  display: block;
  width: 93%;
}
.m5 {
  margin: 5px;
}
.op7 {
  opacity: 0.7;
}
.more-chip {
  position: absolute;
  right: 2px;
  padding: 0.15rem 6px;
  background: #ff4946;
  border-radius: 10px;
  color: #fff;
  font-size: 0.7rem;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.33;
  letter-spacing: 0.5px;
  text-align: center;
  color: #fff;
  bottom: -2px;
}
.h170 {
  height: 170px;
}
</style>
