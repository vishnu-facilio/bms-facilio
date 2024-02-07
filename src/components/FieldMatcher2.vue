<template>
  <el-form :inline="true" :label-position="'left'">
    <div
      v-for="(fieldMatcher, index) in fieldMatchers"
      :key="index"
      class="field-matcher-bg"
    >
      <el-row
        align="middle"
        style="margin:0px;padding-top:10px;padding-bottom: 15px;"
        :gutter="50"
      >
        <el-col
          :span="10"
          style="padding-left: 0px;padding-right: 0;margin-right: 20px;"
        >
          <div class="add">
            <el-select
              v-model="fieldMatcher.field"
              filterable
              allow-create
              default-first-option
              placeholder="Enter field name"
              class="fc-border-select fc-input-full-border-select2 width100"
            >
              <el-option
                v-for="item in fieldList"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
            <!--<el-input v-model="fieldMatcher.field" style="width:100%" type="text" placeholder="Enter field name"></el-input>-->
          </div>
        </el-col>
        <el-col
          :span="10"
          v-if="fieldMatcher.field === 'alarmType'"
          style="padding-left: 0px; padding-right:0;"
        >
          <el-select
            v-model="fieldMatcher.value"
            class="fc-border-select fc-input-full-border-select2"
          >
            <el-option
              v-for="(value, key) in picklistOptions[fieldMatcher.field]"
              :key="key"
              :label="value"
              :value="key"
            ></el-option>
          </el-select>
        </el-col>
        <el-col v-else :span="10" style="padding-left: 0px;padding-right: 0;">
          <div class="add">
            <el-input
              v-model="fieldMatcher.value"
              type="text"
              placeholder="Enter value"
              class="fc-border-select fc-input-full-border-select2"
            ></el-input>
          </div>
        </el-col>
        <el-col
          :span="3"
          style="padding-top: 9px;padding-left: 10px;padding-right: 0;"
        >
          <img
            src="~assets/add-icon.svg"
            style="height:18px;width:18px;margin-right: 4px;"
            class="delete-icon"
            @click="addRow"
          />
          <img
            src="~assets/remove-icon.svg"
            v-if="Object.keys(fieldMatchers).length !== 1"
            style="height:18px;width:18px;margin-right: 3px;"
            class="delete-icon"
            @click="deleteRow(index)"
          />
          <!-- <el-button @click="deleteRow(index)" type="text" class="f18" icon="el-icon-close" style="padding-top: 8px;padding-bottom: 0px;"></el-button> -->
        </el-col>
      </el-row>
    </div>
    <!-- <img src="~assets/add-icon.svg" style="height:18px;width:18px;" class="delete-icon" @click="addRow"/> -->
    <!-- <el-button :size="size" @click="addRow" type="info" plain icon="el-icon-plus"></el-button> -->
  </el-form>
</template>
<script>
export default {
  props: ['size', 'value', 'fields'],
  data() {
    return {
      picklistOptions: {},
      fieldMatchers: [
        {
          field: '',
          regex: '',
        },
      ],
      regex: null,
      fieldSeries: [
        {
          label: 'Type',
          value: 'type',
        },
        {
          label: 'Subject',
          value: 'subject',
        },
        {
          label: 'Status',
          value: 'status',
        },
        {
          label: 'Space',
          value: 'space',
        },
        {
          label: 'Source Type',
          value: 'sourcetype',
        },
        {
          label: 'Serial Number',
          value: 'serialnumber',
        },
        {
          label: 'Scheduled Start',
          value: 'scheduledstart',
        },
        {
          label: 'Priority',
          value: 'priority',
        },
        {
          label: 'Tasks',
          value: 'tasks',
        },
        {
          label: 'Comments',
          value: 'comments',
        },
        {
          label: 'Closed Tasks',
          value: 'closedtasks',
        },
        {
          label: 'Attachments',
          value: 'attachments',
        },
        {
          label: 'Is Acknowledged',
          value: 'isacknowledged',
        },
        {
          label: 'Estimated End',
          value: 'estimatedend',
        },
        {
          label: 'Due Date',
          value: 'duedate',
        },
        {
          label: 'Device',
          value: 'device',
        },
        {
          label: 'Description',
          value: 'description',
        },
        {
          label: 'Created Time',
          value: 'createdtime',
        },
        {
          label: 'Cleared Time',
          value: 'clearedtime',
        },
        {
          label: 'Category',
          value: 'category',
        },
        {
          label: 'Assignment Group',
          value: 'assignmentgroup',
        },
        {
          label: 'Assigned To',
          value: 'assignedto',
        },
        {
          label: 'Asset',
          value: 'asset',
        },
        {
          label: 'Alarm Status',
          value: 'alarmstatus',
        },
        {
          label: 'Actual Work Start',
          value: 'actualworkstart',
        },
        {
          label: 'Actual Work End',
          value: 'actualworkend',
        },
        {
          label: 'Acknowledged Time',
          value: 'acknowledgedtime',
        },
        {
          label: 'Acknowledged By',
          value: 'acknowledgedby',
        },
        {
          label: 'Severity',
          value: 'severity',
        },
        { value: 'source', label: 'Source' },
        { value: 'condition', label: 'Condition' },
        { value: 'eventMessage', label: 'Event Message' },
        { value: 'alarmClass', label: 'Alarm Class' },
        {
          label: 'Category',
          value: 'alarmType',
        },
      ],
    }
  },
  computed: {
    fieldList() {
      let fieldsToReturn = this.fields || ['alarmClass', 'priority']
      return this.fieldSeries.filter(field =>
        fieldsToReturn.includes(field.value)
      )
    },
  },
  mounted() {
    this.picklistOptions.alarmType = this.$constants.AlarmCategory
    this.init()
  },
  watch: {
    fieldMatchers: function(newVal) {
      this.$emit('input', this.fieldMatchers)
    },
  },
  methods: {
    init() {
      if (this.value) {
        this.fieldMatchers = this.value
        if (!this.value.length) {
          this.addRow()
        }
      }
    },
    addRow() {
      this.fieldMatchers.push({
        field: '',
        value: '',
      })
    },
    deleteRow(index) {
      this.fieldMatchers.splice(index, 1)
    },
  },
}
</script>
<style>
.field-matcher-bg {
  padding: 10px 20px 3px 10px;
  cursor: pointer;
  border-radius: 3px;
}
.field-matcher-bg:hover {
  background-color: #f1f8fa;
  transition: 0.3s all ease-in-out;
  -webkit-transition: 0.3s all ease-in-out;
}
</style>
