<template>
  <div>
    <div class="fc-editor-right-side-scroll">
      <div class="fc-black3-16 pB20 bold pL20 pR20 pT10 bold pT10">
        Essentials
      </div>
      <div class="fc-scroll-shortcut-area">
        <el-table
          :data="essentialsData"
          style="width: 100%"
          class="fc-keyboard-shortcut-table"
        >
          <el-table-column prop="command" label="command"> </el-table-column>
          <el-table-column prop="windows" label="windows">
            <template v-slot="windows">
              <div class="fc-shortcut-bg">
                {{ windows.row.windows }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mac" label="mac">
            <template v-slot="mac">
              <div class="fc-shortcut-bg">
                {{ mac.row.mac }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="mT20">
        <div class="fc-black3-16 pB20 bold pL20 pR20 pT10 bold">
          Text Formatting
        </div>
        <el-table
          :data="textFormattingData"
          style="width: 100%"
          class="fc-keyboard-shortcut-table"
        >
          <el-table-column prop="command" label="command"> </el-table-column>
          <el-table-column prop="windows" label="windows">
            <template v-slot="windows">
              <div class="fc-shortcut-bg">
                {{ windows.row.windows }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mac" label="mac">
            <template v-slot="mac">
              <div class="fc-shortcut-bg">
                {{ mac.row.mac }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="mT20">
        <div class="fc-black3-16 pB20 bold pL20 pR20 pT10 bold">
          Paragraph Formatting
        </div>
        <el-table
          :data="paragraphFormatting"
          style="width: 100%"
          class="fc-keyboard-shortcut-table"
        >
          <el-table-column prop="command" label="command"> </el-table-column>
          <el-table-column prop="windows" label="windows">
            <template v-slot="windows">
              <div class="fc-shortcut-bg">
                {{ windows.row.windows }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mac" label="mac">
            <template v-slot="mac">
              <div class="fc-shortcut-bg">
                {{ mac.row.mac }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="mT20 pB100">
        <div class="fc-black3-16 pB20 bold pL20 pR20 pT10 bold">
          Text Selection
        </div>
        <el-table
          :data="textSelection"
          style="width: 100%"
          class="fc-keyboard-shortcut-table"
        >
          <el-table-column prop="command" label="command"> </el-table-column>
          <el-table-column prop="windows" label="windows">
            <template v-slot="windows">
              <div class="fc-shortcut-bg">
                {{ windows.row.windows }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="mac" label="mac">
            <template v-slot="mac">
              <div class="fc-shortcut-bg">
                {{ mac.row.mac }}
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      essentialsData: [
        {
          command: 'Copy',
          windows: 'Control C',
          mac: 'Cmd C',
        },
        {
          command: 'Cut',
          windows: 'Control X',
          mac: 'Cmd X',
        },
        {
          command: 'Paste',
          windows: 'Control V',
          mac: 'Cmd V',
        },
        {
          command: 'Paste without formatting',
          windows: 'Control Shift V',
          mac: 'Cmd Shift V',
        },
        {
          command: 'Paste without formatting',
          windows: 'Control Shift V',
          mac: 'Cmd Shift V',
        },
        {
          command: 'Undo',
          windows: 'Control Z',
          mac: 'Cmd Z',
        },
        {
          command: 'Redo',
          windows: 'Control Shift Z',
          mac: 'Cmd Shift Z',
        },
        {
          command: 'Add a line break',
          windows: 'Shift Enter',
          mac: 'Shift Enter',
        },
      ],
      textFormattingData: [
        {
          command: 'Bold',
          windows: 'Control + B	',
          mac: 'Cmd + B',
        },
        {
          command: 'Italicize',
          windows: 'Control + I',
          mac: 'Cmd + I',
        },
        {
          command: 'Underline	',
          windows: 'Control + U',
          mac: 'Cmd + U',
        },
        {
          command: 'Strikethrough',
          windows: 'Control + Shift + X',
          mac: 'Cmd + Shift + X',
        },
        {
          command: 'Highlight',
          windows: 'Control + Shift + H',
          mac: 'Cmd + Shift + H',
        },
        {
          command: 'Code',
          windows: 'Control + E',
          mac: 'Cmd + E',
        },
      ],
      paragraphFormatting: [
        {
          command: 'Apply normal text style',
          windows: 'Control + Alt + 0',
          mac: 'Cmd + Alt + 0',
        },
        {
          command: 'Apply heading style 1	',
          windows: 'Control + Alt + 1',
          mac: 'Cmd + Alt + 1',
        },
        {
          command: 'Apply heading style 2	',
          windows: 'Control + Alt + 2',
          mac: 'Cmd + Alt + 2',
        },
        {
          command: 'Apply heading style 3',
          windows: 'Control + Alt + 3',
          mac: 'Cmd + Alt + 3',
        },
        {
          command: 'Apply heading style 4',
          windows: 'Control + Alt + 4',
          mac: 'Cmd + Alt + 4',
        },
        {
          command: 'Apply heading style 5',
          windows: 'Control + Alt + 5',
          mac: 'Cmd + Alt + 5',
        },
        {
          command: 'Apply heading style 6',
          windows: 'Control + Alt + 6',
          mac: 'Cmd + Alt + 6',
        },
        {
          command: 'Ordered list',
          windows: 'Control + Shift + 7',
          mac: 'Cmd + Alt + 7',
        },
        {
          command: 'Bullet list',
          windows: 'Control + Shift + 8',
          mac: 'Cmd + Shift + 8',
        },
        {
          command: 'Task list',
          windows: 'Control + Shift + 9',
          mac: 'Cmd + Shift + 9',
        },
        {
          command: 'Blockquote',
          windows: 'Control + Shift + B',
          mac: 'Cmd + Shift + B',
        },
        {
          command: 'Left align	',
          windows: 'Control + Shift + L',
          mac: 'Cmd + Shift + L',
        },
        {
          command: 'Center align',
          windows: 'Control + Shift + E',
          mac: 'Cmd + Shift + E',
        },
        {
          command: 'Right align',
          windows: 'Control + Shift + R',
          mac: 'Cmd + Shift + R',
        },
        {
          command: 'Justify',
          windows: 'Control + Shift + J',
          mac: 'Cmd + Shift + J',
        },
        {
          command: 'Code block',
          windows: 'Control + Alt + C',
          mac: 'Cmd + Alt + C',
        },
        {
          command: 'Subscript',
          windows: 'Control + ',
          mac: 'Cmd + ,',
        },
        {
          command: 'Superscript',
          windows: 'Control + .',
          mac: 'Cmd + .',
        },
      ],
      textSelection: [
        {
          command: 'Select all',
          windows: 'Control + A',
          mac: 'Cmd + A',
        },
        {
          command: 'Extend selection one character to left',
          windows: 'Shift + left arrow',
          mac: 'Shift + left arrow',
        },
        {
          command: 'Extend selection one character to right',
          windows: 'Shift + right arrow',
          mac: 'Shift + right arrow',
        },
        {
          command: 'Extend selection one line up',
          windows: 'Shift + up arrow',
          mac: 'Shift + up arrow',
        },
        {
          command: 'Extend selection one line down',
          windows: 'Shift + Down arrow',
          mac: 'Shift + Down arrow',
        },
        {
          command: 'Extend selection to the beginning of the document',
          windows: 'Control + Shift + up arrow',
          mac: 'Control + Shift + up arrow',
        },
        {
          command: 'Extend selection to the end of the document	',
          windows: 'Control + Shift + down arrow',
          mac: 'Control + Shift + down arrow',
        },
      ],
    }
  },
}
</script>

<style lang="scss">
.fc-keyboard-shortcut-table {
  th.is-leaf {
    background: #f8ffff;
  }
  thead {
    th {
      padding-left: 10px !important;
      padding-right: 10px !important;
      font-weight: 500;
      background: #f8ffff;
      .cell {
        font-size: 13px;
        line-height: 20px;
        text-transform: capitalize;
        padding-left: 10px !important;
        padding-right: 10px !important;
        font-weight: 500;
      }
    }
  }
  tbody {
    .cell {
      font-size: 13px;
      line-height: 20px;
      padding-left: 20px !important;
      padding-right: 20px !important;
    }
    td:first-child {
      font-weight: 500;
    }
  }
  .fc-shortcut-bg {
    font-size: 13px;
  }
  .el-table__body tr:hover > td.el-table__cell {
    background: #f8ffff;
  }
}
.fc-editor-shortcut-dialog {
  .el-dialog {
    margin-top: 3vh !important;
  }
}
.fc-editor-right-side-scroll {
  height: calc(100vh - 200px);
  padding-bottom: 50px;
  overflow-y: scroll;
}
</style>
