<template>
  <div class="fc-setup-loader">
    <slot name="setupLoading"></slot>
  </div>
</template>
<style lang="scss">
.fc-setup-loader {
  width: 100%;
  height: 80vh;
  background: #fff;
  box-shadow: 0 3px 7px 0 rgba(233, 233, 226, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
</style>
