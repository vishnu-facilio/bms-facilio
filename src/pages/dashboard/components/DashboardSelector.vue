<template>
  <el-popover :width="350">
    <div class="dashboard-selector">
      <div class="dashboard-collection">
        <div v-if="!screen.screenDashboards || !screen.screenDashboards.length">
          {{ $t('panel.remote.no_add') }}
        </div>
        <draggable
          class="list-group"
          element="ul"
          v-model="screen.screenDashboards"
        >
          <transition-group type="transition" :name="'flip-list'">
            <li
              class="list-group-item"
              v-for="(screenDashboard, index) in screen.screenDashboards"
              :key="screenDashboard.dashboardId"
            >
              <span class="float: left;">{{
                screenDashboard.dashboard.dashboardName
              }}</span>
              <i
                class="el-icon-delete pointer"
                @click="removeScreenDashboard(screenDashboard, index)"
                title="Remove"
                data-arrow="true"
                v-tippy
                style="float: right;"
              ></i>
              <span style="clear: both;"></span>
            </li>
          </transition-group>
        </draggable>
      </div>
      <div class="add-dashboard">
        <div style="font-weight: 500;">{{ $t('panel.remote.add_dash') }}:</div>
        <el-select
          v-model="addDashboard"
          placeholder="Select Dashboard"
          :filterable="true"
          v-if="dashboards"
        >
          <el-option-group
            v-for="(dfolder, index) in dashboards"
            :key="index"
            :label="dfolder.name"
          >
            <el-option
              v-for="(dashboard, didx) in dfolder.dashboards"
              :key="didx"
              :label="dashboard.dashboardName"
              :value="dashboard.linkName"
            >
            </el-option>
          </el-option-group>
        </el-select>
        <el-button
          type="primary"
          @click="addScreenDashboard"
          class="setup-el-btn"
          style="margin-left: 10px;"
          size="mini"
          >{{ $t('panel.remote.ad') }}</el-button
        >
      </div>
    </div>
    <template slot="reference"><slot name="reference"></slot></template>
  </el-popover>
</template>

<script>
import draggable from 'vuedraggable'
export default {
  props: ['screen', 'dashboards'],
  components: {
    draggable,
  },
  data() {
    return {
      addDashboard: null,
    }
  },
  watch: {
    'screen.screenDashboards': function() {
      this.$emit('update')
    },
  },
  methods: {
    addScreenDashboard() {
      if (this.addDashboard) {
        let db = null
        for (let dfolder of this.dashboards) {
          db = dfolder.dashboards
            ? dfolder.dashboards.find(d => d.linkName === this.addDashboard)
            : null
          if (db) {
            break
          }
        }
        if (db) {
          let exists = this.screen.screenDashboards.find(
            sd => sd.dashboardId === db.id && sd.spaceId === db.baseSpaceId
          )
          if (!exists) {
            this.screen.screenDashboards.push({
              dashboard: db,
              dashboardId: db.id,
              id: null,
              screenId: this.screen.id,
              sequence: 0,
              spaceId: db.baseSpaceId,
            })
          }
        }
        this.addDashboard = null
      }
    },
    removeScreenDashboard(db, index) {
      this.screen.screenDashboards.splice(index, 1)
    },
  },
}
</script>

<style>
.dashboard-collection {
  max-height: 310px;
  overflow: scroll;
}
</style>
