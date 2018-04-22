package sample

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import gxd.android.context.fullScreen
import gxd.android.context.notitle
import gxd.android.context.screenSize
import gxd.utils.LogBuilder
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Administrator on 2018/4/22.
 */

open class EmptyActivity:AppCompatActivity() {
    private val hostName = "EmptyActivity"

    private inline fun manual(msg: Any) = log.manual(hostName, msg)
    private inline fun pilling(msg: Any) = log.pilling(hostName, msg)

    //region    公共方法
    fun updateDump(text: String) {
        tbDump.text = text
    }

    fun appendUiDump(text: String){
        runOnUiThread {
            tbDump.append(text)
        }
    }

    fun appendDump(text: String) {
        tbDump.append(text)
    }

    fun clearDump() {
        tbDump.text = ""
    }
    //更新开关，从UI设置中更新
    val updateSwitch:()->Unit = {
        log.switch(isFlow, isLogv, isPill)
    }
    //endregion

    //region    控制属性
    protected var isLogv = false
        private set
    protected var isFlow = false
        private set
    protected var isPill = false
        private set

/*    protected var t1 = 100
        private set
    protected var t2 = 100
        private set*/

    //endregion

    protected lateinit var winFrame: LinearLayout
    protected lateinit var toolHidePanel:LinearLayout
    private lateinit var tbDump: TextView

    protected val log = LogBuilder("_A").apply {
        switch(true, false, true)
        reset(hostName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        manual("preOnCreate")
        notitle()
        super.onCreate(savedInstanceState)
        pilling("ScreenSize${screenSize}")

        winFrame = linearLayout {
            orientation = LinearLayout.VERTICAL
            toolHidePanel = linearLayout {
                orientation = LinearLayout.VERTICAL
                //默认隐藏
                visibility = View.GONE
                //菜单
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    button("关闭"){
                        onClick {
                            toolHidePanel.visibility = View.GONE
                        }
                    }
                    //region    固定设置
                    button("清空") {
                        onClick { clearDump() }
                    }
                    button("显示") {
                        onClick { appendDump(log.dump) }
                    }
                    checkBox("Flow") {
                        onCheckedChange { buttonView, isChecked ->
                            isFlow = isChecked
                            updateSwitch.invoke()
                        }
                    }
                    checkBox("Pill") {
                        onCheckedChange { buttonView, isChecked ->
                            isPill = isChecked
                            updateSwitch.invoke()
                        }
                    }
                    checkBox("Log") {
                        onCheckedChange { buttonView, isChecked ->
                            isLogv = isChecked
                            updateSwitch.invoke()
                        }
                    }
                    //endregion
                }
                //dump信息
                tbDump = textView() {
                    textSize = 22F
                    gravity = Gravity.LEFT
                    typeface = Typeface.MONOSPACE
                    movementMethod = ScrollingMovementMethod.getInstance()
                }.lparams(matchParent, matchParent)

            }.lparams(width= matchParent)
            textView(){
                text = "第一层"
                gravity = Gravity.CENTER
                backgroundColor = Color.YELLOW
            }.lparams(width= matchParent,height = dip(50))
        }
    }

    override fun onResume() {
        manual("preOnResume")
        super.onResume()
        fullScreen()
    }

    /**
     * 用来唤醒影藏工具栏
     * 唤醒方式：T1时间内切换多次(大于1),并等待T2
     * 条件：只在影藏方式下才能进行唤醒
     */
    private var wakeHiddenToolTick = 0L
    private var wakingIndex = 0
    override fun onConfigurationChanged(newConfig: Configuration?) {
        //切屏太多，注释掉
        manual("preOnConfiguration")
        //pilling("ScreenSize$screenSize")
        super.onConfigurationChanged(newConfig)
        //可见返回
        if (toolHidePanel.visibility == View.VISIBLE){
            return
        }

        val current = System.currentTimeMillis()

        //待启动状态且大于3秒 开始计数
        if (current-wakeHiddenToolTick>4000 && wakingIndex == 0){
            wakingIndex++
            wakeHiddenToolTick = current
            return
        }
        //已启动且小于2.5秒 计数有效，可以触发
        if (current-wakeHiddenToolTick<2500 && wakingIndex == 1){
            wakingIndex++
            wakeHiddenToolTick = current
            //进行触发
            toolHidePanel.visibility = View.VISIBLE
            val lp = tbDump.layoutParams.apply {
                height = screenSize.second/2
            }
            tbDump.layoutParams = lp
        }
        //其他情况重置转入待启动状态
        wakeHiddenToolTick = current
        wakingIndex = 0
    }
}