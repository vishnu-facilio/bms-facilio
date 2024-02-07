<template>
  <div class="height100 relative">
    <div
      class="floor-plan-builder-container p10 mobilefloorplan"
      ref="floorPlanContainer"
    >
      <canvas id="floorplan-canvas" ref="floorPlan"></canvas>
    </div>
    <floorplansettings
      :refresh="refresh"
      class="fp-mobile-viwer-settings"
      @action="handleAction"
    >
    </floorplansettings>
  </div>
</template>

<script>
import VueNativeSock from 'vue-native-websocket'
import Vue from 'vue'
import { fabric } from 'fabric'
import { isEmpty } from '@facilio/utils/validation'
import OutApp from 'src/OuterAppUtil/OuterAppHelper'
import floorplansettings from 'pages/floorPlan/components/FloorPlanViewerSettings'

import canvasSetup from 'pages/floorPlan/mixins/CanvasSetupMixin'
import floorAction from 'pages/floorPlan/mixins/FloorPlanActionMixin'
import filterMixin from 'pages/floorPlan/mixins/FloorPlanFiltersMixin'
import floorplanvalue from 'pages/floorPlan/mixins/FloorPlanValueMixin'
export default {
  mixins: [OutApp, canvasSetup, floorAction, filterMixin, floorplanvalue],
  data() {
    return {
      floorPlan: null,
      floor: null,
      refresh: false,
      leagend: {},
      controlCategoryList: [],
      spaceControllableCategoriesMap: [],
      floorControlCategory: [],
      spaces: [],
      flooplans: [],
      spaceList: [],
      pubSubWatcherKey: null,
      wsPingPongInterval: null,
    }
  },
  components: {
    floorplansettings,
  },
  computed: {
    floorPlanId() {
      let { floor } = this
      if (!isEmpty(floor)) {
        return floor.defaultFloorPlanId
      }
      return null
    },
    floorId() {
      let { params } = this.$route
      if (!isEmpty(params)) {
        return Number(params.floorId)
      }
      return null
    },
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  mounted() {
    this.loadQData()
    this.$store.dispatch('getCurrentAccount').then(() => {
      this.setSocketDetails()
    })
    this.$store.dispatch('getFeatureLicenses')
    Promise.all([
      this.getAllCategoryEnum(),
      this.fetchFloorDetails(),
      this.fetchFloorSpaces(),
      this.getFloorControlCategory(),
      this.loadFloorSpaces(),
    ]).then(() => {
      this.fetchFloorPlan().then(() => {
        this.bindFloorplanData()
      })
    })
  },
  destroyed() {
    this.unSubscribeToLiveData()

    this.destroyFloorPlan()
  },
  methods: {
    setSocketDetails() {
      if (this.$account.config && this.$account.config.ws_endpoint) {
        Vue.use(VueNativeSock, this.$account.config.ws_endpoint, {
          store: this.$store,
          format: 'json',
          reconnection: true, // (Boolean) whether to reconnect automatically (false)
          reconnectionDelay: 10000, // (Number) how long to initially wait before attempting a new (1000)
        })

        if (this.wsPingPongInterval === null) {
          let self = this
          this.wsPingPongInterval = setInterval(function() {
            if (self.$socket) {
              let obj = { from: 0, to: 0, content: { ping: 'check' } }
              self.$socket.sendObj(obj)
            }
          }, 60000)
        }
      }
    },
    subscribeToLiveData() {
      if (!isEmpty(this.pubSubWatcherKey)) {
        this.unSubscribeToLiveData()
      }
      let params = this.getSubscriptionParams()
      if (!isEmpty(params)) {
        this.pubSubWatcherKey = this.subscribe(
          'readingChange',
          params,
          this.loadfloorPlanData()
        )
      }
    },

    unSubscribeToLiveData() {
      this.pubSubWatcherKey &&
        this.unsubscribe(
          this.pubSubWatcherKey,
          'readingChange',
          this.getSubscriptionParams()
        )
      this.pubSubWatcherKey = null
    },
    getSubscriptionParams() {
      return { readings: this.subcriptiondata || [] }
    },
    loadfloorPlanData() {
      Promise.all([
        this.fetchFloorDetails(),
        this.fetchFloorSpaces(),
        this.getFloorControlCategory(),
        this.loadFloorSpaces(),
      ]).then(() => {
        this.fetchFloorPlan().then(() => {
          this.setMarkerValue()
        })
      })
    },
    sendToMobile(floorplan) {
      if (window.JSReceiver) {
        window.JSReceiver.sendData(JSON.stringify(floorplan))
        console.log(
          'WEBAPP[FLOOR_PLAN_VIEW]:',
          '[',
          new Date(),
          ']',
          '[' + JSON.stringify(floorplan),
          ']'
        )
      } else if (
        this.$route.query &&
        this.$route.query.media &&
        this.$route.query.media === 'ios'
      ) {
        console.log(
          'WEBAPP[IOS_FLOOR_PLAN_VIEW]:',
          '[',
          new Date(),
          ']',
          '[' + JSON.stringify(floorplan),
          ']'
        )
        document.location.href = 'floorplan://' + JSON.stringify(floorplan)
      }
    },
    setLeagend() {
      let { floorPlan } = this
      if (!isEmpty(floorPlan) && floorPlan.leagend) {
        let leagend = JSON.parse(floorPlan.leagend)
        this.$set(this, 'leagend', leagend)
      }
    },
    getAllCategoryEnum() {
      return this.$http
        .get('/v2/controlAction/getControllableCategoryList')
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.controlCategoryList = data.result.controllableCategories
          }
        })
    },
    getFloorControlCategory() {
      return this.$http
        .get(
          `/v2/controlAction/getControllableCategories?floorId=${this.floorId}`
        )
        .then(({ data }) => {
          if (data.responseCode === 0 && data.result) {
            this.spaceControllableCategoriesMap =
              data.result.spaceControllableCategoriesMap
          }
        })
    },
    fetchFloorDetails() {
      return this.$http
        .get(`/v2/pages/floor?id=${this.floorId}`)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$set(this, 'floor', data.result.record)
          }
        })
    },
    fetchFloorPlan() {
      return this.$http
        .post('/v2/floorPlan/get', {
          floorPlan: {
            id: this.floorPlanId,
          },
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$set(this, 'floorPlan', data.result.floorPlan)
            this.setLeagend(data.result.floorPlan)
          }
        })
    },
    fetchFloorSpaces() {
      return this.$http
        .get(`/v2/floorPlan/spaces?floorId=${this.floorId}`)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.$set(this, 'spaces', data.result.spaces)
          }
        })
    },
    bindFloorplanData() {
      this.canvas = new fabric.Canvas(this.$refs['floorPlan'], {
        selection: false,
        preserveObjectStacking: true,
      })

      if (this.floorPlanId) {
        let that = this
        this.canvas.loadFromJSON(
          this.floorPlan.canvas,
          () => {
            that.canvas.backgroundColor = null
            // that.canvasFitToScreen()
            that.canvas.renderAll()
          },
          (el, element) => {
            this.applyValue(element)
            this.applyFilters(element)
            this.setUpMobileObjectEvents(element)
            this.setUpViewerProps(element)
          }
        )
      }
      this.canvas.renderAll()
      this.$nextTick(() => {
        this.setFitDimensions()
        this.$nextTick(() => {
          this.registerNativeEvents()
          this.setupMobileEvents()
          try {
            setTimeout(() => {
              this.subscribeToLiveData()
            }, 2000)
          } catch (error) {
            console.log(error)
          }
          this.fitToResolution()
        })
      })
    },
    setUpMobileObjectEvents(obj) {
      let self = this
      obj.on('mouseup', element => {
        // if (!self.touchDargEnd) {
        //   let { target } = element
        //   if (!isEmpty(target) && target.floorplan) {
        //     this.sendToMobile(target.floorplan)
        //   }
        // }
        let { target } = element
        if (!isEmpty(target) && target.floorplan) {
          this.sendToMobile(target.floorplan)
        }
      })
    },
    setMobileDimensions() {
      let container = this.$refs['floorPlanContainer']

      if (isEmpty(container)) return

      this.canvas.setHeight(container.offsetHeight)
      this.canvas.setWidth(container.offsetWidth)
      this.canvas.calcOffset()
      this.canvas.renderAll()
    },
    loadFloorSpaces(categoryId) {
      if (this.floorId) {
        this.$util
          .loadSpacesContext(4, null, [
            {
              key: 'spaceCategory',
              operator: 'is',
              value: categoryId,
            },
            {
              key: 'floor',
              operator: 'is',
              value: this.floorId,
            },
            {
              key: 'space1',
              operator: 'is empty',
            },
            {
              key: 'space2',
              operator: 'is empty',
            },
            {
              key: 'space3',
              operator: 'is empty',
            },
          ])
          .then(response => {
            this.spaceList = response.records
          })
      }
    },
    registerNativeEvents() {},
    destroyFloorPlan() {
      this.canvas = null
    },
  },
}
</script>

<style>
.mobilefloorplan {
  height: 100vh;
}

.fp-mobile-viwer-settings {
  position: absolute;
  right: 2%;
  bottom: 5%;
}

.fb-settings-block button {
  padding: 12px;
}
</style>
