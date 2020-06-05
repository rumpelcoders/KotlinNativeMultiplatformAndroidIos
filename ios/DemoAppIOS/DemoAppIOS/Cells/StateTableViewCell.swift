//
//  StateTableViewCell.swift
//  DemoAppIOS
//
//  Created by Lorenz Schmoliner on 05.06.20.
//  Copyright © 2020 Bitmovin. All rights reserved.
//

import UIKit

class StateTableViewCell: UITableViewCell {

    //MARK: Properties

    @IBOutlet weak var label: UILabel!

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
