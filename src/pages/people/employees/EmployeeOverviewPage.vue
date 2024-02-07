<script>
import CustomModuleOverview from 'pages/base-module-v2/Overview'
import { eventBus } from '@/page/widget/utils/eventBus'
import { isEmpty } from '@facilio/utils/validation'
import { mapState } from 'vuex'
import { API } from '@facilio/api'
import {
  isWebTabsEnabled,
  findRouteForModule,
  pageTypes,
} from '@facilio/router'

export default {
  extends: CustomModuleOverview,
  data() {
    return {
      updateUrl: `/v2/employee/update`,
      notesModuleName: 'peoplenotes',
      showChangeStatusDialog: false,
      selectedStatus: 1,
      attachmentsModuleName: 'peopleattachments',
      saving: false,
      allowedPortals: ['service', 'employee'],
      employee: null,
    }
  },
  computed: {
    isV3Api() {
      return true
    },
    ...mapState({
      roles: state => state.roles,
    }),
    moduleName() {
      return 'employee'
    },
    moduleStoreName() {
      return 'employee'
    },
    customModuleData() {
      return this.employee
    },
    showEdit() {
      let canShowEdit = this.$hasPermission(`employee:UPDATE`)
      let isNotLocked = this.isStateFlowEnabled
        ? !this.isRecordLocked && !this.isRequestedState
        : true
      return canShowEdit && isNotLocked
    },
    showPhotoField() {
      return false
    },
    showAvatar() {
      return false
    },
    photoFieldName() {
      return 'avatarId'
    },
    userRoles() {
      if (this.roles) {
        return this.roles
      }
      return []
    },
  },
  created() {
    this.$store.dispatch('loadRoles')
  },
  mounted() {
    eventBus.$on('refesh-parent-details', () => {
      this.loadCustomModuleData(true)
    })
  },
  methods: {
    transformFormData(returnObj, data) {
      returnObj['employee'] = {
        id: this.customModuleData.id,
      }
      if (data) {
        let { comment } = data
        if (!isEmpty(comment)) {
          data.comment = comment
          delete data.comment
        }
      }
      returnObj['employee'] = { ...returnObj['employee'], ...data }
      return returnObj
    },
    editCustomModuleData() {
      let { moduleName } = this
      let { id } = this
      if (isWebTabsEnabled()) {
        let { name } = findRouteForModule(moduleName, pageTypes.EDIT) || {}

        if (id && name) {
          this.$router.push({
            name,
          })
        }
      } else {
        this.$router.push({
          name: 'employee-edit',
          params: {
            moduleName,
            id,
          },
        })
      }
    },
    async saveUserAccess(userObj) {
      let { email, params } = userObj

      if (isEmpty(email)) {
        this.$message.error(
          this.$t('Please enter a valid email for enabling application access')
        )
        return
      }

      let { error } = await API.post('/v2/employee/update', params)

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
        throw new Error()
      } else {
        this.$message.success(this.$t('people.employee.update_success'))
        this.loadCustomModuleData(true)
      }
    },

    async savePortalUser(userObj) {
      let {
        id,
        email,
        isOccupantPortalAccess,
        employeePortalAccess,
        rolesMap,
        scopingId,
        permissionSets,
      } = userObj || {}
      let params = {
        employees: [
          {
            id,
            isOccupantPortalAccess,
            employeePortalAccess,
            rolesMap,
            scopingId,
            permissionSets,
          },
        ],
      }

      await this.saveUserAccess({ email, params })
    },
    async saveAppAccess(userData) {
      let { email, id, isAppAccess, rolesMap, scopingId, permissionSets } =
        userData || {}
      let params = {
        employees: [{ id, isAppAccess, rolesMap, scopingId, permissionSets }],
      }

      await this.saveUserAccess({ email, params })
    },
    async loadCustomModuleData(force = false) {
      this.isLoading = true

      let { id, moduleName } = this
      let { error, employee } = await API.fetchRecord(
        moduleName,
        { id },
        { force }
      )

      if (error) {
        this.$message.error(error.message || 'Error Occurred')
      } else {
        this.employee = employee
      }
      this.isLoading = false
    },
    showEmployeeactionDialog() {
      if (
        isEmpty(this.customModuleData.roleId) ||
        this.customModuleData.roleId <= 0
      ) {
        this.customModuleData.roleId = null
      }
      this.showEmployeeActionDialog = true
    },
  },
}
</script>
