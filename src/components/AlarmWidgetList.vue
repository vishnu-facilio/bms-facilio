<template>
  <div>
    <template v-if="isActive">
      <portal
        v-if="portalName"
        :to="portalName"
        :key="portalName + '-portalwrap'"
        slim
      >
        <div class="fR position-relative" style="margin-left: auto;top: 5px;">
          <new-date-picker
            :zone="$timezone"
            class="filter-field date-filter-comp"
            style="margin-left: auto;"
            :dateObj="dateObj"
            @date="changeDateFilter"
          ></new-date-picker>
        </div>
      </portal>
    </template>
    <div v-if="loading" class="flex-middle fc-empty-white block">
      <spinner :show="loading" size="80"></spinner>
    </div>
    <div v-else-if="!isDataAvailable" class="block">
      <div class="mT40 mB40 text-center p30imp">
        <InlineSvg
          src="svgs/emptystate/alarmEmpty"
          iconClass="icon text-center icon-xxxxlg emptystate-icon-size"
        ></InlineSvg>
        <div class="fc-black-dark f18 bold">No {{ name }}!</div>
      </div>
    </div>
    <el-table
      class="alarm-occurence-table"
      v-else
      :data="list"
      style="width: 100%"
    >
      <el-table-column
        v-for="(field, index) in fields"
        :key="field.name + index"
        :label="field.displayName"
        :width="field.width"
        :prop="field.name"
      >
        <template v-slot="occurrence">
          <div
            class="fc-id"
            style="width:100px"
            v-if="field.displayType === 'ID'"
          >
            {{ '#' + occurrence.row[field.name] }}
          </div>
          <div
            class="table-subheading"
            v-else-if="field.displayType === 'DATE_TIME'"
          >
            {{
              occurrence.row[field.name] > 0
                ? getDateFomattedTz(occurrence.row[field.name])
                : '--'
            }}
          </div>
          <div
            class="table-subheading"
            v-else-if="field.displayType === 'DURATION'"
          >
            {{
              occurrence.row[field.name] > 0
                ? getDuration(occurrence.row)
                : '--'
            }}
          </div>
          <div
            class="table-subheading"
            v-else-if="field.displayType === 'IMPACT'"
          >
            {{ getImpact(occurrence.row, field.name) }}
          </div>
          <div
            class="table-subheading"
            v-else-if="field.displayType === 'USER_AVATAR'"
          >
            <user-avatar
              v-if="occurrence.row.acknowledged"
              size="md"
              :user="occurrence.row[field.name]"
              :showPopover="true"
              :name="true"
              moduleName="alarm"
            ></user-avatar>
            <div v-else>Unacknowledged</div>
          </div>
          <div
            class="q-item-label self-center f10 secondary-color"
            v-else-if="field.displayType === 'PREVIOUS_SEVERITY'"
            style="min-width: 90px;"
          >
            <template v-if="occurrence.row.previousSeverity">
              <div
                class="q-item-label uppercase severityTag"
                v-bind:style="{
                  'background-color': getPreviousOccurrenceColor(
                    occurrence.row
                  ),
                }"
              >
                {{ getPreviousOccurrenceDisplayName(occurrence.row) }}
              </div>
            </template>
            <template v-else>
              <div
                class="q-item-label uppercase severityTag"
                v-bind:style="{
                  'background-color': getAlarmColor(occurrence.row),
                }"
              >
                {{ getAlarmDisplayName(occurrence.row) }}
              </div>
            </template>
          </div>
          <div class="table-subheading" v-else>
            {{ occurrence.row[field.name] }}
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import NewDatePicker from '@/NewDatePicker'
import NewDateHelper from '@/mixins/NewDateHelper'
import UserAvatar from '@/avatar/User'
import NewAlarmMixin from '@/mixins/NewAlarmMixin'
import { isEmpty } from '@facilio/utils/validation'

export default {
  mixins: [NewDateHelper, NewAlarmMixin],
  components: {
    UserAvatar,
    NewDatePicker,
  },
  props: [
    'isActive',
    'portalName',
    'loading',
    'name',
    'details',
    'list',
    'fields',
    'dateObj',
  ],
  computed: {
    isDataAvailable() {
      let { list } = this
      if (isEmpty(list)) {
        return false
      }
      return list.length > 0
    },
  },
  data() {
    return {
      localDateFormat: [22, 25, 31, 30, 28, 27, 44, 45],
      dateOperator: 28,
      dateValue: null,
    }
  },
  created() {
    this.init()
  },
  mounted() {
    if (this.dateObj) {
      this.changeDateFilter(this.dateObj)
    }
  },
  methods: {
    async init() {},
    getPreviousOccurrenceColor(alarm) {
      let { previousSeverity } = alarm || {}
      return this.$getProperty(previousSeverity, 'color')
    },
    getPreviousOccurrenceDisplayName(alarm) {
      let { previousSeverity } = alarm || {}
      return this.$getProperty(previousSeverity, 'displayName', '---')
    },
    changeDateFilter(dateFilter) {
      this.dateObj = dateFilter
      // TODO...use alarmbarmixin for booleancard and alarminsight
      if (this.localDateFormat.includes(dateFilter.operatorId)) {
        this.dateOperator = dateFilter.operatorId
        this.dateValue = null
      } else {
        this.dateOperator = 20
        this.dateValue = dateFilter.value.join()
      }
      let { dateOperator, dateValue } = this
      this.$emit(
        'filter',
        NewDateHelper.getDatePickerObject(dateOperator, dateValue)
      )
    },
    getDateFomattedTz(date) {
      return this.$options.filters.formatDate(date)
    },
    getDuration(occurrence) {
      let data = ''
      let duration = this.$helpers.getDuration(
        (occurrence.clearedTime - occurrence.createdTime) / 1000,
        'seconds'
      )
      for (let key in duration) {
        data += ' ' + duration[key] + ' ' + key
      }
      return data
    },
    getImpact(occurrence, name) {
      let impact = this.$getProperty(occurrence, name, 0)
      return occurrence[name] > 0 ? impact.toFixed(2) : '--'
    },
  },
}
</script>
<style lang="scss">
.alarm-occurence-table {
  .el-table__row td {
    padding-left: 15px;
  }
}
</style>
