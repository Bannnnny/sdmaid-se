package eu.darken.sdmse.appcleaner.core.forensics.filter

import eu.darken.sdmse.appcleaner.core.forensics.*
import eu.darken.sdmse.common.areas.DataArea.Type.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AdvertisementFilterTest : BaseFilterTest() {

    @BeforeEach
    override fun setup() {
        super.setup()
    }

    @AfterEach
    override fun teardown() {
        super.teardown()
    }

    private fun create() = AdvertisementFilter(
        jsonBasedSieveFactory = createSieveFactory()
    )

    @Test fun testAnalyticsFilterMologiq() = runTest {
        addDefaultNegatives()
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA).prefixFree("eu.thedarken.sdm.test/files")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.something.mologiq")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.abcedefg.mologiq")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA).prefixFree("eu.thedarken.sdm.test/databases")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/databases/mologiq_")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/item/.b0b0bf57-012d-4b0f-8266-1ca07820a91a.mologiq")
        )
        addCandidate(
            neg().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/databases/item/mologiq")
        )
        addCandidate(
            pos().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.b0b0bf57-012d-4b0f-8266-1ca07820a91a.mologiq")
        )
        addCandidate(
            pos().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.e3883dc0-5bd6-4b93-840a-5d95d788a87e.mologiq")
        )
        addCandidate(
            pos().pkgs(testPkg).locs(PRIVATE_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.13a5fef7-518e-4a61-b856-5ae5a8701da0.mologiq")
        )
        addCandidate(
            pos().pkgs(testPkg).locs(PRIVATE_DATA).prefixFree("eu.thedarken.sdm.test/databases/mologiq")

        )
        confirm(create())
    }

    @Test fun testVulge() = runTest {
        addDefaultNegatives()
        addCandidate(
            neg().pkgs(testPkg).locs(PUBLIC_DATA).prefixFree("eu.thedarken.sdm.test/files/.vungleabc")
        )
        addCandidate(neg().pkgs(testPkg).locs(PUBLIC_DATA).prefixFree("eu.thedarken.sdm.test/.vungle"))
        addCandidate(
            neg().pkgs(testPkg).locs(PUBLIC_DATA).prefixFree("eu.thedarken.sdm.test/files/abc.vungleabc")
        )
        addCandidate(pos().pkgs(testPkg).locs(PUBLIC_DATA).prefixFree("eu.thedarken.sdm.test/files/.vungle"))
        addCandidate(
            pos().pkgs(testPkg).locs(PUBLIC_DATA)
                .prefixFree("eu.thedarken.sdm.test/files/.vungle/" + UUID.randomUUID().toString())
        )
        confirm(create())
    }

    @Test fun testWeChat() = runTest {
        addDefaultNegatives()
        addCandidate(
            neg().pkgs("com.tencent.mm").locs(SDCARD).prefixFree("tencent/MicroMsg/sns_ad_landingpages")
        )
        addCandidate(
            neg().pkgs("com.tencent.mm").locs(SDCARD).prefixFree("tencent/MicroMsg/sns_ad_landingpages/.nomedia")

        )
        addCandidate(
            pos().pkgs("com.tencent.mm").locs(SDCARD)
                .prefixFree("tencent/MicroMsg/sns_ad_landingpages/" + UUID.randomUUID().toString())
        )
        confirm(create())
    }

    @Test fun testMidasOversea() = runTest {
        addDefaultNegatives()
        addCandidate(neg().pkgs("com.vng.pubgmobile", "com.tencent.mm").locs(SDCARD).prefixFree("MidasOversea"))
        addCandidate(
            neg().pkgs("com.vng.pubgmobile", "com.tencent.mm").locs(SDCARD).prefixFree("MidasOversea/.nomedia")
        )
        addCandidate(
            pos().pkgs("com.vng.pubgmobile", "com.tencent.mm").locs(SDCARD)
                .prefixFree("MidasOversea/" + UUID.randomUUID().toString())
        )
        confirm(create())
    }

    // https://github.com/d4rken/sdmaid-public/issues/3124
    @Test fun testVungleCache() = runTest {
        addDefaultNegatives()
        addCandidate(
            neg().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA).prefixFree("com.sega.sonicboomandroid/files")

        )
        addCandidate(
            neg().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA)
                .prefixFree("com.sega.sonicboomandroid/files/vungle_ca")
        )
        addCandidate(
            pos().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA)
                .prefixFree("com.sega.sonicboomandroid/files/vungle_cache")
        )
        addCandidate(
            pos().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA)
                .prefixFree("com.sega.sonicboomandroid/files/vungle_cache/" + UUID.randomUUID().toString())
        )

        // https://github.com/d4rken/sdmaid-public/issues/5485
        addCandidate(
            neg().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA).prefixFree("com.sega.sonicboomandroid/no_backup")

        )
        addCandidate(
            pos().pkgs("com.sega.sonicboomandroid").locs(PUBLIC_DATA)
                .prefixFree("com.sega.sonicboomandroid/no_backup/vungle_cache/" + UUID.randomUUID().toString())
        )
        confirm(create())
    }

    @Test fun testMeizuMedia() = runTest {
        addCandidate(
            neg().pkgs("com.meizu.media.video").locs(PUBLIC_DATA).prefixFree("com.meizu.media.video/MzAdLog")
        )
        addCandidate(
            pos().pkgs("com.meizu.media.video").locs(PUBLIC_DATA).prefixFree("com.meizu.media.video/MzAdLog/something")

        )
        confirm(create())
    }

    @Test fun testZCamera() = runTest {
        addCandidate(neg().pkgs("com.jb.zcamera").locs(PUBLIC_OBB).prefixFree("com.jb.zcamera/GoAdSdk/advert"))
        addCandidate(
            pos().pkgs("com.jb.zcamera").locs(PUBLIC_OBB).prefixFree("com.jb.zcamera/GoAdSdk/advert/cacheFile")
        )
        confirm(create())
    }

    @Test fun faceEditor() = runTest {
        addCandidate(
            neg().pkgs("com.scoompa.faceeditor").locs(PUBLIC_DATA).prefixFree("com.scoompa.faceeditor/files/ads")

        )
        addCandidate(
            pos().pkgs("com.scoompa.faceeditor").locs(PUBLIC_DATA).prefixFree("com.scoompa.faceeditor/files/ads/adfile")

        )
        confirm(create())
    }

    @Test fun testTouchPalAdCache() = runTest {
        val pkgs = arrayOf(
            "com.cootek.smartinputv5",
            "com.cootek.smartinputv5.oem",
            "com.emoji.keyboard.touchpal",
            "com.keyboard.cb.oem"
        )
        addCandidate(neg().pkgs(*pkgs).locs(SDCARD).prefixFree("TouchPal2015/plugin_cache"))
        addCandidate(pos().pkgs(*pkgs).locs(SDCARD).prefixFree("TouchPal2015/plugin_cache/theCakeIsALie"))
        confirm(create())
    }

    @Test fun testMeizuFileManager() = runTest {
        addCandidate(
            neg().pkgs("com.meizu.filemanager").locs(PUBLIC_DATA).prefixFree("com.meizu.filemanager/update_component")

        )
        addCandidate(
            pos().pkgs("com.meizu.filemanager").locs(PUBLIC_DATA)
                .prefixFree("com.meizu.filemanager/update_component_log")
        )
        addCandidate(
            pos().pkgs("com.meizu.filemanager").locs(PUBLIC_DATA)
                .prefixFree("com.meizu.filemanager/update_component_log123")
        )
        confirm(create())
    }

    // https://github.com/d4rken/sdmaid-public/issues/4230
    @Test fun testVideoLike() = runTest {
        addDefaultNegatives()
        addCandidate(neg().pkgs("video.like").locs(SDCARD).prefixFree("._sdk_ruui"))
        addCandidate(neg().pkgs("video.like").locs(SDCARD).prefixFree("_sdk_ruuid"))
        addCandidate(pos().pkgs("video.like").locs(SDCARD).prefixFree("._sdk_ruuid"))
        confirm(create())
    }

    @Test fun appoDeal() = runTest {
        addDefaultNegatives()
        addCandidate(neg().pkgs("com.ludashi.dualspace").locs(SDCARD).prefixFree(".appodea"))
        addCandidate(neg().pkgs("com.ludashi.dualspace").locs(SDCARD).prefixFree(".appodeall"))
        addCandidate(pos().pkgs("com.ludashi.dualspace").locs(SDCARD).prefixFree(".appodeal"))
        confirm(create())
    }

    @Test fun testQueVideo() = runTest {
        addDefaultNegatives()
        addCandidate(neg().pkgs("com.quvideo.xiaoying").locs(SDCARD).prefixFree("data/.push"))
        addCandidate(pos().pkgs("com.quvideo.xiaoying").locs(SDCARD).prefixFree("data/.push_deviceid"))
        confirm(create())
    }
}