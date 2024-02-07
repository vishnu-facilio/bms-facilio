<template>
  <div :class="['asset-details-widget', widget.title && 'pT0']">
    <template v-if="widget.title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ widget.title }}</div>
      </div>
    </template>

    <div
      :class="['container', widget.title && 'pL0 pB15']"
      ref="content-container"
    >
      <div
        v-for="(field, index) in fields"
        :key="`${field.name}-${index}`"
        class="field"
      >
        <el-row>
          <el-col :span="12" class="field-label">
            {{ field.name }}
          </el-col>

          <el-col :span="12" class="field-value line-height22">
            {{ field.value }}
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'widget'],
  computed: {
    primaryFields() {
      return {
        host: 'Host',
        checkInTime: 'CheckIn Time',
        checkOutTime: 'CheckOut Time',
        visitorType: 'Type',
      }
    },
    fields() {
      let fields = []
      let { primaryFields, details } = this

      Object.entries(primaryFields).forEach(([key, value]) => {
        let fieldValue

        if (['visitorType', 'host'].includes(key)) {
          fieldValue = this.$getProperty(details, `${key}.name`)
        } else if (
          [
            'checkInTime',
            'checkOutTime',
            'expectedCheckInTime',
            'expectedCheckOutTime',
          ].includes(key)
        ) {
          let value = this.$getProperty(details, key)
          fieldValue = !isEmpty(value)
            ? this.$options.filters.formatDate(value)
            : null
        }
        fields.push({ name: value, value: fieldValue || '---' })
      })
      return fields
    },
  },
}
</script>
<style scoped>
.asset-details-widget {
  padding: 10px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.container {
  padding-left: 10px;
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  overflow: hidden;
}
.field {
  flex: 0 50%;
  padding: 20px 0;
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
.field-label,
.field-value {
  word-break: break-word;
  padding-right: 10px;
}
.field-label {
  color: #324056;
  font-weight: 500;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
</style>
