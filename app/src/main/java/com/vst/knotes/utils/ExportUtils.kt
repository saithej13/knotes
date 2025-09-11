package com.vst.knotes.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.vst.knotes.MVVM.Model.MilkCollectionWithFarmerModel
import com.vst.knotes.MVVM.viewModel.ReportsViewModel
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExportUtils {
    
    companion object {
        
        /**
         * Export milk collection data to Excel file
         */
        fun exportToExcel(
            context: Context,
            collections: List<MilkCollectionWithFarmerModel>,
            summary: ReportsViewModel.ReportSummary,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val workbook = XSSFWorkbook()
                val sheet = workbook.createSheet("Milk Collection Report")
                
                // Create header style
                val headerStyle = workbook.createCellStyle().apply {
                    fillForegroundColor = IndexedColors.LIGHT_BLUE.index
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                    alignment = HorizontalAlignment.CENTER
                    setBorderTop(BorderStyle.THIN)
                    setBorderBottom(BorderStyle.THIN)
                    setBorderLeft(BorderStyle.THIN)
                    setBorderRight(BorderStyle.THIN)
                }
                
                val headerFont = workbook.createFont().apply {
                    bold = true
                    fontHeightInPoints = 12
                }
                headerStyle.setFont(headerFont)
                
                // Create data style
                val dataStyle = workbook.createCellStyle().apply {
                    setBorderTop(BorderStyle.THIN)
                    setBorderBottom(BorderStyle.THIN)
                    setBorderLeft(BorderStyle.THIN)
                    setBorderRight(BorderStyle.THIN)
                }
                
                val summaryStyle = workbook.createCellStyle().apply {
                    fillForegroundColor = IndexedColors.LIGHT_GREEN.index
                    fillPattern = FillPatternType.SOLID_FOREGROUND
                    setBorderTop(BorderStyle.THIN)
                    setBorderBottom(BorderStyle.THIN)
                    setBorderLeft(BorderStyle.THIN)
                    setBorderRight(BorderStyle.THIN)
                }
                
                var rowIndex = 0
                
                // Add title
                val titleRow = sheet.createRow(rowIndex++)
                val titleCell = titleRow.createCell(0)
                titleCell.setCellValue(summary.title)
                titleCell.cellStyle = headerStyle
                
                // Add empty row
                rowIndex++
                
                // Add summary information
                val summaryData = listOf(
                    "Total Collections" to summary.totalCollections.toString(),
                    "Total Quantity" to "${String.format("%.2f", summary.totalQuantity)} L",
                    "Total Amount" to "₹${String.format("%.2f", summary.totalAmount)}",
                    "Unique Farmers" to summary.uniqueFarmers.toString(),
                    "Average Fat" to "${String.format("%.2f", summary.averageFat)}%",
                    "Average SNF" to "${String.format("%.2f", summary.averageSnf)}%",
                    "CM Collections" to summary.cmCollections.toString(),
                    "BM Collections" to summary.bmCollections.toString(),
                    "Date Range" to summary.dateRange
                )
                
                summaryData.forEach { (label, value) ->
                    val row = sheet.createRow(rowIndex++)
                    val labelCell = row.createCell(0)
                    labelCell.setCellValue(label)
                    labelCell.cellStyle = summaryStyle
                    
                    val valueCell = row.createCell(1)
                    valueCell.setCellValue(value)
                    valueCell.cellStyle = summaryStyle
                }
                
                // Add empty rows
                rowIndex += 2
                
                // Add collection data headers
                val headerRow = sheet.createRow(rowIndex++)
                val headers = listOf(
                    "Date", "Time", "Farmer Code", "Farmer Name", "Quantity (L)",
                    "Fat (%)", "SNF (%)", "Rate Type", "Rate/L", "Amount (₹)", "Remarks"
                )
                
                headers.forEachIndexed { index, header ->
                    val cell = headerRow.createCell(index)
                    cell.setCellValue(header)
                    cell.cellStyle = headerStyle
                }
                
                // Add collection data
                collections.forEach { collectionWithFarmer ->
                    val collection = collectionWithFarmer.collection
                    val farmer = collectionWithFarmer.farmer
                    
                    val row = sheet.createRow(rowIndex++)
                    val data = listOf(
                        collection.collectionDate,
                        collection.collectionTime,
                        farmer.farmerCode,
                        farmer.farmerName,
                        collection.quantity.toString(),
                        collection.fat.toString(),
                        collection.snf.toString(),
                        collection.rateType,
                        String.format("%.2f", collection.rate),
                        String.format("%.2f", collection.amount),
                        collection.remarks ?: "-"
                    )
                    
                    data.forEachIndexed { index, value ->
                        val cell = row.createCell(index)
                        cell.setCellValue(value)
                        cell.cellStyle = dataStyle
                    }
                }
                
                // Auto-size columns
                for (i in 0 until headers.size) {
                    sheet.autoSizeColumn(i)
                }
                
                // Save file
                val fileName = "MilkCollection_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.xlsx"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                
                FileOutputStream(file).use { outputStream ->
                    workbook.write(outputStream)
                }
                workbook.close()
                
                onSuccess(file)
                
            } catch (e: Exception) {
                onError("Error creating Excel file: ${e.message}")
            }
        }
        
        /**
         * Export milk collection data to PDF file
         */
        fun exportToPDF(
            context: Context,
            collections: List<MilkCollectionWithFarmerModel>,
            summary: ReportsViewModel.ReportSummary,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val fileName = "MilkCollection_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.pdf"
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                
                val writer = PdfWriter(file)
                val pdfDoc = PdfDocument(writer)
                val document = Document(pdfDoc)
                
                // Add title
                val title = Paragraph(summary.title)
                    .setFontSize(18f)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(title)
                
                document.add(Paragraph("\n"))
                
                // Add summary
                val summaryTitle = Paragraph("Report Summary")
                    .setFontSize(14f)
                    .setBold()
                document.add(summaryTitle)
                
                val summaryTable = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
                    .setWidth(UnitValue.createPercentValue(100f))
                
                val summaryData = listOf(
                    "Total Collections" to summary.totalCollections.toString(),
                    "Total Quantity" to "${String.format("%.2f", summary.totalQuantity)} L",
                    "Total Amount" to "₹${String.format("%.2f", summary.totalAmount)}",
                    "Unique Farmers" to summary.uniqueFarmers.toString(),
                    "Average Fat" to "${String.format("%.2f", summary.averageFat)}%",
                    "Average SNF" to "${String.format("%.2f", summary.averageSnf)}%",
                    "CM Collections" to summary.cmCollections.toString(),
                    "BM Collections" to summary.bmCollections.toString(),
                    "Date Range" to summary.dateRange
                )
                
                summaryData.forEach { (label, value) ->
                    summaryTable.addCell(Cell().add(Paragraph(label).setBold()))
                    summaryTable.addCell(Cell().add(Paragraph(value)))
                }
                
                document.add(summaryTable)
                document.add(Paragraph("\n"))
                
                // Add collections data
                if (collections.isNotEmpty()) {
                    val collectionsTitle = Paragraph("Collections Details")
                        .setFontSize(14f)
                        .setBold()
                    document.add(collectionsTitle)
                    
                    val collectionsTable = Table(UnitValue.createPercentArray(
                        floatArrayOf(0.8f, 0.7f, 0.8f, 1.2f, 0.8f, 0.6f, 0.6f, 0.6f, 0.8f, 0.9f, 1.0f)
                    )).setWidth(UnitValue.createPercentValue(100f))
                    
                    // Add headers
                    val headers = listOf(
                        "Date", "Time", "Code", "Farmer", "Qty(L)", "Fat%", "SNF%", "Type", "Rate", "Amount", "Remarks"
                    )
                    
                    headers.forEach { header ->
                        collectionsTable.addHeaderCell(
                            Cell().add(Paragraph(header).setBold().setFontSize(9f))
                        )
                    }
                    
                    // Add data rows
                    collections.forEach { collectionWithFarmer ->
                        val collection = collectionWithFarmer.collection
                        val farmer = collectionWithFarmer.farmer
                        
                        val data = listOf(
                            collection.collectionDate,
                            collection.collectionTime,
                            farmer.farmerCode,
                            farmer.farmerName,
                            collection.quantity.toString(),
                            collection.fat.toString(),
                            collection.snf.toString(),
                            collection.rateType,
                            String.format("%.2f", collection.rate),
                            String.format("%.2f", collection.amount),
                            collection.remarks ?: "-"
                        )
                        
                        data.forEach { value ->
                            collectionsTable.addCell(
                                Cell().add(Paragraph(value).setFontSize(8f))
                            )
                        }
                    }
                    
                    document.add(collectionsTable)
                }
                
                // Add footer
                document.add(Paragraph("\n"))
                val footer = Paragraph("Generated on: ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}")
                    .setFontSize(10f)
                    .setTextAlignment(TextAlignment.CENTER)
                document.add(footer)
                
                document.close()
                
                onSuccess(file)
                
            } catch (e: Exception) {
                onError("Error creating PDF file: ${e.message}")
            }
        }
        
        /**
         * Share file using system share intent
         */
        fun shareFile(context: Context, file: File, title: String = "Share Report") {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = when (file.extension.toLowerCase()) {
                        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        "pdf" -> "application/pdf"
                        else -> "*/*"
                    }
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(Intent.createChooser(shareIntent, title))
                
            } catch (e: Exception) {
                // Handle error
            }
        }
        
        /**
         * Open file with default application
         */
        fun openFile(context: Context, file: File) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                
                val openIntent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, when (file.extension.toLowerCase()) {
                        "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        "pdf" -> "application/pdf"
                        else -> "*/*"
                    })
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                context.startActivity(openIntent)
                
            } catch (e: Exception) {
                // Handle error - no app available to open file
            }
        }
        
        /**
         * Check if external storage is available for writing
         */
        fun isExternalStorageWritable(): Boolean {
            return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        }
        
        /**
         * Get file size in human readable format
         */
        fun getFileSize(file: File): String {
            val bytes = file.length()
            return when {
                bytes >= 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
                bytes >= 1024 -> String.format("%.1f KB", bytes / 1024.0)
                else -> "$bytes bytes"
            }
        }
    }
}