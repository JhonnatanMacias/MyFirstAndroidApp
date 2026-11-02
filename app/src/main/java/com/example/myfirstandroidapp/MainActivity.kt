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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage

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
    var message by remember {
        mutableStateOf("Escribe un título o tema para buscar.")
    }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    if (selectedBook != null) {
        BookDetailScreen(
            book = selectedBook!!,
            modifier = modifier,
            onBack = { selectedBook = null }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                }
            ) {
                Text("Buscar")
            }

            if (message.isNotEmpty()) {
                Text(text = message)
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(books) { book ->
                    Text(
                        text = "${book.title}\n${book.author}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBook = book }
                            .padding(vertical = 8.dp)
                    )
                }
            }
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

@Composable
fun BookDetailScreen(
    book: Book,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var description by remember(book.workId) {
        mutableStateOf<String?>(null)
    }
    var isLoading by remember(book.workId) {
        mutableStateOf(true)
    }

    LaunchedEffect(book.workId) {
        loadBookDescription(book.workId) { result ->
            description = result
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        ) {
            Text("← Volver")
        }

        if (book.coverId != null) {
            AsyncImage(
                model = "https://covers.openlibrary.org/b/id/${book.coverId}-L.jpg",
                contentDescription = "Portada de ${book.title}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Portada no disponible")
            }
        }

        Text(
            text = book.title,
            style = MaterialTheme.typography.headlineSmall
        )
        Text(text = "Autor: ${book.author}")

        book.firstPublishYear?.let { year ->
            Text(text = "Primera publicación: $year")
        }

        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleLarge
        )

        when {
            isLoading -> Text("Cargando descripción…")
            description.isNullOrBlank() -> Text("Descripción no disponible.")
            else -> Text(description.orEmpty())
        }
    }
}

fun loadBookDescription(
    workId: String,
    onResult: (String?) -> Unit
) {
    Thread {
        val result = try {
            val url = URL("https://openlibrary.org/works/$workId.json")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                val response = connection.inputStream
                    .bufferedReader()
                    .use { it.readText() }

                when (val value = JSONObject(response).opt("description")) {
                    is String -> value
                    is JSONObject -> value.optString("value")
                        .takeIf { it.isNotBlank() }
                    else -> null
                }
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            null
        }

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            onResult(result)
        }
    }.start()
}

data class Book(
    val workId: String,
    val title: String,
    val author: String,
    val firstPublishYear: Int?,
    val coverId: Int?
)

fun searchBooks(query: String, onResult: (List<Book>) -> Unit) {
    Thread {
        val books = try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = URL(
                "https://openlibrary.org/search.json?q=$encodedQuery&fields=key,title,author_name,first_publish_year,cover_i&limit=20"
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
                        workId = item.optString("key").substringAfterLast('/'),
                        title = item.optString("title", "Sin título"),
                        author = item.optJSONArray("author_name")?.optString(0)
                            ?: "Autor desconocido",
                        firstPublishYear = item.optInt("first_publish_year").takeIf { it != 0 },
                        coverId = item.optInt("cover_i").takeIf { it != 0 }
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
