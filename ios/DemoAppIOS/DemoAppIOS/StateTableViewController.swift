//
//  SecondViewController.swift
//  DemoAppIOS
//
//  Created by Lorenz Schmoliner on 05.06.20.
//  Copyright © 2020 Bitmovin. All rights reserved.
//

import UIKit
import Core

class StateTableViewController: UITableViewController {

    var items = [SlackState]()

    override func viewDidLoad() {
        super.viewDidLoad()

        title = "Slack States on Steroids"
        loadSampleItems()
    }

    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        // Table view cells are reused and should be dequeued using a cell identifier.
        let cellIdentifier = "StateTableViewCell"

        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? StateTableViewCell  else {
            fatalError("The dequeued cell is not an instance of MealTableViewCell.")
        }

        // Fetches the appropriate state item for the data source layout.
        let state = items[indexPath.row]

        cell.label.text = state.statusText

        return cell
    }

    @IBAction func unwindToStateList(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.source as? StateViewController, let state = sourceViewController.state {

            // Add a new meal.
            let newIndexPath = IndexPath(row: items.count, section: 0)

            items.append(state)
            tableView.insertRows(at: [newIndexPath], with: .automatic)
        }
    }

    // MARK: - Private methods
    private func loadSampleItems() {
        let item1 = SlackState(statusText: "at lunch", statusEmoji: ":knife_fork_plate:", statusExpiration: 50)
        let item2 = SlackState(statusText: "AFK", statusEmoji: ":coffee:", statusExpiration: 20)

        items += [item1, item2]
    }

}