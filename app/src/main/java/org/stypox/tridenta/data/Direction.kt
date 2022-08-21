package org.stypox.tridenta.data

enum class Direction(val value: Int?) {
    Forward(0), // andata
    Backward(1), // ritorno
    ForwardAndBackward(null), // entrambi
}