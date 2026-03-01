package dev.parcelview.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val dummyParcels = listOf("TRK-001", "TRK-002", "TRK-003")

@Composable
fun ParcelsListScreen(onParcelClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(dummyParcels) { trackingId ->
            ListItem(
                headlineContent = { Text(trackingId) },
                supportingContent = { Text("Tap to view details") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onParcelClick(trackingId) }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}
