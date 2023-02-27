package jp.wataju.model.entity

import jp.wataju.model.table.Product
import org.jetbrains.exposed.sql.ResultRow

class Product(resultRow: ResultRow) {
    var productId = resultRow[Product.productId]
    var productName = resultRow[Product.productName]
    var price = resultRow[Product.price]
}
