<template>
  <div>
    <el-dialog
      :visible.sync="visibility"
      :fullscreen="true"
      :append-to-body="true"
      custom-class="fc-dialog-form fc-dialog-right width32 setup-dialog"
      :before-close="closeDialog"
      style="z-index: 999999"
    >
      <div class="new-header-container">
        <div class="new-header-text">
          <div class="fc-setup-modal-title">Agent Metrics Helper</div>
        </div>
      </div>
      <div class="new-body-modal">
        <div class>
          <div class="fc-text-pink14 text-uppercase fwBold text-left pB10">
            Time
          </div>
          <div class="fc-black-14 text-left line-height25 break-word">
            Time at which the message was sent by the agent
          </div>
        </div>
        <div class="pT20">
          <div class="fc-text-pink14 text-uppercase fwBold text-left pB10">
            Publish Type
          </div>
          <div>
            <div class="flex-middle fc-black-14">
              <div class="dot-dark-grey mR5"></div>
              <div>Type of the message</div>
            </div>
            <div class="flex-middle pT10">
              <div class="dot-dark-grey mR5"></div>
              <div class=" fc-black-14">
                List of publish types
              </div>
            </div>
          </div>
          <div class="mL30">
            <div class="pT20">
              <div class="fc-text-pink14 f12 fwBold pB10">
                ACK
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                Messages with this publish type ack are sent as an while
                executing the commands that are sent from server to an agent for
                statuses like
              </div>
            </div>
            <div class="pL30">
              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Received:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Messages with this publish type ack are sent as an while
                  executing the commands that are sent from server to an agent
                  for statuses like
                </div>
              </div>

              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Processing:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent is processing the command.
                </div>
              </div>

              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Success:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent executed the command successfully.
                </div>
              </div>

              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Failed:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Command execution failed.
                </div>
              </div>

              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Duplicate:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Every command sent from the server has a unique message id, if
                  the agent message id is repeated even though the content of
                  the message may , it is marked as duplicate.
                </div>
              </div>

              <div class="pT20">
                <div class="fc-id fwBold pB10">
                  Invalid:
                </div>
                <div class="fc-black-14 text-left line-height25 break-word">
                  Agent does not support the command sent from the server.
                </div>
              </div>
            </div>
          </div>
          <div class="pT20 pL30">
            <div
              class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
            >
              Agent
            </div>

            <div class="flex-middle pT10">
              <div class="dot-dark-grey mR5"></div>
              <div class=" fc-black-14 pL10 text-left">
                Publish type Agent is used in the following messages
              </div>
            </div>
            <div class="pL30 pR30">
              <div class="fc-black-14 text-left line-height25 break-word pT10">
                1. With a birth json message, which is sent to the server on
                successful connection.
              </div>
              <div class="fc-black-14 text-left line-height25 break-word pT10">
                2. On successful subscription with the server to receive command
                from the server.
              </div>
              <div class="fc-black-14 text-left line-height25 break-word pT10">
                3. With a message which is sent to the server when the agent
                disconnects with the server.
              </div>
              <div class="fc-black-14 text-left line-height25 break-word pT10">
                4. With LastWillTestament message, when an agent abruptly
                terminates the connection with the server.
              </div>
              <div class="fc-black-14 text-left line-height25 break-word pT10">
                5. Also as a part of Agent command execution.
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Cov
              </div>
              <div class="fc-black-14 text-left line-height25 break-word ">
                Bacnet IP points can be subscribed to notify the server when
                change of value occurs for the points, those values are sent to
                the server with “cov” publish type
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Device Points
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                Bacnet IP, OPC XML/DA, OPC UA, SystemController support
                discovering points from the server. Once the agent discovers the
                points for a controller, it sends the data to the server with
                this publish type.
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Events
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                If a controller sends an event to the agent, it is sent to the
                server using this publish type.,
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Timeseries
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                Points which have been configured to push the values at a
                certain interval, use this publish type.
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Controller
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                Commands like Add controller, Edit Controller, Discover
                Controller use this publish type to send the data to the server
                after execution.
              </div>
            </div>

            <div class="pT20">
              <div
                class="fc-text-pink14 f12 text-uppercase fwBold text-left pB10"
              >
                Agent Events
              </div>
              <div class="fc-black-14 text-left line-height25 break-word">
                If the agent cannot connect to a configured controller or not
                able to get a value of a point. Agent will send a message to the
                server using this publish type.
              </div>
            </div>
          </div>

          <div class="pT20">
            <div class="fc-text-pink14 text-uppercase fwBold text-left pB10">
              Messages
            </div>
            <div class="fc-black-14 text-left line-height25 break-word">
              Number of messages sent by the agent to the server in the
              mentioned time.
            </div>
          </div>

          <div class="pT20">
            <div class="fc-text-pink14 text-uppercase fwBold text-left pB10">
              Size
            </div>
            <div class="fc-black-14 text-left line-height25 break-word">
              Total size of all the messages sent by the agent.
            </div>
          </div>
        </div>
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
