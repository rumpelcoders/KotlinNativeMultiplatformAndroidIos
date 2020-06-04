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

    private var slackApi: SlackApi!

    override func viewDidLoad() {
        super.viewDidLoad()


        let apiString = """
        {
          "clientId": "18044401633.961752886881",
          "clientSecret": "d7ee42e29b36e3face61217c59266b2b"
        }
        """

        slackApi = SlackApi(apiPropertiesString: apiString)

        slackApi.authorize { result in
            if result.isAuthenticated {
                print("GOOD")
            } else {
                print("NOT good")

                let webView = WKWebView(frame: CGRect(x: 0, y: 0, width: 400, height: 800))
                webView.center = CGPoint(x: 160, y: 285)

                self.view.addSubview(webView)

                webView.navigationDelegate = self
                guard let content = result.content else {
                    return
                }

                webView.loadHTMLString(content, baseURL: nil)
            }
        }
    }
}

extension ViewController: WKNavigationDelegate {

    func webView(_ webView: WKWebView, decidePolicyFor navigationResponse: WKNavigationResponse, decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void) {
        let responseUrl = navigationResponse.response.url?.absoluteString
        if responseUrl?.starts(with: "https://www.test.com") == true {
            decisionHandler(.cancel)
            self.slackApi.onRedirectCodeReceived(url: responseUrl!) {
                print("Success! - Authenticated!")
                // TODO: swap views
                self.slackApi.setState(state: "Done for today", emoji: ":oof:", duration: 420) { state in
                    print ("state set successfully to: \(state.statusText)")
                }
            }
            return
        }

        decisionHandler(.allow)
    }

}
