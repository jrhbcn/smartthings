
import RPi.GPIO as GPIO
import time

# Setup
GPIO.setmode(GPIO.BOARD)
pin = 11

# Initialize
GPIO.setup(pin, GPIO.OUT)
GPIO.output(pin, GPIO.LOW)


# Simulate button press
GPIO.output(pin, GPIO.HIGH)
print 'HIGH'
time.sleep(3)
GPIO.output(pin, GPIO.LOW)
print 'LOW'

#GPIO.cleanup()



