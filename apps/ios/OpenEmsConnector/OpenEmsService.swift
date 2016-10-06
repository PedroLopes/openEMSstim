//
//  OpenEmsService.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import Foundation
import CoreBluetooth


public class OpenEmsService : NSObject, CBPeripheralDelegate {
    fileprivate var _peripheral: CBPeripheral?
    fileprivate var _stimulationCharacteristic: CBCharacteristic?
    
    
    init(initWithPeripheral peripheral: CBPeripheral) {
        super.init()
        
        _peripheral = peripheral
        _peripheral?.delegate = self
    }
    
    deinit {
        self.reset()
    }
    
    func startDiscoveringServices() {
        _peripheral?.discoverServices(nil)
    }
    
    func reset() {
        _peripheral = nil
        _stimulationCharacteristic = nil;
    }
    
    // MARK: - Send Message
    public func sendMessage(message : OpenEmsMessage) {
        if let stimulationCharacteristic = _stimulationCharacteristic {
            let data = message.payload.data(using: .utf8)!
            print("[EMS-SERVICE] send message \(message.payload) to device")
            _peripheral?.writeValue(data, for: stimulationCharacteristic, type: .withResponse)
        }
    }
    
    public func sendImpulse(channel : UInt, intensity : UInt, onTime : UInt) {
        if let stimulationCharacteristic = _stimulationCharacteristic {
            // Need a mutable var to pass to writeValue function
            let message = OpenEmsHelper.constructMessage(channel: channel, intensity: intensity, onTime: onTime)
            let data = message.payload.data(using: .utf8)!
            print("send message :: \(message) - ch : \(channel) - int : \(intensity)")
            _peripheral?.writeValue(data, for: stimulationCharacteristic, type: .withResponse)
        }
    }
    
    
    // MARK: - CBPeripheralDelegate
    
    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        guard _peripheral == peripheral else {
            return
        }
        
        guard error == nil else {
            return
        }
        
        guard (peripheral.services != nil) && (peripheral.services!.count > 0) else {
            return
        }
        
        for service in peripheral.services! {
            if OpenEmsHelper.isExpectedService(uuid: service.uuid) {
                peripheral.discoverCharacteristics(nil, for: service)
                return
            }
        }
    }
    
    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        guard peripheral == _peripheral else {
            return
        }
        
        guard error == nil else {
            return
        }
        
        if let characteristics = service.characteristics {
            for characteristic in characteristics {
                if OpenEmsHelper.isExpectedCharacteristic(uuid: characteristic.uuid) {
                    _stimulationCharacteristic = characteristic
                    return
                }
            }
        }
    }
    
    
}
