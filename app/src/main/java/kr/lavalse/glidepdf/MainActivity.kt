package kr.lavalse.glidepdf

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val REQ_CODE = 1000
    }

    private lateinit var adapter : PDFAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        adapter = PDFAdapter(this)

        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 2
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            REQ_CODE -> {
                if(resultCode == Activity.RESULT_CANCELED) return

                val uri = data?.data ?: return
                adapter.setPDF(uri)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
        = when(item.itemId){
            R.id.mOpen -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                    type = "application/pdf"
                    addCategory(Intent.CATEGORY_OPENABLE)
                }

                startActivityForResult(intent, REQ_CODE)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }

}