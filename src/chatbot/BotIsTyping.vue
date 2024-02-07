<template>
  <div class="bot-typing">
    <div class="dot" style="animation-delay: .2s;;"></div>
    <div class="dot" style="animation-delay: .4s;"></div>
    <div class="dot" style="animation-delay: .6s;"></div>
  </div>
</template>

<script>
export default {}
</script>

<style lang="scss" scoped>
.bot-typing {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 40px;
  width: 60px;
  background-color: #f2f3f4;
  border-radius: 20px;
  margin-bottom: 25px;
}

.dot {
  margin-right: 3px;
  border-radius: 50%;
  animation: dot-blink 1.5s ease-in-out infinite;
  border: 1px solid transparent;
  animation-delay: 0.4s;
  width: 8px;
  height: 8px;
  background-color: gray;
}
@keyframes dot-blink {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}
</style>
