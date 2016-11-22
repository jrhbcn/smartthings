import RPi.GPIO as GPIO
from flask import Flask, jsonify
import time

# Init
app = Flask(__name__)
GPIO.setmode(GPIO.BOARD)

# Create dictionary with elements 
things = {'fans':   [{'name': 'bedroom',    'gpio': {'slow':11, 'stop':13, 'light':15, 'fast':16}}],
          'blinds': [{'name': 'bedroom',    'gpio': {'up':3,'stop':5,'down': 7}},
                     {'name': 'livingroom', 'gpio': {'up':8,'stop':10,'down':12}}]}


# Initialize pins
for k,val in things.items():
    for t in val:
        for k,g in t['gpio'].items():
            #print k,g
            GPIO.setup(g,GPIO.OUT)
            GPIO.output(g,GPIO.LOW)

            
def thing_do(f,action):
    if action in f['gpio']:
        #print 'thing_do' 
        GPIO.output(f['gpio'][action], GPIO.HIGH)
        time.sleep(1)
        GPIO.output(f['gpio'][action], GPIO.LOW)
        return 200
    else:
        return 400


@app.route("/status")
def status():    
    return 'online', 200

@app.route("/<ttype>/<thing>/<action>")
def action(ttype,thing, action):    
    res = 400
    if ttype in things:
        for f in things[ttype]:
            if thing == f['name'] or thing == "all":
                res = thing_do(f,action)
    return jsonify({'res':res}), res

if __name__ == "__main__":
   app.run(host='0.0.0.0', port=82, debug=False)
