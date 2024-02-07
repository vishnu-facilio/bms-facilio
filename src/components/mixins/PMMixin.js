import { mapState } from 'vuex'
import { isEmpty } from '@facilio/utils/validation'
import { API } from '@facilio/api'

const INITIAL_SCHEDULE = {
  times: ['00:00'],
  frequency: 1,
  skipEvery: -1,
  values: [1],
  frequencyType: 1,
  weekFrequency: -1,
  yearlyDayValue: null,
  monthValue: -1,
}
export default {
  INITIAL_SCHEDULE,
  created() {
    this.$store.dispatch('loadSites')
    this.$store.dispatch('loadBuildings')
  },
  computed: {
    ...mapState({
      sites: state => state.sites,
    }),
  },
  methods: {
    validateMultiSiteWO() {
      if (
        this.model.woData.woModel.hasOwnProperty('subject') &&
        !this.model.woData.woModel.subject
      ) {
        this.$message.error('Please enter the PM subject \n')
        return false
      }
      if (
        this.model.woData.woModel.hasOwnProperty('type') &&
        (!this.model.woData.woModel.type || !this.model.woData.woModel.type.id)
      ) {
        this.$message.error('Please enter the PM type \n')
        return false
      }
      return true
    },
    validateWO() {
      if (
        this.model.woData.woModel.hasOwnProperty('site') &&
        (!this.model.woData.woModel.site.id ||
          Number(this.model.woData.woModel.site.id) <= 0)
      ) {
        this.$message.error('Please enter the PM site \n')
        return false
      }
      if (
        this.model.woData.woModel.hasOwnProperty('subject') &&
        !this.model.woData.woModel.subject
      ) {
        this.$message.error('Please enter the PM subject \n')
        return false
      }
      if (
        this.model.woData.woModel.hasOwnProperty('type') &&
        (!this.model.woData.woModel.type || !this.model.woData.woModel.type.id)
      ) {
        this.$message.error('Please enter the PM type \n')
        return false
      }
      if (
        (this.model.woData.workOrderType === 'bulk' &&
          !this.model.woData.resourceList.length) ||
        (this.model.woData.workOrderType === 'single' &&
          !this.model.woData.singleResource)
      ) {
        this.$message.error(
          'There are no Spaces or Assets to create a work order \n'
        )
        return false
      }
      if (
        this.model.woData.workOrderType === 'bulk' &&
        !this.model.woData.resourceType
      ) {
        this.$message.error('Please select Category \n')
        return false
      }
      if (
        this.model.woData.workOrderType === 'bulk' &&
        this.model.woData.resourceType === 'ALL_FLOORS' &&
        !this.model.woData.selectedBuilding
      ) {
        this.$message.error('Please select a Building \n')
        return false
      }
      return true
    },
    validateTaskForm() {
      let validated = true

      let present = {}
      this.model.taskData.taskSections.forEach(ts => {
        ts.tasks.forEach(t => {
          if (!present[t.additionInfo.uniqueId]) {
            present[t.additionInfo.uniqueId] = true
          } else {
            validated = false
          }
        })
      })

      if (!validated) {
        this.$message.error(`Task Number cannot be duplicated\n`)
        return validated
      }

      this.model.taskData.taskSections.forEach(x => {
        if (!x.name || !x.name.trim()) {
          this.$message.error('Please enter Section Name \n')
          validated = false
        } else if (
          this.model.taskData.taskSections.filter(
            rt => rt.name.trim() === x.name.trim()
          ).length > 1
        ) {
          this.$message.error(`Section name cannot be duplicated\n`)
          validated = false
        }
        x.tasks.forEach(k => {
          let err = this.validateTask(k)
          if (err) {
            this.$message.error(err)
            validated = false
          }
        })
      })
      return validated
    },
    validateTask(task) {
      let errorMsg = ''
      if (!task.name) {
        errorMsg += 'Please specify the task subject \n'
      }
      if (task.enableInput) {
        if (parseInt(task.inputType) === 2) {
          if (parseInt(task.readingFieldId) < 1 || !task.readingFieldId) {
            errorMsg += 'Please select a specific reading type \n'
          }
        } else if (parseInt(task.inputType) === 4) {
          if (task.validation === 'safeLimit') {
            if (!task.minSafeLimit && !task.maxSafeLimit) {
              errorMsg += 'Please enter either minimum or maximum value \n'
            }
          }
        } else if (parseInt(task.inputType) === 5) {
          if (task.options.length < 2) {
            errorMsg += 'Please enter atleast two options\n'
          } else if (!task.options[0].name && !task.options[1].name) {
            errorMsg += 'Please enter atleast two options \n'
          }
        }
      }
      return errorMsg
    },
    validatePreRequestForm() {
      let validated = true
      if (this.model.preRequestData.allowNegativePreRequisite === 3) {
        this.model.preRequestData.approvers.forEach(x => {
          if (!x.sharingType || !x.approversId) {
            this.$message.error('Please select Approver \n')
            validated = false
          }
        })
      }
      if (validated) {
        if (this.model.preRequestData.preRequestSections.length > 0) {
          this.model.preRequestData.preRequestSections.forEach(x => {
            if (!x.name || !x.name.trim()) {
              this.$message.error('Please enter Section Name \n')
              validated = false
            }
            x.preRequests.forEach(k => {
              let err = this.validatePreRequest(k)
              if (err) {
                this.$message.error(err)
                validated = false
              }
            })
          })
        }
      }
      return validated
    },
    validatePreRequest(preRequest) {
      let errorMsg = ''
      if (!preRequest.name || preRequest.name === '') {
        errorMsg += 'Please specify the prerequisite subject \n'
      }
      // if ((!preRequest.options[0].name)||preRequest.options[0].name===''||(!preRequest.options[1].name)||preRequest.options[1].name==='') {
      //       errorMsg += 'Please enter options \n'
      //  }
      if (
        !preRequest.additionInfo.truevalue ||
        preRequest.additionInfo.truevalue === '' ||
        !preRequest.additionInfo.falsevalue ||
        preRequest.additionInfo.falsevalue === ''
      ) {
        errorMsg += 'Please enter options \n'
      }
      return errorMsg
    },
    resourceName() {
      if (this.model.woData.workOrderType === 'bulk') {
        if (
          !this.model.woData.selectedBuilding &&
          this.model.woData.woModel &&
          this.model.woData.woModel.site &&
          this.model.woData.woModel.site.id
        ) {
          let site = this.sites.find(
            i => String(i.id) === this.model.woData.woModel.site.id
          )
          if (site) {
            return site.name
          }
        }
        let buildingId = this.model.woData.selectedBuilding
        if (!isEmpty(this.buildingList)) {
          let building = this.buildingList.find(i => i.id === buildingId)
          if (building) {
            return building.name
          }
        }
      } else {
        return this.model.woData.spaceAssetDisplayName
      }
      return ''
    },
    canHandleSite(user, sites) {
      // if no accessibleSpace is defined, it means the user has access to
      // all sites in the org
      if (isEmpty(user.accessibleSpace)) {
        return true
      }
      // user can handle site if the chosen site is one of his accessible space
      for (let ix = 0; ix < sites.length; ix++) {
        if (user.accessibleSpace.includes(sites[ix])) {
          return true
        }
      }
      return false
    },
    async getScopedGroupsAndUsers(siteIds = null) {
      let params = {}
      let result = {}
      if (siteIds) {
        params.siteIds = siteIds
      }
      params.addAccessibleSpaces = true
      let { data, error } = await API.post(`v3/group/scopeBySite`, params)
      if (!isEmpty(data)) {
        let { groups, users } = data || {}
        result.users = users || []
        result.groups = groups || []
      }
      return result
    },
  },
}
