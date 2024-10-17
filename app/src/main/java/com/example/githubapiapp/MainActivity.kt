package com.example.githubapiapp

import androidx.loader.content.AsyncTaskLoader
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapiapp.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity(),LoaderManager.LoaderCallbacks<List<Repository>> {
    private lateinit var dataListView: ListView
    private lateinit var requestTag: EditText
    private lateinit var errorMessage: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var adapter: RepoListAdapter

    companion object {
        private const val GITHUB_SEARCH_LOADER = 1
        private const val GITHUB_QUERY_TAG = "query"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadingBar = findViewById(R.id.loadingBar)
        errorMessage = findViewById(R.id.errorMessage)
        requestTag = findViewById(R.id.requestTag)
        dataListView = findViewById(R.id.dataListView)
        dataListView.emptyView = errorMessage

        adapter = RepoListAdapter(applicationContext)
        dataListView.adapter = adapter

        savedInstanceState?.let {
            val queryUrl = it.getString(GITHUB_QUERY_TAG)
            requestTag.setText(queryUrl)
        }
        supportLoaderManager.initLoader(GITHUB_SEARCH_LOADER, null, this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.searchMenu -> {
                makeGithubSearchQuery()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GITHUB_QUERY_TAG, requestTag.text.toString().trim())
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Repository>> {
        return object : AsyncTaskLoader<List<Repository>>(this) {
            var mRepositoryList: List<Repository>? = null

            override fun onStartLoading() {
                if (args == null) return
                loadingBar.visibility = View.VISIBLE

                if (mRepositoryList != null) {
                    deliverResult(mRepositoryList)
                } else {
                    forceLoad()
                }
            }

            override fun loadInBackground(): List<Repository>? {
                val searchQueryUrlString = args?.getString(GITHUB_QUERY_TAG) ?: return null
                return try {
                    NetworkUtils.getDataFromApi(searchQueryUrlString)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }

            override fun deliverResult(data: List<Repository>?) {
                mRepositoryList = data
                super.deliverResult(data)
            }
        }
    }
    override fun onLoadFinished(loader: Loader<List<Repository>>, data: List<Repository>?) {
        loadingBar.visibility = View.INVISIBLE
        if (data == null) {
            showErrorMessage()
        } else {
            adapter.clear()
            adapter.addAll(data)
            showJsonDataView()
        }
    }
    override fun onLoaderReset(loader: Loader<List<Repository>>) {}

    private fun showJsonDataView() {
        errorMessage.visibility = View.INVISIBLE
        dataListView.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        dataListView.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
    }

    private fun makeGithubSearchQuery() {
        val githubQuery = requestTag.text.toString()

        val queryBundle = Bundle().apply {
            putString(GITHUB_QUERY_TAG, githubQuery)
        }

        val loaderManager = supportLoaderManager
        val githubSearchLoader = loaderManager.getLoader<List<Repository>>(GITHUB_SEARCH_LOADER)
        if (githubSearchLoader == null) {
            loaderManager.initLoader(GITHUB_SEARCH_LOADER, queryBundle, this)
        } else {
            loaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this)
        }
    }
}





