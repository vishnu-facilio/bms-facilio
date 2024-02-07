<template>
  <div>
    <el-dialog
      :visible="visibility"
      :fullscreen="false"
      :before-close="cancel"
      :append-to-body="true"
      key="1"
      custom-class="fc-animated slideInRight fc-dialog-form fc-dialog-right setup-dialog100 setup-dialog fc-web-form-dialog width65"
    >
      <div class="attendance-transaction-heading pL30 pT20">
        {{ $t('people.attendance.attendance_transaction') }}
      </div>
      <div class="pL30 pR30 fc-text-pink-break-heading pB10 mT20">
        {{ $options.filters.toDateFormat(attendanceObj.day, 'DD MMM YYYY') }}
      </div>
      <el-row
        v-if="!loading.transaction"
        class="attendance-transcation-dialog-row"
      >
        <el-col :span="10">
          <div class="fc-black-11 text-uppercase fwBold text-left">
            {{ $t('common.products.check_in') }}
          </div>
        </el-col>
        <el-col :span="10">
          <div class="fc-black-11 text-uppercase fwBold text-left">
            {{ $t('common.products.check_out') }}
          </div>
        </el-col>
        <el-col :span="4">
          <div class="fc-black-11 text-uppercase fwBold text-left">
            {{ $t('common.products.break_time') }}
          </div>
        </el-col>
      </el-row>
      <div v-if="loading.transaction" class="mT50 fc-webform-loading">
        <spinner :show="loading.transaction" size="80"></spinner>
      </div>
      <div v-else-if="!loading.transaction" class="scrollable pB250 pL0 pR0">
        <el-row v-if="transactionList && transactionList.length === 0">
          <el-col :span="24">
            <div class="attendance-transaction-no-data">
              <img src="~statics/noData-light.png" width="100" height="100" />
              <div class="mT10 label-txt-black f14">
                {{ $t('common.products.no_transaction_available') }}
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row
          v-else
          v-for="(transaction, index) in transactionList"
          :key="index"
          class="pL30 pR30 pT20 pB30 attendance-transcation-dialog-row-bb"
        >
          <el-col :span="10" class="pR120">
            <div class="flex-middle justify-content-space">
              <div v-if="transaction.checkin" class="fc-green-label14 bold">
                {{
                  convertSecondsToTimehhMMa(transaction.checkin.transactionTime)
                }}
              </div>
              <div v-if="transaction.checkin" class="fc-black-13">
                <InlineSvg
                  src="svgs/attendance-web"
                  v-if="transaction.checkin.sourceType === 1"
                  iconClass="icon icon-xs vertical-middle"
                ></InlineSvg>
                <InlineSvg
                  src="svgs/attendance-mobile"
                  v-if="transaction.checkin.sourceType === 2"
                  iconClass="icon icon-xs vertical-middle"
                ></InlineSvg>
                <span class="mL10">{{
                  sourceTypeEnumFormatter(transaction.checkin.sourceType)
                }}</span>
              </div>
            </div>
            <!-- <div
              class="fc-black-13 fw3 pT10 text-left"
            >Manappakkam Main Road, Ambedkar Nagar, Manapakkam, Chennai</div>-->
            <div v-if="transaction.checkin" class="pT10 fc-grey4-12 f13 fw3">
              {{ transaction.checkin.remarks }}
            </div>
          </el-col>
          <el-col :span="10" class="pR120">
            <div
              v-if="transaction.checkout"
              class="flex-middle justify-content-space"
            >
              <div class="fc-red-txt3-14 bold">
                {{
                  convertSecondsToTimehhMMa(
                    transaction.checkout.transactionTime
                  )
                }}
              </div>
              <div class="fc-black-13">
                <InlineSvg
                  src="svgs/attendance-web"
                  v-if="transaction.checkin.sourceType === 1"
                  iconClass="icon icon-xs vertical-middle"
                ></InlineSvg>
                <InlineSvg
                  src="svgs/attendance-mobile"
                  v-if="transaction.checkin.sourceType === 2"
                  iconClass="icon icon-xs vertical-middle"
                ></InlineSvg>
                <span class="mL10">{{
                  sourceTypeEnumFormatter(transaction.checkout.sourceType)
                }}</span>
              </div>
            </div>
            <!-- <div
              class="fc-black-13 fw3 pT10 text-left"
            >Manappakkam Main Road, Ambedkar Nagar, Manapakkam, Chennai</div>-->
            <div v-if="transaction.checkout" class="pT10 fc-grey4-12 f13 fw3">
              {{ transaction.checkout.remarks }}
            </div>
          </el-col>
          <el-col :span="4">
            <div
              v-if="
                transaction.breakObject && transaction.breakObject.timeTaken > 0
              "
              class="fc-red-txt3-14 bold"
            >
              {{
                convertSecondsToTimeHHMM(
                  transaction.breakObject.timeTaken / 1000
                ) + ' Hrs'
              }}
            </div>
            <div
              v-if="
                transaction.breakObject && transaction.breakObject.timeTaken < 0
              "
              class="fc-red-txt3-14 bold"
            >
              --:--
            </div>
            <div
              v-if="transaction.breakObject"
              class="fc-black-13 pT10 text-left"
            >
              <InlineSvg
                src="svgs/attendance-break-time"
                iconClass="icon icon-sm vertical-middle"
              ></InlineSvg>
              <span class="mL10">{{
                transaction.breakObject.breakId.name
              }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="modal-dialog-footer">
        <el-button class="modal-btn-cancel width100" @click="cancel()">{{
          $t('common._common.close')
        }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import moment from 'moment-timezone'
export default {
  props: ['visibility', 'attendanceObj'],
  data() {
    return {
      loading: {
        transaction: false,
      },
      attendanceTransactionList: null,
      breakTransactionList: null,
      transactionList: [],
    }
  },
  mounted() {
    this.loadAttendanceTransactionList()
  },
  computed: {},
  methods: {
    cancel() {
      this.$emit('update:visibility', false)
    },
    loadAttendanceTransactionList() {
      if (this.attendanceObj && this.attendanceObj.id) {
        let filter = {
          attendance: {
            value: [this.attendanceObj.id + ''],
            operatorId: 36,
          },
        }
        this.loading.transaction = true
        let promises = []
        promises.push(
          this.$http.get(
            '/v2/attendanceTransaction/view/all?filters=' +
              encodeURIComponent(JSON.stringify(filter)) +
              '&includeParentFilter=true'
          )
        )
        promises.push(
          this.$http.get(
            '/v2/breakTransaction/view/all?&filters=' +
              encodeURIComponent(JSON.stringify(filter)) +
              '&includeParentFilter=true'
          )
        )
        Promise.all(promises)
          .then(response => {
            if (response[0].data.responseCode === 0) {
              this.attendanceTransactionList =
                response[0].data.result.attendanceTransaction
            } else {
              this.$message.errror(response[0].data.message)
              this.loading.transaction = false
            }
            if (response[1].data.responseCode === 0) {
              this.breakTransactionList =
                response[1].data.result.breakTransaction
            } else {
              this.$message.errror(response[1].data.message)
              this.loading.transaction = false
            }
            if (
              response[0].data.responseCode === 0 &&
              response[1].data.responseCode === 0
            ) {
              this.loading.transaction = false
              this.orderAttendanceTransactionData()
            }
          })
          .catch(error => {
            this.$message.error(error)
            this.loading.transaction = false
          })
      }
    },
    loadBreakTransactionForTime(time, type) {
      let tempBreakObj
      if (time && type === 'stop') {
        tempBreakObj = this.breakTransactionList.find(i => i.stopTime === time)
        if (tempBreakObj) {
          return tempBreakObj
        }
      } else if (time && type === 'start') {
        tempBreakObj = this.breakTransactionList.find(i => i.startTime === time)
        if (tempBreakObj) {
          return tempBreakObj
        }
      }
    },
    orderAttendanceTransactionData() {
      let arrayEntryIndexCheckout
      for (let transaction of this.attendanceTransactionList) {
        if (
          this.transactionList.length === 0 &&
          (transaction.transactionType === 2 ||
            transaction.transactionType === 4)
        ) {
          this.transactionList.push({
            checkin: null,
            checkout: transaction,
            breakObject: null,
          })
        }
        if (transaction.transactionType === 1) {
          this.transactionList.push({
            checkin: transaction,
            checkout: null,
            breakObject: null,
          })
          arrayEntryIndexCheckout = this.transactionList.length - 1
        } else if (transaction.transactionType === 4) {
          this.transactionList.push({
            checkin: transaction,
            checkout: null,
            breakObject: null,
          })
          arrayEntryIndexCheckout = this.transactionList.length - 1
        } else if (transaction.transactionType === 2) {
          this.transactionList[arrayEntryIndexCheckout].checkout = transaction
        } else if (transaction.transactionType === 3) {
          this.transactionList[
            arrayEntryIndexCheckout
          ].breakObject = this.loadBreakTransactionForTime(
            transaction.transactionTime,
            'start'
          )
          this.transactionList[arrayEntryIndexCheckout].checkout = transaction
        }
      }
      this.loading.transaction = false
    },
    getDateTime(val) {
      let value = val.transactionTime
      return !value || value === -1
        ? ''
        : this.$options.filters.formatDate(value)
    },
    convertSecondsToTimehhMMa(val) {
      return moment(val).format('hh:mm a')
    },
    convertSecondsToTimeHHMM(val) {
      if (val > 0) {
        return moment()
          .startOf('day')
          .seconds(val)
          .format('HH:mm')
      } else {
        return '00:00'
      }
    },
    sourceTypeEnumFormatter(val) {
      if (val === 1) {
        return 'Web'
      } else if (val === 2) {
        return 'Mobile'
      }
    },
  },
}
</script>
