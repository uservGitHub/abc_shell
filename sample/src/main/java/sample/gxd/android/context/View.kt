package sample.gxd.android.context

import android.view.View

/**
 * Created by Administrator on 2018/4/22.
 */

fun View.viewSize(delay:(Pair<Int,Int>)->Unit) =
        viewTreeObserver.addOnPreDrawListener {
            //在Activity.onResume之后调用
            delay(Pair<Int, Int>(width, height))
            return@addOnPreDrawListener true
        }
