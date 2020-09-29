package kr.lavalse.glidepdf

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shockwave.pdfium.PdfiumCore

class PDFAdapter(private val activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private lateinit var uri : Uri
    private var pageCount = 0

    override fun getItemCount(): Int = pageCount
    override fun createFragment(position: Int): Fragment = PDFFragment.newInstance(uri, position)

    fun setPDF(uri : Uri){
        this.uri = uri

        val resolver = activity.contentResolver
        resolver.openFileDescriptor(uri, "r")?.use {
            with(PdfiumCore(activity)){
                val doc = newDocument(it)

                pageCount = getPageCount(doc)

                closeDocument(doc)
            }
        }

        notifyDataSetChanged()
    }
}