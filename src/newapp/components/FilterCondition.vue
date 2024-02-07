<template>
  <div class="filter-condition-container">
    <el-popover :value="true" :visibleArrow="false" :popperClass="popoverClass">
      <outside-click
        @onOutsideClick="close()"
        :visibility="true"
        class="filter-condition-block"
      >
        <div class="filter-condition-body">
          <div
            v-if="isTag"
            class="fc-dark-blue-txt13 f13 pointer mL10 pT10 pB10 fwBold"
            @click.stop="$emit('back')"
          >
            <i class="el-icon-back fw6 mR10 fc-dark-blue-txt13"></i>
            Back to Filter List
          </div>

          <div
            v-if="currentFilterProxy"
            class="filter-condition-radion-btn pT10"
          >
            <div
              class="filter-radio-display"
              v-if="
                !dateTimeFields.includes(fieldName) &&
                  !['DATE_TIME', 'DATE'].includes(currentFilterProxy.operator)
              "
            >
              <template v-for="condition in conditionList">
                <el-radio
                  v-if="allowedConditions(condition)"
                  :key="condition.operatorId"
                  v-model="operatorId"
                  :label="condition.operatorId"
                  class="fc-radio-btn pB10"
                >
                  {{ condition.operator }}
                  <div
                    v-if="
                      currentFilterProxy.displayType === 'select' &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <el-select
                      v-model="currentFilterValueProxy"
                      multiple
                      collapse-tags
                      filterable
                      placeholder="Select"
                      class="search-select-comp2 fc-tag mT10 search-filter-border-left fc-search-select-width select-border-left"
                    >
                      <el-option
                        v-for="(label, value) in currentFilterProxy.options"
                        :key="value"
                        :label="label"
                        :value="value"
                      ></el-option>
                    </el-select>
                  </div>

                  <div
                    v-if="
                      ['resourceType', 'lookup'].includes(
                        currentFilterProxy.displayType
                      ) &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <FLookupFieldWrapper
                      v-if="
                        $fieldUtils.isLookupDropDownField(
                          metaInfo.fields.find(v => v.name === fieldName)
                        )
                      "
                      class="search-input-comp search-select-comp2 fc-tag mT10 search-filter-border-left fc-search-select-width select-border-left"
                      v-model="currentFilterValueProxy"
                      :hideDropDown="hideDropDownForLookup"
                      :field="getFieldObj(currentFilterProxy, fieldName)"
                      :label="currentFilterProxy.selectedLabel"
                      @recordSelected="setLookUpFilter"
                      @showingLookupWizard="showingLookupWizard = true"
                    ></FLookupFieldWrapper>
                  </div>

                  <div
                    v-if="
                      currentFilterProxy.displayType === 'string' &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <el-input
                      class="width270px fc-input-full-border2 filter-condition-input"
                      v-model="currentFilterValueProxy"
                      type="text"
                      :autofocus="true"
                    ></el-input>
                  </div>

                  <div
                    v-if="
                      currentFilterProxy.displayType === 'number' &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <el-input
                      class="width270px fc-input-full-border2 filter-condition-input"
                      v-model="currentFilterValueProxy"
                      type="number"
                      :autofocus="true"
                    ></el-input>
                  </div>

                  <div
                    v-if="
                      currentFilterProxy.displayType === 'decimal' &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <el-input
                      class="width270px fc-input-full-border2 filter-condition-input"
                      v-model="currentFilterValueProxy"
                      type="number"
                      :autofocus="true"
                    ></el-input>
                  </div>

                  <div
                    v-if="
                      ['asset', 'space'].includes(
                        currentFilterProxy.displayType
                      ) &&
                        ![1, 2].includes(condition.operatorId) &&
                        condition.operatorId === operatorId
                    "
                  >
                    <div class="resource-list">
                      <el-select
                        class="multi resource-list el-input-textbox-full-border fc-tag mT1 fc-search-select-width mT10 search-filter-border-left"
                        filterable
                        multiple
                        collapse-tags
                        :value="
                          !$validation.isEmpty(currentFilterValueProxy)
                            ? [1]
                            : null
                        "
                        disabled
                      >
                        <el-option
                          :label="spaceAssetFilter"
                          :value="1"
                        ></el-option>
                      </el-select>
                      <i
                        @click="spaceAssetProps(currentFilterProxy)"
                        slot="suffix"
                        class="el-input__icon el-icon-search filter-spacce-condition-search"
                      ></i>
                    </div>

                    <space-asset-multi-chooser
                      v-if="chooserVisibility"
                      :showAsset="currentFilterProxy.displayType === 'asset'"
                      @associate="associateResource($event, currentFilterProxy)"
                      :initialValues="initialValues"
                      :visibility.sync="chooserVisibility"
                      :hideBanner="true"
                      class="fc-input-full-border-select2"
                    ></space-asset-multi-chooser>
                  </div>
                </el-radio>
              </template>
            </div>

            <div v-else>
              <el-radio
                v-model="currentFilterValueProxy"
                class="fc-radio-btn pB10"
                v-for="(label, value) in currentFilterProxy.options"
                :key="value"
                :label="value"
              >
                {{ label }}
              </el-radio>

              <div v-if="currentFilterProxy.type === 'date'">
                <el-radio
                  v-if="!currentFilterProxy.hideDateRange"
                  class="fc-radio-btn pB10"
                  v-model="currentFilterValueProxy"
                  label="20"
                  value="20"
                >
                  Custom
                </el-radio>

                <f-date-picker
                  v-if="currentFilterValueProxy === '20'"
                  class="position-relative fc-subheader-right-search"
                  v-model="currentFilterProxy.dateRange"
                  :type="'datetimerange'"
                  :value-format="'timestamp'"
                  :format="'dd-MM-yyyy HH:mm'"
                  @change="applyFilters"
                ></f-date-picker>
              </div>
            </div>
          </div>
        </div>
        <div class="search-select-filter-btn">
          <el-button
            class="btn-green-full filter-footer-btn fw-bold"
            @click="applyFilters()"
          >
            DONE
          </el-button>
        </div>
      </outside-click>
    </el-popover>
  </div>
</template>
<script>
import OutsideClick from '@/OutsideClick'
import { isEmpty } from '@facilio/utils/validation'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import SearchTagMixin from './SearchTagMixin'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'

export default {
  props: ['fieldName', 'isTag', 'popClass', 'hideDropDownForLookup'],
  components: {
    SpaceAssetMultiChooser,
    FDatePicker,
    OutsideClick,
    FLookupFieldWrapper,
  },
  mixins: [SearchTagMixin],

  data() {
    return {
      initialValues: null,
      chooserVisibility: false,
      excludeOperators: [74, 75, 76, 77, 78, 79, 35, 87, 52, 53],
      operatorId: null,
      operators: {
        subject: 'STRING',
        title: 'STRING',
        primaryContactName: 'STRING',
        description: 'STRING',
        name: 'STRING',
        primaryContactEmail: 'STRING',
        primaryContactPhone: 'STRING',
        assetCategoryId: 'LOOKUP',
        kpiCategory: 'LOOKUP',
        assetDepartment: 'LOOKUP',
        siteId: 'LOOKUP',
        assetType: 'LOOKUP',
        category: 'LOOKUP',
        dueDate: 'DATE',
        actualWorkEnd: 'DATE',
        space: 'LOOKUP',
        visitedSpace: 'LOOKUP',
        asset: 'LOOKUP',
        resource: 'LOOKUP',
        sourceType: 'NUMBER',
        ticketType: 'LOOKUP',
        typeId: 'LOOKUP',
        status: 'LOOKUP',
        moduleState: 'LOOKUP',
        assignedTo: 'LOOKUP',
        registeredBy: 'LOOKUP',
        executedBy: 'LOOKUP',
        frequency: 'LOOKUP',
        createdTime: 'LOOKUP',
        tasks: 'LOOKUP',
        priority: 'LOOKUP',
        alarmType: 'NUMBER',
        acknowledgedBy: 'LOOKUP',
        severity: 'LOOKUP',
        previousSeverity: 'LOOKUP',
        readingAlarmCategory: 'LOOKUP',
        readingrule: 'LOOKUP',
        tenant: 'LOOKUP',
      },
      popoverClass: 'right-0 p0',
      showingLookupWizard: false,
    }
  },

  computed: {
    currentFilterProxy: {
      get() {
        let { fieldName, filterObjectsData } = this
        return filterObjectsData[fieldName] || {}
      },
      set(value) {
        let { filterObjectsData, fieldName } = this
        this.$set(filterObjectsData, fieldName, value)
      },
    },

    currentFilterValueProxy: {
      get() {
        let { currentFilterProxy } = this
        return currentFilterProxy.value || []
      },
      set(value) {
        this.currentFilterProxy = { ...this.currentFilterProxy, value }
      },
    },

    spaceAssetFilter() {
      let { currentFilterProxy } = this
      let filter = ''

      if (!isEmpty(currentFilterProxy.value)) {
        filter = currentFilterProxy.value.length

        if (currentFilterProxy.displayType === 'asset') filter += 'Asset'
        else filter += 'Space'

        if (currentFilterProxy.value.length > 1) filter += 's'
      }

      return filter
    },

    conditionList() {
      let type = this.$getProperty(this.operators, this.fieldName, null)
      let { operators = {} } = this.metaInfo || {}

      if (!isEmpty(type)) {
        return operators[type]
      } else {
        type = this.currentFilterProxy
        return operators[type.operator]
      }
    },
  },

  watch: {
    fieldName: {
      handler() {
        let { currentFilterProxy } = this

        this.operatorId = this.$getProperty(
          currentFilterProxy,
          'operatorId',
          null
        )
      },
      immediate: true,
    },
    popClass: {
      handler(newValue) {
        if (newValue) {
          this.popoverClass = newValue
          this.popoverClass += ' p0'
        }
      },
      immediate: true,
    },
  },

  methods: {
    allowedConditions(label) {
      if (
        (label._name === 'BUILDING_IS' && !(this.fieldName === 'space')) ||
        label._name === 'LOOKUP' ||
        this.excludeOperators.includes(label.operatorId)
      ) {
        return false
      } else {
        return true
      }
    },

    associateResource(resource, field) {
      this.chooserVisibility = false
      field.value = resource.resourceList
        ? resource.resourceList.map(res => res.id)
        : []

      this.applyFilters()
    },

    applyFilters() {
      this.currentFilterProxy = {
        ...this.currentFilterProxy,
        operatorId: this.operatorId,
      }

      this.applyFilter()
      this.close()
    },

    close() {
      if (!this.chooserVisibility && !this.showingLookupWizard)
        this.$emit('close')
    },
  },
}
</script>
<style lang="scss">
.filter-condition-container {
  .search-filter-border-left .el-input__inner {
    border-left: 1px solid #d8dce5 !important;
  }
  .resource-list .multi .el-select .el-tag__close.el-icon-close,
  .resource-list .multi .el-input__suffix {
    display: none;
  }
}
</style>
<style scoped lang="scss">
.fc-subheader-right-search .el-date-editor--datetimerange.el-input__inner {
  border-left: 1px solid #dcdfe6;
  margin-top: 10px !important;
}
</style>
