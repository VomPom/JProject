package wang.julis.jproject.example.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Size
import android.view.ViewGroup
import android.widget.FrameLayout
import wang.julis.jwbase.Utils.DensityUtil
import kotlin.math.abs

/**
 *
 * Created by @juliswang on 2024/04/02 14:45
 *
 * @Description
 */
class BorderView @JvmOverloads constructor(
   context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val DRAG_ADSORPTION_THRESHOLD = 4f * DensityUtil.dip2px(context,2.0f)


    private var position: PointF = PointF()
    private var size = Size(0, 0)
    private var transformMatrix = Matrix()
    private val borderRectF = RectF()

    private var rotate = 0f
    private var scale = 1f
    private var isActive = true

    private val borderPaint: Paint = createPaint()

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        drawBorder(canvas)
    }

    private fun drawBorder(canvas: Canvas) {
        if (!isActive) {
            return
        }
        val centerX = position.x + size.width / 2
        val centerY = position.y + size.height / 2
        transformMatrix.apply {
            reset()
            postRotate(rotate, centerX, centerY)
        }
        canvas.save()
        canvas.concat(transformMatrix)

        transformMatrix.apply {
            reset()
            postScale(scale, scale, centerX, centerY)
        }

        borderRectF.apply {
            left = position.x
            right = position.x + size.width
            top = position.y
            bottom = position.y + size.height
        }
        transformMatrix.mapRect(borderRectF)
        canvas.drawRect(borderRectF, borderPaint)
        canvas.restore()
    }

    private fun createPaint(): Paint {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 10f
        return paint
    }

    fun positionInterceptor(x: Float, y: Float): PointF? {
        val point = PointF(x, y)

        if (abs(x + (size.width - (parent as ViewGroup).width) / 2) < DRAG_ADSORPTION_THRESHOLD) {
            point.x = (((parent as ViewGroup).width - size.width) / 2).toFloat()
        }

        if (abs(y + (size.height - (parent as ViewGroup).height) / 2) < DRAG_ADSORPTION_THRESHOLD) {
            point.y = (((parent as ViewGroup).height - size.height) / 2).toFloat()
        }

        return point
    }


    fun updateTransform(transform: EditViewTransform) {
        position = transform.position
        size = transform.size
        rotate = transform.rotation
        scale = transform.scale
        invalidate()
    }

    fun setActive(active: Boolean) {
        this.isActivated = active
    }

    fun isActive(): Boolean {
        return isActivated
    }

    data class EditViewTransform(
        var position: PointF = PointF(),
        var size: Size = Size(0, 0),
        var scale: Float = 1F,
        var rotation: Float = 0F,
    )
}