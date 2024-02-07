<template>
  <div
    v-if="!loading"
    class="db-company-list"
    v-bind:class="{ singlecard: items.length === 1 }"
  >
    <el-carousel trigger="click" :autoplay="false">
      <el-carousel-item v-for="(list, inx) in items" :key="inx">
        <div class="row sm-gutter">
          <div class="col-3" v-for="(item, index) in list" :key="index">
            <div class="fc-b-card">
              <div class="row pT10 pB10">
                <div class="col-5 p10 text-center">
                  <img
                    class="fc-avatar"
                    :src="item.avatarUrl"
                    style="width:100px; height:100px"
                  />
                </div>
                <div class="col-7 self-center">
                  <div class="row en">
                    <div class="f18 bold">{{ item.displayName }}</div>
                  </div>
                  <div
                    class="f12 pB10 en-secondary-color"
                    style="padding-top: 3px;"
                    v-if="item.city"
                  >
                    {{ item.city }}
                  </div>
                  <div class="row">
                    <div class="f14  bold sb-secondary-color">
                      {{ item.headerText }} :
                    </div>
                    <div class="f14  bold">{{ item.consumpation }}</div>
                  </div>
                  <div class="row en-color-1">
                    <div class="f15 bold" v-if="item.arrow">
                      <i
                        v-bind:class="item.arrow.direction"
                        class="fa fa-arrow-down"
                        aria-hidden="true"
                        v-bind:style="{ color: item.arrow.color }"
                      ></i>
                      <div
                        class="f17 bold"
                        v-bind:style="{ color: item.arrow.color }"
                      >
                        &nbsp; {{ item.variance || 0 }} %
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
</template>
<script>
export default {
  props: ['rawData'],
  data() {
    return {
      arrow: { color: 'red', direction: 'fa fa-arrow-down' },
      upHere: false,
      loading: false,
      items: [
        {
          drop: 22,
          consumption: '23,000',
          site: 'Dar Al Rokham',
          label: 'Facilio HQ',
          image: '',
        },
        {
          drop: 12,
          consumption: '29,000',
          site: 'Al Meydan',
          label: 'Zinc University',
          image: '',
        },
        {
          drop: 13,
          consumption: '18,000',
          site: 'Al Safa',
          label: 'PaperTower Hotel',
          image: '',
        },
        {
          drop: 15,
          consumption: ' 7,000',
          site: 'Al Barsha',
          label: 'Rawana Building',
          image: '',
        },
      ],
      isNewDashboard: false,
    }
  },
  methods: {
    loadBuildingDetails() {
      let self = this
      const formData = new FormData()
      this.loading = true
      self.$http
        .post('/report/alarms/getAllBuildings', formData)
        .then(function(response) {
          let reportData = response.data
          if (reportData != null && reportData.length !== 0) {
            let companyLists = []
            reportData.filter(row => {
              if (row) {
                let activeAlarm, totalAlarms
                activeAlarm = row.activeCount || 0
                totalAlarms = row.totalCount || 0
                let thisx = {
                  avatarUrl: row.avatar,
                  displayName: row.name,
                  city: row.city,
                  consumpation: activeAlarm,
                  total: totalAlarms,
                  id: row.id,
                  subHeader: 'Total Alarms',
                  headerText: 'Active Alarms',
                }
                companyLists.push(thisx)
              }
            })
            self.items = self.prepareitem(companyLists)
            self.isNewDashboard = true
            self.loading = false
          }
        })
        .catch(() => {})
    },
    emitData(rawData) {
      if (rawData) {
        this.items = rawData
      }
    },
    prepareitem(item) {
      let groups = [],
        i
      let chunkSize = 4
      for (i = 0; i < item.length; i += chunkSize) {
        groups.push(item.slice(i, i + chunkSize))
      }
      return groups
    },
  },
  mounted: function() {
    if (this.rawData) {
      this.emitData(this.rawData)
    } else {
      this.loadBuildingDetails()
    }
  },
  watch: {
    rawData: function(rawData) {
      this.emitData(rawData)
    },
  },
}
</script>
<style>
.db-company-list .el-carousel__container {
  height: 135px;
}
.db-company-list .el-carousel__indicator {
  display: none;
}
.singlecard .el-carousel__container .el-carousel__arrow {
  display: none;
}
</style>
