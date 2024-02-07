<template>
  <div>
    <div class="flex-middle justify-content-space">
      <div class="fc-grey7-12 f14 text-left line-height25">
        {{ $t('analytics.custom_baseline.custom_baseline') }}
      </div>
    </div>
    <el-select
      v-model="selectedGraphPointIds"
      multiple
      placeholder="Select"
      class="fc-select-multiple-tag width100 fc-tag"
      @change="buildLineGraphPoints"
    >
      <el-option
        v-for="val in options"
        :key="val.id"
        :label="val.label"
        :value="val.id"
      >
      </el-option>
    </el-select>
    <div class="flex-middle justify-content-space">
      <el-button type="text" @click="addPointsDialog = true" class="mT5">
        {{ $t('analytics.custom_baseline.add_point') }}
      </el-button>

      <el-button type="text" @click="modifyPointsDialog = true" class="mT5">
        {{ $t('analytics.custom_baseline.modify_point') }}
      </el-button>
    </div>

    <el-dialog
      :title="$t('analytics.custom_baseline.add_baline_point')"
      :visible.sync="addPointsDialog"
      class="add-data-dialog"
      :append-to-body="true"
      width="50%"
      :show-close="false"
      style="z-index: 9999999999;"
    >
      <div class="body p20">
        <el-input
          placeholder="Name"
          v-model="name"
          class="fc-input-full-border2 mT20 mB20"
        ></el-input>
        <div class="flex-middle justify-content-space">
          <!-- <el-input placeholder="X" v-model="xValue" class="pT20 mR10" />

        <el-input placeholder="Y" v-model="yValue" class="pT20 mX10" />

        <el-button @click="addNewPoint" class="m10">Add</el-button> -->
        </div>
        <table id="table-data">
          <tr>
            <th>
              {{ $t('analytics.custom_baseline.x_value') }}
            </th>
            <th>
              {{ $t('analytics.custom_baseline.y_value') }}
            </th>
          </tr>
          <tr v-for="val in tableData" :key="val.id">
            <td>
              <input
                type="text"
                :value="val.x"
                v-on:change="
                  e => updateTableValues(e.target.value, val.id, 'x')
                "
                v-on:focus="e => updateTableValues(e.target.value, val.id, 'x')"
              />
            </td>
            <td>
              <input
                type="text"
                :value="val.y"
                v-on:change="
                  e => updateTableValues(e.target.value, val.id, 'y')
                "
                v-on:focus="e => updateTableValues(e.target.value, val.id, 'y')"
              />
            </td>
          </tr>
        </table>
      </div>

      <div class="dialog-save-cancel">
        <el-button @click="closeDialogBox" class="modal-btn-cancel">
          {{ $t('analytics.custom_baseline.cancel') }}
        </el-button>
        <el-button type="primary" @click="addPoints" class="modal-btn-save mL0">
          {{ $t('analytics.custom_baseline.done') }}
        </el-button>
      </div>
    </el-dialog>

    <el-dialog
      :title="$t('analytics.custom_baseline.modify_baline_point')"
      :visible.sync="modifyPointsDialog"
      class="add-data-dialog"
      :append-to-body="true"
      width="50%"
      :show-close="false"
      style="z-index: 9999999999;"
    >
      <div>
        <div class="body p20">
          <el-select
            v-model="modifyPointId"
            placeholder="Select"
            class="fc-input-full-border-select2 width100 fc-tag"
            @change="populateGraphPoints"
          >
            <el-option
              v-for="val in options"
              :key="val.id"
              :label="val.label"
              :value="val.id"
            >
            </el-option>
          </el-select>
          <div class="flex-middle justify-content-space">
            <el-input
              placeholder="Name"
              v-model="name"
              class="fc-input-full-border2 mT20 mB20"
            ></el-input>
            <el-button class="mL20" @click="deleteDataPoint" type="danger">
              {{ $t('analytics.custom_baseline.delete') }}
            </el-button>
          </div>

          <div class="flex-middle justify-content-space">
            <!-- <el-input placeholder="X" v-model="xValue" class="pT20 mR10" />

        <el-input placeholder="Y" v-model="yValue" class="pT20 mX10" />

        <el-button @click="addNewPoint" class="m10">Add</el-button> -->
          </div>
          <table id="table-data">
            <tr>
              <th>
                {{ $t('analytics.custom_baseline.x_value') }}
              </th>
              <th>
                {{ $t('analytics.custom_baseline.y_value') }}
              </th>
            </tr>
            <tr v-for="val in tableData" :key="val.id">
              <td>
                <input
                  type="text"
                  :value="val.x"
                  v-on:change="
                    e => updateTableValues(e.target.value, val.id, 'x')
                  "
                  v-on:focus="
                    e => updateTableValues(e.target.value, val.id, 'x')
                  "
                />
              </td>
              <td>
                <input
                  type="text"
                  :value="val.y"
                  v-on:change="
                    e => updateTableValues(e.target.value, val.id, 'y')
                  "
                  v-on:focus="
                    e => updateTableValues(e.target.value, val.id, 'y')
                  "
                />
              </td>
            </tr>
          </table>
        </div>

        <div class="dialog-save-cancel">
          <el-button @click="closeDialogBox" class="modal-btn-cancel">
            {{ $t('analytics.custom_baseline.cancel') }}
          </el-button>
          <el-button
            type="primary"
            @click="updatePoints"
            class="modal-btn-save mL0"
          >
            {{ $t('analytics.custom_baseline.done') }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { v4 } from 'uuid'
import { API } from '@facilio/api'
import colorHelper from 'newcharts/helpers/color-helper'

export default {
  name: 'FScatterGraphPoints',
  props: ['graphPoints'],
  methods: {
    addNewPoint() {
      this.tableData.push({ x: this.xValue, y: this.yValue, id: v4() })
      this.xValue = null
      this.yValue = null
    },
    buildLineGraphPoints() {
      this.selectedGraphPoints = {
        type: 'line',
        catorgy: 'baseline',
        data: this.selectedGraphPointIds,
        colors: colorHelper.newColorPicker(this.selectedGraphPointIds.length),
      }
      this.$emit('baseLineDataChanged', this.selectedGraphPoints)
    },
    populateGraphPoints() {
      let self = this
      API.get(
        '/v3/report/reading/scatter_line_graph',
        {
          scatterGraphId: parseInt(self.modifyPointId),
          scatterGraphAction: 'GET_BY_ID',
        },
        { force: true }
      )
        .then(res => {
          if (res.error) {
            this.$message.error('Error while fetching scatter line graph data')
          } else if (res.data != null) {
            self.modifyPoint = JSON.stringify({
              id: parseInt(self.modifyPointId),
              value: res.data.result.graphValue,
              label: res.data.result.label,
            })
          }
        })
        .then(() => {
          if (self.modifyPoint) {
            let name = self.options.find(
              op => op.id === JSON.parse(self.modifyPoint).id
            )
            self.name = name.label
            self.tableData = JSON.parse(JSON.parse(self.modifyPoint).value)
          }
        })
        .catch(() => {
          this.$message.error('Error occured')
        })
    },
    validateTableData() {
      let validation = true
      let validationData = [...this.tableData]
      validationData.splice(validationData.length - 1, 1)
      validationData.forEach(td => {
        if (td.x === '' || td.y === '') {
          validation = false
        }
      })

      validationData.forEach((td, idx) => {
        if (td.x === '' && td.y === '') {
          this.tableData.splice(idx, 1)
          validationData.splice(idx, 1)
        }
      })

      const lastDataPoinstValidation =
        (this.tableData[this.tableData.length - 1].x === '' &&
          this.tableData[this.tableData.length - 1].y === '') ||
        this.tableData[this.tableData.length - 1].x !== '' ||
        this.tableData[this.tableData.length - 1].y !== ''

      if (
        validation &&
        lastDataPoinstValidation &&
        (this.name !== '' || this.name !== null)
      ) {
        return true
      } else {
        this.$message.error('Name or data point is missing')
        return false
      }
    },
    updatePoints() {
      let index = this.options.findIndex(
        op => op.id === JSON.parse(this.modifyPoint).id
      )
      if (index !== -1) {
        this.modifyPointsDialog = false
        let scatter = {
          scatterGraphAction: 'MODIFY',
          scatterGraphId: JSON.parse(this.modifyPoint).id,
          scatterGraphLabel: this.name,
          scatterGraphValue: JSON.stringify(this.tableData),
        }
        API.put('/v3/report/reading/scatter_line_graph', scatter)
          .then(response => {
            if (response.error) {
              this.$$message.error('Error occured')
            } else {
              this.$message.success('Baseline updated')
              this.tableData = []
              this.name = ''
              this.tableData.push({ x: '', y: '', id: v4() })
              this.modifyPointId = null
            }
          })
          .catch(() => {
            this.$message.error('Error occured')
          })
      }
    },
    closeDialogBox() {
      this.modifyPointsDialog = false
      this.addPointsDialog = false
      this.tableData = []
      this.name = ''
      this.modifyPointId = null
      this.tableData.push({ x: '', y: '', id: v4() })
    },
    addPoints() {
      if (this.validateTableData()) {
        this.addPointsDialog = false
        API.post('/v3/report/reading/scatter_line_graph', {
          scatterGraphAction: 'ADD',
          scatterGraphLabel: this.name,
          scatterGraphValue: JSON.stringify(this.tableData),
        })
          .then(json => {
            this.$message.success('Baseline added')
            if (json.data != null) {
              let scatter = {
                id: json.data.id,
                label: this.name,
                value: json.data.id,
              }
              this.options.push(scatter)
              this.tableData = []
              this.name = ''
              this.modifyPointId = null
              this.tableData.push({ x: '', y: '', id: v4() })
            }
          })
          .catch(() => {
            this.$message.error('Error occured')
          })
      }
    },
    deleteDataPoint() {
      if (this.modifyPoint) {
        let index = this.options.findIndex(
          op => op.id === JSON.parse(this.modifyPoint).id
        )
        let selected_point_index = this.selectedGraphPointIds.findIndex(
          op => op === JSON.parse(this.modifyPoint).id
        )

        if (index !== -1) {
          API.delete('/v3/report/reading/scatter_line_graph', {
            scatterGraphId: this.options[index].id,
            scatterGraphAction: 'DELETE',
          }).then(() => {
            this.options.splice(index, 1)
            if (selected_point_index != -1) {
              this.selectedGraphPointIds.splice(selected_point_index, 1)
            }
            this.tableData = []
            this.name = ''
            this.tableData.push({ x: '', y: '', id: v4() })
            this.modifyPointId = null
            this.$message.success('Baseline is deleted')
          })
        }
        this.modifyPointsDialog = false
      }
    },
    updateTableValues(value, id, axis) {
      let validationData = [...this.tableData]
      validationData.splice(validationData.length - 1, 1)

      validationData.forEach((td, idx) => {
        if (td.x === '' && td.y === '') {
          this.tableData.splice(idx, 1)
          validationData.splice(idx, 1)
        }
      })
      const index = this.tableData.findIndex(td => td.id === id)
      if (index !== -1) {
        if (axis === 'x') {
          this.tableData[index].x = value
        } else if (axis === 'y') {
          this.tableData[index].y = value
        }
      }

      if (
        this.tableData[this.tableData.length - 1].x !== '' &&
        this.tableData[this.tableData.length - 1].y !== ''
      ) {
        this.tableData.push({ x: '', y: '', id: v4() })
      }
    },
  },
  data() {
    return {
      options: [],
      selectedGraphPoints: [],
      selectedGraphPointIds: [],
      modifyPoint: null,
      modifyPointId: null,
      addPointsDialog: false,
      modifyPointsDialog: false,
      tableData: [],
      name: null,
      xValue: null,
      yValue: null,
    }
  },
  created() {
    this.tableData.push({ x: '', y: '', id: v4() })
  },
  mounted() {
    API.get('/v3/report/reading/scatter_line_graph').then(json => {
      if (json.data != null) {
        json.data.result.forEach(dt => {
          this.options.push({ id: dt.id, label: dt.label, value: dt.id })
        })
      }
    })
    if (this.$route.query.reportId) {
      API.get(
        `/v3/report/reading/view?reportId=${this.$route.query.reportId}`
      ).then(({ data, error }) => {
        if (error) {
          this.$$message.error('Error occured')
          return
        }
        if (data.baselineData) {
          data.baselineData.forEach(op => {
            this.selectedGraphPointIds.push(op.id)
          })
        }
      })
    }
  },
}
</script>

<style lang="scss">
#table-data {
  font-family: Arial, Helvetica, sans-serif;
  border-collapse: collapse;
  width: 100%;
}

#table-data td,
#table-data th {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

#table-data th {
  padding-top: 12px;
  padding-bottom: 12px;
  background-color: #f4f4f4;
  /* background-color: #04aa6d;
  color: white; */
}
.add-data-dialog {
  input[type='text']:not(.q-input-target):not(.quick-search-input):not(.el-input__inner):not(.el-select__input) {
    border: none;
    text-align: center;
  }

  .el-dialog__body {
    padding: 0px;
  }
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .add-data-dialog > .body {
    height: 450px;
    overflow: scroll;
  }
}
</style>
