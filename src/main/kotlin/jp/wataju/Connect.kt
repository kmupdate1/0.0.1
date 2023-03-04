package jp.wataju

import org.jetbrains.exposed.sql.Database

fun connect(platform: String, database: String) {

    Database.connect(
        "jdbc:sqlite:${platform}${database}",
        "org.sqlite.JDBC"
    )

}

const val DATA_PATH = "/Users/ken/Library/CloudStorage/OneDrive-個人用/ドキュメント/10_綿重/05_顧客管理/01_作業/01_project/app/ktor/wataju/0.0.1/src/main/resources/data/"
const val CUSTOMER_MANAGEMENT_SYSTEM = "customer_management_system.db"
const val s = "C:\\Users\\ctjks\\OneDrive\\ドキュメント\\10_綿重\\05_顧客管理\\01_作業\\01_project\\app\\ktor\\wataju\\0.0.1\\src\\main\\resources\\data\\"
