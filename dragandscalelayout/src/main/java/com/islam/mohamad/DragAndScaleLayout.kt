package com.islam.mohamad

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.islam.mohamad.dragandscalelayout.R


@SuppressLint("ClickableViewAccessibility")
class DragAndScaleLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var dX = 0f
    var dY = 0f
    var lastAction = 0

    private val scaleIV by lazy {
        ImageView(context).apply {
            setImageResource(R.drawable.ic_scale)
            val lParams = LayoutParams(LayoutParams.MATCH_PARENT, 80)
            lParams.gravity = Gravity.BOTTOM
            layoutParams = lParams
        }
    }

    private val dragIV by lazy {
        ImageView(context).apply {
            setImageResource(R.drawable.ic_drag)
            val lParams = LayoutParams(LayoutParams.WRAP_CONTENT, 80)
            lParams.gravity = Gravity.TOP
            lParams.gravity = Gravity.START
            layoutParams = lParams
        }
    }

    private val dragOnTouchListener = OnTouchListener { view, event ->
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                dX = x - event.rawX
                dY = y - event.rawY
                lastAction = MotionEvent.ACTION_DOWN
            }
            MotionEvent.ACTION_MOVE -> {
                startMoving(event, this)
                lastAction = MotionEvent.ACTION_MOVE
            }
            MotionEvent.ACTION_UP -> {
                if (lastAction == MotionEvent.ACTION_DOWN)
                    Toast.makeText(
                        view.context,
                        "Clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
            }

        }
        true
    }

    private val scaleOnTouchListener = OnTouchListener { v, event ->
        if (event.actionMasked == MotionEvent.ACTION_MOVE) {
            val params =
                layoutParams
            if (params.height + event.y.toInt() >= (300)) {
                params.height += event.y.toInt()
                layoutParams = params
            }
        }
        true
    }

    init {
        dragIV.setOnTouchListener(dragOnTouchListener)
        scaleIV.setOnTouchListener(scaleOnTouchListener)
        addView(dragIV)
        addView(scaleIV)
        dragIV.z = 1F
        scaleIV.z = 1F
    }

    private fun startMoving(event: MotionEvent, view: View) {
        val parentLeft = getRelativeLeft(this)
        val parentTop = getRelativeTop(this)
        var newX = event.rawX + dX
        var newY = event.rawY + dY
        if (newX + parentLeft < parentLeft) {
            newX = 0f
        }
        if (newX + parentLeft + width > parentLeft + (parent as View).width) {
            newX = ((parent as View).width - width).toFloat()
        }
        if (newY + parentTop < parentTop) {
            newY = 0f
        }
        if (newY + parentTop + height > parentTop + (parent as View).height) {
            newY = ((parent as View).height - height).toFloat()
        }
        if (!(newX + parentLeft < parentLeft && newX + parentLeft + width > parentLeft + (parent as View).width
                    && newY + parentTop < parentTop && newY + parentTop + height > parentTop + (parent as View).height)
        ) {
            view.x = newX
            view.y = newY
        }
    }

    private fun getRelativeLeft(myView: View): Int {
        return if (myView.parent === myView.rootView) myView.left else myView.left + getRelativeLeft(
            myView.parent as View
        )
    }

    private fun getRelativeTop(myView: View): Int {
        return if (myView.parent === myView.rootView) myView.top else myView.top + getRelativeTop(
            myView.parent as View
        )
    }

}