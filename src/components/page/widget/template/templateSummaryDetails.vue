<template>
  <div class="p20">
    <div class="fc-text-pink11 text-uppercase">Description</div>
    <div class="fc-black-13 pT10 text-left">{{ details.description }}</div>
    <div class="pT20">
      <el-row>
        <el-col :span="6">
          <div class="fc-blue-label text-uppercase text-left">Alarm Type</div>
          <div class="fc-black-13 text-left pT5">Assets</div>
        </el-col>
        <el-col :span="6">
          <div class="fc-blue-label text-uppercase text-left">
            ASSET CATEGORY
          </div>
          <div class="fc-black-13 text-left pT5">{{ details.category }}</div>
        </el-col>
        <el-col :span="6">
          <div class="fc-blue-label text-uppercase text-left">ASSETS</div>
          <div class="fc-black-13 text-left pT5">
            <div
              class="fc-black-13 text-left pT5"
              v-if="getAssetDetail.totalAssets === getAssetDetail.unavailable"
            >
              No Assets Applied
            </div>
            <div
              class="fc-black-13 text-left pT5"
              v-else-if="
                getAssetDetail.totalAssets === getAssetDetail.available
              "
            >
              All {{ details.category }}s
            </div>
            <div class="fc-black-13 text-left pT5" v-else>
              {{ partialAssetDetail() }}
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="fc-blue-label text-uppercase text-left">
            THRESHOLD METRIC
          </div>
          <div class="fc-black-13 text-left pT5">
            {{ details.threshold_metric_display }}
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import Constants from 'util/constant'

export default {
  props: ['details'],
  methods: {
    partialAssetDetail() {
      let detail = this.getAssetDetail
      let strConst = ''
      if (detail.available === 1) {
        strConst =
          'Assigned to ' +
          detail.available +
          ' ' +
          this.details.category +
          ' of ' +
          detail.totalAssets
      } else {
        strConst =
          'Assigned to ' +
          detail.available +
          ' ' +
          this.details.category +
          's of ' +
          detail.totalAssets
      }
      return strConst
    },
  },
  computed: {
    getAssetDetail() {
      return this.details.assetDetail
    },
  },
}
</script>

<style lang="scss">
.ecm-option-container {
  padding: 10px 50px 10px 30px;
  flex-direction: row;
  .ecm-reporting-label {
    color: #4273e9;
  }
}
</style>
