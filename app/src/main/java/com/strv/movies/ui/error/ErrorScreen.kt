package com.strv.movies.ui.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.strv.movies.R

@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    errorSource: ErrorSource
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .scrollable(rememberScrollState(), orientation = Orientation.Horizontal),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = stringResource(
                id = R.string.app_logo
            )
        )
        Text(
            text = when (errorSource) {
                ErrorSource.MOVIE_LIST -> stringResource(
                    id = R.string.moviesList_noMoviesSaved
                )
                ErrorSource.MOVIE_DETAIL -> stringResource(
                    id = R.string.movieDetail_movieDetailNotLoaded
                )
            },
            style = MaterialTheme.typography.h2,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary
        )
    }
}

enum class ErrorSource {
    MOVIE_LIST, MOVIE_DETAIL
}