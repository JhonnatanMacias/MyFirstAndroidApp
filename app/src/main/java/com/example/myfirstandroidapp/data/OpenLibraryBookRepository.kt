package com.example.myfirstandroidapp.data

import com.example.myfirstandroidapp.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class OpenLibraryBookRepository : BookRepository {
    override suspend fun searchBooks(query: String): List<Book> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = URL(
            "https://openlibrary.org/search.json?q=$encodedQuery" +
                "&fields=key,title,author_name,first_publish_year,cover_i&limit=20"
        )
        val response = getJson(url)
        val docs = response.getJSONArray("docs")

        (0 until docs.length()).mapNotNull { index ->
            val item = docs.getJSONObject(index)
            val workId = item.optString("key").substringAfterLast('/')
            if (workId.isBlank()) return@mapNotNull null

            Book(
                workId = workId,
                title = item.optString("title").ifBlank { "Sin título" },
                author = item.optJSONArray("author_name")
                    ?.optString(0)
                    ?.takeIf { it.isNotBlank() }
                    ?: "Autor desconocido",
                firstPublishYear = item.optInt("first_publish_year").takeIf { it != 0 },
                coverId = item.optInt("cover_i").takeIf { it != 0 }
            )
        }
    }

    override suspend fun getBookDescription(workId: String): String? = withContext(Dispatchers.IO) {
        val response = getJson(URL("https://openlibrary.org/works/$workId.json"))
        when (val description = response.opt("description")) {
            is String -> description.takeIf { it.isNotBlank() }
            is JSONObject -> description.optString("value").takeIf { it.isNotBlank() }
            null -> null
            else -> null
        }
    }

    private fun getJson(url: URL): JSONObject {
        val connection = url.openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.inputStream.bufferedReader().use { reader ->
                JSONObject(reader.readText())
            }
        } finally {
            connection.disconnect()
        }
    }
}
