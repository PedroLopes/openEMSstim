# Make your own openEMSstim
The autors of the original board (Max Pfeiffer and Tim Dünte) have prepared a quite comprehensible tutorial on how to use the Eagle files to make your own board.

Please refer to their tutorial:
https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/ToolKitHardware

## Part list
Please refer to here: https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home/PartList
For the openEMSstim version add a 1N4001-like SMD diode (in SMB size) such as : http://www.digikey.com/product-detail/en/diodes-incorporated/S1BB-13-F/S1BB-FDICT-ND/815933 or http://www.mouser.de/ProductDetail/Diodes-Incorporated/S1BB-13-F/?qs=4%2fHtbAejkbNHxvTlC8Melg%3d%3d

## Notes on making a openEMSstim:
Currently the openEMSstim differs only slightly from the original board.We added a diode to protect the battery in case you accidentally reserve the polarity. 

## Notes for upcoming versions:
In the upcoming versions we altered a bit more, here's a preview of what's coming up: 

1. Altered the input and output connectors
2. Added a recheargeable lipo battery instead of a 9V block
3. Added a 5V voltage regulator instead of using the built-in from the arduino.  
4. No entire arduino (much smaller package)

Note that this is an open source project, so feel free to send pull requests with your features, mods, etc! 

### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)
