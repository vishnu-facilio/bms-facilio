<template>
  <div class="error-page bg-light column items-center no-wrap">
    <div
      class="error-card column items-center justify-center no-wrap position-relative"
    >
      <inline-svg
        src="svgs/error-ico"
        iconClass="text-center"
        style="padding-left: 100px;"
      ></inline-svg>
      <div class="text-center position-absolute content-area">
        <div class="f40 bold mB20">Not Supported</div>
        <div class="error-sub-txt2">
          You seem to have blocked cookies or your browser doesn't seem to
          support them. Make sure that cookies are allowed and your browser is
          updated.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import isCookieEnabled from 'lib/check-cookie'

export default {
  props: ['app'],
  beforeRouteEnter(to, from, next) {
    if (isCookieEnabled()) {
      next(vm => {
        return vm.app ? next(`/${vm.app}`) : next('/')
      })
    }
  },
}
</script>

<style scoped lang="scss">
.error-page {
  height: 100vh;
  width: 100%;
  overflow: hidden;
  background: #ffffff;

  .content-area {
    padding: 0px 20px;
    max-width: 620px;
  }
  .error-card {
    width: 100%;
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    max-width: 934px;
  }
  .error-card > i {
    font-size: 5rem;
  }
  .error-sub-txt2 {
    font-size: 16px;
    color: #181b3b;
    letter-spacing: 0;
    text-align: center;
    line-height: 19px;
    padding-bottom: 20px;
  }
}
</style>
