<template>
  <div class="d-flex">
    <div class="pL30 pR30 d-flex height100 align-center">
      <div class="chart-container text-center">
        <gauge-chart :gaugeData="gaugeData" v-if="!loading"></gauge-chart>
        <div
          v-if="
            !$validation.isEmpty(details.minTarget) ||
              !$validation.isEmpty(details.target)
          "
        >
          <div
            v-if="
              !$validation.isEmpty(gaugeData.data.remainingValue) &&
                gaugeData.data.remainingValue >= 0
            "
            class="f13 mB20"
            style="margin-top: -15px;"
          >
            {{ $t('common.wo_report.remaining') }}:
            {{ gaugeData.data.remainingValue || '--' }}
          </div>
          <div v-else class="f13 mB20" style="margin-top: -15px;">
            {{ $t('common.wo_report.exceeded') }}:
            {{ -gaugeData.data.remainingValue || '--' }}
          </div>
        </div>
      </div>
      <div class="legend">
        <div class="legend-row f12">
          <div
            v-if="
              !$validation.isEmpty(details.minTarget) ||
                !$validation.isEmpty(details.target)
            "
            class="d-flex"
          >
            <div class="circle"></div>
            <div v-if="$validation.isEmpty(this.details.target)">
              {{ $t('common.wo_report.minimum_value') }} -
              {{ gaugeData.data.minValue }}
            </div>
            <div v-else>
              {{ $t('common.header.target_value') }} -
              {{ gaugeData.data.maxValue }}
            </div>
          </div>
        </div>
        <div class="legend-row f12">
          <div class="circle filled"></div>
          <div>
            {{ $t('common.header.current_value') }} -
            {{ gaugeData.data.baseValue }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'
import GaugeChart from '@/NewGaugeChart'

export default {
  props: ['details'],
  components: { GaugeChart },
  data() {
    return {
      loading: false,
      gaugeData: {
        data: {
          baseValue: 0,
          maxValue: !isEmpty(this.details.target) ? this.details.target : 100,
          minValue: !isEmpty(this.details.minTarget)
            ? this.details.minTarget
            : 0,
        },
        tooltipdata: [
          {
            label: 'Target',
            value: 8500,
          },
          {
            label: 'Achieved',
            value: 4000,
          },
          {
            label: 'Remaining',
            value: 4500,
          },
        ],
        config: {
          type: 1,
          startLabel: '0',
          endLabel: '100',
          colors: ['#FF728E', '#FF728E', '#FF728E', '#FF728E'],
          backgroundColors: ['#FFF', '#FF728E'],
        },
      },
    }
  },
  methods: {
    loadData() {
      let { currentValue, target, minTarget } = this.details
      let startLabel, endLabel

      currentValue = !isEmpty(currentValue) ? currentValue : 0
      minTarget = !isEmpty(minTarget) ? minTarget : 0
      target = !isEmpty(target)
        ? target
        : minTarget === 0 && currentValue === 0
        ? 100
        : minTarget < currentValue
        ? Math.ceil(parseFloat(currentValue) / 100) * 100
        : Math.ceil(parseFloat(minTarget) / 100) * 100

      if (!isEmpty(minTarget) && minTarget < currentValue)
        startLabel = String(minTarget)
      else startLabel = String(currentValue)

      if (!isEmpty(target) && target > currentValue) endLabel = String(target)
      else endLabel = String(currentValue)

      this.gaugeData = {
        ...this.gaugeData,
        data: {
          baseValue: currentValue,
          minValue: minTarget,
          maxValue: target,
          remainingValue: !isEmpty(this.details.target)
            ? parseFloat(target) - parseFloat(currentValue)
            : parseFloat(minTarget) - parseFloat(currentValue),
        },
        config: {
          ...this.gaugeData.config,
          startLabel: startLabel,
          endLabel: endLabel,
        },
      }
    },
  },
  watch: {
    details: {
      handler: function() {
        this.loadData()
      },
      immediate: true,
    },
  },
}
</script>
<style lang="scss" scoped>
.chart-container {
  max-width: 50%;
  margin: 0 auto 0 0;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}
.legend {
  text-align: center;
  display: flex;
  flex-direction: column;

  .legend-row {
    display: inline-flex;
    flex-direction: row;
    margin-bottom: 15px;
  }
  .circle {
    width: 15px;
    height: 15px;
    margin-right: 15px;
    border-radius: 50%;
    border: 1px solid;
    border-color: #e4e4e4;
    background-color: #f1f3f4;
    background-color: rgb(241, 243, 244);
    &.filled {
      border-color: #ff728e;
      background-color: #ff728e;
    }
  }
}
</style>
