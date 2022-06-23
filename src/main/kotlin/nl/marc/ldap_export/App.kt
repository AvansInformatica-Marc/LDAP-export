package nl.marc.ldap_export

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.directory.api.ldap.model.cursor.EntryCursor
import org.apache.directory.api.ldap.model.message.SearchScope
import org.apache.directory.ldap.client.api.LdapConnection
import org.apache.directory.ldap.client.api.LdapNetworkConnection
import java.io.File
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.div
import kotlin.io.path.toPath

@Composable
@Preview
fun App() {
    var ldapConnection by remember { mutableStateOf<LdapConnection?>(null) }

    MaterialTheme(
        colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
    ) {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            val connection = ldapConnection
            if (connection != null) {
                ExportScreen(connection)
            } else {
                LoadLdapConnection {
                    ldapConnection = it
                }
            }
        }
    }
}
