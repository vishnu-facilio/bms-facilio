<template>
  <div class="depreciation-list">
    <div
      v-if="canShowMethod"
      class="flex-middle justify-content-space p20 width100 "
    >
      <span
        v-if="loading"
        class="loading-shimmer width250px"
        style="height: 16px; border-radius: 5px;"
      ></span>

      <div v-else class="label-txt-black bold">
        Depreciation Method -
        <span class="fw4">{{ depreciationMethod }} </span>
      </div>
    </div>

    <div>
      <div v-if="loading" class="mT8">
        <div v-for="index in [1, 2, 3, 4]" :key="index" class="d-flex">
          <div
            :class="['d-flex', idx !== 0 ? 'width100' : 'mL-20']"
            v-for="idx in [0, 1]"
            :key="idx"
          >
            <span
              :class="[
                'lines loading-shimmer',
                idx === 0 ? 'width210px' : 'width100',
              ]"
            ></span>
          </div>
        </div>
      </div>

      <div
        v-else-if="$validation.isEmpty(depreciationTable)"
        style="color: #324056;"
        class="text-center d-flex flex-direction-column mT30"
      >
        <inline-svg
          src="svgs/emptystate/reportlist"
          iconClass="icon text-center icon-100"
        ></inline-svg>

        <div class="nowo-label f13">
          No depreciation schedule applied
        </div>
      </div>

      <div class="mT8" v-else>
        <el-table
          :data="depreciationTable"
          ref="depreciationSchedule"
          style="width: 98%"
          class="mL15"
          height="auto"
          :fit="true"
          :cell-style="{ padding: '10px 20px' }"
          header-row-class-name="hide"
          :row-class-name="rowStyle"
          :cell-class-name="cellStyle"
        >
          <el-table-column
            fixed
            min-width="250"
            prop="displayName"
          ></el-table-column>
          <el-table-column
            min-width="150"
            :align="'right'"
            v-for="(data, index) in depreciationSchedule.length"
            :key="index"
            :prop="`col${index}`"
          ></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import moment from 'moment-timezone'
import {
  DEPRECIATION_TYPES,
  FREQUENCY_TYPES,
  QUARTERLY_FREQUENCY,
} from './DepreciationConstant'

export default {
  props: ['asset', 'selectedDepreciationId', 'showMethod'],
  data() {
    return {
      depreciationSchedule: null,
      depreciation: null,
      loading: false,
    }
  },

  created() {
    if (isEmpty(this.selectedDepreciationId)) {
      this.getDepreciation()
    } else {
      this.fetchDepreciation()
    }
  },

  computed: {
    depreciationTable() {
      let tableData = []
      let { depreciationSchedule, depreciation, $currency } = this

      if (!isEmpty(depreciationSchedule) && !isEmpty(depreciation)) {
        let listObjKeys = [
          'calculatedDate',
          'openPrice',
          'depreciatedAmount',
          'currentPrice',
        ]

        listObjKeys.forEach(key => {
          let columnData = {}

          if (key === 'calculatedDate') {
            depreciationSchedule.forEach((listData, index) => {
              columnData[`col${index}`] = moment(listData[key])
                .tz(this.$timezone)
                .format('MMM YYYY')
                .toUpperCase()
            })
          } else if (key === 'openPrice') {
            columnData['col0'] = ['$', '₹'].includes($currency)
              ? `${$currency}${this.asset.unitPrice}`
              : `${this.asset.unitPrice}${$currency}`

            depreciationSchedule.forEach((listData, idx) => {
              let index = idx + 1

              if (index !== depreciationSchedule.length) {
                if (['$', '₹'].includes($currency))
                  columnData[
                    `col${index}`
                  ] = `${$currency}${listData['currentPrice']}`
                else
                  columnData[
                    `col${index}`
                  ] = `${listData['currentPrice']}${$currency}`
              }
            })
          } else {
            depreciationSchedule.forEach((listData, index) => {
              if (['$', '₹'].includes($currency))
                columnData[`col${index}`] = `${$currency}${listData[key]}`
              else columnData[`col${index}`] = `${listData[key]}${$currency}`
            })
          }

          tableData.push(columnData)
        })

        let data = ['Opening Book Value', 'Depreciation', 'Ending Book Value']
        let { frequencyTypeEnum } = depreciation || {}

        data.splice(0, 0, frequencyTypeEnum)
        data.forEach((data, index) => {
          tableData[index].displayName = data
        })
      }

      return tableData
    },

    currentDepreciationDate() {
      let { depreciation, frequencyTypes } = this
      let today = moment(new Date())
      let dateToCompare = []

      if (!isEmpty(depreciation)) {
        let { frequencyType } = depreciation

        if (frequencyType === frequencyTypes.MONTHLY) {
          dateToCompare.push(today.month())
        } else if (
          [frequencyTypes.QUARTERLY, frequencyTypes.HALFYEARLY].includes(
            frequencyType
          )
        ) {
          let quarterDate = today.quarter()

          if (frequencyType === frequencyTypes.HALFYEARLY) {
            if (QUARTERLY_FREQUENCY.FIRSTHALFYEAR.includes(quarterDate)) {
              quarterDate = 1
            } else if (
              QUARTERLY_FREQUENCY.SECONDHALFYEAR.includes(quarterDate)
            ) {
              quarterDate = 2
            }
          }

          dateToCompare.push(quarterDate)
        }

        dateToCompare.push(today.year())
      }

      return dateToCompare
    },

    currentDepreciationCol() {
      let {
        currentDepreciationDate,
        depreciation,
        depreciationSchedule,
        frequencyTypes,
      } = this
      let currentCol = null

      if (!isEmpty(currentDepreciationDate)) {
        depreciationSchedule.forEach((listData, index) => {
          let dateRange = moment(listData['date'])
          let { frequencyType } = depreciation
          let currentDateValue = []

          if (frequencyType === frequencyTypes.MONTHLY) {
            currentDateValue.push(dateRange.month())
          } else if (
            [frequencyTypes.QUARTERLY, frequencyTypes.HALFYEARLY].includes(
              frequencyType
            )
          ) {
            let quarterDate = dateRange.quarter()

            if (frequencyType === frequencyTypes.HALFYEARLY) {
              if (QUARTERLY_FREQUENCY.FIRSTHALFYEAR.includes(quarterDate)) {
                quarterDate = 1
              } else if (
                QUARTERLY_FREQUENCY.SECONDHALFYEAR.includes(quarterDate)
              ) {
                quarterDate = 2
              }
            }

            currentDateValue.push(quarterDate)
          }

          currentDateValue.push(dateRange.year())

          if (
            currentDepreciationDate.toString() === currentDateValue.toString()
          ) {
            currentCol = index + 1
          }
        })
      }

      return currentCol
    },

    frequencyTypes() {
      return Object.keys(FREQUENCY_TYPES).reduce((res, freq) => {
        res[freq] = FREQUENCY_TYPES[freq].id

        return res
      }, {})
    },

    canShowMethod() {
      let { depreciation, depreciationTable, showMethod } = this

      return !isEmpty(depreciation) && !isEmpty(depreciationTable) && showMethod
    },

    depreciationMethod() {
      let {
        depreciation: { depreciationType = 1 },
      } = this

      return DEPRECIATION_TYPES[depreciationType.toString()]
    },
  },

  watch: {
    selectedDepreciationId: {
      handler(newValue, oldValue) {
        if (newValue && newValue !== oldValue) {
          this.fetchDepreciation()
        }
      },
      immediate: true,
    },
  },

  methods: {
    getDepreciation() {
      let url = 'v2/asset/depreciation/getForAsset'
      let param = { assetId: this.asset.id }

      this.loading = true
      API.post(url, param).then(({ data, error }) => {
        if (error) {
          this.$message.error(error.messsage || 'Error Occured')
        } else {
          this.depreciation = data.assetdepreciation

          if (!isEmpty(this.depreciation)) {
            let { depreciation } = this
            let { assetDepreciationRelList = [] } = depreciation || {}
            this.$emit('appliedDepreciationId', depreciation.id)
            this.$emit('depreciationRel', assetDepreciationRelList[0])
            this.fetchTableList()
          } else {
            this.loading = false
          }
        }
      })
    },

    fetchTableList() {
      let url = 'v2/asset/depreciation/chart'
      let id = this.depreciation.id
      let params = { id, assetId: this.asset.id }

      API.post(url, params).then(({ data, error }) => {
        if (error) {
          this.$emit('error', error.message)
          this.canShowMethod
            ? this.$message.error(error.message || 'Error Occured')
            : null
          this.depreciationSchedule = null
        } else {
          this.depreciationSchedule = data.depreciationList || []
        }

        this.loading = false
      })
    },

    fetchDepreciation() {
      let param = { id: this.selectedDepreciationId }

      this.loading = true
      API.fetchRecord('assetdepreciation', param).then(
        ({ assetdepreciation, error }) => {
          if (error) {
            this.$message.error(error.messsage || 'Error Occured')
            this.depreciation = null
            this.loading = false
          } else {
            this.depreciation = assetdepreciation
            this.fetchTableList()
          }
        }
      )
    },

    rowStyle(rowObject) {
      let { rowIndex } = rowObject

      if (rowIndex === 0) return 'bold-row'
      else if (rowIndex === 2) return 'border-top'
    },

    cellStyle(cellObject) {
      let { rowIndex, columnIndex } = cellObject
      let { currentDepreciationCol } = this
      let cellClass = ''

      if (columnIndex === currentDepreciationCol) {
        cellClass = 'high-light-background current-border-left-and-right'

        if (rowIndex === 0) {
          cellClass += ' current-border-top'
        } else if (rowIndex === 3) {
          cellClass += ' current-border-bottom'
        }
      }

      return cellClass
    },
  },
}
</script>
<style lang="scss">
.depreciation-list {
  .bold-row {
    td {
      font-weight: 500 !important;
      border-top: 1px solid #f2f5f6;
    }
  }
  .border-top {
    td {
      border-bottom: 1px solid #b0d9de;
    }
  }
  .high-light-background {
    background-color: #f9feff;
  }
  .current-border-top {
    border-top: solid 1px #9ed6dd !important;
  }
  .current-border-bottom {
    border-bottom: solid 1px #9ed6dd !important;
  }
  .current-border-left-and-right {
    border-left: solid 1px #9ed6dd;
    border-right: solid 1px #9ed6dd;
  }
  .lines {
    height: 23px;
    border-radius: 5px;
    margin: 12px 20px 12px 20px;
  }
  .width210px {
    width: 210px;
  }
  .mL-20 {
    margin-left: -20px;
  }
}
</style>
