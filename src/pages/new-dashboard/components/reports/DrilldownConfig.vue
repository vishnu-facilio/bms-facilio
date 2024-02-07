<template>
  <div class="drilldown-config">
    <el-dialog
      :visible="visibility"
      class="drilldown-config-dialog"
      :show-close="false"
      :append-to-body="true"
      :title="$t('reportdrilldown.configure')"
      width="60%"
    >
      <div class="body">
        <div class="title mB10">
          {{ $t('reportdrilldown.clickAction') }}
        </div>
        <el-select
          class="fc-input-full-border-h35 mB20 width200px "
          v-model="reportSettingsModel.clickAction"
        >
          <el-option v-bind:value="1" :label="$t('reportdrilldown.none')">
          </el-option>
          <el-option v-bind:value="2" :label="$t('reportdrilldown.list')">
          </el-option>
          <el-option v-bind:value="3" :label="$t('reportdrilldown.drilldown')">
          </el-option>
          <el-option v-bind:value="4" label="Dashboard Action">
          </el-option>
        </el-select>
        <div
          class="path-config-cont "
          v-if="reportSettingsModel.clickAction === 3"
        >
          <div class="option-config-separator"></div>
          <div class="title mB20 mT20">
            {{ $t('reportdrilldown.path') }}
          </div>
          <drill-step-select
            v-for="(drillStep, drillStepIndex) in reportDrilldownPathModel"
            :key="drillStepIndex"
            v-model="reportDrilldownPathModel[drillStepIndex]"
            :dimensions="isDimensionTime ? timeDimensions : nonTimeDimensions"
            :class="[
              {
                'is-last':
                  drillStepIndex == reportDrilldownPathModel.length - 1,
              },
            ]"
            @add="addDrillStep"
            @remove="removeDrillStep"
          >
          </drill-step-select>
        </div>
      </div>

      <div class="dialog-save-cancel">
        <el-button class="modal-btn-cancel" @click="closeDialog">
          {{ $t('common._common.cancel') }}</el-button
        >
        <el-button type="primary" class="modal-btn-save mL0" @click="save">{{
          $t('common._common.done')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deepCloneObject } from 'util/utility-methods'
import DrillStepSelect from './DrillStepSelect'

export default {
  components: { DrillStepSelect },
  props: [
    'visibility',
    'reportSettings',
    'reportDrilldownPath',
    'initialDimensionConfig',
    'timeAggregators',
    'isDimensionTime',
    'moduleResourceField',
    'reportModuleId', //fill formula fields moduleId with this
    'dateField',
  ],

  computed: {
    //For space fields field ID is same but xAgg field{id:123123,value:26} differs and for other fields aggregation is null/0 but field ID differs
    //so construct a unique identifier fieldId_xAgg and use it as v-model key
    nonTimeDimensions() {
      let temp = deepCloneObject(this.initialDimensionConfig)
      temp = temp.filter(e => e.displayName != 'Time')
      temp.forEach(dimension => {
        if (dimension.displayName == 'Space' && this.moduleResourceField) {
          dimension.subFields.forEach(field => {
            if (!field.id) {
              //only space category field has an id, for others use resource field id AS ID
              field.id = this.moduleResourceField.id
              field.moduleId = this.moduleResourceField.moduleId
            }
          })
        } else {
          dimension.subFields.forEach(field => {
            if (field.id == -1) {
              //formula fields
              field.id = field.name
              field.moduleId = this.reportModuleId
            }
          })
        }
      })

      temp.forEach(dimension => {
        dimension.subFields.forEach(field => {
          let xAgg = field.value && field.value > -1 ? field.value : 0
          field.fieldId_xAggr = field.id + '_' + xAgg
        })
      })
      return temp
    },
    timeDimensions() {
      //for report with X-Time axis ,show aggregators as options

      if (this.isDimensionTime && this.dateField) {
        let temp = deepCloneObject(this.timeAggregators)

        temp.forEach(timeAggr => {
          let xAggr = timeAggr.value && timeAggr.value > -1 ? timeAggr.value : 0

          timeAggr.id = this.dateField.field_id
          timeAggr.moduleId = this.dateField.module_id

          timeAggr.fieldId_xAggr = timeAggr.id + '_' + xAggr
        })

        let dimensions = [
          {
            displayName: 'Time',
            subFields: temp,
          },
        ]
        return dimensions
      }
      return null
    },
  },

  created() {
    this.reportSettingsModel = deepCloneObject(this.reportSettings)

    if (!this.reportDrilldownPath || this.reportDrilldownPath.length == 0) {
      //add first drill step if no path configured
      this.addDrillStep()
    } else {
      this.reportDrilldownPathModel = deepCloneObject(this.reportDrilldownPath)

      this.reportDrilldownPathModel.forEach(drillStep => {
        //zero x aggr doesn't get saved in ENUM BMSaggragate operator field
        if (drillStep.xAggr < 0) {
          drillStep.xAggr = 0
        }
        drillStep.fieldId_xAggr =
          drillStep.xField.field_id + '_' + drillStep.xAggr
      })
    }
  },
  data() {
    return {
      reportDrilldownPathModel: [],
      reportSettingsModel: null, //temp editing state. SAVE/SYNC THE original prop after SAVE CLICKED ONLY
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    save() {
      console.log('saving drilldown config')
      this.$emit('update:reportSettings', this.reportSettingsModel)
      this.$emit('update:reportDrilldownPath', this.reportDrilldownPathModel)
      this.$emit('save')
    },
    // serialize(reportSettingsModel)
    // {
    //   // TO DO , send only required props . FIX data type wrong at source
    //     reportSettingsModel.forEach(e=>{
    //       if(e.xField&&!isNaN(e.xField.field_id))
    //       {

    //           e.xField.field_id=parseInt(e.xField.field_id)

    //       }
    //     }

    //     )
    //     return reportSettingsModel
    // },
    removeDrillStep() {
      this.reportDrilldownPathModel.splice(
        this.reportDrilldownPathModel.length - 1,
        1
      )
    },
    addDrillStep() {
      //copy option 1 and add
      let firstOption = null
      if (this.isDimensionTime) {
        firstOption = this.timeDimensions[0].subFields[0]
      } else {
        firstOption = this.nonTimeDimensions[0].subFields[0]
      }
      let field_id = firstOption.fieldId_xAggr.split('_')[0]
      if (!isNaN(field_id)) {
        field_id = parseInt(field_id)
      }
      let xAggr = parseInt(firstOption.fieldId_xAggr.split('_')[1])

      let module_id = firstOption.moduleId
      let drillStep = {
        fieldId_xAggr: firstOption.fieldId_xAggr,
        xAggr,
        xField: {
          field_id,
          module_id,
        },
      }
      this.reportDrilldownPathModel.push(drillStep)
    },
  },
}
</script>
<style lang="scss">
.drilldown-config-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #eff1f4;
  }
  .el-dialog__body {
    padding: 0px;
  }
  .body {
    height: 500px;
    padding: 25px;
  }

  .title {
    height: 14px;
    font-size: 14px;
    letter-spacing: 0.5px;
    color: #6b7e91;
  }
  .option-config-separator {
    height: 1px;
    border-bottom: solid 1px #eff1f4;
  }
}
</style>
