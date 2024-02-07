<template>
  <FContainer>
    <portal :to="`action-${widget.id}-${widget.name}`">
      <FContainer class="related-list-widget-header-action">
        <MainFieldSearch
          v-if="!isEmpty(criteriaData) || !isEmpty(searchText)"
          :mainFieldObj="mainFieldObj"
          :search.sync="searchText"
        ></MainFieldSearch>
      </FContainer>
    </portal>
    <FContainer
      v-if="isLoading"
      display="flex"
      justifyContent="center"
      alignItems="center"
      position="relative"
      top="50%"
    >
      <FSpinner :size="30" />
    </FContainer>
    <FContainer v-else>
      <FContainer
        display="flex"
        justifyContent="center"
        alignItems="center"
        v-if="$validation.isEmpty(criteriaData)"
      >
        <FContainer>
          <img
            width="250"
            height="250"
            src="https://www.pngall.com/wp-content/uploads/5/Construction-Maintenance-Transparent.png"
          />
        </FContainer>
        <FContainer display="block">
          <FText>
            {{ 'Criteria not defined yet!' }}
          </FText>
        </FContainer>
      </FContainer>
      <FTable
        v-else
        :columns="columns"
        :data="criteriaData"
        hideBorder
        :showSelect="false"
      >
        <template #[`cell.fieldName`]="record">
          <FTooltip placement="top">
            <template slot="title">{{ getFieldName(record) }}</template>
            <FText class="truncate-text" cursor="pointer">
              {{ getFieldName(record) }}
            </FText>
          </FTooltip>
        </template>
        <template #[`cell.operatorId`]="record">
          <FText>
            {{ getOperatorDetails(record) }}
          </FText>
        </template>
        <template #[`cell.value`]="record">
          <FText v-if="isValueNotDefined(record.row)">
            {{ `N/A` }}
          </FText>
          <FContainer v-else>
            <FContainer
              v-if="
                [
                  'LOOKUP',
                  'ENUM',
                  'MULTI_ENUM',
                  'MULTI_LOOKUP',
                  'STRING_SYSTEM_ENUM',
                  'SYSTEM_ENUM',
                ].includes(getDataType(record))
              "
              display="flex"
              gap="containerMedium"
            >
              <FTags appearance="simple" :text="record.row.value[0].label" />
              <FPopover placement="bottom" trigger="clickToToggle">
                <FContainer
                  slot="content"
                  minHeight="150px"
                  maxHeight="300px"
                  width="250px"
                  borderRadius="medium"
                  padding="containerXLarge"
                  overflowY="scroll"
                >
                  <FContainer
                    v-for="(value, index) in record.row.value.slice(1)"
                    :key="`lookup-${index}-${value.label}`"
                    display="flex"
                    flexDirection="column"
                    gap="containerLarge"
                  >
                    <FTooltip placement="top">
                      <template slot="title">{{
                        $getProperty(value, 'label', value.value || '---')
                      }}</template>
                      <FText
                        class="criteriaMoreValue truncate-text"
                        cursor="pointer"
                        padding="containerLarge"
                      >
                        {{ $getProperty(value, 'label', value.value || '---') }}
                      </FText>
                    </FTooltip>
                  </FContainer>
                </FContainer>
                <FTags
                  appearance="simple"
                  :text="`+${record.row.value.length - 1}`"
                  v-if="record.row.value.length > 1"
                />
              </FPopover>
            </FContainer>
            <FContainer
              v-else-if="['DATE', 'DATE_TIME'].includes(getDataType(record))"
              display="flex"
              gap="containerLarge"
            >
              <FTooltip
                placement="top"
                v-if="canShowDates(getOperatorDetails(record))"
              >
                <template slot="title"> {{ getDateInfo(record) }}</template>
                <FText class="truncate-text" cursor="pointer">
                  {{ getDateInfo(record) }}
                </FText>
              </FTooltip>
              <FText v-else>
                {{ `N/A` }}
              </FText>
            </FContainer>
            <FContainer
              v-else-if="isOneLevelLookup(record.row)"
              display="flex"
              gap="containerLarge"
            >
              <FText>
                {{ `OLV` }}
              </FText>
            </FContainer>
            <FContainer
              v-else-if="
                ['STRING', 'BIG_STRING', 'URL_FIELD'].includes(
                  getDataType(record)
                )
              "
            >
              <FTooltip placement="top">
                <template slot="title">
                  {{ $getProperty(record, 'row.value.0', '---') }}</template
                >
                <FText class="truncate-text" cursor="pointer">
                  {{ $getProperty(record, 'row.value.0', '---') }}
                </FText>
              </FTooltip>
            </FContainer>
            <FContainer v-else display="flex" gap="containerLarge">
              <FTooltip placement="top">
                <template slot="title">
                  {{ $getProperty(record, 'row.value.0', '---') }}</template
                >
                <FText class="truncate-text" cursor="pointer">
                  {{ $getProperty(record, 'row.value.0', '---') }}
                </FText>
              </FTooltip>
            </FContainer>
          </FContainer>
        </template>
      </FTable>
    </FContainer>
    <portal :to="`footer-${widget.id}-${widget.name}`">
      <Pagination
        v-if="!isEmpty(recordCount) || !isEmpty(criteriaData)"
        :key="`pagination-${criteriaModuleName}`"
        :totalCount="recordCount"
        :currentPageNo.sync="page"
        :currentPageCount="(criteriaData || []).length"
        :perPage="perPage"
      />
    </portal>
  </FContainer>
</template>
<script>
import {
  FTable,
  FContainer,
  FText,
  FSpinner,
  FTags,
  FPopover,
  FTooltip,
} from '@facilio/design-system'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import Pagination from 'src/beta/list/Pagination'
import MainFieldSearch from 'src/beta/components/MainFieldSearch'
import helpers from 'src/util/helpers'
const { getOrgMoment: moment } = helpers

export default {
  props: ['widget', 'details'],
  components: {
    FTable,
    FText,
    FContainer,
    FSpinner,
    FTags,
    FPopover,
    FTooltip,
    Pagination,
    MainFieldSearch,
  },
  data() {
    return {
      columns: [
        { displayName: 'Fields', name: 'fieldName', id: 1 },
        { displayName: 'Operator', name: 'operatorId', id: 2 },
        { displayName: 'Value', name: 'value', id: 3 },
      ],
      data: [],
      page: 1,
      perPage: 10,
      criteriaData: null,
      operators: null,
      advancedFields: null,
      lookupModuleFieldList: null,
      isLoading: false,
      searchText: '',
      recordCount: 10, // To be changed
      mainFieldObj: { dataTypeEnum: 'STRING', name: 'fieldName' },
    }
  },
  computed: {
    criteriaModuleName() {
      return this.$getProperty(
        this,
        'widget.widgetParams.criteriaModuleName',
        ''
      )
    },
    criteriaFieldName() {
      return this.$getProperty(
        this,
        'widget.widgetParams.criteriaFieldName',
        ''
      )
    },
  },
  watch: {
    page() {
      this.loadCriteriaDetails(true)
    },
    searchText() {
      this.loadCriteriaDetails(true)
    },
  },
  created() {
    this.init()
    this.isEmpty = isEmpty
  },
  methods: {
    async init() {
      this.isLoading = true
      await this.loadCriteriaFields()
      await this.loadCriteriaDetails()
      this.isLoading = false
    },
    async loadCriteriaDetails(force = false) {
      this.isLoading = true
      let {
        details,
        criteriaModuleName,
        page,
        perPage,
        searchText,
        criteriaFieldName,
      } = this
      if (!isEmpty(criteriaModuleName)) {
        let criteriaId = this.$getProperty(details, criteriaFieldName, null)
        if (!isEmpty(criteriaId)) {
          let url = 'v2/ruleCriteria/get'
          let params = {
            moduleName: criteriaModuleName,
            criteriaId,
            page,
            perPage,
            force,
            withCount: true,
          }
          if (!isEmpty(searchText)) {
            params = { ...params, search: searchText }
          }
          let { data, error } = await API.get(url, params)
          if (!error) {
            let { conditions } = data || {}
            this.criteriaData = conditions || []
          } else {
            this.$message.error(error.message || 'Error Occured')
          }
        }
      }
      this.isLoading = false
    },
    async loadCriteriaFields() {
      await this.loadOperators()
      await this.loadFields(true)
    },
    async loadOperators() {
      let url = `v2/filter/advanced/operators`
      let { data, error } = await API.get(url)
      if (error) {
        let { message = 'Error Occured' } = error || {}
        this.$message.error(message)
      } else {
        let { operators } = data || {}
        if (!isEmpty(operators)) {
          this.operators = operators
        }
      }
    },
    async loadFields(force) {
      let { criteriaModuleName: moduleName } = this
      let url = `/v2/filter/advanced/fields/${moduleName}`
      let oneLevelUrl = `/v2/lookupFields/list?moduleName=${moduleName}&OneLevelFields=true`
      let response = await Promise.all([
        API.get(url, { force }),
        API.get(oneLevelUrl, { force }),
      ])
      let [advancedFields, oneLevelFields] = response
      let { data: advancedData, error: advancedFieldsError } = advancedFields
      let { fields: advancedFieldsList } = advancedData
      this.advancedFields = advancedFieldsList.map(field => ({
        ...field,
        multiple: false,
      }))
      let {
        data: oneLevelFieldsData,
        error: oneLevelFieldsError,
      } = oneLevelFields
      if (advancedFieldsError || oneLevelFieldsError) {
        let { message = 'Error occured' } =
          advancedFieldsError || oneLevelFieldsError || {}
        this.$message.error(message)
      } else {
        let { fields } = oneLevelFieldsData
        let { lookupModuleFields } = fields
        this.lookupModuleFieldList = lookupModuleFields
      }
    },
    getField(fieldName) {
      let { advancedFields } = this
      let actualField =
        (advancedFields || []).find(field => field.name === fieldName) || {}
      return actualField
    },
    getChildField(lookupModule, childFieldName) {
      let { lookupModuleFieldList } = this
      let activeModuleFields = lookupModuleFieldList[lookupModule] || []
      let actualField =
        (activeModuleFields || []).find(
          field => field.name === childFieldName
        ) || {}
      return actualField
    },
    getFieldName(field) {
      let { row: record } = field || {}
      if (this.isOneLevelLookup(record)) {
        return this.getOneLevelFields(record)
      } else {
        let { fieldName } = record || {}
        let actualField = this.getField(fieldName)

        return this.$getProperty(actualField, 'displayName', '---')
      }
    },

    isValueNotDefined(record) {
      let { operatorId, value } = record || {}
      return !isEmpty(operatorId) && isEmpty(value)
    },
    getOperatorDetails(record) {
      let { operators } = this
      let { row } = record || {}
      let { fieldName, operatorId } = row || {}
      if (this.isOneLevelLookup(row)) {
        return this.getOneLevelOperatorDetails(row)
      } else {
        let actualField = this.getField(fieldName)
        let { dataType } = actualField || {}
        let activeFieldOperators = operators[dataType] || []
        let currentOperator = activeFieldOperators.find(
          operator => operator.operatorId === operatorId
        )

        return this.$getProperty(currentOperator, 'displayName', '---')
      }
    },
    getOneLevelOperatorDetails(record) {
      let { operators } = this
      let { CriteriaValue, lookupModuleName } = record || {}
      let { operatorId, fieldName } = CriteriaValue || {}
      let actualField = this.getChildField(lookupModuleName, fieldName) || {}
      let { dataType } = actualField || {}
      let activeFieldOperators = operators[dataType] || []
      let currentOperator = activeFieldOperators.find(
        operator => operator.operatorId === operatorId
      )

      return this.$getProperty(currentOperator, 'displayName', '---')
    },
    canShowDates(dateInfo) {
      return !['Next 2 Days', 'Next 7 Days'].includes(dateInfo)
    },
    isOneLevelLookup(record) {
      let { operatorId } = record || {}
      return operatorId === 35
    },
    getOneLevelFields(record) {
      let { fieldName, CriteriaValue, lookupModuleName } = record || {}
      let { fieldName: childFieldName } = CriteriaValue || {}
      fieldName = (fieldName || '').split('.')[1]
      let actualParentField = this.getField(fieldName)
      let actualChildField = this.getChildField(
        lookupModuleName,
        childFieldName
      )
      let parentDisplayName = this.$getProperty(
        actualParentField,
        'displayName',
        '---'
      )
      let childDisplayName = this.$getProperty(
        actualChildField,
        'displayName',
        '---'
      )
      return `${parentDisplayName} / ${childDisplayName}`
    },
    getDateInfo(record) {
      let { row } = record || {}
      let { fieldName, value } = row || {}
      let actualField = this.getField(fieldName) || {}
      let { dataType } = actualField || {}
      let modifiedValue = (value || []).map(val => {
        return dataType === 'DATE'
          ? moment(Number(val)).format('MMM-DD-YYYY')
          : moment(Number(val)).format('MMM-DD-YYYY HH:MM')
      })
      return modifiedValue.join(' to ')
    },
    getDataType(record) {
      let { row } = record || {}
      let { fieldName } = row || {}
      let actualField = this.getField(fieldName) || {}
      let { dataType } = actualField || {}

      return dataType
    },
  },
}
</script>
<style lang="scss" scoped>
.criteriaMoreValue {
  &:hover {
    background-color: #efefef;
    border-radius: 6px;
  }
}
</style>
