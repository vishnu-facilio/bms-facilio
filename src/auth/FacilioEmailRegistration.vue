<template>
  <div
    class="tcenter col-sm-6 col-sm-offset-3"
    style="width: 350px;margin: 0 auto;"
  >
    <div v-if="loading">
      Loading...
    </div>
    <div v-else-if="invitation.error">
      Link expired. Please contact your administrator to send the invitation
      again.
    </div>
    <!-- <div v-else-if ="model.username !== this.currentUser">
      Kindly login using different Browser. Or Logout the current user and then login
    </div> -->
    <div v-else-if="invitation.account_exists">
      <div v-if="userMismatchError" style="color:red">
        Kindly login using different Browser. Or Logout the current user and
        then login
      </div>
      <div v-else>
        <h2>JOIN</h2>
        <p>
          Join organization - <b>{{ invitation.orgname }}</b>
        </p>
        <button
          class="btn btn-primary"
          v-if="!saving"
          @click="inviteAccepted()"
        >
          JOIN
        </button>
        <button class="btn btn-primary" disabled v-else>Please wait...</button>
      </div>
    </div>
    <div v-else-if="!invitation.account_exists && !loading">
      <h2>JOIN</h2>
      <p>
        Join organization - <b>{{ invitation.orgname }}</b>
      </p>
      <form @submit.prevent="acceptInvite">
        <div class="form-group">
          <input
            type="text"
            disabled
            class="form-control"
            placeholder="Enter your email address"
            v-model="invitation.email"
            required
          />
        </div>
        <div class="form-group">
          <input
            type="password"
            class="form-control"
            placeholder="Create password"
            v-model="model.pass"
            required
            autocomplete="new-password"
          />
        </div>
        <button class="btn btn-primary" v-if="!saving">
          ACTIVATE AND LOG IN
        </button>
        <button class="btn btn-primary" disabled v-else>Please wait...</button>
      </form>

      <div class="footer">
        <small class="text-muted">Facilio Inc<br />© 2022</small>
      </div>
    </div>
  </div>
</template>

<script>
import http from 'util/http'

export default {
  data() {
    return {
      loading: true,
      invitation: {},
      model: {
        username: '',
        email: '',
        pass: '',
        companyname: '',
      },
      saving: false,
      token: -1,
      currentUser: '',
      userMismatchError: false,
    }
  },
  mounted() {
    this.loadInvitation()
  },
  methods: {
    loadInvitation() {
      this.loading = true
      http
        .get(
          'validateRegistration?inviteToken=' + this.$route.params.invitetoken
        )
        .then(response => {
          if (response.data.invitation) {
            this.loading = false
            let invitation = response.data.invitation
            if (invitation && invitation.accepted) {
              this.$router.replace({
                name: 'login',
                query: { username: invitation.email },
              })
            }
          }
        })
    },
    inviteAccepted() {
      this.token = this.$route.params.invitetoken

      http
        .post('/acceptInvite', { inviteToken: this.token })
        .then(response => {
          JSON.stringify(response)
          if (response.status === 200) {
            if (response.data.invitation.accepted) {
              this.$router.replace(this.$route.query.redirect || '/')
            }
          }
        })
        .catch(() => {
          this.$message.error(
            'Unable to join the organization. Please contact Support'
          )
        })
    },
  },
}
</script>
<style>
body {
  background-color: #f0f3f4;
  color: #58666e;
}
.tcenter {
  text-align: center;
}
.error {
  color: red;
}
.form-group input {
  outline: none;
  width: 100%;
  height: 48px;
  padding: 10px 15px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #555;
  background-color: #fff;
  background-image: none;
  border: 1px solid #d2d2d2;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  -webkit-transition: border-color ease-in-out 0.15s,
    -webkit-box-shadow ease-in-out 0.15s;
  -o-transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  margin-top: 8px;
}

button.btn.btn-primary {
  background-color: #7266ba;
  border-color: #7266ba;
  color: #ffffff !important;
  width: 100%;
  font-weight: 400;
  border-radius: 2px;
  outline: 0 !important;
  padding: 10px 16px;
  font-size: 18px;
  line-height: 1.3333333;
  border: 1px solid #7266ba;
  margin-top: 10px;
  cursor: pointer;
}

.forgot-links div {
  margin-top: 12px;
}

.forgot-links div a {
  color: #000;
  font-size: 13px;
}

.footer {
  padding-top: 30px;
}
</style>
