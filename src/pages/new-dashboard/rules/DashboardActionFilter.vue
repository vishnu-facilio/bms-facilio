<template>
  <div>
    <el-row>
      <el-col :span="14">
        <el-form-item prop="fieldId" label="Target Widget" class="mB10">
          <el-select
            v-model="selectedWidgetList"
            placeholder="Select the field"
            class="fc-input-full-border2 width100 fc-tag"
            filterable
            @change="loadWidgetDetails"
            @visible-change="dropDownVisibiltyChanged"
            :multiple="true"
            collapse-tags
          >
            <el-option
              v-for="(widget, type) in widgets"
              :key="`actionType-${type}`"
              :label="widget.label"
              :value="widget"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>
    <div class="filter-criteri-builder">
      <div v-if="loadingCriteriaBuilder" class="flex-middle height-100">
        <spinner :show="loadingCriteriaBuilder" size="80"></spinner>
      </div>
      <div v-for="(widget, index) in recordWidget" :key="index">
        <el-form>
          <el-form-item class="widget-criteria-label" :label="widget.label">
          </el-form-item>
          <div v-if="widget.reportType === 'READING_REPORT'">
            <el-form-item
              v-for="(dataPoint, dataPointIndex) in widget.dataPoints"
              :key="dataPointIndex"
              prop="fieldId"
              :label="`${dataPoint.display_label}`"
              class="mB10"
            >
              <!-- <NewCriteriaBuilder
              v-if="criteriaBuilderloaded"
              v-model="dashboardRules['criteria' + widget.value]"
              @condition="newVal => updateCriteria(newVal, widget.value)"
              lookupFieldModuleName="vendor"
              :lookupModuleFieldsList="lookupModuleFieldsList"
              :module="widget.module"
              title="Select critirea for rules"
            /> -->
              <CriteriaBuilder
                :moduleName="dataPoint.module"
                :key="`${index}, ${dataPointIndex}`"
                v-model="
                  dashboardRules[
                    `criteria_${widget.value}_dataPoint_${dataPoint.label}`
                  ]
                "
                :showValueType="true"
                :customValues="customValues"
              >
              </CriteriaBuilder>
            </el-form-item>
          </div>
          <div v-else>
            <el-form-item prop="fieldId" class="mB10">
              <CriteriaBuilder
                :moduleName="widget.module"
                v-model="dashboardRules['criteria_' + widget.value]"
                :showValueType="true"
                :key="`${index}`"
                :customValues="customValues"
              >
              </CriteriaBuilder>
            </el-form-item>
          </div>
        </el-form>
      </div>
    </div>
    <span slot="footer" class="modal-dialog-footer row">
      <el-button class="col-6 modal-btn-cancel" @click="close()"
        >Cancel</el-button
      >

      <el-button
        type="primary"
        class="col-6 modal-btn-save"
        @click="checkCriteriaValidation()"
        >Ok</el-button
      >
    </span>
  </div>
</template>
<script>
import { API } from '@facilio/api'
import { isEmpty } from '@facilio/utils/validation'
import { CriteriaBuilder } from '@facilio/criteria'
import { cloneDeep } from 'lodash'
export default {
  props: {
    widgets: {},
    presentAction: {},
    preExistingRule: { type: Boolean, default: false },
    triggerWidgetPlaceholders:{}
  },
  components: {
    CriteriaBuilder,
  },
  computed: {},
  data() {
    return {
      dummy: {},
      criteriaBuilderloaded: true,
      dashboardRules: {},
      lookupModuleFieldsList: [],
      selectedWidgetList: null,
      showwidget: false,
      recordWidget: null,
      loadingCriteriaBuilder: false,
      isSaved: false,
      filterActionList: {
        type: 1,
        actionType: 'filter',
        action_meta: {
          action_detail: {},
        },
        target_widgets: null,
      },
      customValues: [
        {
          allowCreate: true,
          options: [],
          label: 'Field',
          value: 'fields',
          matcher: value => {
            let regexArray = String(value).match(/\$\{(.*?)\}/)
            if (!isEmpty(regexArray)) return true
            return false
          },
        },
      ],
    }
  },
  watch: {
    recordWidget(widgets) {
      const rules = {}
      widgets.forEach(widget => {
        const { dataPoints } = widget
        if (!isEmpty(dataPoints)) {
          dataPoints.forEach(dataPoint => {
            const key = `criteria_${widget.value}_dataPoint_${dataPoint.label}`
            if (!isEmpty(this.dashboardRules[key])) {
              rules[key] = cloneDeep(this.dashboardRules[key])
            } else {
              rules[key] = {}
            }
          })
        } else {
          const key = `criteria_${widget.value}`
          if (!isEmpty(this.dashboardRules[key])) {
            rules[key] = cloneDeep(this.dashboardRules[key])
          } else {
            rules[key] = {}
          }
        }
      })
      this.$set(this, 'dashboardRules', cloneDeep(rules))
    },
  },
  created() {
    if(!isEmpty(this.triggerWidgetPlaceholders)){
      this.customValues[0].options=this.triggerWidgetPlaceholders
    }
    // this.getLookupModuleFieldsList()
    if (!isEmpty(this.presentAction)) {
      if (!isEmpty(this.presentAction.target_widgets)) {
        this.isSaved = true

        const getWidgets = () => {
          const {
            presentAction: { target_widgets: targetWidgets },
          } = this
          if (this.preExistingRule) {
            const widgets = []
            targetWidgets
              .map(targetWidget => {
                return this.widgets.find(
                  widget => targetWidget.target_widget_id === widget.value
                )
              })
              .forEach(widget => {
                if (widgets.indexOf(widget) == -1) {
                  widgets.push(widget)
                }
              })
            return widgets
          } else {
            return targetWidgets.map(targetWidget => {
              return this.widgets.find(
                widget => targetWidget.target_widget_id === widget.value
              )
            })
          }
        }
        this.selectedWidgetList = getWidgets()
        this.filterActionList = cloneDeep(this.presentAction)
        if (this.filterActionList.id === undefined) {
          this.filterActionList.id = -1
        }
        this.loadWidgetDetails()
        this.fillvalue()
      }
    }
  },
  methods: {
    updateCriteria(newVal, value) {
      this.dashboardRules['criteria' + value] = newVal
    },
    // async getLookupModuleFieldsList() {
    //   let {
    //     data: { modules },
    //   } = await API.get('/v2/report/getDataModuleList', {
    //     moduleName: 'workorder',
    //   })
    //   this.lookupModuleFieldsList = modules
    //   console.log(this.lookupModuleFieldsList)
    // },
    async loadWidgetDetails() {
      this.loadingCriteriaBuilder = true
      let promises = this.selectedWidgetList.map(async widget => {
        console.log(widget)
        if ('reportId' in widget && widget?.reportType !== 'READING_REPORT') {
          console.log('if')
          let { data } = await API.get('v3/report/execute', {
            reportId: widget.reportId,
          })
          let {
            module,
            report: {
              dataPoints: [
                {
                  xAxis: {
                    dataTypeEnum,
                    module: { fields },
                    field: { lookupModule },
                  },
                },
              ],
            },
          } = data || {}
          let list = {}
          if (dataTypeEnum == 'LOOKUP') {
            list.module = lookupModule.name
          } else {
            list.module = module.name
          }
          list.label = widget.label
          list.value = widget.value
          if (!isEmpty(fields)) {
            this.lookupModuleFieldsList = fields
          }
          return list || null
        } else if (
          'reportId' in widget &&
          widget?.reportType === 'READING_REPORT'
        ) {
          console.log('else if')
          let { data } = await API.put(
            'v3/report/reading/view?reportId=' + widget?.reportId,
            {
              newFormat: true,
            }
          )
          const {
            report: { dataPoints },
          } = data
          const getDataPoint = () => {
            return dataPoints.map(dataPoint => {
              console.log(dataPoint)
              return {
                moduleName: dataPoint.xAxis.moduleName,
                parentModuleName: 'asset', // Hardcode.
                label: dataPoint.aliases.actual,
                display_label: dataPoint.name,
                module: 'asset',
              }
              // const obj = {}
              // obj.moduleName
              // const {
              //   yAxis: { moduleName },
              // } = datapoint
              // module = 'asset'
              // return data
              // const { data } = await API.get('v2/state/list?parentModuleName=' + 'asset')
              // console.log('data', data)
              // return data
            })
          }
          return {
            label: widget.label,
            reportType: 'READING_REPORT',
            dataPoints: getDataPoint(),
            value: widget.value,
          }
        } else {
          console.log('else')
          return widget
          // let {data} = await API.get('/v2/readings/getSubModuleRel?moduleName=mvproject')
        }
      })
      this.recordWidget = await Promise.all(promises)
      console.log('this.recordWidget', this.recordWidget)
      this.loadingCriteriaBuilder = false
    },
    fillvalue() {
      this.criteriaBuilderloaded = false
      const getTriggerWidgets = () => {
        const {
          presentAction: { target_widgets: targetWidgets },
        } = this
        if (this.preExistingRule) {
          const widgets = []
          const getWidget = widgetId => {
            return widgets.find(widget => {
              return widget.target_widget_id == widgetId
            })
          }
          targetWidgets.forEach(targetWidget => {
            const {
              criteria,
              moduleName = 'asset',
              target_widget_id,
              actionId,
            } = targetWidget
            if ('dataPointMeta' in targetWidget) {
              const {
                dataPointMeta: { datapoint_link, parentModuleName },
              } = targetWidget
              const widget = getWidget(target_widget_id)
              if (widget) {
                widget.dataPointList.push({
                  datapoint_link,
                  criteria,
                  parentModuleName,
                  moduleName,
                })
              } else {
                widgets.push({
                  actionId,
                  target_widget_id,
                  dataPointList: [
                    {
                      datapoint_link,
                      criteria,
                      parentModuleName,
                      moduleName,
                    },
                  ],
                })
              }
            } else {
              widgets.push({
                criteria,
                actionId,
                moduleName,
                target_widget_id,
              })
            }
          })
          return widgets
        }
        return targetWidgets
      }
      const rules = {}
      const triggerWidgets = getTriggerWidgets()
      triggerWidgets.forEach(targetWidget => {
        if ('dataPointList' in targetWidget) {
          const { dataPointList, target_widget_id } = targetWidget
          dataPointList.forEach(dataPoint => {
            const { criteria = {}, datapoint_link } = dataPoint
            const key = `criteria_${target_widget_id}_dataPoint_${datapoint_link}`
            rules[key] = cloneDeep(criteria)
          })
        } else {
          const { criteria = {}, target_widget_id } = targetWidget
          const key = `criteria_${target_widget_id}`
          rules[key] = cloneDeep(criteria)
        }
      })
      this.dashboardRules = cloneDeep(rules)
      this.$nextTick(() => {
        this.criteriaBuilderloaded = true
      })
    },
    dropDownVisibiltyChanged(val) {
      if (!val && !isEmpty(this.recordWidget)) {
        this.showwidget = true
      }
    },
    isValidCriteria(criteria) {
      if (!criteria.conditions) {
        return false
      }
      let conditions = criteria.conditions
      for (const key in conditions) {
        if (Number.isInteger(Number.parseInt(key))) {
          let condition = conditions[key]
          if (
            isEmpty(condition) ||
            isEmpty(condition.operatorId) ||
            isEmpty(condition.fieldName)
          ) {
            return false
          }
        }
      }
      return true
    },
    checkCriteriaValidation() {
      let isAllValid = true
      outer: for (let j = 0; j < this?.recordWidget?.length; j++) {
        const widget = this.recordWidget[j]
        const { dataPoints } = widget
        if (!isEmpty(dataPoints)) {
          for (let i = 0; i < dataPoints.length; i++) {
            const dataPoint = dataPoints[i]
            const key = `criteria_${widget.value}_dataPoint_${dataPoint.label}`
            const criteria = this.dashboardRules[key]
            if (isEmpty(criteria)) {
              continue
            }
            const isValidCriteria = this.isValidCriteria(criteria)
            if (!isValidCriteria) {
              this.$message.error(
                `Please add valid criteria for widget ${widget.label} and data point ${dataPoint.label}`
              )
              isAllValid = false
              break outer
            }
          }
        } else {
          const key = `criteria_${widget.value}`
          const criteria = this.dashboardRules[key]
          if (isEmpty(criteria)) {
            continue
          }
          const isValidCriteria = this.isValidCriteria(this.dashboardRules[key])
          if (!isValidCriteria) {
            this.$message.error(
              `Please add valid criteria for the widget ${widget.label}.`
            )
            isAllValid = false
            break outer
          }
        }
      }
      if (isAllValid) {
        this.save()
      }
    },
    save() {
      const filterActionList = cloneDeep(this.filterActionList)
      if (!isEmpty(this.recordWidget)) {
        filterActionList.target_widgets = this.recordWidget.map(widget => {
          const target = {}
          const { dataPoints } = widget
          if (!isEmpty(dataPoints)) {
            target['dataPointList'] = dataPoints
              .map(dataPoint => {
                const obj = {}
                obj['datapoint_link'] = dataPoint.label
                const key = `criteria_${widget.value}_dataPoint_${dataPoint.label}`
                const criteria = this.dashboardRules[key]
                if (isEmpty(criteria)) {
                  return false
                }
                obj['criteria'] = this.dashboardRules[key]
                obj['parentModuleName'] = dataPoint.moduleName
                obj['moduleName'] = 'asset'
                return obj
              })
              .filter(obj => obj !== false)
          } else {
            const key = `criteria_${widget.value}`
            const criteria = this.dashboardRules[key]
            if (isEmpty(criteria)) {
              return false
            }
            target.criteria = this.dashboardRules[key]
          }
          target.actionId = -1
          target.target_widget_id = widget.value
          target.moduleName = widget.module
          return target
        })
      } else {
        filterActionList.target_widgets = null
      }
      this.$emit('actions', filterActionList, {})
    },
    close() {
      let { filterActionList } = this || {}
      let { target_widgets } = filterActionList
      if (
        // !isEmpty(this.recordWidget) &&
        !isEmpty(target_widgets) &&
        this.isSaved
      ) {
        this.$emit('closeFilter', true)
      } else this.$emit('closeFilter', false)

      this.isSaved = false
    },
  },
}
</script>
<style scoped lang="scss">
.filter-criteri-builder {
  height: 281px;
  overflow-y: scroll;
}
</style>
<style lang="scss">
.widget-criteria-label {
  margin: 0px;
  .el-form-item__label {
    font-weight: bold;
    margin: 0px;
  }
}
.filter-criteri-builder {
  .el-form-item__label {
    margin-top: 17px;
  }
}
</style>
