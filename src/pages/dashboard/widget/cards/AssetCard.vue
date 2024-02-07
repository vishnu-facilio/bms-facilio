<template>
  <div class="dragabale-card asset-card-layout">
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else class="asset-card-body">
      <div class="card-container" v-if="tools && data && metaData">
        <template v-if="metaData.cardType === 'AHU'">
          <div class="card-layout AHU">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="
                tools['runStatus'].value !== null ||
                  tools['tripStatus'].value !== null
              "
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['runStatus'].value"
                  src="~assets/product-icons/on-off-icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/off-on-icon.svg"
                />
              </div>
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  v-if="tools['tripStatus'].value"
                  src="~assets/product-icons/bell-dot-icon.svg"
                  :title="'TRIP STATUS: TRIPPED'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
                <img
                  class="card-svg-icon"
                  v-else-if="tools['tripStatus'].value !== null"
                  src="~assets/product-icons/bell-icon.svg"
                  :title="'TRIP STATUS: NORMAL'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section gray-bg"
              v-if="tools['autoStatus'].value !== null"
            >
              <div class="autoStatus" v-if="tools['autoStatus'].value">
                {{ $t('panel.tyre.auto') }}
              </div>
              <div class="autoStatus" v-else>{{ $t('panel.tyre.manual') }}</div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row single"
              v-if="tools['temperature'].value !== null"
            >
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-6">
                {{ Number(tools['temperature'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['valveFeedback'].value !== null"
            >
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/volve-icon.svg"
                />
              </div>
              <div class="col-6">
                <span v-if="tools['valveFeedback'].value">{{
                  Math.round(tools['valveFeedback'].value)
                }}</span>
                <span
                  v-if="tools['valveFeedback'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="
                tools['scheduledControl'].value !== null ||
                  tools['scheduledControl'].value !== null
              "
            >
              <div
                class="col-6 pointer"
                v-if="tools['scheduledControl'].value !== null"
              >
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                  @click="
                    setShuduleValue(
                      tools['scheduledControl'].value,
                      'scheduledControl'
                    )
                  "
                  v-if="tools['scheduledControl'].value"
                />
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/clock.svg"
                  @click="
                    setShuduleValue(
                      tools['scheduledControl'].value,
                      'scheduledControl'
                    )
                  "
                  v-else
                />
              </div>
              <div
                class="col-6 pointer disabled"
                v-if="tools['scheduledControl'].value === null"
              >
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                  v-if="tools['scheduledControl'].value"
                />
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/clock.svg"
                  v-else
                />
              </div>
              <div
                class="col-6"
                v-if="tools['scheduledControl'].value !== null"
              >
                <el-tooltip :content="'Set value'" placement="top">
                  <el-switch
                    v-model="tools['staticControl'].value"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    @change="
                      setReading(
                        tools['scheduledControl'].value,
                        'scheduledControl'
                      )
                    "
                    size="mini"
                  >
                  </el-switch>
                </el-tooltip>
              </div>
              <div
                class="col-6 disabled"
                v-if="tools['staticControl'].value === null"
              >
                <el-tooltip :content="'Set value'" placement="top">
                  <el-switch
                    v-model="tools['staticControl'].value"
                    active-color="#c1c0c0"
                    inactive-color="#c1c0c0"
                    size="mini"
                  >
                  </el-switch>
                </el-tooltip>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC'">
          <div class="card-layout HVAC">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['supplyFanStatus'].value !== null"
            >
              <div class="supplyFanStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['supplyFanStatus'].value"
                  src="~assets/product-icons/SupplyFanicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/SupplyFanicon_off.svg"
                />
              </div>
              <div class="supplyFanStatus_value displayName col-7">
                {{ tools['supplyFanStatus'].displayName }}
              </div>
              <div
                class="supplyFanStatus_value col-3"
                v-if="tools['supplyFanStatus'].value !== null"
              >
                <div v-if="tools['supplyFanStatus'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['exhaustFanStatus'].value !== null"
            >
              <div class="exhaustFanStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['exhaustFanStatus'].value"
                  src="~assets/product-icons/ExhanustFanicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/ExhanustFanicon_off.svg"
                />
              </div>
              <div class="supplyFanStatus_value displayName col-7">
                {{ tools['exhaustFanStatus'].displayName }}
              </div>
              <div
                class="exhaustFanStatus col-3"
                v-if="tools['exhaustFanStatus'].value !== null"
              >
                <div v-if="tools['exhaustFanStatus'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['heatWheelStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['heatWheelStatus'].value"
                  src="~assets/product-icons/HeatWheelicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/HeatWheelicon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['heatWheelStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['heatWheelStatus'].value !== null"
              >
                <div v-if="tools['heatWheelStatus'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['temperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>Supply</div>
                <div>{{ tools['temperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{ Number(tools['temperature'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'LIGHT'">
          <div class="card-layout LIGHT">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="
                tools['lightStatus'].value !== null ||
                  tools['tripStatus'].value !== null
              "
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon"
                  v-if="tools['lightStatus'].value"
                  src="~statics/cardIcon/light-bulb-on.svg"
                />
                <img
                  class="card-svg-icon"
                  v-else
                  src="~statics/cardIcon/light-bulb-off.svg"
                />
              </div>
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  v-if="tools['tripStatus'].value"
                  src="~assets/product-icons/bell-dot-icon.svg"
                  :title="'TRIP STATUS: TRIPPED'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
                <img
                  class="card-svg-icon"
                  v-else-if="tools['tripStatus'].value !== null"
                  src="~assets/product-icons/bell-icon.svg"
                  :title="'TRIP STATUS: NORMAL'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section gray-bg"
              v-if="tools['autoStatus'].value !== null"
            >
              <div class="autoStatus" v-if="tools['autoStatus'].value">
                {{ $t('panel.tyre.auto') }}
              </div>
              <div class="autoStatus" v-else>{{ $t('panel.tyre.manual') }}</div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="
                tools['scheduledControl'].value !== null ||
                  tools['scheduledControl'].value !== null
              "
            >
              <div
                class="col-6 pointer"
                v-if="tools['scheduledControl'].value !== null"
              >
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                  @click="
                    setShuduleValue(
                      tools['scheduledControl'].value,
                      'scheduledControl'
                    )
                  "
                  v-if="tools['scheduledControl'].value"
                />
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/clock.svg"
                  @click="
                    setShuduleValue(
                      tools['scheduledControl'].value,
                      'scheduledControl'
                    )
                  "
                  v-else
                />
              </div>
              <div
                class="col-6 pointer disabled"
                v-if="tools['scheduledControl'].value === null"
              >
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                  v-if="tools['scheduledControl'].value"
                />
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/clock.svg"
                  v-else
                />
              </div>
              <div
                class="col-6"
                v-if="tools['scheduledControl'].value !== null"
              >
                <el-tooltip :content="'Set value'" placement="top">
                  <el-switch
                    v-model="tools['staticControl'].value"
                    active-color="#13ce66"
                    inactive-color="#ff4949"
                    @change="
                      setReading(
                        tools['scheduledControl'].value,
                        'scheduledControl'
                      )
                    "
                    size="mini"
                  >
                  </el-switch>
                </el-tooltip>
              </div>
              <div
                class="col-6 disabled"
                v-if="tools['scheduledControl'].value === null"
              >
                <el-tooltip :content="'Set value'" placement="top">
                  <el-switch
                    v-model="tools['staticControl'].value"
                    active-color="#c1c0c0"
                    inactive-color="#c1c0c0"
                    size="mini"
                  >
                  </el-switch>
                </el-tooltip>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'PUMP'">
          <div class="card-layout PUMP">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row light"
              v-if="tools['runStatusPercent'].value !== null"
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['runStatusPercent'].value"
                  src="~assets/product-icons/on-off-icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/off-on-icon.svg"
                />
              </div>
              <div class="col-6">
                <span v-if="tools['runStatusPercent'].value">{{
                  Math.round(tools['runStatusPercent'].value)
                }}</span>
                <span
                  v-if="tools['runStatusPercent'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg"
              v-if="
                tools['autoStatus'].value !== null ||
                  tools['tripStatus'].value !== null
              "
            >
              <div class="col-8">
                <div class="autoStatus" v-if="tools['autoStatus'].value">
                  {{ $t('panel.tyre.auto') }}
                </div>
                <div class="autoStatus" v-else>
                  {{ $t('panel.tyre.manual') }}
                </div>
              </div>
              <div class="col-4">
                <img
                  class="card-svg-icon"
                  v-if="tools['tripStatus'].value"
                  src="~assets/product-icons/bell-dot-icon.svg"
                  :title="'TRIP STATUS: TRIPPED'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
                <img
                  class="card-svg-icon"
                  v-else-if="tools['tripStatus'].value !== null"
                  src="~assets/product-icons/bell-icon.svg"
                  :title="'TRIP STATUS: NORMAL'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'PUMP1'">
          <div class="card-layout PUMP">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row light"
              v-if="
                tools['runStatus'].value !== null ||
                  tools['percentage'].value !== null
              "
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['runStatus'].value"
                  src="~assets/product-icons/on-off-icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/off-on-icon.svg"
                />
              </div>
              <div class="col-6">
                <span v-if="tools['percentage'].value">{{
                  Math.round(tools['percentage'].value)
                }}</span>
                <span
                  v-if="tools['percentage'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg"
              v-if="
                tools['autoStatus'].value !== null ||
                  tools['tripStatus'].value !== null
              "
            >
              <div class="col-8">
                <div class="autoStatus" v-if="tools['autoStatus'].value">
                  {{ $t('panel.tyre.auto') }}
                </div>
                <div class="autoStatus" v-else>
                  {{ $t('panel.tyre.manual') }}
                </div>
              </div>
              <div class="col-4">
                <img
                  class="card-svg-icon"
                  v-if="tools['tripStatus'].value"
                  src="~assets/product-icons/bell-dot-icon.svg"
                  :title="'TRIP STATUS: TRIPPED'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
                <img
                  class="card-svg-icon"
                  v-else-if="tools['tripStatus'].value !== null"
                  src="~assets/product-icons/bell-icon.svg"
                  :title="'TRIP STATUS: NORMAL'"
                  v-tippy="{
                    distance: 0,
                    interactive: true,
                    theme: 'light',
                    animation: 'scale',
                    arrow: true,
                  }"
                />
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'WATER_TANK'">
          <div class="card-layout WATER_TANK">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row light"
              v-if="tools['levelPercent'].value !== null"
            >
              <span v-if="tools['levelPercent'].value">{{
                Math.round(tools['levelPercent'].value)
              }}</span>
              <span
                v-if="tools['levelPercent'].value"
                class="card-unit"
                v-html="'%'"
              ></span>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg level-button"
              v-if="tools['levelPercent'].value !== null"
            >
              <div
                class="col-3 level-section L"
                v-bind:class="{
                  active1:
                    Number(tools['levelPercent'].value) > 0 &&
                    Number(tools['levelPercent'].value) < 26,
                }"
              >
                <div class="level-btn">L</div>
              </div>
              <div
                class="col-3 level-section M"
                v-bind:class="{
                  active1:
                    Number(tools['levelPercent'].value) > 26 &&
                    Number(tools['levelPercent'].value) < 51,
                }"
              >
                <div class="level-btn">M</div>
              </div>
              <div
                class="col-3 level-section H"
                v-bind:class="{
                  active1:
                    Number(tools['levelPercent'].value) > 51 &&
                    Number(tools['levelPercent'].value) < 76,
                }"
              >
                <div class="level-btn">H</div>
              </div>
              <div
                class="col-3 level-section C"
                v-bind:class="{
                  active1:
                    Number(tools['levelPercent'].value) > 76 &&
                    Number(tools['levelPercent'].value) < 101,
                }"
              >
                <div class="level-btn">C</div>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC2'">
          <div class="card-layout PUMP">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row light"
              v-if="tools['runStatusValue'].value !== null"
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['runStatusValue'].value"
                  src="~assets/product-icons/on-off-icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/off-on-icon.svg"
                />
              </div>
              <div class="col-6" v-if="tools['runStatusValue'].value !== null">
                <span v-if="tools['runStatusValue'].value" class="on-value">{{
                  $t('panel.tyre.on')
                }}</span>
                <span v-else class="off-value">{{ $t('panel.tyre.off') }}</span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row single"
              v-if="tools['temperature'].value !== null"
            >
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-6 temperature">
                {{ Number(tools['temperature'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC3'">
          <div class="card-layout HVAC3">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row light"
              v-if="tools['runStatusValue'].value !== null"
            >
              <div class="runstatus col-6">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['runStatusValue'].value"
                  src="~assets/product-icons/on-off-icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/off-on-icon.svg"
                />
              </div>
              <div class="col-6" v-if="tools['runStatusValue'].value !== null">
                <span v-if="tools['runStatusValue'].value" class="on-value">{{
                  $t('panel.tyre.on')
                }}</span>
                <span v-else class="off-value">{{ $t('panel.tyre.off') }}</span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section gray-bg"
              v-if="tools['autoStatus'].value !== null"
            >
              <div class="autoStatus" v-if="tools['autoStatus'].value">
                {{ $t('panel.tyre.auto') }}
              </div>
              <div class="autoStatus" v-else>{{ $t('panel.tyre.manual') }}</div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['valveFeedback'].value !== null"
            >
              <div class="col-6">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/volve-icon.svg"
                />
              </div>
              <div class="col-6">
                <span v-if="tools['valveFeedback'].value">{{
                  Math.round(tools['valveFeedback'].value)
                }}</span>
                <span
                  v-if="tools['valveFeedback'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC4'">
          <div class="card-layout HVAC4">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['EnthalpyheatWheelStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['EnthalpyheatWheelStatus'].value"
                  src="~assets/product-icons/HeatWheelicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/HeatWheelicon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['EnthalpyheatWheelStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['EnthalpyheatWheelStatus'].value !== null"
              >
                <div
                  v-if="tools['EnthalpyheatWheelStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['SensibleheatWheelStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['SensibleheatWheelStatus'].value"
                  src="~assets/product-icons/HeatWheelicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/HeatWheelicon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['SensibleheatWheelStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['SensibleheatWheelStatus'].value !== null"
              >
                <div
                  v-if="tools['SensibleheatWheelStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['chilledWaterSupply'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['chilledWaterSupply'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['chilledWaterSupply'].value)
                    ? Number(tools['chilledWaterSupply'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['chilledWaterReturn'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['chilledWaterReturn'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['chilledWaterReturn'].value)
                    ? Number(tools['chilledWaterReturn'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['coolingCoilValveFeedback'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/volve-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['coolingCoilValveFeedback'].displayName }}</div>
              </div>
              <div class="col-3 temperature">
                <span v-if="tools['coolingCoilValveFeedback'].value">{{
                  Math.round(tools['coolingCoilValveFeedback'].value)
                }}</span>
                <span
                  v-if="tools['coolingCoilValveFeedback'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC5'">
          <div class="card-layout HVAC4">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['outsideAirDamperStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['outsideAirDamperStatus'].value"
                  src="~assets/product-icons/box_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/box_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['outsideAirDamperStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['outsideAirDamperStatus'].value !== null"
              >
                <div
                  v-if="tools['outsideAirDamperStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.open') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.close') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['supplyFanAirflow'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['supplyFanAirflow'].value"
                  src="~assets/product-icons/Drop_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Drop_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['supplyFanAirflow'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['supplyFanAirflow'].value !== null"
              >
                <div v-if="tools['supplyFanAirflow'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['supplyFanHOAStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['supplyFanHOAStatus'].value"
                  src="~assets/product-icons/Auto_icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Manual_icon.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['supplyFanHOAStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['supplyFanHOAStatus'].value !== null"
              >
                <div v-if="tools['supplyFanHOAStatus'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['supplyFanVFDFeedback'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/volve-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyFanVFDFeedback'].displayName }}</div>
              </div>
              <div class="col-3 temperature">
                <span v-if="tools['supplyFanVFDFeedback'].value">{{
                  Math.round(tools['supplyFanVFDFeedback'].value)
                }}</span>
                <span
                  v-if="tools['supplyFanVFDFeedback'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['supplyFanVFDRuntime'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyFanVFDRuntime'].displayName }}</div>
              </div>
              <div class="col-3 temperature">
                <span v-if="tools['supplyFanVFDRuntime'].value"
                  >{{
                    Math.round(tools['supplyFanVFDRuntime'].value)
                  }}&nbsp;h</span
                >
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC6'">
          <div class="card-layout HVAC4">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['exhaustAirDamperStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['exhaustAirDamperStatus'].value"
                  src="~assets/product-icons/box_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/box_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['exhaustAirDamperStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['exhaustAirDamperStatus'].value !== null"
              >
                <div
                  v-if="tools['exhaustAirDamperStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.open') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.close') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['exhaustFanAirflow'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['exhaustFanAirflow'].value"
                  src="~assets/product-icons/Drop_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Drop_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['exhaustFanAirflow'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['exhaustFanAirflow'].value !== null"
              >
                <div v-if="tools['exhaustFanAirflow'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['exhaustFanHOAStatus'].value !== null"
            >
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['exhaustFanHOAStatus'].value"
                  src="~assets/product-icons/Auto_icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Manual_icon.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['exhaustFanHOAStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['exhaustFanHOAStatus'].value !== null"
              >
                <div v-if="tools['exhaustFanHOAStatus'].value" class="on-value">
                  {{ $t('panel.tyre.auto') }}
                </div>
                <div v-else class="off-value">
                  {{ $t('panel.tyre.manual') }}
                </div>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['exhaustFanVFDFeedback'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/volve-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['exhaustFanVFDFeedback'].displayName }}</div>
              </div>
              <div class="col-3 temperature">
                <span v-if="tools['exhaustFanVFDFeedback'].value">{{
                  Math.round(tools['exhaustFanVFDFeedback'].value)
                }}</span>
                <span
                  v-if="tools['exhaustFanVFDFeedback'].value"
                  class="card-unit"
                  v-html="'%'"
                ></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row gray-bg single"
              v-if="tools['exhaustFanVFDRuntime'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/active_clock.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['exhaustFanVFDRuntime'].displayName }}</div>
              </div>
              <div class="col-3 temperature">
                <span v-if="tools['exhaustFanVFDRuntime'].value"
                  >{{
                    Math.round(tools['exhaustFanVFDRuntime'].value)
                  }}&nbsp;h</span
                >
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC7'">
          <div class="card-layout HVAC4">
            <div
              :style="'height:' + sectionHeight"
              class="card-section header row"
            >
              {{ metaData.tools['name'].value }}
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['outsideAirTemperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['outsideAirTemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['outsideAirTemperature'].value)
                    ? Number(tools['outsideAirTemperature'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['supplyAirTemperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyAirTemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['supplyAirTemperature'].value)
                    ? Number(tools['supplyAirTemperature'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['onCoiltemperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['onCoiltemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['onCoiltemperature'].value)
                    ? Number(tools['onCoiltemperature'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['offCoiltemperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['offCoiltemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['offCoiltemperature'].value)
                    ? Number(tools['offCoiltemperature'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div
              :style="'height:' + sectionHeight"
              class="card-section row"
              v-if="tools['returnAirTemperature'].value !== null"
            >
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['returnAirTemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{
                  Number(tools['returnAirTemperature'].value)
                    ? Number(tools['returnAirTemperature'].value).toFixed(2)
                    : ''
                }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC8'">
          <div class="card-layout HVAC4">
            <div class="card-section header row">
              {{ metaData.tools['name'].value }}
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyAirTemperature'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{ Number(tools['supplyAirTemperature'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>
                  {{ tools['supplyAirTemperatureSetpoint'].displayName }}
                </div>
              </div>
              <div
                class="temperature col-3"
                v-if="tools['supplyAirTemperatureSetpoint'].value !== null"
              >
                {{ Number(tools['supplyAirTemperatureSetpoint'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['returnAirTemperatureAHRW'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{ Number(tools['returnAirTemperatureAHRW'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyAirTemperatureBHW'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{ Number(tools['supplyAirTemperatureBHW'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~statics/cardIcon/thermometer.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyAirTemperatureAHW'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                {{ Number(tools['supplyAirTemperatureAHW'].value) }}
                <span class="card-unit" v-html="'&degC'"></span>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC9'">
          <div class="card-layout HVAC4">
            <div class="card-section header row">
              {{ metaData.tools['name'].value }}
            </div>
            <div class="card-section row">
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['exhaustFanAirflow'].value"
                  src="~assets/product-icons/Drop_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Drop_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['exhaustFanAirflow'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['exhaustFanAirflow'].value !== null"
              >
                <div v-if="tools['exhaustFanAirflow'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">OFF</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['exhaustFanHOAStatus'].value"
                  src="~assets/product-icons/Auto_icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Manual_icon.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['exhaustFanHOAStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['exhaustFanHOAStatus'].value !== null"
              >
                <div v-if="tools['exhaustFanHOAStatus'].value" class="on-value">
                  {{ $t('panel.tyre.auto') }}
                </div>
                <div v-else class="off-value">
                  {{ $t('panel.tyre.manual') }}
                </div>
              </div>
            </div>
            <div class="card-section row">
              <div class="supplyFanStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['exhaustFanVFDrunStatus'].value"
                  src="~assets/product-icons/SupplyFanicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/SupplyFanicon_off.svg"
                />
              </div>
              <div class="exhaustFanStatus displayName col-7">
                {{ tools['exhaustFanVFDrunStatus'].displayName }}
              </div>
              <div
                class="supplyFanStatus_value col-3"
                v-if="tools['exhaustFanVFDrunStatus'].value !== null"
              >
                <div
                  v-if="tools['exhaustFanVFDrunStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~assets/product-icons/bell-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['exhasutFantripStatus'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                <div
                  v-if="tools['exhasutFantripStatus'].value"
                  class="off-value"
                >
                  {{ $t('panel.tyre.tripped') }}
                </div>
                <div v-else class="on-value">{{ $t('panel.tyre.normal') }}</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~assets/product-icons/bell-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['exhasutFanvfdtripStatus'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                <div
                  v-if="tools['exhasutFanvfdtripStatus'].value"
                  class="off-value"
                >
                  {{ $t('panel.tyre.tripped') }}
                </div>
                <div v-else class="on-value">{{ $t('panel.tyre.normal') }}</div>
              </div>
            </div>
          </div>
        </template>
        <template v-if="metaData.cardType === 'HVAC10'">
          <div class="card-layout HVAC4">
            <div class="card-section header row">
              {{ metaData.tools['name'].value }}
            </div>
            <div class="card-section row">
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['supplyFanAirflow'].value"
                  src="~assets/product-icons/Drop_icon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Drop_icon_off.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['supplyFanAirflow'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['supplyFanAirflow'].value !== null"
              >
                <div v-if="tools['supplyFanAirflow'].value" class="on-value">
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="heatWheelStatus cp-icon col-2">
                <img
                  class="card-svg-icon"
                  v-if="tools['supplyFanHOAStatus'].value"
                  src="~assets/product-icons/Auto_icon.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/Manual_icon.svg"
                />
              </div>
              <div class="heatWheelStatus_value displayName col-7">
                <div>{{ tools['supplyFanHOAStatus'].displayName }}</div>
              </div>
              <div
                class="heatWheelStatus_value col-3"
                v-if="tools['supplyFanHOAStatus'].value !== null"
              >
                <div v-if="tools['supplyFanHOAStatus'].value" class="on-value">
                  {{ $t('panel.tyre.auto') }}
                </div>
                <div v-else class="off-value">
                  {{ $t('panel.tyre.manual') }}
                </div>
              </div>
            </div>
            <div class="card-section row">
              <div class="supplyFanStatus cp-icon col-2">
                <img
                  class="card-svg-icon status-spin"
                  v-if="tools['supplyFanVFDrunStatus'].value"
                  src="~assets/product-icons/SupplyFanicon_on.svg"
                />
                <img
                  class="card-svg-icon "
                  v-else
                  src="~assets/product-icons/SupplyFanicon_off.svg"
                />
              </div>
              <div class="exhaustFanStatus displayName col-7">
                {{ tools['supplyFanVFDrunStatus'].displayName }}
              </div>
              <div
                class="supplyFanStatus_value col-3"
                v-if="tools['supplyFanVFDrunStatus'].value !== null"
              >
                <div
                  v-if="tools['supplyFanVFDrunStatus'].value"
                  class="on-value"
                >
                  {{ $t('panel.tyre.on') }}
                </div>
                <div v-else class="off-value">{{ $t('panel.tyre.off') }}</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~assets/product-icons/bell-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyFantripStatus'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                <div
                  v-if="tools['supplyFantripStatus'].value"
                  class="off-value"
                >
                  {{ $t('panel.tyre.tripped') }}
                </div>
                <div v-else class="on-value">{{ $t('panel.tyre.normal') }}</div>
              </div>
            </div>
            <div class="card-section row">
              <div class="cp-icon col-2">
                <img
                  class="card-svg-icon"
                  src="~assets/product-icons/bell-icon.svg"
                />
              </div>
              <div class="col-7 displayName ">
                <div>{{ tools['supplyFanvfdtripStatus'].displayName }}</div>
              </div>
              <div class="temperature col-3">
                <div
                  v-if="tools['supplyFanvfdtripStatus'].value"
                  class="off-value"
                >
                  {{ $t('panel.tyre.tripped') }}
                </div>
                <div v-else class="on-value">{{ $t('panel.tyre.normal') }}</div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import cardHelper from '@/mixins/AssetCardHelper'
export default {
  props: ['widget', 'config'],
  mixins: [cardHelper],
  data() {
    return {
      loading: false,
      metaData: null,
      BOOLEAN_LABEL: {
        // For lable mismatch
        true: 'OFF',
        false: 'ON',
      },
      selectedSetValue: null,
      readingSetDialog: false,
      data: {
        name: 'TEST',
        runStatus: true,
        autoStatus: true,
        temperature: 90.5,
        tripStatus: true,
        scheduledControl: true,
        staticControl: true,
      },
      meta: null,
    }
  },
  mounted() {
    this.loadCardData()
    /* temp solution only */
  },
  watch: {
    'config.editwidget': {
      // need to be removed
      handler(newData, oldData) {
        if (
          this.widget.id > -1 &&
          this.config.selectedWidgetId === this.widget.id &&
          newData !== oldData
        ) {
          this.updateCard()
        }
      },
      deep: true,
    },
    widget: {
      handler(newData, oldData) {
        this.updateCard()
      },
      deep: true,
    },
  },
  computed: {
    sectionHeight() {
      if (this.$el) {
        return 100 / this.getCardSpacificLayout(this.widget.layout.height) + '%'
        // return 'unset'
      } else {
        return 33 + 'px'
      }
    },
  },
  methods: {
    loadCardData() {
      let self = this
      let params = null
      if (this.widget && this.widget.id > -1) {
        params = {
          widgetId: self.widget.id,
        }
      } else {
        params = {
          workflow: {
            expressions:
              this.widget.dataOptions.readingComboCard &&
              this.widget.dataOptions.readingComboCard.expressions
                ? this.widget.dataOptions.readingComboCard.expressions
                : null,
            workflowUIMode: 1,
          },
          staticKey: 'readingComboCard',
        }
      }

      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getCardData(response.data)
        })
        .catch(function(error) {
          self.setParams('loading')
          console.log('******** error', error)
        })
    },
    updateSectionHeight() {
      if (this.$el) {
        return 100 / this.getCardSpacificLayout(this.widget.height) + '%'
        // return 'unset'
      } else {
        return 33 + 'px'
      }
    },
    getCardSpacificLayout(h) {
      let layout = {
        4: 1,
        6: 2,
        9: 3,
        12: 4,
        18: 5,
        20: 6,
        22: 7,
      }
      return parseInt(layout[h]) || parseInt(h / 4)
    },
    updateCard() {
      let self = this
      let params = null
      params = {
        workflow: {
          expressions:
            this.widget.dataOptions.readingComboCard &&
            this.widget.dataOptions.readingComboCard.expressions
              ? this.widget.dataOptions.readingComboCard.expressions
              : null,
          workflowUIMode: 1,
        },
        staticKey: 'readingComboCard',
      }
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.getCardData(response.data)
        })
        .catch(function(error) {
          self.setParams('loading')
          console.log('******** error' + error)
        })
    },
    setShuduleValue(value, key) {
      let self = this
      this.selectedSetValue = null
      self.$dialog
        .confirm({
          title: 'SET VALUE',
          message: 'Are you sure you want to ' + this.BOOLEAN_LABEL[value],
          rbDanger: true,
          rbLabel: 'SET',
        })
        .then(function(val) {
          if (val) {
            this.tools[key].value =
              this.tools[key].value !== null ? !this.tools[key].value : null
            let meta =
              self.widget.dataOptions.metaJson &&
              self.widget.dataOptions.metaJson
                ? JSON.parse(self.widget.dataOptions.metaJson)
                : null
            let assetId = meta.expressions
              ? Number(meta.expressions[0].criteria.conditions['1'].value)
              : -1
            let fieldId = meta.expressions
              ? meta.expressions.find(rt => rt.name === key).fieldId
              : -1
            this.$http
              .post('/v2/reading/set', {
                assetId: assetId,
                fieldId: fieldId,
                value: self.tools[key].value,
              })
              .then(response => {})
              .catch(function(e) {
                if (e) {
                  self.$message.success('Error..')
                }
              })
          }
        })
    },
    setReading(value, key) {
      let self = this
      this.selectedSetValue = null
      self.$dialog
        .confirm({
          title: 'SET VALUE',
          message: 'Are you sure you want to ' + this.BOOLEAN_LABEL[value],
          rbDanger: true,
          rbLabel: 'SET',
        })
        .then(function(val) {
          if (val) {
            let meta =
              self.widget.dataOptions.metaJson &&
              self.widget.dataOptions.metaJson
                ? JSON.parse(self.widget.dataOptions.metaJson)
                : null
            let assetId = meta.expressions
              ? Number(meta.expressions[0].criteria.conditions['1'].value)
              : -1
            let fieldId = meta.expressions
              ? meta.expressions.find(rt => rt.name === key).fieldId
              : -1
            this.$http
              .post('/v2/reading/set', {
                assetId: assetId,
                fieldId: fieldId,
                value: self.tools[key].value,
              })
              .then(response => {})
              .catch(function(e) {
                if (e) {
                  self.$message.success('Error..')
                }
              })
          } else {
            self.tools[key].value = !self.tools[key].value
          }
        })
    },
    setFieldValue() {
      if (this.newReadingField.value === '') {
        this.$message.error('Please enter a value')
      }
      let self = this
      this.$http
        .post('/v2/reading/set', {
          assetId: this.card,
          fieldId: this.newReadingField.field.fieldId,
          value: this.newReadingField.value.toString(),
        })
        .then(response => {
          if (
            !this.newReadingField.readingType === 2 &&
            !this.$helpers.isLicenseEnabled('CONTROL_ACTIONS')
          ) {
            this.newReadingField = null
          }
          if (response.data.responseCode === 0) {
            this.$message.success('Reading set successfully')
          } else {
            this.$message.error(response.data.message)
          }
        })
        .catch(function(e) {
          if (e) {
            self.$message.success('Error..')
          }
        })
      self.cancelReadingSetDialog()
    },
    cancelReadingSetDialog() {
      this.readingSetDialog = false
    },
    getCardData(data) {
      this.setParams()
      if (data && data.cardResult && data.cardResult.result) {
        Object.keys(this.tools).forEach(rt => {
          if (this.tools[rt].inputType !== 'TEXT') {
            this.tools[rt].value = data.cardResult.result.hasOwnProperty(rt)
              ? data.cardResult.result[rt]
              : null
          }
          if (this.tools[rt].inputType === 'TEXT') {
            this.tools[rt].value =
              this.metaData && this.metaData.metaJson
                ? this.metaData.metaJson[rt]
                : ''
          }
        })
        Object.keys(this.tools).forEach(rt => {
          this.tools[rt].value = data.cardResult.result.hasOwnProperty(rt)
            ? data.cardResult.result[rt]
            : null
          if (
            this.tools[rt].Rvalue === 'BOOLEAN' &&
            this.tools[rt].value === 'true'
          ) {
            this.tools[rt].value = true
          } else if (
            this.tools[rt].Rvalue === 'BOOLEAN' &&
            this.tools[rt].value === 'false'
          ) {
            this.tools[rt].value = false
          }
        })
      }
      this.loading = false
    },
    setParams(loading) {
      if (this.widget.dataOptions.readingComboCard) {
        this.widget.dataOptions.metaJson = JSON.stringify(
          this.widget.dataOptions.readingComboCard
        )
        this.metaData = this.widget.dataOptions.readingComboCard
      } else if (this.widget.id > -1 && this.widget.dataOptions.metaJson) {
        this.metaData = JSON.parse(this.widget.dataOptions.metaJson)
      }
      if (loading) {
        this.loading = false
      }
    },
  },
}
</script>

<style>
.card-section {
  padding: 10px;
  text-align: center;
  align-items: center;
}
.status-spin {
  animation-name: spin;
  animation-duration: 1000ms;
  animation-iteration-count: infinite;
  animation-timing-function: linear;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
.card-section:nth-child(odd) {
  background: #f7f7f7;
}
.card-section.header {
  font-size: 11px;
  padding-top: 10px;
  padding-bottom: 10px;
  background: #816bc5 !important;
  color: #fff;
  letter-spacing: 0.6px;
  font-weight: 500;
  text-transform: uppercase;
  text-align: center;
  justify-content: center;
}
.card-section.single .col-6 {
  padding-left: 10px;
  text-align: left;
}
.card-section.single .col-6:first-child {
  text-align: center;
}
.card-layout.AHU .card-section {
  padding: 10px;
  /* height: 16.66%; */
}
.card-layout.LIGHT .card-section,
.card-layout.HVAC3 .card-section {
  padding: 10px;
  /* height: 25%; */
}
.card-layout.PUMP .card-section {
  padding: 10px;
  /* height: 33.33%; */
}
.card-layout.WATER_TANK .card-section {
  /* height: 33.33%; */
  padding: 10px;
}
.card-layout.HVAC .card-section {
  height: 20%;
  padding: 10px;
}
.card-layout.HVAC4 .card-section {
  /* height: 16.6%; */
  padding: 10px;
}
.level-section {
  width: 25px;
  height: 25px;
  background: #e3e1e1;
  border-radius: 33px;
  margin: 3%;
  max-width: 23px !important;
  padding-top: 5px;
  font-size: 12px;
  font-weight: bold;
}
.level-section.L.active {
  background: #00a8dd !important;
  color: #fff;
}
.level-section.M.active {
  background: #00bf9b !important;
}
.level-section.H.active {
  background: #fcbb1c !important;
  color: #fff;
}
.level-section.C.active {
  background: #ff5f66 !important;
  color: #fff;
}
.asset-set-dialog .set-footer .col-6 {
  border-radius: 0;
  padding-top: 15px;
  padding-bottom: 15px;
}
.asset-set-dialog .set-footer {
  width: 100%;
  display: inline-flex;
}
.asset-set-dialog .set {
  margin-left: 0px !important;
}
.asset-set-dialog .el-dialog__footer {
  padding: 0px !important;
}
.WATER_TANK .card-section.row.light {
  -ms-flex-align: center;
  align-items: center;
  -ms-flex-pack: center;
  justify-content: center;
  font-size: 15px;
  font-size: 14px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #000000;
}
.autoStatus {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #7c7a7f;
}
.asset-card-body,
.card-container,
.card-layout {
  height: 100%;
}
.asset-card-layout {
  height: 100%;
}
.level-button {
  padding-top: 3px !important;
  padding-left: 13px !important;
}
.HVAC .displayName,
.HVAC4 .displayName {
  font-size: 13px;
  font-weight: normal;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.4px;
  color: #3e454a;
  text-align: left;
}
.HVAC .temperature,
.HVAC4 .temperature {
  font-size: 13px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.3px;
  text-align: right;
  display: inline;
}
.on-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #15a7a6;
}
.off-value {
  font-size: 12px;
  font-weight: 600;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.7px;
  text-align: right;
  color: #e41f1f;
}
.HVAC .cp-icon,
.HVAC4 .cp-icon {
  text-align: left;
}
.temperature {
  display: inline-flex;
}
.shine {
  -webkit-animation-duration: 1s;
  -webkit-animation-fill-mode: forwards;
  -webkit-animation-iteration-count: infinite;
  -webkit-animation-name: placeHolderShimmer;
  -webkit-animation-timing-function: linear;
  background: #f6f7f9;
  background-image: linear-gradient(
    to right,
    #f6f7f9 0%,
    #e9ebee 20%,
    #f6f7f9 40%,
    #f6f7f9 100%
  );
  background-repeat: no-repeat;
  background-size: 800px 100%;
  height: calc(100% - 20px);
  position: absolute;
  width: calc(100% - 20px);
}
@keyframes placeHolderShimmer {
  0% {
    background-position: -468px 0;
  }
  100% {
    background-position: 468px 0;
  }
}

@-webkit-keyframes placeholderShimmer {
  0% {
    background-position: -468px 0;
  }

  100% {
    background-position: 468px 0;
  }
}

.shimmer-frame {
  padding: 10px;
  position: absolute;
  width: 100%;
  height: 100%;
}
</style>
