<template>
  <div class="space-qr-page">
    <div v-if="loading" class="flex-middle justify-content-center">
      <spinner :show="true" :size="80"></spinner>
    </div>
    <div v-else class="space-qr-container">
      <div
        class="flex-middle flex-wrap justify-center gap10 print-space-qrcode"
      >
        <div class="qr-code-dotted-space">
          <qrcode-vue :value="spaceData.qrVal || []" :size="size" level="H" />
          <div
            class="label-txt-black fwBold pT10 text-center"
            v-if="spaceData.name"
          >
            {{ spaceData.name }}
          </div>
          <div class="flex-middle flex-wrap justify-center pT10 gap7">
            <div class="fc-black2-label12" v-if="siteData.name">
              {{ siteData.name }} <span class="pL5 pR5">></span>
            </div>
            <div class="fc-black2-label12" v-if="buildingData.name">
              {{ buildingData.name }} <span class="pL5 pR5">></span>
            </div>
            <div class="fc-black2-label12 text-center" v-if="floorData.name">
              {{ floorData.name }}
            </div>
            <div
              class="fc-black2-label12"
              v-if="spaceData && spaceData.space1 && spaceData.space1.name"
            >
              {{ spaceData && spaceData.space1 && spaceData.space1.name }}
              <span class="pL5 pR5">></span>
            </div>
          </div>
        </div>
        <!-- <div v-for="(subSpace, inx) in subSpaceList" :key="inx">
          <div class="qr-code-dotted-space">
            <qrcode-vue :value="subSpace.qrVal || []" :size="size" level="H" />
            <div class="label-txt-black pT10 text-center fwBold">
              {{ subSpace.name }}
            </div>
            <div class="flex-middle flex-wrap justify-center pT10 gap7">
              <div class="fc-black2-label12" v-if="siteData.name">
                {{ siteData.name }} <span class="pL5 pR5">></span>
              </div>
              <div class="fc-black2-label12" v-if="buildingData.name">
                {{ buildingData.name }} <span class="pL5 pR5">></span>
              </div>
              <div class="fc-black2-label12 text-center" v-if="floorData.name">
                {{ floorData.name }} <span class="pL5 pR5">></span>
              </div>
              <div class="fc-black2-label12 text-center" v-if="spaceData.name">
                {{ spaceData.name }}
              </div>
            </div>
          </div>
        </div> -->
      </div>
    </div>
  </div>
</template>
<script>
import QrcodeVue from 'qrcode.vue'
import { API } from '@facilio/api'
import Spinner from '@/Spinner'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['moduleName'],
  title() {
    return 'Space Qr'
  },
  data() {
    return {
      loading: true,
      size: 200,
      buildingData: {},
      siteData: {},
      floorData: {},
      spaceData: {},
    }
  },
  components: {
    QrcodeVue,
    Spinner,
  },
  computed: {
    id() {
      return this.$route.params.spaceId
        ? parseInt(this.$route.params.spaceId)
        : ''
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    async init() {
      this.loading = true
      await this.fetchSpaceOverview()
      await this.getSubSpaceList()
      this.loading = false
      this.printPage()
    },
    async fetchSpaceOverview() {
      let { id } = this
      if (!isEmpty(id)) {
        let { error, space } = await API.fetchRecord('space', { id })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.spaceData = space || {}
          await this.fetchFloorOverView(this.spaceData)
          await this.fetchBuildingOverView(this.spaceData)
          await this.fetchSiteOverView(this.spaceData)
        }
      }
    },
    async fetchFloorOverView(space) {
      let { floor } = space || {}
      let { id } = floor || {}
      if (!isEmpty(id)) {
        let { error, floor: record } = await API.fetchRecord('floor', { id })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.floorData = record || {}
        }
      }
    },
    async fetchBuildingOverView(space) {
      let { building } = space || {}
      let { id } = building || {}
      if (!isEmpty(id)) {
        let { error, building: record } = await API.fetchRecord('building', {
          id,
        })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.buildingData = record || {}
        }
      }
    },
    async fetchSiteOverView(space) {
      let { site } = space || {}
      let { id } = site || {}
      if (!isEmpty(id)) {
        let { error, site: record } = await API.fetchRecord('site', {
          id,
        })
        if (error) {
          this.$message.error(
            error.message || this.$t('common._common.error_occured')
          )
        } else {
          this.siteData = record || {}
        }
      }
    },

    async getSubSpaceList() {
      let filters = {
        spaceType: {
          operator: '=',
          value: ['4'],
        },
        space1: {
          operator: 'is',
          value: [JSON.stringify(this.id)],
        },
        space2: {
          operator: 'is empty',
        },
        space3: {
          operator: 'is empty',
        },
        space4: {
          operator: 'is empty',
        },
        space5: {
          operator: 'is empty',
        },
      }

      let payload = {
        orderBy: 'spaceType',
        orderType: 'asc',
        filters: JSON.stringify(filters),
      }
      let { data, error } = await API.get('space', payload)
      if (!error) {
        let { records } = data || {}
        this.subSpaceList = records
      } else {
        this.$message.error(
          error.message || this.$t('common._common.error_occured')
        )
      }
    },
    printPage() {
      let { spaceData, subSpaceList } = this
      let { qrVal } = spaceData || {}
      let isSupSpaceQrAvailable = subSpaceList.some(
        space => !isEmpty(space?.qrVal)
      )
      if (!isEmpty(qrVal) || isSupSpaceQrAvailable) {
        this.$nextTick(() => {
          window.print()
        }, 2500)
      }
    },
  },
}
</script>

<style lang="scss">
.space-qr-page {
  margin-top: 50px;
  margin-left: auto;
  margin-right: auto;
  max-width: 1300px;
}
.space-qr-container {
  display: flex;
  align-items: center;
  justify-content: center;
}
.qr-code-dotted-space {
  width: 300px;
  height: 370px;
  border: 1px solid #000;
  padding: 20px;
  border-style: dashed;
  border-width: 1px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
@media print {
  .space-qr-page {
    width: 100%;
    height: 100%;
    margin-top: 0;
    display: flex;
    align-items: left;
    justify-content: flex-start;
    max-width: inherit;
  }
  .qr-code-dotted-space {
    page-break-inside: avoid;
    width: 250px;
    height: 300px;
    canvas {
      height: 150px !important;
      width: 150px !important;
    }
  }
  .print-space-qrcode {
    gap: 20px !important;
    align-items: center;
    justify-content: center;
  }
}
</style>
