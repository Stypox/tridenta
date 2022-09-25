package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.enums.StopLineType
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SampleDbNewsItemProvider : PreviewParameterProvider<DbNewsItem> {
    override val values: Sequence<DbNewsItem> = sequenceOf(
        DbNewsItem(
            serviceType = "Generale",
            startDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1649289600), ZoneOffset.UTC),
            endDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1672444800), ZoneOffset.UTC),
            header = "OBBLIGO MASCHERINA FFP2 DAI 6 ANNI DI ETA'",
            details = "Per l'utilizzo dei mezzi pubblici (autobus urbani ed extraurbani," +
                    " treni Ferrovia Trento Male' Mezzana e Trento Borgo Bassano, Funivia" +
                    " Trento Sardagna) e' prevista: --la mascherina FFP2 a copertura di" +
                    " naso e bocca a partire dai 6 anni di eta'.",
            url = "https://www.trentinotrasporti.it/avvisi/7889-obbligo-mascherina-ffp2-" +
                    "dai-6-anni-di-eta-2022-174",
            lineId = 0,
            lineType = StopLineType.Urban,
        ),
        DbNewsItem(
            serviceType = "Extraurbano provinciale",
            startDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1650412800), ZoneOffset.UTC),
            endDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1686355200), ZoneOffset.UTC),
            header = "Sospensione ferrmate a Masi e Lago di Tesero",
            details = "Con decorrenza immediata sono temporaneamente sospese le fermate di Lago" +
                    " di Tesero e di Masi \"Az.Agricola\" e Masi via Miceletti.",
            url = "https://www.trentinotrasporti.it/avvisi/7923-sospensione-ferrmate-a-masi-e-" +
                    "lago-di-tesero-2022-204",
            lineId = 0,
            lineType = StopLineType.Urban,
        ),
        DbNewsItem(
            serviceType = "Extraurbano provinciale",
            startDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1662595200), ZoneOffset.UTC),
            endDate = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1664150400), ZoneOffset.UTC),
            header = "Istituzione collegamenti Penia-Passo Fedaia dal 12 al 25 settembre",
            details = "Dal 12 al 25 settembre 2022, sono istituiti due collegamenti giornalieri" +
                    " da Penia a Passo Fedaia e ritonro.",
            url = "https://www.trentinotrasporti.it/avvisi/8361-istituzione-collegamenti-penia-" +
                    "passo-fedaia-dal-12-al-25-settembre-2022-581",
            lineId = 0,
            lineType = StopLineType.Urban,
        ),
    )
}