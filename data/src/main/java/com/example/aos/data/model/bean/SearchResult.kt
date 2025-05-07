package com.example.aos.data.model.bean

import com.google.gson.annotations.SerializedName

class SearchResult<T> {
    @SerializedName("total_count")
    var totalCount: String? = null
    @SerializedName("incomplete_results")
    var incompleteResults: Boolean = false
    var items: ArrayList<T>? = null
}
