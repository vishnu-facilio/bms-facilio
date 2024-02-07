<template>
  <div>
    <div class="tag-inline" v-if="filters && Object.keys(filters).length > '0'">
      <div v-for="(label, value) in filters" :key="value" class="tag-inline">
        <!-- RESOURCES -->
        <el-tag
          v-if="isBuildingOperator(label)"
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value)"
          class="mR10 filter-search-tag"
          >{{ tagHandling(label, value) }}</el-tag
        >

        <!-- STRING && SINGLE SELECT -->
        <el-tag
          v-else-if="
            $validation.isEmpty(label.value) &&
              !$validation.isEmpty(fieldLabelMap[value]) &&
              !$validation.isEmpty(operatorMap[label.operatorId]) &&
              checkForDateTimeField(value, label) === null
          "
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value)"
          class="mR10 filter-search-tag"
          >{{ fieldLabelMap[value] }} {{ operatorMap[label.operatorId] }}
        </el-tag>

        <el-tag
          v-else-if="
            label.value &&
              (label.value.length === 0 || label.value.length === 1) &&
              checkForDateTimeField(value, label) === null
          "
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value)"
          class="mR10 filter-search-tag"
          >{{ fieldLabelMap[value] }} {{ operatorMap[label.operatorId] }}
          {{ getValue(value, label, null) }}</el-tag
        >
        <!-- MULTI SELECT -->
        <el-tag
          v-else-if="
            label.value &&
              label.value.length > 1 &&
              checkForDateTimeField(value, label) === null
          "
          v-for="(spli, index) in label.value"
          :key="index"
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value, spli)"
          class="mR10 filter-search-tag"
          >{{ fieldLabelMap[value] }} {{ operatorMap[label.operatorId] }}
          {{ getValue(value, label, Number(spli)) }}</el-tag
        >
        <!-- DATE/DATETIME -->
        <el-tag
          v-else-if="checkForDateTimeField(value, label) !== null"
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value)"
          class="mR10 filter-search-tag"
          >{{ checkForDateTimeField(value, label) }}</el-tag
        >

        <!-- RESOURCES temp-->
        <el-tag
          v-else
          :closable="showCloseIcon"
          :disable-transitions="false"
          @close="handleClose(value)"
          class="mR10 filter-search-tag"
          >{{ tagHandling(label, value) }}</el-tag
        >
      </div>
    </div>
    <div class="tag-list-container" v-if="showFilterAdd">
      <div
        class="f13 fc-dark-blue-txt13 pointer mL10 pT10 fwBold"
        @click.stop="showFilterList = true"
      >
        <i class="el-icon-plus fc-dark-blue-txt13 f13 fwBold"></i>
        {{ $t('common._common.add_filter') }}
      </div>
      <transition name="fade">
        <template v-if="showFilterList">
          <outside-click
            :visibility="true"
            @onOutsideClick="close()"
            class="tag-list-block z-index1234"
          >
            <div class="tag-list-header">
              <el-input
                type="text"
                v-model="search"
                placeholder="Search..."
                class="tag-list-search"
                :autofocus="true"
              />
              <div class="tag-list-body">
                <div
                  v-for="(obj, value) in config.data"
                  :key="value"
                  :value="value"
                  v-if="
                    search === '' ||
                      obj.label.toLowerCase().startsWith(search.toLowerCase())
                  "
                >
                  <div
                    class="label-txt-black2 pT10 pB10 pL20 pR20 pointer fc-list-active-hover"
                    @click.stop="showFilter(value)"
                  >
                    {{ config.data[value].label }}
                  </div>
                </div>
              </div>
            </div>
          </outside-click>
        </template>
      </transition>
      <div class="tag-list-filter-condition-container">
        <transition name="fade">
          <template v-if="showTagFilterCondition">
            <outside-click
              :visibility="true"
              @onOutsideClick="handleConditionClose"
              class="filter-condition-block"
            >
              <div class="filter-condition-body">
                <div
                  class="fc-dark-blue-txt13 f13 pointer mL10 pT10 fwBold"
                  @click.stop="back"
                >
                  <i class="el-icon-back fw6 mR10 fc-dark-blue-txt13"></i>
                  Back to {{ fieldLabelMap[fieldName] }}
                </div>
                <div class="filter-condition-radion-btn pT20">
                  <div class="filter-radio-display">
                    <div
                      v-if="
                        fieldName !== 'createdTime' &&
                          fieldName !== 'actualWorkEnd' &&
                          fieldName !== 'lastPurchasedDate' &&
                          fieldName !== 'lastIssuedDate' &&
                          fieldName !== 'acknowledgedTime' &&
                          fieldName !== 'dueDate' &&
                          fieldName !== 'responseDueDate' &&
                          fieldName !== 'warrantyExpiryDate' &&
                          fieldName !== 'clearedTime' &&
                          fieldName !== 'modifiedTime' &&
                          fieldName !== 'isAcknowledged' &&
                          fieldName !== 'connected'
                      "
                    >
                      <div
                        v-if="
                          config.data[fieldName] &&
                            config.data[fieldName].operator
                        "
                      >
                        <el-radio
                          v-model="config.data[fieldName].operatorId"
                          class="fc-radio-btn pB10"
                          v-for="label in moduleMeta.operators[
                            config.data[fieldName].operator
                          ]"
                          :key="label.operatorId"
                          :label="label.operatorId"
                          :value="label.operatorId"
                          v-if="
                            selectedlist(label, config.data[fieldName].operator)
                          "
                        >
                          {{ label.operator }}
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                config.data[fieldName] &&
                                  config.data[fieldName].displayType ===
                                    'select' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <el-select
                                filterable
                                v-model="config.data[fieldName].value"
                                collapse-tags
                                multiple
                                placeholder="Select"
                                class="fc-input-full-border2 search-select-comp2 mT10 fc-tag pR10 fc-search-select-width"
                              >
                                <el-option
                                  v-for="(label, value) in config.data[
                                    fieldName
                                  ].options"
                                  :key="value"
                                  :label="label"
                                  :value="value"
                                ></el-option>
                              </el-select>
                            </div>
                            <div
                              v-if="
                                config.data[fieldName].displayType ===
                                  'lookup' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2 &&
                                  !config.data[fieldName].specialType
                              "
                            >
                              <FLookupFieldWrapper
                                class="search-input-comp"
                                v-model="(formModel[fieldName] || {}).id"
                                :field="
                                  moduleMeta.fields.find(
                                    v => v.name === fieldName
                                  )
                                "
                                @recordSelected="
                                  value => setLookUpFilter(value, fieldName)
                                "
                                @showingLookupWizard="
                                  showingLookupWizard = true
                                "
                              ></FLookupFieldWrapper>
                            </div>
                          </div>
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                fieldName &&
                                  config.data[fieldName] &&
                                  (config.data[fieldName].displayType ===
                                    'string' ||
                                    config.data[fieldName].displayType ===
                                      'number') &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <el-input
                                class="width270px fc-input-full-border2 filter-condition-input"
                                v-model="config.data[fieldName].value"
                                type="text"
                                :autofocus="true"
                              ></el-input>
                            </div>
                          </div>
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                fieldName &&
                                  ['asset', 'space'].includes(
                                    config.data[fieldName].displayType
                                  ) &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <div class="resource-list">
                                <el-select
                                  filterable
                                  class="multi resource-list el-input-textbox-full-border fc-tag"
                                  multiple
                                  collapse
                                  :value="
                                    config.data[fieldName].value &&
                                    config.data[fieldName].value.length
                                      ? [1]
                                      : null
                                  "
                                  disabled
                                  style="width: 100%"
                                >
                                  <el-option
                                    :label="
                                      config.data[fieldName].value &&
                                      config.data[fieldName].value.length
                                        ? config.data[fieldName].value.length +
                                          (config.data[fieldName]
                                            .displayType === 'asset'
                                            ? ' ' + $t('common._common.asset')
                                            : ' ' +
                                              $t(
                                                'common.space_asset_chooser.space'
                                              )) +
                                          (config.data[fieldName].value.length >
                                          1
                                            ? 's'
                                            : '') +
                                          ' ' +
                                          $t('common._common.selected')
                                        : ''
                                    "
                                    :value="1"
                                  ></el-option>
                                </el-select>
                                <i
                                  @click="
                                    $set(
                                      config.data[fieldName],
                                      'chooserVisibility',
                                      true
                                    )
                                  "
                                  slot="suffix"
                                  class="el-input__icon el-icon-search filter-icon-search"
                                ></i>
                              </div>
                            </div>
                          </div>
                        </el-radio>
                      </div>
                      <div v-else>
                        <el-radio
                          v-model="config.data[fieldName].operatorId"
                          class="fc-radio-btn pB10"
                          v-for="label in moduleMeta.operators[
                            operator[fieldName]
                          ]"
                          :key="label.operatorId"
                          :label="label.operatorId"
                          :value="label.operatorId"
                          v-if="selectedlist(label, operator[fieldName])"
                        >
                          {{ label.operator }}
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                config.data[fieldName] &&
                                  config.data[fieldName].displayType ===
                                    'select' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <el-select
                                filterable
                                v-model="config.data[fieldName].value"
                                collapse-tags
                                multiple
                                placeholder="Select"
                                class="fc-input-full-border2 search-select-comp2 mT10 fc-tag pR10 fc-search-select-width"
                              >
                                <el-option
                                  v-for="(label, value) in config.data[
                                    fieldName
                                  ].options"
                                  :key="value"
                                  :label="label"
                                  :value="value"
                                ></el-option>
                              </el-select>
                            </div>
                            <div
                              v-if="
                                config.data[fieldName].displayType ===
                                  'lookup' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2 &&
                                  !config.data[fieldName].specialType
                              "
                            >
                              <FLookupFieldWrapper
                                class="search-input-comp"
                                v-model="(formModel[fieldName] || {}).id"
                                :field="
                                  moduleMeta.fields.find(
                                    v => v.name === fieldName
                                  )
                                "
                                @recordSelected="
                                  value => setLookUpFilter(value, fieldName)
                                "
                                @showingLookupWizard="
                                  showingLookupWizard = true
                                "
                              ></FLookupFieldWrapper>
                            </div>
                          </div>
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                fieldName &&
                                  config.data[fieldName] &&
                                  config.data[fieldName].displayType ===
                                    'string' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <el-input
                                class="width270px fc-input-full-border2 filter-condition-input"
                                v-model="config.data[fieldName].value"
                                type="text"
                                :autofocus="true"
                              ></el-input>
                            </div>
                          </div>

                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                fieldName &&
                                  config.data[fieldName] &&
                                  config.data[fieldName].displayType ===
                                    'number' &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <el-input
                                class="width270px fc-input-full-border2 filter-condition-input"
                                v-model="config.data[fieldName].value"
                                type="number"
                                :autofocus="true"
                              ></el-input>
                            </div>
                          </div>
                          <div
                            v-if="
                              label.operatorId ==
                                config.data[fieldName].operatorId
                            "
                          >
                            <div
                              v-if="
                                fieldName &&
                                  ['asset', 'space'].includes(
                                    config.data[fieldName].displayType
                                  ) &&
                                  label.operatorId !== 1 &&
                                  label.operatorId !== 2
                              "
                            >
                              <div class="resource-list">
                                <el-select
                                  filterable
                                  class="multi resource-list el-input-textbox-full-border fc-tag fc-search-select-width mT10"
                                  multiple
                                  collapse
                                  :value="
                                    config.data[fieldName].value &&
                                    config.data[fieldName].value.length
                                      ? [1]
                                      : null
                                  "
                                  disabled
                                >
                                  <el-option
                                    :label="
                                      config.data[fieldName].value &&
                                      config.data[fieldName].value.length
                                        ? config.data[fieldName].value.length +
                                          (config.data[fieldName]
                                            .displayType === 'asset'
                                            ? ' Asset'
                                            : ' Space') +
                                          (config.data[fieldName].value.length >
                                          1
                                            ? 's'
                                            : '') +
                                          ' selected'
                                        : ''
                                    "
                                    :value="1"
                                  ></el-option>
                                </el-select>
                                <i
                                  @click="
                                    $set(
                                      config.data[fieldName],
                                      'chooserVisibility',
                                      true
                                    )
                                  "
                                  slot="suffix"
                                  class="el-input__icon el-icon-search fc-tag-filter-icon-search"
                                ></i>
                              </div>
                            </div>
                          </div>
                        </el-radio>
                      </div>
                    </div>
                    <div v-else>
                      <el-radio
                        v-model="config.data[fieldName].value"
                        class="fc-radio-btn pB10"
                        v-for="(label4, value4) in config.data[fieldName]
                          .options"
                        :key="value4"
                        :label="value4"
                        >{{ label4 }}</el-radio
                      >
                      <el-radio
                        v-model="config.data[fieldName].value"
                        v-if="
                          config.data[fieldName].type === 'date' &&
                            !config.data[fieldName].hideDateRange
                        "
                        label="20"
                        value="20"
                        >Custom</el-radio
                      >
                      <f-date-picker
                        class="mT10 position-relative"
                        v-if="
                          config.data[fieldName].type === 'date' &&
                            (config.data[fieldName].single
                              ? config.data[fieldName].value === '20'
                              : config.data[fieldName].value.includes('20'))
                        "
                        v-model="config.data[fieldName].dateRange"
                        :type="'datetimerange'"
                        :value-format="'timestamp'"
                        :format="'dd-MM-yyyy HH:mm'"
                        @change="onFilterChanged"
                      ></f-date-picker>
                    </div>
                  </div>
                </div>
              </div>
              <div class="filter-condition-footer">
                <el-button
                  class="btn-green-full filter-footer-btn fw-bold"
                  @click="apply()"
                  >DONE</el-button
                >
              </div>
            </outside-click>
          </template>
        </transition>
      </div>
    </div>
    <space-asset-multi-chooser
      v-if="config.data[fieldName] && config.data[fieldName].chooserVisibility"
      :showAsset="config.data[fieldName].displayType === 'asset'"
      @associate="associateResource($event, config.data[fieldName])"
      :initialValues="{
        isIncludeResource: true,
        selectedResources: config.data[fieldName].value.map(id => ({
          id: parseInt(id),
        })),
      }"
      :visibility.sync="config.data[fieldName].chooserVisibility"
      :hideBanner="true"
      class="fc-input-full-border-select2"
    ></space-asset-multi-chooser>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import SpaceAssetMultiChooser from '@/SpaceAssetMultiChooser'
import FLookupFieldWrapper from '@/FLookupFieldWrapper'
import OutsideClick from '@/OutsideClick'
import FDatePicker from 'pages/assets/overview/FDatePicker'
import { isEmpty, isArray } from '@facilio/utils/validation'
import { getFieldOptions } from 'util/picklist'

const spaceFields = ['space', 'visitedSpace']
export default {
  props: [
    'filters',
    'config',
    'showFilterAdd',
    'showCloseIcon',
    'isAssetsCategory',
    'specialOperatorHandling',
  ],
  data() {
    return {
      inputVisible: false,
      states: [],
      formModel: {},
      excludeOperators: [74, 75, 76, 77, 78, 79, 35, 87, 52, 53],
      siteMap: {},
      done: false,
      search: '',
      fieldName: '',
      itemTypes: null,
      filtersApplied: {},
      inputValue: '',
      searchLists: {
        subject: 'Subject',
        assetCategoryId: 'Category',
        title: 'Title',
        name: 'Name',
        category: 'Category',
        categoryId: 'Category',
        tenant: 'Tenant',
        tenantId: 'Tenant',
        assetCategory: 'Category',
        assetDepartment: 'Department',
        siteId: 'Site',
        assetType: 'Type',
        warrantyExpiryDate: 'Warranty Expiry Date',
        resource: 'Resource',
        dueDate: 'Due Date',
        responseDueDate: 'Response Due Date',
        space: 'Space',
        visitedSpace: 'Visited Space',
        asset: 'Asset',
        sourceType: 'Source Type',
        ticketType: 'Type',
        typeId: 'Type',
        type: 'Type',
        status: 'Status',
        assignedTo: 'Assigned To',
        assignedToId: 'Assigned To',
        priorityId: 'Priority',
        frequency: 'Frequency',
        createdTime: 'Created Time',
        actualWorkEnd: 'Resolved Time',
        comments: 'Comments',
        tasks: 'Tasks',
        priority: 'Priority',
        modifiedTime: 'Modified Time',
        actualWorkStart: 'Actual Work Start',
        acknowledgedBy: 'Acknowledged By',
        acknowledgedTime: 'Acknowledged Time',
        alarmType: 'Type',
        clearedTime: 'Cleared Time',
        previousSeverity: 'Previous Severity',
        severity: 'Severity',
        itemType: 'Item Type',
        toolType: 'Tool Type',
        storeRoom: 'Store Room',
        issuedTo: 'Issued To',
        alarmSeverityId: 'Severity',
        lastPurchasedDate: 'Last Purchased Date',
        workorder: 'Work Order',
        lastIssuedDate: 'Last Issued Date',
        isAcknowledged: 'Is Acknowledged',
      },
      operator: {
        subject: 'STRING',
        title: 'STRING',
        primaryContactName: 'STRING',
        qrVal: 'STRING',
        id: 'STRING',
        name: 'STRING',
        description: 'STRING',
        primaryContactEmail: 'STRING',
        primaryContactPhone: 'STRING',
        assetCategory: 'LOOKUP',
        kpiCategory: 'LOOKUP',
        assetDepartment: 'LOOKUP',
        siteId: 'LOOKUP',
        assetType: 'LOOKUP',
        department: 'LOOKUP',
        type: 'LOOKUP',
        typeId: 'LOOKUP',
        category: 'LOOKUP',
        categoryId: 'LOOKUP',
        tenant: 'LOOKUP',
        vendor: 'LOOKUP',
        tenantId: 'LOOKUP',
        moduleState: 'LOOKUP',
        assetCategoryId: 'LOOKUP',
        alarmSeverityId: 'LOOKUP',
        dueDate: 'DATE',
        responseDueDate: 'DATE',
        space: 'LOOKUP',
        visitedSpace: 'LOOKUP',
        resourceId: 'LOOKUP',
        asset: 'LOOKUP',
        sourceType: 'NUMBER',
        ticketType: 'LOOKUP',
        status: 'LOOKUP',
        assignedTo: 'LOOKUP',
        assignedToId: 'LOOKUP',
        frequency: 'NUMBER',
        assignmentGroup: 'LOOKUP',
        tasks: 'LOOKUP',
        priority: 'LOOKUP',
        priorityId: 'LOOKUP',
        alarmType: 'NUMBER',
        acknowledgedBy: 'LOOKUP',
        severity: 'LOOKUP',
        itemType: 'LOOKUP',
        toolType: 'LOOKUP',
        storeRoom: 'LOOKUP',
        issuedTo: 'LOOKUP',
        lastPurchasedDate: 'DATE',
        lastIssuedDate: 'DATE',
        previousSeverity: 'LOOKUP',
      },
      frequency: {
        1: 'Daily',
        2: 'Weekly',
        3: 'Monthly',
        4: 'Quarterly',
        5: 'Half Yearly',
        6: 'Annually',
      },
      isAcknowledged: {
        true: 'Yes',
        false: 'No',
      },
      showFilterList: false,
      showTagFilterCondition: false,
      showingLookupWizard: false,
    }
  },
  created() {
    Promise.all([
      this.$store.dispatch('loadSite'),
      this.$store.dispatch('loadTicketCategory'),
      this.$store.dispatch('loadTicketType'),
      this.$store.dispatch('loadTicketPriority'),
      this.$store.dispatch('loadAssetCategory'),
      this.$store.dispatch('loadAssetDepartment'),
      this.$store.dispatch('loadAssetType'),
      this.$store.dispatch('loadAlarmSeverity'),
      this.$store.dispatch('loadSites'),
      this.$store.dispatch('loadGroups'),
      this.getStateList(),
    ]).then(() => {
      this.constructStatusField()
      this.init()
      this.setOptions()
    })
  },
  computed: {
    ...mapState({
      users: state => state.users,
      groups: state => state.groups,
      ticketpriority: state => state.ticketPriority,
      ticketcategory: state => state.ticketCategory,
      alarmseverity: state => state.alarmSeverity,
      assetcategory: state => state.assetCategory,
      tenant: state => state.tenant.tenants,
      siteList: state => state.site,
      sitedetails: state => state.sites,
      assetdepartment: state => state.assetDepartment,
      assettype: state => state.assetType,
      moduleMeta: state => state.view.metaInfo,
      viewDetail: state => state.view.currentViewDetail,
    }),
    ...mapGetters(['getTicketTypePickList']),
    tickettype() {
      return this.getTicketTypePickList()
    },
    appliedFilters() {
      if (this.$route.query.search) {
        return JSON.parse(this.$route.query.search)
      }
      return null
    },
    operatorMap() {
      let opMap = {}
      if (this.moduleMeta && this.moduleMeta.operators) {
        for (let key in this.moduleMeta.operators) {
          for (let k in this.moduleMeta.operators[key]) {
            opMap[
              this.moduleMeta.operators[key][k].operatorId
            ] = this.moduleMeta.operators[key][k].operator
          }
        }
      }
      return opMap
    },
    fieldLabelMap() {
      let fieldmap = {}
      if (this.moduleMeta && this.moduleMeta.fields) {
        this.moduleMeta.fields.forEach(d => {
          fieldmap[d.name] = d.displayName
        })
      }
      if (this.config.data) {
        for (let k in this.config.data) {
          if (this.config.data[k].key) {
            fieldmap[this.config.data[k].key] = this.config.data[k].label
          } else {
            fieldmap[k] = this.config.data[k].label
          }
        }
      }
      return fieldmap
    },
  },
  components: {
    SpaceAssetMultiChooser,
    OutsideClick,
    FLookupFieldWrapper,
    FDatePicker,
  },
  watch: {
    appliedFilters(val) {
      if (val) {
        this.applyfilters()
      }
      this.constructFormmodelForLookupFields()
    },
  },
  mounted() {
    this.applyfilters()
    this.setOptions()
    this.siteDetail()
    this.constructFormmodelForLookupFields()
  },
  methods: {
    setLookUpFilter(filter, fieldName) {
      if (!isEmpty(filter)) {
        let value = {
          id: filter.value,
          label: filter.label,
        }
        this.$set(this.formModel, fieldName, value)
      }
      this.showingLookupWizard = false
    },
    getStateList() {
      if (this.config.moduleName) {
        return this.$http
          .get(`v2/state/list?parentModuleName=${this.config.moduleName}`)
          .then(({ data }) => {
            this.states = data.result.status || []
          })
          .catch(({ message = 'Error loading states' }) => {
            this.$message.error(message)
          })
      }
    },
    constructStatusField() {
      if (
        !isEmpty(this.moduleMeta) &&
        !isEmpty(this.moduleMeta.module) &&
        isEmpty(this.config.data.moduleState) &&
        !isEmpty(this.states)
      ) {
        let stateOptions = {}
        if (!isEmpty(this.states)) {
          this.states.forEach(state => {
            stateOptions[state.id] = state.displayName
          })
        }
        if (
          this.moduleMeta.module.stateFlowEnabled ||
          !this.moduleMeta.module.custom
        ) {
          let stateObj = {
            label: 'Status',
            displayType: 'select',
            options: stateOptions,
            value: [],
            key: 'moduleState',
          }

          this.$set(this.config.data, 'moduleState', stateObj)
        }
      }
    },
    constructFormmodelForLookupFields() {
      let { appliedFilters } = this
      for (let key in this.config.data) {
        if (
          this.config.data[key] &&
          this.config.data[key].isCustomFieldLookup
        ) {
          this.$set(this.operator, key, 'LOOKUP')
          let lookupObj = {
            id: '',
            label: '',
          }
          if (
            !isEmpty(this.config.data[key].value) &&
            this.config.data[key].value.length > 0
          ) {
            lookupObj.id = parseInt(this.config.data[key].value[0])
            lookupObj.label = !isEmpty(this.config.data[key].options)
              ? this.config.data[key].options[
                  parseInt(this.config.data[key].value[0])
                ]
              : ''
          }
          this.$set(this.formModel, key, lookupObj)
        }
      }
      if (!isEmpty(appliedFilters) && !isEmpty(this.formModel)) {
        let filters = Object.keys(appliedFilters || {})
        let formModelKey = Object.keys(this.formModel || {})
        if (!isEmpty(filters) && !isEmpty(formModelKey)) {
          formModelKey.forEach(fval => {
            if (!filters.includes(fval)) {
              this.formModel[fval].id = ''
              this.formModel[fval].label = ''
            } else if (filters.includes(fval)) {
              this.formModel[fval].id =
                !isEmpty(appliedFilters[fval][0]) &&
                !isEmpty(appliedFilters[fval][0].value)
                  ? parseInt(appliedFilters[fval][0].value[0])
                  : ''
              this.formModel[fval].label =
                !isEmpty(appliedFilters[fval][0]) &&
                !isEmpty(appliedFilters[fval][0].selectedLabel)
                  ? appliedFilters[fval][0].selectedLabel
                  : ''
            }
          })
        }
      }
    },
    tagHandling(resObj, value) {
      let lookupField = {}
      lookupField =
        this.moduleMeta && this.moduleMeta.fields
          ? this.moduleMeta.fields.find(at => at.name === value)
          : {}
      if (!Array.isArray(resObj)) {
        resObj = [resObj]
      }
      if (value === 'resource' || value === 'resourceId') {
        if (resObj && resObj.length !== 2) {
          return (
            resObj[0].value.length +
            (resObj[0].value && resObj[0].value.length > 1
              ? ' Spaces/Assets Selected'
              : ' Space/Asset Selected')
          )
        } else if (resObj && resObj.length === 2) {
          return resObj[0].value.length + ' Spaces/Assets Selected'
        }
      } else if (spaceFields.includes(value)) {
        return (
          resObj[0].value.length +
          (resObj[0].value.length > 1 ? ' Spaces selected' : ' Space selected')
        )
      } else if (
        resObj &&
        resObj[0] &&
        resObj[0].selectedLabel &&
        lookupField &&
        lookupField.dataTypeEnum._name === 'LOOKUP'
      ) {
        return lookupField.displayName + ' is ' + resObj[0].selectedLabel
      }
      return ''
    },
    fetchDetailForLookUpTag(resObj, value) {
      let filters = {
        [value]: resObj,
      }
      let queryObj = {
        viewname: all,
        page: 1,
        filters: filters ? filters : '',
        moduleName: this.$route.params.moduleName,
      }
      this.$store
        .dispatch('customModule/fetchCustomModuleList', queryObj)
        .then(() => {})
        .catch(error => {})
    },
    checkForDateTimeField(field, fieldObj) {
      if (this.moduleMeta && this.moduleMeta.fields) {
        let a = {}
        a = this.moduleMeta.fields.find(at => at.name === field)
        if (a) {
          if (
            a.dataTypeEnum._name === 'DATE' ||
            a.dataTypeEnum._name === 'DATETIME' ||
            a.dataTypeEnum._name === 'DATE_TIME'
          ) {
            if (fieldObj.value && fieldObj.value.length === 1) {
              return (
                this.fieldLabelMap[field] +
                ' is ' +
                this.operatorMap[fieldObj.operatorId].replace(
                  ' N ',
                  ' ' + fieldObj.value[0] + ' '
                )
              )
            } else if (fieldObj.value && fieldObj.value.length > 1) {
              return (
                this.fieldLabelMap[field] +
                ' is ' +
                this.$options.filters.formatDate(
                  Number(fieldObj.value[0]),
                  true,
                  false
                ) +
                ' - ' +
                this.$options.filters.formatDate(
                  Number(fieldObj.value[1]),
                  true,
                  false
                )
              )
            } else {
              if (isArray(fieldObj)) {
                return (
                  this.fieldLabelMap[field] +
                  ' is ' +
                  this.operatorMap[fieldObj[0].operatorId]
                )
              } else {
                return (
                  this.fieldLabelMap[field] +
                  ' is ' +
                  this.operatorMap[fieldObj.operatorId]
                )
              }
            }
          } else {
            return null
          }
        } else {
          return null
        }
      }
    },
    getValue(field, fieldObj, fieldId) {
      if (this.moduleMeta) {
        let a = {}
        a = this.moduleMeta.fields.find(at => at.name === field)
        if (field === 'id') {
          return parseInt(fieldObj.value[0])
        } else if (
          (field === 'type' || field === 'typeId') &&
          (this.moduleMeta.name === 'workorder' ||
            this.moduleMeta.name === 'preventivemaintenance')
        ) {
          field = 'ticketType'
        } else if (
          (field === 'category' && this.isAssetsCategory) ||
          field === 'assetCategoryId'
        ) {
          field = 'assetCategory'
        } else if (
          field === 'categoryId' &&
          this.moduleMeta.name === 'preventivemaintenance'
        ) {
          field = 'category'
        } else if (
          field === 'assignedToId' &&
          this.moduleMeta.name === 'preventivemaintenance'
        ) {
          field = 'assignedTo'
        } else if (
          field === 'priorityId' &&
          this.moduleMeta.name === 'preventivemaintenance'
        ) {
          field = 'priority'
        } else if (field === 'department') {
          field = 'assetDepartment'
        } else if (field === 'type' && this.isAssetsCategory) {
          field = 'assetType'
        } else if (field === 'alarmSeverityId') {
          field = 'severity'
        }
        if (this.config.data[field]) {
          if (fieldId && fieldObj.value.length === 0) {
            return ''
          } else if (fieldId && fieldObj.value.length > 1) {
            return this.config.data[field].options[fieldId]
          } else if (a && a.dataTypeEnum._name === 'STRING') {
            return fieldObj.value[0]
          } else if (
            a &&
            (a.dataTypeEnum._name === 'NUMBER' ||
              a.dataTypeEnum._name === 'DECIMAL') &&
            field !== 'siteId' &&
            (!a.default ||
              a.name === 'serialNumber' ||
              a.name === 'maxOccupancy' ||
              a.name === 'area')
          ) {
            return fieldObj.value[0].toString()
          } else {
            return this.config.data[field].options[fieldObj.value[0]]
          }
        }
      } else {
        return ''
      }
    },
    siteDetail() {
      if (this.sitedetails) {
        this.sitedetails.forEach(status => {
          this.siteMap[status.id] = status.name
        })
      }
    },
    handleClose(tag, index) {
      let filter = {}
      filter = this.$helpers.cloneObject(this.filtersApplied)
      if (
        index &&
        tag !== 'dueDate' &&
        tag !== 'responseDueDate' &&
        tag !== 'createdTime' &&
        tag !== 'actualWorkEnd' &&
        tag !== 'acknowledgedTime' &&
        tag !== 'warrantyExpiryDate' &&
        tag !== 'clearedTime' &&
        tag !== 'modifiedTime'
      ) {
        let a = []
        let b = []
        a = filter[tag].value
        b = a.filter(item => item !== index)
        filter[tag].value = b
        if (b.length > 0) {
          this.filtersApplied[tag].value = filter[tag].value
        } else {
          delete this.filtersApplied[tag]
          if (
            (tag === 'type' || tag === 'typeId') &&
            (this.moduleMeta.name === 'workorder' ||
              this.moduleMeta.name === 'preventivemaintenance')
          ) {
            tag = 'ticketType'
          } else if (tag === 'category' && this.isAssetsCategory) {
            tag = 'assetCategory'
          } else if (
            tag === 'tenant' &&
            (this.moduleMeta.name === 'workorder' ||
              this.moduleMeta.name === 'preventivemaintenance')
          ) {
            tag = 'tenant'
          } else if (
            tag === 'categoryId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'category'
          } else if (
            tag === 'assignedToId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'assignedTo'
          } else if (
            tag === 'priorityId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'priority'
          } else if (tag === 'department') {
            tag = 'assetDepartment'
          } else if (tag === 'type') {
            tag = 'assetType'
          } else if (tag === 'assetCategoryId') {
            tag = 'assetCategory'
          } else if (tag === 'alarmSeverityId') {
            tag = 'severity'
          }
          this.config.data[tag].value = []
        }
      } else if (!index) {
        if (tag === 'subject' || tag === 'name' || tag === 'title') {
          delete this.filtersApplied[tag]
          this.config.data[tag].value = ''
        } else if (tag === 'resource' || tag === 'resourceId') {
          delete this.filtersApplied[tag]
        } else {
          delete this.filtersApplied[tag]
          if (
            (tag === 'type' || tag === 'typeId') &&
            (this.moduleMeta.name === 'workorder' ||
              this.moduleMeta.name === 'preventivemaintenance')
          ) {
            tag = 'ticketType'
          } else if (
            (tag === 'category' && this.isAssetsCategory) ||
            tag === 'assetCategoryId'
          ) {
            tag = 'assetCategory'
          } else if (
            tag === 'categoryId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'category'
          } else if (
            tag === 'assignedToId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'assignedTo'
          } else if (
            tag === 'priorityId' &&
            this.moduleMeta.name === 'preventivemaintenance'
          ) {
            tag = 'priority'
          } else if (
            tag === 'tenant' &&
            (this.moduleMeta.name === 'workorder' ||
              this.moduleMeta.name === 'preventivemaintenance')
          ) {
            tag = 'tenant'
          } else if (tag === 'department') {
            tag = 'assetDepartment'
          } else if (tag === 'type') {
            tag = 'assetType'
          }
          this.config.data[tag].value = []
        }
      } else if (
        tag === 'createdTime' ||
        tag === 'dueDate' ||
        tag === 'responseDueDate' ||
        tag === 'actualWorkEnd' ||
        tag === 'acknowledgedTime' ||
        tag === 'warrantyExpiryDate' ||
        tag === 'clearedTime' ||
        tag === 'modifiedTime'
      ) {
        let a = []
        let b = []
        a = filter[tag]
        b = a.filter(item => item['operatorId'] !== index)
        filter[tag] = b
        if (b.length > 0) {
          this.filtersApplied[tag] = filter[tag]
        } else {
          delete this.filtersApplied[tag]
          this.config.data[tag].value = []
        }
      }
      let queryParam = {
        search: JSON.stringify(this.filtersApplied),
      }
      if (this.$route.query.view) {
        queryParam.view = this.$helpers.cloneObject(this.$route.query.view)
      }
      this.$router.replace({
        query: {
          ...this.$route.query,
          search: JSON.stringify(this.filtersApplied),
          includeParentFilter: true,
        },
      })
      if (Object.keys(this.filtersApplied).length <= '0') {
        let querry = {}
        if (this.$route.query.view) {
          querry.view = this.$helpers.cloneObject(this.$route.query.view)
        }
        this.$router.push({
          query: querry,
        })
      }
    },
    showInput() {
      this.inputVisible = true
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    outsideClick() {
      this.showFilterCondition = false
    },
    applyfilters() {
      this.filtersApplied = this.filters
    },
    back() {
      this.showTagFilterCondition = false
      this.showFilterList = true
    },
    showFilter(value) {
      this.fieldName = value
      this.showFilterList = false
      this.showTagFilterCondition = true
      if (
        this.fieldName === 'sourceType' ||
        this.fieldName === 'alarmType' ||
        (this.config.data[this.fieldName].operator &&
          this.config.data[this.fieldName].operator === 'NUMBER')
      ) {
        this.$set(this.config.data[this.fieldName], 'operatorId', 9)
      }
      if (
        !this.config.data[this.fieldName].operatorId &&
        this.fieldName !== 'createdTime' &&
        this.fieldName !== 'sourceType' &&
        this.fieldName !== 'dueDate' &&
        this.fieldName !== 'responseDueDate' &&
        this.fieldName !== 'actualWorkEnd' &&
        this.fieldName !== 'acknowledgedTime' &&
        this.fieldName !== 'clearedTime' &&
        this.fieldName !== 'modifiedTime' &&
        this.fieldName !== 'lastPurchasedDate' &&
        this.fieldName !== 'lastIssuedDate'
      ) {
        this.$set(
          this.config.data[this.fieldName],
          'operatorId',
          spaceFields.includes(this.fieldName)
            ? 38
            : this.fieldName === 'isAcknowledged' ||
              this.fieldName === 'connected'
            ? 15
            : this.fieldName === 'subject' ||
              this.fieldName === 'name' ||
              (this.config.data[this.fieldName].operator &&
                this.config.data[this.fieldName].operator === 'STRING') ||
              this.fieldName === 'title'
            ? 5
            : 36
        )
      } else if (this.config.data[this.fieldName].operatorId) {
        let filter = this.appliedFilters[this.fieldName]
        if (!isEmpty(filter) && Array.isArray(filter)) {
          filter = filter[0]
          this.$set(
            this.config.data[this.fieldName],
            'operatorId',
            filter && filter.operatorId ? filter.operatorId : ''
          )
        }
      }
    },
    associateResource(resource, field) {
      this.$set(field, 'chooserVisibility', false)
      field.value = resource.resourceList
        ? resource.resourceList.map(res => res.id)
        : []
      this.onFilterChanged()
      this.apply()
    },
    selectedlist(label, operatorType) {
      if (
        label._name === 'BUILDING_IS' &&
        !spaceFields.includes(this.fieldName)
      ) {
        return false
      } else if (label._name === 'LOOKUP') {
        return false
      } else if (this.excludeOperators.includes(label.operatorId)) {
        return false
      } else {
        return true
      }
    },
    setCustomDateFilters(filters, fieldName) {
      let constructDateFilter = val => {
        val = val.split('_')
        let operatorId = val.length === 1 ? parseInt(val[0]) : parseInt(val[1])
        let filter = {
          operatorId: operatorId,
        }
        if (operatorId === 20) {
          let dateRange = this.config.data[fieldName].dateRange
          if (!dateRange || !dateRange.length) {
            return false
          }
          filter.value = this.config.data[fieldName].dateRange.map(
            date => date + ''
          )
        } else if (this.isDateOperatorWithValue(operatorId)) {
          filter.value = [val[0]]
        }
        return filter
      }

      let filterVal = this.config.data[fieldName].value
      let key = this.config.data[fieldName].key || fieldName
      if (Array.isArray(filterVal)) {
        filters[key] = []
        filterVal.forEach(fval => {
          let filter = constructDateFilter(fval)
          if (filter) {
            filters[key].push(filter)
          }
        })
      } else {
        let filter = constructDateFilter(filterVal)
        if (filter) {
          filters[key] = filter
        }
      }
    },
    isDateOperatorWithValue(operatorId) {
      // TODO get value from date operator
      return [39, 40, 41, 50, 59, 60, 61].includes(operatorId)
    },
    apply() {
      let filters = {}
      let lookupFilter = {}
      for (let fieldName in this.config.data) {
        if (
          typeof this.config.data[fieldName].isCustomFieldLookup ===
            'undefined' ||
          !this.config.data[fieldName].isCustomFieldLookup
        ) {
          if (fieldName === 'rule') {
            this.config.data.rule.push({
              moduleName: 'alarmRaeding',
            })
          }
          let field = this.config.data[fieldName]
          let key = field.key || fieldName
          if (
            (field.operatorId === 1 || field.operatorId === 2 || field.value) &&
            (field.operatorId === 1 ||
              field.operatorId === 2 ||
              !Array.isArray(field.value) ||
              field.value.length)
          ) {
            if (field.customdate) {
              this.setCustomDateFilters(filters, fieldName)
            } else {
              let filterObj = {
                operatorId:
                  field.operatorId ||
                  (spaceFields.includes(fieldName)
                    ? 38
                    : fieldName === 'subject' ||
                      (this.config.data[this.fieldName].operator &&
                        this.config.data[this.fieldName].operator ===
                          'STRING') ||
                      fieldName === 'name' ||
                      fieldName === 'title'
                    ? 5
                    : fieldName === 'sourceType' ||
                      (this.config.data[fieldName].operator &&
                        this.config.data[fieldName].operator === 'NUMBER') ||
                      fieldName === 'alarmType'
                    ? 9
                    : fieldName === 'isAcknowledged' ||
                      fieldName === 'connected'
                    ? 15
                    : 36),
              }
              if (field.operatorId === 1 || field.operatorId === 2) {
                if (
                  fieldName === 'subject' ||
                  fieldName === 'name' ||
                  fieldName === 'title'
                ) {
                  field.value = ''
                } else if (
                  this.config.data[this.fieldName].operator &&
                  this.config.data[this.fieldName].operator === 'STRING'
                ) {
                  field.value = ''
                } else {
                  field.value = []
                }
              }
              if (fieldName === 'assignedTo') {
                let users = [],
                  groups = []
                field.value.forEach(user => {
                  if (user.indexOf('_group') !== -1) {
                    groups.push(user.substring(0, user.indexOf('_group')))
                  } else {
                    users.push(user)
                  }
                })
                if (users.length) {
                  filterObj.value = users
                }
                if (groups.length) {
                  filterObj.value = groups
                  key =
                    key === 'assignedToId'
                      ? 'assignmentGroupId'
                      : 'assignmentGroup'
                }
                filters[key] = filterObj
              } else {
                filterObj.value = !Array.isArray(field.value)
                  ? [field.value]
                  : field.value.map(val => val + '')
                if (spaceFields.includes(fieldName) || fieldName === 'asset') {
                  key = field.key || 'resource'
                  if (!filters[key]) {
                    filters[key] = []
                  }
                  filters[key].push(filterObj)
                } else {
                  filters[key] = filterObj
                }
              }
            }
          }
        }
      }
      if (Object.keys(this.formModel).length) {
        for (let key in this.formModel) {
          if (this.formModel[key].id) {
            let value = [
              {
                operatorId:
                  this.config.data[key] && this.config.data[key].operatorId
                    ? this.config.data[key].operatorId
                    : 36,
                value: [
                  this.formModel[key].id
                    ? this.formModel[key].id.toString()
                    : '',
                ],
                selectedLabel: this.formModel[key].label,
              },
            ]
            this.$set(lookupFilter, key, value)
          }
        }
      }
      if (
        Object.keys(filters).length > '0' ||
        Object.keys(this.formModel).length
      ) {
        let queryParam = {
          search: JSON.stringify(filters),
        }
        if (this.config.includeParentCriteria) {
          queryParam.includeParentFilter = true
        }
        if (this.$route.query.view) {
          queryParam.view = this.$helpers.cloneObject(this.$route.query.view)
        }
        filters = { ...filters, ...lookupFilter }
        this.$router.replace({
          query: {
            ...this.$route.query,
            search: JSON.stringify(filters),
            includeParentFilter: true,
          },
        })
      }
      this.showTagFilterCondition = false
    },
    init() {
      let filters = Object.assign({}, this.appliedFilters)
      let self = this
      let fieldKeyMap = {}
      for (let fieldName in this.config.data) {
        if (
          this.config.data.hasOwnProperty(fieldName) &&
          this.config.data[fieldName].key
        ) {
          fieldKeyMap[this.config.data[fieldName].key] = fieldName
        }
      }

      for (let fieldKey in filters) {
        if (
          filters.hasOwnProperty(fieldKey) ||
          fieldKey === 'assignmentGroup'
        ) {
          let filter = filters[fieldKey]
          let fieldName =
            fieldKey === 'assignmentGroup' ? 'assignedTo' : fieldKey
          let field
          let isResourceField = false
          if (fieldName === 'resource' || fieldName === 'resourceId') {
            field =
              this.config.data.space || this.config.data.asset
                ? {
                    parent: 'resource',
                  }
                : null
            isResourceField = true
          } else {
            field =
              this.config.data[fieldName] ||
              this.config.data[fieldKeyMap[fieldName]]
          }
          if (field) {
            let filterVal
            if (Array.isArray(filter)) {
              filterVal = []
              filter.forEach(fval => {
                if (isResourceField) {
                  filterVal = []
                  field =
                    fval.operatorId === 38
                      ? this.config.data.space
                      : this.config.data.asset
                }

                let val = self.getValuesFromFilter(
                  field,
                  fval,
                  fieldKeyMap[fieldName] || fieldName
                )
                if (Array.isArray(val)) {
                  filterVal = [...filterVal, ...val]
                } else {
                  field.operatorId === 15
                    ? (filterVal = val)
                    : filterVal.push(val)
                }

                if (isResourceField) {
                  self.setFieldValue(filterVal, field, fieldKey)
                }
              })
            } else {
              if (isResourceField) {
                field =
                  filter.operatorId === 38
                    ? this.config.data.space
                    : this.config.data.asset
                field =
                  filter.operatorId === 38
                    ? this.config.data.space
                    : this.config.data.asset
              }
              filterVal = self.getValuesFromFilter(
                field,
                filter,
                fieldKeyMap[fieldName] || fieldName
              )
            }

            if (!Array.isArray(filter) || !isResourceField) {
              field.operatorId = filter.operatorId
              self.setFieldValue(filterVal, field, fieldKey)
            }
          }
        }
      }
    },
    getValuesFromFilter(field, filter, fieldName) {
      let value
      // TODO temporary...needs to improve
      if (filter.operatorId === 35) {
        value = this.getLookupValue(field, filter, fieldName)
      } else if (filter.operatorId === 15 || field.operatorId === 15) {
        value = filter.value && filter.value[0] === 'true' ? 'true' : 'false'
      } else if (
        field.customdate &&
        (!filter.value || !filter.value.length) &&
        field.options[filter.operatorId]
      ) {
        value = field.single ? filter.operatorId + '' : [filter.operatorId + '']
      } else if (filter.value) {
        if (field.type === 'date' && filter.operatorId === 20) {
          value = '20'
          field.dateRange = filter.value
        } else if (this.isDateOperatorWithValue(filter.operatorId)) {
          value = filter.value[0] + '_' + filter.operatorId
          value = field.single ? value : [value]
        } else {
          value = field.single ? filter.value[0] : filter.value
        }
      }
      return value
    },
    setFieldValue(filterVal, field, fieldKey) {
      if (filterVal) {
        if (fieldKey === 'assignmentGroup' || fieldKey === 'assignedTo') {
          filterVal =
            fieldKey === 'assignmentGroup'
              ? filterVal.filter(id => id !== '-100').map(id => id + '_group')
              : filterVal
          if (field.value && !field.single) {
            field.value = [...field.value, ...filterVal]
          } else {
            field.value = filterVal
          }
        } else {
          field.value = filterVal
        }
      }
    },
    onFilterChanged(value) {
      this.apply()
      if (this.filterApplied) {
        this.filterApplied = false
      }
    },
    formatOptions(field, list) {
      let data = this.config.data
      if (data[field]) {
        list.forEach(val => {
          this.$set(data[field].options, val.id, val.name)
        })
      }
    },
    setOptions() {
      let data = this.config.data
      let userOptions = {}
      this.users.forEach(user => {
        userOptions[user.id] = user.name
      })
      if (data.assignedTo) {
        data.assignedTo.options = userOptions
        this.groups.forEach(group => {
          data.assignedTo.options[group.id + '_group'] = group.name
        })
      }
      if (!isEmpty(this.formModel)) {
        for (let key in this.formModel) {
          if (!isEmpty(data[key]) && !isEmpty(data[key]).value) {
            this.loadPickList(data[key].lookupModuleName, data[key])
          }
        }
      }

      if (data.category) {
        this.loadPickList('ticketcategory', data.category)
      }

      if (data.tenant) {
        this.loadPickList('tenant', data.tenant)
      }
      if (data.vendor) {
        this.loadPickList('vendors', data.vendor)
      }
      if (data.itemTypes) {
        this.loadPickList('itemTypes', data.itemTypes)
      }

      if (data.priority) {
        if (!isEmpty(this.ticketpriority)) {
          this.ticketpriority.forEach(prior => {
            data.priority.options[prior.id] = prior.displayName
          })
        }
      }
      if (data.siteId) {
        this.siteList.forEach(sit => {
          data.siteId.options[sit.id] = sit.name
        })
      }
      if (data.severity) {
        if (!isEmpty(this.alarmseverity)) {
          this.alarmseverity.forEach(severity => {
            data.severity.options[severity.id] = severity.severity
            if (data.previousSeverity) {
              data.previousSeverity.options[severity.id] = severity.severity
            }
          })
        }
      }
      if (data.acknowledgedBy) {
        data.acknowledgedBy.options = userOptions
      }
      if (data.clearedBy) {
        data.clearedBy.options = userOptions
      }
      if (data.ticketType) {
        if (!isEmpty(this.tickettype)) {
          data.ticketType.options = this.tickettype
        }
      }
      if (data.frequency) {
        data.frequency.options = Object.assign(
          {},
          {
            0: 'Once',
          },
          this.$constants.FACILIO_FREQUENCY
        )
      }
      if (!isEmpty(this.assetcategory)) {
        this.formatOptions('assetCategory', this.assetcategory)
      }
      if (!isEmpty(this.assetdepartment)) {
        this.formatOptions('assetDepartment', this.assetdepartment)
      }
      if (!isEmpty(this.assettype)) {
        this.formatOptions('assetType', this.assettype)
      }
      this.constructFormmodelForLookupFields()
    },
    async loadPickList(moduleName, field) {
      this.picklistOptions = {}

      let { error, options } = await getFieldOptions({
        field: { lookupModuleName: moduleName, skipDeserialize: true },
      })

      if (error) {
        this.$message.error(error.message || 'Error Occured')
      } else {
        this.$set(field, 'options', options)
      }
    },
    handleInputConfirm() {
      let inputValue = this.inputValue
      if (inputValue) {
        this.dynamicTags.push(inputValue)
      }
      this.inputVisible = false
      this.inputValue = ''
    },
    isBuildingOperator(filter) {
      if (Array.isArray(filter)) {
        return filter.length && filter[0].operatorId === 38
      } else {
        return filter.operatorId === 38
      }
    },
    close() {
      if (!this.showTagFilterCondition && !this.showingLookupWizard)
        this.showFilterList = false
    },
    handleConditionClose() {
      if (!this.showingLookupWizard) {
        this.showTagFilterCondition = false
      }
    },
  },
}
</script>
<style>
.button-new-tag {
  margin-left: 10px;
  height: 32px;
  line-height: 30px;
  padding-top: 0;
  padding-bottom: 0;
}

.filter-search-tag {
  font-size: 13px;
  display: flex;
  letter-spacing: 0.5px;
  color: #748893;
  border-radius: 3px;
  background-color: #eaeef0;
  border: none;
  padding-top: 2px;
  margin-right: 10px;
  margin-bottom: 8px;
}

.filter-search-tag:hover {
  border-radius: 3px;
  background-color: #ddf1f4;
  font-size: 13px;
  letter-spacing: 0.5px;
  color: #38a1ae;
  border: none;
  padding-top: 2px;
  cursor: pointer;
}

.filter-search-tag .el-icon-close {
  color: #748893;
  font-weight: 500;
  top: 8px;
}

.filter-search-tag .el-icon-close:hover {
  color: #748893;
  font-weight: 500;
  background: none;
}

.tag-inline {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
}

.tag-list-container {
  position: relative;
}

.tag-list-block {
  width: 240px;
  min-height: 300px;
  height: 300px;
  overflow-x: hidden;
  overflow-y: scroll;
  box-shadow: 1px 7px 11px 3px #dbe1e4;
  background-color: #ffffff;
  border: 1px solid #d0d9e2;
  position: absolute;
  left: 10px;
  top: 33px;
}

.tag-list-body {
  padding-top: 10px;
  padding-bottom: 20px;
}

.tag-list-filter-condition-container {
  position: absolute;
  left: 380px;
  top: -6px;
}

.filter-search-tag .el-tag .el-icon-close {
  left: 91px;
  top: -30px;
}

.resource-list .multi .el-select .el-tag__close.el-icon-close,
.resource-list .multi .el-input__suffix {
  display: none;
}
.tag-list-search {
  position: sticky;
  top: 0;
  z-index: 200;
  background: #ffffff;
}
</style>
