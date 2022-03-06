package zsofi.applications.githubreposearchapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import zsofi.applications.githubreposearchapp.databinding.ActivityDetailedRepositoryBinding
import zsofi.applications.githubreposearchapp.databinding.ActivityMainBinding

class DetailedRepositoryActivity : AppCompatActivity() {

    private var binding: ActivityDetailedRepositoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailedRepositoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Setting up the Action bar
        setSupportActionBar(binding?.toolBarDetailedRepository)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Repository details"
            binding?.toolBarDetailedRepository?.setNavigationOnClickListener {
                onBackPressed()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        // Setting back binding to null when the Activity is destroyed
        if (binding != null) {
            binding = null
        }
    }
}