package jp.wataju.debug

fun printMessage(key: String, value: String?) {

    if ( value != null ) {
        println(
            """
            *---------------*
             $key: $value
            *---------------*
            """.trimIndent()
        )
    } else {
        println(
            """
            *---------------*
             $key: 表示するメッセージはありません($value)
            *---------------*
            """.trimIndent()
        )
    }
}
