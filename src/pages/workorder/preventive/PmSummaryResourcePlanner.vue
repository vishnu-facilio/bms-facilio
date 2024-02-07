<template>
  <div>
    <div class="fc-pm-form-container">
      <div class="position-relative">
        <div class="flex-middle width100 justify-content-space mT30 mL30 PL20">
          <div class="fc-black3-16 text-capitalize">
            Associated
            {{
              pm.pmCreationType === 2 && pm.assignmentType === 4
                ? 'Assets'
                : 'Spaces'
            }}
          </div>

          <!-- new pagination block -->
          <div class="fc-black-small-txt-12">
            <div class="flex-middle" style="height: 28px;">
              <div class="">
                <span
                  >{{ pageSettings.from + 1 }} -
                  {{ Math.min(pageSettings.to, resources.length) }}
                </span>
                <span>of {{ resources.length }}</span>
                <template>
                  <span
                    class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
                    @click="prevPage()"
                    v-bind:class="{ disable: pageSettings.from === 0 }"
                  ></span>
                  <span
                    class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
                    @click="nextPage()"
                    v-bind:class="{
                      disable:
                        Math.min(pageSettings.to, resources.length) ===
                        resources.length,
                    }"
                  ></span>
                </template>
              </div>
              <div
                class="fc-separator-lg mL10 mR10"
                style="height: 17px; line-height: 17px;"
              ></div>
              <f-search class="mL10 mR30 " v-model="resources"></f-search>
            </div>
          </div>

          <!-- pagination settings -->
          <!-- <div class="fc-black-small-txt-12">
            <div class="flex-middle" style="height: 28px;">
              <div class="">
                <span v-if="from !== to">{{ from }} - </span>
                <span>{{ to }}</span>
                <template>
                  of {{ resources.length }}
                  <span
                    class="el-icon-arrow-left fc-black-small-txt-12 fw-bold f16 pointer mL10 mR5"
                    @click="from > 1 ? prev() : null"
                    v-bind:class="{ disable: from <= 1 }"
                  ></span>
                  <span
                    class="el-icon-arrow-right fc-black-small-txt-12 f16 pointer mR10 fw-bold"
                    @click="hasMorePage ? next() : null"
                    v-bind:class="{
                      disable: to === resources.length,
                    }"
                  ></span>
                </template>
              </div>
              <div
                class="fc-separator-lg mL10 mR10"
                style="height: 17px; line-height: 17px;"
              ></div>

              <f-search
                class="mL10 mR30 pm-resource-search-block"
                v-model="resources"
              ></f-search>
            </div>
          </div> -->
        </div>

        <div
          class="fc-pm-task-form-table-con pm-resource-table pT20 clearboth mL30 pm-summary-resources-table-con position-relative"
        >
          <div
            v-show="
              selectAll ||
                model.woData.resourceList.filter(i => i.selected).length
            "
            class="mL30 trigger-action-btn"
          >
            <el-button
              @click="showTriggerSelect = true"
              class="btn--tertiary fw4 rounded-none"
              >Trigger</el-button
            >
            <el-button
              @click="showNotificationSelect = true"
              class="btn--tertiary fw4 rounded-none"
              >Notifications</el-button
            >
            <el-button
              @click="showUserSelect = true"
              class="btn--tertiary fw4 rounded-none"
              >Assign User</el-button
            >
            <el-button @click="save()" class="btn--tertiary fw4 rounded-none"
              >Apply</el-button
            >
          </div>
          <table class="fc-pm-form-table">
            <thead>
              <tr class="pm-trigger-head-tr">
                <th class="pT10 pB10" style="padding-left: 25px !important;">
                  <div v-if="assignmentToAssetCategory(pm)">
                    ASSETS
                  </div>
                  <div
                    v-if="
                      assignmentToSpaceCategory(pm) || assignmentToAllFloors(pm)
                    "
                  >
                    SPACES
                  </div>
                </th>
                <th class="pT10 pB10 text-left">ASSIGNED TO</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(res, key) in pagedAssets()" :key="key">
                <td>{{ res.name }}</td>
                <td>{{ fetchUserName(res.assignedToId) }}</td>
              </tr>

              <!-- <tr
                v-for="(resource, key) in resources"
                v-if="searchQuery.trim() || (key >= from - 1 && key < to)"
                :key="key"
                class="trigger-tr-visibility visibility-visible-actions"
              >
                <td style="padding-left: 25px !important;">
                  <div>{{ resource.name }}</div>
                </td>

                <td class="text-left visibility-visible-actions">
                  <div v-if="resource && resource.triggerNames.length > 0">
                    <el-tooltip class="item" effect="dark" placement="bottom">
                      <div class="fwBold">
                        {{ resource.triggerNames.length }} Triggers
                      </div>
                      <div
                        slot="content"
                        v-for="item in resource.triggerNames"
                        :key="item"
                      >
                        {{ item }}
                      </div>
                    </el-tooltip>
                  </div>
                  <div
                    v-else-if="
                      resource.triggerName && resource.triggerNames.length === 0
                    "
                  >
                    <div class="fwBold">
                      {{ resource.triggerName }}
                    </div>
                  </div>
                  <div v-else>
                    ---
                  </div>
                </td>
                <td class="text-left visibility-visible-actions">
                  <user-avatar
                    size="sm"
                    :user="resource.assignedTo"
                    :showPopover="false"
                    :name="true"
                  ></user-avatar>
                </td>
                <td>
                  <el-tooltip
                    class="item"
                    effect="dark"
                    placement="bottom"
                    :popper-class="
                      resource.notifications.length ? 'element-tooltip' : 'hide'
                    "
                  >
                    <img
                      v-if="resource.notifications.length"
                      src="~assets/bell-fill.svg"
                      width="15"
                      height="15"
                    />
                    <img v-else src="~assets/bell.svg" width="15" height="15" />
                    <div
                      slot="content"
                      v-for="(item, index) in resource.notifications"
                      :key="index"
                    >
                      {{ item }}
                    </div>
                  </el-tooltip>
                </td>
              </tr> -->
            </tbody>
            <!-- <tbody>
                <tr v-for="(resource, key) in model.woData.resourceList" v-if="searchQuery.trim() || (key >= (from - 1) && key < to)" :key="key" class="trigger-tr-visibility visibility-visible-actions" v-show="!searchQuery.trim() || resource.name.trim().toLowerCase().includes(searchQuery.trim().toLowerCase())">
                  <td class="pL30 text-center"><el-checkbox @change="forceUpdate" v-model="model.woData.resourceList[key].selected" style="padding-left: 7px;"></el-checkbox></td>
                  <td class="pL0" style="padding-left: 0 !important;">
                    <div>{{ resource.name }}</div>
                  </td>

                  <td class="text-left visibility-visible-actions">
                    <el-select @change="(val) => { let zi = model.woData.resourceList[key].triggerNames.findIndex(i => i === '0'); let di = model.woData.resourceList[key].triggerNames.findIndex(i => i === model.triggerData.triggers[0].name); if (di >= 0 && zi >= 0) { model.woData.resourceList[key].triggerNames.splice(zi, 1) }; forceUpdate() }" multiple collapse-tags v-model="model.woData.resourceList[key].triggerNames" placeholder="Select" class="fc-input-border-remove el-caret-down-icon fc-caret-tag">
                      <el-option :label="`${ellipsis(model.triggerData.triggers[0].name)} (Default)`" value="0"></el-option>
                      <el-option v-for="(trigger, index) in model.triggerData.triggers" :key="index" :label="ellipsis(trigger.name)" :value="trigger.name"></el-option>
                    </el-select>
                  </td>
                  <td class="text-left visibility-visible-actions">


                    <el-select v-if="model.woData.resourceList[key].assignedTo > 0" @click.stop v-model="model.woData.resourceList[key].assignedTo" @change="forceUpdate" class="fc-input-border-remove el-caret-down-icon">
                      <el-option v-if="model.triggerData.defaultAssignedTo > 0" :label="`${userName({assignedTo: -1})}`" value=-1></el-option>
                      <el-option v-for="(user, ikey) in userList" :key="ikey" :label="user.name" :value="user.ouid"></el-option>
                    </el-select>
                    <div v-else>
                      <el-popover placement="bottom" width="200" trigger="click"  v-model="popovers[key]" popper-class="popover-height popover-arow-hover">
                        <div v-for="(user, ikey) in userList" :key="ikey" :label="user.label" :value="user.ouid" @click.stop="selectedUser(key, user.ouid)" class="p10 fc-div-hover pointer">
                        {{ user.name }}
                        </div>
                        <el-button slot="reference" class="btn-remove-border btn-remove-border-hover-remove pL0 pR0 bold">{{ userName(resource) }}<i class="el-icon-arrow-down visibility-hide-actions pointer"></i></el-button>
                      </el-popover>
                    </div>
                  </td>
                  <td>
                    <div v-if="model.triggerData.reminders.length === 1" class="">
                      <img class="mL50" v-if="model.woData.resourceList[key].notifications.length === 0" src="~assets/bell.svg" width="15" @click="handleSingleNotification(key)" height="15">
                      <img class="mL50" v-else src="~assets/bell-fill.svg" @click="handleSingleNotification(key)" width="15" height="15">
                    </div>
                    <div v-else>
                      <el-popover trigger="click" placement="bottom" width="200" v-model="notificationPopovers[key]" :popper-class="(model.triggerData.reminders.length) ? 'popover-height' : 'hide'">
                        <div v-for="(notification, ikey) in model.triggerData.reminders" :key="ikey" :label="notification.name" :value="notification.name" class="p10 pointer">
                          <el-checkbox :checked="model.woData.resourceList[key].notifications.includes(model.triggerData.reminders[ikey].name)" @change="(val) => { val ? model.woData.resourceList[key].notifications.push(notification.name): removeNotif(key, notification.name); forceUpdate()}"> {{ notification.name }} </el-checkbox>
                        </div>
                        <el-tooltip slot="reference" class="item" effect="dark" placement="bottom" :popper-class="(model.woData.resourceList[key].notifications.length && !notificationPopovers[key]) ? 'element-tooltip' : 'hide'">
                          <img class="mL50" v-if="model.woData.resourceList[key].notifications.length" src="~assets/bell-fill.svg" width="15" @click="$set(notificationPopovers, key, !notificationPopovers[key]), forceUpdate()" height="15">
                          <img class="mL50" src="~assets/bell.svg" width="15" @click="$set(notificationPopovers, key, !notificationPopovers[key]), forceUpdate()" height="15">
                          <div slot="content" v-for="(item, index) in templateOptions(model.woData.resourceList[key].notifications)" :key="index">{{ `${model.woData.resourceList[key].notifications[index]} (${item})` }}</div>
                        </el-tooltip>
                      </el-popover>
                    </div>
                  </td>
                </tr>
              </tbody> -->
          </table>
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

import FSearch from '@/FSearch'
import PMMixin from '@/mixins/PMMixin'
export default {
  mixins: [PMMixin],
  props: [
    'model',
    'summaryEdit',
    'includeMainContent',
    'pmid',
    'pm',
    'resources',
  ],
  components: {
    FSearch,
  },
  data() {
    return {
      pageSettings: {
        perPage: 10,
        page: 1,
        from: 0,
        to: 10,
      },
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
        if (this.model.woData.resourceList.length) {
          this.from = 1
          if (this.perPage >= this.model.woData.resourceList.length) {
            this.to = this.model.woData.resourceList.length
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
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadGroups')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    ...mapState({
      groups: state => state.groups,
      users: state => state.users,
    }),
    userList() {
      if (
        this.model.woData.woModel.groups &&
        this.model.woData.woModel.groups.id
      ) {
        let group = this.groups.find(
          group => group.id === Number(this.model.woData.woModel.groups.id)
        )
        return group.members
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
      let buildings = this.$store.getters.getBuildings
      if (
        buildings &&
        this.model.woData.woModel &&
        this.model.woData.woModel.site.id
      ) {
        return buildings.filter(
          i => i.siteId === Number(this.model.woData.woModel.site.id)
        )
      }
      return buildings
    },
  },
  methods: {
    nextPage() {
      let maxPage = -1
      if (this.resources.length % this.pageSettings.perPage === 0) {
        // when there are 10 records with 5 per page,
        // you get exactly 2 pages
        maxPage = this.resources.length / this.pageSettings.perPage
      } else {
        // when there are 12 records with 5 per page,
        // you get exactly 2 pages & 1 half filled page
        maxPage = this.resources.length / this.pageSettings.perPage + 1
      }
      if (this.pageSettings.page === maxPage) {
        return
      }
      this.pageSettings.page = this.pageSettings.page + 1
      this.updatePaginationPivots()
    },
    prevPage() {
      // you cannot go beyond page 1
      if (this.pageSettings.page === 1) {
        return
      }
      this.pageSettings.page = this.pageSettings.page - 1
      this.updatePaginationPivots()
    },
    updatePaginationPivots() {
      if (this.pageSettings.page === 1) {
        this.pageSettings.from = 0
        this.pageSettings.to = this.pageSettings.perPage
      } else {
        this.pageSettings.from =
          (this.pageSettings.page - 1) * this.pageSettings.perPage

        this.pageSettings.to = Math.min(
          this.pageSettings.page * this.pageSettings.perPage,
          this.resources.length
        )
      }
    },
    pagedAssets() {
      return this.resources.slice(this.pageSettings.from, this.pageSettings.to)
    },
    fetchUserName(ID) {
      const user = this.$store.getters.getUser(ID)
      return user ? user.name : '---'
    },
    assignmentToAssetCategory(pm) {
      return pm.pmCreationType === 2 && pm.assignmentType === 4
    },
    assignmentToSpaceCategory(pm) {
      return pm.pmCreationType === 2 && pm.assignmentType === 3
    },
    assignmentToAllFloors(pm) {
      return pm.pmCreationType === 2 && pm.assignmentType === 1
    },
    prev() {
      this.page = this.page - 1
    },
    next() {
      this.page = this.page + 1
    },
    save() {
      if (
        !this.model.woData.woModel.site.id ||
        this.model.woData.woModel.site.id <= 0
      ) {
        this.$message({
          message: 'Site is mandatory',
          type: 'error',
        })
        return
      }
      let workorder = this.$helpers.cloneObject(this.model.woData.woModel)
      Object.assign(workorder, workorder.woModel)
      workorder.isSignatureRequired = this.model.woData.isSignatureRequired
      let uploadFiles = []
      if (this.model.woData.woModel.attachedFiles) {
        for (
          let i = 0;
          i < this.model.woData.woModel.attachedFiles.uploadFiles.length;
          i++
        ) {
          uploadFiles.push(
            new File(
              [this.model.woData.woModel.attachedFiles.uploadFiles[i]],
              this.model.woData.woModel.attachedFiles.uploadFiles[i].name
            )
          )
        }
        if (this.model.woData.woModel.attachedFiles.attachments) {
          workorder.attachments = this.model.woData.woModel.attachedFiles.attachments.filter(
            u => u.fileId > 0
          )
        }
        delete workorder.attachedFiles
      }
      delete workorder.woModel

      workorder.resource = this.model.woData.singleResource
      if (workorder.resource && !workorder.resource.id) {
        delete workorder.resource
      }

      if (workorder.category && !workorder.category.id) {
        delete workorder.category
      }

      if (workorder.type && !workorder.type.id) {
        delete workorder.type
      }

      if (workorder.priority && !workorder.priority.id) {
        delete workorder.priority
      }

      if (workorder.site && workorder.site.id) {
        workorder.siteId = workorder.site.id
        delete workorder.site
      }

      workorder.assignmentGroup = workorder.groups
      if (workorder.assignmentGroup) {
        if (!workorder.assignmentGroup.id) {
          delete workorder.assignmentGroup
        } else {
          workorder.assignmentGroup = { id: workorder.assignmentGroup.id }
        }
      }

      if (this.model.triggerData.defaultAssignedTo) {
        workorder.assignedTo = { id: this.model.triggerData.defaultAssignedTo }
      }

      let formdata = {}
      formdata.workorder = workorder
      if (workorder.duration) {
        workorder.duration =
          this.$util.daysHoursToUnixTime(workorder.duration) || -1
      }
      formdata.deleteReadingRulesList = []

      let sections = []
      let sequence = 1

      let isSingleWoAssetType =
        this.model.woData.workOrderType === 'single' &&
        this.model.woData.singleResource.resourceTypeEnum === 'ASSET'

      for (let key in this.model.taskData.taskSections) {
        let section = this.$helpers.cloneObject(
          this.model.taskData.taskSections[key]
        )
        section.pmIncludeExcludeResourceContexts = []
        section.selectedResourceList.forEach(x => {
          section.pmIncludeExcludeResourceContexts.push({
            resourceId: x.id,
            isInclude: true,
            parentType: 2,
          })
        })
        delete section.selectedResourceList

        section.pmTaskSectionTemplateTriggers = []
        section.triggers.forEach((x, index) => {
          if (x.triggerName !== 'All') {
            if (index === 0) {
              x.executeIfNotInTime = -1
            } else {
              x.executeIfNotInTime = x.executeIfNotInTime * 24 * 60 * 60
            }
            section.pmTaskSectionTemplateTriggers.push(x)
          }
        })
        delete section.triggers

        if (section.category && section.category.length) {
          if (
            section.category[0] === 'CURRENT_FLOOR' ||
            section.category[0] === 'CURRENT_ASSET' ||
            section.category[0] === 'CURRENT_SPACE'
          ) {
            section.assignmentType = 5
          } else {
            if (section.category[0] === 'space') {
              section.assignmentType = 3
              section.spaceCategoryId = section.category[1]
            } else if (section.category[0] === 'asset') {
              section.assignmentType = 4
              section.assetCategoryId = section.category[1]
            }
          }
        }

        if (isSingleWoAssetType) {
          section.assignmentType = -1
          section.assetCategoryId = -1
        }

        if (section.inputType === '5' || section.inputType === '6') {
          let secOptions = []
          for (let key3 in section.options) {
            let option = section.options[key3]
            if (option && option.name) {
              secOptions[key3] = option.name
            }
          }
          section.options = secOptions
        } else {
          section.options = []
        }

        let sectionRules = this.constructReadingRules(section)
        section.readingRules = sectionRules.readingRules
        section.actionsList = sectionRules.actionsList
        formdata.deleteReadingRulesList.push(
          ...sectionRules.deleteReadingRulesList
        )

        section.taskTemplates = this.$helpers.cloneObject(section.tasks)
        delete section.tasks
        for (let key2 in section.taskTemplates) {
          let task = section.taskTemplates[key2]
          task.pmIncludeExcludeResourceContexts = []
          task.selectedResourceList.forEach(x => {
            task.pmIncludeExcludeResourceContexts.push({
              resourceId: x.id,
              isInclude: true,
              parentType: 3,
            })
          })
          delete task.selectedResourceList
          delete task.readingFields
          task.siteId = this.model.woData.woModel.site.id
          if (task.category && task.category.length) {
            if (
              task.category[0] === 'CURRENT_FLOOR' ||
              task.category[0] === 'CURRENT_ASSET' ||
              task.category[0] === 'CURRENT_SPACE'
            ) {
              task.assignmentType = 5
            } else {
              if (task.category[0] === 'space') {
                task.assignmentType = 3
                task.spaceCategoryId = task.category[1]
              } else if (task.category[0] === 'asset') {
                task.assignmentType = 4
                task.assetCategoryId = task.category[1]
              }
            }
          }
          if (isSingleWoAssetType) {
            task.assignmentType = -1
            task.assetCategoryId = -1
          }
          let errorMsg = this.validateTask(task)
          if (errorMsg) {
            this.$message({
              message: errorMsg,
              type: 'error',
            })
            this.isSaving = false
            return
          }
          if (task.inputType === '5' || task.inputType === '6') {
            let taskOptions = []
            for (let key3 in task.options) {
              let option = task.options[key3]
              if (option && option.name) {
                taskOptions[key3] = option.name
              }
            }
            task.options = taskOptions
          } else {
            task.options = []
          }

          let taskRules = this.constructReadingRules(task)

          task.readingRules = taskRules.readingRules
          formdata.deleteReadingRulesList.push(
            ...taskRules.deleteReadingRulesList
          )

          if (!task.readingFieldId) {
            task.readingFieldId = -1
          }
          task.sequence = sequence
          sequence = sequence + 1
        }
        sections.push(section)
      }

      formdata.tasks = sections
      let preventiveFormData = {}
      preventiveFormData.title = formdata.workorder.subject
      preventiveFormData.triggers = []

      preventiveFormData.preventOnNoTask = this.model.woData.isPreventOnNoTask

      preventiveFormData.pmCreationType =
        this.model.woData.workOrderType === 'single' ? 1 : 2
      preventiveFormData.baseSpaceId = this.model.woData.selectedBuilding || -1
      preventiveFormData.pmIncludeExcludeResourceContexts = []

      if (this.model.woData.resourceType === 'ALL_FLOORS') {
        preventiveFormData.assignmentType = 1
        this.model.woData.selectedFloorList.forEach(x => {
          preventiveFormData.pmIncludeExcludeResourceContexts.push({
            resourceId: x.id,
            isInclude: true,
            parentType: 1,
          })
        })
      } else if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
        preventiveFormData.assignmentType = 3
        preventiveFormData.spaceCategoryId = this.model.woData.spacecategoryId
        this.model.woData.selectedSpaceList.forEach(x => {
          preventiveFormData.pmIncludeExcludeResourceContexts.push({
            resourceId: x.id,
            isInclude: true,
            parentType: 1,
          })
        })
      } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
        preventiveFormData.assignmentType = 4
        preventiveFormData.assetCategoryId = this.model.woData.assetCategoryId
        this.model.woData.selectedResourceList.forEach(x => {
          preventiveFormData.pmIncludeExcludeResourceContexts.push({
            resourceId: x.id,
            isInclude: true,
            parentType: 1,
          })
        })
      }

      preventiveFormData.resourcePlanners = []
      this.model.woData.resourceList.forEach(resource => {
        let noti = []
        if (resource.triggerNames.length) {
          if (
            resource.triggerNames.length === 1 &&
            resource.triggerNames[0] === '0'
          ) {
            resource.triggerNames = []
          } else {
            let index = resource.triggerNames.findIndex(i => i === '0')
            if (index >= 0 && this.model.triggerData.triggers.length) {
              resource.triggerNames.splice(
                index,
                1,
                this.model.triggerData.triggers[0].name
              )
            }
          }
        }
        resource.notifications.forEach(n => {
          noti.push({ reminderName: n })
        })
        if (
          noti.length ||
          resource.triggerNames.length ||
          (resource.assignedTo && resource.assignedTo > -1)
        ) {
          let triggerContexts
          if (resource.triggerNames.length) {
            triggerContexts = resource.triggerNames.map(i => ({ name: i }))
          } else if (this.model.triggerData.triggers.length) {
            triggerContexts = [
              { name: this.model.triggerData.triggers[0].name },
            ]
          }
          preventiveFormData.resourcePlanners.push({
            assignedToId:
              resource.assignedTo || this.model.triggerData.defaultAssignedTo,
            resourceId: resource.id,
            pmResourcePlannerReminderContexts: noti,
            triggerContexts,
          })
        }
      })

      let reminders = []
      preventiveFormData.triggers = []
      for (let x in this.model.triggerData.triggers) {
        let trigger = Object.assign({}, this.model.triggerData.triggers[x])
        trigger.triggerType = x === '0' ? 1 : 2
        if (trigger.schedule) {
          trigger.custom = trigger.schedule.custom
        }
        if (trigger.type === 1) {
          trigger.triggerExecutionSource = 1
          trigger.startTime = this.$helpers.addTime(
            trigger.startDate,
            trigger.startHour.length ? trigger.startHour[0] : '00:00'
          )
          if (trigger.stopAfter !== 'never') {
            trigger.endTime = -1
          } else {
            trigger.endTime = -1
          }
          trigger.frequency = trigger.schedule.facilioFrequency
          if (trigger.frequency.length > 1) {
            preventiveFormData.frequency = 8
          } else {
            preventiveFormData.frequency = trigger.schedule.facilioFrequency
          }
          preventiveFormData.custom = trigger.schedule.custom
        } else if (trigger.type === 2) {
          trigger.triggerExecutionSource = 2
          trigger.startTime = -1
          trigger.endTime = -1
          if (trigger.stopAfterReading === 'never') {
            trigger.endReading = -1
          }
        } else if (trigger.type === 3) {
          trigger.startTime = -1
          trigger.endTime = -1
          trigger.triggerExecutionSource = 3
          trigger.workFlowRule = {
            criteria: this.$helpers.cloneObject(trigger.criteria),
          }
          if (trigger.workFlowRule.criteria) {
            delete trigger.workFlowRule.criteria.regEx
          }
          delete trigger.criteria
        }
        delete trigger.initialSchedule
        preventiveFormData.triggers.push(trigger)
      }
      let templates = ['email', 'sms', 'mobile']
      if (
        this.model.triggerData.reminders &&
        this.model.triggerData.reminders.length
      ) {
        for (let i = 0; i < this.model.triggerData.reminders.length; i++) {
          let reminder = this.$helpers.cloneObject(
            this.model.triggerData.reminders[i]
          )
          if (reminder.template) {
            templates.forEach(t => {
              let mode = t === 'email' ? 'mail' : t
              if (
                reminder.template[t].toAddr &&
                reminder.template[t].toAddr.length
              ) {
                let data = {
                  type: this.config[t].type,
                  message: reminder.template[t].message,
                  name: this.config[t].name,
                  workflow: {},
                }

                this.$common.setUserMailWorkflow(
                  reminder.template[t].toAddr,
                  data,
                  mode
                )
                this.$common.setExpressionFromPlaceHolder(
                  data.workflow,
                  reminder.template[t].message
                )

                if (t === 'mail' || t === 'mobile') {
                  data.subject = reminder.template[t].subject
                  this.$common.setExpressionFromPlaceHolder(
                    data.workflow,
                    reminder.template[t].subject
                  )
                }

                if (t === 'sms') {
                  data.body = reminder.template[t].subject
                }

                reminder[`${t}template`] = {
                  actionType: this.config[t].actionType,
                  templateJson: data,
                }
              }
            })
          }

          if (reminder.template) {
            delete reminder.template
          }
          reminders.push(reminder)
        }
      }
      formdata.preventivemaintenance = preventiveFormData
      formdata.reminders = []
      for (let idx = 0; idx < reminders.length; idx++) {
        if (reminders[idx].duration !== -1) {
          formdata.reminders.push(reminders[idx])
        }
      }

      this.updatePreventiveMaintenance(formdata, uploadFiles)
    },
    constructReadingRules(obj) {
      let readingRules = []
      let actionsList = []
      let deleteReadingRulesList = []
      if (obj.inputType === '4') {
        if (obj.validation === 'none') {
          if (obj.inputValidationRuleId && obj.inputValidationRuleId !== -1) {
            deleteReadingRulesList.push(obj.inputValidationRuleId)
          }
        } else if (
          obj.validation === 'incremental' ||
          obj.validation === 'decremental'
        ) {
          readingRules.push({
            id: obj.inputValidationRuleId || -1,
            name:
              obj.validation === 'incremental' ? 'Incremental' : 'Decremental',
            ruleType: 9,
            event: {
              activityType: 1,
            },
            thresholdType: 5,
            workflow: {
              expressions: [
                {
                  name: 'a',
                  constant: '${inputValue}', // eslint-disable-line no-template-curly-in-string
                },
                {
                  name: 'b',
                  constant: '${previousValue}', // eslint-disable-line no-template-curly-in-string
                },
              ],
              parameters: [
                {
                  name: 'inputValue',
                  typeString: 'Number',
                },
                {
                  name: 'previousValue',
                  typeString: 'Number',
                },
              ],
              resultEvaluator:
                obj.validation === 'incremental'
                  ? '(b!=-1)&&(a<b)'
                  : '(b!=-1)&&(a>b)',
            },
          })
        } else if (obj.validation === 'safeLimit') {
          if (
            (typeof obj.minSafeLimit !== 'undefined' &&
              obj.minSafeLimit != null) ||
            (typeof obj.maxSafeLimit !== 'undefined' &&
              obj.maxSafeLimit != null)
          ) {
            readingRules.push({
              id: obj.inputValidationRuleId || -1,
              name: 'Safelimit',
              ruleType: 9,
              event: {
                activityType: 1,
              },
              thresholdType: 5,
              workflow: {
                expressions: [
                  {
                    name: 'a',
                    constant: '${inputValue}', // eslint-disable-line no-template-curly-in-string
                  },
                  {
                    name: 'b',
                    constant: obj.minSafeLimit === null ? -1 : obj.minSafeLimit,
                  },
                  {
                    name: 'c',
                    constant: obj.maxSafeLimit === null ? -1 : obj.maxSafeLimit,
                  },
                ],
                parameters: [
                  {
                    name: 'inputValue',
                    typeString: 'Number',
                  },
                ],
                resultEvaluator: '(b!=-1&&a<b)||(c!=-1&&a>c)',
              },
            })
          }
        }
      }
      return { readingRules, actionsList, deleteReadingRulesList }
    },
    updatePreventiveMaintenance(data, uploadFiles) {
      let formData = new FormData()
      data.preventivemaintenance.siteId = data.workorder.siteId
      formData.append('workOrderString', JSON.stringify(data.workorder))
      formData.append(
        'preventiveMaintenanceString',
        JSON.stringify(data.preventivemaintenance)
      )
      formData.append('tasksString', JSON.stringify(data.tasks))
      if (data.reminders) {
        formData.append('reminderString', JSON.stringify(data.reminders))
      }
      formData.append(
        'deleteReadingRulesListString',
        JSON.stringify(data.deleteReadingRulesList)
      )
      if (uploadFiles) {
        for (let num = 0; num < uploadFiles.length; num++) {
          formData.append('attachedFiles', uploadFiles[num])
        }
      }
      this.$http
        .post(
          '/workorder/updateNewPreventiveMaintenance?id=' + parseInt(this.pmid),
          formData
        )
        .then(response => {
          if (typeof response.data === 'object') {
            this.$message({
              message: 'Planned maintenance updated successfully!',
              type: 'success',
            })
          } else {
            this.$message({
              message: 'Planned maintenance updation failed!',
              type: 'error',
            })
          }
        })
        .catch(_ => {
          this.$message({
            message: 'Failed to edit Planned maintenance!',
            type: 'error',
          })
        })
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
.pm-resource-table table tbody tr:hover {
  background-color: #f1f8fa;
  cursor: pointer;
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
.pm-summary-resources-table-con .fc-pm-form-table {
  background-color: #f7fafa;
  border: 1px solid #e6ecf3;
}
.pm-summary-resources-table-con table thead tr th {
  background-color: #f7fafa !important;
  border-top: 1px solid #e6ecf3 !important;
  padding-left: 20px !important;
  padding-right: 20px !important;
}
.pm-resource-table table tbody tr td {
  background: #fff;
}
.pm-resource-table table tbody tr:hover td {
  background: #f1f8fa !important;
  cursor: pointer;
}
.pm-summary-resources-table-con table tbody tr:hover {
  border-top: 1px solid #f2f5f6 !important;
  border-bottom: 1px solid #f2f5f6 !important;
  background: #f1f8fa !important;
}
.pm-summary-resources-table-con table tbody .fc-caret-tag .el-select__caret {
  padding-right: 30px;
  line-height: 34px;
}
.pm-summary-resources-table-con .fc-input-border-remove .el-input__inner {
  padding-right: 0 !important;
}
.fc-caret-tag .el-select__tags-text {
  font-size: 14px;
  font-weight: 500;
  color: #324056;
}
.fc-caret-tag .el-tag__close.el-icon-close {
  background: #fff;
  color: #324056;
  display: none;
}
.fc-caret-tag .el-tag__close.el-icon-close:hover {
  color: #324056;
  background: #fff;
}
.btn-remove-border-hover-remove:hover {
  border: none !important;
  background-color: transparent;
}
.btn-remove-border-hover-remove:active {
  color: #333333;
  border-color: transparent;
}
.btn-remove-border-hover-remove:focus {
  color: #333333;
  border-color: transparent;
  background-color: transparent;
}
.fc-caret-tag .el-select__tags {
  padding-left: 0;
}
.fc-caret-tag .el-select__tags:hover {
  background: transparent;
}
.trigger-action-btn {
  position: absolute;
  top: 30px;
  left: 38px;
  background: #fbfbfb;
  width: 88.3%;
  -webkit-animation: slidefromleft 0.1s ease;
  animation: slidefromleft 0.1s ease;
  -moz-webkit-animation: slidefromleft 0.1s ease;
}
.pm-resource-search-block .el-icon-search {
  font-weight: bold;
  font-size: 16px;
}
</style>
