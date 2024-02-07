<template>
  <div>
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else>
      <!-- multiple data -->
      <div v-if="summaryAssetData">
        <div class="fc-asset-qr-block">
          <div
            v-for="asset in summaryAssetData"
            :key="asset.id"
            class="fc-asset-mulitple-block"
          >
            <div class="qr-code-dotted" v-if="asset.qrVal">
              <div>
                <qrcode-vue
                  :value="asset.qrVal || []"
                  :size="size"
                  level="H"
                  v-if="asset.qrVal"
                />
              </div>
              <div class="pT10 bold fc-black-14 text-center">
                {{ asset.name }}
              </div>
              <div class="flex-middle justify-center fc-black-12">
                {{ getAssetLocation(asset) }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- single data -->
      <div v-if="assetDataSingle.qrVal" class="fc-single-asset-data">
        <div class="qr-code-dotted">
          <qrcode-vue
            :value="assetDataSingle.qrVal || []"
            :size="size"
            level="H"
            v-if="assetDataSingle.qrVal"
          />
          <div class="pT10 bold fc-black-14">
            {{ assetDataSingle.name }}
          </div>
          <div class="flex-middle pT10 fc-black-12">
            {{ getAssetLocation(assetDataSingle) }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import Spinner from '@/Spinner'
import QrcodeVue from 'qrcode.vue'
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      loading: false,
      summaryAssetData: [],
      size: 200,
      assetDataSingle: {},
    }
  },
  components: {
    Spinner,
    QrcodeVue,
  },
  computed: {
    id() {
      const encodedAssetId = this.$route.params.assetId
      if (!encodedAssetId) return ''
      const assetId = decodeURIComponent(encodedAssetId)
      if (assetId.includes(',')) {
        return assetId.split(',').map(id => parseInt(id))
      } else {
        return parseInt(assetId)
      }
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      await this.fetchAssetQrSummary()
      this.loading = false
      this.printPage()
    },
    async fetchAssetQrSummary() {
      if (Array.isArray(this.id)) {
        let assets = []
        for (let singleId of this.id) {
          let { asset, error } = await API.fetchRecord(
            'asset',
            { id: singleId },
            { force: true }
          )
          if (!isEmpty(error)) {
            let { message = 'Error Occurred while fetching Asset' } = error
            this.$message.error(message)
          } else {
            assets.push(asset)
          }
        }
        this.summaryAssetData = assets
      } else {
        let { asset, error } = await API.fetchRecord(
          'asset',
          { id: this.id },
          { force: true }
        )
        if (!isEmpty(error)) {
          let { message = 'Error Occurred while fetching Asset' } = error
          this.$message.error(message)
        } else {
          this.assetDataSingle = asset || {}
        }
      }
    },
    getAssetLocation(asset) {
      let { space = {} } = asset || {}
      let location = ''
      if (!isEmpty(space)) {
        let {
          site,
          building,
          floor,
          space1,
          space2,
          space3,
          space4,
          space5,
        } = space
        if (!isEmpty(site) && !isEmpty(site.name)) {
          location = site.name
        }
        if (!isEmpty(building) && !isEmpty(building.name)) {
          location = `${location} > ${building.name}`
        }
        if (!isEmpty(floor) && !isEmpty(floor.name)) {
          location = `${location} > ${floor.name}`
        }
        if (space.spaceTypeEnum == 'SPACE') {
          if (!isEmpty(space1)) {
            location = `${location} > ${space1.name}`
          }
          if (!isEmpty(space2)) {
            location = `${location} > ${space2.name}`
          }
          if (!isEmpty(space3)) {
            location = `${location} > ${space3.name}`
          }
          if (!isEmpty(space4)) {
            location = `${location} > ${space4.name}`
          }
          if (!isEmpty(space5)) {
            location = `${location} > ${space5.name}`
          }
          if (!isEmpty(space)) {
            location = `${location} > ${space.name}`
          }
        }
      }
      return location
    },
    printPage() {
      this.$nextTick(() => {
        window.print()
      }, 2500)
    },
  },
}
</script>

<style>
.fc-asset-qr-block {
  width: 100%;
  height: 100%;
  top: 0px;
  left: 15px;
  right: 15px;
  position: relative;
  background: white;
  z-index: 100000;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(5, 1fr);
  grid-column-gap: 50px;
  grid-row-gap: 50px;
}
.fc-single-asset-data {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  margin-top: 20px;
}

.fc-asset-mulitple-block {
  text-align: center;
  margin-bottom: 20px;
  margin-top: 20px;
}
@media print {
  .fc-asset-qr-block,
  .fc-asset-mulitple-block {
    page-break-inside: avoid;
  }
  .fc-asset-qr-block {
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
    left: inherit !important;
    right: inherit !important;
  }
}
</style>
