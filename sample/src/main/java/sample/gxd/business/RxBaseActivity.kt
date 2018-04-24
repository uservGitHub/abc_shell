package gxd.business

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import gxd.android.context.fullScreen
import gxd.android.context.notitle
import gxd.utils.LogBuilder
import gxd.utils.toRingModel

/**
 * Created by work on 2018/4/23.
 */

abstract class RxBaseActivity:AppCompatActivity(){

    protected val ringSourceCount = 5.rangeTo(14).step(3).toRingModel()
    //0表示无效，不中断
    protected val ringTBreak = 0L.rangeTo(250L).step(40L).toRingModel()
    //region    xxx1,xxx2
    protected val ringT1 = listOf(100L,150L,200L).toRingModel()
    protected val ringT2 = listOf(100L,150L,200L).toRingModel()
    //none 表示无效，不使用
    protected val ringMap1 = MapType.values().toList().toRingModel()
    protected val ringMap2 = MapType.values().toList().toRingModel()
    protected val ringThreadCount1 = 2.rangeTo(5).toRingModel()
    protected val ringThreadCount2 = 2.rangeTo(5).toRingModel()
    protected val ringThreadType1 = ThreadType.values().toList().toRingModel()
    protected val ringThreadType2 = ThreadType.values().toList().toRingModel()
    //endregion
    protected val log = LogBuilder("_Rx")

    override fun onCreate(savedInstanceState: Bundle?) {
        notitle()
        super.onCreate(savedInstanceState)
    }

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
