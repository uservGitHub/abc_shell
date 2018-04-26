package gxd.business

import android.graphics.Typeface
import android.os.Bundle
import android.widget.HorizontalScrollView
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import gxd.android.context.dragPanel
import gxd.android.context.fullScreen
import gxd.android.context.notitle
import gxd.utils.LogBuilder
import gxd.utils.RingModel
import gxd.utils.toRingModel
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import sample.gxd.utils.BandingConfigure
import sample.gxd.utils.toBandingPanel

/**
 * Created by work on 2018/4/23.
 */

abstract class RxBaseActivity:AppCompatActivity(){

    protected val ringSourceCount = 5.rangeTo(14).step(3).toRingModel().apply { ringTag = "发射数量" }
    //0表示无效，不中断
    protected val ringTBreak = 0L.rangeTo(250L).step(40L).toRingModel().apply { ringTag = "中断点" }
    //region    xxx1,xxx2
    protected val ringT1 = listOf(100L,150L,200L).toRingModel().apply { ringTag = "T1" }
    protected val ringT2 = listOf(100L,150L,200L).toRingModel().apply { ringTag = "T2" }
    //none 表示无效，不使用
    protected val ringMap1 = MapType.values().toList().toRingModel().apply { ringTag = "转换1" }
    protected val ringMap2 = MapType.values().toList().toRingModel().apply { ringTag = "转换2" }
    protected val ringThreadCount1 = 2.rangeTo(5).toRingModel().apply { ringTag = "线程数量1" }
    protected val ringThreadCount2 = 2.rangeTo(5).toRingModel().apply { ringTag = "线程数量2" }
    protected val ringThreadType1 = ThreadType.values().toList().toRingModel().apply { ringTag = "线程类型1" }
    protected val ringThreadType2 = ThreadType.values().toList().toRingModel().apply { ringTag = "线程类型2" }
    //endregion

    private lateinit var tbDump:TextView

    protected val log = LogBuilder("_Rx")

    override fun onCreate(savedInstanceState: Bundle?) {
        notitle()
        super.onCreate(savedInstanceState)
        val items1 = listOf(ringT1,ringMap1,ringThreadCount1,ringThreadType1)
        val items2 = listOf(ringT2,ringMap2,ringThreadCount2,ringThreadType2)
        val bandSet = BandingConfigure()
        verticalLayout {
            horizontalScrollView{
                setPadding(dip(5),dip(5),dip(5),dip(7))
                //region    总体设置
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL
                    button("Clr") {
                        onClick { clearDump() }
                    }
                    checkBox("Flow") {
                        onCheckedChange { buttonView, isChecked ->
                            isFlow = isChecked
                        }
                    }
                    checkBox("Pill") {
                        onCheckedChange { buttonView, isChecked ->
                            isPill = isChecked
                        }
                    }
                    checkBox("Log") {
                        onCheckedChange { buttonView, isChecked ->
                            isLogv = isChecked
                        }
                    }


                }
                //endregion
            }

            addView(items1.toBandingPanel(ctx,bandSet).apply {
                setPadding(dip(5),0,dip(5),dip(2.5F))
            })
            addView(items2.toBandingPanel(ctx,bandSet).apply {
                setPadding(dip(5),dip(2.5F),dip(5),dip(5))
            })

            linearLayout {
                setPadding(dip(5),dip(7),dip(5),dip(5))
                orientation = LinearLayout.VERTICAL
                tbDump = textView() {
                    textSize = 22F
                    gravity = Gravity.LEFT
                    typeface = Typeface.MONOSPACE
                    movementMethod = ScrollingMovementMethod.getInstance()
                }.lparams(matchParent, matchParent)
            }.lparams(matchParent, 0, 1F)
        }

    }

    //region    控制属性
    protected var isLogv = false
        private set
    protected var isFlow = false
        private set
    protected var isPill = false
        private set
    //endregion

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
    //endregion
    override fun onResume() {
        super.onResume()
        fullScreen()
    }

    abstract fun businessSetup()

    enum class ThreadType{
        io,cpu,main,single,cache,fixed,schedule
    }
    enum class MapType{
        flatMap,map,none
    }
}
