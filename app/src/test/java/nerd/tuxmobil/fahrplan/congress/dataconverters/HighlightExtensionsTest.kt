package nerd.tuxmobil.fahrplan.congress.dataconverters

import nerd.tuxmobil.fahrplan.congress.models.Highlight
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import info.metadude.android.eventfahrplan.database.models.Highlight as HighlightDatabaseModel

class HighlightExtensionsTest {

    @Test
    fun toHighlightAppModel() {
        val highlightDatabaseModel = HighlightDatabaseModel(
                sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c37",
                isHighlight = true
        )
        val highlightAppModel = Highlight(
                sessionId = "be9979c4-18bc-52bb-9480-2c0ac2782c37",
                isHighlight = true
        )
        assertThat(highlightDatabaseModel.toHighlightAppModel()).isEqualTo(highlightAppModel)
    }

}
