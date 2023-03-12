package jp.wataju.debug

import jp.wataju.CUSTOMER_MANAGEMENT_SYSTEM
import jp.wataju.DATA_PATH
import jp.wataju.connect
import jp.wataju.model.table.Order
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {

    connect(DATA_PATH, CUSTOMER_MANAGEMENT_SYSTEM)
    transaction {

        /*
        SchemaUtils.create(Account)
        SchemaUtils.create(User)
        SchemaUtils.create(Customer)
        SchemaUtils.create(Product)
        SchemaUtils.drop(Order)
        SchemaUtils.create(Order)
        */

        // Account.deleteAll()
        // User.deleteAll()

        /*
        Customer.insert {
            it[customerName] = "村瀬 賢"
            it[customerNameKana] = "ムラセ ケン"
            it[zipcode] = "5040008"
            it[prefecture] = "岐阜県"
            it[address1] = "各務原市那加桐野町"
            it[address2] = "6-38"
            it[address3] = ""
            it[customerPhone] = "08043036523"
            it[customerMail] = "lax.potcx1562@gmail.com"
        }
        Customer.insert {
            it[customerName] = "村瀬 美帆"
            it[customerNameKana] = "ムラセ ミホ"
            it[zipcode] = "5030852"
            it[prefecture] = "岐阜県"
            it[address1] = "大垣市禾森町"
            it[address2] = "2-26-26"
            it[address3] = "プラシード101"
            it[customerPhone] = "0584272091"
            it[customerMail] = "m_S620@icloud.com"
        }
        Customer.insert {
            it[customerName] = "加藤 瑞己"
            it[customerNameKana] = "カトウ タマキ"
            it[zipcode] = "5016337"
            it[prefecture] = "岐阜県"
            it[address1] = "羽島市堀津町"
            it[address2] = "中屋敷"
            it[address3] = "93"
            it[customerPhone] = "09079568547"
            it[customerMail] = "watajuu1110@yahoo.co.jp"
        }
        Customer.insert {
            it[customerName] = "田代 正二"
            it[customerNameKana] = "タシロ ショウジ"
            it[zipcode] = "5032311"
            it[prefecture] = "岐阜県"
            it[address1] = "安八郡神戸町"
            it[address2] = "川西"
            it[address3] = "88-1"
            it[customerPhone] = "0584272091"
            it[customerMail] = "watajuu1110@yahoo.co.jp"
        }
         */


        Order.deleteAll()

        /*
        Product.insert {
            it[productName] = "芳醇美濃路本醸造醬油（1.0L）"
            it[price] = 440
        }
        Product.insert {
            it[productName] = "芳醇美濃路本醸造醬油（1.8L）"
            it[price] = 700
        }
        Product.insert {
            it[productName] = "赤だし味噌"
            it[price] = 580
        }
        Product.insert {
            it[productName] = "味付酢"
            it[price] = 540
        }
        Product.insert {
            it[productName] = "うまかボン酢"
            it[price] = 640
        }
        Product.insert {
            it[productName] = "うまかソース"
            it[price] = 660
        }
        Product.insert {
            it[productName] = "美濃路料理の素"
            it[price] = 580
        }
        Product.insert {
            it[productName] = "美濃路つゆの素"
            it[price] = 1050
        }
        Product.insert {
            it[productName] = "淡口醤油"
            it[price] = 500
        }
         */
    }
}
