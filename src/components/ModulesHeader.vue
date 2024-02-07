<template>
  <ul
    ref="header"
    class="module-header"
    v-if="!$validation.isEmpty(modulesList.length)"
  >
    <router-link
      v-for="moduleList in activeFilterList"
      tag="li"
      :key="JSON.stringify(moduleList.path)"
      :to="moduleList.path"
      :title="moduleList.title"
    >
      <a
        class="uppercase"
        :ref="moduleList.label"
        role="navigation"
        :aria-label="moduleList.label"
      >
        <template v-if="overflowLabel(moduleList.label)">
          <el-tooltip
            popper-class="label-tooltip"
            effect="dark"
            :content="moduleList.label"
            placement="bottom-start"
            :tabindex="-1"
          >
            <span>{{ moduleList.label }}</span>
          </el-tooltip>
        </template>
        <template v-else>
          {{ moduleList.label }}
        </template>
      </a>
      <div class="module-selection-bar"></div>
    </router-link>
    <li v-if="canShowMoreMenu()" class="ellipsis-icon-hover">
      <el-dropdown
        @command="handleCommand"
        placement="bottom-start"
        trigger="click"
      >
        <span class="el-dropdown-link">
          <inline-svg src="svgs/header-more"></inline-svg>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-input
            v-model="searchText"
            class="modules-header-input"
            placeholder="Search"
          ></el-input>
          <el-dropdown-item
            v-for="moduleList in filteredMoreList"
            :key="moduleList.path.path"
            :command="moduleList"
          >
            <span>{{ moduleList.label }}</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </li>
  </ul>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import OtherMixin from '@/mixins/OtherMixin'

export default {
  mixins: [OtherMixin],
  props: {
    modulesList: {
      type: Array,
      required: true,
    },
    parentRef: {
      type: Object,
    },
  },
  data() {
    return {
      maxActiveModuleCount: 6,
      searchText: '',
      moreList: [],
      activeList: [],
      currentIndex: -1,
      menubarWidth: 0,
    }
  },

  computed: {
    ...mapState({ account: state => state.account }),

    path() {
      let { $route } = this
      let { path, name } = $route
      return { path, name }
    },
    currentTabIndex() {
      let { modulesList, $route } = this
      let { name: currentRouteName, path: currentRoutePath } = $route

      return modulesList.findIndex(tab => {
        let { path } = tab || {}
        let { path: tabRoutePath, name: tabRouteName } = path || {}

        return (
          tabRouteName === currentRouteName ||
          currentRoutePath.includes(tabRoutePath)
        )
      })
    },

    activeFilterList() {
      let { modulesList, activeList, currentIndex } = this
      let maxActiveModuleCount = activeList.length
      modulesList = this.getAltayerFilteredModulesList(modulesList)

      if (currentIndex < maxActiveModuleCount) {
        return modulesList.slice(0, maxActiveModuleCount)
      } else {
        let activeTabs = modulesList.slice(0, maxActiveModuleCount - 1)
        let currentTab = modulesList[currentIndex]

        activeTabs.push(currentTab)
        return activeTabs
      }
    },

    filteredMoreList() {
      let {
        modulesList,

        searchText,
        currentIndex,
        activeList,
      } = this
      let maxActiveModuleCount = activeList.length
      modulesList = this.getAltayerFilteredModulesList(modulesList)
      let moreModulesList = [...modulesList]

      if (currentIndex < maxActiveModuleCount) {
        moreModulesList = moreModulesList.slice(maxActiveModuleCount)
      } else {
        moreModulesList.splice(currentIndex, 1)
        moreModulesList = moreModulesList.slice(maxActiveModuleCount - 1)
      }

      if (!isEmpty(searchText)) {
        const text = searchText.replace(/\s/g, '').toLowerCase()
        return moreModulesList.filter(module => {
          let moduleLabel = ''
          if (!isEmpty((module || {}).label)) {
            moduleLabel = module.label.replace(/\s/g, '').toLowerCase()
          }
          return moduleLabel.includes(text)
        })
      }
      return moreModulesList
    },
  },
  watch: {
    modulesList: {
      handler() {
        this.resize()
      },
      immediate: true,
    },
    path: {
      handler(newVal) {
        this.setCurrentIndex(newVal)
      },
      immediate: true,
    },
  },

  mounted() {
    this.$nextTick(() => {
      let { path, name } = this.$route
      this.setCurrentIndex({ path, name })
    })
    let resizeObserver = new ResizeObserver(this.resize)
    if (this.$refs['header']) {
      resizeObserver.observe(this.$refs['header'])
    }
    if (this.parentRef['menubar']?.$el) {
      resizeObserver.observe(this.parentRef['menubar'].$el)
    }
  },

  methods: {
    setCurrentIndex(obj) {
      let { path: currentRoutePath, name: currentRouteName } = obj
      let index = this.modulesList.findIndex(tab => {
        let { path } = tab || {}
        let { path: tabRoutePath, name: tabRouteName } = path || {}

        return (
          currentRoutePath.includes(tabRoutePath) ||
          (!isEmpty(tabRouteName) ? tabRouteName === currentRouteName : false)
        )
      })
      this.currentIndex = index
    },
    resize() {
      requestAnimationFrame(() => {
        let { modulesList } = this
        if (modulesList.length > 0) {
          this.setCurrentIndex(this.path)
        }
        this.menubarWidth = this.parentRef['menubar']?.$el.clientWidth ?? 150
        let arr = []
        let font = '13px Arial'
        if (isEmpty(this.$refs['header'])) return
        let clientWidth =
          this.$refs['header'].clientWidth - this.menubarWidth - 20
        for (let list of modulesList) {
          let canvas = document.createElement('canvas')
          let context = canvas.getContext('2d')
          context.font = font
          let width = context.measureText(list.label).width + 56
          let formattedWidth = Math.ceil(width)
          arr.push(formattedWidth)
        }

        let summedArr = []
        for (let i = 0; i < arr.length; i++) {
          if (i === 0) {
            summedArr[i] = arr[i]
          } else {
            summedArr[i] = summedArr[i - 1] + (arr[i] >= 230 ? 230 : arr[i])
          }
        }
        let popupArray = []
        for (let i = 0; i < summedArr.length; i++) {
          if (summedArr[i] + 66 >= clientWidth) {
            popupArray.push(i)
          }
        }

        if (popupArray.length === this.modulesList.length) return
        let dropDownlist = []
        let cutoffIndex = popupArray[0]
        for (let i of popupArray) {
          let link = modulesList[i]
          dropDownlist.push(link)
        }
        this.moreList = dropDownlist
        this.activeList = modulesList.slice(0, cutoffIndex)
      })
    },
    overflowLabel(label) {
      let font = '13px Arial'
      let canvas = document.createElement('canvas')
      let context = canvas.getContext('2d')
      context.font = font
      let width = context.measureText(label + ', ').width
      let formattedWidth = Math.ceil(width)

      return formattedWidth >= 80 ? true : false
    },
    handleCommand(selectedModule) {
      let { path } = selectedModule || {}
      !isEmpty(path) && this.$router.push(path).catch(() => {})
    },
    canShowMoreMenu() {
      return this.modulesList.length > 0 && this.moreList.length > 0
    },
  },
}
</script>
<style lang="scss">
.module-header {
  display: flex;
  margin: 0;
  padding-left: 25px;
  li {
    display: block;
    position: relative;
    border: 1px solid transparent;
    &:hover,
    &.active,
    &:has(a:focus) {
      background: #24243e;
      a {
        color: white;
      }
    }
    a {
      display: block;
      color: #90909a;
      padding: 16px;
      max-width: 200px;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      font-size: 13px;
    }
    &:has(a:focus) {
      border-left: 1px solid #39b2c2;
      border-right: 1px solid #39b2c2;
    }

    .module-selection-bar {
      position: absolute;
      bottom: 10px;
      background: #39b2c2;
      width: 25px;
      position: absolute;
      margin-left: 17px;
    }
    &.active .module-selection-bar {
      border: 1px solid;
      border-right: 0px solid #e0e0e0;
      border-left: 0px solid #e0e0e0;
      border-color: #39b2c2;
    }
  }
  .ellipsis-icon-hover {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 8px;
    padding: 0 12px;
    cursor: pointer;
  }
}
.module-header + .header-tabs {
  top: 0;
  position: absolute;
  right: 12px;
}
.modules-header-input.el-input {
  padding: 0px 20px;
  border-bottom: 1px solid #d8dce5;

  .el-input__inner {
    border-bottom: 0px;
  }
}

.more-icon-hover:hover {
  padding: 12px;
  background-color: #25243e;
  margin: -12px 0px 0px -12px;
}
.more-icon-hover:focus {
  padding: 12px;
  background-color: #25243e;
  margin: -12px 0px 0px -12px;
}
</style>
