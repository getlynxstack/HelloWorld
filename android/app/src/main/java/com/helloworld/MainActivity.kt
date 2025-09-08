package com.helloworld

import android.app.Activity
import android.os.Bundle
import com.lynx.tasm.LynxBooleanOption
import com.lynx.tasm.LynxView
import com.lynx.tasm.LynxViewBuilder
import com.lynx.xelement.XElementBehaviors

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val lynxView: LynxView = buildLynxView()
        setContentView(lynxView)

//        val uri = if (BuildConfig.DEBUG) {
//            "http://10.0.2.2:3000/main.lynx.bundle?fullscreen=true"
//        } else {
//            "main.lynx.bundle"
//        }
        val uri = "http://10.0.2.2:3000/main.lynx.bundle?fullscreen=true"
        lynxView.renderTemplateUrl(uri, "")
    }

    private fun buildLynxView(): LynxView {
        val viewBuilder: LynxViewBuilder = LynxViewBuilder()
        viewBuilder.addBehaviors(XElementBehaviors().create())

        viewBuilder.setTemplateProvider(DemoTemplateProvider(this))
        viewBuilder.setEnableGenericResourceFetcher(LynxBooleanOption.TRUE)
        viewBuilder.setGenericResourceFetcher(DemoGenericResourceFetcher())

        return viewBuilder.build(this)
    }
}