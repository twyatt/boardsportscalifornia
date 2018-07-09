package com.boardsportscalifornia.app.features.windgraph

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.boardsportscalifornia.app.R
import com.boardsportscalifornia.app.app
import com.boardsportscalifornia.app.data.ViewModelFactory
import com.boardsportscalifornia.app.data.repository.models.WindGraph
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import kotlinx.android.synthetic.main.wind_graph.last_modified
import kotlinx.android.synthetic.main.wind_graph.windgraph

class WindGraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wind_graph)

        val factory = ViewModelFactory(app.repository)
        val viewModel = ViewModelProviders.of(this, factory).get(WindGraphViewModel::class.java)

        viewModel.latestWindGraph.observe(this, Observer { result ->
            if (result != null) {
                result.success { it.show() }
                result.failure {
                    it.cachedWindGraph?.show()
                    // TODO Display error.
                }
            } else {
                windgraph.setImageDrawable(null)
                last_modified.text = null
            }
        })
    }

    private fun WindGraph.show() {
        windgraph.setImageDrawable(bytes.asBitmapDrawable())
        last_modified.text = lastModified.toString()
    }

    private fun ByteArray.asBitmapDrawable(): BitmapDrawable =
        BitmapDrawable(resources, BitmapFactory.decodeByteArray(this, 0, size))
}

