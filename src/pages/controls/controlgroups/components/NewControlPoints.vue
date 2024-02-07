<template>
  <el-row>
    <el-col class="new-control-point-container">
      <el-form-item prop="controlPoints" label="Control Points" class="mB15">
        <el-table
          :data="category.controlPoints"
          ref="tableList"
          class="width100"
          :fit="true"
        >
          <el-table-column label="POINTS" align="left">
            <template v-slot="data">
              <el-select
                v-model="data.row.point"
                class="fc-input-full-border2 width100"
                placeholder="Select point"
                clearable
                value-key="id"
              >
                <el-option
                  v-for="(point, index) in controlPointsList"
                  :key="`${point.displayName}-${index}`"
                  :label="point.displayName"
                  :value="point"
                ></el-option>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="EVENT TRUE" align="left">
            <template v-slot="data">
              <div v-if="!$validation.isEmpty(data.row.point)">
                <div v-if="isBooleanOrEnumField(getPoint(data.row))">
                  <el-select
                    v-model="data.row.trueVal"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      :label="getPoint(data.row).trueVal || 'ON'"
                      :value="getPoint(data.row).trueVal"
                    ></el-option>
                    <el-option
                      :label="getPoint(data.row).falseVal || 'OFF'"
                      :value="getPoint(data.row).falseVal"
                    ></el-option>
                  </el-select>
                </div>
                <div v-else>
                  <el-input
                    style="width: 70%;text-align: left;"
                    class="fc-input-full-border2 control-action-reading-field"
                    controls-position="right"
                    :type="
                      isNumberOrDecimalField(getPoint(data.row))
                        ? 'number'
                        : 'text'
                    "
                    :disabled="$validation.isEmpty(data.row.point)"
                    v-model="data.row.trueVal"
                  >
                    <template v-if="isUnitPresent(data.row)" slot="suffix"
                      ><div style="padding-top:9px" class="font-bold">
                        {{ getPoint(data.row).unit }}
                      </div></template
                    >
                  </el-input>
                </div>
              </div>
              <div v-else>
                <el-input disabled placeholder="No Points selected"></el-input>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="EVENT FALSE" align="left">
            <template v-slot="data">
              <div v-if="!$validation.isEmpty(data.row.point)">
                <div v-if="isBooleanOrEnumField(getPoint(data.row))">
                  <el-select
                    v-model="data.row.falseVal"
                    class="fc-input-full-border-select2 width100"
                  >
                    <el-option
                      :label="getPoint(data.row).trueVal || 'ON'"
                      :value="getPoint(data.row).trueVal"
                    ></el-option>
                    <el-option
                      :label="getPoint(data.row).falseVal || 'OFF'"
                      :value="getPoint(data.row).falseVal"
                    ></el-option>
                  </el-select>
                </div>
                <div v-else>
                  <el-input
                    style="width: 70%;text-align: left;"
                    class="fc-input-full-border2 control-action-reading-field"
                    controls-position="right"
                    :type="
                      isNumberOrDecimalField(getPoint(data.row))
                        ? 'number'
                        : 'text'
                    "
                    :disabled="$validation.isEmpty(data.row.point)"
                    v-model="data.row.falseVal"
                  >
                    <template v-if="isUnitPresent(data.row)" slot="suffix"
                      ><div style="padding-top:9px" class="font-bold">
                        {{ getPoint(data.row).unit }}
                      </div></template
                    >
                  </el-input>
                </div>
              </div>
              <div v-else>
                <el-input disabled placeholder="No Points selected"></el-input>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="" width="95">
            <template v-slot="data">
              <img
                src="~assets/add-icon.svg"
                class="delete-icon pointer schedule-split-icon"
                @click="addPoint"
              />

              <img
                v-if="$getProperty(category, 'controlPoints.length') > 1"
                @click="deleteControlPoint(data.$index)"
                src="~assets/remove-icon.svg"
                class="delete-icon pointer schedule-split-icon mL15"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-col>
  </el-row>
</template>

<script>
import clone from 'lodash/clone'
import { isEmpty } from '@facilio/utils/validation'
import {
  isEnumField,
  isBooleanField,
  isNumberField,
  isDecimalField,
} from '@facilio/utils/field'
export default {
  props: ['category', 'controlPointsList'],
  data() {
    return {
      dummyPoints: ['Run Command', 'Air Temperature', 'Supply Air temperature'],
    }
  },
  computed: {
    controlPointObject() {
      let { controlPoints } = this.category
      return {
        id: isEmpty(controlPoints) ? 1 : controlPoints.length - 1,
        point: '',
        trueVal: null,
        falseVal: null,
      }
    },
  },
  methods: {
    addPoint() {
      let { controlPoints } = this.category
      let { controlPointObject } = this

      controlPoints.push(clone(controlPointObject))
    },
    deleteControlPoint(index) {
      let { controlPoints } = this.category
      controlPoints.splice(index, 1)
    },

    getPointField(row) {
      let {
        point: { field: field },
      } = row
      return field
    },
    getPoint(row) {
      let { point } = row
      if (!isEmpty(point)) return point
      else {
        let { field } = row
        return field
      }
    },
    isUnitPresent(row) {
      let {
        point: { unit },
      } = row
      return unit
    },
    isBooleanOrEnumField(point) {
      return !isEmpty(point)
        ? isBooleanField(point) || isEnumField(point)
        : false
    },

    isNumberOrDecimalField(point) {
      return !isEmpty(point)
        ? isNumberField(point) || isDecimalField(point)
        : false
    },
  },
}
</script>
<style lang="scss">
.new-control-point-container {
  .el-table td {
    border-bottom: solid 1px white;
  }
  .el-table th.is-leaf {
    border-bottom: solid 1px #dee7ef;
    border-top: solid 1px #dee7ef;
    padding-left: 10px;
  }
}
</style>
