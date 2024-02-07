<template>
  <div
    v-if="readonly"
    :key="graphicsContext.id"
    :class="{
      'graphic-asset-summary': graphicsContext.assetId,
    }"
  >
    <div v-if="loadingState" style="position: absolute; top: 20%; left: 45%;">
      <div>{{ $t('common._common.loading_graphics') }}</div>
      <spinner :show="loadingState"></spinner>
    </div>
    <div
      v-if="noGraphicsAvailable"
      style="font-size: 13px;padding: 80px 50px 50px 50px;line-height: 25px;text-align: center;"
    >
      <inline-svg
        src="svgs/emptystate/reportlist"
        iconClass="icon text-center icon-xxxxlg"
      ></inline-svg>
      <div class="mT15 nowo-label">
        {{ $t('common.products.no_graphics_available_for_this_asset') }}
        <br />
        <el-button
          @click="$router.push({ path: '/app/em/graphics' })"
          class="fc-btn-green-medium-border mT15"
          >{{ $t('common.header.create_graphics') }}</el-button
        >
      </div>
    </div>
    <el-select
      @change="changeAsset(selectedCategoryAsset)"
      :filterable="true"
      v-model="selectedCategoryAsset"
      :placeholder="$t('common._common.select_asset')"
      class="fc-input-full-border-select2"
      style="float: left; margin: 10px; z-index: 11;"
      v-if="showFilters && categoryAssets && categoryAssets.length"
    >
      <el-option
        v-for="(asset, index) in categoryAssets"
        :key="index"
        :label="asset.name"
        :value="asset.id"
      ></el-option>
    </el-select>
    <div
      class="fplan-legend"
      v-if="
        showFloorPlanLegends &&
          $account.org.id != 297 &&
          graphics.id !== 322 &&
          graphics.id !== 324 &&
          $account.org.id !== 445
      "
    >
      <ul>
        <li
          @mouseover="highlightReservableSpaces('reservable', 0.7)"
          @mouseout="highlightReservableSpaces('reservable', 0.2)"
        >
          <div class="circlesmall axis-color" style="background: #75c5d9"></div>
          <div class="fplan-legend-label">
            {{ $t('common.wo_report.reservable') }}
          </div>
        </li>
        <li
          @mouseover="highlightReservableSpaces('non-reservable', 0.7)"
          @mouseout="highlightReservableSpaces('non-reservable', 0.2)"
        >
          <div class="circlesmall axis-color" style="background: #9c9c9b"></div>
          <div class="fplan-legend-label">
            {{ $t('common.wo_report.non_reservable') }}
          </div>
        </li>
      </ul>
    </div>
    <canvas
      id="graphics-canvas"
      ref="graphicsCanvas"
      class="graphics-canvas"
      :class="{ 'graphics-loading-overlay': loadingState }"
      width="1200"
      height="1000"
    ></canvas>
    <div
      class="graphics-actions"
      v-if="clickActions"
      :style="{ top: clickActions.top + 'px', left: clickActions.left + 'px' }"
    >
      <ul class="graphics-actions-menu">
        <li
          class="graphics-actions-menu-item"
          v-for="(action, index) in clickActions.actions"
          :key="index"
          @click="handleClickAction(action)"
        >
          {{
            action.actionType === 'controlAction'
              ? 'Set'
              : action.actionType === 'hyperLink'
              ? action.data.linkType === 'graphics'
                ? 'Open Graphics'
                : 'Goto: ' + action.actionName
              : action.actionName
          }}
        </li>
      </ul>
    </div>
    <div
      class="fplan-space-info-card"
      v-if="selectedSpaceInfo && selectedSpaceInfo.space"
      :style="{
        top: selectedSpaceInfo.top + 'px',
        left: selectedSpaceInfo.left + 'px',
      }"
    >
      <el-row>
        <el-col :span="24" class="mT20">
          <space-avatar
            :space="selectedSpaceInfo.space"
            name="false"
            size="xxxlg"
            :hovercard="false"
          ></space-avatar>
        </el-col>
        <el-col :span="24" class="space-title">{{
          selectedSpaceInfo.space.name
        }}</el-col>
        <el-col
          :span="24"
          v-if="selectedSpaceInfo.space.maxOccupancy"
          class="space-subtitle"
          >{{ $t('common._common.holds') }}
          {{ selectedSpaceInfo.space.maxOccupancy }}
          {{ $t('common._common.people') }}</el-col
        >
        <el-col
          :span="24"
          class="mT10"
          style="border-top: 1px solid #fafafa;"
          v-if="
            graphics.occupancyData &&
              graphics.occupancyData.spaceOccupancyData[
                selectedSpaceInfo.space.id
              ]
          "
        >
          <div class="f20 pT10">
            <i class="el-icon-user"></i>
            {{
              graphics.occupancyData.spaceOccupancyData[
                selectedSpaceInfo.space.id + ''
              ]
            }}
          </div>
          <span class="space-subtitle f11">{{
            $t('common._common.current_occupants')
          }}</span>
        </el-col>
        <el-col :span="24" class="mT30">
          <template v-if="selectedSpaceInfo.space.reservable">
            <button
              type="button"
              class="footer-btn footer-btn-secondary el-col el-col-12"
              @click="openSpace(selectedSpaceInfo.space)"
            >
              <span>{{ $t('common._common.view_details') }}</span>
            </button>
            <button
              type="button"
              class="footer-btn footer-btn-primary el-col el-col-12"
              @click="openReservePopup(selectedSpaceInfo.space)"
            >
              <span>{{ $t('common._common.reserve') }}</span>
            </button>
          </template>
          <template v-else>
            <button
              type="button"
              class="footer-btn footer-btn-secondary el-col el-col-24"
              @click="openSpace(selectedSpaceInfo.space)"
            >
              <span>{{ $t('common._common.view_details') }}</span>
            </button>
          </template>
        </el-col>
      </el-row>
    </div>
    <reservation-form
      v-if="reservationForm.visible"
      :preFillObj="reservationForm.reservation"
      :showCreateNewDialog.sync="reservationForm.visible"
      @saved="
        () => {
          ;(reservationForm.visible = false),
            (reservationForm.reservation = null)
        }
      "
    ></reservation-form>
    <el-dialog
      :title="$t('common.products.trend')"
      v-if="trendPopup.visible"
      :visible.sync="trendPopup.visible"
      :append-to-body="true"
      custom-class="f-popup-view"
      top="0%"
    >
      <div
        @click="openSelectedReadingInAnalytics"
        class="content analytics-txt"
        style="cursor: pointer; color: rgb(57, 178, 194); font-size: 13px; text-align: right; font-weight: 500; margin-right: 20px;"
      >
        {{ $t('common.products.go_to_analytics') }}
        <img
          style="width:13px; height: 9px;"
          src="~statics/icons/right-arrow.svg"
        />
      </div>
      <f-new-analytic-report
        :config.sync="trendPopup.analyticsConfig"
      ></f-new-analytic-report>
    </el-dialog>
    <SetReadingPopup
      v-if="controlPopup.visible"
      :saveAction="onSetControlValue"
      :closeAction="resetControlValue"
      :recordId="controlPopup.controlConfig.assetId"
      :fieldId="controlPopup.controlConfig.fieldId"
      :groupId="controlPopup.controlConfig.groupId"
      :recordName="''"
    ></SetReadingPopup>
    <a
      href
      ref="downloadLink"
      download="graphics.png"
      target="_blank"
      style="opacity: 0;"
    ></a>
  </div>
  <div
    class="formbuilder-fullscreen-popup"
    :class="isSaving || hasError ? 'noselect' : ''"
    v-else
    :key="graphicsContext.id"
  >
    <div class="setting-header2 position-relative" :class="{ hide: expand }">
      <div class="setting-title-block">
        <div class="setting-form-title mT10">
          {{ $t('common.products.graphics_builder') }}
          <span v-show="isSaving" class="saving-container">{{
            $t('common._common._saving')
          }}</span>
        </div>
      </div>
      <div class="action-btn setting-page-btn">
        <el-button
          type="secondary"
          class="fc-btn-green-medium-border shadow-none"
          @click="cancel"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button
          type="primary"
          class="fc-btn-green-medium-fill shadow-none"
          @click="save"
          :loading="isSaving"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </div>
    <div>
      <div class="graphics-builder-toolbar">
        <el-button-group>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.undo')"
            placement="bottom"
          >
            <el-button type="plain" icon="fa fa-undo"></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.redo')"
            placement="bottom"
          >
            <el-button type="plain" icon="fa fa-repeat"></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.send_to_back')"
            placement="bottom"
          >
            <el-button
              type="plain"
              icon="fa fa-object-group"
              @click="sendToBack"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.bring_to_front')"
            placement="bottom"
          >
            <el-button
              type="plain"
              icon="fa fa-object-ungroup"
              @click="bringToFront"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.lock')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="toggleLock(true)"
              icon="fa fa-lock"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.unlock')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="toggleLock(false)"
              icon="fa fa-unlock"
            ></el-button>
          </el-tooltip>
        </el-button-group>

        <el-button-group>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.add_text')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addText"
              icon="fa fa-font"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.add_button')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addButton"
              icon="fa fa-stop"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item position-relative"
            effect="dark"
            :content="$t('common.products.add_image')"
            placement="bottom"
          >
            <el-button type="plain" icon="fa fa-image">
              <input
                type="file"
                @change="addImage($event.target.files)"
                class="graphics-add-image"
              />
            </el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.add_line')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addLine"
              icon="fa fa-pencil"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.add_square')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addSquare"
              icon="fa fa-square-o"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.add_circle')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addCircle"
              icon="fa fa-circle-o"
            ></el-button>
          </el-tooltip>
        </el-button-group>

        <el-button-group>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.floor_plan_zoning')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="addPolygon"
              icon="fa fa-map-o"
            ></el-button>
          </el-tooltip>
        </el-button-group>

        <el-button-group>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.zoom_in')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="zoomIn"
              icon="fa fa-search-plus"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.zoom_out')"
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="zoomOut"
              icon="fa fa-search-minus"
            ></el-button>
          </el-tooltip>
          <el-tooltip
            class="item"
            effect="dark"
            :content="$t('common.products.fit_to_screen')"
            placement="bottom"
          >
            <el-button type="plain" icon="fa fa-compress"></el-button>
          </el-tooltip>
        </el-button-group>
        <el-button-group class="fR">
          <el-tooltip
            class="item"
            effect="dark"
            :content="
              expand
                ? $t('common.products.minimize')
                : $t('common.products.maximize')
            "
            placement="bottom"
          >
            <el-button
              type="plain"
              @click="expandEditor"
              icon="fa fa-arrows-alt"
            ></el-button>
          </el-tooltip>
        </el-button-group>
      </div>
      <div class="formbuilder-sidebar-left" :class="{ hide: expand }">
        <f-graphic-elements
          :id="graphicsContext ? graphicsContext.id : -1"
          :variables="graphics.variables"
          @dragging="handleDrag"
          @update="updateVariable"
        ></f-graphic-elements>
      </div>
      <div class="formbuilder-main-con">
        <div class="formbuilder-main-bg">
          <div class="graphics-editor-container">
            <canvas
              id="graphics-canvas"
              ref="graphicsCanvas"
              width="1200"
              height="1000"
            ></canvas>
          </div>
        </div>
      </div>
    </div>
    <div
      class="graphics-contextmenu graphics-actions"
      ref="graphicsContextMenu"
      v-if="contextmenu"
      :style="{ top: contextmenu.top + 'px', left: contextmenu.left + 'px' }"
    >
      <ul class="graphics-actions-menu">
        <li
          class="graphics-actions-menu-item"
          :class="{ 'graphics-actions-menu-section': menu.section }"
          @mouseover="menu.showSubMenu = true"
          @mouseout="menu.showSubMenu = false"
          v-for="(menu, index) in contextmenu.menu"
          :key="index"
          @click="handleRightClickAction(menu, contextmenu)"
        >
          {{ menu.label }}
          <template v-if="menu.submenu && menu.submenu.length">
            <i class="el-icon-arrow-right fR"></i>
            <ul
              class="graphics-actions-submenu"
              :style="
                contextmenu.width ? 'left: ' + contextmenu.width + 'px' : ''
              "
              v-if="menu.showSubMenu"
            >
              <li
                v-for="(sm, j) in menu.submenu"
                :key="j"
                @click="handleRightClickAction(menu, contextmenu, sm.value)"
              >
                {{ sm.label }}
              </li>
            </ul>
          </template>
        </li>
      </ul>
    </div>
    <el-dialog
      title="Properties"
      :visible.sync="objectPropertiesDialog"
      v-if="objectPropertiesDialog"
      :append-to-body="true"
      custom-class="graphic-object-dialog fc-dialog-center-container setup-dialog60 graphic-object-build-dialog"
    >
      <div v-if="currentObject">
        <el-row
          v-if="
            currentObject.fgraphic.enableTextBinding ||
              currentObject.fgraphic.enableButtonBinding
          "
        >
          <el-col>
            <div class="fc-input-label-txt mb5">
              {{ $t('common._common.text') }}
            </div>
            <div>
              <el-col :span="22">
                <el-input
                  v-model="currentObject.fgraphic.formatText"
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
                      {{ $t('common.products.insert_variable') }}
                    </div>
                    <div class="graphics-insert-variable-filter">
                      <el-input
                        :placeholder="$t('common._common.filter_variables')"
                        v-model="filterVariablesInput"
                        class="fc-input-full-border2"
                      ></el-input>
                    </div>
                    <div class="graphics-insert-variable-list">
                      <ul>
                        <li
                          v-for="(v, index) in graphics.variables.filter(
                            v =>
                              !filterVariablesInput ||
                              v.label
                                .toLowerCase()
                                .indexOf(filterVariablesInput.toLowerCase()) >=
                                0
                          )"
                          :key="index"
                          @click="
                            currentObject.fgraphic.formatText =
                              currentObject.fgraphic.formatText +
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
                    :content="$t('common.products.insert_variable')"
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
          <el-col :span="8" v-if="currentObject.fgraphic.enableStateBinding">
            <div class="fc-input-label-txt mb5">
              {{ $t('common.products.state_binding') }}
            </div>
            <div>
              <el-select
                v-model="currentObject.fgraphic.stateBindingVariable"
                class="fc-input-full-border-select2"
                :filterable="true"
              >
                <el-option
                  v-for="(variable, index) in graphics.variables"
                  :key="index"
                  :label="variable.label"
                  :value="variable.key"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8" v-if="currentObject.fgraphic.enableAnimateBinding">
            <div class="fc-input-label-txt mb5">
              {{ $t('common.products.animate_binding') }}
            </div>
            <div>
              <el-select
                v-model="currentObject.fgraphic.animateBindingVariable"
                class="fc-input-full-border-select2"
                :filterable="true"
              >
                <el-option
                  v-for="(variable, index) in graphics.variables"
                  :key="index"
                  :label="variable.label"
                  :value="variable.key"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8" v-if="currentObject.fgraphic.enableSpaceMapping">
            <div class="fc-input-label-txt mb5">
              {{ $t('common.products.space_mapping') }}
            </div>
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
            v-if="currentObject.fgraphic && currentObject.fgraphic.theme"
          >
            <div class="fc-input-label-txt mb5">
              {{ $t('common.header.theme') }}
            </div>
            <div>
              <el-select
                v-model="currentObject.fgraphic.theme"
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
          <el-col :span="8" v-if="currentObject.fgraphic.enableProgressBinding">
            <div class="fc-input-label-txt mb5">
              {{ $t('common.header.progress_bar_binding') }}
            </div>
            <div>
              <el-select
                v-model="currentObject.fgraphic.progressBindingVariable"
                class="fc-input-full-border-select2"
                :filterable="true"
              >
                <el-option
                  v-for="(variable, index) in graphics.variables"
                  :key="index"
                  :label="variable.label"
                  :value="variable.key"
                ></el-option>
              </el-select>
            </div>
          </el-col>
          <el-col :span="8" v-if="currentObject.fgraphic.enableProgressBinding">
            <div class="fc-input-label-txt mb5">
              {{ $t('common.header.progress_bar_max_value') }}
            </div>
            <div>
              <el-input-number
                v-model="currentObject.fgraphic.progressBindingMaxValue"
              ></el-input-number>
            </div>
          </el-col>
        </el-row>
        <div v-if="currentObject.fgraphic">
          <el-tabs
            type="border-card"
            class="mT30"
            @tab-click="loadControlPoints"
          >
            <el-tab-pane
              :label="$t('common._common.style')"
              v-if="
                currentObject.type === 'text' && currentObject.fgraphic.styles
              "
            >
              <el-row class="mT15">
                <el-col :span="6">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.font_family') }}
                  </div>
                  <div>
                    <el-select
                      v-model="currentObject.fgraphic.styles.fontFamily"
                      class="fc-input-full-border-select2"
                    >
                      <el-option
                        :label="$t('common._common.default')"
                        value="auto"
                      ></el-option>
                      <el-option
                        :label="$t('common._common.aktiv_grotesk')"
                        value="Aktiv-Grotesk"
                      ></el-option>
                    </el-select>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common._common.font_size') }}
                  </div>
                  <div>
                    <el-input-number
                      v-model="currentObject.fgraphic.styles.fontSize"
                    ></el-input-number>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common._common.font_weight') }}
                  </div>
                  <div>
                    <el-radio-group
                      v-model="currentObject.fgraphic.styles.fontWeight"
                    >
                      <el-radio-button
                        :label="$t('common.products.bold')"
                        value="bold"
                      ></el-radio-button>
                      <el-radio-button
                        :label="$t('common.products.normal')"
                        value
                      ></el-radio-button>
                    </el-radio-group>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.font_style') }}
                  </div>
                  <div>
                    <el-radio-group
                      v-model="currentObject.fgraphic.styles.fontStyle"
                    >
                      <el-radio-button
                        :label="$t('common.products.italic')"
                        value="italic"
                      ></el-radio-button>
                      <el-radio-button
                        :label="$t('common.products.normal')"
                        value
                      ></el-radio-button>
                    </el-radio-group>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT20">
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.text_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.fill"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.background_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.backgroundColor"
                    ></el-color-picker>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              label="Style"
              v-if="
                currentObject.fgraphic &&
                  currentObject.fgraphic.type === 'button' &&
                  currentObject.fgraphic.styles
              "
            >
              <el-row class="mT20">
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.text_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.fontColor"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.background_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.backgroundColor"
                    ></el-color-picker>
                  </div>
                </el-col>
              </el-row>
              <el-row class="mT20">
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.padding') }}
                  </div>
                  <div>
                    <el-input-number
                      v-model="currentObject.fgraphic.styles.padding"
                    ></el-input-number>
                  </div>
                </el-col>
                <el-col :span="7">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.radius') }}
                  </div>
                  <div>
                    <el-input-number
                      v-model="currentObject.fgraphic.styles.radius"
                    ></el-input-number>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('common.products.style')"
              v-if="
                (currentObject.type === 'rect' ||
                  currentObject.type === 'circle') &&
                  currentObject.fgraphic.styles
              "
            >
              <el-row class="mT15">
                <el-col :span="8">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.background_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.fill"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.border_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.stroke"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.border_width') }}
                  </div>
                  <div>
                    <el-input-number
                      v-model="currentObject.fgraphic.styles.strokeWidth"
                    ></el-input-number>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              label="Style"
              v-if="
                currentObject.type === 'line' && currentObject.fgraphic.styles
              "
            >
              <el-row class="mT15">
                <el-col :span="12">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.border_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.stroke"
                    ></el-color-picker>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.border_width') }}
                  </div>
                  <div>
                    <el-input-number
                      v-model="currentObject.fgraphic.styles.strokeWidth"
                    ></el-input-number>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              label="Style"
              v-if="
                currentObject.type === 'image' && currentObject.fgraphic.filters
              "
            >
              <el-row class="mT15" :gutter="20">
                <el-col :span="4">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.brightness') }}
                  </div>
                  <div>
                    <el-slider
                      v-model="currentObject.fgraphic.filters.brightness"
                      :min="-100"
                      :max="100"
                      :step="1"
                    ></el-slider>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.produvts.contrast') }}
                  </div>
                  <div>
                    <el-slider
                      v-model="currentObject.fgraphic.filters.contrast"
                      :min="-100"
                      :max="100"
                      :step="1"
                    ></el-slider>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.saturation') }}
                  </div>
                  <div>
                    <el-slider
                      v-model="currentObject.fgraphic.filters.saturation"
                      :min="-100"
                      :max="100"
                      :step="1"
                    ></el-slider>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.hue') }}
                  </div>
                  <div>
                    <el-slider
                      v-model="currentObject.fgraphic.filters.hue"
                      :min="0"
                      :max="100"
                      :step="1"
                    ></el-slider>
                  </div>
                </el-col>
                <el-col :span="4">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.blur') }}
                  </div>
                  <div>
                    <el-slider
                      v-model="currentObject.fgraphic.filters.blur"
                      :min="0"
                      :max="100"
                      :step="1"
                    ></el-slider>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('common.products.style')"
              v-if="
                currentObject.fgraphic &&
                  currentObject.fgraphic.type === 'space_zone' &&
                  currentObject.fgraphic.styles
              "
            >
              <el-row class="mT15">
                <el-col :span="12">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.products.background_color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.fill"
                    ></el-color-picker>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('common.products.select')"
              v-if="
                currentObject.fgraphic &&
                  currentObject.fgraphic.uid ===
                    'com.facilio.graphics.shape.chiller.progress' &&
                  currentObject.fgraphic.styles
              "
            >
              <el-row class="mT15">
                <el-col :span="12">
                  <div class="fc-input-label-txt mb5">
                    {{ $t('common.header.color') }}
                  </div>
                  <div>
                    <el-color-picker
                      :predefine="getPredefinedColors()"
                      v-model="currentObject.fgraphic.styles.fill"
                    ></el-color-picker>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('common.header.conditional_formatting')"
              style="max-height: 380px; overflow: scroll;"
              v-if="
                currentObject.fgraphic &&
                  currentObject.fgraphic.type !== 'space_zone'
              "
            >
              <conditional-formatting
                v-if="variablesLoaded"
                @conditionaldata="getConditionalData"
                :variables="this.graphics.variables"
                :currentObject="currentObject.fgraphic"
              ></conditional-formatting>
            </el-tab-pane>
            <el-tab-pane
              :label="$t('common.products.actions')"
              style="max-height: 225px; overflow: scroll;"
            >
              <el-row
                class="mT15"
                v-if="
                  currentObject.fgraphic.enableTextBinding ||
                    currentObject.fgraphic.enableButtonBinding ||
                    currentObject.fgraphic.enableStateBinding ||
                    currentObject.fgraphic.enableAnimateBinding ||
                    currentObject.fgraphic.enableProgressBinding
                "
              >
                <el-col :span="20">
                  <el-checkbox v-model="objectActions.showTrend.enable">{{
                    $t('common.products.show_trend')
                  }}</el-checkbox>
                </el-col>
              </el-row>
              <el-row class="mT15">
                <el-col :span="20">
                  <el-checkbox v-model="objectActions.controlAction.enable">{{
                    $t('common.products.control_action')
                  }}</el-checkbox>
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
                        :placeholder="$t('common._common.type')"
                      >
                        <el-option
                          :label="$t('common.products.control_point')"
                          value="control_point"
                        ></el-option>
                        <el-option
                          :label="$t('common.products.control_group')"
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
                          ;placeholder="$t('common.products.choose_asset')"
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
                        :placeholder="
                          $t('common.products.select_control_group')
                        "
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
                        :placeholder="
                          $t('common.products.select_control_point')
                        "
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
                  <el-checkbox v-model="objectActions.hyperLink.enable">{{
                    $t('common.products.hyper_link')
                  }}</el-checkbox>
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
                        :placeholder="$t('common._common.type')"
                      >
                        <el-option
                          :label="$t('common.header.graphics')"
                          value="graphics"
                        ></el-option>
                        <el-option
                          :label="$t('common.header.url')"
                          value="url"
                        ></el-option>
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
                              :placeholder="$t('common.header.select_graphics')"
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
                              :placeholder="$t('common._common.select_asset')"
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
                        :placeholder="$t('common.header.url')"
                      ></el-input>
                    </el-col>
                    <el-col :span="5">
                      <el-select
                        class="fc-input-full-border-select2"
                        v-model="li.target"
                        :placeholder="$t('common.header.target')"
                      >
                        <el-option
                          :label="$t('common.header.same_tab')"
                          value="self"
                        ></el-option>
                        <el-option
                          :label="$t('common.header.new_tab')"
                          value="_blank"
                        ></el-option>
                        <el-option
                          :label="$t('common.header.popup')"
                          value="popup"
                        ></el-option>
                      </el-select>
                    </el-col>
                  </el-row>
                </template>
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <div class="modal-dialog-footer">
        <el-button
          class="modal-btn-cancel"
          @click="objectPropertiesDialog = false"
          >{{ $t('common._common.cancel') }}</el-button
        >
        <el-button
          class="modal-btn-save"
          type="primary"
          @click="updateObjectProps"
          >{{ $t('common._common._save') }}</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>
<script>
import fabric from 'fabric'
import util from 'util/util'
import FGraphicElements from './FGraphicElements'
import FNewAnalyticReport from 'pages/energy/analytics/components/FNewAnalyticReport'
import FGraphicUtil from './FGraphicUtil'
import * as PolygonUtil from './PolygonUtil'
import NewDateHelper from '@/mixins/NewDateHelper'
import moment from 'moment-timezone'
import colors from 'charts/helpers/colors'
import JumpToHelper from '@/mixins/JumpToHelper'
import SetReadingPopup from '@/readings/SetReadingValue'
import SpaceAssetChooser from '@/SpaceAssetChooser'
import SpaceAvatar from '@/avatar/Space'
import ReservationForm from 'pages/resourcebooking/reservation/ReservationForm'
import ConditionalFormatting from '@/ConditionalFormatting'
import { themes, getColorThemes } from './ColorThemes'
import ckmeans from 'third_party/ckmeans.min.js'
import * as d3 from 'd3'
import { isPortalDomain } from 'util/utility-methods'
import { getBaseURL } from 'util/baseUrl'
import { isWebTabsEnabled, findRouteForTab, tabTypes } from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'
import throttle from 'lodash/throttle'

const CURRENT_ASSET_FIELD_PREFIX = key => {
  return 'currentAsset.' + key
}
const CURRENT_ASSET_READING_PREFIX = key => {
  return 'currentAsset.reading.' + key
}

export default {
  props: [
    'graphicsContext',
    'readonly',
    'width',
    'height',
    'showFilters',
    'zoomLevel',
    'defaultAsset',
  ],
  mixins: [JumpToHelper],
  components: {
    FGraphicElements,
    FNewAnalyticReport,
    SetReadingPopup,
    SpaceAssetChooser,
    SpaceAvatar,
    ReservationForm,
    ConditionalFormatting,
  },
  data() {
    return {
      showControlPointAssetPicker: false,
      currentAsset: {
        id: null,
        record: null,
        fields: null,
        readings: null,
      },
      graphics: {
        id: null,
        name: null,
        variables: [],
        canvasJson: null,
        liveValues: {},
        usedVariables: [],
        defaultAsset: null,
      },
      expand: false,
      isSaving: false,
      hasError: false,
      showFloorPlanLegends: false,
      // canvas: null,
      config: {},
      activeObject: null,
      graphicUtil: null,
      currentDragElement: null,
      objectPropertiesDialog: false,
      currentObject: null,
      liveData: {},
      pubSubWatcherKey: null,
      trendPopup: {
        visible: false,
        analyticsConfig: {
          period: 0,
          mode: 1,
          dateFilter: NewDateHelper.getDatePickerObject(22),
          dataPoints: [],
          hidechartoptions: true,
          hidecharttypechanger: true,
        },
      },
      controlPopup: {
        visible: false,
        controlConfig: {
          title: null,
          assetId: null,
          fieldId: null,
          groupId: null,
        },
        setVal: null,
        saving: false,
      },
      spaceMapping: {
        visible: false,
        spaceDisplayName: null,
        spaceId: null,
      },
      noGraphicsAvailable: false,
      loadingState: false,
      variablesLoaded: false,
      contextmenu: null,
      noAssetMapped: false,
      categoryAssets: [],
      selectedCategoryAsset: null,
      controlPoints: [],
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
      graphicsListForActions: null,
      assetListForActions: null,
      clickActions: null,
      selectedSpaceInfo: null,
      filterVariablesInput: '',
      showInsertVariablePopover: false,
      polygonOptions: {
        polygonMode: false,
        pointArray: new Array(),
        lineArray: new Array(),
        activeLine: null,
        activeShape: false,
      },
      reservationForm: {
        visible: false,
        reservation: null,
      },
    }
  },
  destroyed() {
    this.destroyGraphics()
  },
  mounted() {
    this.$nextTick(() => {
      this.initGraphics()
    })
  },
  computed: {
    resolution() {
      if (this.width && this.height) {
        return {
          w: this.width,
          h: this.height,
        }
      }
      return null
    },
  },
  created() {
    this.$store.dispatch('loadAssetCategory')
  },
  watch: {
    'graphicsContext.id': function() {
      this.$nextTick(() => {
        this.initGraphics()
      })
    },
    defaultAsset: function() {
      if (this.defaultAsset) {
        this.selectedCategoryAsset = this.defaultAsset
        this.changeAsset(this.defaultAsset)
      }
    },
    zoomLevel: function() {
      this.fitToResolution()
    },
    resolution: function() {
      this.fitToResolution()
    },
  },
  methods: {
    loadDefaultValues(objectActions) {
      let self = this
      if (
        objectActions.hyperLink &&
        objectActions.hyperLink.enable &&
        objectActions.hyperLink.link_list &&
        objectActions.hyperLink.link_list.length
      ) {
        let list = objectActions.hyperLink.link_list[0]
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
          }
          self.loadGraphicAssetList(list)
        })
      }
    },
    setAssetId(id, index) {
      this.$set(this.objectActions.hyperLink.link_list[index], 'assetId', id)
    },
    getPredefinedColors() {
      return colors.readingcardColors
    },
    getColorThemes() {
      if (this.currentObject.fgraphic.themeGroup) {
        return themes[this.currentObject.fgraphic.themeGroup]
      }
      return []
    },
    initGraphics() {
      if (this.canvas) {
        this.destroyGraphics()
      }
      if (this.readonly) {
        this.canvas = new fabric.fabric.Canvas(this.$refs['graphicsCanvas'], {
          selection: false,
          renderOnAddRemove: false,
          skipTargetFind: false,
        })
      } else {
        this.canvas = new fabric.fabric.Canvas(this.$refs['graphicsCanvas'], {
          selection: true,
        })
      }
      this.graphicUtil = new FGraphicUtil.FGraphic(
        this.canvas,
        this.readonly,
        this.graphics
      )
      this.graphics.variables = []

      if (this.graphicsContext.id) {
        this.loadingState = true
        this.$http
          .get('/v2/graphics/getById?recordId=' + this.graphicsContext.id)
          .then(async response => {
            if (response.data.result.graphics) {
              //  response.data.result.graphics = this.tempChanges(response.data.result.graphics)
              this.$emit('loaded', response.data.result.graphics)
              let assetId =
                this.defaultAsset || response.data.result.graphics.assetId
              if (response.data.result.graphics.assetId) {
                this.currentAsset.defaultAsset =
                  response.data.result.graphics.assetId
              }
              await this.loadAsset(assetId)
              if (!assetId) {
                this.loadingState = false
              }
              if (!(!this.showFilters && assetId)) {
                util
                  .loadCategoryAssetsForGraphics(response.data.result.graphics)
                  .then(async assets => {
                    this.categoryAssets = assets
                    if (!this.selectedCategoryAsset) {
                      this.selectedCategoryAsset = assetId
                    }
                    if (!assetId && assets && assets.length) {
                      this.selectedCategoryAsset = assets[0].id
                      await this.loadAsset(assets[0].id)
                    } else if (!assetId && this.showFilters) {
                      this.noAssetMapped = true
                    }
                  })
              }

              if (response.data.result.graphics.variables) {
                let customVariables = JSON.parse(
                  response.data.result.graphics.variables
                )
                if (customVariables.length) {
                  this.graphics.variables.push(...customVariables)
                }
              }

              this.graphics.name = response.data.result.graphics.name
              this.graphics.id = response.data.result.graphics.id

              if (response.data.result.graphics.canvas) {
                let that = this
                let minTop = null
                let maxTop = null
                let minLeft = null
                let maxLeft = null
                let appDomainRegx = new RegExp(
                  'app.facilio.com\\/api\\/v2\\/files\\/preview\\/',
                  'i'
                )
                let canvasStr = null
                if (document.domain.indexOf('tv.facilio.com') >= 0) {
                  canvasStr = response.data.result.graphics.canvas.replace(
                    appDomainRegx,
                    'tv.facilio.com/api/v2/files/preview/'
                  )
                } else if (isPortalDomain()) {
                  canvasStr = response.data.result.graphics.canvas.replace(
                    appDomainRegx,
                    document.domain + '/api/v2/files/preview/'
                  )
                }
                let cv = ''
                if (
                  response.data.result &&
                  response.data.result.graphics &&
                  response.data.result.graphics.canvas
                ) {
                  cv = canvasStr
                    ? canvasStr
                    : response.data.result.graphics.canvas
                }
                this.canvas.loadFromJSON(
                  that.preJsonFormat(cv),
                  function() {
                    if (that.readonly) {
                      that.canvas.backgroundColor = null
                      that.loadUsedVariables()
                    } else if (!response.data.result.graphics.assetCategoryId) {
                      that.refreshLiveData()
                    }
                    that.canvasFitToScreen()
                    that.canvas.renderAll()
                    that.setupEditor()

                    if (that.readonly) {
                      that.canvasFitToScreenWeb(
                        minTop,
                        maxTop,
                        minLeft,
                        maxLeft
                      )
                    }
                  },
                  function(o, object) {
                    let top = object.get('top')
                    let left = object.get('left')
                    let toph = top
                    if (object.get('height') && object.get('scaleY')) {
                      toph = top + object.get('height') * object.get('scaleY')
                    }
                    let leftw = left
                    if (object.get('width') && object.get('scaleX')) {
                      leftw = left + object.get('width') * object.get('scaleX')
                    }
                    if (!minTop || object.get('top') < minTop) {
                      minTop = object.get('top')
                    }
                    if (!maxTop || toph > maxTop) {
                      maxTop = toph
                    }
                    if (!minLeft || object.get('left') < minLeft) {
                      minLeft = object.get('left')
                    }
                    if (!maxLeft || leftw > maxLeft) {
                      maxLeft = leftw
                    }
                    if (that.readonly) {
                      object.set('selectable', false)
                      if (
                        object.fgraphic &&
                        object.fgraphic.spaceMappingBindingVariable
                      ) {
                        that.showFloorPlanLegends = true
                      }
                    }
                  }
                )
              } else {
                this.setupEditor()
              }
            }
          })
      } else if (
        this.graphicsContext.assetId &&
        this.graphicsContext.assetCategoryId
      ) {
        this.canvas.setZoom(0.9)
        this.loadingState = true
        let url =
          '/v2/graphics/getByAssetCategory?assetCategoryId=' +
          this.graphicsContext.assetCategoryId
        if (this.graphicsContext.assetId) {
          url += '&assetId=' + this.graphicsContext.assetId
        }
        this.$http.get(url).then(response => {
          if (
            response.data.result.graphics_list &&
            response.data.result.graphics_list.length
          ) {
            this.loadAsset(this.graphicsContext.assetId)
            if (!(!this.showFilters && this.graphicsContext.assetId)) {
              util
                .loadCategoryAssetsForGraphics(this.graphicsContext)
                .then(assets => {
                  this.categoryAssets = assets
                  if (!this.selectedCategoryAsset) {
                    this.selectedCategoryAsset = this.graphicsContext.assetId
                  }
                  if (
                    !this.graphicsContext.assetId &&
                    assets &&
                    assets.length
                  ) {
                    this.selectedCategoryAsset = assets[0].id
                    this.loadAsset(assets[0].id)
                  } else if (!this.graphicsContext.assetId) {
                    this.noAssetMapped = true
                  }
                })
            }

            this.noGraphicsAvailable = false
            let graphicsObj = response.data.result.graphics_list.find(
              g => g.assetId === this.graphicsContext.assetId
            )
            if (!graphicsObj) {
              graphicsObj = response.data.result.graphics_list[0]
            }

            if (graphicsObj.assetId) {
              this.currentAsset.defaultAsset = graphicsObj.assetId
            }

            if (graphicsObj.variables) {
              let customVariables = JSON.parse(graphicsObj.variables)
              if (customVariables.length) {
                this.graphics.variables.push(...customVariables)
              }
            }

            this.graphics.name = graphicsObj.name
            this.graphics.id = graphicsObj.id

            if (graphicsObj.canvas) {
              let that = this
              let minTop = null
              let maxTop = null
              let minLeft = null
              let maxLeft = null
              let appDomainRegx = new RegExp(
                'app.facilio.com\\/api\\/v2\\/files\\/preview\\/',
                'i'
              )
              let canvasStr = null
              if (document.domain.indexOf('tv.facilio.com') >= 0) {
                canvasStr = graphicsObj.canvas.replace(
                  appDomainRegx,
                  'tv.facilio.com/api/v2/files/preview/'
                )
              } else if (isPortalDomain()) {
                canvasStr = graphicsObj.canvas.replace(
                  appDomainRegx,
                  document.domain + '/api/v2/files/preview/'
                )
              }
              let cv = ''
              if (graphicsObj && graphicsObj.canvas) {
                cv = canvasStr ? canvasStr : graphicsObj.canvas
              }
              this.canvas.loadFromJSON(
                that.preJsonFormat(cv),
                function() {
                  if (that.readonly) {
                    that.canvas.backgroundColor = null
                    that.loadUsedVariables()
                  }
                  that.canvasFitToScreen()
                  that.canvas.renderAll()
                  that.setupEditor()

                  if (that.readonly) {
                    that.canvasFitToScreenWeb(minTop, maxTop, minLeft, maxLeft)
                  }
                },
                function(o, object) {
                  let top = object.get('top')
                  let left = object.get('left')
                  let toph = top
                  if (object.get('height') && object.get('scaleY')) {
                    toph = top + object.get('height') * object.get('scaleY')
                  }
                  let leftw = left
                  if (object.get('width') && object.get('scaleX')) {
                    leftw = left + object.get('width') * object.get('scaleX')
                  }
                  if (!minTop || object.get('top') < minTop) {
                    minTop = object.get('top')
                  }
                  if (!maxTop || toph > maxTop) {
                    maxTop = toph
                  }
                  if (!minLeft || object.get('left') < minLeft) {
                    minLeft = object.get('left')
                  }
                  if (!maxLeft || leftw > maxLeft) {
                    maxLeft = leftw
                  }
                  if (that.readonly) {
                    object.set('selectable', false)
                    if (
                      object.fgraphic &&
                      object.fgraphic.spaceMappingBindingVariable
                    ) {
                      that.showFloorPlanLegends = true
                    }
                  }
                }
              )
            } else {
              this.loadingState = false
              this.setupEditor()
            }
          } else {
            this.loadingState = false
            this.noGraphicsAvailable = true
          }
        })
      } else {
        this.loadAsset(this.graphicsContext.assetId)
        this.setupEditor()
      }
    },
    preJsonFormat(canvas) {
      if (canvas && canvas.trim() !== '') {
        let canvasObject = JSON.parse(canvas)
        if (canvasObject.objects) {
          canvasObject.objects.forEach(obj => {
            let src = this.formatImgUrl(obj)
            this.$set(obj, 'src', src)
          })
        }
        return JSON.stringify(canvasObject)
      }
      return canvas
    },
    formatImgUrl(obj) {
      if (obj && obj.src) {
        let url = obj.src
        if (obj && url) {
          let d = url.split('/')
          let fileId = -1
          if (obj.fgraphic && obj.fgraphic.fileId) {
            fileId = obj.fgraphic.fileId
          } else {
            fileId = d[d.length - 1] ? Number(d[d.length - 1]) : null
          }
          let domain = document.domain
          let newUrl = ''
          if (domain.indexOf('tv.facilio.com') >= 0) {
            newUrl = `https://tv.facilio.com/api/v2/files/preview/${fileId}?fetchOriginal=true`
            return newUrl
          } else if (isPortalDomain()) {
            newUrl = `https://${domain}/api/v2/files/preview/${fileId}?fetchOriginal=true`
            return newUrl
          } else {
            newUrl = `${getBaseURL()}/v2/files/preview/${fileId}?fetchOriginal=true`
            return newUrl
          }
        }
      }
      return obj.src
    },
    toggleTheme(theme) {
      let color = theme === 'dark' ? '#FFFFFF' : '#0c0c0c'
      this.canvas.forEachObject(function(obj) {
        if (obj.fgraphic && obj.fgraphic.formatText) {
          obj.set('fill', color)
          obj.fgraphic.styles.fill = color
        }
      })
      this.canvas.requestRenderAll()
    },
    openSelectedReadingInAnalytics() {
      if (
        this.trendPopup.analyticsConfig.dataPoints &&
        this.trendPopup.analyticsConfig.dataPoints.length
      ) {
        this.trendPopup.visible = false
        this.jumpReadingsToAnalytics(
          this.trendPopup.analyticsConfig.dataPoints,
          null,
          null,
          null
        )
      }
    },
    openInAnalytics() {
      let dataPoints = []
      if (this.graphics.usedVariables && this.graphics.usedVariables.length) {
        for (let readingVar of this.graphics.usedVariables) {
          let varObj = this.graphics.variables.find(v => v.key === readingVar)
          if (varObj) {
            let readingObj = this.currentAsset.readings
              ? this.currentAsset.readings.find(rd => rd.name === varObj.select)
              : null
            let fieldId = null
            if (readingObj) {
              fieldId = readingObj.id
            } else {
              if (this.graphics.liveValues[readingVar]) {
                fieldId = this.graphics.liveValues[readingVar].fieldid
              }
            }
            if (fieldId) {
              dataPoints.push({
                parentId: varObj.parentId,
                yAxis: {
                  fieldId: fieldId,
                  aggr: 3,
                },
              })
            }
          }
        }
      }
      if (dataPoints && dataPoints.length) {
        this.jumpReadingsToAnalytics(dataPoints, null, null, null)
      } else {
        this.$message({
          type: 'warning',
          message: 'No readings mapped in this graphics to explore!',
        })
      }
    },
    expandEditor() {
      this.expand = !this.expand
    },
    exportAsImage() {
      if (this.canvas) {
        let dataUrl = this.canvas.toDataURL('image/jpg')
        if (dataUrl) {
          if (this.$refs['downloadLink']) {
            let downloadFileName = this.graphics.name
              ? this.graphics.name
              : 'Facilio - Graphics'
            downloadFileName += '.png'
            this.$refs['downloadLink'].href = dataUrl
            this.$refs['downloadLink'].setAttribute(
              'download',
              downloadFileName
            )
            this.$refs['downloadLink'].click()
          }
        }
      }
    },
    tempChanges(graphics) {
      // userd to migrate new pump to old pumps
      let canvas = graphics.canvas.replaceAll(
        'com.facilio.graphics.shape.chiller.pump_right',
        'com.facilio.graphics.shape.chiller.pump_right_old'
      )
      let cv = canvas.replaceAll('chiller_pump_right', 'chiller_pump_right_old')
      let c1 = cv.replaceAll(
        'com.facilio.graphics.shape.chiller.pump_left',
        'com.facilio.graphics.shape.chiller.pump_left_old'
      )
      let c = c1.replaceAll('chiller_pump_left', 'chiller_pump_left_old')
      // let canvas = graphics.canvas.replaceAll("com.facilio.graphics.shape.chiller.pump_right_old_old", "com.facilio.graphics.shape.chiller.pump_right_old")
      // let cv = canvas.replaceAll("chiller_pump_right_old", "chiller_pump_right_old");
      //  let c1 = cv.replaceAll("com.facilio.graphics.shape.chiller.pump_left_old_old", "com.facilio.graphics.shape.chiller.pump_left_old")
      // let c = c1.replaceAll("chiller_pump_left_old_old", "chiller_pump_left_old");
      this.$set(graphics, 'canvas', c)
      return graphics
    },
    loadCategoryAssets(graphics, assetId, callback) {
      if (!this.showFilters && assetId) {
        return
      }
      let param
      if (graphics.applyTo) {
        let applyTo = JSON.parse(graphics.applyTo)
        if (applyTo.applyToType === 2 && applyTo.applyToAssetIds.length > 0) {
          param = {
            filters: {
              id: {
                operatorId: 36,
                value: applyTo.applyToAssetIds.map(String),
              },
            },
          }
        } else if (applyTo.applyToType === 3 && applyTo.criteria) {
          param = { filterCriterias: applyTo.criteria }
        } else {
          param = { categoryId: graphics.assetCategoryId }
        }
      } else {
        param = { categoryId: graphics.assetCategoryId }
      }
      util.loadAsset(param).then(response => {
        this.categoryAssets = response.assets
        if (!this.selectedCategoryAsset) {
          this.selectedCategoryAsset = assetId
        }
        callback(this.categoryAssets)
      })
    },
    associateControlPointAsset(ca, resource) {
      ca.label = resource.name
      ca.assetId = resource.id
      ca.pointId = null

      this.loadAssetControlPoints(resource.id)
      this.showControlPointAssetPicker = false
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
    loadControlPoints() {
      if (
        this.currentObject &&
        this.currentObject.fgraphic.actions.controlAction.control_list &&
        this.currentObject.fgraphic.actions.controlAction.control_list.length &&
        this.currentObject.fgraphic.actions.controlAction.control_list[0]
          .type == 'control_point'
      ) {
        let assetId = this.currentObject.fgraphic.actions.controlAction
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
    setControlGroup(controlAction) {
      let cg = this.controlGroups.find(c => c.id === controlAction.groupId)
      if (cg) {
        controlAction.actionName = cg.name
      }
    },
    associateSpace(selectedSpace) {
      this.spaceMapping.spaceDisplayName = selectedSpace.name
      this.spaceMapping.spaceId = selectedSpace.id
      this.spaceMapping.visible = false

      let key =
        'space.' + selectedSpace.name.replace(/[^A-Z0-9]/gi, '_').toLowerCase()
      let varObj = {
        label: selectedSpace.name,
        key: key,
        fetchType: 'space',
        module: 'space',
        select: '',
        parentId: selectedSpace.id,
        unit: '',
        type: 'custom',
      }

      this.currentObject.fgraphic.spaceMappingBindingVariable = key

      if (!this.graphics.variables.find(v => v.key === key)) {
        this.graphics.variables.push(varObj)

        this.fetchGraphicsData([
          this.graphics.variables.find(v => v.key === key),
        ]).then(() => {
          if (this.currentObject) {
            this.graphicUtil.updateLiveValue(this.currentObject, this.graphics)
            this.canvas.requestRenderAll()
          }
        })
      }
    },
    getClickActions(actions, readings) {
      let actionList = []
      if (!actions) {
        if (readings && readings.length) {
          actionList.push({
            actionName: 'Show Trend',
            actionType: 'showTrend',
            data: readings,
          })
        }
      } else {
        if (actions.showTrend.enable && readings && readings.length) {
          actionList.push({
            actionName: 'Show Trend',
            actionType: 'showTrend',
            data: readings,
          })
        }

        if (actions.controlAction.enable) {
          for (let c of actions.controlAction.control_list) {
            actionList.push({
              actionName: c.actionName,
              actionType: 'controlAction',
              data: c,
            })
          }
        }

        if (actions.hyperLink.enable) {
          for (let c of actions.hyperLink.link_list) {
            actionList.push({
              actionName: c.actionName,
              actionType: 'hyperLink',
              data: c,
            })
          }
        }
      }
      return actionList
    },
    handleClickAction(action) {
      let selfHost = window.location.protocol + '//' + window.location.host
      if (action.actionType === 'showTrend') {
        this.trendPopup.analyticsConfig.dataPoints = action.data
        this.trendPopup.visible = true
      } else if (action.actionType === 'controlAction') {
        this.controlPopup.controlConfig.title = action.data.actionName
        if (action.data.type === 'control_group') {
          this.controlPopup.controlConfig.groupId = action.data.groupId
          this.controlPopup.controlConfig.assetId = null
          this.controlPopup.controlConfig.fieldId = null
        } else {
          if (
            action.data.assetCategoryId &&
            this.currentAsset.record &&
            action.data.assetId &&
            this.currentAsset.record.id &&
            this.currentAsset.record.category.id ===
              action.data.assetCategoryId &&
            (action.data.assetId === this.currentAsset.record.id ||
              action.data.assetId === this.currentAsset.defaultAsset) &&
            this.currentAsset.id
          ) {
            this.controlPopup.controlConfig.assetId = this.currentAsset.id
          } else {
            this.controlPopup.controlConfig.assetId = action.data.assetId
          }
          this.controlPopup.controlConfig.fieldId = action.data.fieldId
          this.controlPopup.controlConfig.groupId = null
        }
        this.controlPopup.visible = true
      } else if (action.actionType === 'hyperLink') {
        if (action.data.url || action.data.id) {
          let url = this.graphicUtil.applyLiveVariables(action.data.url, true)
          if (action.data.linkType === 'graphics') {
            url = selfHost + '/app/em/graphics/view/' + action.data.id
            if (action.data.assetId) {
              url += '?assetId=' + action.data.assetId
            }
          }
          if (action.data.target === 'self') {
            if (url.startsWith(selfHost)) {
              if (url.startsWith(selfHost + '/app/em/newdashboard/')) {
                url = url.replace(
                  selfHost + '/app/em/newdashboard/',
                  selfHost + '/app/home/dashboard/'
                )
              }
              let appUrl = url.replace(selfHost, '')
              this.$router.push({ path: appUrl })
            } else {
              window.location.href = url
            }
          } else if (action.data.target === 'popup') {
            this.$popupView.openPopup({
              type: action.data.linkType,
              url: action.data.url,
              alt: '',
              dashboardId: '',
              reportId: '',
              graphicsId: action.data.id,
              assetId: action.data.assetId,
              target: action.data.target,
            })
          } else {
            window.open(url, '_blank')
          }
        }
      }
      this.clickActions = null
    },
    onSetControlValue() {
      if (this.$account && this.$account.org.id === 321) {
        setTimeout(() => {
          this.refreshLiveData()
        }, 2000)
      }
      this.resetControlValue()
    },
    resetControlValue() {
      this.controlPopup = {
        visible: false,
        controlConfig: {
          title: null,
          assetId: null,
          fieldId: null,
          groupId: null,
        },
        setVal: null,
        saving: false,
      }
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let route = findRouteForTab(tabType, { config }) || {}

        if (!isEmpty(route)) {
          return this.$router.resolve({ name: route.name }).href
        } else {
          return null
        }
      } else {
        return '/app/home/portfolio'
      }
    },
    openSpace(space) {
      let parentPath = this.findRoute()

      if (parentPath) {
        this.$router.push({
          path: `${parentPath}/site/${space.site.id}/space/${space.id}`,
        })
      }
      this.selectedSpaceInfo = null
    },
    openReservePopup(space) {
      this.selectedSpaceInfo = null
      this.reservationForm.reservation = {
        siteId: space.site.id + '',
        space: space,
        durationType: '2',
      }
      this.reservationForm.visible = true
    },
    destroyGraphics() {
      this.$emit('loaded', null)
      if (this.pubSubWatcherKey) {
        let readings = []
        for (let varObj of this.graphics.variables.filter(
          v => v.type === 'reading'
        )) {
          readings.push({
            parentId: varObj.parentId + '',
            fieldName: varObj.select,
            moduleName: varObj.module,
          })
        }
        this.unsubscribe(this.pubSubWatcherKey, 'readingChange', {
          readings: readings,
        })
        this.pubSubWatcherKey = null
      } else if (this.$wms) {
        let readings = []
        for (let varObj of this.graphics.variables.filter(
          v => v.type === 'reading'
        )) {
          readings.push({
            parentId: varObj.parentId + '',
            fieldName: varObj.select,
            moduleName: varObj.module,
          })
        }
        this.$http
          .post('/v2/fetchLiveUpdateFields', {
            liveUpdateFields: readings,
          })
          .then(response => {
            let readingFields = response.data.result.liveUpdateFields
            if (readingFields && readingFields.length) {
              for (let reading of readingFields) {
                let topic =
                  '__livereading__/' + reading.parentId + '/' + reading.fieldId
                this.$wms.unsubscribe(topic, this.refreshLiveData)
              }
            }
          })
      }
      if (this.canvas) {
        this.canvas.dispose()
      }
      this.canvas = null
      this.showFloorPlanLegends = false
      this.noAssetMapped = false
      this.noGraphicsAvailable = false
      this.selectedCategoryAsset = null
      if (this.graphicUtil) {
        this.graphicUtil.destroy()
        this.graphicUtil = null
      }
      this.graphics = {
        id: null,
        name: null,
        variables: [],
        canvasJson: null,
        liveValues: {},
      }
    },
    canvasFitToScreen() {
      // need to rewamp
      if (this.readonly && this.$mobile) {
        this.canvas.selection = false
        let clientWidth = this.width
          ? this.width
          : document.getElementById('graphics-widget').offsetWidth
        let clientHeight = this.height
          ? this.height
          : document.getElementById('graphics-widget').offsetHeight
        if (clientWidth && clientHeight) {
          let height10 = (10 / 100) * clientHeight
          let diffPercentage =
            (clientHeight + height10) / this.canvas.getHeight()
          this.canvas.setHeight(clientWidth + 100)
          this.canvas.setWidth(clientHeight + 130)
          let newZoom =
            this.canvas.getZoom() * diffPercentage > 20
              ? 20
              : this.canvas.getZoom() * diffPercentage
          this.canvas.setZoom(newZoom)
        }
      }
    },
    canvasFitToScreenWeb(minTop, maxTop, minLeft, maxLeft) {
      if (!this.readonly || this.$mobile || !this.canvas) {
        return
      }
      let modifyTop = null
      if (minTop < 0) {
        modifyTop = minTop * -1
      } else if (minTop < 50) {
        modifyTop = 50 - minTop
      }
      if (modifyTop) {
        this.canvas.forEachObject(function(obj) {
          let existTop = obj.get('top')
          obj.set({ top: existTop + modifyTop })
        })
      }
      let width = maxLeft - minLeft + 100
      let ratio = 1200 / width
      if (ratio < 1) {
        this.canvas.setZoom(ratio)
      }
      if (
        this.graphicsContext.assetId &&
        this.graphicsContext.assetCategoryId
      ) {
        this.canvas.setZoom(1000 / 1200)
      }
      this.canvas.requestRenderAll()

      this.fitToResolution()
    },
    fitToResolution() {
      if (this.zoomLevel && this.zoomLevel >= 10 && this.zoomLevel <= 200) {
        let zoom = this.zoomLevel * 0.01
        this.canvas.setZoom(zoom)
        this.canvas.requestRenderAll()
      } else if (
        this.resolution &&
        this.resolution.w &&
        this.resolution.h &&
        this.$account &&
        this.$account.org.id === 297
      ) {
        let clientWidth = this.$refs['graphicsCanvas'].parentNode.parentNode
          .parentNode.clientWidth
        let clientHeight = this.$refs['graphicsCanvas'].parentNode.parentNode
          .parentNode.clientHeight

        let wzoom =
          this.resolution.w < 300
            ? clientWidth / 1200
            : this.resolution.w / 1200

        let hzoom =
          this.resolution.h < 200
            ? clientHeight / 1000
            : this.resolution.h / 1000

        let zoom = wzoom < hzoom ? wzoom : hzoom
        if (this.$account && this.$account.org.id !== 297) {
          zoom = wzoom
        }

        if (zoom >= 0.4 && zoom < 1) {
          this.canvas.setWidth(clientWidth)
          this.canvas.setHeight(clientHeight)
          this.canvas.setZoom(zoom)
          this.canvas.requestRenderAll()
        }
      }
    },
    loadCurrentOccupancy() {
      let reportParams = {
        xField: {
          field_id: 540709,
          module_id: 39641,
        },
        yField: [null],
        dateField: null,
        groupBy: null,
        criteria: {
          pattern: '(1 and 2)',
          conditions: {
            1: {
              fieldName: 'datetime',
              value: null,
              columnName: 'CustomModuleData.DATETIME_CF1',
              operatorId: 2,
              isResourceOperator: false,
              parseLabel: null,
              valueArray: [],
              operatorLabel: 'is not empty',
              active: false,
              operatorsDataType: {
                dataType: 'DATE_TIME',
                displayType: 'DATETIME',
              },
              isSpacePicker: false,
            },
            2: {
              fieldName: 'datetime_1',
              isResourceOperator: false,
              parseLabel: null,
              valueArray: [],
              operatorLabel: 'is empty',
              value: null,
              active: false,
              operatorsDataType: {
                dataType: 'DATE_TIME',
                displayType: 'DATETIME',
              },
              isSpacePicker: false,
              operatorId: 1,
              columnName: 'CustomModuleData.DATETIME_CF2',
            },
          },
          resourceOperator: false,
        },
        sortFields: [
          {
            field_id: 'y-field',
          },
        ],
        sortOrder: 3,
        limit: 30,
        userFilters: null,
        moduleType: null,
        moduleName: 'custom_occupanttracking',
      }
      return new Promise((resolve, reject) => {
        this.$http
          .post('/v2/report/fetchReportData', reportParams)
          .then(response => {
            let spaceOccupancyData = {}
            let occupancyData = []
            if (response.data.result && response.data.result.reportData) {
              for (let d of response.data.result.reportData.data) {
                spaceOccupancyData[d.X + ''] = d.Id
                occupancyData.push(d.Id)
              }
            }
            occupancyData.sort()
            resolve({
              spaceOccupancyData: spaceOccupancyData,
              occupancyData: occupancyData,
            })
          })
          .catch(error => {
            if (error) {
              reject()
            }
          })
      })
    },
    resetPan() {
      this.canvas.setZoom(1)
      this.canvas.absolutePan(new fabric.Point(0, 0))
      this.canvas.requestRenderAll()
    },
    setupviewerEvents() {
      let self = this
      this.canvas.on('mouse:down', function(opt) {
        let evt = opt.e
        if (evt.altKey === true) {
          self.config.isDragging = true
          self.config.lastPosX = evt.clientX
          self.config.lastPosY = evt.clientY
        }
      })
      this.canvas.on('mouse:move', function(opt) {
        if (self.config.isDragging) {
          let e = opt.e
          this.viewportTransform[4] += e.clientX - self.config.lastPosX
          this.viewportTransform[5] += e.clientY - self.config.lastPosY
          this.requestRenderAll()
          self.config.lastPosX = e.clientX
          self.config.lastPosY = e.clientY
        }
      })
      this.canvas.on('mouse:up', function() {
        self.config.isDragging = false
        let objects = this.getObjects()
        for (let i = 0; i < objects.length; i++) {
          objects[i].setCoords()
        }
      })
    },
    setupEditor() {
      let self = this
      if (this.readonly) {
        if (this.graphics.id === 322) {
          this.loadCurrentOccupancy().then(data => {
            let clusters = ckmeans(data.occupancyData, 3)

            let colorScale = d3
              .scaleLinear()
              .range(['#fef96d', '#eead09', '#ff0000'])
              .domain(clusters)

            this.graphics.occupancyData = data
            this.graphics.occupancyColorScale = colorScale

            this.graphicUtil.updateAllLiveValues(
              this.canvas.getObjects(),
              this.graphics
            )
            this.canvas.requestRenderAll()
          })
        } else if (this.graphics.id === 324) {
          this.graphics.fireAlarm = {
            1249848: {
              message: 'Fire alarm occurred.',
            },
          }
        }
        if (
          !this.$mobile &&
          (typeof this.drilldown === 'undefined' ||
            this.drilldown === true ||
            this.drilldown === 'true')
        ) {
          let storedTheme = window.localStorage.getItem('theme')
          if (storedTheme && storedTheme === 'black') {
            this.toggleTheme('dark')
          }

          this.canvas.on('mouse:down', function(e) {
            self.clickActions = null
            self.selectedSpaceInfo = null
            if (e.target && e.target.fgraphic) {
              if (e.target.fgraphic.spaceMappingBindingVariable) {
                let top = e.e.offsetY + 20
                let left = e.e.offsetX + 20
                let bottomSpace = window.innerHeight - e.e.clientY
                let divHeight = 260
                if (divHeight > bottomSpace - 40) {
                  top = e.e.offsetY - divHeight
                }

                self.selectedSpaceInfo = {
                  top: top,
                  left: left,
                  space:
                    self.graphics.liveValues[
                      e.target.fgraphic.spaceMappingBindingVariable
                    ] &&
                    self.graphics.liveValues[
                      e.target.fgraphic.spaceMappingBindingVariable
                    ].value &&
                    self.graphics.liveValues[
                      e.target.fgraphic.spaceMappingBindingVariable
                    ].value.length
                      ? self.graphics.liveValues[
                          e.target.fgraphic.spaceMappingBindingVariable
                        ].value[0]
                      : null,
                }
                return
              }

              let readingVariables = []
              if (e.target.fgraphic.formatText) {
                let formatText = e.target.fgraphic.formatText
                let matched = formatText.match(/[^\\${}]+(?=\})/g)
                if (matched && matched.length) {
                  readingVariables.push(...matched)
                }
              }
              if (e.target.fgraphic.enableStateBinding) {
                readingVariables.push(e.target.fgraphic.stateBindingVariable)
              }
              if (e.target.fgraphic.enableAnimateBinding) {
                readingVariables.push(e.target.fgraphic.animateBindingVariable)
              }
              if (e.target.fgraphic.enableProgressBinding) {
                readingVariables.push(e.target.fgraphic.progressBindingVariable)
              }
              let dataPoints = []
              if (readingVariables && readingVariables.length) {
                for (let readingVar of readingVariables) {
                  let varObj = self.graphics.variables.find(
                    v => v.key === readingVar
                  )
                  if (varObj) {
                    let readingObj = null
                    if (varObj.type === 'custom') {
                      readingObj = self.currentAsset.readings
                        ? self.currentAsset.readings.find(rd => {
                            if (
                              rd.name === varObj.select &&
                              rd.module.name === varObj.module
                            ) {
                              return rd
                            }
                          })
                        : null
                    } else {
                      readingObj = self.currentAsset.readings
                        ? self.currentAsset.readings.find(
                            rd => rd.name === varObj.select
                          )
                        : null
                    }

                    let fieldId = null
                    if (readingObj) {
                      fieldId = readingObj.id
                    } else {
                      if (self.graphics.liveValues[readingVar]) {
                        fieldId = self.graphics.liveValues[readingVar].fieldid
                      }
                    }
                    if (fieldId) {
                      dataPoints.push({
                        parentId: varObj.parentId,
                        yAxis: {
                          fieldId: fieldId,
                          aggr: 3,
                        },
                      })
                    }
                  }
                }
              }
              let clickActions = self.getClickActions(
                e.target.fgraphic.actions,
                dataPoints
              )
              if (clickActions && clickActions.length) {
                if (clickActions.length === 1) {
                  self.handleClickAction(clickActions[0])
                } else {
                  self.clickActions = {
                    top: e.e.offsetY + 30,
                    left: e.e.offsetX + 20,
                    actions: clickActions,
                  }
                }
              }
            }
          })

          this.canvas.on('mouse:over', function(e) {
            if (
              e.target &&
              e.target.fgraphic &&
              e.target.fgraphic.type === 'space_zone'
            ) {
              e.target.set('opacity', 0.7)
            }
            self.canvas.renderAll()
          })

          this.canvas.on('mouse:out', function(e) {
            if (
              e.target &&
              e.target.fgraphic &&
              e.target.fgraphic.type === 'space_zone'
            ) {
              e.target.set('opacity', 0.2)
            }
            self.canvas.renderAll()
          })
        }
        this.setupviewerEvents()
        return
      }
      let options = {
        distance: 10,
        width: this.canvas.width,
        height: this.canvas.height,
        param: {
          stroke: 'rgb(249, 249, 253)',
          strokeWidth: 1,
          selectable: false,
          excludeFromExport: true,
        },
      }

      // Create a new instance of the Image class
      let img = new Image()
      // When the image loads, set it as background image
      img.onload = function() {
        self.canvas.backgroundColor = new fabric.fabric.Pattern({ source: img })
      }
      // Define the Data URI
      let myDataURL =
        'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAYAAACNiR0NAAAAT0lEQVQ4T2N8/fr/fwYiADfPP4avX5gIqmQEGSgqyshISOW373//c3EyE1Q3aiDukBwNQ5xhQ/1kAwpsQomaFHnqu3A06+EM/9GcQr+cAgBGqZrSrFyxegAAAABJRU5ErkJggg=='
      // Set the src of the image with the base64 string
      img.src = myDataURL
      this.canvas.on('mouse:down', function(opt) {
        let evt = opt.e
        if (evt.altKey === true) {
          self.config.isDragging = true
          this.selection = false
          self.config.lastPosX = evt.clientX
          self.config.lastPosY = evt.clientY
        } else {
          if (
            opt.target &&
            self.polygonOptions.pointArray.length &&
            opt.target.id == self.polygonOptions.pointArray[0].id
          ) {
            PolygonUtil.generatePolygon(self.polygonOptions, self.canvas)
          }
          if (self.polygonOptions.polygonMode) {
            PolygonUtil.addPoint(self.polygonOptions, opt, self.canvas)
          }
        }
      })
      this.canvas.on('mouse:move', function(opt) {
        if (self.config.isDragging) {
          let e = opt.e
          this.viewportTransform[4] += e.clientX - self.config.lastPosX
          this.viewportTransform[5] += e.clientY - self.config.lastPosY
          this.requestRenderAll()
          self.config.lastPosX = e.clientX
          self.config.lastPosY = e.clientY
        } else {
          if (
            self.polygonOptions.activeLine &&
            self.polygonOptions.activeLine.class == 'line'
          ) {
            let pointer = self.canvas.getPointer(opt.e)
            self.polygonOptions.activeLine.set({ x2: pointer.x, y2: pointer.y })

            let points = self.polygonOptions.activeShape.get('points')
            points[self.polygonOptions.pointArray.length] = {
              x: pointer.x,
              y: pointer.y,
            }
            self.polygonOptions.activeShape.set({
              points: points,
            })
            self.canvas.renderAll()
          }
        }
      })
      this.canvas.on('mouse:up', function() {
        self.config.isDragging = false
        this.selection = true
        let objects = this.getObjects()
        for (let i = 0; i < objects.length; i++) {
          objects[i].setCoords()
        }
      })

      this.canvas.on('selection:created', function() {
        self.activeObject = self.canvas.getActiveObject()
      })

      this.canvas.on('selection:updated', function() {
        self.activeObject = self.canvas.getActiveObject()
      })

      this.canvas.on('selection:cleared', function() {
        self.activeObject = null
      })

      this.canvas.on('mouse:dblclick', function(e) {
        if (self.polygonOptions.polygonMode) {
          PolygonUtil.generatePolygon(self.polygonOptions, self.canvas)
        } else if (e.target && e.target.fgraphic) {
          self.currentObject = e.target
          if (
            self.currentObject.type === 'text' &&
            self.currentObject.fgraphic.styles &&
            typeof self.currentObject.fgraphic.styles.fontSize === 'undefined'
          ) {
            self.currentObject.fgraphic.styles.fontSize = 15
            self.currentObject.lockUniScaling = true
            self.currentObject.scaleX = 1
            self.currentObject.scaleY = 1
          }
          self.objectPropertiesDialog = true
          self.setObjectActions(self.currentObject)
          self.loadDefaultValues(self.objectActions)
        }
      })

      let grid = options.distance
      // snap to grid
      this.canvas.on('object:moving', function(op) {
        let changeOptions = {
          left: Math.round(op.target.left / grid) * grid,
          top: Math.round(op.target.top / grid) * grid,
        }
        op.target.set(changeOptions)
      })

      this.canvas.on('object:scaling', function(options) {
        let target = options.target
        let w = target.width * target.scaleX,
          h = target.height * target.scaleY,
          snap = {
            // Closest snapping points
            top: Math.round(target.top / grid) * grid,
            left: Math.round(target.left / grid) * grid,
            bottom: Math.round((target.top + h) / grid) * grid,
            right: Math.round((target.left + w) / grid) * grid,
          },
          threshold = grid,
          dist = {
            // Distance from snapping points
            top: Math.abs(snap.top - target.top),
            left: Math.abs(snap.left - target.left),
            bottom: Math.abs(snap.bottom - target.top - h),
            right: Math.abs(snap.right - target.left - w),
          },
          attrs = {
            scaleX: target.scaleX,
            scaleY: target.scaleY,
            top: target.top,
            left: target.left,
          }
        switch (target.__corner) {
          case 'tl':
            if (dist.left < dist.top && dist.left < threshold) {
              attrs.scaleX = (w - (snap.left - target.left)) / target.width
              attrs.scaleY = (attrs.scaleX / target.scaleX) * target.scaleY
              attrs.top = target.top + (h - target.height * attrs.scaleY)
              attrs.left = snap.left
            } else if (dist.top < threshold) {
              attrs.scaleY = (h - (snap.top - target.top)) / target.height
              attrs.scaleX = (attrs.scaleY / target.scaleY) * target.scaleX
              attrs.left = attrs.left + (w - target.width * attrs.scaleX)
              attrs.top = snap.top
            }
            break
          case 'mt':
            if (dist.top < threshold) {
              attrs.scaleY = (h - (snap.top - target.top)) / target.height
              attrs.top = snap.top
            }
            break
          case 'tr':
            if (dist.right < dist.top && dist.right < threshold) {
              attrs.scaleX = (snap.right - target.left) / target.width
              attrs.scaleY = (attrs.scaleX / target.scaleX) * target.scaleY
              attrs.top = target.top + (h - target.height * attrs.scaleY)
            } else if (dist.top < threshold) {
              attrs.scaleY = (h - (snap.top - target.top)) / target.height
              attrs.scaleX = (attrs.scaleY / target.scaleY) * target.scaleX
              attrs.top = snap.top
            }
            break
          case 'ml':
            if (dist.left < threshold) {
              attrs.scaleX = (w - (snap.left - target.left)) / target.width
              attrs.left = snap.left
            }
            break
          case 'mr':
            if (dist.right < threshold)
              attrs.scaleX = (snap.right - target.left) / target.width
            break
          case 'bl':
            if (dist.left < dist.bottom && dist.left < threshold) {
              attrs.scaleX = (w - (snap.left - target.left)) / target.width
              attrs.scaleY = (attrs.scaleX / target.scaleX) * target.scaleY
              attrs.left = snap.left
            } else if (dist.bottom < threshold) {
              attrs.scaleY = (snap.bottom - target.top) / target.height
              attrs.scaleX = (attrs.scaleY / target.scaleY) * target.scaleX
              attrs.left = attrs.left + (w - target.width * attrs.scaleX)
            }
            break
          case 'mb':
            if (dist.bottom < threshold)
              attrs.scaleY = (snap.bottom - target.top) / target.height
            break
          case 'br':
            if (dist.right < dist.bottom && dist.right < threshold) {
              attrs.scaleX = (snap.right - target.left) / target.width
              attrs.scaleY = (attrs.scaleX / target.scaleX) * target.scaleY
            } else if (dist.bottom < threshold) {
              attrs.scaleY = (snap.bottom - target.top) / target.height
              attrs.scaleX = (attrs.scaleY / target.scaleY) * target.scaleX
            }
            break
        }
        target.set(attrs)
      })

      const STEP = 10
      let Direction = {
        LEFT: 0,
        UP: 1,
        RIGHT: 2,
        DOWN: 3,
      }

      let moveSelected = function(direction, event) {
        let activeObjects = self.canvas.getActiveObjects()

        if (activeObjects) {
          if (event) {
            event.preventDefault()
          }
          for (let activeObject of activeObjects) {
            if (
              activeObject.lockMovementX &&
              (Direction.LEFT || Direction.RIGHT)
            ) {
              continue
            }
            if (
              activeObject.lockMovementY &&
              (Direction.UP || Direction.DOWN)
            ) {
              continue
            }
            switch (direction) {
              case Direction.LEFT:
                activeObject.set('left', activeObject.get('left') - STEP)
                break
              case Direction.UP:
                activeObject.set('top', activeObject.get('top') - STEP)
                break
              case Direction.RIGHT:
                activeObject.set('left', activeObject.get('left') + STEP)
                break
              case Direction.DOWN:
                activeObject.set('top', activeObject.get('top') + STEP)
                break
            }
            activeObject.setCoords()
          }
          self.canvas.renderAll()
        }
      }

      fabric.fabric.util.addListener(document.body, 'contextmenu', function(
        options
      ) {
        let isCanvas = options.path.find(
          p => p.className === 'graphics-editor-container'
        )
        if (isCanvas) {
          options.preventDefault()

          self.contextmenu = {
            top: options.clientY + 10,
            left: options.clientX + 10,
            menu: self.prepareRightClickMenu(),
          }
          self.$nextTick(() => {
            if (self.contextmenu && self.$refs['graphicsContextMenu']) {
              self.contextmenu.width =
                self.$refs['graphicsContextMenu'].clientWidth - 5
              self.contextmenu.height =
                self.$refs['graphicsContextMenu'].clientHeight

              let top = options.clientY + 10
              let left = options.clientX + 10
              let bottomSpace = window.innerHeight - options.clientY
              let rightSpace = window.innerWidth - options.clientX
              let divHeight = self.contextmenu.height
              let divWidth = self.contextmenu.width
              if (divHeight > bottomSpace - 40) {
                top = options.clientY - divHeight - 10
              }
              if (divWidth > rightSpace - 20) {
                left = options.clientX - divWidth - 10
              }

              self.contextmenu.top = top
              self.contextmenu.left = left
            }
          })
        }
      })

      document.body.onclick = function(options) {
        if (
          options &&
          options.srcElement.className.indexOf('graphics-contextmenu') === -1
        ) {
          self.contextmenu = null
        }
      }

      fabric.fabric.util.addListener(document.body, 'keydown', function(
        options
      ) {
        if (
          options.repeat ||
          self.objectPropertiesDialog ||
          !self.canvas ||
          (options.target.type && options.target.type.indexOf('text') >= 0)
        ) {
          return
        }

        let key = options.which || options.keyCode // key detection
        if (key === 37) {
          // handle Left key
          moveSelected(Direction.LEFT, options)
        } else if (key === 38) {
          // handle Up key
          moveSelected(Direction.UP, options)
        } else if (key === 39) {
          // handle Right key
          moveSelected(Direction.RIGHT, options)
        } else if (key === 40) {
          // handle Down key
          moveSelected(Direction.DOWN, options)
        } else if (key === 8) {
          // handle Backspace key
          if (
            options.target.type &&
            options.target.type.indexOf('text') === -1
          ) {
            self.deleteActiveObject()
          }
        } else if (key === 46) {
          // handle Delete key
          self.deleteActiveObject()
        } else if (key === 65 && (options.ctrlKey || options.metaKey)) {
          options.preventDefault()
          self.selectAll()
        } else if (key === 67 && (options.ctrlKey || options.metaKey)) {
          options.preventDefault()
          self.copy()
        } else if (key === 86 && (options.ctrlKey || options.metaKey)) {
          options.preventDefault()
          self.paste()
        }
      })

      this.canvas.on(
        'drop',
        function(options) {
          let pointer = this.getPointer(event)
          let e = options.e
          let x = e.layerX - this.viewportTransform[4]
          let y = e.layerY - this.viewportTransform[5]
          if (pointer) {
            x = pointer.x
            y = pointer.y
          }
          if (self.currentDragElement) {
            if (self.currentDragElement.fgraphic.uid) {
              let width = self.currentDragElement.get('width')
              let height = self.currentDragElement.get('height')
              if (width) {
                x = x - width / 2 < 0 ? 0 : x - width / 2
              }
              if (height) {
                y = y - height / 2 < 0 ? 0 : y - height / 2
              }
            }
            self.currentDragElement.set({
              left: Math.ceil(x / 10) * 10,
              top: Math.ceil(y / 10) * 10,
            })
            self.canvas.add(self.currentDragElement)
          }
          return false
        },
        false
      )
    },
    updateObjectProps() {
      this.currentObject.fgraphic.actions = JSON.parse(
        JSON.stringify(this.objectActions)
      )
      this.graphicUtil.updateLiveValue(this.currentObject, this.graphics)
      if (this.currentObject.type === 'image') {
        this.graphicUtil.updateImageFilters(this.currentObject)
      }
      this.canvas.requestRenderAll()
      this.objectPropertiesDialog = false
      this.graphicsListForActions = null
      this.assetListForActions = null
    },
    highlightReservableSpaces(type, opacity) {
      if (this.canvas) {
        this.canvas.forEachObject(obj => {
          if (
            obj.fgraphic &&
            obj.fgraphic.type === 'space_zone' &&
            obj.fgraphic.spaceMappingBindingVariable
          ) {
            let liveValObj = this.graphicUtil.getLiveValue(
              this.graphics.liveValues[obj.fgraphic.spaceMappingBindingVariable]
            )

            if (type === 'reservable' && liveValObj.value[0].reservable) {
              obj.set('opacity', opacity)
            } else if (
              type === 'non-reservable' &&
              !liveValObj.value[0].reservable
            ) {
              obj.set('opacity', opacity)
            }
          }
        })
        this.canvas.renderAll()
      }
    },
    handleRightClickAction(menu, context, subMenu) {
      if (menu.action === 'selectAll') {
        this.selectAll()
      } else if (menu.action === 'showProperties') {
        this.showProperties()
      } else if (menu.action === 'changeTheme') {
        if (subMenu) {
          this.changeTheme(subMenu)
        }
      } else if (menu.action === 'cut') {
        this.cut()
      } else if (menu.action === 'copy') {
        this.copy()
      } else if (menu.action === 'paste') {
        this.paste({
          left: Math.ceil(context.left / 10) * 10,
          top: Math.ceil(context.top / 10) * 10,
        })
      } else if (menu.action === 'copyToClipboard') {
        this.copyToClipboard()
      } else if (menu.action === 'pasteFromClipboard') {
        this.pasteFromClipboard({
          left: Math.ceil(context.left / 10) * 10,
          top: Math.ceil(context.top / 10) * 10,
        })
      } else if (menu.action === 'duplicate') {
        this.duplicate()
      } else if (menu.action === 'delete') {
        this.deleteActiveObject()
      } else if (menu.action === 'copyStyle') {
        this.copyStyle()
      } else if (menu.action === 'pasteStyle') {
        this.pasteStyle()
      } else if (menu.action === 'copyCF') {
        this.copyCF()
      } else if (menu.action === 'pasteCF') {
        this.pasteCF()
      } else if (menu.action === 'bringToFront') {
        this.bringToFront()
      } else if (menu.action === 'sendToBack') {
        this.sendToBack()
      } else if (menu.action === 'alignStraight') {
        this.alignStraight()
      }
    },
    prepareRightClickMenu() {
      let menuList = []

      if (this._clipboard) {
        menuList.push({
          label: 'Paste',
          action: 'paste',
          order: 3,
        })
      }
      if (window.graphicsClipboard) {
        menuList.push({
          label: 'Paste from Clipboard',
          action: 'pasteFromClipboard',
          order: 3,
        })
      }

      let activeObjects =
        (this.canvas && this.canvas.getActiveObjects()) || null
      if (activeObjects && activeObjects.length) {
        menuList.push({
          label: 'Cut',
          action: 'cut',
          order: 1,
        })
        menuList.push({
          label: 'Copy',
          action: 'copy',
          order: 2,
        })
        menuList.push({
          label: 'Copy to Clipboard',
          action: 'copyToClipboard',
          order: 2,
        })
        menuList.push({
          label: 'Duplicate',
          action: 'duplicate',
          order: 4,
        })
        menuList.push({
          label: 'Delete',
          action: 'delete',
          order: 5,
        })

        if (activeObjects.length > 1) {
          menuList.push({
            label: 'Align Straight',
            action: 'alignStraight',
            order: 14,
          })
        }

        if (activeObjects.length === 1) {
          menuList.push({
            label: 'Properties',
            action: 'showProperties',
            section: true,
            order: 20,
          })

          if (
            activeObjects[0].fgraphic &&
            activeObjects[0].fgraphic.styles &&
            Object.keys(activeObjects[0].fgraphic.styles).length
          ) {
            menuList.push({
              label: 'Copy Style',
              action: 'copyStyle',
              order: 6,
            })
          }
          if (
            activeObjects[0].fgraphic &&
            activeObjects[0].fgraphic.conditionalFormatting &&
            activeObjects[0].fgraphic.conditionalFormatting.length
          ) {
            menuList.push({
              label: 'Copy Conditional Formatting',
              action: 'copyCF',
              order: 8,
            })
          }
        }

        menuList.push({
          label: 'Bring to front',
          action: 'bringToFront',
          order: 11,
          section: true,
        })
        menuList.push({
          label: 'Send to back',
          action: 'sendToBack',
          order: 12,
        })

        let typeMap = {}
        let uidMap = {}
        let themeMap = {}
        for (let activeObject of activeObjects) {
          if (activeObject.fgraphic) {
            if (activeObject.fgraphic.uid) {
              uidMap[activeObject.fgraphic.uid] = true
            }
            if (activeObject.fgraphic.type) {
              typeMap[activeObject.fgraphic.type] = true
            }
            if (activeObject.fgraphic.themeGroup) {
              themeMap[activeObject.fgraphic.themeGroup] = true
            }
          }
        }

        if (
          this._clipboardStyle &&
          this.isSameTypeObjects(this._clipboardStyle.type)
        ) {
          menuList.push({
            label: 'Paste Style',
            action: 'pasteStyle',
            order: 7,
          })
        }
        if (
          this._clipboardCF &&
          this.isSameTypeObjects(this._clipboardCF.type)
        ) {
          menuList.push({
            label: 'Paste Conditional Formatting',
            action: 'pasteCF',
            order: 9,
          })
        }

        if (Object.keys(themeMap).length === 1) {
          let themeList = getColorThemes(Object.keys(themeMap)[0]).map(th => {
            return {
              label: th.label,
              value: th.key,
            }
          })
          menuList.push({
            label: 'Change Theme',
            action: 'changeTheme',
            order: 10,
            section: true,
            showSubMenu: false,
            submenu: themeList,
          })
        }
      } else {
        menuList.push({
          label: 'Select All',
          action: 'selectAll',
          order: 13,
        })
      }

      return menuList.sort((a, b) => {
        return a.order - b.order
      })
    },
    isSameTypeObjects(type) {
      let typeMap = {}
      let activeObjects = this.canvas.getActiveObjects()
      if (activeObjects) {
        for (let activeObject of activeObjects) {
          if (activeObject.fgraphic) {
            if (activeObject.fgraphic.type) {
              typeMap[activeObject.fgraphic.type] = true
            }
          }
        }
      }
      if (Object.keys(typeMap).length === 1) {
        if (type && type === Object.keys(typeMap)[0]) {
          return true
        }
      }
      return false
    },
    isSameThemeGroup(theme) {
      let themeMap = {}
      let activeObjects = this.canvas.getActiveObjects()
      if (activeObjects) {
        for (let activeObject of activeObjects) {
          if (activeObject.fgraphic) {
            if (activeObject.fgraphic.themeGroup) {
              themeMap[activeObject.fgraphic.themeGroup] = true
            }
          }
        }
      }
      if (Object.keys(themeMap).length === 1) {
        let themeObj = getColorThemes(Object.keys(themeMap)[0]).find(
          th => th.key === theme
        )
        if (themeObj) {
          return true
        }
      }
      return false
    },
    setObjectActions(object) {
      if (!object || !object.fgraphic) {
        return
      }

      this.controlPoints = []
      this.controlGroups = []

      let showTrend = true
      let controlAction = false
      let controlActionList = [
        {
          type: 'control_point',
          actionName: null,
          assetId: null,
          assetCategoryId: null,
          fieldId: null,
          pointId: null,
          groupId: null,
        },
      ]
      let hyperLink = false
      let hyperLinkList = [
        {
          actionName: null,
          linkType: null,
          assetId: null,
          id: null,
          url: null,
          target: '_blank',
        },
      ]
      if (object.fgraphic.actions) {
        if (object.fgraphic.actions.showTrend.enable) {
          showTrend = true
        } else {
          showTrend = false
        }

        if (object.fgraphic.actions.controlAction.enable) {
          controlAction = true
          if (object.fgraphic.actions.controlAction.control_list) {
            controlActionList =
              object.fgraphic.actions.controlAction.control_list
          }
        } else {
          controlAction = false
        }

        if (object.fgraphic.actions.hyperLink.enable) {
          hyperLink = true
          if (object.fgraphic.actions.hyperLink.link_list) {
            hyperLinkList = object.fgraphic.actions.hyperLink.link_list
            hyperLinkList.forEach(rt => {
              if (!rt.hasOwnProperty('assetId')) {
                this.$set(rt, 'assetId', null)
              }
            })
          }
        } else {
          hyperLink = false
        }
      }
      this.objectActions = {
        showTrend: {
          enable: showTrend,
        },
        controlAction: {
          enable: controlAction,
          control_list: controlActionList,
        },
        hyperLink: {
          enable: hyperLink,
          link_list: hyperLinkList,
        },
        invokeFunction: {
          enable: false,
          function_list: [],
        },
      }
    },
    changeAsset(assetId) {
      this.loadingState = true

      if (this.pubSubWatcherKey) {
        let existsReadings = []
        for (let varObj of this.graphics.variables.filter(
          v => v.type === 'reading'
        )) {
          existsReadings.push({
            parentId: varObj.parentId + '',
            fieldName: varObj.select,
            moduleName: varObj.module,
          })
        }

        this.unsubscribe(this.pubSubWatcherKey, 'readingChange', {
          readings: existsReadings,
        })
        this.pubSubWatcherKey = null
      } else if (this.$wms) {
        let existsReadings = []
        for (let varObj of this.graphics.variables.filter(
          v => v.type === 'reading'
        )) {
          existsReadings.push({
            parentId: varObj.parentId + '',
            fieldName: varObj.select,
            moduleName: varObj.module,
          })
        }
        this.$http
          .post('/v2/fetchLiveUpdateFields', {
            liveUpdateFields: existsReadings,
          })
          .then(response => {
            this.loadingState = false
            let readingFields = response.data.result.liveUpdateFields
            if (readingFields && readingFields.length) {
              for (let reading of readingFields) {
                let topic =
                  '__livereading__/' + reading.parentId + '/' + reading.fieldId
                this.$wms.unsubscribe(topic, this.refreshLiveData)
              }
            }
          })
      }

      for (let varObj of this.graphics.variables) {
        if (varObj.type !== 'custom' && varObj.parentId) {
          varObj.parentId = assetId
        }
      }
      this.currentAsset.id = assetId
      this.refreshLiveData()

      let readings = []
      for (let varObj of this.graphics.variables.filter(
        v => v.type === 'reading'
      )) {
        readings.push({
          parentId: varObj.parentId + '',
          fieldName: varObj.select,
          moduleName: varObj.module,
        })
      }

      if (readings.length) {
        if (this.$wms) {
          this.$http
            .post('/v2/fetchLiveUpdateFields', {
              liveUpdateFields: readings,
            })
            .then(response => {
              this.loadingState = false
              let readingFields = response.data.result.liveUpdateFields
              if (readingFields && readingFields.length) {
                for (let reading of readingFields) {
                  let topic =
                    '__livereading__/' +
                    reading.parentId +
                    '/' +
                    reading.fieldId
                  this.$wms.subscribe(topic, this.refreshLiveData)
                }
              }
            })
        } else {
          let self = this
          this.pubSubWatcherKey = this.subscribe(
            'readingChange',
            { readings: readings },
            function() {
              self.refreshLiveData()
            }
          )
        }
      }
    },
    loadAsset(assetId) {
      if (!assetId) {
        return
      }
      this.variablesLoaded = false
      this.loadSystemVariables()

      if (!this.currentAsset.fields || !this.currentAsset.fields.length) {
        util.loadFields('asset').then(fields => {
          this.currentAsset.fields = fields
          this.loadAssetSummary(assetId)
        })
      } else {
        this.loadAssetSummary(assetId)
      }
    },
    loadAssetSummary(assetId) {
      this.$http.get('/asset/summary/' + assetId).then(response => {
        this.currentAsset.record = response.data.asset
        this.currentAsset.id = response.data.asset.id
        this.$emit('assetLoaded', response.data.asset)
        this.loadAssetFieldVarialbes()
        util
          .loadAssetReadingFields(-1, response.data.asset.category.id)
          .then(fields => {
            this.currentAsset.readings = fields
            this.loadAssetReadingVariables()
          })
      })
    },
    loadAssetReadingVariables() {
      let variables = []
      if (this.currentAsset.fields && this.currentAsset.fields) {
        for (let field of this.currentAsset.fields) {
          variables.push({
            fetchType: 'field',
            module: field.module.name,
            id: this.currentAsset.record.id,
            parentId: this.currentAsset.record.id,
            select: field.name,
            key: CURRENT_ASSET_FIELD_PREFIX(field.name),
            dataType: field.dataTypeEnum._name,
            label: field.displayName,
            type: 'field',
          })
        }
      }
      if (this.currentAsset.readings && this.currentAsset.readings) {
        for (let reading of this.currentAsset.readings) {
          if (this.$org.id === 210 && this.currentAsset.record.id === 1145442) {
            variables.push({
              fetchType: 'liveValue',
              module: reading.module.name,
              select: reading.name,
              parentId: this.currentAsset.record.id,
              key: CURRENT_ASSET_READING_PREFIX(
                reading.module.name + '_' + reading.name
              ),
              dataType: reading.dataTypeEnum._name,
              label: reading.displayName,
              unit: reading.unit,
              type: 'reading',
            })
          }
          if (
            variables.find(
              v => v.key === CURRENT_ASSET_READING_PREFIX(reading.name)
            ) &&
            reading.default === false
          ) {
            continue
          }
          variables.push({
            fetchType: 'liveValue',
            module: reading.module.name,
            select: reading.name,
            parentId: this.currentAsset.record.id,
            key: CURRENT_ASSET_READING_PREFIX(reading.name),
            dataType: reading.dataTypeEnum._name,
            label: reading.displayName,
            unit: reading.unit,
            type: 'reading',
          })
        }
      }

      this.graphics.variables.push(...variables)
      this.variablesLoaded = true
      this.refreshLiveData()

      let readings = []
      for (let varObj of this.graphics.variables.filter(
        v => v.fetchType === 'liveValue'
      )) {
        readings.push({
          parentId: varObj.parentId + '',
          fieldName: varObj.select,
          moduleName: varObj.module,
        })
      }

      if (this.$wms) {
        this.$http
          .post('/v2/fetchLiveUpdateFields', {
            liveUpdateFields: readings,
          })
          .then(response => {
            this.loadingState = false
            let readingFields = response.data.result.liveUpdateFields
            if (readingFields && readingFields.length) {
              for (let reading of readingFields) {
                let topic =
                  '__livereading__/' + reading.parentId + '/' + reading.fieldId
                this.$wms.subscribe(topic, this.refreshLiveData)
              }
            }
          })
      } else {
        let self = this
        this.pubSubWatcherKey = this.subscribe(
          'readingChange',
          { readings: readings },
          function() {
            self.refreshLiveData()
          }
        )
      }
    },
    loadSystemVariables() {
      this.graphics.variables.push({
        key: 'system.currentTime',
        label: 'Current Time',
        type: 'sysvar',
      })

      this.graphics.liveValues['system.currentTime'] = {
        value: () => {
          return moment()
            .tz(this.$timezone)
            .format('MMMM Do YYYY, h:mm:ss a')
        },
      }
    },
    loadAssetFieldVarialbes() {
      let variables = []
      if (this.currentAsset.fields && this.currentAsset.fields) {
        for (let field of this.currentAsset.fields) {
          if (this.currentAsset.record[field.name]) {
            if (typeof this.currentAsset.record[field.name] === 'object') {
              this.graphics.liveValues[
                CURRENT_ASSET_FIELD_PREFIX(field.name)
              ] = {
                value: this.currentAsset.record[field.name].displayName
                  ? this.currentAsset.record[field.name].displayName
                  : this.currentAsset.record[field.name].name,
              }
            } else {
              this.graphics.liveValues[
                CURRENT_ASSET_FIELD_PREFIX(field.name)
              ] = {
                value: this.currentAsset.record[field.name],
              }
            }
          } else {
            this.graphics.liveValues[CURRENT_ASSET_FIELD_PREFIX(field.name)] = {
              value: null,
            }
          }
        }
      }

      this.graphics.variables.push(...variables)
    },
    fetchGraphicsData(paramList, cfShapes) {
      let parameters = []
      parameters.push(paramList)
      if (cfShapes && cfShapes.length) {
        parameters.push(cfShapes)
      }
      let url = '/v2/workflow/getDefaultWorkflowResult'
      let params = {
        defaultWorkflowId: 2,
        paramList: parameters,
      }

      return new Promise((resolve, reject) => {
        this.$http
          .post(url, params)
          .then(response => {
            if (response.data.result.workflow.returnValue) {
              let graphicsData = response.data.result.workflow.returnValue
                .conditionalFormatting
                ? response.data.result.workflow.returnValue.data
                : response.data.result.workflow.returnValue
              for (let key in graphicsData) {
                if (typeof graphicsData[key] === 'object') {
                  this.graphics.liveValues[key] = graphicsData[key]
                } else {
                  this.graphics.liveValues[key] = {
                    value:
                      graphicsData[key] && typeof graphicsData[key] === 'object'
                        ? graphicsData[key].value
                        : graphicsData[key],
                    unit: paramList.find(p => p.key === key).unit,
                  }
                }
                resolve()
              }

              if (
                this.canvas &&
                response.data.result.workflow.returnValue.conditionalFormatting
              ) {
                let cfMap = {}
                for (let cf of response.data.result.workflow.returnValue
                  .conditionalFormatting) {
                  cfMap[cf.id] = cf
                }

                this.canvas.forEachObject(obj => {
                  if (obj.fgraphic && obj.fgraphic.id) {
                    let appliedTheme = obj.get('fgraphic').appliedTheme
                    if (cfMap[obj.fgraphic.id]) {
                      cfMap[obj.fgraphic.id].appliedTheme = appliedTheme
                      if (!obj.get('actualFgraphic')) {
                        obj.set('actualFgraphic', obj.get('fgraphic'))
                      }
                      obj.set('fgraphic', cfMap[obj.fgraphic.id])
                    } else {
                      if (obj.get('actualFgraphic')) {
                        obj.set('fgraphic', obj.get('actualFgraphic'))
                      }
                    }
                  }
                })
              }
            }
          })
          .catch(err => {
            if (err) {
              reject(err)
            }
          })
      })
    },
    handleDrag(elm) {
      this.currentDragElement = null
      if (elm) {
        let self = this
        if (elm.object_id) {
          let shape = self.graphicUtil.findShape(elm.object_id)
          self.graphicUtil.getShape(shape, 'default', function(obj) {
            self.currentDragElement = obj
          })
        } else {
          let textElm = new fabric.fabric.Text('', {
            fgraphic: {
              enableTextBinding: true,
              formatText: elm.label + ': ${' + elm.key + '}',
              blink: false,
              hide: false,
              animateEffect: 'none',
              id: self.graphicUtil.getUniqueId(),
              type: 'text',
              styles: {
                fill: '',
                textBackgroundColor: '',
                backgroundColor: '',
                fontFamily: 'Aktiv-Grotesk',
                textAlign: '',
                lineHeight: null,
                charSpacing: null,
                fontWeight: '',
                fontStyle: '',
              },
            },
            fill: '#0c0c0c',
            scaleX: 0.3,
            scaleY: 0.3,
          })
          self.currentDragElement = textElm
          if (self.graphics.liveValues[elm.key]) {
            self.graphicUtil.updateLiveValue(
              self.currentDragElement,
              self.graphics
            )
            self.canvas.requestRenderAll()
          } else {
            this.fetchGraphicsData([
              self.graphics.variables.find(v => v.key === elm.key),
            ]).then(() => {
              self.graphicUtil.updateLiveValue(textElm, self.graphics)
              self.canvas.requestRenderAll()
            })
          }
        }
      }
    },
    updateVariable(elm) {
      let self = this
      if (elm) {
        this.fetchGraphicsData([
          self.graphics.variables.find(v => v.key === elm.key),
        ]).then(() => {
          self.graphicUtil.updateAllLiveValues(
            self.canvas.getObjects(),
            self.graphics
          )
          self.canvas.requestRenderAll()
        })
      }
    },
    zoomIn() {
      this.canvas.setZoom(this.canvas.getZoom() * 1.1)
    },
    zoomOut() {
      this.canvas.setZoom(this.canvas.getZoom() / 1.1)
    },
    sendToBack() {
      let activeObject = this.canvas.getActiveObject()
      if (activeObject) {
        this.canvas.sendToBack(activeObject)
      }
    },
    bringToFront() {
      let activeObject = this.canvas.getActiveObject()
      if (activeObject) {
        this.canvas.bringToFront(activeObject)
      }
    },
    alignStraight() {
      let activeObjects = this.canvas.getActiveObjects()
      if (activeObjects && activeObjects.length) {
        let firstLeft = activeObjects[0].left

        for (let ao of activeObjects) {
          ao.left = firstLeft
        }
        this.canvas.requestRenderAll()
      }
    },
    deleteActiveObject() {
      let activeObjects = this.canvas.getActiveObjects()
      this.canvas.discardActiveObject()
      if (activeObjects.length) {
        this.canvas.remove.apply(this.canvas, activeObjects)
      }
    },
    cancel() {
      this.$emit('close')
    },
    save() {
      this.isSaving = true

      let customVariables = this.graphics.variables.filter(
        v => v.type === 'custom'
      )

      if (this.graphicsContext.id) {
        let gcontext = {
          id: this.graphicsContext.id,
          canvas: JSON.stringify(this.canvas.toJSON(['fgraphic'])),
        }
        if (customVariables) {
          gcontext.variables = JSON.stringify(customVariables)
        }

        this.$http
          .post('/v2/graphics/update', { graphics: gcontext })
          .then(response => {
            if (response) {
              this.$message({
                message: 'Congrats, Graphics saved successfully.',
                type: 'success',
              })
              this.isSaving = false
              this.$emit('close')
            }
          })
      } else {
        let gcontext = {
          name: this.graphicsContext.name,
          description: this.graphicsContext.description,
          assetId: this.currentAsset.record.id,
          assetCategoryId: this.currentAsset.record.category.id,
          canvas: JSON.stringify(this.canvas.toJSON(['fgraphic'])),
        }

        this.$http
          .post('/v2/graphics/add', { graphics: gcontext })
          .then(function(response) {
            if (response) {
              this.$message({
                message: 'Congrats, Graphics saved successfully.',
                type: 'success',
              })
              this.isSaving = false
              this.$emit('close')
            }
          })
      }
    },
    loadUsedVariables() {
      if (this.canvas && this.graphicUtil) {
        let usedVariables = []
        this.canvas.forEachObject(obj => {
          let uv = this.graphicUtil.getUsedVariables(obj)
          if (uv && uv.length) {
            usedVariables.push(...uv)
          }
        })
        this.graphics.usedVariables = usedVariables
      }
      if (!this.currentAsset.record) {
        this.refreshLiveData()
      }
    },
    refreshLiveData() {
      if (!this.graphicUtil) {
        return
      }
      let vars = this.graphics.variables
      if (this.readonly) {
        if (this.graphics.usedVariables && this.graphics.usedVariables.length) {
          vars = vars.filter(
            v => this.graphics.usedVariables.indexOf(v.key) >= 0
          )
        } else {
          this.graphicUtil.updateAllLiveValues(
            this.canvas.getObjects(),
            this.graphics
          )
          this.canvas.requestRenderAll()
          this.loadingState = false
          return
        }
      }
      vars = vars.filter(v => v.key.indexOf('system.') === -1)

      let conditionalFormattingShapes = []
      if (this.readonly) {
        this.canvas.forEachObject(function(obj) {
          if (
            obj.fgraphic &&
            obj.fgraphic.conditionalFormatting &&
            obj.fgraphic.conditionalFormatting.length
          ) {
            let haveCriteria = false
            for (let cf of obj.fgraphic.conditionalFormatting) {
              if (cf.criteria && Object.keys(cf.criteria.conditions).length) {
                let emptyCond = []
                for (let cond in cf.criteria.conditions) {
                  if (Object.keys(cf.criteria.conditions[cond]).length) {
                    haveCriteria = true
                    break
                  } else {
                    emptyCond.push(cond)
                  }
                }
                for (let ec of emptyCond) {
                  delete cf.criteria.conditions[ec]
                }
                if (
                  haveCriteria &&
                  Object.keys(cf.criteria.conditions).length
                ) {
                  break
                }
              }
            }
            if (haveCriteria) {
              conditionalFormattingShapes.push(
                obj.actualFgraphic ? obj.actualFgraphic : obj.fgraphic
              )
            }
          }
        })
      }
      this.fetchGraphicsData(vars, conditionalFormattingShapes)
        .then(() => {
          // console.log('fetch graphics data called')
          this.graphicUtil.updateAllLiveValues(
            this.canvas.getObjects(),
            this.graphics
          )
          this.canvas.requestRenderAll()
          this.loadingState = false
        })
        .catch(() => {
          this.loadingState = false
        })
    },
    addValueBinding(obj) {
      this.currentObject.valueBindings.push(obj)
      this.$forceUpdate()
    },
    selectAll() {
      if (this.canvas) {
        this.canvas.discardActiveObject()
        let sel = new fabric.fabric.ActiveSelection(this.canvas.getObjects(), {
          canvas: this.canvas,
        })
        this.canvas.setActiveObject(sel)
        this.canvas.requestRenderAll()
      }
    },
    cut() {
      if (this.canvas.getActiveObject()) {
        this.canvas.getActiveObject().clone(
          cloned => {
            this._clipboard = cloned
          },
          ['fgraphic']
        )
        this.deleteActiveObject()
      }
    },
    copy() {
      if (this.canvas.getActiveObject()) {
        this.canvas.getActiveObject().clone(
          cloned => {
            this._clipboard = cloned
          },
          ['fgraphic']
        )
      }
    },
    copyToClipboard() {
      if (this.canvas.getActiveObject()) {
        this.canvas.getActiveObject().clone(
          cloned => {
            window.graphicsClipboard = cloned
          },
          ['fgraphic']
        )
      }
    },
    duplicate() {
      if (this.canvas.getActiveObject()) {
        this.canvas.getActiveObject().clone(
          cloned => {
            this.pasteInternal(cloned)
          },
          ['fgraphic']
        )
      }
    },
    paste(position) {
      this.pasteInternal(this._clipboard, position)
    },
    pasteFromClipboard(position) {
      if (window.graphicsClipboard) {
        this.pasteInternal(window.graphicsClipboard, position)
      }
    },
    pasteInternal(clipboard, position) {
      let self = this
      if (clipboard) {
        clipboard.clone(
          clonedObj => {
            clonedObj.set({
              left: position ? position.left : clonedObj.left + 10,
              top: position ? position.top : clonedObj.top + 10,
              evented: true,
            })
            if (clonedObj.type === 'activeSelection') {
              // active selection needs a reference to the canvas.
              clonedObj.canvas = this.canvas
              clonedObj.forEachObject(function(obj) {
                if (obj.fgraphic && obj.fgraphic.id) {
                  obj.fgraphic.id = self.graphicUtil.getUniqueId()
                }
                self.canvas.add(obj)
              })
              // this should solve the unselectability
              clonedObj.setCoords()
            } else {
              if (clonedObj.fgraphic && clonedObj.fgraphic.id) {
                clonedObj.fgraphic.id = self.graphicUtil.getUniqueId()
              }
              self.canvas.add(clonedObj)
            }
            clipboard.top += 10
            clipboard.left += 10
            self.canvas.setActiveObject(clonedObj)
            self.canvas.requestRenderAll()
          },
          ['fgraphic']
        )
      }
    },
    copyStyle() {
      let activeObj = this.canvas.getActiveObject()
      if (
        activeObj &&
        activeObj.fgraphic &&
        activeObj.fgraphic.styles &&
        Object.keys(activeObj.fgraphic.styles).length
      ) {
        let cloned = {}
        if (activeObj.fgraphic.uid) {
          cloned.uid = activeObj.fgraphic.uid
        }
        if (activeObj.fgraphic.type) {
          cloned.type = activeObj.fgraphic.type
        }
        cloned.styles = JSON.parse(JSON.stringify(activeObj.fgraphic.styles))

        this._clipboardStyle = cloned
      }
    },
    pasteStyle() {
      if (
        this._clipboardStyle &&
        this.isSameTypeObjects(this._clipboardStyle.type)
      ) {
        let cloned = JSON.parse(JSON.stringify(this._clipboardStyle))
        let activeObjects = this.canvas.getActiveObjects()
        if (activeObjects) {
          for (let activeObj of activeObjects) {
            if (
              activeObj &&
              activeObj.fgraphic &&
              activeObj.fgraphic.styles &&
              Object.keys(activeObj.fgraphic.styles).length
            ) {
              activeObj.fgraphic.styles = cloned.styles
              if (
                activeObj.type === 'text' &&
                activeObj.fgraphic.styles &&
                activeObj.fgraphic.styles.fontSize
              ) {
                activeObj.scaleX = 1
                activeObj.scaleY = 1
              }
              this.graphicUtil.updateLiveValue(activeObj, this.graphics)
            }
          }
          this.canvas.requestRenderAll()
        }
      }
    },
    copyCF() {
      let activeObj = this.canvas.getActiveObject()
      if (
        activeObj &&
        activeObj.fgraphic &&
        activeObj.fgraphic.conditionalFormatting &&
        activeObj.fgraphic.conditionalFormatting.length
      ) {
        let cloned = {}
        if (activeObj.fgraphic.uid) {
          cloned.uid = activeObj.fgraphic.uid
        }
        if (activeObj.fgraphic.type) {
          cloned.type = activeObj.fgraphic.type
        }
        cloned.conditionalFormatting = JSON.parse(
          JSON.stringify(activeObj.fgraphic.conditionalFormatting)
        )

        this._clipboardCF = cloned
      }
    },
    pasteCF() {
      if (this._clipboardCF && this.isSameTypeObjects(this._clipboardCF.type)) {
        let cloned = JSON.parse(JSON.stringify(this._clipboardCF))
        let activeObjects = this.canvas.getActiveObjects()
        if (activeObjects) {
          for (let activeObj of activeObjects) {
            if (activeObj && activeObj.fgraphic) {
              activeObj.fgraphic.conditionalFormatting =
                cloned.conditionalFormatting
              this.graphicUtil.updateLiveValue(activeObj, this.graphics)
            }
          }
          this.canvas.requestRenderAll()
        }
      }
    },
    changeTheme(theme) {
      if (theme && this.isSameThemeGroup(theme)) {
        let activeObjects = this.canvas.getActiveObjects()
        if (activeObjects) {
          this.canvas.discardActiveObject()
          for (let activeObj of activeObjects) {
            if (activeObj && activeObj.fgraphic) {
              activeObj.fgraphic.theme = theme
              this.graphicUtil.updateLiveValue(activeObj, this.graphics)
            }
          }
          this.canvas.requestRenderAll()
        }
      }
    },
    showProperties() {
      let activeObject = this.canvas.getActiveObject()
      if (activeObject) {
        this.currentObject = activeObject
        if (
          this.currentObject.type === 'text' &&
          this.currentObject.fgraphic.styles &&
          typeof this.currentObject.fgraphic.styles.fontSize === 'undefined'
        ) {
          this.currentObject.fgraphic.styles.fontSize = 15
          this.currentObject.lockUniScaling = true
          this.currentObject.scaleX = 1
          this.currentObject.scaleY = 1
        }
        this.objectPropertiesDialog = true
        this.setObjectActions(self.currentObject)
        this.loadDefaultValues(self.objectActions)
      }
    },
    addText() {
      let textElm = new fabric.fabric.Text('Add Text', {
        fgraphic: {
          enableTextBinding: true,
          formatText: 'Add Text',
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: this.graphicUtil.getUniqueId(),
          type: 'text',
          styles: {
            fill: '',
            textBackgroundColor: '',
            backgroundColor: '',
            fontFamily: 'Aktiv-Grotesk',
            fontSize: 15,
            textAlign: '',
            lineHeight: null,
            charSpacing: null,
            fontWeight: '',
            fontStyle: '',
          },
        },
        lockUniScaling: true,
        fontSize: 15,
        fontFamily: 'Aktiv-Grotesk',
        fill: '#0c0c0c',
        top: 100,
        left: 100,
      })
      this.canvas.add(textElm)
      this.canvas.setActiveObject(textElm)
    },
    addPolygon() {
      PolygonUtil.drawPolygon(this.polygonOptions, this.canvas)
    },
    toggleLock(status) {
      if (this.activeObject) {
        this.activeObject.lockMovementX = status
        this.activeObject.lockMovementY = status
      }
    },
    addButton() {
      let fgraphic = {
        enableButtonBinding: true,
        type: 'button',
        formatText: 'Button',
        blink: false,
        hide: false,
        animateEffect: 'none',
        id: this.graphicUtil.getUniqueId(),
        styles: {
          fill: '',
          fontFamily: 'Aktiv-Grotesk',
          textAlign: '',
          lineHeight: null,
          charSpacing: null,
          fontWeight: '',
          fontStyle: '',
          fontColor: '#FFFFFF',
          backgroundColor: '#C95DB4',
          padding: 20,
          radius: 5,
        },
      }
      let text = new fabric.fabric.Text('Button', {
        fontSize: 15,
        originX: 'center',
        originY: 'center',
        fill: fgraphic.styles.fontColor,
        fgraphic: {
          id: 'button_text',
        },
      })

      let textWidth = text.get('width')
      let textHeight = text.get('height')

      let bg = new fabric.fabric.Rect({
        fill: fgraphic.styles.backgroundColor,
        scaleY: 0.5,
        originX: 'center',
        originY: 'center',
        rx: fgraphic.styles.radius,
        ry: fgraphic.styles.radius,
        width: textWidth + fgraphic.styles.padding * 2,
        height: textHeight + fgraphic.styles.padding * 2,
        fgraphic: {
          id: 'button_rect',
        },
      })

      let group = new fabric.fabric.Group([bg, text], {
        left: 50,
        top: 100,
      })

      group.set({
        fgraphic: fgraphic,
      })

      this.canvas.add(group)
    },
    addImage(files) {
      if (!files && !files.length) return

      const formData = new FormData()
      formData.append('fileContent', files[0])

      let self = this
      self.$http.post('/v2/files/add', formData).then(function(response) {
        let fileId = response.data.result.fileInfo.fileId

        let fileURL = getBaseURL() + '/v2/files/preview/' + fileId
        fabric.fabric.Image.fromURL(fileURL, function(imgObj) {
          let filters = {
            brightness: null,
            contrast: null,
            saturation: null,
            hue: null,
            noise: null,
            pixelate: null,
            blur: null,
          }
          let obj = imgObj.set({
            fgraphic: {
              type: 'image',
              fileId: fileId,
              blink: false,
              hide: false,
              animateEffect: 'none',
              id: self.graphicUtil.getUniqueId(),
              filters: filters,
            },
            left: 50,
            top: 50,
          })
          self.canvas.add(obj)
        })
      })
    },
    addLine() {
      let line = new fabric.fabric.Line([50, 100, 200, 200], {
        left: 170,
        top: 150,
        stroke: '#25243e',
        fgraphic: {
          styles: {
            stroke: '#25243e',
            type: 'line',
            blink: false,
            hide: false,
            animateEffect: 'none',
            id: this.graphicUtil.getUniqueId(),
          },
        },
      })

      this.canvas.add(line)
      this.canvas.setActiveObject(line)
    },
    addSquare() {
      let square = new fabric.fabric.Rect({
        top: 100,
        left: 100,
        width: 60,
        height: 70,
        fill: '',
        stroke: '#25243e',
        fgraphic: {
          styles: { stroke: '#25243e', fill: '', strokeWidth: '' },
          type: 'square',
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: this.graphicUtil.getUniqueId(),
        },
      })
      this.canvas.add(square)
      this.canvas.setActiveObject(square)
    },
    addCircle() {
      let circle = new fabric.fabric.Circle({
        radius: 50,
        stroke: '#25243e',
        fill: '',
        top: 100,
        left: 100,
        fgraphic: {
          styles: { stroke: '#25243e', fill: '', strokeWidth: '' },
          type: 'circle',
          blink: false,
          hide: false,
          animateEffect: 'none',
          id: this.graphicUtil.getUniqueId(),
        },
      })
      this.canvas.add(circle)
      this.canvas.setActiveObject(circle)
    },
    addRect() {
      this.canvas.add(
        new fabric.fabric.Rect({
          left: 120,
          top: 120,
          width: 60,
          height: 60,
          fill: '#faa',
          originX: 'left',
          originY: 'top',
          centeredRotation: true,
        })
      )
    },
    addDuct() {
      let path = new fabric.fabric.Path(
        'm504.339844 441.109375h-53.640625v-156.609375h53.640625c4.230468 0 7.660156-3.429688 7.660156-7.660156 0-4.230469-3.429688-7.660156-7.660156-7.660156h-54.359375c-3.605469-19.234376-20.515625-33.835938-40.777344-33.835938h-37.269531c-20.988282 0-38.375 15.664062-41.109375 35.917969-5.382813-4.511719-11.128907-8.519531-17.167969-11.996094v-32.691406c7.964844-5.949219 13.132812-15.445313 13.132812-26.128907 0-12.640624-7.242187-23.617187-17.789062-29.019531v-22.773437c0-15.246094-10.886719-28-25.292969-30.898438v-15.480468h115.46875c18.535157 0 33.617188-15.078126 33.617188-33.617188 0-18.535156-15.082031-33.617188-33.617188-33.617188h-48.894531c-4.234375 0-7.664062 3.429688-7.664062 7.660157s3.429687 7.660156 7.664062 7.660156h48.894531c10.089844 0 18.296875 8.207031 18.296875 18.296875s-8.207031 18.296875-18.296875 18.296875h-290.1875c-10.089843 0-18.296875-8.207031-18.296875-18.296875 0-10.085938 8.207032-18.296875 18.296875-18.296875h210.644531c4.234376 0 7.660157-3.429687 7.660157-7.660156s-3.425781-7.660157-7.660157-7.660157h-35.925781v-5.417968c0-16.332032-13.289062-29.621094-29.621093-29.621094-16.339844 0-29.632813 13.289062-29.632813 29.621094v5.417968h-115.46875c-18.535156 0-33.617187 15.082032-33.617187 33.617188s15.082031 33.617188 33.617187 33.617188h115.464844v15.480468c-14.40625 2.902344-25.289063 15.652344-25.289063 30.902344v22.769531c-10.550781 5.402344-17.792968 16.378907-17.792968 29.023438 0 10.679687 5.167968 20.179687 13.132812 26.125v32.601562c-4.78125 2.761719-9.382812 5.875-13.78125 9.316407-3.875-18.894532-20.628906-33.148438-40.652344-33.148438h-37.269531c-20.265625 0-37.171875 14.605469-40.777344 33.835938h-54.359375c-4.230468 0-7.660156 3.429687-7.660156 7.660156 0 4.234375 3.429688 7.664062 7.660156 7.664062h53.640625v156.609375h-53.640625c-4.230468 0-7.660156 3.429688-7.660156 7.660157 0 4.230468 3.429688 7.660156 7.660156 7.660156h54.359375c3.605469 19.234375 20.515625 33.835937 40.777344 33.835937h37.269531c20.019532 0 36.773438-14.25 40.652344-33.144531 20.863281 16.320312 46.378906 25.160156 73.363281 25.160156 28.238281 0 55.371094-10.015625 76.738281-27.925781 2.738282 20.25 20.125 35.910156 41.113282 35.910156h37.265625c20.265625 0 37.171875-14.601562 40.777343-33.835937h54.359376c4.230468 0 7.660156-3.429688 7.660156-7.660156 0-4.230469-3.425782-7.664063-7.65625-7.664063zm-264.566406-411.488281c0-7.886719 6.417968-14.300782 14.308593-14.300782 7.886719 0 14.300781 6.414063 14.300781 14.300782v5.417968h-28.609374zm28.609374 72.652344v14.855468h-28.609374v-14.855468zm-37.703124 30.179687h46.796874c8.929688 0 16.199219 7.265625 16.199219 16.199219v19.203125h-79.195312v-19.203125c0-8.933594 7.269531-16.199219 16.199219-16.199219zm-16.722657 50.726563h80.242188c9.519531 0 17.265625 7.746093 17.265625 17.265624 0 9.519532-7.746094 17.265626-17.265625 17.265626h-80.242188c-9.519531 0-17.265625-7.746094-17.265625-17.265626 0-9.519531 7.746094-17.265624 17.265625-17.265624zm-4.132812 49.582031c1.355469.175781 2.730469.273437 4.132812.273437h80.242188c1.402343 0 2.777343-.097656 4.132812-.273437v19.085937c-13.925781-5.558594-28.921875-8.511718-44.25-8.511718-15.433593 0-30.382812 2.890624-44.257812 8.414062zm-133.203125 216.007812v-171.929687c0-13.761719 10.675781-25.066406 24.179687-26.089844v224.113281c-13.503906-1.023437-24.179687-12.332031-24.179687-26.09375zm65.441406 26.09375v-41.511719c0-4.230468-3.429688-7.660156-7.664062-7.660156-4.230469 0-7.660157 3.429688-7.660157 7.660156v41.59375h-10.613281v-224.28125h10.613281v152.042969c0 4.230469 3.429688 7.660157 7.660157 7.660157 4.234374 0 7.664062-3.429688 7.664062-7.660157v-151.960937c13.503906 1.023437 24.179688 12.332031 24.179688 26.09375v8.058594.152343 155.519531.148438 8.050781c0 13.761719-10.675782 25.070313-24.179688 26.09375zm112.019531-7.902343c-27.359375 0-52.988281-10.417969-72.519531-29.402344v-149.5c7.101562-6.90625 15.011719-12.675782 23.523438-17.222656.449218-.1875.878906-.417969 1.285156-.683594 14.554687-7.515625 30.800781-11.496094 47.714844-11.496094 28.996093 0 56.660156 12.109375 76.351562 33.324219v141.660156c-19.691406 21.210937-47.355469 33.320313-76.355469 33.320313zm91.675781-18.191407v-171.929687c0-13.761719 10.679688-25.070313 24.183594-26.089844v224.113281c-13.507812-1.023437-24.183594-12.332031-24.183594-26.09375zm39.503907 26.175781v-224.28125h10.613281v224.28125zm25.933593-.082031v-224.117187c13.503907 1.023437 24.183594 12.332031 24.183594 26.09375v171.929687c0 13.761719-10.679687 25.070313-24.183594 26.09375zm0 0'
      )
      path.set({ left: 120, top: 120, fill: '#faa' })
      this.canvas.add(path)
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
        }
      })
    },
    getConditionalData(data) {
      if (data && data.length) {
        this.currentObject.fgraphic.conditionalFormatting = data
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
    addFan() {
      let self = this
      fabric.fabric.loadSVGFromString(
        '<?xml version="1.0" encoding="UTF-8"?><svg width="30px" height="30px" viewBox="0 0 30 30" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><title>Fan</title><desc>Created with Sketch.</desc><defs><linearGradient x1="87.463788%" y1="16.0727894%" x2="23.4200858%" y2="89.0030632%" id="linearGradient-1"><stop stop-color="#99ACB9" offset="0%"></stop><stop stop-color="#DBE1E6" offset="100%"></stop></linearGradient></defs><g id="AHU-Illustration" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"><g id="Components" transform="translate(-479.000000, -290.000000)" fill="url(#linearGradient-1)"><g id="Fan" transform="translate(479.000000, 290.000000)"><g id="Group-27-Copy-6" transform="translate(15.000000, 15.000000) scale(-1, 1) translate(-15.000000, -15.000000) "><circle id="Oval" cx="15" cy="15" r="3"></circle><ellipse id="Oval" cx="15" cy="5.5" rx="4" ry="5.5"></ellipse><ellipse id="Oval-Copy-6" transform="translate(15.000000, 24.500000) scale(-1, -1) translate(-15.000000, -24.500000) " cx="15" cy="24.5" rx="4" ry="5.5"></ellipse><ellipse id="Oval-Copy-7" transform="translate(24.500000, 15.000000) rotate(90.000000) translate(-24.500000, -15.000000) " cx="24.5" cy="15" rx="4" ry="5.5"></ellipse><ellipse id="Oval-Copy-8" transform="translate(5.500000, 15.000000) scale(-1, -1) rotate(90.000000) translate(-5.500000, -15.000000) " cx="5.5" cy="15" rx="4" ry="5.5"></ellipse></g></g></g></g></svg>',
        function(objects) {
          let groupedObject = fabric.fabric.util.groupSVGElements(objects)

          fabric.fabric.loadSVGFromString(
            '<?xml version="1.0" encoding="UTF-8"?><svg width="99px" height="100px" viewBox="0 0 99 100" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"><title>Fan 1</title><desc>Created with Sketch.</desc><defs><linearGradient x1="95.1568957%" y1="22.8415126%" x2="28.3000453%" y2="81.1660069%" id="linearGradient-1"><stop stop-color="#42535E" offset="0%"></stop><stop stop-color="#1F2930" offset="100%"></stop></linearGradient><linearGradient x1="87.463788%" y1="16.0727894%" x2="23.4200858%" y2="89.0030632%" id="linearGradient-2"><stop stop-color="#99ACB9" offset="0%"></stop><stop stop-color="#DBE1E6" offset="100%"></stop></linearGradient><linearGradient x1="79.0215099%" y1="91.770871%" x2="18.02391%" y2="12.580571%" id="linearGradient-3"><stop stop-color="#42535E" offset="0%"></stop><stop stop-color="#1F2930" offset="100%"></stop></linearGradient><linearGradient x1="87.463788%" y1="16.0727894%" x2="-9.17620837%" y2="143.223567%" id="linearGradient-4"><stop stop-color="#99ACB9" offset="0%"></stop><stop stop-color="#DBE1E6" offset="100%"></stop></linearGradient><linearGradient x1="73.8528983%" y1="0%" x2="73.8528983%" y2="100%" id="linearGradient-5"><stop stop-color="#282828" offset="0%"></stop><stop stop-color="#C6D0E1" offset="100%"></stop></linearGradient></defs><g id="AHU-Illustration" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"><g id="Components" transform="translate(-98.000000, -255.000000)"><g id="Fan-1" transform="translate(147.500000, 305.000000) scale(-1, 1) translate(-147.500000, -305.000000) translate(98.000000, 255.000000)"><polygon id="Path-2-Copy" fill="#5B6777" points="99 0 99 77.26 1.91846539e-13 77.26 1.91846539e-13 0"></polygon><polygon id="Path-4-Copy" fill="#848E9C" points="0 77 0 100 99 100 99 77"></polygon><g id="Group-27-Copy-2" transform="translate(49.500000, 49.500000) scale(-1, 1) translate(-49.500000, -49.500000) translate(6.000000, 10.000000)"><path d="M68.3651583,43.6717631 L81.3063409,37.6401623 C81.6715892,44.3471969 80.7348085,49.2572121 78.4959987,52.370208 C76.2571888,55.4832039 69.0590646,61.403973 56.9016259,70.1325154 L68.3651583,43.6717631 Z" id="Path-28" fill="url(#linearGradient-1)"></path><rect id="Rectangle" fill="#99ACB9" x="31" y="11" width="44" height="33"></rect><circle id="Oval-Copy-5" fill="url(#linearGradient-2)" cx="34" cy="45" r="34"></circle><circle id="Oval" fill="url(#linearGradient-3)" cx="34" cy="45" r="18"></circle><path d="M75.1680043,11.245837 L85.8075031,0.606338247 C60.9289868,0.211590009 45.0157124,0.397834357 38.0676801,1.16507129 C29.3682767,2.1257035 25.3578118,5.78442405 23.788258,6.86348324 C20.3383493,9.23527558 16.7751875,12.9860341 13.0987726,18.1157588 C18.0689898,15.2103642 22.0992812,13.3408657 25.1896468,12.5072634 C28.2800123,11.673661 32.7392083,11.2531856 38.5672346,11.245837 L75.1680043,11.245837 Z" id="Path-26" fill="url(#linearGradient-4)"></path><polygon id="Path-27" stroke="#26333C" fill="#8194A2" points="75.5068392 42.9778867 75.5068392 11.4998489 85.3696371 1.77270043 85.788352 19.2099214 86.1045192 32.3765815"></polygon></g><polygon id="Path-4" fill="url(#linearGradient-5)" opacity="0.5" points="0 0 0 30 99 30 99 0"></polygon></g></g></g></svg>',
            function(objects) {
              let groupedObject1 = fabric.fabric.util.groupSVGElements(objects)

              groupedObject.set({
                left: 40,
                top: 55,
              })
              let group = new fabric.fabric.Group([
                groupedObject1,
                groupedObject,
              ])

              groupedObject.set({
                originX: 'center',
                originY: 'center',
              })
              self.canvas.add(group)
              self.canvas.renderAll()

              let angle = 0
              let animate = function() {
                angle += 30
                if (angle > 360) {
                  angle = 0
                }
                groupedObject.set('angle', angle)
                groupedObject.dirty = true
                self.canvas.renderAll()
                fabric.fabric.util.requestAnimFrame(
                  animate,
                  self.canvas.upperCanvasEl
                )
              }

              animate()
            }
          )
        }
      )
    },
  },
}
</script>
<style scoped>
/* Drag drop placeholder styles for fields, sections */
.ghost-class-top,
.ghost-class,
.ghost-class-sections {
  margin: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 0 1 50%;
}
.ghost-class:before,
.ghost-class-top:before,
.ghost-class-sections:before {
  content: '';
  border: 1px dashed #9ed7de;
  height: 40px;
  display: block;
  background: #f9feff;
}
.ghost-class-sections:before {
  position: relative;
  margin: 0 10px 5px 10px;
}
.ghost-class-top:before {
  margin-top: 20px;
}
.ghost-class *,
.ghost-class-top *,
.ghost-class-sections * {
  display: none;
}
.el-col.el-col-24.ghost-class-sections {
  float: none;
}
.section-container.position-relative.ghost-class-sections {
  border: none;
}
.disable-hover .form-label-container {
  pointer-events: none !important;
}
.layout-icon,
.delete-icon {
  position: absolute;
  z-index: 1000;
  cursor: pointer;
  width: 15px;
}
.delete-icon {
  top: 17px;
  right: -9px;
  width: 15px;
}
.layout-icon {
  top: 15px;
  right: 12px;
  width: 35px;
}
.field-icons {
  width: 15px;
  height: 15px;
  vertical-align: bottom;
}
.form-builder-main-form {
  height: calc(100vh - 100px);
  overflow: scroll;
}
.fc-input-full-border-textarea .el-textarea__inner {
  min-height: 40px;
}
.setting-header2 {
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(155, 157, 180, 0.1);
  background-color: #fff;
  border-color: #e6ecf3;
  border-width: 0 0 1px 0px;
  border-style: solid;
}
.section-container {
  border: 1px dashed #9ed7de;
}
.section-container.border-none {
  border: none;
}
.form-label-container .delete-icon,
.form-label-container .layout-icon {
  display: none;
}
.form-label-container:hover .delete-icon,
.form-label-container:hover .layout-icon {
  display: block;
}
.section-header {
  flex: 1 1 100%;
  border-bottom: 1px dashed #9ed7de;
  margin-left: -10px;
  margin-right: -10px;
}
.section-header .section-icon {
  vertical-align: middle;
  margin-left: 20px;
  width: 17px;
  height: 17px;
}
.section-header .section-name {
  display: inline-flex;
  margin-left: 15px;
  margin-bottom: 10px;
}
.saving-container {
  font-size: 11px;
  font-weight: 500;
  font-style: italic;
  letter-spacing: 0.4px;
  color: #39b2c2;
  margin-left: 15px;
}
.field-label {
  font-size: 0.75rem;
}
.setting-form-title:hover {
  cursor: pointer;
}
.section-container.active {
  background: #f3fafb;
}

.graphics-builder-toolbar ul {
  padding: 0;
  margin: 0;
}

.graphics-builder-toolbar ul li {
  list-style: none;
  display: inline;
  cursor: pointer;
}

.graphics-builder-toolbar ul li i {
  font-size: 20px;
  padding: 12px 12px;
}

.graphics-builder-toolbar {
  height: 50px;
  background: #fafafa;
  border-bottom: 1px solid #ededed;
  box-shadow: 0 3px 4px 0 hsla(0, 0%, 85.5%, 0.32);
  padding: 1px;
}

.graphics-builder-toolbar .el-button-group {
  padding: 0 6px;
  border-right: 1px solid #ededed;
}

.graphics-builder-toolbar .el-button {
  border: none;
  background: transparent;
}

.graphics-editor-container {
  width: 100%;
  border: 0;
  position: relative;
}

.graphic-object-dialog .el-dialog__body {
  height: 400px;
}
.graphics-loading-overlay {
  opacity: 0.05;
  cursor: progress;
}

.graphic-asset-summary {
  width: 1000px;
  height: 650px;
  overflow-x: scroll;
  overflow-y: hidden;
}

.graphics-actions {
  position: absolute;
  min-width: 200px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
}
.graphics-actions .graphics-actions-menu {
  padding: 6px 0;
  margin: 0;
}
.graphics-actions .graphics-actions-menu-item {
  list-style: none;
  padding: 12px;
  font-size: 13px;
  cursor: pointer;
}
.graphics-actions .graphics-actions-menu-section {
  border-top: 1px solid #ededed;
}
.graphics-actions .graphics-actions-menu-item:hover {
  background: #f5f7fa;
}
.graphics-add-image {
  opacity: 0;
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  cursor: pointer;
}
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
.graphics-insert-variable-list {
  max-height: 300px;
  overflow: scroll;
}
.graphics-insert-variable-list ul {
  padding: 0;
  margin: 0;
}
.graphics-insert-variable-list ul li {
  list-style: none;
  padding: 12px;
  cursor: pointer;
}
.graphics-insert-variable-list ul li:hover {
  background: #fafafa;
}
.fplan-legend {
  position: absolute;
  right: 10px;
  top: 25px;
  z-index: 10;
  border: 1px solid #ddd;
  background-color: #fff;
  box-shadow: 0 2px 2px rgba(0, 0, 0, 0.1);
  border-radius: 3px;
  min-width: 150px;
  text-align: left;
}

.fplan-legend ul {
  padding: 0;
  margin: 0;
}

.fplan-legend ul li {
  list-style: none;
  margin: 8px;
  padding: 3px;
  cursor: default;
}

.fplan-legend ul li:hover {
  font-weight: 500;
}

.fplan-legend ul li div {
  display: inline-block;
  padding: 4px;
}
.fplan-space-info-card {
  position: absolute;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  box-sizing: border-box;
  max-width: 250px;
  text-align: center;
}
.fplan-space-info-card .space-title {
  font-size: 18px;
  padding: 5px;
  margin-top: 5px;
}

.fplan-space-info-card .space-subtitle {
  font-size: 13px;
  padding: 5px;
  opacity: 0.7;
}
.graphics-actions-submenu {
  position: absolute;
  bottom: 0px;
  padding: 0px;
  margin: 0;
  list-style: none;
  min-width: 150px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}
.graphics-actions-submenu li {
  padding: 10px;
  font-size: 13px;
  cursor: pointer;
  background: #fff;
}
.graphics-actions-submenu li:hover {
  background: #f5f7fa;
}
</style>
