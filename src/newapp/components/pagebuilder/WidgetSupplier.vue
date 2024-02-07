<template lang="">
  <div class="widget-container">
    <component
      v-bind="$attrs"
      :is="widgetName"
      :widget="widget"
      :resizeWidgetOnPage="resizeWidgetOnPage"
      :interWidgetCom="interWidgetCom"
      :widgetId="widget.id"
      :payloadDrop="payloadDrop"
      :layoutParams="layoutParams"
      :resizeWidget="resizeWidget"
      :calculateDimensions="calculateDimensions"
      style="height:100%"
      :cellHeight="cellHeight"
      :cellWidth="cellWidth"
    ></component>
    <div v-show="!widgetName" class="fc-align-center-column empty-widget-text">
      <span>{{ $t('common.products.widget_not_available') }}</span>
    </div>
  </div>
</template>
<script>
import { eventBus } from 'src/components/page/widget/utils/eventBus'
import { isNullOrUndefined } from '@facilio/utils/validation'

export default {
  props: [
    'resizeWidgetOnPage',
    'cellHeight',
    'resizeWidgetId',
    'widget',
    'cellWidth',
  ],
  data() {
    return {
      payloadDrop: null,
      resizeId: null,
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
    summaryFieldsWidget: () =>
      import('src/beta/summary/widget/SummaryFieldsWidget.vue'),

    bulkRelatedList: () =>
      import('src/components/page/widget/common/line-items/RelatedList.vue'),
    classification: () =>
      import('pageWidgets/specification/ClassificationSpecificationWidget.vue'),
    activity: () => import('components/page/widget/common/ActivityWidget'),
    widgetGroup: () => import('./widgets/widgetGroup.vue'),
    comment: () => import('src/components/page/widget/common/NotesWidget.vue'),
    attachment: () =>
      import('src/components/page/widget/common/DocumentsWidget'),
    bulkRelationShipWidget: () =>
      import('src/components/page/widget/relationship/RelationWidget.vue'),
    statetransitiontimelog: () =>
      import('components/page/widget/common/MetricsWorkLog'),
    failurereports: () =>
      import(
        'src/pages/workorder/workorders/v3/widgets/failureClass/FailureReport.vue'
      ),
    history: () => import('src/components/page/widget/common/History.vue'),
    workorderComments: () =>
      import('src/beta/summary/widget/notes/NotesWidget.vue'),
    workorderAttachments: () =>
      import('src/beta/summary/widget/attachments/AttachmentWidget.vue'),
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
    resizeWidget(params) {
      let { resizeId, cellHeight, cellWidth } = this
      let { h, w, height, width } = params || {}
      let dimensions = {}
      if (isNullOrUndefined(h) && isNullOrUndefined(w)) {
        let dim = this.calculateDimensions({ height, width })
        h = dim.h
        w = dim.w
      }
      Object.entries({ w, h }).forEach(([key, value]) => {
        let unitArr = {
          w: cellWidth,
          h: cellHeight,
        }
        if (Math.sign(value) !== 1) return
        dimensions[key] = value * unitArr[key]
      })
      this.resizeWidgetOnPage(resizeId, dimensions)
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
.empty-widget-text {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-weight: 500;
  text-transform: uppercase;
}
.widget-container {
  height: 100%;
  width: 100%;
  position: relative;
  background-color: #fff;
}
</style>
