<template>
  <div>
    <el-dialog
      title="Tipddvfdvfds"
      :visible.sync="dialogVisible"
      :fullscreen="true"
      custom-class="fc-dialog-form setup-dialog-right setup-dialog50 setup-dialog show-right-dialog"
      :before-close="handleClose"
      style="z-index: 999999"
    >
      <div class="rep-form">
        <div class="rep-title">CHART OPTIONS</div>
        <div class="rep-body row">
          <div class="rep-body-selection col-3">
            <div
              v-for="(label, index) in labels"
              :key="index"
              class="rep-side-label"
              @click="selected = label.name"
              v-bind:class="{ active: selected === label.name }"
            >
              {{ label.name }}
            </div>
          </div>
          <div class="rep-body-action col-9">
            <div class="" v-if="selected === 'General'">
              <div v-if="general">
                <el-row
                  align="middle"
                  style="padding-top: 20px;width: 360px;margin: auto;"
                  :gutter="50"
                >
                  <el-col class="rep-col" :span="24">
                    <div class="new-label-text">Title</div>
                    <div class="form-input">
                      <div prop="name">
                        <el-input
                          class="text required header"
                          v-model="general.title"
                          type="text"
                          autocomplete="off"
                          placeholder="Enter Title Name"
                        />
                      </div>
                    </div>
                  </el-col>
                  <el-col class="rep-col" :span="24">
                    <div class="new-label-text">Discription</div>
                    <div class="form-input">
                      <div prop="name">
                        <el-input
                          class="text required header"
                          v-model="general.description"
                          type="textarea"
                          autocomplete="off"
                          placeholder="Description Please"
                        />
                      </div>
                    </div>
                  </el-col>
                  <el-col class="rep-col" :span="24">
                    <div class="new-label-text">Folder Name</div>
                    <div class="form-input">
                      <div prop="name">
                        <el-select
                          v-model="general.folder"
                          style="width:100%"
                          class="form-item "
                          placeholder="Select Folder"
                        >
                          <el-option
                            v-for="folder in reportFolder"
                            :key="folder.id"
                            :label="folder.label"
                            :value="folder.label"
                          ></el-option>
                        </el-select>
                      </div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </div>

            <div
              v-if="selected === 'Data points' && dataPointlayout"
              class="height100"
            >
              <div class="rep-data-container">
                <div class="rep-data">
                  <el-row class="rep-data-header">
                    <el-col :span="2" class=""></el-col>
                    <el-col :span="10">Data Point Name</el-col>
                    <el-col :span="10">Chart Type</el-col>
                    <el-col :span="2" class="text-right">Color</el-col>
                  </el-row>

                  <div
                    v-for="(layout, index) in dataPointlayout.dataPoints"
                    :key="index"
                    class="row rep-normal-conetnt"
                  >
                    <div
                      class="col-12 rep-cont "
                      v-if="!layout.dataPointsArray.length > 0"
                      :draggable="true"
                      @dragstart="dragstart(layout, index)"
                      @dragend="dragend(layout)"
                      @drag="drag"
                      @dragover="allowDrop(layout, index)"
                      @dragenter="dragEnter(layout, index)"
                      @drop="drop"
                    >
                      <div class="row">
                        <div class="rep-drag-icon col-1">
                          <i class="el-icon-more"></i>
                          <i class="el-icon-more"></i>
                        </div>
                        <div class="col-5">
                          <div>
                            <el-input
                              :autofocus="true"
                              class="text required header"
                              v-model="layout.name"
                              type="text"
                              placeholder=""
                              v-if="layout.edit"
                              @blur="blurevent(layout, index)"
                            />
                            <div
                              @change="forceUpdate"
                              @dblclick="editName(layout, index)"
                              v-else
                              style="padding-top:6px;overflow:hidden;white-space: nowrap;"
                            >
                              {{ layout.name }}
                            </div>
                          </div>
                        </div>
                        <div
                          style="padding-left: 10px;padding-right: 10px;"
                          class="col-5"
                        >
                          <el-select
                            @change="forceUpdate"
                            v-model="layout.chartType"
                            style="width:100%"
                            class="form-item"
                            placeholder="Select Chart"
                          >
                            <el-option label="line" value="line"></el-option>
                            <el-option label="area" value="line"></el-option>
                            <el-option label="bar" value="line"></el-option>
                            <el-option label="pie" value="line"></el-option>
                          </el-select>
                        </div>
                        <div class="text-right col-1">
                          <el-color-picker
                            v-model="layout.color"
                            size="mini"
                          ></el-color-picker>
                        </div>
                      </div>
                    </div>

                    <div
                      class="row datagroup rep-cont"
                      v-if="layout.dataPointsArray.length > 0"
                    >
                      <div class="col-12">Group Data</div>
                      <div
                        class="col-12"
                        v-if="isDragging"
                        @drop="drop()"
                        :draggable="true"
                        @dragstart="dragstart(layout, index)"
                        @dragover="dragGroupallow(index, 'group')"
                        @dragend="dragGroupend(layout)"
                        v-bind:class="{ 'drop-conatiner': isDragging === true }"
                      >
                        drop here
                      </div>
                      <div class="col-12 " style="margin:auto;">
                        <div
                          class="row rep-cont row"
                          v-for="(arrayPoint, idx) in layout.dataPointsArray"
                          :key="idx"
                        >
                          <div class="rep-drag-icon col-1">
                            <i class="el-icon-more"></i>
                            <i class="el-icon-more"></i>
                          </div>
                          <div class="col-4">
                            <div>
                              <el-input
                                :autofocus="true"
                                class="text required header"
                                v-model="arrayPoint.name"
                                type="text"
                                placeholder=""
                                v-if="arrayPoint.edit"
                                @blur="blurevent(arrayPoint, idx)"
                              />
                              <div
                                @change="forceUpdate"
                                @dblclick="editName(arrayPoint, idx)"
                                v-else
                                style="padding-top:6px;overflow:hidden;white-space: nowrap;"
                              >
                                {{ arrayPoint.name }}
                              </div>
                            </div>
                          </div>
                          <div
                            style="padding-left: 10px;padding-right: 10px;"
                            class="col-5"
                          >
                            <el-select
                              @change="forceUpdate"
                              v-model="arrayPoint.chartType"
                              style="width:100%"
                              class="form-item"
                              placeholder="Select Chart"
                            >
                              <el-option label="line" value="line"></el-option>
                              <el-option label="area" value="line"></el-option>
                              <el-option label="bar" value="line"></el-option>
                              <el-option label="pie" value="line"></el-option>
                            </el-select>
                          </div>
                          <div class="text-right col-1">
                            <el-color-picker
                              v-model="arrayPoint.color"
                              size="mini"
                            ></el-color-picker>
                          </div>
                          <div
                            class="col-1 rep-content-rm-icon"
                            v-if="arrayPoint.group"
                          >
                            <i
                              class="el-icon-remove-outline"
                              @click="
                                removeDataPoint(
                                  arrayPoint,
                                  layout.dataPointsArray,
                                  idx,
                                  index
                                )
                              "
                            ></i>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-if="selected === 'Axis'" class="rep-axis-box">
              <div>
                <div
                  class="rep-container"
                  v-if="axisFields && axisFields.xaxis"
                >
                  <div class="row p10 pL20">
                    <div class="uppercase rep-conatiner-header">xaxis</div>
                    <div class="col-4 uppercase rep-conatiner-header">
                      <el-checkbox v-model="xaxisChecked"></el-checkbox>
                    </div>
                  </div>
                  <div class="row p10 pL20" v-if="xaxisChecked">
                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Label</div>
                        <div class="col-4" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.xaxis.title"
                              type="text"
                              placeholder=""
                            />
                          </div>
                        </div>
                        <div class="col-3">
                          <fontstyle
                            :input="axisFields.xaxis"
                            :title="'XAXIS STYLE'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row" style="align-items: center;width:100%;">
                        <div class="col-3" style="">Tick</div>
                        <div class="col-6">
                          <fontstyle
                            :input="axisFields.xaxis"
                            :title="'TICKS'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div
                        class="row rep-label-subsection"
                        style="align-items: center;display: inline-flex;"
                      >
                        <div class="col-3" style="padding-right:6px;">
                          Direction
                        </div>
                        <div class="">
                          <div class="inline-display">
                            <el-radio-group
                              v-model="axisFields.xaxis.position"
                              @change="forceUpdate"
                              size="mini"
                            >
                              <el-radio-button label="left"></el-radio-button>
                              <el-radio-button label="right"></el-radio-button>
                              <el-radio-button label="top"></el-radio-button>
                              <el-radio-button label="bottom"></el-radio-button>
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Min & Max</div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.xaxis.minValue"
                              type="text"
                              placeholder="min"
                            />
                          </div>
                        </div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.xaxis.maxValue"
                              type="text"
                              placeholder="max"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div
                  class="rep-container"
                  v-if="axisFields && axisFields.y1axis"
                >
                  <div class="row p10 pL20">
                    <div class="uppercase rep-conatiner-header">Y1axis</div>
                    <div class="col-4 uppercase rep-conatiner-header">
                      <el-checkbox v-model="y1axisChecked"></el-checkbox>
                    </div>
                  </div>
                  <div class="row p10 pL20" v-if="y1axisChecked">
                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Label</div>
                        <div class="col-4" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y1axis.title"
                              type="text"
                              placeholder=""
                            />
                          </div>
                        </div>
                        <div class="col-3">
                          <fontstyle
                            :input="axisFields.y1axis"
                            :title="'XAXIS STYLE'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row" style="align-items: center;width:100%;">
                        <div class="col-3" style="">Tick</div>
                        <div class="col-6">
                          <fontstyle
                            :input="axisFields.y1axis"
                            :title="'TICKS'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div
                        class="row rep-label-subsection"
                        style="align-items: center;display: inline-flex;"
                      >
                        <div class="col-3" style="padding-right:6px;">
                          Direction
                        </div>
                        <div class="">
                          <div class="inline-display">
                            <el-radio-group
                              v-model="axisFields.y1axis.position"
                              @change="forceUpdate"
                              size="mini"
                            >
                              <el-radio-button label="left"></el-radio-button>
                              <el-radio-button label="right"></el-radio-button>
                              <el-radio-button label="top"></el-radio-button>
                              <el-radio-button label="bottom"></el-radio-button>
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Min & Max</div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y1axis.minValue"
                              type="text"
                              placeholder="min"
                            />
                          </div>
                        </div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y1axis.maxValue"
                              type="text"
                              placeholder="max"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>

                <div
                  class="rep-container"
                  v-if="axisFields && axisFields.y2axis"
                >
                  <div class="row p10 pL20">
                    <div class="uppercase rep-conatiner-header">Y2axis</div>
                    <div class="col-4 uppercase rep-conatiner-header">
                      <el-checkbox v-model="y2axisChecked"></el-checkbox>
                    </div>
                  </div>
                  <div class="row p10 pL20" v-if="y2axisChecked">
                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Label</div>
                        <div class="col-4" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y2axis.title"
                              type="text"
                              placeholder=""
                            />
                          </div>
                        </div>
                        <div class="col-3">
                          <fontstyle
                            :input="axisFields.y2axis"
                            :title="'XAXIS STYLE'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row" style="align-items: center;width:100%;">
                        <div class="col-3" style="">Tick</div>
                        <div class="col-6">
                          <fontstyle
                            :input="axisFields.y2axis"
                            :title="'TICKS'"
                            @output="getOutput()"
                          ></fontstyle>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div
                        class="row rep-label-subsection"
                        style="align-items: center;display: inline-flex;"
                      >
                        <div class="col-3" style="padding-right:6px;">
                          Direction
                        </div>
                        <div class="">
                          <div class="inline-display">
                            <el-radio-group
                              v-model="axisFields.y2axis.position"
                              @change="forceUpdate"
                              size="mini"
                            >
                              <el-radio-button label="left"></el-radio-button>
                              <el-radio-button label="right"></el-radio-button>
                              <el-radio-button label="top"></el-radio-button>
                              <el-radio-button label="bottom"></el-radio-button>
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row rep-label-subsection">
                        <div class="col-3">Min & Max</div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y2axis.minValue"
                              type="text"
                              placeholder="min"
                            />
                          </div>
                        </div>
                        <div class="col-3" style="margin-right:10px;">
                          <div>
                            <el-input
                              class="text required header"
                              v-model="axisFields.y2axis.maxValue"
                              type="text"
                              placeholder="max"
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="rep-axis-box" v-if="selected === 'Legends'">
              <div>
                <div
                  class="rep-container"
                  v-if="leagends"
                  v-for="(leagend, index) in leagends"
                  :key="index"
                >
                  <div class="row p10 pL20">
                    <div class="uppercase rep-conatiner-header">
                      {{ leagend.title }}
                    </div>
                    <div class="col-4 uppercase rep-conatiner-header">
                      <el-checkbox v-model="leagend.enable"></el-checkbox>
                    </div>
                  </div>
                  <div class="row p10 pL20" v-if="leagend.enable">
                    <div class="rep-axis-content col-12">
                      <div
                        class="row rep-label-subsection"
                        style="align-items: center;display: inline-flex;"
                      >
                        <div class="col-3" style="padding-right:6px;">
                          Postion
                        </div>
                        <div class="">
                          <div class="inline-display">
                            <el-radio-group
                              v-model="leagend.position"
                              @change="forceUpdate"
                              size="mini"
                            >
                              <el-radio-button label="left"></el-radio-button>
                              <el-radio-button label="right"></el-radio-button>
                              <el-radio-button label="top"></el-radio-button>
                              <el-radio-button label="bottom"></el-radio-button>
                            </el-radio-group>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="rep-axis-content col-12">
                      <div class="row" style="align-items: center;width:100%;">
                        <div class="col-3" style="">Values</div>
                        <div class="col-9">
                          <el-checkbox-group v-model="leagend.value">
                            <el-checkbox label="Min"></el-checkbox>
                            <el-checkbox label="Max"></el-checkbox>
                            <el-checkbox label="Total"></el-checkbox>
                            <el-checkbox label="Avg"></el-checkbox>
                          </el-checkbox-group>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="" v-if="selected === 'Styles'">
              <div>
                <div>---style not detected ---</div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-dialog-footer">
          <el-button class="modal-btn-save" type="primary" @click="handleClose"
            >SAVE</el-button
          >
          <el-button class="modal-btn-cancel" @click="handleClose">
            CANCEL</el-button
          >
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import fontstyle from '@/FontStyle'
export default {
  props: ['dialogVisible', 'data', 'report'],
  data() {
    return {
      selected: 'General',
      reportFolder: [],
      axisFields: null,
      arrayDataPoints: [],
      groupInsert: null,
      localdata: null,
      isDragging: false,
      leagends: [
        {
          title: 'chart Leagend',
          enable: true,
          value: [],
          position: 'top',
        },
        {
          title: 'widget Leagend',
          enable: true,
          value: [],
          position: 'bottom',
        },
      ],
      xaxisChecked: true,
      y1axisChecked: true,
      y2axisChecked: true,
      dragObj: null,
      dropObj: null,
      dataPointlayout: null,
      general: {
        title: null,
        description: null,
        folder: null,
      },
      labels: [
        {
          name: 'General',
        },
        {
          name: 'Data points',
        },
        {
          name: 'Axis',
        },
        {
          name: 'Legends',
        },
        {
          name: 'Styles',
        },
      ],
    }
  },
  components: {
    fontstyle,
  },
  mounted() {
    this.loadReports()
    this.dataReport()
  },
  methods: {
    handleClose(done) {
      this.$emit('update:dialogVisible', false)
    },
    dataReport() {
      this.localdata = this.$helpers.cloneObject(this.data)
      if (this.localdata) {
        this.dataPointsLayout(this.localdata)
      }
      if (this.report) {
        this.axisField(this.report)
      }
    },
    loadReports() {
      let self = this
      let moduleName = this.getmoduleName()
      self.$http
        .get(
          '/report/workorder/getAllWorkOrderReports?moduleName=' + moduleName
        )
        .then(function(response) {
          let data = response.data.allWorkOrderJsonReports
          let treeData = data.map(function(d) {
            return d
          })
          self.reportFolder = treeData.filter(
            row => row.label !== 'Default' && row.label !== 'Old Reports'
          )
          self.loading = true
        })
    },
    getOutput() {},
    removeDataPoint(data, array, index, parentindex) {
      this.dataPointlayout.dataPoints[parentindex].dataPointsArray.splice(
        index,
        1
      )
      this.dataPointlayout.dataPoints.push(data)
      if (
        this.dataPointlayout.dataPoints[parentindex].dataPointsArray.length ===
        1
      ) {
        this.dataPointlayout.dataPoints[parentindex].dataPointsArray = []
      }
    },
    getmoduleName() {
      if (this.$route.path.indexOf('/app/wo') > -1) {
        return 'workorder'
      } else if (this.$route.path.indexOf('/app/em') > -1) {
        return 'energydata'
      } else if (this.$route.path.indexOf('/app/fa') > -1) {
        return 'alarm'
      } else {
        return 'alarm'
      }
    },
    blurevent(data) {
      data.edit = false
      this.forceUpdate()
    },
    editName(data, index) {
      data.edit = true
      this.dataPointlayout.dataPoints[index].edit = true
      this.forceUpdate()
    },
    dataPointsLayout(data) {
      if (data.dataPoints.length > 0) {
        data.dataPoints.forEach(d => {
          d.color = '#d2d2d2'
          d.chartType = 'auto'
          d.edit = false
          d.dataPointsArray = []
          d.group = false
        })
        this.dataPointlayout = data
      }
    },
    axisField(data) {
      if (data) {
        this.axisFields = data.options
        if (data.options.xaxis) {
          this.axisFields.xaxis.config = null
          this.axisFields.xaxis.position = 'bottom'
        }
        if (data.options.y1axis) {
          this.axisFields.y1axis.config = null
          this.axisFields.y1axis.position = 'left'
        }
        if (data.options.y2axis) {
          this.axisFields.y2axis.config = null
          this.axisFields.y2axis.position = 'right'
        }
      }
    },
    forceUpdate() {
      this.$forceUpdate()
    },
    drag(data) {},
    dragstart(data, index) {
      this.dragObj = data
      this.dragObj.index = index
      this.isDragging = true
    },
    allowDrop() {},
    drop() {},
    dragEnter(data, index) {
      this.dropObj = data
      this.dropObj.index = index
    },
    dragGroupEnter(index) {},
    dragGroupallow(index, mode) {
      if (mode && mode === 'group') {
        this.groupInsert = {
          index: index,
          mode: mode,
        }
      }
    },
    dragGroupend(index) {
      this.dataPointlayout.dataPoints[index].dataPointsArray.push(this.dragObj)
    },
    dragend(data, mode) {
      this.isDragging = false
      if (this.groupInsert && this.groupInsert.mode === 'group') {
        this.dataPointlayout.dataPoints[
          this.groupInsert.index
        ].dataPointsArray.push(data)
        data.group = true
        this.dataPointlayout.dataPoints.splice(data.index, 1)
      }
      if (this.dragObj && this.dropObj && this.dragObj !== this.dropObj) {
        if (this.groupInsert && this.groupInsert.mode === 'group') {
          this.dataPointlayout.dataPoints[
            this.groupInsert.index
          ].dataPointsArray.push(data)
        } else {
          this.dataPointsMerge(this.dragObj, this.dropObj)
          this.dataPointlayout.dataPoints.splice(this.dragObj.index, 1)
        }
      }
      this.groupInsert = null
      this.forceUpdate()
    },
    dataPointsMerge(data1, data2) {
      data1.group = false
      data1.group = false
      if (
        data1 &&
        data2 &&
        this.dataPointlayout.dataPoints[data2.index].dataPointsArray.indexOf(
          data1
        ) < 0
      ) {
        data1.group = true
        if (
          this.dataPointlayout.dataPoints[data2.index].dataPointsArray.indexOf(
            data1
          ) < 0
        ) {
          this.dataPointlayout.dataPoints[data2.index].dataPointsArray.push(
            data1
          )
          this.dataPointlayout.dataPoints[data2.index].group = false
        }
        if (
          this.dataPointlayout.dataPoints[data2.index].dataPointsArray.indexOf(
            data2
          ) < 0
        ) {
          this.dataPointlayout.dataPoints[data2.index].dataPointsArray.push(
            data2
          )
          this.dataPointlayout.dataPoints[data2.index].group = false
        }
      }
    },
  },
}
</script>
<style>
.rep-title {
  font-weight: 500;
  padding: 20px;
  font-size: 16px;
  letter-spacing: 0.3px;
  border-bottom: 1px solid #f4f4f4;
}
.rep-body-selection {
  border-right: 1px solid #f4f4f4;
}
.rep-body-action {
}
.rep-body {
  height: calc(100vh - 110px);
}
.rep-side-label {
  padding: 10px 20px;
  letter-spacing: 0.4px;
  cursor: pointer;
}
.rep-side-label.active {
  background: #3ab2c2;
  color: #fff;
  font-weight: 500;
}
.rep-col {
  padding-left: 20px;
  padding-bottom: 30px;
}
.rep-data {
  width: 85%;
  margin: auto;
  padding: 20px;
  background: #fff;
}
.rep-data-container {
  background: #fff;
  height: 100%;
  padding-top: 25px;
  padding-bottom: 25px;
  overflow-y: scroll;
}
.rep-data-header {
  text-transform: uppercase;
  font-size: 11px;
  letter-spacing: 0.6px;
  color: gray;
  font-weight: 500;
}
.rep-cont {
  border-radius: 4px;
  padding: 10px;
  border: 1px solid #f4f4f4;
  margin-top: 15px;
  box-shadow: 0 3px 2px 0 rgba(217, 217, 217, 0.5);
  position: relative;
}
.rep-drag-icon {
  -webkit-transform: rotate(90deg);
  -moz-transform: rotate(90deg);
  -o-transform: rotate(90deg);
  -ms-transform: rotate(90deg);
  transform: rotate(90deg);
  text-align: center;
  position: relative;
  top: -1px;
  cursor: pointer;
  color: #ddd;
}
.rep-drag-icon .el-icon-more:first-child {
  position: relative;
  top: 10px;
}
.rep-axis-content {
  display: inline-flex;
  width: 100%;
  padding-top: 10px;
  padding-bottom: 10px;
}
.inline-display {
  display: -moz-inline-box;
  display: -o-inline-box;
  display: -ms-inline-box;
  display: -webkit-inline-block;
}
.rep-label-subsection {
  align-items: center;
  width: 100%;
  /* display: inline-box;
  display: -webkit-inline-box !important;
  display: -moz-inline-box !important;
  display: -o-inline-box !important;
  display: -mz-inline-box !important; */
}
.rep-container {
  padding: 10px;
  border: 1px solid #f4f4f4;
  border-radius: 3px;
  margin: 20px;
}
.rep-conatiner-header {
  font-weight: 500;
  padding-right: 10px;
}
.rep-axis-box {
  height: 100%;
  overflow: scroll;
}
.rep-container .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: #ed518f;
  border-color: #ed518f;
}
.datagroup {
  border: 1px solid #f2f2f2;
  width: 100%;
}
.rep-normal-conetnt .rep-drag-icon {
  display: inline-grid;
}
.rep-content-rm-icon {
  margin: auto;
  text-align: end;
  cursor: pointer;
}
.drop-conatiner {
  background: #8a2be257;
  height: calc(100% - 20px);
  position: absolute;
  width: calc(100% - 20px);
  z-index: 100;
  color: #333;
  font-size: 22px;
  text-transform: uppercase;
  align-items: center;
  text-align: center;
}
</style>
