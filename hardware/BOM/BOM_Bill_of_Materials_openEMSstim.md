#Bill of materials to build an openEMSstim:

##The components


|  Component | Quantity per board  | Purchase link | model ID | function | notes |  
| ------------- |:--------:| ---------:|------------:|------------:|------------:|
| ** MOSFET ** (SMD) | 4 | http://www.mouser.de/ProductDetail/STMicroelectronics/STD25NF20/?qs=%2fha2pyFadugG3OhZjoDFKyh0rk18p6Gk6XtsUQDOdFA%3d  | STD25NF20 | reduces the EMS signal | we recommend you get exactly these ones. |
| ** Digital Potenciometer ** (SMD) | 1 | http://www.mouser.de/ProductDetail/Analog-Devices/AD5252BRUZ1/?qs=sGAEpiMZZMuD%2f7PTYBwKqdeb0s0H1yW0gAAjGmwfPX4%3d | AD5252BRUZ1  | regulates the intensity of the MOSFETS (acts like a analog potentiometer) |We recommend this one. Otherwise you will have to alter code and layout substantially  |
| ** MOSFET driver  ** (SMD) | 2 | http://www.mouser.de/ProductDetail/Vishay/VOM1271T/?qs=%2fha2pyFadugAdfNL8MEHhsco%2fxls%252bKOnrvZCnTuskpQ%3d | VOM1271 | controls the MOSFETs | We recommend using exactly this component.|
| ** Optocoupler ** (SMD) | 4 | http://www.mouser.de/ProductDetail/Vishay-Semiconductors/LH1546ADF/?qs=sGAEpiMZZMsUriz2CNI3EztS13T2tkDfLMSNBrdz0Go%3d | LH1546ADF | Opens/Closes EMS channels, safety feature. | You can probably replace this by any other optocoupler.  |
| ** Bluetooth LE ** (SMD) | 1 | http://www.mouser.de/ProductDetail/Microchip-Technology/RN4020-V-RM120/?qs=sGAEpiMZZMuw1rG4%252bG7fprvXSJvrEemrHKaN1GsnGpE%3d | RN4020-V-RM120 | wireless communication between the arduino and phones/etc, via bluetooth.  |  this is a 4.0 low energy device. You can build this device without it, or you can use another module but you might have to change board layout and/or code (code is made for this one). |
| Headers for board to host the arduino (SMD) | 2 | http://www.mouser.de/ProductDetail/Harwin/M20-8771546/?qs=sGAEpiMZZMs%252bGHln7q6pmzlZUuX%2f53qjH35u20JRJjk%3d | 3M 30306-5002HB | electrode cables connect here | If you prefer through-hole headers you will have to change the board layout + routing on the other side. | 
| Header for EMS signals | 2|  http://www.mouser.de/ProductDetail/3M-Electronic-Solutions-Division/30306-5002HB/?qs=sGAEpiMZZMs%252bGHln7q6pmwu5ra4CY41iJpcAbbk2xIE%3d | 3M 30306-5002HB |  connecting to electrodes / EMS machine | |
| ** Diode ** (SMD) | 1 | http://www.mouser.de/ProductDetail/NXP-Semiconductors/1PS70SB10115/?qs=sGAEpiMZZMtQ8nqTKtFS%2fPOtlaMxh7PwzjxDSYDALEk= | 1PS70SB10,115 |  Voltage reversal protection | Not tested yet, part of V3 design only |
| ** Slide Switch ** (SMD) | 1 | http://www.mouser.de/ProductDetail/ALPS/SSSS810701/?qs=sGAEpiMZZMtHXLepoqNyVe%252bcQMRoBF1BAzyvwoNmgBo%3d | SSSS810701 | on/off switch, interrupts battery | Not tested yet, part of V3 design only | 


###Notes about MOSFETS
The board is calibrated to these MOSFETS, in fact we have detected that differences between MOSFETS actually result in perceivable differences in the intensity of the EMS channel. For now, until we find another board design without this component, we recommend you get exactly these ones. 

##The resistors and capacitors (all SMD of size: 0805)


| value | quantity per board |
| ------------- |:--------:|
| 1,5MΩ | 2 | 
| 4,7kΩ | 2 | 
| 2,2kΩ | 2 | | 1kΩ | 4 | | 470Ω | 1 | | 220Ω | 4 | | 100nf | 2 | 
￼##The LEDs (all SMD of size: 0603)1x Green LED, 1x Blue LED, 1x Red LED￼
##The electrode cables

|  Component | Quantity per board  | Purchase link | model ID | function | notes |  
| ------------- |:--------:| ---------:|------------:|------------:|------------:|
| Electrode cables (ribbon cable) | 2m | http://www.mouser.de/ProductDetail/3M-Electronic-Solutions-Division/3365-26-CUT-LENGTH/?qs=sGAEpiMZZMsJiFh04Lj2rhlO6VJHoBHccza31peO430%3d | ribbon cable | Takes EMS signal. | |
| Headers for cable (female)| 2 | http://www.mouser.de/ProductDetail/FCI/71600-006LF/?qs=sGAEpiMZZMs%252bGHln7q6pm24n0txessAMv97Wpyh9hZc%3d | 1600-006LF | Connects to openEMSstim board. | | 
| pins for cable | 4 | https://www.conrad.de/de/crimpkontakt-polzahl-gesamt-1-te-connectivity-60620-1-1-st-1422676.html | crimp pin connector |  connects to pigtail electrodes | | 



