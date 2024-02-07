import http from 'src/util/http'
import dummyResp from './allPMResp.js'
import { PlannerTypes } from 'src/pages/workorder/CalendarConstants'
import { isEmpty } from '@facilio/utils/validation'
const PlannerApiCalls = {
  async getStaffJobs(range, siteId, buildingId, teamId, woFilters) {
    //fetch all workorders for a PM for given duration

    let url = '/v2/workorders/newcalendar/list/all'

    let params = {
      // page: 1,
      perPage: -1,

      filters: {
        ...woFilters,
        // pm: {
        //   operatorId: 36,
        //   value: [this.pm.id+""],
        // },
        //siteId: { operatorId: 36, value: [siteId + ''] },
        sourceType: {
          operatorId: 9,
          value: ['5'],
        },
        scheduledStart: {
          operatorId: 20,
          value: [range.start.toString(), range.end.toString()],
        },
      },

      orderBy: 'createdTime',
      orderType: 'desc',
      includeParentFilter: true,
    }

    if (!isEmpty(siteId) && siteId > 0) {
      let { filters } = params
      filters = {
        ...filters,
        siteId: { operatorId: 36, value: [siteId + ''] },
      }
      params.filters = filters
    }
    if (buildingId) {
      params.filters.resource = { operatorId: 38, value: [buildingId + ''] }
    }
    if (teamId) {
      params.filters.assignmentGroup = { operatorId: 36, value: [teamId + ''] }
    }

    url +=
      '?perPage=' +
      params.perPage +
      '&filters=' +
      encodeURI(JSON.stringify(params.filters)) +
      '&orderBy=' +
      params.orderBy +
      '&orderType=' +
      params.orderType +
      '&includeParentFilter=' +
      params.includeParentFilter

    try {
      let resp = await http.get(url)
      return resp
    } catch (ex) {
      console.log('error while fetching staff Planner jobs', ex)
    }
  },

  async deleteWorkOrder(...workorderIDs) {
    let url = 'workorder/delete'
    let reqJSON = { id: [...workorderIDs] }

    try {
      let resp = await http.post(url, reqJSON)
      return resp.data
    } catch (e) {
      console.log('error in workorder delete API', e)
      throw Error('error in workorder delete API')
    }
  },
  async getBookings(spaceList, calEvent) {
    console.log('bookings api call')
    let url = 'v2/reservations/view/all?'
    let filterJson = {}
    filterJson.space = {
      operator: 'is',
      value: spaceList.map(e => e.id + ''),
    }
    filterJson.scheduledStartTime = {
      operator: 'is before',
      value: [calEvent.end + ''],
    }
    filterJson.scheduledEndTime = {
      operator: 'is after',
      value: [calEvent.start + ''],
    }
    url += `filters=${encodeURIComponent(JSON.stringify(filterJson))}`
    let resp = await http.get(url)
    return resp.data
  },
  async filteredSpaceList(spaceFilter) {
    let url = `space?`
    let filterJson = {
      reservable: { operator: 'is', value: ['true'] },
    }
    filterJson.site = { operator: 'is', value: ['' + spaceFilter.siteId] }

    // spaceType:{operator:"=",value:["4"]},

    //url+=`siteId=${spaceFilter.siteId}`

    if (spaceFilter.buildingId) {
      filterJson.building = {
        operator: 'is',
        value: ['' + spaceFilter.buildingId],
      }
      //url+=`&buildingId=${spaceFilter.buildingId}`
    }
    if (spaceFilter.floorId) {
      filterJson.floor = { operator: 'is', value: ['' + spaceFilter.floorId] }
      // url+=`&floorId=${spaceFilter.floorId}`
    }

    url += `filters=${encodeURIComponent(JSON.stringify(filterJson))}`

    let resp = await http.get(url)
    return resp.data
  },
  async getAllJobs(assetSpaceFilter, woFilter, plannerEvent, plannerTypeEnum) {
    let params = {
      siteId: assetSpaceFilter.siteId || null,
      buildingId: assetSpaceFilter.buildingId || null,
      categoryId: assetSpaceFilter.assetCategoryId || null, //clearable el select puts '' as empty option
      floorId: assetSpaceFilter.floorId || null,
      dateRange: {
        startTime: plannerEvent.start,
        endTime: plannerEvent.end,
      },
      plannerType: PlannerTypes[plannerTypeEnum],
    }
    if (woFilter) {
      params.filterJson = woFilter
    }
    let url = '/v2/pmplanner/jobs'

    try {
      let resp = await http.post(url, params)
      console.log('recieved response is ', resp)
      return resp.data.result.result
      // console.log('dummy resp', dummyResp)
      // return dummyResp.result.result
    } catch (e) {
      console.log('Error in fetching PM planner jobs', e)
      throw Error('Error in fetching PM planner jobs', e)
    }
  },

  async assignStaff(woId, userId) {
    let url = '/v2/pmplanner/jobs/update'
    let workorder = {
      id: woId,
    }
    workorder.assignedTo = {
      id: userId,
    }
    let resp = await http.post(url, { workorder }).then(resp => {
      console.log('update  WO assignee response', resp.data)
    })
  },
}

export default PlannerApiCalls
