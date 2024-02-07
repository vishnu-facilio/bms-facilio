<template>
  <div class="graphic-elements">
    <el-tabs type="card" v-model="activeTab" @tab-click="loadAssetReadings">
      <el-tab-pane label="Shapes" name="shapes">
        <span slot="label">Shapes</span>
        <el-input
          placeholder="Filter shapes"
          class="fc-input-full-border2 shape-filter-box"
          suffix-icon="el-icon-search"
          v-model="filterShapeInput"
        ></el-input>
        <el-collapse v-model="activeSelections">
          <el-collapse-item
            :title="shapeGroup.label"
            :name="shapeGroup.name"
            v-for="(shapeGroup, index) in filteredShapes"
            :key="index"
            class="fc-graphic-legend-collapse"
          >
            <div
              class="graphic-element-shape"
              draggable="true"
              @dragstart="dragStart(shape)"
              @dragend="dragEnd"
              v-for="(shape, j) in shapeGroup.children"
              :key="j"
            >
              <el-tooltip
                class="item"
                effect="dark"
                :content="shape.label"
                placement="right"
              >
                <img
                  class="graphic-element-shape-icon"
                  draggable="false"
                  :src="getIcon(shape.icon)"
                />
              </el-tooltip>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane>
      <el-tab-pane :label="$t('common._common.variables')" name="variables">
        <span slot="label">{{ $t('common._common.variables') }}</span>
        <el-input
          :placeholder="$t('common._common.filter_variables')"
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
                  <i class="el-icon-plus"></i
                  >{{ $t('common.header.add_custom_variable') }}
                </el-button>
              </reading-selector>
              <br />
              <el-button
                type="text"
                size="small"
                class="mB10"
                @click="openCustomScript()"
              >
                <i class="el-icon-plus"></i
                >{{ $t('common.header.add_custom_script') }}
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
      </el-tab-pane>
    </el-tabs>
    <el-dialog
      :visible.sync="customScript.showDialog"
      width="60%"
      class="fieldchange-Dialog pB15 fc-dialog-center-container fc-dialog-center-body-p0"
      :title="$t('common.products.script')"
      :append-to-body="true"
      top="0%"
    >
      <div class="p20" v-if="customScript.config">
        <el-row>
          <el-col :span="12">
            <el-input
              :placeholder="$t('common.header.variable_name')"
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
            <div class="bold p5">{{ $t('common.header.refer_variables') }}</div>
            <el-input
              :placeholder="$t('common._common.filter_variables')"
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
        <el-button @click="closeCustomScript" class="modal-btn-cancel">{{
          $t('common._common.cancel')
        }}</el-button>
        <el-button
          type="primary"
          class="modal-btn-save"
          @click="addCustomScript"
          :loading="customScript.saving"
          >{{
            customScript.saving
              ? $t('common._common._saving')
              : $t('common._common._save')
          }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ReadingSelector from './ReadingSelector'
import CodeMirror from '@/CodeMirror'
import { mapGetters } from 'vuex'
export default {
  components: { ReadingSelector, CodeMirror },
  props: ['id', 'variables'],
  data() {
    return {
      filterShapeInput: '',
      activeTab: 'shapes',
      activeSelections: [''],
      activeSelections1: [''],
      customScript: {
        showDialog: false,
        saving: false,
        config: null,
        filterVariablesInput: '',
      },
      shapeGroups: [
        {
          name: 'duct',
          label: this.$t('common.header.duct'),
          children: [
            {
              label: this.$t('common.header.start_duct'),
              icon: 'start_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.start',
            },
            {
              label: this.$t('common.header.start_duct_closed'),
              icon: 'start_duct_closed.svg',
              object_id: 'com.facilio.graphics.shape.duct.startclosed',
            },
            {
              label: this.$t('common.header.end_duct'),
              icon: 'end_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.end',
            },
            {
              label: this.$t('common.header.end_duct_closed'),
              icon: 'end_duct_closed.svg',
              object_id: 'com.facilio.graphics.shape.duct.endclosed',
            },
            {
              label: this.$t('common.header.horizontal_duct'),
              icon: 'horizontal_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.horizontal',
            },
            {
              label: this.$t('common.header.vertical_duct'),
              icon: 'vertical_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical',
            },
            {
              label: this.$t('common.header.vertical_start_duct'),
              icon: 'vertical_start_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical_start',
            },
            {
              label: this.$t('common.header.vertical_start_duct_closed'),
              icon: 'vertical_start_duct_closed.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical_startclosed',
            },
            {
              label: this.$t('common.header.vertical_end_duct'),
              icon: 'vertical_end_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical_end',
            },
            {
              label: this.$t('common.header.vertical_end_duct_closed'),
              icon: 'vertical_end_duct_closed.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical_endclosed',
            },
            {
              label: this.$t('common.header.horizontal_heat_weel'),
              icon: 'horizontal_heat_wheel.svg',
              object_id:
                'com.facilio.graphics.shape.duct.horizontal_heat_wheel',
            },
            {
              label: this.$t('common.header.vertical_heat_weel'),
              icon: 'vertical_heat_wheel.svg',
              object_id: 'com.facilio.graphics.shape.duct.vertical_heat_wheel',
            },
            {
              label: this.$t('common.header.l_shape_top_left'),
              icon: 'l_shape_duct_top_left.svg',
              object_id: 'com.facilio.graphics.shape.duct.l_shape_top_left',
            },
            {
              label: this.$t('common.header.l_shape_top_right'),
              icon: 'l_shape_duct_top_right.svg',
              object_id: 'com.facilio.graphics.shape.duct.l_shape_top_right',
            },
            {
              label: this.$t('common.header.l_shape_bottom_left'),
              icon: 'l_shape_duct_bottom_left.svg',
              object_id: 'com.facilio.graphics.shape.duct.l_shape_bottom_left',
            },
            {
              label: this.$t('common.header.l_shape_bottom_right'),
              icon: 'l_shape_duct_bottom_right.svg',
              object_id: 'com.facilio.graphics.shape.duct.l_shape_bottom_right',
            },
            {
              label: this.$t('common.header.plus_shape'),
              icon: 'plus_shape_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.plus_shape',
            },
            {
              label: this.$t('common.header.t_shape_top'),
              icon: 't_shape_duct_top.svg',
              object_id: 'com.facilio.graphics.shape.duct.t_shape_top',
            },
            {
              label: this.$t('common.header.t_shape_right'),
              icon: 't_shape_duct_right.svg',
              object_id: 'com.facilio.graphics.shape.duct.t_shape_right',
            },
            {
              label: this.$t('common.header.t_shape_bottom'),
              icon: 't_shape_duct_bottom.svg',
              object_id: 'com.facilio.graphics.shape.duct.t_shape_bottom',
            },
            {
              label: this.$t('common.header.t_shape_left'),
              icon: 't_shape_duct_left.svg',
              object_id: 'com.facilio.graphics.shape.duct.t_shape_left',
            },
            {
              label: this.$t('common.header.bypass'),
              icon: 'bypass_duct.svg',
              object_id: 'com.facilio.graphics.shape.duct.bypass',
            },
            {
              label: this.$t('common.header.dual_split_1'),
              icon: 'dual_split_1.svg',
              object_id: 'com.facilio.graphics.shape.duct.dual_split_1',
            },
            {
              label: this.$t('common.header.dual_split_2'),
              icon: 'dual_split_2.svg',
              object_id: 'com.facilio.graphics.shape.duct.dual_split_2',
            },
            {
              label: this.$t('common.header.dual_split_3'),
              icon: 'dual_split_3.svg',
              object_id: 'com.facilio.graphics.shape.duct.dual_split_3',
            },
            {
              label: this.$t('common.header.dual_split_4'),
              icon: 'dual_split_4.svg',
              object_id: 'com.facilio.graphics.shape.duct.dual_split_4',
            },
          ],
        },
        {
          name: 'ahu',
          label: this.$t('common.header.ahu'),
          children: [
            {
              label: this.$t('common.header.cooling_coil_horizontal'),
              icon: 'cooling_coil_horizontal.svg',
              object_id:
                'com.facilio.graphics.shape.ahu.cooling_coil_horizontal',
            },
            {
              label: this.$t('common.header.cooling_coil_with_sensor'),
              icon: 'cooling_coil_horizontal_with_sensor.svg',
              object_id:
                'com.facilio.graphics.shape.ahu.cooling_coil_horizontal_with_sensor',
            },
            {
              label: this.$t('common.header.cooling_coil_vertical'),
              icon: 'cooling_coil_vertical.svg',
              object_id: 'com.facilio.graphics.shape.ahu.cooling_coil_vertical',
            },
            {
              label: this.$t('common.header.fan_1'),
              icon: 'fan_1.svg',
              object_id: 'com.facilio.graphics.shape.ahu.fan_1',
            },
            {
              label: this.$t('common.header.fan_2'),
              icon: 'fan_2.svg',
              object_id: 'com.facilio.graphics.shape.ahu.fan_2',
            },
            {
              label: this.$t('common.header.fan_3'),
              icon: 'fan_3.svg',
              object_id: 'com.facilio.graphics.shape.ahu.fan_3',
            },
            {
              label: this.$t('common.header.fan_4'),
              icon: 'fan_4.svg',
              object_id: 'com.facilio.graphics.shape.ahu.fan_4',
            },
            {
              label: this.$t('common.header.mini_fan'),
              icon: 'mini_fan.svg',
              object_id: 'com.facilio.graphics.shape.ahu.mini_fan',
            },
            {
              label: this.$t('common.header.danger_horizontal'),
              icon: 'damper_horizontal.svg',
              object_id: 'com.facilio.graphics.shape.ahu.damper_horizontal',
            },
            {
              label: this.$t('common.header.damper_vertical'),
              icon: 'damper_vertical.svg',
              object_id: 'com.facilio.graphics.shape.ahu.damper_vertical',
            },
            {
              label: this.$t('common.header.temperature_sensor'),
              icon: 'temperature_sensor.svg',
              object_id: 'com.facilio.graphics.shape.ahu.temperature_sensor',
            },
            {
              label: this.$t('common.header.sensor'),
              icon: 'plain_sensor.svg',
              object_id: 'com.facilio.graphics.shape.ahu.plain_sensor',
            },
            {
              label: this.$t('common.header.DP_sensor'),
              icon: 'dp_sensor.svg',
              object_id: 'com.facilio.graphics.shape.ahu.dp_sensor',
            },
            {
              label: this.$t('common.header.pipe'),
              icon: 'pipe.svg',
              object_id: 'com.facilio.graphics.shape.ahu.pipe',
            },
            {
              label: this.$t('common.header.sensor'),
              icon: 'sensor.svg',
              object_id: 'com.facilio.graphics.shape.ahu.sensor',
            },
            {
              label: this.$t('common.header.valve'),
              icon: 'valve.svg',
              object_id: 'com.facilio.graphics.shape.ahu.valve',
            },
            {
              label: this.$t('common.header.filter_horizontal'),
              icon: 'filter_horizontal.svg',
              object_id: 'com.facilio.graphics.shape.ahu.filter_horizontal',
            },
            {
              label: this.$t('common.header.filter_horizontal_with_dp'),
              icon: 'filter_horizontal_with_dp.svg',
              object_id:
                'com.facilio.graphics.shape.ahu.filter_horizontal_with_dp',
            },
            {
              label: this.$t('common.header.filter_vertical'),
              icon: 'filter_vertical.svg',
              object_id: 'com.facilio.graphics.shape.ahu.filter_vertical',
            },
            {
              label: this.$t('common.header.ahu_box_left'),
              icon: 'ahu_box_left.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_left',
            },
            {
              label: this.$t('common.header.ahu_box_right'),
              icon: 'ahu_box_right.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_right',
            },
            {
              label: this.$t('common.header.ahu_box_center'),
              icon: 'ahu_box_center.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_center',
            },
            {
              label: this.$t('common.header.ahu_box_duct'),
              icon: 'ahu_box_duct.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_duct',
            },
            {
              label: this.$t('common.header.ahu_box_fan'),
              icon: 'ahu_box_fan.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_fan',
            },
            {
              label: this.$t('common.header.ahu_box_compressor'),
              icon: 'ahu_box_compressor.svg',
              object_id: 'com.facilio.graphics.shape.ahu.box_compressor',
            },
          ],
        },
        {
          name: 'pipe',
          label: this.$t('common.header.pipe'),
          children: [
            {
              label: this.$t('common.header.pipe_horizontal'),
              icon: 'normal_horizontal.svg',
              object_id: 'com.facilio.graphics.shape.pipe.horizontal',
            },
            {
              label: this.$t('common.header.pipe_vertical'),
              icon: 'normal_vertical.svg',
              object_id: 'com.facilio.graphics.shape.pipe.vertical',
            },
            {
              label: this.$t('common.header.pipe_l_shape_bootom_left'),
              icon: 'pipe_normal_l_shape_bottom_left.svg',
              object_id: 'com.facilio.graphics.shape.pipe.l_shape_bottom_left',
            },
            {
              label: this.$t('common.header.pipe_l_shape_bottom_right'),
              icon: 'pipe_normal_l_shape_bottom_right.svg',
              object_id: 'com.facilio.graphics.shape.pipe.l_shape_bottom_right',
            },
            {
              label: this.$t('common.header.pipe_l_shape_top_left'),
              icon: 'pipe_normal_l_shape_top_left.svg',
              object_id: 'com.facilio.graphics.shape.pipe.l_shape_top_left',
            },
            {
              label: this.$t('common.header.pipe_l_shape_top_right'),
              icon: 'pipe_normal_l_shape_top_right.svg',
              object_id: 'com.facilio.graphics.shape.pipe.l_shape_top_right',
            },
            {
              label: this.$t('common.header.pipe_plus_shape'),
              icon: 'pipe_plus_shape.svg',
              object_id: 'com.facilio.graphics.shape.pipe.plus_shape',
            },
            {
              label: this.$t('common.header.pipe_t_shape_bottom'),
              icon: 'pipe_t_shape_bottom.svg',
              object_id: 'com.facilio.graphics.shape.pipe.t_shape_bottom',
            },
            {
              label: this.$t('common.header.pipe_t_shape_top'),
              icon: 'pipe_t_shape_top.svg',
              object_id: 'com.facilio.graphics.shape.pipe.t_shape_top',
            },
            {
              label: this.$t('common.header.pipe_t_shape_left'),
              icon: 'pipe_t_shape_left.svg',
              object_id: 'com.facilio.graphics.shape.pipe.t_shape_left',
            },
            {
              label: this.$t('common.header.pipe_t_shape_right'),
              icon: 'pipe_t_shape_right.svg',
              object_id: 'com.facilio.graphics.shape.pipe.t_shape_right',
            },
          ],
        },
        {
          name: 'valve',
          label: this.$t('common.header.valve'),
          children: [
            {
              label: this.$t('common.header.butterfly_value_horizontal'),
              icon: 'butterfly_valve_horizontal.svg',
              object_id:
                'com.facilio.graphics.shape.valve.butterfly_valve_horizontal',
            },
            {
              label: this.$t('common.header.butterfly_valve_vertical'),
              icon: 'butterfly_valve_vertical.svg',
              object_id:
                'com.facilio.graphics.shape.valve.butterfly_valve_vertical',
            },
            {
              label: this.$t('common.header.bypass_valve_horizontal'),
              icon: 'bypass_valve_horizontal.svg',
              object_id:
                'com.facilio.graphics.shape.valve.bypass_valve_horizontal',
            },
            {
              label: this.$t('common.header.bypass_valve_vertical'),
              icon: 'bypass_valve_vertical.svg',
              object_id:
                'com.facilio.graphics.shape.valve.bypass_valve_vertical',
            },
            {
              label: this.$t('common.header.manual_valve_horizontal'),
              icon: 'manual_valve_horizontal.svg',
              object_id:
                'com.facilio.graphics.shape.valve.manual_valve_horizontal',
            },
            {
              label: this.$t('common.header.manual_valve_vertical'),
              icon: 'manual_valve_vertical.svg',
              object_id:
                'com.facilio.graphics.shape.valve.manual_valve_vertical',
            },
            {
              label: this.$t('common.header.hand_operated_valve_horizontal'),
              icon: 'hand_operated_valve_horizontal.svg',
              object_id:
                'com.facilio.graphics.shape.valve.hand_operated_valve_horizontal',
            },
            {
              label: this.$t('common.header.hand_operated_valve_vertical'),
              icon: 'hand_operated_valve_vertical.svg',
              object_id:
                'com.facilio.graphics.shape.valve.hand_operated_valve_vertical',
            },
          ],
        },
        {
          name: 'chiller',
          label: this.$t('common.header.chiller'),
          children: [
            {
              label: this.$t('common.header.aircool_chiller'),
              icon: 'aircool_chiller.svg',
              object_id: 'com.facilio.graphics.shape.chiller.aircool_chiller',
            },
            {
              label: this.$t('common.header.boiler'),
              icon: 'boiler.svg',
              object_id: 'com.facilio.graphics.shape.chiller.boiler',
            },
            {
              label: this.$t('common.header.compressor'),
              icon: 'compressor.svg',
              object_id: 'com.facilio.graphics.shape.chiller.compressor',
            },
            {
              label: this.$t('common.header.cooling_tower'),
              icon: 'cooling_tower.svg',
              object_id: 'com.facilio.graphics.shape.chiller.cooling_tower',
            },
            {
              label: this.$t('common.header.cooling_tower_1'),
              icon: 'cooling_tower_1.svg',
              object_id: 'com.facilio.graphics.shape.chiller.cooling_tower_1',
            },
            {
              label: this.$t('common.header.cooling_tower_water_drops'),
              icon: 'cooling_tower_water_drops.svg',
              object_id:
                'com.facilio.graphics.shape.chiller.cooling_tower_water_drops',
            },
            {
              label: this.$t('common.header.plate_heat_exchanger'),
              icon: 'plate_heat_exchanger.svg',
              object_id:
                'com.facilio.graphics.shape.chiller.plate_heat_exchanger',
            },
            {
              label: this.$t('common.header.shell_and_tube_exchanger'),
              icon: 'shell_and_tube_heat_exchanger.svg',
              object_id:
                'com.facilio.graphics.shape.chiller.shell_and_tube_heat_exchanger',
            },
            {
              label: this.$t('common.header.vertical_heat_exchanger'),
              icon: 'vertical-heat-exchanger.svg',
              object_id:
                'com.facilio.graphics.shape.chiller.vertical_heat_exchanger',
            },
            {
              label: this.$t('common.header.pump_left'),
              icon: 'pump_left.svg',
              object_id: 'com.facilio.graphics.shape.chiller.pump_left',
            },
            {
              label: this.$t('common.header.pump_right'),
              icon: 'pump_right.svg',
              object_id: 'com.facilio.graphics.shape.chiller.pump_right',
            },
            {
              label: this.$t('common.header.chiller_1'),
              icon: 'chiller_1.svg',
              object_id: 'com.facilio.graphics.shape.chiller.chiller_1',
            },
            {
              label: this.$t('common.header.chiller_2'),
              icon: 'chiller_2.svg',
              object_id: 'com.facilio.graphics.shape.chiller.chiller_2',
            },
            {
              label: this.$t('common.header.water_tank'),
              icon: 'water_tank.svg',
              object_id: 'com.facilio.graphics.shape.chiller.water_tank',
            },
            {
              label: this.$t('common.header.water_tank'),
              icon: 'plain_water_tank.svg',
              object_id: 'com.facilio.graphics.shape.chiller.plain_water_tank',
            },
            {
              label: this.$t('common.header.bush_chiller'),
              icon: 'bush_chiller.svg',
              object_id: 'com.facilio.graphics.shape.chiller.bush_chiller',
            },
            {
              label: this.$t('common.header.centrifugal_chiller'),
              icon: 'centrifugal_chiller.svg',
              object_id:
                'com.facilio.graphics.shape.chiller.centrifugal_chiller',
            },
            {
              label: this.$t('common.header.boiler'),
              icon: 'boiler.svg',
              object_id: 'com.facilio.graphics.shape.chiller.new_boiler',
            },
            {
              label: this.$t('common.header.dx_unit'),
              icon: 'pac.svg',
              object_id: 'com.facilio.graphics.shape.chiller.dxunit',
            },
            {
              label: this.$t('common.header.progress_bar'),
              icon: 'progress-bar.svg',
              object_id: 'com.facilio.graphics.shape.chiller.progress',
            },
          ],
        },
        {
          name: 'arrow',
          label: this.$t('common.header.arrow'),
          children: [
            {
              label: this.$t('common.header.arrow_top'),
              icon: 'arrow_top.svg',
              object_id: 'com.facilio.graphics.shape.arrow.top',
            },
            {
              label: this.$t('common.header.arrow_right'),
              icon: 'arrow_right.svg',
              object_id: 'com.facilio.graphics.shape.arrow.right',
            },
            {
              label: this.$t('common.header.arrow_bottom'),
              icon: 'arrow_bottom.svg',
              object_id: 'com.facilio.graphics.shape.arrow.bottom',
            },
            {
              label: this.$t('common.header.arrow_left'),
              icon: 'arrow_left.svg',
              object_id: 'com.facilio.graphics.shape.arrow.left',
            },
            {
              label: this.$t('common.header.air_flow'),
              icon: 'air-flow.svg',
              object_id: 'com.facilio.graphics.shape.arrow.air_flow',
            },
            {
              label: this.$t('common.header.thin_arrow'),
              icon: 'thin-arrow.svg',
              object_id: 'com.facilio.graphics.shape.arrow.thin_arrow',
            },
            {
              label: this.$t('common.header.double_arrow'),
              icon: 'double-arrow.svg',
              object_id: 'com.facilio.graphics.shape.arrow.double_arrow',
            },
          ],
        },
        {
          name: 'vav',
          label: this.$t('common.header.vav'),
          children: [
            {
              label: this.$t('common.header.vav_with_value'),
              icon: 'vav_with_valve.svg',
              object_id: 'com.facilio.graphics.shape.vav.duct_with_valve',
            },
            {
              label: this.$t('common.header.vav_value_plate'),
              icon: 'vav_valve_plate.svg',
              object_id: 'com.facilio.graphics.shape.vav.valve_plate',
            },
            {
              label: this.$t('common.header.vav_with_fan'),
              icon: 'vav_with_fan.svg',
              object_id: 'com.facilio.graphics.shape.vav.vav_with_fan',
            },
            {
              label: this.$t('common.header.heating_coil'),
              icon: 'vav_heating_coil.svg',
              object_id: 'com.facilio.graphics.shape.vav.heating_coil',
            },
            {
              label: this.$t('common.header.heating_overlay'),
              icon: 'vav_heat_overlay.svg',
              object_id: 'com.facilio.graphics.shape.vav.vav_heat_overlay',
            },
          ],
        },
        {
          name: 'staircase',
          label: this.$t('common.header.staircase'),
          children: [
            {
              label: this.$t('common.header.stairs'),
              icon: 'stairs.svg',
              object_id: 'com.facilio.graphics.shape.staircase.stairs',
            },
            {
              label: this.$t('common.header.air_supply_fan_top'),
              icon: 'stairs_airsupply_topfan.svg',
              object_id: 'com.facilio.graphics.shape.staircase.top_fan',
            },
            {
              label: this.$t('common.header.air_supply_fan_bottom'),
              icon: 'stairs_airsupply_bottomfan.svg',
              object_id: 'com.facilio.graphics.shape.staircase.bottom_fan',
            },
            {
              label: this.$t('common.header.air_supply_duct_top'),
              icon: 'air_supply_duct_top.svg',
              object_id:
                'com.facilio.graphics.shape.staircase.air_supply_duct_top',
            },
            {
              label: this.$t('common.header.air_supply_duct_middle'),
              icon: 'air_supply_duct_middle.svg',
              object_id:
                'com.facilio.graphics.shape.staircase.air_supply_duct_middle',
            },
            {
              label: this.$t('common.header.air_supply_pressure_vent'),
              icon: 'air_supply_duct_pressure_vent.svg',
              object_id:
                'com.facilio.graphics.shape.staircase.air_supply_duct_pressure_vent',
            },
            {
              label: this.$t('common.header.air_supply_fan_bottom'),
              icon: 'air_supply_duct_bottom.svg',
              object_id:
                'com.facilio.graphics.shape.staircase.air_supply_duct_bottom',
            },
            {
              label: this.$t('common.header.air_flown_down'),
              icon: 'air_flow_down.svg',
              object_id: 'com.facilio.graphics.shape.staircase.air_flow_down',
            },
            {
              label: this.$t('common.header.air_flow_up'),
              icon: 'air_flow_up.svg',
              object_id: 'com.facilio.graphics.shape.staircase.air_flow_up',
            },
            {
              label: this.$t('common.header.air_flow_up_down'),
              icon: 'air_flow_twoway.svg',
              object_id: 'com.facilio.graphics.shape.staircase.air_flow_twoway',
            },
          ],
        },
        {
          name: 'plennum',
          label: this.$t('common.header.plennum'),
          children: [
            {
              label: this.$t('common.header.plennum_top'),
              icon: 'plennum_top.svg',
              object_id: 'com.facilio.graphics.shape.plennum.plennum_top',
            },
            {
              label: this.$t('common.header.plennum_middle'),
              icon: 'plennum_middle.svg',
              object_id: 'com.facilio.graphics.shape.plennum.plennum_middle',
            },
            {
              label: this.$t('common.header.plennum_bottom'),
              icon: 'plennum_bottom.svg',
              object_id: 'com.facilio.graphics.shape.plennum.plennum_bottom',
            },
            {
              label: this.$t('common.header.plennum_left_duct_joint'),
              icon: 'plennum_left_duct_joint.svg',
              object_id:
                'com.facilio.graphics.shape.plennum.plennum_left_duct_joint',
            },
            {
              label: this.$t('common.header.plennum_right_duct_joint'),
              icon: 'plennum_right_duct_joint.svg',
              object_id:
                'com.facilio.graphics.shape.plennum.plennum_right_duct_joint',
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
    filteredShapes() {
      if (this.filterShapeInput && this.filterShapeInput.trim()) {
        let filteredShapes = []
        for (let shape of this.shapeGroups) {
          let newShape = {
            name: shape.name,
            label: shape.label,
            children: shape.children.filter(
              sp =>
                sp.label
                  .toLowerCase()
                  .indexOf(this.filterShapeInput.toLowerCase()) >= 0
            ),
          }
          filteredShapes.push(newShape)
        }
        return filteredShapes
      }
      return this.shapeGroups
    },

    filteredPoints() {
      if (
        this.filterShapeInput &&
        this.filterShapeInput.trim() &&
        this.assetReadings
      ) {
        let filteredPoints = []
        for (let category of this.assetReadings) {
          let newCategory = {
            id: category.id,
            name: category.name,
            displayName: category.displayName,
            fields: category.fields.filter(
              sp =>
                sp.displayName
                  .toLowerCase()
                  .indexOf(this.filterShapeInput.toLowerCase()) >= 0
            ),
          }
          filteredPoints.push(newCategory)
        }
        return filteredPoints
      }
      return this.assetReadings
    },

    filteredVariables() {
      if (this.variables && this.variables.length) {
        let filteredVars = this.variables.filter(
          v =>
            v.label
              .toLowerCase()
              .indexOf(this.filterShapeInput.toLowerCase()) >= 0
        )
        let group1 = {
          label: this.$t('common.tabs.readings'),
          key: 'readings',
          fields: filteredVars.filter(v => v.type === 'reading'),
        }
        let group2 = {
          label: this.$t('common._common._fields'),
          key: 'fields',
          fields: filteredVars.filter(v => v.type === 'field'),
        }
        let group3 = {
          label: this.$t('common.date_picker.custom'),
          key: 'custom',
          fields: filteredVars.filter(v => v.type === 'custom'),
        }
        let group4 = {
          label: this.$t('common._common.system'),
          key: 'system',
          fields: [
            {
              key: 'system.currentTime',
              label: this.$t('common._common.current_time'),
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
  methods: {
    getIcon(icon) {
      return require('statics/graphic-elements/' + icon)
    },
    dragStart(shape) {
      this.$emit('dragging', shape)
    },
    dragEnd() {
      this.$emit('dragging', null)
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
              message:
                this.$t('common._common.variable_name_already_exists') +
                data.key,
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
          message: this.$t('common._common.variable_name_cannot_be_empty'),
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
              this.$t('common._common.variable_name_already_exists') +
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
</style>
