package com.vlogonappv1.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.vlogonappv1.R


class CustomErrorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_custom_error)


        val errorDetailsText = findViewById<TextView>(R.id.error_details)
        errorDetailsText.text = CustomActivityOnCrash.getStackTraceFromIntent(intent)

        val restartButton = findViewById<Button>(R.id.restart_button)

        val config = CustomActivityOnCrash.getConfigFromIntent(intent)

        if (config == null) {
            //This should never happen - Just finish the activity to avoid a recursive crash.
            finish()
            return
        }

        if (config.isShowRestartButton && config.restartActivityClass != null) {
            restartButton.setText(R.string.restart_app)
            restartButton.setOnClickListener {
                CustomActivityOnCrash.restartApplication(
                    this@CustomErrorActivity,
                    config
                )
            }
        } else {
            restartButton.setOnClickListener {
                CustomActivityOnCrash.closeApplication(
                    this@CustomErrorActivity,
                    config
                )
            }
        }
    }
}
