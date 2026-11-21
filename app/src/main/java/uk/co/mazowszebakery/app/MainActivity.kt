package uk.co.mazowszebakery.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF171512)
private val Gold = Color(0xFFC6A15B)
private val Cream = Color(0xFFF7F3EA)

data class BakeryProduct(val name: String, val unit: String, val price: Double)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BakeryApp() }
    }
}

@Composable
fun BakeryApp() {
    val products = remember {
        listOf(
            BakeryProduct("Chleb pszenny", "szt.", 1.80),
            BakeryProduct("Chleb żytni", "szt.", 2.10),
            BakeryProduct("Bułka", "szt.", 0.45),
            BakeryProduct("Pączek", "szt.", 1.25),
            BakeryProduct("Drożdżówka", "szt.", 1.40),
            BakeryProduct("Rogal", "szt.", 1.10)
        )
    }
    val quantities = remember { mutableStateMapOf<String, Int>() }
    var tab by remember { mutableStateOf("Zamówienie") }
    val total = products.sumOf { it.price * (quantities[it.name] ?: 0) }

    MaterialTheme(colorScheme = lightColorScheme(primary = Ink, secondary = Gold, background = Cream)) {
        Scaffold(containerColor = Cream, bottomBar = {
            NavigationBar(containerColor = Ink) {
                listOf("Zamówienie", "Historia", "Konto").forEach { item ->
                    NavigationBarItem(
                        selected = tab == item,
                        onClick = { tab = item },
                        icon = {},
                        label = { Text(item, color = if (tab == item) Gold else Color.White) }
                    )
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
                Text("MAZOWSZE", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp)
                Text("BAKERY  •  ZAMÓWIENIA", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(22.dp))
                when (tab) {
                    "Zamówienie" -> {
                        Text("Dzisiejsze zamówienie", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Ink)
                        Text("Ceny brutto • GBP (£)", color = Color.Gray, fontSize = 13.sp)
                        Spacer(Modifier.height(14.dp))
                        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(products) { product ->
                                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                    Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(product.name, fontWeight = FontWeight.SemiBold, color = Ink)
                                            Text("£%.2f / %s".format(product.price, product.unit), color = Color.Gray, fontSize = 13.sp)
                                        }
                                        IconButton(onClick = { quantities[product.name] = maxOf(0, (quantities[product.name] ?: 0) - 1) }) { Text("−", fontSize = 22.sp) }
                                        Text((quantities[product.name] ?: 0).toString(), fontWeight = FontWeight.Bold)
                                        IconButton(onClick = { quantities[product.name] = (quantities[product.name] ?: 0) + 1 }) { Text("+", fontSize = 22.sp) }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Card(colors = CardDefaults.cardColors(containerColor = Ink), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                Text("Wartość zamówienia", color = Color.LightGray)
                                Text("£%.2f".format(total), color = Gold, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { }, enabled = quantities.values.any { it > 0 }, colors = ButtonDefaults.buttonColors(containerColor = Gold), modifier = Modifier.fillMaxWidth()) {
                                    Text("Zapisz zamówienie", color = Ink, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    "Historia" -> Text("Historia zamówień będzie dostępna po podłączeniu wspólnej bazy danych.", color = Ink)
                    else -> Text("Konto pracownika\n\nLogowanie i panel administratora zostaną podłączone w kolejnym etapie.", color = Ink)
                }
            }
        }
    }
}
