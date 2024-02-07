<script>
import SSO from 'pages/setup/sso'

export default {
  extends: SSO,
  methods: {
    getSSOType() {
      return 'sso'
    },
    getUpdateUrl() {
      return '/v2/setup/sso/update'
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
                  sso: {
                    id: this.ssoData.id,
                  },
                }
                console.log('delete called')
                this.$http
                  .post('/v2/setup/sso/delete', ssoObj)
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
    changeSSO() {
      this.disabled = true
      let ssoData = {
        sso: {
          isActive: this.isEnabled,
        },
      }
      this.$http.post('/v2/setup/sso/update', ssoData).then(response => {
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
    downloadMetaUrl() {
      return '/api/v2/setup/sso/downloadMetadata'
    },
    getSSOStatus(state) {
      this.ssoStatusLoading = true
      console.log(state)
      this.loading = true
      this.$http
        .get('/v2/setup/sso/get')
        .then(response => {
          console.log(response)
          if (
            response.status == 200 &&
            response.data != null &&
            response.data.sso == null
          ) {
            this.ssoStatusLoading = false
          } else if (
            response.status == 200 &&
            response.data != null &&
            response.data.sso != null
          ) {
            this.isEnabled = response.data.sso.isActive
            this.ssoData.spAcsURL = response.data.spAcsURL
            this.ssoData.spEntityId = response.data.spEntityId
            this.ssoData.spMetadataURL = response.data.spMetadataURL
            if (response.data.sso.SSOConfig) {
              this.ssoData.certificate = response.data.sso.SSOConfig.certificate
              this.ssoData.entityId = response.data.sso.SSOConfig.entityId
              this.ssoData.loginUrl = response.data.sso.SSOConfig.loginUrl
              this.ssoData.logoutUrl = response.data.sso.SSOConfig.logoutUrl
            }
            this.ssoData.id = response.data.sso.id
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
