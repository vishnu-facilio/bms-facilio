<template>
  <div class="fc-group-action-card" v-if="!loading">
    <ArrowSlider
      :containerName="'groupactioncard'"
      :cardsLength="formatedWidgetData.length"
      :height="200"
    >
      <div
        class="flex-middle nutShellWidget "
        id="groupactioncard"
        style=" overflow-x: scroll; width:100%"
      >
        <div
          style="min-width: 25%; margin-right: 12px;"
          v-for="(widget, index) in formatedWidgetData"
          :key="index"
          id="content"
        >
          <el-card class="box-card fc-second-row-card action-card">
            <!-- <div > -->
            <el-tag size="mini" class="title-tag">{{ widget.title }}</el-tag>
            <div style="min-height:32px; z-index: 2; position: relative;">
              <!-- <el-tooltip
                :content="widget.primaryText"
                placement="bottom"
                effect="dark"
              > -->
              <div class="primary-desc">{{ widget.primaryText }}</div>
              <!-- </el-tooltip> -->
            </div>
            <div
              style="min-height: 46px;margin-top: 16px ; z-index: 2; position: relative;"
            >
              <div class="flex-middle widget-date">
                <inline-svg
                  v-if="widget.date"
                  src="svgs/employeePortal/ic-date-range_2"
                  iconClass="icon text-center icon-md vertical-bottom"
                  style="    margin-right: 5px;"
                ></inline-svg>
                {{ `${widget.date} - ${getDayEnumValue(widget.day)}` }}
              </div>
              <div class="flex-middle widget-date mB0">
                <div class="inline-flex">
                  <!-- <el-tooltip
                    :content="widget.secondaryText"
                    placement="bottom"
                    effect="dark"
                  > -->
                  <div
                    style="  max-width: 185px;"
                    v-if="widget.secondaryText"
                    class=" ellipsis mR10 secondary-text"
                  >
                    <inline-svg
                      v-if="widget.parentModuleName == 'spacebooking'"
                      src="svgs/employeePortal/meeting-room6"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;opacity: 0.4;     margin-right: 5px;"
                    ></inline-svg>
                    <inline-svg
                      v-else-if="widget.parentModuleName == 'desks'"
                      src="svgs/employeePortal/icon-hot-desk"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;opacity: 0.4;     margin-right: 5px;"
                    ></inline-svg>
                    <inline-svg
                      v-else-if="widget.parentModuleName == 'parkingstall'"
                      src="svgs/employeePortal/parking4"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;opacity: 0.4;     margin-right: 5px;"
                    ></inline-svg>
                    <inline-svg
                      v-else-if="widget.moduleName == 'deliveries'"
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;opacity: 0.4;     margin-right: 5px;"
                    ></inline-svg>
                    <inline-svg
                      v-else-if="widget.moduleName == 'invitevisitor'"
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;opacity: 0.4;     margin-right: 5px;"
                    ></inline-svg>
                    <span>{{ widget.secondaryText }}</span>
                  </div>

                  <div class=" ellipsis  secondary-text" v-if="widget.time">
                    <inline-svg
                      :src="'svgs/employeePortal/clock_mini'"
                      iconClass="icon text-center icon-md vertical-bottom"
                      style="height:18px;width=18px;     margin-right: 5px;"
                    ></inline-svg>
                    <span>{{ widget.time }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="trans-btn flex-middle"
              v-if="widget.params && widget.params.record"
            >
              <div class="d-flex overflow-auto">
                <TransitionButtons
                  :key="index"
                  :moduleName="widget.moduleName"
                  :record="widget.params.record"
                  buttonClass="asset-el-btn"
                  @transitionSuccess="handletransitionSuccess"
                  @transitionFailure="handletransitionSuccess"
                ></TransitionButtons>
                <SpaceBookingCancelBtn
                  class="mL10 cancel-btn"
                  :record="widget.params.record"
                  @refreshData="handletransitionSuccess"
                ></SpaceBookingCancelBtn>
              </div>
            </div>

            <div
              class="fc-second-card-bg-img2"
              v-if="widget.parentModuleName === 'desks'"
            >
              <inline-svg
                :src="'svgs/employeePortal/desk'"
                iconClass="text-center vertical-bottom"
              ></inline-svg>
            </div>
            <div
              class="fc-second-card-bg-img2"
              v-else-if="widget.parentModuleName === 'parkingstall'"
            >
              <inline-svg
                :src="'svgs/employeePortal/action-parking'"
                iconClass="text-center vertical-bottom"
              ></inline-svg>
            </div>
            <div
              class="fc-second-card-bg-img"
              v-else-if="widget.parentModuleName === 'spacebooking'"
            >
              <inline-svg
                :src="'svgs/employeePortal/meeting-room'"
                iconClass="text-center vertical-bottom icon-xxxxlg"
              ></inline-svg>
            </div>
            <div
              class="fc-second-card-bg-img2"
              v-else-if="widget.moduleName === 'invitevisitor'"
            >
              <inline-svg
                :src="'svgs/employeePortal/action-visitor'"
                iconClass="text-center vertical-bottom"
              ></inline-svg>
            </div>
            <div
              class="fc-second-card-bg-img2"
              v-else-if="widget.moduleName === 'deliveries'"
            >
              <inline-svg
                :src="'svgs/employeePortal/delivery'"
                iconClass="text-center vertical-bottom"
              ></inline-svg>
            </div>
          </el-card>
        </div>
        <div class="card-empty-state empty-state">
          <inline-svg
            class="w100"
            :src="emptyImagePath"
            iconClass="text-center vertical-bottom h170 w100"
          ></inline-svg>
        </div>
      </div>
    </ArrowSlider>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import TransitionButtons from '@/stateflow/TransitionButtons'
import baseWidget from 'src/components/homepage/HomepageWidgetComponents/BaseWidget.vue'
import ArrowSlider from '@/ArrowSlider.vue'
import moment from 'moment-timezone'
import Vue from 'vue'
import { mapGetters } from 'vuex'
import SpaceBookingCancelBtn from 'src/pages/spacebooking/SpaceBookingCancelBtn.vue'

export default {
  extends: baseWidget,
  components: { TransitionButtons, ArrowSlider, SpaceBookingCancelBtn },
  props: {
    widgetData: {
      required: true,
    },
  },
  data() {
    return {
      dayEnumMap: {
        1: 'Monday',
        2: 'Tuesday',
        3: 'Wednesday',
        4: 'Thursday',
        5: 'Friday',
        6: 'Saturday',
        7: 'Sunday',
      },
      timeFormat: Vue.prototype.$timeformat || 'HH:mm a',
      dateFormat: Vue.prototype.$dateformat,
      loading: true,
      emptyImagePath: 'svgs/employeePortal/actions-empty-state-copy-01',
      emptyStateIconMap: {
        1: 'svgs/employeePortal/actions-empty-state-copy-01',
        2: 'svgs/employeePortal/EmptyState/Action-card-1',
        3: 'svgs/employeePortal/EmptyState/Action-card-2',
        4: 'svgs/employeePortal/EmptyState/Action-card-3',
      },
    }
  },
  computed: {
    ...mapGetters(['getCurrentUser']),
    autoplay() {
      return this.formatedWidgetData.length &&
        this.formatedWidgetData.length > 4
        ? true
        : false
    },
    formatedWidgetData() {
      let array = []
      if (!isEmpty(this.widgetData)) {
        this.widgetData.forEach(value => {
          value.date = this.getUserDate(value.startTime)
          let time1 = this.getUserTime(value.startTime)
          let time2 = this.getUserTime(value.endTime)
          value.time = `${time1} to ${time2}`
          this.$set(value, 'backgroundColor', this.getRandomColor())
          array.push(value)
        })
      }
      return array
    },
    arrow() {
      return this.autoplay ? 'hover' : 'never'
    },
    chunkWidget() {
      return this.splitIntoChunk(this.formatedWidgetData, 4)
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
    getDayEnumValue(day) {
      if (day && day > 0 && day < 8) {
        return this.dayEnumMap[day]
      }
      return ''
    },
    splitIntoChunk(arr, chunk = 4) {
      let chunkArray = []

      for (let i = 0; i < arr.length; i += chunk) {
        let tempArray
        tempArray = arr.slice(i, i + chunk)
        chunkArray.push(tempArray)
      }
      return chunkArray
    },
    handletransitionSuccess() {
      this.$emit('reload')
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
<style lang="scss">
.fc-group-action-card {
  scroll-behavior: smooth;
  .display-flex {
    .d-flex {
      display: contents;
    }
  }
  .fc-second-row-car {
    border: none;
  }
  .asset-el-btn {
    height: auto !important;
    line-height: 1;
    display: inline-block;
    letter-spacing: 0.7px !important;
    border-radius: 3px;
    padding: 8px 15px !important;
    text-transform: inherit;
    border: solid 1px #0053cc;
    background-color: #0053cc;
    &:hover {
      background-color: #0544a1 !important;
    }
  }
}
</style>
<style scoped>
.trans-btn {
  margin-top: 16px;
}
.empty-state {
  display: flex;
  justify-content: center;
}
span.title-tag.el-tag.el-tag--mini.el-tag--light {
  background-color: #22b096;
  color: #fff;
  border-radius: 10px;
  height: 20px;
  display: flex;
  justify-content: center;
  font-size: 10px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  letter-spacing: 0.5px;
  padding: 3px 8px;
  display: initial;
  border: none;
}
.action-card {
  height: 200px !important;
  border: none;
}
.secondary-text {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: justify;
  color: #324056;
  text-transform: lowercase;
}
.primary-desc {
  font-size: 14px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: normal;
  color: #464646;
  margin-top: 10px;
}
.widget-date {
  font-size: 14px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: 1;
  letter-spacing: normal;
  color: #324056;
  margin-bottom: 10px;
}
.button-class {
  width: 90px;
  height: 28px;
  border-radius: 4px;
  background-color: #0053cc;
}
</style>
<style>
.action-card .el-card__body {
  padding: 16px !important;
  height: 200px !important;
  position: relative;
  /* display: flex; */
}
svg.h200 {
  height: 200px;
}
.h170 {
  height: 170px;
}
</style>
