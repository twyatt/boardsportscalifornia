package com.boardsportscalifornia.app.features.windgraph

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.animation.doOnStart
import com.boardsportscalifornia.app.R
import kotlinx.android.synthetic.main.wind_graph.last_modified
import kotlinx.android.synthetic.main.wind_graph.windgraph

class WindGraphActivity : AppCompatActivity() {

    private lateinit var viewModel: WindGraphViewModel
    private var animator: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wind_graph)

        viewModel = ViewModelProviders.of(this).get(WindGraphViewModel::class.java)
        viewModel.windGraph.observe(this, Observer {
            if (it != null) {
                animateTo(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.wind_graph, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reload -> viewModel.refreshWindGraph()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun animateTo(viewModel: WindGraphDisplay) {
        animator?.cancel()
        animator = AnimatorSet().apply {
            playTogether(
                windgraph.createAlphaAnimator(viewModel.alpha).apply {
                    doOnStart { windgraph.setImageDrawable(viewModel.drawable) }
                },
                last_modified.createTextColorAnimator(viewModel.color).apply {
                    doOnStart { last_modified.text = viewModel.text }
                }
            )
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
            start()
        }
    }
}

private fun TextView.createTextColorAnimator(@ColorInt targetColor: Int) = ValueAnimator
    .ofInt(currentTextColor, targetColor)
    .apply {
        setEvaluator(ArgbEvaluator())
        addUpdateListener { setTextColor(it.animatedValue as Int) }
    }

private fun View.createAlphaAnimator(targetAlpha: Float) = ValueAnimator
    .ofFloat(alpha, targetAlpha)
    .apply {
        addUpdateListener { alpha = it.animatedValue as Float }
    }

