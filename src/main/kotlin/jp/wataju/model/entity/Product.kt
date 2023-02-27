package jp.wataju.model.entity

import jp.wataju.TAX_INCLUDE
import jp.wataju.model.table.Product
import org.jetbrains.exposed.sql.ResultRow
import kotlin.math.ceil

class Product(resultRow: ResultRow) {
    val productId = resultRow[Product.productId]
    val productName = resultRow[Product.productName]
    val price = resultRow[Product.price]
    val priceTax = ceil(price * TAX_INCLUDE)
}
