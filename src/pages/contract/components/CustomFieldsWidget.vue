<template
  ><div
    class="fc__white__bg__info__custom mT20"
    v-if="customFields && customFields.length > 0"
  >
    <el-row
      class="border-bottom6 pB20 pT20"
      v-if="index % 2 === 0"
      v-for="(d, index) in customFields"
      :key="index"
    >
      <el-col :span="12">
        <el-col :span="12">
          <div class="fc-blue-label">
            {{ customFields[index].displayName }}
          </div>
        </el-col>
        <el-col
          :span="12"
          v-if="customFields[index].field.dataTypeEnum === 'DATE'"
          >{{
            summaryData.data && summaryData.data[customFields[index].name] > 0
              ? $options.filters.formatDate(
                  summaryData.data[customFields[index].name],
                  true
                )
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index].field.dataTypeEnum === 'DATE_TIME'"
          >{{
            (summaryData.data && summaryData.data[customFields[index].name]) > 0
              ? $options.filters.formatDate(
                  summaryData.data[customFields[index].name]
                )
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="[2, 3].includes(customFields[index].field.dataType)"
          >{{
            summaryData.data &&
            summaryData.data[customFields[index].name] &&
            summaryData.data[customFields[index].name] !== -1
              ? summaryData.data[customFields[index].name]
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index].field.dataTypeEnum === 'ENUM'"
          >{{
            summaryData.data && summaryData.data[customFields[index].name]
              ? customFields[index].field.enumMap[
                  parseInt(summaryData.data[customFields[index].name])
                ]
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index].field.dataTypeEnum === 'BOOLEAN'"
          >{{
            summaryData.data &&
            summaryData.data[customFields[index].name] === true
              ? customFields[index].field.trueVal || 'True'
              : summaryData.data &&
                summaryData.data[customFields[index].name] === false
              ? customFields[index].field.falseVal || 'False'
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index].field.dataTypeEnum === 'MULTI_LOOKUP'"
          >{{ getMultiLookupValue(summaryData, d) }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index].field.dataTypeEnum === 'LOOKUP'"
          >{{
            !$validation.isEmpty(
              (summaryData.data || {})[customFields[index].name]
            )
              ? summaryData.data[customFields[index].name].primaryValue
              : '---'
          }}</el-col
        >
        <el-col :span="12" v-else>{{
          summaryData.data && summaryData.data[customFields[index].name]
            ? summaryData.data[customFields[index].name]
            : '---'
        }}</el-col>
      </el-col>
      <el-col v-if="customFields.length > index + 1" :span="12">
        <el-col :span="12">
          <div class="fc-blue-label">
            {{ customFields[index + 1].displayName }}
          </div>
        </el-col>
        <el-col
          :span="12"
          v-if="customFields[index + 1].field.dataTypeEnum === 'DATE'"
          >{{
            summaryData.data &&
            summaryData.data[customFields[index + 1].name] > 0
              ? $options.filters.formatDate(
                  summaryData.data[customFields[index + 1].name],
                  true
                )
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index + 1].field.dataTypeEnum === 'DATE_TIME'"
          >{{
            (summaryData.data &&
              summaryData.data[customFields[index + 1].name]) > 0
              ? $options.filters.formatDate(
                  summaryData.data[customFields[index + 1].name]
                )
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="[2, 3].includes(customFields[index + 1].field.dataType)"
          >{{
            summaryData.data &&
            summaryData.data[customFields[index + 1].name] &&
            summaryData.data[customFields[index + 1].name] !== -1
              ? summaryData.data[customFields[index + 1].name]
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index + 1].field.dataTypeEnum === 'ENUM'"
          >{{
            summaryData.data && summaryData.data[customFields[index + 1].name]
              ? customFields[index + 1].field.enumMap[
                  parseInt(summaryData.data[customFields[index + 1].name])
                ]
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index + 1].field.dataTypeEnum === 'BOOLEAN'"
          >{{
            summaryData.data &&
            summaryData.data[customFields[index + 1].name] === true
              ? customFields[index + 1].field.trueVal || 'True'
              : summaryData.data &&
                summaryData.data[customFields[index + 1].name] === false
              ? customFields[index + 1].field.falseVal || 'False'
              : '---'
          }}</el-col
        >
        <el-col
          :span="12"
          v-else-if="customFields[index + 1].field.dataTypeEnum === 'LOOKUP'"
          >{{
            !$validation.isEmpty(
              (summaryData.data || {})[customFields[index + 1].name]
            )
              ? summaryData.data[customFields[index + 1].name].primaryValue
              : '---'
          }}</el-col
        >
        <el-col :span="12" v-else>{{
          summaryData.data && summaryData.data[customFields[index + 1].name]
            ? summaryData.data[customFields[index + 1].name]
            : '---'
        }}</el-col>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { isArray, isEmpty } from '@facilio/utils/validation'
export default {
  props: ['customFields', 'summaryData'],
  methods: {
    getMultiLookupValue(record, field) {
      let { data } = record || {}
      let value
      if (!isEmpty(data)) value = data[field.name]

      if (!isEmpty(value) && isArray(value)) {
        let lookupRecordNames = (value || []).map(
          currRecord =>
            currRecord.displayName || currRecord.name || currRecord.subject
        )
        if (lookupRecordNames.length > 5) {
          return `${lookupRecordNames.slice(0, 5).join(', ')} +${Math.abs(
            lookupRecordNames.length - 5
          )}`
        } else {
          return !isEmpty(lookupRecordNames)
            ? `${lookupRecordNames.join(', ')}`
            : '---'
        }
      } else {
        return '---'
      }
    },
  },
}
</script>
