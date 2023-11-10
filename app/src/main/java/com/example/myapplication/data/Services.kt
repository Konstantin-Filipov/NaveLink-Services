package com.example.myapplication.data

data class Service(
    val id: Int,
    val name: String,
    val keywords: String,
    val geometry: String,
    val serviceType: String,
    val endpointUri: String,
    val instanceAsXml:XmlData
)
data class XmlData(
    val id:Int,
    val name:String,
    val comment:String,
    val content:String,
    val contentContentType:String
)
