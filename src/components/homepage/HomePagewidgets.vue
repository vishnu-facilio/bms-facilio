<template>
  <component
    :ref="componentName"
    v-if="componentName"
    :is="componentName"
    :widget="widget"
    :eventBus="eventBus"
    :key="componentName"
    :widgetData="widgetData"
    @reload="handleReload"
    v-bind="$attrs"
    class="h100"
  ></component>
</template>
<script>
/* eslint-disable vue/no-unused-components */
import { eventBus } from 'src/components/page/widget/utils/eventBus.js'
import { getHomePageWidgetData } from 'src/components/homepage/homepageApi.js'
export default {
  props: ['widget'],
  components: {
    nutshellwidget: () =>
      import(
        'src/components/homepage/HomepageWidgetComponents/NutShellWidget.vue'
      ),
    groupedactioncard: () =>
      import(
        'src/components/homepage/HomepageWidgetComponents/GroupedActionCard.vue'
      ),
    recentreservedspacecard: () =>
      import(
        'src/components/homepage/HomepageWidgetComponents/ReservedSpaces.vue'
      ),
    listview: () =>
      import('src/components/homepage/HomepageWidgetComponents/ListView.vue'),
    spacefinder: () =>
      import(
        'src/components/homepage/HomepageWidgetComponents/SpaceFinder.vue'
      ),
  },
  data() {
    return {
      eventBus,
      widgetData: {},
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    afterWidgetAPIHook() {
      if (this.componentName && this.$refs[this.componentName]) {
        try {
          this.$nextTick(() => {
            this.$refs[this.componentName].widgetInit()
          })
        } catch (e) {
          console.log(e)
        }
      }
    },
    handleReload() {
      this.loadWidgetData()
    },
    init() {
      this.loadWidgetData()
    },
    async loadWidgetData() {
      const self = this
      let { data } = await getHomePageWidgetData(this.componentName)
      if (data?.widgetData) {
        self.widgetData = data.widgetData
      }
      self.afterWidgetAPIHook()
    },
  },
  computed: {
    componentName() {
      if (
        this.widget?.widgetTypeObj?.name &&
        [
          'nutshellwidget',
          'groupedactioncard',
          'recentreservedspacecard',
          'listview',
          'spacefinder',
        ].includes(this.widget.widgetTypeObj.name)
      ) {
        return this.widget.widgetTypeObj.name
      }
      return null
    },
  },
}
</script>
<style lang="scss">
.h100 {
  height: 100%;
}
.fw5 {
  font-weight: 500 !important;
}
.grid-stack .grid-stack-placeholder > .placeholder-content {
  left: 2px;
  right: 2px;
}
.grid-stack > .grid-stack-item > .grid-stack-item-content {
  left: 2px;
  right: 2px;
}
#-\1.grid-stack-item-content.homepage-widget-item {
  box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.09);
}
.homepage-shadow-widget {
  box-shadow: 0 1px 7px 0 rgb(0 0 0 / 9%);
}
.homepage-widget-item .scroll-container {
  height: 100%;
}
.fc-employee-portal-home {
  width: 100%;
  .fc-employee-portal-home-header {
    width: 100%;
    height: 56px;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #d1d1d1;
    .fc-show-result-txt {
      font-size: 12px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: normal;

      color: #b1b1b1;
    }
    .fc-employee-heder-right {
      display: flex;
      align-items: center;
    }
  }
  .fc-employee-portal-add-btn {
    .el-button {
      color: #fff;
      font-size: 14px;
      padding-top: 10px;
      padding-bottom: 10px;
      font-weight: 500;
      line-height: normal;
      letter-spacing: normal;

      color: #fff;
      background-color: #0053cc;
      border: 1px solid #0053cc;
    }
    .el-dropdown__caret-button {
      background: #0053cc;
    }
    .el-dropdown__caret-button::before {
      display: none;
    }
  }
  .fc-portal-badge {
    .el-badge__content {
      width: 20px;
      height: 20px;
      border: solid 0.5px #fff;
      background-color: #c2535a;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      margin-right: 10px;
      margin-top: 8px;
    }
  }
}
.fc-employee-portal-main-sec {
  padding: 16px;
  overflow-y: scroll;
  padding-bottom: 100px;
  height: calc(100vh - 200px);
  .fc-employee-portal-first-sec {
    .fc-employee-portal-first-sec-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }
}
.fc-portal-txt-16 {
  font-size: 14px;
  font-weight: 600;
  line-height: normal;
  letter-spacing: normal;

  color: #464646;
}
.fc-portal-sub-txt-16 {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #464646;
}
.fc-portal-txt-14 {
  font-size: 14px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: 0.5px;
  color: #324056;
}
.fc-portal-title-txt-14 {
  font-size: 14px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #464646;
}
.fc-portal-main-sec {
  padding: 0;
}
.fc-pink-circle-img {
  width: 32px;
  height: 32px;
  background-color: #fc436d;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-yellow-circle-img {
  width: 32px;
  height: 32px;
  background-color: #af8417;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-red-circle-img {
  width: 32px;
  height: 32px;
  background-color: #e00107;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-green-circle-img {
  width: 32px;
  height: 32px;
  background-color: #2f98a5;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-green-blue-img {
  width: 32px;
  height: 32px;
  background-color: #2f2cff;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-first-sec-card {
  border-radius: 4px;
  box-shadow: 0 0 7px 0 rgba(0, 0, 0, 0.09);
  .el-card__body {
    padding: 15px;
    height: 160px;
    position: relative;
    width: 240px;
    .redirect-div {
      position: absolute;
      top: 10px;
      right: 10px;
      display: none;
    }
    &:hover > .redirect-div {
      display: block;
    }
  }
}

.fc-second-row-card {
  position: relative;
  border-radius: 6px;
  box-shadow: 0 0 7px 0 rgba(0, 0, 0, 0.09);
  .el-card__body {
    padding: 12px;
    height: 190px;
  }
}

.fc-black2-txt-14 {
  font-size: 13px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #8ca1ad;
}
.fc-second-card-btn {
  padding-left: 42px;
  padding-right: 42px;
  height: 40px;
  border-radius: 8px;
  background-color: #0053cc;
  border: 1px solid #0053cc;
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  &:hover {
    background-color: #0053cc;
    border: 1px solid #0053cc;
  }
}
.fc-second-card-bg-img {
  position: absolute;
  top: 20px;
  right: -75px;
}
.fc-second-card-bg-img2 {
  position: absolute;
  top: 40px;
  right: 0px;
}
.fc-recently-received-card {
  border-radius: 6px;
  box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.09);
}
.fc-hot-desk-card {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-meeting-desk-card {
  width: 120px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-hotelling-desk-card {
  width: 90px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-txt12-grey {
  font-size: 12px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #6f6f6f;
}
.fc-visit-header {
  padding: 15px 20px;
  border-bottom: 1px solid #f1f1f1;
  display: flex;
  align-items: center;
}
.fc-visit-card {
  .el-card__body {
    padding: 0;
  }
}
.fc-grey-10 {
  font-size: 10px;
  font-weight: normal;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: justify;
  color: #6f6f6f;
}
.fc-visit-body {
  border-bottom: 1px solid #f1f1f1;
}
.fc-portal-border-right {
  border-right: 1px solid #f1f1f1;
}
.fc-card-padding-visit-body {
  padding: 10px 20px;
}
.fc-visit-footer {
  padding: 15px 20px;
  .fc-visit-footer-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 500;
    line-height: normal;
    letter-spacing: normal;
    text-align: center;
    color: #0053cc;
    border-radius: 5px;
    border: solid 1px #0053cc;
    background-color: #fff;
    &:hover,
    &:active,
    &:focus {
      color: #fff;
      border: solid 1px #0053cc;
      background-color: #0053cc;
    }
  }
}
.fc-fourth-sec-body {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.fc-grey-lite {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.14;
  letter-spacing: normal;
  text-align: center;
  color: #324056;
  opacity: 0.5;
}
.service-request-add {
  width: 70px;
  border: 1px dotted #cfcaff;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  padding: 10px;
}
.fc-service-request-card {
  .el-card__body {
    padding: 0;
  }
}
.fc-blue-circle {
  width: 32px;
  height: 32px;
  border-radius: 100%;
  background-color: #e1ecfe;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-grey11 {
  font-size: 11px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #464646;
}
.fc-portal-blue-txt14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #483db6;
}
.fc-portal-black14 {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.29;
  letter-spacing: normal;
  color: #12324a;
}
.fc-portal-black2-14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #12324a;
}
.fc-status-dot-green {
  width: 6px;
  height: 6px;
  background-color: #16aa00;
  border-radius: 10px;
}
.fc-status-dot-red {
  width: 6px;
  height: 6px;
  background-color: #c03000;
  border-radius: 10px;
}
.fc-request-list-block {
  background-color: #fbfaff;
  padding: 20px;
}
.service-request-footer {
  padding: 30px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #0053cc;
}
</style>
