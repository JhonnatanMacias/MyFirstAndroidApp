package com.example.myfirstandroidapp.ui.books

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.myfirstandroidapp.model.Book

@Composable
fun BookDetailScreen(
    book: Book,
    description: String?,
    isLoadingDescription: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
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

        Text(book.title, style = MaterialTheme.typography.headlineSmall)
        Text("Autor: ${book.author}")
        book.firstPublishYear?.let { Text("Primera publicación: $it") }
        Text("Descripción", style = MaterialTheme.typography.titleLarge)

        when {
            isLoadingDescription -> CircularProgressIndicator()
            description.isNullOrBlank() -> Text("Descripción no disponible.")
            else -> Text(description)
        }
    }
}
