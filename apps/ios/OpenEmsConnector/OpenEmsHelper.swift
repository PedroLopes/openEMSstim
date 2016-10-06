//
//  OpenEmsHelper.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import Foundation
import CoreBluetooth

class OpenEmsHelper {
    // MARK: Message
    /**
     construct the message to send to the **openEMS**-device to fire an impulse
     
     - Parameter channel:    the channel in with the device should fire [ 0 - 1 ]
     - Parameter intensity:  the intensity with which it fires [ 0 - 100 ]
     - Parameter onTime:     the timespan for how long the impulse stays on [in milliseconds - up to 1000 ms]
     
     - Returns: the message in the format understood by the <i>openEMS</i>-device
     */
    static func constructMessage(channel : UInt, intensity : UInt, onTime : UInt) -> OpenEmsMessage {
        let secureIntensity = min(intensity, 100)
        let secureOnTime = min(onTime, 1000)
        let secureChannel = min(channel, 1)
        
        return OpenEmsMessage(channel: secureChannel, intensity: secureIntensity, onTime: secureOnTime)
    }
    
    // MARK: Concrete Checks
    /**
     checks if the UUID of a service of the **openEMS**-device has the expected descriptor
     
     - Parameter uuid: UUID of the service
     
     - Returns: *true* is the UUID matches the expected descriptor; *false* otherwise
     */
    static func isExpectedService(uuid : CBUUID) -> Bool {
        let encodedDescriptor = String(data: dataFromHexadecimalString(hexString: uuid.uuidString) as! Data, encoding: .utf8)
        
        return encodedDescriptor == OpenEmsConstants.serviceDescriptor
    }
    
    /**
     checks if the UUID of a characteristic of the **openEMS**-device has the expected descriptor
     
     - Parameter uuid: UUID of the characteristic
     
     - Returns: *true* is the UUID matches the expected descriptor; *false* otherwise
     */
    static func isExpectedCharacteristic(uuid : CBUUID) -> Bool {
        let encodedDescriptor = String(data: dataFromHexadecimalString(hexString: uuid.uuidString) as! Data, encoding: .utf8)
        
        return encodedDescriptor == OpenEmsConstants.characteristicDescriptor
    }
    
    
    // MARK: Open Checks
    static func checkUUIDString(uuid : String, descriptor : String) -> Bool {
        let encodedDescriptor = String(data: dataFromHexadecimalString(hexString: uuid) as! Data, encoding: .utf8)
        
        return encodedDescriptor == descriptor
    }
    
    static func checkUUID(uuid : UUID, descriptor : String) -> Bool {
        let encodedDescriptor = String(data: dataFromHexadecimalString(hexString: uuid.uuidString) as! Data, encoding: .utf8)
        
        return encodedDescriptor == descriptor
    }
    
    
    
    // MARK: private helpers
    // -> http://stackoverflow.com/questions/26501276/converting-hex-string-to-nsdata-in-swift
    //
    static fileprivate func dataFromHexadecimalString(hexString : String) -> NSData? {
        let data = NSMutableData(capacity: hexString.characters.count / 2)
        
        let regex = try! NSRegularExpression(pattern: "[0-9a-f]{1,2}", options: .caseInsensitive)
        regex.enumerateMatches(in: hexString, options: [], range: NSMakeRange(0, hexString.characters.count)) { match, flags, stop in
            let byteString = (hexString as NSString).substring(with: match!.range)
            var num = UInt8(byteString, radix: 16)
            data?.append(&num, length: 1)
        }
        
        return data
    }
    
    static fileprivate func hexadecimal(data : NSData) -> String {
        var string = ""
        var byte: UInt8 = 0
        
        for i in 0 ..< data.length {
            data.getBytes(&byte, range: NSMakeRange(i, 1))
            string += String(format: "%02x", byte)
        }
        
        return string
    }
}
