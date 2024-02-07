<template>
  <outside-click v-on:onOutsideClick="$emit('close')" v-bind:visibility="true">
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
            v-on:click="$emit('close')"
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
              >
                <el-option
                  v-for="site in sites"
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
        <el-row class="pT20">
          <el-col :span="24">
            <div class="fc-black-13 text-left">Floor</div>
            <div class="">
              <el-select
                v-model="selectedFilters.floorId"
                clearable
                placeholder="Select"
                filterable
                class="fc-input-full-border2 width100 pT10"
                @change="floorChanged"
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
      </div>
      <div class="modal-dialog-footer">
        <el-button
          @click="handleSave"
          type="primary"
          class="modal-btn-save width100"
          >Apply</el-button
        >
      </div>
    </div>
  </outside-click>
</template>
<script>
import { mapState } from 'vuex'
import outsideClick from '@/OutsideClick'

export default {
  components: {
    outsideClick,
  },
  //copy and display filter props, on save , emit changed filter props
  created() {
    this.$store.dispatch('loadSites')
    this.$store.dispatch('loadBuildings')
    this.selectedFilters = this.$helpers.cloneObject(this.filterOptions)
    if (this.selectedFilters.buildingId) {
      this.getFloorList()
    }
  },
  props: ['filterOptions'],
  mounted() {},
  computed: {
    ...mapState({
      sites: state => state.sites,
      buildings: state => state.buildings,
    }),

    buildingsInSite() {
      return this.buildings.filter(e => e.siteId == this.selectedFilters.siteId)
    },
  },
  data() {
    return {
      floorList: [],
      selectedFilters: {},
    }
  },
  methods: {
    siteChanged() {
      this.selectedFilters.siteName = this.sites.find(
        e => e.id == this.selectedFilters.siteId
      ).name //site is non clearable so no need to handle site null case
      this.selectedFilters.buildingId = null
      this.selectedFilters.buildingName = null
      this.selectedFilters.floorId = null
      this.selectedFilters.floorName = null

      this.getFloorList()
    },
    buildingChanged() {
      if (this.selectedFilters.buildingId) {
        this.selectedFilters.buildingName = this.buildingsInSite.find(
          e => e.id == this.selectedFilters.buildingId
        ).name
      } else {
        this.selectedFilters.buildingName = null //clear case
      }

      this.selectedFilters.floorId = null
      this.selectedFilters.floorName = null
      this.getFloorList()
    },
    floorChanged() {
      if (this.selectedFilters.floorId) {
        this.selectedFilters.floorName = this.floorList.find(
          e => e.id == this.selectedFilters.floorId
        ).name
      } else {
        this.selectedFilters.floorName = null //clear case
      }
    },
    getFloorList() {
      if (!this.selectedFilters.buildingId) {
        this.floorList = []
        return //cant fetch floor without building
      }
      let url = `floor?buildingId=${this.selectedFilters.buildingId}`

      this.$http.get(url).then(resp => {
        this.floorList = resp.data.records
      })
    },
    handleSave() {
      this.$emit('save', this.selectedFilters)
    },
  },
}
</script>

<style lang="scss"></style>
