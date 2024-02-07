<template>
  <div class="dragabale-card">
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div class="fcu-container" v-else>
      <div class="fcu-header">
        {{ level }}
      </div>
      <div class="fcu-sections">
        {{ $t('panel.tyre.total') }}{{ (data && data.totalFcu) || 0 }}
      </div>
      <div class="fcu-data">
        <div class="fcu-count pointer" @click="moveToAssetsForRunning()">
          {{ (data && data.runningFcu) || 0 }}
        </div>
        <div class="fcu-state">
          {{ $t('panel.tyre.running') }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  props: ['widget', 'config'],
  data() {
    return {
      data: null,
      cardData: null,
      loading: false,
    }
  },
  mounted() {
    this.loadCardData()
    this.updateMeta()
  },
  computed: {
    staticKey() {
      if (this.widget.dataOptions.cardData) {
        let data = this.widget.dataOptions.cardData
        if (data && data.level) {
          return 'emrilllevel' + data.level
        }
      }
    },
    level() {
      if (this.cardData && this.cardData.level) {
        return 'level ' + this.cardData.level
      }
    },
    levelKey() {
      if (
        this.widget.dataOptions &&
        this.widget.dataOptions.cardData &&
        this.widget.dataOptions.cardData.level
      ) {
        return 'Level ' + this.widget.dataOptions.cardData.level
      }
    },
    buildingId() {
      if (
        this.widget.dataOptions &&
        this.widget.dataOptions.cardData &&
        this.widget.dataOptions.cardData.selectedBuilding
      ) {
        return parseInt(this.widget.dataOptions.cardData.selectedBuilding)
      }
    },
  },
  methods: {
    updateMeta() {
      if (this.widget.dataOptions.cardData) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.cardData
        )
        this.cardData = this.widget.dataOptions.cardData
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.cardData = JSON.parse(this.widget.dataOptions.metaJson)
      }
    },
    moveToAssetsForRunning() {
      if (this.data.runningFcuList.length > 0) {
        let queryData = {
          id: {
            operatorId: 9,
            value: this.data.runningFcuList,
          },
        }
        if (!this.$mobile) {
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('asset', pageTypes.LIST) || {}

            if (name) {
              this.$router.push({
                name,
                params: { viewname: 'all' },
                query: {
                  search: queryData,
                },
              })
            }
          } else {
            this.$router.push({
              path:
                '/app/at/assets/all?search=' +
                encodeURIComponent(JSON.stringify(queryData)),
            })
          }
        }
      }
    },
    moveToAssetsForAll() {
      if (this.data.allFcuList > 0) {
        let queryData = {
          id: {
            operatorId: 9,
            value: this.data.allFcuList,
          },
        }
        if (!this.$mobile) {
          if (isWebTabsEnabled()) {
            let { name } = findRouteForModule('asset', pageTypes.LIST) || {}

            if (name) {
              this.$router.push({
                name,
                params: { viewname: 'all' },
                query: {
                  search: queryData,
                },
              })
            }
          } else {
            this.$router.push({
              path:
                '/app/at/assets/all?search=' +
                encodeURIComponent(JSON.stringify(queryData)),
            })
          }
        }
      }
    },
    loadCardData() {
      let self = this
      let params = null
      this.loading = true
      if (this.widget.id > -1) {
        let meta = JSON.parse(this.widget.dataOptions.metaJson)
        this.selectedBuilding = meta.selectedBuilding
        params = {
          paramsJson: {
            level: 'Level ' + meta.level,
            buildingId: meta.selectedBuilding,
          },
          staticKey: 'emrillFcu',
        }
      } else {
        params = {
          paramsJson: {
            level: this.levelKey,
            buildingId: this.buildingId,
          },
          staticKey: 'emrillFcu',
        }
      }
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getCardData(response.data)
          self.updateMeta(response.data)
          self.loading = false
        })
        .catch(function(error) {
          console.log('******** error', error)
          self.loading = false
        })
    },
    getCardData(data) {
      if (data && data.cardResult) {
        let carddata = data.cardResult
        this.data = {
          name: carddata.name ? carddata.name : '',
          runningFcu: carddata.runningFcu ? carddata.runningFcu : 0,
          totalFcu: carddata.totalFcu ? carddata.totalFcu : 0,
          runningFcuList: carddata.runningFcuList
            ? carddata.runningFcuList
            : {},
          allFcuList: carddata.allFcuList ? carddata.allFcuList : {},
        }
      } else {
        this.data = {
          name: '',
          runningFcu: '',
          totalFcu: '',
          runningFcuList: '',
          allFcuList: '',
        }
      }
    },
  },
}
</script>
<style>
.fcu-header {
  font-size: 13px;
  font-weight: bold;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: center;
  color: #324056;
  text-transform: uppercase;
}

.fcu-sections {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  color: #ff3d90;
  text-align: center;
  padding-top: 30px;
  padding-bottom: 20px;
}

.fcu-container {
  align-items: center;
  justify-content: center;
  text-align: center;
  margin: auto;
  padding-top: 10%;
}
.fcu-count {
  font-size: 40px;
  font-weight: 300;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #324056;
}
.fcu-state {
  font-size: 15px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: center;
  color: #324056;
}
</style>
