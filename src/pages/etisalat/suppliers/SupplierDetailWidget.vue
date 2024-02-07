<template>
  <div>
    <div class="asset-details-widget" ref="preview-container">
      <div class="container">
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['picklist'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('picklist') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline_1'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_1') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline_2'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_2') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline_3'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_3') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline_4'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_4') }}
            </el-col>
          </el-row>
        </div>
        <div class="field border-none">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['number_3'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('number_3') }}
            </el-col>
          </el-row>
        </div>
        <div class="field border-none">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['singleline_5'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_5') }}
            </el-col>
          </el-row>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
export default {
  props: ['details', 'moduleName', 'resizeWidget', 'calculateDimensions'],
  data() {
    return {
      initialWidgetHeight: null,
    }
  },
  computed: {
    ...mapState({
      moduleMeta: state => state.view.metaInfo,
    }),
    fields() {
      return this.moduleMeta.fields || []
    },
    fieldDisplayNames() {
      if (this.moduleMeta && this.moduleMeta.fields) {
        let fieldMap = {}
        this.moduleMeta.fields.forEach(field => {
          this.$set(fieldMap, field.name, field.displayName)
        })
        return fieldMap
      }
      return []
    },
    data() {
      return this.details.data || null
    },
    currentView() {
      let { $route } = this

      if ($route.params.viewName) {
        return $route.params.viewName
      } else {
        return null
      }
    },
  },
  mounted() {
    this.autoResize()
  },
  methods: {
    autoResize() {
      this.$nextTick(() => {
        if (!isEmpty(this.$refs['preview-container'])) {
          let height = this.$refs['preview-container'].scrollHeight + 100
          let width = this.$refs['preview-container'].scrollWidth
          if (this.resizeWidget) {
            this.resizeWidget({ height, width })
          }
        }
      })
    },
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'singleline') {
          return this.data.singleline ? this.data.singleline : '---'
        } else if (fieldName === 'singleline_1') {
          return this.data.singleline_1 ? this.data.singleline_1 : '---'
        } else if (fieldName === 'singleline_2') {
          return this.data.singleline_2 ? this.data.singleline_2 : '---'
        } else if (fieldName === 'singleline_3') {
          return this.data.singleline_3 ? this.data.singleline_3 : '---'
        } else if (fieldName === 'singleline_4') {
          return this.data.singleline_4 ? this.data.singleline_4 : '---'
        } else if (fieldName === 'singleline_5') {
          return this.data.singleline_5 ? this.data.singleline_5 : '---'
        } else if (fieldName === 'number_3') {
          return this.data.number_3 ? this.data.number_3 : '---'
        } else if (fieldName === 'picklist') {
          if (this.data.picklist === 1) {
            return 'Authority'
          } else if (this.data.picklist === 2) {
            return 'Landlord'
          }
          return '---'
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
