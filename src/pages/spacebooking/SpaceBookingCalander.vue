<template>
  <vue-cal
    class="vuecal--blue-theme"
    style="height: 400px; width: 300px"
    :disable-views="['week', 'month', 'year', 'years']"
    today-button
  ></vue-cal>
</template>

<script>
// Documentation: https://antoniandre.github.io/vue-cal

import VueCal from 'vue-cal'
import 'vue-cal/dist/vuecal.css'

export default {
  components: { VueCal },
}
</script>

<style>
html {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  size: 10px;
}
</style>
