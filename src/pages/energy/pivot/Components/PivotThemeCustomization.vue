<template>
  <el-popover placement="bottom-start" v-model="visible">
    <div class="pivot-theme-customization" >
      <div class="theme-text-color text-left line-height25 mB10">
        {{ $t('pivot.fontSize') }}
      </div>
      <el-radio
        v-model="themeConfig.fontSize"
        label="small"
        class="theme-text-color"
      >
        <span class="theme-text-color">{{ $t('pivot.small') }}</span>
      </el-radio>
      <el-radio
        v-model="themeConfig.fontSize"
        label="medium"
        class="theme-text-color"
      >
        <span class="theme-text-color">{{ $t('pivot.medium') }}</span>
      </el-radio>
      <el-radio
        v-model="themeConfig.fontSize"
        label="large"
        class="theme-text-color"
      >
        <span class="theme-text-color">{{ $t('pivot.large') }}</span>
      </el-radio>

      <div class="theme-text-color text-left line-height25 mT10">
        {{ $t('pivot.themeColor') }}
      </div>
      <div class="theme-color-selection-container pB10">
        <div
          class="theme-color-box"
          v-for="theme in pivotThemeOptions"
          :key="'pivotTheme-' + theme.color"
          :style="{ 'background-color': theme.color }"
          :class="{ 'active-theme': theme.class === themeConfig.class }"
          @click="themeConfig.class = theme.class"
        ></div>
      </div>
      <div class="theme-text-color text-left line-height25 mT5">
        {{ $t('pivot.grid') }}
      </div>
      <el-select
        v-model="themeConfig.grid"
        filterable
        placeholder="Select"
        class="fc-input-full-border-select2 width100 mT10 theme-text-color"
      >
        <el-option :label="$t('pivot.both')" value="both" class="f13">
        </el-option>
        <el-option
          :label="$t('pivot.horizontal')"
          value="horizontal"
          class="f13"
        >
        </el-option>
        <el-option :label="$t('pivot.vertical')" value="vertical" class="f13">
        </el-option>
        <el-option :label="$t('pivot.none')" value="none" class="f13">
        </el-option>
      </el-select>
      <div class="theme-text-color text-left line-height25 mT10">
        <PivotRowHeightCustomization
          :initialRowHieght="initialRowHieght"
          @rowHeightChanged="$emit('rowHeightChanged', $event)"
        />
      </div>
      <div class="mT10">
        <div class="pB15">
          <span class="theme-text-color mR10">{{
            $t('pivot.showRowNumbers')
          }}</span
          ><el-switch
            v-model="themeConfig.number"
            style="margin-left:72px"
          ></el-switch>
        </div>
        <div>
          <span class="theme-text-color mR10 " style="padding-right:14px;">{{
            $t('pivot.stripes')
          }}</span
          ><el-switch
            v-model="themeConfig.stripe"
            style="margin-left:72px;"
          ></el-switch>
        </div>
      </div>
      <!-- <el-checkbox class="mT10" v-model="themeConfig.number" >
        <span class="theme-text-color">{{ $t('pivot.showRowNumbers') }}</span>
      </el-checkbox>
      <el-checkbox class="mT10" v-model="themeConfig.stripe">
        <span class="theme-text-color">{{ $t('pivot.stripes') }}</span>
      </el-checkbox> -->
    </div>
    <div slot="reference" class="theme-label-container pivot-tab-hover" style="margin-left:16px;">
      <inline-svg
        src="svgs/pivot/themes"
        class="vertical-middle"
        iconClass="icon icon-sm-md"
      ></inline-svg>
      <span
        class="theme-text-color"
        style="padding-left:4px;vertical-align: -1px;"
        >{{ $t('pivot.theme') }}</span
      >
    </div>
  </el-popover>
</template>

<script>
import { defaultTheme } from '../PivotDefaults'
import './../pivot.scss'
import PivotRowHeightCustomization from '../Components/PivotRowHeightCustomization.vue'

export default {
  props: ['initialTheme', 'initialRowHieght'],
  watch: {
    themeConfig: {
      handler: function(newVal) {
        this.$emit('themeConfigChanged', newVal)
      },
      deep: true,
    },
  },
  components: {
    PivotRowHeightCustomization,
  },
  mounted() {
    if (this.initialTheme) {
      this.themeConfig = JSON.parse(JSON.stringify(this.initialTheme))
    } else {
      this.themeConfig = defaultTheme
    }

    this.$delete(this.themeConfig, 'density')
  },
  data() {
    return {
      themeConfig: {},
      visible: false,
      pivotThemeOptions: [
        {
          class: 'default',
          color: '#f3f1fc',
        },
        {
          class: 'blue',
          color: '#1a77bc',
        },
        {
          class: 'teal',
          color: '#4b9789',
        },
        {
          class: 'purple',
          color: '#7f56c2',
        },
        {
          class: 'black-orange',
          color: '#fadfd3',
        },
        {
          class: 'bright-orange ',
          color: '#ec743c',
        },
        {
          class: 'yellow',
          color: '#f9c132',
        },
        {
          class: 'green',
          color: '#bef19c',
        },
        {
          class: 'mac-os',
          color: '#ebeaed',
        },
        {
          class: 'mid-night',
          color: '#222323',
        },
      ],
    }
  },
}
</script>

<style scoped lang="scss">
.theme-label-container {
  padding: 6px 10px 5px 10px;
  cursor: pointer;
  font-size: 13px;
}
.theme-text-color {
  color: #324056;
  font-size: 13px;
  letter-spacing: 0.5px;
}
.pivot-theme-customization {
  width: 490px;
  padding: 10px;
  font-size: 13px;
}
.theme-color-selection-container {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
}
.theme-color-box {
  height: 20px;
  width: 20px;
  border-radius: 3px;
  cursor: pointer;
}
.theme-color-selection-container {
  display: flex;
  align-items: center;
}
.active-theme {
  border: 3px solid #409eff;
  height: 25px;
  width: 25px;
  border-radius: 3px;
}
.f13 {
  font-size: 13px;
}
</style>
