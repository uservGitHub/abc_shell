package sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import gxd.android.context.fullScreen
import gxd.android.context.notitle

/**
 * Created by Administrator on 2018/4/22.
 */

open class EmptyActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        notitle()
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        fullScreen()
    }
}