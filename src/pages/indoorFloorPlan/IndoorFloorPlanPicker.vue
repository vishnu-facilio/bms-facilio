<template>
  <div>
    <el-dialog
      :title="$t('floorplan.picker_title')"
      :visible.sync="visibility"
      width="70%"
      :before-close="closeDialog"
      class="fc-dialog-center-container fp-picker-dialog"
      :append-to-body="true"
    >
      <template v-if="!loading">
        <div class=" fp-picker-dialog-body h100" v-if="indoorFloorPlanId">
          <div class="width100 inline-flex">
            <div class="width70 flex align-center" v-if="filterDialogOpen">
              <div class="width70">
                <IndoorFloorPlanPickerSwitcher
                  :floorplanId="indoorFloorPlanId"
                  :visibility.sync="filterDialogOpen"
                  :floorId="floor.id"
                  :buildingId="building.id"
                  @changeFloor="switchFloor"
                ></IndoorFloorPlanPickerSwitcher>
              </div>
              <div
                class="selelectedValue flex-middle width25"
                v-if="selectedFeature"
              >
                <inline-svg
                  v-if="modulesVsIcon[moduleName]"
                  :src="modulesVsIcon[moduleName]"
                  iconClass="icon icon-md mR10 fill-blue2"
                  class="width20px"
                ></inline-svg>
                {{ selectedFeature ? `${selectedFeature.label}` : '' }}
              </div>
            </div>
            <portal-target
              name="indoorFloorPlanPickerSearch"
              class="width30 pull-right"
            >
            </portal-target>
          </div>

          <IndoorFloorPlanPickerCore
            v-if="!loading && indoorFloorPlanId"
            :id="indoorFloorPlanId"
            :building="building"
            :floor="floor"
            :selectedModuleName="moduleName"
            :selectedFeatureId.sync="inputValue"
            :selectedModuleId="moduleId"
            @onFeatureSelect="handleFeatureSelect"
          ></IndoorFloorPlanPickerCore>
        </div>
        <div class=" fp-picker-dialog-body h100" v-else>
          <div class="fp-picker-nodata">
            {{
              siteId
                ? $t('floorplan.no_floorplan_site')
                : $t('floorplan.no_floorplan')
            }}
          </div>
        </div>
      </template>
      <div class="modal-dialog-footer">
        <el-button @click="closeDialog()" class="modal-btn-cancel uppercase"
          >{{ $t('floorplan.cancel') }}
        </el-button>
        <el-button
          :disabled="confirmBtnDisableState"
          type="primary"
          class="modal-btn-save uppercase"
          @click="confirm()"
          >{{ $t('floorplan.confirm') }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import IndoorFloorPlanPickerCore from 'src/pages/indoorFloorPlan/IndoorFloorPlanPickerCore.vue'
import IndoorFloorPlanPickerSwitcher from 'src/pages/indoorFloorPlan/components/IndoorFloorPlanPickerSwitcher.vue'

import { API } from '@facilio/api'

export default {
  props: ['visibility', 'moduleName', 'inputValue', 'siteId', 'moduleId'],
  components: { IndoorFloorPlanPickerCore, IndoorFloorPlanPickerSwitcher },
  data() {
    return {
      selectedFeature: null,
      filterDialogOpen: false,
      indoorFloorPlanId: null,
      loading: true,
      floor: {
        name: this.$t('floorplan.loading_text'),
      },
      building: {
        name: this.$t('floorplan.loading_text'),
      },
      floorId: null,
      modulesVsIcon: {
        employee: 'svgs/person',
        desks: 'svgs/office_desk',
        space: 'svgs/room2',
        lockers: 'svgs/locker',
        parkingstall: 'svgs/parking',
      },
    }
  },
  computed: {
    buildingId() {
      if (this.building?.id) {
        return this.building.id
      }
      return null
    },
    confirmBtnDisableState() {
      return this.indoorFloorPlanId ? false : true
    },
  },
  mounted() {
    this.init()
  },
  methods: {
    confirm() {
      let { selectedFeature } = this
      if (selectedFeature?.featureType) {
        if (selectedFeature.featureType === 'marker') {
          if (selectedFeature?.recordId) {
            this.$emit('onSelect', selectedFeature.recordId, selectedFeature)
            this.$emit('input', selectedFeature.recordId)
          }
        } else if (selectedFeature.featureType === 'zone') {
          if (selectedFeature?.space?.id) {
            this.$emit('onSelect', selectedFeature.space.id, selectedFeature)
            this.$emit('input', selectedFeature.space.id)
          }
        }
      }

      this.$emit('update:visibility', false)
    },
    async switchFloor(value) {
      this.loading = true
      this.floorId = value
      await this.fetchFloorDetails()
      this.loading = false
    },
    async init() {
      this.loading = true
      this.filterDialogOpen = false
      if (this.inputValue && this.moduleName) {
        await this.getModuleData()
        await this.fetchFloorDetails()
      } else {
        await this.getFirstActivePlanFromFloorList()
      }
      this.loading = false
      this.filterDialogOpen = true
    },
    async getModuleData() {
      let params = {
        moduleName: this.moduleName,
        id: this.inputValue,
      }
      let { data, error } = await API.post(`v3/modules/data/summary`, params)
      if (error) {
        this.$message.error('Failed')
        console.log('i failed')
        throw new Error('get Data Failed', error)
      } else {
        let d = data[this.moduleName]
        if (d?.floor) {
          this.floor = d.floor
          this.floorId = this.floor.id
        }
      }
    },
    handleFeatureSelect(feature, type) {
      if (type) {
        this.$set(feature, 'featureType', type)
      }
      this.selectedFeature = feature
    },
    showBuildingFilter() {
      this.filterDialogOpen = true
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    async getFirstActivePlanFromFloorList() {
      this.filterDialogOpen = false

      //get first floor with an  active floor plan
      let filters = {
        indoorFloorPlanId: {
          operatorId: 2,
          value: [],
        },
      }
      if (this.siteId) {
        filters['site'] = {
          operatorId: 36,
          value: [`${this.siteId}`],
        }
      }
      let params = {
        viewName: 'all',
        page: 1,
        perPage: 1,
        moduleName: 'floor',
        filters: JSON.stringify(filters),
      }

      let { data, error } = await API.get('v2/module/data/list', params)

      if (!error) {
        if (data?.moduleDatas && data.moduleDatas.length) {
          let floorPlanIdFromList = data.moduleDatas[0].indoorFloorPlanId
          let floor = data.moduleDatas[0]
          this.floorId = floor.id
          this.indoorFloorPlanId = floorPlanIdFromList
          if (floor?.building) {
            this.building = floor.building
          } else {
            this.building = {
              name: this.$t('floorplan.building'),
            }
          }
          if (floor) {
            this.floor = floor
          } else {
            this.floor = {
              name: this.$t('floorplan.floor'),
            }
          }
        }
      } else {
        this.$error.message('Error loading all floors', error)
      }
      this.filterDialogOpen = true
    },
    async fetchFloorDetails() {
      let params = {
        floorId: this.floorId,
      }
      let { data } = await API.post(`v2/floor/details`, params)
      if (data?.floor) {
        this.floor = data.floor
      } else {
        this.floor = {
          name: this.$t('floorplan.floor'),
        }
      }
      if (data?.floor?.building) {
        this.building = data.floor.building
      } else {
        this.building = {
          name: this.$t('floorplan.building'),
        }
      }
      if (data?.floor?.id && data?.floor?.indoorFloorPlanId) {
        this.indoorFloorPlanId = data.floor.indoorFloorPlanId
      }
    },
  },
}
</script>
<style lang="scss">
.fp-picker-dialog {
  .el-dialog__body {
    height: 70vh;
    padding: 20px;
  }
  .map.mapboxgl-map {
    border-radius: 5px;
    border: 1px solid #d0d9e2;
  }
  .el-dialog__header {
    padding-left: 20px;
  }
}
.fp-picker-dialog-body {
  height: 100%;
}
.fp-picker-header {
  padding: 15px;
}
.fp-picker-nodata {
  align-items: center;
  margin: auto;
  position: absolute;
  top: 40%;
  left: 35%;
  font-size: 14px;
  color: #8c8888;
}
</style>
