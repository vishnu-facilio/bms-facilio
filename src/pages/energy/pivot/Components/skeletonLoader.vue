<template>
  <div v-if="preset == 'Table'">
    <div class="skeleton-table">
      <div class="skl-checkbox-column" v-if="showCheckBox">
        <div class="skl-checkbox-row" :style="'background-color:#f1f2f4;margin-bottom:0;'">
          <div class="skl-checkbox" :style="'background-color:white'">

        </div>
        </div>
        <div v-for="(row,index) in rows" :key="index" class="skl-checkbox-row"> 
          <div class="skl-checkbox shine-lines-effect">

          </div>
        </div>
      </div>
      <div
        v-for="(column, index) in columns"
        class="skeleton-table-column"
        :key="index"
        :style="`width:${calculatedColumnWidth}`"
      >
        <div class="skeleton-table-header">
          <div class="skl-header-cell"></div>
        </div>
        <div
          v-for="(row, index) in rows"
          class="skeleton-table-row"
          :key="index"
        >
          <div class="skl-row-cell">
            <div class="skl-shimmer shine-lines-effect"></div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else>
    <div
      v-for="index in rowCount"
      class="shine-lines-effect skeleton-text"
      :key="index"
      :style="`width:${width};height:${height};`"
    ></div>
  </div>
</template>
<script>
import { isEmpty } from '@facilio/utils/validation'

export default {
  computed: {
    calculatedColumnWidth() {
      if (!isEmpty(this.columnWidth)) {
        return this.columnWidth
      } else {
        return `${parseInt(100 / this.columns)}%`
      }
    },
  },
  props: {
    rowCount: {
      type: Number,
      default: 5,
    },
    showCheckBox:{
      type:Boolean,
      default:false
    },
    preset: {
      type: String,
      default: 'none',
    },
    columns: {
      type: Number,
      default: 3,
    },
    rows: {
      type: Number,
      default: 3,
    },
    columnWidth: {
      type: String,
    },
    width: {
      type: String,
      default: '100%',
    },
    height: {
      type: String,
      default: '100%',
    },
  },
}
</script>
<style lang="scss" scoped>

$base-color: #F4F4F4;
$shine-color: rgba(229,229,229,0.8);
$animation-duration: 2.0s;

@mixin background-gradient {
  background-image: linear-gradient(90deg, $base-color 0px, $shine-color 40px, $base-color 80px);
  background-size: 600px;
}
.skeleton {
  animation: skeleton-loading 1s linear infinite alternate;
}

.skeleton-table {
  background-color: #f1f2f4;
  display: flex;
  // padding-left: 30px;
  justify-content: space-around;
}
.skeleton-table-column {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.skeleton-table-header {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 45px;
}
.skl-header-cell {
  width: 80%;
  height: 60%;
  background-color: white;
  border-radius: 5px;
}
.skeleton-table-row {
  background-color: white;
  margin-bottom: 2px;
  display: flex;
  align-items: center;
  height: 45px;
  width: 100%;
}
.skl-checkbox-column{
  display: flex;
  flex-direction: column;
}
.skl-checkbox-row{
  height:45px;
  width:40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  margin-bottom: 2px;
}
.skl-checkbox{
  width: 15px;
  height: 15px;
  border-radius: 2px;
  background: #f1f2f4;
}

.skl-row-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}
.skl-shimmer {
  background-color: #f1f2f4;
  width: 80%;
  border-radius: 3px;
  height: 8px;
}
.shine-lines-effect{
  @include background-gradient;
  animation: shine-lines $animation-duration infinite ease-out;
}

@keyframes skeleton-loading {
  0% {
    background-color: hsl(200, 20%, 80%);
  }
  100% {
    background-color: hsl(200, 20%, 95%);
  }
}

@keyframes shine-lines{
    0% { background-position: -100px;}
     100% {background-position: 140px;}
}

.skeleton-text {
  width: 100%;
  margin-bottom: 0.5rem;
  border-radius: 3px;
}
</style>
