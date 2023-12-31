package com.example.mtgdeckbuilder.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mtgdeckbuilder.CardListApplication
import com.example.mtgdeckbuilder.data.CardListRepository
import com.example.mtgdeckbuilder.network.CardList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface CardListUiState {
    data class Success(
        val cardList: CardList
    ): CardListUiState
    object Loading : CardListUiState
    object Error : CardListUiState
}
class SearchViewModel(private val cardListRepository: CardListRepository): ViewModel() {
    var cardListUiState : CardListUiState by mutableStateOf(CardListUiState.Loading)
        private set

    var userText by mutableStateOf("")
        private set

    var loadingImage by mutableStateOf(false)
        private set

    private lateinit var currentCardList: CardList
    private lateinit var nextPageCardList: CardList

    private val cardPages: MutableList<CardList> = mutableListOf()

    fun initializeCardList(input: String) {
        viewModelScope.launch {
            cardListUiState = try {
                delay(50)
                currentCardList = cardListRepository.getCardList(input)
                addToList(currentCardList)
                CardListUiState.Success(cardList = currentCardList)
            } catch (e: IOException) {
                CardListUiState.Error
            }
        }
    }

    fun nextPage() {
        viewModelScope.launch {
            cardListUiState = try {
                loadingImage = true
                delay(50)
                if(currentCardList.hasMore) {
                    nextPageCardList =
                        cardListRepository.nextPage(currentCardList.nextPage)
                    currentCardList = nextPageCardList
                    addToList(currentCardList)
                    loadingImage = false
                    CardListUiState.Success(cardList = nextPageCardList)
                } else {
                    CardListUiState.Success(cardList = currentCardList)
                }
            } catch (e: IOException) {
                CardListUiState.Error
            }
        }
    }

    fun previousPage() {
        viewModelScope.launch {
            cardListUiState = try {
                delay(50)
                if(cardPages.size > 1){
                    removeLatest()
                    currentCardList = cardPages[cardPages.size - 1]
                    CardListUiState.Success(cardList = currentCardList)
                } else {
                    CardListUiState.Success(cardList = currentCardList)
                }
            } catch (e: IOException) {
                CardListUiState.Error
            }
        }
    }

    fun currentListSize(): Int = cardPages.size


    private fun addToList(cardList: CardList){
        cardPages.add(cardList)
    }

    private fun removeLatest(){
        cardPages.removeAt(cardPages.size - 1)
    }

    fun updateUserText(input: String) {
        userText = input
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as CardListApplication)
                val cardListRepository = application.container.cardListRepository
                SearchViewModel(cardListRepository = cardListRepository)
            }
        }
    }
}