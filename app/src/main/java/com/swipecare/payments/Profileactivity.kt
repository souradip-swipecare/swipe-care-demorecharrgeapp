package com.swipecare.payments

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.swipecare.payments.databinding.ProfileActivityBinding
import kotlinx.android.synthetic.main.report_activity.*
import kotlinx.coroutines.CoroutineScope

class Profileactivity : AppCompatActivity() {
    private lateinit var binding: ProfileActivityBinding

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPreferences: SharePrfeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setSupportActionBar(toolbar)
        window.statusBarColor = this.resources.getColor(R.color.colorPrimary)


        sharedPreferences = SharePrfeManager.getInstance(this)
        // Set Home selected
        binding.bottomNavigationView.selectedItemId = R.id.profile

        // Perform item selected listener

        // Perform item selected listener
        binding.run {
            bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener setOnNavigationItemSelectedListener@{ item: MenuItem ->
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.profile -> return@setOnNavigationItemSelectedListener true
                    R.id.my_reports ->{
                        startActivity(Intent(applicationContext, Reportactivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.help ->{
                        startActivity(Intent(applicationContext, HelpActtivity::class.java))
                        overridePendingTransition(0, 0)
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.actionLogout ->{
                        SharePrfeManager.getInstance(this@Profileactivity).mLogout()
                        startActivity(Intent(applicationContext, Login::class.java))
                        finish()
                        return@setOnNavigationItemSelectedListener true
                    }

                }
                false
            })
        }


        binding.name.text = sharedPreferences.mGetName()
        binding.email.text = sharedPreferences.email
        binding.aadhar.text = sharedPreferences.mGetaadharcard()
        binding.pan.text = sharedPreferences.mGetpancard()
        binding.address.text = sharedPreferences.mGetaddress()

        binding.button.setOnClickListener{
            Toast.makeText(
                this,
                "UPDATE FUNCTION IS NOT ALLOWED FOR NOW",
                Toast.LENGTH_LONG
            ).show()

        }

    }
}