<template>
  <el-row :gutter="20">
    <el-col :span="12">
      <p class="fc-input-label-txt">
        {{ $t('viewsmanager.sorting.field') }}
      </p>
      <Select
        v-model="sortConfig.orderBy"
        :options="sortableFields"
        valueName="name"
        labelName="displayName"
        :placeholder="placeholder"
        :filterable="true"
        @change="setOrdertype"
      />
    </el-col>
    <el-col :span="12">
      <p class="fc-input-label-txt">
        {{ $t('common._common.sort_by') }}
      </p>
      <Select
        v-model="sortConfig.orderType"
        :options="orderType"
        valueName="value"
        labelName="label"
      />
    </el-col>
  </el-row>
</template>
<script>
import { Select } from '@facilio/ui/forms'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

export default {
  components: { Select },
  props: ['moduleName', 'viewDetail'],
  data() {
    return {
      placeholder: 'Select Field',
      sortableFields: {},
      sortConfig: {
        orderBy: null,
        orderType: null,
      },
      orderType: [
        {
          label: this.$t('common._common.ascending'),
          value: 'asc',
        },
        {
          label: this.$t('common._common.descending'),
          value: 'desc',
        },
      ],
    }
  },
  created() {
    this.fetchSortablefields()
    this.initData()
  },
  methods: {
    initData() {
      let { sortFields = [] } = this.viewDetail || {}
      if (!isEmpty(sortFields)) {
        let { name } = this.$getProperty(sortFields[0], 'sortField', {})
        this.sortConfig = {
          orderType: sortFields[0].isAscending ? 'asc' : 'desc',
          orderBy: name,
        }
      }
    },
    setOrdertype(value) {
      this.$set(this.sortConfig, 'orderType', value ? 'asc' : null)
    },
    fetchSortablefields() {
      let { moduleName } = this
      if (!isEmpty(moduleName)) {
        API.get(`v2/fields/sortable/${this.moduleName}`).then(
          ({ data, error }) => {
            if (error) {
              this.$message.error(error.message || 'Error Occured')
            } else {
              this.sortableFields = data.fields || {}
            }
          }
        )
      }
    },
    serializeData() {
      let { orderBy, orderType } = this.sortConfig
      return { orderBy, orderType }
    },
  },
}
</script>
