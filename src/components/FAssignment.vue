<template>
  <el-popover
    @show="loadrecommendedUsers()"
    ref="assignedtopopover"
    :popper-class="'fc-popover-p02 fc-assignment-popover-height'"
    placement="bottom"
    width="320"
    trigger="click"
    class="assignment-popover groupAssignment"
    v-if="showRecommendedUsers && viewtype === 'view'"
  >
    <div class="row popover-active-block" style="background-color: #f1f3f5;">
      <div
        class="col-12"
        style="padding-left: 25px; padding-right: 25px; padding-top: 10px; width: 100%;"
      >
        <span
          data-test-selector="Team"
          class="col-6 pull-left"
          @click="assignment('assignmentGroup')"
          v-bind:class="{ active: assignmentValue === 'assignmentGroup' }"
          style="width: 46%;"
        >
          <div class="group-label">{{ $t('common.wo_report.team') }}</div>
          <div v-if="selectedGroup" class="group-sublabel pointer">
            <span @click="reassignWO('assignmentGroup')">
              <img
                class="svg-icon remove-icon"
                src="~assets/blocked-sign.svg"
              />
            </span>
            <span class="pL5">{{ selectedGroup }}</span>
          </div>
          <div v-else class="group-sublabel pointer">
            {{ $t('common.header.unassigned') }}
          </div>
        </span>
        <span
          data-test-selector="Staff"
          class="col-6 pull-right"
          @click="assignment('assignedTo')"
          v-bind:class="{
            active:
              assignmentValue === 'assignedTo' ||
              assignmentValue === 'assignedToGroupMember',
          }"
          style="width: 46%;"
        >
          <div class="group-label">{{ $t('common.wo_report.staff') }}</div>
          <div v-if="selectedName" class="group-sublabel pointer">
            <span @click="reassignWO('assignedTo')">
              <img
                class="svg-icon remove-icon"
                src="~assets/blocked-sign.svg"
              />
            </span>
            <span class="pL5">{{ selectedName }}</span>
          </div>
          <div v-else class="group-sublabel">
            {{ $t('common.header.unassigned') }}
          </div>
        </span>
      </div>
    </div>
    <div class="fc-search-sticky">
      <f-search
        v-if="assignmentValue === 'assignedTo'"
        boolval="true"
        class="mL20"
        v-model="userList"
        key="assignedTo"
      ></f-search>
      <f-search
        v-if="assignmentValue === 'assignedToGroupMember'"
        boolval="true"
        class="mL20"
        v-model="memberList"
        key="assignedToGroupMember"
      ></f-search>
      <f-search
        v-if="assignmentValue === 'assignmentGroup'"
        boolval="true"
        class="mL20"
        v-model="groupList"
        key="assignmentGroup"
      ></f-search>
    </div>
    <div
      class="scroll fc-assignment-scroll-popup"
      style="min-width: 150px; min-height: 320px;"
      v-if="assignmentValue === 'assignedTo'"
    >
      <div v-if="recommendedUsersList.length && moduleName === 'workorder'">
        <div class="label-txt-black pL20 pT10 bold">
          {{ $t('common._common.recommended_users') }}
        </div>
        <div
          v-for="(recommendedUser, reusridx) in recommendedUsersList"
          @click="assignViewWorkOrder(recommendedUser)"
          :key="reusridx"
        >
          <div class="group-item-label pointer">
            {{ recommendedUser.name }} (
            {{ userVsTicketCountObj[recommendedUser.id] }} Tickets)
          </div>
        </div>
      </div>
      <div
        class="label-txt-black pL20 pT10 bold"
        v-if="moduleName === 'workorder' && recommendedUsersList.length"
      >
        {{ $t('common._common.others') }}
      </div>
      <div
        v-for="(user, usridx) in userList"
        @click="assignViewWorkOrder(user)"
        :key="usridx"
      >
        <div class="group-item-label">
          <span>{{ user.name }}</span>
        </div>
      </div>
    </div>
    <div
      class="scroll fc-assignment-scroll-popup"
      style="min-width: 150px; min-height: 320px; padding-left: 10px;"
      v-if="assignmentValue === 'assignedToGroupMember'"
    >
      <div
        v-for="(user, usridx) in memberList"
        @click="assignViewWorkOrder(user)"
        :key="usridx"
      >
        <div class="group-item-label">
          <span>{{ user.name }}</span>
        </div>
      </div>
    </div>
    <div
      link
      class="scroll fc-assignment-scroll-popup"
      style="min-width: 150px; min-height: 320px;"
      v-if="assignmentValue === 'assignmentGroup'"
    >
      <div
        v-for="(group, grpidx) in groupList"
        @click="assignViewWorkOrder(null, group, grpidx)"
        :key="grpidx"
        v-if="group.active"
      >
        <div class="group-item-label">
          <span>{{ group.name }}</span>
          <span style="color:#a5a5a5" class="pL5">
            ({{ memberCount(group) }})
          </span>
          <i
            class="q-icon material-icons"
            style="float: right;margin-right: 20px;color: #a5a5a5;"
            >keyboard_arrow_right</i
          >
        </div>
      </div>
    </div>
    <span slot="reference">
      <slot name="showFAssignmentPopover"></slot>
    </span>
  </el-popover>
  <q-popover
    ref="assignedtopopover"
    class="assignment-popover groupAssignment"
    v-else-if="viewtype === 'form'"
  >
    <div class="row" style="background-color: #f1f3f5;">
      <div
        class="col-12"
        style="padding-left: 25px; padding-right: 25px; padding-top: 20px; width: 100%;"
      >
        <span
          data-test-selector="Team"
          class="col-6 pull-left"
          @click="assignment('assignmentGroup')"
          v-bind:class="{ active: assignmentValue === 'assignmentGroup' }"
          style="width: 46%;"
        >
          <div class="group-label">{{ $t('common.wo_report.team') }}</div>
          <div v-if="selectedGroup" class="group-sublabel">
            <span @click="reassign('assignmentGroup')">
              <img
                class="svg-icon remove-icon"
                src="~assets/blocked-sign.svg"
              />
            </span>
            <span class="p5">{{ selectedGroup }}</span>
          </div>
          <div v-else class="group-sublabel">
            {{ $t('common.header.unassigned') }}
          </div>
        </span>
        <span
          data-test-selector="Staff"
          class="col-6 pull-right"
          @click="assignment('assignedTo')"
          v-bind:class="{
            active:
              assignmentValue === 'assignedTo' ||
              assignmentValue === 'assignedToGroupMember',
          }"
          style="width: 46%;"
        >
          <div class="group-label">{{ $t('common.wo_report.staff') }}</div>
          <div v-if="selectedName" class="group-sublabel">
            <span @click="reassign('assignedTo')">
              <img
                class="svg-icon remove-icon"
                src="~assets/blocked-sign.svg"
              />
            </span>
            <span class="p5">{{ selectedName }}</span>
          </div>
          <div v-else class="group-sublabel">
            {{ $t('common.header.unassigned') }}
          </div>
        </span>
      </div>
    </div>
    <div>
      <f-search
        v-if="assignmentValue === 'assignedTo'"
        boolval="true"
        class="mL20"
        v-model="userList"
        key="assignedTo"
      ></f-search>
      <f-search
        v-if="assignmentValue === 'assignedToGroupMember'"
        boolval="true"
        class="mL20"
        v-model="memberList"
        key="assignedToGroupMember"
      ></f-search>
      <f-search
        v-if="assignmentValue === 'assignmentGroup'"
        boolval="true"
        class="mL20"
        v-model="groupList"
        key="assignmentGroup"
      ></f-search>
    </div>
    <q-list
      link
      class="scroll"
      style="min-width: 150px; min-height: 200px; padding-left: 10px;"
      v-if="assignmentValue === 'assignedTo'"
    >
      <q-item
        v-for="(user, usridx) in userList"
        @click="assignWorkOrder(user, usridx, assignmentValue)"
        :key="usridx"
        :data-test-selector="`${user.name}`"
      >
        <q-item-main class="group-item-label">
          <span>{{ user.name }}</span>
        </q-item-main>
      </q-item>
    </q-list>
    <q-list
      link
      class="scroll"
      style="min-width: 150px; min-height: 200px; padding-left: 10px;"
      v-if="assignmentValue === 'assignedToGroupMember'"
    >
      <q-item
        v-for="(user, usridx) in memberList"
        @click="assignWorkOrder(user, usridx, assignmentValue)"
        :key="usridx"
        :data-test-selector="`${user.name}`"
      >
        <q-item-main class="group-item-label">
          <span>{{ user.name }}</span>
        </q-item-main>
      </q-item>
    </q-list>
    <q-list
      link
      class="scroll"
      style="min-width: 150px; min-height: 200px; padding-left: 10px;"
      v-if="assignmentValue === 'assignmentGroup'"
    >
      <q-item
        v-for="(group, grpidx) in groupList"
        @click="assignWorkOrder(group, grpidx, assignmentValue)"
        :key="grpidx"
        v-if="group.active"
        :data-test-selector="`${group.name}`"
      >
        <q-item-main class="group-item-label">
          <span>{{ group.name }}</span>
          <span style="color:#a5a5a5"> ({{ memberCount(group) }}) </span>
          <i
            class="q-icon material-icons"
            style="float: right;margin-right: 20px;color: #a5a5a5;"
            >keyboard_arrow_right</i
          >
        </q-item-main>
      </q-item>
    </q-list>
  </q-popover>
  <q-popover
    ref="assignedtopopover"
    class="assignment-popover groupAssignment"
    @open="isPopverVisible = true"
    @close="isPopverVisible = false"
    v-else-if="viewtype === 'view'"
  >
    <template v-if="isPopverVisible">
      <div class="row" style="background-color: #f1f3f5;">
        <div
          class="col-12"
          style="padding-left: 25px; padding-right: 25px; padding-top: 20px; width: 100%;"
        >
          <span
            data-test-selector="Team"
            class="col-6 pull-left"
            @click="assignment('assignmentGroup')"
            v-bind:class="{ active: assignmentValue === 'assignmentGroup' }"
            style="width: 46%;"
          >
            <div class="group-label">{{ $t('common.wo_report.team') }}</div>
            <div v-if="selectedGroup" class="group-sublabel">
              <span @click="reassignWO('assignmentGroup')">
                <img
                  class="svg-icon remove-icon"
                  src="~assets/blocked-sign.svg"
                />
              </span>
              <span>{{ selectedGroup }}</span>
            </div>
            <div v-else class="group-sublabel">
              {{ $t('common.header.unassigned') }}
            </div>
          </span>
          <span
            data-test-selector="Staff"
            class="col-6 pull-right"
            @click="assignment('assignedTo')"
            v-bind:class="{
              active:
                assignmentValue === 'assignedTo' ||
                assignmentValue === 'assignedToGroupMember',
            }"
            style="width: 46%;"
          >
            <div class="group-label">{{ $t('common.wo_report.staff') }}</div>
            <div v-if="selectedName" class="group-sublabel">
              <span @click="reassignWO('assignedTo')">
                <img
                  class="svg-icon remove-icon"
                  src="~assets/blocked-sign.svg"
                />
              </span>
              <span>{{ selectedName }}</span>
            </div>
            <div v-else class="group-sublabel">
              {{ $t('common.header.unassigned') }}
            </div>
          </span>
        </div>
      </div>
      <div>
        <f-search
          v-if="assignmentValue === 'assignedTo'"
          boolval="true"
          class="mL20"
          v-model="userList"
          key="assignedTo"
        ></f-search>
        <f-search
          v-if="assignmentValue === 'assignedToGroupMember'"
          boolval="true"
          class="mL20"
          v-model="memberList"
          key="assignedToGroupMember"
        ></f-search>
        <f-search
          v-if="assignmentValue === 'assignmentGroup'"
          boolval="true"
          class="mL20"
          v-model="groupList"
          key="assignmentGroup"
        ></f-search>
      </div>
      <q-list
        link
        class="scroll"
        style="min-width: 150px; min-height: 200px; padding-left: 10px;"
        v-if="assignmentValue === 'assignedTo'"
      >
        <q-item
          v-for="(user, usridx) in userList"
          @click="assignViewWorkOrder(user)"
          :key="usridx"
          :data-test-selector="`${user.name}`"
        >
          <q-item-main class="group-item-label">
            <span>{{ user.name }}</span>
          </q-item-main>
        </q-item>
      </q-list>
      <q-list
        link
        class="scroll"
        style="min-width: 150px; min-height: 200px; padding-left: 10px;"
        v-if="assignmentValue === 'assignedToGroupMember'"
      >
        <q-item
          v-for="(user, usridx) in memberList"
          @click="assignViewWorkOrder(user)"
          :key="usridx"
          :data-test-selector="`${user.name}`"
        >
          <q-item-main class="group-item-label">
            <span>{{ user.name }}</span>
          </q-item-main>
        </q-item>
      </q-list>
      <q-list
        link
        class="scroll"
        style="min-width: 150px; min-height: 200px; padding-left: 10px;"
        v-if="assignmentValue === 'assignmentGroup'"
      >
        <q-item
          v-for="(group, grpidx) in groupList"
          @click="assignViewWorkOrder(null, group, grpidx)"
          :key="grpidx"
          v-if="group.active"
          :data-test-selector="`${group.name}`"
        >
          <q-item-main class="group-item-label">
            <span>{{ group.name }}</span>
            <span style="color:#a5a5a5"> ({{ memberCount(group) }}) </span>
            <i
              class="q-icon material-icons"
              style="float: right;margin-right: 20px;color: #a5a5a5;"
              >keyboard_arrow_right</i
            >
          </q-item-main>
        </q-item>
      </q-list>
    </template>
  </q-popover>
</template>
<script>
import { QPopover, QItem, QItemMain, QList } from 'quasar'
import { mapGetters } from 'vuex'
import FSearch from '@/FSearch'
import { isEmpty } from '@facilio/utils/validation'
import isEqual from 'lodash/isEqual'
import { API } from '@facilio/api'
export default {
  props: {
    model: {},
    viewtype: {},
    record: {},
    form: {},
    siteId: { default: null },
    recordId: {},
    moduleName: {},
    showRecommendedUsers: {},
  },
  data() {
    return {
      showQuickSearch: false,
      loading: false,
      quickSearchQuery: null,
      assignmentValue: 'assignmentGroup',
      selectedName: '',
      selectedGroup: '',
      selctedGroupMember: '',
      userVsTicketCountObj: {},
      memberList: [],
      typeVal: this.viewtype,
      workorder: null,
      actions: {
        assign: {
          loading: false,
        },
      },
      userList: [],
      recommendedUsersList: [],
      groupList: [],
      filtereduserList: [],
      filteredgroupList: [],
      filteredmemberList: [],
      isPopverVisible: false,
    }
  },
  components: {
    QPopover,
    QItem,
    QItemMain,
    QList,
    FSearch,
  },
  watch: {
    record: function() {
      this.loadWorkorders()
    },
    siteId: {
      handler(newVal, oldVal) {
        let { getCurrentSiteId, viewtype } = this
        if (
          isEqual(newVal, oldVal) ||
          isEqual(newVal, getCurrentSiteId) ||
          isEqual(viewtype, 'view')
        ) {
          return
        }
        this.scopeTeamUser()
      },
      immediate: true,
    },
    isPopverVisible: function() {
      if (
        this.isPopverVisible &&
        this.viewtype === 'view' &&
        isEmpty(this.userList) &&
        isEmpty(this.groupList)
      ) {
        this.scopeTeamUser()
      }
    },
    model: {
      handler(newVal, oldVal) {
        if (!isEqual(newVal, oldVal)) {
          let { assignedTo, assignmentGroup } = newVal
          let { id: assignedToId } = assignedTo || {}
          let { id: assignmentGroupId } = assignmentGroup || {}
          if (isEmpty(assignedToId)) {
            this.selectedName = null
          }
          if (isEmpty(assignmentGroupId)) {
            this.selectedGroup = null
          }
        }
      },
    },
  },
  computed: {
    ...mapGetters([
      'getTicketStatusByLabel',
      'getCurrentSiteId',
      'getUser',
      'getGroup',
    ]),
  },
  created() {
    this.$store.dispatch('loadTicketStatus', 'workorder').then(() => {
      this.loadWorkorders()
    })
  },
  methods: {
    memberCount(group) {
      let { members } = group || {}
      return (members || []).length
    },
    loadrecommendedUsers() {
      this.loading = true
      this.userVsTicketCountObj = {}
      this.recommendedUsersList = []
      let url = `v2/recommended/workorder?woId=${this.record.id}`
      this.$http
        .get(url)
        .then(response => {
          if (response.data.responseCode === 0) {
            if (response.data.result && response.data.result.recommendedUsers) {
              response.data.result.recommendedUsers.forEach(element => {
                this.recommendedUsersList.push(element.user)
                this.userVsTicketCountObj[element.user.id] =
                  element.reason.no_of_tickets
                for (let i = 0; i < this.userList.length; i++) {
                  if (this.userList[i].id === element.user.id) {
                    this.userList.splice(i, 1)
                  }
                }
              })
            }
          }
          this.loading = false
        })
        .catch(() => {
          // TODO handle errors
        })
    },
    loadWorkorders() {
      let objKey = 'model'
      if (this.viewtype === 'view') objKey = 'record'
      let { [objKey]: parent } = this
      let { assignedTo, assignmentGroup } = parent || {}
      let { id: assignedToId, name: assignedToName } = assignedTo || {}
      let { id: assignmentGroupId, name: assignmentGroupName } =
        assignmentGroup || {}

      if (!isEmpty(assignedToName)) this.selectedName = assignedToName
      else
        this.selectedName = !isEmpty(assignedToId)
          ? this.getUserNameFromList(assignedToId)
          : null

      if (!isEmpty(assignmentGroupName))
        this.selectedGroup = assignmentGroupName
      else
        this.assignmentGroupName = !isEmpty(assignmentGroupId)
          ? this.getGroupNameFromList(assignmentGroupId)
          : null
    },
    assignment(value) {
      this.assignmentValue = value
    },
    assignWorkOrder(assignedObj, index, value) {
      let isValueChanged = false
      let { model, $refs } = this
      let { assignedTo, assignmentGroup } = model || {}
      let { id, name } = assignedObj || {}
      if (value === 'assignmentGroup') {
        let { id: assignmentGroupId } = assignmentGroup || {}
        if (!isEmpty(name)) this.selectedGroup = name
        else
          this.selectedGroup = !isEmpty(id)
            ? this.getGroupNameFromList(id)
            : null
        if (!isEmpty(assignmentGroup)) {
          isValueChanged = assignmentGroupId !== id
          this.$set(this.model.assignmentGroup, 'id', id)
          this.$set(this.model.assignmentGroup, 'name', name)
        }
        this.$emit('input', id)
        this.$emit('value', model)
        this.groupmemberList(index)
        if (isValueChanged) this.$emit('onChange')
      }
      if (value === 'assignedTo' || value === 'assignedToGroupMember') {
        // get the assignedTo user and update using 'updateAssignedTo' emit.
        let { id: assignedToId } = assignedTo || {}
        if (!isEmpty(name)) this.selectedName = name
        else
          this.selectedName = !isEmpty(id) ? this.getUserNameFromList(id) : null
        if (!isEmpty(assignedTo)) {
          isValueChanged = assignedToId !== id
          this.$set(this.model.assignedTo, 'id', id)
          this.$set(this.model.assignedTo, 'name', name)
        }
        this.$emit('input', id)
        this.$emit('value', model)
        if ($refs['assignedtopopover']) $refs['assignedtopopover'].close()
        if (isValueChanged) this.$emit('onChange', assignedObj)
      }
    },
    groupmemberList(groupIndex) {
      this.assignmentValue = 'assignedToGroupMember'
      this.memberList = this.groupList[groupIndex].members
      this.selectedName = null
    },
    assignViewWorkOrder(assignedTo, assignmentGroup, index) {
      let { $refs, record } = this
      let assignObj = {}
      let moduleState = this.getTicketStatusByLabel('Assigned', 'workorder')
      let { id: statusId } = moduleState || {}
      let { id: recordId } = record || {}
      assignObj.id = [recordId]
      if (assignedTo) {
        assignObj.assignedTo = assignedTo
        let { id, name } = assignedTo || {}
        if (!isEmpty(name)) this.selectedName = name
        else
          this.selectedName = !isEmpty(id) ? this.getUserNameFromList(id) : null
        if ($refs['assignedtopopover']) $refs['assignedtopopover'].close()
      }
      if (assignmentGroup) {
        assignObj.assignmentGroup = assignmentGroup
        let { id, name } = assignmentGroup || {}
        if (!isEmpty(name)) this.selectedGroup = name
        else
          this.selectedGroup = !isEmpty(id)
            ? this.getGroupNameFromList(id)
            : null
        this.groupmemberList(index)
      }
      assignObj.status = {
        id: statusId,
        status: 'Assigned',
      }

      this.$store.dispatch('workorder/assignWorkOrder', assignObj).then(() => {
        this.$emit('assignactivity', assignObj)
        this.$dialog.notify(
          this.$t('common.products.workorder_assigned_successfully')
        )
      })
    },
    async scopeTeamUser() {
      let params = {}
      let { siteId } = this
      if (siteId) {
        params.siteId = siteId
      }
      let { data, error } = await API.post(`v3/group/scopeBySite`, params)
      if (!error) {
        let { groups, users } = data || {}
        this.userList = users || []
        this.groupList = groups || []
      }
    },
    removeTeamWO(assignValue) {
      let { record } = this
      let { id: recordId } = record || {}
      let moduleState = this.getTicketStatusByLabel('Assigned', 'workorder')
      let { id: statusId } = moduleState || {}
      let assignObj = {
        id: [recordId],
        [assignValue]: { id: -1 },
        status: {
          id: statusId,
          status: 'Assigned',
        },
      }
      this.$store.dispatch('workorder/assignWorkOrder', assignObj).then(() => {
        this.$emit('assignactivity', assignObj)
        this.$dialog.notify(
          this.$t('common.products.workorder_assigned_successfully')
        )
      })
    },
    reassign(assignValue) {
      let { model } = this
      let { assignmentGroup, assignedTo } = model || {}
      if (assignValue === 'assignmentGroup') {
        this.selectedGroup = null
        if (!isEmpty(assignmentGroup)) {
          this.$set(this.model.assignmentGroup, 'id', null)
          this.$set(this.model.assignmentGroup, 'name', null)
        }
      }
      if (assignValue === 'assignedTo') {
        this.selectedName = null
        if (!isEmpty(assignedTo)) {
          this.$set(this.model.assignedTo, 'id', null)
          this.$set(this.model.assignedTo, 'name', null)
        }
      }
    },
    reassignWO(assignValue) {
      if (assignValue === 'assignmentGroup') {
        this.selectedGroup = null
      }
      if (assignValue === 'assignedTo') {
        this.selectedName = null
      }
      this.removeTeamWO(assignValue)
    },
    getUserNameFromList(id) {
      let { userList } = this
      if (!isEmpty(userList)) {
        let user = userList.find(user => user.id === id)
        if (!isEmpty(user)) return this.$getProperty(user, 'name', null)
      } else return this.getUser(id).name
    },
    getGroupNameFromList(id) {
      let { groupList } = this
      if (!isEmpty(groupList)) {
        let group = groupList.find(group => group.id === id)
        if (!isEmpty(group)) return this.$getProperty(group, 'name', null)
      } else {
        this.$store.dispatch('loadGroups').then(() => {
          return (this.getGroup(id) || {}).name
        })
      }
    },
  },
}
</script>
<style lang="scss">
.popover-active-block .active {
  font-weight: 500;
  color: #ed518f;
  border-bottom: 2px solid #ed518f;
}
.popover-active-block .group-item-label {
  padding-top: 5px;
  padding-bottom: 5px;
}
.popover-active-block {
  background-color: rgb(241, 243, 245);
  position: sticky;
  top: 0;
  z-index: 2;
}
.fc-assignment-scroll-popup .group-item-label:hover {
  background: hsla(0, 0%, 96.1%, 0.5);
  cursor: pointer;
}
.fc-assignment-scroll-popup {
  padding-top: 10px;
}
.fc-assignment-scroll-popup .group-item-label {
  padding-top: 10px;
  padding-bottom: 10px;
  margin-top: 0;
  margin-bottom: 0;
  padding-left: 20px;
}
.groupAssignment .active {
  color: #ed518f;
  border-bottom: 2px solid #ed518f;
}
.fc-search-sticky {
  position: sticky;
  top: 21.8%;
  z-index: 400;
  background: #fff;
}
.groupAssignment.techgroupactive {
  border-bottom: 2px solid pink;
}
.assignment-popover {
  width: 320px;
  height: 350px;
  background-color: #ffffff;
  box-shadow: 0 11px 15px 0 rgba(185, 185, 185, 0.5);
}
.group-label {
  font-size: 10px;
  letter-spacing: 1px;
  text-align: left;
}

.group-sublabel {
  font-size: 14px;
  letter-spacing: 0.5px;
  text-align: left;
  color: #333333;
  margin-bottom: 15px;
  margin-top: 10px;
}

.remove-icon {
  width: 10px;
  height: 10px;
}
.group-item-label {
  color: #333333;
  font-size: 13px;
  letter-spacing: 0.4px;
  margin-top: 5px;
  margin-bottom: 5px;
}
.fc-assignment-popover-height {
  height: 320px;
  overflow-y: scroll;
  overflow-x: hidden;
}
</style>
