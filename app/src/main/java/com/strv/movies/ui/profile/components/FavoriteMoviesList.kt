package com.strv.movies.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.strv.movies.R
import com.strv.movies.model.Movie

@Composable
fun FavoriteMovies(
    movies: List<Movie>,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        items(movies) { movie ->
            FavoriteMovieItem(
                movie = movie,
                modifier = Modifier.fillMaxWidth(0.4f)
            )
        }
    }
}

@Composable
fun FavoriteMovieItem(
    modifier: Modifier = Modifier,
    movie: Movie
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colors.secondary, shape = MaterialTheme.shapes.large)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card {
            AsyncImage(
                contentScale = ContentScale.FillHeight,
                model = "https://image.tmdb.org/t/p/h632${movie.posterPath}",
                contentDescription = stringResource(id = R.string.movie_image)
            )
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(4.dp)
                    .alpha(0.9f)
                    .background(
                        color = MaterialTheme.colors.primaryVariant,
                        shape = CircleShape
                    )
            ) {
                Text(
                    text = movie.popularity.toString(),
                    modifier = Modifier.padding(4.dp),
                    maxLines = 1,
                    fontWeight = FontWeight.Light,
                    fontSize = 8.sp,
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
        Text(
            text = movie.title,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}