<template lang="">
  <FCard :hideBorder="hideTitleSection">
    <template #header>
      <FContainer v-if="!hideHeader">
        <portal-target
          ref="header-portal"
          class="portal-alignment-ws"
          :name="`header-${widget.id}-${widget.name}`"
        >
          <FContainer class="title-section-ws">
            <portal-target
              :name="`title-${widget.id}-${widget.name}`"
              class="portal-alignment-ws"
            >
              <FContainer padding="containerXLarge">
                <FText appearance="headingMed14">{{
                  widget.displayName || widget.name
                }}</FText>
              </FContainer></portal-target
            >
            <portal-target
              :name="`action-${widget.id}-${widget.name}`"
              class="portal-alignment-ws action-ws"
            >
            </portal-target>
          </FContainer>
        </portal-target>
      </FContainer>
    </template>
    <FDivider width="100%" v-if="!hideHeader"></FDivider>
    <component
      v-bind="$attrs"
      :is="widgetName"
      :hideTitleSection="hideTitleSection"
      :widget="widget"
      :resizeWidgetOnPage="resizeWidgetOnPage"
      :interWidgetCom="interWidgetCom"
      :widgetId="widget.id"
      :payloadDrop="payloadDrop"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :calculateDimensions="calculateDimensions"
      style="height:100%;overflow: scroll"
      :cellHeight="cellHeight"
      :cellWidth="cellWidth"
      :fitToViewArea="fitToViewArea"
    ></component>
    <FContainer v-show="!widgetName" padding="containerXLarge">
      <FText>{{ $t('common.products.widget_not_available') }}</FText>
    </FContainer>
    <FDivider v-if="showFooterDivider" width="100%" class="mB0"></FDivider>
    <template #footer>
      <portal-target
        class="portal-alignment-ws"
        ref="footer-portal"
        :class="{ height2px: !showFooterDivider }"
        :name="`footer-${widget.id}-${widget.name}`"
        @change="footerChange"
        :style="{ 'min-height': showFooterDivider ? '48px' : '2px' }"
      ></portal-target>
    </template>
  </FCard>
</template>
<script>
import { FCard, FText, FContainer, FDivider } from '@facilio/design-system'
import { eventBus } from 'src/components/page/widget/utils/eventBus'
import {
  isNullOrUndefined,
  isEmpty,
  isFunction,
} from '@facilio/utils/validation'

const NO_HEADER_HASH = ['widgetGroup']

export default {
  props: [
    'resizeWidgetOnPage',
    'cellHeight',
    'resizeWidgetId',
    'widget',
    'cellWidth',
    'hideBorder',
    'hideTitleSection',
  ],
  data() {
    return {
      payloadDrop: null,
      resizeId: null,
      showFooterDivider: false,
      titleFilled: false,
      widgetCpy: {},
    }
  },
  created() {
    let { resizeWidgetId, widget } = this
    let { id, originalId } = widget //revert change when resize is fixed
    let actualId = id || originalId
    this.widgetCpy = { ...widget, id: actualId }
    this.resizeId = resizeWidgetId || actualId
  },

  components: {
    FCard,
    FText,
    FContainer,
    FDivider,
    summaryFieldsWidget: () => import('./SummaryFieldsWidget.vue'),
    bulkRelatedList: () => import('./RelatedList.vue'),
    relatedList: () => import('./RelatedList.vue'),
    // bulkRelatedList: () =>
    //   import('src/components/page/widget/common/line-items/RelatedList.vue'),
    classification: () =>
      import('./classification/ClassificationSpecificationWidget.vue'),
    widgetGroup: () => import('./WidgetGroup.vue'),
    activity: () => import('./history/ActivityWidget'),
    comment: () => import('src/beta/summary/widget/notes/NotesWidget.vue'),
    attachment: () =>
      import('src/beta/summary/widget/attachments/AttachmentWidget.vue'),

    responsibility: () =>
      import(
        'src/beta/summary/widgets/WorkorderWidget/WorkorderResponsibility.vue'
      ),
    timedetails: () =>
      import('src/beta/summary/widgets/WorkorderWidget/WorkordeTimeDetail.vue'),
    resource: () =>
      import('src/beta/summary/widgets/WorkorderWidget/WorkorderResorce.vue'),
    quotation: () =>
      import('src/beta/summary/widgets/WorkorderWidget/WorkorderCost.vue'),
    multiResource: () =>
      import(
        'src/beta/summary/widgets/WorkorderWidget/WorkorderMultiresource.vue'
      ),
    bulkRelationShipWidget: () =>
      import('src/components/page/widget/relationship/RelationWidget.vue'),
    statetransitiontimelog: () => import('./timeLog/MetricsWorkLog'),
    // failurereports: () =>
    //   import(
    //     'src/pages/workorder/workorders/v3/widgets/failureClass/FailureReport.vue'
    //   ),
    // history: () => import('src/components/page/widget/common/History.vue'),
    meterReadings: () =>
      import('src/beta/summary/widget/meter/MeterReadings.vue'),
    monthlyConsumption: () =>
      import('src/beta/summary/widget/meter/MonthlyConsumption.vue'),
    totalConsumption: () =>
      import('src/beta/summary/widget/meter/TotalConsumption.vue'),
    peakDemand: () => import('src/beta/summary/widget/meter/PeakDemand.vue'),
    virtualMeterTemplateReadings: () =>
      import('src/beta/summary/widget/meter/vmtemplate/VMTemplateReadings.vue'),
    relatedVirtualMetersList: () =>
      import('src/beta/summary/widget/meter/vmtemplate/RelatedVMList.vue'),
    history: () => import('src/components/page/widget/common/History.vue'),
    // activity: () =>
    //   import('src/components/page/widget/common/ActivityWidget.vue'),
    meterWidget: () =>
      import(
        'src/beta/pages/UtilityApiIntegration/UtilityIntegrationMeterDetails.vue'
      ),
    billSummaryWidget: () =>
      import('src/beta/summary/widget/utilityBills/UtilityBillSummary.vue'),
    calendarEventView: () =>
      import('src/beta/summary/widget/calendar/CalendarView.vue'),
    controlActionCriteria: () =>
      import('src/beta/summary/widget/CriteriaFieldsWidget.vue'),
    calendarEventList: () => import('../../components/Calendar/EventsList.vue'),
  },
  mounted() {
    let { widgetType } = this.widget
    eventBus.$on(widgetType, payload => {
      this.payloadDrop = payload
    })
  },
  beforeDestroy() {
    let { widgetType } = this.widget
    eventBus.$off(widgetType, payload => {
      this.payloadDrop = payload
    })
  },
  computed: {
    widgetName() {
      let { widgetTypeObj } = this.widget
      let { name } = widgetTypeObj || {}
      let compAvail = Object.keys(this.$options.components).includes(name)

      return compAvail ? name : null
    },
    hideHeader() {
      let { hideTitleSection, widgetName } = this
      return NO_HEADER_HASH.includes(widgetName) || hideTitleSection
    },
    layoutParams() {
      let { height, width, positionY, positionX } = this.widget

      return {
        h: height,
        w: width,
        x: positionX,
        y: positionY,
      }
    },
  },
  methods: {
    interWidgetCom(widgetType, payload) {
      let { widgetType: fromWidgetsType, id } = this.widget
      let payloadObj = {
        from: {
          widgetType: fromWidgetsType,
          id,
        },
        payload,
      }
      eventBus.$emit(widgetType, payloadObj)
    },
    fitToViewArea() {
      this.$emit('fitToViewArea', this.widget?.id)
    },
    footerChange(param) {
      let { passengers } = param
      if (!isFunction((passengers || [])[0])) {
        return
      }
      let vNode = (passengers || [])[0]()
      let scrollHeight = this.$getProperty(vNode, '0.elm.scrollHeight') || 0
      if (!isEmpty(scrollHeight) && scrollHeight !== 0) {
        this.showFooterDivider = true
      }
    },
    resizeWidget(params) {
      let { configType } = this.widget || {}
      if (configType !== ' FLEXIBLE') return
      let { resizeId, cellHeight, cellWidth } = this
      let { h, w, height, width } = params || {}
      let headerHeight = 0,
        footerHeight = 0
      this.$nextTick().then(
        this.$nextTick(() => {
          headerHeight = this.$refs['header-portal']?.$el?.scrollHeight || 0
          footerHeight = this.$refs['footer-portal']?.$el?.scrollHeight || 0
          let dimensions = {}
          let { h: extraHeight } = this.calculateDimensions({
            height: headerHeight + footerHeight,
          })

          if (isNullOrUndefined(h) && isNullOrUndefined(w)) {
            let dim = this.calculateDimensions({ height, width })
            h = dim.h
            w = dim.w
          }
          h = h + extraHeight

          Object.entries({ w, h }).forEach(([key, value]) => {
            let unitArr = {
              w: cellWidth,
              h: cellHeight,
            }
            if (Math.sign(value) !== 1) return
            dimensions[key] = value * unitArr[key]
          })
          this.resizeWidgetOnPage(resizeId, dimensions)
        })
      )
    },
    calculateDimensions(params) {
      let { height, width } = params
      let { cellHeight, cellWidth } = this
      let reHeight = Math.ceil(height / cellHeight)
      let reWidth = Math.ceil(width / cellWidth)
      return { w: reWidth, h: reHeight }
    },
  },
}
</script>
<style lang="scss" scoped>
.title-section-ws {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}
.portal-alignment-ws {
  display: flex;
  align-items: center;
  min-height: 48px;
  &.action-ws {
    margin-right: 12px;
  }
}
.empty-widget-text {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-weight: 500;
  text-transform: uppercase;
}
.header-part {
  .header-title {
    display: flex;
    align-items: center;
    font-size: 14px;
    font-weight: 500;
    padding: 10px;
  }
}
.widget-container {
  height: 100%;
  width: 100%;
  position: relative;
  background-color: #fff;
}
</style>
