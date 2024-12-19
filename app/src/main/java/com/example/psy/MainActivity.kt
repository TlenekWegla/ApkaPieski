package com.example.psy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource

data class Dog(val name: String, var isFavorite: Boolean = false)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PsyTheme {
                DogsListScreen()
            }
        }
    }
}

@Composable
fun DogsListScreen() {
    var name by remember { mutableStateOf("") }
    var dogs by remember { mutableStateOf(listOf<Dog>()) }
    var filteredDogs by remember { mutableStateOf(dogs) }
    val dogNames = remember { mutableSetOf<String>() }
    var isDuplicate by remember { mutableStateOf(false) }

    fun filterDogs(query: String) {
        filteredDogs = if (query.isBlank()) {
            dogs
        } else {
            dogs.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // licz ulub
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite Icon",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Favorites: ${dogs.count { it.isFavorite }}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // szukanie i dodawania
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        isDuplicate = dogNames.contains(it)
                    },
                    label = { Text("Enter name") },
                    isError = isDuplicate,
                    modifier = Modifier.fillMaxWidth()
                )
                if (isDuplicate) {
                    Text(
                        text = "Dog with this name already exists.",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedButton(
                        onClick = {
                            filterDogs(name)
                        },
                        enabled = name.isNotBlank() && !isDuplicate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Search")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            if (name.isNotBlank() && !isDuplicate) {
                                val newDog = Dog(name)
                                dogs = dogs + newDog
                                dogNames.add(name)
                                name = ""
                                filteredDogs = dogs
                            }
                        },
                        enabled = name.isNotBlank() && !isDuplicate,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp)
            ) {
                val sortedDogs = filteredDogs.sortedWith(
                    compareByDescending<Dog> { it.isFavorite }.thenBy { it.name }
                )
                items(sortedDogs) { dog ->
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.dog_icon),
                                contentDescription = "Dog Icon",
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )
                            Text(
                                text = dog.name,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            IconButton(onClick = {
                                dog.isFavorite = !dog.isFavorite // Przełącz ulubiony
                                dogs = dogs.toList() // Wymuszenie odświeżenia stanu listy
                                filteredDogs = dogs // Aktualizacja widocznej listy psów
                            }) {
                                Icon(
                                    imageVector = if (dog.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (dog.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            IconButton(onClick = {
                                dogs = dogs - dog
                                dogNames.remove(dog.name)
                                filterDogs(name)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PsyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}