//
//  OpenEmsButton.swift
//  OpenEmsConnector
//
//  Created by steff3 on 02/09/16.
//  Copyright © 2016 touchLOGIE. All rights reserved.
//

import UIKit

@IBDesignable
public class OpenEmsButton : UIButton {
    
    private var buttonIconColour : UIColor?
    private var buttonIconForeground : UIColor
    private var channelName : String?
    
    fileprivate var channel2Colour : UIColor {
        get {
            return UIColor(red: 1.000, green: 0.000, blue: 0.000, alpha: 1.000)
        }
    }
    
    fileprivate var channel1Colour : UIColor {
        get {
            return UIColor(red: 0.338, green: 0.800, blue: 0.320, alpha: 1.000)
        }
    }
    
    fileprivate var channel1Name : String {
        get {
            return "Channel 1"
        }
    }
    
    fileprivate var channel2Name : String {
        get {
            return "Channel 2"
        }
    }
    
    @IBInspectable
    public var channelNumber : Int {
        didSet {
            switch channelNumber {
            case 1:
                channelName = channel1Name
                buttonIconColour = channel1Colour
            case 2:
                channelName = channel2Name
                buttonIconColour = channel2Colour
            default:
                break
            }
        }
    }
    
    override public var isHighlighted: Bool {
        didSet {
            if isHighlighted {
                buttonIconColour = UIColor.white
                switch channelNumber {
                case 1:
                    buttonIconForeground = channel1Colour
                case 2:
                    buttonIconForeground = channel2Colour
                default:
                    break
                }
            } else {
                buttonIconForeground = UIColor.white
                switch channelNumber {
                case 1:
                    buttonIconColour = channel1Colour
                case 2:
                    buttonIconColour = channel2Colour
                default:
                    break
                }
            }
            setNeedsDisplay()
        }
    }
    
    // MARK: - Initialisers
    
    override init(frame: CGRect) {
        channelNumber = 1
        buttonIconForeground = UIColor(red: 1.000, green: 1.000, blue: 1.000, alpha: 1.000)
        
        super.init(frame: frame)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        channelNumber = 1
        buttonIconForeground = UIColor(red: 1.000, green: 1.000, blue: 1.000, alpha: 1.000)
        
        super.init(coder: aDecoder)
    }
    
    // MARK: - Drawing
    
    override public func draw(_ rect: CGRect) {
        drawButton(frame: rect)
    }
    
    func drawButton(frame: CGRect = CGRect(x: 10, y: 12, width: 218, height: 218)) {
        //// General Declarations
        let context = UIGraphicsGetCurrentContext()
        
        
        //// Subframes
        let group: CGRect = CGRect(x: frame.minX + 2, y: frame.minY + 2, width: frame.width - 4, height: frame.height - 4)
        
        
        //// Group
        //// Oval Drawing
        let ovalPath = UIBezierPath(ovalIn: CGRect(x: group.minX + floor(group.width * 0.00000 + 0.5), y: group.minY + floor(group.height * 0.00000 + 0.5), width: floor(group.width * 1.00000 + 0.5) - floor(group.width * 0.00000 + 0.5), height: floor(group.height * 1.00000 + 0.5) - floor(group.height * 0.00000 + 0.5)))
        buttonIconColour!.setFill()
        ovalPath.fill()
        buttonIconColour!.setStroke()
        ovalPath.lineWidth = 2
        ovalPath.stroke()
        
        
        //// Bezier Drawing
        let lineWidth : CGFloat = ((frame.width / 215.0) * 10.0)
        
        let bezierPath = UIBezierPath()
        bezierPath.move(to: CGPoint(x: group.minX + 0.04673 * group.width, y: group.minY + 0.45896 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.14876 * group.width, y: group.minY + 0.48053 * group.height), controlPoint1: CGPoint(x: group.minX + 0.04673 * group.width, y: group.minY + 0.45896 * group.height), controlPoint2: CGPoint(x: group.minX + 0.09971 * group.width, y: group.minY + 0.48936 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.24295 * group.width, y: group.minY + 0.42364 * group.height), controlPoint1: CGPoint(x: group.minX + 0.19782 * group.width, y: group.minY + 0.47171 * group.height), controlPoint2: CGPoint(x: group.minX + 0.20371 * group.width, y: group.minY + 0.42168 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.36853 * group.width, y: group.minY + 0.63551 * group.height), controlPoint1: CGPoint(x: group.minX + 0.28219 * group.width, y: group.minY + 0.42561 * group.height), controlPoint2: CGPoint(x: group.minX + 0.29593 * group.width, y: group.minY + 0.63649 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.53728 * group.width, y: group.minY + 0.21963 * group.height), controlPoint1: CGPoint(x: group.minX + 0.44702 * group.width, y: group.minY + 0.63551 * group.height), controlPoint2: CGPoint(x: group.minX + 0.46076 * group.width, y: group.minY + 0.21963 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.67660 * group.width, y: group.minY + 0.60608 * group.height), controlPoint1: CGPoint(x: group.minX + 0.60665 * group.width, y: group.minY + 0.21963 * group.height), controlPoint2: CGPoint(x: group.minX + 0.61773 * group.width, y: group.minY + 0.60216 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.79629 * group.width, y: group.minY + 0.42364 * group.height), controlPoint1: CGPoint(x: group.minX + 0.73547 * group.width, y: group.minY + 0.60216 * group.height), controlPoint2: CGPoint(x: group.minX + 0.74822 * group.width, y: group.minY + 0.42757 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.86693 * group.width, y: group.minY + 0.48053 * group.height), controlPoint1: CGPoint(x: group.minX + 0.81997 * group.width, y: group.minY + 0.42590 * group.height), controlPoint2: CGPoint(x: group.minX + 0.83454 * group.width, y: group.minY + 0.47131 * group.height))
        bezierPath.addCurve(to: CGPoint(x: group.minX + 0.95327 * group.width, y: group.minY + 0.45896 * group.height), controlPoint1: CGPoint(x: group.minX + 0.89091 * group.width, y: group.minY + 0.48736 * group.height), controlPoint2: CGPoint(x: group.minX + 0.95327 * group.width, y: group.minY + 0.45896 * group.height))
        bezierPath.lineCapStyle = .round;
        
        buttonIconForeground.setStroke()
        bezierPath.lineWidth = lineWidth
        bezierPath.stroke()
        
        
        //// Text Drawing
        let fontSize : CGFloat = ((frame.width / 215.0) * 28.0)
        
        let textRect = CGRect(x: group.minX + floor(group.width * 0.16822 + 0.5), y: group.minY + floor(group.height * 0.66822 + 0.5), width: floor(group.width * 0.83178 + 0.5) - floor(group.width * 0.16822 + 0.5), height: floor(group.height * 0.90187 + 0.5) - floor(group.height * 0.66822 + 0.5))
        let textTextContent = NSString(string: channelName!)
        let textStyle = NSMutableParagraphStyle()
        textStyle.alignment = .center
        
        let textFontAttributes = [NSFontAttributeName: UIFont.systemFont(ofSize: fontSize), NSForegroundColorAttributeName: buttonIconForeground, NSParagraphStyleAttributeName: textStyle] as [String : Any]
        
        let textTextHeight: CGFloat = textTextContent.boundingRect(with: CGSize(width: textRect.width, height: CGFloat.infinity), options: NSStringDrawingOptions.usesLineFragmentOrigin, attributes: textFontAttributes, context: nil).size.height
        context!.saveGState()
        context!.clip(to: textRect)
        textTextContent.draw(in: CGRect(x: textRect.minX, y: textRect.minY + (textRect.height - textTextHeight) / 2, width: textRect.width, height: textTextHeight), withAttributes: textFontAttributes)
        context!.restoreGState()
    }
    
}
