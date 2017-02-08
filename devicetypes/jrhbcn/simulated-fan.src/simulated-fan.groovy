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
import groovy.json.JsonSlurper
import groovy.util.XmlSlurper

metadata {
	definition (name: "Simulated Fan", namespace: "jrhbcn", author: "jrhbcn") {
		capability "Actuator"
		capability "Switch"
		capability "Sensor"
        capability "Refresh"
        capability "Polling"

        attribute "state2", "enum", ["online", "offline"]
        attribute "last_request", "number"
        attribute "last_live", "number"
    
        command "force_refresh"
		command "slow"
        command "stopfan"
        command "fast"
        command "light"
        command "twoh"
        command "eighth"
        
	}
    preferences {
        input("name", "string", title:"Name", description: "", required: true, displayDuringSetup: true)
	}

	simulator {
	}
    
	tiles(scale:2) {
		multiAttributeTile(name:"fan", type: "generic", width: 6, height: 4) {
			tileAttribute ("status", key: "PRIMARY_CONTROL") {
           		attributeState("online", label:'${name}', icon:"st.Lighting.light24", backgroundColor:"#79b821")
                //attributeState "offline", label:'${name}', action:"", backgroundColor:"#555555", icon: "st.Lighting.light24"
 			}
		}       
 		standardTile("slow", "device.button", width: 2, height: 2) {
			state "default", label: "Slow", backgroundColor: "#ffffff", action: "slow", icon:""
		} 
 		standardTile("stopfan", "device.button", width: 2, height: 2) {
			state "default", label: "Stop", backgroundColor: "#ffffff", action: "stopfan", icon:""
		} 
 		standardTile("fast", "device.button", width: 2, height: 2) {
			state "default", label: "Fast", backgroundColor: "#ffffff", action: "fast", icon:""
		}
 		standardTile("light", "device.button", width: 2, height: 2) {
			state "default", label: "Light", backgroundColor: "#ffffff", action: "light", icon:""
		}
 		standardTile("twoh", "device.button", width: 2, height: 2) {
			state "default", label: "2h", backgroundColor: "#ffffff", action: "twoh", icon:""
		}
 		standardTile("eighth", "device.button", width: 2, height: 2) {
			state "default", label: "8h", backgroundColor: "#ffffff", action: "eighth", icon:""
		}
    	standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
		details(["fan","slow","stopfan","fast","light","twoh","eighth","refresh"])
	}
}

def parse(description)
{
	log.debug "Parsing '${description}'"
    //def msg = parseLanMessage(description)
    //log.debug msg
    def events = []
    def cmds
    def descMap = parseDescriptionAsMap(description)
    def body
    log.debug "descMap: ${descMap}"
    
    if (descMap["body"]) body = new String(descMap["body"].decodeBase64())

    if (body && body != "") {
    
        if(body.startsWith("{") || body.startsWith("[")) {
            def slurper = new JsonSlurper()
            def result = slurper.parseText(body)
            log.debug "result: ${result}"
    
            if (result.containsKey("online")) {
				// status is online
                log.debug "result: online (INSIDE)"
				events << createEvent(name:"state2", value: 'online')
		        def c = new GregorianCalendar()
        		// Do not know if I need this:         events << createEvent(name: 'state2', value: device.switch )
        		log.debug "[parse()] updating last_live"
        		events << createEvent(name: "last_live", value: c.time.time)
            }
            if (result.containsKey("res")) {
            	// Normal result
                log.debug "result: res (INSIDE)"
                events << createEvent(name:"state2", value: 'online')
            }
        } else {
            log.debug "Response is not JSON: $body"
        }
    } else {
        cmds = refresh()
    }
    
    if (cmds) return cmds else return events
   
}

def poll() {    
    refresh()
}

def force_refresh() {    
    refresh()
}

def refresh() {
	log.debug "refresh()"
    
    def last_request = device.latestValue("last_request")
    def last_live = device.latestValue("last_live")
        
    if(!last_request) {
    	last_request = 0
    }
    if(!last_live) {
    	last_live = 0
    }

    log.debug "[refresh()] last_request: ${last_request}"
    log.debug "[refresh()] last_live: ${last_live}"

	def c = new GregorianCalendar()
    
    if(last_live < last_request) { 
    	sendEvent(name: 'state2', value: "offline")  
        //sendEvent(name: 'ttl', value: ttl())
    }
    
    sendEvent(name: 'last_request', value: c.time.time)
    def cmds = []
    cmds << http_command("/status")
    return cmds
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
    //log.debug cmds
	sendEvent(name: "slow", value: "pushed", data: [buttonNumber: "1"], descriptionText: "$device.displayName slow button was pushed", isStateChange: true)
    return cmds
}

def stopfan() {
	def cmds = []
	cmds << http_command("/fans/${name}/stop")
    //log.debug cmds
	sendEvent(name: "stopfan", value: "pushed", data: [buttonNumber: "2"], descriptionText: "$device.displayName stopfan button was pushed", isStateChange: true)
    return cmds
}

def fast() {
   	def cmds = []
	cmds << http_command("/fans/${name}/fast")
    //log.debug cmds
	sendEvent(name: "fast", value: "pushed", data: [buttonNumber: "3"], descriptionText: "$device.displayName fast button was pushed", isStateChange: true)
	return cmds
}

def light() {
   	def cmds = []
	cmds << http_command("/fans/${name}/light")
    //log.debug cmds
	sendEvent(name: "light", value: "pushed", data: [buttonNumber: "3"], descriptionText: "$device.displayName light button was pushed", isStateChange: true)
	return cmds
}

def twoh() {
	slow()
    runIn(60*60*2, stop)
   	def cmds = []
	return cmds
}

def eighth() {
	slow()
    runIn(60*60*8, stop)
   	def cmds = []
	return cmds
}

def getHostAddress() {
	return "192.168.1.39:82"
}

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
	    def nameAndValue = param.split(":")
        
        if (nameAndValue.length == 2) map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
        else map += [(nameAndValue[0].trim()):""]
	}
}

private http_command(uri) {
	log.debug("Executing hubaction ${uri} on " + getHostAddress())

    def hubAction = new physicalgraph.device.HubAction(
    	method: "GET",
        path: uri,
        headers: [HOST:getHostAddress()])

    return hubAction
}
