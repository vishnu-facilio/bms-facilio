<template>
  <div class="f-select">
    <label>{{ label }}</label>
    <v-select
      v-model="selectedValue"
      :on-change="setValue"
      :class="iconClass"
      :options="options"
    ></v-select>
  </div>
</template>
<script>
import vSelect from 'vue-select'
export default {
  props: ['options', 'icon', 'label', 'value'],
  components: { vSelect },

  data() {
    return {
      iconClass: 'fc-' + this.icon + '-icon',
      selectedValue: this.value ? this.value : null,
    }
  },
  methods: {
    setValue(val) {
      if (val) {
        this.$emit('input', val.value)
      } else {
        this.$emit('input', null)
      }
    },
  },
}
</script>
<style>
.v-select .open-indicator {
  display: inline-block;
  font: normal normal normal 14px/1 FontAwesome;
  font-size: inherit;
  text-rendering: auto;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
.v-select .dropdown-toggle {
  border: 0;
  border-bottom: 1px solid rgba(60, 60, 60, 0.26);
  border-radius: 0px;
}
.v-select.fc-user-icon .open-indicator:before {
  content: '\f007';
}
.v-select.fc-search-icon .open-indicator:before {
  content: '\f007';
}
.v-select.fc-calendar-icon .open-indicator:before {
  content: '\f073';
}
.v-select.fc-sortdown-icon .open-indicator:before {
  content: '\f0dd';
}
.v-select .open-indicator:before {
  transform: none !important;
  border-style: none;
  border: none !important;
}
.v-select .open-indicator {
  bottom: 5px !important;
}
.f-select {
  padding-top: 10px;
}
.v-select .selected-tag {
  background-color: transparent;
  border: 0;
  border-radius: 0px;
  line-height: 30px;
}
</style>
