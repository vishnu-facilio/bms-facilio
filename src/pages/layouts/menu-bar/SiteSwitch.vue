<template>
  <div v-if="hasSwitch" class="flex items-center">
    <el-dropdown
      @command="setSwitchValue"
      @click="setFocus"
      trigger="click"
      class="siteDropdown"
      :tabindex="-1"
    >
      <FContainer
        hover:backgroundColor="neutralBg10"
        class="flex items-center pointer"
        padding="0.5rem 0.75rem"
        border-radius="6px"
      >
        <fc-icon
          group="setup"
          name="building-portfolio"
          class="mR5"
          size="18"
        ></fc-icon>
        <FText>{{ getSelectedOptionValue }}</FText>
        <fc-icon
          group="dsm"
          name="chevron-down"
          class="mL14"
          size="14"
        ></fc-icon>
      </FContainer>
      <el-dropdown-menu slot="dropdown" class="p5">
        <el-input
          @input="loadFilteredOptions"
          prefix-icon="el-icon-search"
          class="site_switch_search_box  p5"
          v-model="searchText"
          ref="site-switch-search-box"
          :placeholder="$t('common._common.search')"
        ></el-input>
        <el-dropdown-item
          v-for="(option, optionKey) in moduleOptionList"
          :key="`option-${optionKey}`"
          class="dropdown-item"
          :command="optionKey"
          >{{ option.label }}
          <span v-if="optionKey === `${selectedId}`" class="success-icon pL8"
            ><i class="el-icon-success"></i
          ></span>
          <span
            v-tippy
            :title="$t('setup.decommission.decommissioned')"
            v-if="getWarningVisible(option)"
            ><fc-icon
              group="alert"
              class="fR pT10 pL20"
              name="decommissioning"
              size="16"
            ></fc-icon
          ></span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>
<script>
import { FText, FContainer } from '@facilio/design-system'
import SwitchValue from 'src/components/home/SwitchValue.vue'
export default {
  extends: SwitchValue,
  name: 'SiteSwitch',
  components: { FText, FContainer },
}
</script>
