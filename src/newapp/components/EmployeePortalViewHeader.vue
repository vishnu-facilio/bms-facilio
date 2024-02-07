<template>
  <ul
    class="subheader-tabs pull-left"
    :class="{
      'has-more-views':
        !$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly,
    }"
  >
    <div class="flex-middle">
      <div class="fL fc-subheader-left d-flex">
        <inline-svg
          src="svgs/hamburger-menu"
          class="d-flex pointer hamburger-icon self-center"
          iconClass="icon icon-sm"
          @click.native="openViewsSidePanel"
        ></inline-svg>
        <div class="pR15 pointer self-center">
          <el-dropdown
            v-if="groupViews && groupViews.length > 1"
            class="fc-dropdown-menu pL10"
            @command="openGroup"
            trigger="click"
          >
            <span class="el-dropdown-link">
              {{ currentGroup ? currentGroup.displayName : '' }}
              <i
                v-if="!showCurrentViewOnly"
                class="el-icon-arrow-down el-icon--right"
              ></i>
            </span>
            <el-dropdown-menu
              slot="dropdown"
              v-if="groupViews && !showCurrentViewOnly"
            >
              <el-dropdown-item
                v-for="(group, key) in groupViews"
                :key="key"
                :command="group.displayName"
              >
                <template v-if="!$validation.isEmpty(group.views)">
                  {{ group.displayName }}
                </template>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div
          v-if="groupViews && groupViews.length > 1"
          class="fc-separator-lg mL10 mR10 self-center"
        ></div>
      </div>

      <template v-for="(item, index) in filteredViews.list">
        <li
          :key="item.name + index"
          class="self-center"
          :class="[currentView === item.name && 'active']"
          @click="goToView(item, currentGroup)"
        >
          <a>{{ item.label }}</a>
          <div v-if="!item.disable" class="emp-selection-bar"></div>
        </li>
      </template>

      <template
        v-if="!$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly"
      >
        <el-dropdown
          trigger="click"
          class="mL20 pointer more-views-dropdown"
          @command="goToView"
        >
          <span class="el-dropdown-link more-icon-container">
            <i class="el-icon-more"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item, index) in filteredViews.more"
              :key="item.name + index"
              :command="item"
            >
              {{ item.label }}
            </el-dropdown-item>
            <el-dropdown-item
              v-if="showRearrange"
              class="rearrange-option"
              :command="{ name: 'rearrange-views' }"
              >{{ $t('setup.users_management.rearrange') }}</el-dropdown-item
            >
          </el-dropdown-menu>
        </el-dropdown>
      </template>
    </div>

    <ViewCustomization
      :visible.sync="showReorderPanel"
      :reload="true"
      :menu="currentViews"
      :moduleName="moduleName ? moduleName : ''"
      @onchange="loadViews()"
    ></ViewCustomization>
    <NewView :pathPrefix="pathPrefix"></NewView>
  </ul>
</template>
<script>
import ViewHeader from 'src/newapp/components/ViewHeader.vue'
export default {
  extends: ViewHeader,
  methods: {
    openViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', !this.canShowViewsSidePanel)
    },
  },
}
</script>
