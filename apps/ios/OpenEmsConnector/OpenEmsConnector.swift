//
//  OpenEmsConnector.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import Foundation
import CoreBluetooth

public class OpenEmsConnector : NSObject, CBCentralManagerDelegate {
    
    fileprivate var _centralManager : CBCentralManager?
    fileprivate var _peripheralBLE : CBPeripheral?
    
    fileprivate var requestedConnection : Bool = true
    
    public var delegate : OpenEmsConnectorDelegate?
    
    override init() {
        super.init()
        
        let centralQueue = DispatchQueue(label: OpenEmsConstants.dispatchQueueUID)
        _centralManager = CBCentralManager(delegate: self, queue: centralQueue)
    }
    
    deinit {
        requestedConnection = false
        if let central = _centralManager {
            if let peripheral = _peripheralBLE {
                central.cancelPeripheralConnection(peripheral)
            }
        }
    }
    
    /*
        On iOS it seems that the UUID used for the service is not compliant with the standards Apple uses and requests
        for UUIDs/CBUUIDs, etc. Therefore it seems best to directly retrieve the device. Not that flexible, a
        good starting point nonetheless, I hope.
        */
    public func connect() {
        requestedConnection = true
        
        if let central = _centralManager {
            let list = central.retrievePeripherals(withIdentifiers: [OpenEmsConstants.deviceId])
            
            if list.count > 0 {
                let peripheral = list[0]
                if peripheral.name == OpenEmsConstants.deviceName {
                    _peripheralBLE = peripheral
                    
                    // Reset service
                    self.emsService = nil
                    
                    // Connect to peripheral
                    central.connect(peripheral, options: nil)
                }
            }
        }
    }
    
    public func disconnect() {
        requestedConnection = false;
        
        clearDevices()
        
    }
    
    var emsService: OpenEmsService? {
        didSet {
            if let service = self.emsService {
                service.startDiscoveringServices()
            }
        }
    }
    
    private func clearDevices() {
        self.emsService = nil
        _peripheralBLE = nil
    }
    
    // MARK: - CBCentralManagerDelegate
    
    public func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        
        // Create new service class
        if (peripheral == _peripheralBLE) {
            self.emsService = OpenEmsService(initWithPeripheral: peripheral)
            
            delegate?.didConnect(connector: self, to: self.emsService!)
        }
    }
    
    public func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        
        // See if it was our peripheral that disconnected
        if (peripheral == _peripheralBLE) {
            self.emsService = nil;
            _peripheralBLE = nil;
            
            delegate?.didDisconnect(connector: self)
        }
        
        // Start scanning for new devices
        if requestedConnection {
            connect()
        }
    }
    
    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        
        switch central.state {
        case .resetting: fallthrough
        case .poweredOff:
            // - clear devices
            clearDevices()
            break
        case .unauthorized: fallthrough
        case .unknown: fallthrough
        case .unsupported: break
        case .poweredOn:
            // seems to be the interesting case -> start scanning
            if requestedConnection {
                connect()
            }
            break
        }
    }
}
