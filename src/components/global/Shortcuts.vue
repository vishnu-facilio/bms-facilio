<template>
  <el-dialog
    :visible.sync="showShortcut"
    :append-to-body="true"
    :show-close="false"
    class="shortcut-dialog"
    :lock-scroll="true"
    width="40%"
    top="8vh"
    :title="$t('panel.panel.shortcuts')"
  >
    <el-table
      ref="table"
      :data="shortcutsArray"
      :border="true"
      style="width: 100%"
      :header-cell-style="{ 'background-color': '#f4f4f4' }"
    >
      <el-table-column label="ACTION">
        <template v-slot="shortcut">
          <div class="mL20">{{ shortcut.row.element }}</div>
        </template>
      </el-table-column>
      <el-table-column label="SHORTCUT">
        <template v-slot="shortcut">
          <div class="mL30 row">
            <div class="shortcut-keys col-4">
              {{ shortcut.row.shortcut_first_key }}
            </div>
            <div class="shortcut-keys col-1">
              {{ shortcut.row.shortcut_second_key }}
            </div>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>
<script>
import { isWebTabsEnabled } from '@facilio/router'
import { eventBus } from '@/page/widget/utils/eventBus'

const shortcutsArray = [
  {
    element: 'Open QuickSwitch',
    shortcut_second_key: 'k',
  },
  {
    element: 'Focus to tab group',
    shortcut_first_key: 'Shift',
    shortcut_second_key: 'g',
  },
  {
    element: 'Focus to tab',
    shortcut_first_key: 'Shift',
    shortcut_second_key: 't',
  },
  {
    element: 'Go to next page',
    shortcut_first_key: 'Shift',
    shortcut_second_key: '→',
  },
  {
    element: 'Go to previous page',
    shortcut_first_key: 'Shift',
    shortcut_second_key: '←',
  },
  {
    element: 'Open advanced filters',
    shortcut_first_key: 'Shift',
    shortcut_second_key: '/',
  },
  {
    element: 'Apply selected criteria in advanced filter',
    shortcut_second_key: '↵',
  },
  {
    element: 'Create New',
    shortcut_first_key: 'Shift',
    shortcut_second_key: 'n',
  },
  {
    element: 'Keyboard shortcuts',
    shortcut_second_key: '?',
  },
]

export default {
  data() {
    return {
      showShortcut: false,
      shortcutsArray,
    }
  },
  created() {
    if (isWebTabsEnabled()) {
      document.addEventListener('keydown', this.keyDownHandler)
    }
    this.windowsAndMacShortcuts
  },
  mounted() {
    eventBus.$on('openShortcutPanel', this.openShowcut)
  },
  beforeDestroy() {
    if (isWebTabsEnabled()) {
      document.removeEventListener('keydown', this.keyDownHandler)
    }
    eventBus.$off('openShortcutPanel', this.openShowcut)
  },
  computed: {
    windowsAndMacShortcuts() {
      let { userAgentData, platform } = navigator
      let { platform: userPlatform } = userAgentData || {}
      let currentOS = userPlatform || platform
      let isMac = currentOS.toUpperCase().indexOf('MAC') >= 0

      shortcutsArray.filter(shortcut => {
        if (shortcut.element === 'Open QuickSwitch') {
          if (isMac) {
            shortcut.shortcut_first_key = 'Cmd'
          } else {
            shortcut.shortcut_first_key = 'Shift'
          }
        } else if (
          shortcut.element === 'Keyboard shortcuts' ||
          shortcut.element === 'Apply selected criteria in advanced filter'
        ) {
          if (isMac) {
            shortcut.shortcut_first_key = 'Cmd'
          } else {
            shortcut.shortcut_first_key = 'Ctrl'
          }
        }
      })
    },
  },
  methods: {
    openShowcut() {
      this.showShortcut = true
    },
    keyDownHandler(e) {
      let { userAgentData, platform } = navigator
      let { platform: userPlatform } = userAgentData || {}
      let currentOS = userPlatform || platform
      let isMac = currentOS.toUpperCase().indexOf('MAC') >= 0

      if (
        ((isMac && e.metaKey) || (!isMac && e.ctrlKey)) &&
        e.code === 'Slash'
      ) {
        this.openShowcut()
      }
    },
  },
}
</script>
<style lang="scss">
.shortcut-dialog {
  .el-dialog__body {
    padding: 10px 25px 30px 20px;
  }
  .shortcut-keys {
    background-color: #f4f4f4;
    border: 1px solid #ccc;
    padding: 2px;
    display: flex;
    justify-content: center;
    min-width: 30px;
    border-radius: 2px;
    margin-left: 10px;
  }
}
</style>
