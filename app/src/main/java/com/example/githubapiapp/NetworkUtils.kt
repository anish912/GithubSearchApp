package com.example.githubapiapp



import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
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

    private const val TAG="NetworkUtils"

    suspend fun getDataFromApi(query: String): List<Repository>? {
        return withContext(Dispatchers.IO){
            try {


                val response=RetrofitInstance.api.searchRepositories(query)
                Log.d(TAG, "Response: ${response.items}")
                response.items
            }
            catch (e:Exception){
                Log.e(TAG,"Error fetching data",e)
                null
            }
        }
    }
}
