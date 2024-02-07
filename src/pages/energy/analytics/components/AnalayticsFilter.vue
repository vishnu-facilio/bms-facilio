<template>
  <div class="building-popover">
    <div class="side-bar-site-details flex-middle">
      <div v-if="isSiteAnalysis">
        <InlineSvg
          :src="`svgs/spacemanagement/site`"
          class="pointer"
          iconClass="icon-50"
        ></InlineSvg>
      </div>
      <div v-else class="building-icon-container" style="margin: -10px -10px;">
        <InlineSvg
          :src="`svgs/spacemanagement/building`"
          class="pointer"
          iconClass="icon-50 resize_building_icon"
        ></InlineSvg>
      </div>
      <div class="pL10 width100">
        <div class="display-flex-between-space">
          <div class="fc-id">{{ '#' }}{{ selectedBuildingId }}</div>
          <div style="margin-top:12px; margin-right:15px;">
            <div
              slot="reference"
              class="popover-text"
              @click="showLookupWizard()"
            >
              {{ $t('maintenance._workorder.change_site_message_label') }}
            </div>
          </div>
        </div>
        <div class="display-flex-between-space" style="margin-top:-8px;">
          <div
            class="label-text pointer label_text"
            @click="showLookupWizard()"
          >
            {{ selectedBuildingLabel }}
          </div>
        </div>
      </div>
    </div>
    <div v-if="showingLookupWizard">
      <LookupWizard
        :canShowLookupWizard.sync="showingLookupWizard"
        :field="isSiteAnalysis ? siteFieldObj : buildingFieldObj"
        @setLookupFieldValue="
          value =>
            toggleBuilding(
              value.field.selectedItems[0].value,
              value.field.selectedItems[0].label
            )
        "
      ></LookupWizard>
    </div>
  </div>
</template>
<script>
import { LookupWizard } from '@facilio/ui/forms'
import { getFieldValue } from 'util/picklist'
export default {
  props: ['config', 'selectedBuildings', 'multiple', 'zones', 'isSiteAnalysis'],
  components: {
    LookupWizard,
  },
  data() {
    return {
      selectedBuildingsInternal: [],
      showingLookupWizard: false,
      selectedBuildingLabel: '',
      selectedBuildingId: null,
      buildingFieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'building',
        field: {
          lookupModule: {
            name: 'building',
            displayName: 'Building',
          },
        },
        selectedItems: [],
        forceFetchAlways: true,
        filters: {},
        isDisabled: false,
      },
      siteFieldObj: {
        isDataLoading: false,
        options: [],
        lookupModuleName: 'site',
        field: {
          lookupModule: {
            name: 'site',
            displayName: 'Site',
          },
        },
        selectedItems: [],
        forceFetchAlways: true,
        filters: {},
        isDisabled: false,
      },
    }
  },
  created() {
    // this.$store.dispatch('loadSites')
    // this.$store.dispatch('loadBuildings')
  },
  computed: {
    // spaceList() {
    //   let spaceList = []
    //   if (this.isSiteAnalysis) {
    //     if (this.$store.state.sites) {
    //       spaceList = this.$store.state.sites.map(site => ({
    //         id: site.id,
    //         name: site.name,
    //         type: 'site',
    //       }))
    //     }
    //   } else {
    //     if (this.$store.getters.getBuildingsPickList()) {
    //       let keys = Object.keys(this.$store.getters.getBuildingsPickList())
    //       for (let key of keys) {
    //         let buildingName = this.$store.getters.getBuildingsPickList()[key]
    //         spaceList.push({
    //           id: parseInt(key),
    //           name: buildingName,
    //           type: 'building',
    //         })
    //       }
    //     }
    //     if (this.zones) {
    //       for (let zone of this.zones) {
    //         spaceList.push({
    //           id: zone.id,
    //           name: zone.name,
    //           type: 'zone',
    //         })
    //       }
    //     }
    //   }
    //   return spaceList
    // },
    // filteredBuildingList() {
    //   if (this.spaceList && this.spaceList.length) {
    //     if (this.searchKeyword && this.searchKeyword.trim().length) {
    //       let matchedBuildings = []
    //       for (let space of this.spaceList) {
    //         if (
    //           space.name
    //             .toLowerCase()
    //             .indexOf(this.searchKeyword.toLowerCase()) !== -1
    //         ) {
    //           matchedBuildings.push(space)
    //         }
    //       }
    //       return matchedBuildings
    //     }
    //     return this.spaceList
    //   }
    //   return null
    // },
    // selectedBuildingName() {
    //   if (this.isSiteAnalysis) {
    //     if (
    //       this.selectedBuildingsInternal &&
    //       this.selectedBuildingsInternal.length
    //     ) {
    //       if (this.selectedBuildingsInternal.length === 1) {
    //         return this.spaceList.find(
    //           sp => sp.id === parseInt(this.selectedBuildingsInternal[0])
    //         ).name
    //       } else if (
    //         this.spaceList &&
    //         Object.keys(this.spaceList).length ===
    //           this.selectedBuildingsInternal.length
    //       ) {
    //         return 'All Sites'
    //       }
    //       return this.selectedBuildingsInternal.length + ' Sites'
    //     }
    //     return 'No Sites'
    //   } else {
    //     if (
    //       this.selectedBuildingsInternal &&
    //       this.selectedBuildingsInternal.length
    //     ) {
    //       if (this.selectedBuildingsInternal.length === 1) {
    //         return this.spaceList.find(
    //           sp => sp.id === parseInt(this.selectedBuildingsInternal[0])
    //         ).name
    //       } else if (
    //         this.spaceList &&
    //         Object.keys(this.spaceList).length ===
    //           this.selectedBuildingsInternal.length
    //       ) {
    //         return 'All Buildings'
    //       }
    //       return this.selectedBuildingsInternal.length + ' Buildings'
    //     }
    //     return 'No Buildings'
    //   }
    // },
  },

  mounted() {
    this.selectedBuildingsInternal = this.selectedBuildings
    this.getBuildingLabel()
    this.selectedBuildingId =
      this.selectedBuildings && this.selectedBuildings[0]
        ? this.selectedBuildings[0]
        : this.isSiteAnalysis
        ? 'No Sites'
        : 'No Buildings'
  },
  watch: {
    selectedBuildingsInternal: function(newVal) {
      this.$emit('update:selectedBuildings', this.selectedBuildingsInternal)
    },
  },
  methods: {
    showLookupWizard() {
      this.showingLookupWizard = true
    },
    async getBuildingLabel() {
      this.selectedBuildingsInternal = this.selectedBuildings
      if (this.selectedBuildingsInternal[0] && !this.isSiteAnalysis) {
        await getFieldValue({
          lookupModuleName: 'building',
          selectedOptionId: [this.selectedBuildingsInternal[0]],
        }).then(({ error, data }) => {
          if (!error && data) {
            let value = data[0].label
            this.selectedBuildingLabel = value
            this.buildingFieldObj.selectedItems[0] = {
              value: this.selectedBuildingsInternal[0],
              label: this.selectedBuildingLabel,
            }
          }
        })
      } else if (this.selectedBuildingsInternal[0] && this.isSiteAnalysis) {
        await getFieldValue({
          lookupModuleName: 'site',
          selectedOptionId: [this.selectedBuildingsInternal[0]],
        }).then(({ error, data }) => {
          if (!error && data) {
            let value = this.$getProperty(data, '0.label')
            this.selectedBuildingLabel = value
            this.siteFieldObj.selectedItems[0] = {
              value: this.selectedBuildingsInternal[0],
              label: this.selectedBuildingLabel,
            }
          }
        })
      } else {
        this.selectedBuildingLabel = this.isSiteAnalysis
          ? 'No Sites'
          : 'No Buildings'
      }
    },
    toggleBuilding(buildingId, buildingName) {
      if (this.multiple) {
        let idx = this.selectedBuildingsInternal.indexOf(buildingId)
        if (idx !== -1) {
          if (this.selectedBuildingsInternal.length > 1) {
            this.selectedBuildingsInternal.splice(idx, 1)
          }
        } else {
          this.selectedBuildingsInternal.push(buildingId)
        }
      } else {
        this.selectedBuildingsInternal = [buildingId]
        if (buildingId && buildingName) {
          this.selectedBuildingId = buildingId
          this.selectedBuildingLabel = buildingName
        }
      }
    },
    updateSelectedBuilding(buildings) {
      this.selectedBuildingsInternal = buildings
    },
  },
}
</script>
<style>
.analytics-selected-txt {
  width: 260px;
  min-width: 260px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  display: block;
  padding-left: 20px;
  padding-right: 10px;
  padding-top: 10px;
  padding-bottom: 10px;
  cursor: pointer;
}
.analytics-list-data {
  display: flex;
  align-items: center;
  flex-direction: row;
}
.analytics-list-data.active {
  background-color: #f0f7f8;
  color: #2c9baa;
  font-weight: 500;
  transition: all 0.3s;
}
.analytics-list-data .el-icon-check {
  color: #39b2c2;
  font-size: 20px;
  font-weight: 500;
}
.analytics-filter-header .analytics-F-search {
  width: 100%;
  height: 54px;
  border: solid 1px #f0f0f0;
  border-left: none;
  border-right: none;
  border-top: none;
  padding-left: 10px !important;
}
.analytics-F-search #q-app input[type='search']:focus {
  border: 1px solid #8fd2db !important;
}
.analytics-filter-body {
  height: 194px;
  min-height: 194px;
  padding-bottom: 20px;
  overflow-y: scroll;
  overflow-x: hidden;
}
.analytics-filter-footer {
  float: right;
  width: 100%;
  border: none;
}
.filter-ok-btn {
  border: solid 1px #39b2c2 !important;
  background-color: #ffffff !important;
  cursor: pointer;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #31a4b4;
  height: 30px;
  padding: 8px 20px;
  float: right;
  margin-right: 15px;
}
.filter-ok-btn:hover {
  background-color: #39b2c2 !important;
  color: #fff;
  cursor: pointer;
  transition: 0.2s ease-in;
  -webkit-transition: 0.2s ease-in;
}
.analytics-filter-container {
  max-width: 270px;
  width: 270px;
  height: 300px;
  min-height: 300px;
  background: #fff !important;
  box-shadow: -4px -3px 20px 12px rgba(195, 195, 195, 0.4);
  background-color: #ffffff;
  top: 57px;
  z-index: 1;
  position: absolute;
  /* transition-timing-function: cubic-bezier(0.54, -0.28, 0.95, 0.79);
  transform: translateX(0);
  transition: transform 3s cubic-bezier(0.43, -0.1, 0.85, -0.04); */
  transition: all 1.5s ease-in-out;
  -webkit-transition: all 1.5s ease-in-out;
}
.analytics-filter-bg .el-select input {
  background-color: #fff !important;
}
.period-select .el-icon-arrow-up {
  color: #5a7591 !important;
  font-size: 16px;
  font-weight: 600;
}
.period-select .el-select .el-input.is-focus .el-input__inner {
  border: solid 1px #39b2c2 !important;
}
.input-search-icon {
  float: right;
  position: absolute;
  top: 22px;
  right: 21px;
  font-size: 20px;
  color: #e0e0e0;
}
.btn-buildong-selected {
  background: #fff;
  border: none;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.4px;
  color: #333333;
  padding-left: 15px;
}
.btn-buildong-selected:hover {
  color: #333333 !important;
  background: #fff !important;
}
.analytics-building-popover .popover__reference:focus:hover,
.analytics-building-popover .el-popover__reference:focus:not(.focusing) {
  color: #333333 !important;
  background: #fff !important;
}
.btn-buildong-selected .el-icon-arrow-down {
  padding-left: 7px;
  position: relative;
  top: 2px;
}
.analytics-building-img {
  width: 35px;
  height: 35px;
  background-color: #f1f1f1;
  border-radius: 50%;
}
.analytics-building-popover {
  position: relative;
  top: 2px;
}
.building-popover {
  padding-top: 6px;
  padding-bottom: 7px;
}
.popover-text {
  font-size: 12px;
  letter-spacing: 0.42px;
  color: #ff3184;
  cursor: pointer;
}
.popover-text:hover {
  text-decoration: underline;
}
.hide {
  display: none;
}
.resize_building_icon {
  transform: scale(0.6);
}
.icon-50 {
  height: 58px;
  width: 50px;
}
.pL12 {
  padding-left: 12px !important;
}
.label_text {
  font-size: 15px;
  font-weight: 500;
  max-width: 181px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
