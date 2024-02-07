<template>
  <div>
    <portal :to="widget.key + '-title-section'">
      <div
        class="widget-header d-flex flex-direction-row justify-content-space mB15 mT5"
      >
        <div class="widget-header-name">ASSET COST DETAILS</div>
        <div
          v-if="$validation.isEmpty(getFieldData) && !loading && !decommission"
          class="fc-pink f13 pointer"
          @click="showAddDepreciationForm = true"
        >
          <inline-svg
            src="svgs/add"
            iconClass="icon icon-xs"
            class="vertical-middle mR5"
          ></inline-svg>
          Apply Depreciation
        </div>
      </div>
    </portal>

    <div>
      <div v-if="loading" class="container">
        <div v-for="index in [1, 2, 3, 4, 5]" :key="index" class="field">
          <el-row>
            <el-col :span="12">
              <span class="lines loading-shimmer"></span>
            </el-col>

            <el-col :span="12">
              <span class="lines loading-shimmer"></span>
            </el-col>
          </el-row>
        </div>
      </div>

      <div
        v-else-if="$validation.isEmpty(getFieldData)"
        class="mT20"
        style="color: #324056;"
      >
        <div class="d-flex flex-direction-column text-center">
          <inline-svg
            src="svgs/emptystate/reportlist"
            iconClass="icon text-center icon-100"
          ></inline-svg>

          <div class="nowo-label f13">
            No depreciation schedules configured
          </div>
        </div>
      </div>

      <div v-else class="container">
        <div
          v-for="(fieldValue, displayName, index) in getFieldData"
          :key="index"
          class="field"
        >
          <el-row class="d-flex">
            <el-col :span="12" class="field-label mL20 mR20">
              {{ displayName }}
            </el-col>

            <el-col :span="12" class="field-value mL20 mR20">
              {{ fieldValue }}
            </el-col>
          </el-row>
        </div>
      </div>
    </div>

    <depreciation-form
      v-if="showAddDepreciationForm"
      :asset="details"
      @fetchAssetDetail="init()"
      @onClose="showAddDepreciationForm = false"
    ></depreciation-form>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import DepreciationForm from 'pages/assets/asset/v1/AddDepreciationForm'

export default {
  props: ['details', 'widget'],

  data() {
    return {
      loading: false,
      depreciation: null,
      showAddDepreciationForm: false,
    }
  },

  components: { DepreciationForm },

  created() {
    this.init()
  },

  computed: {
    ...mapState({
      metaInfo: state => state.view.metaInfo,
    }),
    decommission() {
      return this.$getProperty(this, 'details.decommission', false)
    },
    expectedLife() {
      let { frequencyType, frequency } = this.depreciation || {}

      if (!isEmpty(frequency) && !isEmpty(frequencyType)) {
        if (frequencyType === 1) {
          let years = parseInt(frequency / 12)
          let months = frequency % 12
          let expectedLife = ''

          expectedLife += years ? `${years} Year(s) ` : ''
          expectedLife += months ? `${months} Month(s)` : ''

          return expectedLife
        } else if (frequencyType === 2) {
          let years = parseInt(frequency / 4)
          let months = (frequency % 4) * 3
          let expectedLife = ''

          expectedLife += years ? `${years} Year(s) ` : ''
          expectedLife += months ? `${months} Month(s)` : ''

          return expectedLife
        } else if (frequencyType === 3) {
          let years = parseInt(frequency / 2)
          let months = (frequency % 2) * 6
          let expectedLife = ''

          expectedLife += years ? `${years} Year(s) ` : ''
          expectedLife += months ? `${months} Month(s)` : ''

          return expectedLife
        } else if (frequencyType === 4) {
          return `${frequency} Year(s)`
        }
      }

      return '---'
    },

    constructRemainingTime() {
      let { depreciation, details } = this
      let fields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(depreciation) && !isEmpty(fields)) {
        let startDateField =
          details[
            (
              fields.find(
                field => field.id === depreciation.startDateFieldId
              ) || {}
            ).name || ''
          ]

        let expiredTime = moment.duration(new Date() - startDateField)
        let { frequencyType, frequency } = depreciation
        let constructExpectedLife

        if (frequencyType === 1) {
          constructExpectedLife = moment.duration(frequency, 'M')
        } else if (frequencyType === 2) {
          let years = parseInt(frequency / 4)
          let months = (frequency % 4) * 3

          constructExpectedLife = moment.duration({
            M: months,
            y: years,
          })
        } else if (frequencyType === 3) {
          let years = parseInt(frequency / 2)
          let months = (frequency % 2) * 6

          constructExpectedLife = moment.duration({
            M: months,
            y: years,
          })
        } else if (frequencyType === 4) {
          constructExpectedLife = moment.duration(frequency, 'y')
        }

        let remainingTime = constructExpectedLife.subtract(expiredTime)

        let { _data = {} } = remainingTime || {}
        let remainingPeriod = ''

        remainingPeriod += _data.years > 0 ? `${_data.years} Year(s) ` : ''
        remainingPeriod += _data.months > 0 ? `${_data.months} Month(s)` : ''
        if (!isEmpty(remainingPeriod)) return remainingPeriod
      }

      return '---'
    },

    getFieldData() {
      let fieldData = {}
      let { depreciation, details, $currency } = this
      let fields = this.$getProperty(this.metaInfo, 'fields', null)

      if (!isEmpty(depreciation) && !isEmpty(fields)) {
        fieldData['Current Price'] = !isEmpty(details.currentPrice)
          ? ['$', '₹'].includes($currency)
            ? `${$currency}${details.currentPrice}`
            : `${details.currentPrice}${$currency}`
          : '---'
        fieldData['Total Price'] = !isEmpty(details.unitPrice)
          ? ['$', '₹'].includes($currency)
            ? `${$currency}${details.unitPrice}`
            : `${details.unitPrice}${$currency}`
          : '---'
        fieldData['Salvage Price'] = !isEmpty(details.salvageAmount)
          ? ['$', '₹'].includes($currency)
            ? `${$currency}${details.salvageAmount}`
            : `${details.salvageAmount}${$currency}`
          : ['$', '₹'].includes($currency)
          ? `${$currency}0`
          : `0${$currency}`

        let startDateField =
          details[
            (
              fields.find(
                field => field.id === depreciation.startDateFieldId
              ) || {}
            ).name || ''
          ]

        fieldData['Start Date'] = startDateField
          ? moment(startDateField)
              .tz(this.$timezone)
              .format('MMM YYYY')
          : '---'
        fieldData['Expected Life of Asset'] = this.expectedLife
        fieldData['Expected Life Remaining'] = this.constructRemainingTime
      }

      return fieldData
    },
  },

  methods: {
    init() {
      this.loading = true
      Promise.all([
        this.$store.dispatch('view/loadModuleMeta', 'asset'),
        this.getDepreciation(),
      ]).finally(() => (this.loading = false))
    },

    getDepreciation() {
      let url = 'v2/asset/depreciation/getForAsset'
      let param = { assetId: this.details.id }

      return API.post(url, param).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.depreciation = data.assetdepreciation
        }
      })
    },
  },
}
</script>
<style scoped>
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

.lines {
  height: 12px;
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
</style>
