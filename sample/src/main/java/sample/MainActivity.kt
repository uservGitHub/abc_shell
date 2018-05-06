package sample

import gxd.test.*
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.LinearLayout
import gxd.android.context.*
import gxd.business.RxBaseActivity
import gxd.utils.RingModel
import gxd.utils.toRingModel
import org.jetbrains.anko.*
import ring.TestRingActivity
import sample.gxd.android.context.viewSize
import sample.gxd.business.RxActivity
import sample.gxd.utils.BandingConfigure
import sample.gxd.utils.toBandingPanel


/**
 * Created by work on 2018/4/8.
 */

//class MainActivity:BetaRxActivity()
//class MainActivity:AlphaConfActivity()
//class MainActivity: EmptyActivity()


//class MainActivity:TestRingActivity()
//class MainActivity:RxActivity()
class MainActivity:TestRxLogActivity()

/*class MainActivity:EmptyActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ringT1 = listOf(100L,150L,200L).toRingModel()
        val ringT2 = listOf(100L,150L,200L).toRingModel()
        //none 表示无效，不使用
        val ringMap1 = RxBaseActivity.MapType.values().toList().toRingModel().also { it.ringTag = "RingMap" }
        val ringMap2 = RxBaseActivity.MapType.values().toList().toRingModel()
        val ringThreadCount1 = 2.rangeTo(5).toRingModel().also { it.width = 150 }
        val ringThreadCount2 = 2.rangeTo(5).toRingModel()
        val ringThreadType1 = RxBaseActivity.ThreadType.values().toList().toRingModel()
        val ringThreadType2 = RxBaseActivity.ThreadType.values().toList().toRingModel()

        val items = listOf(ringT1,ringMap1,ringThreadCount1,ringThreadType1)

        verticalLayout {
            addView(items.toBandingPanel(applicationContext, BandingConfigure()))
        }
    }
}*/

