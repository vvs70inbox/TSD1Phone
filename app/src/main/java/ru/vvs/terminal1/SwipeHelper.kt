package ru.vvs.terminal1

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.max

@SuppressLint("ClickableViewAccessibility")
abstract class SwipeHelper(private val recyclerView: RecyclerView) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var swipedPosition = -1
    private val buttonsBuffer: MutableMap<Int, List<UnderlayButton>> = mutableMapOf()
    private var swipeProgressMap = mutableMapOf<Int, Float>()
    private val recoverQueue = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            if (contains(element)) return false
            return super.add(element)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        if (swipedPosition < 0) return@OnTouchListener false
        buttonsBuffer[swipedPosition]?.forEach { it.handle(event) }
        recoverQueue.add(swipedPosition)
        swipedPosition = -1
        recoverSwipedItem()
        true
    }

    init {
        recyclerView.setOnTouchListener(touchListener)
    }

    private fun recoverSwipedItem() {
        while (recoverQueue.isNotEmpty()) {
            val position = recoverQueue.poll() ?: return
            recyclerView.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        dX: Float, pos: Int
    ) {
        var right = itemView.right
        buttons.forEach { button ->
            val width = button.intrinsicWidth / buttons.intrinsicWidth() * abs(dX)
            val left = right - width
            button.onDraw(
                canvas,
                RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat()), pos
            )
            right = left.toInt()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val position = viewHolder.adapterPosition
        var maxDX = dX
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            swipeProgressMap[position] = abs(dX) / viewHolder.itemView.width.toFloat()
            if (dX < 0) {
                if (!buttonsBuffer.containsKey(position)) {
                    buttonsBuffer[position] = instantiateUnderlayButton(position)
                }

                val buttons = buttonsBuffer[position] ?: return
                if (buttons.isEmpty()) return
                maxDX = max(-buttons.intrinsicWidth() * 1.5f, dX)
                drawButtons(c, buttons, itemView, maxDX, position)
            }
        }

        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            maxDX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        Log.d("AAA", "вызвали getSwipeEscapeVelocity $defaultValue")
        return defaultValue
    }


    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        val position = viewHolder.adapterPosition
        val progress = swipeProgressMap.getOrDefault(position, 0f)
        val swipeThreshold = if (progress > 0.4) 1f else 0.05f
        Log.d("AAA", "вызвали getSwipeThreshold $swipeThreshold")
        return swipeThreshold
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        swipeProgressMap.remove(viewHolder.adapterPosition)
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        Log.d("AAA", "вызвали getSwipeVelocityThreshold $defaultValue")
        return defaultValue
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        if (swipedPosition != position) recoverQueue.add(swipedPosition)
        swipedPosition = position
        recoverSwipedItem()
    }

    abstract fun instantiateUnderlayButton(position: Int): List<UnderlayButton>

    interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    class UnderlayButton(
        private val context: Context,
        private val title: String,
        textSize: Float,
        @ColorRes private val colorRes: Int,
        @DrawableRes private val iconRes: Int? = null,
        iconSize: Int? = null,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var pos: Int = 0
        private var clickableRegion: RectF? = null
        private val textSizeInPixel: Float =
            textSize * context.resources.displayMetrics.density // dp to px
        private val iconSizeInPixel: Float =
            iconSize?.let { it * context.resources.displayMetrics.density } ?: 50f
        private val horizontalPadding = 25.0f
        private val verticalPadding = horizontalPadding
        val intrinsicWidth: Float

        init {
            val paint = Paint()
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.LEFT
            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)
            intrinsicWidth = (titleBounds.width() + 2 * horizontalPadding)
        }

        fun onDraw(canvas: Canvas, rect: RectF, pos: Int) {

            val paint = Paint()

            // Фон
            paint.color = ContextCompat.getColor(context, colorRes)
            canvas.drawRect(rect, paint)

            // Расчет размера иконки
            val iconLeft = rect.left + (rect.width() - iconSizeInPixel) / 2
            val iconTop = rect.top + (rect.height() * 0.8f - iconSizeInPixel) / 2

            // Отрисовка иконки
            iconRes?.let {
                val drawable = ContextCompat.getDrawable(context, it)
                drawable?.let { icon ->
                    icon.setBounds(
                        iconLeft.toInt(),
                        iconTop.toInt(),
                        (iconLeft + iconSizeInPixel).toInt(),
                        (iconTop + iconSizeInPixel).toInt()
                    )
                    icon.draw(canvas)
                }
            }

            paint.color = ContextCompat.getColor(context, android.R.color.white)
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.DEFAULT
            paint.textAlign = Paint.Align.CENTER

            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)

            // Отрисовка текста под иконкой с одинаковым отступом от верхней и нижней границы кнопки
            val textTop =
                iconTop + iconSizeInPixel + (verticalPadding / 2) // Расстояние от верха кнопки до верха текста
            val textY = textTop + titleBounds.height()
            canvas.drawText(title, rect.centerX(), textY, paint)

            clickableRegion = rect
            this.pos = pos
        }

        fun handle(event: MotionEvent) {
            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {
                    clickListener.onClick(pos)
                }
            }
        }
    }
}

private fun List<SwipeHelper.UnderlayButton>.intrinsicWidth(): Float {
    if (isEmpty()) return 0.0f
    return map { it.intrinsicWidth }.reduce { acc, fl -> acc + fl }
}