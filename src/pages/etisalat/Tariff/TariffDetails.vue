<template>
  <div>
    <div class="asset-details-widget">
      <div class="container">
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ $t('etisalat.etisalat.supplier_name') }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('lookup') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ $t('etisalat.etisalat.tariff_type') }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('picklist') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ $t('etisalat.etisalat.utility_type') }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('picklist_2') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ $t('etisalat.etisalat.cost_type') }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('picklist_1') }}
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import { getFieldOptions } from 'util/picklist'

export default {
  props: ['details', 'moduleName'],
  data() {
    return {
      lookupMap: {},
    }
  },
  async mounted() {
    let { error, options } = await getFieldOptions({
      field: { lookupModuleName: 'vendors', skipDeserialize: true },
    })

    if (error) {
      this.$message.error(error.message || 'Error Occured')
    } else {
      this.lookupMap = options
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    data() {
      return this.details.data || null
    },
  },
  methods: {
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (
          fieldobj &&
          ['picklist', 'picklist_1', 'picklist_2'].includes(fieldName)
        ) {
          let { enumMap } = fieldobj
          return enumMap[this.data[fieldName]]
        } else if (
          fieldName === 'lookup' &&
          this.lookupMap[this.data.lookup.id]
        ) {
          return this.lookupMap[this.data.lookup.id]
        }
      }
      return ''
    },
  },
}
</script>
<style lang="scss" scoped>
.asset-details-widget {
  padding: 0 22px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.container {
  padding-left: 0;
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

.lines {
  height: 15px;
  width: 200px;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
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
  padding-left: 8px;
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
