package org.stypox.tridenta.util

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.getStackTraceString(): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    this.printStackTrace(printWriter)
    return stringWriter.toString()
}