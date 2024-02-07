<template>
  <div class="m20" ref="content-container">
    <div class>
      <el-row class="flex-middle">
        <el-col :span="24" class="mL10 mR10">
          <div class="fc-invoice-fields pT10">
            <el-row>
              <el-col :span="6">
                <div class="field-label bold">
                  {{ fieldDisplayNames['picklist'] }}
                </div>
              </el-col>
              <el-col :span="6">
                <div class="field-value">{{ getFieldData('picklist') }}</div>
              </el-col>
              <el-col :span="6">
                <div class="field-label bold">Severity</div>
              </el-col>
              <el-col :span="6">
                <div class="field-value">
                  <template v-if="data.picklist_1">
                    <span v-if="data.picklist_1 === 1">Critical</span>
                    <span v-else-if="data.picklist_1 === 2">Major</span>
                    <span v-else-if="data.picklist_1 === 3">Minor</span>
                  </template>
                  <template v-else>
                    <span>Minor</span>
                  </template>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <el-row class="flex-middle">
        <el-col :span="24" class="mL10 mR10">
          <div class="pT10 fc-invoice-fields border-bottom-none">
            <el-row>
              <el-col :span="6">
                <div class="field-label bold">{{ 'Created Time' }}</div>
              </el-col>
              <el-col :span="6">
                <div class="field-value">
                  {{ getFieldData('sysCreatedTime') }}
                </div>
              </el-col>
              <el-col :span="6" v-if="data['singleline_3']">
                <div class="field-label bold">
                  {{ fieldDisplayNames['singleline_3'] }}
                </div>
              </el-col>
              <el-col :span="6" v-if="data['singleline_3']">
                <div class="field-value">
                  {{ getFieldData('singleline_3') }}
                </div>
              </el-col>
            </el-row>
          </div>
        </el-col>
      </el-row>
      <!-- <div
        v-if="needsShowMore"
        class="text-center pT10 pB15 mR20"
        style="background-color: #fff;"
      >
        <a
          @click="toggleVisibility()"
          class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
          >{{ showMoreLinkText }}</a
        >
      </div> -->
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import DetailsWidgetMixin from 'src/components/page/widget/common/DetailsWidgetMixin'
export default {
  props: [
    'details',
    'moduleName',
    'calculateDimensions',
    'resizeWidget',
    'layoutParams',
  ],
  mixins: [DetailsWidgetMixin],

  data() {
    return {
      needsShowMore: true,
      isAllVisible: false,
      defaultWidgetHeight: (this.layoutParams || {}).h,
    }
  },
  computed: {
    showMoreLinkText() {
      return this.isAllVisible
        ? this.$t('common._common.view_less')
        : this.$t('common._common.view_more')
    },
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
  methods: {
    getFieldData(fieldName) {
      if (fieldName) {
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist') {
          let { enumMap } = fieldobj
          return enumMap[this.data['picklist']]
        } else if (fieldName === 'picklist_5') {
          let { enumMap } = fieldobj
          return enumMap[this.data['picklist_5']]
        } else if (fieldName === 'date_1') {
          return moment(this.data.date_1)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'sysCreatedTime') {
          return moment(this.details.sysCreatedTime)
            .tz(this.$timezone)
            .format('DD MMM YYYY')
        } else if (fieldName === 'singleline_3') {
          return this.data.singleline_3 ? this.data.singleline_3 : '---'
        }
      }
      return ''
    },
  },
}
</script>
