package com.swipecare.payments.transactionReceipts

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants
import com.swipecare.payments.Constants.AEPS_MINI_STATEMENT_RESPONSE
import com.swipecare.payments.Constants.PERMISSION_REQUEST_CODE
import com.swipecare.payments.Constants.TRANSACTIONS
import com.swipecare.payments.R
import com.swipecare.payments.SharePrfeManager
import com.swipecare.payments.adapters.TransactionsListAdapter
import com.swipecare.payments.databinding.ActivityMinistatementReceiptBinding
import com.swipecare.payments.model.AEPSMiniStatementResponse
import com.swipecare.payments.model.Transaction
import java.io.*
import java.util.*

const val CREATE_FILE = 32

class MiniStatementReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMinistatementReceiptBinding
    private lateinit var miniStatementResponse: AEPSMiniStatementResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ministatement_receipt)
        setupActionbar()

        miniStatementResponse =
        intent.getParcelableExtra(AEPS_MINI_STATEMENT_RESPONSE)!!

        if (miniStatementResponse != null && miniStatementResponse.status != "false") {
            updateMiniStatementSuccessReceipt(miniStatementResponse)
        } else {
            updateMiniStatementFailureReceipt(miniStatementResponse)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateMiniStatementSuccessReceipt(miniStatementResponse: AEPSMiniStatementResponse) {
        binding.aadhar.text = miniStatementResponse.aadhar
        binding.balance.text = miniStatementResponse.balance
        binding.rrn.text = miniStatementResponse.rrn
        binding.bank.text = miniStatementResponse.bank
        binding.id.text = miniStatementResponse.id.toString()
        binding.transactionType.text = miniStatementResponse.transactionType
        binding.message.text = miniStatementResponse.message

        val date = Date()
        val format: CharSequence =
            android.text.format.DateFormat.format("dd-MM-yyyy hh:mm", date)
        binding.receiptTime.text = format.toString()

        val adapter = TransactionsListAdapter()
        binding.transactionsList.adapter = adapter
        adapter.submitList(miniStatementResponse.data)

        binding.toolbar4.background = getDrawable(R.drawable.cash_success)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Constants.colorGreenLight)))
        window.statusBarColor = getColor(R.color.colorGreenLight)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateMiniStatementFailureReceipt(miniStatementResponse: AEPSMiniStatementResponse?) {
        binding.message.text = miniStatementResponse?.message

        binding.toolbar4.background = getDrawable(R.drawable.cash_failed)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(Constants.redColor)))
        window.statusBarColor = getColor(R.color.red)
    }

    private fun generatePdf(uri: Uri) {
        val pageHeight = 1120
        val pageWidth = 792
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.swipecare_logo)
        val scaledBmp = Bitmap.createScaledBitmap(bmp, 260, 56, false)
        val sharedPreferences = SharePrfeManager.getInstance(this)

        val pdfDocument = PdfDocument()

        val paint = Paint()
        val title = Paint()

        val myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val myPage = pdfDocument.startPage(myPageInfo)

        val canvas = myPage.canvas
        canvas.drawBitmap(scaledBmp, (canvas.width / 2 - 100).toFloat(), 30f, paint)
        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        title.textSize = 15f
        title.color = ContextCompat.getColor(this, R.color.black)

        // user details
        val userDetailsPositionX = 20
        val userDetailsPositionY = 140
        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        title.color = ContextCompat.getColor(this, R.color.black)
        title.textSize = 20f
        canvas.drawText(sharedPreferences.mGetUsername(),
            userDetailsPositionX.toFloat(),
            userDetailsPositionY.toFloat(),
            title)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.textSize = 17f
        canvas.drawText(sharedPreferences.mGetMobile(),
            userDetailsPositionX.toFloat(),
            (userDetailsPositionY + 30).toFloat(),
            title)

        // Transactions made by the user
        title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        title.color = ContextCompat.getColor(this, R.color.black)
        title.textSize = 20f
        canvas.drawText(getString(R.string.date),
            userDetailsPositionX.toFloat(),
            (userDetailsPositionY + 80).toFloat(),
            title)
        canvas.drawText(getString(R.string.description),
            (userDetailsPositionX + 120).toFloat(),
            (userDetailsPositionY + 80).toFloat(),
            title)
        canvas.drawText(getString(R.string.amountCaps),
            (userDetailsPositionX + canvas.width / 2 + 100).toFloat(),
            (userDetailsPositionY + 80).toFloat(),
            title)
        canvas.drawText(getString(R.string.transactionType),
            (userDetailsPositionX + canvas.width / 1.2).toFloat(),
            (userDetailsPositionY + 80).toFloat(),
            title)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.color = ContextCompat.getColor(this, R.color.black)
        title.textSize = 18f
        var yStartPosition = userDetailsPositionY + 160
        val xStartPosition = userDetailsPositionX + 120
        val userTransactions = miniStatementResponse.data
        if (userTransactions != null) {
            for (i in userTransactions.indices) {
                canvas.drawText(userTransactions[i].date,
                    userDetailsPositionX.toFloat(),
                    yStartPosition.toFloat(),
                    title)
                // TODO: for large description we have to do text wrapping.
                val tp = TextPaint()
                tp.color = Color.BLACK
                tp.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                tp.textSize = 17f
                val textWidth =
                    ((userDetailsPositionX + canvas.width / 1.5).toFloat() - userDetailsPositionX + canvas.width / 2).toInt()
                val staticLayout = StaticLayout(userTransactions[i].description, tp,
                    300, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false)
                canvas.translate(xStartPosition.toFloat(), (yStartPosition - 15).toFloat())
                staticLayout.draw(canvas)
                canvas.translate(-xStartPosition.toFloat(), (-yStartPosition + 15).toFloat())

//            canvas.drawText(userTransactions.get(i).getDescription(), xStartPosition, yStartPosition, title);
                canvas.drawText(userTransactions[i].amount.toString(),
                    (userDetailsPositionX + 100 + canvas.width / 2).toFloat(),
                    yStartPosition.toFloat(),
                    title)
                canvas.drawText(userTransactions[i].txnType,
                    (userDetailsPositionX + canvas.width / 1.2).toFloat(),
                    yStartPosition.toFloat(),
                    title)
                yStartPosition += 80
            }
        } else {
            Toast.makeText(this, getString(R.string.no_transactions_found), Toast.LENGTH_LONG)
                .show()
        }
        pdfDocument.finishPage(myPage)

        try {
            contentResolver.openFileDescriptor(uri, "w")?.use {
                FileOutputStream(it.fileDescriptor).use { fos ->
                    pdfDocument.writeTo(contentResolver.openOutputStream(uri, "w"))
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        /*val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "MiniStatement.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(application,
                getString(R.string.pdf_file_generated_successfully),
                Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }*/

        pdfDocument.close()
    }

    private fun shareReceipt(receipt: Bitmap) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/png"

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Receipt")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values)

        val outStream: OutputStream?
        try {
            outStream = contentResolver.openOutputStream(uri!!)
            receipt.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            outStream!!.close()
        } catch (e: java.lang.Exception) {
            System.err.println(e.toString())
        }

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))

        // To delete the file that the user has already shared
        /*val fDelete = File(getFilePath(uri!!))

        if (fDelete.exists()) {
            if (fDelete.delete()) {
                Toast.makeText(this, "Receipt Deleted", Toast.LENGTH_SHORT).show()
            } else {
                // todo: do something if the deletion fails
            }
        }*/
    }

    private fun createFile(pickerInitialUri: Uri? = null) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "MiniStatement.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
//            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE && resultCode == RESULT_OK) {
            data?.data?.also { uri ->
                generatePdf(uri)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mini_statement_download_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.download_mini_statement -> {
                if (checkPermission()) {
                    createFile()
                } else {
                    requestPermission()
                }
                true
            }
            R.id.share_receipt -> {
                binding.viewForScreenshot.background = ColorDrawable(Color.parseColor(
                    Constants.colorBackground))
                screenshot(binding.viewForScreenshot)
                binding.viewForScreenshot.background = null
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun screenshot(view: View) {
        val date = Date()

        /*val format = DateFormat.format("yyyy_MM_dd_hh:mm:ss", date)
        val filename =
            if (cashWithdrawResponse != null) "cash_withdrawal_receipt" else "balance_enquiry_receipt"
        val dirName =
            if (cashWithdrawResponse != null) "cash_withdraw_dir" else "balance_enquiry_dir"
*/
        try {
            view.isDrawingCacheEnabled = true
            val icon: Bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            shareReceipt(icon)

            /*Toast.makeText(this, "Receipt Downloaded Successfully", Toast.LENGTH_LONG)
                .show()*/
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "Some Error occurred downloading receipt $e", Toast.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            Toast.makeText(this, "Some Error occurred downloading receipt $e", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setupActionbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0.0f
        supportActionBar?.title = getString(R.string.mini_statement)
    }

    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED || permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE)
    }
}