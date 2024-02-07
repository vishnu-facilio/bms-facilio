<template>
  <div>
    <div class="row p25 le-background-color-2">
      <div
        class="col-12 col-sm-12 col-lg-12 col-md-12 text-left le-content-label le-cursor-pointer"
      >
        <span class="">Waste Management</span>
      </div>
      <div v-if="Utility">
        <div class="col-6 col-sm-6 col-lg-6 col-md-6 text-left">
          <span class="le-content-sublabel"
            >Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent
            ut lobortis tortor, id sodales nibh.
          </span>
        </div>
        <div class="p30"></div>
        <div class="col-sm-12 col-md-12 col-lg-12 col-12">
          <div class="row le-card-md">
            <div
              class="col-4 col-sm-4 col-lg-4 col-md-4 p25 le-border-left le-cursor-pointer"
            >
              <div class="row">
                <div
                  class="col-3 col-sm-3 col-lg-3 col-md-3 text-center le-position-relative"
                >
                  <img class="le-card-icon" src="~assets/login.svg" />
                </div>
                <div class="col-9 col-sm-9 col-lg-9 col-md-9">
                  <div class="le-content-label-2 text-left">
                    Login Credentials
                  </div>
                  <div class="le-building-sublabel-2 text-left">
                    Provide your login details and we will fetch the information
                    for you
                  </div>
                </div>
              </div>
            </div>
            <div
              class="col-4 col-sm-4 col-lg-4 col-md-4 p25 le-border-left le-cursor-pointer"
              @click="$refs.DataManualModel.open()"
            >
              <div class="row">
                <div
                  class="col-3 col-sm-3 col-lg-3 col-md-3 text-center le-position-relative"
                >
                  <img class="le-card-icon" src="~assets/edit-square.svg" />
                </div>
                <div class="col-9 col-sm-9 col-lg-9 col-md-9">
                  <div class="le-content-label-2 text-left">
                    Enter Data Manually
                  </div>
                  <div class="le-building-sublabel-2 text-left">
                    Provide data manually for this year
                  </div>
                </div>
              </div>
            </div>
            <div class="col-4 col-sm-4 col-lg-4 col-md-4 p25 le-cursor-pointer">
              <div class="row">
                <div
                  class="col-3 col-sm-3 col-lg-3 col-md-3 text-center le-position-relative"
                >
                  <img class="le-card-icon" src="~assets/uploading-file.svg" />
                </div>
                <div class="col-9 col-sm-9 col-lg-9 col-md-9">
                  <div class="le-content-label-2 text-left">Upload Bills</div>
                  <div class="le-building-sublabel-2 text-left">
                    Upload bills and we will fetch the data for you
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="p5"></div>
        </div>
      </div>
      <div
        v-else
        class="col-12 col-sm-12 col-lg-12 col-md-12 text-left le-content-label le-cursor-pointer"
      >
        <div class="row">
          <button class="le-button le-button3 border-left-radious uppercase">
            Generated / Diverted
          </button>
        </div>
        <div class="p20"></div>
        <div class="row">
          <!-- header -->
        </div>
        <div class="row text-center">
          <div
            v-for="(waste, index) in wasteArray"
            :key="`waste-${index}`"
            class="col-1 col-sm-1 col-lg-1 col-md-1 le-card-md le-bill-card "
          >
            <div class="le-bill-card-header ">
              {{ waste.header }}
            </div>
            <div class="le-bill-card-content">
              {{ waste.content }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Enter Data Manually -->
    <q-modal
      ref="DataManualModel"
      position="right"
      content-classes="le-model-right"
      @open="dataManualModelOpen"
      @close="$refs.DataManualModel.close()"
    >
      <div class="row le-model-header p15">
        <div
          class="col-12 col-sm-12 col-md-12 pull-left uppercase le-color-pink "
        >
          <span>ADD UTILITY BILLS MANUALLY</span>
          <img
            class="le-buuton-icon pull-right le-cursor-pointers"
            src="~assets/settings.svg"
          />
        </div>
      </div>
      <div class="row le-model-content">
        <div class="le-scroll-row">
          <div
            v-for="(b, index) in bills"
            :key="`b-${index}`"
            class="col-sm-12 col-lg-12 le-input-content"
          >
            <div class="row">
              <div class="col-sm-8 col-lg-8 col-8">
                <div class="le-datarange">
                  <q-datetime-range
                    v-model="b.range"
                    class="full-width"
                    placeholder="--"
                  />
                </div>
              </div>
              <div class="col-sm-4 col-lg-4 col-4">
                <div class="le-datarange">
                  <q-input
                    v-model="b.value"
                    type="number"
                    placeholder="0.0"
                    suffix="Kw"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
        <div
          class="col-12 col-sm-12 col-lg-12 le-input-content le-add-row le-cursor-pointer"
          @click="addRow"
        >
          <span
            ><q-icon name="add circle outline"></q-icon
            ><font>Add Row</font></span
          >
        </div>
      </div>
      <div class="row le-model-footer">
        <div class="col-6 col-sm-6 col-lg-6">
          <button
            class="le-button2 uppercase le-button-color1"
            @click="leCloseModel"
          >
            cancel
          </button>
        </div>
        <div class="col-6 col-sm-6 col-lg-6">
          <button
            class="le-button2 uppercase le-button-color2"
            @click="leModelSave()"
          >
            save
          </button>
        </div>
      </div>
    </q-modal>
  </div>
</template>
<script>
import { QModal, QIcon, QInput, QDatetimeRange, date, Ripple } from 'quasar'
const { addToDate, subtractFromDate } = date,
  today = new Date(),
  past = subtractFromDate(today, { days: 5 }),
  future = addToDate(today, { days: 3 })
export default {
  directives: {
    Ripple,
  },
  data() {
    return {
      range1: {
        from: null,
        to: null,
      },
      bill: {
        range: {
          from: null,
          to: null,
        },
        value: null,
      },
      bills: [],
      today,
      past,
      future,
      number: 0,
      rippleEnabled: true,
      Utility: false,
      wasteArray: [],
    }
  },
  mounted() {
    console.log('mounted......')
    this.loadWasteData()
  },
  components: {
    QModal,
    QIcon,
    QInput,
    QDatetimeRange,
  },
  methods: {
    loadWasteData() {
      let self = this
      self.wasteArray.push({ header: 'April 2017', content: '10000 / 5000 Kg' })
      self.wasteArray.push({ header: 'May 2017', content: '10000 / 5000 Kg' })
      self.wasteArray.push({ header: 'June 2017', content: '10000 / 5000 Kg' })
      self.wasteArray.push({ header: 'July 2017', content: '10000 / 5000 Kg' })
      self.wasteArray.push({
        header: 'August 2017',
        content: '10000 / 5000 Kg',
      })
    },
    addRow() {
      let newRow = JSON.parse(JSON.stringify(this.bill))
      this.bills.push(newRow)
    },
    dataManualModelOpen() {
      this.bills = []
      this.addRow()
    },
    leCloseModel() {
      this.$refs.DataManualModel.close()
    },
    leModelSave() {
      this.$refs.DataManualModel.close()
    },
  },
}
</script>
