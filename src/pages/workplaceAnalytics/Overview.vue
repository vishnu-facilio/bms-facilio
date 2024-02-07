<template>
  <div>
    <!-- <WorkplaceAnalyticsTopbar></WorkplaceAnalyticsTopbar> -->
    <div v-if="!loading" class=" wp-card-sections pT50 relative">
      <div class="white-banner"></div>
      <div class="width50 margin-auto relative">
        <div class="wp-welocme-notes">
          {{ `Workplace Analytics` }}
        </div>
        <div class="wp-welocme-notes-dis">
          {{
            `Hey There! Welcome to workplace Analytics!
Let’s help you analyse your space, desks and occupancy!`
          }}
        </div>
        <el-input
          placeholder="Search buildings"
          v-model="search"
          class="fc-input-full-border-select2 big-serach-box"
        >
          <!-- <el-select v-model="select" slot="prepend" placeholder="Filter">
            <el-option label="Restaurant" value="1"></el-option>
          </el-select> -->
          <i
            slot="suffix"
            class="el-input__icon el-icon-search f16 custom-search-icon"
          ></i>
        </el-input>
      </div>
      <div class="wp-card-section-list">
        <template v-for="(site, index) in sites">
          <div
            :key="index"
            class="p10 pB0"
            v-if="siteBuildingMap[site.id] && siteBuildingMap[site.id].length"
          >
            <div class="fc-grey7-12 f18 bold text-left pB10">
              {{ site.name }}
            </div>
            <el-row :gutter="10" class="pB20">
              <el-col
                :span="12"
                v-for="(building, idx) in siteBuildingMap[site.id]"
                :key="`${index} ${idx}`"
              >
                <div @click="openAnalytics(building)" class="m10 mL0 mB14">
                  <StatusCard :building="building" class="pointer"></StatusCard>
                </div>
              </el-col>
            </el-row>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>
<script>
import StatusCard from 'src/pages/workplaceAnalytics/WorkplaceAnalyticsStatusCard.vue'
import { isEmpty, isUndefined } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { findRouteForTab, isWebTabsEnabled, tabTypes } from '@facilio/router'

export default {
  components: { StatusCard },
  data() {
    return {
      search: null,
      select: null,
      loading: true,
      openFloorAnalysis: false,
      selectedBuilding: null,
    }
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadSites'),
      this.$store.dispatch('loadBuildings'),
    ]).then(() => {
      this.init()
    })
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
      buildings: state => state.buildings,
    }),
    siteMap() {
      let data = {}
      this.sites.forEach(site => {
        this.$set(data, site.id, site.name)
      })
      return data
    },
    siteBuildingMap() {
      let result = {}
      let { search } = this
      if (this.buildings) {
        this.buildings.forEach(building => {
          if (search !== null && search !== '') {
            let siteName = this.siteMap[building.siteId]
            if (
              siteName
                .trim()
                .toLowerCase()
                .includes(search.trim().toLowerCase()) > 0
            ) {
              let mapedData = result[building.siteId]

              if (isEmpty(mapedData) || isUndefined(mapedData)) {
                this.$set(result, building.siteId, [])
                let d = result[building.siteId]
                d.push(building)
              } else {
                let d = result[building.siteId]
                d.push(building)
              }
            } else if (
              building.name
                .trim()
                .toLowerCase()
                .includes(search.trim().toLowerCase()) > 0
            ) {
              let mapedData = result[building.siteId]
              if (isEmpty(mapedData) || isUndefined(mapedData)) {
                this.$set(result, building.siteId, [])
                let d = result[building.siteId]
                d.push(building)
              } else {
                let d = result[building.siteId]
                d.push(building)
              }
            }
          } else {
            let mapedData = result[building.siteId]
            if (isEmpty(mapedData) || isUndefined(mapedData)) {
              this.$set(result, building.siteId, [])
              let d = result[building.siteId]
              d.push(building)
            } else {
              let d = result[building.siteId]
              d.push(building)
            }
          }
        })
      }

      return result
    },
  },
  methods: {
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'workplace-analytics' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/wp/workplacetreemap'
      }
    },
    openAnalytics(building) {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({ path: `${parentPath}/${building.id}` })
      }
    },
    init() {
      this.loading = false
    },
  },
}
</script>
<style>
.white-banner {
  background: #fff;
  width: 100%;
  height: 200px;
  position: absolute;
  margin-top: -50px;
  box-shadow: 0px -1px 5px 0 rgb(0 0 0 / 20%);
}
.wp-main-header {
  box-shadow: 0px -1px 5px 0 rgb(0 0 0 / 20%);
}
.mB14 {
  margin-bottom: 14px !important;
}
.wp-header-text {
  font-size: 20px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #6b7e91;
}
.wp-card-sections {
  overflow: auto;
  height: calc(100vh - 100px);
}
.big-serach-box .el-input-group__prepend {
  width: 20%;
  border: 0px !important;
  padding: 0;
}
.big-serach-box .el-input-group__prepend .el-select {
  width: 100%;
  margin: 0px;
}

.big-serach-box .el-input-group__prepend .el-input--suffix .el-input__inner {
  /* border: solid 1px #324056 !important; */
  border-right: 0px !important;
}
/* .big-serach-box .el-input__inner:nth-child(1) {
  border-radius: 4px 0px 0px 4px !important;
}
.big-serach-box .el-input__inner:nth-child(2) {
  border-radius: 0px 4px 4px 0px !important;
} */
.big-serach-box .el-input__inner {
  height: 50px !important;
  border-radius: 4px;
  border-color: #324056 !important;
}
.wp-card-section-list {
  width: 90%;
  margin: auto;
  padding-top: 60px;
  padding-bottom: 20px;
}
.wp-card-section-list2 {
  width: 60%;
  margin: auto;
  padding-top: 20px;
  padding-bottom: 20px;
}
.wp-card-section-list2 .fc-workplace-analytics-card {
  border-radius: 10px;
  background: #fff;
  height: 265px;
}
.wp-card-section-list2 .wp-building-img-container {
  width: 300px;
}
.big-serach-box .el-input__inner:focus {
  border-color: #a9aaca !important;
}
.big-serach-box .el-input__suffix {
  padding-right: 10px;
}
.wp-welocme-notes {
  font-size: 20px;
  line-height: 28px;
  padding-bottom: 34px;
  letter-spacing: 0.5px;
  padding-bottom: 5px;
  text-align: center;
}
.wp-welocme-notes-dis {
  text-align: center;
  padding-bottom: 20px;
  color: #767171;
  font-size: 0.85rem;
}
.custom-search-icon {
  color: #324056;
}
</style>
