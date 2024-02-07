<template>
  <div class="graphic-elements">
    <el-tabs type="card" v-model="activeTab" @tab-click="loadAssetReadings">
      <el-tab-pane label="Markers" name="markers">
        <span slot="label">Markers</span>
        <!-- <el-input
          placeholder="Filter shapes"
          class="fc-input-full-border2 shape-filter-box"
          suffix-icon="el-icon-search"
          v-model="filterShapeInput"
        ></el-input> -->
        <el-collapse v-model="activeSelections">
          <template v-for="(marker, index) in markers">
            <el-collapse-item
              :key="index"
              :title="marker.name"
              :name="marker.name"
              class="fc-graphic-legend-collapse pointer"
            >
              <div
                v-for="(icon, idx) in marker.icons"
                :key="idx"
                class="marker-drag-icon"
              >
                <div
                  class="p10 width100 row"
                  draggable="true"
                  @dragstart="dragStart(icon)"
                  @dragend="dragEnd"
                >
                  <div class="col-3">
                    <inline-svg
                      :src="icon.path"
                      class="vertical-middle fp-markers p0"
                      iconClass="icon icon-xlg"
                    ></inline-svg>
                  </div>
                  <div class="col-5 self-center">
                    <span>
                      {{ icon.name }}
                    </span>
                  </div>
                </div>
              </div>
            </el-collapse-item>
          </template>
        </el-collapse>
      </el-tab-pane>
      <!-- <el-tab-pane label="Variables" name="variables">
        <span slot="label">Variables</span>
        <el-input
          placeholder="Filter variables"
          class="fc-input-full-border2 shape-filter-box"
          suffix-icon="el-icon-search"
          v-model="filterShapeInput"
        ></el-input>
        <el-collapse v-model="activeSelections1">
          <el-collapse-item
            v-for="(variableGroup, index) in filteredVariables"
            :key="index"
            :title="variableGroup.label"
            :name="variableGroup.key"
            class="fc-graphic-legend-collapse"
          >
            <div class="text-center" v-if="variableGroup.key === 'custom'">
              <reading-selector @add="onAddCustomVariable">
                <el-button type="text" size="small">
                  <i class="el-icon-plus"></i>Add Custom Variable
                </el-button>
              </reading-selector>
              <br />
              <el-button
                type="text"
                size="small"
                class="mB10"
                @click="openCustomScript()"
              >
                <i class="el-icon-plus"></i>Add Custom Script
              </el-button>
            </div>
            <div
              v-for="(field, j) in variableGroup.fields"
              :key="j"
              class="analytics-data-point cursor-drag"
              draggable="true"
              @dragstart="dragStart(field)"
              @dragend="dragEnd"
              style="display: flex;"
            >
              <div class="data-points-drag-block">
                <div class="data-points-checkbox">
                  <img src="~assets/drag-blue.svg" />
                  <div
                    class="label-txt-black pL10 pR10"
                    @click="editVariable(field)"
                  >
                    {{ field.label }}
                  </div>
                </div>
                <div
                  class="data-points-settings-icon-set"
                  v-if="variableGroup.key === 'custom'"
                >
                  <i class="el-icon-delete" @click="removeVariable(field)"></i>
                </div>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane> -->
    </el-tabs>
    <el-dialog
      :visible.sync="customScript.showDialog"
      width="60%"
      class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
      title="script"
      :append-to-body="true"
      top="0%"
    >
      <div class="p20" v-if="customScript.config">
        <el-row>
          <el-col :span="12">
            <el-input
              placeholder="Variable Name"
              v-model="customScript.config.variableName"
              class="fc-input-full-border2"
            ></el-input>
          </el-col>
        </el-row>
        <el-row class="mT20" :gutter="20">
          <el-col :span="16">
            <div
              style="height: 352px; margin-bottom: 65px; border-radius: 3px !important; background-color: #ffffff; border: solid 1px #d0d9e2 !important;"
            >
              <code-mirror
                ref="cmirror"
                :codeeditor="true"
                v-model="customScript.config.workflowScript"
              ></code-mirror>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="bold p5">Refer Variables</div>
            <el-input
              placeholder="Filter variables"
              v-model="customScript.filterVariablesInput"
              class="p5"
            ></el-input>
            <div class="graphics-refer-variables-container">
              <ul class="graphics-refer-variables">
                <li
                  v-for="(varObj, index) in variables.filter(
                    v =>
                      v.fetchType !== 'function' &&
                      (!customScript.filterVariablesInput ||
                        v.label
                          .toLowerCase()
                          .indexOf(
                            customScript.filterVariablesInput.toLowerCase()
                          ) >= 0)
                  )"
                  :key="index"
                  @click="$refs.cmirror.addCode(varObj.key)"
                >
                  {{ varObj.label }}
                </li>
              </ul>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer z-index9">
        <el-button @click="closeCustomScript" class="modal-btn-cancel"
          >CANCEL</el-button
        >
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="addCustomScript"
          :loading="customScript.saving"
          >{{ customScript.saving ? 'Saving...' : 'Save' }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import CodeMirror from '@/CodeMirror'
import { mapGetters } from 'vuex'
const CURRENT_FLOOR_FIELD_PREFIX = key => {
  return 'currentFloor.' + key
}
const CURRENT_ASSET_READING_PREFIX = key => {
  return 'currentAsset.reading.' + key
}
export default {
  components: { CodeMirror },
  props: ['id', 'floorplan'],
  data() {
    return {
      filterShapeInput: '',
      activeTab: 'markers',
      floorFields: [],
      activeSelections: [''],
      activeSelections1: [''],
      variables: [],
      customScript: {
        showDialog: false,
        saving: false,
        config: null,
        filterVariablesInput: '',
      },
      markers: [
        {
          name: 'Control',
          icons: [
            {
              object_id: 'com.facilio.floorplan.control.light.on',
              type: 'marker',
              objectType: 'control',
              markerType: 'LIGHT_ON_OFF',
              path: 'svgs/floorplan/light',
              name: 'Light',
            },
            {
              object_id: 'com.facilio.floorplan.control.ac.setpoint',
              type: 'marker',
              objectType: 'control',
              markerType: 'TEMPERATURE',
              path: 'svgs/floorplan/temp',
              name: 'Temperature',
            },
            {
              object_id: 'com.facilio.floorplan.maintenance',
              type: 'marker',
              objectType: 'control',
              markerType: 'MAINTENANCE',
              path: 'svgs/floorplan/maintenance',
              name: 'Maintenance',
            },
            {
              object_id: 'com.facilio.floorplan.humidity',
              type: 'marker',
              objectType: 'control',
              markerType: 'HUMIDITY',
              path: 'svgs/floorplan/humidity',
              name: 'Humidity',
            },
            {
              object_id: 'com.facilio.floorplan.occupancy',
              type: 'marker',
              objectType: 'control',
              markerType: 'OCCUPANCY',
              path: 'svgs/floorplan/occupancy',
              name: 'Occupancy',
            },
          ],
        },
      ],
      assetReadings: null,
      assetReadingsLoading: false,
    }
  },
  computed: {
    ...mapGetters(['getAssetCategory']),
    filteredVariables() {
      if (this.variables && this.variables.length) {
        let filteredVars = this.variables.filter(
          v =>
            v.label
              .toLowerCase()
              .indexOf(this.filterShapeInput.toLowerCase()) >= 0
        )
        let group1 = {
          label: 'Readings',
          key: 'readings',
          fields: filteredVars.filter(v => v.variableType === 'reading'),
        }
        let group2 = {
          label: 'Fields',
          key: 'fields',
          fields: filteredVars.filter(v => v.variableType === 'field'),
        }
        let group3 = {
          label: 'Custom',
          key: 'custom',
          fields: filteredVars.filter(v => v.variableType === 'custom'),
        }
        let group4 = {
          label: 'System',
          key: 'system',
          fields: [
            {
              key: 'system.currentTime',
              label: 'Current Time',
              type: 'sysvar',
            },
          ],
        }

        return [group1, group2, group3, group4]
      }
      return []
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  mounted() {
    this.loadVariables()
  },
  methods: {
    loadVariables() {
      this.variables = []
      this.$util.loadFields('floor').then(fields => {
        this.floorFields = fields
        this.loadFloorFieldVariables()
      })
    },
    loadFloorFieldVariables() {
      let variables = []
      for (let field of this.floorFields) {
        variables.push({
          fetchType: 'field',
          module: field.module.name,
          id: this.floorplan.id,
          parentId: this.floorplan.id,
          select: field.name,
          key: CURRENT_FLOOR_FIELD_PREFIX(field.name),
          dataType: field.dataTypeEnum._name,
          label: field.displayName,
          type: 'text',
          objectType: 'variable',
          variableType: 'field',
        })
      }
      this.variables.push(...variables)
    },
    getIcon(icon) {
      return require('statics/graphic-elements/' + icon)
    },
    dragStart(shape) {
      this.$emit('dragging', shape)
    },
    dragEnd() {
      // this.$emit('dragging', null)
    },
    loadAssetReadings() {
      if (this.activeTab === 'points') {
        if (this.assetReadings) {
          return this.assetReadings
        }

        let self = this
        self.assetReadingsLoading = true
        self.$http.get('/asset/getreadings').then(function(response) {
          let readings = response.data
          let readingsMap = []
          for (let categoryId in readings.categoryWithFields) {
            let categoryObj = self.getAssetCategory(parseInt(categoryId))

            let readingIds = Object.keys(
              readings.categoryWithFields[categoryId]
            )
            let fields = readingIds.map(el => {
              return readings.fields[el]
            })

            categoryObj.fields = fields
            readingsMap.push(categoryObj)
          }
          self.assetReadings = readingsMap
          self.assetReadingsLoading = false
        })
      }
    },
    onAddCustomVariable(vars) {
      if (vars && vars.length) {
        for (let data of vars) {
          if (this.variables.find(v => v.key === data.key)) {
            this.$message({
              message: 'Variable name already exists: ' + data.key,
              type: 'error',
            })
          } else {
            this.variables.push(data)
          }
        }
      }
    },
    removeVariable(data) {
      let varObj = this.variables.find(v => v.key === data.key)
      if (varObj) {
        this.variables.splice(this.variables.indexOf(varObj), 1)
      }
    },
    editVariable(variable) {
      if (variable.fetchType === 'function') {
        this.$http
          .get('/v2/workflow/getNameSpaceListWithFunctions')
          .then(response => {
            let nameSpaces = response.data.result.workflowNameSpaceList
              ? response.data.result.workflowNameSpaceList
              : []
            let foundNameSpace = nameSpaces.find(
              n => n.name === variable.namespace
            )
            if (foundNameSpace) {
              let foundFunction = foundNameSpace.functions.find(
                f => f.name === variable.function
              )
              this.openCustomScript(
                variable.key,
                foundFunction.workflowV2String
              )
            }
          })
      }
    },
    openCustomScript(variableName, savedScript) {
      let prefix = 'Map executeFunc(Map variables) { result = {};'
      let suffix = 'return result; }'
      let defaultScript =
        'name = variables.get("currentAsset.name");\n\nresult["value"] = 0;\nresult["unit"] = "";'
      if (savedScript) {
        savedScript = savedScript.replace(prefix, '')
        savedScript = savedScript.replace(suffix, '')
        defaultScript = savedScript
      }

      this.customScript.showDialog = true
      this.customScript.saving = false
      this.customScript.config = {
        showDialog: false,
        variableName: variableName ? variableName : null,
        prefix: prefix,
        suffix: suffix,
        workflowScript: defaultScript,
        isEdit: variableName ? true : false,
      }
    },
    closeCustomScript() {
      this.customScript.showDialog = false
      this.customScript.config = null
    },
    addCustomScript() {
      if (
        !this.customScript.config.variableName ||
        !this.customScript.config.variableName.length
      ) {
        this.$message({
          message: 'Variable name cannot be empty.',
          type: 'error',
        })
        return
      } else if (!this.customScript.config.isEdit) {
        if (
          this.variables.find(
            v => v.key === this.customScript.config.variableName
          )
        ) {
          this.$message({
            message:
              'Variable name already exists: ' +
              this.customScript.config.variableName,
            type: 'error',
          })
          return
        }
      }
      let nameSpace =
        'graphics_' + this.id + '_' + this.customScript.config.variableName
      let functionName = 'executeFunc'
      this.customScript.saving = true
      this.$http
        .get('/v2/workflow/getNameSpaceListWithFunctions')
        .then(response => {
          let nameSpaces = response.data.result.workflowNameSpaceList
            ? response.data.result.workflowNameSpaceList
            : []
          let foundNameSpace = nameSpaces.find(n => n.name === nameSpace)
          if (foundNameSpace) {
            let foundFunction = foundNameSpace.functions.find(
              f => f.name === functionName
            )
            if (!foundFunction) {
              let functionData = {
                userFunction: {
                  isV2Script: true,
                  nameSpaceId: foundNameSpace.id,
                  returnType: 4,
                  workflowV2String:
                    this.customScript.config.prefix +
                    this.customScript.config.workflowScript +
                    this.customScript.config.suffix,
                },
              }
              this.$http
                .post('/v2/workflow/addUserFunction', functionData)
                .then(response => {
                  let functionId = response.data.result.workflowUserFunction.id

                  this.afterSave({
                    variableName: this.customScript.config.variableName,
                    namespace: nameSpace,
                    function: functionName,
                    isEdit: this.customScript.config.isEdit,
                  })
                })
            } else {
              let functionData = {
                userFunction: {
                  isV2Script: true,
                  id: foundFunction.id,
                  nameSpaceId: foundNameSpace.id,
                  workflowV2String:
                    this.customScript.config.prefix +
                    this.customScript.config.workflowScript +
                    this.customScript.config.suffix,
                },
              }
              this.$http
                .post('/v2/workflow/updateUserFunction', functionData)
                .then(response => {
                  let functionId = response.data.result.workflowUserFunction.id

                  this.afterSave({
                    variableName: this.customScript.config.variableName,
                    namespace: nameSpace,
                    function: functionName,
                    isEdit: this.customScript.config.isEdit,
                  })
                })
            }
          } else {
            this.$http
              .post('/v2/workflow/addNameSpace', {
                namespace: { name: nameSpace },
              })
              .then(response => {
                let nameSpaceId = response.data.result.workflowNameSpace.id
                let functionData = {
                  userFunction: {
                    isV2Script: true,
                    nameSpaceId: nameSpaceId,
                    returnType: 4,
                    workflowV2String:
                      this.customScript.config.prefix +
                      this.customScript.config.workflowScript +
                      this.customScript.config.suffix,
                  },
                }
                this.$http
                  .post('/v2/workflow/addUserFunction', functionData)
                  .then(response => {
                    let functionId =
                      response.data.result.workflowUserFunction.id

                    this.afterSave({
                      variableName: this.customScript.config.variableName,
                      namespace: nameSpace,
                      function: functionName,
                      isEdit: this.customScript.config.isEdit,
                    })
                  })
              })
          }
        })
    },
    afterSave(data) {
      if (!data.isEdit) {
        let variableInfo = {
          label: data.variableName,
          key: data.variableName,
          fetchType: 'function',
          module: '',
          select: '',
          dataType: '',
          parentId: '',
          unit: '',
          type: 'custom',
          aggr: '',
          dateRange: '',
          namespace: data.namespace,
          function: data.function,
        }

        this.variables.push(variableInfo)

        this.$emit('update', variableInfo)
      } else {
        this.$emit(
          'update',
          this.variables.find(v => v.key === data.variableName)
        )
      }
      this.saving = false
      this.closeCustomScript()
    },
  },
}
</script>

<style>
.graphic-elements .el-collapse-item__header {
  padding-left: 10px;
}

.graphic-elements .shape-filter-box input {
  border-right: 0px;
  border-left: 0px;
  border-top: 0px;
  border-radius: 0px;
}

.graphic-elements .graphic-element-shape {
  width: 65px;
  height: 65px;
  padding: 10px;
  margin: 10px;
  float: left;
  cursor: move;
}

.graphic-elements .graphic-element-shape:hover {
  background: #f4f4f4;
}

.graphic-elements .graphic-element-shape img {
  width: 100%;
  display: inline-block;
  height: 100%;
  vertical-align: middle;
  outline: none;
}

.graphic-elements .el-collapse {
  height: calc(100vh - 200px);
  overflow-y: scroll;
}

.graphic-elements .el-tabs__nav.is-top {
  width: 100%;
}

.graphic-elements .el-tabs__nav.is-top .el-tabs__item {
  width: 50%;
  font-size: 13px;
  letter-spacing: 0.4px;
  color: #25243e;
  text-align: center;
  height: 45px;
  padding-top: 2px;
}

.graphic-elements .el-tabs__nav.is-top .el-tabs__item.is-active {
  border-bottom: 1px solid #ee518f;
}

.graphic-elements .el-tabs__header.is-top {
  margin: 0;
}

.graphic-elements ul.points-list {
  padding: 0;
  margin: 0;
}

.graphic-elements ul.points-list li {
  list-style: none;
  padding: 10px;
  cursor: move;
}

.graphic-elements ul.points-list li:hover {
  background: #f4f4f4;
}
.fc-graphic-legend-collapse .el-collapse-item__header {
  padding-left: 20px;
  font-size: 14px;
  font-weight: normal;
  border: 1px solid transparent;
  border-bottom: 1px solid #ecf3fa;
  letter-spacing: 0.5px;
  color: #324056;
  padding-right: 3px;
  line-height: 20px;
}
.fc-graphic-legend-collapse .el-collapse-item__header:hover {
  border: 1px solid #9ed6dd;
  background-color: #f9feff;
}
.graphic-elements .el-collapse {
  border-top: none;
  border-bottom: none;
}
.fc-graphic-legend-collapse .el-collapse-item__header.is-active {
  background-color: #fafafa !important;
  font-weight: 500;
  box-shadow: 0 3px 1px 0 rgba(229, 229, 229, 0.34);
}
.fc-graphic-legend-collapse .el-collapse-item__header .el-collapse-item__arrow {
  display: block !important;
  font-weight: 600;
  transform: rotate(90deg);
  font-size: 16px;
  margin-right: 20px;
}
.fc-graphic-legend-collapse .el-collapse-item__arrow.is-active {
  font-weight: 600;
  transform: rotate(-90deg);
  color: #ef508f;
  font-size: 16px;
}

.fc-graphic-legend-collapse .el-collapse-item__content {
  margin: 10px 10px 0 10px;
}
.graphic-elements .fc-graphic-legend-collapse .el-collapse-item__content {
  /* display: inline-flex; */
}

.fc-graphic-legend-collapse .data-points-drag-block {
  padding: 8px 10px !important;
  border-radius: 0px !important;
  margin-bottom: 0px !important;
  overflow: hidden;
  box-shadow: 0 1px 1px 0 rgba(235, 235, 235, 0.5);
}

.fc-graphic-legend-collapse .analytics-data-point.cursor-drag {
  margin-bottom: 6px;
}

.fc-graphic-legend-collapse .data-points-drag-block .label-txt-black {
  font-size: 13px !important;
}

.fc-graphic-legend-collapse .data-points-varname {
  font-size: 11px;
  color: #6b7e91;
}

.fc-graphic-legend-collapse .analytics-data-point.cursor-drag .el-icon-delete {
  float: right;
  font-size: 12px;
  color: rgb(216, 216, 216);
  cursor: pointer;
}
.fc-graphic-legend-collapse
  .analytics-data-point.cursor-drag
  .el-icon-delete:hover {
  color: #324056;
}
.graphics-refer-variables-container {
  height: 300px;
  overflow: scroll;
}
.graphics-refer-variables {
  padding: 0;
  margin: 0;
  list-style: none;
}
.graphics-refer-variables li {
  padding: 10px 5px;
  cursor: pointer;
}
.marker-drag-icon {
  padding-right: 10px;
}
.marker-drag-icon .fp-markers {
  width: 35px;
  height: 35px;
  padding: 0px !important;
}
</style>
