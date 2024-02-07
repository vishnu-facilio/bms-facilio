<template>
  <div>
    <div v-if="!$validation.isEmpty(expression)" style="display:flex;">
      <div v-for="(exp, index) in expression" :key="index">
        <a class="pointer" v-if="exp.id" @click="openAssetLink(exp.id)">{{
          exp.name
        }}</a>
        <span v-else class="bold pR5 pL5">{{ exp.op }}</span>
      </div>
    </div>
    <div v-else>---</div>
  </div>
</template>

<script>
import { isEmpty } from '@facilio/utils/validation'
import util from 'util/util'

export default {
  props: ['assetDetails'],

  mounted() {
    this.getExpression(this.assetDetails.childMeterExpression)
  },
  data() {
    return {
      expression: [],
    }
  },
  methods: {
    getExpression(expression) {
      if (!isEmpty(expression)) {
        let assetIds = expression.split(/[^0-9]/).filter(arr => !isEmpty(arr))
        let param = { filters: { id: { operatorId: 9, value: assetIds } } }

        this.$util.loadAsset(param).then(({ assets }) => {
          this.expression = expression
            .split(/(\d+)*(\()*(\))*(\+)*(\-)*/)
            .filter(arr => !isEmpty(arr))

          this.expression = this.expression.map(exp => {
            if (!isNaN(exp)) {
              let asset = assets.find(asset => asset.id === Number(exp))
              return { id: asset.id, name: asset.name }
            } else {
              return { op: exp }
            }
          })
        })
      }
    },
    openAssetLink(id) {
      this.$router.push({
        path: '/app/at/assets/all/' + id + '/overview',
      })
    },
  },
}
</script>
