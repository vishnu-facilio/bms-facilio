<template>
  <div
    class="form-data-creation inspection-form-template"
    :class="[
      !$validation.isEmpty(widgetsArr) ? 'form-creation-widgets' : '',
      customClassForContainer,
    ]"
    v-if="!bulkMode"
  >
    <div
      :class="[!$validation.isEmpty(widgetsArr) ? 'form-left-container' : '']"
    >
      <div class="header mT20 mB10 d-flex">
        <div class="title mT10">{{ title }}</div>
        <el-select
          v-if="forms.length > 1"
          v-model="selectedForm"
          value-key="name"
          class="fc-input-full-border-select2 mL-auto width25"
        >
          <el-option
            v-for="(form, index) in forms"
            :key="index"
            :value="form"
            :label="form.displayName"
          ></el-option>
        </el-select>
      </div>

      <div class="inspection-form-creation">
        <div v-if="isLoading" class="loading-container d-flex">
          <Spinner :show="isLoading"></Spinner>
        </div>
        <template v-else>
          <f-webform
            :form.sync="formObj"
            :module="moduleName"
            :moduleDisplayName="moduleDisplayName"
            :isSaving="isSaving"
            :canShowPrimaryBtn="false"
            :canShowSecondaryBtn="false"
            :isEdit="isEdit"
            :isV3Api="isV3Api"
            :isWidgetsSupported="isWidgetsSupported"
            :moduleData="moduleData"
            :moduleDataId="moduleDataId"
            @onFormModelChange="onFormModelChange"
          ></f-webform>
          <div class="trigger-section-container mT20 white-background">
            <div class="pL70 pR70">
              <div class="section-heading pT50 text-uppercase">
                {{ $t('qanda.template.triggers') }}
                <div class="fc-heading-border-width43 mT15"></div>
              </div>
              <div class="pT20 pB20">
                <div
                  v-for="(trigger, index) in triggersList"
                  :class="['trigger-list', index === 0 && 'border-top']"
                  :key="index"
                >
                  <div class="d-flex align-center">
                    <div class="dot-green fL mR10"></div>
                    {{ trigger.name }}
                  </div>
                  <div class="trigger-actions">
                    <div
                      class="items-center d-flex mR20 cursor-pointer"
                      @click="openSpaceAssetLookup(index)"
                      v-if="
                        $getProperty(inspectionModel, 'creationType.id') === 2
                      "
                    >
                      <Spinner
                        :show="selectedTrigerIndexLoading == index"
                        size="30"
                      />
                      <inline-svg
                        src="location-icon"
                        class="vertical-middle"
                        iconClass="icon icon-sm location-icon mT3"
                      ></inline-svg>
                      <div
                        class="mL5"
                        v-if="$validation.isEmpty(trigger.assets)"
                      >
                        {{ $t('qanda.template.select_space_assets') }}
                      </div>
                      <div v-else class="mL5">
                        {{ getMultiAssetLabel(trigger.assets) }}
                      </div>
                    </div>
                    <div
                      class="d-flex items-center trigger-actions visibility-hide-actions mT3"
                    >
                      <i
                        class="el-icon-edit  edit-icon-grey pL5 pointer"
                        @click="editTrigger(trigger, index)"
                      ></i>
                      <div class="pL10 pointer" @click="deleteTrigger(index)">
                        <img
                          src="~assets/remove-icon.svg"
                          height="14"
                          width="14"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div
                v-if="$validation.isEmpty(triggersList)"
                class="d-flex items-center mR40 flex-col"
              >
                <inline-svg
                  src="svgs/emptystate/readings-empty"
                  iconClass="icon text-center icon-xxxlg mB10 mT10"
                ></inline-svg>
                <span class="empty-state-text">{{
                  $t('qanda.template.no_triggers')
                }}</span>
              </div>
              <div
                :class="[
                  'd-flex mB60',
                  $validation.isEmpty(triggersList) &&
                    'pT10 mR40 justify-center',
                ]"
              >
                <el-button @click="openTrigger" class="add-trigger-btn mT10">{{
                  $t('qanda.template.add_trigger')
                }}</el-button>
              </div>
            </div>
            <div class="flex-grow flex-shrink white-background"></div>
            <div class="inspection-btn-footer">
              <el-button
                class="modal-btn-cancel text-uppercase"
                @click="redirectToList()"
                >{{ $t('common._common.cancel') }}</el-button
              >
              <el-button
                class="modal-btn-save mL0"
                @click="save"
                :loading="saving"
              >
                {{
                  saving
                    ? $t('common._common._saving')
                    : $t('common._common._save')
                }}
              </el-button>
            </div>
          </div>
        </template>
      </div>
    </div>
    <el-dialog
      v-if="openTriggerDialog"
      :visible.sync="openTriggerDialog"
      :append-to-body="true"
      class="fc-dialog-center-container new-add-triger-dialog fc-dialog-header-hide"
    >
      <div class="positon-relative trigger-dialog-body">
        <trigger :trigger="triggerEdit" :customTriggerTypes="[1, 4]"></trigger>
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
    <portal-target class="widgets-container" name="side-bar-widgets">
    </portal-target>
    <FLookupFieldWizard
      v-if="showLookupFieldWizard"
      :canShowLookupWizard.sync="showLookupFieldWizard"
      :selectedLookupField="fieldObj"
      @setLookupFieldValue="setLookupFieldValue"
    ></FLookupFieldWizard>
  </div>
</template>
<script>
import { isEmpty, isObject, areValuesEmpty } from '@facilio/utils/validation'
import FormCreation from '@/base/FormCreation'
import PMMixin from '@/mixins/PMMixin'
import Trigger from '@/PMTrigger'
import { API } from '@facilio/api'
import FLookupFieldWizard from '@/FLookupFieldWizard'
import FetchViewsMixin from '@/base/FetchViewsMixin'
import SeasonTriggerMixin from '@/mixins/SeasonTriggerMixin'

import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  name: 'NewInspectionTemplate',
  extends: FormCreation,
  mixins: [PMMixin, FetchViewsMixin, SeasonTriggerMixin],
  data() {
    return {
      saving: false,
      inspectionModel: {},
      openTriggerDialog: false,
      triggerEdit: {},
      triggersList: [],
      currentTriggerIndex: null,
      loading: false,
      record: null,
      showLookupFieldWizard: false,
      fieldObj: {},
      selectedTriggerIndex: null,
      selectedTrigerIndexLoading: null,
    }
  },
  components: { Trigger, FLookupFieldWizard },
  created() {
    if (this.moduleDataId) this.deserialise()
  },
  computed: {
    moduleName() {
      return 'inspectionTemplate'
    },
    moduleDisplayName() {
      if (this.formObj && this.formObj.module) {
        return this.formObj.module.displayName
      }
      return ''
    },
    isV3Api() {
      return true
    },
    isWidgetsSupported() {
      return true
    },
    title() {
      let { moduleDisplayName, moduleDataId } = this
      let title = ``
      if (!isEmpty(moduleDataId)) {
        title = `Edit ${moduleDisplayName}`
      } else {
        title = `Create ${moduleDisplayName}`
      }
      return title
    },
    moduleDataId() {
      let { id } = this.$route.params
      return id ? parseInt(id) : null
    },
  },
  methods: {
    async deserialise() {
      this.loading = true
      let { moduleDataId, moduleName } = this

      let { [moduleName]: data, error } = await API.fetchRecord(moduleName, {
        id: moduleDataId,
      })
      if (error) {
        this.$message.error('Error Occured' || error.message)
      } else {
        let { triggers } = data || {}
        triggers.forEach(trigger => {
          let schedule = this.$getProperty(trigger, 'schedule.scheduleInfo')
          let startTime = this.$getProperty(trigger, 'schedule.startTime')
          let assets = this.$getProperty(trigger, 'resInclExclList')
          let { type } = trigger || {}
          if (type === 2) type = 4
          if (!isEmpty(assets)) {
            assets = assets.map(asset => {
              let { resource } = asset
              return { ...asset, label: resource.name, value: resource.id }
            })
          }
          this.triggersList.push({
            ...trigger,
            type,
            schedule,
            assets: assets ? assets : [],
            startTime,
          })
        })
        this.record = data
      }

      this.loading = false
    },
    async redirectToList() {
      let viewname = await this.fetchView(this.moduleName)
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(this.moduleName, pageTypes.LIST) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: viewname,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'inspectionTemplateList',
          params: { viewname: 'all' },
        })
      }
    },
    async redirectToSummary(id) {
      let viewname = await this.fetchView(this.moduleName)
      if (isWebTabsEnabled()) {
        let { name } =
          findRouteForModule(this.moduleName, pageTypes.OVERVIEW) || {}
        name &&
          this.$router.push({
            name,
            params: {
              viewname: viewname,
              id,
            },
            query: this.$route.query,
          })
      } else {
        this.$router.push({
          name: 'inspectionTemplateSummary',
          params: { id, viewname: 'all' },
        })
      }
    },
    onFormModelChange(model) {
      let { inspectionModel } = this
      this.inspectionModel = { ...inspectionModel, ...model }
    },
    openTrigger() {
      let type = 1
      let startDate = new Date()
      startDate.setHours(0, 0, 0, 0)
      let startTime = startDate.getTime()
      let name = `Trigger ${this.triggersList.length + 1}`
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
      this.currentTriggerIndex = null
      this.openTriggerDialog = true
    },
    cancelTriggerSave() {
      this.openTriggerDialog = false
    },
    async saveTrigger() {
      if (!(await this.canSaveTrigger())) {
        return
      }
      let { triggerEdit, currentTriggerIndex } = this
      let frequencyType = this.$getProperty(
        triggerEdit,
        'schedule.frequencyType',
        null
      )
      let basedOn = this.$getProperty(triggerEdit, 'basedOn', null)

      if (!isEmpty(frequencyType)) {
        frequencyType = this.$constants.FACILIO_FREQUENCY[frequencyType]
        if (frequencyType === 'Monthly') {
          if (basedOn === 'Date') {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 3)
          } else {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 4)
          }
        } else if (frequencyType === 'Quarterly') {
          if (basedOn === 'Date') {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 7)
          } else {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 8)
          }
        } else if (frequencyType === 'Half Yearly') {
          if (basedOn === 'Date') {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 9)
          } else {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 10)
          }
        } else if (frequencyType === 'Annually') {
          if (basedOn === 'Date') {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 5)
          } else {
            this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 6)
          }
        }
      }
      if (!isEmpty(currentTriggerIndex)) {
        this.triggersList[currentTriggerIndex] = triggerEdit
      } else {
        this.triggersList.push(triggerEdit)
      }
      this.currentTriggerIndex = null
      this.openTriggerDialog = false
    },
    deleteTrigger(index) {
      this.triggersList.splice(index, 1)
    },
    editTrigger(trigger, index) {
      this.triggerEdit = this.$helpers.cloneObject(trigger)
      if (this.$getProperty(trigger, 'type', null) === 1) {
        let frequencyType = this.$getProperty(
          trigger,
          'schedule.frequencyType',
          null
        )
        if (!(frequencyType % 2)) {
          this.$setProperty(this, 'triggerEdit.basedOn', 'Week')
        } else {
          this.$setProperty(this, 'triggerEdit.basedOn', 'Date')
        }

        if (frequencyType === 3 || frequencyType === 4) {
          this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 3)
        } else if (frequencyType === 7 || frequencyType === 8) {
          this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 4)
        } else if (frequencyType === 9 || frequencyType === 10) {
          this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 5)
        } else if (frequencyType === 5 || frequencyType === 6) {
          this.$setProperty(this, 'triggerEdit.schedule.frequencyType', 6)
        }
      }
      this.currentTriggerIndex = index
      this.openTriggerDialog = true
    },
    async save() {
      this.saving = true
      let {
        inspectionModel,
        triggersList,
        moduleName,
        moduleDataId,
        formObj,
      } = this
      let { id: formId } = formObj || {}
      let data = this.serializeValues(inspectionModel, triggersList, formObj)
      let serializedData = this.serializedData(formObj, inspectionModel)
      data = { ...data, ...serializedData }
      if (!isEmpty(formId)) {
        data.formId = formId
      }
      let promise
      if (!isEmpty(moduleDataId)) {
        promise = await API.updateRecord(moduleName, {
          id: moduleDataId,
          data,
        })
      } else {
        promise = await API.createRecord(moduleName, {
          data,
        })
      }
      let { [moduleName]: record, error } = promise
      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        let { id } = record
        if (isEmpty(moduleDataId)) this.createPage(id)
        this.redirectToSummary(id)
      }
      this.saving = false
    },
    async createPage(templateId) {
      let data = {
        name: 'Page 1',
        description: '',
        parent: templateId,
        position: 1,
      }
      await API.createRecord('qandaPage', {
        data,
      })
    },
    serializeValues(model, triggerList, formObj) {
      let { fields: formFieldObj = [] } = formObj
      let data = { ...model }

      Object.keys(data).forEach(field => {
        let isFileTypeField = formFieldObj.some(
          currField =>
            currField.displayTypeEnum === 'FILE' && currField.name === field
        )

        if (isFileTypeField) {
          delete data[field]
        }

        if (['creationType', 'assignmentType'].includes(field)) {
          let { id } = data[field] || {}
          data[field] = id
        }

        if (field === 'assignment') {
          let { assignedTo, assignmentGroup } = data['assignment'] || {}
          if (!isEmpty(assignedTo.id)) data['assignedTo'] = assignedTo
          if (!isEmpty(assignmentGroup.id))
            data['assignmentGroup'] = assignmentGroup

          delete data['assignment']
        }

        if (field === 'siteId' && !isEmpty(data[field])) {
          let { id } = data[field] || {}
          data[field] = id
        }

        if (isObject(data[field]) && areValuesEmpty(data[field])) {
          delete data[field]
        } else if (isEmpty(data[field])) {
          delete data[field]
        }
      })
      triggerList = triggerList.map(trigger => {
        let { name, type, schedule, assets, startTime } = trigger || {}
        if (type === 4) type = 2
        let param = {
          name,
          type,
          schedule: { scheduleInfo: schedule, startTime },
        }

        if (!isEmpty(assets)) {
          let resInclExclList = assets.map(asset => {
            let { value } = asset || {}
            return { resource: { id: value }, isInclude: true }
          })
          param['resInclExclList'] = resInclExclList
        }
        return param
      })

      data = {
        ...data,
        triggers: triggerList,
      }
      return data
    },
    setLookupFieldValue(value) {
      let selectedItems = this.$getProperty(value, 'field.selectedItems')
      let { selectedTriggerIndex, triggersList } = this
      let selectedTrigger = triggersList[selectedTriggerIndex]
      selectedTrigger['assets'] = selectedItems
      triggersList.splice(selectedTriggerIndex, 1, selectedTrigger)
      this.$set(this, 'triggersList', triggersList)
      this.selectedTriggerIndex = null
    },
    async openSpaceAssetLookup(index) {
      this.selectedTrigerIndexLoading = index
      let { inspectionModel } = this
      let {
        assignmentType,
        buildings,
        spaceCategory,
        assetCategory,
        sites,
        creationType,
      } = inspectionModel || {}
      if (creationType.id === 2) {
        let param = {
          assignmentTypeId: (assignmentType || {}).id,
          baseSpaceIds: buildings,
          spaceCategoryId: (spaceCategory || {}).id,
          assetCategoryId: (assetCategory || {}).id,
          siteIds: sites,
        }
        Object.keys(param).forEach(paramProperty => {
          if (isEmpty(param[paramProperty])) delete param[paramProperty]
        })
        let { data, error } = await API.post(
          'v3/resourceAllocation/getModuleAndCrtieriaFromConfig',
          param
        )
        if (error) {
          this.$message.error(error.message || 'Error Occured')
        } else {
          let { criteria, module } = data || {}
          let fieldObj = {
            isDataLoading: false,
            options: [],
            lookupModuleName: module,
            field: {
              lookupModule: {
                name: module,
                displayName: module,
              },
            },
            forceFetchAlways: true,
            filters: {},
            clientCriteria: criteria,
            isDisabled: false,
            multiple: true,
          }
          if (module === 'asset') {
            fieldObj.filters = {
              storeRoom: {
                operatorId: 1,
              },
            }
          }
          this.fieldObj = fieldObj
        }
      }
      this.selectedTrigerIndexLoading = null
      this.selectedTriggerIndex = index
      let { triggersList, fieldObj } = this
      let selectedTrigger = triggersList[index]
      let { assets } = selectedTrigger
      if (!isEmpty(assets)) this.$set(fieldObj, 'selectedItems', assets)
      else this.$set(fieldObj, 'selectedItems', [])
      this.showLookupFieldWizard = true
    },
    getMultiAssetLabel(assets) {
      assets = assets.map(asset => asset.label)

      if (assets.length > 2) {
        return `${assets.slice(0, 2).join(', ')}, +${Math.abs(
          assets.length - 2
        )}`
      } else {
        return `${assets.join(', ')}`
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.inspection-form-creation {
  overflow-y: scroll;
  height: calc(100vh - 150px);
}
.trigger-section-container {
  display: flex;
  flex-direction: column;
}
.section-heading {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 1.6px;
  color: #324056;
  text-transform: capitalize;
}
.inspection-btn-foote {
  bottom: 0px;
  display: flex;
}

.trigger-dialog-body {
  height: 100%;
  max-height: 600px;
  overflow: hidden;
}
.add-trigger-btn {
  cursor: pointer;
  background-color: #39b2c2;
  color: #fff;
  letter-spacing: 1.1px;
  text-align: center;
  text-transform: uppercase;
  font-weight: 500;
  font-size: 12px;
  border-radius: 0;
  height: 40px;
  cursor: pointer;
  &:hover {
    background-color: #3cbfd0;
    color: #fff;
  }
}
.trigger-list {
  padding: 20px 10px;
  height: 70px;
  border-bottom: solid 1px #f2f5f6;
  display: flex;
  justify-content: space-between;
  &:hover {
    background-color: #f9feff;
    .trigger-actions {
      visibility: visible;
    }
  }
}

.border-top {
  border-top: solid 1px #f2f5f6;
}

.empty-state-text {
  font-weight: 500;
  font-size: 18px;
  color: #324056;
  letter-spacing: 0.5px;
}

.trigger-actions {
  display: flex;
}
</style>
<style lang="scss">
.inspection-form-template {
  .height-100 {
    height: auto !important;
  }
  .f-webform-container {
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    height: 100%;
  }
}
.new-add-triger-dialog .el-dialog__body {
  padding: 0;
}
.new-add-triger-dialog .el-dialog {
  width: 70% !important;
}
</style>
