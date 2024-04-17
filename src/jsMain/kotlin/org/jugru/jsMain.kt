package org.jugru

import kotlinx.browser.document
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.asList


fun main() {
    document.addEventListener("DOMContentLoaded", {
        renderComposable("root") {
            Application()
        }
    })
}