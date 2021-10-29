package de.tforneberg.patchdb.controller

import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

private const val PATH = "/error"

@Controller
@RequestMapping(PATH)
class ForwardToFrontendController : ErrorController {

    @RequestMapping(value = [PATH])
    fun error(): String {
        return "forward:/index.html"
    }
}