<template>
  <div class="height100 p30 pT20 sidebar-contents">
    <!-- <p
      class="f13 line-height20 empty-greyH-bold flex-grow mT30"
    >Select a state or a transition to edit its properties or drag and drop to create transitions between states</p>-->
    <p class="f18 line-height20 flex-grow mB0">{{ stateFlow.name }}</p>
    <hr class="separator-line mT20 mB20" />
    <div class="mT20">
      <p class="f12 fc-text-pink fw6 flex-grow sidebar-empty-txt uppercase mb5">
        Available States
      </p>
      <p
        data-v-df13d97a
        class="f13 line-height20 empty-greyH-bold flex-grow sidebar-empty-txt"
      >
        Drag and drop exisiting states into the stateflow or create a new state
      </p>
      <div class="mR30 mB30 mT20">
        <el-input
          :autofocus="true"
          suffix-icon="el-icon-search"
          placeholder="Search available states"
          v-model="searchText"
          class="fc-input-full-border-select2"
        ></el-input>
      </div>
      <div class="available-state-container d-flex mT10">
        <div class="state-obj new-state" @click="addState">+ Add State</div>
      </div>
      <div
        :class="[
          'available-state-container d-flex flex-wrap needs-scroll',
          !$validation.isEmpty(searchText) ? 'has-search' : '',
        ]"
      >
        <div
          v-for="state in sortedStates"
          :key="state.id"
          :class="['state-obj', state.hasMatch && 'has-match']"
          draggable="true"
          @dragstart="setDraggedState(state)"
          @dblclick="editAction(state)"
        >
          {{ state.displayName }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import sortBy from 'lodash/sortBy'
import { isEmpty } from '@facilio/utils/validation'

export default {
  props: [
    'usedStates',
    'unusedStates',
    'selectedState',
    'editAction',
    'stateFlow',
    'addState',
  ],
  data() {
    return {
      searchText: '',
    }
  },
  computed: {
    sortedStates() {
      let { searchText } = this
      let states = [...this.unusedStates]

      states = sortBy(states, s => s.displayName.toLowerCase())

      states = states.map(s => {
        s.hasMatch = !isEmpty(searchText)
          ? s.displayName.toLowerCase().startsWith(searchText.toLowerCase())
          : false

        return s
      })

      return states
    },
  },
  methods: {
    setDraggedState(state) {
      this.$emit('update:selectedState', state)
    },
  },
}
</script>
<style lang="scss" scoped>
.sidebar-contents {
  width: 100%;
}
.sidebar-empty-txt {
  line-height: 17px;
}

.available-state-container {
  margin: 10px -6px 0;
}

.needs-scroll {
  overflow-y: scroll;
  height: calc(100vh - 330px);
  align-content: flex-start;
}

.state-obj {
  min-width: 80px;
  height: 30px;
  margin: 6px 7px;
  padding: 6px 22px;
  border: 1px solid #3995e2;
  border-radius: 4px;
  font-size: 13px;
  color: #3995e2;
  text-align: center;
  cursor: move;
  transition: box-shadow 0.2s ease-in-out;

  &:hover {
    box-shadow: 5px 5px 7px 0px #bfc7ce87;
  }

  &.new-state {
    color: #fff;
    background-color: #3995e2;
    border-radius: 25px;
    cursor: pointer;
  }
}

.available-state-container.has-search .state-obj {
  &.has-match {
    color: #fff;
    background-color: #3995e2;
    animation: glow 1s forwards ease-in-out;
  }
}

@keyframes glow {
  0% {
    box-shadow: 0 0 8px 3px rgba(57, 149, 226, 0);
  }
  50% {
    box-shadow: 0 0 8px 3px rgba(57, 149, 226, 0.3);
  }
  100% {
    box-shadow: 0 0 8px 3px rgba(57, 149, 226, 0);
  }
}
</style>
