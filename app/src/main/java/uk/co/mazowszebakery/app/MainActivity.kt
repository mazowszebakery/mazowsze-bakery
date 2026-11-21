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
    val id: String,
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

private val defaultProducts = listOf(
    BakeryProduct("1", "Chleb pszenny", "szt.", 1.80, "Chleby"),
    BakeryProduct("2", "Chleb żytni", "szt.", 2.10, "Chleby"),
    BakeryProduct("3", "Chleb wiejski", "szt.", 2.40, "Chleby"),
    BakeryProduct("4", "Chleb graham", "szt.", 2.20, "Chleby"),
    BakeryProduct("5", "Chleb razowy", "szt.", 2.30, "Chleby"),
    BakeryProduct("6", "Chleb orkiszowy", "szt.", 2.80, "Chleby"),
    BakeryProduct("7", "Chleb wieloziarnisty", "szt.", 2.60, "Chleby"),
    BakeryProduct("8", "Chleb tostowy", "szt.", 1.90, "Chleby"),
    BakeryProduct("9", "Chleb na zakwasie", "szt.", 3.00, "Chleby"),
    BakeryProduct("10", "Chleb bezglutenowy", "szt.", 3.50, "Chleby"),
    BakeryProduct("11", "Bułka kajzerka", "szt.", 0.45, "Bułki"),
    BakeryProduct("12", "Bułka maślana", "szt.", 0.65, "Bułki"),
    BakeryProduct("13", "Bułka grahamka", "szt.", 0.55, "Bułki"),
    BakeryProduct("14", "Bułka wieloziarnista", "szt.", 0.60, "Bułki"),
    BakeryProduct("15", "Bułka hot dog", "szt.", 0.70, "Bułki"),
    BakeryProduct("16", "Bułka hamburgerowa", "szt.", 0.70, "Bułki"),
    BakeryProduct("17", "Bagietka", "szt.", 1.25, "Bułki"),
    BakeryProduct("18", "Ciabatta", "szt.", 1.10, "Bułki"),
    BakeryProduct("19", "Paluch z sezamem", "szt.", 0.75, "Bułki"),
    BakeryProduct("20", "Precel", "szt.", 0.80, "Bułki"),
    BakeryProduct("21", "Pączek", "szt.", 1.10, "Słodkie wypieki"),
    BakeryProduct("22", "Drożdżówka z serem", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("23", "Drożdżówka z makiem", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("24", "Drożdżówka z jabłkiem", "szt.", 1.45, "Słodkie wypieki"),
    BakeryProduct("25", "Rogal maślany", "szt.", 1.40, "Słodkie wypieki"),
    BakeryProduct("26", "Rogal z czekoladą", "szt.", 1.60, "Słodkie wypieki"),
    BakeryProduct("27", "Croissant", "szt.", 1.30, "Słodkie wypieki"),
    BakeryProduct("28", "Muffin czekoladowy", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("29", "Muffin jagodowy", "szt.", 1.50, "Słodkie wypieki"),
    BakeryProduct("30", "Cynamonka", "szt.", 1.40, "Słodkie wypieki"),
    BakeryProduct("31", "Sernik", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("32", "Makowiec", "kg", 11.00, "Ciasta i desery"),
    BakeryProduct("33", "Szarlotka", "kg", 10.00, "Ciasta i desery"),
    BakeryProduct("34", "Brownie", "kg", 13.00, "Ciasta i desery"),
    BakeryProduct("35", "Biszkopt z kremem", "kg", 14.00, "Ciasta i desery"),
    BakeryProduct("36", "Ciasto marchewkowe", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("37", "Karpatka", "kg", 13.00, "Ciasta i desery"),
    BakeryProduct("38", "Keks", "kg", 11.00, "Ciasta i desery"),
    BakeryProduct("39", "Piernik", "kg", 12.00, "Ciasta i desery"),
    BakeryProduct("40", "Beza", "szt.", 2.50, "Ciasta i desery"),
    BakeryProduct("41", "Pizza bułka", "szt.", 1.40, "Pozostałe"),
    BakeryProduct("42", "Pasztecik z mięsem", "szt.", 1.50, "Pozostałe"),
    BakeryProduct("43", "Pasztecik z kapustą", "szt.", 1.40, "Pozostałe"),
    BakeryProduct("44", "Drożdżówka z budyniem", "szt.", 1.50, "Pozostałe"),
    BakeryProduct("45", "Chleb czosnkowy", "szt.", 1.80, "Pozostałe"),
    BakeryProduct("46", "Focaccia", "szt.", 2.20, "Pozostałe"),
    BakeryProduct("47", "Sucharki", "kg", 8.00, "Pozostałe"),
    BakeryProduct("48", "Bułka tarta", "kg", 3.50, "Pozostałe"),
    BakeryProduct("49", "Ciastka kruche", "kg", 9.00, "Pozostałe"),
    BakeryProduct("50", "Rogal z makiem", "szt.", 1.50, "Pozostałe")
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

    val products = remember {
        mutableStateListOf<BakeryProduct>().apply {
            val saved = runCatching {
                JSONArray(prefs.getString("products", ""))
            }.getOrNull()

            if (saved != null && saved.length() > 0) {
                for (i in 0 until saved.length()) {
                    val p = saved.optJSONObject(i) ?: continue
                    add(
                        BakeryProduct(
                            p.optString("id"),
                            p.optString("name"),
                            p.optString("unit"),
                            p.optDouble("price"),
                            p.optString("category")
                        )
                    )
                }
            } else {
                addAll(defaultProducts)
            }
        }
    }

    val quantities = remember {
        mutableStateMapOf<String, Int>().apply {
            val saved = runCatching {
                JSONObject(prefs.getString("quantities", "{}") ?: "{}")
            }.getOrNull()

            products.forEach { product ->
                val oldValue = saved?.optInt(product.id, -1) ?: -1
                val oldNameValue = saved?.optInt(product.name, 0) ?: 0
                put(product.id, if (oldValue >= 0) oldValue else oldNameValue)
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
    var editingId by remember { mutableStateOf<String?>(null) }
    var editedName by remember { mutableStateOf("") }

    val categories = listOf(
        "Wszystkie",
        "Chleby",
        "Bułki",
        "Słodkie wypieki",
        "Ciasta i desery",
        "Pozostałe"
    )

    val total = products.sumOf {
        it.price * (quantities[it.id] ?: 0)
    }

    fun saveProducts() {
        val json = JSONArray()
        products.forEach { p ->
            json.put(
                JSONObject()
                    .put("id", p.id)
                    .put("name", p.name)
                    .put("unit", p.unit)
                    .put("price", p.price)
                    .put("category", p.category)
            )
        }
        prefs.edit().putString("products", json.toString()).apply()
    }

    fun saveQuantities() {
        val json = JSONObject()
        quantities.forEach { (id, count) ->
            json.put(id, count)
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
                    "BAKERY • ZAMÓWIENIA",
                    color = Gold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
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
                            "50 produktów • GBP (£)",
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
                            items(products.filter {
                                category == "Wszystkie" || it.category == category
                            }, key = { it.id }) { product ->

                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
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
                                            TextButton(
                                                onClick = {
                                                    editingId = product.id
                                                    editedName = product.name
                                                },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text("Edytuj nazwę", color = Gold)
                                            }
                                        }

                                        IconButton(onClick = {
                                            quantities[product.id] = maxOf(
                                                0,
                                                (quantities[product.id] ?: 0) - 1
                                            )
                                            saveQuantities()
                                        }) {
                                            Text("−", fontSize = 21.sp)
                                        }

                                        Text(
                                            (quantities[product.id] ?: 0).toString(),
                                            fontWeight = FontWeight.Bold
                                        )

                                        IconButton(onClick = {
                                            quantities[product.id] =
                                                (quantities[product.id] ?: 0) + 1
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
                                        val lines = products
                                            .filter { (quantities[it.id] ?: 0) > 0 }
                                            .map {
                                                "${it.name}: ${quantities[it.id]} ${it.unit} — £%.2f"
                                                    .format(
                                                        Locale.UK,
                                                        it.price * (quantities[it.id] ?: 0)
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

                                            products.forEach {
                                                quantities[it.id] = 0
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
                                "Nie zapisano jeszcze zamówień.",
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

                                            order.lines.forEach { line ->
                                                Text(
                                                    line,
                                                    fontSize = 13.sp,
                                                    color = Ink
                                                )
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
                            "Logowanie pracowników i panel administratora wymagają podłączenia bezpiecznej bazy online.",
                            color = Ink
                        )
                        Text(
                            "Katalog i historia są obecnie zapisane tylko na tym telefonie.",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        if (editingId != null) {
            AlertDialog(
                onDismissRequest = { editingId = null },
                title = { Text("Edytuj nazwę produktu") },
                text = {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Nazwa produktu") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        val index = products.indexOfFirst {
                            it.id == editingId
                        }
                        val newName = editedName.trim()
                        val duplicate = products.any {
                            it.id != editingId &&
                                it.name.equals(newName, ignoreCase = true)
                        }

                        if (index >= 0 && newName.isNotBlank() && !duplicate) {
                            products[index] = products[index].copy(name = newName)
                            saveProducts()
                        }
                        editingId = null
                    }) {
                        Text("Zapisz")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingId = null }) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}
