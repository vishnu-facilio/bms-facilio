<template>
  <el-dialog
    :visible.sync="visibility"
    :fullscreen="true"
    title="SELECT THE FLOOR"
    custom-class="fc-dialog-form setup-dialog-right setup-dialog25 setup-dialog show-right-dialog"
    :before-close="closeDialog"
    :append-to-body="true"
    style="z-index: 999999"
  >
    <div>
      <el-input
        placeholder="Filter space"
        class="fc-input-full-border2 shape-filter-box"
        suffix-icon="el-icon-search"
        v-model="filterVariables"
      ></el-input>
      <div class="fp-sm-container">
        <el-collapse v-model="activeSelections">
          <template v-for="(value, key) in filteredVariables">
            <el-collapse-item
              v-if="value && value.length"
              :key="key"
              :title="key"
              :name="key"
              class="fc-graphic-legend-collapse"
            >
              <div
                :class="[
                  'building-anlysis-label-txt',
                  'clearboth',
                  {
                    'asset-list-block-txt-active':
                      selectedSpace.id === space.id,
                  },
                ]"
                v-for="(space, index) in value"
                :key="index"
                @click="selectSpace(space)"
              >
                <i
                  v-bind:class="{
                    'fa fa-circle': true,
                    'dot-blue-icon': space.id === selectedSpace.id,
                    'dot-grey-icon': space.id !== selectedSpace.id,
                  }"
                  aria-hidden="true"
                ></i>
                <div class="width80">{{ space.name }}</div>
                <span class="active-arrow"
                  ><img
                    v-if="space.id === selectedSpace.id"
                    src="~assets/arrow-pointing-to-right.svg"
                    height="14px"
                    width="14px"
                /></span>
              </div>

              <!-- <div v-for="(space, index) in value" :key="index" class="analytics-data-point cursor-drag" @click="selectedSpace(space)" style="display: flex;">
                        <div class="data-points-drag-block">
                            <div class="data-points-checkbox">
                                <img src="~assets/drag-blue.svg" />
                                <div class="label-txt-black pL10 pR10">
                                    {{ space.name }}
                                </div>
                            </div>
                        </div>
                    </div> -->
            </el-collapse-item>
          </template>
        </el-collapse>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['visibility', 'element', 'floorPlan', 'mappedSpaceId'],
  data() {
    return {
      filterVariables: '',
      spaceList: [],
      activeSelections: [''],
      spaceTreeList: {},
      selectedSpace: {
        id: -1,
      },
    }
  },
  computed: {
    filteredSpaceList() {
      let data = this.spaceList.filter(rt => {
        if (!this.mappedSpaceId.find(rl => rl === rt.id)) {
          return rt
        }
      })
      return data
    },
    filteredVariables() {
      let self = this
      if (this.filterVariables && this.filterVariables.trim()) {
        let params = this.spaceList
          .filter(
            rl =>
              rl.name
                .toLowerCase()
                .indexOf(self.filterVariables.toLowerCase()) >= 0
          )
          .filter(rx => {
            if (this.mappedSpaceId.findIndex(rv => rv === rx.id) === -1) {
              return rx
            }
          })
        let data = this.getspaceTreeList(params)
        return data
      }
      return this.spaceTreeList
    },
    spaceCategories() {
      return this.$store.getters.getSpaceCategoryPickList()
    },
    floorId() {
      if (!isEmpty(this.floorPlan) && this.floorPlan.floorId) {
        return Number(this.floorPlan.floorId)
      }
      return null
    },
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
  },
  mounted() {
    this.loadFloorSpaces()
  },
  watch: {
    floorPlan: function(newVal, oldVal) {
      this.loadFloorSpaces()
    },
    deep: true,
  },
  methods: {
    selectSpace(space) {
      this.selectedSpace = space
      this.$emit('update:visibility', false)
      this.$emit('save', this.selectedSpace)
    },
    closeDialog() {
      this.$emit('update:visibility', false)
      this.$emit('close', false)
    },
    loadFloorSpaces() {
      if (this.floorId) {
        this.$util
          .loadSpacesContext(4, null, [
            {
              key: 'floor',
              operator: 'is',
              value: this.floorId,
            },
          ])
          .then(response => {
            this.spaceList = response.records
            let data = this.spaceList.filter(rt => {
              if (!this.mappedSpaceId.find(rl => rl === rt.id)) {
                return rt
              }
            })
            this.spaceTreeList = this.getspaceTreeList(data)
          })
      }
    },
    getspaceTreeList(spaceList) {
      if (!isEmpty(this.spaceCategories)) {
        let treeObject = {}
        Object.keys(this.spaceCategories).forEach((rt, index) => {
          let selectedSpaceList = spaceList.filter(rl => {
            if (rl.spaceCategory && rl.spaceCategory.id === Number(rt)) {
              return rl
            }
          })
          this.$set(treeObject, this.spaceCategories[rt], selectedSpaceList)
        })
        let selectedSpaceList = spaceList.filter(rl => {
          if (rl.spaceCategory === null) {
            return rl
          }
        })
        this.$set(treeObject, 'Default', selectedSpaceList)
        return treeObject
      } else {
        return {
          All: spaceList,
        }
      }
    },
  },
}
</script>

<style>
.fp-sm-container {
  height: calc(100vh - 60px);
  overflow: auto;
}
</style>
