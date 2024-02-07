<template>
  <div class="p20">
    <div class="">
      <div class="fc-text-pink11 text-uppercase">ALARM DETAILS</div>
      <div class="pT20">
        <div class="fc-blue-label">ALARM MESSAGE</div>
        <div class="fc-black-13 text-left pT5 line-height24">
          {{ details.name }}
        </div>
      </div>
      <div class="pT20">
        <div class="fc-blue-label">PROBLEM</div>
        <div
          class="fc-black-13 text-left pT5 line-height24"
          v-html="details.problem"
        ></div>
      </div>
      <div class="pT20">
        <div class="fc-blue-label bold">Possible Causes</div>
        <div
          class="fc-black-13 text-left pT5 line-height24"
          v-html="details.possible_causes"
        ></div>
      </div>
      <div class="pT20">
        <div class="fc-blue-label bold">Possible Solution</div>
        <div
          class="fc-black-13 text-left pT5 line-height24"
          v-html="details.possible_solution"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['details'],
}
</script>

<style lang="scss">
.ecm-option-container {
  padding: 10px 50px 10px 30px;
  flex-direction: row;
  .ecm-reporting-label {
    color: #4273e9;
  }
}
</style>
