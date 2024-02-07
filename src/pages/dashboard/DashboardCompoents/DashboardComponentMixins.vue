<script>
export default {
  data() {
    return {
      showCompoents: false,
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
    updatetextcard(data) {
      this.textcardData = null
      this.textcardData = data
      this.isDraging = false
      this.showtextcard = false
    },
    addtextcard(data) {
      this.textcardData = null
      this.textcardData = data
      this.addNewelemnt()
      this.isDraging = false
      this.showtextcard = false
    },
    closetextcard() {
      this.textcardData = null
      this.isDraging = false
      this.showtextcard = false
    },
    addText() {
      let d = {
        key: 'textcard',
        label: 'Text card',
        w: 48,
        h: 24,
        optionName: 'cards',
      }
      this.start(d)
      this.$nextTick(() => {
        this.drop()
        this.$nextTick(() => {
          this.dragend()
        })
      })
    },
    addImage() {
      let d = {
        key: 'imagecard',
        label: 'Image card',
        w: 24,
        h: 24,
        optionName: 'cards',
      }
      this.start(d)
      this.$nextTick(() => {
        this.drop()
        this.$nextTick(() => {
          this.dragend()
        })
      })
    },
    addDashboardElement(data) {
      if (data) {
        let index = this.dashboardLayout.length
        data.forEach(element => {
          if (element.optionName) {
            this.option = element.optionName
          }
          this.start(element)
          this.drop()
          this.$nextTick(() => {
            this.dragend()
          })

          // let layout = {}
          // let l = this.getReportWidget(element, layout)
          // this.dashboardLayout.push(l)
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
          let elements = document.getElementsByClassName('vue-grid-item')
          for (let i = index; i < this.dashboardLayout.length; i++) {
            elements[i].style.boxShadow = '0 2px 4px 4px rgb(219 232 14 / 50%)'
          }
          document
            .getElementsByClassName('vue-grid-item')
            [index].scrollIntoView({
              behavior: 'smooth',
              block: 'end',
              inline: 'nearest',
            })
          this.countDownCss(index)
        }, 800)
      })
    },
    countDownCss(index) {
      let countDown = 4
      let count = setInterval(() => {
        countDown = countDown - 1
        let elements = document.getElementsByClassName('vue-grid-item')
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
      this.graphicsList.forEach(report => {
        report.selected = false
      })
    },
    openDashboardCompoents() {
      this.showCompoents = true
    },
    closeCompoent() {
      this.showCompoents = false
    },
  },
}
</script>
