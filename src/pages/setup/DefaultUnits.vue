<template>
  <div class="height100">
    <div class="setting-header2">
      <div class="setting-title-block">
        <div class="setting-form-title">Default Units</div>
        <div class="heading-description">List of all Units</div>
      </div>
    </div>
    <div class="row p30 setting-Rlayout mT20">
      <div class="col-lg-12 col-md-12">
        <table class="setting-list-view-table">
          <thead>
            <tr>
              <th class="setting-table-th setting-th-text">
                PARAMETER
              </th>
              <th class="setting-table-th setting-th-text">
                UNITS
              </th>
            </tr>
          </thead>
          <tbody>
            <tr
              class="tablerow"
              v-for="(orgUnit, index) in orgUnitsList"
              :key="index"
            >
              <td>{{ orgUnit.metricEnum ? orgUnit.metricEnum.name : '' }}</td>
              <td>
                <el-popover width="300" trigger="click">
                  <div slot="reference">
                    {{ getUnitKey(orgUnit).displayName }} (<span
                      v-html="getUnitKey(orgUnit).symbol"
                    ></span
                    >) <i class="el-icon-arrow-down"></i>
                  </div>
                  <div
                    class="q-item"
                    v-for="(unit, index) in metricWithUnits[
                      orgUnit.metricEnum._name
                    ]"
                    v-if="unit.unitId !== orgUnit.unit"
                    @click="updateUnit(orgUnit, unit)"
                    :key="index"
                  >
                    {{ unit.displayName }} (<span v-html="unit.symbol"></span>)
                  </div>
                </el-popover>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  title() {
    return 'DefaultUnits'
  },
  data() {
    return {
      orgUnitsList: [],
      metricWithUnits: {},
    }
  },
  mounted() {
    this.loadUnits()
  },
  methods: {
    loadUnits: function() {
      let self = this
      self.$http
        .get('/units/getDefaultMetricUnits')
        .then(function(response) {
          self.orgUnitsList = response.data.orgUnitsList
          self.metricWithUnits = response.data.metricWithUnits
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    updateUnit: function(metric, unit) {
      let self = this
      self.$http
        .post('/units/updatedefaultMetricUnit', {
          metric: metric.metric,
          unit: unit.unitId,
        })
        .then(function(response) {
          self.orgUnitsList.map(el => {
            if (el.metric === metric.metric) {
              el.unit = unit.unitId
            }
            return el
          })
          self.$message.success('Unit updated successfully!')
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    getUnitKey: function(orgUnit) {
      return this.metricWithUnits[orgUnit.metricEnum._name].find(el => {
        if (el.unitId === orgUnit.unit) {
          return el
        }
      })
    },
  },
}
</script>

<style></style>
