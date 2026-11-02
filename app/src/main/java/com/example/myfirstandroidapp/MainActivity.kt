package com.example.myfirstandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstandroidapp.ui.theme.MyFirstAndroidAppTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.material3.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstAndroidAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BooksScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BooksScreen(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    var books by remember { mutableStateOf<List<Book>>(emptyList()) }
    var message by remember { mutableStateOf("Escribe un título o tema para buscar.") }

    Column(modifier = modifier.padding(24.dp)) {
        Text(
            text = "Libros",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Título o tema") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (query.isNotBlank()) {
                    message = "Buscando…"
                    searchBooks(query) { results ->
                        books = results
                        message = if (results.isEmpty()) {
                            "No encontré resultados. Intenta otra búsqueda."
                        } else {
                            ""
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Buscar")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        books.forEach { book ->
            Text(
                text = "${book.title} — ${book.author}",
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BooksScreenPreview() {
    MyFirstAndroidAppTheme {
        BooksScreen()
    }
}

data class Book(
    val title: String,
    val author: String,
    val coverId: Int?
)

fun searchBooks(query: String, onResult: (List<Book>) -> Unit) {
    Thread {
        val books = try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = URL(
                "https://openlibrary.org/search.json?q=$encodedQuery&limit=20"
            )
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val docs = JSONObject(response).getJSONArray("docs")

                (0 until docs.length()).map { index ->
                    val item = docs.getJSONObject(index)
                    val authors = item.optJSONArray("author_name")

                    Book(
                        title = item.optString("title", "Sin título"),
                        author = authors?.optString(0) ?: "Autor desconocido",
                        coverId = if (item.has("cover_i")) {
                            item.optInt("cover_i")
                        } else {
                            null
                        }
                    )
                }
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            emptyList()
        }

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            onResult(books)
        }
    }.start()
}