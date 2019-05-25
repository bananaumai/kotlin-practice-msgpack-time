package dev.bananaumai.practice.msgpack.time

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun main() {
    val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)

    val time = ZonedDateTime.parse("2019-05-24T09:09:09.999999999Z", formatter)

    println(time)

    val encoded = encodeTime(time)

    println(encoded.joinToString(" ") { "%02X".format(it) } )
}

fun encodeTime(time: ZonedDateTime): ByteArray {
    val sec = time.toEpochSecond().toULong()
    val nano = time.nano

    if (sec.shr(34) == 0UL) {
        val data = nano.toULong().shl(34).or(sec)

        return if (data.and(0xffffffff00000000u) == 0UL) {
            data.toUInt().toByteArray()
        } else {
            data.toByteArray()
        }
    }

    return nano.toUInt().toByteArray() + sec.toULong().toByteArray()
}

fun UInt.toByteArray(): ByteArray {
    val bufferSize = UInt.SIZE_BYTES

    var bytes = byteArrayOf()

    var num = this

    repeat(bufferSize) {
        val byte = num.and(0xFFu).toByte()

        println("%02X".format(byte))

        bytes += byte

        num = num.shr(Byte.SIZE_BITS)
    }

    return bytes.reversedArray()
}

fun ULong.toByteArray(): ByteArray {
    val bufferSize = ULong.SIZE_BYTES

    var bytes = byteArrayOf()

    var num = this

    repeat(bufferSize) {
        val byte = num.and(0xFFu).toByte()

        println("%02X".format(byte))

        bytes += byte

        num = num.shr(Byte.SIZE_BITS)
    }

    return bytes.reversedArray()
}