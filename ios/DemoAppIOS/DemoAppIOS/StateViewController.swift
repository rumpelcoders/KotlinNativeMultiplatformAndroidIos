//
//  StateViewController.swift
//  DemoAppIOS
//
//  Created by Lorenz Schmoliner on 05.06.20.
//  Copyright © 2020 Bitmovin. All rights reserved.
//

import UIKit
import Core
import os.log

class StateViewController: UIViewController, UITextFieldDelegate, UINavigationControllerDelegate {

    //MARK: Properties
    @IBOutlet weak var textField: UITextField!
    @IBOutlet weak var emojiField: UITextField!
    @IBOutlet weak var durationField: UITextField!

    @IBOutlet weak var saveButton: UIBarButtonItem!
    /*
         This value is either passed by `StateTableViewController` in `prepare(for:sender:)`
         or constructed as part of adding a new state.
     */
    var state: SlackState?

    override func viewDidLoad() {
        super.viewDidLoad()

        // Handle the text field’s user input through delegate callbacks.
        textField.delegate = self
        emojiField.delegate = self

        // Enable the Save button only if the text field has a valid State name.
        updateSaveButtonState()
    }

    //MARK: UITextFieldDelegate

    func textFieldDidEndEditing(_ textField: UITextField) {
        updateSaveButtonState()

        navigationItem.title = textField.text
    }

    //MARK: Navigation

    @IBAction func cancel(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)
    }

    // This method lets you configure a view controller before it's presented.
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

        super.prepare(for: segue, sender: sender)

        // Configure the destination view controller only when the save button is pressed.
        guard let button = sender as? UIBarButtonItem, button === saveButton else {
            os_log("The save button was not pressed, cancelling", log: OSLog.default, type: .debug)
            return
        }

        let name = textField.text ?? ""
        let emoji = emojiField.text ?? ""
        let duration = durationField.text ?? "0"

        // Set the state to be passed to MealTableViewController after the unwind segue.
        state = SlackState(statusText: name, statusEmoji: emoji, statusExpiration: Int64(duration) ?? 0)
    }

    //MARK: Private Methods

    private func updateSaveButtonState() {
        // Disable the Save button if the text field is empty.
        let text = textField.text ?? ""
        let emoji = emojiField.text ?? ""

        saveButton.isEnabled = !text.isEmpty && !emoji.isEmpty
    }

}
