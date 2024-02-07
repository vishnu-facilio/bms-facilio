<template>
  <div class="flex-middle mT17">
    <div class="flex-middle flex-direction-row">
      <a
        v-for="(view, index) in filteredViews.list"
        :key="view.name + index"
        @click="goToView(view, currentGroup)"
        :class="[
          view.name === currentView && 'router-link-exact-active active',
        ]"
      >
        <div class="label-txt-black pointer mR30">
          {{ view.label }}
          <div class="portal-active-tab"></div>
        </div>
      </a>
    </div>

    <el-dropdown
      v-if="!$validation.isEmpty(filteredViews.more) && !showCurrentViewOnly"
      trigger="click"
      class="mT3 pointer more-views-dropdown"
      @command="goToView"
    >
      <span class="el-dropdown-link">
        <inline-svg src="svgs/header-more"></inline-svg>
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="(item, index) in filteredViews.more"
          :key="item.name + index"
          :command="item"
        >
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>
<script>
import ViewHeader from 'pages/approvals/components/ApprovalHeader'
export default {
  extends: ViewHeader,
}
</script>
