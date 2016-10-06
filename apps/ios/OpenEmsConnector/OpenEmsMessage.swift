//
//  OpenEmsMessage.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import Foundation

public struct OpenEmsMessage {
    fileprivate let channel : UInt
    fileprivate let intensity : UInt
    fileprivate let onTime : UInt
    
    init(channel : UInt, intensity : UInt, onTime : UInt) {
        self.channel = channel
        self.intensity = intensity
        self.onTime = onTime
    }
    
    public var payload : String {
        get {
            return "C\(channel)I\(intensity)T\(onTime)G"
        }
    }
}
