package com.example.githubapiapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class RepoViewModel : ViewModel() {

    private val _repositoryList = MutableLiveData<List<Repository>>()
    val repositoryList: LiveData<List<Repository>> get() = _repositoryList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun searchRepositories(query: String) {
        viewModelScope.launch {
            val result=NetworkUtils.getDataFromApi(query)
            if (result!=null && result.isNotEmpty()){
                _repositoryList.value=result
            }
            else{
                _error.value="No results found"
            }

        }
    }
}