<template>
  <div class="marker-tooltip">
    <div
      class="desk-tooltip"
      v-if="
        tooltipData.markerType === 'DESK' ||
          tooltipData.markerType === 'SPACE' ||
          tooltipData.markerType === 'MODULE_LINKED'
      "
    >
      <div class="tooltip-header d-flex">
        <InlineSvg
          v-if="tooltipData.markerType == 'DESK'"
          class="mR5 mT1"
          src="svgs/indoorFloorPlan/office_desk"
          iconClass="icon icon-sm-md"
          inonStyle="background:#ffffff;fill:#ffffff;"
        ></InlineSvg>

        <InlineSvg
          v-else-if="tooltipData.markerType == 'SPACE'"
          class="mR5 mT1"
          src="svgs/indoorFloorPlan/room"
          iconClass="icon icon-sm-md"
          inonStyle="background:#ffffff;fill:#ffffff;"
        ></InlineSvg>
        <!--
        <img
          v-else-if="tooltipData.icon"
          style="width: 20px; height: 20px"
          :src="pathIcon(tooltipData.icon)"
          class="vertical-middle"
        /> -->
        {{ tooltipData.label }}
      </div>
      <div class="tooltip-body">
        <div
          class="tooltip-row "
          v-if="
            tooltipData.markerType === 'MODULE_LINKED' && tooltipData.recordId
          "
        >
          <div class="tooltip-value">
            {{ `# ${tooltipData.recordId}` }}
          </div>
        </div>
        <div
          class="tooltip-row "
          v-if="tooltipData.desk.typeInt == 1"
          :class="[{ 'fp-unassigned-employee': !tooltipData.employee.name }]"
        >
          <InlineSvg
            class="mR15 mT2"
            src="svgs/indoorFloorPlan/employee"
            iconStyle="opacity:.2;"
            iconClass="icon icon-sm"
          ></InlineSvg>
          <div class="tooltip-value">
            {{
              tooltipData.employee.name
                ? tooltipData.employee.name
                : 'Unassigned'
            }}
          </div>
        </div>
        <div class="tooltip-row " v-if="tooltipData.desk.department">
          <InlineSvg
            class="mR15 mT2"
            iconStyle="opacity:.2;"
            src="svgs/indoorFloorPlan/department"
            iconClass="icon icon-sm"
          ></InlineSvg>
          <div class="tooltip-value">
            {{ tooltipData.desk.department }}
          </div>
        </div>
        <div
          class="tooltip-row "
          v-if="tooltipData.desk.type || tooltipData.space.category"
        >
          <InlineSvg
            class="mR15 mT2"
            src="svgs/indoorFloorPlan/desk_type"
            iconStyle="opacity:.2;"
            iconClass="icon icon-sm"
          ></InlineSvg>
          <div class="tooltip-value">
            {{ tooltipData.desk.type || tooltipData.space.category }}
          </div>
        </div>
        <template v-if="tooltipData.markerType == 'SPACE'">
          <div class="tooltip-row " v-if="showreservable">
            <i class="el-icon-date mR15 mT2"> </i>
            <div class="tooltip-value">
              {{
                tooltipData.space.reservable ? 'Reservable' : 'Non Reservable'
              }}
            </div>
          </div>
        </template>
      </div>
    </div>
    <div class="static-tooltip" v-else-if="tooltipData.markerType == 'STATIC'">
      <div>
        {{ tooltipData.label }}
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['tooltipData'],
  computed: {
    showreservable() {
      let { tooltipData } = this
      if (tooltipData && tooltipData.space && tooltipData.space.category) {
        if (tooltipData.space.category === 'Lockers') {
          return false
        } else if (tooltipData.space.category === 'Parking Stall') {
          return true
        } else {
          return true
        }
      }
      return false
    },
  },
  methods: {
    pathIcon(icon) {
      return require(`statics/floorplan/${icon}`)
    },
  },
}
</script>

<style lang="scss">
.marker-tooltip {
  overflow: hidden;

  .desk-tooltip {
    border-radius: 10px;
    overflow: hidden;
    min-width: 200px;
    max-width: 240px;
    background: #ffffff;
    box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.15);

    .tooltip-header {
      text-align: center;
      background-color: #39b3c2;
      font-size: 14px;
      color: #ffffff;
      padding: 10px 15px;
      font-weight: 500;
      letter-spacing: 1px;
    }
    .tooltip-body {
      background-color: #ffffff;
      color: #000000;
      letter-spacing: 0.6px;
      font-size: 12px;
      padding: 12px;
    }
    .tooltip-row {
      display: flex;
      margin-bottom: 3px;
      .el-icon-date {
        // margin-top: 3px;
        opacity: 0.4;
        font-size: 14px;
      }
    }
    .fp-unassigned-employee {
      color: #727272;
      font-style: italic;
    }
    .tooltip-value {
    }
  }
  .static-tooltip {
    border: 1px solid #efefef;
    border-bottom: none;
    border-radius: 5px;
    text-align: center;
    min-width: 120px;
    overflow: hidden;
    background: #ffffff;
    // box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.15);
    color: #000000;
    font-size: 11px;
    letter-spacing: 0.6px;
    padding: 10px;
  }
}
</style>
