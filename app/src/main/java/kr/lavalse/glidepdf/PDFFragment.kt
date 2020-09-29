package kr.lavalse.glidepdf

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import kr.lavalse.glidepdf.glide.GlideApp
import kr.lavalse.glidepdf.glide.page

class PDFFragment : Fragment() {
    companion object {
        private const val KEY_PDF_URI = "uri"
        private const val KEY_PAGE = "page"

        fun newInstance(uri: Uri, page: Int) : PDFFragment {
            val f = PDFFragment()

            f.arguments = Bundle().apply {
                putString(KEY_PDF_URI, uri.toString())
                putInt(KEY_PAGE, page)
            }

            return f
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ImageView(context).apply {
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        adjustViewBounds = true
        scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val uri = requireArguments().getString(KEY_PDF_URI)
        val page = requireArguments().getInt(KEY_PAGE)

        with(view){ this as ImageView
            GlideApp.with(context).asBitmap().load(Uri.parse(uri)).page(page).into(this)
        }
    }
}