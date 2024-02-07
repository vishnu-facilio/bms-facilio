<template>
  <div
    class="tcenter col-sm-6 col-sm-offset-3"
    style="width: 350px;margin: 0 auto; padding:20px;"
    v-if="!signupDone"
  >
    <h2>
      <img
        v-if="this.model.brandName == 'BuildingsTalk'"
        src="~assets/buildingstalklogo.png"
        style="height:60px;"
      />
      <img
        class="fc-custom-logo fc-moro-logo"
        v-else-if="this.model.brandName == 'Moro'"
        src="~assets/facilio-moro.png"
      />
      <img v-else src="~assets/facilio-logo-black.svg" style="height:40px;" />
    </h2>
    <p>Try {{ this.model.brandName }} free for 30 days.</p>
    <div class="alert alert-danger" v-if="error">
      <p v-if="error" class="error">{{ error.message }}</p>
    </div>
    <form @submit.prevent="signup" class="signupForm">
      <div class="form-group">
        <input
          type="text"
          class="form-control"
          placeholder="Name"
          v-model="model.username"
          required
        />
      </div>
      <div class="form-group">
        <input
          type="text"
          class="form-control"
          placeholder="Work Email"
          v-model="model.emailaddress"
          required
        />
      </div>
      <div class="form-group">
        <input
          type="password"
          class="form-control"
          placeholder="Password"
          v-model="model.password"
          required
          autocomplete="new-password"
        />
      </div>
      <div class="form-group">
        <input
          type="text"
          class="form-control"
          placeholder="Company Name"
          v-model="model.companyname"
          required
        />
      </div>
      <div class="form-group" style="text-align: left;">
        <input
          type="text"
          class="form-control"
          placeholder="Domain Name"
          v-model="model.domainname"
          style="width: 165px"
          required
        />&nbsp;{{ rebrandInfo.domain }}
      </div>
      <div class="form-group">
        <el-select
          class="form-control"
          placeholder="Timezone"
          v-model="model.timezone"
          style="width:100%"
        >
          <el-option
            v-for="(timezone, index) in timezoneList"
            :key="index"
            :label="timezone.label"
            :value="timezone.value"
          >
          </el-option>
        </el-select>
      </div>
      <div class="form-group">
        <el-select
          class="form-control"
          placeholder="Language"
          v-model="model.language"
          style="width:100%"
        >
          <el-option
            v-for="(lang, index) in languages"
            :key="index"
            :label="lang.label"
            :value="lang.value"
          >
          </el-option>
        </el-select>
      </div>
      <div class="form-group">
        <input
          type="text"
          class="form-control"
          placeholder="Phone No"
          v-model="model.phone"
          required
        />
      </div>
      <button class="btn btn-primary" v-if="!creating">
        Get started for free
      </button>
      <button class="btn btn-primary" disabled v-else>Please wait...</button>
    </form>
    <div class="forgot-links">
      <div>
        <router-link :to="{ name: 'login' }"
          >Already have an account?</router-link
        >
      </div>
    </div>
    <div class="footer">
      <small class="text-muted"
        >{{ rebrandInfo.copyright.name }}<br />©
        {{ rebrandInfo.copyright.year }}</small
      >
    </div>
  </div>
  <div
    class="tcenter col-sm-6 col-sm-offset-3"
    style="width: 350px;margin: 0 auto; padding:20px;"
    v-else
  >
    <h2>
      <img
        v-if="this.model.brandName == 'BuildingsTalk'"
        src="~assets/buildingstalklogo.png"
        style="height:60px;"
      />
      <img
        class="fc-custom-logo"
        v-else-if="this.model.brandName == 'Moro'"
        src="~assets/facilio-moro.png"
        width="180"
        height="50"
      />
      <img v-else src="~assets/facilio-logo-black.svg" style="height:40px;" />
    </h2>
    <p>
      Confirmation link sent to your email account. Please check your email and
      confirm
    </p>
  </div>
</template>

<script>
import moment from 'moment-timezone'
import http from 'util/http'
import timezones from 'util/data/timezones'
import Constants from 'util/constant'
import { isEmpty } from '@facilio/utils/validation'

export default {
  computed: {
    rebrandInfo() {
      return window.rebrandInfo
    },
    queryParams() {
      let { $route } = this
      let { query = {} } = $route || {}
      return query
    },
  },
  data() {
    return {
      creating: false,
      subdomain: '',
      model: {
        username: '',
        emailaddress: '',
        password: '',
        companyname: '',
        domainname: '',
        phone: '+',
        timezone: '',
        language: 'en',
        brandName: 'Facilio',
      },
      error: null,
      timezoneList: timezones,
      languages: Constants.languages,
      signupDone: false,
    }
  },
  mounted() {
    this.model.timezone = moment.tz.guess()

    if (window.rebrandInfo && window.rebrandInfo.brandName) {
      this.model.brandName = window.rebrandInfo.brandName
    }
  },
  methods: {
    signup() {
      let { queryParams } = this
      let url = `/integ/apisignup`
      this.creating = true
      let formdata = {
        username: this.model.username,
        emailaddress: this.model.emailaddress.trim().toLowerCase(),
        cognitoId: 'test123',
        password: this.model.password,
        phone: this.model.phone,
        companyname: this.model.companyname,
        domainname: this.model.domainname,
        timezone: this.model.timezone,
        language: this.model.language,
      }
      if (!isEmpty(queryParams)) {
        formdata = {
          ...formdata,
          ...queryParams,
        }
      }
      http
        .post(url, formdata)
        .then(
          ({
            data: {
              jsonresponse: { errorcode, message },
            },
          }) => {
            if (errorcode && Number(errorcode) === 1) {
              if (message) {
                throw new Error(message)
              } else {
                throw new Error('Error Occurred')
              }
            } else {
              this.creating = false
              this.signupDone = true
            }
          }
        )
        .catch(({ message }) => {
          this.$message.error(message)
          this.creating = false
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
  border-radius: 2px;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.075);
  -webkit-transition: border-color ease-in-out 0.15s,
    -webkit-box-shadow ease-in-out 0.15s;
  -o-transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  transition: border-color ease-in-out 0.15s, box-shadow ease-in-out 0.15s;
  margin-bottom: 8px !important;
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
.signupForm input.el-input__inner {
  cursor: auto !important;
  outline: none;
  width: 100%;
  height: 48px;
  padding: 10px 15px;
  font-size: 13px;
  line-height: 1.42857143;
  color: #555;
  background-color: #fff;
  background-image: none;
  border: 1px solid #d3dbda;
  border-radius: 0;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
  margin-bottom: 8px;
  border-radius: 2px;
}
</style>
