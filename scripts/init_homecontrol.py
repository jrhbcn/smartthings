import RPi.GPIO as GPIO
import time

# Setup
GPIO.setmode(GPIO.BOARD)
pin = [3,5,7,8,10,12,11,13,15,16]

# Initialize
for p in pin:
  GPIO.setup(pin, GPIO.OUT)
  GPIO.output(pin, GPIO.LOW)




