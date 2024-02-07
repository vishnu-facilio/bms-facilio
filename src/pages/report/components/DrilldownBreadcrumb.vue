<template>
  <div class="drilldown-breadcrumb  p5 mL90 d-flex">
    <div class="crumb-item">
      <span
        @click="handleClick(-1)"
        class="f12 bold breadcrumb-text mR5 letter-spacing0_5 "
      >
        {{ $t('reportdrilldown.all') }}
      </span>
      <i class="el-icon-arrow-right f12 mR3"></i>
    </div>

    <div
      class="crumb-item"
      v-for="(drilldownCriteria,
      stepIndex) in drilldownParams.drilldownCriteria"
      :key="stepIndex"
      :class="[
        {
          'is-last': stepIndex == drilldownParams.drilldownCriteria.length - 1,
        },
      ]"
    >
      <span
        @click="handleClick(stepIndex)"
        :key="'crumb-text' + stepIndex"
        class="f12 bold breadcrumb-text mR5 letter-spacing0_5 "
        :class="[
          {
            'is-last':
              stepIndex == drilldownParams.drilldownCriteria.length - 1,
          },
        ]"
      >
        {{ drilldownCriteria.breadcrumbLabel }}</span
      >

      <!-- for last option show a close icon instead of Right arro -->
      <i
        :key="'crumb-icon' + stepIndex"
        v-if="stepIndex != drilldownParams.drilldownCriteria.length - 1"
        class="el-icon-arrow-right f12 mR3"
      ></i>

      <i
        @click="handleClick(stepIndex - 1)"
        v-else
        class="el-icon-circle-close pointer f12 hide-v"
        :key="'crumb-close' + stepIndex"
      ></i>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    drilldownParams: {
      type: Object,
      default: null,
    },
  },
  data() {
    return {}
  },
  methods: {
    handleClick(
      crumbIndex // item 'All' has Index=-1 ,indicating remove all crumb steps
    ) {
      //ignore click on last item
      if (crumbIndex != this.drilldownParams.length - 1) {
        this.$emit('crumbClick', crumbIndex)
      }
    },
  },
}
</script>

<style lang="scss">
.drilldown-breadcrumb {
  .crumb-item.is-last:hover {
    .el-icon-circle-close {
      visibility: visible;
    }
  }

  .breadcrumb-text {
    color: #000000;

    &:not(.is-last) {
      cursor: pointer;
      &:hover {
        color: #ff3184;
      }
    }
  }
}
</style>
