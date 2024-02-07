<template>
  <div class="view-header-wg">
    <div @click="toggleViewsSidePanel">
      <inline-svg
        :class="['hamburger-icon', canShowViewsSidePanel && 'active']"
        src="svgs/hamburger-menu"
        iconClass="icon icon-sm"
      ></inline-svg>
    </div>
    <div
      v-if="$validation.isEmpty(currentViewDisplayName)"
      class="view-name-loading loading-shimmer"
    ></div>
    <div v-else-if="!canShowViewsSidePanel && currentGroupViews.length > 1">
      <el-dropdown trigger="click" @command="goToView" placement="bottom-end"
        ><span class="el-dropdown-link view-name view-header-dropdown"
          >{{ currentViewDisplayName }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown" class="pB0">
          <div class="dropdown-custom-class-scroll">
            <div class="view-header-group-title text-uppercase flex">
              <fc-icon group="files" name="folder" color="#ff3184"></fc-icon>
              {{ currentGroup.displayName }}
            </div>
            <el-dropdown-item
              v-for="(group, key) in currentGroupViews"
              :key="`group.id-${key}`"
              :command="group"
              class="view-drop-item"
              :class="[
                currentViewDisplayName == group.label && 'selected-dropdown',
              ]"
            >
              {{ group.label }}</el-dropdown-item
            >
          </div>
          <el-dropdown-item>
            <div @click="toggleViewsSidePanel" class="view-all-btn">
              {{ $t('viewsmanager.list.show_all') }}
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <div v-else class="view-name cursor-auto pL5">
      {{ currentViewDisplayName }}
    </div>
  </div>
</template>
<script>
import ViewHeader from 'src/newapp/components/ViewHeader.vue'

export default {
  extends: ViewHeader,
  computed: {
    currentViewDisplayName() {
      let { currentViews, currentView = '' } = this
      let { label } =
        (currentViews || []).find(view => view.name === currentView) || {}

      return label || ''
    },

    currentGroupViews() {
      let { filteredViews } = this
      let { list, more } = filteredViews || {}
      let views = [...(list || []), ...(more || [])]
      return views || {}
    },
  },
  methods: {
    toggleViewsSidePanel() {
      this.$emit('update:canShowViewsSidePanel', !this.canShowViewsSidePanel)
    },
  },
}
</script>
<style lang="scss">
.view-header-wg {
  display: flex;
  align-items: center;
  cursor: pointer;
  .active {
    background-color: #ebedf4;
    border-radius: 15%;
  }

  .hamburger-icon {
    padding: 5px;
    cursor: pointer;
    display: flex;

    &:hover,
    &.active {
      background-color: #ebedf4;
      border-radius: 15%;
    }
  }
  .view-name {
    margin: 0 20px 0 5px;
    color: #2d2d52;
    text-transform: capitalize;
    font-weight: 500;
    font-size: 14px;
  }
  .view-name-loading {
    height: 20px;
    width: 100px;
    border-radius: 5px;
    margin: 0 20px 0 5px;
  }
}
</style>
<style lang="scss" scoped>
.view-header-group-title {
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 1px;
  color: #ff3184;
  padding: 0 15px;
  line-height: 36px;
}
.view-drop-item {
  text-transform: capitalize;
}
.view-header-dropdown {
  padding: 6px;
  &:hover {
    background-color: #f1f2f4;
    border-radius: 4px;
  }
}
.dropdown-custom-class-scroll {
  max-height: 250px;
  overflow-y: scroll;
}
.view-all-btn {
  height: 50px;
  align-items: center;
  color: #3ab2c1;
  border: none;
  font-weight: 500;
  display: flex;
  justify-content: center;
  border-top: 1px solid #f1f2f4;
}
.selected-dropdown {
  background-color: #f5f7fa;
  border-left: 2px solid #ff3184;
}
</style>
