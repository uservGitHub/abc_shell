package gxd.utils

import android.content.Context
import android.graphics.*
import android.view.View
import gxd.utils.HalfSplitGestureManager

/**
 * Created by work on 2018/4/23.
 */


fun <T> Iterable<T>.toRingModel() = RingModel<T>(toList())
fun <T> List<T>.toRingModel() = RingModel<T>(this)

/**
 * 环状模型
 */
class RingModel<T>(private val list:List<T>) {

    //region    对数据的接口
    /**
     * -1 表示无效值
     */
    var ind = -1
        private set

    fun inc() {
        ind++
        adjustInd()
    }

    fun dec() {
        ind--
        adjustInd()
    }

    /**
     * 值（当前值）
     */
    val value: T?
        get() = if (ind >= 0 && ind < list.size) list[ind] else null
    //endregion

    private inline fun adjustInd() {
        if (ind < 0) ind += list.size
        ind = ind.rem(list.size)
    }

    init {
        if (!list.isEmpty()) {
            ind = 0
        }
    }

    var renderView: InnerView? = null
        private set
    private var showText: ((T) -> String)? = null

    fun setOnShowText(toText: (T) -> String) {
        showText = toText
    }

    fun bindUi(ctx: Context): View {
        if (renderView == null) {
            renderView = InnerView(ctx).apply {
                invalidate()
            }
        }
        return renderView!!
    }

    var ringTag: String = ""

    private inline fun makeText() =
            if (value == null) "null"
            else showText?.invoke(value!!) ?: value.toString()

    inner class InnerView(ctx: Context) : View(ctx) {
        private val gestureManager: HalfSplitGestureManager

        init {
            gestureManager = HalfSplitGestureManager(this,
                    {
                        this@RingModel.dec()
                        invalidate()
                    },
                    {
                        this@RingModel.inc()
                        invalidate()
                    })
        }

        override fun onDraw(canvas: Canvas) {
            //背景是透明的
            val text = "$ringTag: ${makeText()}"
            drawDefaultText(canvas, text)
        }

        /*private val backgroundRect: Rect
            get() = Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)*/
    }
    companion object {
        private inline fun deltaCenterHeightFromFont(paint: Paint) =
                paint.fontMetricsInt.let {
                    (it.top + it.bottom) / 2
                }
        private inline fun textPaint(colorInt: Int, fontSize: Float, fontType:Typeface) =
                Paint().apply {
                    color = colorInt
                    style = Paint.Style.FILL
                    textAlign = Paint.Align.CENTER
                    textSize = fontSize
                    flags = Paint.ANTI_ALIAS_FLAG
                    typeface = fontType
                }
        private val TEXT_PAINT:Paint
            get() = textPaint(FONT_COLOR, FONT_SIZE, FONT_FACE)
        private val TEXT_PAINT_BOLD:Paint
            get() = textPaint(FONT_COLOR, FONT_SIZE, FONT_FACE).apply { isFakeBoldText = true }

        fun View.drawDefaultText(canvas: Canvas, text: String, isLock: Boolean = false, isBold: Boolean = true) {
            val rect = Rect(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom)
            canvas.drawRect(rect, Paint().apply { color = if (isLock) Color.LTGRAY else Color.YELLOW })

            if (text.isNotEmpty()) {
                textDrawCenter(canvas,
                        if (isBold) TEXT_PAINT_BOLD else TEXT_PAINT_BOLD,
                        rect,
                        text)
            }
        }

        private inline fun textDrawCenter(canvas: Canvas, textPaint: Paint, rect: Rect, msg: String) {
            canvas.drawText(msg,
                    rect.exactCenterX(), (rect.exactCenterY() - deltaCenterHeightFromFont(textPaint)),
                    textPaint)
        }

        //region    可动态改变
        val FONTSIZE_RINGVALUE = 20.rangeTo(30).step(2).toRingModel()
        val FONTFACE_RINGVALUE = listOf(Typeface.DEFAULT,
                Typeface.DEFAULT_BOLD,
                Typeface.MONOSPACE,
                Typeface.SANS_SERIF,
                Typeface.MONOSPACE).toRingModel()
        val FONTCOLOR_RINGVALUE = listOf<Pair<Int, String>>(
                Pair(Color.BLACK,"black"),
                Pair(Color.RED,"red"),
                Pair(Color.DKGRAY,"dkgray"),
                Pair(Color.BLUE,"blue")
        ).toRingModel()

        val FONT_SIZE: Float
            get() = FONTSIZE_RINGVALUE.value!!.toFloat()
        val FONT_FACE: Typeface
            get() = FONTFACE_RINGVALUE.value!!
        val FONT_COLOR: Int
            get() = FONTCOLOR_RINGVALUE.value!!.first
        //endregion
    }
}
