package gxd.android.context

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.util.DisplayMetrics
import android.widget.LinearLayout
import gxd.utils.RingModel
import org.jetbrains.anko.*


/**
 * Created by Administrator on 2018/4/22.
 */

inline fun AppCompatActivity.notitle() = supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

inline fun AppCompatActivity.fullScreen() = window.apply {
    setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            WindowManager.LayoutParams.FLAG_FULLSCREEN or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
}

val AppCompatActivity.screenSize:Pair<Int,Int>
    get() {
        return DisplayMetrics().let {
            windowManager.defaultDisplay.getMetrics(it)
            val nativeHeight = navigationBarHeight
            Pair<Int, Int>(it.widthPixels, if (nativeHeight < 0) -1 else it.heightPixels + nativeHeight)
        }
    }

inline val AppCompatActivity.navigationBarHeight:Int
    get() {
        val id = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (id > 0) resources.getDimensionPixelSize(id) else -1
    }

inline val AppCompatActivity.statusBarHeight:Int
    get() {
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (id > 0) resources.getDimensionPixelSize(id) else -1
    }

inline fun<T> Activity.dragPanel(vararg rings:RingModel<T>) = horizontalScrollView {
    linearLayout {
        orientation = LinearLayout.HORIZONTAL
        rings.forEach { ring ->
            button("${ring.value!!}")
            //ring.bindUi(context).lparams(width = dip(210), height = dip(40))
        }
    }
}