
package uk.co.mazowszebakery.app

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Ink = Color(0xFF171512)
private val Gold = Color(0xFFC6A15B)
private val Cream = Color(0xFFF7F3EA)

data class BakeryProduct(
    val name: String,
    val unit: String,
    val price: Double,
    val category: String
)

data class SavedOrder(
    val date: String,
    val lines: List<String>,
    val total: Double
)

private val catalogue = listOf(
    BakeryProduct("Chleb pszenny", "szt.", 1.80, "Chleby"),
    BakeryProduct("Chleb żytni", "szt.", 2.10, "Chleby"),
    BakeryProduct("Chleb wiejski", "szt.", 2.40, "Chleby"),
    BakeryProduct("Chleb graham", "szt.", 2.20, "Chleby"),
    BakeryProduct("Chleb razowy", "szt.", 2.30, "Chleby"),
    BakeryProduct("Chleb orkiszowy", "szt.", 2.80, "Chleby"),
    BakeryProduct("Chleb wieloziarnisty", "szt.", 2.60, "Chleby"),
    BakeryProduct("Chleb tostowy", "szt.", 1.90, "Chleby"),
    BakeryProduct("Chleb na zakwasie", "szt.", 3.00, "Chleby"),
    BakeryProduct("Chleb bezglutenowy", "szt.", 3.50, "Chleby"),

    BakeryProduct("Bułka kajzerka", "szt.", 0.45, "Bułki"),
    BakeryProduct("Bułka maślana", "szt.", 0.65, "Bułki"),
    BakeryProduct("Bułka grahamka", "szt.", 0.55, "Bułki"),
    BakeryProduct("Bułka wieloziarnista", "szt.", 0.60, "Bułki"),
    BakeryProduct("Bułka hot dog", "szt.", 0.70, "Bułki"),
    BakeryProduct("Bułka hamburgerowa", "szt.", 0.70, "Bułki"),
    BakeryProduct("Bagietka", "szt.", 1.25, "Bułki"),
    BakeryProduct("Ciabatta", "szt.", 1.10, "Bułki"),
    BakeryProduct("Paluch z sezamem", "szt.", 0.75, "Bułki"),
    BakeryProduct("Precel", "szt.", 0.80, "Bułki"),

    BakeryProduct("Pączek", "szt.", 1.10, "Słodkie wypieki"),
    BakeryProduct("Drożdżówka z serem", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("Drożdżówka z makiem", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("Drożdżówka z jabłkiem", "szt.", 1.45, "Słodkie wypieki"),
    BakeryProduct("Rogal maślany", "szt.", 1.40, "Słodkie wypieki"),
    BakeryProduct("Rogal z czekoladą", "szt.", 1.60, "Słodkie wypieki"),
    BakeryProduct("Croissant", "szt.", 1.30, "Słodkie wypieki"),
    BakeryProduct("Muffin czekoladowy", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("Muffin jagodowy", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("Cynamonka", "szt.", 1.40, "Słodkie wypieki"),

    BakeryProduct("Sernik", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("Makowiec", "kg", 11.00, "Ciasta i desery"),
    BakeryProduct("Szarlotka", "kg", 10.00, "Ciasta i desery"),
    BakeryProduct("Brownie", "kg", 13.00, "Ciasta i desery"),
    BakeryProduct("Biszkopt z kremem", "kg", 14.00, "Ciasta i desery"),
    BakeryProduct("Ciasto marchewkowe", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("Karpatka", "kg", 13.00, "Ciasta i desery"),
    BakeryProduct("Keks", "kg", 11.00, "Ciasta i desery"),
    BakeryProduct("Piernik", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("Beza", "szt.", 2.50, "Ciasta i desery"),

    BakeryProduct("Pizza bułka", "szt.", 1.40, "Pozostałe"),
    BakeryProduct("Pasztecik z mięsem", "szt.", 1.50, "Pozostałe"),
    BakeryProduct("Pasztecik z kapustą", "szt.", 1.40, "Pozostałe"),
    BakeryProduct("Drożdżówka z budyniem", "szt.", 1.50, "Pozostałe"),
    BakeryProduct("Chleb czosnkowy", "szt.", 1.80, "Pozostałe"),
    BakeryProduct("Focaccia", "szt.", 2.20, "Pozostałe"),
    BakeryProduct("Sucharki", "kg", 8.00, "Pozostałe"),
    BakeryProduct("Bułka tarta", "kg", 3.50, "Pozostałe"),
    BakeryProduct("Ciastka kruche", "kg", 9.00, "Pozostałe"),
    BakeryProduct("Rogal z makiem", "szt.", 1.50, "Pozostałe")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BakeryApp(this) }
    }
}

@Composable
fun BakeryApp(context: Context) {
    val prefs = remember {
        context.getSharedPreferences("mazowsze_bakery", Context.MODE_PRIVATE)
    }

    val quantities = remember {
        mutableStateMapOf<String, Int>().apply {
            val saved = runCatching {
                JSONObject(prefs.getString("quantities", "{}") ?: "{}")
            }.getOrNull()

            catalogue.forEach { product ->
                put(product.name, saved?.optInt(product.name, 0) ?: 0)
            }
        }
    }

    val history = remember {
        mutableStateListOf<SavedOrder>().apply {
            val saved = runCatching {
                JSONArray(prefs.getString("history", "[]") ?: "[]")
            }.getOrNull()

            if (saved != null) {
                for (i in 0 until saved.length()) {
                    val item = saved.optJSONObject(i) ?: continue
                    val linesJson = item.optJSONArray("lines") ?: JSONArray()
                    val lines = (0 until linesJson.length())
                        .map { linesJson.optString(it) }

                    add(
                        SavedOrder(
                            item.optString("date"),
                            lines,
                            item.optDouble("total")
                        )
                    )
                }
            }
        }
    }

    var tab by remember { mutableStateOf("Zamówienie") }
    var category by remember { mutableStateOf("Wszystkie") }
    var editingProduct by remember { mutableStateOf<BakeryProduct?>(null) }
var editedName by remember { mutableStateOf("") }

    val total = catalogue.sumOf {
        it.price * (quantities[it.name] ?: 0)
    }

    val categories = listOf(
        "Wszystkie",
        "Chleby",
        "Bułki",
        "Słodkie wypieki",
        "Ciasta i desery",
        "Pozostałe"
    )

    fun saveQuantities() {
        val json = JSONObject()
        quantities.forEach { (name, count) ->
            json.put(name, count)
        }
        prefs.edit().putString("quantities", json.toString()).apply()
    }

    fun saveHistory() {
        val json = JSONArray()
        history.forEach { order ->
            val lines = JSONArray()
            order.lines.forEach { lines.put(it) }

            json.put(
                JSONObject()
                    .put("date", order.date)
                    .put("lines", lines)
                    .put("total", order.total)
            )
        }
        prefs.edit().putString("history", json.toString()).apply()
    }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Ink,
            secondary = Gold,
            background = Cream
        )
    ) {
        Scaffold(
            containerColor = Cream,
            bottomBar = {
                NavigationBar(containerColor = Ink) {
                    listOf("Zamówienie", "Historia", "Konto").forEach { item ->
                        NavigationBarItem(
                            selected = tab == item,
                            onClick = { tab = item },
                            icon = {},
                            label = {
                                Text(
                                    item,
                                    color = if (tab == item) Gold else Color.White
                                )
                            }
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    "MAZOWSZE",
                    color = Ink,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
                Text(
                    "BAKERY  •  ZAMÓWIENIA",
                    color = Gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(18.dp))

                when (tab) {
                    "Zamówienie" -> {
                        Text(
                            "Dzisiejsze zamówienie",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Text(
                            "50 produktów • ceny przykładowe • GBP (£)",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(
                            Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            categories.forEach { item ->
                                FilterChip(
                                    selected = category == item,
                                    onClick = { category = item },
                                    label = { Text(item) }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        LazyColumn(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                catalogue.filter {
                                    category == "Wszystkie" || it.category == category
                                }
                            ) { product ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text(
                                                product.name,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Ink
                                            )
                                            Text(
                                                "£%.2f / %s".format(
                                                    Locale.UK,
                                                    product.price,
                                                    product.unit
                                                ),
                                                color = Color.Gray,
                                                fontSize = 12.sp
                                            )
                                        }
                                        TextButton(
    onClick = {
        editingProduct = product
        editedName = product.name
    }
) {
    Text("Edytuj", color = Gold)
                                        }

                                        IconButton(onClick = {
                                            quantities[product.name] = maxOf(
                                                0,
                                                (quantities[product.name] ?: 0) - 1
                                            )
                                            saveQuantities()
                                        }) {
                                            Text("−", fontSize = 21.sp)
                                        }

                                        Text(
                                            (quantities[product.name] ?: 0).toString(),
                                            fontWeight = FontWeight.Bold
                                        )

                                        IconButton(onClick = {
                                            quantities[product.name] =
                                                (quantities[product.name] ?: 0) + 1
                                            saveQuantities()
                                        }) {
                                            Text("+", fontSize = 21.sp)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Ink),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Text("Wartość zamówienia", color = Color.LightGray)
                                Text(
                                    "£%.2f".format(Locale.UK, total),
                                    color = Gold,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = {
                                        val lines = catalogue
                                            .filter { (quantities[it.name] ?: 0) > 0 }
                                            .map {
                                                "\${it.name}: \${quantities[it.name]} \${it.unit} — £%.2f"
                                                    .format(
                                                        Locale.UK,
                                                        it.price * (quantities[it.name] ?: 0)
                                                    )
                                            }

                                        if (lines.isNotEmpty()) {
                                            val date = SimpleDateFormat(
                                                "dd/MM/yyyy HH:mm",
                                                Locale.UK
                                            ).format(Date())

                                            history.add(
                                                0,
                                                SavedOrder(date, lines, total)
                                            )
                                            saveHistory()

                                            catalogue.forEach {
                                                quantities[it.name] = 0
                                            }
                                            saveQuantities()
                                        }
                                    },
                                    enabled = quantities.values.any { it > 0 },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Gold
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        "Zapisz zamówienie",
                                        color = Ink,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    "Historia" -> {
                        Text(
                            "Historia zamówień",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Spacer(Modifier.height(10.dp))

                        if (history.isEmpty()) {
                            Text(
                                "Nie zapisano jeszcze żadnych zamówień.",
                                color = Color.Gray
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(history) { order ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.White
                                        )
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                order.date,
                                                fontWeight = FontWeight.Bold,
                                                color = Ink
                                            )
                                            Spacer(Modifier.height(6.dp))
                                            order.lines.forEach {
                                                Text(it, fontSize = 13.sp, color = Ink)
                                            }
                                            Spacer(Modifier.height(6.dp))
                                            Text(
                                                "Razem: £%.2f".format(
                                                    Locale.UK,
                                                    order.total
                                                ),
                                                fontWeight = FontWeight.Bold,
                                                color = Ink
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        Text(
                            "Konto",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Ink
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Wersja demonstracyjna. Logowanie pracowników " +
                                "i panel administratora zostaną dodane po " +
                                "podłączeniu bezpiecznej bazy online.",
                            color = Ink
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Zapisane zamówienia i ilości są przechowywane " +
                                "tylko na tym telefonie.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
    if (editingProduct != null) {
    AlertDialog(
        onDismissRequest = {
            editingProduct = null
        },
        title = {
            Text("Edytuj nazwę produktu")
        },
        text = {
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Nazwa produktu") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val oldProduct = editingProduct!!
                    val newName = editedName.trim()

                    if (newName.isNotBlank() &&
                        products.none {
                            it.name.equals(newName, ignoreCase = true) &&
                            it.name != oldProduct.name
                        }
                    ) {
                        val index = products.indexOf(oldProduct)
                        if (index >= 0) {
                            products[index] = oldProduct.copy(name = newName)
                            val oldQuantity = quantities.remove(oldProduct.name) ?: 0
                            quantities[newName] = oldQuantity
                            saveQuantities()
                            saveProducts()
                        }
                    }
                    editingProduct = null
                }
            ) {
                Text("Zapisz")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                editingProduct = null
            }) {
                Text("Anuluj")
            }
        }
    )
    }


