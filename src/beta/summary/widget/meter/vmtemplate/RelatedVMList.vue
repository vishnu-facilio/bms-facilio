<template>
  <FContainer>
    <portal :to="`action-${widget.id}-${widget.name}`">
      <FContainer display="flex" cursor="pointer" @click="redirectToList()">
        <FText color="borderPrimaryDefault" appearance="headingMed14">{{
          $t('asset.virtual_meters.go_to_list_view')
        }}</FText>
        <fc-icon group="navigation" name="right" size="16" color="borderPrimaryDefault"></fc-icon>
      </FContainer>
    </portal>
    <FContainer>
      <FTable :columns="columns" :data="data" hideBorder :showSelect="false">
        <template #[`cell.id`]="record">
          <FContainer>
            <FText color="textMain" appearance="bodyReg14">{{
              getRecordId(record.row)
            }}</FText>
          </FContainer>
        </template>
        <template #[`cell.name`]="record">
          <FContainer cursor="pointer" @click="redirectToSummary(record.row)">
            <FText color="borderPrimaryDefault" appearance="bodyReg14">{{
              getRecordName(record.row)
            }}</FText>
          </FContainer>
        </template>
      </FTable>
    </FContainer>
  </FContainer>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'
import { FTable, FContainer, FText } from '@facilio/design-system'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'
export default {
  props: ['details', 'widget'],
  components: {
    FTable,
    FContainer,
    FText,
  },
  data() {
    return {
      isLoading: false,
      relatedList: [],
      relatedResource: {},
      columns: [
        { displayName: 'ID', name: 'id', id: 1 },
        { displayName: 'Name', name: 'name', id: 2 },
        { displayName: 'Related To', name: 'relatedTo', id: 3 },
        { displayName: 'Created Date', name: 'createdDate', id: 4 },
      ],
      data: [],
    }
  },
  async created() {
    await this.loadRelatedMeters()
    await this.getRelatedVMData()
  },
  methods: {
    async loadRelatedMeters() {
      let params = {
        page: 1,
        perPage: 5000,
        withCount: true,
        filters: JSON.stringify({
          virtualMeterTemplate: {
            operatorId: 36,
            value: [String((this.details || {}).id)],
          },
        }),
      }
      let moduleName = 'meter'
      let { list, error, meta } = await API.fetchAll(moduleName, params, {
        force: true,
      })
      if (isEmpty(error)) {
        this.relatedList = list
        this.relatedResource =
          this.$getProperty(meta, 'supplements.meter.servingTo', {}) || {}
      }
    },
    getRecordId(record) {
      let id = this.$getProperty(record, 'id', '--')
      return '#' + id
    },
    getRecordName(record) {
      return this.$getProperty(record, 'name', '--')
    },
    getRecordRelatedTo(record) {
      let id = this.$getProperty(record, 'servingTo.id', null)
      let { relatedResource } = this
      return !isEmpty(id) && !isEmpty(relatedResource)
        ? this.$getProperty(relatedResource, `${id}.name`, '--') || '--'
        : '--'
    },
    getRecordCreatedDate(record) {
      let time = this.$getProperty(record, 'sysCreatedTime')
      return !isEmpty(time) ? this.$options.filters.formatDate(time) : '--'
    },
    redirectToList() {
      let moduleName = 'meter'
      let { details } = this
      let id = this.$getProperty(details, 'id')
      let filterObj = {
        virtualMeterTemplate: {
          operatorId: 36,
          value: [JSON.stringify(id)],
        },
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.LIST) || {}
        let filter = JSON.stringify(filterObj)
        if (name) {
          this.$router.push({ name, query: { search: filter } })
        }
      } else {
        this.$router.push({
          name: 'MeterList',
        })
      }
    },
    redirectToSummary(record) {
      let { id } = record || {}
      if (!isEmpty(id)) {
        let moduleName = 'meter'
        if (isWebTabsEnabled()) {
          let { name } =
            findRouteForModule(moduleName, pageTypes.OVERVIEW) || {}
          if (name) {
            this.$router.push({
              name,
              params: {
                id,
                viewname: 'all',
              },
            })
          }
        }
      }
    },
    getRelatedVMData() {
      let { relatedList } = this
      this.data = (relatedList || []).map(record => {
        let { id, name } = record || {}
        return {
          id: id,
          name: name,
          relatedTo: this.getRecordRelatedTo(record),
          createdDate: this.getRecordCreatedDate(record),
        }
      })
    },
  },
}
</script>
