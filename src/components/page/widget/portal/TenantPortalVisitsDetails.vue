<template>
  <div class="asset-details-widget">
    <div class="container" ref="content-container">
      <div
        v-for="(field, index) in textAreaFields"
        :key="field.name + index"
        class="field"
        style="flex: 0 100%"
      >
        <el-row>
          <el-col :span="6" class="field-label">{{ field.displayName }}</el-col>
          <el-col :span="16" class="field-value line-height22">{{
            field.displayValue
          }}</el-col>
        </el-row>
      </div>
      <div
        v-for="(field, index) in ffields"
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
            <span>{{ field.displayValue }}</span>
          </el-col>
        </el-row>
      </div>

      <div
        v-for="(field, index) in sysFields"
        :key="field.name + index"
        class="field"
      >
        <el-row>
          <el-col :span="12" class="field-label">{{
            field.displayName
          }}</el-col>
          <el-col :span="12" class="field-value line-height22">{{
            getSysFieldValue(field)
          }}</el-col>
        </el-row>
      </div>

      <template v-if="details.geoLocationEnabled">
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">Geo Location</el-col>
            <el-col :span="12" class="field-value">
              {{
                details.geoLocation
                  ? '@' + $helpers.parseLocation(details.geoLocation)
                  : '---'
              }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">Current Location</el-col>
            <el-col :span="12" class="field-value">
              {{
                details.currentLocation
                  ? '@' + $helpers.parseLocation(details.currentLocation)
                  : '---'
              }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">Designated Location</el-col>
            <el-col :span="12" class="field-value">{{
              details.designatedLocation ? 'Yes' : 'No'
            }}</el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">Distance Moved</el-col>
            <el-col :span="12" class="field-value">
              {{
                details.distanceMoved > 0
                  ? Math.round(details.distanceMoved) + 'm'
                  : '---'
              }}
            </el-col>
          </el-row>
        </div>
        <div class="field">
          <el-row>
            <el-col :span="12" class="field-label">{{
              $t('space.sites.boundary_radius')
            }}</el-col>
            <el-col :span="12" class="field-value">
              {{
                details.boundaryRadius > 0
                  ? details.boundaryRadius + 'm'
                  : '---'
              }}
            </el-col>
          </el-row>
        </div>
      </template>

      <div v-for="(field, index) in custfields" :key="field.name" class="field">
        <div
          @click="redirectToLookUpFieldSummary(field)"
          v-if="isLookUpRedirectApplicable(field)"
        >
          <el-row>
            <el-col :span="12" :key="field.displayName" class="field-label">{{
              field.displayName
            }}</el-col>
            <el-col :span="12" :key="index + 1 + 'asset'" class="field-value">
              <div class="bluetxt pointer">{{ field.displayValue }}</div>
            </el-col>
          </el-row>
        </div>
        <el-row v-else>
          <el-col :span="12" :key="field.displayName" class="field-label">{{
            field.displayName
          }}</el-col>
          <el-col :span="12" :key="index + 1 + 'asset'" class="field-value">{{
            field.displayValue
          }}</el-col>
        </el-row>
      </div>
    </div>
    <div
      v-if="needsShowMore"
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
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import DetailsWidgetMixin from 'src/components/page/widget/common/DetailsWidgetMixin'
import { getDisplayValue } from 'util/field-utils'
import { mapStateWithLogging } from 'store/utils/log-map-state'
export default {
  props: [
    'moduleName',
    'details',
    'layoutParams',
    'resizeWidget',
    'calculateDimensions',
    'primaryFields',
  ],
  mixins: [DetailsWidgetMixin],

  mounted() {
    this.getFormMeta()
  },

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
      ffields: [],
      sysFields: [],
      custfields: [],
      textAreaFields: [],
      filterfields: [
        'geoLocation',
        'currentLocation',
        'designatedLocation',
        'boundaryRadius',
      ],
      lookupValue: null,
      needsShowMore: false,
      isAllVisible: false,
      defaultWidgetHeight: this.layoutParams.h,
      filterWoFields: [
        'visitorEmail',
        'visitorPhone',
        'host',
        'vendor',
        'tenant',
        'requestedBy',
        'purposeOfVisit',
        'checkInTime',
        'checkOutTime',
        'expectedCheckInTime',
        'expectedCheckOutTime',
        'visitorName',
        'visitedSpace',
        'avatar',
        'nda',
      ],
    }
  },
  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    ...mapStateWithLogging({
      spaces: state => state.spaces,
    }),

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
  },
  methods: {
    getSysFieldValue(field) {
      let value
      let { details } = this
      if (
        field.name === 'sysModifiedBy' ||
        field.name === 'sysCreatedBy' ||
        field.name === 'lastIssuedToUser'
      ) {
        value = details[field.name]
          ? this.$store.getters.getUser(details[field.name].id).name
          : ''
        return !isEmpty(value) ? value : null
      } else if (
        field.name === 'sysCreatedTime' ||
        field.name === 'sysModifiedTime' ||
        field.name === 'lastIssuedTime'
      ) {
        value = details[field.name]
        if (value === 0) {
          return '---'
        }
        let timePerdiod = this.$options.filters.formatDate(value, true)
        if (!isEmpty(timePerdiod)) {
          return timePerdiod
        }
      } else {
        value = details[field.name]
        return value || '---'
      }
      return '---'
    },
    deserialize(data) {
      let { moduleName, metaInfo } = this
      let { user } = this.$account
      let { appType } = user || {}
      this.skipFields = [...this.filterfields, ...this.primaryFields]

      // Fetch field values
      data.fields.forEach(field => {
        let dataObj =
          field.field && field.field.default !== true
            ? this.details.data
            : this.details

        this.getFormattedValue(field, dataObj).then(value => {
          this.$set(field, 'displayValue', value)
        })
      })

      // Cleanup and seperate fields
      let fields = data.fields
        .filter(
          field =>
            field.field &&
            field.field.default === true &&
            field.displayTypeEnum !== 'TEXTAREA' &&
            !this.skipFields.includes(field.field.name)
        )
        .sort((a, b) => {
          if (['DATE', 'DATE_TIME'].includes(a.field.dataTypeEnum)) {
            return -1
          } else if (['DATE', 'DATE_TIME'].includes(b.field.dataTypeEnum)) {
            return 1
          }
          return 0
        })

      this.ffields = []
      if (moduleName === 'visitorlogging') {
        for (let fieldToRemove of this.filterWoFields) {
          let fieldName = fields.findIndex(at => at.name === fieldToRemove)
          if (fieldName !== -1) {
            fields.splice(fieldName, 1)
          }
        }

        this.ffields.push({
          name: 'visitorEmail',
          displayName: 'Email',
          displayValue: this.details.visitorEmail
            ? this.details.visitorEmail
            : '---',
        })
        this.ffields.push({
          name: 'visitorPhone',
          displayName: 'Phone',
          displayValue: this.details.visitorPhone
            ? this.details.visitorPhone
            : '---',
        })
        this.ffields.push({
          name: 'host',
          displayName: 'Host',
          displayValue: this.details.host ? this.details.host.name : '---',
        })
        if (appType === 3) {
          this.ffields.push({
            name: 'tenant',
            displayName: 'Tenant',
            displayValue: this.details.tenant
              ? this.details.tenant.name
              : '---',
          })
        }
        if (appType === 2) {
          this.ffields.push({
            name: 'vendor',
            displayName: 'Vendor',
            displayValue: this.details.vendor
              ? this.details.vendor.name
              : '---',
          })
        }
        this.ffields.push({
          name: 'requestedBy',
          displayName: 'Requested By',
          displayValue: this.details.requestedBy
            ? this.details.requestedBy.name
            : '---',
        })
        this.ffields.push({
          name: 'visitorType',
          displayName: 'Visitor Type',
          displayValue: this.details.visitorType
            ? this.details.visitorType.name
            : '---',
        })
        if (this.details.preregistered) {
          this.ffields.push({
            name: 'expectedCheckInTime',
            displayName: 'Expected Check In Time',
            displayValue:
              this.details.expectedCheckInTime > 0
                ? this.$options.filters.formatDate(
                    this.details.expectedCheckInTime,
                    false
                  )
                : '---',
          })
          this.ffields.push({
            name: 'expectedCheckOutTime',
            displayName: 'Expected Check Out Time',
            displayValue:
              this.details.expectedCheckOutTime &&
              this.details.expectedCheckOutTime !== -1
                ? this.$options.filters.formatDate(
                    this.details.expectedCheckOutTime
                  )
                : '---',
          })
        }
        if (appType === 3 || !this.details.preregistered) {
          this.ffields.push({
            name: 'checkInTime',
            displayName: 'Check In Time',
            displayValue:
              this.details.checkInTime > 0
                ? this.$options.filters.formatDate(
                    this.details.checkInTime,
                    false
                  )
                : '---',
          })
          this.ffields.push({
            name: 'checkOutTime',
            displayName: 'Check Out Time',
            displayValue:
              this.details.checkOutTime && this.details.checkOutTime !== -1
                ? this.$options.filters.formatDate(this.details.checkOutTime)
                : '---',
            // this.details.modifiedTime > 0 ? this.details.modifiedTime : '---',
          })
        }
        let purposeOfVisitField = data.fields.find(
          field => field.name === 'purposeOfVisit'
        )
        if ((purposeOfVisitField || {}).field) {
          this.ffields.push({
            name: 'purposeOfVisit',
            displayName: 'Purpose Of Visit',
            displayValue: getDisplayValue(
              purposeOfVisitField.field,
              this.details.purposeOfVisit
            ),
          })
        }

        for (let f of fields) {
          this.ffields.push(f)
        }
      }

      this.custfields = data.fields.filter(
        field =>
          field.lookupModuleName !== 'site' &&
          field.field &&
          field.field.default !== true &&
          field.displayTypeEnum !== 'TEXTAREA'
      )
      this.textAreaFields = data.fields.filter(
        field =>
          field.displayTypeEnum === 'TEXTAREA' &&
          field.field.name !== 'description' &&
          !this.skipFields.includes(field.field.name)
      )
      this.sysFields = (this.metaInfo.fields || []).filter(field =>
        this.systemFields.includes(field.name)
      )
      let custfieldsCount = this.custfields ? this.custfields.length : 0
      let textAreaFieldsCount = this.textAreaFields
        ? this.textAreaFields.length
        : 0
      let sysFieldsCount = this.sysFields ? this.sysFields.length : 0
      let ffieldsCount = this.ffields ? this.ffields.length : 0
      let totalCount =
        custfieldsCount + textAreaFieldsCount + sysFieldsCount + ffieldsCount
      if (totalCount > 4) {
        this.needsShowMore = true
      } else {
        this.needsShowMore = false
      }
    },

    getFormMeta() {
      this.loading = true
      let url = `/v2/forms/${this.moduleName}?formId=${this.details.formId}`
      this.$http
        .get(url)
        .then(response => {
          this.deserialize(response.data.result.form)
          //   this.$nextTick(() => this.autoResize())
        })
        .catch(() => {})
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
