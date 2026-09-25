package co.edu.uniquindio.demoapp.domain.model

enum class ReportStatus {
    PENDING,  // Pendiente de revisión por un administrador
    VERIFIED, // Verificado: el reporte fue revisado y aprobado
    REJECTED, // Rechazado: el reporte no procede
    RESOLVED, // Resuelto: el problema reportado ya fue solucionado
    DELETED   // Eliminado: borrado lógico del reporte
}