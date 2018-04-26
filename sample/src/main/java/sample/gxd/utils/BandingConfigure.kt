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
    var itemDefOffset:Int = 12, //outerOffset
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
            val count = items.count()
            if (count == 1) {
                val item = items[0]
                container.addView(item.bindUi(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                            dip(item.width ?: configure.itemDefWidth),
                            dip(configure.itemDefHeight))

                    setPadding(0, 0, 0, 0)
                })
            } else if (count > 1) {
                for (i in 0..count - 1) {
                    val item = items[i]
                    container.addView(item.bindUi(ctx).apply {
                        when {
                            i == 0 -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(halfOffset + (item.width ?: configure.itemDefWidth)),
                                        dip(configure.itemDefHeight))
                                setPadding(0, 0, dip(halfOffset), 0)
                            }
                            i == count - 1 -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(halfOffset + (item.width ?: configure.itemDefWidth)),
                                        dip(configure.itemDefHeight))
                                setPadding(dip(halfOffset), 0, 0, 0)
                            }
                            else -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(2 * halfOffset + (item.width ?: configure.itemDefWidth)),
                                        dip(configure.itemDefHeight))
                                setPadding(dip(halfOffset), 0, dip(halfOffset), 0)
                            }
                        }
                    })
                }
            }
        }

private inline fun buildVerBandingPanel(items:List<RingModel<out Any>>, ctx: Context,configure: BandingConfigure) =
        ScrollView(ctx).also {
            val container = LinearLayout(ctx).apply {
                orientation = if (configure.isHor) LinearLayout.HORIZONTAL else LinearLayout.VERTICAL
            }
            it.addView(container)
            val halfOffset = (configure.itemDefOffset / 2).toInt()
            val count = items.count()
            if (count == 1) {
                val item = items[0]
                container.addView(item.bindUi(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                            dip(configure.itemDefWidth),
                            dip(item.heigth ?: configure.itemDefHeight))

                    setPadding(0, 0, 0, 0)
                })
            } else if (count > 1) {
                for (i in 0..count - 1) {
                    val item = items[i]
                    container.addView(item.bindUi(ctx).apply {
                        when {
                            i == 0 -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(configure.itemDefWidth),
                                        dip(halfOffset + (item.heigth ?: configure.itemDefHeight)))
                                setPadding(0, 0, 0, dip(halfOffset))
                            }
                            i == count - 1 -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(configure.itemDefWidth),
                                        dip(halfOffset + (item.heigth ?: configure.itemDefHeight)))
                                setPadding(0, dip(halfOffset), 0, 0)
                            }
                            else -> {
                                layoutParams = ViewGroup.LayoutParams(
                                        dip(configure.itemDefWidth),
                                        dip(2 * halfOffset + (item.heigth ?: configure.itemDefHeight)))
                                setPadding(0, dip(halfOffset), dip(halfOffset), 0)
                            }
                        }
                    })
                }
            }
        }


