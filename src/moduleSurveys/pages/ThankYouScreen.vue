<template>
  <div class="thankyouscreen" v-if="isLoading">
    <spinner :show="isLoading" size="80"></spinner>
  </div>
  <div class="thankyouscreen" data-testid="thankyouscreen" v-else>
    <div class="tq-area">
      <div class="company-logo mL30 mB20">
        <inline-svg
          src="svgs/survey/thank-you"
          iconClass="icon icon-200 mL50"
        ></inline-svg>
      </div>
      <div class="tq-text mB10" data-testid="tq-text">
        {{ $t('survey.thankyou_message') }}
      </div>
      <div>
        <div
          class="closebutton pointer"
          data-testid="closebutton"
          @click="closewindow()"
        >
          <div class="close">{{ $t('survey.close') }}</div>
        </div>
      </div>
    </div>
    <div class="survey-footer-section" data-testid="survey-footer-section">
      <div class="cpy-right">{{ $t('survey.copyright') }}</div>
    </div>
  </div>
</template>
<script>
import Spinner from '@/Spinner'

export default {
  name: 'ThankYouScreen',
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
      if (window.opener) {
        window.opener.postMessage(
          'Refresh current route',
          window.location.origin
        )
      }
      window.open('', '_self').close()
    },
  },
  props: ['companyLogoUrl'],
}
</script>
<style scoped>
.thankyouscreen {
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
