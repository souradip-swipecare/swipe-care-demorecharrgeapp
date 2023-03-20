package com.swipecare.payments.transactionReceipts

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.swipecare.payments.Constants
import com.swipecare.payments.Constants.AEPS_BALANCE_ENQUIRY_RESPONSE
import com.swipecare.payments.Constants.AEPS_CASH_WITHDRAW_RESPONSE
import com.swipecare.payments.Constants.MOBILE_NUMBER
import com.swipecare.payments.Constants.colorBackground
import com.swipecare.payments.Constants.colorGreenLight
import com.swipecare.payments.Constants.redColor
import com.swipecare.payments.R
import com.swipecare.payments.databinding.ActivityCashWithdrawReceiptBinding
import com.swipecare.payments.model.AEPSBalanceEnquiryResponse
import com.swipecare.payments.model.AEPSCashWithdrawResponse
import java.io.FileNotFoundException
import java.io.OutputStream
import java.util.*

class CashWithdrawAndBalanceEnquiryReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCashWithdrawReceiptBinding
    private var cashWithdrawResponse: AEPSCashWithdrawResponse? = null
    private var balanceEnquiryResponse: AEPSBalanceEnquiryResponse? = null
    private var mobileNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cash_withdraw_receipt)

        cashWithdrawResponse =
            intent.getParcelableExtra(AEPS_CASH_WITHDRAW_RESPONSE)

        balanceEnquiryResponse =
            intent.getParcelableExtra(AEPS_BALANCE_ENQUIRY_RESPONSE)

        mobileNumber = intent.getStringExtra(MOBILE_NUMBER)

        if (cashWithdrawResponse != null && cashWithdrawResponse!!.status != "false") {
            updateCashWithdrawReceipt(cashWithdrawResponse!!)
            setupActionbar(getString(R.string.cash_withdraw_receipt))
        } else if (balanceEnquiryResponse != null && balanceEnquiryResponse!!.status != "false") {
            updateBalanceEnquiryReceipt(balanceEnquiryResponse!!)
            setupActionbar(getString(R.string.balance_enquiry_receipt))
        } else {
            if (cashWithdrawResponse != null && cashWithdrawResponse!!.status == "false") {
                Toast.makeText(this,
                    cashWithdrawResponse!!.message,
                    Toast.LENGTH_LONG).show()
                updateReceiptUiWhenCashWithdrawFailed(cashWithdrawResponse!!)
                setupActionbar(getString(R.string.cash_withdraw_receipt))
            } else if (balanceEnquiryResponse != null && balanceEnquiryResponse!!.status == "false") {
                Toast.makeText(this,
                    balanceEnquiryResponse!!.message,
                    Toast.LENGTH_LONG).show()
                updateReceiptUiWhenBalanceEnquiryFailed(balanceEnquiryResponse!!)
                setupActionbar(getString(R.string.balance_enquiry_receipt))
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateCashWithdrawReceipt(cashWithdrawResponse: AEPSCashWithdrawResponse) {
        binding.message.text = cashWithdrawResponse.message
        binding.aadhar.text = cashWithdrawResponse.aadhar
        binding.rrn.text = cashWithdrawResponse.rrn
        binding.bank.text = cashWithdrawResponse.bank
        binding.id.text = cashWithdrawResponse.id
        binding.balance.text = cashWithdrawResponse.balance
        binding.amount.text = cashWithdrawResponse.amount
        binding.transactionType.text = cashWithdrawResponse.transactionType
        binding.status.text = cashWithdrawResponse.status

        val date = Date()
        val format: CharSequence =
            android.text.format.DateFormat.format("dd-MM-yyyy hh:mm", date)

        binding.date.text = format.toString()
        binding.mobile.text = mobileNumber

        binding.toolbar4.background = getDrawable(R.drawable.cash_success)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(colorGreenLight)))
        window.statusBarColor = getColor(R.color.colorGreenLight)
    }

    private fun updateBalanceEnquiryReceipt(balanceEnquiryResponse: AEPSBalanceEnquiryResponse) {
        binding.message.text = balanceEnquiryResponse.message
        binding.aadhar.text = balanceEnquiryResponse.aadhar
        binding.rrn.text = balanceEnquiryResponse.rrn
        binding.bank.text = balanceEnquiryResponse.bank
        binding.id.text = balanceEnquiryResponse.id.toString()
        binding.balance.text = balanceEnquiryResponse.balance
        binding.transactionType.text = balanceEnquiryResponse.transactionType
        binding.status.text = balanceEnquiryResponse.status

        val date = Date()
        val format: CharSequence =
            android.text.format.DateFormat.format("dd-MM-yyyy hh:mm", date)

        binding.date.text = format.toString()
        binding.mobile.text = mobileNumber

        binding.amount.text = "0"
        binding.title.text = getString(R.string.balance_enquiry_caps)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateReceiptUiWhenCashWithdrawFailed(cashWithdrawResponse: AEPSCashWithdrawResponse) {
        binding.message.text = cashWithdrawResponse.message
        binding.status.text = cashWithdrawResponse.status

        binding.toolbar4.background = getDrawable(R.drawable.cash_failed)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(redColor)))
        window.statusBarColor = getColor(R.color.red)
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateReceiptUiWhenBalanceEnquiryFailed(balanceEnquiryResponse: AEPSBalanceEnquiryResponse) {
        binding.message.text = balanceEnquiryResponse.message
        binding.status.text = balanceEnquiryResponse.status

        binding.toolbar4.background = getDrawable(R.drawable.cash_failed)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor(redColor)))
        window.statusBarColor = getColor(R.color.red)
    }

    private fun setupActionbar(title: String) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0.0f
        supportActionBar?.title = title
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.cash_withdraw_balance_enquiry_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.share_receipt -> {
                if (checkPermission()) {
                    binding.viewForScreenshot.background = ColorDrawable(Color.parseColor(
                        colorBackground))
                    screenshot(binding.viewForScreenshot)
                    binding.viewForScreenshot.background = null
                    true
                } else {
                    requestPermission()
                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
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
            Constants.PERMISSION_REQUEST_CODE)
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

    private fun openReceiptScreenshot() {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        startActivity(Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    //getting real path from uri
    private fun getFilePath(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(projection[0])
            val picturePath: String = cursor.getString(columnIndex) // returns null
            cursor.close()
            return picturePath
        }
        return null
    }


}