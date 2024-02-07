<template>
  <div
    class="card-wrapper"
    :class="{ loading: loading }"
    v-observe-visibility="
      isLazyDashboard ? handleViewportVisibilityChange : false
    "
  >
    <el-popover
      v-if="showWidgetInfoIcon"
      :popper-class="'widget-info-popper'"
      placement="bottom"
      trigger="click"
      class="widget-info-container"
      v-model="isInfoIconPopOverOpen"
      :class="{ 'show-info-icon': isInfoIconPopOverOpen }"
    >
      <div
        class="widget-help-text space-preline break-normal pT5 pB5 pL10 pR10 "
      >
        {{ this.widget.helpText }}
      </div>
      <i
        slot="reference"
        class="pointer el-icon-info "
        :class="{ 'move-left': showEdit }"
      ></i>
    </el-popover>
    <el-popover
      v-if="canShowOptions"
      :popper-class="'card-options'"
      placement="bottom"
      width="200"
      trigger="click"
      v-model="showOptionsPopover"
      class="card-options-container"
    >
      <div class="card-options-list">
        <div v-if="showEdit" @click="edit" class="pointer card-options-label">
          {{ $t('common._common.edit') }}
        </div>
        <div
          v-if="showRemove"
          class="pointer card-options-label"
          @click="remove"
        >
          {{ $t('common._common.remove') }}
        </div>
        <div
          v-if="showEdit"
          class="pointer card-options-label"
          @click="duplicate"
        >
          {{ $t('common._common.duplicate') }}
        </div>
        <div
          v-if="showFilterConfig"
          class="pointer card-options-label"
          @click="editFilterConfig"
        >
          {{ $t('dashboardfilters.configure') }}
        </div>
        <div
          v-if="showEdit"
          class="pointer card-options-label"
          @click="helpTextConfig"
        >
          {{ $t('common._common.help_text') }}
        </div>
      </div>
      <i
        slot="reference"
        class="pointer pull-right el-icon-more fc-widget-moreicon-vertical"
      ></i>
    </el-popover>
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
      :dbTimelineFilter="dbTimelineFilter"
      :dbCustomScriptFilter="dbCustomScriptFilter"
      :cardDrilldown="cardActions"
      :triggerAction="triggerAction"
      :widgetConfig="config"
      :customScriptId="customScriptId"
      :scriptModeInt="scriptModeInt"
    ></component>
    <CardActions ref="card-actions"></CardActions>
  </div>
</template>
<script>
// Utils, components
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

export default {
  props: [
    'dashboardId',
    'widget',
    'showEdit',
    'showRemove',
    'showFilterConfig',
    'widgetConfig',
    'dbFilterJson', //all three params related to db filters, ignored if not present
    'dbTimelineFilter',
    'dbCustomScriptFilter',
    'isLazyDashboard',
  ],
  components: {
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
      loading: true,
      result: null,
      isVisibleInViewport: false,
      showOptionsPopover: false,
      pubSubWatcherKey: null,
      isInfoIconPopOverOpen: false,
    }
  },
  created() {
    //when dashboard in lazy mode and widget ain't visible dont load
    if (this.isLazyDashboard && !this.isVisibleInViewport) {
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
    isVisibleInViewport(newValue) {
      if (newValue) {
        this.init()
      }
    },
    dbTimelineFilter(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.getData()
      }
    },
    dbFilterJson(newValue, oldValue) {
      if (!isEqual(newValue, oldValue)) {
        this.getData()
      }
    },
    dbCustomScriptFilter(newValue, oldValue) {
      if (!isEqual(newValue.filterValues, oldValue.filterValues)) {
        this.getData()
      }
    },
  },
  computed: {
    allFilters() {
      return {
        dbFilterJson: this.dbFilterJson,
        dbTimelineFilter: this.dbTimelineFilter,
      }
    },
    customScriptId() {
      let { widget } = this
      return widget?.dataOptions?.customScriptId
        ? widget.dataOptions.customScriptId
        : null
    },
    scriptModeInt() {
      let { widget } = this
      return widget?.dataOptions?.scriptModeInt
        ? widget.dataOptions.scriptModeInt
        : null
    },
    showWidgetInfoIcon() {
      return this.widget?.widgetSettings?.showHelpText && this.widget.helpText
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
      return !isEmpty(this.result) ? this.result.data : {}
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
    config() {
      // need to remove after event bust
      return this.widgetConfig
    },
  },
  methods: {
    init() {
      //first time get data call alone, set up sockets
      this.getData().then(() => {
        this.$nextTick(() => this.subscribeToLiveData())
      })
    },
    handleViewportVisibilityChange(isVisible) {
      //one set state from false to true, DONT reset it to false when widget goes out of view again , no need to trigger refresh again. see watcher for isVisibleInViewport inside report comp
      if (isVisible) {
        this.isVisibleInViewport = isVisible
      }
    },
    edit() {
      this.showOptionsPopover = false

      let { widget } = this
      let { widgetConfig } = this
      this.$emit('editCard', widget, widgetConfig)
    },
    helpTextConfig() {
      this.showOptionsPopover = false
      let { widget } = this
      this.$emit('helpTextConfig', widget)
    },
    editFilterConfig() {
      this.showOptionsPopover = false

      let { widget } = this
      this.$emit('editWidgetFilterConfig', widget)
    },
    remove() {
      this.showOptionsPopover = false

      let { widget, dashboardId } = this
      let data = { widgetId: widget.id, dashboardId, widget }
      this.$emit('removeCard', data)
    },
    duplicate() {
      this.showOptionsPopover = false

      let { widget } = this
      let { widgetConfig } = this
      this.$emit('duplicate', widget, widgetConfig)
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

      cardComponent.cardUserFilters = this.dbFilterJson
      cardComponent.cardFilters = this.dbTimelineFilter

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
      if (this.isLazyDashboard && !this.isVisibleInViewport) {
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

      let data = {}

      if (!isEmpty(id) && !this.hasPropsChanged) {
        data.cardId = id
        //for live card , support dashboard filter.
        if (this.dbTimelineFilter) {
          data.cardFilters = JSON.stringify(this.dbTimelineFilter)
        }
        if (this.dbFilterJson) {
          data.cardUserFilters = JSON.stringify(this.dbFilterJson)
        }
        if (this.dbCustomScriptFilter) {
          data.customScriptFilter = this.dbCustomScriptFilter
        }
      } else {
        data.cardContext = {
          cardLayout,
          cardParams,
          cardState,
          cardDrilldown,
          conditionalFormatting,
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
.card-options-container {
  position: absolute;
  right: 5px;
  top: 10px;
  z-index: 300;
}
.card-options-label:not(:last-of-type) {
  margin-bottom: 10px;
}
.fc-widget-moreicon-vertical {
  padding: 5px;
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
