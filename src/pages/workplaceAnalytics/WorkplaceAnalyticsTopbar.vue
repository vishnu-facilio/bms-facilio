<template>
  <div>
    <div
      class="setting-header3 position-relative fp-header indoor-fp-topbar p10"
    >
      <div class="wp-back-icon pointer">
        <i class="el-icon-back" @click="goBack()"></i>
      </div>
      <div
        class="setting-title-block fp-chooser-new"
        @click="showBuildingFilter"
      >
        <div
          class="fc-black3-16 fw4 f15 line-height20"
          v-if="buildingId !== null"
        >
          <i class="el-icon-office-building f18 mR10"></i>
          <span v-if="building">{{ `${building.name}` }}</span>
          <span v-else>{{ `Building` }}</span>
          <span v-if="floor !== null">
            <i
              class="el-icon-arrow-right fc-grey-text12-light f18 pR5 pL5 vertical-bottom bold"
            ></i>
          </span>
          <span v-if="floor !== null">{{ `${floor.name}` }}</span>
        </div>
        <div class="fc-black3-16 fw4 f15 line-height20" v-else>
          <i class="el-icon-office-building f18 mR10"></i>
          <span>{{ `Workplace` }}</span>
        </div>
      </div>

      <!-- <div class="action-btn setting-page-btn flex mT5 wp-more-iocn">
        <el-dropdown
          class="pointer fc-actions-floor"
          @command="actions"
          trigger="click"
        >
          <span class="el-dropdown-link">
            <i class="el-icon-more rotate-90 pointer"></i>
          </span>
          <el-dropdown-menu slot="dropdown" class="dashboard-subheader-dp">
            <el-dropdown-item command="edit">
              <div>
                {{ $t('common._common.edit') }}
              </div>
            </el-dropdown-item>
            <el-dropdown-item command="new">
              <div>
                {{ 'Add Floor Plan' }}
              </div>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div> -->
    </div>
    <FloorSwicther
      v-if="filterDialogOpen"
      :visibility.sync="filterDialogOpen"
      :floorId="floorId"
      :buildingId="buildingId"
    ></FloorSwicther>
  </div>
</template>

<script>
import FloorSwicther from 'src/pages/workplaceAnalytics/WorkplaceAnalysisFloorSwitcher.vue'

export default {
  components: { FloorSwicther },
  props: {
    building: {
      type: Object,
      required: false,
      default: null,
    },
    floor: {
      type: Object,
      required: false,
      default: null,
    },
  },
  data() {
    return {
      filterDialogOpen: false,
    }
  },
  computed: {
    buildingId() {
      return this.building?.id ? this.building.id : null
    },
    floorId() {
      return this.floor?.id ? this.floor.id : null
    },
  },
  methods: {
    goBack() {
      this.$emit('goback')
    },
    showBuildingFilter() {
      this.filterDialogOpen = true
    },
    actions(name) {
      if (name === 'edit') {
        //action edit
      } else if (name === 'new') {
        //action new
      }
    },
  },
}
</script>

<style>
.indoor-fp-topbar {
  padding: 10px 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
</style>
<style lang="scss" scopped>
.fp-chooser-new {
  height: 34px;
  margin-top: 5px;
  padding: 5px 10px;
  border: 1px solid #efefef;
  border-radius: 5px;
  cursor: pointer;
  display: flex;
  align-items: center;

  &:hover {
    background: rgb(57 179 194 / 10%);
    transition: 0.6s all;
  }
}
.fc-actions-floor {
  color: #605e88;
  font-size: 18px;
  margin-left: 10px;
  padding: 6px 6px 6px;
  border-radius: 3px;
  border: 1px solid #efefef;
}
.wp-more-iocn {
  position: absolute;
  right: 20px;
}
.wp-back-icon {
  padding: 11px;
  font-size: 18px;
  font-weight: 500;
  top: 5px;
}
</style>
