<template>
  <div class="height100 scrollable">
    <table
      class="setting-list-view-table mB10"
      style="width: 100%"
      ref="readings-container"
    >
      <thead>
        <tr v-if="list && list.length > 0">
          <th width="10%" class="setting-table-th setting-th-text">Date</th>
          <th width="20%" class="setting-table-th setting-th-text">From</th>
          <th width="10%" class="setting-table-th setting-th-text"></th>
          <th width="10%" class="setting-table-th setting-th-text"></th>
          <th width="10%" class="setting-table-th setting-th-text"></th>
          <th width="20%" class="setting-table-th setting-th-text">To</th>
          <th width="10%" class="setting-table-th setting-th-text">
            Requested By
          </th>
          <th width="10%" class="setting-table-th setting-th-text"></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="loading" class="nodata">
          <td colspan="100%" class="text-center p30imp">
            <spinner :show="loading"></spinner>
          </td>
        </tr>
        <tr v-else-if="!list || list.length === 0" class="nodata">
          <td colspan="100%" class="text-center p30imp">
            <div class="mT40">
              <InlineSvg
                src="svgs/emptystate/readings-empty"
                iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
              ></InlineSvg>
              <div class="pT20 fc-black-dark f18 bold">
                No Movements available here!
              </div>
            </div>
          </td>
        </tr>
        <tr
          class="tablerow"
          v-else
          v-for="(move, index) in list"
          :key="index"
          v-bind:class="{
            requested:
              index === 0 &&
              move.moduleState.typeCode != 5 &&
              move.moduleState.typeCode != 2,
          }"
        >
          <td style="white-space:nowrap;">
            <div class="role-name ">{{ formatDay(move.sysCreatedTime) }}</div>
            <div
              style="width:100%;white-space:nowrap;"
              class="el-timeline-item__timestamp is-bottom"
            >
              {{ formatTime(move.sysCreatedTime) }}
            </div>
          </td>
          <td width="20%">
            <div style="width:100%;white-space:nowrap;" class="text-ellipsis">
              {{
                move.fromSite > 0
                  ? $store.getters.getSite(move.fromSite).name
                  : '---'
              }}
            </div>
            <div
              style="width:100%;white-space:nowrap;"
              class="el-timeline-item__timestamp is-bottom"
            >
              {{
                move.fromSpace > 0
                  ? $store.getters.getSpace(move.fromSpace).name
                  : '---'
              }}
            </div>
            <div
              v-if="move.fromGeoLocation"
              style="width:100%;white-space:nowrap;"
              class="el-timeline-item__timestamp is-bottom"
            >
              {{ move.fromGeoLocation }}
            </div>
          </td>
          <td style="white-space:nowrap;padding : 0px" width="10%">
            <div class="line"></div>
          </td>
          <td style="white-space:nowrap;padding : 0px;" width="10%">
            <div
              style="text-align:center; margin: auto;"
              class="fc-asset-circle"
            >
              &#8594;
            </div>
          </td>
          <td style="white-space:nowrap; padding : 0px" width="10%">
            <div class="line"></div>
          </td>

          <td width="20%">
            <div style="width:100%;white-space:nowrap;" class="text-ellipsis">
              {{
                move.toSite > 0
                  ? $store.getters.getSite(move.toSite).name
                  : '---'
              }}
            </div>
            <div
              style="width:100%;white-space:nowrap;"
              class="el-timeline-item__timestamp is-bottom"
            >
              {{
                move.toSpace > 0
                  ? $store.getters.getSpace(move.toSpace).name
                  : '---'
              }}
            </div>
            <div
              v-if="move.toGeoLocation"
              style="width:100%;white-space:nowrap;"
              class="el-timeline-item__timestamp is-bottom"
            >
              {{ move.toGeoLocation }}
            </div>
          </td>
          <td width="10%">
            <div class="flLeft f12 secondary-color comment-by comment-by2">
              <user-avatar
                size="md"
                :user="move.requestedBy"
                :id="move.requestedBy.id"
              ></user-avatar>
            </div>
          </td>
          <td style="white-space:nowrap;" width="10%">
            <div
              v-if="move.moduleState.typeCode == 5"
              style="width:100%"
              class="fc-badge-critical text-uppercase inline vertical-middle mL15 text-ellipsis"
            >
              {{
                $store.getters.getTicketStatus(
                  move.moduleState.id,
                  'assetmovement'
                ).displayName
              }}
            </div>
            <div
              v-else-if="move.moduleState.typeCode == 2"
              style="width:100%"
              class="fc-badge-approved text-uppercase inline vertical-middle mL15 text-ellipsis"
            >
              {{
                $store.getters.getTicketStatus(
                  move.moduleState.id,
                  'assetmovement'
                ).displayName
              }}
            </div>
            <div
              v-else
              style="width:100%"
              class="fc-badge-major text-uppercase inline vertical-middle mL15 text-ellipsis"
            >
              {{
                $store.getters.getTicketStatus(
                  move.moduleState.id,
                  'assetmovement'
                ).displayName
              }}
            </div>
          </td>

          <td style="white-space:nowrap;">
            <a
              v-if="availableStates(move) && availableStates(move)[0]"
              @click="updateState(availableStates(move)[0], move)"
              class="pT10 f13 pL15 pR15 text-fc-green"
              >{{ availableStates(move)[0].name }}</a
            >
            <a
              v-if="availableStates(move) && availableStates(move)[1]"
              @click="updateState(availableStates(move)[1], move)"
              class="pT10 f13 pL15 pR15 text-coral"
              >{{ availableStates(move)[1].name }}</a
            >
          </td>
        </tr>
      </tbody>
    </table>
    <!-- portal for pagination and search -->
    <div class="widget-topbar-actions">
      <portal-target :name="widget.key + '-topbar'"></portal-target>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import UserAvatar from '@/avatar/User'
import moment from 'moment-timezone'
export default {
  props: [
    'details',
    'layoutParams',
    'hideTitleSection',
    'sectionKey',
    'widget',
    'resizeWidget',
    'eventBus',
  ],
  components: {
    UserAvatar,
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'assetmovement')
  },
  mounted() {
    this.loadAssetMovements()
  },
  data() {
    return {
      list: [],
      loading: true,
      stateflows: [],
    }
  },
  computed: {
    ...mapState({
      users: state => state.users,
    }),
  },
  methods: {
    getUserName(id) {
      let userObj = this.users.find(user => user.id == id)
      return userObj ? userObj.name : '--'
    },
    formatTime(date) {
      return moment(date)
        .tz(this.$timezone)
        .format('hh:mm a')
    },
    formatDay(date) {
      return this.$options.filters.formatDate(date, true, false)
    },
    updateState(transition, assetMovement) {
      let self = this
      this.$http
        .post(
          '/v2/assetMovement/update?' + 'stateTransitionId=' + transition.id,
          { assetMovement }
        )
        .then(response => {
          if (response.data.responseCode === 0) {
            self.$message.success('Asset movement state updated successfully.')
            self.loadAssetMovements()
            self.showAssetMoveDialog = false
            if (
              response.data.result.assetmovementrecords.moduleState.typeCode ==
                2 ||
              response.data.result.assetmovementrecords.moduleState.typeCode ==
                5
            ) {
              this.eventBus.$emit('stateflows', [])
            }
            if (
              response.data.result.assetmovementrecords.moduleState.typeCode ==
              2
            ) {
              this.eventBus.$emit('currentSpaceId', assetMovement.toSpace)
            } else {
              this.eventBus.$emit('currentSpaceId')
            }
          } else {
            self.$message.error(response.data.message)
          }
        })
    },
    availableStates(move) {
      let states = this.stateflows[move.stateFlowId + '_' + move.moduleState.id]
      if (states) {
        return [...states].sort((a, b) => a.buttonType - b.buttonType)
      }
      return null
    },
    loadAssetMovements() {
      this.loading = true
      let self = this
      this.$http
        .get(
          '/v2/assetMovement/list?filters=' +
            encodeURIComponent(
              JSON.stringify({
                assetId: [{ operatorId: 36, value: [self.details.id + ''] }],
              })
            )
        )
        .then(function(response) {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.assetmovementrecords
          ) {
            self.list = response.data.result.assetmovementrecords
            self.stateflows = response.data.result.stateFlows
            for (let i = 0; i < self.list.length; i++) {
              self.list[i].canCurrentUserApprove = true
            }
          }
        })
      this.loading = false
    },
  },
}
</script>
<style scoped>
.line {
  width: 70px;
  height: 1px;
  border-top: 2px dashed #f5f5f5;
}
.arrow {
  width: 35px;
  height: 35px;
  box-shadow: 0 4px 13px 0 rgba(21, 17, 45, 0.08);
  background-color: #ffffff;
}
.requested {
  background: #ff0000;
  border-radius: 25px;
  padding: 20px;
  width: 200px;
  height: 150px;
}
.fc-asset-circle {
  top: 110px;
  width: 25px;
  height: 25px;
  background-color: #f5f5f5;
  box-shadow: 0 4px 13px 0 #f7f9f9;
  border-radius: 50%;
  border-color: #f7f9f9;
  line-height: 25px;
}
</style>
