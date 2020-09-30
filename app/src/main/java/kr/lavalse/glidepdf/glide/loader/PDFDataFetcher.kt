package kr.lavalse.glidepdf.glide.loader

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.data.DataFetcher
import com.shockwave.pdfium.PdfiumCore
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class PDFDataFetcher(private val context: Context, uri: Uri) : DataFetcher<ByteBuffer> {
    private val resolver = context.contentResolver

    private var fd = resolver.openFileDescriptor(uri, "r")

    private var page : Int = 0

    private var width = 0
    private var height = 0

    /**
     * 실질적인 데이터를 변환하는 작업을 하는 곳 == 데이터를 로드하는 곳
     */
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in ByteBuffer>) {
        try {
            val buffer : ByteBuffer

            with(PdfiumCore(context)){
                val document = newDocument(fd)
                openPage(document, page)

                if(!document.hasPage(page))
                    throw NullPointerException("없는 페이지 ($page)")

                // PDF 원본 사이즈
                val size = getPageSize(document, page)

                // 화면에 맞게 사이즈를 줄인다
                var (_width, _height) = width to height
                if(size.width < size.height){
                    _width = (size.width * (_height / size.height.toFloat())).toInt()
                }else{
                    _height = (size.height * (_width / size.width.toFloat())).toInt()
                }

                // PDF 가 그려질 Bitmap 객체를 생성한다.
                // Config.RGB_565 를 선택하면 메모리가 그나마 적게 든다 ARGB_8888 이랑 비교해도 별 차이 안 느껴질 정도
                val out = Bitmap.createBitmap(_width, _height, Bitmap.Config.RGB_565)

                renderPageBitmap(document, out, page, 0, 0, out.width, out.height)
                closeDocument(document)

                // ByteBuffer 를 만들기 위해서는 Compress 를 통해서 ByteArrayOutputStream 에 접근해야 한다.
                // 그 다음 ByteArrayOutputStream 에서 toByteArray 를 통해 꺼내 올 수 있다.
                buffer = ByteArrayOutputStream().use {
                    out.compress(Bitmap.CompressFormat.JPEG, 80, it)

                    ByteBuffer.wrap(it.toByteArray())
                }

                // 비트맵은 항상 필요없어지면 recycle 을 호출한다.
                out.recycle()
            }

            // ByteBuffer 가 만들어지면 호출
            callback.onDataReady(buffer)

            // 전송한 버퍼는 클리
            buffer.clear()
        } catch (e : Exception) {
            callback.onLoadFailed(e)
        }
    }

    /**
     * InputStream 처럼 I/O 객체일 때 여기에서 close 하도록 한다. InputStream 을 읽어들일 때나 사용.
     */
    override fun cleanup() {
        // 열어놓은 파일을 닫아 놓는다.
        fd?.close()

        fd = null
    }

    /**
     * Request 가 취소되었을 때 처리를 구현한다. 네트워킹일 때나 사용
     */
    override fun cancel() {}

    /**
     * Data 클래스의 클래스 정보를 입력해준다.
     * 클래스 객체만 전달한다.
     */
    override fun getDataClass(): Class<ByteBuffer> = ByteBuffer::class.java

    /**
     * DataSource 의 캐시 정책 (인듯)
     */
    override fun getDataSource(): DataSource = DataSource.LOCAL

    /**
     * Render 하는 View 의 Size 를 전달 받는다
     */
    fun setSize(width: Int, height: Int){
        this.width = width
        this.height = height
    }

    fun setPage(page: Int){
        this.page = page
    }
}