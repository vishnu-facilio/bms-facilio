<template>
  <div class="row dashboard-filter">
    <div class="col-12">
      <div>
        <el-select
          v-model="siteId"
          filterable
          clearable
          collapse-tags
          @change="loadsiteAssets(siteId)"
          @clear="assets = []"
          placeholder="Sites"
          class="db-filter fc-tag"
          v-tippy
        >
          <el-option
            v-for="(site, index) in sites"
            :key="index"
            :label="site.name"
            :value="site.id"
          >
          </el-option>
        </el-select>
      </div>
      <div>
        <el-select
          v-model="dashboardTemplate.parentId"
          filterable
          clearable
          collapse-tags
          @change="applyFilter"
          @clear="clearFilters"
          placeholder="Asset"
          class="db-filter fc-tag"
          v-tippy
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
      <div
        v-if="dashboardTemplate.parentId !== null"
        @click="clearFilters"
        class="clear-all-text pointer"
      >
        Clear
      </div>
      <div class="filter-icon-up" @click="close">
        <i class="el-icon-arrow-up"></i>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
export default {
  props: ['linknName', 'dashboardTemplateConfig'],
  data() {
    return {
      loading: false,
      siteId: null,
      assets: [],
      spaceList: [],
      readings: null,
      dashboardTemplate: {
        parentId: null,
      },
      templateObj: {},
      swapAssetList: {},
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSites')
    this.loadReadings()
  },
  mounted() {
    let self = this
    this.$store.dispatch('loadAssetCategory').then(function(response) {})
    if (self.$route.query.templateParams) {
      self.dashboardTemplate = JSON.parse(self.$route.query.templateParams)
    }
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),

    ...mapGetters(['getAssetCategoryByName', 'getAssetCategory']),

    category() {
      let config = this.dashboardTemplateConfig
      if (config && config.category) {
        return this.getAssetCategoryByName(config.category)
      } else {
        if (this.$route.path.endsWith('precoolerwithtemplate')) {
          return this.getAssetCategoryByName('Precooler')
        } else if (this.$route.path.endsWith('chillerwithtemplate')) {
          return this.getAssetCategoryByName('Chiller')
        } else if (this.$route.path.endsWith('ductedsplitunitwithtemplate')) {
          return this.getAssetCategoryByName('Ducted Split Unit')
        } else if (this.$route.path.endsWith('energymeterwithtemplate')) {
          return this.getAssetCategoryByName('Energy Meter')
        }
        return this.getAssetCategoryByName('FAHU')
      }
    },
    assetCategory() {
      let data = {}
      if (this.readings && this.readings.categoryWithAssets) {
        Object.keys(this.readings.categoryWithAssets).forEach(rt => {
          data[rt] = this.getAssetCategory(Number(rt)).displayName
        })

        return data
      }
      return data
    },
    showTemplate() {
      if (this.$route.query.hasOwnProperty('showTemplate')) {
        return this.$route.query.showTemplate
      } else {
        return true
      }
    },
  },
  methods: {
    getCategoryIdFromLinkName(linknName) {
      if (linknName) {
        linknName
      }
      return null
    },
    clearFilters() {
      this.dashboardTemplate = {
        parentId: null,
      }
      this.siteId = null
      this.$router.push({
        query: {
          showTemplate: true,
        },
      })
    },
    loadsiteAssets(siteId) {
      this.dashboardTemplate = {
        parentId: null,
      }
      this.assets = []
      this.$util
        .loadAsset({
          spaceId: siteId,
          categoryId: this.category.id,
        })
        .then(response => {
          this.assets = response.assets
        })
    },
    loadReadings() {
      let self = this
      self.loading = true
      let url = '/asset/getreadings'
      self.$http.get(url).then(function(response) {
        self.readings = response.data
        self.loading = false
      })
    },
    applyCustomFilter(type) {
      let self = this
      if (type === 2) {
        let dashboardTemplate = this.$helpers.cloneObject(
          this.dashboardTemplate
        )
        let template = {
          fahu: {
            parentId: dashboardTemplate.parentId,
          },
          energymeter: {
            parentId: null,
          },
        }
        let url = `/v2/assets/related/${this.dashboardTemplate.parentId}`
        self.$http.get(url).then(function(response) {
          if (
            response.data.result.relatedAssets &&
            response.data.result.relatedAssets.length
          ) {
            template['energymeter'].parentId =
              response.data.result.relatedAssets[0]
            dashboardTemplate.parentId = response.data.result.relatedAssets[0]
          }
          let dahboardtem = {
            type: 2,
            categoryTemplate: template,
          }
          self.$router.push({
            query: {
              showTemplate: true,
              templateParams: JSON.stringify(dahboardtem),
            },
          })
        })
      }
    },
    applyFilter() {
      let config = this.dashboardTemplateConfig
      if (config && config.type && config.type === 2) {
        this.applyCustomFilter(config.type)
      } else {
        let dashboardTemplate = this.$helpers.cloneObject(
          this.dashboardTemplate
        )
        this.$router.push({
          query: {
            showTemplate: true,
            templateParams: JSON.stringify(dashboardTemplate),
          },
        })
      }
    },
    close() {
      if (this.dashboardTemplate.parentId) {
        let dashboardTemplate = this.$helpers.cloneObject(
          this.dashboardTemplate
        )
        this.$router.replace({
          query: {
            showTemplate: false,
            templateParams: JSON.stringify(this.dashboardTemplate),
          },
        })
      } else {
        this.$router.replace({
          query: {
            showTemplate: false,
          },
        })
      }
    },
  },
}
</script>

<style>
.db-filter .el-input .el-input__inner {
  font-size: 13px;
  padding: 10px;
  height: 35px !important;
  border-radius: 3px;
  background-color: #ffffff;
  border: solid 1px #cff1f6;
}
.db-filter.el-select {
  margin-right: 10px;
  border: none;
  width: 250px;
}
.db-filter.el-select .el-tag {
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  text-align: left;
  color: #31a4b4;
  background: #31a4b412;
}
.db-filter > .el-select__tags {
  display: inline-flex;
}
.dashboard-filter {
  background: #fff;
  box-shadow: 0 2px 4px 0 rgba(167, 167, 167, 0.2);
  padding: 20px;
  position: relative;
  margin-bottom: 15px;
}
.dashboard-filter > .col-12 {
  display: inline-flex;
}
.db-filter input.el-select__input {
  position: relative;
  cursor: pointer;
}
.db-filter .el-select .el-input .el-select__caret {
  color: #39b2c2;
  font-weight: 600;
  font-size: 16px;
}
.dashboard-filter .filter-icon-up {
  position: absolute;
  right: 20px;
  top: 30px;
  color: #39b2c2;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
}
.clear-all-text {
  font-size: 12px;
  position: absolute;
  margin-right: 40px;
  color: #39b2c2;
  right: 20px;
  top: 34px;
}
</style>
