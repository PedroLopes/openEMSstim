# openEMSstim: the schematics 

The openEMSstim is a mod by [Pedro Lopes](plopes.org) of the [EMS toolkit](https://bitbucket.org/MaxPfeiffer/letyourbodymove/wiki/Home). See the License which acknoledges all the work from the original makers. This project is forked to (1) provide a simpler design with a few changes, (2) preserve the original design and credit without pulling all the changes to it and (3) be used in the [UIST Student Innovation Contest 2016](https://uist.acm.org/uist2016/contest) without needing to change the instructions of the original project which is meant for HCI researchers and not for a UIST student audience. 	
## How to open the schematics
These were done in **Eagle**. So that will be the easiest. Eagle is free to use but not open source, for that matter we are trying to migrate these schematics to a KiCad format which fits better with the nature of this project. Hang in there or help us by doing it and sending us a pull request. 

## Status
Note that V3 hasn't been tested. If you want a stable version, roll back to V2 by doing:
``git checkout 3d5f0cdcc33ded0a633e556be4b3511867ce1fb2``

## Detailed differences to the original board (openEMSstim_03)
1. Diode protection against reverse polarity (using http://www.mouser.com/ds/2/302/1PS70SB10-838739.pdf)
2. Marked + and - on the board layout, so you know how to plug the battery
3. ON/OFF slider switch (using http://www.alps.com/prod/info/E/HTML/Switch/Slide/SSSS8/SSSS810701.html)
4. (more to come, see hardware specifications in the tutorials folder) 

### License and Liability

Please refer to the liability waiver (in documentation/liability_waiver.md).

Please refer to the license (in /license.md)

