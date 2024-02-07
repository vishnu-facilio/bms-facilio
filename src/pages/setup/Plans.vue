<template>
  <div class="row white">
    <div class="col-3">
      <ul class="pricing p-yel">
        <li>
          <img src="http://bread.pp.ua/n/settings_y.svg" alt="" />
          <big>Starter</big>
        </li>
        <li>Preventive Maintenance</li>
        <li>Space based tracking</li>
        <li>Work order management</li>
        <li>Asset based tracking</li>
        <li>Mobile App</li>
        <li>
          <h3>$399</h3>
          <span>per month</span>
        </li>
        <li>
          <button
            data-cb-type="checkout"
            data-cb-plan-id="facil-blossom"
            data-cb-addons_id_0="staff-basic"
            data-cb-addons_quantity_0="5"
            data-cb-addons_id_1="buildings"
            data-cb-addons_quantity_1="10"
          >
            Join now
          </button>
          <!-- <button @click="Starter()">Join Now</button> -->
        </li>
      </ul>
    </div>

    <div class="col-3">
      <ul class="pricing p-red">
        <li>
          <img src="http://bread.pp.ua/n/settings_r.svg" alt="" />
          <big>Professional</big>
        </li>
        <li>Starter Features +</li>
        <li>Occupant portal</li>
        <li>Tenant zones &amp; portal</li>
        <li>Space consoles</li>
        <li>BMS Integration</li>
        <li>
          <h3>$1699</h3>
          <span>per month</span>
        </li>
        <li>
          <button
            data-cb-type="checkout"
            data-cb-plan-id="professional"
            data-cb-addons_id_0="staff-professional"
            data-cb-addons_quantity_0="5"
            data-cb-addons_id_1="building-professional"
            data-cb-addons_quantity_1="10"
          >
            Join now
          </button>
          <!-- <button @click="Professional()">Join Now</button> -->
        </li>
      </ul>
    </div>

    <div class="col-3">
      <ul class="pricing p-blue">
        <li>
          <img src="http://bread.pp.ua/n/settings_b.svg" alt="" />
          <big>Enterprise</big>
        </li>
        <li>Professional Features +</li>
        <li>Single sign on</li>
        <li>Integration</li>
        <li>Custom branding</li>
        <li>ML based Analytics on Energy</li>

        <li>
          <h3>&nbsp;</h3>
          <span>Contact for price</span>
        </li>
        <li>
          <button
            data-cb-type="checkout"
            data-cb-plan-id="professional"
            data-cb-addons_id_0="staff-professional"
            data-cb-addons_quantity_0="5"
            data-cb-addons_id_1="building-professional"
            data-cb-addons_quantity_1="10"
          >
            Join now
          </button>
          <!-- <button @click= "Enterprise()">Join Now</button> -->
        </li>
      </ul>
    </div>
  </div>
  <!-- /row -->
</template>

<script>
// import payment from 'store/modules/payment'
export default {
  data() {
    return {
      temp: {
        cbInstance: window.Chargebee.getInstance(),
      },
    }
  },
  mounted() {
    window.Chargebee.registerAgain()
    let cart = this.temp.cbInstance.getCart()
    let email = this.$account.user.email
    let customer = { email: email }
    cart.setCustomer(encodeURIComponent(customer))
    this.temp.cbInstance.setCheckoutCallbacks(function(cart) {
      return {
        loaded: function() {},
        close: function() {},
        success: function(hostedPageId) {},
        step: function(value) {},
      }
    })
  },
  methods: {},
}
</script>

<style>
@import url(
  https://fonts.googleapis.com/css?family=Lato:100,
  300,
  400,
  700,
  900,
  100italic,
  300italic,
  400italic,
  700italic,
  900italic
);

body {
  background: #111d27;
  color: #111;
  min-width: 320px;
  font-size: 16px;
  font-weight: 300;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -webkit-backface-visibility: hidden;
  position: relative;
  padding: 50px 20px;
}

.pricing {
  text-align: center;
  border: 1px solid #f0f0f0;
  color: #777;
  font-size: 14px;
  padding-left: 0;
  margin-bottom: 30px;
  font-family: 'Lato';
}
.pricing img {
  display: block;
  margin: auto;
  width: 32px;
}
.pricing li:first-child,
.pricing li:last-child {
  padding: 20px 13px;
}
.pricing li {
  list-style: none;
  padding: 13px;
}
.pricing li + li {
  border-top: 1px solid #f0f0f0;
}
.pricing big {
  font-size: 32px;
}
.pricing h3 {
  margin-bottom: 0;
  font-size: 36px;
}
.pricing span {
  font-size: 12px;
  color: #999;
  font-weight: normal;
}
.pricing li:nth-last-child(2) {
  padding: 30px 13px;
}
.pricing button {
  width: auto;
  margin: auto;
  font-size: 15px;
  font-weight: bold;
  border-radius: 50px;
  color: #fff;
  padding: 9px 24px;
  background: #aaa;
  opacity: 1;
  transition: opacity 0.2s ease;
  border: none;
  outline: none;
}
.pricing button:hover {
  opacity: 0.9;
}
.pricing button:active {
  box-shadow: inset 0px 2px 2px rgba(0, 0, 0, 0.1);
}
/* pricing color */
.p-green big,
.p-green h3 {
  color: #4c7737;
}
.p-green button {
  background: #4c7737;
}
.p-yel big,
.p-yel h3 {
  color: #ffbb42;
}
.p-yel button {
  background: #ffbb42;
}
.p-red big,
.p-red h3 {
  color: #e13c4c;
}
.p-red button {
  background: #e13c4c;
}
.p-blue big,
.p-blue h3 {
  color: #3f4bb8;
}
.p-blue button {
  background: #3f4bb8;
}
</style>
