package com.wafflestudio.spring2025.course.extract.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

@Service
class SugangFetchService(
    private val webClient: WebClient,
    @Value("\${sugang.init-url}") private val initUrl: String,
    @Value("\${sugang.ajax-url}") private val ajaxUrl: String,
    @Value("\${sugang.search-url}") private val searchUrl: String,
    @Value("\${sugang.export-url}") private val exportUrl: String,
) {
    fun fetchXls(
        year: Int,
        semCode: String,
    ): ByteArray {
        // go to sugang site
        webClient
            .get()
            .uri(initUrl)
            .retrieve()
            .toBodilessEntity()
            .block()

        // search
        webClient
            .post()
            .uri(ajaxUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
            .contentType(
                MediaType.APPLICATION_FORM_URLENCODED,
            ).body(BodyInserters.fromFormData("openUpDeptCd", "").with("openDeptCd", ""))
            .retrieve()
            .toBodilessEntity()
            .block()

        // before semester lookup
        webClient
            .post()
            .uri(ajaxUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
            .contentType(
                MediaType.APPLICATION_FORM_URLENCODED,
            ).body(
                BodyInserters
                    .fromFormData(
                        "openUpDeptCd",
                        "",
                    ).with("openDeptCd", "")
                    .with("srchOpenSchyy", year.toString())
                    .with("srchOpenShtm", "U000200002U000300002"),
            ).retrieve()
            .toBodilessEntity()
            .block()

        // choose 'semester - fall'
        webClient
            .post()
            .uri(ajaxUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
            .contentType(
                MediaType.APPLICATION_FORM_URLENCODED,
            ).body(
                BodyInserters
                    .fromFormData(
                        "openUpDeptCd",
                        "",
                    ).with("openDeptCd", "")
                    .with("srchOpenSchyy", year.toString())
                    .with("srchOpenShtm", semCode),
            ).retrieve()
            .toBodilessEntity()
            .block()

        // searching
        val search = baseSearchForm(year, semCode).toFormDataInserter()
        webClient
            .post()
            .uri(searchUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
            .contentType(
                MediaType.APPLICATION_FORM_URLENCODED,
            ).body(search)
            .retrieve()
            .toBodilessEntity()
            .block()

        // downloading excel(byte)
        val export = baseExportForm(year, semCode).toFormDataInserter()
        return webClient
            .post()
            .uri(exportUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
            .contentType(
                MediaType.APPLICATION_FORM_URLENCODED,
            ).body(export)
            .retrieve()
            .onStatus({ !it.is2xxSuccessful }) { resp ->
                resp.bodyToMono(String::class.java).map { body ->
                    IllegalStateException("Excel export failed: ${resp.statusCode()} body=$body")
                }
            }.toEntity(ByteArray::class.java)
            .map { entity ->
                val ct = entity.headers.contentType?.toString() ?: ""
                require(ct.contains("application/vnd.ms-excel") || ct.contains("application/octet-stream")) {
                    "Unexpected content-type from export: $ct"
                }
                entity.body ?: error("Empty excel bytes")
            }.block()!!
    }

    private fun baseSearchForm(
        year: Int,
        semCode: String,
    ): Map<String, String> =
        mapOf(
            "workType" to "S",
            "pageNo" to "1",
            "srchOpenSchyy" to year.toString(),
            "srchOpenShtm" to semCode,
            "srchSbjtNm" to "",
            "srchSbjtCd" to "",
            "seeMore" to "닫기",
            "srchCptnCorsFg" to "",
            "srchOpenShyr" to "",
            "srchOpenUpSbjtFldCd" to "",
            "srchOpenSbjtFldCd" to "",
            "srchOpenUpDeptCd" to "",
            "srchOpenDeptCd" to "",
            "srchOpenMjCd" to "",
            "srchOpenSubmattCorsFg" to "",
            "srchOpenSubmattFgCd1" to "",
            "srchOpenSubmattFgCd2" to "",
            "srchOpenSubmattFgCd3" to "",
            "srchOpenSubmattFgCd4" to "",
            "srchOpenSubmattFgCd5" to "",
            "srchOpenSubmattFgCd6" to "",
            "srchOpenSubmattFgCd7" to "",
            "srchOpenSubmattFgCd8" to "",
            "srchOpenSubmattFgCd9" to "",
            "srchExcept" to "",
            "srchOpenPntMin" to "",
            "srchOpenPntMax" to "",
            "srchCamp" to "",
            "srchBdNo" to "",
            "srchProfNm" to "",
            "srchOpenSbjtTmNm" to "",
            "srchOpenSbjtDayNm" to "",
            "srchOpenSbjtTm" to "",
            "srchOpenSbjtNm" to "",
            "srchTlsnAplyCapaCntMin" to "",
            "srchTlsnAplyCapaCntMax" to "",
            "srchLsnProgType" to "",
            "srchTlsnRcntMin" to "",
            "srchTlsnRcntMax" to "",
            "srchMrksGvMthd" to "",
            "srchIsEngSbjt" to "",
            "srchMrksApprMthdChgPosbYn" to "",
            "srchIsPendingCourse" to "",
            "srchGenrlRemoteLtYn" to "",
            "srchLanguage" to "ko",
            "srchCurrPage" to "1",
            "srchPageSize" to "9999",
        )

    private fun baseExportForm(
        year: Int,
        semCode: String,
    ): Map<String, String> =
        baseSearchForm(year, semCode).toMutableMap().apply {
            this["workType"] = "EX"
        }

    private fun Map<String, String>.toFormDataInserter(): BodyInserters.FormInserter<String> {
        var inserter = BodyInserters.fromFormData(firstKeyOrEmpty(), firstValueOrEmpty())
        this.entries.drop(1).forEach { (k, v) -> inserter = inserter.with(k, v) }
        return inserter
    }

    private fun Map<String, String>.firstKeyOrEmpty() = keys.firstOrNull() ?: ""

    private fun Map<String, String>.firstValueOrEmpty() = values.firstOrNull() ?: ""
}
