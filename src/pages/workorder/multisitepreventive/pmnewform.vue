<template>
  <div class="wo-form-main-container">
    <div class="fc-pm-form-container">
      <div class="fc-pm-form-left-menu">
        <div class="fc-pm-form-list">
          <ul
            v-for="(data, key) in pmFormType"
            v-if="key !== 'resourceplan' || model.triggerData.triggers.length"
            :key="data"
          >
            <li class="label-txt-black pointer" @click="formSelect(key)">
              <div
                :class="{
                  'form-dot-align': true,
                  'dot-inactive-grey': currentView !== key,
                  'dot-active-pink': currentView === key,
                }"
                :key="key"
                value="key"
              ></div>
              {{ data }}
            </li>
          </ul>
        </div>
      </div>
    </div>
    <pm-config
      :model="model"
      v-show="show['pmconfig']"
      @next="moveToNext"
      @previous="moveToPrevious"
      @validated="validated"
      @failed="failed"
    ></pm-config>
    <workorder
      :tenant="tenantId"
      v-show="show['workorder']"
      :model="model"
      @next="moveToNext"
      @previous="moveToPrevious"
      @validated="validated"
      @failed="failed"
    ></workorder>
    <wo-nx-form
      v-if="!model.isLoading"
      :tenant="tenantId"
      v-show="show['workorderNx']"
      :model="model"
      @next="moveToNext"
      @previous="moveToPrevious"
      @validated="validated"
      @failed="failed"
      @setFormObject="setFormObject"
    ></wo-nx-form>
    <prerequests
      v-if="!model.isLoading"
      v-show="show['prerequests']"
      :model="model"
      :users="users"
      :teams="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
    ></prerequests>
    <tasks
      v-if="!model.isLoading"
      v-show="show['tasks']"
      :model="model"
      @next="moveToNext"
      @previous="moveToPrevious"
    ></tasks>
    <trigger
      v-if="
        !model.isLoading &&
          ($helpers.isLicenseEnabled('SKIP_TRIGGERS') ||
            this.model.triggerData.enableSkipTriggers)
      "
      v-show="show['trigger']"
      :model="model"
      :users="users"
      :groups="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
    ></trigger>
    <old-trigger
      v-else-if="
        !model.isLoading && !$helpers.isLicenseEnabled('SKIP_TRIGGERS')
      "
      v-show="show['trigger']"
      :model="model"
      :users="users"
      :groups="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
    ></old-trigger>
    <resource-plan
      v-if="
        !model.isLoading &&
          model.woData.workOrderType === 'bulk' &&
          model.triggerData.triggers.length
      "
      v-show="show['resourceplan']"
      :model="model"
      :users="users"
      :groups="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
    ></resource-plan>
    <review
      ref="reviewer"
      v-if="
        !model.isLoading &&
          ($helpers.isLicenseEnabled('SKIP_TRIGGERS') ||
            this.model.triggerData.enableSkipTriggers)
      "
      v-show="show['review']"
      :model="model"
      :formObj="formObj"
      :users="users"
      :groups="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
      @saved="saved"
      @edited="edited"
    ></review>
    <old-review
      ref="reviewer"
      v-else-if="
        !model.isLoading && !$helpers.isLicenseEnabled('SKIP_TRIGGERS')
      "
      v-show="show['review']"
      :model="model"
      :users="users"
      :groups="teams"
      @next="moveToNext"
      @previous="moveToPrevious"
      @saved="saved"
      @edited="edited"
    ></old-review>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import WoForm from 'pages/workorder/multisitepreventive/PmWoForm'
import WoNxForm from 'pages/workorder/multisitepreventive/WoNxForm'
import TaskForm from 'pages/workorder/multisitepreventive/pmformtask'
import PreRequestForm from 'pages/workorder/multisitepreventive/pmformprerequest'
import TriggerForm from 'pages/workorder/multisitepreventive/pmformtrigger'
import OldTriggerForm from 'pages/workorder/multisitepreventive/pmformtriggerold'
import ReviewForm from 'pages/workorder/multisitepreventive/PmReview'
import OldReviewForm from 'pages/workorder/multisitepreventive/OldPmReview'
import ResourcePlan from 'pages/workorder/multisitepreventive/PmResourcePlan'
import PMConfigForm from 'pages/workorder/multisitepreventive/PMConfigForm'
import PMMixin from '@/mixins/PMMixin'
import FormMixin from '@/mixins/FormMixin'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
import { isObject, isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'

export default {
  mixins: [PMMixin, FormMixin],
  data() {
    return {
      currentView: 'pmconfig',
      show: {
        pmconfig: true,
        workorder: false,
        prerequests: false,
        tasks: false,
        trigger: false,
        review: false,
        resourceplan: false,
        workorderNx: false,
      },
      model: {
        isEdit: false,
        isLoading: false,
        emitForm: false,
        woData: {
          sites: [],
          woModel: null,
          workOrderType: 'bulk',
          resourceLabel: '',
          resourceName: '',
          selectedBuilding: null,
          spacecategoryId: null,
          resourceQuery: null,
          selectedResourceList: [],
          isIncludeResource: true,
          assetCategoryId: null,
          resourceType: null,
          selectedSpaceList: [],
          isIncludeSpace: true,
          selectedFloorList: [],
          isIncludeFloor: true,
          selectedBuildingList: [],
          selectedSitesList: [],
          singleResource: null,
          quickSearchQuery: '',
          spaceAssetDisplayName: '',
          resourceList: [],
          isPreventOnNoTask: true,
          isSignatureRequired: false,
          isOffsetWOCreation: false,
          woCreationOffset: 0,
          offsetDays: 0,
          offsetHours: 0,
          sendForApproval: false,
          workPermitNeeded: false,
          markIgnoredWo: false,
          enableSkipTriggers: false,
        },
        taskData: {
          taskSections: [
            {
              name: 'Untitled Section',
              category: null,
              dummyValue: [1],
              inputType: null,
              resourceLabel: '',
              selectedResourceList: [],
              isIncludeResource: null,
              attachmentRequired: false,
              enableInput: false,
              readingFieldId: null,
              validation: null,
              minSafeLimit: null,
              maxSafeLimit: null,
              options: [],
              triggers: [{ triggerName: 'All' }],
              additionInfo: {
                defaultValue: '',
                failureValue: '',
                woCreateFormId: null,
                deviationOperatorId: null,
                remarksRequired: false,
                remarkOptionValues: [],
                attachmentOptionValues: [],
              },
              tasks: [
                {
                  name: '',
                  resourceLabel: '',
                  description: '',
                  inputType: null,
                  attachmentRequired: false,
                  enableInput: false,
                  readingFieldId: null,
                  inputValidationRuleId: null,
                  validation: null,
                  minSafeLimit: null,
                  maxSafeLimit: null,
                  options: [],
                  selectedResourceList: [],
                  isIncludeResource: null,
                  category: null,
                  readingFields: [],
                  additionInfo: {
                    defaultValue: '',
                    failureValue: '',
                    woCreateFormId: null,
                    deviationOperatorId: null,
                    remarksRequired: false,
                    remarkOptionValues: [],
                    attachmentOptionValues: [],
                    uniqueId: 1,
                  },
                },
              ],
            },
          ],
        },
        preRequestData: {
          photoMandatory: false,
          allowNegative: false,
          allowNegativePreRequisite: 2,
          preRequestSections: [],
          approvers: [{ type: null, approversId: null }],
          approvalTypes: [
            {
              id: 1,
              name: 'User',
            },
            {
              id: 2,
              name: 'Role',
            },
            {
              id: 3,
              name: 'Group',
            },
          ],
        },
        triggerData: {
          defaultAssignedTo: null,
          isDefaultAllTriggers: false,
          enableSkipTriggers: true,
          triggers: [],
          reminders: [],
        },
      },
      pmFormType: {
        pmconfig: 'Configuration',
        // workorder: this.$t('common.header.workorders'),
        workorderNx: this.$t('common.header.workorders'),
        trigger: this.$t('maintenance.pm_list.trigger'),
        prerequests: this.$t('maintenance._workorder.prerequisites'),
        tasks: this.$t('maintenance._workorder.tasks'),
        resourceplan: this.$t('maintenance.pm_list.resource_planning'),
        review: this.$t('maintenance.pm_list.review'),
      },
      newTrigger: {
        name: '',
        type: -1,
        startHour: ['00:00'],
        startDate: new Date(),
        startTime: null,
        schedule: null,
        stopAfter: 'never',
        endTime: new Date(),
        startReading: null,
        readingFieldId: null,
        stopAfterReading: 'never',
        endReading: null,
        readingInterval: null,
        frequency: -1,
        sharingContext: null,
      },
      remindersTemplate: {
        name: null,
        type: null,
        duration: null,
        isEmail: null,
        isSms: null,
        isMobile: null,
        reminderActions: null,
        emailtemplate: null,
        smstemplate: null,
        mobiletemplate: null,
      },
      formObj: {},
      durationFields: [],
      defaultDuration: 0,
      users: [],
      teams: [],
    }
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    this.$store.dispatch('loadRoles')

    let groupsAndUsersResponse = this.getScopedGroupsAndUsers()
    groupsAndUsersResponse.then(groupsAndUsersResult => {
      if (!isEmpty(groupsAndUsersResult)) {
        this.$set(this, 'users', groupsAndUsersResult.users)
        this.$set(this, 'teams', groupsAndUsersResult.groups)
      } else {
        //this.$store.dispatch('loadGroups') // can be loaded from store if required.
      }
    })
  },
  mounted() {
    this.fetchPmDetail()
  },
  computed: {
    ...mapState({
      //users: state => state.users,
      //teams: state => state.groups,
      roles: state => state.roles,
    }),
    ...mapGetters(['getCurrentUser']),
    tenantId() {
      return this.$route.query.tenant
    },
  },
  components: {
    workorder: WoForm,
    prerequests: PreRequestForm,
    tasks: TaskForm,
    trigger: TriggerForm,
    review: ReviewForm,
    'resource-plan': ResourcePlan,
    'old-trigger': OldTriggerForm,
    'old-review': OldReviewForm,
    'pm-config': PMConfigForm,
    WoNxForm,
  },
  methods: {
    saved() {
      this.isFormSaved = true

      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.LIST) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'active',
            },
          })
        }
      } else {
        this.$router.push({ path: '/app/wo/planned' })
      }
    },
    edited() {
      this.isFormSaved = true

      if (isWebTabsEnabled()) {
        let { id } = this.$route.params
        let { name } =
          findRouteForModule('preventivemaintenance', pageTypes.OVERVIEW) || {}

        if (name) {
          this.$router.push({
            name,
            params: {
              viewname: 'all',
              id,
            },
          })
        }
      } else {
        let { edit } = this.$route.query || {}

        this.$router.replace({
          path: `/app/wo/planned/multisummary/${edit}`,
        })
      }
    },
    fetchPmDetail() {
      let id = parseInt(this.$route.query.edit || this.$route.params.id)
      if (id) {
        this.model.isEdit = true
      }

      if (this.model.isEdit) {
        this.model.isLoading = true
        this.$http.get(`v2/workorders/pmDetail?id=${id}`).then(response => {
          let woData = this.$helpers.cloneObject(response.data.workorder)
          woData.site = { id: -1 }
          delete woData.siteId
          if (woData?.duration && woData.duration === -1) {
            woData.duration = this.defaultDuration
          }

          if (
            woData?.estimatedWorkDuration &&
            woData.estimatedWorkDuration === -1
          ) {
            woData.estimatedWorkDuration = this.defaultDuration
          }

          if (woData.assignmentGroup) {
            woData.groups = this.$helpers.cloneObject(woData.assignmentGroup)
            delete woData.assignmentGroup
          }

          if (woData.attachments) {
            woData.attachedFiles = { attachments: [], uploadFiles: [] }
            woData.attachedFiles.attachments = woData.attachments
          }

          if (woData.resource) {
            this.model.woData.singleResource = this.$helpers.cloneObject(
              woData.resource
            )
            delete woData.resource
          }

          this.model.woData.sendForApproval = woData.sendForApproval
          this.model.woData.workPermitNeeded = woData.workPermitNeeded

          if (
            response.data.preventivemaintenance.woCreationOffset &&
            response.data.preventivemaintenance.woCreationOffset > 0
          ) {
            this.model.woData.woCreationOffset =
              response.data.preventivemaintenance.woCreationOffset
          } else {
            this.model.woData.woCreationOffset = 0
          }

          if (this.model.woData.woCreationOffset) {
            this.model.woData.offsetDays = Math.floor(
              this.model.woData.woCreationOffset / (24 * 60 * 60)
            )
            this.model.woData.offsetHours =
              (this.model.woData.woCreationOffset % (24 * 60 * 60)) / (60 * 60)
          }

          this.model.woData.isOffsetWOCreation =
            this.model.woData.woCreationOffset > 0

          this.model.woData.isSignatureRequired = woData.isSignatureRequired
          delete woData.isSignatureRequired

          this.model.woData.isPreventOnNoTask =
            response.data.preventivemaintenance.preventOnNoTask

          this.model.woData.sites = response.data.preventivemaintenance.siteIds

          let customData = woData.data
          let newCustomData = {}
          // added this check - as data from import doesn't have custom data
          if (!isEmpty(customData)) {
            Object.keys(customData).forEach(d => {
              let cd = customData[d]
              if (isObject(cd) && cd.hasOwnProperty('id') && !isEmpty(cd.id)) {
                newCustomData[d] = { id: parseInt(cd.id) }
              }
            })
          }
          this.$set(this.model.woData, 'woModel', {
            ...woData,
            ...newCustomData,
          })
          this.$set(this.model, 'actualWoData', cloneDeep(woData))

          this.model.woData.assetCategoryId =
            response.data.preventivemaintenance.assetCategoryId
          this.model.woData.spacecategoryId = String(
            response.data.preventivemaintenance.spaceCategoryId
          )

          this.model.woData.woModel.preventOnNoTask =
            response.data.preventivemaintenance.preventOnNoTask

          this.model.woData.workOrderType = 'bulk'

          // boolean flag to check if the given baseSpaceId is Site ID.
          //  [ Use Case: When the user creates SPACE_CATEGORY/ASSET_CATEGORY PM,
          //  site level(selects single site), and doesn't select the Building option. ]
          let isBaseSpaceASiteId = false
          if (response.data.preventivemaintenance.siteIds) {
            response.data.preventivemaintenance.siteIds.forEach(id => {
              if (id === response.data.preventivemaintenance.baseSpaceId) {
                isBaseSpaceASiteId = true
              }
            })
          }
          if (!isBaseSpaceASiteId) {
            this.model.woData.selectedBuilding =
              response.data.preventivemaintenance.baseSpaceId
          }

          this.model.woData.markIgnoredWo =
            response.data.preventivemaintenance.markIgnoredWo

          this.model.triggerData.isDefaultAllTriggers =
            response.data.preventivemaintenance.defaultAllTriggers || false

          this.model.triggerData.enableSkipTriggers =
            response.data.preventivemaintenance.enableSkipTriggers || false

          let resourceMap = {}
          if (response.data.listOfTasks) {
            response.data.listOfTasks.forEach(t => {
              if (
                t.resource &&
                t.resource.id > 0 &&
                !resourceMap.hasOwnProperty(t.resource.id)
              ) {
                resourceMap[t.resource.id] = t.resource
              }
            })
          }

          let triggerMap = {}
          if (response.data.preventivemaintenance.triggers) {
            response.data.preventivemaintenance.triggers.forEach(i => {
              triggerMap[i.id] = i
            })
          }
          if (response.data.prerequisiteApproverTemplates) {
            woData.approvers = response.data.prerequisiteApproverTemplates
          }
          this.fillPreRequests(
            response.data.preRequestSectionTemplates,
            woData.photoMandatory,
            woData.allowNegativePreRequisite,
            woData.approvers
          )

          this.$nextTick(() => {
            // when building is filled watchers fire
            if (response.data.preventivemaintenance.pmCreationType === 3) {
              if (response.data.preventivemaintenance.assignmentType === 1) {
                this.model.woData.resourceType = 'ALL_FLOORS'
                this.$nextTick(() => {
                  // when resourceType is filled watchers fire
                  if (
                    response.data.preventivemaintenance
                      .pmIncludeExcludeResourceContexts
                  ) {
                    response.data.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                      x => {
                        this.model.woData.selectedFloorList.push({
                          id: x.resourceId,
                        })
                      }
                    )
                  }
                  this.fillTasks(
                    response.data.sectionTemplates,
                    this.model.woData.resourceType,
                    2,
                    null,
                    resourceMap,
                    triggerMap
                  )
                  this.model.isLoading = false
                })
              } else if (
                response.data.preventivemaintenance.assignmentType === 3
              ) {
                this.model.woData.resourceType = 'SPACE_CATEGORY'
                this.$nextTick(() => {
                  if (
                    response.data.preventivemaintenance
                      .pmIncludeExcludeResourceContexts
                  ) {
                    response.data.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                      x => {
                        this.model.woData.selectedSpaceList.push({
                          id: x.resourceId,
                        })
                      }
                    )
                  }
                  this.fillTasks(
                    response.data.sectionTemplates,
                    this.model.woData.resourceType,
                    2,
                    null,
                    resourceMap,
                    triggerMap
                  )
                  this.model.isLoading = false
                })
              } else if (
                response.data.preventivemaintenance.assignmentType === 7
              ) {
                this.model.woData.resourceType = 'BUILDINGS'
                this.$nextTick(() => {
                  if (
                    response.data.preventivemaintenance
                      .pmIncludeExcludeResourceContexts
                  ) {
                    response.data.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                      x => {
                        this.model.woData.selectedBuildingList.push(
                          x.resourceId
                        )
                      }
                    )
                  }
                  this.fillTasks(
                    response.data.sectionTemplates,
                    this.model.woData.resourceType,
                    2,
                    null,
                    resourceMap,
                    triggerMap
                  )
                  this.model.isLoading = false
                })
              } else if (
                response.data.preventivemaintenance.assignmentType === 4
              ) {
                this.model.woData.resourceType = 'ASSET_CATEGORY'
                this.$nextTick(() => {
                  if (
                    response.data.preventivemaintenance
                      .pmIncludeExcludeResourceContexts
                  ) {
                    response.data.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                      x => {
                        this.model.woData.selectedResourceList.push({
                          id: x.resourceId,
                        })
                      }
                    )
                  }
                  this.fillTasks(
                    response.data.sectionTemplates,
                    this.model.woData.resourceType,
                    2,
                    null,
                    resourceMap,
                    triggerMap
                  )
                  this.model.isLoading = false
                })
              } else if (
                response.data.preventivemaintenance.assignmentType === 8
              ) {
                this.model.woData.resourceType = 'ALL_SITES'
                this.$nextTick(() => {
                  if (
                    response.data.preventivemaintenance
                      .pmIncludeExcludeResourceContexts
                  ) {
                    response.data.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                      x => {
                        this.model.woData.selectedSitesList.push(x.resourceId)
                      }
                    )
                  }
                  this.fillTasks(
                    response.data.sectionTemplates,
                    this.model.woData.resourceType,
                    2,
                    null,
                    resourceMap,
                    triggerMap
                  )
                  this.model.isLoading = false
                })
              }
            } else {
              this.$nextTick(() => {
                this.fillTasks(
                  response.data.sectionTemplates,
                  this.model.woData.resourceType,
                  1,
                  this.model.woData.singleResource,
                  resourceMap,
                  triggerMap
                )
                this.model.isLoading = false
              })
            }

            if (response.data.workorder.assignedTo) {
              this.model.triggerData.defaultAssignedTo =
                response.data.workorder.assignedTo.id
            }

            if (response.data.reminders) {
              this.model.triggerData.reminders = []
              if (response.data.reminders && response.data.reminders.length) {
                response.data.reminders.forEach(r => {
                  let reminder = this.$helpers.cloneFromSchema(
                    this.remindersTemplate,
                    r
                  )
                  if (
                    reminder.reminderActions &&
                    reminder.reminderActions.length
                  ) {
                    reminder.reminderActions.forEach(ra => {
                      if (ra.action.actionType === 3) {
                        reminder.isEmail = true
                      } else if (ra.action.actionType === 4) {
                        reminder.isSms = true
                      } else if (ra.action.actionType === 7) {
                        reminder.isMobile = true
                      }
                    })
                  }
                  this.model.triggerData.reminders.push(reminder)
                })
              }
            }

            if (response.data.preventivemaintenance.triggers) {
              this.model.triggerData.triggers = response.data.preventivemaintenance.triggers.map(
                trigger => {
                  let newTrigger = this.$helpers.cloneObject(this.newTrigger)
                  for (let key in newTrigger) {
                    if (
                      trigger.hasOwnProperty(key) &&
                      trigger[key] &&
                      trigger[key] !== -1
                    ) {
                      newTrigger[key] = trigger[key]
                    }
                  }

                  if (trigger['frequency'] === 0) {
                    newTrigger['frequency'] = 0
                  }

                  if (trigger.triggerExecutionSource === 1) {
                    newTrigger.type = 1
                    trigger.schedule.timeObjects = null
                    newTrigger.initialSchedule = trigger.schedule
                    newTrigger.initialSchedule.facilioFrequency =
                      trigger.frequency
                    newTrigger.initialSchedule.custom = trigger.schedule.custom
                    if (trigger.custom) {
                      newTrigger.initialSchedule.custom = trigger.custom
                    }
                    newTrigger.startHour = trigger.schedule.times

                    if (newTrigger.startTime && newTrigger.startTime !== -1) {
                      newTrigger.startDate = new Date(
                        newTrigger.startTime
                      ).setHours(0, 0, 0, 0)
                    }
                    if (newTrigger.endTime && newTrigger.endTime !== -1) {
                      newTrigger.endTime = new Date(newTrigger.endTime)
                      newTrigger.stopAfter = 'time'
                    }
                  } else if (trigger.triggerExecutionSource === 2) {
                    newTrigger.type = 2
                    newTrigger.stopAfterReading =
                      trigger.endReading && trigger.endReading !== -1
                        ? 'reading'
                        : 'never'
                  } else if (trigger.triggerExecutionSource === 3) {
                    newTrigger.type = 3
                    if (trigger.workFlowRule) {
                      newTrigger.criteria = trigger.workFlowRule.criteria
                      delete newTrigger.criteria.regEx
                      delete newTrigger.criteria.computedValues
                      if (newTrigger.criteria.conditions) {
                        Object.keys(newTrigger.criteria.conditions).forEach(
                          i => {
                            delete newTrigger.criteria.conditions[i]
                              .computedValues
                            delete newTrigger.criteria.conditions[i]
                              .computedWhereClause
                          }
                        )
                      }
                    }
                  } else if (trigger.triggerExecutionSource === 4) {
                    newTrigger.type = 4
                    let sharingContext = newTrigger.sharingContext
                    let newSharingContext = {
                      shareTo: null,
                      sharedRoles: [],
                      sharedUsers: [],
                      sharedGroups: [],
                    }
                    if (!sharingContext || sharingContext.length === 0) {
                      newSharingContext.shareTo = 2
                    } else if (
                      sharingContext.length === 1 &&
                      sharingContext[0].userId === this.getCurrentUser().ouid
                    ) {
                      newSharingContext.shareTo = 1
                      newSharingContext.sharedUsers = [sharingContext[0].userId]
                    } else {
                      newSharingContext.shareTo = 3
                      for (let i = 0; i < sharingContext.length; i++) {
                        if (sharingContext[i].type === 1) {
                          newSharingContext.sharedUsers.push(
                            sharingContext[i].userId
                          )
                        } else if (sharingContext[i].type === 2) {
                          newSharingContext.sharedRoles.push(
                            sharingContext[i].roleId
                          )
                        } else if (sharingContext[i].type === 3) {
                          newSharingContext.sharedGroups.push(
                            sharingContext[i].groupId
                          )
                        }
                      }
                    }
                    newTrigger.sharingContext = newSharingContext
                  } else if (trigger.triggerExecutionSource === 5) {
                    newTrigger.type = 5
                    let customTrigger
                    if (trigger.executeOn === 1) {
                      customTrigger = {
                        executeOn: trigger.executeOn,
                        days: 0,
                        hours: 0,
                        customModuleId: trigger.customModuleId,
                        fieldId: trigger.fieldId,
                      }
                    } else {
                      let days = Math.floor(
                        trigger.executionOffset / (24 * 60 * 60)
                      )
                      let hours = trigger.executionOffset % (24 * 60 * 60)
                      customTrigger = {
                        executeOn: trigger.executeOn,
                        days,
                        hours,
                        customModuleId: trigger.customModuleId,
                        fieldId: trigger.fieldId,
                      }
                    }

                    newTrigger.customTrigger = customTrigger
                  }
                  return newTrigger
                }
              )
            }

            this.$nextTick(() => {
              let resources =
                response.data.preventivemaintenance.resourcePlanners
              if (resources) {
                resources.forEach(r => {
                  let selected = false
                  let triggerNames = []
                  let name = r.resource ? r.resource.name : ''
                  let assignedTo = r.assignedToId
                  let notifications = []
                  let id = r.resourceId
                  if (
                    r.triggerContexts &&
                    response.data.preventivemaintenance.triggers
                  ) {
                    r.triggerContexts.forEach(k => {
                      let trigger = response.data.preventivemaintenance.triggers.find(
                        i => i.id === k.id
                      )
                      if (trigger) {
                        triggerNames.push(trigger.name)
                      }
                    })
                  }
                  if (triggerNames == null || !triggerNames.length) {
                    triggerNames = ['0']
                  }
                  if (r.pmResourcePlannerReminderContexts) {
                    r.pmResourcePlannerReminderContexts.forEach(i => {
                      if (response.data.reminders) {
                        let rem = response.data.reminders.find(
                          k => k.id === i.reminderId
                        )
                        if (rem) {
                          notifications.push(rem.name)
                        }
                      }
                    })
                  }
                  this.model.woData.resourceList.push({
                    selected,
                    triggerNames,
                    name,
                    assignedTo,
                    notifications,
                    id,
                  })
                })
              }
            })
          })
        })
      } else {
        let startDate = new Date()
        startDate.setHours(0, 0, 0, 0)
        let startTime = startDate.getTime()

        this.model.triggerData.isDefaultAllTriggers = true
        if (
          this.$helpers.isLicenseEnabled('SKIP_TRIGGERS') ||
          !this.model.isEdit
        ) {
          this.model.triggerData.triggers.push({
            name: 'Trigger 1',
            type: 4,
            startDate: startDate,
            startTime: startTime,
            basedOn: 'Date',
            schedule: {},
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
            customTrigger: {
              customModuleId: null,
              fieldId: null,
              days: null,
              hours: null,
            },
          })
        }
      }
    },
    resourceListHandler() {
      if (this.model.woData.workOrderType === 'bulk') {
        let filters = []
        let sitesStr = ''
        if (this.model.woData.sites && this.model.woData.sites.length > 0) {
          sitesStr = sitesStr + this.model.woData.sites[0]
          if (this.model.woData.sites.length > 1) {
            for (let i = 1; i < this.model.woData.sites.length; i++) {
              if (this.model.woData.sites[i] > 0) {
                sitesStr = sitesStr + ',' + this.model.woData.sites[i]
              }
            }
          }
        }
        if (this.model.woData.resourceType === 'SPACE_CATEGORY') {
          if (
            this.model.woData.selectedSpaceList &&
            this.model.woData.selectedSpaceList.length !== 0
          ) {
            if (
              this.model.woData.isIncludeSpace === null ||
              this.model.woData.isIncludeSpace === undefined ||
              this.model.woData.isIncludeSpace
            ) {
              this.$set(
                this.model.woData,
                'resourceList',
                this.model.woData.selectedSpaceList
              )
              this.model.woData.resourceList.forEach(resource => {
                resource.triggerNames = ['0']
                resource.assignedTo = null
                resource.notifications = []
                resource.selected = false
              })
            } else {
              if (
                this.model.woData.spacecategoryId &&
                this.model.woData.spacecategoryId > 0
              ) {
                this.$util
                  .loadSpacesContext(4, null, [
                    {
                      key: 'site',
                      operator: 'is',
                      value: sitesStr,
                    },
                    {
                      key: 'building',
                      operator: 'is',
                      value: this.model.woData.selectedBuilding,
                    },
                    {
                      key: 'spaceCategory',
                      operator: 'is',
                      value: this.model.woData.spacecategoryId,
                    },
                  ])
                  .then(response => {
                    this.$set(
                      this.model.woData,
                      'resourceList',
                      response.records
                    )
                    this.model.woData.resourceList = this.model.woData.resourceList.filter(
                      i => {
                        for (
                          let j = 0;
                          j < this.model.woData.selectedSpaceList.length;
                          j++
                        ) {
                          if (
                            this.model.woData.selectedSpaceList[j].id === i.id
                          ) {
                            return false
                          }
                        }
                        return true
                      }
                    )
                    this.model.woData.resourceList.forEach(resource => {
                      resource.triggerNames = ['0']
                      resource.assignedTo = null
                      resource.notifications = []
                      resource.selected = false
                    })
                  })
              }
            }
          } else {
            if (
              this.model.woData.spacecategoryId &&
              this.model.woData.spacecategoryId > 0
            ) {
              this.$util
                .loadSpacesContext(4, null, [
                  {
                    key: 'site',
                    operator: 'is',
                    value: sitesStr,
                  },
                  {
                    key: 'building',
                    operator: 'is',
                    value: this.model.woData.selectedBuilding,
                  },
                  {
                    key: 'spaceCategory',
                    operator: 'is',
                    value: this.model.woData.spacecategoryId,
                  },
                ])
                .then(response => {
                  this.$set(this.model.woData, 'resourceList', response.records)
                  this.model.woData.resourceList.forEach(resource => {
                    resource.triggerNames = ['0']
                    resource.assignedTo = null
                    resource.notifications = []
                    resource.selected = false
                  })
                })
            }
          }
        } else if (this.model.woData.resourceType === 'ASSET_CATEGORY') {
          if (
            this.model.woData.selectedResourceList &&
            this.model.woData.selectedResourceList.length !== 0
          ) {
            if (
              this.model.woData.isIncludeResource === null ||
              this.model.woData.isIncludeResource === undefined ||
              this.model.woData.isIncludeResource
            ) {
              this.$set(
                this.model.woData,
                'resourceList',
                this.model.woData.selectedResourceList
              )
              this.model.woData.resourceList.forEach(resource => {
                resource.triggerNames = ['0']
                resource.assignedTo = null
                resource.notifications = []
                resource.selected = false
              })
            } else {
              if (
                this.model.woData.assetCategoryId &&
                this.model.woData.assetCategoryId > 0
              ) {
                let spaceIds = [this.model.woData.selectedBuilding]
                if (
                  !this.model.woData.selectedBuilding ||
                  this.model.woData.selectedBuilding < 0
                ) {
                  spaceIds = this.model.woData.sites
                }
                this.$util
                  .loadAsset({
                    spaceIds: spaceIds,
                    categoryId: this.model.woData.assetCategoryId,
                  })
                  .then(response => {
                    this.$set(
                      this.model.woData,
                      'resourceList',
                      response.assets.filter(i => {
                        for (
                          let j = 0;
                          j < this.model.woData.selectedResourceList.length;
                          j++
                        ) {
                          if (
                            this.model.woData.selectedResourceList[j].id ===
                            i.id
                          ) {
                            return false
                          }
                        }
                        return true
                      })
                    )
                    this.model.woData.resourceList.forEach(resource => {
                      resource.triggerNames = ['0']
                      resource.assignedTo = null
                      resource.notifications = []
                      resource.selected = false
                    })
                  })
              }
            }
          } else {
            if (
              this.model.woData.assetCategoryId &&
              this.model.woData.assetCategoryId > 0
            ) {
              let spaceIds = [this.model.woData.selectedBuilding]
              if (
                !this.model.woData.selectedBuilding ||
                this.model.woData.selectedBuilding < 0
              ) {
                spaceIds = this.model.woData.sites
              }
              this.$util
                .loadAsset({
                  spaceIds: spaceIds,
                  categoryId: this.model.woData.assetCategoryId,
                })
                .then(response => {
                  this.$set(this.model.woData, 'resourceList', response.assets)
                  this.model.woData.resourceList.forEach(resource => {
                    resource.triggerNames = ['0']
                    resource.assignedTo = null
                    resource.notifications = []
                    resource.selected = false
                  })
                })
            }
          }
        }
      }
    },
    validated() {
      this.$refs.reviewer.validated()
    },
    failed() {
      this.$refs.reviewer.failed()
    },
    sortBySequence(a, b) {
      if (a.taskTemplates[0].sequence < b.taskTemplates[0].sequence) {
        return -1
      }

      if (a.taskTemplates[0].sequence > b.taskTemplates[0].sequence) {
        return 1
      }

      return 0
    },
    fillTasks(
      sectionTemplates,
      resourceType,
      creationType,
      singleResource,
      resourceMap,
      triggerMap
    ) {
      if (sectionTemplates) {
        this.model.taskData.taskSections.splice(0, 1)
      } else {
        for (let i = 0; i < this.model.taskData.taskSections.length; i++) {
          if (creationType === 2) {
            if (resourceType === 'ALL_FLOORS') {
              this.model.taskData.taskSections[i].category = ['CURRENT_FLOOR']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_FLOOR',
                ]
              }
            } else if (resourceType === 'SPACE_CATEGORY') {
              this.model.taskData.taskSections[i].category = ['CURRENT_SPACE']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_SPACE',
                ]
              }
            } else if (resourceType === 'BUILDINGS') {
              this.model.taskData.taskSections[i].category = [
                'CURRENT_BUILDING',
              ]
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_BUILDING',
                ]
              }
            } else if (resourceType === 'ALL_SITES') {
              // fill in task-section & tasks category for ALL_SITES
              this.model.taskData.taskSections[i].category = ['ALL_SITES']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'ALL_SITES',
                ]
              }
            } else if (resourceType === 'ASSET_CATEGORY') {
              this.model.taskData.taskSections[i].category = ['CURRENT_ASSET']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_ASSET',
                ]
              }
            }
          } else {
            if (singleResource.resourceType === 2) {
              this.model.taskData.taskSections[i].category = ['CURRENT_ASSET']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_ASSET',
                ]
              }
            } else if (singleResource.resourceType === 1) {
              this.model.taskData.taskSections[i].category = ['CURRENT_SPACE']
              for (
                let j = 0;
                j < this.model.taskData.taskSections[i].tasks.length;
                j++
              ) {
                this.model.taskData.taskSections[i].tasks[j].category = [
                  'CURRENT_SPACE',
                ]
              }
            }
          }
        }
        return
      }
      sectionTemplates.sort(this.sortBySequence)
      sectionTemplates.forEach((section, i) => {
        let taskSection = {
          name: 'Untitled Section',
          category: null,
          dummyValue: [1],
          inputType: null,
          resourceLabel: '',
          selectedResourceList: [],
          isIncludeResource: null,
          attachmentRequired: false,
          enableInput: false,
          validation: null,
          minSafeLimit: null,
          maxSafeLimit: null,
          options: [],
          tasks: [],
          triggers: [],
          additionInfo: {
            defaultValue: '',
            failureValue: '',
            woCreateFormId: null,
            deviationOperatorId: null,
            remarksRequired: false,
            remarkOptionValues: [],
            attachmentOptionValues: [],
          },
        }

        if (!section.additionInfo) {
          section.additionInfo = {
            defaultValue: '',
            failureValue: '',
            woCreateFormId: null,
            deviationOperatorId: null,
            remarksRequired: false,
            remarkOptionValues: [],
            attachmentOptionValues: [],
          }
        }

        Object.keys(section)
          .filter(key => key in taskSection)
          .forEach(key => {
            taskSection[key] = this.$helpers.cloneObject(section[key])
          })

        taskSection.tasks = []
        taskSection.enableInput = section.inputType > 1
        taskSection.inputType = String(section.inputType)
        taskSection.assignmentType = section.assignmentType
        taskSection.selectedResourceList = []
        if (section.pmIncludeExcludeResourceContexts) {
          section.pmIncludeExcludeResourceContexts.forEach(i =>
            taskSection.selectedResourceList.push({ id: i.resourceId })
          )
        }
        if (section.additionInfo) {
          if (section.additionInfo.options) {
            taskSection.options = section.additionInfo.options.map(x => {
              return { name: x }
            })
          }
          taskSection.additionInfo.defaultValue =
            section.additionInfo.defaultValue
          taskSection.additionInfo.failureValue =
            section.additionInfo.failureValue
          taskSection.additionInfo.woCreateFormId =
            section.additionInfo.woCreateFormId
          taskSection.additionInfo.deviationOperatorId =
            section.additionInfo.deviationOperatorId
          taskSection.additionInfo.remarksRequired =
            section.additionInfo.remarksRequired
          taskSection.additionInfo.remarkOptionValues =
            section.additionInfo.remarkOptionValues
          taskSection.additionInfo.attachmentOptionValues =
            section.additionInfo.attachmentOptionValues
        }
        if (
          section.pmTaskSectionTemplateTriggers &&
          section.pmTaskSectionTemplateTriggers.length
        ) {
          taskSection.triggers = []
          section.pmTaskSectionTemplateTriggers.forEach(ptc => {
            ptc.triggerName = triggerMap[ptc.triggerId].name
            ptc.executeIfNotInTime =
              ptc.executeIfNotInTime > 0
                ? ptc.executeIfNotInTime / (24 * 60 * 60)
                : -1
            taskSection.triggers.push(ptc)
          })
        } else {
          taskSection.triggers = [{ triggerName: 'All' }]
        }

        if (creationType === 2) {
          if (taskSection.assignmentType === 5) {
            if (resourceType === 'ALL_FLOORS') {
              taskSection.category = ['CURRENT_FLOOR']
            } else if (resourceType === 'SPACE_CATEGORY') {
              taskSection.category = ['CURRENT_SPACE']
            } else if (resourceType === 'ASSET_CATEGORY') {
              taskSection.category = ['CURRENT_ASSET']
            } else if (resourceType === 'BUILDINGS') {
              taskSection.category = ['CURRENT_BUILDING']
            } else if (resourceType === 'ALL_SITES') {
              taskSection.category = ['ALL_SITES']
            }
          } else if (taskSection.assignmentType === 4) {
            taskSection.category = ['asset', section.assetCategoryId]
          } else if (taskSection.assignmentType === 3) {
            taskSection.category = ['space', section.spaceCategoryId]
          } else if (taskSection.assignmentType === 8) {
            taskSection.category = ['ALL_SITES']
          }
        } else {
          if (singleResource.resourceType === 2) {
            taskSection.category = ['CURRENT_ASSET']
          } else if (singleResource.resourceType === 1) {
            if (
              taskSection.assignmentType === 5 ||
              !taskSection.assignmentType ||
              taskSection.assignmentType === -1
            ) {
              taskSection.assignmentType = 5
              taskSection.category = ['CURRENT_SPACE']
            } else if (taskSection.assignmentType === 4) {
              taskSection.category = ['asset', section.assetCategoryId]
            } else if (taskSection.assignmentType === 3) {
              taskSection.category = ['space', section.spaceCategoryId]
            }
          }
        }

        let task = {
          name: '',
          resourceLabel: '',
          description: '',
          inputType: null,
          attachmentRequired: false,
          enableInput: false,
          readingFieldId: null,
          validation: null,
          inputValidationRuleId: null,
          minSafeLimit: null,
          maxSafeLimit: null,
          options: [],
          selectedResourceList: [],
          isIncludeResource: null,
          category: taskSection.category,
          readingFields: [],
          createWoOnFailure: false,
          additionInfo: {
            defaultValue: '',
            failureValue: '',
            woCreateFormId: null,
            deviationOperatorId: null,
            uniqueId: null,
            remarksRequired: false,
            remarkOptionValues: [],
            attachmentOptionValues: [],
            createWoOnFailure: false,
          },
        }

        if (creationType === 2) {
          if (section.taskTemplates) {
            section.taskTemplates.forEach(t => {
              if (t.assignmentType === 5) {
                if (taskSection.category[0] === 'CURRENT_FLOOR') {
                  t.category = ['CURRENT_FLOOR']
                } else if (
                  taskSection.category[0] === 'SPACE_CATEGORY' ||
                  taskSection.category[0] === 'space'
                ) {
                  t.category = ['CURRENT_SPACE']
                } else if (
                  taskSection.category[0] === 'ASSET_CATEGORY' ||
                  taskSection.category[0] === 'asset'
                ) {
                  t.category = ['CURRENT_ASSET']
                } else if (taskSection.category[0] === 'BUILDINGS') {
                  t.category = ['CURRENT_BUILDING']
                } else if (taskSection.category[0] === 'ALL_SITES') {
                  t.category = ['ALL_SITES']
                }
              } else if (t.assignmentType === 4) {
                t.category = ['asset', t.assetCategoryId]
              } else if (t.assignmentType === 3) {
                t.category = ['space', t.spacecategoryId]
              } else if (t.assignmentType === 8) {
                t.category = ['ALL_SITES']
              }
              t.enableInput = t.inputType > 1
              t.inputType = String(t.inputType)
              if (t.additionInfo && t.additionInfo.options) {
                t.options = t.additionInfo.options.map(x => {
                  return { name: x }
                })
              }

              t.selectedResourceList = []
              if (t.pmIncludeExcludeResourceContexts) {
                t.pmIncludeExcludeResourceContexts.forEach(i =>
                  t.selectedResourceList.push({ id: i.resourceId })
                )
              }
              if (t.readingRules && t.readingRules.length) {
                let rule = t.readingRules[0]
                t.inputValidationRuleId = rule.id
                if (rule.workflow.resultEvaluator === '(b!=-1)&&(a<b)') {
                  t.validation = 'incremental'
                } else if (rule.workflow.resultEvaluator === '(b!=-1)&&(a>b)') {
                  t.validation = 'decremental'
                } else if (
                  rule.workflow.resultEvaluator === '(b!=-1&&a<b)||(c!=-1&&a>c)'
                ) {
                  t.validation = 'safeLimit'
                  let min = rule.workflow.expressions.find(val => {
                    return val.name === 'b'
                  }).constant
                  t.minSafeLimit = min === '-1' ? null : min
                  let max = rule.workflow.expressions.find(val => {
                    return val.name === 'c'
                  }).constant
                  t.maxSafeLimit = max === '-1' ? null : max
                }
              }
              taskSection.tasks.push(this.$helpers.cloneFromSchema(task, t))
            })
          }
        } else {
          if (section.taskTemplates) {
            section.taskTemplates.forEach((t, taskIndex) => {
              if (t.assignmentType === 5) {
                if (
                  taskSection.category[0] === 'CURRENT_SPACE' ||
                  taskSection.category[0] === 'space'
                ) {
                  t.category = ['CURRENT_SPACE']
                } else if (
                  taskSection.category[0] === 'CURRENT_ASSET' ||
                  taskSection.category[0] === 'asset'
                ) {
                  t.category = ['CURRENT_ASSET']
                } else if (taskSection.category[0] === 'CURRENT_BUILDING') {
                  t.category = ['CURRENT_BUILDING']
                }
              } else if (t.assignmentType === 4) {
                t.category = ['asset', t.assetCategoryId]
              } else if (t.assignmentType === 3) {
                t.category = ['space', t.spacecategoryId]
              } else if (!t.assignmentType || t.assignmentType === -1) {
                if (
                  t.readingFieldId > 0 &&
                  t.resourceId > 0 &&
                  resourceMap[t.resourceId]
                ) {
                  let resource = resourceMap[t.resourceId]
                  if (taskSection.category[0] === 'CURRENT_SPACE') {
                    if (resource.resourceType === 1) {
                      if (
                        resource.category &&
                        resource.category.id &&
                        resource.id !== singleResource.id
                      ) {
                        t.assignmentType = 3
                        t.category = ['space', resource.category.id]
                        t.pmIncludeExcludeResourceContexts = [resource]
                      }
                    } else if (resource.resourceType === 2) {
                      if (
                        resource.category &&
                        resource.category.id &&
                        resource.id !== singleResource.id
                      ) {
                        t.assignmentType = 3
                        t.category = ['asset', resource.category.id]
                        t.pmIncludeExcludeResourceContexts = [resource]
                      }
                    }
                  }
                } else {
                  t.assignmentType = 5
                  if (taskSection.category[0] === 'CURRENT_SPACE') {
                    t.category = ['CURRENT_SPACE']
                  } else if (taskSection.category[0] === 'CURRENT_ASSET') {
                    t.category = ['CURRENT_ASSET']
                  } else if (taskSection.category[0] === 'ALL_SITES') {
                    t.category = ['ALL_SITES']
                    //t.assignmentType = 8 // commented to change assignmentType of taskSection assignmentType to 5
                  }
                }
              }
              t.enableInput = t.inputType > 1
              t.inputType = String(t.inputType)
              if (t.additionInfo && t.additionInfo.options) {
                t.options = t.additionInfo.options.map(x => {
                  return { name: x }
                })
              }
              t.selectedResourceList = []
              if (t.pmIncludeExcludeResourceContexts) {
                t.pmIncludeExcludeResourceContexts.forEach(i =>
                  t.selectedResourceList.push({ id: i.resourceId })
                )
              }
              if (t.readingRules && t.readingRules.length) {
                let rule = t.readingRules[0]
                t.inputValidationRuleId = rule.id
                if (rule.workflow.resultEvaluator === '(b!=-1)&&(a<b)') {
                  t.validation = 'incremental'
                } else if (rule.workflow.resultEvaluator === '(b!=-1)&&(a>b)') {
                  t.validation = 'decremental'
                } else if (
                  rule.workflow.resultEvaluator === '(b!=-1&&a<b)||(c!=-1&&a>c)'
                ) {
                  t.validation = 'safeLimit'
                  let min = rule.workflow.expressions.find(val => {
                    return val.name === 'b'
                  }).constant
                  t.minSafeLimit = min === '-1' ? null : min
                  let max = rule.workflow.expressions.find(val => {
                    return val.name === 'c'
                  }).constant
                  t.maxSafeLimit = max === '-1' ? null : max
                }
              }
              taskSection.tasks.push(this.$helpers.cloneFromSchema(task, t))
            })
          }
        }
        this.model.taskData.taskSections.push(taskSection)
      })
    },
    fillPreRequests(
      sectionTemplates,
      photoMandatory,
      allowNegativePreRequisite,
      approvers
    ) {
      this.model.preRequestData.photoMandatory = photoMandatory
      if (allowNegativePreRequisite === -1) {
        allowNegativePreRequisite = 2
      }
      this.model.preRequestData.allowNegativePreRequisite = allowNegativePreRequisite
      this.model.preRequestData.allowNegative =
        this.model.preRequestData.allowNegativePreRequisite === 3
      if (!approvers) {
        approvers = []
        approvers.push({ sharingType: null, approversId: null })
      } else if (approvers.length === 0) {
        approvers.push({ sharingType: null, approversId: null })
      } else {
        approvers.forEach(approver => {
          this.getFieldDrop(approver)
          if (approver.sharingType === 1) {
            approver.approversId = approver.userId
          } else if (approver.sharingType === 2) {
            approver.approversId = parseInt(approver.roleId)
          } else if (approver.sharingType === 3) {
            approver.approversId = parseInt(approver.groupId)
          }
        })
      }
      this.model.preRequestData.approvers = approvers
      if (sectionTemplates) {
        this.model.preRequestData.preRequestSections.splice(0, 1)
        sectionTemplates.sort(this.sortBySequence)
        sectionTemplates.forEach((section, i) => {
          let taskSection = {
            name: 'Untitled Section',
            dummyValue: [1],
            inputType: null,
            selectedResourceList: [],
            isIncludeResource: null,
            attachmentRequired: false,
            enableInput: false,
            validation: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            options: [],
            tasks: [],
            triggers: [],
            additionInfo: {
              truevalue: null,
              falsevalue: null,
              defaultValue: '',
              failureValue: '',
              woCreateFormId: null,
              deviationOperatorId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
          }
          Object.keys(section)
            .filter(key => key in taskSection)
            .forEach(key => {
              taskSection[key] = this.$helpers.cloneObject(section[key])
            })

          taskSection.preRequests = []
          taskSection.enableInput = section.inputType > 1
          taskSection.inputType = String(section.inputType)
          taskSection.assignmentType = section.assignmentType
          taskSection.selectedResourceList = []
          if (section.additionInfo) {
            if (section.additionInfo.options) {
              taskSection.options = section.additionInfo.options.map(x => {
                return { name: x }
              })
            }
            taskSection.additionInfo.truevalue = section.additionInfo.truevalue
            taskSection.additionInfo.falsevalue =
              section.additionInfo.falsevalue
            taskSection.additionInfo.defaultValue =
              section.additionInfo.defaultValue
            taskSection.additionInfo.failureValue =
              section.additionInfo.failureValue
            taskSection.additionInfo.woCreateFormId =
              section.additionInfo.woCreateFormId
            taskSection.additionInfo.deviationOperatorId =
              section.additionInfo.deviationOperatorId
            taskSection.additionInfo.remarksRequired =
              section.additionInfo.remarksRequired
            taskSection.additionInfo.remarkOptionValues =
              section.additionInfo.remarkOptionValues
            taskSection.additionInfo.attachmentOptionValues =
              section.additionInfo.attachmentOptionValues
          }

          let task = {
            name: '',
            description: '',
            inputType: null,
            attachmentRequired: false,
            enableInput: false,
            readingFieldId: null,
            validation: null,
            inputValidationRuleId: null,
            minSafeLimit: null,
            maxSafeLimit: null,
            options: [],
            selectedResourceList: [],
            isIncludeResource: null,
            category: taskSection.category,
            readingFields: [],
            additionInfo: {
              truevalue: null,
              falsevalue: null,
              defaultValue: '',
              failureValue: '',
              woCreateFormId: null,
              deviationOperatorId: null,
              remarksRequired: false,
              remarkOptionValues: [],
              attachmentOptionValues: [],
            },
          }
          if (section.taskTemplates) {
            section.taskTemplates.forEach(t => {
              t.enableInput = t.inputType > 1
              t.inputType = String(t.inputType)
              if (t.additionInfo && t.additionInfo.options) {
                t.options = t.additionInfo.options.map(x => {
                  return { name: x }
                })
              }
              taskSection.preRequests.push(
                this.$helpers.cloneFromSchema(task, t)
              )
            })
          }
          this.model.preRequestData.preRequestSections.push(taskSection)
        })
      }
    },
    getFieldDrop(approver) {
      approver.approversId = null
      approver.approvesDataType = []
      if (approver.sharingType === 1) {
        approver.approvesDataType = this.users
      } else if (approver.sharingType === 2) {
        approver.approvesDataType = this.roles
      } else if (approver.sharingType === 3) {
        approver.approvesDataType = this.teams
      }
    },
    formSelect(view) {
      let currView = null
      for (let key in this.show) {
        if (this.show[key]) {
          currView = key
          break
        }
      }
      if (view === currView) {
        return
      }
      if (currView === 'workorder') {
        let isValid = this.validateMultiSiteWO()
        if (!isValid) {
          return
        }
      } else if (currView === 'tasks') {
        if (view !== 'workorder') {
          let isValid = this.validateTaskForm()
          if (!isValid) {
            return
          }
        }
      }
      for (let key in this.show) {
        this.show[key] = false
      }
      this.show[view] = true
      this.currentView = view
    },
    moveToNext() {
      if (this.currentView === 'pmconfig') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'workorderNx'
        this.show['workorderNx'] = true
      } else if (this.currentView === 'workorderNx') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'trigger'
        this.show['trigger'] = true
      } else if (this.currentView === 'trigger') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'prerequests'
        this.show['prerequests'] = true
      } else if (this.currentView === 'prerequests') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'tasks'
        this.show['tasks'] = true
      } else if (this.currentView === 'tasks') {
        for (let key in this.show) {
          this.show[key] = false
        }

        if (
          this.model.woData.workOrderType === 'bulk' &&
          this.model.triggerData.triggers.length
        ) {
          this.currentView = 'resourceplan'
          this.show['resourceplan'] = true
        } else {
          this.currentView = 'review'
          this.show['review'] = true
        }
      } else if (this.currentView === 'resourceplan') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'review'
        this.show['review'] = true
      }
    },
    moveToPrevious() {
      if (this.currentView === 'workorder') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'pmconfig'
        this.show['pmconfig'] = true
      } else if (this.currentView === 'trigger') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'workorder'
        this.show['workorder'] = true
      } else if (this.currentView === 'prerequests') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'trigger'
        this.show['trigger'] = true
      } else if (this.currentView === 'tasks') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'prerequests'
        this.show['prerequests'] = true
      } else if (this.currentView === 'resourceplan') {
        for (let key in this.show) {
          this.show[key] = false
        }
        this.currentView = 'tasks'
        this.show['tasks'] = true
      } else if (this.currentView === 'review') {
        for (let key in this.show) {
          this.show[key] = false
        }
        if (this.model.triggerData.triggers.length) {
          this.currentView = 'resourceplan'
          this.show['resourceplan'] = true
        } else {
          this.currentView = 'trigger'
          this.show['trigger'] = true
        }
      }
    },
    setFormObject(formObject) {
      this.formObj = formObject

      let { fields } = this.formObj || {}
      // Handling Custom Duration fields
      if (!isEmpty(fields)) {
        fields.forEach(field_ => {
          let { field } = field_ || {}
          if (!isEmpty(field)) {
            if (
              field.displayType === 'DURATION' &&
              field.dataTypeEnum === 'NUMBER'
            ) {
              this.durationFields.push(field.name)
            }
          }
        })
      }
      if (!isEmpty(this.durationFields)) {
        let { defaultDuration = 0 } = this
        this.durationFields.forEach(name => {
          if (this.$getProperty(this.model.woData.woModel, `${name}`)) {
            let unixTime =
              this.$util.daysHoursToUnixTime(
                this.model.woData.woModel[`${name}`]
              ) || -1
            if (unixTime === -1) {
              this.model.woData.woModel[`${name}`] = {
                date: defaultDuration,
                hours: defaultDuration,
              }
              if (this.$getProperty(this.model.actualWoData, 'data')) {
                this.model.actualWoData.data[`${name}`] = {
                  date: defaultDuration,
                  hours: defaultDuration,
                }
              }
            }
          }
        })
      }
    },
  },
}
</script>
<style>
.wo-form-main-container {
  height: 100%;
  overflow-y: scroll;
  overflow-x: hidden;
  padding-bottom: 50px;
}
</style>
