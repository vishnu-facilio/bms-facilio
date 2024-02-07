<template>
  <div ref="graphicsContextMenu" class="pivot-header-dropdown">
    <el-dropdown trigger="click" @command="handleClickAction" :ref="prop">
      <div class="more-icon-header">
        <i class="el-icon-more" style="margin-left:6px;"></i>
      </div>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="(menu, index) in menuOptions"
          :key="index"
          @click="handleClickAction(menu)"
          :command="menu"
        >
          <div class="dropdown-menu-text">
            <div
              style="  padding-top: 2.5px;
  margin-right: 10px;
"
            >
              <InlineSvg
                :src="imgUrl[index]"
                iconClass="icon-sm flex icon"
              ></InlineSvg>
            </div>
            <span>
              {{ menu.isActive ? menu.activeLabel : menu.label }}
            </span>
          </div>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
export default {
  props: ['contextmenu', 'prop', 'visible', 'isPinned'],
  data() {
    return {
      iconsSet1: [
        'svgs/pivot/pin-notification',
        'svgs/pivot/sort',
        'svgs/pivot/auto-size-icon',
        'svgs/pivot/cli-text-icon',
        'svgs/pivot/Column',
        'svgs/pivot/paint-icon',
        'svgs/pivot/rename-icon',
      ],
      iconsSet2: [
        'svgs/pivot/pin-notification',
        'svgs/pivot/sort',
        'svgs/pivot/auto-size-icon',
        'svgs/pivot/cli-text-icon',
        'svgs/pivot/cell-visualixzation',
        'svgs/pivot/Column',
        'svgs/pivot/paint-icon',
        'svgs/pivot/rename-icon',
      ],
    }
  },
  computed: {
    menuOptions() {
      return this.contextmenu.menu
    },
    imgUrl() {
      if (this.menuOptions.length < 8) {
        return this.iconsSet1
      } else {
        return this.iconsSet2
      }
    },
  },
  methods: {
    handleClickAction(menu) {
      this.$emit('contextMenuSelectEvent', {
        menu: menu,
        prop: this.prop,
      })
    },
  },
}
</script>

<style scoped lang="scss">
.el-dropdown {
  font-size: 10px;
  width: 100%;
}

.dropdown-menu-text {
  display: flex;
  justify-content: flex-start;
}

.el-dropdown {
  vertical-align: top;
}
.el-dropdown + .el-dropdown {
  margin-left: 15px;
}
.el-icon-arrow-down {
  font-size: 12px;
}

.pivot-header-dropdown {
  .el-dropdown-menu__item {
    background-color: #ebeff4;
  }
}
.more-icon-header {
  border-radius: 50%;
  width: 22px;

  &:hover {
    background-color: #ebedf4 !important;
    color: black !important;
    cursor: pointer;
  }
}
</style>
