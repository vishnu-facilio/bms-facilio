<template>
  <div class="scrollable-y100 pm-trigger-form">
    <div class="fc-pm-form-container">
      <div class="fc-pm-form-right-main">
        <div v-if="!model.isEdit" class="fc-grey-text mT10">
          NEW PLANNED MAINTENANCE
        </div>
        <div v-else>EDIT PLANNED MAINTENANCE</div>
        <div class="heading-black22 mB20">
          Maintenance for {{ resourceName() }}
        </div>
        <div class="fc-pm-main-bg fc-pm-main-bg2">
          <div class="fc-pm-main-content">
            <div class>
              <div
                v-if="model.triggerData.triggers.length"
                class="fc-pm-main-content-H"
              >
                TRIGGER AND ASSIGNING
              </div>
              <div
                v-if="model.triggerData.triggers.length"
                class="fc-heading-border-width43"
              ></div>
            </div>
            <div class="fc-pm-main-inner-container">
              <div
                v-if="!model.triggerData.triggers.length"
                class="text-center mT20"
              >
                <div class="pT10 pB10">
                  <div class="fc-text-pink">TRIGGER LIST</div>
                </div>
                <div>
                  <InlineSvg
                    src="svgs/emptystate/data-empty"
                    iconClass="icon icon-xxxxlg new-icon"
                  ></InlineSvg>
                </div>
                <div class="empty-greyH-bold bold">
                  {{ $t('maintenance.pm_list.schedule_your_maintenance') }}
                </div>
                <div class="empty-grey-desc pT10">
                  {{ $t('maintenance.pm_list.set_trigger_your_wo') }}
                </div>
                <div class="pT15">
                  <el-button
                    class="fc-btn-green-medium-border"
                    @click="addTrigger"
                    >ADD TRIGGER</el-button
                  >
                </div>
              </div>
              <div v-else class="fc-pm-task-form-table-con">
                <table class="fc-pm-form-table">
                  <thead>
                    <tr>
                      <th class="pT10 pB10">
                        <div class="fc-text-pink">TRIGGER LIST</div>
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(trigger, key) in model.triggerData.triggers"
                      :key="key"
                      @click="currentTriggerIndex(key)"
                      class="visibility-visible-actions pointer table-hover"
                    >
                      <td class>
                        <div class="felx-middle">
                          <div
                            class="dot-green fL"
                            style="margin-top: 9px;"
                          ></div>
                          <div
                            class="label-txt-black mL20 max-width500px fL line-height20"
                          >
                            {{ trigger.name }}
                          </div>
                          <div
                            v-if="
                              key === 0 &&
                                !model.triggerData.isDefaultAllTriggers
                            "
                            class="fc-tag fL mL10"
                          >
                            <el-tag>Default</el-tag>
                          </div>
                        </div>
                      </td>
                      <td class="pL30 pR30 pT20 pB20"></td>
                      <td class="pL30 pR30 pT20 pB20"></td>
                      <td class="text-right">
                        <div
                          class="flex-middle visibility-hide-actions text-right justify-content-end pR10"
                        >
                          <div>
                            <i
                              @click="handleactions('edit', key)"
                              class="el-icon-edit visibility-hide-actions edit-icon-grey pL5 mT10"
                            ></i>
                          </div>
                          <div
                            @click="handleactions('delete', key)"
                            class="mT10 pL10"
                          >
                            <img
                              src="~assets/remove-icon.svg"
                              height="16"
                              width="16"
                            />
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div class>
                  <el-button
                    class="fc-btn-green-medium-border mT30"
                    @click="addTrigger"
                    >Add trigger</el-button
                  >
                </div>
                <div class="pT30">
                  <div class="fc-text-pink">ASSIGNMENT</div>
                  <el-row>
                    <el-col :span="4">
                      <div class="label-txt-black pT10">Default Assignee</div>
                    </el-col>
                    <el-col :span="6">
                      <el-select
                        v-model="model.triggerData.defaultAssignedTo"
                        clearable
                        filterable
                        class="fc-input-full-border-select2 width100 mR10 background-hide mR10"
                        @change="updateResourceList"
                      >
                        <el-option
                          v-for="(user, key) in userList"
                          :key="key"
                          :label="user.name"
                          :value="user.ouid"
                        ></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                </div>
                <div class="pT30">
                  <div class="flex-middle justify-content-space">
                    <div class="fc-text-pink">NOTIFICATIONS</div>
                  </div>
                  <div class="mB30 pT20">
                    <el-row
                      v-for="(reminder, key) in model.triggerData.reminders"
                      :key="key"
                      class="pm-form-notifications-row visibility-visible-actions border-bottom2 pT10 pB10"
                    >
                      <el-col :span="9" class="pT5 pB5 p10">{{
                        model.triggerData.reminders[key].name
                      }}</el-col>
                      <el-col :span="6" class="pT5 pB5">{{
                        templateOptions[key].label
                      }}</el-col>
                      <el-col :span="6" class="fc-tag fc-tag-pm">
                        <!-- <el-tag v-if="model.triggerData.reminders[key].isEmail"
                          >Email</el-tag
                        >
                        <el-tag v-if="model.triggerData.reminders[key].isMobile"
                          >Mobile</el-tag
                        >
                        <el-tag v-if="model.triggerData.reminders[key].isSms"
                          >SMS</el-tag
                        > -->
                      </el-col>
                      <el-col
                        :span="3"
                        class="visibility-hide-actions text-right flex-middle justify-content-end pR20"
                      >
                        <i
                          @click="showEditNotificationConfiguration(key)"
                          class="el-icon-edit visibility-hide-actions edit-icon-grey pR10 f16"
                        ></i>
                        <span
                          @click="removeReminder(key)"
                          class="visibility-hide-actions"
                        >
                          <img
                            src="~assets/remove-icon.svg"
                            height="16"
                            width="16"
                            class="mT5"
                          />
                        </span>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <el-button
                  class="fc-btn-green-medium-border"
                  @click="showAddNotificationConfiguration"
                  >ADD NOTIFICATION</el-button
                >
              </div>
            </div>
          </div>
          <div class="modal-dialog-footer">
            <el-button class="modal-btn-cancel" @click="moveToPrevious"
              >PREVIOUS</el-button
            >
            <el-button type="primary" class="modal-btn-save" @click="moveToNext"
              >PROCEED TO NEXT</el-button
            >
          </div>
        </div>
      </div>
    </div>
    <el-dialog
      v-if="showScheduleSettings"
      :visible.sync="showScheduleSettings"
      :append-to-body="true"
      title
      class="fc-dialog-center-container new-add-triger-dialog fc-dialog-header-hide"
    >
      <div class="positon-relative trigger-dialog-body">
        <trigger
          :trigger="triggerEdit"
          :isSingleWo="model.woData.workOrderType !== 'bulk'"
          :resource="model.woData.singleResource"
        ></trigger>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel" @click="cancelTriggerSave"
          >Cancel</el-button
        >
        <el-button type="primary" class="modal-btn-save" @click="saveTrigger"
          >SAVE</el-button
        >
      </div>
    </el-dialog>
    <el-dialog
      v-if="showReminderCustomization"
      :visible.sync="showReminderCustomization"
      width="40%"
      :append-to-body="true"
      class="fc-dialog-center-container form-customize-dialog"
    >
      <div class="el-tabs-block">
        <el-tabs v-model="activeName">
          <el-tab-pane label="Email" name="email">
            <el-row>
              <el-col :span="24" class="el-select-block">
                <div class="fc-input-label-txt mb5">To</div>
                <el-select
                  v-model="templateEdit[activeName].toAddr"
                  multiple
                  filterable
                  default-first-option
                  collapse-tags
                  placeholder="Select"
                  class="fc-input-full-border-select2 width500px"
                >
                  <el-option
                    v-for="user in notificationUserLists"
                    :key="user.value"
                    :label="user.label"
                    :value="user.value"
                    class="subject"
                    >{{
                      user.displayLabel ? user.displayLabel : user.label
                    }}</el-option
                  >
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="24">
                <div class="fc-input-label-txt mb5">Subject</div>
                <el-input
                  type="text"
                  v-model="templateEdit[activeName].subject"
                  class="fc-input-full-border2"
                  placeholder="Please Enter the Subject"
                ></el-input>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="24">
                <div class="fc-input-label-txt mb5">Message</div>
                <el-input
                  type="textarea"
                  v-model="templateEdit[activeName].message"
                  :autosize="{ minRows: 6, maxRows: 3 }"
                  class="fc-input-txt fc-desc-input fc-input-full-border-textarea"
                  autocomplete="off"
                  :rows="1"
                  resize="none"
                  placeholder="Enter Description"
                ></el-input>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="SMS" name="sms">
            <el-row>
              <el-col :span="24" class="el-select-block">
                <div class="fc-input-label-txt mb5">To</div>
                <el-select
                  v-model="templateEdit[activeName].toAddr"
                  multiple
                  filterable
                  default-first-option
                  collapse-tags
                  placeholder="Select"
                  class="fc-input-full-border-select2 el-select-block width500px"
                >
                  <el-option
                    v-for="user in notificationUserLists"
                    :key="user.value"
                    :label="user.label"
                    :value="user.value"
                    class="subject"
                    >{{
                      user.displayLabel ? user.displayLabel : user.label
                    }}</el-option
                  >
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="24">
                <div class="fc-input-label-txt mb5">Message</div>
                <el-input
                  type="textarea"
                  v-model="templateEdit[activeName].message"
                  :autosize="{ minRows: 6, maxRows: 3 }"
                  class="fc-input-txt fc-desc-input fc-input-full-border-textarea"
                  autocomplete="off"
                  :rows="1"
                  resize="none"
                  placeholder="Enter Description"
                ></el-input>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Mobile" name="mobile">
            <el-row>
              <el-col :span="24" class="el-select-block">
                <div class="fc-input-label-txt mb5">To</div>
                <el-select
                  v-model="templateEdit[activeName].toAddr"
                  multiple
                  filterable
                  default-first-option
                  collapse-tags
                  placeholder="Select"
                  class="fc-input-full-border-select2 el-select-block width500px"
                >
                  <el-option
                    v-for="user in notificationUserLists"
                    :key="user.value"
                    :label="user.label"
                    :value="user.value"
                    class="subject"
                    >{{
                      user.displayLabel ? user.displayLabel : user.label
                    }}</el-option
                  >
                </el-select>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="24">
                <div class="fc-input-label-txt mb5">Subject</div>
                <el-input
                  type="text"
                  v-model="templateEdit[activeName].subject"
                  class="fc-input-full-border2"
                  placeholder="Please Enter the Subject"
                ></el-input>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="24">
                <div class="fc-input-label-txt mb5">Message</div>
                <el-input
                  type="textarea"
                  v-model="templateEdit[activeName].message"
                  :autosize="{ minRows: 6, maxRows: 3 }"
                  class="fc-input-txt fc-desc-input fc-input-full-border-textarea"
                  autocomplete="off"
                  :rows="1"
                  resize="none"
                  placeholder="Enter Description"
                ></el-input>
              </el-col>
            </el-row>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click="
            ;(showReminderCustomization = false),
              (activeName = 'email'),
              (currentTrigger = 0)
          "
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="saveReminderTemplate"
          >Save</el-button
        >
      </div>
    </el-dialog>
    <notification-form
      :showConfigureNotification="showConfigureNotification"
      :reminderTemplateEdit="reminderTemplateEdit"
      :reminderType="reminderType"
      @closeDialog="closeDialog()"
      @addReminder="addReminder"
    ></notification-form>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
import Trigger from '@/PMTrigger'
import NotificationForm from 'pages/workorder/preventive/PMFormNotification'
import SeasonTriggerMixin from '@/mixins/SeasonTriggerMixin'

export default {
  props: ['model', 'users', 'groups'],
  mixins: [PMMixin, SeasonTriggerMixin],
  components: {
    Trigger,
    NotificationForm,
  },
  data() {
    return {
      showScheduleSettings: false,
      triggerIndex: -1,
      dateOptions: {
        disabledDate(time) {
          let today = new Date()
          today.setHours(0, 0, 0, 0)
          let fiveYear = new Date()
          fiveYear.setFullYear(today.getFullYear() + 5)
          return (
            time.getTime() < today.getTime() ||
            time.getTime() > fiveYear.getTime()
          )
        },
      },
      timesOption: [],
      readingFields: [],
      popovers: {},
      triggerPopovers: {},
      showConfigureNotification: false,
      showReminderCustomization: false,
      editNotificationConfiguration: false,
      customizeVisible: false,
      activeName: 'email',
      reminderType: [
        {
          label: 'Before Execution',
          value: 1,
          helpText: 'before execution',
        },
        {
          label: 'After Execution',
          value: 2,
          helpText: 'after execution',
        },
        {
          label: 'Before Due',
          value: 3,
          helpText: 'before due',
        },
        {
          label: 'After Due',
          value: 4,
          helpText: 'after due',
        },
      ],
      notificationEdit: [],
      currentTrigger: 0,
      reminderTemplateEdit: {},
      triggerEdit: {},
      templateEdit: {},
      isEditingTrigger: false,
      searchQuery: '',
    }
  },
  computed: {
    templateOptions() {
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
      this.model.triggerData.reminders.forEach((reminder, i) => {
        options.push({
          label: `${timeLookup[reminder.duration]} ${
            typeLookup[reminder.type]
          }`,
          value: i,
        })
      })
      return options
    },
    ...mapState({
      //groups: state => state.groups,
      //users: state => state.users,
    }),
    triggerTypes() {
      let types = [
        {
          label: 'Schedule',
          value: 1,
        },
        {
          label: 'Reading',
          value: 2,
        },
        {
          label: 'Alarm Condition',
          value: 3,
        },
      ]
      if (this.model.woData.workOrderType === 'single') {
        return types
      }
      types.splice(1, 2)
      return types
    },
    notificationUserLists() {
      if (this.users) {
        let users = this.$helpers
          .cloneObject(this.users)
          .filter(user => user.inviteAcceptStatus)
          .map(user => ({
            label: user.name,
            value: user.id,
            displayLabel: user.name + ' (' + user.email + ')',
          }))
        return users
      }
      return []
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
    userList() {
      let groups = this.$getProperty(this, 'model.woData.woModel.groups', {})
      if (groups && groups.id) {
        let group = this.groups.find(
          group => group.id === Number(this.model.woData.woModel.groups.id)
        )
        if (group) {
          return group.members
        }
      }
      return this.users.filter(user =>
        this.canHandleSite(user, this.model.woData.sites)
      )
    },
  },
  methods: {
    closeDialog() {
      this.showConfigureNotification = false
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
    resetDate(val) {
      this.$forceUpdate()
    },
    assignCriteria(val) {
      this.triggerEdit.criteria = val
      this.$forceUpdate()
    },
    userName(resource) {
      let user = this.userList.find(user => user.ouid === resource.assignedTo)
      if (user) {
        return user.name
      }
      return 'Default'
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    setHourOptions() {
      for (let i = 0; i <= 23; i++) {
        let time = (i < 10 ? '0' + i : i) + ':'
        this.timesOption.push(time + '00')
        this.timesOption.push(time + '30')
      }
    },
    currentTriggerIndex(key) {
      this.triggerIndex = key
    },
    addTrigger() {
      let type = 1
      let startDate = new Date()
      startDate.setHours(0, 0, 0, 0)

      let startTime = startDate.getTime()

      let name = `Trigger ${this.model.triggerData.triggers.length + 1}`

      this.triggerEdit = {
        name: name,
        type: type,
        startDate: startDate,
        startTime: startTime,
        basedOn: 'Date',
        schedule: {
          times: ['00:00'],
          frequency: 1,
          skipEvery: -1,
          values: [],
          frequencyType: 1,
          weekFrequency: -1,
          yearlyDayValue: null,
          monthValue: -1,
          yearlyDayOfWeekValues: [],
        },
        customTrigger: {
          customModuleId: null,
          fieldId: null,
          days: null,
          hours: null,
        },
        stopAfter: 'never',
        endTime: new Date(),
        startReading: null,
        readingFieldId: null,
        stopAfterReading: 'never',
        endReading: null,
        readingInterval: null,
        assignedTo: null,
        reminders: [],
        criteria: null,
        sharingContext: {
          shareTo: 2,
          sharedRoles: [],
          sharedUsers: [],
          sharedGroups: [],
        },
      }

      this.isEditingTrigger = false
      this.triggerIndex = this.triggerIndex + 1
      this.showScheduleSettings = true
    },
    async saveTrigger() {
      if (!(await this.canSaveTrigger())) {
        return
      }
      let currIndex = this.triggerIndex
      if (this.isEditingTrigger) {
        let previous = this.$helpers.cloneObject(
          this.model.triggerData.triggers[this.triggerIndex]
        )
        this.$set(
          this.model.triggerData.triggers,
          this.triggerIndex,
          this.triggerEdit
        )

        // Replace the tigger name in resource planner
        if (previous.name !== this.triggerEdit.name) {
          for (let i = 0; i < this.model.woData.resourceList.length; i++) {
            let triggerNames = this.$helpers.cloneObject(
              this.model.woData.resourceList[i].triggerNames
            )
            if (triggerNames) {
              let index = -1
              for (let k = 0; k < triggerNames.length; k++) {
                if (triggerNames[k] === previous.name) {
                  index = k
                  break
                }
              }
              if (index > -1) {
                triggerNames[index] = this.triggerEdit.name
                this.$set(
                  this.model.woData.resourceList[i],
                  'triggerNames',
                  triggerNames
                )
              }
            }
          }
        }
      } else {
        this.model.triggerData.triggers.push(this.triggerEdit)
        currIndex = this.model.triggerData.triggers.length - 1
        this.triggerIndex = this.triggerIndex + 1
      }
      if (this.model.triggerData.triggers[currIndex].type === 1) {
        if (!this.model.triggerData.triggers[currIndex].schedule.skipEvery) {
          this.model.triggerData.triggers[currIndex].schedule.skipEvery = -1
        }

        if (
          !this.model.triggerData.triggers[currIndex].schedule.yearlyDayValue
        ) {
          this.model.triggerData.triggers[
            currIndex
          ].schedule.yearlyDayValue = -1
        }

        let frequencyType = this.model.triggerData.triggers[currIndex].schedule
          .frequencyType
        frequencyType = this.$constants.FACILIO_FREQUENCY[frequencyType]

        let basedOn = this.model.triggerData.triggers[currIndex].basedOn

        let freqMap = {}
        Object.keys(this.$constants.FACILIO_FREQUENCY).forEach(
          i => (freqMap[this.$constants.FACILIO_FREQUENCY[i]] = i)
        )

        this.model.triggerData.triggers[currIndex].frequency =
          freqMap[frequencyType] || 0

        if (frequencyType === 'Monthly') {
          if (basedOn === 'Date') {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 3
          } else {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 4
          }
        } else if (frequencyType === 'Quarterly') {
          if (basedOn === 'Date') {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 7
          } else {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 8
          }
        } else if (frequencyType === 'Half Yearly') {
          if (basedOn === 'Date') {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 9
          } else {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 10
          }
        } else if (frequencyType === 'Annually') {
          if (basedOn === 'Date') {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 5
          } else {
            this.model.triggerData.triggers[
              currIndex
            ].schedule.frequencyType = 6
          }
        }

        delete this.model.triggerData.triggers[currIndex].schedule.basedOn
      }

      this.showScheduleSettings = false
      this.isEditingTrigger = false
    },
    resourceChanged(newVal, oldVal) {
      if (
        newVal.workOrderType !== oldVal.workOrderType ||
        newVal.resourceType !== oldVal.resourceType ||
        newVal.spacecategoryId !== oldVal.spacecategoryId ||
        newVal.assetCategoryId !== oldVal.assetCategoryId
      ) {
        return true
      }
      return false
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    moveToNext() {
      this.$emit('next')
    },
    selectedUser(key, userId) {
      this.model.woData.resourceList[key].assignedTo = userId
      this.updateResourceList()
      this.popovers[key] = false
    },
    updateResourceList() {
      let resourceList = this.$helpers.cloneObject(
        this.model.woData.resourceList
      )
      this.$set(this.model.woData, 'resourceList', resourceList)
    },
    selectedTrigger(key, index) {
      this.model.woData.resourceList[key].triggerIndex = index
      this.triggerPopovers[key] = false
    },
    getFloorReadings() {
      this.$http
        .post('/reading/getallspacetypereadings', {
          spaceType: 'Floors',
        })
        .then(response => {
          for (let categoryId in response.data.moduleMap) {
            let moduleJson = response.data.moduleMap[categoryId]
            for (let moduleIndex in moduleJson) {
              let readingFields = moduleJson[moduleIndex].fields
              for (let fieldIndex in readingFields) {
                let field = readingFields[fieldIndex]
                if (categoryId === -1) {
                  field['spaceId'] = 'Default Readings'
                  field.buildingName = 'All'
                } else {
                  field['spaceId'] = response.data.spaces[categoryId].name
                  field.buildingName =
                    response.data.spaces[categoryId].building.name
                }
                this.readingFields.push(field)
              }
            }
          }
        })
    },
    handleSingleResource() {
      if (this.model.woData.workOrderType === 'single') {
        if (
          this.model.woData.singleResource &&
          this.model.woData.singleResource.id
        ) {
          if (this.model.woData.singleResource.resourceTypeEnum === 'SPACE') {
            this.$util
              .loadSpaceReadingFields(
                this.model.woData.singleResource.id,
                false
              )
              .then(fields => {
                this.readingFields = fields
              })
          } else if (
            this.model.woData.singleResource.resourceTypeEnum === 'ASSET'
          ) {
            this.$util
              .loadAssetReadingFields(
                this.model.woData.singleResource.id,
                -1,
                false
              )
              .then(fields => {
                this.readingFields = fields
              })
          }
        }
      }
    },
    removeReminder(index) {
      let reminderName = this.model.triggerData.reminders[index].name
      this.model.triggerData.reminders.splice(index, 1)
      // removing notifications assigned to resources
      if (
        this.model.woData.workOrderType === 'bulk' &&
        this.model.woData.resourceList
      ) {
        for (let i = 0; i < this.model.woData.resourceList.length; i++) {
          if (this.model.woData.resourceList[i].notifications) {
            for (
              let j = 0;
              j < this.model.woData.resourceList[i].notifications.length;
              j++
            ) {
              if (
                this.model.woData.resourceList[i].notifications[j] ===
                reminderName
              ) {
                this.$delete(this.model.woData.resourceList[i].notifications, j)
              }
            }
          }
        }
      }
    },
    addReminder(reminderTemplateEdit) {
      this.showConfigureNotification = false
      if (this.editNotificationConfiguration) {
        Object.assign(
          this.model.triggerData.reminders[reminderTemplateEdit.index],
          this.$helpers.cloneObject(reminderTemplateEdit)
        )
      } else {
        this.model.triggerData.reminders.push(
          this.$helpers.cloneObject(reminderTemplateEdit)
        )
      }
    },
    handleactions(val, key) {
      if (val === 'edit') {
        this.isEditingTrigger = true
        this.triggerEdit = this.$helpers.cloneObject(
          this.model.triggerData.triggers[key]
        )

        if (this.triggerEdit.type === 1) {
          let frequencyType = this.triggerEdit.schedule.frequencyType
          if (!(this.triggerEdit.schedule.frequencyType % 2)) {
            this.triggerEdit.basedOn = 'Week'
          } else {
            this.triggerEdit.basedOn = 'Date'
          }

          if (frequencyType === 3 || frequencyType === 4) {
            this.triggerEdit.schedule.frequencyType = 3
          } else if (frequencyType === 7 || frequencyType === 8) {
            this.triggerEdit.schedule.frequencyType = 4
          } else if (frequencyType === 9 || frequencyType === 10) {
            this.triggerEdit.schedule.frequencyType = 5
          } else if (frequencyType === 5 || frequencyType === 6) {
            this.triggerEdit.schedule.frequencyType = 6
          }
        }

        this.showScheduleSettings = true
      } else if (val === 'delete') {
        if (this.model.triggerData.triggers.length > 0) {
          let triggerName = this.model.triggerData.triggers[key].name
          this.model.triggerData.triggers.splice(key, 1)
          // deleting triggers from resources planner
          if (
            this.model.woData.workOrderType === 'bulk' &&
            this.model.woData.resourceList
          ) {
            let rList = this.$helpers.cloneObject(
              this.model.woData.resourceList
            )
            for (let i = 0; i < rList.length; i++) {
              let zi = rList[i].triggerNames.findIndex(l => l === triggerName)
              if (zi > -1) {
                rList[i].triggerNames.splice(zi, 1)
                if (rList[i].triggerNames.length === 0) {
                  rList[i].triggerNames.push('0')
                }
              }
            }
            this.$set(this.model.woData, 'resourceList', rList)
          }

          // delete associated trigger in task
          for (let j = 0; j < this.model.taskData.taskSections.length; j++) {
            let index = -1
            let triggers = this.$helpers.cloneObject(
              this.model.taskData.taskSections[j].triggers
            )
            for (let k = 0; k < triggers.length; k++) {
              index = triggers.findIndex(
                n => n !== 'All' && n.triggerName === triggerName
              )
              if (index > -1) {
                triggers.splice(index, 1)
                if (triggers.length === 0) {
                  triggers.push({ triggerName: 'All' })
                }
                this.$set(
                  this.model.taskData.taskSections[j],
                  'triggers',
                  triggers
                )
                break
              }
            }
          }
        }
      }
    },
    cancelTriggerSave() {
      this.isEditingTrigger = false
      this.showScheduleSettings = false
    },
    showEditNotificationConfiguration(index) {
      this.editNotificationConfiguration = true
      this.reminderTemplateEdit = {
        name: '',
        type: null,
        duration: null,
        isEmail: false,
        isSms: false,
        isMobile: false,
        template: {
          email: {},
          mobile: {},
          sms: {},
        },
        reminderActions: [],
      }
      this.reminderTemplateEdit.index = index
      Object.assign(
        this.reminderTemplateEdit,
        this.model.triggerData.reminders[index]
      )
      this.showConfigureNotification = true
    },
    showAddNotificationConfiguration() {
      this.editNotificationConfiguration = false
      let count = this.model.triggerData.reminders.length
      this.reminderTemplateEdit = {
        name: `Notification ${count + 1}`,
        type: 2,
        duration: 3600,
        isEmail: true,
        isSms: false,
        isMobile: true,
        template: {
          email: {},
          mobile: {},
          sms: {},
        },
        reminderActions: [],
      }
      this.showConfigureNotification = true
    },
    showReminderTemplate(key) {
      this.templateEdit = {
        email: {
          toAddr: [],
          subject: '',
          message: '',
        },
        mobile: {
          toAddr: [],
          subject: '',
          message: '',
        },
        sms: {
          toAddr: [],
          message: '',
        },
        key: key,
      }

      if (this.editNotificationConfiguration) {
        if (this.model.triggerData.reminders[key].template.email) {
          Object.assign(
            this.templateEdit.email,
            this.model.triggerData.reminders[key].template.email
          )
        }

        if (this.model.triggerData.reminders[key].template.mobile) {
          Object.assign(
            this.templateEdit.mobile,
            this.model.triggerData.reminders[key].template.mobile
          )
        }

        if (this.model.triggerData.reminders[key].template.sms) {
          Object.assign(
            this.templateEdit.sms,
            this.model.triggerData.reminders[key].template.sms
          )
        }
      } else {
        if (this.reminderTemplateEdit.template.email) {
          Object.assign(
            this.templateEdit.email,
            this.reminderTemplateEdit.template.email
          )
        }

        if (this.reminderTemplateEdit.template.mobile) {
          Object.assign(
            this.templateEdit.mobile,
            this.reminderTemplateEdit.template.mobile
          )
        }

        if (this.reminderTemplateEdit.template.sms) {
          Object.assign(
            this.templateEdit.sms,
            this.reminderTemplateEdit.template.sms
          )
        }
      }

      this.showReminderCustomization = true
    },
    async saveReminderTemplate() {
      Object.assign(
        this.reminderTemplateEdit.template.email,
        this.templateEdit.email
      )
      Object.assign(
        this.reminderTemplateEdit.template.mobile,
        this.templateEdit.mobile
      )
      Object.assign(
        this.reminderTemplateEdit.template.sms,
        this.templateEdit.sms
      )
      this.showReminderCustomization = false
      this.activeName = 'email'
    },
  },
  watch: {
    'model.woData.spacecategoryId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal && newVal) {
          if (this.model.woData.workOrderType === 'bulk') {
            this.$util
              .loadSpaceReadingFields(
                -1,
                false,
                Number(this.model.woData.spacecategoryId)
              )
              .then(fields => {
                this.readingFields = fields
              })
          }
        }
      },
      deep: true,
    },
    'model.woData.assetCategoryId': {
      handler: function(newVal, oldVal) {
        if (newVal !== oldVal && newVal) {
          if (this.model.woData.workOrderType === 'bulk') {
            this.$util
              .loadAssetReadingFields(
                -1,
                false,
                Number(this.model.woData.assetCategoryId)
              )
              .then(fields => {
                this.readingFields = fields
              })
          }
        }
      },
      deep: true,
    },
    'model.woData.resourceType': {
      deep: true,
      handler: function(newVal, oldVal) {
        if (this.model.woData.workOrderType === 'bulk') {
          if (this.model.woData.resourceType === 'ALL_FLOORS') {
            this.getFloorReadings()
          }
        }
      },
    },
    'model.woData.workOrderType': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleSingleResource()
      },
    },
    'model.woData.singleResource': {
      deep: true,
      handler: function(newVal, oldVal) {
        this.handleSingleResource()
      },
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
    //this.$store.dispatch('loadGroups') // users/groups sent from pmnewform.vue as props
  },
  mounted() {
    this.setHourOptions()
  },
}
</script>
<style lang="scss">
.pm-trigger-form {
  .pm-trigger-dialog-notifications .el-dialog__header {
    padding-left: 40px !important;
    max-height: 360px !important;
    padding-bottom: 20px;
  }

  .trigger-dialog-body {
    height: 100%;
    max-height: 600px;
    overflow: hidden;
  }

  .pm-trigger-dialog-notifications .el-dialog__body {
    max-height: 400px;
    padding-left: 20px !important;
    padding-right: 20px !important;
  }

  .pm-form-notifications-row {
    padding: 10px 0px;
  }

  .pm-form-notifications-row:hover {
    border-radius: 3px;
    cursor: pointer;
    background-color: #f1f8fa;
    transition: background 0.3s all ease-in-out;
    -webkit-transition: background 0.3s all ease-in-out;
  }

  .form-customize-dialog .el-dialog__body {
    padding-left: 0 !important;
    padding-right: 0 !important;
    overflow: hidden;
    height: 70vh;
  }

  .date-picker-trigger .el-input__inner {
    padding-left: 30px;
  }

  .el-caret-down-icon .el-input .el-icon-arrow-up {
    color: #5a7591;
    visibility: hidden;
  }

  .trigger-tr-visibility:hover .el-input .el-icon-arrow-up {
    visibility: visible;
  }

  .custom-txt {
    position: absolute;
    top: 23px;
    right: 70px;
    color: #ee518f;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
  }

  .custom-txt:hover {
    text-decoration: underline;
    text-decoration-color: #ee518f;
    cursor: pointer;
  }

  .notification-trigger-dialog .el-dialog__body {
    height: 50vh;
    overflow: hidden;
  }
}

.trigger-dialog-body {
  height: 100%;
  max-height: 700px;
  overflow-y: scroll;
  overflow-x: hidden;
}

.new-add-triger-dialog .el-dialog__body {
  padding: 0;
}
.new-add-triger-dialog .el-dialog {
  width: 70% !important;
}

@media screen and (max-width: 1280px) and (min-width: 800px) {
  .notification-trigger-dialog .el-dialog {
    width: 45% !important;
  }
}
</style>
