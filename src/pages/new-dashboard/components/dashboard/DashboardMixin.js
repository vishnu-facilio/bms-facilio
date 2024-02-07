import { sort } from '@facilio/ui/dashboard'
import { isEmpty } from '@facilio/utils/validation'
import { getUserFilterModel as getUserFilter } from 'src/pages/dashboard/dashboard-filters/DashboardFilterHelper.js'
import DateHelper from 'src/pages/new-dashboard/components/date-picker/NewDateHelper.js'
import merge from 'lodash/merge'
import { cloneDeep } from 'lodash'
import { isWebTabsEnabled, pageTypes, findRouteForTab } from '@facilio/router'
import { eventBus } from 'src/pages/new-dashboard/utils/eventBus.js'
export default {
  data() {
    return {
      showGridstackComponent: false,
      preparedDashboardLayout: [],
      widgetsToCopy: [],
    }
  },
  computed: {
    sectionStyle() {
      const { dashboardLayout: sections } = this
      const style = sections.reduce((style, section) => {
        const { id, type: widgetType, banner_meta: header } = section
        if (widgetType == 'section') {
          const { type: headerType } = header
          if (headerType == 'banner') {
            const {
              banner: { borderColor, backgroundColor },
            } = header
            style[id] = {
              border: `1px solid ${borderColor}`,
              'border-radus': '5px',
              'background-color': backgroundColor,
            }
          } else if (headerType == 'color') {
            const {
              color: { color, background },
            } = header
            style[id] = {
              border: `1px solid ${color}`,
              'border-left': `5px solid ${color}`,
              'border-radius': '3px',
              'background-color': background,
            }
          }
        } else {
          style[id] = {}
        }
        return style
      }, {})
      return style
    },
  },
  methods: {
    unsubscribeEventBus() {
      eventBus.$off('initWidget')
      eventBus.$off('onAction')
      eventBus.$off('updateEditDropDown')
      eventBus.$off('updateViewDropDown')
    },
    subscribeEventBus() {
      this.unsubscribeEventBus() // To make sure that all the existing listeners are disconnected first. To prevent multiple listeners listening for the same event.
      const self = this
      eventBus.$on('initWidget', config => {
        self.$set(self.widgetConfigMap, config.id, config)
      })
      eventBus.$on('updateEditDropDown', ({ items, id }) => {
        self.widgetConfigMap[id].editMenu.push(...items)
      })
      eventBus.$on('updateViewDropDown', ({ items, id }) => {
        self.widgetConfigMap[id].viewMenu.push(...items)
      })
      eventBus.$on('onAction', this.onAction)
    },
    gotoEditer() {
      const { dashboardLink } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.DASHBOARD_EDITOR) || {}
        if (name) {
          let params = {
            dashboardlink: dashboardLink,
          }

          this.$router.push({
            name,
            params,
            query: { create: 'edit' },
          })
        }
      } else {
        this.$router.push({
          path: `/app/home/editdashboard/${dashboardLink}`,
          query: { create: 'edit' },
        })
      }
    },
    editSection(id) {
      const index = this.dashboardLayout.findIndex(w => w.id == id)
      this.sectionSelectedForEditing = this.dashboardLayout[index]
      this.showSectionEditor = true
    },
    duplicateWidget(widget) {
      const { id } = widget
      const { index, childIndex } = this.findIndexUsingId(id)
      const newId = this.get_uid()
      widget['id'] = newId
      widget.widget['id'] = newId
      if (!isEmpty(childIndex)) {
        this.dashboardLayout[index].children.push(widget)
      } else {
        this.dashboardLayout.push(widget)
      }
    },
    deleteWidget(id) {
      this.$refs['gridstack'].removeItem(id)
    },
    findIndexUsingId(id) {
      let index = null
      let childIndex = null
      if (this.dashboardLayout && this.dashboardLayout.length > 0) {
        outerloop: for (let i = 0; i < this.dashboardLayout.length; i++) {
          const w = this.dashboardLayout[i]
          if (w.id == id) {
            index = i
            childIndex = null
            break outerloop // Break out of the outer loop.
          } else if (w.children) {
            const children = this.dashboardLayout[i].children
            for (let ci = 0; ci < children.length; ci++) {
              const cw = children[ci]
              if (cw.id == id) {
                index = i
                childIndex = ci
                break outerloop // Break out of the outer loop.
              }
            }
          }
        }
      }
      return {
        index: index,
        childIndex: childIndex,
      }
    },
    updateWidget(updatedWidget) {
      const { id } = updatedWidget ?? {}
      const { index, childIndex } = this.findIndexUsingId(id)
      if (!isEmpty(childIndex)) {
        this.$set(
          this.dashboardLayout[index].children,
          childIndex,
          updatedWidget
        )
      } else {
        this.$set(this.dashboardLayout, index, updatedWidget)
      }
      const getDimension = () => {
        const dimension = {}
        if ('h' in updatedWidget) {
          dimension['h'] = updatedWidget.h
        }
        if ('w' in updatedWidget) {
          dimension['w'] = updatedWidget.w
        }
        if ('x' in updatedWidget) {
          dimension['x'] = updatedWidget.x
        }
        if ('y' in updatedWidget) {
          dimension['y'] = updatedWidget.y
        }
        return dimension
      }
      this.resizeWidget(id, getDimension())
    },
    getMinW(type, widget) {
      if (
        this.newDashboardData &&
        this.newDashboardData.widget &&
        this.newDashboardData.widget.dataOptions &&
        this.newDashboardData.widget.dataOptions.type &&
        this.newDashboardData.widget.dataOptions.type === 'boolean'
      ) {
        return 4
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'readingComboCard') {
          return 4
        }
      }
      if (type) {
        let width = 24
        switch (type) {
          case 'chart':
            width = 32
            break
          case 'static':
            width = 24
            break
          case 'view':
            width = 48
            break
          case 'list':
            width = 40
            break
          case 'map':
            width = 56
            break
          case 'web':
            width = 32
            break
          case 'connectedapps':
            width = 32
            break
          case 'graphics':
            width = 96
            break
          default:
            width = 32
            break
        }
        return width
      } else {
        return 24
      }
    },
    getMinH(type, widget) {
      if (
        this.newDashboardData &&
        this.newDashboardData.widget &&
        this.newDashboardData.widget.dataOptions &&
        this.newDashboardData.widget.dataOptions.type &&
        this.newDashboardData.widget.dataOptions.type === 'boolean'
      ) {
        return 4
      }
      if (widget && widget.dataOptions && widget.dataOptions.staticKey) {
        if (widget.dataOptions.staticKey === 'textcard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'imagecard') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'web') {
          return 4
        }
        if (widget.dataOptions.staticKey === 'readingComboCard') {
          return 4
        }
      }
      if (type) {
        let height = 24
        switch (type) {
          case 'chart':
            height = 24
            break
          case 'static':
            height = 32
            break
          case 'view':
            height = 32
            break
          case 'list':
            height = 32
            break
          case 'map':
            height = 32
            break
          case 'buildingcard':
            height = 24
            break
          case 'web':
            height = 32
            break
          case 'connectedapps':
            height = 32
            break
          case 'graphics':
            height = 45
            break
          default:
            height = 32
            break
        }
        return height
      } else {
        return 32
      }
    },
    resizeSection({ id, height, collapsed }) {
      const index = this.dashboardLayout.findIndex(w => w.id == id)
      const section = this.dashboardLayout[index]
      section.collapsed = collapsed
      // this.animateWidget({
      //   id,
      //   keyframes: [
      //     { transform: `rotateY(0deg) skew(0deg, 0deg)` },
      //     { transform: `rotateY(0.5deg) skew(0deg, 0.5deg)` },
      //     { transform: `rotateY(0deg) skew(0deg, 0.5deg)` },
      //   ],
      //   options: {
      //     duration: 300,
      //     iterations: 1,
      //   },
      // })
      this.resizeWidget(id, { h: height })
    },
    resizeWidget(id, dimension) {
      // The 'height' and 'width' should be an integer not a float number else it doesn't work.
      this.$refs['gridstack'].updateItem(id, dimension)
    },
    deserializeWidgets(widgets) {
      const self = this
      const makeWidget = item => {
        const getDimension = () => {
          if (self.printMode && item?.type == 'view') {
            return {
              x: 0,
              y: (item.layout.y ? item.layout.y : 0) * 1,
              w: 96,
              h: item.layout.height * 1,
            }
          } else {
            return {
              x: (item.layout.x ? item.layout.x : 0) * 1,
              y: (item.layout.y ? item.layout.y : 0) * 1,
              w: item.layout.width * 1,
              h: item.layout.height * 1,
              minW: 0,
              minH: 0,
            }
          }
        }
        return {
          id: item.id + '',
          widget: item,
          ...getDimension(),
          minW: this.getMinW(item.type, item),
          minH: this.getMinH(item.type, item),
        }
      }
      const makeSectionWidgets = widgets => {
        const children = []
        widgets.forEach(widget => {
          const madeWidget = makeWidget(widget.widget)
          children.push(madeWidget)
        })
        return children
      }
      return widgets.reduce((layout, item) => {
        item = item.widget
        if (item.type == 'section') {
          const sectionWidgets = makeSectionWidgets(item.section)
          item['children'] = sectionWidgets
          delete item.section
          item.banner_meta = JSON.parse(item.banner_meta ?? '{}')
          item.id = item.id + '' // Typecasting number id to a string id...
          layout.push(item)
        } else {
          const madeWidget = makeWidget(item)
          layout.push(madeWidget)
        }
        return layout
      }, [])
    },
    prepareDashboardLayout(widgets) {
      this.preparedDashboardLayout = this.deserializeWidgets(
        cloneDeep((widgets ?? []).concat(this.widgetsToCopy ?? []))
      )
      this.widgetsToCopy = []
      this.loadWidgetsUsingActions()
    },
    loadWidgetsUsingActions() {
      const { isTimelineFilterEnabled, userFilterList, dbTimelineFilter } =
        this ?? {}
      // Handling timeline filter onLoad.
      if (isTimelineFilterEnabled) {
        this.recentTimelineFilterPayload = cloneDeep(
          this.getTimelineFilterReq(dbTimelineFilter)
        )
      }
      // Handling userFilter onLoad.
      if (!isEmpty(userFilterList)) {
        const userFilter = getUserFilter(userFilterList)
        this.recentUserFilterPayload = cloneDeep(
          this.getUserFilterReq({
            filter: userFilter,
          })
        )
      }
      // 1. If there are filters - either timeline filter or user filter, then hit the API get the actions, announce the actions to appropriate widgets
      // using eventBus. The actions announced to the widgets will load the widgets.
      // 2. If there are no filters, that is, if there is no timeline filter or user filter then load the widgets without making any API call to get a list of actions.
      // Only mount the widgets after setting the prop `loadImmediately` to true in this case. This is automatically taken care of since `changeDashboardLayout()`
      // method mounts all the widgets in the nextTick() after the `loadImmediately` is changed.
      const req = merge(
        cloneDeep(this.recentUserFilterPayload),
        cloneDeep(this.recentTimelineFilterPayload)
      )
      if (!isEmpty(req)) {
        this.changeDashboardLayout({
          layout: this.preparedDashboardLayout,
          loadImmediately: false,
        })
        this.executeActions(req)
      } else {
        this.changeDashboardLayout({
          layout: this.preparedDashboardLayout,
          loadImmediately: true,
        })
      }
    },
    changeDashboardLayout({ layout, loadImmediately = false } = {}) {
      // This is a hack, this has been done to overcome a flaw in Gridstack component...
      // 1) Unmount the Gridstack component.
      // 2) Change this.gridstackLayout.
      // 3) Mount the Gridstack component back.
      // Or layout.sync will emit events from inside the Gridstack component and change the prop value... This is unpredictable.
      this.loadImmediately = loadImmediately
      this.showGridstackComponent = false
      const self = this
      this.$nextTick(() => {
        self.dashboardLayout = sort(cloneDeep(layout))
        self.dashboardLayout.forEach(layout => {
          const print = JSON.parse(
            this.$getProperty(this, '$route.query.printing', false)
          )
          if (layout.hasOwnProperty('children') && layout.collapsed && print) {
            layout.collapsed = false
            const { children } = layout
            const SECTION_HEAD_HEIGHT = 7
            const SECTION_BODY_HEIGHT = this.findSectionMaxHeight(children)
            const SECTION_BOTTOM_PADDING = 1
            layout.h =
              SECTION_HEAD_HEIGHT + SECTION_BODY_HEIGHT + SECTION_BOTTOM_PADDING
          }
        })
        self.showGridstackComponent = true
      })
    },
    findSectionMaxHeight(children) {
      const { length } = children
      if (length) {
        return Math.max(...children.map(o => o.y + o.h))
      } else {
        return 13
      }
    },
    animateWidget({
      id,
      keyframes = [
        { 'transform-origin': '0% 0%' },
        {
          transform: `translateY(50%)`,
        },
        { transform: 'translate(5px, 5px) rotate(1deg)' },
        { transform: 'translate(0, 0) rotate(0deg)' },
        { transform: 'translate(-5px, 5px) rotate(-1deg)' },
        { transform: 'translate(0, 0) rotate(0deg)' },
      ],
      options = {
        duration: 200,
        iterations: 3,
      },
    }) {
      const element = this.$el.querySelector(`[gs-id="${id}"]`)
      element.animate(keyframes, options)
    },
    getAllWidgetElements() {
      return (
        Array.from(
          this.$el.querySelectorAll(`[gs-id], .gridstack-group`) ?? []
        ) ?? []
      )
    },
    getWidgetElements() {
      return this.getAllWidgetElements().filter(element => {
        return !element.classList.contains('gridstack-group')
      })
    },
    getAllGroupElements() {
      return this.getAllWidgetElements().filter(element => {
        return element.classList.contains('gridstack-group')
      })
    },
    animateAllGroups() {
      const groupElements = this.getAllGroupElements()
      const self = this
      groupElements.forEach(element => {
        const id = element.getAttribute()
        self.animateWidget({
          id,
          keyframes: [
            { transform: `rotateY(0deg) skew(0deg, 0deg)` },
            { transform: `rotateY(1deg) skew(0deg, 1deg)` },
            { transform: `rotateY(0deg) skew(0deg, 0deg)` },
          ],
          options: {
            duration: 300,
            iterations: 1,
          },
        })
      })
    },
    animateAllWidgets() {
      const groupElements = this.getWidgetElements()
      const self = this
      groupElements.forEach(element => {
        const id = element.getAttribute('gs-id')
        self.animateWidget(id)
      })
      // Excludes groups, groups are also widgets btw :)
    },
  },
}
