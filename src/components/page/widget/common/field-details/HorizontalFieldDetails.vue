<template>
  <div :class="['asset-details-widget', widget.title && 'pT0']">
    <template v-if="widget.title">
      <div class="widget-topbar">
        <div class="widget-title mL0">{{ widget.title }}</div>
      </div>
    </template>

    <div v-if="loading" :class="['container', widget.title && 'pL0']">
      <div v-for="index in [1, 2, 3]" :key="index" class="field">
        <el-row>
          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>

          <el-col :span="12">
            <span
              :class="['lines loading-shimmer', widget.title && 'm0']"
            ></span>
          </el-col>
        </el-row>
      </div>

      <div class="field">
        <el-row>
          <el-col :span="12"></el-col>
          <el-col :span="12"></el-col>
        </el-row>
      </div>
    </div>

    <div v-else-if="emptyState" class="mT20" style="color: #8ca1ad;">
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

    <div
      v-else
      :class="['container', widget.title && 'pL0']"
      ref="content-container"
    >
      <template v-for="(field, index) in fieldsList">
        <el-row
          v-if="['TEXTAREA', 'RICH_TEXT'].includes(field.displayTypeEnum)"
          :key="field.name + index"
          class="field full-row"
        >
          <el-col :span="6" class="field-label">
            {{ getFormattedDisplayName(field) }}
          </el-col>
          <el-col
            v-if="field.displayTypeEnum === 'TEXTAREA'"
            :span="16"
            class="field-value line-height22 text-area"
          >
            {{ field.displayValue }}
          </el-col>
          <el-col
            v-if="field.displayTypeEnum === 'RICH_TEXT'"
            :span="16"
            class="field-value line-height22"
          >
            <div
              class="rich-text-preview-details"
              v-if="!$validation.isEmpty(field.displayValue)"
              v-html="sanitize(field.displayValue)"
            ></div>
            <span v-else>---</span>
          </el-col>
        </el-row>
        <!-- TODO Handle this based on displaytype and span-->
        <el-row
          :key="`childMeter-${field.name}-${index}`"
          v-else-if="showChildMeterExp && field.name === 'childMeterExpression'"
          class="field full-row"
        >
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>
          <el-col :span="18" class="field-value">
            <child-meter-expression
              :assetDetails="details"
            ></child-meter-expression>
          </el-col>
        </el-row>
        <el-row v-else :key="`${field.name}-${index}`" class="field">
          <el-col :span="12" :key="field.displayName" class="field-label">
            {{ getFormattedDisplayName(field) }}
          </el-col>
          <el-col
            :span="12"
            :key="field.displayName + 'value'"
            class="field-value"
          >
            <div
              v-if="
                details.rotatingItem &&
                  details.rotatingItem.id &&
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
                details.rotatingTool &&
                  details.rotatingTool.id &&
                  field.name === 'rotatingTool'
              "
              @click="
                $router.push({
                  path:
                    '/app/inventory/tools/' +
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
            <div v-else-if="isLookupSimple(field)">
              <GlimpseLookupWrapper
                :field="field"
                :record="details"
                :siteList="siteList"
                :recordModuleName="moduleName"
              ></GlimpseLookupWrapper>
            </div>

            <div v-else-if="systemFields.includes(field.name)">
              {{ getSysFieldValue(field) }}
            </div>

            <div v-else-if="isFileField(field)">
              <el-row>
                <el-col
                  :span="12"
                  :key="index + 1 + 'asset'"
                  class="attachment-label pointer"
                >
                  <iframe
                    v-if="exportDownloadUrl[field.name]"
                    :src="exportDownloadUrl[field.name]"
                    style="display: none;"
                  ></iframe>

                  <a @click="downloadAttachment(field)">
                    {{ getFileName(field) }}
                  </a>
                </el-col>
              </el-row>
            </div>

            <div
              v-else-if="
                $getProperty(field, 'field.displayType') === 'SIGNATURE'
              "
            >
              <SignatureField :field="(field || {}).field" :record="details" />
            </div>

            <template v-else-if="isGeoLocationField(field)">
              <div
                v-if="!$validation.isEmpty(field.displayValue)"
                class="bluetxt pointer"
                @click="$helpers.openInMapForLatLngStr(field.displayValue)"
              >
                {{ `@${field.displayValue}` }}
              </div>
              <span v-else>---</span>
            </template>
            <template v-else-if="field.displayTypeEnum === 'URL_FIELD'">
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
            <template v-else-if="field.displayTypeEnum === 'CURRENCY'">
              <div class="d-flex" v-if="showCurrencyField(field)">
                <span>{{ getCurrencyFieldValue(field) }}</span>
                <el-popover
                  v-if="showInfoIcon(field)"
                  v-model="showPopOver"
                  placement="bottom"
                  :title="$t('setup.currency.rate')"
                  width="230"
                  trigger="click"
                >
                  <div
                    class="currency-conversion-rate"
                    @click="showPopOver = false"
                  >
                    <fc-icon
                      group="default"
                      name="close"
                      size="15"
                      class="pointer rate-svg-close"
                    ></fc-icon>
                    <span class="currency-desc">
                      {{ `${getCurrencyFieldValue(field, true)}` }}
                    </span>
                  </div>
                  <fc-icon
                    slot="reference"
                    group="dsm"
                    class="pointer info-position mL2"
                    name="info"
                    size="13"
                  ></fc-icon>
                </el-popover>
              </div>
              <span v-else>---</span>
            </template>
            <template v-else-if="field.displayTypeEnum === 'MULTI_CURRENCY'">
              <CurrencyPopOver
                :field="field"
                :details="details"
                :showInfo="true"
              />
            </template>
            <div
              v-else-if="$getProperty(field, 'field.displayType') === 'TIME'"
            >
              <div v-if="!$validation.isEmpty(field.displayValue)">
                {{ `${getTimeFieldValue(field)}` }}
              </div>
              <span v-else>---</span>
            </div>
            <span v-else>{{ field.displayValue }}</span>
          </el-col>
        </el-row>
      </template>

      <div v-if="additionalField" class="field">
        <el-row>
          <el-col :span="12" class="field-label"></el-col>
          <el-col :span="12" class="field-value"></el-col>
        </el-row>
      </div>
    </div>
    <div
      v-if="needsShowMore"
      class="text-center pT10 pB15 mR20"
      style="background-color: #fff;"
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
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import ItemToolAvatar from '@/avatar/ItemTool'
import { mapState } from 'vuex'
import ChildMeterExpression from '@/fields/ChildMeterExpression'
import DetailsWidgetMixin from '../DetailsWidgetMixin'
import SignatureField from '@/list/SignatureColumn'
import isEqual from 'lodash/isEqual'
import { isGeoLocationField } from '@facilio/utils/field'
import { sanitize } from '@facilio/utils/sanitize'
import { getFieldOptions } from 'util/picklist'
import GlimpseLookupWrapper from 'src/newapp/components/GlimpseLookupWrapper.vue'
import { mapStateWithLogging } from 'store/utils/log-map-state'
import { getFormatedTime } from '@/mixins/TimeFormatMixin'
import CurrencyPopOver from 'src/pages/setup/organizationSetting/currency/CurrencyPopOver.vue'

const skipFieldsForModuleMap = {
  facility: [
    'slotDuration',
    'amenities',
    'isChargeable',
    'maxSlotBookingAllowed',
    'isMultiBookingPerSlotAllowed',
    'bookingAdvancePeriodInDays',
    'isAttendeeListNeeded',
  ],
}
export default {
  name: 'FieldDetails',
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'calculateDimensions',
    'primaryFields',
    'widget',
    'isV3Api',
  ],
  components: {
    ItemToolAvatar,
    ChildMeterExpression,
    SignatureField,
    GlimpseLookupWrapper,
    CurrencyPopOver,
  },
  mixins: [DetailsWidgetMixin],
  data() {
    return {
      loading: false,
      systemFields: [
        'sysModifiedBy',
        'sysCreatedBy',
        'sysCreatedTime',
        'sysModifiedTime',
        'lastIssuedToUser',
        'lastIssuedToWo',
        'lastIssuedTime',
      ],
      fieldsList: [],
      filterfields: ['address'],
      lookupValue: null,
      needsShowMore: false,
      isAllVisible: false,
      defaultWidgetHeight: (this.layoutParams || {}).h,
      additionalField: false,
      emptyState: false,
      showChildMeterExp: false,
      siteList: [],
      showPopOver: false,
    }
  },
  created() {
    this.isGeoLocationField = isGeoLocationField
    this.sanitize = sanitize

    this.$store.dispatch('getActiveCurrencyList')
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
      users: state => state.users,
      account: state => state.account,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),
    sites() {
      let { siteList } = this
      let siteObj = {}
      siteList.forEach(obj => {
        siteObj[`${obj.value}`] = obj
      })
      return siteObj
    },
    space() {
      return this.spaces.reduce((arr, space) => {
        arr[space.id] = space.name
        return arr
      }, {})
    },
    showMoreLinkText() {
      return this.isAllVisible
        ? this.$t('common._common.view_less')
        : this.$t('common._common.view_more')
    },
    multiCurrency() {
      let { displaySymbol, currencyCode, multiCurrencyEnabled } =
        this.account.data.currencyInfo || {}
      return { displaySymbol, currencyCode, multiCurrencyEnabled }
    },
  },
  watch: {
    details: {
      async handler(value, oldValue) {
        if (!isEqual(value, oldValue)) {
          await this.$store.dispatch('view/loadModuleMeta', this.moduleName)
          await this.getSites()
          this.getFormMeta()
        }
      },
      immediate: true,
    },
  },
  methods: {
    async getSites() {
      let { siteId = null } = this.details || {}
      if (isEmpty(siteId)) return
      let defaultIds = [siteId]
      let params = {
        field: { lookupModuleName: 'site' },
        defaultIds,
        perPage: 1,
      }
      await getFieldOptions(params).then(({ error, options }) => {
        if (!error) {
          this.siteList = options
        }
      })
    },
    deserialize(data) {
      let { moduleName } = this
      let promises = []
      let skipFields = [
        ...this.filterfields,
        ...this.primaryFields,
        ...(skipFieldsForModuleMap[moduleName] || []),
      ]

      // Fetch field values
      let fieldsArray = this.$getProperty(data, 'fields', [])

      let fields = []
      fieldsArray.forEach(field => {
        let dataObj =
          !this.isV3Api && field.field && field.field.default !== true
            ? this.details.data
            : this.details

        if (!skipFields.includes(this.$getProperty(field, 'field.name'))) {
          let promise = this.getFormattedValue(field, dataObj, this.sites).then(
            value => {
              this.$set(field, 'displayValue', value)
            }
          )
          promises.push(promise)
          fields.push(field)
        }

        if (field.name === 'childMeterExpression') {
          this.showChildMeterExp = !isEmpty(dataObj.childMeterExpression)
        }
      })

      this.fieldsList = fields

      let fieldCount =
        this.fieldsList.length -
        this.fieldsList.filter(field => field.displayTypeEnum === 'TEXTAREA')
          .length
      if (this.showChildMeterExp) {
        --fieldCount
      }

      if (fieldCount % 2 !== 0) this.additionalField = true

      Promise.all(promises).then(() => {
        this.loading = false
        this.autoResize()
      })
    },
    getFormMeta() {
      let { moduleName, details } = this
      if (isEmpty(moduleName) || isEmpty(details)) {
        this.emptyState = true
        return
      }

      this.loading = true
      API.get(`/v3/summary/fields/${this.moduleName}`, {
        formId: details.formId ? details.formId : -1,
        id: details.id,
        widgetParams: this.widget?.widgetParams
          ? JSON.stringify(this.widget?.widgetParams)
          : null,
      }).then(({ error, data }) => {
        if (!error) {
          this.emptyState = false
          this.deserialize(data)
        } else {
          this.emptyState = true
          this.autoResize()
        }
        this.loading = false
      })
    },
    getUrlDisplayName(field) {
      let { displayValue } = field || {}
      let { name, href } = displayValue || {}
      return name || href
    },
    getCurrencyFieldValue(field, rate = false) {
      let { displayValue } = field || {}
      let {
        displaySymbol: baseSymbol,
        currencyCode: baseCode,
        multiCurrencyEnabled,
      } = this.multiCurrency
      let {
        displaySymbol,
        currencyValue,
        baseCurrencyValue,
        exchangeRate,
        currencyCode,
      } = displayValue || {}
      let baseValue = ''
      if (multiCurrencyEnabled && baseCode !== currencyCode)
        baseValue = `( ${baseSymbol} ${baseCurrencyValue} )`
      return rate
        ? `${baseSymbol} 1 = ${displaySymbol} ${exchangeRate}`
        : `${displaySymbol} ${currencyValue} ${baseValue}`
    },

    showCurrencyField(field) {
      return !isEmpty(field.displayValue) && field.displayValue != '---'
    },
    showInfoIcon(field) {
      let { currencyCode: baseCode, multiCurrencyEnabled } = this.multiCurrency
      let { displayValue } = field || {}
      let { currencyCode } = displayValue || {}
      return multiCurrencyEnabled && baseCode !== currencyCode
    },
    getTimeFieldValue(field) {
      let { displayValue } = field || {}
      if (displayValue === '---') {
        return displayValue
      }
      return getFormatedTime(displayValue)
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
.text-area {
  white-space: pre-line;
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
  border-bottom: 1px solid #edf4fa;
  transition: top 0.45s cubic-bezier(0.44, 0.99, 0.48, 1);
}

.fc-link-animation {
  animation: linkanimation 0.85s linear infinite alternate;
}
@keyframes linkanimation {
  from {
    transform: translateY(-2px);
  }
  to {
    transform: translateY(2px);
  }
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
  letter-spacing: 0.5px;
}
.field-value {
  padding-left: 10px;
  font-size: 13px;
  font-weight: normal;
  letter-spacing: 0.5px;
  color: #324056;
}
.full-row {
  flex: 0 100%;
}
</style>
<style scoped lang="scss">
.url-field-display {
  color: #46a2bf;
  &:hover {
    text-decoration: underline;
    text-underline-offset: 3px;
    color: #46a2bf;
  }
}
.rich-text-preview-details {
  display: flex;
  white-space: pre-line;
  word-break: break-word;
}

.info-position {
  position: relative;
  top: 1px;
}
.rate-svg-close {
  position: absolute;
  right: 11px;
  top: 13px;
}
</style>
