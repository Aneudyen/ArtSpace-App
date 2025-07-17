package com.example.artspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.artspace.data.DataSource
import com.example.artspace.model.Art
import com.example.artspace.ui.theme.ArtSpaceTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val navController = rememberNavController()
      ArtSpaceTheme {
        NavHost(
          navController = navController, startDestination = Screen.Home.route + "/{id}"
        ) {
          composable(
            Screen.Home.route + "/{id}", arguments = listOf(navArgument("id") {
              type = NavType.IntType
              defaultValue = 0
            })
          ) {
            HomePage(navController)
          }
          composable(
            Screen.Artist.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
          ) {
            ArtistPage(navController)
          }
        }
      }
    }
  }
}

@Composable
fun ArtistPage(navController: NavController) {
  val id = navController.currentBackStackEntry?.arguments?.getInt("id") ?: 0
  val art = DataSource.arts[id]

  // Use the ArtistProfile composable function to display artist details
  ArtistProfile(art)

  // Navigation button to go back to the previous screen
  Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
    Text(text = stringResource(id = R.string.back))
  }
}

@Composable
fun ArtistProfile(artist: Art) {
  Column(modifier = Modifier.padding(20.dp)) {
    Row(verticalAlignment = Alignment.Top) {
      Image(
        painter = painterResource(id = artist.artistImageId),
        contentDescription = stringResource(id = artist.artistId),
        modifier = Modifier
          .size(150.dp)
          .clip(CircleShape)
          .background(Color.White)
          .padding(4.dp)
          .border(3.dp, Color.Blue, CircleShape)
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )
      Spacer(Modifier.width(16.dp))
    }
    // Below the image
    Column(modifier = Modifier.padding(top = 8.dp)) {
      // Display artist name
      Text(
        text = stringResource(id = artist.artistId),
        modifier = Modifier.padding(vertical = 8.dp),
        style = TextStyle(fontWeight = FontWeight.Bold),
        textAlign = TextAlign.Center
      )


      Text(
        text = stringResource(id = artist.artistInfoId),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 4.dp)
      )


      Box(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(vertical = 8.dp)
      ) {
        Text(
          text = stringResource(id = artist.artistBioId),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}


@Composable
fun ArtWall(
  artistId: Int,
  artImageId: Int,
  artDescriptionId: Int,
  navController: NavController,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(align = Alignment.CenterVertically)
  ) {
    Box(
      modifier = Modifier
        .size(width = 350.dp, height = 350.dp)
        .align(Alignment.CenterHorizontally)
        .clickable {
          // Navigate to the artist page when the artwork is clicked
          navController.navigate(Screen.Artist.route + "/$artistId")
        }
        .clip(RectangleShape)
        .shadow(8.dp, RectangleShape)
        .border(6.dp, Color.White.copy(alpha = 0.8f), shape = RectangleShape)
    ) {
      Image(
        painter = painterResource(id = artImageId),
        contentDescription = null,
        modifier = Modifier
          .fillMaxSize()
          .border(3.dp, Color.Black, RectangleShape),
        contentScale = ContentScale.FillBounds
      )
    }
  }
}
@Composable
fun ArtDescriptor(artTitleId: Int, artistId: Int, artYearId: Int) {
  Column(
    modifier = Modifier
      .fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // HOME PAGE section B
    Text(
      text = stringResource(id = artTitleId),
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // Add artist name and year of artwork
    Text(
      text = stringResource(id = artistId) + "  " + stringResource(id = artYearId),
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun DisplayController(current: Int, move: (Int) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalArrangement = Arrangement.Center
  ) {
    Button(
      onClick = { move(current - 1) },
      enabled = current != 0,

      modifier = Modifier.weight(1f).padding(end = 8.dp)
    ) {
      Text(text = "Previous")
    }
    Button(
      onClick = { move(current + 1) },
      enabled = current != DataSource.arts.size - 1,

      modifier = Modifier.weight(1f).padding(start = 8.dp)
    ) {
      Text(text = "Next")
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(navController: NavController) {
  var current by remember {
    mutableIntStateOf(
      navController.currentBackStackEntry?.arguments?.getInt(
        "id"
      ) ?: 0
    )
  }
  val art = DataSource.arts[current]

  Scaffold(topBar = {
    CenterAlignedTopAppBar(
      title = { Text(text = stringResource(id = R.string.app_name)) },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary
      )
    )
  }) { innerPadding ->
    /**
     *The children without weight (a) are measured first. After that, the remaining space in the column
     * is spread among the children with weights (b), proportional to their weight. If you have 2
     * children with weight 1f, each will take half the remaining space.
     */
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(innerPadding)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f) // children with weight (b)
      ) {

        Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen.spacer_extra_large)))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center,
        ) {
          ArtWall(current, art.artworkImageId, art.descriptionId, navController)
        }
      }
      // (a) children without weight
      ArtDescriptor(art.titleId, art.artistId, art.yearId)
      DisplayController(current) {
        current = if (it !in 0..<DataSource.arts.size) 0 else it
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun ArtSpaceAppPreview() {
  ArtSpaceTheme {
    HomePage(rememberNavController())
  }
}