package com.example.chuchoprojhect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chuchoprojhect.model.Perro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DogListViewModel : ViewModel() {
    private val _allDogs = MutableStateFlow<List<Perro>>(emptyList())
    private val _filteredDogs = MutableStateFlow<List<Perro>>(emptyList())
    val filteredDogs: StateFlow<List<Perro>> = _filteredDogs

    private var currentFilter: String? = null

    fun setDogs(dogs: List<Perro>) {
        _allDogs.value = dogs
        applyFilter()
    }

    fun setFilter(filter: String?) {
        currentFilter = filter
        applyFilter()
    }

    private fun applyFilter() {
        _filteredDogs.value = if (currentFilter.isNullOrEmpty()) {
            _allDogs.value
        } else {
            _allDogs.value.filter { dog ->
                dog.lugar.contains(currentFilter!!, ignoreCase = true) ||
                        dog.nombre.contains(currentFilter!!, ignoreCase = true)
            }
        }
    }
}