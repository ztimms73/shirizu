package app.shirizu.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "org.xtimms.shirizu.benchmark",
        profileBlock = {
            pressHome()
            startActivityAndWait()
            device.findObject(By.text("Shelf")).click()
            device.findObject(By.text("History")).click()
            device.findObject(By.text("Explore")).click()
        },
    )
}