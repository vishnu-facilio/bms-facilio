<script>
import { isEmpty } from '@facilio/utils/validation'
import { getBaseURL } from 'util/baseUrl'

export default {
  data() {
    return {
      floorPlan: {},
      leagend: {},
      isNew: false,
      controlCategoryList: [],
      spaceControllableCategoriesMap: [],
      spaceControlCategory: [],
      floor: null,
      floorPlanId: null,
      spaces: [],
      flooplans: [],
      spaceList: [],
      pubSubWatcherKey: null,
      employeeList: [],
      employeeFields: [],
    }
  },
  computed: {
    devloper() {
      if (this.$route.query && this.$route.query.devloper) {
        return true
      }
      return false
    },
  },
  methods: {
    fetchEmployeeDetails() {
      if (this.viewMode === 'employee') {
        let url = `v2/module/data/list?moduleName=custom_deskmanager&page=1&perPage=500&viewName=all`
        this.$http.get(url).then(response => {
          if (
            response.data &&
            response.data.result &&
            response.data.result.moduleDatas
          ) {
            this.employeeList = response.data.result.moduleDatas
          }
        })
        this.getFields()
      }
    },
    getFields() {
      let moduleName = 'custom_deskmanager'
      let url = `/v2/filter/advanced/fields/${moduleName}`
      this.$http.get(url).then(response => {
        if (
          response.data &&
          response.data.result &&
          response.data.result.fields
        ) {
          this.employeeFields = response.data.result.fields
        }
      })
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
    setLeagend() {
      let { floorPlan } = this
      if (!isEmpty(floorPlan) && floorPlan.leagend) {
        let leagend = JSON.parse(floorPlan.leagend)
        this.$set(this, 'leagend', leagend)
      }
    },
    setFloorPlanData(floorPlan) {
      this.$set(this, 'floorPlan', this.formatImgUrl(floorPlan))
    },
    formatImgUrl(floorPlan) {
      if (floorPlan && floorPlan.canvas) {
        let canvas = JSON.parse(floorPlan.canvas)
        let imageIndex = canvas.objects.findIndex(rt => rt.type === 'image')
        let obj = canvas.objects[imageIndex]
        let url = obj.src
        if (obj && url) {
          let d = url.split('/')
          let fileId = d[d.length - 1] ? Number(d[d.length - 1]) : null
          if (fileId) {
            let newUrl = `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
            this.$set(canvas.objects[imageIndex], 'src', newUrl)
            this.$set(floorPlan, 'canvas', JSON.stringify(canvas))
          }
        }
      }
      return floorPlan
    },
    addFloorPlan(floorPlan) {
      return this.$http
        .post('/v2/floorPlan/add', { floorPlan: floorPlan })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.setFloorPlanData(data.result.floorPlan)
          }
        })
    },
    fetchFloorDetails() {
      if (this.floorId) {
        return this.$http
          .get(`/v2/pages/floor?id=${this.floorId}`)
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.$set(this, 'floor', data.result.record)
              if (this.floorplanId) {
                this.$set(this, 'floorPlanId', this.floorplanId)
              } else {
                this.$set(
                  this,
                  'floorPlanId',
                  data.result.record.defaultFloorPlanId
                )
              }
            }
          })
      }
    },
    fetchViewMode(floorPlanId) {
      let viewMode = {
        viewMode: this.viewMode,
        floorPlanId: floorPlanId,
        scriptModeInt: 1,
      }
      this.$http
        .post('/v2/floorPlan/viewFloorPlanMode', {
          floorPlanViewMode: viewMode,
        })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            data.result.data
            this.$emit('floorModeData', data.result.data)
          }
        })
    },
    fetchFloorPlan(fetchFloorPlan) {
      if (fetchFloorPlan > 0 || this.floorPlanId > 0) {
        this.$emit('floorplanId', fetchFloorPlan || this.floorPlanId)
        if (this.floorPlanId && this.viewMode && this.loadViewMode) {
          this.fetchViewMode(this.floorPlanId)
        }

        return this.$http
          .post('/v2/floorPlan/get', {
            floorPlan: { id: fetchFloorPlan || this.floorPlanId },
          })
          .then(({ data }) => {
            if (data.responseCode === 0) {
              this.setFloorPlanData(data.result.floorPlan)
              this.setLeagend(data.result.floorPlan)
            }
          })
      } else {
        this.$emit('floorplanId', -1)
        return
      }
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
    fetchListOfFloorPlans() {
      let floorPlan = {
        floorId: this.floorId,
      }
      this.$http
        .post('/v2/floorPlan/getListOfFloorPlan', { floorPlan: floorPlan })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.flooplans = data.result.floorPlans
          }
        })
    },
    updateFloorPlan() {
      this.floorPlan.canvas = JSON.stringify(this.canvas.toJSON(['floorplan']))
      this.floorPlan.leagend = JSON.stringify(this.leagend)
      this.floorPlan.floorPlanId = this.floorPlan.id
      return this.$http
        .post('/v2/floorPlan/update', { floorPlan: this.floorPlan })
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.setFloorPlanData(data.result.floorPlan)
          }
        })
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
    getspaceControlCategory(spaceId) {
      return this.$http
        .get(`/v2/controlAction/getControllableCategories?spaceId=${spaceId}`)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.spaceControlCategory = data.result.controllableCategories
          }
        })
    },
    loadFloorSpaces(categoryId) {
      if (this.floorId) {
        this.$util
          .loadSpacesContext(4, null, [
            // {
            //   key: 'spaceCategory',
            //   operator: 'is',
            //   value: categoryId,
            // },
            {
              key: 'floor',
              operator: 'is',
              value: this.floorId,
            },
          ])
          .then(response => {
            this.spaceList = response.records
          })
      }
    },
  },
}
</script>
