import { Outlet } from "react-router-dom";
import events from "events";
import { WebSocketChargePointNotification } from "../../conf/chargePointController";
import { IconButton } from "@mui/material";
import DisplayNotification, { NotificationMessage } from "../../sharedComponents/DisplayNotification";
import { useState } from "react";
import NotificationImportantIcon from '@mui/icons-material/NotificationImportant';
import {BACKEND_PORT, WEBSOCKET_PATH} from "../../conf/backendController";
import NavBar from "../../sharedComponents/NavBar";

/**
 * Define all listener event that can be emitted 
 */
declare interface AppListener {
    on(event: 'charge-point-update', listener: (message: WebSocketChargePointNotification) => void): this;
    on(event: 'notify', listener: (message: NotificationMessage) => void): this;
}

/** Definition of web socket messages sent by the backend */
type WebSocketMessage = {
  /** Types of messages */
  name: "ChargePointWebsocketNotification" | "CriticalityWebsocketNotification",
  /** Message content */
  value: any,
}

/**
 * This class is used to dispatch notification all around the application.
 */
class AppListener extends events.EventEmitter {
    connected: boolean;
    websocket: WebSocket | undefined;

    constructor() {
      super()
      this.connected = false;
    }

    /**
     * Start the web socket connection with the backend
     */
    startWebSocket(): void {
      if (this.connected) {
        console.error("WebSocket connection is already established.")
        return
      }

      let isLocal = window.location.hostname.startsWith("localhost") || window.location.hostname.startsWith("127.0.0.1")
      let protocol = isLocal ? "ws://" : "wss://"
      let websocketAddress = `${protocol}${window.location.hostname}${isLocal ? ":" + BACKEND_PORT : ""}${WEBSOCKET_PATH}`
      this.websocket = new WebSocket(websocketAddress);
      this.websocket.onopen = (ev: Event) => {
          console.log('Websocket connected to the server');
          this.connected = true
      }
      this.websocket.onmessage = (ev: MessageEvent<any>) => {
        // Try parse as WebSocketChargePointNotification
        const message = JSON.parse(ev.data) as WebSocketMessage
        // Parse message content
        switch (message.name) {
          case "ChargePointWebsocketNotification":
            let wsChargePointNotification = message.value as WebSocketChargePointNotification
            if (!wsChargePointNotification) {
              console.error("Wrongly formatted message received : " + ev.data)
              return
            }
            this.emit('charge-point-update', wsChargePointNotification)
            break;
          case "CriticalityWebsocketNotification":
            let wsNotification = message.value as NotificationMessage
            if (!wsNotification) {
              console.error("Wrongly formatted message received : " + ev.data)
              return
            }
            this.emitNotification(wsNotification)
            break;
          default:
            console.warn("Unknown received message from websocket : " + ev.data)
        }
      }

      this.websocket.onclose = (ev: CloseEvent) => {
          this.connected = false
          // If connection closed, try to reconnect the websocket 5 second later
          setTimeout(() => this.startWebSocket(), 5000)
      }
    }

    /**
     * Emit a notification through the listener
     * @param message Notification message
     */
    emitNotification(message: NotificationMessage){
      this.emit("notify", message)
    }
}

// Create the application listener
export const notificationManager = new AppListener();
notificationManager.startWebSocket()

/**
 * Define the home page React component (navBar, embedded page, notification)
 * @returns The react component of the home page
 */
export function Home() {
    const [openNotification, setOpenNotification] = useState(false);
    return (
        <div className="App" style={{maxWidth: "true", height: "100vh", overflow: "hidden"}}>
          {!openNotification && (
            <IconButton                     
              style={{position: "fixed", top: "92%", right: "3%", zIndex: 999}}
              onClick={() => {
                setOpenNotification(true)
              }}>
              <NotificationImportantIcon 
                    color="primary"
                    fontSize={"large"}
                >
              </NotificationImportantIcon>
            </IconButton>
          )}
          <NavBar/>
          <Outlet />
          <DisplayNotification open={openNotification} onClose={() => setOpenNotification(false)}/>
        </div>
    );
}


export default Home;

