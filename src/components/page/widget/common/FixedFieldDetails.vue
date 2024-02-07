<template>
  <div class="asset-details-widget">
    <div
      v-if="
        $validation.isEmpty(textAreaFields) &&
          $validation.isEmpty(generalFields)
      "
      class="mT20"
      style="color: #8ca1ad;"
    >
      <div class="d-flex flex-direction-column">
        <inline-svg
          src="svgs/emptystate/notes"
          class="vertical-middle self-center"
          iconClass="icon icon-xxxlg mR5"
        ></inline-svg>
        <div class="f14 pL10 self-center">
          {{ $t('qanda.template.no_fields') }}
        </div>
      </div>
    </div>
    <div v-else class="container" ref="content-container">
      <!-- Full span -->
      <div
        v-for="(field, index) in textAreaFields"
        :key="field.name + index"
        class="field"
        style="flex: 0 100%"
      >
        <el-row>
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>
          <el-col :span="16" class="field-value line-height22">
            <template v-if="field.displayTypeEnum === 'RICH_TEXT'">
              <div
                class="rich-text-preview-details"
                v-if="!$validation.isEmpty(field.displayValue)"
                v-html="sanitize(field.displayValue)"
              ></div>
              <span v-else>---</span>
            </template>
            <template v-else>{{ field.displayValue }} </template>
          </el-col>
        </el-row>
      </div>

      <!-- Half span -->
      <div
        v-for="(field, index) in generalFields"
        :key="field.name + index"
        class="field"
      >
        <el-row>
          <el-col :span="12" :key="field.displayName" class="field-label">{{
            field.displayName
          }}</el-col>
          <el-col
            :span="12"
            :key="field.displayName + 'value'"
            class="field-value"
          >
            <span
              v-if="field.name === 'frequencyEnum' && field.displayValue"
              class="text-capitalize"
              >{{ field.displayValue.toLowerCase() }}</span
            >
            <span
              v-else-if="isLocationFieldOrNot(field)"
              class="bluetxt pointer"
              @click="$helpers.openInMapForLatLngStr(field.displayValue)"
              >{{ `@${field.displayValue}` }}</span
            >
            <div v-else-if="isFileField(field)">
              <el-row>
                <el-col :span="12" class="attachment-label pointer">
                  <iframe
                    v-if="exportDownloadUrl"
                    :src="exportDownloadUrl"
                    style="display: none;"
                  ></iframe>

                  <a @click="downloadAttachment(field)">
                    {{ getFileName(field) }}
                  </a>
                </el-col>
              </el-row>
            </div>
            <div
              v-else-if="$getProperty(field, 'displayTypeEnum') === 'SIGNATURE'"
            >
              <SignatureField
                :field="field"
                :record="details"
                :isV3Api="isV3Api"
              />
            </div>
            <template
              v-else-if="$getProperty(field, 'displayTypeEnum') === 'URL_FIELD'"
            >
              <a
                v-if="
                  !$validation.isEmpty(field.displayValue) &&
                    field.displayValue !== '---'
                "
                rel="nofollow"
                class="url-field-display"
                :href="$getProperty(field, 'displayValue.href', '')"
                :target="$getProperty(field, 'displayValue.target', '')"
                referrerpolicy="no-referrer"
                >{{ getUrlDisplayName(field) }}</a
              >
              <span v-else>---</span>
            </template>
            <div
              class="bluetxt pointer"
              @click="redirectToLookUpFieldSummary(field)"
              v-else-if="isLookUpRedirectApplicable(field)"
            >
              {{ field.displayValue }}
            </div>

            <span v-else>{{ field.displayValue }}</span>
          </el-col>
        </el-row>
      </div>
    </div>
    <div
      v-if="needsShowMore && moduleName !== 'formulaField'"
      class="text-center"
      style="padding: 6px 0 15px; background-color: #fff;"
    >
      <a
        @click="toggleVisibility()"
        class="fc-link fc-link-animation text-capitalize letter-spacing0_3 f13"
        >{{ showMoreLinkText }}</a
      >
    </div>
  </div>
</template>
<script>
import { mapState, mapGetters } from 'vuex'
import DetailsWidgetMixin from './DetailsWidgetMixin'
import { getDefaultFields } from '../utils/constants'
import { getFieldValue } from 'util/picklist'
import { getDisplayValue, isLookupPopup } from 'util/field-utils'
import {
  isBooleanField,
  isEnumField,
  isDateField,
  isDateTimeField,
  isLookupSimple,
  isLocationField,
} from '@facilio/utils/field'
import { isEmpty, isBoolean, isObject } from '@facilio/utils/validation'
import SignatureField from '@/list/SignatureColumn'
import { sanitize } from '@facilio/utils/sanitize'
import { mapStateWithLogging } from 'store/utils/log-map-state'

export default {
  components: { SignatureField },
  mixins: [DetailsWidgetMixin],
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'primaryFields',
    'calculateDimensions',
    'isV3Api',
  ],
  created() {
    this.deserialize()
    this.sanitize = sanitize
  },
  mounted() {
    this.autoResize()
  },
  data() {
    return {
      filterFields: [],
      generalFields: [],
      textAreaFields: [],
      isAllVisible: false,
      defaultWidgetHeight: this.layoutParams.h,
      needsShowMore: false,
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
    showMoreLinkText() {
      return this.isAllVisible ? 'View Less' : 'View More'
    },
  },
  methods: {
    deserialize() {
      let { moduleName, metaInfo = {} } = this
      this.skipFields = [
        ...(this.filterfields || []),
        ...(this.primaryFields || []),
      ]
      let fields,
        promises = []

      if (this.$constants.moduleVsFixedDetails[moduleName]) {
        let showFields = this.$constants.moduleVsFixedDetails[moduleName]
        fields = []
        let metaFields
        metaFields = (metaInfo.fields || []).filter(
          f => showFields.includes(f.name) || !f.default
        )
        if (!isEmpty(metaFields)) {
          metaFields.forEach(field => {
            if (field) {
              if (field.name === 'description') {
                if (!isEmpty((this.details || {}).description)) {
                  fields.push({
                    name: 'description',
                    displayName: 'Description',
                    displayTypeEnum: 'TEXTAREA',
                    default: field.default,
                  })
                }
                return
              } else if (field.name === 'unitReservationCost') {
                if ((this.details || {}).reservable) {
                  fields.push({
                    name: 'unitReservationCost',
                    displayName: field.displayName,
                    displayTypeEnum: field.displayType._name,
                    default: field.default,
                  })
                }
                return
              } else if (
                (this.$org.id === 349 && field.name === 'number') ||
                field.name === 'sortingHelper'
              ) {
                // Temp for removing Accomations count and sort custom field
                return
              } else {
                let fieldClone = this.$helpers.cloneObject(field)
                fieldClone['displayTypeEnum'] = fieldClone.displayType._name
                fieldClone['lookupModuleName'] = (
                  fieldClone.lookupModule || {}
                ).name
                fields.push(fieldClone)
              }
            }
          })
        }
      } else {
        fields = getDefaultFields(this.details)[moduleName]
      }

      // Fetch field values
      fields.forEach(field => {
        let dataObj =
          (field || {}).default === false && !this.isV3Api
            ? this.details.data
            : this.details
        let promise = this.getFormattedValue(field, dataObj).then(value => {
          this.$set(field, 'displayValue', value)
        })
        promises.push(promise)
      })

      // Cleanup and seperate fields
      let textAreaFieldDisplayTypeEnum = ['RICH_TEXT', 'TEXTAREA']
      let textAreaFields = field =>
        textAreaFieldDisplayTypeEnum.includes(field.displayTypeEnum)

      this.generalFields = fields
        .filter(
          field =>
            !textAreaFields(field) && !this.skipFields.includes(field.name)
        )
        .sort((a, b) => {
          if (['DATE', 'DATE_TIME'].includes(a.dataTypeEnum)) {
            return -1
          } else if (['DATE', 'DATE_TIME'].includes(b.dataTypeEnum)) {
            return 1
          }
          return 0
        })
      if (this.generalFields.length % 2 !== 0) this.generalFields.push({})

      this.textAreaFields = fields.filter(
        field => textAreaFields(field) && !this.skipFields.includes(field.name)
      )
      Promise.all(promises).then(() => {
        this.loading = false
        this.autoResize()
      })
    },

    getFormattedValue(fieldObj, lookupObj = {}) {
      return new Promise(resolve => {
        if (
          fieldObj.name === 'matchedResources' ||
          fieldObj.name === 'includedResources'
        ) {
          let fieldObjName = lookupObj && lookupObj[fieldObj.name]

          fieldObjName = fieldObjName[0].name
          resolve(fieldObjName || '---')
        } else {
          let value = lookupObj && lookupObj[fieldObj.name]

          if (isDateField(fieldObj) || isDateTimeField(fieldObj)) {
            resolve(value > 0 ? getDisplayValue(fieldObj, value) : '---')
          } else if (isEnumField(fieldObj)) {
            resolve(value ? getDisplayValue(fieldObj, value) : '---')
          } else if (isBooleanField(fieldObj)) {
            resolve(isBoolean(value) ? getDisplayValue(fieldObj, value) : '---')
          } else if (fieldObj.displayTypeEnum === 'DURATION') {
            resolve(
              this.$helpers.getFormattedDuration(
                value,
                !isEmpty(fieldObj.unit) ? fieldObj.unit : 's'
              )
            )
          } else if (!isEmpty(fieldObj) && [2, 3].includes(fieldObj.dataType)) {
            let { unit } = fieldObj || {}
            if (!isEmpty(value) && !isEmpty(unit)) {
              value = `${value} ${unit}`
            } else if (!isEmpty(value)) {
              value = `${value}`
            } else {
              value = `---`
            }
            resolve(value)
          } else if (!isEmpty(fieldObj) && isLocationField(fieldObj)) {
            resolve(
              value
                ? parseFloat(value.lat).toFixed(6) +
                    ',' +
                    parseFloat(value.lng).toFixed(6)
                : '---'
            )
          } else if (
            isLookupSimple(fieldObj) &&
            value &&
            this.$getProperty(fieldObj, 'lookupModuleName', null)
          ) {
            let isRotating = ['rotatingItem', 'rotatingTool'].includes(
              fieldObj.name
            )
            if (!isRotating) {
              if (fieldObj.displayName === 'Site' && value === -1) {
                resolve('All sites')
              } else if (
                ['spacecategory'].includes(fieldObj.lookupModuleName)
              ) {
                resolve((this.details[`${fieldObj.name}`] || {}).name || '---')
              } else if (
                ['ticket', 'workorder'].includes(
                  this.$getProperty(fieldObj, 'lookupModule.name')
                )
              ) {
                resolve(
                  !isEmpty((value || {}).serialNumber)
                    ? `#${value.serialNumber}`
                    : '---'
                )
              } else if (
                this.$getProperty(fieldObj, 'lookupModule.name') ===
                'purchaseorder'
              ) {
                resolve(
                  !isEmpty((value || {}).localId) ? `#${value.localId}` : '---'
                )
              } else if (
                (isLookupSimple(fieldObj) &&
                  this.$getProperty(lookupObj, fieldObj.name, null) &&
                  isObject(lookupObj[fieldObj.name]) &&
                  this.$getProperty(
                    lookupObj,
                    `${fieldObj.name}.primaryValue`,
                    null
                  )) ||
                (fieldObj &&
                  fieldObj.displayType === 'LOOKUP_SIMPLE' &&
                  this.$getProperty(lookupObj, fieldObj.name, null) &&
                  isObject(lookupObj[fieldObj.name]) &&
                  this.$getProperty(
                    lookupObj,
                    `${fieldObj.name}.primaryValue`,
                    null
                  ))
              ) {
                // Custom modules lookup fields
                let name = fieldObj && fieldObj.name

                resolve(
                  lookupObj[name].primaryValue
                    ? lookupObj[name].primaryValue
                    : '---'
                )
              } else {
                let lookupModuleName =
                  fieldObj.lookupModuleName || fieldObj.lookupModule.name
                getFieldValue({
                  lookupModuleName,
                  selectedOptionId: [(value || {}).id || value],
                }).then(({ error, data }) => {
                  if (!error) {
                    let value = this.$getProperty(data, '0.label')
                    resolve(value ? value : '---')
                  } else {
                    resolve('---')
                  }
                })
              }
            }
          } else if (isLookupPopup(fieldObj)) {
            if (fieldObj.lookupModuleName === 'users') {
              let { id } = value || {}
              resolve(id > 0 ? this.getUser(id).name : '---')
            }
          } else {
            resolve(value || '---')
          }
        }
      })
    },
    isLookUpRedirectApplicable(field) {
      let { lookUpSpecialSupportModules } = this
      return (
        field.displayTypeEnum === 'LOOKUP_SIMPLE' &&
        field.lookupModuleName &&
        (lookUpSpecialSupportModules.includes(field.lookupModuleName) ||
          (field.lookupModule && field.lookupModule.typeEnum === 'CUSTOM'))
      )
    },
    isLocationFieldOrNot(field) {
      let { displayValue } = field || {}

      return !isEmpty(field)
        ? isLocationField(field) && displayValue !== '---'
        : false
    },
    getUrlDisplayName(field) {
      let { displayValue } = field || {}
      let { name, href } = displayValue || {}
      return name || href
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
  -webkit-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
  -moz-transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}
.fc-link-animation {
  animation: linkanimation 0.85s linear infinite alternate;
  -webkit-animation: linkanimation 0.85s linear infinite alternate;
  -moz-animation: linkanimation 0.85s linear infinite alternate;
}
@keyframes linkanimation {
  from {
    -webkit-transform: translateY(-2px);
    transform: translateY(-2px);
  }
  to {
    -webkit-transform: translateY(2px);
    transform: translateY(2px);
  }
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
