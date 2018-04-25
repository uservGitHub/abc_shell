package sample.gxd.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import gxd.utils.RingModel
import org.jetbrains.anko.dip


/**
 * Created by Administrator on 2018/4/25.
 * 带状面板配置
 */

data class BandingConfigure(
    var isHor:Boolean = true,
    var itemDefOffset:Int = 12,
    var itemDefWidth:Int = 220,
    var itemDefHeight:Int = 38) {
}

fun List<RingModel<out Any>>.toBandingPanel(ctx:Context,configure: BandingConfigure) =
        if (configure.isHor) buildHorBandingPanel(this, ctx, configure)
        else buildVerBandingPanel(this, ctx, configure)

fun Iterable<RingModel<out Any>>.toBandingPanel(ctx:Context,configure: BandingConfigure) =
        toList().toBandingPanel(ctx,configure)


private inline fun buildHorBandingPanel(items:List<RingModel<out Any>>, ctx: Context,configure: BandingConfigure) =
        HorizontalScrollView(ctx).also {
            val container = LinearLayout(ctx).apply {
                orientation = if (configure.isHor) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            }
            it.addView(container)
            val halfOffset = (configure.itemDefOffset / 2).toInt()
            items.forEach { item ->
                container.addView(item.bindUi(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                            dip(halfOffset + (item.width ?: configure.itemDefWidth)),
                            dip(item.heigth ?: configure.itemDefHeight))

                    setPadding(dip(halfOffset), 0, dip(halfOffset), 0)
                })
            }
        }

private inline fun buildVerBandingPanel(items:List<RingModel<out Any>>, ctx: Context,configure: BandingConfigure) =
        ScrollView(ctx).also {
            val container = LinearLayout(ctx).apply {
                orientation = if (configure.isHor) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            }
            it.addView(container)
            val halfOffset = (configure.itemDefOffset / 2).toInt()
            items.forEach { item ->
                container.addView(item.bindUi(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                            dip(halfOffset + (item.width ?: configure.itemDefWidth)),
                            dip(item.heigth ?: configure.itemDefHeight))

                    setPadding(dip(halfOffset), 0, dip(halfOffset), 0)
                })
            }
        }


