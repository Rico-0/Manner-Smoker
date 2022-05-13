package com.kapstone.mannersmoker.util

// 두 지점간의 거리를 계산 (흡연 구역 반경 몇 m 내에 있을 경우 푸시 알림 전송에 사용됨)
// https://www.codegrepper.com/code-examples/java/kotlin+calculate+distance+between+two+locations+in+meters
// https://fruitdev.tistory.com/189

object LocationDistance {
    fun distance(lat1: Double, lon1 : Double, lat2: Double, lon2 : Double, unit : String) : Double {
        val theta : Double = lon1 - lon2
        var dist : Double = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))

        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515

        if (unit.equals("kilometer"))
            dist *= 1.609344
        else if (unit.equals("meter"))
            dist *= 1609.344
        return (dist)
    }

    private fun deg2rad(deg : Double): Double {
        return (deg * Math.PI / 180.0)
    }

    private fun rad2deg(rad : Double) : Double {
        return (rad * 180 / Math.PI)
    }
}