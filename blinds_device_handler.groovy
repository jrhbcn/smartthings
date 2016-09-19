/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
metadata {
	definition (name: "Simulated Blind", namespace: "jrhbcn", author: "jrhbcn") {
		capability "Actuator"
		capability "Button"
		capability "Sensor"
        
		attribute "status", "enum", ["online", "offline"]

		command "up"
        command "stop"
        command "down"
	}
    preferences {
        input("name", "string", title:"Name", description: "", required: true, displayDuringSetup: true)
	}

	simulator {

	}
	tiles(scale:2) {

		multiAttributeTile(name:"blind", type: "generic", width: 6, height: 4) {
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
           		attributeState("online", label:'${name}', icon:"st.Home.home9", backgroundColor:"#79b821")
            	attributeState("offline", label:'${name}', icon:"st.Home.home9", backgroundColor:"#ffa81e")   
 			}
		}
        
 		standardTile("up", "device.button", width: 2, height: 2) {
			state "default", label: "Up", backgroundColor: "#ffffff", action: "up", icon:"http://cdn.device-icons.smartthings.com/thermostat/thermostat-up@2x.png"
		} 
 		standardTile("stop", "device.button", width: 2, height: 2) {
			state "default", label: "", backgroundColor: "#ffffff", action: "stop", icon:"http://cdn.device-icons.smartthings.com/sonos/stop-btn@2x.png"
		} 
 		standardTile("down", "device.button", width: 2, height: 2) {
			state "default", label: "Down", backgroundColor: "#ffffff", action: "down", icon:"http://cdn.device-icons.smartthings.com/thermostat/thermostat-down@2x.png"
		}
		details(["blind","up","stop","down"])
	}
}

def handlerScheduler() {
    log.debug "handlerMethod called at ${new Date()}"
    try {
    httpPost("http://192.168.0.160:2240/blinds/${name}/status", "") { resp ->
        log.debug "response data: ${resp.data}"
        log.debug "response contentType: ${resp.contentType}"
        if (resp.data == "ok") {
			sendEvent(name: "status", value: "online", isStateChange: true)
        } else {
			sendEvent(name: "status", value: "offline", isStateChange: true)
        }
    }
	} catch (e) {
    	log.debug "something went wrong: $e"
		sendEvent(name: "status", value: "offline", isStateChange: true)
	}
}

def installed() {
	initialize()
}

def updated() {
	initialize()
}

def initialize() {
	unschedule(handlerScheduler)
	schedule("0 30 * * * ?", handlerScheduler)
    //runEvery3Hours(handlerScheduler)
}


def parse(String description) {
	
}

def http_command(command)
{
    try {
    httpPost("http://192.168.0.160:2240/blinds/${name}/${command}", "") { resp ->
        log.debug "response data: ${resp.data}"
        log.debug "response contentType: ${resp.contentType}"
    }
	} catch (e) {
    	log.debug "something went wrong: $e"
	}
}

def up() {
    http_command("up")
	sendEvent(name: "up", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName up button was pushed", isStateChange: true)
}

def stop() {
    http_command("stop")
	sendEvent(name: "stop", value: "pushed", data: [buttonNumber: "2"], descriptionText: "$device.displayName stop button was pushed", isStateChange: true)
}

def down() {
    http_command("down")
	sendEvent(name: "down", value: "pushed", data: [buttonNumber: "3"], descriptionText: "$device.displayName down button was pushed", isStateChange: true)
}
