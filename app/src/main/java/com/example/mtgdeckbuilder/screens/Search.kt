package com.example.mtgdeckbuilder.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mtgdeckbuilder.R
import com.example.mtgdeckbuilder.network.Card
import com.example.mtgdeckbuilder.network.CardImage
import com.example.mtgdeckbuilder.network.CardList

@Composable
fun searchScreen(
    searchViewModel: SearchViewModel,
    onKeyboardSearch: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        searchBar(
            searchViewModel = searchViewModel,
            onKeyboardSearch = onKeyboardSearch,
        )
        Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.medium_padding)))
        when (val searchUiState = searchViewModel.cardListUiState) {
            is CardListUiState.Success ->
                cardList(
                    initializePage = { searchViewModel.nextPage() },
                    loadPreviousPage = { searchViewModel.previousPage() },
                    cardList = searchUiState.cardList,
                    onClick = {}
                )
            is CardListUiState.Error -> null
            is CardListUiState.Loading -> null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun searchBar(
    searchViewModel: SearchViewModel,
    onKeyboardSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ){
        //Spacer(Modifier.weight(.25F))
        Text(text = stringResource(R.string.search_for_cards))
        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)))
        TextField(
            value = searchViewModel.userText,
            singleLine = true,
            onValueChange = { input -> searchViewModel.updateUserText(input)},
            label = { Text(stringResource(R.string.search))},
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onKeyboardSearch() }
            )
        )
        //Spacer(Modifier.weight(.75F))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun cardList(
    initializePage: () -> Unit,
    loadPreviousPage: () -> Unit,
    cardList: CardList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberLazyStaggeredGridState()


    Column {
        Row {
            loadPrevious{
                loadPreviousPage()
            }
            Spacer(modifier = Modifier.weight(1F))
            load {
                initializePage()
            }
        }
        LazyHorizontalStaggeredGrid(
            rows = StaggeredGridCells.Adaptive(minSize = 240.dp),
            state = state,
            modifier = modifier.fillMaxSize()
        ) {
            itemsIndexed(cardList.data) { index, entry ->
                entry.imageUris?.let{ cardEntry(it) }
            }
        }

    }
}

@Composable
fun load(initializePage: () -> Unit) {
    Button(onClick = { initializePage() }) {
        Icon(
            imageVector = Icons.Rounded.ArrowForward,
            contentDescription = stringResource(R.string.next_page)
        )
    }
}
@Composable
fun loadPrevious(loadPreviousPage: () -> Unit) {
    Button(onClick = { loadPreviousPage() }) {
        Icon(
            imageVector = Icons.Rounded.ArrowBack,
            contentDescription = stringResource(R.string.load_previous)
        )
    }

}
@Composable
fun textComp(page: String?){
    Text(text = page?: "")
}

@Composable
fun cardEntry(
    cardImage: CardImage
) {
    Card() {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(cardImage.large)
                .build(),
            contentDescription = null,
        )
    }
}