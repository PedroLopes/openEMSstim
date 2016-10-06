//
//  OpenEmsConstants.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

// <key>ITSAppUsesNonExemptEncryption</key><false/>

import Foundation

public struct OpenEmsConstants {
    public static var serviceDescriptor : String {
        get {
            return "EMS-Service-BLE1"
        }
    }
    
    public static var characteristicDescriptor : String {
        get {
            return "EMS-Steuerung-CH"
        }
    }
    
    public static var deviceName : String {
        get {
            return "xxxx"           // <- enter the name of your open EMS stim device here
        }
    }
    
    public static var deviceId : UUID {
        get {
            return UUID(uuidString:"xxxx")!     // <- enter the UUID of your Open EMS stim device here
        }
    }
    
    public static var dispatchQueueUID : String {
        get {
            return "xxxx"  // <- enter your domain there
        }
    }
}
