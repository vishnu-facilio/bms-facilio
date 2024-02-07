<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right setup-dialog35 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container pL20">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">Command Log Helper</div>
        </div>
      </div>
      <div
        class="new-body-modal pL0 pR0 mT0"
        style="height: calc(100vh - 50px);"
      >
        <div class>
          <div class="pB10 pT10">
            <div class="pT10 pB20 pL20">
              <div
                class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
              >
                Command:
              </div>
              <div class="fc-black-14 text-left line-height25">
                Command sent from the server to the agent.
              </div>
            </div>
            <!-- collapse -->
            <el-collapse accordion class="border-border-none helplog-collapse">
              <el-collapse-item name="1" title="List of Commands:">
                <div class="pB10">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Ping
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to check if the MQTT connection is active for
                    the selected agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Reboot
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to restart the Facilio Agent.
                  </div>
                </div>

                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Shutdown
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to shutdown the Facilio Agent.
                  </div>
                </div>
                <div class="pT2pT10 pB10 border-top30">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Upgrade
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to upgrade the Facilio Agent to a newer
                    version.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Property
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to update a property for a Facilio agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Threaddump
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Command used to take threaddump of Facilio agent to debug.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Stats
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    To get the JVM stats of Facilio Agent
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    DiscoverControllers
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    This command is used to discover controllers in an Facilio
                    Agent. Currently supports BACNet IP in Facilio Agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Add Controller
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Adds a new controller in the Facilio Agent where the
                    controllers cannot be discovered. But able to connect and
                    get the data. Modbus and OPC protocols are currently
                    supported in Facilio Agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Edit Controller
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Edit already added controller.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Discover Points
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Discover the points available for the given Controller. This
                    option is currently supported only for BACNet IP and OPC
                    Controllers in Facilio Agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Controller Status
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Get the status of the given Controller. Returns online,
                    offline or unreachable.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Configure
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Configure the points of the given Controller. Agent will get
                    the value of these points and sent to server and at the
                    configured interval.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Remove
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Remove the points which has been configured for the given
                    Controller. Agent will remove these points from periodic
                    data collection.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Reset
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Resets the controller. Removes all available, configured and
                    subscribed points.
                  </div>
                </div>
                <div class="ppT10 pB10 border-top3T20">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Get
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Get the value of the selected points.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Set
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Sets value for the selected points.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Subscribe
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Subscribe the points of the given Controller. Agent will be
                    notified when there is a change of value for these points
                    and will be compared to the threshold values. The value will
                    be sent if the threshold value is satisfied. This is only
                    enabled for BACNet IP in Facilio Agent.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Uunsubscribe
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Unsubscribe the points which has been subscribed for the
                    given Controller.
                  </div>
                </div>
                <div class="pT10 pB10 border-top3">
                  <div
                    class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                  >
                    Debug
                  </div>
                  <div class="fc-black-14 text-left line-height25">
                    Debug command is used to collect the data for the points
                    which has been configured for the given Controller at much
                    smaller intervals than configured.
                  </div>
                </div>
              </el-collapse-item>
            </el-collapse>
            <div class="pL20 pR20">
              <div class="pT10 pB10">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Sent
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Time the command was sent from the server
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Ack:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Acknowledgement time received by the agent.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Completed:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Time at which the agent completed the command execution.
                </div>
              </div>
            </div>
          </div>
          <div class="pL20 pR20 pT20 pB10">
            <div class="label-txt-black text-uppercase bold text-left pB10">
              Status:
            </div>
            <div class="fc-black-14 text-left line-height25">
              Status of the command execution
            </div>
          </div>
          <el-collapse accordion class="border-border-none helplog-collapse">
            <el-collapse-item title="List of status:" name="2">
              <div class="pB10">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Sent:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Command sent from the server.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Received:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent successfully received the command.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Processing:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent is processing the command.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Success:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent executed the command successfully.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Failed:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Command execution failed.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  {{ $t('common._common.duplicate') }}:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Every command sent from the server has a unique message id, if
                  the agent message id is repeated even though the content of
                  the message may , it is marked as duplicate.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Invalid:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent does not support the command sent from the server.
                </div>
              </div>
              <div class="pT10 pB10 border-top3">
                <div
                  class="fc-text-pink14 f11 text-uppercase fwBold text-left pB10"
                >
                  Message:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Message sent to the agent with the details of the command.
                </div>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
        <!-- collapse -->
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  props: ['visibility'],
  methods: {
    closeDialog() {
      this.$emit('update:visibility', false)
    },
  },
}
</script>
<style lang="scss">
.helplog-collapse {
  .el-collapse-item__header {
    padding-left: 20px;
    padding-right: 20px;
    text-transform: capitalize;
    font-weight: 500;
    font-size: 14px;
    letter-spacing: 0.5px;
    border-top: 1px solid #ebeef5;
  }

  .el-collapse-item__content {
    padding: 20px;
    word-break: break-word;
  }

  .el-collapse-item__header.is-active {
    border-bottom: 1px solid #ebeef5;
  }

  .el-collapse-item__arrow {
    font-weight: 600;
    color: #000;
  }

  .el-collapse-item__arrow.is-active {
    color: #ee518f;
  }
}
</style>
