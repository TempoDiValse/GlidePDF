package kr.lavalse.glidepdf.glide

import android.content.Context
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import kr.lavalse.glidepdf.glide.loader.PDFModelLoader
import java.nio.ByteBuffer

@GlideModule
class GlideModuler : AppGlideModule() {
    /**
     * 커스텀으로 개발한 모듈을 등록한다.
     */
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry
            .prepend(Uri::class.java, ByteBuffer::class.java, PDFModelLoader.Factory(context)) // PDF 로더
    }

    /**
     * Glide 모듈의 Default 를 설정하는 곳
     */
    override fun applyOptions(context: Context, builder: GlideBuilder) {}

    /**
     * 얘는 뭔지 모르겠음
     */
    override fun isManifestParsingEnabled(): Boolean = false
}