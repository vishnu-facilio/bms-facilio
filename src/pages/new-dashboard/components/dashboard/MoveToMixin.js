import { cloneDeep } from 'lodash'

export default {
  data() {
    return {}
  },
  methods: {
    moveToGroup(toGroupId, widgetToBeMoved) {
      const self = this
      const { dashboardLayout: groups } = this
      // Remove the widget from its Gridstack grid and push it into the `toGroupId` Gridstack grid.
      if (toGroupId == 'master') {
        this.deleteWidget(widgetToBeMoved.id)
        groups.push(cloneDeep(widgetToBeMoved))
      } else {
        this.deleteWidget(widgetToBeMoved.id)
        const sectionIndex = groups.findIndex(({ id }) => id == toGroupId)
        groups[sectionIndex].children.push(cloneDeep(widgetToBeMoved))
      }
      this.$nextTick(() => {
        const { id } = widgetToBeMoved
        const { childIndex, index } = self.findIndexUsingId(id)
        // If the widget has a child index then it must be present inside of a section / group,
        if (childIndex >= 0) {
          const { collapsed = false } = self.dashboardLayout[index] ?? {}
          // The widget has been moved into a section,
          // TODO: Expand the section before making the widget vibrate.
          // For now if the widget has been moved to a section which is collapsed then don't
          // vibrate the widget.
          // const keyframes = [
          //   {
          //     transform: 'scale(1)',
          //   },
          //   {
          //     transform: 'scale(1.05)',
          //   },
          //   {
          //     transform: 'scale(1)',
          //   },
          //   {
          //     transform: 'scale(1.05)',
          //   },
          //   {
          //     transform: 'scale(1)',
          //   },
          // ]
          // const options = {
          //   duration: 500,
          //   iterations: 2,
          // }
          if (!collapsed) {
            const widgetElement = self.$el.querySelector(`[gs-id="${id}"]`)
            widgetElement.scrollIntoView({
              block: 'nearest',
              behavior: 'smooth',
            })
            //   self.animateWidget({ id, keyframes, options })
          } else {
            const { id: groupId } = self.dashboardLayout[index]
            const groupElement = self.$el.querySelector(`[gs-id="${groupId}"]`)
            groupElement.scrollIntoView({
              block: 'nearest',
              behavior: 'smooth',
            })
            // self.$nextTick(() => {
            //   self.animateWidget({ id: groupId, keyframes, options })
            // })
          }
        }
      })
    },
  },
  computed: {
    groupList() {
      const { dashboardLayout } = this
      const self = this
      const groupList = dashboardLayout.reduce(
        (groupList, section) => {
          const { type, id, name } = section
          if (type == 'section') {
            groupList.push({
              label: name,
              groupId: id,
              action: widget => {
                self.moveToGroup(id, widget)
              },
              icon: 'el-icon-folder-opened',
            })
          }
          return groupList
        },
        [
          {
            label: 'Master',
            groupId: 'master',
            action: widget => {
              self.moveToGroup('master', widget)
            },
            icon: 'el-icon-folder-opened',
          },
        ]
      )
      return groupList
    },
  },
}
