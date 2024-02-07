<template>
  <div
    class="widget-wrapper"
    :class="[
      alwaysShowWidgetControls || isDropDownExpanded
        ? 'always-visible-widget-controls'
        : 'hover-visible-widget-controls',
      { 'widget-wrapper-border': borderAroundWidget },
    ]"
    v-observe-visibility="{
      callback: visibilityChanged,
      once: true,
    }"
  >
    <el-row
      v-if="showHeader"
      class="widget-header"
      :class="{ 'cursor-move': viewOrEdit == 'edit' }"
    >
      <el-col :span="20" class="title-input-container">
        <div
          v-if="titleInEditMode == false"
          @dblclick="editTitle"
          class="fc-widget-label ellipsis"
        >
          {{ title }}
        </div>
        <el-input
          v-if="viewOrEdit == 'edit' && titleInEditMode == true"
          v-model="title"
          :autofocus="true"
          class="widget-wrapper-input ellipsis"
          placeholder="Widget title"
          @blur="updateTitle"
        ></el-input>
      </el-col>
      <el-col :span="4" class="widget-controls-container">
        <div class="widget-controls mT1 row">
          <div v-if="showDatePickerInHeader" class="date-picker-portal">
            <portal-target :name="'widget-datepicker' + id"></portal-target>
          </div>
          <div v-if="showHelpText">
            <el-popover
              v-model="isDropDownExpanded"
              :popper-class="'widget-info-popper'"
              placement="bottom"
              trigger="click"
              class="widget-info-container mR12"
              :class="{ 'show-info-icon': isDropDownExpanded }"
            >
              <div
                class="widget-help-text  space-preline break-normal pT5 pB5 pL10 pR10"
              >
                {{ widget.helpText }}
              </div>
              <i slot="reference" class="pointer el-icon-info"></i>
            </el-popover>
          </div>
          <div v-if="showExpand" class="mT1">
            <i
              @click="expandReport"
              v-if="viewOrEdit == 'view'"
              class="fa fa-expand expander"
              :title="$t('common._common.expand')"
              data-position="top"
              v-tippy="{ arrow: true, animation: 'perspective' }"
            ></i>
          </div>
          <div :span="8" v-if="showDropDown">
            <drop-down
              :isDropDownExpanded.sync="isDropDownExpanded"
              :options="options"
              placement="bottom"
              :item="item"
            >
              <template #icon>
                <i
                  class="pointer fc-widget-moreicon-vertical fa fa-ellipsis-v"
                ></i>
              </template>
            </drop-down>
          </div>
        </div>
      </el-col>
    </el-row>
    <!-- Some widgets (like cards) don't have a `header` but they have dropdowns. -->
    <div class="floating-controls" v-else>
      <div class="row">
        <div>
          <el-popover
            v-if="showHelpText"
            v-model="isDropDownExpanded"
            :popper-class="'widget-info-popper'"
            placement="bottom"
            trigger="click"
            class="widget-info-container mT6"
            :class="{ 'show-info-icon': isInfoIconPopOverOpen }"
          >
            <div
              class="widget-help-text  space-preline break-normal pT5 pB5 pL10 pR10"
            >
              {{ widget.helpText }}
            </div>
            <i slot="reference" class="pointer mR5 el-icon-info"></i>
          </el-popover>
        </div>
        <div>
          <drop-down
            v-if="showDropDown && !showHeader"
            :isDropDownExpanded.sync="isDropDownExpanded"
            :options="options"
            :item="item"
          >
            <template #icon>
              <i slot="reference" class="pointer el-icon-more more-icon"></i>
            </template>
          </drop-down>
        </div>
      </div>
    </div>
    <div class="horizontal-rule" v-if="showHeader"></div>
    <div class="widget-body">
      <component
        ref="component"
        :is="widgetType"
        :loadImmediately="loadImmediately"
        :dbFilterJson="dbFilterJson"
        :viewOrEdit="viewOrEdit"
        :id="id"
        :item="item"
        :hideTimelineFilterInsideWidget="hideTimelineFilterInsideWidget"
        :componentVisibleInViewPort="componentVisibleInViewPort"
        :widgetBodyDimension="widgetBodyDimension"
        :updateWidget="updateWidget"
        :printMode="printMode"
      />
    </div>
    <widget-help-text-config
      v-if="helpTextDialog"
      :visibility.sync="helpTextDialog"
      :widget="widget"
      @helpTextChanged="updateHelpText"
    />
  </div>
</template>

<script>
import KpiCard from 'src/pages/new-dashboard/components/widgets/KpiMutiLayout.vue'
import NewDatePicker from 'src/pages/new-dashboard/components/date-picker/NewDatePicker.vue'
import FListWidget from 'src/pages/new-dashboard/components/widgets/FListWidget.vue'
import AnalyticalReportWidget from 'src/pages/new-dashboard/components/widgets/AnalyticalReportWidget.vue'
import { isEmpty } from '@facilio/utils/validation'
import cloneDeep from 'lodash/cloneDeep'
import ModuleReportWidget from 'src/pages/new-dashboard/components/widgets/ModuleReportWidget.vue'
import CardWidget from 'src/pages/new-dashboard/components/widgets/CardWidget.vue'
import ImageWidget from 'src/pages/new-dashboard/components/widgets/ImageWidget.vue'
import TextWidget from 'src/pages/new-dashboard/components/widgets/TextWidget.vue'
import WidgetHelpTextConfig from 'src/pages/new-dashboard/components/reusable-components/WidgetHelpTextConfig.vue'
import DropDown from 'src/pages/new-dashboard/components/DropDown.vue'
import PivotTableWrapper from 'src/pages/new-dashboard/components/widgets/PivotTableWrapper.vue'
import FGraphicsWidget from 'src/pages/new-dashboard/components/widgets/FGraphicsWidget.vue'
export default {
  components: {
    NewDatePicker,
    FListWidget,
    ModuleReportWidget,
    CardWidget,
    ImageWidget,
    TextWidget,
    WidgetHelpTextConfig,
    DropDown,
    AnalyticalReportWidget,
    PivotTableWrapper,
    FGraphicsWidget,
    KpiCard,
  },
  created() {
    if (this.printMode) {
      this.componentVisibleInViewPort = true
    }
    this.title = cloneDeep(this.widget?.header?.title ?? '')
    if (this.title.length == 0 && this.viewOrEdit == 'edit') {
      this.titleInEditMode = true
    }
  },
  mounted() {
    this.calculateWidgetBodyDimension()
  },
  methods: {
    calculateWidgetBodyDimension() {
      const self = this
      this.$nextTick(() => {
        self.$nextTick(() => {
          self.$nextTick(() => {
            // Note: $el will only be available in mount life cycle hook. Before $el will return
            const [widgetBody] = self.$el.getElementsByClassName('widget-body')
            const { offsetWidth, offsetHeight } = widgetBody ?? {}
            self.widgetBodyWidth = offsetWidth
            self.widgetBodyHeight = offsetHeight
          })
        })
      })
    },
    visibilityChanged(visibility) {
      const { isLazyDashboard, componentVisibleInViewPort } = this
      if (componentVisibleInViewPort == false) {
        if (isLazyDashboard) {
          if (visibility) {
            this.componentVisibleInViewPort = true
          }
        } else {
          this.componentVisibleInViewPort = true
        }
      }
    },
    dateChanged(date) {
      console.log(date)
    },
    editTitle() {
      if (this.viewOrEdit == 'edit') {
        this.titleInEditMode = true
      }
    },
    expandReport() {
      this.$refs['component'].expandReport()
    },
    updateTitle() {
      const { item, title, updateWidget } = this
      const updatedWidget = cloneDeep(item)
      updatedWidget.widget.header.title = title
      updateWidget(updatedWidget)
      if (title.length > 0) {
        this.titleInEditMode = false
      }
    },
    showHelpTextModal() {
      this.helpTextDialog = true
    },
    removeWidget() {
      this.$emit('removeWidget')
    },
    updateHelpText({ helpText, widgetSettings: { showHelpText } }) {
      const { item, updateWidget } = this
      const updatedWidget = cloneDeep(item)
      const { widget } = updatedWidget
      widget['widgetSettings'].showHelpText = showHelpText
      widget['helpText'] = helpText
      updateWidget(updatedWidget)
      this.helpTextDialog = false
    },
  },
  props: {
    printMode: {
      type: Boolean,
      default: false,
    },
    updateWidget: {
      type: Function, // This is a anti pattern in general, works better than using $parent.$emit and $emit.
    },
    parentId: {
      type: String,
      default: 'master',
    },
    groupList: {
      type: Array,
      default: () => [],
    },
    loadImmediately: {
      type: Boolean,
      default: false,
      required: true,
    },
    hideTimelineFilterInsideWidget: {
      type: Boolean,
      required: true,
      default: false,
    },
    viewOrEdit: {
      type: String,
      required: true,
    },
    widgetConfig: {
      type: Object,
      default: () => {},
      required: true,
    },
    item: {
      type: Object,
      required: true,
    },
    isLazyDashboard: {
      type: Boolean,
      required: true,
    },
    dbFilterJson: {
      type: Object,
      default: () => {},
    },
  },
  watch: {
    widgetDimension() {
      this.calculateWidgetBodyDimension()
    },
  },
  computed: {
    alwaysShowWidgetControls() {
      return this?.widgetConfig?.alwaysShowWidgetControls ?? false
    },
    filteredGroupList() {
      // If the widget is in master grid then don't show master grid in
      // move to drop down. If widget is in section -1 then don't show -1
      // in the drop down and so on.
      const { groupList, parentId } = this
      const filteredGroupList = groupList.filter(({ groupId }) => {
        return parentId != groupId
      })
      return filteredGroupList
    },
    commonEditMenu() {
      const commonEditMenu = [
        {
          label: 'Edit Help Text',
          action: this.showHelpTextModal,
          icon: 'el-icon-edit',
        },
        {
          label: 'Remove',
          action: this.removeWidget,
          icon: 'el-icon-delete',
        },
      ]
      const { filteredGroupList } = this
      if (filteredGroupList.length > 0) {
        // Don't show `Move to section` if no group is present in the dashboard.
        commonEditMenu.push({
          label: 'Move to Group',
          action: () => {},
          nested: this.filteredGroupList,
          icon: 'el-icon-rank',
        })
      }
      return commonEditMenu
    },
    commonViewMenu() {
      return []
    },
    widgetDimension() {
      const {
        item: { w, h },
      } = this
      return {
        w,
        h,
      }
    },
    widgetBodyDimension() {
      const { widgetBodyWidth, widgetBodyHeight } = this
      return {
        widgetBodyWidth: widgetBodyWidth ?? 0,
        widgetBodyHeight: widgetBodyHeight ?? 0,
      }
    },
    showDatePickerInHeader() {
      return this?.widgetConfig?.showDatePickerInHeader ?? false
    },
    showDropDown() {
      const {
        options: { length },
      } = this
      return this?.widgetConfig?.showDropDown && length > 0
    },
    widget() {
      return this?.item?.widget ?? []
    },
    id() {
      return this.item.id
    },
    showHeader() {
      return this?.widgetConfig?.showHeader ?? false
    },
    showHelpText() {
      return this?.widget?.widgetSettings?.showHelpText ?? false
    },
    showExpand() {
      return this?.widgetConfig?.showExpand ?? false
    },
    widgetType() {
      const {
        type: widgetType,
        dataOptions: { newReportId, reportType, staticKey },
      } = this.widget ?? {}
      switch (widgetType) {
        case 'chart':
          if (!isEmpty(newReportId)) {
            if (reportType == 2) {
              return 'ModuleReportWidget'
            } else if (reportType == 5) {
              return 'PivotTableWrapper'
            } else {
              return 'AnalyticalReportWidget'
            }
          } else {
            return 'ChartWidget' // Don't know the purpose of this :)
          }
        case 'card':
          return 'CardWidget'
        case 'static': // Image widget and Text widget.
          if (staticKey == 'textcard') {
            return 'TextWidget'
          } else if (staticKey == 'imagecard') {
            return 'ImageWidget'
          } else if (staticKey == 'kpiCard') {
            return 'KpiCard'
          }
          break
        case 'view':
          return 'FListWidget'
        case 'graphics':
          return 'FGraphicsWidget'
      }
      return '' // Default.
    },
    borderAroundWidget() {
      return this?.widgetConfig?.borderAroundWidget ?? false
    },
    options() {
      const { viewOrEdit, widgetConfig, commonEditMenu, commonViewMenu } = this
      if (viewOrEdit == 'edit') {
        return !isEmpty(widgetConfig?.editMenu)
          ? [...widgetConfig.editMenu, ...commonEditMenu]
          : [...commonEditMenu]
      } else if (viewOrEdit == 'view') {
        return !isEmpty(widgetConfig?.viewMenu)
          ? [...widgetConfig.viewMenu, ...commonViewMenu]
          : [...commonViewMenu]
      } else if (viewOrEdit == 'configFilter') {
        return widgetConfig?.configFilter ?? []
      } else {
        return []
      }
    },
  },
  data() {
    return {
      isDropDownExpanded: false,
      widgetBodyWidth: 0, // Initial width.
      widgetBodyHeight: 0, // Initial height.
      componentVisibleInViewPort: false,
      isInfoIconPopOverOpen: false,
      widgetDateObj: null,
      titleInEditMode: false,
      title: null,
      showOptionsPopover: false,
      helpTextDialog: false,
    }
  },
}
</script>

<style lang="scss" scoped>
.title-input-container {
  height: 18.5px;
}
.more-icon {
  font-size: 18px;
  color: #6a6990;
  opacity: 1;
  background: #fff;
  border-radius: 3px;
  padding: 5px;
  font-weight: 300 !important;
  border: 1px solid #7f7e7e;
}
.fc-widget-moreicon-vertical {
  display: block;
  font-size: 18px;
  color: #6a6990;
  opacity: 1;
  background: #fff;
  border-radius: 3px;
  padding-right: 5px;
  font-weight: 300 !important;
}
.fc-widget-moreicon-vertical:hover {
  opacity: 1;
}
.fa.fa-ellipsis-v:hover {
  color: #25243e;
}
.widget-body {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  border: 0px;
  padding: 0px;
  margin: 0px;
}
.date-picker-portal {
  display: block;
  margin-top: -8px;
}
.mR12 {
  margin-right: 12px;
}
.fc-widget-label {
  font-size: 15.4px;
  font-weight: 500;
  font-style: normal;
  font-stretch: normal;
  line-height: normal;
  letter-spacing: 0.5px;
  text-align: left;
  color: #2f2e49;
  padding-bottom: 0px;
}
.hover-visible-widget-controls {
  &:hover {
    .floating-controls {
      display: block;
    }
    .widget-controls {
      display: flex;
    }
  }
}
.always-visible-widget-controls {
  .floating-controls {
    display: block;
  }
  .widget-controls {
    display: flex;
  }
}
.row {
  display: flex;
  justify-content: flex-end;
}
.mT6 {
  margin-top: 6px !important;
}
.expander:hover {
  opacity: 1;
}
.expander {
  font-size: 15px;
  cursor: pointer;
  opacity: 0.6;
  color: #6a6990;
  margin-top: 0px;
  margin-right: 15px;
  display: block;
  transform: rotate(-7deg);
}
.widget-wrapper {
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  width: 100%;
  height: 100%;
  padding: 0px;
  margin: 0px;
}
.widget-wrapper-border {
  border: solid 1px #eae8e8;
}
.widget-header {
  height: 48.5px;
  padding: 15px;
  box-sizing: border-box;
}
.cursor-move {
  cursor: move;
}
.horizontal-rule {
  width: 100%;
  border-top: 1px solid #eae8e8;
}
.widget-controls-container {
  position: relative;
}
.widget-controls {
  width: 230px;
  position: absolute;
  right: 0px;
  top: 0px;
  display: none;
}

.floating-controls {
  width: 55px;
  position: absolute;
  right: 10px;
  top: 10px;
  z-index: 1;
  display: none;
}
</style>
<style lang="scss">
.widget-wrapper-input .el-input__inner {
  letter-spacing: 0.5px;
  height: 18.5px;
  font-size: 15.4px;
  font-weight: 500;
  color: #2f2e49;
  border: none;
}
</style>
