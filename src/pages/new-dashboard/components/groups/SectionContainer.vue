<template>
  <div>
    <el-row
      :style="accordionHeadStyle"
      class="accordion-head"
      :class="{ 'cursor-move': viewOrEdit == 'edit' }"
    >
      <el-col :span="1" class="mT32">
        <div
          @click="resizeSection"
          :class="{ mL30: headerType == 'banner', mL25: headerType == 'color' }"
        >
          <inline-svg
            class="pointer"
            :iconClass="
              `arrow-icon-border ${
                collapsed ? 'accordion-expanded' : 'accordion-collapsed'
              }`
            "
            iconStyle="width: 30px; height: 30px; style"
            src="svgs/dashboard/icons/arrow"
          >
          </inline-svg>
        </div>
      </el-col>
      <el-col :span="19" class="mT27">
        <div class="mL15">
          <div class="title">{{ section.name }}</div>
          <div class="widget-count">{{ widgetCountText }}</div>
        </div>
      </el-col>
      <el-col :span="3" class="mT25" v-if="viewOrEdit == 'edit'">
        <div class="row">
          <div class="col" @click="expandSection">
            <el-dropdown
              @command="addWidget"
              placement="bottom"
              trigger="click"
            >
              <el-button circle class="section-icon">
                <inline-svg
                  :iconStyle="controlsIconStyle"
                  src="svgs/dashboard/icons/add"
                ></inline-svg>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="addText">Add Text</el-dropdown-item>
                <el-dropdown-item command="addImage"
                  >Add Image</el-dropdown-item
                >
                <el-dropdown-item command="addComponent"
                  >Add Component</el-dropdown-item
                >
                <el-dropdown-item command="addCard">Add Card</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="col">
            <el-button @click="editSection" circle class="section-icon">
              <inline-svg
                :iconStyle="controlsIconStyle"
                src="svgs/dashboard/icons/config"
              ></inline-svg>
            </el-button>
          </div>
          <div class="col" @click="expandSection">
            <el-dropdown
              @command="executeOptions"
              placement="bottom"
              trigger="click"
            >
              <el-button circle class="section-icon">
                <inline-svg
                  :iconStyle="controlsIconStyle"
                  src="svgs/dashboard/icons/more"
                ></inline-svg>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <!-- <el-dropdown-item command="copyLink"
                  >Copy Link</el-dropdown-item
                > -->
                <el-dropdown-item command="clone"
                  ><i class="el-icon-document-copy"></i>Clone</el-dropdown-item
                >
                <el-dropdown-item command="delete"
                  ><i class="el-icon-delete"></i>Delete</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <!-- <el-col :span="6">
            <el-button @click="gotoDashboard" circle class="section-icon">
              <inline-svg
                :iconStyle="controlsIconStyle"
                src="svgs/dashboard/goto"
              ></inline-svg>
            </el-button>
          </el-col> -->
        </div>
      </el-col>
    </el-row>
    <div :style="sectionBodyStyle" class="section-body">
      <div
        v-if="widgetCount == 0 && viewOrEdit == 'edit' && !collapsed"
        class="add-widget-message"
        :class="{ 'add-widget-message-color': headerType == 'color' }"
      >
        There are no widgets in this group. click + to begin adding widgets.
      </div>
      <slot></slot>
    </div>
    <section-delete-confirmation
      v-if="showDeleteModal"
      :groupName="groupName"
      @yes="deleteSection"
      @no="showDeleteModal = false"
    />
  </div>
</template>

<script>
import InlineSvg from 'src/components/InlineSvg.vue'
import SectionDeleteConfirmation from 'src/pages/new-dashboard/components/groups/SectionDeleteConfirmation.vue'
export default {
  props: {
    section: {
      required: true,
      default: () => ({}),
      type: Object,
    },
    viewOrEdit: {
      type: String,
      required: true,
      default: 'view',
    },
  },
  methods: {
    gotoDashboard() {
      // Write the logic to handle link dashboard... etc.
    },
    executeOptions(option) {
      if (option == 'clone') {
        this.$emit('clone')
      } else if (option == 'delete') {
        this.showDeleteModal = true
      } else if (option == 'copyLink') {
        // Write the logic to copy link here.
      }
    },
    deleteSection() {
      this.showDeleteModal = false
      this.$emit('removeSection')
    },
    editSection() {
      const self = this
      if (!this.collapsed) {
        self.$emit('editSection')
      } else {
        setTimeout(() => {
          // Show the section-editer dialog box after some delay,
          // expand the section first.
          self.$emit('editSection')
        }, 500)
        self.expandSection()
      }
    },
    resizeSection() {
      const { collapsed } = this
      if (collapsed) {
        this.expandSection()
      } else {
        this.collapseSection()
      }
    },
    expandSection() {
      const { collapsed } = this
      if (!collapsed) {
        return
      }
      const { widgets, id } = this
      const SECTION_HEAD_HEIGHT = 7 // 7 x 15px (rowHeight) = 105px - 5px X 2 (Top and Bottom margin of Gridstack items) = 95px;
      const SECTION_BODY_HEIGHT = this.findSectionMaxHeight(widgets)
      const SECTION_BOTTOM_PADDING = 1
      this.$emit('resizeSection', {
        id,
        height:
          SECTION_HEAD_HEIGHT + SECTION_BODY_HEIGHT + SECTION_BOTTOM_PADDING,
        collapsed: false,
      })
    },
    collapseSection() {
      const { collapsed } = this
      if (collapsed) {
        return
      }
      const { id } = this
      const SECTION_HEAD_HEIGHT = 7 // 7 x 15px (rowHeight) = 105px - 5px X 2 (Top and Bottom margin of Gridstack items) = 95px;
      this.$emit('resizeSection', {
        id,
        height: SECTION_HEAD_HEIGHT,
        collapsed: true,
      })
    },
    addWidget(widgetType) {
      this.$emit('addWidgetToSection', widgetType)
    },
    findSectionMaxHeight(children) {
      const { length } = children
      if (length) {
        return Math.max(...children.map(o => o.y + o.h))
      } else {
        return 13
      }
    },
  },
  data() {
    return {
      showDeleteModal: false,
    }
  },
  computed: {
    groupName() {
      const {
        section: { name = '' },
      } = this
      return name
    },
    controlsIconStyle() {
      const {
        section: {
          banner_meta: { type: bannerType },
        },
      } = this
      if (bannerType == 'color') {
        return 'fill: #324056'
      } else if (bannerType == 'banner') {
        return 'fill: #ffffff'
      } else {
        return ''
      }
    },
    id() {
      const {
        section: { id },
      } = this
      return id
    },
    widgets() {
      const {
        section: { children },
      } = this
      return children
    },
    collapsed() {
      const {
        section: { collapsed },
      } = this
      return collapsed ?? false
    },
    widgetCount() {
      const {
        section: { children: widgets },
      } = this
      const { length: count } = widgets
      return count
    },
    widgetCountText() {
      const { widgetCount } = this
      if (widgetCount > 1) {
        return `${widgetCount} widgets`
      } else {
        return `${widgetCount} widget`
      }
    },
    sectionBodyStyle() {
      return {
        border: 'red',
      }
    },
    headerType() {
      let {
        section: {
          banner_meta: { type: headerType },
        },
      } = this
      return headerType
    },
    accordionHeadStyle() {
      let {
        headerType,
        section: { banner_meta: header },
      } = this
      let style = {}
      if (headerType == 'banner') {
        const {
          banner: { label },
        } = header
        style[
          'background-image'
        ] = `url(${require(`src/assets/svgs/dashboard/banners/${label}.svg`)})`
        style['background-size'] = 'cover'
      } else if (headerType == 'color') {
        const { color } = header
      }
      return style
    },
  },
  components: { InlineSvg, SectionDeleteConfirmation },
}
</script>
<style lang="scss" scoped>
.add-widget-message {
  position: absolute;
  top: 55%;
  left: 35%;
  font-size: 13px;
  margin-top: 7px;
  color: #68758b;
}
.add-widget-message-color {
  top: 44%;
}
.cursor-move {
  cursor: move;
}
.row {
  display: flex;
  justify-content: flex-end;
}
.widget-count {
  font-size: 12px;
  margin-top: 7px;
  color: #68758b;
}
.mT27 {
  margin-top: 27px;
}
.mT32 {
  margin-top: 32px;
}
.section-body {
  padding: 5px;
}
.title {
  font-size: 16px;
  font-weight: 500;
  font-stretch: normal;
  font-style: normal;
  line-height: normal;
  letter-spacing: 0.57px;
  color: #324056;
}
.accordion-head {
  height: 95px;
}
</style>

<style scoped lang="scss">
.el-button--default.section-icon {
  background: transparent;
  border: none;
}
.el-button--default.section-icon:hover {
  background: #e4e4e4;
}
</style>
<style lang="scss">
.el-button--default.section-icon {
  &:hover {
    .add-icon {
      fill: #324056;
    }
    .config-icon {
      fill: #324056;
    }
    .more-icon {
      fill: #324056;
    }
  }
}
.accordion-expanded {
  transform-origin: center;
  transform: rotate(90deg) scale(0.8);
  transition: 0.5s ease-in-out;
}
.accordion-collapsed {
  transform-origin: center;
  transform: rotate(-90deg) scale(0.8);
  transition: 0.5s ease-in-out;
}
.arrow-icon-border {
  border: 2px solid rgb(45, 40, 40);
  border-radius: 50%;
}
</style>
