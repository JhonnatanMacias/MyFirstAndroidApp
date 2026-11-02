package com.example.myfirstandroidapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.myfirstandroidapp.data.OpenLibraryBookRepository
import com.example.myfirstandroidapp.ui.books.BooksRoute
import com.example.myfirstandroidapp.ui.books.BooksViewModel
import com.example.myfirstandroidapp.ui.books.BooksViewModelFactory
import com.example.myfirstandroidapp.ui.theme.MyFirstAndroidAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(
            this,
            BooksViewModelFactory(OpenLibraryBookRepository())
        )[BooksViewModel::class.java]

        setContent {
            MyFirstAndroidAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BooksRoute(modifier = Modifier.padding(innerPadding), viewModel = viewModel)
                }
            }
        }
    }
}
