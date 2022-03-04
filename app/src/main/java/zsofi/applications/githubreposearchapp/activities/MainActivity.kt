package zsofi.applications.githubreposearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarMain)
        if(supportActionBar != null){
            supportActionBar?.title = "Search for GitHub Repositories"
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if(binding != null){
            binding = null
        }
    }
}