package kr.lavalse.glidepdf.glide.loader

import android.content.Context
import android.net.Uri
import com.bumptech.glide.load.Option
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import java.nio.ByteBuffer
import java.security.MessageDigest

/**
 * URI 를 받는 ModelLoader
 */
class PDFModelLoader private constructor (private val context: Context) : ModelLoader<Uri, ByteBuffer> {
    companion object {
        // Key 의 이름을 지정한다. 해당 Key 를 통해서 Value 를 가져오는 것은 아니다.
        private const val OPTION_KEY_PAGE = "glide.pdf.page"

        // Option 은 Unique 해야 하기 때문에 static 한 곳에서 정의하라고 한다.
        // Option.memory 를 통해 Option 을 정의하며, defaultValue 를 함께 지정해줄 수 있다.
        val OPTION_PAGE : Option<Int> = Option.memory(OPTION_KEY_PAGE, 0)
    }

    /**
     * DataFetcher 에서 가져온 데이터를 Glide 내에서 처리할 수 있도록 넘겨주는 곳이다.
     */
    override fun buildLoadData(model: Uri, width: Int, height: Int, options: Options): ModelLoader.LoadData<ByteBuffer>? {
        val page = options[OPTION_PAGE]

        val fetcher = PDFDataFetcher(context, model).apply {
            setSize(width, height)

            page?.run(::setPage)
        }

        val key = ObjectKey("PAGE_${model}_${page}")

        return ModelLoader.LoadData(key, fetcher)
    }

    /**
     *  일종의 필터링 메소드.
     *  PDF 파일을 다룰 것이라고 명시를 한다.
     */
    override fun handles(model: Uri): Boolean = context.contentResolver.getType(model) == "application/pdf"

    /**
     * Factory 클래스
     * GlideModule 에 등록할 때에는 Factory 클래스를 통해서 등록하도록 되어있다.
     */
    class Factory(private val context:Context) : ModelLoaderFactory<Uri, ByteBuffer> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Uri, ByteBuffer>
            = PDFModelLoader(context)

        // 빈칸으로 냅두라고 함.
        override fun teardown() {}
    }
}