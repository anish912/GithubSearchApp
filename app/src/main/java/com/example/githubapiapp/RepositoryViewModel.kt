package com.example.githubapiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RepositoryViewModel : ViewModel() {

    private val _repositoryList = MutableLiveData<List<Repository>>()
    val repositoryList: LiveData<List<Repository>> get() = _repositoryList

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    NetworkUtils.getDataFromApi(query)
                }
                _repositoryList.value = result
            } catch (e: IOException) {
                _errorMessage.value = "Error fetching data"
            }
        }
    }
}