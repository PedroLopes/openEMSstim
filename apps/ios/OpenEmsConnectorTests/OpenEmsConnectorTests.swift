//
//  OpenEmsConnectorTests.swift
//  OpenEmsConnectorTests
//
//  Created by steff3 on 02/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import XCTest
@testable import OpenEmsConnector

class OpenEmsConnectorTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
    func testMessage() {
        let mess1 = OpenEmsHelper.constructMessage(channel: 0, intensity: 85, onTime: 100)
        let mess2 = OpenEmsHelper.constructMessage(channel: 1, intensity: 50, onTime: 100)
        
        let mess3 = OpenEmsHelper.constructMessage(channel: 4, intensity: 150, onTime: 10000)
        
        
        XCTAssertTrue(mess1.payload == "C0I85T100G")
        XCTAssertTrue(mess2.payload == "C1I50T100G")
        XCTAssertTrue(mess3.payload == "C1I100T1000G")
    }
    
    
    func testDescriptorCheckPasses() {
        XCTAssertTrue(OpenEmsHelper.checkUUIDString(uuid: "454D532D-5365-7276-6963652D424C4531",
                                                    descriptor: OpenEmsConstants.serviceDescriptor))
        XCTAssertTrue(OpenEmsHelper.checkUUIDString(uuid: "454D532D-5374-6575-6572756E672D4348",
                                                    descriptor: OpenEmsConstants.characteristicDescriptor))
    }
    
    func testDescriptorCheckFails() {
        XCTAssertFalse(OpenEmsHelper.checkUUIDString(uuid: "454D532D-5365-7276-6963652D424C4531",
                                                     descriptor: OpenEmsConstants.characteristicDescriptor))
        XCTAssertFalse(OpenEmsHelper.checkUUIDString(uuid: "454D532D-5374-6575-6572756E672D4348",
                                                     descriptor: OpenEmsConstants.serviceDescriptor))
    }
    
}
