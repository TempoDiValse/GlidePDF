package kr.lavalse.glidepdf.glide

import android.graphics.Bitmap
import kr.lavalse.glidepdf.glide.loader.PDFModelLoader

fun GlideRequest<Bitmap>.page(page: Int) = apply {
    set(PDFModelLoader.OPTION_PAGE, page)
}