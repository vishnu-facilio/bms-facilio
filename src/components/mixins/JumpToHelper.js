import NewDateHelper from '@/mixins/NewDateHelper'
import {
  isWebTabsEnabled,
  findRouteForTab,
  findRouteForModule,
  pageTypes,
  tabTypes,
} from '@facilio/router'
import { isEmpty } from '@facilio/utils/validation'

export default {
  methods: {
    cardBuilderJumpToHelper(data) {
      let { linkType } = data
      let { module } = data
      let { id } = data
      let { readings } = data
      if (linkType) {
        if (linkType === 'SUMMARY' && module && id) {
          this.jumpToSummary(module, id)
        } else if (linkType === 'ANALYTICS' && readings && readings.length) {
          let parentId = readings[0].parentId
          let fieldId = readings[0].fieldId
          let { buildingId } = data
          let { yAggr } = data
          let { dateOperator } = data
          this.jumpReadingToAnalytics(
            parentId,
            fieldId,
            dateOperator,
            null,
            yAggr,
            buildingId
          )
        }
      }
    },
    jumpAlarmToAnalytics(
      alarmId,
      datePicker,
      isWithPrerequsite,
      ruleId,
      buildingId,
      shouldIncludeMarked
    ) {
      if (!datePicker) {
        datePicker = {
          operatorId: 22,
          value: null,
        }
      }
      let datePickerObject = NewDateHelper.getDatePickerObject(
        datePicker.operatorId,
        datePicker.value
      )
      let queryParams = {
        alarmId: alarmId,
        dateFilter: JSON.stringify(datePickerObject),
        isFromAlarmSummary: true,
        buildingId: buildingId,
      }
      if (isWithPrerequsite) {
        queryParams.isWithPrerequsite = isWithPrerequsite
      }
      if (!isEmpty(shouldIncludeMarked) && shouldIncludeMarked) {
        queryParams.shouldIncludeMarked = shouldIncludeMarked
      }
      if (ruleId) {
        queryParams.readingRuleId = ruleId
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

        if (name) {
          this.$router.push({
            name,
            query: queryParams,
          })
        }
      } else {
        this.$router.push({
          path: '/app/em/analytics/building',
          query: queryParams,
        })
      }
    },

    jumpCardToAnalytics(cardId, datePicker, card) {
      let cardData = JSON.parse(card.dataOptions.metaJson)
      if (!datePicker) {
        datePicker = {
          operatorId: 22,
          value: null,
        }
      }
      let datePickerObject = NewDateHelper.getDatePickerObject(
        datePicker.operatorId,
        datePicker.value
      )
      let queryParams = {
        cardId: cardId,
        dateFilter: JSON.stringify(datePickerObject),
      }
      if (
        cardData.readings &&
        cardData.readings.asset &&
        cardData.readings.asset.space &&
        cardData.readings.asset.space.buildingId
      ) {
        queryParams.buildingId = cardData.readings.asset.space.buildingId
      } else if (
        cardData.readings &&
        !cardData.readings.asset &&
        cardData.readings.space &&
        cardData.readings.space.buildingId
      ) {
        queryParams.buildingId = cardData.readings.space.buildingId
      }
      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

        if (name) {
          this.$router.push({
            name,
            query: queryParams,
          })
        }
      } else {
        this.$router.replace({
          path: '/app/em/analytics/building',
          query: queryParams,
        })
      }
    },

    jumpReportToAnalytics(reportId, datePicker) {
      if (!datePicker) {
        datePicker = {
          operatorId: 22,
          value: null,
        }
      }
      let datePickerObject = NewDateHelper.getDatePickerObject(
        datePicker.operatorId,
        datePicker.value
      )

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

        if (name) {
          this.$router.push({
            name,
            query: {
              reportId: reportId,
              dateFilter: JSON.stringify(datePickerObject),
            },
          })
        }
      } else {
        this.$router.replace({
          path: '/app/em/analytics/building',
          query: {
            reportId: reportId,
            dateFilter: JSON.stringify(datePickerObject),
          },
        })
      }
    },

    jumpReadingToAnalytics(
      parentId,
      readingFieldId,
      datePicker,
      xAggr,
      yAggr,
      buildingId
    ) {
      let dataPoint = {
        parentId: parentId,
        yAxis: {
          fieldId: readingFieldId,
          aggr: yAggr || 3,
        },
      }

      this.jumpReadingsToAnalytics([dataPoint], datePicker, xAggr, buildingId)
    },

    jumpReadingsToAnalytics(dataPoints, datePicker, xAggr, buildingId) {
      if (!datePicker) {
        datePicker = {
          operatorId: 22,
          value: null,
        }
      }

      let analyticsConfig = {
        analyticsType: 2,
        mode: 1,
        period: xAggr || 0,
        dateFilter: NewDateHelper.getDatePickerObject(
          datePicker.operatorId,
          datePicker.value
        ),
        dataPoints: dataPoints,
      }
      if (buildingId) {
        analyticsConfig.buildingId = buildingId
      }

      if (isWebTabsEnabled()) {
        let { name } = findRouteForTab(pageTypes.ANALYTIC_BUILDING) || {}

        if (name) {
          this.$router.push({
            name,
            query: {
              filters: JSON.stringify(analyticsConfig),
            },
          })
        }
      } else {
        this.$router.push({
          path: '/app/em/analytics/building',
          query: {
            filters: JSON.stringify(analyticsConfig),
          },
        })
      }
    },
    findRoute() {
      if (isWebTabsEnabled()) {
        let tabType = tabTypes.CUSTOM
        let config = { type: 'portfolio' }
        let { name } = findRouteForTab(tabType, { config }) || {}

        return name ? this.$router.resolve({ name }).href : null
      } else {
        return '/app/home/portfolio'
      }
    },
    getModuleURl(module, data) {
      if (['site', 'building'].includes(module)) {
        let parentPath = this.findRoute()
        if (parentPath) {
          let path =
            module === 'site'
              ? `${parentPath}/site/${data.id}/overview`
              : `${parentPath}/site/${data.siteId}/building/${data.id}`

          return path
        } else {
          return null
        }
      } else if (module === 'asset') {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule('asset', pageTypes.OVERVIEW) || {}

          if (name) {
            return this.$router.resolve({
              name,
              params: {
                viewname: 'all',
                id: data.id,
              },
            }).href
          }
        } else {
          return `/app/at/assets/all/${data.id}/overview`
        }
      } else {
        return `/app/ca/modules/${module}/all/${data.id}/summary`
      }
    },
    jumpToModule(module, data) {
      if (data && module) {
        let url = this.getModuleURl(module, data)
        url && this.$router.push({ path: url })
      }
    },
    jumpToSpace(data) {
      if (data) {
        let url = ''
        if (data.siteId > -1 && data.spaceId > -1) {
          let parentPath = this.findRoute()
          if (parentPath) {
            url = `${parentPath}/site/${data.siteId}/space/${data.spaceId}`
          }
        }

        !isEmpty(url) && this.$router.push({ path: url })
      }
    },
    jumpToBuilding(data) {
      if (data) {
        let url = ''
        if (data.siteId > -1 && data.spaceId > -1) {
          let parentPath = this.findRoute()
          if (parentPath) {
            url = `${parentPath}/site/${data.siteId}/building/${data.spaceId}`
          }
        }
        !isEmpty(url) && this.$router.push({ path: url })
      }
    },
    jumpToAlarms(alarmId) {
      if (!this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        if (alarmId) {
          if (Array.isArray(alarmId)) {
            if (alarmId.length === 1) {
              this.$router.push({
                path: '/app/fa/faults/summary/' + alarmId[0],
              })
            } else if (alarmId.length > 1) {
              let alarmIdList = []
              for (let id of alarmId) {
                alarmIdList.push(id + '')
              }
              let filters = {
                id: {
                  operatorId: 36,
                  value: alarmIdList,
                },
              }

              this.$router.push({
                path: '/app/fa/faults/active',
                query: {
                  search: encodeURIComponent(JSON.stringify(filters)),
                },
              })
            }
          } else {
            this.$router.push({
              path: '/app/fa/faults/summary/' + alarmId,
            })
          }
        }
      }
    },
    jumpToAlarmsNew(alarmId) {
      if (alarmId && !this.$helpers.isLicenseEnabled('NEW_ALARMS')) {
        if (Array.isArray(alarmId)) {
          if (alarmId.length === 1) {
            this.$router.push({
              path: '/app/fa/faults/active/newsummary/' + alarmId[0],
            })
          } else if (alarmId.length > 1) {
            let alarmIdList = []
            for (let id of alarmId) {
              alarmIdList.push(id + '')
            }
            let filters = {
              id: {
                operatorId: 36,
                value: alarmIdList,
              },
            }

            this.$router.push({
              path: '/app/fa/faults/all',
              query: {
                search: JSON.stringify(filters),
                includeParentFilter: true,
              },
            })
          }
        } else {
          this.$router.push({
            path: '/app/fa/faults/all/newsummary/' + alarmId,
          })
        }
      }
    },
    jumpToViewList(filters, moduleName, newTab, viewName = 'all') {
      if (filters && moduleName) {
        if (isWebTabsEnabled()) {
          let { name } = findRouteForModule(moduleName, pageTypes.LIST)

          if (name) {
            let routePath = this.$router.resolve({
              name,
              params: { viewname: viewName },
              query: {
                search: JSON.stringify(filters),
                includeParentFilter: true,
              },
            }).href

            if (newTab) {
              window.open(routePath, '_blank')
              this.$emit('showUrl', false)
            } else {
              this.$router.push({ path: routePath })
            }
          }
        } else {
          let url = this.$constants.MODULE_TO_LIST_URL[moduleName]
          if (!url) {
            url = `/app/ca/modules/${moduleName}/${viewName}`
          }
          url = url.replaceAll('all', viewName)
          if (newTab) {
            let fullUrl = `${url}?search=${encodeURIComponent(
              JSON.stringify(filters)
            )}&includeParentFilter=true`
            window.open(fullUrl, '_blank')
            this.$emit('showUrl', false)
          } else {
            this.$router.push({
              path: url,
              query: {
                search: JSON.stringify(filters),
                includeParentFilter: true,
              },
            })
          }
        }
      }
    },
    jumpToSummary(moduleName, id, filters) {
      if (isWebTabsEnabled()) {
        if (moduleName) {
          let { name } = findRouteForModule(moduleName, pageTypes.OVERVIEW)
          let query = {}

          if (filters) {
            query = {
              search: JSON.stringify(filters),
              includeParentFilter: true,
            }
          }

          if (name) {
            this.$router.push({ name, params: { viewname: 'all', id }, query })
          }
        }
      } else {
        if (filters && moduleName) {
          let url = this.$constants.MODULE_TO_SUMMARY_URL[moduleName].replace(
            '${id}',
            id
          )
          this.$router.push({
            path: url,
            query: {
              search: JSON.stringify(filters),
              includeParentFilter: true,
            },
          })
        } else if (moduleName) {
          let url = this.$constants.MODULE_TO_SUMMARY_URL[moduleName].replace(
            '${id}',
            id
          )
          this.$router.push({
            path: url,
          })
        }
      }
    },
  },
}
