<template>
  <div class="scrollable-y100">
    <div class="fc-pm-form-right-main">
      <div v-if="!model.isEdit" class="fc-grey-text mT10">
        NEW PLANNED MAINTENANCE
      </div>
      <div v-else class="fc-grey-text mT10">EDIT PLANNED MAINTENANCE</div>
      <div class="heading-black22 mB20">
        Maintenance for {{ resourceName() }}
      </div>
      <div class="fc-pm-main-bg fc-pm-main-bg2">
        <div class="fc-pm-main-content">
          <div class="fc-text-pink">OVERVIEW</div>
          <div class="fc-pm-main-inner-container">
            <div class="fc-pm-task-form-table-con">
              <div class="review-wrap-block">
                <el-row class="pB15">
                  <el-col :span="12">
                    <div class="fc-red-txt14 pB5">Sites</div>
                    <div class="label-txt-black">
                      {{ siteName !== '' ? siteName : '' }}
                    </div>
                  </el-col>
                  <el-col
                    v-if="model.woData.workOrderType === 'single'"
                    :span="12"
                  >
                    <div class="pB5 fc-summary-content-header">
                      Space / Asset
                    </div>
                    <div class="label-txt-black">
                      {{ model.woData.spaceAssetDisplayName }}
                    </div>
                  </el-col>
                  <el-col v-else-if="model.woData.selectedBuilding" :span="12">
                    <div class="pB5 fc-summary-content-header">Building</div>
                    <div class="label-txt-black">{{ buildingName }}</div>
                  </el-col>
                </el-row>
                <el-row class="pB15 pT15">
                  <el-col
                    v-if="model.woData.resourceType === 'ALL_FLOORS'"
                    :span="12"
                  >
                    <div class="pB5 fc-summary-content-header">Floors</div>
                    <div class="label-txt-black">
                      {{ resourceListLength }} Floors
                    </div>
                  </el-col>
                  <el-col
                    v-if="model.woData.resourceType === 'SPACE_CATEGORY'"
                    :span="12"
                  >
                    <div class="pB5 fc-summary-content-header">
                      Space Category
                    </div>
                    <div class="label-txt-black">
                      {{ spacecategory[model.woData.spacecategoryId] }}({{
                        model.woData.resourceList.length
                      }})
                    </div>
                  </el-col>
                  <el-col
                    v-if="model.woData.resourceType === 'ASSET_CATEGORY'"
                    :span="12"
                  >
                    <div class="pB5 fc-summary-content-header">
                      Asset Category
                    </div>
                    <div class="label-txt-black">
                      {{ assetCategoryName }} ({{
                        model.woData.resourceList.length
                      }})
                    </div>
                  </el-col>
                </el-row>
                <el-row class="pB15 pT15">
                  <el-col :span="12">
                    <div class="pB5 fc-summary-content-header">
                      Task Template
                    </div>
                    <div class="label-txt-black">
                      {{ model.taskData.taskSections.length }}
                    </div>
                  </el-col>
                  <el-col :span="12">
                    <div class="pB5 fc-summary-content-header">
                      No of Triggers
                    </div>
                    <div class="label-txt-black">
                      {{ model.triggerData.triggers.length }}
                    </div>
                  </el-col>
                </el-row>
              </div>
              <div
                v-if="
                  model.triggerData.triggers &&
                    model.triggerData.triggers.length
                "
                class="fc-text-pink mT50"
              >
                RESOURCE PLAN
              </div>
              <div
                v-if="
                  model.triggerData.triggers &&
                    model.triggerData.triggers.length
                "
                class="review-plan-container-scroll mT20"
              >
                <el-row
                  class="border-bottom2 pT10 pB10 visibility-visible-actions pointer"
                  v-for="(resource, index) in model.woData.resourceList"
                  :key="index"
                >
                  <el-col :span="10">
                    <div class="label-txt-black pT10 pB10">
                      {{ resource.name }}
                    </div>
                  </el-col>
                  <template
                    v-if="model.woData.resourceList[index].triggerNames.length"
                  >
                    <template
                      v-if="
                        model.woData.resourceList[index].triggerNames.length > 1
                      "
                    >
                      <el-col :span="4">
                        <el-tooltip
                          class="item"
                          effect="dark"
                          placement="bottom"
                        >
                          <div
                            class="fc-grey-text14 pT5 pB5 textoverflow-ellipsis"
                          >
                            {{
                              `${model.woData.resourceList[index].triggerNames.length} Triggers`
                            }}
                          </div>
                          <div
                            slot="content"
                            v-for="(item, index) in model.woData.resourceList[
                              index
                            ].triggerNames"
                            :key="index"
                          >
                            {{ item }}
                          </div>
                        </el-tooltip>
                      </el-col>
                    </template>
                    <template
                      v-else-if="
                        model.woData.resourceList[index].triggerNames[0] === '0'
                      "
                    >
                      <el-col :span="4">
                        <div
                          v-if="!model.triggerData.isDefaultAllTriggers"
                          class="fc-grey-text14 pT5 pB5 textoverflow-ellipsis"
                        >
                          {{ `${model.triggerData.triggers[0].name}` }}
                        </div>
                        <div
                          class="fc-grey-text14 textoverflow-ellipsis pT5 pB5"
                          v-else
                        >
                          All Triggers
                        </div>
                      </el-col>
                    </template>
                    <template v-else>
                      <el-col :span="4">
                        <div
                          class="fc-grey-text14 textoverflow-ellipsis pT5 pB5"
                        >
                          {{
                            `${model.woData.resourceList[index].triggerNames[0]}`
                          }}
                        </div>
                      </el-col>
                    </template>
                  </template>
                  <template v-else>
                    <el-col :span="4">
                      <div class="fc-grey-text14 textoverflow-ellipsis pT5 pB5">
                        {{ `${model.triggerData.triggers[0].name}` }}
                      </div>
                    </el-col>
                  </template>
                  <el-col :span="7">
                    <user-avatar
                      size="md"
                      :user="
                        userObj(model.woData.resourceList[index].assignedTo)
                      "
                      :showPopover="false"
                      :name="true"
                    ></user-avatar>
                  </el-col>
                  <el-col :span="3">
                    <div>
                      <el-tooltip
                        class="item"
                        effect="dark"
                        placement="bottom"
                        :popper-class="
                          model.woData.resourceList[index].notifications.length
                            ? 'element-tooltip'
                            : 'hide'
                        "
                      >
                        <div
                          v-for="(item, k) in templateOp(
                            model.woData.resourceList[index].notifications
                          )"
                          :key="k"
                          slot="content"
                        >
                          {{
                            `${model.woData.resourceList[index].notifications[k]} (${item})`
                          }}
                        </div>
                        <img
                          v-if="
                            model.woData.resourceList[index].notifications
                              .length > 0
                          "
                          src="~assets/bell-fill.svg"
                          width="15"
                          height="15"
                          class="mT7"
                        />
                        <img
                          v-else
                          src="~assets/bell.svg"
                          width="15"
                          height="15"
                          class="mT7"
                        />
                      </el-tooltip>
                    </div>
                  </el-col>
                </el-row>
              </div>
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
            :loading="isSaving"
            @click="triggerValidation"
            >CONFIRM</el-button
          >
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import UserAvatar from '@/avatar/User'
import PMMixin from '@/mixins/PMMixin'
import { getFieldOptions } from 'util/picklist'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: ['model', 'formObj', 'users', 'groups'],
  mixins: [PMMixin],
  components: { UserAvatar },
  created() {
    this.$store.dispatch('loadRoles')
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadBuildings')
    //this.$store.dispatch('loadGroups') // users/groups sent from store pmnewform.vue as props.
  },
  data() {
    return {
      isSaving: false,
      siteOptions: [],
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
      config: {
        email: {
          type: 1, // Template type
          actionType: 3,
          label: 'Send Email Action',
          name: 'New WorkOrder Email Template',
        },
        sms: {
          type: 2, // Template type
          actionType: 4,
          label: 'Send SMS Action',
          name: 'New WorkOrder SMS Template',
        },
        mobile: {
          type: 8, // Template type
          actionType: 7,
          label: 'Send Mobile Notification',
          name: 'New WorkOrder Push Notification Template',
        },
        web: {
          type: 9, // Template type
          label: 'Send Web Notification',
          name: 'New WorkOrder Push Notification Template',
        },
      },
    }
  },
  mounted() {
    this.loadPickList('site', this.siteOptions)
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
    templateOp(notifications) {
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
    async loadPickList(moduleName, options) {
      if (moduleName) {
        let { error, options: picklistOptions } = await getFieldOptions({
          field: { lookupModuleName: moduleName },
        })

        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          this.siteOptions = picklistOptions
        }
      }
    },
    moveToPrevious() {
      this.$emit('previous')
    },
    userObj(userId) {
      userId =
        userId && userId > -1
          ? userId
          : this.model.triggerData.defaultAssignedTo
      let userObj = null
      if (userId && userId > 0) {
        userObj = this.userList.find(i => i.ouid === userId)
        if (!userObj) {
          userObj = { name: '' }
        }
      }
      return userObj
    },
    triggerValidation() {
      this.model.emitForm = true
    },
    sendClientErrorLog(error) {
      if (isEmpty(error)) {
        return
      }
      let { $account } = this
      let { user } = $account || {}
      let data = {
        userId: user.ouid,
        orgId: user.orgId,
        route: this.$router.name,
        message: error.message,
        stacktrace: error.stack || '',
        browser: 'browser',
        os: 'os',
        ceType: 'client: PMReview.vue',
        ceInfo: null,
        ceUa: null,
      }
      this.$http.post('/v2/errors/web', data).catch(() => {
        setTimeout(function cb() {}, 1 * 60 * 1000) //delays logging by 1min if api call fails
      })
    },
    validated() {
      try {
        this.save()
      } catch (error) {
        this.$message.error(
          this.$t(
            'maintenance.old_pm.something_went_wrong_while_saving_the_record'
          )
        )
        this.isSaving = false
        this.sendClientErrorLog(error)
      }
    },
    failed() {
      this.isSaving = false
    },
    save() {
      this.isSaving = true
      this.model.emitForm = false
      if (!this.model.woData.sites || this.model.woData.sites.length === 0) {
        this.$message({
          message: 'Site is mandatory',
          type: 'error',
        })
        this.isSaving = false
        return
      }

      let { fields } = this.formObj || {}
      let durationFields = [] // For handling Custom Duration fields

      if (!isEmpty(fields)) {
        fields.forEach(field_ => {
          let { field } = field_ || {}
          if (!isEmpty(field)) {
            if (
              field.displayType === 'DURATION' &&
              field.dataTypeEnum === 'NUMBER'
            ) {
              durationFields.push(field.name)
            }
          }
        })
      }

      let workorder = this.$helpers.cloneObject(this.model.woData.woModel)
      Object.assign(workorder, workorder.woModel)
      workorder.isSignatureRequired = this.model.woData.isSignatureRequired
      let uploadFiles = []
      if (this.model.woData.woModel.ticketattachments) {
        for (
          let i = 0;
          i < this.model.woData.woModel.attachedFiles.uploadFiles.length;
          i++
        ) {
          /* 
            since attachments gets added in `this.model.woData.woModel.ticketattachments`, 
            it never comes here, this for-loop can be removed if not required later.
          */
          uploadFiles.push(
            new File(
              [this.model.woData.woModel.attachedFiles.uploadFiles[i]],
              this.model.woData.woModel.attachedFiles.uploadFiles[i].name
            )
          )
        }
        if (this.model.woData.woModel.ticketattachments) {
          workorder.attachments = this.model.woData.woModel.ticketattachments.filter(
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
      workorder.photoMandatory = this.model.preRequestData.photoMandatory
      workorder.allowNegativePreRequisite = this.model.preRequestData.allowNegativePreRequisite
      if (workorder.allowNegativePreRequisite === -1) {
        workorder.allowNegativePreRequisite = 2
      }
      let formdata = {}
      formdata.workorder = workorder
      if (workorder.duration) {
        workorder.duration =
          this.$util.daysHoursToUnixTime(workorder.duration) || -1
      }
      if (workorder.estimatedWorkDuration) {
        workorder.estimatedWorkDuration =
          this.$util.daysHoursToUnixTime(workorder.estimatedWorkDuration) || -1
      }

      if (!isEmpty(durationFields)) {
        durationFields.forEach(name => {
          if (this.$getProperty(workorder, `${name}`)) {
            workorder[`${name}`] =
              this.$util.daysHoursToUnixTime(workorder[`${name}`]) || -1
          }
        })
      }

      formdata.deleteReadingRulesList = []

      let preRequestsections = []
      let preRequestsequence = 1

      for (let key in this.model.preRequestData.preRequestSections) {
        let section = this.$helpers.cloneObject(
          this.model.preRequestData.preRequestSections[key]
        )
        delete section.assignmentType
        section.enableInput = true
        section.inputType = '6'
        if (section.inputType === '6') {
          let secOptions = []
          if (!section.options) {
            section.options = []
            section.options.push({ name: section.additionInfo.truevalue })
            section.options.push({ name: section.additionInfo.falsevalue })
          }

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
        section.taskTemplates = this.$helpers.cloneObject(section.preRequests)
        delete section.preRequests
        for (let key2 in section.taskTemplates) {
          let task = section.taskTemplates[key2]

          let errorMsg = this.validatePreRequest(task)
          if (errorMsg) {
            this.$message({
              message: errorMsg,
              type: 'error',
            })
            this.isSaving = false
            return
          }
          task.enableInput = true
          task.inputType = '6'
          if (task.inputType === '6') {
            let taskOptions = []
            if (!task.options) {
              task.options = []
              task.options.push({ name: task.additionInfo.truevalue })
              task.options.push({ name: task.additionInfo.falsevalue })
            }
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
          if (!task.readingFieldId) {
            task.readingFieldId = -1
          }
          task.sequence = preRequestsequence
          preRequestsequence = preRequestsequence + 1
        }
        preRequestsections.push(section)
      }

      formdata.preRequests = preRequestsections
      if (this.model.preRequestData.allowNegativePreRequisite === 3) {
        let approvers = []
        let approverKeys = { 1: 'userId', 2: 'roleId', 3: 'groupId' }
        this.model.preRequestData.approvers.forEach(approver => {
          if (approver.sharingType && approver.approversId) {
            approvers.push({
              [approverKeys[approver.sharingType]]: approver.approversId,
              sharingType: approver.sharingType,
              actions: approver.actions,
            })
          }
        })
        this.model.preRequestData.approvers = approvers
      } else {
        delete this.model.preRequestData.approvers
      }
      formdata.prerequisiteApprover = this.model.preRequestData.approvers
      // prerequisite end

      let sections = []
      let sequence = 1
      let uniqueId = 1
      let sectionSequence = 1

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
            section.category[0] === 'CURRENT_SPACE' ||
            section.category[0] === 'CURRENT_BUILDING'
          ) {
            section.assignmentType = 5
          } else if (section.category[0] === 'ALL_SITES') {
            // this sets the Section's assignmentType
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
          if (task.category && task.category.length) {
            if (
              task.category[0] === 'CURRENT_FLOOR' ||
              task.category[0] === 'CURRENT_ASSET' ||
              task.category[0] === 'CURRENT_SPACE' ||
              task.category[0] === 'CURRENT_BUILDING'
            ) {
              task.assignmentType = 5
            } else if (task.category[0] == 'ALL_SITES') {
              // set task assignmentType
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
          this.$set(task.additionInfo, 'uniqueId', uniqueId++)
          sequence = sequence + 1
        }
        section = {
          ...section,
          sequenceNumber: sectionSequence++,
        }
        sections.push(section)
      }

      formdata.tasks = sections

      let preventiveFormData = {}
      preventiveFormData.title = formdata.workorder.subject
      preventiveFormData.triggers = []

      preventiveFormData.preventOnNoTask = this.model.woData.isPreventOnNoTask

      if (this.model.woData.isOffsetWOCreation) {
        let offset =
          this.model.woData.offsetDays * 24 * 60 * 60 +
          this.model.woData.offsetHours * 60 * 60
        preventiveFormData.woCreationOffset = offset
        formdata.workorder.woCreationOffset = offset
      } else {
        preventiveFormData.woCreationOffset = 0
        formdata.workorder.woCreationOffset = 0
      }

      formdata.workorder.sendForApproval = this.model.woData.sendForApproval
      formdata.workorder.workPermitNeeded = this.model.woData.workPermitNeeded
      preventiveFormData.markIgnoredWo = this.model.woData.markIgnoredWo

      preventiveFormData.pmCreationType = 3
      preventiveFormData.siteIds = this.model.woData.sites
      preventiveFormData.baseSpaceId =
        this.model.woData.selectedBuilding || this.getSelectedSiteId() || -1
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
      } else if (this.model.woData.resourceType === 'BUILDINGS') {
        preventiveFormData.assignmentType = 7
        this.model.woData.selectedBuildingList.forEach(x => {
          preventiveFormData.pmIncludeExcludeResourceContexts.push({
            resourceId: x,
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
      } else if (this.model.woData.resourceType === 'ALL_SITES') {
        // set assignmentType & pmIncludeExcludeResourceContexts in preventiveFormData
        preventiveFormData.assignmentType = 8
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
          // trigger.startTime = this.$helpers.addTime(trigger.startDate, trigger.startHour.length ? trigger.startHour[0] : '00:00')
          if (trigger.stopAfter !== 'never') {
            trigger.endTime = -1
          } else {
            trigger.endTime = -1
          }
          // TODO - check the need ?
          // trigger.frequency = trigger.schedule.facilioFrequency
          // if (trigger.frequency.length > 1) {
          //  preventiveFormData.frequency = 8
          // }
          // else {
          //  preventiveFormData.frequency = trigger.schedule.facilioFrequency
          // }
          // preventiveFormData.custom = trigger.schedule.custom
          delete trigger.sharingContext
        } else if (trigger.type === 2) {
          trigger.triggerExecutionSource = 2
          trigger.startTime = -1
          trigger.endTime = -1
          if (trigger.stopAfterReading === 'never') {
            trigger.endReading = -1
          }
          delete trigger.sharingContext
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
          delete trigger.sharingContext
          delete trigger.criteria
        } else if (trigger.type === 4) {
          let userTrigger = {
            name: trigger.name,
            type: trigger.type,
            sharingContext: this.constructSharingContext(
              trigger.sharingContext
            ),
            triggerExecutionSource: 4,
          }
          trigger = userTrigger
        } else if (trigger.type == 5) {
          let customTrigger = {
            name: trigger.name,
            type: trigger.type,
            triggerExecutionSource: 5,
            executeOn: trigger.customTrigger.executeOn,
            executionOffset:
              trigger.customTrigger.hours +
              trigger.customTrigger.days * 60 * 60 * 24,
            customModuleId: trigger.customTrigger.customModuleId,
            fieldId: trigger.customTrigger.fieldId,
          }
          trigger = customTrigger
          delete trigger.sharingContext
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

      let { workorder: woData } = formdata || {}
      let { data } = woData || {}
      woData = { ...woData, ...data }
      delete woData.data
      formdata = { ...formdata, workorder: woData }

      if (this.model.isEdit) {
        this.updatePreventiveMaintenance(formdata, uploadFiles)
      } else {
        this.savePreventiveMaintenance(formdata, uploadFiles)
      }
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
    constructSharingContext(sharing) {
      let sharingContext = []
      if (sharing.shareTo === 1) {
        sharingContext.push({
          type: 1,
          userId: this.getCurrentUser().ouid,
        })
      } else if (sharing.shareTo === 3) {
        if (sharing.sharedUsers.length > 0) {
          sharingContext.push({
            type: 1,
            userId: this.getCurrentUser().ouid,
          })
          for (let i = 0; i < sharing.sharedUsers.length; i++) {
            if (sharing.sharedUsers[i] !== this.getCurrentUser().ouid) {
              sharingContext.push({
                type: 1,
                userId: sharing.sharedUsers[i],
              })
            }
          }
        }
        if (sharing.sharedRoles.length > 0) {
          for (let i = 0; i < sharing.sharedRoles.length; i++) {
            sharingContext.push({
              type: 2,
              roleId: sharing.sharedRoles[i],
            })
          }
        }
        if (sharing.sharedGroups.length > 0) {
          for (let i = 0; i < sharing.sharedGroups.length; i++) {
            sharingContext.push({
              type: 3,
              groupId: sharing.sharedGroups[i],
            })
          }
        }
      }
      return sharingContext
    },
    updatePreventiveMaintenance(data, uploadFiles) {
      let formData = new FormData()
      formData.append('workOrderString', JSON.stringify(data.workorder))
      formData.append(
        'preventiveMaintenanceString',
        JSON.stringify(data.preventivemaintenance)
      )
      formData.append('tasksString', JSON.stringify(data.tasks))
      formData.append('preRequestsString', JSON.stringify(data.preRequests))
      formData.append(
        'prerequisiteApproverString',
        JSON.stringify(data.prerequisiteApprover)
      )
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
      let id = parseInt(this.$route.query.edit || this.$route.params.id)
      this.$http
        .post('/workorder/updateNewPreventiveMaintenance?id=' + id, formData)
        .then(response => {
          if (typeof response.data === 'object') {
            this.$message({
              message: 'Planned maintenance updated successfully!',
              type: 'success',
            })
            this.$emit('edited')
          } else {
            this.$message({
              message: 'Planned maintenance updation failed!',
              type: 'error',
            })
            this.isSaving = false
          }
        })
        .catch(error => {
          this.$message.error(
            error?.data?.message || 'Failed to edit Planned maintenance!'
          )
          this.isSaving = false
        })
    },
    savePreventiveMaintenance(data, uploadFiles) {
      let formData = new FormData()
      let error = this.validationPM(data)
      if (error) {
        this.$message.error(error)
        this.isSaving = false
        return
      }
      formData.append('workOrderString', JSON.stringify(data.workorder))
      formData.append(
        'preventiveMaintenanceString',
        JSON.stringify(data.preventivemaintenance)
      )
      formData.append('tasksString', JSON.stringify(data.tasks))
      formData.append('preRequestsString', JSON.stringify(data.preRequests))
      formData.append(
        'prerequisiteApproverString',
        JSON.stringify(data.prerequisiteApprover)
      )
      if (uploadFiles) {
        for (let num = 0; num < uploadFiles.length; num++) {
          formData.append('attachedFiles', uploadFiles[num])
        }
      }
      if (data.reminders) {
        formData.append('reminderString', JSON.stringify(data.reminders))
      }
      let saveurl = '/workorder/addNewPreventiveMaintenance'
      let self = this
      self.$http
        .post(saveurl, formData)
        .then(function(response) {
          if (typeof response.data === 'object') {
            self.$message({
              message: 'Planned maintenance created successfully!',
              type: 'success',
            })
            self.isSaving = false
            self.$emit('saved')
          } else {
            self.$message({
              message: 'Planned maintenance creation failed!',
              type: 'error',
            })
            self.isSaving = false
          }
        })
        .catch(function(_) {
          self.$message({
            message: 'Planned maintenance creation failed!',
            type: 'error',
          })
          self.isSaving = false
        })
    },
    validationPM(data) {
      let errorMsg = ''
      if (!data.workorder.subject) {
        errorMsg += 'Please enter the PPM subject \n'
        return errorMsg
      }
      if (data.preventivemaintenance.triggers.length > 0) {
        for (let keys in data.preventivemaintenance.triggers) {
          let ds = data.preventivemaintenance.triggers[keys]
          if (parseInt(ds.type) === 2) {
            if (!ds.readingFieldId || ds.readingFieldId < 1) {
              errorMsg +=
                'Please select the specific reading type for this trigger \n'
              return errorMsg
            }
            if (!ds.readingInterval || ds.readingFieldId < 0) {
              errorMsg += 'Please specify the reading interval \n'
              return errorMsg
            }
            if (!ds.startReading || ds.startReading < 0) {
              errorMsg += 'Please specify the reading start value \n'
              return errorMsg
            }
          } else if (parseInt(ds.type) === 1) {
            // if (!ds.startHour[0]) {
            //   errorMsg += 'Please enter a date for this trigger \n'
            //   return errorMsg
            // }
            // if (!ds.startDate) {
            //   errorMsg += 'Please enter a time for this trigger \n'
            //   return errorMsg
            // }
          }
        }
      }
    },
    validateTask(task) {
      let errorMsg = ''
      if (!task.name) {
        errorMsg += 'Please specify the task subject \n'
      }
      if (task.enableInput) {
        if (parseInt(task.inputType) === 2) {
          if (parseInt(task.readingFieldId) < 1 || !task.readingFieldId) {
            errorMsg += 'Please select a specific reading type \n'
          }
        } else if (parseInt(task.inputType) === 4) {
          if (task.validation === 'safeLimit') {
            if (!task.minSafeLimit && !task.maxSafeLimit) {
              errorMsg += 'Please enter either minimum or maximum value \n'
            }
          }
        } else if (parseInt(task.inputType) === 5) {
          if (task.options.length < 2) {
            errorMsg += 'Please enter atleast two options\n'
          } else if (!task.options[0].name && !task.options[1].name) {
            errorMsg += 'Please enter atleast two options \n'
          }
        }
      }
      return errorMsg
    },
    getSelectedSiteId() {
      // return siteId only if one site is selected or else return null
      // this condition applies when category is selected as SPACE/ASSET Category.
      return this.model.woData.sites.length == 1
        ? this.model.woData.sites[0]
        : null
    },
  },
  watch: {
    'model.woData.resourceList': {
      handler: function(newValue) {
        this.$forceUpdate()
      },
      deep: true,
    },
  },
  computed: {
    ...mapState({
      assetcategory: state => state.assetCategory,
      // groups: state => state.groups,
      // users: state => state.users,
    }),
    ...mapGetters(['getSpaceCategoryPickList', 'getCurrentUser']),
    spacecategory() {
      return this.getSpaceCategoryPickList()
    },
    resourceListLength() {
      if (this.model.woData.resourceList) {
        return this.model.woData.resourceList.length
      }
      return 0
    },
    userList() {
      if (
        this.model.woData.woModel.groups &&
        this.model.woData.woModel.groups.id
      ) {
        let group = this.groups.find(
          group => group.id === Number(this.model.woData.woModel.groups.id)
        )
        return isEmpty(group) ? [] : group.members
      }
      return this.users
    },
    siteName() {
      if (
        this.model.woData.sites &&
        this.model.woData.sites.length > 0 &&
        this.siteOptions
      ) {
        let siteLabels = []
        for (let i = 0; i < this.model.woData.sites.length; i++) {
          for (let j = 0; j < this.siteOptions.length; j++) {
            if (
              Number(this.siteOptions[j].value) === this.model.woData.sites[i]
            ) {
              siteLabels.push(this.siteOptions[j].label)
              break
            }
          }
        }

        let label
        if (siteLabels.length > 0) {
          label = siteLabels[0]
          for (let k = 1; k < siteLabels.length; k++) {
            label = label + ',' + siteLabels[k]
          }
        }
        return label
      }
      return ''
    },
    assetCategoryName() {
      let category = this.assetcategory.find(
        i => i.id === Number(this.model.woData.assetCategoryId)
      )
      if (category) {
        return category.name
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
    buildingName() {
      let building = this.buildingList.find(
        i => i.id === this.model.woData.selectedBuilding
      )
      if (building) {
        return building.name
      }
      return ''
    },
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
  },
}
</script>
<style>
.review-wrap-block {
  max-width: 700px;
}
</style>
