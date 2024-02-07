<template>
  <div class="fc-employee-portal-home">
    <!-- header -->
    <el-header class="fc-employee-portal-home-header" height="65">
      <div class="fc-employee-heder-left">
        <div class="fc-show-result-txt">Showing results for</div>
        <el-dropdown class="pT5">
          <span class="el-dropdown-link flex-middle bold f16">
            <inline-svg
              src="svgs/employeePortal/building"
              iconClass="icon text-center icon-md mR5"
            ></inline-svg>
            Dropdown List<i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item>Action 1</el-dropdown-item>
            <el-dropdown-item>Action 2</el-dropdown-item>
            <el-dropdown-item>Action 3</el-dropdown-item>
            <el-dropdown-item disabled>Action 4</el-dropdown-item>
            <el-dropdown-item divided>Action 5</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
      <div class="fc-employee-heder-right">
        <div>
          <el-dropdown
            split-button
            type="primary"
            @click="handleClick"
            class="fc-employee-portal-add-btn"
          >
            Add
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>Action 1</el-dropdown-item>
              <el-dropdown-item>Action 2</el-dropdown-item>
              <el-dropdown-item>Action 3</el-dropdown-item>
              <el-dropdown-item>Action 4</el-dropdown-item>
              <el-dropdown-item>Action 5</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
        <div class="pL20 pR10">
          <el-badge :value="12" class="fc-portal-badge">
            <inline-svg
              src="svgs/employeePortal/bell"
              iconClass="icon text-center icon-xxxl mR5"
            ></inline-svg>
          </el-badge>
        </div>
        <div class="pR10">
          <el-dropdown>
            <span class="el-dropdown-link">
              <inline-svg
                src="svgs/employeePortal/profile-pic"
                iconClass="icon text-center icon-xlg"
              ></inline-svg>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>Action 1</el-dropdown-item>
              <el-dropdown-item>Action 2</el-dropdown-item>
              <el-dropdown-item>Action 3</el-dropdown-item>
              <el-dropdown-item disabled>Action 4</el-dropdown-item>
              <el-dropdown-item divided>Action 5</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <!-- main section -->
    <el-main class="fc-portal-main-sec">
      <div class="fc-employee-portal-main-sec">
        <div class="fc-employee-portal-first-sec">
          <div class="fc-employee-portal-first-sec-content">
            <div class="fc-portal-txt-16">In a Nutshell</div>
            <div class="fc-portal-txt-14">View all</div>
          </div>
          <!--first card section -->
          <el-row class="mT15 flex-middle" :gutter="10">
            <el-col :span="5">
              <el-card class="box-card fc-first-sec-card">
                <div class="flex-middle">
                  <div class="fc-pink-circle-img">
                    <inline-svg
                      src="svgs/employeePortal/hot-desk"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                  </div>
                  <div class="fc-portal-txt-16 pL10">Desk</div>
                </div>
                <div class="pT20">
                  <div class="fc-portal-txt-14">Title goes here…</div>
                </div>
                <div class="flex-middle pT5">
                  <div class="fc-portal-txt-14">1A-01</div>
                  <div class="pL10 pR10">|</div>
                  <div class="fc-portal-txt-14">5:00 PM to 6:00 PM</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="5">
              <el-card class="box-card fc-first-sec-card">
                <div class="flex-middle">
                  <div class="fc-yellow-circle-img">
                    <inline-svg
                      src="svgs/employeePortal/announcement"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                  </div>
                  <div class="fc-portal-txt-16 pL10">Desk</div>
                </div>
                <div class="pT20">
                  <div class="fc-portal-txt-14">Title goes here…</div>
                </div>
                <div class="flex-middle pT5">
                  <div class="fc-portal-txt-14">1A-01</div>
                  <div class="pL10 pR10">|</div>
                  <div class="fc-portal-txt-14">5:00 PM to 6:00 PM</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="5">
              <el-card class="box-card fc-first-sec-card">
                <div class="flex-middle">
                  <div class="fc-red-circle-img">
                    <inline-svg
                      src="svgs/employeePortal/parking"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                  </div>
                  <div class="fc-portal-txt-16 pL10">Desk</div>
                </div>
                <div class="pT20">
                  <div class="fc-portal-txt-14">Title goes here…</div>
                </div>
                <div class="flex-middle pT5">
                  <div class="fc-portal-txt-14">1A-01</div>
                  <div class="pL10 pR10">|</div>
                  <div class="fc-portal-txt-14">5:00 PM to 6:00 PM</div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="5">
              <el-card class="box-card fc-first-sec-card">
                <div class="flex-middle">
                  <div class="fc-green-circle-img">
                    <inline-svg
                      src="svgs/employeePortal/meeting-room"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                  </div>
                  <div class="fc-portal-txt-16 pL10">Desk</div>
                </div>
                <div class="pT20">
                  <div class="fc-portal-txt-14">Title goes here…</div>
                </div>
                <div class="flex-middle pT5">
                  <div class="fc-portal-txt-14">1A-01</div>
                  <div class="pL10 pR10">|</div>
                  <div class="fc-portal-txt-14">5:00 PM to 6:00 PM</div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <!-- Second card section -->
          <div class="fc-employee-portal-first-sec-content pT25">
            <div class="fc-portal-txt-16">This week</div>
          </div>
          <el-row class="mT15 flex-middle" :gutter="10">
            <el-col :span="6">
              <el-card class="box-card fc-second-row-card">
                <div class="fc-black2-txt-14">Checked-in</div>
                <div class="pT10 fc-portal-txt-14 bold">Review daily items</div>
                <div class="pT20">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">A274</div>
                  </div>
                  <div class="flex-middle pT5">
                    <inline-svg
                      src="svgs/employeePortal/clock"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">9am - 10am</div>
                  </div>
                </div>
                <div class="pT20 flex-middle">
                  <div>
                    <el-button type="primary" class="fc-second-card-btn"
                      >Check out</el-button
                    >
                  </div>
                  <div class="pL30 fc-portal-txt-14 underline fw6">Extend</div>
                </div>
                <div class="fc-second-card-bg-img">
                  <inline-svg
                    src="svgs/employeePortal/meeting-room3"
                    iconClass="text-center vertical-bottom"
                  ></inline-svg>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="box-card fc-second-row-card">
                <div class="fc-black2-txt-14">Checked-in</div>
                <div class="pT10 fc-portal-txt-14 bold">Review daily items</div>
                <div class="pT20">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">A274</div>
                  </div>
                  <div class="flex-middle pT5">
                    <inline-svg
                      src="svgs/employeePortal/clock"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">9am - 10am</div>
                  </div>
                </div>
                <div class="pT20 flex-middle">
                  <div>
                    <el-button type="primary" class="fc-second-card-btn"
                      >Check out</el-button
                    >
                  </div>
                  <div class="pL30 fc-portal-txt-14 underline fw6">Extend</div>
                </div>
                <div class="fc-second-card-bg-img2">
                  <inline-svg
                    src="svgs/employeePortal/town-hall"
                    iconClass="text-center vertical-bottom"
                  ></inline-svg>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="box-card fc-second-row-card">
                <div class="fc-black2-txt-14">Checked-in</div>
                <div class="pT10 fc-portal-txt-14 bold">Review daily items</div>
                <div class="pT20">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">A274</div>
                  </div>
                  <div class="flex-middle pT5">
                    <inline-svg
                      src="svgs/employeePortal/clock"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">9am - 10am</div>
                  </div>
                </div>
                <div class="pT20 flex-middle">
                  <div>
                    <el-button type="primary" class="fc-second-card-btn"
                      >Check out</el-button
                    >
                  </div>
                  <div class="pL30 fc-portal-txt-14 underline fw6">Extend</div>
                </div>
                <div class="fc-second-card-bg-img">
                  <inline-svg
                    src="svgs/employeePortal/meeting-room3"
                    iconClass="text-center vertical-bottom"
                  ></inline-svg>
                </div>
              </el-card>
            </el-col>
            <el-col :span="6">
              <el-card class="box-card fc-second-row-card">
                <div class="fc-black2-txt-14">Checked-in</div>
                <div class="pT10 fc-portal-txt-14 bold">Review daily items</div>
                <div class="pT20">
                  <div class="flex-middle">
                    <inline-svg
                      src="svgs/employeePortal/meeting-room2"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">A274</div>
                  </div>
                  <div class="flex-middle pT5">
                    <inline-svg
                      src="svgs/employeePortal/clock"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 pL10">9am - 10am</div>
                  </div>
                </div>
                <div class="pT20 flex-middle">
                  <div>
                    <el-button type="primary" class="fc-second-card-btn"
                      >Check out</el-button
                    >
                  </div>
                  <div class="pL30 fc-portal-txt-14 underline fw6">Extend</div>
                </div>
                <div class="fc-second-card-bg-img">
                  <inline-svg
                    src="svgs/employeePortal/meeting-room3"
                    iconClass="text-center vertical-bottom"
                  ></inline-svg>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <!-- third section -->
          <el-row class="mT15 flex-middle" :gutter="10">
            <el-col :span="12">
              <el-card class="box-card fc-recently-received-card">
                <div class="flex-middle">
                  <inline-svg
                    src="svgs/employeePortal/recent"
                    iconClass="text-center vertical-bottom icon-sm-md"
                  ></inline-svg>
                  <div class="fc-portal-txt-14 pL5 fw6">
                    Recently Reserved Space
                  </div>
                </div>
                <div class="pT20 flex-middle">
                  <div class="fc-hot-desk-card">
                    <div class="fc-txt12-grey">Hot Desk</div>
                    <div class="flex-middle pT10">
                      <div>
                        <inline-svg
                          src="svgs/employeePortal/hot-desk2"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-portal-txt-16 pL5">78</div>
                    </div>
                  </div>
                  <!-- second card -->
                  <div class="fc-meeting-desk-card mL15">
                    <div class="fc-txt12-grey">Meeting Room</div>
                    <div class="flex-middle pT10">
                      <div>
                        <inline-svg
                          src="svgs/employeePortal/meeting-room4"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-portal-txt-16 pL5">78</div>
                    </div>
                  </div>
                  <!-- Third card -->
                  <div class="fc-hotelling-desk-card mL15">
                    <div class="fc-txt12-grey">Hoteling</div>
                    <div class="flex-middle pT10">
                      <div>
                        <inline-svg
                          src="svgs/employeePortal/hoteling"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-portal-txt-16 pL5">78</div>
                    </div>
                  </div>
                </div>
              </el-card>
              <el-card
                class="box-card fc-recently-received-card fc-visit-card mT20"
              >
                <div class="fc-visit-header">
                  <inline-svg
                    src="svgs/employeePortal/mybooking"
                    iconClass="icon text-center icon-md vertical-bottom"
                  ></inline-svg>
                  <div class="fc-portal-txt-14 fw6 pL10">
                    Plan your next Visit
                  </div>
                </div>
                <div class="fc-visit-body">
                  <el-row>
                    <el-col
                      :span="12"
                      class="fc-portal-border-right fc-card-padding-visit-body"
                    >
                      <div class="fc-grey-10">Date</div>
                      <div class="fc-portal-txt-16 fw6 fw6 pT5">26 Jan</div>
                      <div class="fc-grey-10 pT5">Date</div>
                    </el-col>
                    <el-col :span="12" class="fc-card-padding-visit-body">
                      <div class="fc-grey-10">Time</div>
                      <div class="fc-portal-txt-16 fw6 fw6 pT5">
                        Select timing
                      </div>
                    </el-col>
                  </el-row>
                </div>
                <div class="fc-visit-footer">
                  <el-button class="fc-visit-footer-btn">
                    Find Space
                  </el-button>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <div class="portal-service-request-card">
                <el-card
                  class="box-card fc-recently-received-card fc-service-request-card"
                >
                  <div class="flex-middle p20">
                    <inline-svg
                      src="svgs/employeePortal/mybooking"
                      iconClass="icon text-center icon-md vertical-bottom"
                    ></inline-svg>
                    <div class="fc-portal-txt-14 fw6 pL10">
                      Service Requests
                    </div>
                  </div>
                  <el-row
                    class="flex-middle"
                    :gutter="10"
                    style="padding: 0 20px"
                  >
                    <el-col :span="5">
                      <div class="service-request-add">
                        <inline-svg
                          src="svgs/employeePortal/plus"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                        <div class="label-txt-black2 pT5 text-center">
                          Add Request
                        </div>
                      </div>
                    </el-col>
                    <el-col :span="5" class="flex-middle flex-direction-column">
                      <div class="fc-blue-circle">
                        <inline-svg
                          src="svgs/employeePortal/lap-charger"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-grey11 text-center pT5">
                        Lap <br />
                        Charger
                      </div>
                    </el-col>
                    <el-col :span="5" class="flex-middle flex-direction-column">
                      <div class="fc-blue-circle">
                        <inline-svg
                          src="svgs/employeePortal/printer-service"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-grey11 text-center pT5">
                        Printer <br />
                        Service
                      </div>
                    </el-col>
                    <el-col :span="5" class="flex-middle flex-direction-column">
                      <div class="fc-blue-circle">
                        <inline-svg
                          src="svgs/employeePortal/desktop-service"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-grey11 text-center pT5">
                        Desktop <br />
                        Service
                      </div>
                    </el-col>
                    <el-col :span="5" class="flex-middle flex-direction-column">
                      <div class="fc-blue-circle">
                        <inline-svg
                          src="svgs/employeePortal/ic-more-horiz"
                          iconClass="icon text-center icon-md vertical-bottom"
                        ></inline-svg>
                      </div>
                      <div class="fc-grey11 text-center pT5">
                        Access <br />
                        Card
                      </div>
                    </el-col>
                  </el-row>
                  <el-row :gutter="10" class="fc-request-list-block mT20">
                    <el-col :span="4">
                      <div class="fc-portal-blue-txt14">#123456</div>
                    </el-col>
                    <el-col :span="10">
                      <div class="fc-portal-black14">
                        Lorem ipsum dolor sit amet, consectetur elit, sed do
                      </div>
                    </el-col>
                    <el-col :span="10">
                      <div class="fc-portal-black2-14 flex-middle">
                        <div class="fc-status-dot-green mR5"></div>
                        Processing
                      </div>
                    </el-col>
                  </el-row>
                  <el-row :gutter="10" class="fc-request-list-block">
                    <el-col :span="4">
                      <div class="fc-portal-blue-txt14">#123456</div>
                    </el-col>
                    <el-col :span="10">
                      <div class="fc-portal-black14">
                        Lorem ipsum dolor sit amet, consectetur elit, sed do
                      </div>
                    </el-col>
                    <el-col :span="10">
                      <div class="fc-portal-black2-14 flex-middle">
                        <div class="fc-status-dot-red mR5"></div>
                        Processing
                      </div>
                    </el-col>
                  </el-row>
                  <div class="service-request-footer pointer">Show all</div>
                </el-card>
              </div>
            </el-col>
          </el-row>
          <!-- fourth section -->
          <el-row class="mT15" :gutter="10">
            <el-col :span="12">
              <el-card
                class="box-card fc-recently-received-card"
                style="height: 272px"
              >
                <div class="fc-fourth-sec-header flex-middle">
                  <inline-svg
                    src="svgs/employeePortal/recent"
                    iconClass="text-center vertical-bottom icon-sm-md"
                  ></inline-svg>
                  <div class="fc-portal-txt-14 pL5 fw6">Visitor</div>
                </div>
                <div class="fc-fourth-sec-body">
                  <inline-svg
                    src="svgs/employeePortal/empty-visitor"
                    iconClass="text-center vertical-bottom icon-sm-md"
                  ></inline-svg>
                  <div class="fc-grey-lite text-center">
                    This section will show, list <br />
                    of service request
                  </div>
                </div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card
                class="box-card fc-recently-received-card"
                style="height: 272px"
              >
                <div class="fc-fourth-sec-header flex-middle">
                  <inline-svg
                    src="svgs/employeePortal/recent"
                    iconClass="text-center vertical-bottom icon-sm-md"
                  ></inline-svg>
                  <div class="fc-portal-txt-14 pL5 fw6">Deliverires</div>
                </div>
                <div class="fc-fourth-sec-body pT40">
                  <inline-svg
                    src="svgs/employeePortal/deliveries-empty"
                    iconClass="text-center vertical-bottom icon-sm-md"
                  ></inline-svg>
                  <div class="fc-grey-lite text-center">
                    This section will show, <br />
                    recent events
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-main>
  </div>
</template>
<script>
export default {}
</script>
<style lang="scss">
.fc-employee-portal-home {
  width: 100%;
  .fc-employee-portal-home-header {
    width: 100%;
    height: 56px;
    background-color: #fff;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-bottom: 1px solid #d1d1d1;
    .fc-show-result-txt {
      font-size: 12px;
      font-weight: normal;
      line-height: normal;
      letter-spacing: normal;

      color: #b1b1b1;
    }
    .fc-employee-heder-right {
      display: flex;
      align-items: center;
    }
  }
  .fc-employee-portal-add-btn {
    .el-button {
      color: #fff;
      font-size: 14px;
      padding-top: 10px;
      padding-bottom: 10px;
      font-weight: 500;
      line-height: normal;
      letter-spacing: normal;

      color: #fff;
      background-color: #0053cc;
      border: 1px solid #0053cc;
    }
    .el-dropdown__caret-button {
      background: #0053cc;
    }
    .el-dropdown__caret-button::before {
      display: none;
    }
  }
  .fc-portal-badge {
    .el-badge__content {
      width: 20px;
      height: 20px;
      border: solid 0.5px #fff;
      background-color: #c2535a;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      margin-right: 10px;
      margin-top: 8px;
    }
  }
}
.fc-employee-portal-main-sec {
  padding: 16px;
  overflow-y: scroll;
  padding-bottom: 100px;
  height: calc(100vh - 200px);
  .fc-employee-portal-first-sec {
    .fc-employee-portal-first-sec-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
  }
}
.fc-portal-txt-16 {
  font-size: 16px;
  font-weight: bold;
  line-height: normal;
  letter-spacing: normal;

  color: #464646;
}
.fc-portal-txt-14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #464646;
}
.fc-portal-main-sec {
  padding: 0;
}
.fc-pink-circle-img {
  width: 32px;
  height: 32px;
  background-color: #fc436d;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-yellow-circle-img {
  width: 32px;
  height: 32px;
  background-color: #af8417;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-red-circle-img {
  width: 32px;
  height: 32px;
  background-color: #e00107;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-green-circle-img {
  width: 32px;
  height: 32px;
  background-color: #2f98a5;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-green-blue-img {
  width: 32px;
  height: 32px;
  background-color: #2f2cff;
  border-radius: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-first-sec-card {
  border-radius: 4px;
  box-shadow: 0 0 7px 0 rgba(0, 0, 0, 0.09);
  .el-card__body {
    padding: 12px 12px 20px;
  }
}

.fc-second-row-card {
  position: relative;
  border-radius: 6px;
  box-shadow: 0 0 7px 0 rgba(0, 0, 0, 0.09);
  .el-card__body {
    padding: 12px;
    height: 190px;
  }
}

.fc-black2-txt-14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;

  color: #949494;
}
.fc-second-card-btn {
  padding-left: 42px;
  padding-right: 42px;
  height: 40px;
  border-radius: 8px;
  background-color: #0053cc;
  border: 1px solid #0053cc;
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #fff;
  &:hover {
    background-color: #0053cc;
    border: 1px solid #0053cc;
  }
}
.fc-second-card-bg-img {
  position: absolute;
  top: 20px;
  right: -75px;
}
.fc-second-card-bg-img2 {
  position: absolute;
  top: 30px;
  right: 0px;
}
.fc-recently-received-card {
  border-radius: 6px;
  box-shadow: 0 1px 7px 0 rgba(0, 0, 0, 0.09);
}
.fc-hot-desk-card {
  width: 80px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-meeting-desk-card {
  width: 120px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-hotelling-desk-card {
  width: 90px;
  height: 60px;
  border-radius: 4px;
  border: solid 1px #e6e6e6;
  background-color: #fff;
  padding: 8px;
}
.fc-txt12-grey {
  font-size: 12px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #6f6f6f;
}
.fc-visit-header {
  padding: 20px;
  border-bottom: 1px solid #f1f1f1;
  display: flex;
  align-items: center;
}
.fc-visit-card {
  .el-card__body {
    padding: 0;
  }
}
.fc-grey-10 {
  font-size: 10px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;

  color: #6f6f6f;
}
.fc-visit-body {
  border-bottom: 1px solid #f1f1f1;
}
.fc-portal-border-right {
  border-right: 1px solid #f1f1f1;
}
.fc-card-padding-visit-body {
  padding: 12px 24px;
}
.fc-visit-footer {
  padding: 20px;
  .fc-visit-footer-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    line-height: normal;
    letter-spacing: normal;
    text-align: center;
    color: #0053cc;
    border-radius: 5px;
    border: solid 1px #0053cc;
    background-color: #fff;
    &:hover,
    &:active,
    &:focus {
      color: #fff;
      border: solid 1px #0053cc;
      background-color: #0053cc;
    }
  }
}
.fc-fourth-sec-body {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}
.fc-grey-lite {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.14;
  letter-spacing: normal;
  text-align: center;
  color: #324056;
  opacity: 0.5;
}
.service-request-add {
  width: 70px;
  border: 1px dotted #cfcaff;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  padding: 10px;
}
.fc-service-request-card {
  .el-card__body {
    padding: 0;
  }
}
.fc-blue-circle {
  width: 32px;
  height: 32px;
  border-radius: 100%;
  background-color: #e1ecfe;
  display: flex;
  align-items: center;
  justify-content: center;
}
.fc-grey11 {
  font-size: 11px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #464646;
}
.fc-portal-blue-txt14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #483db6;
}
.fc-portal-black14 {
  font-size: 14px;
  font-weight: normal;
  line-height: 1.29;
  letter-spacing: normal;
  color: #12324a;
}
.fc-portal-black2-14 {
  font-size: 14px;
  font-weight: normal;
  line-height: normal;
  letter-spacing: normal;
  color: #12324a;
}
.fc-status-dot-green {
  width: 6px;
  height: 6px;
  background-color: #16aa00;
  border-radius: 10px;
}
.fc-status-dot-red {
  width: 6px;
  height: 6px;
  background-color: #c03000;
  border-radius: 10px;
}
.fc-request-list-block {
  background-color: #fbfaff;
  padding: 20px;
}
.service-request-footer {
  padding: 30px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  line-height: normal;
  letter-spacing: normal;
  text-align: center;
  color: #0053cc;
}
</style>
