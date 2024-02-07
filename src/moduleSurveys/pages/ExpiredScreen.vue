<!-- eslint-disable vue/no-bare-strings-in-template -->
<template>
  <div class="expiredscreen" v-if="isLoading">
    <spinner :show="isLoading" size="80"></spinner>
  </div>
  <div class="expiredscreen" v-else>
    <!-- <div class="closeicon" @click="closewindow()">
      <inline-svg src="svgs/ic-close"></inline-svg>
    </div> -->
    <div class="tq-area">
      <div class="company-logo mL30 mB20">
        <inline-svg src="svgs/survey/survey-expired" iconClass="icon icon-200 mL50"></inline-svg>
      </div>
      <div class="tq-text mB10">
        {{ $t('survey.expiry_message') }}
      </div>
      <div>
        <div class="closebutton" @click="closewindow()">
          <div class="close">{{ $t('survey.close') }}</div>
        </div>
      </div>
    </div>
    <div class="survey-footer-section">
      <div class="cpy-right">{{ $t('survey.copyright') }}</div>
    </div>
  </div>
</template>
<script>
import Spinner from '@/Spinner'

export default {
  name: 'Expiredscreen',
  components: {
    Spinner,
  },
  data() {
    return {
      isLoading: false,
    }
  },
  methods: {
    closewindow() {
      window.open('', '_self').close()
    },
  },
  props: ['companyLogoUrl'],
}
</script>
<style scoped>
.expiredscreen {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.closeicon {
  position: absolute;
  top: 10px;
  right: 10px;
}
.tq-area {
  margin: auto;
  display: flex;
  padding-left: 50px;
  flex-direction: column;
}
.company-logo img {
  margin-left: 55px;
}
.tq-text {
  font-size: 20px;
  font-weight: 500;
  color: #324056;
  letter-spacing: 0.7px;
  text-align: center;
}
.survey-footer-section {
  bottom: 0px;
  height: 50px;
  display: flex;
  flex-direction: column !important;
  justify-content: center;
  align-items: center;
  margin-bottom: 10px;
}
.cpy-right {
  font-size: 12px;
}
.close {
  width: 40px;
  height: 16px;
  font-family: Helvetica;
  font-size: 14px;
  font-weight: bold;
  font-stretch: normal;
  font-style: normal;
  line-height: 1.14;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
}
.closebutton {
  margin: 0 0 0 100px;
  width: 176px;
  height: 40px;
  padding: 13px 68px 11px;
  border-radius: 4px;
  background-color: #0074d1;
}
</style>
