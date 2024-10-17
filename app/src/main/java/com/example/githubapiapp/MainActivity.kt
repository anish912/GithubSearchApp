package com.example.githubapiapp

import androidx.loader.content.AsyncTaskLoader
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
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

class MainActivity : AppCompatActivity() {
    private lateinit var dataListView: ListView
    private lateinit var requestTag: EditText
    private lateinit var errorMessage: TextView
    private lateinit var loadingBar: ProgressBar
    private lateinit var adapter: RepoListAdapter
    private lateinit var searchButton: Button

    private lateinit var  repoViewModel:RepoViewModel

//    companion object {
//        private const val GITHUB_SEARCH_LOADER = 1
//        private const val GITHUB_QUERY_TAG = "query"
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repoViewModel=ViewModelProvider(this).get(RepoViewModel::class.java)





        loadingBar = findViewById(R.id.loadingBar)
        errorMessage = findViewById(R.id.errorMessage)
        requestTag = findViewById(R.id.requestTag)
        dataListView = findViewById(R.id.dataListView)
        dataListView.emptyView = errorMessage
        searchButton = findViewById(R.id.searchButton)

        adapter = RepoListAdapter(applicationContext)
        dataListView.adapter = adapter

        repoViewModel.repositoryList.observe(this){repositories->

            loadingBar.visibility=View.INVISIBLE
            if (repositories.isNullOrEmpty()){
                showErrorMessage()
            }else{
                adapter.clear()
                adapter.addAll(repositories)
                showJsonDataView()
            }

        }
        repoViewModel.error.observe(this) { errorMessageText ->
            Log.d("MainActivity", "Error received: $errorMessageText")
            loadingBar.visibility = View.GONE
            errorMessage.text = errorMessageText
            showErrorMessage()
        }
        searchButton.setOnClickListener {
            loadingBar.visibility = View.VISIBLE
            makeGithubSearchQuery()
            dataListView.visibility = View.INVISIBLE

        }
    }





    private fun makeGithubSearchQuery() {
        val githubQuery = requestTag.text.toString().trim()

        loadingBar.visibility = View.VISIBLE
        repoViewModel.searchRepositories(githubQuery)
    }

    private fun showJsonDataView() {
        Log.d("MainActivity", "Showing data view")
        errorMessage.visibility = View.GONE
        dataListView.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        Log.d("MainActivity", "Showing error message")
        dataListView.visibility = View.GONE
        errorMessage.visibility = View.VISIBLE
    }
}







