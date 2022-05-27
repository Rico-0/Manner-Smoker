package com.kapstone.mannersmoker.model.data

object HomeData {
    var currentHealth : Int = 100 // 100 ~ 80 : 최상, 80 ~ 60 : 상, 60 ~ 40 : 중, 40 ~ 20 : 하, 20 ~ : 최하
    lateinit var timeFromLastSmoke : String
    lateinit var timeStartSmoke : String
    var usedMoney : Int = 0
    var todaySmokeAmount : Int = 0
}