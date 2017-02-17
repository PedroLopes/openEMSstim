# A space for collecting ideas regarding openEMSstim

## Hardware
1. Max reported that the ems modules work better on USB than 9V, this must be checked, I believe it is related to the 5V regulator used by the arduino. 
2. Add the voltage regulator
3. Add the protection FET (inverted to prevent reverse polarity)
4. Add a LiPo battery and a charging circuit
5. Change the connectors to simple audio plugs and use this for cables:
- https://www.alibaba.com/product-detail/banana-connector-medical-funcation-electrode-wire_850949001.html
6. Add screws to the board, requested by Rob
7. Add Wifi/Bluetooth double shield instead: https://www.seeedstudio.com/ESP-32S-Wifi-Bluetooth-Combo-Module-p-2706.html

### Next version of the protocol (future)

Right now, the protocol is the same as on the board by the original authors. We plan to simplify it soon. The plan is:

1. command generation is handled by a function, never by raw text
2. there will be no time expiration for a command
3. commands will be shorter, here's a preview (work in progress):

| Command       | Values   | Sample  	| Description | Lenght (bits) | 
| ------------- |:--------:| ---------:|------------:|------------:|
| Channel 		|0-1		|0  		|Set channel 0 or 1| 1 | 
| Intensity		|0-100 		|100		|Set intensity in steps of digipot | ? |  
| Ack needed?   |0-1 (N/Y) 	|0			|Writes back via the BT channel an ACK| 1 |
| Checksum	    |       	|0			|(Checksum of Channel + Intensity + Ack Needed) modulus 8| ? |  

**Why: ** Overall this simplifies the protocol, and generates a smaller payload too. The programmer must keep an eye for commands arriving/etc. Because the time-expiration will not be per command, it can be added to the device directly or on top by adding a "keep alive" protocol. 


## Documentation
1. Make an instructables page


## Software
1. Add apps to Google Play Store
2. Add app to App store (future iPhone app)
3. Android apps should register a [notification](http://www.androidbegin.com/tutorial/android-broadcast-receiver-notification-tutorial/) while running

### Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).
