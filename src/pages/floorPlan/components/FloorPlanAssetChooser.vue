<template>
  <div class="fp-asset-chooser">
    <div class="fp-leagend-header-settings">
      <p class="fc-input-label-txt pB5 pT5">Asset Category</p>
      <el-select
        filterable
        @change="loadAssets()"
        v-model="leagend.categoryId"
        placeholder="Select"
        class="fc-input-select fc-input-full-border2 width100"
      >
        <el-option
          v-for="(category, index) in assetCategory"
          :key="index"
          :label="category.name"
          :value="category.id"
        >
        </el-option>
      </el-select>
    </div>
    <div class="fp-leagend-header-settings">
      <p class="fc-input-label-txt pB5 pT5">Asset Name</p>
      <el-select
        filterable
        v-model="leagend.assetId"
        placeholder="Select"
        class="fc-input-select fc-input-full-border2 width100"
      >
        <el-option
          v-for="(asset, index) in assets"
          :key="index"
          :label="asset.name"
          :value="asset.id"
        >
        </el-option>
      </el-select>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: ['leagend'],
  data() {
    return {
      assetFields: [],
      assets: [],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  mounted() {},
  methods: {
    loadAssets() {
      let categoryId = this.leagend.categoryId
      let filter = {
        category: { operator: 'is', value: [categoryId + ''] },
      }
      this.$http
        .get('/asset/all?filters=' + encodeURIComponent(JSON.stringify(filter)))
        .then(response => {
          this.assets = response.data.assets
        })
    },
  },
}
</script>
<style>
.fp-leagend-header-settings .fc-input-full-border2 .el-input__inner {
  width: 100%;
}
.fp-asset-chooser {
  position: absolute;
  right: 10px;
  z-index: 10;
  background: #fff;
  top: 140px;
  padding: 10px;
  border: 1px solid #f2f2f2;
}
</style>
