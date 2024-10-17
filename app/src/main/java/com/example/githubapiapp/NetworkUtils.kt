package com.example.githubapiapp



import android.net.Uri
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

object NetworkUtils {

    private const val GITHUB_BASE_URL = "https://api.github.com/search/repositories"
    private const val PARAM_QUERY = "q"
    private const val PARAM_SORT = "sort"
    private const val SORT_BY = "stars"

    /**
     * Builds the URL used to query GitHub.
     *
     * @param gitHubSearchQuery The keyword that will be queried for.
     * @return The URL to use to query the GitHub server.
     */
    private fun buildUrl(gitHubSearchQuery: String): URL? {
        val builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
            .appendQueryParameter(PARAM_QUERY, gitHubSearchQuery)
            .appendQueryParameter(PARAM_SORT, SORT_BY)
            .build()

        return try {
            URL(builtUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            null
        }
    }


    @Throws(IOException::class)
    private fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection = url.openConnection() as HttpURLConnection
        return try {
            val inputStream: InputStream = urlConnection.inputStream
            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")

            if (scanner.hasNext()) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }
    }


    private fun jsonFormatter(jsonResponse: String?): List<Repository> {
        val repositoryList = mutableListOf<Repository>()
        try {
            val json = JSONObject(jsonResponse ?: "")
            val items: JSONArray = json.getJSONArray("items")
            var dataLen = 50
            if (items.length() < dataLen) {
                dataLen = items.length()
            }
            for (i in 0 until dataLen) {
                val currentRepo = items.getJSONObject(i)
                val repoName = currentRepo.getString("name")
                val repoOwner = currentRepo.getJSONObject("owner").getString("login")
                val repoLang = currentRepo.getString("language")
                val repoStart = currentRepo.getString("stargazers_count")

                Log.v("Data", "Number $i")
                val repository = Repository(repoName, repoOwner, repoLang, repoStart)
                repositoryList.add(repository)
            }
        } catch (ex: JSONException) {
            Log.v("Network", "Can't Read Json")
        }
        return repositoryList
    }


    @Throws(IOException::class)
    fun getDataFromApi(query: String): List<Repository> {
        val apiURL = buildUrl(query)
        val jsonResponse = apiURL?.let { getResponseFromHttpUrl(it) }
        return jsonFormatter(jsonResponse)
    }
}
