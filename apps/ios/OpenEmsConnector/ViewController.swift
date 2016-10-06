//
//  ViewController.swift
//  OpenEmsConnector
//
//  Created by steff3 on 02/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import UIKit

class ViewController: UIViewController, OpenEmsConnectorDelegate {
    
    fileprivate var emsService : OpenEmsService?
    fileprivate var emsConnector : OpenEmsConnector?
    
    @IBOutlet weak var ch1_intensity : UISlider?
    @IBOutlet weak var ch2_intensity : UISlider?
    @IBOutlet weak var ch1_length : UISlider?
    @IBOutlet weak var ch2_length : UISlider?
    @IBOutlet weak var connectionIndicator : UILabel?
    @IBOutlet weak var titleLabel : UILabel?
    
    
    // MARK: - UIViewController standards

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        connectionIndicator?.textColor = UIColor.clear
        
        emsConnector = OpenEmsConnector()
        emsConnector?.delegate = self
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
    
    // MARK: - IBActions
    
    @IBAction func didTapTrigger(button : UIButton) {
        guard emsService != nil else {
            return
        }
        
        var message : OpenEmsMessage? = nil
        switch button.tag {
            case 0:
                message = OpenEmsHelper.constructMessage(channel: UInt(button.tag), intensity: UInt(ch1_intensity!.value), onTime: UInt(ch1_length!.value))
            case 1:
                message = OpenEmsHelper.constructMessage(channel: UInt(button.tag), intensity: UInt(ch2_intensity!.value), onTime: UInt(ch2_length!.value))
            default:
                break
        }
        
        if let message = message {
            emsService?.sendMessage(message: message)
        }
    }
    
    // MARK: - Connector Delegate
    
    func didConnect(connector: OpenEmsConnector, to service: OpenEmsService) {
        emsService = service;
        
        DispatchQueue.main.async { [weak self] in
            self?.indicateConnection(connected: true)
        }
    }
    
    func didDisconnect(connector: OpenEmsConnector) {
        emsService = nil;
        
        DispatchQueue.main.async { [weak self] in
            self?.indicateConnection(connected: false)
        }
    }
    
    fileprivate func indicateConnection(connected : Bool) {
        var colour = UIColor.darkText
        var offset = 0
        if connected {
            colour = UIColor.blue
            offset = -4
        }
        let titleText = (self.titleLabel?.attributedText)!
        let indicatingText = "stim"
        let range = (titleText.string as NSString).range(of: indicatingText)
        let attributedString = NSMutableAttributedString(string:titleText.string)
        attributedString.addAttribute(NSForegroundColorAttributeName, value: colour , range: range)
        attributedString.addAttributes([ NSBaselineOffsetAttributeName : offset ], range: range)
        
        self.titleLabel?.attributedText = attributedString
    }
}

