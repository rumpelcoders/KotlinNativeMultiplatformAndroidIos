//
//  ViewController.swift
//  DemoAppIOS
//
//  Created by jarhoax on 03.01.20.
//

import UIKit
import Core
import AVFoundation
import AVKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let label = UILabel(frame: CGRect(x: 0, y: 0, width: 300, height: 21))
        label.center = CGPoint(x: 160, y: 285)
        label.textAlignment = .center
        label.font = label.font.withSize(25)
        label.text = CommonKt.createApplicationScreenMessage()
        view.addSubview(label)
    
        let example = ExampleClass()
        example.doSomething(param: "Hello iOS world")
    }
}
