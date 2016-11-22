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
	definition (name: "Simulated Fan", namespace: "jrhbcn", author: "jrhbcn") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"

		command "slow"
        command "stop"
        command "fast"
        command "light"
        
	}
    preferences {
        input("name", "string", title:"Name", description: "", required: true, displayDuringSetup: true)
	}

	simulator {
	}
    
	tiles(scale:2) {
		multiAttributeTile(name:"fan", type: "generic", width: 6, height: 4) {
			tileAttribute ("device.status", key: "PRIMARY_CONTROL") {
           		attributeState("online", label:'${name}', icon:"st.Lighting.light24", backgroundColor:"#79b821")
 			}
		}       
 		standardTile("slow", "device.button", width: 2, height: 2) {
			state "default", label: "Slow", backgroundColor: "#ffffff", action: "slow", icon:""
		} 
 		standardTile("stop", "device.button", width: 2, height: 2) {
			state "default", label: "Stop", backgroundColor: "#ffffff", action: "stop", icon:""
		} 
 		standardTile("fast", "device.button", width: 2, height: 2) {
			state "default", label: "Fast", backgroundColor: "#ffffff", action: "fast", icon:""
		}
 		standardTile("light", "device.button", width: 2, height: 2) {
			state "default", label: "Light", backgroundColor: "#ffffff", action: "light", icon:""
		}
		details(["fan","slow","stop","fast","light"])
	}
}

def parse(String description) {
	log.debug "Parsing '${description}'"
    def msg = parseLanMessage(description)
    log.debug msg
}

private http_command(uri) {
	log.debug("Executing hubaction ${uri} on " + getHostAddress())

    def hubAction = new physicalgraph.device.HubAction(
    	method: "GET",
        path: uri,
        headers: [HOST:getHostAddress()])

    return hubAction
}

def on() {
	light()
}
    
def off() {
	light()
}

def slow() {
	def cmds = []
	cmds << http_command("/fans/${name}/slow")
    log.debug cmds
	sendEvent(name: "slow", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName slow button was pushed", isStateChange: true)
    return cmds
}

def stop() {
	def cmds = []
	cmds << http_command("/fans/${name}/stop")
    log.debug cmds
	sendEvent(name: "stop", value: "pushed", data: [buttonNumber: "2"], descriptionText: "$device.displayName stop button was pushed", isStateChange: true)
    return cmds
}

def fast() {
   	def cmds = []
	cmds << http_command("/fans/${name}/fast")
    log.debug cmds
	sendEvent(name: "fast", value: "pushed", data: [buttonNumber: "3"], descriptionText: "$device.displayName fast button was pushed", isStateChange: true)
	return cmds
}

def light() {
   	def cmds = []
	cmds << http_command("/fans/${name}/light")
    log.debug cmds
	sendEvent(name: "light", value: "pushed", data: [buttonNumber: "3"], descriptionText: "$device.displayName light button was pushed", isStateChange: true)
	return cmds
}

def getHostAddress() {
	return "192.168.0.162:82"
}
