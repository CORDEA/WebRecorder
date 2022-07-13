package jp.cordea.webrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.cordea.webrecorder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
        }
    }
}
