package com.example.githubapiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class RepoListAdapter(context: Context) : ArrayAdapter<Repository>(context, 0) {

    override fun clear() {
        super.clear() // Clear existing items
        notifyDataSetChanged() // Notify that data has changed
    }
    fun addAll(repositories: List<Repository>) {
        super.addAll(repositories) // Add new items to the adapter
        notifyDataSetChanged() // Notify that data has changed
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.repository_item, parent, false)

        val repoName = view.findViewById<TextView>(R.id.repoName)
        val repoLang = view.findViewById<TextView>(R.id.repoLang)
        val repoOwner = view.findViewById<TextView>(R.id.repoOwner)
        val repoStars = view.findViewById<TextView>(R.id.repoStars)

        val currentRepository = getItem(position)

        currentRepository?.let {
            repoName?.text = "Repo Name: ${it.name}"
            repoLang?.text = "Language: ${it.language ?: "Unknown Language"}"
            repoOwner?.text = "Owner: ${it.owner.login}"
            repoStars?.text = "Stars: ${it.stargazers_count}"
        }

        return view
    }

}