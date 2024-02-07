<template>
  <div
    class="p10 kpi-violations-list fc-list-view fc-list-table-container fc-table-td-height fc-table-viewchooser"
  >
    <!-- action area for tab-->
    <portal :to="tab.name + '-title-section'" slim>
      <new-date-picker
        :dateObj="dateObj"
        :zone="$timezone"
        @date="onDateChange"
        class="facilio-resource-date-picker fR"
        style="margin-top: -10px;"
      ></new-date-picker>
    </portal>
    <!-- action area -->

    <div v-if="loading" class="flex-middle fc-empty-white">
      <spinner :show="loading" size="80"></spinner>
    </div>

    <el-table
      v-else
      key="violationsList"
      :data="list"
      class="scrollable violations-table"
      style="width: 100%"
      height="250px"
      row-class-name="tablerow-edit-delete-icon"
    >
      <el-table-column prop="name" label="Assets / Space" width>
        <template v-slot="scope">
          <span v-if="!$validation.isEmpty(scope.row.resource)">{{
            scope.row.resource.name
          }}</span>
        </template>
      </el-table-column>

      <el-table-column label="Start Time">
        <template v-slot="scope">
          <span v-if="scope.row.createdTime">{{
            scope.row.createdTime | formatDate()
          }}</span>
          <span v-else>---</span>
        </template>
      </el-table-column>

      <el-table-column label="End Time">
        <template v-slot="scope">
          <span v-if="scope.row.status">{{
            scope.row.clearedTime | formatDate()
          }}</span>
          <span v-else>---</span>
        </template>
      </el-table-column>

      <el-table-column label="Duration">
        <template v-slot="scope">
          <span v-if="!$validation.isEmpty(scope.row.duration)">{{
            scope.row.duration
          }}</span>
          <span v-else>---</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import NewDatePicker from '@/NewDatePicker'
import { viewStateMeta } from './helpers/kpiConstants'
import NewDateHelper from '@/mixins/NewDateHelper'

export default {
  props: ['details', 'tab'],
  components: { NewDatePicker },

  data() {
    return {
      loading: false,
      dateObj: null,
      list: [],
    }
  },

  created() {
    this.init()
    this.loadData()
  },

  computed: {},

  methods: {
    init() {
      let { frequency } = this.details
      let { operatorID } = Object.values(viewStateMeta).find(freqObj => {
        return freqObj.frequencyId === frequency
      })

      this.dateObj = NewDateHelper.getDatePickerObject(operatorID || 44, null)
    },

    onDateChange(dateFilter) {
      this.$set(this, 'dateObj', dateFilter)
      this.loadData()
    },

    loadData() {
      let { dateObj, details } = this

      let filters = encodeURIComponent(
        JSON.stringify({
          formulaField: { operatorId: 36, value: [details.id + ''] },
          createdTime: {
            operatorId: 20,
            value: [dateObj.value[0] + '', dateObj.value[1] + ''],
          },
        })
      )
      let url = `/v2/alarmOccurrence/occurrenceList?occurrenceModule=violationalarmoccurrence&filters=${filters}`

      this.loading = true
      this.$http(url)
        .then(({ data }) => {
          if (data.responseCode === 0) {
            this.list = data.result.records || []
          }
          this.loading = false
        })
        .catch(error => {
          this.loading = false
        })
    },
  },
}
</script>
<style lang="scss">
.kpi-violations-list {
  height: calc(100vh - 220px);

  &.fc-list-table-container .el-table {
    padding-bottom: 0 !important;
    th > .cell {
      font-weight: 500;
    }
    td > .cell {
      font-size: 13px;
      padding: 5px 0;
    }
  }
}
</style>
