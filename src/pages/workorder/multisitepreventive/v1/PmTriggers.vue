<template>
  <div>
    <div v-if="pm.triggers && pm.triggers.length" class="pm-trigger-scroll">
      <!-- trigger block -->

      <div class="fc-black3-16 text-capitalize mL30 mT30">
        Trigger List
      </div>

      <div
        class="clearboth mT20 mL30"
        v-if="pm.triggers && pm.triggers.length"
        style="border: 1px solid #e6ecf3;border-bottom: none;"
      >
        <el-row class="trigger-table-heading">
          <el-col :span="2" class="text-left pL15">
            <InlineSvg
              src="svgs/speedmeter"
              iconClass="icon icon-md vertical-middle"
              style="visibility: hidden"
            ></InlineSvg>
          </el-col>
          <el-col :span="7">
            <div class="fc-black-11 fwBold text-uppercase">Name</div>
          </el-col>
          <el-col :span="4">
            <div class="fc-black-11 fwBold text-uppercase">Type</div>
          </el-col>
          <el-col :span="9">
            <div class="fc-black-11 fwBold text-uppercase">Trigger Message</div>
          </el-col>
          <el-col :span="2"> </el-col>
        </el-row>
        <div v-for="(trigger, index) in pm.triggers" :key="index">
          <el-row class="border-bottom4 pT15 pB15 pm-trigger-hover">
            <div
              v-if="trigger.triggerExecutionSource === 1"
              class="flex-middle"
            >
              <el-col :span="2" class="text-center pL15">
                <InlineSvg
                  src="svgs/calendar"
                  iconClass="icon icon-md vertical-middle fc-grey-svg-color"
                ></InlineSvg>
              </el-col>
              <el-col :span="7" class="pL15">
                <span class="label-txt-black line-height20">{{
                  trigger.name
                }}</span>
              </el-col>
              <el-col :span="4" style="padding-left: 6px;">
                <span class="label-txt-black bold">Scheduled</span>
              </el-col>
              <el-col :span="9">
                <div class="fc-list-subject line-height20 fw4">
                  {{ trigger.scheduleMsg }}
                </div>
              </el-col>
              <el-col :span="2" v-if="trigger.triggerType === 1">
                <div class="fc-dark-blue-txt13">Default</div>
              </el-col>
            </div>
            <div v-if="trigger.triggerExecutionSource === 2">
              <el-col :span="2" class="text-center pL15">
                <InlineSvg
                  src="svgs/speedmeter"
                  iconClass="icon icon-md vertical-middle"
                ></InlineSvg>
              </el-col>
              <el-col :span="7" class="pL15">
                <span class="label-txt-black line-height20">{{
                  trigger.name
                }}</span>
              </el-col>
              <el-col :span="4" style="padding-left: 6px;">
                <span class="label-txt-black bold">Reading</span>
              </el-col>
              <el-col :span="9">
                <div class="fc-list-subject line-height20 fw4">
                  {{ trigger.readingRule.readingField.displayName }}
                </div>
                <div>
                  {{
                    'Exceeds ' +
                      trigger.startReading +
                      (trigger.readingRule.readingField.unit
                        ? ' ' + trigger.readingRule.readingField.unit
                        : '') +
                      ' and at every successive ' +
                      trigger.readingInterval +
                      (trigger.readingRule.readingField.unit
                        ? ' ' + trigger.readingRule.readingField.unit
                        : '')
                  }}
                </div>
              </el-col>
              <el-col :span="2" v-if="trigger.triggerType === 1">
                <div class="fc-dark-blue-txt13">Default</div>
              </el-col>
            </div>
            <div v-if="trigger.triggerExecutionSource === 3">
              <el-col :span="2" class="text-center pL15">
                <InlineSvg
                  src="svgs/alarm"
                  iconClass="icon icon-md vertical-middle fc-grey-svg3"
                ></InlineSvg>
              </el-col>
              <el-col :span="7" class="pL15">
                <span class="label-txt-black line-height20">{{
                  trigger.name
                }}</span>
              </el-col>
              <el-col :span="4" style="padding-left: 6px;">
                <span class="label-txt-black bold">Alarm Rule</span>
              </el-col>
              <el-col :span="9">
                <div
                  v-for="condition in trigger.workFlowRule.criteria.conditions"
                  :key="condition"
                  class="fc-list-subject line-height20 fw4"
                >
                  {{ condition.fieldName }}
                  {{ condition.operator.toLowerCase() }} {{ condition.value }}
                </div>
              </el-col>
              <el-col :span="2" v-if="trigger.triggerType === 1">
                <div class="fc-dark-blue-txt13">Default</div>
              </el-col>
            </div>
            <div v-if="trigger.triggerExecutionSource === 4">
              <el-col :span="2" class="text-center pL15">
                <InlineSvg
                  src="svgs/calendar"
                  iconClass="icon icon-md vertical-middle fc-grey-svg-color"
                ></InlineSvg>
              </el-col>
              <el-col :span="7" class="pL15">
                <span class="label-txt-black line-height20">{{
                  trigger.name
                }}</span>
              </el-col>
              <el-col :span="4" style="padding-left: 6px;">
                <span class="label-txt-black bold">Manual</span>
              </el-col>
              <el-col :span="9">
                <template v-if="trigger.sharingContext">
                  <div
                    v-for="(sharing, key) in trigger.sharingContext"
                    :key="key"
                  >
                    <div
                      v-if="sharing.type === 1"
                      class="fc-list-subject line-height20 fw4"
                    >
                      {{ $account.user.name }}
                    </div>
                    <div
                      v-if="sharing.type === 2"
                      class="fc-list-subject line-height20 fw4"
                    >
                      {{ findRoleName(sharing.roleId) }}
                    </div>
                    <div
                      v-if="sharing.type === 3"
                      class="fc-list-subject line-height20 fw4"
                    >
                      {{ $store.getters.getGroup(sharing.groupId).name }}
                    </div>
                  </div>
                </template>
                <div v-else class="fc-list-subject line-height20 fw4">
                  Everyone
                </div>
              </el-col>
              <el-col :span="2" v-if="trigger.triggerType === 1">
                <div class="fc-dark-blue-txt13">Default</div>
              </el-col>
            </div>
            <div v-if="trigger.triggerExecutionSource === 5">
              <el-col :span="2" class="text-center pL15">
                <InlineSvg
                  src="svgs/calendar"
                  iconClass="icon icon-md vertical-middle fc-grey-svg-color"
                ></InlineSvg>
              </el-col>
              <el-col :span="7" class="pL15">
                <span class="label-txt-black line-height20">{{
                  trigger.name
                }}</span>
              </el-col>
              <el-col :span="4" style="padding-left: 6px;">
                <span class="label-txt-black bold">Custom</span>
              </el-col>
              <el-col :span="9">
                <div class="fc-list-subject line-height20 fw4">
                  {{ trigger.customModuleName }} - {{ trigger.dateFieldName }}
                </div>
              </el-col>
              <el-col :span="2" v-if="trigger.triggerType === 1">
                <div class="fc-dark-blue-txt13">Default</div>
              </el-col>
            </div>
          </el-row>
        </div>
      </div>
      <!-- resource planning -->
      <resource-plan
        v-if="
          !model.isLoading &&
            model.woData.workOrderType === 'bulk' &&
            model.triggerData.triggers.length
        "
        :pmid="pm.id"
        :pm="pm"
        :resources="resources"
        :summaryEdit="true"
        :model="model"
        class="mT60"
      ></resource-plan>
    </div>
    <div v-else>
      <div
        class="flex-middle width100 height80vh justify-center shadow-none white-bg-block flex-direction-column"
      >
        <InlineSvg
          src="svgs/emptystate/history"
          iconClass="icon icon-xxxxlg mR10"
        ></InlineSvg>
        <div class="nowo-label text-center pT10">
          No Triggers Available
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import ResourcePlan from 'pages/workorder/preventive/PmSummaryResourcePlanner'
import InlineSvg from '@/InlineSvg'
export default {
  props: ['pm', 'resources', 'pmObject'],
  created() {
    this.$store.dispatch('loadAssetCategory')
    this.$store.dispatch('loadSpaceCategory')
    if (!this.$store.state.roles || !this.$store.state.roles.length) {
      this.$store.dispatch('loadRoles')
    }
    this.$store.dispatch('loadGroups')
  },
  data() {
    return {
      value: '',
      model: {
        isEdit: false,
        isLoading: false,
        emitForm: false,
        isClone: false,
        woData: {
          woModel: null,
          workOrderType: 'single',
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
          singleResource: null,
          quickSearchQuery: '',
          spaceAssetDisplayName: '',
          resourceList: [],
          isPreventOnNoTask: true,
          isSignatureRequired: false,
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
                  },
                },
              ],
            },
          ],
        },
        triggerData: {
          defaultAssignedTo: null,
          triggers: [],
          reminders: [],
        },
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
      },
      remindersTemplate: {
        name: null,
        type: null,
        duration: null,
        isEmail: null,
        isSms: null,
        isMobile: null,
        emailtemplate: null,
        smstemplate: null,
        mobiletemplate: null,
      },
    }
  },
  components: {
    'resource-plan': ResourcePlan,
    InlineSvg,
  },
  mounted() {
    this.model.isEdit = true
    this.model.isLoading = true
    // let siteId = this.$route.query.site
    if (this.model.isEdit) {
      let woData = this.$helpers.cloneObject(this.pmObject.workorder)
      woData.site = { id: '' }
      woData.site.id = woData.siteId
      delete woData.siteId

      woData.duration = this.$util.unixTimeToDaysHours(woData.duration)

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

      this.model.woData.isSignatureRequired = woData.isSignatureRequired
      delete woData.isSignatureRequired

      this.model.woData.isPreventOnNoTask = this.pmObject.preventivemaintenance.preventOnNoTask

      this.$set(this.model.woData, 'woModel', woData)

      this.model.woData.assetCategoryId = this.pmObject.preventivemaintenance.assetCategoryId
      this.model.woData.spacecategoryId = String(
        this.pmObject.preventivemaintenance.spaceCategoryId
      )

      this.model.woData.woModel.preventOnNoTask = this.pmObject.preventivemaintenance.preventOnNoTask

      this.model.woData.workOrderType =
        this.pmObject.preventivemaintenance.pmCreationType === 1
          ? 'single'
          : 'bulk'

      this.model.woData.selectedBuilding = this.pmObject.preventivemaintenance.baseSpaceId

      let resourceMap = {}
      if (this.pmObject.listOfTasks) {
        this.pmObject.listOfTasks.forEach(t => {
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
      if (this.pmObject.preventivemaintenance.triggers) {
        this.pmObject.preventivemaintenance.triggers.forEach(i => {
          triggerMap[i.id] = i
        })
      }

      this.$nextTick(() => {
        // when building is filled watchers fire
        if (this.pmObject.preventivemaintenance.pmCreationType === 2) {
          if (this.pmObject.preventivemaintenance.assignmentType === 1) {
            this.model.woData.resourceType = 'ALL_FLOORS'
            this.$nextTick(() => {
              // when resourceType is filled watchers fire
              if (
                this.pmObject.preventivemaintenance
                  .pmIncludeExcludeResourceContexts
              ) {
                this.pmObject.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                  x => {
                    this.model.woData.selectedFloorList.push({
                      id: x.resourceId,
                    })
                  }
                )
              }
              this.fillTasks(
                this.pmObject.sectionTemplates,
                this.model.woData.resourceType,
                2,
                null,
                resourceMap,
                triggerMap
              )
              this.model.isLoading = false
            })
          } else if (this.pmObject.preventivemaintenance.assignmentType === 3) {
            this.model.woData.resourceType = 'SPACE_CATEGORY'
            this.$nextTick(() => {
              if (
                this.pmObject.preventivemaintenance
                  .pmIncludeExcludeResourceContexts
              ) {
                this.pmObject.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                  x => {
                    this.model.woData.selectedSpaceList.push({
                      id: x.resourceId,
                    })
                  }
                )
              }
              this.fillTasks(
                this.pmObject.sectionTemplates,
                this.model.woData.resourceType,
                2,
                null,
                resourceMap,
                triggerMap
              )
              this.model.isLoading = false
            })
          } else if (this.pmObject.preventivemaintenance.assignmentType === 4) {
            this.model.woData.resourceType = 'ASSET_CATEGORY'
            this.$nextTick(() => {
              if (
                this.pmObject.preventivemaintenance
                  .pmIncludeExcludeResourceContexts
              ) {
                this.pmObject.preventivemaintenance.pmIncludeExcludeResourceContexts.forEach(
                  x => {
                    this.model.woData.selectedResourceList.push({
                      id: x.resourceId,
                    })
                  }
                )
              }
              this.fillTasks(
                this.pmObject.sectionTemplates,
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
              this.pmObject.sectionTemplates,
              this.model.woData.resourceType,
              1,
              this.model.woData.singleResource,
              resourceMap,
              triggerMap
            )
            this.model.isLoading = false
          })
        }

        if (this.pmObject.workorder.assignedTo) {
          this.model.triggerData.defaultAssignedTo = this.pmObject.workorder.assignedTo.id
        }

        if (this.pmObject.reminders) {
          this.model.triggerData.reminders = []
          if (this.pmObject.reminders && this.pmObject.reminders.length) {
            this.pmObject.reminders.forEach(r => {
              this.model.triggerData.reminders.push(
                this.$helpers.cloneFromSchema(this.remindersTemplate, r)
              )
            })
          }
        }

        if (this.pmObject.preventivemaintenance.triggers) {
          this.model.triggerData.triggers = this.pmObject.preventivemaintenance.triggers.map(
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
              if (trigger.triggerExecutionSource === 1) {
                newTrigger.type = 1
                trigger.schedule.timeObjects = null
                newTrigger.initialSchedule = trigger.schedule
                newTrigger.initialSchedule.facilioFrequency = trigger.frequency
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
                    Object.keys(newTrigger.criteria.conditions).forEach(i => {
                      delete newTrigger.criteria.conditions[i].computedValues
                      delete newTrigger.criteria.conditions[i]
                        .computedWhereClause
                    })
                  }
                }
              }
              return newTrigger
            }
          )
        }

        this.$nextTick(() => {
          let resources = this.pmObject.preventivemaintenance.resourcePlanners
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
                this.pmObject.preventivemaintenance.triggers
              ) {
                r.triggerContexts.forEach(k => {
                  let trigger = this.pmObject.preventivemaintenance.triggers.find(
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
                  if (this.pmObject.reminders) {
                    let rem = this.pmObject.reminders.find(
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
    }
  },

  computed: {},
  methods: {
    findRoleName(id) {
      let role = this.$store.state.roles.filter(i => i.roleId === id)
      return role[0].name
    },
    resourceListHandler() {
      if (this.model.woData.workOrderType === 'bulk') {
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
                      value: this.model.woData.woModel.site.id,
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
                    value: this.model.woData.woModel.site.id,
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
                let spaceId = this.model.woData.selectedBuilding
                if (
                  !this.model.woData.selectedBuilding ||
                  this.model.woData.selectedBuilding < 0
                ) {
                  spaceId = this.model.woData.woModel.site.id
                }
                this.$util
                  .loadAsset({
                    spaceId: spaceId,
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
              let spaceId = this.model.woData.selectedBuilding
              if (
                !this.model.woData.selectedBuilding ||
                this.model.woData.selectedBuilding < 0
              ) {
                spaceId = this.model.woData.woModel.site.id
              }
              this.$util
                .loadAsset({
                  spaceId: spaceId,
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
          },
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
            }
          } else if (taskSection.assignmentType === 4) {
            taskSection.category = ['asset', section.assetCategoryId]
          } else if (taskSection.assignmentType === 3) {
            taskSection.category = ['space', section.spaceCategoryId]
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
              taskSection.category = ['space', section.spacecategoryId]
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
          additionInfo: {
            defaultValue: '',
            failureValue: '',
            woCreateFormId: null,
            deviationOperatorId: null,
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
                }
              } else if (t.assignmentType === 4) {
                t.category = ['asset', t.assetCategoryId]
              } else if (t.assignmentType === 3) {
                t.category = ['space', t.spacecategoryId]
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
  },
}
</script>
<style lang="scss">
.pm-trigger-scroll {
  max-width: 840px;
  height: calc(100vh - 150px);
  overflow-y: scroll;
  overflow-x: hidden;
  padding-bottom: 100px;
}
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
.trigger-table-heading {
  background-color: #f7fafa;
  padding: 9px 20px;
  line-height: 25px;
  border-bottom: solid 1px #e6ecf3;
}
.pm-trigger-hover:hover {
  background-color: #f1f8fa;
  cursor: pointer;
}
</style>
