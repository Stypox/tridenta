package org.stypox.tridenta.repo

import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.repo.data.UiStopTime
import org.stypox.tridenta.repo.data.UiTrip

abstract class TripsRepository(
    private val linesRepository: LinesRepository,
    private val stopsRepository: StopsRepository
) {
    protected fun loadUiTripFromExTrip(exTrip: ExTrip): UiTrip {
        return UiTrip(
            delay = exTrip.delay,
            direction = exTrip.direction,
            lastEventReceivedAt = exTrip.lastEventReceivedAt,
            line = linesRepository.getDbLine(exTrip.lineId, exTrip.type),
            headSign = exTrip.headSign,
            tripId = exTrip.tripId,
            type = exTrip.type,
            completedStops = exTrip.completedStops,
            stopTimes = exTrip.stopTimes.map { exStopTime ->
                UiStopTime(
                    arrivalTime = exStopTime.arrivalTime,
                    departureTime = exStopTime.departureTime,
                    stop = stopsRepository.getDbStop(exStopTime.stopId, exTrip.type)
                )
            }
        )
    }
}