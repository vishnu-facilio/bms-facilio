<template>
  <div class="error-page">
    <div class="error-card">
      <inline-svg
        src="svgs/error-ico"
        iconClass="icon icon-934"
        style="margin-left: 80px;"
      ></inline-svg>
      <div class="text-center position-absolute max-width400px">
        <div class="error-txt">
          <div>{{ errorCode }}</div>
        </div>
        <div class="error-sub-txt2">
          <div>{{ 'An error occurred while processing your request.' }}</div>
        </div>
        <el-button type="primary" round @click="goHome" class="error-btn">
          {{ $t('auth.error.go-home') }}
        </el-button>
      </div>
    </div>
  </div>
</template>
<script>
import ErrorMixin from './ErrorMixin'
import { emitEvent } from 'src/webViews/utils/mobileapps'

export default {
  mixins: [ErrorMixin],
  methods: {
    goHome() {
      emitEvent('goToHome', { goToHome: 'ok' })
      emitEvent('reloadConnectedApp', { reloadConnectedApp: 'true' })
    },
  },
}
</script>
<style lang="scss" scoped>
.error-page {
  overflow: hidden;
  background: #f4f4f4;

  .error-card {
    width: 100%;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;

    > i {
      font-size: 5rem;
    }
  }

  .error-btn {
    border: solid 0.7px #f6f2ff;
    background: #4e36a6 !important;
    border-radius: 20px;
    font-size: 14px;
    color: #ffffff;
    letter-spacing: 0;
    text-align: center;
    text-transform: uppercase;
    font-weight: 600;
    letter-spacing: 0.8px;
    border-radius: 30px !important;
    padding: 14px 30px !important;
    line-height: 16px;
    margin: 0px;

    &:hover {
      border: none;
      border-left: 1px solid transparent;
      border-right: 1px solid transparent;
      opacity: 0.85;
    }
  }

  .margin-right-20 {
    margin-right: 20px;
  }

  .error-txt {
    font-size: 80px;
    color: #181b3b;
    font-weight: 600;
    letter-spacing: 2px;
    text-align: center;
    padding-bottom: 10px;
  }

  .error-sub-txt {
    font-size: 28px;
    color: #181b3b;
    letter-spacing: 0;
    text-align: center;
    font-weight: 500;
    padding-bottom: 10px;
  }

  .error-sub-txt2 {
    font-size: 14px;
    color: #181b3b;
    letter-spacing: 0;
    text-align: center;
    line-height: 19px;
    padding-bottom: 20px;
    word-break: break-word;
  }

  .icon.icon-934 {
    height: 500px;
    width: 930px;
  }

  .logged-in-user {
    margin-left: 5px;
    color: #8ca1ad;
  }
}
</style>
