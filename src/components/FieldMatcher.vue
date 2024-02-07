<template>
  <el-form :inline="true" :label-position="'left'">
    <div v-for="(fieldMatcher, index) in fieldMatchers" :key="index">
      <el-form-item label="Field">
        <el-select
          v-model="fieldMatcher.field"
          filterable
          class="fc-input-full-border-select2"
        >
          <el-option
            v-for="(field, index) in fieldSeries"
            :key="index"
            :label="field.label"
            :value="field.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="Regular Expression">
        <el-input
          v-model="fieldMatcher.value"
          class="fc-input-full-border-select2"
        ></el-input>
      </el-form-item>
      <el-form-item>
        <el-button
          @click="deleteRow(index)"
          type="text"
          class="f18"
          icon="el-icon-close"
        ></el-button>
      </el-form-item>
    </div>
    <el-button
      :size="size"
      @click="addRow"
      type="info"
      plain
      icon="el-icon-plus"
    ></el-button>
  </el-form>
</template>
<script>
export default {
  props: ['size', 'value'],
  data() {
    return {
      fieldMatchers: [
        {
          field: '',
          regex: '',
        },
      ],
      fields: null,
      regex: null,
      fieldSeries: [
        {
          label: 'Source',
          value: 'source',
        },
        {
          label: 'Node',
          value: 'node',
        },
        {
          label: 'Resource',
          value: 'resource',
        },
        {
          label: 'Event Message',
          value: 'eventMessage',
        },
        {
          label: 'Severity',
          value: 'severity',
        },
        {
          label: 'Priority',
          value: 'priority',
        },
        {
          label: 'Alarm Class',
          value: 'alarmClass',
        },
        {
          label: 'State',
          value: 'state',
        },
      ],
    }
  },
  mounted() {
    if (this.value) {
      this.fieldMatchers = this.value
    }
  },
  watch: {
    fieldMatchers: function(newVal) {
      this.$emit('input', this.fieldMatchers)
    },
  },
  methods: {
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
<style></style>
