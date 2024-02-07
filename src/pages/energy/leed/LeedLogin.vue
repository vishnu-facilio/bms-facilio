<template>
  <div class="fc-form-container">
    <div class="form-header">Login into Leed ARC Console</div>
    <form method="post" @submit.prevent="loginIntoLeed" class="fc-form pT15">
      <div class="row">
        <div class="col-lg-4 col-md-4">
          <div class="form-input">
            <label for="current_password">Arc UserName</label>
            <input class="text required" v-model="login.email" type="text" />
          </div>
          <div class="form-input">
            <label for="password">Password</label>
            <input
              class="text required"
              v-model="login.password"
              type="password"
              autocomplete="off"
            />
          </div>
        </div>
        <div class="col-lg-12 col-md-12 form-footer">
          <div class="button-container">
            <input
              class="btn btn--primary"
              name="commit"
              type="submit"
              value="Login"
            />
          </div>
        </div>
      </div>
    </form>
  </div>
</template>
<script>
export default {
  data() {
    return {
      login: {
        email: null,
        password: null,
      },
    }
  },
  methods: {
    loginIntoLeed() {
      let self = this
      this.$http
        .post('/leed/arcloginaction', {
          arcUserName: this.login.email,
          arcPassword: this.login.password,
        })
        .then(function(response) {
          let resp = response.data
          console.log(resp)
          self.$emit('onlogin', true)
        })
    },
  },
}
</script>
