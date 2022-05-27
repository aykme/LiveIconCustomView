package com.example.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.graphics.scale


enum class LiveImageState(val state: Int) {
    NORMAL(0),
    ACTION(1);

    fun next() = when (this) {
        NORMAL -> ACTION
        ACTION -> NORMAL
    }
}

class LiveImage @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var state = LiveImageState.NORMAL
    private val radius = 50
    private val textPaint = Paint().apply {
        color = Color.RED
        textSize = 100f
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
    }
    private val text = "You Died"
    private val normalStateImg: Bitmap = getRoundedCornerBitmap(
        R.drawable.live_icon_normal_img,
        radius
    )
    private val actionStateImg: Bitmap = getRoundedCornerBitmap(
        R.drawable.live_icon_in_action_img,
        radius
    )
    var currentBitmap = normalStateImg

    /*override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        currentBitmap = when(state) {
            LiveImageState.NORMAL -> {
                getRoundedCornerBitmap(normalStateImg, radius)
            }
            LiveImageState.ACTION -> {
                getRoundedCornerBitmap(actionStateImg, radius)
            }
        }

    }*/

    private fun getRoundedCornerBitmap(drawable: Int, radius: Int): Bitmap {
        val bitmap = BitmapFactory.decodeResource(
            context.applicationContext.resources,
            drawable
        ).scale(800, 800, false)
        val output = Bitmap.createBitmap(bitmap.width, bitmap
            .height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawBitmap(
            currentBitmap,
            ((width - normalStateImg.width) / 2).toFloat(),
            ((height - normalStateImg.height) / 2).toFloat(),
            null
        )

        if (state == LiveImageState.ACTION) {
            canvas?.drawText(text, (width/2).toFloat(), (height/2).toFloat() - (radius*2), textPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                state = LiveImageState.ACTION
                currentBitmap = actionStateImg
                invalidate()
                val scaleUp = AnimationUtils.loadAnimation(
                    context.applicationContext,
                    R.anim.scale_up
                )
                startAnimation(scaleUp)
            }
            MotionEvent.ACTION_UP -> {
                state = LiveImageState.NORMAL
                currentBitmap = normalStateImg
                invalidate()
                val scaleDown = AnimationUtils.loadAnimation(
                    context.applicationContext,
                    R.anim.scale_down
                )
                startAnimation(scaleDown)
            }
        }
        return true
    }
}
