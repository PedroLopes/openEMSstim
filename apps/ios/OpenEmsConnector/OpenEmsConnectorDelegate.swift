//
//  OpenEmsConnectorDelegate.swift
//  OpenEmsConnector
//
//  Created by steff3 on 03/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import Foundation

public protocol OpenEmsConnectorDelegate {
    func didConnect(connector : OpenEmsConnector, to service : OpenEmsService)
    func didDisconnect(connector : OpenEmsConnector)
}
