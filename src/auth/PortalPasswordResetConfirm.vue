<script>
import homeMixin from 'src/PortalTenant/auth/portalHomeMixin'
import FaciloPasswordResetConfirm from './FacilioPasswordResetConfirm'

export default {
  mixins: [homeMixin],
  extends: FaciloPasswordResetConfirm,
  created() {
    this.loading = true
    this.loadPublicInfo().finally(() => (this.loading = false))
  },
  methods: {
    routeToLogin() {
      this.$router.replace({ name: 'login' })
    },
  },
}
</script>
<style>
body {
  background-color: #f0f3f4;
  color: #58666e;
}
.error {
  color: red;
}
.tcenter {
  text-align: center;
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
<style>
@import '../assets/styles/portal.css';
@import '../assets/styles/portalform.css';
@import '../assets/styles/white-theme.css';
@import '../assets/styles/black-theme.scss';
@import '../assets/styles/helper.scss';
@import '../assets/styles/c3.css';
</style>
