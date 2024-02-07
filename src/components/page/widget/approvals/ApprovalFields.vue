<template>
  <div class="asset-details-widget">
    <template v-if="!hideTitleSection">
      <div class="widget-topbar">
        <div class="widget-title mL0">Approval Details</div>
        <div class="flex-end">
          <portal-target :name="widget.key + '-topbar'"></portal-target>
        </div>
      </div>
    </template>
    <div v-if="loading" class="container">
      <div
        v-for="index in [1, 2]"
        :key="index"
        class="field"
        style="flex: 0 100%"
      >
        <el-row>
          <el-col :span="5">
            <span class="lines loading-shimmer"></span>
          </el-col>
          <el-col :span="index === 1 ? 17 : 9">
            <span class="lines loading-shimmer"></span>
          </el-col>
        </el-row>
      </div>
    </div>

    <div v-else class="container" ref="content-container">
      <div class="field" style="flex: 0 100%">
        <el-row>
          <el-col :span="5" class="field-label">Approval Process</el-col>
          <el-col :span="17" class="field-value">{{ ruleName }}</el-col>
        </el-row>
      </div>
      <div
        v-for="(field, index) in fields"
        :key="field.name + index"
        class="field"
        style="flex: 0 100%"
      >
        <el-row>
          <el-col :span="5" class="field-label">{{ field.displayName }}</el-col>
          <el-col
            :span="17"
            :key="field.displayName + 'value'"
            class="field-value"
          >
            <div
              v-if="
                $getProperty(details, 'rotatingItem.id') &&
                  field.name === 'rotatingItem'
              "
              @click="
                $router.push({
                  path:
                    '/app/inventory/item/' +
                    'all/' +
                    details.rotatingItem.id +
                    '/summary',
                })
              "
            >
              <item-tool-avatar
                name="true"
                size="lg"
                module="item"
                :recordData="details['rotatingItem'].itemType"
              ></item-tool-avatar>
            </div>
            <div
              v-else-if="
                $getProperty(details, 'rotatingItem.id') &&
                  field.name === 'rotatingTool'
              "
              @click="
                $router.push({
                  path:
                    '/app/inventory/tool/' +
                    'all/' +
                    details.rotatingTool.id +
                    '/summary',
                })
              "
            >
              <item-tool-avatar
                name="true"
                size="lg"
                module="tool"
                :recordData="details['rotatingTool'].toolType"
              ></item-tool-avatar>
            </div>
            <div
              v-else-if="isLookUpRedirectApplicable(field)"
              @click="redirectToLookUpFieldSummary(field)"
              class="bluetxt pointer"
            >
              {{ field.displayValue }}
            </div>
            <span v-else>{{ field.displayValue }}</span>
          </el-col>
        </el-row>
      </div>
      <div v-if="getSummaryRoute(details)" class="mT30 mB10 border-none">
        <el-row>
          <el-col :span="24" class="field-label">
            <a @click="goToModuleSummary(details)" class="f13 pB15">
              Go To Summary
              <inline-svg
                src="svgs/new-tab"
                iconClass="icon vertical-middle icon-xs mL3"
              ></inline-svg>
            </a>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>
<script>
import { isObject, isEmpty, isBoolean } from '@facilio/utils/validation'
import DetailsWidgetMixin from '../common/DetailsWidgetMixin'
import { mapState, mapGetters } from 'vuex'
import ItemToolAvatar from '@/avatar/ItemTool'
import { getFieldValue } from 'util/picklist'
import { getDisplayValue } from 'util/field-utils'
import {
  isBooleanField,
  isSystemEnumField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isLookupSimple,
  isSiteField,
  isSpecialEnumField,
  isMultiEnumField,
} from '@facilio/utils/field'
import {
  findRouteForModule,
  isWebTabsEnabled,
  pageTypes,
} from '@facilio/router'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  props: [
    'widget',
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'isMetaLoading',
    'approvalMeta',
    'metaFields',
    'moduleMeta',
    'hideTitleSection',
  ],
  components: { ItemToolAvatar },
  mixins: [DetailsWidgetMixin],
  data() {
    return {
      fields: {},
      isLoading: true,
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    ...mapGetters(['getUser']),
    ruleName() {
      return this.$getProperty(this.approvalMeta, 'approvalRule.name') || ''
    },
    loading() {
      return this.isMetaLoading || this.isLoading
    },
  },
  watch: {
    metaFields: {
      handler(fields) {
        this.deserialize(fields)
      },
      immediate: true,
    },
  },
  methods: {
    deserialize(fields) {
      this.isLoading = false
      this.fields = fields

      let promises = []
      fields.forEach(field => {
        let dataObj = field.default !== true ? this.details.data : this.details

        let promise = this.getFormattedMetaValue(
          { field },
          dataObj
        ).then(value => this.$set(field, 'displayValue', value))
        promises.push(promise)
      })
      Promise.all(promises)
        .catch(() => {})
        .finally(() => {
          this.$nextTick(() => {
            this.isLoading = false
            this.autoResize()
          })
        })
    },
    getSummaryRoute(record) {
      let { moduleName, $router, moduleMeta, $getProperty } = this
      let isCustomModule = $getProperty(moduleMeta, 'module.custom', false)

      if (isWebTabsEnabled()) {
        let params = { viewname: 'all', id: record.id }
        let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)

        if (name) {
          let { href } = $router.resolve({ name, params }) || {}
          return href
        }
      } else {
        const summaryRoutes = {
          workorder: {
            name: 'wosummarynew',
            params: { id: record.id },
          },
          workpermit: {
            name: 'workPermitSummaryV3',
            params: { viewname: 'all', id: record.id },
          },
          inventoryrequest: {
            name: 'inventoryrequestSummary',
            params: { viewname: 'all', id: record.id },
          },
          custom: {
            name: 'custommodules-summary',
            params: { moduleName, viewname: 'all', id: record.id },
          },
        }

        let route = isCustomModule
          ? summaryRoutes['custom']
          : summaryRoutes[moduleName]

        if (route) {
          let { href } = $router.resolve(route)
          return href
        }
      }
    },
    goToModuleSummary(record) {
      let url = this.getSummaryRoute(record)
      window.open(url, '_blank')
    },
    getFormattedMetaValue(fieldObj, lookupObj) {
      return new Promise(resolve => {
        if (!lookupObj) lookupObj = {}

        let { field } = fieldObj
        let value
        if (lookupObj && field) {
          value = lookupObj[field.name]
        } else if (lookupObj) {
          value = lookupObj[fieldObj.name]
        }

        if ((field && isSiteField(field)) || isSiteField(fieldObj)) {
          let site = this.$store.getters.getSite(value)
          value = site && site.name ? site.name : '---'
          resolve(value)
        } else if (
          !isEmpty(field) &&
          (isDateField(field) || isDateTimeField(field))
        ) {
          resolve(value > 0 ? getDisplayValue(field, value) : '---')
        } else if (!isEmpty(field) && isSpecialEnumField(field)) {
          resolve(value ? getDisplayValue(field, value) : '---')
        } else if (
          !isEmpty(field) &&
          (isEnumField(field) || isSystemEnumField(field))
        ) {
          resolve(value ? getDisplayValue(field, value) : '---')
        } else if (!isEmpty(field) && isBooleanField(field)) {
          resolve(isBoolean(value) ? getDisplayValue(field, value) : '---')
        } else if (
          (field &&
            this.$getProperty(field.displayType, '_name', '') === 'DURATION') ||
          fieldObj.displayTypeEnum === 'DURATION'
        ) {
          resolve(this.$helpers.getFormattedDuration(value, 'seconds'))
        } else if (!isEmpty(field) && [2, 3].includes(field.dataType)) {
          let { field } = fieldObj
          let { unit } = field || {}
          if (!isEmpty(value) && !isEmpty(unit)) {
            value = `${value} ${unit}`
          } else if (!isEmpty(value)) {
            value = `${value}`
          } else {
            value = `---`
          }
          resolve(value)
        } else if (
          value &&
          ((field &&
            this.$getProperty(field.lookupModule, 'name', '') ===
              'basespace') ||
            fieldObj.lookupModuleName === 'basespace')
        ) {
          resolve(this.space[value.id] ? this.space[value.id] : '---')
        } else if (
          (isLookupSimple(fieldObj) &&
            this.$getProperty(lookupObj, fieldObj.name, null) &&
            isObject(lookupObj[fieldObj.name]) &&
            this.$getProperty(
              lookupObj,
              `${fieldObj.name}.primaryValue`,
              null
            )) ||
          (field &&
            this.$getProperty(field.displayType, '_name', '') ===
              'LOOKUP_SIMPLE' &&
            this.$getProperty(lookupObj, field.name, null) &&
            isObject(lookupObj[field.name]) &&
            this.$getProperty(lookupObj, `${field.name}.primaryValue`, null))
        ) {
          // Custom modules lookup fields
          let name = (field && field.name) || fieldObj.name

          resolve(
            lookupObj[name].primaryValue ? lookupObj[name].primaryValue : '---'
          )
        } else if (
          value &&
          ((isLookupSimple(fieldObj) && fieldObj.lookupModuleName) ||
            (field &&
              this.$getProperty(field.displayType, '_name', '') ===
                'LOOKUP_SIMPLE' &&
              this.$getProperty(field.lookupModule, 'name', '')))
        ) {
          let isRotating = ['rotatingItem', 'rotatingTool'].includes(field.name)
          if (!isRotating) {
            let lookupModuleName =
              (field && this.$getProperty(field.lookupModule, 'name', '')) ||
              fieldObj.lookupModuleName
            getFieldValue({
              lookupModuleName,
              selectedOptionId: [value.id],
            }).then(({ error, data }) => {
              if (!error) {
                let value = this.$getProperty(data, '0.label')
                resolve(value ? value : '---')
              } else {
                resolve('---')
              }
            })
          } else resolve(value || '---')
        } else if (
          this.$getProperty(field.dataTypeEnum, '_name', '') === 'LOOKUP'
        ) {
          resolve((value || {}).name ? value.name : '---')
        } else if (
          this.$getProperty(field.dataTypeEnum, '_name', '') === 'MULTI_LOOKUP'
        ) {
          resolve(this.getMultiLookupValue(value))
        } else if (!isEmpty(field) && isMultiEnumField(field)) {
          resolve(this.getMultiEnumFieldValues(field, value))
        } else {
          resolve(value || '---')
        }
      })
    },
    getMultiLookupValue(value) {
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
    },
    getMultiEnumFieldValues(field, values = []) {
      let { enumMap } = field

      let valueStr = values.reduce((accStr, value) => {
        let str = enumMap[value] || ''
        return isEmpty(accStr) ? `${str}` : `${accStr}, ${str}`
      }, '')

      return isEmpty(valueStr) ? '---' : valueStr
    },
    autoResize() {
      this.$nextTick(() => {
        let container = this.$refs['content-container']
        if (container) {
          let height = container.scrollHeight + 70
          let width = container.scrollWidth
          this.resizeWidget({ height, width })
        }
      })
    },
  },
}
</script>
<style lang="scss" scoped>
.asset-details-widget {
  padding: 0px 30px 0px;
  text-align: left;
  height: 100%;
  display: flex;
  flex-direction: column;
}
.container {
  font-size: 13px;
  display: flex;
  flex-wrap: wrap;
  overflow: hidden;
}
.field {
  flex: 0 50%;
  padding: 20px 0;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  &:not(.border-none) {
    border-bottom: 1px solid #edf4fa;
  }
}
.field-label,
.field-value {
  word-break: break-word;
  padding-right: 10px;
}
.field-label {
  color: #324056;
  font-size: 13px;
  letter-spacing: 0.5px;
  font-weight: normal;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: 500;
  letter-spacing: 0.5px;
  color: #324056;
}
.lines {
  height: 15px;
  width: 90%;
  margin: 0px 20px 0px 20px;
  border-radius: 5px;
}
</style>
