<template>
  <div class="pm-planner-filter">
    <div class="pm-resource-input-search">
      <div class="flex-middle">
        <div
          class="fc-border-input-div2 pointer"
          style="width: 285px;"
          v-on:click.stop="showFilter = true"
        >
          <div class="label-txt-black">
            <!-- Vision HQ -->
            {{ siteName }}
          </div>
          <div class="fc-grey-text f11">
            {{ buildingName }} /
            {{
              selectedFilters.hasOwnProperty('assetCategoryId')
                ? assetCategoryName
                : null
            }}
            {{ selectedFilters.hasOwnProperty('teamId') ? teamName : null }}
            {{ selectedFilters.hasOwnProperty('floorId') ? floorName : null }}
          </div>
        </div>
        <el-button
          type="primmary"
          class="pm-search-btn pointer"
          v-on:click="showFilter = true"
        >
          <InlineSvg
            src="svgs/filter"
            iconClass="icon icon-md text-center vertical-baseline"
          ></InlineSvg>
        </el-button>
      </div>
    </div>

    <outside-click
      v-on:onOutsideClick="showFilter = false"
      v-bind:visibility="true"
      v-if="showFilter"
    >
      <div class="pm-resource-input-select-con">
        <div class="pL20 pR20">
          <el-tooltip
            class="item showfilter-close-icon z-30"
            effect="dark"
            content="Close"
            placement="top"
          >
            <i
              class="el-icon-close pointer"
              aria-hidden="true"
              v-on:click="showFilter = false"
            ></i>
          </el-tooltip>
          <el-row class="pT40">
            <el-col :span="24">
              <div class="fc-black-13 text-left">Site</div>
              <div class="">
                <el-select
                  v-model="selectedFilters.siteId"
                  placeholder="Select"
                  filterable
                  class="fc-input-full-border2 width100 pT10"
                  @change="siteChanged"
                  :loading="siteLoading"
                  remote
                  :remote-method="siteRemoteMethod"
                >
                  <el-option
                    v-for="site in siteList"
                    :key="site.id"
                    :label="site.name"
                    :value="site.id"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="pT20">
            <el-col :span="24">
              <div class="fc-black-13 text-left">Building</div>
              <div class="">
                <el-select
                  v-model="selectedFilters.buildingId"
                  clearable
                  placeholder="Select"
                  filterable
                  class="fc-input-full-border2 width100 pT10"
                  @change="buildingChanged"
                  :loading="siteLoading"
                  remote
                  :remote-method="buildingRemoteMethod"
                >
                  <el-option
                    v-for="building in buildingsInSite"
                    :key="building.id"
                    :label="building.name"
                    :value="building.id"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row class="pT20" v-if="selectedFilters.hasOwnProperty('floorId')">
            <el-col :span="24">
              <div class="fc-black-13 text-left">Floor</div>
              <div class="">
                <el-select
                  v-model="selectedFilters.floorId"
                  placeholder="Select"
                  clearable
                  filterable
                  class="fc-input-full-border2 width100 pT10"
                >
                  <el-option
                    v-for="floor in floorList"
                    :key="floor.id"
                    :label="floor.name"
                    :value="floor.id"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
          <el-row
            class="pT20"
            v-if="selectedFilters.hasOwnProperty('assetCategoryId')"
          >
            <el-col :span="24">
              <div class="fc-black-13 text-left">Asset category</div>
              <div class="">
                <el-select
                  v-model="selectedFilters.assetCategoryId"
                  placeholder="Select"
                  clearable
                  filterable
                  class="fc-input-full-border2 width100 pT10"
                >
                  <el-option
                    v-for="assetCategory in assetCategories"
                    :key="assetCategory.id"
                    :label="assetCategory.displayName"
                    :value="assetCategory.id"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>

          <el-row class="pT20" v-if="selectedFilters.hasOwnProperty('teamId')">
            <el-col :span="24">
              <div class="fc-black-13 text-left">Team</div>
              <div class="">
                <el-select
                  v-model="selectedFilters.teamId"
                  placeholder="Select"
                  clearable
                  filterable
                  class="fc-input-full-border2 width100 pT10"
                >
                  <el-option
                    v-for="team in teams"
                    :key="team.id"
                    :label="team.name"
                    :value="team.id"
                  >
                  </el-option>
                </el-select>
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="modal-dialog-footer">
          <el-button
            @click="handleSave"
            type="primary"
            class="modal-btn-save width100"
            >Save</el-button
          >
        </div>
      </div>
    </outside-click>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import outsideClick from '@/OutsideClick'
import { getSites, getBuildings } from 'pages/workorder/wo-util.js'
import debounce from 'lodash/debounce'
import { isEmpty } from '@facilio/utils/validation'

export default {
  components: {
    outsideClick,
  },
  //copy and display filter props, on save , emit changed filter props
  created() {
    let { filterOptions } = this
    this.$set(this, 'selectedFilters', filterOptions)
    //this.selectedFilters = this.$helpers.cloneObject(this.filterOptions)
    this.siteRemoteMethod = debounce(this.filterSites, 500)
    this.buildingRemoteMethod = debounce(this.filterBuildingsInSite, 500)
    Promise.all([
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadGroups'),
      this.initResources(),
    ]).then(() => {
      if (this.selectedFilters.buildingId) {
        this.getFloorList()
      }
      this.setFilterLabels()
    })
  },
  props: ['filterOptions'],
  computed: {
    ...mapState({
      assetCategories: state => state.assetCategory,
      teams: state => state.groups,
    }),
    ...mapGetters(['getAssetCategory']),
  },
  data() {
    return {
      buildingName: null,
      assetCategoryName: null,
      siteName: null,
      teamName: null,
      floorName: null,
      showFilter: false,
      floorList: [],
      selectedFilters: {},
      siteList: [],
      buildings: [],
      siteLoading: false,
      buildingLoading: false,
      buildingsInSite: [],
    }
  },
  methods: {
    async initResources() {
      this.siteList = await getSites()
      this.buildings = await getBuildings()
    },
    async filterSites(searchText) {
      this.siteLoading = true
      this.siteList = await getSites({ searchText })
      this.siteLoading = false
    },
    async filterBuildingsInSite(searchText) {
      let { selectedFilters } = this
      let { siteId } = selectedFilters || {}
      let params = { siteId }

      if (!isEmpty(searchText)) params = { ...params, searchText }
      this.buildingLoading = true
      this.buildingsInSite = await getBuildings(params)
      this.buildingLoading = false
    },
    handleSave() {
      //on save set new lables in filter UI
      this.setFilterLabels()
      this.$emit('filterChanged', this.selectedFilters)
      this.showFilter = false
    },
    async siteChanged() {
      this.selectedFilters.buildingId = null
      await this.filterBuildingsInSite()
      this.buildingChanged()
    },
    buildingChanged() {
      if (this.selectedFilters.hasOwnProperty('floorId')) {
        this.selectedFilters.floorId = null
        this.getFloorList()
      }
    },

    async getFloorList() {
      if (!this.selectedFilters.buildingId) {
        this.floorList = []
        return //cant fetch floor without building
      }
      let url = `floor?buildingId=${this.selectedFilters.buildingId}`

      let resp = await this.$http.get(url)
      this.floorList = resp.data.records
      return
    },

    setFilterLabels() {
      let site =
        this.siteList.filter(site => site.id == this.selectedFilters.siteId) ||
        []
      if (!isEmpty(site) && site.length == 1) {
        let { name } = site[0] || {}
        this.siteName = name || ''
      } else {
        this.siteName = ''
      }

      // Setting Building name
      let { selectedFilters, buildingsInSite } = this
      let { buildingId } = selectedFilters || {}
      if (!isEmpty(buildingId)) {
        let building = buildingsInSite.find(
          building => building.id == buildingId
        )
        let buildingName = building ? building.name : ''
        // Truncate the building name if it has more than 25 characters
        if (!isEmpty(buildingName) && buildingName.length > 25) {
          buildingName = buildingName.slice(0, 25) + '...'
        }
        this.$set(this, 'buildingName', buildingName)
      } else {
        this.$set(this, 'buildingName', 'All buildings')
      }

      this.assetCategoryName = this.selectedFilters.assetCategoryId
        ? this.getAssetCategory(this.selectedFilters.assetCategoryId)
            .displayName
        : 'All categories'
      this.teamName = this.selectedFilters.teamId
        ? this.$store.getters.getGroup(this.selectedFilters.teamId).name
        : 'All teams'

      if (this.selectedFilters.floorId) {
        this.getFloorList().then(() => {
          this.floorName = this.floorList.find(
            e => e.id == this.selectedFilters.floorId
          ).name
        })
      } else {
        this.floorName = 'All floors'
      }
    },
  },
}
</script>

<style lang="scss">
.pm-planner-filter {
  position: relative;
}
.pm-resource-input-search {
  position: relative;
  .el-input-group__append {
    border-top-left-radius: 0;
    border-bottom-left-radius: 0;
    background: none;
  }
  .fc-border-input-div2 {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
    border-right: none;
    border-radius: 0;
  }
  .label-txt-black {
    line-height: 13px;
    padding-top: 7px;
    font-weight: 500;
    max-width: 280px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    font-size: 12px;
  }
  .fc-grey-text {
    line-height: 12px;
    text-transform: unset;
    max-width: 280px;
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    font-size: 11px;
    letter-spacing: 0.3px;
    opacity: 0.6;
  }
}
.pm-resource-input-select-con {
  .modal-dialog-footer {
    position: static;
    margin-top: 30px; //filter needs to scale for different number of options
  }
  width: 333px;
  left: 0;
  right: 0;
  position: absolute;
  z-index: 200;
  box-shadow: 0 8px 20px 0 rgba(19, 23, 63, 0.09);
  border: 1px solid #d0d9e2;
  background-color: #ffffff;
  border-radius: 0;
  top: 40px;
  border-top: none;
}
.pm-search-btn {
  padding: 9px 14px 8px;
  border-radius: 0;
  border: 1px solid #d0d9e2;
  background: none;
  &:hover {
    background: #ffffff;
    border: 1px solid #d0d9e2;
    color: #324056;
  }
}
.showfilter-close-icon {
  position: absolute;
  right: 10px;
  top: 10px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  color: #324056;
}
</style>
