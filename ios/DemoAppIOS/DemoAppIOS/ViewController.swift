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
import WebKit

class ViewController: UIViewController {

    @IBOutlet weak var webView: WKWebView?

    private var slackApi: SlackApi!

    override func viewDidLoad() {
        super.viewDidLoad()

        let properties = loadPropertiesJsonString()

        slackApi = SlackApi(apiPropertiesString: properties)
    }

    override func viewDidAppear(_ animated: Bool) {
        slackApi.authorize { result in
                    if result.isAuthenticated {
                        print("Authenticated. Continuing directly")
                        let request = URLRequest(url: URL(string: "https://www.google.at")!)

                        self.webView?.load(request)
                        self.showList()
        //                self.slackApi.setState(state: "Done for today", emoji: ":beers:", duration: 420) { state in
        //                    print ("state set successfully to: \(state.statusText)")
        //                }
                    } else {
                        print("Not Authenticated. Showing WebView")
                        self.webView?.navigationDelegate = self
                        guard let content = result.content else {
                            return
                        }

                        let request = URLRequest(url: URL(string: content)!)

                        self.webView?.load(request)
                    }
                }
    }

    private func loadPropertiesJsonString() -> String {
        guard let filepath = Bundle.main.path(forResource: "properties", ofType: "json") else {
            print("Properties file not found!")
            return ""
        }

        do {
            let contents = try String(contentsOfFile: filepath)
            return contents
        } catch {
            print("Failed to read properties file!")
        }

        return ""
    }

    private func showList() {
        let mainStoryBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)

        let listViewController = mainStoryBoard.instantiateViewController(withIdentifier: "StateTableViewController") as! StateTableViewController
        self.navigationController?.setViewControllers([listViewController], animated: true)
    }
}

extension ViewController: WKNavigationDelegate {

    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        let responseUrl = navigationResponse.response.url?.absoluteString
        if responseUrl?.starts(with: "https://www.test.com") == true {
            decisionHandler(.cancel)
            self.slackApi.onRedirectCodeReceived(url: responseUrl!) {
                print("Success! - Authenticated!")
                self.showList()
            }
            return
        }

        decisionHandler(.allow)
    }

}
