<template>
  <div class="scrollable-y100">
    <div class="fc-pm-form-container">
      <div class="fc-pm-form-right-main">
        <div v-if="!model.isEdit" class="fc-grey-text mT10">
          NEW PLANNED MAINTENANCE
        </div>
        <div v-else class="fc-grey-text mT10">
          EDIT PLANNED MAINTENANCE
        </div>
        <div class="heading-black22 mB20">
          Maintenance for {{ resourceName() }}
        </div>
        <div class="fc-pm-main-bg fc-pm-main-bg2">
          <div class="fc-pm-main-content">
            <div>
              <div class="fc-text-pink width50 fL">RESOURCE PLAN</div>
              <div class="fR fc-black-small-txt-12">
                <div class="flex-middle">
                  <div class="">
                    <span v-if="from !== to">{{ from }} - </span>
                    <span>{{ to }}</span>
                    <template>
                      of {{ resourceListLength }}
                      <span
                        class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
                        @click="from > 1 ? prev() : null"
                        v-bind:class="{ disable: from <= 1 }"
                      ></span>
                      <span
                        class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
                        @click="hasMorePage ? next() : null"
                        v-bind:class="{
                          disable: to === resourceListLength,
                        }"
                      ></span>
                    </template>
                  </div>
                  <el-input
                    placeholder="search"
                    v-model="searchQuery"
                    type="search"
                    class="fc-input-full-border2 text-input-icon-align input-padding width300px"
                    prefix-icon="el-icon-search"
                  ></el-input>
                </div>
              </div>

              <div
                v-show="selectAll || selectedResourceListLength"
                class="clearboth pT20"
              >
                <el-button
                  @click="showTriggerSelect = true"
                  class="add-border-btn"
                  >Trigger</el-button
                >
                <el-button
                  @click="showNotificationSelect = true"
                  class="add-border-btn"
                  >Notifications</el-button
                >
                <el-button @click="showUserSelect = true" class="add-border-btn"
                  >Assign User</el-button
                >
              </div>
              <div
                class="fc-pm-task-form-table-con pm-resource-table pT20 clearboth"
              >
                <table class="fc-pm-form-table">
                  <thead>
                    <tr class="pm-trigger-head-tr">
                      <th>
                        <el-checkbox
                          v-model="selectAll"
                          @change="selected"
                        ></el-checkbox>
                      </th>
                      <th class="pT10 pB10" style="width: 300px;">RESOURCE</th>
                      <th
                        class="pT10 pB10 text-left"
                        style="width: 250px;padding-left: 25px !important;"
                      >
                        TRIGGER
                      </th>
                      <th
                        class="pT10 pB10 text-left"
                        style="width: 200px;padding-left: 25px !important;"
                      >
                        ASSIGN TO
                      </th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(resource, key) in model.woData.resourceList"
                      v-if="searchQuery.trim() || (key >= from - 1 && key < to)"
                      :key="key"
                      class="trigger-tr-visibility visibility-visible-actions"
                      v-show="
                        !searchQuery.trim() ||
                          resource.name
                            .trim()
                            .toLowerCase()
                            .includes(searchQuery.trim().toLowerCase())
                      "
                    >
                      <td>
                        <el-checkbox
                          @change="forceUpdate"
                          v-model="model.woData.resourceList[key].selected"
                        ></el-checkbox>
                      </td>
                      <td style="width: 300px;">
                        <div>{{ resource.name }}</div>
                      </td>
                      <td style="width: 250px;" class="text-left">
                        <el-select
                          @change="onChangeTriggerValue(key)"
                          multiple
                          collapse-tags
                          v-model="model.woData.resourceList[key].triggerNames"
                          placeholder="Select"
                          class="fc-input-full-border-select-hover fc-input-border-remove mR30 line-height35px height35px el-caret-down-icon width250px"
                        >
                          <el-option
                            v-if="!model.triggerData.isDefaultAllTriggers"
                            :label="
                              `${ellipsis(
                                model.triggerData.triggers[0].name
                              )} (Default)`
                            "
                            value="0"
                          ></el-option>
                          <el-option
                            v-else
                            label="All Triggers"
                            value="0"
                          ></el-option>
                          <el-option
                            v-for="(trigger, index) in model.triggerData
                              .triggers"
                            :key="index"
                            :label="ellipsis(trigger.name)"
                            :value="trigger.name"
                          ></el-option>
                        </el-select>
                      </td>
                      <!-- assign to column -->
                      <td style="width: 250px;" class="text-left">
                        <el-select
                          v-if="model.woData.resourceList[key].assignedTo > 0"
                          clearable
                          @click.stop
                          v-model="model.woData.resourceList[key].assignedTo"
                          @change="forceUpdate"
                          class="fc-input-full-border-select-hover fc-input-border-remove line-height35px height35px el-caret-down-icon pR0"
                          style="width:190px;"
                        >
                          <el-option
                            v-if="model.triggerData.defaultAssignedTo > 0"
                            :label="`${userName({ assignedTo: -1 })}`"
                            value="-1"
                          ></el-option>
                          <el-option
                            v-for="(user, ikey) in userList"
                            :key="ikey"
                            :label="user.name"
                            :value="user.ouid"
                          ></el-option>
                        </el-select>
                        <div v-else class="width200px">
                          <el-popover
                            placement="bottom"
                            width="200"
                            trigger="click"
                            v-model="popovers[key]"
                            popper-class="popover-height popover-arow-hover"
                          >
                            <div
                              v-for="(user, ikey) in userListBySite(
                                model.woData.resourceList[key].id
                              )"
                              :key="ikey"
                              :label="user.label"
                              :value="user.ouid"
                              @click.stop="selectedUser(key, user.ouid)"
                              class="p10 fc-div-hover pointer"
                            >
                              {{ user.name }}
                            </div>
                            <el-button
                              slot="reference"
                              class="btn-remove-border pL15 pR15"
                              >{{ userName(resource)
                              }}<i class="el-icon-arrow-down"></i
                            ></el-button>
                          </el-popover>
                        </div>
                      </td>

                      <!-- notifications -->
                      <td style="width: 100px;">
                        <div v-if="model.triggerData.reminders.length === 1">
                          <img
                            class="mL50"
                            v-if="
                              model.woData.resourceList[key].notifications
                                .length === 0
                            "
                            src="~assets/bell.svg"
                            width="15"
                            @click="handleSingleNotification(key)"
                            height="15"
                          />
                          <img
                            class="mL50"
                            v-else
                            src="~assets/bell-fill.svg"
                            @click="handleSingleNotification(key)"
                            width="15"
                            height="15"
                          />
                        </div>
                        <div v-else>
                          <el-popover
                            trigger="click"
                            placement="bottom"
                            width="200"
                            v-model="notificationPopovers[key]"
                            :popper-class="
                              model.triggerData.reminders.length
                                ? 'popover-height'
                                : 'hide'
                            "
                          >
                            <div
                              v-for="(notification, ikey) in model.triggerData
                                .reminders"
                              :key="ikey"
                              :label="notification.name"
                              :value="notification.name"
                              class="p10 pointer"
                            >
                              <el-checkbox
                                :checked="
                                  model.woData.resourceList[
                                    key
                                  ].notifications.includes(
                                    model.triggerData.reminders[ikey].name
                                  )
                                "
                                @change="
                                  val => {
                                    val
                                      ? model.woData.resourceList[
                                          key
                                        ].notifications.push(notification.name)
                                      : removeNotif(key, notification.name)
                                    forceUpdate()
                                  }
                                "
                              >
                                {{ notification.name }}
                              </el-checkbox>
                            </div>
                            <el-tooltip
                              slot="reference"
                              class="item"
                              effect="dark"
                              placement="bottom"
                              :popper-class="
                                model.woData.resourceList[key].notifications
                                  .length && !notificationPopovers[key]
                                  ? 'element-tooltip'
                                  : 'hide'
                              "
                            >
                              <img
                                class="mL50"
                                v-if="
                                  model.woData.resourceList[key].notifications
                                    .length
                                "
                                src="~assets/bell-fill.svg"
                                width="15"
                                @click="
                                  $set(
                                    notificationPopovers,
                                    key,
                                    !notificationPopovers[key]
                                  ),
                                    forceUpdate()
                                "
                                height="15"
                              />
                              <img
                                class="mL50"
                                src="~assets/bell.svg"
                                width="15"
                                @click="
                                  $set(
                                    notificationPopovers,
                                    key,
                                    !notificationPopovers[key]
                                  ),
                                    forceUpdate()
                                "
                                height="15"
                              />
                              <div
                                slot="content"
                                v-for="(item, index) in templateOptions(
                                  model.woData.resourceList[key].notifications
                                )"
                                :key="index"
                              >
                                {{
                                  `${model.woData.resourceList[key].notifications[index]} (${item})`
                                }}
                              </div>
                            </el-tooltip>
                          </el-popover>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel f13" @click="moveToPrevious"
              >PREVIOUS</el-button
            >
            <el-button
              type="primary"
              class="modal-btn-save f13"
              @click="moveToNext"
              >PROCEED TO NEXT
              <img
                src="~assets/arrow-pointing-white-right.svg"
                width="17px"
                class="fR"
            /></el-button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      v-if="showTriggerSelect"
      :visible.sync="showTriggerSelect"
      :append-to-body="true"
      title="TRIGGERS"
      width="30%"
      class="fc-dialog-center-container resource-dialog"
    >
      <el-row v-if="model.triggerData.triggers.length" class="pT10 pB10">
        <el-checkbox
          :checked="checkedDefault"
          @change="
            val => {
              val
                ? selectedTriggers.push('0')
                : (selectedTriggers = selectedTriggers.filter(i => i === '0'))
              forceUpdate()
            }
          "
        >
          {{ model.triggerData.triggers[0].name }} (Default)
        </el-checkbox>
      </el-row>
      <el-row
        v-for="(trigger, index) in model.triggerData.triggers"
        :key="index"
        class="pT10 pB10"
      >
        <el-checkbox
          :checked="selectedTriggers.includes(trigger.name)"
          @change="
            val => {
              val
                ? selectedTriggers.push(trigger.name)
                : (selectedTriggers = selectedTriggers.filter(
                    i => i === trigger.name
                  ))
              forceUpdate()
            }
          "
        >
          {{ trigger.name }}
        </el-checkbox>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click="
            ;(showTriggerSelect = false),
              (selectedTriggers = []),
              (checkedDefault = false)
          "
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="applyTriggers"
          >Apply</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      v-if="showNotificationSelect"
      :visible.sync="showNotificationSelect"
      :append-to-body="true"
      title="NOTIFICATIONS"
      width="30%"
      class="fc-dialog-center-container add-triger-dialog resource-dialog"
    >
      <el-row
        v-for="(notification, ikey) in model.triggerData.reminders"
        :key="ikey"
        class="mT10 mB10"
      >
        <el-checkbox
          :checked="selectedReminders.includes(notification.name)"
          @change="
            val => {
              val
                ? selectedReminders.push(notification.name)
                : (selectedReminders = selectedReminders.filter(
                    i => i === notification.name
                  ))
              forceUpdate()
            }
          "
          :label="notification.name"
        >
          {{ notification.name }}</el-checkbox
        >
      </el-row>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click=";(showNotificationSelect = false), (selectedReminders = [])"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="applyNotifications"
          >Apply</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      v-if="showUserSelect"
      :visible.sync="showUserSelect"
      :append-to-body="true"
      title="ASSIGN USER"
      width="30%"
      class="fc-dialog-center-container add-triger-dialog resource-dialog"
    >
      <el-row class="pT10">
        <el-radio v-model="selectedAssignee" label="-1" class="fc-radio-btn">{{
          model.triggerData.defaultAssignedTo > 0
            ? `${
                userList.find(
                  i => i.ouid === model.triggerData.defaultAssignedTo
                ).name
              } (Default)`
            : 'Default'
        }}</el-radio>
      </el-row>
      <el-row v-for="(user, ikey) in userList" :key="ikey" class="pT10 pB10">
        <el-radio
          v-model="selectedAssignee"
          :label="user.ouid"
          class="fc-radio-btn"
        >
          {{ user.name }}</el-radio
        >
      </el-row>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click=";(showUserSelect = false), (selectedAssignee = null)"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="applyUser"
          >Apply</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
import { isEmpty } from '@facilio/utils/validation'
export default {
  mixins: [PMMixin],
  props: ['model', 'users', 'groups'],
  data() {
    return {
      selectAll: false,
      searchQuery: '',
      popovers: {},
      triggerPopovers: {},
      notificationPopovers: {},
      showTriggerSelect: false,
      showNotificationSelect: false,
      selectedTrigger: null,
      selectedReminders: [],
      selectedTriggers: [],
      checkedDefault: false,
      showUserSelect: false,
      selectedAssignee: null,
      reminderType: [
        {
          label: 'Before Execution',
          value: 1,
        },
        {
          label: 'After Execution',
          value: 2,
        },
        {
          label: 'Before Due',
          value: 3,
        },
        {
          label: 'After Due',
          value: 4,
        },
      ],
      perPage: 20,
      page: 1,
      from: 0,
      to: 0,
      hasMorePage: false,
    }
  },
  watch: {
    page() {
      this.from = (this.page - 1) * this.perPage + 1
      this.to = this.from + this.perPage - 1
      if (this.to >= this.model.woData.resourceList.length) {
        this.to = this.model.woData.resourceList.length
        this.hasMorePage = true
      }
    },
    'model.woData.resourceList': {
      handler: function(newVal, oldVal) {
        if (!isEmpty(this.resourceListLength) && this.resourceListLength > 0) {
          this.from = 1
          if (this.perPage >= this.resourceListLength) {
            this.to = this.resourceListLength
            this.hasMorePage = false
          } else {
            this.to = this.perPage
            this.hasMorePage = true
          }
        }
      },
      // deep: true,
      immediate: true,
    },
  },
  created() {
    this.$store.dispatch('loadBuildings')
    //this.$store.dispatch('loadGroups') // users/groups sent from store pmnewform.vue as props.
  },
  computed: {
    ...mapState({
      // groups: state => state.groups,
      // users: state => state.users,
    }),
    userList() {
      if (
        this.model.woData.woModel.groups &&
        this.model.woData.woModel.groups.id
      ) {
        let group = this.groups.find(
          group => group.id === Number(this.model.woData.woModel.groups.id)
        )
        return group ? group.members : []
      }
      return this.users
    },
    buildingName() {
      let buildingId = this.model.woData.selectedBuilding
      let building = this.buildingList.find(i => i.id === buildingId)
      if (building) {
        return building.name
      }
      return ''
    },
    buildingList() {
      let buildings = []
      if (this.$store.getters.getBuildings && this.model.woData.sites) {
        for (let i = 0; i < this.model.woData.sites.length; i++) {
          for (let j = 0; j < this.$store.getters.getBuildings.length; j++) {
            if (
              this.$store.getters.getBuildings[j].siteId ===
              this.model.woData.sites[i]
            ) {
              buildings.push(this.$store.getters.getBuildings[j])
            }
          }
        }
      }
      return buildings
    },
    resourceListLength() {
      let { model } = this
      let { woData } = model || {}
      let { resourceList = [] } = woData || {}
      return resourceList.length
    },
    selectedResourceListLength() {
      let { model } = this
      let { woData } = model || {}
      let { resourceList = [] } = woData || {}
      return resourceList.filter(resource => resource.selected).length
    },
  },
  methods: {
    prev() {
      this.page = this.page - 1
    },
    next() {
      this.page = this.page + 1
    },
    ellipsis(string) {
      if (string.length > 10) {
        return string.substring(0, 9) + '...'
      }
      return string
    },
    generateReminderOptions() {
      let options = []
      options.push({
        label: '30 Min',
        value: 1800,
      })
      for (let i = 1; i < 24; i++) {
        options.push({
          label: i + (i === 1 ? ' Hour' : ' Hours'),
          value: i * 3600,
        })
      }
      for (let i = 1; i < 32; i++) {
        options.push({
          label: i + (i === 1 ? ' Day' : ' Days'),
          value: i * 24 * 3600,
        })
      }
      return options
    },
    templateOptions(notifications) {
      let options = []
      let allTimeOptions = this.generateReminderOptions()
      let timeLookup = {}
      allTimeOptions.forEach(op => {
        timeLookup[op.value] = op.label
      })
      let typeLookup = {}
      this.reminderType.forEach(type => {
        typeLookup[type.value] = type.label
      })
      notifications.forEach((notification, i) => {
        let n = this.model.triggerData.reminders.find(
          k => k.name === notification
        )
        options.push(`${timeLookup[n.duration]} ${typeLookup[n.type]}`)
      })
      return options
    },
    moveToNext() {
      this.$emit('next')
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    applyNotifications() {
      if (this.selectedReminders.length) {
        for (let i = 0; i < this.model.woData.resourceList.length; i++) {
          if (this.model.woData.resourceList[i].selected) {
            this.model.woData.resourceList[
              i
            ].notifications = this.$helpers.cloneObject(this.selectedReminders)
          }
        }
      }
      this.showNotificationSelect = false
      this.selectedReminders = []
      this.$forceUpdate()
    },
    applyTriggers() {
      if (this.checkedDefault) {
        for (let i = 0; i < this.model.woData.resourceList.length; i++) {
          this.model.woData.resourceList[i].triggerNames = ['0']
        }
      }
      if (this.selectedTriggers.length) {
        for (let i = 0; i < this.model.woData.resourceList.length; i++) {
          if (this.model.woData.resourceList[i].selected) {
            if (this.checkedDefault) {
              this.model.woData.resourceList[i].triggerNames = [
                ...this.model.woData.resourceList[i].triggerNames,
                ...this.selectedTriggers,
              ]
            } else {
              this.model.woData.resourceList[i].triggerNames = [
                ...this.selectedTriggers,
              ]
            }
          }
        }
      }
      this.showTriggerSelect = false
      this.selectedTriggers = []
      this.checkedDefault = false
      this.$forceUpdate()
    },
    applyUser() {
      if (this.selectedAssignee) {
        for (let i = 0; i < this.model.woData.resourceList.length; i++) {
          if (this.model.woData.resourceList[i].selected) {
            if (this.selectedAssignee === -1) {
              this.model.woData.resourceList[i].assignedTo = -1
            } else {
              this.model.woData.resourceList[i].assignedTo = Number(
                this.selectedAssignee
              )
            }
          }
        }
      }
      this.showUserSelect = false
      this.selectedAssignee = null
      this.$forceUpdate()
    },
    selected(val) {
      for (let i = 0; i < this.model.woData.resourceList.length; i++) {
        if (
          !this.searchQuery.trim() ||
          this.model.woData.resourceList[i].name
            .trim()
            .toLowerCase()
            .includes(this.searchQuery.trim().toLowerCase())
        ) {
          if (i >= this.from - 1 && i < this.to) {
            this.model.woData.resourceList[i].selected = val
          }
        }
      }
    },
    userName(resource) {
      if (!this.userList) {
        return ''
      }
      let user = this.userList.find(user => user.ouid === resource.assignedTo)
      if (user) {
        return user.name
      }
      let defaultVal = this.userList.find(
        user => user.ouid === this.model.triggerData.defaultAssignedTo
      )
      if (defaultVal) {
        return `${defaultVal.name} (Default)`
      }
      return ''
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    selectTrigger(index, triggerName) {
      this.model.woData.resourceList[index].triggerName = triggerName
      this.$forceUpdate()
    },
    selectedUser(index, ouid) {
      this.model.woData.resourceList[index].assignedTo = Number(ouid)
      this.popovers[index] = false
      let resourceList = this.$helpers.cloneObject(
        this.model.woData.resourceList
      )
      this.$set(this.model.woData, 'resourceList', resourceList) // hack: since pmreview is not re-rendering
    },
    removeNotif(key, name) {
      this.model.woData.resourceList[
        key
      ].notifications = this.model.woData.resourceList[
        key
      ].notifications.filter(i => i !== name)
      this.$forceUpdate()
    },
    handleSingleNotification(key) {
      if (this.model.woData.resourceList[key].notifications.length === 1) {
        this.model.woData.resourceList[key].notifications.splice(0, 1)
      } else {
        this.model.woData.resourceList[key].notifications.push(
          this.model.triggerData.reminders[0].name
        )
      }
    },
    userListBySite(siteID) {
      return this.users.filter(user => this.canHandleSite(user, [siteID]))
    },
    onChangeTriggerValue(key) {
      if (isEmpty(key)) {
        return
      }
      let { model } = this
      let { woData } = model || {}
      let { resourceList = [] } = woData || {}
      let { triggerData } = model || {}
      let { triggers = [] } = triggerData || {}
      let actualTriggerNames = []

      triggers.forEach(trigger => {
        actualTriggerNames.push(trigger.name)
      })
      let triggerNames = resourceList[key] ? resourceList[key].triggerNames : []

      let allTriggerIndex = triggerNames.findIndex(i => i === '0')
      let actualTriggerIndex = triggerNames.findIndex(i =>
        actualTriggerNames.includes(i)
      )

      if (actualTriggerIndex < 0 && allTriggerIndex >= 0) {
        let allTrigger =
          triggerNames[allTriggerIndex] || null
            ? [triggerNames[allTriggerIndex]]
            : []
        this.$set(
          this,
          `model.woData.resourceList[${key}].triggerNames`,
          allTrigger
        )
      } else if (actualTriggerIndex >= 0 && allTriggerIndex >= 0) {
        this.$set(
          this,
          `model.woData.resourceList[${key}].triggerNames`,
          triggerNames.splice(allTriggerIndex, 1)
        )
      }
      this.forceUpdate()
    },
  },
}
</script>
<style>
.pm-resource-table table tbody tr td {
  padding-top: 12px !important;
  padding-bottom: 12px !important;
  padding-left: 10px !important;
  padding-right: 10px !important;
}
.pm-resource-table table thead tr th {
  padding-top: 12px !important;
  padding-bottom: 12px !important;
  background: #fbfbfb;
  padding-left: 10px !important;
  padding-right: 10px !important;
}
.resource-dialog .el-dialog__body {
  max-height: 420px;
  overflow-x: hidden;
  overflow-y: scroll;
  padding-bottom: 60px;
}
.el-icon-arrow-right.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
.el-icon-arrow-left.disable {
  color: #50506c;
  font-weight: bold;
  opacity: 0.4;
}
</style>
