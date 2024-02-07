<template>
  <el-dialog
    title="Properties"
    :visible.sync="visibility"
    v-if="visibility"
    :append-to-body="true"
    :before-close="closeDialog"
    custom-class="floorplan-property-dialog graphic-object-dialog fc-dialog-center-container setup-dialog60 graphic-object-build-dialog"
  >
    <div v-if="currentObject">
      <el-row
        v-if="
          currentObject.floorplan.enableTextBinding ||
            currentObject.floorplan.enableButtonBinding
        "
      >
        <el-col>
          <div class="fc-input-label-txt mb5">Text</div>
          <div>
            <el-col :span="22">
              <el-input
                v-model="currentObject.floorplan.formatText"
                class="fc-input-full-border2"
              ></el-input>
            </el-col>
            <el-col :span="2">
              <el-popover
                v-model="showInsertVariablePopover"
                placement="right"
                width="250"
                trigger="click"
                :popper-class="'f-popover'"
              >
                <div class="graphics-insert-variable-container">
                  <div class="graphics-insert-variable-title">
                    Insert Variable
                  </div>
                  <div class="graphics-insert-variable-filter">
                    <el-input
                      placeholder="Filter variables"
                      v-model="filterVariablesInput"
                      class="fc-input-full-border2"
                    ></el-input>
                  </div>
                  <div
                    class="graphics-insert-variable-list floorplan-insert-variable-list"
                  >
                    <ul>
                      <li
                        v-for="(v, index) in variables.filter(
                          v =>
                            !filterVariablesInput ||
                            v.label
                              .toLowerCase()
                              .indexOf(filterVariablesInput.toLowerCase()) >= 0
                        )"
                        :key="index"
                        @click="
                          currentObject.floorplan.formatText =
                            currentObject.floorplan.formatText +
                            ' ${' +
                            v.key +
                            '}'
                          showInsertVariablePopover = false
                        "
                      >
                        {{ v.label }}
                      </li>
                    </ul>
                  </div>
                </div>
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="Insert Variable"
                  placement="top"
                  slot="reference"
                >
                  <el-button
                    icon="el-icon-s-order"
                    style="padding: 2px 8px; font-size: 24px;"
                    type="text"
                  ></el-button>
                </el-tooltip>
              </el-popover>
            </el-col>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="8" v-if="currentObject.floorplan.enableStateBinding">
          <div class="fc-input-label-txt mb5">State Binding</div>
          <div>
            <el-select
              v-model="currentObject.floorplan.stateBindingVariable"
              class="fc-input-full-border-select2"
              :filterable="true"
            >
              <el-option
                v-for="(variable, index) in variables"
                :key="index"
                :label="variable.label"
                :value="variable.key"
              ></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="8" v-if="currentObject.floorplan.enableAnimateBinding">
          <div class="fc-input-label-txt mb5">Animate Binding</div>
          <div>
            <el-select
              v-model="currentObject.floorplan.animateBindingVariable"
              class="fc-input-full-border-select2"
              :filterable="true"
            >
              <el-option
                v-for="(variable, index) in variables"
                :key="index"
                :label="variable.label"
                :value="variable.key"
              ></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="8" v-if="currentObject.floorplan.enableSpaceMapping">
          <div class="fc-input-label-txt mb5">Space Mapping</div>
          <div>
            <el-input
              v-model="spaceMapping.spaceDisplayName"
              style="width:100%"
              type="text"
              :placeholder="$t('common._common.to_search_type')"
              class="fc-border-input-div fc-input-border-remove"
            >
              <i
                @click="spaceMapping.visible = true"
                slot="suffix"
                style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                class="el-input__icon el-icon-search"
              ></i>
            </el-input>
            <space-asset-chooser
              @associate="associateSpace"
              :visibility.sync="spaceMapping.visible"
              picktype="space"
              :query="spaceMapping.spaceDisplayName"
              :resourceType="[4]"
              :appendToBody="true"
            ></space-asset-chooser>
          </div>
        </el-col>
        <el-col
          :span="8"
          v-if="currentObject.floorplan && currentObject.floorplan.theme"
        >
          <div class="fc-input-label-txt mb5">Theme</div>
          <div>
            <el-select
              v-model="currentObject.floorplan.theme"
              class="fc-input-full-border-select2"
              :filterable="true"
            >
              <el-option
                v-for="(theme, index) in getColorThemes()"
                :key="index"
                :label="theme.label"
                :value="theme.key"
              ></el-option>
            </el-select>
          </div>
        </el-col>
      </el-row>
      <div v-if="currentObject.floorplan">
        <el-tabs type="border-card" class="mT30" @tab-click="loadControlPoints">
          <el-tab-pane
            label="Style"
            v-if="
              currentObject.type === 'text' && currentObject.floorplan.styles
            "
          >
            <el-row class="mT15">
              <el-col :span="10">
                <div class="fc-input-label-txt mb5">Font Family</div>
                <div>
                  <el-select
                    v-model="currentObject.floorplan.styles.fontFamily"
                    class="fc-input-full-border-select2"
                  >
                    <el-option label="Default" value="auto"></el-option>
                    <el-option
                      label="Aktiv Grotesk"
                      value="Aktiv-Grotesk"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Font Weight</div>
                <div>
                  <el-radio-group
                    v-model="currentObject.floorplan.styles.fontWeight"
                  >
                    <el-radio-button
                      label="Bold"
                      value="bold"
                    ></el-radio-button>
                    <el-radio-button label="Normal" value></el-radio-button>
                  </el-radio-group>
                </div>
              </el-col>
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Font Style</div>
                <div>
                  <el-radio-group
                    v-model="currentObject.floorplan.styles.fontStyle"
                  >
                    <el-radio-button
                      label="Italic"
                      value="italic"
                    ></el-radio-button>
                    <el-radio-button label="Normal" value></el-radio-button>
                  </el-radio-group>
                </div>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Text Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.fill"
                  ></el-color-picker>
                </div>
              </el-col>
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Background Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.backgroundColor"
                  ></el-color-picker>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            label="Style"
            v-if="
              currentObject.floorplan &&
                currentObject.floorplan.type === 'button_group' &&
                currentObject.floorplan.styles
            "
          >
            <el-row class="mT20">
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Text Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.fontColor"
                  ></el-color-picker>
                </div>
              </el-col>
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Background Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.backgroundColor"
                  ></el-color-picker>
                </div>
              </el-col>
            </el-row>
            <el-row class="mT20">
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Padding</div>
                <div>
                  <el-input-number
                    v-model="currentObject.floorplan.styles.padding"
                  ></el-input-number>
                </div>
              </el-col>
              <el-col :span="7">
                <div class="fc-input-label-txt mb5">Radius</div>
                <div>
                  <el-input-number
                    v-model="currentObject.floorplan.styles.radius"
                  ></el-input-number>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            label="Style"
            v-if="
              (currentObject.type === 'rect' ||
                currentObject.type === 'circle') &&
                currentObject.floorplan.styles
            "
          >
            <el-row class="mT15">
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Background Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.fill"
                  ></el-color-picker>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Border Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.stroke"
                  ></el-color-picker>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Border Width</div>
                <div>
                  <el-input-number
                    v-model="currentObject.floorplan.styles.strokeWidth"
                  ></el-input-number>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            label="Style"
            v-if="
              currentObject.type === 'line' && currentObject.floorplan.styles
            "
          >
            <el-row class="mT15">
              <el-col :span="12">
                <div class="fc-input-label-txt mb5">Border Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.stroke"
                  ></el-color-picker>
                </div>
              </el-col>
              <el-col :span="12">
                <div class="fc-input-label-txt mb5">Border Width</div>
                <div>
                  <el-input-number
                    v-model="currentObject.floorplan.styles.strokeWidth"
                  ></el-input-number>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            label="Style"
            v-if="
              currentObject.type === 'image' && currentObject.floorplan.filters
            "
          >
            <el-row class="mT15" :gutter="20">
              <el-col :span="4">
                <div class="fc-input-label-txt mb5">Brightness</div>
                <div>
                  <el-slider
                    v-model="currentObject.floorplan.filters.brightness"
                    :min="-100"
                    :max="100"
                    :step="1"
                  ></el-slider>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-input-label-txt mb5">Contrast</div>
                <div>
                  <el-slider
                    v-model="currentObject.floorplan.filters.contrast"
                    :min="-100"
                    :max="100"
                    :step="1"
                  ></el-slider>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-input-label-txt mb5">Saturation</div>
                <div>
                  <el-slider
                    v-model="currentObject.floorplan.filters.saturation"
                    :min="-100"
                    :max="100"
                    :step="1"
                  ></el-slider>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-input-label-txt mb5">Hue</div>
                <div>
                  <el-slider
                    v-model="currentObject.floorplan.filters.hue"
                    :min="0"
                    :max="100"
                    :step="1"
                  ></el-slider>
                </div>
              </el-col>
              <el-col :span="4">
                <div class="fc-input-label-txt mb5">Blur</div>
                <div>
                  <el-slider
                    v-model="currentObject.floorplan.filters.blur"
                    :min="0"
                    :max="100"
                    :step="1"
                  ></el-slider>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane
            label="Style"
            v-if="
              currentObject.floorplan &&
                currentObject.floorplan.type === 'space_zone' &&
                currentObject.floorplan.styles
            "
          >
            <el-row class="mT15">
              <el-col :span="12">
                <div class="fc-input-label-txt mb5">Background Color</div>
                <div>
                  <el-color-picker
                    :predefine="getPredefinedColors()"
                    v-model="currentObject.floorplan.styles.fill"
                  ></el-color-picker>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="Config">
            <el-row class="mT15">
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Select Space</div>
                <div>
                  <el-select
                    filterable
                    clearable
                    v-model="currentObject.floorplan.spaceId"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(space, index) in spaceList"
                      :key="index"
                      :label="space.name"
                      :value="space.id"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Select asset category</div>
                <div>
                  <el-select
                    @change="setAssetcategoryId()"
                    filterable
                    clearable
                    v-model="currentObject.floorplan.assetCategoryId"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(category, index) in assetCategory"
                      :key="index"
                      :label="category.displayName"
                      :value="category.id"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
              <el-col :span="8">
                <div class="fc-input-label-txt mb5">Select asset</div>
                <div>
                  <el-select
                    filterable
                    clearable
                    v-model="currentObject.floorplan.assetId"
                    class="fc-input-full-border-select2"
                  >
                    <el-option
                      v-for="(asset, index) in assetList"
                      :key="index"
                      :label="asset.name"
                      :value="asset.id"
                    ></el-option>
                  </el-select>
                </div>
              </el-col>
            </el-row>
          </el-tab-pane>
          <!-- <el-tab-pane
              label="Conditional formatting"
              style="max-height: 380px; overflow: scroll;"
              v-if="
                currentObject.floorplan &&
                  currentObject.floorplan.type !== 'space_zone'
              "
            >
              <conditional-formatting
                @conditionaldata="getConditionalData"
                :variables="variables"
                :currentObject="currentObject.floorplan"
              ></conditional-formatting>
            </el-tab-pane> -->
          <el-tab-pane
            label="Actions"
            style="max-height: 225px; overflow: scroll;"
          >
            <el-row
              class="mT15"
              v-if="
                currentObject.floorplan.enableTextBinding ||
                  currentObject.floorplan.enableButtonBinding ||
                  currentObject.floorplan.enableStateBinding ||
                  currentObject.floorplan.enableAnimateBinding
              "
            >
              <el-col :span="20">
                <el-checkbox v-model="objectActions.showTrend.enable"
                  >Show Trend</el-checkbox
                >
              </el-col>
            </el-row>
            <el-row class="mT15">
              <el-col :span="20">
                <el-checkbox v-model="objectActions.controlAction.enable"
                  >Control Action</el-checkbox
                >
              </el-col>
              <template v-if="objectActions.controlAction.enable">
                <el-row
                  v-for="(ca, index) in objectActions.controlAction
                    .control_list"
                  :key="index"
                  :gutter="10"
                  class="mT35"
                >
                  <el-col :span="6">
                    <el-select
                      class="fc-input-full-border-select2"
                      v-model="ca.type"
                      placeholder="Type"
                    >
                      <el-option
                        label="Control Point"
                        value="control_point"
                      ></el-option>
                      <el-option
                        label="Control Group"
                        value="control_group"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <template v-if="ca.type === 'control_point'">
                      <el-input
                        v-model="ca.label"
                        :readonly="true"
                        type="text"
                        placeholder="Choose Asset"
                        class="fc-input-full-border-select2"
                      >
                        <i
                          @click="showControlPointAssetPicker = true"
                          slot="suffix"
                          style="line-height:0px !important; font-size:16px !important; cursor:pointer;"
                          class="el-input__icon el-icon-search"
                        ></i>
                      </el-input>
                      <space-asset-chooser
                        @associate="
                          (resource, resourceType) =>
                            associateControlPointAsset(
                              ca,
                              resource,
                              resourceType
                            )
                        "
                        :visibility.sync="showControlPointAssetPicker"
                        v-if="showControlPointAssetPicker"
                        :withWritableReadings="true"
                        picktype="asset"
                        :closeOnClickModel="true"
                      ></space-asset-chooser>
                    </template>
                    <el-select
                      v-if="ca.type === 'control_group'"
                      filterable
                      class="fc-input-full-border-select2"
                      v-model="ca.groupId"
                      @change="setControlGroup(ca)"
                      placeholder="Select Control Group"
                    >
                      <el-option
                        v-for="(cg, index) in controlGroups"
                        :key="index"
                        :label="cg.name"
                        :value="cg.id"
                      ></el-option>
                    </el-select>
                  </el-col>
                  <el-col :span="8">
                    <el-select
                      v-if="ca.type === 'control_point'"
                      filterable
                      class="fc-input-full-border-select2"
                      v-model="ca.pointId"
                      @change="setControlPoint(ca)"
                      placeholder="Select Control Point"
                    >
                      <el-option
                        v-for="(cp, index) in controlPoints"
                        :key="index"
                        :label="cp.field.displayName"
                        :value="cp.id"
                      ></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </template>
            </el-row>
            <el-row class="mT15">
              <el-col :span="20">
                <el-checkbox v-model="objectActions.hyperLink.enable"
                  >Hyperlink</el-checkbox
                >
              </el-col>
              <template v-if="objectActions.hyperLink.enable">
                <el-row
                  v-for="(li, index) in objectActions.hyperLink.link_list"
                  :key="index"
                  :gutter="10"
                  class="mT35"
                >
                  <el-col :span="5">
                    <el-select
                      class="fc-input-full-border-select2"
                      @change="loadGraphicsList"
                      v-model="li.linkType"
                      placeholder="Type"
                    >
                      <el-option label="Graphics" value="graphics"></el-option>
                      <!-- <el-option label="Dashboard" value="dashboard"></el-option>
                        <el-option label="Report" value="report"></el-option>-->
                      <el-option label="URL" value="url"></el-option>
                      <!-- <el-option label="Popup" value="popup"></el-option> -->
                    </el-select>
                  </el-col>
                  <el-col :span="10">
                    <template v-if="li.linkType === 'graphics'">
                      <el-row :gutter="10">
                        <el-col :span="12">
                          <el-select
                            @change="loadGraphicAssetList(li)"
                            class="fc-input-full-border-select2"
                            :filterable="true"
                            v-model="li.id"
                            placeholder="Select Graphics"
                          >
                            <template v-if="graphicsListForActions">
                              <el-option
                                v-for="(g, idx) in graphicsListForActions"
                                :key="idx"
                                :label="g.name"
                                :value="g.id"
                              ></el-option>
                            </template>
                          </el-select>
                        </el-col>
                        <el-col :span="12">
                          <el-select
                            class="fc-input-full-border-select2"
                            v-model.lazy="li.assetId"
                            @click="setAssetId(li.assetId, index)"
                            :filterable="true"
                            placeholder="Select Asset"
                          >
                            <template v-if="assetListForActions">
                              <el-option
                                v-for="(at, idx) in assetListForActions"
                                :key="idx"
                                :label="at.name"
                                :value="at.id"
                              ></el-option>
                            </template>
                          </el-select>
                        </el-col>
                      </el-row>
                    </template>
                    <el-input
                      v-else
                      v-model="li.url"
                      class="fc-input-full-border2"
                      placeholder="URL"
                    ></el-input>
                  </el-col>
                  <el-col :span="5">
                    <el-select
                      class="fc-input-full-border-select2"
                      v-model="li.target"
                      placeholder="Target"
                    >
                      <el-option label="Same tab" value="self"></el-option>
                      <el-option label="New tab" value="_blank"></el-option>
                      <el-option label="Popup" value="popup"></el-option>
                    </el-select>
                  </el-col>
                </el-row>
              </template>
            </el-row>
            <!-- <el-row class="mT15">
                <el-col :span="20">
                  <el-checkbox v-model="objectActions.invokeFunction.enable">Invoke Function</el-checkbox>
                </el-col>
              </el-row>-->
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
    <div class="modal-dialog-footer">
      <el-button class="modal-btn-cancel" @click="closeDialog()"
        >CANCEL</el-button
      >
      <el-button
        class="modal-btn-save"
        type="primary"
        @click="updateObjectProps"
        >SAVE</el-button
      >
    </div>
  </el-dialog>
</template>

<script>
import SpaceAssetChooser from '@/SpaceAssetChooser'
import colors from 'charts/helpers/colors'
import { mapState } from 'vuex'
export default {
  props: ['visibility', 'currentObject', 'variables', 'spaceList'],
  components: { SpaceAssetChooser },
  computed: {
    ...mapState({
      assetCategory: state => state.assetCategory,
    }),
  },
  data() {
    return {
      showInsertVariablePopover: false,
      showControlPointAssetPicker: false,
      filterVariablesInput: '',
      graphicsListForActions: null,
      assetListForActions: null,
      controlPoints: [],
      assetList: [],
      controlGroups: [],
      objectActions: {
        showTrend: {
          enable: true,
        },
        controlAction: {
          enable: false,
          control_list: [
            {
              actionName: null,
              assetId: null,
              assetCategoryId: null,
              fieldId: null,
            },
          ],
        },
        hyperLink: {
          enable: false,
          link_list: [
            {
              actionName: null,
              linkType: null,
              id: null,
              assetId: null,
              url: null,
              target: '_blank',
            },
          ],
        },
        invokeFunction: {
          enable: false,
          function_list: [],
        },
      },
    }
  },
  mounted() {
    if (
      this.currentObject.floorplan &&
      this.currentObject.floorplan.assetCategoryId
    ) {
      this.loadAssets(this.currentObject.floorplan.assetCategoryId)
    }
  },
  methods: {
    setAssetcategoryId() {
      this.assetList = []
      console.log(
        'asset category id',
        this.currentObject.floorplan.assetCategoryId
      )
      this.loadAssets(this.currentObject.floorplan.assetCategoryId)
    },
    loadAssets(categoryId) {
      this.$util.loadAsset({ categoryId: categoryId }).then(response => {
        if (response.assets) {
          this.assetList = response.assets
        }
      })
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    closeDialog() {
      this.$emit('update:visibility', false)
    },
    loadGraphicsList() {
      this.graphicsListForActions = null
      this.$http.get('/v2/graphics/list').then(response => {
        if (response.data.result.graphics_list) {
          this.graphicsListForActions = response.data.result.graphics_list.map(
            g => {
              return {
                name: g.name,
                description: g.description,
                id: g.id,
                assetId: g.assetId,
                assetCategoryId: g.assetCategoryId,
                applyTo: g.applyTo,
              }
            }
          )

          if (
            this.currentObject &&
            this.currentObject.floorplan &&
            this.currentObject.floorplan.actions &&
            this.currentObject.floorplan.actions.hyperLink &&
            this.currentObject.floorplan.actions.hyperLink.enable &&
            this.currentObject.floorplan.actions.hyperLink.link_list &&
            this.currentObject.floorplan.actions.hyperLink.link_list.length
          ) {
            let li = this.currentObject.floorplan.actions.hyperLink.link_list[0]
            this.loadpreGraphicAssetList(li)
          }
        }
      })
    },
    // loadDefaultValues(objectActions) {
    //   let self = this
    //   if (
    //     objectActions.hyperLink &&
    //     objectActions.hyperLink.enable &&
    //     objectActions.hyperLink.link_list &&
    //     objectActions.hyperLink.link_list.length
    //   ) {
    //     let list = objectActions.hyperLink.link_list[0]
    //     this.graphicsListForActions = null
    //     this.$http.get('/v2/graphics/list').then(response => {
    //       if (response.data.result.graphics_list) {
    //         this.graphicsListForActions = response.data.result.graphics_list.map(
    //           g => {
    //             return {
    //               name: g.name,
    //               description: g.description,
    //               id: g.id,
    //               assetId: g.assetId,
    //               assetCategoryId: g.assetCategoryId,
    //               applyTo: g.applyTo,
    //             }
    //           }
    //         )
    //       }
    //       self.loadGraphicAssetList(list)
    //     })
    //   }
    // },
    associateControlPointAsset(ca, resource, resourceType) {
      ca.label = resource.name
      ca.assetId = resource.id
      ca.pointId = null

      this.loadAssetControlPoints(resource.id)
      this.showControlPointAssetPicker = false
    },
    loadpreGraphicAssetList(li) {
      this.assetListForActions = null
      if (li.id) {
        let gobj = this.graphicsListForActions.find(g => g.id === li.id)
        this.$util.loadCategoryAssetsForGraphics(gobj).then(assets => {
          this.assetListForActions = assets
        })
      }
    },
    loadGraphicAssetList(li) {
      this.assetListForActions = null
      if (li.id) {
        let gobj = this.graphicsListForActions.find(g => g.id === li.id)
        this.$util.loadCategoryAssetsForGraphics(gobj).then(assets => {
          this.assetListForActions = assets
          if (gobj.assetId) {
            li.assetId = gobj.assetId
          }
        })
      }
    },
    setControlPoint(controlAction) {
      let cp = this.controlPoints.find(c => c.id === controlAction.pointId)
      if (cp) {
        controlAction.assetId = cp.resourceId
        controlAction.fieldId = cp.fieldId
        if (
          cp.resourceContext &&
          cp.resourceContext.category &&
          cp.resourceContext.category.id
        ) {
          controlAction.assetCategoryId = cp.resourceContext.category.id
        }
        controlAction.actionName =
          cp.resourceContext.name + ' (' + cp.field.displayName + ')'
      }
    },
    loadAssetControlPoints(assetId) {
      if (!assetId) {
        return
      }
      let filters = {
        resourceId: { operatorId: 9, value: [assetId + ''] },
      }
      this.controlPoints = []
      this.$http
        .get(
          '/v2/controlAction/getControllablePoints?filters=' +
            encodeURIComponent(JSON.stringify(filters))
        )
        .then(response => {
          if (response.data.result && response.data.result.controllablePoints) {
            this.controlPoints = response.data.result.controllablePoints
          }
        })
    },
    loadControlPoints(tab) {
      if (this.currentObject && this.currentObject.floorplan.actions) {
        this.objectActions = this.currentObject.floorplan.actions
        this.loadGraphicsList()
      }
      if (
        this.currentObject &&
        this.currentObject.floorplan.actions &&
        this.currentObject.floorplan.actions.controlAction.control_list &&
        this.currentObject.floorplan.actions.controlAction.control_list
          .length &&
        this.currentObject.floorplan.actions.controlAction.control_list[0]
          .type == 'control_point'
      ) {
        let assetId = this.currentObject.floorplan.actions.controlAction
          .control_list[0].assetId
        this.loadAssetControlPoints(assetId)
      }

      this.controlGroups = []
      this.$http.get('/v2/controlAction/getControlGroups').then(response => {
        if (response.data.result && response.data.result.controlActionGroups) {
          this.controlGroups = response.data.result.controlActionGroups
        }
      })
    },
    getConditionalData(data) {
      if (data && data.length) {
        this.currentObject.floorplan.conditionalFormatting = data
      }
    },
    updateObjectProps() {
      if (this.currentObject.floorplan.actions) {
        this.currentObject.floorplan.actions = JSON.parse(
          JSON.stringify(this.objectActions)
        )
      } else {
        this.$set(
          this.currentObject.floorplan,
          'actions',
          JSON.parse(JSON.stringify(this.objectActions))
        )
      }
      this.$emit('updateObject', this.currentObject)
    },
  },
}
</script>

<style>
.graphics-insert-variable-title {
  padding: 12px;
  border-bottom: 1px solid #fafafa;
  text-transform: uppercase;
  font-weight: 500;
  font-size: 13px;
}
.graphics-insert-variable-filter {
  padding: 12px;
}
.floorplan-insert-variable-list {
  max-height: 300px;
  overflow: scroll;
}
.floorplan-insert-variable-list ul {
  padding: 0;
  margin: 0;
}
.floorplan-insert-variable-list ul li {
  list-style: none;
  padding: 12px;
  cursor: pointer;
}
.floorplan-insert-variable-listul li:hover {
  background: #fafafa;
}
</style>
