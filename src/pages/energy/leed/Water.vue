<template>
  <div class="scrollable">
    <div class="row p25 le-background-color-2">
      <div
        class="col-12 col-sm-12 col-lg-12 col-md-12 text-left le-content-label le-cursor-pointer"
      >
        <span class="">Water Managment</span>
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
          <span v-if="metersLoading">Loading...</span>
          <button
            v-else
            v-for="(meter, index) in meters"
            :key="`meter-${index}`"
            @click="loadDataMeter(meter)"
            class="le-button le-button3 border-left-radious uppercase"
          >
            {{ meter.name }}
          </button>

          <button
            @click="$refs.DataManualModel.open()"
            class="le-button le-button3 border-right-radious"
          >
            <q-icon name="add" class=" leed-color-1 " size="18px"></q-icon>
          </button>
          <!--button @click="addSampleData" class="le-button le-button3 border-left-radious uppercase">AddSample</button -->
        </div>
        <div id="utility-more-options" class="hide">
          <ul class="le-ul">
            <li class="le-li-header uppercase p10">NEW UTILITY PROVIDER</li>
            <li class="le-li-content text-left p5 le-cursor-pointer">
              Login Credentials
            </li>
            <li class="le-li-content text-left p5 le-cursor-pointer">
              Enter Manually
            </li>
            <li class="le-li-content text-left p5 le-cursor-pointer">
              Upload Bills
            </li>
          </ul>
        </div>
        <div class="p20"></div>
        <div class="row">
          <!-- header -->
        </div>
        <div class="row text-center">
          <span v-if="consumpDataLoading">Loading...</span>
          <div
            v-else
            v-for="(consumpData, index) in consumpData"
            :key="`consumpData-${index}`"
            class="col-1 col-sm-1 col-lg-1 col-md-1 le-card-md le-bill-card "
          >
            <div class="le-bill-card-header ">
              {{ consumpData.Month | monthName }}
            </div>
            <div class="le-bill-card-content">
              {{ consumpData.Consumption }} gal
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
          <span>ADD UTILITY BILLS MANUALLY </span>
          <img
            class="le-buuton-icon pull-right le-cursor-pointers"
            src="~assets/settings.svg"
          />
        </div>
      </div>
      <!--     <div class="row le-model-content">
    <q-input v-model="" class="ellipsis" type="number" placeholder="0.0" suffix="Kw" />
    </div> -->
      <div class="row le-model-content">
        <div class="le-scroll-row">
          <div class="col-sm-12 col-lg-12 le-input-content">
            <div class="row">
              <div
                class="col-sm-4 col-lg-4 col-4  self-center"
                style="align-self: center;"
              >
                <div class="le-datarange uppercase">
                  Name
                </div>
              </div>
              <div class="col-sm-8 col-lg-8 col-8">
                <div class="le-datarange">
                  <q-input
                    v-model="providername"
                    class="ellipsis"
                    type="text"
                    placeholder=""
                    suffix=""
                  />
                </div>
              </div>
            </div>
          </div>
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
              <div class="col-sm-7 col-lg-7 col-7">
                <div class="le-datarange">
                  <q-datetime-range
                    v-model="b.range"
                    class="full-width ellipsis"
                    placeholder="--"
                  />
                </div>
              </div>
              <div class="col-sm-4 col-lg-4 col-4">
                <div class="le-datarange">
                  <q-input
                    v-model="b.value"
                    class="ellipsis"
                    type="number"
                    placeholder="0.0"
                    suffix="gal"
                  />
                </div>
              </div>
              <div class="col-sm-1 col-lg-1 col-1">
                <div class="le-datarange">
                  <img
                    @click="delRow(index)"
                    class="le-bin"
                    src="~assets/bin-1.svg"
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
    <div class="p30"></div>
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
      openLeedId: -1,
      meters: [],
      metersLoading: true,
      consumpData: [],
      providername: null,
      consumpDataLoading: true,
    }
  },
  mounted() {
    console.log('mounted......')
    this.loadLeedConfiguration()
  },
  computed: {
    currentLeedId() {
      return this.$route.params.id
    },
    currentLeed() {
      return this.$store.getters['leed/getLeedById'](
        parseInt(this.$route.params.id)
      )
    },
  },
  components: {
    QModal,
    QIcon,
    QInput,
    QDatetimeRange,
  },
  methods: {
    loadDataMeter(meter) {
      let self = this
      self.consumpDataLoading = true
      this.$http
        .get('/leed/energy/loaddataformeter?deviceId=' + meter.id)
        .then(function(response) {
          let resp = response.data
          console.log(resp)
          self.consumpData = response.data.consumptionArray
          self.consumpDataLoading = false
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    addSampleData() {
      this.$http
        .get('/leed/energy/addsample')
        .then(function(response) {
          let resp = response.data
          console.log(resp)
        })
        .catch(function(error) {
          console.log(error)
        })
    },
    loadLeedConfiguration() {
      let self = this
      console.log(this.$route.params.id)
      self.metersLoading = true
      let openLeedId = parseInt(this.$route.params.id)
      this.$http
        .get('/leed/energy/' + openLeedId + '?meterType=water')
        .then(function(response) {
          let resp = response.data
          console.log(resp)
          self.meters = response.data.meterList
          self.metersLoading = false
          if (self.meters.length) {
            self.loadDataMeter(self.meters[0])
          }
        })
        .catch(function(error) {
          console.log(error)
          self.metersLoading = false
        })
    },
    test() {
      console.log('clicked')
    },
    addRow() {
      let newRow = JSON.parse(JSON.stringify(this.bill))
      this.bills.push(newRow)
      console.log('newrow---------->', newRow)
    },
    delRow(index) {
      this.bills.splice(index, 1)
      console.log('del row-------->', index)
    },
    dataManualModelOpen() {
      this.bills = []
      this.addRow()
    },
    leCloseModel() {
      this.$refs.DataManualModel.close()
    },
    leModelSave() {
      let self = this
      console.log('bills ------------------>', this.bills)
      console.log('providername ------->', this.providername)
      console.log('LeedId ---->', this.currentLeed.leedId)
      console.log('buildingId --->', this.currentLeed.id)
      console.log('currentLeed --->', this.currentLeed)
      console.log('param.id ---->', this.$route.params.id)
      let consumptionJSONArray = []
      for (let key in this.bills) {
        let json = {}
        let stTime = Date.parse(this.bills[key].range.from)
        let enTime = Date.parse(this.bills[key].range.to)
        console.log('st --->', stTime)
        console.log('en -->', enTime)
        let enTime2 = enTime / 1000
        console.log('en 1000 --->', enTime2)
        json.startTime = Date.parse(this.bills[key].range.from) / 1000
        json.addedTime = Date.parse(this.bills[key].range.to) / 1000
        json.totalEnergyConsumptionDelta = this.bills[key].value
        consumptionJSONArray.push(json)
      }
      let postdata = {
        meterName: this.providername,
        leedID: this.currentLeed.leedId,
        consumptionJSONArray: consumptionJSONArray,
        buildingId: this.currentLeed.id,
        meterType: 'water',
      }
      self.metersLoading = true
      self.consumpDataLoading = true
      this.$http
        .post('/leed/energy/addmeterconsumptiondata', postdata)
        .then(function(response) {
          let resp = response.data
          console.log(resp)
          self.meters = response.data.meterList
          self.metersLoading = false
          if (self.meters.length) {
            self.loadDataMeter(self.meters[0])
          }
          self.consumpData = response.data.consumptionArray
          self.consumpDataLoading = false
        })
        .catch(function(error) {
          console.log(error)
          self.metersLoading = false
          self.consumpDataLoading = false
        })
      this.$refs.DataManualModel.close()
    },
  },
}
</script>
