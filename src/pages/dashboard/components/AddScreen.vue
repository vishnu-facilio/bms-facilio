<template>
  <f-popover
    placement="left"
    v-model="visibility"
    popper-class="connect-remote-screen"
    title="Add Screen"
    trigger="click"
    width="300"
    @save="addScreen"
    @close="closeNewDialog"
    @hide="closeNewDialog"
    confirmTitle="Add"
  >
    <template slot="content">
      <el-form
        :model="screen"
        class="pT20"
        ref="addScreen"
        :label-position="'top'"
      >
        <el-form-item prop="name">
          <p class="grey-text2">{{ $t('panel.remote.name') }}</p>
          <el-input
            :autofocus="true"
            v-model="screen.name"
            placeholder="Screen name"
            class="fc-input-full-border2"
          ></el-input>
        </el-form-item>
        <el-form-item prop="screenDashboards" v-if="dashboards">
          <p class="grey-text2">{{ $t('panel.remote.dashboard') }}</p>
          <el-select
            v-model="screen.screenDashboards"
            multiple
            filterable
            placeholder="Select Dashboards"
            style="width: 100%"
            class="fc-input-full-border2"
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
                :value="
                  dashboard.linkName.indexOf('buildingdashboard') !== -1 ||
                  dashboard.linkName.indexOf('sitedashboard') !== -1
                    ? dashboard.id + '_' + dashboard.baseSpaceId
                    : dashboard.id + ''
                "
              >
              </el-option>
            </el-option-group>
          </el-select>
        </el-form-item>
        <el-form-item prop="interval">
          <p class="grey-text2">{{ $t('panel.remote.interval') }}</p>
          <el-select
            v-model="screen.interval"
            :allow-create="true"
            :filterable="true"
            placeholder="Select Interval"
            style="width: 80px;"
            class="fc-input-full-border2"
          >
            <el-option
              v-for="(ival, idx) in defaultIntervals"
              :key="idx"
              :label="ival"
              :value="ival"
            ></el-option>
          </el-select>
          {{ $t('panel.remote.minutes') }}
        </el-form-item>
        <el-form-item prop="siteId">
          <p class="grey-text2">{{ $t('panel.remote.scope') }}</p>
          <el-select
            v-model="screen.siteId"
            :allow-create="true"
            :filterable="true"
            placeholder="Select Interval"
            style="width: 100%"
            class="fc-input-full-border2"
          >
            <el-option
              v-for="(site, idx) in sites"
              :key="idx"
              :label="site.name"
              :value="site.id"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </template>
    <template slot="reference"><slot name="reference"></slot></template>
  </f-popover>
</template>

<script>
import { mapState } from 'vuex'
import FPopover from '@/FPopover'
export default {
  props: ['dashboards', 'defaultIntervals'],
  components: {
    FPopover,
  },
  data() {
    return {
      visibility: false,
      screen: {
        name: null,
        screenDashboards: [],
        interval: 5,
        siteId: null,
      },
    }
  },
  computed: {
    ...mapState({
      sites: state => state.site,
    }),
  },
  methods: {
    addScreen() {
      let self = this

      let screenDashboardList = []

      let index = 1
      for (let d of this.screen.screenDashboards) {
        let dbId = parseInt(d.split('_')[0])
        let spaceId = null
        if (d.split('_').length > 1) {
          spaceId = parseInt(d.split('_')[1])
        }

        screenDashboardList.push({
          dashboardId: dbId,
          spaceId: spaceId,
          sequence: index,
        })
        index = index + 1
      }

      let data = {
        screenContext: {
          name: this.screen.name,
          screenDashboards: screenDashboardList,
          interval: this.screen.interval * 60,
          siteId: this.screen.siteId,
        },
      }

      self.$http
        .post('/screen/addScreen', data)
        .then(function(response) {
          if (response.data.screenContext) {
            self.screen = {
              name: null,
              screenDashboards: [],
              interval: 5,
              siteId: null,
            }
            self.$emit('save', response.data.screenContext)
          }
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    closeNewDialog() {
      this.screen = {
        name: null,
        screenDashboards: [],
        interval: 5,
      }
      this.$emit('close')
    },
  },
}
</script>

<style></style>
