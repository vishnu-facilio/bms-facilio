<template>
  <div
    v-if="!loading"
    class="db-company-list-slider"
    v-bind:class="{ singlecard: items.length === 1 }"
  >
    <el-carousel trigger="click" :autoplay="false">
      <el-carousel-item v-for="(list, inx) in items" :key="inx">
        <div class="row sm-gutter">
          <div class="col-3" v-for="(item, index) in list" :key="index">
            <div class=" fc-b-card">
              <div class="row pT10 pB10">
                <div class="col-5 p10 text-center">
                  <img
                    class="fc-avatar"
                    :src="item.avatar"
                    style="width:100px; height:100px"
                  />
                </div>
                <div
                  class="col-7 self-center pL20"
                  v-on:click="openDashBoard(item.space)"
                >
                  <div class="row pB10 en">
                    <div class="f18 bold">{{ item.name }}</div>
                  </div>
                  <div class="f13 bold sb-secondary-color">
                    {{ $t('common.date_picker.this_month') }}
                  </div>
                  <div
                    class="row item"
                    v-if="item.hasOwnProperty('thisMonthMwh')"
                  >
                    <span class="f20 bold en" style="padding-top: 1px;">{{
                      item.thisMonthMwh || '-'
                    }}</span>
                    <div class="f14 secondary-color" style="padding-top: 5px;">
                      &nbsp; {{ item.thisMonthMwh ? 'MWh' : '' }}
                    </div>
                  </div>
                  <div
                    class="row item"
                    v-else-if="item.hasOwnProperty('thisMonthKwh')"
                  >
                    <span class="f20 bold en" style="padding-top: 1px;">{{
                      item.thisMonthKwh || '-'
                    }}</span>
                    <div class="f14 secondary-color" style="padding-top: 5px;">
                      &nbsp; {{ item.thisMonthKwh ? 'KWh' : '' }}
                    </div>
                  </div>
                  <div
                    class="row en-color-1"
                    v-if="item.hasOwnProperty('variance')"
                  >
                    <div
                      class="f15 bold row"
                      v-if="item.variance < 0"
                      style="padding-top: 5px;"
                    >
                      <img
                        style="background-color:transparent;height: 14px;"
                        src="~statics/energy/arrow-pointing-down.svg"
                      />
                      <div class="f17 bold" v-bind:style="{ color: '#75bd2b' }">
                        &nbsp; {{ item.variance + '%' || '-' }}
                      </div>
                    </div>
                    <div class="f15 bold row" v-else style="padding-top: 5px;">
                      <img
                        style="background-color:transparent;height: 14px;"
                        src="~statics/energy/arrow-up.svg"
                      />
                      <div
                        class="f17 bold"
                        v-bind:style="{ color: '##\f56c6c' }"
                      >
                        &nbsp; {{ item.variance + '%' || '-' }}
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
  props: ['rawData', 'dashboardLink'],
  data() {
    return {
      arrow: { color: 'red', direction: 'fa fa-arrow-down' },
      upHere: false,
      items: null,
      isNewDashboard: false,
      loading: true,
    }
  },
  computed: {
    linkName() {
      if (this.dashboardLink) {
        return this.dashboardLink
      } else {
        return this.$route.params.dashboardlink
      }
    },
  },
  methods: {
    openDashBoard(id) {
      if (this.linkName !== 'alarmportfolio') {
        if (this.isNewDashboard) {
          if (
            this.linkName === 'residential' ||
            this.linkName === 'commercial'
          ) {
            this.$router.push({
              path: this.linkName + 'buildingdashboard/' + id,
            })
          } else {
            this.$router.push({ path: 'buildingdashboard/' + id })
          }
        } else {
          this.$router.push({ path: 'default/' + id })
        }
      }
    },
    emitData(rawData) {
      if (rawData) {
        this.items = rawData
      }
    },
    loadEnergySource() {
      let self = this
      let params = {}
      if (this.linkName) {
        if (this.linkName === 'residential' || this.linkName === 'commercial') {
          params.dashboardKey = this.linkName
        }
      }
      this.loading = true
      self.$http
        .post(
          '/report/energy/portfolio/getAllBuildingsWithRootMeterMeta',
          params
        )
        .then(function(response) {
          let reportData = response.data.reportData
          if (reportData && reportData.buildingsWithRootMeterMeta) {
            if (self.$account.user.id === 1214016) {
              reportData.buildingsWithRootMeterMeta = reportData.buildingsWithRootMeterMeta.filter(
                rt => {
                  if (
                    rt.building === 1109686 ||
                    rt.building === 1109785 ||
                    rt.building === 1109784
                  ) {
                    return rt
                  }
                }
              )
            }
            reportData.buildingsWithRootMeterMeta.forEach(element => {
              if (
                element.hasOwnProperty('thisMonthKwh') &&
                element.thisMonthKwh
              ) {
                element.thisMonthMwh = (
                  Number(element.thisMonthKwh) / 1000
                ).toFixed(2)
              }
            })
            self.items = self.prepareitem(reportData.buildingsWithRootMeterMeta)
          }

          self.isNewDashboard = true
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
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
      this.emitData()
    } else {
      this.loadEnergySource()
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
.add-label {
  display: none;
}
.item:hover span.align {
  display: none;
}
.item:hover span.add-label {
  display: block;
}
.db-company-list-slider .el-carousel__container {
  height: 135px;
}
.db-company-list-slider .el-carousel__indicator {
  display: none;
}
.singlecard .el-carousel__container .el-carousel__arrow {
  display: none;
}
</style>
