<template>
  <div class="scrollable-y100">
    <div class="fc-pm-form-container">
      <div class="fc-pm-form-right-main">
        <div v-if="!model.isEdit" class="fc-grey-text mT10">
          NEW PLANNED MAINTENANCE
        </div>
        <div v-else>
          EDIT PLANNED MAINTENANCE
        </div>
        <div class="heading-black22 mB20">
          Maintenance for {{ resourceName() }}
        </div>
        <div class="fc-pm-main-bg fc-pm-main-bg2">
          <div class="fc-pm-main-content">
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
            <div class="fc-pm-main-inner-container">
              <div
                v-if="!model.triggerData.triggers.length"
                class="text-center mT20"
              >
                <div>
                  <img
                    src="~statics/icons/triggers_icon.svg"
                    style="width: 49px;height: 49px;"
                  />
                </div>
                <div class="trigger-empty-state">
                  {{ $t('maintenance.pm_list.schedule_your_maintenance') }}
                </div>
                <div class="trigger-empty-state-subtext">
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
                      <th class="pT10 pB10"></th>
                      <th class="pT10 pB10"></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr
                      v-for="(trigger, key) in model.triggerData.triggers"
                      :key="key"
                      @click="currentTriggerIndex(key)"
                      class="visibility-visible-actions pointer table-hover"
                    >
                      <td class="pT10 pB10">
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
                            key === 0 && !model.triggerData.isDefaultAllTriggers
                          "
                          class="width200px fc-tag fL mL10"
                        >
                          <el-tag>Default</el-tag>
                        </div>
                      </td>
                      <td></td>
                      <td></td>
                      <td class="text-right pT10 pB10">
                        <el-col :span="4" class="visibility-visible-actions">
                          <i
                            @click="handleactions('edit', key)"
                            class="el-icon-edit visibility-hide-actions edit-icon-grey pL5 mT10"
                          ></i>
                        </el-col>
                        <el-col :span="3" class="visibility-hide-actions">
                          <div
                            @click="handleactions('delete', key)"
                            class="mT10 pL20 fL"
                          >
                            <img
                              src="~assets/remove-icon.svg"
                              height="16"
                              width="16"
                            />
                          </div>
                        </el-col>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div>
                  <el-button
                    class="fc-btn-green-medium-border mT30"
                    @click="addTrigger"
                    >ADD TRIGGER</el-button
                  >
                </div>
                <div class="fc-pm-border mT40 mB30"></div>
                <div class="fc-text-pink">ASSIGNMENT</div>
                <el-row>
                  <el-col :span="4">
                    <div class="label-txt-black bold pT10">
                      Default Assignee
                    </div>
                  </el-col>
                  <el-col :span="10">
                    <el-select
                      v-model="model.triggerData.defaultAssignedTo"
                      clearable
                      filterable
                      class="fc-input-full-border-select2 width170px mR10 background-hide mR10"
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
                <div class="fc-pm-border mT40 mB30"></div>
                <div class="fc-text-pink">NOTIFICATIONS</div>
                <div class="mB30">
                  <el-row
                    v-for="(reminder, key) in model.triggerData.reminders"
                    :key="key"
                    class="pm-form-notifications-row visibility-visible-actions border-bottom2"
                  >
                    <el-col :span="9" class="pT5 pB5">
                      {{ model.triggerData.reminders[key].name }}
                    </el-col>
                    <el-col :span="6" class="pT5 pB5">
                      {{ templateOptions[key].label }}
                    </el-col>
                    <el-col :span="6" class="fc-tag fc-tag-pm">
                      <el-tag v-if="model.triggerData.reminders[key].isEmail"
                        >Email</el-tag
                      >
                      <el-tag v-if="model.triggerData.reminders[key].isMobile"
                        >Mobile</el-tag
                      >
                      <el-tag v-if="model.triggerData.reminders[key].isSms"
                        >SMS</el-tag
                      >
                    </el-col>
                    <el-col :span="3" class="visibility-hide-actions">
                      <i
                        @click="showEditNotificationConfiguration(key)"
                        class="el-icon-edit visibility-hide-actions edit-icon-grey pL5 pR5 f16"
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
      title="TRIGGER DETAILS"
      class="fc-dialog-center-container add-triger-dialog"
    >
      <el-row>
        <el-col>
          <div class="fc-input-label-txt mb5">Name</div>
          <div>
            <el-input
              v-model="triggerEdit.name"
              class="fc-input-full-border2"
            ></el-input>
          </div>
        </el-col>
      </el-row>
      <el-row class="mT25">
        <el-col>
          <div class="fc-input-label-txt mb5">Trigger Type</div>
          <div>
            <el-select
              v-model="triggerEdit.type"
              @change=";(triggerEdit.schedule = null), forceUpdate()"
              :disabled="model.woData.workOrderType === 'bulk'"
              style="width:100%"
              class="form-item fc-input-full-border-select2"
              placeholder=" "
            >
              <el-option
                v-for="triggerType in triggerTypes"
                v-show="triggerType.value !== 1 || triggerEdit.type === 1"
                :key="triggerType.value"
                :label="triggerType.label"
                :value="triggerType.value"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <el-row v-if="triggerEdit.type === 1" class="mT25">
        <el-col :span="24">
          <div class="fc-input-label-txt mb5">Execution Start Date</div>
          <el-col :span="12">
            <el-date-picker
              @input="resetDate"
              v-model="triggerEdit.startDate"
              :picker-options="dateOptions"
              class="form-item fc-input-full-border-select2 date-picker-trigger"
              style="width:100%"
              float-label="Start Date"
              type="date"
              suffix-icon="el-icon-date"
            />
          </el-col>
          <el-col :span="12" class="el-select-block">
            <el-select
              @change="forceUpdate"
              class="form-item pL10 fc-input-full-border-select2"
              style="width:100%"
              multiple
              v-model="triggerEdit.startHour"
              :multiple-limit="
                triggerEdit.schedule
                  ? parseInt(triggerEdit.schedule.facilioFrequency) === 0
                    ? 1
                    : 0
                  : 0
              "
              :collapse-tags="true"
            >
              <el-option
                v-for="time in timesOption"
                :label="time"
                :value="time"
                :key="time"
              ></el-option>
            </el-select>
          </el-col>
        </el-col>
      </el-row>
      <f-schedule
        v-if="triggerEdit.type === 1"
        :from="triggerEdit.startDate"
        :times="triggerEdit.startHour"
        v-model="triggerEdit.schedule"
        :initialSchedule="triggerEdit.initialSchedule"
        class="mT25"
      ></f-schedule>
      <el-row v-if="triggerEdit.type === 2" class="mT25">
        <el-col :span="24">
          <div class="fc-input-label-txt mb5">
            {{ $t('maintenance._workorder.reading_field') }}
          </div>
          <el-select
            filterable
            v-model="triggerEdit.readingFieldId"
            style="width:100%"
            @change="forceUpdate"
            class="form-item fc-input-full-border-select2"
            placeholder=" "
          >
            <el-option
              v-for="readingField in readingFields"
              :key="readingField.id"
              :label="readingField.displayName"
              :value="readingField.id"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row v-if="triggerEdit.type === 2" class="mT25">
        <el-col :span="24">
          <div class="fc-input-label-txt mb5">
            {{ $t('maintenance._workorder.every') }}
          </div>
          <el-input
            v-model="triggerEdit.readingInterval"
            class="fc-input-full-border2"
            type="number"
            placeholder=""
          >
          </el-input>
        </el-col>
      </el-row>
      <el-row v-if="triggerEdit.type === 2" class="mT25">
        <el-col :span="24">
          <div class="fc-input-label-txt mb5">
            {{ $t('maintenance._workorder.start_reading') }}
          </div>
          <el-input
            v-model="triggerEdit.startReading"
            class="fc-input-full-border2"
            type="number"
            placeholder=""
          >
          </el-input>
        </el-col>
      </el-row>
      <el-row v-if="triggerEdit.type === 3" class="mT25">
        <new-criteria-builder
          :exrule="triggerEdit.criteria"
          @condition="assignCriteria"
          module="alarm"
        ></new-criteria-builder>
      </el-row>
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
                  class="fc-input-full-border-select2  width500px"
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
                  autoComplete="off"
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
                  autoComplete="off"
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
                  autoComplete="off"
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
    <el-dialog
      v-if="showConfigureNotification"
      title="NOTIFICATION"
      :visible.sync="showConfigureNotification"
      width="40%"
      :append-to-body="true"
      class="fc-dialog-center-container notification-trigger-dialog"
    >
      <!-- <div class="custom-txt" @click="showReminderTemplate(reminderTemplateEdit.index)">Customize Templates</div> -->
      <el-row class="">
        <div class="fc-input-label-txt mb5">Name</div>
        <el-col :span="24">
          <el-input
            v-model="reminderTemplateEdit.name"
            class="fc-input-full-border-select3"
          ></el-input>
        </el-col>
      </el-row>
      <el-row class="mT25">
        <el-col :span="11">
          <div class="fc-input-label-txt mb5">Duration</div>
          <el-select
            v-model="reminderTemplateEdit.duration"
            placeholder="Select"
            class="fc-input-full-border-select3 width240px"
          >
            <el-option
              v-for="item in generateReminderOptions()"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
        <el-col :span="8" class="mL45">
          <div class="fc-input-label-txt mb5">Type</div>
          <el-select
            v-model="reminderTemplateEdit.type"
            placeholder="Select"
            class="fc-input-full-border-select3 width240px"
          >
            <el-option
              v-for="item in reminderType"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
      <el-row class="mT25">
        <!-- <el-col :span="3">Email</el-col> -->
        <el-col :span="4">
          <el-checkbox v-model="reminderTemplateEdit.isEmail" class="mT10"
            >Email</el-checkbox
          >
        </el-col>
        <!-- <el-col :span="3">SMS</el-col> -->
        <el-col :span="4">
          <el-checkbox v-model="reminderTemplateEdit.isSms" class="mT10"
            >SMS</el-checkbox
          >
        </el-col>
        <!-- <el-col :span="3">Mobile</el-col> -->
        <el-col :span="3">
          <el-checkbox v-model="reminderTemplateEdit.isMobile" class="mT10"
            >Mobile</el-checkbox
          >
        </el-col>
      </el-row>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel f13"
          @click="showConfigureNotification = false"
          >Cancel</el-button
        >
        <el-button class="modal-btn-save f13" @click="addReminder"
          >Save</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import FSchedule from '@/FSchedule'
import NewCriteriaBuilder from '@/NewCriteriaBuilder'
import { mapState } from 'vuex'
import PMMixin from '@/mixins/PMMixin'
export default {
  props: ['model', 'users', 'groups'],
  mixins: [PMMixin],
  components: {
    FSchedule,
    NewCriteriaBuilder,
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
      notificationEdit: [],
      currentTrigger: 0,
      reminderTemplateEdit: {},
      triggerEdit: {},
      templateEdit: {},
      isEditingTrigger: false,
      searchQuery: '',
    }
  },
  created() {
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
    //this.$store.dispatch('loadGroups') // users/groups sent from store pmnewform.vue as props.
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
      // groups: state => state.groups,
      // users: state => state.users,
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
  },
  methods: {
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

      let name = `Trigger ${this.model.triggerData.triggers.length + 1}`

      this.triggerEdit = {
        name: name,
        type: type,
        startHour: ['00:00'],
        startDate: startDate,
        startTime: null,
        schedule: null,
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
      }

      this.isEditingTrigger = false
      this.triggerIndex = this.triggerIndex + 1
      this.showScheduleSettings = true
    },
    saveTrigger() {
      if (this.isEditingTrigger) {
        let previous = this.$helpers.cloneObject(
          this.model.triggerData.triggers[this.triggerIndex]
        )
        this.$set(
          this.model.triggerData.triggers,
          this.triggerIndex,
          this.triggerEdit
        )
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
        this.triggerIndex = this.triggerIndex + 1
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
        .post('/reading/getallspacetypereadings', { spaceType: 'Floors' })
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
    addReminder() {
      this.showConfigureNotification = false
      if (this.editNotificationConfiguration) {
        Object.assign(
          this.model.triggerData.reminders[this.reminderTemplateEdit.index],
          this.$helpers.cloneObject(this.reminderTemplateEdit)
        )
      } else {
        this.model.triggerData.reminders.push(
          this.$helpers.cloneObject(this.reminderTemplateEdit)
        )
      }
    },
    handleactions(val, key) {
      if (val === 'edit') {
        this.isEditingTrigger = true
        this.triggerEdit = Object.assign(
          {},
          this.model.triggerData.triggers[key]
        )
        this.triggerEdit.initialSchedule = this.$helpers.cloneObject(
          this.model.triggerData.triggers[key].schedule
        )
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
        template: { email: {}, mobile: {}, sms: {} },
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
        template: { email: {}, mobile: {}, sms: {} },
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
    saveReminderTemplate() {
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
  mounted() {
    this.setHourOptions()
  },
}
</script>
<style>
.pm-trigger-dialog-notifications .el-dialog__header {
  padding-left: 40px !important;
  max-height: 360px !important;
  padding-bottom: 20px;
}
.pm-trigger-dialog-notifications .el-dialog {
  padding-bottom: 70px !important;
}
.pm-trigger-dialog-notifications .el-dialog__body {
  max-height: 400px;
  padding-left: 20px !important;
  padding-right: 20px !important;
}
.pm-form-notifications-row {
  padding: 10px 20px;
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
.add-triger-dialog .el-dialog__body {
  padding-bottom: 100px;
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
@media screen and (max-width: 1280px) and (min-width: 800px) {
  .notification-trigger-dialog .el-dialog {
    width: 45% !important;
  }
}
</style>
