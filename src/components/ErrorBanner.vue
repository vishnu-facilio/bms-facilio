<template>
  <div>
    <div class="error-block" id="error" v-if="hasError">
      <p class="error-txt">
        <i class="fa fa-info-circle" aria-hidden="true"></i>
        {{ errorMessage ? errorMessage : 'Please fill all mandatory fields.' }}
      </p>
      <i class="el-icon-close error-close-icon" @click="closeBanner"></i>
    </div>
  </div>
</template>
<script>
export default {
  props: ['error', 'errorMessage'],
  computed: {
    hasError: {
      get() {
        return this.error
      },
      set(value) {
        this.$emit('update:error', value)
      },
    },
  },
  methods: {
    closeBanner() {
      this.hasError = false
      this.$emit('update:errorMessage', '')
    },
  },
}
</script>
<style>
.error-block {
  width: 100%;
  background-color: #f9ecec;
  border: none;
  height: 45px;
  padding-top: 8px;
  padding-bottom: 8px;
  position: relative;
  top: 0;
  right: 0;
  left: 0;
}
.error-block .error-txt {
  font-size: 13px;
  font-weight: 500;
  font-style: normal;
  letter-spacing: 0.4px;
  text-align: center;
  color: #e16868;
}
.error-block .error-close-icon {
  position: absolute;
  right: 17px;
  top: 12px;
  color: #de5656;
  font-size: 18px;
  font-weight: 300;
  cursor: pointer;
}
</style>
