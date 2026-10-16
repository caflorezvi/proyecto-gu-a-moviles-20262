package co.edu.uniquindio.demoapp.features.report.create

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import co.edu.uniquindio.demoapp.core.component.CategoryChip
import co.edu.uniquindio.demoapp.core.component.reportCategories
import co.edu.uniquindio.demoapp.core.util.RequestResult
import co.edu.uniquindio.demoapp.domain.model.Location
import java.util.Locale

@Composable
fun CreateReportScreen(
    onNavigateBack: () -> Unit,
    padding: PaddingValues = PaddingValues(),
    onReportCreated: (String, String) -> Unit = { _, _ -> onNavigateBack() }, // Se avisa que el reporte se creó (recibe su ID y el mensaje de confirmación)
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    // Se observa el estado completo de la pantalla
    val state by viewModel.uiState.collectAsState()

    // Cuando el reporte se crea, se avisa su ID para que la navegación abra su detalle
    LaunchedEffect(state.createResult) {
        val result = state.createResult
        val reportId = state.createdReportId
        if (result is RequestResult.Success && reportId != null) {
            onReportCreated(reportId, result.message)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            // Sube el contenido cuando aparece el teclado, sin volver a sumar el espacio de las barras
            .consumeWindowInsets(padding)
            .imePadding()
    ) {

        TopBar(onNavigateBack = onNavigateBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Section(title = "Categoría") {
                // FlowRow pasa los chips a la siguiente línea cuando no caben en el ancho
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    reportCategories.forEach { category ->
                        CategoryChip(
                            label = category.label,
                            icon = category.icon,
                            iconTint = category.contentColor,
                            selected = state.category == category.label,
                            onClick = { viewModel.onCategoryChange(category.label) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Título")
                },
                placeholder = {
                    Text(text = "¿Qué está pasando?")
                },
                isError = state.titleError != null,
                supportingText = state.titleError?.let { error ->
                    { Text(text = error) }
                },
                singleLine = true
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChange(it) },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(text = "Descripción")
                },
                placeholder = {
                    Text(text = "Describe lo que viste, cuándo y cualquier detalle útil.")
                },
                minLines = 4
            )

            Section(title = "Ubicación") {
                LocationItem(location = state.location)
            }

            Section(title = "Fotos") {
                PhotoPicker(
                    photoUrl = state.photoUrl,
                    onPhotoChange = { viewModel.onPhotoChange(it) }
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Tu reporte se publica como «Sin verificar» hasta que un moderador lo revise. Si es una emergencia, llama al 123.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Button(
            onClick = { viewModel.createReport() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
                .height(48.dp),
            // El botón se deshabilita si el formulario no es válido o si el envío está en curso
            enabled = state.isFormValid && state.createResult == null
        ) {
            if (state.createResult is RequestResult.Loading) {
                Text(text = "Enviando...")
            } else {
                Text(text = "Enviar reporte")
            }
        }
    }
}

@Composable
private fun TopBar(onNavigateBack: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = onNavigateBack
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Cerrar"
            )
        }
        Text(
            text = "Nuevo reporte",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

// Sección del formulario: un título y, debajo, su contenido
@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit
){
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )
        content()
    }
}

@Composable
private fun LocationItem(location: Location){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(start = 16.dp, top = 12.dp, end = 4.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.MyLocation,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Ubicación predeterminada",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = String.format(Locale.US, "%.4f, %.4f", location.latitude, location.longitude),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // La pantalla para elegir la ubicación en el mapa aún no existe
        TextButton(
            onClick = {},
            enabled = false
        ) {
            Text(text = "Cambiar")
        }
    }
}

@Composable
private fun PhotoPicker(
    photoUrl: String,
    onPhotoChange: (String) -> Unit
){
    // Abre el selector de fotos del sistema y devuelve la URI de la imagen elegida (o null si se canceló)
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoChange(uri.toString())
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (photoUrl.isNotBlank()) {
            AsyncImage(
                contentScale = ContentScale.Crop,
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl) // URI de la imagen elegida
                    .crossfade(true) // Efecto de desvanecimiento al cargar
                    .build(),
                contentDescription = "Foto del reporte",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        // Botón para elegir la foto (o cambiarla, si ya hay una)
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    photoPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AddAPhoto,
                contentDescription = if (photoUrl.isBlank()) "Agregar foto" else "Cambiar foto",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
