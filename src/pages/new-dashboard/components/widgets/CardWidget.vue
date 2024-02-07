<template>
  <div class="card-wrapper" :class="{ loading: loading }">
    <component
      ref="card-component"
      :key="componentKey"
      v-if="componentName"
      :is="componentName"
      :isLoading="loading"
      :widget="widget"
      :componentKey="componentKey"
      :cardData="dataObj"
      :cardState="stateObj"
      :cardParams="paramsObj"
      :dbCustomScriptFilter="dbCustomScriptFilter"
      :cardDrilldown="cardActions"
      :triggerAction="triggerAction"
      :widgetConfig="widgetConfig"
      :customScriptId="customScriptId"
      :scriptModeInt="scriptModeInt"
    ></component>
    <CardActions ref="card-actions"></CardActions>
    <card-builder
      style="z-index: 10000"
      v-if="showCardBuilder"
      :cardData="widget"
      :isDuplicate="isDuplicate"
      @close="closeCardBuilder"
      @duplicate="duplicateCard"
      @update="updateCard"
    />
  </div>
</template>
<script>
// Utils, components
import { cloneDeep } from 'lodash'
import CardBuilder from 'src/pages/new-dashboard/components/card-builder/CardBuilder.vue'
import { isEmpty } from '@facilio/utils/validation'
import { v4 as uuid } from 'uuid'
import CardActions from 'pages/card-builder/components/CardActions'
import { isEqual } from 'lodash'
// Cards
import ReadingCard1 from 'pages/card-builder/cards/readingCard/type1/Card'
import ReadingCard2 from 'pages/card-builder/cards/readingCard/type2/Card'
import ReadingCard3 from 'pages/card-builder/cards/readingCard/type3/Card'
import ReadingCard4 from 'pages/card-builder/cards/readingCard/type4/Card'
import ReadingCard5 from 'pages/card-builder/cards/readingCard/type5/Card'
import TargetMeter1 from 'pages/card-builder/cards/targetMeter/type1/Card'
import TargetMeter2 from 'pages/card-builder/cards/targetMeter/type2/Card'
import TargetMeter3 from 'pages/card-builder/cards/targetMeter/type3/Card'
import TargetMeter4 from 'pages/card-builder/cards/targetMeter/type4/Card'
import TargetMeter5 from 'pages/card-builder/cards/targetMeter/type5/Card'
import TargetMeter6 from 'pages/card-builder/cards/targetMeter/type6/Card'
import TargetMeter7 from 'pages/card-builder/cards/targetMeter/type7/Card'
import MapCard from 'pages/card-builder/cards/mapCard/base/Card'
import DataTable from 'pages/card-builder/cards/dataTable/base/Card'
import weatherCard1 from 'pages/card-builder/cards/weather/base/Card'
import carbonCard1 from 'pages/card-builder/cards/carbon/type1/Card'
import energyCard1 from 'pages/card-builder/cards/energy/type1/Card'
import energyCostCard1 from 'pages/card-builder/cards/energyCost/type1/Card'
import graphicCard1 from 'pages/card-builder/cards/graphicCard/type1/Card'
import controlCard1 from 'pages/card-builder/cards/controlCard/type1/Card'
import PmReadingcCard1 from 'pages/card-builder/cards/pmReadings/type1/Card'
import KpiCard1 from 'pages/card-builder/cards/kpi/type1/Card'
import KpiCard2 from 'pages/card-builder/cards/kpi/type2/Card'
import webCard1 from 'pages/card-builder/cards/web/base/Card'
import photosCard1 from 'pages/card-builder/cards/photos/type1/Card'
import readingTable1 from 'pages/card-builder/cards/readingTable/type1/Card'
import floorPlan1 from 'pages/card-builder/cards/floorPlan/type1/Card'
import BaseWidgetMixin from 'src/pages/new-dashboard/components/widgets/BaseWidgetMixin.js'
export default {
  props: {
    loadImmediately: {
      type: Boolean,
      default: true,
    },
    updateWidget: {
      type: Function,
    },
    dashboardId: {},
    item: {},
    showEdit: {},
    showRemove: {},
    showFilterConfig: {},
    widgetConfig: {},
    componentVisibleInViewPort: {
      type: Boolean,
      required: true,
    },
  },
  mixins: [BaseWidgetMixin],
  components: {
    CardBuilder,
    CardActions,
    readingcard_layout_1: ReadingCard1,
    readingcard_layout_2: ReadingCard2,
    readingcard_layout_3: ReadingCard3,
    readingcard_layout_4: ReadingCard4,
    readingcard_layout_5: ReadingCard5,
    gauge_layout_1: TargetMeter1,
    gauge_layout_2: TargetMeter2,
    gauge_layout_3: TargetMeter3,
    gauge_layout_4: TargetMeter4,
    gauge_layout_5: TargetMeter5,
    gauge_layout_6: TargetMeter6,
    gauge_layout_7: TargetMeter7,
    mapcard_layout_1: MapCard,
    datatable: DataTable,
    weathercard_layout_1: weatherCard1,
    carboncard_layout_1: carbonCard1,
    energycost_layout_1: energyCostCard1,
    graphicalcard_layout_1: graphicCard1,
    energycard_layout_1: energyCard1,
    controlcard_layout_1: controlCard1,
    pmreadings_layout_1: PmReadingcCard1,
    kpicard_layout_1: KpiCard1,
    kpicard_layout_2: KpiCard2,
    web_layout_1: webCard1,
    photos_layout_1: photosCard1,
    table_layout_1: readingTable1,
    floorplan_layout_1: floorPlan1,
  },
  data() {
    return {
      cardParentId: '',
      isReportLoadedOnes: false,
      isDuplicate: false,
      showCardBuilder: false,
      dbFilterJson: {},
      dbTimelineFilter: {},
      loading: true,
      result: null,
      showOptionsPopover: false,
      pubSubWatcherKey: null,
      isInfoIconPopOverOpen: false,
    }
  },

  created() {
    this.initWidget(this.config)
    //when dashboard in lazy mode and widget ain't visible dont load
    if (!this.componentVisibleInViewPort) {
      return
    }
    this.init()
  },
  beforeDestroy() {
    this.unSubscribeToLiveData()
  },
  watch: {
    'widget.dataOptions': function() {
      this.getData()
    },
    hasPropsChanged() {
      this.getData()
    },
    //when widget at bottom of dashboard is scrolled up , this prop changes from false to true,
    componentVisibleInViewPort(componentVisibleInViewPort) {
      if (
        componentVisibleInViewPort &&
        (this.isReportLoadedOnes || this.loadImmediately)
      ) {
        this.init()
      }
    },
    allFilters(newVal, oldVal) {
      if (this.isReportLoadedOnes == false) {
        this.isReportLoadedOnes = true
      }
      if (!isEqual(newVal, oldVal) || this.loading) {
        this.getData()
      }
    },
  },
  computed: {
    allFilters() {
      const {
        dbTimelineFilter,
        dbFilterJson,
        cardParentId,
        dbCustomScriptFilter,
      } = this ?? {}
      return {
        dbFilterJson: dbFilterJson,
        dbTimelineFilter: dbTimelineFilter,
        cardParentId: cardParentId,
        dbCustomScriptFilter: dbCustomScriptFilter,
      }
    },
    config() {
      const { id } = this ?? {}
      return {
        id: id,
        minW: 25,
        maxW: 90,
        minH: 10,
        maxH: 50,
        borderAroundWidget: false, // Gives a pale black border around the widgetWrapper...
        showHeader: false,
        showExpand: false,
        noResize: false,
        showDropDown: true,
        editMenu: [
          {
            label: 'Edit card',
            action: () => {
              this.showCardBuilder = true
            },
            icon: 'el-icon-edit-outline',
          },
          {
            label: 'Duplicate',
            action: () => {
              ;(this.isDuplicate = true), (this.showCardBuilder = true)
            },
            icon: 'el-icon-document-copy',
          },
        ],
        viewMenu: [],
      }
    },
    id() {
      return this.item.id ?? null
    },
    customScriptId() {
      let { widget } = this
      return widget?.dataOptions?.customScriptId
        ? widget.dataOptions.customScriptId
        : null
    },
    widget() {
      return this.item.widget
    },
    scriptModeInt() {
      let { widget } = this
      return widget?.dataOptions?.scriptModeInt
        ? widget.dataOptions.scriptModeInt
        : null
    },
    showWidgetInfoIcon() {
      return this.widget?.widgetSettings?.showHelpText && this.widget?.helpText
    },
    canShowOptions() {
      return this.showEdit || this.showRemove || this.showFilterConfig
    },
    componentKey() {
      let { result, $getProperty } = this
      if (!isEmpty(result)) {
        let id = $getProperty(result, 'cardContext.id', null)
        if (!isEmpty(id)) return id
      }
      return uuid()
    },
    componentName() {
      let { dataOptions = {} } = this.widget
      return dataOptions.cardLayout || null
    },
    dataObj() {
      const { result } = this
      const { data } = result ?? {}
      if (!isEmpty(result)) {
        return data
      } else {
        return {}
      }
    },
    stateObj() {
      let { result, $getProperty } = this

      if (!isEmpty(result)) {
        let cardState = $getProperty(result, 'state', null)
        return !isEmpty(cardState) ? cardState : {}
      }
      return {}
    },
    paramsObj() {
      let { result, $getProperty } = this

      if (!isEmpty(result)) {
        let cardParams = $getProperty(result, 'cardContext.cardParams', null)
        return !isEmpty(cardParams) ? cardParams : {}
      }
      return {}
    },
    cardActions() {
      let cardDrilldown = this.$getProperty(
        this.widget,
        'dataOptions.cardDrilldown',
        null
      )
      return !isEmpty(cardDrilldown) ? cardDrilldown : {}
    },
    hasPropsChanged() {
      let { widget } = this
      return this.$getProperty(widget, 'dataOptions.hasEdited', false) || false
    },
  },
  methods: {
    duplicateCard(duplicateCardContext) {
      // Don't remove the `id` of the widget, because we want to find out
      // which grid it belongs to, section or master grid using the id only.
      const duplicateCard = this.getUpdatedCard(duplicateCardContext)
      this.closeCardBuilder()
      this.$parent.$emit('duplicateWidget', duplicateCard)
    },
    getUpdatedCard(updatedCardContext) {
      const { item } = this
      const clonedCard = cloneDeep(item)
      let {
        widget: { dataOptions },
      } = clonedCard

      this.$set(clonedCard.widget, 'dataOptions', {
        ...dataOptions,
        ...updatedCardContext,
        hasEdited: true,
      })
      return clonedCard
    },
    updateCard(updatedCardContext) {
      const updatedCard = this.getUpdatedCard(updatedCardContext)
      this.closeCardBuilder()
      const { updateWidget } = this
      updateWidget(updatedCard)
    },
    closeCardBuilder() {
      this.showCardBuilder = false
      this.isDuplicate = false
    },
    init() {
      //first time get data call alone, set up sockets
      this.getData().then(() => {
        this.$nextTick(() => this.subscribeToLiveData())
      })
    },
    helpTextConfig() {
      this.showOptionsPopover = false

      this.$emit('helpTextConfig')
    },
    editFilterConfig() {
      this.showOptionsPopover = false

      let { widget } = this
      this.$emit('editWidgetFilterConfig', widget)
    },
    remove() {
      this.showOptionsPopover = false
      this.$emit('removeCard')
    },
    duplicate() {
      this.showOptionsPopover = false
      this.$emit('duplicate')
    },
    subscribeToLiveData() {
      if (!isEmpty(this.pubSubWatcherKey)) {
        this.unSubscribeToLiveData()
      }
      let params = this.getSubscriptionParams()
      if (!isEmpty(params)) {
        if (this.$wms) {
          this.$http
            .post('/v2/fetchLiveUpdateFields', {
              liveUpdateFields: params.readings,
            })
            .then(response => {
              let readingFields = response.data.result.liveUpdateFields
              if (readingFields && readingFields.length) {
                for (let reading of readingFields) {
                  let topic =
                    '__livereading__/' +
                    reading.parentId +
                    '/' +
                    reading.fieldId
                  this.$wms.subscribe(topic, this.getData)
                }
              }
            })
        } else {
          this.pubSubWatcherKey = this.subscribe(
            this.getSubscriptionEvent(),
            params,
            this.getData
          )
        }
      }
    },

    unSubscribeToLiveData() {
      let params = this.getSubscriptionParams()
      if (this.$wms) {
        this.$http
          .post('/v2/fetchLiveUpdateFields', {
            liveUpdateFields: params.readings,
          })
          .then(response => {
            let readingFields = response.data.result.liveUpdateFields
            if (readingFields && readingFields.length) {
              for (let reading of readingFields) {
                let topic =
                  '__livereading__/' + reading.parentId + '/' + reading.fieldId
                this.$wms.unsubscribe(topic, this.getData)
              }
            }
          })
      } else if (this.pubSubWatcherKey) {
        this.unsubscribe(
          this.pubSubWatcherKey,
          this.getSubscriptionEvent(),
          params
        )
        this.pubSubWatcherKey = null
      }
    },

    getSubscriptionEvent() {
      let cardComponent = this.$refs['card-component']
      let eventName = cardComponent && cardComponent.subscriptionEvent

      return eventName ? eventName : 'readingChange'
    },

    getSubscriptionParams() {
      let cardComponent = this.$refs['card-component']
      let params = cardComponent ? cardComponent.subscriptionParams : []

      return params || {}
    },

    triggerAction(element) {
      let cardComponent = this.$refs['card-component']
      let cardDrilldown = cardComponent.getCardDrillDown()

      if (isEmpty(cardComponent)) return
      element = isEmpty(element) ? 'default' : element
      //cardUserFilters and cardFilters are passed through . CardActioComp->executeAction to CardListView->executeAction
      if (!isEmpty(this.dbFilterJson)) {
        cardComponent.cardUserFilters = cloneDeep(this.dbFilterJson)
      } else {
        cardComponent.cardUserFilters = {}
      }
      if (!isEmpty(this.dbTimelineFilter)) {
        const timelineFilter = cloneDeep(this.dbTimelineFilter)
        timelineFilter.operatorId = cloneDeep(timelineFilter.dateOperator)
        delete timelineFilter.dateOperator
        cardComponent.cardFilters = timelineFilter
      } else {
        cardComponent.cardFilters = {}
      }

      let action = (cardDrilldown || {})[element]
      if (!isEmpty(action)) {
        const useDashboardFilters =
          this?.widget?.dataOptions?.cardDrilldown?.default?.data
            ?.dashboardFilters ?? false
        this.$refs['card-actions'].executeAction(
          action,
          cardComponent,
          this.allFilters,
          useDashboardFilters
        )
      }
    },

    getData() {
      if (!this.componentVisibleInViewPort) {
        return
      }
      let {
        widget: { id, dataOptions },
      } = this
      let {
        cardLayout,
        cardParams,
        cardState,
        cardDrilldown,
        conditionalFormatting,
      } = dataOptions
      const data = {}
      data.cardId = id
      const {
        dbTimelineFilter,
        dbFilterJson,
        dbCustomScriptFilter,
        cardParentId,
      } = this
      if (!isEmpty(dbTimelineFilter)) {
        data.cardFilters = JSON.stringify(dbTimelineFilter)
      }
      if (!isEmpty(dbFilterJson)) {
        data.cardUserFilters = JSON.stringify(dbFilterJson)
      }
      if (!isEmpty(dbCustomScriptFilter)) {
        data.customScriptFilter = dbCustomScriptFilter
      }
      if (data.cardId == -1 || cardParentId != '') {
        data.cardContext = cloneDeep({
          cardLayout,
          cardParams,
          cardState,
          cardDrilldown,
          conditionalFormatting,
        })
        if (cardParentId != '') {
          data.cardContext.cardParams.reading.parentId = Number(cardParentId)
        }
      }
      this.loading = true
      return this.$http
        .post(`/v2/dashboard/cards/getCardData`, data)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$set(this, 'result', data.result)
          } else {
            // TODO
          }
          this.$set(this, 'loading', false)
        })
        .catch(() => {
          this.$set(this, 'loading', false)
          // TODO
        })
    },
  },
}
</script>
<style scoped lang="scss">
.card-wrapper {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 6px 23px 0 rgba(22, 18, 58, 0.03);
  border: solid 1px #edf3f3;
  height: 100%;
  overflow: hidden;
}
.widget-info-container {
  .el-icon-info {
    position: absolute;
    right: 15px;
    z-index: 2;
    top: 15px;

    &.move-left {
      right: 35px;
    }
  }
}
</style>
