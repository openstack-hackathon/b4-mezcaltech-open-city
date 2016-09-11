#!/usr/bin/python

import atexit
import ConfigParser
import signal
import sys
import time

import pyupm_grove as grove
import pyupm_grovespeaker as upmGrovespeaker
import pyupm_i2clcd as lcd
import pyupm_buzzer as upmBuzzer
import json

from telegram.ext import Updater, CommandHandler, MessageHandler, Filters

# For web socket
#from __future__ import print_function
import websocket

credentials = ConfigParser.ConfigParser()
credentialsfile = "credentials.config"
credentials.read(credentialsfile)

button = grove.GroveButton(8)
display = lcd.Jhd1313m1(0, 0x3E, 0x62)
light = grove.GroveLight(0)
relay = grove.GroveRelay(2)
buzzer = upmBuzzer.Buzzer(5)

chords = [upmBuzzer.DO, upmBuzzer.RE, upmBuzzer.MI, upmBuzzer.FA,
          upmBuzzer.SOL, upmBuzzer.LA, upmBuzzer.SI, upmBuzzer.DO,
          upmBuzzer.SI];

def functionLight(bot, update):
    luxes = light.value()
    bot.sendMessage(update.message.chat_id, text='Light ' + str(luxes))

def functionMessage(bot, update):
    bot.sendMessage(update.message.chat_id, text=message)

def functionRelay(bot, update):
    relay.on()
    time.sleep(2)
    relay.off()
    bot.sendMessage(update.message.chat_id, text='Relay Used!')

def functionEcho(bot, update):
    bot.sendMessage(update.message.chat_id, text=update.message.text)

def SIGINTHandler(signum, frame):
	raise SystemExit

def exitHandler():
	print "Exiting"
	sys.exit(0)

atexit.register(exitHandler)
signal.signal(signal.SIGINT, SIGINTHandler)

if __name__ == '__main__':

    credential = credentials.get("telegram", "token")
    updater = Updater(credential)
    dp = updater.dispatcher

    dp.add_handler(CommandHandler("light", functionLight))
    dp.add_handler(CommandHandler("message", functionMessage))
    dp.add_handler(CommandHandler("relay", functionRelay))
    dp.add_handler(MessageHandler([Filters.text], functionEcho))

    updater.start_polling()

    message = "Sending Emergency..."

    while True:

        luxes = light.value()
        luxes = int(luxes)
        display.setColor(luxes, luxes, luxes)
        display.clear()

        if button.value() is 1:
            # necesitamos mandar data al wsServer
            websocket.enableTrace(True)
            ws = websocket.create_connection("ws://172.16.11.227:1337")
            data = json.dumps({"lat": 20.7351541, "long":-103.456078, "emergency": {"type": "Emergencia", "description": "Edison TEC MTY"}})

            ws.send(data)
            print("Sent")
            print(data)
            message = "Sending Emergency..."

            print("Receiving...")
            result = ws.recv()
            print("Received '%s'" % result)
            ws.close()

            message = "Emergency received..."

            display.setColor(255, 0, 0)
            display.setCursor(0,0)
            display.write(str(message))
            relay.on()
            time.sleep(1)
            relay.off()
            for chord_ind in range (0,3):
                print buzzer.playSound(chords[chord_ind], 1000000)
                buzzer.stopSound();
                

    updater.idle()
