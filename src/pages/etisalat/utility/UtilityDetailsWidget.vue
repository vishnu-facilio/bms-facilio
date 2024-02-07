<template>
  <div>
    <div class="asset-details-widget" ref="content-container">
      <div class="container">
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ fieldDisplayNames['supplier'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{
                details.data.supplier.name ? details.data.supplier.name : '---'
              }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              Site Name
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline_3') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              Site ID
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('singleline') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              Region and Cost Centre
            </el-col>
            <el-col :span="12" class="field-value">
              {{ details.data.regionandcostcentre.name }}
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
              Meter type
            </el-col>
            <el-col :span="12" class="field-value">
              <div v-if="details.data.picklist === 1">
                Smart Meter
              </div>
              <div v-else-if="details.data.picklist === 2">
                Manual
              </div>
              <div v-else>
                ---
              </div>
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ 'Associated utilities' }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ associatedUtility || '---' }}
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
              {{ fieldDisplayNames['date'] }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('date') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ 'Created Time' }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getDate(details.sysCreatedTime) }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ 'Modified Time' }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getDate(details.sysModifiedTime) }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ 'Created By' }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('sysCreatedBy') }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">
              {{ 'Modified By' }}
            </el-col>
            <el-col :span="12" class="field-value">
              {{ getFieldData('sysModifiedBy') }}
            </el-col>
          </el-row>
        </div>
      </div>
      <div
        v-if="needsShowMore"
        class="text-center pT10 pB15 mR20"
        style="background-color: #fff;"
      >
        <a
          @click="toggleVisibility(340)"
          class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
          >{{ showMoreLinkText }}</a
        >
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from 'vuex'
import moment from 'moment-timezone'
import { isEmpty } from '@facilio/utils/validation'
import DetailsWidgetMixin from 'src/components/page/widget/common/DetailsWidgetMixin'
export default {
  props: [
    'details',
    'moduleName',
    'resizeWidget',
    'calculateDimensions',
    'layoutParams',
  ],
  mixins: [DetailsWidgetMixin],
  data() {
    return {
      needsShowMore: true,
      initialWidgetHeight: null,
      utilityConnections: null,
      associatedUtility: '---',
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
  mounted() {
    // this.autoResize()
    if (this.details.name) {
      this.loadUtilityConnection()
    }
  },
  methods: {
    // autoResize() {
    //   this.$nextTick(() => {
    //     if (!isEmpty(this.$refs['preview-container'])) {
    //       let height = this.$refs['preview-container'].scrollHeight + 80
    //       let width = this.$refs['preview-container'].scrollWidth
    //       if (this.resizeWidget) {
    //         this.resizeWidget({ height, width })
    //       }
    //     }
    //   })
    // },
    getSiteName(siteId) {
      let site = (this.sites || []).find(({ id }) => id === siteId)
      return site ? site.name : '---'
    },
    loadUtilityConnection() {
      let accountNumber = this.details.name
      this.$http
        .get(
          `/v2/module/data/list?moduleName=custom_utilityconnection&page=1&perPage=500&filters=%7B%22accountdetails%22%3A%7B%22operatorId%22%3A36%2C%22value%22%3A%5B%22${this.details.id}%22%5D%2C%22selectedLabel%22%3A%22${accountNumber}%22%7D%7D&viewName=all&includeParentFilter=true`
        )
        .then(response => {
          if (
            response.data.responseCode === 0 &&
            response.data.result &&
            response.data.result.moduleDatas
          ) {
            this.utilityConnections = response.data.result.moduleDatas
            this.associatedUtility = ''
            let utility = {}
            this.utilityConnections.forEach(rt => {
              if (rt.data && rt.data.picklist) {
                if (rt.data.picklist === 1) {
                  this.$set(utility, 'Electricity', 1)
                } else if (rt.data.picklist === 2) {
                  this.$set(utility, 'Water', 1)
                } else if (rt.data.picklist === 3) {
                  this.$set(utility, 'Gas', 1)
                } else if (rt.data.picklist === 4) {
                  this.$set(utility, 'Sewage', 1)
                }
              }
            })
            Object.keys(utility).forEach(rt => {
              if (rt === 'Sewage') {
                this.associatedUtility += rt
              } else {
                this.associatedUtility += rt + ','
              }
            })
          }
        })
    },
    getDate(time, formatter) {
      if (formatter) {
        return moment(Number(time))
          .tz(this.$timezone)
          .format(formatter)
      } else {
        return moment(Number(time))
          .tz(this.$timezone)
          .format('DD-MMM-YYYY')
      }
    },
    formatData(data, field) {
      if (data) {
        let { dataType } = field
        if (dataType === 4 && field.trueVal && data === true) {
          return field.trueVal
        } else if (dataType === 4 && field.falseVal && data === false) {
          return field.falseVal
        } else if (dataType === 1 || dataType === 2 || dataType === 3) {
          return data
        } else if (dataType === 5 || dataType === 6) {
          return data ? this.getDate(date, 'DD-MMM-YYYY') : '---'
        } else if (dataType === 7 || dataType === 8) {
          let { enumMap } = field
          return enumMap[data]
        }
      }
      return '---'
    },
    getFieldData(fieldName) {
      if (fieldName) {
        let { data, details } = this
        let fieldobj = this.fields.find(rt => rt.name === fieldName)
        if (fieldName === 'picklist_1') {
          if (this.data.picklist === 1) {
            return 'Authority'
          } else if (this.data.picklist === 2) {
            return 'Landlord'
          }
          return '---'
        } else if (fieldName === 'singleline_2') {
          return this.data.singleline_2 ? this.data.singleline_2 : '---'
        } else if (fieldName === 'supplier') {
          return this.data.supplier ? this.data.supplier : '---'
        } else if (fieldName === 'picklist') {
          return this.data.picklist ? this.data.picklist : '---'
        } else if (fieldName === 'singleline_1') {
          return this.data.singleline_1 ? this.data.singleline_1 : '---'
        } else if (fieldName === 'singleline') {
          return this.data.singleline ? this.data.singleline : '---'
        } else if (fieldName === 'singleline_3') {
          return this.data.singleline_3 ? this.data.singleline_3 : '---'
        } else if (fieldName === 'date') {
          return this.data.date
            ? this.getDate(this.data.date, 'DD-MMM-YYYY')
            : '---'
        } else if (
          fieldName === 'sysModifiedBy' ||
          fieldName === 'sysCreatedBy' ||
          fieldName === 'lastIssuedToUser'
        ) {
          let value = details[fieldName]
            ? this.$store.getters.getUser(details[fieldName].id).name
            : ''
          return !isEmpty(value) ? value : null
        } else {
          return data[fieldName]
            ? this.formatData(data[fieldName], fieldobj)
            : '---'
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
