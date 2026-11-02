package com.example.myfirstandroidapp.ui.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myfirstandroidapp.data.BookRepository
import com.example.myfirstandroidapp.model.Book
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BooksViewModel(
    private val repository: BookRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BooksUiState())
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var descriptionJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun searchBooks() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null) }
            try {
                val books = repository.searchBooks(query)
                _uiState.update {
                    it.copy(
                        books = books,
                        isSearching = false,
                        searchError = if (books.isEmpty()) {
                            "No encontré resultados. Intenta otra búsqueda."
                        } else {
                            null
                        }
                    )
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchError = "No se pudo completar la búsqueda. Revisa tu conexión e inténtalo de nuevo."
                    )
                }
            }
        }
    }

    fun selectBook(book: Book) {
        descriptionJob?.cancel()
        _uiState.update {
            it.copy(
                selectedBook = book,
                description = null,
                isLoadingDescription = true
            )
        }
        descriptionJob = viewModelScope.launch {
            try {
                val description = repository.getBookDescription(book.workId)
                _uiState.update {
                    it.copy(description = description, isLoadingDescription = false)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _uiState.update { it.copy(description = null, isLoadingDescription = false) }
            }
        }
    }

    fun clearSelectedBook() {
        descriptionJob?.cancel()
        _uiState.update {
            it.copy(
                selectedBook = null,
                description = null,
                isLoadingDescription = false
            )
        }
    }
}

class BooksViewModelFactory(
    private val repository: BookRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            return BooksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
