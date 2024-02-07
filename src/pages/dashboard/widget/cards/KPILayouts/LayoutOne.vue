<template>
  <div class="dragabale-card height100 fcu-list-view">
    <div class="fcu-table-header row">
      <div class="fcu-table-name col-10 text-center fcu-table-name1">
        {{ $t('panel.layout.prediction') }}
      </div>
    </div>
    <div v-if="loading" class="shimmer-frame">
      <div class="assetcard-shimmer shine"></div>
    </div>
    <div v-else>
      <div class="fcu-list-container">
        <table>
          <tr
            class="fcu-list-header"
            style="background: #fff;border-bottom: solid 1px #e6ecf3;"
          >
            <th>
              <div>{{ $t('panel.layout.name') }}</div>
            </th>
            <th>
              <div>{{ $t('panel.layout.breach') }}</div>
            </th>
          </tr>
          <tbody>
            <tr v-for="key in Object.keys(result.result)" :key="key">
              <td class="fcu-td pointer">
                <div class="fcu-name">
                  {{ getkey[key] }}
                </div>
              </td>
              <td class="fcu-td pointer">
                <div class="fcu-spacename pointer">
                  {{ result.result[key] }} {{ $t('panel.layout.days') }}
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: ['widget', 'config'],
  data() {
    return {
      devMode: false,
      result: '',
      loading: false,
      getkey: {
        airflowcoillifetimeprediction: 'Airflow Coil Lifetime Prediction',
        bagfilterlifetimeprediction: 'Bag Filter Lifetime Prediction',
        chwflowlifetimeprediction: 'CHW flow Lifetime Prediction',
        exhaustfanmotordriveendlifetimeprediction:
          'Exhaust Fan Motor Drive End Lifetime Prediction',
        exhaustfanmotornondriveendvibrationlifetimeprediction:
          'Exhaust Fan Motor Non Drive End Lifetime Prediction',
        prefilterlifetimeprediction: 'Pre Filter Lifetime Prediction',
        returnfilterlifetimeprediction: 'Return Filter Lifetime Prediction',
        strainerlifetimeprediction: 'Strainer Lifetime Prediction',
        supplyfanmotordriveendvibrationlifetimeprediction:
          'Supply Fan Motor Drive End Vibration Lifetime Prediction',
        supplyfanmotornondriveendvibrationlifetimeprediction:
          'Supply Fan Motor Non Drive End Vibration Lifetime Prediction',
      },
      params: {
        workflow: {
          expressions: [
            {
              name: 'exhaustfanmotornondriveendvibrationlifetimeprediction',
              actualName: 'test',
              fieldName:
                'exhaustfanmotornondriveendvibrationlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'airflowcoillifetimeprediction',
              actualName: 'test',
              fieldName: 'airflowcoillifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'bagfilterlifetimeprediction',
              actualName: 'test',
              fieldName: 'bagfilterlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'chwflowlifetimeprediction',
              actualName: 'test',
              fieldName: 'chwflowlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'exhaustfanmotordriveendlifetimeprediction',
              actualName: 'test',
              fieldName: 'exhaustfanmotordriveendlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'prefilterlifetimeprediction',
              actualName: 'test',
              fieldName: 'prefilterlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'returnfilterlifetimeprediction',
              actualName: 'test',
              fieldName: 'returnfilterlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'strainerlifetimeprediction',
              actualName: 'test',
              fieldName: 'strainerlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'supplyfanmotordriveendvibrationlifetimeprediction',
              actualName: 'test',
              fieldName: 'supplyfanmotordriveendvibrationlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
            {
              name: 'supplyfanmotornondriveendvibrationlifetimeprediction',
              actualName: 'test',
              fieldName: 'supplyfanmotornondriveendvibrationlifetimeprediction',
              aggregateString: '[0]',
              aggregate: null,
              moduleName: 'asset',
              criteria: {
                pattern: '(1)',
                conditions: {
                  '1': {
                    fieldName: 'id',
                    value: '969283',
                    columnName: 'Assets.ID',
                    operatorId: 9,
                    isResourceOperator: false,
                    parseLabel: null,
                    valueArray: [],
                    operatorLabel: '=',
                    active: true,
                    operatorsDataType: {
                      dataType: 'NUMBER',
                      displayType: 'NUMBER',
                    },
                    isSpacePicker: false,
                  },
                },
                resourceOperator: false,
              },
            },
          ],
          workflowUIMode: 1,
        },
        staticKey: 'kpiMultiResultCard',
      },
    }
  },
  mounted() {
    this.loadCardData()
  },
  computed: {
    getStrinfyData() {
      return JSON.stringify(this.params)
    },
  },
  methods: {
    loadCardData() {
      let self = this
      let params = null
      // if (this.widget && (this.widget.id > -1)) {
      //   params = {
      //     widgetId: self.widget.id
      //   }
      // }
      // else {
      //   params = this.params
      //   }
      params = this.params
      this.loading = true
      self.$http
        .post('dashboard/getCardData', params)
        .then(function(response) {
          self.result = response.data.cardResult
          self.loading = false
        })
        .catch(function(error) {
          self.loading = false
        })
    },
  },
}
</script>
<style>
.fcu-table-name1 {
  text-align: center !important;
}
</style>
