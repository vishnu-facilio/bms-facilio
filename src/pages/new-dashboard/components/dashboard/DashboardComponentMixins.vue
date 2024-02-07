<script>
import { cloneDeep } from 'lodash'
import { isEmpty } from '@facilio/utils/validation'
export default {
  data() {
    return {
      showReportChooser: false,
      textcardData: null,
    }
  },
  computed: {
    componentData() {
      let data = {}
      let {
        newReportTree,
        reportTree,
        supportedModules,
        customModuleList,
        graphicsList,
        getLicenseEnabledModules,
      } = this
      if (newReportTree && newReportTree.length) {
        this.$set(data, 'reports', newReportTree)
      }
      if (reportTree && reportTree.length) {
        this.$set(data, 'reportTree', reportTree)
      }
      if (supportedModules && supportedModules.length) {
        this.$set(data, 'supportedModules', supportedModules)
      }
      if (customModuleList && customModuleList.length) {
        this.$set(data, 'customModuleList', customModuleList)
      }
      if (graphicsList && graphicsList.length) {
        this.$set(data, 'graphicsList', graphicsList)
      }
      if (getLicenseEnabledModules && getLicenseEnabledModules.length) {
        this.$set(data, 'getLicenseEnabledModules', getLicenseEnabledModules)
      }

      return data
    },
  },
  methods: {
    updatetextcard({ textData, id }) {
      const { index, childIndex } = this.findIndexUsingId(id)
      let widget = null
      if (!isEmpty(childIndex)) {
        widget = this.dashboardLayout[index].children[childIndex]
      } else {
        widget = this.dashboardLayout[index]
      }
      this.$set(widget.widget.dataOptions, 'metaJson', textData)
      this.showtextcard = false
    },
    addtextcard(text) {
      const newId = this.get_uid()
      const {
        dashboardLayout: { length: widgetCount },
      } = this
      const widget = {
        i: newId,
        id: newId,
        x: 0,
        y: 0,
        w: 48,
        h: 24,
        widget: {
          layout: {
            width: 48,
            x: 0,
            y: 0,
            position: widgetCount + 1,
            height: 24,
          },
          header: {
            subtitle: 'today',
            title: '',
            export: false,
          },
          dataOptions: {
            dataurl: '',
            refresh_interval: 100,
            name: 'dummy',
            staticKey: 'textcard',
            metaJson: text,
          },
          type: 'static',
          id: newId,
          name: '',
          widgetSettings: {
            showHelpText: false,
          },
          helpText: '',
        },
        minW: 48,
        minH: 24,
        moved: false,
      }
      this.addWidget(widget)
      this.showtextcard = false
    },
    addImageCard(jsonData) {
      const {
        dashboardLayout: { length: widgetCount },
      } = this
      const newId = this.get_uid()
      const widget = {
        i: newId,
        id: newId,
        x: 0,
        y: 0,
        w: 24,
        h: 24,
        widget: {
          layout: {
            width: 24,
            x: 0,
            y: 0,
            position: widgetCount + 1,
            height: 24,
          },
          header: {
            subtitle: 'today',
            title: '',
            export: false,
          },
          dataOptions: {
            dataurl: '',
            refresh_interval: 100,
            name: 'dummy',
            staticKey: 'imagecard',
            imagecardDataUrl: null,
            imagecardData: jsonData,
          },
          type: 'static',
          id: newId,
          name: '',
          widgetSettings: {
            showHelpText: false,
            excludeDbFilters: false,
          },
          helpText: '',
        },
        minW: 24,
        minH: 24,
        moved: false,
      }
      this.addWidget(widget)
      this.showImageEditer = false
    },

    imageCardUploadFailed() {
      this.$message.error('Unable to upload image, please try again later.')
      this.showImageEditer = false
    },
    addWidget(widget) {
      const clonedWidget = cloneDeep(widget)
      if (this.sectionIndex >= 0) {
        this.dashboardLayout[this.sectionIndex].children.push(clonedWidget)
      } else {
        this.dashboardLayout.push(clonedWidget)
      }
    },
    addReports(reports) {
      const self = this
      if (!isEmpty(reports)) {
        reports.forEach(report => {
          const {
            dashboardLayout: { length: widgetCount },
          } = self
          const { optionName: reportType = '' } = report
          if (reportType == 'report') {
            // This covers `Module Widget` and `Analytical widget`, see component choose from dashboardEditer.vue;
            const { id: newReportId, name, type, reportTemplate } = report
            const newId = self.get_uid()
            const widget = {
              i: newId,
              x: 0,
              y: 0,
              w: 32,
              h: 32,
              id: newId,
              widget: {
                layout: {
                  width: 32,
                  x: 0,
                  y: 0,
                  position: widgetCount + 1, // + 1 since we are calculating before pushing it to the array.
                  height: 32,
                },
                header: {
                  title: name,
                },
                dataOptions: {
                  newReportId: newReportId,
                  newReport: report,
                },
                id: newId,
                newReportId: newReportId,
                type: 'chart',
                name: '',
                widgetSettings: {
                  showHelpText: false,
                  excludeDbFilters: false,
                },
                helpText: '',
              },
              minW: 32,
              minH: 32,
              moved: false,
            }
            if (type === 4) {
              // This is lame... They have monkey patched to support a feature in a random widget.
              widget.widget.dataOptions['reportTemplate'] = reportTemplate
            }
            self.addWidget(widget)
          } else if (reportType == 'view') {
            // This covers the `view` widget, see component chooser in dashboardEditer.
            const newId = self.get_uid()
            const { moduleName, name, displayName } = report
            const widget = {
              i: newId,
              id: newId,
              x: 0,
              y: 0,
              w: 32,
              h: 32,
              widget: {
                layout: {
                  width: 32,
                  x: 0,
                  y: 0,
                  position: widgetCount + 1,
                  height: 32,
                },
                header: {
                  subtitle: 'today',
                  title: displayName,
                  export: false,
                },
                dataOptions: {
                  dataurl: '',
                  refresh_interval: 100,
                  viewName: name,
                  name: 'dummy',
                  moduleName: moduleName,
                },
                type: 'view',
                id: newId,
                widgetSettings: {
                  showHelpText: false,
                },
                helpText: '',
              },
              label: displayName,
              minW: 32,
              minH: 32,
              moved: false,
            }
            self.addWidget(widget)
          } else if (reportType == 'graphics') {
            const { label, id, name, url } = report
            const {
              dashboardLayout: { length: widgetCount },
            } = this
            const newId = self.get_uid()
            const widget = {
              i: newId,
              id: newId,
              x: 0,
              y: 0,
              w: 96,
              h: 45,
              widget: {
                layout: {
                  width: 96,
                  x: 0,
                  y: 0,
                  position: widgetCount + 1,
                  height: 45,
                },
                header: {
                  subtitle: 'today',
                  title: name,
                  export: false,
                },
                dataOptions: {
                  dataurl: '',
                  refresh_interval: 100,
                  webUrl: url,
                  name: 'dummy',
                },
                type: 'graphics',
                graphicsId: id,
                widgetSettings: {
                  showHelpText: false,
                },
                helpText: '',
              },
              minW: 96,
              minH: 45,
              label: label,
              moved: false,
            }
            self.addWidget(widget)
          }
        })
        this.$message.success(
          this.$t('common._common.widget_added_successfully')
        )
      }
      this.sectionIndex = -1
    },
    addDashboardElement(reports) {
      if (reports) {
        let index = this.dashboardLayout.length
        reports.forEach(element => {
          this.selectedBuilding = null
          this.editwidgetData = null
          this.newDashboardData = element
          this.drop()
        })
        this.$message.success(
          this.$t('common._common.widget_added_successfully')
        )
        this.scrollToLatestWidget(index)
      }
    },
    scrollToLatestWidget(index) {
      this.$nextTick(() => {
        setTimeout(() => {
          let elements = document.getElementsByClassName(
            'grid-stack-item-content'
          )
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 4px rgb(219 232 14 / 50%)'
          }
          document
            .getElementsByClassName('grid-stack-item-content')
            [index].scrollIntoView({
              behavior: 'smooth',
              block: 'end',
              inline: 'nearest',
            })
          this.countDownCss(index)
        }, 1000)
      })
    },
    countDownCss(index) {
      let countDown = 4
      let count = setInterval(() => {
        countDown = countDown - 1
        let elements = document.getElementsByClassName(
          'grid-stack-item-content'
        )
        if (countDown === 4) {
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 4px rgb(219 232 14 / 50%)'
          }
        }
        if (countDown === 3) {
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 3px rgb(219 232 14 / 50%)'
          }
        }
        if (countDown === 2) {
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 2px rgb(219 232 14 / 50%)'
          }
        }
        if (countDown === 1) {
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 1px rgb(219 232 14 / 50%)'
          }
        }

        if (countDown === 0) {
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = 'none'
          }
          clearInterval(count)
        }
      }, 500)
    },
    resetreportData() {
      this.newReportTree.forEach(folder => {
        folder.reports.forEach(report => {
          report.selected = false
        })
      })
      this.reportTree.forEach(folder => {
        folder.reports.forEach(report => {
          report.selected = false
        })
      })
      this.supportedModules.forEach(folder => {
        folder.list.forEach(report => {
          report.selected = false
        })
      })
      this.customModuleList.forEach(folder => {
        folder.list.forEach(report => {
          report.selected = false
        })
      })
      this.graphicsList?.forEach(report => {
        report.selected = false
      })
    },
  },
}
</script>
