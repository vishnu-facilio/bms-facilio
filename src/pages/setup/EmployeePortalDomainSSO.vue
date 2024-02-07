<script>
import SSO from './sso'

export default {
  extends: SSO,
  methods: {
    getSSOType() {
      return 'domainSSO'
    },
    downloadMetaUrl() {
      return '/api/v2/setup/portalsso/metadata?appDomainType=employeeportal'
    },
    getUpdateUrl() {
      return '/v2/setup/portalsso/update?appDomainType=employeeportal'
    },
    handleCommand(command) {
      if (command == 'edit') {
        this.configureSSO()
      } else if (command == 'delete') {
        this.$dialog
          .confirm({
            title: 'Delete Single Sign-On Settings',
            message:
              'This will permanently delete your Single Sign-On Settings. Continue?',
            rbDanger: true,
            rbLabel: 'Delete',
          })
          .then(value => {
            if (value) {
              if (this.ssoData.id != null) {
                let ssoObj = {
                  appDomainType: 'employeeportal',
                }
                this.$http
                  .post('/v2/setup/portalsso/delete', ssoObj)
                  .then(response => {
                    console.log(response)
                    if (response.status == 200) {
                      this.showContent = false
                      this.$emit('Your SSO configs are deleted')
                    }
                  })
              }
            }
          })
      }
    },
    updateCreateUserStatus() {
      this.disabled = true
      let ssoData = {
        createUserStatus: this.isCreateUserEnabled,
      }
      this.$http
        .post(
          '/v2/setup/portalsso/updateCreateUserStatus?appDomainType=employeeportal',
          ssoData
        )
        .then(response => {
          let { status } = response
          let { errorMessage } = response.data
          if (errorMessage) {
            this.$message.error(errorMessage)
          } else if (status === 200 && this.isEnabled) {
            this.$message({
              type: 'success',
              message: 'SSO enabled successfully',
            })
          }
        })
    },
    changeSSO() {
      this.disabled = true
      let ssoData = {
        domainSSO: {
          isActive: this.isEnabled,
        },
        appDomainType: 'employeeportal',
      }
      this.$http.post('/v2/setup/portalsso/update', ssoData).then(response => {
        console.log(response)
        let { status } = response
        let { errorMessage } = response.data
        if (errorMessage) {
          this.$message.error(errorMessage)
        } else if (status === 200 && this.isEnabled) {
          this.$message({
            type: 'success',
            message: 'SSO enabled successfully',
          })
        } else if (status === 200 && !this.isEnabled) {
          this.$message({
            type: 'success',
            message: 'SSO disabled successfully',
          })
        } else {
          console.log('Something went wrong')
        }
        this.disabled = false
      })
    },
    getSSOStatus(state) {
      this.ssoStatusLoading = true
      console.log(state)
      this.loading = true

      this.$http
        .get('/v2/setup/portalsso/get', {
          params: {
            appDomainType: 'employeeportal',
          },
        })
        .then(response => {
          console.log(response)
          if (
            response.status == 200 &&
            response.data != null &&
            response.data.domainSSO == null
          ) {
            this.ssoStatusLoading = false
          } else if (
            response.status == 200 &&
            response.data != null &&
            response.data.domainSSO != null
          ) {
            this.isEnabled = response.data.domainSSO.isActive
            this.isCreateUserEnabled = response.data.domainSSO.isCreateUser
            this.ssoData.spAcsURL = response.data.spAcsURL
            this.ssoData.spEntityId = response.data.spEntityId
            this.ssoData.spMetadataURL = response.data.spMetadataURL
            this.ssoData.showSSOLink = response.data.domainSSO.showSSOLink
            if (response.data.domainSSO.SSOConfig) {
              this.ssoData.certificate =
                response.data.domainSSO.SSOConfig.certificate
              this.ssoData.entityId = response.data.domainSSO.SSOConfig.entityId
              this.ssoData.loginUrl = response.data.domainSSO.SSOConfig.loginUrl
              this.ssoData.logoutUrl =
                response.data.domainSSO.SSOConfig.logoutUrl
            }

            this.ssoData.id = response.data.domainSSO.id
            this.loading = false
            this.ssoStatusLoading = false
            if (this.ssoData.loginUrl) {
              this.showContent = true
            }
            if (state == 'onedit') {
              this.showDialog = true
            }
          }
          console.log('--------sso data-----')
          console.log(this.ssoData)
        })
        .catch(err => {
          console.log('something wrong')
          console.log(err)
          this.loading = false
        })
    },
  },
}
</script>
